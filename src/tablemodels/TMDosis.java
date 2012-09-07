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

import entity.prescription.Prescriptions;
import entity.prescription.PrescriptionScheduleTools;

import javax.swing.table.AbstractTableModel;


/**
 * @author tloehr
 */
public class TMDosis
        extends AbstractTableModel {
    public static final int COL_Dosis = 0;

    String anwendung;

    Prescriptions verordnung = null;
    //VerordnungPlanung[] planungen;


    public TMDosis(String anwtext, Prescriptions verordnung) {
        super();

        this.anwendung = anwtext;
        this.verordnung = verordnung;
    }

    public int getRowCount() {
        return verordnung.getPrescriptionSchedule().size();
    }

    public int getColumnCount() {
        return 1;
    }

    public Class getColumnClass(int c) {
        return String.class;
    }



    public Object getValueAt(int r, int c) {
        String result = "";
        switch (c) {
            case COL_Dosis: {
                result = PrescriptionScheduleTools.getDosisAsHTML(verordnung.getPrescriptionSchedule().get(r), null, true);
                break;
            }
            default: {
                result = "";
            }
        }
        return result;
    }
}