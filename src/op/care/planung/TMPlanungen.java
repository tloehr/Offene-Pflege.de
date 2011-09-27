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
package op.care.planung;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import javax.swing.table.AbstractTableModel;
import op.tools.DlgException;
import op.OPDE;
import op.tools.DBRetrieve;
import op.tools.SYSCalendar;
import op.tools.SYSConst;

/**
 *
 * @author root
 */
public class TMPlanungen extends AbstractTableModel {

    public static final int COL_KATEGORIE = 0;
    public static final int COL_BEMERKUNG = 1;
    public static final int COL_AN = 2;
    public static final int COL_AB = 3;
    public static final int COL_PLANID = 4;
    public static final int COL_ABGESETZT = 5;
    public static final int COL_VON = 6;
    public static final int COL_BIS = 7;
    public static final int COL_HTML = 8;
    public static final int COL_STICHWORT = 9;
    public static final int COL_ZEBRA = 10;
    public static final int COL_ANUKENNUNG = 11;
    public static final int COL_ABUKENNUNG = 12;
    ResultSet rs;
    PreparedStatement stmt;
    String sql;
    boolean details;
    private boolean[] zebra = null;

    public TMPlanungen(String bwkennung, boolean abgesetzt, boolean details) {
        super();
        this.details = details;
        try {
            sql = "" +
                    " SELECT p.PlanID, p.Stichwort, p.Situation, p.Ziel, p.Von, p.Bis, p.AnUKennung, " +
                    " p.AbUKennung, p.NKontrolle, b.Bezeichnung, b.Sortierung " +
                    " FROM Planung p " +
                    " INNER JOIN BWInfoKat b ON b.BWIKID = p.BWIKID " +
//                    // Hier die angehangenen Vorgänge
//                    " INNER JOIN " +
//                    " (" +
//                    " 	SELECT DISTINCT f2.PlanID, ifnull(anzahl,0) anzahl" +
//                    " 	FROM Planung f2" +
//                    " 	LEFT OUTER JOIN (" +
//                    " 		SELECT ForeignKey, count(*) anzahl FROM VorgangAssign" +
//                    " 		WHERE TableName='Planung'" +
//                    " 		GROUP BY ForeignKey" +
//                    " 		) va ON va.ForeignKey = f2.PlanID" +
//                    " 	WHERE f2.BWKennung=? " +
//                    " ) vrg ON vrg.PlanID = p.PlanID " +
                    " WHERE BWKennung = ? " +
                    // Nur für die Sturzgeschichte
                    // (!abgesetzt ? " AND p.Von <= now() AND p.Bis >= now() " : "");
                    " AND p.Von <= '2011-01-03 12:20:00' AND p.Bis >= '2011-01-03 12:20:00' ";


            sql += " ORDER BY b.Sortierung, p.Stichwort ";
            stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setString(1, bwkennung);
            rs = stmt.executeQuery();

            // Das hier zur Beschleunigung der katindices. Hatte ich mal
            // als Rekursion aufgebaut. Wird dann aber SCHWEINElangsam.
            if (rs.last()) {
                zebra = new boolean[rs.getRow()];
                rs.first();
                int prev = rs.getInt("b.Sortierung");
                rs.beforeFirst();
                boolean z = true;
                while (rs.next()) {
                    int current = rs.getInt("b.Sortierung");
                    //OPDE.getLogger().debug("#"+rs.getRow()+"   prev: "+prev + "    current: "+current + "     "+z+"  "+rs.getString("k.Bezeichnung"));                    
                    if (prev != current) {
                        prev = current;
                        z = !z;
                    }
                    zebra[rs.getRow() - 1] = z;
                }
            }
        } catch (SQLException se) {
            new DlgException(se);
        }
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
            new DlgException(se);
            return -1;
        }
    }

    public boolean isDetailview() {
        return details;
    }

    public int getColumnCount() {
        int result = 4;
        return result;
    }

    public Class getColumnClass(int c) {
        Class result;
        switch (c) {
            case COL_PLANID: {
                result = Long.class;
                break;
            }
            default: {
                result = String.class;
            }
        }
        return result;
    }

    private String getBemerkung() throws SQLException {
        String result = "";
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

        if (isAbgesetzt()) {//rs.getDate("AbDatum") != null && rs.getTimestamp("AbDatum").getTime() <=  SYSCalendar.nowDB() ){
            result += "<s>"; // Abgesetzte
        }

        result += "<font color=\"green\" >";
        result += "Pflegeplanung <b>&raquo;" + rs.getString("Stichwort") + "&laquo;</b><br/>";
        result += "</font>";

//        if (!SYSTools.catchNull(rs.getString("Bemerkung")).equals("")) {
//            result += "<b>Bemerkung:</b> " + rs.getString("Bemerkung") + "<br/>";
//        }
        if (!isAbgesetzt()) {
            if (SYSCalendar.isInFuture(rs.getDate("NKontrolle").getTime())) {
                int daysdiff = SYSCalendar.getDaysBetween(SYSCalendar.toGC(SYSCalendar.nowDB()), SYSCalendar.toGC(rs.getDate("NKontrolle")));
                if (daysdiff > 7) {
                    result += "<font " + SYSConst.html_darkgreen + ">";
                } else if (daysdiff == 0) {
                    result += "<font " + SYSConst.html_gold7 + ">";
                } else {
                    result += "<font " + SYSConst.html_darkorange + ">";
                }
            } else {
                result += "<font " + SYSConst.html_darkred + ">";
            }

            result += "<b>Nächste Kontrolle:</b> " + df.format(rs.getDate("NKontrolle")) + "</font>";
            if (isAbgesetzt()) {//if (rs.getDate("AbDatum") != null && rs.getTimestamp("AbDatum").getTime() <=  SYSCalendar.nowDB() ){
                result += "</s>"; // Abgesetzte
            }
        }
        return result;
    }

    public String getAN() {
        String result = "";
        try {

            DateFormat sdf = DateFormat.getDateInstance(DateFormat.SHORT);
            String datum = sdf.format(rs.getDate("Von"));

            result += "<html><body>";
            result += "<font color=\"green\">" + datum + "; ";
            result += DBRetrieve.getUsername(rs.getString("AnUKennung")) + "</font>";
            result += "</body></html>";

        } catch (SQLException ex) {
            new DlgException(ex);
        }
        return result;
    }

    public String getAB() {
        String result = "";
        try {
            if (rs.getDate("Bis") != null && rs.getTimestamp("Bis").getTime() < SYSConst.BIS_AUF_WEITERES.getTimeInMillis()) {
                DateFormat sdf = DateFormat.getDateInstance(DateFormat.SHORT);
                String datum = sdf.format(rs.getDate("Bis"));

                result += "<html><body>";
                result += "<font color=\"red\">" + datum + "; ";
                result += DBRetrieve.getUsername(rs.getString("AbUKennung")) + "</font>";
                result += "</body></html>";
            }
        } catch (SQLException ex) {
            new DlgException(ex);
        }
        return result;
    }

    private boolean isAbgesetzt() throws SQLException {
        return false;
        // Sturzgeschichte
        //return rs.getDate("Bis") != null && SYSCalendar.sameDay(rs.getDate("Bis"), SYSCalendar.today_date()) <= 0;
    }

    public Object getValueAt(int r, int c) {
        Object result = null;
        boolean katchange = (r == 0) || zebra[r - 1] != zebra[r];
        try {
            rs.absolute(r + 1);
            long planid = rs.getLong("PlanID");
            //OPDE.getLogger().debug(this.toString() + ":" + verid);
            switch (c) {
                case COL_KATEGORIE: {
                    String res = "";
                    if (katchange) {
                        res += "<font color=\"blue\" >";
                        res += "<b>" + rs.getString("Bezeichnung") + "</b>";
                        res += "</font>";
                    }
                    result = res;
                    break;
                }
                case COL_BEMERKUNG: {
//                    int vrganzahl = rs.getInt("vrg.Anzahl");
//                    if (vrganzahl > 0) {
//                        result = result + "<font color=\"red\">&#9679;</font>";
//                        OPDE.getLogger().debug(result);
//                    }
                    if (this.details) {
                        result = DBHandling.getPlanungAsHTML(planid);
                    } else {
                        result = getBemerkung();
                    }
                    break;
                }
                case COL_AN: {
                    result = getAN();
                    break;
                }
                case COL_AB: {
                    result = getAB();
                    break;
                }
                case COL_PLANID: {
                    result = planid;
                    break;
                }
                case COL_ABGESETZT: {
                    result = isAbgesetzt();
                    break;
                }
                case COL_VON: {
                    result = rs.getTimestamp("Von");
                    break;
                }
                case COL_BIS: {
                    result = rs.getTimestamp("Bis");
                    break;
                }
                case COL_HTML: {
                    result = DBHandling.getPlanungAsHTML(planid);
                    break;
                }
                case COL_STICHWORT: {
                    result = rs.getString("Stichwort");
                    break;
                }
                case COL_ANUKENNUNG: {
                    result = rs.getString("AnUKennung");
                    break;
                }
                case COL_ABUKENNUNG: {
                    result = rs.getString("AbUKennung");
                    break;
                }
                case COL_ZEBRA: {
                    result = zebra[r];
                    break;
                }
                default: {
                    result = "!!FEHLER!!";
                }
            }
        } catch (SQLException se) {
            new DlgException(se);
        }
        return result;
    }
}
