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
package op.care.med.vorrat;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.prescription.*;
import entity.system.SYSPropsTools;
import op.OPDE;
import op.system.InternalClassACL;
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
    private HashMap<String, JPanel> contentmap;
    //    private HashMap<MedInventory, BigDecimal> invsummap;
//    private HashMap<MedStock, BigDecimal> stocksummap;
    private HashMap<MedStockTransaction, JPanel> linemap;

    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private JToggleButton tbClosedVorrat;

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
        contentmap = new HashMap<String, JPanel>();
//        invsummap = new HashMap<MedInventory, BigDecimal>();
//        stocksummap = new HashMap<MedStock, BigDecimal>();
        linemap = new HashMap<MedStockTransaction, JPanel>();
        prepareSearchArea();
    }

    @Override
    public void switchResident(Resident resident) {
        this.resident = resident;
        OPDE.getDisplayManager().setMainMessage(ResidentTools.getLabelText(resident));
        OPDE.getDisplayManager().clearSubMessages();
        lstInventories = tbClosedVorrat.isSelected() ? MedInventoryTools.getAll(resident) : MedInventoryTools.getAllActive(resident);
        reloadDisplay();
    }

    @Override
    public void cleanup() {
        cpMap.clear();
        contentmap.clear();
        lstInventories.clear();
//        invsummap.clear();
//        stocksummap.clear();
        cpsInventory.removeAll();
        linemap.clear();
    }

    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);

        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout(3));
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

        JXSearchField search = new JXSearchField(OPDE.lang.getString(internalClassID + ".search.stockid"));
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

        tbClosedVorrat = GUITools.getNiceToggleButton(OPDE.lang.getString(internalClassID + ".showclosedinventories"));
        SYSPropsTools.restoreState(internalClassID + ":tbClosedVorrat", tbClosedVorrat);
        tbClosedVorrat.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                SYSPropsTools.storeState(internalClassID + ":tbClosedVorrat", tbClosedVorrat);
                lstInventories = tbClosedVorrat.isSelected() ? MedInventoryTools.getAll(resident) : MedInventoryTools.getAllActive(resident);
                reloadDisplay();
            }
        });
        list.add(tbClosedVorrat);

        return list;
    }

    private java.util.List<Component> addCommands() {
        java.util.List<Component> list = new ArrayList<Component>();
//        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.INSERT)) {
//            JideButton addButton = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID+".newmedstocktx"), SYSConst.icon22add, new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//                    if (pnlBuchungen.hasBestand()) {
//                        pnlBuchungen.getNeueBuchungPopup((JComponent) actionEvent.getSource()).showPopup();
//                    } else {
//                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Wählen Sie zuerst einen Bestand aus", 2));
//                    }
//                }
//            });
//            list.add(addButton);
//        }
//
//        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.MANAGER)) {
//            JideButton resetButton = GUITools.createHyperlinkButton("Alle Buchungen zurücksetzen", SYSConst.icon22undo, new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//                    if (pnlBuchungen.hasBestand()) {
//                        pnlBuchungen.resetBuchungen();
//                    } else {
//                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Wählen Sie zuerst einen Bestand aus", 2));
//                    }
//                }
//            });
//            list.add(resetButton);
//        }

        return list;
    }

