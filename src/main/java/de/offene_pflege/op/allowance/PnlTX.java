/*
 * Created by JFormDesigner on Tue Sep 04 16:11:31 CEST 2012
 */

package de.offene_pflege.op.allowance;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.offene_pflege.backend.entity.Allowance;
import de.offene_pflege.backend.entity.done.ResInfo;
import de.offene_pflege.backend.entity.done.Resident;
import de.offene_pflege.backend.services.ResInfoService;
import de.offene_pflege.backend.services.ResInfoTypeTools;
import de.offene_pflege.backend.services.ResidentTools;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.SYSCalendar;
import de.offene_pflege.op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.joda.time.LocalDate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;

/**
 * @author Torsten Löhr
 */
public class PnlTX extends JPanel {
    public static final String internalClassID = "admin.residents.cash.pnltx";
    private Allowance tx;
    private Closure afterChange;
//    DecimalFormat cf = new DecimalFormat("######.00");
    LocalDate min;
    Component focusOwner = null;

    public PnlTX(Allowance tx, Closure afterChange) {
        super();
        this.tx = tx;
        this.afterChange = afterChange;
        initComponents();
        initPanel();
    }

    private void txtDateFocusLost(FocusEvent evt) {
        SYSCalendar.handleDateFocusLost(evt, min, new LocalDate());
    }

    private void txtDateFocusGained(FocusEvent e) {
        txtDate.selectAll();
        focusOwner = e.getComponent();
    }

    private void txtTextFocusGained(FocusEvent e) {
        txtText.selectAll();
        focusOwner = e.getComponent();
    }

    private void txtCashFocusGained(FocusEvent e) {
        txtCash.selectAll();
        focusOwner = e.getComponent();
    }

    private void txtTextFocusLost(FocusEvent e) {
        if (txtText.getText().trim().isEmpty()) {
            txtText.setText(SYSTools.xx("admin.residents.cash.pnltx.txtText"));
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("misc.msg.emptyentry")));
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
        tx.setPit(SYSCalendar.parseDate(txtDate.getText()));
        tx.setAmount(checkCash(txtCash.getText(), BigDecimal.ONE));
        tx.setText(txtText.getText().trim());
        tx.setResident((Resident) cmbResident.getSelectedItem());
    }

    private void txtCashFocusLost(FocusEvent e) {
        txtCash.setText(SYSTools.formatCurrency(checkCash(txtCash.getText(), BigDecimal.ONE)));
    }


    private void cmbResidentItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            ResInfo firstStay = ResInfoService.getFirstResinfo((Resident) e.getItem(), ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_STAY));
            min = firstStay == null ? new LocalDate().dayOfMonth().withMinimumValue() : new LocalDate(firstStay.getFrom());
            txtDate.setText(new LocalDate().toString("dd.MM.yyyy"));
        }
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
        cmbResident.setFocusable(false);
        cmbResident.addItemListener(e -> cmbResidentItemStateChanged(e));
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
        txtDate.addActionListener(e -> txtDateActionPerformed(e));
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
        txtText.addActionListener(e -> txtTextActionPerformed(e));
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
        txtCash.addActionListener(e -> txtCashActionPerformed(e));
        add(txtCash, CC.xy(5, 9));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private void initPanel() {
        lblResident.setText(SYSTools.xx("misc.msg.resident"));
        lblDate.setText(SYSTools.xx("admin.residents.cash.pnltx.lbldate"));
        lblText.setText(SYSTools.xx("admin.residents.cash.pnltx.lbltext"));
        lblCash.setText(SYSTools.xx("admin.residents.cash.pnltx.lblcash"));
        txtDate.setText(DateFormat.getDateInstance().format(tx.getPit()));
        txtCash.setText(SYSTools.formatCurrency(tx.getAmount()));
        if (tx.getId() == null) {
            txtText.setText(SYSTools.xx("admin.residents.cash.pnltx.txtText"));
        } else {
            txtText.setText(tx.getText());
        }


        ResInfo firstStay = null;
        if (tx.getResident() != null) {
            cmbResident.setModel(new DefaultComboBoxModel<Resident>(new Resident[]{tx.getResident()}));
            firstStay = ResInfoService.getFirstResinfo(tx.getResident(), ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_STAY));
        } else {
            ArrayList<Resident> list = ResidentTools.getAllActive();
            cmbResident.setModel(SYSTools.list2cmb(list));
            if (!list.isEmpty()) {
                firstStay = ResInfoService.getFirstResinfo(list.get(0), ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_STAY));
            }
        }

        min = (firstStay == null ? new LocalDate().dayOfMonth().withMinimumValue() : new LocalDate(firstStay.getFrom()));

        setFocusCycleRoot(true);
        setFocusTraversalPolicy(new FocusTraversalPolicy() {
            @Override
            public Component getComponentAfter(Container aContainer, Component aComponent) {
                if (focusOwner == null) {
                    focusOwner = txtDate;
                } else if (focusOwner.equals(txtDate)) {
                    focusOwner = txtText;
                } else if (focusOwner.equals(txtText)) {
                    focusOwner = txtCash;
                } else if (focusOwner.equals(txtCash)) {
                    focusOwner = txtDate;
                } else {
                    focusOwner = txtDate;
                }
                return focusOwner;
            }

            @Override
            public Component getComponentBefore(Container aContainer, Component aComponent) {
                if (focusOwner == null) {
                    focusOwner = txtDate;
                } else if (focusOwner.equals(txtDate)) {
                    focusOwner = txtCash;
                } else if (focusOwner.equals(txtText)) {
                    focusOwner = txtDate;
                } else if (focusOwner.equals(txtCash)) {
                    focusOwner = txtText;
                } else {
                    focusOwner = txtDate;
                }
                return focusOwner;
            }

            @Override
            public Component getFirstComponent(Container aContainer) {
                return txtDate;
            }

            @Override
            public Component getLastComponent(Container aContainer) {
                return txtCash;
            }

            @Override
            public Component getDefaultComponent(Container aContainer) {
                return txtDate;
            }
        });

        txtDate.requestFocus();
    }

    private BigDecimal checkCash(String text, BigDecimal defaultAmount) {
        BigDecimal myamount = SYSTools.parseCurrency(text);
        if (myamount != null) {
            if (myamount.equals(BigDecimal.ZERO)) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("misc.msg.emptycash")));
                myamount = defaultAmount;
            }
        } else {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("misc.msg.wrongcash")));
            myamount = defaultAmount;
        }
        return myamount;
    }

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
