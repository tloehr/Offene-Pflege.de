/*
 * Created by JFormDesigner on Fri Jun 22 12:26:53 CEST 2012
 */

package op.care.info;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.DefaultOverlayable;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import entity.info.*;
import entity.process.QProcess;
import entity.process.QProcessElement;
import entity.process.SYSINF2PROCESS;
import entity.system.SYSPropsTools;
import op.OPDE;
import op.care.sysfiles.DlgFiles;
import op.process.DlgProcessAssign;
import op.system.InternalClassACL;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
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
import java.text.DateFormat;
import java.util.*;
import java.util.List;

/**
 * @author Torsten Löhr
 */
public class PnlInfo extends NursingRecordsPanel {
    public static final String internalClassID = "nursingrecords.info";

    private final int MAX_HTML_LENGTH = 80;
    private Resident resident;
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;

    private HashMap<String, CollapsiblePane> cpMap;
    private HashMap<String, JPanel> contentmap;
    private HashMap<ResInfoType, ArrayList<ResInfo>> valuecache;
    private HashMap<ResInfo, JPanel> linemap;


    private HashMap<ResInfoType, CollapsiblePane> panelmap;
    private HashMap<ResInfoCategory, List<ResInfoType>> bwinfotypen;
    private HashMap<ResInfo, JToggleButton> bwinfo4html;
    //    private HashMap<ResInfoType, List<ResInfo>> bwinfos;
    private List<ResInfoCategory> categories;

    private JToggleButton tbEmpty, tbInactive;
    private JideButton btnBWDied, btnBWMovedOut, btnBWisAway, btnBWisBack;
    private Color[] color1, color2;

    private boolean initPhase;

    public PnlInfo(Resident resident, JScrollPane jspSearch) {
        initPhase = true;
        this.jspSearch = jspSearch;
        this.resident = resident;
        initComponents();
        initPanel();
        switchResident(resident);
        initPhase = false;
    }

    private void initPanel() {
        prepareSearchArea();
        bwinfotypen = new HashMap<ResInfoCategory, List<ResInfoType>>();
//        bwinfos = new HashMap<ResInfoType, List<ResInfo>>();
        bwinfo4html = new HashMap<ResInfo, JToggleButton>();
        panelmap = new HashMap<ResInfoType, CollapsiblePane>();
        valuecache = new HashMap<ResInfoType, ArrayList<ResInfo>>();
        contentmap = new HashMap<String, JPanel>();
        cpMap = new HashMap<String, CollapsiblePane>();

        linemap = new HashMap<ResInfo, JPanel>();

        categories = ResInfoCategoryTools.getAll();

        color1 = SYSConst.green1;
        color2 = SYSConst.greyscale;
    }

    @Override
    public void switchResident(Resident bewohner) {
        this.resident = bewohner;
        GUITools.setBWDisplay(bewohner);
        reloadDisplay();
    }

//    private void setAllViewButtonsOff(ResInfoCategory category) {
//        initPhase = true;
//        for (ResInfoType type : bwinfotypen.get(category)) {
//            if (bwinfos.containsKey(type)) {
//                for (ResInfo info : bwinfos.get(type)) {
//                    if (bwinfo4html.containsKey(info)) {
//                        bwinfo4html.get(info).setSelected(false);
//                    }
//                }
//            }
//        }
//        initPhase = false;
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

        final boolean withworker = false;
        if (withworker) {


//            OPDE.getMainframe().setBlocked(true);
//            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));
//
////            lblWait.setText(OPDE.lang.getString("misc.msg.wait"));
////            ((CardLayout) pnlCard.getLayout()).show(pnlCard, "cardWait");
//
//            tabKat.removeAll();
//            bwinfos.clear();
//            panelmap.clear();
//
//            SwingWorker worker = new SwingWorker() {
//                TableModel model;
//
//                @Override
//                protected Object doInBackground() throws Exception {
//                    try {
//                        int progress = 0;
//
//                        // Eliminate empty categories
//                        categories = new ArrayList<ResInfoCategory>();
//                        for (final ResInfoCategory kat : ResInfoCategoryTools.getAll()) {
//                            if (!ResInfoTypeTools.getByCat(kat).isEmpty()) {
//                                categories.add(kat);
//                            }
//                        }
//
//                        // create tabs
//                        for (final ResInfoCategory kat : categories) {
//                            progress++;
//                            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, categories.size()));
//
//                            if (!ResInfoTypeTools.getByCat(kat).isEmpty()) {
//                                tabKat.addTab(kat.getBezeichnung(), new JScrollPane(createCollapsiblePanesFor(kat)));
//                            } else {
//                                categories.remove(kat);
//                            }
//                        }
//                    } catch (Exception e) {
//                        OPDE.fatal(e);
//                    }
//                    return null;
//                }
//
//                @Override
//                protected void done() {
//                    txtHTML.setText(null);
//                    tabKat.setSelectedIndex(SYSPropsTools.getInteger(internalClassID + ":tabKatSelectedIndex"));
//                    refreshDisplay();
//                    btnBWDied.setEnabled(resident.isActive());
//                    btnBWMovedOut.setEnabled(resident.isActive());
//                    btnBWisAway.setEnabled(resident.isActive() && !ResInfoTools.isAway(resident));
//                    btnBWisBack.setEnabled(resident.isActive() && ResInfoTools.isAway(resident));
//                    initPhase = false;
//                    OPDE.getDisplayManager().setProgressBarMessage(null);
//                    OPDE.getMainframe().setBlocked(false);
//                }
//            };
//            worker.execute();

        } else {

            contentmap.clear();
            cpMap.clear();
            cpsInfo.removeAll();
            linemap.clear();
            valuecache.clear();

            ArrayList<ResInfo> listInfo = ResInfoTools.getAll(resident);
            for (ResInfo resInfo : listInfo) {
                if (!valuecache.containsKey(resInfo.getResInfoType())) {
                    valuecache.put(resInfo.getResInfoType(), new ArrayList<ResInfo>());
                }
                valuecache.get(resInfo.getResInfoType()).add(resInfo);
            }

            for (ResInfoCategory cat : categories) {
                createCP4Cat(cat);
            }
            buildPanel();
        }
        initPhase = false;

    }

    private CollapsiblePane createCP4Cat(final ResInfoCategory cat) {
        /***
         *                          _        ____ ____  _  _      ____      _
         *       ___ _ __ ___  __ _| |_ ___ / ___|  _ \| || |    / ___|__ _| |_ ___  __ _  ___  _ __ _   _
         *      / __| '__/ _ \/ _` | __/ _ \ |   | |_) | || |_  | |   / _` | __/ _ \/ _` |/ _ \| '__| | | |
         *     | (__| | |  __/ (_| | ||  __/ |___|  __/|__   _| | |__| (_| | ||  __/ (_| | (_) | |  | |_| |
         *      \___|_|  \___|\__,_|\__\___|\____|_|      |_|    \____\__,_|\__\___|\__, |\___/|_|   \__, |
         *                                                                          |___/            |___/
         */
        final String keyCat = cat.getID() + ".xcategory";
        if (!cpMap.containsKey(keyCat)) {
            cpMap.put(keyCat, new CollapsiblePane());
            try {
                cpMap.get(keyCat).setCollapsed(true);
            } catch (PropertyVetoException e) {
                // Bah!
            }

        }

        final CollapsiblePane cpCat = cpMap.get(keyCat);


        String title = "<html><font size=+1><b>" +
                cat.getBezeichnung() +
                "</b></font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cpCat.setCollapsed(!cpCat.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            }
        });

        cpCat.setTitleLabelComponent(cptitle.getMain());
        cpCat.setSlidingDirection(SwingConstants.SOUTH);
        cpCat.setBackground(getColor(cat)[SYSConst.medium3]);
        cpCat.setOpaque(true);
        cpCat.setHorizontalAlignment(SwingConstants.LEADING);

        cpCat.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                JPanel pnlContent = new JPanel(new VerticalLayout());
                for (ResInfoType type : ResInfoTypeTools.getByCat(cat)) {
                    pnlContent.add(createCP4Type(type));
                }
                cpCat.setContentPane(pnlContent);
            }
        });
//        cpYear.setBackground(getColor(vtype, SYSConst.light4));

//        if (!cpCat.isCollapsed()) {
//            JPanel pnlContent = new JPanel(new VerticalLayout());
//
//            for (DateMidnight month = end; month.compareTo(start) >= 0; month = month.minusMonths(1)) {
//                pnlContent.add(createCP4Month(month));
//            }
//
//            cpCat.setContentPane(pnlContent);
//            cpCat.setOpaque(false);
//        }

        return cpCat;
    }

//    private Color getColor(ResInfoCategory category, int level) {
//
//        if (categories.indexOf(category) % 2 == 0) {
//            return demandColors[level];
//        } else {
//            return regularColors[level];
//        }
//    }

    private CollapsiblePane createCP4Info(final ResInfo resInfo) {
        final String keyType = resInfo.getID() + ".xinfo";
        if (!cpMap.containsKey(keyType)) {
            cpMap.put(keyType, new CollapsiblePane());
            try {
                cpMap.get(keyType).setCollapsed(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
        final CollapsiblePane cpInfo = cpMap.get(keyType);

        String title = "<html><table border=\"0\">" +
                "<tr valign=\"top\">" +
                "<td width=\"280\" align=\"left\">" + resInfo.getPITAsHTML() + "</td>" +
                "<td width=\"500\" align=\"left\">" +

                (resInfo.isClosed() ? "<s>" : "") +
//                SYSTools.getHTMLSubstring(resInfo.getHtml(), MAX_HTML_LENGTH) +
                resInfo.getHtml() +
                (resInfo.isClosed() ? "</s>" : "") +
                "</td>" +
                "</table>" +
                "</html>";
        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cpInfo.setCollapsed(!cpInfo.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            }
        });
//        cptitle.getButton().setIcon(getIcon(resInfo.getResInfoType()));


        /***
         *      __  __
         *     |  \/  | ___ _ __  _   _
         *     | |\/| |/ _ \ '_ \| | | |
         *     | |  | |  __/ | | | |_| |
         *     |_|  |_|\___|_| |_|\__,_|
         *
         */
        final JButton btnMenu = new JButton(SYSConst.icon22menu);
        btnMenu.setPressedIcon(SYSConst.icon22edit3Pressed);
        btnMenu.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnMenu.setAlignmentY(Component.TOP_ALIGNMENT);
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
                JPanel pnl = getMenu(resInfo);
                popup.getContentPane().add(pnl);
                popup.setDefaultFocusComponent(pnl);

                GUITools.showPopup(popup, SwingConstants.WEST);
            }
        });
