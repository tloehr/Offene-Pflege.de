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

import java.awt.event.*;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.border.*;
import javax.swing.event.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import entity.BarbetragTools;
import entity.BewohnerTools;
import op.OCSec;
import op.OPDE;
import op.tools.*;
import tablerenderer.RNDStandard;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.html.parser.Entity;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author tloehr
 */
public class FrmTG extends JFrame {
    public static final String internalClassID = "admin.residents.cash";

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


    /**
     * Creates new form FrmBWAttr
     */
    private void tblTGMousePressed(MouseEvent e) {
        // TODO add your code here
    }

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
        btnStorno = new JButton();
        btnDelete = new JButton();
        btnPrint = new JButton();
        jtpMain = new JTabbedPane();
        pnlBarbetrag = new JPanel();
        jPanel1 = new JPanel();
        rbAlle = new JRadioButton();
        txtBW = new JTextField();
        btnFind = new JButton();
        jLabel2 = new JLabel();
        jPanel2 = new JPanel();
        jLabel3 = new JLabel();
        cmbVon = new JComboBox();
        cmbBis = new JComboBox();
        jLabel4 = new JLabel();
        jSeparator1 = new JSeparator();
        rbZeitraum = new JRadioButton();
        rbMonat = new JRadioButton();
        jPanel3 = new JPanel();
        jLabel6 = new JLabel();
        cmbMonat = new JComboBox();
        btnTop = new JButton();
        btnLeft = new JButton();
        btnRight = new JButton();
        btnBottom = new JButton();
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

