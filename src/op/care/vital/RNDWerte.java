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

package op.care.vital;

import op.tools.SYSConst;
import tablerenderer.RNDTextArea;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;

/**
 * @author tloehr
 */
public class RNDWerte
        extends RNDTextArea {
    Color color;
    Color fontcolor;
    Font font;

    public RNDWerte() {
        super();
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        fontcolor = Color.BLACK;

        if (row % 2 == 0 && !isSelected) {
            this.color = Color.WHITE;
            // cell is selected, use the highlight color
        } else if (isSelected) {
            this.color = Color.LIGHT_GRAY;
        } else {
            this.color = SYSConst.khaki1;
        }

        if (column == 3) { // Werte-Spalte
            TableModel tm = table.getModel();
            boolean isBraden = ((String) tm.getValueAt(row, TMWerte.COL_XML)).indexOf("<braden") >= 0;
            boolean isBilanz = ((String) tm.getValueAt(row, TMWerte.COL_XML)).indexOf("<BILANZ/>") >= 0;
            this.setToolTipText(null);
            if (isBraden) {
                Double Wert = (Double) tm.getValueAt(row, TMWerte.COL_DBLWERT);
                double wert = Wert.doubleValue();
                ParseXMLBraden p = new ParseXMLBraden((String) tm.getValueAt(row, TMWerte.COL_XML));
                int[] werte = p.getWerte();
                String text = "";
                if (wert <= 10) {
                    text = "<h2 color=\"red\">Risiko: sehr hoch</h2>";
                }
                if (wert <= 15 && wert >= 11) {
                    text = "<h2 " + SYSConst.html_darkred + ">Risiko: hoch</h2>";
                }
                if (wert <= 19 && wert >= 16) {
                    text = "<h2 " + SYSConst.html_darkorange + ">Risiko: mittel</h2>";
                }
                if (wert >= 20) {
                    text = "<h2 " + SYSConst.html_darkgreen + ">Risiko: niedrig</h2>";
                }

                this.setToolTipText("<html><body><h1>Berechnung Bradenwert</h1>" +
                        "<ol><li>Wahrnehmung: " + werte[0] + "</li><li>Feuchtigkeit: " + werte[1] + "</li>" +
                        "<li>Aktivitäten: " + werte[2] + "</li><li>Mobilität: " + werte[3] + "</li><li>Ernährung: " + werte[4] + "</li>" +
                        "<li>Reibung: " + werte[5] + "</li></ol><br><h2>Summe: " + wert + "</h2>" +
                        "<br>" + text + "</body></html>");
            }

            if (isBilanz) {
                Double Wert = (Double) tm.getValueAt(row, TMWerte.COL_DBLWERT);
                double wert = Wert.doubleValue();
                if (wert < 0) {
                    fontcolor = Color.RED;
                } else {
                    fontcolor = Color.BLACK;
                }
            }
        }

        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }

    public Color getBackground() {
        return color;
    }

    public Color getForeground() {
        return fontcolor;
    }

} // TBTableRenderer
