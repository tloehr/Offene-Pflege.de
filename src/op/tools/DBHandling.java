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
 */
package op.tools;

import op.OPDE;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author tloehr
 */
public class DBHandling {

//    public static DefaultComboBoxModel getDCMBMassnahmen() {
//        DefaultComboBoxModel dcbmMassnahmen = new DefaultComboBoxModel();
//
//        PreparedStatement stmt;
//        ResultSet rs;
//
//        try {
//            String sql = "SELECT MassID, Bezeichnung FROM oc.Massnahmen ORDER BY Bezeichnung";
//            stmt = OPDE.getDb().db.prepareStatement(sql);
//            rs = stmt.executeQuery();
//            rs.last();
//            System.out.println(rs.getRow());
//            rs.beforeFirst();
//            //int i = 0;
//
//            while (rs.next()) {
//                dcbmMassnahmen.addElement(new MassListElement(rs.getString(2), rs.getString(1)));
//            //massnahmenIDX.put(rs.getString(1), new Integer(i++)); // das brauche ich, damit ich die ComboBox je nach MassID richtig setzen kann.
//            }
//        } // try
//        catch (SQLException se) {
//            se.printStackTrace();
//        } // catch
//
//        return dcbmMassnahmen;
//    }
//   

//
//    public static DefaultListModel getLstMassnahmen(String order) {
//        DefaultListModel dcbmMassnahmen = new DefaultListModel();
//
//        PreparedStatement stmt;
//        ResultSet rs;
//
//        try {
//            String sql = "SELECT MassID, Bezeichnung FROM oc.Massnahmen WHERE Sortierung > 0 ORDER BY " + order;
//            stmt = OPDE.getDb().db.prepareStatement(sql);
//            rs = stmt.executeQuery();
//            rs.last();
//            System.out.println(rs.getRow());
//            rs.beforeFirst();
//            //int i = 0;
//
//            while (rs.next()) {
//                dcbmMassnahmen.addElement(new MassListElement(rs.getString(2), rs.getString(1)));
//            //massnahmenIDX.put(rs.getString(1), new Integer(i++)); // das brauche ich, damit ich die ComboBox je nach MassID richtig setzen kann.
//            }
//        } // try
//        catch (SQLException se) {
//            se.printStackTrace();
//        } // catch
//
//        return dcbmMassnahmen;
//    }

