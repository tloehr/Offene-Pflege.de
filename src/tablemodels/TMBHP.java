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
import entity.verordnungen.BHP;
import entity.verordnungen.BHPTools;
import entity.verordnungen.VerordnungTools;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.table.AbstractTableModel;
import java.math.BigInteger;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author tloehr
 */
public class TMBHP extends AbstractTableModel {

    public static final int COL_BEZEICHNUNG = 0;
    public static final int COL_DOSIS = 1;
    public static final int COL_ZEIT = 2;
    public static final int COL_STATUS = 3;
    public static final int COL_UKENNUNG = 4;
    public static final int COL_BEMPLAN = 5;
    public static final int COL_BEMBHP = 6;


    public List<Object[]> getListeBHP() {
        return listeBHP;
    }

    protected List<Object[]> listeBHP;

    /**
     * @param schicht entsprechen OC_Const.ZEIT
     */
    public TMBHP(Bewohner bewohner, Date datum, int schicht) {
        super();

        listeBHP = new ArrayList<Object[]>();

        ArrayList al = SYSCalendar.getZeiten4Schicht((byte) schicht);
        String zeit1 = al.get(2).toString();
        String zeit2 = al.get(3).toString();
        byte schicht1 = ((Byte) al.get(0)).byteValue();
        byte schicht2 = ((Byte) al.get(1)).byteValue();

        EntityManager em = OPDE.createEM();
        try {

            Query queryOHNEmedi = em.createNamedQuery("BHP.findByBewohnerDatumSchichtKeineMedis");
            queryOHNEmedi.setParameter(1, datum);
            queryOHNEmedi.setParameter(2, bewohner.getBWKennung());

            queryOHNEmedi.setParameter(3, schicht == SYSConst.ZEIT_ALLES);

            queryOHNEmedi.setParameter(4, schicht1);
            queryOHNEmedi.setParameter(5, schicht2);
            queryOHNEmedi.setParameter(6, zeit1);
            queryOHNEmedi.setParameter(7, zeit2);

            listeBHP.addAll(queryOHNEmedi.getResultList());

            Query queryMITmedi = em.createNamedQuery("BHP.findByBewohnerDatumSchichtMitMedis");
            queryMITmedi.setParameter(1, bewohner.getBWKennung());
            queryMITmedi.setParameter(2, datum);
            queryMITmedi.setParameter(3, bewohner.getBWKennung());

            queryMITmedi.setParameter(4, schicht == SYSConst.ZEIT_ALLES);

            queryMITmedi.setParameter(5, schicht1);
            queryMITmedi.setParameter(6, schicht2);
            queryMITmedi.setParameter(7, zeit1);
            queryMITmedi.setParameter(8, zeit2);

            listeBHP.addAll(queryMITmedi.getResultList());

        } catch (Exception e) {
            OPDE.fatal(e);
        } finally {
            em.close();
        }
        OPDE.debug(listeBHP);

    }

    @Override
    public int getRowCount() {
        return listeBHP.size();
    }

    public int getColumnCount() {
        return 7;
    }

    public Class getColumnClass(int c) {
        return String.class;
    }

    public BHP getBHP(int row) {
        return (BHP) listeBHP.get(row)[0];
    }


    // Abgesetzt nur dann ausstreichen, wenn HEUTE abgesetzt wurde, aber nicht erst zum Ende des Tages.
//sdfsdfsdfsdfsdfgesetzt() throws SQLException {
//        return rs.getDate("AbDatum") != null && SYSCalendar.sameDay(rs.getDate("AbDatum"), SYSCalendar.today_date()) == 0 && rs.getTimestamp("AbDatum").before(SYSCalendar.nowDBDate());
//    }

    @Override
    public Object getValueAt(int row, int col) {
        Object result = null;
        Object[] objects = (Object[]) listeBHP.get(row);
        BHP bhp = (BHP) objects[0];
        BigInteger bestid = (BigInteger) objects[1];
        BigInteger nextbest = objects[2] == null ? null : BigInteger.valueOf((Long) objects[2]); // Komisch einmal ist es Long einmal ist es BigInteger.

        switch (col) {
            case COL_BEZEICHNUNG: {
                result = VerordnungTools.getMassnahme(bhp.getVerordnungPlanung().getVerordnung());
                break;
            }
            case COL_DOSIS: {
                result = bhp.getDosis().toPlainString();
                break;
            }
            case COL_ZEIT: {
                if (bhp.getSollZeit() == 0) { // Uhrzeit
                    result = DateFormat.getTimeInstance(DateFormat.SHORT).format(bhp.getSoll());
                } else {
                    result = BHPTools.SOLLZEITTEXT[bhp.getSollZeit()];
                }
                break;
            }
            case COL_STATUS: {
                result = bhp.getStatus();
                break;
            }
            case COL_UKENNUNG: {
                result = "";
                if (bhp.getUser() != null) {
                    result = bhp.getUser().getUKennung();
                }
                break;
            }
            case COL_BEMPLAN: {
                result = "";

                if (bestid != null && !bestid.equals(BigInteger.ZERO)) {
                    result = "<i>Bestand im Anbruch Nr.: " + bestid + "</i><br/>";
                    if (nextbest != null && !nextbest.equals(BigInteger.ZERO)) {
                        result = result.toString() + "<i>nächster anzubrechender Bestand Nr.: " + nextbest + "<i><br/>";
                    }
                }
                if (!SYSTools.catchNull(bhp.getVerordnungPlanung().getVerordnung().getBemerkung()).isEmpty()) {
                    result = result.toString() + "<b>Bemerkung:</b> " + bhp.getVerordnungPlanung().getVerordnung().getBemerkung();
                }
                break;
            }
            case COL_BEMBHP: {
                result = SYSTools.catchNull(bhp.getBemerkung());
                break;
            }

            default: {
                result = "";
            }
        }
        return result;
    }
}