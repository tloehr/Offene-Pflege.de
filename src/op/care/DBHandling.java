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

import entity.Bewohner;
import entity.Einrichtungen;
import entity.PflegeberichteTools;
import op.OPDE;
import tablemodels.TMVerordnung;
import op.share.bwinfo.BWInfo;
import op.share.bwinfo.TMBWInfo;
import op.tools.DBRetrieve;
import op.tools.DlgException;
import op.tools.SYSCalendar;
import op.tools.SYSTools;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.persistence.Query;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @author root
 */
public class DBHandling {

    public static String getUeberleitung(Bewohner bewohner, boolean print,
                                         boolean mitEinrichtung, boolean medi, boolean bilanz, boolean bericht,
                                         boolean diag, boolean grundpflege, boolean haut, boolean vital, boolean bwi) {

        String result = "<h1>Pflegeinformationen</h1>";
        String bwkennung = bewohner.getBWKennung();

        if (print) {
            result += "<h2>" + SYSTools.getBWLabel(bwkennung) + "</h2>";
        }
        result += "<table border=\"1\" cellspacing=\"0\">";
        if (print) {
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT);
            result += "<tr><td valign=\"top\">Gedruckt:</td><td valign=\"top\"><b>" + df.format(SYSCalendar.nowDB()) + " (" + DBRetrieve.getUsername(OPDE.getLogin().getUser().getUKennung()) + ")</b></td></tr>";
        }

        if (mitEinrichtung) {
            if (bewohner.getStation() != null) {
                Einrichtungen einrichtung = bewohner.getStation().getEinrichtung();
                result += "<tr><td valign=\"top\">BewohnerIn wohnt im</td><td valign=\"top\"><b>" + einrichtung.getBezeichnung() + ", " + einrichtung.getStrasse() + ", " + einrichtung.getPlz() + " " + einrichtung.getOrt() + ", Tel.: " + einrichtung.getTel() + ", Fax.: " + einrichtung.getFax() + "</b></td></tr>";
            }
        }

        ArrayList gewicht = op.tools.DBRetrieve.getLetztenBWert(bwkennung, "<GEWICHT/>");
        result += "<tr><td valign=\"top\">Zuletzt bestimmtes Körpergewicht</td><td valign=\"top\"><b>";
        if (gewicht == null) {
            result += "Die/der BW wurde noch nicht gewogen.";
        } else {
            result += gewicht.get(1).toString() + " kg (" + SYSCalendar.printGCGermanStyle(SYSCalendar.toGC((Date) gewicht.get(0))) + ")";
        }
        result += "</b></td></tr>";


        ArrayList groesse = op.tools.DBRetrieve.getLetztenBWert(bwkennung, "<GROESSE/>");
        result += "<tr><td valign=\"top\">Zuletzt bestimmte Körpergröße</td><td valign=\"top\"><b>";
        if (groesse == null) {
            result += "Bisher wurde noch keine Körpergröße ermittelt.";
        } else {
            result += groesse.get(1).toString() + " m (" + SYSCalendar.printGCGermanStyle(SYSCalendar.toGC((Date) groesse.get(0))) + ")";
        }
        result += "</b></td></tr>";

        result += "<tr><td valign=\"top\">Somit letzter BMI</td><td valign=\"top\"><b>";
        if (gewicht == null || groesse == null) {
            result += "Ein BMI kann noch nicht bestimmt werden.";
        } else {
            Double bmi = ((Double) gewicht.get(1)).doubleValue() / (((Double) groesse.get(1)).doubleValue() * ((Double) groesse.get(1)).doubleValue());
            bmi = Math.round(bmi * 100.) / 100.;
            result += bmi;
        }
        result += "</b></td></tr>";

        ArrayList bz = op.tools.DBRetrieve.getLetztenBWert(bwkennung, "<BZ/>");
        result += "<tr><td valign=\"top\">Zuletzt gemessener BZ</td><td valign=\"top\"><b>";
        if (bz == null) {
            result += "Bisher kein BZ Wert vorhanden.";
        } else {
            result += bz.get(1).toString() + " mg/dl (" + SYSCalendar.printGCGermanStyle(SYSCalendar.toGC((Date) bz.get(0))) + ")";
        }
        result += "</b></td></tr>";

