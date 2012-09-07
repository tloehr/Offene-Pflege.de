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
 */
package op.tools;

import op.OPDE;

import javax.swing.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Diese Klasse enthält Methoden, die bestimmte Informationen aus der Datenbank herausziehen.
 *
 * @author tloehr
 */
@Deprecated
public class DBRetrieve {

    public static final int MODE_INTERVAL_OVERLAP = 0;
    public static final int MODE_INTERVAL_DISJUNCTIVE = 1;
    public static final int MODE_INTERVAL_NOCONSTRAINTS = 2;

//    public static Properties getOCProps(String ip) {
//        Properties p = new Properties();
//
//        PreparedStatement stmt;
//        ResultSet rs;
//
//        try {
//            String sql = "SELECT K, V FROM OCProps WHERE IP = ?";
//            stmt = OPDE.getDb().db.prepareStatement(sql);
//            stmt.setString(1, ip);
//            rs = stmt.executeQuery();
//
//            while (rs.next()) {
//                p.put(rs.getString("K"), rs.getString("V"));
//            }
//        } // try
//        catch (SQLException se) {
//            // new DlgException(se);
//        } // catch
//
//        return p;
//    }

//    /**
//     * Ermittelt, seit wann ein Bewohner abwesend war.
//     *
//     * @return Datum des Beginns der Abwesenheitsperiode. =NULL wenn ANwesend.
//     */
//    public static Date absentSince(String bwkennung) {
//        PreparedStatement stmt;
//        ResultSet rs;
//        Date d = null;
//        try {
//            String sql = "" +
//                    " SELECT Von " +
//                    " FROM BWInfo " +
//                    " WHERE BWINFTYP = 'abwe' AND BWKennung = ? AND von <= NOW() AND bis >= NOW()";
//            stmt = OPDE.getDb().db.prepareStatement(sql);
//            stmt.setString(1, bwkennung);
//            rs = stmt.executeQuery();
//            if (rs.first()) {
//                d = rs.getDate("von");
//            } else {
//                d = null;
//            }
//
//        } // try
//        catch (SQLException se) {
//            // new DlgException(se);
//        } // catch
//        return d;
//    }

//    public static HashMap getBW(String bwkennung) {
//        HashMap hm = new HashMap();
//
//        // Bewohnernamen in den Label schreiben.
//        try {
//            String sql = "SELECT b.nachname, b.vorname, b.GebDatum FROM Bewohner b WHERE BWKennung = ?";
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            stmt.setString(1, bwkennung);
//            ResultSet rs = stmt.executeQuery();
//            rs.first();
//            hm.put("vorname", rs.getString("b.vorname"));
//            hm.put("nachname", rs.getString("b.nachname"));
//            hm.put("bwkennung", bwkennung);
//            hm.put("gebdatum", rs.getDate("b.GebDatum"));
//
//        } catch (SQLException ex) {
//            // new DlgException(ex);
//        }
//
//
//        return hm;
//    }

    /**
     * liest aus einer beliegigen Tabelle, genau einen Wert heraus.
     */
    public static Object getSingleValue(String table, String field, String wherefield, Object wherevalue) {
        HashMap where = new HashMap();
        where.put(wherefield, new Object[]{wherevalue, "="});
        return getSingleValue(table, field, where);
    }

    public static Object getSingleValue(String table, String field, HashMap where) {
        PreparedStatement stmt;
        Object o = null;

        String sql = "SELECT " + field + " FROM " + table + " WHERE ";

        ArrayList whereval = new ArrayList();

        Iterator it = where.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String wherefield = (String) entry.getKey();
            Object[] os = (Object[]) entry.getValue();
            whereval.add(os[0]); // value
            sql += wherefield + " " + os[1] + " ?"; // enthält den operator
            if (it.hasNext()) {
                sql += " AND ";
            }
        }

        OPDE.debug("getSingleValue/3: " + sql);

        try {
            stmt = OPDE.getDb().db.prepareStatement(sql);
            for (int i = 0; i < whereval.size(); i++) {
                stmt.setObject(i + 1, whereval.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.first()) {
                o = rs.getObject(1);
            }
        } catch (SQLException ex) {
            // new DlgException(ex);
        }
        return o;
    }

    /**
     * Zieht eine Zeile aus der Datenbank. Sollte das Suchkriterium mehrere Treffer ergeben, dann gibts nur die erste Zeile.
     *
     * @param table  Name der Datenbanktabelle
     * @param fields Ein Array mit den gewünschten Spalteninhalten der Tabelle
     * @param pk     Spaltenname, der in der WHERE Clause verwendet werden soll.
     * @param pkval  gewünschte Filterkriterium
     * @return Suchergebnis, null bei leerem Suchergebnis
     */
    public static HashMap getSingleRecord(String table, Object[] fields, String pk, Object pkval) {
        HashMap result = null;
        try {
            ResultSet rs = getResultSet(table, fields, pk, pkval, "=");
            if (rs.first()) {
                result = new HashMap();
                for (int i = 0; i < fields.length; i++) {
                    result.put(fields[i], rs.getObject(fields[i].toString()));
                }
            }
        } catch (SQLException ex) {
            // new DlgException(ex);
        }
        return result;
    }