    /**
     * Lädt alle Properties aus der Tabelle OCProps ein, die nicht an der aktuellen Userkennung hängen.
     * Passend zu einer IP bzw. IP='*', wenn die Properties für alle gedacht sind.
     *
     * @param ip String mit der IP-Adresse oder '*'
     * @return Ergebnis in einem Properties Objekt.
     */
//    public static Properties getOCProps(String ip) {
//        return getOCProps(ip, false);
//
//    }
//
//    public static void putOCProps(String k, String v, String ip) {
//        HashMap hm = new HashMap();
//        hm.put("K", new Object[]{k, "="});
//        hm.put("UKennung", new Object[]{OPDE.getLogin().getUser().getUKennung(), "="});
//        hm.put("IP", new Object[]{ip, "="});
//        long ocpid = ((BigInteger) DBRetrieve.getSingleValue("OCProps", "OCPID", hm)).longValue();
//        hm.clear();
//        hm.put("V", v);
//        if (ocpid > 0) {
//            updateRecord("OCProps", hm, "OCPID", ocpid);
//        } else {
//            hm.put("K", k);
//            hm.put("IP", ip);
//            hm.put("UKennung", OPDE.getLogin().getUser().getUKennung());
//            insertRecord("OCProps", hm);
//        }
//        OPDE.getProps().put(k, v);
//        hm.clear();
//    }

//    /**
//     * Lädt Properties aus der Tabelle OCProps ein.
//     * Passend zu einer IP bzw. IP='*', wenn die Properties für alle gedacht sind.
//     * @param ip String mit der IP-Adresse oder '*'
//     * @param only4me, true, dann werden nur die Properties geladen, die zu der aktuellen Userkennung passen. false, alle.
//     * @return Ergebnis in einem Properties Objekt.
//     *
//     */
//    public static Properties getOCProps(String ip, boolean only4me) {
//        Properties p = new Properties();
//
//        PreparedStatement stmt;
//        ResultSet rs;
//
//        try {
//            String sql = "SELECT K, V FROM OCProps WHERE IP = ? ";
//            sql += (only4me ? " AND UKennung = ? " : " AND (UKennung = '' OR UKennung IS NULL) ");
//            stmt = OPDE.getDb().db.prepareStatement(sql);
//            stmt.setString(1, ip);
//            if (only4me) {
//                stmt.setString(2, OPDE.getLogin().getUser().getUKennung());
//            }
//            rs = stmt.executeQuery();
//            OPDE.getLogger().debug("getOCProps/2: " + sql);
//            OPDE.getLogger().debug("getOCProps/2: ip:" + ip);
//            OPDE.getLogger().debug("getOCProps/2: only4me:" + only4me);
//            while (rs.next()) {
//                p.put(rs.getString("K"), rs.getString("V"));
//                OPDE.getLogger().debug("getOCProps/2: " + rs.getString("K") + " ==> " + rs.getString("V"));
//            }
//        } // try
//        catch (SQLException se) {
//            se.printStackTrace();
//            System.exit(1);
//        } // catch
//
//        return p;
//    }
    public static HashMap getBW(String bwkennung) {
        HashMap hm = new HashMap();

        // Bewohnernamen in den Label schreiben.
        try {
            String sql = ""
                    + " SELECT b.nachname, b.vorname, b.GebDatum, b.Geschlecht "
                    + " FROM Bewohner b "
                    + " WHERE b.BWKennung = ?";

            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setString(1, bwkennung);
            ResultSet rs = stmt.executeQuery();
            rs.first();
            String vorname;
            String nachname;
            Date gebdatum;

            if (OPDE.isAnonym()) {
                String nachnameErsterBuchstabe = rs.getString("b.nachname").toLowerCase().substring(0, 1);
                String vornameErsterBuchstabe = rs.getString("b.vorname").toLowerCase().substring(0, 1);
                //String letzterBuchstabe = in.get("nachname").toString().toLowerCase().substring(in.get("nachname").toString().length()-1, in.get("nachname").toString().length());
                gebdatum = SYSTools.anonymizeDate(rs.getDate("b.GebDatum"));

                //int random = letzterBuchstabe.codePointAt(0) % 5;
                int random1 = rs.getString("b.nachname").toString().toLowerCase().charAt(1) % 5;
                int random2 = rs.getString("b.vorname").toString().toLowerCase().charAt(1) % 5;
                nachname = ((String[]) OPDE.anonymize[SYSTools.INDEX_NACHNAME].get(nachnameErsterBuchstabe))[random1];

                if (rs.getInt("Geschlecht") == 2) {
                    vorname = ((String[]) OPDE.anonymize[SYSTools.INDEX_VORNAME_FRAU].get(vornameErsterBuchstabe))[random2];
                } else {
                    vorname = ((String[]) OPDE.anonymize[SYSTools.INDEX_VORNAME_MANN].get(vornameErsterBuchstabe))[random2];
                }
            } else {
                vorname = rs.getString("b.vorname");
                nachname = rs.getString("b.nachname");
                gebdatum = rs.getDate("b.GebDatum");
            }

            hm.put("vorname", vorname);
            hm.put("nachname", nachname);
            hm.put("bwkennung", bwkennung);
            hm.put("gebdatum", gebdatum);
            hm.put("geschlecht", (rs.getInt("Geschlecht") == 2 ? "weiblich" : "männlich"));

        } catch (SQLException ex) {
            new DlgException(ex);
        }

        return hm;
    }


    public static Object getSingleValue(String table, String field, String wherefield, Object wherevalue) {
        HashMap where = new HashMap();
        where.put(wherefield, new Object[]{wherevalue, "="});
        return getSingleValue(table, field, where);
    }

