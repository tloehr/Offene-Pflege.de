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

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import com.toedter.calendar.JDateChooser;
import entity.Bewohner;
import entity.BewohnerTools;
import entity.planung.DFN;
import entity.planung.DFNTools;
import entity.planung.NursingProcess;
import entity.verordnungen.BHPTools;
import op.OPDE;
import op.tools.*;
import org.jdesktop.swingx.HorizontalLayout;
import org.jdesktop.swingx.VerticalLayout;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author root
 */
public class PnlDFN extends NursingRecordsPanel {

    public static final String internalClassID = "nursingrecords.dfn";

    String bwkennung;
    Bewohner bewohner;
    JPopupMenu menu;

    private boolean initPhase;

    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private JDateChooser jdcDatum;
    private JComboBox cmbSchicht;
    private int DFN_MAX_MINUTES_TO_WITHDRAW;
    private JideButton addButton;
    private ArrayList<NursingProcess> involvedNPs;

    public PnlDFN(Bewohner bewohner, JScrollPane jspSearch) {
        initComponents();
        this.jspSearch = jspSearch;
        initPanel();
        change2Bewohner(bewohner);
    }


    private void initPanel() {
        DFN_MAX_MINUTES_TO_WITHDRAW = Integer.parseInt(OPDE.getProps().getProperty("dfn_max_minutes_to_withdraw"));
//        planungCollapsiblePaneMap = new HashMap<NursingProcess, CollapsiblePane>();
//        categoryCPMap = new HashMap<BWInfoKat, CollapsiblePane>();
//        planungen = new HashMap<BWInfoKat, java.util.List<NursingProcess>>();
        involvedNPs = new ArrayList<NursingProcess>();
        prepareSearchArea();

    }

    @Override
    public void cleanup() {
        jdcDatum.cleanup();
        SYSTools.unregisterListeners(this);
    }

    private void btnLockActionPerformed(ActionEvent e) {
        change2Bewohner(bewohner);
    }

    @Override
    public void reload() {
        reloadTable();
    }


    @Override
    public void change2Bewohner(Bewohner bewohner) {
        this.bewohner = bewohner;

        OPDE.getDisplayManager().setMainMessage(BewohnerTools.getBWLabelText(bewohner));
        initPhase = true;


        prepareSearchArea();

        initPhase = false;
        reloadDisplay();


//        btnLock.setEnabled(readOnly);
//
//        ignoreJDCEvent = true;
//        jdcDatum.setDate(SYSCalendar.nowDBDate());
//        btnForward.setEnabled(false); // In die Zukunft kann man nicht gucken.
//
//        ArrayList hauf = DBRetrieve.getHauf(bwkennung);
//        Date[] d = (Date[]) hauf.get(0);
//        jdcDatum.setMinSelectableDate(d[0]);
//
//
//        ignoreJDCEvent = false;
//        cmbSchicht.setSelectedIndex(SYSCalendar.ermittleSchicht() + 1);

    }


    private void reloadDisplay() {
        /***
         *               _                 _ ____  _           _
         *      _ __ ___| | ___   __ _  __| |  _ \(_)___ _ __ | | __ _ _   _
         *     | '__/ _ \ |/ _ \ / _` |/ _` | | | | / __| '_ \| |/ _` | | | |
         *     | | |  __/ | (_) | (_| | (_| | |_| | \__ \ |_) | | (_| | |_| |
         *     |_|  \___|_|\___/ \__,_|\__,_|____/|_|___/ .__/|_|\__,_|\__, |
         *                                              |_|            |___/
         */
        initPhase = true;

        final boolean withworker = false;
        if (withworker) {

//            OPDE.getMainframe().setBlocked(true);
//            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));
//
//            cpDFN.removeAll();
//
//            SwingWorker worker = new SwingWorker() {
//
//                @Override
//                protected Object doInBackground() throws Exception {
//                    try {
//                        int progress = 0;
//
//                        if (kategorien.isEmpty()) {
//                            // Elmininate empty categories
//                            for (final BWInfoKat kat : BWInfoKatTools.getKategorien()) {
//                                if (!NursingProcessTools.findByKategorieAndBewohner(bewohner, kat).isEmpty()) {
//                                    kategorien.add(kat);
//                                }
//                            }
//                        }
//
//                        cpPlan.setLayout(new JideBoxLayout(cpPlan, JideBoxLayout.Y_AXIS));
//                        for (BWInfoKat kat : kategorien) {
//                            progress++;
//                            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, kategorien.size()));
//                            cpPlan.add(createCollapsiblePanesFor(kat));
//                        }
//
//
//                    } catch (Exception e) {
//                        OPDE.fatal(e);
//                    }
//                    return null;
//                }
//
//                @Override
//                protected void done() {
//                    cpPlan.addExpansion();
//                    initPhase = false;
//                    OPDE.getDisplayManager().setProgressBarMessage(null);
//                    OPDE.getMainframe().setBlocked(false);
//                }
//            };
//            worker.execute();

        } else {

            cpDFN.removeAll();
            involvedNPs = DFNTools.getInvolvedNPs(DFNTools.SHIFT_EARLY, bewohner, jdcDatum.getDate());

            cpDFN.setLayout(new JideBoxLayout(cpDFN, JideBoxLayout.Y_AXIS));
            for (NursingProcess np : involvedNPs) {
                cpDFN.add(createCollapsiblePanesFor(np));
            }
            cpDFN.addExpansion();
        }
        initPhase = false;
    }


