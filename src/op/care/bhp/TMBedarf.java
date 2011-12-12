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

import op.OPDE;
import op.tools.DlgException;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.swing.table.AbstractTableModel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author tloehr
 */
public class TMBedarf
        extends AbstractTableModel {

    public static final int COL_SIT = 0;
    public static final int COL_MSSN = 1;
    public static final int COL_Dosis = 2;
    public static final int COL_Hinweis = 3;
    public static final int COL_VERID = 4;
    public static final int COL_BHPPID = 5;
    public static final int COL_MaxEDosis = 6;
    public static final int COL_DAFID = 7;
    public static final int COL_TAGESDOSIS = 8;
    public static final int COL_MAXERREICHT = 9;
    ResultSet rs;
    PreparedStatement stmt;
    String sql;

    public TMBedarf(String bwkennung) {
        super();
        try {
            sql =
                    " SELECT v.DafID, v.VerID, p.BHPPID, v.MassID, ifnull(vor.Saldo,0) saldo, mp.Bezeichnung mptext, m.Bezeichnung mssntext, " +
                            " s.Text sittext, p.MaxAnzahl, p.MaxEDosis, bisher.tagesdosis, d.Zusatz, f.Zubereitung, f.AnwText, f.AnwEinheit, f.PackEinheit, f.AnwEinheit," +
                            " v.Bemerkung, bestand.APV APV, bestand.Summe bestsumme, ifnull(bestand.BestID, 0) BestID " +
                            " FROM BHPVerordnung v " +
                            " INNER JOIN Situationen s ON v.SitID = s.SitID " +
                            " INNER JOIN BHPPlanung p ON v.VerID = p.VerID" +
                            " INNER JOIN Massnahmen m ON v.MassID = m.MassID " +
                            " LEFT OUTER JOIN MPDarreichung d ON v.DafID = d.DafID " +
                            " LEFT OUTER JOIN MProdukte mp ON mp.MedPID = d.MedPID " +
                            " LEFT OUTER JOIN MPFormen f ON d.FormID = f.FormID" +
                            // Dieser Konstrukt bestimmt die Vorräte für einen Bewohner
                            // Dabei wird berücksichtigt, dass ein Vorrat unterschiedliche Hersteller umfassen
                            // kann. Dies wird durch den mehrfach join erreicht. Dadurch stehen die verschiedenen
                            // DafIDs der unterschiedlichen Produkte im selben Vorrat jeweils in verschiedenen Zeilen.
                            // Durch den LEFT OUTER JOIN pickt sich die Datenbank die richtigen Paare heraus.
                            //                    " (" +
                            //                    "       SELECT DISTINCT a.VorID, b.DafID, a.saldo FROM (" +
                            //                    "           SELECT best.VorID, best.DafID, sum(buch.Menge) saldo FROM MPBestand best " +
                            //                    "           INNER JOIN MPBuchung buch ON buch.BestID = best.BestID " +
                            //                    "           GROUP BY VorID" +
                            //                    "       ) a " +
                            //                    "       INNER JOIN (" +
                            //                    "           SELECT best.VorID, best.DafID FROM MPBestand best " +
                            //                    "       ) b ON a.VorID = b.VorID " +
                            //                    "       INNER JOIN MPVorrat vrr ON a.VorID = vrr.VorID " +
                            //                    "       WHERE vrr.BWKennung=? AND vrr.Bis = '9999-12-31 23:59:59'" +
                            //                    " ) vor ON vor.DafID = v.DafID " +
                            " LEFT OUTER JOIN " +
                            " ( " +
                            "   SELECT DISTINCT a.VorID, b.DafID, a.saldo FROM ( " +
                            "           SELECT best.VorID, best.DafID, sum(buch.Menge) saldo FROM MPBestand best " +
                            "           INNER JOIN MPBuchung buch ON buch.BestID = best.BestID " +
                            "           INNER JOIN MPVorrat vor1 ON best.VorID = vor1.VorID" +
                            "           WHERE vor1.BWKennung=? AND vor1.Bis = '9999-12-31 23:59:59'" +
                            "           GROUP BY VorID" +
                            "   ) a  " +
                            "   INNER JOIN (" +
                            "       SELECT best.VorID, best.DafID FROM MPBestand best " +
                            "   ) b ON a.VorID = b.VorID " +
                            " ) vor ON vor.DafID = v.DafID " +
                            //
                            " LEFT OUTER JOIN" +
                            " 	(" +
                            "       SELECT b3.VerID, sum(b1.dosis) tagesdosis " +
                            "       FROM BHP b1" +
                            "       INNER JOIN BHPPlanung b2 ON b1.BHPPID = b2.BHPPID" +
                            "       INNER JOIN BHPVerordnung b3 ON b3.VerID = b2.VerID" +
                            "       WHERE b3.BWKennung=? AND b3.AbDatum = '9999-12-31 23:59:59'" +
                            "       AND DATE(b1.Ist) = Date(now()) AND b1.Status = 1" +
                            "       GROUP BY b3.VerID" +
                            " 	) bisher ON bisher.VerID = v.VerID" +
                            // Hier kommen jetzt die Bestände im Anbruch dabei. Die Namen der Medikamente könnten ja vom
                            // ursprünglich verordneten abweichen.
                            " LEFT OUTER JOIN( " +
                            "       SELECT best1.NextBest, best1.VorID, best1.BestID, best1.DafID, best1.APV, SUM(buch1.Menge) summe " +
                            "       FROM MPBestand best1 " +
                            "       INNER JOIN MPBuchung buch1 ON buch1.BestID = best1.BestID " +
                            "       WHERE best1.Aus = '9999-12-31 23:59:59' AND best1.Anbruch < now() " +
                            "       GROUP BY best1.BestID" +
                            " ) bestand ON bestand.VorID = vor.VorID " +
                            " LEFT OUTER JOIN MPDarreichung D1 ON bestand.DafID = D1.DafID " +
                            " LEFT OUTER JOIN MProdukte M1 ON M1.MedPID = D1.MedPID " +
                            //                    " LEFT OUTER JOIN" +
                            //                    " ( " +
                            //                    "   SELECT VorID, BestID, DafID, APV " +
                            //                    "   FROM MPBestand " +
                            //                    "   WHERE Aus = '9999-12-31 23:59:59' AND Anbruch < now() " +
                            //                    " ) bestand ON bestand.VorID = vor.VorID " +
                            //
                            " WHERE v.BWKennung = ? AND v.AbDatum = '9999-12-31 23:59:59' " +
                            " ORDER BY sittext";
            stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setString(1, bwkennung);
            stmt.setString(2, bwkennung);
            stmt.setString(3, bwkennung);
            rs = stmt.executeQuery();
            rs.first();
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

    public int getColumnCount() {
        return 4;
    }

    public Class getColumnClass(int c) {
        Class result;
        switch (c) {
            case COL_VERID: {
                result = Long.class;
                break;
            }
            case COL_BHPPID: {
                result = Long.class;
                break;
            }
            case COL_MaxEDosis: {
                result = Double.class;
                break;
            }
            case COL_TAGESDOSIS: {
                result = Double.class;
                break;
            }
            case COL_MAXERREICHT: {
                result = Boolean.class;
                break;
            }
            default: {
                result = String.class;
            }
        }
        return result;
    }

    // Dosis from Double
//    private String dfd(double d) {
//        String result;
//        if (d == 0) {
//            result = "";
//        } else {
//            result = Double.toString(d);
//        }
//        return result;
//    }
    private String getMassnahme() {
        String result = "<html><body>";
        try {
            if (rs.getLong("DafID") == 0) {
                result += rs.getString("mssntext");
            } else {
                result += "<b>" + rs.getString("mptext").replaceAll("-", "- ") + "</b>" +
                        SYSTools.catchNull(rs.getString("Zusatz"), ", ", "") + ", " +
                        SYSTools.catchNull(rs.getString("F.Zubereitung"), "", ", ") +
                        SYSTools.catchNull(rs.getString("AnwText").equals("") ? SYSConst.EINHEIT[rs.getInt("AnwEinheit")] : rs.getString("AnwText"));
            }
        } catch (SQLException ex) {
            new DlgException(ex);
        }

        result += "</body></html>";
        return result;
    }

    private String getHinweis() {
        String result = "";
        try {
            if (rs.getString("v.Bemerkung") != null && !rs.getString("v.Bemerkung").equals("")) {
                result += "<b><u>Bemerkung:</u></b> " + rs.getString("Bemerkung");
            }
        } catch (SQLException ex) {
            new DlgException(ex);
        }
        return (result.equals("") ? "" : "<html><body>" + result + "</body></html>");
    }

    public Object getValueAt(int r, int c) {
        Object result = null;
        try {
            rs.absolute(r + 1);
            long verid = rs.getLong("VerID");
            //OPDE.debug(this.toString() + ":" + verid);
            switch (c) {
                // Hier muss die Situation stehen. Dann gehts im DlgBedarf weiter
                case COL_SIT: {
                    String tmp = "<html><body>";
                    tmp += rs.getString("sittext");
                    tmp += "</body></html>";
                    result = tmp;
                    break;
                }
                case COL_MSSN: {
                    result = getMassnahme();
                    break;
                }
                case COL_Dosis: {
                    String tmp = "<html><body>";
                    tmp += op.care.verordnung.DBRetrieve.getDosis(verid);
                    double tagesdosis = rs.getDouble("bisher.tagesdosis");
                    tmp += "Bisherige Tagesdosis: " + tagesdosis + "<br/>";
                    if (rs.getLong("DafID") > 0) { // Gilt nur für Medikamente, sonst passt das nicht
                        double maxanzahl = rs.getDouble("p.MaxAnzahl");
                        double edosis = rs.getDouble("p.MaxEDosis");

                        if (rs.getDouble("saldo") > 0) {

                            // Wenn die Tagesdosis bei einer erneuten Gabe des Medikamentes überschritten würde,
                            // dann melden.
                            if (tagesdosis + edosis > maxanzahl * edosis) {
                                tmp += "<b>Keine weitere Gabe des Medikamentes mehr möglich. Tagesdosis ist erreicht</b><br/>";
                            }

                            tmp += "<u>Vorrat:</u> <font color=\"green\">" + rs.getDouble("saldo") + " " + SYSConst.EINHEIT[rs.getInt("f.PackEinheit")] +
                                    " " + rs.getString("Zubereitung") + "</font>";
                            if (rs.getInt("f.PackEinheit") != rs.getInt("f.AnwEinheit")) {
                                double anwmenge = rs.getDouble("saldo") * rs.getDouble("APV");

                                tmp += " <i>entspricht " + anwmenge + " ";
                                tmp += (rs.getString("AnwText") == null || rs.getString("AnwText").equals("") ? SYSConst.EINHEIT[rs.getInt("AnwEinheit")] : rs.getString("AnwText"));
                                tmp += "</i>";
                            }
                            if (rs.getLong("BestID") > 0) {
                                tmp += "<br/>Bestand im Anbruch Nr.: <b><font color=\"green\">" + rs.getLong("BestID") + "</font></b>";

                                if (rs.getDouble("bestsumme") != rs.getDouble("saldo")) {
                                    tmp += "<br/>Restmenge im Anbruch: <b><font color=\"green\">" + rs.getDouble("bestsumme") + " " +
                                            SYSConst.EINHEIT[rs.getInt("PackEinheit")] + "</font></b>";
                                    if (rs.getInt("f.PackEinheit") != rs.getInt("f.AnwEinheit")) {
                                        double anwmenge = SYSTools.roundScale2(rs.getDouble("bestsumme") * rs.getDouble("APV"));
                                        tmp += " <i>entspricht " + anwmenge + " " +//SYSConst.EINHEIT[rs.getInt("f.AnwEinheit")]+"</i>";
                                                (rs.getString("AnwText") == null || rs.getString("AnwText").equals("") ? SYSConst.EINHEIT[rs.getInt("AnwEinheit")] : rs.getString("AnwText")) + "</i>";
                                    }
                                }
                            } else {
                                tmp += "<br/><b><font color=\"red\">Kein Bestand im Anbruch. Vergabe nicht möglich.</font></b>";
                            }
                        } else {
                            tmp += "<b><font color=\"red\">Der Vorrat an diesem Medikament ist <u>leer</u>.</font></b>";
                        }

                    }
                    tmp += "</body></html>";
                    result = tmp;
                    break;
                }
                case COL_Hinweis: {
                    result = getHinweis();
                    break;
                }
                case COL_VERID: {
                    result = verid;
                    break;
                }
                case COL_BHPPID: {
                    result = rs.getLong("BHPPID");
                    break;
                }
                case COL_DAFID: {
                    result = rs.getLong("DAFID");
                    break;
                }
                case COL_MaxEDosis: {
                    result = rs.getDouble("p.MaxEDosis");
                    break;
                }
                case COL_TAGESDOSIS: {
                    result = rs.getDouble("bisher.tagesdosis");
                    break;
                }
                case COL_MAXERREICHT: {
                    double tagesdosis = rs.getDouble("bisher.tagesdosis");
                    double maxanzahl = rs.getDouble("p.MaxAnzahl");
                    double edosis = rs.getDouble("p.MaxEDosis");
                    // Wenn die Tagesdosis bei einer erneuten Gabe des Medikamentes überschritten würde,
                    // dann melden.
                    result = new Boolean(tagesdosis + edosis > maxanzahl * edosis);
                    break;
                }
//                case COL_REICHTVORRAT : {
//                    long dafid = rs.getLong("DafID");
//                    if (dafid > 0){
//                        double vorrat = rs.getDouble("saldo");
//                        double edosis = rs.getDouble("p.MaxEDosis");
//                        double apv = rs.getDouble("apv");
//                        double packdosis = edosis / apv; // in der PackEinheit
//                        boolean kalkulieren = rs.getBoolean("Kalkulieren");
//                        result = new Boolean(!kalkulieren || packdosis <= vorrat);
//                    } else {
//                        result = new Boolean(true);
//                    }
//                    break;
//                } 
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