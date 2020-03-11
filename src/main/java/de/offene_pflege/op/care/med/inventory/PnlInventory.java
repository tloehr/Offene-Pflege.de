/*
 * OffenePflege
 * Copyright (C) 2006-2012 Torsten Löhr
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
package de.offene_pflege.op.care.med.inventory;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import de.offene_pflege.entity.EntityTools;
import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.entity.info.ResidentTools;
import de.offene_pflege.entity.prescription.*;
import de.offene_pflege.entity.system.SYSPropsTools;
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.gui.interfaces.DefaultCPTitle;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.care.med.structure.DlgTradeForm;
import de.offene_pflege.op.care.med.structure.DlgUPREditor;
import de.offene_pflege.op.system.InternalClassACL;
import de.offene_pflege.op.system.LogicalPrinter;
import de.offene_pflege.op.system.PrinterForm;
import de.offene_pflege.op.system.Validator;
import de.offene_pflege.op.threads.DisplayManager;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.VerticalLayout;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.List;
import java.util.*;

/**
 * In OPDE gibt es eine Bestandsverwaltung für Medikamente. Bestände werden mit Hilfe von 3 Tabellen
 * in der Datenbank verwaltet.
 * <ul>
 * <li><B>MPVorrat</B> Ein Vorrat ist wie eine Schachtel oder Schublade zu sehen, in denen
 * einzelne Päckchen enthalten sind. Jetzt kann es natürlich passieren, dass verschiedene
 * Präparete, die aber pharmazeutisch gleichwertig sind in derselben Schachtel enthalten
 * sind. Wenn z.B. 3 verschiedene Medikamente mit demselben Wirkstoff, derselben Darreichungsform
 * und in derselben Stärke vorhanden sind, dann sollten sie auch in demselben Vorrat zusammengefasst
 * werden. Vorräte gehören immer einem bestimmten Bewohner.</li>
 * <li><B>MPBestand</B> Ein Bestand entspricht i.d.R. einer Verpackung. Also eine Schachtel eines
 * Medikamentes wäre für sich genommen ein Bestand. Aber auch z.B. wenn ein BW von zu Hause einen
 * angebrochenen Blister mitbringt, dann wird dies als eigener Bestand angesehen. Bestände gehören
 * immer zu einem bestimmten Vorrat. Das Eingangs-, Ausgangs und Anbruchdatum wird vermerkt. Es meistens
 * einen Verweis auf eine MPID aus der Tabelle MPackung. Bei eigenen Gebinden kann dieses Feld auch
 * <CODE>null</CODE> sein.</li>
 * <li><B>MPBuchung</B> Eine Buchung ist ein Ein- bzw. Ausgang von einer Menge von Einzeldosen zu oder von
 * einem bestimmten Bestand. Also wenn eine Packung eingebucht wird, dann wird ein Bestand erstellt und
 * eine Eingangsbuchung in Höhe der Ursprünglichen Packungsgrößen (z.B. 100 Stück). Bei Vergabe von
 * Medikamenten an einen Bewohner (über Abhaken in der BHP) werden die jeweiligen Mengen
 * ausgebucht. In diesem Fall steht in der Spalte BHPID der Verweis zur entsprechenden Zeile in der
 * Tabelle BHP.</li>
 * </ul>
 *
 * @author tloehr
 */
public class PnlInventory extends NursingRecordsPanel {


    private Resident resident;

    private List<MedInventory> lstInventories;
    private Map<String, CollapsiblePane> cpMap;
    private Map<String, JToggleButton> mapKey2ClosedToggleButton;
    private Map<String, CollapsiblePaneAdapter> cpListener;
//    private Map<MedStockTransaction, JPanel> linemap;

    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private JToggleButton tbClosedInventory; // <= only for search function
    private Color[] color1, color2;

    /**
     * Creates new form DlgVorrat
     */
    public PnlInventory(Resident resident, JScrollPane jspSearch) {
        super("nursingrecords.inventory");
        this.jspSearch = jspSearch;
        initComponents();

        initPanel();
        switchResident(resident);
    }

    private void initPanel() {
        cpMap = Collections.synchronizedMap(new HashMap<String, CollapsiblePane>());
        cpListener = Collections.synchronizedMap(new HashMap<String, CollapsiblePaneAdapter>());
        lstInventories = Collections.synchronizedList(new ArrayList<MedInventory>());


        mapKey2ClosedToggleButton = Collections.synchronizedMap(new HashMap<String, JToggleButton>());
        color1 = SYSConst.yellow1;
        color2 = SYSConst.greyscale;

//        linemap = Collections.synchronizedMap(new HashMap<MedStockTransaction, JPanel>());
        prepareSearchArea();
    }

    @Override
    public void switchResident(Resident resident) {
        switchResident(resident, null, null);
    }

    private void switchResident(Resident res, MedInventory inventory, Closure afterwards) {
        this.resident = EntityTools.find(Resident.class, res.getId());
        GUITools.setResidentDisplay(resident);

        // only for the zebra coloring. can you believe that ?

        reloadDisplay(inventory, afterwards);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        cpsInventory.removeAll();
        synchronized (cpMap) {
            SYSTools.clear(cpMap);
        }
        synchronized (lstInventories) {
            SYSTools.clear(lstInventories);
        }
        synchronized (mapKey2ClosedToggleButton) {
            SYSTools.clear(mapKey2ClosedToggleButton);
        }
        synchronized (cpListener) {
            SYSTools.clear(cpListener);
        }
    }