    private CollapsiblePane createCollapsiblePanesFor(final NursingProcess np) {
        final CollapsiblePane npPane = new CollapsiblePane(np.getStichwort() + " ("+np.getKategorie().getBezeichnung()+")");

//        katpane.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent mouseEvent) {
//                try {
//                    if (katpane.isCollapsed()) {
//                        katpane.setCollapsed(false);
//                    } else {
//                        // collapse all children
//                        for (NursingProcess planung : planungen.get(kat)) {
//                            planungCollapsiblePaneMap.get(planung).setCollapsed(true);
//                        }
//                        katpane.setCollapsed(true);
//                    }
//                } catch (PropertyVetoException e) {
//                    OPDE.error(e);
//                }
//            }
//        });
        npPane.setSlidingDirection(SwingConstants.SOUTH);
        npPane.setBackground(np.getKategorie().getBackgroundHeader());
        npPane.setForeground(np.getKategorie().getForegroundHeader());
        npPane.setOpaque(false);

        JPanel npPanel = new JPanel();
        npPanel.setLayout(new VerticalLayout());
        npPanel.setBackground(np.getKategorie().getBackgroundContent());


        for (DFN dfn : DFNTools.getDFNs(DFNTools.SHIFT_EARLY, np, jdcDatum.getDate())) {



            final JideButton btnDFN = GUITools.createHyperlinkButton(dfn.getIntervention().getBezeichnung(), SYSConst.icon22redo, null);
            btnDFN.setForeground(np.getKategorie().getForegroundContent());
            btnDFN.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnDFN.setIcon(SYSConst.icon22apply);
                }
            });
            npPanel.add(btnDFN);
        }
        npPane.setContentPane(npPanel);
        npPane.setCollapsible(false);
        try {
            npPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }
        return npPane;
    }

    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);

        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout());
        mypanel.setBackground(Color.WHITE);

        CollapsiblePane searchPane = new CollapsiblePane(OPDE.lang.getString(internalClassID));
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);

        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }

        GUITools.addAllComponents(mypanel, addCommands());
        GUITools.addAllComponents(mypanel, addFilters());

        searchPane.setContentPane(mypanel);

        searchPanes.add(searchPane);
        searchPanes.addExpansion();

    }

    private java.util.List<Component> addFilters() {
        java.util.List<Component> list = new ArrayList<Component>();

        String[] strs = GUITools.getLocalizedMessages(new String[]{"misc.msg.everything", internalClassID + ".shift.veryearly", internalClassID + ".shift.early", internalClassID + ".shift.late", internalClassID + ".shift.verylate"});

        cmbSchicht = new JComboBox(new DefaultComboBoxModel(strs));
        cmbSchicht.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbSchicht.setSelectedIndex(SYSCalendar.ermittleSchicht() + 1);
        cmbSchicht.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (!initPhase) {
                    reloadTable();
                }
            }
        });
        list.add(cmbSchicht);

        jdcDatum = new JDateChooser(new Date());
        jdcDatum.setFont(new Font("Arial", Font.PLAIN, 14));
        jdcDatum.setMinSelectableDate(BHPTools.getMinDatum(bewohner));

        jdcDatum.setBackground(Color.WHITE);
        jdcDatum.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (initPhase) {
                    return;
                }
                if (evt.getPropertyName().equals("date")) {
                    reloadTable();
                }
            }
        });
        list.add(jdcDatum);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setLayout(new HorizontalLayout(5));
        buttonPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        JButton homeButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_start.png")));
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                jdcDatum.setDate(jdcDatum.getMinSelectableDate());
            }
        });
        homeButton.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_start_pressed.png")));
        homeButton.setBorder(null);
        homeButton.setBorderPainted(false);
        homeButton.setOpaque(false);
        homeButton.setContentAreaFilled(false);
        homeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton backButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_back.png")));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                jdcDatum.setDate(SYSCalendar.addDate(jdcDatum.getDate(), -1));
            }
        });
        backButton.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_back_pressed.png")));
        backButton.setBorder(null);
        backButton.setBorderPainted(false);
        backButton.setOpaque(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));


        JButton fwdButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_play.png")));
        fwdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                jdcDatum.setDate(SYSCalendar.addDate(jdcDatum.getDate(), 1));
            }
        });
        fwdButton.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_play_pressed.png")));
        fwdButton.setBorder(null);
        fwdButton.setBorderPainted(false);
        fwdButton.setOpaque(false);
        fwdButton.setContentAreaFilled(false);
        fwdButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton endButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_end.png")));
        endButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                jdcDatum.setDate(new Date());
            }
        });
        endButton.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_end_pressed.png")));
        endButton.setBorder(null);
        endButton.setBorderPainted(false);
        endButton.setOpaque(false);
        endButton.setContentAreaFilled(false);
        endButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));


        buttonPanel.add(homeButton);
        buttonPanel.add(backButton);
        buttonPanel.add(fwdButton);
        buttonPanel.add(endButton);

        list.add(buttonPanel);

        return list;
    }

    private void jspDFNComponentResized(ComponentEvent evt) {
//        JScrollPane jsp = (JScrollPane) evt.getComponent();
//        Dimension dim = jsp.getSize();
//        // Größe der Text Spalten im DFN ändern.
//        // Summe der fixen Spalten  = 175 + ein bisschen
//        int textWidth = dim.width - (50 + 80 + 55 + 80 + 25);
//        TableColumnModel tcm1 = tblDFN.getColumnModel();
//        if (tcm1.getColumnCount() < 4) {
//            return;
//        }
//
//        //tcm1.getColumn(TMDFN.COL_MassID).setPreferredWidth(50);
//        tcm1.getColumn(TMDFN.COL_BEZEICHNUNG).setPreferredWidth(textWidth / 2);
//        tcm1.getColumn(TMDFN.COL_ZEIT).setPreferredWidth(80);
//        tcm1.getColumn(TMDFN.COL_STATUS).setPreferredWidth(55);
//        tcm1.getColumn(TMDFN.COL_UKENNUNG).setPreferredWidth(80);
//        tcm1.getColumn(TMDFN.COL_BEMDFN).setPreferredWidth(textWidth / 2);
//
//        //tcm1.getColumn(0).setHeaderValue("ID");
//        tcm1.getColumn(TMDFN.COL_BEZEICHNUNG).setHeaderValue("Bezeichnung");
//        tcm1.getColumn(TMDFN.COL_ZEIT).setHeaderValue("Zeit");
//        tcm1.getColumn(TMDFN.COL_STATUS).setHeaderValue("Status");
//        tcm1.getColumn(TMDFN.COL_UKENNUNG).setHeaderValue("PflegerIn");
//        tcm1.getColumn(TMDFN.COL_BEMDFN).setHeaderValue("Hinweis");
    }

    private java.util.List<Component> addCommands() {

        java.util.List<Component> list = new ArrayList<Component>();

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.INSERT)) {
            addButton = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".btnadd"), new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")), null);
