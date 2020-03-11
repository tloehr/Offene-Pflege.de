/*
 * Created by JFormDesigner on Mon Apr 27 15:47:59 CEST 2015
 */

package de.offene_pflege.op.settings;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.offene_pflege.gui.interfaces.CleanablePanel;
import de.offene_pflege.gui.interfaces.DefaultPanel;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.settings.subpanels.*;
import de.offene_pflege.op.system.EMailSystem;
import de.offene_pflege.op.system.InternalClassACL;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Torsten Löhr
 */
public class PnlSettings extends CleanablePanel {
    //    public static final String internalClassID = "opde.settings";
    DefaultPanel currentPanel;

    public PnlSettings(JScrollPane jspSearch) {
        super("opde.settings");
        helpkey = OPDE.getAppInfo().getInternalClasses().get(internalClassID).getHelpurl();
        initComponents();
        initPanel();
    }

    private void initPanel() {
        authorize();
        i18n();
        OPDE.getDisplayManager().setMainMessage(SYSTools.xx(internalClassID));
        OPDE.getDisplayManager().clearAllIcons();
    }

    private void authorize() {

        boolean admin = OPDE.isAdmin() || OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, internalClassID);

        btnMyEMail.setEnabled(EMailSystem.isMailsystemActive());
        btnPassword.setEnabled(true);
        btnLabelPrinter.setEnabled(admin);
        btnTimeout.setEnabled(admin);
        btnTags.setEnabled(admin);
        btnHomes.setEnabled(admin);
        btnAnonymous.setEnabled(admin);
        btnGlobalEMail.setEnabled(admin);
        btnModel.setEnabled(admin);
        btnICD.setEnabled(admin);
        btnMedication.setEnabled(admin);
        btnFTP.setEnabled(admin);
        btnStation.setEnabled(admin);
        btnLanguage.setEnabled(admin);
    }

    private void i18n() {
        lblPersonal.setText(SYSTools.xx("opde.settings.personal"));
        lblLocal.setText(SYSTools.xx("opde.settings.local"));
        lblGlobal.setText(SYSTools.xx("opde.settings.global"));
        lblPassword.setText(SYSTools.xx("opde.settings.personal.password"));
        lblMyEMail.setText(SYSTools.toHTMLForScreen(SYSConst.center("opde.settings.personal.mail")));
        lblLabelPrinter.setText(SYSTools.toHTMLForScreen(SYSConst.center("opde.settings.labelPrinters")));
        lblTimeout.setText(SYSTools.toHTMLForScreen(SYSConst.center("opde.settings.local.timeout")));
        lblHomes.setText(SYSTools.toHTMLForScreen(SYSConst.center("opde.settings.homes")));
        lblAnonymous.setText(SYSTools.toHTMLForScreen(SYSConst.center("opde.settings.anonymous")));
        lblStation.setText(SYSTools.toHTMLForScreen(SYSConst.center("opde.settings.default.station")));
        lblICD.setText(SYSTools.toHTMLForScreen(SYSConst.center("opde.settings.icd")));
        lblGlobalEMail.setText(SYSTools.toHTMLForScreen(SYSConst.center("opde.settings.global.mail")));
        lblModel.setText(SYSTools.toHTMLForScreen(SYSConst.center("opde.settings.model")));
        lblMedication.setText(SYSTools.toHTMLForScreen(SYSConst.center("opde.settings.medication.calc")));
        lblFTP.setText(SYSTools.toHTMLForScreen(SYSConst.center("opde.settings.ftp")));
        lblTags.setText(SYSTools.toHTMLForScreen(SYSConst.center("opde.settings.commontags")));
        lblLanguage.setText(SYSTools.toHTMLForScreen(SYSConst.center("opde.settings.language")));
    }

    @Override
    public void cleanup() {
        super.cleanup();
        if (currentPanel != null) {
            currentPanel.cleanup();
            pnlSingle.remove(currentPanel);
        }
        currentPanel = null;
    }

    @Override
    public String getInternalClassID() {
        return internalClassID;
    }

    private void btnLabelPrinterActionPerformed(ActionEvent e) {
        genericButtonAction(new PnlLabelPrinterSetup(), ((JButton) e.getSource()).getIcon());
    }

    private void btnBackActionPerformed(ActionEvent e) {
        cleanup();
        ((CardLayout) getLayout()).show(this, "all");
    }

    private void btnTimeoutActionPerformed(ActionEvent e) {
        genericButtonAction(new PnlTimeout(), ((JButton) e.getSource()).getIcon());
    }

    private void btnHomesActionPerformed(ActionEvent e) {
        genericButtonAction(new PnlHomeStationRoomEditor(), ((JButton) e.getSource()).getIcon());
    }

    private void btnStationActionPerformed(ActionEvent e) {
        genericButtonAction(new PnlStation(), ((JButton) e.getSource()).getIcon());
    }

    private void btnICDActionPerformed(ActionEvent e) {
        genericButtonAction(new PnlICD(), ((JButton) e.getSource()).getIcon());
    }

    private void btnGlobalEMailActionPerformed(ActionEvent e) {
        genericButtonAction(new PnlGlobalMailSettings(), ((JButton) e.getSource()).getIcon());

    }

    private void btnModelActionPerformed(ActionEvent e) {
        genericButtonAction(new PnlModelEditor(), ((JButton) e.getSource()).getIcon());
    }

    private void btnMedicationActionPerformed(ActionEvent e) {
        genericButtonAction(new PnlMedication(), ((JButton) e.getSource()).getIcon());
    }


    private void btnFTPActionPerformed(ActionEvent e) {
        genericButtonAction(new PnlFTP(), ((JButton) e.getSource()).getIcon());
    }


    @Override
    public String getHelpKey() {
        return currentPanel == null ? super.getHelpKey() : currentPanel.getHelpKey();
    }


    private void genericButtonAction(DefaultPanel pnl, Icon icon) {
        if (currentPanel != null) {
            pnlSingle.remove(currentPanel);
            currentPanel.cleanup();
        }
        currentPanel = pnl;
        pnlSingle.add(currentPanel, CC.xyw(1, 3, 3));
        lblSingle.setText(SYSTools.toHTMLForScreen(currentPanel.getInternalClassID()));
        lblSingle.setIcon(icon);
        ((CardLayout) getLayout()).show(this, "single");
    }

    private void btnTagsActionPerformed(ActionEvent e) {
        genericButtonAction(new PnlCommonTags(), ((JButton) e.getSource()).getIcon());
    }

    private void btnPasswordActionPerformed(ActionEvent e) {
        genericButtonAction(new PnlPasswordChange(), ((JButton) e.getSource()).getIcon());
    }

    private void btnMyEMailActionPerformed(ActionEvent e) {
        genericButtonAction(new PnlUserMailSettings(), ((JButton) e.getSource()).getIcon());
    }

    private void btnLanguageActionPerformed(ActionEvent e) {
        genericButtonAction(new PnlLanguage(), ((JButton) e.getSource()).getIcon());
    }

    private void btnAnonymousActionPerformed(ActionEvent e) {
        genericButtonAction(new PnlAnonymous(), ((JButton) e.getSource()).getIcon());
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JScrollPane();
        pnlAll = new JPanel();
        lblPersonal = new JLabel();
        btnPassword = new JButton();
        btnMyEMail = new JButton();
        lblPassword = new JLabel();
        lblMyEMail = new JLabel();
        separator1 = new JSeparator();
        lblLocal = new JLabel();
        btnLabelPrinter = new JButton();
        btnTimeout = new JButton();
        btnStation = new JButton();
        btnAnonymous = new JButton();
        lblLabelPrinter = new JLabel();
        lblTimeout = new JLabel();
        lblStation = new JLabel();
        lblAnonymous = new JLabel();
        separator2 = new JSeparator();
        lblGlobal = new JLabel();
        btnHomes = new JButton();
        btnICD = new JButton();
        btnGlobalEMail = new JButton();
        btnModel = new JButton();
        btnMedication = new JButton();
        btnFTP = new JButton();
        btnTags = new JButton();
        btnLanguage = new JButton();
        lblHomes = new JLabel();
        lblICD = new JLabel();
        lblGlobalEMail = new JLabel();
        lblModel = new JLabel();
        lblMedication = new JLabel();
        lblFTP = new JLabel();
        lblTags = new JLabel();
        lblLanguage = new JLabel();
        pnlSingle = new JPanel();
        btnBack = new JButton();
        lblSingle = new JLabel();

        //======== this ========
        setLayout(new CardLayout());

        //======== panel1 ========
        {

            //======== pnlAll ========
            {
                pnlAll.setLayout(new FormLayout(
                    "default, $lcgap, left:55dlu, 7*($ugap, 55dlu), $lcgap, default:grow",
                    "default, $lcgap, default, $lgap, 50dlu, 2*($lgap, default), $ugap, default, $rgap, fill:50dlu, $rgap, default, $lgap, default, $ugap, default, $lgap, 50dlu, 2*($lgap, default)"));

                //---- lblPersonal ----
                lblPersonal.setText("Pers\u00f6nliche Einstellungen");
                lblPersonal.setFont(new Font("Arial", Font.PLAIN, 20));
                lblPersonal.setIcon(new ImageIcon(getClass().getResource("/artwork/48x48/userconfig.png")));
                pnlAll.add(lblPersonal, CC.xywh(3, 3, 5, 1));

                //---- btnPassword ----
                btnPassword.setText(null);
                btnPassword.setIcon(new ImageIcon(getClass().getResource("/artwork/48x48/password.png")));
                btnPassword.addActionListener(e -> btnPasswordActionPerformed(e));
                pnlAll.add(btnPassword, CC.xy(3, 5, CC.FILL, CC.FILL));

                //---- btnMyEMail ----
                btnMyEMail.setText(null);
                btnMyEMail.setIcon(new ImageIcon(getClass().getResource("/artwork/48x48/mail_config_personal.png")));
                btnMyEMail.addActionListener(e -> btnMyEMailActionPerformed(e));
                pnlAll.add(btnMyEMail, CC.xy(5, 5, CC.FILL, CC.FILL));

                //---- lblPassword ----
                lblPassword.setText("text");
                lblPassword.setFont(new Font("Arial", Font.PLAIN, 14));
                pnlAll.add(lblPassword, CC.xy(3, 7, CC.CENTER, CC.DEFAULT));

                //---- lblMyEMail ----
                lblMyEMail.setText("text");
                lblMyEMail.setFont(new Font("Arial", Font.PLAIN, 14));
                lblMyEMail.setHorizontalAlignment(SwingConstants.CENTER);
                pnlAll.add(lblMyEMail, CC.xy(5, 7, CC.FILL, CC.DEFAULT));
                pnlAll.add(separator1, CC.xywh(3, 9, 17, 1, CC.FILL, CC.DEFAULT));

                //---- lblLocal ----
                lblLocal.setText("Lokale Einstellungen");
                lblLocal.setFont(new Font("Arial", Font.PLAIN, 20));
                lblLocal.setIcon(new ImageIcon(getClass().getResource("/artwork/48x48/localconfig.png")));
                pnlAll.add(lblLocal, CC.xywh(3, 11, 5, 1));

                //---- btnLabelPrinter ----
                btnLabelPrinter.setText(null);
                btnLabelPrinter.setIcon(new ImageIcon(getClass().getResource("/artwork/48x48/labelprinter3.png")));
                btnLabelPrinter.addActionListener(e -> btnLabelPrinterActionPerformed(e));
                pnlAll.add(btnLabelPrinter, CC.xy(3, 13, CC.CENTER, CC.FILL));

                //---- btnTimeout ----
                btnTimeout.setText(null);
                btnTimeout.setIcon(new ImageIcon(getClass().getResource("/artwork/48x48/timer.png")));
                btnTimeout.addActionListener(e -> btnTimeoutActionPerformed(e));
                pnlAll.add(btnTimeout, CC.xy(5, 13));

                //---- btnStation ----
                btnStation.setText(null);
                btnStation.setIcon(new ImageIcon(getClass().getResource("/artwork/48x48/etage.png")));
                btnStation.addActionListener(e -> btnStationActionPerformed(e));
                pnlAll.add(btnStation, CC.xy(7, 13));

                //---- btnAnonymous ----
                btnAnonymous.setText(null);
                btnAnonymous.setIcon(new ImageIcon(getClass().getResource("/artwork/48x48/if_anonymous_45050_48.png")));
                btnAnonymous.addActionListener(e -> btnAnonymousActionPerformed(e));
                pnlAll.add(btnAnonymous, CC.xy(9, 13));

                //---- lblLabelPrinter ----
                lblLabelPrinter.setText("text");
                lblLabelPrinter.setFont(new Font("Arial", Font.PLAIN, 14));
                pnlAll.add(lblLabelPrinter, CC.xy(3, 15, CC.CENTER, CC.DEFAULT));

                //---- lblTimeout ----
                lblTimeout.setText("text");
                lblTimeout.setFont(new Font("Arial", Font.PLAIN, 14));
                pnlAll.add(lblTimeout, CC.xy(5, 15, CC.CENTER, CC.DEFAULT));

                //---- lblStation ----
                lblStation.setText("text");
                lblStation.setHorizontalAlignment(SwingConstants.CENTER);
                lblStation.setFont(new Font("Arial", Font.PLAIN, 14));
                pnlAll.add(lblStation, CC.xy(7, 15));

                //---- lblAnonymous ----
                lblAnonymous.setText("text");
                lblAnonymous.setHorizontalAlignment(SwingConstants.CENTER);
                lblAnonymous.setFont(new Font("Arial", Font.PLAIN, 14));
                pnlAll.add(lblAnonymous, CC.xy(9, 15));
                pnlAll.add(separator2, CC.xywh(3, 17, 17, 1));

                //---- lblGlobal ----
                lblGlobal.setText("Globale Einstellungen");
                lblGlobal.setFont(new Font("Arial", Font.PLAIN, 20));
                lblGlobal.setIcon(new ImageIcon(getClass().getResource("/artwork/48x48/systemconfig.png")));
                pnlAll.add(lblGlobal, CC.xywh(3, 19, 5, 1));

                //---- btnHomes ----
                btnHomes.setText(null);
                btnHomes.setIcon(new ImageIcon(getClass().getResource("/artwork/48x48/hotel_finder.png")));
                btnHomes.addActionListener(e -> btnHomesActionPerformed(e));
                pnlAll.add(btnHomes, CC.xy(3, 21, CC.FILL, CC.FILL));

                //---- btnICD ----
                btnICD.setText(null);
                btnICD.setIcon(new ImageIcon(getClass().getResource("/artwork/48x48/icd10.png")));
                btnICD.addActionListener(e -> btnICDActionPerformed(e));
                pnlAll.add(btnICD, CC.xy(5, 21, CC.FILL, CC.FILL));

                //---- btnGlobalEMail ----
                btnGlobalEMail.setText(null);
                btnGlobalEMail.setIcon(new ImageIcon(getClass().getResource("/artwork/48x48/mail_config_global.png")));
                btnGlobalEMail.addActionListener(e -> btnGlobalEMailActionPerformed(e));
                pnlAll.add(btnGlobalEMail, CC.xy(7, 21, CC.FILL, CC.FILL));

                //---- btnModel ----
                btnModel.setText(null);
                btnModel.setIcon(new ImageIcon(getClass().getResource("/artwork/48x48/network.png")));
                btnModel.addActionListener(e -> btnModelActionPerformed(e));
                pnlAll.add(btnModel, CC.xy(9, 21, CC.FILL, CC.FILL));

                //---- btnMedication ----
                btnMedication.setText(null);
                btnMedication.setIcon(new ImageIcon(getClass().getResource("/artwork/48x48/drug_basket.png")));
                btnMedication.addActionListener(e -> btnMedicationActionPerformed(e));
                pnlAll.add(btnMedication, CC.xy(11, 21, CC.FILL, CC.FILL));

                //---- btnFTP ----
                btnFTP.setText(null);
                btnFTP.setIcon(new ImageIcon(getClass().getResource("/artwork/48x48/transfer_p2p.png")));
                btnFTP.addActionListener(e -> btnFTPActionPerformed(e));
                pnlAll.add(btnFTP, CC.xy(13, 21, CC.FILL, CC.FILL));

                //---- btnTags ----
                btnTags.setText(null);
                btnTags.setIcon(new ImageIcon(getClass().getResource("/artwork/48x48/tag.png")));
                btnTags.addActionListener(e -> btnTagsActionPerformed(e));
                pnlAll.add(btnTags, CC.xy(15, 21, CC.FILL, CC.FILL));

                //---- btnLanguage ----
                btnLanguage.setText(null);
                btnLanguage.setIcon(new ImageIcon(getClass().getResource("/artwork/48x48/language48.png")));
                btnLanguage.setVisible(false);
                btnLanguage.addActionListener(e -> btnLanguageActionPerformed(e));
                pnlAll.add(btnLanguage, CC.xy(17, 21, CC.FILL, CC.FILL));

                //---- lblHomes ----
                lblHomes.setText("text");
                lblHomes.setFont(new Font("Arial", Font.PLAIN, 14));
                lblHomes.setHorizontalAlignment(SwingConstants.CENTER);
                pnlAll.add(lblHomes, CC.xy(3, 23, CC.CENTER, CC.DEFAULT));

                //---- lblICD ----
                lblICD.setText("text");
                lblICD.setFont(new Font("Arial", Font.PLAIN, 14));
                pnlAll.add(lblICD, CC.xy(5, 23, CC.CENTER, CC.DEFAULT));

                //---- lblGlobalEMail ----
                lblGlobalEMail.setText("text");
                lblGlobalEMail.setFont(new Font("Arial", Font.PLAIN, 14));
                pnlAll.add(lblGlobalEMail, CC.xy(7, 23, CC.CENTER, CC.DEFAULT));

                //---- lblModel ----
                lblModel.setText("text");
                lblModel.setFont(new Font("Arial", Font.PLAIN, 14));
                pnlAll.add(lblModel, CC.xy(9, 23, CC.CENTER, CC.DEFAULT));

                //---- lblMedication ----
                lblMedication.setText("text");
                lblMedication.setFont(new Font("Arial", Font.PLAIN, 14));
                pnlAll.add(lblMedication, CC.xy(11, 23, CC.CENTER, CC.DEFAULT));

                //---- lblFTP ----
                lblFTP.setText("text");
                lblFTP.setFont(new Font("Arial", Font.PLAIN, 14));
                pnlAll.add(lblFTP, CC.xy(13, 23, CC.CENTER, CC.DEFAULT));

                //---- lblTags ----
                lblTags.setText("text");
                lblTags.setFont(new Font("Arial", Font.PLAIN, 14));
                pnlAll.add(lblTags, CC.xy(15, 23, CC.CENTER, CC.DEFAULT));

                //---- lblLanguage ----
                lblLanguage.setText("text");
                lblLanguage.setFont(new Font("Arial", Font.PLAIN, 14));
                lblLanguage.setVisible(false);
                pnlAll.add(lblLanguage, CC.xy(17, 23, CC.CENTER, CC.DEFAULT));
            }
            panel1.setViewportView(pnlAll);
        }
        add(panel1, "all");

        //======== pnlSingle ========
        {
            pnlSingle.setLayout(new FormLayout(
                "default, $rgap, default:grow, $lcgap, default",
                "fill:default, $ugap, fill:default:grow"));

            //---- btnBack ----
            btnBack.setText(null);
            btnBack.setIcon(new ImageIcon(getClass().getResource("/artwork/48x48/agt_back.png")));
            btnBack.addActionListener(e -> btnBackActionPerformed(e));
            pnlSingle.add(btnBack, CC.xy(1, 1));

            //---- lblSingle ----
            lblSingle.setText("text");
            lblSingle.setFont(new Font("Arial", Font.PLAIN, 24));
            pnlSingle.add(lblSingle, CC.xy(3, 1, CC.RIGHT, CC.DEFAULT));
        }
        add(pnlSingle, "single");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane panel1;
    private JPanel pnlAll;
    private JLabel lblPersonal;
    private JButton btnPassword;
    private JButton btnMyEMail;
    private JLabel lblPassword;
    private JLabel lblMyEMail;
    private JSeparator separator1;
    private JLabel lblLocal;
    private JButton btnLabelPrinter;
    private JButton btnTimeout;
    private JButton btnStation;
    private JButton btnAnonymous;
    private JLabel lblLabelPrinter;
    private JLabel lblTimeout;
    private JLabel lblStation;
    private JLabel lblAnonymous;
    private JSeparator separator2;
    private JLabel lblGlobal;
    private JButton btnHomes;
    private JButton btnICD;
    private JButton btnGlobalEMail;
    private JButton btnModel;
    private JButton btnMedication;
    private JButton btnFTP;
    private JButton btnTags;
    private JButton btnLanguage;
    private JLabel lblHomes;
    private JLabel lblICD;
    private JLabel lblGlobalEMail;
    private JLabel lblModel;
    private JLabel lblMedication;
    private JLabel lblFTP;
    private JLabel lblTags;
    private JLabel lblLanguage;
    private JPanel pnlSingle;
    private JButton btnBack;
    private JLabel lblSingle;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