    /**
     * Fügt einen neuen Datensatz in eine Tabelle ein.
     *
     * @param table Name der Datenbanktabelle
     * @param data  HashMap mit Paaren aus Spaltenname und Spaltenwert, z.B. ("Nachname", "Löhr") oder ("PLZ", 53783) etc.
     * @return der PrimaryKey des neuen Records. Geht immer von einem Long als PK aus. -1, bei Exception.
     */
    public static long insertRecord(String table, HashMap data) {
        PreparedStatement stmt;
        long result = -1;

        String cols = "(";
        String values = "(";
        ArrayList val = new ArrayList();

        // Zuerst die Columns eintragen
        Iterator it = data.entrySet().iterator();
        int l = 0;
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String col = (String) entry.getKey();
            cols += col;
            Object o = entry.getValue();
            if (o != null && o.toString().equalsIgnoreCase("!NOW!")) { // Sonderfall für mysql Datenbankfunktion now()
                values += "now()";
            } else if (o != null && o.toString().equalsIgnoreCase("!BAW!")) { // Sonderfall für mysql Datenbankfunktion now()
                values += SYSConst.MYSQL_DATETIME_BIS_AUF_WEITERES;
            } else if (o != null && o.toString().equalsIgnoreCase("!VAA!")) { // Sonderfall für mysql Datenbankfunktion now()
                values += SYSConst.MYSQL_DATETIME_VON_ANFANG_AN;
            } else if (o != null && o.toString().equalsIgnoreCase("!NOW+1!")) { // Sonderfall für mysql Datenbankfunktion now()
                values += "DATE_ADD(now(),INTERVAL 1 SECOND)";
            } else {
                OPDE.getLogger().debug(l++ + ": " + col);
                val.add(o); // value, für spätere setObject Schleife
                values += "?";
            }
            if (it.hasNext()) {
                cols += ",";
                values += ",";
            }
        }

        cols += ")";
        values += ")";

        String sql = "INSERT INTO " + table + " " + cols + " VALUES " + values;
        OPDE.getLogger().debug("DBHandling.insertRecord: " + sql);

