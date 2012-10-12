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
package op.care.uebergabe;

import entity.*;
import entity.reports.NReport;
import entity.reports.NReportTools;
import op.OPDE;
import op.tools.SYSCalendar;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author tloehr
 */
public class TMUebergabe
        extends AbstractTableModel {

    public static final int COL_PIT = 0;
    public static final int COL_INFO = 1;
    public static final int COL_HTML = 2;
    public static final int COL_ACKN = 3;
    public static final int COL_BERICHT = 99;
    public static final int SORT_NAME = 0;
    public static final int SORT_UHRZEIT = 1;
    public static final int LIST_BERICHT = 0;
    public static final int LIST_ACKNOWLEDGED = 1;

    private ArrayList berichte;
//    private ImageIcon iconOK, iconQuestion;

    public TMUebergabe(Date datum, Homes einrichtung) {
        super();

//        iconOK = new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/apply.png"));

        EntityManager em = OPDE.createEM();

        // TODO: SQLMAPPING
//                 @SqlResultSetMapping(name = "Handovers.findByEinrichtungAndDatumAndAckUserResultMapping", entities =
//        @EntityResult(entityClass = Handovers.class), columns =
//        @ColumnResult(name = "num"))
        Query queryUB = em.createNativeQuery(" SELECT u.*, ifnull(u2u.num, 0) num FROM Uebergabebuch u "
                + " LEFT OUTER JOIN ( SELECT uebid, count(*) num FROM Uebergabe2User WHERE UKennung=? GROUP BY uebid, UKennung) as u2u ON u2u.UEBID = u.UEBID "
                + " WHERE "
                + "     u.EID = ? "
                + "     AND u.PIT >= ? AND u.PIT <= ? "
                + " GROUP BY u.UEBID "
                + " ORDER BY u.PIT DESC");
        queryUB.setParameter(1, OPDE.getLogin().getUser().getUID());
        queryUB.setParameter(2, einrichtung.getEID());
        queryUB.setParameter(3, new Date(SYSCalendar.startOfDay(datum)));
        queryUB.setParameter(4, new Date(SYSCalendar.endOfDay(datum)));

//                @SqlResultSetMapping(name = "NReport.findByEinrichtungAndDatumAndAckUserResultMapping", entities =
//        @EntityResult(entityClass = NReport.class), columns =
//        @ColumnResult(name = "num")),

        Query queryPB = em.createNativeQuery(" SELECT p.*, ifnull(p2u.num, 0) num FROM Pflegeberichte p "
                + " INNER JOIN Bewohner bw ON bw.BWKennung = p.BWKennung "
                + " INNER JOIN PB2TAGS tag ON tag.PBID = p.PBID "
                + " INNER JOIN Station stat ON stat.StatID = bw.StatID "
                + " LEFT OUTER JOIN ( SELECT pbid, count(*) num FROM PB2User WHERE UKennung = ? GROUP BY pbid, ukennung ) AS p2u ON p2u.PBID = p.PBID "
                + " WHERE "
                + "     stat.EID = ? "
                + "     AND p.PIT >= ? AND p.PIT <= ? "
                + "     AND tag.PBTAGID = 1 "
                + "     AND p.editBy IS NULL "
                + " GROUP BY p.PBID "
                + " ORDER BY p.PIT DESC");
        queryPB.setParameter(1, OPDE.getLogin().getUser().getUID());
        queryPB.setParameter(2, einrichtung.getEID());
        queryPB.setParameter(3, new Date(SYSCalendar.startOfDay(datum)));
        queryPB.setParameter(4, new Date(SYSCalendar.endOfDay(datum)));

        berichte = new ArrayList();
        berichte.addAll(queryUB.getResultList());
        berichte.addAll(queryPB.getResultList());

        em.close();

    }

    @Override
    public int getRowCount() {
        return berichte.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    public ArrayList getBerichte() {
        return berichte;
    }

    @Override
    public Class getColumnClass(int c) {
        return String.class;
//            if (1 >= c+1 && c+1 <= 4) return String.class;
//            return Boolean.class;
    }

    @Override
    public Object getValueAt(int row, int col) {
        Object result = null;
        OPDE.debug(berichte);
        Object[] bericht = (Object[]) berichte.get(row);
        // Zur Info
        // bericht[LIST_BERICHT] enthält immer das Berichte Objekt (entweder Ubergabebuch oder NReport)
        // bericht[1] ist 1, wenn der aktuelle User den Bericht bestätigt hat. 0 sonst.

        if (bericht[LIST_BERICHT] instanceof Handovers) {
            switch (col) {
                case COL_PIT: {
                    result = HandoversTools.getDatumUndUser((Handovers) bericht[LIST_BERICHT], false);
                    break;
                }
                case COL_INFO: {
                    result = HandoversTools.getEinrichtungAsHTML((Handovers) bericht[LIST_BERICHT]);
                    break;
                }
                case COL_HTML: {
                    if (((Long) bericht[LIST_ACKNOWLEDGED]).longValue() == 0){
                        result = "<font color=\"red\"><b>?</b> ";
                    } else {
                        result = "<font color=\"green\"><b>OK</b> ";
                    }

                    result = result + HandoversTools.getAsHTML((Handovers) bericht[LIST_BERICHT]) + "</font>";
                    break;
                }
                case COL_ACKN: { // Vom aktuellen User bereits gesehen ?
                    result = ((Long) bericht[LIST_ACKNOWLEDGED]).longValue() == 0;
                    break;
                }
                default: {
                    result = bericht[LIST_BERICHT];
                    break;
                }
            }
        } else {
            switch (col) {
                case COL_PIT: {
                    result = NReportTools.getDatumUndUser((NReport) bericht[LIST_BERICHT], false, false);
                    break;
                }
                case COL_INFO: {
                    result = NReportTools.getBewohnerName((NReport) bericht[LIST_BERICHT]);
                    break;
                }
                case COL_HTML: {
                    if (((Long) bericht[LIST_ACKNOWLEDGED]).longValue() == 0){
                                           result = "<font color=\"red\"><b>?</b> ";
                                       } else {
                                           result = "<font color=\"green\"><b>OK</b> ";
                                       }

                    result = result + NReportTools.getAsHTML((NReport) bericht[LIST_BERICHT], null) + "</font>";
//                    result = ;
                    break;
                }
                case COL_ACKN: { // Vom aktuellen User bereits gesehen ?
                    result = ((Long) bericht[LIST_ACKNOWLEDGED]).longValue() == 0;
                    break;
                }
                default: {
                    result = bericht[LIST_BERICHT];
                    break;
                }
            }
        }

        return result;
    }
}
