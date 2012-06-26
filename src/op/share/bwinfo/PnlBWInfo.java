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
package op.share.bwinfo;

import entity.Bewohner;
import entity.BewohnerTools;
import entity.system.SYSPropsTools;
import op.OCSec;
import op.OPDE;
import op.tools.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author root
 */
public class PnlBWInfo extends NursingRecordsPanel {

    private JPopupMenu menu;
    private int mode;
    private String preselection;
    private Frame parent;
    private String bwkennung;
    private BWInfo bwinfo;
    private OCSec ocs;
    private boolean ignoreEvent;
    private ActionListener fileActionListener;
    private Bewohner bewohner;


    /**
     * @param parent    - zu welchem Fenster gehört dieses Panel ?
     * @param mode      - Welche Fragen sollen angeboten werden ? Verwenden Sie: BWInfo.ART_*
     * @param bwkennung - Um wen geht es ?
     */
    public PnlBWInfo(Frame parent, int mode, String bwkennung, String preselection) {
        initComponents();
        this.mode = mode;
        this.parent = parent;
        this.bwkennung = bwkennung;
        bewohner = BewohnerTools.findByBWKennung(bwkennung);
        this.preselection = preselection;
        ocs = OPDE.getOCSec();
        //SYSTools.setBWLabel(lblBW, bwkennung);
        initPanel();
    }

    public void cleanup() {
        SYSTools.unregisterListeners(menu);
        SYSTools.unregisterListeners(this);
    }

    @Override
    public void change2Bewohner(Bewohner bewohner) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

     @Override
    public void reload() {
         reloadTable();
    }

