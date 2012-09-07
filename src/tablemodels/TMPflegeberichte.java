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
package tablemodels;

import entity.reports.NReport;
import entity.reports.NReportTools;
import op.tools.SYSTools;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Diese Klasse ist das TableModel für die Bewohner-Liste
 */
public class TMPflegeberichte
        extends AbstractTableModel {

    public static final int COL_PIT = 0;
    public static final int COL_Flags = 1;
    public static final int COL_HTML = 2;
//    public static final int COL_Text = 3;
    public static final int COL_BERICHT = 99;
    boolean showIDs;
    ArrayList<NReport> NReport = new ArrayList();


    public TMPflegeberichte(ArrayList<NReport> NReport, boolean showIDs) {
        this.showIDs = showIDs;
        this.NReport = NReport;
    }

    @Override
    public int getRowCount() {
        return NReport.size();
    }

    public ArrayList<NReport> getNReport() {
        return NReport;
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    public void setPflegebericht(int row, NReport bericht){
        NReport.set(row, bericht);
        fireTableRowsUpdated(row, row);
    }

    @Override
    public Class getColumnClass(int c) {
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int col) {
        Object result = "";
        NReport bericht = NReport.get(row);


        switch (col) {
            case COL_PIT: {
                result = SYSTools.toHTML(NReportTools.getDatumUndUser(bericht, showIDs, true));
                break;
            }
            case COL_Flags: {
                result = SYSTools.toHTML(NReportTools.getTagsAsHTML(bericht));
                break;
            }
            case COL_HTML: {
                result = SYSTools.toHTML(NReportTools.getAsHTML(bericht));
                break;
            }
            default: {
                result = bericht;
                break;
            }
        }

        return result;
    }
}

