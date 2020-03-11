/*
 * Created by JFormDesigner on Mon Apr 29 15:30:35 CEST 2013
 */

package de.offene_pflege.op.care.med.inventory;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.offene_pflege.op.tools.SYSCalendar;
import de.offene_pflege.op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.joda.time.DateTime;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.text.DateFormat;
import java.util.Date;

/**
 * @author Torsten Löhr
 */
public class PnlExpiry extends JPanel {
    Date expiry;
    private final Closure action;

    public PnlExpiry(Date expiry, String title, Closure action) {
        this.expiry = expiry;
        this.action = action;
        initComponents();
        lblTitle.setText(title);
        cbExpiry.setSelected(expiry != null);
        txtExpiry.setEnabled(expiry != null);
        cbExpiry.setText(SYSTools.xx("misc.msg.expires"));
        if (expiry != null) {
            txtExpiry.setText(DateFormat.getDateInstance().format(expiry));
        } else {
            txtExpiry.setText(null);
        }
    }

    private void cbExpiryItemStateChanged(ItemEvent e) {
        txtExpiry.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
        if (e.getStateChange() == ItemEvent.SELECTED){
            txtExpiry.requestFocus();
        }
    }

    private void txtExpiryFocusLost(FocusEvent e) {
        try {
            DateTime myExpiry = SYSCalendar.parseExpiryDate(txtExpiry.getText());
            if (myExpiry.isBeforeNow()) {
                throw new Exception("date must not be in the past");
            }
            expiry = myExpiry.toDate();
            txtExpiry.setText(DateFormat.getDateInstance().format(expiry));
        } catch (Exception ex) {
            expiry = null;
            txtExpiry.setText(null);
        }
    }

    private void txtExpiryActionPerformed(ActionEvent e) {
        txtExpiryFocusLost(null);
    }

    private void btnSaveActionPerformed(ActionEvent e) {
        action.execute(cbExpiry.isSelected() ? expiry : null);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblTitle = new JLabel();
        cbExpiry = new JCheckBox();
        txtExpiry = new JTextField();
        btnSave = new JButton();

        //======== this ========
        setLayout(new FormLayout(
            "2*(default, $lcgap), default:grow, 2*($lcgap, default)",
            "default, 2*($lgap, 14dlu), $lgap, default"));

        //---- lblTitle ----
        lblTitle.setText("text");
        lblTitle.setFont(lblTitle.getFont().deriveFont(lblTitle.getFont().getSize() + 4f));
        add(lblTitle, CC.xywh(3, 3, 5, 1));

        //---- cbExpiry ----
        cbExpiry.setText("text");
        cbExpiry.addItemListener(e -> cbExpiryItemStateChanged(e));
        add(cbExpiry, CC.xy(3, 5));

        //---- txtExpiry ----
        txtExpiry.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                txtExpiryFocusLost(e);
            }
        });
        txtExpiry.addActionListener(e -> txtExpiryActionPerformed(e));
        add(txtExpiry, CC.xy(5, 5, CC.DEFAULT, CC.FILL));

        //---- btnSave ----
        btnSave.setText(null);
        btnSave.setContentAreaFilled(false);
        btnSave.setBorderPainted(false);
        btnSave.setBorder(null);
        btnSave.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
        btnSave.addActionListener(e -> btnSaveActionPerformed(e));
        add(btnSave, CC.xy(7, 5));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblTitle;
    private JCheckBox cbExpiry;
    private JTextField txtExpiry;
    private JButton btnSave;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
