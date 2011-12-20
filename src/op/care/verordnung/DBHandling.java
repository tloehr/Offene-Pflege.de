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
package op.care.verordnung;

import entity.Bewohner;
import entity.BewohnerTools;
import entity.EinrichtungenTools;
import op.OPDE;
import op.tools.DlgException;
import op.tools.SYSCalendar;
import op.tools.SYSTools;
import tablemodels.TMVerordnung;

import java.math.BigInteger;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author root
 */
public class DBHandling {

//    /**
//     * Setzt eine Verordnung ab. Die zugehörigen BHPs werden ab JETZT entfernt.
//     *
//     * @param verid   welche Verordnung soll abgesetzt werden.
//     * @param abdatum Ab wann die Verordnung abgesetzt werden soll. <b>NULL bedeutet hier ab JETZT.</b>
//     * @param arztid  welcher Arzt hat sie abgesetzt.
//     * @param khid    welches KH hat sie abgesetzt
//     * @return erfolg
//     */
//    public static boolean absetzen(long verid, long arztid, long khid) {
//        Connection db = OPDE.getDb().db;
//        boolean result = false;
//        boolean doCommit = false;
//        try {
//            // Hier beginnt eine Transaktion, wenn es nicht schon eine gibt.
//            if (db.getAutoCommit()) {
//                db.setAutoCommit(false);
//                db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
//                db.commit();
//                doCommit = true;
//            }
//
//            HashMap hm = new HashMap();
//            hm.put("AbDatum", "!NOW!");
//            hm.put("AbArztID", arztid);
//            hm.put("AbKHID", khid);
//            hm.put("AbUKennung", OPDE.getLogin().getUser().getUKennung());
//            result = op.tools.DBHandling.updateRecord("BHPVerordnung", hm, "VerID", verid);
//            hm.clear();
//            cleanBHP(verid, SYSCalendar.nowDB());
//
//            if (doCommit) {
//                db.commit();
//                db.setAutoCommit(true);
//            }
//
//            result = true;
//        } catch (SQLException ex) {
//            try {
//                if (doCommit) {
//                    db.rollback();
//                }
//                result = false;
//            } catch (SQLException ex1) {
//                new DlgException(ex1);
//                ex1.printStackTrace();
//                System.exit(1);
//            }
//            new DlgException(ex);
//        }
//        return result;
//    }

//    /**
//     * Setzt eine Verordnung ab. Die zugehörigen BHPs werden ab JETZT entfernt. Sie wird mit sofortiger
//     * Wirkung abgesetzt. Abgesetzt wird sie durch den ansetzen Arzt bzw. KH.
//     *
//     * @param verid welche Verordnung soll abgesetzt werden.
//     * @return erfolg
//     */
//    public static boolean absetzen(long verid) {
//        boolean result = false;
//        HashMap input = op.tools.DBRetrieve.getSingleRecord("BHPVerordnung", "VerID", verid);
//        result = absetzen(verid, ((BigInteger) input.get("AnArztID")).longValue(), ((BigInteger) input.get("AnKHID")).longValue());
//        input.clear();
//        return result;
//    }

//    /**
//     * Setzt alle Verordnungen ab, die bis PackungsEnde laufen.
//     *
//     * @param vorid - Vorrat, die zu den Verordnungen gehören.
//     */
//    public static boolean absetzenBisPackEnde2Vorrat(long vorid) {
//        // Das hier sucht alle Verordnungen zu einem bestimmten Vorrat raus,
//        // die bis PackEnde sind.
//        boolean result = true;
//        // #0000042
//        // Korrigierter SQL Ausruck
//        String sql2 = " " +
//                " SELECT DISTINCT bhp.VerID FROM BHPVerordnung bhp " +
//                " INNER JOIN MPVorrat v ON v.BWKennung = bhp.BWKennung " +
//                " INNER JOIN MPBestand b ON bhp.DafID = b.DafID AND v.VorID = b.VorID " +
//                " WHERE b.VorID=? AND bhp.BisPackEnde = 1 " +
//                " AND bhp.AbDatum > now() ";
//        try {
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql2);
//            stmt.setLong(1, vorid);
//            ResultSet rs = stmt.executeQuery();
//            rs.beforeFirst();
//            while (rs.next()) {
//                result &= op.care.verordnung.DBHandling.absetzen(rs.getLong("VerID"));
//            }
//        } catch (SQLException ex) {
//            new DlgException(ex);
//            result = false;
//        }
//        return result;
//    }

