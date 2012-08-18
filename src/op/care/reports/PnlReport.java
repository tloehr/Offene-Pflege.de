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
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import com.toedter.calendar.JDateChooser;
import entity.NReport;
import entity.NReportTAGS;
import entity.NReportTAGSTools;
import entity.NReportTools;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.system.SYSPropsTools;
import op.OPDE;
import op.threads.DisplayManager;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.HorizontalLayout;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.DateMidnight;
import org.joda.time.DateTimeConstants;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * @author root
 */
public class PnlReport extends NursingRecordsPanel {

    public static final String internalClassID = "nursingrecords.reports";

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
    private HashMap<DateMidnight, CollapsiblePane> dayCPMap;
    private HashMap<NReport, CollapsiblePane> reportMap;

    private Resident resident;
    private JPopupMenu menu;
    private boolean initPhase;

    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;

    /**
     * Dieser Actionlistener wird gebraucht, damit die einzelnen Menüpunkte des Kontextmenüs, nachdem sie
     * aufgerufen wurden, einen reloadTable() auslösen können.
     */
    private ActionListener standardActionListener;


    /**
     * Diese Liste enhtält die Menge der Tags, die im Suchfenster gesetzt wurden.
     */
    private ArrayList<NReportTAGS> tagFilter;

    /**
     * Creates new form PnlReport
     */
    public PnlReport(Resident resident, JScrollPane jspSearch) {
        this.initPhase = true;
        initComponents();
        this.jspSearch = jspSearch;
//        standardActionListener = new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                reloadTable();
//            }
//        };

        tagFilter = new ArrayList<NReportTAGS>();
        prepareSearchArea();

        initPanel();
        this.initPhase = false;

        switchResident(resident);


    }

