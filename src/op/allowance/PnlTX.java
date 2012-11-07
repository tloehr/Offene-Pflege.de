/*
 * Created by JFormDesigner on Tue Sep 04 16:11:31 CEST 2012
 */

package op.allowance;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.Allowance;
import entity.info.*;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.SYSCalendar;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Vector;

/**
 * @author Torsten Löhr
 */
public class PnlTX extends JPanel {
    public static final String internalClassID = "admin.residents.cash.pnltx";
    private Allowance tx;
    private Closure afterChange;
    DecimalFormat cf = new DecimalFormat("######.00");
    DateMidnight min;

    public PnlTX(Allowance tx, Closure afterChange) {
        super();
        this.tx = tx;
        this.afterChange = afterChange;
        initComponents();
        initPanel();
    }

    private void txtDateFocusLost(FocusEvent evt) {
//        DateTime dt;
//        try {
//            dt = new DateTime(SYSCalendar.parseDate(((JTextField) evt.getSource()).getText()));
//        } catch (NumberFormatException ex) {
//            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wrongdate")));
//            dt = new DateTime();
//        }
//        if (dt.isAfterNow()) {
//            dt = new DateTime();
//            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.futuredate")));
//        }
//        if (dt.isBefore(min)) {
//            dt = new DateTime();
//            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.DateTooOld")));
//        }
//
//        ((JTextField) evt.getSource()).setText(DateFormat.getDateInstance().format(dt.toDate()));
        SYSCalendar.handleDateFocusLost(evt, min.toDateTime());
    }

    private void txtDateFocusGained(FocusEvent e) {
        txtDate.selectAll();
    }

    private void txtTextFocusGained(FocusEvent e) {
        txtText.selectAll();
    }

    private void txtCashFocusGained(FocusEvent e) {
        txtCash.selectAll();
    }

    private void txtTextFocusLost(FocusEvent e) {
        if (txtText.getText().trim().isEmpty()) {
            txtText.setText(OPDE.lang.getString(internalClassID + ".txtText"));
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.emptyentry")));
        }
    }

    private void txtDateActionPerformed(ActionEvent e) {
        txtText.requestFocus();
    }

    private void txtTextActionPerformed(ActionEvent e) {
        txtCash.requestFocus();
    }

    private void txtCashActionPerformed(ActionEvent e) {
        save();
        afterChange.execute(tx);
        tx = new Allowance((Resident) cmbResident.getSelectedItem());
        txtDate.requestFocus();
    }

    private void save() {
        tx.setEditedBy(OPDE.getLogin().getUser());
        tx.setEditDate(new Date());
        tx.setDate(SYSCalendar.parseDate(txtDate.getText()));
        tx.setAmount(checkCash(txtCash.getText(), BigDecimal.ONE));
        tx.setText(txtText.getText().trim());
        tx.setResident((Resident) cmbResident.getSelectedItem());
    }

    private void txtCashFocusLost(FocusEvent e) {
        txtCash.setText(NumberFormat.getCurrencyInstance().format(checkCash(txtCash.getText(), BigDecimal.ONE)));
    }

    private void cmbResidentActionPerformed(ActionEvent e) {
        txtDate.requestFocus();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblResident = new JLabel();
        cmbResident = new JComboBox();
        lblDate = new JLabel();
        txtDate = new JTextField();
        lblText = new JLabel();
        txtText = new JTextField();
        lblCash = new JLabel();
        txtCash = new JTextField();

        //======== this ========
        setLayout(new FormLayout(
            "default, $lcgap, pref, $lcgap, 161dlu, $lcgap, default",
            "default, $lgap, pref, 4*($lgap, default)"));

        //---- lblResident ----
        lblResident.setText("text");
        lblResident.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblResident, CC.xy(3, 3));