        boolean bilanzdurchbwinfo = false;
        int trinkmin = 0;
        int trinkmax = 0;
        if (bilanz) {
            BWInfo bwinfo4 = new BWInfo(bwkennung, "CONTROL", SYSCalendar.nowDBDate());
            if (bwinfo4.getAttribute().size() > 0) {
                HashMap antwort = (HashMap) ((HashMap) bwinfo4.getAttribute().get(0)).get("antwort");
                bilanzdurchbwinfo = antwort.get("c.bilanz").toString().equalsIgnoreCase("true");

                boolean minkontrolle = antwort.get("c.einfuhr").toString().equalsIgnoreCase("true");
                boolean maxkontrolle = antwort.get("c.ueber").toString().equalsIgnoreCase("true");
                if (minkontrolle || maxkontrolle) {
                    trinkmin = Integer.parseInt(antwort.get("c.einfmenge").toString());
                    trinkmax = Integer.parseInt(antwort.get("c.uebermenge").toString());
                    result += "<tr><td valign=\"top\">Trinkmenge</td><td valign=\"top\">";
                    if (minkontrolle) {
                        result += "Die Trinkmenge sollte nicht <b><u>unter</u> " + trinkmin + " ml in 24h</b> liegen.<br/>";
                    }
                    if (maxkontrolle) {
                        result += "Die Trinkmenge sollte nicht <b><u>über</u> " + trinkmax + " ml in 24h</b> liegen.";
                    }
                    result += "</td></tr>";
                }
            }
            bwinfo4.cleanup();
        }
        bilanzdurchbwinfo &= bilanz;

//        boolean zieltrinkmenge = false;
//        int zieltrink = 0;
//        BWInfo bwinfo3 = new BWInfo(bwkennung, "ZIELTRINK", SYSCalendar.nowDBDate());
//        if (bwinfo3.getAttribute().size() > 0) {
//            TMBWInfo tmbwi3 = new TMBWInfo(bwinfo3.getAttribute(), true, false, false);
//            HashMap antwort = (HashMap) ((HashMap) bwinfo3.getAttribute().get(0)).get("antwort");
//            zieltrink = Integer.parseInt(antwort.get("zieltrinkmenge").toString());
//            zieltrinkmenge = true;
//            result += "<tr><td valign=\"top\">Zieltrinkmenge</td><td valign=\"top\">";
//            result += SYSTools.anonymizeString(SYSTools.unHTML2(tmbwi3.getValueAt(0, TMBWInfo.COL_HTML).toString()));
//            result += "</td></tr>";
//        }
//        bwinfo3.cleanup();
//        zieltrinkmenge &= bilanz;

        // Beginn des aktuellen Heimaufenthaltes.

        BWInfo bwinfoHAUF = new BWInfo(bwkennung, "HAUF", SYSCalendar.nowDBDate());
        if (bwinfoHAUF.getAttribute().size() > 0) {
            TMBWInfo tmbwihauf = new TMBWInfo(bwinfoHAUF.getAttribute(), true, false, false);
            result += "<tr><td valign=\"top\">Heimaufnahme</td><td valign=\"top\">";
            DateFormat df = DateFormat.getDateInstance();
            result += "<b>" + df.format(bwinfoHAUF.getVonHauf()) + "</b>";
            result += "</td></tr>";
        }
        bwinfoHAUF.cleanup();


        BWInfo bwinfoPS = new BWInfo(bwkennung, "PSTF", SYSCalendar.nowDBDate());
        if (bwinfoPS.getAttribute().size() > 0) {
            TMBWInfo tmbwips = new TMBWInfo(bwinfoPS.getAttribute(), true, false, false);
            result += "<tr><td valign=\"top\">Pflegestufe</td><td valign=\"top\">";
            result += SYSTools.anonymizeString(SYSTools.unHTML2(tmbwips.getValueAt(0, TMBWInfo.COL_HTML).toString()));
            result += "</td></tr>";
        }
        bwinfoPS.cleanup();

