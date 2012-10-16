/*
 * OffenePflege
 * Copyright (C) 2006-2012 Torsten Löhr
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

package op.care.supervisor;

import op.OPDE;
import op.tools.SYSConst;
import tablerenderer.RNDHTML;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;

/**
 * @author tloehr
 */
public class RNDUbergabe
        extends RNDHTML {
    Color color;
    Font font;

    public RNDUbergabe() {
        super();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        TableModel tm = table.getModel();
        //long qsuid = ((Long) tm.getValueAt(row, TMUebergabe.COL_QSUID)).longValue();

        if (row % 2 == 0 && !isSelected) {
            this.color = Color.WHITE;
            // cell is selected, use the highlight color
        } else if (isSelected) {
            this.color = Color.LIGHT_GRAY;
        } else {
            this.color = SYSConst.khaki1;
        }

        OPDE.debug(value);

        // Das hier für das Zeichen zur Kenntnisnahme
        if (column == TMUebergabe.COL_ACKN) {
            JLabel j;
            if ((Boolean) value) {
                j = new JLabel(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/apply.png")));
            } else {
                j = new JLabel(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/help.png")));
            }
            j.setOpaque(true);
            j.setBackground(color);
            return j;
        }

        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }

    public Color getBackground() {
        return color;
    }

} // RNDUbergabe
