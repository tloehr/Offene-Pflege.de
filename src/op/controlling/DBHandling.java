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
package op.controlling;

import entity.*;
import op.OPDE;
import op.care.vital.DlgVital;
import op.share.bwinfo.BWInfo;
import op.tools.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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





    public static String getAnonymSturz(int headertiefe, int monate) {
        StringBuilder html = new StringBuilder(1000);
        String sql = "" +
                " SELECT count(*) Anzahl, bs.XML, bwi.Von FROM BWInfo bwi " +
                " INNER JOIN Bewohner bw ON bw.BWKennung = bwi.BWKennung " +
                " LEFT OUTER JOIN BWInfo bs ON bwi.BWKennung = bs.BWKennung  " +
                " WHERE bwi.BWINFTYP LIKE 'STURZPROT%' AND bs.BWINFTYP = 'STATION' AND " +
                "   bs.von <= bwi.von AND bs.bis >= bwi.bis AND " +
                "   bw.AdminOnly <> 2 AND bwi.Von >= ? " +
                " GROUP BY bs.XML, CONCAT(YEAR(bwi.Von),'-',MONTH(bwi.Von)) " +
                " ORDER BY bwi.Von, bs.XML ";
        long von = SYSCalendar.bom(SYSCalendar.addField(new Date(), monate * -1, GregorianCalendar.MONTH)).getTime();

        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setTimestamp(1, new Timestamp(von));
            ResultSet rs = stmt.executeQuery();
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
            if (rs.first()) {
                html.append("<h" + headertiefe + ">");
                html.append("Analyse der Sturzprotkolle (anonym)");
                html.append("</h" + headertiefe + ">");
                html.append("<h" + (headertiefe + 1) + ">");
                html.append("Zeitraum: " + monate + " Monate");
                html.append("</h" + (headertiefe + 1) + ">");
                html.append("<table border=\"1\"><tr>" +
                        "<th>Monat</th><th>Station</th><th>Anzahl Stürze</th></tr>");
                rs.beforeFirst();
                while (rs.next()) {
                    html.append("<tr>");
                    int anzahl = rs.getInt("Anzahl");
                    String monat = sdf.format(rs.getDate("bwi.Von"));
                    String xml = rs.getString("xml");
                    xml = xml.substring(16, xml.length() - 3);
                    html.append("<td>" + monat + "</td>");
                    html.append("<td>" + xml.replaceAll("\\<.*?\\>", "") + "</td>");
                    html.append("<td>" + anzahl + "</td>");
                    html.append("</tr>");
                }
                html.append("</table>");
            }

        } catch (SQLException sQLException) {
            new DlgException(sQLException);
        }
        return html.toString();
    }

    public static String getBWSturz(int headertiefe, int monate, Object[] o) {
        StringBuilder html = new StringBuilder(1000);
        int progress = 0;
        boolean isCancelled = false;
        JProgressBar pb = null;
        JLabel lbl = null;
        if (o != null) {
            pb = (JProgressBar) o[0];
            lbl = (JLabel) o[1];
            isCancelled = (Boolean) o[2];
        }
        String sql = "" +
                " SELECT bs.XML, bwi.Von, bwi.BWKennung, bwi.BWINFOID " +
                " FROM BWInfo bwi " +
                " INNER JOIN Bewohner bw ON bw.BWKennung = bwi.BWKennung " +
                " LEFT OUTER JOIN BWInfo bs ON bwi.BWKennung = bs.BWKennung  " +
                " WHERE bwi.BWINFTYP LIKE 'STURZPROT%' AND bs.BWINFTYP = 'STATION' AND " +
                " bs.von <= bwi.von AND bs.bis >= bwi.bis AND " +
                " bw.AdminOnly <> 2 " +
                " AND bwi.Von >= ? " +
                " ORDER BY bs.XML, bw.Nachname, bw.Vorname, bw.BWKennung, bwi.Von ";

        long von = SYSCalendar.bom(SYSCalendar.addField(new Date(), monate * -1, GregorianCalendar.MONTH)).getTime();

        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setTimestamp(1, new Timestamp(von));
            ResultSet rs = stmt.executeQuery();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            String bwkennung = "";
            int stuerze = 0;
            if (rs.last()) {
                pb.setMaximum(rs.getRow() - 1);
                pb.setMinimum(1);
            }

            if (rs.first()) {
                html.append("<h" + headertiefe + ">");
                html.append("Zusammenstellung der Sturzprotkolle");
                html.append("</h" + headertiefe + ">");
                html.append("<h" + (headertiefe + 1) + ">");
                html.append("Zeitraum: " + monate + " Monate");
                html.append("</h" + (headertiefe + 1) + ">");

                rs.beforeFirst();
                isCancelled = (Boolean) o[2];
                while (!isCancelled && rs.next()) {
                    if (pb != null) {
                        progress++;
                        pb.setValue(progress);
                    }
                    if (!rs.getString("bwi.BWKennung").equalsIgnoreCase(bwkennung)) {

                        if (!bwkennung.equals("")) { // nicht der erste Durchgang.
                            html.append("</table>");
                            html.append("<h" + (headertiefe + 3) + ">Anzahl der Stürze im betrachteten Zeitraum: " + stuerze + "</h" + (headertiefe + 3) + ">");
                        }

                        bwkennung = rs.getString("bwi.BWKennung");
                        String bwlabel = SYSTools.getBWLabel(bwkennung);
                        if (lbl != null) {
                            lbl.setText("Sturzstatistik: " + bwlabel);
                        }
                        stuerze = 0;
                        html.append("<h" + (headertiefe + 2) + ">");
                        html.append(bwlabel);
                        html.append("</h" + (headertiefe + 2) + ">");
                        html.append("<table border=\"1\"><tr>" +
                                "<th>Datum</th><th>Protokoll</th></tr>");
                    }

                    html.append("<tr>");
                    String datum = sdf.format(rs.getDate("bwi.Von"));
                    BWInfo bwinfo = new BWInfo(rs.getLong("bwi.BWINFOID"));
                    ArrayList content = bwinfo.getAttribute();
                    HashMap attrib = (HashMap) content.get(0); // Diese BWInfo hat nur eine Zeile
                    //String bemerkung = SYSTools.catchNull(rs.getString("bwi.Bemerkung"), "<i>keine Bemerkung</i>");
                    html.append("<td>" + datum + "</td>");
                    html.append("<td>" + attrib.get("html").toString() + "</td>");
                    html.append("</tr>");
                    stuerze++;
                    isCancelled = (Boolean) o[2];
                }

                if (rs.first()) { // war das Resultset nicht leer ?
                    html.append("</table>");
                    html.append("<h" + (headertiefe + 3) + ">Anzahl der Stürze im betrachteten Zeitraum: " + stuerze + "</h" + (headertiefe + 3) + ">");
                }

            }

        } catch (SQLException sQLException) {
            new DlgException(sQLException);
        }
        return html.toString();
    }

//    public static String getMediKontrolle(String station, int headertiefe) {
//        StringBuilder html = new StringBuilder(1000);
//        String sql = "" +
//                " SELECT bw.nachname, bw.vorname, bw.geschlecht, bw.bwkennung, prod.Bezeichnung, daf.Zusatz, form.Zubereitung, form.AnwText, " +
//                " (CASE form.PackEinheit WHEN 1 THEN 'Stück' WHEN 2 THEN 'ml' WHEN 3 THEN 'l' WHEN 4 THEN 'mg' WHEN 5 THEN 'g' WHEN 6 THEN 'cm' WHEN 7 THEN 'm' ELSE '!FEHLER!' END) Bestandsmenge, " +
//                " best.BestID, best.Anbruch " +
//                " FROM Bewohner bw " +
//                " INNER JOIN BWInfo ba ON bw.BWKennung = ba.BWKennung " +
//                " INNER JOIN MPVorrat vor ON vor.BWKennung = bw.BWKennung " +
//                " INNER JOIN MPBestand best ON vor.VorID = best.VorID " +
//                " INNER JOIN MPDarreichung daf ON best.DafID = daf.DafID " +
//                " INNER JOIN MProdukte prod ON prod.MedPID = daf.MedPID " +
//                " INNER JOIN MPFormen form ON form.FormID = daf.FormID " +
//                " WHERE ba.BWINFTYP = 'station' and ba.von < now() and ba.bis > now() and ba.XML = '<station value=\"" + station + "\"/>' " +
//                " AND best.Anbruch < now() and best.Aus = '9999-12-31 23:59:59' AND adminonly <> 2 " +
//                " ORDER BY bw.Nachname, bw.Vorname, vor.Text, best.Anbruch ";
//
//        try {
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            ResultSet rs = stmt.executeQuery();
//            DateFormat df = DateFormat.getDateInstance();
//            if (rs.first()) {
//                html.append("<h" + headertiefe + ">");
//                html.append("Liste zur Medikamentenkontrolle");
//                html.append("</h" + headertiefe + ">");
//                html.append("<h" + (headertiefe + 1) + ">");
//                html.append("Legende");
//                html.append("</h" + (headertiefe + 1) + ">");
//                html.append("#1 - Medikament abgelaufen<br/>");
//                html.append("#2 - Packung nicht beschriftet<br/>");
//                html.append("#3 - Packung beschädigt<br/>");
//                html.append("#4 - Anbruchsdatum nicht vermerkt<br/>");
//                html.append("#5 - Medikament ist nicht verordnet<br/>");
//                html.append("#6 - Mehr als 1 Blister im Anbruch<br/>");
//                html.append("#7 - Mehr als 1 Tablette geteilt<br/><br/>");
//
//                html.append("<table border=\"1\"><tr>" +
//                        "<th>BewohnerIn</th><th>BestNr</th><th>Präparat</th><th>Anbruch</th><th>#1</th><th>#2</th><th>#3</th><th>#4</th><th>#5</th><th>#6</th><th>#7</th></tr>");
//                rs.beforeFirst();
//                while (rs.next()) {
//                    html.append("<tr>");
//                    String name = SYSTools.anonymizeBW(rs.getString("Nachname"), rs.getString("Vorname"), rs.getString("BWKennung"), rs.getInt("geschlecht"));
//                    //String name = rs.getString("BWName");
//                    String bez = rs.getString("Bezeichnung");
//                    String zusatz = rs.getString("Zusatz");
//                    String zubereitung = rs.getString("Zubereitung");
//                    String anwtext = rs.getString("AnwText");
//                    //String bestmng = rs.getString("Bestandsmenge");
//                    long bestid = rs.getLong("BestID");
//                    Date datum = rs.getDate("Anbruch");
//                    String praep = bez + SYSTools.catchNull(zusatz, " ", "") + SYSTools.catchNull(zubereitung, ", ", "") + SYSTools.catchNull(anwtext, ", ", "");
//                    html.append("<td>" + name + "</td>");
//                    html.append("<td>" + bestid + "</td>");
//                    html.append("<td>" + praep + "</td>");
//                    html.append("<td>" + df.format(datum) + "</td>");
//                    html.append("<td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>");
//                    html.append("</tr>");
//                }
//                html.append("</table>");
//            }
//
//        } catch (SQLException sQLException) {
//            new DlgException(sQLException);
//        }
//        return html.toString();
//    }

