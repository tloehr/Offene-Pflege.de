/*
 * Created by JFormDesigner on Wed Jun 13 11:31:13 CEST 2012
 */

package op.care.vital;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDateChooser;
import entity.BWerte;
import op.tools.MyJDialog;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.HorizontalLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * @author Torsten Löhr
 */
public class DlgWert extends MyJDialog {

    public DlgWert(BWerte wert, Closure actionBlock) {
        super();
        initComponents();
        setVisible(true);
    }

    private void txtUhrzeitFocusLost(FocusEvent e) {
        // TODO add your code here
    }

    private void txtUhrzeitActionPerformed(ActionEvent e) {
        // TODO add your code here
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        contentPanel = new JPanel();
        label1 = new JLabel();
        jdcDatum = new JDateChooser();
        label2 = new JLabel();
        txtUhrzeit = new JTextField();
        tabbedPane1 = new JTabbedPane();
        pnlRR = new JPanel();
        lblRRSys = new JLabel();
        txtRRSys = new JTextField();
        lblRRSysEinheit = new JLabel();
        lblRRDia = new JLabel();
        txtRRDia = new JTextField();
        lblRRDiaEinheit = new JLabel();
        lblPuls = new JLabel();
        txtRRPuls = new JTextField();
        lblPulsEinheit = new JLabel();
        pnlPuls = new JPanel();
        panel2 = new JPanel();
        scrollPane1 = new JScrollPane();
        textArea1 = new JTextArea();
        panel1 = new JPanel();
        btnCancel = new JButton();
        btnSave = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== contentPanel ========
        {
            contentPanel.setLayout(new FormLayout(
                "14dlu, $lcgap, default, $lcgap, default:grow, $lcgap, 14dlu",
                "2*(14dlu, $lgap), 14dlu, 5dlu, fill:default:grow, $lgap, fill:43dlu:grow, $lgap, 23dlu, $lgap, 14dlu"));

            //---- label1 ----
            label1.setText("Datum");
            label1.setFont(new Font("Arial", Font.PLAIN, 14));
            contentPanel.add(label1, CC.xy(3, 3));

            //---- jdcDatum ----
            jdcDatum.setFont(new Font("Arial", Font.PLAIN, 14));
            contentPanel.add(jdcDatum, CC.xy(5, 3));

            //---- label2 ----
            label2.setText("Uhrzeit");
            label2.setFont(new Font("Arial", Font.PLAIN, 14));
            contentPanel.add(label2, CC.xy(3, 5));

            //---- txtUhrzeit ----
            txtUhrzeit.setFont(new Font("Arial", Font.PLAIN, 14));
            txtUhrzeit.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    txtUhrzeitFocusLost(e);
                }
            });
            txtUhrzeit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    txtUhrzeitActionPerformed(e);
                }
            });
            contentPanel.add(txtUhrzeit, CC.xy(5, 5));

            //======== tabbedPane1 ========
            {
                tabbedPane1.setFont(new Font("Arial", Font.PLAIN, 12));
                tabbedPane1.setTabPlacement(SwingConstants.RIGHT);

                //======== pnlRR ========
                {
                    pnlRR.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlRR.setLayout(new FormLayout(
                        "$rgap, $lcgap, pref, $lcgap, default:grow, 2*($lcgap), pref, $rgap",
                        "$rgap, 3*($lgap, default), $lgap, $rgap"));

                    //---- lblRRSys ----
                    lblRRSys.setText("text");
                    lblRRSys.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlRR.add(lblRRSys, CC.xy(3, 3));

                    //---- txtRRSys ----
                    txtRRSys.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlRR.add(txtRRSys, CC.xy(5, 3));

                    //---- lblRRSysEinheit ----
                    lblRRSysEinheit.setText("text");
                    lblRRSysEinheit.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlRR.add(lblRRSysEinheit, CC.xy(8, 3));

                    //---- lblRRDia ----
                    lblRRDia.setText("text");
                    lblRRDia.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlRR.add(lblRRDia, CC.xy(3, 5));

                    //---- txtRRDia ----
                    txtRRDia.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlRR.add(txtRRDia, CC.xy(5, 5));

                    //---- lblRRDiaEinheit ----
                    lblRRDiaEinheit.setText("text");
                    lblRRDiaEinheit.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlRR.add(lblRRDiaEinheit, CC.xy(8, 5));

                    //---- lblPuls ----
                    lblPuls.setText("text");
                    lblPuls.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlRR.add(lblPuls, CC.xy(3, 7));

                    //---- txtRRPuls ----
                    txtRRPuls.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlRR.add(txtRRPuls, CC.xy(5, 7));

                    //---- lblPulsEinheit ----
                    lblPulsEinheit.setText("text");
                    lblPulsEinheit.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlRR.add(lblPulsEinheit, CC.xy(8, 7));
                }
                tabbedPane1.addTab("RR", pnlRR);


                //======== pnlPuls ========
                {
                    pnlPuls.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlPuls.setLayout(new FormLayout(
                        "default, $lcgap, default",
                        "2*(default, $lgap), default"));
                }
                tabbedPane1.addTab("Puls", pnlPuls);


                //======== panel2 ========
                {
                    panel2.setFont(new Font("Arial", Font.PLAIN, 14));
                    panel2.setLayout(new FormLayout(
                        "default, $lcgap, default",
                        "2*(default, $lgap), default"));
                }
                tabbedPane1.addTab("Temperatur", panel2);

            }
            contentPanel.add(tabbedPane1, CC.xywh(3, 7, 3, 1));

            //======== scrollPane1 ========
            {
                scrollPane1.setViewportView(textArea1);
            }
            contentPanel.add(scrollPane1, CC.xywh(3, 9, 3, 1, CC.DEFAULT, CC.FILL));

            //======== panel1 ========
            {
                panel1.setLayout(new HorizontalLayout(5));

                //---- btnCancel ----
                btnCancel.setText(null);
                btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
                panel1.add(btnCancel);

                //---- btnSave ----
                btnSave.setText(null);
                btnSave.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                panel1.add(btnSave);
            }
            contentPanel.add(panel1, CC.xy(5, 11, CC.RIGHT, CC.DEFAULT));
        }
        contentPane.add(contentPanel, BorderLayout.NORTH);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel contentPanel;
    private JLabel label1;
    private JDateChooser jdcDatum;
    private JLabel label2;
    private JTextField txtUhrzeit;
    private JTabbedPane tabbedPane1;
    private JPanel pnlRR;
    private JLabel lblRRSys;
    private JTextField txtRRSys;
    private JLabel lblRRSysEinheit;
    private JLabel lblRRDia;
    private JTextField txtRRDia;
    private JLabel lblRRDiaEinheit;
    private JLabel lblPuls;
    private JTextField txtRRPuls;
    private JLabel lblPulsEinheit;
    private JPanel pnlPuls;
    private JPanel panel2;
    private JScrollPane scrollPane1;
    private JTextArea textArea1;
    private JPanel panel1;
    private JButton btnCancel;
    private JButton btnSave;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
