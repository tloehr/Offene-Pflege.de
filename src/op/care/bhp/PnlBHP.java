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
package op.care.bhp;

import com.toedter.calendar.JDateChooser;
import entity.BWInfo;
import entity.BWInfoTools;
import entity.Bewohner;
import entity.BewohnerTools;
import entity.system.SYSRunningClasses;
import entity.system.SYSRunningClassesTools;
import entity.verordnungen.*;
import op.OCSec;
import op.OPDE;
import op.care.CleanablePanel;
import op.care.FrmPflege;
import op.tools.Pair;
import op.tools.SYSCalendar;
import op.tools.SYSTools;
import tablemodels.TMBHP;
import tablerenderer.RNDBHP;

import javax.persistence.EntityManager;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

/**
 * @author tloehr
 */
public class PnlBHP extends CleanablePanel {

    public static final String internalClassID = "nursingrecords.bhp";
    String bwkennung;
    private Bewohner bewohner;
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

    /**
     * Creates ner form PnlBHP
     */
    public PnlBHP(FrmPflege parent, Bewohner bewohner) {
        this.parent = parent;

        ocs = OPDE.getOCSec();
        initComponents();
        change2Bewohner(bewohner);

    }

    @Override
    public void change2Bewohner(Bewohner bewohner) {
        this.bwkennung = bewohner.getBWKennung();
        this.bewohner = bewohner;

        if (runningClass != null) {
            SYSRunningClassesTools.endModule(runningClass);
        }

        Pair<SYSRunningClasses, SYSRunningClasses> pair = SYSRunningClassesTools.startModule(internalClassID, bewohner, new String[]{"nursingrecords.prescription", "nursingrecords.bhp", "nursingrecords.bhpimport"});
        runningClass = pair.getFirst();
        readOnly = !runningClass.isRW();

        if (readOnly) {
            blockingClass = pair.getSecond();
            btnLock.setToolTipText("<html><body><h3>Dieser Datensatz ist belegt durch:</h3>"
                    + blockingClass.getLogin().getUser().getNameUndVorname()
                    + "</body></html>");
        } else {
            btnLock.setToolTipText(null);
        }


        cmbSchicht.setModel(new DefaultComboBoxModel(new String[]{"Alles", "Nacht, früh morgens", "Früh", "Spät", "Nacht, spät abends"}));

        abwesend = BWInfoTools.getAbwesendSeit(bewohner) != null;

        ocs.setEnabled(this, "btnBedarf", btnBedarf, !readOnly);

        ignoreJDCEvent = true;
        jdcDatum.setDate(SYSCalendar.today_date());

        java.util.List<BWInfo> listHeimaufenhtalte = BWInfoTools.getHeimaufenthalte(bewohner);
        if (listHeimaufenhtalte.isEmpty()) {
            jdcDatum.setMinSelectableDate(new Date());
        } else {
            jdcDatum.setMinSelectableDate(listHeimaufenhtalte.get(0).getVon());
        }

        BewohnerTools.setBWLabel(lblBW, bewohner);
        ignoreJDCEvent = false;
        cmbSchicht.setSelectedIndex(SYSCalendar.ermittleSchicht() + 1);
    }

