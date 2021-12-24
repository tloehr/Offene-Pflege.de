package de.offene_pflege.op.tools;

import de.offene_pflege.entity.system.SYSPropsTools;
import de.offene_pflege.op.OPDE;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * 
 */
@Log4j2
public class FtpClient {

    private final String server;
    private final int port;
    private final String user;
    private final String password;
    private final String path;
    private FTPClient ftp;

    public FtpClient() {
        this.server = OPDE.getProps().getProperty(SYSPropsTools.KEY_FTP_HOST);
        this.port = Integer.parseInt(OPDE.getProps().getProperty(SYSPropsTools.KEY_FTP_PORT));
        this.user = OPDE.getProps().getProperty(SYSPropsTools.KEY_FTP_USER);
        this.password = OPDE.getProps().getProperty(SYSPropsTools.KEY_FTP_PASSWORD);
        this.path = OPDE.getProps().getProperty(SYSPropsTools.KEY_FTP_WD);
    }

    public void open() throws IOException {
        ftp = new FTPClient();

        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

        ftp.connect(server, port);
        log.debug(ftp.getReplyString());
        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new IOException("Exception in connecting to FTP Server");
        }

        ftp.login(user, password);
        log.debug(ftp.getReplyString());

        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.enterLocalPassiveMode();

    }

    public void close() throws IOException {
        ftp.disconnect();
    }

    Collection<String> listFiles() throws IOException {
        FTPFile[] files = ftp.listFiles(path);

        return Arrays.stream(files)
                .map(FTPFile::getName)
                .collect(Collectors.toList());
    }

    public void putFile(File file, String remoteName) throws IOException {
        ftp.storeFile(path.isEmpty() ? "" : path + "/" + remoteName, new FileInputStream(file));
        log.debug(ftp.getReplyString());
    }

    public void getFile(String source, String destination) throws IOException {
        FileOutputStream out = new FileOutputStream(destination);
        ftp.retrieveFile(path.isEmpty() ? "" : path + "/" + source, out);
        log.debug(ftp.getReplyString());
        out.close();
    }

    public void delete(String source) throws IOException {
        ftp.deleteFile(path.isEmpty() ? "" : path + "/" + source);
        log.debug(ftp.getReplyString());
    }
}