//    private void jspBestandComponentResized(ComponentEvent e) {
//        JScrollPane jsp = (JScrollPane) e.getComponent();
//        Dimension dim = jsp.getSize();
//
//        TableColumnModel tcm1 = tblBestand.getColumnModel();
//
//        if (tcm1.getColumnCount() == 0) {
//            return;
//        }
//
//        tcm1.getColumn(TMBestand.COL_NAME).setPreferredWidth(dim.width / 5 * 4);  // 4/5 tel der Gesamtbreite
//        tcm1.getColumn(TMBestand.COL_MENGE).setPreferredWidth(dim.width / 5);  // 1/5 tel der Gesamtbreite
//        tcm1.getColumn(TMBestand.COL_NAME).setHeaderValue("Bestandsangabe");
//        tcm1.getColumn(TMBestand.COL_MENGE).setHeaderValue("Restsumme");
//    }
//
//    private void jspVorratComponentResized(ComponentEvent e) {
//        JScrollPane jsp = (JScrollPane) e.getComponent();
//        Dimension dim = jsp.getSize();
//
//        TableColumnModel tcm1 = tblVorrat.getColumnModel();
//
//        if (tcm1.getColumnCount() == 0) {
//            return;
//        }
//
//        tcm1.getColumn(TMVorraete.COL_NAME).setPreferredWidth(dim.width / 4 * 3);
//        tcm1.getColumn(TMVorraete.COL_MENGE).setPreferredWidth(dim.width / 4);
//        tcm1.getColumn(TMVorraete.COL_NAME).setHeaderValue("Vorratsbezeichnung");
//        tcm1.getColumn(TMVorraete.COL_MENGE).setHeaderValue("Gesamtsumme");
//    }

    @Override
    public void reload() {
        reloadDisplay();
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


        final boolean withworker = true;
        cpsInventory.removeAll();
        cpMap.clear();
        contentmap.clear();
        linemap.clear();

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
                    OPDE.getDisplayManager().setProgressBarMessage(null);
                    OPDE.getMainframe().setBlocked(false);
                }
            };
            worker.execute();

        } else {

            for (MedInventory inventory : lstInventories) {
                createCP4(inventory);
            }
//            if (currentResident != null) {
//                OPDE.getDisplayManager().setMainMessage(ResidentTools.getLabelText(currentResident));
//            } else {
//                OPDE.getDisplayManager().setMainMessage(OPDE.lang.getString(internalClassID));
//            }
            buildPanel();
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
                (inventory.isClosed() ? "<s>" : "") +
                inventory.getText() + "</font></td>" +
                (inventory.isClosed() ? "</s>" : "") +
                "<td width=\"200\" align=\"right\"><font size=+1>" + NumberFormat.getNumberInstance().format(sumInventory) + " " + DosageFormTools.getUsageText(MedInventoryTools.getForm(inventory)) + "</font></td>" +

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


        final JToggleButton tbClosedBestand = GUITools.getNiceToggleButton(null);
        tbClosedBestand.setToolTipText(OPDE.lang.getString(internalClassID + ".showclosedstocks"));
        tbClosedBestand.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cpInventory.setContentPane(createContentPanel4(inventory, tbClosedBestand.isSelected()));

                try {
                    cpInventory.setCollapsed(false);
                } catch (PropertyVetoException e1) {
                    // bah!
                }

            }
        });

        cptitle.getRight().add(tbClosedBestand);

        /***
         *                                 _ _      _            _                 _                      _
         *      _   _ ___  ___ _ __    ___| (_) ___| | _____  __| |   ___  _ __   (_)_ ____   _____ _ __ | |_ ___  _ __ _   _
         *     | | | / __|/ _ \ '__|  / __| | |/ __| |/ / _ \/ _` |  / _ \| '_ \  | | '_ \ \ / / _ \ '_ \| __/ _ \| '__| | | |
         *     | |_| \__ \  __/ |    | (__| | | (__|   <  __/ (_| | | (_) | | | | | | | | \ V /  __/ | | | || (_) | |  | |_| |
         *      \__,_|___/\___|_|     \___|_|_|\___|_|\_\___|\__,_|  \___/|_| |_| |_|_| |_|\_/ \___|_| |_|\__\___/|_|   \__, |
         *                                                                                                              |___/
         */
        cpInventory.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                cpInventory.setContentPane(createContentPanel4(inventory, tbClosedBestand.isSelected()));
            }
        });

        if (!cpInventory.isCollapsed()) {
            cpInventory.setContentPane(createContentPanel4(inventory, tbClosedBestand.isSelected()));
        }

        cpInventory.setHorizontalAlignment(SwingConstants.LEADING);
        cpInventory.setOpaque(false);

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

                "<td width=\"600\" align=\"left\">" + MedStockTools.getCompactHTML(stock) + "</td>" +
                "<td width=\"200\" align=\"right\">" + NumberFormat.getNumberInstance().format(sumStock) + " " + DosageFormTools.getUsageText(MedInventoryTools.getForm(stock.getInventory())) + "</td>" +

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

        if (!stock.getInventory().isClosed()) {
            /***
             *       ___                   ____  _             _
             *      / _ \ _ __   ___ _ __ / ___|| |_ ___   ___| | __
             *     | | | | '_ \ / _ \ '_ \\___ \| __/ _ \ / __| |/ /
             *     | |_| | |_) |  __/ | | |___) | || (_) | (__|   <
             *      \___/| .__/ \___|_| |_|____/ \__\___/ \___|_|\_\
             *           |_|
             */
            final JButton btnOpenStock = new JButton(SYSConst.icon22play);
            btnOpenStock.setPressedIcon(SYSConst.icon22playPressed);
            btnOpenStock.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnOpenStock.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnOpenStock.setContentAreaFilled(false);
            btnOpenStock.setBorder(null);
            btnOpenStock.setToolTipText(OPDE.lang.getString(internalClassID + ".stock.btnopen.tooltip"));
            btnOpenStock.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
//                MedStock openedStock = MedInventoryTools.getCurrentOpened(stock.getInventory());
//                if (openedStock != null && !openedStock.equals(stock)) {
//                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".stock.error.otheropen") + " " + openedStock.getID()));
//                    return;
//                }
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        MedStock myStock = em.merge(stock);
                        em.lock(myStock, LockModeType.OPTIMISTIC);
                        em.lock(em.merge(myStock.getInventory().getResident()), LockModeType.OPTIMISTIC);
                        em.lock(em.merge(myStock.getInventory()), LockModeType.OPTIMISTIC);
                        myStock.setOpened(new Date());
                        myStock.setAPV(MedStockTools.getAPV4(myStock));
                        em.getTransaction().commit();
                        int index = lstInventories.indexOf(myStock.getInventory());
                        lstInventories.get(index).getMedStocks().remove(stock);
                        lstInventories.get(index).getMedStocks().add(myStock);
                        contentmap.remove(key);
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
            MedStock openedStock = MedInventoryTools.getCurrentOpened(stock.getInventory());
            btnOpenStock.setEnabled(stock.isNew() && openedStock == null);
            cptitle.getRight().add(btnOpenStock);

            /***
             *      ____       ____ _                  ____  _             _
             *     |  _ \ ___ / ___| | ___  ___  ___  / ___|| |_ ___   ___| | __
             *     | |_) / _ \ |   | |/ _ \/ __|/ _ \ \___ \| __/ _ \ / __| |/ /
             *     |  _ <  __/ |___| | (_) \__ \  __/  ___) | || (_) | (__|   <
             *     |_| \_\___|\____|_|\___/|___/\___| |____/ \__\___/ \___|_|\_\
             *
             */
            final JButton btnReclose = new JButton(SYSConst.icon22playerStart);
            btnReclose.setPressedIcon(SYSConst.icon22playerStartPressed);
            btnReclose.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnReclose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnReclose.setContentAreaFilled(false);
            btnReclose.setBorder(null);
            btnReclose.setToolTipText(OPDE.lang.getString(internalClassID + ".stock.btnreclose.tooltip"));
            btnReclose.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        MedStock myStock = em.merge(stock);
                        em.lock(myStock, LockModeType.OPTIMISTIC);
                        em.lock(em.merge(myStock.getInventory().getResident()), LockModeType.OPTIMISTIC);
                        em.lock(em.merge(myStock.getInventory()), LockModeType.OPTIMISTIC);
                        myStock.setOut(SYSConst.DATE_BIS_AUF_WEITERES);
                        myStock.setOpened(SYSConst.DATE_BIS_AUF_WEITERES);
                        em.getTransaction().commit();
                        int index = lstInventories.indexOf(myStock.getInventory());
                        lstInventories.get(index).getMedStocks().remove(stock);
                        lstInventories.get(index).getMedStocks().add(myStock);
                        contentmap.remove(key);
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
            btnReclose.setEnabled(stock.isOpened());
            cptitle.getRight().add(btnReclose);

            /***
             *      ____  _             _       ___  _   _ _____
             *     / ___|| |_ ___   ___| | __  / _ \| | | |_   _|
             *     \___ \| __/ _ \ / __| |/ / | | | | | | | | |
             *      ___) | || (_) | (__|   <  | |_| | |_| | | |
             *     |____/ \__\___/ \___|_|\_\  \___/ \___/  |_|
             *
             */
            final JButton btnOut = new JButton(SYSConst.icon22playerStop);
            btnOut.setPressedIcon(SYSConst.icon22playerStopPressed);
            btnOut.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnOut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnOut.setContentAreaFilled(false);
            btnOut.setBorder(null);
            btnOut.setToolTipText(OPDE.lang.getString(internalClassID + ".stock.btnout.tooltip"));
            btnOut.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        MedStock myStock = em.merge(stock);
                        em.lock(myStock, LockModeType.OPTIMISTIC);
                        em.lock(em.merge(myStock.getInventory().getResident()), LockModeType.OPTIMISTIC);
                        em.lock(em.merge(myStock.getInventory()), LockModeType.OPTIMISTIC);
                        MedStockTools.close(em, myStock, "", MedStockTransactionTools.STATE_EDIT_STOCK_CLOSED);
                        em.getTransaction().commit();
                        int index = lstInventories.indexOf(myStock.getInventory());
                        lstInventories.get(index).getMedStocks().remove(stock);
                        lstInventories.get(index).getMedStocks().add(myStock);
                        contentmap.remove(key);
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
            btnOut.setEnabled(stock.isOpened());
            cptitle.getRight().add(btnOut);

            /***
             *      ____  _             _      ___ _   _
             *     / ___|| |_ ___   ___| | __ |_ _| \ | |
             *     \___ \| __/ _ \ / __| |/ /  | ||  \| |
             *      ___) | || (_) | (__|   <   | || |\  |
             *     |____/ \__\___/ \___|_|\_\ |___|_| \_|
             *
             */
            final JButton btnIn = new JButton(SYSConst.icon22redo);
            btnIn.setPressedIcon(SYSConst.icon22redoPressed);
            btnIn.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnIn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnIn.setContentAreaFilled(false);
            btnIn.setBorder(null);
            btnIn.setToolTipText(OPDE.lang.getString(internalClassID + ".stock.btnin.tooltip"));
            btnIn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        MedStock myStock = em.merge(stock);
                        em.lock(myStock, LockModeType.OPTIMISTIC);
                        em.lock(em.merge(myStock.getInventory().getResident()), LockModeType.OPTIMISTIC);
                        em.lock(em.merge(myStock.getInventory()), LockModeType.OPTIMISTIC);
                        myStock.setOut(SYSConst.DATE_BIS_AUF_WEITERES);
                        em.getTransaction().commit();
                        int index = lstInventories.indexOf(myStock.getInventory());
                        lstInventories.get(index).getMedStocks().remove(stock);
                        lstInventories.get(index).getMedStocks().add(myStock);
                        contentmap.remove(key);
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
            btnIn.setEnabled(stock.isClosed() && openedStock == null);
            cptitle.getRight().add(btnIn);


            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.MANAGER)) {
                /***
                 *      ____       _   ____  _             _
                 *     |  _ \  ___| | / ___|| |_ ___   ___| | __
                 *     | | | |/ _ \ | \___ \| __/ _ \ / __| |/ /
                 *     | |_| |  __/ |  ___) | || (_) | (__|   <
                 *     |____/ \___|_| |____/ \__\___/ \___|_|\_\
                 *
                 */
                final JButton btnDelete = new JButton(SYSConst.icon22delete);
                btnDelete.setPressedIcon(SYSConst.icon22deletePressed);
                btnDelete.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnDelete.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnDelete.setContentAreaFilled(false);
                btnDelete.setBorder(null);
                btnDelete.setToolTipText(OPDE.lang.getString(internalClassID + ".stock.btndelete.tooltip"));
                btnDelete.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        new DlgYesNo(OPDE.lang.getString("misc.questions.delete1") + "<br/><b>" + OPDE.lang.getString(internalClassID + ".search.stockid") + ": " + stock.getID() + "</b>" +
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
                                        contentmap.remove(key);
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
                cptitle.getRight().add(btnDelete);
            }
        }
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
        cpStock.revalidate();
        for (Component comp : cpStock.getComponents()) {
            OPDE.debug(comp.getLocation());
        }

        return cpStock;
    }


    private JPanel createContentPanel4(final MedStock stock) {
        final String key = stock.getID() + ".xstock";

        if (!contentmap.containsKey(key)) {

            final JPanel pnlTX = new JPanel(new VerticalLayout());
//            pnlTX.setLayout(new BoxLayout(pnlTX, BoxLayout.PAGE_AXIS));
            pnlTX.setOpaque(false);

            JTextPane txtPane = new JTextPane();
            txtPane.setContentType("text/html");
            txtPane.setEditable(false);
            txtPane.setOpaque(false);
            txtPane.setText(MedStockTools.getASHTML(stock));
            pnlTX.add(txtPane);

            /***
             *         _       _     _ _______  __
             *        / \   __| | __| |_   _\ \/ /
             *       / _ \ / _` |/ _` | | |  \  /
             *      / ___ \ (_| | (_| | | |  /  \
             *     /_/   \_\__,_|\__,_| |_| /_/\_\
             *
             */
            JideButton btnAddTX = GUITools.createHyperlinkButton(internalClassID + ".newmedstocktx", SYSConst.icon22add, new ActionListener() {
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
                                    contentmap.remove(key);
                                    createCP4(myStock.getInventory());

                                    buildPanel();

                                    SwingUtilities.invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            GUITools.scroll2show(jspInventory, linemap.get(myTX).getLocation().y, new Closure() {
                                                @Override
                                                public void execute(Object o) {
                                                    GUITools.flashBackground(linemap.get(myTX), Color.YELLOW, 2);
                                                }
                                            });
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

            BigDecimal rowsum = BigDecimal.ZERO;
            for (final MedStockTransaction tx : stock.getStockTransaction()) {
                rowsum = rowsum.add(tx.getAmount());

                String title = "<html><table border=\"0\">" +
                        "<tr>" +
                        "<td width=\"130\" align=\"left\">" + DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(tx.getPit()) + "</td>" +
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
                        tx.getUser().getUID() +
                        "</td>" +
                        "</tr>" +
                        "</table>" +

                        "</font></html>";

                DefaultCPTitle pnlTitle = new DefaultCPTitle(title, null);


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
                btnDelTX.setToolTipText(OPDE.lang.getString(internalClassID + ".tx.btndelete.tooltip"));
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

                                        contentmap.remove(key);
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

                linemap.put(tx, pnlTitle.getMain());
                pnlTX.add(pnlTitle.getMain());


            }


//            lblBOM.setBackground(getBG(resident, 11));

            contentmap.put(key, pnlTX);
        }

        return contentmap.get(key);
    }


//    private void initDialog() {
//
//        pnlBuchungen = new PnlBuchungen(null, new Closure() {
//            @Override
//            public void execute(Object o) {
//                if (o != null) {
////                    if (o instanceof MedStockTransaction) {
////                        recalculate(((MedStockTransaction) o).getStock());
////                    } else if (o instanceof MedBestand) {
////                        recalculate((MedBestand) o);
////                    } else {
////                        reloadVorratTable();
////                        reloadBestandTable();
////                    }
//
//                    reloadVorratTable();
//                    reloadBestandTable();
//                }
//            }
//        });
//        add(pnlBuchungen, CC.xywh(3, 1, 1, 3));
//
//        prepareSearchArea();
//
//        tblVorrat.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//            @Override
//            public void valueChanged(ListSelectionEvent listSelectionEvent) {
//                if (!listSelectionEvent.getValueIsAdjusting()) {
//                    if (tblVorrat.getSelectedRowCount() > 0) {
//                        MedInventory myInventory = ((TMVorraete) tblVorrat.getModel()).getVorrat(tblVorrat.getSelectedRow());
//                        if (!myInventory.equals(inventory)) {
//                            inventory = myInventory;
//                            reloadBestandTable();
//                        }
//                    }
//                }
//            }
//        });
//        tblBestand.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//            @Override
//            public void valueChanged(ListSelectionEvent listSelectionEvent) {
//                if (!listSelectionEvent.getValueIsAdjusting()) {
//                    if (tblBestand.getSelectedRowCount() > 0) {
//                        MedStock myBestand = ((TMBestand) tblBestand.getModel()).getStock(tblBestand.getSelectedRow());
//                        if (!myBestand.equals(bestand)) {
//                            bestand = myBestand;
//                            pnlBuchungen.setBestand(bestand);
//                        }
//                    }
//
//                }
//            }
//        });
//    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
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

//    private void printBestand() {
////        if (!bwkennung.equals("") ||
////                JOptionPane.showConfirmDialog(this, "Es wurde kein Bewohner ausgewählt.\nMöchten Sie wirklich eine Gesamtliste ausdrucken ?", "Frage", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
////
////            HashMap params = new HashMap();
////            JRDSMedBestand jrds = new JRDSMedBestand(bwkennung);
////
////            SYSPrint.printReport(preview, jrds, params, "medbestand", dialog);
////            if (!preview && !dialog) {
////                JOptionPane.showMessageDialog(this, "Der Druckvorgang ist abgeschlossen.", "Drucker", JOptionPane.INFORMATION_MESSAGE);
////            }
////        }
//    }

    private void txtSucheActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSucheActionPerformed

        JXSearchField search = (JXSearchField) evt.getSource();
        if (!search.getText().isEmpty() && search.getText().matches("\\d*")) {
            // numbers only !
            long id = Long.parseLong(search.getText());
            EntityManager em = OPDE.createEM();
            MedStock stock = em.find(MedStock.class, id);
            em.close();

            if (bestand != null) {
                if (!resident.equals(bestand.getInventory().getResident())) {
                    resident = bestand.getInventory().getResident();
                    OPDE.getDisplayManager().setMainMessage(ResidentTools.getLabelText(resident));
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Medikament gehört eine[m|r] anderen Bewohner[in]. Habe umgeschaltet.", 2));
                    OPDE.getMainframe().change2Bewohner(resident);
                }

//                reloadVorratTable(bestand);

            } else {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Der eingegebene Bestand existiert nicht.", 2));
            }

        }
    }//GEN-LAST:event_txtSucheActionPerformed

//    private void tblBestandMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBestandMousePressed
//        final TMBestand tm = (TMBestand) tblBestand.getModel();
//        if (tm.getRowCount() == 0) {
//            bestand = null;
//            return;
//        }
//
//        Point p = evt.getPoint();
//        final int col = tblBestand.columnAtPoint(p);
//        final int row = tblBestand.rowAtPoint(p);
//
//
//        bestand = tm.getStock(row);
//        pnlBuchungen.setBestand(bestand);
//
//
//        // Menüeinträge
//        if (evt.isPopupTrigger()) {
//
//            ListSelectionModel lsm = tblBestand.getSelectionModel();
//            lsm.setSelectionInterval(row, row);
//
//            SYSTools.unregisterListeners(menuV);
//            menuV = new JPopupMenu();
//
//            // ---------------
//            JMenuItem itemPopupAnbruch = new JMenuItem("Bestand anbrechen", new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_play.png")));
//            itemPopupAnbruch.addActionListener(new java.awt.event.ActionListener() {
//
//                public void actionPerformed(java.awt.event.ActionEvent evt) {
//
//                    EntityManager em = OPDE.createEM();
//                    try {
//                        em.getTransaction().begin();
//                        bestand = em.merge(bestand);
//                        em.lock(bestand, LockModeType.OPTIMISTIC);
//                        BigDecimal apv = MedStockTools.getAPV4(bestand);
//                        MedStockTools.anbrechen(bestand, apv);
//                        em.getTransaction().commit();
//                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Bestand Nr. " + bestand.getID() + " wurde angebrochen", 2));
//                        reloadBestandTable();
//                    } catch (OptimisticLockException ole) {
//                        em.getTransaction().rollback();
//                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Dieser Bestand wurde zwischenzeitlich geändert.", internalClassID));
//                        reloadVorratTable();
//                    } catch (Exception e) {
//                        if (em.getTransaction().isActive()) {
//                            em.getTransaction().rollback();
//                        }
//                        OPDE.fatal(e);
//                    } finally {
//                        em.close();
//                    }
//
//                    reloadBestandTable();
//                }
//            });
//            itemPopupAnbruch.setEnabled(!bestand.isClosed() && !bestand.isOpened());
//            menuV.add(itemPopupAnbruch);
//
//            // ---------------
//            JMenuItem itemPopupVerschließen = new JMenuItem("Bestand wieder verschließen", new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_stop.png")));
//            itemPopupVerschließen.addActionListener(new java.awt.event.ActionListener() {
//
//                public void actionPerformed(java.awt.event.ActionEvent evt) {
//                    EntityManager em = OPDE.createEM();
//                    try {
//                        em.getTransaction().begin();
//                        bestand = em.merge(bestand);
//                        em.lock(bestand, LockModeType.OPTIMISTIC);
//                        bestand.setOut(SYSConst.DATE_BIS_AUF_WEITERES);
//                        bestand.setOpened(SYSConst.DATE_BIS_AUF_WEITERES);
//                        em.getTransaction().commit();
//                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Bestand Nr. " + bestand.getID() + " wurde wieder verschlossen", 2));
//                        reloadBestandTable();
//                    } catch (OptimisticLockException ole) {
//                        em.getTransaction().rollback();
//                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Dieser Bestand wurde zwischenzeitlich geändert.", internalClassID));
//                        reloadVorratTable();
//                    } catch (Exception e) {
//                        if (em.getTransaction().isActive()) {
//                            em.getTransaction().rollback();
//                        }
//                        OPDE.fatal(e);
//                    } finally {
//                        em.close();
//                    }
//                    reloadBestandTable();
//                }
//            });
//            itemPopupVerschließen.setEnabled(!bestand.isClosed() && bestand.isOpened());
//            menuV.add(itemPopupVerschließen);
//
//            // ----------------
//            JMenuItem itemPopupClose = new JMenuItem("Bestand abschließen", new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_end.png")));
//            itemPopupClose.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//                    new DlgYesNo("Möchten Sie den Bestand Nr. " + bestand.getID() + " wirklich abschließen ?", new ImageIcon(getClass().getResource("/artwork/48x48/bw/bottom.png")), new Closure() {
//                        @Override
//                        public void execute(Object answer) {
//                            if (answer.equals(JOptionPane.YES_OPTION)) {
//                                EntityManager em = OPDE.createEM();
//                                try {
//                                    em.getTransaction().begin();
//                                    bestand = em.merge(bestand);
//                                    MedStockTools.close(em, bestand, "", MedStockTransactionTools.STATE_EDIT_MANUAL);
//                                    em.getTransaction().commit();
//                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Bestand Nr. " + bestand.getID() + " wurde abgeschlossen", 2));
//                                    reloadBestandTable();
//                                } catch (OptimisticLockException ole) {
//                                    em.getTransaction().rollback();
//                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Dieser Bestand wurde zwischenzeitlich geändert.", internalClassID));
//                                    reloadVorratTable();
//                                } catch (Exception e) {
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    OPDE.fatal(e);
//                                } finally {
//                                    em.close();
//                                }
//                            }
//                        }
//                    });
//                }
//            });
//
//
//            itemPopupClose.setEnabled(bestand.isOpened());
//            menuV.add(itemPopupClose);
//
//            // ---------------
//            JMenuItem itemPopupEinbuchen = new JMenuItem("Bestand wieder aktivieren", new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_start.png")));
//            itemPopupEinbuchen.addActionListener(new java.awt.event.ActionListener() {
//
//                public void actionPerformed(java.awt.event.ActionEvent evt) {
//
//                    EntityManager em = OPDE.createEM();
//                    try {
//                        em.getTransaction().begin();
//                        bestand = em.merge(bestand);
//                        em.lock(bestand, LockModeType.OPTIMISTIC);
//                        bestand.setOut(SYSConst.DATE_BIS_AUF_WEITERES);
//                        em.getTransaction().commit();
//                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Bestand Nr. " + bestand.getID() + " wurde wieder aktiviert", 2));
//                        reloadBestandTable();
//                    } catch (OptimisticLockException ole) {
//                        em.getTransaction().rollback();
//                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Dieser Bestand wurde zwischenzeitlich geändert.", internalClassID));
//                        reloadVorratTable();
//                    } catch (Exception e) {
//                        if (em.getTransaction().isActive()) {
//                            em.getTransaction().rollback();
//                        }
//                        OPDE.fatal(e);
//                    } finally {
//                        em.close();
//                    }
//                }
//            });
//            itemPopupEinbuchen.setEnabled(bestand.isClosed());
//            menuV.add(itemPopupEinbuchen);
//
//            JMenuItem itemPopupDelete = new JMenuItem("Löschen", new ImageIcon(getClass().getResource("/artwork/22x22/bw/trashcan_empty.png")));
//            itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {
//
//                public void actionPerformed(java.awt.event.ActionEvent evt) {
//
//                    new DlgYesNo("Möchten Sie den Bestand Nr. " + bestand.getID() + " wirklich löschen ?", new ImageIcon(getClass().getResource("/artwork/48x48/bw/trashcan_empty.png")), new Closure() {
//                        @Override
//                        public void execute(Object answer) {
//                            if (answer.equals(JOptionPane.YES_OPTION)) {
//                                EntityManager em = OPDE.createEM();
//                                try {
//                                    em.getTransaction().begin();
//                                    bestand = em.merge(bestand);
//
//                                    MedInventory inventory = em.merge(bestand.getInventory());
//
//                                    em.lock(bestand, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
//                                    em.lock(inventory, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
//
//                                    inventory.getMedStocks().remove(bestand);
//                                    em.remove(bestand);
//
//                                    em.getTransaction().commit();
//                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Bestand Nr. " + bestand.getID() + " und alle zugehörigen Buchungen wurden gelöscht.", 2));
//                                    reloadBestandTable();
//                                } catch (OptimisticLockException ole) {
//                                    em.getTransaction().rollback();
//                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Dieser Bestand wurde zwischenzeitlich geändert.", internalClassID));
//                                    reloadVorratTable();
//                                } catch (Exception e) {
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    OPDE.fatal(e);
//                                } finally {
//                                    em.close();
//                                }
//                            }
//                        }
//                    });
//                }
//            });
//            menuV.add(itemPopupDelete);
//            // ----------------
//            menuV.add(new JSeparator());
//
//            JMenuItem itemPopupPrint = new JMenuItem("Beleg drucken", new ImageIcon(getClass().getResource("/artwork/22x22/bw/printer.png")));
//            itemPopupPrint.addActionListener(new java.awt.event.ActionListener() {
//
//                public void actionPerformed(java.awt.event.ActionEvent evt) {
//                    //SYSPrint.printLabel(mybestid);
//                }
//            });
//            menuV.add(itemPopupPrint);
//
//
//            menuV.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
//        }
//    }//GEN-LAST:event_tblBestandMousePressed
//
//    private void tblVorratMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVorratMousePressed
//        final TMVorraete tm = (TMVorraete) tblVorrat.getModel();
//        if (tm.getRowCount() == 0) {
//            inventory = null;
//            return;
//        }
//
//        Point p = evt.getPoint();
////        final int col = tblVorrat.columnAtPoint(p);
//        final int row = tblVorrat.rowAtPoint(p);
//        ListSelectionModel lsm = tblVorrat.getSelectionModel();
//        lsm.setSelectionInterval(row, row);
//
//        inventory = tm.getVorrat(row);
//        reloadBestandTable();
//
//        if (evt.isPopupTrigger()) {
//            // Menüeinträge
//            SYSTools.unregisterListeners(menuV);
//            menuV = new JPopupMenu();
//
//            // <** TEST 0001
//            JMenuItem itemPopupDelete = new JMenuItem("Vorrat löschen", new ImageIcon(getClass().getResource("/artwork/22x22/bw/trashcan_empty.png")));
//            itemPopupDelete.addActionListener(new java.awt.event.ActionListener() {
//
//                public void actionPerformed(java.awt.event.ActionEvent evt) {
//
//                    new DlgYesNo("Möchten Sie den Vorrat Nr. " + inventory.getID() + " wirklich löschen ?", new ImageIcon(getClass().getResource("/artwork/48x48/bw/trashcan_empty.png")), new Closure() {
//                        @Override
//                        public void execute(Object answer) {
//                            if (answer.equals(JOptionPane.YES_OPTION)) {
//                                OPDE.getDisplayManager().setDBActionMessage(true);
//                                EntityManager em = OPDE.createEM();
//                                try {
//                                    em.getTransaction().begin();
//
//                                    inventory = em.merge(inventory);
//                                    em.lock(inventory, LockModeType.OPTIMISTIC);
//                                    em.remove(inventory);
//
//                                    em.getTransaction().commit();
//                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Vorrat Nr. " + inventory.getID() + " und alle zugehörigen Bestände und Buchungen wurden gelöscht.", 2));
//                                } catch (OptimisticLockException ole) {
//                                    em.getTransaction().rollback();
//                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Dieser Vorrat wurde zwischenzeitlich geändert.", internalClassID));
//                                } catch (Exception e) {
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    OPDE.fatal(e);
//                                } finally {
//                                    em.close();
//                                }
//                                OPDE.getDisplayManager().setDBActionMessage(false);
//                            }
//                        }
//                    });
//                    reloadVorratTable();
//
//                }
//            });
//            menuV.add(itemPopupDelete);
//            // TEST 0001 **>
//
//
//            // <** TEST 0002
//            JMenuItem itemPopupClose = new JMenuItem("Vorrat abschließen und ausbuchen", new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_end.png")));
//            itemPopupClose.addActionListener(new java.awt.event.ActionListener() {
//
//                public void actionPerformed(java.awt.event.ActionEvent evt) {
//                    new DlgYesNo("Möchten Sie den Vorrat Nr. " + inventory.getID() + " wirklich abschließen ?", new ImageIcon(getClass().getResource("/artwork/48x48/bw/player_end.png")), new Closure() {
//                        @Override
//                        public void execute(Object answer) {
//                            if (answer.equals(JOptionPane.YES_OPTION)) {
//                                OPDE.getDisplayManager().setDBActionMessage(true);
//                                EntityManager em = OPDE.createEM();
//                                try {
//                                    em.getTransaction().begin();
//
//                                    inventory = em.merge(inventory);
//                                    em.lock(inventory, LockModeType.OPTIMISTIC);
//
//
//                                    // Alle Bestände close.
//                                    for (MedStock bestand : inventory.getMedStocks()) {
//                                        if (!bestand.isClosed()) {
//                                            bestand = em.merge(bestand);
//                                            em.lock(bestand, LockModeType.OPTIMISTIC);
//                                            MedStockTools.close(em, bestand, "Abschluss des Bestandes bei Vorratsabschluss.", MedStockTransactionTools.STATE_EDIT_INVENTORY_CLOSED);
//                                        }
//                                    }
//
//                                    // Vorrat close
//                                    inventory.setBis(new Date());
//
//                                    em.getTransaction().commit();
//                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Vorrat Nr. " + inventory.getID() + " und alle zugehörigen Bestände abgeschlossen", 2));
//
//                                } catch (OptimisticLockException ole) {
//                                    em.getTransaction().rollback();
//                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Dieser Vorrat wurde zwischenzeitlich geändert", internalClassID));
//                                } catch (Exception e) {
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    OPDE.fatal(e);
//                                } finally {
//                                    em.close();
//                                }
//                                OPDE.getDisplayManager().setDBActionMessage(false);
//                            }
//                        }
//                    });
//                    reloadVorratTable();
//                }
//            });
//            menuV.add(itemPopupClose);
//            // TEST 0002 **>
//
//            menuV.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
//        }
//    }//GEN-LAST:event_tblVorratMousePressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jspInventory;
    private CollapsiblePanes cpsInventory;
    // End of variables declaration//GEN-END:variables


    private void buildPanel() {
        cpsInventory.removeAll();
        cpsInventory.setLayout(new JideBoxLayout(cpsInventory, JideBoxLayout.Y_AXIS));

        for (MedInventory inventory : lstInventories) {
            cpsInventory.add(cpMap.get(inventory.getID() + ".xinventory"));
            cpMap.get(inventory.getID() + ".xinventory").getContentPane().revalidate();
            cpsInventory.revalidate();

        }


        cpsInventory.addExpansion();
    }

}
