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

import entity.process.QProcessElement;
import entity.process.QProcessTools;
import entity.process.PReport;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * @author tloehr
 */
public class TMElement extends AbstractTableModel {

    public static final int COL_CONTENT = 2;
    public static final int COL_PDCA = 1;
    public static final int COL_PIT = 0;
    public static final int COL_OPERATIONS = 2;
    private List mymodel;

    // Enthält den Tabellennamen. Dieses Array passt zum Wert in der gleichnamigen Spalte
    //public static final String[] tblidx = new String[]{"Tagesberichte", "Planung", "BHPVerordnung", "BWerte", "BWInfo"};
    public TMElement(List model) {

        this.mymodel = model;


//            String sql1 =
//                    " SELECT * FROM " +
//                    " (" +
//                    "   SELECT 0 tblidx, tbid pk, pit, t.UKennung ukennung, t.BWKennung, t.EKennung, t.Text, 0 Art, v.VAID " +
//                    "   FROM VorgangAssign v " +
//                    "   INNER JOIN Tagesberichte t ON t.tbid = v.ForeignKey " +
//                    "   WHERE v.TableName='Tagesberichte' AND v.VorgangID = ? " +
//                    " UNION" +
//                    "   SELECT 1 tblidx, p.PlanID pk, p.Von pit, p.AnUkennung ukennung, p.BWKennung, '', '', 0, v.VAID" +
//                    "   FROM VorgangAssign v " +
//                    "   INNER JOIN Planung p ON p.PlanID = v.ForeignKey " +
//                    "   WHERE v.TableName='Planung' AND v.VorgangID = ? " +
//                    " UNION " +
//                    "   SELECT 2 tblidx, b.VerID pk, b.AnDatum pit, b.AnUkennung ukennung, b.BWKennung, '', '', 0, v.VAID" +
//                    "   FROM VorgangAssign v " +
//                    "   INNER JOIN BHPVerordnung b ON b.VerID = v.ForeignKey " +
//                    "   WHERE v.TableName='BHPVerordnung' AND v.VorgangID = ? " +
//                    " UNION " +
//                    "   SELECT 3 tblidx, b.BWID pk, pit, b.Ukennung ukennung, b.BWKennung, '', '', 0, v.VAID " +
//                    "   FROM VorgangAssign v " +
//                    "   INNER JOIN BWerte b ON b.BWID = v.ForeignKey " +
//                    "   WHERE v.TableName='BWerte' AND v.VorgangID = ? " +
//                    " UNION " +
//                    "   SELECT 4 tblidx, b.BWInfoID pk, b.Von, b.AnUkennung ukennung, b.BWKennung, '', '', 0, v.VAID " +
//                    "   FROM VorgangAssign v " +
//                    "   INNER JOIN BWInfo b ON b.BWInfoID = v.ForeignKey " +
//                    "   WHERE v.TableName='BWInfo' AND v.VorgangID = ? " +
//                    " UNION " +
//                    "   SELECT 5 tblidx, vb.VBID pk, vb.Datum, vb.UKennung ukennung, '', '', '', Art, vb.VBID VAID" +
//                    "   FROM PReport vb " +
//                    "   WHERE vb.VorgangID = ? " +
//                    (system ? "" : "AND Art = 0 ") +
//                    " ) as va " +
//                    "   ORDER BY pit ";
//            PreparedStatement stmt1 = OPDE.getDb().db.prepareStatement(sql1);
//            stmt1.setLong(1, vorgangid);
//            stmt1.setLong(2, vorgangid);
//            stmt1.setLong(3, vorgangid);
//            stmt1.setLong(4, vorgangid);
//            stmt1.setLong(5, vorgangid);
//            stmt1.setLong(6, vorgangid);
//            rs = stmt1.executeQuery();
//
//            rs.last();
//            cache = new ArrayList(rs.getRow());
//            for (int i = 0; i <= rs.getRow(); i++) {
//                cache.add(null);
//            }

    }

//    @Override
//    public void removeRow(int row) {
//
//        Object element = mymodel.get(row);
//
//        em.getTransaction().begin();
//        try {
//            if (element instanceof PReport) {
//                sourcemodel.remove(element);
//                mymodel.remove(element);
//                pdca.remove(row);
//                em.remove(element);
//                fireTableRowsDeleted(row, row);
//            } else {
//                OPDE.debug("nothing to delete... yet");
//            }
//            em.getTransaction().commit();
//
//        } catch (Exception e) {
//            OPDE.fatal(e);
//            em.getTransaction().rollback();
//        }
//    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public int getRowCount() {
        int rowcount = 0;

        if (mymodel != null) {
            rowcount = mymodel.size();
        }
        return rowcount;
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Class getColumnClass(int c) {
        return String.class;
    }

    /**
     * Gibt das Element Objekt einer bestimmten Zeile zurück.
     *
     * @param row
     * @return
     */
//    public QProcessElement getElement(int row) {
//        return mymodel.get(row);
//    }


//    private String getBWInfo(long bwinfoid) {
//        BWInfo bwinfo = new BWInfo(bwinfoid);
//        ArrayList content = bwinfo.getAttribute();
//        HashMap attrib = (HashMap) content.get(0); // Diese BWInfo hat nur eine Zeile
//        String result = "";
//
//        String color = "green";
//        if (attrib.containsKey("unbeantwortet")) {
//            color = "red";
//        }
//
//        result += "<font color=\"" + color + "\"><b>" + attrib.get("bwinfokurz").toString() + "</b></font>";
//        result += attrib.get("html").toString();
//        return result;
//    }

//    public String getTableAsHTML() {
//        String html = "<h2>Einträge zum Vorgang</h2>";
//
//        html += "<table border=\"1\" cellspacing=\"0\"><tr>"
//                + "<th style=\"width:20%\">Info</th><th style=\"width:80%\">Eintrag</th></tr>";
//        for (int v = 0; v < getRowCount(); v++) {
//
//            html += "<tr>";
//            html += "<td>" + getValueAt(v, COL_PIT).toString() + "</td>";
//            html += "<td>" + getValueAt(v, COL_CONTENT).toString() + "</td>";
//            html += "</tr>";
//
//        }
//        html += "</table>";
//
//        return html;
//    }

    @Override
    public Object getValueAt(int r, int c) {
        Object result = "";
        QProcessElement elementQ = null;
        short pdca = QProcessTools.PDCA_OFF;

        if (mymodel.get(r) instanceof Object[]){
            elementQ = (QProcessElement) ((Object[]) mymodel.get(r))[0];
            pdca = (Short) ((Object[]) mymodel.get(r))[1];
        } else if (mymodel.get(r) instanceof PReport){
            elementQ = (QProcessElement) mymodel.get(r);
            pdca = ((PReport) elementQ).getPdca();
        }

        switch (c) {
            case COL_CONTENT: {
                result = elementQ.getContentAsHTML();
                break;
            }
            case COL_PIT: {
                result = elementQ.getPITAsHTML();
                break;
            }
            case COL_PDCA: {
                result = QProcessTools.PDCA[pdca];
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
