package op.settings.subpanels;

import com.enterprisedt.net.ftp.FileTransferClient;
import entity.files.SYSFilesTools;
import entity.system.SYSPropsTools;
import gui.PnlBeanEditor;
import gui.interfaces.DefaultPanel;
import op.OPDE;
import op.settings.FTPConfigBean;
import op.threads.DisplayMessage;
import op.tools.SYSTools;
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
            final PnlBeanEditor<FTPConfigBean> pbe = new PnlBeanEditor<>(() -> new FTPConfigBean(OPDE.getProps()), FTPConfigBean.class);
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


            SwingWorker worker = new SwingWorker() {
                @Override
                protected Object doInBackground() {
                    boolean success = false;
                    try {
                        FileTransferClient ftp = SYSFilesTools.getFTPClient(OPDE.getProps());


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
                        ftp.uploadFile(file.getPath(), "ftptest.file");
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.settings.ftp.msg.upload", 1));
                        ftp.downloadFile(OPDE.getOPWD() + File.separator + "ftptest.file", "ftptest.file");
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.settings.ftp.msg.download", 1));
                        File file2 = new File(OPDE.getOPWD() + File.separator + "ftptest.file");
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
                        ftp.deleteFile("ftptest.file");
                        ftp.disconnect();
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.settings.ftp.msg.test.ok", 2));
                        SYSPropsTools.storeProp(SYSPropsTools.KEY_FTP_IS_WORKING, "true");
                    } catch (Exception ftpEx) {
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
