/*
 * OffenePflege
 * Copyright (C) 2008 Torsten L?hr
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
 * Auf deutsch (freie ?bersetzung. Rechtlich gilt die englische Version)
 * Dieses Programm ist freie Software. Sie k?nnen es unter den Bedingungen der GNU General Public License,
 * wie von der Free Software Foundation ver?ffentlicht, weitergeben und/oder modifizieren, gem?? Version 2 der Lizenz.
 *
 * Die Ver?ffentlichung dieses Programms erfolgt in der Hoffnung, da? es Ihnen von Nutzen sein wird, aber
 * OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT F?R EINEN
 * BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht,
 * schreiben Sie an die Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA.
 *
 */
package op.care.med;

import op.OPDE;
import op.tools.*;

import javax.swing.*;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author tloehr
 */
public class DBHandling {
    // Stati für MPBuchung

//    public static final int STATUS_AUSBUCHEN_NORMAL = 0;
//    public static final int STATUS_EINBUCHEN_ANFANGSBESTAND = 1;
//    public static final int STATUS_KORREKTUR_MANUELL = 2;
//    public static final int STATUS_KORREKTUR_AUTO_LEER = 3;
//    public static final int STATUS_KORREKTUR_AUTO_VORAB = 4;
//    public static final int STATUS_KORREKTUR_AUTO_ABGELAUFEN = 5;
//    public static final int STATUS_KORREKTUR_AUTO_RUNTERGEFALLEN = 6;
//    public static final int STATUS_KORREKTUR_AUTO_ABSCHLUSS_BEI_PACKUNGSENDE = 7;
//    public static final int STATUS_KORREKTUR_AUTO_ABSCHLUSS_BEI_VORRATSABSCHLUSS = 8;
    public static final int EINHEIT_STUECK = 1;
    public static final int EINHEIT_ML = 2;
    public static final int EINHEIT_LITER = 3;
    public static final int EINHEIT_MG = 4;
    public static final int EINHEIT_GRAMM = 5;
    public static final int EINHEIT_CM = 6;
    public static final int EINHEIT_METER = 7;    // STATI für MPFormen
//    public static final int FORMSTATUS_APV1 = 0;
//    public static final int FORMSTATUS_APV_PER_DAF = 1;
//    public static final int FORMSTATUS_APV_PER_BW = 2;



    /**
     * Setzt für einen Bestand <b>alle</b> Buchungen zur¸ck, bis auf die Anfangsbuchung.
     *
     * @param bestid
     */
    public static void resetBestand(long bestid) {
        String sql = "DELETE FROM MPBuchung WHERE BestID = ? AND Status <> 1";
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setLong(1, bestid);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            new DlgException(ex);
        }
    }


//    public static int getFormStatus(long bestid) {
//        long dafid = ((BigInteger) op.tools.DBHandling.getSingleValue(" MPBestand", "DafID", "BestID", bestid)).longValue();
//        return getFormStatusDafID(dafid);
//    }
//
//    public static int getFormStatusDafID(long dafid) {
//        long formid = ((BigInteger) op.tools.DBHandling.getSingleValue("MPDarreichung", "FormID", "DafID", dafid)).longValue();
//        int formstatus = ((Integer) op.tools.DBHandling.getSingleValue("MPFormen", "Status", "FormID", formid)).intValue();
//        return formstatus;
//    }
//
//    public static int getFormStatusFormID(long formid) {
//        int formstatus = ((Integer) op.tools.DBHandling.getSingleValue("MPFormen", "Status", "FormID", formid)).intValue();
//        return formstatus;
//    }

