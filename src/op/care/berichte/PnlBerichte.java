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

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import entity.Bewohner;
import entity.PBerichtTAGS;
import entity.PBerichtTAGSTools;
import entity.Pflegeberichte;
import entity.PflegeberichteTools;
import entity.SYSFilesTools;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import javax.persistence.Query;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import op.OPDE;
import op.care.CleanablePanel;
import op.care.FrmPflege;
import op.tools.InternalClassACL;
import op.tools.SYSCalendar;
import op.tools.SYSPrint;
import op.tools.SYSTools;

/**
 *
 * @author  root
 */
public class PnlBerichte extends CleanablePanel {

    public static final String internalClassID = "nursingrecords.reports";
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

    /** Creates new form PnlBerichte */
    public PnlBerichte(FrmPflege pflege) {
        this.initPhase = true;
        bewohner = pflege.getBewohner();
        this.parent = pflege;
        this.bericht = null;

        initComponents();
        if (pflege.bwlabel == null) {
            SYSTools.setBWLabel(lblBW, bewohner);
            pflege.bwlabel = lblBW;
        } else {
            lblBW.setText(pflege.bwlabel.getText());
            lblBW.setToolTipText(pflege.bwlabel.getToolTipText());
        }
        //TODO: die RestoreStates müssen nach nach JPA gewandelt werden.
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
        jspFilter.setViewportView(PBerichtTAGSTools.createCheckBoxPanelForTags(new ItemListener() {

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
        }, new ArrayList<PBerichtTAGS>(), new GridLayout(3, 4)));
        
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

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgSuche = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        cbShowEdits = new javax.swing.JCheckBox();
        cbTBIDS = new javax.swing.JCheckBox();
        lblBW = new javax.swing.JLabel();
        jspTblTB = new javax.swing.JScrollPane();
        tblTB = new javax.swing.JTable();
        pnlFilter = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jdcVon = new com.toedter.calendar.JDateChooser();
        jdcBis = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnVonAnfangAn = new javax.swing.JButton();
        btn2Wochen = new javax.swing.JButton();
        btn4Wochen = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        txtSuche = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jspFilter = new javax.swing.JScrollPane();

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jToolBar2.setFloatable(false);

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/filenew.png"))); // NOI18N
        btnNew.setText("Neu");
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewHandler(evt);
            }
        });
        jToolBar2.add(btnNew);

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/fileprint.png"))); // NOI18N
        btnPrint.setText("Drucken");
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jToolBar2.add(btnPrint);

        btnLogout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/lock.png"))); // NOI18N
        btnLogout.setText("Abmelden");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutbtnLogoutHandler(evt);
            }
        });
        jToolBar2.add(btnLogout);

        cbShowEdits.setText("Änderungen anzeigen");
        cbShowEdits.setToolTipText("<html>Damit Änderungen und Löschungen trotzdem nachvollziehbar bleiben<br/>werden sie nur ausgeblendet. Mit diesem Schalter werden diese Änderungen wieder angezeigt.</html>");
        cbShowEdits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbShowEditsActionPerformed(evt);
            }
        });
        jToolBar2.add(cbShowEdits);

        cbTBIDS.setText("Berichte-Nr. anzeigen");
        cbTBIDS.setToolTipText("<html>Jeder Bericht hat immer eine eindeutige Nummer.<br/>Diese Nummern werden im Alltag nicht benötigt.<br/>Sollten Sie diese Nummern dennoch sehen wollen<br/>dann schalten Sie diese hier ein.</html>");
        cbTBIDS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbTBIDSActionPerformed(evt);
            }
        });
        jToolBar2.add(cbTBIDS);

        lblBW.setFont(new java.awt.Font("Dialog", 1, 18));
        lblBW.setForeground(new java.awt.Color(255, 51, 0));
        lblBW.setText("jLabel3");

        jspTblTB.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jspTblTBComponentResized(evt);
            }
        });

        tblTB.setModel(new javax.swing.table.DefaultTableModel(
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
        tblTB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblTBMousePressed(evt);
            }
        });
        jspTblTB.setViewportView(tblTB);

        pnlFilter.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                pnlFilterStateChanged(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jdcVon.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jdcVonPropertyChange(evt);
            }
        });

        jdcBis.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jdcBisPropertyChange(evt);
            }
        });

        jLabel1.setText("Berichte anzeigen ab dem");

        jLabel2.setText("Berichte anzeigen bis zum");

        btnVonAnfangAn.setText("von Anfang an");
        btnVonAnfangAn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVonAnfangAnActionPerformed(evt);
            }
        });

        btn2Wochen.setText("vor 2 Wochen");
        btn2Wochen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn2WochenActionPerformed(evt);
            }
        });

        btn4Wochen.setText("vor 4 Wochen");
        btn4Wochen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn4WochenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jdcBis, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jdcVon, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn2Wochen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn4Wochen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnVonAnfangAn)
                .addContainerGap(442, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                    .addComponent(jdcVon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn2Wochen, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn4Wochen)
                        .addComponent(btnVonAnfangAn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jdcBis, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn2Wochen, btnVonAnfangAn, jLabel1, jdcVon});

        jPanel4Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel2, jdcBis});

        pnlFilter.addTab("Zeiraum", jPanel4);

        txtSuche.setFont(new java.awt.Font("Lucida Grande", 0, 18));
        txtSuche.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSucheActionPerformed(evt);
            }
        });

        btnSearch.setText("Los gehts");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtSuche, javax.swing.GroupLayout.DEFAULT_SIZE, 1017, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSearch)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSuche, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                    .addComponent(btnSearch, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
                .addContainerGap())
        );

        pnlFilter.addTab("Suchbegriff", jPanel5);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jspFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 1117, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jspFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
        );

        pnlFilter.addTab("Markierung", jPanel6);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jspTblTB, javax.swing.GroupLayout.DEFAULT_SIZE, 1117, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnlFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 1117, Short.MAX_VALUE))
                    .addComponent(jToolBar2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 1123, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(lblBW, javax.swing.GroupLayout.DEFAULT_SIZE, 1111, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblBW)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jspTblTB, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                .addContainerGap())
        );
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

    private void reloadTable() {
        if (initPhase) {
            return;
        }

        Query query = null;

        switch (pnlFilter.getSelectedIndex()) {
            case TAB_DATE: {
                query = cbShowEdits.isSelected() ? OPDE.getEM().createNamedQuery("Pflegeberichte.findByBewohnerWithinPeriod") : OPDE.getEM().createNamedQuery("Pflegeberichte.findByBewohnerWithinPeriodWithoutEdits");
                query.setParameter("von", new Date(SYSCalendar.startOfDay(jdcVon.getDate())));
                query.setParameter("bis", new Date(SYSCalendar.endOfDay(jdcBis.getDate())));
                query.setParameter("bewohner", bewohner);
                break;
            }
            case TAB_SEARCH: {
                query = cbShowEdits.isSelected() ? OPDE.getEM().createNamedQuery("Pflegeberichte.findByBewohnerAndSearchText") : OPDE.getEM().createNamedQuery("Pflegeberichte.findByBewohnerAndSearchTextWithoutEdits");
                query.setParameter("search", "%" + txtSuche.getText() + "%");
                query.setParameter("bewohner", bewohner);
                break;
            }
            case TAB_TAGS: {
                String tags = "";
                Iterator<PBerichtTAGS> it = tagFilter.iterator();
                while (it.hasNext()) {
                    tags += Long.toString(it.next().getPbtagid());
                    tags += (it.hasNext() ? "," : "");
                }
                if (!tags.isEmpty()) {
                    query = OPDE.getEM().createQuery(" "
                            + " SELECT p FROM Pflegeberichte p "
                            + " JOIN p.tags t "
                            + " WHERE p.bewohner = :bewohner "
                            + " AND t.pbtagid IN (" + tags + ")"
                            + (cbShowEdits.isSelected() ? "" : " AND p.editedBy is null ")
                            + " ORDER BY p.pit DESC ");
                    query.setParameter("bewohner", bewohner);
                }
                break;
            }
            default: {
                break;
            }
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
    private javax.swing.ButtonGroup bgSuche;
    private javax.swing.JButton btn2Wochen;
    private javax.swing.JButton btn4Wochen;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnVonAnfangAn;
    private javax.swing.JCheckBox cbShowEdits;
    private javax.swing.JCheckBox cbTBIDS;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JToolBar jToolBar2;
    private com.toedter.calendar.JDateChooser jdcBis;
    private com.toedter.calendar.JDateChooser jdcVon;
    private javax.swing.JScrollPane jspFilter;
    private javax.swing.JScrollPane jspTblTB;
    private javax.swing.JLabel lblBW;
    private javax.swing.JTabbedPane pnlFilter;
    private javax.swing.JTable tblTB;
    private javax.swing.JTextField txtSuche;
    // End of variables declaration//GEN-END:variables
}
