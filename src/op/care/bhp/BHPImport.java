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
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;

import java.sql.*;
import java.util.GregorianCalendar;

/**
 * @author tloehr
 */
public class BHPImport {

    public static final String internalClassID = "nursingrecords.bhpimport";

    public static void importBHP(long verid)
            throws SQLException {
        importBHP(verid, 0, 0);
    }

    /**
     * @param verid Die ID der Verordnung, auf den sich der Import Vorgang beschränken soll.
     *              Steht hier eine 0, dann wird die BHP für alle erstellt.
     *              Wird eine Zeit angegeben, dann wird der Plan nur ab diesem Zeitpunkt (innerhalb des Tages) erstellt.
     *              Es wird noch geprüft, ob es abgehakte BHPs in dieser Schicht gibt. Wenn ja, wird alles erst ab der nächsten
     *              Schicht eingetragen.
     */
    public static void importBHP(long verid, long zeit, int daysoffset)
            throws SQLException {

        String wtag; // kurze Darstellung des Wochentags des Stichtags
        double nachtMo;
        double morgens;
        double mittags;
        double nachmittags;
        double abends;
        double nachtAb;
        Time uhrzeit;
        double uhrzeitDosis;
        int taeglich;
        int woechentlich;
        int monatlich;
        int tagnum;
        int wtagnum;
        GregorianCalendar ref;
        long bhppid;
        boolean treffer;
        int schichtOffset = 0;
        boolean doCommit = false;
        SYSRunningClasses me = null;

        OPDE.initDB();

        Connection db = OPDE.getDb().db;

        // Zugriffskonflikt auflösen.
//        String pk = null;
//        if (verid > 0) {
//            pk = (String) DBRetrieve.getSingleValue("BHPVerordnung", "BWKennung", "VerID", verid);
//        }
//        SYSMessenger.emergencyExit("op.care.bhp.BHPImport", pk);

        // Nicht länger als 5 Minuten versuchen. Es kann passieren, dass der abzuschiessende
        // Computer es nicht mehr schafft auf die Anfrage zu antworten BEVOR er runterfährt.
//        int i = 0;
//        while (i < 5 && !SYSMessenger.allApplied()) {
//            try {
//                OPDE.info("BHPImport: Muss noch warten. Gibt noch welche, die nicht beendet wurden.");
//                Thread.sleep(60000); // Eine Minute warten.
//                i++;
//            } catch (InterruptedException ex) {
//                ex.printStackTrace();
//            }
//        }
//        SYSMessenger.forceApply();
//


        if (verid == 0) {
            me = SYSRunningClassesTools.startModule(internalClassID, new String[]{"nursingrecords.prescription", "nursingrecords.bhp", "nursingrecords.bhpimport"}, 5, "BHP Tagesplan muss erstellt werden.");
        }

        if (verid > 0 || me != null) { // Bei Verid <> 0 wird diese Methode nicht registriert. Ansonsten müssen wir einen Lock haben.

            OPDE.debug("VerID: " + verid);
            OPDE.debug("Zeit: " + zeit);
            OPDE.debug("Offset:" + daysoffset);

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
                OPDE.debug("Stichtag: " + SYSCalendar.printGC(gcStichtag));
                Date sDatum = new Date(gcStichtag.getTimeInMillis());

                // Mache aus "Montag" -> "Mon"
                wtag = SYSCalendar.WochentagName(gcStichtag);
                wtag = wtag.substring(0, 3);

                tagnum = gcStichtag.get(GregorianCalendar.DAY_OF_MONTH);
                wtagnum = gcStichtag.get(GregorianCalendar.DAY_OF_WEEK_IN_MONTH); // der erste Mitwwoch im Monat hat 1, der zweite 2 usw...

                ResultSet rs;
                PreparedStatement stmtSource;
                PreparedStatement stmtTarget;
                PreparedStatement stmtUpdate;

                String selectSQL = "SELECT v.BWKennung, v.AbDatum, v.AnUKennung, v.MassID, bhp.BHPPID, bhp.Uhrzeit, bhp.NachtMo, "
                        + "bhp.Morgens, bhp.Mittags, bhp.Nachmittags, bhp.Abends, bhp.NachtAb, bhp.UhrzeitDosis, bhp.Mon, bhp.Die, bhp.Mit, "
                        + "bhp.Don, bhp.Fre, bhp.Sam, bhp.Son, bhp.Taeglich, bhp.Woechentlich, bhp.Monatlich, bhp.TagNum, bhp.LDatum "
                        + "FROM BHPPlanung bhp "
                        + "INNER JOIN BHPVerordnung v ON bhp.VerID = v.VerID "
                        + // nur die Verordnungen, die überhaupt gültig sind
                        // das sind die mit Gültigkeit BAW oder Gültigkeit endet irgendwann in der Zukunft.
                        // Das heisst, wenn eine Verordnung heute endet, dann wird sie dennoch eingetragen.
                        // Also alle, die bis EINSCHLIEßLICH heute gültig sind.
                        "WHERE v.SitID = 0 AND date(v.AnDatum) <= date(DATE_ADD(NOW(), INTERVAL ? DAY)) AND "
                        + "                       date(v.AbDatum) >= date(DATE_ADD(NOW(), INTERVAL ? DAY)) "
                        + // Einschränkung auf bestimmte Verordnung0en, wenn gewünscht
                        (verid > 0 ? " AND bhp.VerID = ? " : " ")
                        + // und nur die Planungen, die überhaupt auf den Stichtag passen könnten.
                        // Hier werden bereits die falschen Wochentage rausgefiltert. Brauchen
                        // wir uns nachher nicht mehr drum zu kümmern.
                        "AND ((Taeglich > 0) OR (Woechentlich > 0 AND " + wtag + " > 0) OR (monatlich > 0 AND (TagNum = ? OR " + wtag + " = ?))) "
                        + // und nur diejenigen, deren Referenzdatum nicht in der Zukunft liegt.
                        "AND Date(LDatum) <= Date(DATE_ADD(NOW(), INTERVAL ? DAY)) AND tmp = 0 AND BWKennung <> 'MM4'";
                // Einschränkung auf bestimmten Bewohner, wenn gewünscht.
                //(bwkennung.equalsIgnoreCase("") ? "" : "AND v.BWKennung = ?");


                String insertSQL = "INSERT INTO BHP (BHPPID, Soll, SZeit, Dosis, Status, _mdate)"
                        + " VALUES (?,?,?,?,0,now())";

                String updateSQL = "UPDATE BHPPlanung SET LDatum = ? WHERE BHPPID = ?";

                stmtTarget = OPDE.getDb().db.prepareStatement(insertSQL);
                stmtUpdate = OPDE.getDb().db.prepareStatement(updateSQL);
                stmtSource = OPDE.getDb().db.prepareStatement(selectSQL);

                OPDE.info(SYSTools.getWindowTitle("BHPImport"));
                OPDE.info("Schreibe nach: " + OPDE.getUrl());

                if (verid > 0) {
                    stmtSource.setInt(1, daysoffset);
                    stmtSource.setInt(2, daysoffset);
                    stmtSource.setLong(3, verid); // bestimmte Verordnung
                    stmtSource.setInt(4, tagnum); // TagNum
                    stmtSource.setInt(5, wtagnum); // wtag (z.B. "der x.Donnerstag im Monat")
                    stmtSource.setInt(6, daysoffset);
                } else {
                    stmtSource.setInt(1, daysoffset);
                    stmtSource.setInt(2, daysoffset);
                    stmtSource.setInt(3, tagnum); // TagNum
                    stmtSource.setInt(4, wtagnum); // wtag (z.B. "der x.Donnerstag im Monat")
                    stmtSource.setInt(5, daysoffset);
                }
                rs = stmtSource.executeQuery();
                rs.last();
                int maxrows = rs.getRow();
                rs.first();


                // Erstmal alle Einträge, die täglich oder wöchentlich nötig sind.
                while (maxrows > 0 && !rs.isAfterLast()) {


                    OPDE.info("Fortschritt Vorgang: " + ((float) rs.getRow() / maxrows) * 100 + "%");

                    treffer = false;

                    nachtMo = rs.getDouble("bhp.NachtMo");
                    morgens = rs.getDouble("bhp.Morgens");
                    mittags = rs.getDouble("bhp.Mittags");
                    nachmittags = rs.getDouble("bhp.Nachmittags");
                    abends = rs.getDouble("bhp.Abends");
                    nachtAb = rs.getDouble("bhp.NachtAb");
                    uhrzeitDosis = rs.getDouble("bhp.UhrzeitDosis");
                    taeglich = rs.getInt("bhp.Taeglich");
                    uhrzeit = rs.getTime("bhp.Uhrzeit");
                    woechentlich = rs.getInt("bhp.Woechentlich");
                    monatlich = rs.getInt("bhp.Monatlich");
                    tagnum = rs.getInt("bhp.TagNum");
                    Date ldatum = rs.getDate("bhp.LDatum");
                    ref = SYSCalendar.toGC(rs.getDate("bhp.LDatum"));
                    bhppid = rs.getLong("bhp.BHPPID");
                    //soll = SYSCalendar.today().getTimeInMillis()

                    // Hierdurch wird der Faktor Uhrzeit ausgeschaltet.
                    gcStichtag.set(GregorianCalendar.HOUR_OF_DAY, ref.get(GregorianCalendar.HOUR_OF_DAY));
                    gcStichtag.set(GregorianCalendar.MINUTE, ref.get(GregorianCalendar.MINUTE));
                    gcStichtag.set(GregorianCalendar.SECOND, ref.get(GregorianCalendar.SECOND));
                    gcStichtag.set(GregorianCalendar.MILLISECOND, ref.get(GregorianCalendar.MILLISECOND));

                    //(BHPPID, SDatum, SUhrzeit, SZeit, Dosis, Status, _cdate, _mdate)

                    stmtTarget.setLong(1, bhppid);

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
//
//                if (zeit > 0) { // Gibt es bereits abgehakte BHPs in dieser Schicht ?
//
//                }

                    // Es wird immer erst eine Schicht später eingetragen. Damit man nicht mit bereits
                    // abgelaufenen Zeitpunkten arbeitet.
                    boolean erstAbFM = (zeit == 0) || SYSCalendar.ermittleZeit(zeit) + schichtOffset == SYSConst.FM;
                    boolean erstAbMO = (zeit == 0) || erstAbFM || SYSCalendar.ermittleZeit(zeit) + schichtOffset == SYSConst.MO;
                    boolean erstAbMI = (zeit == 0) || erstAbMO || SYSCalendar.ermittleZeit(zeit) + schichtOffset == SYSConst.MI;
                    boolean erstAbNM = (zeit == 0) || erstAbMI || SYSCalendar.ermittleZeit(zeit) + schichtOffset == SYSConst.NM;
                    boolean erstAbAB = (zeit == 0) || erstAbNM || SYSCalendar.ermittleZeit(zeit) + schichtOffset == SYSConst.AB;
                    boolean erstAbNA = (zeit == 0) || erstAbAB || SYSCalendar.ermittleZeit(zeit) + schichtOffset == SYSConst.NA;
                    // X07: DEBUG: klappt das mit Uhrzeiten für denselben Tag ?
                    boolean uhrzeitOK = (zeit == 0) || (uhrzeit != null && SYSCalendar.compareTime(uhrzeit.getTime(), zeit) > 0);

                    long insertTS = gcStichtag.getTimeInMillis();

                    if (treffer) {
                        if (erstAbFM && nachtMo > 0) {
                            stmtTarget.setTimestamp(2, new Timestamp(insertTS)); // Solldatum
                            stmtTarget.setInt(3, SYSConst.FM);
                            stmtTarget.setDouble(4, nachtMo);
                            stmtTarget.executeUpdate();
                        }
                        if (erstAbMO && morgens > 0) {
                            stmtTarget.setTimestamp(2, new Timestamp(insertTS)); // Solldatum
                            stmtTarget.setInt(3, SYSConst.MO);
                            stmtTarget.setDouble(4, morgens);
                            stmtTarget.executeUpdate();
                        }
                        if (erstAbMI && mittags > 0) {
                            stmtTarget.setTimestamp(2, new Timestamp(insertTS)); // Solldatum
                            stmtTarget.setInt(3, SYSConst.MI);
                            stmtTarget.setDouble(4, mittags);
                            stmtTarget.executeUpdate();
                        }
                        if (erstAbNM && nachmittags > 0) {
                            stmtTarget.setTimestamp(2, new Timestamp(insertTS)); // Solldatum
                            stmtTarget.setInt(3, SYSConst.NM);
                            stmtTarget.setDouble(4, nachmittags);
                            stmtTarget.executeUpdate();
                        }
                        if (erstAbAB && abends > 0) {
                            stmtTarget.setTimestamp(2, new Timestamp(insertTS)); // Solldatum
                            stmtTarget.setInt(3, SYSConst.AB);
                            stmtTarget.setDouble(4, abends);
                            stmtTarget.executeUpdate();
                        }
                        if (erstAbNA && nachtAb > 0) {
                            stmtTarget.setTimestamp(2, new Timestamp(insertTS)); // Solldatum
                            stmtTarget.setInt(3, SYSConst.NA);
                            stmtTarget.setDouble(4, nachtAb);
                            stmtTarget.executeUpdate();
                        }
                        if (uhrzeitOK && uhrzeit != null) {
                            long s = SYSCalendar.addTime2Date(gcStichtag, SYSCalendar.toGC(uhrzeit.getTime())).getTimeInMillis();
                            stmtTarget.setTimestamp(2, new Timestamp(s)); // Solldatum
                            stmtTarget.setInt(3, SYSConst.UZ);
                            stmtTarget.setDouble(4, uhrzeitDosis);
                            stmtTarget.executeUpdate();
                        }

                        // Nun noch das LDatum in der Tabelle DFNPlanung neu setzen.
                        stmtUpdate.setDate(1, sDatum);
                        stmtUpdate.setLong(2, bhppid);
                        stmtUpdate.executeUpdate();

                    } // if (treffer)
                    //////////////////////////////////////////////////////////////
                    rs.next(); // Nächster
                } // while(!rs.isAfterLast()){
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
            OPDE.info("BHPImport abgeschlossen");
            if (me != null) {
                SYSRunningClassesTools.endModule(me);
            }
        } else {
            OPDE.warn("BHPImport nicht abgeschlossen. Zugriffskonflikt.");
        }
    }
}