//    public static DefaultComboBoxModel getPackungen(long dafid, boolean withEmptyItem) {
//        DefaultComboBoxModel result = null;
//        String sql = "SELECT p.MPID, CONCAT(Inhalt, ' '," +
//                " CASE f.PackEinheit WHEN 1 THEN 'Stueck' WHEN 2 THEN 'ml' WHEN 3 THEN 'l' WHEN 4 THEN 'mg' " +
//                " WHEN 5 THEN 'g' WHEN 6 THEN 'cm' WHEN 7 THEN 'm' ELSE '!FEHLER!' END) Inhalt, " +
//                " CASE p.Groesse WHEN 0 THEN 'N1' WHEN 1 THEN 'N2' WHEN 2 THEN 'N3' WHEN 3 THEN 'AP' " +
//                " WHEN 4 THEN 'OP' ELSE '!FEHLER!' END Groesse," +
//                " CONCAT('PZN: ', p.PZN) PZN" +
//                " FROM MPackung p " +
//                " INNER JOIN MPDarreichung daf ON p.DafID = daf.DafID" +
//                " INNER JOIN MPFormen f ON daf.FormID = f.FormID" +
//                " WHERE p.DafID = ?" +
//                " ORDER BY Groesse";
//        try {
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            stmt.setLong(1, dafid);
//            ResultSet rs = stmt.executeQuery();
//            result = SYSTools.rs2cmb(rs);
//            if (withEmptyItem) {
//                result.insertElementAt(new ListElement("<Sonderpackung>", -1l), 0);
//            }
//        } catch (SQLException ex) {
//            new DlgException(ex);
//        }
//
//        return result;
//    }

    public static long getMassID(long dafid) {
        long result = -1;
        String sql = " SELECT F.MassID FROM MProdukte M " +
                " INNER JOIN MPDarreichung D ON M.MedPID = D.MedPID " +
                " INNER JOIN MPFormen F ON D.FormID = F.FormID " +
                " WHERE D.DafID = ? ";
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setLong(1, dafid);
            ResultSet rs = stmt.executeQuery();
            if (rs.first()) {
                result = rs.getLong("MassID");
            }
//            System.out.println("getMassID.result: "+result);
//            System.out.println("getMassID.dafid: "+dafid);
        } catch (SQLException ex) {
            new DlgException(ex);
        }
        return result;
    }

    public static DefaultComboBoxModel getSit(String suchmuster) {
        DefaultComboBoxModel dlm = null;
        suchmuster = "%" + suchmuster + "%";
        String sql = " SELECT SitID, Text FROM Situationen" +
                " WHERE Text like ? " +
                " ORDER BY Text ";
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setString(1, suchmuster);
            ResultSet rs = stmt.executeQuery();
            dlm = SYSTools.rs2cmb(rs);
        } catch (SQLException ex) {
            new DlgException(ex);
        }
        return dlm;
    }

    public static DefaultComboBoxModel getSit(long sitid) {
        DefaultComboBoxModel dlm = null;
        String sql = " SELECT SitID, Text FROM Situationen" +
                " WHERE SitID = ?";
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setLong(1, sitid);
            ResultSet rs = stmt.executeQuery();
            dlm = SYSTools.rs2cmb(rs);
        } catch (SQLException ex) {
            new DlgException(ex);
        }
        return dlm;
    }

    public static ResultSet getMedikamente(String suche) {
        ResultSet rs = null;
        String sql = "SELECT M.Bezeichnung, F.Zubereitung " +
                "FROM MProdukte M 	INNER JOIN MPDarreichung D ON M.MedPID = D.MedPID " +
                "			INNER JOIN MPFormen F ON D.FormID = F.FormID " +
                "WHERE M.Bezeichnung LIKE ?";
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setString(1, "%" + suche + "%");
            rs = stmt.executeQuery();
        } catch (SQLException ex) {
            new DlgException(ex);
        }
        return rs;
    }

    public static String getAnwEinheit(long dafid) {
        String result = "";
        String sql = "" +
                " SELECT form.AnwText, form.AnwEinheit" +
                " FROM MPDarreichung daf " +
                " INNER JOIN MPFormen form ON daf.FormID = form.FormID" +
                " WHERE daf.DafID=?";

        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setLong(1, dafid);
            ResultSet rs = stmt.executeQuery();
            rs.first();
            String zubereitung = SYSTools.catchNull(rs.getString("AnwText"));
            String einheit = SYSConst.EINHEIT[rs.getInt("AnwEinheit")];
            result = zubereitung.equals("") ? einheit : zubereitung + ", " + einheit;
        } catch (SQLException ex) {
            new DlgException(ex);
        }
        return result;
    }

    public static String getPackEinheit(long dafid) {
        String result = "";
        String sql = "" +
                " SELECT form.Zubereitung, form.PackEinheit " +
                " FROM MPDarreichung daf " +
                " INNER JOIN MPFormen form ON daf.FormID = form.FormID" +
                " WHERE daf.DafID=?";

//                " CASE form.PackEinheit WHEN 1 THEN 'Stueck' WHEN 2 THEN 'ml' WHEN 3 THEN 'l' WHEN 4 THEN 'mg' " +
//                " WHEN 5 THEN 'g' WHEN 6 THEN 'cm' WHEN 7 THEN 'm' ELSE '!FEHLER!' END) Einheit " +
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setLong(1, dafid);
            ResultSet rs = stmt.executeQuery();
            rs.first();
            String zubereitung = SYSTools.catchNull(rs.getString("Zubereitung"));
            String einheit = SYSConst.EINHEIT[rs.getInt("PackEinheit")];
            result = zubereitung.equals("") ? einheit : zubereitung + ", " + einheit;
        } catch (SQLException ex) {
            new DlgException(ex);
        }
        return result;
    }

    /**
     * Diese Methode liefert ein ResultSet. Dabei ist der Gedanke wie folgt. Wenn einem Vorrat einmal
     * ein Medizinprodukt zugeordnet wurde. Dann kann man ab diesem Moment nur noch Produkte zuordnen,
     * welche dieselbe Form haben (Einmal Tablette, immer Tablette).
     * Wirkstoffe werden nicht gepr¸ft, da uns dazu keine verl?sslichen Tabellen vorliegen. Ist auch
     * die Verantwortung des Arztes, der Apotheke und des Examens.
     * <p/>
     * Gibt es noch keine Zuordnung, dann kann man diesem Vorrat alles zuordnen.
     *
     * @param vorid PK des Vorrats
     * @return ResultSet mit dem Ergebnis. NULL bei exception.
     */
    public static ResultSet getDAF2Vorrat(long vorid) {
        ResultSet result = null;
        String sql = "SELECT D.FormID FROM MPBestand B " +
                " INNER JOIN MPDarreichung D ON B.DafID = D.DafID" +
                " WHERE B.VorID=? " +
                " LIMIT 0,1";
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setLong(1, vorid);
            ResultSet rs = stmt.executeQuery();
            if (rs.first()) {
                long formid = rs.getLong("FormID"); // Welche FormID wurde diesem Vorrat bereits zugeordnet.
                // Es reicht sich immer den ersten anzusehen, sind eh alle gleich.
                String sql1 = "SELECT D.DafID, M.Bezeichnung, D.Zusatz, F.Zubereitung FROM MPDarreichung D " +
                        " INNER JOIN MPFormen F ON D.FormID = F.FormID" +
                        " INNER JOIN MProdukte M ON M.MedPID = D.MedPID" +
                        " WHERE D.FormID = ?" +
                        " ORDER BY M.Bezeichnung, D.Zusatz, F.Zubereitung";
                PreparedStatement stmt1 = OPDE.getDb().db.prepareStatement(sql1);
                stmt1.setLong(1, formid);
                result = stmt1.executeQuery();
            } else {
                String sql1 = "SELECT D.DafID, M.Bezeichnung, D.Zusatz, F.Zubereitung FROM MPDarreichung D " +
                        " INNER JOIN MPFormen F ON D.FormID = F.FormID" +
                        " INNER JOIN MProdukte M ON M.MedPID = D.MedPID" +
                        " ORDER BY M.Bezeichnung, D.Zusatz, F.Zubereitung";
                Statement stmt1 = OPDE.getDb().db.createStatement();
                result = stmt1.executeQuery(sql1);
            }
        } catch (SQLException ex) {
            new DlgException(ex);
        }

        return result;
    }

    /**
     * Dieses Methode wird vorwiegend bei den Verordnungen eingesetzt.
     * Der Gedanke ist wie folgt: Eine neue Verordnung eines Medikamentes wird immer
     * einem aktiven Vorrat zugeordnet, wenn es bereits fr¸her mal eine Zuordnung zu einer
     * bestimmten DAF gab.
     * Gibt es keine fr¸here Zuweisung, dann werden nur Vorr?te angezeigt, die zu der FormID der
     * neuen DAF passen. Notfalls muss man einen Vorrat anlegen.
     * ?nderung durch #000028. Es werden Zuordnungen erlaubt, die aufgrund der ?quivalenzen zwischen
     * Formen bestehen. z.B. Tabletten zu Dragees zu Filmtabletten etc.
     *
     * @param dafid PK der Darreichung
     * @return ResultSet mit der gew¸nschten Liste.
     */
//    public static ResultSet getVorrat2DAF(String bwkennung, long dafid, Bool foundMatch) {
//        ResultSet result = null;
//        foundMatch.setBool(true);
//        String sql = " SELECT v.VorID, v.Text " +
//                " FROM MPVorrat v" +
//                " INNER JOIN MPBestand b ON v.VorID = b.VorID" +
//                " WHERE v.BWKennung=? AND b.DafID = ?  " +
//                " AND v.Bis = " + SYSConst.MYSQL_DATETIME_BIS_AUF_WEITERES;
//        try {
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            stmt.setString(1, bwkennung);
//            stmt.setLong(2, dafid);
//            result = stmt.executeQuery();
//            if (!result.first()) {
//                // Gibts nicht. Dann zeigen wir alle Vorr?te an, die zumindest dieselbe FormID haben, wie
//                // die gesuchte DAF.
//                foundMatch.setBool(false);
//
//                String sql1 = " SELECT DISTINCT v.VorID, v.Text " +
//                        " FROM MPVorrat v" +
//                        " INNER JOIN MPBestand b ON v.VorID = b.VorID " +
//                        " INNER JOIN MPDarreichung d ON b.DafID = d.DafID" +
//                        " WHERE d.FormID IN (" +
//                        "       SELECT FormID " +
//                        "       FROM MPFormen " +
//                        "       WHERE (Equiv IN ( " + // Alle Formen, die gleichwertig sind
//                        "               SELECT Equiv " +
//                        "               FROM MPDarreichung d " +
//                        "               INNER JOIN MPFormen f ON f.FormID = d.FormID " +
//                        "               WHERE d.DafID = ? " +
//                        "               ) AND Equiv <> 0 " +
//                        "           OR " +
//                        "           ( FormID IN (" + // Falls diese Form keine Gleichwertigen besitzt (Equiv = 0), dann nur die Form selbst.
//                        "               SELECT FormID FROM MPDarreichung WHERE DafID = ? )" +
//                        "           )" +
//                        "       )" +
//                        " ) " +
//                        " AND v.BWKennung=? " +
//                        " AND v.Bis = " + SYSConst.MYSQL_DATETIME_BIS_AUF_WEITERES +
//                        " ORDER BY v.Text ";
//                PreparedStatement stmt1 = OPDE.getDb().db.prepareStatement(sql1);
//                stmt1.setLong(1, dafid);
//                stmt1.setLong(2, dafid);
//                stmt1.setString(3, bwkennung);
//                result = stmt1.executeQuery();
//            }
//        } catch (SQLException ex) {
//            foundMatch.setBool(false);
//            new DlgException(ex);
//        }
//
//        return result;
//    }

    /**
     * Ermittelt eine Liste von Verordnungen, die sich auf einen bestimmten Vorrat beziehen.
     */
    public static ResultSet getVerordnungen2Vorrat(long vorid) {
        ResultSet result = null;
        String sql =
                " SELECT DISTINCT ver.VerID " +
                        " FROM MPVorrat v " +
                        " INNER JOIN MPBestand b ON v.VorID = b.VorID " +
                        " INNER JOIN BHPVerordnung ver ON b.DafID = ver.DafID " +
                        " WHERE v.VorID = ? ";
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setLong(1, vorid);
            result = stmt.executeQuery();
        } catch (SQLException ex) {
            new DlgException(ex);
        }

        return result;
    }

    /**
     * Ermittelt die Menge (in PackEinheit), die in einem Vorrat noch enthalten ist.
     *
     * @param vorid pk des betreffenden Vorrats
     * @return die Summe in der Packungs Einheit.
     */
    public static double getVorratSumme(long vorid) {
        double result = 0d;
        String sql = "SELECT IFNULL(SUM(b.Menge), 0) summe " +
                " FROM MPBestand m " +
                " INNER JOIN MPBuchung b ON m.BestID = b.BestID " +
                " WHERE m.VorID=?";
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setLong(1, vorid);
            ResultSet rs = stmt.executeQuery();
            rs.first();
            result = rs.getDouble("summe");
        } catch (SQLException ex) {
            new DlgException(ex);
        }
        return result;
    }

