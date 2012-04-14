/*
 * OffenePflege
 * Copyright (C) 2008 Torsten Löhr
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
package op.bw.tg;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.*;
import op.OPDE;
import op.tools.*;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.JXTitledSeparator;
import tablemodels.TMBarbetrag;
import tablemodels.TMTGStat;
import tablerenderer.RNDHTML;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * @author tloehr
 */
public class FrmTG extends JFrame {
    public static final String internalClassID = "admin.residents.cash";
    public static final int TAB_TG = 0;
    public static final int TAB_STAT = 1;
    public static final long MIN_DATE = 1230764400000l; // das hier ist der 01.01.2009 um 0 uhr. Gilt für neue Belege.

    //    private ListSelectionListener lsl;
    private TableModelListener tml;
    //    private ListSelectionListener lslstat;

    private Date min;
    private Date max;
    private BigDecimal betrag;
    private JPopupMenu menu;
    private DateFormat timeDF;
    private Bewohner bewohner;
    private JXTaskPane panelTime, panelText;
    //    private JDateChooser jdcVon, jdcBis;
    private JComboBox cmbVon, cmbBis, cmbMonat;
    private JXSearchField txtBW;
    private JFrame thisComponent;
    private boolean ignoreDateComboEvent;

    /**
     * Creates new form FrmBWAttr
     */
    private void tblTGMousePressed(MouseEvent e) {

        Point p = e.getPoint();
        final int row = tblTG.rowAtPoint(p);
        final ListSelectionModel lsm = tblTG.getSelectionModel();
        boolean singleRowSelected = lsm.getMaxSelectionIndex() == lsm.getMinSelectionIndex();

        if (lsm.getMinSelectionIndex() == lsm.getMaxSelectionIndex()) {
            lsm.setSelectionInterval(row, row);
        }

        // Kontext Menü
        if (singleRowSelected && e.isPopupTrigger()) {

            SYSTools.unregisterListeners(menu);
            menu = new JPopupMenu();
            JMenuItem itemPopupPrint = new JMenuItem("Eintrag löschen");
            itemPopupPrint.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    TMBarbetrag tm = (TMBarbetrag) tblTG.getModel();
                    Barbetrag mytg = tm.getListData().get(tm.getModelRow(row));  // Rechnet die Zeile um. Berücksichtigt die Zusammenfassungszeile
                    if (JOptionPane.showConfirmDialog(thisComponent, "Sie löschen nun den Datensatz '"+mytg.getBelegtext()+"'.\nMöchten Sie das ?", "Löschen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        EntityTools.delete(mytg);
                        tm.getListData().remove(mytg);
                        tm.fireTableRowsDeleted(row, row);
                        summeNeuRechnen();
                        lblMessage.setText(timeDF.format(new Date()) + " Uhr : " + "Datensatz '"+mytg.getBelegtext()+"' gelöscht.");
                    }
                }
            });
            menu.add(itemPopupPrint);
            itemPopupPrint.setEnabled(OPDE.isAdmin() && ((TMBarbetrag) tblTG.getModel()).isReal(row));
            menu.show(e.getComponent(), (int) p.getX(), (int) p.getY());
        }
    }

    public FrmTG() {
        thisComponent = this;
        timeDF = DateFormat.getTimeInstance(DateFormat.SHORT);
        bewohner = null;
        initComponents();
        this.setTitle(SYSTools.getWindowTitle("Barbetragsverwaltung"));
        setVisible(true);
        tblTG.setModel(new DefaultTableModel());
        ignoreDateComboEvent = true;
        prepareSearchArea();
        setMinMax();
        initSearchTime();
        cmbPast.setModel(SYSCalendar.createMonthList(SYSCalendar.addField(SYSCalendar.today_date(), -2, GregorianCalendar.YEAR), SYSCalendar.today_date()));
        cmbPast.setSelectedIndex(cmbPast.getModel().getSize() - 1);
        cmbPast.setRenderer(new ListCellRenderer() {
            Format formatter = new SimpleDateFormat("MMMM yyyy");

            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                String text = formatter.format(o);
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
            }
        });
        cmbPast.setEnabled(false);
        ignoreDateComboEvent = false;

