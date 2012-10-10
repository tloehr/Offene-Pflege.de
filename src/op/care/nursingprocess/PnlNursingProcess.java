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
package op.care.nursingprocess;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import entity.info.ResInfoCategory;
import entity.info.ResInfoCategoryTools;
import entity.info.ResInfoTypeTools;
import entity.info.Resident;
import entity.nursingprocess.*;
import entity.system.Unique;
import entity.system.UniqueTools;
import op.OPDE;
import op.system.InternalClassACL;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Period;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.Query;
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
 * @author tloehr
 */
public class PnlNursingProcess extends NursingRecordsPanel {
    public static final String internalClassID = "nursingrecords.nursingprocess";
    private boolean initPhase;

    private Resident resident;
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;


    //    private HashMap<NursingProcess, CollapsiblePane> planungCollapsiblePaneMap;
    private HashMap<ResInfoCategory, ArrayList<NursingProcess>> valuecache;
    private java.util.List<ResInfoCategory> categories;
    private HashMap<String, CollapsiblePane> cpMap;
    private ArrayList<NursingProcess> listNP;

    private JToggleButton tbInactive;
    private JXSearchField txtSearch;

    private Color[] color1, color2;

    /**
     * Creates new form PnlNursingProcess
     */
    public PnlNursingProcess(Resident resident, JScrollPane jspSearch) {
        initPhase = true;
        this.jspSearch = jspSearch;

        initComponents();
        initPanel();
        initPhase = false;

        switchResident(resident);

    }

    private void initPanel() {
//        planungCollapsiblePaneMap = new HashMap<NursingProcess, CollapsiblePane>();
        cpMap = new HashMap<String, CollapsiblePane>();
        valuecache = new HashMap<ResInfoCategory, ArrayList<NursingProcess>>();
        categories = ResInfoCategoryTools.getAll4NP();
        prepareSearchArea();
        color1 = SYSConst.red2;
        color2 = SYSConst.greyscale;
    }

    @Override
    public void cleanup() {
        cpMap.clear();
        valuecache.clear();
        if (listNP != null) {
            listNP.clear();
        }
    }

    @Override
    public void reload() {
        cleanup();
        listNP = NursingProcessTools.getAll(resident);
        Collections.sort(listNP);
        reloadDisplay();
    }

    @Override
    public void switchResident(Resident resident) {
        this.resident = resident;
        GUITools.setBWDisplay(resident);
        reload();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jspPlanung = new JScrollPane();
        cpsPlan = new CollapsiblePanes();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== jspPlanung ========
        {

            //======== cpsPlan ========
            {
                cpsPlan.setLayout(new BoxLayout(cpsPlan, BoxLayout.X_AXIS));
            }
            jspPlanung.setViewportView(cpsPlan);
        }
        add(jspPlanung);
    }// </editor-fold>//GEN-END:initComponents


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
//            cpPlan.removeAll();
//
//            SwingWorker worker = new SwingWorker() {
//
//                @Override
//                protected Object doInBackground() throws Exception {
//                    try {
//                        int progress = 0;
//
//                        if (categories.isEmpty()) {
//                            // Elmininate empty categories
//                            for (final ResInfoCategory kat : ResInfoCategoryTools.getAll4ResInfo()) {
//                                if (!NursingProcessTools.findByKategorieAndBewohner(resident, kat).isEmpty()) {
//                                    categories.add(kat);
//                                }
//                            }
//                        }
//
//                        cpPlan.setLayout(new JideBoxLayout(cpPlan, JideBoxLayout.Y_AXIS));
//                        for (ResInfoCategory kat : categories) {
//                            progress++;
//                            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, categories.size()));
//                            cpPlan.add(createNPPanel(kat));
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

//            cpsPlan.removeAll();

            if (valuecache.isEmpty()) {
                for (NursingProcess np : listNP) {
                    if (!valuecache.containsKey(np.getCategory())) {
                        valuecache.put(np.getCategory(), new ArrayList<NursingProcess>());
                    }
                    valuecache.get(np.getCategory()).add(np);
                }
            }

            for (ResInfoCategory cat : categories) {
                createCP4(cat);
            }

