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

import entity.info.ResInfo;
import entity.info.Resident;
import entity.nursingprocess.NursingProcess;
import entity.prescription.Prescription;
import entity.reports.NReport;
import entity.values.ResValue;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author tloehr
 */
public class SYSFilesTools {

    public static ListCellRenderer getSYSFilesRenderer() {
//        final int v = verbosity;
        return new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                String text;
                if (o == null) {
                    text = SYSTools.toHTML("<i>Keine Auswahl</i>");
                } else if (o instanceof SYSFiles) {
                    SYSFiles sysfile = (SYSFiles) o;
                    text = sysfile.getFilename() + SYSTools.catchNull(sysfile.getBeschreibung(), " (", ")");
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
            }
        };
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
    public static SYSFiles putFile(EntityManager em, FTPClient ftp, File file, Resident bewohner) throws Exception {
        SYSFiles sysfile = null;

        String md5 = SYSTools.getMD5Checksum(file);
        Query query = em.createQuery("SELECT s FROM SYSFiles s WHERE s.md5 = :md5");
        query.setParameter("md5", md5);

        // Gibts die Datei schon ?
        if (query.getResultList().isEmpty()) { // nein, noch nicht
            long ts = file.lastModified();
            sysfile = em.merge(new SYSFiles(file.getName(), md5, new Date(ts), file.length(), OPDE.getLogin().getUser(), bewohner));
            FileInputStream fis = new FileInputStream(file);
            ftp.storeFile(sysfile.getRemoteFilename(), fis);
            fis.close();
        } else { // Ansonsten die bestehende Datei zurückgeben
            sysfile = (SYSFiles) query.getSingleResult();

        }

        return sysfile;
    }

    public static List<SYSFiles> putFiles(File[] files, Resident bewohner) {
        return putFiles(files, bewohner, null);
    }

    public static List<SYSFiles> putFiles(File[] files, Object attachable) {
        Resident bw = null;
        if (attachable instanceof NReport) {
            bw = ((NReport) attachable).getResident();
        } else if (attachable instanceof Prescription) {
            bw = ((Prescription) attachable).getResident();
        } else if (attachable instanceof ResInfo) {
            bw = ((ResInfo) attachable).getResident();
        } else if (attachable instanceof ResValue) {
            bw = ((ResValue) attachable).getResident();
        } else if (attachable instanceof NursingProcess) {
            bw = ((NursingProcess) attachable).getResident();
        }
        return putFiles(files, bw, attachable);
    }

