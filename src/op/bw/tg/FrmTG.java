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

import entity.BarbetragTools;
import entity.BewohnerTools;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import op.OCSec;
import op.OPDE;
import op.tools.ListElement;
import op.tools.CEDefault;
import op.tools.SYSCalendar;
import op.tools.DBHandling;
import op.tools.DlgException;
import op.tools.SYSPrint;
import op.tools.SYSTools;
import op.tools.StandardCurrencyRenderer;
import tablerenderer.RNDStandard;
import op.tools.SYSConst;

/**
 *
 * @author  tloehr
 */
public class FrmTG extends JFrame {

    public static final int TAB_TG = 0;
    public static final int TAB_STAT = 1;
    private String currentBW;
    private ListSelectionListener lsl;
    private TableModelListener tml;
    private ListSelectionListener lslstat;
    private boolean initPhase;
    private Date von;
    private Date bis;
    private Date min;
    private Date max;
    private long currentTGID;
    private double betrag;
    private String classname;
    private OCSec ocs;

    /** Creates new form FrmBWAttr */
    public FrmTG() {
        initPhase = true;
//        deleteAllowed = false;
//        updateAllowed = false;
        currentBW = "";
        this.classname = this.getClass().getName();
        ocs = OPDE.getOCSec();
        initComponents();
        this.setTitle(SYSTools.getWindowTitle("Barbetragsverwaltung"));
        setVisible(true);
        tblTG.setModel(new DefaultTableModel());
        rbAlle.setEnabled(false);
        rbZeitraum.setEnabled(false);
        rbMonat.setEnabled(false);
        cmbVon.setModel(new DefaultComboBoxModel());
        cmbVon.setEnabled(false);
        cmbBis.setModel(new DefaultComboBoxModel());
        cmbBis.setEnabled(false);
        cmbMonat.setModel(new DefaultComboBoxModel());
        cmbMonat.setEnabled(false);
        cmbPast.setModel(SYSCalendar.createMonthList(SYSCalendar.addField(SYSCalendar.today_date(), -2, GregorianCalendar.YEAR), SYSCalendar.today_date()));
        cmbPast.setSelectedIndex(cmbPast.getModel().getSize() - 1);
        cmbPast.setEnabled(false);
        btnTop.setEnabled(false);
        btnLeft.setEnabled(false);
        btnRight.setEnabled(false);
        btnBottom.setEnabled(false);

        txtBW.requestFocus();
        jtpMain.setSelectedIndex(TAB_TG);
        jtpMain.setEnabledAt(TAB_STAT, OPDE.isAdmin());
        //applySecurity();

        initPhase = false;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgFilter = new javax.swing.ButtonGroup();
        bgBWFilter = new javax.swing.ButtonGroup();
        jToolBar1 = new javax.swing.JToolBar();
        btnEdit = new javax.swing.JToggleButton();
        btnStorno = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        jtpMain = new javax.swing.JTabbedPane();
        pnlBarbetrag = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        rbAlle = new javax.swing.JRadioButton();
        txtBW = new javax.swing.JTextField();
        btnFind = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        cmbVon = new javax.swing.JComboBox();
        cmbBis = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        rbZeitraum = new javax.swing.JRadioButton();
        rbMonat = new javax.swing.JRadioButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        cmbMonat = new javax.swing.JComboBox();
        btnTop = new javax.swing.JButton();
        btnLeft = new javax.swing.JButton();
        btnRight = new javax.swing.JButton();
        btnBottom = new javax.swing.JButton();
        lblBW = new javax.swing.JLabel();
        lblBetrag = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jspData = new javax.swing.JScrollPane();
        tblTG = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        txtDatum = new javax.swing.JTextField();
        txtBelegtext = new javax.swing.JTextField();
        txtBetrag = new javax.swing.JTextField();
        pnlStat = new javax.swing.JPanel();
        lbl1 = new javax.swing.JLabel();
        jspStat = new javax.swing.JScrollPane();
        tblStat = new javax.swing.JTable();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        rbBWAlle = new javax.swing.JRadioButton();
        rbAktuell = new javax.swing.JRadioButton();
        lblSumme = new javax.swing.JLabel();
        cmbPast = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        pnlStatus = new javax.swing.JPanel();
        lblMessage = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jToolBar1.setFloatable(false);

        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/edit.png"))); // NOI18N
        btnEdit.setMnemonic('b');
        btnEdit.setText("Bearbeiten");
        btnEdit.setEnabled(false);
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });
        jToolBar1.add(btnEdit);

        btnStorno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/edit_remove.png"))); // NOI18N
        btnStorno.setMnemonic('s');
        btnStorno.setText("Storno");
        btnStorno.setEnabled(false);
        btnStorno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStornoActionPerformed(evt);
            }
        });
        jToolBar1.add(btnStorno);

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/editdelete.png"))); // NOI18N
        btnDelete.setMnemonic('l');
        btnDelete.setText("Löschen");
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        jToolBar1.add(btnDelete);

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/printer.png"))); // NOI18N
        btnPrint.setMnemonic('d');
        btnPrint.setText("Drucken");
        btnPrint.setEnabled(false);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrint);

        jtpMain.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        jtpMain.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jtpMainStateChanged(evt);
            }
        });

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        bgFilter.add(rbAlle);
        rbAlle.setMnemonic('a');
        rbAlle.setSelected(true);
        rbAlle.setText("Alle Belege anzeigen");
        rbAlle.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbAlle.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbAlle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbAlleActionPerformed(evt);
            }
        });

        txtBW.setToolTipText("Sie können hier Teile des Nachnamens oder die Kennung eingeben.");
        txtBW.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBWActionPerformed(evt);
            }
        });
        txtBW.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBWFocusGained(evt);
            }
        });

        btnFind.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/search.png"))); // NOI18N
        btnFind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFindActionPerformed(evt);
            }
        });

        jLabel2.setText("Bewohner(in) suchen");

        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel3.setText("Von:");

        cmbVon.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbVon.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbVonItemStateChanged(evt);
            }
        });

        cmbBis.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbBis.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbBisItemStateChanged(evt);
            }
        });

        jLabel4.setText("Bis:");

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cmbVon, 0, 206, Short.MAX_VALUE)
                    .add(cmbBis, 0, 206, Short.MAX_VALUE)
                    .add(jLabel3)
                    .add(jLabel4))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cmbVon, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cmbBis, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bgFilter.add(rbZeitraum);
        rbZeitraum.setMnemonic('z');
        rbZeitraum.setText("Zeitraum einschränken");
        rbZeitraum.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbZeitraum.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbZeitraum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbZeitraumActionPerformed(evt);
            }
        });

        bgFilter.add(rbMonat);
        rbMonat.setMnemonic('m');
        rbMonat.setText("Monat einschränken");
        rbMonat.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbMonat.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbMonat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbMonatActionPerformed(evt);
            }
        });

        jPanel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel6.setText("Monat:");

        cmbMonat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbMonat.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbMonatItemStateChanged(evt);
            }
        });

        btnTop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/2leftarrow.png"))); // NOI18N
        btnTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTopActionPerformed(evt);
            }
        });

        btnLeft.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/1leftarrow.png"))); // NOI18N
        btnLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLeftActionPerformed(evt);
            }
        });

        btnRight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/1rightarrow.png"))); // NOI18N
        btnRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRightActionPerformed(evt);
            }
        });

        btnBottom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/2rightarrow.png"))); // NOI18N
        btnBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBottomActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jLabel6)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(btnTop, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnLeft, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnRight, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnBottom, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 47, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(cmbMonat, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(new java.awt.Component[] {btnBottom, btnLeft, btnRight, btnTop}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel6)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cmbMonat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(btnTop)
                    .add(btnLeft)
                    .add(btnRight)
                    .add(btnBottom))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(txtBW, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 157, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnFind, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jLabel2)
                    .add(rbAlle)
                    .add(rbZeitraum)
                    .add(rbMonat)
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtBW, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnFind))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rbAlle)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rbZeitraum)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rbMonat)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblBW.setFont(new java.awt.Font("Dialog", 1, 18));
        lblBW.setForeground(new java.awt.Color(51, 51, 255));
        lblBW.setText("Kein(e) Bewohner(in) ausgewählt.");

        lblBetrag.setFont(new java.awt.Font("Dialog", 1, 18));
        lblBetrag.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        jPanel4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jspData.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jspDataComponentResized(evt);
            }
        });

        tblTG.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jspData.setViewportView(tblTG);

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jspData, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jspData, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel5.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        txtDatum.setEnabled(false);
        txtDatum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDatumActionPerformed(evt);
            }
        });
        txtDatum.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDatumFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDatumFocusLost(evt);
            }
        });

        txtBelegtext.setEnabled(false);
        txtBelegtext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBelegtextActionPerformed(evt);
            }
        });
        txtBelegtext.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBelegtextFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBelegtextFocusLost(evt);
            }
        });

        txtBetrag.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBetrag.setEnabled(false);
        txtBetrag.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBetragActionPerformed(evt);
            }
        });
        txtBetrag.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBetragFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBetragFocusLost(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(txtDatum, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtBelegtext, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtBetrag, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 84, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtDatum, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtBetrag, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtBelegtext, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout pnlBarbetragLayout = new org.jdesktop.layout.GroupLayout(pnlBarbetrag);
        pnlBarbetrag.setLayout(pnlBarbetragLayout);
        pnlBarbetragLayout.setHorizontalGroup(
            pnlBarbetragLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlBarbetragLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlBarbetragLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlBarbetragLayout.createSequentialGroup()
                        .add(lblBW, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 704, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lblBetrag, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlBarbetragLayout.createSequentialGroup()
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlBarbetragLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        pnlBarbetragLayout.setVerticalGroup(
            pnlBarbetragLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlBarbetragLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlBarbetragLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblBW, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblBetrag, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlBarbetragLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlBarbetragLayout.createSequentialGroup()
                        .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jtpMain.addTab("Barbetrag", pnlBarbetrag);

        lbl1.setFont(new java.awt.Font("Dialog", 1, 18));
        lbl1.setForeground(new java.awt.Color(51, 51, 255));
        lbl1.setText("Summe aller Barbeträge:");

        jspStat.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jspStatComponentResized(evt);
            }
        });

        tblStat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblStat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblStatMouseClicked(evt);
            }
        });
        jspStat.setViewportView(tblStat);

        jLabel5.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel5.setText("Übersicht über Kontostände je BewohnerIn:");

        bgBWFilter.add(rbBWAlle);
        rbBWAlle.setText("Alle BW anzeigen");
        rbBWAlle.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbBWAlle.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbBWAlle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbBWAlleActionPerformed(evt);
            }
        });

        bgBWFilter.add(rbAktuell);
        rbAktuell.setSelected(true);
        rbAktuell.setText("Nur die aktuellen BW anzeigen");
        rbAktuell.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbAktuell.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rbAktuell.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbAktuellActionPerformed(evt);
            }
        });

        lblSumme.setFont(new java.awt.Font("Dialog", 1, 18));
        lblSumme.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSumme.setText("jLabel6");

        cmbPast.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbPast.setToolTipText("Summenanzeige für die Vergangenheit");
        cmbPast.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbPastItemStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlStatLayout = new org.jdesktop.layout.GroupLayout(pnlStat);
        pnlStat.setLayout(pnlStatLayout);
        pnlStatLayout.setHorizontalGroup(
            pnlStatLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlStatLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlStatLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jspStat, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 778, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jSeparator2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 778, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 778, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlStatLayout.createSequentialGroup()
                        .add(rbBWAlle)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(rbAktuell))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlStatLayout.createSequentialGroup()
                        .add(lbl1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(cmbPast, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 215, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lblSumme, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlStatLayout.setVerticalGroup(
            pnlStatLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlStatLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlStatLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbl1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblSumme, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cmbPast, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jspStat, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlStatLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(rbBWAlle)
                    .add(rbAktuell))
                .addContainerGap())
        );

        jtpMain.addTab("Übersicht", pnlStat);

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 24));
        jLabel1.setText("Barbetrag");

        pnlStatus.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        lblMessage.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        org.jdesktop.layout.GroupLayout pnlStatusLayout = new org.jdesktop.layout.GroupLayout(pnlStatus);
        pnlStatus.setLayout(pnlStatusLayout);
        pnlStatusLayout.setHorizontalGroup(
            pnlStatusLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lblMessage, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 868, Short.MAX_VALUE)
        );
        pnlStatusLayout.setVerticalGroup(
            pnlStatusLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, lblMessage, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 15, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 872, Short.MAX_VALUE)
            .add(pnlStatus, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jtpMain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 839, Short.MAX_VALUE)
                    .add(jLabel1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jtpMain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-872)/2, (screenSize.height-693)/2, 872, 693);
    }// </editor-fold>//GEN-END:initComponents

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        if (!btnEdit.isSelected()) {
            if (tblTG.getCellEditor() != null) {
                tblTG.getCellEditor().cancelCellEditing();
            }
        }
    }//GEN-LAST:event_btnEditActionPerformed

    private void txtBelegtextFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBelegtextFocusLost
        if (((JTextField) evt.getSource()).getText().trim().equals("")) {
            ((JTextField) evt.getSource()).setText("Geben Sie einen Belegtext ein.");
            lblMessage.setText(SYSCalendar.toGermanTime(new Time(SYSCalendar.heute().getTimeInMillis())) + " Uhr : " + "Sie können das Belegtextfeld nicht leer lassen.");
        }
    }//GEN-LAST:event_txtBelegtextFocusLost

    private void txtBelegtextFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBelegtextFocusGained
        ((JTextField) evt.getSource()).selectAll();
    }//GEN-LAST:event_txtBelegtextFocusGained

    private void txtBetragFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBetragFocusGained
        ((JTextField) evt.getSource()).selectAll();
    }//GEN-LAST:event_txtBetragFocusGained

    private void txtBetragFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBetragFocusLost
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        String test = txtBetrag.getText();
        test = test.replace(".", ",");
        Number num = null;
        try {
            num = nf.parse(test);
        } catch (ParseException ex) {
            try {
                String test1 = test + " " + SYSConst.eurosymbol;
                num = nf.parse(test1);
            } catch (ParseException ex1) {
                try {
                    test += " " + SYSConst.eurosymbol;
                    num = nf.parse(test);
                } catch (ParseException ex2) {
                    lblMessage.setText(SYSCalendar.toGermanTime(new Time(SYSCalendar.heute().getTimeInMillis())) + " Uhr : " + "Bitte geben Sie Euro Beträge in der folgenden Form ein: '10,0 " + SYSConst.eurosymbol + "'");
                }
            }
        }
        if (num != null) {
            this.betrag = num.doubleValue();
            if (this.betrag != 0.0d) {
                insert();
                summeNeuRechnen();
                setMinMax();
            } else {
                lblMessage.setText(SYSCalendar.toGermanTime(new Time(SYSCalendar.heute().getTimeInMillis())) + " Uhr : " + "Beträge mit '0,00 " + SYSConst.eurosymbol + "' werden nicht angenommen.");
            }

        } else {
            this.betrag = 0.0d;
        }
        txtBetrag.setText(nf.format(this.betrag));
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
            lblMessage.setText(SYSCalendar.toGermanTime(new Time(SYSCalendar.heute().getTimeInMillis())) + " Uhr : Sie haben ein falsches Datum eingegeben. Wurde auf heute zurückgesetzt.");
            gc = SYSCalendar.today();
        }
        // Datum in der Zukunft ?
        if (SYSCalendar.sameDay(gc, SYSCalendar.today()) > 0) {
            gc = SYSCalendar.today();
            lblMessage.setText(SYSCalendar.toGermanTime(new Time(SYSCalendar.heute().getTimeInMillis())) + " Uhr : Sie haben ein Datum in der Zukunft eingegeben. Wurde auf heute zurückgesetzt.");
        }
        ((JTextField) evt.getSource()).setText(SYSCalendar.printGCGermanStyle(gc));
    }//GEN-LAST:event_txtDatumFocusLost

    private void txtDatumFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDatumFocusGained
        ((JTextField) evt.getSource()).selectAll();
    }//GEN-LAST:event_txtDatumFocusGained

    private void btnBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBottomActionPerformed
        cmbMonat.setSelectedIndex(cmbMonat.getModel().getSize() - 1);
    }//GEN-LAST:event_btnBottomActionPerformed

    private void btnRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRightActionPerformed
        cmbMonat.setSelectedIndex(cmbMonat.getSelectedIndex() + 1);
    }//GEN-LAST:event_btnRightActionPerformed

    private void btnLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeftActionPerformed
        cmbMonat.setSelectedIndex(cmbMonat.getSelectedIndex() - 1);
    }//GEN-LAST:event_btnLeftActionPerformed

    private void btnTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTopActionPerformed
        cmbMonat.setSelectedIndex(0);
    }//GEN-LAST:event_btnTopActionPerformed

    private void rbMonatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbMonatActionPerformed
        if (initPhase) {
            return;
        }
        cmbVon.setModel(new DefaultComboBoxModel());
        cmbVon.setEnabled(false);
        cmbBis.setModel(new DefaultComboBoxModel());
        cmbBis.setEnabled(false);
        setMinMax();
        cmbMonat.setModel(SYSCalendar.createMonthList(min, max));
        cmbMonat.setSelectedIndex(cmbMonat.getModel().getSize() - 1);
        cmbMonat.setEnabled(true);
        ListElement leMonat = (ListElement) cmbMonat.getSelectedItem();
        this.von = (Date) leMonat.getObject();
        this.bis = SYSCalendar.eom((Date) leMonat.getObject());
        btnTop.setEnabled(this.min.compareTo(this.von) < 0);
        btnLeft.setEnabled(this.min.compareTo(this.von) < 0);
        btnRight.setEnabled(this.max.compareTo(this.bis) >= 0);
        btnBottom.setEnabled(this.max.compareTo(this.bis) >= 0);
        reloadDisplay();
    }//GEN-LAST:event_rbMonatActionPerformed

    public void dispose() {
        ListSelectionModel lsm1 = tblStat.getSelectionModel();
        if (lslstat != null) {
            lsm1.removeListSelectionListener(lslstat);
        }
        ListSelectionModel lsm2 = tblTG.getSelectionModel();
        if (lsl != null) {
            lsm2.removeListSelectionListener(lsl);
        }
        SYSTools.unregisterListeners(this);
        super.dispose();
    }

    private void cmbMonatItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbMonatItemStateChanged
        if (initPhase) {
            return;
        }
        initPhase = true; // damit die andere combobox nicht auch noch auf die Änderungen reagiert.
        ListElement leMonat = (ListElement) cmbMonat.getSelectedItem();
        this.von = (Date) leMonat.getObject();
        this.bis = SYSCalendar.eom((Date) leMonat.getObject());
        btnTop.setEnabled(this.min.compareTo(this.von) < 0);
        btnLeft.setEnabled(this.min.compareTo(this.von) < 0);
        btnRight.setEnabled(this.max.compareTo(this.bis) >= 0);
        btnBottom.setEnabled(this.max.compareTo(this.bis) >= 0);
        initPhase = false;
        reloadDisplay();
    }//GEN-LAST:event_cmbMonatItemStateChanged

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        ResultSet rs = ((TMBarbetrag) tblTG.getModel()).getResultSet();
        double vortrag = ((TMBarbetrag) tblTG.getModel()).getVortrag();
        printSingle(rs, vortrag);
    }//GEN-LAST:event_btnPrintActionPerformed

    private void printSingle(ResultSet rs, double vortrag) {

        try {
            // Create temp file.
            File temp = File.createTempFile("barbetrag", ".html");

            // Delete temp file when program exits.
            temp.deleteOnExit();

            // Write to temp file
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));
            String html = SYSTools.htmlUmlautConversion(BarbetragTools.getEinzelnAsHTML(rs, vortrag, BewohnerTools.findByBWKennung(currentBW)));

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

        tcm1.getColumn(0).setHeaderValue("Nachname");
        tcm1.getColumn(0).setPreferredWidth(textWidth / 4);
        tcm1.getColumn(1).setHeaderValue("Vorname");
        tcm1.getColumn(1).setPreferredWidth(textWidth / 4);
        tcm1.getColumn(2).setHeaderValue("BWKennung");
        tcm1.getColumn(2).setPreferredWidth(textWidth / 4);
        tcm1.getColumn(3).setHeaderValue("Summe");
        tcm1.getColumn(3).setPreferredWidth(textWidth / 4);
    }//GEN-LAST:event_jspStatComponentResized

    private void rbAktuellActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbAktuellActionPerformed
        reloadDisplay();
    }//GEN-LAST:event_rbAktuellActionPerformed

    private void rbBWAlleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbBWAlleActionPerformed
        reloadDisplay();
    }//GEN-LAST:event_rbBWAlleActionPerformed

    private void tblStatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblStatMouseClicked
        if (evt.getClickCount() > 1) {
            txtBW.setText(currentBW);
            jtpMain.setSelectedIndex(TAB_TG);
            btnFind.doClick();
//            rbAlle.setEnabled(true);
//            rbMonat.setEnabled(true);
//            rbZeitraum.setEnabled(true);
//            rbAlle.doClick();
        }
    }//GEN-LAST:event_tblStatMouseClicked

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        if (JOptionPane.showConfirmDialog(this, "Sie löschen nun den markierten Datensatz.\nAuch ein evtl. vorhandener, zugehöriger Stornodatensatz wird mit entfernt.\n\nMöchten Sie das ?", "Storno eines Taschengeldvorgangs", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        Connection db = OPDE.getDb().db;
        String deleteSQL = "DELETE FROM Taschengeld WHERE TGID=? OR _cancel=?";

        try {
            // Löschen
            PreparedStatement stmtDelete = db.prepareStatement(deleteSQL);
            stmtDelete.setLong(1, currentTGID);
            stmtDelete.setLong(2, currentTGID);
            stmtDelete.executeUpdate();

        } catch (SQLException ex) {
            new DlgException(ex);
            ex.printStackTrace();
        }
        // Nach dem Löschen ist erstmal nix gewählt. Daher würden sonst die Knöpfe aktiv bleiben.
        // Schalten wir sie lieber vorsichtshalber ab.
        btnDelete.setEnabled(false);
        btnStorno.setEnabled(false);
        setMinMax();
        reloadDisplay();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnStornoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStornoActionPerformed
        if (JOptionPane.showConfirmDialog(this, "Sie stornieren nun den markierten Datensatz\n\nMöchten Sie das ?", "Storno eines Taschengeldvorgangs", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        Connection db = OPDE.getDb().db;
        String updateSQL = "UPDATE Taschengeld SET Belegtext = CONCAT('***',Belegtext,'*** (Vorgang storniert)'),"
                + " _cancel = ?, _editor=?, _edate=now()"
                + " WHERE TGID=?";
        String insertSQL = "INSERT INTO Taschengeld (BelegDatum,Belegtext,Betrag,BWKennung,_cancel,_creator,_editor,_cdate,_edate)"
                + " SELECT BelegDatum, CONCAT('Storno des Vorgangs >>',Belegtext,'<<'),Betrag * -1, BWKennung, TGID, ?, null, now(), now()"
                + " FROM Taschengeld WHERE TGID = ?";

        try {
            // Hier beginnt eine Transaktion
            db.setAutoCommit(false);
            db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            db.commit();

            // CancelRec einfügen
            PreparedStatement stmtCancelRec = db.prepareStatement(insertSQL);
            stmtCancelRec.setString(1, OPDE.getLogin().getUser().getUKennung());
            stmtCancelRec.setLong(2, currentTGID);
            stmtCancelRec.executeUpdate();

            long canceltgid = OPDE.getDb().getLastInsertedID();

            // stornierten Record updaten
            PreparedStatement stmtCancelledRec = db.prepareStatement(updateSQL);
            stmtCancelledRec.setLong(1, canceltgid);
            stmtCancelledRec.setString(2, OPDE.getLogin().getUser().getUKennung());
            stmtCancelledRec.setLong(3, currentTGID);
            stmtCancelledRec.executeUpdate();

            db.commit();
            db.setAutoCommit(true);

        } catch (SQLException ex) {
            try {
                db.rollback();
            } catch (SQLException ex1) {
                new DlgException(ex1);
                ex1.printStackTrace();
                System.exit(1);
            }
            new DlgException(ex);
        }
        // Nach dem Löschen ist erstmal nix gewählt. Daher würden sonst die Knöpfe aktiv bleiben.
        // Schalten wir sie lieber vorsichtshalber ab.
        btnDelete.setEnabled(false);
        btnStorno.setEnabled(false);
        setMinMax();
        reloadDisplay();
    }//GEN-LAST:event_btnStornoActionPerformed

    private void cmbBisItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbBisItemStateChanged
        if (initPhase) {
            return;
        }
        initPhase = true; // damit die andere combobox nicht auch noch auf die Änderungen reagiert.
        int iVon = cmbVon.getSelectedIndex();
        int iBis = cmbBis.getSelectedIndex();
        if (iBis < iVon) {
            cmbVon.setSelectedIndex(iBis); // Wenn der Anwender den Von Index über den Bis Index hinaus zieht, passt sich der Bis Index an.
        }
        ListElement leVon = (ListElement) cmbVon.getSelectedItem();
        ListElement leBis = (ListElement) cmbBis.getSelectedItem();
        this.von = (Date) leVon.getObject();
        this.bis = SYSCalendar.eom((Date) leBis.getObject());
        initPhase = false;
        reloadDisplay();
    }//GEN-LAST:event_cmbBisItemStateChanged

    private void cmbVonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbVonItemStateChanged
        if (initPhase) {
            return;
        }
        initPhase = true; // damit die andere combobox nicht auch noch auf die Änderungen reagiert.
        int iVon = cmbVon.getSelectedIndex();
        int iBis = cmbBis.getSelectedIndex();
        if (iVon > iBis) {
            cmbBis.setSelectedIndex(iVon); // Wenn der Anwender den Von Index über den Bis Index hinaus zieht, passt sich der Bis Index an.
        }
        ListElement leVon = (ListElement) cmbVon.getSelectedItem();
        ListElement leBis = (ListElement) cmbBis.getSelectedItem();
        this.von = (Date) leVon.getObject();
        this.bis = SYSCalendar.eom((Date) leBis.getObject());
        initPhase = false;
        reloadDisplay();
    }//GEN-LAST:event_cmbVonItemStateChanged

    private void rbZeitraumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbZeitraumActionPerformed
        if (initPhase) {
            return;
        }
        cmbVon.setEnabled(true);
        cmbBis.setEnabled(true);
        cmbMonat.setModel(new DefaultComboBoxModel());
        cmbMonat.setEnabled(false);
        setMinMax();
        cmbVon.setModel(SYSCalendar.createMonthList(min, max));
        cmbBis.setModel(SYSCalendar.createMonthList(min, max));
        cmbVon.setSelectedIndex(0);
        cmbBis.setSelectedIndex(cmbBis.getModel().getSize() - 1);
        ListElement leVon = (ListElement) cmbVon.getSelectedItem();
        ListElement leBis = (ListElement) cmbBis.getSelectedItem();
        this.von = (Date) leVon.getObject();
        this.bis = SYSCalendar.eom((Date) leBis.getObject());
        btnTop.setEnabled(false);
        btnLeft.setEnabled(false);
        btnRight.setEnabled(false);
        btnBottom.setEnabled(false);
        reloadDisplay();
    }//GEN-LAST:event_rbZeitraumActionPerformed

    private void rbAlleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbAlleActionPerformed
        if (initPhase) {
            return;
        }
        this.von = this.min;
        this.bis = this.max;
        cmbVon.setModel(new DefaultComboBoxModel());
        cmbVon.setEnabled(false);
        cmbBis.setModel(new DefaultComboBoxModel());
        cmbBis.setEnabled(false);
        cmbMonat.setModel(new DefaultComboBoxModel());
        cmbMonat.setEnabled(false);
        btnTop.setEnabled(false);
        btnLeft.setEnabled(false);
        btnRight.setEnabled(false);
        btnBottom.setEnabled(false);
        reloadDisplay();
    }//GEN-LAST:event_rbAlleActionPerformed

    private void jtpMainStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jtpMainStateChanged
        if (initPhase) {
            return;
        }
        if (jtpMain.getSelectedIndex() == TAB_STAT) {
            cmbPast.setEnabled(true);
            reloadDisplay();
        } else {
            cmbPast.setEnabled(false);
            if (currentBW.equals("")) {
                btnPrint.setEnabled(false);
            } else {
                txtBW.setText(currentBW);
                btnFind.doClick();
            }
        }
    }//GEN-LAST:event_jtpMainStateChanged

    private void jspDataComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspDataComponentResized
        JScrollPane jsp = (JScrollPane) evt.getComponent();
        Dimension dim = jsp.getSize();
        // Größe der Text Spalten im DFN ändern.
        // Summe der fixen Spalten  = 175 + ein bisschen
        int textWidth = dim.width - 340;
        TableColumnModel tcm1 = tblTG.getColumnModel();
        if (tblTG.getModel().getRowCount() == 0) {
            return;
        }

        tcm1.getColumn(0).setHeaderValue("Belegdatum");
        tcm1.getColumn(0).setPreferredWidth(80);
        tcm1.getColumn(1).setHeaderValue("Belegtext");
        tcm1.getColumn(1).setPreferredWidth(textWidth);
        tcm1.getColumn(2).setHeaderValue("Betrag");
        tcm1.getColumn(2).setPreferredWidth(120);
        tcm1.getColumn(3).setHeaderValue("Zeilensaldo");
        tcm1.getColumn(3).setPreferredWidth(120);
    }//GEN-LAST:event_jspDataComponentResized

    private void txtBWFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBWFocusGained
        txtBW.setSelectionStart(0);
        txtBW.setSelectionEnd(txtBW.getText().length());
    }//GEN-LAST:event_txtBWFocusGained

    private void btnFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindActionPerformed
        txtBWActionPerformed(evt);
    }//GEN-LAST:event_btnFindActionPerformed

    private void txtBWActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBWActionPerformed
        String prevBW = currentBW;
        currentBW = SYSTools.findeBW(this, txtBW.getText(), OPDE.isAdmin());
        if (currentBW.equals("")) {
            JOptionPane.showMessageDialog(this, "Keine(n) passende(n) Bewohner(in) gefunden.", "Hinweis", JOptionPane.INFORMATION_MESSAGE);
            //tblTG.setModel(new DefaultTableModel());
            currentBW = prevBW;
        } else {
            //long numRecs = ((Long) DBHandling.getSingleValue("Taschengeld","COUNT(*)","BWKennung",currentBW)).longValue();
            setMinMax();
            rbAlle.setEnabled(true);
            rbZeitraum.setEnabled(true);
            rbMonat.setEnabled(true);
            tblTG.setVisible(true);
            rbMonat.doClick();
        }
    }//GEN-LAST:event_txtBWActionPerformed

    private void cmbPastItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbPastItemStateChanged
        if (initPhase) {
            return;
        }
        reloadDisplay();
    }//GEN-LAST:event_cmbPastItemStateChanged

    /**
     * Setzt den Zeitraum, innerhalb dessen die Belege in der Tabelle angezeigt werden können. Nicht unbedingt werden.
     */
    private void setMinMax() {
        // Ermittelt die maximale Ausdehnung (chronologisch gesehen) aller Belege für einen bestimmten BW
        ResultSet rs = DBHandling.getResultSet("Taschengeld", new String[]{"MIN(BelegDatum)", "Max(BelegDatum)"}, "BWKennung", currentBW, "=");
        try {
            this.min = (java.util.Date) rs.getDate(1);
            if (this.min == null) {
                this.min = SYSCalendar.today_date();
            }
            this.max = SYSCalendar.today_date();
            //this.von = min;
            //this.bis = max;
        } catch (SQLException ex) {
            new DlgException(ex);
            ex.printStackTrace();
        }
    }

    private void summeNeuRechnen() {
        BigDecimal summe = (BigDecimal) DBHandling.getSingleValue("Taschengeld", "SUM(Betrag)", "BWKennung", currentBW);
        if (summe == null) {
            summe = new BigDecimal(0);
        }
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        lblBetrag.setText(nf.format(summe.floatValue()));
        if (summe.floatValue() < 0) {
            lblBetrag.setForeground(Color.RED);
        } else {
            lblBetrag.setForeground(Color.BLACK);
        }
    }

    private void reloadDisplay() {
        lblMessage.setText("");
        if (!initPhase) {
            // Welcher Tab ist gerade ausgewählt ?
            switch (jtpMain.getSelectedIndex()) {
                case TAB_TG: {

                    // Anzuzeigenden Zeitraum ermitteln.
                    if (rbMonat.isSelected()) {
                        ListElement leMonat = (ListElement) cmbMonat.getSelectedItem();
                        this.von = (Date) leMonat.getObject();
                        this.bis = SYSCalendar.eom((Date) leMonat.getObject());
                    } else if (rbZeitraum.isSelected()) {
                        ListElement leVon = (ListElement) cmbVon.getSelectedItem();
                        ListElement leBis = (ListElement) cmbBis.getSelectedItem();
                        this.von = (Date) leVon.getObject();
                        this.bis = SYSCalendar.eom((Date) leBis.getObject());
                    } else {
                        setMinMax();
                        this.von = this.min;
                        this.bis = this.max;
                    }

                    if (!currentBW.equals("")) {
                        boolean outerPhase = initPhase;
                        initPhase = true;
                        summeNeuRechnen();
                        SYSTools.setBWLabel(lblBW, currentBW);
                        txtDatum.setText(SYSCalendar.printGermanStyle(SYSCalendar.today_date()));
                        txtBelegtext.setText("Bitte geben Sie einen Belegtext ein.");
                        txtBetrag.setText("0,00 " + SYSConst.eurosymbol);
                        this.betrag = 0.0d;
                        txtDatum.setEnabled(true);
                        txtBelegtext.setEnabled(true);
                        txtBetrag.setEnabled(true);
                        reloadTable();
                        rbAlle.setEnabled(true);
                        rbMonat.setEnabled(true);
                        rbZeitraum.setEnabled(true);
                        btnDelete.setEnabled(false);
                        btnStorno.setEnabled(false);
                        initPhase = outerPhase;
                    }
                    if (tblTG.getModel().getRowCount() == 0) {
                        btnPrint.setEnabled(false);
                        btnEdit.setEnabled(false);
                    } else {
                        btnPrint.setEnabled(true);
                        ocs.setEnabled(classname, "btnEdit", btnEdit, true);
                        if (btnEdit.isEnabled()) {
                            btnEdit.setSelected(false);
                        }
                    }
                    break;
                }
                case TAB_STAT: {
                    cmbPast.setEnabled(true);
                    String sql = "SELECT SUM(Betrag) FROM Taschengeld";
                    try {
                        PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
                        ResultSet rs = stmt.executeQuery();
                        rs.first();
                        float summe = rs.getFloat(1);
                        NumberFormat nf = NumberFormat.getCurrencyInstance();
                        String summentext = nf.format(summe);

                        // Ist auch eine Anzeige für die Vergangenheit gewünscht ?
                        // Nur wenn ein anderer Monat als der aktuelle gewählt ist.
                        if (cmbPast.getSelectedIndex() < cmbPast.getModel().getSize() - 1) {
                            String sqlPast = "SELECT SUM(Betrag) FROM Taschengeld WHERE BelegDatum <= ? ";
                            PreparedStatement stmtPast = OPDE.getDb().db.prepareStatement(sqlPast);
                            ListElement le = (ListElement) cmbPast.getSelectedItem();
                            Date monat = (Date) le.getObject();
                            stmtPast.setDate(1, new java.sql.Date(SYSCalendar.eom(monat).getTime()));
                            ResultSet rsPast = stmtPast.executeQuery();
                            rsPast.first();
                            float summePast = rsPast.getFloat(1);
                            summentext += " (" + nf.format(summePast) + ")";
                            lblSumme.setToolTipText("<html>Die Summe in Klammern bezeichnet den Stand zum Monatsende <b>" + le.toString() + "</b></html>");
                        } else {
                            lblSumme.setToolTipText(null);
                        }

                        lblSumme.setText(summentext);
                        if (summe < 0) {
                            lblSumme.setForeground(Color.RED);
                        } else {
                            lblSumme.setForeground(Color.BLACK);
                        }
                    } catch (SQLException ex) {
                        new DlgException(ex);
                        ex.printStackTrace();
                    }
                    btnPrint.setEnabled(false);
                    btnStorno.setEnabled(false);
                    btnDelete.setEnabled(false);
                    btnEdit.setEnabled(false);
                    reloadStatTable();
                    break;
                }
            }
        }
    }

    private void reloadStatTable() {
        ListSelectionModel lsm = tblStat.getSelectionModel();
        if (lslstat != null) {
            lsm.removeListSelectionListener(lslstat);
        }
        lslstat = new HandleStatSelections();
        tblStat.setModel(new TMTGStat(rbBWAlle.isSelected()));
        tblStat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblStat.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lsm.addListSelectionListener(lslstat);

        //if (tblStat.getModel().getRowCount() > 0) lsm.setSelectionInterval(0,0);

        jspStat.dispatchEvent(new ComponentEvent(jspStat, ComponentEvent.COMPONENT_RESIZED));

        tblStat.getColumnModel().getColumn(0).setCellRenderer(new RNDStandard());
        tblStat.getColumnModel().getColumn(1).setCellRenderer(new RNDStandard());
        tblStat.getColumnModel().getColumn(2).setCellRenderer(new RNDStandard());
        tblStat.getColumnModel().getColumn(3).setCellRenderer(new StandardCurrencyRenderer());
    }

    private void insert() {
        PreparedStatement stmt;
        java.sql.Date datum = new java.sql.Date(SYSCalendar.erkenneDatum(txtDatum.getText()).getTimeInMillis());

        try {
            String insertSQL = "INSERT INTO Taschengeld (BelegDatum,Belegtext,Betrag,BWKennung,_creator,_editor,_cdate,_edate)"
                    + " VALUES (?,?,?,?,?,null,now(),now())";

            stmt = OPDE.getDb().db.prepareStatement(insertSQL);

            stmt.setDate(1, datum);
            stmt.setString(2, txtBelegtext.getText());
            stmt.setDouble(3, this.betrag);
            stmt.setString(4, currentBW);
            stmt.setString(5, OPDE.getLogin().getUser().getUKennung());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            new DlgException(ex);
            ex.printStackTrace();
        }

        // schaltet auf den Monat um, in dem der letzte Beleg eingegeben wurde.
        // Sofern die ein bestimmter Monat eingestellt war.
        if (rbMonat.isSelected()) {
            SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy");
            String pattern = formatter.format(datum);
            SYSTools.selectInComboBox(cmbMonat, pattern, true);
        }

        reloadTable();
        txtBelegtext.setText("Bitte geben Sie einen Belegtext ein.");
        txtBetrag.setText("0.00 " + SYSConst.eurosymbol);
        this.betrag = 0.0d;
        txtDatum.requestFocus();
        lblMessage.setText(SYSCalendar.toGermanTime(new Time(SYSCalendar.heute().getTimeInMillis())) + " Uhr : " + "Neuen Datensatz eingefügt.");

        TMBarbetrag tm = (TMBarbetrag) tblTG.getModel();


        // Das hier markiert den zuletzt eingefügten Datensatz.
        int index = tm.getRow(OPDE.getDb().getLastInsertedID());
        ListSelectionModel lsm = tblTG.getSelectionModel();
        lsm.setSelectionInterval(index, index);
        // Das hier rollt auf den zuletzt eingefügten Datensatz.
        tblTG.invalidate();
        Rectangle rect = tblTG.getCellRect(index, 0, true);
        tblTG.scrollRectToVisible(rect);
    }

    private void reloadTable() {
        // ScrollPosition merken
        //Point oldpos = jspData.getViewport().getViewPosition();

        // Tagesbericht Liste aktualisieren
        ListSelectionModel lsm = tblTG.getSelectionModel();
        if (lsl != null) {
            lsm.removeListSelectionListener(lsl);
        }
        if (tml != null) {
            tblTG.getModel().removeTableModelListener(tml);
        }
        lsl = new HandleSelections();
        tml = new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                if (e.getColumn() == 2) { // Betrag hat sich geändert
                    summeNeuRechnen();
                }
            }
        };
        boolean subset = rbMonat.isSelected() || rbZeitraum.isSelected();
        tblTG.setModel(new TMBarbetrag(this.currentBW, subset, this.von, this.bis, btnEdit));
        tblTG.getModel().addTableModelListener(tml);
        tblTG.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblTG.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lsm.addListSelectionListener(lsl);

        //if (tblTG.getModel().getRowCount() > 0) lsm.setSelectionInterval(0,0);

        jspData.dispatchEvent(new ComponentEvent(jspData, ComponentEvent.COMPONENT_RESIZED));

        tblTG.getColumnModel().getColumn(0).setCellRenderer(new RNDStandard());
        tblTG.getColumnModel().getColumn(1).setCellRenderer(new RNDStandard());
        tblTG.getColumnModel().getColumn(2).setCellRenderer(new StandardCurrencyRenderer());
        tblTG.getColumnModel().getColumn(3).setCellRenderer(new StandardCurrencyRenderer());

        tblTG.getColumnModel().getColumn(0).setCellEditor(new CEDefault());
        tblTG.getColumnModel().getColumn(1).setCellEditor(new CEDefault());
        tblTG.getColumnModel().getColumn(2).setCellEditor(new CEDefault());
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgBWFilter;
    private javax.swing.ButtonGroup bgFilter;
    private javax.swing.JButton btnBottom;
    private javax.swing.JButton btnDelete;
    private javax.swing.JToggleButton btnEdit;
    private javax.swing.JButton btnFind;
    private javax.swing.JButton btnLeft;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnRight;
    private javax.swing.JButton btnStorno;
    private javax.swing.JButton btnTop;
    private javax.swing.JComboBox cmbBis;
    private javax.swing.JComboBox cmbMonat;
    private javax.swing.JComboBox cmbPast;
    private javax.swing.JComboBox cmbVon;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JScrollPane jspData;
    private javax.swing.JScrollPane jspStat;
    private javax.swing.JTabbedPane jtpMain;
    private javax.swing.JLabel lbl1;
    private javax.swing.JLabel lblBW;
    private javax.swing.JLabel lblBetrag;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JLabel lblSumme;
    private javax.swing.JPanel pnlBarbetrag;
    private javax.swing.JPanel pnlStat;
    private javax.swing.JPanel pnlStatus;
    private javax.swing.JRadioButton rbAktuell;
    private javax.swing.JRadioButton rbAlle;
    private javax.swing.JRadioButton rbBWAlle;
    private javax.swing.JRadioButton rbMonat;
    private javax.swing.JRadioButton rbZeitraum;
    private javax.swing.JTable tblStat;
    private javax.swing.JTable tblTG;
    private javax.swing.JTextField txtBW;
    private javax.swing.JTextField txtBelegtext;
    private javax.swing.JTextField txtBetrag;
    private javax.swing.JTextField txtDatum;
    // End of variables declaration//GEN-END:variables

    class HandleSelections implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent lse) {
            // Erst reagieren wenn der Auswahl-Vorgang abgeschlossen ist.
            TMBarbetrag tm = (TMBarbetrag) tblTG.getModel();
            if (tm.getRowCount() <= 1) {
                return;
            }
            ListSelectionModel lsm = tblTG.getSelectionModel();

            boolean _cancel = false;
            if (!lse.getValueIsAdjusting()) {
                if (lsm.isSelectionEmpty()) {
                    currentTGID = 0L;
                } else {
                    currentTGID = ((Long) tm.getValueAt(lsm.getLeadSelectionIndex(), 5 - 1)).longValue();
                    _cancel = ((Long) tm.getValueAt(lsm.getLeadSelectionIndex(), 6 - 1)).longValue() > 0; // Ist das ein StornoRec oder ein stornierter Rec ?
                }
                btnStorno.setEnabled(currentTGID != 0 && !_cancel);
                ocs.setEnabled(classname, "btnDelete", btnDelete, currentTGID != 0);
                //btnDelete.setEnabled(ocs.mayEnabled(classname, "btnDelete", true) && );
            }
        }
    } // class HandleTBSelections

    class HandleStatSelections implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent lse) {
            // Erst reagieren wenn der Auswahl-Vorgang abgeschlossen ist.
            TableModel tm = tblStat.getModel();
            if (tm.getRowCount() <= 1) {
                return;
            }
            ListSelectionModel lsm = tblStat.getSelectionModel();
            if (!lse.getValueIsAdjusting()) {
                if (lsm.isSelectionEmpty()) {
                    currentBW = "";
                } else {
                    currentBW = ((String) tm.getValueAt(lsm.getLeadSelectionIndex(), 3 - 1));
                }
            }
        }
    } // class HandleTBSelections
}
