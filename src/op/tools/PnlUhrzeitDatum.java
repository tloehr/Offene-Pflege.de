/*
 * Created by JFormDesigner on Fri Jun 15 14:37:24 CEST 2012
 */

package op.tools;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.Time;
import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Torsten Löhr
 */
public class PnlUhrzeitDatum extends JPanel {
    private Time uhrzeit;
    private Date preset;


    public PnlUhrzeitDatum() {
        this(new Date(), new Date());
    }

    public PnlUhrzeitDatum(Date preset) {
        this(preset, new Date());
    }

    public PnlUhrzeitDatum(Date preset, Date max) {
        initComponents();
        this.preset = preset;
        jdcDatum.setDate(preset);
        jdcDatum.setMaxSelectableDate(max == null ? SYSConst.DATE_BIS_AUF_WEITERES : max);
        uhrzeit = new Time(preset.getTime());
        txtUhrzeit.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(uhrzeit));
    }

    public Time getUhrzeit() {
        return uhrzeit;
    }

    public Date getDatum() {
        return jdcDatum.getDate();
    }


    public Date getPIT() {
        return SYSCalendar.addTime2Date(jdcDatum.getDate(), uhrzeit);
    }

    private void txtUhrzeitFocusLost(FocusEvent e) {
        txtUhrzeitActionPerformed(null);
    }

    private void txtUhrzeitActionPerformed(ActionEvent e) {
        GregorianCalendar gc;
        try {
            gc = SYSCalendar.erkenneUhrzeit(txtUhrzeit.getText());
        } catch (NumberFormatException nfe) {
            gc = new GregorianCalendar();
            gc.setTime(preset);
        }
        txtUhrzeit.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date(gc.getTimeInMillis())));
        uhrzeit = new Time(gc.getTimeInMillis());
    }

    private void txtUhrzeitFocusGained(FocusEvent e) {
        SYSTools.markAllTxt(txtUhrzeit);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        label1 = new JLabel();
        jdcDatum = new JDateChooser();
        txtUhrzeit = new JTextField();
        label2 = new JLabel();

        //======== this ========
        setLayout(new FormLayout(
            "default, $lcgap, default:grow",
            "16dlu, $lgap, 16dlu"));

        //---- label1 ----
        label1.setText("Datum");
        label1.setFont(new Font("Arial", Font.PLAIN, 14));
        add(label1, CC.xy(1, 1));

        //---- jdcDatum ----
        jdcDatum.setFont(new Font("Arial", Font.PLAIN, 14));
        add(jdcDatum, CC.xy(3, 1));

        //---- txtUhrzeit ----
        txtUhrzeit.setFont(new Font("Arial", Font.PLAIN, 14));
        txtUhrzeit.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtUhrzeitFocusGained(e);
            }
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
        add(txtUhrzeit, CC.xy(3, 3));

        //---- label2 ----
        label2.setText("Uhrzeit");
        label2.setFont(new Font("Arial", Font.PLAIN, 14));
        add(label2, CC.xy(1, 3));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel label1;
    private JDateChooser jdcDatum;
    private JTextField txtUhrzeit;
    private JLabel label2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
