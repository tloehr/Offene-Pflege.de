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

import com.enterprisedt.net.ftp.EventListener;
import com.enterprisedt.net.ftp.FTPConnectMode;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FileTransferClient;
import entity.info.ResInfo;
import entity.info.Resident;
import entity.nursingprocess.NursingProcess;
import entity.prescription.Prescription;
import entity.qms.Qms;
import entity.qms.Qmsplan;
import entity.reports.NReport;
import entity.staff.Training;
import entity.staff.Training2Users;
import entity.system.SYSPropsTools;
import entity.system.Users;
import entity.values.ResValue;
import op.OPDE;
import op.system.AppInfo;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.io.FileUtils;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

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
     * @param file File Objekt der zu speichernden Datei
     * @return EB der neuen Datei. null bei Fehler.
     */
    private static SYSFiles putFile(EntityManager em, FileTransferClient ftp, File file) throws Exception {
        SYSFiles sysfile = null;

        String md5 = SYSTools.getMD5Checksum(file);
        Query query = em.createQuery("SELECT s FROM SYSFiles s WHERE s.md5 = :md5");
        query.setParameter("md5", md5);

        ArrayList<SYSFiles> alreadyExistingFiles = new ArrayList<SYSFiles>(query.getResultList());

        // Gibts die Datei schon ?
        if (alreadyExistingFiles.isEmpty()) { // nein, noch nicht
            sysfile = em.merge(new SYSFiles(file.getName(), md5, new Date(file.lastModified()), file.length(), OPDE.getLogin().getUser()));
//            FileInputStream fis = new FileInputStream(file);
            ftp.uploadFile(file.getPath(), sysfile.getRemoteFilename());

//            ftp.storeF.storeFile(file.getPath(),sysfile.getRemoteFilename());
            OPDE.info(SYSTools.xx("misc.msg.upload") + ": " + sysfile.getFilename() + " (" + sysfile.getMd5() + ")");
//            fis.close();
        } else { // Ansonsten die bestehende Datei zurückgeben

            sysfile = alreadyExistingFiles.get(0);


            // Does the User own this file already ?
//            for (SYSFiles mySYSfile : alreadyExistingFiles) {
//                if (mySYSfile.getResident().equals(resident)) {
//                    sysfile = mySYSfile;
//                    break;
//                }
//            }
//            if (sysfile == null) {
//                sysfile = em.merge(new SYSFiles(file.getName(), md5, new Date(file.lastModified()), file.length(), OPDE.getLogin().getUser(), resident));
//            }
        }

        return sysfile;
    }


    public static List<SYSFiles> putFiles(File[] files, Object attachable) {
        ArrayList<SYSFiles> successful = new ArrayList<SYSFiles>(files.length);
        FileTransferClient ftp = getFTPClient();

        if (ftp != null) {
            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();

                for (File file : files) {
                    if (file.isFile()) { // prevents exceptions if somebody has the bright idea to include directories.
                        SYSFiles sysfile = putFile(em, ftp, file);
                        if (attachable != null) {
                            if (attachable instanceof NReport) {
                                SYSNR2FILE link = em.merge(new SYSNR2FILE(sysfile, (NReport) attachable, OPDE.getLogin().getUser(), new Date()));
                                sysfile.getNrAssignCollection().add(link);
                                ((NReport) attachable).getAttachedFilesConnections().add(link);
                            } else if (attachable instanceof Prescription) {
                                SYSPRE2FILE link = em.merge(new SYSPRE2FILE(sysfile, (Prescription) attachable, OPDE.getLogin().getUser(), new Date()));
                                sysfile.getPreAssignCollection().add(link);
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
                            } else if (attachable instanceof Training) {
                                Training2File link = em.merge(new Training2File(sysfile, (Training) attachable, OPDE.getLogin().getUser(), new Date()));
                                sysfile.getTrAssignCollection().add(link);
                                ((Training) attachable).getAttachedFilesConnections().add(link);
                            } else if (attachable instanceof Users) {
                                User2File link = em.merge(new User2File(sysfile, (Users) attachable, OPDE.getLogin().getUser(), new Date()));
                                sysfile.getUsersAssignCollection().add(link);
                                ((Users) attachable).getAttachedFilesConnections().add(link);
                            } else if (attachable instanceof Resident) {
                                Resident2File link = em.merge(new Resident2File(sysfile, (Resident) attachable, OPDE.getLogin().getUser(), new Date()));
                                sysfile.getResidentAssignCollection().add(link);
                                ((Resident) attachable).getAttachedFilesConnections().add(link);
                            } else if (attachable instanceof Qmsplan) {
                                Qmsplan2File link = em.merge(new Qmsplan2File(sysfile, (Qmsplan) attachable, OPDE.getLogin().getUser(), new Date()));
                                sysfile.getQmsplanAssignCollection().add(link);
                                ((Qmsplan) attachable).getAttachedFilesConnections().add(link);
                            } else if (attachable instanceof Qms) {
                                Qms2File link = em.merge(new Qms2File(sysfile, (Qms) attachable, OPDE.getLogin().getUser(), new Date()));
                                sysfile.getQmsAssignCollection().add(link);
                                ((Qms) attachable).getAttachedFilesConnections().add(link);
                            } else if (attachable instanceof Training) {
                                Training2File link = em.merge(new Training2File(sysfile, (Training) attachable, OPDE.getLogin().getUser(), new Date()));
                                sysfile.getTrainingAssignCollection().add(link);
                                ((Training) attachable).getAttachedFilesConnections().add(link);
                            } else if (attachable instanceof Training2Users) {
                                em.merge(((SYSFilesContainer) attachable).attachFile(sysfile));
                            }
                        }
                        successful.add(sysfile);
                    }
                    if (successful.size() != files.length) {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("misc.msg.nodirectories")));
                    }
                }
                em.getTransaction().commit();
            } catch (OptimisticLockException ole) {
                OPDE.warn(ole);
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                    OPDE.getMainframe().emptyFrame();
                    OPDE.getMainframe().afterLogin();
                }
                // Bereits gespeicherte wieder löschen
