package op.settings.subpanels;

import entity.EntityTools;
import gui.PnlBeanEditor;
import gui.interfaces.DefaultPanel;
import op.OPDE;
import op.settings.PasswordBean;
import op.threads.DisplayMessage;
import op.tools.SYSTools;
import org.apache.log4j.Logger;

import javax.swing.*;

/**
 * Created by tloehr on 10.07.15.
 */
public class PnlPasswordChange extends DefaultPanel {

    Logger logger = Logger.getLogger(getClass());

    public PnlPasswordChange() {
        super("opde.settings.personal.password");
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        try {
            final PnlBeanEditor<PasswordBean> pbe = new PnlBeanEditor<>(() -> new PasswordBean(), PasswordBean.class, PnlBeanEditor.SAVE_MODE_OK_CANCEL);

            pbe.addDataChangeListener(evt -> {


                if (!evt.getData().getNewPassword().trim().equals(evt.getData().getNewPasswordAgain().trim())) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("opde.settings.personal.newpw.wrong")));
                    return;
                }


                if (!OPDE.getLogin().getUser().getMd5pw().equals(SYSTools.hashword(evt.getData().getOldPassword().trim()))) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("opde.settings.personal.oldpw.wrong")));
                    return;
                }

                OPDE.getLogin().getUser().setMd5pw(SYSTools.hashword(evt.getData().getNewPassword().trim()));
                OPDE.getLogin().setUser(EntityTools.merge(OPDE.getLogin().getUser()));

            });
            add(pbe);
        } catch (Exception e) {
            OPDE.fatal(logger, e);
        }

    }


}
