/*
 * Created by JFormDesigner on Tue May 01 14:14:53 CEST 2012
 */

package op.care.verordnung;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.verordnungen.VerordnungPlanung;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.CleanablePanel;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.border.DropShadowBorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Torsten Löhr
 */
public class PnlBedarfDosis extends CleanablePanel {
    private VerordnungPlanung planung;
    private Closure actionBlock;

    public PnlBedarfDosis(VerordnungPlanung planung, Closure actionBlock) {
        this.actionBlock = actionBlock;

        if (planung == null) {
            planung = new VerordnungPlanung(true);
        }
        this.planung = planung;
        initComponents();
        initPanel();
    }

    private void initPanel() {
        txtEDosis.setText(planung.getMaxEDosis().toPlainString());
        txtMaxTimes.setText(planung.getMaxAnzahl().toString());
    }

    @Override
    public void cleanup() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void save() throws NumberFormatException {

        if (Double.parseDouble(txtEDosis.getText()) == 0d) {
            throw new NumberFormatException("Alle Dosierungen sind Null.");
        }

        if (Integer.parseInt(txtMaxTimes.getText()) == 0) {
            throw new NumberFormatException("Die Anzahl ist Null.");
        }

        planung.setNachtMo(BigDecimal.ZERO);
        planung.setMorgens(BigDecimal.ZERO);
        planung.setMittags(BigDecimal.ZERO);
        planung.setNachmittags(BigDecimal.ZERO);
        planung.setAbends(BigDecimal.ZERO);
        planung.setNachtAb(BigDecimal.ZERO);
        planung.setUhrzeitDosis(BigDecimal.ZERO);
        planung.setUhrzeit(null);

        planung.setTaeglich((short) 0);
        planung.setWoechentlich((short) 0);
        planung.setMonatlich((short) 0);
        planung.setLDatum(new Date());

        planung.setMon((short) 0);
        planung.setDie((short) 0);
        planung.setMit((short) 0);
        planung.setDon((short) 0);
        planung.setFre((short) 0);
        planung.setSam((short) 0);
        planung.setSon((short) 0);

        planung.setTagNum((short) 0);

        planung.setMaxEDosis(new BigDecimal(Double.parseDouble(txtEDosis.getText())));
        planung.setMaxAnzahl(Integer.parseInt(txtMaxTimes.getText()));

    }

    @Override
    public void reload() {

    }

    private void txtMaxTimesActionPerformed(ActionEvent e) {
        txtEDosis.requestFocus();
    }

    private void txtMaxTimesFocusGained(FocusEvent e) {
        SYSTools.markAllTxt((JTextField) e.getSource());
    }

    private void txtEDosisActionPerformed(ActionEvent e) {
        btnSave.doClick();
    }

    private void txtEDosisFocusGained(FocusEvent e) {
        SYSTools.markAllTxt((JTextField) e.getSource());
    }

    private void btnSaveActionPerformed(ActionEvent e) {
        try {
            save();
            actionBlock.execute(planung);
        } catch (NumberFormatException nfe) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Eingabefehler bei der Dosierung. Bitte prüfen. " + nfe.getLocalizedMessage(), 2));
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jPanel2 = new JPanel();
        label1 = new JLabel();
        label2 = new JLabel();
        lblDosis = new JLabel();
        txtMaxTimes = new JTextField();
        lblX = new JLabel();
        txtEDosis = new JTextField();
        btnSave = new JButton();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        //======== jPanel2 ========
        {
            jPanel2.setBorder(new DropShadowBorder(Color.black, 5, 0.5f, 12, true, true, true, true));
            jPanel2.setLayout(new FormLayout(
                "$rgap, $lcgap, default, $lcgap, pref, $lcgap, default, $lcgap, 37dlu, $lcgap, 52dlu, $lcgap, $rgap",
                "default, fill:default, $lgap, $rgap"));

            //---- label1 ----
            label1.setText("Anzahl");
            jPanel2.add(label1, CC.xy(5, 1));

            //---- label2 ----
            label2.setText("Dosis");
            jPanel2.add(label2, CC.xy(9, 1, CC.CENTER, CC.DEFAULT));

            //---- lblDosis ----
            lblDosis.setText("Max. Tagesdosis:");
            jPanel2.add(lblDosis, CC.xy(3, 2));

            //---- txtMaxTimes ----
            txtMaxTimes.setHorizontalAlignment(SwingConstants.RIGHT);
            txtMaxTimes.setText("1");
            txtMaxTimes.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    txtMaxTimesActionPerformed(e);
                }
            });
            txtMaxTimes.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    txtMaxTimesFocusGained(e);
                }
            });
            jPanel2.add(txtMaxTimes, CC.xy(5, 2));

            //---- lblX ----
            lblX.setText("x");
            jPanel2.add(lblX, CC.xy(7, 2));

            //---- txtEDosis ----
            txtEDosis.setHorizontalAlignment(SwingConstants.RIGHT);
            txtEDosis.setText("1.0");
            txtEDosis.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    txtEDosisActionPerformed(e);
                }
            });
            txtEDosis.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    txtEDosisFocusGained(e);
                }
            });
            jPanel2.add(txtEDosis, CC.xy(9, 2));

            //---- btnSave ----
            btnSave.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnSave.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnSaveActionPerformed(e);
                }
            });
            jPanel2.add(btnSave, CC.xy(11, 2, CC.RIGHT, CC.DEFAULT));
        }
        add(jPanel2);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel jPanel2;
    private JLabel label1;
    private JLabel label2;
    private JLabel lblDosis;
    private JTextField txtMaxTimes;
    private JLabel lblX;
    private JTextField txtEDosis;
    private JButton btnSave;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
