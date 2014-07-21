/*
 * Created by JFormDesigner on Thu Jul 17 15:44:17 CEST 2014
 */

package op.training;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDateChooser;
import entity.staff.Training;
import entity.staff.TrainingTools;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.PnlCommonTags;
import op.tools.SYSCalendar;
import op.tools.SYSTools;
import org.joda.time.LocalDate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormat;
import java.util.Date;

/**
 * @author Torsten Löhr
 */
public class PnlTrainingEditor extends JPanel {
    private Training training;

    public PnlTrainingEditor(Training training) {
        this.training = training;
        initComponents();
        initPanel();
    }

    private void initPanel() {
        lblDate.setText(SYSTools.xx("misc.msg.Date"));
        lblTitle.setText(SYSTools.xx("misc.msg.title"));
        lblDocent.setText(SYSTools.xx("opde.training.docent"));
        lblText.setText(SYSTools.xx("misc.msg.details"));
        lblTags.setText(SYSTools.xx("misc.msg.tags"));

        jdcStarting.setDate(training.getStarting());

        if (new LocalDate(training.getStarting()).toDateTimeAtStartOfDay().toDate().equals(training.getStarting())) {
            txtTimeStarting.setText(null);
            cbTime.setSelected(false);
        } else {
            txtTimeStarting.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(training.getStarting()));
            cbTime.setSelected(true);
        }

        cmbState.setModel(new DefaultComboBoxModel(SYSTools.translate(TrainingTools.STATES)));

        txtTitle.setText(training.getTitle());
        txtDocent.setText(training.getDocent());
        txtText.setText(training.getText());

        add(new PnlCommonTags(training.getCommontags(), true), CC.xywh(3, 13, 5, 1));
        add(new PnlUserlistEditor(training, true), CC.xywh(3, 15, 5, 1));
    }

    private void txtTimeFocusLost(FocusEvent e) {
        try {
            txtTimeStarting.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date(SYSCalendar.parseTime(txtTimeStarting.getText()).getTimeInMillis())));
        } catch (NumberFormatException nfe) {
            txtTimeStarting.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date()));
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.wrongtime"));
        }
    }

    private void cbTimeItemStateChanged(ItemEvent e) {
        txtTimeStarting.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblTitle = new JLabel();
        txtTitle = new JTextField();
        lblDate = new JLabel();
        jdcStarting = new JDateChooser();
        cbTime = new JCheckBox();
        txtTimeStarting = new JTextField();
        lblDocent = new JLabel();
        txtDocent = new JTextField();
        lblText = new JLabel();
        scrollPane1 = new JScrollPane();
        txtText = new JTextArea();
        cmbState = new JComboBox();
        lblTags = new JLabel();
        lblAttendees = new JLabel();

        //======== this ========
        setLayout(new FormLayout(
            "default, $lcgap, default:grow, $lcgap, default, $lcgap, default:grow",
            "6*(default, $lgap), fill:30dlu, $lgap, fill:default:grow"));

        //---- lblTitle ----
        lblTitle.setText("text");
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblTitle, CC.xy(1, 1));

        //---- txtTitle ----
        txtTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtTitle, CC.xywh(3, 1, 5, 1));

        //---- lblDate ----
        lblDate.setText("text");
        lblDate.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblDate, CC.xy(1, 3));

        //---- jdcStarting ----
        jdcStarting.setFont(new Font("Arial", Font.PLAIN, 14));
        add(jdcStarting, CC.xy(3, 3));

        //---- cbTime ----
        cbTime.setText(null);
        cbTime.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cbTimeItemStateChanged(e);
            }
        });
        add(cbTime, CC.xy(5, 3));

        //---- txtTimeStarting ----
        txtTimeStarting.setFont(new Font("Arial", Font.PLAIN, 14));
        txtTimeStarting.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                txtTimeFocusLost(e);
            }
        });
        add(txtTimeStarting, CC.xy(7, 3));

        //---- lblDocent ----
        lblDocent.setText("text");
        lblDocent.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblDocent, CC.xy(1, 7));

        //---- txtDocent ----
        txtDocent.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtDocent, CC.xywh(3, 7, 5, 1));

        //---- lblText ----
        lblText.setText("text");
        lblText.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblText, CC.xy(1, 9, CC.DEFAULT, CC.TOP));

        //======== scrollPane1 ========
        {

            //---- txtText ----
            txtText.setLineWrap(true);
            txtText.setWrapStyleWord(true);
            scrollPane1.setViewportView(txtText);
        }
        add(scrollPane1, CC.xywh(3, 9, 5, 1, CC.DEFAULT, CC.FILL));
        add(cmbState, CC.xy(3, 11));

        //---- lblTags ----
        lblTags.setText("text");
        lblTags.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblTags, CC.xy(1, 13, CC.DEFAULT, CC.TOP));

        //---- lblAttendees ----
        lblAttendees.setText("text");
        lblAttendees.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblAttendees, CC.xy(1, 15, CC.DEFAULT, CC.TOP));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblTitle;
    private JTextField txtTitle;
    private JLabel lblDate;
    private JDateChooser jdcStarting;
    private JCheckBox cbTime;
    private JTextField txtTimeStarting;
    private JLabel lblDocent;
    private JTextField txtDocent;
    private JLabel lblText;
    private JScrollPane scrollPane1;
    private JTextArea txtText;
    private JComboBox cmbState;
    private JLabel lblTags;
    private JLabel lblAttendees;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
