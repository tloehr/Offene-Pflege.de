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

import entity.prescription.BHP;
import entity.prescription.BHPTools;
import op.tools.SYSConst;
import tablemodels.TMBHP;

import javax.swing.*;
import java.awt.*;

/**
 * @author tloehr
 */
public class RNDBHP extends RNDHTML {

    Color color;

    public RNDBHP() {
        super();
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JPanel panel = (JPanel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        Object[] objects = ((TMBHP) table.getModel()).getListeBHP().get(row);

        BHP bhp = (BHP) objects[0];

        color = panel.getBackground();

        if (bhp.getStatus() != BHPTools.STATE_OPEN) {
            if (isSelected) {
                color = SYSConst.bluegrey;
                if (bhp.getStatus() == BHPTools.STATE_DONE) {
                    color = SYSConst.darkolivegreen4;
                }
                if (bhp.getStatus() == BHPTools.STATE_REFUSED || bhp.getStatus() == BHPTools.STATE_REFUSED_DISCARDED) {
                    color = SYSConst.salmon4;
                }
            } else {
                if (bhp.getStatus() == BHPTools.STATE_DONE) {
                    color = SYSConst.darkolivegreen2;
                }
                if (bhp.getStatus() == BHPTools.STATE_REFUSED || bhp.getStatus() == BHPTools.STATE_REFUSED_DISCARDED) {
                    color = SYSConst.salmon2;
                }
            }
            panel.setBackground(color);
        }


        if (column == TMBHP.COL_STATUS) {
            JLabel j;

            // Sonst werden immer weiter Labels zu dem Panel aus der Super Klasse hinzugefügt.
            if (panel.getComponent(0) instanceof JLabel) {
                j = (JLabel) panel.getComponent(0);
            } else {
                panel.remove(txt);
                j = new JLabel();
                j.setOpaque(false);
                j.setBackground(new Color(0, 0, 0, 0));
                panel.setLayout(new BorderLayout());
                panel.add(j, BorderLayout.CENTER);
            }

            ImageIcon icon;
            if (bhp.getStatus() == BHPTools.STATE_OPEN) {
                icon = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/infoyellow.png"));
            } else if (bhp.getStatus() == BHPTools.STATE_DONE) {
                icon = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/apply.png"));
            } else {
                icon = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/cancel.png"));
            }
            j.setIcon(icon);
        }

        return panel;
    }

    public Color getBackground() {
        return color;
    }
} // RNDBHP

