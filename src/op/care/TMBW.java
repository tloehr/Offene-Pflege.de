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

import entity.Stationen;
import op.OPDE;
import op.tools.DlgException;
import op.tools.SYSTools;

import javax.swing.table.AbstractTableModel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author tloehr
 */
public class TMBW extends AbstractTableModel {

    public static final int COL_NAME = 0;
    public static final int COL_BWKENNUNG = 1;
    ResultSet rs;
    PreparedStatement stmt;
    boolean anonym;

    TMBW(Stationen station) {

        anonym = OPDE.isAnonym();
        try {

            String s = ""
                    + " SELECT bw.nachname Nachname, bw.vorname Vorname, bw.BWKennung, bw.Geschlecht "
                    + " FROM Bewohner bw "
                    + " WHERE bw.StatID = ? ";
//                    + " INNER JOIN ("
//                    + "               SELECT BWKennung, Von, Bis FROM BWInfo WHERE BWINFTYP = 'hauf' "
//                    + "               AND von <= now() AND bis >= now()"
//                    + "           ) AS hauf ON bw.BWKennung = hauf.BWKennung ";
//
//
//            s += " INNER JOIN ("
//                    + "           SELECT BWKennung, Von, Bis FROM BWInfo WHERE BWINFTYP = 'station' "
//                    + "           AND von <= now() AND bis >= now() and XML=? "
//                    + "       ) AS stat ON bw.BWKennung = stat.BWKennung ";
//

            s += " ORDER BY Nachname, Vorname ";

            stmt = OPDE.getDb().db.prepareStatement(s);
            stmt.setLong(1, station.getStatID());

            rs = stmt.executeQuery();
            //rs.first();
        } // try
        catch (SQLException se) {
            new DlgException(se);
        } // catch
    }

    TMBW() {
        anonym = OPDE.isAnonym();
        try {
            String s;

            s = "SELECT bw.nachname Nachname, bw.vorname Vorname, bw.BWKennung, bw.Geschlecht  FROM Bewohner bw "
                    + " ORDER BY Nachname, Vorname";

            //OPDE.getLogger().debug(s);

            stmt = OPDE.getDb().db.prepareStatement(s);

            rs = stmt.executeQuery();
            //rs.first();
        } // try
        catch (SQLException se) {
            new DlgException(se);
        } // catch

    }

    public int getRowCount() {
        try {
            rs.last();
            return rs.getRow();
        } catch (SQLException se) {
            System.out.println(se.getMessage());
            return -1;
        }
    }

    public int getColumnCount() {
        return 1;
    }

    public Class getColumnClass(int c) {
        return String.class;
    }

    public Object getValueAt(int r, int c) {
        Object result;
        try {
            rs.absolute(r + 1);
            switch (c) {
                case COL_NAME: {
                    String tmp = "";
                    if (rs.getInt("Geschlecht") == 2) {
                        tmp = "<font color=\"red\">";
                        tmp += SYSTools.anonymizeName(rs.getString("Nachname"), SYSTools.INDEX_NACHNAME) + ", " + SYSTools.anonymizeName(rs.getString("Vorname"), SYSTools.INDEX_VORNAME_FRAU) + " [" + rs.getString("BWKennung") + "]";
                    } else {
                        tmp = "<font color=\"blue\">";
                        tmp += SYSTools.anonymizeName(rs.getString("Nachname"), SYSTools.INDEX_NACHNAME) + ", " + SYSTools.anonymizeName(rs.getString("Vorname"), SYSTools.INDEX_VORNAME_MANN) + " [" + rs.getString("BWKennung") + "]";
                    }
                    tmp += "</font>";
                    result = tmp;
                    break;
                }
                case COL_BWKENNUNG: {
                    result = rs.getString("BWKennung");
                    break;
                }
                default: {
                    result = "";
                    break;
                }
            }
        } catch (SQLException se) {
            System.out.println(se.getMessage());
            result = "";
        }
        return result;
    }
} // BWTableModel

