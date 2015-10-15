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
package op.tools;

import com.jidesoft.swing.JideSplitPane;
import com.sun.istack.internal.Nullable;
import entity.files.SYSFilesTools;
import entity.system.SYSPropsTools;
import entity.system.Users;
import op.OPDE;
import op.system.AppInfo;
import op.threads.DisplayMessage;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;
import org.joda.time.DateTime;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SYSTools {

    public static final int INDEX_LASTNAME = 0;
    public static final int INDEX_FIRSTNAME_FEMALE = 1;
    public static final int INDEX_FIRSTNAME_MALE = 2;

    public static final boolean LEFT_UPPER_SIDE = false;
    public static final boolean RIGHT_LOWER_SIDE = true;
    public static final boolean MUST_BE_POSITIVE = true;

    public static final int SPEED_NORMAL = 500;
    public static final int SPEED_SLOW = 700;

//    private static final Logger logger = Logger.getLogger(this.getClass());


    public static ListCellRenderer getDefaultRenderer() {
        return new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                String text;
                if (o == null) {
                    text = SYSTools.xx("misc.commands.>>noselection<<");
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
            }
        };
    }

    public static String[] translate(String[] list) {
        String[] translated = new String[list.length];
        int i = 0;
        for (String element : list) {
            translated[i] = xx(element);
            i++;
        }
        return translated;
    }

    public static void clear(Collection coll) {
        if (coll != null) {
            coll.clear();
        }
    }

    public static void clear(Map map) {
        if (map != null) {
            map.clear();
        }
    }

    public static Properties load(String text) {
        Properties props = new Properties();
        try {
            StringReader reader = new StringReader(text);
            props.load(reader);
            reader.close();
        } catch (IOException ex) {
            OPDE.fatal(ex);
        }
        return props;
    }

    public static void center(java.awt.Window w) {
        Dimension us = w.getSize();
        Dimension them = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int newX = (them.width - us.width) / 2;
        int newY = (them.height - us.height) / 2;
        w.setLocation(newX, newY);
    } // center

    public static void showTimeDifference(long begin) {
        if (!OPDE.isDebug()) return;
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();

        OPDE.debug(ste[2].toString() + ": " + (System.currentTimeMillis() - begin) + " ms");
    }

    public static void handleIntegerFocusLost(FocusEvent evt, int min, int max, int def) {
        int myInt;
        try {
            myInt = Integer.parseInt(((JTextField) evt.getSource()).getText());
        } catch (NumberFormatException ex) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("misc.msg.wrongentry")));
            myInt = def;
        }
        if (myInt < min) {
            myInt = min;
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("misc.msg.entryTooSmall")));
        }
        if (myInt > max) {
            myInt = max;
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("misc.msg.entryTooBig")));
        }

        ((JTextField) evt.getSource()).setText(Integer.toString(myInt));
    }

    public static void handleBigDecimalFocusLost(FocusEvent evt, BigDecimal min, BigDecimal max, BigDecimal def) {
        BigDecimal myBD;
        try {
            myBD = BigDecimal.valueOf(Double.parseDouble(assimilateDecimalSeparators(((JTextField) evt.getSource()).getText())));
        } catch (NumberFormatException ex) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("misc.msg.wrongentry")));
            myBD = def;
        }
        if (myBD.compareTo(min) < 0) {
            myBD = min;
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("misc.msg.entryTooSmall")));
        }
        if (myBD.compareTo(max) > 0) {
            myBD = max;
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("misc.msg.entryTooBig")));
        }

        ((JTextField) evt.getSource()).setText(myBD.setScale(2, RoundingMode.HALF_UP).toString());
    }

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
     * @param input      - Eingang
     * @param find       - Muster nach dem gesucht werden soll
     * @param replace    - Ersatzzeichenkette
     * @param ignoreCase
     * @return String mit Ersetzung
     */
    public static String replace(String input, String find, String replace, boolean ignoreCase) {
        int s = 0;
        int e = 0;
        String input_case_adjusted = ignoreCase ? input.toLowerCase() : input;
        String find_case_adjusted = ignoreCase ? find.toLowerCase() : find;

        StringBuffer result = new StringBuffer();

        while ((e = input_case_adjusted.indexOf(find_case_adjusted, s)) >= 0) {
            result.append(input.substring(s, e));
            result.append(replace);
            s = e + find_case_adjusted.length();
        }
        result.append(input.substring(s));
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

    public static String anonymizeName(String in, int arrayindex) {
        String name = in;
        if (OPDE.isAnonym()) {
            String ersterBuchstabe = in.toLowerCase().substring(0, 1);
            int random = in.charAt(1) % 5;
            name = ((String[]) OPDE.anonymize[arrayindex].get(ersterBuchstabe))[random];
        }
        return name;
    }

    public static String anonymizeRID(String in) {
        String rid = in;
        if (OPDE.isAnonym()) {
            Random rnd = new Random(System.currentTimeMillis());

            char c1 = (char) (65 + rnd.nextInt(25));
            char c2 = (char) (65 + rnd.nextInt(25));

            rid = new String(new char[]{c1, c2}) + rnd.nextInt(9);
        }
        return rid;
    }

    public static String anonymizeString(String in) {
        String out = "[" + SYSTools.xx("misc.msg.anon") + "]";
        if (!OPDE.isAnonym()) {
            out = in;
        }
        return out;
    }

    public static Date anonymizeDate(Date in) {
        Date result = in;
        if (OPDE.isAnonym()) {
            Random rnd = new Random(System.nanoTime());
            DateTime dt = new DateTime(in);
            int factor = rnd.nextBoolean() ? -1 : 1;
            result = dt.plusDays(rnd.nextInt(300) * factor).plusYears(rnd.nextInt(5) * factor).toDate();
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


    public static void markAllTxt(JTextField jtf) {
        jtf.setSelectionStart(0);
        jtf.setSelectionEnd(jtf.getText().length());
    }

    public static DefaultListModel list2dlm(List list) {
        DefaultListModel dlm = new DefaultListModel();
        if (list != null) {
            for (Object o : list) {
                dlm.addElement(o);
            }
        }
        return dlm;
    }

    public static <T> DefaultComboBoxModel<T> list2cmb(List<T> list) {
        DefaultComboBoxModel cmb = new DefaultComboBoxModel<T>();
        if (list != null) {
            for (T t : list) {
                cmb.addElement(t);
            }
        }
        return cmb;
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
    public static byte[] createChecksum(File file) throws Exception {
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

//    /**
//     * Wählt in einer ComboBox aus ListElements das Element mit einem bestimmten String aus.
//     */
//    public static void selectInComboBox(JComboBox j, String pattern, boolean useValue) {
//        ComboBoxModel cbm = (ComboBoxModel) j.getModel();
//        for (int i = 0; i < cbm.getSize(); i++) {
//            ListElement le = (ListElement) cbm.getElementAt(i);
//            if (useValue) {
//                if (le.getValue().equalsIgnoreCase(pattern)) {
//                    j.setSelectedIndex(i);
//                    return;
//                }
//            } else {
//                if (le.getData().equalsIgnoreCase(pattern)) {
//                    j.setSelectedIndex(i);
//                    return;
//                }
//
//            }
//        }
//    }

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
            if (result.isEmpty()) {
                result = neutral;
            }
            result = xx(result);
        }
        return result;
    }

    public static String catchNull(String in) {
        return (in == null ? "" : xx(in.trim()));
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
        if (!catchNull(in).isEmpty()) {
            result = prefix + catchNull(in) + suffix;
        }
        return result;
    }

    /**
     * Gibt eine einheitliche Titelzeile für alle Fenster zurück.
     */
    public static String getWindowTitle(String moduleName) {
        if (!moduleName.isEmpty()) {
            moduleName = ", " + SYSTools.xx(moduleName);
        }
        return OPDE.getAppInfo().getProgname() + moduleName + ", v" + OPDE.getAppInfo().getVersion();
    }

    public static void expandAll(JTree tree) {
        int row = 0;
        while (row < tree.getRowCount()) {
            tree.expandRow(row);
            row++;
        }
    }

    /**
     * taken from: http://www.torsten-horn.de/techdocs/java-net.htm#SimplePing
     *
     * @param sHost
     * @param sPort
     * @return
     */
    public static String socketping(String sHost, String sPort) throws IOException {
        String result = null;

        InetAddress host = InetAddress.getByName(sHost);
        int port = !catchNull(sPort).isEmpty() ? Integer.parseInt(sPort) : 80;
        long tm = System.nanoTime();
        Socket so = new Socket();
        so.connect(new InetSocketAddress(host, port), 5000);
        so.close();
        tm = (System.nanoTime() - tm) / 1000000L;
        result = "Connection ok (port " + port + ", time = " + tm + " ms). \n" +
                "Host Address = " + host.getHostAddress() + "\n" +
                "Host Name    = " + host.getHostName();

        return result;
    }


//    public static DefaultListModel cmb2lst(DefaultComboBoxModel dcbm) {
//        DefaultListModel dlm = new DefaultListModel();
//        for (int i = 0; i < dcbm.getSize(); i++) {
//            dlm.addElement(dcbm.getElementAt(i));
//        }
//        return dlm;
//    }

//    public static File[] chooseFile(Component parent, boolean multiselection) {
//        File[] result = null;
//        String cname = parent.getClass().getName();
//        String startdir = System.getProperty("user.home");
//        if (OPDE.getProps().containsKey("DIR." + cname)) {
//            startdir = OPDE.getProps().getProperty("DIR." + cname);
//        }
//        JFileChooser jfc = new JFileChooser(startdir);
//        jfc.setMultiSelectionEnabled(multiselection);
//        int response = jfc.showOpenDialog(parent);
//        if (response == JFileChooser.APPROVE_OPTION) {
//            if (multiselection) {
//                result = jfc.getSelectedFiles();
//            } else {
//                result = new File[]{jfc.getSelectedFile()};
//            }
//
//            //String newPath = result[0].getAbsolutePath();
//            String myPath = result[0].getParent();
//            SYSPropsTools.storeProp("DIR." + cname, myPath, OPDE.getLogin().getUser());
//
//        }
//        return result;
//    }


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

    public static String getAsHTML(BigDecimal bd) {
        String dbl = bd.toPlainString();
        if (dbl.endsWith(".00")) {
            dbl = dbl.substring(0, dbl.length() - 3);
        } else if (dbl.equals("0.50")) {
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

    /**
     * Fügt html Tags vor und hinter den Eingangsstring ein.
     *
     * @param in Eingangsstring
     * @return String mit HTML Erweiterungen.
     */
    public static String toHTML(String in) {
        String out = null;

        if (!catchNull(in).equals("")) {
            out = "<html>"
                    + "<head>"
                    + "<title>" + OPDE.getAppInfo().getProgname() + "</title>"
                    + OPDE.getCSS()
                    + "</head>"
                    + "<body>" + xx(in) + "</body></html>";
        }
        return htmlUmlautConversion(out);
    }


    public static String toHTMLForScreen(String in) {
        String out = null;

        if (!catchNull(in).isEmpty()) {
            out = "<html>"
                    + xx(in) + "</html>";
        }
        return htmlUmlautConversion(out);
    }


    public static String getHTMLSubstring(String in, int maximum) {
        if (in == null || in.isEmpty()) {
            return "";
        }
        String htmlshort = in.replaceAll("\\<.*?\\>", " "); // Alle HTML Tags entfernen.
        int max = Math.min(maximum, htmlshort.length() - 1); // Nur die ersten 40 Zeichen zeigen...
        return htmlshort.substring(0, max) + (htmlshort.length() - 1 <= max ? "" : "...");
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

    /**
     * removes any dangerous char or html stuff from a string.
     *
     * @param in
     * @return
     */
    public static String tidy(String in) {

        if (in == null) {
            return null;
        }

        String result = in.replaceAll("\\<[^>]*>", "");

        result = result.replaceAll("[\\>\\<]", "");
        result = result.replaceAll("\\&.*\\;", "");

        return result.trim();
    }

    public static String escapeXML(String in) {
        String result = in;
        result = SYSTools.replace(result, "\"", "&quot;", false);
        result = SYSTools.replace(result, "<", "&lt;", false);
        result = SYSTools.replace(result, ">", "&gt;", false);
        result = SYSTools.replace(result, "&", "&amp;", false);
        return result;
    }

    public static String unescapeXML(String in) {
        String result = in;
        result = SYSTools.replace(result, "&quot;", "\"", false);
        result = SYSTools.replace(result, "&lt;", "<", false);
        result = SYSTools.replace(result, "&gt;", ">", false);
        result = SYSTools.replace(result, "&amp;", "&", false);
        return result;
    }

    /**
     * Tauscht in einem String alle Umlaute gegen den entsprechend HTML Tag aus.
     *
     * @param in String
     * @return geänderter String
     */
    public static String htmlUmlautConversion(String in) {
        if (in == null) {
            return null;
        }
        String result = in;
        result = SYSTools.replace(result, "Ä", "&Auml;", false);
        result = SYSTools.replace(result, "ä", "&auml;", false);
        result = SYSTools.replace(result, "Ö", "&Ouml;", false);
        result = SYSTools.replace(result, "ö", "&ouml;", false);
        result = SYSTools.replace(result, "Ü", "&Uuml;", false);
        result = SYSTools.replace(result, "ü", "&uuml;", false);
        result = SYSTools.replace(result, "ß", "&szlig;", false);
        result = SYSTools.replace(result, "°", "&deg;", false); //&deg;
        result = SYSTools.replace(result, "µ", "&micro;", false); //micro
        return result;
    }

    public static String removeTags(String input, String tag) {
        String result = input;
        result = SYSTools.replace(result, "<" + tag + ">", "", false);
        result = SYSTools.replace(result, "</" + tag + ">", "", false);
        return result;
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
            result = replace(text, nachname, "[anonym]", false);
        }
        return result;
    }


    public static String padL(String str, int size, String padChar) {
        StringBuilder padded = new StringBuilder(str);
        while (padded.length() < size) {
            padded.insert(0, padChar);
        }
        return padded.toString();
    }

    public static String padC(String str, int size, String padChar) {
        StringBuilder padded = new StringBuilder(str);
        while (padded.length() < size) {
            padded.insert(0, padChar);
            // Dadurch sitzt das Ergebnis nicht unbedingt in der Mitte. Aber doch so mittig wie möglich.
            if (padded.length() < size) {
                padded.append(padChar);
            }
        }
        return padded.toString();
    }

    public static String padR(String str, int size, String padChar) {
        StringBuilder padded = new StringBuilder(str);
        while (padded.length() < size) {
            padded.append(padChar);
        }
        return padded.toString();
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
        return showSide(split, leftUpper, 0);
    }

    public static double showSide(JSplitPane split, boolean leftUpper, int speedInMillis) {
        double stop = leftUpper ? 0.0d : 1.0d;
        return showSide(split, stop, speedInMillis);
    }

    public static double showSide(JSplitPane split, Double pos) {
        return showSide(split, pos, 0);
    }


    /**
     * Setzt eine Split Pane (animiert oder nicht animiert) auf eine entsprechende Position (Prozentual zwischen 0 und 1)
     *
     * @param split
     * @param stop
     * @param speedInMillis
     * @return Die neue, relative Position (zwischen 0 und 1)
     */
    public static double showSide(final JSplitPane split, final double stop, int speedInMillis) {

        if (OPDE.isAnimation() && speedInMillis > 0) {
            OPDE.debug("ShowSide double-version");
            final double start = new Double(split.getDividerLocation()) / new Double(getDividerInAbsolutePosition(split, 1.0d));

            final TimingSource ts = new SwingTimerTimingSource();
            Animator.setDefaultTimingSource(ts);
            ts.init();
            Animator animator = new Animator.Builder().setInterpolator(new AccelerationInterpolator(0.2, 0.2)).setDuration(speedInMillis, TimeUnit.MILLISECONDS).setRepeatCount(1).setStartDirection(Animator.Direction.FORWARD).addTarget(new TimingTargetAdapter() {
                double differenz = stop - start;

                @Override
                public void timingEvent(Animator animator, double fraction) {
                    split.setDividerLocation(start + (differenz * fraction));
                }

            }).build();

            animator.start();

        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    split.setDividerLocation(stop);
                }
            });
        }
        return stop;
    }

    public static Integer getDividerInAbsolutePosition(JSplitPane mysplit, double pos) {
        int max;
        if (mysplit.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
            max = mysplit.getWidth();
        } else {
            max = mysplit.getHeight();
        }
        return new Double(max * pos).intValue();
    }

    public static Integer getDividerInAbsolutePosition(JideSplitPane mysplit, double pos) {
        int max;

        if (mysplit.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
            max = mysplit.getWidth();
        } else {
            max = mysplit.getHeight();
        }
        return new Double(max * pos).intValue();
    }


    public static Double getDividerInRelativePosition(JideSplitPane mysplit) {
        int max;
        int pos = mysplit.getDividerLocation(0);

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


    public static String getThrowableAsHTML(Throwable exc) {
        String html = "";
        StackTraceElement[] stacktrace = exc.getStackTrace();

        html += SYSConst.html_h1("mail.errormail.attachment.line1");
        html += SYSConst.html_h2(exc.getClass().getName());
        html += SYSConst.html_paragraph(exc.getMessage());


        if (OPDE.getMainframe().getCurrentResident() != null) {
            html += SYSConst.html_h3("ResID: " + OPDE.getMainframe().getCurrentResident().getRID());
        }

        html += SYSConst.html_h3(OPDE.getMainframe().getCurrentVisiblePanel().getInternalClassID());

        String table = SYSConst.html_table_th("mail.errormail.attachment.tab.col1") +
                SYSConst.html_table_th("mail.errormail.attachment.tab.col2") +
                SYSConst.html_table_th("mail.errormail.attachment.tab.col3") +
                SYSConst.html_table_th("mail.errormail.attachment.tab.col4");

        for (int exception = 0; exception < stacktrace.length; exception++) {
            StackTraceElement element = stacktrace[exception];
            table += SYSConst.html_table_tr(
                    SYSConst.html_table_td(element.getMethodName()) +
                            SYSConst.html_table_td(Integer.toString(element.getLineNumber())) +
                            SYSConst.html_table_td(element.getClassName()) +
                            SYSConst.html_table_td(element.getFileName())
            );
        }

        html += SYSConst.html_table(table, "1");


        // Possible Cause
        if (exc.getCause() != null) {
            html += SYSConst.html_h3("Caused by: " + exc.getCause().getMessage());
            StackTraceElement[] stacktrace1 = exc.getCause().getStackTrace();
            String table1 = SYSConst.html_table_th("mail.errormail.attachment.tab.col1") +
                    SYSConst.html_table_th("mail.errormail.attachment.tab.col2") +
                    SYSConst.html_table_th("mail.errormail.attachment.tab.col3") +
                    SYSConst.html_table_th("mail.errormail.attachment.tab.col4");

            for (int exception = 0; exception < stacktrace1.length; exception++) {
                StackTraceElement element = stacktrace1[exception];
                table1 += SYSConst.html_table_tr(
                        SYSConst.html_table_td(element.getMethodName()) +
                                SYSConst.html_table_td(Integer.toString(element.getLineNumber())) +
                                SYSConst.html_table_td(element.getClassName()) +
                                SYSConst.html_table_td(element.getFileName())
                );
            }

            html += SYSConst.html_table(table1, "1");
        }


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


    public static BigDecimal checkBigDecimal(javax.swing.event.CaretEvent evt, boolean nees2BePositive) {
        BigDecimal bd = null;
        JTextComponent txt = (JTextComponent) evt.getSource();
        Action toolTipAction = txt.getActionMap().get("hideTip");
        if (toolTipAction != null) {
            ActionEvent hideTip = new ActionEvent(txt, ActionEvent.ACTION_PERFORMED, "");
            toolTipAction.actionPerformed(hideTip);
        }
        try {
            OPDE.debug(txt.getText());
            OPDE.debug(assimilateDecimalSeparators(txt.getText()));

            bd = parseDecimal(txt.getText());

//            bd = BigDecimal.valueOf(Double.parseDouble(assimilateDecimalSeparators(txt.getText())));
            OPDE.debug(bd);
            if (nees2BePositive && bd.compareTo(BigDecimal.ZERO) <= 0) {
                txt.setToolTipText("<html><font color=\"red\"><b>" + SYSTools.xx("misc.msg.invalidnumber") + "</b></font></html>");
                toolTipAction = txt.getActionMap().get("postTip");
                bd = BigDecimal.ONE;
            } else {
                txt.setToolTipText("");
            }

        } catch (NumberFormatException ex) {
            if (nees2BePositive) {
                bd = BigDecimal.ONE;
            } else {
                bd = BigDecimal.ZERO;
            }
            txt.setToolTipText("<html><font color=\"red\"><b>" + SYSTools.xx("misc.msg.invalidnumber") + "</b></font></html>");
            toolTipAction = txt.getActionMap().get("postTip");
            if (toolTipAction != null) {
                ActionEvent postTip = new ActionEvent(txt, ActionEvent.ACTION_PERFORMED, "");
                toolTipAction.actionPerformed(postTip);
            }
        }
        return bd;
    }

    // fixes #17
    // caused #23 afterwards, so got rid of it
    // todo: remove me
    public static String assimilateDecimalSeparators(String in) {
        return in;
//        if (SYSTools.catchNull(in).isEmpty()) return in;
//
//        char sep = new DecimalFormatSymbols(Locale.getDefault(Locale.Category.FORMAT)).getDecimalSeparator();
//        char replace = '.';
//
//        if (sep == '.') {
//            replace = ',';
//        }
//
//        return in.replace(replace, sep);
    }

    public static BigDecimal checkBigDecimal(String txt) {
        BigDecimal bd = null;

        txt = assimilateDecimalSeparators(txt);
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

    public static Integer checkInteger(String txt) {
        Integer i = null;
        try {
            NumberFormat nf = DecimalFormat.getIntegerInstance();
            Number number = nf.parse(txt);

            if (number instanceof Integer) {
                i = Integer.valueOf(number.intValue());
            } else if (number instanceof Long) {
                i = Integer.valueOf(number.intValue());
            }

        } catch (ParseException ex) {
            OPDE.debug(ex);
            // Pech
        }
        return i;
    }

    public static String left(String text, int size) {
        return left(text, size, "...");
    }

    public static String left(String text, int size, String abrev) {
        //        OPDE.debug("IN: " + text);
        int originalLaenge = text.length();
        int max = Math.min(size, originalLaenge);
        text = text.substring(0, max);
        if (max < originalLaenge) {
            text += abrev;
        }
        return text;
    }

    //FIXES: GitHub #17
    public static BigDecimal parseDecimal(String test) {
        NumberFormat nf = DecimalFormat.getNumberInstance();

        test = assimilateDecimalSeparators(test);
        Number num;
        try {
            num = nf.parse(test);
        } catch (Exception ex) {
            num = null;
        }

        BigDecimal wert = null;
        if (num != null) {
            if (num instanceof Long) {
                wert = new BigDecimal(num.longValue());
            } else if (num instanceof Double) {
                wert = new BigDecimal(num.doubleValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
            } else if (num instanceof BigDecimal) {
                wert = (BigDecimal) num;
            } else {
                wert = null;
            }
        }




        return wert;
    }


    public static BigDecimal parseCurrency(String test) {
        NumberFormat nf = DecimalFormat.getCurrencyInstance();
        test = assimilateDecimalSeparators(test);
        Number num;
        try {
            num = nf.parse(test);
        } catch (ParseException ex) {
            try {
                String test1 = test + " " + SYSConst.eurosymbol;
                num = nf.parse(test1);
            } catch (ParseException ex1) {
                num = null;
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


    /**
     * http://nakkaya.com/2009/11/08/command-line-progress-bar/
     *
     * @param percent
     */
    public static void printProgBar(int percent) {
        StringBuilder bar = new StringBuilder("[");

        for (int i = 0; i < 50; i++) {
            if (i < (percent / 2)) {
                bar.append("=");
            } else if (i == (percent / 2)) {
                bar.append(">");
            } else {
                bar.append(" ");
            }
        }

        bar.append("]   " + percent + "%     ");
        System.out.print("\r" + bar.toString());
    }


    public static void removeSearchPanels(JPanel panelSearch, int positionToAddPanels) {
        if (panelSearch.getComponentCount() > positionToAddPanels) {
            int count = panelSearch.getComponentCount();
            for (int i = count - 1; i >= positionToAddPanels; i--) {
                panelSearch.remove(positionToAddPanels);
            }
        }
    }

    /**
     * compares two objects. a null value is always "smaller" than a non null value.
     *
     * @param one
     * @param two
     * @return 1 if one != null && two == null, -1 if one == null && two != null, 0 if one and two are both == null or both != null.
     */
    public static int nullCompare(Object one, Object two) {
        if (one != null && two == null) {
            return 1;
        }
        if (one == null && two != null) {
            return -1;
        }
        return 0;
    }


    public static String generatePassword(String firstname, String lastname) {
        Random generator = new Random(System.currentTimeMillis());
        return lastname.substring(0, 1).toLowerCase() + firstname.substring(0, 1).toLowerCase() + SYSTools.padL(Integer.toString(generator.nextInt(9999)), 4, "0");
    }

    public static void printpw(String password, Users user) {
        String html;

        try {
            html = SYSTools.readFileAsString(AppInfo.getTemplate(AppInfo.fileNewuser).getAbsolutePath());
        } catch (IOException ie) {
            html = "<body>"
                    + "<h1>Access to Offene-Pflege.de (OPDE)</h1>"
                    + "<br/>"
                    + "<br/>"
                    + "<br/>"
                    + "<h2>For <opde-user-fullname/></h2>"
                    + "<br/>"
                    + "<br/>"
                    + "<br/>"
                    + "<p>UserID: <b><opde-user-userid/></b></p>"
                    + "<p>Password: <b><opde-user-pw/></b></p>"
                    + "<br/>"
                    + "<br/>"
                    + "Please keep this note in a safe place. Don't tell Your password to anyone."
                    + "</body>";
        }

        html = SYSTools.replace(html, "<opde-user-fullname/>", user.getFullname(), false);
        html = SYSTools.replace(html, "<opde-user-userid/>", user.getUID(), false);
        html = SYSTools.replace(html, "<opde-user-pw/>", password, false);
        html = SYSTools.htmlUmlautConversion(html);


        SYSFilesTools.print(html, true);
    }

    public static Collection subtract(Collection coll1, Collection coll2) {
        // http://code.hammerpig.com/find-the-difference-between-two-lists-in-java.html
        Collection result = new ArrayList(coll2);
        result.removeAll(coll1);
        return result;
    }

    public static Collection difference(Collection coll1, Collection coll2) {
        // http://code.hammerpig.com/find-the-difference-between-two-lists-in-java.html
        Collection result = union(coll1, coll2);
        result.removeAll(intersect(coll1, coll2));
        return result;
    }

    public static Collection union(Collection coll1, Collection coll2) {
        // http://code.hammerpig.com/find-the-difference-between-two-lists-in-java.html
        Set union = new HashSet(coll1);
        union.addAll(new HashSet(coll2));
        return new ArrayList(union);
    }


    public static Collection intersect(Collection set1, Collection set2) {
        // http://code.hammerpig.com/find-the-difference-between-two-lists-in-java.html
        Set intersection = new HashSet(set1);
        intersection.retainAll(new HashSet(set2));
        return intersection;
    }

    /**
     * tiny method to automatically find out if the message is a language key or not.
     *
     * @param message
     * @return replaced message or the original message if there is no appropriate language key.
     */
    public static String xx(String message, @Nullable Object... args) {
        if (message == null || message.isEmpty()) return "";

        String title = message;
        try {
            title = String.format(OPDE.lang.getString(message), args);
        } catch (Exception e) {
            // ok, its not a langbundle key
        }
        return title;
    }



    public static void packTable(JTable table, int margin) {
        for (int colindex = 0; colindex < table.getColumnCount(); colindex++) {
            packColumn(table, colindex, margin);
        }
    }

    public static String booleanToString(boolean b){
        return b ? SYSTools.xx("misc.msg.yes") : SYSTools.xx("misc.msg.no");
    }

    /**
     * http://exampledepot.com/egs/javax.swing.table/PackCol.html
     *
     * @param table
     * @param vColIndex
     * @param margin
     */
    public static void packColumn(JTable table, int vColIndex, int margin) {
        TableModel model = table.getModel();
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

        OPDE.debug("packColumn/3: col=" + vColIndex + "  width=" + width);
    }

//    public static void checkForSoftwareupdates() {
////        final String FTPServer = "ftp.offene-pflege.de";
//        final int FTPPort = 21;
//        final String FTPUser = "anonymous";
//        final String FTPPassword = Integer.toString(OPDE.getAppInfo().getBuildnum());
//        final String FTPWorkingDirectory = "/pub/opde";
//        final String FILENAME = "buildnum";
//
//        int remoteBuildnum = -1;
//        String descriptionURL = "";
//        int mybuildnum = OPDE.getAppInfo().getBuildnum();
//        FileTransferClient ftp = null;
//        try {
//            File target = File.createTempFile("opde", ".txt");
//            target.deleteOnExit();
//            ftp = new FileTransferClient();
//
//            ftp.setRemoteHost(OPDE.UPDATE_FTPSERVER);
//            ftp.setUserName(FTPUser);
//            ftp.setPassword(FTPPassword);
//            ftp.setRemotePort(FTPPort);
//            ftp.setTimeout(5000);
//            ftp.connect();
//            ftp.getAdvancedFTPSettings().setConnectMode(FTPConnectMode.PASV);
//            ftp.changeDirectory(FTPWorkingDirectory);
//            ftp.downloadFile(target.getPath(), FILENAME);
//
//            String strRemoteBuildnum = FileUtils.readLines(target).get(0);
//            remoteBuildnum = Integer.parseInt(strRemoteBuildnum);
//
//            descriptionURL = FileUtils.readLines(target).get(1);
//
//            ftp.disconnect();
//        } catch (Exception e) {
//            if (ftp != null && ftp.isConnected()) {
//                try {
//                    ftp.disconnect();
//                } catch (FTPException e1) {
//                    OPDE.error(e1);
//                } catch (IOException e1) {
//                    OPDE.error(e1);
//                }
//            }
//            OPDE.warn(e);
//        }
//
//        OPDE.setUpdateAvailable(remoteBuildnum > mybuildnum, descriptionURL);
//    }

}