//    public static void copy2tmp(long verid)
//            throws SQLException {
//        String sql = "INSERT INTO BHPPlanung (VerID, NachtMo, Morgens, Mittags, Nachmittags, Abends, NachtAb, UhrzeitDosis, Uhrzeit, " +
//                " MaxAnzahl, MaxEDosis, Taeglich, Woechentlich, Monatlich, TagNum, Mon, Die, Mit, Don, Fre, Sam, Son, LDatum, UKennung," +
//                " tmp)" +
//                " SELECT VerID, NachtMo, Morgens, Mittags, Nachmittags, Abends, NachtAb, UhrzeitDosis, Uhrzeit, MaxAnzahl, MaxEDosis, Taeglich, " +
//                " Woechentlich, Monatlich, TagNum, Mon, Die, Mit, Don, Fre, Sam, Son, LDatum, UKennung, ?" +
//                " FROM BHPPlanung WHERE VerID = ? AND tmp = 0";
//        PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//        stmt.setLong(1, OPDE.getLogin().getLoginID());
//        stmt.setLong(2, verid);
//        stmt.executeUpdate();
//        stmt.close();
//    }
//
//    public static void tmp2real(long verid)
//            throws SQLException {
//
//        Connection db = OPDE.getDb().db;
//        boolean doCommit = false;
//        try {
//            // Hier beginnt eine Transaktion, wenn es nicht schon eine gibt.
//            if (db.getAutoCommit()) {
//                db.setAutoCommit(false);
//                db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
//                db.commit();
//                doCommit = true;
//            }
//
//            String delete = "DELETE FROM BHPPlanung WHERE VerID = ? AND tmp = 0";
//            PreparedStatement stmtDel = OPDE.getDb().db.prepareStatement(delete);
//            stmtDel.setLong(1, verid);
//            stmtDel.executeUpdate();
//            stmtDel.close();
//
//            String update = "UPDATE BHPPlanung SET VerID = ?, tmp=0 WHERE tmp = ?";
//            PreparedStatement stmtUpd = OPDE.getDb().db.prepareStatement(update);
//            stmtUpd.setLong(1, verid);
//            stmtUpd.setLong(2, OPDE.getLogin().getLoginID());
//            stmtUpd.executeUpdate();
//            stmtUpd.close();
//
//            if (doCommit) {
//                db.commit();
//                db.setAutoCommit(true);
//            }
//
//        } catch (SQLException ex) {
//            try {
//                if (doCommit) {
//                    db.rollback();
//                }
//            } catch (SQLException ex1) {
//                new DlgException(ex1);
//                ex1.printStackTrace();
//                System.exit(1);
//            }
//            new DlgException(ex);
//        }
//    }
//
//    public static void dropTmp()
//            throws SQLException {
//        String delete = "DELETE FROM BHPPlanung WHERE tmp = ?";
//        PreparedStatement stmtDel = OPDE.getDb().db.prepareStatement(delete);
//        stmtDel.setLong(1, OPDE.getLogin().getLoginID());
//        stmtDel.executeUpdate();
//        stmtDel.close();
//    }

//    /**
//     * Löscht alle <b>heutigen</b> nicht <b>abgehakten</b> BHPs für eine bestimmte Verordnung ab einer bestimmten Tages-Zeit.
//     *
//     * @param ts    ist ein bestimmter Zeitpunkt. Das gilt natürlich nur für den aktuellen Tag. Somit ist
//     *              bei ts nur der Uhrzeit anteil relevant. Über diesen wird die Schicht (bzw. Zeit) ermittelt. Bei BHPs,
//     *              die sich auf eine bestimmte Uhrzeit beziehen, werden nur diejenigen gelöscht, die <b>größer gleich</b> ts sind.
//     * @param verid ist die Verordnung, um die es geht.
//     */
//    public static void cleanBHP(long verid, long ts)
//            throws SQLException {
//        int zeit = SYSCalendar.ermittleZeit(ts);
//        String bhp = "DELETE b.* FROM BHP b INNER JOIN BHPPlanung bhp ON b.BHPPID = bhp.BHPPID " +
//                " WHERE bhp.VerID = ? AND Status = 0 AND Date(Soll)=Date(now()) AND " +
//                " (" +
//                "   ( " +
//                "       ( SZeit > ? )" +
//                "   ) " +
//                "   OR " +
//                "   (" +
//                "       ( SZeit = 0 AND Time(Soll) >= Time(?) )" +
//                "   )" +
//                " )";
//        PreparedStatement stmtDel = OPDE.getDb().db.prepareStatement(bhp);
//        stmtDel.setLong(1, verid);
//        stmtDel.setInt(2, zeit);
//        stmtDel.setTimestamp(3, new Timestamp(ts));
//        stmtDel.executeUpdate();
//        stmtDel.close();
//    }
//
//    /**
//     * Löscht <u>alle</u> nicht <b>abgehakten</b> BHPs für eine bestimmte Verordnung.
//     *
//     * @param verid ist die Verordnung, um die es geht.
//     */
//    public static void cleanBHP(long verid)
//            throws SQLException {
//        String bhp = "DELETE b.* FROM BHP b INNER JOIN BHPPlanung bhp ON b.BHPPID = bhp.BHPPID " +
//                " WHERE bhp.VerID = ? AND b.Status = 0";
//        PreparedStatement stmtDel = OPDE.getDb().db.prepareStatement(bhp);
//        stmtDel.setLong(1, verid);
//        stmtDel.executeUpdate();
//        stmtDel.close();
//    }

