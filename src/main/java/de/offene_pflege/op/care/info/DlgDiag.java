/*
 * OffenePflege
 * Copyright (C) 2006-2012 Torsten Löhr
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License V2 as published by the Free Software Foundation
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; if not, write to
 * the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
 * www.offene-pflege.de
 * ------------------------
 * Auf deutsch (freie Übersetzung. Rechtlich gilt die englische Version)
 * Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU General Public License,
 * wie von der Free Software Foundation veröffentlicht, weitergeben und/oder modifizieren, gemäß Version 2 der Lizenz.
 *
 * Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, daß es Ihnen von Nutzen sein wird, aber
 * OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN
 * BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht,
 * schreiben Sie an die Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA.
 *
 */
package de.offene_pflege.op.care.info;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
import de.offene_pflege.entity.EntityTools;
import de.offene_pflege.entity.info.ICD;
import de.offene_pflege.entity.info.ResInfo;
import de.offene_pflege.entity.info.ResInfoTools;
import de.offene_pflege.entity.prescription.GP;
import de.offene_pflege.entity.prescription.GPTools;
import de.offene_pflege.entity.prescription.Hospital;
import de.offene_pflege.entity.prescription.HospitalTools;
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.residents.PnlEditGP;
import de.offene_pflege.op.residents.PnlEditHospital;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.MyJDialog;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.HorizontalLayout;
import org.jdesktop.swingx.JXSearchField;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Properties;

/**
 * @author root
 */
public class DlgDiag extends MyJDialog {
    public static final String internalClassID = "nursingrecords.info.dlg.diags";

    private ListSelectionListener lsl;
    private String text;
    private ResInfo diag;
    private Closure actionBlock;

    /**
     * Der Dialog erzeugt immer eine neue Diagnose. Obwohl hier eine übergeben wird, kann sie nachträglich nicht bearbeitet werden.
     */
    public DlgDiag(ResInfo diag, Closure actionBlock) {
        super(false);
        this.diag = diag;
        this.actionBlock = actionBlock;
        initComponents();
        initDialog();
    }

