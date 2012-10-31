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
package op.controlling;

import entity.*;
import entity.reports.NReport;
import entity.reports.NReportTools;
import op.OPDE;
import op.tools.*;

import javax.swing.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * @author tloehr
 */
@Deprecated
public class DBHandling {


//
//
//
////    public static String getSozialBerichte(EntityManager em, String bwkennung, String ukennung, int headertiefe, int sozialwochen) {
////
////        try {
////
//////        Query query = em.createNamedQuery("NReportTAGS.findByKurzbezeichnung");
//////        query.setParameter("kurzbezeichnung", "soz");
//////        NReportTAGS sozTag = (NReportTAGS) query.getSingleResult();
//////
//////        NReportTools.getBerichteASHTML(em, "", sozTag, headertiefe,sozialwochen );
////
////        StringBuilder html = new StringBuilder(1000);
////        String sql = "" +
////                " SELECT b.nachname, b.vorname, b.geschlecht, b.bwkennung, tb.Text, Date(tb.PIT) Datum, tb.UKennung, tb.Dauer " +
////                " FROM Bewohner b " +
////                " INNER JOIN ResInfo ba ON b.BWKennung = ba.BWKennung  " +
////                " LEFT OUTER JOIN (" +
////                "       SELECT BWKennung, UKennung, Text, PIT, Dauer FROM Tagesberichte WHERE Sozial > 0 " +
////                "       AND Date(PIT) >= DATE_ADD(now(), INTERVAL ? WEEK) AND Date(PIT) <= Date(now()) " +
////                " ) tb ON tb.BWKennung = b.BWKennung " +
////                " WHERE " +
////                (bwkennung.equals("") ? "" : " BWKennung = ? AND ") +
////                (ukennung.equals("") ? "" : " UKennung = ? AND ") +
////                " ba.BWINFTYP = 'hauf' AND ba.von <= NOW() AND ba.bis >= NOW() AND b.AdminOnly <> 2 " +
////                " ORDER BY b.BWKennung, tb.PIT ";
////
////
////            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
////            stmt.setInt(1, sozialwochen * -1);
////            if (!bwkennung.equals("")) {
////                stmt.setString(2, bwkennung);
////                if (!ukennung.equals("")) {
////                    stmt.setString(3, ukennung);
////                }
////            } else if (!ukennung.equals("")) {
////                stmt.setString(3, ukennung);
////            }
////            ResultSet rs = stmt.executeQuery();
////            DateFormat df = DateFormat.getDateInstance();
////            if (rs.first()) {
////                html.append("<h" + headertiefe + ">");
////                html.append("Aktivitäten des Sozialen Dienstes");
////                html.append("</h" + headertiefe + ">");
////                rs.beforeFirst();
////                String prevBW = "";
////                int summeMinuten = 0;
////                while (rs.next()) {
////                    String name = SYSTools.anonymizeBW(rs.getString("Nachname"), rs.getString("Vorname"), rs.getString("BWKennung"), rs.getInt("geschlecht"));
////                    String bwk = rs.getString("bwkennung");
////                    String text = rs.getString("text");
////                    int min = rs.getInt("Dauer");
////                    String uk = rs.getString("ukennung");
////                    Date datum = rs.getDate("Datum");
////                    if (rs.isFirst() || !prevBW.equalsIgnoreCase(bwk)) {
////                        if (!rs.isFirst()) {
////                            // Zusammenfassung des letzten Durchgangs.
////                            html.append("<tr><td></td><td align=\"right\"><b>Summe Minuten:</b></td><td><b>" + summeMinuten + "</b></td><td></td></tr>");
////                            html.append("</table>");
////                            summeMinuten = 0;
////                        }
////                        html.append("<h" + (headertiefe + 1) + ">");
////                        html.append(name + " [" + bwk + "]");
////                        html.append("</h" + (headertiefe + 1) + ">");
////                        html.append("<table border=\"1\"><tr><th>Datum</th><th>Text</th><th>Dauer</th><th>UKennung</th></tr>");
////                        prevBW = bwk;
////                    }
////                    html.append("<tr>");
////                    if (SYSTools.catchNull(uk).equals("")) {
////                        html.append("<td align=\"center\">--</td>");
////                        html.append("<td><b>Keine BV Aktivitäten gefunden.</b></td>");
////                        html.append("<td align=\"center\">--</td>");
////                        html.append("<td align=\"center\">--</td>");
////                    } else {
////                        summeMinuten += min;
////                        html.append("<td>" + df.format(datum) + "</td>");
////                        html.append("<td>" + text + "</td>");
////                        html.append("<td>" + min + "</td>");
////                        html.append("<td>" + uk + "</td>");
////                    }
////                    html.append("</tr>");
////                }
////                html.append("<tr><td></td><td align=\"right\"><b>Summe Minuten:</b></td><td><b>" + summeMinuten + "</b></td><td></td></tr>");
////                html.append("</table>");
////            }
////
////        } catch (SQLException sQLException) {
////            // new DlgException(sQLException);
////        }
////        return html.toString();
////    }
//

//
////    public static String getSozialZeiten(int headertiefe, Date monat) {
////        StringBuilder html = new StringBuilder(1000);
////        SimpleDateFormat df = new SimpleDateFormat("MMMM yyyy");
////
////        String sql = "" +
////                " SELECT s.Name, s.BWKennung, " +
////                "               s.dauer 'Dauer (Minuten)', ROUND(s.dauer/60, 2) 'Dauer (Stunden)', ROUND(s.dauer/60/?,2) '(Stunden-Schnitt pro Tag)', " +
////                "               p.dauer 'PEA (Minuten)', ROUND(p.dauer/60,2) 'PEA (Stunden)', ROUND(p.dauer/60/?,2) '(Stunden-Schnitt pro Tag)' " +
////                " FROM ( " +
////                "   SELECT CONCAT(b.nachname,', ',b.vorname) Name, b.BWKennung, ifnull(SUM(sdauer), 0) dauer " +
////                "   FROM Bewohner b " +
////                "   INNER JOIN ResInfo ba1 ON b.BWKennung = ba1.BWKennung " +
////                "   LEFT OUTER JOIN ( " +
////                "       SELECT BWKennung, Date(PIT) Datum, UKennung, Text, Dauer sdauer FROM Tagesberichte " +
////                "       WHERE Sozial=1 AND Date(PIT) >= DATE(?) AND Date(PIT) <= DATE(?) " +
////                "   ) s ON s.BWKennung = b.BWKennung " +
////                "   WHERE ba1.BWINFTYP = 'hauf' AND ( " +
////                "       (DATE(ba1.von) <= DATE(?) AND DATE(ba1.bis) >= DATE(?)) " +
////                "       OR " +
////                "       (DATE(ba1.von) <= DATE(?) AND DATE(ba1.bis) >= DATE(?))  " +
////                "       OR " +
////                "       (DATE(ba1.von) > DATE(?) AND DATE(ba1.bis) < DATE(?))  " +
////                "   ) " +
////                "   AND b.AdminOnly <> 2 " +
////                "   GROUP BY BWKennung " +
////                " ) s INNER JOIN (" +
////                "   SELECT CONCAT(b.nachname,', ',b.vorname) Name, b.BWKennung, ifnull(SUM(sdauer), 0) dauer " +
////                "   FROM Bewohner b " +
////                "   INNER JOIN ResInfo ba1 ON b.BWKennung = ba1.BWKennung " +
////                "   LEFT OUTER JOIN ( " +
////                "       SELECT BWKennung, Date(PIT) Datum, UKennung, Text, Dauer sdauer FROM Tagesberichte  " +
////                "       WHERE PEA=1 AND Date(PIT) >= DATE(?) AND Date(PIT) <= DATE(?)  " +
////                "   ) s ON s.BWKennung = b.BWKennung  " +
////                "   WHERE ba1.BWINFTYP = 'hauf' AND ( " +
////                "       (DATE(ba1.von) <= DATE(?) AND DATE(ba1.bis) >= DATE(?)) " +
////                "       OR " +
////                "       (DATE(ba1.von) <= DATE(?) AND DATE(ba1.bis) >= DATE(?))  " +
////                "       OR " +
////                "       (DATE(ba1.von) > DATE(?) AND DATE(ba1.bis) < DATE(?))  " +
////                "   ) " +
////                "   AND b.AdminOnly <> 2   " +
////                "   GROUP BY BWKennung   " +
////                " ) p ON s.BWKennung = p.BWKennung" +
////                " order by s.Name ";
////
////        try {
////            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
////            java.sql.Date von = new java.sql.Date(SYSCalendar.bom(monat).getTime());
////            java.sql.Date bis = new java.sql.Date(SYSCalendar.eom(monat).getTime());
////            int daysinmonth = SYSCalendar.eom(SYSCalendar.toGC(monat));
////
////            stmt.setInt(1, daysinmonth);
////            stmt.setInt(2, daysinmonth);
////
////            stmt.setDate(3, von);
////            stmt.setDate(4, bis);
////            stmt.setDate(5, von);
////            stmt.setDate(6, von);
////            stmt.setDate(7, bis);
////            stmt.setDate(8, bis);
////            stmt.setDate(9, von);
////            stmt.setDate(10, bis);
////
////            stmt.setDate(11, von);
////            stmt.setDate(12, bis);
////            stmt.setDate(13, von);
////            stmt.setDate(14, von);
////            stmt.setDate(15, bis);
////            stmt.setDate(16, bis);
////            stmt.setDate(17, von);
////            stmt.setDate(18, bis);
////
////            ResultSet rs = stmt.executeQuery();
////            //DateFormat df = DateFormat.getDateInstance();
////            if (rs.first()) {
////                html.append("<h" + headertiefe + ">");
////                html.append("Zeiten des Sozialen Dienstes je BewohnerIn");
////                html.append("</h" + headertiefe + ">");
////
////                headertiefe++;
////
////                html.append("<h" + headertiefe + ">");
////                html.append("Zeitraum: " + df.format(monat));
////                html.append("</h" + headertiefe + ">");
////
////                html.append(SYSTools.rs2html(rs, true));
////
////                html.append("<p><b>PEA:</b> Personen mit erheblich eingeschränkter Alltagskompetenz (gemäß §87b SGB XI)." +
////                        " Der hier errechnete Wert ist der <b>Anteil</b> für die PEA Leistungen, die in den allgemeinen Sozialzeiten" +
////                        " mit enthalten sind.</p>");
////
////            }
////
////        } catch (SQLException sQLException) {
////            // new DlgException(sQLException);
////        }
////        return html.toString();
////    }
//
//    public static String getAktiveVorraeteOhneBestandImAnbruch(int headertiefe) {
//        StringBuilder html = new StringBuilder(1000);
//        String sql = "" +
//                " SELECT CONCAT(b.nachname,', ',b.vorname) Name, b.BWKennung, v.VorID, v.Text " +
//                " FROM Bewohner b " +
//                " INNER JOIN ResInfo ba1 ON b.BWKennung = ba1.BWKennung  " +
//                " INNER JOIN MPVorrat v ON v.BWKennung = b.BWKennung " +
//                " INNER JOIN MPBestand best ON v.VorID = best.VorID " +
//                " INNER JOIN BHPVerordnung ver ON ver.DafID = best.DafID AND ver.BWKennung = b.BWKennung " +
//                " WHERE ba1.BWINFTYP = 'hauf' AND ba1.von <= NOW() AND ba1.bis >= NOW() AND b.AdminOnly <> 2 " +
//                " AND v.Von <= now() AND v.Bis > now() AND ver.AnDatum < now() AND ver.AbDatum > now() " +
//                " AND v.VorID NOT IN ( " +
//                " 	SELECT VorID FROM MPBestand " +
//                " 	WHERE Anbruch < '9999-12-31 23:59:59' AND Aus = '9999-12-31 23:59:59' " +
//                " ) " +
//                " ORDER BY Name ";
//
//        try {
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            ResultSet rs = stmt.executeQuery();
//            //DateFormat df = DateFormat.getDateInstance();
//            if (rs.first()) {
//                html.append("<h" + headertiefe + ">");
//                html.append("Aktive Vorräte ohne Bestand im Anbruch");
//                html.append("</h" + headertiefe + ">");
//
//                html.append(SYSTools.rs2html(rs, true));
//
//            }
//
//        } catch (SQLException sQLException) {
//            // new DlgException(sQLException);
//        }
//        return html.toString();
//    }
//
//    public static String getNichtAbgehakteBHPs(int headertiefe, int days) {
//        StringBuilder html = new StringBuilder(1000);
//        String sql = "" +
//                " SELECT CONCAT(b.nachname,', ',b.vorname) Name, b.BWKennung, bhp.Soll, ifnull(mp.Bezeichnung, '--') Medikament, ifnull(daf.Zusatz, '--') Zusatz, m.Bezeichnung " +
//                " FROM BHP bhp " +
//                " INNER JOIN BHPPlanung bhpp ON bhp.BHPPID = bhpp.BHPPID " +
//                " INNER JOIN BHPVerordnung v ON bhpp.VerID = v.VerID " +
//                " INNER JOIN Bewohner b ON b.BWKennung = v.BWKennung " +
//                " LEFT OUTER JOIN MPDarreichung daf ON daf.DafID = v.DafID " +
//                " LEFT OUTER JOIN MProdukte mp ON mp.MedPID = daf.MedPID " +
//                " INNER JOIN Massnahmen m ON m.MassID = v.MassID " +
//                " WHERE status = 0 " +
//                " AND Date(Soll) >= DATE_ADD(now(), INTERVAL ? DAY) AND Date(Soll) < DATE(now()) AND b.AdminOnly <> 2 " +
//                " ORDER BY name, Soll ";
//
//        try {
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            stmt.setInt(1, days * -1);
//            ResultSet rs = stmt.executeQuery();
//            //DateFormat df = DateFormat.getDateInstance();
//            if (rs.first()) {
//                html.append("<h" + headertiefe + ">");
//                html.append("Nicht abgehakte BHPs");
//                html.append("</h" + headertiefe + ">");
//
//                html.append(SYSTools.rs2html(rs, false));
//
//            }
//
//        } catch (SQLException sQLException) {
//            // new DlgException(sQLException);
//        }
//        return html.toString();
//    }
//
//    public static String getAblaufendePlanungen(int headertiefe, int days) {
//        StringBuilder html = new StringBuilder(1000);
//        String sql = "" +
//                " SELECT CONCAT(b.nachname,', ',b.vorname) Name, b.BWKennung, p.Stichwort, p.NKontrolle Kontrolldatum " +
//                " FROM Planung p " +
//                " INNER JOIN Bewohner b ON b.BWKennung = p.BWKennung " +
//                " WHERE Bis = '9999-12-31 23:59:59' AND NKontrolle < DATE_ADD(now(), INTERVAL ? DAY) AND b.AdminOnly <> 2 " +
//                " ORDER BY Name, NKontrolle ";
//
//        try {
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            stmt.setInt(1, days);
//            ResultSet rs = stmt.executeQuery();
//            //DateFormat df = DateFormat.getDateInstance();
//            if (rs.first()) {
//                html.append("<h" + headertiefe + ">");
//                html.append("Planungen, die bald enden / abgelaufen sind");
//                html.append("</h" + headertiefe + ">");
//
//                html.append(SYSTools.rs2html(rs, false));
//
//            }
//
//        } catch (SQLException sQLException) {
//            // new DlgException(sQLException);
//        }
//        return html.toString();
//    }
//
//    public static String getBilanzen(int headertiefe, Date monat, Object[] o) {
//        Date vormonat = SYSCalendar.addField(monat, -1, GregorianCalendar.MONTH);
//        SimpleDateFormat df = new SimpleDateFormat("MMMM yyyy");
//        int progress = 0;
//        boolean isCancelled = false;
//        JProgressBar pb = null;
//        JLabel lbl = null;
//        if (o != null) {
//            pb = (JProgressBar) o[0];
//            lbl = (JLabel) o[1];
//            isCancelled = (Boolean) o[2];
//            pb.setIndeterminate(true);
//            lbl.setText("Ein-/Ausfuhr/Bilanz: Datenbankzugriff");
//        }
//        double avgEin = 0d;
//        double avgAus = 0d;
//        double sumBilanz = 0d;
//        String prev = "";
//        StringBuilder html = new StringBuilder(1000);
//        String sqlVormonat = "" +
//                " SELECT avg(ein.EINFUHR) Einfuhr, ifnull(avg(aus.AUSFUHR), 0) Ausfuhr, ifnull(sum(ein.EINFUHR)+sum(aus.AUSFUHR), 0) BILANZ FROM " +
//                " (" +
//                "   SELECT Pit, Date(PIT), bw.BWKennung, SUM(Wert) AUSFUHR FROM BWerte bw" +
//                "   WHERE ReplacedBy = 0 AND Wert < 0 AND bw.BWKennung = ? AND XML='<BILANZ/>' AND Date(PIT) >= DATE(?) AND Date(PIT) <= DATE(?)" +
//                "   GROUP BY bw.BWKennung, Date(PIT) " +
//                " ) aus " +
//                " RIGHT OUTER JOIN " +
//                " (" +
//                "   SELECT Pit, Date(PIT), bw.BWKennung, SUM(Wert) EINFUHR FROM BWerte bw " +
//                "   WHERE ReplacedBy = 0 AND Wert > 0 AND bw.BWKennung = ? AND XML='<BILANZ/>' AND Date(PIT) >= DATE(?) AND Date(PIT) <= DATE(?) " +
//                "   GROUP BY bw.BWKennung, Date(PIT) " +
//                " ) ein " +
//                " ON aus.BWKennung = ein.BWKennung AND Date(aus.PIT) = Date(ein.PIT) " +
//                " INNER JOIN Bewohner b ON ein.BWKennung = b.BWKennung " +
//                " GROUP BY b.BWKennung ";
//        String sql = "" +
//                " SELECT ein.BWKennung, DATE_FORMAT(ein.PIT, '%d.%c.%Y') Datum, ein.Einfuhr, ifnull(aus.AUSFUHR, 0) Ausfuhr, ifnull((ein.EINFUHR+aus.AUSFUHR), 0) BILANZ FROM " +
//                " ( " +
//                "   SELECT PIT, bw.BWKennung, SUM(Wert) AUSFUHR FROM BWerte bw " +
//                "   INNER JOIN Bewohner b ON b.BWKennung = bw.BWKennung " +
//                "   WHERE ReplacedBy = 0 AND Wert < 0 AND AdminOnly <> 2 AND XML='<BILANZ/>' AND Date(PIT) >= DATE(?) AND Date(PIT) <= DATE(?) " +
//                "   GROUP BY bw.BWKennung, Date(PIT) " +
//                " ) aus " +
//                " RIGHT OUTER JOIN " +
//                " (" +
//                "   SELECT PIT, bw.BWKennung, SUM(Wert) EINFUHR FROM BWerte bw " +
//                "   INNER JOIN Bewohner b ON b.BWKennung = bw.BWKennung " +
//                "   WHERE ReplacedBy = 0 AND Wert > 0 AND AdminOnly <> 2 AND XML='<BILANZ/>' AND Date(PIT) >= DATE(?) AND Date(PIT) <= DATE(?) " +
//                "   GROUP BY bw.BWKennung, Date(PIT) " +
//                " ) ein " +
//                " ON aus.BWKennung = ein.BWKennung AND Date(aus.PIT) = Date(ein.PIT)" +
//                " INNER JOIN Bewohner b ON ein.BWKennung = b.BWKennung " +
//                " ORDER BY ein.BWKennung, Date(ein.PIT) ";
//
//        try {
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            PreparedStatement stmtVormonat = OPDE.getDb().db.prepareStatement(sqlVormonat);
//            stmt.setDate(1, new java.sql.Date(SYSCalendar.bom(monat).getTime()));
//            stmt.setDate(2, new java.sql.Date(SYSCalendar.eom(monat).getTime()));
//            stmt.setDate(3, new java.sql.Date(SYSCalendar.bom(monat).getTime()));
//            stmt.setDate(4, new java.sql.Date(SYSCalendar.eom(monat).getTime()));
//
//            // BWKennung wird in der Schleife gesetzt.
//            stmtVormonat.setDate(2, new java.sql.Date(SYSCalendar.bom(vormonat).getTime()));
//            stmtVormonat.setDate(3, new java.sql.Date(SYSCalendar.eom(vormonat).getTime()));
//            stmtVormonat.setDate(5, new java.sql.Date(SYSCalendar.bom(vormonat).getTime()));
//            stmtVormonat.setDate(6, new java.sql.Date(SYSCalendar.eom(vormonat).getTime()));
//
//            ResultSet rs = stmt.executeQuery();
//
//            if (rs.first()) {
//
//                String bwkennung = "";
//
//                if (pb != null) {
//                    rs.last();
//                    pb.setMaximum(rs.getRow());
//                    pb.setIndeterminate(false);
//                }
//
//                rs.beforeFirst();
//
//                html.append("<h" + headertiefe + ">");
//                html.append("Ein-, Ausfuhr / Bilanzen");
//                html.append("</h" + headertiefe + ">");
//
//                ResultSetMetaData md = rs.getMetaData();
//                int count = md.getColumnCount();
//
//                int rows = 0;
//                isCancelled = (Boolean) o[2];
//                int zieltrink = 0;
//                boolean bilanz = false;
//                while (!isCancelled && rs.next()) {
//                    if (pb != null) {
//                        progress++;
//                        pb.setValue(progress);
//                    }
//                    rows++;
//                    bwkennung = rs.getString("BWKennung");
//                    if (!prev.equalsIgnoreCase(bwkennung)) {
//                        if (rs.getRow() > 1) {
//                            avgEin = avgEin / rows;
//                            avgAus = avgAus / rows;
//
//                            avgEin = Math.round(avgEin * 100d) / 100d;
//                            avgAus = Math.round(avgAus * 100d) / 100d;
//                            //String.format(prev, arg1)
//
//
//                            html.append("<tr><td></td><td>" + df.format(monat) + "</td><td>&Oslash; " + avgEin + "</td><td>&Oslash; " + avgAus + "</td><td>&Sigma; " + sumBilanz + "</td></tr>");
//
//                            // Vormonat berechnen
//                            stmtVormonat.setString(1, prev);
//                            stmtVormonat.setString(4, prev);
//                            ResultSet rsVormonat = stmtVormonat.executeQuery();
//
//                            if (rsVormonat.first()) {
//                                double avgEinVor = SYSTools.roundScale2(rsVormonat.getDouble("Einfuhr"));
//                                double avgAusVor = SYSTools.roundScale2(rsVormonat.getDouble("Ausfuhr"));
//                                double sumBilanzVor = SYSTools.roundScale2(rsVormonat.getDouble("Bilanz"));
//                                html.append("<tr><td></td><td>" + df.format(vormonat) + "</td><td>&Oslash; " + avgEinVor + "</td><td>&Oslash; " + avgAusVor + "</td><td>&Sigma; " + sumBilanzVor + "</td></tr>");
//                            }
//
//                            rsVormonat.close();
//
//                            html.append("</table>");
//                            avgEin = 0;
//                            avgAus = 0;
//                            sumBilanz = 0;
//                            rows = 0;
//                        }
//
//                        prev = bwkennung;
//                        String bwlabel = "";//SYSTools.getBWLabel(bwkennung);
//
//                        zieltrink = 0;
////                        ResInfo bwinfo3 = new ResInfo(bwkennung, "ZIELTRINK", SYSCalendar.nowDBDate());
////                        if (bwinfo3.getAttribute().size() > 0) {
////                            HashMap antwort = (HashMap) ((HashMap) bwinfo3.getAttribute().get(0)).get("antwort");
////                            zieltrink = Integer.parseInt(antwort.get("zieltrinkmenge").toString());
////                        }
////                        bwinfo3.cleanup();
////
////                        bilanz = false;
////                        ResInfo bwinfo4 = new ResInfo(bwkennung, "CONTROL", SYSCalendar.nowDBDate());
////                        if (bwinfo4.getAttribute().size() > 0) {
////                            HashMap antwort = (HashMap) ((HashMap) bwinfo4.getAttribute().get(0)).get("antwort");
////                            bilanz = antwort.get("c.bilanz").toString().equalsIgnoreCase("true");
////                        }
////                        bwinfo4.cleanup();
//
//
//                        if (lbl != null) {
//                            lbl.setText("Ein-/Ausfuhr/Bilanz: " + bwlabel);
//                        }
//
//                        html.append("<h" + (headertiefe + 1) + ">");
//                        html.append(bwlabel);
//                        html.append("</h" + (headertiefe + 1) + ">");
//                        if (zieltrink > 0) {
//                            html.append("<h" + (headertiefe + 2) + ">Zieltrinkmenge (ZTM): " + zieltrink + " ml in 24h</h" + (headertiefe + 2) + ">");
//                        }
//                        if (!bilanz) {
//                            html.append("<h" + (headertiefe + 2) + ">Keine Bilanzierung vorgesehen</h" + (headertiefe + 2) + ">");
//                        }
//                        html.append("<table border=1>");
//                        html.append("<tr>");
//                        for (int i = 1; i <= count; i++) {
//                            html.append("<th>");
//                            html.append(md.getColumnLabel(i));
//                            html.append("</th>");
//                        }
//                        if (zieltrink > 0) {
//                            html.append("<th>Differenz zur ZTM</th>");
//                        }
//                        html.append("</tr>");
//
//                    }
//
//                    html.append("<tr>");
//                    for (int i = 1; i <= count; i++) {
//                        html.append("<td>");
//                        html.append(rs.getString(i));
//                        html.append("</td>");
//                    }
//                    if (zieltrink > 0) {
//                        html.append("<td>" + (rs.getDouble("Einfuhr") - zieltrink) + "</td>");
//                    }
//                    avgEin += rs.getDouble("Einfuhr");
//                    avgAus += rs.getDouble("Ausfuhr");
//                    sumBilanz += rs.getDouble("Bilanz");
//
//                    html.append("</tr>");
//                    isCancelled = (Boolean) o[2];
//                }
//                avgEin = avgEin / rows;
//                avgAus = avgAus / rows;
//                avgEin = Math.round(avgEin * 100d) / 100d;
//                avgAus = Math.round(avgAus * 100d) / 100d;
//                html.append("<tr><td></td><td></td><td>&Oslash; " + avgEin + "</td><td>&Oslash; " + avgAus + "</td><td>&Sigma; " + sumBilanz + "</td></tr>");
//                // Vormonat berechnen
//                stmtVormonat.setString(1, bwkennung);
//                stmtVormonat.setString(4, bwkennung);
//                ResultSet rsVormonat = stmtVormonat.executeQuery();
//
//                if (rsVormonat.first()) {
//                    double avgEinVor = SYSTools.roundScale2(rsVormonat.getDouble("Einfuhr"));
//                    double avgAusVor = SYSTools.roundScale2(rsVormonat.getDouble("Ausfuhr"));
//                    double sumBilanzVor = SYSTools.roundScale2(rsVormonat.getDouble("Bilanz"));
//                    html.append("<tr><td></td><td>" + df.format(vormonat) + "</td><td>&Oslash; " + avgEinVor + "</td><td>&Oslash; " + avgAusVor + "</td><td>&Sigma; " + sumBilanzVor + "</td></tr>");
//                }
//
//                rsVormonat.close();
//                html.append("</table>");
//            }
//
//            rs.close();
//            stmtVormonat.close();
//            stmt.close();
//
//        } catch (SQLException sQLException) {
//            // new DlgException(sQLException);
//        }
//
//        isCancelled = (Boolean) o[2];
//        String s = "";
//        if (!isCancelled) {
//            s = html.toString();
//        }
//        return s;
//    }
//
//    public static String getGeringeVorraete(int headertiefe, double percent, Object[] o) {
//        StringBuilder html = new StringBuilder(1000);
//        JProgressBar pb = null;
//        JLabel lbl = null;
//        if (o != null) {
//            pb = (JProgressBar) o[0];
//            lbl = (JLabel) o[1];
//            pb.setIndeterminate(true);
//            lbl.setText("Medikamenten Bestandsermittlung");
//        }
//        String sql = "" +
//                " SELECT DISTINCT CONCAT(bw.nachname,', ',bw.vorname) Name, v.BWKennung, M.Bezeichnung Praeparat, " +
//                " D.Zusatz, F.Zubereitung, " +
//                " CASE F.PackEinheit WHEN 1 THEN 'Stück' WHEN 2 THEN 'ml' WHEN 3 THEN 'l' WHEN 4 THEN 'mg' WHEN 5 THEN 'g' WHEN 6 THEN 'cm' WHEN 7 THEN 'm' ELSE '!FEHLER!' END Packungseinheit, " +
//                " F.AnwText Anwendungstext, " +
//                " CASE F.AnwEinheit WHEN 1 THEN 'Stück' WHEN 2 THEN 'ml' WHEN 3 THEN 'l' WHEN 4 THEN 'mg' WHEN 5 THEN 'g' WHEN 6 THEN 'cm' WHEN 7 THEN 'm' ELSE '!FEHLER!' END Anwendungseinheit, " +
//                " ifnull(vor.saldo,0) Bestand, " +
//                " ROUND(if(vor.saldo IS NULL OR vor.saldo <= 0, 0, saldo/ifnull(vor.anfang,0) * 100)) 'Restquote%', " +
//                " ifnull(vor.BestID, '<b>OHNE ANBRUCH</b>') anbruchnr " +
//                " FROM BHPVerordnung v " +
//                " INNER JOIN Bewohner bw ON bw.BWKennung = v.BWKennung  " +
//                " LEFT OUTER JOIN MPDarreichung D ON v.DafID = D.DafID " +
//                " LEFT OUTER JOIN MProdukte M ON M.MedPID = D.MedPID " +
//                " LEFT OUTER JOIN MPFormen F ON D.FormID = F.FormID " +
//                " LEFT OUTER JOIN Situationen S ON v.SitID = S.SitID " +
//                " LEFT OUTER JOIN ( " +
//                "       SELECT DISTINCT a.VorID, vrr.BWKennung, b.DafID, a.saldo, c.saldo anfang, c.BestID FROM ( " +
//                "               SELECT best.VorID, best.DafID, sum(buch.Menge) saldo FROM MPBestand best " +
//                "               INNER JOIN MPBuchung buch ON buch.BestID = best.BestID  " +
//                "               GROUP BY VorID " +
//                "       ) a INNER JOIN ( " +
//                "               SELECT best.VorID, best.DafID FROM MPBestand best " +
//                "       ) b ON a.VorID = b.VorID " +
//                " 	INNER JOIN ( " +
//                "               SELECT best.VorID, best.DafID, best.BestID, sum(buch.Menge) saldo FROM MPBestand best " +
//                "               INNER JOIN MPBuchung buch ON buch.BestID = best.BestID  " +
//                " 	        WHERE best.Aus = '9999-12-31 23:59:59' AND buch.Status = 1" +
//                "               GROUP BY VorID " +
//                "      ) c ON c.VorID = b.VorID " +
//                "      INNER JOIN MPVorrat vrr ON a.VorID = vrr.VorID " +
//                "      WHERE vrr.Bis = '9999-12-31 23:59:59' " +
//                " ) vor ON vor.BWKennung = v.BWKennung AND vor.DafID = v.DafID " +
//                " WHERE bw.AdminOnly <> 2 AND (vor.saldo <= vor.anfang*? OR vor.BestID is null) AND v.DafID > 0 AND Date(v.AnDatum) <= now() AND v.AbDatum > now() " +
//                " ORDER BY Name, bwkennung, M.Bezeichnung ";
//
//
//        try {
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            stmt.setDouble(1, percent / 100);
//            ResultSet rs = stmt.executeQuery();
//            //DateFormat df = DateFormat.getDateInstance();
//            if (rs.first()) {
//                html.append("<h" + headertiefe + ">");
//                html.append("Vorräte ohne Anbruch / mit einem Rest-Bestand unter " + percent + "%");
//                html.append("</h" + headertiefe + ">");
//
//                html.append(SYSTools.rs2html(rs, false));
//
//            }
//            pb.setIndeterminate(false);
//        } catch (SQLException sQLException) {
//            // new DlgException(sQLException);
//        }
//        return html.toString();
//    }
//
//    /**
//     * Ermittelt eine Liste aller Medikamente die nicht länger reichen als in reichweite festgelegt.
//     *
//     * @param headertiefe
//     * @param reichweite  in Tagen
//     * @param o
//     * @return
//     */
//    public static String getBestellliste(int headertiefe, int reichweite, Object[] o) {
//        StringBuilder html = new StringBuilder(1000);
//        JProgressBar pb = null;
//        JLabel lbl = null;
//        if (o != null) {
//            pb = (JProgressBar) o[0];
//            lbl = (JLabel) o[1];
//            pb.setIndeterminate(true);
//            lbl.setText("Medikamenten Bestell-Liste");
//        }
//        String sql = "" +
//                " SELECT DISTINCT CONCAT(bw.nachname,', ',bw.vorname) Name, v.BWKennung, M.Bezeichnung Praeparat, D.Zusatz, F.Zubereitung, " +
//                " CASE F.PackEinheit WHEN 1 THEN 'Stück' WHEN 2 THEN 'ml' WHEN 3 THEN 'l' WHEN 4 THEN 'mg' WHEN 5 THEN 'g' WHEN 6 THEN 'cm' WHEN 7 THEN 'm' ELSE '!FEHLER!' END Packungseinheit, " +
//                " F.AnwText Anwendungstext, " +
//                " CASE F.AnwEinheit WHEN 1 THEN 'Stück' WHEN 2 THEN 'ml' WHEN 3 THEN 'l' WHEN 4 THEN 'mg' WHEN 5 THEN 'g' WHEN 6 THEN 'cm' WHEN 7 THEN 'm' ELSE '!FEHLER!' END Anwendungseinheit, " +
//                " ifnull(vor.saldo,0) Bestand, (bedarf.tw / vor.APV) * ? bdf, (ifnull(vor.saldo,0) / bedarf.tw) reichweite, " +
//                " ifnull(vor.BestID, '<b>OHNE ANBRUCH</b>') anbruchnr " +
//                " FROM BHPVerordnung v " +
//                " INNER JOIN Bewohner bw ON bw.BWKennung = v.BWKennung  " +
//                " LEFT OUTER JOIN MPDarreichung D ON v.DafID = D.DafID  " +
//                " LEFT OUTER JOIN MProdukte M ON M.MedPID = D.MedPID " +
//                " LEFT OUTER JOIN MPFormen F ON D.FormID = F.FormID " +
//                " LEFT OUTER JOIN Situationen S ON v.SitID = S.SitID " +
//                " LEFT OUTER JOIN ( " +
//                "       SELECT DISTINCT a.VorID, vrr.BWKennung, b.DafID, a.saldo, c.saldo anfang, c.BestID, a.APV FROM ( " +
//                "               SELECT best.VorID, best.APV, best.DafID, sum(buch.Menge) saldo FROM MPBestand best " +
//                "               INNER JOIN MPBuchung buch ON buch.BestID = best.BestID  " +
//                "               GROUP BY VorID " +
//                "       ) a INNER JOIN ( " +
//                "               SELECT best.VorID, best.DafID FROM MPBestand best " +
//                "       ) b ON a.VorID = b.VorID " +
//                " 	INNER JOIN ( " +
//                "              SELECT best.VorID, best.DafID, best.BestID, sum(buch.Menge) saldo FROM MPBestand best " +
//                "              INNER JOIN MPBuchung buch ON buch.BestID = best.BestID  " +
//                "  	       WHERE best.Aus = '9999-12-31 23:59:59' AND buch.Status = 1 " +
//                "              GROUP BY VorID " +
//                "       ) c ON c.VorID = b.VorID " +
//                "       INNER JOIN MPVorrat vrr ON a.VorID = vrr.VorID " +
//                "      WHERE vrr.Bis = '9999-12-31 23:59:59' " +
//                "	) vor ON vor.BWKennung = v.BWKennung AND vor.DafID = v.DafID " +
//                " INNER JOIN (" +
//                "	SELECT ver.VerID, (ifnull(sums.sumdosis, 0) + ifnull(sums.TWTaeg, 0) + ifnull(sums.TWWoech, 0) + ifnull(sums.TWMonat, 0) + ifnull(sums.TWBedarf, 0)) tw" +
//                "	FROM BHPVerordnung ver " +
//                "	INNER JOIN ( " +
//                " 		SELECT VerID, " +
//                "		SUM(NachtMo + Morgens + Mittags + Nachmittags + Abends + NachtAb + UhrzeitDosis) sumdosis, " +
//                "	 	SUM((NachtMo + Morgens + Mittags + Nachmittags + Abends + NachtAb + UhrzeitDosis)/ Taeglich) TWTaeg, " +
//                "		SUM((NachtMo + Morgens + Mittags + Nachmittags + Abends + NachtAb + UhrzeitDosis)/ (Woechentlich * 7)) TWWoech, " +
//                "		SUM((NachtMo + Morgens + Mittags + Nachmittags + Abends + NachtAb + UhrzeitDosis)/ (Monatlich * 30.4375)) TWMonat, " +
//                "		MaxAnzahl * MaxEDosis TWBedarf " +
//                "		FROM BHPPlanung plan " +
//                "		GROUP BY VerID " +
//                "	) sums ON sums.VerID = ver.VerID " +
//                "	WHERE ver.AnDatum <= now() AND ver.AbDatum > now() AND ver.DafID > 0 " +
//                ") bedarf ON bedarf.VerID = v.VerID " +
//                " WHERE bw.AdminOnly <> 2 AND v.DafID > 0 AND v.AnDatum <= now() AND v.AbDatum > now() AND (ifnull(vor.saldo,0) / bedarf.tw) < ? " +
//                " ORDER BY Name, bwkennung, M.Bezeichnung ";
//
//
//        try {
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            stmt.setInt(1, reichweite);
//            stmt.setInt(2, reichweite);
//            ResultSet rs = stmt.executeQuery();
//            //DateFormat df = DateFormat.getDateInstance();
//            if (rs.first()) {
//                html.append("<h" + headertiefe + ">");
//                html.append("Vorräte mit einer Reichweite von geschätzt unter " + reichweite + " Tagen");
//                html.append("</h" + headertiefe + ">");
//
//                html.append(SYSTools.rs2html(rs, false));
//
//            }
//            pb.setIndeterminate(false);
//        } catch (SQLException sQLException) {
//            // new DlgException(sQLException);
//        }
//        return html.toString();
//    }
//

//

//
//    public static String getInkontinenz(int headertiefe) {
//        StringBuilder html = new StringBuilder(1000);
//        HashMap bwkennung = new HashMap();
//        boolean[] inko = {false, false};
//
//        String sql = "" +
//                " SELECT b.BWKennung, CONCAT(b.Nachname, ', ', b.Vorname, ' [', b.BWKennung, ']') name, BI2.XML FROM Bewohner B " +
//                " INNER JOIN ResInfo BI1 ON  B.BWKennung = BI1.BWKennung " +
//                " INNER JOIN ResInfo BI2 ON  B.BWKennung = BI2.BWKennung " +
//                " WHERE  " +
//                " BI1.BWINFTYP = 'hauf' AND " +
//                " BI1.von <= NOW() AND BI1.bis >= NOW() AND " +
//                " B.AdminOnly <> 2  " +
//                " AND  " +
//                " ( BI2.BWINFTYP LIKE 'INKO%' OR BI2.BWINFTYP LIKE 'HINKO%' )" +
//                " AND " +
//                "   (  " +
//                "       BI2.XML LIKE '%<inko.harn value=\"ja\"/>%' " +
//                "       OR " +
//                "       BI2.XML LIKE '%<inko.stuhl value=\"ja\"/>%' " +
//                "   )" +
//                " AND BI2.VON < NOW() AND BI2.BIS > NOW() " +
//                " ORDER BY name ";
//
//        try {
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            ResultSet rs = stmt.executeQuery();
//            html.append("<h" + headertiefe + ">");
//            html.append("Liste der Bewohner mit Inkontinenz");
//            html.append("</h" + headertiefe + ">");
//
//            if (rs.first()) {
//                html.append("<table border=\"1\"><tr>" +
//                        "<th>BewohnerIn</th><th>Stuhl</th><th>Harn</th></tr>");
//                rs.beforeFirst();
//                while (rs.next()) {
//                    String name = rs.getString("name");
//                    String xml = rs.getString("XML");
//                    boolean harn = xml.indexOf("<inko.harn value=\"ja\"/>") >= 0;
//                    boolean stuhl = xml.indexOf("<inko.stuhl value=\"ja\"/>") >= 0;
//                    if (bwkennung.containsKey(rs.getString("b.BWKennung"))) {
//                        harn |= (Boolean) ((Object[]) bwkennung.get(rs.getString("b.BWKennung")))[1];
//                        stuhl |= (Boolean) ((Object[]) bwkennung.get(rs.getString("b.BWKennung")))[2];
//                    }
//                    bwkennung.put(rs.getString("b.BWKennung"), new Object[]{name, harn, stuhl});
//                }
//
//                SortedSet<String> sortedset = new TreeSet<String>(bwkennung.keySet());
//
//                Iterator it = sortedset.iterator();
//                while (it.hasNext()) {
//                    Object[] o = (Object[]) bwkennung.get(it.next());
//                    html.append("<tr>");
//                    html.append("<td>" + o[0].toString() + "</td>");
//                    html.append("<td>" + ((Boolean) o[2] ? "Ja" : "Nein") + "</td>");
//                    html.append("<td>" + ((Boolean) o[1] ? "Ja" : "Nein") + "</td>");
//                    html.append("</tr>");
//                }
//
//                html.append("</table>");
//            } else {
//                html.append("<br/>keine Einträge gefunden...");
//            }
//        } catch (SQLException sQLException) {
//            // new DlgException(sQLException);
//        }
//        return html.toString();
//    }
//


//
//    /**
//     * Erstellt ein HTML Dokument mit dem folgenden Inhalt:
//     * <ul>
//     * <li>Aufstellung über Häufigkeit der Beschwerden auf Monate verteilt</li>
//     * <li>Aufstellung über Häufigkeit der Beschwerden auf Staff verteilt</li>
//     * <li>Aufstellung über Häufigkeit der Beschwerden auf Bewohner verteilt</li>
//     * <li>Aufstellung über die Zeit zwischen öffnen und schließen in Tagen</li>
//     * <li>Auflistung aller Beschwerden in einem bestimmten Zeitraum</li>
//     * </ul>
//     *
//     * @param headertiefe
//     * @param monate
//     * @return
//     */
//    public static String getBeschwerdeAuswertung(int headertiefe, int monate) {
//        StringBuilder html = new StringBuilder(1000);
//        String sql = "SET lc_time_names = 'de_DE'";
//        String sql1 = "" +
//                " SELECT COUNT(*) Anzahl, Monthname(Von) Monat, YEAR(Von) Jahr FROM Vorgaenge v " +
//                " INNER JOIN Bewohner b ON b.BWKennung = v.BWKennung " +
//                " WHERE VKatID = 2 AND b.AdminOnly <> 2 " +
//                " AND DATE(Von) >= DATE_ADD(now(), INTERVAL ? MONTH) " +
//                " GROUP BY YEAR(Von), MONTH(Von) " +
//                " ORDER BY YEAR(Von), MONTH(Von) ";
//
//        String sql2 = "" +
//                " SELECT COUNT(*) Anzahl, CONCAT(o.Nachname, ', ', o.Vorname) MitarbeiterIn FROM Vorgaenge v " +
//                " INNER JOIN Bewohner b ON b.BWKennung = v.BWKennung " +
//                " INNER JOIN OCUsers o ON o.Ukennung = v.Ersteller " +
//                " WHERE v.VKatID = 2 AND v.BWKennung <> '' AND b.AdminOnly <> 2 " +
//                " AND DATE(v.Von) >= DATE_ADD(now(), INTERVAL ? MONTH) " +
//                " GROUP BY v.Ersteller " +
//                " ORDER BY Anzahl DESC, MitarbeiterIn ";
//
//        String sql3 = "" +
//                " SELECT COUNT(*) Anzahl, CONCAT(b.Nachname, ', ', b.Vorname) BewohnerIn FROM Vorgaenge v " +
//                " INNER JOIN Bewohner b ON b.BWKennung = v.BWKennung " +
//                " WHERE v.VKatID = 2 AND v.BWKennung <> '' AND b.AdminOnly <> 2 " +
//                " AND DATE(v.Von) >= DATE_ADD(now(), INTERVAL ? MONTH)  " +
//                " GROUP BY v.BWKennung " +
//                " ORDER BY Anzahl DESC, BewohnerIn ";
//
//        String sql4 = "" +
//                " SELECT v.Titel, DATEDIFF(Bis,Von) Tage, CONCAT(b.Nachname, ', ', b.Vorname) Bewohner FROM Vorgaenge v " +
//                " INNER JOIN Bewohner b ON b.BWKennung = v.BWKennung " +
//                " WHERE v.VKatID = 2 AND v.BWKennung <> '' AND b.AdminOnly <> 2 AND Bis < '9999-12-31 23:59:59' " +
//                " AND DATE(v.Von) >= DATE_ADD(now(), INTERVAL ? MONTH)  " +
//                " ORDER BY Tage DESC";
//
//        String sql5 = "" +
//                " SELECT v.Titel, DATE_FORMAT(Von, '%d.%m.%Y') Von, DATE_FORMAT(Bis, '%d.%m.%Y') Bis, CONCAT(b.Nachname, ', ', b.Vorname) BewohnerIn FROM Vorgaenge v " +
//                " INNER JOIN Bewohner b ON b.BWKennung = v.BWKennung " +
//                " WHERE v.VKatID = 2 AND v.BWKennung <> '' AND b.AdminOnly <> 2 " +
//                " AND DATE(v.Von) >= DATE_ADD(now(), INTERVAL ? MONTH)  " +
//                " ORDER BY Von DESC";
//
//        try {
//            // Zur Sprachumstellung
//            PreparedStatement stmt0 = OPDE.getDb().db.prepareStatement(sql);
//            stmt0.execute();
//            PreparedStatement stmt1 = OPDE.getDb().db.prepareStatement(sql1);
//            stmt1.setInt(1, monate * -1);
//            ResultSet rs1 = stmt1.executeQuery();
//            PreparedStatement stmt2 = OPDE.getDb().db.prepareStatement(sql2);
//            stmt2.setInt(1, monate * -1);
//            ResultSet rs2 = stmt2.executeQuery();
//            PreparedStatement stmt3 = OPDE.getDb().db.prepareStatement(sql3);
//            stmt3.setInt(1, monate * -1);
//            ResultSet rs3 = stmt3.executeQuery();
//            PreparedStatement stmt4 = OPDE.getDb().db.prepareStatement(sql4);
//            stmt4.setInt(1, monate * -1);
//            ResultSet rs4 = stmt4.executeQuery();
//            PreparedStatement stmt5 = OPDE.getDb().db.prepareStatement(sql5);
//            stmt5.setInt(1, monate * -1);
//            ResultSet rs5 = stmt5.executeQuery();
//
//            if (rs1.first()) { // wenn rs1 nicht leer ist, dann sind es die anderen auch nicht.
//                html.append("<h" + headertiefe + ">");
//                html.append("Auswertungen über Beschwerden der letzten " + monate + " Monate");
//                html.append("</h" + headertiefe + ">");
//
//                html.append("<h" + (headertiefe + 1) + ">");
//                html.append("Häufigkeit nach Monaten");
//                html.append("</h" + (headertiefe + 1) + ">");
//                html.append(SYSTools.rs2html(rs1, true));
//
//                html.append("<h" + (headertiefe + 1) + ">");
//                html.append("Häufigkeit nach Mitarbeitern");
//                html.append("</h" + (headertiefe + 1) + ">");
//                html.append(SYSTools.rs2html(rs2, true));
//
//                html.append("<h" + (headertiefe + 1) + ">");
//                html.append("Häufigkeit nach BewohnerInnen");
//                html.append("</h" + (headertiefe + 1) + ">");
//                html.append(SYSTools.rs2html(rs3, true));
//
//                html.append("<h" + (headertiefe + 1) + ">");
//                html.append("Auswertung Bearbeitungszeit");
//                html.append("</h" + (headertiefe + 1) + ">");
//                html.append(SYSTools.rs2html(rs4, true));
//
//                html.append("<h" + (headertiefe + 1) + ">");
//                html.append("Gesamtaufstellung");
//                html.append("</h" + (headertiefe + 1) + ">");
//                html.append(SYSTools.rs2html(rs5, true));
//            }
//
//        } catch (SQLException sQLException) {
//            // new DlgException(sQLException);
//        }
//        return html.toString();
//    }
}