//        txtBW.requestFocus();
        jtpMain.setSelectedIndex(TAB_TG);
        jtpMain.setEnabledAt(TAB_STAT, OPDE.isAdmin());
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jToolBar1 = new JToolBar();
        btnEdit = new JToggleButton();
        btnPrint = new JButton();
        jtpMain = new JTabbedPane();
        pnlBarbetrag = new JPanel();
        scrollPane1 = new JScrollPane();
        taskSearch = new JXTaskPaneContainer();
        lblBW = new JLabel();
        lblBetrag = new JLabel();
        jPanel4 = new JPanel();
        jspData = new JScrollPane();
        tblTG = new JTable();
        jPanel5 = new JPanel();
        txtDatum = new JTextField();
        txtBelegtext = new JTextField();
        txtBetrag = new JTextField();
        pnlStat = new JPanel();
        lbl1 = new JLabel();
        jspStat = new JScrollPane();
        tblStat = new JTable();
        jSeparator2 = new JSeparator();
        jLabel5 = new JLabel();
        rbBWAlle = new JRadioButton();
        rbAktuell = new JRadioButton();
        lblSumme = new JLabel();
        cmbPast = new JComboBox();
        jLabel1 = new JLabel();
        pnlStatus = new JPanel();
        lblMessage = new JLabel();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();

        //======== jToolBar1 ========
        {
            jToolBar1.setFloatable(false);

            //---- btnEdit ----
            btnEdit.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/edit.png")));
            btnEdit.setMnemonic('b');
            btnEdit.setText("Bearbeiten");
            btnEdit.setEnabled(false);
            btnEdit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnEditActionPerformed(e);
                }
            });
            jToolBar1.add(btnEdit);

            //---- btnPrint ----
            btnPrint.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/printer.png")));
            btnPrint.setMnemonic('d');
            btnPrint.setText("Drucken");
            btnPrint.setEnabled(false);
            btnPrint.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnPrintActionPerformed(e);
                }
            });
            jToolBar1.add(btnPrint);
        }

        //======== jtpMain ========
        {
            jtpMain.setTabPlacement(SwingConstants.BOTTOM);
            jtpMain.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    jtpMainStateChanged(e);
                }
            });

            //======== pnlBarbetrag ========
            {
                pnlBarbetrag.setLayout(new FormLayout(
                    "default, $lcgap, default:grow, $lcgap, pref",
                    "fill:default, $lgap, fill:default:grow, $lgap, fill:default, $lgap, $rgap"));

                //======== scrollPane1 ========
                {
                    scrollPane1.setViewportView(taskSearch);
                }
                pnlBarbetrag.add(scrollPane1, CC.xywh(1, 3, 1, 3));

                //---- lblBW ----
                lblBW.setFont(new Font("Dialog", Font.BOLD, 18));
                lblBW.setForeground(new Color(51, 51, 255));
                lblBW.setText("Kein(e) Bewohner(in) ausgew\u00e4hlt.");
                pnlBarbetrag.add(lblBW, CC.xywh(1, 1, 3, 1));

                //---- lblBetrag ----
                lblBetrag.setFont(new Font("Dialog", Font.BOLD, 18));
                lblBetrag.setHorizontalAlignment(SwingConstants.RIGHT);
                pnlBarbetrag.add(lblBetrag, CC.xy(5, 1));

                //======== jPanel4 ========
                {
                    jPanel4.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
                    jPanel4.setLayout(new FormLayout(
                        "default:grow",
                        "fill:default:grow"));

                    //======== jspData ========
                    {
                        jspData.addComponentListener(new ComponentAdapter() {
                            @Override
                            public void componentResized(ComponentEvent e) {
                                jspDataComponentResized(e);
                            }
                        });

                        //---- tblTG ----
                        tblTG.setModel(new DefaultTableModel(
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
                        tblTG.setFont(new Font("sansserif", Font.PLAIN, 14));
                        tblTG.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mousePressed(MouseEvent e) {
                                tblTGMousePressed(e);
                            }
                        });
                        jspData.setViewportView(tblTG);
                    }
                    jPanel4.add(jspData, CC.xy(1, 1, CC.DEFAULT, CC.FILL));
                }
                pnlBarbetrag.add(jPanel4, CC.xywh(3, 3, 3, 1));

                //======== jPanel5 ========
                {
                    jPanel5.setBorder(LineBorder.createBlackLineBorder());
                    jPanel5.setLayout(new FormLayout(
                        "default:grow(0.30000000000000004), $lcgap, default:grow(0.7000000000000001), $lcgap, 30dlu:grow(0.30000000000000004)",
                        "fill:default"));

                    //---- txtDatum ----
                    txtDatum.setEnabled(false);
                    txtDatum.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            txtDatumActionPerformed(e);
                        }
                    });
                    txtDatum.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            txtDatumFocusGained(e);
                        }
                        @Override
                        public void focusLost(FocusEvent e) {
                            txtDatumFocusLost(e);
                        }
                    });
                    jPanel5.add(txtDatum, CC.xy(1, 1, CC.FILL, CC.DEFAULT));

                    //---- txtBelegtext ----
                    txtBelegtext.setEnabled(false);
                    txtBelegtext.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            txtBelegtextActionPerformed(e);
                        }
                    });
                    txtBelegtext.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            txtBelegtextFocusGained(e);
                        }
                        @Override
                        public void focusLost(FocusEvent e) {
                            txtBelegtextFocusLost(e);
                        }
                    });
                    jPanel5.add(txtBelegtext, CC.xy(3, 1, CC.FILL, CC.DEFAULT));

                    //---- txtBetrag ----
                    txtBetrag.setHorizontalAlignment(SwingConstants.RIGHT);
                    txtBetrag.setEnabled(false);
                    txtBetrag.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            txtBetragActionPerformed(e);
                        }
                    });
                    txtBetrag.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            txtBetragFocusGained(e);
                        }
                        @Override
                        public void focusLost(FocusEvent e) {
                            txtBetragFocusLost(e);
                        }
                    });
                    jPanel5.add(txtBetrag, CC.xy(5, 1, CC.FILL, CC.DEFAULT));
                }
                pnlBarbetrag.add(jPanel5, CC.xywh(3, 5, 3, 1));
            }
            jtpMain.addTab("Barbetrag", pnlBarbetrag);


            //======== pnlStat ========
            {

                //---- lbl1 ----
                lbl1.setFont(new Font("Dialog", Font.BOLD, 18));
                lbl1.setForeground(new Color(51, 51, 255));
                lbl1.setText("Summe aller Barbetr\u00e4ge:");

                //======== jspStat ========
                {
                    jspStat.addComponentListener(new ComponentAdapter() {
                        @Override
                        public void componentResized(ComponentEvent e) {
                            jspStatComponentResized(e);
                        }
                    });

                    //---- tblStat ----
                    tblStat.setModel(new DefaultTableModel(
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
                    tblStat.setFont(new Font("sansserif", Font.PLAIN, 14));
                    tblStat.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            tblStatMouseClicked(e);
                        }
                    });
                    jspStat.setViewportView(tblStat);
                }

                //---- jLabel5 ----
                jLabel5.setFont(new Font("Dialog", Font.BOLD, 14));
                jLabel5.setText("\u00dcbersicht \u00fcber Kontost\u00e4nde je BewohnerIn:");

                //---- rbBWAlle ----
                rbBWAlle.setText("Alle BW anzeigen");
                rbBWAlle.setBorder(BorderFactory.createEmptyBorder());
                rbBWAlle.setMargin(new Insets(0, 0, 0, 0));
                rbBWAlle.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        rbBWAlleActionPerformed(e);
                    }
                });

                //---- rbAktuell ----
                rbAktuell.setSelected(true);
                rbAktuell.setText("Nur die aktuellen BW anzeigen");
                rbAktuell.setBorder(BorderFactory.createEmptyBorder());
                rbAktuell.setMargin(new Insets(0, 0, 0, 0));
                rbAktuell.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        rbAktuellActionPerformed(e);
                    }
                });

                //---- lblSumme ----
                lblSumme.setFont(new Font("Dialog", Font.BOLD, 18));
                lblSumme.setHorizontalAlignment(SwingConstants.RIGHT);
                lblSumme.setText("jLabel6");

                //---- cmbPast ----
                cmbPast.setModel(new DefaultComboBoxModel(new String[] {
                    "Item 1",
                    "Item 2",
                    "Item 3",
                    "Item 4"
                }));
                cmbPast.setToolTipText("Summenanzeige f\u00fcr die Vergangenheit");
                cmbPast.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        cmbPastItemStateChanged(e);
                    }
                });

                GroupLayout pnlStatLayout = new GroupLayout(pnlStat);
                pnlStat.setLayout(pnlStatLayout);
                pnlStatLayout.setHorizontalGroup(
                    pnlStatLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, pnlStatLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(pnlStatLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(jspStat, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 833, Short.MAX_VALUE)
                                .addComponent(jSeparator2, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 833, Short.MAX_VALUE)
                                .addComponent(jLabel5, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 833, Short.MAX_VALUE)
                                .addGroup(GroupLayout.Alignment.LEADING, pnlStatLayout.createSequentialGroup()
                                    .addComponent(rbBWAlle)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(rbAktuell))
                                .addGroup(GroupLayout.Alignment.LEADING, pnlStatLayout.createSequentialGroup()
                                    .addComponent(lbl1)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(cmbPast, GroupLayout.PREFERRED_SIZE, 215, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(lblSumme, GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)))
                            .addContainerGap())
                );
                pnlStatLayout.setVerticalGroup(
                    pnlStatLayout.createParallelGroup()
                        .addGroup(pnlStatLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(pnlStatLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lbl1, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblSumme, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                                .addComponent(cmbPast, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jSeparator2, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel5)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jspStat, GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(pnlStatLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(rbBWAlle)
                                .addComponent(rbAktuell))
                            .addContainerGap())
                );
            }
            jtpMain.addTab("\u00dcbersicht", pnlStat);

        }

        //---- jLabel1 ----
        jLabel1.setFont(new Font("Dialog", Font.BOLD, 24));
        jLabel1.setText("Barbetrag");

        //======== pnlStatus ========
        {
            pnlStatus.setBorder(new BevelBorder(BevelBorder.LOWERED));
            pnlStatus.setLayout(new BoxLayout(pnlStatus, BoxLayout.X_AXIS));

            //---- lblMessage ----
            lblMessage.setHorizontalAlignment(SwingConstants.RIGHT);
            lblMessage.setText(" ");
            pnlStatus.add(lblMessage);
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(jToolBar1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 870, Short.MAX_VALUE)
                .addComponent(pnlStatus, GroupLayout.DEFAULT_SIZE, 870, Short.MAX_VALUE)
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(jtpMain, GroupLayout.DEFAULT_SIZE, 855, Short.MAX_VALUE)
                        .addComponent(jLabel1))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addComponent(jToolBar1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel1)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jtpMain, GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(pnlStatus, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );
        setSize(872, 693);
        setLocationRelativeTo(null);

        //---- bgBWFilter ----
        ButtonGroup bgBWFilter = new ButtonGroup();
        bgBWFilter.add(rbBWAlle);
        bgBWFilter.add(rbAktuell);
    }// </editor-fold>//GEN-END:initComponents

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        if (!btnEdit.isSelected()) {
            if (tblTG.getCellEditor() != null) {
                tblTG.getCellEditor().cancelCellEditing();
            }
        }
        ((TMBarbetrag) tblTG.getModel()).setEditable(btnEdit.isSelected());
    }//GEN-LAST:event_btnEditActionPerformed

    private void txtBelegtextFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBelegtextFocusLost
        if (txtBelegtext.getText().trim().isEmpty()) {
            txtBelegtext.setText("Geben Sie einen Belegtext ein.");
            lblMessage.setText(timeDF.format(new Date()) + " Uhr : " + "Sie können das Belegtextfeld nicht leer lassen.");
        }
    }//GEN-LAST:event_txtBelegtextFocusLost

    private void txtBelegtextFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBelegtextFocusGained
        ((JTextField) evt.getSource()).selectAll();
    }//GEN-LAST:event_txtBelegtextFocusGained

    private void txtBetragFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBetragFocusGained
        ((JTextField) evt.getSource()).selectAll();
    }//GEN-LAST:event_txtBetragFocusGained

    private void txtBetragFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBetragFocusLost

        betrag = SYSTools.parseCurrency(txtBetrag.getText());
        if (betrag != null) {
            if (!betrag.equals(BigDecimal.ZERO)) {
                insert();
                summeNeuRechnen();
            } else {
                lblMessage.setText(timeDF.format(new Date()) + " Uhr : " + "Beträge mit '0,00 " + SYSConst.eurosymbol + "' werden nicht angenommen.");
            }

        } else {
            lblMessage.setText(timeDF.format(new Date()) + " Uhr : " + "Bitte geben Sie Euro Beträge in der folgenden Form ein: '10,0 " + SYSConst.eurosymbol + "'");
            betrag = BigDecimal.ZERO;
        }
        txtBetrag.setText(NumberFormat.getCurrencyInstance().format(betrag));
    }//GEN-LAST:event_txtBetragFocusLost

    private void txtBetragActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBetragActionPerformed
        txtDatum.requestFocus();
    }//GEN-LAST:event_txtBetragActionPerformed

    private void txtBelegtextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBelegtextActionPerformed
        txtBetrag.requestFocus();
    }//GEN-LAST:event_txtBelegtextActionPerformed

    private void txtDatumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDatumActionPerformed
        txtBelegtext.requestFocus();
    }//GEN-LAST:event_txtDatumActionPerformed

    private void txtDatumFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDatumFocusLost
        GregorianCalendar gc;
        try {
            gc = SYSCalendar.erkenneDatum(((JTextField) evt.getSource()).getText());

        } catch (NumberFormatException ex) {
            lblMessage.setText(timeDF.format(new Date()) + " Uhr : Sie haben ein falsches Datum eingegeben. Wurde auf heute zurückgesetzt.");
            gc = SYSCalendar.today();
        }
        // Datum in der Zukunft ?
        if (SYSCalendar.sameDay(gc, SYSCalendar.today()) > 0) {
            gc = SYSCalendar.today();
            lblMessage.setText(timeDF.format(new Date()) + " Uhr : Sie haben ein Datum in der Zukunft eingegeben. Wurde auf heute zurückgesetzt.");
        }
        // Ist Datum zu weit in der Vergangenheit ?
        if (gc.getTimeInMillis() < MIN_DATE) {
            gc = SYSCalendar.today();
            lblMessage.setText(timeDF.format(new Date()) + " Uhr : Sie können keine Belege vor dem "+DateFormat.getDateInstance(DateFormat.SHORT).format(new Date(MIN_DATE))+" eingeben.");
        }
        ((JTextField) evt.getSource()).setText(SYSCalendar.printGCGermanStyle(gc));
    }//GEN-LAST:event_txtDatumFocusLost

    private void txtDatumFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDatumFocusGained
        ((JTextField) evt.getSource()).selectAll();
    }//GEN-LAST:event_txtDatumFocusGained

