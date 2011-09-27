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

package op.care.verordnung;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.table.AbstractTableModel;
import op.tools.DlgException;
import op.OPDE;
import op.tools.SYSCalendar;

/**
 *
 * @author tloehr
 */
public class TMDosis
        extends AbstractTableModel {
    public static final int COL_Dosis = 0;
    public static final int COL_Wiederholung = 1;
    public static final int COL_BHPPID = 2;
    
    ResultSet rs;
    PreparedStatement stmt;
    String sql;
    String anwendung;
    //boolean withTime;
    //long verid;
    
    public TMDosis(String anwtext){
        super();
        //this.withTime = withTime;
        this.anwendung = anwtext;
        //this.verid = verid;
        try{
            sql = " SELECT bhp.BHPPID, UhrzeitDosis, bhp.Uhrzeit, bhp.MaxAnzahl, bhp.MaxEDosis," +
                    " bhp.NachtMo, bhp.Morgens, bhp.Mittags, bhp.Nachmittags, bhp.Abends, bhp.NachtAb," +
                    " bhp.Mon, bhp.Die, bhp.Mit, bhp.Don, bhp.Fre, bhp.Sam, bhp.Son, bhp.Taeglich," +
                    " bhp.Woechentlich, bhp.Monatlich, bhp.TagNum, bhp.LDatum " +
                    " FROM BHPPlanung bhp" +
                    " WHERE bhp.tmp = ?";
            stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setLong(1, OPDE.getLogin().getLoginID());
            rs = stmt.executeQuery();
            rs.first();
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
        int result = 1;
        return result;
    }
    
    public Class getColumnClass(int c){
        Class result;
        switch (c){
            case COL_BHPPID:{ result = Long.class; break;}
            default:{ result = String.class; }
        }
        return result;
    }
    
    private String getDosis(){
        String result = "<html><body>";
        try {
            // Zeit verwendet ?
            boolean zeit = (rs.getDouble("NachtMo")+rs.getDouble("NachtAb")+rs.getDouble("Morgens")+
                    rs.getDouble("Mittags")+rs.getDouble("Nachmittags")+rs.getDouble("Abends")) > 0;
            boolean maxdosis = rs.getInt("MaxAnzahl") > 0;
            boolean uhrzeit = rs.getTime("Uhrzeit") != null;
            
            if (zeit) {
                
                
                
                result += "<table border=\"1\">" +
                        "   <tr>" +
                        "      <th align=\"center\">fm</th>" +
                        "      <th align=\"center\">mo</th>" +
                        "      <th align=\"center\">mi</th>" +
                        "      <th align=\"center\">nm</th>" +
                        "      <th align=\"center\">ab</th>" +
                        "      <th align=\"center\">sa</th>" +
                        "      <th align=\"center\">Wdh.</th>" +
                        "   </tr>";
                
                result +=  "    <tr>" +
                        "      <td align=\"center\">"+(rs.getDouble("NachtMo") > 0 ? rs.getDouble("NachtMo") : "--")+"</td>" +
                        "      <td align=\"center\">"+(rs.getDouble("Morgens") > 0 ? rs.getDouble("Morgens") : "--")+"</td>" +
                        "      <td align=\"center\">"+(rs.getDouble("Mittags") > 0 ? rs.getDouble("Mittags") : "--")+"</td>" +
                        "      <td align=\"center\">"+(rs.getDouble("Nachmittags") > 0 ? rs.getDouble("Nachmittags") : "--")+"</td>" +
                        "      <td align=\"center\">"+(rs.getDouble("Abends") > 0 ? rs.getDouble("Abends") : "--")+"</td>" +
                        "      <td align=\"center\">"+(rs.getDouble("NachtAb") > 0 ? rs.getDouble("NachtAb") : "--")+"</td>" +
                        "      <td>"+getWiederholung()+"</td>" +
                        "    </tr>";
                
                
                
                
//                result += ( rs.getDouble("NachtMo") > 0 ? "Nacht, früh morgens: " + rs.getDouble("NachtMo") + " " + einheit: "" );
//                result += ( result.endsWith(", ") || result.equals("") ? "" : ", ");
//                result += ( rs.getDouble("Morgens") > 0 ? "Morgens: " + rs.getDouble("Morgens") + " " + einheit : "" );
//                result += ( result.endsWith(", ") || result.equals("") ? "" : ", ");
//                result += ( rs.getDouble("Mittags") > 0 ? "Mittags: " + rs.getDouble("Mittags") + " " + einheit : "" );
//                result += ( result.endsWith(", ") || result.equals("") ? "" : ", ");
//                result += ( rs.getDouble("Nachmittags") > 0 ? "Nachmittags: " + rs.getDouble("Nachmittags") + " " + einheit : "" );
//                result += ( result.endsWith(", ") || result.equals("") ? "" : ", ");
//                result += ( rs.getDouble("Abends") > 0 ? "Abends: " + rs.getDouble("Abends") + " " + einheit : "" );
//                result += ( result.endsWith(", ") || result.equals("") ? "" : ", ");
//                result += ( rs.getDouble("NachtAb") > 0 ? "Nacht, spät abends: " + rs.getDouble("NachtAb") + " " + einheit : "" );
//                result = ( result.endsWith(", ") ? result.substring(0, result.length()-2) : result);
            } else if (maxdosis) {
                result += "Maximale Tagesdosis: <font color=\"blue\"><b>" + rs.getInt("MaxAnzahl") + "x " + rs.getDouble("MaxEDosis") + "</b></font> " + anwendung;
            } else if (uhrzeit) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                result += sdf.format(rs.getTime("Uhrzeit"))+ " Uhr: " + rs.getDouble("UhrzeitDosis") + " " + anwendung;
            } else {
                result = "<b>!!FEHLER!!</b>";
            }
            
        } catch (SQLException ex) {
            new DlgException(ex);
        }
        
        result += "</body></html>";
        return result;
    }
    
    private String getWiederholung(){
        String result = "";
        try {
            if (rs.getInt("Taeglich") > 0) {
                if (rs.getInt("Taeglich") > 1) { result += "alle "+rs.getInt("Taeglich")+" Tage"; }
            } else if (rs.getInt("Woechentlich") > 0) {
                if (rs.getInt("Woechentlich") == 1) result += "jede Woche ";
                else result += "alle "+rs.getInt("Woechentlich")+" Wochen ";
                
                if (rs.getInt("Mon")>0) { result += "Mon ";}
                if (rs.getInt("Die")>0) { result += "Die ";}
                if (rs.getInt("Mit")>0) { result += "Mit ";}
                if (rs.getInt("Don")>0) { result += "Don ";}
                if (rs.getInt("Fre")>0) { result += "Fre ";}
                if (rs.getInt("Sam")>0) { result += "Sam ";}
                if (rs.getInt("Son")>0) { result += "Son ";}
                
            } else if (rs.getInt("Monatlich") > 0) {
                if (rs.getInt("Monatlich") == 1) { result += "jeden Monat ";} else { result += "alle "+rs.getInt("Monatlich")+" Monate ";}
                
                if (rs.getInt("TagNum") > 0) {result += "jeweils am "+rs.getInt("TagNum")+". des Monats";} else {
                    
                    int wtag = 0;
                    String tag = "";
                    if (rs.getInt("Mon")>0) {tag += "Montag "; wtag=rs.getInt("Mon");}
                    if (rs.getInt("Die")>0) {tag += "Dienstag "; wtag=rs.getInt("Die");}
                    if (rs.getInt("Mit")>0) {tag += "Mittwoch "; wtag=rs.getInt("Mit");}
                    if (rs.getInt("Don")>0) {tag += "Donnerstag "; wtag=rs.getInt("Don");}
                    if (rs.getInt("Fre")>0) {tag += "Freitag "; wtag=rs.getInt("Fre");}
                    if (rs.getInt("Sam")>0) {tag += "Samstag "; wtag=rs.getInt("Sam");}
                    if (rs.getInt("Son")>0) {tag += "Sonntag "; wtag=rs.getInt("Son");}
                    result += "jeweils am "+wtag+". "+tag+" des Monats";
                }
            } else {
                result = "!! FEHLER !!";
            }
            Date ldatum = new Date(rs.getTimestamp("LDatum").getTime());
            if (SYSCalendar.sameDay(ldatum, new Date()) > 0){ // Die erste Ausführung liegt in der Zukunft
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
                result += "<br/>erst ab: " + sdf.format(ldatum);
            }
        } catch (SQLException ex) {
            new DlgException(ex);
        }
        return result;
    }
    
// Dosis from Double
    private String dfd(double d){
        String result;
        if (d == 0){
            result = "";
        } else {
            result = Double.toString(d);
        }
        return result;
    }
    
    public Object getValueAt(int r, int c) {
        Object result = null;
        try{
            rs.absolute(r+1);
            switch (c){
                case COL_Dosis : {
                    result = getDosis();
                    break;
                }
//                case COL_Wiederholung : {result = getWiederholung(); break;}
                case COL_BHPPID : {
                    result = rs.getLong("BHPPID");
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