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

import op.OPDE;
import op.care.DFNImport;
import op.tools.*;

import javax.swing.*;
import java.math.BigInteger;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author root
 */
public class DBHandling {

    //    public static final int ART_PROBLEM = 0;
//    public static final int ART_RESSOURCE = 1;
//    public static final int ART_ZIEL = 2;
//    public static final int ART_INFO = 3;
    public static final int ART_MASSNAHME = 4;
    public static final int ART_KONTROLLEN = 5;
    public static final String[] ARTEN = new String[]{"", "", "", "", "Massnahme", "Kontrolle"}; // die ersten vier leeren stammen noch aus alten Zeiten.
    public static final int MSSN_MODE_ALLE = 0;
    public static final int MSSN_MODE_NUR_BHP = 1;
    public static final int MSSN_MODE_NUR_PFLEGE = 2;

    public static DefaultComboBoxModel getMassnahmen(int mode) {
        return getMassnahmen(mode, "");
    }

    public static DefaultComboBoxModel getMassnahmen(int mode, String suche) {
        DefaultComboBoxModel dcbmMassnahmen = new DefaultComboBoxModel();

        PreparedStatement stmt;
        ResultSet rs;

        // Fehler: nur aktive Massnahmen anzeigen
        try {
            String sql = "";
            if (suche.equals("")) { // alle anzeigen
                sql = "SELECT MassID, Bezeichnung FROM Massnahmen " +
                        " WHERE Aktiv=1 AND " +
                        (mode == MSSN_MODE_NUR_BHP ? " MassArt = 2 " : "") +
                        (mode == MSSN_MODE_NUR_PFLEGE ? " MassArt = 1 " : "") +
                        " ORDER BY Bezeichnung ";
            } else {
                sql = "SELECT MassID, Bezeichnung FROM Massnahmen " +
                        " WHERE Aktiv=1 AND Bezeichnung like ? " +
                        (mode == MSSN_MODE_NUR_BHP ? " AND MassArt = 2 " : "") +
                        (mode == MSSN_MODE_NUR_PFLEGE ? " AND MassArt = 1 " : "") +
                        " ORDER BY Bezeichnung ";
            }
            stmt = OPDE.getDb().db.prepareStatement(sql);
            if (!suche.equals("")) {
                suche = "%" + suche + "%";
                stmt.setString(1, suche);
            }
            rs = stmt.executeQuery();
            dcbmMassnahmen = SYSTools.rs2cmb(rs);
        } catch (SQLException se) {
            new DlgException(se);
        }

        return dcbmMassnahmen;
    }

    /**
     * copy2tmp erstellt eine temporäre Kopie der Pflegeplanung zur Bearbeitung.
     * Die beteiligten Datenbanktabellen haben alle ein "tmp" Feld. Steht das auf 0
     * dann handelt es sich um die "echte" Planung.
     * Die temporären Planungen haben in dieser Spalte die aktuelle LoginID
     * des Anwenders, der die Bearbeitung vornimmt.
     *
     * @param planid, PK des Planes der kopiert werden soll.
     * @throws java.sql.SQLException
     */
    public static void copy2tmp(long planid) {
        Connection db = OPDE.getDb().db;
        try {
            // Hier beginnt eine Transaktion
            db.setAutoCommit(false);
            db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            db.commit();
            // MassTermin --------------------------
            String sql = "" +
                    " INSERT INTO MassTermin (MassID, PlanID, NachtMo, Morgens, Mittags, Nachmittags, Abends, NachtAb, UhrzeitAnzahl," +
                    " Uhrzeit, Taeglich, Woechentlich, Monatlich, TagNum, Mon, Die, Mit, Don, Fre, Sam, Son, " +
                    " tmp, LDatum, Bemerkung, Erforderlich, Dauer, XML)" +
                    " SELECT m.MassID, PlanID, NachtMo, Morgens, Mittags, Nachmittags, Abends, NachtAb, UhrzeitAnzahl, " +
                    " Uhrzeit, Taeglich, Woechentlich, Monatlich, TagNum, Mon, Die, Mit, Don, Fre, Sam, Son, " +
                    " ?, LDatum, Bemerkung, Erforderlich, Dauer, XML " +
                    " FROM MassTermin m " +
                    //" INNER JOIN Massnahmen mass ON mass.MassID = m.MassID " +
                    " WHERE m.PlanID = ? AND m.tmp = 0 ";
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setLong(1, OPDE.getLogin().getLoginID());
            stmt.setLong(2, planid);
            stmt.executeUpdate();
            stmt.close();


            db.commit();
            db.setAutoCommit(true);
        } catch (SQLException ex) {
            try {
                db.rollback();
            } catch (SQLException ex1) {
                new DlgException(ex1);
                ex1.printStackTrace();
                System.exit(1);
            }
            new DlgException(ex);
        }
    }

