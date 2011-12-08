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
package op.care;

import entity.SYSRunningClasses;
import entity.SYSRunningClassesTools;
import op.OPDE;
import op.tools.*;

import java.sql.*;
import java.util.GregorianCalendar;

/**
 * @author tloehr
 */
public class DFNImport {

    public static final String internalClassID = "nursingrecords.dfnimport";
    private static SYSRunningClasses runningClass, blockingClass;

    //    public static void importBHP(int offset)
//    throws SQLException{
//        importBHP(0,0,offset);
//    }
    public static void importDFN(long planid)
            throws SQLException {
        importDFN(planid, 0, 0);
    }

    public static void importDFN()
            throws SQLException {
        importDFN(0, 0, 0);
    }

    /**
     * @param planid Die ID der Verordnung, auf den sich der Import Vorgang beschränken soll.
     *               Steht hier eine 0, dann wird der DFN für alle erstellt.
     *               Wird eine Zeit angegeben, dann wird der Plan nur ab diesem Zeitpunkt (innerhalb des Tages) erstellt.
     *               Es wird noch geprüft, ob es abgehakte DFNs in dieser Schicht gibt. Wenn ja, wird alles erst ab der nächsten
     *               Schicht eingetragen.
     */
    public static void importDFN(long planid, long zeit, int daysoffset)
            throws SQLException {

        String wtag; // kurze Darstellung des Wochentags des Stichtags
        int nachtMo;
        int morgens;
        int mittags;
        int nachmittags;
        int abends;
        int nachtAb;
        Time uhrzeit;
        int uhrzeitAnzahl;
        int taeglich;
        int woechentlich;
        int monatlich;
        int tagnum;
        int wtagnum;
        double dauer;
        GregorianCalendar ref;
        long termid;
        long massid;
        boolean treffer;
        boolean erforderlich;
        //Timestamp soll;
        String bwkennung;
        int schichtOffset = 0;
        boolean doCommit = false;
        SYSRunningClasses me = null;



        OPDE.initDB();

        Connection db = OPDE.getDb().db;

        if (planid == 0) {
            me = SYSRunningClassesTools.startModule(internalClassID, new String[]{"nursingrecords.prescription", "nursingrecords.bhp", "nursingrecords.bhpimport"}, 5);
        }

        if (planid > 0 || me != null) { // Bei Verid <> 0 wird diese Methode nicht registriert. Ansonsten müssen wir einen Lock haben.

            OPDE.getLogger().debug("PlanID: " + planid);
            OPDE.getLogger().debug("Zeit: " + zeit);
            OPDE.getLogger().debug("Offset:" + daysoffset);

            try {
                // Hier beginnt eine Transaktion, wenn es nicht schon eine gibt.
                if (db.getAutoCommit()) {
                    db.setAutoCommit(false);
                    db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                    db.commit();
                    doCommit = true;
                }

                GregorianCalendar gcStichtag = new GregorianCalendar();
                gcStichtag.add(GregorianCalendar.DATE, daysoffset);
                OPDE.getLogger().debug("Stichtag: " + SYSCalendar.printGC(gcStichtag));
                Date sDatum = new Date(gcStichtag.getTimeInMillis());

                // Mache aus "Montag" -> "Mon"
                wtag = SYSCalendar.WochentagName(gcStichtag);
                wtag = wtag.substring(0, 3);

                tagnum = gcStichtag.get(GregorianCalendar.DAY_OF_MONTH);
                wtagnum = gcStichtag.get(GregorianCalendar.DAY_OF_WEEK_IN_MONTH); // der erste Mitwwoch im Monat hat 1, der zweite 2 usw...
                //Timestamp ts = new Timestamp(new GregorianCalendar().getTimeInMillis());

                ResultSet rs;
                PreparedStatement stmtSource;
                PreparedStatement stmtTarget;
                PreparedStatement stmtUpdate;
                PreparedStatement stmtForced;

                String selectSQL = "SELECT"
                        + " t.TermID, p.BWKennung, p.Von, p.AnUKennung, t.MassID, t.Uhrzeit, t.NachtMo, "
                        + " t.Morgens, t.Mittags, t.Nachmittags, t.Abends, t.NachtAb, t.UhrzeitAnzahl, t.Mon, t.Die, t.Mit, "
                        + " t.Don, t.Fre, t.Sam, t.Son, t.Taeglich, t.Woechentlich, t.Monatlich, t.TagNum, t.LDatum, t.Erforderlich,"
                        + " t.Dauer "
                        + " FROM MassTermin t "
                        + " INNER JOIN Planung p ON t.PlanID = p.PlanID "
                        + " INNER JOIN Bewohner bw ON bw.BWKennung = p.BWKennung "
                        + " INNER JOIN Massnahmen mass ON t.MassID = mass.MassID "
                        + // nur die Verordnungen, die überhaupt gültig sind
                        // das sind die mit Gültigkeit BAW oder Gültigkeit endet irgendwann in der Zukunft.
                        " WHERE date(p.Von) <= date(DATE_ADD(NOW(), INTERVAL ? DAY)) AND date(p.Bis) >= date(DATE_ADD(NOW(), INTERVAL ? DAY)) "
                        + // Einschränkung auf bestimmte Verordnung0en, wenn gewünscht
                        (planid > 0 ? " AND p.PlanID = ? " : "")
                        + // und nur die Planungen, die überhaupt auf den Stichtag passen könnten.
                        // Hier werden bereits die falschen Wochentage rausgefiltert. Brauchen
                        // wir uns nachher nicht mehr drum zu kümmern.
                        " AND ( "
                        + "       (Taeglich > 0) "
                        + "   OR "
                        + "       (Woechentlich > 0 AND " + wtag + " > 0)"
                        + "   OR "
                        + "       (monatlich > 0 AND (TagNum = ? OR " + wtag + " = ? ))"
                        + "   ) "
                        + // und nur diejenigen, deren Referenzdatum nicht in der Zukunft liegt.
                        " AND Date(LDatum) <= Date(DATE_ADD(NOW(), INTERVAL ? DAY) ) "
                        + // Keine TMP Daten und keine Test-Bewohner.
                        " AND t.tmp = 0 AND bw.AdminOnly <> 2 ";

                String insertSQL = "INSERT INTO DFN (TermID, StDatum, Soll, SZeit, Status, Erforderlich, MassID, BWKennung, Dauer)"
                        + " VALUES (?,now(),?,?,0,?,?,?,?)";

                String updateSQL = " UPDATE MassTermin SET LDatum = ? WHERE TermID = ? ";

                String forcedSQL = " UPDATE DFN d "
                        + " INNER JOIN MassTermin t ON d.TermID = t.TermID "
                        + " INNER JOIN Planung p ON p.PlanID = t.PlanID "
                        + " SET d.Soll=now() "
                        + " WHERE d.Erforderlich > 0 AND d.Status = 0 AND DATE(d.Soll) < Date(now()) "
                        + " AND p.von < now() AND p.bis > now()";

                stmtTarget = OPDE.getDb().db.prepareStatement(insertSQL);
                stmtUpdate = OPDE.getDb().db.prepareStatement(updateSQL);
                stmtSource = OPDE.getDb().db.prepareStatement(selectSQL);
                stmtForced = OPDE.getDb().db.prepareStatement(forcedSQL);

                OPDE.info(SYSTools.getWindowTitle("DFNImport"));
                OPDE.info("Schreibe nach: " + OPDE.getUrl());

                OPDE.getLogger().debug(selectSQL);
                OPDE.getLogger().debug(updateSQL);
                OPDE.getLogger().debug(insertSQL);
                OPDE.getLogger().debug(forcedSQL);

                if (planid > 0) {
                    stmtSource.setInt(1, daysoffset);
                    stmtSource.setInt(2, daysoffset);
                    stmtSource.setLong(3, planid); // bestimmte Planung
                    stmtSource.setInt(4, tagnum); // TagNum
                    stmtSource.setInt(5, wtagnum); // wtag (z.B. "der x.Donnerstag im Monat")
                    stmtSource.setInt(6, daysoffset);
//            if (!bwkennung.equalsIgnoreCase("")){
//                stmtSource.setString(4, bwkennung); // Nur wenn nötig.
//            }
                } else {
                    stmtSource.setInt(1, daysoffset);
                    stmtSource.setInt(2, daysoffset);
                    stmtSource.setInt(3, tagnum); // TagNum
                    stmtSource.setInt(4, wtagnum); // wtag (z.B. "der x.Donnerstag im Monat")
                    stmtSource.setInt(5, daysoffset);
//            if (!bwkennung.equalsIgnoreCase("")){
//                stmtSource.setString(3, bwkennung); // Nur wenn nötig.
//            }
                }
                rs = stmtSource.executeQuery();
                rs.last();
                int maxrows = rs.getRow();
                rs.first();


                // Erstmal alle Einträge, die täglich oder wöchentlich nötig sind.
                while (maxrows > 0 && !rs.isAfterLast()) {


                    OPDE.info("Fortschritt Vorgang: " + ((float) rs.getRow() / maxrows) * 100 + "%");

                    treffer = false;

                    nachtMo = rs.getInt("t.NachtMo");
                    morgens = rs.getInt("t.Morgens");
                    mittags = rs.getInt("t.Mittags");
                    nachmittags = rs.getInt("t.Nachmittags");
                    abends = rs.getInt("t.Abends");
                    nachtAb = rs.getInt("t.NachtAb");
                    uhrzeitAnzahl = rs.getInt("t.UhrzeitAnzahl");
                    taeglich = rs.getInt("t.Taeglich");
                    uhrzeit = rs.getTime("t.Uhrzeit");
                    woechentlich = rs.getInt("t.Woechentlich");
                    monatlich = rs.getInt("t.Monatlich");
                    tagnum = rs.getInt("t.TagNum");
                    ref = SYSCalendar.toGC(rs.getDate("t.LDatum"));
                    termid = rs.getLong("t.TermID");
                    massid = rs.getLong("t.MassID");
                    erforderlich = rs.getBoolean("t.Erforderlich");
                    bwkennung = rs.getString("p.BWKennung");
                    dauer = rs.getDouble("t.Dauer");

                    // Hierdurch wird der Faktor Uhrzeit ausgeschaltet.
                    gcStichtag.set(GregorianCalendar.HOUR_OF_DAY, ref.get(GregorianCalendar.HOUR_OF_DAY));
                    gcStichtag.set(GregorianCalendar.MINUTE, ref.get(GregorianCalendar.MINUTE));
                    gcStichtag.set(GregorianCalendar.SECOND, ref.get(GregorianCalendar.SECOND));
                    gcStichtag.set(GregorianCalendar.MILLISECOND, ref.get(GregorianCalendar.MILLISECOND));

                    // Taeglich -------------------------------------------------------------
                    if (taeglich > 0) {
                        if (!ref.equals(gcStichtag)) {
                            do {
                                ref.add(GregorianCalendar.DATE, taeglich);
                            } while (!(ref.equals(gcStichtag) || ref.after(gcStichtag)));
                        }
                        treffer = ref.equals(gcStichtag);
                    }

                    // Woechentlich -------------------------------------------------------------
                    if (woechentlich > 0) {
                        if (!ref.equals(gcStichtag) && SYSCalendar.sameWeek(ref, gcStichtag) != 0) {
                            do {
                                ref.add(GregorianCalendar.WEEK_OF_YEAR, woechentlich);
                            } while (!(SYSCalendar.sameWeek(ref, gcStichtag) >= 0));
                        }
                        // Ein Treffer ist es dann, wenn das Referenzdatum gleich dem Stichtag ist ODER es zumindest in der selben Kalenderwoche liegt.
                        // Da bei der Vorauswahl durch die Datenbank nur passende Wochentage überhaupt zugelassen wurden, muss das somit der richtige sein.
                        treffer = (ref.equals(gcStichtag)) || (SYSCalendar.sameWeek(ref, gcStichtag) == 0);
                    }

                    // Monatlich -------------------------------------------------------------
                    if (monatlich > 0) {
                        if (!ref.equals(gcStichtag) && SYSCalendar.sameMonth(ref, gcStichtag) != 0) {
                            do {
                                ref.add(GregorianCalendar.MONTH, monatlich);
                            } while (!(SYSCalendar.sameMonth(ref, gcStichtag) >= 0));
                        }
                        // Ein Treffer ist es dann, wenn das Referenzdatum gleich dem Stichtag ist ODER es zumindest im selben Monat desselben Jahres liegt.
                        // Da bei der Vorauswahl durch die Datenbank nur passende Wochentage oder Tage im Monat überhaupt zugelassen wurden, muss das somit der richtige sein.
                        treffer = (ref.equals(gcStichtag)) || (SYSCalendar.sameMonth(ref, gcStichtag) == 0);
                    }

                    // Es wird immer erst eine Schicht später eingetragen. Damit man nicht mit bereits
                    boolean erstAbFM = (zeit == 0) || SYSCalendar.ermittleZeit(zeit) + schichtOffset == SYSConst.FM;
                    boolean erstAbMO = (zeit == 0) || erstAbFM || SYSCalendar.ermittleZeit(zeit) + schichtOffset == SYSConst.MO;
                    boolean erstAbMI = (zeit == 0) || erstAbMO || SYSCalendar.ermittleZeit(zeit) + schichtOffset == SYSConst.MI;
                    boolean erstAbNM = (zeit == 0) || erstAbMI || SYSCalendar.ermittleZeit(zeit) + schichtOffset == SYSConst.NM;
                    boolean erstAbAB = (zeit == 0) || erstAbNM || SYSCalendar.ermittleZeit(zeit) + schichtOffset == SYSConst.AB;
                    boolean erstAbNA = (zeit == 0) || erstAbAB || SYSCalendar.ermittleZeit(zeit) + schichtOffset == SYSConst.NA;
                    // X07: DEBUG: klappt das mit Uhrzeiten für denselben Tag ?
                    boolean uhrzeitOK = (zeit == 0) || (uhrzeit != null && SYSCalendar.compareTime(uhrzeit.getTime(), zeit) > 0);

                    long insertTS = gcStichtag.getTimeInMillis();
                    stmtTarget.setLong(1, termid);
                    stmtTarget.setBoolean(4, erforderlich);
                    stmtTarget.setLong(5, massid);
                    stmtTarget.setString(6, bwkennung);
                    stmtTarget.setDouble(7, dauer);

                    if (treffer) {
                        if (erstAbFM && nachtMo > 0) {
                            stmtTarget.setTimestamp(2, new Timestamp(insertTS)); // Solldatum
                            stmtTarget.setInt(3, SYSConst.FM);
                            for (int i = 1; i <= nachtMo; i++) {
                                stmtTarget.executeUpdate();
                            }
                        }
                        if (erstAbMO && morgens > 0) {
                            stmtTarget.setTimestamp(2, new Timestamp(insertTS)); // Solldatum
                            stmtTarget.setInt(3, SYSConst.MO);
                            for (int i = 1; i <= morgens; i++) {
                                stmtTarget.executeUpdate();
                            }
                        }
                        if (erstAbMI && mittags > 0) {
                            stmtTarget.setTimestamp(2, new Timestamp(insertTS)); // Solldatum
                            stmtTarget.setInt(3, SYSConst.MI);
                            for (int i = 1; i <= mittags; i++) {
                                stmtTarget.executeUpdate();
                            }

                        }
                        if (erstAbNM && nachmittags > 0) {
                            stmtTarget.setTimestamp(2, new Timestamp(insertTS)); // Solldatum
                            stmtTarget.setInt(3, SYSConst.NM);
                            for (int i = 1; i <= nachmittags; i++) {
                                stmtTarget.executeUpdate();
                            }

                        }
                        if (erstAbAB && abends > 0) {
                            stmtTarget.setTimestamp(2, new Timestamp(insertTS)); // Solldatum
                            stmtTarget.setInt(3, SYSConst.AB);
                            for (int i = 1; i <= abends; i++) {
                                stmtTarget.executeUpdate();
                            }

                        }
                        if (erstAbNA && nachtAb > 0) {
                            stmtTarget.setTimestamp(2, new Timestamp(insertTS)); // Solldatum
                            stmtTarget.setInt(3, SYSConst.NA);
                            for (int i = 1; i <= nachtAb; i++) {
                                stmtTarget.executeUpdate();
                            }

                        }
                        if (uhrzeitOK && uhrzeit != null) {
                            long s = SYSCalendar.addTime2Date(gcStichtag, SYSCalendar.toGC(uhrzeit.getTime())).getTimeInMillis();
                            stmtTarget.setTimestamp(2, new Timestamp(s)); // Solldatum
                            stmtTarget.setInt(3, SYSConst.UZ);
                            for (int i = 1; i <= uhrzeitAnzahl; i++) {
                                stmtTarget.executeUpdate();
                            }

                        }

                        // Nun noch das LDatum in der Tabelle DFNPlanung neu setzen.
                        stmtUpdate.setDate(1, sDatum);
                        stmtUpdate.setLong(2, termid);
                        stmtUpdate.executeUpdate();

                    } // if (treffer)
                    //////////////////////////////////////////////////////////////
                    rs.next(); // Nächster

                } // while(!rs.isAfterLast()){
                // Die nicht beachteten zu erzwingenden müssen noch auf das heutige Datum umgetragen werden.
                // Das erfolgt unabhängig von dem eingegebenen Stichtag.
                // Nur bei uneingeschränkten Imports.
                if (planid == 0) {
                    OPDE.info("Notwendige Massnahmen werden übertragen...");
                    stmtForced.executeUpdate();
                }
                if (doCommit) {
                    db.commit();
                    db.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                try {
                    if (doCommit) {
                        db.rollback();
                    }
                } catch (SQLException ex1) {
                    ex1.printStackTrace();
                    OPDE.fatal(ex1.getMessage());
                    System.exit(1);
                }
                ex.printStackTrace();
                OPDE.getLogger().error(ex.getMessage());
                OPDE.getLogger().error("Rolling back transaction");
            }
            OPDE.info("DFNImport abgeschlossen");
            if (me != null) {
                SYSRunningClassesTools.endModule(me);
            }
        } else {
            OPDE.warn("DFNImport NICHT abgeschlossen. Zugriffskonflikt.");
        }
    }
}
