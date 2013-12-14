/*
 * Created by JFormDesigner on Sat Dec 14 14:12:27 CET 2013
 */

package op.tools;

import com.toedter.calendar.JDateChooser;
import org.joda.time.LocalDate;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.util.Date;

/**
 * @author Torsten Löhr
 */
public class PnlFromTo extends PopupPanel {
    public PnlFromTo(LocalDate minFrom, LocalDate maxFrom, LocalDate minTo, LocalDate maxTo, String text) {
        initComponents();
        lblText.setText(SYSTools.xx(text));
        jdcFrom.setMinSelectableDate(minFrom.toDate());
        jdcFrom.setMaxSelectableDate(maxFrom.toDate());
        jdcTo.setMinSelectableDate(minTo.toDate());
        jdcTo.setMaxSelectableDate(maxTo.toDate());
    }

    public PnlFromTo(String text) {
        initComponents();
        jdcFrom.setDate(new Date());
        jdcTo.setDate(new Date());
        lblText.setText(SYSTools.xx(text));
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(super.getPreferredSize().width + 30, super.getPreferredSize().height);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        jdcFrom = new JDateChooser();
        label1 = new JLabel();
        jdcTo = new JDateChooser();
        lblText = new JLabel();

        //======== this ========
        setLayout(new BorderLayout());

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

            //---- jdcFrom ----
            jdcFrom.setFont(new Font("Arial", Font.PLAIN, 18));
            panel1.add(jdcFrom);

            //---- label1 ----
            label1.setText(null);
            label1.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/2rightarrow.png")));
            panel1.add(label1);

            //---- jdcTo ----
            jdcTo.setFont(new Font("Arial", Font.PLAIN, 18));
            panel1.add(jdcTo);
        }
        add(panel1, BorderLayout.CENTER);

        //---- lblText ----
        lblText.setText("text");
        lblText.setFont(new Font("Arial", Font.PLAIN, 22));
        add(lblText, BorderLayout.NORTH);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JDateChooser jdcFrom;
    private JLabel label1;
    private JDateChooser jdcTo;
    private JLabel lblText;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    @Override
    public Object getResult() {
        return new Pair<LocalDate, LocalDate>(new LocalDate(jdcFrom.getDate()), new LocalDate(jdcTo.getDate()));
    }

    @Override
    public void setStartFocus() {
        jdcFrom.requestFocus();
    }

    @Override
    public boolean isSaveOK() {
        return jdcFrom.getDate() != null && jdcTo.getDate() != null && jdcFrom.getDate().before(jdcTo.getDate());
    }

    public String getReason(){
        return "geht halt nicht";
    }

}