//    public static String getBestandTextAsHTML(long bestid) {
//        String result = "";
//        String sql = " SELECT vor.BWKennung, best.Ein, best.UKennung, prod.Bezeichnung, daf.Zusatz, pack.PZN, " +
//                " CASE pack.Groesse WHEN 0 THEN 'N1' WHEN 1 THEN 'N2' " +
//                " WHEN 2 THEN 'N3' WHEN 3 THEN 'AP' WHEN 4 THEN 'OP' ELSE '' END Groesse, pack.Inhalt, best.Text," +
//                " f.PackEinheit, f.Zubereitung, f.AnwText " +//, ifnull(b.saldo, 0.00) Bestandsmenge " +
//                " FROM MPBestand best " +
//                " INNER JOIN MPVorrat vor ON vor.VorID = best.VorID " +
//                " INNER JOIN MPDarreichung daf ON daf.DafID = best.DafID " +
//                " INNER JOIN MProdukte prod ON prod.MedPID = daf.MedPID " +
//                " INNER JOIN MPFormen f ON f.FormID = daf.FormID " +
//                " LEFT OUTER JOIN MPackung pack ON best.MPID = pack.MPID " +
//                " WHERE best.BestID = ? ";
//        try {
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            stmt.setLong(1, bestid);
//            ResultSet rs = stmt.executeQuery();
//            if (rs.first()) {
//                result += "<br/><font color=\"blue\"><b>" + rs.getString("prod.Bezeichnung") + " " + rs.getString("daf.Zusatz") + ", ";
//                double inhalt = rs.getDouble("pack.Inhalt");
//                if (!SYSTools.catchNull(rs.getString("PZN")).equals("")) {
//                    result += "PZN: " + rs.getString("PZN") + ", ";
//                    result += rs.getString("Groesse") + ", " + inhalt + " " + SYSConst.EINHEIT[rs.getInt("f.PackEinheit")] + " ";
//                    String zubereitung = SYSTools.catchNull(rs.getString("f.Zubereitung"));
//                    String anwtext = SYSTools.catchNull(rs.getString("f.AnwText"));
//                    result += zubereitung.equals("") ? anwtext : (anwtext.equals("") ? zubereitung : zubereitung + ", " + anwtext);
//                    result += "</b></font><br/>";
//                }
//            }
//            rs.close();
//            stmt.close();
//        } catch (SQLException ex) {
//            new DlgException(ex);
//        }
//        return result;
//    }

//    /**
//     * Ermittelt die Menge, die in einer Packung noch enthalten ist.
//     *
//     * @param bestid pk der betreffenden Packung
//     * @return die Summe in der Packungs Einheit.
//     */
//    public static double getBestandSumme(long bestid) {
//        double result = 0d;
//        String sql = "SELECT IFNULL(SUM(b.Menge), 0) summe " +
//                " FROM MPBestand m " +
//                " INNER JOIN MPBuchung b ON m.BestID = b.BestID " +
//                " WHERE m.BestID=?";
//        try {
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            stmt.setLong(1, bestid);
//            ResultSet rs = stmt.executeQuery();
//            rs.first();
//            result = rs.getDouble("summe");
//        } catch (SQLException ex) {
//            new DlgException(ex);
//        }
//        return result;
//    }

    /**
     * Ermittelt, welches das P?ckchen im Anbruch in einem bestimmten Vorrat ist.
     *
     * @param vorid pk des Vorrats
     * @return bestid des P?ckchens, 0, wenn kein P?ckchen im Anbruch ist.
     */
    public static long getBestandImAnbruch(long vorid) {
        long result = 0;
        String sql = "SELECT BestID FROM MPBestand" +
                " WHERE VorID = ? AND Aus = '9999-12-31 23:59:59' AND Anbruch < now()";
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setLong(1, vorid);
            ResultSet rs = stmt.executeQuery();
            if (rs.first()) {
                result = rs.getLong("BestID");
            }
//            else { // Keiner im Anbruch ? Dann gucken, welcher als n?chstes angebrochen werden muss.
//                String sql1 = " SELECT NextBest FROM MPBestand " +
//                        " WHERE VorID = ? AND NextBest > 0 "; // Das kann immer nur genau einer sein.
//                PreparedStatement stmt1 = OPDE.getDb().db.prepareStatement(sql1);
//                stmt1.setLong(1, vorid);
//                ResultSet rs1 = stmt1.executeQuery();
//                if (rs1.first()) {
//                    result = rs1.getLong("NextBest");
//                }
//            }
        } catch (SQLException ex) {
            new DlgException(ex);
        }
        return result;
    }

//    /**
//     * Ermittelt eines Liste alles Best?nde in einem Vorrat, die noch nicht angebrochen sind.
//     *
//     * @param vorid pk des Vorrats
//     * @return Ein DCBM, was die passenden BestIDs enth?lt. An erster Stelle steht immer das Wort "Keine"
//     */
//    public static DefaultComboBoxModel getBestandGeschlossen(long vorid) {
//        DefaultComboBoxModel result = new DefaultComboBoxModel();
//        result.addElement("Keine");
//        String sql = "SELECT BestID FROM MPBestand " +
//                " WHERE VorID = ? AND Aus = '9999-12-31 23:59:59' AND Anbruch = '9999-12-31 23:59:59' " +
//                " ORDER BY Ein ";
//        try {
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            stmt.setLong(1, vorid);
//            ResultSet rs = stmt.executeQuery();
//            rs.beforeFirst();
//            while (rs.next()) {
//                result.addElement(new Long(rs.getLong("BestID")));
//            }
//
//        } catch (SQLException ex) {
//            new DlgException(ex);
//        }
//        return result;
//    }

