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
import entity.verordnungen.*;
import op.OPDE;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

/**
 * @author tloehr
 */
public class TMBedarf
        extends AbstractTableModel {

    public static final int COL_SIT = 0;
    public static final int COL_MSSN = 1;
    public static final int COL_Dosis = 2;
    public static final int COL_Hinweis = 3;

    protected List<Object[]> listeBedarf;
    protected HashMap cache;

    public TMBedarf(List<Object[]> lst) {
        this.cache = new HashMap();
        this.listeBedarf = lst;
//        listeBedarf = VerordnungTools.getBedarfsliste(bewohner);
//        if (listeBedarf == null){
//
//        }
//        OPDE.debug(listeBedarf);
    }

    @Override
    public int getRowCount() {
        return listeBedarf.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Class getColumnClass(int c) {
        return String.class;
    }

    public Verordnung getVerordnung(int row) {
        return (Verordnung) listeBedarf.get(row)[0];
    }

    public Situationen getSituation(int row) {
        return (Situationen) listeBedarf.get(row)[1];
    }

    public VerordnungPlanung getVerordnungPlanung(int row) {
        return (VerordnungPlanung) listeBedarf.get(row)[2];
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

    public MedBestand getBestand(int row) {
        MedBestand bestand = null;

        BigInteger bestid = (BigInteger) listeBedarf.get(row)[7];
        if (bestid != null) {
            EntityManager em = OPDE.createEM();
            bestand = em.find(MedBestand.class, bestid.longValue());
            em.close();
        }
        return bestand;
    }

    public boolean isMaximaleTagesdosisErreicht(int row) {
        VerordnungPlanung vp = getVerordnungPlanung(row);
        BigDecimal maxTagesdosis = vp.getMaxEDosis().multiply(new BigDecimal(vp.getMaxAnzahl()));
        BigDecimal bisherigeTagesdosisPlusEineGabe = getTagesdosisBisher(row).add(vp.getMaxEDosis());
        return bisherigeTagesdosisPlusEineGabe.compareTo(maxTagesdosis) > 0;
    }

    /**
     * Dient nur zu Optimierungszwecken. Damit die Datenbankzugriffe minimiert werden.
     * Lokaler Cache.
     */
    protected String getDosis(int row) {
        String result = "";
        if (cache.containsKey(getVerordnung(row))) {
            result = cache.get(getVerordnung(row)).toString();
        } else {
//            result = VerordnungTools.getDosis(getVerordnung(row), true, getBestand(row));
            cache.put(getVerordnung(row), result);
        }
        return result;
    }

    @Override
    public Object getValueAt(int row, int col) {
        Object result = null;

        switch (col) {
            // Hier muss die Situation stehen. Dann gehts im DlgBedarf weiter
            case COL_SIT: {
                result = VerordnungTools.getHinweis(getVerordnung(row));
                break;
            }
            case COL_MSSN: {
                result = VerordnungTools.getMassnahme(getVerordnung(row));
                break;
            }
            case COL_Dosis: {
                String tmp = "<html><body>";
                tmp += getDosis(row);

                if (getTagesdosisBisher(row).equals(BigDecimal.ZERO)) {
                    tmp += "<br/><b>Diese Verordnung wurde heute nocht nicht angewendet.</b>";
                } else if (isMaximaleTagesdosisErreicht(row)) {
                    tmp += "<br/><b>Keine weitere Gabe des Medikamentes mehr möglich. Tagesdosis ist erreicht</b>";
                } else {
                    tmp += "<br/><b>Bisherige Tagesdosis: " + getTagesdosisBisher(row).setScale(2, BigDecimal.ROUND_HALF_UP) + "</b>";
                }

                tmp += "</body></html>";
                result = tmp;
                break;
            }
            case COL_Hinweis: {
                result = VerordnungTools.getHinweis(getVerordnung(row));
                break;
            }

            default: {
                result = "!!FEHLER!!";
            }
        }

        return result;
    }
}