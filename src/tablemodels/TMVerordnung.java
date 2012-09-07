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

import entity.info.Resident;
import entity.prescription.*;
import op.OPDE;
import op.threads.DisplayMessage;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author tloehr
 */
public class TMVerordnung extends AbstractTableModel {

    public static final int COL_MSSN = 0;
    public static final int COL_Dosis = 1;
    public static final int COL_Hinweis = 2;
    protected boolean mitBestand, abgesetzt;
    protected Resident bewohner;
    protected HashMap cache;

    protected List<Object[]> listeVerordnungen;

    public TMVerordnung(Resident bewohner, boolean archiv, boolean bestand) {
        super();

        this.bewohner = bewohner;
        this.abgesetzt = archiv;
        cache = new HashMap();

        listeVerordnungen = new ArrayList<Object[]>();

        EntityManager em = OPDE.createEM();
        Query queryVerordnung = em.createQuery("SELECT v FROM Prescriptions v WHERE " + (archiv ? "" : " v.to >= :now AND ") + " v.resident = :bewohner ");
        if (!archiv) {
            queryVerordnung.setParameter("now", new Date());
        }
        queryVerordnung.setParameter("bewohner", bewohner);
        List<Prescriptions> listeVerordnung = queryVerordnung.getResultList();
        em.close();

        int i = 0;
        for (Prescriptions verordnung : listeVerordnung) {
            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.loading"), i / 2, listeVerordnung.size()));
            MedInventory inventory = verordnung.getTradeForm() == null ? null : TradeFormTools.getInventory4TradeForm(bewohner, verordnung.getTradeForm());
            MedStock aktiverBestand = MedStockTools.getStockInUse(inventory);
            listeVerordnungen.add(new Object[]{verordnung, inventory, aktiverBestand});
            i++;
        }

        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.sorting"), 50, 100));
        Collections.sort(listeVerordnungen, new Comparator<Object[]>() {
            @Override
            public int compare(Object[] us, Object[] them) {
                Prescriptions usVerordnung = (Prescriptions) us[0];
                Prescriptions themVerordnung = (Prescriptions) them[0];
                return usVerordnung.compareTo(themVerordnung);
            }
        });

        // Cache vorbereiten
        for (int row = 0; row < listeVerordnungen.size(); row++) {
            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.caching"), i / 2 + row / 2, listeVerordnungen.size()));
            getDosis(row);
        }

        this.mitBestand = bestand;

    }

    public Prescriptions getPrescription(int row) {
        return (Prescriptions) listeVerordnungen.get(row)[0];
    }

    public MedInventory getVorrat(int row) {
        return (MedInventory) listeVerordnungen.get(row)[1];
    }

    public MedStock getBestand(int row) {
        return (MedStock) listeVerordnungen.get(row)[2];
    }

    public BigDecimal getVorratSaldo(int row) {
        return (BigDecimal) listeVerordnungen.get(row)[2];
    }

    public BigDecimal getBestandSaldo(int row) {
        return (BigDecimal) listeVerordnungen.get(row)[4];
    }

    public List<Prescriptions> getVordnungenAt(int[] sel) {
        ArrayList<Prescriptions> selection = new ArrayList<Prescriptions>();
        if (sel == null) { // dann alle
            for (int i = 0; i < listeVerordnungen.size(); i++) {
                selection.add(getPrescription(i));
            }
        } else {
            for (int i : sel) {
                selection.add(getPrescription(i));
            }
        }
        return selection;
    }

//    public void reload(Bewohner bewohner, boolean archiv) {
//        this.bewohner = bewohner;
//        this.abgesetzt = archiv;
//        reload();
//    }

//    public void reload() {
//        cache.clear();
//        listeVerordnungen = VerordnungTools.getPrescriptionenUndVorraeteUndBestaende(bewohner, this.abgesetzt);
//        Collections.sort(listeVerordnungen, comparator);
//        fireTableDataChanged();
//    }

    @Override
    public int getRowCount() {
        return listeVerordnungen.size();
    }

    @Override
    public int getColumnCount() {
        int result = 3;
        return result;
    }

    @Override
    public Class getColumnClass(int c) {
        return String.class;
    }

    public void cleanup() {
        listeVerordnungen.clear();
    }

    /**
     * Dient nur zu Optimierungszwecken. Damit die Datenbankzugriffe minimiert werden.
     * Lokaler Cache.
     */
    private String getDosis(int row) {
        // Verordnung prescription, MedBestand bestandImAnbruch, BigDecimal bestandSumme, BigDecimal vorratSumme, boolean mitBestand)
        String result = "";
        if (cache.containsKey(getPrescription(row))) {
            result = cache.get(getPrescription(row)).toString();
        } else {
            result = PrescriptionsTools.getDosis(getPrescription(row), mitBestand);
            cache.put(getPrescription(row), result);
        }
        return result;
    }

    @Override
    public Object getValueAt(int row, int col) {
        Object result = null;
        Prescriptions verordnung = getPrescription(row);

        switch (col) {
            case COL_MSSN: {

                String res = "";
                res = PrescriptionsTools.getPrescriptionAsText(verordnung);
                if (!verordnung.getAttachedFiles().isEmpty()) {
                    res += "<font color=\"green\">&#9679;</font>";
                }
                if (!verordnung.getAttachedVorgaenge().isEmpty()) {
                    res += "<font color=\"red\">&#9679;</font>";
                }
                result = res;
                break;
            }
            case COL_Dosis: {

                result = getDosis(row);
                break;
            }
            case COL_Hinweis: {

                String hinweis = PrescriptionsTools.getHinweis(verordnung);
                String an = PrescriptionsTools.getAN(verordnung);
                String ab = PrescriptionsTools.getAB(verordnung);

                ab = ab.isEmpty() ? "" : "<br/>" + ab;
                hinweis = hinweis.isEmpty() ? "" : hinweis + "<br/>";

                result = hinweis + an + ab;
                break;
            }

            default: {
                result = "!!FEHLER!!";
            }
        }

        return result;
    }
}
