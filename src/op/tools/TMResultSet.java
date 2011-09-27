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
package op.tools;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import javax.swing.table.AbstractTableModel;
import op.OPDE;

/**
 *
 *
 */
public class TMResultSet
        extends AbstractTableModel {

    private ResultSet rs;
    private ResultSetMetaData rsmd;
    private HashMap filter;
    private boolean noFilter;
    private int colCount;
    private HashMap cols;

    /**
     *
     *
     * So umstellen, dass man einen Filter eingeben kann, und dann nur die angezeigt werden, die man
     * haben will
     *
     */
    public TMResultSet(ResultSet rs) {
        this(rs, null);
    }

    /**
     * 
     * 
     * 
     * @param rs
     * @param filter. Dies ist eine HashMap, die die vorhandenen Datenbank Spalte auf die anzuzeigende Tabellen Spalte abbildet. Haben wir 
     * z.B. eine Tabelle mit den Spalten A,B,C möchten aber nur die Spalten B,C angezeigt bekommen, so enthält die HashMap folgende Paare:
     * [(1,2),(2,3),(ScreenTableCol, DBTableCol)]. Bei einem leeren Filter (oder null), wird 1:1 angezeigt.
     * Die Spaltennummerierung fängt immer bei 1 an.
     * Unabhängig vom Filter wird stets die erste Spalte geschlabbert, da wird der PK drin erwartet.
     * 
     */
    public TMResultSet(ResultSet rs, HashMap filter) {
        super();
        this.rs = rs;
        try {
            this.rsmd = rs.getMetaData();
            this.colCount = rsmd.getColumnCount();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        noFilter = (filter == null || filter.size() == 0);
        this.filter = filter;
        initCols();
    }

    /**
     * Er stellt einen Index, damit man nachher im Klartext auf die Spalten zugreifen kann.
     */
    private void initCols() {
        cols = new HashMap();
        for (int i = 1; i <= colCount; i++) {
            try {
                cols.put(rsmd.getColumnName(i), i);
            } catch (SQLException ex) {
                OPDE.getLogger().error(ex);
            }
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

    public void reload(int row, int col) {
        try {
            col = getColumn(col);
            PreparedStatement stmt = (PreparedStatement) rs.getStatement();
            rs = stmt.executeQuery();
            rs.first();
            fireTableRowsUpdated(row, col);
        } catch (SQLException se) {
            new DlgException(se);
        }
    }

    public int getColumnCount() {
        int cols;
        if (noFilter) {
            cols = colCount - 1;
        } else {
            cols = filter.size();
        }
        return cols;
    }

    public Class getColumnClass(int col) {
        Class c = null;
        col = getColumn(col);
        try {
            String classname = "";
            if (noFilter) {
                classname = rsmd.getColumnClassName(col + 1);
            } else {
                classname = rsmd.getColumnClassName(col);
            }
            if (classname.equals("java.sql.Date")) {
                classname = "java.util.Date";
            }
            if (classname.equals("java.sql.Timestamp")) {
                classname = "java.util.Date";
            }
            c = Class.forName(classname);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return c;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        rs.close();
        cols.clear();
    }

    public Object getValueAt(int r, int c) {
        Object result = "";
        c = getColumn(c);
        try {
            rs.absolute(r + 1);
            if (noFilter) {
                result = rs.getObject(c + 2); // Niemals die erste Spalte.    
            } else {
                result = rs.getObject(c);
            }
//            if (result instanceof java.sql.Timestamp){
//                result = new java.util.Date(((java.sql.Timestamp) result).getTime());
//            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * Verhält sich genau wie getValueAt ignoriert nur jede Filterung. Damit man auch noch an alle Spalten rankommt, selbst
     * wenn die eigentlich nicht angezeigt werden sollen.
     * 
     * @param r
     * @param c
     * @return
     */
    public Object getValue(int r, int c) {
        Object result = "";
        try {
            rs.absolute(r + 1);
            result = rs.getObject(c);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public Object getValue(int r, String col) {
        Object result = "";
        if (cols.containsKey(col)) {
            result = getValue(r, ((Integer) cols.get(col)).intValue());
        }
        return result;
    }

    public Object getPK(int r) {
        Object result = null;
        try {
            rs.absolute(r + 1);
            result = rs.getObject(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public String getColumnName(int column) {
        column = getColumn(column);
        String result = "";
        try {
            if (noFilter) {
                result = rsmd.getColumnName(column + 2);
            } else {
                result = rsmd.getColumnName(column);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    private int getColumn(int col) {
        int result;
        if (noFilter) {
            result = col;
        } else {
            result = ((Integer) this.filter.get(col + 1)).intValue();
        }
        return result;
    }
}

