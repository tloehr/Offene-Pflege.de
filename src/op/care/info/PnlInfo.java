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
import com.jidesoft.wizard.WizardDialog;
import entity.files.SYSFilesTools;
import entity.info.*;
import entity.process.QProcess;
import entity.process.QProcessElement;
import entity.process.SYSINF2PROCESS;
import op.OPDE;
import op.care.sysfiles.DlgFiles;
import op.process.DlgProcessAssign;
import op.residents.DlgEditResidentBaseData;
import op.residents.bwassistant.AddBWWizard;
import op.system.InternalClassACL;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.DateTime;

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
import java.util.*;
import java.util.List;

/**
 * @author Torsten Löhr
 */
public class PnlInfo extends NursingRecordsPanel {
    public static final String internalClassID = "nursingrecords.info";

    private Resident resident;
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;

    private HashMap<String, CollapsiblePane> cpMap;
    private HashMap<ResInfoType, ArrayList<ResInfo>> valuecache;
    //    private HashMap<ResInfoCategory, List<ResInfoType>> bwinfotypen;
    private List<ResInfoCategory> categories;
    private ArrayList<ResInfo> listInfo;

    private JToggleButton tbInactive;
    private JideButton btnBWDied, btnBWMovedOut, btnBWisAway, btnBWisBack;
    private Color[] color1, color2;

    private ResInfoType typeAbsence, typeStartOfStay;

    private boolean initPhase;

    @Override
    public String getInternalClassID() {
        return internalClassID;
    }

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

//        bwinfotypen = new HashMap<ResInfoCategory, List<ResInfoType>>();
        valuecache = new HashMap<ResInfoType, ArrayList<ResInfo>>();
        cpMap = new HashMap<String, CollapsiblePane>();
        prepareSearchArea();

