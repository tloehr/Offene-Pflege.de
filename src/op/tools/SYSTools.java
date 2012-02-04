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
package op.tools;

import entity.system.SYSPropsTools;
import op.OPDE;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.callback.TimelineCallback;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;
import org.pushingpixels.trident.ease.Spline;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.List;

public class SYSTools {

    public static final int INDEX_NACHNAME = 0;
    public static final int INDEX_VORNAME_FRAU = 1;
    public static final int INDEX_VORNAME_MANN = 2;

    public static final boolean LEFT_UPPER_SIDE = false;
    public static final boolean RIGHT_LOWER_SIDE = true;

    public static final boolean MUST_BE_POSITIVE = true;

    /**
     *
     */
    public static void center(java.awt.Window w) {
        Dimension us = w.getSize();
        Dimension them = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int newX = (them.width - us.width) / 2;
        int newY = (them.height - us.height) / 2;
        w.setLocation(newX, newY);
    } // center

    public static void centerOnParent(Component parent, Component child) {

        Dimension dimParent = parent.getSize();
        Dimension dimChild = child.getSize();


        //Dimension them = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int newX = (dimParent.width - dimChild.width) / 2;
        int newY = (dimParent.height - dimChild.height) / 2;
        newX += parent.getX();
        newY += parent.getY();
        child.setLocation(newX, newY);
    }

    public static void centerOnParent(Component comp) {
        centerOnParent(comp.getParent(), comp);
    }

    public static double roundScale2(double d) {
        return Math.rint(d * 100) / 100.;
    }

    public static String roundScale2(BigDecimal bd) {
        return bd.setScale(2, BigDecimal.ROUND_HALF_EVEN).toPlainString();
    }


    /**
     * läuft rekursiv durch alle Kinder eines Containers und setzt deren Enabled Status auf
     * enabled.
     */
    public static void setXEnabled(JComponent container, boolean enabled) {
        // Bei einer Combobox muss die Rekursion ebenfalls enden.
        // Sie besteht aus weiteren Unterkomponenten
        // "disabled" wird sie aber bereits hier.
        if (container.getComponentCount() == 0 || container instanceof JComboBox) {
            // Rekursionsanker
            container.setEnabled(enabled);
        } else {
            Component[] c = container.getComponents();
            for (int i = 0; i < c.length; i++) {
                if (c[i] instanceof JComponent) {
                    JComponent jc = (JComponent) c[i];
                    setXEnabled(jc, enabled);
                }
            }
        }
    }

    /**
     * läuft rekursiv durch alle Kinder eines Containers und entfernt evtl. vorhandene Listener.
     */
    public static void unregisterListeners(JComponent container) {
        if (container == null) {
            return;
        }
        removeListeners(container);
        if (container.getComponentCount() > 0) {
            Component[] c = container.getComponents();
            for (int i = 0; i < c.length; i++) {
                if (c[i] instanceof JComponent) {
                    unregisterListeners((JComponent) c[i]);
                }
            }
        }
    }

    /**
     * läuft rekursiv durch alle Kinder eines JFrames und entfernt evtl. vorhandene Listener.
     */
    public static void unregisterListeners(JDialog container) {
        if (container == null) {
            return;
        }
        if (container.getComponentCount() > 0) {
            Component[] c = container.getComponents();
            for (int i = 0; i < c.length; i++) {
                if (c[i] instanceof JComponent) {
                    unregisterListeners((JComponent) c[i]);
                }
            }
        }
    }

    /**
     * läuft rekursiv durch alle Kinder eines JFrames und entfernt evtl. vorhandene Listener.
     */
    public static void unregisterListeners(JFrame container) {
        if (container == null) {
            return;
        }
        if (container.getComponentCount() > 0) {
            Component[] c = container.getComponents();
            for (int i = 0; i < c.length; i++) {
                if (c[i] instanceof JComponent) {
                    unregisterListeners((JComponent) c[i]);
                }
            }
        }
    }

    /**
     * Tauscht Zeichen in einem String in bester Textverarbeitungsmanier ;-)<br/>
     * <b>Beispiel:</b> replace("AABBCC", "BB", "DD") = "AADDCC"
     *
     * @param str     - Eingang
     * @param pattern - Muster nach dem gesucht werden soll
     * @param replace - Ersatzzeichenkette
     * @return String mit Ersetzung
     */
    public static String replace(String str, String pattern, String replace) {
        int s = 0;
        int e = 0;
        StringBuffer result = new StringBuffer();

        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(replace);
            s = e + pattern.length();
        }
        result.append(str.substring(s));
        return result.toString();
    }

    /**
     * see: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4380536
     * Puh, das hier ist aus der Sun Bug Datenbank. Etwas krude... Ich hoffe
     * die lassen sich mal was besseres einfallen.
     */
    static private void removeListeners(Component comp) {
        Method[] methods = comp.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            String name = method.getName();
            if (name.startsWith("remove") && name.endsWith("Listener")) {

                Class[] params = method.getParameterTypes();
                if (params.length == 1) {
                    EventListener[] listeners = null;
                    try {
                        listeners = comp.getListeners(params[0]);
                    } catch (Exception e) {
                        // It is possible that someone could create a listener
                        // that doesn't extend from EventListener.  If so,
                        // ignore it
                        OPDE.debug("Listener " + params[0] + " does not extend EventListener");
                        continue;
                    }
                    for (int j = 0; j < listeners.length; j++) {
                        try {
                            method.invoke(comp, new Object[]{listeners[j]});
                            //OPDE.debug("removed Listener " + name + "for comp " + comp + "\n");
                        } catch (Exception e) {
                            OPDE.debug("Cannot invoke removeListener method " + e);
                            // Continue on.  The reason for removing all listeners is to
                            // make sure that we don't have a listener holding on to something
                            // which will keep it from being garbage collected. We want to
                            // continue freeing listeners to make sure we can free as much
                            // memory has possible
                        }
                    }
                } else {
                    // The only Listener method that I know of that has more
                    // one argument is removePropertyChangeListener.  If it is
                    // something other than that, flag it and move on.
                    if (!name.equals("removePropertyChangeListener")) {
                        OPDE.debug("    Wrong number of Args " + name);
                    }
                }
            }
        }
    }

//    public static void setBWLabel(JLabel bwlabel, Bewohner bewohner) {
//        setBWLabel(bwlabel, bewohner.getBWKennung());
//    }

