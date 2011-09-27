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

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.BitSet;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import tablerenderer.RNDHTML;

/**
 *
 * @author tloehr
 */
public class RNDVerordnung extends RNDHTML {
    //private Color color;
    /** Creates a new instance of RNDVerordnung */
    public RNDVerordnung() {
        super();
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        TableModel tm = table.getModel();
        BitSet bs = ((BitSet) tm.getValueAt(row, TMVerordnung.COL_INFO));

        if (column == TMVerordnung.COL_INFO) {
            JPanel jp = new JPanel();
            jp.setLayout(new FlowLayout());
            if (bs.get(0)) {
                JLabel l1 = new JLabel(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/attach.png")));
                l1.setToolTipText("Zu dieser Verordnung gibt es Dokumente");
                jp.add(l1);
            }
            if (bs.get(1)) {
                JLabel l2 = new JLabel(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/apply.png")));
                l2.setToolTipText("Zu dieser Verordnung gibt es bereits eine Medikamentenbestellung.");
                jp.add(l2);
            }
//            if (isSelected) {
//                if (row % 2 == 0) {
//                    color = SYSConst.grey80;
//                } else {
//                color = SYSConst.khaki3;
//                }
//            } else {
//                if (row % 2 == 0) {
//                    color = Color.WHITE;
//                } else {
//                    color = SYSConst.khaki2;
//                }
//            }
//            jp.setBackground(color);
            return jp;
        } else {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}
