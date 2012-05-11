/*
 * Created by JFormDesigner on Mon Apr 23 16:41:35 CEST 2012
 */

package op.care.berichte;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDateChooser;
import entity.Bewohner;
import entity.PBerichtTAGS;
import entity.PBerichtTAGSTools;
import entity.Pflegeberichte;
import op.tools.CleanablePanel;
import op.tools.MyJDialog;
import op.tools.SYSCalendar;
import org.apache.commons.collections.Closure;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Time;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Torsten Löhr
 */
public class DlgBericht extends MyJDialog {
    private Pflegeberichte bericht;
    private Bewohner bewohner;
    private PropertyChangeListener pcl;
    private final int DAUER = 3;
    private Closure actionBlock;

    public DlgBericht(Bewohner bewohner, Closure actionBlock) {
        super();
        this.bewohner = bewohner;
        this.actionBlock = actionBlock;
        initComponents();
        initDialog();
    }

    private void initDialog() {

        Date now = new Date();
        bericht = new Pflegeberichte(bewohner);
        bericht.setPit(now);
        bericht.setText("");
        bericht.setDauer(DAUER);

        pnlTags.setViewportView(PBerichtTAGSTools.createCheckBoxPanelForTags(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                JCheckBox cb = (JCheckBox) e.getSource();
                PBerichtTAGS tag = (PBerichtTAGS) cb.getClientProperty("UserObject");
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    bericht.getTags().remove(tag);
                } else {
                    bericht.getTags().add(tag);
                }
            }
        }, bericht.getTags(), new GridLayout(0, 1)));

        DateFormat df = DateFormat.getTimeInstance();
        jdcDatum.setDate(now);
        jdcDatum.setMaxSelectableDate(now);
        txtUhrzeit.setText(df.format(now));
        txtBericht.setText("");


        pcl = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                Time uhrzeit = new Time(SYSCalendar.erkenneUhrzeit(txtUhrzeit.getText()).getTimeInMillis());
                bericht.setPit(SYSCalendar.addTime2Date(jdcDatum.getDate(), uhrzeit));
            }
        };
        jdcDatum.addPropertyChangeListener(pcl);

        txtBericht.requestFocus();
    }

    @Override
    public void dispose() {
        jdcDatum.removePropertyChangeListener(pcl);
        jdcDatum.cleanup();
        super.dispose();
    }

    private void txtUhrzeitFocusLost(FocusEvent e) {
        GregorianCalendar gc;
        try {
            gc = SYSCalendar.erkenneUhrzeit(txtUhrzeit.getText());
            txtUhrzeit.setText(SYSCalendar.toGermanTime(gc));

        } catch (NumberFormatException nfe) {
            gc = new GregorianCalendar();
            txtUhrzeit.setText(SYSCalendar.toGermanTime(gc));
        }
        Time uhrzeit = new Time(gc.getTimeInMillis());
        bericht.setPit(SYSCalendar.addTime2Date(jdcDatum.getDate(), uhrzeit));
    }

    private void txtUhrzeitActionPerformed(ActionEvent e) {
        txtDauer.requestFocus();
    }

    private void txtDauerFocusGained(FocusEvent e) {
        txtBericht.requestFocus();
    }

    private void txtDauerFocusLost(FocusEvent e) {
        NumberFormat nf = NumberFormat.getIntegerInstance();
        String test = txtDauer.getText();
        int dauer = DAUER;
        try {
            Number num = nf.parse(test);
            dauer = num.intValue();
            if (dauer < 0) {
                dauer = DAUER;
                txtDauer.setText(Integer.toString(DAUER));
            }
        } catch (ParseException ex) {
            dauer = DAUER;
            txtDauer.setText(Integer.toString(DAUER));
        }
        bericht.setDauer(dauer);
    }

    private void txtBerichtCaretUpdate(CaretEvent e) {
        bericht.setText(txtBericht.getText());
    }

    private void btnCancelActionPerformed(ActionEvent e) {
        actionBlock.execute(null);
        dispose();
    }

    private void btnApplyActionPerformed(ActionEvent e) {
        actionBlock.execute(bericht);
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        label1 = new JLabel();
        jdcDatum = new JDateChooser();
        pnlTags = new JScrollPane();
        label2 = new JLabel();
        txtUhrzeit = new JTextField();
        label3 = new JLabel();
        txtDauer = new JTextField();
        scrollPane1 = new JScrollPane();
        txtBericht = new JTextArea();
        panel2 = new JPanel();
        btnCancel = new JButton();
        btnApply = new JButton();

        //======== this ========
        setResizable(false);
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "default:grow",
            "fill:default:grow"));

        //======== panel1 ========
        {
            panel1.setLayout(new FormLayout(
                "$rgap, $lcgap, default, $lcgap, 177dlu:grow, $lcgap, 115dlu:grow, 0dlu, $rgap",
                "0dlu, 3*($lgap, default), $lgap, default:grow, $lgap, default, $lgap, $rgap"));

            //---- label1 ----
            label1.setText("Datum");
            panel1.add(label1, CC.xy(3, 3));

            //---- jdcDatum ----
            jdcDatum.setFont(new Font("Lucida Grande", Font.BOLD, 16));
            panel1.add(jdcDatum, CC.xy(5, 3));
            panel1.add(pnlTags, CC.xywh(7, 3, 1, 7, CC.FILL, CC.DEFAULT));

            //---- label2 ----
            label2.setText("Uhrzeit");
            panel1.add(label2, CC.xy(3, 5));

            //---- txtUhrzeit ----
            txtUhrzeit.setFont(new Font("Lucida Grande", Font.BOLD, 16));
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
            panel1.add(txtUhrzeit, CC.xy(5, 5));

            //---- label3 ----
            label3.setText("Dauer");
            panel1.add(label3, CC.xy(3, 7));

            //---- txtDauer ----
            txtDauer.setText("3");
            txtDauer.setToolTipText("Dauer in Minuten");
            txtDauer.setFont(new Font("Lucida Grande", Font.BOLD, 16));
            txtDauer.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    txtDauerFocusGained(e);
                }
                @Override
                public void focusLost(FocusEvent e) {
                    txtDauerFocusLost(e);
                }
            });
            panel1.add(txtDauer, CC.xy(5, 7));

            //======== scrollPane1 ========
            {

                //---- txtBericht ----
                txtBericht.addCaretListener(new CaretListener() {
                    @Override
                    public void caretUpdate(CaretEvent e) {
                        txtBerichtCaretUpdate(e);
                    }
                });
                scrollPane1.setViewportView(txtBericht);
            }
            panel1.add(scrollPane1, CC.xywh(3, 9, 3, 1, CC.FILL, CC.FILL));

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
            panel1.add(panel2, CC.xywh(3, 11, 5, 1, CC.RIGHT, CC.FILL));
        }
        contentPane.add(panel1, CC.xy(1, 1));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel label1;
    private JDateChooser jdcDatum;
    private JScrollPane pnlTags;
    private JLabel label2;
    private JTextField txtUhrzeit;
    private JLabel label3;
    private JTextField txtDauer;
    private JScrollPane scrollPane1;
    private JTextArea txtBericht;
    private JPanel panel2;
    private JButton btnCancel;
    private JButton btnApply;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