//    private void btnBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBottomActionPerformed
//        cmbMonat.setSelectedIndex(cmbMonat.getModel().getSize() - 1);
//    }//GEN-LAST:event_btnBottomActionPerformed
//
//    private void btnRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRightActionPerformed
//        cmbMonat.setSelectedIndex(cmbMonat.getSelectedIndex() + 1);
//    }//GEN-LAST:event_btnRightActionPerformed
//
//    private void btnLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeftActionPerformed
//        cmbMonat.setSelectedIndex(cmbMonat.getSelectedIndex() - 1);
//    }//GEN-LAST:event_btnLeftActionPerformed
//
//    private void btnTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTopActionPerformed
//        cmbMonat.setSelectedIndex(0);
//    }//GEN-LAST:event_btnTopActionPerformed
//
//    private void rbMonatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbMonatActionPerformed
//        if (initPhase) {
//            return;
//        }
//        initPhase = true;
//        cmbVon.setModel(new DefaultComboBoxModel());
//        cmbVon.setEnabled(false);
//        cmbBis.setModel(new DefaultComboBoxModel());
//        cmbBis.setEnabled(false);
//        setMinMax();
//        cmbMonat.setModel(SYSCalendar.createMonthList(min, max));
//        cmbMonat.setSelectedIndex(cmbMonat.getModel().getSize() - 1);
//        cmbMonat.setEnabled(true);
//        ListElement leMonat = (ListElement) cmbMonat.getSelectedItem();
//        this.von = (Date) leMonat.getObject();
//        this.bis = SYSCalendar.eom((Date) leMonat.getObject());
//        btnTop.setEnabled(this.min.compareTo(this.von) < 0);
//        btnLeft.setEnabled(this.min.compareTo(this.von) < 0);
//        btnRight.setEnabled(this.max.compareTo(this.bis) >= 0);
//        btnBottom.setEnabled(this.max.compareTo(this.bis) >= 0);
//        initPhase = false;
//        reloadDisplay();
//    }//GEN-LAST:event_rbMonatActionPerformed
//
//    public void dispose() {
//        SYSTools.unregisterListeners(this);
//        super.dispose();
//    }
//
//    private void cmbMonatItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbMonatItemStateChanged
//        if (initPhase) {
//            return;
//        }
//        initPhase = true; // damit die andere combobox nicht auch noch auf die Änderungen reagiert.
//        ListElement leMonat = (ListElement) cmbMonat.getSelectedItem();
//        this.von = (Date) leMonat.getObject();
//        this.bis = SYSCalendar.eom((Date) leMonat.getObject());
//        btnTop.setEnabled(this.min.compareTo(this.von) < 0);
//        btnLeft.setEnabled(this.min.compareTo(this.von) < 0);
//        btnRight.setEnabled(this.max.compareTo(this.bis) >= 0);
//        btnBottom.setEnabled(this.max.compareTo(this.bis) >= 0);
//        initPhase = false;
//        reloadDisplay();
//    }//GEN-LAST:event_cmbMonatItemStateChanged

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        TMBarbetrag tm = (TMBarbetrag) tblTG.getModel();
        printSingle(tm.getListData(), tm.getVortrag());
    }//GEN-LAST:event_btnPrintActionPerformed

    private void printSingle(List<Barbetrag> liste, BigDecimal vortrag) {

        try {
            // Create temp file.
            File temp = File.createTempFile("barbetrag", ".html");

            // Delete temp file when program exits.
            temp.deleteOnExit();

            // Write to temp file
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));
            String html = SYSTools.htmlUmlautConversion(BarbetragTools.getEinzelnAsHTML(liste, vortrag, bewohner));

            out.write(html);

            out.close();
            SYSPrint.handleFile(this, temp.getAbsolutePath(), Desktop.Action.OPEN);
        } catch (IOException e) {
            new DlgException(e);
        }

    }

    private void jspStatComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspStatComponentResized
        JScrollPane jsp = (JScrollPane) evt.getComponent();
        Dimension dim = jsp.getSize();
        // Größe der Text Spalten im DFN ändern.
        // Summe der fixen Spalten  = 175 + ein bisschen
        int textWidth = dim.width - 20;
        TableColumnModel tcm1 = tblStat.getColumnModel();
        if (tblStat.getModel().getRowCount() == 0) {
            return;
        }

        tcm1.getColumn(0).setHeaderValue("BewohnerIn");
        tcm1.getColumn(0).setPreferredWidth(textWidth / 4 * 3);
        tcm1.getColumn(1).setHeaderValue("Summe");
        tcm1.getColumn(1).setPreferredWidth(textWidth / 4);
    }//GEN-LAST:event_jspStatComponentResized

    private void rbAktuellActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbAktuellActionPerformed
        reloadDisplay();
    }//GEN-LAST:event_rbAktuellActionPerformed

    private void rbBWAlleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbBWAlleActionPerformed
        reloadDisplay();
    }//GEN-LAST:event_rbBWAlleActionPerformed

    private void tblStatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblStatMouseClicked
        if (evt.getClickCount() > 1) {
            TMTGStat tm = (TMTGStat) tblStat.getModel();
            ListSelectionModel lsm = tblStat.getSelectionModel();
            jtpMain.setSelectedIndex(TAB_TG);
//            OPDE.debug("txtBW.setText(" + tm.getBewohner(lsm.getLeadSelectionIndex()).getBWKennung()+")");
            txtBW.setText(tm.getBewohner(lsm.getLeadSelectionIndex()).getBWKennung());
            txtBW.postActionEvent();
        }
    }//GEN-LAST:event_tblStatMouseClicked

