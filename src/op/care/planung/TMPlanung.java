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
package op.care.planung;

/**
 *
 * @author tloehr
 */
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author tloehr
 */
public class TMPlanung extends AbstractTableModel {

    private ArrayList content;
    public static final int COL_ID = 1;
    public static final int COL_TXT = 0;
    public static final int COL_ART = 2;
    public static final int COL_PKID = 3;

    TMPlanung(ArrayList c) {
        super();
        this.content = c;
    }

    public int getRowCount() {
        return content.size();
    }

    public int getColumnCount() {
        return 1;
    }

    @Override
    public Class getColumnClass(int c) {
        return String.class;
    }

    public Object getValueAt(int r, int c) {
        Object result;
        Object[] o = (Object[]) content.get(r);
        int art = ((Integer) o[0]).intValue();
        //long relid = ((Long) o[1]).longValue();
        //Object obj = o[1];
        String txt = o[1].toString();
        Object pkid = o[2];

        switch (c) {
            case COL_TXT: {
                result = txt;
                break;
            }
            case COL_ART: {
                result = art;
                break;
            }
            case COL_PKID: {
                result = pkid;
                break;
            }
            case COL_ID: {
//                if (OPDE.ocprops.getProperty("debug").equalsIgnoreCase("true")) {
//                    result = pkid.toString() + " " + DBHandling.ARTEN[art];
//                } else {
//                    
//                }
                result = DBHandling.ARTEN[art];
                break;
            }
            default: {
                result = "";
            }
        }
        return result;
    }

    public void cleanup() {
        content.clear();
    }
}
