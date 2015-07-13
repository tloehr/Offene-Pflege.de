package op.settings.subpanels;

import entity.EntityTools;
import entity.system.SYSPropsTools;
import entity.system.Users;
import entity.system.UsersTools;
import gui.PnlBeanEditor;
import gui.events.RelaxedDocumentListener;
import gui.interfaces.BoundedTextField;
import gui.interfaces.DefaultPanel;
import gui.parser.IntegerParser;
import op.OPDE;
import op.settings.databeans.PersonalMailBean;
import op.system.EMailSystem;
import op.system.Recipient;
import op.threads.DisplayMessage;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * Created by tloehr on 11.07.15.
 */
public class PnlUserMailSettings extends DefaultPanel {

    boolean checkInProgress = false;
    boolean lastCheckOk = false;
    Logger logger = Logger.getLogger(getClass());
    String mailaddress = "";
    boolean keyConfirmed = false;
    final JLabel lblLED = new JLabel();

    public PnlUserMailSettings() {
        super("opde.settings.personal.mail");
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        keyConfirmed = OPDE.getLogin().getUser().getMailConfirmed() >= UsersTools.MAIL_CONFIRMED;
        lblLED.setIcon(keyConfirmed ? SYSConst.icon22ledGreenOn : SYSConst.icon22ledRedOn);

        try {
            final PnlBeanEditor<PersonalMailBean> pbe = new PnlBeanEditor<>(() -> new PersonalMailBean(OPDE.getLogin().getUser()), PersonalMailBean.class);
            pbe.setCustomPanel(getButtonPanel(pbe));
            pbe.addDataChangeListener(evt -> mailaddress = evt.getData().getMail());
            add(pbe);
        } catch (Exception e) {
            OPDE.fatal(logger, e);
        }

    }


    private JPanel getButtonPanel(PnlBeanEditor<PersonalMailBean> pbe) {

        // todo: notifications noch einschaltbar machen
        // final YesNoToggleButton tbActive = new YesNoToggleButton("opde.settings.global.mail.active", "opde.settings.global.mail.inactive", SYSTools.catchNull(OPDE.getProps().getProperty(SYSPropsTools.KEY_MAIL_SYSTEM_ACTIVE)).equalsIgnoreCase("true"));
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
                        lblLED.setIcon(SYSConst.icon22ledRedOn);

                        Users myUser = OPDE.getLogin().getUser();
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
                                Users myUser = OPDE.getLogin().getUser();
                                myUser.setEMail(mailaddress);
                                myUser.setMailConfirmed(UsersTools.MAIL_UNCONFIRMED);
                                OPDE.getLogin().setUser(EntityTools.merge(myUser));
                            } else {
                                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.settings.personal.mail.fail", DisplayMessage.WARNING));
                            }

                        } catch (InterruptedException e1) {
                            lastCheckOk = false;
                            OPDE.warn(logger, e1);
                        } catch (ExecutionException e1) {
                            lastCheckOk = false;
                            OPDE.warn(logger, e1);
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
            }
            reload();
        });


        lblLED.setText(SYSTools.xx("opde.settings.personal.mail.key"));
        BoundedTextField btf = new BoundedTextField(4, 4);
        btf.setText(OPDE.getProps().getProperty(SYSPropsTools.KEY_MAIL_TESTKEY));
//        btf.getDocument().insertString(0, SYSTools.catchNull(OPDE.getProps().getProperty(SYSPropsTools.KEY_MAIL_TESTKEY)), null);
        btf.getDocument().addDocumentListener(new RelaxedDocumentListener(var1 -> {
            try {
                String text = var1.getDocument().getText(0, var1.getDocument().getLength());
                IntegerParser parser = new IntegerParser();
                parser.parse(text);
                keyConfirmed = text.equals(OPDE.getProps().getProperty(SYSPropsTools.KEY_MAIL_TESTKEY));
                lblLED.setIcon(keyConfirmed ? SYSConst.icon22ledGreenOn : SYSConst.icon22ledRedOn);
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(keyConfirmed ? "opde.settings.personal.mail.confirmed" : "opde.settings.personal.mail.not.confirmed"));
                if (keyConfirmed) {
                    Users myUser = OPDE.getLogin().getUser();
                    myUser.setMailConfirmed(UsersTools.MAIL_CONFIRMED);
                    OPDE.getLogin().setUser(EntityTools.merge(myUser));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (BadLocationException e) {
                e.printStackTrace();
            }

        }));
        btf.setEnabled(OPDE.getProps().containsKey(SYSPropsTools.KEY_MAIL_TESTKEY));

        Box line1 = Box.createHorizontalBox();
        line1.add(btnTestmail);
        line1.add(Box.createRigidArea(new Dimension(50, 00)));
        line1.add(lblLED);
        line1.add(Box.createHorizontalStrut(5));
        line1.add(btf);
        line1.add(Box.createHorizontalGlue());

//        line.add(Box.createHorizontalGlue());
//        line.add(tbActive);

        Box page = Box.createVerticalBox();
        page.add(Box.createRigidArea(new Dimension(0, 10)));
        page.add(new JSeparator());
        page.add(Box.createRigidArea(new Dimension(0, 10)));
        page.add(line1);
//        page.add(line2);
        page.add(Box.createVerticalGlue());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
        content.add(page);

        return content;

    }

}
