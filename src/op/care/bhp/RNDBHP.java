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

import entity.verordnungen.BHP;
import entity.verordnungen.BHPTools;
import op.tools.SYSConst;
import op.tools.SYSTools;
import tablerenderer.RNDHTML;

import javax.swing.*;
import java.awt.*;

/**
 * @author tloehr
 */
public class RNDBHP extends RNDHTML {

    Color color;
    Font font;

    public RNDBHP() {
        super();
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component result = null;

        Object[] objects = (Object[]) ((TMBHP) table.getModel()).getListeBHP().get(row);

        BHP bhp = (BHP) objects[0];

        color = Color.white;

        if (isSelected) {
            color = SYSConst.bluegrey;
            if (bhp.getStatus() == BHPTools.STATUS_ERLEDIGT) {
                color = SYSConst.darkolivegreen4;
            }
            if (bhp.getStatus() == BHPTools.STATUS_VERWEIGERT || bhp.getStatus() == BHPTools.STATUS_VERWEIGERT_VERWORFEN) {
                color = SYSConst.salmon4;
            }
        } else {
            if (bhp.getStatus() == BHPTools.STATUS_ERLEDIGT) {
                color = SYSConst.darkolivegreen2;
            }
            if (bhp.getStatus() == BHPTools.STATUS_VERWEIGERT || bhp.getStatus() == BHPTools.STATUS_VERWEIGERT_VERWORFEN) {
                color = SYSConst.salmon2;
            }
        }

//        if (column == TMBHP.COL_DOSIS) {
//            double dosis = ((Double) value).doubleValue();
//            if (dosis == 0) {
//                value = "";
//            } else {
//                value = SYSTools.printDouble(dosis);
//            }
//        }

        if (column == TMBHP.COL_STATUS) {
            JLabel j = new JLabel();

            ImageIcon icon;
            if (bhp.getStatus() == BHPTools.STATUS_OFFEN) {


                icon = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/infoyellow.png"));
            } else if (bhp.getStatus() == BHPTools.STATUS_ERLEDIGT) {
                icon = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/apply.png"));
            } else {
                icon = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/cancel.png"));
            }
            j.setIcon(icon);
            j.setOpaque(true);
            j.setBackground(color);
            result = j;
        } else if (column == TMBHP.COL_BEMBHP && !SYSTools.catchNull(bhp.getBemerkung()).isEmpty()) {
            JLabel j = new JLabel();
            ImageIcon icon;
            icon = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/edit.png"));
            j.setIcon(icon);
            j.setOpaque(true);
            j.setBackground(color);
            // Hier den Code der <p> in eine Table packen für die Zeilenumbrüche.
            String text = "<html><body><table border=\"0\">";
            text += "    <tr><td align=\"left\" width=\"600\">" + bhp.getBemerkung() + "</td></tr>";
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

