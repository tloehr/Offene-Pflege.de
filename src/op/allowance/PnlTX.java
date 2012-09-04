/*
 * Created by JFormDesigner on Tue Sep 04 16:11:31 CEST 2012
 */

package op.allowance;

import java.awt.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;

import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import entity.Allowance;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.swing.*;

/**
 * @author Torsten Löhr
 */
public class PnlTX extends JPanel {
    private Allowance tx;
    private Closure afterChange;

    public PnlTX(Allowance tx, Closure afterChange) {
        super();
        this.tx = tx;
        this.afterChange = afterChange;
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblResident = new JLabel();
        comboBox1 = new JComboBox();
        lblDate = new JLabel();
        txtDate = new JTextField();
        lblText = new JLabel();
        txtText = new JTextField();
        lblCash = new JLabel();
        txtCash = new JTextField();

        //======== this ========
        setLayout(new FormLayout(
            "default, $lcgap, pref, $lcgap, 140dlu, $lcgap, default",
            "default, $lgap, pref, 4*($lgap, default)"));

        //---- lblResident ----
        lblResident.setText("text");
        lblResident.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblResident, CC.xy(3, 3));

        //---- comboBox1 ----
        comboBox1.setFont(new Font("Arial", Font.PLAIN, 14));
        add(comboBox1, CC.xy(5, 3));

        //---- lblDate ----
        lblDate.setText("text");
        lblDate.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblDate, CC.xy(3, 5));

        //---- txtDate ----
        txtDate.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtDate, CC.xy(5, 5));

        //---- lblText ----
        lblText.setText("text");
        lblText.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblText, CC.xy(3, 7));

        //---- txtText ----
        txtText.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtText, CC.xy(5, 7));

        //---- lblCash ----
        lblCash.setText("text");
        lblCash.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblCash, CC.xy(3, 9));

        //---- txtCash ----
        txtCash.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtCash, CC.xy(5, 9));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private Date checkDate(String text, Date resetDate) {
        GregorianCalendar gc;
        Date result = resetDate;

        try {
            gc = SYSCalendar.erkenneDatum(text);
            if (SYSCalendar.sameDay(gc, SYSCalendar.today()) > 0) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Sie haben ein Datum in der Zukunft eingegeben.", 2));
            } else {
                result = new Date(gc.getTimeInMillis());
            }
        } catch (NumberFormatException ex) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Sie haben ein falsches Datum eingegeben.", 2));

        }

        return result;
    }


    private BigDecimal checkCash(String text, BigDecimal defaultAmount) {
        BigDecimal mybetrag = SYSTools.parseCurrency(text);
        if (mybetrag != null) {
            if (mybetrag.equals(BigDecimal.ZERO)) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Beträge mit '0,00 " + SYSConst.eurosymbol + "' werden nicht angenommen.", 2));
                mybetrag = defaultBetrag;
            }
        } else {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Bitte geben Sie Euro Beträge in der folgenden Form ein: '10,0 " + SYSConst.eurosymbol + "'", 2));
//            lblMessage.setText(timeDF.format(new Date()) + " Uhr : " + "Bitte geben Sie Euro Beträge in der folgenden Form ein: '10,0 " + SYSConst.eurosymbol + "'");
            mybetrag = defaultBetrag;
        }
        return mybetrag;
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblResident;
    private JComboBox comboBox1;
    private JLabel lblDate;
    private JTextField txtDate;
    private JLabel lblText;
    private JTextField txtText;
    private JLabel lblCash;
    private JTextField txtCash;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
