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
package tablemodels;

import entity.prescription.*;
import op.tools.SYSConst;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author tloehr
 */
public class TMBedarf
        extends AbstractTableModel {
//
//    public static final int COL_SIT = 0;
//    public static final int COL_MSSN = 1;
//    public static final int COL_Dosis = 2;
//    public static final int COL_Hinweis = 3;

    protected List<Object[]> listeBedarf;
    protected HashMap cache;

    public TMBedarf(List<Object[]> lst) {
        this.cache = new HashMap();
        this.listeBedarf = lst == null ? new ArrayList<Object[]>() : lst;
    }

    @Override
    public int getRowCount() {
        return listeBedarf.size();
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public Class getColumnClass(int c) {
        return String.class;
    }

    public Prescriptions getPrescription(int row) {
        return (Prescriptions) listeBedarf.get(row)[0];
    }

    public Situationen getSituation(int row) {
        return (Situationen) listeBedarf.get(row)[1];
    }

    public PrescriptionSchedule getPrescriptionSchedule(int row) {
        return (PrescriptionSchedule) listeBedarf.get(row)[2];
    }

    public BigDecimal getVorratSaldo(int row) {
        return (BigDecimal) listeBedarf.get(row)[3];
    }

    public BigDecimal getTagesdosisBisher(int row) {
        BigDecimal tagesdosis = (BigDecimal) listeBedarf.get(row)[4];
        if (tagesdosis == null) {
            tagesdosis = BigDecimal.ZERO;
        }
        return tagesdosis;
    }

    public BigDecimal getBestandAPV(int row) {
        return (BigDecimal) listeBedarf.get(row)[5];
    }

    public BigDecimal getBestandSumme(int row) {
        return (BigDecimal) listeBedarf.get(row)[6];
    }

    public TradeForm getDarreichung(int row) {
        return (TradeForm) listeBedarf.get(row)[5];
    }

    public MedStock getBestand(int row) {
        return (MedStock) listeBedarf.get(row)[8];
    }

    public boolean isMaximaleTagesdosisErreicht(int row) {
        PrescriptionSchedule vp = getPrescriptionSchedule(row);
        BigDecimal maxTagesdosis = vp.getMaxEDosis().multiply(new BigDecimal(vp.getMaxAnzahl()));
        BigDecimal bisherigeTagesdosisPlusEineGabe = getTagesdosisBisher(row).add(vp.getMaxEDosis());
        return bisherigeTagesdosisPlusEineGabe.compareTo(maxTagesdosis) > 0;
    }

    public MedInventory getVorrat(int row) {
        return getBestand(row) == null ? null : getBestand(row).getInventory();
    }

    /**
     * Dient nur zu Optimierungszwecken. Damit die Datenbankzugriffe minimiert werden.
     * Lokaler Cache.
     */
    protected String getDosis(int row) {
        String result = "";
        if (cache.containsKey(getPrescription(row))) {
            result = cache.get(getPrescription(row)).toString();
        } else {
            result = PrescriptionsTools.getDosis(getPrescription(row), true);
            cache.put(getPrescription(row), result);
        }
        return result;
    }

    @Override
    public Object getValueAt(int row, int col) {

        String result = "<font size=\"+1\">"+ PrescriptionsTools.getPrescriptionAsText(getPrescription(row))+"</font>";

        result += SYSConst.html_fontface;
        result += "<br/>" + getDosis(row);

        if (getTagesdosisBisher(row).equals(BigDecimal.ZERO)) {
            result += "<br/>Diese Verordnung wurde heute noch nicht angewendet.";
        } else if (isMaximaleTagesdosisErreicht(row)) {
            result += "<br/><b>Keine weitere Gabe des Medikamentes mehr möglich. Tagesdosis ist erreicht</b>";
        } else {

            result += "<br/>Bisherige Tagesdosis: " + getTagesdosisBisher(row).setScale(2, BigDecimal.ROUND_HALF_UP) + " " + getDarreichung(row) == null ? "x" : DosageFormTools.EINHEIT[getDarreichung(row).getDosageForm().getAnwEinheit()];
        }

        result += "</font>";
        result += "<br/>" + PrescriptionsTools.getHinweis(getPrescription(row));


        return result;
    }
}