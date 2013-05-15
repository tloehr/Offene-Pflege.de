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
package op.care.med.inventory;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import entity.EntityTools;
import entity.info.Resident;
import entity.prescription.*;
import entity.system.SYSPropsTools;
import op.OPDE;
import op.care.info.PnlInfo;
import op.care.med.structure.DlgTradeForm;
import op.care.med.structure.DlgUPREditor;
import op.care.med.structure.PnlMed;
import op.system.InternalClassACL;
import op.system.LogicalPrinter;
import op.system.PrinterForm;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.VerticalLayout;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

/**
 * In OPDE.de gibt es eine Bestandsverwaltung für Medikamente. Bestände werden mit Hilfe von 3 Tabellen
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
    public static final String internalClassID = "nursingrecords.inventory";

    private Resident resident;

    private ArrayList<MedInventory> lstInventories;
    private HashMap<String, CollapsiblePane> cpMap;
    private HashMap<String, JToggleButton> mapKey2ClosedToggleButton;
    //    private HashMap<String, JPanel> contentmap;
    private HashMap<MedStockTransaction, JPanel> linemap;

    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private JToggleButton tbClosedInventory; // <= only for search function
    private Color[] color1, color2;

    /**
     * Creates new form DlgVorrat
     */
    public PnlInventory(Resident resident, JScrollPane jspSearch) {
        super();
        this.jspSearch = jspSearch;
        initComponents();

        initPanel();
        switchResident(resident);
    }

    private void initPanel() {
        cpMap = new HashMap<String, CollapsiblePane>();
//        contentmap = new HashMap<String, JPanel>();
        lstInventories = new ArrayList<MedInventory>();
        mapKey2ClosedToggleButton = new HashMap<String, JToggleButton>();
        color1 = SYSConst.yellow1;
        color2 = SYSConst.greyscale;

        linemap = new HashMap<MedStockTransaction, JPanel>();
        prepareSearchArea();
    }

    @Override
    public void switchResident(Resident resident) {
        switchResident(resident, null, null);
    }

    private void switchResident(Resident res, MedInventory inventory, Closure afterwards) {
        this.resident = EntityTools.find(Resident.class, res.getRID());
        GUITools.setResidentDisplay(resident);
        if (inventory == null) {
            lstInventories = tbClosedInventory.isSelected() ? MedInventoryTools.getAll(resident) : MedInventoryTools.getAllActive(resident);
        } else {
            lstInventories.clear();
            lstInventories.add(inventory);
        }

        reloadDisplay(afterwards);
    }

    @Override
    public void cleanup() {
        SYSTools.clear(cpMap);
//        SYSTools.clear(contentmap);
        SYSTools.clear(lstInventories);
        SYSTools.clear(mapKey2ClosedToggleButton);
        cpsInventory.removeAll();
        SYSTools.clear(linemap);
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

        CollapsiblePane searchPane = new CollapsiblePane(OPDE.lang.getString("nursingrecords.inventory"));
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

    private java.util.List<Component> addFilters() {
        java.util.List<Component> list = new ArrayList<Component>();

        JXSearchField search = new JXSearchField(OPDE.lang.getString("nursingrecords.inventory.search.stockid"));
        search.setFont(new Font("Arial", Font.PLAIN, 14));
        search.setFocusBehavior(org.jdesktop.swingx.prompt.PromptSupport.FocusBehavior.HIGHLIGHT_PROMPT);
        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSucheActionPerformed(e);
            }
        });
        search.setInstantSearchDelay(5000);
        list.add(search);

        tbClosedInventory = GUITools.getNiceToggleButton(OPDE.lang.getString("nursingrecords.inventory.showclosedinventories"));
//        SYSPropsTools.restoreState("nursingrecords.inventory" + ":tbClosedInventory", tbClosedInventory);
        tbClosedInventory.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