//        btnMenu.setEnabled(!resInfo.isC);
        cptitle.getRight().add(btnMenu);

        cpInfo.setCollapsible(!resInfo.getText().isEmpty());
        cpInfo.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
//                cpInfo.setContentPane();
            }
        });

        if (!cpInfo.isCollapsed()) {
//            cpInfo.setContentPane();
        }

        cpInfo.setTitleLabelComponent(cptitle.getMain());
        cpInfo.setSlidingDirection(SwingConstants.SOUTH);
        cpInfo.setBackground(getColor(resInfo.getResInfoType().getCategory())[SYSConst.light3]);
        cpInfo.setOpaque(true);
        cpInfo.setHorizontalAlignment(SwingConstants.LEADING);
        return cpInfo;
    }

//    private JPanel createContentPanel4Type(final ResInfoType type) {
//
////        if (!contentmap.containsKey(key)) {
//
//        JPanel pnlMonth = new JPanel(new VerticalLayout());
//
//        pnlMonth.setOpaque(false);
//
//        DateMidnight now = new DateMidnight();
//
//        boolean sameMonth = now.dayOfMonth().withMaximumValue().equals(month.dayOfMonth().withMaximumValue());
//
//        final DateMidnight start = sameMonth ? now : month.dayOfMonth().withMaximumValue();
//        final DateMidnight end = month.dayOfMonth().withMinimumValue();
//
//        for (DateMidnight week = start; end.compareTo(week) <= 0; week = week.minusWeeks(1)) {
//            pnlMonth.add(createCP4Week(week));
//        }
//
//        return pnlMonth;
//    }

//    private CollapsiblePanes createCollapsiblePanesFor(ResInfoCategory category) {
//        CollapsiblePanes cpane = new CollapsiblePanes();
//        cpane.setLayout(new JideBoxLayout(cpane, JideBoxLayout.Y_AXIS));
//
//        bwinfotypen.put(category, ResInfoTypeTools.getByCat(category));
//
//        for (ResInfoType type : bwinfotypen.get(category)) {
//            bwinfos.put(type, ResInfoTools.getByResidentAndType(resident, type));
//            CollapsiblePane panel = createPanelFor(type);
//            cpane.add(panel);
//            panel.setVisible((tbEmpty.isSelected() || !bwinfos.get(type).isEmpty()) && (tbInactive.isSelected() || bwinfos.get(type).isEmpty() || !bwinfos.get(type).get(0).isClosed()));
//            panelmap.put(type, panel);
//        }
//        cpane.addExpansion();
//
//        return cpane;
//    }

    private JPanel createContentPanel4Type(final ResInfoType type) {
        final JPanel infoPanel = new JPanel(new VerticalLayout());
        infoPanel.setOpaque(false);

        if (!valuecache.containsKey(type)) {
            valuecache.put(type, ResInfoTools.getByResidentAndType(resident, type));
        }

        int i = 0; // for zebra pattern
        for (final ResInfo resInfo : valuecache.get(type)) {
            CollapsiblePane cp = createCP4Info(resInfo);
            if (i % 2 == 0) {
                cp.setBackground(Color.WHITE);
            }
            i++;
            infoPanel.add(cp);
        }
        return infoPanel;
    }


    private CollapsiblePane createCP4Type(final ResInfoType type) {
        /***
         *                          _        ____ ____  _  _     _____
         *       ___ _ __ ___  __ _| |_ ___ / ___|  _ \| || |   |_   _|   _ _ __   ___
         *      / __| '__/ _ \/ _` | __/ _ \ |   | |_) | || |_    | || | | | '_ \ / _ \
         *     | (__| | |  __/ (_| | ||  __/ |___|  __/|__   _|   | || |_| | |_) |  __/
         *      \___|_|  \___|\__,_|\__\___|\____|_|      |_|     |_| \__, | .__/ \___|
         *                                                            |___/|_|
         */
        final String keyType = type.getID() + ".xtype";
        if (!cpMap.containsKey(keyType)) {
            cpMap.put(keyType, new CollapsiblePane());
            try {
                cpMap.get(keyType).setCollapsed(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
        final CollapsiblePane cpType = cpMap.get(keyType);

        String title = "<html><font size=+1>" +
                type.getBWInfoKurz() +
                "</font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cpType.setCollapsed(!cpType.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            }
        });

        final JButton btnAdd = new JButton(SYSConst.icon22add);
        btnAdd.setPressedIcon(SYSConst.icon22addPressed);
        btnAdd.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnAdd.setAlignmentY(Component.TOP_ALIGNMENT);
        btnAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAdd.setContentAreaFilled(false);
        btnAdd.setBorder(null);
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        btnAdd.setEnabled(!valuecache.containsKey(type) || valuecache.get(type).isEmpty());
        cptitle.getRight().add(btnAdd);

        final JButton btnPrint = new JButton(SYSConst.icon22print2);
        btnPrint.setPressedIcon(SYSConst.icon22print2Pressed);
        btnPrint.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnPrint.setAlignmentY(Component.TOP_ALIGNMENT);
        btnPrint.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnPrint.setContentAreaFilled(false);
        btnPrint.setBorder(null);
        btnPrint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        cptitle.getRight().add(btnPrint);

        cpType.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                cpType.setContentPane(createContentPanel4Type(type));
            }
        });

        cpType.setCollapsible(valuecache.containsKey(type) && !valuecache.get(type).isEmpty());

