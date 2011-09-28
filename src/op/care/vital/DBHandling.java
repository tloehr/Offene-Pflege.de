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
package op.care.vital;

import op.OPDE;
import op.tools.*;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

/**
 * @author tloehr
 */
public class DBHandling {

    public static String getBWertArt(String xml) {
        String result;
        if (xml.indexOf("<RRSYS/>") >= 0) {
            result = "Blutdruck Systole";
        } else if (xml.indexOf("<RRDIA/>") >= 0) {
            result = "Blutdruck Diastole";
        } else if (xml.indexOf("<TEMP/>") >= 0) {
            result = "Temperatur";
        } else if (xml.indexOf("<PULS/>") >= 0) {
            result = "Puls";
        } else if (xml.indexOf("<BZ/>") >= 0) {
            result = "Blutzucker";
        } else if (xml.indexOf("<GEWICHT/>") >= 0) {
            result = "Gewicht";
        } else if (xml.indexOf("<GROESSE/>") >= 0) {
            result = "Groesse";
        } else if (xml.indexOf("<ATEM/>") >= 0) {
            result = "Atemfrequenz";
        } else if (xml.indexOf("<braden") >= 0) {
            result = "Bradenskala";
        } else if (xml.indexOf("<QUICK/>") >= 0) {
            result = "Quickwert";
        } else if (xml.indexOf("<STUHLGANG/>") >= 0) {
            result = "Stuhlgang";
        } else if (xml.indexOf("<ERBRECHEN/>") >= 0) {
            result = "Erbrochen";
        } else if (xml.indexOf("<BILANZ/>") >= 0) {
            result = "Ein-/Ausfuhrbilanz";
        } else {
            result = "?? unbekannt ??";
        }
        return result;
    }

    public static String getBWertEinheit(String xml) {
        String result;
        if (xml.indexOf("<RRSYS/>") >= 0) {
            result = "mmHg";
        } else if (xml.indexOf("<RRDIA/>") >= 0) {
            result = "mmHg";
        } else if (xml.indexOf("<TEMP/>") >= 0) {
            result = "°C";
        } else if (xml.indexOf("<PULS/>") >= 0) {
            result = "s/m";
        } else if (xml.indexOf("<BZ/>") >= 0) {
            result = "mg/dl";
        } else if (xml.indexOf("<GEWICHT/>") >= 0) {
            result = "kg";
        } else if (xml.indexOf("<GROESSE/>") >= 0) {
            result = "m";
        } else if (xml.indexOf("<ATEM/>") >= 0) {
            result = "A/m";
        } else if (xml.indexOf("<braden") >= 0) {
            result = "Bradenskala";
        } else if (xml.indexOf("<QUICK/>") >= 0) {
            result = "%";
        } else if (xml.indexOf("<STUHLGANG/>") >= 0) {
            result = "";
        } else if (xml.indexOf("<ERBRECHEN/>") >= 0) {
            result = "";
        } else if (xml.indexOf("<BILANZ/>") >= 0) {
            result = "ml";
        } else {
            result = "?? unbekannt ??";
        }
        return result;
    }

    public static int getBWertMode(String xml) {
        int result;
        if (xml.indexOf("<RRSYS/>") >= 0) {
            result = DlgVital.MODE_RRSYS;
        } else if (xml.indexOf("<RRDIA/>") >= 0) {
            result = DlgVital.MODE_RRDIA;
        } else if (xml.indexOf("<TEMP/>") >= 0) {
            result = DlgVital.MODE_TEMP;
        } else if (xml.indexOf("<RR/>") >= 0) {
            result = DlgVital.MODE_RR;
        } else if (xml.indexOf("<PULS/>") >= 0) {
            result = DlgVital.MODE_PULS;
        } else if (xml.indexOf("<BZ/>") >= 0) {
            result = DlgVital.MODE_BZ;
        } else if (xml.indexOf("<GEWICHT/>") >= 0) {
            result = DlgVital.MODE_GEWICHT;
        } else if (xml.indexOf("<GROESSE/>") >= 0) {
            result = DlgVital.MODE_GROESSE;
        } else if (xml.indexOf("<ATEM/>") >= 0) {
            result = DlgVital.MODE_ATEM;
        } else if (xml.indexOf("<QUICK/>") >= 0) {
            result = DlgVital.MODE_QUICK;
        } else if (xml.indexOf("<STUHLGANG/>") >= 0) {
            result = DlgVital.MODE_STUHLGANG;
        } else if (xml.indexOf("<ERBRECHEN/>") >= 0) {
            result = DlgVital.MODE_ERBRECHEN;
        } else if (xml.indexOf("<BILANZ/>") >= 0) {
            result = DlgVital.MODE_BILANZ;
        } else {
            result = DlgVital.MODE_UNKNOWN;
        }
        return result;
    }

    public static String getBWertXML(int mode) {

        String result;

        switch (mode) {
            case DlgVital.MODE_RRSYS: {
                result = "<RRSYS/>";
                break;
            }
            case DlgVital.MODE_RRDIA: {
                result = "<RRDIA/>";
                break;
            }
            case DlgVital.MODE_TEMP: {
                result = "<TEMP/>";
                break;
            }
            case DlgVital.MODE_RR: {
                result = "<RR/>";
                break;
            }
            case DlgVital.MODE_PULS: {
                result = "<PULS/>";
                break;
            }
            case DlgVital.MODE_BZ: {
                result = "<BZ/>";
                break;
            }
            case DlgVital.MODE_GEWICHT: {
                result = "<GEWICHT/>";
                break;
            }
            case DlgVital.MODE_GROESSE: {
                result = "<GROESSE/>";
                break;
            }
            case DlgVital.MODE_ATEM: {
                result = "<ATEM/>";
                break;
            }
            case DlgVital.MODE_QUICK: {
                result = "<QUICK/>";
                break;
            }
            case DlgVital.MODE_ERBRECHEN: {
                result = "<ERBRECHEN/>";
                break;
            }
            case DlgVital.MODE_STUHLGANG: {
                result = "<STUHLGANG/>";
                break;
            }
            case DlgVital.MODE_BILANZ: {
                result = "<BILANZ/>";
                break;
            }
            default: {
                result = "<UNKNOWN/>";
                break;
            }
        }
        return result;
    }