    public static HashMap getSingleRecord(String table, String pk, Object pkval) {
        return getSingleRecord(table, getCols(table), pk, pkval);
    }

    /**
     * Diese Methode liest ein ResultSet ein und gibt es an die aufrufende Methode zurück.
     *
     * @param table              Name der Datenbanktabelle
     * @param fields             Ein Array mit den gewünschten Spalteninhalten der Tabelle
     * @param wherefield         Spaltenname, der in der WHERE Clause verwendet werden soll.
     * @param wherevalue         gewünschte Filterkriterium
     * @param comparisonOperator Vergleichsoperator, der in der WHERE Clause benutzt wird
     * @return Suchergebnis, null bei leerem Suchergebnis
     */
    public static ResultSet getResultSet(String table, Object[] fields, String wherefield, Object wherevalue, String comparisonOperator) {
        HashMap where = new HashMap();
        //where.put(wherefield,wherevalue);
        where.put(wherefield, new Object[]{wherevalue, comparisonOperator});
        return getResultSet(table, fields, where);
    }

    public static ResultSet getResultSet(String table, Object[] fields, String wherefield, Object wherevalue, String comparisonOperator, String[] order) {
        HashMap where = new HashMap();
        //where.put(wherefield,wherevalue);
        where.put(wherefield, new Object[]{wherevalue, comparisonOperator});
        return getResultSet(table, fields, where, order);
    }

    public static ResultSet getResultSet(String table, String wherefield, Object wherevalue, String comparisonOperator) {
        HashMap where = new HashMap();
        //where.put(wherefield,wherevalue);
        where.put(wherefield, new Object[]{wherevalue, comparisonOperator});
        return getResultSet(table, getCols(table), where);
    }

    public static ResultSet getResultSet(String table, String wherefield, Object wherevalue, String comparisonOperator, String[] order) {
        HashMap where = new HashMap();
        //where.put(wherefield,wherevalue);
        where.put(wherefield, new Object[]{wherevalue, comparisonOperator});
        return getResultSet(table, getCols(table), where, order);
    }

    public static ResultSet getResultSet(String table, Object[] fields) {
        return getResultSet(table, fields, null, null);
    }

    public static ResultSet getResultSet(String table, Object[] fields, String[] order) {
        return getResultSet(table, fields, null, order);
    }

    public static ResultSet getResultSet(String table, HashMap where, String[] order) {
        return getResultSet(table, getCols(table), where, order);
    }

    public static ResultSet getResultSet(String table, Object[] fields, HashMap where) {
        return getResultSet(table, fields, where, null);
    }

    public static ResultSet getResultSet(String table, HashMap where) {
        return getResultSet(table, getCols(table), where, null);
    }

    public static ResultSet getResultSet(String table) {
        return getResultSet(table, getCols(table), null, null);
    }

    public static ResultSet getResultSet(String table, Object[] fields, HashMap where, String[] order) {
        PreparedStatement stmt;
        ResultSet rs = null;

        String sql = "SELECT ";

        for (int i = 0; i < fields.length; i++) {
            sql += fields[i].toString();
            if (i + 1 < fields.length) {
                sql += ",";
            }
        }

        sql += " FROM " + table;

        ArrayList whereval = new ArrayList();
        // FILTER------------
        if (where != null) {
            sql += " WHERE ";

            Iterator it = where.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                String wherefield = (String) entry.getKey();

                Object[] os = (Object[]) entry.getValue();
                whereval.add(os[0]); // value

                //whereval.add(entry.getValue());
                sql += wherefield + " " + os[1] + " ?";
                if (it.hasNext()) {
                    sql += " AND ";
                }
            }
        }

        // ORDER
        if (order != null) {
            sql += " ORDER BY ";

            if (order.length > 1) {
                for (int i = 0; i < order.length - 1; i++) {
                    sql += order[i] + ", ";
                }
            }
            sql += order[order.length - 1];
        }