//        if (!cpType.isCollapsed()) {
//            cpType.setContentPane(createContentPanel4Type(type));
//        }

        cpType.setTitleLabelComponent(cptitle.getMain());
        cpType.setSlidingDirection(SwingConstants.SOUTH);
        cpType.setBackground(getColor(type.getCategory())[SYSConst.light4]);
        cpType.setOpaque(true);
        cpType.setHorizontalAlignment(SwingConstants.LEADING);

        return cpType;
    }


    private Color[] getColor(ResInfoCategory cat) {
        if (categories.indexOf(cat) % 2 == 0) {
            return color1;
        } else {
            return color2;
        }
    }

    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);

        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout(5));
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

    private List<Component> addCommands() {

        List<Component> list = new ArrayList<Component>();

//        JPanel mypanel = new JPanel();
//        mypanel.setLayout(new VerticalLayout());
//        mypanel.setBackground(Color.WHITE);
//
//        CollapsiblePane searchPane = new CollapsiblePane(); //OPDE.lang.getString(internalClassID)
//        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
//        searchPane.setCollapsible(false);
//
//        try {
//            searchPane.setCollapsed(false);
//        } catch (PropertyVetoException e) {
//            OPDE.error(e);
//        }

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.MANAGER)) {

            /***
             *      _       ____                 _
             *     (_)___  |  _ \  ___  __ _  __| |
             *     | / __| | | | |/ _ \/ _` |/ _` |
             *     | \__ \ | |_| |  __/ (_| | (_| |
             *     |_|___/ |____/ \___|\__,_|\__,_|
             *
             */
            btnBWMovedOut = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".resident.movedout"), SYSConst.icon22residentGone, null);
            btnBWDied = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".resident.died"), SYSConst.icon22residentDied, null);
            btnBWisAway = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".resident.isaway"), SYSConst.icon22residentAbsent, null);
            btnBWisBack = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".resident.isback"), SYSConst.icon22residentBack, null);
            btnBWDied.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgUhrzeitDatum(OPDE.lang.getString(internalClassID + ".dlg.dateofdeath"), new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {
                                Date dod = (Date) o;
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                                    resident.setStation(null);
                                    ResInfo hauf = em.merge(ResInfoTools.getLastBWInfo(resident, ResInfoTypeTools.getByID(ResInfoTypeTools.TYP_HEIMAUFNAHME)));
                                    em.lock(hauf, LockModeType.OPTIMISTIC);

                                    hauf.setTo(dod);
                                    hauf.setUserOFF(em.merge(OPDE.getLogin().getUser()));

                                    Properties props = ResInfoTools.getContent(hauf);
                                    props.setProperty("hauf", "verstorben");
                                    ResInfoTools.setContent(hauf, props);

                                    ResidentTools.endOfStay(em, em.merge(resident), dod);
                                    em.getTransaction().commit();
                                    btnBWDied.setEnabled(false);
                                    btnBWMovedOut.setEnabled(false);
                                    btnBWisAway.setEnabled(false);
                                    btnBWisBack.setEnabled(false);
//                                    reloadDisplay();
//                                    GUITools.setBWDisplay(resident);
                                    OPDE.getMainframe().emptyFrame();
                                    OPDE.getMainframe().afterLogin();
                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".msg.isdeadnow"), 5));
                                } catch (OptimisticLockException ole) {
                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
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
            btnBWDied.setEnabled(resident.isActive());
            list.add(btnBWDied);
            /***
             *                                   _               _
             *      _ __ ___   _____   _____  __| |   ___  _   _| |_
             *     | '_ ` _ \ / _ \ \ / / _ \/ _` |  / _ \| | | | __|
             *     | | | | | | (_) \ V /  __/ (_| | | (_) | |_| | |_
             *     |_| |_| |_|\___/ \_/ \___|\__,_|  \___/ \__,_|\__|
             *
             */
            btnBWMovedOut.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgUhrzeitDatum(OPDE.lang.getString(internalClassID + ".dlg.dateofmoveout"), new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {
                                Date dod = (Date) o;
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                                    resident.setStation(null);
                                    ResInfo hauf = em.merge(ResInfoTools.getLastBWInfo(resident, ResInfoTypeTools.getByID(ResInfoTypeTools.TYP_HEIMAUFNAHME)));
                                    em.lock(hauf, LockModeType.OPTIMISTIC);
                                    hauf.setTo(dod);
                                    hauf.setUserOFF(em.merge(OPDE.getLogin().getUser()));

                                    Properties props = ResInfoTools.getContent(hauf);
                                    props.setProperty("hauf", "verstorben");
                                    ResInfoTools.setContent(hauf, props);

                                    ResidentTools.endOfStay(em, em.merge(resident), dod);
                                    em.getTransaction().commit();
                                    btnBWDied.setEnabled(false);
                                    btnBWMovedOut.setEnabled(false);
                                    btnBWisAway.setEnabled(false);
                                    btnBWisBack.setEnabled(false);
//                                    reloadDisplay();
//                                    GUITools.setBWDisplay(resident);
                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".msg.hasgonenow"), 5));
                                    OPDE.getMainframe().emptyFrame();
                                    OPDE.getMainframe().afterLogin();
                                } catch (OptimisticLockException ole) {
                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                    reload();
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
            btnBWMovedOut.setEnabled(resident.isActive());
            list.add(btnBWMovedOut);
            /***
             *      _          _
             *     (_)___     / \__      ____ _ _   _
             *     | / __|   / _ \ \ /\ / / _` | | | |
             *     | \__ \  / ___ \ V  V / (_| | |_| |
             *     |_|___/ /_/   \_\_/\_/ \__,_|\__, |
             *                                  |___/
             */
            btnBWisAway.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    final JidePopup popup = new JidePopup();
                    final ResInfo abwesenheit = new ResInfo(ResInfoTypeTools.getByID(ResInfoTypeTools.TYP_ABWESENHEIT), resident);
                    PnlAbwesend pnlAbwesend = new PnlAbwesend(abwesenheit, new Closure() {
                        @Override
                        public void execute(Object o) {
                            popup.hidePopup();
                            if (o != null) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                                    em.merge(o);
                                    em.getTransaction().commit();
                                    btnBWisAway.setEnabled(false);
                                    btnBWisBack.setEnabled(true);
//                                    refreshTabKat(abwesenheit.getResInfoType().getCategory());
                                    GUITools.setBWDisplay(resident);
                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".msg.isawaynow")));
                                } catch (OptimisticLockException ole) {
                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                    reload();
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
                    popup.setMovable(false);
                    popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));

                    popup.setOwner(btnBWisAway);
                    popup.removeExcludedComponent(btnBWisAway);
                    popup.getContentPane().add(pnlAbwesend);
                    popup.setDefaultFocusComponent(pnlAbwesend);
                    GUITools.showPopup(popup, SwingConstants.NORTH_EAST);
                }
            });
            btnBWisAway.setEnabled(resident.isActive() && !ResInfoTools.isAway(resident));
            list.add(btnBWisAway);
            /***
             *      _       ____             _
             *     (_)___  | __ )  __ _  ___| | __
             *     | / __| |  _ \ / _` |/ __| |/ /
             *     | \__ \ | |_) | (_| | (__|   <
             *     |_|___/ |____/ \__,_|\___|_|\_\
             *
             */

            btnBWisBack.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        em.lock(em.merge(resident), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                        ResInfo lastabsence = em.merge(ResInfoTools.getLastBWInfo(resident, ResInfoTypeTools.getByID(ResInfoTypeTools.TYP_ABWESENHEIT)));
                        em.lock(lastabsence, LockModeType.OPTIMISTIC);
                        lastabsence.setTo(new Date());
                        lastabsence.setUserOFF(em.merge(OPDE.getLogin().getUser()));
                        em.getTransaction().commit();
                        btnBWisAway.setEnabled(true);
                        btnBWisBack.setEnabled(false);
//                        refreshTabKat(lastabsence.getResInfoType().getCategory());
                        GUITools.setBWDisplay(resident);
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".msg.isbacknow")));
                    } catch (OptimisticLockException ole) {
                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        reload();
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
            btnBWisBack.setEnabled(resident.isActive() && ResInfoTools.isAway(resident));
            list.add(btnBWisBack);
        }

//        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.PRINT)) { // => ACL_MATRIX
//            JideButton printButton = GUITools.createHyperlinkButton(OPDE.lang.getString("misc.commands.print"), new ImageIcon(getClass().getResource("/artwork/22x22/bw/printer.png")), new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//                    SYSFilesTools.print("<h1 id=\"fonth1\" >" + OPDE.lang.getString(internalClassID) + ": " + BewohnerTools.getLabelText(resident) + "</h1>" + getHTML(), true);
//                }
//            });
//            mypanel.add(printButton);
//        }


        return list;
    }

    private List<Component> addFilters() {
        List<Component> list = new ArrayList<Component>();
//        JPanel labelPanel = new JPanel();
//        labelPanel.setBackground(Color.WHITE);
//        labelPanel.setLayout(new VerticalLayout(5));
//
//        CollapsiblePane panelFilter = new CollapsiblePane(); // OPDE.lang.getString("misc.msg.Filter")
//        panelFilter.setStyle(CollapsiblePane.PLAIN_STYLE);
//        panelFilter.setCollapsible(false);

        tbEmpty = GUITools.getNiceToggleButton(OPDE.lang.getString(internalClassID + ".empty"));
        tbEmpty.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (initPhase) return;
                SYSPropsTools.storeState(internalClassID + ":tbEmpty", tbEmpty);
                reloadDisplay();
            }
        });
        tbEmpty.setHorizontalAlignment(SwingConstants.LEFT);
        list.add(tbEmpty);
        SYSPropsTools.restoreState(internalClassID + ":tbEmpty", tbEmpty);


        tbInactive = GUITools.getNiceToggleButton(OPDE.lang.getString(internalClassID + ".inactive"));
        tbInactive.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (initPhase) return;
                SYSPropsTools.storeState(internalClassID + ":tbInactive", tbInactive);
                reloadDisplay();
            }
        });
        tbInactive.setHorizontalAlignment(SwingConstants.LEFT);
        list.add(tbInactive);
        SYSPropsTools.restoreState(internalClassID + ":tbInactive", tbInactive);

//        panelFilter.setContentPane(labelPanel);

        return list;
    }

//    private String getResInfoText(ResInfo resInfo) {
//        String result = "";
//        DateFormat df = resInfo.isSingleIncident() ? DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT) : DateFormat.getDateInstance();
//
//        if (resInfo.isClosed() || resInfo.isNoConstraints() || resInfo.isSingleIncident()) {
//            result += df.format()
//        } else {
//            result += DateFormat.getDateInstance().format(ersterResInfo.getVon()) + " &rarr;| ";
//            result += "<b>" + type.getBWInfoKurz() + "</b>: ";
//            result += SYSTools.getHTMLSubstring(ersterResInfo.getHtml(), MAX_HTML_LENGTH);
//        }
//
//        result += "</font>";
//
//        return SYSTools.toHTMLForScreen(result);
//    }
//
//    private String getHyperlinkButtonTextForPanelContent(ResInfo bwinfo) {
//        String result = "";
//
//        result += df.format(bwinfo.getVon()) + (bwinfo.isSingleIncident() ? " " : " &rarr;" + (bwinfo.getTo().equals(SYSConst.DATE_BIS_AUF_WEITERES) ? "|" : " " + DateFormat.getDateInstance().format(bwinfo.getTo())));
//        result += ": ";
//        result += SYSTools.getHTMLSubstring(bwinfo.getHtml(), MAX_HTML_LENGTH);
//
//        return SYSTools.toHTMLForScreen(result);
//    }

//    private Icon getIcon(ResInfoType type) {
//        if (type.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_SINGLE_INCIDENTS) {
//            return SYSConst.icon16pit;
//        }
//        if (type.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_BYDAY) {
//            return SYSConst.icon16byday;
//        }
//        return SYSConst.icon16bysecond;
//    }

    private String getUserInfoAsHTML(ResInfo bwinfo) {
        String html = "</br><div id=\"fonttext\">";
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT);
        if (bwinfo.getResInfoType().getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_BYDAY) {
            df = DateFormat.getDateInstance();
        }
        html += df.format(bwinfo.getVon()) + (bwinfo.isSingleIncident() ? " " : " &rarr; ") + bwinfo.getUserON().getFullname();
        html += "</div>";
        return html;
    }