    @Override
    public String getInternalClassID() {
        return internalClassID;
    }

    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);

        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout(3));
        mypanel.setBackground(Color.WHITE);

        CollapsiblePane searchPane = new CollapsiblePane(SYSTools.xx("nursingrecords.inventory"));
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);

        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }

        GUITools.addAllComponents(mypanel, addCommands());
        GUITools.addAllComponents(mypanel, addFilters());
        GUITools.addAllComponents(mypanel, addKey());

        searchPane.setContentPane(mypanel);

        searchPanes.add(searchPane);
        searchPanes.addExpansion();
    }

    private List<Component> addFilters() {
        List<Component> list = new ArrayList<Component>();

        JXSearchField search = new JXSearchField(SYSTools.xx("nursingrecords.inventory.search.stockid"));
        search.setFont(new Font("Arial", Font.PLAIN, 14));
        search.setFocusBehavior(org.jdesktop.swingx.prompt.PromptSupport.FocusBehavior.HIGHLIGHT_PROMPT);
        search.addActionListener(e -> txtSucheActionPerformed(e));
        search.setInstantSearchDelay(5000);
        list.add(search);

        tbClosedInventory = GUITools.getNiceToggleButton(SYSTools.xx("nursingrecords.inventory.showclosedinventories"));
        tbClosedInventory.addItemListener(e -> {
//                synchronized (lstInventories) {
//                    lstInventories = tbClosedInventory.isSelected() ? MedInventoryTools.getAll(resident) : MedInventoryTools.getAll(resident);
//                }
            reload();
        });
        list.add(tbClosedInventory);

        return list;
    }

    private List<Component> addKey() {
        List<Component> list = new ArrayList<Component>();
        list.add(new JSeparator());
        list.add(new JLabel(SYSTools.xx("misc.msg.key")));
        list.add(new JLabel(SYSTools.xx("nursingrecords.inventory.keydescription1"), SYSConst.icon22ledGreenOn, SwingConstants.LEADING));
        list.add(new JLabel(SYSTools.xx("nursingrecords.inventory.keydescription2"), SYSConst.icon22ledYellowOn, SwingConstants.LEADING));
        list.add(new JLabel(SYSTools.xx("nursingrecords.inventory.keydescription3"), SYSConst.icon22ledRedOn, SwingConstants.LEADING));

        return list;
    }

    private List<Component> addCommands() {
        List<Component> list = new ArrayList<Component>();

        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.INSERT, "nursingrecords.inventory")) {
            JideButton buchenButton = GUITools.createHyperlinkButton("nursingrecords.inventory.newstocks", SYSConst.icon22addrow, actionEvent -> {
                if (!ResidentTools.isActive(resident)) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.cantChangeInactiveResident"));
                    return;
                }
                if (!resident.getCalcMediUPR1()) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.inactiveCalcMed"));
                    return;
                }
                currentEditor = new DlgNewStocks(resident);
                currentEditor.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        super.windowClosing(e);
                        currentEditor = null;
                    }
                });
                currentEditor.setVisible(true);
                reload();
            });
            list.add(buchenButton);
        }

        return list;
    }

    @Override
    public void reload() {

        reloadDisplay(null, null);
    }

    private void reloadDisplay(final MedInventory singleInventory, final Closure afterwards) {
        /***
         *               _                 _ ____  _           _
         *      _ __ ___| | ___   __ _  __| |  _ \(_)___ _ __ | | __ _ _   _
         *     | '__/ _ \ |/ _ \ / _` |/ _` | | | | / __| '_ \| |/ _` | | | |
         *     | | |  __/ | (_) | (_| | (_| | |_| | \__ \ |_) | | (_| | |_| |
         *     |_|  \___|_|\___/ \__,_|\__,_|____/|_|___/ .__/|_|\__,_|\__, |
         *                                              |_|            |___/
         */
//        final boolean withworker = true;
        cpsInventory.removeAll();
        synchronized (cpMap) {
            SYSTools.clear(cpMap);
        }
//        synchronized (linemap) {
//            linemap.clear();
//        }
        synchronized (mapKey2ClosedToggleButton) {
            mapKey2ClosedToggleButton.clear();
        }
        synchronized (lstInventories) {
            lstInventories.clear();
            if (singleInventory != null) {
                lstInventories.add(singleInventory);
            } else {
                lstInventories = tbClosedInventory.isSelected() ? MedInventoryTools.getAll(resident) : MedInventoryTools.getAllActive(resident);
            }
        }

        OPDE.getMainframe().setBlocked(true);
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), -1, 100));

        SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                int progress = 0;

                synchronized (lstInventories) {
                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, lstInventories.size()));
                    for (MedInventory inventory : lstInventories) {
                        progress++;
                        createCP4(inventory);
                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, lstInventories.size()));
                    }
                }

                return null;
            }

            @Override
            protected void done() {
                buildPanel(singleInventory == null);
                if (afterwards != null) {
                    afterwards.execute(null);
                }
                OPDE.getDisplayManager().setProgressBarMessage(null);
                OPDE.getMainframe().setBlocked(false);
            }
        };
        worker.execute();


    }

    private Color getColor(int level, boolean odd) {
        if (odd) {
            return color1[level];
        } else {
            return color2[level];
        }
    }

    private CollapsiblePane createCP4(final MedInventory inventory) {
        /***
         *                          _        ____ ____  _  _    _____                      _                 __
         *       ___ _ __ ___  __ _| |_ ___ / ___|  _ \| || |  / /_ _|_ ____   _____ _ __ | |_ ___  _ __ _   \ \
         *      / __| '__/ _ \/ _` | __/ _ \ |   | |_) | || |_| | | || '_ \ \ / / _ \ '_ \| __/ _ \| '__| | | | |
         *     | (__| | |  __/ (_| | ||  __/ |___|  __/|__   _| | | || | | \ V /  __/ | | | || (_) | |  | |_| | |
         *      \___|_|  \___|\__,_|\__\___|\____|_|      |_| | ||___|_| |_|\_/ \___|_| |_|\__\___/|_|   \__, | |
         *                                                     \_\                                       |___/_/
         */
        final String key = inventory.getID() + ".xinventory";
        synchronized (cpMap) {
            if (!cpMap.containsKey(key)) {
                cpMap.put(key, new CollapsiblePane());
                try {
                    cpMap.get(key).setCollapsed(true);
                } catch (PropertyVetoException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

            }


            cpMap.get(key).setName("inventory");

//            final CollapsiblePane cpInventory = cpMap.get(key);


            BigDecimal sumInventory = BigDecimal.ZERO;
            try {
                EntityManager em = OPDE.createEM();
                sumInventory = MedInventoryTools.getSum(em, inventory);
                em.close();
            } catch (Exception e) {
                OPDE.fatal(e);
            }

            String title = "<html><table border=\"0\">" +
                    "<tr>" +

                    "<td width=\"520\" align=\"left\"><font size=+1>" +
                    inventory.getText() + "</font></td>" +
                    "<td width=\"200\" align=\"right\"><font size=+1>" + SYSTools.formatBigDecimal(sumInventory) + " " + DosageFormTools.getPackageText(MedInventoryTools.getForm(inventory)) + "</font></td>" +

                    "</tr>" +
                    "</table>" +

                    "</html>";

            DefaultCPTitle cptitle = new DefaultCPTitle(title, e -> {
                try {
                    cpMap.get(key).setCollapsed(!cpMap.get(key).isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            });
            cpMap.get(key).setTitleLabelComponent(cptitle.getMain());
            cpMap.get(key).setSlidingDirection(SwingConstants.SOUTH);
            cptitle.getButton().setIcon(inventory.isClosed() ? SYSConst.icon22stopSign : null);


            // https://github.com/tloehr/Offene-Pflege.de/issues/42
            if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, "nursingrecords.inventory")) {
                /***
                 *      _____    _ _ _     _   _
                 *     | ____|__| (_) |_  | \ | | __ _ _ __ ___   ___
                 *     |  _| / _` | | __| |  \| |/ _` | '_ ` _ \ / _ \
                 *     | |__| (_| | | |_  | |\  | (_| | | | | | |  __/
                 *     |_____\__,_|_|\__| |_| \_|\__,_|_| |_| |_|\___|
                 *
                 */
                final JButton btnEditInvName = new JButton(SYSConst.icon22edit3);
                btnEditInvName.setPressedIcon(SYSConst.icon22edit3);
                btnEditInvName.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnEditInvName.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnEditInvName.setContentAreaFilled(false);
                btnEditInvName.setBorder(null);
                btnEditInvName.setToolTipText(SYSTools.xx("nursingrecords.inventory.btnEditInvName.tooltip"));
                btnEditInvName.addActionListener(actionEvent -> {
                    currentEditor = new DlgYesNo(SYSConst.icon48edit, answer -> {
                        if (answer != null) {
                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();
                                MedInventory myInventory = em.merge(inventory);
                                em.lock(myInventory, LockModeType.OPTIMISTIC);
                                em.lock(myInventory.getResident(), LockModeType.OPTIMISTIC);
                                myInventory.setText(answer.toString());
                                em.getTransaction().commit();

                                createCP4(myInventory);
                                buildPanel();
                            } catch (OptimisticLockException ole) {
                                OPDE.warn(ole);
                                if (em.getTransaction().isActive()) {
                                    em.getTransaction().rollback();
                                }
                                if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
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
                        currentEditor = null;
                    }, "nursingrecords.inventory.btnEditInvName.tooltip", inventory.getText(), new Validator() {
                        @Override
                        public boolean isValid(String value) {
                            return !value.trim().isEmpty();
                        }

                        @Override
                        public Object parse(String text) {
                            return text;
                        }
                    });
                    currentEditor.setVisible(true);
                });
                btnEditInvName.setEnabled(!inventory.isClosed());
                cptitle.getRight().add(btnEditInvName);
            }

            if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, "nursingrecords.inventory")) {
                /***
                 *       ____ _                ___                      _
                 *      / ___| | ___  ___  ___|_ _|_ ____   _____ _ __ | |_ ___  _ __ _   _
                 *     | |   | |/ _ \/ __|/ _ \| || '_ \ \ / / _ \ '_ \| __/ _ \| '__| | | |
                 *     | |___| | (_) \__ \  __/| || | | \ V /  __/ | | | || (_) | |  | |_| |
                 *      \____|_|\___/|___/\___|___|_| |_|\_/ \___|_| |_|\__\___/|_|   \__, |
                 *                                                                    |___/
                 */
                final JButton btnCloseInventory = new JButton(SYSConst.icon22playerStop);
                btnCloseInventory.setPressedIcon(SYSConst.icon22playerStopPressed);
                btnCloseInventory.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnCloseInventory.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnCloseInventory.setContentAreaFilled(false);
                btnCloseInventory.setBorder(null);
                btnCloseInventory.setToolTipText(SYSTools.xx("nursingrecords.inventory.btncloseinventory.tooltip"));
                btnCloseInventory.addActionListener(actionEvent -> {
                    currentEditor = new DlgYesNo(SYSTools.xx("nursingrecords.inventory.question.close1") + "<br/><b>" + inventory.getText() + "</b>" +
                            "<br/>" + SYSTools.xx("nursingrecords.inventory.question.close2"), SYSConst.icon48playerStop, answer -> {
                        if (answer.equals(JOptionPane.YES_OPTION)) {
                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();

                                MedInventory myInventory = em.merge(inventory);
                                em.lock(myInventory, LockModeType.OPTIMISTIC);
                                em.lock(myInventory.getResident(), LockModeType.OPTIMISTIC);

                                // close all stocks
                                for (MedStock stock : MedStockTools.getAll(myInventory)) {
                                    if (!stock.isClosed()) {
                                        MedStock mystock = em.merge(stock);
                                        em.lock(mystock, LockModeType.OPTIMISTIC);
                                        mystock.setNextStock(null);
                                        MedStockTools.close(em, mystock, SYSTools.xx("nursingrecords.inventory.stock.msg.inventory_closed"), MedStockTransactionTools.STATE_EDIT_INVENTORY_CLOSED);
                                    }
                                }
                                // close inventory
                                myInventory.setTo(new Date());

                                em.getTransaction().commit();

                                createCP4(myInventory);
                                buildPanel();
                            } catch (OptimisticLockException ole) {
                                OPDE.warn(ole);
                                if (em.getTransaction().isActive()) {
                                    em.getTransaction().rollback();
                                }
                                if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
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
                        currentEditor = null;
                    });
                    currentEditor.setVisible(true);
                });
                btnCloseInventory.setEnabled(!inventory.isClosed());
                cptitle.getRight().add(btnCloseInventory);
            }

            if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.DELETE, "nursingrecords.inventory")) {
                /***
                 *      ____       _ ___                      _
                 *     |  _ \  ___| |_ _|_ ____   _____ _ __ | |_ ___  _ __ _   _
                 *     | | | |/ _ \ || || '_ \ \ / / _ \ '_ \| __/ _ \| '__| | | |
                 *     | |_| |  __/ || || | | \ V /  __/ | | | || (_) | |  | |_| |
                 *     |____/ \___|_|___|_| |_|\_/ \___|_| |_|\__\___/|_|   \__, |
                 *                                                          |___/
                 */
                final JButton btnDelInventory = new JButton(SYSConst.icon22delete);
                btnDelInventory.setPressedIcon(SYSConst.icon22deletePressed);
                btnDelInventory.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnDelInventory.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnDelInventory.setContentAreaFilled(false);
                btnDelInventory.setBorder(null);
                btnDelInventory.setEnabled(ResidentTools.isActive(resident));
                btnDelInventory.setToolTipText(SYSTools.xx("nursingrecords.inventory.btndelinventory.tooltip"));
                btnDelInventory.addActionListener(actionEvent -> {
                    currentEditor = new DlgYesNo(SYSTools.xx("nursingrecords.inventory.question.delete1") + "<br/><b>" + inventory.getText() + "</b>" +
                            "<br/>" + SYSTools.xx("nursingrecords.inventory.question.delete2"), SYSConst.icon48delete, answer -> {
                        if (answer.equals(JOptionPane.YES_OPTION)) {
                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();

                                MedInventory myInventory = em.merge(inventory);
                                em.lock(myInventory, LockModeType.OPTIMISTIC);
                                em.lock(myInventory.getResident(), LockModeType.OPTIMISTIC);

                                em.remove(myInventory);

                                em.getTransaction().commit();

//                                        lstInventories.remove(inventory);
                                buildPanel();
                            } catch (OptimisticLockException ole) {
                                OPDE.warn(ole);
                                if (em.getTransaction().isActive()) {
                                    em.getTransaction().rollback();
                                }
                                if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
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
                                currentEditor = null;
                            }
                        }
                    });
                    currentEditor.setVisible(true);
                });
                cptitle.getRight().add(btnDelInventory);
            }


            final JToggleButton tbClosedStock = GUITools.getNiceToggleButton(null);
            tbClosedStock.setToolTipText(SYSTools.xx("nursingrecords.inventory.showclosedstocks"));
            if (!inventory.isClosed()) {
                tbClosedStock.addItemListener(e -> cpMap.get(key).setContentPane(createContentPanel4(inventory, tbClosedStock.isSelected())));
            }
            tbClosedStock.setSelected(inventory.isClosed());
            tbClosedStock.setEnabled(!inventory.isClosed());

            mapKey2ClosedToggleButton.put(key, tbClosedStock);

            cptitle.getRight().add(tbClosedStock);


            CollapsiblePaneAdapter adapter = new CollapsiblePaneAdapter() {
                @Override
                public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                    cpMap.get(key).setContentPane(createContentPanel4(inventory, tbClosedStock.isSelected()));
                }
            };
            synchronized (cpListener) {
                if (cpListener.containsKey(key)) {
                    cpMap.get(key).removeCollapsiblePaneListener(cpListener.get(key));
                }
                cpListener.put(key, adapter);
                cpMap.get(key).addCollapsiblePaneListener(adapter);
            }

            if (!cpMap.get(key).isCollapsed()) {
                cpMap.get(key).setContentPane(createContentPanel4(inventory, tbClosedStock.isSelected()));
            }

            cpMap.get(key).setHorizontalAlignment(SwingConstants.LEADING);
            cpMap.get(key).setOpaque(false);
            cpMap.get(key).setBackground(getColor(SYSConst.medium2, lstInventories.indexOf(inventory) % 2 != 0));

            return cpMap.get(key);
        }
    }


    private JPanel createContentPanel4(final MedInventory inventory, boolean closed2) {
        final JPanel pnlContent = new JPanel(new VerticalLayout());
//        Collections.sort(inventory.getMedStocks());
        for (MedStock stock : MedStockTools.getAll(inventory)) {
            if (closed2 || !stock.isClosed()) {
                pnlContent.add(createCP4(stock));
            }
        }

        return pnlContent;
    }

    private CollapsiblePane createCP4(final MedStock stock) {
        /***
         *                          _        ____ ____  _  _    __   _             _   __
         *       ___ _ __ ___  __ _| |_ ___ / ___|  _ \| || |  / /__| |_ ___   ___| | _\ \
         *      / __| '__/ _ \/ _` | __/ _ \ |   | |_) | || |_| / __| __/ _ \ / __| |/ /| |
         *     | (__| | |  __/ (_| | ||  __/ |___|  __/|__   _| \__ \ || (_) | (__|   < | |
         *      \___|_|  \___|\__,_|\__\___|\____|_|      |_| | |___/\__\___/ \___|_|\_\| |
         *                                                     \_\                     /_/
         */
        final String key = stock.getID() + ".xstock";
        synchronized (cpMap) {
            if (!cpMap.containsKey(key)) {
                cpMap.put(key, new CollapsiblePane());
                try {
                    cpMap.get(key).setCollapsed(true);
                } catch (PropertyVetoException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

            }
            cpMap.get(key).setName("stock");

            BigDecimal sumStock = BigDecimal.ZERO;
            try {
                EntityManager em = OPDE.createEM();
                sumStock = MedStockTools.getSum(em, stock);
                em.close();
            } catch (Exception e) {
                OPDE.fatal(e);
            }

            String title = "<html><table border=\"0\">" +
                    "<tr>" +
                    (stock.isClosed() ? "<s>" : "") +
                    "<td width=\"600\" align=\"left\">" + MedStockTools.getAsHTML(stock) + "</td>" +
                    "<td width=\"200\" align=\"right\">" + SYSTools.formatBigDecimal(sumStock) + " " + DosageFormTools.getPackageText(MedInventoryTools.getForm(stock.getInventory())) + "</td>" +
                    (stock.isClosed() ? "</s>" : "") +
                    "</tr>" +
                    "</table>" +


                    "</html>";

            DefaultCPTitle cptitle = new DefaultCPTitle(title, e -> {
                try {
                    cpMap.get(key).setCollapsed(!cpMap.get(key).isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            });

            cpMap.get(key).setTitleLabelComponent(cptitle.getMain());
            cpMap.get(key).setSlidingDirection(SwingConstants.SOUTH);


            cptitle.getRight().add(new StockPanel(stock));


            if (!stock.getInventory().isClosed()) {
                /***
                 *      ____       _       _   _          _          _
                 *     |  _ \ _ __(_)_ __ | |_| |    __ _| |__   ___| |
                 *     | |_) | '__| | '_ \| __| |   / _` | '_ \ / _ \ |
                 *     |  __/| |  | | | | | |_| |__| (_| | |_) |  __/ |
                 *     |_|   |_|  |_|_| |_|\__|_____\__,_|_.__/ \___|_|
                 *
                 */
                final JButton btnPrintLabel = new JButton(SYSConst.icon22print2);
                btnPrintLabel.setPressedIcon(SYSConst.icon22print2Pressed);
                btnPrintLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnPrintLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnPrintLabel.setContentAreaFilled(false);
                btnPrintLabel.setBorder(null);
                btnPrintLabel.setToolTipText(SYSTools.xx("nursingrecords.inventory.stock.btnprintlabel.tooltip"));
                btnPrintLabel.addActionListener(actionEvent -> {
                    LogicalPrinter logicalPrinter = OPDE.getLogicalPrinters().getMapName2LogicalPrinter().get(OPDE.getProps().getProperty(SYSPropsTools.KEY_LOGICAL_PRINTER));
                    PrinterForm printerForm1 = logicalPrinter.getForms().get(OPDE.getProps().getProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL));
                    OPDE.getPrintProcessor().addPrintJob(new PrintListElement(stock, logicalPrinter, printerForm1, OPDE.getProps().getProperty(SYSPropsTools.KEY_PHYSICAL_PRINTER)));
                });
                btnPrintLabel.setEnabled(OPDE.getPrintProcessor().isWorking());
                cptitle.getRight().add(btnPrintLabel);
            }

            /***
             *      __  __
             *     |  \/  | ___ _ __  _   _
             *     | |\/| |/ _ \ '_ \| | | |
             *     | |  | |  __/ | | | |_| |
             *     |_|  |_|\___|_| |_|\__,_|
             *
             */
            final JButton btnMenu = new JButton(SYSConst.icon22menu);
            btnMenu.setPressedIcon(SYSConst.icon22Pressed);
            btnMenu.setAlignmentX(Component.RIGHT_ALIGNMENT);
//        btnMenu.setAlignmentY(Component.TOP_ALIGNMENT);
            btnMenu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnMenu.setContentAreaFilled(false);
            btnMenu.setBorder(null);
            btnMenu.addActionListener(e -> {
                JidePopup popup = new JidePopup();
                popup.setMovable(false);
                popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
                popup.setOwner(btnMenu);
                popup.removeExcludedComponent(btnMenu);
                JPanel pnl = getMenu(stock);
                popup.getContentPane().add(pnl);
                popup.setDefaultFocusComponent(pnl);

                GUITools.showPopup(popup, SwingConstants.WEST);
            });
            cptitle.getRight().add(btnMenu);


            CollapsiblePaneAdapter adapter = new CollapsiblePaneAdapter() {
                @Override
                public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                    cpMap.get(key).setContentPane(createContentPanel4(stock));
                }
            };
            synchronized (cpListener) {
                if (cpListener.containsKey(key)) {
                    cpMap.get(key).removeCollapsiblePaneListener(cpListener.get(key));
                }
                cpListener.put(key, adapter);
                cpMap.get(key).addCollapsiblePaneListener(adapter);
            }


            if (!cpMap.get(key).isCollapsed()) {
                JPanel contentPane = createContentPanel4(stock);
                cpMap.get(key).setContentPane(contentPane);
            }

            cpMap.get(key).setHorizontalAlignment(SwingConstants.LEADING);
            cpMap.get(key).setOpaque(false);
            cpMap.get(key).setBackground(getColor(SYSConst.light3, lstInventories.indexOf(stock.getInventory()) % 2 != 0));


            return cpMap.get(key);
        }
    }

    /**
     * This inner class is responsible for the three led controls to open, close, reactivate or reclose a given stock.
     */
    private class StockPanel extends JPanel {
        boolean selRed, selGreen, selYellow;
        JRadioButton red, green, yellow;
        MedStock stock;
        boolean ignoreEvent = false;

        StockPanel(MedStock stock1) {
            super();
            this.stock = stock1;
            final String key = stock.getID() + ".xstock";
            final ButtonGroup bg = new ButtonGroup();
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

            red = new JRadioButton(SYSConst.icon22ledRedOff);
            red.setSelectedIcon(SYSConst.icon22ledRedOn);
            selRed = stock.isClosed();
            red.setSelected(selRed);
            red.setContentAreaFilled(false);
            red.setBorderPainted(false);
            red.setBorder(null);
            red.setOpaque(false);
            red.setEnabled(ResidentTools.isActive(resident));
            bg.add(red);


            yellow = new JRadioButton(SYSConst.icon22ledYellowOff);
            yellow.setSelectedIcon(SYSConst.icon22ledYellowOn);
            selYellow = stock.isOpened();
            yellow.setSelected(selYellow);
            yellow.setContentAreaFilled(false);
            yellow.setBorderPainted(false);
            yellow.setBorder(null);
            yellow.setOpaque(false);
            yellow.setEnabled(ResidentTools.isActive(resident));
            bg.add(yellow);

            green = new JRadioButton(SYSConst.icon22ledGreenOff);
            green.setSelectedIcon(SYSConst.icon22ledGreenOn);
            selGreen = stock.isNew();
            green.setSelected(selGreen);
            green.setContentAreaFilled(false);
            green.setBorderPainted(false);
            green.setBorder(null);
            green.setOpaque(false);
            green.setEnabled(ResidentTools.isActive(resident));
            bg.add(green);
            /***
             *                   _
             *      _ __ ___  __| |
             *     | '__/ _ \/ _` |
             *     | | |  __/ (_| |
             *     |_|  \___|\__,_|
             *
             */
            red.addItemListener(ie -> {
                if (ignoreEvent) return;
                if (!OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, "nursingrecords.inventory")) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.noaccess"));
                    reset();
                    return;
                }
                if (ie.getStateChange() == ItemEvent.SELECTED) {
                    /***
                     *       ____ _
                     *      / ___| | ___  ___  ___
                     *     | |   | |/ _ \/ __|/ _ \
                     *     | |___| | (_) \__ \  __/
                     *      \____|_|\___/|___/\___|
                     *
                     */
                    if (!stock.isOpened()) {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("nursingrecords.inventory.stockpanel.stockIsNotOpen1"));
                        reset();
                        return;
                    }

                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        MedStock myStock = em.merge(stock);
                        em.lock(myStock, LockModeType.OPTIMISTIC);
                        em.lock(em.merge(myStock.getInventory().getResident()), LockModeType.OPTIMISTIC);
                        em.lock(em.merge(myStock.getInventory()), LockModeType.OPTIMISTIC);
                        myStock.setNextStock(null);
                        MedStockTools.close(em, myStock, SYSTools.xx("nursingrecords.inventory.stockpanel.STATE_EDIT_STOCK_CLOSED"), MedStockTransactionTools.STATE_EDIT_STOCK_CLOSED);
                        myStock.setState(MedStockTools.STATE_NOTHING);
                        em.getTransaction().commit();
//                            synchronized (lstInventories) {
//                                int index = lstInventories.indexOf(myStock.getInventory());
//                                lstInventories.get(index).getMedStocks().remove(stock);
//                                lstInventories.get(index).getMedStocks().add(myStock);
//                            }
//                            contentmap.remove(key);
                        createCP4(myStock.getInventory());
                        buildPanel();
                    } catch (OptimisticLockException ole) {
                        OPDE.warn(ole);
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
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

                    selRed = true;
//                        selOrange = false;
                    selYellow = false;
                    selGreen = false;
                }
            });

            /***
             *                 _ _
             *      _   _  ___| | | _____      __
             *     | | | |/ _ \ | |/ _ \ \ /\ / /
             *     | |_| |  __/ | | (_) \ V  V /
             *      \__, |\___|_|_|\___/ \_/\_/
             *      |___/
             */
            yellow.addItemListener(ie -> {
                if (ignoreEvent) return;
                if (!OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, "nursingrecords.inventory")) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.noaccess"));
                    reset();
                    return;
                }
                ;
                if (ie.getStateChange() == ItemEvent.SELECTED) {
                    if (!stock.isToBeClosedSoon()) {
                        MedStock openedStock = MedInventoryTools.getCurrentOpened(stock.getInventory());
                        if (openedStock != null) {
                            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("nursingrecords.inventory.stockpanel.anotherStockIsOpened"));
                            reset();
                            return;
                        }
                    }

                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        MedStock myStock = em.merge(stock);
                        em.lock(myStock, LockModeType.OPTIMISTIC);
                        em.lock(em.merge(myStock.getInventory().getResident()), LockModeType.OPTIMISTIC);
                        em.lock(em.merge(myStock.getInventory()), LockModeType.OPTIMISTIC);

                        if (!stock.isToBeClosedSoon()) {
                            if (stock.isClosed()) {
                                /***
                                 *      ____                 _   _            _
                                 *     |  _ \ ___  __ _  ___| |_(_)_   ____ _| |_ ___
                                 *     | |_) / _ \/ _` |/ __| __| \ \ / / _` | __/ _ \
                                 *     |  _ <  __/ (_| | (__| |_| |\ V / (_| | ||  __/
                                 *     |_| \_\___|\__,_|\___|\__|_| \_/ \__,_|\__\___|
                                 *
                                 */
                                myStock.setOut(SYSConst.DATE_UNTIL_FURTHER_NOTICE);
                            } else {
                                /***
                                 *       ___
                                 *      / _ \ _ __   ___ _ __
                                 *     | | | | '_ \ / _ \ '_ \
                                 *     | |_| | |_) |  __/ | | |
                                 *      \___/| .__/ \___|_| |_|
                                 *           |_|
                                 */
                                myStock.setOpened(new Date());
                            }
                        }
                        myStock.setState(MedStockTools.STATE_NOTHING);
                        myStock.setNextStock(null);
                        em.getTransaction().commit();
