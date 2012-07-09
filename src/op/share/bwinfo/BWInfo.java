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

import op.OPDE;
import op.tools.DBHandling;
import op.tools.*;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Diese Klasse dient dazu, die Informationen aus der Tabelle BWInfo in den Speicher zu laden und für spätere Zugriffe zu halten.
 * Dazu werden verschiedene Informationen über den Bewohner zur Verfügung gestellt.
 * <ul>
 * <li><i>attribute</i> ist eine ArrayList, die alle Daten der Tabelle "BWInfo" bereit hält. Die Elemente der ArrayList sind HashMaps.
 * Das ist eigentlich die wichtige <i>Errungenschaft</i> dieser Klasse. Denn diese ArrayList wird später auch für das Model der JTable verwendet.</li>
 * <li><i>verstorben</i> boolean, ob der Bewohner verstorben ist oder nicht.
 * </ul>
 *
 * @author tloehr
 */
public class BWInfo {
    //private String bemerkung;

    private ArrayList attribute;
    private boolean aufenthalt;
    private Date vonHauf;
    private Date bisHauf;
    private boolean verstorben;
    private boolean ausgezogen;
    private long HaufBWINFOID;
    public static final int MODE_INTERVAL_BYSECOND = 0;
    public static final int MODE_INTERVAL_BYDAY = 1;
    public static final int MODE_INTERVAL_NOCONSTRAINTS = 2;
    public static final int MODE_INTERVAL_SINGLE_INCIDENTS = 3; // Das sind Ereignisse, bei denen von == bis gilt. Weitere Einschränkungen werden nicht gemacht.
    public static final int ART_PFLEGE = 0;
    public static final int ART_VERWALTUNG = 1;
    public static final int ART_STAMMDATEN = 2;
    public static final int ART_PFLEGE_STAMMDATEN = 100;
    public static final int ART_VERWALTUNG_STAMMDATEN = 101;
    public static final int ART_ALLES = 102;
    private int katnum; // Einfach nur um die Kategorien durch zu nummerieren. Brauche ich für die einfärbung der Zebramuster.
    private long currentkat; // ebenso
    private double scalesum; // Wird nur bei Skalen benutzt. Enthält immer die Gesamtsumme einer Skala.
    private boolean scalemode; // anzeige, ob sich der parser innerhalb einer Scale Umgebung befindet.
    private ArrayList scaleriskmodel;

    /**
     * <p>
     * BWInfo(String, Date) liest die AttributDaten an einem bestimmten Zeitpunkt und für einen bestimmten Bewohner ein.
     * Details siehe BWInfo(String, Date, Date)
     * </p>
     *
     * @param bwkennung Kennung des betreffenden Bewohners
     * @param datum     Datum des Zeitpunkts
     */
    public BWInfo(String bwkennung, Date datum, boolean include_single_incidents) {
        this(bwkennung, datum, ART_ALLES, 0, include_single_incidents);
    }

    /**
     * <p>
     * BWInfo(String) liest die <b>aktuellen</b> AttributDaten für einen bestimmten Bewohner ein.
     * Details siehe BWInfo(String, Date, Date, int)
     * </p>
     *
     * @param bwkennung Kennung des betreffenden Bewohners
     * @param datum     Datum des Zeitpunkts
     */
    public BWInfo(String bwkennung) {
        this(bwkennung, SYSCalendar.nowDBDate(), true);
    }

    /**
     * <p>
     * BWInfo(String, Date) liest die <b>aktuellen</b> AttributDaten für einen bestimmten Bewohner ein.
     * Details siehe BWInfo(String, Date, Date)
     * </p>
     *
     * @param bwkennung Kennung des betreffenden Bewohners
     * @param datum     Datum des Zeitpunkts
     */
    public BWInfo(String bwkennung, int art, boolean include_single_incidents) {
        this(bwkennung, SYSCalendar.nowDBDate(), art, 0, include_single_incidents);
    }