        typeAbsence = ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_ABSENCE);
        typeStartOfStay = ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_STAY);
        categories = ResInfoCategoryTools.getAll4ResInfo();

        color1 = SYSConst.green1;
        color2 = SYSConst.greyscale;
    }

    @Override
    public void switchResident(Resident res) {
        this.resident = res;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                btnBWDied.setEnabled(resident.isActive());
                btnBWMovedOut.setEnabled(resident.isActive());
                btnBWisAway.setEnabled(resident.isActive() && !isAway());
                btnBWisBack.setEnabled(resident.isActive() && isAway());
            }
        });

        GUITools.setBWDisplay(resident);
        reload();
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
//                        for (final ResInfoCategory kat : ResInfoCategoryTools.getAll4ResInfo()) {
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
//                                tabKat.addTab(kat.getText(), new JScrollPane(createCollapsiblePanesFor(kat)));
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
            if (valuecache.isEmpty()) {
                for (ResInfo resInfo : listInfo) {
                    if (!valuecache.containsKey(resInfo.getResInfoType())) {
                        valuecache.put(resInfo.getResInfoType(), new ArrayList<ResInfo>());
                    }
                    valuecache.get(resInfo.getResInfoType()).add(resInfo);
                }
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
                cat.getText() +
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

        GUITools.addExpandCollapseButtons(cpCat, cptitle.getRight());

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

        if (!cpCat.isCollapsed()) {
            JPanel pnlContent = new JPanel(new VerticalLayout());
            for (ResInfoType type : ResInfoTypeTools.getByCat(cat)) {
                pnlContent.add(createCP4Type(type));
            }
            cpCat.setContentPane(pnlContent);
        }

        return cpCat;
    }

    private JPanel createInfoPanel(final ResInfo resInfo) {
//        OPDE.debug(resInfo.getResInfoType().getShortDescription());
        String title = "<html><table border=\"0\">" +
                "<tr valign=\"top\">" +
                "<td width=\"280\" align=\"left\">" + resInfo.getPITAsHTML() + "</td>" +
                "<td width=\"500\" align=\"left\">" +
                (resInfo.isClosed() ? "<s>" : "") +
                resInfo.getHtml() +
                (resInfo.isClosed() ? "</s>" : "") +
                (SYSTools.catchNull(resInfo.getText()).trim().isEmpty() ? "" : "<b>" + OPDE.lang.getString("misc.msg.comment") + ": </b><p>" + resInfo.getText().trim() + "</p>") +
                "</td>" +
                "</table>" +
                "</html>";
        DefaultCPTitle cptitle = new DefaultCPTitle(title, null);

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

        cptitle.getRight().add(btnMenu);
        cptitle.getMain().setBackground(getColor(resInfo.getResInfoType().getResInfoCat())[SYSConst.light2]);
        cptitle.getMain().setOpaque(true);
        cptitle.getButton().setIcon(resInfo.isClosed() ? SYSConst.icon22stopSign : null);
        cptitle.getButton().setVerticalTextPosition(SwingConstants.TOP);

        return cptitle.getMain();
    }

    private JPanel createContentPanel4Type(final ResInfoType type, boolean closed2) {
        final JPanel infoPanel = new JPanel(new VerticalLayout());
        infoPanel.setOpaque(false);

        if (!valuecache.containsKey(type)) {
            valuecache.put(type, ResInfoTools.getByResidentAndType(resident, type));
        }

        int i = 0; // for zebra pattern
        for (final ResInfo resInfo : valuecache.get(type)) {
            if (closed2 || !resInfo.isClosed()) {
                JPanel cp = createInfoPanel(resInfo);
                if (i % 2 == 0) {
                    cp.setBackground(Color.WHITE);
                }
                i++;
                infoPanel.add(cp);
            }
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
                type.getShortDescription() +
                "</font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!cpType.isCollapsible()) return;
                try {
                    cpType.setCollapsed(!cpType.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            }
        });

        /***
         *         _       _     _
         *        / \   __| | __| |
         *       / _ \ / _` |/ _` |
         *      / ___ \ (_| | (_| |
         *     /_/   \_\__,_|\__,_|
         *
         */
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
                Closure closure = new Closure() {
                    @Override
                    public void execute(Object o) {
                        if (o != null) {
                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();
                                em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                // so that no conflicts can occur if another user enters a new info at the same time
                                em.lock(em.merge(type), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                                ResInfo newinfo = em.merge((ResInfo) o);
                                newinfo.setHtml(ResInfoTools.getContentAsHTML(newinfo));
                                em.getTransaction().commit();

                                if (!valuecache.containsKey(newinfo.getResInfoType())){
                                    valuecache.put(newinfo.getResInfoType(), new ArrayList<ResInfo>());
                                }
                                valuecache.get(newinfo.getResInfoType()).add(newinfo);
                                Collections.sort(valuecache.get(newinfo.getResInfoType()));
                                createCP4Type(newinfo.getResInfoType());
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
                        } else {
                            OPDE.getDisplayManager().clearSubMessages();
                        }
                    }
                };

                if (type.getType().intValue() == ResInfoTypeTools.TYPE_DIAGNOSIS) {
                    new DlgDiag(new ResInfo(type, resident), closure);
                } else {
                    new DlgInfo(new ResInfo(type, resident), closure);
                }

            }
        });
        btnAdd.setEnabled(type.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_NOCONSTRAINTS || type.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_SINGLE_INCIDENTS || !valuecache.containsKey(type) || valuecache.get(type).isEmpty() || containsOnlyClosedInfos(type));
        cptitle.getRight().add(btnAdd);

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.PRINT)) {
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
//                    SYSFilesTools.print(ResInfoTools.getResInfosAsHTML(valuecache.get(type), tbInactive.isSelected(), true, type.getShortDescription()), true);
                }
            });
            cptitle.getRight().add(btnPrint);
            btnPrint.setEnabled(valuecache.containsKey(type) && !valuecache.get(type).isEmpty());
        }


        cpType.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                cpType.setContentPane(createContentPanel4Type(type, tbInactive.isSelected()));
            }
        });