//                SYSPropsTools.storeState("nursingrecords.inventory" + ":tbClosedInventory", tbClosedInventory);
                lstInventories = tbClosedInventory.isSelected() ? MedInventoryTools.getAll(resident) : MedInventoryTools.getAllActive(resident);
                reloadDisplay(null);
            }
        });
        list.add(tbClosedInventory);

        return list;
    }

    private java.util.List<Component> addKey() {
        java.util.List<Component> list = new ArrayList<Component>();
        list.add(new JSeparator());
        list.add(new JLabel(OPDE.lang.getString("misc.msg.key")));
        list.add(new JLabel(OPDE.lang.getString("nursingrecords.inventory.keydescription1"), SYSConst.icon22ledGreenOn, SwingConstants.LEADING));
        list.add(new JLabel(OPDE.lang.getString("nursingrecords.inventory.keydescription2"), SYSConst.icon22ledYellowOn, SwingConstants.LEADING));
        list.add(new JLabel(OPDE.lang.getString("nursingrecords.inventory.keydescription3"), SYSConst.icon22ledRedOn, SwingConstants.LEADING));

        return list;
    }

    private java.util.List<Component> addCommands() {
        java.util.List<Component> list = new ArrayList<Component>();

        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.INSERT, "nursingrecords.inventory")) {
            JideButton buchenButton = GUITools.createHyperlinkButton("nursingrecords.inventory.newstocks", SYSConst.icon22addrow, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (!resident.isActive()) {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.cantChangeInactiveResident"));
                        return;
                    }
                    if (!resident.isCalcMediUPR1()) {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.inactiveCalcMed"));
                        return;
                    }
                    new DlgNewStocks(resident);
                    reload();
                }
            });
            list.add(buchenButton);
        }

        return list;
    }

    @Override
    public void reload() {
        lstInventories = tbClosedInventory.isSelected() ? MedInventoryTools.getAll(resident) : MedInventoryTools.getAllActive(resident);
        reloadDisplay(null);
    }

    private void reloadDisplay(final Closure afterwards) {
        /***
         *               _                 _ ____  _           _
         *      _ __ ___| | ___   __ _  __| |  _ \(_)___ _ __ | | __ _ _   _
         *     | '__/ _ \ |/ _ \ / _` |/ _` | | | | / __| '_ \| |/ _` | | | |
         *     | | |  __/ | (_) | (_| | (_| | |_| | \__ \ |_) | | (_| | |_| |
         *     |_|  \___|_|\___/ \__,_|\__,_|____/|_|___/ .__/|_|\__,_|\__, |
         *                                              |_|            |___/
         */
        final boolean withworker = true;
        cpsInventory.removeAll();
        cpMap.clear();
//        contentmap.clear();
        linemap.clear();
        mapKey2ClosedToggleButton.clear();

        if (withworker) {

            OPDE.getMainframe().setBlocked(true);
            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));

            SwingWorker worker = new SwingWorker() {

                @Override
                protected Object doInBackground() throws Exception {
                    int progress = 0;
                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, lstInventories.size()));

                    for (MedInventory inventory : lstInventories) {
                        progress++;
                        createCP4(inventory);
                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, lstInventories.size()));
                    }

                    return null;
                }

                @Override
                protected void done() {
                    buildPanel();
                    if (afterwards != null) {
                        afterwards.execute(null);
                    }
                    OPDE.getDisplayManager().setProgressBarMessage(null);
                    OPDE.getMainframe().setBlocked(false);
                }
            };
            worker.execute();

        } else {

            for (MedInventory inventory : lstInventories) {
                createCP4(inventory);
            }

            buildPanel();
        }

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
        if (!cpMap.containsKey(key)) {
            cpMap.put(key, new CollapsiblePane());
            try {
                cpMap.get(key).setCollapsed(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
        final CollapsiblePane cpInventory = cpMap.get(key);

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
                "<td width=\"200\" align=\"right\"><font size=+1>" + NumberFormat.getNumberInstance().format(sumInventory) + " " + DosageFormTools.getPackageText(MedInventoryTools.getForm(inventory)) + "</font></td>" +

                "</tr>" +
                "</table>" +

                "</html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cpInventory.setCollapsed(!cpInventory.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            }
        });
        cpInventory.setTitleLabelComponent(cptitle.getMain());
        cpInventory.setSlidingDirection(SwingConstants.SOUTH);
        cptitle.getButton().setIcon(inventory.isClosed() ? SYSConst.icon22stopSign : null);


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
            btnCloseInventory.setToolTipText(OPDE.lang.getString("nursingrecords.inventory.btncloseinventory.tooltip"));
            btnCloseInventory.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgYesNo(OPDE.lang.getString("nursingrecords.inventory.question.close1") + "<br/><b>" + inventory.getText() + "</b>" +
                            "<br/>" + OPDE.lang.getString("nursingrecords.inventory.question.close2"), SYSConst.icon48playerStop, new Closure() {
                        @Override
                        public void execute(Object answer) {
                            if (answer.equals(JOptionPane.YES_OPTION)) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();

                                    MedInventory myInventory = em.merge(inventory);
                                    em.lock(myInventory, LockModeType.OPTIMISTIC);
                                    em.lock(myInventory.getResident(), LockModeType.OPTIMISTIC);

                                    // close all stocks
                                    for (MedStock stock : myInventory.getMedStocks()) {
                                        if (!stock.isClosed()) {
                                            MedStock mystock = em.merge(stock);
                                            em.lock(mystock, LockModeType.OPTIMISTIC);
                                            mystock.setNextStock(null);
                                            MedStockTools.close(em, mystock, OPDE.lang.getString("nursingrecords.inventory.stock.msg.inventory_closed"), MedStockTransactionTools.STATE_EDIT_INVENTORY_CLOSED);
                                        }
                                    }
                                    // close inventory
                                    myInventory.setTo(new Date());

                                    em.getTransaction().commit();

                                    lstInventories.remove(inventory);
                                    lstInventories.add(myInventory);
                                    createCP4(myInventory);
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
            btnDelInventory.setToolTipText(OPDE.lang.getString("nursingrecords.inventory.btndelinventory.tooltip"));
            btnDelInventory.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgYesNo(OPDE.lang.getString("nursingrecords.inventory.question.delete1") + "<br/><b>" + inventory.getText() + "</b>" +
                            "<br/>" + OPDE.lang.getString("nursingrecords.inventory.question.delete2"), SYSConst.icon48delete, new Closure() {
                        @Override
                        public void execute(Object answer) {
                            if (answer.equals(JOptionPane.YES_OPTION)) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();

                                    MedInventory myInventory = em.merge(inventory);
                                    em.lock(myInventory, LockModeType.OPTIMISTIC);
                                    em.lock(myInventory.getResident(), LockModeType.OPTIMISTIC);

                                    em.remove(myInventory);

                                    em.getTransaction().commit();

                                    lstInventories.remove(inventory);
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
            cptitle.getRight().add(btnDelInventory);
        }


        final JToggleButton tbClosedStock = GUITools.getNiceToggleButton(null);
        tbClosedStock.setToolTipText(OPDE.lang.getString("nursingrecords.inventory.showclosedstocks"));
        if (!inventory.isClosed()) {
            tbClosedStock.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    cpInventory.setContentPane(createContentPanel4(inventory, tbClosedStock.isSelected()));
                }
            });
        }
        tbClosedStock.setSelected(inventory.isClosed());
        tbClosedStock.setEnabled(!inventory.isClosed());

        mapKey2ClosedToggleButton.put(key, tbClosedStock);

        cptitle.getRight().add(tbClosedStock);


        cpInventory.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                cpInventory.setContentPane(createContentPanel4(inventory, tbClosedStock.isSelected()));
            }
        });

        if (!cpInventory.isCollapsed()) {
            cpInventory.setContentPane(createContentPanel4(inventory, tbClosedStock.isSelected()));
        }

        cpInventory.setHorizontalAlignment(SwingConstants.LEADING);
        cpInventory.setOpaque(false);
        cpInventory.setBackground(getColor(SYSConst.medium2, lstInventories.indexOf(inventory) % 2 != 0));

        return cpInventory;
    }


    private JPanel createContentPanel4(final MedInventory inventory, boolean closed2) {
        final JPanel pnlContent = new JPanel(new VerticalLayout());
        Collections.sort(inventory.getMedStocks());
        for (MedStock stock : inventory.getMedStocks()) {
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
        if (!cpMap.containsKey(key)) {
            cpMap.put(key, new CollapsiblePane());
            try {
                cpMap.get(key).setCollapsed(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
        final CollapsiblePane cpStock = cpMap.get(key);

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
                "<td width=\"200\" align=\"right\">" + NumberFormat.getNumberInstance().format(sumStock) + " " + DosageFormTools.getPackageText(MedInventoryTools.getForm(stock.getInventory())) + "</td>" +
                (stock.isClosed() ? "</s>" : "") +
                "</tr>" +
                "</table>" +


                "</html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cpStock.setCollapsed(!cpStock.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            }
        });

        cpStock.setTitleLabelComponent(cptitle.getMain());
        cpStock.setSlidingDirection(SwingConstants.SOUTH);


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
            btnPrintLabel.setToolTipText(OPDE.lang.getString("nursingrecords.inventory.stock.btnprintlabel.tooltip"));
            btnPrintLabel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    LogicalPrinter logicalPrinter = OPDE.getLogicalPrinters().getMapName2LogicalPrinter().get(OPDE.getProps().getProperty(SYSPropsTools.KEY_LOGICAL_PRINTER));
                    PrinterForm printerForm1 = logicalPrinter.getForms().get(OPDE.getProps().getProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL));
                    OPDE.getPrintProcessor().addPrintJob(new PrintListElement(stock, logicalPrinter, printerForm1, OPDE.getProps().getProperty(SYSPropsTools.KEY_PHYSICAL_PRINTER)));
                }
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
        btnMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JidePopup popup = new JidePopup();
                popup.setMovable(false);
                popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
                popup.setOwner(btnMenu);
                popup.removeExcludedComponent(btnMenu);
                JPanel pnl = getMenu(stock);
                popup.getContentPane().add(pnl);
                popup.setDefaultFocusComponent(pnl);

                GUITools.showPopup(popup, SwingConstants.WEST);
            }
        });
        cptitle.getRight().add(btnMenu);

        /***
         *                                 _ _      _            _                 _                      _
         *      _   _ ___  ___ _ __    ___| (_) ___| | _____  __| |   ___  _ __   (_)_ ____   _____ _ __ | |_ ___  _ __ _   _
         *     | | | / __|/ _ \ '__|  / __| | |/ __| |/ / _ \/ _` |  / _ \| '_ \  | | '_ \ \ / / _ \ '_ \| __/ _ \| '__| | | |
         *     | |_| \__ \  __/ |    | (__| | | (__|   <  __/ (_| | | (_) | | | | | | | | \ V /  __/ | | | || (_) | |  | |_| |
         *      \__,_|___/\___|_|     \___|_|_|\___|_|\_\___|\__,_|  \___/|_| |_| |_|_| |_|\_/ \___|_| |_|\__\___/|_|   \__, |
         *                                                                                                              |___/
         */
        cpStock.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                cpStock.setContentPane(createContentPanel4(stock));
            }
        });

        if (!cpStock.isCollapsed()) {
            JPanel contentPane = createContentPanel4(stock);
            cpStock.setContentPane(contentPane);
        }

        cpStock.setHorizontalAlignment(SwingConstants.LEADING);
        cpStock.setOpaque(false);
        cpStock.setBackground(getColor(SYSConst.light3, lstInventories.indexOf(stock.getInventory()) % 2 != 0));


        return cpStock;
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
            bg.add(red);


            yellow = new JRadioButton(SYSConst.icon22ledYellowOff);
            yellow.setSelectedIcon(SYSConst.icon22ledYellowOn);
            selYellow = stock.isOpened();
            yellow.setSelected(selYellow);
            yellow.setContentAreaFilled(false);
            yellow.setBorderPainted(false);
            yellow.setBorder(null);
            yellow.setOpaque(false);
            bg.add(yellow);

            green = new JRadioButton(SYSConst.icon22ledGreenOff);
            green.setSelectedIcon(SYSConst.icon22ledGreenOn);
            selGreen = stock.isNew();
            green.setSelected(selGreen);
            green.setContentAreaFilled(false);
            green.setBorderPainted(false);
            green.setBorder(null);
            green.setOpaque(false);
            bg.add(green);
            /***
             *                   _
             *      _ __ ___  __| |
             *     | '__/ _ \/ _` |
             *     | | |  __/ (_| |
             *     |_|  \___|\__,_|
             *
             */
            red.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent ie) {
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
                            MedStockTools.close(em, myStock, OPDE.lang.getString("nursingrecords.inventory.stockpanel.STATE_EDIT_STOCK_CLOSED"), MedStockTransactionTools.STATE_EDIT_STOCK_CLOSED);
                            myStock.setState(MedStockTools.STATE_NOTHING);
                            em.getTransaction().commit();
                            int index = lstInventories.indexOf(myStock.getInventory());
                            lstInventories.get(index).getMedStocks().remove(stock);
                            lstInventories.get(index).getMedStocks().add(myStock);
//                            contentmap.remove(key);
                            createCP4(myStock.getInventory());
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

                        selRed = true;
//                        selOrange = false;
                        selYellow = false;
                        selGreen = false;
                    }
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
            yellow.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent ie) {
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
                            int index = lstInventories.indexOf(myStock.getInventory());
                            lstInventories.get(index).getMedStocks().remove(stock);
                            lstInventories.get(index).getMedStocks().add(myStock);

                            createCP4(myStock.getInventory());
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


                        selRed = false;
//                        selOrange = false;
                        selYellow = true;
                        selGreen = false;
                    }
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
            green.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent ie) {
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
                            int index = lstInventories.indexOf(myStock.getInventory());
                            lstInventories.get(index).getMedStocks().remove(stock);
                            lstInventories.get(index).getMedStocks().add(myStock);
                            // contentmap.remove(key);
                            createCP4(myStock.getInventory());
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

                        selRed = false;

                        selYellow = false;
                        selGreen = true;
                    }
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

        pnlTX.setOpaque(false);
        pnlTX.setBackground(getColor(SYSConst.light1, lstInventories.indexOf(stock.getInventory()) % 2 != 0));

        /***
         *         _       _     _ _______  __
         *        / \   __| | __| |_   _\ \/ /
         *       / _ \ / _` |/ _` | | |  \  /
         *      / ___ \ (_| | (_| | | |  /  \
         *     /_/   \_\__,_|\__,_| |_| /_/\_\
         *
         */
        JideButton btnAddTX = GUITools.createHyperlinkButton("nursingrecords.inventory.newmedstocktx", SYSConst.icon22add, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DlgTX(new MedStockTransaction(stock, BigDecimal.ONE, MedStockTransactionTools.STATE_EDIT_MANUAL), new Closure() {
                    @Override
                    public void execute(Object o) {
                        if (o != null) {
                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();

                                final MedStockTransaction myTX = (MedStockTransaction) em.merge(o);
                                MedStock myStock = em.merge(stock);
                                myStock.getStockTransaction().add(myTX);
                                em.lock(myStock, LockModeType.OPTIMISTIC);
                                em.lock(myStock.getInventory(), LockModeType.OPTIMISTIC);
                                em.lock(em.merge(myTX.getStock().getInventory().getResident()), LockModeType.OPTIMISTIC);
                                em.getTransaction().commit();

                                int indexInventory = lstInventories.indexOf(stock.getInventory());
                                int indexStock = lstInventories.get(indexInventory).getMedStocks().indexOf(stock);
                                lstInventories.get(indexInventory).getMedStocks().remove(stock);
                                lstInventories.get(indexInventory).getMedStocks().add(indexStock, myStock);
                                createCP4(myStock.getInventory());

                                buildPanel();

                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (linemap.containsKey(myTX)) {
                                            GUITools.scroll2show(jspInventory, linemap.get(myTX).getLocation().y, new Closure() {
                                                @Override
                                                public void execute(Object o) {
                                                    GUITools.flashBackground(linemap.get(myTX), Color.YELLOW, 2);
                                                }
                                            });
                                        }
                                    }
                                });

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
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));

        SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                int progress = 0;
                OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, stock.getStockTransaction().size()));

                BigDecimal rowsum = MedStockTools.getSum(stock);
                Collections.sort(stock.getStockTransaction());
                for (final MedStockTransaction tx : stock.getStockTransaction()) {
                    progress++;
                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, stock.getStockTransaction().size()));
                    String title = "<html><table border=\"0\">" +
                            "<tr>" +
                            "<td width=\"130\" align=\"left\">" + DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(tx.getPit())
                            + "<br/>[" + tx.getID() + "]"
                            + "</td>" +
                            "<td width=\"200\" align=\"center\">" + SYSTools.catchNull(tx.getText(), "--") + "</td>" +
                            "<td width=\"100\" align=\"right\">" +
                            NumberFormat.getNumberInstance().format(tx.getAmount()) +
                            "</td>" +
                            "<td width=\"100\" align=\"right\">" +
                            (rowsum.compareTo(BigDecimal.ZERO) < 0 ? "<font color=\"red\">" : "") +
                            NumberFormat.getNumberInstance().format(rowsum) +
                            (rowsum.compareTo(BigDecimal.ZERO) < 0 ? "</font>" : "") +
                            "</td>" +
                            "<td width=\"100\" align=\"left\">" +
                            SYSTools.anonymizeUser(tx.getUser().getUID()) +
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
                        btnDelTX.setToolTipText(OPDE.lang.getString("nursingrecords.inventory.tx.btndelete.tooltip"));
                        btnDelTX.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                new DlgYesNo(OPDE.lang.getString("misc.questions.delete1") + "<br/><i>" + DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(tx.getPit()) +
                                        "&nbsp;" + tx.getUser().getUID() + "</i><br/>" + OPDE.lang.getString("misc.questions.delete2"), SYSConst.icon48delete, new Closure() {
                                    @Override
                                    public void execute(Object answer) {
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
                                                myStock.getStockTransaction().remove(myTX);
                                                em.getTransaction().commit();

                                                int indexInventory = lstInventories.indexOf(stock.getInventory());
                                                int indexStock = lstInventories.get(indexInventory).getMedStocks().indexOf(stock);
                                                lstInventories.get(indexInventory).getMedStocks().remove(stock);
                                                lstInventories.get(indexInventory).getMedStocks().add(indexStock, myStock);

                                                linemap.remove(myTX);

                                                createCP4(myStock.getInventory());

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
                    btnUndoTX.setToolTipText(OPDE.lang.getString("nursingrecords.inventory.tx.btnUndoTX.tooltip"));
                    btnUndoTX.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            new DlgYesNo(OPDE.lang.getString("misc.questions.undo1") + "<br/><i>" + DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(tx.getPit()) +
                                    "&nbsp;" + tx.getUser().getUID() + "</i><br/>" + OPDE.lang.getString("misc.questions.undo2"), SYSConst.icon48undo, new Closure() {
                                @Override
                                public void execute(Object answer) {
                                    if (answer.equals(JOptionPane.YES_OPTION)) {
                                        EntityManager em = OPDE.createEM();
                                        try {
                                            em.getTransaction().begin();
                                            MedStock myStock = em.merge(stock);
                                            final MedStockTransaction myOldTX = em.merge(tx);

                                            myOldTX.setState(MedStockTransactionTools.STATE_CANCELLED);
                                            final MedStockTransaction myNewTX = new MedStockTransaction(myStock, myOldTX.getAmount().negate(), MedStockTransactionTools.STATE_CANCEL_REC);
                                            myOldTX.setText(OPDE.lang.getString("misc.msg.reversedBy") + ": " + DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(myNewTX.getPit()));
                                            myNewTX.setText(OPDE.lang.getString("misc.msg.reversalFor") + ": " + DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(myOldTX.getPit()));

                                            myStock.getStockTransaction().add(myNewTX);
                                            myStock.getStockTransaction().remove(tx);
                                            myStock.getStockTransaction().add(myOldTX);

                                            em.lock(em.merge(myNewTX.getStock().getInventory().getResident()), LockModeType.OPTIMISTIC);
                                            em.lock(myStock, LockModeType.OPTIMISTIC);
                                            em.lock(myStock.getInventory(), LockModeType.OPTIMISTIC);

                                            em.getTransaction().commit();

                                            int indexInventory = lstInventories.indexOf(stock.getInventory());
                                            int indexStock = lstInventories.get(indexInventory).getMedStocks().indexOf(stock);
                                            lstInventories.get(indexInventory).getMedStocks().remove(stock);
                                            lstInventories.get(indexInventory).getMedStocks().add(indexStock, myStock);

                                            linemap.remove(tx);
                                            createCP4(myStock.getInventory());
                                            buildPanel();
                                            SwingUtilities.invokeLater(new Runnable() {
                                                @Override
                                                public void run() {
                                                    GUITools.flashBackground(linemap.get(myOldTX), Color.RED, 2);
                                                    GUITools.flashBackground(linemap.get(myNewTX), Color.YELLOW, 2);
                                                }
                                            });
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
                    btnUndoTX.setEnabled(!stock.isClosed() && (tx.getState() == MedStockTransactionTools.STATE_DEBIT || tx.getState() == MedStockTransactionTools.STATE_EDIT_MANUAL));
                    pnlTitle.getRight().add(btnUndoTX);


                    linemap.put(tx, pnlTitle.getMain());
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
                    if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.ARCHIVE, PnlInfo.internalClassID)) {
                        switchResident(stock.getInventory().getResident(), stock.getInventory(), new Closure() {
                            @Override
                            public void execute(Object o) {
                                mapKey2ClosedToggleButton.get(key).setSelected(true);
                            }
                        });
                    } else {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.noarchiveaccess"));
                    }

                } else {

                    CollapsiblePane myCP = cpMap.get(key);

                    if (myCP.isCollapsed()) {
                        try {
                            myCP.setCollapsed(false);
                        } catch (PropertyVetoException e) {
                            // bah!
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
        cpsInventory.removeAll();
        cpsInventory.setLayout(new JideBoxLayout(cpsInventory, JideBoxLayout.Y_AXIS));

//        int i = 0;
        // for the zebra coloring
        for (MedInventory inventory : lstInventories) {
            cpsInventory.add(cpMap.get(inventory.getID() + ".xinventory"));
            cpMap.get(inventory.getID() + ".xinventory").getContentPane().revalidate();
//            cpsInventory.revalidate();
        }

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
            btnDelete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgYesNo(OPDE.lang.getString("misc.questions.delete1") + "<br/><b>" + OPDE.lang.getString("nursingrecords.inventory.search.stockid") + ": " + stock.getID() + "</b>" +
                            "<br/>" + OPDE.lang.getString("misc.questions.delete2"), SYSConst.icon48delete, new Closure() {
                        @Override
                        public void execute(Object answer) {
                            if (answer.equals(JOptionPane.YES_OPTION)) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    MedStock myStock = em.merge(stock);
                                    em.lock(em.merge(myStock.getInventory().getResident()), LockModeType.OPTIMISTIC);
                                    em.lock(em.merge(myStock.getInventory()), LockModeType.OPTIMISTIC);
                                    int index = lstInventories.indexOf(myStock.getInventory());
                                    lstInventories.get(index).getMedStocks().remove(myStock);
                                    em.remove(myStock);
                                    em.getTransaction().commit();

                                    cpMap.remove(key);
                                    createCP4(myStock.getInventory());
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
        btnExpiry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                final JidePopup popup = new JidePopup();
                popup.setMovable(false);

                PnlExpiry pnlExpiry = new PnlExpiry(stock.getExpires(), OPDE.lang.getString("nursingrecords.inventory.pnlExpiry.title") + ": " + stock.getID(), new Closure() {
                    @Override
                    public void execute(Object o) {
                        popup.hidePopup();

                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();
                            MedStock myStock = em.merge(stock);
                            em.lock(em.merge(myStock.getInventory().getResident()), LockModeType.OPTIMISTIC);
                            em.lock(em.merge(myStock.getInventory()), LockModeType.OPTIMISTIC);
                            myStock.setExpires((Date) o);
                            em.getTransaction().commit();

                            cpMap.remove(key);
                            createCP4(myStock.getInventory());
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
                });
                popup.setOwner(btnExpiry);
                popup.setContentPane(pnlExpiry);
                popup.removeExcludedComponent(pnlExpiry);
                popup.setDefaultFocusComponent(pnlExpiry);
                GUITools.showPopup(popup, SwingConstants.WEST);
            }
        });
        btnExpiry.setEnabled(!stock.isClosed());
        pnlMenu.add(btnExpiry);

        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, PnlMed.internalClassID)) {
            /***
             *      _____              _      _____                    _____    _ _ _
             *     |_   _| __ __ _  __| | ___|  ___|__  _ __ _ __ ___ | ____|__| (_) |_ ___  _ __
             *       | || '__/ _` |/ _` |/ _ \ |_ / _ \| '__| '_ ` _ \|  _| / _` | | __/ _ \| '__|
             *       | || | | (_| | (_| |  __/  _| (_) | |  | | | | | | |__| (_| | | || (_) | |
             *       |_||_|  \__,_|\__,_|\___|_|  \___/|_|  |_| |_| |_|_____\__,_|_|\__\___/|_|
             *
             */
            final JButton btnTFEditor = GUITools.createHyperlinkButton("TFEditor.tooltip", SYSConst.icon22medical, null);
            btnTFEditor.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgTradeForm(stock.getTradeForm());
                    reload();
                }
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
            btnUPReditor.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgUPREditor(stock.getTradeForm());
                    reload();
                }
            });
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

//                btnChange.setEnabled(!prescription.isClosed() && !prescription.isOnDemand() && numBHPs != 0);
//                pnlMenu.add(btnChange);


        return pnlMenu;
    }

}
