/*
 * Created by JFormDesigner on Mon Apr 23 16:41:35 CEST 2012
 */

package de.offene_pflege.op.care.reports;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.offene_pflege.backend.entity.info.ResInfo;
import de.offene_pflege.backend.services.ResInfoService;
import de.offene_pflege.backend.services.ResInfoTypeTools;
import de.offene_pflege.backend.entity.reports.NReport;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.MyJDialog;
import de.offene_pflege.op.tools.PnlCommonTags;
import de.offene_pflege.op.tools.PnlPIT;
import de.offene_pflege.op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

/**
 * @author Torsten Löhr
 */
public class DlgReport extends MyJDialog {
    private NReport nReport;
    private Closure actionBlock;

    private PnlPIT pnlPIT;
    private PnlCommonTags pnlCommonTags;

    public DlgReport(NReport nReport, Closure actionBlock) {
        super(false);
        this.nReport = nReport;
        this.actionBlock = actionBlock;
        initComponents();
        initDialog();
        pack();

    }

    private void initDialog() {

        ResInfo firstStay = ResInfoService.getFirstResinfo(nReport.getResident(), ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_STAY));
        pnlPIT = new PnlPIT(nReport.getPit(), new Date(), firstStay == null ? new Date() : firstStay.getFrom());
        add(pnlPIT, CC.xyw(2, 2, 3));

        pnlCommonTags = new PnlCommonTags(nReport.getCommontags(), true, 5);
        add(new JScrollPane(pnlCommonTags), CC.xyw(2, 6, 3));

        txtBericht.setText(nReport.getText());

        SwingUtilities.invokeLater(() -> txtBericht.requestFocus());
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
            "13dlu, default, $nlgap, fill:143dlu, fill:46dlu, default, 13dlu"));

        //======== scrollPane1 ========
        {

            //---- txtBericht ----
            txtBericht.setFont(new Font("Arial", Font.PLAIN, 14));
            txtBericht.setWrapStyleWord(true);
            txtBericht.setLineWrap(true);
            scrollPane1.setViewportView(txtBericht);
        }
        contentPane.add(scrollPane1, CC.xywh(2, 4, 3, 1, CC.FILL, CC.FILL));

        //======== panel2 ========
        {
            panel2.setLayout(new BoxLayout(panel2, BoxLayout.LINE_AXIS));

            //---- btnCancel ----
            btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
            btnCancel.addActionListener(e -> btnCancelActionPerformed(e));
            panel2.add(btnCancel);

            //---- btnApply ----
            btnApply.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnApply.addActionListener(e -> btnApplyActionPerformed(e));
            panel2.add(btnApply);
        }
        contentPane.add(panel2, CC.xy(4, 6, CC.RIGHT, CC.FILL));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane scrollPane1;
    private JTextArea txtBericht;
    private JPanel panel2;
    private JButton btnCancel;
    private JButton btnApply;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
