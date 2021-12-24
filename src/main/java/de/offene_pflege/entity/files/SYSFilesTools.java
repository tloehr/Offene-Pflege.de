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
package de.offene_pflege.entity.files;

import de.offene_pflege.entity.info.ResInfo;
import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.entity.nursingprocess.NursingProcess;
import de.offene_pflege.entity.prescription.Prescription;
import de.offene_pflege.entity.reports.NReport;
import de.offene_pflege.entity.system.OPUsers;
import de.offene_pflege.entity.system.SYSPropsTools;
import de.offene_pflege.entity.values.ResValue;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.system.AppInfo;
import de.offene_pflege.op.threads.DisplayManager;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.FtpClient;
import de.offene_pflege.op.tools.SYSTools;
import lombok.extern.log4j.Log4j2;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author tloehr
 */
@Log4j2
public class SYSFilesTools {

    public static ListCellRenderer getSYSFilesRenderer() {
//        final int v = verbosity;
        return (jList, o, i, isSelected, cellHasFocus) -> {
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
        };
    }


    /**
     * <code>putFile(String)</code> speichert eine Datei auf dem FTP Server ab und erstellt eine SYSFiles EntityBean.
     * Die Methode geht dabei wie folgt vor:
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
    private static SYSFiles putFile(EntityManager em, File file) throws Exception {
        SYSFiles sysfile = null;

        String md5 = SYSTools.getMD5Checksum(file);
        Query query = em.createQuery("SELECT s FROM SYSFiles s WHERE s.md5 = :md5");
        query.setParameter("md5", md5);

        ArrayList<SYSFiles> alreadyExistingFiles = new ArrayList<SYSFiles>(query.getResultList());

        // Gibts die Datei schon ?
        if (alreadyExistingFiles.isEmpty()) { // nein, noch nicht
            sysfile = em.merge(new SYSFiles(file.getName(), md5, new Date(file.lastModified()), file.length(), OPDE.getLogin().getUser()));
            FtpClient ftpClient = new FtpClient();
            ftpClient.open();
            ftpClient.putFile(file, sysfile.getRemoteFilename());
            ftpClient.close();
            log.info(SYSTools.xx("misc.msg.upload") + ": " + sysfile.getFilename() + " (" + sysfile.getMd5() + ")");
        } else { // Ansonsten die bestehende Datei zurückgeben
            sysfile = alreadyExistingFiles.get(0);
        }

        return sysfile;
    }


    public static List<SYSFiles> putFiles(File[] files, Object attachable) {
        ArrayList<SYSFiles> successful = new ArrayList<SYSFiles>(files.length);


        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();

            for (File file : files) {
                if (file.isFile()) { // prevents exceptions if somebody has the bright idea to include directories.
                    SYSFiles sysfile = putFile(em, file);
                    if (attachable != null) {
                        if (attachable instanceof NReport) {
                            SYSNR2FILE link = em.merge(new SYSNR2FILE(sysfile, (NReport) attachable, OPDE.getLogin().getUser(), new Date()));
                            sysfile.getNrAssignCollection().add(link);
                            ((NReport) attachable).getAttachedFilesConnections().add(link);

                            // backup link to prevent orphaned files
                            // https://github.com/tloehr/Offene-Pflege.de/issues/45
                            Resident2File link2 = em.merge(new Resident2File(sysfile, ((NReport) attachable).getResident(), OPDE.getLogin().getUser(), new Date()));
                            sysfile.getResidentAssignCollection().add(link2);

                        } else if (attachable instanceof Prescription) {
                            SYSPRE2FILE link = em.merge(new SYSPRE2FILE(sysfile, (Prescription) attachable, OPDE.getLogin().getUser(), new Date()));
                            sysfile.getPreAssignCollection().add(link);
                            ((Prescription) attachable).getAttachedFilesConnections().add(link);

                            // backup link to prevent orphaned files
                            // https://github.com/tloehr/Offene-Pflege.de/issues/45
                            Resident2File link2 = em.merge(new Resident2File(sysfile, ((Prescription) attachable).getResident(), OPDE.getLogin().getUser(), new Date()));
                            sysfile.getResidentAssignCollection().add(link2);

                        } else if (attachable instanceof ResInfo) {
                            SYSINF2FILE link = em.merge(new SYSINF2FILE(sysfile, (ResInfo) attachable, OPDE.getLogin().getUser(), new Date()));
                            sysfile.getBwiAssignCollection().add(link);
                            ((ResInfo) attachable).getAttachedFilesConnections().add(link);

                            // backup link to prevent orphaned files
                            // https://github.com/tloehr/Offene-Pflege.de/issues/45
                            Resident2File link2 = em.merge(new Resident2File(sysfile, ((ResInfo) attachable).getResident(), OPDE.getLogin().getUser(), new Date()));
                            sysfile.getResidentAssignCollection().add(link2);

                        } else if (attachable instanceof ResValue) {
                            SYSVAL2FILE link = em.merge(new SYSVAL2FILE(sysfile, (ResValue) attachable, OPDE.getLogin().getUser(), new Date()));
                            sysfile.getValAssignCollection().add(link);
                            ((ResValue) attachable).getAttachedFilesConnections().add(link);

                            // backup link to prevent orphaned files
                            // https://github.com/tloehr/Offene-Pflege.de/issues/45
                            Resident2File link2 = em.merge(new Resident2File(sysfile, ((ResValue) attachable).getResident(), OPDE.getLogin().getUser(), new Date()));
                            sysfile.getResidentAssignCollection().add(link2);

                        } else if (attachable instanceof NursingProcess) {
                            SYSNP2FILE link = em.merge(new SYSNP2FILE(sysfile, (NursingProcess) attachable, OPDE.getLogin().getUser(), new Date()));
                            sysfile.getNpAssignCollection().add(link);
                            ((NursingProcess) attachable).getAttachedFilesConnections().add(link);

                            // backup link to prevent orphaned files
                            // https://github.com/tloehr/Offene-Pflege.de/issues/45
                            Resident2File link2 = em.merge(new Resident2File(sysfile, ((NursingProcess) attachable).getResident(), OPDE.getLogin().getUser(), new Date()));
                            sysfile.getResidentAssignCollection().add(link2);

                        } else if (attachable instanceof OPUsers) {
                            User2File link = em.merge(new User2File(sysfile, (OPUsers) attachable, OPDE.getLogin().getUser(), new Date()));
                            sysfile.getUsersAssignCollection().add(link);
                            ((OPUsers) attachable).getAttachedFilesConnections().add(link);
                        } else if (attachable instanceof Resident) {
                            Resident2File link = em.merge(new Resident2File(sysfile, (Resident) attachable, OPDE.getLogin().getUser(), new Date()));
                            sysfile.getResidentAssignCollection().add(link);
                            ((Resident) attachable).getAttachedFilesConnections().add(link);
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
            log.warn(ole);
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
            OPDE.fatal(ex);
        } finally {
            em.close();

        }

        return successful;
    }

//    public static void detachFiles(SYSFiles[] files, SYSFilesContainer sysFilesContainer) {
//
//        FileTransferClient ftp = getFTPClient();
//
//        if (ftp != null) {
//            EntityManager em = OPDE.createEM();
//            try {
//                em.getTransaction().begin();
//
//                for (SYSFiles sysfile : files) {
//                    SYSFilesLink link = em.merge(sysFilesContainer.detachFile(sysfile));
//                    em.remove(link);
//                }
//
//                em.getTransaction().commit();
//            } catch (OptimisticLockException ole) {
//                log.warn(ole);
//                if (em.getTransaction().isActive()) {
//                    em.getTransaction().rollback();
//                }
//                if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
//                    OPDE.getMainframe().emptyFrame();
//                    OPDE.getMainframe().afterLogin();
//                }
//                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//            } catch (Exception ex) {
//                if (em.getTransaction().isActive()) {
//                    em.getTransaction().rollback();
//                }
//                OPDE.fatal(ex);
//            } finally {
//                em.close();
//                try {
//                    ftp.disconnect();
//                } catch (Exception e) {
//                    log.error(e);
//                }
//            }
//        }
//    }


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
    private static File getFile(SYSFiles sysfile) {
        File target = null;
        try {

            String sep = System.getProperties().getProperty("file.separator"); // Fileseparator

            target = new File(AppInfo.getOPCache() + sep + sysfile.getFilename());
            // File present in cache directory ?
            if (!target.exists() || !SYSTools.getMD5Checksum(target).equals(sysfile.getMd5())) {
                log.info(SYSTools.xx("misc.msg.download") + ": " + OPDE.getProps().getProperty("FTPServer") + "://" + OPDE.getProps().getProperty("FTPWorkingDirectory") + "/" + sysfile.getFilename());
                FileUtils.deleteQuietly(target);

                FtpClient ftpClient = new FtpClient();
                ftpClient.open();
                ftpClient.getFile(sysfile.getRemoteFilename(), AppInfo.getOPCache() + sep + sysfile.getFilename());
                ftpClient.close();

                target = new File(AppInfo.getOPCache() + sep + sysfile.getFilename());
                target.setLastModified(sysfile.getFiledate().getTime());

            }
        } catch (Exception ex) {
            log.error(ex);
            target = null;
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
            log.warn(ole);
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
                FtpClient ftpClient = new FtpClient();
                ftpClient.open();
                ftpClient.delete(sysfile.getRemoteFilename());
                ftpClient.close();
                log.info("DELETING FILE FROM FTPSERVER: " + sysfile.getFilename() + " (" + sysfile.getMd5() + ")");
            } catch (Exception e) {
                log.error(e);
                success = false;
            }
        }

        return success;
    }


    /**
     * Diese Methode findet aus den properties eine lokal definierte Applikation heraus. Das braucht man nur dann, wenn
     * die Funktionen der Java eigenen Desktop API nicht funktionieren. z.B. linux-html=/usr/
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
     * Diese Methode ermittelt zu einer gebenen Datei und einer gewünschten Aktion das passende Anzeigeprogramm. Falls
     * die Desktop API nicht passendes hat, werdne die lokal definierten Anzeigeprogramme verwendet.
     * <p>
     * Bei Linux müssen dafür unbedingt die Gnome Libraries installiert sein. apt-get install libgnome2-0
     *
     * @param file
     * @param action
     */
    public static void handleFile(File file, Desktop.Action action) {
        if (file == null) {
            return;
        }
        Desktop desktop = null;

        if (getLocalDefinedApp(file) != null) {
            try {
                Runtime.getRuntime().exec(getLocalDefinedApp(file));
            } catch (IOException ex) {
                log.error(ex);
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
     * Methode zum vereinfachten Anzeigen von Dateien, die nur über SYSFiles definiert wurden. Sie holt sich die Datei
     * selbst vom FTP Server.
     *
     * @param sysfile
     * @param action
     */
    public static void handleFile(SYSFiles sysfile, Desktop.Action action) {
        if (sysfile == null) {
            return;
        }
        handleFile(getFile(sysfile), action);
    }


    public static File print(String html, boolean addPrintJScript) {
        return print(html, addPrintJScript, true);
    }

    /**
     * Standard Druck Routine. Nimmt einen HTML Text entgegen und öffnet den lokal installierten Browser damit. Erstellt
     * temporäre Dateien im temp Verzeichnis opde<irgendwas>.html
     *
     * @param html
     * @param addPrintJScript Auf Wunsch kann an das HTML automatisch eine JScript Druckroutine angehangen werden.
     */
    public static File print(String html, boolean addPrintJScript, boolean handleTheFile) {
        File temp = null;
        try {
            // Create temp file.
            temp = createTempFile("opde", ".html");
            temp.deleteOnExit();

            // Write to temp file
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));
            out.write(getHTML4Printout(html, addPrintJScript));

            out.close();
            if (handleTheFile) SYSFilesTools.handleFile(temp, Desktop.Action.OPEN);
        } catch (IOException e) {
            log.error(e);
        }
        return temp;
    }

    public static File createTempFile(String prefix, String suffix) throws IOException {
        return File.createTempFile(prefix, suffix, new File(OPDE.getLocalProps().getProperty(SYSPropsTools.KEY_TMP_DIR)));
    }

    public static String getHTML4Printout(String html, boolean addPrintJScript) {
        String text = "<html><head>";
        // https://github.com/tloehr/Offene-Pflege.de/issues/32
        text += "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>";
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

        return text;
    }


}