        try {
            OPDE.debug("getResultSet: " + sql);
            stmt = OPDE.getDb().db.prepareStatement(sql);

            // FILTER------------
            if (where != null) {
                for (int i = 0; i < whereval.size(); i++) {
                    stmt.setObject(i + 1, whereval.get(i));
                }
            }

            rs = stmt.executeQuery();
        } catch (SQLException ex) {
            // new DlgException(ex);
        }
        return rs;

    }

    public static Object[] getCols(String tablename) {
        String sql = "SELECT * FROM " + tablename + " LIMIT 0,1";
        ArrayList result = new ArrayList();
        try {
            Statement stmt = OPDE.getDb().db.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int col = 1; col <= rsmd.getColumnCount(); col++) {
                result.add(rsmd.getColumnName(col));
            }
        } catch (SQLException ex) {
            // new DlgException(ex);
        }
        return result.toArray();
    }

    public static HashMap getDFNPlanungDetails(long dfnpid) {
        HashMap hm = null;
        String sql = "SELECT dfn.BWKennung, dfn.UKennung, dfn.MassID, dfn.Frueh, dfn.Spaet, dfn.Nacht, " +
                "dfn.Sonst, dfn.Taeglich, dfn.Woechentlich, dfn.Monatlich, dfn.TagNum, dfn.Mon, dfn.Die, " +
                "dfn.Mit, dfn.Don, dfn.Fre, dfn.Sam, dfn.Son, dfn.LDatum, dfn.Erzwingen, dfn.DFNPID, dfn.Bemerkung, mass.Bezeichnung " +
                "FROM oc.DFNPlanung dfn INNER JOIN oc.Massnahmen mass ON dfn.MassID = mass.MassID " +
                "WHERE dfn.DFNPID = ?";
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setLong(1, dfnpid);
            ResultSet rs = stmt.executeQuery();
            if (rs.first()) {
                hm = new HashMap();
                try {
                    hm.put("frueh", rs.getInt("dfn.Frueh"));
                    hm.put("spaet", rs.getInt("dfn.Spaet"));
                    hm.put("nacht", rs.getInt("dfn.Nacht"));
                    hm.put("sonst", rs.getInt("dfn.Sonst"));
                    hm.put("taeglich", rs.getInt("dfn.Taeglich"));
                    hm.put("woche", rs.getInt("dfn.Woechentlich"));
                    hm.put("monat", rs.getInt("dfn.Monatlich"));
                    hm.put("monattag", rs.getInt("dfn.TagNum"));

                    if (rs.getInt("dfn.Mon") > 0) {
                        hm.put("mon", rs.getInt("dfn.Mon"));
                    }
                    if (rs.getInt("dfn.Die") > 0) {
                        hm.put("die", rs.getInt("dfn.Die"));
                    }
                    if (rs.getInt("dfn.Mit") > 0) {
                        hm.put("mit", rs.getInt("dfn.Mit"));
                    }
                    if (rs.getInt("dfn.Don") > 0) {
                        hm.put("don", rs.getInt("dfn.Don"));
                    }
                    if (rs.getInt("dfn.Fre") > 0) {
                        hm.put("fre", rs.getInt("dfn.Fre"));
                    }
                    if (rs.getInt("dfn.Sam") > 0) {
                        hm.put("sam", rs.getInt("dfn.Sam"));
                    }
                    if (rs.getInt("dfn.Son") > 0) {
                        hm.put("son", rs.getInt("dfn.Son"));
                    }

                    if (rs.getBoolean("dfn.Erzwingen")) {
                        hm.put("erzwingen", true);
                    }

                    hm.put("ref", rs.getDate("dfn.LDatum"));
                    hm.put("bemerkung", rs.getString("dfn.Bemerkung"));
                    hm.put("title", "Details für " + rs.getString("dfn.MassID") + "-" + rs.getString("mass.Bezeichnung"));

                } catch (SQLException ex) {
                    // new DlgException(ex);
                }
            }

        } catch (SQLException ex) {
            // new DlgException(ex);
        }

        return hm;
    }

//    /**
//     * liest aus der Tabelle "BWerte" den jeweils letzten aktuell gemessenen Wert heraus.
//     *
//     * @param Kennung des Bewohners
//     * @param xml     code des gewünschten Wertes
//     * @return Eine Liste mit zwei Elementen (Date datum, double wert)
//     */
//    public static ArrayList getLetztenBWert(String bwkennung, String xml) {
//        ArrayList result = new ArrayList();
//
//        String sql = "SELECT PIT, WERT FROM BWerte WHERE BWKennung = ? AND XML = ? " +
//                " ORDER BY PIT DESC LIMIT 0,1 ";
//        try {
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            stmt.setString(1, bwkennung);
//            //stmt.setString(3, bwkennung);
//            stmt.setString(2, xml);
//            //stmt.setString(4, xml);
//            ResultSet rs = stmt.executeQuery();
//            if (rs.first()) {
//                result.add(rs.getDate("PIT"));
//                result.add(rs.getDouble("Wert"));
//            } else {
//                result = null;
//            }
//        } catch (SQLException ex) {
//            // new DlgException(ex);
//        }
//        return result;
//    }

    /**
     * Diese Methode sammelt das aktuelleste, letzte Attribut, gemäß einer BWINFTYP und einer BWKennung auf.
     *
     * @param bwkennung - Kennung des Bewohners
     * @param BWINFTYP  - Kennung des gewünschten Attributs
     * @return Liste der Daten des letzten Attributes (XMLC, Von, Bis, BWINFOID, _creator, _editor, _cdate, _mdate). Datentypen
     *         (String, Date, Date, long, String, String, Timestamp, Timestamp)
     */
    public static ArrayList getLetztesBWAttribut(String bwkennung, String BWINFTYP) {
        OPDE.debug("DBRetrieve.getLetztesBWAttribut() :: " + bwkennung + ", " + BWINFTYP);
        ArrayList result = new ArrayList();
        String sql = "SELECT XML, Von, Bis, BWINFOID, Bemerkung FROM BWInfo " +
                " WHERE BWKennung=? AND BWINFTYP=? " +
                " AND Von IN (SELECT MAX(Von) FROM BWInfo WHERE BWKennung=? AND BWINFTYP=?)";
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setString(1, bwkennung);
            stmt.setString(3, bwkennung);
            stmt.setString(2, BWINFTYP);
            stmt.setString(4, BWINFTYP);
            ResultSet rs = stmt.executeQuery();
            if (rs.first()) {
                result.add(rs.getString("XML"));
                result.add(rs.getDate("Von"));
                result.add(rs.getDate("Bis"));
                result.add(rs.getLong("BWINFOID"));
                result.add(rs.getString("Bemerkung"));
            } else {
                result = null;
            }
        } catch (SQLException ex) {
            // new DlgException(ex);
        }
        return result;
    }

    /**
     * Diese Methode ermittelt die freien Zeiträume aus Tabellen mit historisierten Daten (in der Regel mit "Von","Bis" Spalten). Dabei werden die einzelnen Zeiträume
     * der Reihe nach durchgegangen und jeweils nur dann ein freier Zeitraum dazwischen ermittelt, wenn mindestens ein
     * Tag <i>Platz</i> dazwischen ist.
     * <p/>
     *
     * @param table Name der Datenbanktabelle
     * @param from  Name der Date Spalte, die die Information zum Beginn des Intervalls enthält.
     * @param to    Name der Date Spalte, die die Information zum Ende des Intervalls enthält.
     * @param where String mit der Where Bedingung zur Filterung des Datensätze.
     * @param mode  hier kann man bestimmen, wie die Zeiträume ermittelt werden sollen. Es können die Konstanten MODE_INTERVAL_OVERLAP, MODE_INTERVAL_DISJUNCTIVE,
     *              MODE_INTERVAL_NOCONSTRAINTS verwendet werden. Bei Overlap überlappen die Zeiträume um genau einen Tag. Bei Disjunctive gibt es keine Überlappung, bei noConstraints ist der
     *              freie Zeitraum <b>immer</b> VAA - BAW.
     * @return ArrayList mit den Lücken in den Zeiträumen. Die Zeiträumen beginnen mit SYSConst.VON_ANFANG_AN und enden mit SYSConst.BIS_AUF_WEITERES. NULL bei Fehler.
     */
