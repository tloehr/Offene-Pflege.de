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
package op.care.dfn;

import com.toedter.calendar.JDateChooser;
import entity.SYSRunningClasses;
import entity.SYSRunningClassesTools;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import op.care.planung.DlgMassSelect;
import op.tools.ListElement;
import op.tools.DBHandling;
import op.tools.DBRetrieve;
import op.tools.SYSCalendar;
import op.tools.SYSTools;

/**
 *
 * @author  root
 */
public class PnlDFN extends CleanablePanel {

    public static final String internalClassID = "nursingrecords.dfn";

    String bwkennung;
    JPopupMenu menu;
    private FrmPflege parent;
    //private String classname;
    private OCSec ocs;
    private boolean ignoreJDCEvent;
    private boolean readOnly;
    private boolean abwesend;
    private SYSRunningClasses runningClass, blockingClass;

    /** Creates new form PnlDFN */
    public PnlDFN(FrmPflege parent, String bwkennung) {
        this.parent = parent;
        this.bwkennung = bwkennung;
        ocs = OPDE.getOCSec();
        initComponents();
        initPanel();
    }

    public void cleanup() {
        jdcDatum.cleanup();
        SYSTools.unregisterListeners(this);
        SYSRunningClassesTools.moduleEnded(runningClass);
    }

    private void initPanel() {
        
        SYSRunningClasses[] result = SYSRunningClassesTools.moduleStarted(internalClassID, parent.getBewohner().getBWKennung(), SYSRunningClasses.STATUS_RW);
        runningClass = result[0];
        abwesend = DBRetrieve.getAbwesendSeit(bwkennung) != null;
        
        if (!runningClass.isRW()) {
            blockingClass = result[1];
            btnLock.setEnabled(true);
            btnLock.setToolTipText("<html><body><h3>Dieser Datensatz ist belegt durch:</h3>" 
                    + blockingClass.getLogin().getUser().getNameUndVorname()
                    + "</body></html>");
        } else {
            btnLock.setEnabled(false);
            btnLock.setToolTipText(null);
        }
//        ocs.setEnabled(this, "btnBedarf", btnBedarf, !readOnly);

        ignoreJDCEvent = true;
        jdcDatum.setDate(SYSCalendar.nowDBDate());
        btnForward.setEnabled(false); // In die Zukunft kann man nicht gucken.

        //jdcDatum.setMaxSelectableDate(SYSCalendar.today_date());
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

        ignoreJDCEvent = false;
        cmbSchicht.setSelectedIndex(SYSCalendar.ermittleSchicht() + 1);
        //cmbSchichtItemStateChanged(null);
        // reloadTable(); wird durch cmbSchicht durchgeführt.
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgSort = new javax.swing.ButtonGroup();
        jToolBar1 = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        lblBW = new javax.swing.JLabel();
        btnLock = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        btnNow = new javax.swing.JButton();
        btnForward = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();
        btnTop = new javax.swing.JButton();
        jdcDatum = new com.toedter.calendar.JDateChooser();
        cmbSchicht = new javax.swing.JComboBox();
        jspDFN = new javax.swing.JScrollPane();
        tblDFN = new javax.swing.JTable();

        jToolBar1.setFloatable(false);

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/filenew.png"))); // NOI18N
        btnNew.setText("Neu");
        btnNew.setFocusable(false);
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNew);

