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
package op.controlling;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
import entity.nursingprocess.Intervention;
import entity.qms.Qmsplan;
import entity.qms.Qmssched;
import op.OPDE;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.HorizontalLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author root
 */
public class DlgQMSPlan extends MyJDialog {
    public static final String internalClassID = "opde.controlling.qms.dlgqmsplan";
    private Qmsplan qmsplan;
    private Closure actionBlock;
    private JPopupMenu menu;
    private PnlCommonTags pnlCommonTags;
    private ArrayList<Qmssched> listSchedules2Remove = new ArrayList();

    /**
     * Creates new form DlgNursingProcess
     */
    public DlgQMSPlan(Qmsplan qmsplan, Closure actionBlock) {
        super(false);
        this.qmsplan = qmsplan;
        this.actionBlock = actionBlock;
        initComponents();
        initDialog();
        setVisible(true);
    }

    private void initDialog() {

        pnlCommonTags = new PnlCommonTags(new HashSet<>(qmsplan.getCommontags()));
        pnlLeft.add(pnlCommonTags, CC.xy(3, 5));

        lblTitle.setText(OPDE.lang.getString("misc.msg.title"));
        lblDescription.setText(OPDE.lang.getString("misc.msg.description"));
        lblTags.setText(OPDE.lang.getString("misc.msg.tags"));

        txtTitle.setText(qmsplan.getTitle());
        txtDescription.setText(qmsplan.getDescription());

        reloadInterventions();

//        cmbFlags.setModel(new DefaultComboBoxModel(NursingProcessTools.FLAGS));
//        cmbFlags.setSelectedIndex(nursingProcess.getFlag());


    }

    @Override
    public void dispose() {
        super.dispose();

        if (qmsplan == null) {
            actionBlock.execute(null);
        } else {
            actionBlock.execute(new Pair<Qmsplan, ArrayList<Qmssched>>(qmsplan, listSchedules2Remove));
        }
    }

    private void reloadInterventions() {
//        tblPlanung.setModel(new TMPlan(nursingProcess));
//        tblPlanung.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//        tblPlanung.getColumnModel().getColumn(TMPlan.COL_TXT).setCellRenderer(new RNDHTML());
//        tblPlanung.getColumnModel().getColumn(TMPlan.COL_TXT).setHeaderValue(OPDE.lang.getString("nursingrecords.nursingprocess.interventions"));
    }


