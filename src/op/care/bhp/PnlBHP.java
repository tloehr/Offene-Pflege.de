/*
 * OffenePflege
 * Copyright (C) 2008 Torsten L�hr
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
 * Auf deutsch (freie �bersetzung. Rechtlich gilt die englische Version)
 * Dieses Programm ist freie Software. Sie k�nnen es unter den Bedingungen der GNU General Public License, 
 * wie von der Free Software Foundation ver�ffentlicht, weitergeben und/oder modifizieren, gem�� Version 2 der Lizenz.
 *
 * Die Ver�ffentlichung dieses Programms erfolgt in der Hoffnung, da� es Ihnen von Nutzen sein wird, aber 
 * OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT F�R EINEN 
 * BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht, 
 * schreiben Sie an die Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA.
 * 
 */
package op.care.bhp;

import com.toedter.calendar.JDateChooser;
import entity.SYSRunningClasses;
import entity.SYSRunningClassesTools;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import op.OCSec;
import op.OPDE;
import op.care.CleanablePanel;
import op.care.FrmPflege;
import op.tools.DBHandling;
import op.tools.DBRetrieve;
import op.tools.DlgException;
import op.tools.SYSCalendar;
import op.tools.SYSTools;

/**
 *
 * @author  tloehr
 */
public class PnlBHP extends CleanablePanel {

    public static final String internalClassID = "nursingrecords.bhp";
    String bwkennung;
    JPopupMenu menu;
    private FrmPflege parent;
    //private String classname;
    private OCSec ocs;
    private boolean ignoreJDCEvent;
    //private boolean ignoreSchichtEvent;
    //private long[] ocwo;
    private boolean readOnly;
    private boolean abwesend;
    private SYSRunningClasses runningClass, blockingClass;

    /** Creates ner form PnlBHP */
    public PnlBHP(FrmPflege parent, String bwkennung) {
        this.parent = parent;
        this.bwkennung = bwkennung;

        ocs = OPDE.getOCSec();
        initComponents();
        initPanel();

    }

    private void initPanel() {
        SYSRunningClasses[] result = SYSRunningClassesTools.moduleStarted(internalClassID, parent.getBewohner().getBWKennung(), SYSRunningClasses.STATUS_RW);
        runningClass = result[0];

        cmbSchicht.setModel(new DefaultComboBoxModel(new String[]{"Alles", "Nacht, fr�h morgens", "Fr�h", "Sp�t", "Nacht, sp�t abends"}));

        abwesend = DBRetrieve.getAbwesendSeit(bwkennung) != null;
        if (runningClass.isRW()) {
            btnLock.setEnabled(false);
            btnLock.setToolTipText(null);
        } else {
            blockingClass = result[1];
            btnLock.setEnabled(true);
            btnLock.setToolTipText("<html><body><h3>Dieser Datensatz ist belegt durch:</h3>"
                    + blockingClass.getLogin().getUser().getNameUndVorname()
                    + "</body></html>");
        }
        ocs.setEnabled(this, "btnBedarf", btnBedarf, !readOnly);

        ignoreJDCEvent = true;
        jdcDatum.setDate(SYSCalendar.today_date());
        ArrayList hauf = DBRetrieve.getHauf(bwkennung);
        Date[] d = (Date[]) hauf.get(0);
        jdcDatum.setMinSelectableDate(d[0]);
        if (parent.bwlabel == null) {
            SYSTools.setBWLabel(lblBW, this.bwkennung);
            parent.bwlabel = lblBW;
        } else {
            lblBW.setText(parent.bwlabel.getText());
            lblBW.setToolTipText(parent.bwlabel.getToolTipText());
        }
        //SYSTools.setBWLabel(lblBW, bwkennung);
        ignoreJDCEvent = false;
        cmbSchicht.setSelectedIndex(SYSCalendar.ermittleSchicht() + 1);
    }

