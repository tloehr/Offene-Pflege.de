package de.offene_pflege.op.settings.subpanels;

import de.offene_pflege.entity.EntityTools;
import de.offene_pflege.entity.system.SYSPropsTools;
import de.offene_pflege.entity.system.OPUsers;
import de.offene_pflege.entity.system.UsersTools;
import de.offene_pflege.gui.PnlBeanEditor;
import de.offene_pflege.gui.events.RelaxedDocumentListener;
import de.offene_pflege.gui.interfaces.BoundedTextField;
import de.offene_pflege.gui.interfaces.DefaultPanel;
import de.offene_pflege.gui.interfaces.YesNoToggleButton;

import de.offene_pflege.gui.parser.IntegerParser;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.settings.databeans.PersonalMailBean;
import de.offene_pflege.op.system.EMailSystem;
import de.offene_pflege.op.system.Recipient;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;
import lombok.extern.log4j.Log4j2;


import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.ParseException;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * Created by tloehr on 11.07.15.
 */
@Log4j2
public class PnlUserMailSettings extends DefaultPanel {

    boolean checkInProgress = false;
    boolean lastCheckOk = false;

    String mailaddress = "";
    boolean keyConfirmed = false;
    final JLabel lblLED = new JLabel();
    final YesNoToggleButton tbNotifications;

    public PnlUserMailSettings() {
        super("opde.settings.personal.mail");
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        keyConfirmed = OPDE.getLogin().getUser().getMailConfirmed() >= UsersTools.MAIL_CONFIRMED;
        lblLED.setIcon(keyConfirmed ? SYSConst.icon22ledGreenOn : SYSConst.icon22ledRedOn);

        tbNotifications = new YesNoToggleButton("opde.settings.personal.enable.notification", "opde.settings.personal.disable.notification", OPDE.getLogin().getUser().getMailConfirmed() == UsersTools.MAIL_NOTIFICATIONS_ENABLED);
        tbNotifications.setEnabled(keyConfirmed);

        try {
            final PnlBeanEditor<PersonalMailBean> pbe = new PnlBeanEditor<>(() -> new PersonalMailBean(OPDE.getLogin().getUser()), PersonalMailBean.class);
            pbe.setCustomPanel(getButtonPanel(pbe));
            pbe.addDataChangeListener(evt -> mailaddress = evt.getData().getMail());
            add(pbe);
        } catch (Exception e) {
            OPDE.fatal(e);
        }

        tbNotifications.addItemListener(ie -> {
            if (ie.getStateChange() != ItemEvent.SELECTED && ie.getStateChange() != ItemEvent.DESELECTED) return;
            OPUsers myUser = OPDE.getLogin().getUser();
            myUser.setMailConfirmed(ie.getStateChange() == ItemEvent.SELECTED ? UsersTools.MAIL_NOTIFICATIONS_ENABLED : UsersTools.MAIL_CONFIRMED);
            OPDE.getLogin().setUser(EntityTools.merge(myUser));
        });

    }