    @Override
    public void cleanup() {
        jdcDatum.cleanup();
        SYSTools.unregisterListeners(this);
        SYSRunningClassesTools.endModule(runningClass);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jspBHP = new JScrollPane();
        tblBHP = new JTable();
        jPanel1 = new JPanel();
        btnNow = new JButton();
        btnForward = new JButton();
        btnBack = new JButton();
        btnTop = new JButton();
        jdcDatum = new JDateChooser();
        cmbSchicht = new JComboBox();
        btnBedarf = new JButton();
        lblBW = new JLabel();
        btnLock = new JButton();
        jToolBar1 = new JToolBar();
        btnLogout = new JButton();
        jLabel12 = new JLabel();

        //======== this ========

        //======== jspBHP ========
        {
            jspBHP.setBorder(new BevelBorder(BevelBorder.RAISED));
            jspBHP.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    jspBHPComponentResized(e);
                }
            });

            //---- tblBHP ----
            tblBHP.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    tblBHPMousePressed(e);
                }
            });
            jspBHP.setViewportView(tblBHP);
        }

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new BevelBorder(BevelBorder.RAISED));

            //---- btnNow ----
            btnNow.setBackground(Color.white);
            btnNow.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/history.png")));
            btnNow.setBorder(null);
            btnNow.setBorderPainted(false);
            btnNow.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnNowActionPerformed(e);
                }
            });

            //---- btnForward ----
            btnForward.setBackground(Color.white);
            btnForward.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/1rightarrow.png")));
            btnForward.setBorder(null);
            btnForward.setBorderPainted(false);
            btnForward.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnForwardActionPerformed(e);
                }
            });

            //---- btnBack ----
            btnBack.setBackground(Color.white);
            btnBack.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/1leftarrow.png")));
            btnBack.setBorder(null);
            btnBack.setBorderPainted(false);
            btnBack.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnBackActionPerformed(e);
                }
            });

            //---- btnTop ----
            btnTop.setBackground(Color.white);
            btnTop.setIcon(new ImageIcon(getClass().getResource("/artwork/16x16/2leftarrow.png")));
            btnTop.setBorder(null);
            btnTop.setBorderPainted(false);
            btnTop.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnTopActionPerformed(e);
                }
            });

            //---- jdcDatum ----
            jdcDatum.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent e) {
                    jdcDatumPropertyChange(e);
                }
            });

            //---- cmbSchicht ----
            cmbSchicht.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    cmbSchichtItemStateChanged(e);
                }
            });

            //---- btnBedarf ----
            btnBedarf.setText("Bei Bedarf");
            btnBedarf.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnBedarfActionPerformed(e);
                }
            });

            GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                    jPanel1Layout.createParallelGroup()
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(jdcDatum, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnTop, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnBack)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnForward)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnNow, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 407, Short.MAX_VALUE)
                                    .addComponent(btnBedarf)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cmbSchicht, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addContainerGap())
            );
            jPanel1Layout.setVerticalGroup(
                    jPanel1Layout.createParallelGroup()
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel1Layout.createParallelGroup()
                                            .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(cmbSchicht, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(btnBedarf))
                                            .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                                    .addComponent(btnNow, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(btnForward, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(btnBack, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(btnTop, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(jdcDatum, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }

        //---- lblBW ----
        lblBW.setFont(new Font("Dialog", Font.BOLD, 18));
        lblBW.setForeground(new Color(255, 51, 0));
        lblBW.setText("Nachname, Vorname (*GebDatum, 00 Jahre) [??1]");

        //---- btnLock ----
        btnLock.setBackground(Color.white);
        btnLock.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/encrypted.png")));
        btnLock.setBorder(null);
        btnLock.setBorderPainted(false);
        btnLock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnLockActionPerformed(e);
            }
        });

        //======== jToolBar1 ========
        {
            jToolBar1.setFloatable(false);

            //---- btnLogout ----
            btnLogout.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/lock.png")));
            btnLogout.setText("Abmelden");
            btnLogout.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnLogoutbtnLogoutHandler(e);
                }
            });
            jToolBar1.add(btnLogout);
        }

        //---- jLabel12 ----
        jLabel12.setText("<html>Hinweis: &frac14; = 0,25 | <sup>1</sup>/<sub>3</sub> = 0,33 | &frac12; = 0,5 | &frac34; = 0,75</html>");

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addComponent(jToolBar1, GroupLayout.DEFAULT_SIZE, 861, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lblBW, GroupLayout.DEFAULT_SIZE, 803, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnLock)
                                .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jspBHP, GroupLayout.DEFAULT_SIZE, 821, Short.MAX_VALUE)
                                .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel12, GroupLayout.DEFAULT_SIZE, 821, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jToolBar1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(btnLock)
                                        .addComponent(lblBW))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jspBHP, GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogoutbtnLogoutHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutbtnLogoutHandler
        OPDE.ocmain.lockOC();
    }//GEN-LAST:event_btnLogoutbtnLogoutHandler

    private void btnLockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLockActionPerformed
        change2Bewohner(bewohner);
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
        } // Hier dürfen nur Examen dran.
        final TMBHP tm = (TMBHP) tblBHP.getModel();
        if (tm.getRowCount() == 0) {
            return;
        }
        Point p = evt.getPoint();
        final int col = tblBHP.columnAtPoint(p);
        final int row = tblBHP.rowAtPoint(p);

        ListSelectionModel lsm = tblBHP.getSelectionModel();
        lsm.setSelectionInterval(row, row);

        BHP bhp = tm.getBHP(row);

