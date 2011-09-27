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

package op.care.planung;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import javax.swing.table.AbstractTableModel;
import op.tools.DlgException;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;

/**
 *
 * @author root
 */
public class TMVorlagen extends AbstractTableModel {

    public static final int COL_STICHWORT = 0;
    public static final int COL_BWNAME = 1;
    public static final int COL_VON = 2;
    public static final int COL_BIS = 3;
    public static final int COL_PLANID = 4;
    ResultSet rs;
    PreparedStatement stmt;
    String sql;

    public TMVorlagen(String suche, boolean abgesetzte) {
        super();
        try {
            sql = "SELECT" +
                    " p.PlanID, p.Stichwort, CONCAT(b.Nachname,', ',b.Vorname,' [',p.BWKennung,']') bwname, p.Von, p.Bis " +
                    " FROM Planung p " +
                    " INNER JOIN Bewohner b on p.BWKennung = b.BWKennung " +
                    " WHERE p.Stichwort like ? " +
                    (!abgesetzte ? " AND p.Von <= now() AND p.Bis >= now() " : "");
            sql += " ORDER BY p.Stichwort, p.BWKennung, p.Von DESC ";
            stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setString(1, "%" + suche + "%");
            rs = stmt.executeQuery();
        } catch (SQLException se) {
            new DlgException(se);
        }
    }

    public void reload(int row, int col) {
        try {
            rs = stmt.executeQuery();
            fireTableRowsUpdated(row, col);
        } catch (SQLException se) {
            new DlgException(se);
        }
    }

    public int getRowCount() {
        try {
            rs.last();
            return rs.getRow();
        } catch (SQLException se) {
            new DlgException(se);
            return -1;
        }
    }

    public int getColumnCount() {
        int result = 4;
        return result;
    }

    public Class getColumnClass(int c) {
        Class result;
        switch (c) {
            case COL_PLANID: {
                result = Long.class;
                break;
            }
            default: {
                result = String.class;
            }
        }
        return result;
    }

    public Object getValueAt(int r, int c) {
        Object result = null;
        try {
            rs.absolute(r + 1);
            //OPDE.getLogger().debug(this.toString() + ":" + verid);
            switch (c) {
                case COL_STICHWORT: {
                    result = rs.getString("Stichwort");
                    break;
                }
                case COL_BWNAME: {
                    result = rs.getString("bwname");
                    break;
                }
                case COL_PLANID: {
                    result = rs.getLong("PlanID");
                    break;
                }
                case COL_VON: {
                    DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
                    result = df.format(rs.getDate("Von"));
                    break;
                }
                case COL_BIS: {
                    if (rs.getDate("Bis").equals(SYSConst.DATE_BIS_AUF_WEITERES)) {
                        result = "--";
                    } else {
                        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
                        result = df.format(rs.getDate("Bis"));
                    }
                    break;
                }

                default: {
                    result = "!!FEHLER!!";
                }
            }
        } catch (SQLException se) {
            new DlgException(se);
        }
        return result;
    }
}
