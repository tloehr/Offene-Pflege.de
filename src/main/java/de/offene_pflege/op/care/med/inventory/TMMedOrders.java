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

package de.offene_pflege.op.care.med.inventory;

import de.offene_pflege.entity.EntityTools;
import de.offene_pflege.entity.info.ResidentTools;
import de.offene_pflege.entity.prescription.*;
import de.offene_pflege.entity.system.OPUsers;
import de.offene_pflege.entity.system.SYSLogin;
import de.offene_pflege.entity.system.UsersTools;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.ButtonAppearance;
import de.offene_pflege.op.tools.HTMLTools;
import de.offene_pflege.op.tools.JavaTimeConverter;
import de.offene_pflege.op.tools.SYSConst;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


/**
 * @author tloehr
 */
@Log4j2
public class TMMedOrders extends AbstractTableModel {
    public static final int COL_TradeForm = 0;
    public static final int COL_Resident = 1;
    public static final int COL_ORDER_INFO = 2;
    public static final int COL_WHERE_TO_ORDER = 3;
    public static final int COL_CONFIRMED = 4;
    public static final int COL_DELETE = 5;
    public static final int COL_complete = 6;

    private final List<MedOrder> medOrderList;
    private final boolean is_allowed_to_update;
    private final boolean is_allowed_to_delete;
    private final String[] header = new String[]{"Medikament/Text", "Bewohner:in", "Datum", "Arzt/KH", "?", "X", "√"};

    public String[] getHeader() {
        return header;
    }

    public TMMedOrders(List<MedOrder> medOrderList, boolean is_allowed_to_update, boolean is_allowed_to_delete) {
        this.medOrderList = medOrderList;
        this.is_allowed_to_update = is_allowed_to_update;
        this.is_allowed_to_delete = is_allowed_to_delete;
    }

    @Override
    public int getRowCount() {
        return medOrderList.size();
    }

    @Override
    public int getColumnCount() {
        return 7;
    }

    @Override
    public Class getColumnClass(int col) {
        switch (col) {
            case COL_TradeForm:
            case COL_ORDER_INFO:
            case COL_CONFIRMED:
            case COL_complete:
            case COL_DELETE:
            case COL_Resident: {
                return String.class;
            }
            case COL_WHERE_TO_ORDER: {
                return Object.class;
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
        if (column == COL_CONFIRMED) return;
        if (column == COL_complete) return;
        MedOrder medOrder = medOrderList.get(row);
        if (column == COL_WHERE_TO_ORDER) {
            if (aValue instanceof GP) {
                medOrder.setGp((GP) aValue);
                medOrder.setHospital(null);
            } else {
                medOrder.setGp(null);
                medOrder.setHospital((Hospital) aValue);
            }
            medOrder.setCreated_by(OPDE.getLogin().getUser());
        }
//        else if (column == COL_note) {
//            medOrder.setNote(StringUtils.abbreviate(aValue.toString().trim(), 200));
//            medOrder.setCreated_by(OPDE.getLogin().getUser());
//        }
        medOrderList.set(row, EntityTools.merge(medOrder));
        if (column == COL_complete) fireTableCellUpdated(row, COL_TradeForm);
        fireTableCellUpdated(row, column);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (!is_allowed_to_update) return false;
        if (column == COL_complete) return true;
        if (column == COL_CONFIRMED) return true;
        if (column == COL_DELETE) return true;
        if (MedOrderTools.is_closed(medOrderList.get(row))) return false;
        return column == COL_WHERE_TO_ORDER;
    }

    public MedOrder get(int row) {
        return medOrderList.get(row);
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
                result = medOrder.getTradeForm() != null ? TradeFormTools.toPrettyHTML(medOrder.getTradeForm()) : "--";
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
            case COL_ORDER_INFO: {
                result = DateFormat.getDateInstance(DateFormat.SHORT).format(JavaTimeConverter.toDate(medOrder.getCreated_on())) + "\n";
                result += medOrder.getCreated_by().toString();

                if (MedOrderTools.get_confirmed_by(medOrder).isPresent()) {
                    result += "\ngeprüft: " + MedOrderTools.get_confirmed_by(medOrder).get().toString();
                }
                break;
            }
            case COL_DELETE: {
                result = new ButtonAppearance() {
                    @Override
                    public Icon get_icon() {
                        return SYSConst.icon22delete;
                    }

                    @Override
                    public boolean is_enabled() {
                        return is_allowed_to_delete;
                    }
                };
                break;
            }
            case COL_complete: {
                result = new ButtonAppearance() {
                    @Override
                    public Icon get_icon() {
                        return new ImageIcon(this.getClass().getResource(MedOrderTools.is_closed(medOrder) ? "/artwork/22x22/checked.png" : "/artwork/22x22/unchecked.png"));
                    }
                };
                break;
            }
            case COL_CONFIRMED: {
                final Optional<OPUsers> confirmed_by = MedOrderTools.get_confirmed_by(medOrder);
                result = new ButtonAppearance() {
                    @Override
                    public Icon get_icon() {
                        if (MedOrderTools.is_closed(medOrder)) return SYSConst.icon22ledGrey;
                        if (confirmed_by.isPresent()) return SYSConst.icon22ledGreenOn;
                        return SYSConst.icon22ledYellowOn;
                    }

                    @Override
                    public String get_tooltip() {
                        return (confirmed_by.isPresent() ? UsersTools.getFullname(confirmed_by.get()) : null);
                    }

                    @Override
                    public boolean is_enabled() {
                        return !MedOrderTools.is_closed(medOrder);
                    }
                };
                break;
            }
            default: {
                result = "";
            }
        }
        return result;
    }

    // Operations

    public void complete(int row) {
        MedOrder medOrder = medOrderList.get(row);

        boolean completed = MedOrderTools.is_closed(medOrder);
        medOrder.setClosed_on(completed ? null : LocalDateTime.now());
        medOrder.setClosed_by(completed ? null : OPDE.getLogin().getUser());
        medOrderList.set(row, EntityTools.merge(medOrder));
        fireTableCellUpdated(row, COL_complete);
        fireTableCellUpdated(row, COL_TradeForm);
        fireTableCellUpdated(row, COL_CONFIRMED);
    }

    public void delete(int row) {
        MedOrder medOrder = medOrderList.get(row);
        EntityTools.delete(medOrder);
        medOrderList.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public void confirm(int row) {
        MedOrder medOrder = medOrderList.get(row);
        Optional<OPUsers> confirmed = MedOrderTools.get_confirmed_by(medOrder);
        // ich kann meine eigenen confirms wieder wegnehmen

        if (confirmed.isPresent()) {
            if (!confirmed.get().equals(OPDE.getLogin().getUser())) return;
            medOrder.setConfirmed_by(null);
        } else {
            medOrder.setConfirmed_by(OPDE.getLogin().getUser());
        }
        medOrderList.set(row, EntityTools.merge(medOrder));
        fireTableCellUpdated(row, COL_CONFIRMED);
    }
}