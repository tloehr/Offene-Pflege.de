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


import entity.info.Resident;
import entity.info.ResidentTools;
import op.OPDE;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 * @author tloehr
 */
public class TMTGStat extends AbstractTableModel {
    public static final int COL_BW = 0;
    public static final int COL_SALDO = 1;
    ResultSet rs;
    PreparedStatement stmt;
    List<Object[]> listData;

    public TMTGStat(boolean alle) {

        EntityManager em = OPDE.createEM();

        Query query = em.createQuery(" " +
                " SELECT b, SUM(k.betrag) FROM Resident b " +
                " LEFT JOIN b.konto k " +
                (alle ? "" : " WHERE b.station IS NOT NULL ") +
                " GROUP BY b " +
                " ORDER BY b.nachname, b.vorname, b.bWKennung ");

        listData = query.getResultList();
    }

    @Override
    public int getRowCount() {
        return listData.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    public Class getColumnClass(int col) {
        if (col == COL_SALDO) {
            return BigDecimal.class;
        } else {
            return String.class;
        }
    }

    public Resident getBewohner(int row){
        return (Resident) listData.get(row)[0];
    }

    public Object getValueAt(int row, int col) {
        Object result;
        Resident bewohner = getBewohner(row);
        BigDecimal saldo = listData.get(row)[1] == null ? BigDecimal.ZERO : (BigDecimal) listData.get(row)[1];
        switch (col) {
            case COL_BW: {
                result = SYSTools.htmlUmlautConversion("<b>"+ ResidentTools.getBWLabel1(bewohner)+"</b> ["+bewohner.getBWKennung()+"]");
                break;
            }
            case COL_SALDO: {
                result = saldo.setScale(2, BigDecimal.ROUND_HALF_UP);
                break;
            }
            default: {
                result = "";
                break;
            }
        }
        return result;
    }

} // BWTableModel