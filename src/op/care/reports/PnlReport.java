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
package op.care.reports;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import com.toedter.calendar.JDateChooser;
import entity.NReport;
import entity.NReportTAGS;
import entity.NReportTAGSTools;
import entity.NReportTools;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.nursingprocess.DFNTools;
import entity.system.SYSPropsTools;
import op.OPDE;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.HorizontalLayout;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.DateMidnight;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author root
 */
public class PnlReport extends NursingRecordsPanel {

    public static final String internalClassID = "nursingrecords.reports";
    private int MAX_TEXT_LENGTH = 80;
    /**
     * Dies ist immer der zur Zeit ausgewählte Bericht. null, wenn nichts ausgewählt ist. Wenn mehr als ein
     * Bericht ausgewählt wurde, steht hier immer der Verweis auf den ERSTEN Bericht der Auswahl.
     */

    private final int WEEKS_BACK = 4;
    private JDateChooser jdcVon;
    private JXSearchField txtSearch;
    private CollapsiblePane panelTags;
    private JToggleButton tbShowReplaced, tbShowIDs, tbFilesOnly;
    private JComboBox cmbAuswahl;

    private HashMap<DateMidnight, ArrayList<NReport>> dayMap;
    //    private HashMap<DateMidnight, CollapsiblePane> dayCPMap;
    private HashMap<NReport, CollapsiblePane> reportMap;

    private Resident resident;
    private JPopupMenu menu;
    private boolean initPhase;

    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private NReport firstReport;


    /**
     * Creates new form PnlReport
     */
    public PnlReport(Resident resident, JScrollPane jspSearch) {
        this.initPhase = true;
        initComponents();
        this.jspSearch = jspSearch;

        prepareSearchArea();

        initPanel();
        this.initPhase = false;

        switchResident(resident);


    }

