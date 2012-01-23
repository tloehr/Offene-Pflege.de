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

import entity.Barbetrag;
import entity.Bewohner;
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
    Bewohner bewohner;
    List<Barbetrag> listData;

    boolean editable = false;


    public TMBarbetrag(Bewohner bewohner, boolean subset, Date von, Date bis, boolean editable) {
        super();
        this.von = SYSCalendar.bom(von); // Bottom Of Month
        this.bis = SYSCalendar.eom(bis); // End Of Month
        this.subset = subset;
        this.bewohner = bewohner;
        this.editable = editable;

        EntityManager em = OPDE.createEM();

        String jpql = " SELECT tg FROM Barbetrag tg WHERE tg.bewohner = :bewohner AND tg.belegDatum >= :von AND tg.belegDatum <= :bis ";

        if (!subset) {
            vortrag = BigDecimal.ZERO;
        } else {
            jpql += " AND tg.belegDatum >= :von AND tg.belegDatum <= :bis ";

            Query queryVortrag = em.createQuery(" SELECT SUM(tg.betrag) FROM Barbetrag tg WHERE tg.bewohner = :bewohner AND tg.belegDatum < :von ");
            queryVortrag.setParameter("bewohner", bewohner);
            queryVortrag.setParameter("von", von);
            vortrag = (BigDecimal) queryVortrag.getSingleResult();

            if (vortrag == null) {
                vortrag = BigDecimal.ZERO;
            }

        }

        jpql += " ORDER BY tg.belegDatum, tg.tgid ";

        Query queryList = em.createQuery(jpql);
        queryList.setParameter("bewohner", bewohner);
        queryList.setParameter("von", von);
        queryList.setParameter("bis", bis);

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
        boolean allowed;
        // Vortrag
        if (subset && rowIndex == 0) {
            // Steht auf Vortrag
            allowed = false;
        } else if (subset && rowIndex == getRowCount() - 1) { // Zusammenfassung
            allowed = false;
        } else if (columnIndex == COL_Zeilensaldo) { // Steht auf Zeilensaldo
            allowed = false;
        } else {
            allowed = editable;
        }
        return allowed;
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

    public List<Barbetrag> getListData() {
        return listData;
    }

    /**
     * Zeilensaldo in der letzten Zeile der Tabelle.
     *
     * @return
     */
    public BigDecimal getZeilenSaldo() {
        OPDE.debug("getZeilenSaldo("+getRowCount()+"-1)");
        return getZeilenSaldo(getRowCount()-1);
    }

    public BigDecimal getZeilenSaldo(int row) {
        BigDecimal zeilensaldo;
        OPDE.debug("row:"+row);
        int listRow = subset ? row - 1 : row;

        if (row == 0) { // erste Zeile
            zeilensaldo = vortrag; // Anker
        } else {
            zeilensaldo = getZeilenSaldo(listRow-1).add(listData.get(listRow).getBetrag());
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
                    result = getZeilenSaldo(row);
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
                    result = BigDecimal.ZERO; //getZeilenSaldo();
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
        Barbetrag tg2edit = listData.get(subset ? rowIndex - 2 : rowIndex);
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
            }
        } // switch

        tg2edit = EntityTools.merge(tg2edit);
        fireTableCellUpdated(rowIndex, columnIndex);
    } // setValueAt


    public BigDecimal getVortrag() {
        return vortrag;
    }

} // BWTableModel