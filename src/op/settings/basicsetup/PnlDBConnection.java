package op.settings.basicsetup;

import entity.system.SYSPropsTools;
import gui.PnlBeanEditor;
import gui.interfaces.DefaultPanel;
import op.OPDE;
import op.settings.databeans.DatabaseConnectionBean;
import op.settings.databeans.FTPConfigBean;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.apache.log4j.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.swing.*;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * Created by tloehr on 02.07.15.
 */
public class PnlDBConnection extends DefaultPanel {
    boolean checkInProgress = false;
    boolean lastCheckOk = false;
    Logger logger = Logger.getLogger(getClass());

    public PnlDBConnection() {
        super("opde.settings.ftp");

        try {
            final PnlBeanEditor<DatabaseConnectionBean> pbe = new PnlBeanEditor<>(() -> new DatabaseConnectionBean(OPDE.getLocalProps()), DatabaseConnectionBean.class, new Closure() {
                @Override
                public void execute(Object o) {
                    System.exit(0);
                }
            });
            pbe.addDataChangeListener(evt -> {
                try {
                    OPDE.getLocalProps().putAll(evt.getData().toProperties(new Properties()));
                    OPDE.saveLocalProps();
                    System.exit(0);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                }
            });
            mainPanel.add(pbe);
        } catch (Exception e) {
            OPDE.fatal(logger, e);
        }
    }



}
