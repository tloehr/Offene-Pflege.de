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

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import javax.swing.JToggleButton;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.DBHandling;
import op.tools.DlgException;

/**
 *
 * @author tloehr
 */
public class TMBarbetrag extends AbstractTableModel {
    static final long serialVersionUID = 1;
    public static final int EDIT_MODE_NONE = 0;
    public static final int EDIT_MODE_CHANGE = 1;
    ResultSet rs;
    PreparedStatement stmt;
    private double vortrag;
    boolean subset;
    int offset;
    Date von;
    Date bis;
    JToggleButton btnEdit; // Dieses boolean gibt an, ob der Anwender die nötigen Rechte für Updates hat.
    // das weiss nur FrmTG.
    
    // Hier sind Variabeln die für das Edit benutzt werden.
    private int _editmode;
    String bwkennung;
    
    TMBarbetrag(String bwkennung, boolean subset, Date von, Date bis, JToggleButton btnEdit){
        super();
        this.von = SYSCalendar.bom(von); // Bottom Of Month
        this.bis = SYSCalendar.eom(bis); // End Of Month
        this._editmode = EDIT_MODE_NONE; // Standardmässig wird nur angezeigt.
        this.bwkennung = bwkennung;
        this.btnEdit = btnEdit;
        this.subset = subset;
        initData();
    }
    
