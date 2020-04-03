/*
 * Created by JFormDesigner on Tue Aug 21 17:15:05 CEST 2012
 */

package de.offene_pflege.op.process;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDateChooser;
import de.offene_pflege.backend.entity.done.Resident;
import de.offene_pflege.backend.entity.process.PCat;
import de.offene_pflege.backend.entity.process.PCatTools;
import de.offene_pflege.backend.entity.process.QProcess;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.MyJDialog;
import de.offene_pflege.op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * @author Torsten Löhr
 */
public class DlgProcess extends MyJDialog {
    private QProcess qProcess, resultingQProcess;
    private Closure actionBlock;

    public DlgProcess(QProcess qProcess, Closure actionBlock) {
        super(false);
        this.qProcess = qProcess;
        this.actionBlock = actionBlock;
        initComponents();
        initDialog();
        pack();
//        setVisible(true);
    }

    private void initDialog() {
        cmbPCat.setModel(SYSTools.list2cmb(PCatTools.getPCats()));
        cmbPCat.setSelectedIndex(0);


        cmbResident.setRenderer((jList, o, i, isSelected, cellHasFocus) -> {
            String text;
            if (o == null) {
                text = SYSTools.toHTMLForScreen("<i>" + SYSTools.xx("nursingrecords.qprocesses.commonprocess") + "</i>");
            } else {
                text = o.toString();
            }
            return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
        });
        if (qProcess.isCommon()) {
            EntityManager em = OPDE.createEM();
            Query query = em.createQuery("SELECT b FROM Resident b WHERE b.station IS NOT NULL ORDER BY b.name, b.firstname");
            ArrayList<Resident> listResident = new ArrayList<Resident>(query.getResultList());
            listResident.add(0, null);
            cmbResident.setModel(SYSTools.list2cmb(listResident));
            em.close();
        } else {
            cmbResident.setModel(new DefaultComboBoxModel(new Resident[]{qProcess.getResident()}));
        }

        jdcWV.setDate(qProcess.getRevision());
        jdcWV.setMinSelectableDate(new DateTime().plusDays(1).toDate());

        txtTitel.setText(qProcess.getTitle().trim());

    }

    private void btnCancelActionPerformed(ActionEvent e) {
        resultingQProcess = null;
        dispose();
    }

    private void btnApplyActionPerformed(ActionEvent e) {
        save();
        resultingQProcess = qProcess;
        dispose();
    }

    private void save() {
        qProcess.setResident((Resident) cmbResident.getSelectedItem());
        qProcess.setPcat((PCat) cmbPCat.getSelectedItem());
        qProcess.setRevision(jdcWV.getDate());
        qProcess.setTitle(txtTitel.getText().trim());
    }

    @Override
    public void dispose() {
        actionBlock.execute(resultingQProcess);
        super.dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        pnlDetails = new JPanel();
        lblTitle = new JLabel();
        lblEvalDate = new JLabel();
        lblBW = new JLabel();
        txtTitel = new JTextField();
        jdcWV = new JDateChooser();
        cmbResident = new JComboBox();
        lblCat = new JLabel();
        cmbPCat = new JComboBox();
        panel2 = new JPanel();
        btnCancel = new JButton();
        btnApply = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        //======== pnlDetails ========
        {
            pnlDetails.setLayout(new FormLayout(
                "14dlu, 0dlu, $lcgap, 70dlu, $lcgap, default:grow, $lcgap, 0dlu, 14dlu",
                "14dlu, 0dlu, 3*($lgap, fill:default), $lgap, pref, $lgap, 12dlu, $lgap, top:20dlu, $lgap, 14dlu"));

            //---- lblTitle ----
            lblTitle.setText("Titel");
            lblTitle.setFont(new Font("Arial", Font.PLAIN, 14));
            pnlDetails.add(lblTitle, CC.xywh(4, 4, 2, 1));

            //---- lblEvalDate ----
            lblEvalDate.setText("Wiedervorlage");
            lblEvalDate.setFont(new Font("Arial", Font.PLAIN, 14));
            pnlDetails.add(lblEvalDate, CC.xywh(4, 6, 2, 1));

            //---- lblBW ----
            lblBW.setText("Zugeordnet zu");
            lblBW.setFont(new Font("Arial", Font.PLAIN, 14));
            pnlDetails.add(lblBW, CC.xywh(4, 8, 2, 1));

            //---- txtTitel ----
            txtTitel.setFont(new Font("Arial", Font.PLAIN, 18));
            pnlDetails.add(txtTitel, CC.xywh(6, 4, 2, 1));

            //---- jdcWV ----
            jdcWV.setFont(new Font("Arial", Font.PLAIN, 14));
            pnlDetails.add(jdcWV, CC.xywh(6, 6, 2, 1));

            //---- cmbResident ----
            cmbResident.setFont(new Font("Arial", Font.PLAIN, 14));
            pnlDetails.add(cmbResident, CC.xy(6, 8));

            //---- lblCat ----
            lblCat.setText("Kategorie");
            lblCat.setFont(new Font("Arial", Font.PLAIN, 14));
            pnlDetails.add(lblCat, CC.xy(4, 10));

            //---- cmbPCat ----
            cmbPCat.setFont(new Font("Arial", Font.PLAIN, 14));
            cmbPCat.setToolTipText("Kategorie des Vorgangs");
            pnlDetails.add(cmbPCat, CC.xy(6, 10));

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
            pnlDetails.add(panel2, CC.xy(6, 14, CC.RIGHT, CC.DEFAULT));
        }
        contentPane.add(pnlDetails);
        setSize(590, 255);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel pnlDetails;
    private JLabel lblTitle;
    private JLabel lblEvalDate;
    private JLabel lblBW;
    private JTextField txtTitel;
    private JDateChooser jdcWV;
    private JComboBox cmbResident;
    private JLabel lblCat;
    private JComboBox cmbPCat;
    private JPanel panel2;
    private JButton btnCancel;
    private JButton btnApply;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