    public void cleanup() {
        jdcDatum.cleanup();
        SYSTools.unregisterListeners(this);
        SYSRunningClassesTools.moduleEnded(runningClass);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jspBHP = new javax.swing.JScrollPane();
        tblBHP = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        btnNow = new javax.swing.JButton();
        btnForward = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();
        btnTop = new javax.swing.JButton();
        jdcDatum = new com.toedter.calendar.JDateChooser();
        cmbSchicht = new javax.swing.JComboBox();
        btnBedarf = new javax.swing.JButton();
        lblBW = new javax.swing.JLabel();
        btnLock = new javax.swing.JButton();
        jToolBar1 = new javax.swing.JToolBar();
        btnLogout = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();

        jspBHP.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jspBHP.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jspBHPComponentResized(evt);
            }
        });

        tblBHP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblBHPMousePressed(evt);
            }
        });
        jspBHP.setViewportView(tblBHP);

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnNow.setBackground(java.awt.Color.white);
        btnNow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/history.png"))); // NOI18N
        btnNow.setBorder(null);
        btnNow.setBorderPainted(false);
        btnNow.setOpaque(false);
        btnNow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNowActionPerformed(evt);
            }
        });

        btnForward.setBackground(java.awt.Color.white);
        btnForward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/1rightarrow.png"))); // NOI18N
        btnForward.setBorder(null);
        btnForward.setBorderPainted(false);
        btnForward.setOpaque(false);
        btnForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnForwardActionPerformed(evt);
            }
        });

        btnBack.setBackground(java.awt.Color.white);
        btnBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/1leftarrow.png"))); // NOI18N
        btnBack.setBorder(null);
        btnBack.setBorderPainted(false);
        btnBack.setOpaque(false);
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        btnTop.setBackground(java.awt.Color.white);
        btnTop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/16x16/2leftarrow.png"))); // NOI18N
        btnTop.setBorder(null);
        btnTop.setBorderPainted(false);
        btnTop.setOpaque(false);
        btnTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTopActionPerformed(evt);
            }
        });

        jdcDatum.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jdcDatumPropertyChange(evt);
            }
        });

        cmbSchicht.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbSchichtItemStateChanged(evt);
            }
        });

        btnBedarf.setText("Bei Bedarf");
        btnBedarf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBedarfActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jdcDatum, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnTop, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBack)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnForward)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnNow, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 407, Short.MAX_VALUE)
                .addComponent(btnBedarf)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbSchicht, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmbSchicht, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnBedarf))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btnNow, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnForward, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnBack, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnTop, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jdcDatum, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblBW.setFont(new java.awt.Font("Dialog", 1, 18));
        lblBW.setForeground(new java.awt.Color(255, 51, 0));
        lblBW.setText("Nachname, Vorname (*GebDatum, 00 Jahre) [??1]");

        btnLock.setBackground(new java.awt.Color(255, 255, 255));
        btnLock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/encrypted.png"))); // NOI18N
        btnLock.setBorder(null);
        btnLock.setBorderPainted(false);
        btnLock.setOpaque(false);
        btnLock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLockActionPerformed(evt);
            }
        });

        jToolBar1.setFloatable(false);

        btnLogout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/lock.png"))); // NOI18N
        btnLogout.setText("Abmelden");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutbtnLogoutHandler(evt);
            }
        });
        jToolBar1.add(btnLogout);

        jLabel12.setText("<html>Hinweis: &frac14; = 0,25 | <sup>1</sup>/<sub>3</sub> = 0,33 | &frac12; = 0,5 | &frac34; = 0,75</html>");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 809, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblBW, javax.swing.GroupLayout.DEFAULT_SIZE, 751, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLock)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jspBHP, javax.swing.GroupLayout.DEFAULT_SIZE, 785, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 785, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnLock)
                    .addComponent(lblBW))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jspBHP, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    private void btnLogoutbtnLogoutHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutbtnLogoutHandler
        OPDE.ocmain.lockOC();
    }//GEN-LAST:event_btnLogoutbtnLogoutHandler

    private void btnLockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLockActionPerformed
        initPanel();
    }//GEN-LAST:event_btnLockActionPerformed

    private void btnForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnForwardActionPerformed
        jdcDatum.setDate(SYSCalendar.addDate(jdcDatum.getDate(), 1));
    }//GEN-LAST:event_btnForwardActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        jdcDatum.setDate(SYSCalendar.addDate(jdcDatum.getDate(), -1));
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTopActionPerformed
        jdcDatum.setDate(jdcDatum.getMinSelectableDate());
    }//GEN-LAST:event_btnTopActionPerformed

    private void btnNowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNowActionPerformed
        jdcDatum.setDate(SYSCalendar.today_date());
    }//GEN-LAST:event_btnNowActionPerformed

    private void jdcDatumPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jdcDatumPropertyChange
        if (!evt.getPropertyName().equals("date") || ignoreJDCEvent) {
            return;
        }
        ignoreJDCEvent = true;
        SYSCalendar.checkJDC((JDateChooser) evt.getSource());
        ignoreJDCEvent = false;
        reloadTable();
    }//GEN-LAST:event_jdcDatumPropertyChange

    private void btnBedarfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBedarfActionPerformed
        new DlgBedarf(parent, bwkennung);
        reloadTable();
    }//GEN-LAST:event_btnBedarfActionPerformed

    private void cmbSchichtItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbSchichtItemStateChanged
        if (evt.getStateChange() != ItemEvent.SELECTED) {
            return;
        }
        reloadTable();
    }//GEN-LAST:event_cmbSchichtItemStateChanged

    private void tblBHPMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBHPMousePressed
        if (readOnly || !ocs.isAccessible(this, "tblBHP")) {
            return;
        } // Hier d�rfen nur Examen dran.
        final TMBHP tm = (TMBHP) tblBHP.getModel();
        if (tm.getRowCount() == 0) {
            return;
        }
        Point p = evt.getPoint();
        final int col = tblBHP.columnAtPoint(p);
        final int row = tblBHP.rowAtPoint(p);
        ListSelectionModel lsm = tblBHP.getSelectionModel();
        lsm.setSelectionInterval(row, row);
        final long bhpid = ((Long) tm.getValueAt(row, TMBHP.COL_BHPID)).longValue();
        int status = ((Integer) tm.getValueAt(row, TMBHP.COL_STATUS)).intValue();
        final double dosis = ((Double) tm.getValueAt(row, TMBHP.COL_DOSIS)).doubleValue();
        String ukennung = ((String) tm.getValueAt(row, TMBHP.COL_UKENNUNG)).toString();
        long mdate = ((Long) tm.getValueAt(row, TMBHP.COL_MDATE)).longValue();
        long abdatum = ((Long) tm.getValueAt(row, TMBHP.COL_ABDATUM)).longValue();
        final long dafid = ((Long) tm.getValueAt(row, TMBHP.COL_DAFID)).longValue();
        boolean bedarf = ((Long) tm.getValueAt(row, TMBHP.COL_SITID)).longValue() > 0;
        //long verid = ((Long) tm.getValueAt(row, TMBHP.COL_VERID)).longValue();

        //boolean bisPackEnde = ((Boolean) tm.getValueAt(row, TMBHP.COL_BISPACKENDE)).booleanValue();

        boolean changeable =
                // Diese Kontrolle stellt sicher, dass ein User nur seine eigenen Eintr�ge und das auch nur
                // eine halbe Stunde lang bearbeiten kann.
                // Ausserdem kann man nur dann etwas geben, wenn es
                // a) eine Massnahmen ohne Medikation ist
                // ODER
                // (
                //      b) ein angebrochener Bestand vorhanden ist
                //      UND
                //      c)  das h�kchen NICHT gesetzt ist oder wenn es gesetzt ist, kann man es 
                //          nur dann wieder wegnehmen, wenn es derselbe Benutzer FR�H GENUG tut.
                //          Und auch nur dann, wenn 
                // )
                !abwesend && //
                // Aus Performance Gr�nden muss er Ausdruck, der hier bereits ermittelt wurde
                // an anderer Stelle verarbeitet werden, sonst dauert die SQL Abfrage zu lang
                // Urspr�nglich stand hier:
                // (dafid == 0 || status != TMBHP.STATUS_OFFEN || bestid > 0) && // wenn es ein Medikament ist und der Status offen, dann nur �nderbar, wenn es einen angebrochenen Bestand gibt.
                // Diese Abfrage habe ich nach Unten verschoben und das bestid muss nun im Einzelfall ermittelt werden.
                SYSCalendar.isInFuture(abdatum)
                && (status == TMBHP.STATUS_OFFEN
                || (ukennung.equalsIgnoreCase(OPDE.getLogin().getUser().getUKennung())
                && SYSCalendar.earlyEnough(mdate, 30) && !op.care.med.DBHandling.betrifftAbgeschlossenenBestand(bhpid))); // damit man nichts r�ckg�ngig machen kann, was irgendwie einen abgeschlossenen Bestand betrifft.
        OPDE.getLogger().debug(changeable ? "changeable" : "NOT changeable");
        if (changeable) {
            // Dr�ckt auch wirklich mit der LINKEN Maustaste auf die mittlere Spalte.
            if (!evt.isPopupTrigger() && col == TMBHP.COL_STATUS) {

                // wenn es ein Medikament ist und der Status offen, dann nur �nderbar, wenn es einen angebrochenen Bestand gibt.
                // Etwas umst�ndlich, aus Optimierungsgr�nden
                long vorid = op.care.med.DBHandling.getVorrat2DAF(bwkennung, dafid);
                long bestid = op.care.med.DBHandling.getBestandImAnbruch(vorid);
                boolean changeable_additional = (dafid == 0 || status != TMBHP.STATUS_OFFEN || bestid > 0);
                if (changeable_additional) {

                    boolean fullReloadNecessary = false;
                    status++;
                    if (status > 1) {
                        status = TMBHP.STATUS_OFFEN;
                    }
                    HashMap hm = new HashMap();
                    hm.put("Status", status);
                    if (status == TMBHP.STATUS_OFFEN) {
                        hm.put("UKennung", null);
                        hm.put("Ist", null);
                        hm.put("IZeit", null);
                        hm.put("Bemerkung", null);
                    } else {
                        hm.put("UKennung", OPDE.getLogin().getUser().getUKennung());
                        hm.put("Ist", "!NOW!");
                        hm.put("IZeit", SYSCalendar.ermittleZeit());
                    }

                    // Transaktion
                    Connection db = OPDE.getDb().db;
                    try {

//                    // Hier beginnt eine Transaktion
                        db.setAutoCommit(false);
                        db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                        db.commit();

                        DBHandling.updateRecord("BHP", hm, "BHPID", bhpid);

                        if (dafid > 0) { // mit Medikamenten
                            if (status == TMBHP.STATUS_ERLEDIGT) {
                                if (!op.care.med.DBHandling.entnahmeVorrat(dafid, bwkennung, dosis, true, bhpid)) {
                                    throw new SQLException("entnahmeVorrat");
                                }
                            } else {
                                if (!op.care.med.DBHandling.r�ckgabeVorrat(bhpid)) {
                                    throw new SQLException("r�ckgabeVorrat");
                                }
                            }
                        }

                        // Wenn man eine Massnahme aus der Bedarfsmedikation
                        // r�ckg�ngig macht, wird sie gel�scht.
                        if (bedarf && status == 0) {
                            if (DBHandling.deleteRecords("BHP", "BHPID", bhpid) < 0) {
                                throw new SQLException("Bedarfsmedikation l�schen");
                            }
                            fullReloadNecessary = true;
                        }

                        db.commit();
                        db.setAutoCommit(true);

                    } catch (SQLException ex) {
                        try {
                            ex.printStackTrace();
                            db.rollback();
                        } catch (SQLException ex1) {
                            new DlgException(ex1);
                            ex1.printStackTrace();
                            System.exit(1);
                        }
                        new DlgException(ex);
                    }

                    tm.setUpdate(row, status);

                    if (fullReloadNecessary) {
                        //OPDE.getLogger().debug("fullReloadNecessary");
                        reloadTable();
                    }
                }
            }

        }
        // Nun noch Men�eintr�ge
        if (evt.isPopupTrigger()) {
            SYSTools.unregisterListeners(menu);
            menu = new JPopupMenu();

            if (dafid > 0) {

//                JMenuItem itemPopupCloseBestand = new JMenuItem("Bestand abschlie�en");
//                itemPopupCloseBestand.addActionListener(new java.awt.event.ActionListener() {
//
//                    public void actionPerformed(java.awt.event.ActionEvent evt) {
//                        try {
//                            new DlgBestandAbschliessen(parent, bestid);
//                            Thread.sleep(1000);
//                            reloadTable();
//                        } catch (InterruptedException ex) {
//                            Logger.getLogger(PnlBHP.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                    }
//                });
//                menu.add(itemPopupCloseBestand);
//                ocs.setEnabled(this, "itemPopupCloseBestand", itemPopupCloseBestand, !readOnly && nextbest == 0 && bestid > 0);
//
//                JMenuItem itemPopupOpenBestand = new JMenuItem("Bestand anbrechen");
//                itemPopupOpenBestand.addActionListener(new java.awt.event.ActionListener() {
//
//                    public void actionPerformed(java.awt.event.ActionEvent evt) {
//                        try {
//                            new DlgBestandAnbrechen(parent, dafid, bwkennung);
//                            Thread.sleep(1000);
//                            reloadTable();
//                        } catch (InterruptedException ex) {
//                            Logger.getLogger(PnlBHP.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                    }
//                });
//                menu.add(itemPopupOpenBestand);
//                ocs.setEnabled(this, "itemPopupOpenBestand", itemPopupOpenBestand, !readOnly && bestid == 0);

                //-----------------------------------------
                JMenuItem itemPopupXDiscard = new JMenuItem("Verweigert (Medikament wird trotzdem ausgebucht.)");
                itemPopupXDiscard.addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        Connection db = OPDE.getDb().db;
                        try {
                            // Hier beginnt eine Transaktion
                            db.setAutoCommit(false);
                            db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                            db.commit();

                            HashMap hm = new HashMap();
                            hm.put("Status", TMBHP.STATUS_VERWEIGERT_VERWORFEN);
                            hm.put("UKennung", OPDE.getLogin().getUser().getUKennung());
                            hm.put("Ist", "!NOW!");
                            hm.put("IZeit", SYSCalendar.ermittleZeit());
                            DBHandling.updateRecord("BHP", hm, "BHPID", bhpid);
                            hm.clear();

                            if (!op.care.med.DBHandling.entnahmeVorrat(dafid, bwkennung, dosis, true, bhpid)) {
                                throw new SQLException("entnahmeVorrat");
                            }
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

                        tm.setUpdate(row, TMBHP.STATUS_VERWEIGERT_VERWORFEN);
                        //tm.reload(row);
                    }
                });
                menu.add(itemPopupXDiscard);
                ocs.setEnabled(this, "itemPopupXDiscard", itemPopupXDiscard, changeable && status == TMBHP.STATUS_OFFEN);

                menu.add(new JSeparator());
            }
            //-----------------------------------------
            String str;
            if (dafid > 0) {
                str = "Verweigert (Medikament wird nicht ausgebucht.)";
            } else {
                str = "Verweigert";
            }

            JMenuItem itemPopupXPreserve = new JMenuItem(str);
            itemPopupXPreserve.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    HashMap hm = new HashMap();
                    hm.put("Status", TMBHP.STATUS_VERWEIGERT);
                    hm.put("UKennung", OPDE.getLogin().getUser().getUKennung());
                    hm.put("Ist", "!NOW!");
                    hm.put("IZeit", SYSCalendar.ermittleZeit());
                    DBHandling.updateRecord("BHP", hm, "BHPID", bhpid);
                    hm.clear();
                    tm.setUpdate(row, TMBHP.STATUS_VERWEIGERT);
                    //tm.reload(row);
                }
            });
            menu.add(itemPopupXPreserve);
            ocs.setEnabled(this, "itemPopupXPreserve", itemPopupXPreserve, changeable && status == TMBHP.STATUS_OFFEN);

            menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
        }
    }//GEN-LAST:event_tblBHPMousePressed

    private void jspBHPComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspBHPComponentResized
        JScrollPane jsp = (JScrollPane) evt.getComponent();
        Dimension dim = jsp.getSize();
        // Gr��e der Text Spalten im DFN �ndern.
        // Summe der fixen Spalten  = 175 + ein bisschen
        int textWidth = dim.width - (50 + 80 + 35 + 80 + 25);
        TableColumnModel tcm1 = tblBHP.getColumnModel();
        if (tcm1.getColumnCount() < 6) {
            return;
        }

        //tcm1.getColumn(TMBHP.COL_massid).setPreferredWidth(50);
        tcm1.getColumn(TMBHP.COL_BEZEICHNUNG).setPreferredWidth(textWidth / 2);
        tcm1.getColumn(TMBHP.COL_DOSIS).setPreferredWidth(50);
        tcm1.getColumn(TMBHP.COL_ZEIT).setPreferredWidth(80);
        tcm1.getColumn(TMBHP.COL_STATUS).setPreferredWidth(35);
        tcm1.getColumn(TMBHP.COL_UKENNUNG).setPreferredWidth(80);
        tcm1.getColumn(TMBHP.COL_BEMPLAN).setPreferredWidth(textWidth / 2);
        tcm1.getColumn(TMBHP.COL_BEMBHP).setPreferredWidth(35);

        //tcm1.getColumn(0).setHeaderValue("ID");
        tcm1.getColumn(TMBHP.COL_BEZEICHNUNG).setHeaderValue("Bezeichnung");
        tcm1.getColumn(TMBHP.COL_DOSIS).setHeaderValue("Dosis");
        tcm1.getColumn(TMBHP.COL_ZEIT).setHeaderValue("Zeit");
        tcm1.getColumn(TMBHP.COL_STATUS).setHeaderValue("Status");
        tcm1.getColumn(TMBHP.COL_UKENNUNG).setHeaderValue("PflegerIn");
        tcm1.getColumn(TMBHP.COL_BEMPLAN).setHeaderValue("Hinweis");
        tcm1.getColumn(TMBHP.COL_BEMBHP).setHeaderValue("!");
    }//GEN-LAST:event_jspBHPComponentResized

    private void reloadTable() {
        tblBHP.setModel(new TMBHP(bwkennung, jdcDatum.getDate(), false, cmbSchicht.getSelectedIndex() - 1));
        tblBHP.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jspBHP.dispatchEvent(new ComponentEvent(jspBHP, ComponentEvent.COMPONENT_RESIZED));
        tblBHP.getColumnModel().getColumn(TMBHP.COL_BEZEICHNUNG).setCellRenderer(new RNDBHP());
        tblBHP.getColumnModel().getColumn(TMBHP.COL_DOSIS).setCellRenderer(new RNDBHP());
        tblBHP.getColumnModel().getColumn(TMBHP.COL_ZEIT).setCellRenderer(new RNDBHP());
        tblBHP.getColumnModel().getColumn(TMBHP.COL_STATUS).setCellRenderer(new RNDBHP());
        tblBHP.getColumnModel().getColumn(TMBHP.COL_UKENNUNG).setCellRenderer(new RNDBHP());
        tblBHP.getColumnModel().getColumn(TMBHP.COL_BEMPLAN).setCellRenderer(new RNDBHP());
        tblBHP.getColumnModel().getColumn(TMBHP.COL_BEMBHP).setCellRenderer(new RNDBHP());

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnBedarf;
    private javax.swing.JButton btnForward;
    private javax.swing.JButton btnLock;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnNow;
    private javax.swing.JButton btnTop;
    private javax.swing.JComboBox cmbSchicht;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JToolBar jToolBar1;
    private com.toedter.calendar.JDateChooser jdcDatum;
    private javax.swing.JScrollPane jspBHP;
    private javax.swing.JLabel lblBW;
    private javax.swing.JTable tblBHP;
    // End of variables declaration//GEN-END:variables
}