//    private CollapsiblePane createPanelFor(final ResInfoType type) {
//
//        /***
//         *      _   _ _____    _    ____  _____ ____
//         *     | | | | ____|  / \  |  _ \| ____|  _ \
//         *     | |_| |  _|   / _ \ | | | |  _| | |_) |
//         *     |  _  | |___ / ___ \| |_| | |___|  _ <
//         *     |_| |_|_____/_/   \_\____/|_____|_| \_\
//         *
//         */
//        final CollapsiblePane panelForBWInfoTyp = new CollapsiblePane();
//        try {
//
//            final ResInfo ersterResInfo = bwinfos.get(type).isEmpty() ? null : bwinfos.get(type).get(0);
//            final boolean shallBeCollapsible = !bwinfos.get(type).isEmpty() && (bwinfos.get(type).size() > 1 || ersterResInfo.isClosed() || type.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_NOCONSTRAINTS || type.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_SINGLE_INCIDENTS);
//
//            JPanel titlePanel = new JPanel();
//            titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.LINE_AXIS));
//
//            JPanel titlePanelleft = new JPanel();
//            titlePanelleft.setLayout(new BoxLayout(titlePanelleft, BoxLayout.LINE_AXIS));
//
//            /***
//             *      _     _       _    _           _   _                _   _                _
//             *     | |   (_)_ __ | | _| |__  _   _| |_| |_ ___  _ __   | | | | ___  __ _  __| | ___ _ __
//             *     | |   | | '_ \| |/ / '_ \| | | | __| __/ _ \| '_ \  | |_| |/ _ \/ _` |/ _` |/ _ \ '__|
//             *     | |___| | | | |   <| |_) | |_| | |_| || (_) | | | | |  _  |  __/ (_| | (_| |  __/ |
//             *     |_____|_|_| |_|_|\_\_.__/ \__,_|\__|\__\___/|_| |_| |_| |_|\___|\__,_|\__,_|\___|_|
//             *
//             */
//            JideButton title = GUITools.createHyperlinkButton(getHyperlinkButtonTextForPanelHead(type), getIcon(type), null);
//            title.addMouseListener(GUITools.getHyperlinkStyleMouseAdapter());
//            title.setAlignmentX(Component.LEFT_ALIGNMENT);
//            title.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//                    if (ersterResInfo != null && !ersterResInfo.isClosed() && !ersterResInfo.isNoConstraints() && !ersterResInfo.isSingleIncident()) {
//                        setAllViewButtonsOff(type.getCategory());
//                        txtHTML.setText(SYSTools.toHTML(ResInfoTools.getHTML(ersterResInfo)));
//                    } else {
//                        txtHTML.setText("<html>&nbsp;</html>");
//                        try {
//                            panelForBWInfoTyp.setCollapsed(false);
//                        } catch (PropertyVetoException e) {
//                            OPDE.error(e);
//                        }
//                    }
//                }
//            });
//            titlePanelleft.add(title);
//
//
//            JPanel titlePanelright = new JPanel();
//            titlePanelright.setLayout(new BoxLayout(titlePanelright, BoxLayout.LINE_AXIS));
//
//            /***
//             *      ____        _   _               __     ___                 _   _                _
//             *     | __ ) _   _| |_| |_ ___  _ __   \ \   / (_) _____      __ | | | | ___  __ _  __| | ___ _ __
//             *     |  _ \| | | | __| __/ _ \| '_ \   \ \ / /| |/ _ \ \ /\ / / | |_| |/ _ \/ _` |/ _` |/ _ \ '__|
//             *     | |_) | |_| | |_| || (_) | | | |   \ V / | |  __/\ V  V /  |  _  |  __/ (_| | (_| |  __/ |
//             *     |____/ \__,_|\__|\__\___/|_| |_|    \_/  |_|\___| \_/\_/   |_| |_|\___|\__,_|\__,_|\___|_|
//             *
//             */
//            if (ersterResInfo != null) {
//                final JToggleButton btnView = new JToggleButton(SYSConst.icon22view);
//                btnView.setSelectedIcon(SYSConst.icon22viewPressed);
//                btnView.setAlignmentX(Component.RIGHT_ALIGNMENT);
//                btnView.setContentAreaFilled(false);
//                btnView.setBorder(null);
//                btnView.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//
//                bwinfo4html.put(ersterResInfo, btnView);
//                btnView.addItemListener(new ItemListener() {
//                    @Override
//                    public void itemStateChanged(ItemEvent itemEvent) {
//                        if (initPhase) return;
//                        // Pressing the VIEW button on a header causes all members in that list to show themself, too.
//                        if (!panelForBWInfoTyp.isCollapsed()) {
//                            for (ResInfo info : bwinfos.get(type)) {
//                                if (bwinfo4html.containsKey(info)) {
//                                    bwinfo4html.get(info).setSelected(btnView.isSelected());
//                                }
//                            }
//                        }
//                        String html = getHTML();
//                        txtHTML.setText(html.isEmpty() ? "<html>&nbsp;</html>" : SYSTools.toHTMLForScreen(html));
//                    }
//                });
////                btnView.setEnabled(ersterResInfo == null || !ersterResInfo.isHeimaufnahme());
//                titlePanelright.add(btnView);
//            }
//
//            /***
//             *      ____        _   _                   _       _     _
//             *     | __ ) _   _| |_| |_ ___  _ __      / \   __| | __| |
//             *     |  _ \| | | | __| __/ _ \| '_ \    / _ \ / _` |/ _` |
//             *     | |_) | |_| | |_| || (_) | | | |  / ___ \ (_| | (_| |
//             *     |____/ \__,_|\__|\__\___/|_| |_| /_/   \_\__,_|\__,_|
//             *
//             */
//            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.INSERT)) { // => ACL_MATRIX
//                JButton btnAdd = new JButton(SYSConst.icon22add);
//                btnAdd.setPressedIcon(SYSConst.icon22addPressed);
//                btnAdd.setAlignmentX(Component.RIGHT_ALIGNMENT);
//                btnAdd.setContentAreaFilled(false);
//                btnAdd.setBorder(null);
//                btnAdd.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent actionEvent) {
//                        final ResInfo mybwinfo;
//                        if (ersterResInfo == null || type.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_SINGLE_INCIDENTS || ersterResInfo.isClosed()) {
//                            mybwinfo = new ResInfo(type, resident);
//                        } else {
//                            mybwinfo = ersterResInfo.clone();
//                            mybwinfo.setFrom(new Date());
//                            mybwinfo.setTo(SYSConst.DATE_BIS_AUF_WEITERES);
//                        }
//
//                        Closure closure = new Closure() {
//                            @Override
//                            public void execute(Object o) {
//                                if (o != null) {
//                                    EntityManager em = OPDE.createEM();
//                                    try {
//                                        em.getTransaction().begin();
//                                        em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
//                                        ResInfo newinfo = em.merge((ResInfo) o);
//                                        em.lock(newinfo, LockModeType.OPTIMISTIC);
//                                        if (type.getIntervalMode() != ResInfoTypeTools.MODE_INTERVAL_SINGLE_INCIDENTS && ersterResInfo != null) {
//                                            ResInfo myersterbwinfo = em.merge(ersterResInfo);
//                                            em.lock(myersterbwinfo, LockModeType.OPTIMISTIC);
//                                            myersterbwinfo.setTo(new DateTime(newinfo.getVon()).minusSeconds(1).toDate());
//                                            myersterbwinfo.setUserOFF(newinfo.getUserON());
//                                        }
//                                        newinfo.setHtml(ResInfoTools.getContentAsHTML(newinfo));
//                                        newinfo = em.merge(newinfo);
//                                        em.getTransaction().commit();
//                                        refreshTabKat(newinfo.getResInfoType().getCategory());
//                                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.anewentryhasbeenadded")));
//                                    } catch (OptimisticLockException ole) {
//                                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                        if (em.getTransaction().isActive()) {
//                                            em.getTransaction().rollback();
//                                        }
//                                        reload();
//                                    } catch (Exception e) {
//                                        if (em.getTransaction().isActive()) {
//                                            em.getTransaction().rollback();
//                                        }
//                                        OPDE.fatal(e);
//                                    } finally {
//                                        em.close();
//                                    }
//                                } else {
//                                    OPDE.getDisplayManager().clearSubMessages();
//                                }
//                            }
//                        };
//
//                        if (type.getID().equalsIgnoreCase(ResInfoTypeTools.TYP_DIAGNOSE)) {
//                            new DlgDiag(mybwinfo, closure);
//                        } else {
//                            new DlgInfo(mybwinfo, closure);
//                        }
//                    }
//                });
//                titlePanelright.add(btnAdd);
//                btnAdd.setEnabled(ersterResInfo == null || (!ersterResInfo.isHeimaufnahme() && !ersterResInfo.getResInfoType().isObsolete()));
//            }
//
//            /***
//             *      ____        _   _                _____    _ _ _
//             *     | __ ) _   _| |_| |_ ___  _ __   | ____|__| (_) |_
//             *     |  _ \| | | | __| __/ _ \| '_ \  |  _| / _` | | __|
//             *     | |_) | |_| | |_| || (_) | | | | | |__| (_| | | |_
//             *     |____/ \__,_|\__|\__\___/|_| |_| |_____\__,_|_|\__|
//             *
//             */
//            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE)) {  // => ACL_MATRIX
//                JButton btnEdit = new JButton(SYSConst.icon22edit);
//                btnEdit.setPressedIcon(SYSConst.icon22editPressed);
//                btnEdit.setAlignmentX(Component.RIGHT_ALIGNMENT);
//                btnEdit.setContentAreaFilled(false);
//                btnEdit.setBorder(null);
//                btnEdit.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent actionEvent) {
//                        new DlgInfo(ersterResInfo, new Closure() {
//                            @Override
//                            public void execute(Object o) {
//                                if (o != null) {
//                                    EntityManager em = OPDE.createEM();
//                                    try {
//                                        em.getTransaction().begin();
//                                        ResInfo newinfo = em.merge((ResInfo) o);
//                                        em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
//                                        em.lock(newinfo, LockModeType.OPTIMISTIC);
//                                        newinfo.setHtml(ResInfoTools.getContentAsHTML(newinfo));
//                                        newinfo.setUserON(em.merge(OPDE.getLogin().getUser()));
//                                        em.getTransaction().commit();
//                                        refreshTabKat(type.getCategory());
//                                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.thisentryhasbeenedited")));
//                                    } catch (OptimisticLockException ole) {
//                                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                        if (em.getTransaction().isActive()) {
//                                            em.getTransaction().rollback();
//                                        }
//                                        reload();
//                                    } catch (Exception e) {
//                                        if (em.getTransaction().isActive()) {
//                                            em.getTransaction().rollback();
//                                        }
//                                        OPDE.fatal(e);
//                                    } finally {
//                                        em.close();
//                                    }
//                                }
//                            }
//                        });
//                    }
//                });
//                boolean isManager = OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.MANAGER);
//                boolean mayBeEdited = ersterResInfo != null && !ersterResInfo.isClosed() && !ersterResInfo.isHeimaufnahme() && !ersterResInfo.getResInfoType().isObsolete() && (OPDE.isAdmin() || isManager || ersterResInfo.getUserON().equals(OPDE.getLogin().getUser()));
//                btnEdit.setEnabled(mayBeEdited);
//            }
//
//            /***
//             *      ____        _   _                ____  _
//             *     | __ ) _   _| |_| |_ ___  _ __   / ___|| |_ ___  _ __
//             *     |  _ \| | | | __| __/ _ \| '_ \  \___ \| __/ _ \| '_ \
//             *     | |_) | |_| | |_| || (_) | | | |  ___) | || (_) | |_) |
//             *     |____/ \__,_|\__|\__\___/|_| |_| |____/ \__\___/| .__/
//             *                                                     |_|
//             */
//            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.CANCEL)) { // => ACL_MATRIX
//                JButton btnStop = new JButton(SYSConst.icon22stop);
//                btnStop.setPressedIcon(SYSConst.icon22stopPressed);
//                btnStop.setAlignmentX(Component.RIGHT_ALIGNMENT);
//                btnStop.setContentAreaFilled(false);
//                btnStop.setBorder(null);
//                btnStop.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent actionEvent) {
//                        new DlgYesNo(OPDE.lang.getString("misc.questions.cancel") + "<br/>" + ersterResInfo.getResInfoType().getBWInfoKurz() + "<br/>" + ersterResInfo.getHtml(), SYSConst.icon48stop, new Closure() {
//                            @Override
//                            public void execute(Object answer) {
//                                if (answer.equals(JOptionPane.YES_OPTION)) {
//                                    EntityManager em = OPDE.createEM();
//                                    try {
//                                        em.getTransaction().begin();
//                                        ResInfo newinfo = em.merge(ersterResInfo);
//                                        em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
//                                        em.lock(newinfo, LockModeType.OPTIMISTIC);
//                                        newinfo.setTo(new Date());
//                                        newinfo.setUserOFF(em.merge(OPDE.getLogin().getUser()));
//                                        em.getTransaction().commit();
//                                        refreshTabKat(type.getCategory());
//                                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.thisentryhasbeencancelled")));
//                                    } catch (OptimisticLockException ole) {
//                                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                        if (em.getTransaction().isActive()) {
//                                            em.getTransaction().rollback();
//                                        }
//                                        reload();
//                                    } catch (Exception e) {
//                                        if (em.getTransaction().isActive()) {
//                                            em.getTransaction().rollback();
//                                        }
//                                        OPDE.fatal(e);
//                                    } finally {
//                                        em.close();
//                                    }
//                                }
//                            }
//                        });
//                    }
//                });
//                btnStop.setEnabled(ersterResInfo != null && !ersterResInfo.isClosed() && !ersterResInfo.isHeimaufnahme() && !ersterResInfo.isNoConstraints() && !ersterResInfo.isSingleIncident());
//                titlePanelright.add(btnStop);
//            }
//
//            /***
//             *      ____        _   _                ____       _      _
//             *     | __ ) _   _| |_| |_ ___  _ __   |  _ \  ___| | ___| |_ ___
//             *     |  _ \| | | | __| __/ _ \| '_ \  | | | |/ _ \ |/ _ \ __/ _ \
//             *     | |_) | |_| | |_| || (_) | | | | | |_| |  __/ |  __/ ||  __/
//             *     |____/ \__,_|\__|\__\___/|_| |_| |____/ \___|_|\___|\__\___|
//             *
//             */
//            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.DELETE)) {  // => ACL_MATRIX
//                JButton btnDelete = new JButton(SYSConst.icon22delete);
//                btnDelete.setPressedIcon(SYSConst.icon22deletePressed);
//                btnDelete.setAlignmentX(Component.RIGHT_ALIGNMENT);
//                btnDelete.setContentAreaFilled(false);
//                btnDelete.setBorder(null);
//                btnDelete.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent actionEvent) {
//                        new DlgYesNo(OPDE.lang.getString("misc.questions.delete") + "<br/>" + ersterResInfo.getResInfoType().getBWInfoKurz() + "<br/>" + ersterResInfo.getHtml(), SYSConst.icon48delete, new Closure() {
//                            @Override
//                            public void execute(Object answer) {
//                                if (answer.equals(JOptionPane.YES_OPTION)) {
//                                    EntityManager em = OPDE.createEM();
//                                    try {
//                                        em.getTransaction().begin();
//                                        em.merge(ersterResInfo);
//                                        em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
//                                        em.lock(ersterResInfo, LockModeType.OPTIMISTIC);
//                                        em.remove(ersterResInfo);
//                                        em.getTransaction().commit();
//                                        refreshTabKat(type.getCategory());
//                                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.thisentryhasbeendeleted")));
//                                    } catch (OptimisticLockException ole) {
//                                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                        if (em.getTransaction().isActive()) {
//                                            em.getTransaction().rollback();
//                                        }
//                                        reload();
//                                    } catch (Exception e) {
//                                        if (em.getTransaction().isActive()) {
//                                            em.getTransaction().rollback();
//                                        }
//                                        OPDE.fatal(e);
//                                    } finally {
//                                        em.close();
//                                    }
//                                }
//                            }
//                        });
//                    }
//                });
//                btnDelete.setEnabled(ersterResInfo != null && !ersterResInfo.isHeimaufnahme() && !ersterResInfo.isSingleIncident() && !ersterResInfo.isNoConstraints());
//                titlePanelright.add(btnDelete);
//            }
//
//            /***
//             *      ____        _   _                _____ _ _         _   _   _             _       _   _                _
//             *     | __ ) _   _| |_| |_ ___  _ __   |  ___(_) | ___   / \ | |_| |_ __ _  ___| |__   | | | | ___  __ _  __| | ___ _ __
//             *     |  _ \| | | | __| __/ _ \| '_ \  | |_  | | |/ _ \ / _ \| __| __/ _` |/ __| '_ \  | |_| |/ _ \/ _` |/ _` |/ _ \ '__|
//             *     | |_) | |_| | |_| || (_) | | | | |  _| | | |  __// ___ \ |_| || (_| | (__| | | | |  _  |  __/ (_| | (_| |  __/ |
//             *     |____/ \__,_|\__|\__\___/|_| |_| |_|   |_|_|\___/_/   \_\__|\__\__,_|\___|_| |_| |_| |_|\___|\__,_|\__,_|\___|_|
//             *
//             */
//            if (OPDE.getAppInfo().userHasAccessLevelForThisClass(PnlFiles.internalClassID, InternalClassACL.INSERT)) { // => ACL_MATRIX
//                JButton btnAttach = new JButton(SYSConst.icon22attach);
//                btnAttach.setPressedIcon(SYSConst.icon22attachPressed);
//                btnAttach.setAlignmentX(Component.RIGHT_ALIGNMENT);
//                btnAttach.setContentAreaFilled(false);
//                btnAttach.setBorder(null);
//                btnAttach.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent actionEvent) {
//                        new DlgFiles(ersterResInfo, new Closure() {
//                            @Override
//                            public void execute(Object o) {
//                                refreshTabKat(type.getCategory());
//                            }
//                        });
//                    }
//                });
//                btnAttach.setEnabled(ersterResInfo != null && !ersterResInfo.isSingleIncident() && !ersterResInfo.isNoConstraints() && !ersterResInfo.isClosed() && !ersterResInfo.isHeimaufnahme());
//
//                if (ersterResInfo != null && !ersterResInfo.isClosed() && !ersterResInfo.isSingleIncident() && !ersterResInfo.isNoConstraints() && ersterResInfo.getAttachedFilesConnections().size() > 0) {
//                    JLabel lblNum = new JLabel(Integer.toString(ersterResInfo.getAttachedFilesConnections().size()), SYSConst.icon16redStar, SwingConstants.CENTER);
//                    lblNum.setFont(SYSConst.ARIAL10BOLD);
//                    lblNum.setForeground(Color.YELLOW);
//                    lblNum.setHorizontalTextPosition(SwingConstants.CENTER);
//                    DefaultOverlayable overlayableBtn = new DefaultOverlayable(btnAttach, lblNum, DefaultOverlayable.SOUTH_EAST);
//                    overlayableBtn.setOpaque(false);
//                    overlayableBtn.setOverlayVisible(ersterResInfo != null);
//                    titlePanelright.add(overlayableBtn);
//                } else {
//                    titlePanelright.add(btnAttach);
//                }
//            }
//
//            titlePanelleft.setOpaque(false);
//            titlePanelright.setOpaque(false);
//            titlePanel.setOpaque(false);
//
//            titlePanel.add(titlePanelleft);
//            titlePanel.add(titlePanelright);
//
//            panelForBWInfoTyp.setTitleLabelComponent(titlePanel);
//            panelForBWInfoTyp.setSlidingDirection(SwingConstants.SOUTH);
//            panelForBWInfoTyp.setStyle(CollapsiblePane.TREE_STYLE);
//            panelForBWInfoTyp.setHorizontalAlignment(SwingConstants.LEADING);
//
//            panelForBWInfoTyp.setEmphasized(bwinfos.get(type).isEmpty());
//
//            JPanel labelPanel = new JPanel();
//            labelPanel.setLayout(new VerticalLayout());
//
//            /***
//             *       ___ ___  _  _ _____ ___ _  _ _____
//             *      / __/ _ \| \| |_   _| __| \| |_   _|
//             *     | (_| (_) | .` | | | | _|| .` | | |
//             *      \___\___/|_|\_| |_| |___|_|\_| |_|
//             *
//             */
//            if (!bwinfos.get(type).isEmpty()) {
//                // In diesen Fällen steht im Kopf kein ResInfo Eintrag. Daher müssen die alle hier rein geschrieben werden.
//                int startwert = ersterResInfo.isClosed() || ersterResInfo.isNoConstraints() || ersterResInfo.isSingleIncident() ? 0 : 1;
//
//                for (int infonum = startwert; infonum < bwinfos.get(type).size(); infonum++) {
//                    final ResInfo innerResInfo = bwinfos.get(type).get(infonum);
//
//                    final JideButton contentButton = GUITools.createHyperlinkButton(getHyperlinkButtonTextForPanelContent(innerResInfo), null, null);
//                    contentButton.setAlignmentX(Component.LEFT_ALIGNMENT);
//                    contentButton.addMouseListener(GUITools.getHyperlinkStyleMouseAdapter());
//                    contentButton.addActionListener(new ActionListener() {
//                        @Override
//                        public void actionPerformed(ActionEvent actionEvent) {
//                            setAllViewButtonsOff(type.getCategory());
//                            txtHTML.setText(SYSTools.toHTML(ResInfoTools.getHTML(innerResInfo)));
//                        }
//                    });
//
//
//                    JPanel contentLine = new JPanel();
//                    contentLine.setLayout(new BoxLayout(contentLine, BoxLayout.LINE_AXIS));
//
//                    JPanel contentLineRight = new JPanel();
//                    contentLineRight.setLayout(new BoxLayout(contentLineRight, BoxLayout.LINE_AXIS));
//
//                    /***
//                     *      ____        _   _               __     ___                  ____            _             _
//                     *     | __ ) _   _| |_| |_ ___  _ __   \ \   / (_) _____      __  / ___|___  _ __ | |_ ___ _ __ | |_
//                     *     |  _ \| | | | __| __/ _ \| '_ \   \ \ / /| |/ _ \ \ /\ / / | |   / _ \| '_ \| __/ _ \ '_ \| __|
//                     *     | |_) | |_| | |_| || (_) | | | |   \ V / | |  __/\ V  V /  | |__| (_) | | | | ||  __/ | | | |_
//                     *     |____/ \__,_|\__|\__\___/|_| |_|    \_/  |_|\___| \_/\_/    \____\___/|_| |_|\__\___|_| |_|\__|
//                     *
//                     */
//                    JToggleButton btnView = new JToggleButton(SYSConst.icon22view);
//                    btnView.setSelectedIcon(SYSConst.icon22viewPressed);
//                    btnView.setAlignmentX(Component.RIGHT_ALIGNMENT);
//                    btnView.setContentAreaFilled(false);
//                    btnView.setBorder(null);
//
//                    bwinfo4html.put(innerResInfo, btnView);
//                    btnView.addItemListener(new ItemListener() {
//                        @Override
//                        public void itemStateChanged(ItemEvent itemEvent) {
//                            if (initPhase) return;
//                            String html = getHTML();
//                            txtHTML.setText(html.isEmpty() ? "<html>&nbsp;</html>" : SYSTools.toHTML(html));
//                        }
//                    });
//                    contentLineRight.add(btnView);
//
//                    /***
//                     *      ____        _   _              ____  _                 ____            _             _
//                     *     | __ ) _   _| |_| |_ ___  _ __ / ___|| |_ ___  _ __    / ___|___  _ __ | |_ ___ _ __ | |_
//                     *     |  _ \| | | | __| __/ _ \| '_ \\___ \| __/ _ \| '_ \  | |   / _ \| '_ \| __/ _ \ '_ \| __|
//                     *     | |_) | |_| | |_| || (_) | | | |___) | || (_) | |_) | | |__| (_) | | | | ||  __/ | | | |_
//                     *     |____/ \__,_|\__|\__\___/|_| |_|____/ \__\___/| .__/   \____\___/|_| |_|\__\___|_| |_|\__|
//                     *                                                   |_|
//                     */
//                    if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.CANCEL)) { // => ACL_MATRIX
//                        JButton btnStop = new JButton(SYSConst.icon22stop);
//                        btnStop.setPressedIcon(SYSConst.icon22stopPressed);
//                        btnStop.setAlignmentX(Component.RIGHT_ALIGNMENT);
//                        btnStop.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//                        btnStop.setContentAreaFilled(false);
//                        btnStop.setBorder(null);
//                        btnStop.addActionListener(new ActionListener() {
//                            @Override
//                            public void actionPerformed(ActionEvent actionEvent) {
//                                new DlgYesNo(OPDE.lang.getString("misc.questions.cancel") + "<br/>" + innerResInfo.getResInfoType().getBWInfoKurz() + "<br/>" + innerResInfo.getHtml(), SYSConst.icon48stop, new Closure() {
//                                    @Override
//                                    public void execute(Object answer) {
//                                        if (answer.equals(JOptionPane.YES_OPTION)) {
//                                            EntityManager em = OPDE.createEM();
//                                            try {
//                                                em.getTransaction().begin();
//                                                ResInfo newinfo = em.merge(innerResInfo);
//                                                em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
//                                                em.lock(newinfo, LockModeType.OPTIMISTIC);
//                                                newinfo.setTo(new Date());
//                                                newinfo.setUserOFF(em.merge(OPDE.getLogin().getUser()));
//                                                em.getTransaction().commit();
//                                                refreshTabKat(type.getCategory());
//                                                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.thisentryhasbeencancelled")));
//                                            } catch (OptimisticLockException ole) {
//                                                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                                if (em.getTransaction().isActive()) {
//                                                    em.getTransaction().rollback();
//                                                }
//                                                reload();
//                                            } catch (Exception e) {
//                                                if (em.getTransaction().isActive()) {
//                                                    em.getTransaction().rollback();
//                                                }
//                                                OPDE.fatal(e);
//                                            } finally {
//                                                em.close();
//                                            }
//                                        }
//                                    }
//                                });
//                            }
//                        });
//                        btnStop.setEnabled(!innerResInfo.isClosed() && innerResInfo.isNoConstraints());
//                        contentLineRight.add(btnStop);
//                    }
//
//                    /***
//                     *      ____        _   _                 ____ _                              ____           _           _
//                     *     | __ ) _   _| |_| |_ ___  _ __    / ___| |__   __ _ _ __   __ _  ___  |  _ \ ___ _ __(_) ___   __| |
//                     *     |  _ \| | | | __| __/ _ \| '_ \  | |   | '_ \ / _` | '_ \ / _` |/ _ \ | |_) / _ \ '__| |/ _ \ / _` |
//                     *     | |_) | |_| | |_| || (_) | | | | | |___| | | | (_| | | | | (_| |  __/ |  __/  __/ |  | | (_) | (_| |
//                     *     |____/ \__,_|\__|\__\___/|_| |_|  \____|_| |_|\__,_|_| |_|\__, |\___| |_|   \___|_|  |_|\___/ \__,_|
//                     *                                                               |___/
//                     */
//                    final JButton btnChangePeriod = new JButton(SYSConst.icon22changePeriod);
//                    btnChangePeriod.setPressedIcon(SYSConst.icon22changePeriodPressed);
//                    btnChangePeriod.setAlignmentX(Component.RIGHT_ALIGNMENT);
//                    btnChangePeriod.setContentAreaFilled(false);
//                    btnChangePeriod.setBorder(null);
//                    btnChangePeriod.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//
//                    ActionListener al;
//                    if (type.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_SINGLE_INCIDENTS) {
//                        al = new ActionListener() {
//                            @Override
//                            public void actionPerformed(ActionEvent actionEvent) {
//                                final JidePopup popup = new JidePopup();
//                                PnlPIT pnlPIT = new PnlPIT(innerResInfo.getVon(), new Closure() {
//                                    @Override
//                                    public void execute(Object o) {
//                                        popup.hidePopup();
//                                        if (o != null) {
//
//                                            EntityManager em = OPDE.createEM();
//                                            try {
//                                                em.getTransaction().begin();
//                                                ResInfo mybwinfo = em.merge(innerResInfo);
//                                                em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
//                                                em.lock(mybwinfo, LockModeType.OPTIMISTIC);
//                                                Date date = (Date) o;
//                                                mybwinfo.setFrom(date);
//                                                mybwinfo.setTo(date);
//                                                em.getTransaction().commit();
//                                                refreshTabKat(type.getCategory());
//                                                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.pitchanged")));
//                                            } catch (OptimisticLockException ole) {
//                                                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                                if (em.getTransaction().isActive()) {
//                                                    em.getTransaction().rollback();
//                                                }
//                                                reload();
//                                            } catch (Exception e) {
//                                                if (em.getTransaction().isActive()) {
//                                                    em.getTransaction().rollback();
//                                                }
//                                                OPDE.fatal(e);
//                                            } finally {
//                                                em.close();
//                                            }
//                                        }
//                                    }
//                                });
//                                popup.setMovable(false);
//                                popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
//
//                                popup.setOwner(panelForBWInfoTyp);
//                                popup.removeExcludedComponent(panelForBWInfoTyp);
//                                popup.getContentPane().add(pnlPIT);
//                                popup.setDefaultFocusComponent(pnlPIT);
//                                GUITools.showPopup(popup, SwingConstants.SOUTH_WEST);
//                            }
//                        };
//                    } else {
//                        al = new ActionListener() {
//                            @Override
//                            public void actionPerformed(ActionEvent actionEvent) {
//                                final JidePopup popup = new JidePopup();
//                                Pair<Date, Date> ausdehnung = ResInfoTools.getMinMaxAusdehnung(innerResInfo, new ArrayList<ResInfo>(bwinfos.get(type)));
//                                PnlPeriod pnlPeriod = new PnlPeriod(ausdehnung.getFirst(), ausdehnung.getSecond(), innerResInfo.getVon(), innerResInfo.getTo(), new Closure() {
//                                    @Override
//                                    public void execute(Object o) {
//                                        popup.hidePopup();
//                                        if (o != null) {
//                                            EntityManager em = OPDE.createEM();
//                                            try {
//                                                em.getTransaction().begin();
//                                                ResInfo mybwinfo = em.merge(innerResInfo);
//                                                em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
//                                                em.lock(mybwinfo, LockModeType.OPTIMISTIC);
//                                                Pair<Date, Date> period = (Pair<Date, Date>) o;
//                                                mybwinfo.setFrom(period.getFirst());
//                                                mybwinfo.setTo(period.getSecond());
//                                                em.getTransaction().commit();
//                                                refreshTabKat(type.getCategory());
//                                                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.periodchanged")));
//                                            } catch (OptimisticLockException ole) {
//                                                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                                if (em.getTransaction().isActive()) {
//                                                    em.getTransaction().rollback();
//                                                }
//                                                reload();
//                                            } catch (Exception e) {
//                                                if (em.getTransaction().isActive()) {
//                                                    em.getTransaction().rollback();
//                                                }
//                                                OPDE.fatal(e);
//                                            } finally {
//                                                em.close();
//                                            }
//                                        }
//
//                                    }
//                                });
//                                popup.setMovable(false);
//                                popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
//
//                                popup.setOwner(btnChangePeriod);
//                                popup.removeExcludedComponent(btnChangePeriod);
//                                popup.getContentPane().add(pnlPeriod);
//                                popup.setDefaultFocusComponent(pnlPeriod);
//                                GUITools.showPopup(popup, SwingConstants.SOUTH_WEST);
//                            }
//                        };
//                    }
//                    btnChangePeriod.addActionListener(al);
//                    btnChangePeriod.setEnabled(!innerResInfo.isHeimaufnahme());
//                    contentLineRight.add(btnChangePeriod);
//
//                    /***
//                     *      ____        _   _                _____ _ _         _   _   _             _        ____            _             _
//                     *     | __ ) _   _| |_| |_ ___  _ __   |  ___(_) | ___   / \ | |_| |_ __ _  ___| |__    / ___|___  _ __ | |_ ___ _ __ | |_
//                     *     |  _ \| | | | __| __/ _ \| '_ \  | |_  | | |/ _ \ / _ \| __| __/ _` |/ __| '_ \  | |   / _ \| '_ \| __/ _ \ '_ \| __|
//                     *     | |_) | |_| | |_| || (_) | | | | |  _| | | |  __// ___ \ |_| || (_| | (__| | | | | |__| (_) | | | | ||  __/ | | | |_
//                     *     |____/ \__,_|\__|\__\___/|_| |_| |_|   |_|_|\___/_/   \_\__|\__\__,_|\___|_| |_|  \____\___/|_| |_|\__\___|_| |_|\__|
//                     *
//                     */
//                    if (OPDE.getAppInfo().userHasAccessLevelForThisClass(PnlFiles.internalClassID, InternalClassACL.INSERT)) {    // => ACL_MATRIX
//                        JButton btnAttach = new JButton(SYSConst.icon22attach);
//                        btnAttach.setPressedIcon(SYSConst.icon22attachPressed);
//                        btnAttach.setAlignmentX(Component.RIGHT_ALIGNMENT);
//                        btnAttach.setContentAreaFilled(false);
//                        btnAttach.setBorder(null);
//                        btnAttach.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//                        btnAttach.addActionListener(new ActionListener() {
//                            @Override
//                            public void actionPerformed(ActionEvent actionEvent) {
//                                // No Attachments anymore for OLD entries. Only view.
//                                // But active NoConstraints may get attachments
//                                Closure closure = null;
//                                if (innerResInfo.isActiveNoConstraint()) {
//                                    closure = new Closure() {
//                                        @Override
//                                        public void execute(Object o) {
//                                            refreshTabKat(type.getCategory());
//                                        }
//                                    };
//                                }
//
//                                new DlgFiles(innerResInfo, closure);
//                            }
//                        });
//
//                        if (innerResInfo.getAttachedFilesConnections().size() > 0) {
//                            JLabel lblNum = new JLabel(Integer.toString(innerResInfo.getAttachedFilesConnections().size()), SYSConst.icon16redStar, SwingConstants.CENTER);
//                            lblNum.setFont(SYSConst.ARIAL10BOLD);
//                            lblNum.setForeground(Color.YELLOW);
//                            lblNum.setHorizontalTextPosition(SwingConstants.CENTER);
//                            DefaultOverlayable overlayableBtn = new DefaultOverlayable(btnAttach, lblNum, DefaultOverlayable.SOUTH_EAST);
//                            overlayableBtn.setOpaque(false);
//                            overlayableBtn.setOverlayVisible(ersterResInfo != null);
//                            contentLineRight.add(overlayableBtn);
//                        } else {
//                            contentLineRight.add(btnAttach);
//                        }
//
//                        btnAttach.setEnabled(innerResInfo.isActiveNoConstraint() || innerResInfo.getAttachedFilesConnections().size() > 0);
//                    }
//
//                    contentLine.add(contentButton);
//                    contentLine.add(contentLineRight);
//
//                    labelPanel.add(contentLine);
//                }
//            }
//
//            panelForBWInfoTyp.setContentPane(labelPanel);
//            panelForBWInfoTyp.setCollapsible(shallBeCollapsible);
//            panelForBWInfoTyp.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
//                @Override
//                public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
//                    if (ersterResInfo != null) {
//                        bwinfo4html.get(ersterResInfo).setEnabled(!ersterResInfo.isClosed() || ersterResInfo.isSingleIncident());
//                    }
//                    SYSPropsTools.storeBoolean(internalClassID + ":panelCollapsed:" + type.getID(), false, OPDE.getLogin().getUser());
//                }
//
//                @Override
//                public void paneCollapsed(CollapsiblePaneEvent collapsiblePaneEvent) {
//                    if (ersterResInfo != null) {
//                        bwinfo4html.get(ersterResInfo).setEnabled(!ersterResInfo.isClosed() || ersterResInfo.isSingleIncident());
//                    }
//                    SYSPropsTools.storeBoolean(internalClassID + ":panelCollapsed:" + type.getID(), true, OPDE.getLogin().getUser());
//                }
//            });
//            if (shallBeCollapsible) {
//                panelForBWInfoTyp.setCollapsed(SYSPropsTools.isBooleanTrue(internalClassID + ":panelCollapsed:" + type.getID(), true));
//            }
////            panelForBWInfoTyp.setVisible((tbEmpty.isSelected() || ersterResInfo != null) && tbInactive.isSelected() || (ersterResInfo != null && !ersterResInfo.isDiscontinued()));
//
//        } catch (PropertyVetoException e) {
//            OPDE.error(e);
//        } catch (Exception e) {
//            OPDE.fatal(e);
//        }
//
//
//        return panelForBWInfoTyp;
//    }


    private JPanel getMenu(final ResInfo resInfo) {

        JPanel pnlMenu = new JPanel(new VerticalLayout());

        final JButton btnChange = GUITools.createHyperlinkButton(internalClassID + ".btnChange.tooltip", SYSConst.icon22playerPlay, null);
        btnChange.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnChange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
            }
        });
