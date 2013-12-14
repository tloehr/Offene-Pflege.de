/*
 * Created by JFormDesigner on Sat Dec 14 11:20:19 CET 2013
 */

package op.tools;

import java.beans.*;
import com.toedter.calendar.JCalendar;
import op.OPDE;
import org.joda.time.LocalDate;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Torsten Löhr
 */
public class PnlDay extends PopupPanel {
    public PnlDay(LocalDate min, LocalDate max, String text) {
        initComponents();
        lblText.setText(SYSTools.xx(text));
        lblDate.setText(DateFormat.getDateInstance().format(cal.getDate()));
        cal.setMinSelectableDate(min.toDate());
        cal.setMaxSelectableDate(max.toDate());
    }

    public JCalendar getCal() {
        return cal;
    }

    @Override
    public Object getResult() {
        return cal.getDate();
    }

    @Override
    public void setStartFocus() {
        cal.requestFocus();
    }

    @Override
    public boolean isSaveOK() {
        return cal.getDate() != null;
    }

    @Override
    public String getReason() {
        return "ungültiges Datum";
    }

    private void calPropertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals("calendar")){
            lblDate.setText(DateFormat.getDateInstance().format(new Date(((GregorianCalendar) e.getNewValue()).getTimeInMillis())));
        }
//        OPDE.debug(e.getPropertyName());
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblText = new JLabel();
        cal = new JCalendar();
        lblDate = new JLabel();

        //======== this ========
        setLayout(new BorderLayout());

        //---- lblText ----
        lblText.setText("text");
        lblText.setFont(new Font("Arial", Font.PLAIN, 22));
        add(lblText, BorderLayout.NORTH);

        //---- cal ----
        cal.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                calPropertyChange(e);
            }
        });
        add(cal, BorderLayout.CENTER);

        //---- lblDate ----
        lblDate.setText("text");
        lblDate.setFont(new Font("Arial", Font.PLAIN, 22));
        lblDate.setForeground(Color.blue);
        lblDate.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblDate, BorderLayout.SOUTH);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblText;
    private JCalendar cal;
    private JLabel lblDate;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