        btnLogout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/lock.png"))); // NOI18N
        btnLogout.setText("Abmelden");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutbtnLogoutHandler(evt);
            }
        });
        jToolBar1.add(btnLogout);

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

        cmbSchicht.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Alles", "Nacht, morgens", "Früh", "Spät", "Nacht, abends" }));
        cmbSchicht.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbSchichtItemStateChanged(evt);
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 308, Short.MAX_VALUE)
                .addComponent(cmbSchicht, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbSchicht, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btnNow, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnForward, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnBack, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnTop, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jdcDatum, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jspDFN.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jspDFN.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jspDFNComponentResized(evt);
            }
        });

        tblDFN.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblDFNMousePressed(evt);
            }
        });
        jspDFN.setViewportView(tblDFN);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 688, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblBW, javax.swing.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLock)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jspDFN, javax.swing.GroupLayout.DEFAULT_SIZE, 664, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnLock)
                    .addComponent(lblBW))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jspDFN, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    private void btnLogoutbtnLogoutHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutbtnLogoutHandler
        OPDE.ocmain.lockOC();
    }//GEN-LAST:event_btnLogoutbtnLogoutHandler

    private void btnLockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLockActionPerformed
        initPanel();
    }//GEN-LAST:event_btnLockActionPerformed

    private void btnNowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNowActionPerformed
        jdcDatum.setDate(SYSCalendar.today_date());
        btnForward.setEnabled(SYSCalendar.sameDay(jdcDatum.getDate(), SYSCalendar.nowDBDate()) < 0);
    }//GEN-LAST:event_btnNowActionPerformed

    private void btnForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnForwardActionPerformed
        jdcDatum.setDate(SYSCalendar.addDate(jdcDatum.getDate(), 1));
        btnForward.setEnabled(SYSCalendar.sameDay(jdcDatum.getDate(), SYSCalendar.nowDBDate()) < 0);
    }//GEN-LAST:event_btnForwardActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        jdcDatum.setDate(SYSCalendar.addDate(jdcDatum.getDate(), -1));
        btnForward.setEnabled(SYSCalendar.sameDay(jdcDatum.getDate(), SYSCalendar.nowDBDate()) < 0);
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTopActionPerformed
        jdcDatum.setDate(jdcDatum.getMinSelectableDate());
        btnForward.setEnabled(SYSCalendar.sameDay(jdcDatum.getDate(), SYSCalendar.nowDBDate()) < 0);
    }//GEN-LAST:event_btnTopActionPerformed

    private void jdcDatumPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jdcDatumPropertyChange
        if (!evt.getPropertyName().equals("date") || ignoreJDCEvent) {
            return;
        }
        ignoreJDCEvent = true;
        SYSCalendar.checkJDC((JDateChooser) evt.getSource());
        ignoreJDCEvent = false;
        reloadTable();
    }//GEN-LAST:event_jdcDatumPropertyChange

    private void cmbSchichtItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbSchichtItemStateChanged
        if (evt.getStateChange() != ItemEvent.SELECTED) {
            return;
        }
        reloadTable();
    }//GEN-LAST:event_cmbSchichtItemStateChanged

    private void reloadTable() {
        tblDFN.setModel(new TMDFN(bwkennung, jdcDatum.getDate(), cmbSchicht.getSelectedIndex() - 1));
        tblDFN.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jspDFN.dispatchEvent(new ComponentEvent(jspDFN, ComponentEvent.COMPONENT_RESIZED));
        tblDFN.getColumnModel().getColumn(TMDFN.COL_BEZEICHNUNG).setCellRenderer(new RNDDFN());
        tblDFN.getColumnModel().getColumn(TMDFN.COL_ZEIT).setCellRenderer(new RNDDFN());
        tblDFN.getColumnModel().getColumn(TMDFN.COL_STATUS).setCellRenderer(new RNDDFN());
        tblDFN.getColumnModel().getColumn(TMDFN.COL_UKENNUNG).setCellRenderer(new RNDDFN());
        tblDFN.getColumnModel().getColumn(TMDFN.COL_BEMPLAN).setCellRenderer(new RNDDFN());

    }

    private void tblDFNMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDFNMousePressed
        if (readOnly) {
            return;
        }
        final TMDFN tm = (TMDFN) tblDFN.getModel();
        if (tm.getRowCount() == 0) {
            return;
        }
        Point p = evt.getPoint();
        final int col = tblDFN.columnAtPoint(p);
        final int row = tblDFN.rowAtPoint(p);
        ListSelectionModel lsm = tblDFN.getSelectionModel();
        lsm.setSelectionInterval(row, row);
        final long dfnid = ((Long) tm.getValueAt(row, TMDFN.COL_DFNID)).longValue();
        //final long termid = ((Long) tm.getValueAt(row, TMDFN.COL_TERMID)).longValue();
        int status = ((Integer) tm.getValueAt(row, TMDFN.COL_STATUS)).intValue();
        String ukennung = ((String) tm.getValueAt(row, TMDFN.COL_UKENNUNG)).toString();
        long abdatum = ((Long) tm.getValueAt(row, TMDFN.COL_BIS)).longValue();
        //final long soll = ((Long) tm.getValueAt(row, TMDFN.COL_SOLL)).longValue();
        //final int szeit = ((Integer) tm.getValueAt(row, TMDFN.COL_SZEIT)).intValue();
        //final long relid = ((Long) tm.getValueAt(row, TMDFN.COL_RELID)).longValue();
        long mdate = ((Long) tm.getValueAt(row, TMDFN.COL_MDATE)).longValue();

        boolean changeable =
                // Diese Kontrolle stellt sicher, dass ein User nur seine eigenen Einträge und das auch nur
                // eine halbe Stunde lang bearbeiten kann.
                !abwesend &&
                SYSCalendar.isInFuture(abdatum) &&
                (ukennung.equals("") ||
                (ukennung.equalsIgnoreCase(OPDE.getLogin().getUser().getUKennung()) &&
                SYSCalendar.earlyEnough(mdate, 30)));
        OPDE.getLogger().debug(changeable ? "changeable" : "NOT changeable");
        if (changeable) {
            // Drückt der Anwender auch wirklich mit der LINKEN Maustaste auf die mittlere Spalte.
            if (!evt.isPopupTrigger() && col == TMDFN.COL_STATUS) {
                boolean fullReloadNecessary = false;
                status++;
                if (status > 1) {
                    status = 0;
                }
                HashMap hm = new HashMap();
                hm.put("Status", status);
                if (status == 0) {
                    hm.put("UKennung", null);
                    hm.put("Ist", null);
                    hm.put("IZeit", null);
                    hm.put("Dauer", 0);
                    DBHandling.updateRecord("DFN", hm, "DFNID", dfnid);

                } else {
                    hm.put("UKennung", OPDE.getLogin().getUser().getUKennung());
                    hm.put("Ist", "!NOW!");
                    hm.put("IZeit", SYSCalendar.ermittleZeit());
                    DBHandling.updateRecord("DFN", hm, "DFNID", dfnid);
                }

                hm.clear();
                tm.setUpdate(row, status);
                if (fullReloadNecessary) {
                    reloadTable();
                }
            }

        }
        // Nun noch Menüeinträge
        if (evt.isPopupTrigger()) {
            SYSTools.unregisterListeners(menu);
            menu = new JPopupMenu();

            JMenuItem itemPopupRefuse = new JMenuItem("Verweigert / nicht durchgeführt");
            itemPopupRefuse.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    HashMap hm = new HashMap();
                    hm.put("Status", TMDFN.STATUS_VERWEIGERT);
                    hm.put("UKennung", OPDE.getLogin().getUser().getUKennung());
                    hm.put("Ist", "!NOW!");
                    hm.put("IZeit", SYSCalendar.ermittleZeit());
                    DBHandling.updateRecord("DFN", hm, "DFNID", dfnid);
                    hm.clear();
                    tm.setUpdate(row, TMDFN.STATUS_VERWEIGERT);
                    //tm.reload(row, col);
                }
            });
            menu.add(itemPopupRefuse);
            ocs.setEnabled(this, "itemPopupRefuse", itemPopupRefuse, changeable && status == TMDFN.STATUS_OFFEN);

            if (changeable) {
                menu.add(new JSeparator());
                int[] mins = new int[]{1, 2, 3, 4, 5, 10, 15, 20, 30, 45, 60, 120, 240, 360};
                HashMap text = new HashMap();
                text.put(60, "1 Stunde");
                text.put(120, "2 Stunden");
                text.put(240, "3 Stunden");
                text.put(360, "4 Stunden");
                for (int i = 0; i < mins.length; i++) {
                    String einheit = mins[i] + " Minuten";
                    if (text.containsKey(mins[i])) {
                        einheit = mins[i] + " " + text.get(mins[i]).toString();
                    }
                    JMenuItem item = new JMenuItem(einheit);
                    final int minutes = mins[i];
                    item.addActionListener(new java.awt.event.ActionListener() {

                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            HashMap hm = new HashMap();
                            hm.put("Dauer", minutes);
                            DBHandling.updateRecord("DFN", hm, "DFNID", dfnid);
                            hm.clear();
                            tm.reload(row, TMDFN.COL_BEZEICHNUNG);
                        }
                    });
                    menu.add(item);
                    item.setEnabled(status == TMDFN.STATUS_ERLEDIGT);
                }
                text.clear();
            }

            menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
        }
}//GEN-LAST:event_tblDFNMousePressed

    private void jspDFNComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspDFNComponentResized
        JScrollPane jsp = (JScrollPane) evt.getComponent();
        Dimension dim = jsp.getSize();
        // Größe der Text Spalten im DFN ändern.
        // Summe der fixen Spalten  = 175 + ein bisschen
        int textWidth = dim.width - (50 + 80 + 55 + 80 + 25);
        TableColumnModel tcm1 = tblDFN.getColumnModel();
        if (tcm1.getColumnCount() < 4) {
            return;
        }

        //tcm1.getColumn(TMDFN.COL_MassID).setPreferredWidth(50);
        tcm1.getColumn(TMDFN.COL_BEZEICHNUNG).setPreferredWidth(textWidth / 2);
        tcm1.getColumn(TMDFN.COL_ZEIT).setPreferredWidth(80);
        tcm1.getColumn(TMDFN.COL_STATUS).setPreferredWidth(55);
        tcm1.getColumn(TMDFN.COL_UKENNUNG).setPreferredWidth(80);
        tcm1.getColumn(TMDFN.COL_BEMPLAN).setPreferredWidth(textWidth / 2);

        //tcm1.getColumn(0).setHeaderValue("ID");
        tcm1.getColumn(TMDFN.COL_BEZEICHNUNG).setHeaderValue("Bezeichnung");
        tcm1.getColumn(TMDFN.COL_ZEIT).setHeaderValue("Zeit");
        tcm1.getColumn(TMDFN.COL_STATUS).setHeaderValue("Status");
        tcm1.getColumn(TMDFN.COL_UKENNUNG).setHeaderValue("PflegerIn");
        tcm1.getColumn(TMDFN.COL_BEMPLAN).setHeaderValue("Hinweis");
}//GEN-LAST:event_jspDFNComponentResized

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        Object[] sel = DlgMassSelect.showDialog(parent, true);
        if (sel.length > 0) {
            HashMap hmnew = new HashMap();
            hmnew.put("Status", TMDFN.STATUS_ERLEDIGT);
            hmnew.put("UKennung", OPDE.getLogin().getUser().getUKennung());
            hmnew.put("BWKennung", bwkennung);
            hmnew.put("TermID", 0);
            hmnew.put("Soll", "!NOW!");
            hmnew.put("StDatum", "!NOW!");
            hmnew.put("SZeit", SYSCalendar.ermittleZeit());
            hmnew.put("Ist", "!NOW!");
            hmnew.put("IZeit", SYSCalendar.ermittleZeit());
            for (int i = 0; i < sel.length; i++) {
                // Zuerst neuen DFN einfügen.
                ListElement elmass = (ListElement) sel[i];
                long massID = elmass.getPk();
                hmnew.put("MassID", massID);
                double dauer = ((BigDecimal) DBRetrieve.getSingleValue("Massnahmen", "Dauer", "MassID", massID)).doubleValue();
                hmnew.put("Dauer", dauer);
                DBHandling.insertRecord("DFN", hmnew);
            }
            reloadTable();
        }
    }//GEN-LAST:event_btnNewActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgSort;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnForward;
    private javax.swing.JButton btnLock;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnNow;
    private javax.swing.JButton btnTop;
    private javax.swing.JComboBox cmbSchicht;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JToolBar jToolBar1;
    private com.toedter.calendar.JDateChooser jdcDatum;
    private javax.swing.JScrollPane jspDFN;
    private javax.swing.JLabel lblBW;
    private javax.swing.JTable tblDFN;
    // End of variables declaration//GEN-END:variables
}
