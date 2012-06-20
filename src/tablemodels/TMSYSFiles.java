/*
 * OffenePflege
 * Copyright (C) 2011 Torsten Löhr
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
 */
package tablemodels;

import entity.files.SYSFiles;
import op.OPDE;
import op.tools.SYSTools;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * @author tloehr
 */
public class TMSYSFiles extends AbstractTableModel {
    ArrayList<SYSFiles> mymodel;

    public TMSYSFiles(ArrayList<SYSFiles> modelData) {
        mymodel = modelData;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Class getColumnClass(int column) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public SYSFiles getRow(int row) {
        return mymodel.get(row);
    }

    @Override
    public String getColumnName(int column) {
        String name;
        switch (column) {
            case 0: {
                name = "Datei";
                break;
            }
            case 1: {
                name = "Beschreibung";
                break;
            }
            default: {
                name = Integer.toString(column);
            }
        }
        return name;
    }

    @Override
    public int getRowCount() {
        int rowcount = 0;

        if (mymodel != null) {
            rowcount = mymodel.size();
        }
        return rowcount;
    }

    private String getAssignmentAsHTML(int row) {
        SYSFiles sysfile = mymodel.get(row);
        String result = "";
        boolean start = true;

        result += sysfile.getPbAssignCollection().isEmpty() ? "" : OPDE.lang.getString("nursingrecords.reports") + ": " + sysfile.getPbAssignCollection().size() + ", ";
        result += sysfile.getBwiAssignCollection().isEmpty() ? "" : OPDE.lang.getString("nursingrecords.information") + ": " + sysfile.getBwiAssignCollection().size() + ", ";
        result += sysfile.getVerAssignCollection().isEmpty() ? "" : OPDE.lang.getString("nursingrecords.prescription") + ": " + sysfile.getVerAssignCollection().size() + ", ";

        if (!result.isEmpty()) {
            result = result.substring(0, result.length() - 3);
        }

        return result;
    }

    @Override
    public Object getValueAt(int row, int column) {
        String value = "";
        switch (column) {
            case 0: {
                value += mymodel.get(row).getFilename() + ", ";
                value += OPDE.lang.getString("misc.msg.Size") + ": " + BigDecimal.valueOf(mymodel.get(row).getFilesize()).divide(new BigDecimal(1048576), 2, BigDecimal.ROUND_HALF_UP) + " mb";
                break;
            }
            case 1: {
                value += SYSTools.catchNull(mymodel.get(row).getBeschreibung());
                break;
            }
            default: {
                value = null;
            }
        }
        return value;
    }
}
