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

package op.bw.tg;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import javax.swing.table.AbstractTableModel;
import op.OPDE;
import op.tools.DlgException;

/**
 *
 * @author tloehr
 */
public class TMTGStat extends AbstractTableModel {
    static final long serialVersionUID = 1;
    ResultSet rs;
    PreparedStatement stmt;
    
    
    TMTGStat(boolean alle){
        super();
        
        String sql = "SELECT b.nachname, b.vorname, b.bwkennung, tg.summe stand FROM Bewohner b ";
        //
        if (!alle) {
            sql +=  " INNER JOIN (" +
                    " SELECT BWKennung, Von, Bis FROM BWInfo " +
                    " WHERE BWINFTYP = 'hauf' AND " +
                    " ((von <= now() AND bis >= now()) OR (von <= now() AND" +
                    " bis >= now()) OR (von > now() AND bis < now()))" +
                    " ) AS bwattr ON b.BWKennung = bwattr.BWKennung ";
        }
        //
        sql += " LEFT OUTER JOIN (SELECT SUM(Betrag) summe, BWKennung FROM Taschengeld GROUP BY BWKennung) AS tg ON b.BWKennung = tg.BWKennung" +
                " ORDER BY b.nachname, b.vorname, b.bwkennung";
        
        try{
            stmt = OPDE.getDb().db.prepareStatement(sql);
            rs = stmt.executeQuery();
            rs.first();
        } // try
        catch (SQLException se){
            new DlgException(se);
            se.printStackTrace();
        } // catch
        
    }
    
    public int getRowCount() {
        try{
            rs.last();
            return rs.getRow();
        } catch (SQLException se){
            new DlgException(se);
            se.printStackTrace();
            return -1;
        }
    }
    
    public int getColumnCount() {
        return 4;
    }
    
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
    
    public Class getColumnClass(int c){
        if (c+1 >= 4) {
            return Double.class;
        } else {
            return String.class;
        }
    }
    
    public Object getValueAt(int r, int c) {
        Object result;
        
        try{
            rs.absolute(r+1);
            
            switch (c+1){
                case 1 : {result = rs.getString("b.nachname"); break;}
                case 2 : {result = rs.getString("b.vorname"); break;}
                case 3 : {result = rs.getString("b.bwkennung"); break;}
                case 4 : {result = rs.getDouble("stand"); break;}
                default : {result = ""; break;}
            }
        } catch (SQLException se){
            new DlgException(se);
            se.printStackTrace();
            result = "";
        }
        
        return result;
    }
    
    public ResultSet getResultSet(){
        return this.rs;
    }
} // BWTableModel