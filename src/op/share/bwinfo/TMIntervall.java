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

import javax.swing.table.AbstractTableModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author tloehr
 */
public class TMIntervall extends AbstractTableModel {
    private ArrayList content;
    private SimpleDateFormat sdf;

    TMIntervall(ArrayList c, boolean withTime) {
        super();
        sdf = new SimpleDateFormat(withTime ? "dd.MM.yyyy HH:mm:ss" : "dd.MM.yyyy");
        this.content = c;
    }

    public int getRowCount() {
        return content.size();
    }

    public int getColumnCount() {
        return 2;
    }


    public Class getColumnClass(int c) {
        return String.class;
    }

    public Object getValueAt(int r, int c) {
        String result;
        Date[] d = (Date[]) this.content.get(r);
        switch (c + 1) {
            //case 1 : // hier fielen mir die Augen zu
            case 1: {
                result = sdf.format(d[0]);
                break;
            }
            case 2: {
                result = sdf.format(d[1]);
                break;
            }
            default: {
                result = "";
                break;
            }
        }
        return result;
    }
}