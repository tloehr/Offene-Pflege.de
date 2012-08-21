/*
 * Created by JFormDesigner on Tue Aug 21 17:15:05 CEST 2012
 */

package op.vorgang;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import com.toedter.calendar.*;
import entity.process.QProcess;
import op.tools.MyJDialog;
import org.apache.commons.collections.Closure;

/**
 * @author Torsten Löhr
 */
public class DlgProcess extends MyJDialog {
    private QProcess qProcess;
    private Closure actionBlock;

    public DlgProcess(QProcess qProcess, Closure actionBlock) {
        super();
        this.qProcess = qProcess;
        this.actionBlock = actionBlock;
        initComponents();
        initDialog();
    }

    private void initDialog(){

    }

    private void btnCancelActionPerformed(ActionEvent e) {
        qProcess = null;
        dispose();
    }

    private void btnApplyActionPerformed(ActionEvent e) {
        save();
        dispose();
    }

    private void save(){

    }

    @Override
    public void dispose() {
        actionBlock.execute(qProcess);
        super.dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        pnlDetails = new JPanel();
        lblTitle = new JLabel();
        lblCreatedOn = new JLabel();
        lblEvalDate = new JLabel();
        lblClosed = new JLabel();
        lblCreated = new JLabel();
        lblEditor = new JLabel();
        txtTitel = new JTextField();
        lblStart = new JLabel();
        lblEnde = new JLabel();
        lblCreator = new JLabel();
        lblOwner = new JLabel();
        jdcWV = new JDateChooser();
        lblCat = new JLabel();
        cmbKat = new JComboBox();
        panel2 = new JPanel();
        btnCancel = new JButton();
        btnApply = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        //======== pnlDetails ========
        {
            pnlDetails.setLayout(new FormLayout(
                "default, $lcgap, 0dlu, $lcgap, 70dlu, $lcgap, default:grow, $lcgap, 0dlu, $lcgap, default",
                "default, $lgap, 0dlu, 6*($lgap, fill:default), $lgap, pref, 2*($lgap, default)"));

            //---- lblTitle ----
            lblTitle.setText("Titel");
            lblTitle.setFont(new Font("Arial", Font.PLAIN, 14));
            pnlDetails.add(lblTitle, CC.xywh(5, 5, 2, 1));

            //---- lblCreatedOn ----
            lblCreatedOn.setText("Erstellt am");
            lblCreatedOn.setFont(new Font("Arial", Font.PLAIN, 14));
            pnlDetails.add(lblCreatedOn, CC.xywh(5, 7, 2, 1));

            //---- lblEvalDate ----
            lblEvalDate.setText("Wiedervorlage");
            lblEvalDate.setFont(new Font("Arial", Font.PLAIN, 14));
            pnlDetails.add(lblEvalDate, CC.xywh(5, 9, 2, 1));

            //---- lblClosed ----
            lblClosed.setText("Abgeschlossen am");
            lblClosed.setFont(new Font("Arial", Font.PLAIN, 14));
            pnlDetails.add(lblClosed, CC.xywh(5, 11, 2, 1));

            //---- lblCreated ----
            lblCreated.setText("Erstellt von");
            lblCreated.setFont(new Font("Arial", Font.PLAIN, 14));
            pnlDetails.add(lblCreated, CC.xywh(5, 13, 2, 1));

            //---- lblEditor ----
            lblEditor.setText("Wird bearbeitet von");
            lblEditor.setFont(new Font("Arial", Font.PLAIN, 14));
            pnlDetails.add(lblEditor, CC.xywh(5, 15, 2, 1));

            //---- txtTitel ----
            txtTitel.setFont(new Font("Arial", Font.PLAIN, 14));
            pnlDetails.add(txtTitel, CC.xywh(7, 5, 2, 1));

            //---- lblStart ----
            lblStart.setText("15.05.2011");
            lblStart.setFont(new Font("Arial", Font.PLAIN, 14));
            pnlDetails.add(lblStart, CC.xywh(7, 7, 2, 1));

            //---- lblEnde ----
            lblEnde.setText("noch nicht abgeschlossen");
            lblEnde.setFont(new Font("Arial", Font.PLAIN, 14));
            pnlDetails.add(lblEnde, CC.xywh(7, 11, 2, 1));

            //---- lblCreator ----
            lblCreator.setText("text");
            lblCreator.setFont(new Font("Arial", Font.PLAIN, 14));
            pnlDetails.add(lblCreator, CC.xywh(7, 13, 2, 1));

            //---- lblOwner ----
            lblOwner.setText("text");
            lblOwner.setFont(new Font("Arial", Font.PLAIN, 14));
            pnlDetails.add(lblOwner, CC.xy(7, 15));

            //---- jdcWV ----
            jdcWV.setFont(new Font("Arial", Font.PLAIN, 14));
            pnlDetails.add(jdcWV, CC.xywh(7, 9, 2, 1));

            //---- lblCat ----
            lblCat.setText("Kategorie");
            lblCat.setFont(new Font("Arial", Font.PLAIN, 14));
            pnlDetails.add(lblCat, CC.xy(5, 17));

            //---- cmbKat ----
            cmbKat.setFont(new Font("Arial", Font.PLAIN, 14));
            cmbKat.setToolTipText("Kategorie des Vorgangs");
            pnlDetails.add(cmbKat, CC.xywh(7, 17, 2, 1));

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
            pnlDetails.add(panel2, CC.xy(7, 21, CC.RIGHT, CC.FILL));
        }
        contentPane.add(pnlDetails);
        setSize(640, 300);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel pnlDetails;
    private JLabel lblTitle;
    private JLabel lblCreatedOn;
    private JLabel lblEvalDate;
    private JLabel lblClosed;
    private JLabel lblCreated;
    private JLabel lblEditor;
    private JTextField txtTitel;
    private JLabel lblStart;
    private JLabel lblEnde;
    private JLabel lblCreator;
    private JLabel lblOwner;
    private JDateChooser jdcWV;
    private JLabel lblCat;
    private JComboBox cmbKat;
    private JPanel panel2;
    private JButton btnCancel;
    private JButton btnApply;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
