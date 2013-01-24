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
import entity.EntityTools;
import entity.files.SYSFilesTools;
import entity.info.*;
import entity.process.*;
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
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.DateMidnight;
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

    private JXSearchField txtSearch;
    private Resident resident;
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;

    private HashMap<String, CollapsiblePane> mapKey2CP;
    private HashMap<ResInfoType, ArrayList<ResInfo>> mapType2InfoList;
    private List<ResInfoCategory> listCategories;
    private ArrayList<ResInfo> listInfo;
    private HashMap<ResInfo, JPanel> mapInfo2Panel;
    private ArrayList<ResInfoCategory> listOfCategoriesWithContent;

    private JideButton btnResDied, btnResMovedOut, btnResIsAway, btnResIsBack;
    private Color[] color1, color2;

    private ResInfoType typeAbsence, typeStartOfStay;

    private JToggleButton tbShowClosedWithOldForms, tbShowClosedWithActiveForms, tbShowEmpty;

    @Override
    public String getInternalClassID() {
        return internalClassID;
    }

    public PnlInfo(Resident resident, JScrollPane jspSearch) {
        this.jspSearch = jspSearch;
        this.resident = resident;
        initComponents();
        initPanel();
        switchResident(resident);
    }

    private void initPanel() {
        mapType2InfoList = new HashMap<ResInfoType, ArrayList<ResInfo>>();
        mapKey2CP = new HashMap<String, CollapsiblePane>();
        mapInfo2Panel = new HashMap<ResInfo, JPanel>();
        listOfCategoriesWithContent = new ArrayList<ResInfoCategory>();
        prepareSearchArea();

        typeAbsence = ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_ABSENCE);
        typeStartOfStay = ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_STAY);
        listCategories = ResInfoCategoryTools.getAll4ResInfo();

        color1 = SYSConst.green1;
        color2 = SYSConst.greyscale;
    }

    @Override
    public void switchResident(Resident res) {
        resident = EntityTools.find(Resident.class, res.getRID());
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (btnResDied != null) btnResDied.setEnabled(resident.isActive());
                if (btnResMovedOut != null) btnResMovedOut.setEnabled(resident.isActive());
                if (btnResIsAway != null)
                    btnResIsAway.setEnabled(resident.isActive() && !ResInfoTools.isAway(resident));
                if (btnResIsBack != null) btnResIsBack.setEnabled(resident.isActive() && ResInfoTools.isAway(resident));
            }
        });

        GUITools.setResidentDisplay(resident);
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
//        initPhase = true;

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
//                        // Eliminate empty listCategories
//                        listCategories = new ArrayList<ResInfoCategory>();
//                        for (final ResInfoCategory kat : ResInfoCategoryTools.getAll4ResInfo()) {
//                            if (!ResInfoTypeTools.getByCat(kat).isEmpty()) {
//                                listCategories.add(kat);
//                            }
//                        }
//
//                        // create tabs
//                        for (final ResInfoCategory kat : listCategories) {
//                            progress++;
//                            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, listCategories.size()));
//
//                            if (!ResInfoTypeTools.getByCat(kat).isEmpty()) {
//                                tabKat.addTab(kat.getText(), new JScrollPane(createCollapsiblePanesFor(kat)));
//                            } else {
//                                listCategories.remove(kat);
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
//                    btnResDied.setEnabled(resident.isActive());
//                    btnResMovedOut.setEnabled(resident.isActive());
//                    btnResIsAway.setEnabled(resident.isActive() && !ResInfoTools.isAway(resident));
//                    btnResIsBack.setEnabled(resident.isActive() && ResInfoTools.isAway(resident));
//                    initPhase = false;
//                    OPDE.getDisplayManager().setProgressBarMessage(null);
//                    OPDE.getMainframe().setBlocked(false);
//                }
//            };
//            worker.execute();

        } else {
            if (mapType2InfoList.isEmpty()) {
                for (ResInfo resInfo : listInfo) {
                    if (!mapType2InfoList.containsKey(resInfo.getResInfoType())) {
                        mapType2InfoList.put(resInfo.getResInfoType(), new ArrayList<ResInfo>());
                    }
                    mapType2InfoList.get(resInfo.getResInfoType()).add(resInfo);
                }
            }

            for (ResInfoCategory cat : listCategories) {
                createCP4(cat);
            }
            buildPanel();
        }
