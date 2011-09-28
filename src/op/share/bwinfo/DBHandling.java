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
package op.share.bwinfo;

import entity.Bewohner;
import entity.EinrichtungenTools;
import op.OPDE;
import op.tools.*;

import javax.swing.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

/**
 * @author tloehr
 */
public class DBHandling {

    /**
     * Diese Methode sammelt das aktuelleste, letzte Attribut, gemäß eines bwinftyp und einer BWKennung auf.
     *
     * @param bwkennung - Kennung des Bewohners
     * @param bwinftyp  - Kennung des gewünschten Attributs
     * @return Liste der Daten des letzten Attributes (XMLC, Von, Bis, BWINFOID, _creator, _editor, _cdate, _mdate). Datentypen
     *         (String, Date, Date, long, String, String, Timestamp, Timestamp)
     */
    public static ArrayList getLastBWInfo(String bwkennung, String bwinftyp) {
        //OPDE.getLogger().debug("DBRetrieve.getLastBWInfo() :: " + bwkennung + ", " + bwinftyp);
        ArrayList result = new ArrayList();
        String sql = "SELECT BWINFOID, XML, Von, Bis, AnUKennung FROM BWInfo " +
                " WHERE BWKennung=? AND BWINFTYP=? " +
                " ORDER BY Von DESC " +
                " LIMIT 0,1 ";

        try {
            PreparedStatement stmt = OPDE.db.db.prepareStatement(sql);
            stmt.setString(1, bwkennung);
            stmt.setString(2, bwinftyp);
            ResultSet rs = stmt.executeQuery();
            if (rs.first()) {
                result.add(rs.getString("XML"));
                result.add(rs.getTimestamp("Von"));
                result.add(rs.getTimestamp("Bis"));
                result.add(rs.getLong("BWINFOID"));
                result.add(rs.getString("AnUKennung"));
            } else {
                result = null;
            }
        } catch (SQLException ex) {
            new DlgException(ex);
        }
        return result;
    }

    /**
     * Fügt eine neue Zeile in BWInfo ein. Immer ab sofort bis auf weiteres.
     *
     * @param bwinfoid
     * @param bwkennung
     */
    public static long neueBWInfoEinfuegen(String bwinftyp, String bwkennung) {
        int interval = (Integer) op.tools.DBRetrieve.getSingleValue("BWInfoTyp", "IntervalMode", "BWINFTYP", bwinftyp);
        HashMap data = new HashMap();
        data.put("BWINFTYP", bwinftyp);
        data.put("BWKennung", bwkennung);
        data.put("AnUKennung", OPDE.getLogin().getUser().getUKennung());
        if (interval == BWInfo.MODE_INTERVAL_BYDAY) {
            data.put("Von", new Date(SYSCalendar.startOfDay()));
            data.put("Bis", "!BAW!");
        } else if (interval == BWInfo.MODE_INTERVAL_BYSECOND) {
            data.put("Von", new Date(SYSCalendar.midOfDay()));
            data.put("Bis", "!BAW!");
        } else if (interval == BWInfo.MODE_INTERVAL_NOCONSTRAINTS) {
            data.put("Von", "!NOW!");
            data.put("Bis", "!BAW!");
        } else if (interval == BWInfo.MODE_INTERVAL_SINGLE_INCIDENTS) {
            data.put("Von", "!NOW!");
            data.put("Bis", "!NOW!");
        }
        data.put("XML", "<unbeantwortet value=\"true\"/>");
        return op.tools.DBHandling.insertRecord("BWInfo", data);
    }

