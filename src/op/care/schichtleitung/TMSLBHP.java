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

package op.care.schichtleitung;

import op.OPDE;
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

/**
 * @author tloehr
 */
public class TMSLBHP
        extends AbstractTableModel {
    //public static final int COL_MassID = 0;
    public static final int COL_BEZEICHNUNG = 0;
    //public static final int COL_ZUSATZ = 1;
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
    public static final int COL_REICHTVORRAT = 13;
    public static final int COL_BISPACKENDE = 14;
    public static final int COL_SALDO = 15;
    public static final int COL_PACKEINHEIT = 16;
    public static final int COL_ABDATUM = 17;

    public static final int STATUS_OFFEN = 0;
    public static final int STATUS_ERLEDIGT = 1;
    public static final int STATUS_VERWEIGERT = 2;

    ResultSet rs;
    PreparedStatement stmt;
    String sql;
    boolean withTime;

    /**
     * @param schicht entsprechen OC_Const.ZEIT
     */
    public TMSLBHP(String currentBW, Date datum, boolean withTime, int schicht) {
        super();
        this.withTime = withTime;
        String filter = "";
        if (schicht != SYSConst.ZEIT_ALLES) {
            filter = " WHERE (SZeit >= ? AND SZeit <= ?) OR (SZeit = 0 AND TIME(Soll) >= ? AND TIME(Soll) <= ?) ";
        }

        try {
            sql = "SELECT * FROM " +
                    "((" +
                    " SELECT v.VerID, bhp.BHPID, bhp.BHPPID, v.BWKennung, v.MassID, mp.Bezeichnung, daf.Zusatz, bhp.Soll, bhp.SZeit, " +
                    "       CASE bhp.SZeit WHEN 0 THEN 'Uhrzeit' WHEN 1 THEN 'NachtMo' WHEN 2 THEN 'Morgens' WHEN 3 THEN 'Mittags' " +
                    "       WHEN 4 THEN 'Nachmittags' WHEN 5 THEN 'Abends' WHEN 6 THEN 'NachtAb' ELSE '!FEHLER!' END SZeitText, bhp.Dosis, " +
                    "       bhp.Status, v.Bemerkung vbemerkung, bhp.Bemerkung, bhp.UKennung, daf.DafID, bhp._mdate, v.SitID, ifnull(saldo,0) saldo, daf.APV," +
                    "       daf.Kalkulieren, v.BisPackEnde, F.Zubereitung, F.AnwText, F.PackEinheit, F.AnwEinheit, v.AbDatum," +
                    "       ifnull(bestand.DafID, 0) bestandDafID, M1.Bezeichnung mptext1, D1.Zusatz dafzusatz1, " +
                    "       ifnull(bestand.BestID, 0) BestID " +
                    " FROM BHP bhp " +
                    " INNER JOIN BHPPlanung bhpp ON bhp.BHPPID = bhpp.BHPPID" +
                    " INNER JOIN BHPVerordnung v ON bhpp.VerID = v.VerID" +
                    " INNER JOIN MPDarreichung daf ON daf.DafID = v.DafID" +
                    " INNER JOIN MProdukte mp ON mp.MedPID = daf.MedPID" +
                    " INNER JOIN MPFormen F ON daf.FormID = F.FormID" +
                    " LEFT OUTER JOIN " +
                    " (" +
                    "       SELECT DISTINCT a.VorID, b.DafID, a.saldo FROM (" +
                    "           SELECT best.VorID, best.DafID, sum(buch.Menge) saldo FROM MPBestand best " +
                    "           INNER JOIN MPBuchung buch ON buch.BestID = best.BestID " +
                    "           GROUP BY VorID" +
                    "       ) a " +
                    "       INNER JOIN (" +
                    "           SELECT best.VorID, best.DafID FROM MPBestand best " +
                    "       ) b ON a.VorID = b.VorID " +
                    "       INNER JOIN MPVorrat vrr ON a.VorID = vrr.VorID " +
                    "       WHERE vrr.BWKennung=? AND vrr.Bis = '9999-12-31 23:59:59'" +
                    " ) vor ON vor.DafID = v.DafID " +
                    // Hier kommen jetzt die Bestände im Anbruch dabei. Die Namen der Medikamente könnten ja vom
                    // ursprünglich verordneten abweichen.
                    " LEFT OUTER JOIN( " +
                    "   SELECT VorID, BestID, DafID " +
                    "   FROM MPBestand " +
                    "   WHERE Aus = '9999-12-31 23:59:59' AND Anbruch < now() " +
                    " ) bestand ON bestand.VorID = vor.VorID " +
                    " LEFT OUTER JOIN MPDarreichung D1 ON bestand.DafID = D1.DafID " +
                    " LEFT OUTER JOIN MProdukte M1 ON M1.MedPID = D1.MedPID " +
                    //
                    " WHERE Date(Soll)=? AND BWKennung=?" +
                    " ) " +
                    " UNION " + //-------------------------------------------------
                    " ( " +
                    " SELECT v.VerID, bhp.BHPID, bhp.BHPPID, v.BWKennung, v.MassID, mass.Bezeichnung, null Zusatz, bhp.Soll, bhp.SZeit, " +
                    "       CASE bhp.SZeit WHEN 0 THEN 'Uhrzeit' WHEN 1 THEN 'NachtMo' WHEN 2 THEN 'Morgens' WHEN 3 THEN 'Mittags' " +
                    "       WHEN 4 THEN 'Nachmittags' WHEN 5 THEN 'Abends' WHEN 6 THEN 'NachtAb' ELSE '!FEHLER!' END SZeitText, null Dosis, " +
                    "       bhp.Status, v.Bemerkung vbemerkung, bhp.Bemerkung, bhp.UKennung, 0 DafID, bhp._mdate, v.SitID, 0 saldo, 1 APV," +
                    "       0 Kalkulieren, 0 BisPackEnde, 0 Zubereitung, 0 AnwText, 0 PackEinheit, 0 AnwEinheit, v.AbDatum, 0, null, null," +
                    "       0 BestID  " +
                    " FROM BHP bhp " +
                    " INNER JOIN BHPPlanung bhpp ON bhp.BHPPID = bhpp.BHPPID" +
                    " INNER JOIN BHPVerordnung v ON bhpp.VerID = v.VerID" +
                    " INNER JOIN Massnahmen mass ON v.MassID = mass.MassID" +
                    " WHERE Date(Soll)=? AND BWKennung=? AND v.DafID = 0" +
                    ")) plan " +
                    filter +
                    " ORDER BY Soll, SZeit";
            stmt = OPDE.getDb().db.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            //Date d = new Date(new GregorianCalendar(2006,GregorianCalendar.AUGUST,11).getTimeInMillis());
            stmt.setString(1, currentBW);
            stmt.setDate(2, new java.sql.Date(datum.getTime()));
            stmt.setString(3, currentBW);
            stmt.setDate(4, new java.sql.Date(datum.getTime()));
            stmt.setString(5, currentBW);

            if (schicht != SYSConst.ZEIT_ALLES) {
                ArrayList al = SYSCalendar.getZeiten4Schicht((byte) schicht);
                String zeit1 = al.get(2).toString();
                String zeit2 = al.get(3).toString();
                int schicht1 = ((Integer) al.get(0)).intValue();
                int schicht2 = ((Integer) al.get(1)).intValue();
                stmt.setInt(6, schicht1);
                stmt.setInt(7, schicht2);
                stmt.setString(8, zeit1);
                stmt.setString(9, zeit2);
            }

            rs = stmt.executeQuery();
            rs.first();
        } // try
        catch (SQLException se) {
//            new DlgException(se);
        } // catch
    }

    public void reload(int row, int col) {
        try {
            rs = stmt.executeQuery();
            rs.first();
            fireTableRowsUpdated(row, col);
        } catch (SQLException se) {
//            new DlgException(se);
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
        int result = 7;
        if (withTime) {
            result++;
        }
        return result;
    }


    public Class getColumnClass(int c) {
        switch (c) {
            //case COL_MassID:{ return String.class;}
            case COL_BEZEICHNUNG: {
                return String.class;
            }
            //case COL_ZUSATZ:{ return String.class;}
            case COL_ZEIT: {
                return String.class;
            }
            case COL_STATUS: {
                return Integer.class;
            }
            case COL_DOSIS: {
                return Double.class;
            }
            case COL_UKENNUNG: {
                return String.class;
            }
            case COL_BEMPLAN: {
                return String.class;
            }
            case COL_BEMBHP: {
                return String.class;
            }
            case COL_BHPID: {
                return Long.class;
            }
            case COL_BHPPID: {
                return Long.class;
            }
            case COL_MDATE: {
                return Long.class;
            }
            case COL_VERID: {
                return Long.class;
            }
            case COL_DAFID: {
                return Long.class;
            }
            case COL_SITID: {
                return Long.class;
            }
            case COL_REICHTVORRAT: {
                return Boolean.class;
            }
            case COL_BISPACKENDE: {
                return Boolean.class;
            }
            case COL_SALDO: {
                return Double.class;
            }
            case COL_PACKEINHEIT: {
                return Integer.class;
            }
            case COL_ABDATUM: {
                return Long.class;
            }
        }
        return String.class;
    }

    // Abgesetzt nur dann ausstreichen, wenn der BHP für den heutigem Tag ist.
    private boolean isAbgesetzt() throws SQLException {
        return rs.getDate("AbDatum") != null && SYSCalendar.sameDay(rs.getDate("AbDatum"), SYSCalendar.today_date()) == 0;
    }

    private String getMassnahme() {
        String result = "<html><body>";
        try {
            if (isAbgesetzt()) {
                result += "<s>"; // Abgesetzte
            }
            if (rs.getLong("DafID") == 0) {
                result += rs.getString("MassID") + " - " + rs.getString("Bezeichnung");
            } else {
                if (rs.getLong("bestandDafID") > 0 && rs.getLong("bestandDafID") != rs.getLong("DafID")) { // Nur bei Abweichung.
                    result += "<b>" + rs.getString("mptext1").replaceAll("-", "- ") +
                            SYSTools.catchNull(rs.getString("dafzusatz1"), " ", "") + "</b>" + ", " +
                            SYSTools.catchNull(rs.getString("Zubereitung"), ", ", ", ") +
                            SYSTools.catchNull(rs.getString("AnwText").equals("") ? SYSConst.EINHEIT[rs.getInt("AnwEinheit")] : rs.getString("AnwText"));
                } else {
                    result += "<b>" + rs.getString("Bezeichnung").replaceAll("-", "- ") +
                            SYSTools.catchNull(rs.getString("Zusatz"), " ", "") + "</b>" + ", " +
                            SYSTools.catchNull(rs.getString("Zubereitung"), ", ", ", ") +
                            SYSTools.catchNull(rs.getString("AnwText").equals("") ? SYSConst.EINHEIT[rs.getInt("AnwEinheit")] : rs.getString("AnwText"));
                }
            }
            if (isAbgesetzt()) {
                result += "</s>"; // Abgesetzte
            }
        } catch (SQLException ex) {
//            new DlgException(ex);
        }

        result += "</body></html>";
        return result;
    }

    public Object getValueAt(int r, int c) {
        Object result = null;
        try {
            rs.absolute(r + 1);
            switch (c) {
                //case COL_MassID : {result = rs.getString("MassID"); break;}
                case COL_BEZEICHNUNG: {
                    result = getMassnahme();
                    break;
                } // rs.getString("Bezeichnung"); break;}
//                case COL_ZUSATZ : {
//                    result = rs.getString("Zusatz");
//                    result = (result == null ? "" : result);
//                    break;
//                }
                case COL_DOSIS: {
                    result = rs.getDouble("Dosis");
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
                    result = rs.getInt("Status");
                    break;
                }
                case COL_UKENNUNG: {
                    result = rs.getString("UKennung");
                    if (result == null) {
                        result = "";
                    }
                    break;
                }
                case COL_BEMPLAN: {
                    result = "";
                    if (rs.getLong("BestID") > 0) {
                        result = "<i>Bestand im Anbruch Nr.: " + rs.getLong("BestID") + "</i><br/>";
                    }
                    if (!SYSTools.catchNull(rs.getString("vbemerkung")).equals("")) {
                        result = result.toString() + "<b>Bemerkung:</b> " + rs.getString("vbemerkung");
                    }
                    break;
                }
                case COL_BEMBHP: {
                    result = rs.getString("bemerkung");
                    break;
                }
                case COL_BHPID: {
                    result = rs.getLong("BHPID");
                    break;
                }
                case COL_BHPPID: {
                    result = rs.getLong("BHPPID");
                    break;
                }
                case COL_MDATE: {
                    result = rs.getTimestamp("_mdate").getTime();
                    break;
                }
                case COL_VERID: {
                    result = rs.getLong("VerID");
                    break;
                }
                case COL_DAFID: {
                    result = rs.getLong("DafID");
                    break;
                }
                case COL_SITID: {
                    result = rs.getLong("SitID");
                    break;
                }
                case COL_SALDO: {
                    result = rs.getDouble("Saldo");
                    break;
                }
                case COL_PACKEINHEIT: {
                    result = rs.getInt("PackEinheit");
                    break;
                }
                case COL_REICHTVORRAT: {
                    long dafid = rs.getLong("DafID");
                    if (dafid > 0) {
                        double vorrat = rs.getDouble("saldo");
                        double dosis = rs.getDouble("dosis");
                        double apv = rs.getDouble("apv");
                        double packdosis = dosis / apv; // in der PackEinheit
                        boolean kalkulieren = rs.getBoolean("Kalkulieren");
                        result = new Boolean(!kalkulieren || packdosis <= vorrat);
                    } else {
                        result = new Boolean(true);
                    }
                    break;
                }
                case COL_BISPACKENDE: {
                    result = new Boolean(rs.getBoolean("BisPackEnde"));
                    break;
                }
                case COL_ABDATUM: {
                    result = rs.getTimestamp("AbDatum").getTime();
                    break;
                }
                default: {
                    result = "";
                }
            }
        } catch (SQLException se) {
//            new DlgException(se);
        }
        return result;
    }
}