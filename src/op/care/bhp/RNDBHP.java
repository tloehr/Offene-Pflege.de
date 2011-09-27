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
package op.care.bhp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import op.tools.SYSConst;
import op.tools.SYSTools;
import tablerenderer.RNDHTML;

/**
 *
 * @author tloehr
 */
public class RNDBHP
        extends RNDHTML {

    Color color;
    Font font;

    public RNDBHP() {
        super();
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component result = null;

//        Color bermuda_sand = new Color(246, 201, 204);
//        Color melonrindgreen = new Color(223, 255, 165);
        TableModel tm = table.getModel();
        int status = ((Integer) tm.getValueAt(row, TMBHP.COL_STATUS)).intValue();
        long dafid = ((Long) tm.getValueAt(row, TMBHP.COL_DAFID)).longValue();
        //long bestid = ((Long) tm.getValueAt(row, TMBHP.COL_BESTID)).longValue();
        String bembhp = SYSTools.catchNull((String) tm.getValueAt(row, TMBHP.COL_BEMBHP));
        //boolean reichtvorrat = ((Boolean) tm.getValueAt(row, TMBHP.COL_REICHTVORRAT)).booleanValue();
        color = Color.white;
//        if (status == TMBHP.STATUS_ERLEDIGT) { color = melonrindgreen; }
//        if (status == TMBHP.STATUS_VERWEIGERT) { color = bermuda_sand; }
//        if (isSelected) {
//            color = Color.LIGHT_GRAY;
//        }

        if (isSelected) {
            color = SYSConst.bluegrey;
            if (status == TMBHP.STATUS_ERLEDIGT) {
                color = SYSConst.darkolivegreen4;
            }
            if (status == TMBHP.STATUS_VERWEIGERT || status == TMBHP.STATUS_VERWEIGERT_VERWORFEN) {
                color = SYSConst.salmon4;
            }
        } else {
            if (status == TMBHP.STATUS_ERLEDIGT) {
                color = SYSConst.darkolivegreen2;
            }
            if (status == TMBHP.STATUS_VERWEIGERT || status == TMBHP.STATUS_VERWEIGERT_VERWORFEN) {
                color = SYSConst.salmon2;
            }
        }

        if (column == TMBHP.COL_DOSIS) {
            double dosis = ((Double) value).doubleValue();
            if (dosis == 0) {
                value = "";
            } else {
                value = SYSTools.printDouble(dosis);
            }
        }

        if (column == TMBHP.COL_STATUS) {
            JLabel j = new JLabel();
//            if (dafid > 0) {
//                //double saldo = ((Double) tm.getValueAt(row, TMBHP.COL_SALDO)).doubleValue();
//                //double bestsumme = ((Double) tm.getValueAt(row, TMBHP.COL_BESTAND_SALDO)).doubleValue();
//                int packeinheit = ((Integer) tm.getValueAt(row, TMBHP.COL_PACKEINHEIT)).intValue();
//                //String tmp = "Vorrat (gesamt): <b>" + SYSTools.roundScale2(saldo) + "</b> " + SYSConst.EINHEIT[packeinheit];
////                if (saldo != bestsumme) {
////                    tmp += "<br/>" + "Restmenge in der <u>angebrochenen</u> Packung: <b>" + SYSTools.roundScale2(bestsumme) + "</b> " + SYSConst.EINHEIT[packeinheit];
////                }
//                j.setToolTipText(SYSTools.toHTML(tmp));
//            }
            ImageIcon icon;
            if (status == TMBHP.STATUS_OFFEN) {
//                if (dafid > 0 && bestid == 0) { // kein Bestand angebrochen
//                    icon = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/infored.png"));
//                    j.setToolTipText("Es befindet sich kein Bestand im Anbruch. Um dieses Medikament zu geben, müssen sie erst einen anbrechen.");
//                } else {
//                    icon = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/infoyellow.png"));
//                }

                icon = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/infoyellow.png"));
            } else if (status == TMBHP.STATUS_ERLEDIGT) {
                icon = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/apply.png"));
            } else {
                icon = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/cancel.png"));
            }
            j.setIcon(icon);
            j.setOpaque(true);
            j.setBackground(color);
            result = j;
        } else if (column == TMBHP.COL_BEMBHP && !bembhp.equals("")) {
            JLabel j = new JLabel();
            ImageIcon icon;
            icon = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/edit.png"));
            j.setIcon(icon);
            j.setOpaque(true);
            j.setBackground(color);
            // Hier den Code der <p> in eine Table packen für die Zeilenumbrüche.
            String text = "<html><body><table border=\"0\">";
            text += "    <tr><td align=\"left\" width=\"600\">" + bembhp + "</td></tr>";
            text += "   </table></body></html>";
            j.setToolTipText(text);
            result = j;
        } else {
            result = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }

        return result;
    }

    public Color getBackground() {
        return color;
    }
} // RNDBHP