        //---- cmbResident ----
        cmbResident.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbResident.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cmbResidentActionPerformed(e);
            }
        });
        add(cmbResident, CC.xy(5, 3));

        //---- lblDate ----
        lblDate.setText("text");
        lblDate.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblDate, CC.xy(3, 5));

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
        txtDate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtDateActionPerformed(e);
            }
        });
        add(txtDate, CC.xy(5, 5));

        //---- lblText ----
        lblText.setText("text");
        lblText.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblText, CC.xy(3, 7));

        //---- txtText ----
        txtText.setFont(new Font("Arial", Font.PLAIN, 14));
        txtText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtTextFocusGained(e);
            }
            @Override
            public void focusLost(FocusEvent e) {
                txtTextFocusLost(e);
            }
        });
        txtText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtTextActionPerformed(e);
            }
        });
        add(txtText, CC.xy(5, 7));

        //---- lblCash ----
        lblCash.setText("text");
        lblCash.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblCash, CC.xy(3, 9));

        //---- txtCash ----
        txtCash.setFont(new Font("Arial", Font.PLAIN, 14));
        txtCash.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtCashFocusGained(e);
            }
            @Override
            public void focusLost(FocusEvent e) {
                txtCashFocusLost(e);
            }
        });
        txtCash.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtCashActionPerformed(e);
            }
        });
        add(txtCash, CC.xy(5, 9));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private void initPanel() {
        lblResident.setText(OPDE.lang.getString("misc.msg.resident"));
        lblDate.setText(OPDE.lang.getString(internalClassID + ".lbldate"));
        lblText.setText(OPDE.lang.getString(internalClassID + ".lbltext"));
        lblCash.setText(OPDE.lang.getString(internalClassID + ".lblcash"));
        txtDate.setText(DateFormat.getDateInstance().format(tx.getDate()));
        txtCash.setText(cf.format(tx.getAmount()));
        txtText.setText(tx.getText());
        cmbResident.setModel(SYSTools.list2cmb(ResidentTools.getAllActive()));
        txtText.setText(OPDE.lang.getString(internalClassID + ".txtText"));

//        Vector<Component> traversalOrder = new Vector<Component>();
//        traversalOrder.add(cmbResident);
//        traversalOrder.add(txtDate);
//        traversalOrder.add(txtText);
//        traversalOrder.add(txtCash);
//        setFocusTraversalPolicy(new MyOwnFocusTraversalPolicy(traversalOrder));

        if (tx.getResident() != null) {
            cmbResident.setSelectedItem(tx.getResident());
            cmbResident.setEnabled(false);
        }

        ResInfo firstStay = ResInfoTools.getFirstResinfo(tx.getResident(), ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_STAY));
        min = firstStay == null ? new DateMidnight().dayOfMonth().withMinimumValue() : new DateMidnight(firstStay.getFrom());

        txtDate.requestFocus();
    }

    private BigDecimal checkCash(String text, BigDecimal defaultAmount) {
        BigDecimal myamount = SYSTools.parseCurrency(text);
        if (myamount != null) {
            if (myamount.equals(BigDecimal.ZERO)) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.emptycash")));
                myamount = defaultAmount;
            }
        } else {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wrongcash")));
            myamount = defaultAmount;
        }
        return myamount;
    }


//    /**
//     * http://docs.oracle.com/javase/tutorial/displayCode.html?code=http://docs.oracle.com/javase/tutorial/uiswing/examples/misc/FocusTraversalDemoProject/src/misc/FocusTraversalDemo.java
//     */
//    public static class MyOwnFocusTraversalPolicy
//            extends FocusTraversalPolicy {
//        Vector<Component> order;
//
//        public MyOwnFocusTraversalPolicy(Vector<Component> order) {
//            this.order = new Vector<Component>(order.size());
//            this.order.addAll(order);
//        }
//
//        public Component getComponentAfter(Container focusCycleRoot,
//                                           Component aComponent) {
//            int idx = (order.indexOf(aComponent) + 1) % order.size();
//            return order.get(idx);
//        }
//
//        public Component getComponentBefore(Container focusCycleRoot,
//                                            Component aComponent) {
//            int idx = order.indexOf(aComponent) - 1;
//            if (idx < 0) {
//                idx = order.size() - 1;
//            }
//            return order.get(idx);
//        }
//
//        public Component getDefaultComponent(Container focusCycleRoot) {
//            return order.get(0);
//        }
//
//        public Component getLastComponent(Container focusCycleRoot) {
//            return order.lastElement();
//        }
//
//        public Component getFirstComponent(Container focusCycleRoot) {
//            return order.get(0);
//        }
//    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblResident;
    private JComboBox cmbResident;
    private JLabel lblDate;
    private JTextField txtDate;
    private JLabel lblText;
    private JTextField txtText;
    private JLabel lblCash;
    private JTextField txtCash;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
