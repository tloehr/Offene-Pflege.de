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
package op.share.vorgang;

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
import java.util.ArrayList;
import java.util.Date;

/**
 * @author tloehr
 */
public class TMVorgang extends AbstractTableModel {

    private ResultSet rs;
    public static final int COL_TITEL = 0;
    public static final int COL_VORGANGID = 1;
    public static final int COL_BEENDET = 2;
    public static final int COL_BESITZER = 3;
    public static final int COL_BWKENNUNG = 4;
    public static final int VKAT_ART_ALLGEMEIN = 0;
    public static final int VKAT_ART_PFLEGE = 1;
    public static final int VKAT_ART_BHP = 2;
    public static final int VKAT_ART_SOZIAL = 3;
    public static final int VKAT_ART_VERWALTUNG = 4;
    String bwkennung;
    DateFormat df = DateFormat.getDateInstance();
    private ArrayList cache;

    public TMVorgang(String bwkennung, boolean archiv) {
        this(bwkennung, archiv, 0, null);
    }

//    public TMVorgang(long vorgangid) {
//        this.bwkennung = "";
//        String sql =
//                " SELECT v.VorgangID, v.Titel, v.BWKennung, v.Von, v.WV, v.Bis, v.Ersteller, v.Besitzer, " +
//                " vk.Text, vk.Art, ocu1.Nachname, ocu1.Vorname, ocu2.Nachname, ocu2.Vorname, v.BWKennung " +
//                " FROM Vorgaenge v " +
//                " INNER JOIN OCUsers ocu1 ON ocu1.UKennung = v.Besitzer " +
//                " INNER JOIN OCUsers ocu2 ON ocu2.UKennung = v.Ersteller " +
//                " INNER JOIN VKat vk ON v.VKatID = vk.VKatID " +
//                " WHERE v.VorgangID = ? ";
//
//        try {
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            stmt.setLong(1, vorgangid);
//            rs = stmt.executeQuery();
//
//            initArrayList();
//
//        } catch (SQLException ex) {
//            new DlgException(ex);
//        }
//
//    }

