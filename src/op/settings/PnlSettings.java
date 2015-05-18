/*
 * Created by JFormDesigner on Mon Apr 27 15:47:59 CEST 2015
 */

package op.settings;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import op.OPDE;
import op.tools.CleanablePanel;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Torsten Löhr
 */
public class PnlSettings extends CleanablePanel {
    public static final String internalClassID = "opde.settings";
    CleanablePanel currentPanel;

    public PnlSettings(JScrollPane jspSearch) {
        initComponents();
        initPanel();
    }

    private void initPanel() {
        i18n();
        OPDE.getDisplayManager().setMainMessage(SYSTools.xx(internalClassID));
        OPDE.getDisplayManager().clearAllIcons();
    }

    private void i18n() {
        lblPersonal.setText(SYSTools.xx("opde.settings.personal"));
        lblLocal.setText(SYSTools.xx("opde.settings.local"));
        lblGlobal.setText(SYSTools.xx("opde.settings.global"));
        lblPassword.setText(SYSTools.xx("opde.settings.userpassword"));
        lblMyEMail.setText(SYSTools.toHTMLForScreen(SYSConst.center("opde.settings.personal.mailsettings")));
        lblLabelPrinter.setText(SYSTools.toHTMLForScreen(SYSConst.center("opde.settings.local.labelPrinters")));
        lblTimeout.setText(SYSTools.toHTMLForScreen(SYSConst.center("opde.settings.local.timeout")));
        lblHomes.setText(SYSTools.toHTMLForScreen(SYSConst.center("opde.settings.global.homes")));
    }

    @Override
    public void cleanup() {
        if (currentPanel != null) currentPanel.cleanup();
        pnlSingle.remove(currentPanel);
    }

    @Override
    public void reload() {

    }

    @Override
    public String getInternalClassID() {
        return internalClassID;
    }

    private void btnLabelPrinterActionPerformed(ActionEvent e) {
        if (currentPanel != null) {
            pnlSingle.remove(currentPanel);
            currentPanel.cleanup();
        }
        currentPanel = new PnlLabelPrinterSetup();
        pnlSingle.add(currentPanel, CC.xyw(1, 3, 3));
        lblSingle.setText(SYSTools.toHTMLForScreen(SYSConst.center("opde.settings.local.labelPrinters")));
        lblSingle.setIcon(btnLabelPrinter.getIcon());
        ((CardLayout) getLayout()).show(this, "single");
    }

    private void btnBackActionPerformed(ActionEvent e) {
        cleanup();
        ((CardLayout) getLayout()).show(this, "all");
    }

    private void btnTimeoutActionPerformed(ActionEvent e) {
        if (currentPanel != null) {
            pnlSingle.remove(currentPanel);
            currentPanel.cleanup();
        }

        currentPanel = new PnlTimeout();
        pnlSingle.add(currentPanel, CC.xyw(1, 3, 3));
        lblSingle.setText(SYSTools.toHTMLForScreen(SYSConst.center("opde.settings.local.timeout")));
        lblSingle.setIcon(btnTimeout.getIcon());
        ((CardLayout) getLayout()).show(this, "single");
    }