            //---- btnStorno ----
            btnStorno.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/edit_remove.png")));
            btnStorno.setMnemonic('s');
            btnStorno.setText("Storno");
            btnStorno.setEnabled(false);
            btnStorno.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnStornoActionPerformed(e);
                }
            });
            jToolBar1.add(btnStorno);

            //---- btnDelete ----
            btnDelete.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/editdelete.png")));
            btnDelete.setMnemonic('l');
            btnDelete.setText("L\u00f6schen");
            btnDelete.setEnabled(false);
            btnDelete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnDeleteActionPerformed(e);
                }
            });
            jToolBar1.add(btnDelete);

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
                    "2*(fill:default, $lgap), fill:default"));

                //======== jPanel1 ========
                {
                    jPanel1.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
                    jPanel1.setLayout(new FormLayout(
                        "3*(default, $lcgap), default",
                        "7*(fill:default, $lgap), fill:default"));

                    //---- rbAlle ----
                    rbAlle.setMnemonic('a');
                    rbAlle.setSelected(true);
                    rbAlle.setText("Alle Belege anzeigen");
                    rbAlle.setBorder(BorderFactory.createEmptyBorder());
                    rbAlle.setMargin(new Insets(0, 0, 0, 0));
                    rbAlle.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            rbAlleActionPerformed(e);
                        }
                    });
                    jPanel1.add(rbAlle, CC.xy(3, 7));

                    //---- txtBW ----
                    txtBW.setToolTipText("Sie k\u00f6nnen hier Teile des Nachnamens oder die Kennung eingeben.");
                    txtBW.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            txtBWActionPerformed(e);
                        }
                    });
                    txtBW.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            txtBWFocusGained(e);
                        }
                    });
                    jPanel1.add(txtBW, CC.xy(3, 3));

                    //---- btnFind ----
                    btnFind.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/search.png")));
                    btnFind.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnFindActionPerformed(e);
                        }
                    });
                    jPanel1.add(btnFind, CC.xy(5, 3));

                    //---- jLabel2 ----
                    jLabel2.setText("Bewohner(in) suchen");
                    jPanel1.add(jLabel2, CC.xy(3, 1));

                    //======== jPanel2 ========
                    {
                        jPanel2.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
                        jPanel2.setLayout(new FormLayout(
                            "default, default:grow, $lcgap, default",
                            "3*(fill:default, $lgap), fill:default"));

                        //---- jLabel3 ----
                        jLabel3.setText("Von:");
                        jPanel2.add(jLabel3, CC.xy(2, 1));

                        //---- cmbVon ----
                        cmbVon.setModel(new DefaultComboBoxModel(new String[] {
                            "Item 1",
                            "Item 2",
                            "Item 3",
                            "Item 4"
                        }));
                        cmbVon.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent e) {
                                cmbVonItemStateChanged(e);
                            }
                        });
                        jPanel2.add(cmbVon, CC.xy(2, 3));

                        //---- cmbBis ----
                        cmbBis.setModel(new DefaultComboBoxModel(new String[] {
                            "Item 1",
                            "Item 2",
                            "Item 3",
                            "Item 4"
                        }));
                        cmbBis.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent e) {
                                cmbBisItemStateChanged(e);
                            }
                        });
                        jPanel2.add(cmbBis, CC.xy(2, 7));

                        //---- jLabel4 ----
                        jLabel4.setText("Bis:");
                        jPanel2.add(jLabel4, CC.xy(2, 5));
                    }
                    jPanel1.add(jPanel2, CC.xywh(3, 11, 3, 1));
                    jPanel1.add(jSeparator1, CC.xywh(3, 5, 3, 1));

                    //---- rbZeitraum ----
                    rbZeitraum.setMnemonic('z');
                    rbZeitraum.setText("Zeitraum einschr\u00e4nken");
                    rbZeitraum.setBorder(BorderFactory.createEmptyBorder());
                    rbZeitraum.setMargin(new Insets(0, 0, 0, 0));
                    rbZeitraum.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            rbZeitraumActionPerformed(e);
                        }
                    });
                    jPanel1.add(rbZeitraum, CC.xy(3, 9));

                    //---- rbMonat ----
                    rbMonat.setMnemonic('m');
                    rbMonat.setText("Monat einschr\u00e4nken");
                    rbMonat.setBorder(BorderFactory.createEmptyBorder());
                    rbMonat.setMargin(new Insets(0, 0, 0, 0));
                    rbMonat.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            rbMonatActionPerformed(e);
                        }
                    });
                    jPanel1.add(rbMonat, CC.xy(3, 13));

                    //======== jPanel3 ========
                    {
                        jPanel3.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
                        jPanel3.setLayout(new FormLayout(
                            "5*(default, $lcgap), default",
                            "2*(fill:default, $lgap), fill:default"));

                        //---- jLabel6 ----
                        jLabel6.setText("Monat:");
                        jPanel3.add(jLabel6, CC.xy(3, 1));

                        //---- cmbMonat ----
                        cmbMonat.setModel(new DefaultComboBoxModel(new String[] {
                            "Item 1",
                            "Item 2",
                            "Item 3",
                            "Item 4"
                        }));
                        cmbMonat.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent e) {
                                cmbMonatItemStateChanged(e);
                            }
                        });
                        jPanel3.add(cmbMonat, CC.xywh(3, 3, 7, 1));

                        //---- btnTop ----
                        btnTop.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/2leftarrow.png")));
                        btnTop.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnTopActionPerformed(e);
                            }
                        });
                        jPanel3.add(btnTop, CC.xy(3, 5));

                        //---- btnLeft ----
                        btnLeft.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/1leftarrow.png")));
                        btnLeft.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnLeftActionPerformed(e);
                            }
                        });
                        jPanel3.add(btnLeft, CC.xy(5, 5));

                        //---- btnRight ----
                        btnRight.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/1rightarrow.png")));
                        btnRight.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnRightActionPerformed(e);
                            }
                        });
                        jPanel3.add(btnRight, CC.xy(7, 5));

                        //---- btnBottom ----
                        btnBottom.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/2rightarrow.png")));
                        btnBottom.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnBottomActionPerformed(e);
                            }
                        });
                        jPanel3.add(btnBottom, CC.xy(9, 5));
                    }
                    jPanel1.add(jPanel3, CC.xywh(3, 15, 3, 1));
                }
                pnlBarbetrag.add(jPanel1, CC.xywh(1, 3, 1, 3));

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
                    jPanel5.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
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
                            .addComponent(jspStat, GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
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
                    .addComponent(jtpMain, GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(pnlStatus, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );
        setSize(872, 693);
        setLocationRelativeTo(null);

        //---- bgFilter ----
        ButtonGroup bgFilter = new ButtonGroup();
        bgFilter.add(rbAlle);
        bgFilter.add(rbZeitraum);
        bgFilter.add(rbMonat);

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

        EntityManager em = OPDE.createEM();

        try{
            em.getTransaction().begin();
            Query query = em.createQuery("DELETE FROM Taschengeld t WHERE t.replacedBy = :tg ");
            query.executeUpdate();

            em.remove(currentTG);
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
    private JToolBar jToolBar1;
    private JToggleButton btnEdit;
    private JButton btnStorno;
    private JButton btnDelete;
    private JButton btnPrint;
    private JTabbedPane jtpMain;
    private JPanel pnlBarbetrag;
    private JPanel jPanel1;
    private JRadioButton rbAlle;
    private JTextField txtBW;
    private JButton btnFind;
    private JLabel jLabel2;
    private JPanel jPanel2;
    private JLabel jLabel3;
    private JComboBox cmbVon;
    private JComboBox cmbBis;
    private JLabel jLabel4;
    private JSeparator jSeparator1;
    private JRadioButton rbZeitraum;
    private JRadioButton rbMonat;
    private JPanel jPanel3;
    private JLabel jLabel6;
    private JComboBox cmbMonat;
    private JButton btnTop;
    private JButton btnLeft;
    private JButton btnRight;
    private JButton btnBottom;
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

//    class HandleSelections implements ListSelectionListener {
//
//        public void valueChanged(ListSelectionEvent lse) {
//            // Erst reagieren wenn der Auswahl-Vorgang abgeschlossen ist.
//            TMBarbetrag tm = (TMBarbetrag) tblTG.getModel();
//            if (tm.getRowCount() <= 1) {
//                return;
//            }
//            ListSelectionModel lsm = tblTG.getSelectionModel();
//
//            boolean _cancel = false;
//            if (!lse.getValueIsAdjusting()) {
//                if (lsm.isSelectionEmpty()) {
//                    currentTGID = 0L;
//                } else {
//                    currentTGID = ((Long) tm.getValueAt(lsm.getLeadSelectionIndex(), 5 - 1)).longValue();
//                    _cancel = ((Long) tm.getValueAt(lsm.getLeadSelectionIndex(), 6 - 1)).longValue() > 0; // Ist das ein StornoRec oder ein stornierter Rec ?
//                }
//                btnStorno.setEnabled(currentTGID != 0 && !_cancel);
//                ocs.setEnabled(classname, "btnDelete", btnDelete, currentTGID != 0);
//                //btnDelete.setEnabled(ocs.mayEnabled(classname, "btnDelete", true) && );
//            }
//        }
//    } // class HandleTBSelections

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