//        final long bhpid = ((Long) tm.getValueAt(row, TMBHP.COL_BHPID)).longValue();
//        int status = ((Integer) tm.getValueAt(row, TMBHP.COL_STATUS)).intValue();
//        final double dosis = ((Double) tm.getValueAt(row, TMBHP.COL_DOSIS)).doubleValue();
//        long mdate = ((Long) tm.getValueAt(row, TMBHP.COL_MDATE)).longValue();
//        long abdatum = ((Long) tm.getValueAt(row, TMBHP.COL_ABDATUM)).longValue();
//        final long dafid = ((Long) tm.getValueAt(row, TMBHP.COL_DAFID)).longValue();
//        boolean bedarf = ((Long) tm.getValueAt(row, TMBHP.COL_SITID)).longValue() > 0;
        //long verid = ((Long) tm.getValueAt(row, TMBHP.COL_VERID)).longValue();

        //boolean bisPackEnde = ((Boolean) tm.getValueAt(row, TMBHP.COL_BISPACKENDE)).booleanValue();

        boolean changeable =
                // Diese Kontrolle stellt sicher, dass ein User nur seine eigenen Einträge und das auch nur
                // eine halbe Stunde lang bearbeiten kann.
                // Ausserdem kann man nur dann etwas geben, wenn es
                // a) eine Massnahmen ohne Medikation ist
                // ODER
                // (
                //      b) ein angebrochener Bestand vorhanden ist
                //      UND
                //      c)  das häkchen NICHT gesetzt ist oder wenn es gesetzt ist, kann man es 
                //          nur dann wieder wegnehmen, wenn es derselbe Benutzer FRüH GENUG tut.
                //          Und auch nur dann, wenn 
                // )
                !abwesend && //
                        // Aus Performance Gründen muss der Ausdruck, der hier bereits ermittelt wurde
                        // an anderer Stelle verarbeitet werden, sonst dauert die SQL Abfrage zu lang
                        // Ursprünglich stand hier:
                        // (dafid == 0 || status != BHPTools.STATUS_OFFEN || bestid > 0) && // wenn es ein Medikament ist und der Status offen, dann nur änderbar, wenn es einen angebrochenen Bestand gibt.
                        // Diese Abfrage habe ich nach Unten verschoben und das bestid muss nun im Einzelfall ermittelt werden.

                        // Absetzdatum in der Zukunft ?
                        bhp.getVerordnungPlanung().getVerordnung().getAbDatum().after(new Date())
                        // Offener Status geht immer
                        && (bhp.getStatus() == BHPTools.STATUS_OFFEN
                        // Nicht mehr offen ?
                        // Dann nur wenn derselbe Benutzer dass wieder rückgängig machen will
                        ||
                        (bhp.getUser().equals(OPDE.getLogin().getUser())
                                // und es noch früh genug ist (30 Minuten)
                                && SYSCalendar.earlyEnough(bhp.getMdate().getTime(), 30)
                                // und kein abgesetzter Bestand beteiligt ist. Von wegen Rückgabe der Bestände
                                && !MedBestandTools.hasAbgesetzteBestaende(bhp)
                        )
                ); // damit man nichts rückgängig machen kann, was irgendwie einen abgeschlossenen Bestand betrifft.
        OPDE.debug(changeable ? "changeable" : "NOT changeable");
        if (changeable) {
            // Drückt auch wirklich mit der LINKEN Maustaste auf die mittlere Spalte.
            if (!evt.isPopupTrigger() && col == TMBHP.COL_STATUS) {

                byte status = bhp.getStatus();
                Verordnung verordnung = bhp.getVerordnungPlanung().getVerordnung();
                MedBestand bestandImAnbruch = null;
                if (verordnung.hasMedi()) {
                    bestandImAnbruch = MedBestandTools.findByVerordnungImAnbruch(verordnung);
                }

                //boolean changeable_additional = (dafid == 0 || status != BHPTools.STATUS_OFFEN || bestid > 0);
                if (!verordnung.hasMedi() || status != BHPTools.STATUS_OFFEN || bestandImAnbruch != null) {
                    boolean fullReloadNecessary = false;
                    status++;
                    if (status > 1) {
                        status = BHPTools.STATUS_OFFEN;
                    }

                    bhp.setStatus(status);
                    if (status == BHPTools.STATUS_OFFEN) {
                        bhp.setUser(null);
                        bhp.setIst(null);
                        bhp.setiZeit(null);
                        bhp.setBemerkung(null);
                    } else {
                        bhp.setUser(OPDE.getLogin().getUser());
                        bhp.setIst(new Date());
                        bhp.setiZeit(SYSCalendar.ermittleZeit());
                    }

                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        bhp = em.merge(bhp);

                        if (verordnung.hasMedi()) {
                            if (status == BHPTools.STATUS_ERLEDIGT) {
                                MedVorratTools.entnahmeVorrat(em, verordnung.getDarreichung(), verordnung.getBewohner(), bhp.getDosis(), true, bhp);
                            } else {
                                MedVorratTools.rueckgabeVorrat(em, bhp);
                            }
                        }
                        // Wenn man eine Massnahme aus der Bedarfsmedikation
                        // rückgängig macht, wird sie gelöscht.
                        if (verordnung.isBedarf() && status == BHPTools.STATUS_OFFEN) {
                            em.remove(bhp);
                            fullReloadNecessary = true;
                        }
                        em.getTransaction().commit();
                    } catch (Exception ex) {
                        em.getTransaction().rollback();
                        OPDE.fatal(ex);
                    } finally {
                        em.close();
                    }
                    tm.fireTableRowsUpdated(row, row);
                    if (fullReloadNecessary) {
                        reloadTable();
                    }
                }
            }
        }


        // Nun noch Menüeinträge