    /**
     * TableModel für die Vorgänge.
     *
     * @param bwkennung. Filter um auf bestimmte BWkennungen einzuschränken. Kann auch "" übergeben werden.
     *                   Dann werden <b>nur</b> die nicht zugeordneten ermittelt. Übergibt man NULL, dann wird alles zurück gegeben.
     * @param archiv.    Wenn TRUE, dann auch die noch abgesetzen.
     * @param wv.        ist die Anzahl Tage, innerhalb derer die Wiedervorlage erreicht werden muss. Oder früher. bei 0, egal.
     * @param besitzer.  filtert auf Eigentümer, in gleicher weise wie bei der BWKennung.
     */
    public TMVorgang(String bwkennung, boolean archiv, int wv, String besitzer) {
        this.bwkennung = bwkennung;
        wv = Math.max(0, wv);
        String sql =
                " SELECT v.VorgangID, v.Titel, v.BWKennung, v.Von, v.WV, v.Bis, v.Ersteller, v.Besitzer, " +
                        " vk.Text, vk.Art, ocu1.Nachname, ocu1.Vorname, ocu2.Nachname, ocu2.Vorname, v.BWKennung " +
                        " FROM Vorgaenge v " +
                        " INNER JOIN OCUsers ocu1 ON ocu1.UKennung = v.Besitzer " +
                        " INNER JOIN OCUsers ocu2 ON ocu2.UKennung = v.Ersteller " +
                        " INNER JOIN VKat vk ON v.VKatID = vk.VKatID ";

        String where = "";
        if (!archiv) {
            where += " AND v.Bis = " + SYSConst.MYSQL_DATETIME_BIS_AUF_WEITERES;
        }

        if (bwkennung != null) {
            where += " AND v.BWKennung = ? ";
            OPDE.getLogger().debug(bwkennung);
        }

        if (besitzer != null) {
            where += " AND v.Besitzer = ? ";
            OPDE.getLogger().debug(besitzer);
        }

        Date dwv = new Date();
        if (wv > 0) {
            where += " AND v.WV < ? ";
            dwv = SYSCalendar.addDate(new Date(), wv);
        }

        if (!where.equals("")) {
            sql += " WHERE " + where.substring(4, where.length());
        }

        sql += " ORDER BY Titel ";
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            int paramindex = 0;

            if (bwkennung != null) {
                paramindex++;
                stmt.setString(paramindex, bwkennung);
            }

            if (besitzer != null) {
                paramindex++;
                stmt.setString(paramindex, besitzer);
            }

            if (wv > 0) {
                paramindex++;
                stmt.setDate(paramindex, new java.sql.Date(dwv.getTime()));
            }

            rs = stmt.executeQuery();
            initArrayList();
        } catch (SQLException ex) {
            new DlgException(ex);
        }

    }

    public int getRowCount() {
        int row = -1;
        try {
            rs.last();
            row = rs.getRow();
        } catch (SQLException se) {
            se.printStackTrace();
        }
        return row;
    }

    private void initArrayList() {
        try {
            rs.last();
            cache = new ArrayList(rs.getRow());
            for (int i = 0; i <= rs.getRow(); i++) {
                cache.add(null);
            }
        } catch (SQLException ex) {
            OPDE.getLogger().error(ex);
        }
    }

    public int getColumnCount() {
        return 1;
    }

    public Class getColumnClass(int c) {
        return String.class;
    }

    public Object getValueAt(int r, int c) {
        Object result = "";
        try {
            rs.absolute(r + 1);
            switch (c) {
                case COL_TITEL: {
                    if (cache.get(rs.getRow()) == null) {
                        String html = "";
                        html += "<h2>" + rs.getString("Titel") + "</h2>";
                        // Es könnte sein, dass die Vorgänge nicht nur für einen bestimmten Bewohner waren.
                        if (SYSTools.catchNull(bwkennung).equals("")) {
                            // Für wen denn ?
                            String currbw = rs.getString("v.BWkennung");
                            if (currbw.equals("")) {
                                html += "<br/>Allgemeiner Vorgang<br/>";
                            } else {
                                html += "<br/>Vorgang gehört zu BewohnerIn: <b>" + SYSTools.getBWLabel(currbw) + "</b><br/>";
                            }
                        }
                        html += "<b>Von:</b> " + df.format(rs.getDate("v.Von"));
                        if (rs.getDate("v.Bis").before(SYSConst.DATE_BIS_AUF_WEITERES_WO_TIME)) {
                            html += "&nbsp;&nbsp;<b>Bis:</b> " + df.format(rs.getDate("v.Bis"));
                        }
                        // Farb-Bestimmung für die Wiedervorlage.
                        if (SYSCalendar.isInFuture(rs.getDate("v.WV").getTime())) {
                            int daysdiff = SYSCalendar.getDaysBetween(SYSCalendar.toGC(SYSCalendar.nowDB()), SYSCalendar.toGC(rs.getDate("v.WV")));
                            if (daysdiff > 7) {
                                html += "<font " + SYSConst.html_darkgreen + ">";
                            } else if (daysdiff == 0) {
                                html += "<font " + SYSConst.html_gold7 + ">";
                            } else {
                                html += "<font " + SYSConst.html_darkorange + ">";
                            }
                        } else {
                            html += "<font " + SYSConst.html_darkred + ">";
                        }
                        html += "&nbsp;&nbsp;<b>Wiedervorlage:</b> ";
                        html += df.format(rs.getDate("v.WV")) + "</font>";
                        html += "<br/><b>Erstellt von:</b> " + SYSTools.anonymizeString(rs.getString("ocu2.Nachname") + ", " + rs.getString("ocu2.Vorname"));
                        html += "&nbsp;&nbsp;<b>Aktueller Besitzer:</b> " + SYSTools.anonymizeString(rs.getString("ocu1.Nachname") + ", " + rs.getString("ocu1.Vorname"));
                        cache.set(rs.getRow(), html);
                    }
                    result = cache.get(rs.getRow()).toString();
                    break;
                }
                case COL_VORGANGID: {
                    result = rs.getLong("VorgangID");
                    break;
                }
                case COL_BEENDET: {
                    result = rs.getTimestamp("v.Bis").getTime() < SYSConst.DATE_BIS_AUF_WEITERES.getTime();
                    break;
                }
                case COL_BESITZER: {
                    result = rs.getString("Besitzer");
                    break;
                }
                case COL_BWKENNUNG: {
                    result = rs.getString("v.BWKennung");
                    break;
                }
                default: {
                    result = "";
                    break;
                }
            }
        } catch (SQLException ex) {
            new DlgException(ex);
        }
        return result;
    }
}
