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
package op.care.dfn;

import op.OPDE;
import op.tools.DlgException;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.swing.table.AbstractTableModel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

/**
 * @author tloehr
 */
public class TMDFN
        extends AbstractTableModel {

    public static final int COL_BEZEICHNUNG = 0;
    public static final int COL_ZEIT = 1;
    public static final int COL_STATUS = 2;
    public static final int COL_UKENNUNG = 3;
    public static final int COL_BEMPLAN = 4;
    public static final int COL_DFNID = 5;
    public static final int COL_PLANID = 7;
    public static final int COL_BIS = 8;
    public static final int COL_MDATE = 9;
    public static final int COL_ERFORDERLICH = 11;
    public static final int COL_STDATUM = 12;
    public static final int COL_TERMID = 14;
    public static final int COL_SOLL = 15;
    public static final int COL_SZEIT = 16;
    public static final int COL_SITUATION = 17;
    //public static final int COL_ZEBRA = 18;
    public static final int COL_STICHWORT = 19;
    public static final int STATUS_OFFEN = 0;
    public static final int STATUS_ERLEDIGT = 1;
    public static final int STATUS_VERWEIGERT = 2;
    public static final int STATUS_VERWEIGERT_ALTERNATIVE = 3;
    ResultSet rs;
    PreparedStatement stmt;
    String sql;
    // #0000031: Optimierung zur Geschwindigkeit.
    Vector update = null;
    //boolean withTime;

    /**
     * @param schicht entsprechen OC_Const.ZEIT
     */
    public TMDFN(String currentBW, Date datum, int schicht) {
        super();
        //this.withTime = withTime;
        String filter = "";
        if (schicht != SYSConst.ZEIT_ALLES) {
            filter = " AND ((dfn.SZeit >= ? AND dfn.SZeit <= ?) OR " +
                    " (dfn.SZeit = 0 AND TIME(dfn.Soll) >= ? AND TIME(dfn.Soll) <= ?)) ";
        }

        try {
            sql = "SELECT" +
                    " dfn.DFNID, dfn.UKennung, dfn.Soll, dfn.Ist, dfn.StDatum, dfn.SZeit, dfn.IZeit, " +
                    " dfn.Status, dfn.Erforderlich, m.Bezeichnung, p.Stichwort, dfn._mdate, dfn.TermID, " +
                    " IFNULL(p.PlanID, 0) planid, k.Bezeichnung, t.Bemerkung, p.Situation, p.Ziel, p.Bis, " +
                    " CASE dfn.SZeit WHEN 0 THEN 'Uhrzeit' WHEN 1 THEN 'NachtMo' WHEN 2 THEN 'Morgens' WHEN 3 THEN 'Mittags' " +
                    " WHEN 4 THEN 'Nachmittags' WHEN 5 THEN 'Abends' WHEN 6 THEN 'NachtAb' ELSE '!FEHLER!' END SZeitText," +
                    " dfn.Dauer, k.Sortierung " +
                    " FROM DFN dfn " +
                    " INNER JOIN Massnahmen m ON dfn.MassID = m.MassID " +
                    " LEFT OUTER JOIN MassTermin t ON t.TermID = dfn.TermID " +
                    " LEFT OUTER JOIN Planung p ON t.PlanID = p.PlanID " +
                    " LEFT OUTER JOIN BWInfoKat k ON k.BWIKID = p.BWIKID " +
                    " WHERE dfn.BWKennung = ? AND Date(dfn.Soll) = Date(?) " +
                    filter +
                    " ORDER BY k.Sortierung, p.Stichwort, dfn.SZeit, TIME(dfn.Soll), m.Bezeichnung ";
            stmt = OPDE.getDb().db.prepareStatement(sql); //, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            //Date d = new Date(new GregorianCalendar(2006,GregorianCalendar.AUGUST,11).getTimeInMillis());
            stmt.setString(1, currentBW);
            stmt.setDate(2, new java.sql.Date(datum.getTime()));

            if (schicht != SYSConst.ZEIT_ALLES) {
                ArrayList al = SYSCalendar.getZeiten4Schicht(schicht);
                String zeit1 = al.get(2).toString();
                String zeit2 = al.get(3).toString();
                int schicht1 = ((Integer) al.get(0)).intValue();
                int schicht2 = ((Integer) al.get(1)).intValue();
                stmt.setInt(3, schicht1);
                stmt.setInt(4, schicht2);
                stmt.setString(5, zeit1);
                stmt.setString(6, zeit2);
            }

            rs = stmt.executeQuery();

            // Das hier zur Beschleunigung der katindices. Hatte ich mal
            // als Rekursion aufgebaut. Wird dann aber SCHWEINElangsam.
//            if (rs.last()) {
//                zebra = new boolean[rs.getRow()];
//                rs.first();
//                int prev = rs.getInt("k.Sortierung");
//                rs.beforeFirst();
//                boolean z = true;
//                while (rs.next()) {
//                    int current = rs.getInt("k.Sortierung");
//                    //OPDE.debug("#"+rs.getRow()+"   prev: "+prev + "    current: "+current + "     "+z+"  "+rs.getString("k.Bezeichnung"));
//                    if (prev != current) {
//                        prev = current;
//                        z = !z;
//                    }
//                    zebra[rs.getRow() - 1] = z;
//                }
//            }
            rs.last();
            update = new Vector(rs.getRow() + 1);
            update.setSize(rs.getRow() + 1);
            rs.first();
        } // try
        catch (SQLException se) {
            new DlgException(se);
        } // catch
    }

    public void setUpdate(int row, int status) {
        update.set(row + 1, new Object[]{status, System.nanoTime()});
        fireTableRowsUpdated(row, row);
    }

    public void reload(int row, int col) {
        try {
            rs = stmt.executeQuery();
            rs.first();
            fireTableRowsUpdated(row, col);
        } catch (SQLException se) {
            new DlgException(se);
        }
    }

    public int getRowCount() {
        try {
            rs.last();
            return rs.getRow();
        } catch (SQLException se) {
            System.out.println(se.getMessage());
            return -1;
        }
    }

    public int getColumnCount() {
        int result = 5;
//        if (withTime) {
//            result++;
//        }
        return result;
    }

    public Class getColumnClass(int c) {
        switch (c) {
            case COL_BEZEICHNUNG: {
                return String.class;
            }
            case COL_ZEIT: {
                return String.class;
            }
            case COL_STATUS: {
                return Integer.class;
            }
            case COL_UKENNUNG: {
                return String.class;
            }
            case COL_BEMPLAN: {
                return String.class;
            }
            case COL_DFNID: {
                return Long.class;
            }
            case COL_MDATE: {
                return Long.class;
            }
            case COL_PLANID: {
                return Long.class;
            }
            case COL_BIS: {
                return Long.class;
            }
            case COL_ERFORDERLICH: {
                return Boolean.class;
            }
            case COL_STDATUM: {
                return Date.class;
            }
        }
        return String.class;
    }

    // Soll nur ausgestrichen werden, wenn die Absetzung am selben Tag wie heute erfolgte.
    private boolean isAbgesetzt() throws SQLException {
        return (rs.getLong("dfn.TermID") != 0 && SYSCalendar.sameDay(rs.getDate("p.Bis"), SYSCalendar.today_date()) <= 0);
    }

    private String getMassnahme() {
        String result = "<html><body>";
        try {
            if (isAbgesetzt()) {
                result += "<s>"; // Abgesetzte
            }
            result += "<font face=\"Sans Serif\"><b>" + rs.getString("m.Bezeichnung") + "</b></font>";
            result += "<font size=\"-1\">";
            result += "<br/>Dauer: <b>" + rs.getInt("dfn.Dauer") + "</b> Minuten";

            if (rs.getLong("dfn.TermID") == 0) { // Einzelmassnahme
                result += " (Spontane Einzelmassnahme)";
            } else {
                result += " <font color=\"blue\">(" + rs.getString("k.Bezeichnung") + "</font>)";
            }

            result += "</font>";

            if (isAbgesetzt()) {
                result += "</s>"; // Abgesetzte
            }
        } catch (SQLException ex) {
            new DlgException(ex);
        }

        result += "</body></html>";
        return result;
    }

    public Object getValueAt(int r, int c) {
        Object result = null;
        try {
            rs.absolute(r + 1);
            //boolean katchange = (r == 0) || zebra[r - 1] != zebra[r];
            switch (c) {
                //case COL_MassID : {result = rs.getString("MassID"); break;}
                case COL_BEZEICHNUNG: {
                    result = getMassnahme();
                    break;
                }
                case COL_ZEIT: {
                    result = rs.getString("SZeitText");
                    if (result.equals("Uhrzeit")) {
                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                        result = formatter.format(rs.getTime("Soll")) + " Uhr";
                        //result = rs.getTime("SUhrzeit");
                    }
                    break;
                }
                case COL_STATUS: {
                    if (update.get(r + 1) == null) {
                        result = rs.getInt("Status");
                    } else {
                        int status = (Integer) ((Object[]) update.get(r + 1))[0];
                        result = status;
                    }
                    break;
                }
                case COL_UKENNUNG: {
                    if (update.get(r + 1) == null) {
                        result = rs.getString("UKennung");
                        if (result == null) {
                            result = "";
                        }
                    } else {
                        result = OPDE.getLogin().getUser().getUKennung();
                    }
                    break;
                }
                case COL_BEMPLAN: {
                    result = "";
                    if (!SYSTools.catchNull(rs.getString("t.Bemerkung")).equals("")) {
                        result = result.toString() + "<b>Bemerkung (Massnahme):</b> " + rs.getString("t.Bemerkung") + "<br/>";
                    }
                    break;
                }

                case COL_DFNID: {
                    result = rs.getLong("DFNID");
                    break;
                }
                case COL_PLANID: {
                    result = rs.getLong("PlanID");
                    break;
                }
                case COL_BIS: {
                    if (rs.getLong("TermID") == 0) {
                        result = SYSConst.DATE_BIS_AUF_WEITERES.getTime();
                    } else {
                        result = rs.getTimestamp("Bis").getTime();
                    }
                    break;
                }
                case COL_MDATE: {
                    if (update.get(r + 1) == null) {
                        result = rs.getTimestamp("_mdate").getTime();
                    } else {
                        long mdate = (Long) ((Object[]) update.get(r + 1))[1];
                        result = mdate;
                    }
                    break;
                }
                case COL_ERFORDERLICH: {
                    result = rs.getBoolean("Erforderlich");
                    break;
                }
                case COL_STDATUM: {
                    result = rs.getDate("StDatum");
                    break;
                }
                case COL_TERMID: {
                    result = rs.getLong("TermID");
                    break;
                }
                case COL_SOLL: {
                    result = rs.getTimestamp("Soll").getTime();
                    break;
                }
                case COL_SZEIT: {
                    result = rs.getInt("SZeit");
                    break;
                }
                case COL_STICHWORT: {
                    result = rs.getString("p.Stichwort");
                    break;
                }
                case COL_SITUATION: {
                    if (!SYSTools.catchNull(rs.getString("p.Situation")).equals("")) {
                        result = "<b>Situationsbeschreibung (Planung):</b> " + rs.getString("p.Situation") + "<br/>";
                        result = result.toString() + "<b>Ziele (Planung):</b> " + rs.getString("p.Ziel");
                    }
                    break;
                }
//                case COL_ZEBRA: {
//                    result = zebra[r];
//                    break;
//                }
                default: {
                    result = "";
                }
            }
        } catch (SQLException se) {
            new DlgException(se);
        }
        return result;
    }
}
