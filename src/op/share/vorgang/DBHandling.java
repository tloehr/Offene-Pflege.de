/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package op.share.vorgang;

import op.OPDE;
import op.tools.DBRetrieve;
import op.tools.DlgException;
import op.tools.SYSTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author tloehr
 */
public class DBHandling {

    //    public static final int ART_PFLEGE = 1;
//    public static final int ART_BHP = 2;
//    public static final int ART_SOZIAL = 3;
//    public static final int ART_VERWALTUNG = 4;
    public static final int MODE_ASSIGN = 0;
    public static final int MODE_REMOVE = 1;
    // Die VBerichte enthalten neben einfachen Texteinträgen auch automatische Systemnachrichten,
    // die den Entwicklungsverlauf eines Vorgangs wiederspiegeln. Alle VBerichte mit der Art > 0 sind
    // Systemberichte. Die man bei Bedarf wegfiltern kann.
    public static final int VBERICHT_ART_USER = 0;
    public static final int VBERICHT_ART_ASSIGN_ELEMENT = 1;
    public static final int VBERICHT_ART_REMOVE_ELEMENT = 2;
    public static final int VBERICHT_ART_SET_OWNERSHIP = 3;
    public static final int VBERICHT_ART_CREATE = 4;
    public static final int VBERICHT_ART_CLOSE = 5;
    public static final int VBERICHT_ART_REOPEN = 6;
    public static final int VBERICHT_ART_EDIT = 7;
    public static final int VBERICHT_ART_WV = 8;
    public static final String[] VBERICHT_ARTEN = {"Benutzerbericht", "SYS Zuordnung Element", "SYS Entfernung Element", "SYS Eigentümer geändert", "SYS Vorgang erstellt", "SYS Vorgang geschlossen", "SYS Vorgang wieder geöffnet", "SYS Vorgang bearbeitet", "SYS Wiedervorlage gesetzt"};

