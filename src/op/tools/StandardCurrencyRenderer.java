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

package op.tools;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.NumberFormat;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import op.tools.SYSConst;

/**
 *
 * @author tloehr
 */
public class StandardCurrencyRenderer
        extends DefaultTableCellRenderer {
    Color color;
    Color fontcolor;
    Font font;
    
    public StandardCurrencyRenderer() {
        super();
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        
        TableModel tm = table.getModel();
        
       if (isSelected) {
            if (row % 2 == 0) {
                this.color = SYSConst.grey80;
            } else {
                this.color = SYSConst.khaki4;
            }
        } else {
            if (row % 2 == 0) {
                this.color = Color.WHITE;
            } else {
                this.color = SYSConst.khaki2;
            }
        }
        
        if (value instanceof Double) {
            
            if (((Double) value).doubleValue() < 0) {
                fontcolor = Color.RED;
            } else {
                fontcolor = Color.BLACK;
            }
            
            NumberFormat nf = NumberFormat.getCurrencyInstance();
            value = nf.format(value);
            setHorizontalAlignment(JLabel.RIGHT);
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
    
    public Color getForeground(){
        return fontcolor;
    }
    
    public Color getBackground(){
        return color;
    }
    
}