    private void initPanel() {

        dayMap = new HashMap<DateMidnight, ArrayList<NReport>>();
//        dayCPMap = new HashMap<DateMidnight, CollapsiblePane>();
        reportMap = new HashMap<NReport, CollapsiblePane>();
        prepareSearchArea();
    }

    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);
        searchPanes.add(addCommands());
        searchPanes.add(addFilters());
        searchPanes.addExpansion();
    }

    @Override
    public void reload() {
        if (tbFilesOnly.isSelected()) {
            reloadDisplay(NReportTools.getReportsWithFilesOnly(resident));
        } else {
            reloadDisplay(NReportTools.getReports(resident, jdcVon.getDate(), WEEKS_BACK));
        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jspReports = new JScrollPane();
        cpReports = new CollapsiblePanes();

        //======== this ========
        setLayout(new FormLayout(
                "default:grow",
                "fill:default:grow"));

        //======== jspReports ========
        {

            //======== cpReports ========
            {
                cpReports.setLayout(new BoxLayout(cpReports, BoxLayout.X_AXIS));
            }
            jspReports.setViewportView(cpReports);
        }
        add(jspReports, CC.xy(1, 1));
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void cleanup() {

        jdcVon.cleanup();

    }

    @Override
    public void switchResident(Resident bewohner) {
        this.resident = bewohner;
        OPDE.getDisplayManager().setMainMessage(ResidentTools.getBWLabelText(bewohner));
        txtSearch.setText(null);
        firstReport = NReportTools.getFirstReport(resident);
        jdcVon.setMaxSelectableDate(new Date());
        jdcVon.setMinSelectableDate(firstReport.getPit());
        reload();
    }

//    private void printBericht(int[] sel) {
//        TMPflegeberichte tm = (TMPflegeberichte) tblTB.getModel();
//        SYSFilesTools.print(SYSTools.toHTML(NReportTools.getBerichteAsHTML(SYSTools.getSelectionAsList(tm.getNReport(), sel), false, true)), true);
//    }

//    private void tblTBMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTBMousePressed
//        Point p = evt.getPoint();
//
//        Point p2 = evt.getPoint();
//        SwingUtilities.convertPointToScreen(p2, tblTB);
//        final Point screenposition = p2;
//
//        final int row = tblTB.rowAtPoint(p);
//        final int col = tblTB.columnAtPoint(p);
//
//        ListSelectionModel lsm = tblTB.getSelectionModel();
//        boolean singleRowSelected = lsm.getMaxSelectionIndex() == lsm.getMinSelectionIndex();
//
//        if (singleRowSelected) {
//            lsm.setSelectionInterval(row, row);
//        }
//
//        bericht = (NReport) tblTB.getModel().getValueAt(lsm.getLeadSelectionIndex(), TMPflegeberichte.COL_BERICHT);
//        final boolean alreadyEdited = bericht.isDeleted() || bericht.isReplaced();
//        final boolean sameUser = bericht.getUser().equals(OPDE.getLogin().getUser());
//
////        btnEdit.setEnabled(singleRowSelected && !aktuellerBericht.isDeleted() && !aktuellerBericht.isReplaced() && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE));
////        btnDelete.setEnabled(singleRowSelected && !aktuellerBericht.isDeleted() && !aktuellerBericht.isReplaced() && OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.DELETE));
//
//        if (evt.isPopupTrigger()) {
//            SYSTools.unregisterListeners(menu);
//            menu = new JPopupMenu();
//
//            /**
//             * KORRIGIEREN
//             * Ein Bericht kann geändert werden (Korrektur)
//             * - Wenn sie nicht im Übergabeprotokoll abgehakt wurde.
//             */
//            final boolean bearbeitenMoeglich = !alreadyEdited && singleRowSelected && bericht.getUsersAcknowledged().isEmpty();
//
//            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE)) {
//
//                /***
//                 *      _____    _ _ _     ____            _      _     _
//                 *     | ____|__| (_) |_  | __ )  ___ _ __(_) ___| |__ | |_
//                 *     |  _| / _` | | __| |  _ \ / _ \ '__| |/ __| '_ \| __|
//                 *     | |__| (_| | | |_  | |_) |  __/ |  | | (__| | | | |_
//                 *     |_____\__,_|_|\__| |____/ \___|_|  |_|\___|_| |_|\__|
//                 *
//                 */
//                final JMenuItem itemPopupEdit = new JMenuItem(OPDE.lang.getString("misc.commands.edit"), new ImageIcon(getClass().getResource("/artwork/22x22/bw/edit.png")));
//                itemPopupEdit.addActionListener(new java.awt.event.ActionListener() {
//                    public void actionPerformed(java.awt.event.ActionEvent evt) {
//
//                        final JidePopup popup = new JidePopup();
//                        popup.setMovable(false);
//                        popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
//
//                        final JComponent editor;
//                        final Collection<NReportTAGS> mytags = new ArrayList(bericht.getTags());
//
//                        switch (col) {
//                            case TMPflegeberichte.COL_PIT: {
//                                editor = new PnlUhrzeitDatum(bericht.getPit());
//                                break;
//                            }
//                            case TMPflegeberichte.COL_Flags: {
//                                if (sameUser || OPDE.isAdmin()) {
//                                    ItemListener il = new ItemListener() {
//                                        @Override
//                                        public void itemStateChanged(ItemEvent itemEvent) {
//                                            JCheckBox cb = (JCheckBox) itemEvent.getSource();
//                                            NReportTAGS tag = (NReportTAGS) cb.getClientProperty("UserObject");
//                                            if (itemEvent.getStateChange() == ItemEvent.DESELECTED) {
//                                                mytags.remove(tag);
//                                            } else {
//                                                mytags.add(tag);
//                                            }
//                                        }
//                                    };
//
//                                    editor = NReportTAGSTools.createCheckBoxPanelForTags(il, bericht.getTags(), new GridLayout(8, 4));
//                                } else {
//                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.cantedit")));
//                                    editor = null;
//                                }
//                                break;
//                            }
//                            case TMPflegeberichte.COL_HTML: {
//                                editor = new JTextArea(bericht.getText(), 10, 40);
//                                ((JTextArea) editor).setLineWrap(true);
//                                ((JTextArea) editor).setWrapStyleWord(true);
//                                ((JTextArea) editor).setEditable(true);
//                                break;
//                            }
//                            default: {
//                                editor = null;
//                            }
//                        }
//
//                        if (editor != null) {
////                            popup.getContentPane().add(new JScrollPane(editor));
//                            final JButton saveButton = new JButton(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
//                            saveButton.addActionListener(new ActionListener() {
//                                @Override
//                                public void actionPerformed(ActionEvent actionEvent) {
//                                    popup.hidePopup();
//                                    EntityManager em = OPDE.createEM();
//                                    try {
//                                        em.getTransaction().begin();
//
//                                        em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
//                                        bericht = em.merge(bericht);
//                                        NReport newBericht = em.merge(NReportTools.copyBericht(bericht));
//
//                                        switch (col) {
//                                            case TMPflegeberichte.COL_PIT: {
//                                                newBericht.setPit(((PnlUhrzeitDatum) editor).getPIT());
//                                                NReportTools.changeBericht(em, bericht, newBericht);
////                                                reloadTable();
////                                                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.edited")));
//                                                break;
//                                            }
//                                            case TMPflegeberichte.COL_Flags: {
//                                                bericht.getTags().clear();
//                                                bericht.getTags().addAll(mytags);
////                                                ((TMPflegeberichte) tblTB.getModel()).setPflegebericht(row, bericht);
////                                                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.edited")));
//                                                break;
//                                            }
//                                            case TMPflegeberichte.COL_HTML: {
//                                                if (!((JTextArea) editor).getText().trim().isEmpty()) {
//                                                    newBericht.setText(((JTextArea) editor).getText().trim());
//                                                    NReportTools.changeBericht(em, bericht, newBericht);
////                                                    reloadTable();
////                                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.edited")));
//                                                } else {
//                                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.emptyentry")));
//                                                }
//                                                break;
//                                            }
//                                            default: {
//
//                                            }
//                                        }
//                                        em.getTransaction().commit();
//                                        reloadTable();
//                                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.edited")));
//                                    } catch (OptimisticLockException ole) {
//
//                                        if (em.getTransaction().isActive()) {
//                                            em.getTransaction().rollback();
//                                        }
//
//                                        //Class> entity.info.Bewohner
//                                        // if (((org.eclipse.persistence.exceptions.OptimisticLockException) ole.getCause()).getObject() instanceof Bewohner) {
//                                        if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                            OPDE.getMainframe().emptyFrame();
//                                            OPDE.getMainframe().afterLogin();
//                                        }
//                                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                    } catch (Exception e) {
//                                        if (em.getTransaction().isActive()) {
//                                            em.getTransaction().rollback();
//                                        }
//                                        OPDE.fatal(e);
//                                    } finally {
//                                        em.close();
//                                    }
//                                }
//                            });
//
//                            JPanel pnl = new JPanel(new BorderLayout(10, 10));
//                            JScrollPane pnlEditor = new JScrollPane(editor);
//
//                            pnl.add(pnlEditor, BorderLayout.CENTER);
//                            JPanel buttonPanel = new JPanel();
//                            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
//                            buttonPanel.add(saveButton);
//                            pnl.setBorder(new EmptyBorder(10, 10, 10, 10));
//                            pnl.add(buttonPanel, BorderLayout.SOUTH);
//
//                            popup.setOwner(tblTB);
//                            popup.removeExcludedComponent(tblTB);
//                            popup.getContentPane().add(pnl);
//                            popup.setDefaultFocusComponent(editor);
//                            popup.showPopup(screenposition.x, screenposition.y);
//                        } else {
//                            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.cantedit")));
//                        }
//                    }
//                });
//                menu.add(itemPopupEdit);
//                itemPopupEdit.setEnabled(bearbeitenMoeglich);
//
//                JMenu timemenu = new JMenu(OPDE.lang.getString("misc.commands.changeeffort"));
//                int[] mins = new int[]{15, 30, 45, 60, 120, 240, 360};
//
//                HashMap text = new HashMap();
//                text.put(60, "1 " + OPDE.lang.getString("misc.msg.Hour"));
//                text.put(120, "2 " + OPDE.lang.getString("misc.msg.Hours"));
//                text.put(240, "3 " + OPDE.lang.getString("misc.msg.Hours"));
//                text.put(360, "4 " + OPDE.lang.getString("misc.msg.Hours"));
//
//                for (int min : mins) {
//                    String einheit = "";
//                    if (text.containsKey(min)) {
//                        einheit = text.get(min).toString();
//                    } else {
//                        einheit = min + " " + OPDE.lang.getString("misc.msg.Minutes");
//                    }
//                    JMenuItem item = new JMenuItem(einheit);
//                    final int minutes = min;
//                    item.addActionListener(new java.awt.event.ActionListener() {
//                        public void actionPerformed(java.awt.event.ActionEvent evt) {
//                            bericht.setDauer(minutes);
//                            bericht = EntityTools.merge(bericht);
//                            ((TMPflegeberichte) tblTB.getModel()).setPflegebericht(row, bericht);
//                            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.thisentryhasbeenedited")));
//                        }
//                    });
//                    timemenu.add(item);
//                }
//                text.clear();
//                menu.add(timemenu);
//                timemenu.setEnabled(OPDE.isAdmin() || (!alreadyEdited && singleRowSelected && sameUser)); // Sonderfall, egal ob schon bestätigt.
//            }
//
//            /***
//             *      ____       _      _
//             *     |  _ \  ___| | ___| |_ ___
//             *     | | | |/ _ \ |/ _ \ __/ _ \
//             *     | |_| |  __/ |  __/ ||  __/
//             *     |____/ \___|_|\___|\__\___|
//             *
//             */
//            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.DELETE)) {
//                JMenuItem itemPopupDelete = new JMenuItem(OPDE.lang.getString("misc.commands.delete"), new ImageIcon(getClass().getResource("/artwork/22x22/bw/trashcan_empty.png")));
//                itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {
//                    @Override
//                    public void actionPerformed(java.awt.event.ActionEvent evt) {
//                        new DlgYesNo(OPDE.lang.getString("misc.questions.delete"), new ImageIcon(getClass().getResource("/artwork/48x48/bw/trashcan_empty.png")), new Closure() {
//                            @Override
//                            public void execute(Object answer) {
//                                if (answer.equals(JOptionPane.YES_OPTION)) {
//
//                                    EntityManager em = OPDE.createEM();
//                                    try {
//                                        em.getTransaction().begin();
//                                        em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
//                                        NReport mybericht = NReportTools.deleteBericht(em, bericht);
//                                        em.getTransaction().commit();
//
//                                        ((TMPflegeberichte) tblTB.getModel()).setPflegebericht(row, mybericht);
//                                        if (!tbShowReplaced.isSelected()) {
//                                            reloadTable();
//                                        }
//                                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.thisentryhasbeendeleted")));
//                                    } catch (OptimisticLockException ole) {
//                                        if (em.getTransaction().isActive()) {
//                                            em.getTransaction().rollback();
//                                        }
//                                        if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                            OPDE.getMainframe().emptyFrame();
//                                            OPDE.getMainframe().afterLogin();
//                                        }
//                                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                    } catch (Exception e) {
//                                        if (em.getTransaction().isActive()) {
//                                            em.getTransaction().rollback();
//                                        }
//                                        OPDE.fatal(e);
//                                    } finally {
//                                        em.close();
//                                    }
//
//                                }
//                            }
//                        });
//                    }
//                });
//                menu.add(itemPopupDelete);
//                itemPopupDelete.setEnabled(bearbeitenMoeglich);
//            }
//
//            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(PnlFiles.internalClassID, InternalClassACL.PRINT)) {
//                JMenuItem itemPopupPrint = new JMenuItem(OPDE.lang.getString("misc.commands.printselected"), new ImageIcon(getClass().getResource("/artwork/22x22/bw/printer.png")));
//                itemPopupPrint.addActionListener(new java.awt.event.ActionListener() {
//                    public void actionPerformed(java.awt.event.ActionEvent evt) {
////                        int[] sel = tblTB.getSelectedRows();
//                        printBericht(tblTB.getSelectedRows());
//                    }
//                });
//                menu.add(itemPopupPrint);
//            }
//
//            menu.add(new JSeparator());
//
//            final JMenuItem itemPopupFiles = SYSFilesTools.getFileMenu(bericht, new Closure() {
//                @Override
//                public void execute(Object o) {
//                    reloadTable();
//                }
//            });
//            menu.add(itemPopupFiles);
//
//
//            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.SELECT) && !alreadyEdited && singleRowSelected) {
////                menu.add(new JSeparator());
//                menu.add(VorgaengeTools.getVorgangContextMenu(new JFrame(), bericht, resident, standardActionListener));
//            }
//
//            menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
//        }
//    }//GEN-LAST:event_tblTBMousePressed


    private CollapsiblePane addFilters() {
        JPanel labelPanel = new JPanel();
        labelPanel.setBackground(Color.WHITE);
        labelPanel.setLayout(new VerticalLayout(5));

        txtSearch = new JXSearchField(OPDE.lang.getString("misc.msg.searchphrase"));
        txtSearch.setFont(SYSConst.ARIAL14);
        txtSearch.setInstantSearchDelay(750);
        txtSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (SYSTools.catchNull(txtSearch.getText()).trim().length() > 3) {
                    reloadDisplay(NReportTools.getReports(resident, txtSearch.getText()));
                }
            }
        });


        labelPanel.add(txtSearch);

        jdcVon = new JDateChooser(new Date());
        jdcVon.setBackground(Color.WHITE);
        jdcVon.setFont(SYSConst.ARIAL14);
        jdcVon.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (initPhase) {
                    return;
                }
                if (!evt.getPropertyName().equals("date")) {
                    return;
                }
                reloadDisplay(NReportTools.getReports(resident, jdcVon.getDate(), WEEKS_BACK));
            }
        });
        labelPanel.add(jdcVon);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setLayout(new HorizontalLayout(5));
        buttonPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        EntityManager em = OPDE.createEM();
        MouseAdapter ma = GUITools.getHyperlinkStyleMouseAdapter();
        Query query = em.createNamedQuery("PBerichtTAGS.findAllActive");
        DefaultComboBoxModel dcbm = new DefaultComboBoxModel(query.getResultList().toArray());
        em.close();

        dcbm.insertElementAt(OPDE.lang.getString("misc.commands.noselection"), 0);
        cmbAuswahl = new JComboBox(dcbm);
        cmbAuswahl.setFont(SYSConst.ARIAL14);
        cmbAuswahl.setRenderer(NReportTAGSTools.getPBerichtTAGSRenderer());
        cmbAuswahl.setSelectedIndex(0);
        cmbAuswahl.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (initPhase || itemEvent.getStateChange() != ItemEvent.SELECTED) return;