    private void initPanel() {
        dayMap = new HashMap<DateMidnight, ArrayList<NReport>>();
        dayCPMap = new HashMap<DateMidnight, CollapsiblePane>();
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
        reloadDisplay();

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
        reloadDisplay();
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
        txtSearch.setInstantSearchDelay(750);
        txtSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reloadDisplay();
            }
        });


        labelPanel.add(txtSearch);

        jdcVon = new JDateChooser(new Date());
        jdcVon.setBackground(Color.WHITE);
        jdcVon.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (initPhase) {
                    return;
                }
                if (!evt.getPropertyName().equals("date")) {
                    return;
                }
                reloadDisplay();
            }
        });
        labelPanel.add(jdcVon);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setLayout(new HorizontalLayout(5));
        buttonPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        JButton homeButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_start.png")));
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                NReport first = NReportTools.getFirstBericht(resident);
                jdcVon.setDate(first == null ? SYSCalendar.addField(SYSCalendar.today_date(), -2, GregorianCalendar.WEEK_OF_MONTH) : first.getPit());
            }
        });
        homeButton.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_start_pressed.png")));
        homeButton.setBorder(null);
        homeButton.setBorderPainted(false);
        homeButton.setOpaque(false);
        homeButton.setContentAreaFilled(false);
        homeButton.setToolTipText(OPDE.lang.getString("misc.nav.home"));
        homeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton twoweeksButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/2weeksback.png")));
        twoweeksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                jdcVon.setDate(SYSCalendar.addField(new Date(), -2, GregorianCalendar.WEEK_OF_MONTH));
            }
        });
        twoweeksButton.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/2weeksback-pressed.png")));
        twoweeksButton.setBorder(null);
        twoweeksButton.setBorderPainted(false);
        twoweeksButton.setOpaque(false);
        twoweeksButton.setContentAreaFilled(false);
        twoweeksButton.setToolTipText(OPDE.lang.getString("misc.nav.2weeksback"));
        twoweeksButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));


        JButton fourweeksButton = new JButton(new ImageIcon(getClass().getResource("/artwork/32x32/4weeksback.png")));
        fourweeksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                jdcVon.setDate(SYSCalendar.addDate(jdcVon.getDate(), 1));
            }
        });
        fourweeksButton.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/4weeksback-pressed.png")));
        fourweeksButton.setBorder(null);
        fourweeksButton.setBorderPainted(false);
        fourweeksButton.setOpaque(false);
        fourweeksButton.setContentAreaFilled(false);
        fourweeksButton.setToolTipText(OPDE.lang.getString("misc.nav.4weeksback"));
        fourweeksButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));


        buttonPanel.add(homeButton);
        buttonPanel.add(twoweeksButton);
        buttonPanel.add(fourweeksButton);


        labelPanel.add(buttonPanel);

        EntityManager em = OPDE.createEM();
        MouseAdapter ma = GUITools.getHyperlinkStyleMouseAdapter();
        Query query = em.createNamedQuery("PBerichtTAGS.findAllActive");
        DefaultComboBoxModel dcbm = new DefaultComboBoxModel(query.getResultList().toArray());
        em.close();

        dcbm.insertElementAt(OPDE.lang.getString("misc.commands.noselection"), 0);
        cmbAuswahl = new JComboBox(dcbm);
        cmbAuswahl.setRenderer(NReportTAGSTools.getPBerichtTAGSRenderer());
        cmbAuswahl.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (initPhase || itemEvent.getStateChange() != ItemEvent.SELECTED) return;
                SYSPropsTools.storeState(internalClassID + ":cmbAuswahl", cmbAuswahl);
                reloadDisplay();
            }
        });
        labelPanel.add(cmbAuswahl);
        SYSPropsTools.restoreState(internalClassID + ":cmbAuswahl", cmbAuswahl);

        tbFilesOnly = GUITools.getNiceToggleButton(OPDE.lang.getString("misc.filters.filesonly"));
        tbFilesOnly.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (initPhase) return;
                SYSPropsTools.storeState(internalClassID + ":tbFilesOnly", tbFilesOnly);
                reloadDisplay();
            }
        });
        labelPanel.add(tbFilesOnly);
        SYSPropsTools.restoreState(internalClassID + ":tbFilesOnly", tbFilesOnly);
        tbFilesOnly.setHorizontalAlignment(SwingConstants.LEFT);

        tbShowReplaced = GUITools.getNiceToggleButton(OPDE.lang.getString("misc.filters.showreplaced"));
        tbShowReplaced.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (initPhase) return;
                SYSPropsTools.storeState(internalClassID + ":tbShowReplaced", tbShowReplaced);
                reloadDisplay();
            }
        });
        labelPanel.add(tbShowReplaced);
        SYSPropsTools.restoreState(internalClassID + ":tbShowReplaced", tbShowReplaced);
        tbShowReplaced.setHorizontalAlignment(SwingConstants.LEFT);

        tbShowIDs = GUITools.getNiceToggleButton(OPDE.lang.getString("misc.filters.showpks"));
        tbShowIDs.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (initPhase) return;
                SYSPropsTools.storeState(internalClassID + ":tbShowIDs", tbShowIDs);
                reloadDisplay();
            }
        });
        tbShowIDs.setHorizontalAlignment(SwingConstants.LEFT);

        labelPanel.add(tbShowIDs);
        SYSPropsTools.restoreState(internalClassID + ":tbShowIDs", tbShowIDs);

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
                        public void execute(Object bericht) {
                            if (bericht != null) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                    em.merge(bericht);
                                    em.getTransaction().commit();
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
                                reloadDisplay();
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
        dayMap.clear();


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

            // insert the reports into the appropriate sublists
            for (NReport report : NReportTools.getReports(resident, jdcVon.getDate(), WEEKS_BACK)) {
                DateMidnight dateMidnight = new DateMidnight(report.getPit());
                if (!dayMap.containsKey(dateMidnight)) {
                    dayMap.put(dateMidnight, new ArrayList<NReport>());
                }
                dayMap.get(dateMidnight).add(report);
            }

            // now create a create a CP for every single day. Even the empty ones.
            DateMidnight start = new DateMidnight(jdcVon.getDate());
            DateMidnight end = new DateMidnight(start.minusWeeks(WEEKS_BACK));
            for (DateMidnight date = start; date.isAfter(end); date = date.minusDays(1)) {
                dayCPMap.put(date, createCP4(date));
//                shiftMAPpane.put(shift, createCP4(shift));
//                try {
//                    shiftMAPpane.get(shift).setCollapsed(shift == DFNTools.SHIFT_ON_DEMAND || shift != SYSCalendar.whatShiftIs(new Date()));
//                } catch (PropertyVetoException e) {
//                    OPDE.debug(e);
//                }
            }

            buildPanel(true);
        }
        initPhase = false;
    }


    private CollapsiblePane createCP4(DateMidnight dateMidnight) {
        /***
         *                          _        ____ ____  _  _
         *       ___ _ __ ___  __ _| |_ ___ / ___|  _ \| || |
         *      / __| '__/ _ \/ _` | __/ _ \ |   | |_) | || |_
         *     | (__| | |  __/ (_| | ||  __/ |___|  __/|__   _|
         *      \___|_|  \___|\__,_|\__\___|\____|_|      |_|
         *
         */
        SimpleDateFormat df = new SimpleDateFormat("EEE, dd.MM.yyyy");
        String title = df.format(dateMidnight.toDate());

        final CollapsiblePane dayPane = new CollapsiblePane(title);
        dayPane.setSlidingDirection(SwingConstants.SOUTH);

        if (dateMidnight.getDayOfWeek() == DateTimeConstants.SATURDAY || dateMidnight.getDayOfWeek() == DateTimeConstants.SUNDAY) {
            dayPane.setBackground(Color.pink);
        } else {
            dayPane.setBackground(Color.LIGHT_GRAY);
        }
//        npPane.setForeground(SYSCalendar.getFGSHIFT(DFNTools.SHIFT_ON_DEMAND));
        dayPane.setOpaque(false);

        JPanel dayPanel = new JPanel();
        dayPanel.setLayout(new VerticalLayout());

        if (dayMap.containsKey(dateMidnight)) {
            for (NReport report : dayMap.get(dateMidnight)) {
                OPDE.debug(report.getPbid());
                reportMap.put(report, createCP4(report));
                dayPanel.add(reportMap.get(report));
            }
        }

        dayPane.setContentPane(dayPanel);
        dayPane.setCollapsible(false);

        return dayPane;
    }

    private CollapsiblePane createCP4(NReport report) {
        CollapsiblePane cp = new CollapsiblePane(SYSTools.left(report.getText(), 80));
        cp.setBackground(SYSCalendar.getBGSHIFT(SYSCalendar.whatShiftIs(report.getPit())).brighter());
        try {
            cp.setCollapsed(true);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }
        return cp;
    }

    private void buildPanel(boolean resetCollapseState) {
        cpReports.removeAll();
        cpReports.setLayout(new JideBoxLayout(cpReports, JideBoxLayout.Y_AXIS));

        DateMidnight start = new DateMidnight(jdcVon.getDate());
        DateMidnight end = new DateMidnight(start.minusWeeks(WEEKS_BACK));
        for (DateMidnight date = start; date.isAfter(end); date = date.minusDays(1)) {

            cpReports.add(dayCPMap.get(date));
            if (resetCollapseState) {
//                try {
//                    shiftMAPpane.get(shift).setCollapsed(shift != SYSCalendar.whatShiftIs(new Date()));
//                } catch (PropertyVetoException e) {
//                    OPDE.debug(e);
//                }
            }
        }
        cpReports.addExpansion();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jspReports;
    private CollapsiblePanes cpReports;
    // End of variables declaration//GEN-END:variables
}
