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
package op.share.bwinfo;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.JTextPane;
import tablerenderer.RNDHTML;
import op.tools.SYSTools;

/**
 *
 * @author tloehr
 */
public class RNDBWI extends RNDHTML {

    public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, obj, isSelected, hasFocus, row, column);

        TMBWInfo tm = (TMBWInfo) table.getModel();
        if (tm.isTooltip()) {
            String html = tm.getValueAt(row, TMBWInfo.COL_HTMLRAW).toString();
            String anuser = (String) tm.getValueAt(row, TMBWInfo.COL_ANUSER);
            String abuser = (String) tm.getValueAt(row, TMBWInfo.COL_ABUSER);

            if (column == TMBWInfo.COL_HTML) {
                ((JTextPane) c).setToolTipText(SYSTools.toHTML(html));
            }
            if (column == TMBWInfo.COL_VON) {
                ((JTextPane) c).setToolTipText(SYSTools.toHTML(anuser));
            }
            if (column == TMBWInfo.COL_BIS) {
                ((JTextPane) c).setToolTipText(SYSTools.toHTML(abuser));
            }
        }
        return c;
    }
}