    /**
     * Diese Methode berechnet zu einem PrimaryKey eines bekannten Zeitraums den Inhalt des vorhergehenden oder nachfolgenden Zeitraums aus der
     * BWInfo Tabelle.
     *
     * @param pk    des gegebenen Zeitraums.
     * @param true, wenn der vorhergehenden Zeitraum gesucht wird. false, wenn der nachfolgende gesucht wird.
     * @return Eine Liste mit den Daten des Zeitraums (long BWINFOID, Date von, Date bis, String xml). Null, wenn des keinen vorhergehenden Zeitraum gab oder bei Exception
     */
    public static ArrayList getAdjacentBWInfo(long bwinfoid, boolean previous) {
        ArrayList result = null;
        // An den beiden Stellen unterscheidet sich der SQL Konstrukt.
        String op1 = (previous ? "MAX" : "MIN");
        String op2 = (previous ? "<" : ">");

        // Dieser SQL Konstrukt funktioniert nur, wenn die PKs streng monoton steigend verwendet wurden, da er
        // mit MAX(PK) bzw. MIN(PK) gearbeitet wird. Bei unseren AutoIncrement Tabellen ist das aber kein Problem.
        String sql =
                " SELECT BWINFOID, Von, Bis, XML FROM BWInfo WHERE BWINFOID = " +
                        "   (" +
                        "       SELECT " + op1 + "(BWINFOID) FROM BWInfo " +
                        "       WHERE BWKennung=(SELECT BWKennung FROM BWInfo B WHERE BWINFOID=?) " +
                        "       AND BWINFTYP=(SELECT BWINFTYP FROM BWInfo B WHERE BWINFOID=?) " +
                        "       AND Von " + op2 + " (SELECT Von FROM BWInfo B WHERE BWINFOID=?) " +
                        "   )";
        try {
            PreparedStatement stmt = OPDE.db.db.prepareStatement(sql);
            stmt.setLong(1, bwinfoid);
            stmt.setLong(2, bwinfoid);
            stmt.setLong(3, bwinfoid);
            ResultSet rs = stmt.executeQuery();

            if (rs.first()) {
                result = new ArrayList();
                result.add(rs.getLong("BWINFOID"));
                result.add(rs.getDate("Von"));
                result.add(rs.getDate("Bis"));
                result.add(rs.getString("XML"));
            }

        } catch (SQLException ex) {
            new DlgException(ex);
            result = null;
        }
        return result;
    }

    /**
     * Gibt eine kurze BWInfo zurück. Wir meistens für den BW Info Label verwendet.
     *
     * @param bwkennung
     * @return Die Informationen in dem Array sind: (aufenthalt, vonHauf, bisHauf, ausgezogen, verstorben)
     *         <ul>
     *         <li>aufenthalt. Boolean ob der BW zur Zeit in der Einrichtung wohnt.</li>
     *         <li>vonHauf. Date, seit wann der BW in der Einrichtung wohnt.</li>
     *         <li>bisHauf. Date, bis wann der BW in der Einrichtung wohnt.</li>
     *         <li>ausgezogen. boolean, true wenn ausgezogen.</li>
     *         <li>verstorben. boolean, true wenn verstorben.</li>
     *         </ul>
     */
    public static Object[] miniBWInfo(String bwkennung) {
        // Gibt es einen aktuellen oder zurückliegenden Heimaufenthalt.
        ArrayList lastHauf = getLastBWInfo(bwkennung, "hauf");

        boolean aufenthalt;
        Date vonHauf;
        Date bisHauf;
        boolean ausgezogen;
        boolean verstorben;

        if (lastHauf != null) {
            Date lastBis = (Date) lastHauf.get(2);
            aufenthalt = lastBis.getTime() == SYSConst.DATE_BIS_AUF_WEITERES.getTime();
            vonHauf = (Date) lastHauf.get(1);
            bisHauf = lastBis;
            ausgezogen = ((String) lastHauf.get(0)).indexOf("ausgezogen") > -1;
            verstorben = ((String) lastHauf.get(0)).indexOf("verstorben") > -1;
        } else {
            aufenthalt = false;
            vonHauf = null;
            bisHauf = null;
            ausgezogen = false;
            verstorben = false;
        }
        return new Object[]{aufenthalt, vonHauf, bisHauf, ausgezogen, verstorben};
    }