    public static String getWertAsHTML(long bwid) {
        String result = "";
        HashMap bwert1 = DBRetrieve.getSingleRecord("BWerte", "BWID", bwid);
        // Jetzt kann es sein, dass der eine BewohnerWert einen Blutdruck mit Puls darstellt. Dann brauchen wir noch die anderen beiden Werte.
        // Das gilt nur für Records, deren Beziehung > 0 ist.
        // Die Spalte Sortierung sorgt nur dafür, das Systole, Diastole und Puls immer in der richtigen Reihenfolge stehen.
        String xml = bwert1.get("XML").toString();
        long beziehung = ((BigInteger) bwert1.get("Beziehung")).longValue();

        if (beziehung > 0) {
            ResultSet rs = DBRetrieve.getResultSet("BWerte", "Beziehung", beziehung, "=", new String[]{"Sortierung"});
            // Durch die Sortierung des ResultSets nach Beziehung und dann erst nach Datum und Uhrzeit
            // ist sichergestellt, dass die RR Werte immer in der richtigen Reihenfolge zusammen stehen.
            int sys;
            try {
                sys = (int) rs.getDouble("Wert");
                rs.next();
                int dia = (int) rs.getDouble("Wert");
                rs.next();
                int puls = (int) rs.getDouble("Wert");
                result = "<b>Blutdruck/Puls</b> " + sys + "/" + dia + " P" + puls;
            } catch (SQLException ex) {
                OPDE.fatal(ex);
            }
        } else {
            result = "<b>" + getBWertArt(xml) + "</b> " + bwert1.get("Wert").toString();
        }

        if (!SYSTools.catchNull(bwert1.get("Bemerkung")).equals("")) {
            result += " (" + bwert1.get("Bemerkung").toString() + ")";
        }

        return result;
    }

    public static String getWerteAsHTML(TMWerte tm, String bwkennung, int[] sel) {

        String html = "";

        html += "<h1>Bewohner-Werte für " + SYSTools.getBWLabel(bwkennung) + "</h1>";

        int num = tm.getRowCount();
        if (num > 0) {
            html += "<table border=\"1\" cellspacing=\"0\"><tr>" +
                    "<th style=\"width:30%\">Info</th><th style=\"width:70%\">Wert</th></tr>";
            for (int v = 0; v < num; v++) {
                if (sel == null || Arrays.binarySearch(sel, v) > -1) {
                    html += "<tr>";
                    html += "<td>" + tm.getValueAt(v, TMWerte.TBL_PIT).toString() + "</td>";
                    html += "<td>" + tm.getValueAt(v, TMWerte.TBL_HTML).toString() + "</td>";
                    html += "</tr>";
                }
            }
            html += "</table>";
        } else {
            html += "<i>keine Werte in der Auswahl vorhanden</i>";
        }

        html = "<html><head>" +
                "<title>" + SYSTools.getWindowTitle("") + "</title>" +
                "<script type=\"text/javascript\">" +
                "window.onload = function() {" +
                "window.print();" +
                "}</script></head><body>" + html + "</body></html>";
        return html;
    }

    /**
     * Ermittelt das Datum des ersten Bewohnerwertes den ein bestimmter Bewohner besitzt.
     *
     * @param bwkennung
     * @return
     */
    public static Date firstWert(String bwkennung) {
        Date result;
        String sql =
                " SELECT PIT " +
                        " FROM BWerte " +
                        " WHERE BWKennung = ? AND ReplacedBy = 0" +
                        " ORDER BY PIT " +
                        " LIMIT 0, 1 ";
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setString(1, bwkennung);
            ResultSet rs = stmt.executeQuery();
            if (rs.first()) {
                result = new Date(rs.getTimestamp("PIT").getTime());
            } else {
                result = SYSCalendar.today_date();
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            new DlgException(ex);
            result = SYSCalendar.today_date();
        }
        return result;
    }

    /**
     * Ermittelt das Datum des ersten Bewohnerwertes den ein bestimmter Bewohner besitzt.
     *
     * @param bwkennung
     * @return
     */
    public static Date lastWert(String bwkennung, int wertart) {
        Date result;
        String sql =
                " SELECT PIT " +
                        " FROM BWerte " +
                        " WHERE BWKennung = ? AND ReplacedBy = 0 AND XML = ? " +
                        " ORDER BY PIT DESC" +
                        " LIMIT 0, 1 ";
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setString(1, bwkennung);
            stmt.setString(2, getBWertXML(wertart));
            ResultSet rs = stmt.executeQuery();
            if (rs.first()) {
                result = new Date(rs.getTimestamp("PIT").getTime());
            } else {
                result = SYSConst.DATE_BIS_AUF_WEITERES;
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            new DlgException(ex);
            result = SYSConst.DATE_BIS_AUF_WEITERES;
        }
        return result;
    }
}