//        initPhase = false;

    }

    private CollapsiblePane createCP4(final ResInfoCategory cat) {
        /***
         *                          _        ____ ____  _  _      ____      _
         *       ___ _ __ ___  __ _| |_ ___ / ___|  _ \| || |    / ___|__ _| |_ ___  __ _  ___  _ __ _   _
         *      / __| '__/ _ \/ _` | __/ _ \ |   | |_) | || |_  | |   / _` | __/ _ \/ _` |/ _ \| '__| | | |
         *     | (__| | |  __/ (_| | ||  __/ |___|  __/|__   _| | |__| (_| | ||  __/ (_| | (_) | |  | |_| |
         *      \___|_|  \___|\__,_|\__\___|\____|_|      |_|    \____\__,_|\__\___|\__, |\___/|_|   \__, |
         *                                                                          |___/            |___/
         */
        final String keyCat = cat.getID() + ".xcategory";
        if (!mapKey2CP.containsKey(keyCat)) {
            mapKey2CP.put(keyCat, new CollapsiblePane());
            try {
                mapKey2CP.get(keyCat).setCollapsed(true);
            } catch (PropertyVetoException e) {
                // Bah!
            }

        }
        final CollapsiblePane cpCat = mapKey2CP.get(keyCat);

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

        if (listOfCategoriesWithContent.contains(cat)) {
            cptitle.getButton().setIcon(SYSConst.icon22ledGreenOn);
        } else {
            cptitle.getButton().setIcon(SYSConst.icon22ledGreenOff);
        }

        cpCat.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                JPanel pnlContent = new JPanel(new VerticalLayout());
                for (ResInfoType type : ResInfoTypeTools.getByCat(cat)) {

                    if (tbShowEmpty.isSelected() || mapType2InfoList.containsKey(type)){
                        pnlContent.add(createCP4(type));
                    }

//                    if (type.getType() != ResInfoTypeTools.TYPE_OLD || mapType2InfoList.containsKey(type)) {
//                        pnlContent.add(createCP4(type));
//                    }
                }
                cpCat.setContentPane(pnlContent);
            }
        });

        if (!cpCat.isCollapsed()) {
            JPanel pnlContent = new JPanel(new VerticalLayout());
            for (ResInfoType type : ResInfoTypeTools.getByCat(cat)) {
                pnlContent.add(createCP4(type));
            }
            cpCat.setContentPane(pnlContent);
        }

        return cpCat;
    }

    private JPanel createPanel(final ResInfo resInfo) {
        String title = "<html><table border=\"0\">" +
                "<tr valign=\"top\">" +
                "<td width=\"280\" align=\"left\">" + resInfo.getPITAsHTML() + "</td>" +
                "<td width=\"500\" align=\"left\">" +
                resInfo.getHtml() +
                (SYSTools.catchNull(resInfo.getText()).trim().isEmpty() ? "" : "<b>" + OPDE.lang.getString("misc.msg.comment") + ": </b><p>" + resInfo.getText().trim() + "</p>") +
                "</td>" +
                "</table>" +
                "</html>";
        DefaultCPTitle cptitle = new DefaultCPTitle(title, null);

        if (!resInfo.getAttachedFilesConnections().isEmpty()) {
            /***
             *      _     _         _____ _ _
             *     | |__ | |_ _ __ |  ___(_) | ___  ___
             *     | '_ \| __| '_ \| |_  | | |/ _ \/ __|
             *     | |_) | |_| | | |  _| | | |  __/\__ \
             *     |_.__/ \__|_| |_|_|   |_|_|\___||___/
             *
             */
            final JButton btnFiles = new JButton(Integer.toString(resInfo.getAttachedFilesConnections().size()), SYSConst.icon22greenStar);
            btnFiles.setToolTipText(OPDE.lang.getString("misc.btnfiles.tooltip"));
            btnFiles.setForeground(Color.BLUE);
            btnFiles.setHorizontalTextPosition(SwingUtilities.CENTER);
            btnFiles.setFont(SYSConst.ARIAL18BOLD);
            btnFiles.setPressedIcon(SYSConst.icon22Pressed);
            btnFiles.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnFiles.setAlignmentY(Component.TOP_ALIGNMENT);
            btnFiles.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnFiles.setContentAreaFilled(false);
            btnFiles.setBorder(null);

            btnFiles.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    Closure fileHandleClosure = new Closure() {
                        @Override
                        public void execute(Object o) {
                            EntityManager em = OPDE.createEM();
                            final ResInfo myInfo = em.merge(resInfo);
                            em.refresh(myInfo);
                            em.close();


                            mapType2InfoList.get(resInfo.getResInfoType()).remove(resInfo);
                            mapType2InfoList.get(myInfo.getResInfoType()).add(myInfo);
                            Collections.sort(mapType2InfoList.get(myInfo.getResInfoType()));
                            final CollapsiblePane myCP = createCP4(myInfo.getResInfoType());
                            buildPanel();

                            // TODO: KNOWN_ISSUE: After attaching a file the green star appears only after the CP has been collapsed and expanded again.
                        }
                    };
                    new DlgFiles(resInfo, fileHandleClosure);
                }
            });
            btnFiles.setEnabled(OPDE.isFTPworking());
            cptitle.getRight().add(btnFiles);
        }


        if (!resInfo.getAttachedQProcessConnections().isEmpty()) {
            /***
             *      _     _         ____
             *     | |__ | |_ _ __ |  _ \ _ __ ___   ___ ___  ___ ___
             *     | '_ \| __| '_ \| |_) | '__/ _ \ / __/ _ \/ __/ __|
             *     | |_) | |_| | | |  __/| | | (_) | (_|  __/\__ \__ \
             *     |_.__/ \__|_| |_|_|   |_|  \___/ \___\___||___/___/
             *
             */
            final JButton btnProcess = new JButton(Integer.toString(resInfo.getAttachedQProcessConnections().size()), SYSConst.icon22redStar);
            btnProcess.setToolTipText(OPDE.lang.getString("misc.btnprocess.tooltip"));
            btnProcess.setForeground(Color.YELLOW);
            btnProcess.setHorizontalTextPosition(SwingUtilities.CENTER);
            btnProcess.setFont(SYSConst.ARIAL18BOLD);
            btnProcess.setPressedIcon(SYSConst.icon22Pressed);
            btnProcess.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnProcess.setAlignmentY(Component.TOP_ALIGNMENT);
            btnProcess.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnProcess.setContentAreaFilled(false);
            btnProcess.setBorder(null);
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
                                ResInfo myInfo = em.merge(resInfo);
                                em.lock(myInfo, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                                ArrayList<SYSINF2PROCESS> attached = new ArrayList<SYSINF2PROCESS>(myInfo.getAttachedQProcessConnections());
                                for (SYSINF2PROCESS linkObject : attached) {
                                    if (unassigned.contains(linkObject.getQProcess())) {
                                        linkObject.getQProcess().getAttachedNReportConnections().remove(linkObject);
                                        linkObject.getResInfo().getAttachedQProcessConnections().remove(linkObject);
                                        em.merge(new PReport(OPDE.lang.getString(PReportTools.PREPORT_TEXT_REMOVE_ELEMENT) + ": " + myInfo.getTitle() + " ID: " + myInfo.getID(), PReportTools.PREPORT_TYPE_REMOVE_ELEMENT, linkObject.getQProcess()));
                                        em.remove(linkObject);
                                    }
                                }
                                attached.clear();

                                for (QProcess qProcess : assigned) {
                                    java.util.List<QProcessElement> listElements = qProcess.getElements();
                                    if (!listElements.contains(myInfo)) {
                                        QProcess myQProcess = em.merge(qProcess);
                                        SYSINF2PROCESS myLinkObject = em.merge(new SYSINF2PROCESS(myQProcess, myInfo));
                                        em.merge(new PReport(OPDE.lang.getString(PReportTools.PREPORT_TEXT_ASSIGN_ELEMENT) + ": " + myInfo.getTitle() + " ID: " + myInfo.getID(), PReportTools.PREPORT_TYPE_ASSIGN_ELEMENT, myQProcess));
                                        qProcess.getAttachedResInfoConnections().add(myLinkObject);
                                        myInfo.getAttachedQProcessConnections().add(myLinkObject);
                                    }
                                }

                                em.getTransaction().commit();

                                mapType2InfoList.get(resInfo.getResInfoType()).remove(resInfo);
                                mapType2InfoList.get(myInfo.getResInfoType()).add(myInfo);
                                Collections.sort(mapType2InfoList.get(myInfo.getResInfoType()));
                                final CollapsiblePane myCP = createCP4(myInfo.getResInfoType());
                                buildPanel();


                            } catch (OptimisticLockException ole) {
                                if (em.getTransaction().isActive()) {
                                    em.getTransaction().rollback();
                                }
                                if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
                                    OPDE.getMainframe().emptyFrame();
                                    OPDE.getMainframe().afterLogin();
                                } else {
                                    reloadDisplay();
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
                    });
                }
            });
            btnProcess.setEnabled(OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID));
            cptitle.getRight().add(btnProcess);
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
        cptitle.getButton().setVerticalTextPosition(SwingConstants.TOP);

        if (resInfo.isClosed()) {
            cptitle.getButton().setIcon(resInfo.getResInfoType().isObsolete() ? SYSConst.icon22stopSignGray : SYSConst.icon22stopSign);
//            cptitle.getAdditionalIconPanel().add(new JLabel(SYSConst.icon22stopSign));
        }

        mapInfo2Panel.put(resInfo, cptitle.getMain());

        return cptitle.getMain();
    }

    private JPanel createPanel(final ResInfoType type) {
        final JPanel infoPanel = new JPanel(new VerticalLayout());
        infoPanel.setOpaque(false);

        if (!mapType2InfoList.containsKey(type)) {
            mapType2InfoList.put(type, ResInfoTools.getByResidentAndType(resident, type));
        }

        int i = 0; // for zebra pattern
        for (final ResInfo resInfo : mapType2InfoList.get(type)) {
            JPanel cp = createPanel(resInfo);
            if (i % 2 == 0) {
                cp.setBackground(Color.WHITE);
            }
            i++;
            infoPanel.add(cp);
        }

        return infoPanel;
    }


    private CollapsiblePane createCP4(final ResInfoType type) {
        /***
         *                          _        ____ ____  _  _     _____
         *       ___ _ __ ___  __ _| |_ ___ / ___|  _ \| || |   |_   _|   _ _ __   ___
         *      / __| '__/ _ \/ _` | __/ _ \ |   | |_) | || |_    | || | | | '_ \ / _ \
         *     | (__| | |  __/ (_| | ||  __/ |___|  __/|__   _|   | || |_| | |_) |  __/
         *      \___|_|  \___|\__,_|\__\___|\____|_|      |_|     |_| \__, | .__/ \___|
         *                                                            |___/|_|
         */
        final String keyType = type.getID() + ".xtype";
        if (!mapKey2CP.containsKey(keyType)) {
            mapKey2CP.put(keyType, new CollapsiblePane());
            try {
                mapKey2CP.get(keyType).setCollapsed(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
        final CollapsiblePane cpType = mapKey2CP.get(keyType);

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

        if (type.getType() != ResInfoTypeTools.TYPE_ABSENCE && type.getType() != ResInfoTypeTools.TYPE_STAY && type.getType() != ResInfoTypeTools.TYPE_OLD) {
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
                                    final ResInfo newinfo = em.merge((ResInfo) o);
                                    if (newinfo.getResInfoType().getType() != ResInfoTypeTools.TYPE_DIAGNOSIS) {
                                        newinfo.setHtml(ResInfoTools.getContentAsHTML(newinfo));
                                    }
                                    em.getTransaction().commit();

                                    if (!mapType2InfoList.containsKey(newinfo.getResInfoType())) {
                                        mapType2InfoList.put(newinfo.getResInfoType(), new ArrayList<ResInfo>());
                                    }
                                    mapType2InfoList.get(newinfo.getResInfoType()).add(newinfo);
                                    Collections.sort(mapType2InfoList.get(newinfo.getResInfoType()));
                                    createCP4(newinfo.getResInfoType());

                                    if (newinfo.getResInfoType().isAlertType()) {
                                        GUITools.setResidentDisplay(resident);
                                    }

                                    if (mapKey2CP.get(keyType).isCollapsed()) {
                                        try {
                                            mapKey2CP.get(keyType).setCollapsed(false);
                                        } catch (PropertyVetoException e1) {
                                            // BAH!
                                        }
                                    }
                                    GUITools.scroll2show(jspInfo, mapKey2CP.get(keyType), cpsInfo, new Closure() {
                                        @Override
                                        public void execute(Object o) {
                                            GUITools.flashBackground(mapInfo2Panel.get(newinfo), Color.YELLOW, 2);
                                        }
                                    });
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
            btnAdd.setEnabled(type.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_NOCONSTRAINTS || type.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_SINGLE_INCIDENTS || !mapType2InfoList.containsKey(type) || mapType2InfoList.get(type).isEmpty() || containsOnlyClosedInfos(type));
            cptitle.getRight().add(btnAdd);
        }


        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.PRINT, internalClassID)) {
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
                    if (mapType2InfoList.containsKey(type) && !mapType2InfoList.get(type).isEmpty()) {
                        String html = "";
                        html += "<h3 id=\"fonth2\" >" + ResidentTools.getLabelText(resident) + "</h2>\n";

                        html += type.getType() == ResInfoTypeTools.TYPE_BIOHAZARD ? SYSConst.html_48x48_biohazard : "";
                        html += type.getType() == ResInfoTypeTools.TYPE_DIABETES ? SYSConst.html_48x48_diabetes : "";
                        html += type.getType() == ResInfoTypeTools.TYPE_ALLERGY ? SYSConst.html_48x48_allergy : "";
                        html += type.getType() == ResInfoTypeTools.TYPE_WARNING ? SYSConst.html_48x48_warning : "";

                        html += ResInfoTools.getResInfosAsHTML(mapType2InfoList.get(type), true, null);
                        SYSFilesTools.print(html, true);
                    }
                }
            });
            cptitle.getRight().add(btnPrint);
            btnPrint.setEnabled(mapType2InfoList.containsKey(type) && !mapType2InfoList.get(type).isEmpty());
        }


        cpType.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                cpType.setContentPane(createPanel(type));
            }
        });


        if (!cpType.isCollapsed()) {
            cpType.setContentPane(createPanel(type));
        }


        boolean empty = !mapType2InfoList.containsKey(type) || mapType2InfoList.get(type).isEmpty();
        if (!empty) {
            if (containsOnlyClosedInfos(type))
                cptitle.getAdditionalIconPanel().add(new JLabel(type.isObsolete() ? SYSConst.icon22stopSignGray : SYSConst.icon22stopSign));
            else
                cptitle.getAdditionalIconPanel().add(new JLabel(SYSConst.icon22infogreen2));
        }


        if (type.getType() == ResInfoTypeTools.TYPE_ALLERGY)
            cptitle.getAdditionalIconPanel().add(new JLabel(SYSConst.icon22allergy));
        if (type.getType() == ResInfoTypeTools.TYPE_DIABETES)
            cptitle.getAdditionalIconPanel().add(new JLabel(SYSConst.icon22diabetes));
        if (type.getType() == ResInfoTypeTools.TYPE_BIOHAZARD)
            cptitle.getAdditionalIconPanel().add(new JLabel(SYSConst.icon22biohazard));
        if (type.getType() == ResInfoTypeTools.TYPE_WARNING)
            cptitle.getAdditionalIconPanel().add(new JLabel(SYSConst.icon22warning));


        cptitle.getButton().setToolTipText(getTooltip(type));

        cpType.setTitleLabelComponent(cptitle.getMain());
        cpType.setSlidingDirection(SwingConstants.SOUTH);
        cpType.setBackground(getColor(type.getResInfoCat())[SYSConst.light4]);
        cpType.setOpaque(true);
        cpType.setHorizontalAlignment(SwingConstants.LEADING);

        return cpType;
    }


    private Color[] getColor(ResInfoCategory cat) {
        if (listCategories.indexOf(cat) % 2 == 0) {
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


        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, internalClassID) || OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID) || OPDE.getAppInfo().isAllowedTo(InternalClassACL.PRINT, internalClassID)) {
            JPanel cmdPanel = new JPanel();
            CollapsiblePane commandPane = new CollapsiblePane(OPDE.lang.getString(internalClassID + ".functions"));
            cmdPanel.setLayout(new VerticalLayout());
            GUITools.addAllComponents(cmdPanel, addCommands());
            commandPane.setContentPane(cmdPanel);
            searchPanes.add(commandPane);
            try {
                commandPane.setCollapsed(true);
            } catch (PropertyVetoException e) {
                //BAH!
            }
        }

        /***
         *      ____       _       _
         *     |  _ \ _ __(_)_ __ | |_
         *     | |_) | '__| | '_ \| __|
         *     |  __/| |  | | | | | |_
         *     |_|   |_|  |_|_| |_|\__|
         *
         */
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.PRINT, internalClassID)) {
            final JButton btnPrint = GUITools.createHyperlinkButton(OPDE.lang.getString("misc.commands.print"), SYSConst.icon22print2, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    String html = "";
                    html += "<h1 id=\"fonth1\" >" + OPDE.lang.getString("nursingrecords.info");
                    html += " " + OPDE.lang.getString("misc.msg.for") + " " + ResidentTools.getLabelText(resident) + "</h1>\n";

                    for (ResInfoCategory cat : listCategories) {
                        html += "<h2 id=\"fonth2\" >" + cat.getText() + "</h2>\n";
                        for (ResInfoType type : ResInfoTypeTools.getByCat(cat)) {
                            if (mapType2InfoList.containsKey(type) && !mapType2InfoList.get(type).isEmpty()) {
                                html += "<h3 id=\"fonth3\" >" + type.getShortDescription() + "</h3>\n";

                                html += type.getType() == ResInfoTypeTools.TYPE_BIOHAZARD ? SYSConst.html_48x48_biohazard : "";
                                html += type.getType() == ResInfoTypeTools.TYPE_DIABETES ? SYSConst.html_48x48_diabetes : "";
                                html += type.getType() == ResInfoTypeTools.TYPE_ALLERGY ? SYSConst.html_48x48_allergy : "";
                                html += type.getType() == ResInfoTypeTools.TYPE_WARNING ? SYSConst.html_48x48_warning : "";

                                html += ResInfoTools.getResInfosAsHTML(mapType2InfoList.get(type), true, null);
                            }
                        }
                    }
                    SYSFilesTools.print(html, true);
                }
            });
            btnPrint.setAlignmentX(Component.RIGHT_ALIGNMENT);

            mypanel.add(btnPrint);
        }


        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }


        GUITools.addAllComponents(mypanel, addFilters());
        GUITools.addAllComponents(mypanel, addKey());

        searchPane.setContentPane(mypanel);

        searchPanes.add(searchPane);
        searchPanes.addExpansion();
    }

    private java.util.List<Component> addKey() {
        java.util.List<Component> list = new ArrayList<Component>();
        list.add(new JSeparator());
        list.add(new JLabel(OPDE.lang.getString("misc.msg.key")));
        list.add(new JLabel(OPDE.lang.getString(internalClassID + ".keydescription1"), SYSConst.icon22stopSign, SwingConstants.LEADING));
        list.add(new JLabel(OPDE.lang.getString(internalClassID + ".keydescription7"), SYSConst.icon22stopSignGray, SwingConstants.LEADING));
        list.add(new JLabel(OPDE.lang.getString(internalClassID + ".keydescription2"), SYSConst.icon22infogreen2, SwingConstants.LEADING));
        list.add(new JLabel(OPDE.lang.getString(internalClassID + ".keydescription3"), SYSConst.icon22ledGreenOn, SwingConstants.LEADING));
        list.add(new JLabel(OPDE.lang.getString(internalClassID + ".keydescription4"), SYSConst.icon22ledGreenOff, SwingConstants.LEADING));

        return list;
    }

    private List<Component> addCommands() {

        List<Component> list = new ArrayList<Component>();

        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, internalClassID)) {
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
                    if (!resident.isActive()) {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.cantChangeInactiveResident"));
                        return;
                    }
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
                                    GUITools.setResidentDisplay(resident);
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
            btnResMovedOut = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".resident.movedout"), SYSConst.icon22residentGone, null);
            btnResDied = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".resident.died"), SYSConst.icon22residentDied, null);
            btnResDied.addActionListener(new ActionListener() {
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
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                    resident.setStation(null);
                                    ResidentTools.endOfStay(em, em.merge(resident), dod, ResInfoTypeTools.STAY_VALUE_DEAD);
                                    em.getTransaction().commit();
                                    btnResDied.setEnabled(false);
                                    btnResMovedOut.setEnabled(false);
                                    btnResIsAway.setEnabled(false);
                                    btnResIsBack.setEnabled(false);

                                    OPDE.getMainframe().completeRefresh();
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
            btnResDied.setEnabled(resident.isActive());
            list.add(btnResDied);


            //TODO: We need a MOVED IN AGAIN
            /***
             *                                   _               _
             *      _ __ ___   _____   _____  __| |   ___  _   _| |_
             *     | '_ ` _ \ / _ \ \ / / _ \/ _` |  / _ \| | | | __|
             *     | | | | | | (_) \ V /  __/ (_| | | (_) | |_| | |_
             *     |_| |_| |_|\___/ \_/ \___|\__,_|  \___/ \__,_|\__|
             *
             */
            btnResMovedOut.addActionListener(new ActionListener() {
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
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                    resident.setStation(null);

                                    ResidentTools.endOfStay(em, em.merge(resident), dod, ResInfoTypeTools.STAY_VALUE_LEFT);
                                    em.getTransaction().commit();

                                    btnResDied.setEnabled(false);
                                    btnResMovedOut.setEnabled(false);
                                    btnResIsAway.setEnabled(false);
                                    btnResIsBack.setEnabled(false);

                                    OPDE.getMainframe().completeRefresh();
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
            btnResMovedOut.setEnabled(resident.isActive());
            list.add(btnResMovedOut);
        }


        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
            btnResIsAway = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".resident.isaway"), SYSConst.icon22residentAbsent, null);
            /***
             *      _          _
             *     (_)___     / \__      ____ _ _   _
             *     | / __|   / _ \ \ /\ / / _` | | | |
             *     | \__ \  / ___ \ V  V / (_| | |_| |
             *     |_|___/ /_/   \_\_/\_/ \__,_|\__, |
             *                                  |___/
             */
            btnResIsAway.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    final JidePopup popup = new JidePopup();
                    PnlAway pnlAway = new PnlAway(new ResInfo(typeAbsence, resident), new Closure() {
                        @Override
                        public void execute(Object o) {
                            popup.hidePopup();
                            if (o != null) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    Resident myResident = em.merge(resident);
                                    em.lock(myResident, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                                    ResInfo absence = em.merge((ResInfo) o);
                                    em.getTransaction().commit();
                                    resident = myResident;

                                    btnResIsAway.setEnabled(false);
                                    btnResIsBack.setEnabled(true);

                                    if (!mapType2InfoList.containsKey(typeAbsence)) {
                                        mapType2InfoList.put(typeAbsence, new ArrayList<ResInfo>());
                                    }
                                    mapType2InfoList.get(typeAbsence).add(absence);
                                    Collections.sort(mapType2InfoList.get(typeAbsence));
                                    createCP4(typeAbsence);
                                    buildPanel();

                                    GUITools.setResidentDisplay(resident);
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

                    popup.setOwner(btnResIsAway);
                    popup.removeExcludedComponent(btnResIsAway);
                    popup.getContentPane().add(pnlAway);
                    popup.setDefaultFocusComponent(pnlAway);
                    GUITools.showPopup(popup, SwingConstants.NORTH_EAST);
                }
            });
            btnResIsAway.setEnabled(resident.isActive() && !ResInfoTools.isAway(resident));
            list.add(btnResIsAway);

            btnResIsBack = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".resident.isback"), SYSConst.icon22residentBack, null);
            /***
             *      _       ____             _
             *     (_)___  | __ )  __ _  ___| | __
             *     | / __| |  _ \ / _` |/ __| |/ /
             *     | \__ \ | |_) | (_| | (__|   <
             *     |_|___/ |____/ \__,_|\___|_|\_\
             *
             */
            btnResIsBack.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        Resident myResident = em.merge(resident);
                        em.lock(myResident, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                        ResInfo lastabsence = em.merge(ResInfoTools.getLastResinfo(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_ABSENCE)));
                        em.lock(lastabsence, LockModeType.OPTIMISTIC);
                        lastabsence.setTo(new Date());
                        lastabsence.setUserOFF(em.merge(OPDE.getLogin().getUser()));
                        em.getTransaction().commit();
                        resident = myResident;

                        btnResIsAway.setEnabled(true);
                        btnResIsBack.setEnabled(false);

                        mapType2InfoList.get(typeAbsence).remove(lastabsence);
                        mapType2InfoList.get(typeAbsence).add(lastabsence);
                        Collections.sort(mapType2InfoList.get(typeAbsence));
                        createCP4(typeAbsence);
                        buildPanel();

                        GUITools.setResidentDisplay(resident);
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
            btnResIsBack.setEnabled(resident.isActive() && !ResInfoTools.isAway(resident));
            list.add(btnResIsBack);
        }

        return list;
    }

    private boolean containsOnlyClosedInfos(final ResInfoType type) {
        boolean containsOnlyClosedInfos = true;
        if (mapType2InfoList.containsKey(type)) {
            for (ResInfo info : mapType2InfoList.get(type)) {
                containsOnlyClosedInfos = info.isClosed();
                if (!containsOnlyClosedInfos) {
                    break;
                }
            }
        }
        return containsOnlyClosedInfos;
    }

    private ArrayList<ResInfo> search(String pattern) {
        pattern = pattern.toLowerCase();
        ArrayList<ResInfo> hits = new ArrayList<ResInfo>();
        for (ResInfo info : listInfo) {
            boolean hit = false;
            if (!hit) hit = SYSTools.catchNull(info.getText()).toLowerCase().indexOf(pattern) > 0;
            if (!hit) hit = SYSTools.catchNull(info.getHtml()).toLowerCase().indexOf(pattern) > 0;
            if (!hit) hit = SYSTools.catchNull(info.getProperties()).toLowerCase().indexOf(pattern) > 0;
            if (!hit)
                hit = SYSTools.catchNull(info.getResInfoType().getShortDescription()).toLowerCase().indexOf(pattern) > 0;
            if (!hit)
                hit = SYSTools.catchNull(info.getResInfoType().getLongDescription()).toLowerCase().indexOf(pattern) > 0;
            if (hit) hits.add(info);
        }
        return hits;
    }

    private List<Component> addFilters() {
        List<Component> list = new ArrayList<Component>();

        txtSearch = new JXSearchField(OPDE.lang.getString("misc.msg.searchphrase"));
        txtSearch.setFont(SYSConst.ARIAL14);
        txtSearch.setInstantSearchDelay(750);
        txtSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (SYSTools.catchNull(txtSearch.getText()).trim().length() > 3) {

                    ArrayList<ResInfo> searchResults = search(txtSearch.getText().trim());

                    if (searchResults.isEmpty()) {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.searchempty"));
                    } else {
                        SYSFilesTools.print(SYSConst.html_h2(ResidentTools.getLabelText(resident)) + ResInfoTools.getResInfosAsHTML(searchResults, true, txtSearch.getText().trim()), false);
                    }
                }
            }
        });
        list.add(txtSearch);

        tbShowEmpty = GUITools.getNiceToggleButton(internalClassID+".tbShowEmpty");
        tbShowEmpty.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                reload();
            }
        });
        tbShowEmpty.setHorizontalAlignment(SwingConstants.LEFT);
        list.add(tbShowEmpty);

        tbShowClosedWithActiveForms = GUITools.getNiceToggleButton(internalClassID + ".tbShowClosedWithActiveForms");
        tbShowClosedWithActiveForms.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                reload();
            }
        });
        tbShowClosedWithActiveForms.setHorizontalAlignment(SwingConstants.LEFT);
        list.add(tbShowClosedWithActiveForms);

        tbShowClosedWithOldForms = GUITools.getNiceToggleButton(internalClassID + ".tbShowClosedWithOldForms");
        tbShowClosedWithOldForms.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                reload();
            }
        });
        tbShowClosedWithOldForms.setHorizontalAlignment(SwingConstants.LEFT);
        list.add(tbShowClosedWithOldForms);

        return list;
    }

    private Icon getContentIcon(ResInfoType type) {
        boolean empty = !mapType2InfoList.containsKey(type) || mapType2InfoList.get(type).isEmpty();
        if (empty) {
            return null;
        }
        if (containsOnlyClosedInfos(type)) {
            return SYSConst.icon22stopSign;
        }
        return SYSConst.icon22infogreen2;
    }


    private String getTooltip(ResInfoType type) {
        if (type.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_SINGLE_INCIDENTS) {
            return OPDE.lang.getString(internalClassID + ".dlg.interval_single_incidents");
        }
        if (type.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_BYDAY) {
            return OPDE.lang.getString(internalClassID + ".dlg.interval_byday");
        }
        if (type.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_NOCONSTRAINTS) {
            return OPDE.lang.getString(internalClassID + ".dlg.interval_noconstraints");
        }
        return OPDE.lang.getString(internalClassID + ".dlg.interval_bysecond");
    }


    private ResInfo getLastStay() {
        ResInfo lastStay = null;
        if (mapType2InfoList.containsKey(typeStartOfStay) && !mapType2InfoList.get(typeStartOfStay).isEmpty()) {
            int size = mapType2InfoList.get(typeStartOfStay).size();
            lastStay = mapType2InfoList.get(typeStartOfStay).get(size - 1);
        }
        return lastStay;
    }


    private JPanel getMenu(final ResInfo resInfo) {

        final JPanel pnlMenu = new JPanel(new VerticalLayout());

        if (resInfo.getResInfoType().getType() != ResInfoTypeTools.TYPE_ABSENCE && resInfo.getResInfoType().getType() != ResInfoTypeTools.TYPE_STAY) {

            if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
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

                                        mapType2InfoList.get(resInfo.getResInfoType()).remove(resInfo);
                                        mapType2InfoList.get(oldinfo.getResInfoType()).add(oldinfo);
                                        mapType2InfoList.get(newinfo.getResInfoType()).add(newinfo);
                                        Collections.sort(mapType2InfoList.get(newinfo.getResInfoType()));
                                        CollapsiblePane myCP = createCP4(newinfo.getResInfoType());
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
                btnChange.setEnabled(resInfo.getResInfoType().getType() != ResInfoTypeTools.TYPE_DIAGNOSIS && resInfo.getResInfoType().getType() != ResInfoTypeTools.TYPE_OLD && !resInfo.isClosed() && !resInfo.isSingleIncident() && !resInfo.isNoConstraints());
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

                                        mapType2InfoList.get(resInfo.getResInfoType()).remove(resInfo);
                                        mapType2InfoList.get(newinfo.getResInfoType()).add(newinfo);
                                        Collections.sort(mapType2InfoList.get(newinfo.getResInfoType()));
                                        CollapsiblePane myCP = createCP4(newinfo.getResInfoType());

                                        if (newinfo.getResInfoType().isAlertType()) {
                                            GUITools.setResidentDisplay(resident);
                                        }

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

                                        mapType2InfoList.get(resInfo.getResInfoType()).remove(resInfo);
                                        mapType2InfoList.get(editinfo.getResInfoType()).add(editinfo);
                                        Collections.sort(mapType2InfoList.get(editinfo.getResInfoType()));
                                        CollapsiblePane myCP = createCP4(editinfo.getResInfoType());
                                        buildPanel();

                                        GUITools.flashBackground(mapInfo2Panel.get(editinfo), Color.YELLOW, 2);
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
                btnEdit.setEnabled(ResInfoTools.isEditable(resInfo) && (OPDE.isAdmin() ||
                        (resInfo.getUserON().equals(OPDE.getLogin().getUser()) && new DateMidnight(resInfo.getFrom()).equals(new DateMidnight()))  // The same user only on the same day.
                ));
                pnlMenu.add(btnEdit);
            }


            if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.DELETE, internalClassID)) {
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

                                        mapType2InfoList.get(resInfo.getResInfoType()).remove(resInfo);
                                        createCP4(newinfo.getResInfoType());
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
            if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
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

                                            mapType2InfoList.get(resInfo.getResInfoType()).remove(resInfo);
                                            mapType2InfoList.get(editinfo.getResInfoType()).add(editinfo);
                                            Collections.sort(mapType2InfoList.get(editinfo.getResInfoType()));
                                            createCP4(editinfo.getResInfoType());
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
                            Pair<Date, Date> expansion = ResInfoTools.getMinMaxExpansion(resInfo, mapType2InfoList.get(resInfo.getResInfoType()));
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

                                            mapType2InfoList.get(resInfo.getResInfoType()).remove(resInfo);
                                            mapType2InfoList.get(editinfo.getResInfoType()).add(editinfo);
                                            Collections.sort(mapType2InfoList.get(editinfo.getResInfoType()));
                                            createCP4(editinfo.getResInfoType());
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
                btnChangePeriod.setEnabled(ResInfoTools.isEditable(resInfo) && !resInfo.isSingleIncident()
                        && (OPDE.isAdmin() ||
                        (resInfo.getUserON().equals(OPDE.getLogin().getUser()) && new DateMidnight(resInfo.getFrom()).equals(new DateMidnight()))  // The same user only on the same day.
                ));
//            btnChangePeriod.setEnabled(!resInfo.isClosed() && !resInfo.isSingleIncident());
                pnlMenu.add(btnChangePeriod);

                pnlMenu.add(new JSeparator());
            }
        }
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
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
                    if (!resInfo.isClosed() && resInfo.getResInfoType().getType() != ResInfoTypeTools.TYPE_OLD) {
                        closure = new Closure() {
                            @Override
                            public void execute(Object o) {
                                EntityManager em = OPDE.createEM();
                                final ResInfo myInfo = em.merge(resInfo);
                                em.refresh(myInfo);
                                em.close();

                                mapType2InfoList.get(resInfo.getResInfoType()).remove(resInfo);
                                mapType2InfoList.get(myInfo.getResInfoType()).add(myInfo);
                                Collections.sort(mapType2InfoList.get(myInfo.getResInfoType()));
                                createPanel(myInfo);

                                buildPanel();
                            }
                        };
                    }
                    new DlgFiles(resInfo, closure);
                }
            });
            btnFiles.setEnabled(OPDE.isFTPworking());
            pnlMenu.add(btnFiles);


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

                                ArrayList<SYSINF2PROCESS> attached = new ArrayList<SYSINF2PROCESS>(myInfo.getAttachedQProcessConnections());
                                for (SYSINF2PROCESS linkObject : attached) {
                                    if (unassigned.contains(linkObject.getQProcess())) {
                                        linkObject.getQProcess().getAttachedNReportConnections().remove(linkObject);
                                        linkObject.getResInfo().getAttachedQProcessConnections().remove(linkObject);
                                        em.merge(new PReport(OPDE.lang.getString(PReportTools.PREPORT_TEXT_REMOVE_ELEMENT) + ": " + myInfo.getTitle() + " ID: " + myInfo.getID(), PReportTools.PREPORT_TYPE_REMOVE_ELEMENT, linkObject.getQProcess()));
                                        em.remove(linkObject);
                                    }
                                }
                                attached.clear();

                                for (QProcess qProcess : assigned) {
                                    java.util.List<QProcessElement> listElements = qProcess.getElements();
                                    if (!listElements.contains(myInfo)) {
                                        QProcess myQProcess = em.merge(qProcess);
                                        SYSINF2PROCESS myLinkObject = em.merge(new SYSINF2PROCESS(myQProcess, myInfo));
                                        em.merge(new PReport(OPDE.lang.getString(PReportTools.PREPORT_TEXT_ASSIGN_ELEMENT) + ": " + myInfo.getTitle() + " ID: " + myInfo.getID(), PReportTools.PREPORT_TYPE_ASSIGN_ELEMENT, myQProcess));
                                        qProcess.getAttachedResInfoConnections().add(myLinkObject);
                                        myInfo.getAttachedQProcessConnections().add(myLinkObject);
                                    }
                                }

                                em.getTransaction().commit();

                                mapType2InfoList.get(resInfo.getResInfoType()).remove(resInfo);
                                mapType2InfoList.get(myInfo.getResInfoType()).add(myInfo);
                                Collections.sort(mapType2InfoList.get(myInfo.getResInfoType()));

                                createPanel(myInfo);

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
            btnProcess.setEnabled(ResInfoTools.isEditable(resInfo));

            if (!resInfo.getAttachedQProcessConnections().isEmpty()) {
                JLabel lblNum = new JLabel(Integer.toString(resInfo.getAttachedQProcessConnections().size()), SYSConst.icon16redStar, SwingConstants.CENTER);
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
        SYSTools.clear(mapKey2CP);
        SYSTools.clear(mapInfo2Panel);
        cpsInfo.removeAll();
        SYSTools.clear(mapType2InfoList);
        SYSTools.clear(listOfCategoriesWithContent);
        SYSTools.clear(listInfo);
    }

    @Override
    public void reload() {
        cleanup();
        listInfo = ResInfoTools.getAllActive(resident);
        if (tbShowClosedWithActiveForms.isSelected()) {
            listInfo.addAll(ResInfoTools.getClosedWithActiveForms(resident));
        }
        if (tbShowClosedWithOldForms.isSelected()) {
            listInfo.addAll(ResInfoTools.getClosedWithOldForms(resident));
        }

        listOfCategoriesWithContent = ResInfoTools.getCategories(listInfo);
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
        for (ResInfoCategory cat : listCategories) {
            cpsInfo.add(mapKey2CP.get(cat.getID() + ".xcategory"));
        }
        cpsInfo.addExpansion();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jspInfo;
    private CollapsiblePanes cpsInfo;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