//    public static ArrayList getFreeIntervals(String table, String from, String to, String where, int mode) {
//        ArrayList result = null;
//        switch (mode) {
//            case MODE_INTERVAL_DISJUNCTIVE: {
//                result = new ArrayList();
//                result = getFreeIntervalsDisjunctive(table, from, to, where);
//                break;
//            }
//            case MODE_INTERVAL_OVERLAP: {
//                result = new ArrayList();
//                result = getFreeIntervalsOverlap(table, from, to, where);
//                break;
//            }
//            case MODE_INTERVAL_NOCONSTRAINTS: {
//                result = new ArrayList();
//                result = getFreeIntervalsNoConstraints();
//                break;
//            }
//            default: {
//            }
//        }
//        return result;
//    }

    /**
     * Diese Methode ermittelt die freien Zeiträume aus Tabellen mit historisierten Daten (in der Regel mit "Von","Bis" Spalten).
     * Diese Version der Routine ermittelt freie Zeiträume in Tabellen, bei denen die Von und Bis Spalten Datetimes sind. Also
     * Datum und Uhrzeiten enthalten.
     * <p/>
     *
     * @param table Name der Datenbanktabelle
     * @param from  Name der Date Spalte, die die Information zum Beginn des Intervalls enthält.
     * @param to    Name der Date Spalte, die die Information zum Ende des Intervalls enthält.
     * @param where String mit der Where Bedingung zur Filterung des Datensätze.
     * @return ArrayList mit den Lücken in den Zeiträumen. Die Zeiträume beginnen mit SYSConst.DATE_VON_ANFANG_AN und
     *         enden mit SYSConst.DATE_BIS_AUF_WEITERES. NULL bei Fehler. Somit stehen in der ArrayList immer Paare von java.util.Date.
     *         Beispiel: [(VAA),(2007-12-31 23:59:59),(2008-02-02 00:00:00),(BAW)]
     */
    public static ArrayList getFreeIntervals(String table, String from, String to, String where) {
        ArrayList result = new ArrayList();

        String sql = "SELECT " + from + ", " + to + " FROM " + table + " WHERE " + where + " ORDER BY " + from;
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            if (rs.first()) {
                Date von = SYSConst.DATE_VON_ANFANG_AN;
                if (((Date) rs.getTimestamp(from)).equals(SYSConst.DATE_VON_ANFANG_AN)) {
                    // Wenn der erste Datensatz bereits mit VAA beginnt, dann beginnt der erste Zwischenraum, NACH diesem Intervall.
                    // Also unmittelbar nach "Bis".
                    von = SYSCalendar.addField(rs.getTimestamp(to), 1, GregorianCalendar.SECOND);
                } else {
                    // Wenn nicht, dann schieben wir den DB-Pointer wieder zurück,
                    rs.beforeFirst();
                }

                while (rs.next()) {
                    Date bis = SYSCalendar.addField(rs.getTimestamp(from), -1, GregorianCalendar.SECOND);
                    if (bis.after(von)) {
                        // Dieses if schließt aus, dass bei direkt aneinanderliegenden belegten Zeiträumen
                        // ein freier ermittelt wird, der gar nicht da ist.
                        Date[] date = {von, bis};
                        result.add(date);
                    }

                    // Hier beginnt schon die Vorbereitung für den nächsten Datensatz.
                    von = rs.getTimestamp(to);
                    if (von.before(SYSConst.DATE_BIS_AUF_WEITERES)) {
                        von = SYSCalendar.addField(von, +1, GregorianCalendar.SECOND);
                    }
                }

                // Massnahmen für das abschließende Intervall, dass evtl. mit BAW endet.
                Date[] date = (Date[]) result.get(result.size() - 1); // Letzten Eintrag nochmal holen.
                if (!date[1].equals(SYSConst.DATE_BIS_AUF_WEITERES)) {// Endet der mit BIS_AUF_WEITERES ??
                    // Nicht, dann hängen wir noch das abschließende Intervall an
                    Date[] lastInterval = {von, SYSConst.DATE_BIS_AUF_WEITERES};
                    result.add(lastInterval);
                }

            } else {
                Date[] date = {SYSConst.DATE_VON_ANFANG_AN, SYSConst.DATE_BIS_AUF_WEITERES};
                result.add(date);
            }
        } catch (SQLException ex) {
            // new DlgException(ex);
            result = null;
        }

        return result;
    }

    public static boolean isInFreeIntervals(ArrayList freeIntervals, Date from, Date to) {
        boolean yes = freeIntervals == null || freeIntervals.size() == 0;
        if (freeIntervals != null && freeIntervals.size() > 0 && from.compareTo(to) <= 0) { // Vorbedingung: from muss kleiner gleich to sein.
            Iterator it = freeIntervals.iterator();
            while (!yes && it.hasNext()) {
                Date[] intervall = (Date[]) it.next();
                Date fromInt = intervall[0];
                Date toInt = intervall[1];
                // Warum wird hier die Overlap Version genommen. Es könnte ja auch ein disjunctive Attribut sein ?
                // Ganz einfach. Die zu prüfenden Zeiträume wurden gemäß der Interval Art ausgewählt. Das heisst,
                // ob es ein disjunctive Attribut oder ein Overlap Attribut ist spielt keine Rolle. Die Liste der freien
                // Zeiträume wurde entsprechend erstellt. Sie berücksichtigt also schon ob disjunctive oder overlap.
                // Die Zeitraumkontrolle selbst muss immer die Ränder miteinbeziehen. Daher betweenOverlap().
                yes = SYSCalendar.betweenOverlap(fromInt, toInt, from, to);
            }
        }
        // Leere freeIntervals führen immer zu einem TRUE als Rückgabewert.
        return yes;
    }

    private static ArrayList getFreeIntervalsDisjunctive(String table, String from, String to, String where) {
        ArrayList result = new ArrayList();

        String sql = "SELECT " + from + ", " + to + " FROM " + table + " WHERE " + where + " ORDER BY " + from;
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            if (rs.first()) {
                Date von = SYSConst.DATE_VON_ANFANG_AN;
                if (((Date) rs.getDate(from)).equals(SYSConst.DATE_VON_ANFANG_AN)) {
                    // Wenn der erste Datensatz bereits mit VAA beginnt, dann beginnt der erste Zwischenraum, NACH diesem Intervall.
                    // Also einen Tag nach "to".
                    von = SYSCalendar.addDate(rs.getDate(to), +1);
                } else {
                    // Wenn nicht, dann schieben wir den Pointer wieder zurück,
                    rs.beforeFirst();
                }

                while (rs.next()) {
                    Date bis = SYSCalendar.addDate(rs.getDate(from), -1);
                    if (bis.after(von)) {
                        // Dieses if schließt aus, dass bei direkt aneinanderliegenden belegten Zeiträumen
                        // ein freier ermittelt wird, der gar nicht da ist.
                        Date[] date = {von, bis};
                        result.add(date);
                        OPDE.debug("DBRetrieve.getFreeIntervalsDisjunctive(): von =>" + SYSCalendar.printGermanStyle(von));
                        OPDE.debug("DBRetrieve.getFreeIntervalsDisjunctive(): bis =>" + SYSCalendar.printGermanStyle(bis));
                    }

                    // Hier beginnt schon die Vorbereitung für den nächsten Datensatz.
                    // Sollten die Zeiträume noch nicht mit BAW enden, dann rechnen wir einen Tag drauf.
                    // Da das ResultSet sortiert ist, kann das nur beim letzten Record auftreten.
                    von = rs.getDate(to);
                    if (von.before(SYSConst.DATE_BIS_AUF_WEITERES)) {
                        von = SYSCalendar.addDate(von, +1);
                    } else {
                        // Ist das Ende nach hinten offen, dann beginnt der neue freie Zeitraum
                        // einen Tag nach dem Von des aktuell belegten Zeitraums
                        // also belegt: 4.5.2007 - baw
                        // dann möglich zu belegen 5.5.2007 - baw
                        von = SYSCalendar.addDate(rs.getDate(from), +1);
                    }
                }
                Date[] date = (Date[]) result.get(result.size() - 1); // Letzten Eintrag nochmal holen.
                if (!date[1].equals(SYSConst.DATE_BIS_AUF_WEITERES)) {// Endet der mit BIS_AUF_WEITERES ??
                    // Nicht, dann hängen wir noch das abschließende Intervall an
                    Date[] lastInterval = {von, SYSConst.DATE_BIS_AUF_WEITERES};
                    result.add(lastInterval);
                    OPDE.debug("DBRetrieve.getFreeIntervalsDisjunctive(): von =>" + SYSCalendar.printGermanStyle(von));
                    OPDE.debug("DBRetrieve.getFreeIntervalsDisjunctive(): bis =>" + SYSCalendar.printGermanStyle(SYSConst.DATE_BIS_AUF_WEITERES));
                }
            } else {
                Date[] date = {SYSConst.DATE_VON_ANFANG_AN, SYSConst.DATE_BIS_AUF_WEITERES};
                result.add(date);
                OPDE.debug("DBRetrieve.getFreeIntervalsDisjunctive(): von =>" + SYSCalendar.printGermanStyle(SYSConst.DATE_VON_ANFANG_AN));
                OPDE.debug("DBRetrieve.getFreeIntervalsDisjunctive(): bis =>" + SYSCalendar.printGermanStyle(SYSConst.DATE_BIS_AUF_WEITERES));
            }
        } catch (SQLException ex) {
            // new DlgException(ex);
            result = null;
        }

        return result;
    }

    private static ArrayList getFreeIntervalsOverlap(String table, String from, String to, String where) {
        ArrayList result = new ArrayList();

        String sql = "SELECT " + from + ", " + to + " FROM " + table + " WHERE " + where + " ORDER BY " + from;
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            if (rs.first()) {
                Date von = SYSConst.DATE_VON_ANFANG_AN;
                if (((Date) rs.getDate(from)).equals(SYSConst.DATE_VON_ANFANG_AN)) {
                    // Wenn der erste Datensatz bereits mit VAA beginnt, dann beginnt der erste Zwischenraum, NACH diesem Intervall.
                    // Also einen Tag nach "to".
                    von = rs.getDate(to);
                } else {
                    // Wenn nicht, dann schieben wir den Pointer wieder zurück,
                    rs.beforeFirst();
                }

                while (rs.next()) {
                    Date bis = rs.getDate(from);
                    if (bis.after(von)) {
                        // Dieses if schließt aus, dass bei direkt aneinanderliegenden belegten Zeiträumen
                        // ein freier ermittelt wird, der gar nicht da ist.
                        Date[] date = {von, bis};
                        result.add(date);
                        OPDE.debug("DBRetrieve.getFreeIntervalsOverlap(): von =>" + SYSCalendar.printGermanStyle(von));
                        OPDE.debug("DBRetrieve.getFreeIntervalsOverlap(): bis =>" + SYSCalendar.printGermanStyle(bis));
                    }

                    // Hier beginnt schon die Vorbereitung für den nächsten Datensatz.
                    // Sollten die Zeiträume noch nicht mit BAW enden, dann rechnen wir einen Tag drauf.
                    // Da das ResultSet sortiert ist, kann das nur beim letzten Record auftreten.
                    if (SYSCalendar.sameDay(rs.getDate(to), SYSConst.DATE_BIS_AUF_WEITERES) == 0) {
                        // Ist das Ende nach hinten offen, dann beginnt der neue freie Zeitraum
                        // einen Tag nach dem Von des aktuell belegten Zeitraums
                        // also belegt: 4.5.2007 - baw
                        // dann möglich zu belegen 5.5.2007 - baw
                        von = SYSCalendar.addDate(rs.getDate(from), +1);
                    } else {
                        von = rs.getDate(to); // der potentielle neue Zeitraum beginnt mit dem Ende des bisherigen Zeitraums.
                    }
                }
                Date[] date = (Date[]) result.get(result.size() - 1); // Letzten Eintrag nochmal holen.
                if (SYSCalendar.sameDay(date[1], (SYSConst.DATE_BIS_AUF_WEITERES)) != 0) {// Endet der mit BIS_AUF_WEITERES ??
                    // Nicht, dann hängen wir noch das abschließende Intervall an
                    Date[] lastInterval = {von, SYSConst.DATE_BIS_AUF_WEITERES};
                    result.add(lastInterval);
                    OPDE.debug("DBRetrieve.getFreeIntervalsOverlap(): von =>" + SYSCalendar.printGermanStyle(von));
                    OPDE.debug("DBRetrieve.getFreeIntervalsOverlap(): bis =>" + SYSCalendar.printGermanStyle(SYSConst.DATE_BIS_AUF_WEITERES));
                }
            } else { // ist noch alles leer, dann kann man auch alles eintragen.
                Date[] date = {SYSConst.DATE_VON_ANFANG_AN, SYSConst.DATE_BIS_AUF_WEITERES};
                result.add(date);
                OPDE.debug("DBRetrieve.getFreeIntervalsOverlap(): von =>" + SYSCalendar.printGermanStyle(SYSConst.DATE_VON_ANFANG_AN));
                OPDE.debug("DBRetrieve.getFreeIntervalsOverlap(): bis =>" + SYSCalendar.printGermanStyle(SYSConst.DATE_BIS_AUF_WEITERES));
            }
        } catch (SQLException ex) {
            // new DlgException(ex);
            result = null;
        }

        return result;
    }

    private static ArrayList getFreeIntervalsNoConstraints() {
        ArrayList result = new ArrayList();
        Date[] date = {SYSConst.DATE_VON_ANFANG_AN, SYSConst.DATE_BIS_AUF_WEITERES};
        result.add(date);

        return result;
    }

    /**
     * Diese Methode berechnet zu einem PrimaryKey eines bekannten Zeitraums den Inhalt des vorhergehenden oder nachfolgenden Zeitraums aus der
     * BWInfo Tabelle.
     *
     * @param pk    des gegebenen Zeitraums.
     * @param true, wenn der vorhergehenden Zeitraum gesucht wird. false, wenn der nachfolgende gesucht wird.
     * @return Eine Liste mit den Daten des Zeitraums (long BWINFOID, Date von, Date bis, String xml). Null, wenn des keinen vorhergehenden Zeitraum gab oder bei Exception
     */
    public static ArrayList getAdjacentBWAttrib(long BWINFOID, boolean previous) {
        ArrayList result = null;
        // An den beiden Stellen unterscheidet sich der SQL Konstrukt.
        String op1 = (previous ? "MAX" : "MIN");
        String op2 = (previous ? "<" : ">");

        // Dieser SQL Konstrukt funktioniert nur, wenn die PKs streng monoton steigend verwendet wurden, da er
        // mit MAX(PK) bzw. MIN(PK) gearbeitet wird. Bei unseren AutoIncrement Tabellen ist das aber kein Problem.
        String sql = " SELECT BWINFOID, Von, Bis, XML FROM BWInfo WHERE BWINFOID = (" +
                " SELECT " + op1 + "(BWINFOID) FROM BWInfo " +
                " WHERE BWKennung=(SELECT BWKennung FROM BWInfo B WHERE BWINFOID=?) " +
                " AND BWINFTYP=(SELECT BWINFTYP FROM BWInfo B WHERE BWINFOID=?) " +
                " AND Von " + op2 + " (SELECT Von FROM BWInfo B WHERE BWINFOID=?)" +
                " )";
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setLong(1, BWINFOID);
            stmt.setLong(2, BWINFOID);
            stmt.setLong(3, BWINFOID);
            ResultSet rs = stmt.executeQuery();

            if (rs.first()) {
                result = new ArrayList();
                result.add(rs.getLong("BWINFOID"));
                result.add(rs.getDate("Von"));
                result.add(rs.getDate("Bis"));
                result.add(rs.getString("XML"));
            }

        } catch (SQLException ex) {
            // new DlgException(ex);
            result = null;
        }
        return result;
    }

    /**
     * @return Eine ArrayList aus Date[0..1] Arrays mit jeweils Von, Bis, die alle Heimaufenthalte des BW enthalten.
     */
    public static ArrayList getHauf(String bwkennung) {
        ArrayList result = new ArrayList();
        HashMap hm = new HashMap();
        hm.put("BWKennung", new Object[]{bwkennung, "="});
        hm.put("BWINFTYP", new Object[]{"hauf", "="});
//        hm.put("BWKennung", bwkennung);
//        hm.put("BWINFTYP", "hauf");
        ResultSet rs = getResultSet("BWInfo", new String[]{"Von", "Bis"}, hm, new String[]{"Von"});
        try {
            if (rs != null && rs.first()) {
                rs.beforeFirst();
                while (rs.next()) {
                    result.add(new Date[]{rs.getTimestamp("Von"), rs.getTimestamp("Bis")});
                }
                rs.close();
            }
        } catch (SQLException ex) {
            // new DlgException(ex);
            result = null;
        }
        return result;
    }