//            addButton.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//
//                    if (VerordnungTools.hasBedarf(bewohner)) {
//
//                        final JidePopup popup = new JidePopup();
//
//                        DlgBedarf dlg = new DlgBedarf(bewohner, new Closure() {
//                            @Override
//                            public void execute(Object o) {
//                                popup.hidePopup();
//                                if (o != null) {
//                                    reloadTable();
//                                }
//                            }
//                        });
//
//                        popup.setMovable(false);
//                        popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
//                        popup.getContentPane().add(dlg);
//                        popup.setOwner(addButton);
//                        popup.removeExcludedComponent(addButton);
//                        popup.setDefaultFocusComponent(dlg);
//                        Point p = new Point(addButton.getX(), addButton.getY());
//                        SwingUtilities.convertPointToScreen(p, addButton);
//                        popup.showPopup(p.x, p.y - (int) dlg.getPreferredSize().getHeight()); // - (int) addButton.getPreferredSize().getHeight()
//                    } else {
//                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Keine Bedarfsverordnungen vorhanden"));
//                    }
//                }
//            });
            list.add(addButton);
        }

        return list;
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jspDFN = new JScrollPane();
        cpDFN = new CollapsiblePanes();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== jspDFN ========
        {
            jspDFN.setBorder(new BevelBorder(BevelBorder.RAISED));
            jspDFN.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    jspDFNComponentResized(e);
                }
            });

            //======== cpDFN ========
            {
                cpDFN.setLayout(new BoxLayout(cpDFN, BoxLayout.X_AXIS));
            }
            jspDFN.setViewportView(cpDFN);
        }
        add(jspDFN);
    }// </editor-fold>//GEN-END:initComponents

