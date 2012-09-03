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

import entity.Allowance;
import entity.info.Resident;
import entity.EntityTools;
import op.OPDE;
import op.tools.SYSCalendar;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author tloehr
 */
public class TMBarbetrag extends AbstractTableModel {
    public static final int COL_Datum = 0;
    public static final int COL_Text = 1;
    public static final int COL_Betrag = 2;
    public static final int COL_Zeilensaldo = 3;
    public static final int EDIT_MODE_NONE = 0;
    // Der Vortrag bis zu dem gewünschten Zeitraum, der dargestellt wird. Also die Summe von allem was davor war.
    private BigDecimal vortrag;
    boolean subset;
    //    int offset;
    Date von;
    Date bis;
    Resident bewohner;
    List<Allowance> listData;

    boolean editable = false;


    public TMBarbetrag(Resident bewohner, Date von, Date bis, boolean editable) {
        super();

        this.subset = von != null;
        if (subset) {
            this.von = SYSCalendar.bom(von); // Bottom Of Month
            this.bis = SYSCalendar.eom(bis); // End Of Month
        }

        this.bewohner = bewohner;
        this.editable = editable;

        EntityManager em = OPDE.createEM();

        String jpql = " SELECT tg FROM Allowance tg WHERE tg.bewohner = :bewohner ";

        if (!subset) {
            vortrag = BigDecimal.ZERO;
        } else {
            jpql += " AND allowance.belegDatum >= :von AND allowance.belegDatum <= :bis ";

            Query queryVortrag = em.createQuery(" SELECT SUM(tg.betrag) FROM Allowance tg WHERE tg.bewohner = :bewohner AND tg.belegDatum < :von ");
            queryVortrag.setParameter("bewohner", bewohner);
            queryVortrag.setParameter("von", von);
            vortrag = (BigDecimal) queryVortrag.getSingleResult();

            if (vortrag == null) {
                vortrag = BigDecimal.ZERO;
            }

        }

        jpql += " ORDER BY allowance.belegDatum, allowance.tgid ";

        Query queryList = em.createQuery(jpql);
        queryList.setParameter("bewohner", bewohner);

        if (subset) {
            queryList.setParameter("von", this.von);
            queryList.setParameter("bis", this.bis);
        }

        listData = queryList.getResultList();

        em.close();

    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public int getRowCount() {
        int rows = subset ? listData.size() + 2 : listData.size();
        return rows;
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
//        boolean allowed;
//        // Vortrag
//        if (subset && rowIndex == 0) {
//            // Steht auf Vortrag
//            allowed = false;
//        } else if (subset && rowIndex == getRowCount() - 1) { // Zusammenfassung
//            allowed = false;
//        } else if (columnIndex == COL_Zeilensaldo) { // Steht auf Zeilensaldo
//            allowed = false;
//        } else {
//            allowed = editable;
//        }
        return false;
    }

    /**
     * ermittelt ob eine bestimmte Zeile errechnet oder aus der Datenbank stammt. Errechnet heisst,
     * ob der betreffende auf dem Vortrag oder der Zusammenfassung steht.
     * @param row
     * @return
     */
    public boolean isReal(int row){
        return !subset || (row != 0 && row < getRowCount()-1);
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        Class c;
        switch (columnIndex) {
            case COL_Datum: {
                c = Date.class;
                break;
            }
            case COL_Text: {
                c = String.class;
                break;
            }
            case COL_Betrag: {
                c = BigDecimal.class;
                break;
            }
            case COL_Zeilensaldo: {
                c = BigDecimal.class;
                break;
            }
            default: {
                c = String.class;
            }
        }
        return c;
    }

    public List<Allowance> getListData() {
        return listData;
    }

    public int getModelRow(int row){
        return subset ? row - 1 : row;
    }

    /**
     * Zeilensaldo in der letzten Zeile der Tabelle.
     *
     * @return
     */
    public BigDecimal getZeilenSaldo() {
        return getZeilenSaldo(subset ? getRowCount() - 3 : getRowCount() - 1);
    }

    public BigDecimal getZeilenSaldo(int row) {
        BigDecimal zeilensaldo;

        if (row == -1) { // erste Zeile
            zeilensaldo = vortrag; // Anker
        } else {
            zeilensaldo = getZeilenSaldo(row - 1).add(listData.isEmpty() ? BigDecimal.ZERO : listData.get(row).getBetrag());
        }
        return zeilensaldo;
    }

    public Object getValueAt(int row, int col) {
        Object result;

        if (subset && row == 0) { // Zeile für Vortrag
            switch (col) {
                case COL_Datum: {
                    result = SYSCalendar.addDate(von, -1);
                    break;
                }
                case COL_Text: {
                    result = "Übertrag aus Vormonat(en)";
                    break;
                }
                case COL_Betrag: {
                    result = this.vortrag;
                    break;
                }
                case COL_Zeilensaldo: {
                    result = vortrag;
                    break;
                }
                default: {
                    result = "";
                    break;
                }
            }
        } else if (subset && row == getRowCount() - 1) { // Zusammenfassung in der letzten Zeile
            switch (col) {
                case COL_Datum: {
                    result = SYSCalendar.eom(bis);
                    break;
                }
                case COL_Text: {
                    result = "Saldo zum Monatsende";
                    break;
                }
                case COL_Betrag: {
                    result = "";
                    break;
                }
                case COL_Zeilensaldo: {
                    result = getZeilenSaldo();
                    break;
                }
                default: {
                    result = "";
                    break;
                }
            }
        } else {
            int listRow = subset ? row - 1 : row;
            switch (col) {
                case COL_Datum: {
                    result = listData.get(listRow).getBelegDatum();
                    break;
                }
                case COL_Text: {
                    result = listData.get(listRow).getBelegtext();
                    break;
                }
                case COL_Betrag: {
                    result = listData.get(listRow).getBetrag();
                    break;
                }
                case COL_Zeilensaldo: {
                    result = getZeilenSaldo(listRow);
                    break;
                }
                default: {
                    result = "";
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Allowance tg2edit = listData.get(subset ? rowIndex - 1 : rowIndex);
        switch (columnIndex) {
            case COL_Datum: {
                tg2edit.setBelegDatum((Date) aValue);
                break;
            }
            case COL_Text: {
                tg2edit.setBelegtext(aValue.toString());
                break;
            }
            case COL_Betrag: {
                tg2edit.setBetrag((BigDecimal) aValue);
                break;
            }
            default: {
                break;
            }
        } // switch

        tg2edit = EntityTools.merge(tg2edit);
        fireTableCellUpdated(rowIndex, columnIndex);
    } // setValueAt


    public BigDecimal getVortrag() {
        return vortrag;
    }

} // BWTableModel