//        cpType.setCollapsible(valuecache.containsKey(type) && !valuecache.get(type).isEmpty());

        if (!cpType.isCollapsed()) {
            cpType.setContentPane(createContentPanel4Type(type, tbInactive.isSelected()));
        }

        cptitle.getButton().setIcon(getIcon(type));
        cptitle.getButton().setToolTipText(getTooltip(type));

        cpType.setTitleLabelComponent(cptitle.getMain());
        cpType.setSlidingDirection(SwingConstants.SOUTH);
        cpType.setBackground(getColor(type.getResInfoCat())[SYSConst.light4]);
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

    private List<Component> addCommands() {

        List<Component> list = new ArrayList<Component>();

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.MANAGER)) {
            /***
             *                          ____           _     _            _
             *      _ __   _____      _|  _ \ ___  ___(_) __| | ___ _ __ | |_
             *     | '_ \ / _ \ \ /\ / / |_) / _ \/ __| |/ _` |/ _ \ '_ \| __|
             *     | | | |  __/\ V  V /|  _ <  __/\__ \ | (_| |  __/ | | | |_
             *     |_| |_|\___| \_/\_/ |_| \_\___||___/_|\__,_|\___|_| |_|\__|
             *
             */
            JideButton addRes = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".addbw"), SYSConst.icon22addbw, null);
            addRes.addMouseListener(GUITools.getHyperlinkStyleMouseAdapter());
            addRes.setAlignmentX(Component.LEFT_ALIGNMENT);
            addRes.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    final MyJDialog dlg = new MyJDialog();
                    WizardDialog wizard = new AddBWWizard(new Closure() {
                        @Override
                        public void execute(Object o) {
                            dlg.dispose();
                        }
                    }).getWizard();
                    dlg.setContentPane(wizard.getContentPane());
                    dlg.pack();
                    dlg.setSize(new Dimension(800, 550));
                    dlg.setVisible(true);
                }
            });
            list.add(addRes);

            JideButton editRes = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".editbw"), SYSConst.icon22edit3, null);
            editRes.addMouseListener(GUITools.getHyperlinkStyleMouseAdapter());
            editRes.setAlignmentX(Component.LEFT_ALIGNMENT);
            editRes.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgEditResidentBaseData(resident, new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {
                                EntityManager em = OPDE.createEM();

                                try {
                                    em.getTransaction().begin();
                                    Resident myResident = em.merge((Resident) o);
                                    em.lock(em.merge(myResident), LockModeType.OPTIMISTIC);
                                    em.getTransaction().commit();
                                    resident = myResident;
                                    GUITools.setBWDisplay(resident);
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
            list.add(editRes);

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
                    new DlgPIT(OPDE.lang.getString(internalClassID + ".dlg.dateofdeath"), new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {
                                Date dod = (Date) o;
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                                    resident.setStation(null);
                                    ResInfo stay = em.merge(getLastStay());
                                    em.lock(stay, LockModeType.OPTIMISTIC);

                                    stay.setTo(dod);
                                    stay.setUserOFF(em.merge(OPDE.getLogin().getUser()));

                                    Properties props = ResInfoTools.getContent(stay);
                                    props.setProperty("hauf", "verstorben");
                                    ResInfoTools.setContent(stay, props);

                                    ResidentTools.endOfStay(em, em.merge(resident), dod);
                                    em.getTransaction().commit();
                                    btnBWDied.setEnabled(false);
                                    btnBWMovedOut.setEnabled(false);
                                    btnBWisAway.setEnabled(false);
                                    btnBWisBack.setEnabled(false);
//                                    reloadDisplay();
                                    GUITools.setBWDisplay(resident);
                                    OPDE.getMainframe().emptyFrame();
                                    OPDE.getMainframe().afterLogin();
                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".msg.isdeadnow"), 5));
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
                    new DlgPIT(OPDE.lang.getString(internalClassID + ".dlg.dateofmoveout"), new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {
                                Date dod = (Date) o;
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                                    resident.setStation(null);
                                    ResInfo stay = em.merge(getLastStay());
                                    em.lock(stay, LockModeType.OPTIMISTIC);
                                    stay.setTo(dod);
                                    stay.setUserOFF(em.merge(OPDE.getLogin().getUser()));

                                    Properties props = ResInfoTools.getContent(stay);
                                    props.setProperty("hauf", "ausgezogen");
                                    ResInfoTools.setContent(stay, props);

                                    ResidentTools.endOfStay(em, em.merge(resident), dod);
                                    em.getTransaction().commit();

                                    btnBWDied.setEnabled(false);
                                    btnBWMovedOut.setEnabled(false);
                                    btnBWisAway.setEnabled(false);
                                    btnBWisBack.setEnabled(false);

                                    GUITools.setBWDisplay(resident);
                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".msg.hasgonenow"), 5));
                                    OPDE.getMainframe().emptyFrame();
                                    OPDE.getMainframe().afterLogin();
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
            btnBWMovedOut.setEnabled(resident.isActive());
            list.add(btnBWMovedOut);
        }

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE)) {
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
                    PnlAbwesend pnlAbwesend = new PnlAbwesend(new ResInfo(typeAbsence, resident), new Closure() {
                        @Override
                        public void execute(Object o) {
                            popup.hidePopup();
                            if (o != null) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                                    ResInfo absence = em.merge((ResInfo) o);
                                    em.getTransaction().commit();
                                    btnBWisAway.setEnabled(false);
                                    btnBWisBack.setEnabled(true);

                                    if (!valuecache.containsKey(typeAbsence)) {
                                        valuecache.put(typeAbsence, new ArrayList<ResInfo>());
                                    }
                                    valuecache.get(typeAbsence).add(absence);
                                    Collections.sort(valuecache.get(typeAbsence));
                                    createCP4Type(typeAbsence);
                                    buildPanel();

                                    GUITools.setBWDisplay(resident);
                                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".msg.isawaynow")));
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
                    popup.setMovable(false);
                    popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));

                    popup.setOwner(btnBWisAway);
                    popup.removeExcludedComponent(btnBWisAway);
                    popup.getContentPane().add(pnlAbwesend);
                    popup.setDefaultFocusComponent(pnlAbwesend);
                    GUITools.showPopup(popup, SwingConstants.NORTH_EAST);
                }
            });
            btnBWisAway.setEnabled(resident.isActive() && !isAway());
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
                        ResInfo lastabsence = em.merge(getLastAbsence());
                        em.lock(lastabsence, LockModeType.OPTIMISTIC);
                        lastabsence.setTo(new Date());
                        lastabsence.setUserOFF(em.merge(OPDE.getLogin().getUser()));
                        em.getTransaction().commit();

                        btnBWisAway.setEnabled(true);
                        btnBWisBack.setEnabled(false);

                        valuecache.get(typeAbsence).remove(lastabsence);
                        valuecache.get(typeAbsence).add(lastabsence);
                        Collections.sort(valuecache.get(typeAbsence));
                        createCP4Type(typeAbsence);
                        buildPanel();

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
            btnBWisBack.setEnabled(resident.isActive() && isAway());
            list.add(btnBWisBack);
        }

