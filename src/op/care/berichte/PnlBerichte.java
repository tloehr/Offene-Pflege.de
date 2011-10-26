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
package op.care.berichte;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import entity.*;
import op.OPDE;
import op.care.CleanablePanel;
import op.care.FrmPflege;
import op.tools.InternalClassACL;
import op.tools.SYSCalendar;
import op.tools.SYSPrint;
import op.tools.SYSTools;
import org.jdesktop.swingx.*;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTitledPanel;
import tablemodels.TMPflegeberichte;

import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

/**
 * @author root
 */
public class PnlBerichte extends CleanablePanel {

    public static final String internalClassID = "nursingrecords.reports";

    private static int speedSlow = 700;
    private static int speedFast = 500;

    private final int LAUFENDE_OPERATION_NICHTS = 0;
    private final int LAUFENDE_OPERATION_BERICHT_EINGABE = 1;
    private final int LAUFENDE_OPERATION_BERICHT_LOESCHEN = 2;
    private final int LAUFENDE_OPERATION_ELEMENT_ENTFERNEN = 3;
    private final int LAUFENDE_OPERATION_VORGANG_BEARBEITEN = 4;

    private int laufendeOperation;

    private Date von, bis;

    private Bewohner bewohner;
    private JPopupMenu menu;
    private boolean initPhase;
    private javax.swing.JFrame parent;
    /**
     * Dieser Actionlistener wird gebraucht, damit die einzelnen Menüpunkte des Kontextmenüs, nachdem sie
     * aufgerufen wurden, einen reloadTable() auslösen können.
     */
    private ActionListener fileActionListener;
    /**
     * Dies ist immer der zur Zeit ausgewählte Bericht. null, wenn nichts ausgewählt ist. Wenn mehr als ein
     * Bericht ausgewählt wurde, steht hier immer der Verweis auf den ERSTEN Bericht der Auswahl.
     */
    private Pflegeberichte bericht;
    private final int TAB_DATE = 0;
    private final int TAB_SEARCH = 1;
    private final int TAB_TAGS = 2;
    /**
     * Diese Liste enhtält die Menge der Tags, die im Suchfenster gesetzt wurden.
     */
    private ArrayList<PBerichtTAGS> tagFilter;