//                for (SYSFiles sysfile : successful) {
//                    try {
//                        ftp.deleteFile(sysfile.getRemoteFilename());
//                    } catch (IOException e) {
//                        OPDE.fatal(e);
//                    }
//                }
                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
            } catch (Exception ex) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                // Bereits gespeicherte wieder löschen
//                for (SYSFiles sysfile : successful) {
//                    try {
//                        ftp.deleteFile(sysfile.getRemoteFilename());
//                    } catch (IOException e) {
//                        OPDE.fatal(e);
//                    }
//                }
                OPDE.fatal(ex);
            } finally {
                em.close();
                try {
                    ftp.disconnect();
                } catch (Exception e) {
                    OPDE.error(e);
                }
            }
        }
        return successful;
    }

    public static void detachFiles(SYSFiles[] files, SYSFilesContainer sysFilesContainer) {

        FileTransferClient ftp = getFTPClient();

        if (ftp != null) {
            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();

                for (SYSFiles sysfile : files) {
                    SYSFilesLink link = em.merge(sysFilesContainer.detachFile(sysfile));
                    em.remove(link);
                }

                em.getTransaction().commit();
            } catch (OptimisticLockException ole) {
                OPDE.warn(ole);
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                    OPDE.getMainframe().emptyFrame();
                    OPDE.getMainframe().afterLogin();
                }
                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
            } catch (Exception ex) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                OPDE.fatal(ex);
            } finally {
                em.close();
                try {
                    ftp.disconnect();
                } catch (Exception e) {
                    OPDE.error(e);
                }
            }
        }
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
    private static File getFile(SYSFiles sysfile, EventListener eventListener) {
        File target = null;
        try {

            FileTransferClient ftp = getFTPClient();

            String sep = System.getProperties().getProperty("file.separator"); // Fileseparator

            target = new File(AppInfo.getOPCache() + sep + sysfile.getFilename());
            // File present in cache directory ?
            if (!target.exists() || !SYSTools.getMD5Checksum(target).equals(sysfile.getMd5())) {
                OPDE.info(SYSTools.xx("misc.msg.download") + ": " + OPDE.getProps().getProperty("FTPServer") + "://" + OPDE.getProps().getProperty("FTPWorkingDirectory") + "/" + sysfile.getFilename());
                FileUtils.deleteQuietly(target);

                ftp.setEventListener(eventListener);
                ftp.downloadFile(AppInfo.getOPCache() + sep + sysfile.getFilename(), sysfile.getRemoteFilename());
                target = new File(AppInfo.getOPCache() + sep + sysfile.getFilename());
                target.setLastModified(sysfile.getFiledate().getTime());
            }

            ftp.disconnect();
        } catch (FTPException ftpex) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("FTPError: " + ftpex.getMessage(), "SYSFilesTools.getFile(SYSFiles, EventListener)"));
            target = null;
        } catch (Exception ex) {
            OPDE.fatal(ex);
        }
        return target;
    }


    public static boolean deleteFile(SYSFiles sysfile) {
        boolean success = false;
        EntityManager em = OPDE.createEM();

        try {
            em.getTransaction().begin();
            SYSFiles mySYSFile = em.merge(sysfile);
            em.remove(mySYSFile);
            em.getTransaction().commit();
            success = true;
        } catch (OptimisticLockException ole) {
            OPDE.warn(ole);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                OPDE.getMainframe().emptyFrame();
                OPDE.getMainframe().afterLogin();
            }

            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
            success = false;
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
                FileTransferClient ftp = getFTPClient();
                ftp.deleteFile(sysfile.getRemoteFilename());
                ftp.disconnect();
                OPDE.info("DELETING FILE FROM FTPSERVER: " + sysfile.getFilename() + " (" + sysfile.getMd5() + ")");
            } catch (Exception e) {
                OPDE.error(e);
                success = false;
            }
        }

        return success;
    }