//                SYSPropsTools.storeState(internalClassID + ":cmbAuswahl", cmbAuswahl);
                buildPanel();
            }
        });
        labelPanel.add(cmbAuswahl);
//        SYSPropsTools.restoreState(internalClassID + ":cmbAuswahl", cmbAuswahl);

        tbFilesOnly = GUITools.getNiceToggleButton(OPDE.lang.getString("misc.filters.filesonly"));
        tbFilesOnly.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (initPhase) return;
//                SYSPropsTools.storeState(internalClassID + ":tbFilesOnly", tbFilesOnly);
                reload();
            }
        });
        labelPanel.add(tbFilesOnly);
//        SYSPropsTools.restoreState(internalClassID + ":tbFilesOnly", tbFilesOnly);
        tbFilesOnly.setHorizontalAlignment(SwingConstants.LEFT);

        tbShowReplaced = GUITools.getNiceToggleButton(OPDE.lang.getString("misc.filters.showreplaced"));
        tbShowReplaced.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (initPhase) return;
//                SYSPropsTools.storeState(internalClassID + ":tbShowReplaced", tbShowReplaced);
                buildPanel();
            }
        });
        labelPanel.add(tbShowReplaced);
//        SYSPropsTools.restoreState(internalClassID + ":tbShowReplaced", tbShowReplaced);
        tbShowReplaced.setHorizontalAlignment(SwingConstants.LEFT);


        JideButton resetButton = GUITools.createHyperlinkButton(OPDE.lang.getString("misc.commands.resetFilter"), SYSConst.icon22undo, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                initPhase = true;
                jdcVon.setDate(new Date());
                cmbAuswahl.setSelectedIndex(0);
                tbFilesOnly.setSelected(false);
                tbShowReplaced.setSelected(false);
                txtSearch.setText(null);
                initPhase = false;
                reload();
            }
        });
        labelPanel.add(resetButton);


        CollapsiblePane panelFilter = new CollapsiblePane(OPDE.lang.getString("misc.msg.Filter"));
        panelFilter.setStyle(CollapsiblePane.PLAIN_STYLE);
        panelFilter.setCollapsible(false);
        panelFilter.setContentPane(labelPanel);

        return panelFilter;
    }

    private CollapsiblePane addCommands() {

        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout());
        mypanel.setBackground(Color.WHITE);

        CollapsiblePane searchPane = new CollapsiblePane(OPDE.lang.getString(internalClassID));
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);

        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        /***
         *      _   _
         *     | \ | | _____      __
         *     |  \| |/ _ \ \ /\ / /
         *     | |\  |  __/\ V  V /
         *     |_| \_|\___| \_/\_/
         *
         */
        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.INSERT)) {
            JideButton addButton = GUITools.createHyperlinkButton(OPDE.lang.getString("misc.commands.new"), new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgReport(resident, new Closure() {
                        @Override
                        public void execute(Object report) {
                            if (report != null) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                    NReport myReport = (NReport) em.merge(report);
                                    em.getTransaction().commit();
                                    DateMidnight dm = new DateMidnight(myReport.getPit());
                                    if (!dayMap.containsKey(dm)){
                                        dayMap.put(dm, new ArrayList<NReport>());
                                    }
                                    dayMap.get(dm).add(myReport);
                                    Collections.sort(dayMap.get(dm));
                                    reportMap.put(myReport, createCP4(myReport));
                                    buildPanel();
                                } catch (OptimisticLockException ole) {
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                    if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
                                        OPDE.getMainframe().emptyFrame();
                                        OPDE.getMainframe().afterLogin();
                                    }
                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                                } catch (Exception e) {
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                    OPDE.fatal(e);
                                } finally {
                                    em.close();
                                }
                            }
                        }
                    });
                }
            });
            mypanel.add(addButton);
        }