//    private void btnNowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNowActionPerformed
//        jdcDatum.setDate(SYSCalendar.today_date());
//        btnForward.setEnabled(SYSCalendar.sameDay(jdcDatum.getDate(), SYSCalendar.nowDBDate()) < 0);
//    }//GEN-LAST:event_btnNowActionPerformed
//
//    private void btnForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnForwardActionPerformed
//        jdcDatum.setDate(SYSCalendar.addDate(jdcDatum.getDate(), 1));
//        btnForward.setEnabled(SYSCalendar.sameDay(jdcDatum.getDate(), SYSCalendar.nowDBDate()) < 0);
//    }//GEN-LAST:event_btnForwardActionPerformed
//
//    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
//        jdcDatum.setDate(SYSCalendar.addDate(jdcDatum.getDate(), -1));
//        btnForward.setEnabled(SYSCalendar.sameDay(jdcDatum.getDate(), SYSCalendar.nowDBDate()) < 0);
//    }//GEN-LAST:event_btnBackActionPerformed
//
//    private void btnTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTopActionPerformed
//        jdcDatum.setDate(jdcDatum.getMinSelectableDate());
//        btnForward.setEnabled(SYSCalendar.sameDay(jdcDatum.getDate(), SYSCalendar.nowDBDate()) < 0);
//    }//GEN-LAST:event_btnTopActionPerformed
//
//    private void jdcDatumPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jdcDatumPropertyChange
//        if (!evt.getPropertyName().equals("date") || ignoreJDCEvent) {
//            return;
//        }
//        ignoreJDCEvent = true;
//        SYSCalendar.checkJDC((JDateChooser) evt.getSource());
//        ignoreJDCEvent = false;
//        reloadTable();
//    }//GEN-LAST:event_jdcDatumPropertyChange

    private void cmbSchichtItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbSchichtItemStateChanged
        if (evt.getStateChange() != ItemEvent.SELECTED) {
            return;
        }
        reloadTable();
    }//GEN-LAST:event_cmbSchichtItemStateChanged

    private void reloadTable() {
//        tblDFN.setModel(new TMDFN(bewohner, jdcDatum.getDate(), cmbSchicht.getSelectedIndex() - 1));
//        tblDFN.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
//        jspDFN.dispatchEvent(new ComponentEvent(jspDFN, ComponentEvent.COMPONENT_RESIZED));
//        tblDFN.getColumnModel().getColumn(TMDFN.COL_BEZEICHNUNG).setCellRenderer(new RNDDFN());
//        tblDFN.getColumnModel().getColumn(TMDFN.COL_ZEIT).setCellRenderer(new RNDDFN());
//        tblDFN.getColumnModel().getColumn(TMDFN.COL_STATUS).setCellRenderer(new RNDDFN());
//        tblDFN.getColumnModel().getColumn(TMDFN.COL_UKENNUNG).setCellRenderer(new RNDDFN());
//        tblDFN.getColumnModel().getColumn(TMDFN.COL_BEMDFN).setCellRenderer(new RNDDFN());

    }

    private void tblDFNMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDFNMousePressed
