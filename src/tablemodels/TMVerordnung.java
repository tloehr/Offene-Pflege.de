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
import entity.BewohnerTools;
import entity.verordnungen.MedBestand;
import entity.verordnungen.MedVorrat;
import entity.verordnungen.Verordnung;
import entity.verordnungen.VerordnungTools;
import op.OPDE;
import op.tools.DlgException;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

/**
 * @author tloehr
 */
public class TMVerordnung
        extends AbstractTableModel {

    public static final int COL_MSSN = 0;
    public static final int COL_Dosis = 1;
    public static final int COL_Hinweis = 2;
    protected boolean mitBestand, abgesetzt;
    protected Bewohner bewohner;

    protected HashMap cache;

    protected List<Object[]> listeVerordnungen;

    public TMVerordnung(Bewohner bewohner, boolean abgesetzt, boolean bestand) {
        super();

        listeVerordnungen = VerordnungTools.getVerordnungenUndVorraeteUndBestaende(bewohner, !abgesetzt);
        this.bewohner = bewohner;
        this.abgesetzt = abgesetzt;

        this.cache = new HashMap();
        this.mitBestand = bestand;
    }

    public Verordnung getVerordnung(int row){
         return (Verordnung) listeVerordnungen.get(row)[0];
    }

    public MedVorrat getVorrat(int row){
         return (MedVorrat) listeVerordnungen.get(row)[1];
    }

    public MedBestand getBestand(int row){
         return (MedBestand) listeVerordnungen.get(row)[3];
    }

    public BigDecimal getVorratSaldo(int row){
         return (BigDecimal) listeVerordnungen.get(row)[2];
    }

    public BigDecimal getBestandSaldo(int row){
         return (BigDecimal) listeVerordnungen.get(row)[4];
    }

    public List<Verordnung> getVordnungenAt(int [] sel){
        ArrayList<Verordnung> selection = new ArrayList<Verordnung>();
        for (int i : sel){
            selection.add(getVerordnung(i));
        }
        return selection;
    }

    public void reload(){
        cache.clear();
        listeVerordnungen = VerordnungTools.getVerordnungenUndVorraeteUndBestaende(bewohner, !abgesetzt);
        fireTableDataChanged();
    }

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

    /**
     * Dient nur zu Optimierungszwecken. Damit die Datenbankzugriffe minimiert werden.
     * Lokaler Cache.
     */
    protected String getDosis(Verordnung verordnung, MedBestand bestandImAnbruch, BigDecimal bestandSumme, BigDecimal vorratSumme, boolean mitBestand) {
        String result = "";
        if (cache.containsKey(verordnung)) {
            result = cache.get(verordnung).toString();
        } else {
            result = VerordnungTools.getDosis(verordnung, bestandImAnbruch, bestandSumme, vorratSumme, mitBestand);
            cache.put(verordnung, result);
        }
        return result;
    }

    @Override
    public Object getValueAt(int row, int col) {
        Object result = null;
        Verordnung verordnung = getVerordnung(row);

        switch (col) {
            case COL_MSSN: {
                String res = "";
                res = VerordnungTools.getMassnahme(verordnung);
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
                result = getDosis(verordnung, getBestand(row),  getBestandSaldo(row), getVorratSaldo(row), mitBestand);
                break;
            }
            case COL_Hinweis: {
                String hinweis =  VerordnungTools.getHinweis(verordnung);
                String an =  VerordnungTools.getAN(verordnung);
                String ab =  VerordnungTools.getAB(verordnung);

                ab = ab.isEmpty() ? "" : "<br/>"+ab;
                hinweis = hinweis.isEmpty() ? "" : hinweis + "<br/>";

                result = hinweis + an + ab;
                break;
            }
//            case COL_AN: {
//                result = VerordnungTools.getAN(verordnung);
//                break;
//            }
//            case COL_AB: {
//                result = VerordnungTools.getAB(verordnung);
//                break;
//            }

            default: {
                result = "!!FEHLER!!";
            }
        }

        return result;
    }
}