    private void btnHomesActionPerformed(ActionEvent e) {
        if (currentPanel != null) {
            pnlSingle.remove(currentPanel);
            currentPanel.cleanup();
        }

        currentPanel = new PnlHomeStationRoomEditor();
        pnlSingle.add(currentPanel, CC.xyw(1, 3, 3));
        lblSingle.setText(SYSTools.toHTMLForScreen(SYSConst.center("opde.settings.global.homes")));
        lblSingle.setIcon(btnHomes.getIcon());
        ((CardLayout) getLayout()).show(this, "single");
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
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
        lblLabelPrinter = new JLabel();
        lblTimeout = new JLabel();
        lblStation = new JLabel();
        separator2 = new JSeparator();
        lblGlobal = new JLabel();
        btnHomes = new JButton();
        btnICD = new JButton();
        btnGlobalEMail = new JButton();
        btnModell = new JButton();
        btnMedication = new JButton();
        btnFTP = new JButton();
        btnTags = new JButton();
        lblHomes = new JLabel();
        lblICD = new JLabel();
        lblGlobalEMail = new JLabel();
        lblModel = new JLabel();
        lblMedication = new JLabel();
        lblFTP = new JLabel();
        lblTags = new JLabel();
        pnlSingle = new JPanel();
        btnBack = new JButton();
        lblSingle = new JLabel();

        //======== this ========
        setLayout(new CardLayout());

        //======== pnlAll ========
        {
            pnlAll.setLayout(new FormLayout(
                "default, $lcgap, left:55dlu, 6*($ugap, 55dlu), $lcgap, default:grow",
                "default, $lcgap, default, $lgap, 50dlu, 2*($lgap, default), $ugap, default, $rgap, fill:50dlu, $rgap, default, $lgap, default, $ugap, default, $lgap, 50dlu, $lgap, default, $lcgap, default"));

            //---- lblPersonal ----
            lblPersonal.setText("Pers\u00f6nliche Einstellungen");
            lblPersonal.setFont(new Font("Arial", Font.PLAIN, 20));
            lblPersonal.setIcon(new ImageIcon(getClass().getResource("/artwork/48x48/userconfig.png")));
            pnlAll.add(lblPersonal, CC.xywh(3, 3, 5, 1));

            //---- btnPassword ----
            btnPassword.setText(null);
            btnPassword.setIcon(new ImageIcon(getClass().getResource("/artwork/48x48/password.png")));
            pnlAll.add(btnPassword, CC.xy(3, 5, CC.FILL, CC.FILL));

            //---- btnMyEMail ----
            btnMyEMail.setText(null);
            btnMyEMail.setIcon(new ImageIcon(getClass().getResource("/artwork/48x48/mail_config_personal.png")));
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
            pnlAll.add(separator1, CC.xywh(3, 9, 15, 1, CC.FILL, CC.DEFAULT));

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
            pnlAll.add(btnStation, CC.xy(7, 13));

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
            pnlAll.add(separator2, CC.xywh(3, 17, 15, 1));

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
            pnlAll.add(btnICD, CC.xy(5, 21, CC.FILL, CC.FILL));

            //---- btnGlobalEMail ----
            btnGlobalEMail.setText(null);
            btnGlobalEMail.setIcon(new ImageIcon(getClass().getResource("/artwork/48x48/mail_config_global.png")));
            pnlAll.add(btnGlobalEMail, CC.xy(7, 21, CC.FILL, CC.FILL));

            //---- btnModell ----
            btnModell.setText(null);
            btnModell.setIcon(new ImageIcon(getClass().getResource("/artwork/48x48/network.png")));
            pnlAll.add(btnModell, CC.xy(9, 21, CC.FILL, CC.FILL));

            //---- btnMedication ----
            btnMedication.setText(null);
            btnMedication.setIcon(new ImageIcon(getClass().getResource("/artwork/48x48/drug_basket.png")));
            pnlAll.add(btnMedication, CC.xy(11, 21, CC.FILL, CC.FILL));

            //---- btnFTP ----
            btnFTP.setText(null);
            btnFTP.setIcon(new ImageIcon(getClass().getResource("/artwork/48x48/transfer_p2p.png")));
            pnlAll.add(btnFTP, CC.xy(13, 21, CC.FILL, CC.FILL));

            //---- btnTags ----
            btnTags.setText(null);
            btnTags.setIcon(new ImageIcon(getClass().getResource("/artwork/48x48/tag.png")));
            pnlAll.add(btnTags, CC.xy(15, 21, CC.FILL, CC.FILL));

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
        }
        add(pnlAll, "all");

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
    private JLabel lblLabelPrinter;
    private JLabel lblTimeout;
    private JLabel lblStation;
    private JSeparator separator2;
    private JLabel lblGlobal;
    private JButton btnHomes;
    private JButton btnICD;
    private JButton btnGlobalEMail;
    private JButton btnModell;
    private JButton btnMedication;
    private JButton btnFTP;
    private JButton btnTags;
    private JLabel lblHomes;
    private JLabel lblICD;
    private JLabel lblGlobalEMail;
    private JLabel lblModel;
    private JLabel lblMedication;
    private JLabel lblFTP;
    private JLabel lblTags;
    private JPanel pnlSingle;
    private JButton btnBack;
    private JLabel lblSingle;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