//        final JideButton btnExpandAll = GUITools.createHyperlinkButton(OPDE.lang.getString("misc.msg.expandall"), SYSConst.icon22expand, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent actionEvent) {
//                try {
//                    GUITools.setCollapsed(cpsInfo, false);
//                } catch (PropertyVetoException e) {
//                    // bah!
//                }
//            }
//        });
//        list.add(btnExpandAll);
//
//        final JideButton btnCollapseAll = GUITools.createHyperlinkButton(OPDE.lang.getString("misc.msg.collapseall"), SYSConst.icon22collapse, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent actionEvent) {
//                try {
//                    GUITools.setCollapsed(cpsInfo, true);
//                } catch (PropertyVetoException e) {
//                    // bah!
//                }
//            }
//        });
//        list.add(btnCollapseAll);

        /***
         *      ____       _       _
         *     |  _ \ _ __(_)_ __ | |_
         *     | |_) | '__| | '_ \| __|
         *     |  __/| |  | | | | | |_
         *     |_|   |_|  |_|_| |_|\__|
         *
         */
        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.PRINT)) {
            final JButton btnPrint = GUITools.createHyperlinkButton(OPDE.lang.getString("misc.commands.print"), SYSConst.icon22print2, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
//                    Collections.sort(listInfo);
                    String html = "";
                    html += "<h1 id=\"fonth1\" >" + OPDE.lang.getString("nursingrecords.info");
                    html += " " + OPDE.lang.getString("misc.msg.for") + " " + ResidentTools.getLabelText(resident) + "</h1>\n";

                    for (ResInfoCategory cat : categories) {
                        html += "<h2 id=\"fonth2\" >" + cat.getText() + "</h2>\n";
                        for (ResInfoType type : ResInfoTypeTools.getByCat(cat)) {
                            if (valuecache.containsKey(type) && !valuecache.get(type).isEmpty() && (tbInactive.isSelected() || !containsOnlyClosedInfos(type))) {
                                html += "<h3 id=\"fonth3\" >" + type.getShortDescription() + "</h3>\n";
                                html += ResInfoTools.getResInfosAsHTML(valuecache.get(type), tbInactive.isSelected());
                            }
                        }
                    }
                    SYSFilesTools.print(html, true);
                }
            });
            btnPrint.setAlignmentX(Component.RIGHT_ALIGNMENT);

            list.add(btnPrint);
        }

        return list;
    }

    private boolean containsOnlyClosedInfos(final ResInfoType type) {
        boolean containsOnlyClosedInfos = true;
        for (ResInfo info : valuecache.get(type)) {
            containsOnlyClosedInfos = info.isClosed();
            if (!containsOnlyClosedInfos) {
                break;
            }
        }
        return containsOnlyClosedInfos;
    }

    private List<Component> addFilters() {
        List<Component> list = new ArrayList<Component>();

        tbInactive = GUITools.getNiceToggleButton(OPDE.lang.getString(internalClassID + ".inactive"));
        tbInactive.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (initPhase) return;
                reloadDisplay();
            }
        });
        tbInactive.setHorizontalAlignment(SwingConstants.LEFT);
        list.add(tbInactive);

        return list;
    }

    private Icon getIcon(ResInfoType type) {
//        boolean empty = !valuecache.containsKey(type) || valuecache.get(type).isEmpty();

        if (type.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_SINGLE_INCIDENTS) {
            return SYSConst.icon22singleIncident;
        }
        if (type.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_BYDAY) {
            return SYSConst.icon22intervalByDay;
        }
        if (type.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_NOCONSTRAINTS) {
            return SYSConst.icon22intervalNoConstraints;
        }

        return SYSConst.icon22intervalBySecond;
    }

    private String getTooltip(ResInfoType type) {
        if (type.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_SINGLE_INCIDENTS) {
            return OPDE.lang.getString(internalClassID + ".interval_single_incidents");
        }
        if (type.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_BYDAY) {
            return OPDE.lang.getString(internalClassID + ".interval_byday");
        }
        if (type.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_NOCONSTRAINTS) {
            return OPDE.lang.getString(internalClassID + ".interval_noconstraints");
        }
        return OPDE.lang.getString(internalClassID + ".interval_bysecond");
    }

    private ResInfo getLastAbsence() {
        ResInfo lastAbsence = null;
        if (valuecache.containsKey(typeAbsence) && !valuecache.get(typeAbsence).isEmpty()) {
            int size = valuecache.get(typeAbsence).size();
            lastAbsence = valuecache.get(typeAbsence).get(size - 1);
        }
        return lastAbsence;
    }

    private ResInfo getLastStay() {
        ResInfo lastStay = null;
        if (valuecache.containsKey(typeStartOfStay) && !valuecache.get(typeStartOfStay).isEmpty()) {
            int size = valuecache.get(typeStartOfStay).size();
            lastStay = valuecache.get(typeStartOfStay).get(size - 1);
        }
        return lastStay;
    }

    private boolean isAway() {
        ResInfo lastAbsence = getLastAbsence();
        return lastAbsence != null && !lastAbsence.isClosed();
    }

    private JPanel getMenu(final ResInfo resInfo) {

        final JPanel pnlMenu = new JPanel(new VerticalLayout());

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE)) {
            /***
             *       ____ _
             *      / ___| |__   __ _ _ __   __ _  ___
             *     | |   | '_ \ / _` | '_ \ / _` |/ _ \
             *     | |___| | | | (_| | | | | (_| |  __/
             *      \____|_| |_|\__,_|_| |_|\__, |\___|
             *                              |___/
             */
            final JButton btnChange = GUITools.createHyperlinkButton(internalClassID + ".btnChange.tooltip", SYSConst.icon22playerPlay, null);
            btnChange.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnChange.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgInfo(resInfo.clone(), new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    ResInfo oldinfo = em.merge(resInfo);
                                    ResInfo newinfo = em.merge((ResInfo) o);
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                    // so that no conflicts can occur if another user enters a new info at the same time
                                    em.lock(em.merge(resInfo.getResInfoType()), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                                    em.lock(oldinfo, LockModeType.OPTIMISTIC);

                                    newinfo.setHtml(ResInfoTools.getContentAsHTML(newinfo));
                                    newinfo.setUserON(em.merge(OPDE.getLogin().getUser()));
                                    newinfo.setFrom(new Date());

                                    oldinfo.setTo(new DateTime(newinfo.getFrom()).minusSeconds(1).toDate());
                                    oldinfo.setUserOFF(newinfo.getUserON());

                                    em.getTransaction().commit();

                                    valuecache.get(resInfo.getResInfoType()).remove(resInfo);
                                    valuecache.get(oldinfo.getResInfoType()).add(oldinfo);
                                    valuecache.get(newinfo.getResInfoType()).add(newinfo);
                                    Collections.sort(valuecache.get(newinfo.getResInfoType()));
                                    CollapsiblePane myCP = createCP4Type(newinfo.getResInfoType());
                                    buildPanel();
                                    GUITools.flashBackground(myCP, Color.YELLOW, 2);
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
            btnChange.setEnabled(!resInfo.isClosed() && !resInfo.isSingleIncident() && !resInfo.isNoConstraints());
            pnlMenu.add(btnChange);


            /***
             *      ____  _
             *     / ___|| |_ ___  _ __
             *     \___ \| __/ _ \| '_ \
             *      ___) | || (_) | |_) |
             *     |____/ \__\___/| .__/
             *                    |_|
             */
            final JButton btnStop = GUITools.createHyperlinkButton(internalClassID + ".btnStop.tooltip", SYSConst.icon22playerStop, null);
            btnStop.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnStop.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgYesNo(OPDE.lang.getString("misc.questions.cancel") + "<br/>" + resInfo.getResInfoType().getShortDescription() + "<br/>" + resInfo.getPITAsHTML(), SYSConst.icon48stop, new Closure() {
                        @Override
                        public void execute(Object answer) {
                            if (answer.equals(JOptionPane.YES_OPTION)) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    ResInfo newinfo = em.merge(resInfo);
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                    em.lock(newinfo, LockModeType.OPTIMISTIC);
                                    newinfo.setTo(new Date());
                                    newinfo.setUserOFF(em.merge(OPDE.getLogin().getUser()));
                                    em.getTransaction().commit();

                                    valuecache.get(resInfo.getResInfoType()).remove(resInfo);
                                    valuecache.get(newinfo.getResInfoType()).add(newinfo);
                                    Collections.sort(valuecache.get(newinfo.getResInfoType()));
                                    CollapsiblePane myCP = createCP4Type(newinfo.getResInfoType());
                                    buildPanel();

                                    GUITools.flashBackground(myCP, Color.YELLOW, 2);
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
            btnStop.setEnabled(!resInfo.isClosed() && !resInfo.isSingleIncident());
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
                    new DlgInfo(resInfo, new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    ResInfo editinfo = em.merge((ResInfo) o);
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                    em.lock(editinfo, LockModeType.OPTIMISTIC);
                                    editinfo.setHtml(ResInfoTools.getContentAsHTML(editinfo));
                                    editinfo.setUserON(em.merge(OPDE.getLogin().getUser()));
                                    em.getTransaction().commit();

                                    valuecache.get(resInfo.getResInfoType()).remove(resInfo);
                                    valuecache.get(editinfo.getResInfoType()).add(editinfo);
                                    Collections.sort(valuecache.get(editinfo.getResInfoType()));
                                    CollapsiblePane myCP = createCP4Type(editinfo.getResInfoType());
                                    buildPanel();
                                    GUITools.flashBackground(myCP, Color.YELLOW, 2);
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
            // Only active ones can be edited, and only by the same user that started it or the admin.
            btnEdit.setEnabled(ResInfoTools.isChangeable(resInfo) && (OPDE.isAdmin() || resInfo.getUserON().equals(OPDE.getLogin().getUser())));
            pnlMenu.add(btnEdit);
        }

//        btnDelete.setEnabled(!prescription.isClosed() && (!prescriptionHasBeenUsedAlready || OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.MANAGER)));
        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.DELETE)) {
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
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    ResInfo newinfo = em.merge(resInfo);
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                    em.lock(newinfo, LockModeType.OPTIMISTIC);
                                    em.remove(newinfo);
                                    em.getTransaction().commit();

                                    valuecache.get(resInfo.getResInfoType()).remove(resInfo);
                                    createCP4Type(newinfo.getResInfoType());
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
            pnlMenu.add(btnDelete);
        }
        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE)) {
            /***
             *      ____           _           _
             *     |  _ \ ___ _ __(_) ___   __| |
             *     | |_) / _ \ '__| |/ _ \ / _` |
             *     |  __/  __/ |  | | (_) | (_| |
             *     |_|   \___|_|  |_|\___/ \__,_|
             *
             */
            final JButton btnChangePeriod = GUITools.createHyperlinkButton(internalClassID + ".btnChangePeriod.tooltip", SYSConst.icon22changePeriod, null);
            btnChangePeriod.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnChangePeriod.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (resInfo.isSingleIncident()) {
                        final JidePopup popup = new JidePopup();
                        PnlPIT pnlPIT = new PnlPIT(resInfo.getFrom(), new Closure() {
                            @Override
                            public void execute(Object o) {
                                popup.hidePopup();
                                if (o != null) {
                                    EntityManager em = OPDE.createEM();
                                    try {
                                        em.getTransaction().begin();
                                        ResInfo editinfo = em.merge(resInfo);
                                        em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                        em.lock(editinfo, LockModeType.OPTIMISTIC);
                                        Date date = (Date) o;
                                        editinfo.setFrom(date);
                                        editinfo.setTo(date);
                                        em.getTransaction().commit();

                                        valuecache.get(resInfo.getResInfoType()).remove(resInfo);
                                        valuecache.get(editinfo.getResInfoType()).add(editinfo);
                                        Collections.sort(valuecache.get(editinfo.getResInfoType()));
                                        createCP4Type(editinfo.getResInfoType());
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
                        popup.setMovable(false);
                        popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));

                        popup.setOwner(pnlMenu);
                        popup.removeExcludedComponent(pnlMenu);
                        popup.getContentPane().add(pnlPIT);
                        popup.setDefaultFocusComponent(pnlPIT);
                        GUITools.showPopup(popup, SwingConstants.WEST);
                    } else {
                        final JidePopup popup = new JidePopup();
                        Pair<Date, Date> expansion = ResInfoTools.getMinMaxExpansion(resInfo, valuecache.get(resInfo.getResInfoType()));
                        PnlPeriod pnlPeriod = new PnlPeriod(expansion.getFirst(), expansion.getSecond(), resInfo.getFrom(), resInfo.getTo(), new Closure() {
                            @Override
                            public void execute(Object o) {
                                popup.hidePopup();
                                if (o != null) {
                                    EntityManager em = OPDE.createEM();
                                    try {
                                        em.getTransaction().begin();
                                        ResInfo editinfo = em.merge(resInfo);
                                        em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                        em.lock(editinfo, LockModeType.OPTIMISTIC);
                                        Pair<Date, Date> period = (Pair<Date, Date>) o;
                                        editinfo.setFrom(period.getFirst());
                                        editinfo.setTo(period.getSecond());
                                        editinfo.setUserOFF(editinfo.getTo().equals(SYSConst.DATE_UNTIL_FURTHER_NOTICE) ? null : em.merge(OPDE.getLogin().getUser()));
                                        em.getTransaction().commit();

                                        valuecache.get(resInfo.getResInfoType()).remove(resInfo);
                                        valuecache.get(editinfo.getResInfoType()).add(editinfo);
                                        Collections.sort(valuecache.get(editinfo.getResInfoType()));
                                        createCP4Type(editinfo.getResInfoType());
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
                        popup.setMovable(false);
                        popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));

                        popup.setOwner(pnlMenu);
                        popup.removeExcludedComponent(pnlMenu);
                        popup.getContentPane().add(pnlPeriod);
                        popup.setDefaultFocusComponent(pnlPeriod);
                        GUITools.showPopup(popup, SwingConstants.WEST);

                    }

                }
            });
            btnChangePeriod.setEnabled(!resInfo.isClosed() && !resInfo.isSingleIncident());
            pnlMenu.add(btnChangePeriod);


            /***
             *      _     _         _____ _ _
             *     | |__ | |_ _ __ |  ___(_) | ___  ___
             *     | '_ \| __| '_ \| |_  | | |/ _ \/ __|
             *     | |_) | |_| | | |  _| | | |  __/\__ \
             *     |_.__/ \__|_| |_|_|   |_|_|\___||___/
             *
             */
            final JButton btnFiles = GUITools.createHyperlinkButton(resInfo.isClosed() ? "misc.btnfiles.tooltip.closed" : "misc.btnfiles.tooltip", SYSConst.icon22attach, null);
            btnFiles.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnFiles.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    // If the closure is null, only attached files can be viewed but no new ones can be attached.
                    Closure closure = null;
                    if (!resInfo.isClosed()) {
                        closure = new Closure() {
                            @Override
                            public void execute(Object o) {
                                EntityManager em = OPDE.createEM();
                                final ResInfo myInfo = em.merge(resInfo);
                                em.refresh(myInfo);
                                em.close();

                                valuecache.get(resInfo.getResInfoType()).remove(resInfo);
                                valuecache.get(myInfo.getResInfoType()).add(myInfo);
                                Collections.sort(valuecache.get(myInfo.getResInfoType()));
                                createInfoPanel(myInfo);

                                buildPanel();
                            }
                        };
                    }
                    new DlgFiles(resInfo, closure);
                }
            });

//            btnFiles.setEnabled(ResInfoTools.isChangeable(resInfo));
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

                                createInfoPanel(myInfo);

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
            btnProcess.setEnabled(ResInfoTools.isChangeable(resInfo));

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

        }
        return pnlMenu;
    }


    @Override
    public void cleanup() {
//        bwinfotypen.clear();
//        categories.clear();
        cpMap.clear();
        cpsInfo.removeAll();
        valuecache.clear();
        if (listInfo != null) {
            listInfo.clear();
        }
    }

    @Override
    public void reload() {
        cleanup();
        listInfo = ResInfoTools.getAll(resident);
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