//    /**
//     * ermittelt ob ein bestimmter Vorrat einen Bestand im Anbruch hat oder nicht.
//     *
//     * @param vorid
//     * @return true oder false
//     */
//    public static boolean hasAnbruch(long vorid) {
//        boolean result = false;
//        String sql = "SELECT BestID FROM MPBestand" +
//                " WHERE VorID = ? AND Aus = '9999-12-31 23:59:59' AND Anbruch < now()";
//        try {
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            stmt.setLong(1, vorid);
//            ResultSet rs = stmt.executeQuery();
//            result = rs.first();
//        } catch (SQLException ex) {
//            new DlgException(ex);
//        }
//        return result;
//    }

    /**
     * ermittelt ob ein Bestand im Anbruch ist oder nicht
     *
     * @param bestid
     * @return true oder false
     */
    public static boolean isAnbruch(long bestid) {
        boolean result = false;
        String sql = "SELECT BestID FROM MPBestand" +
                " WHERE BestID = ? AND Aus = '9999-12-31 23:59:59' AND Anbruch < now()";
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setLong(1, bestid);
            ResultSet rs = stmt.executeQuery();
            result = rs.first();
        } catch (SQLException ex) {
            new DlgException(ex);
        }
        return result;
    }

    /**
     * ermittelt ob ein Bestand bereits ausgebucht ist oder nicht.
     *
     * @param bestid
     * @return true oder false
     */
    public static boolean isAusgebucht(long bestid) {
        boolean result = false;
        String sql = "SELECT BestID FROM MPBestand" +
                " WHERE BestID = ? AND Aus < '9999-12-31 23:59:59'";
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setLong(1, bestid);
            ResultSet rs = stmt.executeQuery();
            result = rs.first();
        } catch (SQLException ex) {
            new DlgException(ex);
        }
        return result;
    }

//    public static void closeBestand(long bestid, String text, boolean mitNeuberechnung, int status) {
//        Connection db = OPDE.getDb().db;
//        //boolean result = false;
//        boolean doCommit = false;
//        try {
//            // Hier beginnt eine Transaktion, wenn es nicht schon eine gibt.
//            if (db.getAutoCommit()) {
//                db.setAutoCommit(false);
//                db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
//                db.commit();
//                doCommit = true;
//            }
//
//            double bestand = getBestandSumme(bestid);
//            HashMap hm = new HashMap();
//            hm.put("BestID", bestid);
//            hm.put("BHPID", 0);
//            hm.put("Menge", bestand * -1);
//            hm.put("UKennung", OPDE.getLogin().getUser().getUKennung());
//            hm.put("Text", text);
//            hm.put("Status", status);
//            hm.put("PIT", "!NOW!");
//            if (op.tools.DBHandling.insertRecord("MPBuchung", hm) < 0) {
//                throw new SQLException("Fehler beim Einf¸gen der Buchung");
//            }
//
//            hm.clear();
//            hm.put("Aus", "!NOW!");
//            hm.put("NextBest", 0);
//            if (!op.tools.DBHandling.updateRecord("MPBestand", hm, "BestID", bestid)) {
//                throw new SQLException("Fehler bei Update des Bestandes");
//            }
//            hm.clear();
//
//            if (mitNeuberechnung) { // Wenn gew¸nscht wird bei Abschluss der Packung der APV neu berechnet.
//                long vorid = ((BigInteger) op.tools.DBHandling.getSingleValue("MPBestand", "VorID", "BestID", bestid)).longValue();
//                long dafid = ((BigInteger) op.tools.DBHandling.getSingleValue("MPBestand", "DafID", "BestID", bestid)).longValue();
//                OPDE.info("Neuberechnung von DafID:" + dafid);
//                String bwkennung = op.tools.DBHandling.getSingleValue("MPVorrat", "BWKennung", "VorID", vorid).toString();
//                int formstatus = getFormStatus(bestid);
//                if (formstatus != FORMSTATUS_APV1) {
//                    double apvNeu = berechneBuchungsWert(bestid);
//                    if (formstatus == FORMSTATUS_APV_PER_BW) {
//                        addAPV(dafid, bwkennung, apvNeu);
//                        OPDE.info("FormStatus APV_PER_BW. APVneu: " + apvNeu);
//                    } else {
//                        // Das ist der APV aus MPBestand.
//                        double apvAlt = ((BigDecimal) op.tools.DBHandling.getSingleValue("MPBestand", "APV", "BestID", bestid)).doubleValue();
//                        HashMap hm2 = new HashMap();
//                        hm2.put("BWKennung", new Object[]{"", "="});
//                        hm2.put("DafID", new Object[]{dafid, "="});
//                        boolean tauschen = (Boolean) DBRetrieve.getSingleValue("MPAPV", "Tauschen", hm2);
//                        hm2.clear();
//                        if (tauschen) {
//                            setAPV(dafid, apvNeu, false); // der alte APV wird durch den neuen APV ersetzt.
//                            op.tools.DBHandling.updateRecord("MPAPV", hm2, text, hm);
//                            OPDE.info("FormStatus APV_PER_DAF. APValt: " + apvAlt + "  APVneu: " + apvNeu + "  !Wert wurde ausgetauscht!");
//                        } else {
//                            setAPV(dafid, (apvAlt + apvNeu) / 2, false); // der DafID APV wird durch den Mittelwert aus altem und neuem APV ersetzt.
//                            OPDE.info("FormStatus APV_PER_DAF. APValt: " + apvAlt + "  APVneu: " + (apvAlt + apvNeu) / 2);
//                        }
//
//                    }
//                } else {
//                    OPDE.info("FormStatus APV1. Keine Berechnung n?tig.");
//                }
//            }
//            if (doCommit) {
//                db.commit();
//                db.setAutoCommit(true);
//            }
//        } catch (SQLException ex) {
//            try {
//                if (doCommit) {
//                    db.rollback();
//                }
//                new DlgException(ex);
//            } catch (SQLException ex1) {
//                new DlgException(ex1);
//                ex1.printStackTrace();
//                System.exit(1);
//            }
//        }
//    }

//    public static boolean anbrechen(long bestid) {
//        boolean result = false;
//        long vorid = ((BigInteger) op.tools.DBHandling.getSingleValue("MPBestand", "VorID", "BestID", bestid)).longValue();
//        long dafid = ((BigInteger) op.tools.DBHandling.getSingleValue("MPBestand", "DafID", "BestID", bestid)).longValue();
//        // welchen APV m¸ssen nehmen (h?ngt vom Formstatus ab)
//        int formstatus = getFormStatus(bestid);
//        double apv;
//        if (formstatus == FORMSTATUS_APV_PER_BW) {
//            String bwkennung = DBRetrieve.getSingleValue("MPVorrat", "BWKennung", "VorID", vorid).toString();
//            apv = getAPV(dafid, bwkennung);
//        } else if (formstatus == FORMSTATUS_APV_PER_DAF) {
//            apv = getAPV(dafid, "");
//        } else { //APV1
//            apv = 1d;
//        }
//
//        if (!hasAnbruch(vorid)) {
//            HashMap hm = new HashMap();
//            hm.put("Anbruch", "!NOW!");
//            hm.put("APV", apv);
//            result = op.tools.DBHandling.updateRecord("MPBestand", hm, "BestID", bestid);
//            hm.clear();
//
//        } else {
//            result = false;
//        }
//        return result;
//    }
//
//    public static boolean anbrechenNaechste(long vorid) {
//        boolean result = false;
//        if (!hasAnbruch(vorid)) {
//            try {
//                String sql1 = " SELECT BestID FROM MPBestand " +
//                        " WHERE VorID = ? AND Aus = '9999-12-31 23:59:59' AND Anbruch = '9999-12-31 23:59:59' " +
//                        " ORDER BY Ein, BestID " +
//                        " LIMIT 0,1";
//                PreparedStatement stmt1 = OPDE.getDb().db.prepareStatement(sql1);
//                stmt1.setLong(1, vorid);
//                ResultSet rs1 = stmt1.executeQuery();
//                if (rs1.first()) {
//                    long bestid = rs1.getLong("BestID");
//                    result = anbrechen(bestid);
//                }
//                rs1.close();
//                stmt1.close();
//            } catch (SQLException ex) {
//                new DlgException(ex);
//                result = false;
//            }
//        } else {
//            result = false;
//        }
//        return result;
//    }

