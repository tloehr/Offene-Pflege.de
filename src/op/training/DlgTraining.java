/*
 * Created by JFormDesigner on Tue May 20 14:55:03 CEST 2014
 */

package op.training;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDateChooser;
import entity.staff.Training;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.MyJDialog;
import op.tools.SYSCalendar;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DateFormat;
import java.util.Date;

/**
 * @author Torsten Löhr
 */
public class DlgTraining extends MyJDialog {
    private Training training;
    private final Closure actionBlock;

    public DlgTraining(Training training, Closure actionBlock) {
        super();
        this.training = training;
        this.actionBlock = actionBlock;
        initComponents();
        initDialog();
//        pack();
        setVisible(true);
    }

    private void initDialog() {
        lblDate.setText(SYSTools.xx("misc.msg.Date"));
        lblTime.setText(SYSTools.xx("misc.msg.Time"));
        lblTitle.setText(SYSTools.xx("misc.msg.title"));
        lblDocent.setText(SYSTools.xx("opde.training.docent"));
        lblText.setText(SYSTools.xx("misc.msg.details"));
        cbInternal.setText(SYSTools.xx("opde.training.internal"));

        if (training.getId() != 0) {
            jdcDate.setDate(training.getDate());
            txtTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(training.getDate()));
            txtTitle.setText(training.getTitle());
            txtDocent.setText(training.getDocent());
            txtTitle.setText(training.getTitle());
            txtText.setText(training.getText());
            cbInternal.setSelected(training.getInternal());
        }

    }

    private void btnCancelActionPerformed(ActionEvent e) {
        training = null;
        dispose();
    }

    @Override
    public void dispose() {
        super.dispose();
        actionBlock.execute(training);
    }

    private void btnApplyActionPerformed(ActionEvent e) {
        if (!saveOK()) return;

        training.setTitle(txtTitle.getText());
        training.setDate(SYSCalendar.addTime2Date(jdcDate.getDate(), new Date(SYSCalendar.parseTime(txtTime.getText()).getTimeInMillis())));
        training.setDocent(txtDocent.getText());
        training.setText(txtText.getText());
        training.setInternal(cbInternal.isSelected());

        dispose();

    }

    private boolean saveOK() {
//        try {
//            SYSCalendar.parseTime(txtTime.getText());
//        } catch (NumberFormatException nfe) {
//            return false;
//        }

        if (jdcDate.getDate() == null){
            return false;
        }

        if (txtTitle.getText().trim().isEmpty()){
            return false;
        }

        return true;
    }

    private void txtTimeFocusLost(FocusEvent e) {
        try {
            txtTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date(SYSCalendar.parseTime(txtTime.getText()).getTimeInMillis())));
        } catch (NumberFormatException nfe) {
            txtTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date()));
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.wrongtime"));
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        lblDate = new JLabel();
        jdcDate = new JDateChooser();
        lblTime = new JLabel();
        txtTime = new JTextField();
        lblTitle = new JLabel();
        txtTitle = new JTextField();
        lblDocent = new JLabel();
        txtDocent = new JTextField();
        lblText = new JLabel();
        scrollPane1 = new JScrollPane();
        txtText = new JTextArea();
        cbInternal = new JCheckBox();
        buttonBar = new JPanel();
        btnCancel = new JButton();
        btnApply = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new FormLayout(
                    "pref, $ugap, default:grow",
                    "default, $rgap, default, $lgap, 2*(default, $rgap), default:grow, $lgap, default"));

                //---- lblDate ----
                lblDate.setText("text");
                lblDate.setFont(new Font("Arial", Font.PLAIN, 14));
                contentPanel.add(lblDate, CC.xy(1, 1));

                //---- jdcDate ----
                jdcDate.setFont(new Font("Arial", Font.PLAIN, 14));
                contentPanel.add(jdcDate, CC.xy(3, 1));

                //---- lblTime ----
                lblTime.setText("text");
                lblTime.setFont(new Font("Arial", Font.PLAIN, 14));
                contentPanel.add(lblTime, CC.xy(1, 3));

                //---- txtTime ----
                txtTime.setFont(new Font("Arial", Font.PLAIN, 14));
                txtTime.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        txtTimeFocusLost(e);
                    }
                });
                contentPanel.add(txtTime, CC.xy(3, 3));

                //---- lblTitle ----
                lblTitle.setText("text");
                lblTitle.setFont(new Font("Arial", Font.PLAIN, 14));
                contentPanel.add(lblTitle, CC.xy(1, 5));

                //---- txtTitle ----
                txtTitle.setFont(new Font("Arial", Font.PLAIN, 14));
                contentPanel.add(txtTitle, CC.xy(3, 5));

                //---- lblDocent ----
                lblDocent.setText("text");
                lblDocent.setFont(new Font("Arial", Font.PLAIN, 14));
                contentPanel.add(lblDocent, CC.xy(1, 7));

                //---- txtDocent ----
                txtDocent.setFont(new Font("Arial", Font.PLAIN, 14));
                contentPanel.add(txtDocent, CC.xy(3, 7));

                //---- lblText ----
                lblText.setText("text");
                lblText.setFont(new Font("Arial", Font.PLAIN, 14));
                contentPanel.add(lblText, CC.xy(1, 9, CC.DEFAULT, CC.TOP));

                //======== scrollPane1 ========
                {

                    //---- txtText ----
                    txtText.setLineWrap(true);
                    txtText.setWrapStyleWord(true);
                    scrollPane1.setViewportView(txtText);
                }
                contentPanel.add(scrollPane1, CC.xy(3, 9, CC.DEFAULT, CC.FILL));

                //---- cbInternal ----
                cbInternal.setText("text");
                contentPanel.add(cbInternal, CC.xywh(1, 11, 3, 1));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

                //---- btnCancel ----
                btnCancel.setText(null);
                btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
                btnCancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnCancelActionPerformed(e);
                    }
                });
                buttonBar.add(btnCancel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- btnApply ----
                btnApply.setText(null);
                btnApply.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                btnApply.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnApplyActionPerformed(e);
                    }
                });
                buttonBar.add(btnApply, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(450, 390);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel lblDate;
    private JDateChooser jdcDate;
    private JLabel lblTime;
    private JTextField txtTime;
    private JLabel lblTitle;
    private JTextField txtTitle;
    private JLabel lblDocent;
    private JTextField txtDocent;
    private JLabel lblText;
    private JScrollPane scrollPane1;
    private JTextArea txtText;
    private JCheckBox cbInternal;
    private JPanel buttonBar;
    private JButton btnCancel;
    private JButton btnApply;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
