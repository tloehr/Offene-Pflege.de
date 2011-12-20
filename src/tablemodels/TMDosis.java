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

package tablemodels;

import entity.verordnungen.Verordnung;
import entity.verordnungen.VerordnungPlanung;
import entity.verordnungen.VerordnungPlanungTools;
import op.OPDE;

import javax.persistence.Persistence;
import javax.persistence.PersistenceUtil;
import javax.swing.table.AbstractTableModel;
import java.util.Collection;


/**
 * @author tloehr
 */
public class TMDosis
        extends AbstractTableModel {
    public static final int COL_Dosis = 0;

    String anwendung;

    Verordnung verordnung = null;
    //VerordnungPlanung[] planungen;


    public TMDosis(String anwtext, Verordnung verordnung) {
        super();

        this.anwendung = anwtext;
        this.verordnung = verordnung;
        //Collection<VerordnungPlanung> t = this.verordnung.getPlanungen();
//
//        PersistenceUtil util = Persistence.getPersistenceUtil();
//        OPDE.debug(util.isLoaded(verordnung));



    }

    public int getRowCount() {
        return verordnung.getPlanungen().size();
    }

    public int getColumnCount() {
        return 1;
    }

    public Class getColumnClass(int c) {
        return String.class;
    }

//    private String getDosis() {
//        String result = "<html><body>";
//        try {
//            // Zeit verwendet ?
//            boolean zeit = (rs.getDouble("NachtMo") + rs.getDouble("NachtAb") + rs.getDouble("Morgens") +
//                    rs.getDouble("Mittags") + rs.getDouble("Nachmittags") + rs.getDouble("Abends")) > 0;
//            boolean maxdosis = rs.getInt("MaxAnzahl") > 0;
//            boolean uhrzeit = rs.getTime("Uhrzeit") != null;
//
//            if (zeit) {
//
//
//                result += "<table border=\"1\">" +
//                        "   <tr>" +
//                        "      <th align=\"center\">fm</th>" +
//                        "      <th align=\"center\">mo</th>" +
//                        "      <th align=\"center\">mi</th>" +
//                        "      <th align=\"center\">nm</th>" +
//                        "      <th align=\"center\">ab</th>" +
//                        "      <th align=\"center\">sa</th>" +
//                        "      <th align=\"center\">Wdh.</th>" +
//                        "   </tr>";
//
//                result += "    <tr>" +
//                        "      <td align=\"center\">" + (rs.getDouble("NachtMo") > 0 ? rs.getDouble("NachtMo") : "--") + "</td>" +
//                        "      <td align=\"center\">" + (rs.getDouble("Morgens") > 0 ? rs.getDouble("Morgens") : "--") + "</td>" +
//                        "      <td align=\"center\">" + (rs.getDouble("Mittags") > 0 ? rs.getDouble("Mittags") : "--") + "</td>" +
//                        "      <td align=\"center\">" + (rs.getDouble("Nachmittags") > 0 ? rs.getDouble("Nachmittags") : "--") + "</td>" +
//                        "      <td align=\"center\">" + (rs.getDouble("Abends") > 0 ? rs.getDouble("Abends") : "--") + "</td>" +
//                        "      <td align=\"center\">" + (rs.getDouble("NachtAb") > 0 ? rs.getDouble("NachtAb") : "--") + "</td>" +
//                        "      <td>" + getWiederholung() + "</td>" +
//                        "    </tr>" +
//                        "   </table>";
//
//
////                result += ( rs.getDouble("NachtMo") > 0 ? "Nacht, früh morgens: " + rs.getDouble("NachtMo") + " " + einheit: "" );
////                result += ( result.endsWith(", ") || result.equals("") ? "" : ", ");
////                result += ( rs.getDouble("Morgens") > 0 ? "Morgens: " + rs.getDouble("Morgens") + " " + einheit : "" );
////                result += ( result.endsWith(", ") || result.equals("") ? "" : ", ");
////                result += ( rs.getDouble("Mittags") > 0 ? "Mittags: " + rs.getDouble("Mittags") + " " + einheit : "" );
////                result += ( result.endsWith(", ") || result.equals("") ? "" : ", ");
////                result += ( rs.getDouble("Nachmittags") > 0 ? "Nachmittags: " + rs.getDouble("Nachmittags") + " " + einheit : "" );
////                result += ( result.endsWith(", ") || result.equals("") ? "" : ", ");
////                result += ( rs.getDouble("Abends") > 0 ? "Abends: " + rs.getDouble("Abends") + " " + einheit : "" );
////                result += ( result.endsWith(", ") || result.equals("") ? "" : ", ");
////                result += ( rs.getDouble("NachtAb") > 0 ? "Nacht, spät abends: " + rs.getDouble("NachtAb") + " " + einheit : "" );
////                result = ( result.endsWith(", ") ? result.substring(0, result.length()-2) : result);
//            } else if (maxdosis) {
//                result += "Maximale Tagesdosis: <font color=\"blue\"><b>" + rs.getInt("MaxAnzahl") + "x " + rs.getDouble("MaxEDosis") + "</b></font> " + anwendung;
//            } else if (uhrzeit) {
//                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//                result += sdf.format(rs.getTime("Uhrzeit")) + " Uhr: " + rs.getDouble("UhrzeitDosis") + " " + anwendung;
//            } else {
//                result = "<b>!!FEHLER!!</b>";
//            }
//
//        } catch (SQLException ex) {
//            new DlgException(ex);
//        }
//
//        result += "</body></html>";
//        return result;
//    }

    public Object getValueAt(int r, int c) {
        String result = "";
        switch (c) {
            case COL_Dosis: {
                result = VerordnungPlanungTools.getDosisAsHTML(verordnung.getPlanungen().get(r), null, true);
                break;
            }
            default: {
                result = "";
            }
        }
        return result;
    }
}