//    public static long getVorrat2DAF(String bwkennung, long dafid) {
//        long vorid = 0;
//        // Welche VorID ist zutreffend.
//        Bool found = new Bool(false);
//        ResultSet rs = getVorrat2DAF(bwkennung, dafid, found);
//        if (found.isTrue()) {
//            try {
//                rs.first();
//                vorid = rs.getLong("VorID");
//            } catch (SQLException ex) {
//                new DlgException(ex);
//            }
//        }
//        return vorid;
//    }

//    /**
//     * Bucht eine Menge aus einem Vorrat aus, ggf. zugeh?rig zu einer BHP. ?bersteigt die Entnahme Menge den
//     * Restbestband, dann wird entweder
//     * <ul>
//     * <li>wenn NextBest==0 &rarr; der Bestand trotzdem weiter gebucht. Bis ins Negative.</li>
//     * <li>wenn NextBest > 0 &rarr; ein neuer Bestand wird angebrochen.</li>
//     * </ul>
//     * Ist keine Packung im Anbruch, dann passiert gar nichts. Der R¸ckgabewert ist dann false.
//     *
//     * @param dafid      pk der Darreichungsform
//     * @param menge      gew¸nschte Entnahmemenge
//     * @param bhpid      pk der BHP aufgrund dere dieser Buchungsvorgang erfolgt.
//     * @param anweinheit true, dann wird in der anweinheit ausgebucht. false, in der packeinheit.
//     * @return true, bei Erfolg; false, sonst
//     */
//    public static boolean entnahmeVorrat(long dafid, String bwkennung, double menge, boolean anweinheit, long bhpid) {
//        if (dafid <= 0) {
//            return true;
//        }
//        boolean result = true;
//        long vorid = getVorrat2DAF(bwkennung, dafid);
//
//        if (vorid > 0) {
//            if (anweinheit) { // Umrechnung der Anwendungs Menge in die PackMenge.
//                long bestid = getBestandImAnbruch(vorid);
//                // Das ist der APV aus MPBestand.
//                double apv = ((BigDecimal) op.tools.DBHandling.getSingleValue("MPBestand", "APV", "BestID", bestid)).doubleValue();
//                menge = menge / apv;
//            }
//            result &= menge > 0 && entnahmeVorrat(vorid, menge, bhpid);
//        }
//        OPDE.debug(result ? "" : "entnahmeVorrat/5 fehlgeschlagen");
//
//        return result;
//    }
//
//    private static boolean entnahmeVorrat(long vorid, double wunschmenge, long bhpid) {
//        boolean result = true;
//        long bestid = getBestandImAnbruch(vorid);
//
//        if (bestid > 0 && wunschmenge > 0) {
//            // ist schon eine Packung festgelegt, die angebrochen werden soll, sobald diese leer ist ?
//            long nextBest = ((Long) DBRetrieve.getSingleValue("MPBestand", "NextBest", "BestID", bestid)).longValue();
//            double entnahme; // wieviel in diesem Durchgang tats?chlich entnommen wird.
//            double restsumme = getBestandSumme(bestid); // wieviel der angebrochene Bestand noch hergibt.
//            entnahme = wunschmenge; // normalerweise wird immer das hergegeben, was auch gew¸nscht ist. Notfalls bis ins minus.
//            if (nextBest > 0 && restsumme <= wunschmenge) { // sollte eine Packung aber schon als nachfolger bestimmt sein,
//                entnahme = restsumme; // dann wird erst diese hier leergebraucht
//            } // und dann der Rest aus der n?chsten Packung genommen.
//
//            // Erstmal die Buchung f¸r diesen Durchgang
//            HashMap hm = new HashMap();
//            hm.put("BestID", bestid);
//            hm.put("BHPID", bhpid);
//            hm.put("Menge", entnahme * -1);
//            hm.put("UKennung", OPDE.getLogin().getUser().getUKennung());
//            hm.put("PIT", "!NOW!");
//            hm.put("Status", STATUS_AUSBUCHEN_NORMAL);
//            result &= op.tools.DBHandling.insertRecord("MPBuchung", hm) > 0;
//
//            if (nextBest > 0) { // Jetzt gibt es direkt noch den Wunsch das n?chste P?ckchen anzubrechen.
//
//                if (restsumme <= wunschmenge) { // Es war mehr gew¸nscht, als die angebrochene Packung hergegeben hat.
//                    // Dann m¸ssen wird erstmal den alten Bestand abschlie?en.
//                    try {
//                        // Es war mehr gew¸nscht, als die angebrochene Packung hergegeben hat.
//                        // Dann m¸ssen wird erstmal den alten Bestand abschlie?en.
//                        closeBestand(bestid, "Automatischer Abschluss bei leerer Packung", true, STATUS_KORREKTUR_AUTO_VORAB);
//
//                        // dann den neuen Bestand anbrechen.
//                        result &= anbrechen(nextBest);
//                        Thread.sleep(1000); // Sonst ist die Anzeige im PnlBHP falsch.
//                    } catch (InterruptedException ex) {
//                        Logger.getLogger(DBHandling.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
//
//                if (wunschmenge > entnahme) { // Sind wir hier fertig, oder m¸ssen wir noch mehr ausbuchen.
////                    try {
//                    // Sind wir hier fertig, oder m¸ssen wir noch mehr ausbuchen.
//                    //Thread.sleep(1000); // Ohne diese Warteschleife verschlucken sich die Datenbankanfragen.
//                    // und es kommt zur Exception. :-( Schrecklich !
//                    result &= entnahmeVorrat(vorid, wunschmenge - entnahme, bhpid);
////                    } catch (InterruptedException ex) {
////                        Logger.getLogger(DBHandling.class.getName()).log(Level.SEVERE, null, ex);
////                    }
//                }
//            }
//            hm.clear();
//        } else {
//            result = false;
//        }
//        OPDE.debug(result ? "" : "entnahmeVorrat/3 fehlgeschlagen");
//        return result;
//    }
//
//    public static boolean entnahmeVorrat(long vorid, double wunschmenge) {
//        return entnahmeVorrat(vorid, wunschmenge, 0);
//    }

//    /**
//     * Diese Methode bucht auf einen Bestand immer genau soviel drauf oder runter, damit er auf
//     * dem gew¸nschten soll landet.
//     *
//     * @param bestid um die es geht.
//     * @param soll.  gew¸nschter Endbestand. Muss >= 0 sein.
//     * @return PK der neuen Buchung.
//     * @throws java.sql.SQLException
//     */
//    public static long setzeBestand(long bestid, double soll, String text, int status) throws SQLException {
//        long buchid = -1;
//        if (soll >= 0) {
//            double bestand = getBestandSumme(bestid);
//            if (bestand != soll) {
//                double result = 0d;
//                if (bestand <= 0) {
//                    result = Math.abs(bestand) + soll;
//                } else {
//                    result = bestand * -1 + soll;
//                }
//                // passende Buchung anlegen.
//                HashMap hm2 = new HashMap();
//                hm2.put("BHPID", 0l);
//                hm2.put("Menge", result);
//                hm2.put("BestID", bestid);
//                hm2.put("UKennung", OPDE.getLogin().getUser().getUKennung());
//                hm2.put("PIT", "!NOW!");
//                hm2.put("Text", text);
//                hm2.put("Status", status);
//
//                buchid = op.tools.DBHandling.insertRecord("MPBuchung", hm2);
//                if (buchid < 0) {
//                    throw new SQLException();
//                }
//            } else {
//                buchid = 0;
//            }
//        }
//        return buchid;
//    }