    /**
     * <p>
     * BWInfo() liest die AttributDaten innerhalb eines bestimmten Zeitraums und für einen bestimmten Bewohner ein.
     * Die Hauptaufgabe ist es, die ArrayList attribute zu füllen. Diese Liste besteht aus HashMaps, die zu jeder Zeile der
     * Tabelle "BWInfo" die Inhalte der einzelnen Zellen enthalten.
     * Die Spalte XML wird dazu ebenfalls entschlüsselt. Wie mit der XML Spalte zu verfahren ist, entnimmt die Routine anhand der
     * Struktur XMLs in der korresponierenden Tabelle "BWInfoTyp". Kurz gesagt steht in BWInfoTyp, WIE die Daten zu
     * verstehen sind und in BWInfo stehen die Inhalte.
     * </p>
     * <p>
     * Daten, die in dem bestimmten Zeitraum nicht gelten fehlen in der Liste. Bisher unbeantwortete Attribute stehen nicht
     * in der Liste.
     * </p>
     * <p>
     * Eine Besonderheit stellen die Attribute mit der Kennung "hauf" dar. Sie verwenden die XML Spalte nur in Ausnahmefällen.
     * Durch die Existenz der Datensätze wird der Aufenthalt abgebildet. Die XML Spalte enthält höchstens Angaben darüber
     * warum ein Aufenthalt endete.
     * </p>
     * <p>
     * Dieser Konstruktor geht wie folgt vor:
     * <ol>
     * <li>Zuerst wird ermittelt, ob der BW zum Stichtag bei uns wohnt(e). Wenn ja, dann wird eine HashMap mit den zugehörigen Daten erstellt. </li>
     * <li>Wenn nicht, dann wird geprüft, ob der BW jemals bei uns gewohnt hat. Wenn ja, dann wird diese Information mit aufgenommen. </li>
     * <li>Wenn er noch nie da war, dann wird eben dass in die attribute eingetragen.</li>
     * </ol>
     * </p>
     *
     * @param bwkennung                 Kennung des betreffenden Bewohners
     * @param Datum                     Zeitpunkt, für die gewünschten Daten
     * @param art                       Gibt an, welche Art von Fragen angezeigt werden soll. Welche möglich sind, steht in den ART_ Konstanten dieser Klasse.
     * @param bwikid                    falls man alle Infos einer Klasse haben will. 0 sonst. Ist bwikid != 0, dann gilt das, was über <b>art</b> definiert wurde.
     * @param include_single_incidents, true, wenn auch Einzelereignisse gewünscht sind (von==bis). false sonst.
     */
    public BWInfo(String bwkennung, Date datum, int art, long bwikid, boolean include_single_incidents) {
        attribute = new ArrayList(100);
        katnum = 0;

        try {

            // wenn es EINE Klasse gibt, dann werden weitere Klassenfilter nicht benötigt. ART_ALLES entfernt die WHERE Klausel im SQL String.
            if (bwikid > 0) {
                art = ART_ALLES;
            }

            // Gibt es einen aktuellen oder zurückliegenden Heimaufenthalt.
            ArrayList lastHauf = op.share.bwinfo.DBHandling.getLastBWInfo(bwkennung, "hauf");

            if (lastHauf != null) {
                Date lastBis = (Date) lastHauf.get(2);
                this.aufenthalt = lastBis.getTime() == SYSConst.DATE_BIS_AUF_WEITERES.getTime();
                this.vonHauf = (Date) lastHauf.get(1);
                this.bisHauf = lastBis;
                this.HaufBWINFOID = (Long) lastHauf.get(3);
                this.ausgezogen = ((String) lastHauf.get(0)).indexOf("ausgezogen") > -1;
                this.verstorben = ((String) lastHauf.get(0)).indexOf("verstorben") > -1;
            } else {
                this.aufenthalt = false;
                this.vonHauf = null;
                this.bisHauf = null;
                this.ausgezogen = false;
                this.verstorben = false;
                this.HaufBWINFOID = -1;
            }

            String sql = "" +
                    " SELECT bi.BWINFOID, bi.BWINFTYP, bi.Von, bi.Bis, bi.Bemerkung, bi.AnUKennung, bi.AbUKennung, bi.XML XMLC, bt.XML XMLS, " +
                    " bt.Status, bt.BWInfoKurz, bt.IntervalMode, bk.KatArt, fia.anzahl, bk.Bezeichnung, bk.bwikid " +
                    " FROM BWInfo bi" +
                    " INNER JOIN BWInfoTyp bt ON bi.BWINFTYP = bt.BWINFTYP " +
                    " INNER JOIN BWInfoKat bk ON bt.BWIKID = bk.BWIKID " +
                    // Hier kommen die angehangen Dokumente hinzu
                    " INNER JOIN " +
                    " (" +
                    " 	SELECT DISTINCT f1.BWINFOID, ifnull(anzahl,0) anzahl" +
                    " 	FROM BWInfo f1" +
                    " 	LEFT OUTER JOIN (" +
                    " 		SELECT BWInfoID, count(*) anzahl FROM SYSBWI2FILE" +
                    " 		GROUP BY BWInfoID" +
                    " 		) fa ON fa.BWInfoID = f1.BWINFOID" +
                    " 	WHERE f1.BWKennung=?" +
                    " ) fia ON fia.BWINFOID = bi.BWINFOID " +
                    // Hier die angehangenen Vorgänge
//                    " INNER JOIN " +
//                    " (" +
//                    " 	SELECT DISTINCT f2.BWINFOID, ifnull(anzahl,0) anzahl" +
//                    " 	FROM BWInfo f2" +
//                    " 	LEFT OUTER JOIN (" +
//                    " 		SELECT ForeignKey, count(*) anzahl FROM VorgangAssign" +
//                    " 		WHERE TableName='BWInfo'" +
//                    " 		GROUP BY ForeignKey" +
//                    " 		) va ON va.ForeignKey = f2.BWINFOID" +
//                    " 	WHERE f2.BWKennung=? " +
//                    " ) vrg ON vrg.BWINFOID = bi.BWINFOID " +
                    " WHERE bi.BWKennung=? " +
                    (datum != null ? (include_single_incidents ? " AND ((DATE(bi.von) <= DATE(?) AND DATE(bi.bis) >= DATE(?)) OR (bi.von = bi.bis)) " : " AND (DATE(bi.von) <= DATE(?) AND DATE(bi.bis) >= DATE(?)) ") : "") +
                    (art == ART_ALLES ? "" : (art == ART_PFLEGE_STAMMDATEN ? " AND bk.KatArt IN (" + ART_PFLEGE + "," + ART_STAMMDATEN + ") " : (art == ART_VERWALTUNG_STAMMDATEN ? " AND bk.KatArt IN (" + ART_VERWALTUNG + "," + ART_STAMMDATEN + ") " : " AND bk.KatArt = ? "))) +
                    (bwikid > 0 ? " AND bt.BWIKID = ? " : "") +
                    " ORDER BY bk.Bezeichnung COLLATE latin1_german2_ci, bt.BWInfoKurz COLLATE latin1_german2_ci, bi.Von";
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);

            stmt.setString(1, bwkennung);
            stmt.setString(2, bwkennung);
            //stmt.setString(3, bwkennung);
            int lastParamCount = 2;

            // #0000036: Austausch von sql.Timestamp durch sql.Date
            if (datum != null) {
                lastParamCount++;
                stmt.setTimestamp(lastParamCount, new java.sql.Timestamp(datum.getTime()));
                lastParamCount++;
                stmt.setTimestamp(lastParamCount, new java.sql.Timestamp(datum.getTime()));
            }

            if (art < ART_PFLEGE_STAMMDATEN) { // Also nur Einzel Gruppen. Die Kombinierten beginnen bei 100. :-$
                lastParamCount++;
                stmt.setInt(lastParamCount, art);
            }

            if (bwikid > 0) {
                lastParamCount++;
                stmt.setLong(lastParamCount, bwikid);
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.first()) {
                rs.last();
                //OPDE.debug("BWInfo: Anzahl Datensätze: " + rs.getRow());
                rs.beforeFirst();

                while (rs.next()) {
                    parseXML(rs.getLong("BWINFOID"), rs.getString("BWINFTYP"), rs.getTimestamp("Von"), rs.getTimestamp("Bis"), rs.getString("Bemerkung"),
                            rs.getString("AnUKennung"), rs.getString("AbUKennung"), rs.getString("XMLS"), rs.getString("XMLC"), rs.getString("BWInfoKurz"),
                            rs.getInt("IntervalMode"), rs.getInt("KatArt"), rs.getInt("fia.anzahl"), rs.getString("bk.Bezeichnung"), rs.getLong("bwikid"),
                            0);
                }
            }
        } catch (SQLException ex) {
            new DlgException(ex);
        }
    }

    /**
     * Dieser Konstruktor wird vor allem benutzt um die Grundinformationen für den Tooltip der Bewohner Labels zu setzen.
     *
     * @param bwkennung
     * @param bwinftyp
     * @param datum
     */
    public BWInfo(String bwkennung, String bwinftyp, Date datum) {
        super();
        // #0000036: die folgende Zeile zur Sicherstellung, dass die Anzeige am Tag der Änderung korrekt ist.
        datum.setTime(SYSCalendar.endOfDay(datum));
        attribute = new ArrayList(100);
        katnum = 0;

        try {

            // Gibt es einen aktuellen oder zurückliegenden Heimaufenthalt.
            ArrayList lastHauf = op.share.bwinfo.DBHandling.getLastBWInfo(bwkennung, "hauf");

            if (lastHauf != null) {
                Date lastBis = (Date) lastHauf.get(2);
                this.aufenthalt = lastBis.getTime() == SYSConst.DATE_BIS_AUF_WEITERES.getTime();
                this.vonHauf = (Date) lastHauf.get(1);
                this.bisHauf = lastBis;
                this.HaufBWINFOID = (Long) lastHauf.get(3);
                this.ausgezogen = ((String) lastHauf.get(0)).indexOf("ausgezogen") > -1;
                this.verstorben = ((String) lastHauf.get(0)).indexOf("verstorben") > -1;
            } else {
                this.aufenthalt = false;
                this.vonHauf = null;
                this.bisHauf = null;
                this.ausgezogen = false;
                this.verstorben = false;
                this.HaufBWINFOID = -1;
            }

            String sql = "" +
                    " SELECT bi.BWINFOID, bi.BWINFTYP, bi.Von, bi.Bis, bi.Bemerkung, bi.AnUKennung, bi.AbUKennung, bi.XML XMLC, bt.XML XMLS, " +
                    " bt.Status, bt.BWInfoKurz, bt.IntervalMode, bk.KatArt, bk.Bezeichnung, bk.bwikid " +
                    " FROM BWInfo bi" +
                    " INNER JOIN BWInfoTyp bt ON bi.BWINFTYP = bt.BWINFTYP " +
                    " INNER JOIN BWInfoKat bk ON bt.BWIKID = bk.BWIKID " +
                    " WHERE bi.BWKennung=? AND bi.BWINFTYP = ? " +
                    " AND (bi.von <= ? AND bi.bis >= ?) " +
                    " ORDER BY bk.Bezeichnung COLLATE latin1_german2_ci, bt.BWInfoKurz COLLATE latin1_german2_ci, bi.Von";
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);

            stmt.setString(1, bwkennung);
            stmt.setString(2, bwinftyp);
            stmt.setTimestamp(3, new java.sql.Timestamp(datum.getTime()));
            stmt.setTimestamp(4, new java.sql.Timestamp(datum.getTime()));

            ResultSet rs = stmt.executeQuery();
            if (rs.first()) {
                rs.last();
//                OPDE.debug("BWInfo: Anzahl Datensätze: " + rs.getRow());
                rs.beforeFirst();

                while (rs.next()) {
                    parseXML(rs.getLong("BWINFOID"), rs.getString("BWINFTYP"), rs.getTimestamp("Von"), rs.getTimestamp("Bis"), rs.getString("Bemerkung"),
                            rs.getString("AnUKennung"), rs.getString("AbUKennung"), rs.getString("XMLS"), rs.getString("XMLC"), rs.getString("BWInfoKurz"),
                            rs.getInt("IntervalMode"), rs.getInt("KatArt"), 0, rs.getString("bk.Bezeichnung"), rs.getLong("bwikid"), 0);
                }
            }
        } catch (SQLException ex) {
            new DlgException(ex);
        }
    }

    /**
     * Dieser Konstruktor wird gebraucht um die Daten aus genau einer Information anzuzeigen. Wird bei der Vorgangsverwaltung gebraucht.
     *
     * @param bwinfoid pk der Information, die gewünscht wird.
     */
    public BWInfo(long bwinfoid) {
        super();
        attribute = new ArrayList();
        katnum = 0;

        try {

            // Aktuelle oder zurückliegende Heimaufenthalt interessieren hier nicht.

            this.aufenthalt = false;
            this.vonHauf = null;
            this.bisHauf = null;
            this.ausgezogen = false;
            this.verstorben = false;
            this.HaufBWINFOID = -1;

            String sql = "" +
                    " SELECT bi.BWINFOID, bi.BWINFTYP, bi.Von, bi.Bis, bi.Bemerkung, bi.AnUKennung, bi.AbUKennung, bi.XML XMLC, bt.XML XMLS, " +
                    " bt.Status, bt.BWInfoKurz, bt.IntervalMode, bk.KatArt, bk.Bezeichnung, bk.bwikid " +
                    " FROM BWInfo bi" +
                    " INNER JOIN BWInfoTyp bt ON bi.BWINFTYP = bt.BWINFTYP " +
                    " INNER JOIN BWInfoKat bk ON bt.BWIKID = bk.BWIKID " +
                    " WHERE bi.BWINFOID=? ";
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);

            stmt.setLong(1, bwinfoid);

            ResultSet rs = stmt.executeQuery();
            if (rs.first()) {
                parseXML(rs.getLong("BWINFOID"), rs.getString("BWINFTYP"), rs.getTimestamp("Von"), rs.getTimestamp("Bis"), rs.getString("Bemerkung"),
                        rs.getString("AnUKennung"), rs.getString("AbUKennung"), rs.getString("XMLS"), rs.getString("XMLC"), rs.getString("BWInfoKurz"),
                        rs.getInt("IntervalMode"), rs.getInt("KatArt"), 0, rs.getString("bk.Bezeichnung"), rs.getLong("bwikid"), 0);
            }
        } catch (SQLException ex) {
            new DlgException(ex);
        }
    }

    private String toHTML(DefaultMutableTreeNode struktur, HashMap antwort) {
        String html = "<ul>";
        //showTree(struktur, 0);
        if (!antwort.containsKey("unbeantwortet")) {
            Enumeration en = struktur.children();

            while (en.hasMoreElements()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
                Object[] userObject = (Object[]) node.getUserObject();
                String key = SYSTools.catchNull(userObject[0]);
                String name = SYSTools.catchNull(userObject[1]);
                String label = SYSTools.catchNull(userObject[2]);
                String value = SYSTools.catchNull(antwort.get(name));
//                OPDE.debug("toHTML(): key=" + key + "  name=" + name + "  value=" + value);

                if (node.isLeaf()) {
                    if (key.equalsIgnoreCase("list")) {
                        // Der Klartext muss hier auf andere Weise bestimmt werden, da es sich die Informatioen
                        // in einer anderen Tabelle befinden.
                        // Felderliste zusammenbauen
                        HashMap liststruct = (HashMap) userObject[3]; // nur bei der Liste gibt es ein extra Element im Array
                        ArrayList colstruct = (ArrayList) liststruct.get("col");
                        String[] fields = new String[colstruct.size()];
                        Iterator it = colstruct.iterator();
                        int i = 0;
                        while (it.hasNext()) {
                            HashMap col = (HashMap) it.next();
                            fields[i] = col.get("name").toString();
                            i++;
                        }
                        Object pk = antwort.get(liststruct.get("name"));
                        ResultSet rs = DBHandling.getResultSet(liststruct.get("table").toString(), fields, liststruct.get("pk").toString(), pk, "=");

                        // Jetzt haben wir gleich einen ResultSet mit einem (1) Record. Da stehen unsere Informationen drin.
                        try {
                            // Klartext zusammen basteln.
                            if (rs.first()) {
                                Iterator it1 = colstruct.iterator();
                                it1.next(); // wir überspringein einfach den PK.
                                String str = "";
                                while (it1.hasNext()) {
                                    HashMap col = (HashMap) it1.next();
                                    str += col.get("prefix").toString() + rs.getString(col.get("name").toString());
                                    if (it1.hasNext()) {
                                        str += ", ";
                                    }
                                }
                                //html += "<li>" + str + "</li>";
                                if (html.equals("<ul>")) {
                                    html = "";
                                } else {
                                    html += "</ul>";
                                }
                                html += "<font " + SYSConst.html_darkred + "><b>" + str + "<b></font><br/>";
                                if (en.hasMoreElements()) {
                                    html += "<ul>";
                                }
                            } else {
                                html += "<li><b>keine gültige Zuordnung zu Tabelle: " + liststruct.get("table").toString() + "</b></li>";
                            }
//                            OPDE.debug(html);
                        } catch (SQLException ex) {
                            new DlgException(ex);
                        }

                    } else if (!value.equals("") && !value.equalsIgnoreCase("false")) {
                        if (value.equalsIgnoreCase("true")) {
                            html += "<li><b>" + label + "</b></li>";
                        } else {
                            if (!value.equalsIgnoreCase("tnz")) {
                                if (!name.equalsIgnoreCase("hauf") && (key.equalsIgnoreCase("optiongroup") || key.equalsIgnoreCase("scalegroup") || key.equalsIgnoreCase("combobox"))) {
//                                    OPDE.debug("toHTML().key = " + key);
                                    DefaultMutableTreeNode thisNode = findNameInTree(struktur, value);
                                    String text = SYSTools.catchNull(((Object[]) thisNode.getUserObject())[2]);
                                    if (key.equalsIgnoreCase("scalegroup")) {
                                        Double score = (Double) ((Object[]) thisNode.getUserObject())[3];
                                        html += "<li><b>" + label + ":</b> " + text + " (Risikowert: " + score + ")</li>";
                                        scalesum += score;
                                    } else {
                                        html += "<li><b>" + label + ":</b> " + text + "</li>";
                                    }
                                } else {
                                    if (name.equalsIgnoreCase("java")) {
                                        html += value;
                                    } else {
                                        html += "<li><b>" + label + ":</b> " + value + "</li>";
                                    }
                                }
                            }
                        }
                    } //else if (key.equalsIgnoreCase("label")) {
                    // html += "<h3>" + label + "</h3>";
                    //}
                } else { // TABGROUPS, weil is kein Blatt
                    // nur anzeigen, wenn es mindestens eine angekreuzte Checkbox in dieser TABGROUP gibt.
                    if (treeHasTrueCheckboxes(node, antwort)) {
//                        // Fals von der üblichen Zeichengröße abgewichen wird, rechne ich zwei Punkte noch runter, damit
//                        // es besser zur Bildschirmdarstellung in den HTML Listen passt.
//                        int size = Integer.parseInt(SYSTools.catchNull(userObject[3], "12"));
//                        size -= 4;
//                        // Hier werden die TabGroups erzeugt, in dem die toHTML rekursiv aufgerufen wird.
//                        String f1 = "<font size=\""+size+"\">";
//                        String f2 = "</font>";

                        html += "<li><u>" + label + "</u>" + toHTML(node, antwort) + "</li>";
                    }
                }

            } // while
            html += "</ul>";
        } else {
            html = "<font color=\"red\"><i>bisher unbeantwortet</i></font><br/>";
        }

        if (scalemode) {
            // nun noch die Einschätzung des Risikos
            // Bezeichnung und Farbe
            Iterator it = scaleriskmodel.iterator();
            boolean found = false;
            String risiko = "unbekanntes Risiko";
            String color = "black";
            while (!found && it.hasNext()) {
                Object[] o = (Object[]) it.next();
                double from = (Double) o[0];
                double to = (Double) o[1];
                if (from <= scalesum && scalesum <= to) {
                    found = true;
                    color = o[3].toString();
                    risiko = o[2].toString();
                }
            }
            html += "<b><font color=\"" + color + "\">Risiko-Einschätzung: " + scalesum + " (" + risiko + ")</font></b><br/>";
        }
//        OPDE.debug(html);
        return html;

    }

    private boolean treeHasTrueCheckboxes(DefaultMutableTreeNode tree, HashMap antwort) {
        Enumeration en = tree.children();
        boolean found = false;

        while (!found & en.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
            Object[] userObject = (Object[]) node.getUserObject();
            String key = SYSTools.catchNull(userObject[0]);
            String name = SYSTools.catchNull(userObject[1]);
            String label = SYSTools.catchNull(userObject[2]);
            String value = SYSTools.catchNull(antwort.get(name));

            if (node.isLeaf()) {
                found = key.equalsIgnoreCase("checkbox") && value.equalsIgnoreCase("true");
            } else {
                found = treeHasTrueCheckboxes(node, antwort);
            }

        }
        return found;
    }

    private void parseXML(long bwinfoid, String bwinftyp, Date von, Date bis, String bemerkung, String anukennung, String abukennung,
                          String xmls, String xmlc, String bwinfokurz, int intervalmode, int katart, int dokanzahl, String katbez,
                          long bwikid, int vrganzahl) {
        try {
            scalemode = false;
            scaleriskmodel = null;
            if (bwikid != currentkat) {
                currentkat = bwikid;
                katnum++;
            }
            String html = "";
            HashMap hm = new HashMap();
//            OPDE.debug("BWInfTyp: " + bwinftyp + "  BWInfoID:" + bwinfoid);
            // Erst Struktur...
            String texts = "<?xml version=\"1.0\"?><xml>" + xmls + "</xml>";
            XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
            InputSource is = new org.xml.sax.InputSource(new java.io.BufferedReader(new java.io.StringReader(texts)));
            HandlerStruktur s = new HandlerStruktur();
            parser.setContentHandler(s);
            parser.parse(is);
            DefaultMutableTreeNode struktur = s.getStruktur();


            // ...dann Inhalt
            String textc = "<?xml version=\"1.0\"?><xml>" + xmlc + "</xml>";
            InputSource ic = new org.xml.sax.InputSource(new java.io.BufferedReader(new java.io.StringReader(textc)));
            HandlerInhalt c = new HandlerInhalt(struktur);
            parser.setContentHandler(c);
            parser.parse(ic);

            html = toHTML(struktur, c.getAntwort());

            // Bemerkung dabei, wenn nicht leer
            if (bemerkung != null && !bemerkung.equals("")) {
                html += "<b><u>Bemerkung:</u></b><br/>" + bemerkung + "<br/>";
            }

            hm.put("xmlc", xmlc);
            hm.put("html", html);
            hm.put("bwinftyp", bwinftyp);
            hm.put("bemerkung", bemerkung);
            hm.put("xmls", xmls);
            hm.put("bwinfoid", new Long(bwinfoid));
            hm.put("bwinfokurz", bwinfokurz);
            hm.put("anukennung", anukennung);
            hm.put("abukennung", abukennung);
            hm.put("von", von);
            hm.put("bis", bis);
            hm.put("intervalmode", intervalmode);
            hm.put("katart", katart);
            hm.put("katbez", katbez);
            hm.put("dokanzahl", dokanzahl);
            hm.put("katindex", katnum);
            hm.put("vrganzahl", vrganzahl);
            hm.put("antwort", c.getAntwort());

            if (c.getAntwort().containsKey("unbeantwortet")) {
                hm.put("unbeantwortet", "true");
            }

            //showTree(struktur,0);
            DefaultMutableTreeNode java = findNameInTree(struktur, "java");
            if (java != null) {
                String classname = (((Object[]) java.getUserObject())[2]).toString();
                hm.put("java", classname);
            }

            attribute.add(hm);
        } catch (IOException ex) {
            new DlgException(ex);
            System.exit(1);
        } catch (SAXException ex) {
            new DlgException(ex);
            System.exit(1);
        }
    }

    private class HandlerInhalt extends DefaultHandler {

        private HashMap antwort = new HashMap();
        private DefaultMutableTreeNode struktur;
        // private String html;

        HandlerInhalt(DefaultMutableTreeNode struktur) {
            this.struktur = struktur;
            //System.out.println("struktur: "+struktur);
            //antwort.put("xml", xml);
        }

        public void startElement(String nsURI, String strippedName, String tagName, Attributes attributes) throws SAXException {
            if (!tagName.equalsIgnoreCase("xml")) {
//                OPDE.debug(this.toString() + ":" + tagName);
                if (tagName.equalsIgnoreCase("java")) { // eine Java Klasse sorgt selbst für ihre Darstellung. Da gibts hier nicht viel zu tun.
                    String atr = attributes.getValue("html");
                    atr = atr.replaceAll("&lt;", "<");
                    atr = atr.replaceAll("&gt;", ">");
                    antwort.put(tagName, atr); // Hier steht schon HTML drin.
                } else if (tagName.equalsIgnoreCase("unbeantwortet")) {
                    antwort.put("unbeantwortet", "true");
                } else {
                    DefaultMutableTreeNode node = findNameInTree(struktur, tagName);
                    if (node != null) {
                        String value = (String) attributes.getValue("value");
                        if (value == null) {
                            value = "";
                        }
                        if (((Object[]) node.getUserObject())[0].toString().equalsIgnoreCase("option")) {
                            antwort.put(tagName, ((Object[]) node.getUserObject())[1]);
                        } else {
                            antwort.put(tagName, value);
                        }

                    }
                }
            }

        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
        }

        public void endDocument() {
            //html += "</ul>";
        }

        public HashMap getAntwort() {
            return antwort;
        }
    } // private class HandlerFragenInhalt

    private class HandlerStruktur extends DefaultHandler {

        private HashMap listStruct = null;
        private ArrayList colStruct = null;
        private DefaultMutableTreeNode struktur;
        private DefaultMutableTreeNode tabgroup;

        public void startDocument() throws SAXException {
            struktur = new DefaultMutableTreeNode(new Object[]{"root", "", ""});
        }

        public void startElement(String nsURI, String strippedName, String tagName, Attributes attributes) throws SAXException {
            // Baut eine Liste mit names und labels auf.
            String name = attributes.getValue("name");
            String label = attributes.getValue("label");
//            OPDE.debug("STARTELEMENT: " + this.toString() + ": " + tagName + "    name: " + name);

            if (!tagName.equalsIgnoreCase("java")) {
                if (tagName.equalsIgnoreCase("list")) {
                    listStruct = new HashMap();
                    colStruct = new ArrayList();
                    listStruct.put("name", name);
                    listStruct.put("label", label);
                    listStruct.put("fk", attributes.getValue("fk"));
                    listStruct.put("pk", attributes.getValue("pk"));
                    listStruct.put("table", attributes.getValue("table"));
                } else {
                    if (listStruct != null) { // wir müssen uns innerhalb einer List Struktur befinden.
                        if (tagName.equalsIgnoreCase("col")) {
                            HashMap hm = new HashMap();
                            hm.put("name", attributes.getValue("name"));
                            String prefix = attributes.getValue("prefix");
                            if (prefix == null) {
                                prefix = "";
                            }
                            hm.put("prefix", prefix);
                            colStruct.add(hm);
                        }
                    } else { // keine LIST Struktur
                        if (name != null) {
                            if (tagName.equalsIgnoreCase("scale")) {
                                scalemode = true;
                                scalesum = 0d;
                                scaleriskmodel = new ArrayList();
                            } else if (tagName.equalsIgnoreCase("tabgroup")) {
//                                OPDE.debug("TabGroup STARTS: " + name);
                                tabgroup = new DefaultMutableTreeNode(new Object[]{"tabgroup", name, label, SYSTools.catchNull(attributes.getValue("size"))});
                            } else {
                                if (tabgroup != null) {
//                                    OPDE.debug("Füge zur Tabgroup hinzu:" + ((Object[]) tabgroup.getUserObject())[1].toString() + "=>> " + tagName + "    " + name);
                                    tabgroup.add(new DefaultMutableTreeNode(new Object[]{tagName, name, label}));
                                } else {
//                                    OPDE.debug("Füge zur STRUKTUR hinzu: =>> " + tagName + "    " + name);
                                    if (scalemode && tagName.equalsIgnoreCase("option")) {
                                        double score = Double.parseDouble(attributes.getValue("score"));
                                        struktur.add(new DefaultMutableTreeNode(new Object[]{tagName, name, label, score}));
                                    } else {
                                        struktur.add(new DefaultMutableTreeNode(new Object[]{tagName, name, label}));
                                    }
                                }
                            }
                        } else if (tagName.equalsIgnoreCase("risk")) {
                            // from, to, label, color
                            Object[] o = new Object[]{Double.parseDouble(attributes.getValue("from")), Double.parseDouble(attributes.getValue("to")), attributes.getValue("label"), attributes.getValue("color")};
                            scaleriskmodel.add(o);
                        }

                    }
                }
            } else {
                struktur.add(new DefaultMutableTreeNode(new Object[]{attributes.getValue("label").toString(), "java", attributes.getValue("classname").toString()}));
            }

        }

        public DefaultMutableTreeNode getStruktur() {
            return struktur;
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (localName.equalsIgnoreCase("tabgroup")) {
                //OPDE.debug("TabGroup ends");
                struktur.add(tabgroup);
                tabgroup = null;
            }
            if (localName.equalsIgnoreCase("col")) {
                listStruct.put("col", colStruct.clone());
            }
            if (localName.equalsIgnoreCase("list")) {
                struktur.add(new DefaultMutableTreeNode(new Object[]{"list", listStruct.get("name").toString(), "", listStruct.clone()}));
                colStruct = null;
                listStruct = null;
            }

        }
    } // private class HandlerFragenStruktur

    public ArrayList getAttribute() {
        return attribute;
    }

    public boolean isAufenthalt() {
        return aufenthalt;
    }

    public Date getVonHauf() {
        return vonHauf;
    }

    public Date getBisHauf() {
        return bisHauf;
    }

    public boolean isVerstorben() {
        return verstorben;
    }

    public boolean isAusgezogen() {
        return ausgezogen;
    }

    public long getHaufBWINFOID() {
        return HaufBWINFOID;
    }

    public void cleanup() {
        attribute.clear();
    }

    public DefaultMutableTreeNode findNameInTree(DefaultMutableTreeNode nodeintree, String name) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) nodeintree.getRoot();
        Enumeration en = root.breadthFirstEnumeration();
        boolean found = false;
        DefaultMutableTreeNode result = null;
        while (!found && en.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
            //OPDE.debug("findNameInTree/2: gesucht: " + name + "   gefunden: " + ((Object[]) node.getUserObject())[1].toString());
            found = ((Object[]) node.getUserObject())[1].toString().equalsIgnoreCase(name);
            if (found) {
                //OPDE.debug("TREFFER!");
                result = node;
            }

        }
        return result;
    }//    public void showTree(DefaultMutableTreeNode root, int depth) {
//        Enumeration en = root.children();
//        String indent = "";
//        depth++;
//        for (int i = 0; i <= depth; i++) {
//            indent += "  ";
//        }
//        while (en.hasMoreElements()) {
//            DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
//            String name = ((Object[]) node.getUserObject())[1].toString();
//            String label = SYSTools.catchNull(((Object[]) node.getUserObject())[2]);
//            if (node.isLeaf()) {
//                System.out.println(indent + "-" + name + " " + label);
//            } else {
//                System.out.println(indent + "+" + name + " " + label);
//                showTree(node, depth);
//            }
//        }
//    }
}