        BWInfo bwinfo1 = new BWInfo(bwkennung, "BETREUER1", SYSCalendar.nowDBDate());
        if (bwinfo1.getAttribute().size() > 0) {
            TMBWInfo tmbwi1 = new TMBWInfo(bwinfo1.getAttribute(), true, false, false);
            result += "<tr><td valign=\"top\">BetreuerIn</td><td valign=\"top\">";
            result += SYSTools.anonymizeString(SYSTools.unHTML2(tmbwi1.getValueAt(0, TMBWInfo.COL_HTML).toString()));
            result += "</td></tr>";
        }
        bwinfo1.cleanup();

        BWInfo bwinfo2 = new BWInfo(bwkennung, "ANGEH", SYSCalendar.nowDBDate());
        if (bwinfo2.getAttribute().size() > 0) {
            TMBWInfo tmbwi2 = new TMBWInfo(bwinfo2.getAttribute(), true, false, false);
            result += "<tr><td valign=\"top\">Angehörige</td><td valign=\"top\">";
            result += SYSTools.anonymizeString(SYSTools.unHTML2(tmbwi2.getValueAt(0, TMBWInfo.COL_HTML).toString()));
            result += "</td></tr>";
        }
        bwinfo2.cleanup();

        result += "</table>";

        result += getAerzte(bwkennung);

        if (diag) {
            result += getDiagnosen(bwkennung);
        }

        if (medi) {
            TMVerordnung tmv = new TMVerordnung(bewohner, false, false);
            result += op.care.verordnung.DBHandling.getVerordnungenAsHTML(tmv, bewohner, null);
        }

        if (bericht) {

            Query query = OPDE.getEM().createNamedQuery("Pflegeberichte.findByBewohnerWithinPeriod");
            query.setParameter("bewohner", bewohner);
            query.setParameter("von", SYSCalendar.addDate(new Date(), -7));
            query.setParameter("bis", new Date());

            result += PflegeberichteTools.getBerichteAsHTML(query.getResultList(), true);

        }

