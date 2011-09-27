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

package op.care.med.vorrat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.AbstractTableModel;
import op.tools.DlgException;
import op.OPDE;
import op.tools.SYSTools;

/**
 *
 * @author tloehr
 */
public class TMBestellung
        extends AbstractTableModel {
    public static final int COL_BESTELLID = 0;
    public static final int COL_VORRAT = 1;
    public static final int COL_SALDO = 2;
    public static final int COL_BW = 3;
    public static final int COL_DATUM = 4;
    public static final int COL_ARZT = 5;
    public static final int COL_TEXT = 6;
    public static final int COL_UKENNUNG = 7;
    public static final int COL_ARZTID = 8;
    
    
    ResultSet rs;
    PreparedStatement stmt;
    String sql;
    //long verid;
    
    public TMBestellung(){
        super();
        //this.verid = verid;
        try{
            sql = " SELECT v.VorID, v.BWKennung, CONCAT(b.nachname,', ',b.vorname) BWName, b.DafID, v.Text, " +
                    " ifnull(b.saldo, 0.00) Bestandsmenge, " +
                    " bestell.BestellID, bestell.ArztID, bestell.UKennung, bestell.Text, bestell.Datum, bestell.Abschluss, " +
                    " a.Anrede, a.Titel, a.Name, a.Fax " +
                    " FROM MPVorrat v " +
                    " INNER JOIN MPBestellung bestell ON bestell.VorID = v.VorID " +
                    " INNER JOIN Arzt a ON a.ArztID = bestell.ArztID " +
                    " INNER JOIN Bewohner b ON v.BWKennung = b.BWKennung " +
                    " LEFT OUTER JOIN ( " +
                    "       SELECT best.VorID, best.DafID, sum(buch.Menge) saldo FROM MPBestand best " +
                    "       INNER JOIN MPBuchung buch ON buch.BestID = best.BestID " +
                    "       GROUP BY VorID " +
                    " ) b ON b.VorID = v.VorID " +
                    " WHERE v.Bis = '9999-12-31 23:59:59' AND bestell.Abschluss = '9999-12-31 23:59:59' " +
                    " ORDER BY ArztID, v.BWKennung, v.Text ";
            stmt = OPDE.getDb().db.prepareStatement(sql);
            //stmt.setLong(1, OPDE.getLogin().getLoginID());
            rs = stmt.executeQuery();
        } // try
        catch (SQLException se){
            new DlgException(se);
        } // catch
    }
    
    public void reload(int row, int col){
        try {
            rs = stmt.executeQuery();
            rs.first();
            fireTableRowsUpdated(row, col);
        } catch (SQLException se) {
            new DlgException(se);
        }
    }
    
    public int getRowCount() {
        try{
            rs.last();
            return rs.getRow();
        } catch (SQLException se){
            System.out.println(se.getMessage());
            return -1;
        }
    }
    
    public int getColumnCount() {
        int result = 8;
        return result;
    }
    
    public Class getColumnClass(int c){
        Class result;
        switch (c){
            case COL_BESTELLID:{ result = Long.class; break;}
            case COL_SALDO:{ result = Double.class; break;}
            case COL_ARZTID:{ result = Long.class; break;}
            default:{ result = String.class; }
        }
        return result;
    }
    
    
    public Object getValueAt(int r, int c) {
        Object result = null;
        try{
            rs.absolute(r+1);
            switch (c){
                case COL_VORRAT : {
                    result = rs.getString("v.Text");
                    break;
                }
                case COL_BW : {
                    result = rs.getString("BWName");
                    break;
                }
                case COL_SALDO : {
                    result = rs.getDouble("Bestandsmenge");
                    break;
                }
                case COL_DATUM : {
                    result = rs.getDate("Datum");
                    break;
                }
                case COL_ARZT : {
                    String name = rs.getString("a.Anrede") + " " +
                            ( SYSTools.catchNull(rs.getString("a.Titel")).equals("") ? "" : rs.getString("a.Titel") + " " ) +
                            rs.getString("a.Name") + " Fax.: " + rs.getString("a.Fax");
                    result = name;
                    break;
                }
                case COL_TEXT : {
                    result = rs.getString("bestell.Text");
                    break;
                }
                case COL_UKENNUNG : {
                    result = rs.getString("bestell.UKennung");
                    break;
                }
                case COL_BESTELLID : {
                    result = rs.getLong("BestellID");
                    break;
                }
                case COL_ARZTID : {
                    result = rs.getLong("ArztID");
                    break;
                }
                default : {result = "";}
            }
        } catch (SQLException se){
            new DlgException(se);
        }
        return result;
    }
}