    private void initData(){
        String sql = "SELECT TGID, BelegDatum, Belegtext, Betrag, _creator, _editor, _cdate, _edate, _cancel" +
                " FROM Taschengeld" +
                " WHERE BWKennung = ?";
        
        if (!subset) {
            sql += " ORDER BY BelegDatum, TGID";
            this.offset = 0;
            this.vortrag = 0f;
        } else {
            this.offset = 1;
            HashMap where = new HashMap();
            where.put("BWKennung",new Object[]{bwkennung, "="});
            
            sql += " AND BelegDatum >= ?" +
                    " AND BelegDatum <= ?" +
                    " ORDER BY BelegDatum, TGID";
            where.put("BelegDatum",new Object[]{von, "<"});
            
            BigDecimal summe = (BigDecimal) DBHandling.getSingleValue("Taschengeld","SUM(Betrag)",where);
            if (summe == null) summe = new BigDecimal(0);
            this.vortrag = summe.doubleValue();
        }
        
        try{
            stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setString(1, bwkennung);
            if (subset){
                stmt.setDate(2, new java.sql.Date(von.getTime()));
                stmt.setDate(3, new java.sql.Date(bis.getTime()));
            }
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
            int rows = rs.getRow() + this.offset*2;
            return rows;
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
        boolean allowed;
        // Vortrag
        if (rowIndex+1 == this.offset){ // Geht nur, wenn der Offset != 0 ist.
            // Steht auf Vortrag
            allowed =  false;
        } else if (this.offset > 0 && rowIndex+1 == getRowCount()) { // Zusammenfassung
            allowed = false;
        } else if (columnIndex+1 > getColumnCount()-1){ // Steht auf Zeilensaldo
            allowed = false;
        } else if (((Long)getValueAt(rowIndex, 6-1)).longValue() != 0){ // Steht auf Storno
            allowed = false;
        } else {
            allowed = this.btnEdit.isSelected();
        }
        return allowed;
    }
    
    public ResultSet getResultSet(){
        return this.rs;
    }
    
    public Class getColumnClass(int columnIndex){
        Class c;
        switch (columnIndex+1){
            case 1 : {c = Date.class; break;}
            case 2 : {c = String.class; break;}
            case 3 : {c = Double.class; break;}
            case 4 : {c = Double.class; break;}
            default : {c = String.class;}
        }
        return c;
    }
    
    private void update(Date belegdatum, String belegtext, double betrag, long tgid){
        PreparedStatement stmt;
        
        try {
            String insertSQL = "UPDATE Taschengeld SET BelegDatum=?,Belegtext=?,Betrag=?,BWKennung=?,_editor=?,_edate=now() WHERE TGID=?";
            
            stmt = OPDE.getDb().db.prepareStatement(insertSQL);
            
            stmt.setDate(1, new java.sql.Date(belegdatum.getTime()));
            stmt.setString(2, belegtext);
            stmt.setDouble(3, betrag);
            stmt.setString(4, this.bwkennung);
            stmt.setString(5, OPDE.getLogin().getUser().getUKennung());
            stmt.setLong(6, tgid);
            
            stmt.executeUpdate();
            
        } catch (SQLException ex) {
            new DlgException(ex);
            ex.printStackTrace();
        }
    }
    
    public Object getValueAt(int r, int c) {
        Object result;
        double zeilensaldo = 0.00;
        
        // Ermittlung des Zeilensaldos per Rekursion
        if (c+1 == 4) {
            if (r+1 == 1){
                zeilensaldo = vortrag; // Anker
            } else {
                zeilensaldo = ((Double) getValueAt(r-1, 4-1)).doubleValue(); // Rekursion
//                System.out.println(zeilensaldo);
            }
        }
        
        // Vortrag
        if (r+1 == this.offset){ // Geht nur, wenn der Offset != 0 ist.
            switch (c+1){
                case 1 : {result = SYSCalendar.addDate(this.von, -1); break;}//SYSCalendar.printGermanStyleShort(SYSCalendar.addDate(this.von, -1)); break;} // Von - 1 Tag
                case 2 : {result = "Übertrag aus Vormonat(en)"; break;}
                case 3 : {result = this.vortrag; break;}
                case 4 : {result = this.vortrag; break;}
                case 5 : {result = 0l; break;}
                case 6 : {result = 0l; break;} // cancel
                default : {result = ""; break;}
            }
        } else if (this.offset > 0 && r+1 == getRowCount()) { // Zusammenfassung
            switch (c+1){
                case 1 : {result = SYSCalendar.eom(bis); break;}
                case 2 : {result = "Saldo zum Monatsende"; break;}
                case 3 : {result = ""; break;}
                case 4 : {result = zeilensaldo; break;}
                case 5 : {result = 0l; break;}
                case 6 : {result = 0l; break;} // cancel
                default : {result = ""; break;}
            }
        } else {
            try{
                rs.absolute(r+1-this.offset);
                
                switch (c+1){
                    case 1 : {result = rs.getDate("BelegDatum"); break;}
                    case 2 : {result = rs.getString("Belegtext"); break;}
                    case 3 : {result = rs.getDouble("Betrag"); break;}
                    case 4 : {result = new Double(zeilensaldo + rs.getDouble("Betrag")); break;}
                    case 5 : {result = rs.getLong("TGID"); break;}
                    case 6 : {result = rs.getLong("_cancel"); break;}
                    default : {result = ""; break;}
                }
            } catch (SQLException se){
                new DlgException(se);
                se.printStackTrace();
                result = "";
            }
        }
        return result;
    }
    
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        long tgid = ((Long) getValueAt(rowIndex, 5-1)).longValue();
        Date belegdatum;
        String belegtext;
        double betrag;
        switch(columnIndex+1){
            case 1 : {
                belegdatum = (Date) aValue;
                belegtext = (String) getValueAt(rowIndex, 2-1);
                betrag = ((Double) getValueAt(rowIndex, 3-1)).doubleValue();
                break;}
            case 2 : {
                belegdatum = (Date) getValueAt(rowIndex, 1-1);
                belegtext = (String) aValue;
                betrag = ((Double) getValueAt(rowIndex, 3-1)).doubleValue();
                break;}
            case 3 : {
                belegdatum = (Date) getValueAt(rowIndex, 1-1);
                belegtext = (String) getValueAt(rowIndex, 2-1);
                betrag = ((Double) aValue).doubleValue();
                break;
            }
            default : { // Kann eigentlich nie passieren. Aber der formalen Korrektheit zu liebe.
                belegdatum=new Date();
                belegtext="";
                betrag=0.0d;
            }
        } // switch
        update(belegdatum, belegtext, betrag, tgid);
        initData();
        fireTableCellUpdated(rowIndex, columnIndex);
    } // setValueAt
    
    
    public double getVortrag() {
        return vortrag;
    }
    
    public int getEditmode() {
        return _editmode;
    }
    
    public int getRow(long tgid){
        int found = 0;
        int index = 0;
        try {
            rs.beforeFirst();
            while(found == 0 && rs.next()){
                if (rs.getLong("TGID") == tgid){
                    found = index;
                }
                index++;
            }
        } catch (SQLException ex) {
            found = 0;
            ex.printStackTrace();
        }
        return found;
    }
} // BWTableModel