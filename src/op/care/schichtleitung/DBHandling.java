/*
 * OffenePflege
 * Copyright (C) 2006-2012 Torsten Löhr
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
package op.care.schichtleitung;

import op.OPDE;

import op.tools.SYSCalendar;
import op.tools.SYSConst;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author root
 */
public class DBHandling {

//    public static String leereBHPs(int schicht, Date datum, String einrichtung) {
//        String filter;
//        if (schicht != SYSConst.ZEIT_ALLES) {
//            filter = " AND ((SZeit >= ? AND SZeit <= ?) OR (SZeit = 0 AND TIME(Soll) >= ? AND TIME(Soll) <= ?)) ";
//        } else {
//            filter = "";
//        }
//        String f = ""; //op.tools.DBRetrieve.createXMLStationListEINR(einrichtung);
//        String sql = "SELECT bw.BWKennung, CONCAT(bw.nachname,', ',bw.vorname) bwname, COUNT(*) numleer, ifnull(abwe.Von, '9999-12-31 23:59:59') abwesend " +
//                " FROM BHPPlanung bhpp " +
//                " INNER JOIN ( " +
//                " 	SELECT * " +
//                " 	FROM BHP bhp " +
//                " 	WHERE DATE(Soll) = ? AND Status = 0 " +
//                " 	) AS t ON t.BHPPID = bhpp.BHPPID " +
//                " INNER JOIN BHPVerordnung v ON bhpp.VerID = v.VerID  " +
//                " INNER JOIN (" +
//                " 	SELECT b1.BWKennung, b1.XML FROM BWInfo b1 " +
//                "   WHERE BWINFTYP = 'station' AND von <= now() AND bis >= now()  " +
//                "   AND XML IN ( " + f + " ) " +
//                " ) AS station ON v.BWKennung = station.BWKennung " +
//                " LEFT OUTER JOIN ( " +
//                "          SELECT BWKennung, Von, Bis FROM BWInfo " +
//                "          WHERE BWINFTYP = 'abwe' AND " +
//                "          von <= now() AND bis >= now() " +
//                "         ) AS abwe ON v.BWKennung = abwe.BWKennung " +
//                " INNER JOIN Bewohner bw ON bw.BWKennung = v.BWKennung " +
//                " WHERE bw.AdminOnly <> 2 " + filter +
//                " GROUP BY v.BWKennung " +
//                " ORDER BY bwname ";
//
////        String sql1 = " SELECT * FROM ( " +
////                " SELECT bw.BWKennung, CONCAT(bw.nachname,', ',bw.vorname) bwname, COUNT(*) numleer, ifnull(abwe.Von, '9999-12-31 23:59:59') abwesend " +
////                " FROM BHP bhp " +
////                " INNER JOIN BHPPlanung bhpp ON bhp.BHPPID = bhpp.BHPPID " +
////                " INNER JOIN BHPVerordnung v ON bhpp.VerID = v.VerID " +
////                "       INNER JOIN (" +
////                "           SELECT b1.BWKennung, b1.XML FROM BWInfo b1" +
////                "           WHERE BWINFTYP = 'station' AND von <= now() AND bis >= now() " +
////                "           AND XML IN (" + f + ") " +
////                "           ) AS station ON v.BWKennung = station.BWKennung " +
////                " LEFT OUTER JOIN ( " +
////                "       SELECT BWKennung, Von, Bis FROM BWInfo " +
////                "       WHERE BWINFTYP = 'abwe' AND " +
////                "       von <= now() AND bis >= now() " +
////                " ) AS abwe ON v.BWKennung = abwe.BWKennung " +
////                " INNER JOIN Bewohner bw ON bw.BWKennung = v.BWKennung " +
////                " WHERE Date(Soll)=? AND Status = 0 AND bw.AdminOnly <> 2 " +
////                filter +
////                " GROUP BY v.BWKennung " +
////                " ORDER BY bwname " +
////                " ) a " +
////                " WHERE numleer > 0";
//
//        String s = "";
//        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
//
//
//        s += "<h2>Anzahl unabgehakter Behandlungspflegen je BewohnerIn</h2" +
//                "<ul>";
//
//        try {
//
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//
//            stmt.setDate(1, new java.sql.Date(datum.getTime()));
//            if (schicht != SYSConst.ZEIT_ALLES) {
//                ArrayList al = SYSCalendar.getZeiten4Schicht((byte) schicht);
//                String zeit1 = al.get(2).toString();
//                String zeit2 = al.get(3).toString();
//                int schicht1 = ((Integer) al.get(0)).intValue();
//                int schicht2 = ((Integer) al.get(1)).intValue();
//                stmt.setInt(2, schicht1);
//                stmt.setInt(3, schicht2);
//                stmt.setString(4, zeit1);
//                stmt.setString(5, zeit2);
//            }
//            ResultSet rs = stmt.executeQuery();
//            rs.last();
//            int numrec = rs.getRow();
//            if (numrec > 0) {
//                rs.beforeFirst();
//                while (rs.next()) {
//                    s += "<li><b>" + rs.getInt("numleer") + ":</b> " + rs.getString("bwname") + " [" + rs.getString("bwkennung") + "] ";
//                    if (rs.getTimestamp("abwesend").before(SYSConst.DATE_BIS_AUF_WEITERES)) {
//                        s += "<i>BewohnerIn ist abwesend seit dem: " + sdf.format(rs.getDate("abwesend")) + "</i>";
//                    }
//                    s += "</li>";
//                }
//                s += "</ul><br/>" + numrec + " BewohnerInnen";
//            } else {
//                s += "<li>Alles erledigt...</li>";
//                s += "</ul>";
//            }
//
//            rs.close();
//            stmt.close();
//        } catch (SQLException ex) {
////            new DlgException(ex);
//        }
//
//        return s;
//    }
//
//    public static String keineTBs(Date datum, String einrichtung) {
//        String f = ""; //op.tools.DBRetrieve.createXMLStationListEINR(einrichtung);
//        String sql = " " +
//                " SELECT CONCAT(bw.nachname,', ',bw.vorname) bwname, bw.bwkennung, ifnull(tb.anz, 0) anzahl, ifnull(abwe.Von, '9999-12-31 23:59:59') abwesend " +
//                " FROM Bewohner bw " +
//                " INNER JOIN BWInfo ba ON bw.BWKennung = ba.BWKennung " +
//                "       INNER JOIN (" +
//                "           SELECT b1.BWKennung, b1.XML FROM BWInfo b1" +
//                "           WHERE BWINFTYP = 'station' AND von <= now() AND bis >= now() " +
//                "           AND XML IN (" + f + ") " +
//                "           ) AS station ON bw.BWKennung = station.BWKennung " +
//                " LEFT OUTER JOIN ( " +
//                "       SELECT BWKennung, count(*) anz FROM Tagesberichte " +
//                "   	WHERE DATE(PIT) = ? " +
//                "       GROUP BY BWKennung " +
//                " ) tb ON tb.BWKennung = bw.BWKennung " +
//                " LEFT OUTER JOIN ( " +
//                "       SELECT BWKennung, Von, Bis FROM BWInfo " +
//                "       WHERE BWINFTYP = 'abwe' AND " +
//                "       von <= ? AND bis >= ? " +
//                " ) AS abwe ON bw.BWKennung = abwe.BWKennung " +
//                " WHERE ba.BWINFTYP = 'hauf' AND ba.von <= ? AND ba.bis >= ? AND bw.AdminOnly <> 2 " +
//                " AND ifnull(tb.anz, 0) = 0 " +
//                " ORDER BY bwname ";
//
//        String s = "";
//        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
//
//        s += "<h2>BewohnerInnen ohne Pflegebericht</h2" +
//                "<ul>";
//
//        try {
//
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            java.sql.Date d = new java.sql.Date(datum.getTime());
//
//            stmt.setDate(1, d);
//            stmt.setDate(2, d);
//            stmt.setDate(3, d);
//            stmt.setDate(4, d);
//            stmt.setDate(5, d);
//
//            ResultSet rs = stmt.executeQuery();
//            rs.last();
//            int numrec = rs.getRow();
//            if (numrec > 0) {
//                rs.beforeFirst();
//                while (rs.next()) {
//                    s += "<li>" + rs.getString("bwname") + " [" + rs.getString("bwkennung") + "] ";
//                    if (rs.getTimestamp("abwesend").before(SYSConst.DATE_BIS_AUF_WEITERES)) {
//                        s += "<i>BewohnerIn ist abwesend seit dem: " + sdf.format(rs.getDate("abwesend")) + "</i>";
//                    }
//                    s += "</li>";
//                }
//                s += "</ul><br/>" + numrec + " BewohnerInnen";
//            } else {
//                s += "<li>Alles erledigt...</li>";
//                s += "</ul>";
//            }
//
//            rs.close();
//            stmt.close();
//        } catch (SQLException ex) {
////            new DlgException(ex);
//        }
//
//        return s;
//    }
//
//    public static String geringeVorraete(String einrichtung) {
//        String f = ""; // op.tools.DBRetrieve.createXMLStationListEINR(einrichtung);
//        String sql = " " +
//                " SELECT DISTINCT CONCAT(bw.nachname,', ',bw.vorname) bwname, v.BWKennung, M.Bezeichnung mptext, D.Zusatz, F.Zubereitung " +
//                " FROM BHPVerordnung v " +
//                " INNER JOIN Bewohner bw ON bw.BWKennung = v.BWKennung  " +
//                " LEFT OUTER JOIN MPDarreichung D ON v.DafID = D.DafID " +
//                " LEFT OUTER JOIN MProdukte M ON M.MedPID = D.MedPID " +
//                " LEFT OUTER JOIN MPFormen F ON D.FormID = F.FormID " +
//                " LEFT OUTER JOIN Situationen S ON v.SitID = S.SitID " +
//                "       INNER JOIN (" +
//                "           SELECT b1.BWKennung, b1.XML FROM BWInfo b1" +
//                "           WHERE BWINFTYP = 'station' AND von <= now() AND bis >= now() " +
//                "           AND XML IN (" + f + ") " +
//                "           ) AS station ON bw.BWKennung = station.BWKennung " +
//                " LEFT OUTER JOIN ( " +
//                // Diese Subselect Kaskade (alles was zu Ausdruck a gehört) dient der Beschleunigung. Dadurch wird der Suchraum der zwischen den Joins
//                // erheblich eingeschränkt.
//                "       SELECT DISTINCT a.VorID, vrr.BWKennung, b.DafID, a.bsaldo FROM ( " +
//                "           SELECT best.VorID, best.DafID, SUM(saldo) bsaldo FROM MPBestand best " +
//                "           INNER JOIN (" +
//                "               SELECT BestID, sum(Menge) saldo FROM MPBuchung " +
//                "               GROUP BY BestID " +
//                "           ) buch ON buch.BestID = best.BestID " +
//                "           GROUP BY VorID " +
//                "       ) a INNER JOIN ( " +
//                "               SELECT best.VorID, best.DafID FROM MPBestand best " +
//                "       ) b ON a.VorID = b.VorID " +
//                "       INNER JOIN MPVorrat vrr ON a.VorID = vrr.VorID " +
//                "       WHERE vrr.Bis = '9999-12-31 23:59:59' " +
//                " ) vor ON vor.BWKennung = v.BWKennung AND vor.DafID = v.DafID " +
//                " WHERE bw.AdminOnly <> 2 AND ifnull(vor.bsaldo, 0) = 0 AND v.DafID > 0 AND Date(v.AnDatum) <= now() AND v.AbDatum > now() " +
//                " ORDER BY bwname, bwkennung, mptext ";
//
//        String s = "";
//        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
//
//        s += "<h2>BewohnerInnen / Verordnungen mit leeren Vorräten per: " + sdf.format(SYSCalendar.today_date()) + "</h2" +
//                "<ul>";
//
//        try {
//
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//
//            //stmt.setString(1, einrichtung);
//
//            ResultSet rs = stmt.executeQuery();
//            rs.last();
//            int numrec = rs.getRow();
//            if (numrec > 0) {
//                rs.beforeFirst();
//                while (rs.next()) {
//                    s += "<li><b>" + rs.getString("bwname") + " [" + rs.getString("bwkennung") + "]</b> ";
////                    if (rs.getTimestamp("abwesend").before(SYSConst.DATE_BIS_AUF_WEITERES)){
////                        s += "<i>BewohnerIn ist abwesend seit dem: "+sdf.format(rs.getDate("abwesend")) + "</i>";
////                    }
//                    s += rs.getString("mptext") + " " + rs.getString("Zusatz") + " " + rs.getString("Zubereitung");
//                    s += "</li>";
//                }
//                s += "</ul><br/>" + numrec + " BewohnerInnen";
//            } else {
//                s += "<li>Keine leeren Vorräte gefunden...</li>";
//                s += "</ul>";
//            }
//
//            rs.close();
//            stmt.close();
//        } catch (SQLException ex) {
////            new DlgException(ex);
//        }
//
//        return s;
//    }
}