//        if (readOnly) {
//            return;
//        }
//        final TMDFN tm = (TMDFN) tblDFN.getModel();
//        if (tm.getRowCount() == 0) {
//            return;
//        }
//        Point p = evt.getPoint();
//        final int col = tblDFN.columnAtPoint(p);
//        final int row = tblDFN.rowAtPoint(p);
//        ListSelectionModel lsm = tblDFN.getSelectionModel();
//        lsm.setSelectionInterval(row, row);
//        final long dfnid = ((Long) tm.getValueAt(row, TMDFN.COL_DFNID)).longValue();
//        //final long termid = ((Long) tm.getValueAt(row, TMDFN.COL_TERMID)).longValue();
//        int status = ((Integer) tm.getValueAt(row, TMDFN.COL_STATUS)).intValue();
//        String ukennung = ((String) tm.getValueAt(row, TMDFN.COL_UKENNUNG)).toString();
//        long abdatum = ((Long) tm.getValueAt(row, TMDFN.COL_BIS)).longValue();
//        //final long soll = ((Long) tm.getValueAt(row, TMDFN.COL_SOLL)).longValue();
//        //final int szeit = ((Integer) tm.getValueAt(row, TMDFN.COL_SZEIT)).intValue();
//        //final long relid = ((Long) tm.getValueAt(row, TMDFN.COL_RELID)).longValue();
//        long mdate = ((Long) tm.getValueAt(row, TMDFN.COL_MDATE)).longValue();
//
//        boolean changeable =
//                // Diese Kontrolle stellt sicher, dass ein User nur seine eigenen Einträge und das auch nur
//                // eine halbe Stunde lang bearbeiten kann.
//                !abwesend &&
//                        SYSCalendar.isInFuture(abdatum) &&
//                        (ukennung.equals("") ||
//                                (ukennung.equalsIgnoreCase(OPDE.getLogin().getUser().getUKennung()) &&
//                                        SYSCalendar.earlyEnough(mdate, 30)));
//        OPDE.debug(changeable ? "changeable" : "NOT changeable");
//        if (changeable) {
//            // Drückt der Anwender auch wirklich mit der LINKEN Maustaste auf die mittlere Spalte.
//            if (!evt.isPopupTrigger() && col == TMDFN.COL_STATUS) {
//                boolean fullReloadNecessary = false;
//                status++;
//                if (status > 1) {
//                    status = 0;
//                }
//                HashMap hm = new HashMap();
//                hm.put("Status", status);
//                if (status == 0) {
//                    hm.put("UKennung", null);
//                    hm.put("Ist", null);
//                    hm.put("IZeit", null);
//                    hm.put("Dauer", 0);
//                    DBHandling.updateRecord("DFN", hm, "DFNID", dfnid);
//
//                } else {
//                    hm.put("UKennung", OPDE.getLogin().getUser().getUKennung());
//                    hm.put("Ist", "!NOW!");
//                    hm.put("IZeit", SYSCalendar.ermittleZeit());
//                    DBHandling.updateRecord("DFN", hm, "DFNID", dfnid);
//                }
//
//                hm.clear();
//                tm.setUpdate(row, status);
//                if (fullReloadNecessary) {
//                    reloadTable();
//                }
//            }
//
//        }
//        // Nun noch Menüeinträge
//        if (evt.isPopupTrigger()) {
//            SYSTools.unregisterListeners(menu);
//            menu = new JPopupMenu();
//
//            JMenuItem itemPopupRefuse = new JMenuItem("Verweigert / nicht durchgeführt");
//            itemPopupRefuse.addActionListener(new java.awt.event.ActionListener() {
//
//                public void actionPerformed(java.awt.event.ActionEvent evt) {
//                    HashMap hm = new HashMap();
//                    hm.put("Status", TMDFN.STATUS_VERWEIGERT);
//                    hm.put("UKennung", OPDE.getLogin().getUser().getUKennung());
//                    hm.put("Ist", "!NOW!");
//                    hm.put("IZeit", SYSCalendar.ermittleZeit());
//                    DBHandling.updateRecord("DFN", hm, "DFNID", dfnid);
//                    hm.clear();
//                    tm.setUpdate(row, TMDFN.STATUS_VERWEIGERT);
//                    //tm.reload(row, col);
//                }
//            });
//            menu.add(itemPopupRefuse);
//            ocs.setEnabled(this, "itemPopupRefuse", itemPopupRefuse, changeable && status == TMDFN.STATUS_OFFEN);
//
//            if (changeable) {
//                menu.add(new JSeparator());
//                int[] mins = new int[]{1, 2, 3, 4, 5, 10, 15, 20, 30, 45, 60, 120, 240, 360};
//                HashMap text = new HashMap();
//                text.put(60, "1 Stunde");
//                text.put(120, "2 Stunden");
//                text.put(240, "3 Stunden");
//                text.put(360, "4 Stunden");
//                for (int i = 0; i < mins.length; i++) {
//                    String einheit = mins[i] + " Minuten";
//                    if (text.containsKey(mins[i])) {
//                        einheit = mins[i] + " " + text.get(mins[i]).toString();
//                    }
//                    JMenuItem item = new JMenuItem(einheit);
//                    final int minutes = mins[i];
//                    item.addActionListener(new java.awt.event.ActionListener() {
//
//                        public void actionPerformed(java.awt.event.ActionEvent evt) {
//                            HashMap hm = new HashMap();
//                            hm.put("Dauer", minutes);
//                            DBHandling.updateRecord("DFN", hm, "DFNID", dfnid);
//                            hm.clear();
//                            tm.reload(row, TMDFN.COL_BEZEICHNUNG);
//                        }
//                    });
//                    menu.add(item);
//                    item.setEnabled(status == TMDFN.STATUS_ERLEDIGT);
//                }
//                text.clear();
//            }
//
//            menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
//        }
//    }//GEN-LAST:event_tblDFNMousePressed
//
//    private void jspDFNComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jspDFNComponentResized
//        JScrollPane jsp = (JScrollPane) evt.getComponent();
//        Dimension dim = jsp.getSize();
//        // Größe der Text Spalten im DFN ändern.
//        // Summe der fixen Spalten  = 175 + ein bisschen
//        int textWidth = dim.width - (50 + 80 + 55 + 80 + 25);
//        TableColumnModel tcm1 = tblDFN.getColumnModel();
//        if (tcm1.getColumnCount() < 4) {
//            return;
//        }
//
//        //tcm1.getColumn(TMDFN.COL_MassID).setPreferredWidth(50);
//        tcm1.getColumn(TMDFN.COL_BEZEICHNUNG).setPreferredWidth(textWidth / 2);
//        tcm1.getColumn(TMDFN.COL_ZEIT).setPreferredWidth(80);
//        tcm1.getColumn(TMDFN.COL_STATUS).setPreferredWidth(55);
//        tcm1.getColumn(TMDFN.COL_UKENNUNG).setPreferredWidth(80);
//        tcm1.getColumn(TMDFN.COL_BEMPLAN).setPreferredWidth(textWidth / 2);
//
//        //tcm1.getColumn(0).setHeaderValue("ID");
//        tcm1.getColumn(TMDFN.COL_BEZEICHNUNG).setHeaderValue("Bezeichnung");
//        tcm1.getColumn(TMDFN.COL_ZEIT).setHeaderValue("Zeit");
//        tcm1.getColumn(TMDFN.COL_STATUS).setHeaderValue("Status");
//        tcm1.getColumn(TMDFN.COL_UKENNUNG).setHeaderValue("PflegerIn");
//        tcm1.getColumn(TMDFN.COL_BEMPLAN).setHeaderValue("Hinweis");
    }//GEN-LAST:event_jspDFNComponentResized

