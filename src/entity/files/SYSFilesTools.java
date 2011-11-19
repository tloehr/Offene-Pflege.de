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
package entity.files;

import entity.BWInfo;
import entity.BWerte;
import entity.Bewohner;
import entity.Pflegeberichte;
import entity.verordnungen.Verordnung;
import op.OPDE;
import op.care.sysfiles.DlgNewFile;
import op.tools.DlgException;
import op.tools.SYSTools;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * @author tloehr
 */
public class SYSFilesTools {

    /**
     * Sucht alle Dateien für einen bestimmten Bewohner raus und gibt diesen als Set von SYSFiles zurück.
     *
     * @param bewohner
     * @return
     */
    public static Set<SYSFiles> findByBewohner(Bewohner bewohner) {
        Set<SYSFiles> files = new TreeSet<SYSFiles>();
        Query query;

        query = OPDE.getEM().createNamedQuery("SYSFiles.findByBWKennung", SYSFiles.class);
        query.setParameter("bewohner", bewohner);
        files.addAll(query.getResultList());

        query = OPDE.getEM().createNamedQuery("SYSFiles.findByBWKennung2PB", SYSFiles.class);
        query.setParameter("bewohner", bewohner);
        files.addAll(query.getResultList());

        query = OPDE.getEM().createNamedQuery("SYSFiles.findByBWKennung2BWI", SYSFiles.class);
        query.setParameter("bewohner", bewohner);
        files.addAll(query.getResultList());

        query = OPDE.getEM().createNamedQuery("SYSFiles.findByBWKennung2VER", SYSFiles.class);
        query.setParameter("bewohner", bewohner);
        files.addAll(query.getResultList());

        return files;
    }

    /**
     * <code>putFile(String)</code> speichert eine Datei auf dem FTP Server ab und erstellt eine SYSFiles EntityBean. Die Methode geht dabei wie folgt vor:
     * <ol>
     * <li>Zuerst wird der MD5 der zu speichernden Datei berechnet.</li>
     * <li>Anhand dieser MD5 Signatur wird ermittelt, ob es schon einen entsprechenden Eintrag in der Datenbank gibt.
     * <ul>
     * <li>Wenn ja, dann wird einfach der PK zurückgegeben. Nochmal speichern braucht man das ja nicht. <b>RETURN</b></li>
     * <li>Wenn nicht, dann geht die Bearbeitung weiter.</li>
     * </ul>
     * <li>Erstellen einer SYSFiles EB</li>
     * <li>Senden der Datei an den FTP Server</li>
     * <li>Wenn die letzten beiden Schritte erfolgreich waren, dann wird die neue EB als Ergebnis zurück gegeben. null, bei Fehler.</li>
     * </ol>
     *
     * @param file File Obkjekt der zu speichernden Datei
     * @return EB der neuen Datei. null bei Fehler.
     */
    public static SYSFiles putFile(File file) {
        SYSFiles sysfile = null;

        try {

            FTPClient ftp = new FTPClient();
            ftp.connect(OPDE.getProps().getProperty("FTPServer"), Integer.parseInt(OPDE.getProps().getProperty("FTPPort")));
            ftp.login(OPDE.getProps().getProperty("FTPUser"), OPDE.getProps().getProperty("FTPPassword"));
            ftp.changeWorkingDirectory(OPDE.getProps().getProperty("FTPWorkingDirectory"));
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            String md5 = SYSTools.getMD5Checksum(file);
            Query query = OPDE.getEM().createNamedQuery("SYSFiles.findByMd5");
            query.setParameter("md5", md5);

            // Gibts die Datei schon ?
            if (query.getResultList().isEmpty()) { // nein, noch nicht
                try {
                    long ts = file.lastModified();
                    sysfile = new SYSFiles(file.getName(), md5, new Date(ts), file.length(), OPDE.getLogin().getUser());
                    OPDE.getEM().getTransaction().begin();
                    OPDE.getEM().persist(sysfile);
                    OPDE.getEM().getTransaction().commit();
                    String remoteFilename = Long.toString(sysfile.getOcfid()) + ".opfile";
                    FileInputStream fis = new FileInputStream(file);
                    ftp.storeFile(remoteFilename, fis);
                    fis.close();
                    ftp.disconnect();
                } catch (Exception e1) {
                    OPDE.fatal(e1);
                    /*
                     * Das ist etwas unelegant. Aber leider geht es nicht anders.
                     * An sich hätte ich lieber mit der Transaction.rollback gearbeitet.
                     * Ich brauche aber direkt nach dem Persist den primary key der entity.
                     * Und den krieg ich nur, wenn ich das persist committe.
                     * Falls dann doch noch was schief geht, lösche ich die Entität wieder.
                     */
                    OPDE.getEM().getTransaction().begin();
                    OPDE.getEM().remove(sysfile);
                    OPDE.getEM().getTransaction().commit();
                }
            } else { // Ansonsten die bestehende Datei zurückgeben
                sysfile = (SYSFiles) query.getSingleResult();
            }

        } catch (Exception ex) {
            //new DlgException(ex);
            OPDE.fatal(ex);
        }
        return sysfile;
    }

