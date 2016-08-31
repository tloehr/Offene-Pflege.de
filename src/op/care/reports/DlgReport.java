/*
 * Created by JFormDesigner on Mon Apr 23 16:41:35 CEST 2012
 */

package op.care.reports;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.swing.DefaultOverlayable;
import com.jidesoft.swing.OverlayableIconsFactory;
import com.jidesoft.swing.OverlayableUtils;
import entity.info.ResInfo;
import entity.info.ResInfoTools;
import entity.info.ResInfoTypeTools;
import entity.reports.NReport;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.MyJDialog;
import op.tools.PnlCommonTags;
import op.tools.PnlPIT;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.Date;

/**
 * @author Torsten Löhr
 */
public class DlgReport extends MyJDialog {
    private NReport nReport;
    private Closure actionBlock;

    private DefaultOverlayable ovrDauer;
    private JLabel attentionIcon;
    private PnlPIT pnlPIT;
    private int defaultMinutes;
    private PnlCommonTags pnlCommonTags;

    public DlgReport(NReport nReport, Closure actionBlock) {
        super(false);
        this.nReport = nReport;
        this.actionBlock = actionBlock;
        initComponents();
        initDialog();
        pack();
        setVisible(true);
    }

    private void initDialog() {

        ResInfo firstStay = ResInfoTools.getFirstResinfo(nReport.getResident(), ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_STAY));
        pnlPIT = new PnlPIT(nReport.getPit(), new Date(), firstStay == null ? new Date() : firstStay.getFrom());
        add(pnlPIT, CC.xyw(2, 2, 3));

        pnlCommonTags = new PnlCommonTags(nReport.getCommontags(), true, 5);
        add(new JScrollPane(pnlCommonTags), CC.xyw(2, 6, 3));

        txtBericht.setText(nReport.getText());
        defaultMinutes = nReport.getMinutes();
        txtMinutes.setText(Integer.toString(defaultMinutes));

        txtMinutes.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                txtDauerFocusGained(focusEvent);
            }

            @Override
            public void focusLost(FocusEvent focusEvent) {
                txtDauerFocusLost(focusEvent);
            }
        });

        lblTime.setText(SYSTools.xx("misc.msg.Minutes"));

        attentionIcon = new JLabel(OverlayableUtils.getPredefinedOverlayIcon(OverlayableIconsFactory.ATTENTION));
        ovrDauer = new DefaultOverlayable(lblTime, attentionIcon, DefaultOverlayable.SOUTH_EAST);
        ovrDauer.setOverlayVisible(true);
        add(ovrDauer, CC.xy(2, 4));

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                txtBericht.requestFocus();
            }
        });
    }

    private void txtDauerFocusGained(FocusEvent e) {
        SYSTools.markAllTxt(txtMinutes);
    }

    private void txtDauerFocusLost(FocusEvent e) {
        if (nReport == null) return;

        NumberFormat nf = NumberFormat.getIntegerInstance();
        String test = txtMinutes.getText();
        int dauer;
        try {
            Number num = nf.parse(test);
            dauer = num.intValue();
            if (dauer < 0) {
                dauer = defaultMinutes;
                txtMinutes.setText(Integer.toString(defaultMinutes));
            }
        } catch (Exception exc) {
            dauer = defaultMinutes;
            txtMinutes.setText(Integer.toString(defaultMinutes));
        }
        ovrDauer.setOverlayVisible(dauer == defaultMinutes);
        nReport.setMinutes(dauer);
    }

    @Override
    public void dispose() {
        super.dispose();
        actionBlock.execute(nReport);
    }

    private void btnCancelActionPerformed(ActionEvent e) {
        nReport = null;
        dispose();
    }

    private void btnApplyActionPerformed(ActionEvent e) {
        if (SYSTools.catchNull(txtBericht.getText()).trim().isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("misc.msg.emptyentry")));
            return;
        }
        nReport.setText(txtBericht.getText());
        nReport.getCommontags().clear();
        nReport.getCommontags().addAll(pnlCommonTags.getListSelectedTags());

        nReport.setPit(pnlPIT.getPIT());
        nReport.setNewBy(OPDE.getMe());
        dispose();
    }

    private void thisWindowClosing(WindowEvent e) {
        btnCancelActionPerformed(null);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblTime = new JLabel();
        txtMinutes = new JTextField();
        scrollPane1 = new JScrollPane();
        txtBericht = new JTextArea();
        panel2 = new JPanel();
        btnCancel = new JButton();
        btnApply = new JButton();

        //======== this ========
        setResizable(false);
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                thisWindowClosing(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "13dlu, pref, $rgap, 336dlu, 13dlu",
            "13dlu, default, $nlgap, default, fill:143dlu, fill:46dlu, default, 13dlu"));

        //---- lblTime ----
        lblTime.setText("Dauer");
        lblTime.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPane.add(lblTime, CC.xy(2, 4));

        //---- txtMinutes ----
        txtMinutes.setColumns(5);
        contentPane.add(txtMinutes, CC.xy(4, 4, CC.LEFT, CC.DEFAULT));

        //======== scrollPane1 ========
        {

            //---- txtBericht ----
            txtBericht.setFont(new Font("Arial", Font.PLAIN, 14));
            txtBericht.setWrapStyleWord(true);
            txtBericht.setLineWrap(true);
            scrollPane1.setViewportView(txtBericht);
        }
        contentPane.add(scrollPane1, CC.xywh(2, 5, 3, 1, CC.FILL, CC.FILL));

        //======== panel2 ========
        {
            panel2.setLayout(new BoxLayout(panel2, BoxLayout.LINE_AXIS));

            //---- btnCancel ----
            btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
            btnCancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnCancelActionPerformed(e);
                }
            });
            panel2.add(btnCancel);

            //---- btnApply ----
            btnApply.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnApply.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnApplyActionPerformed(e);
                }
            });
            panel2.add(btnApply);
        }
        contentPane.add(panel2, CC.xy(4, 7, CC.RIGHT, CC.FILL));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblTime;
    private JTextField txtMinutes;
    private JScrollPane scrollPane1;
    private JTextArea txtBericht;
    private JPanel panel2;
    private JButton btnCancel;
    private JButton btnApply;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