    /**
     * Creates new form PnlBerichte
     */
    public PnlBerichte(FrmPflege pflege, Bewohner bewohner) {
        this.initPhase = true;
        this.bewohner = bewohner;
        this.parent = pflege;
        this.bericht = null;

        initComponents();
        BewohnerTools.setBWLabel(lblBW, bewohner);

        //TODO: die RestoreStates müssen nach JPA gewandelt werden.
        SYSTools.restoreState(this.getClass().getName() + ":cbShowEdits", cbShowEdits);
        SYSTools.restoreState(this.getClass().getName() + ":cbTBIDS", cbTBIDS);

        jdcVon.setDate(SYSCalendar.addField(SYSCalendar.today_date(), -2, GregorianCalendar.WEEK_OF_MONTH));
        jdcVon.setMaxSelectableDate(SYSCalendar.today_date());
        jdcVon.getDateEditor().getUiComponent().addFocusListener(new FocusAdapter() {

            public void focusLost(java.awt.event.FocusEvent evt) {
                jdcFocusLost(evt);
            }
        });

        jdcBis.setDate(SYSCalendar.today_date());
        jdcBis.setMaxSelectableDate(SYSCalendar.today_date());
        jdcBis.getDateEditor().getUiComponent().addFocusListener(new FocusAdapter() {

            public void focusLost(java.awt.event.FocusEvent evt) {
                jdcFocusLost(evt);
            }
        });

        fileActionListener = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                reloadTable();
            }
        };

        tagFilter = new ArrayList<PBerichtTAGS>();
        prepareSearchArea();

        btnNew.setEnabled(OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.INSERT));

        this.initPhase = false;

        reloadTable();

    }

    private void jdcFocusLost(java.awt.event.FocusEvent evt) {
        if (this.initPhase) {
            return;
        }

        JTextFieldDateEditor jdc = (JTextFieldDateEditor) evt.getSource();

        if (jdc.getDate() == null) {
            jdc.setDate(SYSCalendar.addField(SYSCalendar.today_date(), -2, GregorianCalendar.WEEK_OF_MONTH));
        }
        jdc.firePropertyChange("date", 0, 0);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jToolBar2 = new JToolBar();
        btnNew = new JButton();
        btnPrint = new JButton();
        btnLogout = new JButton();
        cbShowEdits = new JCheckBox();
        cbTBIDS = new JCheckBox();
        lblBW = new JLabel();
        jspTblTB = new JScrollPane();
        tblTB = new JTable();
        pnlFilter = new JTabbedPane();
        jPanel4 = new JPanel();
        jdcVon = new JDateChooser();
        jdcBis = new JDateChooser();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        btnVonAnfangAn = new JButton();
        btn2Wochen = new JButton();
        btn4Wochen = new JButton();
        jPanel5 = new JPanel();
        txtSuche = new JTextField();
        btnSearch = new JButton();
        jPanel6 = new JPanel();
        jspFilter = new JScrollPane();
        scrollPane2 = new JScrollPane();
        taskSearch = new JXTaskPaneContainer();
        splitButtonsCenter = new JSplitPane();
        panel1 = new JPanel();
        panel2 = new JPanel();

        //======== this ========
        setLayout(new FormLayout(
            "$rgap, pref, $lcgap, default:grow, 0dlu, $rgap",
            "4*(fill:default, $lgap), 20dlu, $lgap, default"));

        //======== jToolBar2 ========
        {
            jToolBar2.setFloatable(false);

            //---- btnNew ----
            btnNew.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/filenew.png")));
            btnNew.setText("Neu");
            btnNew.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnNewHandler(e);
                }
            });
            jToolBar2.add(btnNew);

            //---- btnPrint ----
            btnPrint.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/fileprint.png")));
            btnPrint.setText("Drucken");
            btnPrint.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnPrintActionPerformed(e);
                }
            });
            jToolBar2.add(btnPrint);

            //---- btnLogout ----
            btnLogout.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/lock.png")));
            btnLogout.setText("Abmelden");
            btnLogout.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnLogoutbtnLogoutHandler(e);
                }
            });
            jToolBar2.add(btnLogout);

            //---- cbShowEdits ----
            cbShowEdits.setText("\u00c4nderungen anzeigen");
            cbShowEdits.setToolTipText("<html>Damit \u00c4nderungen und L\u00f6schungen trotzdem nachvollziehbar bleiben<br/>werden sie nur ausgeblendet. Mit diesem Schalter werden diese \u00c4nderungen wieder angezeigt.</html>");
            cbShowEdits.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cbShowEditsActionPerformed(e);
                }
            });
            jToolBar2.add(cbShowEdits);

            //---- cbTBIDS ----
            cbTBIDS.setText("Berichte-Nr. anzeigen");
            cbTBIDS.setToolTipText("<html>Jeder Bericht hat immer eine eindeutige Nummer.<br/>Diese Nummern werden im Alltag nicht ben\u00f6tigt.<br/>Sollten Sie diese Nummern dennoch sehen wollen<br/>dann schalten Sie diese hier ein.</html>");
            cbTBIDS.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cbTBIDSActionPerformed(e);
                }
            });
            jToolBar2.add(cbTBIDS);
        }
        add(jToolBar2, CC.xy(4, 1));

        //---- lblBW ----
        lblBW.setFont(new Font("Dialog", Font.BOLD, 18));
        lblBW.setForeground(new Color(255, 51, 0));
        lblBW.setText("jLabel3");
        add(lblBW, CC.xy(4, 3));

        //======== jspTblTB ========
        {
            jspTblTB.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    jspTblTBComponentResized(e);
                }
            });

            //---- tblTB ----
            tblTB.setModel(new DefaultTableModel(
                new Object[][] {
                    {null, null, null, null},
                    {null, null, null, null},
                    {null, null, null, null},
                    {null, null, null, null},
                },
                new String[] {
                    "Title 1", "Title 2", "Title 3", "Title 4"
                }
            ) {
                Class<?>[] columnTypes = new Class<?>[] {
                    Object.class, Object.class, Object.class, Object.class
                };
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    return columnTypes[columnIndex];
                }
            });
            tblTB.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    tblTBMousePressed(e);
                }
            });
            jspTblTB.setViewportView(tblTB);
        }
        add(jspTblTB, CC.xy(4, 7));

        //======== pnlFilter ========
        {
            pnlFilter.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    pnlFilterStateChanged(e);
                }
            });

            //======== jPanel4 ========
            {
                jPanel4.setBorder(new BevelBorder(BevelBorder.RAISED));

                //---- jdcVon ----
                jdcVon.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent e) {
                        jdcVonPropertyChange(e);
                    }
                });

                //---- jdcBis ----
                jdcBis.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent e) {
                        jdcBisPropertyChange(e);
                    }
                });

                //---- jLabel1 ----
                jLabel1.setText("Berichte anzeigen ab dem");

                //---- jLabel2 ----
                jLabel2.setText("Berichte anzeigen bis zum");

                //---- btnVonAnfangAn ----
                btnVonAnfangAn.setText("von Anfang an");
                btnVonAnfangAn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnVonAnfangAnActionPerformed(e);
                    }
                });

                //---- btn2Wochen ----
                btn2Wochen.setText("vor 2 Wochen");
                btn2Wochen.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btn2WochenActionPerformed(e);
                    }
                });

                //---- btn4Wochen ----
                btn4Wochen.setText("vor 4 Wochen");
                btn4Wochen.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btn4WochenActionPerformed(e);
                    }
                });

                GroupLayout jPanel4Layout = new GroupLayout(jPanel4);
                jPanel4.setLayout(jPanel4Layout);
                jPanel4Layout.setHorizontalGroup(
                    jPanel4Layout.createParallelGroup()
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel4Layout.createParallelGroup()
                                .addComponent(jLabel1)
                                .addComponent(jLabel2))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jdcBis, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jdcVon, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btn2Wochen)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btn4Wochen)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnVonAnfangAn)
                            .addContainerGap(246, Short.MAX_VALUE))
                );
                jPanel4Layout.setVerticalGroup(
                    jPanel4Layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel4Layout.createParallelGroup()
                                .addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                                .addComponent(jdcVon, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(btn2Wochen, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btn4Wochen)
                                    .addComponent(btnVonAnfangAn, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel4Layout.createParallelGroup()
                                .addComponent(jLabel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jdcBis, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addContainerGap())
                );
                jPanel4Layout.linkSize(SwingConstants.VERTICAL, new Component[] {btn2Wochen, btnVonAnfangAn, jLabel1, jdcVon});
                jPanel4Layout.linkSize(SwingConstants.VERTICAL, new Component[] {jLabel2, jdcBis});
            }
            pnlFilter.addTab("Zeiraum", jPanel4);


            //======== jPanel5 ========
            {

                //---- txtSuche ----
                txtSuche.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
                txtSuche.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        txtSucheActionPerformed(e);
                    }
                });

                //---- btnSearch ----
                btnSearch.setText("Los gehts");
                btnSearch.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnSearchActionPerformed(e);
                    }
                });

                GroupLayout jPanel5Layout = new GroupLayout(jPanel5);
                jPanel5.setLayout(jPanel5Layout);
                jPanel5Layout.setHorizontalGroup(
                    jPanel5Layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(txtSuche, GroupLayout.DEFAULT_SIZE, 872, Short.MAX_VALUE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnSearch)
                            .addContainerGap())
                );
                jPanel5Layout.setVerticalGroup(
                    jPanel5Layout.createParallelGroup()
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel5Layout.createParallelGroup()
                                .addComponent(txtSuche, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                                .addComponent(btnSearch, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE))
                            .addContainerGap())
                );
            }
            pnlFilter.addTab("Suchbegriff", jPanel5);


            //======== jPanel6 ========
            {

                GroupLayout jPanel6Layout = new GroupLayout(jPanel6);
                jPanel6.setLayout(jPanel6Layout);
                jPanel6Layout.setHorizontalGroup(
                    jPanel6Layout.createParallelGroup()
                        .addComponent(jspFilter, GroupLayout.DEFAULT_SIZE, 1023, Short.MAX_VALUE)
                );
                jPanel6Layout.setVerticalGroup(
                    jPanel6Layout.createParallelGroup()
                        .addComponent(jspFilter, GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                );
            }
            pnlFilter.addTab("Markierung", jPanel6);

        }
        add(pnlFilter, CC.xy(4, 5));

        //======== scrollPane2 ========
        {
            scrollPane2.setViewportView(taskSearch);
        }
        add(scrollPane2, CC.xywh(2, 5, 1, 3));

        //======== splitButtonsCenter ========
        {
            splitButtonsCenter.setOrientation(JSplitPane.VERTICAL_SPLIT);
            splitButtonsCenter.setDividerSize(0);

            //======== panel1 ========
            {
                panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));
            }
            splitButtonsCenter.setTopComponent(panel1);

            //======== panel2 ========
            {
                panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
            }
            splitButtonsCenter.setBottomComponent(panel2);
        }
        add(splitButtonsCenter, CC.xy(4, 9));
    }// </editor-fold>//GEN-END:initComponents

    private void txtSucheActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSucheActionPerformed
        if (!txtSuche.equals("")) {
            reloadTable();
        }
    }//GEN-LAST:event_txtSucheActionPerformed

    private void jdcVonPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jdcVonPropertyChange
        if (this.initPhase) {
            return;
        }
        if (!evt.getPropertyName().equals("date")) {
            return;
        }
        SYSCalendar.checkJDC((JDateChooser) evt.getSource());
        if (jdcBis.getDate().before(jdcVon.getDate())) {
            jdcVon.setDate(jdcBis.getDate());
        }
        reloadTable();
    }//GEN-LAST:event_jdcVonPropertyChange

    private void jspTblTBComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspTblTBComponentResized
        JScrollPane jsp = (JScrollPane) evt.getComponent();
        Dimension dim = jsp.getSize();
        // Größe der Text Spalte im TB ändern.
        // Summe der fixen Spalten  = 210 + ein bisschen
        int textWidth = dim.width - 200 - 50;
        TableColumnModel tcm1 = tblTB.getColumnModel();
        tcm1.getColumn(0).setPreferredWidth(200);
        tcm1.getColumn(1).setPreferredWidth(50);
        tcm1.getColumn(2).setPreferredWidth(textWidth);

        tcm1.getColumn(0).setHeaderValue("Datum");
        tcm1.getColumn(1).setHeaderValue("Info");
        tcm1.getColumn(2).setHeaderValue("Bericht");

    }//GEN-LAST:event_jspTblTBComponentResized

    private void btnLogoutbtnLogoutHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutbtnLogoutHandler
        OPDE.ocmain.lockOC();
    }//GEN-LAST:event_btnLogoutbtnLogoutHandler

    public void cleanup() {
        jdcVon.cleanup();
    }

    private void printBericht(int[] sel) {
        try {
            // Create temp file.
            File temp = File.createTempFile("pflegebericht", ".html");

            // Delete temp file when program exits.
            temp.deleteOnExit();

            // Write to temp file
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));

            TMPflegeberichte tm = (TMPflegeberichte) tblTB.getModel();
            out.write(SYSTools.htmlUmlautConversion(PflegeberichteTools.getBerichteAsHTML(SYSTools.getSelectionAsList(tm.getPflegeberichte(), sel))));

            out.close();
            SYSPrint.handleFile(parent, temp.getAbsolutePath(), Desktop.Action.OPEN);
        } catch (IOException e) {
        }
    }

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        printBericht(null);
    }//GEN-LAST:event_btnPrintActionPerformed

    private void btnNewHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewHandler
        new DlgBericht(parent, new Pflegeberichte(bewohner));
        reloadTable();
    }//GEN-LAST:event_btnNewHandler

    private void tblTBMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTBMousePressed
        Point p = evt.getPoint();
        ListSelectionModel lsm = tblTB.getSelectionModel();

        boolean singleRowSelected = lsm.getMaxSelectionIndex() == lsm.getMinSelectionIndex();

        int row = tblTB.rowAtPoint(p);
        if (singleRowSelected) {
            lsm.setSelectionInterval(row, row);
        }

        bericht = (Pflegeberichte) tblTB.getModel().getValueAt(lsm.getLeadSelectionIndex(), TMPflegeberichte.COL_BERICHT);
        boolean alreadyEdited = bericht.isDeleted() || bericht.isReplaced();
        boolean sameUser = bericht.getUser().equals(OPDE.getLogin().getUser());

        if (evt.isPopupTrigger()) {
            /**
             * KORRIGIEREN
             * Ein Bericht kann geändert werden (Korrektur)
             * - Wenn sie nicht im Übergabeprotokoll abgehakt wurde.
             */
            boolean bearbeitenMöglich = !alreadyEdited && singleRowSelected && bericht.getUsersAcknowledged().isEmpty();

            SYSTools.unregisterListeners(menu);
            menu = new JPopupMenu();

            // KORRIGIEREN
            JMenuItem itemPopupEdit = new JMenuItem("Korrigieren");
            itemPopupEdit.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    new DlgBericht(parent, bericht);
                    reloadTable();
                }
            });
            menu.add(itemPopupEdit);

            JMenuItem itemPopupDelete = new JMenuItem("Löschen");
            itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if (JOptionPane.showConfirmDialog(parent, "Möchten Sie diesen Eintrag wirklich löschen ?",
                            "Bericht löschen ?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        PflegeberichteTools.deleteBericht(bericht);
                        reloadTable();
                    }
                }
            });
            menu.add(itemPopupDelete);

            // #0000039
            JMenuItem itemPopupPrint = new JMenuItem("Markierte Berichte drucken");
            itemPopupPrint.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    int[] sel = tblTB.getSelectedRows();
                    printBericht(sel);
                }
            });
            menu.add(itemPopupPrint);

            itemPopupEdit.setEnabled(OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE));
            itemPopupDelete.setEnabled(OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.CANCEL));

            // Nur anzeigen wenn derselbe User die Änderung versucht, der auch den Text geschrieben hat.
            if (bearbeitenMöglich && (sameUser || OPDE.isAdmin())) {
                menu.add(PBerichtTAGSTools.createMenuForTags(bericht));
            }


            if (OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.SELECT) && !alreadyEdited && singleRowSelected) {
                menu.add(new JSeparator());
                menu.add(SYSFilesTools.getSYSFilesContextMenu(parent, bericht, fileActionListener));
            }

            menu.add(new JSeparator());
            menu.add(VorgaengeTools.getVorgangContextMenu(parent, bericht, bewohner));