//    public static void setBWLabel(JLabel bwlabel, String currentBW) {
//        HashMap bw = DBHandling.getBW(currentBW);
//        String result = bw.get("nachname") + ", " + bw.get("vorname") + " (*" + SYSCalendar.printGermanStyle((Date) bw.get("gebdatum")) + ", ";
//        result += SYSCalendar.calculateAge(SYSCalendar.toGC((Date) bw.get("gebdatum"))) + " Jahre) [" + currentBW + "]";
//
//        // Diese bwinfo5 ist für die besonderheiten bzgl. Vitalwerten. Da steht was über Allergien usw. drin.
//        BWInfo bwinfo5 = new BWInfo(currentBW, SYSCalendar.today_date(), BWInfo.ART_ALLES, 8, false);
//
//        String tooltipvorab = "";
//        // ======================== Besonderheiten =======================
//        boolean besonderheiten = false;
//        if (bwinfo5.getAttribute().size() > 0) {
//            ArrayList attribs = bwinfo5.getAttribute();
//            TMBWInfo tmbwi5 = new TMBWInfo(bwinfo5.getAttribute(), true, false, false);
//            for (int i = 0; i < attribs.size(); i++) {
//                besonderheiten = true;
//                tooltipvorab += "<li>";
//                tooltipvorab += SYSTools.unHTML2(tmbwi5.getValueAt(i, TMBWInfo.COL_HTML).toString());
//                tooltipvorab += "</li>";
//            }
//        }
//
//        if (besonderheiten) {
//            result += " <font color=\"blue\">&#9679;</font> ";
//        }
//        if (DBRetrieve.getAbwesendSeit(currentBW) != null) {
//            result += " &rarr; ";
//        }
//        if (op.share.vorgang.DBHandling.hatVorgang(currentBW)) {
//            result += " <font color=\"red\">&#9679;</font> ";
//        }
//        if (bwinfo5.isVerstorben()) {
//            result += " <font color=\"black\">&dagger;</font> ";
//        }
//        if (bwinfo5.isAusgezogen()) {
//            result += " &darr; ";
//        }
//
//
//        String tooltip = "<ul>";
//
//        bwlabel.setText(SYSTools.toHTML(result));
//
//        // =============== BVs ==============================
//        ArrayList bwa1 = DBRetrieve.getLetztesBWAttribut(currentBW, "BV1");
//        ArrayList bwa2 = DBRetrieve.getLetztesBWAttribut(currentBW, "BV2");
//        if (bwa1 != null || bwa2 != null) {
//            if (bwa1 != null && (bwa1.get(0).toString().equalsIgnoreCase("<unbeantwortet value=\"true\"/>") || ((Date) bwa1.get(2)).before(SYSCalendar.nowDBDate()))) {
//                bwa1 = null;
//            }
//            if (bwa2 != null && (bwa2.get(0).toString().equalsIgnoreCase("<unbeantwortet value=\"true\"/>") || ((Date) bwa2.get(2)).before(SYSCalendar.nowDBDate()))) {
//                bwa2 = null;
//            }
//            if (bwa1 != null) {
//                // Etwas unorthodox mit dem Tokenizer einen XML Schnipsel zu zerschneiden. Geht aber. ;-)
//                StringTokenizer st = new StringTokenizer(bwa1.get(0).toString(), "\"");
//                st.nextToken();
//                String pk = st.nextToken();
//                HashMap hm = DBRetrieve.getSingleRecord("OCUsers", new String[]{"Nachname", "Vorname"}, "UKennung", pk);
//                tooltip += "<li>BV1: " + hm.get("Nachname") + ", " + hm.get("Vorname") + "</li>";
//            }
//            if (bwa2 != null) {
//                // Etwas unorthodox mit dem Tokenizer einen XML Schnipsel zu zerschneiden. Geht aber. ;-)
//                StringTokenizer st = new StringTokenizer(bwa2.get(0).toString(), "\"");
//                st.nextToken();
//                String pk = st.nextToken();
//                HashMap hm = DBRetrieve.getSingleRecord("OCUsers", new String[]{"Nachname", "Vorname"}, "UKennung", pk);
//                tooltip += "<li>BV2: " + hm.get("Nachname") + ", " + hm.get("Vorname") + "</li>";
//            }
//
//        } else {
//            tooltip += "<li><i>kein BV zugeordnet</i></li>";
//        }
//
//        BWInfo bwinfo1 = new BWInfo(currentBW, "HAUSARZT", SYSCalendar.nowDBDate());
//        BWInfo bwinfo2 = new BWInfo(currentBW, "FACHARZT", SYSCalendar.nowDBDate());
//        BWInfo bwinfo3 = new BWInfo(currentBW, "BETREUER1", SYSCalendar.nowDBDate());
//        BWInfo bwinfo4 = new BWInfo(currentBW, "ANGEH", SYSCalendar.nowDBDate());
//
//        tooltip += tooltipvorab;
//
//        // ======================== Ärzte =======================
//        if (bwinfo1.getAttribute().size() > 0) {
//            TMBWInfo tmbwi1 = new TMBWInfo(bwinfo1.getAttribute(), true, false, false);
//            tooltip += "<li>";
//            tooltip += anonymizeString(SYSTools.unHTML2(tmbwi1.getValueAt(0, TMBWInfo.COL_HTML).toString()));
//            tooltip += "</li>";
//        }
//
//        if (bwinfo2.getAttribute().size() > 0) {
//            TMBWInfo tmbwi2 = new TMBWInfo(bwinfo2.getAttribute(), true, false, false);
//            tooltip += "<li>";
//            tooltip += anonymizeString(SYSTools.unHTML2(tmbwi2.getValueAt(0, TMBWInfo.COL_HTML).toString()));
//            tooltip += "</li>";
//        }
//
//        // ======================= Betreuer ======================
//        if (bwinfo3.getAttribute().size() > 0) {
//            TMBWInfo tmbwi3 = new TMBWInfo(bwinfo3.getAttribute(), true, false, false);
//            tooltip += "<li>";
//            tooltip += anonymizeString(SYSTools.unHTML2(tmbwi3.getValueAt(0, TMBWInfo.COL_HTML).toString()));
//            tooltip += "</li>";
//        }
//
//        // ======================= Angehörige =====================
//        if (bwinfo4.getAttribute().size() > 0) {
//            TMBWInfo tmbwi4 = new TMBWInfo(bwinfo4.getAttribute(), true, false, false);
//            tooltip += "<li>";
//            tooltip += anonymizeString(SYSTools.unHTML2(tmbwi4.getValueAt(0, TMBWInfo.COL_HTML).toString()));
//            tooltip += "</li>";
//        }
//
//        bwlabel.setToolTipText(SYSTools.toHTML(tooltip + "</ul>"));
//        bwinfo1.cleanup();
//        bwinfo2.cleanup();
//        bwinfo3.cleanup();
//        bwinfo4.cleanup();
//    }

    //    public static String anonymizeUser(String ukennung) {
//        String result = ukennung;
//        if (OPDE.isAnonym()) {
//            int len = ukennung.length();
//            result = ukennung.substring(0, 2);
//            for (int i = 2; i < len; i++) {
//                result += "*";
//            }
//        }
//        return result;
//    }
    public static String anonymizeName(String in, int arrayindex) {
        String name = in;
        if (OPDE.isAnonym()) {
            String ersterBuchstabe = in.toLowerCase().substring(0, 1);
            int random = in.charAt(1) % 5;
            name = ((String[]) OPDE.anonymize[arrayindex].get(ersterBuchstabe))[random];
        }
        return name;
    }

    public static String anonymizeString(String in) {
        String out = "[anonymisiert]";
        if (!OPDE.isAnonym()) {
            out = in;
        }
        return out;
    }

    public static String anonymizeName(String in) {
        String out = "[anonymisiert]";
        if (!OPDE.isAnonym()) {
            out = in;
        }
        return out;
    }

    public static Date anonymizeDate(Date in) {
        Date result = in;
        if (OPDE.isAnonym()) {
            GregorianCalendar gc = SYSCalendar.toGC(in);
            // Plus oder Minus. Wird zufällig entschieden.
            boolean plus = (gc.get(GregorianCalendar.DAY_OF_MONTH) % 2) == 0;
            GregorianCalendar gcnow = new GregorianCalendar();

            if (plus) {
                gc.add(GregorianCalendar.DAY_OF_YEAR, gcnow.get(GregorianCalendar.DAY_OF_YEAR));
                gc.add(GregorianCalendar.YEAR, gcnow.get(GregorianCalendar.MONTH));
            } else {
                gc.add(GregorianCalendar.DAY_OF_YEAR, gcnow.get(GregorianCalendar.DAY_OF_YEAR) * -1);
                gc.add(GregorianCalendar.YEAR, gcnow.get(GregorianCalendar.MONTH) * -1);
            }
            result = new Date(gc.getTimeInMillis());
        }
        return result;

    }

    public static String anonymizeBW(String nachname, String vorname, String bwkennung, int geschlecht) {

        if (OPDE.isAnonym()) {
            nachname = anonymizeName(nachname, INDEX_NACHNAME);
            if (geschlecht == 2) {
                vorname = anonymizeName(vorname, INDEX_VORNAME_FRAU);
            } else {
                vorname = anonymizeName(vorname, INDEX_VORNAME_MANN);
            }
        }
        return nachname + ", " + vorname + " [" + bwkennung + "]";
    }

    /**
     * Erstellt eine ComboxBox mit den Namen der Benutzer.
     *
     * @param status Status = 1 aktive Benutzer. Status = 0 inaktive. Status = -1 alle
     * @return ComboBox aus ListElements mit den UKennungen in dem "Value" Attribut
     */
    public static DefaultComboBoxModel getUserList(int status) {
        HashMap where = new HashMap();
        DefaultComboBoxModel result = new DefaultComboBoxModel();
        if (status >= 0) {
            where.put("Status", new Object[]{status, "="});
        }
        ResultSet rs = op.tools.DBHandling.getResultSet("OCUsers", new String[]{"UKennung", "Nachname", "Vorname"}, where, new String[]{"Nachname", "Vorname"});

        result.addElement(new ListElement(toHTML("<i>alle</i>"), null));
        try {
            rs.beforeFirst();
            while (rs.next()) {
                String s = rs.getString("Nachname") + ", " + rs.getString("Vorname") + " (" + rs.getString("UKennung") + ")";
                result.addElement(new ListElement(s, rs.getString("UKennung")));
            }
        } catch (SQLException exc) {
        }
        return result;
    }

    public static String anonymizeUser(String nachname, String vorname) {
        String result;
        if (OPDE.isAnonym()) {
            result = nachname.substring(0, 1) + "***, " + vorname.substring(0, 1) + "***";
        } else {
            result = nachname + ", " + vorname;
        }
        return result;
    }

    public static String anonymizeUser(String name) {
        String result;
        if (OPDE.isAnonym()) {
            result = name.substring(0, 1) + "***";
        } else {
            result = name;
        }
        return result;
    }

