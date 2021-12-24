package de.offene_pflege.op.tools;

import de.offene_pflege.entity.system.SYSPropsTools;
import de.offene_pflege.op.OPDE;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.net.ftp.FTPClient;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * https://www.torsten-horn.de/techdocs/java-ftp.htm#FTP-Upload-Download
 */
@Log4j2
public class FtpUploadDownloadUtil {
    /**
     * FTP-Dateienliste.
     *
     * @return String-Array der Dateinamen auf dem FTP-Server
     */
    public static String[] list(String host, int port, String usr, String pwd) throws IOException {
        FTPClient ftpClient = new FTPClient();
        String[] filenameList;

        try {
            ftpClient.connect(host, port);
            ftpClient.login(usr, pwd);
            filenameList = ftpClient.listNames();
            ftpClient.logout();
        } finally {
            ftpClient.disconnect();
        }

        return filenameList;
    }

    /**
     * FTP-Client-Download.
     *
     * @return true falls ok
     */
    public static boolean download(String localResultFile, String remoteSourceFile, Properties ftpProps) throws IOException {
        FTPClient ftpClient = new FTPClient();
        FileOutputStream fos = null;
        boolean resultOk = true;

        try {

            ftpClient.connect(ftpProps.getProperty(SYSPropsTools.KEY_FTP_HOST), Integer.parseInt(ftpProps.getProperty(SYSPropsTools.KEY_FTP_PORT)));
            log.debug(ftpClient.getReplyString());
            resultOk &= ftpClient.login(ftpProps.getProperty(SYSPropsTools.KEY_FTP_USER), ftpProps.getProperty(SYSPropsTools.KEY_FTP_PASSWORD));
            log.debug(ftpClient.getReplyString());
            fos = new FileOutputStream(localResultFile);
            resultOk &= ftpClient.retrieveFile(OPDE.getProps().getProperty("FTPWorkingDirectory")+"/"+remoteSourceFile, fos);
            log.debug(ftpClient.getReplyString());
            resultOk &= ftpClient.logout();
            log.debug(ftpClient.getReplyString());

        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {/* nothing to do */}
            ftpClient.disconnect();
        }

        return resultOk;
    }

    /**
     * FTP-Client-Upload.
     *
     * @return true falls ok
     */
    public static boolean upload(String localSourceFile, String remoteResultFile,
                                 Properties ftpProps) throws IOException {
        FTPClient ftpClient = new FTPClient();
        FileInputStream fis = null;
        boolean resultOk = true;

        try {
            ftpClient.connect(ftpProps.getProperty(SYSPropsTools.KEY_FTP_HOST), Integer.parseInt(ftpProps.getProperty(SYSPropsTools.KEY_FTP_PORT)));
            log.debug(ftpClient.getReplyString());
            resultOk &= ftpClient.login(ftpProps.getProperty(SYSPropsTools.KEY_FTP_USER), ftpProps.getProperty(SYSPropsTools.KEY_FTP_PASSWORD));
            log.debug(ftpClient.getReplyString());
            fis = new FileInputStream(localSourceFile);
            resultOk &= ftpClient.storeFile(OPDE.getProps().getProperty("FTPWorkingDirectory")+"/"+remoteResultFile, fis);
            log.debug(ftpClient.getReplyString());
            resultOk &= ftpClient.logout();
            log.debug(ftpClient.getReplyString());
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {/* nothing to do */}
            ftpClient.disconnect();
        }

        return resultOk;

    }

    /**
     * FTP-Client-Upload.
     *
     * @return true falls ok
     */
    public static boolean delete(String remoteResultFile,
                                 Properties ftpProps) throws IOException {
        FTPClient ftpClient = new FTPClient();
        boolean resultOk = true;

        try {
            ftpClient.connect(ftpProps.getProperty(SYSPropsTools.KEY_FTP_HOST), Integer.parseInt(ftpProps.getProperty(SYSPropsTools.KEY_FTP_PORT)));
            log.debug(ftpClient.getReplyString());
            resultOk &= ftpClient.login(ftpProps.getProperty(SYSPropsTools.KEY_FTP_USER), ftpProps.getProperty(SYSPropsTools.KEY_FTP_PASSWORD));
            log.debug(ftpClient.getReplyString());
            resultOk &= ftpClient.deleteFile(OPDE.getProps().getProperty("FTPWorkingDirectory")+"/"+remoteResultFile);
            log.debug(ftpClient.getReplyString());
            resultOk &= ftpClient.logout();
            log.debug(ftpClient.getReplyString());
        } finally {
            ftpClient.disconnect();
        }

        return resultOk;

    }
}