//    /**
//     * @return Eine ArrayList mit folgendem Inhalt (Zubereitung, AnwEinheit, PackEinheit, AnwText)
//     */
//    public static ArrayList getMPForm(long formid) {
//        ArrayList result = null;
//        String sql = "SELECT F.Zubereitung, F.AnwEinheit, F.PackEinheit, F.AnwText " +
//                "FROM MPFormen F  " +
//                "WHERE F.FormID = ?";
//        try {
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            stmt.setLong(1, formid);
//            ResultSet rs = stmt.executeQuery();
//            if (rs.first()) {
//                result = new ArrayList(4);
//                result.add(rs.getString("Zubereitung"));
//                result.add(rs.getInt("AnwEinheit"));
//                result.add(rs.getInt("PackEinheit"));
//                result.add(rs.getString("AnwText"));
//            }
//            rs.close();
//            stmt.close();
//        } catch (SQLException ex) {
//            // new DlgException(ex);
//            result = null;
//        }
//        return result;
//    }
//
//    public static DefaultComboBoxModel getMPFormen() {
//        DefaultComboBoxModel dcbmMassnahmen = new DefaultComboBoxModel();
//
//        PreparedStatement stmt;
//        ResultSet rs;
//
//        try {
//            String sql = "SELECT F.FormID, F.Zubereitung, F.AnwText, F.AnwEinheit, F.PackEinheit " +
//                    "FROM MPFormen F  " +
//                    "ORDER BY CONCAT(F.Zubereitung, F.AnwText)";
//            stmt = OPDE.getDb().db.prepareStatement(sql);
//            rs = stmt.executeQuery();
//
//            while (rs.next()) {
//                String text = "";
//                String zub = rs.getString("Zubereitung");
//                String anw = rs.getString("AnwText");
//                int anwein = rs.getInt("AnwEinheit");
//                if (SYSTools.catchNull(anw).equals("")) {
//                    anw = SYSConst.EINHEIT[anwein];
//                }
//
//                text = (SYSTools.catchNull(zub).equals("") ? anw : zub + ", " + anw);
//
//                ListElement le = new ListElement(text, rs.getLong("FormID"));
//                OPDE.debug("getMPFormen:>>" + text + ", " + rs.getLong("FormID"));
//                dcbmMassnahmen.addElement(le);
//            }
//        } // try
//        catch (SQLException se) {
//            // new DlgException(se);
//        } // catch
//
//        return dcbmMassnahmen;
//    }

    /**
     * Ermittelt den echten Benutzernamen zu einer UKennung
     *
     * @param ukennung
     * @return Realname
     */
    public static String getUsername(String ukennung) {
        //OPDE.info(ukennung);
        if (ukennung == null || ukennung.equals("")) {
            return "NULL";
        } else {
            HashMap hm = DBRetrieve.getSingleRecord("OCUsers", new String[]{"Nachname", "Vorname"}, "UKennung", ukennung);
            String result;
            if (OPDE.isAnonym()) {
                result = hm.get("Nachname").toString().substring(0, 1) + "***, " + hm.get("Vorname").toString().substring(0, 1) + "***";
            } else {
                result = hm.get("Nachname") + ", " + hm.get("Vorname");
            }
            //String result = hm.get("Nachname") + ", " + hm.get("Vorname");
            return result;
        }
    }

    /**
     * Gibt direkt eine HTML Beschreibung für eine bestimmte LoginID zurück.
     */
