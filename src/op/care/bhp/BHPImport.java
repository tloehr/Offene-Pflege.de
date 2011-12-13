/*
 * OffenePflege
 * Copyright (C) 2008 Torsten Löhr
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License V2 as published by the Free Software Foundation
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even 
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to 
 * the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
 * www.offene-pflege.de
 * ------------------------ 
 * Auf deutsch (freie Übersetzung. Rechtlich gilt die englische Version)
 * Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU General Public License, 
 * wie von der Free Software Foundation veröffentlicht, weitergeben und/oder modifizieren, gemäß Version 2 der Lizenz.
 *
 * Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, daß es Ihnen von Nutzen sein wird, aber 
 * OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN 
 * BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht, 
 * schreiben Sie an die Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA.
 * 
 */
package op.care.bhp;

import entity.system.SYSRunningClasses;
import entity.system.SYSRunningClassesTools;
import entity.verordnungen.BHP;
import entity.verordnungen.Verordnung;
import entity.verordnungen.VerordnungPlanung;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.*;

/**
 * @author tloehr
 */
public class BHPImport {

    public static final String internalClassID = "nursingrecords.bhpimport";


    public static void importBHP(Verordnung verordnung) {
        importBHP(verordnung, null);
    }

    /**
     * @param verordnung Die Verordnung, auf den sich der Import Vorgang beschränken soll. Steht hier null, dann wird die BHP für alle erstellt.
     *                   Wird eine Zeit angegeben, dann wird der Plan nur ab diesem Zeitpunkt (innerhalb des Tages) erstellt.
     *                   Es wird noch geprüft, ob es abgehakte BHPs in dieser Schicht gibt. Wenn ja, wird alles erst ab der nächsten
     *                   Schicht eingetragen.
     * @param zeit       ist ein Date, dessen Uhrzeit-Anteil benutzt wird, um nur die BHPs für diesen Tag <b>ab</b> dieser Uhrzeit zu importieren. <code>null</code>, wenn alle importiert werden sollen.
     */
    public static void importBHP(Verordnung verordnung, Date zeit) {

        String wtag; // kurze Darstellung des Wochentags des Stichtags
//        double nachtMo;
//        double morgens;
//        double mittags;
//        double nachmittags;
//        double abends;
//        double nachtAb;
//        //Tim uhrzeit;
//        double uhrzeitDosis;
//        int taeglich;
//        int woechentlich;
//        int monatlich;
        int tagnum;
        int wtagnum;
//        GregorianCalendar ref;
//        long bhppid;

        int schichtOffset = 0;
        boolean doCommit = false;
        SYSRunningClasses me = null;


        if (verordnung == null) {
            me = SYSRunningClassesTools.startModule(internalClassID, new String[]{"nursingrecords.prescription", "nursingrecords.bhp", "nursingrecords.bhpimport"}, 5, "BHP Tagesplan muss erstellt werden.");
        }

        if (verordnung != null || me != null) { // Bei einer einzelnen Verordnung wird diese Methode nicht registriert. Ansonsten müssen wir einen Lock haben.
            EntityManager em = OPDE.createEM();
            try {

                em.getTransaction().begin();

                Date stichtag = new Date();
                GregorianCalendar gcStichtag = SYSCalendar.toGC(stichtag);

                // Mache aus "Montag" -> "Mon"
                wtag = SYSCalendar.WochentagName(gcStichtag);
                wtag = wtag.substring(0, 3).toLowerCase();

                tagnum = gcStichtag.get(GregorianCalendar.DAY_OF_MONTH);
                wtagnum = gcStichtag.get(GregorianCalendar.DAY_OF_WEEK_IN_MONTH); // der erste Mitwwoch im Monat hat 1, der zweite 2 usw...

                Query select = em.createQuery(" " +
                        " SELECT vp FROM VerordnungPlanung vp " +
                        " JOIN vp.verordnung v " +
                        // nur die Verordnungen, die überhaupt gültig sind
                        // das sind die mit Gültigkeit BAW oder Gültigkeit endet irgendwann in der Zukunft.
                        // Das heisst, wenn eine Verordnung heute endet, dann wird sie dennoch eingetragen.
                        // Also alle, die bis EINSCHLIEßLICH heute gültig sind.
                        " WHERE v.situation IS NULL AND v.anDatum <= :andatum AND v.abDatum >= :abdatum" +
                        // Einschränkung auf bestimmte Verordnung0en, wenn gewünscht
                        (verordnung != null ? " AND v = :verordnung " : " ") +
                        // und nur die Planungen, die überhaupt auf den Stichtag passen könnten.
                        // Hier werden bereits die falschen Wochentage rausgefiltert. Brauchen
                        // wir uns nachher nicht mehr drum zu kümmern.
                        " AND ((vp.taeglich > 0) OR (vp.woechentlich > 0 AND vp." + wtag + " > 0) OR (vp.monatlich > 0 AND (vp.tagNum = :tagnum OR vp." + wtag + " = :wtag))) " +
                        // und nur diejenigen, deren Referenzdatum nicht in der Zukunft liegt.
                        "AND vp.lDatum <= :ldatum AND v.bewohner.adminonly <> 2");

                OPDE.info(SYSTools.getWindowTitle("BHPImport"));

                OPDE.info("Schreibe nach: " + OPDE.getUrl());

                select.setParameter("andatum", new Date(SYSCalendar.endOfDay()));
                select.setParameter("abdatum", new Date(SYSCalendar.startOfDay()));
                select.setParameter("tagnum", tagnum);
                select.setParameter("wtag", wtagnum);
                select.setParameter("ldatum", new Date(SYSCalendar.endOfDay()));

                if (verordnung != null) {
                    select.setParameter("verordnung", verordnung);
                }

                List<VerordnungPlanung> list = select.getResultList();

                int maxrows = list.size();

                Iterator<VerordnungPlanung> planungen = list.iterator();
                int row = 0;

                OPDE.debug("MaxRows: " + maxrows);


                // Erstmal alle Einträge, die täglich oder wöchentlich nötig sind.
                while (planungen.hasNext()) {
                    row++;
                    VerordnungPlanung planung = planungen.next();
                    OPDE.info("Fortschritt Vorgang: " + ((float) row / maxrows) * 100 + "%");
                    OPDE.debug("==========================================");
                    //OPDE.debug(planung);
                    OPDE.debug("Planung: " + planung.getBhppid());
                    OPDE.debug("BWKennung: " + planung.getVerordnung().getBewohner().getBWKennung());
                    OPDE.debug("VerID: " + planung.getVerordnung().getVerid());


                    boolean treffer = false;
                    GregorianCalendar ldatum = SYSCalendar.toGC(planung.getLDatum());

                    OPDE.debug("LDatum: " + DateFormat.getDateTimeInstance().format(planung.getLDatum()));
                    OPDE.debug("Stichtag: " + DateFormat.getDateTimeInstance().format(stichtag));


                    if (planung.getTaeglich() > 0) { // Taeglich -------------------------------------------------------------
                        OPDE.debug("Eine tägliche Planung");
                        // Dann wird das LDatum solange um die gewünschte Tagesanzahl erhöht, bis
                        // der stichtag getroffen wurde oder überschritten ist.
                        while (SYSCalendar.sameDay(ldatum, gcStichtag) < 0) {
                            OPDE.debug("ldatum liegt vor dem stichtag. Addiere tage: "+planung.getTaeglich());
                            ldatum.add(GregorianCalendar.DATE, planung.getTaeglich());
                        }
                        // Mich interssiert nur der Treffer, also die Punktlandung auf dem Stichtag
                        treffer = SYSCalendar.sameDay(ldatum, gcStichtag) == 0;
                    } else if (planung.getWoechentlich() > 0) { // Woechentlich -------------------------------------------------------------
                        OPDE.debug("Eine wöchentliche Planung");
                        while (SYSCalendar.sameWeek(ldatum, gcStichtag) < 0) {
                            OPDE.debug("ldatum liegt vor dem stichtag. Addiere Wochen: "+planung.getWoechentlich());
                            ldatum.add(GregorianCalendar.WEEK_OF_YEAR, planung.getWoechentlich());
                        }
                        // Ein Treffer ist es dann, wenn das Referenzdatum gleich dem Stichtag ist ODER es zumindest in der selben Kalenderwoche liegt.
                        // Da bei der Vorauswahl durch die Datenbank nur passende Wochentage überhaupt zugelassen wurden, muss das somit der richtige sein.
                        treffer = SYSCalendar.sameWeek(ldatum, gcStichtag) == 0;
                    } else if (planung.getMonatlich() > 0) { // Monatlich -------------------------------------------------------------
                        OPDE.debug("Eine monatliche Planung");
                        while (SYSCalendar.sameMonth(ldatum, gcStichtag) < 0) {
                            OPDE.debug("ldatum liegt vor dem stichtag. Addiere Monate: "+planung.getMonatlich());
                            ldatum.add(GregorianCalendar.MONTH, planung.getMonatlich());
                        }
                        // Ein Treffer ist es dann, wenn das Referenzdatum gleich dem Stichtag ist ODER es zumindest im selben Monat desselben Jahres liegt.
                        // Da bei der Vorauswahl durch die Datenbank nur passende Wochentage oder Tage im Monat überhaupt zugelassen wurden, muss das somit der richtige sein.
                        treffer = SYSCalendar.sameMonth(ldatum, gcStichtag) == 0;
                    }

                    OPDE.debug("LDatum jetzt: " + DateFormat.getDateTimeInstance().format(new Date(ldatum.getTimeInMillis())));
                    OPDE.debug("Treffer ? : " + Boolean.toString(treffer));

                    // Es wird immer erst eine Schicht später eingetragen. Damit man nicht mit bereits
                    // abgelaufenen Zeitpunkten arbeitet.
                    // Bei zeit == null werden all diese Booleans zu true und damit neutralisiert.
                    boolean erstAbFM = (zeit == null) || SYSCalendar.ermittleZeit(zeit.getTime()) == SYSConst.FM;
                    boolean erstAbMO = (zeit == null) || erstAbFM || SYSCalendar.ermittleZeit(zeit.getTime()) == SYSConst.MO;
                    boolean erstAbMI = (zeit == null) || erstAbMO || SYSCalendar.ermittleZeit(zeit.getTime()) == SYSConst.MI;
                    boolean erstAbNM = (zeit == null) || erstAbMI || SYSCalendar.ermittleZeit(zeit.getTime()) == SYSConst.NM;
                    boolean erstAbAB = (zeit == null) || erstAbNM || SYSCalendar.ermittleZeit(zeit.getTime()) == SYSConst.AB;
                    boolean erstAbNA = (zeit == null) || erstAbAB || SYSCalendar.ermittleZeit(zeit.getTime()) == SYSConst.NA;
                    boolean uhrzeitOK = (zeit == null) || (planung.getUhrzeit() != null && SYSCalendar.compareTime(planung.getUhrzeit(), zeit) > 0);

                    if (treffer) {
                        if (erstAbFM && planung.getNachtMo().compareTo(BigDecimal.ZERO) > 0) {
                            BHP bhp = new BHP(planung, stichtag, SYSConst.FM, planung.getNachtMo());
                            //OPDE.debug(bhp);
                            OPDE.debug("SYSConst.FM, "+planung.getNachtMo());
                            em.persist(bhp);
                        }
                        if (erstAbMO && planung.getMorgens().compareTo(BigDecimal.ZERO) > 0) {
                            BHP bhp = new BHP(planung, stichtag, SYSConst.MO, planung.getMorgens());
                            OPDE.debug("SYSConst.MO, "+planung.getMorgens());
                            em.persist(bhp);
                        }
                        if (erstAbMI && planung.getMittags().compareTo(BigDecimal.ZERO) > 0) {
                            BHP bhp = new BHP(planung, stichtag, SYSConst.MI, planung.getMittags());
                            OPDE.debug("SYSConst.MI, "+planung.getMittags());
                            em.persist(bhp);
                        }
                        if (erstAbNM && planung.getNachmittags().compareTo(BigDecimal.ZERO) > 0) {
                            BHP bhp = new BHP(planung, stichtag, SYSConst.NM, planung.getNachmittags());
                            OPDE.debug("SYSConst.NM, "+planung.getNachmittags());
                            em.persist(bhp);
                        }
                        if (erstAbAB && planung.getAbends().compareTo(BigDecimal.ZERO) > 0) {
                            BHP bhp = new BHP(planung, stichtag, SYSConst.AB, planung.getAbends());
                            OPDE.debug("SYSConst.AB, "+planung.getAbends());
                            em.persist(bhp);
                        }
                        if (erstAbNA && planung.getNachtAb().compareTo(BigDecimal.ZERO) > 0) {
                            BHP bhp = new BHP(planung, stichtag, SYSConst.NA, planung.getNachtAb());
                            OPDE.debug("SYSConst.NA, "+planung.getNachtAb());
                            em.persist(bhp);
                        }
                        if (uhrzeitOK && planung.getUhrzeit() != null) {
                            Date neuerStichtag = SYSCalendar.addTime2Date(stichtag, planung.getUhrzeit());
                            BHP bhp = new BHP(planung, neuerStichtag, SYSConst.UZ, planung.getUhrzeitDosis());
                            OPDE.debug("SYSConst.UZ, "+planung.getUhrzeitDosis() + ", " + DateFormat.getDateTimeInstance().format(neuerStichtag));
                            em.persist(bhp);
                        }

                        // Nun noch das LDatum in der Tabelle DFNPlanung neu setzen.
                        planung.setLDatum(stichtag);
                        em.merge(planung);
                    }
                } // Main Loop
                em.getTransaction().rollback();
                //em.getTransaction().commit();
            } catch (Exception ex) {
                em.getTransaction().rollback();
                OPDE.fatal(ex);
            } finally {
                em.close();
            }
            OPDE.info("BHPImport abgeschlossen");
            if (me != null) {
                SYSRunningClassesTools.endModule(me);
            }
        } else {
            OPDE.warn("BHPImport nicht abgeschlossen. Zugriffskonflikt.");
        }
    }
}
