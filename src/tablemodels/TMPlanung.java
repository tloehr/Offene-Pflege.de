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
package tablemodels;

/**
 *
 * @author tloehr
 */

import entity.planung.MassTermin;
import entity.planung.MassTerminTools;
import entity.planung.Planung;
import op.OPDE;
import op.tools.SYSTools;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * @author tloehr
 */
public class TMPlanung extends AbstractTableModel {

    private ArrayList<MassTermin> planungsliste;
    public static final int COL_TXT = 0;


    public TMPlanung(Planung planung) {
        super();
        this.planungsliste = new ArrayList<MassTermin>(planung.getMassnahmen());
    }

    public int getRowCount() {
        return planungsliste.size();
    }

    public int getColumnCount() {
        return 1;
    }

    @Override
    public Class getColumnClass(int c) {
        return String.class;
    }

    public Object getValueAt(int row, int col) {
        Object result;

        switch (col) {
            case COL_TXT: {
                String html = "";
                html += "<b>" + planungsliste.get(row).getMassnahme().getBezeichnung() + "</b> (" + planungsliste.get(row).getDauer().toPlainString() + " " + OPDE.lang.getString("misc.msg.Minutes") + ")<br/>";
                html += MassTerminTools.getTerminAsHTML(planungsliste.get(row));
                result = SYSTools.toHTML(html);
                break;
            }
            default: {
                result = "";
            }
        }
        return result;
    }
}