//        if (evt.isPopupTrigger()) {
//            SYSTools.unregisterListeners(menu);
//            menu = new JPopupMenu();
//
//            if (dafid > 0) {
//
//                JMenuItem itemPopupCloseBestand = new JMenuItem("Bestand abschließen");
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
//
//                //-----------------------------------------
//                JMenuItem itemPopupXDiscard = new JMenuItem("Verweigert (Medikament wird trotzdem ausgebucht.)");
//                itemPopupXDiscard.addActionListener(new java.awt.event.ActionListener() {
//
//                    public void actionPerformed(java.awt.event.ActionEvent evt) {
//                        Connection db = OPDE.getDb().db;
//                        try {
//                            // Hier beginnt eine Transaktion
//                            db.setAutoCommit(false);
//                            db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
//                            db.commit();
//
//                            HashMap hm = new HashMap();
//                            hm.put("Status", BHPTools.STATUS_VERWEIGERT_VERWORFEN);
//                            hm.put("UKennung", OPDE.getLogin().getUser().getUKennung());
//                            hm.put("Ist", "!NOW!");
//                            hm.put("IZeit", SYSCalendar.ermittleZeit());
//                            DBHandling.updateRecord("BHP", hm, "BHPID", bhpid);
//                            hm.clear();
//
//                            /*if (!op.care.med.DBHandling.entnahmeVorrat(dafid, bwkennung, dosis, true, bhpid)) {
//                                throw new SQLException("entnahmeVorrat");
//                            }*/
//                            db.commit();
//                            db.setAutoCommit(true);
//                        } catch (SQLException ex) {
//                            try {
//                                db.rollback();
//                            } catch (SQLException ex1) {
//                                new DlgException(ex1);
//                                ex1.printStackTrace();
//                                System.exit(1);
//                            }
//                            new DlgException(ex);
//                        }
//
//                        //tm.setUpdate(row, BHPTools.STATUS_VERWEIGERT_VERWORFEN);
//                        //tm.reload(row);
//                    }
//                });
//                menu.add(itemPopupXDiscard);
//                ocs.setEnabled(this, "itemPopupXDiscard", itemPopupXDiscard, changeable && status == BHPTools.STATUS_OFFEN);
//
//                menu.add(new JSeparator());
//            }
//            //-----------------------------------------
//            String str;
//            if (dafid > 0) {
//                str = "Verweigert (Medikament wird nicht ausgebucht.)";
//            } else {
//                str = "Verweigert";
//            }
//
//            JMenuItem itemPopupXPreserve = new JMenuItem(str);
//            itemPopupXPreserve.addActionListener(new java.awt.event.ActionListener() {
//
//                public void actionPerformed(java.awt.event.ActionEvent evt) {
//                    HashMap hm = new HashMap();
//                    hm.put("Status", BHPTools.STATUS_VERWEIGERT);
//                    hm.put("UKennung", OPDE.getLogin().getUser().getUKennung());
//                    hm.put("Ist", "!NOW!");
//                    hm.put("IZeit", SYSCalendar.ermittleZeit());
//                    DBHandling.updateRecord("BHP", hm, "BHPID", bhpid);
//                    hm.clear();
//                    //tm.setUpdate(row, BHPTools.STATUS_VERWEIGERT);
//                    //tm.reload(row);
//                }
//            });
//            menu.add(itemPopupXPreserve);
//            ocs.setEnabled(this, "itemPopupXPreserve", itemPopupXPreserve, changeable && status == BHPTools.STATUS_OFFEN);
//
//            menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
//
//        }
    }//GEN-LAST:event_tblBHPMousePressed

    private void jspBHPComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspBHPComponentResized
        JScrollPane jsp = (JScrollPane) evt.getComponent();
        Dimension dim = jsp.getSize();
        // Größe der Text Spalten im DFN ändern.
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

        // das hier muss wieder rein
        tblBHP.setModel(new TMBHP(bewohner, jdcDatum.getDate(), cmbSchicht.getSelectedIndex() - 1));
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
    private JScrollPane jspBHP;
    private JTable tblBHP;
    private JPanel jPanel1;
    private JButton btnNow;
    private JButton btnForward;
    private JButton btnBack;
    private JButton btnTop;
    private JDateChooser jdcDatum;
    private JComboBox cmbSchicht;
    private JButton btnBedarf;
    private JLabel lblBW;
    private JButton btnLock;
    private JToolBar jToolBar1;
    private JButton btnLogout;
    private JLabel jLabel12;
    // End of variables declaration//GEN-END:variables
}