//    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
//        Object[] sel = DlgMassSelect.showDialog(parent, true);
//        if (sel.length > 0) {
//            HashMap hmnew = new HashMap();
//            hmnew.put("Status", TMDFN.STATUS_ERLEDIGT);
//            hmnew.put("UKennung", OPDE.getLogin().getUser().getUKennung());
//            hmnew.put("BWKennung", bwkennung);
//            hmnew.put("TermID", 0);
//            hmnew.put("Soll", "!NOW!");
//            hmnew.put("StDatum", "!NOW!");
//            hmnew.put("SZeit", SYSCalendar.ermittleZeit());
//            hmnew.put("Ist", "!NOW!");
//            hmnew.put("IZeit", SYSCalendar.ermittleZeit());
//            for (int i = 0; i < sel.length; i++) {
//                // Zuerst neuen DFN einfügen.
//                ListElement elmass = (ListElement) sel[i];
//                long massID = elmass.getPk();
//                hmnew.put("MassID", massID);
//                double dauer = ((BigDecimal) DBRetrieve.getSingleValue("Massnahmen", "Dauer", "MassID", massID)).doubleValue();
//                hmnew.put("Dauer", dauer);
//                DBHandling.insertRecord("DFN", hmnew);
//            }
//            reloadTable();
//        }
//    }//GEN-LAST:event_btnNewActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jspDFN;
    private CollapsiblePanes cpDFN;
    // End of variables declaration//GEN-END:variables
}