//    /**
//     * Die Berechnungsmethode unterscheidet verschiedene F?lle:
//     * <p/>
//     * <ul>
//     * <li>Die betroffene Packung hat die FormStatus = APV1. Das sind z.B. alle Tabletten oder Kapseln. Hier macht es
//     * keinen Sinn, irgendwelche Verh?ltnisma?e neu zu rechnen. Wenn es eine Diskrepanz zwischen gerechnetem und realem Bestandswert
//     * gibt, dann ist irgendeine Tablette runtergefallen und nicht ausgebucht worden. Deshalb wird der Bestand einfach korrgiert und dann
//     * ist das eben so.
//     * <ul><li>Die Packung ist <b>jetzt</b> leer. Egal, wie der Buchungsbestand war. Er wird jetzt <i>gewaltsam</i> auf 0 gebracht.
//     * Der Bestand abgeschlossen und (wenn gew¸nscht) der neue angebrochen.</li>
//     * </ul></li>
//     * <p/>
//     * <li>Die Packung hat die Form Salben, Cremes. FORMSTATUS = APV_PER_BW. Hierbei passiert es h?ufig, dass die Menge, die bei einer Anwendung
//     * verbraucht wird individuell vom Bewohner abh?ngt. Der eine braucht viel Salbe, der andere wenig. H?ngt auch vom Krankheitsverlauf ab.
//     * Sagen wir mal eine Salbe gegen Schuppenflechte. Das kann an einem Tag wenig und an einem anderen Tag viel sein. Somit macht es keinen Sinn,
//     * hier einen Wert f¸r die Salbe zu speichern. H?chstens einen Mittelwert aus allen Bewohner APVs.
//     * <ul>
//     * <li>Der Bestand wird abgeschlossen. Es wird ein neuer APV gerechnet. Dieser wird in der Tabelle MPAPV eingetragen und zwar mit
//     * mit der entsprechenden BWKennung.</li>
//     * </ul></li>
//     * <p/>
//     * <li>Die Packung hat die Form Tropfen, Sirup. FORMSTATUS = APV_PER_DAF. Hier macht es wiederum keinen Sinn einen APV pro BW zu speichern.
//     * 5 Tropfen sind eben bei allen Bewohnern 5 Tropfen.
//     * <ul>
//     * <li>Der Bestand wird abgeschlossen. Es wird ein neuer APV gerechnet. Dieser wird in der Tabelle MPAPV eingetragen, hier allerdings mit
//     * BWKennung = "".</li>
//     * </ul></li>
//     * </ul>
//     * <p/>
//     * Nach den Berechnungen wird immer (wenn gew¸nscht) ein neuer Bestand angebrochen. Falls ein APV berechnet wurde, wird jeweils der neue Mittelwert
//     * ¸ber alle APVs einer Darreichung als neuer Anfangs APV dieser Darreichung hinterlegt. Also MPDarreicung.APV = AVG(MPAPV.APV).
//     *
//     * @param bestid des Bestandes, f¸r den das Verh?ltnis neu berechnet werden soll.
//     */
//    public static double berechneBuchungsWert(long bestid) {
//
//        long dafid = ((BigInteger) op.tools.DBHandling.getSingleValue("MPBestand", "DafID", "BestID", bestid)).longValue();
//        long formid = ((BigInteger) op.tools.DBHandling.getSingleValue("MPDarreichung", "FormID", "DafID", dafid)).longValue();
//        long formstatus = ((Integer) op.tools.DBHandling.getSingleValue("MPFormen", "Status", "FormID", formid)).intValue();
//
//        double apvNeu = 1d;
//
//        if (formstatus != FORMSTATUS_APV1) {
//            HashMap filter = new HashMap();
//            filter.put("BestID", new Object[]{bestid, "="});
//            filter.put("Status", new Object[]{STATUS_EINBUCHEN_ANFANGSBESTAND, "="});
//            double inhaltReal = ((BigDecimal) op.tools.DBHandling.getSingleValue("MPBuchung", "Menge", filter)).doubleValue();
//            filter.clear();
//            double inhaltRechnerisch = 0d;
//            double apvAlt = ((BigDecimal) op.tools.DBHandling.getSingleValue("MPBestand", "APV", "BestID", bestid)).doubleValue();
//
//            String sqlGetSumme = "" +
//                    " SELECT SUM(Dosis) FROM BHP bhp " +
//                    " INNER JOIN MPBuchung buch ON bhp.BHPID = buch.BHPID " +
//                    " WHERE buch.BestID = ? ";
//            try {
//                PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sqlGetSumme);
//                stmt.setLong(1, bestid);
//                ResultSet rs = stmt.executeQuery();
//                rs.first();
//                double bhpsumme = rs.getDouble(1);
//                inhaltRechnerisch = bhpsumme / apvAlt;
//            } catch (SQLException ex) {
//                new DlgException(ex);
//            }
//
//            apvNeu = inhaltReal / inhaltRechnerisch * apvAlt;
//
//            // Zu gro?e APV Abweichungen verhindern. Siehe Problem 14 in bugs.offene-pflege.de
//            double apvkorridor = Double.parseDouble(OPDE.getProps().getProperty("apv_korridor")) / 100;
//            double halbeBreite = apvAlt * apvkorridor;
//            double korridorUnten = apvAlt - halbeBreite;
//            double korridorOben = apvAlt + halbeBreite;
//            // Liegt der neue apv AUSSERHALB des maximalen Korridors, so wird er verworfen
//            if (apvNeu < korridorUnten || korridorOben < apvNeu) {
//                apvNeu = apvAlt;
//            }
//        }
//        return apvNeu;
//    }

    public static double getAPV(long dafid, String bwkennung) {
        double apv = 1d;
        String sqlAverage = "" +
                " SELECT IFNULL(AVG(APV),0) FROM MPAPV WHERE DafID = ? AND BWKennung = ?";
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sqlAverage);
            stmt.setLong(1, dafid);
            stmt.setString(2, bwkennung);
            ResultSet rs = stmt.executeQuery();
            rs.first();
            apv = rs.getBigDecimal(1).doubleValue();
            if (!bwkennung.equals("") && apv == 0d) {
                // Es war ein bewohnerspezifischer APV gew¸nscht und den gab es nicht.
                // Dann suchen wir den DAF spezifischen und geben den zur¸ck.
                rs.close();
                stmt.setString(2, "");
                rs = stmt.executeQuery();
                rs.first();
                apv = rs.getBigDecimal(1).doubleValue();
                if (apv == 0d) { // Immer noch nicht. Dann ist er jetzt eben 1.
                    apv = 1d;
                }
                rs.close();
                stmt.close();
            }
        } catch (SQLException ex) {
            new DlgException(ex);
        }
        return apv;
    }

//    public static void setAPV(long dafid, double apv, boolean tauschen) {
//        // Gibt es schon einen APV Eintrag ?
//        String sqlAverage = "" +
//                " SELECT APVID FROM MPAPV WHERE DafID = ? AND BWKennung = ''";
//        long apvid = 0;
//        try {
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sqlAverage);
//            stmt.setLong(1, dafid);
//            ResultSet rs = stmt.executeQuery();
//            if (rs.first()) {
//                apvid = rs.getLong(1);
//            }
//        } catch (SQLException ex) {
//            new DlgException(ex);
//        }
//
//        HashMap hm = new HashMap();
//        hm.put("APV", apv);
//        hm.put("Tauschen", tauschen);
//
//        if (apvid == 0) {
//            hm.put("BWKennung", "");
//            hm.put("DafID", dafid);
//            op.tools.DBHandling.insertRecord("MPAPV", hm);
//        } else {
//            op.tools.DBHandling.updateRecord("MPAPV", hm, "APVID", apvid);
//        }
//        hm.clear();
//    }