//    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
////        if (JOptionPane.showConfirmDialog(this, "Sie löschen nun den markierten Datensatz.\nMöchten Sie das ?", "Storno eines Taschengeldvorgangs", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
////            return;
////        }
////
////        EntityManager em = OPDE.createEM();
////
////        try{
////            em.getTransaction().begin();
////            Query query = em.createQuery("DELETE FROM Barbetrag t WHERE t.replacedBy = :tg ");
////            query.executeUpdate();
////
////            em.remove(currentTG);
////        }
////
////        Connection db = OPDE.getDb().db;
////        String deleteSQL = "DELETE FROM Taschengeld WHERE TGID=? OR _cancel=?";
////
////        try {
////            // Löschen
////            PreparedStatement stmtDelete = db.prepareStatement(deleteSQL);
////            stmtDelete.setLong(1, currentTGID);
////            stmtDelete.setLong(2, currentTGID);
////            stmtDelete.executeUpdate();
////
////        } catch (SQLException ex) {
////            new DlgException(ex);
////            ex.printStackTrace();
////        }
////        // Nach dem Löschen ist erstmal nix gewählt. Daher würden sonst die Knöpfe aktiv bleiben.
////        // Schalten wir sie lieber vorsichtshalber ab.
////        btnDelete.setEnabled(false);
////        btnStorno.setEnabled(false);
////        setMinMax();
////        reloadDisplay();
//    }//GEN-LAST:event_btnDeleteActionPerformed

    private void jtpMainStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jtpMainStateChanged
        if (jtpMain.getSelectedIndex() == TAB_STAT) {
            cmbPast.setEnabled(true);
            reloadDisplay();
        } else {
            cmbPast.setEnabled(false);
            if (bewohner == null) {
                btnPrint.setEnabled(false);
            } else {
                txtBW.setText(bewohner.getBWKennung());
            }
        }
    }//GEN-LAST:event_jtpMainStateChanged

    private void jspDataComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspDataComponentResized
        JScrollPane jsp = (JScrollPane) evt.getComponent();
        Dimension dim = jsp.getSize();
        // Größe der Text Spalten im DFN ändern.
        // Summe der fixen Spalten  = 175 + ein bisschen
        int textWidth = dim.width - 370;

        if (tblTG.getModel().getRowCount() == 0) {
            return;
        }
        TableColumnModel tcm1 = tblTG.getColumnModel();


