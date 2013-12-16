/*
 * Created by JFormDesigner on Tue Aug 28 16:03:54 CEST 2012
 */

package op.users;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.DefaultOverlayable;
import com.jidesoft.swing.JideBoxLayout;
import entity.roster.ContractsParameterSet;
import entity.roster.UserContract;
import entity.roster.UserContracts;
import entity.system.Users;
import entity.system.UsersTools;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.beans.PropertyVetoException;
import java.util.Collections;

/**
 * @author Torsten Löhr
 */
public class DlgUser extends MyJDialog {
    public static final String internalClassID = "opde.users.dlgusers";
    private Users user;
    private Closure callback;
    //    private Map<String, CollapsiblePane> cpMap;
    UserContracts userContracts;
    private JLabel lblFirstname, lblName, lblPW, lblUID, lblEmail;
    private JTextField txtName, txtEMail, txtVorname, txtPW, txtUID;
    private JDialog me;

    public DlgUser(Users user, Closure callback) {
        super(false);
        this.user = user;
        this.callback = callback;
        me = this;
        initComponents();

        userContracts = UsersTools.getContracts(user);
        initDialog();
        pack();
        setVisible(true);

    }

    private void initDialog() {
//        cpMap = Collections.synchronizedMap(new HashMap<String, CollapsiblePane>());

        if (userContracts == null) {

            cpsContracts.removeAll();
            cpsContracts.setLayout(new JideBoxLayout(cpsContracts, JideBoxLayout.Y_AXIS));

            if (user.isActive()) {

                cpsContracts.add(GUITools.createHyperlinkButton(OPDE.lang.getString("no.entry.yet"), SYSConst.icon22add, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        UserContract contract = new UserContract(new ContractsParameterSet());
                        contract.getDefaults().setExam(UsersTools.isQualified(user));
                        JidePopup popup = GUITools.createPanelPopup(new PnlContractsEditor(contract, true), new Closure() {
                            @Override
                            public void execute(Object o) {
                                if (o != null) {
                                    UserContracts contracts = new UserContracts();
                                    contracts.add((UserContract) o);

                                    user.setXml(contracts.toXML());
                                    userContracts = UsersTools.getContracts(user);

                                    OPDE.debug(user.getXml());
                                    initDialog();
                                }
                            }
                        }, me);
                        GUITools.showPopup(popup, SwingUtilities.CENTER);
                    }
                }));
            } else {
                cpsContracts.add(new JLabel(OPDE.lang.getString("opde.users.dlgusers.not.active.no.contract")));
            }
            cpsContracts.addExpansion();


        } else {

            cpsContracts.removeAll();
            cpsContracts.setLayout(new JideBoxLayout(cpsContracts, JideBoxLayout.Y_AXIS));

            Collections.sort(userContracts.getListContracts());
            for (UserContract userContract : userContracts.getListContracts()) {
                cpsContracts.add(createCP4(userContract));
            }

            cpsContracts.addExpansion();
        }


        lblFirstname = new JLabel(OPDE.lang.getString("misc.msg.firstname") + " ");
        lblName = new JLabel(OPDE.lang.getString("misc.msg.name") + " ");
        lblPW = new JLabel(OPDE.lang.getString("misc.msg.password") + " ");
        lblUID = new JLabel(OPDE.lang.getString("misc.msg.uid") + " ");
        lblEmail = new JLabel(OPDE.lang.getString("misc.msg.email") + " ");

        txtName = new JTextField(user.getName());
        txtEMail = new JTextField(user.getEMail());
        txtVorname = new JTextField(user.getVorname());
        txtUID = new JTextField(user.getUID());
        txtPW = new JTextField();

        DefaultOverlayable overUID = new DefaultOverlayable(txtUID);
        lblUID.setForeground(SYSConst.bluegrey.darker());
        overUID.addOverlayComponent(lblUID, SwingConstants.EAST);

        pnlMain.add(overUID, CC.xy(3, 3, CC.FILL, CC.DEFAULT));

        DefaultOverlayable overFirstname = new DefaultOverlayable(txtVorname);
        lblFirstname.setForeground(SYSConst.bluegrey.darker());
        overFirstname.addOverlayComponent(lblFirstname, SwingConstants.EAST);
        pnlMain.add(overFirstname, CC.xy(3, 5, CC.FILL, CC.DEFAULT));

        DefaultOverlayable overName = new DefaultOverlayable(txtName);
        lblName.setForeground(SYSConst.bluegrey.darker());
        overName.addOverlayComponent(lblName, SwingConstants.EAST);
        pnlMain.add(overName, CC.xy(3, 7, CC.FILL, CC.DEFAULT));

        DefaultOverlayable overEmail = new DefaultOverlayable(txtEMail);
        lblEmail.setForeground(SYSConst.bluegrey.darker());
        overEmail.addOverlayComponent(lblEmail, SwingConstants.EAST);
        pnlMain.add(overEmail, CC.xy(3, 9, CC.FILL, CC.DEFAULT));

        DefaultOverlayable overPW = new DefaultOverlayable(txtPW);
        lblPW.setForeground(SYSConst.bluegrey.darker());
        overPW.addOverlayComponent(lblPW, SwingConstants.EAST);
        pnlMain.add(overPW, CC.xy(3, 11, CC.FILL, CC.DEFAULT));

        txtPW.setEnabled(user.getUID() == null);
        txtUID.setEnabled(user.getUID() == null);

    }

    private void txtNameFocusLost(FocusEvent e) {
        if (txtPW.isEnabled() && txtName.getText().isEmpty() && !txtVorname.getText().isEmpty()) {
            txtPW.setText(SYSTools.generatePassword(txtVorname.getText(), txtName.getText()));
        }
    }

    private void txtVornameFocusLost(FocusEvent e) {
        if (txtPW.isEnabled() && !txtName.getText().isEmpty() && !txtVorname.getText().isEmpty()) {
            txtPW.setText(SYSTools.generatePassword(txtVorname.getText(), txtName.getText()));
        }
    }

    private void btnCancelActionPerformed(ActionEvent e) {
        user = null;
        dispose();
    }

    @Override
    public void dispose() {
        super.dispose();
        callback.execute(user);
    }


    private void btnSaveActionPerformed(ActionEvent e) {
        if (txtName.getText().isEmpty() || txtVorname.getText().isEmpty() || (txtPW.isEnabled() && txtPW.getText().isEmpty()) || txtUID.getText().isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.emptyentry"));
            return;
        }

        if (!txtEMail.getText().isEmpty() && !SYSTools.isValidEMail(txtEMail.getText().trim())) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".wrongemail")));
            return;
        }

        if (txtUID.isEnabled()) {
            EntityManager em = OPDE.createEM();
            Users check4user = em.find(Users.class, txtUID.getText().trim());
            em.close();
            if (check4user != null) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".uidtaken")));
                return;
            }
        }

        user.setEMail(txtEMail.getText().isEmpty() ? null : txtEMail.getText().trim());
        user.setVorname(txtVorname.getText().trim());
        user.setNachname(txtName.getText().trim());

        if (txtUID.isEnabled()) {
            user.setMd5pw(SYSTools.hashword(txtPW.getText()));
            user.setUID(txtUID.getText().trim());
            SYSTools.printpw(txtPW.getText().trim(), user);
        }

        dispose();
    }

    private CollapsiblePane createCP4(final UserContract contract) {

        final CollapsiblePane cpContract = new CollapsiblePane();

        String title = "<html>" + contract.getPeriodAsHTML() + "</html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cpContract.setCollapsed(!cpContract.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            }
        });

        final JButton btnMenu = new JButton(SYSConst.icon22menu);
        btnMenu.setPressedIcon(SYSConst.icon22Pressed);
        btnMenu.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnMenu.setAlignmentY(Component.TOP_ALIGNMENT);
        btnMenu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnMenu.setContentAreaFilled(false);
        btnMenu.setBorder(null);
        btnMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JidePopup popup = new JidePopup();
                popup.setMovable(false);
                popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
                popup.setOwner(btnMenu);
                popup.removeExcludedComponent(btnMenu);
                JPanel pnl = getMenu(contract);
                popup.getContentPane().add(pnl);
                popup.setDefaultFocusComponent(pnl);

                GUITools.showPopup(popup, SwingConstants.WEST);
            }
        });
        cptitle.getRight().add(btnMenu);

        cpContract.setTitleLabelComponent(cptitle.getMain());
        cpContract.setSlidingDirection(SwingConstants.SOUTH);


        cpContract.setBackground(Color.WHITE);
        cpContract.setOpaque(false);
        cpContract.setHorizontalAlignment(SwingConstants.LEADING);

        cpContract.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                cpContract.setContentPane(new PnlContractsEditor(contract, false));
            }
        });

        if (!cpContract.isCollapsed()) {
            cpContract.setContentPane(new PnlContractsEditor(contract, false));
        }


        return cpContract;
    }


    private JPanel getMenu(final UserContract contract) {
        final JPanel pnlMenu = new JPanel(new VerticalLayout());

        JButton btnEndContract = GUITools.createHyperlinkButton("opde.users.dlgusers.end.contract", SYSConst.icon22playerStop, null);
        btnEndContract.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnEndContract.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                LocalDate min = contract.getDefaults().getFrom().plusDays(1);
                LocalDate max = SYSConst.LD_UNTIL_FURTHER_NOTICE;
                PnlDay pnlDay = new PnlDay(min, max, "opde.users.dlgusers.when.end.contract");
                GUITools.showPopup(GUITools.createPanelPopup(pnlDay, new Closure() {
                    @Override
                    public void execute(final Object date) {
                        int i = userContracts.getListContracts().indexOf(contract);
                        userContracts.getListContracts().get(i).endOn(new LocalDate(date));
                        OPDE.debug(userContracts.toXML());
                        initDialog();
                    }
                }, me), SwingConstants.CENTER);
            }
        });
        btnEndContract.setEnabled(true);
        pnlMenu.add(btnEndContract);

        JButton btnNewProbation = GUITools.createHyperlinkButton("opde.users.dlgusers.new.probation", SYSConst.icon22add, null);
        btnNewProbation.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnNewProbation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                LocalDate from = SYSCalendar.bom(new LocalDate().plusMonths(1));
                LocalDate to = SYSCalendar.eom(from.plusMonths(6));
                final PnlFromTo pnlFromTo = new PnlFromTo(from, to, "opde.users.dlgusers.period.add.probation");
                GUITools.showPopup(GUITools.createPanelPopup(pnlFromTo, new Closure() {
                    @Override
                    public void execute(final Object o) {
                        if (o != null) {
                            final Pair<LocalDate, LocalDate> period = (Pair<LocalDate, LocalDate>) o;
                            contract.addProbation(period);
                            int i = userContracts.getListContracts().indexOf(contract);
                            userContracts.getListContracts().set(i, contract);
                            OPDE.debug(userContracts.toXML());
                            initDialog();
                        }
                    }
                }, me), SwingConstants.CENTER);
            }
        });
        btnNewProbation.setEnabled(true);
        pnlMenu.add(btnNewProbation);

        JButton btnNewExtension = GUITools.createHyperlinkButton("opde.users.dlgusers.new.extension", SYSConst.icon22add, null);
        btnNewExtension.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnNewExtension.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                LocalDate from = SYSCalendar.bom(new LocalDate().plusMonths(1));
                LocalDate to = SYSCalendar.eom(from.plusMonths(6));
                final PnlFromTo pnlFromTo = new PnlFromTo(from, to, "opde.users.dlgusers.period.add.extension");
                GUITools.showPopup(GUITools.createPanelPopup(pnlFromTo, new Closure() {
                    @Override
                    public void execute(final Object o) {
                        if (o != null) {
                            final Pair<LocalDate, LocalDate> period = (Pair<LocalDate, LocalDate>) o;
                            contract.addExtension(period);
                            int i = userContracts.getListContracts().indexOf(contract);
                            userContracts.getListContracts().set(i, contract);
                            OPDE.debug(userContracts.toXML());
                            initDialog();
                        }
                    }
                }, me), SwingConstants.CENTER);

            }
        });
        btnNewExtension.setEnabled(true);
        pnlMenu.add(btnNewExtension);


        JButton btnSetUnlimited = GUITools.createHyperlinkButton("opde.users.dlgusers.set.unlimited", SYSConst.icon22add, null);
        btnSetUnlimited.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnSetUnlimited.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                contract.getDefaults().setTo(SYSConst.LD_UNTIL_FURTHER_NOTICE);
                int i = userContracts.getListContracts().indexOf(contract);
                userContracts.getListContracts().set(i, contract);
                OPDE.debug(userContracts.toXML());
                initDialog();
            }
        });
        btnSetUnlimited.setEnabled(true);
        pnlMenu.add(btnSetUnlimited);

        return pnlMenu;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        pnlMain = new JPanel();
        lbl = new JLabel();
        scrlContracts = new JScrollPane();
        cpsContracts = new CollapsiblePanes();
        jPanel3 = new JPanel();
        btnCancel = new JButton();
        btnSave = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        //======== pnlMain ========
        {
            pnlMain.setLayout(new FormLayout(
                    "14dlu, $lcgap, 160dlu:grow, $ugap, 229dlu:grow, $lcgap, 14dlu",
                    "14dlu, $lgap, default, 4*($lgap, fill:default), $lgap, default, fill:135dlu, default, $lgap, 14dlu"));

            //---- lbl ----
            lbl.setText("Arbeitsvertr\u00e4ge");
            lbl.setFont(new Font("Arial", Font.PLAIN, 11));
            lbl.setHorizontalAlignment(SwingConstants.TRAILING);
            pnlMain.add(lbl, CC.xy(5, 3));

            //======== scrlContracts ========
            {
                scrlContracts.setViewportView(cpsContracts);
            }
            pnlMain.add(scrlContracts, CC.xywh(5, 5, 1, 10));

            //======== jPanel3 ========
            {
                jPanel3.setLayout(new BoxLayout(jPanel3, BoxLayout.X_AXIS));

                //---- btnCancel ----
                btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
                btnCancel.setToolTipText("Abbrechen");
                btnCancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnCancelActionPerformed(e);
                    }
                });
                jPanel3.add(btnCancel);

                //---- btnSave ----
                btnSave.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                btnSave.setToolTipText("Sichern");
                btnSave.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnSaveActionPerformed(e);
                    }
                });
                jPanel3.add(btnSave);
            }
            pnlMain.add(jPanel3, CC.xy(5, 15, CC.RIGHT, CC.DEFAULT));
        }
        contentPane.add(pnlMain);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel pnlMain;
    private JLabel lbl;
    private JScrollPane scrlContracts;
    private CollapsiblePanes cpsContracts;
    private JPanel jPanel3;
    private JButton btnCancel;
    private JButton btnSave;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