    private void initDialog() {
        fillCMBs();

        String tooltip = SYSTools.xx("nursingrecords.info.dlg.diags.tx.tooltip").replace('[', '<').replace(']', '>');
        lblTX.setToolTipText(SYSTools.toHTMLForScreen("<p style=\"width:300px;\">" + tooltip + "</p>"));

        txtSuche.setPrompt(SYSTools.xx("misc.msg.search"));
        lblDiagBy.setText(SYSTools.xx("nursingrecords.info.dlg.diags.by"));
        lblSide.setText(SYSTools.xx("misc.msg.diag.side"));
        lblSecurity.setText(SYSTools.xx("misc.msg.diag.security"));
        lblInterval.setText(SYSTools.xx("nursingrecords.info.dlg.interval_noconstraints"));
        lblInterval.setIcon(SYSConst.findIcon(SYSConst.icon22intervalNoConstraints));

        lblQDVSTitle.setText(SYSTools.xx("misc.msg.diag.qdvstitle"));
        cbTumor.setText(SYSTools.xx("misc.msg.diag.cbtumor"));
        cbTetra.setText(SYSTools.xx("misc.msg.diag.cbtetra"));
        cbChorea.setText(SYSTools.xx("misc.msg.diag.cbchorea"));
        cbApallisch.setText(SYSTools.xx("misc.msg.diag.cbapallisch"));
        cbParkinson.setText(SYSTools.xx("misc.msg.diag.cbparkinson"));
        cbOsteo.setText(SYSTools.xx("misc.msg.diag.cbosteo"));
        cbMS.setText(SYSTools.xx("misc.msg.diag.cbms"));
        lblQDVSNotiz.setText(SYSTools.xx("misc.msg.diag.diab.demenz.note"));

        reloadTable();
        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("nursingrecords.info.dlg.diags"), 10));
    }

    private void txtSucheActionPerformed(ActionEvent e) {
        reloadTable();
    }

    private void btnAddGPActionPerformed(ActionEvent e) {
        final PnlEditGP pnlGP = new PnlEditGP(new GP());
        JidePopup popup = GUITools.createPanelPopup(pnlGP, o -> {
            if (o != null) {
                GP gp = EntityTools.merge((GP) o);
                cmbArzt.setModel(new DefaultComboBoxModel(new GP[]{gp}));
            }
        }, btnAddGP);
        GUITools.showPopup(popup, SwingConstants.EAST);
    }

    private void btnAddHospitalActionPerformed(ActionEvent e) {
        final PnlEditHospital pnlHospital = new PnlEditHospital(new Hospital());
        JidePopup popup = GUITools.createPanelPopup(pnlHospital, o -> {
            if (o != null) {
                Hospital hospital = EntityTools.merge((Hospital) o);
                cmbKH.setModel(new DefaultComboBoxModel(new Hospital[]{hospital}));
            }
        }, btnAddHospital);
        GUITools.showPopup(popup, SwingConstants.EAST);
    }


    private void fillCMBs() {
        EntityManager em = OPDE.createEM();
        Query queryArzt = em.createQuery("SELECT a FROM GP a WHERE a.status >= 0 ORDER BY a.name, a.vorname");
        java.util.List<GP> listAerzte = queryArzt.getResultList();
        listAerzte.add(0, null);

        Query queryKH = em.createQuery("SELECT k FROM Hospital k WHERE k.state >= 0 ORDER BY k.name");
        java.util.List<Hospital> listKH = queryKH.getResultList();
        listKH.add(0, null);
        em.close();

        cmbArzt.setModel(new DefaultComboBoxModel(listAerzte.toArray()));
        cmbArzt.setRenderer(GPTools.getRenderer());
        cmbArzt.setSelectedIndex(0);

        cmbKH.setModel(new DefaultComboBoxModel(listKH.toArray()));
        cmbKH.setRenderer(HospitalTools.getKHRenderer());
        cmbKH.setSelectedIndex(0);

        cmbSicherheit.setModel(new DefaultComboBoxModel(new String[]{
                SYSTools.xx("misc.msg.diag.security.na"),
                SYSTools.xx("misc.msg.diag.security.confirmed"),
                SYSTools.xx("misc.msg.diag.security.suspected"),
                SYSTools.xx("misc.msg.diag.security.rulingout"),
                SYSTools.xx("misc.msg.diag.security.conditionafter")
        }));
        cmbSicherheit.setSelectedIndex(1);

        cmbKoerper.setModel(new DefaultComboBoxModel(new String[]{
                SYSTools.xx("misc.msg.diag.side.na"),
                SYSTools.xx("misc.msg.diag.side.left"),
                SYSTools.xx("misc.msg.diag.side.right"),
                SYSTools.xx("misc.msg.diag.side.both")
        }));
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the PrinterForm Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new JPanel();
        txtSuche = new JXSearchField();
        lblTX = new JLabel();
        jspDiagnosen = new JScrollPane();
        lstDiag = new JList();
        lblDiagBy = new JLabel();
        cmbArzt = new JComboBox<>();
        btnAddGP = new JButton();
        cmbKH = new JComboBox<>();
        btnAddHospital = new JButton();
        lblSecurity = new JLabel();
        lblSide = new JLabel();
        cmbKoerper = new JComboBox<>();
        cmbSicherheit = new JComboBox<>();
        panel3 = new JPanel();
        jScrollPane1 = new JScrollPane();
        txtBemerkung = new JTextArea();
        pnlQDVSMarkierungen = new JPanel();
        lblQDVSTitle = new JLabel();
        panel2 = new JPanel();
        cbTumor = new JCheckBox();
        cbTetra = new JCheckBox();
        cbChorea = new JCheckBox();
        cbApallisch = new JCheckBox();
        cbParkinson = new JCheckBox();
        cbOsteo = new JCheckBox();
        cbMS = new JCheckBox();
        lblQDVSNotiz = new JTextArea();
        lblInterval = new JLabel();
        panel1 = new JPanel();
        btnCancel = new JButton();
        btnOK = new JButton();

        //======== this ========
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
            jPanel1.setLayout(new FormLayout(
                "default, $lcgap, pref, $lcgap, default:grow, $ugap, default, pref, $lcgap, default:grow, 2*($lcgap, default)",
                "default, $lgap, fill:default, $lgap, fill:104dlu:grow, $lgap, fill:default, $lgap, default, $lgap, fill:default, $lgap, fill:89dlu:grow, $ugap, default, $lgap, default"));

            //---- txtSuche ----
            txtSuche.setFont(new Font("Arial", Font.PLAIN, 14));
            txtSuche.addActionListener(e -> txtSucheActionPerformed(e));
            jPanel1.add(txtSuche, CC.xywh(3, 3, 8, 1));

            //---- lblTX ----
            lblTX.setText(null);
            lblTX.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/ambulance2.png")));
            jPanel1.add(lblTX, CC.xy(12, 3));

            //======== jspDiagnosen ========
            {

                //---- lstDiag ----
                lstDiag.setFont(new Font("Arial", Font.PLAIN, 14));
                jspDiagnosen.setViewportView(lstDiag);
            }
            jPanel1.add(jspDiagnosen, CC.xywh(3, 5, 10, 1));

            //---- lblDiagBy ----
            lblDiagBy.setText("Festgestellt durch:");
            lblDiagBy.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel1.add(lblDiagBy, CC.xy(3, 7, CC.RIGHT, CC.DEFAULT));

            //---- cmbArzt ----
            cmbArzt.setModel(new DefaultComboBoxModel<>(new String[] {
                "Item 1",
                "Item 2",
                "Item 3",
                "Item 4"
            }));
            cmbArzt.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel1.add(cmbArzt, CC.xywh(5, 7, 6, 1));

            //---- btnAddGP ----
            btnAddGP.setText(null);
            btnAddGP.setBorder(null);
            btnAddGP.setContentAreaFilled(false);
            btnAddGP.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
            btnAddGP.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnAddGP.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/pressed.png")));
            btnAddGP.addActionListener(e -> btnAddGPActionPerformed(e));
            jPanel1.add(btnAddGP, CC.xy(12, 7));

            //---- cmbKH ----
            cmbKH.setModel(new DefaultComboBoxModel<>(new String[] {
                "Item 1",
                "Item 2",
                "Item 3",
                "Item 4"
            }));
            cmbKH.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel1.add(cmbKH, CC.xywh(5, 9, 6, 1));

            //---- btnAddHospital ----
            btnAddHospital.setText(null);
            btnAddHospital.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
            btnAddHospital.setBorder(null);
            btnAddHospital.setContentAreaFilled(false);
            btnAddHospital.setBorderPainted(false);
            btnAddHospital.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnAddHospital.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/pressed.png")));
            btnAddHospital.addActionListener(e -> btnAddHospitalActionPerformed(e));
            jPanel1.add(btnAddHospital, CC.xy(12, 9));

            //---- lblSecurity ----
            lblSecurity.setText("Diagnosesicherheit:");
            lblSecurity.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel1.add(lblSecurity, CC.xy(8, 11));

            //---- lblSide ----
            lblSide.setText("K\u00f6rperseite:");
            lblSide.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel1.add(lblSide, CC.xy(3, 11, CC.RIGHT, CC.DEFAULT));

            //---- cmbKoerper ----
            cmbKoerper.setModel(new DefaultComboBoxModel<>(new String[] {
                "Nicht festgelegt",
                "links",
                "rechts",
                "beidseitig"
            }));
            cmbKoerper.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel1.add(cmbKoerper, CC.xywh(5, 11, 2, 1));

            //---- cmbSicherheit ----
            cmbSicherheit.setModel(new DefaultComboBoxModel<>(new String[] {
                "Nicht festgelegt",
                "gesichert",
                "Verdacht auf",
                "Ausschlu\u00df von",
                "Zustand nach"
            }));
            cmbSicherheit.setFont(new Font("Arial", Font.PLAIN, 14));
            jPanel1.add(cmbSicherheit, CC.xywh(10, 11, 3, 1));

            //======== panel3 ========
            {
                panel3.setLayout(new FormLayout(
                    "166dlu, $ugap, pref:grow",
                    "fill:89dlu:grow"));

                //======== jScrollPane1 ========
                {

                    //---- txtBemerkung ----
                    txtBemerkung.setColumns(20);
                    txtBemerkung.setRows(5);
                    txtBemerkung.setFont(new Font("Arial", Font.PLAIN, 14));
                    jScrollPane1.setViewportView(txtBemerkung);
                }
                panel3.add(jScrollPane1, CC.xy(1, 1));

                //======== pnlQDVSMarkierungen ========
                {
                    pnlQDVSMarkierungen.setBorder(LineBorder.createBlackLineBorder());
                    pnlQDVSMarkierungen.setLayout(new FormLayout(
                        "144dlu:grow",
                        "fill:default, $lgap, fill:pref:grow, pref"));

                    //---- lblQDVSTitle ----
                    lblQDVSTitle.setText("text");
                    lblQDVSTitle.setFont(new Font("Arial", Font.BOLD, 18));
                    lblQDVSTitle.setBackground(new Color(255, 255, 102));
                    lblQDVSTitle.setForeground(new Color(51, 51, 255));
                    pnlQDVSMarkierungen.add(lblQDVSTitle, CC.xy(1, 1, CC.CENTER, CC.DEFAULT));

                    //======== panel2 ========
                    {
                        panel2.setLayout(new FlowLayout());

                        //---- cbTumor ----
                        cbTumor.setText("text");
                        cbTumor.setFont(new Font("Arial", Font.PLAIN, 14));
                        panel2.add(cbTumor);

                        //---- cbTetra ----
                        cbTetra.setText("text");
                        cbTetra.setFont(new Font("Arial", Font.PLAIN, 14));
                        panel2.add(cbTetra);

                        //---- cbChorea ----
                        cbChorea.setText("text");
                        cbChorea.setFont(new Font("Arial", Font.PLAIN, 14));
                        panel2.add(cbChorea);

                        //---- cbApallisch ----
                        cbApallisch.setText("text");
                        cbApallisch.setFont(new Font("Arial", Font.PLAIN, 14));
                        panel2.add(cbApallisch);

                        //---- cbParkinson ----
                        cbParkinson.setText("text");
                        cbParkinson.setFont(new Font("Arial", Font.PLAIN, 14));
                        panel2.add(cbParkinson);

                        //---- cbOsteo ----
                        cbOsteo.setText("text");
                        cbOsteo.setFont(new Font("Arial", Font.PLAIN, 14));
                        panel2.add(cbOsteo);

                        //---- cbMS ----
                        cbMS.setText("text");
                        cbMS.setFont(new Font("Arial", Font.PLAIN, 14));
                        panel2.add(cbMS);
                    }
                    pnlQDVSMarkierungen.add(panel2, CC.xy(1, 3));

                    //---- lblQDVSNotiz ----
                    lblQDVSNotiz.setText("Die Passage \"Lorem ipsum ...\" stammt aus folgendem Text: \"Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit ...\". Dieser l\u00e4sst sich wie folgt \u00fcbersetzen: Es gibt niemanden, der den Schmerz selbst liebt, der ihn sucht und haben will, einfach, weil es Schmerz ist.\"");
                    lblQDVSNotiz.setFont(new Font("Arial", Font.PLAIN, 14));
                    lblQDVSNotiz.setWrapStyleWord(true);
                    lblQDVSNotiz.setBackground(new Color(255, 255, 204));
                    lblQDVSNotiz.setLineWrap(true);
                    pnlQDVSMarkierungen.add(lblQDVSNotiz, CC.xy(1, 4));
                }
                panel3.add(pnlQDVSMarkierungen, CC.xy(3, 1));
            }
            jPanel1.add(panel3, CC.xywh(3, 13, 11, 1));

            //---- lblInterval ----
            lblInterval.setText("text");
            jPanel1.add(lblInterval, CC.xywh(3, 15, 6, 1));

            //======== panel1 ========
            {
                panel1.setLayout(new HorizontalLayout(5));

                //---- btnCancel ----
                btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
                btnCancel.setText(null);
                btnCancel.addActionListener(e -> btnCancelActionPerformed(e));
                panel1.add(btnCancel);

                //---- btnOK ----
                btnOK.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                btnOK.setText(null);
                btnOK.addActionListener(e -> btnOKActionPerformed(e));
                panel1.add(btnOK);
            }
            jPanel1.add(panel1, CC.xywh(8, 15, 5, 1, CC.RIGHT, CC.DEFAULT));
        }
        contentPane.add(jPanel1, BorderLayout.CENTER);
        setSize(860, 655);
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    private void btnOKActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        if (saveOK()) {
            save();
            dispose();
        }
    }//GEN-LAST:event_btnOKActionPerformed

    private void btnCancelActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        diag = null;
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    @Override
    public void dispose() {
        super.dispose();
        actionBlock.execute(diag);
    }

    private boolean saveOK() {
        boolean saveOK = true;

        if (cmbArzt.getSelectedItem() == null && cmbKH.getSelectedItem() == null) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("misc.msg.gpANDhospitalempty")));
            saveOK = false;
        }

        if (lstDiag.getSelectedValue() == null) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("nursingrecords.info.dlg.diags.emptydiag")));
            saveOK = false;
        }

        return saveOK;
    }


    private void save() {
        GP doc = (GP) cmbArzt.getSelectedItem();
        Hospital kh = (Hospital) cmbKH.getSelectedItem();
        ICD icd = (ICD) lstDiag.getSelectedValue();

        Properties props = new Properties();
        props.put("icd", icd.getICD10());
        props.put("text", icd.getText());
        props.put("koerperseite", cmbKoerper.getSelectedItem());
        props.put("diagnosesicherheit", cmbSicherheit.getSelectedItem());
        props.put("arztid", doc == null ? "null" : doc.getArztID().toString());
        props.put("khid", kh == null ? "null" : kh.getKhid().toString());

        // Erweiterung für die QDVS V01.1
        props.put("tumor", Boolean.toString(cbTumor.isSelected()));
        props.put("tetra", Boolean.toString(cbTetra.isSelected()));
        props.put("chorea", Boolean.toString(cbChorea.isSelected()));
        props.put("apallisch", Boolean.toString(cbApallisch.isSelected()));
        props.put("parkinson", Boolean.toString(cbParkinson.isSelected()));
        props.put("osteo", Boolean.toString(cbOsteo.isSelected()));
        props.put("ms", Boolean.toString(cbMS.isSelected()));

        ResInfoTools.setContent(diag, props);
        diag.setText(txtBemerkung.getText().trim());
    }

    private void reloadTable() {
        if (txtSuche.getText().isEmpty()) {
            lstDiag.setModel(new DefaultListModel());
            return;
        }

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT i FROM ICD i WHERE i.icd10 LIKE :icd10 OR i.text like :text ORDER BY i.icd10 ");

        String suche = "%" + txtSuche.getText() + "%";

        query.setParameter("icd10", suche);
        query.setParameter("text", suche);

        lstDiag.setModel(SYSTools.list2dlm(query.getResultList()));
        em.close();

        lstDiag.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel jPanel1;
    private JXSearchField txtSuche;
    private JLabel lblTX;
    private JScrollPane jspDiagnosen;
    private JList lstDiag;
    private JLabel lblDiagBy;
    private JComboBox<String> cmbArzt;
    private JButton btnAddGP;
    private JComboBox<String> cmbKH;
    private JButton btnAddHospital;
    private JLabel lblSecurity;
    private JLabel lblSide;
    private JComboBox<String> cmbKoerper;
    private JComboBox<String> cmbSicherheit;
    private JPanel panel3;
    private JScrollPane jScrollPane1;
    private JTextArea txtBemerkung;
    private JPanel pnlQDVSMarkierungen;
    private JLabel lblQDVSTitle;
    private JPanel panel2;
    private JCheckBox cbTumor;
    private JCheckBox cbTetra;
    private JCheckBox cbChorea;
    private JCheckBox cbApallisch;
    private JCheckBox cbParkinson;
    private JCheckBox cbOsteo;
    private JCheckBox cbMS;
    private JTextArea lblQDVSNotiz;
    private JLabel lblInterval;
    private JPanel panel1;
    private JButton btnCancel;
    private JButton btnOK;
    // End of variables declaration//GEN-END:variables

}
