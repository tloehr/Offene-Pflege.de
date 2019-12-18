/*
 * Created by JFormDesigner on Fri Jun 15 14:37:24 CEST 2012
 */

package op.tools;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Torsten Löhr
 */
public class PnlPIT extends JPanel {
    private LocalTime time;
    private Date preset;
    private Date max, min;


    public PnlPIT() {
        this(new Date(), new Date(), SYSConst.DATE_THE_VERY_BEGINNING);
    }

    public PnlPIT(Date preset, boolean enableDate) {
        this(preset, new Date(), SYSConst.DATE_THE_VERY_BEGINNING);
        txtDate.setEnabled(enableDate);
    }
//
//    public PnlPIT(Date preset) {
//        this(preset, true);
//
//    }
//

    public PnlPIT(Date preset, Date max, Date min) {
        this.max = max;
        this.min = min;
        initComponents();
        labelDatum.setText(SYSTools.xx("misc.msg.Date"));
        labelUhrzeit.setText(SYSTools.xx("misc.msg.Time.long"));
        this.preset = preset;


        txtDate.setText(DateFormat.getDateInstance().format(preset));

        time = new LocalTime(preset);
        txtUhrzeit.setText(time.toString("HH:mm"));
    }

    public Date getPIT() {
        LocalDate day;
        try {
            day = new LocalDate(SYSCalendar.parseDate(txtDate.getText()));
        } catch (NumberFormatException ex) {
            day = new LocalDate();
        }

//        DateTime time = new DateTime(this.time);

        return day.toLocalDateTime(time).toDate();//.plusHours(time.getHourOfDay()).plusMinutes(time.getMinuteOfHour()).plusSeconds(time.getSecondOfMinute()).toDate();
    }

    private void txtUhrzeitFocusLost(FocusEvent e) {
        txtUhrzeitActionPerformed(null);
    }

    private void txtUhrzeitActionPerformed(ActionEvent e) {
        GregorianCalendar gc;
        try {
            gc = SYSCalendar.parseTime(txtUhrzeit.getText());
        } catch (NumberFormatException nfe) {
            gc = null;
        }

        DateTime pit;
        if (gc != null) {
            LocalTime time = new LocalTime(gc.getTimeInMillis());
            LocalDate day = new LocalDate(SYSCalendar.parseDate(txtDate.getText()));
            pit = day.toDateTime(time);//.plusHours(time.getHourOfDay()).plusMinutes(time.getMinuteOfHour()).plusSeconds(time.getSecondOfMinute());

            if (pit.isAfter(new DateTime(max))) {
                pit = new DateTime(max);
            }

            if (pit.isBefore(new DateTime(min))) {
                pit = new DateTime(min);
            }

        } else {
            pit = new DateTime();
        }


        txtUhrzeit.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(pit.toDate()));
        time = pit.toLocalTime();//this.time = new Time(gc.getTimeInMillis());

    }

    private void txtUhrzeitFocusGained(FocusEvent e) {
        txtUhrzeit.selectAll();
    }

    private void txtDateFocusLost(FocusEvent evt) {
        SYSCalendar.handleDateFocusLost(evt, new LocalDate(min), new LocalDate(max));
    }

    private void txtDateFocusGained(FocusEvent e) {
        txtDate.selectAll();
    }

    private void txtDateActionPerformed(ActionEvent e) {
        txtUhrzeit.requestFocus();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        labelDatum = new JLabel();
        txtDate = new JTextField();
        txtUhrzeit = new JTextField();
        labelUhrzeit = new JLabel();

        //======== this ========
        setLayout(new FormLayout(
                "default, $lcgap, default:grow",
                "16dlu, $nlgap, 16dlu"));

        //---- labelDatum ----
        labelDatum.setText("Datum");
        labelDatum.setFont(new Font("Arial", Font.PLAIN, 14));
        add(labelDatum, CC.xy(1, 1, CC.DEFAULT, CC.FILL));

        //---- txtDate ----
        txtDate.setFont(new Font("Arial", Font.PLAIN, 14));
        txtDate.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtDateFocusGained(e);
            }

            @Override
            public void focusLost(FocusEvent e) {
                txtDateFocusLost(e);
            }
        });
        txtDate.addActionListener(e -> txtDateActionPerformed(e));
        add(txtDate, CC.xy(3, 1, CC.DEFAULT, CC.FILL));

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
        txtUhrzeit.addActionListener(e -> txtUhrzeitActionPerformed(e));
        add(txtUhrzeit, CC.xy(3, 3, CC.DEFAULT, CC.FILL));

        //---- labelUhrzeit ----
        labelUhrzeit.setText("Uhrzeit");
        labelUhrzeit.setFont(new Font("Arial", Font.PLAIN, 14));
        add(labelUhrzeit, CC.xy(1, 3, CC.DEFAULT, CC.FILL));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel labelDatum;
    private JTextField txtDate;
    private JTextField txtUhrzeit;
    private JLabel labelUhrzeit;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
