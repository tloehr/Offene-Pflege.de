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

package de.offene_pflege.op.care.med.structure;

import de.offene_pflege.entity.EntityTools;
import de.offene_pflege.entity.info.ResidentTools;
import de.offene_pflege.entity.prescription.*;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.threads.DisplayManager;
import de.offene_pflege.op.tools.SYSTools;
import lombok.extern.log4j.Log4j2;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OneToMany;
import javax.persistence.OptimisticLockException;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDateTime;
import java.util.List;


/**
 * @author tloehr
 */
@Log4j2
public class TMMedOrders extends AbstractTableModel {
    public static final int COL_TradeForm = 0;
    public static final int COL_Resident = 1;
    public static final int COL_GP = 2;
    public static final int COL_HOSPITAL = 3;
    public static final int COL_note = 4;
    public static final int COL_complete = 5;
    private final List<MedOrder> medOrderList;

    public TMMedOrders(List<MedOrder> medOrderList) {
        this.medOrderList = medOrderList;
    }

    @Override
    public int getRowCount() {
        return medOrderList.size();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public Class getColumnClass(int col) {
        switch (col) {
            case COL_TradeForm:
            case COL_note:
            case COL_Resident: {
                return String.class;
            }
            case COL_GP: {
                return GP.class;
            }
            case COL_HOSPITAL: {
                return Hospital.class;
            }
            case COL_complete: {
                return Boolean.class;
            }
            default: {
                return null;
            }
        }
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        MedOrder medOrder = medOrderList.get(row);
        if (column == COL_complete) {
            Boolean complete = (Boolean) aValue;
            medOrder.setClosed_on(complete ? LocalDateTime.now() : null);
            medOrder.setClosed_by(complete ? OPDE.getLogin().getUser() : null);
        } else if (column == COL_GP) {
            GP gp = (GP) aValue;
            medOrder.setGp(gp);
        } else if (column == COL_note) {
            medOrder.setNote(aValue.toString().trim());
        }
        medOrderList.set(row, EntityTools.merge(medOrder));
        fireTableCellUpdated(row, column);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == COL_complete || column == COL_note || column == COL_GP;
    }

    public MedOrder get(int row) {
        return medOrderList.get(row);
    }

    public void delete(int row) {
        medOrderList.remove(row);
        fireTableRowsDeleted(row, row);
    }

    @Override
    public Object getValueAt(int row, int col) {
        Object result;
        MedOrder medOrder = medOrderList.get(row);
        switch (col) {
            case COL_TradeForm: {
                result = TradeFormTools.toPrettyHTML(medOrder.getTradeForm());
                break;
            }
            case COL_Resident: {
                result = ResidentTools.getNameAndFirstname(medOrder.getResident()) + String.format(" [%s]", medOrder.getResident().getId());
                break;
            }
            case COL_GP: {
                result = medOrder.getGp() != null ? GPTools.getFullName(medOrder.getGp()) : "--";
                break;
            }
            case COL_note: {
                result = medOrder.getNote();
                break;
            }
            case COL_HOSPITAL: {
                result = medOrder.getHospital() != null ? HospitalTools.getFullName(medOrder.getHospital()) : "--";
                break;
            }
            case COL_complete: {
                result = Boolean.valueOf(MedOrderTools.is_closed(medOrder));
                break;
            }
            default: {
                result = "";
            }
        }
        return result;
    }
}