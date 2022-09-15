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
import de.offene_pflege.op.tools.HTMLTools;
import de.offene_pflege.op.tools.JavaTimeConverter;
import de.offene_pflege.op.tools.SYSTools;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.html.HTML;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


/**
 * @author tloehr
 */
@Log4j2
public class TMMedOrders extends AbstractTableModel {
    public static final int COL_TradeForm = 0;
    public static final int COL_Resident = 1;
    public static final int COL_ORDER_DATE = 2;
    public static final int COL_WHERE_TO_ORDER = 3;
    public static final int COL_note = 4;
    public static final int COL_complete = 5;
    private final List<MedOrder> medOrderList;
    private final String[] header = new String[]{"Medikament", "Bewohner:in", "Datum", "Arzt/KH", "Text", "ok"};

    public String[] getHeader() {
        return header;
    }

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
            case COL_ORDER_DATE:
            case COL_Resident: {
                return String.class;
            }
            case COL_WHERE_TO_ORDER: {
                return Object.class;
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
    public String getColumnName(int column) {
        return header[column];
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        MedOrder medOrder = medOrderList.get(row);
        if (column == COL_complete) {
            Boolean complete = (Boolean) aValue;
            medOrder.setClosed_on(complete ? LocalDateTime.now() : null);
            medOrder.setClosed_by(complete ? OPDE.getLogin().getUser() : null);
            if (!complete) medOrder.setCreated_by(OPDE.getLogin().getUser());
        } else if (column == COL_WHERE_TO_ORDER) {
            if (aValue instanceof GP) {
                medOrder.setGp((GP) aValue);
                medOrder.setHospital(null);
            } else {
                medOrder.setGp(null);
                medOrder.setHospital((Hospital) aValue);
            }
            medOrder.setCreated_by(OPDE.getLogin().getUser());
        } else if (column == COL_note) {
            medOrder.setNote(aValue.toString().trim());
            medOrder.setCreated_by(OPDE.getLogin().getUser());

        }
        medOrderList.set(row, EntityTools.merge(medOrder));
        fireTableCellUpdated(row, column);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (column == COL_complete) return true;
        if (medOrderList.get(row).getClosed_by() != null) return false;
        return column == COL_note || column == COL_WHERE_TO_ORDER;
    }

    public MedOrder get(int row) {
        return medOrderList.get(row);
    }

    public void delete(int row) {
        MedOrder medOrder = medOrderList.get(row);
        EntityTools.delete(medOrder);
        medOrderList.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public List<MedOrder> getMedOrderList() {
        return medOrderList;
    }

    @Override
    public Object getValueAt(int row, int col) {
        Object result;
        MedOrder medOrder = medOrderList.get(row);
        switch (col) {
            case COL_TradeForm: {
                result = TradeFormTools.toPrettyHTML(medOrder.getTradeForm());
                if (medOrder.getClosed_by() != null) result = HTMLTools.strike(result.toString());
                break;
            }
            case COL_Resident: {
                result = ResidentTools.getNameAndFirstname(medOrder.getResident()) + String.format(" [%s]", medOrder.getResident().getId());
                break;
            }
            case COL_WHERE_TO_ORDER: {
                result = MedOrderTools.get_where_to_order(medOrder);
                break;
            }
            case COL_note: {
                result = medOrder.getNote();
                break;
            }
            case COL_ORDER_DATE: {
                result = DateFormat.getDateInstance(DateFormat.SHORT).format(JavaTimeConverter.toDate(medOrder.getCreated_on()));
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