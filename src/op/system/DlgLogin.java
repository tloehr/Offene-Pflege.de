/*
 * OffenePflege
 * Copyright (C) 2006-2012 Torsten Löhr
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License V2 as published by the Free Software Foundation
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even 
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to 
 * the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
 * www.offene-pflege.de
 * ------------------------ 
 * Auf deutsch (freie Übersetzung. Rechtlich gilt die englische Version)
 * Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU General Public License, 
 * wie von der Free Software Foundation veröffentlicht, weitergeben und/oder modifizieren, gemäß Version 2 der Lizenz.
 *
 * Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, daß es Ihnen von Nutzen sein wird, aber 
 * OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN 
 * BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht, 
 * schreiben Sie an die Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA.
 */
package op.system;

import entity.roster.RostersTools;
import entity.system.SYSLoginTools;
import entity.system.SYSPropsTools;
import op.OPDE;
import op.roster.PnlTimeClock;
import op.threads.DisplayMessage;
import op.tools.MyJDialog;
import op.tools.SYSConst;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.VerticalLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author __USER__
 */
public class DlgLogin extends MyJDialog {

    public static final String internalClassID = "dlglogin";
    private JPanel pnl;
    private ComponentAdapter componentAdapter;
    private Closure actionBlock;

    private void btnExitActionPerformed(ActionEvent e) {
        SYSLoginTools.logout();
        dispose();
    }

    private void thisWindowActivated(WindowEvent e) {
        txtUsername.requestFocus();
    }

