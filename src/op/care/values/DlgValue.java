/*
 * Created by JFormDesigner on Wed Jun 13 11:31:13 CEST 2012
 */

package op.care.values;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.values.ResValue;
import entity.values.ResValueTools;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.MyJDialog;
import op.tools.PnlUhrzeitDatum;
import op.tools.SYSConst;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.HorizontalLayout;
import org.jdesktop.swingx.VerticalLayout;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

import java.util.Date;

/**
 * @author Torsten Löhr
 */
public class DlgValue extends MyJDialog {
    public static final String internalClassID = "nursingrecords.vitalparameters.dialog";

    private ResValue wert;
    private Closure actionBlock;
    private int type;

    private PnlUhrzeitDatum pnlUhrzeitDatum;

    public DlgValue(ResValue wert, Closure actionBlock) {
        super();
        this.wert = wert;
        this.actionBlock = actionBlock;
        initComponents();
        initDialog();
        pack();
        setVisible(true);
    }

    private boolean saveOK() {

        PnlWerte123 pnl123 = tabWert.getSelectedComponent() instanceof PnlWerte123 ? (PnlWerte123) tabWert.getSelectedComponent() : null;

        boolean wert1OK = type == ResValueTools.VOMIT || type == ResValueTools.STOOL || pnl123.getWert1() != null;
        boolean wert2OK = type != ResValueTools.RR || pnl123.getWert2() != null && pnl123.getWert2().compareTo(BigDecimal.ZERO) > 0;
        boolean wert3OK = type != ResValueTools.RR || pnl123.getWert3() != null && pnl123.getWert3().compareTo(BigDecimal.ZERO) > 0;
        boolean bemerkungOK = (type != ResValueTools.VOMIT && type != ResValueTools.STOOL) || !txtBemerkung.getText().trim().isEmpty();

        String ursache = "";
        ursache += (wert1OK ? "" : "Der 1. Wert ist falsch");
        ursache += (wert2OK ? "" : "Der 2. Wert ist falsch.");
        ursache += (wert3OK ? "" : "Der 3. Wert ist falsch.");

        ursache += (bemerkungOK ? "" : "Bei Stuhlgang oder Erbrechen müssen Sie <b>unbedingt</b> eine Bemerkung eintragen. ");

        if (!ursache.isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("<html>" + ursache + "</html>", DisplayMessage.WARNING));
        }
        return wert1OK & wert2OK & wert3OK & bemerkungOK;

    }

    @Override
    public void dispose() {
        actionBlock.execute(wert);
        super.dispose();
    }

    private boolean save() {
        if (!saveOK()) return false;
        PnlWerte123 pnl123 = tabWert.getSelectedComponent() instanceof PnlWerte123 ? (PnlWerte123) tabWert.getSelectedComponent() : null;

        wert.setPit(pnlUhrzeitDatum.getPIT());
        wert.setText(txtBemerkung.getText().trim());
        wert.setCdate(new Date());
//        wert.setType(type);
        wert.setWert(type == ResValueTools.VOMIT || type == ResValueTools.STOOL ? null : pnl123.getWert1());
        wert.setValue2(type != ResValueTools.RR ? null : pnl123.getWert2());
        wert.setValue3(type != ResValueTools.RR ? null : pnl123.getWert3());

        return true;
    }

    private void initDialog() {

        pnlUhrzeitDatum = new PnlUhrzeitDatum(new Date());
        contentPanel.add(pnlUhrzeitDatum, CC.xy(3, 3));

        for (int tabnum = 1; tabnum < ResValueTools.VALUES.length; tabnum++) {
            if (tabnum == ResValueTools.RR) {
                tabWert.addTab(ResValueTools.VALUES[ResValueTools.RR], new PnlWerte123(new BigDecimal(120), new BigDecimal(80), new BigDecimal(60), ResValueTools.RRSYS, ResValueTools.UNITS[ResValueTools.RR], ResValueTools.RRDIA, ResValueTools.UNITS[ResValueTools.RR], ResValueTools.VALUES[ResValueTools.PULSE], ResValueTools.UNITS[ResValueTools.PULSE]));
            } else if (tabnum == ResValueTools.VOMIT || tabnum == ResValueTools.STOOL) {
                JLabel lbl = new JLabel("Bemerkung nicht vergessen");
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(SYSConst.ARIAL20);
                tabWert.addTab(ResValueTools.VALUES[tabnum], new JPanel(new VerticalLayout()).add(lbl));
            } else {
                tabWert.addTab(ResValueTools.VALUES[tabnum], new PnlWerte123(BigDecimal.ONE, ResValueTools.VALUES[tabnum], ResValueTools.UNITS[tabnum]));
            }
        }
//        jdcDatum.setDate(new Date());
//        uhrzeit = new Time(new Date().getTime());
//        txtUhrzeit.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(uhrzeit));

    }

    private void tabWertStateChanged(ChangeEvent e) {
        type = tabWert.getSelectedIndex() + 1;
    }

    private void btnSaveActionPerformed(ActionEvent e) {
        if (save()) {
            dispose();
        }
    }

    private void btnCancelActionPerformed(ActionEvent e) {
        wert = null;
        dispose();
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        contentPanel = new JPanel();
        tabWert = new JTabbedPane();
        scrollPane1 = new JScrollPane();
        txtBemerkung = new JTextArea();
        panel1 = new JPanel();
        btnCancel = new JButton();
        btnSave = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== contentPanel ========
        {
            contentPanel.setLayout(new FormLayout(
                "14dlu, $lcgap, 184dlu, $lcgap, 14dlu",
                "14dlu, $lgap, pref, $lgap, 5dlu, fill:132dlu:grow, $lgap, fill:43dlu:grow, $lgap, 23dlu, $lgap, 14dlu"));

            //======== tabWert ========
            {
                tabWert.setFont(new Font("Arial", Font.PLAIN, 12));
                tabWert.setTabPlacement(SwingConstants.RIGHT);
                tabWert.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        tabWertStateChanged(e);
                    }
                });
            }
            contentPanel.add(tabWert, CC.xy(3, 6));

            //======== scrollPane1 ========
            {
                scrollPane1.setViewportView(txtBemerkung);
            }
            contentPanel.add(scrollPane1, CC.xy(3, 8, CC.DEFAULT, CC.FILL));

            //======== panel1 ========
            {
                panel1.setLayout(new HorizontalLayout(5));

                //---- btnCancel ----
                btnCancel.setText(null);
                btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
                btnCancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnCancelActionPerformed(e);
                    }
                });
                panel1.add(btnCancel);

                //---- btnSave ----
                btnSave.setText(null);
                btnSave.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                btnSave.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnSaveActionPerformed(e);
                    }
                });
                panel1.add(btnSave);
            }
            contentPanel.add(panel1, CC.xy(3, 10, CC.RIGHT, CC.DEFAULT));
        }
        contentPane.add(contentPanel, BorderLayout.NORTH);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel contentPanel;
    private JTabbedPane tabWert;
    private JScrollPane scrollPane1;
    private JTextArea txtBemerkung;
    private JPanel panel1;
    private JButton btnCancel;
    private JButton btnSave;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