//    public static String getBWLabel1(String currentBW) {
//        HashMap bw = DBHandling.getBW(currentBW);
//        String result = bw.get("nachname") + ", " + bw.get("vorname");
//        return result;
//    }
//
//    public static String getBWLabel2(String currentBW) {
//        HashMap bw = DBHandling.getBW(currentBW);
//        String result = "(*" + SYSCalendar.printGermanStyle((Date) bw.get("gebdatum")) + ") [" + currentBW + "]";
//        return result;
//    }


    public static String getBWLabel(String currentBW) {
        HashMap bw = DBHandling.getBW(currentBW);
        String result = bw.get("nachname") + ", " + bw.get("vorname") + " (*" + SYSCalendar.printGermanStyle((Date) bw.get("gebdatum")) + ", ";

        Object[] obj = op.share.bwinfo.DBHandling.miniBWInfo(currentBW);
//        boolean aufenthalt = (Boolean) obj[0];
//        Date vonHauf = (Date) obj[1];;
        Date bisHauf = (Date) obj[2];
        boolean ausgezogen = (Boolean) obj[3];
        boolean verstorben = (Boolean) obj[4];
        if (verstorben) {
            // In dem Fall, wird das Alter bis zum Sterbedatum gerechnet.
            result += SYSCalendar.calculateAge(SYSCalendar.toGC((Date) bw.get("gebdatum")), SYSCalendar.toGC(bisHauf)) + " Jahre) [" + currentBW + "]";
            result += "  verstorben: " + SYSCalendar.printGermanStyle(bisHauf) + ", ";
        } else {
            if (ausgezogen) {
                result += "  ausgezogen: " + SYSCalendar.printGermanStyle(bisHauf) + ", ";
            }
            result += SYSCalendar.calculateAge(SYSCalendar.toGC((Date) bw.get("gebdatum"))) + " Jahre) [" + currentBW + "]";
        }
        return result;
    }

    /**
     * @return die BWKennung des gewünschten Bewohners oder "" wenn die Suche nicht erfolgreich war.
     */
    @Deprecated
    public static String findeBW(java.awt.Frame parent, String muster, boolean admin) {
        String result = "";

        HashMap where = new HashMap();
        where.put("BWKennung", new Object[]{muster, "="});
        if (!admin) {
            where.put("adminonly", new Object[]{new Boolean(false), "="});
        }

        result = (String) DBHandling.getSingleValue("Bewohner", "BWKennung", where); //"BWKennung",muster);
        if (result == null) { // das Muster war kein gültiger Primary Key, dann suchen wir eben nach Namen.
            muster += "%"; // MySQL Wildcard
            HashMap where1 = new HashMap();
            where1.put("Nachname", new Object[]{muster, "like"});
            if (!admin) {
                where1.put("adminonly", new Object[]{new Boolean(false), "="});
            }

            ResultSet rs = DBHandling.getResultSet("Bewohner", new String[]{"BWKennung", "Nachname", "Vorname", "GebDatum", "BWKennung"}, where1);
            DefaultListModel dlm = rs2lst(rs);
            if (dlm.getSize() > 1) {
                DlgListSelector dlg = new DlgListSelector(parent, "Auswahlliste Bewohner", "Bitte wählen Sie eine(n) Bewohner(in) aus.", "Ihre Suche ergab mehrere Möglichkeiten. Welche(n) Bewohner(in) meinten Sie ?", dlm);
                Object selection = dlg.getSelection();
                if (selection != null) {
                    ListElement le = (ListElement) selection;
                    result = le.getData();
                } else {
                    result = "";
                }
            } else if (dlm.getSize() == 1) {
                try {
                    rs.first();
                    result = rs.getString("BWKennung");
                } catch (SQLException ex) {
                    new DlgException(ex);
                    ex.printStackTrace();
                }
            } else {
                result = "";
            }
        }
        return result;
    }

    public static void markAllTxt(JTextField jtf) {
        jtf.setSelectionStart(0);
        jtf.setSelectionEnd(jtf.getText().length());
    }

    public static void changeTabTraversal(JTextArea component) {
        // Add actions
        component.getActionMap().put(nextFocusAction.getValue(Action.NAME), nextFocusAction);
        component.getActionMap().put(prevFocusAction.getValue(Action.NAME), prevFocusAction);
    }

    // The actions
    public static Action nextFocusAction = new AbstractAction("Move Focus Forwards") {

        public void actionPerformed(ActionEvent evt) {
            ((Component) evt.getSource()).transferFocus();
        }
    };
    public static Action prevFocusAction = new AbstractAction("Move Focus Backwards") {

        public void actionPerformed(ActionEvent evt) {
            ((Component) evt.getSource()).transferFocusBackward();
        }
    };

    public static DefaultListModel rs2lst(ResultSet rs) {
        return rs2lst(rs, null, false);
    }

    public static DefaultListModel rs2lst(ResultSet rs, String[] prefix) {
        return rs2lst(rs, prefix, false);
    }

    /**
     * Die erste Spalte enthält immer den Primary Key.
     */
    public static DefaultListModel rs2lst(ResultSet rs, String[] prefix, boolean withEmptyFirstElement) {
        DefaultListModel dcbm = new DefaultListModel();
        if (withEmptyFirstElement) {
            dcbm.addElement(new ListElement("<html><i>Keine Auswahl</i></html>", 0l));
        }
        if (rs != null) {
            try {
                ResultSetMetaData rsmd = rs.getMetaData();
                int colcount = rsmd.getColumnCount();
                int pktype = rsmd.getColumnType(1);

                // hier stimmt was nicht. Es muss für jeden Col einen Prefix geben. Und wenn der auch leer ist.
                if (prefix != null && prefix.length != colcount) {
                    return dcbm;
                }

                rs.beforeFirst();

                while (rs.next()) {
                    String value = "";
                    boolean dropComma = false;
                    for (int i = 2; i <= colcount; i++) {
                        if (prefix != null) {
                            value += prefix[i - 1]; // das prefix Array beginnt bei 0, aber den PK brauchen wir ja nicht, somit 1
                        }
                        int type = rsmd.getColumnType(i);
                        switch (type) {
                            case (java.sql.Types.DATE): {
                                value += SYSCalendar.printGermanStyle(rs.getDate(i));
                                dropComma = false;
                                break;
                            }
                            default: {
                                String str = rs.getString(i);
                                if (str == null || str.equals("")) {
                                    dropComma = true;
                                } else {
                                    dropComma = false;
                                    value += str;
                                }
                            }
                        }
                        if (i < colcount && !dropComma) {
                            value += ", ";
                        }
                    }

                    if (pktype == java.sql.Types.BIGINT || pktype == java.sql.Types.INTEGER) {
                        java.math.BigInteger bi = (java.math.BigInteger) rs.getObject(1);
                        dcbm.addElement(new ListElement(value, bi.longValue()));
                    } else {
                        dcbm.addElement(new ListElement(value, rs.getString(1)));
                    }
                }
            } // try
            catch (SQLException se) {
                new DlgException(se);
                se.printStackTrace();
            } // catch
        }
        return dcbm;
    }


    public static DefaultListModel list2dlm(List list) {
        DefaultListModel dlm = new DefaultListModel();
        for (Object o : list) {
            dlm.addElement(o);
        }
        return dlm;
    }

    /**
     * Erstellt aus einem Result Set ein ComboBox Modell. Wobei davon ausgegangen wird, dass in der ersten Spalte immer der PK steht.
     *
     * @param rs
     * @return
     */
    public static DefaultComboBoxModel rs2cmb(ResultSet rs) {
        return rs2cmb(rs, null, false);
    }

    public static DefaultComboBoxModel rs2cmb(ResultSet rs, boolean withEmptyFirstElement) {
        return rs2cmb(rs, null, withEmptyFirstElement);
    }

    public static DefaultComboBoxModel rs2cmb(ResultSet rs, String[] prefix) {
        return rs2cmb(rs, prefix, false);
    }

    public static DefaultComboBoxModel rs2cmb(ResultSet rs, String[] prefix, boolean withEmptyFirstElement) {
        DefaultListModel dlm = rs2lst(rs, prefix, withEmptyFirstElement);
        Object ar = Array.newInstance(ListElement.class, dlm.size());
        ListElement[] array = (ListElement[]) ar;
        dlm.copyInto(array);
        dlm.clear();
        return new DefaultComboBoxModel(array);
    }

    // This method iconifies a frame; the maximized bits are not affected.
    // taken from: The Java Developers Almanac 1.4
    // e564. Iconifying and Maximizing a Frame
    public static void iconify(Frame frame) {
        int state = frame.getExtendedState();

        // Set the iconified bit
        state |= Frame.ICONIFIED;

        // Iconify the frame
        frame.setExtendedState(state);
    }

    // This method deiconifies a frame; the maximized bits are not affected.
    public static void deiconify(Frame frame) {
        int state = frame.getExtendedState();

        // Clear the iconified bit
        state &= ~Frame.ICONIFIED;

        // Deiconify the frame
        frame.setExtendedState(state);
    }

    public static String hashword(String password) {
        String hashword = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(password.getBytes());
            BigInteger hash = new BigInteger(1, md5.digest());
            hashword = hash.toString(16);
            if (hashword.length() == 31) {
                hashword = "0" + hashword;
            }
        } catch (NoSuchAlgorithmException nsae) {
            // ignore
        }
        return hashword;
    }

    // Taken From: http://www.rgagnon.com/javadetails/java-0416.html
    public static byte[] createChecksum(File file) throws
            Exception {
        InputStream fis = new FileInputStream(file);

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;
        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);
        fis.close();
        return complete.digest();
    }

    /**
     * Berechnet aus einer Datei die MD5 Signatur.
     * Teilweise auf http://rgagnon.com/javadetails/java-0596.html
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static String getMD5Checksum(File file) throws Exception {
        byte[] b = createChecksum(file);
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result +=
                    Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public static char[] cleanCharArray(char[] pw) {
        if (pw != null) {
            for (int i = 0; i < pw.length; i++) {
                pw[i] = '\0';
            }
        }
        return new char[]{};
    }

    public static String[] ArrayList2StringArray(ArrayList al) {
        String[] result = new String[al.size()];
        for (int i = 0; i < al.size(); i++) {
            result[i] = (String) al.get(i);
        }
        return result.clone();
    }

    /**
     * Wählt in einer ComboBox aus ListElements das Element mit einem bestimmten PK aus. Wurde
     * entwickelt für Comboboxen mit einem Modell aus der RS2CMB Methode.
     *
     * @param j  die gesetzt werden soll
     * @param pk gesuchter PK
     */
    public static void selectInComboBox(JComboBox j, long pk) {
        ComboBoxModel cbm = (ComboBoxModel) j.getModel();
        for (int i = 0; i < cbm.getSize(); i++) {
            ListElement le = (ListElement) cbm.getElementAt(i);
            if (le.getPk() == pk) {
                j.setSelectedIndex(i);
                return;
            }
        }
    }

    public static void selectInList(JList j, long pk) {
        ListModel m = (ListModel) j.getModel();
        for (int i = 0; i < m.getSize(); i++) {
            ListElement le = (ListElement) m.getElementAt(i);
            if (le.getPk() == pk) {
                j.setSelectedIndex(i);
                return;
            }
        }
    }

    /**
     * Wählt in einer ComboBox aus ListElements das Element mit einem bestimmten String aus.
     */
    public static void selectInComboBox(JComboBox j, String pattern, boolean useValue) {
        ComboBoxModel cbm = (ComboBoxModel) j.getModel();
        for (int i = 0; i < cbm.getSize(); i++) {
            ListElement le = (ListElement) cbm.getElementAt(i);
            if (useValue) {
                if (le.getValue().equalsIgnoreCase(pattern)) {
                    j.setSelectedIndex(i);
                    return;
                }
            } else {
                if (le.getData().equalsIgnoreCase(pattern)) {
                    j.setSelectedIndex(i);
                    return;
                }

            }
        }
    }

    public static void selectInComboBox(JComboBox j, String pattern) {
        selectInComboBox(j, pattern, false);
    }

    public static void addAllNodes(DefaultMutableTreeNode root, java.util.List children) {
        if (children.size() > 0) {
            Iterator it = children.iterator();
            while (it.hasNext()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) it.next();
                root.add(node);
            }
        }
    }

    public static String catchNull(Object in) {
        return catchNull(in, "");

    }

    /**
     * Ermittelt die Zeichendarstellung eines Objekts (toString). Ist das Ergebnis null oder eine leere Zeichenkette, dann wird
     * der String neutral zurück gegeben.
     *
     * @param in
     * @param neutral
     * @return
     */
    public static String catchNull(Object in, String neutral) {
        String result = neutral;
        if (in != null) {
            result = in.toString();
            if (result.equals("")) {
                result = neutral;
            }
        }
        return result;
    }

    public static String catchNull(String in) {
        return (in == null ? "" : in);
    }

    /**
     * Gibt die toString Ausgabe eines Objektes zurück. Hierbei kann man sicher sein, dass man nicht über
     * ein <code>null</code> stolpert.
     *
     * @param in     Eingangsobjekt
     * @param prefix Präfix, der vorangestellt wird, wenn das Objekt nicht null ist.
     * @param suffix Suffix, der angehangen wird, wenn das Objekt nicht null ist.
     * @return
     */
    public static String catchNull(Object in, String prefix, String suffix) {
        String result = "";
        if (!catchNull(in).equals("")) {
            result = prefix + catchNull(in) + suffix;
        }
        return result;
    }

    /**
     * Gibt eine einheitliche Titelzeile für alle Fenster zurück.
     */
    public static String getWindowTitle(String moduleName) {
        if (!moduleName.equals("")) {
            moduleName = ", " + moduleName;
        }
        return OPDE.getAppInfo().getProgname() + moduleName + ", v" + OPDE.getAppInfo().getVersion()
                + "/" + OPDE.getAppInfo().getBuild() + (OPDE.isDebug() ? " !! DEBUG !!" : "");
    }

    public static void expandAll(JTree tree) {
        int row = 0;
        while (row < tree.getRowCount()) {
            tree.expandRow(row);
            row++;
        }
    }

    public static void collapseAll(JTree tree) {
        int row = 0;
        while (row < tree.getRowCount()) {
            tree.collapseRow(row);
            row++;
        }
    }

    public static Vector getExpansionState(JTree tree) {
        Vector v = new Vector();
        int row = 0;
        while (row < tree.getRowCount()) {
            v.add(new Boolean(tree.isExpanded(row)));
            row++;
        }
        return v;
    }

    public static void setExpansionState(JTree tree, Vector v) {
        int row = 0;
        while (row < tree.getRowCount()) {
            Boolean expanded = (Boolean) v.get(row);
            if (expanded) {
                tree.expandRow(row);
            } else {
                tree.collapseRow(row);
            }
            row++;
        }
    }

    public static DefaultListModel cmb2lst(DefaultComboBoxModel dcbm) {
        DefaultListModel dlm = new DefaultListModel();
        for (int i = 0; i < dcbm.getSize(); i++) {
            dlm.addElement(dcbm.getElementAt(i));
        }
        return dlm;
    }

    public static File[] chooseFile(Component parent, boolean multiselection) {
        File[] result = null;
        String cname = parent.getClass().getName();
        String startdir = System.getProperty("user.home");
        if (OPDE.getProps().containsKey("DIR." + cname)) {
            startdir = OPDE.getProps().getProperty("DIR." + cname);
        }
        JFileChooser jfc = new JFileChooser(startdir);
        jfc.setMultiSelectionEnabled(multiselection);
        int response = jfc.showOpenDialog(parent);
        if (response == JFileChooser.APPROVE_OPTION) {
            if (multiselection) {
                result = jfc.getSelectedFiles();
            } else {
                result = new File[]{jfc.getSelectedFile()};
            }

            //String newPath = result[0].getAbsolutePath();
            String myPath = result[0].getParent();
            SYSPropsTools.storeProp("DIR." + cname, myPath, OPDE.getLogin().getUser());

        }
        return result;
    }

    public static ArrayList getSelectedFromTableModel(JTable tbl, int col) {
        ArrayList result = new ArrayList();
        ListSelectionModel lsm = tbl.getSelectionModel();
        TableModel tm = tbl.getModel();
        if (!lsm.isSelectionEmpty()) {
            for (int i = lsm.getMinSelectionIndex(); i <= lsm.getMaxSelectionIndex(); i++) {
                if (lsm.isSelectedIndex(i)) {
                    result.add(tm.getValueAt(i, col));
                }
            }
        }
        return result;
    }

    public static List getSelectionAsList(List list, int[] sel) {
        List target = null;
        if (sel == null) {
            target = list;
        } else {
            target = new ArrayList(sel.length);
            if (sel.length > 0) {
                for (int i = 0; i < sel.length; i++) {
                    target.add(list.get(sel[i]));
                }
            }
        }
        return target;
    }

    /**
     * Gibt den Teil eines Dateinamens zurück, der als Extension bezeichnet wird. Also html oder pdf etc.
     *
     * @param name
     * @return
     */
    public static String filenameExtension(String name) {
        int dot = name.lastIndexOf(".");
        return name.substring(dot + 1);
    }

    public static String printDouble(double d) {
        String dbl = Double.toString(d);
        if (dbl.substring(dbl.length() - 2).equals(".0")) {
            dbl = dbl.substring(0, dbl.length() - 2);
        } else if (dbl.equals("0.5")) {
            dbl = "&frac12;";
        } else if (dbl.equals("0.25")) {
            dbl = "&frac14;";
        } else if (dbl.equals("0.75")) {
            dbl = "&frac34;";
        } else if (dbl.equals("0.33")) {
            dbl = "<sup>1</sup>/<sub>3</sub>";
        }
        return dbl;
    }

    public static String printDouble4Jasper(double d) {
        String dbl = Double.toString(d);
        if (d == 0d) {
            dbl = "";
        } else if (dbl.substring(dbl.length() - 2).equals(".0")) {
            dbl = dbl.substring(0, dbl.length() - 2);
        } else if (dbl.equals("0.5")) {
            dbl = "<sup>1</sup>/<sub>2</sub>";
        } else if (dbl.equals("0.25")) {
            dbl = "<sup>1</sup>/<sub>4</sub>";
        } else if (dbl.equals("0.75")) {
            dbl = "<sup>3</sup>/<sub>4</sub>";
        } else if (dbl.equals("0.33")) {
            dbl = "<sup>1</sup>/<sub>3</sub>";
        }
        return dbl;
    }

    /**
     * Fügt html Tags vor und hinter den Eingangsstring ein.
     *
     * @param in Eingangsstring
     * @return String mit HTML Erweiterungen.
     */
    public static String toHTML(String in) {
        String out = null;
        if (!catchNull(in).equals("")) {
            out = "<html><body>" + in + "</body></html>";
        }
        return out;
    }

    /**
     * Einfache Funktion, die einen Text mit einer HTML Dokumentenstruktur umschließt
     * und dadurch auch den Titel des Browsers setzt. Auf Wunsch wird auch direkt
     * ein javascript hinzugefügt, dass die Browser Druckfunktion startet.
     *
     * @param in    - Eingabetext
     * @param title - Browsertitel
     * @param print - Drucken ?
     * @return HTML Dokument
     */
    public static String addHTMLTitle(String in, String title, boolean print) {
        String out = null;
        if (!catchNull(in).equals("")) {
            out = "<html><head><title>" + title + "</title>";
            if (print) {
                out += "<script type=\"text/javascript\">"
                        + "window.onload = function() {"
                        + "window.print();"
                        + "}</script>";
            }
            out += "</head><body>" + in + "</body></html>";
        }
        return out;
    }

    public static String unHTML(String in) {
        String result = in;
        result = SYSTools.replace(result, "<ul>", "");
        result = SYSTools.replace(result, "</ul>", "");
        result = unHTML2(result);
        result = SYSTools.replace(result, "</font>", "");
        result = result.replaceAll("\\<font.*?\\>", "");
        result = result.replaceAll("&diams;", "");
        return result;
    }

    public static String escapeXML(String in) {
        String result = in;
        result = SYSTools.replace(result, "\"", "&quot;");
        result = SYSTools.replace(result, "<", "&lt;");
        result = SYSTools.replace(result, ">", "&gt;");
        result = SYSTools.replace(result, "&", "&amp;");
        return result;
    }

    public static String unescapeXML(String in) {
        String result = in;
        result = SYSTools.replace(result, "&quot;", "\"");
        result = SYSTools.replace(result, "&lt;", "<");
        result = SYSTools.replace(result, "&gt;", ">");
        result = SYSTools.replace(result, "&amp;", "&");
        return result;
    }

    /**
     * Tauscht in einem String alle Umlaute gegen den entsprechend HTML Tag aus.
     *
     * @param in String
     * @return geänderter String
     */
    public static String htmlUmlautConversion(String in) {
        String result = in;
        result = SYSTools.replace(result, "Ä", "&Auml;");
        result = SYSTools.replace(result, "ä", "&auml;");
        result = SYSTools.replace(result, "Ö", "&Ouml;");
        result = SYSTools.replace(result, "ö", "&ouml;");
        result = SYSTools.replace(result, "Ü", "&Uuml;");
        result = SYSTools.replace(result, "ü", "&uuml;");
        result = SYSTools.replace(result, "ß", "&szlig;");
        return result;
    }

    public static String unHTML2(String in) {
        String result = in;
        result = SYSTools.replace(result, "<html>", "");
        result = SYSTools.replace(result, "</html>", "");
        result = SYSTools.replace(result, "<body>", "");
        result = SYSTools.replace(result, "</body>", "");
        return result;
    }

    public static DefaultComboBoxModel lst2cmb(DefaultListModel dlm) {
        DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
        for (int i = 0; i < dlm.size(); i++) {
            dcbm.addElement(dlm.get(i));
        }
        return dcbm;
    }


    public static Color getColor(String colorname) {
        Color color = Color.black;

        if (colorname.equalsIgnoreCase("red")) {
            color = Color.red;
        } else if (colorname.equalsIgnoreCase("blue")) {
            color = Color.blue;
        } else if (colorname.equalsIgnoreCase("green")) {
            color = Color.green;
        } else if (colorname.equalsIgnoreCase("yellow")) {
            color = Color.yellow;
        } else if (colorname.equalsIgnoreCase("cyan")) {
            color = Color.CYAN;
        } else if (colorname.equalsIgnoreCase("light_gray")) {
            color = Color.LIGHT_GRAY;
        } else if (colorname.equalsIgnoreCase("dark_gray")) {
            color = Color.DARK_GRAY;
        } else if (colorname.equalsIgnoreCase("gray")) {
            color = Color.GRAY;
        } else if (colorname.equalsIgnoreCase("pink")) {
            color = Color.PINK;
        } else if (colorname.equalsIgnoreCase("magenta")) {
            color = Color.MAGENTA;
        } else if (colorname.equalsIgnoreCase("white")) {
            color = Color.WHITE;
        } else if (colorname.equalsIgnoreCase("orange")) {
            color = Color.ORANGE;
        }
        return color;
    }

    /**
     * Diese Methode findet aus den properties eine lokal definierte Applikation
     * heraus. Das braucht man nur dann, wenn die Funktionen der Java eigenen
     * Desktop API nicht funktionieren.
     *
     * @param filename
     * @return String[] der das passende command array für den EXEC Aufruf erhält.
     */
    public static String[] getLocalDefinedApp(String filename) {
        String os = System.getProperty("os.name").toLowerCase();
        String extension = filenameExtension(filename);
        String[] result = null;
        if (OPDE.getProps().containsKey(os + "-" + extension)) {
            result = new String[]{OPDE.getProps().getProperty(os + "-" + extension), filename};
        }
        return result;
    }

    /**
     * Wrapper Methode für das Parsing eines Doubles. Ersetzt vorher noch alle Kommas durch Punkte.
     * Außerdem werden Brüche wie 1/4, 1/2 usw.
     *
     * @param text
     * @return
     */
    public static double parseDouble(String text) throws NumberFormatException {
        text = text.replace(",", ".");
        text = text.replace("1/4", "0.25");
        text = text.replace("1/2", "0.5");
        text = text.replace("3/4", "0.75");
        text = text.replace("1/3", "0.33");
        text = text.replace("2/3", "0.66");

        return Double.parseDouble(text);
    }

    public static String rs2html(ResultSet rs, boolean markiere0) {
        String result = "";
        try {
            ResultSetMetaData md = rs.getMetaData();
            int count = md.getColumnCount();
            rs.beforeFirst();

            result += "<table border=1>";
            result += "<tr>";
            for (int i = 1; i <= count; i++) {
                result += "<th>";
                result += md.getColumnLabel(i);
                result += "</th>";
            }
            result += "</tr>";
            while (rs.next()) {
                result += "<tr>";
                for (int i = 1; i <= count; i++) {
                    result += "<td>";
                    if (markiere0) {
                        try {
                            double d = Double.parseDouble(rs.getString(i));
                            if (d == 0) {
                                result += "<font color=\"red\"><b>" + rs.getString(i) + "</b></font>";
                            } else {
                                result += rs.getString(i);
                            }
                        } catch (NumberFormatException ne) {
                            result += rs.getString(i);
                        }
                    } else {
                        result += rs.getString(i);
                    }
                    result += "</td>";
                }
                result += "</tr>";
            }
            result += "</table>";
        } catch (SQLException sql) {
            OPDE.getLogger().error(sql);
        }
        return result;
    }

    /**
     * Erstellt ein Teil eines SQL Ausdrucks für "WHERE X IN (1, 2, 3, 4)" ähnliche Ausdrücke.
     *
     * @param ids ein Array aus longs, die jeweils in die Klammern geschrieben werdenb sollen.
     * @return der Klammerausdruck aus dem obigen Beispiel mit jeweils einem Leerzeichen davor und dahinter.
     */
    public static String array2sqlset(Object[] ids) {
        String result = " (-1) ";
        if (ids.length > 0) {
            result = " (";

            for (int i = 0; i < ids.length; i++) {
                result += ids[i];
                if (i < ids.length - 1) {
                    result += ",";
                }
            }

            result += ") ";
        }
        return result;

    }

    public static String text2HTMLColumn(String in, int width) {
        String out = "<table><tr><td width=\"" + width + "\" align=\"justify\">" + in
                + "</td><tr></table>";
        return out;
    }

    /**
     * Habe ich von http://helpdesk.objects.com.au/java/how-can-i-merge-two-java-arrays-into-one-combined-array
     *
     * @param <T>
     * @param arrays
     * @return
     */
    public static <T> T[] merge(T[]... arrays) {
        // Determine required size of new array

        int count = 0;
        for (T[] array : arrays) {
            count += array.length;
        }

        // create new array of required class

        T[] mergedArray = (T[]) Array.newInstance(
                arrays[0][0].getClass(), count);

        // Merge each array into new array

        int start = 0;
        for (T[] array : arrays) {
            System.arraycopy(array, 0,
                    mergedArray, start, array.length);
            start += array.length;
        }
        return (T[]) mergedArray;
    }

    public static String anonymizeText(String nachname, String text) {
        String result = text;
        if (OPDE.isAnonym() && !catchNull(nachname).equals("")) {
            result = replace(text, nachname, "[anonym]");
        }
        return result;
    }