            buildPanel();
        }
        initPhase = false;

    }


    private void buildPanel() {
        cpsPlan.removeAll();
        cpsPlan.setLayout(new JideBoxLayout(cpsPlan, JideBoxLayout.Y_AXIS));
        for (ResInfoCategory cat : categories) {
//            if (cpMap.containsKey(cat.getID() + ".xcategory") || tbInactive.isSelected())
            cpsPlan.add(cpMap.get(cat.getID() + ".xcategory"));
        }
        cpsPlan.addExpansion();
    }


    private CollapsiblePane createCP4(final ResInfoCategory cat) {
        /***
         *                          _        ____ ____  _  _               _
         *       ___ _ __ ___  __ _| |_ ___ / ___|  _ \| || |     ___ __ _| |_ ___  __ _  ___  _ __ _   _
         *      / __| '__/ _ \/ _` | __/ _ \ |   | |_) | || |_   / __/ _` | __/ _ \/ _` |/ _ \| '__| | | |
         *     | (__| | |  __/ (_| | ||  __/ |___|  __/|__   _| | (_| (_| | ||  __/ (_| | (_) | |  | |_| |
         *      \___|_|  \___|\__,_|\__\___|\____|_|      |_|    \___\__,_|\__\___|\__, |\___/|_|   \__, |
         *                                                                         |___/            |___/
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
        cpCat.setBackground(getColor(cat)[SYSConst.medium2]);
        cpCat.setOpaque(true);
        cpCat.setHorizontalAlignment(SwingConstants.LEADING);

        cpCat.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                JPanel pnlContent = new JPanel(new VerticalLayout());
                if (valuecache.containsKey(cat)) {
                    int i = 0; // for zebra pattern
                    for (NursingProcess np : valuecache.get(cat)) {
                        JPanel pnl = createNPPanel(np);
                        pnl.setBackground(i % 2 == 0 ? Color.WHITE : getColor(cat)[SYSConst.light3]);
                        pnl.setOpaque(true);
                        pnlContent.add(pnl);
                        i++;
                    }
                }
                cpCat.setContentPane(pnlContent);
            }
        });


        if (!cpCat.isCollapsed()) {
            JPanel pnlContent = new JPanel(new VerticalLayout());
            if (valuecache.containsKey(cat)) {
                for (NursingProcess np : valuecache.get(cat)) {
                    pnlContent.add(createNPPanel(np));
                }
            }
            cpCat.setContentPane(pnlContent);
        }

        return cpCat;
    }


    private Color[] getColor(ResInfoCategory cat) {
        if (categories.indexOf(cat) % 2 == 0) {
            return color1;
        } else {
            return color2;
        }
    }

    private JPanel createNPPanel(final NursingProcess np) {
        /***
         *                          _        ____ ____  _  _     _   _ ____
         *       ___ _ __ ___  __ _| |_ ___ / ___|  _ \| || |   | \ | |  _ \
         *      / __| '__/ _ \/ _` | __/ _ \ |   | |_) | || |_  |  \| | |_) |
         *     | (__| | |  __/ (_| | ||  __/ |___|  __/|__   _| | |\  |  __/
         *      \___|_|  \___|\__,_|\__\___|\____|_|      |_|   |_| \_|_|
         *
         */

        String title = "<html><table border=\"0\">" +
                "<tr valign=\"top\">" +
                "<td width=\"280\" align=\"left\">" + np.getPITAsHTML() + "</td>" +
                "<td width=\"500\" align=\"left\">" +
                (np.isClosed() ? "<s>" : "") +
                np.getContentAsHTML() +
                (np.isClosed() ? "</s>" : "") +
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
        final JButton btnMenu = new JButton(SYSConst.icon32menu);
        btnMenu.setPressedIcon(SYSConst.icon32Pressed);
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
                JPanel pnl = getMenu(np);
                popup.getContentPane().add(pnl);
                popup.setDefaultFocusComponent(pnl);

                GUITools.showPopup(popup, SwingConstants.WEST);
            }
        });


        cptitle.getButton().setIcon(getIcon(np));

        cptitle.getRight().add(btnMenu);
        cptitle.getMain().setBackground(getColor(np.getCategory())[SYSConst.light2]);
        cptitle.getMain().setOpaque(true);

        return cptitle.getMain();
    }