//        SYSTools.packTable(tblTG, 5);

        tcm1.getColumn(0).setHeaderValue("Belegdatum");
        tcm1.getColumn(0).setPreferredWidth(100);
        tcm1.getColumn(1).setHeaderValue("Belegtext");
        tcm1.getColumn(1).setPreferredWidth(textWidth);
        tcm1.getColumn(2).setHeaderValue("Betrag");
        tcm1.getColumn(2).setPreferredWidth(120);
        tcm1.getColumn(3).setHeaderValue("Zeilensaldo");
        tcm1.getColumn(3).setPreferredWidth(120);


    }//GEN-LAST:event_jspDataComponentResized

    private void cmbPastItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbPastItemStateChanged
        if (ignoreDateComboEvent) {
            return;
        }
        updateSummenAngabe();
    }//GEN-LAST:event_cmbPastItemStateChanged

    /**
     * Setzt den Zeitraum, innerhalb dessen die Belege in der Tabelle angezeigt werden können. Nicht unbedingt werden.
     */
    private void setMinMax() {
        // Ermittelt die maximale Ausdehnung (chronologisch gesehen) aller Belege für einen bestimmten BW

        min = SYSCalendar.today_date();

        if (bewohner != null) {
            EntityManager em = OPDE.createEM();
            Query query = em.createQuery("SELECT MIN(tg.belegDatum) FROM Barbetrag tg WHERE tg.bewohner = :bewohner");
            query.setParameter("bewohner", bewohner);
            min = (Date) query.getSingleResult();
            em.close();
        }

        min = SYSCalendar.bom(min == null ? SYSCalendar.today_date() : min);
        max = SYSCalendar.eom(SYSCalendar.today_date());

    }

    private void summeNeuRechnen() {
        TMBarbetrag tm = (TMBarbetrag) tblTG.getModel();
        BigDecimal zeilensaldo = tm.getZeilenSaldo();

//        BigDecimal summe = (BigDecimal) DBHandling.getSingleValue("Taschengeld", "SUM(Betrag)", "BWKennung", currentBW);

        NumberFormat nf = NumberFormat.getCurrencyInstance();
        lblBetrag.setText(nf.format(zeilensaldo));
        if (zeilensaldo.compareTo(BigDecimal.ZERO) < 0) {
            lblBetrag.setForeground(Color.RED);
        } else {
            lblBetrag.setForeground(Color.BLACK);
        }
    }

    private void reloadDisplay() {
        lblMessage.setText("");

        // Welcher Tab ist gerade ausgewählt ?
        switch (jtpMain.getSelectedIndex()) {
            case TAB_TG: {
                setMinMax();
                initSearchTime();

                if (bewohner != null) {
                    BewohnerTools.setBWLabel(lblBW, bewohner);
                    txtDatum.setText(SYSCalendar.printGermanStyle(SYSCalendar.today_date()));
                    txtBelegtext.setText("Bitte geben Sie einen Belegtext ein.");
                    txtBetrag.setText("0,00 " + SYSConst.eurosymbol);
                    betrag = BigDecimal.ZERO;
                    txtDatum.setEnabled(true);
                    txtBelegtext.setEnabled(true);
                    txtBetrag.setEnabled(true);
                    reloadTable((Date) cmbVon.getSelectedItem(), (Date) cmbBis.getSelectedItem());
                }
                if (tblTG.getModel().getRowCount() == 0) {
                    btnPrint.setEnabled(false);
                    btnEdit.setEnabled(false);
                } else {
                    btnPrint.setEnabled(true);
                    btnEdit.setEnabled(OPDE.isAdmin());
                    if (btnEdit.isEnabled()) {
                        btnEdit.setSelected(false);
                    }
                }
                break;
            }
            case TAB_STAT: {
                btnPrint.setEnabled(false);
                btnEdit.setEnabled(false);
                updateSummenAngabe();
                reloadStatTable();
                break;
            }
        }

    }

    private void reloadStatTable() {

        tblStat.setModel(new TMTGStat(rbBWAlle.isSelected()));
        tblStat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblStat.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jspStat.dispatchEvent(new ComponentEvent(jspStat, ComponentEvent.COMPONENT_RESIZED));

        tblStat.getColumnModel().getColumn(0).setCellRenderer(new RNDHTML());
        tblStat.getColumnModel().getColumn(1).setCellRenderer(new CurrencyRenderer());
    }

    private void updateSummenAngabe() {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT SUM(tg.betrag) FROM Barbetrag tg ");
        BigDecimal summe = BigDecimal.ZERO;
        try {
            summe = (BigDecimal) query.getSingleResult();
        } catch (NoResultException nre) {
            summe = BigDecimal.ZERO;
        } catch (Exception e) {
            OPDE.fatal(e);
        }

        NumberFormat nf = NumberFormat.getCurrencyInstance();
        String summentext = nf.format(summe);

        // Ist auch eine Anzeige für die Vergangenheit gewünscht ?
        // Nur wenn ein anderer Monat als der aktuelle gewählt ist.
        if (cmbPast.getSelectedIndex() < cmbPast.getModel().getSize() - 1) {
            Query queryPast = em.createQuery("SELECT SUM(tg.betrag) FROM Barbetrag tg WHERE tg.belegDatum <= :datum");
            queryPast.setParameter("datum", SYSCalendar.eom((Date) cmbPast.getSelectedItem()));

            BigDecimal summePast = BigDecimal.ZERO;
            try {
                summePast = (BigDecimal) queryPast.getSingleResult();
            } catch (NoResultException nre) {
                summePast = BigDecimal.ZERO;
            } catch (Exception e) {
                OPDE.fatal(e);
            }

            summentext += " (" + nf.format(summePast) + ")";
        }

        em.close();

        lblSumme.setText(summentext);

        if (summe.compareTo(BigDecimal.ZERO) < 0) {
            lblSumme.setForeground(Color.RED);
        } else {
            lblSumme.setForeground(Color.BLACK);
        }

    }

    private void insert() {

        Date datum = new Date(SYSCalendar.erkenneDatum(txtDatum.getText()).getTimeInMillis());
        TMBarbetrag tm = (TMBarbetrag) tblTG.getModel();

        Barbetrag barbetrag = new Barbetrag(datum, txtBelegtext.getText().trim(), betrag, bewohner, OPDE.getLogin().getUser());
        EntityTools.persist(barbetrag);
        tm.getListData().add(barbetrag);
        Collections.sort(tm.getListData());

//        schaltet auf den Monat um, in dem der letzte Beleg eingegeben wurde.
//        Sofern die ein bestimmter Monat eingestellt war.
        if (!panelTime.isCollapsed()) {
            if (min.after(datum)) {
                // Neuer Eintrag liegt ausserhalb des bisherigen Intervals.
                min = SYSCalendar.bom(datum);
                initSearchTime();
            }
            cmbMonat.setSelectedItem(SYSCalendar.bom(datum));
        } else {
            reloadTable();
        }

//        reloadTable((Date) cmbVon.getSelectedItem(), (Date) cmbBis.getSelectedItem());
        txtBelegtext.setText("Bitte geben Sie einen Belegtext ein.");
        txtBetrag.setText("0.00 " + SYSConst.eurosymbol);
        betrag = BigDecimal.ZERO;
        txtDatum.requestFocus();
        lblMessage.setText(timeDF.format(new Date()) + " Uhr : " + "Neuen Datensatz eingefügt.");


        // Das hier markiert den zuletzt eingefügten Datensatz.
        int index = tm.getListData().indexOf(barbetrag);
        ListSelectionModel lsm = tblTG.getSelectionModel();
        lsm.setSelectionInterval(index, index);
        // Das hier rollt auf den zuletzt eingefügten Datensatz.
        tblTG.invalidate();
        Rectangle rect = tblTG.getCellRect(index, 0, true);
        tblTG.scrollRectToVisible(rect);
    }

    private void reloadTable() {
        reloadTable(null, null);
    }

    private void reloadTable(Date von, Date bis) {
        if (bewohner == null) {
            return;
        }

        tml = new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                if (e.getColumn() == 2) { // Betrag hat sich geändert
                    summeNeuRechnen();
                }
            }
        };

        tblTG.setModel(new TMBarbetrag(bewohner, von, bis, btnEdit.isSelected()));
        tblTG.getModel().addTableModelListener(tml);
        tblTG.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblTG.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jspData.dispatchEvent(new ComponentEvent(jspData, ComponentEvent.COMPONENT_RESIZED));

        tblTG.getColumnModel().getColumn(2).setCellRenderer(new CurrencyRenderer());
        tblTG.getColumnModel().getColumn(3).setCellRenderer(new CurrencyRenderer());

        tblTG.getColumnModel().getColumn(0).setCellEditor(new CEDefault());
        tblTG.getColumnModel().getColumn(1).setCellEditor(new CEDefault());
        tblTG.getColumnModel().getColumn(2).setCellEditor(new CEDefault());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JToolBar jToolBar1;
    private JToggleButton btnEdit;
    private JButton btnPrint;
    private JTabbedPane jtpMain;
    private JPanel pnlBarbetrag;
    private JScrollPane scrollPane1;
    private JXTaskPaneContainer taskSearch;
    private JLabel lblBW;
    private JLabel lblBetrag;
    private JPanel jPanel4;
    private JScrollPane jspData;
    private JTable tblTG;
    private JPanel jPanel5;
    private JTextField txtDatum;
    private JTextField txtBelegtext;
    private JTextField txtBetrag;
    private JPanel pnlStat;
    private JLabel lbl1;
    private JScrollPane jspStat;
    private JTable tblStat;
    private JSeparator jSeparator2;
    private JLabel jLabel5;
    private JRadioButton rbBWAlle;
    private JRadioButton rbAktuell;
    private JLabel lblSumme;
    private JComboBox cmbPast;
    private JLabel jLabel1;
    private JPanel pnlStatus;
    private JLabel lblMessage;
    // End of variables declaration//GEN-END:variables


    private void prepareSearchArea() {
        addBySearchBW();
        addByTime();
//        addSpecials();
    }


    private void addBySearchBW() {
        if (panelText != null) {
            return;
        }
        panelText = new JXTaskPane("nach Bewohnername");
        txtBW = new JXSearchField("Bewohnername");
        txtBW.setInstantSearchDelay(2000); // 2 Sekunden bevor der Caret Update zieht
        txtBW.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (txtBW.getText().trim().isEmpty()) {
                    return;
                }
                Bewohner prevBW = bewohner;
//                bewohner = BewohnerTools.findeBW(thisComponent, txtBW.getText());
                if (bewohner == null) {
                    JOptionPane.showMessageDialog(thisComponent, "Keine(n) passende(n) Bewohner(in) gefunden.", "Hinweis", JOptionPane.INFORMATION_MESSAGE);
                    //tblTG.setModel(new DefaultTableModel());
                    bewohner = prevBW;
                } else {
                    reloadDisplay();
                }
            }
        });

        panelText.add(txtBW);
        panelText.setCollapsed(false);
        panelText.setSpecial(true);
        panelText.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/edit_group.png")));
        taskSearch.add((JPanel) panelText);

    }

    private void initSearchTime() {

        ignoreDateComboEvent = true;

        cmbVon.setModel(SYSCalendar.createMonthList(min, max));
        cmbVon.setSelectedIndex(cmbVon.getModel().getSize() - 1);

        cmbBis.setModel(SYSCalendar.createMonthList(min, max));
        cmbBis.setSelectedIndex(cmbBis.getModel().getSize() - 1);

        cmbMonat.setModel(SYSCalendar.createMonthList(min, max));
        cmbMonat.setSelectedIndex(cmbMonat.getModel().getSize() - 1);

        ignoreDateComboEvent = false;
    }

    private void addByTime() {
        panelTime = new JXTaskPane("nach Zeitraum");
        cmbVon = new JComboBox();

        cmbVon.setRenderer(new ListCellRenderer() {
            Format formatter = new SimpleDateFormat("MMMM yyyy");

            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                String text = formatter.format(o);
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
            }
        });

        cmbVon.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (ignoreDateComboEvent) {
                    return;
                }
                if (cmbVon.getSelectedIndex() > cmbBis.getSelectedIndex()) {
                    ignoreDateComboEvent = true;
                    cmbBis.setSelectedIndex(cmbVon.getSelectedIndex());
                    ignoreDateComboEvent = false;
                }
                reloadTable((Date) cmbVon.getSelectedItem(), (Date) cmbBis.getSelectedItem());
            }
        });

        panelTime.add(new JXTitledSeparator("Von"));
        panelTime.add(cmbVon);


        cmbBis = new JComboBox();
        cmbBis.setRenderer(new ListCellRenderer() {
            Format formatter = new SimpleDateFormat("MMMM yyyy");

            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                String text = formatter.format(o);
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
            }
        });

        cmbBis.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (ignoreDateComboEvent) {
                    return;
                }
                if (cmbVon.getSelectedIndex() > cmbBis.getSelectedIndex()) {
                    ignoreDateComboEvent = true;
                    cmbVon.setSelectedIndex(cmbBis.getSelectedIndex());
                    ignoreDateComboEvent = false;
                }
                reloadTable((Date) cmbVon.getSelectedItem(), (Date) cmbBis.getSelectedItem());
            }
        });