//    public static void addAPV(long dafid, String bwkennung, double apv) {
//        HashMap hm = new HashMap();
//        hm.put("APV", apv);
//        hm.put("BWKennung", bwkennung);
//        hm.put("DafID", dafid);
//        op.tools.DBHandling.insertRecord("MPAPV", hm);
//        hm.clear();
//    }

//    public static boolean betrifftAbgeschlossenenBestand(long bhpid) {
//        String sql = " SELECT best.Aus " +
//                " FROM BHP bhp " +
//                " INNER JOIN MPBuchung buch ON bhp.BHPID = buch.BHPID " +
//                " INNER JOIN MPBestand best ON best.BestID = buch.BestID " +
//                " WHERE bhp.BHPID = ? AND best.Aus < '9999-12-31 23:59:59'";
//        boolean result = false;
//        try {
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            stmt.setLong(1, bhpid);
//            ResultSet rs = stmt.executeQuery();
//            result = rs.first();
//        } catch (SQLException ex) {
//            OPDE.getLogger().error(ex);
//            ex.printStackTrace();
//        }
//        return result;
//    }

//    /**
//     * Die R¸ckgabe eines Vorrats bezieht sich auf eine BHPID f¸r die die Buchungen zur¸ckgerechnet werden
//     * sollen.
//     * <ol>
//     * <li>Zuerst werden alle Buchungen zu einer BHPID herausgesucht.</li>
//     * <li>Gibt es mehr als eine, dann wurde f¸r die Buchung ein P?ckchen aufgebraucht und ein neues angefangen. In diesem Fall wird die Ausf¸hrung abgelehnt.</li>
//     * <li>Es werden alle zugeh?rigen Buchungen zu dieser BHPID gel?scht.</li>
//     * </ol>
//     *
//     * @result true bei Erfolg, false sonst.
//     */
//    public static boolean rückgabeVorrat(long bhpid) {
//        boolean result = false;
//        String sql = "" +
//                " SELECT count(*) " +
//                " FROM MPBuchung " +
//                " WHERE BHPID = ? ";
//        try {
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            stmt.setLong(1, bhpid);
//            ResultSet rs = stmt.executeQuery();
//            rs.first();
//            if (rs.getLong(1) == 1) { // es gibt genau eine Buchung.
//                // Weg damit
//                result = (op.tools.DBHandling.deleteRecords("MPBuchung", "BHPID", bhpid) >= 0);
//
//            }
//
//            result |= (rs.getLong(1) == 0); // Auf true, wenn anzahl der Buchungen = 0 ist.
//
//            rs.close();
//            stmt.close();
//
//        } catch (SQLException ex) {
//            OPDE.getLogger().error(ex);
//            ex.printStackTrace();
//            result = false;
//        }
//        return result;
//    }

//    /**
//     * Erstellt einen neuen Vorrat f¸r einen Bewohner zu einer bestimmten DafID.
//     *
//     * @return pk des neuen Vorrats.
//     */
//    public static long createVorrat(String text, long dafid, String bwkennung)
//            throws SQLException {
//        // Vorrat
//        HashMap hm2 = new HashMap();
//        hm2.put("Text", text);
//        hm2.put("BWKennung", bwkennung);
//        hm2.put("UKennung", OPDE.getLogin().getUser().getUKennung());
//        hm2.put("Von", "!NOW!");
//        hm2.put("Bis", "!BAW!");
//        long newVorid = op.tools.DBHandling.insertRecord("MPVorrat", hm2);
//        if (newVorid <= 0) {
//            throw new SQLException();
//        }
//
//        hm2.clear();
//        return newVorid;
//    }

//    /**
//     * bucht ein Medikament in einen Vorrat ein. Aber nur dann, wenn es keinen anderen Vorrat gibt,
//     * der mit seiner DafID schon passt.
//     * Falls diese DafID noch den Dummy "Startbestand" hat, wird dieser zuerst gel?scht.
//     *
//     * @return true, bei Erfolg. false, sonst.
//     */
//    public static long einbuchenVorrat(long vorid, long mpid, long dafid, String text, double menge)
//            throws SQLException {
//        long bestid = -1;
//        if (menge > 0) {
//
//            int formstatus = getFormStatusDafID(dafid);
//            double apv;
//            if (formstatus == FORMSTATUS_APV_PER_BW) {
//                String bwkennung = DBRetrieve.getSingleValue("MPVorrat", "BWKennung", "VorID", vorid).toString();
//                apv = getAPV(dafid, bwkennung);
//            } else if (formstatus == FORMSTATUS_APV_PER_DAF) {
//                apv = getAPV(dafid, "");
//            } else { //APV1
//                apv = 1d;
//            }
//
//            // Bestand anlegen
//            HashMap hm = new HashMap();
//            hm.put("VorID", vorid);
//            hm.put("MPID", (mpid == -1 ? null : mpid));
//            hm.put("Text", text);
//            hm.put("Ein", "!NOW!");
//            hm.put("UKennung", OPDE.getLogin().getUser().getUKennung());
//            hm.put("Anbruch", "!BAW!");
//            hm.put("Aus", "!BAW!");
//            hm.put("DafID", dafid);
//            hm.put("APV", apv);
//
//            bestid = op.tools.DBHandling.insertRecord("MPBestand", hm);
//            if (bestid <= 0) {
//                throw new SQLException("MPBestand konnte nicht gebucht werden");
//            }
//
//            // passende Buchung anlegen.
//            HashMap hm2 = new HashMap();
//            hm2.put("BHPID", 0l);
//            hm2.put("Menge", menge);
//            hm2.put("BestID", bestid);
//            hm2.put("UKennung", OPDE.getLogin().getUser().getUKennung());
//            hm2.put("PIT", "!NOW!");
//            hm2.put("Status", STATUS_EINBUCHEN_ANFANGSBESTAND);
//
//            long buchid = op.tools.DBHandling.insertRecord("MPBuchung", hm2);
//            if (buchid < 0) {
//                throw new SQLException();
//            }
//
////            filter.clear();
//            hm.clear();
//            hm2.clear();
//        }
//        return bestid;
//    }

//    /**
//     * ermittelt zu einer BHPID die zugeh?rige Darreichungsform DafID und
//     * ob diese Medikamente gebucht werden oder nicht.
//     * @param bhpid pk der BHP um die es geht.
//     * @return ArrayList mit folgenden Ergebnissen: <CODE>{long DafID, bool buchen}</CODE>
//     */
//    public static ArrayList getBHPInfo(long bhpid) {
//        ArrayList result = new ArrayList();
//        String sql = "SELECT v.DafID, ifnull(daf.Kalkulieren, 0) kalkulieren" +
//                " FROM BHP bhp " +
//                " INNER JOIN BHPPlanung bhpp ON bhp.BHPPID = bhpp.BHPPID " +
//                " INNER JOIN BHPVerordnung v ON bhpp.VerID = v.VerID " +
//                " LEFT OUTER JOIN MPDarreichung daf ON daf.DafID = v.DafID " +
//                " WHERE bhp.BHPID=?";
//        try {
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            stmt.setLong(1, bhpid);
//            ResultSet rs = stmt.executeQuery();
//            if (rs.first()) {
//                result.add(rs.getLong("DafID"));
//                result.add(rs.getBoolean("kalkulieren"));
//            }
//        } catch (SQLException ex) {
//            new DlgException(ex);
//        }
//        return result;
//    }

    /**
     * Rechnet eine bestimmte AnwMenge in die entsprechende wunschmenge eines Vorrats um. Je nach Formstatus
     * wird der individuelle Faktor eines Bewohners ber¸cksichtigt.
     *
     * @param vorid, pk des Vorrats, ¸ber dessen DafID Zuordnung die Menge gerechnet wird.
     * @param menge  (Anwendungs Menge) die umgerechnet werden soll
     * @return Umgerechnete wunschmenge. 0d bei Fehler.
     */