//                            synchronized (lstInventories) {
//                                int index = lstInventories.indexOf(myStock.getInventory());
//                                lstInventories.get(index).getMedStocks().remove(stock);
//                                lstInventories.get(index).getMedStocks().add(myStock);
//                            }
                        createCP4(myStock.getInventory());
                        buildPanel();
                    } catch (OptimisticLockException ole) {
                        OPDE.warn(ole);
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
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


                    selRed = false;
//                        selOrange = false;
                    selYellow = true;
                    selGreen = false;
                }
            });
            /***
             *
             *       __ _ _ __ ___  ___ _ __
             *      / _` | '__/ _ \/ _ \ '_ \
             *     | (_| | | |  __/  __/ | | |
             *      \__, |_|  \___|\___|_| |_|
             *      |___/
             */
            green.addItemListener(ie -> {
                if (ignoreEvent) return;
                if (!OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, "nursingrecords.inventory")) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.noaccess"));
                    reset();
                    return;
                }
                if (ie.getStateChange() == ItemEvent.SELECTED) {
                    /***
                     *                      _                                                  _
                     *      _ __ ___   __ _| | _____   _ __   _____      __   __ _  __ _  __ _(_)_ __
                     *     | '_ ` _ \ / _` | |/ / _ \ | '_ \ / _ \ \ /\ / /  / _` |/ _` |/ _` | | '_ \
                     *     | | | | | | (_| |   <  __/ | | | |  __/\ V  V /  | (_| | (_| | (_| | | | | |
                     *     |_| |_| |_|\__,_|_|\_\___| |_| |_|\___| \_/\_/    \__,_|\__, |\__,_|_|_| |_|
                     *                                                             |___/
                     */
                    if (!stock.isOpened()) {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("nursingrecords.inventory.stockpanel.stockIsNotOpen2"));
                        reset();
                        return;
                    }

                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        MedStock myStock = em.merge(stock);
                        em.lock(myStock, LockModeType.OPTIMISTIC);
                        em.lock(em.merge(myStock.getInventory().getResident()), LockModeType.OPTIMISTIC);
                        em.lock(em.merge(myStock.getInventory()), LockModeType.OPTIMISTIC);
                        myStock.setOut(SYSConst.DATE_UNTIL_FURTHER_NOTICE);
                        myStock.setOpened(SYSConst.DATE_UNTIL_FURTHER_NOTICE);
                        myStock.setState(MedStockTools.STATE_NOTHING);
                        em.getTransaction().commit();