    /**
     * Erstellt eine Liste aktueller Vorgänge.
     *
     * @param bwkennung - bezieht auch die Vorgänge für einen bestimmten Bewohner ein. Kann auch "" oder null sein. Dann eben nur allgemeine Vorgänge.
     * @return verschachtelte ArrayListe mit den Ergebnissen zuerst Allgemein, dann für Bewohner geordnet.
     *         [ ("Allgemein",{"Spaziergänge",5},{"Waffeln backen 2008",7}),("Muster, Marga",{"Einzelgespräche",8},{"Gehübungen",VorgangID}) ]
     */
    public static ArrayList getVorgaenge(String bwkennung) {
        ArrayList vorgaenge = new ArrayList(); // Äußere Liste
        String sql = "" +
                " SELECT DISTINCT v.VorgangID, v.Titel, v.BWKennung, if(b.Nachname IS NULL, '', CONCAT(b.Nachname, ', ', b.Vorname)) name FROM Vorgang v " +
                " LEFT OUTER JOIN Bewohner b ON v.BWKennung = b.BWKennung" +
                " WHERE (" + (SYSTools.catchNull(bwkennung).equals("") ? "" : " v.BWKennung=? OR ") + " v.BWKennung='' ) " +
                " AND (v.Von <= now() AND v.Bis >= now())  " +
                " ORDER BY v.BWKennung, v.Titel ";
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            if (!SYSTools.catchNull(bwkennung).equals("")) {
                stmt.setString(1, bwkennung);
            }
            ResultSet rs = stmt.executeQuery();

            if (rs.first()) {
                rs.beforeFirst();
                String bwk = "";
                ArrayList gruppe = null; // umgibt die innerste Liste
                while (rs.next()) {
                    String currentBWK = SYSTools.catchNull(rs.getString("v.BWKennung"));
                    // Station hat gewechselt oder erster Record ?
                    if (rs.isFirst() || !bwk.equalsIgnoreCase(currentBWK)) {  // JA!
                        if (!rs.isFirst()) {
                            vorgaenge.add(gruppe);
                        }
                        bwk = currentBWK;
                        gruppe = new ArrayList();
                        gruppe.add(bwk.equals("") ? "Allgemein" : rs.getString("name"));
                    }
                    ArrayList vorgang = new ArrayList(2); // innerste Liste
                    vorgang.add(rs.getString("v.Titel"));
                    vorgang.add(rs.getLong("v.VorgangID"));
                    gruppe.add(vorgang);
                }
                vorgaenge.add(gruppe);
            }
        } catch (SQLException sQLException) {
            new DlgException(sQLException);
        }
        return vorgaenge;
    }

    public static String getBerichtAsHTML(long vbid) {
        String html = "";
        HashMap bericht = DBRetrieve.getSingleRecord("VBericht", "VBID", vbid);
        int art = ((Integer) bericht.get("Art")).intValue();
        html += "<b>Vorgangsbericht</b>";
        if (art > 0) {
            html += " <i>" + VBERICHT_ARTEN[art] + "</i>";
        }

        //String name = DBRetrieve.getUsername(bericht.get("UKennung").toString());
        html += "<p>" + bericht.get("Text").toString() + "</p>";
        return html;
    }

    public static void deleteVorgang(long vorgangid) {
        Connection db = OPDE.getDb().db;
        boolean doCommit = false;
        try {
            // Hier beginnt eine Transaktion, wenn es nicht schon eine gibt.
            if (db.getAutoCommit()) {
                db.setAutoCommit(false);
                db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                db.commit();
                doCommit = true;
            }

            String delete1 = "DELETE FROM Vorgaenge WHERE VorgangID = ? ";
            PreparedStatement stmtDel1 = OPDE.getDb().db.prepareStatement(delete1);
            stmtDel1.setLong(1, vorgangid);
            stmtDel1.executeUpdate();
            stmtDel1.close();

            String delete2 = "DELETE FROM VorgangAssign WHERE VorgangID = ? ";
            PreparedStatement stmtDel2 = OPDE.getDb().db.prepareStatement(delete2);
            stmtDel2.setLong(1, vorgangid);
            stmtDel2.executeUpdate();
            stmtDel2.close();

            String delete3 = "DELETE FROM VBericht WHERE VorgangID = ? ";
            PreparedStatement stmtDel3 = OPDE.getDb().db.prepareStatement(delete3);
            stmtDel3.setLong(1, vorgangid);
            stmtDel3.executeUpdate();
            stmtDel3.close();

            if (doCommit) {
                db.commit();
                db.setAutoCommit(true);
            }

        } catch (SQLException ex) {
            try {
                if (doCommit) {
                    db.rollback();
                }
            } catch (SQLException ex1) {
                new DlgException(ex1);
                ex1.printStackTrace();
                System.exit(1);
            }
            new DlgException(ex);
        }
    }

    private static JMenu getVorgaengeAssign(String tablename, long fk, String bwkennung, ActionListener al) {
        JMenu result = new JMenu("Zuordnen");
        final ActionListener f_al = al;
        try {
            String sql = "" +
                    " SELECT V.* " +
                    " FROM Vorgaenge V " +
                    " WHERE V.VorgangID NOT IN " +
                    " ( SELECT VorgangID FROM VorgangAssign WHERE TableName= ? AND ForeignKey = ? )" +
                    " AND BWKennung = ? AND Bis = '9999-12-31 23:59:59' ";
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setString(1, tablename);
            stmt.setLong(2, fk);
            stmt.setString(3, bwkennung);
            ResultSet rs = stmt.executeQuery();

            if (rs.first()) {

                rs.beforeFirst();
                while (rs.next()) {
                    JMenuItem mi = new JMenuItem(rs.getString("Titel"));
                    final HashMap hm = new HashMap();
                    hm.put("VorgangID", rs.getLong("vorgangid"));
                    hm.put("TableName", tablename);
                    hm.put("ForeignKey", fk);
                    hm.put("UKennung", OPDE.getLogin().getUser().getUKennung());
                    final long vorgangid = rs.getLong("vorgangid");
                    final long f = fk;
                    final String t = tablename;
                    mi.addActionListener(new java.awt.event.ActionListener() {

                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            if (op.tools.DBHandling.insertRecord("VorgangAssign", hm) > 0) {
                                newVBericht(vorgangid, "Neue Zuordnung wurde vorgenommen. TableName: " + t + "  Key: " + f, VBERICHT_ART_ASSIGN_ELEMENT);
                            }
                            f_al.actionPerformed(new ActionEvent(this, 0, "VorgangAssign"));
                        }
                    });
                    result.add(mi);
                }
            } else {
                result.setEnabled(false);
            }
        } catch (SQLException sQLException) {
            new DlgException(sQLException);
        }
        return result;
    }

    private static JMenu getVorgaengeRemove(String tablename, long fk, String bwkennung, ActionListener al) {
        JMenu result = new JMenu("Entfernen");
        final ActionListener f_al = al;
        try {
            String sql = "" +
                    " SELECT * " +
                    " FROM Vorgaenge V " +
                    " INNER JOIN VorgangAssign VA ON V.VorgangID = VA.VorgangID " +
                    " WHERE V.BWKennung = ? AND V.Bis = '9999-12-31 23:59:59' " +
                    " AND VA.TableName=? AND VA.ForeignKey = ? ";
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setString(1, bwkennung);
            stmt.setString(2, tablename);
            stmt.setLong(3, fk);

            ResultSet rs = stmt.executeQuery();

            if (rs.first()) {

                rs.beforeFirst();
                while (rs.next()) {
                    JMenuItem mi = new JMenuItem(rs.getString("V.Titel"));
                    final long vaid = rs.getLong("VA.VAID");
                    final long vorgangid = rs.getLong("V.VorgangID");
                    final long f = fk;
                    final String t = tablename;
                    mi.addActionListener(new java.awt.event.ActionListener() {

                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            if (op.tools.DBHandling.deleteRecords("VorgangAssign", "VAID", vaid) > 0) {
                                newVBericht(vorgangid, "Zuordnung wurde gelöscht. TableName: " + t + "   Key: " + f, VBERICHT_ART_REMOVE_ELEMENT);
                            }
                            f_al.actionPerformed(new ActionEvent(this, 0, "VorgangRemove"));
                        }
                    });
                    result.add(mi);
                }
            } else {
                result.setEnabled(false);
            }
        } catch (SQLException sQLException) {
            new DlgException(sQLException);
        }
        return result;
    }

    public static JMenu getVorgangContextMenu(Frame parent, String tablename, long fk, String bwk, ActionListener al) {
        JMenu menu = new JMenu("<html>Vorgänge <font color=\"red\">&#9679;</font></html>");
        JMenuItem neu = new JMenuItem("Neu erstellen");

        final Frame p = parent;
        final String[] b = new String[]{bwk};
        final String t = tablename;
        final long f = fk;
        final ActionListener f_al = al;
        neu.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DlgVorgang dlg = new DlgVorgang(p, b);
                if (dlg.getID() > 0) {
                    HashMap hm = new HashMap();
                    hm.put("TableName", t);
                    hm.put("ForeignKey", f);
                    hm.put("UKennung", OPDE.getLogin().getUser().getUKennung());
                    hm.put("VorgangID", dlg.getID());
                    // Den neuen Vorgang auch direkt zuordnen.
                    if (op.tools.DBHandling.insertRecord("VorgangAssign", hm) > 0) {
                        newVBericht(dlg.getID(), "Neue Zuordnung wurde vorgenommen. TableName: " + t + "   Key: " + f, VBERICHT_ART_ASSIGN_ELEMENT);
                    }
                    f_al.actionPerformed(new ActionEvent(this, 0, "Neuer Vorgang"));
                }
            }
        });
        menu.add(neu);

        // op.OCSec ocs = new op.OCSec();

        menu.add(getVorgaengeAssign(tablename, fk, bwk, al));
        menu.add(getVorgaengeRemove(tablename, fk, bwk, al));

        return menu;
    }

    public static long newVBericht(long vorgangid, String text, int art) {
        HashMap hm = new HashMap();
        hm.put("Datum", "!NOW!");
        hm.put("Text", text);
        hm.put("VorgangID", vorgangid);
        hm.put("Art", art);
        hm.put("UKennung", OPDE.getLogin().getUser().getUKennung());

        return op.tools.DBHandling.insertRecord("VBericht", hm);
    }

    /**
     * beendet den Vorgang
     *
     * @param vorgangid
     */
    public static void endVorgang(long vorgangid) {
        boolean doCommit = false;
        Connection db = OPDE.getDb().db;
        try {
            // Hier beginnt eine Transaktion, wenn es nicht schon eine gibt.
            if (db.getAutoCommit()) {
                db.setAutoCommit(false);
                db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                db.commit();
                doCommit = true;
            }

            HashMap hm = new HashMap();
            hm.put("Bis", "!NOW!");

            if (!op.tools.DBHandling.updateRecord("Vorgaenge", hm, "VorgangID", vorgangid)) {
                throw new SQLException("Vorgang konnte nicht geändert werden.");
            }

            if (DBHandling.newVBericht(vorgangid, "Vorgang abgeschlossen", DBHandling.VBERICHT_ART_CLOSE) < 0) {
                throw new SQLException("Neuer VBericht konnte nicht erstellt werden.");
            }

            if (doCommit) {
                db.commit();
                db.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            try {
                if (doCommit) {
                    db.rollback();
                }
                vorgangid = -1;
            } catch (SQLException ex1) {
                new DlgException(ex1);
                ex1.printStackTrace();
                System.exit(1);
            }
            new DlgException(ex);
        }
    }

    /**
     * Trägt den aktuellen OCUser als Besitzer ein.
     *
     * @param vorgangid
     */
    public static void takeVorgang(long vorgangid) {
        boolean doCommit = false;
        Connection db = OPDE.getDb().db;
        try {
            // Hier beginnt eine Transaktion, wenn es nicht schon eine gibt.
            if (db.getAutoCommit()) {
                db.setAutoCommit(false);
                db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                db.commit();
                doCommit = true;
            }

            HashMap hm = new HashMap();
            hm.put("Besitzer", OPDE.getLogin().getUser().getUKennung());

            if (!op.tools.DBHandling.updateRecord("Vorgaenge", hm, "VorgangID", vorgangid)) {
                throw new SQLException("Vorgang konnte nicht geändert werden.");
            }

            if (DBHandling.newVBericht(vorgangid, "Vorgang wurde zugewiesen an: " + DBRetrieve.getUsername(OPDE.getLogin().getUser().getUKennung()) + " (Übernahme)", DBHandling.VBERICHT_ART_SET_OWNERSHIP) < 0) {
                throw new SQLException("Neuer VBericht konnte nicht erstellt werden.");
            }

            if (doCommit) {
                db.commit();
                db.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            try {
                if (doCommit) {
                    db.rollback();
                }
                vorgangid = -1;
            } catch (SQLException ex1) {
                new DlgException(ex1);
                ex1.printStackTrace();
                System.exit(1);
            }
            new DlgException(ex);
        }
    }

    public static boolean hatVorgang(String bwkennung) {
        HashMap filter = new HashMap();
        filter.put("BWKennung", new Object[]{bwkennung, "="});
        filter.put("Bis", new Object[]{"9999-12-31 23:59:59", "="});
        ResultSet rs = op.tools.DBHandling.getResultSet("Vorgaenge", new String[]{"VorgangID"}, filter);
        return rs != null;
    }

    /**
     * aktiviert den Vorgang wieder
     *
     * @param vorgangid
     */
    public static void reopenVorgang(long vorgangid) {
        boolean doCommit = false;
        Connection db = OPDE.getDb().db;
        try {
            // Hier beginnt eine Transaktion, wenn es nicht schon eine gibt.
            if (db.getAutoCommit()) {
                db.setAutoCommit(false);
                db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                db.commit();
                doCommit = true;
            }

            HashMap hm = new HashMap();
            hm.put("Bis", "!BAW!");

            if (!op.tools.DBHandling.updateRecord("Vorgaenge", hm, "VorgangID", vorgangid)) {
                throw new SQLException("Vorgang konnte nicht geändert werden.");
            }

            if (DBHandling.newVBericht(vorgangid, "Vorgang wieder geöffnet", DBHandling.VBERICHT_ART_REOPEN) < 0) {
                throw new SQLException("Neuer VBericht konnte nicht erstellt werden.");
            }

            if (doCommit) {
                db.commit();
                db.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            try {
                if (doCommit) {
                    db.rollback();
                }
                vorgangid = -1;
            } catch (SQLException ex1) {
                new DlgException(ex1);
                ex1.printStackTrace();
                System.exit(1);
            }
            new DlgException(ex);
        }
    }

    /**
     * Setzt das Wiedervorlage Datum
     *
     * @param vorgangid
     * @param wv
     */
    public static void setWVVorgang(long vorgangid, Date wv) {
        boolean doCommit = false;
        Connection db = OPDE.getDb().db;
        try {
            // Hier beginnt eine Transaktion, wenn es nicht schon eine gibt.
            if (db.getAutoCommit()) {
                db.setAutoCommit(false);
                db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                db.commit();
                doCommit = true;
            }

            HashMap hm = new HashMap();
            hm.put("WV", wv);

            if (!op.tools.DBHandling.updateRecord("Vorgaenge", hm, "VorgangID", vorgangid)) {
                throw new SQLException("Vorgang konnte nicht geändert werden.");
            }

            DateFormat df = DateFormat.getDateInstance();
            if (DBHandling.newVBericht(vorgangid, "Wiedervorlage gesetzt auf: " + df.format(wv), DBHandling.VBERICHT_ART_WV) < 0) {
                throw new SQLException("Neuer VBericht konnte nicht erstellt werden.");
            }

            if (doCommit) {
                db.commit();
                db.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            try {
                if (doCommit) {
                    db.rollback();
                }
                vorgangid = -1;
            } catch (SQLException ex1) {
                new DlgException(ex1);
                ex1.printStackTrace();
                System.exit(1);
            }
            new DlgException(ex);
        }
    }

    /**
     * Entfernt einen Element aus dem Vorgang wieder.
     *
     * @param tblidx
     * @param elementid
     */
    public static void removeElement(int tblidx, long elementid) {
        boolean doCommit = false;
        Connection db = OPDE.getDb().db;
        try {
            // Hier beginnt eine Transaktion, wenn es nicht schon eine gibt.
            if (db.getAutoCommit()) {
                db.setAutoCommit(false);
                db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                db.commit();
                doCommit = true;
            }

            long vorgangid = 0;
            String bericht = "";

            if (tblidx == TMElement.TBL_VBERICHT) {
                HashMap vbericht = DBRetrieve.getSingleRecord("VBericht", "VBID", elementid);
                vorgangid = ((BigInteger) vbericht.get("VorgangID")).longValue();
                int art = ((Integer) vbericht.get("Art")).intValue();
                if (art > 0) {
                    throw new SQLException("Systemberichte können nicht gelöscht werden.");
                }
                if (op.tools.DBHandling.deleteRecords("VBericht", "VBID", elementid) < 0) {
                    throw new SQLException("Element konnte nicht gelöscht werden.");
                }
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");
                String ukennung = vbericht.get("UKennung").toString();
                bericht = "Vorgangsbericht gelöscht. Text war: '" + vbericht.get("Text").toString() +
                        "'<br/>Ursprünglich geschrieben von: " + DBRetrieve.getUsername(ukennung) +
                        "<br/>Am: " + sdf.format(vbericht.get("Datum"));
            } else {
                HashMap va = DBRetrieve.getSingleRecord("VorgangAssign", "VAID", elementid);
                if (op.tools.DBHandling.deleteRecords("VorgangAssign", "VAID", elementid) < 0) {
                    throw new SQLException("Element konnte nicht gelöscht werden.");
                }
                vorgangid = ((BigInteger) va.get("VorgangID")).longValue();
                bericht = "Element entfernt. Tabelle: " + va.get("TableName").toString() + ". PK: " + va.get("ForeignKey").toString();
            }

            if (DBHandling.newVBericht(vorgangid, bericht, DBHandling.VBERICHT_ART_REMOVE_ELEMENT) < 0) {
                throw new SQLException("Neuer VBericht konnte nicht erstellt werden.");
            }

            if (doCommit) {
                db.commit();
                db.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            try {
                if (doCommit) {
                    db.rollback();
                }
            } catch (SQLException ex1) {
                new DlgException(ex1);
                ex1.printStackTrace();
                System.exit(1);
            }
            new DlgException(ex);
        }
    }

    public static void endAllVorgaenge(String bwkennung) {

        String sql = "" +
                " SELECT v.VorgangID FROM Vorgaenge v " +
                " WHERE v.BWKennung=? " +
                " AND v.Von <= now() AND v.Bis >= now() ";
        try {
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setString(1, bwkennung);
            ResultSet rs = stmt.executeQuery();
            if (rs.first()) {
                rs.beforeFirst();
                while (rs.next()) {
                    endVorgang(rs.getLong("v.VorgangID"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBHandling.class.getName()).log(Level.SEVERE, null, ex);
        }


    }
}