//        panelTime.add(new JLabel(" "));
        panelTime.add(new JXTitledSeparator("Bis"));
        panelTime.add(cmbBis);

        cmbMonat = new JComboBox();
        cmbMonat.setRenderer(new ListCellRenderer() {
            Format formatter = new SimpleDateFormat("MMMM yyyy");

            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
//                OPDE.debug(o.toString());
                String text = formatter.format(o);
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
            }
        });

        cmbMonat.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                ignoreDateComboEvent = true;
                cmbVon.setSelectedItem(cmbMonat.getSelectedItem());
                cmbBis.setSelectedItem(cmbMonat.getSelectedItem());
                ignoreDateComboEvent = false;
                reloadTable((Date) cmbVon.getSelectedItem(), (Date) cmbBis.getSelectedItem());
            }
        });

        panelTime.add(new JLabel(" "));
        panelTime.add(new JXTitledSeparator("Bestimmter Monat"));
        panelTime.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/date.png")));
        panelTime.add(cmbMonat);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        JButton homeButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_start.png")));
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (cmbMonat.getSelectedIndex() > 0) {
                    cmbMonat.setSelectedIndex(0);
                }
            }
        });
//        homeButton.setBorder(new EmptyBorder(0,0,0,0));
        JButton backButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/bw/back.png")));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (cmbMonat.getSelectedIndex() > 0) {
                    cmbMonat.setSelectedIndex(cmbMonat.getSelectedIndex() - 1);
                }
            }
        });