//                            synchronized (lstInventories) {
//                                int index = lstInventories.indexOf(myStock.getInventory());
//                                lstInventories.get(index).getMedStocks().remove(stock);
//                                lstInventories.get(index).getMedStocks().add(myStock);
//                            }
                        // contentmap.remove(key);
                        createCP4(myStock.getInventory());
                        buildPanel();
                    } catch (OptimisticLockException ole) {
                        OPDE.warn(ole);
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
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

                    selRed = false;

                    selYellow = false;
                    selGreen = true;
                }
            });

            add(green);
            add(yellow);

            add(red);
            setOpaque(false);
        }

        void reset() {
            ignoreEvent = true;
            red.setSelected(selRed);

            yellow.setSelected(selYellow);
            green.setSelected(selGreen);
            ignoreEvent = false;
        }
    }


    private JPanel createContentPanel4(final MedStock stock) {
//        final String key = stock.getID() + ".xstock";

//        if (!contentmap.containsKey(key)) {

        final JPanel pnlTX = new JPanel(new VerticalLayout());
//            pnlTX.setLayout(new BoxLayout(pnlTX, BoxLayout.PAGE_AXIS));

        pnlTX.setOpaque(true);
//        pnlTX.setBackground(Color.white);
        synchronized (lstInventories) {
            pnlTX.setBackground(getColor(SYSConst.light2, lstInventories.indexOf(stock.getInventory()) % 2 != 0));
        }

        /***
         *         _       _     _ _______  __
         *        / \   __| | __| |_   _\ \/ /
         *       / _ \ / _` |/ _` | | |  \  /
         *      / ___ \ (_| | (_| | | |  /  \
         *     /_/   \_\__,_|\__,_| |_| /_/\_\
         *
         */
        JideButton btnAddTX = GUITools.createHyperlinkButton("nursingrecords.inventory.newmedstocktx", SYSConst.icon22add, e -> {
            currentEditor = new DlgTX(new MedStockTransaction(stock, BigDecimal.ONE, MedStockTransactionTools.STATE_EDIT_MANUAL), o -> {
                if (o != null) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();

                        final MedStockTransaction myTX = (MedStockTransaction) em.merge(o);
                        MedStock myStock = em.merge(stock);
                        em.lock(myStock, LockModeType.OPTIMISTIC);
                        em.lock(myStock.getInventory(), LockModeType.OPTIMISTIC);
                        em.lock(em.merge(myTX.getStock().getInventory().getResident()), LockModeType.OPTIMISTIC);
                        em.getTransaction().commit();

                        createCP4(myStock.getInventory());

                        buildPanel();
                    } catch (OptimisticLockException ole) {
                        OPDE.warn(ole);
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                            OPDE.getMainframe().emptyFrame();
                            OPDE.getMainframe().afterLogin();
                        }
                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                    } catch (Exception e1) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        OPDE.fatal(e1);
                    } finally {
                        em.close();
                    }
                }
                currentEditor = null;
            });
        });
        btnAddTX.setEnabled(!stock.isClosed());
        pnlTX.add(btnAddTX);

        /***
         *      ____  _                           _ _   _______  __
         *     / ___|| |__   _____      __   __ _| | | |_   _\ \/ /___
         *     \___ \| '_ \ / _ \ \ /\ / /  / _` | | |   | |  \  // __|
         *      ___) | | | | (_) \ V  V /  | (_| | | |   | |  /  \\__ \
         *     |____/|_| |_|\___/ \_/\_/    \__,_|_|_|   |_| /_/\_\___/
         *
         */
        OPDE.getMainframe().setBlocked(true);
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), -1, 100));

        SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                int progress = 0;


                List<MedStockTransaction> listTX = MedStockTransactionTools.getAll(stock);
                OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, listTX.size()));

                BigDecimal rowsum = MedStockTools.getSum(stock);
