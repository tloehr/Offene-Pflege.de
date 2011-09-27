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

import entity.SYSFiles;
import entity.Sysbw2file;
import entity.Sysbwi2file;
import entity.Syspb2file;
import entity.Sysver2file;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import op.tools.BeanTableModel;
import op.tools.HTMLTools;
import op.tools.SYSTools;

/**
 *
 * @author tloehr
 */
public class TMSYSFiles extends DefaultTableModel {

    private BeanTableModel<SYSFiles> mymodel;
    private HashMap<Integer, String> assignmentCache;

    public TMSYSFiles(List<SYSFiles> modelData) {
        mymodel = new BeanTableModel(SYSFiles.class, modelData);
        assignmentCache = new HashMap<Integer, String>();
    }

    @Override
    public Object getValueAt(int row, int column) {
        SYSFiles sysfile = mymodel.getRow(row);
        String value = "";
        switch (column) {
            case 0: {
                value = getAssignmentAsHTML(row);
                break;
            }
            case 1: {
                value = "<p>" + sysfile.getFilename() + "</p>";
                break;
            }
            case 2: {
                value = Double.toString(SYSTools.roundScale2(BigDecimal.valueOf(sysfile.getFilesize()).doubleValue() / 1024 / 1024)) + " mb";
                break;
            }
//            case 2: {
//                value = rezept.getVegetarisch();
//                break;
//            }
            default: {
                value = null;
            }
        }
        return value;
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Class<?> getColumnClass(int column) {
        Class<?> myclass;
        switch (column) {
            case 0: {
                myclass = String.class;
                break;
            }
//            case 1: {
//                myclass = Boolean.class;
//                break;
//            }
//            case 2: {
//                myclass = Boolean.class;
//                break;
//            }
            default: {
                myclass = String.class;
            }
        }
        return myclass;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public SYSFiles getRow(int row) {
        return mymodel.getRow(row);
    }

    @Override
    public String getColumnName(int column) {
        String name;
        switch (column) {
            case 0: {
                name = "Zuordnungen";
                break;
            }
            case 1: {
                name = "Dateiname";
                break;
            }
            case 2: {
                name = "Dateigröße";
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
            rowcount = mymodel.getRowCount();
        }
        return rowcount;
    }

    String getAssignmentAsHTML(int row) {
        SYSFiles sysfile = mymodel.getRow(row);
        String result = "";
        boolean start = true;

        // Pflegeberichte
        start = true;
        List<Syspb2file> pbAssignments = (List<Syspb2file>) sysfile.getPbAssignCollection();
        Iterator<Syspb2file> it2 = pbAssignments.iterator();
        while (it2.hasNext()) {
            if (start) {
                start = false;
                result += "<tr><th colspan=\"2\">Pflegebericht</th></tr>";
            }
            Syspb2file assign = it2.next();
            result += HTMLTools.getTableRow(assign.getBemerkung(), DateFormat.getDateTimeInstance().format(assign.getPflegebericht().getPit()));
        }

        // BWInfos
        start = true;
        List<Sysbwi2file> bwiAssignments = (List<Sysbwi2file>) sysfile.getBwiAssignCollection();
        Iterator<Sysbwi2file> it1 = bwiAssignments.iterator();
        while (it1.hasNext()) {
            if (start) {
                start = false;
                result += "<tr><th colspan=\"2\">Bewohner Informationen</th></tr>";
            }
            Sysbwi2file assign = it1.next();
            result += HTMLTools.getTableRow(assign.getBemerkung(), assign.getBwinfo().getBwinfotyp().getBWInfoKurz());
        }


        // Verordnungen
        start = true;
        List<Sysver2file> verAssignments = (List<Sysver2file>) sysfile.getVerAssignCollection();
        Iterator<Sysver2file> it3 = verAssignments.iterator();
        while (it3.hasNext()) {
            if (start) {
                start = false;
                result += "<tr><th colspan=\"2\">Ärztliche Verordnungen</th></tr>";
            }
            Sysver2file assign = it3.next();
            result += HTMLTools.getTableRow(assign.getBemerkung(), DateFormat.getDateTimeInstance().format(assign.getVerordnung().getAnDatum()));
        }

        // Direkte Zuordnung
        start = true;
        List<Sysbw2file> bwAssignments = (List<Sysbw2file>) sysfile.getBwAssignCollection();
        Iterator<Sysbw2file> it4 = bwAssignments.iterator();
        while (it4.hasNext()) {
            if (start) {
                start = false;
                result += "<tr><th colspan=\"2\">Direkte Zuordnung zu BewohnerIn</th></tr>";
            }
            Sysbw2file assign = it4.next();
            result += HTMLTools.getTableRow(assign.getBemerkung(), DateFormat.getDateTimeInstance().format(assign.getPit()));
        }


        if (!result.equals("")) {
            result = "<table border=\"1\">" + result + "</table>";
        }

        return result;
    }
}