//        backButton.setBorder(new EmptyBorder(0,0,0,0));
        JButton fwdButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/bw/forward.png")));
        fwdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (cmbMonat.getSelectedIndex() < cmbMonat.getModel().getSize() - 1) {
                    cmbMonat.setSelectedIndex(cmbMonat.getSelectedIndex() + 1);
                }
            }
        });
//        fwdButton.setBorder(new EmptyBorder(0,0,0,0));
        JButton endButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_end.png")));
        endButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (cmbMonat.getSelectedIndex() < cmbMonat.getModel().getSize() - 1) {
                    cmbMonat.setSelectedIndex(cmbMonat.getModel().getSize() - 1);
                }
            }
        });
//        endButton.setBorder(new EmptyBorder(0,0,0,0));

        buttonPanel.add(homeButton);
        buttonPanel.add(backButton);
        buttonPanel.add(fwdButton);
        buttonPanel.add(endButton);
        panelTime.add(buttonPanel);

        taskSearch.add((JPanel) panelTime);

        panelTime.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("collapsed")) {
                    if (panelTime.isCollapsed()) {
                        reloadTable();
                    } else {
                        reloadTable((Date) cmbVon.getSelectedItem(), (Date) cmbBis.getSelectedItem());
                    }

                }
            }
        });
    }

    private class CurrencyRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable jTable, Object value, boolean b, boolean b1, int i, int i1) {
            String text = value.toString();
            if (value instanceof BigDecimal) {
                BigDecimal bd = (BigDecimal) value;
                if (bd.compareTo(BigDecimal.ZERO) < 0) {
                    setForeground(Color.RED);
                } else {
                    setForeground(Color.BLACK);
                }

                NumberFormat nf = NumberFormat.getCurrencyInstance();
                text = nf.format(value);
                setHorizontalAlignment(JLabel.RIGHT);
            }
            return super.getTableCellRendererComponent(jTable, text, b, b1, i, i1);    //To change body of overridden methods use File | Settings | File Templates.
        }
    }

}