//    public static String getSozialBerichte(EntityManager em, String bwkennung, String ukennung, int headertiefe, int sozialwochen) {
//
//        try {
//
////        Query query = em.createNamedQuery("PBerichtTAGS.findByKurzbezeichnung");
////        query.setParameter("kurzbezeichnung", "soz");
////        PBerichtTAGS sozTag = (PBerichtTAGS) query.getSingleResult();
////
////        PflegeberichteTools.getBerichteASHTML(em, "", sozTag, headertiefe,sozialwochen );
//
//        StringBuilder html = new StringBuilder(1000);
//        String sql = "" +
//                " SELECT b.nachname, b.vorname, b.geschlecht, b.bwkennung, tb.Text, Date(tb.PIT) Datum, tb.UKennung, tb.Dauer " +
//                " FROM Bewohner b " +
//                " INNER JOIN BWInfo ba ON b.BWKennung = ba.BWKennung  " +
//                " LEFT OUTER JOIN (" +
//                "       SELECT BWKennung, UKennung, Text, PIT, Dauer FROM Tagesberichte WHERE Sozial > 0 " +
//                "       AND Date(PIT) >= DATE_ADD(now(), INTERVAL ? WEEK) AND Date(PIT) <= Date(now()) " +
//                " ) tb ON tb.BWKennung = b.BWKennung " +
//                " WHERE " +
//                (bwkennung.equals("") ? "" : " BWKennung = ? AND ") +
//                (ukennung.equals("") ? "" : " UKennung = ? AND ") +
//                " ba.BWINFTYP = 'hauf' AND ba.von <= NOW() AND ba.bis >= NOW() AND b.AdminOnly <> 2 " +
//                " ORDER BY b.BWKennung, tb.PIT ";
//
//
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            stmt.setInt(1, sozialwochen * -1);
//            if (!bwkennung.equals("")) {
//                stmt.setString(2, bwkennung);
//                if (!ukennung.equals("")) {
//                    stmt.setString(3, ukennung);
//                }
//            } else if (!ukennung.equals("")) {
//                stmt.setString(3, ukennung);
//            }
//            ResultSet rs = stmt.executeQuery();
//            DateFormat df = DateFormat.getDateInstance();
//            if (rs.first()) {
//                html.append("<h" + headertiefe + ">");
//                html.append("Aktivitäten des Sozialen Dienstes");
//                html.append("</h" + headertiefe + ">");
//                rs.beforeFirst();
//                String prevBW = "";
//                int summeMinuten = 0;
//                while (rs.next()) {
//                    String name = SYSTools.anonymizeBW(rs.getString("Nachname"), rs.getString("Vorname"), rs.getString("BWKennung"), rs.getInt("geschlecht"));
//                    String bwk = rs.getString("bwkennung");
//                    String text = rs.getString("text");
//                    int min = rs.getInt("Dauer");
//                    String uk = rs.getString("ukennung");
//                    Date datum = rs.getDate("Datum");
//                    if (rs.isFirst() || !prevBW.equalsIgnoreCase(bwk)) {
//                        if (!rs.isFirst()) {
//                            // Zusammenfassung des letzten Durchgangs.
//                            html.append("<tr><td></td><td align=\"right\"><b>Summe Minuten:</b></td><td><b>" + summeMinuten + "</b></td><td></td></tr>");
//                            html.append("</table>");
//                            summeMinuten = 0;
//                        }
//                        html.append("<h" + (headertiefe + 1) + ">");
//                        html.append(name + " [" + bwk + "]");
//                        html.append("</h" + (headertiefe + 1) + ">");
//                        html.append("<table border=\"1\"><tr><th>Datum</th><th>Text</th><th>Dauer</th><th>UKennung</th></tr>");
//                        prevBW = bwk;
//                    }
//                    html.append("<tr>");
//                    if (SYSTools.catchNull(uk).equals("")) {
//                        html.append("<td align=\"center\">--</td>");
//                        html.append("<td><b>Keine BV Aktivitäten gefunden.</b></td>");
//                        html.append("<td align=\"center\">--</td>");
//                        html.append("<td align=\"center\">--</td>");
//                    } else {
//                        summeMinuten += min;
//                        html.append("<td>" + df.format(datum) + "</td>");
//                        html.append("<td>" + text + "</td>");
//                        html.append("<td>" + min + "</td>");
//                        html.append("<td>" + uk + "</td>");
//                    }
//                    html.append("</tr>");
//                }
//                html.append("<tr><td></td><td align=\"right\"><b>Summe Minuten:</b></td><td><b>" + summeMinuten + "</b></td><td></td></tr>");
//                html.append("</table>");
//            }
//
//        } catch (SQLException sQLException) {
//            new DlgException(sQLException);
//        }
//        return html.toString();
//    }

    public static String getGewichtsverlauf(int gewichtmonate, int headertiefe, Object[] o) {
        int progress = 0;
        boolean isCancelled = false;
        JProgressBar pb = null;
        JLabel lbl = null;
        if (o != null) {
            pb = (JProgressBar) o[0];
            lbl = (JLabel) o[1];
            isCancelled = (Boolean) o[2];
        }

        long von = SYSCalendar.bom(SYSCalendar.addField(new Date(), gewichtmonate * -1, GregorianCalendar.MONTH)).getTime();
        long bis = SYSCalendar.eom(SYSCalendar.addField(new Date(), -1, GregorianCalendar.MONTH)).getTime();
        java.sql.Date v = new java.sql.Date(von);
        java.sql.Date b = new java.sql.Date(bis);
        String sql = "SELECT " +
                " BW.BWKennung, Date(PIT) Datum, Wert " +
                " FROM BWerte BW " +
                " INNER JOIN Bewohner B ON BW.BWKennung = B.BWKennung " +
                " INNER JOIN BWInfo ba ON B.BWKennung = ba.BWKennung  " +
                " WHERE ba.BWINFTYP = 'hauf' AND ba.von <= ? AND ba.bis >= ? AND B.AdminOnly <> 2 " +
                " AND BW.XML = '<GEWICHT/>' AND Date(PIT) >= ? AND Date(PIT) <= ?" +
                " ORDER BY B.Nachname, B.Vorname, Date(PIT) ";
        String s = "";
        DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT);
        {
            //FileWriter out = null;
            try {
                PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
                stmt.setDate(1, b); // Es interessieren nur die BW, die zum Zeitpunkt der Auswertung auch da waren.
                stmt.setDate(2, b);
                stmt.setDate(3, v);
                stmt.setDate(4, b);
                ResultSet rs = stmt.executeQuery();
                if (rs.first()) {

                    if (pb != null) {
                        rs.last();
                        pb.setMaximum(rs.getRow());
                    }

                    s += "<h" + headertiefe + ">Verlauf Gewicht / BMI</h" + headertiefe + ">";
                    s += "<h" + (headertiefe + 1) + ">Auswertung vom " + df.format(bis) + "</h" + (headertiefe + 1) + ">";
                    //s += "<h1>Verlauf Gewicht / BMI</h1> <h2>Auswertung vom " + df.format(bis) + "</h2>";
                    String prev = "";
                    rs.beforeFirst();
                    double gewichtPlusMinus = 0d;
                    //double gewichtProzent = 0d;
                    double bmiPlusMinus = 0d;
                    //double bmiProzent = 0d;
                    double prevBMI = 0d;
                    double prevGewicht = 0d;
                    double gr = 0d;
                    double startGewicht = 0d;
                    double startBMI = 0d;
                    double gewicht = 0d;
                    double bmi = 0d;
                    isCancelled = (Boolean) o[2];
                    while (!isCancelled && rs.next()) {
                        if (pb != null) {
                            progress++;
                            pb.setValue(progress);
                        }
                        String bwkennung = rs.getString("BWKennung");
                        if (!prev.equalsIgnoreCase(bwkennung)) {
                            prev = bwkennung;
                            if (rs.getRow() > 1) {
                                double bpm = bmi - startBMI;
                                double gpm = gewicht - startGewicht;
                                bpm = Math.round(bpm * 100d) / 100d;
                                gpm = Math.round(gpm * 100d) / 100d;

                                s += "<tr><td>gesamter Zeitraum</td><td></td><td>" + gpm + " kg</td>";
                                s += "<td></td><td>" + bpm + "</td></tr>";
                                s += "</table>";
                            }

                            String bwlabel = SYSTools.getBWLabel(bwkennung);

                            if (lbl != null) {
                                lbl.setText("Gewichtstatistik: " + bwlabel);
                            }
                            s += "<h" + (headertiefe + 2) + ">" + bwlabel + "</h" + (headertiefe + 2) + "> ";

                            // TODO: Das hier muss wieder gefixt werden.
//                            BWerte groesse = BWerteTools.getLetztenBWert(bewohner, BWerteTools.GROESSE);
//                            startGewicht = rs.getDouble("Wert");
//                            if (groesse == null) {
//                                gr = -1d;
//                                s += "<b>Körpergröße wurde bisher nicht eingetragen. Somit kann kein BMI berechnet werden.</b>";
//                                startBMI = -1d;
//                                bmiPlusMinus = -1d;
//                                //bmiProzent = -1d;
//                                prevBMI = -1d;
//                            } else {
////                                gr = ((Double) groesse.get(1)).doubleValue();
//                                s += "Groesse: " + groesse.getWert().toPlainString() + " m";
//                                startBMI = (startGewicht / (gr * gr));
//                                bmiPlusMinus = 0d;
//                                //bmiProzent = 0d;
//                                prevBMI = 0d;
//                            }

                            s += "<table border=\"1\">" + "<tr><th>Datum</th><th>Gewicht</th><th>+-(%)</th><th>BMI</th><th>+-(%)</th></tr>";
                            gewichtPlusMinus = 0d;
                            //gewichtProzent = 0d;
                            prevGewicht = 0d;

                        }
                        gewicht = rs.getDouble("Wert");
                        if (gr > 0) {
                            bmi = gewicht / (gr * gr);
                            bmi = Math.round(bmi * 100d) / 100d;
                            bmiPlusMinus = bmi - prevBMI;
                            bmiPlusMinus = Math.round(bmiPlusMinus * 100d) / 100d;
                        } else {
                            bmi = -1d;
                        }
                        gewichtPlusMinus = gewicht - prevGewicht;
                        gewichtPlusMinus = Math.round(gewichtPlusMinus * 100d) / 100d;

                        if (bmi > 0) {
                            if (prevBMI == 0d) {
                                s += "<tr><td>" + df.format(rs.getDate("Datum")) + "</td><td>" + gewicht + " kg</td><td>--</td>";
                                s += "<td>" + bmi + "</td><td>--</td></tr>";
                            } else {
                                s += "<tr><td>" + df.format(rs.getDate("Datum")) + "</td><td>" + gewicht + " kg</td><td>" + gewichtPlusMinus + " kg</td>";
                                s += "<td>" + bmi + "</td><td>" + bmiPlusMinus + "</td></tr>";
                            }
                            prevGewicht = gewicht;
                            prevBMI = bmi;
                        } else {
                            s += "<tr><td>" + df.format(rs.getDate("Datum")) + "</td><td>" + gewicht + " kg</td><td>" + gewichtPlusMinus + " kg</td>";
                            s += "<td>??</td><td>?? </td></tr>";
                        }
                        isCancelled = (Boolean) o[2];
                    }
                    double bpm = bmi - startBMI;
                    double gpm = gewicht - startGewicht;
                    bpm = Math.round(bpm * 100d) / 100d;
                    gpm = Math.round(gpm * 100d) / 100d;

                    s += "<tr><td>gesamter Zeitraum</td><td></td><td>" + gpm + " kg</td>";
                    s += "<td></td><td>" + bpm + "</td></tr>";
                    s += "</table>";
                }

                rs.close();
                stmt.close();
            } catch (SQLException ex) {
                new DlgException(ex);
            }
        }

        isCancelled = (Boolean) o[2];
        if (isCancelled) {
            s = "";
        }

        return s;

    }

