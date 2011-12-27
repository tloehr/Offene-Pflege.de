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
import op.share.bwinfo.BWInfo;
import op.tools.DBRetrieve;
import op.tools.DlgException;

import javax.swing.table.AbstractTableModel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author tloehr
 */
@Deprecated
public class TMElement extends AbstractTableModel {

    private ResultSet rs;
    private boolean mitBWKennung = false; // Soll bei den Tagesberichten jedesmal der BWLabel dabeigeschrieben werden ?
    public static final int COL_CONTENT = 1;
    public static final int COL_PIT = 0;
    public static final int COL_PK = 2;
    public static final int COL_SYSBERICHT = 3;
    public static final int COL_ELEMENTID = 4;
    public static final int COL_TBLIDX = 5;
    public static final int TBL_TAGESBERICHTE = 0;
    public static final int TBL_PLANUNG = 1;
    public static final int TBL_BHPVERORDNUNG = 2;
    public static final int TBL_BWERTE = 3;
    public static final int TBL_BWINFO = 4;
    public static final int TBL_VBERICHT = 5;
    private ArrayList cache;
    //    private DateFormat tf = DateFormat.getTimeInstance(DateFormat.SHORT);
    private SimpleDateFormat df;

    // Enthält den Tabellennamen. Dieses Array passt zum Wert in der gleichnamigen Spalte
    //public static final String[] tblidx = new String[]{"Tagesberichte", "Planung", "BHPVerordnung", "BWerte", "BWInfo"};
    public TMElement(long vorgangid, boolean system) {
        try {

            df = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");

            String sql1 =
                    " SELECT * FROM " +
                            " (" +
                            "   SELECT 0 tblidx, tbid pk, pit, t.UKennung ukennung, t.BWKennung, t.EKennung, t.Text, 0 Art, v.VAID " +
                            "   FROM VorgangAssign v " +
                            "   INNER JOIN Tagesberichte t ON t.tbid = v.ForeignKey " +
                            "   WHERE v.TableName='Tagesberichte' AND v.VorgangID = ? " +
                            " UNION" +
                            "   SELECT 1 tblidx, p.PlanID pk, p.Von pit, p.AnUkennung ukennung, p.BWKennung, '', '', 0, v.VAID" +
                            "   FROM VorgangAssign v " +
                            "   INNER JOIN Planung p ON p.PlanID = v.ForeignKey " +
                            "   WHERE v.TableName='Planung' AND v.VorgangID = ? " +
                            " UNION " +
                            "   SELECT 2 tblidx, b.VerID pk, b.AnDatum pit, b.AnUkennung ukennung, b.BWKennung, '', '', 0, v.VAID" +
                            "   FROM VorgangAssign v " +
                            "   INNER JOIN BHPVerordnung b ON b.VerID = v.ForeignKey " +
                            "   WHERE v.TableName='BHPVerordnung' AND v.VorgangID = ? " +
                            " UNION " +
                            "   SELECT 3 tblidx, b.BWID pk, pit, b.Ukennung ukennung, b.BWKennung, '', '', 0, v.VAID " +
                            "   FROM VorgangAssign v " +
                            "   INNER JOIN BWerte b ON b.BWID = v.ForeignKey " +
                            "   WHERE v.TableName='BWerte' AND v.VorgangID = ? " +
                            " UNION " +
                            "   SELECT 4 tblidx, b.BWInfoID pk, b.Von, b.AnUkennung ukennung, b.BWKennung, '', '', 0, v.VAID " +
                            "   FROM VorgangAssign v " +
                            "   INNER JOIN BWInfo b ON b.BWInfoID = v.ForeignKey " +
                            "   WHERE v.TableName='BWInfo' AND v.VorgangID = ? " +
                            " UNION " +
                            "   SELECT 5 tblidx, vb.VBID pk, vb.Datum, vb.UKennung ukennung, '', '', '', Art, vb.VBID VAID" +
                            "   FROM VBericht vb " +
                            "   WHERE vb.VorgangID = ? " +
                            (system ? "" : "AND Art = 0 ") +
                            " ) as va " +
                            "   ORDER BY pit ";
            PreparedStatement stmt1 = OPDE.getDb().db.prepareStatement(sql1);
            stmt1.setLong(1, vorgangid);
            stmt1.setLong(2, vorgangid);
            stmt1.setLong(3, vorgangid);
            stmt1.setLong(4, vorgangid);
            stmt1.setLong(5, vorgangid);
            stmt1.setLong(6, vorgangid);
            rs = stmt1.executeQuery();

            rs.last();
            cache = new ArrayList(rs.getRow());
            for (int i = 0; i <= rs.getRow(); i++) {
                cache.add(null);
            }

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

    public int getColumnCount() {
        return 2;
    }

    public Class getColumnClass(int c) {
        return String.class;
    }

    private String getBWInfo(long bwinfoid) {
        BWInfo bwinfo = new BWInfo(bwinfoid);
        ArrayList content = bwinfo.getAttribute();
        HashMap attrib = (HashMap) content.get(0); // Diese BWInfo hat nur eine Zeile
        String result = "";

        String color = "green";
        if (attrib.containsKey("unbeantwortet")) {
            color = "red";
        }

        result += "<font color=\"" + color + "\"><b>" + attrib.get("bwinfokurz").toString() + "</b></font>";
        result += attrib.get("html").toString();
        return result;
    }

    public String getTableAsHTML() {
        String html = "<h2>Einträge zum Vorgang</h2>";

        html += "<table border=\"1\" cellspacing=\"0\"><tr>" +
                "<th style=\"width:20%\">Info</th><th style=\"width:80%\">Eintrag</th></tr>";
        for (int v = 0; v < getRowCount(); v++) {

            html += "<tr>";
            html += "<td>" + getValueAt(v, COL_PIT).toString() + "</td>";
            html += "<td>" + getValueAt(v, COL_CONTENT).toString() + "</td>";
            html += "</tr>";

        }
        html += "</table>";

        return html;
    }

    public Object getValueAt(int r, int c) {
        Object result = "";
        try {
            rs.absolute(r + 1);
            switch (c) {
                case COL_CONTENT: {
                    if (cache.get(rs.getRow()) == null) {
                        int tblidx = rs.getInt("tblidx");
                        switch (tblidx) {
                            case TBL_TAGESBERICHTE: {
                                cache.set(rs.getRow(), op.care.berichte.DBHandling.getBerichtAsHTML(rs.getString("text"), rs.getString("UKennung"), rs.getString("BWKennung"), rs.getString("EKennung"), mitBWKennung, rs.getTimestamp("pit")));
                                break;
                            }
                            case TBL_PLANUNG: {
                                cache.set(rs.getRow(), op.care.planung.DBHandling.getPlanungAsHTML(rs.getLong("pk")));
                                break;
                            }
                            case TBL_BHPVERORDNUNG: {
                                //cache.set(rs.getRow(), op.care.verordnung.DBRetrieve.getVerordnungAsHTML(rs.getLong("pk")));
                                break;
                            }
                            case TBL_BWERTE: {
                                cache.set(rs.getRow(), op.care.vital.DBHandling.getWertAsHTML(rs.getLong("pk")));
                                break;
                            }
                            case TBL_BWINFO: {
                                cache.set(rs.getRow(), getBWInfo(rs.getLong("pk")));
                                break;
                            }
                            case TBL_VBERICHT: {
                                String html = "";
                                if (rs.getInt("Art") > 0) {
                                    html += "<font color=\"blue\">";
                                    html += op.share.vorgang.DBHandling.getBerichtAsHTML(rs.getLong("pk"));
                                    html += "</font>";
                                } else {
                                    html += op.share.vorgang.DBHandling.getBerichtAsHTML(rs.getLong("pk"));
                                }
                                cache.set(rs.getRow(), html);
                                break;
                            }
                            default: {
                                cache.set(rs.getRow(), "");
                                break;
                            }
                        }
                    }
                    result = cache.get(rs.getRow()).toString();
                    break;
                }
                case COL_PK: {
                    result = rs.getLong("pk");
                    break;
                }
                case COL_PIT: {
                    String name = DBRetrieve.getUsername(rs.getString("ukennung"));
                    String datum = df.format(rs.getTimestamp("pit"));
                    String html = "";
                    if (rs.getInt("Art") > 0) {
                        html += "<font color=\"blue\">";
                        html += datum + "; " + name;
                        html += "</font>";
                    } else {
                        html += datum + "; " + name;
                    }

                    result = html;
                    break;
                }
                case COL_SYSBERICHT: {
                    result = rs.getInt("Art") > 0;
                    break;
                }
                case COL_ELEMENTID: {
                    result = rs.getLong("VAID");
                    break;
                }
                case COL_TBLIDX: {
                    result = rs.getInt("tblidx");
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
