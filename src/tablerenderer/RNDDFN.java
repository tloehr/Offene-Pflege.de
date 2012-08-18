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

package tablerenderer;

import entity.nursingprocess.DFN;
import entity.nursingprocess.DFNTools;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.DateTime;
import org.joda.time.Days;
import tablemodels.TMDFN;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;

/**
 * @author tloehr
 */
public class RNDDFN
        extends RNDHTML {

    Color color;

    public RNDDFN() {
        super();
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component result = null;
//        Component result = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        TMDFN tmdfn = (TMDFN) table.getModel();
        DFN dfn = tmdfn.getListeDFN().get(row);

        color = panel.getBackground();
        if (dfn.getStatus() == DFNTools.STATE_OPEN) {
            if (isSelected) {
                color = SYSConst.grey80;
            } else {
                color = Color.white;
            }
        } else if (dfn.getStatus() == DFNTools.STATE_DONE) {
            if (isSelected) {
                color = SYSConst.darkolivegreen3;
            } else {
                color = SYSConst.darkolivegreen1;
            }
        } else { // (status == TMDFN.STATE_REFUSED
            if (isSelected) {
                color = SYSConst.salmon3;
            } else {
                color = SYSConst.salmon1;
            }
        }
        panel.setBackground(color);

        if (column == TMDFN.COL_STATUS) {
            JPanel p = new JPanel();
            JLabel j = new JLabel();
            ImageIcon icon;
            ImageIcon icon2 = null;
            ImageIcon icon4 = null;
            if (dfn.getStatus() == DFNTools.STATE_OPEN) {
                icon = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/infoyellow.png"));
                if (dfn.isFloating()) {
                    int daysBetween = Days.daysBetween(new DateTime(dfn.getStDatum()), new DateTime()).getDays();
                    if (daysBetween < 4) {
                        icon2 = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/reload-green.png"));
                    } else if (daysBetween < 7) {
                        icon2 = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/reload-yellow.png"));
                    } else {
                        icon2 = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/reload-red.png"));
                    }
                }
            } else if (dfn.getStatus() == DFNTools.STATE_DONE) {
                if (dfn.isFloating()) {
                    icon2 = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/infogreen.png"));
                }
                icon = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/apply.png"));
            } else {
                if (dfn.isFloating()) {
                    icon2 = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/infogreen.png"));
                }
                icon = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/cancel.png"));
            }
            j.setIcon(icon);
            j.setOpaque(true);
            j.setBackground(color);
            p.add(j);
            p.setOpaque(true);
            p.setBackground(color);
            if (icon4 != null) {
                JLabel j2 = new JLabel();
                j2.setIcon(icon4);
                j2.setOpaque(true);
                j2.setBackground(color);
                p.add(j2);
            }
            if (icon2 != null) {
                JLabel j2 = new JLabel();
                j2.setIcon(icon2);
                j2.setOpaque(true);
                j2.setBackground(color);
                String tiptext = "Diese Massnahme muss bearbeitet werden.";

                if (Days.daysBetween(new DateTime(dfn.getStDatum()), new DateTime()).getDays() != 0) {
                    tiptext += " Sie wurde ursprünglich für den <b>" + DateFormat.getDateInstance().format(dfn.getStDatum()) + "</b> eingeplant.";
                }
                p.setToolTipText(SYSTools.toHTML(tiptext));
                p.add(j2);
            }
            result = p;
        } else if (column == TMDFN.COL_BEMDFN) {
//            result = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//            String text = "";
//            if (dfn.getNursingProcess() == null) {
//                text = SYSTools.toHTML("<i>" + OPDE.lang.getString(PnlDFN.internalClassID + ".ondemand") + "</i>");
//            } else {
//                text = "<b><font color=\"green\">" +
//                        dfn.getNursingProcess().getStichwort() + "</font></b><table border=\"0\">";
//                text += "    <tr><td align=\"left\" width=\"600\">" + dfn.getNursingProcess().getSituation() + "</td></tr>";
//                text += "   </table>";
//            }
//            ((JTextPane) result.getComponentAt(row, column)).setToolTipText(text);
        } else {
            result = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
        return result;
    }


} // RNDDFN

