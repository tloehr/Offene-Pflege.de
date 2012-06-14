/*
 * Created by JFormDesigner on Wed Jun 13 11:31:13 CEST 2012
 */

package op.care.vital;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDateChooser;
import entity.BWerte;
import entity.BWerteTools;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.MyJDialog;
import op.tools.SYSCalendar;
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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;
import java.sql.Time;
import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Torsten Löhr
 */
public class DlgWert extends MyJDialog {
    public static final String internalClassID = "nursingrecords.vitalparameters.dialog";

    private BWerte wert;
    private Closure actionBlock;
    private int type;
    private Time uhrzeit;

    public DlgWert(BWerte wert, Closure actionBlock) {
        super();
        this.wert = wert;
        this.actionBlock = actionBlock;
        initComponents();
        initDialog();
        pack();
        setVisible(true);
    }

    private void txtUhrzeitFocusLost(FocusEvent e) {
        txtUhrzeitActionPerformed(null);
    }

    private boolean saveOK() {

        PnlWerte123 pnl123 = tabWert.getSelectedComponent() instanceof PnlWerte123 ? (PnlWerte123) tabWert.getSelectedComponent() : null;

        boolean wert1OK = type == BWerteTools.ERBRECHEN || type == BWerteTools.STUHLGANG || pnl123.getWert1() != null;
        boolean wert2OK = type != BWerteTools.RR || pnl123.getWert2() != null && pnl123.getWert2().compareTo(BigDecimal.ZERO) > 0;
        boolean wert3OK = type != BWerteTools.RR || pnl123.getWert3() != null && pnl123.getWert3().compareTo(BigDecimal.ZERO) > 0;
        boolean bemerkungOK = (type != BWerteTools.ERBRECHEN && type != BWerteTools.STUHLGANG) || !txtBemerkung.getText().trim().isEmpty();

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

        wert.setPit(SYSCalendar.addTime2Date(jdcDatum.getDate(), uhrzeit));
        wert.setBemerkung(txtBemerkung.getText().trim());
        wert.setCdate(new Date());
        wert.setType(type);
        wert.setWert(type == BWerteTools.ERBRECHEN || type == BWerteTools.STUHLGANG ? null : pnl123.getWert1());
        wert.setWert2(type != BWerteTools.RR ? null : pnl123.getWert2());
        wert.setWert3(type != BWerteTools.RR ? null : pnl123.getWert3());

        return true;
    }

    private void initDialog() {
        for (int tabnum = 1; tabnum < BWerteTools.WERTE.length; tabnum++) {
            if (tabnum == BWerteTools.RR) {
                tabWert.addTab(BWerteTools.WERTE[BWerteTools.RR], new PnlWerte123(new BigDecimal(120), new BigDecimal(80), new BigDecimal(60), BWerteTools.RRSYS, BWerteTools.EINHEIT[BWerteTools.RR], BWerteTools.RRDIA, BWerteTools.EINHEIT[BWerteTools.RR], BWerteTools.WERTE[BWerteTools.PULS], BWerteTools.EINHEIT[BWerteTools.PULS]));
            } else if (tabnum == BWerteTools.ERBRECHEN || tabnum == BWerteTools.STUHLGANG) {
                JLabel lbl = new JLabel("Bemerkung nicht vergessen");
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(SYSConst.ARIAL20);
                tabWert.addTab(BWerteTools.WERTE[tabnum], new JPanel(new VerticalLayout()).add(lbl));
            } else {
                tabWert.addTab(BWerteTools.WERTE[tabnum], new PnlWerte123(BigDecimal.ONE, BWerteTools.WERTE[tabnum], BWerteTools.EINHEIT[tabnum]));
            }
        }
        jdcDatum.setDate(new Date());
        uhrzeit = new Time(new Date().getTime());
        txtUhrzeit.setText(DateFormat.getTimeInstance().format(uhrzeit));

    }

    private void txtUhrzeitActionPerformed(ActionEvent e) {
        GregorianCalendar gc;
        try {
            gc = SYSCalendar.erkenneUhrzeit(txtUhrzeit.getText());
        } catch (NumberFormatException nfe) {
            gc = new GregorianCalendar();
        }
        txtUhrzeit.setText(DateFormat.getTimeInstance().format(new Date(gc.getTimeInMillis())));
        uhrzeit = new Time(gc.getTimeInMillis());

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
        label1 = new JLabel();
        jdcDatum = new JDateChooser();
        label2 = new JLabel();
        txtUhrzeit = new JTextField();
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
                    "14dlu, $lcgap, default, $lcgap, 184dlu, $lcgap, 14dlu",
                    "2*(14dlu, $lgap), 14dlu, 5dlu, fill:132dlu:grow, $lgap, fill:43dlu:grow, $lgap, 23dlu, $lgap, 14dlu"));

            //---- label1 ----
            label1.setText("Datum");
            label1.setFont(new Font("Arial", Font.PLAIN, 14));
            contentPanel.add(label1, CC.xy(3, 3));

            //---- jdcDatum ----
            jdcDatum.setFont(new Font("Arial", Font.PLAIN, 14));
            contentPanel.add(jdcDatum, CC.xy(5, 3));

            //---- label2 ----
            label2.setText("Uhrzeit");
            label2.setFont(new Font("Arial", Font.PLAIN, 14));
            contentPanel.add(label2, CC.xy(3, 5));

            //---- txtUhrzeit ----
            txtUhrzeit.setFont(new Font("Arial", Font.PLAIN, 14));
            txtUhrzeit.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    txtUhrzeitFocusLost(e);
                }
            });
            txtUhrzeit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    txtUhrzeitActionPerformed(e);
                }
            });
            contentPanel.add(txtUhrzeit, CC.xy(5, 5));

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
            contentPanel.add(tabWert, CC.xywh(3, 7, 3, 1));

            //======== scrollPane1 ========
            {
                scrollPane1.setViewportView(txtBemerkung);
            }
            contentPanel.add(scrollPane1, CC.xywh(3, 9, 3, 1, CC.DEFAULT, CC.FILL));

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
            contentPanel.add(panel1, CC.xy(5, 11, CC.RIGHT, CC.DEFAULT));
        }
        contentPane.add(contentPanel, BorderLayout.NORTH);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel contentPanel;
    private JLabel label1;
    private JDateChooser jdcDatum;
    private JLabel label2;
    private JTextField txtUhrzeit;
    private JTabbedPane tabWert;
    private JScrollPane scrollPane1;
    private JTextArea txtBemerkung;
    private JPanel panel1;
    private JButton btnCancel;
    private JButton btnSave;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
