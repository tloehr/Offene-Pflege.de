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
package op.care.bhp;

import entity.verordnungen.BHP;
import entity.verordnungen.BHPTools;
import entity.verordnungen.VerordnungTools;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.swing.table.AbstractTableModel;
import java.math.BigInteger;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author tloehr
 */
public class TMBHP
        extends AbstractTableModel {
    //public static final int COL_massid = 0;

    public static final int COL_BEZEICHNUNG = 0;
    public static final int COL_DOSIS = 1;
    public static final int COL_ZEIT = 2;
    public static final int COL_STATUS = 3;
    public static final int COL_UKENNUNG = 4;
    public static final int COL_BEMPLAN = 5;
    public static final int COL_BEMBHP = 6;
    public static final int COL_BHPID = 7;
    public static final int COL_BHPPID = 8;
    public static final int COL_MDATE = 9;
    public static final int COL_VERID = 10;
    public static final int COL_DAFID = 11;
    public static final int COL_SITID = 12;
    public static final int COL_BISPACKENDE = 14;
    public static final int COL_PACKEINHEIT = 16;
    public static final int COL_ABDATUM = 17;
    //    public static final int STATUS_OFFEN = 0;
//    public static final int STATUS_ERLEDIGT = 1;
//    public static final int STATUS_VERWEIGERT = 2;
//    public static final int STATUS_VERWEIGERT_VERWORFEN = 3;
    boolean withTime;

    public List<Object[]> getListeBHP() {
        return listeBHP;
    }

    protected List<Object[]> listeBHP;

    /**
     * @param schicht entsprechen OC_Const.ZEIT
     */
    public TMBHP(String currentBW, Date datum, int schicht) {
        super();
        this.withTime = withTime;
        String filter = "";
        if (schicht != SYSConst.ZEIT_ALLES) {
            filter = " WHERE (SZeit >= ? AND SZeit <= ?) OR (SZeit = 0 AND TIME(Soll) >= ? AND TIME(Soll) <= ?) ";
        }


        //OPDE.debug("------------ TMBHP ---------------");
        //OPDE.debug(sql);
        //Date d = new Date(new GregorianCalendar(2006,GregorianCalendar.AUGUST,11).getTimeInMillis());
        stmt.setString(1, currentBW);
        stmt.setDate(2, new java.sql.Date(datum.getTime()));
        stmt.setString(3, currentBW);
        stmt.setDate(4, new java.sql.Date(datum.getTime()));
        stmt.setString(5, currentBW);

        if (schicht != SYSConst.ZEIT_ALLES) {
            ArrayList al = SYSCalendar.getZeiten4Schicht(schicht);
            String zeit1 = al.get(2).toString();
            String zeit2 = al.get(3).toString();
            int schicht1 = ((Integer) al.get(0)).intValue();
            int schicht2 = ((Integer) al.get(1)).intValue();
            stmt.setInt(6, schicht1);
            stmt.setInt(7, schicht2);
            stmt.setString(8, zeit1);
            stmt.setString(9, zeit2);
        }

    }

    @Override
    public int getRowCount() {
        listeBHP.size();
    }

    public int getColumnCount() {
        return 7;
    }

    public Class getColumnClass(int c) {
        return String.class;
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
        BigInteger nextbest = (BigInteger) objects[2];

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

                if (bestid != null) {
                    result = "<i>Bestand im Anbruch Nr.: " + bestid + "</i><br/>";
                    if (nextbest != null) {
                        result = result.toString() + "<i>nächster anzubrechender Bestand Nr.: " + nextbest + "<i><br/>";
                    }
                }
                if (!SYSTools.catchNull(bhp.getVerordnungPlanung().getVerordnung().getBemerkung()).isEmpty()
                {
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