//            if (!singleRowSelected){
//                int[] sel = tblTB.getSelectedRows();
//                long[] ids = (long[])Array.newInstance(long.class, 2);
//                for (int i = 0; i < sel.length; i++){
//                    ids[i] = (Long) tm.getValueAt(sel[i], TMBerichte.COL_TBID);
//                }
//                menu.add(new JSeparator());
//                // #0000003
//                menu.add(op.share.vorgang.DBHandling.getVorgangContextMenu(parent, "Tagesberichte", ids, currentBW, fileActionListener));
//
//                // #0000035
//                menu.add(SYSFiles.getOPFilesContextMenu(parent, "Tagesberichte", selectedTBID, currentBW, tblTB, true, true, SYSFiles.CODE_BERICHTE, fileActionListener));
//            }

            menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
        }
    }//GEN-LAST:event_tblTBMousePressed

    private void jdcBisPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jdcBisPropertyChange
        if (this.initPhase) {
            return;
        }
        if (!evt.getPropertyName().equals("date")) {
            return;
        }
        SYSCalendar.checkJDC((JDateChooser) evt.getSource());
        if (jdcBis.getDate().before(jdcVon.getDate())) {
            jdcVon.setDate(jdcBis.getDate());
        }
        reloadTable();
    }//GEN-LAST:event_jdcBisPropertyChange

    private void cbShowEditsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbShowEditsActionPerformed
        if (this.initPhase) {
            return;
        }
        SYSTools.storeState(this.getClass().getName() + ":cbShowEdits", cbShowEdits);
        reloadTable();
    }//GEN-LAST:event_cbShowEditsActionPerformed

    private void cbTBIDSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbTBIDSActionPerformed
        if (this.initPhase) {
            return;
        }
        SYSTools.storeState(this.getClass().getName() + ":cbTBIDS", cbTBIDS);
        reloadTable();
    }//GEN-LAST:event_cbTBIDSActionPerformed

    private void btnVonAnfangAnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVonAnfangAnActionPerformed
        jdcVon.setDate(PflegeberichteTools.firstBericht(bewohner).getPit());
    }//GEN-LAST:event_btnVonAnfangAnActionPerformed

    private void btn2WochenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn2WochenActionPerformed
        jdcVon.setDate(SYSCalendar.addField(new Date(), -2, GregorianCalendar.WEEK_OF_MONTH));
    }//GEN-LAST:event_btn2WochenActionPerformed

    private void pnlFilterStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_pnlFilterStateChanged
        if (pnlFilter.getSelectedIndex() == TAB_SEARCH) {
            txtSuche.requestFocus();
        }
    }//GEN-LAST:event_pnlFilterStateChanged

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        if (!txtSuche.equals("")) {
            reloadTable();
        }
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btn4WochenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn4WochenActionPerformed
        jdcVon.setDate(SYSCalendar.addField(new Date(), -4, GregorianCalendar.WEEK_OF_MONTH));
    }//GEN-LAST:event_btn4WochenActionPerformed

    protected void prepareSearchArea() {

        addByTime();
        addByTags();
    }


    private void addByTags() {
        JXTaskPane panel = new JXTaskPane("nach Markierung");
        PBerichtTAGSTools.addCheckBoxPanelForTags(panel, new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                JCheckBox cb = (JCheckBox) e.getSource();
                // Ich benutze hier die ClientProperty Map um die Entities dem Listener mitzugeben.
                // Das war wohl nicht so gedacht. Aber es geht trotzdem.
                PBerichtTAGS tag = (PBerichtTAGS) cb.getClientProperty("UserObject");
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    tagFilter.remove(tag);
                } else {
                    tagFilter.add(tag);
                }
                reloadTable();
            }
        }, new ArrayList<PBerichtTAGS>());

        panel.setCollapsed(true);
        taskSearch.add((JPanel) panel);

    }

    private void addByTime() {

        JXTaskPane panel = new JXTaskPane("nach Zeit");
        final JDateChooser jdcVon = new JDateChooser(SYSCalendar.addField(new Date(), -2, GregorianCalendar.WEEK_OF_MONTH));
        jdcVon.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (initPhase) {
                    return;
                }
                if (!evt.getPropertyName().equals("date")) {
                    return;
                }
                reloadTable();
            }
        });
        panel.add(new JXTitledSeparator("Von"));
        panel.add(jdcVon);
        panel.add(new AbstractAction() {
            {
                putValue(Action.NAME, "vor 2 Wochen");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                jdcVon.setDate(SYSCalendar.addField(new Date(), -2, GregorianCalendar.WEEK_OF_MONTH));
            }
        });

        panel.add(new AbstractAction() {
            {
                putValue(Action.NAME, "vor 4 Wochen");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                jdcVon.setDate(SYSCalendar.addField(new Date(), -4, GregorianCalendar.WEEK_OF_MONTH));
            }
        });


        panel.add(new AbstractAction() {
            {
                putValue(Action.NAME, "von Anfang an");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                jdcVon.setDate(PflegeberichteTools.firstBericht(bewohner).getPit());
            }
        });

        final JDateChooser jdcBis = new JDateChooser(new Date());
        jdcBis.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (initPhase) {
                    return;
                }
                if (!evt.getPropertyName().equals("date")) {
                    return;
                }
                bis = jdcBis.getDate();
                reloadTable();
            }
        });
        panel.add(new JLabel(" "));
        panel.add(new JXTitledSeparator("Bis"));
        panel.add(jdcBis);
        panel.add(new AbstractAction() {
            {
                putValue(Action.NAME, "heute");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                jdcVon.setDate(new Date());
            }
        });


        taskSearch.add((JPanel) panel);
    }

    private void reloadTable() {
        if (initPhase) {
            return;
        }

        Query query = null;

        String tags = "";
        Iterator<PBerichtTAGS> it = tagFilter.iterator();
        while (it.hasNext()) {
            tags += Long.toString(it.next().getPbtagid());
            tags += (it.hasNext() ? "," : "");
        }

        String search = txtSuche.getText().trim();

        query = OPDE.getEM().createQuery(" "
                + " SELECT p FROM Pflegeberichte p "
                + (tags.isEmpty() ? "" : " JOIN p.tags t ")
                + " WHERE p.bewohner = :bewohner "
                + " AND p.pit >= :von AND p.pit <= :bis "
                + (search.isEmpty() ? "" : " p.text like :search ")
                + (tags.isEmpty() ? "" : " AND t.pbtagid IN (" + tags + ")")
                + (cbShowEdits.isSelected() ? "" : " AND p.editedBy is null ")
                + " ORDER BY p.pit DESC ");
        query.setParameter("bewohner", bewohner);
        query.setParameter("von", von);
        query.setParameter("bis", bis);

        if (!search.isEmpty()) {
            query.setParameter("search", "%" + search + "%");
        }
        tblTB.setModel(new TMPflegeberichte(query, cbTBIDS.isSelected()));

        btnPrint.setEnabled(tblTB.getModel().getRowCount() > 0 && OPDE.getInternalClasses().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.PRINT));

        tblTB.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        jspTblTB.dispatchEvent(new ComponentEvent(jspTblTB, ComponentEvent.COMPONENT_RESIZED));

        tblTB.getColumnModel().getColumn(0).setCellRenderer(new RNDBerichte());
        tblTB.getColumnModel().getColumn(1).setCellRenderer(new RNDBerichte());
        tblTB.getColumnModel().getColumn(2).setCellRenderer(new RNDBerichte());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JToolBar jToolBar2;
    private JButton btnNew;
    private JButton btnPrint;
    private JButton btnLogout;
    private JCheckBox cbShowEdits;
    private JCheckBox cbTBIDS;
    private JLabel lblBW;
    private JScrollPane jspTblTB;
    private JTable tblTB;
    private JTabbedPane pnlFilter;
    private JPanel jPanel4;
    private JDateChooser jdcVon;
    private JDateChooser jdcBis;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JButton btnVonAnfangAn;
    private JButton btn2Wochen;
    private JButton btn4Wochen;
    private JPanel jPanel5;
    private JTextField txtSuche;
    private JButton btnSearch;
    private JPanel jPanel6;
    private JScrollPane jspFilter;
    private JScrollPane scrollPane2;
    private JXTaskPaneContainer taskSearch;
    private JSplitPane splitButtonsCenter;
    private JPanel panel1;
    private JPanel panel2;
    // End of variables declaration//GEN-END:variables
}