//
//    public static DefaultListModel newListModel(String namedQuery) {
//        return newListModel(namedQuery, null);
//    }

    public static DefaultListModel newListModel(String namedQuery, Object[]... params) {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery(namedQuery);
        if (params != null) {
            for (Object[] param : params) {
                query.setParameter(param[0].toString(), param[1]);
            }
        }

        DefaultListModel lmodel = newListModel(query.getResultList());

        em.close();

        return lmodel;
    }

    public static DefaultListModel newListModel(List list) {
        DefaultListModel listModel = new DefaultListModel();
        if (list != null) {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                listModel.addElement(it.next());
            }
        }
        return listModel;
    }

    public static DefaultComboBoxModel newComboboxModel(String namedQuery, Object[]... params) {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery(namedQuery);
        if (params != null) {
            for (Object[] param : params) {
                query.setParameter(param[0].toString(), param[1]);
            }
        }
        DefaultComboBoxModel lcombo = newComboboxModel(query.getResultList());

        em.close();

        return lcombo;
    }

    public static DefaultComboBoxModel newComboboxModel(List list) {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        if (list != null) {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                model.addElement(it.next());
            }
        }
        return model;
    }

    /**
     * Erstellt eine UKennung. Prüft aber <b>nicht</b> danach, ob die schon
     * vergeben ist.
     *
     * @param nachname
     * @param vorname
     * @return UKennung mit einer Länge von maximal 10 Zeichen.
     */
    public static String generateUKennung(String nachname, String vorname) {
        String kennung = "";
        if (nachname.length() > 0 && vorname.length() > 0) {
            kennung = vorname.substring(0, 1) + nachname.substring(0, Math.min(9, nachname.length()));
        }
        return kennung.toLowerCase();
    }

    public static String padL(String str, int size, String padChar) {
        StringBuilder padded = new StringBuilder(str);
        while (padded.length() < size) {
            padded.insert(0, padChar);
        }
        return padded.toString();
    }

    public static ImageIcon resizeImageIcon(ImageIcon imageIcon, double factor) {
        Image image = imageIcon.getImage();

        int width = (int) (factor * image.getWidth(null));
        int height = (int) (factor * image.getHeight(null));

        Image newImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon newImageIcon = new ImageIcon(newImage);

        return newImageIcon;
    }

    public static Color getTableCellBackgroundColor(boolean isSelected, int row) {
        Color color;
        Color selectionBackground = UIManager.getColor("Table.selectionBackground");
//        Color selectionBackground = new Color(57,105,138);  // Nimbus Background Selection Color
        Color alternate = UIManager.getColor("Table.alternateRowColor");

        if (isSelected) {
            color = selectionBackground;
        } else {
            if (row % 2 == 0) {
                color = alternate;
            } else {
                color = Color.white;
            }
        }
        return color;
    }


    public static double showSide(JSplitPane split, boolean leftUpper) {
        return showSide(split, leftUpper, 0, null);
    }

    public static double showSide(JSplitPane split, boolean leftUpper, int speedInMillis, TimelineCallback callback) {
        double stop = leftUpper ? 0.0d : 1.0d;
        return showSide(split, stop, speedInMillis, callback);
    }

