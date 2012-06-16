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

import entity.BWerte;
import entity.BWerteTools;
import entity.Bewohner;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.table.AbstractTableModel;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Diese Klasse ist das TableModel für die Vitalwerte
 */
public class TMWerte
        extends AbstractTableModel {

    public static final int COL_PIT = 0;
    public static final int COL_CONTENT = 1;
    public static final int COL_COMMENT = 2;

    private boolean showids = false;
    private ArrayList<BWerte> content;
    private Bewohner bewohner;

    /**
     * Ein einfaches Tablemodel zur Anzeige der Bewohnerwerte für den gewünschten Bewohner. Rendert alles in HTML.
     *
     * @param from
     * @param bewohner
     * @param showedits
     * @param showids
     */
    public TMWerte(Date from, Bewohner bewohner, boolean showedits, boolean showids) {
        super();
        this.showids = showids;
        this.bewohner = bewohner;
        EntityManager em = OPDE.createEM();
        try {
            String sql =
                    " SELECT bw.BWID " +
                            " FROM BWerte bw" +
                            // Hier kommen die angehangen Dokumente hinzu
//                            " INNER JOIN " +
//                            " (" +
//                            " 	SELECT DISTINCT f1.BWID, ifnull(anzahl,0) anzahl" +
//                            " 	FROM BWerte f1" +
//                            " 	LEFT OUTER JOIN (" +
//                            " 		SELECT BWID, count(*) anzahl FROM SYSBWERTE2FILE" +
//                            " 		GROUP BY BWID" +
//                            " 		) fa ON fa.BWID = f1.BWID" +
//                            " 	WHERE f1.BWKennung=? AND f1.PIT >= ? AND f1.PIT <= ? " +
//                            " ) fia ON fia.BWID = bw.BWID" +
//                            // Hier die angehangenen Vorgänge
//                            " INNER JOIN " +
//                            " (" +
//                            " 	SELECT DISTINCT f2.BWID, ifnull(anzahl,0) anzahl" +
//                            " 	FROM BWerte f2" +
//                            " 	LEFT OUTER JOIN (" +
//                            " 		SELECT ForeignKey, count(*) anzahl FROM SYSBWERTE2VORGANG" +
//                            " 		GROUP BY BWID " +
//                            " 		) va ON va.BWID = f2.BWID" +
//                            " 	WHERE f2.BWKennung=? AND f2.PIT >= ? AND f2.PIT <= ? " +
//                            " ) vrg ON vrg.BWID = bw.BWID " +
                            " WHERE bw.BWKennung = ? AND bw.PIT >= ?  " +
                            (showedits ? "" : " AND bw.EditBy IS NULL ") +
                            " ORDER BY bw.PIT desc ";

            Query query = em.createNativeQuery(sql);
            query.setParameter(1, bewohner.getBWKennung());
            query.setParameter(2, from);

            List<BigInteger> rawlist = query.getResultList();
            content = new ArrayList<BWerte>(rawlist.size());

            int i = 0;
            for (BigInteger bigint : rawlist) {
                if (i % 100 == 0) {
                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.loading"), i, rawlist.size()));
                }

                content.add(em.find(BWerte.class, bigint.longValue()));
                i++;
            }

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
    }

    public int getRowCount() {
        return content.size();
    }

    public ArrayList<BWerte> getContent() {
        return content;
    }

    public BWerte getBWert(int row) {
        return content.get(row);
    }

    public void setBWert(int row, BWerte wert) {
        content.set(row, wert);
        fireTableRowsUpdated(row, row);
    }

    public int getColumnCount() {
        return 3;
    }

    public Class getColumnClass(int c) {
        return String.class;
    }

    public void cleanup() {
        content.clear();
    }

    public Object getValueAt(int row, int col) {

        String result = "";
//        String color = "";

        BWerte wert = content.get(row);

//        if (wert.isReplaced()) {
//            color = SYSConst.html_lightslategrey;
//        } else {
//            color = SYSCalendar.getHTMLColor4Schicht(SYSCalendar.ermittleSchicht(wert.getPit()));
//        }


        switch (col) {
            case COL_PIT: { // COL_DATUM
                result = BWerteTools.getPITasHTML(wert, showids, true);
                break;
            }
            case COL_CONTENT: {
                result = BWerteTools.getBWertAsHTML(wert, true);
                break;
            }
            case COL_COMMENT: {
                if (!SYSTools.catchNull(wert.getBemerkung()).isEmpty()) {
                    String color = "";
                    if (wert.isReplaced() || wert.isDeleted()) {
                        color = SYSConst.html_lightslategrey;
                    } else {
                        color = SYSCalendar.getHTMLColor4Schicht(SYSCalendar.ermittleSchicht(wert.getPit()));
                    }
                    result += "<font " + color + " " + SYSConst.html_arial14 + ">" + "<b>Bemerkung:</b> " + wert.getBemerkung() + "</font>";
                }
                break;
            }

            default: {
                result = null;
                break;
            }
        }

        return result;
    }
}