    private void btnLoginActionPerformed(ActionEvent e) {
        if (SYSPropsTools.isTrue(SYSPropsTools.KEY_MAINTENANCE_MODE, null)) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("dlglogin.maintenance.mode", DisplayMessage.IMMEDIATELY, 5));
            return;
        }

        if (OPDE.getLogin() != null) {
            dispose();
            return;
        }

        String username = txtUsername.getText().trim();

        try {
            registerLogin();
            if (OPDE.getLogin() == null) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.usernameOrPasswordWrong"));
                OPDE.info(OPDE.lang.getString("misc.msg.usernameOrPasswordWrong") + ": " + username + "  " + OPDE.lang.getString("misc.msg.triedPassword") + ": " + new String(txtPassword.getPassword()));
            } else {
                OPDE.initProps();
                OPDE.info("Login: " + username + "  LoginID: " + OPDE.getLogin().getLoginID());
                dispose();
            }

        } catch (Exception se) {
            OPDE.fatal(se);
            System.exit(1);
        }
    }

    private void btnTimeclockActionPerformed(ActionEvent e) {
        if (RostersTools.getAll(RostersTools.SECTION_CARE).isEmpty()) {
            return;
        }

        if (pnl == null) {

            if (SYSPropsTools.isTrue(SYSPropsTools.KEY_MAINTENANCE_MODE, null)) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("dlglogin.maintenance.mode", DisplayMessage.IMMEDIATELY, 5));
                return;
            }

            String username = txtUsername.getText().trim();

            try {
                registerLogin();
                if (OPDE.getLogin() == null) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.usernameOrPasswordWrong"));
                    OPDE.info(OPDE.lang.getString("misc.msg.usernameOrPasswordWrong") + ": " + username + "  " + OPDE.lang.getString("misc.msg.triedPassword") + ": " + new String(txtPassword.getPassword()));
                } else {
                    OPDE.initProps();
                    OPDE.info("Login: " + username + "  LoginID: " + OPDE.getLogin().getLoginID());
                }

            } catch (Exception se) {
                OPDE.fatal(se);
                System.exit(1);
            }

            if (OPDE.getLogin() != null) {

                OPDE.getMainframe().setLabelUser(OPDE.getLogin().getUser().getFullname());

                pnl = new PnlTimeClock();
                add(pnl, BorderLayout.EAST);
                btnTimeclock.setIcon(SYSConst.icon222leftArrow);
            } else {
                OPDE.getMainframe().setLabelUser("--");
            }

        } else {

            btnTimeclock.setIcon(SYSConst.icon222rightArrow);
            remove(pnl);
            pnl.removeComponentListener(componentAdapter);
            componentAdapter = null;
            pnl = null;

            SYSLoginTools.logout();
            OPDE.getMainframe().setLabelUser("--");

            // After the logout a forced garbage collection seems to be adequate
            System.gc();

        }

        txtUsername.setText(null);
        txtPassword.setText(null);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                pack();
                setLocationRelativeTo(getOwner());
            }
        });
    }

    public DlgLogin(Closure actionBlock) {
        super(false);

        this.actionBlock = actionBlock;

        initComponents();

        String defaultlogin = "";

        String defaultpw = "";
        if (OPDE.getLocalProps().containsKey("defaultlogin")) {
            defaultlogin = OPDE.getLocalProps().getProperty("defaultlogin");
        }
        if (OPDE.getLocalProps().containsKey("defaultpw")) {
            defaultpw = OPDE.getLocalProps().getProperty("defaultpw");
        }
        txtUsername.setText(defaultlogin);
        txtPassword.setText(defaultpw);
        lblUsername.setText(OPDE.lang.getString("misc.msg.username"));
        lblPassword.setText(OPDE.lang.getString("misc.msg.password"));
        pack();
        setVisible(true);
    }


    /**
     * This method is called from within the constructor to
     * initialize the printerForm.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the PrinterForm Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        pnlMain = new JPanel();
        pnlButtons = new JPanel();
        btnExit = new JButton();
        pnlLogin = new JPanel();
        lblOPDE = new JLabel();
        btnAbout = new JButton();
        lblUsername = new JLabel();
        txtUsername = new JTextField();
        lblPassword = new JLabel();
        panel6 = new JPanel();
        txtPassword = new JPasswordField();
        btnLogin = new JButton();
        btnTimeclock = new JButton();

        //======== this ========
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                thisWindowActivated(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        //======== pnlMain ========
        {
            pnlMain.setBorder(new EmptyBorder(15, 15, 15, 15));
            pnlMain.setLayout(new BorderLayout());

            //======== pnlButtons ========
            {
                pnlButtons.setLayout(new BoxLayout(pnlButtons, BoxLayout.LINE_AXIS));

                //---- btnExit ----
                btnExit.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/exit.png")));
                btnExit.setContentAreaFilled(false);
                btnExit.setBorder(new EmptyBorder(5, 5, 5, 5));
                btnExit.setSelectedIcon(null);
                btnExit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnExit.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/exit_pressed.png")));
                btnExit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnExitActionPerformed(e);
                    }
                });
                pnlButtons.add(btnExit);
            }
            pnlMain.add(pnlButtons, BorderLayout.SOUTH);

            //======== pnlLogin ========
            {
                pnlLogin.setBorder(new EmptyBorder(5, 5, 5, 5));
                pnlLogin.setOpaque(false);
                pnlLogin.setLayout(new VerticalLayout());

                //---- lblOPDE ----
                lblOPDE.setText("Offene-Pflege.de");
                lblOPDE.setFont(new Font("Arial", Font.PLAIN, 24));
                lblOPDE.setHorizontalAlignment(SwingConstants.CENTER);
                pnlLogin.add(lblOPDE);

                //---- btnAbout ----
                btnAbout.setIcon(new ImageIcon(getClass().getResource("/artwork/256x256/opde-logo.png")));
                btnAbout.setBorderPainted(false);
                btnAbout.setBorder(null);
                btnAbout.setOpaque(false);
                btnAbout.setContentAreaFilled(false);
                btnAbout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnAbout.setToolTipText(null);
                btnAbout.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnAboutActionPerformed(e);
                    }
                });
                pnlLogin.add(btnAbout);

                //---- lblUsername ----
                lblUsername.setText("text");
                lblUsername.setFont(new Font("Arial", Font.PLAIN, 12));
                lblUsername.setHorizontalAlignment(SwingConstants.LEFT);
                pnlLogin.add(lblUsername);

                //---- txtUsername ----
                txtUsername.setFont(new Font("Arial", Font.PLAIN, 18));
                txtUsername.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        txtUsernameActionPerformed(e);
                    }
                });
                txtUsername.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        txtUsernameFocusGained(e);
                    }
                });
                pnlLogin.add(txtUsername);

                //---- lblPassword ----
                lblPassword.setText("text");
                lblPassword.setFont(new Font("Arial", Font.PLAIN, 12));
                lblPassword.setHorizontalAlignment(SwingConstants.LEFT);
                pnlLogin.add(lblPassword);

                //======== panel6 ========
                {
                    panel6.setLayout(new BoxLayout(panel6, BoxLayout.X_AXIS));

                    //---- txtPassword ----
                    txtPassword.setFont(new Font("Arial", Font.PLAIN, 18));
                    txtPassword.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            txtPasswordActionPerformed(e);
                        }
                    });
                    txtPassword.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            txtPasswordFocusGained(e);
                        }
                    });
                    panel6.add(txtPassword);

                    //---- btnLogin ----
                    btnLogin.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                    btnLogin.setActionCommand("btnLogin");
                    btnLogin.setBorder(new EmptyBorder(0, 5, 0, 10));
                    btnLogin.setContentAreaFilled(false);
                    btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    btnLogin.setSelectedIcon(null);
                    btnLogin.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/pressed.png")));
                    btnLogin.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnLoginActionPerformed(e);
                        }
                    });
                    panel6.add(btnLogin);

                    //---- btnTimeclock ----
                    btnTimeclock.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/2rightarrow.png")));
                    btnTimeclock.setActionCommand("btnLogin");
                    btnTimeclock.setContentAreaFilled(false);
                    btnTimeclock.setBorder(null);
                    btnTimeclock.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/pressed.png")));
                    btnTimeclock.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    btnTimeclock.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnTimeclockActionPerformed(e);
                        }
                    });
                    panel6.add(btnTimeclock);
                }
                pnlLogin.add(panel6);
            }
            pnlMain.add(pnlLogin, BorderLayout.CENTER);
        }
        contentPane.add(pnlMain);
        pack();
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    private void txtPasswordFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPasswordFocusGained
        ((JTextField) evt.getSource()).selectAll();
    }//GEN-LAST:event_txtPasswordFocusGained

    private void txtUsernameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUsernameFocusGained
        ((JTextField) evt.getSource()).selectAll();
    }//GEN-LAST:event_txtUsernameFocusGained


    private void txtPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPasswordActionPerformed
        btnLogin.doClick();
    }//GEN-LAST:event_txtPasswordActionPerformed

    @Override
    public void dispose() {
        actionBlock.execute(OPDE.getLogin());
//        SYSTools.unregisterListeners(this);
        super.dispose();
    }

    private void txtUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsernameActionPerformed
        txtPassword.requestFocus();
    }//GEN-LAST:event_txtUsernameActionPerformed

    private void btnAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAboutActionPerformed
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI("http://www.offene-pflege.de"));
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (URISyntaxException use) {
                use.printStackTrace();

            }
        }
    }//GEN-LAST:event_btnAboutActionPerformed

    private void registerLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        OPDE.setLogin(SYSLoginTools.login(username, password));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel pnlMain;
    private JPanel pnlButtons;
    private JButton btnExit;
    private JPanel pnlLogin;
    private JLabel lblOPDE;
    private JButton btnAbout;
    private JLabel lblUsername;
    private JTextField txtUsername;
    private JLabel lblPassword;
    private JPanel panel6;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnTimeclock;
    // End of variables declaration//GEN-END:variables
}