//        btnEdit.setEnabled(!resInfo.isClosed());
        pnlMenu.add(btnChange);

        final JButton btnStop = GUITools.createHyperlinkButton(internalClassID + ".btnStop.tooltip", SYSConst.icon22playerStop, null);
        btnStop.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
            }
        });
//        btnEdit.setEnabled(!resInfo.isClosed());
        pnlMenu.add(btnStop);

        /***
         *      _____    _ _ _
         *     | ____|__| (_) |_
         *     |  _| / _` | | __|
         *     | |__| (_| | | |_
         *     |_____\__,_|_|\__|
         *
         */
        final JButton btnEdit = GUITools.createHyperlinkButton(internalClassID + ".btnEdit.tooltip", SYSConst.icon22edit3, null);
        btnEdit.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
//                new DlgReport(nreport.clone(), new Closure() {
//                    @Override
//                    public void execute(Object o) {
//                        if (o != null) {
//
//                            EntityManager em = OPDE.createEM();
//                            try {
//                                em.getTransaction().begin();
//                                em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
//                                final NReport newReport = em.merge((NReport) o);
//                                NReport oldReport = em.merge(nreport);
//                                em.lock(oldReport, LockModeType.OPTIMISTIC);
//                                newReport.setReplacementFor(oldReport);
//
//                                for (SYSNR2FILE oldAssignment : oldReport.getAttachedFilesConnections()) {
//                                    em.remove(oldAssignment);
//                                }
//                                oldReport.getAttachedFilesConnections().clear();
//                                for (SYSNR2PROCESS oldAssignment : oldReport.getAttachedProcessConnections()) {
//                                    em.remove(oldAssignment);
//                                }
//                                oldReport.getAttachedProcessConnections().clear();
//
//                                oldReport.setEditedBy(em.merge(OPDE.getLogin().getUser()));
//                                oldReport.setEditDate(new Date());
//                                oldReport.setReplacedBy(newReport);
//
//                                em.getTransaction().commit();
//
//                                final String keyNewDay = DateFormat.getDateInstance().format(newReport.getPit());
//                                final String keyOldDay = DateFormat.getDateInstance().format(oldReport.getPit());
//
//                                contentmap.remove(keyNewDay);
//                                contentmap.remove(keyOldDay);
//
//                                linemap.remove(oldReport);
//
//                                valuecache.get(keyOldDay).remove(nreport);
//                                valuecache.get(keyOldDay).add(oldReport);
//                                Collections.sort(valuecache.get(keyOldDay));
//
//                                if (valuecache.containsKey(keyNewDay)) {
//                                    valuecache.get(keyNewDay).add(newReport);
//                                    Collections.sort(valuecache.get(keyNewDay));
//                                }
//
//                                createCP4Day(new DateMidnight(oldReport.getPit()));
//                                createCP4Day(new DateMidnight(newReport.getPit()));
//
//                                buildPanel();
//                                GUITools.scroll2show(jspReports, cpMap.get(keyNewDay), cpsReports, new Closure() {
//                                    @Override
//                                    public void execute(Object o) {
//                                        GUITools.flashBackground(linemap.get(newReport), Color.YELLOW, 2);
//                                    }
//                                });
//                            } catch (OptimisticLockException ole) {
//                                if (em.getTransaction().isActive()) {
//                                    em.getTransaction().rollback();
//                                }
//                                if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                    OPDE.getMainframe().emptyFrame();
//                                    OPDE.getMainframe().afterLogin();
//                                }
//                                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                            } catch (Exception e) {
//                                if (em.getTransaction().isActive()) {
//                                    em.getTransaction().rollback();
//                                }
//                                OPDE.fatal(e);
//                            } finally {
//                                em.close();
//                            }
//                        }
//                    }
//                });
            }
        });
