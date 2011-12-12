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
import op.tools.DlgException;

import javax.swing.table.AbstractTableModel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author tloehr
 */
public class TMInfoListe extends AbstractTableModel {

    //    private ArrayList content;
//    private SimpleDateFormat sdf;
    private ResultSet rs;
    private boolean[] zebra = null;
    public static final int COL_KATEGORIE = 0;
    public static final int COL_HTML = 1;
    public static final int COL_BWIKID = 2;
    public static final int COL_ZEBRA = 3;
    public static final int COL_BWINFTYP = 4;
    public static final int COL_INTERVALMODE = 5;
    public static final int COL_ART = 6;

    TMInfoListe(int katart, String suche) {
        String sql =
                " SELECT t.BWINFTYP, t.BWInfoKurz, t.IntervalMode, k.KatArt, k.Bezeichnung, k.BWIKID, k.Sortierung " +
                        " FROM BWInfoTyp t " +
                        " INNER JOIN BWInfoKat k ON t.BWIKID = k.BWIKID " +
                        " WHERE UPPER(t.BWINFTYP) <> 'HAUF' AND t.Sortierung >= 0 AND k.Sortierung >= 0 " +
                        (katart == BWInfo.ART_ALLES ? "" : (katart == BWInfo.ART_PFLEGE_STAMMDATEN ? " AND k.KatArt IN (" + BWInfo.ART_PFLEGE + "," + BWInfo.ART_STAMMDATEN + ") " : (katart == BWInfo.ART_VERWALTUNG_STAMMDATEN ? " AND k.KatArt IN (" + BWInfo.ART_VERWALTUNG + "," + BWInfo.ART_STAMMDATEN + ") " : " AND k.KatArt = ? ")));
        if (!suche.equals("")) {
            sql += " AND t.BWInfoKurz like ? ";
        }
        sql += "       ORDER BY k.Sortierung, k.Bezeichnung, t.Sortierung, t.BWInfoKurz ";
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            int lastParamCount = 0;
            if (katart < BWInfo.ART_PFLEGE_STAMMDATEN) {
                lastParamCount++;
                stmt.setInt(lastParamCount, katart);
            }
            if (!suche.equals("")) {
                suche = "%" + suche + "%";
                lastParamCount++;
                stmt.setString(lastParamCount, suche);
            }
            rs = stmt.executeQuery();

            // Das hier zur Beschleunigung der katindices. Hatte ich mal
            // als Rekursion aufgebaut. Wird dann aber SCHWEINElangsam.
            if (rs.last()) {
                zebra = new boolean[rs.getRow()];
                rs.first();
                int prev = rs.getInt("k.BWIKID");
                rs.beforeFirst();
                boolean z = true;
                while (rs.next()) {
                    int current = rs.getInt("k.BWIKID");
                    //OPDE.debug("#"+rs.getRow()+"   prev: "+prev + "    current: "+current + "     "+z+"  "+rs.getString("k.Bezeichnung"));
                    if (prev != current) {
                        prev = current;
                        z = !z;
                    }
                    zebra[rs.getRow() - 1] = z;
                }
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

    public Object getValueAt(int r, int c) {
        Object result = "";
        try {
            rs.absolute(r + 1);
            boolean katchange = (r == 0) || zebra[r - 1] != zebra[r];

            switch (c) {
                case COL_KATEGORIE: {
                    if (katchange) {
                        result = "<font color=\"blue\"><b>" + rs.getString("Bezeichnung") + "</b></font>";
                    }

                    break;
                }
                case COL_HTML: {
                    result = rs.getString("BWInfoKurz");
                    break;
                }
                case COL_BWIKID: {
                    result = rs.getLong("BWIKID");
                    break;
                }
                case COL_ZEBRA: {
                    result = zebra[r];
                    break;
                }
                case COL_BWINFTYP: {
                    result = rs.getString("BWINFTYP");
                    break;
                }
                case COL_INTERVALMODE: {
                    result = rs.getInt("IntervalMode");
                    break;
                }
                case COL_ART: {
                    result = rs.getLong("KATART");
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