//    public void refreshDisplay() {
//        for (ResInfoCategory category : categories) {
//            for (NursingProcess planung : valuecache.get(category)) {
//                planungCollapsiblePaneMap.get(planung).setVisible(tbInactive.isSelected() || !planung.isClosed());
//            }
//        }
//    }

    private Icon getIcon(NursingProcess np) {
        DateTime nexteval = new DateTime(np.getNextEval());
        if (!np.isClosed()) {
            Period period = new Period(nexteval, new DateTime());
            if (period.getDays() > 7) {
                return SYSConst.icon22ledGreenOn;
            } else if (period.getDays() < 0) {
                return SYSConst.icon22ledRedOn;
            } else {
                return SYSConst.icon22ledYellowOn;
            }
        } else {
            return SYSConst.icon22ledGreenOff;
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

    private String getHyperlinkButtonTextFor(NursingProcess planung) {
        String result = "<b>" + planung.getTopic() + "</b> ";

        if (planung.isClosed()) {
            result += DateFormat.getDateInstance().format(planung.getFrom()) + " &rarr; " + DateFormat.getDateInstance().format(planung.getTo());
        } else {
            result += DateFormat.getDateInstance().format(planung.getFrom()) + " &rarr;| ";
        }

        return SYSTools.toHTMLForScreen(result);
    }

    private List<Component> addFilters() {
        List<Component> list = new ArrayList<Component>();

        txtSearch = new JXSearchField(OPDE.lang.getString("misc.msg.searchphrase"));
        txtSearch.setInstantSearchDelay(750);
        txtSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        list.add(txtSearch);

        tbInactive = GUITools.getNiceToggleButton(OPDE.lang.getString(internalClassID + ".inactive"));
        tbInactive.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (initPhase) return;
                buildPanel();
            }
        });
        tbInactive.setHorizontalAlignment(SwingConstants.LEFT);
        list.add(tbInactive);
        return list;
    }

    private void addNursingProcessToDisplay(NursingProcess np) {
//        private HashMap<NursingProcess, CollapsiblePane> planungCollapsiblePaneMap;
//    private HashMap<ResInfoCategory, java.util.List<NursingProcess>> valuecache;

        if (!valuecache.containsKey(np.getCategory())) {
            valuecache.put(np.getCategory(), new ArrayList<NursingProcess>());
        }
        valuecache.get(np.getCategory()).add(np);
        Collections.sort(valuecache.get(np.getCategory()));

        if (!categories.contains(np.getCategory())) {
            categories.add(np.getCategory());
        }
        Collections.sort(categories);
    }

    private List<Component> addCommands() {

        List<Component> list = new ArrayList<Component>();

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
                    new DlgNursingProcess(new NursingProcess(resident), new Closure() {
                        @Override
                        public void execute(Object planung) {
                            if (planung != null) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                    Unique unique = UniqueTools.getNewUID(em, NursingProcessTools.UNIQUEID);
                                    NursingProcess myplan = em.merge((NursingProcess) planung);
                                    myplan.setNPSeries(unique.getUid());
                                    DFNTools.generate(em, myplan.getInterventionSchedule(), new DateMidnight(), true);
                                    em.getTransaction().commit();
                                    addNursingProcessToDisplay(myplan);
                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getSuccessMessage(myplan.getTopic(), "entered"));
                                    reloadDisplay();
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
            list.add(addButton);

            /***
             *      _   _                  _______                    _       _     __
             *     | \ | | _____      __  / /_   _|__ _ __ ___  _ __ | | __ _| |_ __\ \
             *     |  \| |/ _ \ \ /\ / / | |  | |/ _ \ '_ ` _ \| '_ \| |/ _` | __/ _ \ |
             *     | |\  |  __/\ V  V /  | |  | |  __/ | | | | | |_) | | (_| | ||  __/ |
             *     |_| \_|\___| \_/\_/   | |  |_|\___|_| |_| |_| .__/|_|\__,_|\__\___| |
             *                            \_\                  |_|                  /_/
             */
            final JideButton addTemplate = GUITools.createHyperlinkButton(OPDE.lang.getString("misc.commands.newfromtemplate"), new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")), null);
            addTemplate.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    final JidePopup popup = new JidePopup();

                    // first a template is selected
                    JPanel dlg = new PnlTemplate(new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {
                                popup.hidePopup();

                                // that selected template is cloned and handed over to the DlgNursingProcess for further editing
                                NursingProcess template = ((NursingProcess) o).clone();
                                template.setNPSeries(-2); // so the next dialog knows thats a template
                                template.setResident(resident);
                                template.setTo(SYSConst.DATE_BIS_AUF_WEITERES);
                                template.setUserOFF(null);
                                template.setUserON(OPDE.getLogin().getUser());
                                template.setNextEval(new DateTime().plusWeeks(4).toDate());

                                new DlgNursingProcess(template, new Closure() {
                                    @Override
                                    public void execute(Object planung) {
                                        if (planung != null) {
                                            EntityManager em = OPDE.createEM();
                                            try {
                                                em.getTransaction().begin();
                                                em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                                Unique unique = UniqueTools.getNewUID(em, NursingProcessTools.UNIQUEID);
                                                NursingProcess myplan = em.merge((NursingProcess) planung);
                                                myplan.setNPSeries(unique.getUid());
                                                DFNTools.generate(em, myplan.getInterventionSchedule(), new DateMidnight(), true);
                                                em.getTransaction().commit();
                                                addNursingProcessToDisplay(myplan);
                                                OPDE.getDisplayManager().addSubMessage(DisplayManager.getSuccessMessage(myplan.getTopic(), "entered"));
                                                reloadDisplay();
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
                        }
                    });

                    popup.setMovable(false);
                    popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
                    popup.getContentPane().add(dlg);
                    popup.setOwner(addTemplate);
                    popup.removeExcludedComponent(addTemplate);
                    popup.setDefaultFocusComponent(dlg);

                    GUITools.showPopup(popup, SwingConstants.NORTH_EAST);

                }
            });

            list.add(addTemplate);
        }


        return list;
    }


    private JPanel getMenu(final NursingProcess np) {

        final JPanel pnlMenu = new JPanel(new VerticalLayout());
        long numDFNs = DFNTools.getNumDFNs(np);

        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.UPDATE)) {
            /***
             *       ____ _
             *      / ___| |__   __ _ _ __   __ _  ___
             *     | |   | '_ \ / _` | '_ \ / _` |/ _ \
             *     | |___| | | | (_| | | | | (_| |  __/
             *      \____|_| |_|\__,_|_| |_|\__, |\___|
             *                              |___/
             */
            JButton btnChange = GUITools.createHyperlinkButton(internalClassID + ".btnchange.tooltip", SYSConst.icon22playerPlay, null);
            btnChange.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnChange.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    NursingProcess template = np.clone();
                    template.setTo(SYSConst.DATE_BIS_AUF_WEITERES);
                    template.setUserOFF(null);
                    template.setUserON(OPDE.getLogin().getUser());
                    template.setNextEval(new DateTime().plusWeeks(4).toDate());
                    new DlgNursingProcess(template, new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);

                                    // Fetch the new Plan from the PAIR
                                    NursingProcess myNewNP = em.merge(((Pair<NursingProcess, ArrayList<InterventionSchedule>>) o).getFirst());
                                    NursingProcess myOldNP = em.merge(np);
                                    em.lock(myOldNP, LockModeType.OPTIMISTIC);

                                    // Close old NP
                                    myOldNP.setUserOFF(em.merge(OPDE.getLogin().getUser()));
                                    myOldNP.setTo(new DateTime().minusSeconds(1).toDate());
                                    NPControl lastValidation = em.merge(new NPControl(myNewNP.getSituation(), myOldNP));
                                    myOldNP.getEvaluations().add(lastValidation);

                                    // Starts 1 second after the old one stopped
                                    myNewNP.setFrom(new DateTime(myOldNP.getTo()).plusSeconds(1).toDate());

                                    // DFNs to delete
                                    Query delQuery = em.createQuery("DELETE FROM DFN dfn WHERE dfn.nursingProcess = :nursingprocess AND dfn.status = :status ");
                                    delQuery.setParameter("nursingprocess", myOldNP);
                                    delQuery.setParameter("status", DFNTools.STATE_OPEN);
                                    delQuery.executeUpdate();

                                    // Create new DFNs according to plan
                                    DFNTools.generate(em, myNewNP.getInterventionSchedule(), new DateMidnight(), true);
                                    em.getTransaction().commit();
                                    // Refresh Display
                                    valuecache.get(np.getCategory()).remove(np);
                                    valuecache.get(np.getCategory()).add(myOldNP);
                                    addNursingProcessToDisplay(myNewNP);
                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getSuccessMessage(np.getTopic(), "changed"));
                                    reloadDisplay();
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
//                                reloadTable();
                            }
                        }
                    });

                }
            });
            btnChange.setEnabled(!np.isClosed());
            pnlMenu.add(btnChange);

            /***
             *      ____        _   _                ____  _
             *     | __ ) _   _| |_| |_ ___  _ __   / ___|| |_ ___  _ __
             *     |  _ \| | | | __| __/ _ \| '_ \  \___ \| __/ _ \| '_ \
             *     | |_) | |_| | |_| || (_) | | | |  ___) | || (_) | |_) |
             *     |____/ \__,_|\__|\__\___/|_| |_| |____/ \__\___/| .__/
             *                                                     |_|
             */

            final JButton btnStop = GUITools.createHyperlinkButton(internalClassID + ".btnstop.tooltip", SYSConst.icon22stop, null);
            btnStop.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnStop.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    final JidePopup popup = new JidePopup();

                    JPanel dlg = new PnlEval(np, new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {
                                popup.hidePopup();

                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    NursingProcess myOldNP = em.merge(np);
                                    myOldNP.setUserOFF(em.merge(OPDE.getLogin().getUser()));
                                    myOldNP.setTo(new Date());
                                    NPControl lastValidation = em.merge(new NPControl(o.toString(), myOldNP));
                                    myOldNP.getEvaluations().add(lastValidation);

                                    // DFNs to delete
                                    Query delQuery = em.createQuery("DELETE FROM DFN dfn WHERE dfn.nursingProcess = :nursingprocess AND dfn.status = :status ");
                                    delQuery.setParameter("nursingprocess", myOldNP);
                                    delQuery.setParameter("status", DFNTools.STATE_OPEN);
                                    delQuery.executeUpdate();

                                    // Refresh Display
                                    valuecache.get(np.getCategory()).remove(np);
                                    valuecache.get(np.getCategory()).add(myOldNP);
                                    Collections.sort(valuecache.get(myOldNP.getCategory()));

                                    em.lock(myOldNP, LockModeType.OPTIMISTIC);
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

                                OPDE.getDisplayManager().addSubMessage(DisplayManager.getSuccessMessage(np.getTopic(), "closed"));
                                reloadDisplay();
                            }
                        }
                    });

                    popup.setMovable(false);
                    popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
                    popup.getContentPane().add(dlg);
                    popup.setOwner(btnStop);
                    popup.removeExcludedComponent(btnStop);
                    popup.setDefaultFocusComponent(dlg);

                    GUITools.showPopup(popup, SwingConstants.WEST);
                }
            });
            btnStop.setEnabled(!np.isClosed());
            pnlMenu.add(btnStop);


            /***
             *      ____        _   _                _____    _ _ _
             *     | __ ) _   _| |_| |_ ___  _ __   | ____|__| (_) |_
             *     |  _ \| | | | __| __/ _ \| '_ \  |  _| / _` | | __|
             *     | |_) | |_| | |_| || (_) | | | | | |__| (_| | | |_
             *     |____/ \__,_|\__|\__\___/|_| |_| |_____\__,_|_|\__|
             *
             */

            JButton btnEdit = GUITools.createHyperlinkButton(internalClassID + ".btnedit.tooltip", SYSConst.icon22edit, null);
            btnEdit.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnEdit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgNursingProcess(np, new Closure() {
                        @Override
                        public void execute(Object np) {
                            if (np != null) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                    NursingProcess mynp = em.merge(((Pair<NursingProcess, ArrayList<InterventionSchedule>>) np).getFirst());
                                    em.lock(mynp, LockModeType.OPTIMISTIC);
                                    // Schedules to delete
                                    for (InterventionSchedule is : ((Pair<NursingProcess, ArrayList<InterventionSchedule>>) np).getSecond()) {
                                        em.remove(em.merge(is));
                                    }
                                    // No unused DFNs to delete
                                    Query delQuery = em.createQuery("DELETE FROM DFN dfn WHERE dfn.nursingProcess = :nursingprocess ");
                                    delQuery.setParameter("nursingprocess", mynp);
                                    delQuery.executeUpdate();
                                    // Create new DFNs according to plan
                                    DFNTools.generate(em, mynp.getInterventionSchedule(), new DateMidnight(), true);
                                    em.getTransaction().commit();
                                    Collections.sort(valuecache.get(mynp.getCategory()));
                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getSuccessMessage(mynp.getTopic(), "edited"));
                                    reloadDisplay();
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
//                                reloadTable();
                            }
                        }
                    });
                }
            });
            btnEdit.setEnabled(!np.isClosed() && numDFNs == 0);
            pnlMenu.add(btnEdit);
        }

        /***
         *      ____        _   _                ____       _      _
         *     | __ ) _   _| |_| |_ ___  _ __   |  _ \  ___| | ___| |_ ___
         *     |  _ \| | | | __| __/ _ \| '_ \  | | | |/ _ \ |/ _ \ __/ _ \
         *     | |_) | |_| | |_| || (_) | | | | | |_| |  __/ |  __/ ||  __/
         *     |____/ \__,_|\__|\__\___/|_| |_| |____/ \___|_|\___|\__\___|
         *
         */
        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.DELETE)) {  // => ACL_MATRIX
            JButton btnDelete = GUITools.createHyperlinkButton(internalClassID + ".btnedit.tooltip", SYSConst.icon22delete, null);
            btnDelete.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnDelete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgYesNo(OPDE.lang.getString("misc.questions.delete1") + "<b>" + np.getTopic() + "</b>" + OPDE.lang.getString("misc.questions.delete2"), SYSConst.icon48delete, new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o.equals(JOptionPane.YES_OPTION)) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    NursingProcess myOldNP = em.merge(np);

                                    // DFNs to delete
                                    Query delQuery = em.createQuery("DELETE FROM DFN dfn WHERE dfn.nursingProcess = :nursingprocess AND dfn.status = :status ");
                                    delQuery.setParameter("nursingprocess", myOldNP);
                                    delQuery.setParameter("status", DFNTools.STATE_OPEN);
                                    delQuery.executeUpdate();

                                    em.remove(myOldNP);
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

                                OPDE.getDisplayManager().addSubMessage(DisplayManager.getSuccessMessage(np.getTopic(), "deleted"));
                                // TODO: das reicht nicht. muss wohl mehr gemacht werden. die gelöschten bleiben stehen
                                reloadDisplay();
                            }
                        }
                    });
                }
            });
            btnDelete.setEnabled(!np.isClosed() && numDFNs == 0);
            pnlMenu.add(btnDelete);
        }


        /***
         *      ____  _         _____            _
         *     | __ )| |_ _ __ | ____|_   ____ _| |
         *     |  _ \| __| '_ \|  _| \ \ / / _` | |
         *     | |_) | |_| | | | |___ \ V / (_| | |
         *     |____/ \__|_| |_|_____| \_/ \__,_|_|
         *
         */
        final JButton btnEval = GUITools.createHyperlinkButton(internalClassID + ".btneval.tooltip", SYSConst.icon22redo, null);
        btnEval.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnEval.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                final JidePopup popup = new JidePopup();

                JPanel dlg = new PnlEval(np, new Closure() {
                    @Override
                    public void execute(Object o) {
                        if (o != null) {
                            popup.hidePopup();

                            Pair<NursingProcess, String> result = (Pair<NursingProcess, String>) o;

                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();
                                em.lock(em.merge(resident), LockModeType.OPTIMISTIC);

                                NursingProcess evaluatedNP = em.merge(result.getFirst());
                                em.lock(evaluatedNP, LockModeType.OPTIMISTIC);

                                NPControl newEvaluation = em.merge(new NPControl(result.getSecond(), evaluatedNP));
                                evaluatedNP.getEvaluations().add(newEvaluation);

                                // Refresh Display
                                valuecache.get(np.getCategory()).remove(np);
                                valuecache.get(np.getCategory()).add(evaluatedNP);
                                Collections.sort(valuecache.get(np.getCategory()));

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

                            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".success.neweval")));
                            reloadDisplay();
                        }
                    }
                });

                popup.setMovable(false);
                popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
                popup.getContentPane().add(dlg);
                popup.setOwner(btnEval);
                popup.removeExcludedComponent(btnEval);
                popup.setDefaultFocusComponent(dlg);

                GUITools.showPopup(popup, SwingConstants.WEST);
            }
        });
        btnEval.setEnabled(!np.isClosed());
        pnlMenu.add(btnEval);

        return pnlMenu;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jspPlanung;
    private CollapsiblePanes cpsPlan;
    // End of variables declaration//GEN-END:variables
}