    /**
     * <code>getFile(SYSFiles file)</code> holt eine Datei vom FTP Server entsprechend der Angaben aus der EntityBean.
     * Die Methode geht dabei wie folgt vor:
     * <ol>
     * <li>Es wird nachgeprüft, ob es einen passenden Eintrag in der DB-Tabelle <i>SYSFiles</i> gibt.</li>
     * <li>Gibt es die Datei schon und hat sie auch die korrekte MD5 Signatur ?
     * <ul>
     * <li>Wenn ja, dann wird einfach ein File auf die Datei zurückgegeben. <b>RETURN</b></li>
     * <li>Wenn nicht, dann geht die Bearbeitung weiter.</li>
     * </ul>
     * <li>Datei wird vom FTP Server geholt.</li>
     * <li>Der Timestamp des letzten Zugriffs wird wieder hergestellt.</li>
     * <li>Wenn die letzten beiden Schritte erfolgreich waren, dann wird ein File als Ergebnis zurückgegeben. null, bei Fehler.</li>
     * </ol>
     *
     * @param sysfile - Datei, die vom FTP Server geholt werden soll.
     * @return File Objekt der geladenen Datei, null bei Fehler.
     */
    public static File getFile(SYSFiles sysfile) {
        File result = null;

        try {

            FTPClient ftp = new FTPClient();

            ftp.connect(OPDE.getProps().getProperty("FTPServer"), Integer.parseInt(OPDE.getProps().getProperty("FTPPort")));
            ftp.login(OPDE.getProps().getProperty("FTPUser"), OPDE.getProps().getProperty("FTPPassword"));
            ftp.changeWorkingDirectory(OPDE.getProps().getProperty("FTPWorkingDirectory"));
            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            String sep = System.getProperties().getProperty("file.separator"); // Fileseparator
            String cachedir = OPDE.getProps().getProperty("opcache");
            // Gibts die Datei schon ?
            File target = new File(cachedir + sep + sysfile.getFilename());
            // Gibts den Download schon ?
            if (target.exists() && SYSTools.getMD5Checksum(target).equals(sysfile.getMd5())) {
                result = target;
            } else { // Nein, muss runtergeladen werden.
                target.delete();
                // Filetransfer...........................
                String remoteFilename = Long.toString(sysfile.getOcfid()) + ".opfile";
                FileOutputStream fos = new FileOutputStream(target);
                ftp.retrieveFile(remoteFilename, fos);
                fos.close();
                target.setLastModified(sysfile.getFiledate().getTime());
                result = target;
            }

            ftp.disconnect();
        } catch (Exception ex) {
            result = null;
            new DlgException(ex);
            OPDE.fatal(ex);
        }
        return result;
    }