//    public static double showSide(JSplitPane split, boolean leftUpper, int speedInMillis, Closure whatToDoWhenDone) {
//        double stop = leftUpper ? 0.0d : 1.0d;
//        final Closure closure = whatToDoWhenDone;
//
//        TimelineCallbackAdapter adapter = new TimelineCallbackAdapter(){
//            @Override
//            public void onTimelineStateChanged(Timeline.TimelineState oldState, Timeline.TimelineState newState, float durationFraction, float timelinePosition) {
//                if (newState == Timeline.TimelineState.DONE){
//                    closure.execute(null);
//                }
//            }
//        };
//        return showSide(split, stop, speedInMillis, adapter);
//    }

    public static double showSide(JSplitPane split, boolean leftUpper, int speedInMillis) {
        double stop = leftUpper ? 0.0d : 1.0d;
        return showSide(split, stop, speedInMillis, null);
    }

    public static double showSide(JSplitPane split, Double pos) {
        return showSide(split, pos, 0, null);
    }

    public static double showSide(JSplitPane split, Double pos, int speedInMillis) {
        return showSide(split, pos, speedInMillis, null);
    }


    /**
     * Setzt eine Split Pane (animiert oder nicht animiert) auf eine entsprechende Position (Prozentual zwischen 0 und 1)
     *
     * @param split
     * @param pos
     * @param speedInMillis
     * @return Die neue, relative Position (zwischen 0 und 1)
     */
    public static double showSide(JSplitPane split, Double pos, int speedInMillis, TimelineCallback callback) {

        if (OPDE.isAnimation() && speedInMillis > 0) {
            OPDE.debug("ShowSide double-version");
            Object start;
            Object stop;

            if (isMac() || isWindows()) {
                start = split.getDividerLocation();
                stop = getDividerInAbsolutePosition(split, pos);
            } else {
                OPDE.debug("*nix running");
                start = new Double(split.getDividerLocation()) / new Double(getDividerInAbsolutePosition(split, 1.0d));
                stop = pos;
            }

            OPDE.debug(start.getClass().toString());
            OPDE.debug(stop.getClass().toString());

            final Timeline timeline1 = new Timeline(split);
            timeline1.setEase(new Spline(0.9f));
            timeline1.addPropertyToInterpolate("dividerLocation", start, stop);
            timeline1.setDuration(speedInMillis);

            if (callback != null) {
                timeline1.addCallback(callback);
            }


            timeline1.play();
        } else {
            split.setDividerLocation(pos);
        }
        return pos;
    }

    public static double showSide(JSplitPane split, Integer pos, int speedInMillis) {
        return showSide(split, pos, speedInMillis, null);
    }

    public static double showSide(JSplitPane split, Integer pos) {
        return showSide(split, pos, 0, null);
    }

    public static double showSide(JSplitPane split, Integer pos, int speedInMillis, TimelineCallback callback) {

        if (OPDE.isAnimation() && speedInMillis > 0) {
            OPDE.debug("ShowSide int-version");
            Object start;
            Object stop;

            if (isMac() || isWindows()) {
                start = split.getDividerLocation();
                stop = pos;
            } else {
                OPDE.debug("*nix running");
                start = new Double(split.getDividerLocation()) / new Double(getDividerInAbsolutePosition(split, 1.0d));
                stop = getDividerInRelativePosition(split, pos);
            }
            OPDE.debug(start.getClass().toString());
            OPDE.debug(stop.getClass().toString());

            final Timeline timeline1 = new Timeline(split);
            timeline1.setEase(new Spline(0.9f));
            timeline1.addPropertyToInterpolate("dividerLocation", start, stop);
            timeline1.setDuration(speedInMillis);

            if (callback != null) {
                timeline1.addCallback(callback);
            }

            timeline1.play();
        } else {
            split.setDividerLocation(pos);
        }
        return getDividerInRelativePosition(split, pos);
    }

