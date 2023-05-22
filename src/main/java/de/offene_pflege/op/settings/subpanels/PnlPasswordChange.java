package de.offene_pflege.op.settings.subpanels;

import de.offene_pflege.entity.EntityTools;
import de.offene_pflege.gui.PnlBeanEditor;
import de.offene_pflege.gui.interfaces.DefaultPanel;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.settings.databeans.PasswordBean;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.SYSTools;


import javax.swing.*;

/**
 * Created by tloehr on 10.07.15.
 */
public class PnlPasswordChange extends DefaultPanel {



    public PnlPasswordChange() {
        super("opde.settings.personal.password");
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        try {
            final PnlBeanEditor<PasswordBean> pbe = new PnlBeanEditor<>(() -> new PasswordBean(), PasswordBean.class, PnlBeanEditor.SAVE_MODE_OK_CANCEL);

            pbe.addDataChangeListener(evt -> {

                if (!evt.getData().getNewPassword().trim().equals(evt.getData().getNewPasswordAgain().trim())) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.settings.personal.newpw.wrong"));
                    return;
                }

                OPDE.getLogin().getUser().setHashed_pw(SYSTools.hashword(evt.getData().getNewPassword().trim(), "SHA-256"));
                OPDE.getLogin().setUser(EntityTools.merge(OPDE.getLogin().getUser()));

                pbe.reload();
                pbe.refreshDisplay();

                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.settings.personal.pwchanged"));

            });
            add(pbe);
        } catch (Exception e) {
            OPDE.fatal(e);
        }

    }


}