//    public static String getSozialZeiten(int headertiefe, Date monat) {
//        StringBuilder html = new StringBuilder(1000);
//        SimpleDateFormat df = new SimpleDateFormat("MMMM yyyy");
//
//        String sql = "" +
//                " SELECT s.Name, s.BWKennung, " +
//                "               s.dauer 'Dauer (Minuten)', ROUND(s.dauer/60, 2) 'Dauer (Stunden)', ROUND(s.dauer/60/?,2) '(Stunden-Schnitt pro Tag)', " +
//                "               p.dauer 'PEA (Minuten)', ROUND(p.dauer/60,2) 'PEA (Stunden)', ROUND(p.dauer/60/?,2) '(Stunden-Schnitt pro Tag)' " +
//                " FROM ( " +
//                "   SELECT CONCAT(b.nachname,', ',b.vorname) Name, b.BWKennung, ifnull(SUM(sdauer), 0) dauer " +
//                "   FROM Bewohner b " +
//                "   INNER JOIN BWInfo ba1 ON b.BWKennung = ba1.BWKennung " +
//                "   LEFT OUTER JOIN ( " +
//                "       SELECT BWKennung, Date(PIT) Datum, UKennung, Text, Dauer sdauer FROM Tagesberichte " +
//                "       WHERE Sozial=1 AND Date(PIT) >= DATE(?) AND Date(PIT) <= DATE(?) " +
//                "   ) s ON s.BWKennung = b.BWKennung " +
//                "   WHERE ba1.BWINFTYP = 'hauf' AND ( " +
//                "       (DATE(ba1.von) <= DATE(?) AND DATE(ba1.bis) >= DATE(?)) " +
//                "       OR " +
//                "       (DATE(ba1.von) <= DATE(?) AND DATE(ba1.bis) >= DATE(?))  " +
//                "       OR " +
//                "       (DATE(ba1.von) > DATE(?) AND DATE(ba1.bis) < DATE(?))  " +
//                "   ) " +
//                "   AND b.AdminOnly <> 2 " +
//                "   GROUP BY BWKennung " +
//                " ) s INNER JOIN (" +
//                "   SELECT CONCAT(b.nachname,', ',b.vorname) Name, b.BWKennung, ifnull(SUM(sdauer), 0) dauer " +
//                "   FROM Bewohner b " +
//                "   INNER JOIN BWInfo ba1 ON b.BWKennung = ba1.BWKennung " +
//                "   LEFT OUTER JOIN ( " +
//                "       SELECT BWKennung, Date(PIT) Datum, UKennung, Text, Dauer sdauer FROM Tagesberichte  " +
//                "       WHERE PEA=1 AND Date(PIT) >= DATE(?) AND Date(PIT) <= DATE(?)  " +
//                "   ) s ON s.BWKennung = b.BWKennung  " +
//                "   WHERE ba1.BWINFTYP = 'hauf' AND ( " +
//                "       (DATE(ba1.von) <= DATE(?) AND DATE(ba1.bis) >= DATE(?)) " +
//                "       OR " +
//                "       (DATE(ba1.von) <= DATE(?) AND DATE(ba1.bis) >= DATE(?))  " +
//                "       OR " +
//                "       (DATE(ba1.von) > DATE(?) AND DATE(ba1.bis) < DATE(?))  " +
//                "   ) " +
//                "   AND b.AdminOnly <> 2   " +
//                "   GROUP BY BWKennung   " +
//                " ) p ON s.BWKennung = p.BWKennung" +
//                " order by s.Name ";
//
//        try {
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            java.sql.Date von = new java.sql.Date(SYSCalendar.bom(monat).getTime());
//            java.sql.Date bis = new java.sql.Date(SYSCalendar.eom(monat).getTime());
//            int daysinmonth = SYSCalendar.eom(SYSCalendar.toGC(monat));
//
//            stmt.setInt(1, daysinmonth);
//            stmt.setInt(2, daysinmonth);
//
//            stmt.setDate(3, von);
//            stmt.setDate(4, bis);
//            stmt.setDate(5, von);
//            stmt.setDate(6, von);
//            stmt.setDate(7, bis);
//            stmt.setDate(8, bis);
//            stmt.setDate(9, von);
//            stmt.setDate(10, bis);
//
//            stmt.setDate(11, von);
//            stmt.setDate(12, bis);
//            stmt.setDate(13, von);
//            stmt.setDate(14, von);
//            stmt.setDate(15, bis);
//            stmt.setDate(16, bis);
//            stmt.setDate(17, von);
//            stmt.setDate(18, bis);
//
//            ResultSet rs = stmt.executeQuery();
//            //DateFormat df = DateFormat.getDateInstance();
//            if (rs.first()) {
//                html.append("<h" + headertiefe + ">");
//                html.append("Zeiten des Sozialen Dienstes je BewohnerIn");
//                html.append("</h" + headertiefe + ">");
//
//                headertiefe++;
//
//                html.append("<h" + headertiefe + ">");
//                html.append("Zeitraum: " + df.format(monat));
//                html.append("</h" + headertiefe + ">");
//
//                html.append(SYSTools.rs2html(rs, true));
//
//                html.append("<p><b>PEA:</b> Personen mit erheblich eingeschränkter Alltagskompetenz (gemäß §87b SGB XI)." +
//                        " Der hier errechnete Wert ist der <b>Anteil</b> für die PEA Leistungen, die in den allgemeinen Sozialzeiten" +
//                        " mit enthalten sind.</p>");
//
//            }
//
//        } catch (SQLException sQLException) {
//            new DlgException(sQLException);
//        }
//        return html.toString();
//    }

    public static String getAktiveVorraeteOhneBestandImAnbruch(int headertiefe) {
        StringBuilder html = new StringBuilder(1000);
        String sql = "" +
                " SELECT CONCAT(b.nachname,', ',b.vorname) Name, b.BWKennung, v.VorID, v.Text " +
                " FROM Bewohner b " +
                " INNER JOIN BWInfo ba1 ON b.BWKennung = ba1.BWKennung  " +
                " INNER JOIN MPVorrat v ON v.BWKennung = b.BWKennung " +
                " INNER JOIN MPBestand best ON v.VorID = best.VorID " +
                " INNER JOIN BHPVerordnung ver ON ver.DafID = best.DafID AND ver.BWKennung = b.BWKennung " +
                " WHERE ba1.BWINFTYP = 'hauf' AND ba1.von <= NOW() AND ba1.bis >= NOW() AND b.AdminOnly <> 2 " +
                " AND v.Von <= now() AND v.Bis > now() AND ver.AnDatum < now() AND ver.AbDatum > now() " +
                " AND v.VorID NOT IN ( " +
                " 	SELECT VorID FROM MPBestand " +
                " 	WHERE Anbruch < '9999-12-31 23:59:59' AND Aus = '9999-12-31 23:59:59' " +
                " ) " +
                " ORDER BY Name ";

        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            //DateFormat df = DateFormat.getDateInstance();
            if (rs.first()) {
                html.append("<h" + headertiefe + ">");
                html.append("Aktive Vorräte ohne Bestand im Anbruch");
                html.append("</h" + headertiefe + ">");

                html.append(SYSTools.rs2html(rs, true));

            }

        } catch (SQLException sQLException) {
            new DlgException(sQLException);
        }
        return html.toString();
    }

    public static String getNichtAbgehakteBHPs(int headertiefe, int days) {
        StringBuilder html = new StringBuilder(1000);
        String sql = "" +
                " SELECT CONCAT(b.nachname,', ',b.vorname) Name, b.BWKennung, bhp.Soll, ifnull(mp.Bezeichnung, '--') Medikament, ifnull(daf.Zusatz, '--') Zusatz, m.Bezeichnung " +
                " FROM BHP bhp " +
                " INNER JOIN BHPPlanung bhpp ON bhp.BHPPID = bhpp.BHPPID " +
                " INNER JOIN BHPVerordnung v ON bhpp.VerID = v.VerID " +
                " INNER JOIN Bewohner b ON b.BWKennung = v.BWKennung " +
                " LEFT OUTER JOIN MPDarreichung daf ON daf.DafID = v.DafID " +
                " LEFT OUTER JOIN MProdukte mp ON mp.MedPID = daf.MedPID " +
                " INNER JOIN Massnahmen m ON m.MassID = v.MassID " +
                " WHERE status = 0 " +
                " AND Date(Soll) >= DATE_ADD(now(), INTERVAL ? DAY) AND Date(Soll) < DATE(now()) AND b.AdminOnly <> 2 " +
                " ORDER BY name, Soll ";

        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setInt(1, days * -1);
            ResultSet rs = stmt.executeQuery();
            //DateFormat df = DateFormat.getDateInstance();
            if (rs.first()) {
                html.append("<h" + headertiefe + ">");
                html.append("Nicht abgehakte BHPs");
                html.append("</h" + headertiefe + ">");

                html.append(SYSTools.rs2html(rs, false));

            }

        } catch (SQLException sQLException) {
            new DlgException(sQLException);
        }
        return html.toString();
    }

    public static String getAblaufendePlanungen(int headertiefe, int days) {
        StringBuilder html = new StringBuilder(1000);
        String sql = "" +
                " SELECT CONCAT(b.nachname,', ',b.vorname) Name, b.BWKennung, p.Stichwort, p.NKontrolle Kontrolldatum " +
                " FROM Planung p " +
                " INNER JOIN Bewohner b ON b.BWKennung = p.BWKennung " +
                " WHERE Bis = '9999-12-31 23:59:59' AND NKontrolle < DATE_ADD(now(), INTERVAL ? DAY) AND b.AdminOnly <> 2 " +
                " ORDER BY Name, NKontrolle ";

        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setInt(1, days);
            ResultSet rs = stmt.executeQuery();
            //DateFormat df = DateFormat.getDateInstance();
            if (rs.first()) {
                html.append("<h" + headertiefe + ">");
                html.append("Planungen, die bald enden / abgelaufen sind");
                html.append("</h" + headertiefe + ">");

                html.append(SYSTools.rs2html(rs, false));

            }

        } catch (SQLException sQLException) {
            new DlgException(sQLException);
        }
        return html.toString();
    }

    public static String getBilanzen(int headertiefe, Date monat, Object[] o) {
        Date vormonat = SYSCalendar.addField(monat, -1, GregorianCalendar.MONTH);
        SimpleDateFormat df = new SimpleDateFormat("MMMM yyyy");
        int progress = 0;
        boolean isCancelled = false;
        JProgressBar pb = null;
        JLabel lbl = null;
        if (o != null) {
            pb = (JProgressBar) o[0];
            lbl = (JLabel) o[1];
            isCancelled = (Boolean) o[2];
            pb.setIndeterminate(true);
            lbl.setText("Ein-/Ausfuhr/Bilanz: Datenbankzugriff");
        }
        double avgEin = 0d;
        double avgAus = 0d;
        double sumBilanz = 0d;
        String prev = "";
        StringBuilder html = new StringBuilder(1000);
        String sqlVormonat = "" +
                " SELECT avg(ein.EINFUHR) Einfuhr, ifnull(avg(aus.AUSFUHR), 0) Ausfuhr, ifnull(sum(ein.EINFUHR)+sum(aus.AUSFUHR), 0) BILANZ FROM " +
                " (" +
                "   SELECT Pit, Date(PIT), bw.BWKennung, SUM(Wert) AUSFUHR FROM BWerte bw" +
                "   WHERE ReplacedBy = 0 AND Wert < 0 AND bw.BWKennung = ? AND XML='<BILANZ/>' AND Date(PIT) >= DATE(?) AND Date(PIT) <= DATE(?)" +
                "   GROUP BY bw.BWKennung, Date(PIT) " +
                " ) aus " +
                " RIGHT OUTER JOIN " +
                " (" +
                "   SELECT Pit, Date(PIT), bw.BWKennung, SUM(Wert) EINFUHR FROM BWerte bw " +
                "   WHERE ReplacedBy = 0 AND Wert > 0 AND bw.BWKennung = ? AND XML='<BILANZ/>' AND Date(PIT) >= DATE(?) AND Date(PIT) <= DATE(?) " +
                "   GROUP BY bw.BWKennung, Date(PIT) " +
                " ) ein " +
                " ON aus.BWKennung = ein.BWKennung AND Date(aus.PIT) = Date(ein.PIT) " +
                " INNER JOIN Bewohner b ON ein.BWKennung = b.BWKennung " +
                " GROUP BY b.BWKennung ";
        String sql = "" +
                " SELECT ein.BWKennung, DATE_FORMAT(ein.PIT, '%d.%c.%Y') Datum, ein.Einfuhr, ifnull(aus.AUSFUHR, 0) Ausfuhr, ifnull((ein.EINFUHR+aus.AUSFUHR), 0) BILANZ FROM " +
                " ( " +
                "   SELECT PIT, bw.BWKennung, SUM(Wert) AUSFUHR FROM BWerte bw " +
                "   INNER JOIN Bewohner b ON b.BWKennung = bw.BWKennung " +
                "   WHERE ReplacedBy = 0 AND Wert < 0 AND AdminOnly <> 2 AND XML='<BILANZ/>' AND Date(PIT) >= DATE(?) AND Date(PIT) <= DATE(?) " +
                "   GROUP BY bw.BWKennung, Date(PIT) " +
                " ) aus " +
                " RIGHT OUTER JOIN " +
                " (" +
                "   SELECT PIT, bw.BWKennung, SUM(Wert) EINFUHR FROM BWerte bw " +
                "   INNER JOIN Bewohner b ON b.BWKennung = bw.BWKennung " +
                "   WHERE ReplacedBy = 0 AND Wert > 0 AND AdminOnly <> 2 AND XML='<BILANZ/>' AND Date(PIT) >= DATE(?) AND Date(PIT) <= DATE(?) " +
                "   GROUP BY bw.BWKennung, Date(PIT) " +
                " ) ein " +
                " ON aus.BWKennung = ein.BWKennung AND Date(aus.PIT) = Date(ein.PIT)" +
                " INNER JOIN Bewohner b ON ein.BWKennung = b.BWKennung " +
                " ORDER BY ein.BWKennung, Date(ein.PIT) ";

        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            PreparedStatement stmtVormonat = OPDE.getDb().db.prepareStatement(sqlVormonat);
            stmt.setDate(1, new java.sql.Date(SYSCalendar.bom(monat).getTime()));
            stmt.setDate(2, new java.sql.Date(SYSCalendar.eom(monat).getTime()));
            stmt.setDate(3, new java.sql.Date(SYSCalendar.bom(monat).getTime()));
            stmt.setDate(4, new java.sql.Date(SYSCalendar.eom(monat).getTime()));

            // BWKennung wird in der Schleife gesetzt.
            stmtVormonat.setDate(2, new java.sql.Date(SYSCalendar.bom(vormonat).getTime()));
            stmtVormonat.setDate(3, new java.sql.Date(SYSCalendar.eom(vormonat).getTime()));
            stmtVormonat.setDate(5, new java.sql.Date(SYSCalendar.bom(vormonat).getTime()));
            stmtVormonat.setDate(6, new java.sql.Date(SYSCalendar.eom(vormonat).getTime()));

            ResultSet rs = stmt.executeQuery();

            if (rs.first()) {

                String bwkennung = "";

                if (pb != null) {
                    rs.last();
                    pb.setMaximum(rs.getRow());
                    pb.setIndeterminate(false);
                }

                rs.beforeFirst();

                html.append("<h" + headertiefe + ">");
                html.append("Ein-, Ausfuhr / Bilanzen");
                html.append("</h" + headertiefe + ">");

                ResultSetMetaData md = rs.getMetaData();
                int count = md.getColumnCount();

                int rows = 0;
                isCancelled = (Boolean) o[2];
                int zieltrink = 0;
                boolean bilanz = false;
                while (!isCancelled && rs.next()) {
                    if (pb != null) {
                        progress++;
                        pb.setValue(progress);
                    }
                    rows++;
                    bwkennung = rs.getString("BWKennung");
                    if (!prev.equalsIgnoreCase(bwkennung)) {
                        if (rs.getRow() > 1) {
                            avgEin = avgEin / rows;
                            avgAus = avgAus / rows;

                            avgEin = Math.round(avgEin * 100d) / 100d;
                            avgAus = Math.round(avgAus * 100d) / 100d;
                            //String.format(prev, arg1)


                            html.append("<tr><td></td><td>" + df.format(monat) + "</td><td>&Oslash; " + avgEin + "</td><td>&Oslash; " + avgAus + "</td><td>&Sigma; " + sumBilanz + "</td></tr>");

                            // Vormonat berechnen
                            stmtVormonat.setString(1, prev);
                            stmtVormonat.setString(4, prev);
                            ResultSet rsVormonat = stmtVormonat.executeQuery();

                            if (rsVormonat.first()) {
                                double avgEinVor = SYSTools.roundScale2(rsVormonat.getDouble("Einfuhr"));
                                double avgAusVor = SYSTools.roundScale2(rsVormonat.getDouble("Ausfuhr"));
                                double sumBilanzVor = SYSTools.roundScale2(rsVormonat.getDouble("Bilanz"));
                                html.append("<tr><td></td><td>" + df.format(vormonat) + "</td><td>&Oslash; " + avgEinVor + "</td><td>&Oslash; " + avgAusVor + "</td><td>&Sigma; " + sumBilanzVor + "</td></tr>");
                            }

                            rsVormonat.close();

                            html.append("</table>");
                            avgEin = 0;
                            avgAus = 0;
                            sumBilanz = 0;
                            rows = 0;
                        }

                        prev = bwkennung;
                        String bwlabel = SYSTools.getBWLabel(bwkennung);

                        zieltrink = 0;
                        BWInfo bwinfo3 = new BWInfo(bwkennung, "ZIELTRINK", SYSCalendar.nowDBDate());
                        if (bwinfo3.getAttribute().size() > 0) {
                            HashMap antwort = (HashMap) ((HashMap) bwinfo3.getAttribute().get(0)).get("antwort");
                            zieltrink = Integer.parseInt(antwort.get("zieltrinkmenge").toString());
                        }
                        bwinfo3.cleanup();

                        bilanz = false;
                        BWInfo bwinfo4 = new BWInfo(bwkennung, "CONTROL", SYSCalendar.nowDBDate());
                        if (bwinfo4.getAttribute().size() > 0) {
                            HashMap antwort = (HashMap) ((HashMap) bwinfo4.getAttribute().get(0)).get("antwort");
                            bilanz = antwort.get("c.bilanz").toString().equalsIgnoreCase("true");
                        }
                        bwinfo4.cleanup();


                        if (lbl != null) {
                            lbl.setText("Ein-/Ausfuhr/Bilanz: " + bwlabel);
                        }

                        html.append("<h" + (headertiefe + 1) + ">");
                        html.append(bwlabel);
                        html.append("</h" + (headertiefe + 1) + ">");
                        if (zieltrink > 0) {
                            html.append("<h" + (headertiefe + 2) + ">Zieltrinkmenge (ZTM): " + zieltrink + " ml in 24h</h" + (headertiefe + 2) + ">");
                        }
                        if (!bilanz) {
                            html.append("<h" + (headertiefe + 2) + ">Keine Bilanzierung vorgesehen</h" + (headertiefe + 2) + ">");
                        }
                        html.append("<table border=1>");
                        html.append("<tr>");
                        for (int i = 1; i <= count; i++) {
                            html.append("<th>");
                            html.append(md.getColumnLabel(i));
                            html.append("</th>");
                        }
                        if (zieltrink > 0) {
                            html.append("<th>Differenz zur ZTM</th>");
                        }
                        html.append("</tr>");

                    }

                    html.append("<tr>");
                    for (int i = 1; i <= count; i++) {
                        html.append("<td>");
                        html.append(rs.getString(i));
                        html.append("</td>");
                    }
                    if (zieltrink > 0) {
                        html.append("<td>" + (rs.getDouble("Einfuhr") - zieltrink) + "</td>");
                    }
                    avgEin += rs.getDouble("Einfuhr");
                    avgAus += rs.getDouble("Ausfuhr");
                    sumBilanz += rs.getDouble("Bilanz");

                    html.append("</tr>");
                    isCancelled = (Boolean) o[2];
                }
                avgEin = avgEin / rows;
                avgAus = avgAus / rows;
                avgEin = Math.round(avgEin * 100d) / 100d;
                avgAus = Math.round(avgAus * 100d) / 100d;
                html.append("<tr><td></td><td></td><td>&Oslash; " + avgEin + "</td><td>&Oslash; " + avgAus + "</td><td>&Sigma; " + sumBilanz + "</td></tr>");
                // Vormonat berechnen
                stmtVormonat.setString(1, bwkennung);
                stmtVormonat.setString(4, bwkennung);
                ResultSet rsVormonat = stmtVormonat.executeQuery();

                if (rsVormonat.first()) {
                    double avgEinVor = SYSTools.roundScale2(rsVormonat.getDouble("Einfuhr"));
                    double avgAusVor = SYSTools.roundScale2(rsVormonat.getDouble("Ausfuhr"));
                    double sumBilanzVor = SYSTools.roundScale2(rsVormonat.getDouble("Bilanz"));
                    html.append("<tr><td></td><td>" + df.format(vormonat) + "</td><td>&Oslash; " + avgEinVor + "</td><td>&Oslash; " + avgAusVor + "</td><td>&Sigma; " + sumBilanzVor + "</td></tr>");
                }

                rsVormonat.close();
                html.append("</table>");
            }

            rs.close();
            stmtVormonat.close();
            stmt.close();

        } catch (SQLException sQLException) {
            new DlgException(sQLException);
        }

        isCancelled = (Boolean) o[2];
        String s = "";
        if (!isCancelled) {
            s = html.toString();
        }
        return s;
    }

    public static String getGeringeVorraete(int headertiefe, double percent, Object[] o) {
        StringBuilder html = new StringBuilder(1000);
        JProgressBar pb = null;
        JLabel lbl = null;
        if (o != null) {
            pb = (JProgressBar) o[0];
            lbl = (JLabel) o[1];
            pb.setIndeterminate(true);
            lbl.setText("Medikamenten Bestandsermittlung");
        }
        String sql = "" +
                " SELECT DISTINCT CONCAT(bw.nachname,', ',bw.vorname) Name, v.BWKennung, M.Bezeichnung Praeparat, " +
                " D.Zusatz, F.Zubereitung, " +
                " CASE F.PackEinheit WHEN 1 THEN 'Stück' WHEN 2 THEN 'ml' WHEN 3 THEN 'l' WHEN 4 THEN 'mg' WHEN 5 THEN 'g' WHEN 6 THEN 'cm' WHEN 7 THEN 'm' ELSE '!FEHLER!' END Packungseinheit, " +
                " F.AnwText Anwendungstext, " +
                " CASE F.AnwEinheit WHEN 1 THEN 'Stück' WHEN 2 THEN 'ml' WHEN 3 THEN 'l' WHEN 4 THEN 'mg' WHEN 5 THEN 'g' WHEN 6 THEN 'cm' WHEN 7 THEN 'm' ELSE '!FEHLER!' END Anwendungseinheit, " +
                " ifnull(vor.saldo,0) Bestand, " +
                " ROUND(if(vor.saldo IS NULL OR vor.saldo <= 0, 0, saldo/ifnull(vor.anfang,0) * 100)) 'Restquote%', " +
                " ifnull(vor.BestID, '<b>OHNE ANBRUCH</b>') anbruchnr " +
                " FROM BHPVerordnung v " +
                " INNER JOIN Bewohner bw ON bw.BWKennung = v.BWKennung  " +
                " LEFT OUTER JOIN MPDarreichung D ON v.DafID = D.DafID " +
                " LEFT OUTER JOIN MProdukte M ON M.MedPID = D.MedPID " +
                " LEFT OUTER JOIN MPFormen F ON D.FormID = F.FormID " +
                " LEFT OUTER JOIN Situationen S ON v.SitID = S.SitID " +
                " LEFT OUTER JOIN ( " +
                "       SELECT DISTINCT a.VorID, vrr.BWKennung, b.DafID, a.saldo, c.saldo anfang, c.BestID FROM ( " +
                "               SELECT best.VorID, best.DafID, sum(buch.Menge) saldo FROM MPBestand best " +
                "               INNER JOIN MPBuchung buch ON buch.BestID = best.BestID  " +
                "               GROUP BY VorID " +
                "       ) a INNER JOIN ( " +
                "               SELECT best.VorID, best.DafID FROM MPBestand best " +
                "       ) b ON a.VorID = b.VorID " +
                " 	INNER JOIN ( " +
                "               SELECT best.VorID, best.DafID, best.BestID, sum(buch.Menge) saldo FROM MPBestand best " +
                "               INNER JOIN MPBuchung buch ON buch.BestID = best.BestID  " +
                " 	        WHERE best.Aus = '9999-12-31 23:59:59' AND buch.Status = 1" +
                "               GROUP BY VorID " +
                "      ) c ON c.VorID = b.VorID " +
                "      INNER JOIN MPVorrat vrr ON a.VorID = vrr.VorID " +
                "      WHERE vrr.Bis = '9999-12-31 23:59:59' " +
                " ) vor ON vor.BWKennung = v.BWKennung AND vor.DafID = v.DafID " +
                " WHERE bw.AdminOnly <> 2 AND (vor.saldo <= vor.anfang*? OR vor.BestID is null) AND v.DafID > 0 AND Date(v.AnDatum) <= now() AND v.AbDatum > now() " +
                " ORDER BY Name, bwkennung, M.Bezeichnung ";


        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setDouble(1, percent / 100);
            ResultSet rs = stmt.executeQuery();
            //DateFormat df = DateFormat.getDateInstance();
            if (rs.first()) {
                html.append("<h" + headertiefe + ">");
                html.append("Vorräte ohne Anbruch / mit einem Rest-Bestand unter " + percent + "%");
                html.append("</h" + headertiefe + ">");

                html.append(SYSTools.rs2html(rs, false));

            }
            pb.setIndeterminate(false);
        } catch (SQLException sQLException) {
            new DlgException(sQLException);
        }
        return html.toString();
    }

    /**
     * Ermittelt eine Liste aller Medikamente die nicht länger reichen als in reichweite festgelegt.
     *
     * @param headertiefe
     * @param reichweite  in Tagen
     * @param o
     * @return
     */
    public static String getBestellliste(int headertiefe, int reichweite, Object[] o) {
        StringBuilder html = new StringBuilder(1000);
        JProgressBar pb = null;
        JLabel lbl = null;
        if (o != null) {
            pb = (JProgressBar) o[0];
            lbl = (JLabel) o[1];
            pb.setIndeterminate(true);
            lbl.setText("Medikamenten Bestell-Liste");
        }
        String sql = "" +
                " SELECT DISTINCT CONCAT(bw.nachname,', ',bw.vorname) Name, v.BWKennung, M.Bezeichnung Praeparat, D.Zusatz, F.Zubereitung, " +
                " CASE F.PackEinheit WHEN 1 THEN 'Stück' WHEN 2 THEN 'ml' WHEN 3 THEN 'l' WHEN 4 THEN 'mg' WHEN 5 THEN 'g' WHEN 6 THEN 'cm' WHEN 7 THEN 'm' ELSE '!FEHLER!' END Packungseinheit, " +
                " F.AnwText Anwendungstext, " +
                " CASE F.AnwEinheit WHEN 1 THEN 'Stück' WHEN 2 THEN 'ml' WHEN 3 THEN 'l' WHEN 4 THEN 'mg' WHEN 5 THEN 'g' WHEN 6 THEN 'cm' WHEN 7 THEN 'm' ELSE '!FEHLER!' END Anwendungseinheit, " +
                " ifnull(vor.saldo,0) Bestand, (bedarf.tw / vor.APV) * ? bdf, (ifnull(vor.saldo,0) / bedarf.tw) reichweite, " +
                " ifnull(vor.BestID, '<b>OHNE ANBRUCH</b>') anbruchnr " +
                " FROM BHPVerordnung v " +
                " INNER JOIN Bewohner bw ON bw.BWKennung = v.BWKennung  " +
                " LEFT OUTER JOIN MPDarreichung D ON v.DafID = D.DafID  " +
                " LEFT OUTER JOIN MProdukte M ON M.MedPID = D.MedPID " +
                " LEFT OUTER JOIN MPFormen F ON D.FormID = F.FormID " +
                " LEFT OUTER JOIN Situationen S ON v.SitID = S.SitID " +
                " LEFT OUTER JOIN ( " +
                "       SELECT DISTINCT a.VorID, vrr.BWKennung, b.DafID, a.saldo, c.saldo anfang, c.BestID, a.APV FROM ( " +
                "               SELECT best.VorID, best.APV, best.DafID, sum(buch.Menge) saldo FROM MPBestand best " +
                "               INNER JOIN MPBuchung buch ON buch.BestID = best.BestID  " +
                "               GROUP BY VorID " +
                "       ) a INNER JOIN ( " +
                "               SELECT best.VorID, best.DafID FROM MPBestand best " +
                "       ) b ON a.VorID = b.VorID " +
                " 	INNER JOIN ( " +
                "              SELECT best.VorID, best.DafID, best.BestID, sum(buch.Menge) saldo FROM MPBestand best " +
                "              INNER JOIN MPBuchung buch ON buch.BestID = best.BestID  " +
                "  	       WHERE best.Aus = '9999-12-31 23:59:59' AND buch.Status = 1 " +
                "              GROUP BY VorID " +
                "       ) c ON c.VorID = b.VorID " +
                "       INNER JOIN MPVorrat vrr ON a.VorID = vrr.VorID " +
                "      WHERE vrr.Bis = '9999-12-31 23:59:59' " +
                "	) vor ON vor.BWKennung = v.BWKennung AND vor.DafID = v.DafID " +
                " INNER JOIN (" +
                "	SELECT ver.VerID, (ifnull(sums.sumdosis, 0) + ifnull(sums.TWTaeg, 0) + ifnull(sums.TWWoech, 0) + ifnull(sums.TWMonat, 0) + ifnull(sums.TWBedarf, 0)) tw" +
                "	FROM BHPVerordnung ver " +
                "	INNER JOIN ( " +
                " 		SELECT VerID, " +
                "		SUM(NachtMo + Morgens + Mittags + Nachmittags + Abends + NachtAb + UhrzeitDosis) sumdosis, " +
                "	 	SUM((NachtMo + Morgens + Mittags + Nachmittags + Abends + NachtAb + UhrzeitDosis)/ Taeglich) TWTaeg, " +
                "		SUM((NachtMo + Morgens + Mittags + Nachmittags + Abends + NachtAb + UhrzeitDosis)/ (Woechentlich * 7)) TWWoech, " +
                "		SUM((NachtMo + Morgens + Mittags + Nachmittags + Abends + NachtAb + UhrzeitDosis)/ (Monatlich * 30.4375)) TWMonat, " +
                "		MaxAnzahl * MaxEDosis TWBedarf " +
                "		FROM BHPPlanung plan " +
                "		GROUP BY VerID " +
                "	) sums ON sums.VerID = ver.VerID " +
                "	WHERE ver.AnDatum <= now() AND ver.AbDatum > now() AND ver.DafID > 0 " +
                ") bedarf ON bedarf.VerID = v.VerID " +
                " WHERE bw.AdminOnly <> 2 AND v.DafID > 0 AND v.AnDatum <= now() AND v.AbDatum > now() AND (ifnull(vor.saldo,0) / bedarf.tw) < ? " +
                " ORDER BY Name, bwkennung, M.Bezeichnung ";


        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setInt(1, reichweite);
            stmt.setInt(2, reichweite);
            ResultSet rs = stmt.executeQuery();
            //DateFormat df = DateFormat.getDateInstance();
            if (rs.first()) {
                html.append("<h" + headertiefe + ">");
                html.append("Vorräte mit einer Reichweite von geschätzt unter " + reichweite + " Tagen");
                html.append("</h" + headertiefe + ">");

                html.append(SYSTools.rs2html(rs, false));

            }
            pb.setIndeterminate(false);
        } catch (SQLException sQLException) {
            new DlgException(sQLException);
        }
        return html.toString();
    }

    public static String getWunden(int headertiefe, int monate, Object[] o) {
        StringBuilder html = new StringBuilder(1000);
        int progress = 0;
        boolean isCancelled = false;
        JProgressBar pb = null;
        JLabel lbl = null;
        String bwkennung = "";
        if (o != null) {
            pb = (JProgressBar) o[0];
            lbl = (JLabel) o[1];
            isCancelled = (Boolean) o[2];
        }
        String sql = "" +
                " SELECT c.pit, c.pk, c.bwkennung, c.tbl, c.UKennung, c.sort FROM ( " +
                "  SELECT pb.PIT pit, pb.PBID pk, pb.bwkennung, 'Pflegeberichte' tbl, pb.UKennung, '' sort" +
                "  FROM Pflegeberichte pb " +
                "  INNER JOIN PB2TAGS pbt ON pb.PBID = pbt.PBID" +
                "  INNER JOIN PBericht_TAGS pt ON pt.PBTAGID = pbt.PBTAGID" +
                "  WHERE ReplacedBy IS NULL AND pb.PIT > ? AND (pt.Kurzbezeichnung='Wun' OR pt.Kurzbezeichnung='BeaWun')" +
                "  UNION " +
                "  SELECT bwi.Von pit, bwi.BWInfoID pk, bwi.bwkennung, 'BWInfo' tbl, bwi.AnUKennung, bwi.BWINFTYP sort FROM BWInfo bwi " +
                "  WHERE bwi.BWINFTYP LIKE 'WUND%' AND bwi.Von >= ? " +
                " ) c " +
                " INNER JOIN Bewohner bw ON bw.BWKennung = c.BWKennung " +
                " WHERE bw.AdminOnly <> 2 AND bw.StatID IS NOT NULL" +
                " ORDER BY c.bwkennung, c.pit, sort; ";

        long von = SYSCalendar.bom(SYSCalendar.addField(new Date(), monate * -1, GregorianCalendar.MONTH)).getTime();

        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setTimestamp(1, new Timestamp(von));
            stmt.setTimestamp(2, new Timestamp(von));
            ResultSet rs = stmt.executeQuery();
            DateFormat df = DateFormat.getDateInstance();
            if (rs.last()) {
                pb.setMaximum(rs.getRow() - 1);
                pb.setMinimum(1);
            }

            if (rs.first()) {
                html.append("<h" + headertiefe + ">");
                html.append("Aufstellung über Wunden");
                html.append("</h" + headertiefe + ">");

                rs.beforeFirst();
                isCancelled = (Boolean) o[2];

                while (!isCancelled && rs.next()) {

                    if (!rs.getString("c.BWKennung").equalsIgnoreCase(bwkennung)) {

                        if (!bwkennung.equals("")) { // nicht der erste Durchgang.
                            html.append("</table>");
                        }

                        bwkennung = rs.getString("c.BWKennung");
                        String bwlabel = SYSTools.getBWLabel(bwkennung);
                        if (lbl != null) {
                            lbl.setText("Wund-Doku: " + bwlabel);
                        }
                        html.append("<h" + (headertiefe + 1) + ">");
                        html.append(bwlabel);
                        html.append("</h" + (headertiefe + 1) + ">");

                        html.append("<table border=\"1\"><tr>" +
                                "<th>Datum</th><th>Wund-Doku/Bericht</th></tr>");
                    }

                    if (pb != null) {
                        progress++;
                        pb.setValue(progress);
                    }

                    html.append("<tr>");
                    String datum = df.format(rs.getTimestamp("c.pit"));

                    html.append("<td>" + datum + " " + rs.getString("c.UKennung") + "</td>");

                    if (rs.getString("c.tbl").equals("Pflegeberichte")) {
                        Pflegeberichte bericht = EntityTools.find(Pflegeberichte.class, rs.getLong("c.pk"));
                        html.append("<td>" + PflegeberichteTools.getBerichtAsHTML(bericht, false) + "</td>");
                    } else {
                        BWInfo bwinfo = new BWInfo(rs.getLong("c.pk"));
                        ArrayList content = bwinfo.getAttribute();
                        HashMap attrib = (HashMap) content.get(0); // Diese BWInfo hat nur eine Zeile
                        html.append("<td>" + "<b>" + attrib.get("bwinfokurz") + "</b><br/>" + attrib.get("html").toString() + "</td>");
                    }
                    html.append("</tr>");
                    isCancelled = (Boolean) o[2];
                }

                html.append("</table>");
            }

        } catch (SQLException sQLException) {
            new DlgException(sQLException);
        }
        return html.toString();
    }

    public static String getGeburtstage(int tage) {
        StringBuilder html = new StringBuilder(1000);
        String sql = "" +
                " SELECT GebDatum, b.nachname, b.vorname, b.Geschlecht, b.BWKennung FROM Bewohner b " +
                " WHERE " +
                " b.AdminOnly <> 2 AND b.StatID IS NOT NULL " +
                " AND " +
                " ( " +
                "   DAYOFYEAR(now()) <= dayofyear(GebDatum) " +
                "   AND DAYOFYEAR(now()) + ? >= dayofyear(GebDatum) " +
                "   OR DAYOFYEAR(now()) <= dayofyear(GebDatum)+365 " +
                "   AND DAYOFYEAR(now()) + ? >= dayofyear(GebDatum)+365 " +
                " )" +
                "ORDER BY nachname, vorname ";

        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setInt(1, tage);
            stmt.setInt(2, tage);
            ResultSet rs = stmt.executeQuery();
            DateFormat df = DateFormat.getDateInstance();

            if (rs.first()) {
                html.append("<h2>");
                html.append("Geburtstage in den nächsten " + tage + " Tagen");
                html.append("</h2>");

                rs.beforeFirst();

                html.append("<table border=\"1\"><tr>" +
                        "<th>Geburtsdatum</th><th>Name</th></tr>");


                while (rs.next()) {

                    html.append("<tr>");
                    String datum = df.format(rs.getDate("GebDatum"));

                    //SYSCalendar.calculateAge(SYSCalendar.toGC((Date) bw.get("gebdatum")), SYSCalendar.toGC(bisHauf)) + " Jahre) [" + currentBW + "]";
                    String name = SYSTools.anonymizeBW(rs.getString("Nachname"), rs.getString("Vorname"), rs.getString("BWKennung"), rs.getInt("geschlecht"));
                    html.append("<td>" + datum + "</td><td>" + name + "</td>");

                    html.append("</tr>");
                }

                html.append("</table>");
            } else {
                html.append("<h2>");
                html.append("<u>Keine</u> Geburtstage in den nächsten " + tage + " Tagen");
                html.append("</h2>");

            }
        } catch (SQLException sQLException) {
            new DlgException(sQLException);
        }

        return html.toString();
    }

    public static String getInkontinenz(int headertiefe) {
        StringBuilder html = new StringBuilder(1000);
        HashMap bwkennung = new HashMap();
        boolean[] inko = {false, false};

        String sql = "" +
                " SELECT b.BWKennung, CONCAT(b.Nachname, ', ', b.Vorname, ' [', b.BWKennung, ']') name, BI2.XML FROM Bewohner B " +
                " INNER JOIN BWInfo BI1 ON  B.BWKennung = BI1.BWKennung " +
                " INNER JOIN BWInfo BI2 ON  B.BWKennung = BI2.BWKennung " +
                " WHERE  " +
                " BI1.BWINFTYP = 'hauf' AND " +
                " BI1.von <= NOW() AND BI1.bis >= NOW() AND " +
                " B.AdminOnly <> 2  " +
                " AND  " +
                " ( BI2.BWINFTYP LIKE 'INKO%' OR BI2.BWINFTYP LIKE 'HINKO%' )" +
                " AND " +
                "   (  " +
                "       BI2.XML LIKE '%<inko.harn value=\"ja\"/>%' " +
                "       OR " +
                "       BI2.XML LIKE '%<inko.stuhl value=\"ja\"/>%' " +
                "   )" +
                " AND BI2.VON < NOW() AND BI2.BIS > NOW() " +
                " ORDER BY name ";

        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            html.append("<h" + headertiefe + ">");
            html.append("Liste der Bewohner mit Inkontinenz");
            html.append("</h" + headertiefe + ">");

            if (rs.first()) {
                html.append("<table border=\"1\"><tr>" +
                        "<th>BewohnerIn</th><th>Stuhl</th><th>Harn</th></tr>");
                rs.beforeFirst();
                while (rs.next()) {
                    String name = rs.getString("name");
                    String xml = rs.getString("XML");
                    boolean harn = xml.indexOf("<inko.harn value=\"ja\"/>") >= 0;
                    boolean stuhl = xml.indexOf("<inko.stuhl value=\"ja\"/>") >= 0;
                    if (bwkennung.containsKey(rs.getString("b.BWKennung"))) {
                        harn |= (Boolean) ((Object[]) bwkennung.get(rs.getString("b.BWKennung")))[1];
                        stuhl |= (Boolean) ((Object[]) bwkennung.get(rs.getString("b.BWKennung")))[2];
                    }
                    bwkennung.put(rs.getString("b.BWKennung"), new Object[]{name, harn, stuhl});
                }

                SortedSet<String> sortedset = new TreeSet<String>(bwkennung.keySet());

                Iterator it = sortedset.iterator();
                while (it.hasNext()) {
                    Object[] o = (Object[]) bwkennung.get(it.next());
                    html.append("<tr>");
                    html.append("<td>" + o[0].toString() + "</td>");
                    html.append("<td>" + ((Boolean) o[2] ? "Ja" : "Nein") + "</td>");
                    html.append("<td>" + ((Boolean) o[1] ? "Ja" : "Nein") + "</td>");
                    html.append("</tr>");
                }

                html.append("</table>");
            } else {
                html.append("<br/>keine Einträge gefunden...");
            }
        } catch (SQLException sQLException) {
            new DlgException(sQLException);
        }
        return html.toString();
    }

    public static String getAlarmStuhl(int headertiefe) {
        StringBuilder html = new StringBuilder(1000);

        // Für wen soll die Ausfuhr überwacht werden ?

        String sql = "" +
                " SELECT bi.BWInfoID, b.nachname, b.vorname, b.Geschlecht, b.BWKennung FROM BWInfo bi " +
                " INNER JOIN Bewohner b ON bi.BWKennung = b.BWKennung " +
                " INNER JOIN BWInfo ba ON ba.BWKennung = bi.BWKennung " +
                " WHERE b.AdminOnly <> 2 AND bi.BWINFTYP='CONTROL' AND ba.BWINFTYP='HAUF' " +
                " AND bi.XML LIKE '%<c.stuhl value=\"true\"/>%' " +
                " and bi.von < now() and bi.bis > now() " +
                " AND ba.von <= NOW() AND ba.bis >= NOW()" +
                " ORDER BY nachname, vorname ";

        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            DateFormat df = DateFormat.getDateInstance();

            if (rs.first()) {

                rs.beforeFirst();
                while (rs.next()) {
                    Date last = op.care.vital.DBHandling.lastWert(rs.getString("b.BWKennung"), DlgVital.MODE_STUHLGANG);
                    BWInfo bwi = new BWInfo(rs.getLong("bi.BWInfoID"));
                    HashMap antwort = (HashMap) ((HashMap) bwi.getAttribute().get(0)).get("antwort");
                    int tage = Integer.parseInt(antwort.get("c.stuhltage").toString());

                    Date grenze = SYSCalendar.addDate(new Date(), tage * -1);

                    if (last.before(grenze)) {

                        if (html.length() == 0) {
                            html.append("<h" + headertiefe + ">");
                            html.append("Bewohner ohne Stuhlgang");
                            html.append("</h" + headertiefe + ">");
                            html.append("<table border=\"1\"><tr>" +
                                    "<th>BewohnerIn</th><th>Letzter Stuhlgang</th><th>Tage bis Alarm</th></tr>");
                        }

                        html.append("<tr>");
                        String name = SYSTools.anonymizeBW(rs.getString("Nachname"), rs.getString("Vorname"), rs.getString("BWKennung"), rs.getInt("geschlecht"));
                        html.append("<td>" + name + "</td>");
                        html.append("<td>" + df.format(last) + "</td>");
                        html.append("<td>" + tage + "</td>");
                        html.append("</tr>");
                    }

                }

                html.append("</table>");
            }
        } catch (SQLException sQLException) {
            new DlgException(sQLException);
        }
        return html.toString();
    }

    public static String getAlarmEinfuhr(int headertiefe) {
        StringBuilder html = new StringBuilder(1000);

        // Für wen soll die Ausfuhr überwacht werden ?

        String sql = "" +
                " SELECT bi.BWInfoID, b.nachname, b.vorname, b.Geschlecht, b.BWKennung FROM BWInfo bi " +
                " INNER JOIN Bewohner b ON bi.BWKennung = b.BWKennung " +
                " INNER JOIN BWInfo ba ON ba.BWKennung = bi.BWKennung " +
                " WHERE b.AdminOnly <> 2 AND bi.BWINFTYP='CONTROL' AND ba.BWINFTYP='HAUF' " +
                " AND (bi.XML LIKE '%<c.einfuhr value=\"true\"/>%' OR  bi.XML LIKE '%<c.ueber value=\"true\"/>%') " +
                " and bi.von < now() and bi.bis > now() " +
                " AND ba.von <= NOW() AND ba.bis >= NOW()" +
                " ORDER BY nachname, vorname ";

        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            DateFormat df = DateFormat.getDateInstance();

            if (rs.first()) {

                rs.beforeFirst();
                while (rs.next()) {

                    //Date last = op.care.vital.DBHandling.lastWert(rs.getString("b.BWKennung"), DlgVital.MODE_STUHLGANG);
                    BWInfo bwi = new BWInfo(rs.getLong("bi.BWInfoID"));
                    HashMap antwort = (HashMap) ((HashMap) bwi.getAttribute().get(0)).get("antwort");
                    boolean minkontrolle = antwort.get("c.einfuhr").toString().equalsIgnoreCase("true");
                    boolean maxkontrolle = antwort.get("c.ueber").toString().equalsIgnoreCase("true");
                    int tage = Integer.parseInt(antwort.get("c.einftage").toString());
                    int minmenge = Integer.parseInt(antwort.get("c.einfmenge").toString());
                    int maxmenge = Integer.parseInt(antwort.get("c.uebermenge").toString());
                    if (!minkontrolle) {
                        minmenge = -1000000; // Klein genug um im SQL Ausdruck ignoriert zu werden.
                    }
                    if (!maxkontrolle) {
                        maxmenge = 1000000; // Groß genug um im SQL Ausdruck ignoriert zu werden.
                    }

                    String s = " SELECT * FROM (" +
                            "       SELECT PIT, SUM(Wert) EINFUHR FROM BWerte " +
                            "       WHERE ReplacedBy = 0 AND Wert > 0 AND BWKennung=? AND XML='<BILANZ/>' " +
                            "       AND DATE(PIT) >= ADDDATE(DATE(now()), INTERVAL ? DAY) " +
                            "       Group By DATE(PIT) " +
                            "       ORDER BY PIT desc " +
                            " ) a" +
                            " WHERE a.EINFUHR < ? OR a.Einfuhr > ? ";
                    PreparedStatement stmt1 = OPDE.getDb().db.prepareStatement(s);
                    stmt1.setString(1, rs.getString("b.BWKennung"));
                    stmt1.setInt(2, tage * -1);
                    stmt1.setInt(3, minmenge);
                    stmt1.setInt(4, maxmenge);

                    ResultSet rs1 = stmt1.executeQuery();
                    if (rs1.first()) {
                        rs1.beforeFirst();
                        while (rs1.next()) {

                            if (html.length() == 0) {
                                html.append("<h" + headertiefe + ">");
                                html.append("Bewohner mit zu geringer / zu hoher Einfuhr");
                                html.append("</h" + headertiefe + ">");
                                html.append("<table border=\"1\"><tr>" +
                                        "<th>BewohnerIn</th><th>Datum</th><th>Einfuhr (ml)</th><th>Bemerkung</th></tr>");
                            }
                            html.append("<tr>");
                            String name = SYSTools.anonymizeBW(rs.getString("Nachname"), rs.getString("Vorname"), rs.getString("BWKennung"), rs.getInt("geschlecht"));
                            html.append("<td>" + name + "</td>");
                            html.append("<td>" + df.format(rs1.getDate("PIT")) + "</td>");
                            html.append("<td>" + rs1.getString("Einfuhr") + "</td>");
                            html.append("<td>" + (rs1.getDouble("Einfuhr") < minmenge ? "Einfuhr zu niedrig" : "Einfuhr zu hoch") + "</td>");
                            html.append("</tr>");
                        }
                    }
                }
                if (html.length() > 0) {
                    html.append("</table>");
                }
            }
        } catch (SQLException sQLException) {
            new DlgException(sQLException);
        }
        return html.toString();
    }

    /**
     * Erstellt ein HTML Dokument mit dem folgenden Inhalt:
     * <ul>
     * <li>Aufstellung über Häufigkeit der Beschwerden auf Monate verteilt</li>
     * <li>Aufstellung über Häufigkeit der Beschwerden auf Mitarbeiter verteilt</li>
     * <li>Aufstellung über Häufigkeit der Beschwerden auf Bewohner verteilt</li>
     * <li>Aufstellung über die Zeit zwischen öffnen und schließen in Tagen</li>
     * <li>Auflistung aller Beschwerden in einem bestimmten Zeitraum</li>
     * </ul>
     *
     * @param headertiefe
     * @param monate
     * @return
     */
    public static String getBeschwerdeAuswertung(int headertiefe, int monate) {
        StringBuilder html = new StringBuilder(1000);
        String sql = "SET lc_time_names = 'de_DE'";
        String sql1 = "" +
                " SELECT COUNT(*) Anzahl, Monthname(Von) Monat, YEAR(Von) Jahr FROM Vorgaenge v " +
                " INNER JOIN Bewohner b ON b.BWKennung = v.BWKennung " +
                " WHERE VKatID = 2 AND b.AdminOnly <> 2 " +
                " AND DATE(Von) >= DATE_ADD(now(), INTERVAL ? MONTH) " +
                " GROUP BY YEAR(Von), MONTH(Von) " +
                " ORDER BY YEAR(Von), MONTH(Von) ";

        String sql2 = "" +
                " SELECT COUNT(*) Anzahl, CONCAT(o.Nachname, ', ', o.Vorname) MitarbeiterIn FROM Vorgaenge v " +
                " INNER JOIN Bewohner b ON b.BWKennung = v.BWKennung " +
                " INNER JOIN OCUsers o ON o.Ukennung = v.Ersteller " +
                " WHERE v.VKatID = 2 AND v.BWKennung <> '' AND b.AdminOnly <> 2 " +
                " AND DATE(v.Von) >= DATE_ADD(now(), INTERVAL ? MONTH) " +
                " GROUP BY v.Ersteller " +
                " ORDER BY Anzahl DESC, MitarbeiterIn ";

        String sql3 = "" +
                " SELECT COUNT(*) Anzahl, CONCAT(b.Nachname, ', ', b.Vorname) BewohnerIn FROM Vorgaenge v " +
                " INNER JOIN Bewohner b ON b.BWKennung = v.BWKennung " +
                " WHERE v.VKatID = 2 AND v.BWKennung <> '' AND b.AdminOnly <> 2 " +
                " AND DATE(v.Von) >= DATE_ADD(now(), INTERVAL ? MONTH)  " +
                " GROUP BY v.BWKennung " +
                " ORDER BY Anzahl DESC, BewohnerIn ";

        String sql4 = "" +
                " SELECT v.Titel, DATEDIFF(Bis,Von) Tage, CONCAT(b.Nachname, ', ', b.Vorname) Bewohner FROM Vorgaenge v " +
                " INNER JOIN Bewohner b ON b.BWKennung = v.BWKennung " +
                " WHERE v.VKatID = 2 AND v.BWKennung <> '' AND b.AdminOnly <> 2 AND Bis < '9999-12-31 23:59:59' " +
                " AND DATE(v.Von) >= DATE_ADD(now(), INTERVAL ? MONTH)  " +
                " ORDER BY Tage DESC";

        String sql5 = "" +
                " SELECT v.Titel, DATE_FORMAT(Von, '%d.%m.%Y') Von, DATE_FORMAT(Bis, '%d.%m.%Y') Bis, CONCAT(b.Nachname, ', ', b.Vorname) BewohnerIn FROM Vorgaenge v " +
                " INNER JOIN Bewohner b ON b.BWKennung = v.BWKennung " +
                " WHERE v.VKatID = 2 AND v.BWKennung <> '' AND b.AdminOnly <> 2 " +
                " AND DATE(v.Von) >= DATE_ADD(now(), INTERVAL ? MONTH)  " +
                " ORDER BY Von DESC";

        try {
            // Zur Sprachumstellung
            PreparedStatement stmt0 = OPDE.getDb().db.prepareStatement(sql);
            stmt0.execute();
            PreparedStatement stmt1 = OPDE.getDb().db.prepareStatement(sql1);
            stmt1.setInt(1, monate * -1);
            ResultSet rs1 = stmt1.executeQuery();
            PreparedStatement stmt2 = OPDE.getDb().db.prepareStatement(sql2);
            stmt2.setInt(1, monate * -1);
            ResultSet rs2 = stmt2.executeQuery();
            PreparedStatement stmt3 = OPDE.getDb().db.prepareStatement(sql3);
            stmt3.setInt(1, monate * -1);
            ResultSet rs3 = stmt3.executeQuery();
            PreparedStatement stmt4 = OPDE.getDb().db.prepareStatement(sql4);
            stmt4.setInt(1, monate * -1);
            ResultSet rs4 = stmt4.executeQuery();
            PreparedStatement stmt5 = OPDE.getDb().db.prepareStatement(sql5);
            stmt5.setInt(1, monate * -1);
            ResultSet rs5 = stmt5.executeQuery();

            if (rs1.first()) { // wenn rs1 nicht leer ist, dann sind es die anderen auch nicht.
                html.append("<h" + headertiefe + ">");
                html.append("Auswertungen über Beschwerden der letzten " + monate + " Monate");
                html.append("</h" + headertiefe + ">");

                html.append("<h" + (headertiefe + 1) + ">");
                html.append("Häufigkeit nach Monaten");
                html.append("</h" + (headertiefe + 1) + ">");
                html.append(SYSTools.rs2html(rs1, true));

                html.append("<h" + (headertiefe + 1) + ">");
                html.append("Häufigkeit nach Mitarbeitern");
                html.append("</h" + (headertiefe + 1) + ">");
                html.append(SYSTools.rs2html(rs2, true));

                html.append("<h" + (headertiefe + 1) + ">");
                html.append("Häufigkeit nach BewohnerInnen");
                html.append("</h" + (headertiefe + 1) + ">");
                html.append(SYSTools.rs2html(rs3, true));

                html.append("<h" + (headertiefe + 1) + ">");
                html.append("Auswertung Bearbeitungszeit");
                html.append("</h" + (headertiefe + 1) + ">");
                html.append(SYSTools.rs2html(rs4, true));

                html.append("<h" + (headertiefe + 1) + ">");
                html.append("Gesamtaufstellung");
                html.append("</h" + (headertiefe + 1) + ">");
                html.append(SYSTools.rs2html(rs5, true));
            }

        } catch (SQLException sQLException) {
            new DlgException(sQLException);
        }
        return html.toString();
    }
}