        if (bilanzdurchbwinfo) {
            boolean hateinfuhren = hatEinfuhren(bwkennung);
            boolean hatausfuhren = hatAusfuhren(bwkennung);
            if (!(hatausfuhren || hateinfuhren)) {
                result += "<h2>Bilanzierung / Trinkplan gewünscht aber keine Daten vorhanden !</h2>";
            } else {
                if (hatAusfuhren(bwkennung)) {
                    try {
                        String s = "SELECT ein.PIT, ein.EINFUHR, ifnull(aus.AUSFUHR,0) AUSFUHR, (ein.EINFUHR+ifnull(aus.AUSFUHR,0)) BILANZ FROM "
                                + "("
                                + "   SELECT PIT, SUM(Wert) AUSFUHR FROM BWerte "
                                + "   WHERE ReplacedBy = 0 AND Wert < 0 AND BWKennung=? AND XML='<BILANZ/>' AND DATE(PIT) >= ADDDATE(DATE(now()), INTERVAL -7 DAY)"
                                + "   GROUP BY DATE(PIT) "
                                + ") aus"
                                + " "
                                + "RIGHT OUTER JOIN"
                                + " "
                                + "("
                                + "   SELECT PIT, SUM(Wert) EINFUHR FROM BWerte "
                                + "   WHERE ReplacedBy = 0 AND Wert > 0 AND BWKennung=? AND XML='<BILANZ/>' AND DATE(PIT) >= ADDDATE(DATE(now()), INTERVAL -7 DAY)"
                                + "   GROUP BY DATE(PIT) "
                                + ") ein "
                                + "ON DATE(aus.PIT) = DATE(ein.PIT) "
                                + "ORDER BY aus.PIT desc";
                        PreparedStatement stmt = OPDE.getDb().db.prepareStatement(s);
                        stmt.setString(1, bwkennung);
                        stmt.setString(2, bwkennung);

                        ResultSet rs = stmt.executeQuery();
                        if (rs.first()) {

                            result += "<h2>Bilanzierung</h2><table border=\"1\" cellspacing=\"0\"><tr>"
                                    + "<th>Datum</th><th>Einfuhr</th><th>Ausfuhr</th><th>Bilanz</th><th>Hinweis</th></tr>";

                            rs.beforeFirst();
                            while (rs.next()) {


                                DateFormat df = DateFormat.getDateInstance();
                                result += "<tr>";
                                result += "<td>" + df.format(rs.getDate("PIT")) + "</td>";
                                result += "<td>" + rs.getDouble("Einfuhr") + "</td>";
                                result += "<td>" + Math.abs(rs.getDouble("Ausfuhr")) + "</td>";
                                result += "<td>" + rs.getDouble("Bilanz") + "</td>";
                                if (trinkmin > 0 && rs.getDouble("Einfuhr") < trinkmin) {
                                    result += "<td>Einfuhr zu niedrig. Minimum: " + trinkmin + " ml in 24h</td>";
                                } else if (trinkmax > 0 && rs.getDouble("Einfuhr") > trinkmax) {
                                    result += "<td>Einfuhr zu hoch. Maximum: " + trinkmax + " ml in 24h</td>";
                                } else {
                                    result += "<td>--</td>";
                                }

                                result += "</tr>";
                            }
                            result += "</table>";
                        }

                    } // try
                    catch (SQLException se) {
                        new DlgException(se);
                    } // catch


                } else if (hatEinfuhren(bwkennung)) {
                    try {
                        result += "<h2>Einfuhrprotokoll</h2><table border=\"1\" cellspacing=\"0\"><tr>"
                                + "<th>Datum</th><th>Einfuhr</th><th>Hinweis</th></tr>";

                        String s = " SELECT PIT, SUM(Wert) EINFUHR FROM BWerte "
                                + "   WHERE ReplacedBy = 0 AND Wert > 0 AND BWKennung=? AND XML='<BILANZ/>' "
                                + "   AND DATE(PIT) >= ADDDATE(DATE(now()), INTERVAL -7 DAY) "
                                + "   Group By DATE(PIT) "
                                + " ORDER BY PIT desc";
                        PreparedStatement stmt = OPDE.getDb().db.prepareStatement(s);
                        stmt.setString(1, bwkennung);

                        ResultSet rs = stmt.executeQuery();
                        rs.beforeFirst();
                        while (rs.next()) {

                            DateFormat df = DateFormat.getDateInstance();
                            result += "<tr>";
                            result += "<td>" + df.format(rs.getDate("PIT")) + "</td>";
                            result += "<td>" + rs.getDouble("Einfuhr") + "</td>";
                            if (trinkmin > 0 && rs.getDouble("Einfuhr") < trinkmin) {
                                result += "<td>Einfuhr zu niedrig. Minimum: " + trinkmin + " ml in 24h</td>";
                            } else if (trinkmax > 0 && rs.getDouble("Einfuhr") > trinkmax) {
                                result += "<td>Einfuhr zu hoch. Maximum: " + trinkmax + " ml in 24h</td>";
                            } else {
                                result += "<td>--</td>";
                            }
                            result += "</tr>";
                        }
                        result += "</table>";

                    } catch (SQLException se) {
                        new DlgException(se);
                    }
                }
            }
        }

