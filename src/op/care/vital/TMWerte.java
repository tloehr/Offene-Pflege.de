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
import op.tools.DlgException;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.swing.table.AbstractTableModel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Diese Klasse ist das TableModel für die Vitalwerte
 */
public class TMWerte
        extends AbstractTableModel {

    static final long serialVersionUID = 1;
    PreparedStatement stmt;
    private ArrayList content;
    public static final int COL_PIT = 0;
    public static final int COL_BWKENNUNG = 1;
    public static final int COL_ART = 2;
    public static final int COL_WERT = 3;
    public static final int COL_UKENNUNG = 4;
    public static final int COL_BEMERKUNG = 5;
    public static final int COL_CDATE = 6;
    public static final int COL_BWID = 7;
    public static final int COL_XML = 8;
    public static final int COL_DBLWERT = 9;
    public static final int COL_V1 = 9;
    public static final int COL_V2 = 10;
    public static final int COL_V3 = 11;
    public static final int COL_PK1 = 12;
    public static final int COL_PK2 = 13;
    public static final int COL_PK3 = 14;
    public static final int COL_VORNAME = 15;
    public static final int COL_REPLACEDBY = 16;
    public static final int COL_REPLACEMENTFOR = 17;
    public static final int COL_EDITBY = 18;
    public static final int COL_VORGANG_ANZAHL = 19;
    public static final int COL_DATEIEN_ANZAHL = 20;
    public static final int COL_EINHEIT = 21;
    public static final int COL_MDATE = 22;
    public static final int COL_NACHNAME = 23;
    public static final int TBL_PIT = 0;
    public static final int TBL_HTML = 1;
    public static final int TBL_OBJECT = 10;
    boolean showids = false;
    boolean showedits = false;
    SimpleDateFormat sdf;

    /**
     *
     *
     *
     *
     *
     */
    TMWerte(Date from, Date to, boolean[] filter, String currentBW, boolean showedits, boolean showids) {
        super();
        sdf = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");
        this.showids = showids;
        this.showedits = showedits;
        try {
            //stmt = OPDE.db.db.createStatement();
            String s =
                    " SELECT bw.BWID, bw.PIT, bw.UKennung, bw.Wert, bw.XML, bw.Bemerkung, bw.Beziehung, bw.Sortierung, bw._cdate, bw._mdate, bw.ReplacedBy, bw.ReplacementFor," +
                            "       bw.EditBy, fia.anzahl, vrg.anzahl, ocu.Vorname, ocu.Nachname " +
                            " FROM BWerte bw" +
                            " INNER JOIN OCUsers ocu ON ocu.UKennung = bw.UKennung " +
                            // Hier kommen die angehangen Dokumente hinzu
                            " INNER JOIN " +
                            " (" +
                            " 	SELECT DISTINCT f1.BWID, ifnull(anzahl,0) anzahl" +
                            " 	FROM BWerte f1" +
                            " 	LEFT OUTER JOIN (" +
                            " 		SELECT BWID, count(*) anzahl FROM SYSBWERTE2FILE" +
                            " 		GROUP BY BWID" +
                            " 		) fa ON fa.BWID = f1.BWID" +
                            " 	WHERE f1.BWKennung=? AND f1.PIT >= ? AND f1.PIT <= ? " +
                            " ) fia ON fia.BWID = bw.BWID" +
                            // Hier die angehangenen Vorgänge
                            " INNER JOIN " +
                            " (" +
                            " 	SELECT DISTINCT f2.BWID, ifnull(anzahl,0) anzahl" +
                            " 	FROM BWerte f2" +
                            " 	LEFT OUTER JOIN (" +
                            " 		SELECT ForeignKey, count(*) anzahl FROM VorgangAssign" +
                            " 		WHERE TableName='BWerte'" +
                            " 		GROUP BY ForeignKey" +
                            " 		) va ON va.ForeignKey = f2.BWID" +
                            " 	WHERE f2.BWKennung=? AND f2.PIT >= ? AND f2.PIT <= ? " +
                            " ) vrg ON vrg.BWID = bw.BWID " +
                            " WHERE bw.BWKennung = ? AND bw.PIT >= ? AND bw.PIT <= ? " +
                            (showedits ? "" : " AND bw.ReplacedBy = 0 ");
            //"ORDER BY Beziehung, Datum, Uhrzeit";

            // alle ist nur dann false, wenn keines der Häkchen gesetzt ist. Dann soll er AUCH alle anzeigen
            boolean alle = false;
            for (int i = 0; i < filter.length; i++) {
                alle = alle | filter[i];
            }
            alle = !alle;

            String f = "";
            if (!alle) {
                f += "AND (";
                // Beziehung > 0 können nur Blutdruckwerte sein.
                f += (filter[PnlVitalwerte.RR] ? "Beziehung > 0 OR " : "") +
                        // Nur Pulswerte, die nicht zu einer Blutdruck 3er Beziehung gehören.
                        (filter[PnlVitalwerte.PULS] ? "(XML like '%<PULS/>%' AND Beziehung = 0) OR " : "") +
                        (filter[PnlVitalwerte.BZ] ? "XML like '%<BZ/>%' OR " : "") +
                        (filter[PnlVitalwerte.TEMP] ? "XML like '%<TEMP/>%' OR " : "") +
                        (filter[PnlVitalwerte.GEWICHT] ? "XML like '%<GEWICHT/>%' OR " : "") +
                        (filter[PnlVitalwerte.BRADEN] ? "XML like '%<braden%' OR " : "") +
                        (filter[PnlVitalwerte.GROESSE] ? "XML like '%<GROESSE/>%' OR " : "") +
                        (filter[PnlVitalwerte.BILANZ] ? "XML like '%<BILANZ/>%' OR " : "") +
                        (filter[PnlVitalwerte.QUICK] ? "XML like '%<QUICK/>%' OR " : "") +
                        (filter[PnlVitalwerte.STUHLGANG] ? "XML like '%<STUHLGANG/>%' OR " : "") +
                        (filter[PnlVitalwerte.ERBRECHEN] ? "XML like '%<ERBRECHEN/>%' OR " : "") +
                        (filter[PnlVitalwerte.ATEM] ? "XML like '%<ATEM/>%' OR " : "");
                f = f.substring(0, f.length() - 4); // Hier wird das letzt " OR " abgeschnitten.
                f += ") ";
            }
            // Hier wird der ursprüngliche SQL Ausdruck mit einem evtl. Filter und der Sortierung zusammengefasst.
            s += f + " ORDER BY PIT desc, Beziehung, Sortierung ";
            stmt = OPDE.getDb().db.prepareStatement(s);

            // Dieses doppelt und dreifach übergeben der Zeiträume an den
            // SQL Ausdruck führt dazu, dass die Ausführung der Abfrage beschleunigt
            // wird. Nämlich dadurch das der Suchraum frühzeitig eingeschränkt wird
            // und nicht erst beim JOIN.
            stmt.setString(1, currentBW);
            stmt.setTimestamp(2, new java.sql.Timestamp(SYSCalendar.startOfDay(from)));
            stmt.setTimestamp(3, new java.sql.Timestamp(SYSCalendar.endOfDay(to)));
            stmt.setString(4, currentBW);
            stmt.setTimestamp(5, new java.sql.Timestamp(SYSCalendar.startOfDay(from)));
            stmt.setTimestamp(6, new java.sql.Timestamp(SYSCalendar.endOfDay(to)));
            stmt.setString(7, currentBW);
            stmt.setTimestamp(8, new java.sql.Timestamp(SYSCalendar.startOfDay(from)));
            stmt.setTimestamp(9, new java.sql.Timestamp(SYSCalendar.endOfDay(to)));

            ResultSet rs = stmt.executeQuery();

            if (rs.last()) {
                content = new ArrayList(rs.getRow());
            } else {
                content = new ArrayList();
            }

            if (rs.first()) {
                rs.beforeFirst();
                while (rs.next()) {
                    Object[] o;
                    /*
                     * Diese Unterscheidung ist nötig, da es Werte wie den Blutdruck gibt, wo drei Datenbank
                     * Zeilen zu einer Tabellenzeile zusammengefasst werden müssen.
                     */
                    if (rs.getInt("Beziehung") > 0) { // Hier ist der Start einer RRSYS, RRDIA, PULS Beziehung
                        OPDE.info("Beziehung:" + rs.getInt("Beziehung"));
                        // Durch die Sortierung des ResultSets nach Beziehung und dann erst nach Datum und Uhrzeit
                        // ist sichergestellt, dass die RR Werte immer in der richtigen Reihenfolge zusammen stehen.
                        int sys = (int) rs.getDouble("Wert");
                        long sysbwid = rs.getLong("BWID");
                        OPDE.info("sysbwid:" + sysbwid);
                        rs.next();
                        int dia = (int) rs.getDouble("Wert");
                        long diabwid = rs.getLong("BWID");
                        OPDE.info("diabwid:" + diabwid);
                        rs.next();
                        int puls = (int) rs.getDouble("Wert");
                        long pulsbwid = rs.getLong("BWID");
                        OPDE.info("pulsbwid:" + pulsbwid);
                        String rrline = sys + "/" + dia + " P" + puls;
                        // Die nachfolgenden Abfragen sind sowieso bei allen 3 Zeilen gleich, deswegen kann man
                        // auch ruhig die Datum, etc. Werte aus der letzten Zeile des RR 3er Gespanns nehmen.
                        o = new Object[]{
                                rs.getTimestamp("PIT"), currentBW, "Blutdruck und Puls",
                                rrline, rs.getString("UKennung"), rs.getString("Bemerkung"), rs.getTimestamp("_cdate"),
                                rs.getLong("BWID"), "<RR/>", sys, dia, puls, sysbwid, diabwid, pulsbwid, rs.getString("ocu.Vorname"), rs.getLong("ReplacedBy"),
                                rs.getLong("ReplacementFor"), rs.getString("EditBy"), rs.getInt("vrg.anzahl"), rs.getInt("fia.anzahl"), "mmHg, S/m",
                                rs.getTimestamp("_mdate"), rs.getString("ocu.Nachname")
                        };
                    } else {
                        o = new Object[]{
                                rs.getTimestamp("PIT"), currentBW, DBHandling.getBWertArt(rs.getString("XML")),
                                (rs.getDouble("Wert") == 0d ? "siehe Bemerkung" : new Double(rs.getDouble("Wert")).toString()),
                                rs.getString("UKennung"), rs.getString("Bemerkung"), rs.getTimestamp("_cdate"),
                                rs.getLong("BWID"), rs.getString("XML"), rs.getDouble("Wert"), 0d, 0d, 0L, 0L, 0L, rs.getString("ocu.Vorname"), rs.getLong("ReplacedBy"),
                                rs.getLong("ReplacementFor"), rs.getString("EditBy"), rs.getInt("vrg.anzahl"), rs.getInt("fia.anzahl"), DBHandling.getBWertEinheit(rs.getString("XML")),
                                rs.getTimestamp("_mdate"), rs.getString("ocu.Nachname")
                        };

                    }
                    content.add(o);
                }
            }
            rs.close();
            stmt.close();
        } // try
        catch (SQLException se) {
            new DlgException(se);
        } // catch
    }

    public int getRowCount() {
        return content.size();
    }

    public int getColumnCount() {
        return 2;
    }

    public Class getColumnClass(int c) {
        if (c <= COL_BEMERKUNG) {
            return String.class;
        } else if (c == COL_CDATE) {
            return java.sql.Timestamp.class;
        } else if (c == COL_DBLWERT) {
            return java.lang.Double.class;
        } else if (c == COL_BWID) {
            return Long.class;
        } else {
            return String.class;
        }
    }

    public void cleanup() {
        content.clear();
    }

    public Object getValueAt(int r, int c) {
        String result = "";

        Object[] o = (Object[]) content.get(r);

        long replacedby = (Long) o[COL_REPLACEDBY];
        long replacement4 = (Long) o[COL_REPLACEMENTFOR];
        int fianzahl = (Integer) o[COL_DATEIEN_ANZAHL];
        int vrganzahl = (Integer) o[COL_VORGANG_ANZAHL];
        long bwid = (Long) o[COL_BWID];


        String color = "";

        if (replacedby == 0) {
            color = SYSCalendar.getHTMLColor4Schicht(SYSCalendar.ermittleSchicht(((Date) o[COL_PIT]).getTime()));
        } else {
            color = SYSConst.html_lightslategrey;
        }

        String fonthead = "<font " + color + ">";

        switch (c) {
            case TBL_PIT: { // COL_DATUM
                //GregorianCalendar gc = SYSCalendar.addTime2Date(SYSCalendar.toGC((Date) o[COL_DATUM]), )
                result = sdf.format((Date) o[COL_PIT]) + "; " + SYSTools.anonymizeUser(o[COL_NACHNAME].toString(), o[COL_VORNAME].toString());
                if (showids) {
                    result += "<br/><i>(" + bwid + ")</i>";
                }
                result = fonthead + result + "</font>";

                break;
            }
            case TBL_HTML: {
                DateFormat df = DateFormat.getDateTimeInstance();
                if (replacedby == bwid) {
                    result += "<i>Gelöschter Eintrag. Gelöscht am/um: " + df.format((Date) o[COL_MDATE]) + " von " + o[COL_EDITBY].toString() + "</i><br/>";
                }
                if (replacement4 > 0 && replacement4 != bwid) {
                    result += "<i>Dies ist ein Eintrag, der nachbearbeitet wurde.<br/>Am/um: " + df.format((Date) o[COL_CDATE]) + "<br/>Der Originaleintrag hatte die Nummer: " + replacement4 + "</i><br/>";
                }
                if (replacedby > 0 && replacedby != bwid) {
                    result += "<i>Dies ist ein Eintrag, der durch eine Nachbearbeitung ungültig wurde. Bitte ignorieren.<br/>Änderung wurde am/um: " + df.format((Date) o[COL_CDATE]) + " von " + o[COL_EDITBY].toString() + " vorgenommen.";
                    result += "<br/>Der Korrektureintrag hat die Nummer: " + replacedby + "</i><br/>";
                }
                result += "<b>" + o[COL_WERT].toString() + " " + o[COL_EINHEIT].toString() + "</b> (" + o[COL_ART].toString() + ")";
                String bemerkung = SYSTools.catchNull(o[COL_BEMERKUNG]);
                if (!bemerkung.equals("")) {
                    result += "<br/><b>Bemerkung:</b> " + bemerkung;
                }
                if (fianzahl > 0) {
                    //URL url = this.getClass().getResource("/artwork/16x16/attach.png");
                    //System.out.println(url.getPath());
                    result += "<font color=\"green\">&#9679;</font>";
                }
                if (vrganzahl > 0) {
                    //URL url = this.getClass().getResource("/artwork/16x16/mail-tagged.png");
                    //System.out.println(url.getPath());
                    result += "<font color=\"red\">&#9679;</font>";
                }
                result = fonthead + result + "</font>";

                break;
            }
            case TBL_OBJECT: {
                return o;
            }
            default: {
                result = null;
                break;
            }
        }
        return result;
    }
}