    /**
     * <code>updateFile(File, long)</code> aktualisiert eine bestehende Datei auf dem FTP Server gemäß des PK aus der DB-Tabelle <i>OCFiles</i>
     * Der Dateiname wird nicht geändert.
     * Die Methode geht dabei wie folgt vor:
     * <ol>
     * <li>Es wird nachgeprüft, ob es einen passenden Eintrag in der DB-Tabelle <i>OCFiles</i> gibt.</li>
     * <li>Dann wird die MD5 Summe der neuen Datei ermittelt. Stimmt sie mit der bestehenden überein, gibt es nichts zu tun.</li>
     * <li>Wenn nicht, dann wird die Datei auf dem Server gelöscht und anschließend erneut raufgeladen.
     * <li>Eintrag in der DBTabelle <i>OCFiles</i> wird mit der neuen MD5 Summe und den neuen Dateidaten aktualisiert.</li>
     * </ol>
     *
     * @param file  File Objekt der neuen Datei.q
     * @param ocfid pk aus der DB-Tabelle <i>OCFiles</i>
     * @return true, wenn Austausch erfolgreich war. false, wenn nicht.
     */
    public static boolean updateFile(File file, SYSFiles sysfile) {
        boolean success = false;
        try {
            String newmd5 = SYSTools.getMD5Checksum(file);
            OPDE.getEM().getTransaction().begin();
            // Ist überhaupt ein Austausch nötig ?
            if (!sysfile.getMd5().equals(newmd5)) {
                FTPClient ftp = new FTPClient();
                ftp.connect(OPDE.getProps().getProperty("FTPServer"), Integer.parseInt(OPDE.getProps().getProperty("FTPPort")));
                ftp.login(OPDE.getProps().getProperty("FTPUser"), OPDE.getProps().getProperty("FTPPassword"));
                ftp.changeWorkingDirectory(OPDE.getProps().getProperty("FTPWorkingDirectory"));
                ftp.setFileType(FTP.BINARY_FILE_TYPE);

                // neuen Timestamp bestimmen
                long ts = file.lastModified();
                // Löschen.................
                String remoteFilename = Long.toString(sysfile.getOcfid()) + ".opfile";

                ftp.deleteFile(remoteFilename);
                FileInputStream fis = new FileInputStream(file);
                ftp.storeFile(remoteFilename, fis);
                fis.close();

                // Datenbank Eintrag erneuern
                sysfile.setMd5(newmd5);
                sysfile.setFiledate(new Date(ts));
                sysfile.setFilesize(file.length());

                OPDE.getEM().merge(sysfile);
                ftp.disconnect();
                success = true;
            }
            OPDE.getEM().getTransaction().commit();
        } catch (Exception ex) {
            OPDE.fatal(ex);
            OPDE.getEM().getTransaction().rollback();
        }
        return success;
    }