//    public static double showSide(JSplitPane split, int pos, int speedInMillis) {
//
//        int start = split.getDividerLocation();
//        int stop = pos;
//
//        if (Main.isAnimation()) {
//            final Timeline timeline1 = new Timeline(split);
//            timeline1.setEase(new Spline(0.9f));
//            timeline1.addPropertyToInterpolate("dividerLocation", start, stop);
//            timeline1.setDuration(speedInMillis);
//            timeline1.play();
//        } else {
//            split.setDividerLocation(stop);
//        }
//
//        return new Double(stop) / new Double(split.getWidth());
//
//    }

    public static Integer getDividerInAbsolutePosition(JSplitPane mysplit, double pos) {
        int max;
        if (mysplit.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
            max = mysplit.getWidth();
        } else {
            max = mysplit.getHeight();
        }
        return new Double(max * pos).intValue();
    }

    public static Double getDividerInRelativePosition(JSplitPane mysplit, int pos) {
        int max;
        if (mysplit.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
            max = mysplit.getWidth();
        } else {
            max = mysplit.getHeight();
        }
        OPDE.debug("DIVIDER IN ABSOLUTE POSITION: " + pos);
        OPDE.debug("DIVIDER MAX POSITION: " + max);
        OPDE.debug("DIVIDER IN RELATIVE POSITION: " + new Double(pos) / new Double(max));
        return new Double(pos) / new Double(max);
    }


    // http://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/
    public static boolean isWindows() {

        String os = System.getProperty("os.name").toLowerCase();
        //windows
        return (os.indexOf("win") >= 0);

    }

    // http://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/
    public static boolean isMac() {

        String os = System.getProperty("os.name").toLowerCase();
        //Mac
        return (os.indexOf("mac") >= 0);

    }

    // http://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/
    public static boolean isUnix() {

        String os = System.getProperty("os.name").toLowerCase();
        //linux or unix
        return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);

    }

    public static void fadeout(JLabel lbl) {
        //lbl.setIcon(null);
        final JLabel lbl1 = lbl;
        final Color foreground = lbl.getForeground();
        Timeline timeline1 = new Timeline(lbl);
        timeline1.addPropertyToInterpolate("foreground", lbl.getForeground(), lbl.getBackground());
        timeline1.setDuration(500);
        timeline1.addCallback(new TimelineCallback() {
            @Override
            public void onTimelineStateChanged(Timeline.TimelineState timelineState, Timeline.TimelineState timelineState1, float v, float v1) {
                if (timelineState1 == Timeline.TimelineState.DONE) {
                    lbl1.setText(null);
                    lbl1.setForeground(foreground);
                    lbl1.setIcon(null);
                }
            }

            @Override
            public void onTimelinePulse(float v, float v1) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        timeline1.play();
    }

    public static void fadein(JLabel lbl, String text) {
        final JLabel lbl1 = lbl;
        final String text1 = text;
        final Color foreground = lbl1.getForeground();
        final Color background = lbl1.getBackground();

        if (!SYSTools.catchNull(lbl.getText()).isEmpty()) {
            Timeline timeline1 = new Timeline(lbl1);
            timeline1.addPropertyToInterpolate("foreground", foreground, background);
            timeline1.setDuration(500);
            timeline1.addCallback(new TimelineCallbackAdapter() {
                @Override
                public void onTimelineStateChanged(Timeline.TimelineState timelineState, Timeline.TimelineState timelineState1, float v, float v1) {
                    if (timelineState1 == Timeline.TimelineState.DONE) {
                        lbl1.setIcon(null);
                        lbl1.setForeground(background);
                        lbl1.setText(text1);
                        Timeline timeline2 = new Timeline(lbl1);
                        timeline2.addPropertyToInterpolate("foreground", lbl1.getBackground(), foreground);
                        timeline2.setDuration(700);
                        timeline2.play();
                    }
                }
            });
            timeline1.play();
        } else {
            lbl1.setForeground(background);
            lbl1.setText(text);
            Timeline timeline1 = new Timeline(lbl1);
            timeline1.addPropertyToInterpolate("foreground", lbl1.getBackground(), foreground);
            timeline1.setDuration(700);
            timeline1.play();
        }
    }

    public static Timeline flashLabel(JLabel lbl1, String text) {
        return flashLabel(lbl1, text, 0);
    }

    public static Timeline flashLabel(JLabel lbl1, String text, int times) {
        return flashLabel(lbl1, text, times, Color.RED);
    }

    public static Timeline flashLabel(JLabel lbl1, String text, int times, Color flashColor) {

        final JLabel lbl = lbl1;
        final Color oldColor = Color.black; //lbl1.getForeground();
        final String oldText = lbl1.getText();
        //OPDE.debug("oldText: " + oldText);
        lbl.setText(text);
        Timeline textmessageTL = new Timeline(lbl);
        textmessageTL.addPropertyToInterpolate("foreground", oldColor, flashColor);
        textmessageTL.setDuration(600);

        textmessageTL.addCallback(new TimelineCallbackAdapter() {
            @Override
            public void onTimelineStateChanged(Timeline.TimelineState oldState, Timeline.TimelineState newState, float durationFraction, float timelinePosition) {
                OPDE.debug(newState);
                if (newState == Timeline.TimelineState.CANCELLED || newState == Timeline.TimelineState.DONE) {
                    lbl.setText(oldText);
                    lbl.setForeground(Color.black);
                    OPDE.debug("flashLabel cancelled or done. Label set to: " + oldText);
                }
            }
        });

        if (times == 0) {
            textmessageTL.playLoop(Timeline.RepeatBehavior.REVERSE);
        } else {
            textmessageTL.playLoop(times, Timeline.RepeatBehavior.REVERSE);
        }
        return textmessageTL;
    }


    public static void fadeinout(JLabel lbl, String text) {
        final JLabel lbl1 = lbl;
        final Color foreground = Color.black;
        final Color background = lbl.getBackground();
        lbl.setForeground(lbl.getBackground());
        lbl.setText(text);
        Timeline timeline1 = new Timeline(lbl);
        timeline1.addPropertyToInterpolate("foreground", background, foreground);
        timeline1.setDuration(400);
        timeline1.addCallback(new TimelineCallback() {
            @Override
            public void onTimelineStateChanged(Timeline.TimelineState timelineState, Timeline.TimelineState timelineState1, float v, float v1) {
                if (timelineState1 == Timeline.TimelineState.DONE) {
                    lbl1.setText("");
                    lbl1.setForeground(foreground);
                    lbl1.setIcon(null);
                }
            }

            @Override
            public void onTimelinePulse(float v, float v1) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        timeline1.playLoop(2, Timeline.RepeatBehavior.REVERSE);
    }

    public static void packTable(JTable table, int margin) {
        for (int colindex = 0; colindex < table.getColumnCount(); colindex++) {
            packColumn(table, colindex, margin);
        }
    }

    /*
     * http://exampledepot.com/egs/javax.swing.table/PackCol.html
     */
    public static void packColumn(JTable table, int vColIndex, int margin) {
        DefaultTableColumnModel colModel = (DefaultTableColumnModel) table.getColumnModel();
        TableColumn col = colModel.getColumn(vColIndex);
        int width = 0;

        // Get width of column header
        TableCellRenderer renderer = col.getHeaderRenderer();
        if (renderer == null) {
            renderer = table.getTableHeader().getDefaultRenderer();
        }
        Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);
        width = comp.getPreferredSize().width;

        // Get maximum width of column data
        for (int r = 0; r < table.getRowCount(); r++) {
            renderer = table.getCellRenderer(r, vColIndex);
            comp = renderer.getTableCellRendererComponent(
                    table, table.getValueAt(r, vColIndex), false, false, r, vColIndex);
            width = Math.max(width, comp.getPreferredSize().width);
        }

        // Add margin
        width += 2 * margin;
        // Set the width
        col.setPreferredWidth(width);
    }

    public static String getThrowableAsHTML(Throwable exc) {
        String html = "";
        StackTraceElement[] stacktrace = exc.getStackTrace();


        html += "<h1>Ausnahmezustand aufgetreten</h1>";
        html += "<h2>" + exc.getClass().getName() + "</h2>";
        html += "<p>" + exc.getMessage() + "</p>";
        html += "<table border=\"1\" cellspacing=\"0\"><tr>"
                + "<th>Methode</th><th>Zeile</th><th>Klasse</th><th>Datei</th></tr>";


        for (int exception = 0; exception < stacktrace.length; exception++) {
            StackTraceElement element = stacktrace[exception];
            html += "<tr>";
            html += "<td>" + element.getMethodName() + "</td>";
            html += "<td>" + element.getLineNumber() + "</td>";
            html += "<td>" + element.getClassName() + "</td>";
            html += "<td>" + element.getFileName() + "</td>";
            html += "</tr>";
        }
        html += "</table>";

        return html;
    }

    /**
     * @param filePath name of file to open. The file can reside
     *                 anywhere in the classpath
     *                 http://snippets.dzone.com/posts/show/4480
     */
    public static String readFileAsString(String filePath) throws IOException {
        StringBuffer fileData = new StringBuffer(1000);
        FileInputStream fis = new FileInputStream(filePath);
        InputStreamReader reader = new InputStreamReader(fis, "UTF-8");
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        fis.close();
        return fileData.toString();
    }


    public static BigDecimal parseBigDecimal(String txt) {
        BigDecimal bd;
        try {
            bd = BigDecimal.valueOf(Double.parseDouble(txt.replaceAll(",", "\\.")));
        } catch (Exception ex) {
            bd = null;
        }
        return bd;
    }


    public static BigDecimal checkBigDecimal(javax.swing.event.CaretEvent evt, boolean mustBePositive) {
        BigDecimal bd = null;
        JTextComponent txt = (JTextComponent) evt.getSource();
        Action toolTipAction = txt.getActionMap().get("hideTip");
        if (toolTipAction != null) {
            ActionEvent hideTip = new ActionEvent(txt, ActionEvent.ACTION_PERFORMED, "");
            toolTipAction.actionPerformed(hideTip);
        }
        try {
            bd = BigDecimal.valueOf(Double.parseDouble(txt.getText().replaceAll(",", "\\.")));
            if (mustBePositive && bd.compareTo(BigDecimal.ZERO) <= 0) {
                txt.setToolTipText("<html><font color=\"red\"><b>Sie können nur Zahlen größer 0 eingeben</b></font></html>");
                toolTipAction = txt.getActionMap().get("postTip");
                bd = BigDecimal.ONE;
            } else {
                txt.setToolTipText("");
            }

        } catch (NumberFormatException ex) {
            if (mustBePositive) {
                bd = BigDecimal.ONE;
            } else {
                bd = BigDecimal.ZERO;
            }
            txt.setToolTipText("<html><font color=\"red\"><b>Sie haben eine ungültige Zahl eingegeben.</b></font></html>");
            toolTipAction = txt.getActionMap().get("postTip");
            if (toolTipAction != null) {
                ActionEvent postTip = new ActionEvent(txt, ActionEvent.ACTION_PERFORMED, "");
                toolTipAction.actionPerformed(postTip);
            }
        }
        return bd;
    }

    public static BigDecimal checkBigDecimal(String txt) {
        BigDecimal bd = null;
        try {
            NumberFormat nf = DecimalFormat.getNumberInstance();
            Number number = nf.parse(txt);

            if (number instanceof Long) {
                bd = BigDecimal.valueOf(number.longValue());
            } else if (number instanceof Double) {
                bd = BigDecimal.valueOf(number.doubleValue());
            } else if (number instanceof BigDecimal) {
                bd = (BigDecimal) number;
            }

        } catch (ParseException ex) {
            OPDE.debug(ex);
            // Pech
        }
        return bd;
    }

    public static String left(String text, int size) {
        int originalLaenge = text.length();
        int max = Math.min(size, originalLaenge);
        text = text.substring(0, max - 1);
        if (max < originalLaenge) {
            text += "...";
        }
        return text;
    }

    public static BigDecimal parseCurrency(String test) {
        NumberFormat nf = DecimalFormat.getCurrencyInstance();
        test = test.replace(".", ",");
        Number num;
        try {
            num = nf.parse(test);
        } catch (ParseException ex) {
            try {
                String test1 = test + " " + SYSConst.eurosymbol;
                num = nf.parse(test1);
            } catch (ParseException ex1) {
                num = null;
//                try {
//                    test += " " + SYSConst.eurosymbol;
//                    num = nf.parse(test);
//                } catch (ParseException ex2) {
//                    lblMessage.setText(timeDF.format(new Date()) + " Uhr : " + "Bitte geben Sie Euro Beträge in der folgenden Form ein: '10,0 " + SYSConst.eurosymbol + "'");
//                }
            }
        }

        BigDecimal betrag = null;
        if (num != null) {
            if (num instanceof Long) {
                betrag = new BigDecimal(num.longValue());
            } else if (num instanceof Double) {
                betrag = new BigDecimal(num.doubleValue());
            } else if (num instanceof BigDecimal) {
                betrag = (BigDecimal) num;
            } else {
                betrag = null;
            }
        }


        return betrag;
    }


    public static void removeSearchPanels(JPanel panelSearch, int positionToAddPanels) {
        if (panelSearch.getComponentCount() > positionToAddPanels) {
            int count = panelSearch.getComponentCount();
            for (int i = count - 1; i >= positionToAddPanels; i--) {
                panelSearch.remove(positionToAddPanels);
            }
        }
    }

}