    /**
     *
     */
    public static void copyPlanung(String sourceBW, String targetBW)
            throws SQLException {
        Connection db = OPDE.getDb().db;
        boolean doCommit = false;

        try {
            // Hier wird bestimmt, welche Pflegeplanung mit dem Quellbewohner verbunden sind.
            String selectSQL = "" +
                    " SELECT p.PlanID, p.BWKennung, p.Stichwort, p.Situation, p.Ziel, p.PlanKennung, p.BWIKID," +
                    " p.NKontrolle " +
                    " FROM Planung p " +
                    " WHERE p.BWKennung = ? ";
            PreparedStatement selectStmt = OPDE.getDb().db.prepareStatement(selectSQL);
            selectStmt.setString(1, sourceBW);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.first()) {
                rs.beforeFirst();

                // Hier beginnt eine Transaktion, wenn es nicht schon eine gibt.
                if (db.getAutoCommit()) {
                    db.setAutoCommit(false);
                    db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                    db.commit();
                    doCommit = true;
                }

                while (rs.next()) {
                    long altPlanid = rs.getLong("PlanID");

                    // Daten für die NEUE Planung
                    HashMap hm = new HashMap();
                    hm.put("BWKennung", targetBW);
                    hm.put("Stichwort", rs.getString("Stichwort"));
                    hm.put("Situation", rs.getString("Situation"));
                    hm.put("Ziel", rs.getString("Ziel"));
                    hm.put("BWIKID", rs.getLong("BWIKID"));
                    hm.put("Von", "!NOW!");
                    hm.put("Bis", "!BAW!");
                    hm.put("AnUKennung", OPDE.getLogin().getUser().getUKennung());
                    hm.put("AbUKennung", null);
                    hm.put("PlanKennung", OPDE.getDb().getUID("__plankenn"));
                    hm.put("NKontrolle", rs.getDate("NKontrolle"));

                    long neuPlanid = op.tools.DBHandling.insertRecord("Planung", hm);
                    if (neuPlanid < 0) {
                        throw new SQLException("Fehler bei Insert into Planung");
                    }
                    hm.clear();

                    // MassTermin kopieren
                    String sql = "" +
                            " INSERT INTO MassTermin (MassID, PlanID, Morgens, Mittags, Nachmittags, Abends, NachtAb, UhrzeitAnzahl," +
                            " Uhrzeit, Taeglich, Woechentlich, Monatlich, TagNum, Mon, Die, Mit, Don, Fre, Sam, Son, " +
                            " tmp, LDatum, Bemerkung, Erforderlich) " +
                            " SELECT MassID, ?, Morgens, Mittags, Nachmittags, Abends, NachtAb, UhrzeitAnzahl, " +
                            " Uhrzeit, Taeglich, Woechentlich, Monatlich, TagNum, Mon, Die, Mit, Don, Fre, Sam, Son, " +
                            " 0, LDatum, Bemerkung, Erforderlich " +
                            " FROM MassTermin m " +
                            " WHERE m.PlanID = ? AND m.tmp = 0 ";
                    PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
                    stmt.setLong(1, neuPlanid);
                    //stmt.setLong(2, OPDE.getLogin().getLoginID());
                    stmt.setLong(2, altPlanid);
                    stmt.executeUpdate();
                    stmt.close();

                    // DFN abgleichen.
                    DFNImport.importDFN(neuPlanid, SYSCalendar.nowDB(), 0);

                } // WHILE
                if (doCommit) {
                    db.commit();
                    db.setAutoCommit(true);
                }
            } // if(rs.first)
        } catch (SQLException ex) {
            if (doCommit) {
                db.rollback();
            }
            throw new SQLException(ex);

        }
    }


    /**
     * Löscht die Original Einträge und ändert die Temporären Records
     * auf dauerhaft gültig (tmp = 0)
     *
     * @param planid
     * @throws java.sql.SQLException
     */
    public static void tmp2real(long planid)
            throws SQLException {

        // DEEEEEEEEELLLLEEEEEEEEEEEEEEETTTTTEEEEEEEEEEEEEEEEEEEE
        // MassTermin
        String delete = "" +
                " DELETE m.* FROM MassTermin m " +
                " WHERE m.PlanID = ? AND m.tmp = 0 ";
        PreparedStatement stmtDel = OPDE.getDb().db.prepareStatement(delete);
        stmtDel.setLong(1, planid);
        stmtDel.executeUpdate();
        stmtDel.close();

        String update;
        PreparedStatement stmt;

        // UUUUUUUUPPPPPPPPDDDDDAAAAAAAAATTTTTTTTTTTEEEEEEEEEEEEEEEE
        update = "UPDATE MassTermin SET PlanID=?, tmp=0 WHERE tmp = ?";
        stmt = OPDE.getDb().db.prepareStatement(update);
        stmt.setLong(1, planid);
        stmt.setLong(2, OPDE.getLogin().getLoginID());
        stmt.executeUpdate();
        stmt.close();

    }

    public static void dropTmp() {
        Connection db = OPDE.getDb().db;
        try {
            // Hier beginnt eine Transaktion
            db.setAutoCommit(false);
            db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            db.commit();

            // MassTermin
            String delete = "DELETE FROM MassTermin WHERE tmp = ?";
            PreparedStatement stmtDel = OPDE.getDb().db.prepareStatement(delete);
            stmtDel.setLong(1, OPDE.getLogin().getLoginID());
            stmtDel.executeUpdate();
            stmtDel.close();

            db.commit();
            db.setAutoCommit(true);

        } catch (SQLException ex) {
            try {
                db.rollback();
            } catch (SQLException ex1) {
                new DlgException(ex1);
                ex1.printStackTrace();
                System.exit(1);
            }
            new DlgException(ex);
        }
    }

    public static ArrayList loadPlanung(String bwkennung, long planid, long tmp, boolean auchKontrollen) {
        String sql;
        ArrayList detail = new ArrayList();

        // MASSNAHMEN
        sql = "" +
                " SELECT t.TermID, t.UhrzeitAnzahl, t.Uhrzeit, t.NachtMo, t.Morgens, " +
                " t.Mittags, t.Nachmittags, t.Abends, t.NachtAb, t.Mon, t.Die, t.Mit, " +
                " t.Don, t.Fre, t.Sam, t.Son, t.Taeglich, t.Woechentlich, t.Monatlich, " +
                " t.TagNum, t.TermID, m.Bezeichnung, " +
                " t.Erforderlich, t.LDatum, t.Bemerkung, t.Dauer" +
                " FROM MassTermin t " +
                " INNER JOIN Massnahmen m ON m.MassID = t.MassID " +
                " WHERE t.PlanID = ? AND t.tmp = ? " +
                " ORDER BY m.Bezeichnung ";

        PreparedStatement stmt;
        ResultSet rs;

        OPDE.debug("loadPlanung: planid:" + planid);

        try {
            stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setLong(1, planid);
            stmt.setLong(2, tmp);
            rs = stmt.executeQuery();

            while (rs.next()) {
                OPDE.debug("loadPlanung: termid:" + rs.getLong("TermID"));
                String termin = getTerminAsHTML(
                        rs.getTime("Uhrzeit"), rs.getInt("UhrzeitAnzahl"), rs.getInt("NachtMo"),
                        rs.getInt("Morgens"), rs.getInt("Mittags"), rs.getInt("Nachmittags"), rs.getInt("Abends"),
                        rs.getInt("NachtAb"), rs.getInt("Mon"), rs.getInt("Die"), rs.getInt("Mit"), rs.getInt("Don"),
                        rs.getInt("Fre"), rs.getInt("Sam"), rs.getInt("Son"), rs.getInt("Taeglich"), rs.getInt("Woechentlich"),
                        rs.getInt("Monatlich"), rs.getInt("TagNum"), rs.getDate("LDatum"), rs.getString("Bemerkung"),
                        rs.getBoolean("Erforderlich"));

                Object[] s = {ART_MASSNAHME, "<font size\"+1\"><b>" + rs.getString("Bezeichnung") + "</b> (" + rs.getDouble("t.Dauer") + " Minuten)</font>" + termin, rs.getLong("TermID")};

                detail.add(s);
            }
            rs.close();
            stmt.close();
        } // try
        catch (SQLException se) {
            new DlgException(se);
        } // catch

        if (auchKontrollen) {
            // Kontrollen ==================================================
            sql = "" +
                    " SELECT Datum, Bemerkung, UKennung, PKonID, Abschluss " +
                    " FROM PlanKontrolle " +
                    " WHERE PlanID = ? " +
                    " ORDER BY Datum ";
            try {
                stmt = OPDE.getDb().db.prepareStatement(sql);
                stmt.setLong(1, planid);
                rs = stmt.executeQuery();
                //SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
                while (rs.next()) {
                    String kontrolle = getKontrolleAsHTML(rs.getTimestamp("Datum"), rs.getString("UKennung"), rs.getString("Bemerkung"), rs.getBoolean("Abschluss"));
                    Object[] s = {ART_KONTROLLEN, kontrolle, rs.getLong("PKonID")};
                    detail.add(s);
                }
            } catch (SQLException ex) {
                new DlgException(ex);
            }
        }
        // Kontrollen Ende ==============================================

        return detail;
    }

    public static ArrayList loadBibliothek(String suche, String bwkennung) {
        String sql;
        ArrayList detail = new ArrayList();

        if (!SYSTools.catchNull(suche).equals("")) {
            suche = op.tools.DBHandling.createSearchPattern(suche);
            // Zusammenstellen der Bibliothek

            // Einzel-Massnahmen
            PreparedStatement stmt;
            ResultSet rs;

            // Fehler: Nur aktive und Pflege- und Sozial maßnahmen anzeigen.
            sql = "" +
                    " SELECT m.MassID, m.Bezeichnung, m.Bezeichnung " +
                    " FROM Massnahmen m " +
                    " WHERE m.Aktiv = 1 AND (m.MassArt = 4 OR m.MassArt = 1) AND m.Bezeichnung LIKE ? " +
                    " ORDER BY m.Bezeichnung";

            try {
                stmt = OPDE.getDb().db.prepareStatement(sql);
                stmt.setString(1, suche);

                rs = stmt.executeQuery();

                while (rs.next()) {
                    Object[] s = {ART_MASSNAHME, rs.getString("Bezeichnung"), rs.getLong("MassID")};
                    detail.add(s);
                }
                rs.close();
                stmt.close();
            } // try
            catch (SQLException se) {
                new DlgException(se);
            } // catch

        }
        return detail;
    }

    public static String getKontrolleAsHTML(Date date, String ukennung, String bemerkung, boolean abschluss) {
        String result = "";
        DateFormat df = DateFormat.getDateInstance();
        result += "<b>Kontrolle vom " + df.format(date) + "</b>";
        result += "<p><b>Durchgeführt von:</b> " + op.ma.admin.DBHandling.getName2UKennung(ukennung) + "</p>";
        result += "<p><b>Ergebnis:</b> " + bemerkung + "</p>";
        if (abschluss) {
            result += "<u>Die Pflegeplanung wurde mit dieser Kontrolle geändert bzw. abgeschlossen</u>";
        }

        return result;
    }

    public static String getTerminAsHTML(Time Uhrzeit, int UhrzeitAnzahl, int NachtMo, int Morgens,
                                         int Mittags, int Nachmittags, int Abends, int NachtAb, int Mon, int Die, int Mit, int Don, int Fre,
                                         int Sam, int Son, int Taeglich, int Woechentlich, int Monatlich, int TagNum, Date LDatum, String kommentar,
                                         boolean erforderlich) {
        String result = "";


        final int ZEIT = 0;
        final int UHRZEIT = 1;
        int previousState = -1;

        int currentState;
        // Zeit verwendet ?
        if ((NachtMo + NachtAb + Morgens +
                Mittags + Nachmittags + Abends) > 0) {
            currentState = ZEIT;
        } else {
            currentState = UHRZEIT;
        }
        boolean headerNeeded = previousState == -1 || currentState != previousState;

        if (previousState > -1 && headerNeeded) {
            // noch den Footer vom letzten Durchgang dabei. Aber nur, wenn nicht
            // der erste Durchlauf, ein Wechsel stattgefunden hat und der
            // vorherige Zustand nicht MAXDOSIS war, das braucht nämlich keinen Footer.
            result += "</table>";
        }
        previousState = currentState;
        if (currentState == ZEIT) {
            if (headerNeeded) {
                result += "<table border=\"1\">" +
                        "   <tr>" +
                        "      <th align=\"center\">fm</th>" +
                        "      <th align=\"center\">mo</th>" +
                        "      <th align=\"center\">mi</th>" +
                        "      <th align=\"center\">nm</th>" +
                        "      <th align=\"center\">ab</th>" +
                        "      <th align=\"center\">sa</th>" +
                        "      <th align=\"center\">Wdh.</th>" +
                        "   </tr>";
            }
            String wdh = getWiederholung(Mon, Die, Mit, Don, Fre,
                    Sam, Son, Taeglich, Woechentlich, Monatlich, TagNum, LDatum);

            result += "    <tr>" +
                    "      <td align=\"center\">" + (NachtMo > 0 ? NachtMo : "--") + "</td>" +
                    "      <td align=\"center\">" + (Morgens > 0 ? Morgens : "--") + "</td>" +
                    "      <td align=\"center\">" + (Mittags > 0 ? Mittags : "--") + "</td>" +
                    "      <td align=\"center\">" + (Nachmittags > 0 ? Nachmittags : "--") + "</td>" +
                    "      <td align=\"center\">" + (Abends > 0 ? Abends : "--") + "</td>" +
                    "      <td align=\"center\">" + (NachtAb > 0 ? NachtAb : "--") + "</td>" +
                    "      <td>" + wdh + "</td>" +
                    "    </tr>";
        } else if (currentState == UHRZEIT) {
            if (headerNeeded) {
                result += "<table border=\"1\" >" +
                        "   <tr>" +
                        "      <th align=\"center\">Uhrzeit</th>" +
                        "      <th align=\"center\">Anzahl</th>" +
                        "      <th align=\"center\">Wdh.</th>" +
                        "   </tr>";
            }
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String wdh = getWiederholung(Mon, Die, Mit, Don, Fre,
                    Sam, Son, Taeglich, Woechentlich, Monatlich, TagNum, LDatum);
            result += "    <tr>" +
                    "      <td align=\"center\">" + sdf.format(Uhrzeit) + " Uhr</td>" +
                    "      <td align=\"center\">" + UhrzeitAnzahl + "</td>" +
                    "      <td>" + wdh + "</td>" +
                    "    </tr>";
        } else {
            result = "!!FEHLER!!";
        }

        result += "</table>";

        if (!SYSTools.catchNull(kommentar).equals("")) {
            result += "<br/><b>Kommentar: </b>" + kommentar;
        }

        if (erforderlich) {
            result += "<br/><font color=\"red\">Bearbeitung erforderlich !</font>";
        }

        return result;
    }

    public static String getPlanungenAsHTML(String bwkennung, Date von, Date bis) {

        /*
         * 		((von <= '2006-07-01' AND bis >= '2006-07-31') OR (von <= '2006-07-01' AND
        bis >= '2006-07-31') OR (von > '2006-07-01' AND bis < '2006-07-31'))
         */
        String sql = "" +
                " SELECT PlanID FROM Planung " +
                " WHERE BWKennung = ? AND ((von <= ? AND bis >= ?) OR (von <= ? AND bis >= ?) OR (von > ? AND bis < ?)) " +
                " ORDER BY PlanKennung, von, Stichwort ";
        String html = "";
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setString(1, bwkennung);
            stmt.setTimestamp(2, new java.sql.Timestamp(von.getTime()));
            stmt.setTimestamp(3, new java.sql.Timestamp(von.getTime()));
            stmt.setTimestamp(4, new java.sql.Timestamp(bis.getTime()));
            stmt.setTimestamp(5, new java.sql.Timestamp(bis.getTime()));
            stmt.setTimestamp(6, new java.sql.Timestamp(von.getTime()));
            stmt.setTimestamp(7, new java.sql.Timestamp(bis.getTime()));

            ResultSet rs = stmt.executeQuery();
            if (rs.first()) {
                html += "<h1>Pflegeplanungen für " + SYSTools.getBWLabel(bwkennung) + "</h1>";
                rs.beforeFirst();
                while (rs.next()) {
                    html += getPlanungAsHTML(rs.getLong("PlanID"));
                }
                rs.close();
                stmt.close();
            }
        } catch (SQLException ex) {
            new DlgException(ex);
        }
        html = "<html><head>" +
                "<title>" + SYSTools.getWindowTitle("") + "</title>" +
                "<script type=\"text/javascript\">" +
                "window.onload = function() {" +
                "window.print();" +
                "}</script></head><body>" + html + "</body></html>";
        return html;
    }

    /**
     * Gibt einen String zurück, der eine HTML Darstellung einer Pflegeplanung enthält.
     *
     * @param planid
     * @return
     */
    public static String getPlanungAsHTML(long planid) {
        ArrayList planung = loadPlanung("", planid, 0, true);
        HashMap plandetails = DBRetrieve.getSingleRecord("Planung", "PlanID", planid);
        String kategorie = DBRetrieve.getSingleValue("BWInfoKat", "Bezeichnung", "BWIKID", (BigInteger) plandetails.get("BWIKID")).toString();
        String html = "<h2><font color=\"green\">";
//        if (((Date) plandetails.get("Bis")).before(SYSConst.DATE_BIS_AUF_WEITERES)) {
//            html += "<s>";
//        }
        html += "Pflegeplanung &raquo;" + plandetails.get("Stichwort").toString() + "&laquo;";
//        if (((Date) plandetails.get("Bis")).before(SYSConst.DATE_BIS_AUF_WEITERES)) {
//            html += "</s>";
//        }
        html += "</font></h2>";
        html += "<b>Kategorie:</b> " + kategorie + "<br/>";
//        if (!SYSTools.catchNull(plandetails.get("Bemerkung")).equals("")) {
//            html += "<p><u>Bemerkung:</u> " + plandetails.get("Bemerkung") + "</b></p>";
//        }
        DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT);
        html += "<b>Prüfungstermin:</b> " + df.format((Date) plandetails.get("NKontrolle")) + "<br/>";
        html += "<b>Erstellt von:</b> " + op.ma.admin.DBHandling.getName2UKennung(plandetails.get("AnUKennung").toString()) + "  ";
        html += "<b>Am:</b> " + df.format((Date) plandetails.get("Von")) + "<br/>";
        if (((Date) plandetails.get("Bis")).before(SYSConst.DATE_BIS_AUF_WEITERES)) {
            html += "<b>Abgesetzt von:</b> " + op.ma.admin.DBHandling.getName2UKennung(plandetails.get("AbUKennung").toString()) + "  ";
            html += "<b>Am:</b> " + df.format((Date) plandetails.get("Bis")) + "<br/>";
        }


        html += "<h3>Situation</h3>" + SYSTools.replace(plandetails.get("Situation").toString(), "\n", "<br/>");
        html += "<h3>Ziel(e):</h3>" + SYSTools.replace(plandetails.get("Ziel").toString(), "\n", "<br/>");

        Iterator it = planung.iterator();
        int prevArt = -1;
        html += "<h3>Informationen und Massnahmen</h3>";
        if (!it.hasNext()) {
            html += "<ul><li><i>bisher nichts zugeordnet</i></li></ul>";
        }
        while (it.hasNext()) {
            Object[] o = (Object[]) it.next(); //{ART_MASSNAHME_GRUPPE, 0l, false, html, rs.getLong("TermID")};
            int art = ((Integer) o[0]).intValue();
//            Object obj = o[2];
            String txt = o[1].toString();
//            Object pkid = o[4];

            if (prevArt != art) {
                if (prevArt > -1) { // nicht beim ersten mal.
                    html += "</ul>";
                } else {
                    html += "<ul>";
                }
                prevArt = art;
                switch (art) {
                    case ART_MASSNAHME: {
                        html += "<li><b>Einzelmassnahmen</b></li><ul>";
                        break;
                    }
                    case ART_KONTROLLEN: {
                        html += "<li><b>Kontrolltermine</b></li><ul>";
                        break;
                    }
                    default: {
                    }
                } // switch
                //html += "<ul>";
            } // Gruppenwechsel
            html += "<li>" + txt + "</li>";
        }
        html += "</ul></ul>";
        return html;
    }

    public static String getWiederholung(int Mon, int Die, int Mit, int Don, int Fre,
                                         int Sam, int Son, int Taeglich, int Woechentlich, int Monatlich, int TagNum, Date LDatum) {
        String result = "";

        if (Taeglich > 0) {
            if (Taeglich > 1) {
                result += "alle " + Taeglich + " Tage";
            } else {
                result += "jeden Tag";
            }
        } else if (Woechentlich > 0) {
            if (Woechentlich == 1) {
                result += "jede Woche ";
            } else {
                result += "alle " + Woechentlich + " Wochen ";
            }

            if (Mon > 0) {
                result += "Mon ";
            }
            if (Die > 0) {
                result += "Die ";
            }
            if (Mit > 0) {
                result += "Mit ";
            }
            if (Don > 0) {
                result += "Don ";
            }
            if (Fre > 0) {
                result += "Fre ";
            }
            if (Sam > 0) {
                result += "Sam ";
            }
            if (Son > 0) {
                result += "Son ";
            }

        } else if (Monatlich > 0) {
            if (Monatlich == 1) {
                result += "jeden Monat ";
            } else {
                result += "alle " + Monatlich + " Monate ";
            }

            if (TagNum > 0) {
                result += "jeweils am " + TagNum + ". des Monats";
            } else {

                int wtag = 0;
                String tag = "";
                if (Mon > 0) {
                    tag += "Montag ";
                    wtag = Mon;
                }
                if (Die > 0) {
                    tag += "Dienstag ";
                    wtag = Die;
                }
                if (Mit > 0) {
                    tag += "Mittwoch ";
                    wtag = Mit;
                }
                if (Don > 0) {
                    tag += "Donnerstag ";
                    wtag = Don;
                }
                if (Fre > 0) {
                    tag += "Freitag ";
                    wtag = Fre;
                }
                if (Sam > 0) {
                    tag += "Samstag ";
                    wtag = Sam;
                }
                if (Son > 0) {
                    tag += "Sonntag ";
                    wtag = Son;
                }
                result += "jeweils am " + wtag + ". " + tag + " des Monats";
            }
        } else {
            result = "";
        }

        if (SYSCalendar.sameDay(LDatum, new Date()) > 0) { // Die erste Ausführung liegt in der Zukunft
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
            result += "<br/>erst ab: " + sdf.format(LDatum);
        }

        return result;
    }

    /**
     * @param massid
     * @param planid
     * @param tmp
     * @throws java.sql.SQLException
     */
    public static void addMassnahme2Planung(long massid, long planid) throws SQLException {

        String sql = "" +
                " INSERT INTO MassTermin (MassID, PlanID, Morgens, Taeglich, LDatum, tmp, Dauer, XML) " +
                " VALUES (?, ?, 1, 1, now(), ?, " +
                "   (SELECT Dauer From Massnahmen WHERE MassID = ?)," +
                "   (SELECT XMLT From Massnahmen WHERE MassID = ?)" +
                " ) ";
        PreparedStatement stmt1 = OPDE.getDb().db.prepareStatement(sql);
        stmt1.setLong(1, massid);
        stmt1.setLong(2, planid);
        stmt1.setLong(3, OPDE.getLogin().getLoginID());
        stmt1.setLong(4, massid);
        stmt1.setLong(5, massid);
        stmt1.executeUpdate();

    }

    /**
     * Löscht <u>alle</u> nicht <b>abgehakten</b> BHPs für eine bestimmte Verordnung.
     *
     * @param verid ist die Verordnung, um die es geht.
     */
    public static void deletePlanung(long planid) {
        try {
            String bhp = " DELETE d.*, t.*, p.* FROM Planung p " +
                    " LEFT OUTER JOIN MassTermin t ON t.PlanID = p.PlanID " +
                    " LEFT OUTER JOIN DFN d ON d.TermID = t.TermID " +
                    " WHERE p.PlanID = ? ";
            PreparedStatement stmtDel = OPDE.getDb().db.prepareStatement(bhp);
            stmtDel.setLong(1, planid);
            stmtDel.executeUpdate();
            stmtDel.close();
        } catch (SQLException ex) {
            new DlgException(ex);
        }
    }

    /**
     * Löscht alle DFNs für eine bestimmte Planung.
     *
     * @param planid ist die Planung, um die es geht.
     */
    public static void cleanDFN(long planid)
            throws SQLException {
        String bhp = " DELETE d.* FROM DFN d " +
                " INNER JOIN MassTermin t ON t.TermID = d.TermID " +
                " INNER JOIN Planung p ON t.PlanID = p.PlanID " +
                " WHERE p.PlanID = ? ";
        PreparedStatement stmtDel = OPDE.getDb().db.prepareStatement(bhp);
        stmtDel.setLong(1, planid);
        stmtDel.executeUpdate();
        stmtDel.close();
    }

    /**
     * Löscht alle <b>heutigen</b> nicht <b>abgehakten</b> DFNs für eine bestimmte Planung ab einer bestimmten Tages-Zeit.
     *
     * @param ts    ist ein bestimmter Zeitpunkt. Das gilt natürlich nur für den aktuellen Tag. Somit ist
     *              bei ts nur der Uhrzeit anteil relevant. Über diesen wird die Schicht (bzw. Zeit) ermittelt. Bei BHPs,
     *              die sich auf eine bestimmte Uhrzeit beziehen, werden nur diejenigen gelöscht, die <b>größer gleich</b> ts sind.
     * @param verid ist die Verordnung, um die es geht.
     */
    public static void cleanDFN(long planid, long ts)
            throws SQLException {
        int zeit = SYSCalendar.ermittleZeit(ts);
        String bhp = " DELETE d.* FROM DFN d " +
                " INNER JOIN MassTermin t ON t.TermID = d.TermID " +
                " INNER JOIN Planung p ON t.PlanID = p.PlanID " +
                " WHERE p.PlanID = ? AND Status = 0 AND Date(Soll)=Date(now()) AND " +
                " (" +
                "   ( " +
                "       ( SZeit > ? )" +
                "   ) " +
                "   OR " +
                "   (" +
                "       ( SZeit = 0 AND Time(Soll) >= Time(?) )" +
                "   )" +
                " )";
        PreparedStatement stmtDel = OPDE.getDb().db.prepareStatement(bhp);
        stmtDel.setLong(1, planid);
        stmtDel.setInt(2, zeit);
        stmtDel.setTimestamp(3, new Timestamp(ts));
        stmtDel.executeUpdate();
        stmtDel.close();
    }

    /**
     * Setzt eine Pflegeplanung ab. Die zugehörigen DFNs werden ab JETZT entfernt. Es wird automatisch
     * eine Planungskontrolle eingefügt, zum selben Zeitpunkt. Als Kommentar enthält sie die Bemerkung.
     * Sie ist als Abschlussprüfung markiert.
     *
     * @param planid    welche Planung soll abgesetzt werden.
     * @param bemerkung Bemerkung, die in den Planungskontrollen hinterlegt werden soll
     * @return erfolg
     */
    public static boolean absetzen(long planid, String bemerkung) {
        Connection db = OPDE.getDb().db;
        boolean result = false;
        boolean doCommit = false;
        try {
            // Hier beginnt eine Transaktion, wenn es nicht schon eine gibt.
            if (db.getAutoCommit()) {
                db.setAutoCommit(false);
                db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                db.commit();
                doCommit = true;
            }

            // Plan anpassen
            HashMap hm = new HashMap();
            hm.put("Bis", "!NOW!");
            hm.put("AbUKennung", OPDE.getLogin().getUser().getUKennung());
            if (!op.tools.DBHandling.updateRecord("Planung", hm, "PlanID", planid)) {
                throw new SQLException("Fehler bei UPDATE Planung");
            }

            hm.clear();
            // Letzte Kontrolle einfügen
            hm.put("Datum", "!NOW!");
            hm.put("PlanID", planid);
            hm.put("UKennung", OPDE.getLogin().getUser().getUKennung());
            hm.put("Bemerkung", bemerkung);
            hm.put("Abschluss", true);
            if (op.tools.DBHandling.insertRecord("PlanKontrolle", hm) < 0) {
                throw new SQLException("Fehler bei INSERT Planung");
            }

            hm.clear();

            cleanDFN(planid, SYSCalendar.nowDB());

            if (doCommit) {
                db.commit();
                db.setAutoCommit(true);
            }

            result = true;
        } catch (SQLException ex) {
            try {
                if (doCommit) {
                    db.rollback();
                }
                result = false;
            } catch (SQLException ex1) {
                new DlgException(ex1);
                ex1.printStackTrace();
                System.exit(1);
            }
            new DlgException(ex);
        }
        return result;
    }

    public static long numAffectedDFNs(long planid) {
        long result = 0;
        String sql = " SELECT count(*) " +
                " FROM DFN dfn " +
                " INNER JOIN MassTermin t ON t.TermID = dfn.TermID " +
                " WHERE t.PlanID = ? AND dfn.Status > 0 ";
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setLong(1, planid);
            ResultSet rs = stmt.executeQuery();
            if (rs.first()) {
                result = rs.getLong(1);
            }
        } catch (SQLException ex) {
            new DlgException(ex);
        }
        return result;
    }


}