        try {
            stmt = OPDE.getDb().db.prepareStatement(sql);
            for (int i = 0; i < val.size(); i++) {
                OPDE.getLogger().debug(i);
                stmt.setObject(i + 1, val.get(i));
                //System.out.println(whereval.get(i));
            }
            stmt.executeUpdate();
            result = OPDE.getDb().getLastInsertedID();

        } catch (SQLException ex) {
            new DlgException(ex);
            result = -1;
        }
        return result;
    }

    /**
     * Ändert einen Datensatz
     *
     * @param table Name der Datenbanktabelle
     * @param data  HashMap mit Paaren aus Spaltenname und Spaltenwert, z.B. ("Nachname", "Löhr") oder ("PLZ", 53783) etc.
     * @param long  Primärschlüssel des zu ändernden Datensatzes.
     * @return boolean, ob die Operation erfolgreich war.
     */
    public static boolean updateRecord(String table, HashMap data, String pkname, Object pk) {
        PreparedStatement stmt;
        boolean result = false;

        ArrayList val = new ArrayList();
        String sql = "UPDATE " + table + " SET ";

        // Zuerst die Columns eintragen
        Iterator it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String col = (String) entry.getKey();
            sql += col;
            Object o = entry.getValue();
            if (o != null && o.toString().equalsIgnoreCase("!NOW!")) { // Sonderfall für mysql Datenbankfunktion now()
                sql += "=now()";
            } else if (o != null && o.toString().equalsIgnoreCase("!BAW!")) { // Sonderfall für mysql Datenbankfunktion now()
                sql += "=" + SYSConst.MYSQL_DATETIME_BIS_AUF_WEITERES;
            } else if (o != null && o.toString().equalsIgnoreCase("!VAA!")) { // Sonderfall für mysql Datenbankfunktion now()
                sql += "=" + SYSConst.MYSQL_DATETIME_VON_ANFANG_AN;
            } else if (o != null && o.toString().equalsIgnoreCase("!NOW+1!")) { // Sonderfall für mysql Datenbankfunktion now()
                sql += "=DATE_ADD(now(),INTERVAL 1 SECOND)";
            } else {
                val.add(o); // value, für spätere setObject Schleife
                sql += "=?";
            }
            if (it.hasNext()) {
                sql += ",";
            }
        }

        sql += " WHERE " + pkname + " = ?";

        OPDE.getLogger().debug("DBHandling.updatingRecord: " + sql);

        try {
            stmt = OPDE.getDb().db.prepareStatement(sql);
            for (int i = 0; i < val.size(); i++) {
                stmt.setObject(i + 1, val.get(i));
            }
            stmt.setObject(val.size() + 1, pk);
            result = stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            new DlgException(ex);
            result = false;
        }
        return result;
    }

    /**
     * Ändert einen Datensatz
     *
     * @param table Name der Datenbanktabelle
     * @param data  HashMap mit Paaren aus Spaltenname und Spaltenwert, z.B. ("Nachname", "Löhr") oder ("PLZ", 53783) etc.
     * @param long  Primärschlüssel des zu ändernden Datensatzes.
     * @return boolean, ob die Operation erfolgreich war.
     */
    public static boolean updateRecord(String table, HashMap data, String pkname, long pk) {
        PreparedStatement stmt;
        boolean result = false;

        ArrayList val = new ArrayList();
        String sql = "UPDATE " + table + " SET ";

        // Zuerst die Columns eintragen
        Iterator it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String col = (String) entry.getKey();
            sql += col;
            Object o = entry.getValue();
            if (o != null && o.toString().equalsIgnoreCase("!NOW!")) { // Sonderfall für mysql Datenbankfunktion now()
                sql += "=now()";
            } else if (o != null && o.toString().equalsIgnoreCase("!BAW!")) { // Sonderfall für mysql Datenbankfunktion now()
                sql += "=" + SYSConst.MYSQL_DATETIME_BIS_AUF_WEITERES;
            } else if (o != null && o.toString().equalsIgnoreCase("!VAA!")) { // Sonderfall für mysql Datenbankfunktion now()
                sql += "=" + SYSConst.MYSQL_DATETIME_VON_ANFANG_AN;
            } else if (o != null && o.toString().equalsIgnoreCase("!NOW+1!")) { // Sonderfall für mysql Datenbankfunktion now()
                sql += "=DATE_ADD(now(),INTERVAL 1 SECOND)";
            } else {
                val.add(o); // value, für spätere setObject Schleife
                sql += "=?";
            }

            if (it.hasNext()) {
                sql += ",";
            }
        }

        sql += " WHERE " + pkname + " = ?";

        OPDE.getLogger().debug("DBHandling.updatingRecord: " + sql);

        try {
            stmt = OPDE.getDb().db.prepareStatement(sql);
            for (int i = 0; i < val.size(); i++) {
                stmt.setObject(i + 1, val.get(i));
            }
            stmt.setLong(val.size() + 1, pk);
            result = stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            new DlgException(ex);
            result = false;
        }
        return result;
    }

    public static int deleteRecords(String table, String wherefield, Object wherevalue) {
        HashMap where = new HashMap();
        where.put(wherefield, new Object[]{wherevalue, "="});
        return deleteRecords(table, where);
    }

    /**
     * Löscht Records aus der Datenbank.
     *
     * @return anzahl der gelöschten Datensätze. -1 bei Fehler.
     */
    public static int deleteRecords(String table, HashMap where) {
        PreparedStatement stmt;
        int deletedRows = -1;

        String sql = "DELETE FROM " + table + " WHERE ";
        //+ wherefield + " = ?";

        ArrayList whereval = new ArrayList();

        Iterator it = where.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String wherefield = (String) entry.getKey();
            Object[] os = (Object[]) entry.getValue();
            whereval.add(os[0]); // value
            sql += wherefield + os[1] + "?"; // enthält den operator
            if (it.hasNext()) {
                sql += " AND ";
            }
        }

        try {
            stmt = OPDE.getDb().db.prepareStatement(sql);
            //System.out.println(sql);
            for (int i = 0; i < whereval.size(); i++) {
                stmt.setObject(i + 1, whereval.get(i));
                //System.out.println(whereval.get(i));
            }

            deletedRows = stmt.executeUpdate();
        } catch (SQLException ex) {
            deletedRows = -1;
            new DlgException(ex);
            ex.printStackTrace();
        }
        return deletedRows;
    }

    public static Object getSingleValue(String table, String field, HashMap where) {
        PreparedStatement stmt;
        Object o = null;

        String sql = "SELECT " + field + " FROM " + table + " WHERE ";
        //+ wherefield + " = ?";

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

        try {
            stmt = OPDE.getDb().db.prepareStatement(sql);
            //System.out.println(sql);
            for (int i = 0; i < whereval.size(); i++) {
                stmt.setObject(i + 1, whereval.get(i));
                //System.out.println(whereval.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.first()) {
                o = rs.getObject(1);
            }
        } catch (SQLException ex) {
            new DlgException(ex);
            ex.printStackTrace();
        }
        return o;
    }

    public static ResultSet getResultSet(String table, String[] fields, String wherefield, Object wherevalue, String comparisonOperator) {
        HashMap where = new HashMap();
        where.put(wherefield, new Object[]{wherevalue, comparisonOperator});
        return getResultSet(table, fields, where, null);
    }

    public static ResultSet getResultSet(String table, String[] fields) {
        return getResultSet(table, fields, null, null);
    }

    public static ResultSet getResultSet(String table, String[] fields, HashMap where) {
        return getResultSet(table, fields, where, null);
    }

    public static ResultSet getResultSet(String table, String[] fields, HashMap where, String[] order) {
        PreparedStatement stmt;
        ResultSet rs = null;

        String sql = " SELECT ";

        for (int i = 0; i < fields.length; i++) {
            sql += fields[i];
            if (i + 1 < fields.length) {
                sql += ",";
            }
        }

        sql += " FROM " + table;

        ArrayList whereval = new ArrayList();
        // FILTER------------
        if (where != null && where.size() > 0) {
            sql += " WHERE ";

            Iterator it = where.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                String wherefield = (String) entry.getKey();
                Object[] os = (Object[]) entry.getValue();
                //whereval.add(entry.getValue());
                whereval.add(os[0]); // value
                sql += wherefield + " " + os[1] + " ?"; // enthält den operator
                //sql += wherefield + " "+comparisonOperator+" ?";
                if (it.hasNext()) {
                    sql += " AND ";
                }
            }
        }

        // Sortierung ORDER -------------------------
        if (order != null && order.length > 0) {
            sql += " ORDER BY ";
            for (int i = 0; i < order.length; i++) {
                sql += order[i];
                if (i + 1 < order.length) {
                    sql += ",";
                }
            }
        }

        try {
            stmt = OPDE.getDb().db.prepareStatement(sql);

            // FILTER------------
            if (where != null) {
                for (int i = 0; i < whereval.size(); i++) {
                    stmt.setObject(i + 1, whereval.get(i));
                }
            }

            ResultSet rs1 = stmt.executeQuery();
            if (rs1.first()) {
                rs = rs1;
            }
        } catch (SQLException ex) {
            new DlgException(ex);
            ex.printStackTrace();
        }
        return rs;

    }

    /**
     * Diese Methode liefert exact den SQL Ausdruck zurück, der in getResultSet verwendet wird um
     * dort die Datenbank Anfrage zu formulieren.
     *
     * @param table
     * @param fields
     * @param where
     * @param order
     * @return
     */
    public static String getSQLStatement(String table, String[] fields, HashMap where, String[] order) {
        String sql = " SELECT ";

        for (int i = 0; i < fields.length; i++) {
            sql += fields[i];
            if (i + 1 < fields.length) {
                sql += ",";
            }
        }

        sql += " FROM " + table;

        // FILTER------------
        if (where != null && where.size() > 0) {
            sql += " WHERE ";

            Iterator it = where.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                String wherefield = (String) entry.getKey();
                Object[] os = (Object[]) entry.getValue();
                //whereval.add(entry.getValue());
                sql += wherefield + " " + os[1] + " ?"; // enthält den operator
                //sql += wherefield + " "+comparisonOperator+" ?";
                if (it.hasNext()) {
                    sql += " AND ";
                }
            }
        }

        // Sortierung ORDER -------------------------
        if (order != null && order.length > 0) {
            sql += " ORDER BY ";
            for (int i = 0; i < order.length; i++) {
                sql += order[i];
                if (i + 1 < order.length) {
                    sql += ",";
                }
            }
        }

        return sql;

    }

    /**
     * @param pattern
     * @return
     */
    public static String createSearchPattern(String pattern) {
        pattern = pattern.replaceAll("%", "");
        pattern = "%" + pattern + "%";
        return pattern;
    }

    public static ArrayList getGroups(String ukennung) {
        ArrayList result = new ArrayList();
        try {
            ResultSet rs = DBRetrieve.getResultSet("OCMember", new String[]{"GKennung"}, "UKennung", ukennung, "=");
            while (rs.next()) {
                result.add(rs.getString("GKennung"));
            }
        } catch (SQLException sQLException) {
        }
        return result;
    }

    /**
     * Erstellt eine Liste der Bewohner zum angegebenen Zeitpunkt
     *
     * @param date - gewünschter Zeitraum
     * @return verschachtelte ArrayListe mit den Ergebnissen nach Station geordnet.
     *         (("Station1",("Mustermann, Max","MM4"),("Muster, Marga","MM5")),("Station2",("...","BA1")))
     */
    public static ArrayList getBWList(Date date) {
        ArrayList result = new ArrayList();

        String sql = "" +
                " SELECT CONCAT(b.nachname,', ',b.vorname) name, b.bwkennung, ba2.XML " +
                " FROM Bewohner b " +
                " INNER JOIN BWInfo ba ON b.BWKennung = ba.BWKennung " +
                " LEFT OUTER JOIN BWInfo ba2 ON b.BWKennung = ba2.BWKennung " +
                " WHERE ba.BWINFTYP = 'hauf' AND " +
                " ba.von <= ? AND ba.bis >= ? AND " +
                " b.AdminOnly <> 2 AND ba2.BWINFTYP = 'STATION' AND " +
                " ba2.von <= ? AND ba2.bis >= ? " +
                " ORDER BY XML, name ";
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setDate(1, new java.sql.Date(date.getTime()));
            stmt.setDate(2, new java.sql.Date(date.getTime()));
            stmt.setDate(3, new java.sql.Date(date.getTime()));
            stmt.setDate(4, new java.sql.Date(date.getTime()));
            ResultSet rs = stmt.executeQuery();

            if (rs.first()) {
                String xml = rs.getString("ba2.XML");
                ArrayList station = new ArrayList();
                StringTokenizer st1 = new StringTokenizer(xml, "\"");
                st1.nextToken(); // den ersten nicht.
                station.add(st1.nextToken());
                rs.beforeFirst();
                while (rs.next()) {
                    String currentXML = rs.getString("ba2.xml");
                    // Station hat gewechselt ?
                    if (!xml.equalsIgnoreCase(currentXML)) {  // JA!
                        xml = currentXML;
                        result.add(station);
                        station = new ArrayList();

                        StringTokenizer st = new StringTokenizer(xml, "\"");
                        st.nextToken(); // den ersten nicht.
                        station.add(st.nextToken());

                    }
                    ArrayList bw = new ArrayList();
                    bw.add(rs.getString("name"));
                    bw.add(rs.getString("bwkennung"));
                    station.add(bw);
                }
                result.add(station);
            }

        } catch (SQLException sQLException) {
        }
        return result;
    }
}