//    private static ArrayList<SYSFiles> getConnectedFiles(String md5) {
//        ArrayList<SYSFiles> result = null;
//        EntityManager em = OPDE.createEM();
//        Query query = em.createQuery("SELECT s FROM SYSFiles s WHERE s.md5 = :md5");
//        query.setParameter("md5", md5);
//        result = new ArrayList<SYSFiles>(query.getResultList());
//        em.close();
//        return result;
//    }

    /**
     * Diese Methode findet aus den properties eine lokal definierte Applikation
     * heraus. Das braucht man nur dann, wenn die Funktionen der Java eigenen
     * Desktop API nicht funktionieren.
     * z.B. linux-html=/usr/
     *
     * @param file
     * @return String[] der das passende command array für den EXEC Aufruf erhält.
     */
    public static String[] getLocalDefinedApp(File file) {
        String os = System.getProperty("os.name").toLowerCase();
        if (SYSTools.isMac()) os = "mac";

        String extension = filenameExtension(file.getName());
        String[] result = null;
        if (OPDE.getProps().containsKey(os + "-" + extension)) {
            result = new String[]{OPDE.getProps().getProperty(os + "-" + extension), file.getAbsolutePath()};
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
     * <p>
     * Bei Linux müssen dafür unbedingt die Gnome Libraries installiert sein.
     * apt-get install libgnome2-0
     *
     * @param file
     * @param action
     */
    public static void handleFile(File file, java.awt.Desktop.Action action) {
        if (file == null) {
            return;
        }
        Desktop desktop = null;

        if (getLocalDefinedApp(file) != null) {
            try {
                Runtime.getRuntime().exec(getLocalDefinedApp(file));
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
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("misc.msg.noviewer"), DisplayMessage.WARNING));
                    }
                } else if (action == Desktop.Action.PRINT && desktop.isSupported(Desktop.Action.PRINT)) {
                    try {
                        desktop.print(file);
                    } catch (IOException ex) {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("misc.msg.noprintprog"), DisplayMessage.WARNING));
                    }
                } else {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("misc.msg.nofilehandler"), DisplayMessage.WARNING));
                }
            } else {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("misc.msg.nojavadesktop"), DisplayMessage.WARNING));
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
        if (sysfile == null) {
            return;
        }
        handleFile(getFile(sysfile, null), action);
    }

    public static FileTransferClient getFTPClient(Properties ftpProps) throws Exception {
        FileTransferClient ftp = new FileTransferClient();

        ftp.setRemoteHost(ftpProps.getProperty(SYSPropsTools.KEY_FTP_HOST));
        ftp.setUserName(ftpProps.getProperty(SYSPropsTools.KEY_FTP_USER));
        ftp.setPassword(ftpProps.getProperty(SYSPropsTools.KEY_FTP_PASSWORD));
        ftp.setRemotePort(Integer.parseInt(ftpProps.getProperty(SYSPropsTools.KEY_FTP_PORT)));
        ftp.connect();
        ftp.getAdvancedFTPSettings().setConnectMode(FTPConnectMode.PASV);
        if (!ftpProps.getProperty(SYSPropsTools.KEY_FTP_WD).isEmpty()) {
            ftp.changeDirectory(ftpProps.getProperty(SYSPropsTools.KEY_FTP_WD));
        }


        return ftp;
    }


    public static FileTransferClient getFTPClient() {
        FileTransferClient ftp;
        try {
            ftp = getFTPClient(OPDE.getProps());
        } catch (Exception e) {
            OPDE.error(e);
            ftp = null;

        }
        return ftp;
    }

    public static boolean isFTPServerReady() {
        FileTransferClient ftp = getFTPClient();
        boolean ready = ftp != null;
        if (ready) {
            try {
                ftp.disconnect();
            } catch (Exception e) {
                OPDE.error(e);
                ready = false;
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
                    "<b>" + SYSTools.xx("misc.msg.endofreport") + "</b><br/>" + (OPDE.getLogin() != null ? SYSTools.htmlUmlautConversion(OPDE.getLogin().getUser().getUID()) : "")
                    + "<br/>" + DateFormat.getDateTimeInstance().format(new Date())
                    + "<br/>" + OPDE.getAppInfo().getProgname() + ", v" + OPDE.getAppInfo().getVersion() + "</div></body></html>";


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


    public static File getHtmlFile(String html, String prefix, String ext) {
        File temp = null;
        try {
            // Create temp file.
            temp = File.createTempFile(prefix, ext);

            String text = "<html><head>";

            text += OPDE.getCSS();
            text += "</head><body>" + SYSTools.htmlUmlautConversion(html)
                    + "<hr/>" +
                    "<div id=\"fonttext\">" +
                    "<b>" + SYSTools.xx("misc.msg.endofreport") + "</b><br/>" + (OPDE.getLogin() != null ? SYSTools.htmlUmlautConversion(OPDE.getLogin().getUser().getUID()) : "")
                    + "<br/>" + DateFormat.getDateTimeInstance().format(new Date())
                    + "<br/>" + OPDE.getAppInfo().getProgname() + ", v" + OPDE.getAppInfo().getVersion() + "</div></body></html>";


            // Write to temp file
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));
            out.write(text);

            out.close();
        } catch (IOException e) {
            OPDE.error(e);
        }
        return temp;
    }

}