    public static boolean deleteFile(SYSFiles sysfile) {
        boolean success = false;
        OPDE.getEM().getTransaction().begin();
        try {
            FTPClient ftp = new FTPClient();
            ftp.connect(OPDE.getProps().getProperty("FTPServer"), Integer.parseInt(OPDE.getProps().getProperty("FTPPort")));
            ftp.login(OPDE.getProps().getProperty("FTPUser"), OPDE.getProps().getProperty("FTPPassword"));
            ftp.changeWorkingDirectory(OPDE.getProps().getProperty("FTPWorkingDirectory"));
            String remoteFilename = Long.toString(sysfile.getOcfid()) + ".opfile";
            ftp.deleteFile(remoteFilename);
            OPDE.getEM().remove(sysfile);
            ftp.disconnect();
            OPDE.getEM().getTransaction().commit();
            success = true;
        } catch (Exception ex) {
            OPDE.getLogger().debug(ex.getMessage(), ex);
            OPDE.getEM().getTransaction().rollback();
            success = false;
        }
        return success;
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
     * Gibt den Teil eines Dateinamens zurück, der als Extension bezeichnet wird. Also html oder pdf etc.
     *
     * @param name
     * @return
     */
    public static String filenameExtension(String name) {
        int dot = name.lastIndexOf(".");
        return name.substring(dot + 1);
    }

    /**
     * Diese Methode ermittelt zu einer gebenen Datei und einer gewünschten Aktion das passende Anzeigeprogramm.
     * Falls die Desktop API nicht passendes hat, werdne die lokal definierten Anzeigeprogramme verwendet.
     *
     * @param filename
     * @param action
     */
    public static void handleFile(File file, java.awt.Desktop.Action action) {
        Desktop desktop = null;

        if (getLocalDefinedApp(file.getName()) != null) {
            try {
                Runtime.getRuntime().exec(getLocalDefinedApp(file.getName()));
            } catch (IOException ex) {
                OPDE.getLogger().error(ex);
            }
        } else {
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
                if (action == Desktop.Action.OPEN && desktop.isSupported(Desktop.Action.OPEN)) {
                    try {
                        desktop.open(file);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Datei \n" + file.getName() + "\nkonnte nicht angezeigt werden.)",
                                "Kein Anzeigeprogramm vorhanden", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else if (action == Desktop.Action.PRINT && desktop.isSupported(Desktop.Action.PRINT)) {
                    try {
                        desktop.print(file);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Datei \n" + file.getName() + "\nkonnte nicht gedruckt werden.)",
                                "Kein Druckprogramm vorhanden", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Datei \n" + file.getName() + "\nkonnte nicht bearbeitet werden.)",
                            "Keine passende Anwendung vorhanden", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "JAVA Desktop Unterstützung nicht vorhanden", "JAVA Desktop API", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Methode zum vereinfachten Anzeigen von Dateien, die nur über SYSFiles definiert wurden.
     * Sie holt sich die Datei selbst vom FTP Server.
     *
     * @param sysfile
     * @param action
     */
    public static void handleFile(SYSFiles sysfile, java.awt.Desktop.Action action) {
        handleFile(getFile(sysfile), action);
    }

    /**
     * @param parent
     * @param tablename
     * @param fk
     * @param bwk
     * @param table
     * @param documentsAllowed
     * @param attachAllowed
     * @param code
     * @param callback
     * @return
     */
    public static JMenu getSYSFilesContextMenu(java.awt.Frame parent, Object entity, ActionListener callback) {
        JMenu menu = new JMenu("<html>Dokumente <font color=\"green\">&#9679;</font></html>");
        final Frame p = parent;
        final ActionListener cb = callback;
        JMenu menuFiles = null;
        String namedQuery = "";
        String queryParameter = "";
        final Object ent = entity;
        ArrayList<Object[]> filesList = new ArrayList();

        // Was für eine Art von Objekt wurde übergeben ?
        // Je nachdem, muss eine unterschiedliche Abfrage ausgeführt werden
        if (entity instanceof Pflegeberichte) {
            namedQuery = "SYSFiles.findByPB";
            queryParameter = "pflegebericht";
        } else if (entity instanceof BWInfo) {
            namedQuery = "SYSFiles.findByBWInfo";
            queryParameter = "bwinfo";
        } else if (entity instanceof BWerte) {
            namedQuery = "SYSFiles.findByBWert";
            queryParameter = "wert";
        } else if (entity instanceof Verordnung) {
            namedQuery = "SYSFiles.findByVerordnung";
            queryParameter = "verordnung";
        } else {
        }

        // Wenn diese Methode weiss, wie sie mit
        // dem Objekt umgehen muss, dann gehts weiter. Ansonsten
        // gibts kein Menü.
        if (!namedQuery.equals("")) {
            Query query;
            query = OPDE.getEM().createNamedQuery(namedQuery, SYSFiles.class);
            query.setParameter(queryParameter, entity);
            filesList = new ArrayList(query.getResultList());
            menuFiles = getFilesAsMenu("Anzeigen", filesList);
        }


        // -------------------------------------------------
        JMenuItem itemPopupAddDoc = new JMenuItem("Dokument hinzufügen");
        itemPopupAddDoc.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new DlgNewFile(p, ent);
                cb.actionPerformed(new ActionEvent(this, 0, "fileuploaded"));
            }
        });
        menu.add(itemPopupAddDoc);

        if (!filesList.isEmpty()) {
            //final ListElement[] lesfinal = les.clone();
            // -------------------------------------------------
            for (int f = 0; f < filesList.size(); f++) {

                Object[] resultlist = filesList.get(f);
                final SYSFiles file = (SYSFiles) resultlist[0];
                //final Syspb2file pb2file = (Syspb2file) resultlist[1];

                menuFiles.getItem(f).addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        showFile(p, getFile(file));
                    }
                });

            }
            menu.add(menuFiles);
        }
        return menu;
    }

    /**
     * @param menuText
     * @param list
     * @return
     */
    protected static JMenu getFilesAsMenu(String menuText, List<Object[]> list) {
        JMenu result = null;
        if (list.size() > 0) {
            result = new JMenu(menuText);
            Iterator<Object[]> files = list.iterator();
            while (files.hasNext()) {
                Object[] resultlist = files.next();
                SYSFiles file = (SYSFiles) resultlist[0];
                String bemerkung = "";
                if (resultlist[1] instanceof Syspb2file) {
                    bemerkung = ((Syspb2file) resultlist[1]).getBemerkung();
                } else if (resultlist[1] instanceof Sysbwi2file) {
                    bemerkung = ((Sysbwi2file) resultlist[1]).getBemerkung();
                } else if (resultlist[1] instanceof Sysver2file) {
                    bemerkung = ((Sysver2file) resultlist[1]).getBemerkung();
                } else if (resultlist[1] instanceof Sysbwerte2file) {
                    bemerkung = ((Sysbwerte2file) resultlist[1]).getBemerkung();
                }
                JMenuItem mi = new JMenuItem(bemerkung + " [" + file.getFilename() + "]");
                //mi.setToolTipText(pb2file.getBemerkung());
                result.add(mi);
            }
        }
        return result;
    }

    public static void showFile(Component parent, File file) {
        handleFile(parent, file, Desktop.Action.OPEN);
    }

    /**
     * Verarbeitet die übergebene Datei entsprechend der Desktop Action. Berücksichtigt dabei die
     * evtl. lokal definierten Applikationen.
     *
     * @param parent
     * @param file
     * @param action
     */
    public static void handleFile(Component parent, File file, java.awt.Desktop.Action action) {
        Desktop desktop = null;
        if (getLocalDefinedViewerApp(file) != null) {
            try {
                Runtime.getRuntime().exec(getLocalDefinedViewerApp(file));
            } catch (IOException ex) {
                OPDE.getLogger().error(ex);
            }
        } else {
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
                if (action == Desktop.Action.OPEN && desktop.isSupported(Desktop.Action.OPEN)) {
                    try {
                        desktop.open(file);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(parent, "Datei \n" + file.getAbsolutePath() + "\nkonnte nicht angezeigt werden.)",
                                "Kein Anzeigeprogramm vorhanden", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else if (action == Desktop.Action.PRINT && desktop.isSupported(Desktop.Action.PRINT)) {
                    try {
                        desktop.print(file);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(parent, "Datei \n" + file.getAbsolutePath() + "\nkonnte nicht gedruckt werden.)",
                                "Kein Druckprogramm vorhanden", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(parent, "Datei \n" + file.getAbsolutePath() + "\nkonnte nicht bearbeitet werden.)",
                            "Keine passende Anwendung vorhanden", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(parent, "JAVA Desktop Unterstützung nicht vorhanden", "JAVA Desktop API", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Diese Methode findet aus den properties eine lokal definierte Applikation
     * heraus. Das braucht man nur dann, wenn die Funktionen der Java eigenen
     * Desktop API nicht funktionieren.
     *
     * @param file betreffende Datei
     * @return String[] der das passende command array für den EXEC Aufruf erhält.
     */
    public static String[] getLocalDefinedViewerApp(File file) {
        String os = System.getProperty("os.name").toLowerCase();
        String extension = filenameExtension(file.getAbsolutePath());
        String[] result = null;
        if (OPDE.getProps().containsKey(os + "-" + extension)) {
            result = new String[]{OPDE.getProps().getProperty(os + "-" + extension), file.getAbsolutePath()};
        }
        return result;
    }
}