//    public static double getPackMenge(long vorid, double menge) {
//        double result = 0d;
//        long dafid = 0;
//        try {
//            ResultSet rs = getDAF2Vorrat(vorid);
//            rs.first();
//            dafid = rs.getLong(1);
//        } catch (SQLException e) {
//            new DlgException(e);
//        }
//        long formid = ((BigInteger) op.tools.DBHandling.getSingleValue("MPDarreichung", "FormID", "DafID", dafid)).longValue();
//        long formstatus = ((Integer) op.tools.DBHandling.getSingleValue("MPFormen", "Status", "FormID", formid)).intValue();
//
//        if (formstatus != FORMSTATUS_APV1) {
//            String bwkennung = "";
//            if (formstatus == FORMSTATUS_APV_PER_BW) {
//                bwkennung = op.tools.DBHandling.getSingleValue("MPVorrat", "BWKennung", "VorID", vorid).toString();
//            }
//            long bestid = getBestandImAnbruch(vorid);
//            double apv = ((BigDecimal) op.tools.DBHandling.getSingleValue("MPBestand", "APV", "BestID", bestid)).doubleValue();
//            result = menge / apv;
//        } else {
//            result = menge;
//        }
//        return result;
//    }

//    public static String getBestandText4Print(long bestid, boolean kurz) {
//        String result = "";
//        String sql = " SELECT vor.BWKennung, best.Ein, best.UKennung, prod.Bezeichnung, daf.Zusatz, pack.PZN, " +
//                " CASE pack.Groesse WHEN 0 THEN 'N1' WHEN 1 THEN 'N2' " +
//                " WHEN 2 THEN 'N3' WHEN 3 THEN 'AP' WHEN 4 THEN 'OP' ELSE '' END Groesse, pack.Inhalt, best.Text," +
//                " f.PackEinheit, f.Zubereitung, f.AnwText " +//, ifnull(b.saldo, 0.00) Bestandsmenge " +
//                " FROM MPBestand best " +
//                " INNER JOIN MPVorrat vor ON vor.VorID = best.VorID " +
//                " INNER JOIN MPDarreichung daf ON daf.DafID = best.DafID " +
//                " INNER JOIN MProdukte prod ON prod.MedPID = daf.MedPID " +
//                " INNER JOIN MPFormen f ON f.FormID = daf.FormID " +
//                " LEFT OUTER JOIN MPackung pack ON best.MPID = pack.MPID " +
//                //                " LEFT OUTER JOIN (" +
//                //                "           SELECT best.VorID, best.DafID, sum(buch.Menge) saldo FROM MPBestand best " +
//                //                "           INNER JOIN MPBuchung buch ON buch.BestID = best.BestID " +
//                //                "           GROUP BY VorID " +
//                //                "       ) b ON b.VorID = vor.VorID " +
//                " WHERE best.BestID = ? ";
//        try {
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            stmt.setLong(1, bestid);
//            ResultSet rs = stmt.executeQuery();
//            if (rs.first()) {
//                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
//                if (kurz) {
//
//                    result = SYSPrint.EPL2_CLEAR_IMAGE_BUFFER;
//                    result += SYSPrint.EPL2_labelformat(57, 19, 3);
//                    result += SYSPrint.EPL2_print_ascii(5, 5, 0, SYSPrint.EPL2_FONT_7pt, 1, 1, false, rs.getString("prod.Bezeichnung") + " " + rs.getString("daf.Zusatz"));
//                    if (!SYSTools.catchNull(rs.getString("PZN")).equals("")) {
//                        result += SYSPrint.EPL2_print_ascii(5, 30, 0, SYSPrint.EPL2_FONT_6pt, 1, 1, false, "PZN:" + rs.getString("PZN") + "  Datum:" + sdf.format(rs.getDate("best.Ein")) + " (" + rs.getString("best.UKennung") + ")");
//                    }
//
//                    result += SYSPrint.EPL2_print_ascii(5, 55, 0, SYSPrint.EPL2_FONT_12pt, 2, 2, true, Long.toString(bestid));
//                    result += SYSPrint.EPL2_print_ascii(5, 107, 0, SYSPrint.EPL2_FONT_6pt, 1, 1, false, SYSTools.getBWLabel1(rs.getString("vor.BWKennung")));
//                    result += SYSPrint.EPL2_print_ascii(5, 122, 0, SYSPrint.EPL2_FONT_6pt, 1, 1, false, SYSTools.getBWLabel2(rs.getString("vor.BWKennung")));
//                    //result += SYSPrint.EPL2_print_ascii(5, 120, 0, SYSPrint.EPL2_FONT_6pt, 1, 1, false, );
//                    result += SYSPrint.EPL2_PRINT;
//                } else {
//                    result = SYSPrint.reset() + "\n";
//                    result += SYSTools.getWindowTitle("Einbuchungsbeleg") + "\n";
//                    result += "============================\n\n";
//                    //result += OCPrint.doubleHeight(rs.getString("prod.Bezeichnung") + " " + rs.getString("daf.Zusatz")) + "\n";
//                    result += SYSPrint.doubleStrike(SYSPrint.doubleHeight(rs.getString("prod.Bezeichnung") + " " + rs.getString("daf.Zusatz") + "\n"));
//                    double inhalt = rs.getDouble("pack.Inhalt");
//                    if (!SYSTools.catchNull(rs.getString("PZN")).equals("")) {
//                        result += "PZN: " + rs.getString("PZN") + "\n";
//                        result += rs.getString("Groesse") + ", " + inhalt + " " + SYSConst.EINHEIT[rs.getInt("f.PackEinheit")] + " ";
//                        result += SYSTools.catchNull(rs.getString("f.Zubereitung"));
//                        result += "\n\n";
//                    }
//                    if (!SYSTools.catchNull(rs.getString("Text")).equals("")) {
//                        result += "Bemerkung: " + rs.getString("Text") + "\n";
//                    }
//                    // Das hier ist falsch. Man muss die Buchungsmenge der letzten Buchung nehmen.
//                    // Nicht die Bestandsmenge.
//                    double bestandsmenge = getBestandSumme(bestid);
//                    if (bestandsmenge < inhalt) { // War bei Einbuchung nicht voll. Das sollte man erw?hnen.
//                        result += "Inhalt bei Einbuchung: " + bestandsmenge + " " + SYSConst.EINHEIT[rs.getInt("f.PackEinheit")] + "\n";
//                    }
//                    result += SYSPrint.doubleHeight(SYSPrint.doubleStrike(SYSPrint.red("Bestands Nr.: " + bestid + "\n\n")));
//                    result += SYSPrint.doubleStrike("Bewohner: " + SYSTools.getBWLabel(rs.getString("vor.BWKennung")) + "\n");
//                    result += "Datum: " + sdf.format(rs.getDate("best.Ein")) + "\n";
//                    result += "Eingebucht durch: " + rs.getString("best.UKennung") + "\n\n\n";
//                    result += SYSPrint.ESCPOS_PARTIAL_CUT;
//                    result += "\n";
//                }
//            }
//        } catch (SQLException ex) {
//            new DlgException(ex);
//        }
//        // Konvertierung auf PC850 weg. Umlauten.
//        try {
//            byte[] conv = result.getBytes("Cp850");
//            result = new String(conv);
//        } catch (UnsupportedEncodingException ex) {
//            //ex.printStackTrace();
//            new DlgException(ex);
//        }
//        return result;
//    }

    /**
     * schlie?t einen Vorrat ab, in dem alles ausgebucht wird, was da drin war.
     */
//
}
