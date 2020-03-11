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
package de.offene_pflege.op.care.sysfiles;

import de.offene_pflege.entity.files.SYSFiles;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author tloehr
 */
public class TMSYSFiles extends AbstractTableModel {
    public static final int COL_PIT = 0;
    public static final int COL_USER = 1;
    public static final int COL_FILE = 2;
    public static final int COL_DESCRIPTION = 3;

    ArrayList<SYSFiles> mymodel;

    public TMSYSFiles(ArrayList<SYSFiles> modelData) {
        mymodel = modelData;
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Class getColumnClass(int column) {


        Class thisclass;
               switch (column) {
                   case COL_PIT: {
                       thisclass = Date.class;
                       break;
                   }
                   case COL_USER: {
                       thisclass = String.class;
                       break;
                   }
                   case COL_FILE: {
                       thisclass = String.class;
                       break;
                   }
                   case COL_DESCRIPTION: {
                       thisclass = String.class;
                       break;
                   }
                   default: {
                       thisclass = String.class;
                   }
               }


        return thisclass;
    }

    public void setSYSFile(int row, SYSFiles sysfile) {
        mymodel.set(row, sysfile);
        fireTableRowsUpdated(row, row);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public SYSFiles getRow(int row) {
        return mymodel.get(row);
    }

    @Override
    public int getRowCount() {
        int rowcount = 0;

        if (mymodel != null) {
            rowcount = mymodel.size();
        }
        return rowcount;
    }

//    private String getAttachmentsAsHTML(int row) {
//        SYSFiles sysfile = mymodel.get(row);
//        String result = "";
//
//        result += sysfile.getNrAssignCollection().isEmpty() ? "" : SYSTools.xx("nursingrecords.reports") + " " + sysfile.getNrAssignCollection().size() + ", ";
//        result += sysfile.getBwiAssignCollection().isEmpty() ? "" : SYSTools.xx("nursingrecords.info") + " " + sysfile.getBwiAssignCollection().size() + ", ";
//        result += sysfile.getPreAssignCollection().isEmpty() ? "" : SYSTools.xx("nursingrecords.prescription") + " " + sysfile.getPreAssignCollection().size() + ", ";
//
//        String html = SYSTools.xx(PnlFiles.internalClassID+".Attachments")+": ";
//        if (result.isEmpty()) {
//            html += html = SYSTools.xx("misc.msg.none");
//        } else {
//            html += result; //result.substring(0, result.length() - 3);
//        }
//
//        return html;
//    }



    @Override
    public Object getValueAt(int row, int column) {
        Object value;
        switch (column) {
            case COL_PIT: {
                value = mymodel.get(row).getPit();
                break;
            }
            case COL_USER: {
                value = SYSTools.anonymizeUser(mymodel.get(row).getUser());
                break;
            }
            case COL_FILE: {
                String html = "";
                html += SYSConst.html_fontface;
                html += mymodel.get(row).getFilename() + ", ";
                html += SYSTools.xx("misc.msg.Size") + ": " + BigDecimal.valueOf(mymodel.get(row).getFilesize()).divide(new BigDecimal(1048576), 2, BigDecimal.ROUND_HALF_UP) + " mb";
//                value += ", " + getAttachmentsAsHTML(row);
                html += "</font>";
                value = html;
                break;
            }
            case COL_DESCRIPTION: {
                String html = "";
                html += SYSConst.html_fontface;
                html += "<p>" + SYSTools.catchNull(mymodel.get(row).getBeschreibung()) + "</p>";
                html += "</font>";
                value = html;
                break;
            }
            default: {
                value = null;
            }
        }
        return value;
    }
}