    private void btnAddQMSActionPerformed(ActionEvent e) {
                final JidePopup popup = new JidePopup();
                PnlQMSSchedule pnlQMSSchedule = new PnlQMSSchedule(new Qmssched(), new Closure() {
                    @Override
                    public void execute(Object o) {
                        popup.hidePopup();
//                        if (o != null) {
//                            for (Object obj : (Object[]) o) {
//                                Intervention intervention = (Intervention) obj;
//                                nursingProcess.getInterventionSchedule().add(new InterventionSchedule(nursingProcess, intervention));
//                            }
//                            reloadInterventions();
//                        }
                    }
                });

                popup.setMovable(false);
                popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));

                popup.setOwner(btnAddQMS);
                popup.removeExcludedComponent(btnAddQMS);
                popup.getContentPane().add(pnlQMSSchedule);
                popup.setDefaultFocusComponent(pnlQMSSchedule);
                GUITools.showPopup(popup, SwingConstants.NORTH_WEST);
    }

    private void txtTitleFocusGained(FocusEvent e) {
        ((JTextComponent) e.getSource()).selectAll();
    }

    private void txtDescriptionFocusGained(FocusEvent e) {
        ((JTextComponent) e.getSource()).selectAll();
    }

    /**
     * Reasons why you couldn't save it
     *
     * @return
     */
    private boolean saveOK() {

//        if (txtStichwort.getText().trim().isEmpty()) {
//            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("nursingrecords.nursingprocess.dlgplanung.stichwortxx"), DisplayMessage.WARNING));
//            return false;
//        }
//
//        if (jdcKontrolle.getDate() == null) {
//            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("nursingrecords.nursingprocess.dlgplanung.datumxx"), DisplayMessage.WARNING));
//            return false;
//        }
//
//        if (cmbKategorie.getSelectedItem() == null) {
//            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("nursingrecords.nursingprocess.dlgplanung.kategoriexx"), DisplayMessage.WARNING));
//            return false;
//        }
//
//        if (nursingProcess.getInterventionSchedule().isEmpty()) {
//            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("nursingrecords.nursingprocess.dlgplanung.schedulexx"), DisplayMessage.WARNING));
//            return false;
//        }
//
//        if (txtSituation.getText().isEmpty()) {
//            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("nursingrecords.nursingprocess.dlgplanung.situationxx"), DisplayMessage.WARNING));
//            return false;
//        }
//
//        if (txtZiele.getText().isEmpty()) {
//            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("nursingrecords.nursingprocess.dlgplanung.goalxx"), DisplayMessage.WARNING));
//            return false;
//        }

        return true;

    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the PrinterForm Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        pnlLeft = new JPanel();
        lblTitle = new JLabel();
        txtTitle = new JTextField();
        jScrollPane3 = new JScrollPane();
        txtDescription = new JTextArea();
        lblDescription = new JLabel();
        lblTags = new JLabel();
        pnlRight = new JPanel();
        jspPlanung = new JScrollPane();
        tblPlanung = new JTable();
        panel3 = new JPanel();
        btnAddQMS = new JButton();
        panel1 = new JPanel();
        btnCancel = new JButton();
        btnSave = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "14dlu, $lcgap, 280dlu:grow, $ugap, pref, $lcgap, 14dlu",
            "fill:14dlu, $lgap, default, $rgap, pref, $lgap, 14dlu"));

        //======== pnlLeft ========
        {
            pnlLeft.setLayout(new FormLayout(
                "default, $lcgap, default:grow",
                "fill:default, $rgap, fill:default:grow, $lgap, fill:default"));

            //---- lblTitle ----
            lblTitle.setFont(new Font("Arial", Font.PLAIN, 14));
            lblTitle.setText("Stichwort");
            pnlLeft.add(lblTitle, CC.xy(1, 1, CC.DEFAULT, CC.TOP));

            //---- txtTitle ----
            txtTitle.setFont(new Font("Arial", Font.BOLD, 20));
            txtTitle.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    txtTitleFocusGained(e);
                }
            });
            pnlLeft.add(txtTitle, CC.xy(3, 1));

            //======== jScrollPane3 ========
            {

                //---- txtDescription ----
                txtDescription.setColumns(20);
                txtDescription.setLineWrap(true);
                txtDescription.setRows(5);
                txtDescription.setWrapStyleWord(true);
                txtDescription.setFont(new Font("Arial", Font.PLAIN, 14));
                txtDescription.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        txtDescriptionFocusGained(e);
                    }
                });
                jScrollPane3.setViewportView(txtDescription);
            }
            pnlLeft.add(jScrollPane3, CC.xy(3, 3));

            //---- lblDescription ----
            lblDescription.setFont(new Font("Arial", Font.PLAIN, 14));
            lblDescription.setText("Situation");
            pnlLeft.add(lblDescription, CC.xy(1, 3, CC.DEFAULT, CC.TOP));

            //---- lblTags ----
            lblTags.setFont(new Font("Arial", Font.PLAIN, 14));
            lblTags.setText("Markierung");
            pnlLeft.add(lblTags, CC.xy(1, 5));
        }
        contentPane.add(pnlLeft, CC.xy(3, 3, CC.DEFAULT, CC.FILL));

        //======== pnlRight ========
        {
            pnlRight.setLayout(new FormLayout(
                "default:grow",
                "default, $lgap, default"));

            //======== jspPlanung ========
            {
                jspPlanung.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        jspPlanungComponentResized(e);
                    }
                });

                //---- tblPlanung ----
                tblPlanung.setModel(new DefaultTableModel(
                    new Object[][] {
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null},
                    },
                    new String[] {
                        "Title 1", "Title 2", "Title 3", "Title 4"
                    }
                ));
                tblPlanung.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                tblPlanung.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        tblPlanungMousePressed(e);
                    }
                });
                jspPlanung.setViewportView(tblPlanung);
            }
            pnlRight.add(jspPlanung, CC.xy(1, 1));

            //======== panel3 ========
            {
                panel3.setLayout(new BoxLayout(panel3, BoxLayout.X_AXIS));

                //---- btnAddQMS ----
                btnAddQMS.setText(null);
                btnAddQMS.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
                btnAddQMS.setContentAreaFilled(false);
                btnAddQMS.setBorderPainted(false);
                btnAddQMS.setBorder(null);
                btnAddQMS.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add-pressed.png")));
                btnAddQMS.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnAddQMSActionPerformed(e);
                    }
                });
                panel3.add(btnAddQMS);
            }
            pnlRight.add(panel3, CC.xy(1, 3));
        }
        contentPane.add(pnlRight, CC.xy(5, 3));

        //======== panel1 ========
        {
            panel1.setLayout(new HorizontalLayout(5));

            //---- btnCancel ----
            btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
            btnCancel.setText(null);
            btnCancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnCancelActionPerformed(e);
                }
            });
            panel1.add(btnCancel);

            //---- btnSave ----
            btnSave.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnSave.setText(null);
            btnSave.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnSaveActionPerformed(e);
                }
            });
            panel1.add(btnSave);
        }
        contentPane.add(panel1, CC.xy(5, 5, CC.RIGHT, CC.DEFAULT));
        setSize(1145, 570);
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents


    private void btnCancelActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        qmsplan = null;
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void jspPlanungComponentResized(ComponentEvent evt) {//GEN-FIRST:event_jspPlanungComponentResized
        JScrollPane jsp = (JScrollPane) evt.getComponent();
        if (tblPlanung.getRowCount() <= 0) {
            return;
        }
        Dimension dim = jsp.getSize();
        int textWidth = dim.width - 25;
        TableColumnModel tcm1 = tblPlanung.getColumnModel();
        tcm1.getColumn(0).setPreferredWidth(textWidth);
        tcm1.getColumn(0).setHeaderValue(OPDE.lang.getString("nursingrecords.nursingprocess.interventions"));
    }//GEN-LAST:event_jspPlanungComponentResized

    private void btnSaveActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (saveOK()) {
            save();
            dispose();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void tblPlanungMousePressed(MouseEvent evt) {//GEN-FIRST:event_tblPlanungMousePressed

    }//GEN-LAST:event_tblPlanungMousePressed


    private void save() {
        qmsplan.setTitle(SYSTools.tidy(txtTitle.getText()));
        qmsplan.setDescription(SYSTools.tidy(txtDescription.getText()));
        qmsplan.getCommontags().clear();
        qmsplan.getCommontags().addAll(pnlCommonTags.getListSelectedTags());
    }

    //GEN-BEGIN:variables
    private JPanel pnlLeft;
    private JLabel lblTitle;
    private JTextField txtTitle;
    private JScrollPane jScrollPane3;
    private JTextArea txtDescription;
    private JLabel lblDescription;
    private JLabel lblTags;
    private JPanel pnlRight;
    private JScrollPane jspPlanung;
    private JTable tblPlanung;
    private JPanel panel3;
    private JButton btnAddQMS;
    private JPanel panel1;
    private JButton btnCancel;
    private JButton btnSave;
    //GEN-END:variables
}
