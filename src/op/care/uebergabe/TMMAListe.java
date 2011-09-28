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

package op.care.uebergabe;

import op.OPDE;
import op.tools.DlgException;
import op.tools.SYSCalendar;

import javax.swing.table.AbstractTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.GregorianCalendar;

/**
 * @author tloehr
 */
public class TMMAListe
        extends AbstractTableModel {
    ResultSet rs;
    public static final int COL_UKennung = 0;
    public static final int COL_STR_DatumUhrzeit = 1;
    public static final int COL_Datum = 2;
    public static final int COL_Uhrzeit = 3;

    /**
     * Creates a new instance of TMMAListe
     */
    public TMMAListe(ResultSet rs) {
        this.rs = rs;
        try {
            rs.first();
        } catch (SQLException ex) {
            OPDE.getLogger().error(ex);
        }
    }

    public int getRowCount() {
        try {
            rs.last();
            return rs.getRow();
        } catch (SQLException se) {
            OPDE.getLogger().error(se);
            return -1;
        }
    }

    public int getColumnCount() {
        return 2;
    }

    public Class getColumnClass(int c) {
        return String.class;
    }

    public Object getValueAt(int r, int c) {
        Object result;
        try {
            rs.absolute(r + 1);

            long ts = rs.getTimestamp("_cdate").getTime();
            GregorianCalendar gc = SYSCalendar.toGC(ts);
//            Date date = new Date(gc.getTimeInMillis());
//            Time time = new Time(gc.getTimeInMillis());

            switch (c + 1) {
                case 1: {
                    result = rs.getString("UKennung");
                    break;
                }
                case 2: {
                    result = SYSCalendar.printGCGermanStyle(gc) + ", " + SYSCalendar.toGermanTime(gc);
                    break;
                }
                case 3: {
                    result = SYSCalendar.printGCGermanStyle(gc);
                    break;
                }
                case 4: {
                    result = SYSCalendar.toGermanTime(gc);
                    break;
                }
                default: {
                    result = null;
                    break;
                }
            }
        } catch (SQLException se) {
            new DlgException(se);
            result = "";
        }
        return result;
    }
}