//    public static String identifyUser(long OCLoginID) {
//        String sql = " SELECT l.HOST, l.Login, p.V, l.UKennung " +
//                " FROM OCLogin l " +
//                " LEFT OUTER JOIN OCProps p ON l.IP = p.IP " +
//                " WHERE l.OCLoginID = ?";
//        String result = "Fehler";
//        try {
//            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
//            stmt.setLong(1, OCLoginID);
//            ResultSet rs = stmt.executeQuery();
//            if (rs.first()) {
//                SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
//                result = "<ul>";
//                result += "<li>Benutzer: <b>" + getUsername(rs.getString("UKennung")) + "</b></li>";
//                result += "<li>Computer: <b>" + rs.getString("HOST") + "</b></li>";
//                result += "<li>Angemeldet am: <b>" + formatter.format(rs.getTimestamp("Login")) + "</b></li>";
//                if (!SYSTools.catchNull(rs.getString("V")).equals("")) {
//                    result += "<li>Station: <b>" + rs.getString("V") + "</b></li>";
//                }
//                result += "</ul>";
//            } else {
//                result = "Keine Information vorhanden.";
//            }
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//            result = ex.getMessage();
//        }
//
//
//        return result;
//
//    }

//    public static String getEinrichtung2Station(String station) {
//        HashMap hm = new HashMap();
//        String ekennung = "";
//        hm.put("Stationen", new String[]{"%" + station + "%", "like"});
//        try {
//            ResultSet rs = getResultSet("Einrichtung", hm);
//
//            rs.first();
//            ekennung = rs.getString("EKennung");
//        } catch (SQLException ex) {
//            // new DlgException(ex);
//        }
//        return ekennung;
//    }
//
//    public static HashMap getEinrichtung() {
//        return getEinrichtung("");
//    }
//
//    public static HashMap getEinrichtung(String bwkennung) {
//        // In welcher Einrichtung wohnt der Bewohner ?
//        ArrayList al = null;
//        if (!SYSTools.catchNull(bwkennung).equals("")) {
//            al = op.tools.DBRetrieve.getLetztesBWAttribut(bwkennung, "station");
//        }
//        HashMap einrichtung = null;
//        if (al != null) {
//
//            String xmlc = al.get(0).toString();
//            xmlc = SYSTools.replace(xmlc, "<station value=", "");
//            String station = xmlc.replaceAll("[\\p{Punct}]", "");
//            HashMap hm = new HashMap();
//            hm.put("Stationen", new String[]{"%" + station + "%", "like"});
//            try {
//                ResultSet rs = getResultSet("Einrichtung", hm);
//
//                rs.first();
//                String ekennung = rs.getString("EKennung");
//
//                einrichtung = op.tools.DBRetrieve.getSingleRecord("Einrichtung", "Ekennung", ekennung);
//            } catch (SQLException ex) {
//                // new DlgException(ex);
//            }
//        } else {
//            einrichtung = op.tools.DBRetrieve.getSingleRecord("Einrichtung", "standard", true);
//        }
//        return einrichtung;
//    }
//
//    public static String createXMLStationListBEW(String bwkennung) {
//        HashMap e = getEinrichtung(bwkennung);
//        return createXMLStationListEINR(e.get("EKennung").toString());
//    }
//
//    public static String createXMLStationListEINR(String ekennung) {
//        String stationen = getSingleValue("Einrichtung", "Stationen", "EKennung", ekennung).toString();
//        StringTokenizer st = new StringTokenizer(stationen, ",");
//        String result = "";
//        while (st.hasMoreTokens()) {
//            result += "'<station value=\"" + st.nextToken() + "\"/>'";
//            result += (st.hasMoreTokens() ? "," : "");
//        }
//        return result;
//    }
}
