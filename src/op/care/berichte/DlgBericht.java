/*
 * Created by JFormDesigner on Mon Apr 23 16:41:35 CEST 2012
 */

package op.care.berichte;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.swing.DefaultOverlayable;
import com.jidesoft.swing.OverlayTextField;
import com.jidesoft.swing.OverlayableIconsFactory;
import com.jidesoft.swing.OverlayableUtils;
import entity.info.Resident;
import entity.PBerichtTAGS;
import entity.PBerichtTAGSTools;
import entity.Pflegeberichte;
import op.tools.MyJDialog;
import op.tools.PnlUhrzeitDatum;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * @author Torsten Löhr
 */
public class DlgBericht extends MyJDialog {
    private Pflegeberichte bericht;
    private Resident bewohner;
    private PropertyChangeListener pcl;
    private final int DAUER = 3;
    private Closure actionBlock;
    private OverlayTextField txtDauer;
    private DefaultOverlayable ovrDauer;
    private JLabel attentionIcon;
    private PnlUhrzeitDatum pnlUhrzeitDatum;

    public DlgBericht(Resident bewohner, Closure actionBlock) {
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

        pnlUhrzeitDatum = new PnlUhrzeitDatum(new Date());
        panel1.add(pnlUhrzeitDatum, CC.xywh(3, 3, 3, 1, CC.DEFAULT, CC.FILL));


        txtBericht.setText("");

        txtDauer = new OverlayTextField(Integer.toString(DAUER));
        txtDauer.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                txtDauerFocusGained(focusEvent);
            }
            @Override
            public void focusLost(FocusEvent focusEvent) {
                txtDauerFocusLost(focusEvent);
            }
        });
        attentionIcon = new JLabel(OverlayableUtils.getPredefinedOverlayIcon(OverlayableIconsFactory.ATTENTION));
        ovrDauer = new DefaultOverlayable(txtDauer, attentionIcon, DefaultOverlayable.SOUTH_EAST);
        ovrDauer.setOverlayVisible(true);
        panel1.add(ovrDauer, CC.xy(5, 5));

        txtBericht.requestFocus();
        setVisible(true);
    }

    private void txtDauerFocusGained(FocusEvent e) {
        SYSTools.markAllTxt(txtDauer);
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
        ovrDauer.setOverlayVisible(dauer == DAUER);
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
        bericht.setPit(pnlUhrzeitDatum.getPIT());
        actionBlock.execute(bericht);
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        pnlTags = new JScrollPane();
        label3 = new JLabel();
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
            "default, default:grow, $lcgap, default",
            "default, $lgap, fill:default:grow, $lgap, default"));

        //======== panel1 ========
        {
            panel1.setLayout(new FormLayout(
                "$rgap, $lcgap, default, $lcgap, 177dlu:grow, $lcgap, 115dlu:grow, 0dlu, $rgap",
                "0dlu, 2*($lgap, default), $lgap, fill:default:grow, $lgap, default, $lgap, $rgap"));
            panel1.add(pnlTags, CC.xywh(7, 3, 1, 5, CC.FILL, CC.FILL));

            //---- label3 ----
            label3.setText("Dauer");
            label3.setFont(new Font("Arial", Font.PLAIN, 14));
            panel1.add(label3, CC.xy(3, 5));

            //======== scrollPane1 ========
            {

                //---- txtBericht ----
                txtBericht.setFont(new Font("Arial", Font.PLAIN, 14));
                txtBericht.addCaretListener(new CaretListener() {
                    @Override
                    public void caretUpdate(CaretEvent e) {
                        txtBerichtCaretUpdate(e);
                    }
                });
                scrollPane1.setViewportView(txtBericht);
            }
            panel1.add(scrollPane1, CC.xywh(3, 7, 3, 1, CC.FILL, CC.FILL));

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
            panel1.add(panel2, CC.xywh(3, 9, 5, 1, CC.RIGHT, CC.FILL));
        }
        contentPane.add(panel1, CC.xy(2, 3));
        setSize(730, 455);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JScrollPane pnlTags;
    private JLabel label3;
    private JScrollPane scrollPane1;
    private JTextArea txtBericht;
    private JPanel panel2;
    private JButton btnCancel;
    private JButton btnApply;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
