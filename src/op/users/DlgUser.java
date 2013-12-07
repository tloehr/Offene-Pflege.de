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
import com.jidesoft.swing.DefaultOverlayable;
import com.jidesoft.swing.JideBoxLayout;
import entity.roster.Rosters;
import entity.roster.RostersTools;
import entity.roster.UserContract;
import entity.roster.UserContracts;
import entity.system.Users;
import entity.system.UsersTools;
import op.OPDE;
import op.system.InternalClassACL;
import op.threads.DisplayMessage;
import op.tools.DefaultCPTitle;
import op.tools.MyJDialog;
import op.tools.SYSCalendar;
import op.tools.SYSTools;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Torsten Löhr
 */
public class DlgUser extends MyJDialog {
    public static final String internalClassID = "opde.users.dlgusers";
    private Users user;
    private Closure callback;
    private Map<String, CollapsiblePane> cpMap;
    UserContracts userContracts;
    private JLabel lblFirstname, lblName, lblPW, lblUID, lblEmail;
    private JTextField txtName, txtEMail, txtVorname, txtPW, txtUID;

    public DlgUser(Users user, Closure callback) {
        super(false);
        this.user = user;
        this.callback = callback;
        initComponents();
        initDialog();
        pack();
        setVisible(true);
    }

    private void initDialog() {
        cpMap = Collections.synchronizedMap(new HashMap<String, CollapsiblePane>());

        userContracts = UsersTools.getContracts(user);
        if (userContracts != null) {
            Collections.sort(userContracts.getListContracts());
            for (UserContract userContract : userContracts.getListContracts()) {
                createCP4(userContract);
            }
            cpsContracts.removeAll();
            cpsContracts.setLayout(new JideBoxLayout(cpsContracts, JideBoxLayout.Y_AXIS));

            synchronized (cpMap) {
                for (UserContract userContract : userContracts.getListContracts()) {
                    final String key = userContract.hashCode() + ".contract";
                    cpsContracts.add(cpMap.get(key));
                }
            }
            cpsContracts.addExpansion();
        }


        lblFirstname = new JLabel(OPDE.lang.getString("misc.msg.firstname"));
        lblName = new JLabel(OPDE.lang.getString("misc.msg.name"));
        lblPW = new JLabel(OPDE.lang.getString("misc.msg.password"));
        lblUID = new JLabel(OPDE.lang.getString("misc.msg.uid"));
        lblEmail = new JLabel(OPDE.lang.getString("misc.msg.email"));

        txtName = new JTextField(user.getName());
        txtEMail = new JTextField(user.getEMail());
        txtVorname = new JTextField(user.getVorname());
        txtUID = new JTextField(user.getUID());
        txtPW = new JTextField();

        DefaultOverlayable overUID = new DefaultOverlayable(txtUID);
//        lblUID.setHorizontalTextPosition(SwingConstants.TRAILING);
        lblUID.setForeground(Color.LIGHT_GRAY);
        overUID.addOverlayComponent(lblUID,SwingConstants.EAST);

        pnlMain.add(overUID, CC.xy(3, 3, CC.FILL, CC.DEFAULT));

        DefaultOverlayable overFirstname = new DefaultOverlayable(txtVorname);
        lblFirstname.setHorizontalTextPosition(SwingConstants.TRAILING);
        lblFirstname.setForeground(Color.LIGHT_GRAY);
        overFirstname.addOverlayComponent(lblFirstname);
        pnlMain.add(overFirstname, CC.xy(3, 5, CC.FILL, CC.DEFAULT));

        DefaultOverlayable overName = new DefaultOverlayable(txtName);
        lblName.setForeground(Color.LIGHT_GRAY);
        overName.addOverlayComponent(lblName);
        pnlMain.add(overName, CC.xy(3, 7, CC.FILL, CC.DEFAULT));

        DefaultOverlayable overEmail = new DefaultOverlayable(txtEMail);
        lblEmail.setForeground(Color.LIGHT_GRAY);
        overEmail.addOverlayComponent(lblEmail);
        pnlMain.add(overEmail, CC.xy(3, 9, CC.FILL, CC.DEFAULT));

        DefaultOverlayable overPW = new DefaultOverlayable(txtPW);
        lblPW.setForeground(Color.LIGHT_GRAY);
        overPW.addOverlayComponent(lblPW);
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

    private CollapsiblePane createCP4(UserContract contract) {
        final String key = contract.hashCode() + ".contract";
        synchronized (cpMap) {
            if (!cpMap.containsKey(key)) {
                cpMap.put(key, new CollapsiblePane());
                try {
                    cpMap.get(key).setCollapsed(true);
                } catch (PropertyVetoException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

            }
        }
        final CollapsiblePane cpContract = cpMap.get(key);

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

        cpContract.setTitleLabelComponent(cptitle.getMain());
        cpContract.setSlidingDirection(SwingConstants.SOUTH);

        cpContract.setBackground(Color.WHITE);
        cpContract.setOpaque(false);
        cpContract.setHorizontalAlignment(SwingConstants.LEADING);

        cpContract.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                cpContract.setContentPane(createContentPane4(roster));
            }
        });

        if (!cpContract.isCollapsed()) {
//            cpRoster.setContentPane(createContentPane4(roster));
        }


        return cpContract;
    }


    private JPanel createContentPane4(final UserContract contract) {
            JPanel pnlContract = new JPanel(new VerticalLayout());
            pnlContract.setOpaque(false);
            LocalDate month = new LocalDate(roster.getMonth());

            ArrayList<Users> listAllPossibleUsers = new ArrayList<Users>(RostersTools.getAllUsersIn(roster));
            ArrayList<Users> listUsers = new ArrayList<Users>();
            if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, internalClassID) || OPDE.getAppInfo().isAllowedTo(InternalClassACL.USER1, internalClassID)) {
                listUsers.addAll(listAllPossibleUsers);
            } else if (listAllPossibleUsers.contains(OPDE.getLogin().getUser())) {
                listUsers.add(OPDE.getLogin().getUser());
            }
            Collections.sort(listUsers);
            listAllPossibleUsers.clear();

            final LocalDate start = SYSCalendar.bow(SYSCalendar.bom(month));
            final LocalDate end = SYSCalendar.bow(SYSCalendar.eom(month));

            for (LocalDate week = start; !week.isAfter(end); week = week.plusWeeks(1)) {
                pnlContract.add(createCP4(week, roster, listUsers));
            }

            return pnlContract;
        }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        pnlMain = new JPanel();
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
                "14dlu, 4*($lgap, fill:default), $lgap, default, 9dlu, default, $lgap, 14dlu"));

            //======== scrlContracts ========
            {
                scrlContracts.setViewportView(cpsContracts);
            }
            pnlMain.add(scrlContracts, CC.xywh(5, 3, 1, 9));

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
            pnlMain.add(jPanel3, CC.xy(5, 13, CC.RIGHT, CC.DEFAULT));
        }
        contentPane.add(pnlMain);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel pnlMain;
    private JScrollPane scrlContracts;
    private CollapsiblePanes cpsContracts;
    private JPanel jPanel3;
    private JButton btnCancel;
    private JButton btnSave;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