    public static DefaultComboBoxModel ladeKategorien(int katart, boolean mitAllen, boolean mitVerwUndStamm) {
        DefaultComboBoxModel dlm = new DefaultComboBoxModel();

        if (mitAllen) {
            dlm.addElement(new ListElement("Alle Kategorien", BWInfo.ART_ALLES));
        }


        PreparedStatement stmt;
        ResultSet rs;

        try {
            String sql = "SELECT BWIKID, Bezeichnung FROM BWInfoKat ";

            if (katart == BWInfo.ART_PFLEGE_STAMMDATEN) {
                sql += " WHERE KatArt IN (" + BWInfo.ART_PFLEGE + "," + BWInfo.ART_STAMMDATEN + ") ";
            } else if (katart == BWInfo.ART_VERWALTUNG_STAMMDATEN) {
                sql += " WHERE KatArt IN (" + BWInfo.ART_VERWALTUNG + "," + BWInfo.ART_STAMMDATEN + ") ";
            } else if (katart != BWInfo.ART_ALLES) {
                sql += " WHERE KatArt = ? ";
            } // das letzte else kann man sich sparen. Da würde eh nur ein leerer String produziert.
            sql += " ORDER BY Bezeichnung COLLATE latin1_german2_ci";
            stmt = OPDE.getDb().db.prepareStatement(sql);
            if (katart < BWInfo.ART_PFLEGE_STAMMDATEN) {
                stmt.setInt(1, katart);
            }
            rs = stmt.executeQuery();

            while (rs.next()) {
                dlm.addElement(new ListElement(rs.getString("Bezeichnung"), rs.getLong("BWIKID")));
            }
            rs.close();
            stmt.close();
        } // try
        catch (SQLException se) {
            new DlgException(se);
        } // catch

        if (mitVerwUndStamm) {
            dlm.addElement(new ListElement("Verwaltung und Stammdaten", BWInfo.ART_VERWALTUNG_STAMMDATEN));
        }

        return dlm;
    }

    /**
     * Gibt einen String zurück, der eine HTML Darstellung der BWInformationen enthält.
     *
     * @param tmbwi     - TableModel, dass die zu druckenden Daten enthält.
     * @param bwkennung - Bewohner, um dessen Daten es geht
     * @param sel       - int array mit den ausgewählten Zeilen. wenn das NULL ist, dann wird alles gedruckt.
     * @return
     */
    public static String bwInfo2HTML(TMBWInfo tmbwi, Bewohner bewohner, int[] sel) {
        if (sel != null) {
            Arrays.sort(sel);
        }

        String result = "<h1>Bewohner-Informationen</h1>";
        result += "<h2>" + SYSTools.getBWLabel(bewohner) + "</h2>";

        result += "<table border=\"1\" cellspacing=\"0\">";
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT);
        result += "<tr><td valign=\"top\">Gedruckt:</td><td valign=\"top\"><b>" + df.format(SYSCalendar.nowDB()) + " (" + op.tools.DBRetrieve.getUsername(OPDE.getLogin().getUser().getUKennung()) + ")</b></td></tr>";

        if (bewohner.getStation() != null) {
            result += "<tr><td valign=\"top\">BewohnerIn wohnt im</td><td valign=\"top\"><b>" + EinrichtungenTools.getAsText(bewohner.getStation().getEinrichtung()) + "</b></td></tr>";
        }
        result += "</table>";

        int numBwi = tmbwi.getRowCount();
        if (numBwi > 0) {
            for (int v = 0; v < numBwi; v++) {
                // nur drucken, wenn sel = null ist oder wenn v im Array sel vorhanden ist.
                if (sel == null || Arrays.binarySearch(sel, v) > -1) {
                    //OPDE.getLogger().debug(tmbwi.getValueAt(v, TMBWInfo.COL_KATBEZ));
                    if (!tmbwi.getValueAt(v, TMBWInfo.COL_KATBEZ).toString().equalsIgnoreCase("")) {
                        result += "<h2>" + SYSTools.unHTML2(tmbwi.getValueAt(v, TMBWInfo.COL_KATBEZ).toString()) + "</h2>";
                    } else {
                        result += "<br/><br/>";
                    }

                    result += SYSTools.unHTML2(tmbwi.getValueAt(v, TMBWInfo.COL_PRINT).toString());
                }
            }
        }

        String tmp = "<html><head>" +
                "<title>" + SYSTools.getWindowTitle("") + "</title>";
        tmp = tmp + "<script type=\"text/javascript\">" +
                "window.onload = function() {" +
                "window.print();" +
                "}</script></head><body>" + result +
                "<hr/><b>Ende des Berichtes</b><br/>http://www.offene-pflege.de</body></html>";
        return tmp;
    }
}