    public static List<SYSFiles> putFiles(File[] files, Resident bewohner, Object attachable) {

        ArrayList<SYSFiles> successful = new ArrayList<SYSFiles>(files.length);
        FTPClient ftp = getFTPClient();

        if (ftp != null) {
            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();
                for (File file : files) {
                    if (file.isFile()) { // prevents exceptions if somebody has the bright idea to include directories.
                        SYSFiles sysfile = putFile(em, ftp, file, bewohner);
                        if (attachable != null) {
                            if (attachable instanceof NReport) {
                                SYSNR2FILE link = em.merge(new SYSNR2FILE(sysfile, (NReport) attachable, OPDE.getLogin().getUser(), new Date()));
                                sysfile.getPbAssignCollection().add(link);
                                ((NReport) attachable).getAttachedFilesConnections().add(link);
                            } else if (attachable instanceof Prescription) {
                                SYSPRE2FILE link = em.merge(new SYSPRE2FILE(sysfile, (Prescription) attachable, OPDE.getLogin().getUser(), new Date()));
                                sysfile.getVerAssignCollection().add(link);
                                ((Prescription) attachable).getAttachedFilesConnections().add(link);
                            } else if (attachable instanceof ResInfo) {
                                SYSINF2FILE link = em.merge(new SYSINF2FILE(sysfile, (ResInfo) attachable, OPDE.getLogin().getUser(), new Date()));
                                sysfile.getBwiAssignCollection().add(link);
                                ((ResInfo) attachable).getAttachedFilesConnections().add(link);
                            } else if (attachable instanceof ResValue) {
                                SYSVAL2FILE link = em.merge(new SYSVAL2FILE(sysfile, (ResValue) attachable, OPDE.getLogin().getUser(), new Date()));
                                sysfile.getValAssignCollection().add(link);
                                ((ResValue) attachable).getAttachedFilesConnections().add(link);
                            } else if (attachable instanceof NursingProcess) {
                                SYSNP2FILE link = em.merge(new SYSNP2FILE(sysfile, (NursingProcess) attachable, OPDE.getLogin().getUser(), new Date()));
                                sysfile.getNpAssignCollection().add(link);
                                ((NursingProcess) attachable).getAttachedFilesConnections().add(link);
                            }
                        }
                        successful.add(sysfile);
                    }
                    if (successful.size() != files.length){
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.nodirectories")));
                    }
                }
                em.getTransaction().commit();
            } catch (Exception ex) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                // Bereits gespeicherte wieder löschen
                for (SYSFiles sysfile : successful) {
                    try {
                        ftp.deleteFile(sysfile.getRemoteFilename());
                    } catch (IOException e) {
                        OPDE.fatal(e);
                    }
                }
                OPDE.fatal(ex);
            } finally {
                em.close();
                try {
                    ftp.disconnect();
                } catch (IOException e) {
                    OPDE.error(e);
                }
            }
        }
        return successful;
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

            FTPClient ftp = getFTPClient();

            String sep = System.getProperties().getProperty("file.separator"); // Fileseparator
            // Gibts die Datei schon ?
            File target = new File(OPDE.getOPCache() + sep + sysfile.getFilename());
            // Gibts den Download schon ?
            if (target.exists() && SYSTools.getMD5Checksum(target).equals(sysfile.getMd5())) {
                result = target;
            } else { // Nein, muss runtergeladen werden.
                target.delete();
                // Filetransfer...........................
                FileOutputStream fos = new FileOutputStream(target);
                ftp.retrieveFile(sysfile.getRemoteFilename(), fos);
                fos.close();
                target.setLastModified(sysfile.getFiledate().getTime());
                result = target;
            }

            ftp.disconnect();
        } catch (Exception ex) {
            result = null;
            OPDE.fatal(ex);
        }
        return result;
    }

    public static boolean deleteFile(SYSFiles sysfile) {
        boolean success = false;
        EntityManager em = OPDE.createEM();

        try {
            em.getTransaction().begin();
            em.remove(em.merge(sysfile));
            em.getTransaction().commit();
            success = true;
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            success = false;
        } finally {
            em.close();
        }

        if (success) {
            try {
                FTPClient ftp = getFTPClient();
                ftp.deleteFile(sysfile.getRemoteFilename());
                ftp.disconnect();
            } catch (Exception e) {
                OPDE.error(e);
                success = false;
            }
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
     * @param file
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
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.noviewer"), DisplayMessage.WARNING));
                    }
                } else if (action == Desktop.Action.PRINT && desktop.isSupported(Desktop.Action.PRINT)) {
                    try {
                        desktop.print(file);
                    } catch (IOException ex) {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.noprintprog"), DisplayMessage.WARNING));
                    }
                } else {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.nofilehandler"), DisplayMessage.WARNING));
                }
            } else {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.nojavadesktop"), DisplayMessage.WARNING));
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

//    public static JMenuItem getFileMenu(final Object attachable, final Closure afterAttach) {
//        final JMenuItem itemPopupAttach = new JMenuItem(OPDE.lang.getString(PnlFiles.internalClassID), SYSConst.icon22attach);
//        itemPopupAttach.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                new DlgFiles(attachable, afterAttach);
//            }
//        });
//        return itemPopupAttach;
//    }

    public static FTPClient getFTPClient() {
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(OPDE.getProps().getProperty("FTPServer"), Integer.parseInt(OPDE.getProps().getProperty("FTPPort")));
            ftp.login(OPDE.getProps().getProperty("FTPUser"), OPDE.getProps().getProperty("FTPPassword"));
            ftp.changeWorkingDirectory(OPDE.getProps().getProperty("FTPWorkingDirectory"));
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
        } catch (IOException e) {
            OPDE.error(e);
            ftp = null;
        }
        return ftp;
    }

    public static boolean isFTPServerReady() {
        FTPClient ftp = getFTPClient();
        boolean ready = ftp != null;
        if (ready) {
            try {
                ftp.disconnect();
            } catch (IOException e) {
                OPDE.error(e);
            }
        }
        return ready;
    }

    public static String getDatumUndUser(SYSFiles sysFiles) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");
        result = sdf.format(sysFiles.getPit()) + "; " + sysFiles.getUser().getFullname();

        return SYSConst.html_fontface + result + "</font>";
    }

    /**
     * Standard Druck Routine. Nimmt einen HTML Text entgegen und öffnet den lokal installierten Browser damit.
     * Erstellt temporäre Dateien im temp Verzeichnis opde<irgendwas>.html
     *
     * @param html
     * @param addPrintJScript Auf Wunsch kann an das HTML automatisch eine JScript Druckroutine angehangen werden.
     */
    public static File print(String html, boolean addPrintJScript) {
        File temp = null;
        try {
            // Create temp file.
            temp = File.createTempFile("opde", ".html");

            String text = "<html><head>";
            if (addPrintJScript) {
                text += "<script type=\"text/javascript\">" +
                        "window.onload = function() {"
                        + "window.print();"
                        + "}</script>";
            }
            text += OPDE.getCSS();
            text += "</head><body>" + SYSTools.htmlUmlautConversion(html)
                    + "<hr/>" +
                    "<div id=\"fonttext\">" +
                    "<b>" + OPDE.lang.getString("misc.msg.endofreport") + "</b><br/>" + (OPDE.getLogin() != null ? SYSTools.htmlUmlautConversion(OPDE.getLogin().getUser().getFullname()) : "")
                    + "<br/>" + DateFormat.getDateTimeInstance().format(new Date())
                    + "<br/>" + OPDE.getAppInfo().getProgname() + ", v" + OPDE.getAppInfo().getVersion() + "/" + OPDE.getAppInfo().getBuild() + "</div></body></html>";


            // Write to temp file
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));
            out.write(text);

            out.close();
            SYSFilesTools.handleFile(temp, Desktop.Action.OPEN);
        } catch (IOException e) {
            OPDE.error(e);
        }
        return temp;
    }

}