//        btnEdit.setEnabled(!resInfo.isClosed());
        pnlMenu.add(btnEdit);

        /***
         *      ____       _      _
         *     |  _ \  ___| | ___| |_ ___
         *     | | | |/ _ \ |/ _ \ __/ _ \
         *     | |_| |  __/ |  __/ ||  __/
         *     |____/ \___|_|\___|\__\___|
         *
         */
        final JButton btnDelete = GUITools.createHyperlinkButton(internalClassID + ".btnDelete.tooltip", SYSConst.icon22delete, null);
        btnDelete.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new DlgYesNo(OPDE.lang.getString("misc.questions.delete1") + "<br/><i>" + resInfo.getPITAsHTML() + "</i><br/>" + OPDE.lang.getString("misc.questions.delete2"), SYSConst.icon48delete, new Closure() {
                    @Override
                    public void execute(Object answer) {
                        if (answer.equals(JOptionPane.YES_OPTION)) {
                        }
                    }
                });
            }
        });
//        btnDelete.setEnabled(NReportTools.isChangeable(nreport));
        pnlMenu.add(btnDelete);


        /***
         *      _     _         _____ _ _
         *     | |__ | |_ _ __ |  ___(_) | ___  ___
         *     | '_ \| __| '_ \| |_  | | |/ _ \/ __|
         *     | |_) | |_| | | |  _| | | |  __/\__ \
         *     |_.__/ \__|_| |_|_|   |_|_|\___||___/
         *
         */
        final JButton btnFiles = GUITools.createHyperlinkButton("misc.btnfiles.tooltip", SYSConst.icon22attach, null);
        btnFiles.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnFiles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new DlgFiles(resInfo, new Closure() {
                    @Override
                    public void execute(Object o) {
                        EntityManager em = OPDE.createEM();
                        final ResInfo myInfo = em.merge(resInfo);
                        em.refresh(myInfo);
                        em.close();

                        valuecache.get(resInfo.getResInfoType()).remove(resInfo);
                        valuecache.get(myInfo.getResInfoType()).add(myInfo);
                        Collections.sort(valuecache.get(myInfo.getResInfoType()));

                        createCP4Info(myInfo);

                        buildPanel();
//                        GUITools.flashBackground(linemap.get(myReport), Color.YELLOW, 2);
                    }
                });
            }
        });

