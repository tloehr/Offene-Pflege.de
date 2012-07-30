/*
 * OffenePflege
 * Copyright (C) 2011 Torsten Löhr
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

import java.sql.*;

@Deprecated
public class Database {


    public Connection db;


    public Database(String url, String username, char[] passwort)
            throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.db = DriverManager.getConnection(url, username, new String(passwort));

        } catch (ClassNotFoundException exCLASS) {
            OPDE.fatal(exCLASS);
        }

    } // Database()

    /**
     * Ermittelt die zuletzt den Primärschlüsser, des zuletzt eingefügten Datensatzes.
     *
     * @return neuer Primärschlüssel
     */
    public long getLastInsertedID() {
        long result = 0;
        try {
            Statement stmtPK = OPDE.getDb().db.createStatement();
            ResultSet rspk = stmtPK.executeQuery("SELECT LAST_INSERT_ID()");
            rspk.first();
            result = rspk.getLong(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return result;
    }

//    public void doLogout(){
//        doLogout("");
//    }
//
//    public void doLogout(String message){
//        message = SYSTools.catchNull(message);
//        HashMap hm = new HashMap();
//        hm.put("Finish","!NOW!");
//        DBHandling.updateRecord("OCWorkingOn",hm,"OCLoginID",OPDE.getLogin().getLoginID());
//        DBHandling.deleteRecords("OCMessage","Receiver",OPDE.getLogin().getLoginID());
//        String sqlLogout = "UPDATE OCLogin SET Logout=NOW() WHERE OCLoginID=?";
//        try {
//            PreparedStatement stmtLogout= db.prepareStatement(sqlLogout);
//            stmtLogout.setLong(1, OPDE.getLogin().getLoginID());
//            stmtLogout.executeUpdate();
//            OPDE.info("Abmeldung erfolgt: UKennung: "+OPDE.getLogin().getUser().getUKennung() + " Grund: "+message);
//            OPDE.setLogin(null);
//            //OPDE.UPW = SYSTools.cleanCharArray(OPDE.UPW);
//            //OPDE.lastlogout = 0l;
//        } catch (SQLException ex) {
//            OPDE.getLogger().error("doLogout fehlgeschlagen", ex);
//            System.exit(1);
//        }
//    }

    /**
     * Gibt eine eindeutige Nummer an den Aufrufer zurück. Die Nummer wird anhand der Datenbanktabelle UNIQUEID bestimmt.
     * Da führt das System über die vergebenen IDs buch. Können für alles mögliche benutzt werden wo eben globale, eindeutige Schlüssel
     * benöigt werden. Man kann auch einen prefix angeben. Dann führt die Methode in der Tabelle auch mehrere, getrennte Zähler.
     * <p/>
     * Der Standardzähler ist leer, also "".
     *
     * @return long   UID
     */
    public long getUID(String prefix) {
        long currentID = -1L;
        long newID = 0L;

        PreparedStatement stmt = null;
        ResultSet rs = null;

        // Solange versuchen, bis es klappt.
        while (currentID == -1L) {

            try {
                stmt = db.prepareStatement("select max(UID) from UNIQUEID where prefix = ?");
                stmt.setString(1, prefix);
                rs = stmt.executeQuery();
                rs.first();

                // Hier beginnt eine Transaktion
                boolean wasAutoCommit = db.getAutoCommit();
                db.setAutoCommit(false);
                db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                db.commit();
                currentID = rs.getLong(1);

                if (rs.getLong(1) == 0) { // für diesen prefix gibt es noch keinen Zähler. Es wird einer angelegt.
                    newID = 1L;
                } else {
                    newID = rs.getLong(1) + 1;
                }

                stmt = db.prepareStatement("INSERT UNIQUEID (UID, PREFIX) VALUES (?, ?)");
                stmt.setLong(1, newID);
                stmt.setString(2, prefix);
                stmt.executeUpdate();

                stmt = db.prepareStatement("DELETE FROM UNIQUEID WHERE UID=? AND PREFIX=?");
                stmt.setLong(1, currentID);
                stmt.setString(2, prefix);
                stmt.executeUpdate();

                db.commit();
                rs.close();
                stmt.close();

                db.setAutoCommit(wasAutoCommit);

            } catch (SQLException exc) {
                try {
                    db.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                exc.printStackTrace();
                currentID = -1L;
            } // try
        } // while()
        return newID;
    } // getUID()


}