        if (grundpflege) {
            BWInfo bwinfo = new BWInfo(bwkennung, SYSCalendar.nowDBDate(), 0, 1, false);
            TMBWInfo tmbwi = new TMBWInfo(bwinfo.getAttribute(), true, false, false);
            int numBwi = tmbwi.getRowCount();
            if (numBwi > 0) {
                result += "<h2>Körperpflege</h2>";
                for (int v = 0; v < numBwi; v++) {
                    result += SYSTools.unHTML2(tmbwi.getValueAt(v, TMBWInfo.COL_HTML).toString());
                    result += "<br/><br/>";
                }
            }
            bwinfo.cleanup();
        }
        if (haut) {
            BWInfo bwinfo = new BWInfo(bwkennung, SYSCalendar.nowDBDate(), 0, 9, false);
            TMBWInfo tmbwi = new TMBWInfo(bwinfo.getAttribute(), true, false, false);
            int numBwi = tmbwi.getRowCount();
            if (numBwi > 0) {
                result += "<h2>Hautzustand / Wunden</h2>";
                for (int v = 0; v < numBwi; v++) {
                    result += SYSTools.unHTML2(tmbwi.getValueAt(v, TMBWInfo.COL_HTML).toString());
                    result += "<br/><br/>";
                }
            }
            bwinfo.cleanup();
        }
        if (vital) {
            BWInfo bwinfo = new BWInfo(bwkennung, SYSCalendar.nowDBDate(), 0, 8, false);
            TMBWInfo tmbwi = new TMBWInfo(bwinfo.getAttribute(), true, false, false);
            int numBwi = tmbwi.getRowCount();
            if (numBwi > 0) {
                result += "<h2>Besonderheiten</h2>";
                for (int v = 0; v < numBwi; v++) {
                    result += SYSTools.unHTML2(tmbwi.getValueAt(v, TMBWInfo.COL_HTML).toString());
                    result += "<br/><br/>";
                }
            }
            bwinfo.cleanup();
        }
        if (bwi) {
            BWInfo bwinfo = new BWInfo(bwkennung, BWInfo.ART_PFLEGE_STAMMDATEN, false);
            TMBWInfo tmbwi = new TMBWInfo(bwinfo.getAttribute(), true, false, false);
            int numBwi = tmbwi.getRowCount();
            if (numBwi > 0) {
                for (int v = 0; v < numBwi; v++) {
                    //OPDE.getLogger().debug(tmbwi.getValueAt(v, TMBWInfo.COL_KATBEZ));
                    if (!tmbwi.getValueAt(v, TMBWInfo.COL_KATBEZ).toString().equalsIgnoreCase("")) {
                        result += "<h2>" + SYSTools.unHTML2(tmbwi.getValueAt(v, TMBWInfo.COL_KATBEZ).toString()) + "</h2>";
                    } else {
                        result += "<br/><br/>";
                    }

                    result += SYSTools.unHTML2(tmbwi.getValueAt(v, TMBWInfo.COL_HTML).toString());
                }
            }
            bwinfo.cleanup();
        }
//        result = "<head>" +
//                "<title>" + SYSTools.getWindowTitle("") + "</title>" +
//                "<script type=\"text/javascript\">" +
//                "window.onload = function() {" +
//                "window.print();" +
//                "}</script></head><body>"+result;
        String tmp = "<html><head>"
                + "<title>" + SYSTools.getWindowTitle("") + "</title>";
        if (print) {
            tmp = tmp + "<script type=\"text/javascript\">"
                    + "window.onload = function() {"
                    + "window.print();"
                    + "}</script></head><body>" + result
                    + "<hr/><b>Ende des Berichtes</b><br/>http://www.offene-pflege.de</body></html>";
        } else {
            tmp = tmp + result;
        }
        tmp += "</body></html>";
        return tmp;
    }

    public static boolean hatEinfuhren(String bwkennung) {
        boolean result = false;
        try {

            String s = "   "
                    + "SELECT SUM(Wert) EINFUHR FROM BWerte "
                    + "   WHERE Wert > 0 AND BWKennung=? AND XML='<BILANZ/>' AND PIT >= ADDDATE(now(), INTERVAL -7 DAY)";
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(s);
            stmt.setString(1, bwkennung);

            ResultSet rs = stmt.executeQuery();
            result =
                    rs.first() && rs.getDouble("EINFUHR") > 0;
        } // try
        catch (SQLException se) {
            new DlgException(se);
        } // catch

        return result;
    }

    public static boolean hatAusfuhren(String bwkennung) {
        boolean result = false;
        try {

            String s = "   "
                    + "SELECT SUM(Wert) AUSFUHR FROM BWerte "
                    + "   WHERE Wert < 0 AND BWKennung=? AND XML='<BILANZ/>' AND PIT >= ADDDATE(now(), INTERVAL -7 DAY)";
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(s);
            stmt.setString(1, bwkennung);

            ResultSet rs = stmt.executeQuery();
            result =
                    rs.first() && Math.abs(rs.getDouble("AUSFUHR")) > 0;
        } // try
        catch (SQLException se) {
            new DlgException(se);
        } // catch

        return result;
    }

    private static String getDiagnosen(String bwkennung) {
        String result = "";
        BWInfo bwinfo = new BWInfo(bwkennung, "DIAG", SYSCalendar.nowDBDate());
        //TMBWInfo tmbwi = new TMBWInfo(bwinfo.getAttribute(), true, false);

        int numBwi = bwinfo.getAttribute().size();
        if (numBwi > 0) {
            result += "<h2>Diagnosen</h2>";
            result +=
                    "<table border=\"1\" cellspacing=\"0\">";
            result +=
                    "<tr><th>ICD</th><th>Diagnose</th><th>Körperseite</th><th>Diagnose-Sicherheit</th></tr>";
            for (int v = 0; v
                    < numBwi; v++) {
                HashMap hm = (HashMap) bwinfo.getAttribute().get(v);
                String xml = hm.get("xmlc").toString();
                if (!xml.equalsIgnoreCase("<unbeantwortet value=\"true\"/>")) {
                    xml = "<?xml version=\"1.0\"?><xml>" + xml + "</xml>";
                    try {
                        XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
                        InputSource is = new org.xml.sax.InputSource(new java.io.BufferedReader(new java.io.StringReader(xml)));
                        HandlerDiagnosen d = new HandlerDiagnosen();
                        parser.setContentHandler(d);
                        parser.parse(is);
                        result +=
                                "<tr><td>" + d.getICD() + "</td><td>" + d.getDiagnose() + "</td><td>" + d.getSeite() + "</td><td>" + d.getSicherheit() + "</td></tr>";
                    } catch (SAXException sAXException) {
                    } catch (IOException iOException) {
                    }
                }
            }
            result += "</table>";
        }

        return result;
    }

    private static String getAerzte(String bwkennung) {
        String result = "";
        BWInfo bwinfo1 = new BWInfo(bwkennung, "HAUSARZT", SYSCalendar.nowDBDate());
        BWInfo bwinfo2 = new BWInfo(bwkennung, "FACHARZT", SYSCalendar.nowDBDate());

        int numBwi1 = bwinfo1.getAttribute().size();
        int numBwi2 = bwinfo2.getAttribute().size();

        if (numBwi1 + numBwi2 > 0) {
            result += "<h2>Ärzte</h2>";
            if (numBwi1 > 0) {
                TMBWInfo tmbwi = new TMBWInfo(bwinfo1.getAttribute(), true, false, false);
                result +=
                        SYSTools.anonymizeString(SYSTools.unHTML2(tmbwi.getValueAt(0, TMBWInfo.COL_HTML).toString()));
            }

            if (numBwi2 > 0) {
                TMBWInfo tmbwi = new TMBWInfo(bwinfo2.getAttribute(), true, false, false);
                for (int v = 0; v
                        < numBwi2; v++) {
                    //HashMap hm = (HashMap) bwinfo2.getAttribute().get(v);
                    //String xml = hm.get("xmlc").toString();
                    result += SYSTools.anonymizeString(SYSTools.unHTML2(tmbwi.getValueAt(v, TMBWInfo.COL_HTML).toString()));
                }
//result += "</table>";

            }
        }
        bwinfo1.cleanup();
        bwinfo2.cleanup();
        return result;
    }

    private static class HandlerDiagnosen extends DefaultHandler {

        private String icd;
        private String diagnose;

        public String getSeite() {
            return seite;
        }

        public String getSicherheit() {
            return sicherheit;
        }

        private String sicherheit;
        private String seite;

        public void startElement(String nsURI, String strippedName, String tagName, Attributes attributes) throws SAXException {
            if (tagName.equalsIgnoreCase("java")) {
                icd = attributes.getValue("icd");
                diagnose = attributes.getValue("text");
                sicherheit = attributes.getValue("diagnosesicherheit");
                seite = attributes.getValue("koerperseite");
            }
        }

        public String getICD() {
            return icd;
        }

        public String getDiagnose() {
            return diagnose;
        }
    }
}