//        btnFiles.setEnabled(!nreport.isObsolete());
        if (!resInfo.getAttachedFilesConnections().isEmpty()) {
            JLabel lblNum = new JLabel(Integer.toString(resInfo.getAttachedFilesConnections().size()), SYSConst.icon16greenStar, SwingConstants.CENTER);
            lblNum.setFont(SYSConst.ARIAL14BOLD);
            lblNum.setForeground(Color.BLACK);
            lblNum.setHorizontalTextPosition(SwingConstants.CENTER);
            DefaultOverlayable overlayableBtn = new DefaultOverlayable(btnFiles, lblNum, DefaultOverlayable.SOUTH_EAST);
            overlayableBtn.setOpaque(false);
            pnlMenu.add(overlayableBtn);
        } else {
            pnlMenu.add(btnFiles);
        }


        /***
         *      _     _         ____
         *     | |__ | |_ _ __ |  _ \ _ __ ___   ___ ___  ___ ___
         *     | '_ \| __| '_ \| |_) | '__/ _ \ / __/ _ \/ __/ __|
         *     | |_) | |_| | | |  __/| | | (_) | (_|  __/\__ \__ \
         *     |_.__/ \__|_| |_|_|   |_|  \___/ \___\___||___/___/
         *
         */
        final JButton btnProcess = GUITools.createHyperlinkButton("misc.btnprocess.tooltip", SYSConst.icon22link, null);
        btnProcess.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnProcess.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new DlgProcessAssign(resInfo, new Closure() {
                    @Override
                    public void execute(Object o) {
                        if (o == null) {
                            return;
                        }
                        Pair<ArrayList<QProcess>, ArrayList<QProcess>> result = (Pair<ArrayList<QProcess>, ArrayList<QProcess>>) o;

                        ArrayList<QProcess> assigned = result.getFirst();
                        ArrayList<QProcess> unassigned = result.getSecond();

                        EntityManager em = OPDE.createEM();

                        try {
                            em.getTransaction().begin();

                            em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                            final ResInfo myInfo = em.merge(resInfo);
                            em.lock(myInfo, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                            for (SYSINF2PROCESS linkObject : myInfo.getAttachedProcessConnections()) {
                                if (unassigned.contains(linkObject.getQProcess())) {
                                    em.remove(em.merge(linkObject));
                                }
                            }

                            for (QProcess qProcess : assigned) {
                                java.util.List<QProcessElement> listElements = qProcess.getElements();
                                if (!listElements.contains(myInfo)) {
                                    QProcess myQProcess = em.merge(qProcess);
                                    SYSINF2PROCESS myLinkObject = em.merge(new SYSINF2PROCESS(myQProcess, myInfo));
                                    qProcess.getAttachedResInfoConnections().add(myLinkObject);
                                    myInfo.getAttachedProcessConnections().add(myLinkObject);
                                }
                            }

                            em.getTransaction().commit();

                            valuecache.get(resInfo.getResInfoType()).remove(resInfo);
                            valuecache.get(myInfo.getResInfoType()).add(myInfo);
                            Collections.sort(valuecache.get(myInfo.getResInfoType()));

                            createCP4Info(myInfo);

                            buildPanel();
//                            GUITools.flashBackground(linemap.get(myReport), Color.YELLOW, 2);
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
            }
        });
//        btnProcess.setEnabled(!nreport.isObsolete());

        if (!resInfo.getAttachedProcessConnections().isEmpty()) {
            JLabel lblNum = new JLabel(Integer.toString(resInfo.getAttachedProcessConnections().size()), SYSConst.icon16redStar, SwingConstants.CENTER);
            lblNum.setFont(SYSConst.ARIAL14BOLD);
            lblNum.setForeground(Color.YELLOW);
            lblNum.setHorizontalTextPosition(SwingConstants.CENTER);
            DefaultOverlayable overlayableBtn = new DefaultOverlayable(btnProcess, lblNum, DefaultOverlayable.SOUTH_EAST);
            overlayableBtn.setOpaque(false);
            pnlMenu.add(overlayableBtn);
        } else {
            pnlMenu.add(btnProcess);
        }
        return pnlMenu;
    }


    @Override
    public void cleanup() {
        bwinfotypen.clear();

        contentmap.clear();
        cpMap.clear();
        cpsInfo.removeAll();
        linemap.clear();
        valuecache.clear();

    }

    @Override
    public void reload() {
        reloadDisplay();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jspInfo = new JScrollPane();
        cpsInfo = new CollapsiblePanes();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== jspInfo ========
        {

            //======== cpsInfo ========
            {
                cpsInfo.setLayout(new BoxLayout(cpsInfo, BoxLayout.X_AXIS));
            }
            jspInfo.setViewportView(cpsInfo);
        }
        add(jspInfo);
        add(jspInfo);
    }// </editor-fold>//GEN-END:initComponents

    private void buildPanel() {
        cpsInfo.removeAll();
        cpsInfo.setLayout(new JideBoxLayout(cpsInfo, JideBoxLayout.Y_AXIS));
        for (ResInfoCategory cat : categories) {
            cpsInfo.add(cpMap.get(cat.getID() + ".xcategory"));
        }

        cpsInfo.addExpansion();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jspInfo;
    private CollapsiblePanes cpsInfo;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
