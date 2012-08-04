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

import entity.Bewohner;
import entity.planung.DFN;
import entity.planung.DFNTools;
import op.OPDE;
import op.tools.DlgException;
import op.tools.SYSTools;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.table.AbstractTableModel;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author tloehr
 */
public class TMDFN
        extends AbstractTableModel {

    public static final int COL_BEZEICHNUNG = 0;
    public static final int COL_ZEIT = 1;
    public static final int COL_STATUS = 2;
    public static final int COL_UKENNUNG = 3;
    public static final int COL_BEMDFN = 4;

    protected List<DFN> listeDFN;
    protected Comparator comparator;

    /**
     * @param schicht entsprechen OC_Const.ZEIT
     */
    public TMDFN(Bewohner bewohner, Date datum, int schicht) {
        super();

//        if (schicht != SYSConst.ZEIT_ALLES) {
//            filter = " AND ((dfn.SZeit >= ? AND dfn.SZeit <= ?) OR " +
//                    " (dfn.SZeit = 0 AND TIME(dfn.Soll) >= ? AND TIME(dfn.Soll) <= ?)) ";
//        }

        EntityManager em = OPDE.createEM();
        try {

            Query query = em.createQuery(" SELECT dfn " +
                    " FROM DFN dfn " +
                    " WHERE dfn.bewohner = :bewohner AND dfn.soll >= :von AND dfn.soll <= :bis " +
                    " ORDER BY dfn.nursingProcess.kategorie.bezeichnung, dfn.nursingProcess.stichwort, dfn.intervention.bezeichnung ");

            query.setParameter("bewohner", bewohner);
            query.setParameter("von", new DateTime(datum).toDateMidnight().toDate());
            query.setParameter("bis", new DateTime(datum).toDateMidnight().plusDays(1).toDateTime().minusSeconds(1).toDate());

            listeDFN = query.getResultList();

        } catch (Exception se) {
            OPDE.fatal(se);
        } finally {
            em.close();
        }
    }

    public List<DFN> getListeDFN() {
        return listeDFN;
    }

    public int getRowCount() {
        return listeDFN.size();
    }

    public int getColumnCount() {
        return 5;
    }

    public Object getValueAt(int row, int col) {
        Object result = null;
        DFN dfn = listeDFN.get(row);

        switch (col) {
            //case COL_MassID : {result = rs.getString("MassID"); break;}
            case COL_BEZEICHNUNG: {
                result = SYSTools.toHTML(DFNTools.getInterventionAsHTML(dfn));
                break;
            }
            case COL_ZEIT: {
                if (dfn.getSollZeit() == 0) { // Uhrzeit
                    result = DateFormat.getTimeInstance(DateFormat.SHORT).format(dfn.getSoll());
                } else {
                    result = DFNTools.SOLLZEITTEXT[dfn.getSollZeit()];
                }
                break;
            }
            case COL_STATUS: {
                result = dfn.getStatus();
                break;
            }
            case COL_UKENNUNG: {
                result = dfn.getUser() != null ? dfn.getUser().getUKennung() : "";
                break;
            }
            case COL_BEMDFN: {
                result = SYSTools.catchNull(dfn.getInterventionSchedule().getBemerkung(), "<b>Bemerkung:</b> ", "");
                break;
            }
            default: {
                result = "";
            }
        }

        return result;
    }
}