//                BigDecimal rowsum = MedStockTools.getSum(stock);
//                Collections.sort(stock.getStockTransaction());
                for (final MedStockTransaction tx : listTX) {
                    progress++;
                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, listTX.size()));
                    String title = "<html><table border=\"0\">" +
                            "<tr>" +
                            "<td width=\"130\" align=\"left\">" + DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(tx.getPit())
                            + "<br/>[" + tx.getID() + "]"
                            + "</td>" +
                            "<td width=\"200\" align=\"center\">" + SYSTools.catchNull(tx.getText(), "--") + "</td>" +

                            "<td width=\"100\" align=\"right\">" +
                            SYSTools.formatBigDecimal(tx.getAmount()) +
                            "</td>" +

                            "<td width=\"100\" align=\"right\">" +
                            (rowsum.compareTo(BigDecimal.ZERO) < 0 ? "<font color=\"red\">" : "") +
                            SYSTools.formatBigDecimal(rowsum) +
                            (rowsum.compareTo(BigDecimal.ZERO) < 0 ? "</font>" : "") +
                            "</td>" +


                            (stock.getTradeForm().isWeightControlled() ?
                                    "<td width=\"100\" align=\"right\">" +
                                            SYSTools.formatBigDecimal(tx.getWeight()) + "g" +
                                            "</td>" : "") +

                            "<td width=\"100\" align=\"left\">" +
                            SYSTools.anonymizeUser(tx.getUser()) +
                            "</td>" +
                            "</tr>" +
                            "</table>" +

                            "</font></html>";

                    rowsum = rowsum.subtract(tx.getAmount());

                    final DefaultCPTitle pnlTitle = new DefaultCPTitle(title, null);


                    //                pnlTitle.getLeft().addMouseListener();


                    if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.DELETE, "nursingrecords.inventory")) {
                        /***
                         *      ____       _ _______  __
                         *     |  _ \  ___| |_   _\ \/ /
                         *     | | | |/ _ \ | | |  \  /
                         *     | |_| |  __/ | | |  /  \
                         *     |____/ \___|_| |_| /_/\_\
                         *
                         */
                        final JButton btnDelTX = new JButton(SYSConst.icon22delete);
                        btnDelTX.setPressedIcon(SYSConst.icon22deletePressed);
                        btnDelTX.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        btnDelTX.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        btnDelTX.setContentAreaFilled(false);
                        btnDelTX.setBorder(null);
                        btnDelTX.setToolTipText(SYSTools.xx("nursingrecords.inventory.tx.btndelete.tooltip"));
                        btnDelTX.addActionListener(actionEvent -> {
                            currentEditor = new DlgYesNo(SYSTools.xx("misc.questions.delete1") + "<br/><i>" + DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(tx.getPit()) +
                                    "&nbsp;" + tx.getUser().getUID() + "</i><br/>" + SYSTools.xx("misc.questions.delete2"), SYSConst.icon48delete, answer -> {
                                if (answer.equals(JOptionPane.YES_OPTION)) {
                                    EntityManager em = OPDE.createEM();
                                    try {
                                        em.getTransaction().begin();

                                        MedStockTransaction myTX = em.merge(tx);
                                        MedStock myStock = em.merge(stock);
                                        em.lock(em.merge(myTX.getStock().getInventory().getResident()), LockModeType.OPTIMISTIC);
                                        em.lock(myStock, LockModeType.OPTIMISTIC);
                                        em.lock(myStock.getInventory(), LockModeType.OPTIMISTIC);
                                        em.remove(myTX);
//                                                myStock.getStockTransaction().remove(myTX);
                                        em.getTransaction().commit();

                                        createCP4(myStock.getInventory());

                                        buildPanel();
                                    } catch (OptimisticLockException ole) {
                                        OPDE.warn(ole);
                                        if (em.getTransaction().isActive()) {
                                            em.getTransaction().rollback();
                                        }
                                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
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
                                        currentEditor = null;
                                    }
                                }
                            });
                            currentEditor.setVisible(true);


                        });
                        btnDelTX.setEnabled(!stock.isClosed() && (tx.getState() == MedStockTransactionTools.STATE_DEBIT || tx.getState() == MedStockTransactionTools.STATE_EDIT_MANUAL));
                        pnlTitle.getRight().add(btnDelTX);
                    }


                    /***
                     *      _   _           _         _______  __
                     *     | | | |_ __   __| | ___   |_   _\ \/ /
                     *     | | | | '_ \ / _` |/ _ \    | |  \  /
                     *     | |_| | | | | (_| | (_) |   | |  /  \
                     *      \___/|_| |_|\__,_|\___/    |_| /_/\_\
                     *
                     */
                    final JButton btnUndoTX = new JButton(SYSConst.icon22undo);
                    btnUndoTX.setPressedIcon(SYSConst.icon22Pressed);
                    btnUndoTX.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    btnUndoTX.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    btnUndoTX.setContentAreaFilled(false);
                    btnUndoTX.setBorder(null);
                    btnUndoTX.setToolTipText(SYSTools.xx("nursingrecords.inventory.tx.btnUndoTX.tooltip"));
                    btnUndoTX.addActionListener(actionEvent -> {
                        currentEditor = new DlgYesNo(SYSTools.xx("misc.questions.undo1") + "<br/><i>" + DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(tx.getPit()) +
                                "&nbsp;" + tx.getUser().getUID() + "</i><br/>" + SYSTools.xx("misc.questions.undo2"), SYSConst.icon48undo, answer -> {
                            if (answer.equals(JOptionPane.YES_OPTION)) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    MedStock myStock = em.merge(stock);
                                    final MedStockTransaction myOldTX = em.merge(tx);

                                    myOldTX.setState(MedStockTransactionTools.STATE_CANCELLED);
                                    final MedStockTransaction myNewTX = em.merge(new MedStockTransaction(myStock, myOldTX.getAmount().negate(), MedStockTransactionTools.STATE_CANCEL_REC));
                                    myOldTX.setText(SYSTools.xx("misc.msg.reversedBy") + ": " + DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(myNewTX.getPit()));
                                    myNewTX.setText(SYSTools.xx("misc.msg.reversalFor") + ": " + DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(myOldTX.getPit()));

//                                            myStock.getStockTransaction().add(myNewTX);
//                                            myStock.getStockTransaction().remove(tx);
//                                            myStock.getStockTransaction().add(myOldTX);

                                    em.lock(em.merge(myNewTX.getStock().getInventory().getResident()), LockModeType.OPTIMISTIC);
                                    em.lock(myStock, LockModeType.OPTIMISTIC);
                                    em.lock(myStock.getInventory(), LockModeType.OPTIMISTIC);

                                    em.getTransaction().commit();

//                                            synchronized (lstInventories) {
//                                                int indexInventory = lstInventories.indexOf(stock.getInventory());
//                                                int indexStock = lstInventories.get(indexInventory).getMedStocks().indexOf(stock);
//                                                lstInventories.get(indexInventory).getMedStocks().remove(stock);
//                                                lstInventories.get(indexInventory).getMedStocks().add(indexStock, myStock);
//                                            }

//                                            synchronized (linemap) {
//                                                linemap.remove(tx);
//                                            }
                                    createCP4(myStock.getInventory());
                                    buildPanel();
//                                            SwingUtilities.invokeLater(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    synchronized (linemap) {
//                                                        GUITools.flashBackground(linemap.get(myOldTX), Color.RED, 2);
//                                                        GUITools.flashBackground(linemap.get(myNewTX), Color.YELLOW, 2);
//                                                    }
//                                                }
//                                            });
                                } catch (OptimisticLockException ole) {
                                    OPDE.warn(ole);
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                    if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
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
                                    currentEditor = null;
                                }
                            }
                        });
                        currentEditor.setVisible(true);

                    });
                    btnUndoTX.setEnabled(!stock.isClosed() && (tx.getState() == MedStockTransactionTools.STATE_DEBIT || tx.getState() == MedStockTransactionTools.STATE_EDIT_MANUAL));
                    pnlTitle.getRight().add(btnUndoTX);

                    if (stock.getTradeForm().isWeightControlled() && OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, "nursingrecords.inventory")) {
                        /***
                         *               _ __        __   _       _     _
                         *      ___  ___| |\ \      / /__(_) __ _| |__ | |_
                         *     / __|/ _ \ __\ \ /\ / / _ \ |/ _` | '_ \| __|
                         *     \__ \  __/ |_ \ V  V /  __/ | (_| | | | | |_
                         *     |___/\___|\__| \_/\_/ \___|_|\__, |_| |_|\__|
                         *                                  |___/
                         */
                        final JButton btnSetWeight = new JButton(SYSConst.icon22scales);
                        btnSetWeight.setPressedIcon(SYSConst.icon22Pressed);
                        btnSetWeight.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        btnSetWeight.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        btnSetWeight.setContentAreaFilled(false);
                        btnSetWeight.setBorder(null);
                        btnSetWeight.setToolTipText(SYSTools.xx("nursingrecords.inventory.tx.btnUndoTX.tooltip"));
                        btnSetWeight.addActionListener(actionEvent -> {


                            BigDecimal weight;
                            currentEditor = new DlgYesNo(SYSConst.icon48scales, o -> {
                                if (!SYSTools.catchNull(o).isEmpty()) {
                                    BigDecimal weight1 = (BigDecimal) o;

                                    EntityManager em = OPDE.createEM();
                                    try {
                                        em.getTransaction().begin();
                                        MedStock myStock = em.merge(stock);
                                        final MedStockTransaction myTX = em.merge(tx);
                                        em.lock(myTX, LockModeType.OPTIMISTIC);
                                        myTX.setWeight(weight1);
                                        em.lock(myStock, LockModeType.OPTIMISTIC);
                                        em.lock(myStock.getInventory(), LockModeType.OPTIMISTIC);

                                        em.getTransaction().commit();

                                        createCP4(myStock.getInventory());
                                        buildPanel();
                                    } catch (OptimisticLockException ole) {
                                        OPDE.warn(ole);
                                        if (em.getTransaction().isActive()) {
                                            em.getTransaction().rollback();
                                        }
                                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
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
                                currentEditor = null;
                            }, "nursingrecords.bhp.weight", SYSTools.formatBigDecimal(tx.getWeight()), new Validator<BigDecimal>() {
                                @Override
                                public boolean isValid(String value) {
                                    BigDecimal bd = parse(value);
                                    return bd != null && bd.compareTo(BigDecimal.ZERO) > 0;

                                }

                                @Override
                                public BigDecimal parse(String text) {
                                    return SYSTools.parseDecimal(text);
                                }
                            });
                            currentEditor.setVisible(true);


                        });
                        btnSetWeight.setEnabled(!stock.isClosed() && (tx.getState() == MedStockTransactionTools.STATE_DEBIT || tx.getState() == MedStockTransactionTools.STATE_CREDIT || tx.getState() == MedStockTransactionTools.STATE_EDIT_MANUAL));
                        pnlTitle.getRight().add(btnSetWeight);
                    }

                    pnlTX.add(pnlTitle.getMain());
                }

                return null;
            }

            @Override
            protected void done() {
                OPDE.getDisplayManager().setProgressBarMessage(null);
                OPDE.getMainframe().setBlocked(false);
            }
        };
        worker.execute();


        return pnlTX;
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the PrinterForm Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jspInventory = new JScrollPane();
        cpsInventory = new CollapsiblePanes();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== jspInventory ========
        {

            //======== cpsInventory ========
            {
                cpsInventory.setLayout(new BoxLayout(cpsInventory, BoxLayout.X_AXIS));
            }
            jspInventory.setViewportView(cpsInventory);
        }
        add(jspInventory);
    }// </editor-fold>//GEN-END:initComponents

    private void txtSucheActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSucheActionPerformed

        JXSearchField search = (JXSearchField) evt.getSource();
        if (!search.getText().isEmpty() && search.getText().matches("\\d*")) {
            // numbers only !
            long id = Long.parseLong(search.getText());
            EntityManager em = OPDE.createEM();
            final MedStock stock = em.find(MedStock.class, id);
            em.close();

            if (stock != null) {
                final String key = stock.getInventory().getID() + ".xinventory";
                if (!resident.equals(stock.getInventory().getResident())) {
                    if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.ARCHIVE, "nursingrecords.info")) {
                        switchResident(stock.getInventory().getResident(), stock.getInventory(), o -> {
                            synchronized (mapKey2ClosedToggleButton) {
                                mapKey2ClosedToggleButton.get(key).setSelected(true);
                            }
                        });
                    } else {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.noarchiveaccess"));
                    }

                } else {
                    synchronized (mapKey2ClosedToggleButton) {
//                    CollapsiblePane myCP = cpMap.get(key);

                        if (cpMap.get(key).isCollapsed()) {
                            try {
                                cpMap.get(key).setCollapsed(false);
                            } catch (PropertyVetoException e) {
                                // bah!
                            }
                        }

                    }
                }


            } else {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.notfound"));
            }

        }
    }//GEN-LAST:event_txtSucheActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jspInventory;
    private CollapsiblePanes cpsInventory;
    // End of variables declaration//GEN-END:variables


    private void buildPanel() {
        buildPanel(true);
    }

    private void buildPanel(boolean reloadListInventory) {
        cpsInventory.removeAll();
        cpsInventory.setLayout(new JideBoxLayout(cpsInventory, JideBoxLayout.Y_AXIS));

        synchronized (lstInventories) {
            if (reloadListInventory) {
                lstInventories.clear();
                lstInventories = tbClosedInventory.isSelected() ? MedInventoryTools.getAll(resident) : MedInventoryTools.getAllActive(resident);
            }

            int row = 0;
            for (MedInventory inventory : lstInventories) {

                synchronized (cpMap) {
                    String key = inventory.getID() + ".xinventory";
//                    cpMap.get(key).setBackground(getColor(SYSConst.medium2, row % 2 != 0));
                    cpsInventory.add(cpMap.get(key));
                    cpMap.get(key).getContentPane().revalidate();
                    row++;
                }
            }
        }
//
//        boolean odd = true;
//        for (Component cp : cpsInventory.getComponents()) {
//            colorize(cp, 1, odd);
//            odd = !odd;
//        }

//        }

        cpsInventory.addExpansion();
    }


    private JPanel getMenu(final MedStock stock) {
        final String key = stock.getID() + ".xstock";
        JPanel pnlMenu = new JPanel(new VerticalLayout());
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.DELETE, "nursingrecords.inventory")) {
            /***
             *      ____       _   ____  _             _
             *     |  _ \  ___| | / ___|| |_ ___   ___| | __
             *     | | | |/ _ \ | \___ \| __/ _ \ / __| |/ /
             *     | |_| |  __/ |  ___) | || (_) | (__|   <
             *     |____/ \___|_| |____/ \__\___/ \___|_|\_\
             *
             */
            final JButton btnDelete = GUITools.createHyperlinkButton("nursingrecords.inventory.stock.btndelete.tooltip", SYSConst.icon22delete, null);
            btnDelete.addActionListener(actionEvent -> {
                currentEditor = new DlgYesNo(SYSTools.xx("misc.questions.delete1") + "<br/><b>" + SYSTools.xx("nursingrecords.inventory.search.stockid") + ": " + stock.getID() + "</b>" +
                        "<br/>" + SYSTools.xx("misc.questions.delete2"), SYSConst.icon48delete, answer -> {
                    if (answer.equals(JOptionPane.YES_OPTION)) {
                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();
                            MedStock myStock = em.merge(stock);
                            em.lock(em.merge(myStock.getInventory().getResident()), LockModeType.OPTIMISTIC);
                            em.lock(em.merge(myStock.getInventory()), LockModeType.OPTIMISTIC);

                            em.remove(myStock);
                            em.getTransaction().commit();

                            synchronized (cpMap) {
                                cpMap.remove(key);
                            }
                            createCP4(myStock.getInventory());
                            buildPanel();
                        } catch (OptimisticLockException ole) {
                            OPDE.warn(ole);
                            if (em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }
                            if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
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
                            currentEditor = null;
                        }
                    }
                });
                currentEditor.setVisible(true);

            });
            btnDelete.setEnabled(!stock.isClosed());
            pnlMenu.add(btnDelete);
        }


        /***
         *      ____       _   _____            _            ____        _
         *     / ___|  ___| |_| ____|_  ___ __ (_)_ __ _   _|  _ \  __ _| |_ ___
         *     \___ \ / _ \ __|  _| \ \/ / '_ \| | '__| | | | | | |/ _` | __/ _ \
         *      ___) |  __/ |_| |___ >  <| |_) | | |  | |_| | |_| | (_| | ||  __/
         *     |____/ \___|\__|_____/_/\_\ .__/|_|_|   \__, |____/ \__,_|\__\___|
         *                               |_|           |___/
         */
        final JButton btnExpiry = GUITools.createHyperlinkButton("nursingrecords.inventory.tooltip.btnSetExpiry", SYSConst.icon22gotoEnd, null);
        btnExpiry.addActionListener(actionEvent -> {
            final JidePopup popup = new JidePopup();
            popup.setMovable(false);

            PnlExpiry pnlExpiry = new PnlExpiry(stock.getExpires(), SYSTools.xx("nursingrecords.inventory.pnlExpiry.title") + ": " + stock.getID(), o -> {
                popup.hidePopup();

                EntityManager em = OPDE.createEM();
                try {
                    em.getTransaction().begin();
                    MedStock myStock = em.merge(stock);
                    em.lock(em.merge(myStock.getInventory().getResident()), LockModeType.OPTIMISTIC);
                    em.lock(em.merge(myStock.getInventory()), LockModeType.OPTIMISTIC);
                    myStock.setExpires((Date) o);
                    em.getTransaction().commit();
                    synchronized (cpMap) {
                        cpMap.remove(key);
                    }
                    createCP4(myStock.getInventory());
                    buildPanel();
                } catch (OptimisticLockException ole) {
                    OPDE.warn(ole);
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
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

            });
            popup.setOwner(btnExpiry);
            popup.setContentPane(pnlExpiry);
            popup.removeExcludedComponent(pnlExpiry);
            popup.setDefaultFocusComponent(pnlExpiry);
            GUITools.showPopup(popup, SwingConstants.WEST);
        });
        btnExpiry.setEnabled(!stock.isClosed());
        pnlMenu.add(btnExpiry);

        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, "opde.medication")) {
            /***
             *      _____              _      _____                    _____    _ _ _
             *     |_   _| __ __ _  __| | ___|  ___|__  _ __ _ __ ___ | ____|__| (_) |_ ___  _ __
             *       | || '__/ _` |/ _` |/ _ \ |_ / _ \| '__| '_ ` _ \|  _| / _` | | __/ _ \| '__|
             *       | || | | (_| | (_| |  __/  _| (_) | |  | | | | | | |__| (_| | | || (_) | |
             *       |_||_|  \__,_|\__,_|\___|_|  \___/|_|  |_| |_| |_|_____\__,_|_|\__\___/|_|
             *
             */
            final JButton btnTFEditor = GUITools.createHyperlinkButton("TFEditor.tooltip", SYSConst.icon22medical, null);
            btnTFEditor.addActionListener(actionEvent -> {
                new DlgTradeForm(stock.getTradeForm());
                reload();
            });
            pnlMenu.add(btnTFEditor);

            /***
             *      _   _ ____  ____          _ _ _
             *     | | | |  _ \|  _ \ ___  __| (_) |_ ___  _ __
             *     | | | | |_) | |_) / _ \/ _` | | __/ _ \| '__|
             *     | |_| |  __/|  _ <  __/ (_| | | || (_) | |
             *      \___/|_|   |_| \_\___|\__,_|_|\__\___/|_|
             *
             */
            final JButton btnUPReditor = GUITools.createHyperlinkButton("upreditor.tooltip", SYSConst.icon22calc, null);
            btnUPReditor.addActionListener(actionEvent -> new DlgUPREditor(stock.getTradeForm(), o -> reload()));
            btnUPReditor.setEnabled(stock.getTradeForm().getDosageForm().isUPRn());
            pnlMenu.add(btnUPReditor);
        }

//            if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
//                /***
//                 *       ____ _
//                 *      / ___| |__   __ _ _ __   __ _  ___
//                 *     | |   | '_ \ / _` | '_ \ / _` |/ _ \
//                 *     | |___| | | | (_| | | | | (_| |  __/
//                 *      \____|_| |_|\__,_|_| |_|\__, |\___|
//                 *                              |___/
//                 */
//                final JButton btnChange = GUITools.createHyperlinkButton(internalClassID + ".btnChange.tooltip", SYSConst.icon22playerPlay, null);

//                btnChange.setPanelEnabled(!prescription.isClosed() && !prescription.isOnDemand() && numBHPs != 0);
//                pnlMenu.add(btnChange);


        return pnlMenu;
    }

}