    private JPanel getButtonPanel(PnlBeanEditor<PersonalMailBean> pbe) {
        final JButton btnTestmail = new JButton(SYSTools.xx("opde.settings.global.mail.sendtest"));
        btnTestmail.addActionListener(e -> {
            try {
                if (checkInProgress) return;
                checkInProgress = true;
                pbe.broadcast();

                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.settings.personal.mail.testing"));

                SwingWorker worker = new SwingWorker() {
                    @Override
                    protected Object doInBackground() {
                        lastCheckOk = false;
                        keyConfirmed = false;
                        tbNotifications.setEnabled(keyConfirmed);
                        lblLED.setIcon(SYSConst.icon22ledRedOn);

                        OPUsers myUser = OPDE.getLogin().getUser();
                        myUser.setMailConfirmed(UsersTools.MAIL_UNCONFIRMED);
                        OPDE.getLogin().setUser(EntityTools.merge(myUser));

                        Random generator = new Random(System.currentTimeMillis());
                        String testkey = SYSTools.padL(Integer.toString(generator.nextInt(9999)), 4, "0");
                        SYSPropsTools.storeProp(SYSPropsTools.KEY_MAIL_TESTKEY, testkey, OPDE.getLogin().getUser()); // this key is always stored centrally in the system's properties. it belongs to the current user.
                        return EMailSystem.sendMail(SYSTools.xx("opde.settings.global.mail.testsubject"), SYSTools.xx("opde.settings.personal.confirmmail.testbody") + "<br/>" + SYSConst.html_h2(SYSTools.xx("opde.settings.personal.mail.key") + ": " + testkey), new Recipient(mailaddress.trim(), OPDE.getLogin().getUser().getFullname()), null);
                    }

                    @Override
                    protected void done() {
                        checkInProgress = false;

                        try {
                            lastCheckOk = (Boolean) get();

                            if (lastCheckOk) {
                                OPUsers myUser = OPDE.getLogin().getUser();
                                myUser.setEMail(mailaddress);
                                myUser.setMailConfirmed(UsersTools.MAIL_UNCONFIRMED);
                                OPDE.getLogin().setUser(EntityTools.merge(myUser));
                            } else {
                                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.settings.personal.mail.fail", DisplayMessage.WARNING));
                            }

                        } catch (InterruptedException e1) {
                            lastCheckOk = false;
                            log.warn( e1);
                        } catch (ExecutionException e1) {
                            lastCheckOk = false;
                            log.warn( e1);
                        }

                    }
                };

                worker.execute();

            } catch (ConstraintViolationException cve) {
                String violations = "";
                for (ConstraintViolation cv : cve.getConstraintViolations()) {
                    violations += cv.getMessage() + "; ";
                }
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(violations, DisplayMessage.WARNING));
            } catch (InvocationTargetException e1) {
                e1.printStackTrace();
            } catch (NoSuchMethodException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (SQLIntegrityConstraintViolationException e1) {
                e1.printStackTrace();
            }
            reload();
        });


        lblLED.setText(SYSTools.xx("opde.settings.personal.mail.key"));
        BoundedTextField btf = new BoundedTextField(4, 4);
        btf.setText(OPDE.getLogin().getUser().getMailConfirmed() == UsersTools.MAIL_UNCONFIRMED ? "" : OPDE.getProps().getProperty(SYSPropsTools.KEY_MAIL_TESTKEY));
//        btf.getDocument().insertString(0, SYSTools.catchNull(OPDE.getProps().getProperty(SYSPropsTools.KEY_MAIL_TESTKEY)), null);
        btf.getDocument().addDocumentListener(new RelaxedDocumentListener(var1 -> {
            try {
                if (!OPDE.getProps().containsKey(SYSPropsTools.KEY_MAIL_TESTKEY)) return;

                String text = var1.getDocument().getText(0, var1.getDocument().getLength());
                IntegerParser parser = new IntegerParser();
                parser.parse(text);
                keyConfirmed = text.equals(OPDE.getProps().getProperty(SYSPropsTools.KEY_MAIL_TESTKEY));
                tbNotifications.setEnabled(keyConfirmed);
                lblLED.setIcon(keyConfirmed ? SYSConst.icon22ledGreenOn : SYSConst.icon22ledRedOn);
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(keyConfirmed ? "opde.settings.personal.mail.confirmed" : "opde.settings.personal.mail.not.confirmed"));
                if (keyConfirmed) {
                    OPUsers myUser = OPDE.getLogin().getUser();
                    myUser.setMailConfirmed(UsersTools.MAIL_CONFIRMED);
                    OPDE.getLogin().setUser(EntityTools.merge(myUser));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (BadLocationException e) {
                e.printStackTrace();
            }

        }));

        Box line1 = Box.createHorizontalBox();
        line1.add(btnTestmail);
        line1.add(Box.createRigidArea(new Dimension(50, 00)));
        line1.add(lblLED);
        line1.add(Box.createHorizontalStrut(5));
        line1.add(btf);
        line1.add(Box.createHorizontalGlue());

//        Box line2 = Box.createHorizontalBox();
//        line2.add(lbl);
//        line2.add(Box.createRigidArea(new Dimension(50, 00)));
//        line2.add(lblLED);
//        line2.add(Box.createHorizontalStrut(5));
//        line2.add(btf);
//        line2.add(Box.createHorizontalGlue());


//        line.add(Box.createHorizontalGlue());
//        line.add(tbActive);

        Box page = Box.createVerticalBox();
        page.add(Box.createRigidArea(new Dimension(0, 10)));
        page.add(new JSeparator());
        page.add(Box.createRigidArea(new Dimension(0, 10)));
        page.add(line1);
        page.add(Box.createRigidArea(new Dimension(0, 10)));
        page.add(tbNotifications);

        page.add(Box.createVerticalGlue());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
        content.add(page);

        return content;

    }

}