//    /**
//     * Löscht <u>alle</u> BHPs für eine bestimmte Verordnung.
//     *
//     * @param verid ist die Verordnung, um die es geht.
//     */
//    public static void deleteBHP(long verid)
//            throws SQLException {
//        String bhp = "DELETE b.* FROM BHP b INNER JOIN BHPPlanung bhp ON b.BHPPID = bhp.BHPPID " +
//                " WHERE bhp.VerID = ?";
//        PreparedStatement stmtDel = OPDE.getDb().db.prepareStatement(bhp);
//        stmtDel.setLong(1, verid);
//        stmtDel.executeUpdate();
//        stmtDel.close();
//    }

//    /**
//     * Gibt eine HTML Darstellung der Verordungen zurück, die in dem übergebenen TableModel enthalten sind.
//     *
//     * @param tmv - TableModel vom Typ TMVerordnungen
//     * @return - HTML Darstellung als String
//     */
//    public static String getVerordnungenAsHTML(TMVerordnung tmv, Bewohner bewohner, int sel[]) {
//        String result = "";
//        String bwkennung = bewohner.getBWKennung();
//        int numVer = tmv.getRowCount();
//        if (numVer > 0) {
//            if (SYSTools.catchNull(bwkennung).equals("")) {
//                result += "<h2>Ärztliche Verordnungen</h2>";
//            } else {
//                result += "<h1>Ärztliche Verordnungen für " + BewohnerTools.getBWLabelText(bewohner) + "</h1>";
//                if (bewohner.getStation() != null) {
//                    result += EinrichtungenTools.getAsText(bewohner.getStation().getEinrichtung());
//                }
//            }
//
//            result += "<table border=\"1\" cellspacing=\"0\"><tr>" +
//                    "<th style=\"width:30%\">Medikament/Massnahme</th><th style=\"width:50%\">Dosierung / Hinweise</th><th style=\"width:20%\">Angesetzt</th></tr>";
//            for (int v = 0; v < numVer; v++) {
//                if (sel == null || Arrays.binarySearch(sel, v) > -1) {
//                    result += "<tr>";
//                    result += "<td valign=\"top\">" + SYSTools.unHTML2(tmv.getValueAt(v, TMVerordnung.COL_MSSN).toString()) + "</td>";
//                    result += "<td valign=\"top\">" + SYSTools.unHTML2(tmv.getValueAt(v, TMVerordnung.COL_Dosis).toString()) + "<br/>";
//                    result += SYSTools.unHTML2(tmv.getValueAt(v, TMVerordnung.COL_Hinweis).toString()) + "</td>";
//                    result += "<td valign=\"top\">" + SYSTools.unHTML2(tmv.getValueAt(v, TMVerordnung.COL_AN).toString()) + "</td>";
//                    //result += "<td>" + SYSTools.unHTML2(tmv.getValueAt(v, TMVerordnung.COL_AB).toString()) + "</td>";
//                    result += "</tr>";
//                }
//            }
//
//            result += "</table>";
//        } else {
//            result += "<h2>Ärztliche Verordnungen</h2><i>zur Zeit gibt es keine Verordnungen</i>";
//        }
//        return result;
//    }

//    /**
//     * Löscht eine Verordnung und die zugehörigen BHPs .
//     *
//     * @param verid ist die Verordnung, um die es geht.
//     */
//    public static void deleteVerordnung(long verid) {
//        Connection db = OPDE.getDb().db;
//        try {
//            // Hier beginnt eine Transaktion
//            db.setAutoCommit(false);
//            db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
//            db.commit();
//
//            if (op.tools.DBHandling.deleteRecords("BHPVerordnung", "VerID", verid) < 0) {
//                throw new SQLException();
//            }
//
//            if (op.tools.DBHandling.deleteRecords("BHPPlanung", "VerID", verid) < 0) {
//                throw new SQLException();
//            }
//
//            String bhp = "DELETE b.* FROM BHP b INNER JOIN BHPPlanung bhp ON b.BHPPID = bhp.BHPPID " +
//                    " WHERE bhp.VerID = ?";
//            PreparedStatement stmtDel = OPDE.getDb().db.prepareStatement(bhp);
//            stmtDel.setLong(1, verid);
//            stmtDel.executeUpdate();
//            stmtDel.close();
//
//
//            db.commit();
//            db.setAutoCommit(true);
//
//        } catch (SQLException ex) {
//            try {
//                db.rollback();
//            } catch (SQLException ex1) {
//                new DlgException(ex1);
//                ex1.printStackTrace();
//                System.exit(1);
//            }
//            new DlgException(ex);
//        }
//    }
}