    public void initPanel() {
        ignoreEvent = true;
        cmbKategorie.setModel(DBHandling.ladeKategorien(mode, true, mode == BWInfo.ART_ALLES));
        if (preselection.equals("")) {
            cmbKategorie.setSelectedIndex(0);
        } else {
            SYSTools.selectInComboBox(cmbKategorie, preselection, true);
        }

        // Dieses Panel kann von verschiedenen Parents aus aufgerufen werden. Je nachdem von wo, ist die Liste
        // der Kategorien unterschiedlich. Stellt man jetzt die Kategorien von Parent A aus auf ein bestimmtes
        // Item ein, ruft dann dieses Panel von einem Parent B auf, dann kann es passieren, dass das System versucht
        // die Combobox in leere zu setzen, was zu einer Exception führt. Daher müssen die Kriterien, die zur
        // Auswahl der Kategorien für die ComboBox führen ebenfalls mit in den Klassen-String für storeState und restoreState
        // mit aufgenommen werden. Allerdings nur für die ComboBox.
        // Siehe Problem Nr. 15 und 20.
        String classname = this.getClass().getName() + ":" + mode;
        SYSPropsTools.restoreState(classname + "::cmbKategorie", cmbKategorie);

        SYSPropsTools.restoreState(this.getClass().getName() + "::cbPast", cbPast);
        SYSPropsTools.restoreState(this.getClass().getName() + "::cbEinzel", cbEinzel);
        SYSPropsTools.restoreState(this.getClass().getName() + "::cbTooltip", cbTooltip);
        SYSPropsTools.restoreState(this.getClass().getName() + "::cbDetail", cbDetail);

        fileActionListener = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                reloadTable();
            }
        };

        ignoreEvent = false;
        reloadTable();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblBW = new javax.swing.JLabel();
        jspBWInfo = new javax.swing.JScrollPane();
        tblBWInfo = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        cbPast = new javax.swing.JCheckBox();
        cmbKategorie = new javax.swing.JComboBox();
        cbDetail = new javax.swing.JCheckBox();
        cbTooltip = new javax.swing.JCheckBox();
        cbEinzel = new javax.swing.JCheckBox();

        lblBW.setFont(new java.awt.Font("Dialog", 1, 18));
        lblBW.setForeground(new java.awt.Color(255, 51, 0));
        lblBW.setText("jLabel3");

        jspBWInfo.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jspBWInfoComponentResized(evt);
            }
        });

        tblBWInfo.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tblBWInfo.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null}
                },
                new String[]{
                        "Title 1", "Title 2", "Title 3", "Title 4"
                }
        ));
        tblBWInfo.setShowVerticalLines(false);
        tblBWInfo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tblBWInfoMousePressed(evt);
            }
        });
        jspBWInfo.setViewportView(tblBWInfo);

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        cbPast.setText("Alte Informationen anzeigen");
        cbPast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbPastActionPerformed(evt);
            }
        });

        cmbKategorie.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
        cmbKategorie.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbKategorieItemStateChanged(evt);
            }
        });

        cbDetail.setText("Details anzeigen");
        cbDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbDetailActionPerformed(evt);
            }
        });

        cbTooltip.setText("Schnellanzeige");
        cbTooltip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbTooltipActionPerformed(evt);
            }
        });

        cbEinzel.setText("Einzelereignisse");
        cbEinzel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbEinzelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cbPast)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cbDetail)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 125, Short.MAX_VALUE)
                                .addComponent(cmbKategorie, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cbTooltip)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbEinzel)
                                .addGap(409, 409, 409))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(cbPast)
                                        .addComponent(cmbKategorie, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cbDetail))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(cbTooltip)
                                        .addComponent(cbEinzel)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lblBW, javax.swing.GroupLayout.DEFAULT_SIZE, 658, Short.MAX_VALUE)
                                        .addComponent(jspBWInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 658, Short.MAX_VALUE))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lblBW, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jspBWInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
                                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    public void reloadTable() {
        if (this.bwinfo != null) {
            this.bwinfo.cleanup();
        }

        int art = mode;
        ListElement le = (ListElement) cmbKategorie.getSelectedItem();
        long bwikid = le.getPk();
        if (bwikid == BWInfo.ART_ALLES && (mode == BWInfo.ART_PFLEGE_STAMMDATEN || mode == BWInfo.ART_PFLEGE)) {
            art = mode;
            bwikid = 0;
        } else if (bwikid == BWInfo.ART_ALLES || bwikid == BWInfo.ART_VERWALTUNG_STAMMDATEN) {
            art = (int) bwikid;
            bwikid = 0;
        }

        if (cbPast.isSelected()) {
            this.bwinfo = new BWInfo(bwkennung, null, art, bwikid, cbEinzel.isSelected());
        } else {
            this.bwinfo = new BWInfo(bwkennung, SYSCalendar.nowDBDate(), art, bwikid, cbEinzel.isSelected());
        }
        tblBWInfo.setModel(new TMBWInfo(this.bwinfo.getAttribute(), cbDetail.isSelected(), cbTooltip.isSelected(), true));
        tblBWInfo.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // #0000029
        tblBWInfo.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        jspBWInfo.dispatchEvent(new ComponentEvent(jspBWInfo, ComponentEvent.COMPONENT_RESIZED));

        tblBWInfo.getColumnModel().getColumn(0).setCellRenderer(new RNDBWI());
        tblBWInfo.getColumnModel().getColumn(1).setCellRenderer(new RNDBWI());
        tblBWInfo.getColumnModel().getColumn(2).setCellRenderer(new RNDBWI());
        tblBWInfo.getColumnModel().getColumn(3).setCellRenderer(new RNDBWI());
        BewohnerTools.setBWLabel(lblBW, bewohner);
    }

    private void tblBWInfoMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBWInfoMousePressed
        Point p = evt.getPoint();
        ListSelectionModel lsm = tblBWInfo.getSelectionModel();

        boolean singleRowSelected = lsm.getMaxSelectionIndex() == lsm.getMinSelectionIndex();

        if (singleRowSelected) {
            int row = tblBWInfo.rowAtPoint(p);
            lsm.setSelectionInterval(row, row);
        }

        final HashMap entry = (HashMap) bwinfo.getAttribute().get(tblBWInfo.getSelectedRow());
        boolean sameUser = entry.get("anukennung").toString().equalsIgnoreCase(OPDE.getLogin().getUser().getUKennung());
        boolean unbeantwortet = entry.containsKey("unbeantwortet") && entry.get("unbeantwortet").toString().equalsIgnoreCase("true");
        final String bwinftyp = entry.get("bwinftyp").toString();
        final int interval = (Integer) entry.get("intervalmode");
        final int katart = (Integer) entry.get("katart");
        Date von = (Date) entry.get("von");


        /**
         * KORRIGIEREN
         * Eine Frage kann geändert werden (Korrektur)
         * - Vom Admin immer
         * - Wenn sie nicht HAUF ist
         * - wenn es eine Verwaltungsfrage ist
         * - bei einer Pflegefrage nur dann:
         *      - wenn sie vom selben OCUser ist.
         *      - wenn sie vom selben Tag ist.
         * - Wenn sie nicht abgesetzt ist.
         * - Nicht, falls das ein Stammdatum ist und wir das Panel von der Pflege aus geöffnet haben.
         */
        // wenn es sich nicht um eine Heimaufnahme handelt.
        boolean bearbeitenMöglich = !bwinftyp.equalsIgnoreCase("hauf") & singleRowSelected;
        // Entweder bin ich Admin, dann darf ich alles. Ansonsten nur wenn es meine Info ist und nur dann wenn sie früh genug ist.
        bearbeitenMöglich &= (OPDE.isAdmin() ||
                (sameUser &&
                        (unbeantwortet || SYSCalendar.sameDay(von.getTime(), SYSCalendar.now()) == 0)) ||
                // SYSCalendar.earlyEnough(von.getTime(), 30)) ||  << Das hier nicht mehr. Geht schief, weil manche Sachen direkt ab Mittag angesetzt werden. Somit kann man die nicht nachbearbeiten.
                katart == BWInfo.ART_STAMMDATEN || katart == BWInfo.ART_VERWALTUNG);
        // Je nachdem, von wo dieses Panel aufgerufen wurde. Aus Pflege, bzw. Pflegestammdaten Umgebung heraus, dann darf die zu
        // bearbeitetende Info nicht vom Typ Stammdaten oder Verwaltung sein.
        bearbeitenMöglich &= OPDE.isAdmin() || !((this.mode == BWInfo.ART_PFLEGE || this.mode == BWInfo.ART_PFLEGE_STAMMDATEN) &&
                (katart == BWInfo.ART_STAMMDATEN || katart == BWInfo.ART_VERWALTUNG));

        // Nur zu Testzwecken. Warum darf ich nicht nachbearbeiten.
        if (!bearbeitenMöglich) {
            if (bwinftyp.equalsIgnoreCase("hauf")) {
                OPDE.info("Heimaufnahmen können nicht bearbeitet werden.");
            }
            if (!singleRowSelected) {
                OPDE.info("Es wurde mehr als eine Zeile ausgewählt.");
            }
            if (!sameUser) {
                OPDE.info("Der Eintrag wurde von einem ANDEREN Benutzer eingetragen.");
            }
            if (!SYSCalendar.earlyEnough(von.getTime(), 30)) {
                OPDE.info("Der Eintrag ist zu alt.");
            }

        }


        if (evt.isPopupTrigger()) {
            SYSTools.unregisterListeners(menu);
            menu = new JPopupMenu();

            // KORRIGIEREN
            JMenuItem itemPopupEdit = new JMenuItem("Korrigieren");
            itemPopupEdit.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if (entry.containsKey("java")) {
                        Class c = null;
                        try {
                            c = Class.forName(entry.get("java").toString());
                            //Constructor as = c.getConstructor(Frame.class, HashMap.class, Integer.class);
                            DlgCustom dlg = (DlgCustom) c.getConstructor(Frame.class, HashMap.class, int.class).newInstance(parent, entry, DlgCustom.MODE_EDIT);
                            //Method m = c.getMethod("add", Integer.TYPE, Integer.TYPE);
                            dlg.showDialog();
                        } catch (InstantiationException ex) {
                            new DlgException(ex);
                        } catch (IllegalAccessException ex) {
                            new DlgException(ex);
                        } catch (IllegalArgumentException ex) {
                            new DlgException(ex);
                        } catch (InvocationTargetException ex) {
                            new DlgException(ex);
                        } catch (ClassNotFoundException ex) {
                            new DlgException(ex);
                        } catch (NoSuchMethodException ex) {
                            new DlgException(ex);
                        }
                    } else {
                        new DlgBWInfo(parent, bwkennung, entry, DlgBWInfo.MODE_EDIT);
                    }
                    reloadTable();
                }
            });
            menu.add(itemPopupEdit);

            JMenuItem itemPopupChange = new JMenuItem("Hat sich geändert");
            itemPopupChange.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if (entry.containsKey("java")) {
                        Class c = null;
                        try {
                            c = Class.forName(entry.get("java").toString());
                            DlgCustom dlg = (DlgCustom) c.getConstructor(Frame.class, HashMap.class, Integer.class).newInstance(parent, entry, DlgCustom.MODE_CHANGE);
                            //Method m = c.getMethod("add", Integer.TYPE, Integer.TYPE);
                            dlg.showDialog();
                        } catch (InstantiationException ex) {
                            new DlgException(ex);
                        } catch (IllegalAccessException ex) {
                            new DlgException(ex);
                        } catch (IllegalArgumentException ex) {
                            new DlgException(ex);
                        } catch (InvocationTargetException ex) {
                            new DlgException(ex);
                        } catch (ClassNotFoundException ex) {
                            new DlgException(ex);
                        } catch (NoSuchMethodException ex) {
                            new DlgException(ex);
                        }
                    } else {
                        new DlgBWInfo(parent, bwkennung, entry, DlgBWInfo.MODE_CHANGE);
                    }

                    reloadTable();
                }
            });
            menu.add(itemPopupChange);

            JMenuItem itemPopupQuit = new JMenuItem("Gilt nicht mehr");
            itemPopupQuit.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if (JOptionPane.showConfirmDialog(parent, "Möchten Sie diese Information wirklich abschließen ?",
                            entry.get("bwinfokurz").toString() + " abschließen ?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        HashMap data = new HashMap();
                        if (interval == BWInfo.MODE_INTERVAL_BYDAY) {
                            data.put("Bis", new Date(SYSCalendar.endOfDay()));
                        } else if (interval == BWInfo.MODE_INTERVAL_BYSECOND) {
                            data.put("Bis", SYSCalendar.addField(new Date(SYSCalendar.midOfDay()), -1, GregorianCalendar.SECOND)); //11:59
                        } else if (interval == BWInfo.MODE_INTERVAL_NOCONSTRAINTS) {
                            data.put("Bis", "!NOW!");
                        }

                        data.put("AbUKennung", OPDE.getLogin().getUser().getUKennung());
                        Connection db = OPDE.getDb().db;

                        try {
                            db.setAutoCommit(false);
                            db.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                            db.commit();

                            op.tools.DBHandling.updateRecord("BWInfo", data, "BWINFOID", entry.get("bwinfoid"));

                            db.commit();
                            db.setAutoCommit(true);
                        } catch (SQLException ex) {
                            try {
                                db.rollback();
                            } catch (SQLException ex1) {
                                System.exit(1);
                            }
                        }
                        data.clear();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(PnlBWInfo.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        reloadTable();
                    }
                }
            });
            menu.add(itemPopupQuit);

            JMenuItem itemPopupDelete = new JMenuItem("Löschen");
            itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if (JOptionPane.showConfirmDialog(parent, "Möchten Sie diese Information wirklich löschen ?",
                            entry.get("bwinfokurz").toString() + " löschen ?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        op.tools.DBHandling.deleteRecords("BWInfo", "BWINFOID", entry.get("bwinfoid"));
                        reloadTable();
                    }
                }
            });
            menu.add(itemPopupDelete);


            JMenuItem itemPopupClone = new JMenuItem("Diese Info erneut eintragen.");
            itemPopupClone.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    DBHandling.neueBWInfoEinfuegen(bwinftyp, bwkennung);
                    reloadTable();
                }
            });
            menu.add(itemPopupClone);

            menu.add(new JSeparator()); // ---------------------------------------

            // #0000029
            JMenuItem itemPopupPrint = new JMenuItem("Markierte Einträge drucken");
            itemPopupPrint.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    int[] sel = tblBWInfo.getSelectedRows();
                    SYSPrint.print(op.share.bwinfo.DBHandling.bwInfo2HTML((TMBWInfo) tblBWInfo.getModel(), bewohner, sel), false);
                }
            });
            menu.add(itemPopupPrint);

            /**
             * Löschen
             * Eine Frage kann gelöscht werden
             * - Wenn sie nicht HAUF ist
             * - wenn es eine Verwaltungsfrage ist
             * - bei einer Pflegefrage nur dann:
             *      - Wenn sie unbeantwortet ist
             *      - ODER
             *      - wenn sie vom selben OCUser ist. UND wenn sie noch keine 30 Minuten alt ist.
             * - man mindestens Examen oder ADMIN ist
             * - Nicht, falls das ein Stammdatum ist und wir das Panel von der Pflege aus geöffnet haben.
             */
            boolean löschenMöglich =
                    // Nicht HAUF
                    !entry.get("bwinftyp").toString().equalsIgnoreCase("hauf") && singleRowSelected &&
                            // Admins immer
                            OPDE.isAdmin() ||
                            // wenn es eine Verwaltungsfrage ist
                            ((((Integer) entry.get("katart")).intValue() == BWInfo.ART_VERWALTUNG || ((Integer) entry.get("katart")).intValue() == BWInfo.ART_STAMMDATEN) ||
                                    // oder bei einer Pflegefrage, nur wenn sie unbeantwortet ODER noch nicht so alt ist.
                                    (unbeantwortet || (sameUser && SYSCalendar.earlyEnough(von.getTime(), 30)))) &&
                                    // Nicht, falls das ein Stammdatum ist und wir das Panel von der Pflege aus geöffnet haben.
                                    !((this.mode == BWInfo.ART_PFLEGE || this.mode == BWInfo.ART_PFLEGE_STAMMDATEN) &&
                                            (((Integer) entry.get("katart")).intValue() == BWInfo.ART_STAMMDATEN ||
                                                    ((Integer) entry.get("katart")).intValue() == BWInfo.ART_VERWALTUNG));

            /**
             * Verändern
             * Eine Frage kann verändert werden
             * - Wenn sie nicht HAUF ist
             * - Keine Single Incidents sind
             * - Wenn sie nicht NoCONSTRAINT ist.
             * - ansonsten FAST IMMER
             * - Die Veränderung läuft immer ab jetzt (bzw. ab heute 0h) (je nach ByDAY oder BySECOND)
             * - Es können nur Fragen verändert werden, die BAW laufen.
             * - Sollte es bereits eine Antwort geben, die heute beginnt, dann kann man NICHT verändern.
             *   In dem Fall bietet sich "BEARBEITEN" an.
             * - ab Examen aufwärts. (Muss über SYSRights geändert werden)
             * - Nicht, falls das ein Stammdatum ist und wir das Panel von der Pflege aus geöffnet haben.
             * - Nicht bei unbeantworteten.
             *
             */
            Date bis = (Date) entry.get("bis");


            boolean verändernMöglich = !bwinftyp.equalsIgnoreCase("hauf") & singleRowSelected;
            verändernMöglich &= !unbeantwortet;
            verändernMöglich &= (interval != BWInfo.MODE_INTERVAL_NOCONSTRAINTS && interval != BWInfo.MODE_INTERVAL_SINGLE_INCIDENTS);
            verändernMöglich &= SYSCalendar.sameDay(von, SYSCalendar.nowDBDate()) < 0;
            verändernMöglich &= bis.getTime() == SYSConst.DATE_BIS_AUF_WEITERES.getTime();
            verändernMöglich &= !((this.mode == BWInfo.ART_PFLEGE || this.mode == BWInfo.ART_PFLEGE_STAMMDATEN) &&
                    (katart == BWInfo.ART_STAMMDATEN || katart == BWInfo.ART_VERWALTUNG));

            /**
             * Absetzen
             * Eine Frage kann abgesetzt werden.
             * - Wenn sie nicht HAUF ist
             * - Keine Single Incidents sind
             * - FAST Immer
             * - Sollte es bereits eine Antwort geben, die heute beginnt, dann kann man NICHT verändern.
             *   In dem Fall bietet sich "BEARBEITEN" an.
             * - ab Examen aufwärts.
             * - Nur, was nicht bereits abgesetzt ist.
             * - Nicht, falls das ein Stammdatum ist und wir das Panel von der Pflege aus geöffnet haben.
             * - Nicht bei unbeantworteten.
             *
             */
            boolean absetzenMöglich = !entry.get("bwinftyp").toString().equalsIgnoreCase("hauf") && singleRowSelected &&
                    !unbeantwortet &&
                    ((Integer) entry.get("intervalmode")).intValue() != BWInfo.MODE_INTERVAL_SINGLE_INCIDENTS &&
                    SYSCalendar.sameDay(von, SYSCalendar.nowDBDate()) < 0 &&
                    bis.getTime() == SYSConst.DATE_BIS_AUF_WEITERES.getTime() &&
                    !((this.mode == BWInfo.ART_PFLEGE || this.mode == BWInfo.ART_PFLEGE_STAMMDATEN) &&
                            (((Integer) entry.get("katart")).intValue() == BWInfo.ART_STAMMDATEN ||
                                    ((Integer) entry.get("katart")).intValue() == BWInfo.ART_VERWALTUNG));


            ocs.setEnabled(this, "itemPopupEdit", itemPopupEdit, bearbeitenMöglich);
            ocs.setEnabled(this, "itemPopupChange", itemPopupChange, verändernMöglich);
            ocs.setEnabled(this, "itemPopupDelete", itemPopupDelete, löschenMöglich);
            ocs.setEnabled(this, "itemPopupQuit", itemPopupQuit, absetzenMöglich);
            ocs.setEnabled(this, "itemPopupClone", itemPopupClone, singleRowSelected && (interval == BWInfo.MODE_INTERVAL_NOCONSTRAINTS || interval == BWInfo.MODE_INTERVAL_SINGLE_INCIDENTS));

            if (singleRowSelected) {
                // Dokumente zu Stammdaten werden in der Pflege nicht angezeigt.
                if (!((this.mode == BWInfo.ART_PFLEGE || this.mode == BWInfo.ART_PFLEGE_STAMMDATEN) &&
                        (((Integer) entry.get("katart")).intValue() == BWInfo.ART_STAMMDATEN ||
                                ((Integer) entry.get("katart")).intValue() == BWInfo.ART_VERWALTUNG))) {
                    menu.add(new JSeparator());

                    // TODO: Das hier muss wieder rein
//                    menu.add(op.share.vorgang.DBHandling.getVorgangContextMenu(parent, "BWInfo", ((Long) entry.get("bwinfoid")).longValue(), bwkennung, fileActionListener));

                    long bwinfoid = ((Long) entry.get("bwinfoid")).longValue();
                    EntityManager em = OPDE.createEM();
                    Query query = em.createNamedQuery("BWInfo.findByBwinfoid");
                    query.setParameter("bwinfoid", bwinfoid);
                    entity.info.BWInfo bwinfo = (entity.info.BWInfo) query.getSingleResult();
//                    menu.add(SYSFilesTools.getSYSFilesContextMenu(parent, bwinfo, fileActionListener));
                    em.close();
                    //menu.add(SYSFiles.getOPFilesContextMenu(parent, "BWInfo", ((Long) entry.get("bwinfoid")).longValue(), bwkennung, tblBWInfo, true, true, SYSFiles.CODE_PLANUNG, fileActionListener));
                }
            }

            menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
        } else if (bearbeitenMöglich && evt.getClickCount() == 2) { // Bearbeiten, wenn möglich
            if (entry.containsKey("java")) {
                Class c = null;
                try {
                    c = Class.forName(entry.get("java").toString());
                    //Constructor as = c.getConstructor(Frame.class, HashMap.class, Integer.class);
                    DlgCustom dlg = (DlgCustom) c.getConstructor(Frame.class, HashMap.class, int.class).newInstance(parent, entry, DlgCustom.MODE_EDIT);
                    //Method m = c.getMethod("add", Integer.TYPE, Integer.TYPE);
                    dlg.showDialog();
                } catch (InstantiationException ex) {
                    new DlgException(ex);
                } catch (IllegalAccessException ex) {
                    new DlgException(ex);
                } catch (IllegalArgumentException ex) {
                    new DlgException(ex);
                } catch (InvocationTargetException ex) {
                    new DlgException(ex);
                } catch (ClassNotFoundException ex) {
                    new DlgException(ex);
                } catch (NoSuchMethodException ex) {
                    new DlgException(ex);
                }
            } else {
                new DlgBWInfo(parent, bwkennung, entry, DlgBWInfo.MODE_EDIT);
            }
            reloadTable();
        }
    }//GEN-LAST:event_tblBWInfoMousePressed

    private void cbPastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbPastActionPerformed
        if (ignoreEvent) {
            return;
        }
        SYSPropsTools.storeState(this.getClass().getName() + "::cbPast", cbPast);
        reloadTable();
    }//GEN-LAST:event_cbPastActionPerformed

    private void jspBWInfoComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspBWInfoComponentResized
        JScrollPane jsp = (JScrollPane) evt.getComponent();
        Dimension dim = jsp.getSize();
        // Größe der Text Spalten im DFN ändern.
        // Summe der fixen Spalten + ein bisschen
        int textWidth = dim.width - (200 + 25 + 120 + 80);
        TableColumnModel tcm1 = tblBWInfo.getColumnModel();

        tcm1.getColumn(TMBWInfo.COL_ATTRIBNAME).setPreferredWidth(200);
        tcm1.getColumn(TMBWInfo.COL_HTML).setPreferredWidth(textWidth);
        tcm1.getColumn(TMBWInfo.COL_VON).setPreferredWidth(120);
        tcm1.getColumn(TMBWInfo.COL_BIS).setPreferredWidth(80);
        tcm1.getColumn(TMBWInfo.COL_ATTRIBNAME).setHeaderValue("Kategorie");
        tcm1.getColumn(TMBWInfo.COL_HTML).setHeaderValue("Details");
        tcm1.getColumn(TMBWInfo.COL_VON).setHeaderValue("Von/Am");
        tcm1.getColumn(TMBWInfo.COL_BIS).setHeaderValue("Bis");
    }//GEN-LAST:event_jspBWInfoComponentResized

    private void cmbKategorieItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbKategorieItemStateChanged
        if (ignoreEvent) {
            return;
        }
        String classname = this.getClass().getName() + ":" + mode;
        SYSPropsTools.storeState(classname + "::cmbKategorie", cmbKategorie);
        reloadTable();
    }//GEN-LAST:event_cmbKategorieItemStateChanged

    private void cbDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbDetailActionPerformed
        if (ignoreEvent) {
            return;
        }
        SYSPropsTools.storeState(this.getClass().getName() + "::cbDetail", cbDetail);
        reloadTable();
    }//GEN-LAST:event_cbDetailActionPerformed

    private void cbEinzelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbEinzelActionPerformed
        if (ignoreEvent) {
            return;
        }
        SYSPropsTools.storeState(this.getClass().getName() + "::cbEinzel", cbEinzel);
        reloadTable();
    }//GEN-LAST:event_cbEinzelActionPerformed

    private void cbTooltipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbTooltipActionPerformed
        if (ignoreEvent) {
            return;
        }
        SYSPropsTools.storeState(this.getClass().getName() + "::cbTooltip", cbTooltip);
        reloadTable();
    }//GEN-LAST:event_cbTooltipActionPerformed

    /*
     * mit dieser Methode wird nach neuen Informationen gefragt. Sie wird hier als public implementiert, damit man sie 
     * von umgebenden Fenstern aus aufrufen kann.
     * <ul>
     * <li>Gibts die Frage schon. Das heisst aktuell. <code>Von <= now() AND Bis > now()</code> Wenn nicht, 
     * dann kann sie einfach als unbeantwortet eingefügt werden. Nachbearbeitet wird sie dann durch Aufruf von "Bearbeiten" aus
     * dem Kontextmenü.<b>ENDE</b></li>
     * <li>Wenn ja, dann passiert gar nichts. Rückgabe = ""</li>
     * </ul>
     */
    public void neu() {
        // Von der Pflege aus kann man zwar STAMMDATEN sehen, soll aber keine neuen eingeben können.
        int mymode = this.mode;
        if (this.mode == BWInfo.ART_PFLEGE_STAMMDATEN) {
            mymode = BWInfo.ART_PFLEGE;
        }
        DlgBWInfoTypVorlage dlg = new DlgBWInfoTypVorlage(parent, this.bwkennung, mymode);

        if (dlg.getBWIKID() >= 0) { // Hat nicht auf Abbrechen geclickt.

            if (dlg.getIntervalmode() == BWInfo.MODE_INTERVAL_SINGLE_INCIDENTS && !cbEinzel.isSelected()) {
                ignoreEvent = true;
                cbEinzel.setSelected(true);
                ignoreEvent = false;
                SYSPropsTools.storeState(this.getClass().getName() + "::cbEinzel", cbEinzel);
            }
            if (dlg.getBWIKID() != ((ListElement) cmbKategorie.getSelectedItem()).getPk()) {
                SYSTools.selectInComboBox(cmbKategorie, dlg.getBWIKID());
            } else {
                reloadTable();
            }
        }
    }

    public void print() {
        SYSPrint.print(SYSTools.htmlUmlautConversion(op.share.bwinfo.DBHandling.bwInfo2HTML((TMBWInfo) tblBWInfo.getModel(), bewohner, null)), false);

//        HashMap params = new HashMap();
//        params.put("BWName", SYSTools.unHTML(lblBW.getText()));
//        params.put("formname", "Bewohner-Daten");
//
//        HashMap assign = new HashMap();
//        assign.put("bwinfo", TMBWInfo.COL_PRINT);
//        assign.put("kat", TMBWInfo.COL_KATBEZ);
//
//        JRDSTableModel jrds = new JRDSTableModel(tblBWInfo.getModel(), assign);
//
//        SYSPrint.printReport(preview, jrds, params, "bwinfo", dialog);
//        if (!preview && !dialog) {
//            JOptionPane.showMessageDialog(this, "Der Druckvorgang ist abgeschlossen.", "Drucker", JOptionPane.INFORMATION_MESSAGE);
//        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbDetail;
    private javax.swing.JCheckBox cbEinzel;
    private javax.swing.JCheckBox cbPast;
    private javax.swing.JCheckBox cbTooltip;
    private javax.swing.JComboBox cmbKategorie;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jspBWInfo;
    private javax.swing.JLabel lblBW;
    private javax.swing.JTable tblBWInfo;
    // End of variables declaration//GEN-END:variables

    public BWInfo getBWInfo() {
        return bwinfo;
    }
}
