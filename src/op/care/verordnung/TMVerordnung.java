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
package op.care.verordnung;

import op.OPDE;
import op.tools.DlgException;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.swing.table.AbstractTableModel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.HashMap;

/**
 * @author tloehr
 */
public class TMVerordnung
        extends AbstractTableModel {

    public static final int COL_MSSN = 0;
    public static final int COL_Dosis = 1;
    public static final int COL_Hinweis = 2;
    public static final int COL_AN = 3;
    public static final int COL_AB = 4;
    public static final int COL_INFO = 5;
    public static final int COL_DOK = 6;
    public static final int COL_VERID = 7;
    public static final int COL_DAFID = 8;
    public static final int COL_ANARZTID = 9;
    public static final int COL_VORID = 10;
    public static final int COL_ABGESETZT = 11;
    public static final int COL_ABDATUM = 12;
    public static final int COL_SITID = 13;
    public static final int COL_ABARZTID = 14;
    public static final int COL_ANKHID = 15;
    public static final int COL_ABKHID = 16;
    public static final int COL_BESTID = 17;
    public static final int COL_NEXTBEST = 18;
    public final String[] debug = {"COL_MSSN", "COL_Dosis", "COL_Hinweis", "COL_AN", "COL_AB", "COL_INFO", "COL_DOK", "COL_VERID", "COL_BESTELLID",
            "COL_ANARZTID", "COL_VORID", "COL_ABGESETZT", "COL_ABDATUM", "COL_SITID", "COL_ABARZTID", "COL_ANKHID", "COL_ABKHID"
    };
    ResultSet rs;
    PreparedStatement stmt;
    String sql;
    boolean mitBestand;

    HashMap cache;

    public TMVerordnung(String bwkennung, boolean abgesetzt, boolean medi, boolean ohneMedi, boolean bedarf, boolean regel, boolean bestand) {
        super();
        this.cache = new HashMap();
        this.mitBestand = bestand;
        try {
            sql = " SELECT v.VerID, v.AnDatum, v.AbDatum, an.Anrede, an.Titel, an.Name, ab.Anrede, khan.Name, ab.Titel, ab.Name, " +
                    " khab.Name, v.AnUKennung, v.AbUKennung, v.MassID, Ms.Bezeichnung mssntext, v.DafID," +
                    " v.SitID, S.Text sittext, v.Bemerkung, v.BisPackEnde, M.Bezeichnung mptext, D.Zusatz, " +
                    " F.Zubereitung, F.AnwText, F.PackEinheit, ifnull(bestand.DafID, 0) bestandDafID, M1.Bezeichnung mptext1, D1.Zusatz, " +
                    " F.AnwEinheit, bestand.APV, ifnull(vor.VorID, 0) vorid, vor.saldo, v.AnArztID, " +
                    " v.AbArztID, v.AnKHID, v.AbKHID, bestand.Summe bestsumme, " +
                    " ifnull(bestand.BestID, 0) BestID, ifnull(bestand.NextBest, 0) nextbest " +
                    " FROM BHPVerordnung v" +
                    " INNER JOIN Massnahmen Ms ON Ms.MassID = v.MassID" +
                    " LEFT OUTER JOIN MPDarreichung D ON v.DafID = D.DafID" +
                    " LEFT OUTER JOIN Arzt an ON an.ArztID = v.AnArztID" +
                    " LEFT OUTER JOIN KH khan ON khan.KHID = v.AnKHID" +
                    " LEFT OUTER JOIN Arzt ab ON ab.ArztID = v.AbArztID" +
                    " LEFT OUTER JOIN KH khab ON khab.KHID = v.AbKHID" +
                    " LEFT OUTER JOIN MProdukte M ON M.MedPID = D.MedPID" +
                    " LEFT OUTER JOIN MPFormen F ON D.FormID = F.FormID" +
                    " LEFT OUTER JOIN Situationen S ON v.SitID = S.SitID" +
                    // Dieser Konstrukt bestimmt die Vorräte für einen Bewohner
                    // Dabei wird berücksichtigt, dass ein Vorrat unterschiedliche Hersteller umfassen
                    // kann. Dies wird durch den mehrfach join erreicht. Dadurch stehen die verschiedenen
                    // DafIDs der unterschiedlichen Produkte im selben Vorrat jeweils in verschiedenen Zeilen.
                    // Durch den LEFT OUTER JOIN pickt sich die Datenbank die richtigen Paare heraus.
                    " LEFT OUTER JOIN " +
                    " ( " +
                    "       SELECT DISTINCT a.VorID, b.DafID, a.saldo FROM ( " +
                    "       SELECT best.VorID, best.DafID, sum(buch.Menge) saldo FROM MPBestand best " +
                    "       INNER JOIN MPBuchung buch ON buch.BestID = best.BestID " +
                    "       INNER JOIN MPVorrat vor1 ON best.VorID = vor1.VorID" +
                    "       WHERE vor1.BWKennung=? AND vor1.Bis = '9999-12-31 23:59:59'" +
                    "       GROUP BY VorID" +
                    "   ) a  " +
                    "   INNER JOIN (" +
                    "       SELECT best.VorID, best.DafID FROM MPBestand best " +
                    "   ) b ON a.VorID = b.VorID " +
                    " ) vor ON vor.DafID = v.DafID " +
                   // " INNER JOIN " +
                    // Hier kommen die angehangen Dokumente hinzu
//                    " (" +
//                    " 	SELECT DISTINCT f1.VerID, ifnull(anzahl,0) anzahl" +
//                    " 	FROM BHPVerordnung f1" +
//                    " 	LEFT OUTER JOIN (" +
//                    " 		SELECT VerID, count(*) anzahl FROM SYSVER2FILE" +
//                    " 		GROUP BY VerID" +
//                    " 		) fa ON fa.VerID = f1.VerID" +
//                    " 	WHERE f1.BWKennung=?" +
//                    " ) fia ON fia.VerID = v.VerID " +
//                    // Hier die angehangenen Vorgänge
//                    " INNER JOIN " +
//                    " (" +
//                    " 	SELECT DISTINCT f2.VerID, ifnull(anzahl,0) anzahl" +
//                    " 	FROM BHPVerordnung f2" +
//                    " 	LEFT OUTER JOIN (" +
//                    " 		SELECT ForeignKey, count(*) anzahl FROM VorgangAssign" +
//                    " 		WHERE TableName='BHPVerordnung'" +
//                    " 		GROUP BY ForeignKey" +
//                    " 		) va ON va.ForeignKey = f2.VerID" +
//                    " 	WHERE f2.BWKennung=? " +
//                    " ) vrg ON vrg.VerID = v.VerID " +
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
                    " WHERE BWKennung=? ";
            if (!abgesetzt) {
                // sql += " AND v.AbDatum = '9999-12-31 23:59:59' ";
                sql += " AND date(v.AbDatum) >= date(now()) ";
            }
            if (!(medi && ohneMedi)) { // ungleich gesetzt
                if (medi) {
                    sql += " AND v.DafID > 0 ";
                } else {
                    sql += " AND v.DafID = 0 ";
                }
            }
            if (!(bedarf && regel)) { // ungleich gesetzt
                if (bedarf) {
                    sql += " AND v.SitID > 0 ";
                } else {
                    sql += " AND v.SitID = 0 ";
                }

            }
            sql += " ORDER BY v.SitID = 0, v.DafID <> 0, ifnull(mptext, mssntext)  ";
            stmt = OPDE.getDb().db.prepareStatement(sql);
            //OPDE.getLogger().debug(sql);
            stmt.setString(1, bwkennung);
            stmt.setString(2, bwkennung);
//            stmt.setString(3, bwkennung);
//            stmt.setString(4, bwkennung);
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
            System.out.println(se.getMessage());
            return -1;
        }
    }

    public int getColumnCount() {
        int result = 5;
        return result;
    }

    public Class getColumnClass(int c) {
        Class result;
        switch (c) {
            case COL_VERID: {
                result = Long.class;
                break;
            }
            case COL_INFO: {
                result = BitSet.class;
                break;
            }
            case COL_DOK: {
                result = Boolean.class;
                break;
            }
//            case COL_BESTELLID: {
//                result = Long.class;
//                break;
//            }
            case COL_ANARZTID: {
                result = Long.class;
                break;
            }
            case COL_VORID: {
                result = Long.class;
                break;
            }
            case COL_ABGESETZT: {
                result = Boolean.class;
                break;
            }
            case COL_ABDATUM: {
                result = Timestamp.class;
                break;
            }
            case COL_ABARZTID: {
                result = Long.class;
                break;
            }
            case COL_ANKHID: {
                result = Long.class;
                break;
            }
            case COL_ABKHID: {
                result = Long.class;
                break;
            }
            default: {
                result = String.class;
            }
        }
        return result;
    }

//    // Dosis from Double
//    private String dfd(double d) {
//        String result;
//        if (d == 0) {
//            result = "";
//        } else {
//            result = Double.toString(d);
//        }
//        return result;
//    }

    private String getMassnahme() throws SQLException {
        String result = "";

//        if (OPDE.getProps().getProperty("DEBUG").equals("true")){
//            result += "<b>"+rs.getLong("VerID")+"</b> ";
//        }

        if (isAbgesetzt()) {//rs.getDate("AbDatum") != null && rs.getTimestamp("AbDatum").getTime() <=  SYSCalendar.nowDB() ){
            result += "<s>"; // Abgesetzte
        }
        if (rs.getLong("DafID") == 0) {
            result += rs.getString("mssntext");
        } else {
            // Prüfen, was wirklich im Anbruch gegeben wird.
            if (rs.getLong("bestandDafID") > 0 && rs.getLong("bestandDafID") != rs.getLong("v.DafID")) { // Nur bei Abweichung.
                result += "<font face=\"Sans Serif\"><b>" + rs.getString("mptext1").replaceAll("-", "- ") +
                        SYSTools.catchNull(rs.getString("D1.Zusatz"), " ", "") + "</b></font>" +
                        SYSTools.catchNull(rs.getString("F.Zubereitung"), ", ", ", ") + " " +
                        SYSTools.catchNull(rs.getString("AnwText").equals("") ? SYSConst.EINHEIT[rs.getInt("AnwEinheit")] : rs.getString("AnwText"));
                result += " <i>(ursprünglich verordnet: " + rs.getString("mptext").replaceAll("-", "- ") +
                        SYSTools.catchNull(rs.getString("D.Zusatz"), " ", "") + "</i>";
            } else {
                result += "<font face=\"Sans Serif\"><b>" + rs.getString("mptext").replaceAll("-", "- ") +
                        SYSTools.catchNull(rs.getString("D.Zusatz"), " ", "") + "</b></font>" +
                        SYSTools.catchNull(rs.getString("F.Zubereitung"), ", ", ", ") + " " +
                        SYSTools.catchNull(rs.getString("AnwText").equals("") ? SYSConst.EINHEIT[rs.getInt("AnwEinheit")] : rs.getString("AnwText"));

            }
        }
        if (isAbgesetzt()) {//if (rs.getDate("AbDatum") != null && rs.getTimestamp("AbDatum").getTime() <=  SYSCalendar.nowDB() ){
            result += "</s>"; // Abgesetzte
            //OPDE.getLogger().debug(this.toString() + ": " + result);
        }

        return result;
    }

    private String getHinweis() {
        String result = "";
        try {
            if (rs.getLong("SitID") > 0) {
                result += "<b><u>Nur bei Bedarf:</u> <font color=\"blue\">" + rs.getString("sittext") + "</font></b><br/>";
            }
            if (rs.getString("Bemerkung") != null && !rs.getString("Bemerkung").equals("")) {
                result += "<b><u>Bemerkung:</u> </b>" + rs.getString("Bemerkung");
            }
        } catch (SQLException ex) {
            new DlgException(ex);
        }
        return (result.equals("") ? "" : "<html><body>" + result + "</body></html>");
    }

    public String getAN() {
        String result = "";
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
            String datum = sdf.format(rs.getDate("AnDatum"));

            result += "<html><body>";
            result += "<font color=\"green\">" + datum + "; ";
            if (rs.getLong("v.AnKHID") > 0) {
                result += rs.getString("khan.Name");
            }
            if (rs.getLong("v.AnArztID") > 0) {
                if (rs.getLong("v.AnKHID") > 0) {
                    result += " <i>bestätigt durch:</i> ";
                }
                result += rs.getString("an.Titel") + " ";
                if (OPDE.isAnonym()) {
                    result += rs.getString("an.Name").substring(0, 1) + "***";
                } else {
                    result += rs.getString("an.Name");
                }

            }
            result += "; " + op.tools.DBRetrieve.getUsername(rs.getString("AnUKennung")) + "</font>";
            result += "</body></html>";

        } catch (SQLException ex) {
            new DlgException(ex);
        }
        return result;
    }

    public String getAB() {
        String result = "";
        try {
            if (rs.getDate("AbDatum") != null && rs.getTimestamp("AbDatum").getTime() < SYSConst.BIS_AUF_WEITERES.getTimeInMillis()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
                String datum = sdf.format(rs.getDate("AbDatum"));

                result += "<html><body>";
                result += "<font color=\"red\">" + datum + "; ";
                if (rs.getLong("v.AbKHID") > 0) {
                    result += rs.getString("khab.Name");
                }
                if (rs.getLong("v.AbArztID") > 0) {
                    if (rs.getLong("v.AbKHID") > 0) {
                        result += " <i>bestätigt durch:</i> ";
                    }
                    result += rs.getString("ab.Titel");
                    if (OPDE.isAnonym()) {
                        result += rs.getString("ab.Name").substring(0, 1) + "***";
                    } else {
                        result += rs.getString("ab.Name");
                    }

                }
                result += "; " + op.tools.DBRetrieve.getUsername(rs.getString("AbUKennung")) + "</font>";
                result += "</body></html>";
            }
        } catch (SQLException ex) {
            new DlgException(ex);
        }
        return result;
    }

    private boolean isAbgesetzt() throws SQLException {
        return rs.getDate("AbDatum") != null && SYSCalendar.sameDay(rs.getDate("AbDatum"), SYSCalendar.today_date()) <= 0 && rs.getTimestamp("AbDatum").before(SYSCalendar.nowDBDate());
    }

    /**
     * Dient nur zu Optimierungszwecken. Damit die Datenbankzugriffe minimiert werden.
     * Lokaler Cache.
     *
     * @param verid
     * @return
     */
    private String getDosis(long verid) {
        String result = "";
        if (cache.containsKey(verid)) {
            result = cache.get(verid).toString();
        } else {
            result = op.care.verordnung.DBRetrieve.getDosis(verid);
            cache.put(verid, result);
        }
        return result;
    }

    public Object getValueAt(int r, int c) {
        Object result = null;
        try {
            rs.absolute(r + 1);
            long verid = rs.getLong("VerID");
            //OPDE.getLogger().debug(this.toString() + ":" + verid);
            switch (c) {
                case COL_MSSN: {
                    String res = "";
                    res = getMassnahme();
//                    if (fianzahl > 0) {
//                        res += "<font color=\"green\">&#9679;</font>";
//                    }
//                    if (vrganzahl > 0) {
//                        res += "<font color=\"red\">&#9679;</font>";
//                    }
                    result = res;
                    break;
                }
                case COL_Dosis: {
                    //long now = System.currentTimeMillis();
//                    OPDE.getLogger().debug("Start @" + now);
//                    OPDE.getLogger().debug(r);
//                    OPDE.getLogger().debug(debug[c]);

                    String tmp = "<html><body>";
                    tmp += getDosis(verid);

                    if (rs.getLong("DafID") > 0) { // Gilt nur für Medikamente, sonst passt das nicht
                        if (rs.getBoolean("BisPackEnde")) {
                            tmp += "nur bis Packungs Ende<br/>";
                        }
                        if (!isAbgesetzt() && mitBestand) {
                            if (rs.getDouble("saldo") > 0) {
                                tmp += "<b><u>Vorrat:</u> <font color=\"green\">" + SYSTools.roundScale2(rs.getDouble("saldo")) + " " +
                                        SYSConst.EINHEIT[rs.getInt("PackEinheit")] +
                                        "</font></b>";
                                if (rs.getInt("f.PackEinheit") != rs.getInt("f.AnwEinheit")) {
                                    double anwmenge = SYSTools.roundScale2(rs.getDouble("saldo") * rs.getDouble("APV"));
                                    tmp += " <i>entspricht " + SYSTools.roundScale2(anwmenge) + " " +//SYSConst.EINHEIT[rs.getInt("f.AnwEinheit")]+"</i>";
                                            (rs.getString("AnwText") == null || rs.getString("AnwText").equals("") ? SYSConst.EINHEIT[rs.getInt("AnwEinheit")] : rs.getString("AnwText")) + "</i>";
                                }
                                if (rs.getLong("BestID") > 0) {
                                    tmp += "<br/>Bestand im Anbruch Nr.: <b><font color=\"green\">" + rs.getLong("BestID") + "</font></b>";

                                    if (rs.getDouble("bestsumme") != rs.getDouble("saldo")) {
                                        tmp += "<br/>Restmenge im Anbruch: <b><font color=\"green\">" + SYSTools.roundScale2(rs.getDouble("bestsumme")) + " " +
                                                SYSConst.EINHEIT[rs.getInt("PackEinheit")] + "</font></b>";
                                        if (rs.getInt("f.PackEinheit") != rs.getInt("f.AnwEinheit")) {
                                            double anwmenge = SYSTools.roundScale2(rs.getDouble("bestsumme") * rs.getDouble("APV"));
                                            tmp += " <i>entspricht " + SYSTools.roundScale2(anwmenge) + " " +//SYSConst.EINHEIT[rs.getInt("f.AnwEinheit")]+"</i>";
                                                    (rs.getString("AnwText") == null || rs.getString("AnwText").equals("") ? SYSConst.EINHEIT[rs.getInt("AnwEinheit")] : rs.getString("AnwText")) + "</i>";
                                        }
                                    }
                                } else {
                                    tmp += "<br/><b><font color=\"red\">Kein Bestand im Anbruch. Vergabe nicht möglich.</font></b>";
                                }
//                                if (rs.getLong("BestellID") > 0){
//                                    tmp += "<br/>Produkt / Medikament wurde nachbestellt";
//                                }
                            } else {
                                tmp += "<b><font color=\"red\">Der Vorrat an diesem Medikament ist <u>leer</u>.</font></b>";
                            }
                        }
                    }
                    tmp += "</body></html>";
                    result = tmp;
                    //OPDE.getLogger().debug("END @" + System.currentTimeMillis());
                    //OPDE.getLogger().debug("Duration in sec" + (System.currentTimeMillis() - now));
                    break;
                }
                case COL_Hinweis: {
                    result = getHinweis();
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
                case COL_VERID: {
                    result = verid;
                    break;
                }

//                case COL_BESTELLID: {
//                    result = rs.getLong("BestellID");
//                    break;
//                }
                case COL_ANARZTID: {
                    result = rs.getLong("AnArztID");
                    break;
                }
                case COL_ABARZTID: {
                    result = rs.getLong("AbArztID");
                    break;
                }
                case COL_ANKHID: {
                    result = rs.getLong("AnKHID");
                    break;
                }
                case COL_ABKHID: {
                    result = rs.getLong("AbKHID");
                    break;
                }
                case COL_VORID: {
                    result = rs.getLong("vorid");
                    break;
                }
                case COL_ABGESETZT: {
                    // Abgesetzt ist alles, was nicht heute oder in der vergangenheit endete.
                    result = isAbgesetzt();// && rs.getTimestamp("AbDatum").getTime() <= SYSCalendar.nowDB();
                    break;
                }
                case COL_ABDATUM: {
                    result = rs.getTimestamp("AbDatum");
                    break;
                }
                case COL_SITID: {
                    result = rs.getLong("SitID");
                    break;
                }
                case COL_BESTID: {
                    result = rs.getLong("BestID");
                    break;
                }
                case COL_DAFID: {
                    result = rs.getLong("v.DafID");
                    break;
                }
                case COL_NEXTBEST: {
                    result = rs.getLong("nextbest");
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