//        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.PRINT)) {
//            JideButton printButton = GUITools.createHyperlinkButton(OPDE.lang.getString("misc.commands.print"), new ImageIcon(getClass().getResource("/artwork/22x22/bw/printer.png")), new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//                    TMPflegeberichte tm = (TMPflegeberichte) tblTB.getModel();
//                    SYSFilesTools.print(SYSTools.toHTML(NReportTools.getBerichteAsHTML(tm.getNReport(), false, true)), true);
//                }
//            });
//            mypanel.add(printButton);
//        }
//

        searchPane.setContentPane(mypanel);
        searchPanes.add(searchPane);


        searchPane.setContentPane(mypanel);
        return searchPane;
    }

//    private void reloadTable() {
//        if (initPhase) {
//            return;
//        }
//
//        String tags = "";
//        if (cmbAuswahl.getSelectedIndex() > 0) {
//            tags = ((NReportTAGS) cmbAuswahl.getSelectedItem()).getPbtagid().toString();
//        }
////        if (!panelTags.isCollapsed()) {
////            Iterator<NReportTAGS> it = tagFilter.iterator();
////            while (it.hasNext()) {
////                tags += Long.toString(it.next().getPbtagid());
////                tags += (it.hasNext() ? "," : "");
////            }
////        }
//
//        String search = txtSearch.getText().trim();
//
//        EntityManager em = OPDE.createEM();
//        Query query = em.createQuery(" "
//                + " SELECT p FROM NReport p "
//                + (tbFilesOnly.isSelected() ? " JOIN p.attachedFiles af " : "")
//                + (tags.isEmpty() ? "" : " JOIN p.tags t ")
//                + " WHERE p.resident = :resident "
//                + (search.isEmpty() ? " AND p.pit >= :von " : " AND p.text like :search ")
//                + (tags.isEmpty() ? "" : " AND t.pbtagid IN (" + tags + " ) ")
//                + (tbShowReplaced.isSelected() ? "" : " AND p.editedBy is null ")
//                + " ORDER BY p.pit DESC ");
//        query.setParameter("resident", resident);
//
//
//        if (!search.isEmpty()) {
//            query.setParameter("search", "%" + search + "%");
//        } else {
//            query.setParameter("von", new Date(SYSCalendar.startOfDay(jdcVon.getDate())));
//        }
//
//        ArrayList<NReport> listBerichte = new ArrayList<NReport>(query.getResultList());
//        em.close();
//
//        tblTB.setModel(new TMPflegeberichte(listBerichte, tbShowIDs.isSelected()));
//        tblTB.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//
//        jspTblTB.dispatchEvent(new ComponentEvent(jspTblTB, ComponentEvent.COMPONENT_RESIZED));
//
//
//        tblTB.getColumnModel().getColumn(0).setCellRenderer(new RNDHTML());
//        tblTB.getColumnModel().getColumn(1).setCellRenderer(new RNDHTML());
//        tblTB.getColumnModel().getColumn(2).setCellRenderer(new RNDHTML());
//
//
//        dispatchEvent(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));
//
//    }

    private void reloadDisplay(ArrayList<NReport> reportList) {
        /***
         *               _                 _ ____  _           _
         *      _ __ ___| | ___   __ _  __| |  _ \(_)___ _ __ | | __ _ _   _
         *     | '__/ _ \ |/ _ \ / _` |/ _` | | | | / __| '_ \| |/ _` | | | |
         *     | | |  __/ | (_) | (_| | (_| | |_| | \__ \ |_) | | (_| | |_| |
         *     |_|  \___|_|\___/ \__,_|\__,_|____/|_|___/ .__/|_|\__,_|\__, |
         *                                              |_|            |___/
         */
        initPhase = true;
        dayMap.clear();
        reportMap.clear();


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
//
//                    int progress = 0;
//                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, 100));
//
//                    for (DFN dfn : DFNTools.getDFNs(resident, jdcDatum.getDate())) {
//                        shiftMAPDFN.get(dfn.getShift()).add(dfn);
//                    }
//
//                    for (Byte shift : new Byte[]{DFNTools.SHIFT_ON_DEMAND, DFNTools.SHIFT_VERY_EARLY, DFNTools.SHIFT_EARLY, DFNTools.SHIFT_LATE, DFNTools.SHIFT_VERY_LATE}) {
//                        shiftMAPpane.put(shift, createCP4(shift));
//                        try {
//                            shiftMAPpane.get(shift).setCollapsed(shift == DFNTools.SHIFT_ON_DEMAND || shift != SYSCalendar.whatShiftIs(new Date()));
//                        } catch (PropertyVetoException e) {
//                            OPDE.debug(e);
//                        }
//                        progress += 20;
//                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, 100));
//                    }
//
//                    return null;
//                }
//
//                @Override
//                protected void done() {
//                    buildPanel(true);
//                    initPhase = false;
//                    OPDE.getDisplayManager().setProgressBarMessage(null);
//                    OPDE.getMainframe().setBlocked(false);
//                }
//            };
//            worker.execute();

        } else {

            // insert the reports into the appropriate sublists and create the CPs
            for (NReport report : reportList) {
                DateMidnight dateMidnight = new DateMidnight(report.getPit());
                if (!dayMap.containsKey(dateMidnight)) {
                    dayMap.put(dateMidnight, new ArrayList<NReport>());
                }
                dayMap.get(dateMidnight).add(report);
                reportMap.put(report, createCP4(report));
            }

            buildPanel();
        }
        initPhase = false;
    }


    private CollapsiblePane createCP4(final NReport report) {
        String title = "[" + DateFormat.getTimeInstance(DateFormat.SHORT).format(report.getPit()) + "] " + SYSTools.left(report.getText(), MAX_TEXT_LENGTH) + SYSTools.catchNull(NReportTools.getTagsAsHTML(report), " [", "]");
        title = (report.isObsolete() ? "<s>" : "") + title + (report.isObsolete() ? "</s>" : "");
        final CollapsiblePane cp = new CollapsiblePane();

        /***
         *      _   _ _____    _    ____  _____ ____
         *     | | | | ____|  / \  |  _ \| ____|  _ \
         *     | |_| |  _|   / _ \ | | | |  _| | |_) |
         *     |  _  | |___ / ___ \| |_| | |___|  _ <
         *     |_| |_|_____/_/   \_\____/|_____|_| \_\
         *
         */

        JPanel titlePanelleft = new JPanel();
        titlePanelleft.setLayout(new BoxLayout(titlePanelleft, BoxLayout.LINE_AXIS));


        /***
         *      _     _       _    _           _   _                _   _                _
         *     | |   (_)_ __ | | _| |__  _   _| |_| |_ ___  _ __   | | | | ___  __ _  __| | ___ _ __
         *     | |   | | '_ \| |/ / '_ \| | | | __| __/ _ \| '_ \  | |_| |/ _ \/ _` |/ _` |/ _ \ '__|
         *     | |___| | | | |   <| |_) | |_| | |_| || (_) | | | | |  _  |  __/ (_| | (_| |  __/ |
         *     |_____|_|_| |_|_|\_\_.__/ \__,_|\__|\__\___/|_| |_| |_| |_|\___|\__,_|\__,_|\___|_|
         *
         */
        JideButton btnReport = GUITools.createHyperlinkButton(SYSTools.toHTMLForScreen(title), null, null);
        btnReport.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnReport.setBackground(SYSCalendar.getBG(SYSCalendar.whatShiftIs(report.getPit())));
        btnReport.setForeground(report.isObsolete() ? Color.gray : Color.black);

        titlePanelleft.add(btnReport);

        JPanel titlePanelright = new JPanel();
        titlePanelright.setLayout(new BoxLayout(titlePanelright, BoxLayout.LINE_AXIS));

        titlePanelleft.setOpaque(false);
        titlePanelright.setOpaque(false);
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);

        titlePanel.setLayout(new GridBagLayout());
        ((GridBagLayout) titlePanel.getLayout()).columnWidths = new int[]{0, 80};
        ((GridBagLayout) titlePanel.getLayout()).columnWeights = new double[]{1.0, 1.0};

        titlePanel.add(titlePanelleft, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 0, 5), 0, 0));

        titlePanel.add(titlePanelright, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 0, 0), 0, 0));

        cp.setTitleLabelComponent(titlePanel);
        cp.setSlidingDirection(SwingConstants.SOUTH);

        try {
            cp.setCollapsed(true);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }


        /***
         *       ___ ___  _  _ _____ ___ _  _ _____
         *      / __/ _ \| \| |_   _| __| \| |_   _|
         *     | (_| (_) | .` | | | | _|| .` | | |
         *      \___\___/|_|\_| |_| |___|_|\_| |_|
         *
         */

        cp.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                JTextPane contentPane = new JTextPane();
                contentPane.setContentType("text/html");
                contentPane.setEditable(false);
                contentPane.setText(SYSTools.toHTMLForScreen(NReportTools.getAsHTML(report)));
                cp.setContentPane(contentPane);
            }
        });
        cp.setHorizontalAlignment(SwingConstants.LEADING);
        cp.setOpaque(false);

        return cp;
    }

    private void buildPanel() {
        OPDE.debug(cpReports.getComponentCount());
        cpReports.removeAll();

        JButton older = new JButton(OPDE.lang.getString("misc.msg.olderEntries"), SYSConst.icon22down);
        older.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (reportMap.containsKey(firstReport)) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.noOlderEntries")));
                    return;
                }
                DateMidnight dm = new DateMidnight(jdcVon.getDate());
                jdcVon.setDate(dm.minusWeeks(WEEKS_BACK).minusDays(1).toDate());
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        jspReports.getVerticalScrollBar().setValue(0);
                    }
                });
            }
        });
        JButton newer = new JButton(OPDE.lang.getString("misc.msg.newerEntries"), SYSConst.icon22up);
        newer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (new DateMidnight(jdcVon.getDate()).plusWeeks(WEEKS_BACK).isAfterNow()) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.noNewerEntries")));
                    return;
                }
                DateMidnight dm = new DateMidnight(jdcVon.getDate());
                jdcVon.setDate(dm.plusWeeks(WEEKS_BACK).plusDays(1).toDate());
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        jspReports.getVerticalScrollBar().setValue(jspReports.getVerticalScrollBar().getMaximum());
                    }
                });
            }
        });

        cpReports.setLayout(new JideBoxLayout(cpReports, JideBoxLayout.Y_AXIS));

        cpReports.add(newer);
        NReportTAGS tag = cmbAuswahl.getSelectedIndex() == 0 ? null : (NReportTAGS) cmbAuswahl.getSelectedItem();

        boolean empty = true;

        if (!dayMap.isEmpty()) {

            ArrayList<DateMidnight> dateList = new ArrayList(dayMap.keySet());
            Collections.sort(dateList, new Comparator<DateMidnight>() {
                @Override
                public int compare(DateMidnight o1, DateMidnight o2) {
                    return o1.compareTo(o2) * -1;
                }
            });

            int year = dateList.get(0).getYear();
            int currentYear = year;
            HashMap hollidays = SYSCalendar.getFeiertage(year);

            for (DateMidnight date : dateList) {

                if (date.getYear() != currentYear) {
                    currentYear = date.getYear();
                    hollidays = SYSCalendar.getFeiertage(currentYear);
                }


                JPanel dayPanel = new JPanel();
                dayPanel.setLayout(new VerticalLayout());

                for (NReport report : dayMap.get(date)) {

                    NReport report2add = report;

                    if (tag != null && !report.getTags().contains(tag)) {
                        report2add = null;
                    }

                    if (report.isObsolete() && !tbShowReplaced.isSelected()) {
                        report2add = null;
                    }

                    if (report2add != null) {
                        dayPanel.add(reportMap.get(report2add));
                    }

                }

                if (dayPanel.getComponentCount() > 0) {
                    // create header panel for that day
                    SimpleDateFormat df = new SimpleDateFormat("EEEE, dd.MM.yyyy");

                    String holliday = SYSTools.catchNull(hollidays.get(DateTimeFormat.forPattern("yyyy-MM-dd").print(date)));
                    String title = df.format(date.toDate()) + (holliday.isEmpty() ? "" : " " + holliday);

                    final CollapsiblePane dayPane = new CollapsiblePane(title);
                    dayPane.setSlidingDirection(SwingConstants.SOUTH);
                    dayPane.setFont(SYSConst.ARIAL20);

                    if (!holliday.isEmpty()) {
                        dayPane.setBackground(SYSConst.colorHolliday);
                    } else if (date.getDayOfWeek() == DateTimeConstants.SATURDAY || date.getDayOfWeek() == DateTimeConstants.SUNDAY) {
                        dayPane.setBackground(SYSConst.colorWeekend);
                    } else {
                        dayPane.setBackground(SYSConst.colorWeekday);
                    }
                    dayPane.setContentPane(dayPanel);
                    dayPane.setCollapsible(false);
                    dayPane.setOpaque(false);

                    cpReports.add(dayPane);
                    empty = false;
                }
            }
        }

        if (empty) {
            CollapsiblePane emptyCP = new CollapsiblePane(OPDE.lang.getString(internalClassID + ".noreports"));
            emptyCP.setCollapsible(false);
            try {
                emptyCP.setCollapsed(false);
            } catch (PropertyVetoException e) {
                OPDE.error(e);
            }
            cpReports.add(emptyCP);
        }

        cpReports.add(older);
        cpReports.addExpansion();

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jspReports;
    private CollapsiblePanes cpReports;
    // End of variables declaration//GEN-END:variables
}
