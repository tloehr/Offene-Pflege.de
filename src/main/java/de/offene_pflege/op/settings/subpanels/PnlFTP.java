package de.offene_pflege.op.settings.subpanels;

import de.offene_pflege.entity.system.SYSPropsTools;
import de.offene_pflege.gui.PnlBeanEditor;
import de.offene_pflege.gui.interfaces.DefaultPanel;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.settings.databeans.FTPConfigBean;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.FtpClient;
import de.offene_pflege.op.tools.FtpUploadDownloadUtil;
import de.offene_pflege.op.tools.LocalMachine;
import de.offene_pflege.op.tools.SYSTools;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Created by tloehr on 02.07.15.
 */
public class PnlFTP extends DefaultPanel {
    boolean checkInProgress = false;
    boolean lastCheckOk = false;
    Logger logger = Logger.getLogger(getClass());

    public PnlFTP() {
        super("opde.settings.ftp");
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        try {
            final PnlBeanEditor<FTPConfigBean> pbe = new PnlBeanEditor<>(() -> new FTPConfigBean(OPDE.getProps()), FTPConfigBean.class, PnlBeanEditor.SAVE_MODE_CUSTOM); // SAVE_MODE_CUSTOM -> fixes #25
            pbe.setCustomPanel(getButtonPanel(pbe));
            pbe.addDataChangeListener(evt -> SYSPropsTools.storeProps(evt.getData().toProperties(new Properties())));
            add(pbe);
        } catch (Exception e) {
            OPDE.fatal(logger, e);
        }
    }


    private JPanel getButtonPanel(PnlBeanEditor<FTPConfigBean> pbe) {


        final JButton btnFTPTest = new JButton(SYSTools.xx("opde.settings.ftp.test"));
        btnFTPTest.addActionListener(e -> {

            if (checkInProgress) return;
            // fixes #25
            try {
                pbe.broadcast();
            } catch (Exception e1) {
                logger.warn(e1);
                return;
            }

            SwingWorker worker = new SwingWorker() {
                @Override
                protected Object doInBackground() {
                    boolean success = false;
                    try {

                        FtpClient ftpClient = new FtpClient();
                        ftpClient.open();

                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.settings.ftp.creating.testfile", 1));
                        // creating a testfile for the ftp test
                        File file = File.createTempFile("opde", ".txt");
                        file.createNewFile();
                        FileWriter writer = new FileWriter(file);

                        for (int length = 0; length <= 1e+7; length += 39) {
                            writer.write("abcdefghijkl");
                            writer.write("\n");
                            writer.write("abcdefghijkl");
                            writer.write("\n");
                            writer.write("abcdefghijkl");
                            writer.write("\n");
                        }
                        writer.flush();
                        writer.close();
                        file.deleteOnExit();

//                        File file = new File(OPDE.getOPWD() + File.separator + "opdestart.jar");
                        String md5a = SYSTools.getMD5Checksum(file);
                        ftpClient.putFile(file, "ftptest.file");
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.settings.ftp.msg.upload", 1));
                        ftpClient.getFile("ftptest.file", LocalMachine.getAppDataPath() + File.separator + "ftptest.file");
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.settings.ftp.msg.download", 1));
                        File file2 = new File(LocalMachine.getAppDataPath() + File.separator + "ftptest.file");
                        String md5b = SYSTools.getMD5Checksum(file2);
                        if (md5b.equalsIgnoreCase(md5a)) {
                            success = true;
                            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.settings.ftp.msg.files.equal", 1));
                        } else {
                            success = false;
                            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.settings.ftp.msg.files.differ", DisplayMessage.WARNING));
                            throw new Exception("MD5 error");
                        }
                        FileUtils.deleteQuietly(file2);
                        ftpClient.delete("ftptest.file");
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.settings.ftp.msg.test.ok", 2));
                        SYSPropsTools.storeProp(SYSPropsTools.KEY_FTP_IS_WORKING, "true");

                        ftpClient.close();

                    } catch (Exception ftpEx) {
//                        OPDE.fatal(ftpEx);
                        logger.error(ftpEx);
//                        ftpEx.printStackTrace();
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(ftpEx.getMessage(), DisplayMessage.WARNING));
                        SYSPropsTools.storeProp(SYSPropsTools.KEY_FTP_IS_WORKING, "false");
                    }

                    return success;

                }

                @Override
                protected void done() {
                    checkInProgress = false;

                    try {
                        lastCheckOk = (Boolean) get();
                    } catch (InterruptedException e) {
                        logger.debug(e);
                    } catch (ExecutionException e) {
                        OPDE.fatal(logger, e);
                    }
                }
            };

            worker.execute();


            reload();
        });


        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.LINE_AXIS));
        content.add(btnFTPTest);

        return content;

    }
}
