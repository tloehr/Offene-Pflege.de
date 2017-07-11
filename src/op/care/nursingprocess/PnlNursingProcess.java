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
import entity.EntityTools;
import entity.files.SYSFilesTools;
import entity.info.ResInfoCategory;
import entity.info.ResInfoCategoryTools;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.nursingprocess.*;
import entity.process.*;
import entity.system.Commontags;
import entity.system.CommontagsTools;
import entity.system.Unique;
import entity.system.UniqueTools;
import gui.GUITools;
import gui.interfaces.DefaultCPTitle;
import op.OPDE;
import op.care.sysfiles.DlgFiles;
import op.process.DlgProcessAssign;
import op.system.InternalClassACL;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import javax.persistence.*;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.text.DateFormat;
import java.util.*;
import java.util.List;

/**
 * @author tloehr
 */
public class PnlNursingProcess extends NursingRecordsPanel {


    private Resident resident;
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;

    private HashMap<ResInfoCategory, ArrayList<NursingProcess>> valuecache;
    private java.util.List<ResInfoCategory> categories;
    private HashMap<String, CollapsiblePane> cpMap;
    private HashMap<NursingProcess, JPanel> contenPanelMap;
    private ArrayList<NursingProcess> listNP;

    private Color[] color1, color2;
    private List<Commontags> listUsedCommontags;
    private JToggleButton tbShowClosed;
    private PnlTemplate templateDialog;


    /**
     * Creates new form PnlNursingProcess
     */
    public PnlNursingProcess(Resident resident, JScrollPane jspSearch) {
        super("nursingrecords.nursingprocess");
        this.jspSearch = jspSearch;

        initComponents();
        initPanel();

        switchResident(resident);

    }

    private void initPanel() {
        cpMap = new HashMap<String, CollapsiblePane>();
        valuecache = new HashMap<ResInfoCategory, ArrayList<NursingProcess>>();
        categories = ResInfoCategoryTools.getAll4NP();
        contenPanelMap = new HashMap<NursingProcess, JPanel>();
        listUsedCommontags = new ArrayList<>();
        prepareSearchArea();
        color1 = SYSConst.red2;
        color2 = SYSConst.greyscale;
    }

    @Override
    public void cleanup() {
        super.cleanup();
        if (templateDialog != null && templateDialog.isShowing()) {
            templateDialog.dispose();
        }
        SYSTools.clear(cpMap);
        SYSTools.clear(valuecache);
        SYSTools.clear(contenPanelMap);
        SYSTools.clear(listNP);
        SYSTools.clear(listUsedCommontags);
    }

    @Override
    public void reload() {
        cleanup();
        listNP = NursingProcessTools.getAll(resident);
        Collections.sort(listNP, (o1, o2) -> {
            int result = 0;

            if (result == 0 && !o1.isClosed() && o2.isClosed()) {
                result = -1;
            }

            if (result == 0 && o1.isClosed() && !o2.isClosed()) {
                result = 1;
            }

            if (result == 0) {
                result = o1.getFrom().compareTo(o2.getFrom()) * -1;
            }

            return result;
        });
        reloadDisplay();
    }

    @Override
    public void switchResident(Resident res) {
        this.resident = EntityTools.find(Resident.class, res.getRID());
        GUITools.setResidentDisplay(resident);
        reload();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the PrinterForm Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jspNP = new JScrollPane();
        cpsPlan = new CollapsiblePanes();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== jspNP ========
        {

            //======== cpsPlan ========
            {
                cpsPlan.setLayout(new BoxLayout(cpsPlan, BoxLayout.X_AXIS));
            }
            jspNP.setViewportView(cpsPlan);
        }
        add(jspNP);
    }// </editor-fold>//GEN-END:initComponents


    @Override
    public String getInternalClassID() {
        return internalClassID;
    }

    private void reloadDisplay() {
        if (valuecache.isEmpty()) {
            for (NursingProcess np : listNP) {
                if (tbShowClosed.isSelected() || !np.isClosed()) {
                    if (!valuecache.containsKey(np.getCategory())) {
                        valuecache.put(np.getCategory(), new ArrayList<NursingProcess>());
                    }
                    valuecache.get(np.getCategory()).add(np);

                }
            }
        }

        for (ResInfoCategory cat : categories) {
            createCP4(cat);
        }

        buildPanel();
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

        DefaultCPTitle cptitle = new DefaultCPTitle(title, e -> {
            try {
//                    if (cpCat.isCollapsed() && !tbInactive.isSelected()  && !isEmpty(cat) && containsOnlyClosedNPs(cat)) {
//                        tbInactive.setSelected(true);
//                    }
                cpCat.setCollapsed(!cpCat.isCollapsed());
            } catch (PropertyVetoException pve) {
                // BAH!
            }
        });

        if (isEmpty(cat)) {
            cptitle.getButton().setIcon(SYSConst.icon22ledGreenOff);
        } else if (containsOnlyClosedNPs(cat)) {
            cptitle.getButton().setIcon(SYSConst.icon22stopSign);
        } else {
            cptitle.getButton().setIcon(getIcon(getMinimumNextEvalDays(cat)));
        }

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
//                        if (!np.isClosed()) { // tbInactive.isSelected() ||
                        JPanel pnl = createNPPanel(np);
                        pnl.setBackground(i % 2 == 0 ? Color.WHITE : getColor(cat)[SYSConst.light3]);
                        pnl.setOpaque(true);
                        pnlContent.add(pnl);
                        i++;
//                        }
                    }
                }
                cpCat.setContentPane(pnlContent);
            }
        });

        if (!cpCat.isCollapsed()) {
            JPanel pnlContent = new JPanel(new VerticalLayout());
            if (valuecache.containsKey(cat)) {
                int i = 0; // for zebra pattern
                for (NursingProcess np : valuecache.get(cat)) {
//                    if (!np.isClosed()) { // tbInactive.isSelected() ||
                    JPanel pnl = createNPPanel(np);
                    pnl.setBackground(i % 2 == 0 ? Color.WHITE : getColor(cat)[SYSConst.light3]);
                    pnl.setOpaque(true);
                    pnlContent.add(pnl);
                    i++;
//                    }
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
        if (!contenPanelMap.containsKey(np)) {
            String title = "<html><table border=\"0\">";

            if (!np.getCommontags().isEmpty()) {
                title += "<tr>" +
                        "    <td colspan=\"2\">" + CommontagsTools.getAsHTML(np.getCommontags(), SYSConst.html_16x16_tagPurple_internal) + "</td>" +
                        "  </tr>";
            }

            title += "<tr valign=\"top\">" +
                    "<td width=\"280\" align=\"left\">" + np.getPITAsHTML() + "</td>" +
                    "<td width=\"500\" align=\"left\">" +
                    (np.isClosed() ? "<s>" : "") +
                    SYSConst.html_h2(np.getTopic()) +
                    np.getContentAsHTML() +
                    (np.isClosed() ? "</s>" : "") +
                    "</td></tr>";
            title += "</table>" +
                    "</html>";

            DefaultCPTitle cptitle = new DefaultCPTitle(title, null);
            cptitle.getButton().setVerticalTextPosition(SwingConstants.TOP);

            if (!np.getAttachedFilesConnections().isEmpty()) {
                /***
                 *      _     _         _____ _ _
                 *     | |__ | |_ _ __ |  ___(_) | ___  ___
                 *     | '_ \| __| '_ \| |_  | | |/ _ \/ __|
                 *     | |_) | |_| | | |  _| | | |  __/\__ \
                 *     |_.__/ \__|_| |_|_|   |_|_|\___||___/
                 *
                 */
                final JButton btnFiles = new JButton(Integer.toString(np.getAttachedFilesConnections().size()), SYSConst.icon22greenStar);
                btnFiles.setToolTipText(SYSTools.xx("misc.btnfiles.tooltip"));
                btnFiles.setForeground(Color.BLUE);
                btnFiles.setHorizontalTextPosition(SwingUtilities.CENTER);
                btnFiles.setFont(SYSConst.ARIAL18BOLD);
                btnFiles.setPressedIcon(SYSConst.icon22Pressed);
                btnFiles.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnFiles.setAlignmentY(Component.TOP_ALIGNMENT);
                btnFiles.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnFiles.setContentAreaFilled(false);
                btnFiles.setBorder(null);
                btnFiles.addActionListener(actionEvent -> {
                    Closure fileHandleClosure = np.isClosed() ? null : o -> {
                        EntityManager em = OPDE.createEM();
                        final NursingProcess myNP = em.find(NursingProcess.class, np.getID());
                        em.close();
                        // Refresh Display
                        valuecache.get(np.getCategory()).remove(np);
                        contenPanelMap.remove(np);
                        valuecache.get(myNP.getCategory()).add(myNP);
                        Collections.sort(valuecache.get(myNP.getCategory()));
                        currentEditor = null;
                        createCP4(myNP.getCategory());
                        buildPanel();
                    };
                    currentEditor = new DlgFiles(np, fileHandleClosure);
                    currentEditor.setVisible(true);
                });
                btnFiles.setEnabled(OPDE.isFTPworking());
                cptitle.getRight().add(btnFiles);
            }

            if (!np.getAttachedQProcessConnections().isEmpty()) {
                /***
                 *      _     _         ____
                 *     | |__ | |_ _ __ |  _ \ _ __ ___   ___ ___  ___ ___
                 *     | '_ \| __| '_ \| |_) | '__/ _ \ / __/ _ \/ __/ __|
                 *     | |_) | |_| | | |  __/| | | (_) | (_|  __/\__ \__ \
                 *     |_.__/ \__|_| |_|_|   |_|  \___/ \___\___||___/___/
                 *
                 */
                final JButton btnProcess = new JButton(Integer.toString(np.getAttachedQProcessConnections().size()), SYSConst.icon22redStar);
                btnProcess.setToolTipText(SYSTools.xx("misc.btnprocess.tooltip"));
                btnProcess.setForeground(Color.YELLOW);
                btnProcess.setHorizontalTextPosition(SwingUtilities.CENTER);
                btnProcess.setFont(SYSConst.ARIAL18BOLD);
                btnProcess.setPressedIcon(SYSConst.icon22Pressed);
                btnProcess.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnProcess.setAlignmentY(Component.TOP_ALIGNMENT);
                btnProcess.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnProcess.setContentAreaFilled(false);
                btnProcess.setBorder(null);
                btnProcess.addActionListener(actionEvent -> {
                    currentEditor = new DlgProcessAssign(np, o -> {
                        if (o == null) {
                            currentEditor = null;
                            return;
                        }
                        Pair<ArrayList<QProcess>, ArrayList<QProcess>> result = (Pair<ArrayList<QProcess>, ArrayList<QProcess>>) o;

                        ArrayList<QProcess> assigned = result.getFirst();
                        ArrayList<QProcess> unassigned = result.getSecond();

                        EntityManager em = OPDE.createEM();

                        try {
                            em.getTransaction().begin();

                            em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                            final NursingProcess myNP = em.merge(np);
                            em.lock(myNP, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                            ArrayList<SYSNP2PROCESS> attached = new ArrayList<SYSNP2PROCESS>(myNP.getAttachedQProcessConnections());
                            for (SYSNP2PROCESS linkObject : attached) {
                                if (unassigned.contains(linkObject.getQProcess())) {
                                    linkObject.getQProcess().getAttachedNReportConnections().remove(linkObject);
                                    linkObject.getNursingProcess().getAttachedQProcessConnections().remove(linkObject);
                                    em.merge(new PReport(SYSTools.xx(PReportTools.PREPORT_TEXT_REMOVE_ELEMENT) + ": " + myNP.getTitle() + " ID: " + myNP.getID(), PReportTools.PREPORT_TYPE_REMOVE_ELEMENT, linkObject.getQProcess()));
                                    em.remove(linkObject);
                                }
                            }
                            attached.clear();

                            for (QProcess qProcess : assigned) {
                                List<QProcessElement> listElements = qProcess.getElements();
                                if (!listElements.contains(myNP)) {
                                    QProcess myQProcess = em.merge(qProcess);
                                    SYSNP2PROCESS myLinkObject = em.merge(new SYSNP2PROCESS(myQProcess, myNP));
                                    em.merge(new PReport(SYSTools.xx(PReportTools.PREPORT_TEXT_ASSIGN_ELEMENT) + ": " + myNP.getTitle() + " ID: " + myNP.getID(), PReportTools.PREPORT_TYPE_ASSIGN_ELEMENT, myQProcess));
                                    qProcess.getAttachedNursingProcessesConnections().add(myLinkObject);
                                    myNP.getAttachedQProcessConnections().add(myLinkObject);
                                }
                            }

                            em.getTransaction().commit();

                            // Refresh Display
                            valuecache.get(np.getCategory()).remove(np);
                            contenPanelMap.remove(np);
                            valuecache.get(myNP.getCategory()).add(myNP);
                            Collections.sort(valuecache.get(myNP.getCategory()));

                            createCP4(myNP.getCategory());
                            buildPanel();

                        } catch (OptimisticLockException ole) {
                            OPDE.warn(ole);
                            if (em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }
                            if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                                OPDE.getMainframe().emptyFrame();
                                OPDE.getMainframe().afterLogin();
                            } else {
                                reloadDisplay();
                            }
                        } catch (RollbackException ole) {
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

                    });
                    currentEditor.setVisible(true);
                });
                btnProcess.setEnabled(np.isActive() && OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID));
                cptitle.getRight().add(btnProcess);
            }

            if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.PRINT, internalClassID)) {
                /***
                 *      _     _         ____       _       _
                 *     | |__ | |_ _ __ |  _ \ _ __(_)_ __ | |_
                 *     | '_ \| __| '_ \| |_) | '__| | '_ \| __|
                 *     | |_) | |_| | | |  __/| |  | | | | | |_
                 *     |_.__/ \__|_| |_|_|   |_|  |_|_| |_|\__|
                 *
                 */
                JButton btnPrint = new JButton(SYSConst.icon22print2);
                btnPrint.setContentAreaFilled(false);
                btnPrint.setBorder(null);
                btnPrint.setPressedIcon(SYSConst.icon22print2Pressed);
                btnPrint.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnPrint.setAlignmentY(Component.TOP_ALIGNMENT);
                btnPrint.addActionListener(actionEvent -> SYSFilesTools.print(NursingProcessTools.getAsHTML(np, true, true, true, true), true));

                cptitle.getRight().add(btnPrint);
                //                cptitle.getTitleButton().setVerticalTextPosition(SwingConstants.TOP);
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
            btnMenu.addActionListener(e -> {
                JidePopup popup = new JidePopup();
                popup.setMovable(false);
                popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
                popup.setOwner(btnMenu);
                popup.removeExcludedComponent(btnMenu);
                JPanel pnl = getMenu(np);
                popup.getContentPane().add(pnl);
                popup.setDefaultFocusComponent(pnl);

                GUITools.showPopup(popup, SwingConstants.WEST);
            });

            btnMenu.setEnabled(!np.isClosed());
            cptitle.getButton().setIcon(getIcon(np));

            cptitle.getRight().add(btnMenu);
            cptitle.getMain().setBackground(getColor(np.getCategory())[SYSConst.light2]);
            cptitle.getMain().setOpaque(true);
            contenPanelMap.put(np, cptitle.getMain());
        }

        return contenPanelMap.get(np);
    }


    private java.util.List<Component> addKey() {
        java.util.List<Component> list = new ArrayList<Component>();
        list.add(new JSeparator());
        list.add(new JLabel(SYSTools.xx("misc.msg.key")));
        list.add(new JLabel(SYSTools.xx("nursingrecords.nursingprocess.keydescription1"), SYSConst.icon22stopSign, SwingConstants.LEADING));
        list.add(new JLabel(SYSTools.xx("nursingrecords.nursingprocess.keydescription2"), SYSConst.icon22ledGreenOn, SwingConstants.LEADING));
        list.add(new JLabel(SYSTools.xx("nursingrecords.nursingprocess.keydescription3"), SYSConst.icon22ledYellowOn, SwingConstants.LEADING));
        list.add(new JLabel(SYSTools.xx("nursingrecords.nursingprocess.keydescription4"), SYSConst.icon22ledRedOn, SwingConstants.LEADING));
        list.add(new JLabel(SYSTools.xx("nursingrecords.nursingprocess.keydescription5"), SYSConst.icon22ledGreenOff, SwingConstants.LEADING));

        return list;
    }

    private Icon getIcon(NursingProcess np) {
        DateTime nexteval = new DateTime(np.getNextEval());
        if (!np.isClosed()) {
            return getIcon(Days.daysBetween(new DateTime(), nexteval).getDays());
        } else {
            return SYSConst.icon22stopSign;
        }
    }

    private Icon getIcon(int days2nextEvalDate) {
        if (days2nextEvalDate > 7) {
            return SYSConst.icon22ledGreenOn;
        } else if (days2nextEvalDate < 0) {
            return SYSConst.icon22ledRedOn;
        } else {
            return SYSConst.icon22ledYellowOn;
        }
    }

    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);

        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout());
        mypanel.setBackground(Color.WHITE);

        CollapsiblePane searchPane = new CollapsiblePane(SYSTools.xx(internalClassID));
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);

        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }

        GUITools.addAllComponents(mypanel, addCommands());
        GUITools.addAllComponents(mypanel, addFilters());

        /***
         *      ____       _       _
         *     |  _ \ _ __(_)_ __ | |_
         *     | |_) | '__| | '_ \| __|
         *     |  __/| |  | | | | | |_
         *     |_|   |_|  |_|_| |_|\__|
         *
         */
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.PRINT, internalClassID)) {
            final JButton btnPrint = GUITools.createHyperlinkButton(SYSTools.xx("misc.commands.print"), SYSConst.icon22print2, e -> {
                String html = "";
                html += SYSConst.html_h1(SYSTools.xx("nursingrecords.nursingprocess") + " " + SYSTools.xx("misc.msg.for") + " " + ResidentTools.getLabelText(resident));
                for (ResInfoCategory cat : categories) {
                    if (valuecache.containsKey(cat) && !valuecache.get(cat).isEmpty()) {
                        html += SYSConst.html_h2(cat.getText());
                        for (NursingProcess np : valuecache.get(cat)) {
                            if (tbShowClosed.isSelected() || !np.isClosed()) {
                                html += SYSConst.html_h3(np.getTopic());
                                html += NursingProcessTools.getAsHTML(np, false, true, true, true);
                                html += "<hr/>";
                            }
                        }
                    }
                }
                SYSFilesTools.print(html, true);
            });
            btnPrint.setAlignmentX(Component.RIGHT_ALIGNMENT);

            mypanel.add(btnPrint);
        }
        GUITools.addAllComponents(mypanel, addKey());

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


    private java.util.List<Component> addFilters() {
        java.util.List<Component> list = new ArrayList<Component>();

        tbShowClosed = GUITools.getNiceToggleButton(SYSTools.xx("misc.filters.showclosed"));
        tbShowClosed.addItemListener(itemEvent -> {
            reload();
        });
        list.add(tbShowClosed);
        tbShowClosed.setHorizontalAlignment(SwingConstants.LEFT);


//           if (!listUsedCommontags.isEmpty()) {
//
//               JPanel pnlTags = new JPanel();
//               pnlTags.setLayout(new BoxLayout(pnlTags, BoxLayout.Y_AXIS));
//               pnlTags.setOpaque(false);
//
//               for (final Commontags commontag : listUsedCommontags) {
//                   final JButton btnTag = GUITools.createHyperlinkButton(commontag.getText(), SYSConst.icon16tagPurple, new ActionListener() {
//                       @Override
//                       public void actionPerformed(ActionEvent e) {
//                           SYSFilesTools.print(NursingProcessTools.getAsHTML().getPrescriptionsAsHTML(PrescriptionTools.getPrescriptions4Tags(resident, commontag), true, true, false, tbClosed.isSelected(), true), true);
//                       }
//                   });
//                   btnTag.setForeground(GUITools.getColor(commontag.getColor()));
//                   pnlTags.add(btnTag);
//               }
//               list.add(pnlTags);
//           }
//
        return list;
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
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
            JideButton addButton = GUITools.createHyperlinkButton(SYSTools.xx("misc.commands.new"), SYSConst.icon22add, actionEvent -> {
                if (!resident.isActive()) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.cantChangeInactiveResident"));
                    return;
                }
                currentEditor = new DlgNursingProcess(new NursingProcess(resident), np -> {
                    if (np != null) {
                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();
                            em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                            Unique unique = UniqueTools.getNewUID(em, NursingProcessTools.UNIQUEID);
                            final NursingProcess newNP = em.merge((NursingProcess) np);
                            newNP.setNPSeries(unique.getUid());
                            DFNTools.generate(em, newNP.getInterventionSchedule(), new LocalDate(), true);
                            em.getTransaction().commit();

                            // Refresh Display
                            if (!valuecache.containsKey(newNP.getCategory())) {
                                valuecache.put(newNP.getCategory(), new ArrayList<NursingProcess>());
                            }
                            valuecache.get(newNP.getCategory()).add(newNP);
                            Collections.sort(valuecache.get(newNP.getCategory()));
                            createCP4(newNP.getCategory());

                            boolean reloadSearch = false;
                            for (Commontags ctag : newNP.getCommontags()) {
                                if (!listUsedCommontags.contains(ctag)) {
                                    listUsedCommontags.add(ctag);
                                    reloadSearch = true;
                                }
                            }
                            if (reloadSearch) {
                                prepareSearchArea();
                            }


                            buildPanel();

                            SwingUtilities.invokeLater(() -> {
                                expandCat(newNP.getCategory());
                                GUITools.scroll2show(jspNP, contenPanelMap.get(newNP).getLocation().y, o -> GUITools.flashBackground(contenPanelMap.get(newNP), Color.YELLOW, 2));
                            });
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
            list.add(addButton);

            /***
             *      _   _                  _______                    _       _     __
             *     | \ | | _____      __  / /_   _|__ _ __ ___  _ __ | | __ _| |_ __\ \
             *     |  \| |/ _ \ \ /\ / / | |  | |/ _ \ '_ ` _ \| '_ \| |/ _` | __/ _ \ |
             *     | |\  |  __/\ V  V /  | |  | |  __/ | | | | | |_) | | (_| | ||  __/ |
             *     |_| \_|\___| \_/\_/   | |  |_|\___|_| |_| |_| .__/|_|\__,_|\__\___| |
             *                            \_\                  |_|                  /_/
             */
            final JideButton addTemplate = GUITools.createHyperlinkButton(SYSTools.xx("misc.commands.newfromtemplate"), SYSConst.icon22add, null);
            addTemplate.addActionListener(actionEvent -> {
                if (!resident.isActive()) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.cantChangeInactiveResident"));
                    return;
                }
//                    final JidePopup popup = new JidePopup();

                // first a template is selected
                templateDialog = new PnlTemplate(o -> {
                    if (o != null) {
                        // that selected template is cloned and handed over to the DlgNursingProcess for further editing
                        NursingProcess template = ((NursingProcess) o).clone();
                        template.setNPSeries(-2); // so the next dialog knows thats a template
                        template.setResident(resident);
                        template.setFrom(new Date());
                        template.setTo(SYSConst.DATE_UNTIL_FURTHER_NOTICE);
                        template.setUserOFF(null);
                        template.setUserON(OPDE.getLogin().getUser());
                        template.setNextEval(new DateTime().plusWeeks(4).toDate());

                        currentEditor = new DlgNursingProcess(template, np -> {
                            if (np != null) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                    Unique unique = UniqueTools.getNewUID(em, NursingProcessTools.UNIQUEID);
                                    final NursingProcess newNP = em.merge(((Pair<NursingProcess, ArrayList<InterventionSchedule>>) np).getFirst());
                                    newNP.setNPSeries(unique.getUid());
                                    DFNTools.generate(em, newNP.getInterventionSchedule(), new LocalDate(), true);
                                    em.getTransaction().commit();


                                    boolean reloadSearch = false;
                                    for (Commontags ctag : newNP.getCommontags()) {
                                        if (!listUsedCommontags.contains(ctag)) {
                                            listUsedCommontags.add(ctag);
                                            reloadSearch = true;
                                        }
                                    }
                                    if (reloadSearch) {
                                        prepareSearchArea();
                                    }


                                    // Refresh Display
                                    if (!valuecache.containsKey(newNP.getCategory())) {
                                        valuecache.put(newNP.getCategory(), new ArrayList<NursingProcess>());
                                    }
                                    valuecache.get(newNP.getCategory()).add(newNP);
                                    Collections.sort(valuecache.get(newNP.getCategory()));
                                    createCP4(newNP.getCategory());
                                    buildPanel();

                                    SwingUtilities.invokeLater(() -> {
                                        expandCat(newNP.getCategory());
                                        GUITools.scroll2show(jspNP, contenPanelMap.get(newNP).getLocation().y, o1 -> GUITools.flashBackground(contenPanelMap.get(newNP), Color.YELLOW, 2));
                                    });
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
                    }
                    templateDialog = null;
                });

                templateDialog.setVisible(true);
            });


            list.add(addTemplate);


        }

        final JideButton btnExpandAll = GUITools.createHyperlinkButton(SYSTools.xx("misc.msg.expandall"), SYSConst.icon22expand, actionEvent -> {
            try {
                GUITools.setCollapsed(cpsPlan, false);
            } catch (PropertyVetoException e) {
                // bah!
            }
        });
        list.add(btnExpandAll);

        final JideButton btnCollapseAll = GUITools.createHyperlinkButton(SYSTools.xx("misc.msg.collapseall"), SYSConst.icon22collapse, actionEvent -> {
            try {
                GUITools.setCollapsed(cpsPlan, true);
            } catch (PropertyVetoException e) {
                // bah!
            }
        });
        list.add(btnCollapseAll);

        return list;
    }


    private void expandCat(final ResInfoCategory cat) {
        // expand the category if necessary
        final String keyCat = cat.getID() + ".xcategory";
        if (cpMap.get(keyCat).isCollapsed()) {
            try {
                cpMap.get(keyCat).setCollapsed(false);
            } catch (PropertyVetoException e) {
                // Bah!
            }
        }
    }

    private JPanel getMenu(final NursingProcess np) {

        final JPanel pnlMenu = new JPanel(new VerticalLayout());
        long numDFNs = DFNTools.getNumDFNs(np);

        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
            /***
             *       ____ _
             *      / ___| |__   __ _ _ __   __ _  ___
             *     | |   | '_ \ / _` | '_ \ / _` |/ _ \
             *     | |___| | | | (_| | | | | (_| |  __/
             *      \____|_| |_|\__,_|_| |_|\__, |\___|
             *                              |___/
             */
            JButton btnChange = GUITools.createHyperlinkButton("nursingrecords.nursingprocess.btnchange.tooltip", SYSConst.icon22playerPlay, null);
            btnChange.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnChange.addActionListener(actionEvent -> {
                NursingProcess template = np.clone();
                template.setTo(SYSConst.DATE_UNTIL_FURTHER_NOTICE);
                template.setUserOFF(null);
                template.setUserON(OPDE.getLogin().getUser());
                template.setNextEval(new DateTime().plusWeeks(4).toDate());
                currentEditor = new DlgNursingProcess(template, o -> {
                    if (o != null) {
                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();
                            em.lock(em.merge(resident), LockModeType.OPTIMISTIC);

                            // Fetch the new Plan from the PAIR
                            NursingProcess myNewNP = em.merge((NursingProcess) o);
                            NursingProcess myOldNP = em.merge(np);
                            em.lock(myOldNP, LockModeType.OPTIMISTIC);

                            // Close old NP
                            myOldNP.setUserOFF(em.merge(OPDE.getLogin().getUser()));
                            myOldNP.setTo(new DateTime().minusSeconds(1).toDate());
                            NPControl lastValidation = em.merge(new NPControl(myNewNP.getSituation(), myOldNP));
                            lastValidation.setLastValidation(true);
                            myOldNP.getEvaluations().add(lastValidation);

                            // Starts 1 second after the old one stopped
                            myNewNP.setFrom(new DateTime(myOldNP.getTo()).plusSeconds(1).toDate());

                            // Create new DFNs according to plan
                            DFNTools.generate(em, myNewNP.getInterventionSchedule(), new LocalDate(), true);
                            em.getTransaction().commit();

                            // Refresh Display
                            valuecache.get(np.getCategory()).remove(np);
                            contenPanelMap.remove(np);
                            valuecache.get(myOldNP.getCategory()).add(myOldNP);
                            valuecache.get(myNewNP.getCategory()).add(myNewNP);
                            Collections.sort(valuecache.get(myNewNP.getCategory()));
                            createCP4(myNewNP.getCategory());

                            boolean reloadSearch = false;
                            for (Commontags ctag : myNewNP.getCommontags()) {
                                if (!listUsedCommontags.contains(ctag)) {
                                    listUsedCommontags.add(ctag);
                                    reloadSearch = true;
                                }
                            }
                            if (reloadSearch) {
                                prepareSearchArea();
                            }


                            buildPanel();
                            GUITools.flashBackground(contenPanelMap.get(myNewNP), Color.YELLOW, 2);
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
            btnChange.setEnabled(!np.isClosed() && numDFNs != 0);
            pnlMenu.add(btnChange);

            /***
             *      ____        _   _                ____  _
             *     | __ ) _   _| |_| |_ ___  _ __   / ___|| |_ ___  _ __
             *     |  _ \| | | | __| __/ _ \| '_ \  \___ \| __/ _ \| '_ \
             *     | |_) | |_| | |_| || (_) | | | |  ___) | || (_) | |_) |
             *     |____/ \__,_|\__|\__\___/|_| |_| |____/ \__\___/| .__/
             *                                                     |_|
             */

            final JButton btnStop = GUITools.createHyperlinkButton("nursingrecords.nursingprocess.btnstop.tooltip", SYSConst.icon22stop, null);
            btnStop.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnStop.addActionListener(actionEvent -> {
                final JidePopup popup = new JidePopup();

                JPanel dlg = new PnlEval(np, o -> {
                    if (o != null) {
                        popup.hidePopup();

                        Pair<NursingProcess, String> result = (Pair<NursingProcess, String>) o;

                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();
                            em.lock(em.merge(resident), LockModeType.OPTIMISTIC);

                            NursingProcess myOldNP = em.merge(np);
                            em.lock(myOldNP, LockModeType.OPTIMISTIC);

                            myOldNP.setUserOFF(em.merge(OPDE.getLogin().getUser()));
                            myOldNP.setTo(new Date());
                            NPControl lastValidation = em.merge(new NPControl(result.getSecond(), myOldNP));
                            lastValidation.setLastValidation(true);
                            myOldNP.getEvaluations().add(lastValidation);

                            em.getTransaction().commit();

                            // Refresh Display
                            valuecache.get(np.getCategory()).remove(np);
                            contenPanelMap.remove(np);
                            valuecache.get(myOldNP.getCategory()).add(myOldNP);
                            Collections.sort(valuecache.get(myOldNP.getCategory()));

                            createCP4(myOldNP.getCategory());
                            buildPanel();
//                                    GUITools.flashBackground(contenPanelMap.get(myOldNP), Color.YELLOW, 2);

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

                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getSuccessMessage(np.getTopic(), "closed"));
                        reloadDisplay();
                    }
                }, true);

                popup.setMovable(false);
                popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
                popup.getContentPane().add(dlg);
                popup.setOwner(btnStop);
                popup.removeExcludedComponent(btnStop);
                popup.setDefaultFocusComponent(dlg);

                GUITools.showPopup(popup, SwingConstants.WEST);
            });
            btnStop.setEnabled(!np.isClosed() && numDFNs != 0);
            pnlMenu.add(btnStop);


            /***
             *      ____        _   _                _____    _ _ _
             *     | __ ) _   _| |_| |_ ___  _ __   | ____|__| (_) |_
             *     |  _ \| | | | __| __/ _ \| '_ \  |  _| / _` | | __|
             *     | |_) | |_| | |_| || (_) | | | | | |__| (_| | | |_
             *     |____/ \__,_|\__|\__\___/|_| |_| |_____\__,_|_|\__|
             *
             */

            JButton btnEdit = GUITools.createHyperlinkButton("nursingrecords.nursingprocess.btnedit.tooltip", SYSConst.icon22edit, null);
            btnEdit.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnEdit.addActionListener(actionEvent -> {
                currentEditor = new DlgNursingProcess(np, o -> {
                    if (o != null) {
                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();
                            em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                            NursingProcess mynp = em.merge((NursingProcess) o);
                            em.lock(mynp, LockModeType.OPTIMISTIC);

                            // No unused DFNs to delete
                            Query delQuery = em.createQuery("DELETE FROM DFN dfn WHERE dfn.nursingProcess = :nursingprocess ");
                            delQuery.setParameter("nursingprocess", mynp);
                            delQuery.executeUpdate();

                            // Create new DFNs according to plan
                            DFNTools.generate(em, mynp.getInterventionSchedule(), new LocalDate(), true);
                            em.getTransaction().commit();

                            boolean reloadSearch = false;
                            for (Commontags ctag : mynp.getCommontags()) {
                                if (!listUsedCommontags.contains(ctag)) {
                                    listUsedCommontags.add(ctag);
                                    reloadSearch = true;
                                }
                            }

                            if (reloadSearch) {
                                prepareSearchArea();
                            }

                            // Refresh Display
                            valuecache.get(np.getCategory()).remove(np);
                            contenPanelMap.remove(np);
                            valuecache.get(mynp.getCategory()).add(mynp);
                            Collections.sort(valuecache.get(mynp.getCategory()));

                            createCP4(mynp.getCategory());
                            buildPanel();
                            GUITools.flashBackground(contenPanelMap.get(mynp), Color.YELLOW, 2);

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
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.DELETE, internalClassID)) {  // => ACL_MATRIX
            JButton btnDelete = GUITools.createHyperlinkButton("nursingrecords.nursingprocess.btndelete.tooltip", SYSConst.icon22delete, null);
            btnDelete.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnDelete.addActionListener(actionEvent -> {
                currentEditor = new DlgYesNo(SYSTools.xx("misc.questions.delete1") + "<br/><b>" + np.getTopic() + "</b><br/>" + SYSTools.xx("misc.questions.delete2"), SYSConst.icon48delete, o -> {
                    if (o.equals(JOptionPane.YES_OPTION)) {
                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();
                            em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                            NursingProcess myOldNP = em.merge(np);
                            em.lock(myOldNP, LockModeType.OPTIMISTIC);

                            // DFNs to delete
                            Query delQuery = em.createQuery("DELETE FROM DFN dfn WHERE dfn.nursingProcess = :nursingprocess ");
                            delQuery.setParameter("nursingprocess", myOldNP);
//                                    delQuery.setParameter("status", DFNTools.STATE_OPEN);
                            delQuery.executeUpdate();

                            em.remove(myOldNP);
                            em.getTransaction().commit();


                            // Refresh Display
                            valuecache.get(np.getCategory()).remove(np);
                            contenPanelMap.remove(np);

                            createCP4(myOldNP.getCategory());
                            buildPanel();

                            OPDE.getDisplayManager().addSubMessage(DisplayManager.getSuccessMessage(np.getTopic(), "deleted"));

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
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
            final JButton btnEval = GUITools.createHyperlinkButton("nursingrecords.nursingprocess.btneval.tooltip", SYSConst.icon22redo, null);
            btnEval.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnEval.addActionListener(actionEvent -> {
                final JidePopup popup = new JidePopup();

                JPanel dlg = new PnlEval(np, o -> {
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

                            em.getTransaction().commit();

                            // Refresh Display
                            valuecache.get(np.getCategory()).remove(np);
                            contenPanelMap.remove(np);
                            valuecache.get(evaluatedNP.getCategory()).add(evaluatedNP);
                            Collections.sort(valuecache.get(evaluatedNP.getCategory()));

                            createCP4(evaluatedNP.getCategory());
                            buildPanel();
                            GUITools.flashBackground(contenPanelMap.get(evaluatedNP), Color.YELLOW, 2);

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

                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("nursingrecords.nursingprocess.success.neweval")));
                        reloadDisplay();
                    }
                }, false);

                popup.setMovable(false);
                popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
                popup.getContentPane().add(dlg);
                popup.setOwner(btnEval);
                popup.removeExcludedComponent(btnEval);
                popup.setDefaultFocusComponent(dlg);

                GUITools.showPopup(popup, SwingConstants.NORTH_WEST);
            });

            btnEval.setEnabled(!np.isClosed());
            pnlMenu.add(btnEval);


            /***
             *      _     _       _____  _    ____
             *     | |__ | |_ _ _|_   _|/ \  / ___|___
             *     | '_ \| __| '_ \| | / _ \| |  _/ __|
             *     | |_) | |_| | | | |/ ___ \ |_| \__ \
             *     |_.__/ \__|_| |_|_/_/   \_\____|___/
             *
             */
            final JButton btnTAGs = GUITools.createHyperlinkButton("misc.msg.editTags", SYSConst.icon22tagPurple, null);
            btnTAGs.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnTAGs.addActionListener(actionEvent -> {
                final JidePopup popup = new JidePopup();

                final JPanel pnl = new JPanel(new BorderLayout(5, 5));
                final PnlCommonTags pnlCommonTags = new PnlCommonTags(np.getCommontags(), true, 3);
                pnl.add(new JScrollPane(pnlCommonTags), BorderLayout.CENTER);
                JButton btnApply = new JButton(SYSConst.icon22apply);
                pnl.add(btnApply, BorderLayout.SOUTH);
                btnApply.addActionListener(ae -> {
                    EntityManager em = OPDE.createEM();
                    try {

                        em.getTransaction().begin();
                        em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                        NursingProcess myNP = em.merge(np);
                        em.lock(myNP, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                        myNP.getCommontags().clear();
                        for (Commontags commontag : pnlCommonTags.getListSelectedTags()) {
                            myNP.getCommontags().add(em.merge(commontag));
                        }

                        em.getTransaction().commit();

                        // Refresh Display
                        valuecache.get(np.getCategory()).remove(np);
                        contenPanelMap.remove(np);
                        valuecache.get(myNP.getCategory()).add(myNP);
                        Collections.sort(valuecache.get(myNP.getCategory()));

                        boolean reloadSearch = false;
                        for (Commontags ctag : myNP.getCommontags()) {
                            if (!listUsedCommontags.contains(ctag)) {
                                listUsedCommontags.add(ctag);
                                reloadSearch = true;
                            }
                        }
                        if (reloadSearch) {
                            prepareSearchArea();
                        }

                        createCP4(myNP.getCategory());
                        buildPanel();
                        GUITools.flashBackground(contenPanelMap.get(myNP), Color.YELLOW, 2);


                    } catch (OptimisticLockException ole) {
                        OPDE.warn(ole);
                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
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
                });

                popup.setMovable(false);
                popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
                popup.setOwner(btnTAGs);
                popup.removeExcludedComponent(btnTAGs);
                pnl.setPreferredSize(new Dimension(350, 150));
                popup.getContentPane().add(pnl);
                popup.setDefaultFocusComponent(pnl);

                GUITools.showPopup(popup, SwingConstants.WEST);

            });
            btnTAGs.setEnabled(!np.isClosed());
            pnlMenu.add(btnTAGs);

            pnlMenu.add(new JSeparator());

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
            btnFiles.addActionListener(actionEvent -> {
                Closure fileHandleClosure = np.isClosed() ? null : o -> {
                    EntityManager em = OPDE.createEM();
                    final NursingProcess myNP = em.find(NursingProcess.class, np.getID());
                    em.close();
                    // Refresh Display
                    valuecache.get(np.getCategory()).remove(np);
                    contenPanelMap.remove(np);
                    valuecache.get(myNP.getCategory()).add(myNP);
                    Collections.sort(valuecache.get(myNP.getCategory()));
                    currentEditor = null;
                    createCP4(myNP.getCategory());
                    buildPanel();
                };
                currentEditor = new DlgFiles(np, fileHandleClosure);
                currentEditor.setVisible(true);
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
            btnProcess.addActionListener(actionEvent -> {
                currentEditor = new DlgProcessAssign(np, o -> {
                    if (o == null) {
                        currentEditor = null;
                        return;
                    }
                    Pair<ArrayList<QProcess>, ArrayList<QProcess>> result = (Pair<ArrayList<QProcess>, ArrayList<QProcess>>) o;

                    ArrayList<QProcess> assigned = result.getFirst();
                    ArrayList<QProcess> unassigned = result.getSecond();

                    EntityManager em = OPDE.createEM();

                    try {
                        em.getTransaction().begin();

                        em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                        final NursingProcess myNP = em.merge(np);
                        em.lock(myNP, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                        ArrayList<SYSNP2PROCESS> attached = new ArrayList<SYSNP2PROCESS>(myNP.getAttachedQProcessConnections());
                        for (SYSNP2PROCESS linkObject : attached) {
                            if (unassigned.contains(linkObject.getQProcess())) {
                                linkObject.getQProcess().getAttachedNReportConnections().remove(linkObject);
                                linkObject.getNursingProcess().getAttachedQProcessConnections().remove(linkObject);
                                em.merge(new PReport(SYSTools.xx(PReportTools.PREPORT_TEXT_REMOVE_ELEMENT) + ": " + myNP.getTitle() + " ID: " + myNP.getID(), PReportTools.PREPORT_TYPE_REMOVE_ELEMENT, linkObject.getQProcess()));
                                em.remove(linkObject);
                            }
                        }
                        attached.clear();

                        for (QProcess qProcess : assigned) {
                            List<QProcessElement> listElements = qProcess.getElements();
                            if (!listElements.contains(myNP)) {
                                QProcess myQProcess = em.merge(qProcess);
                                SYSNP2PROCESS myLinkObject = em.merge(new SYSNP2PROCESS(myQProcess, myNP));
                                em.merge(new PReport(SYSTools.xx(PReportTools.PREPORT_TEXT_ASSIGN_ELEMENT) + ": " + myNP.getTitle() + " ID: " + myNP.getID(), PReportTools.PREPORT_TYPE_ASSIGN_ELEMENT, myQProcess));
                                qProcess.getAttachedNursingProcessesConnections().add(myLinkObject);
                                myNP.getAttachedQProcessConnections().add(myLinkObject);
                            }
                        }

                        em.getTransaction().commit();

                        // Refresh Display
                        valuecache.get(np.getCategory()).remove(np);
                        contenPanelMap.remove(np);
                        valuecache.get(myNP.getCategory()).add(myNP);
                        Collections.sort(valuecache.get(myNP.getCategory()));

                        createCP4(myNP.getCategory());
                        buildPanel();

                    } catch (OptimisticLockException ole) {
                        OPDE.warn(ole);
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                            OPDE.getMainframe().emptyFrame();
                            OPDE.getMainframe().afterLogin();
                        } else {
                            reloadDisplay();
                        }
                    } catch (RollbackException ole) {
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

                });
                currentEditor.setVisible(true);
            });
            btnProcess.setEnabled(np.isActive() && OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID));
            pnlMenu.add(btnProcess);

        }
        return pnlMenu;
    }


    private boolean isEmpty(final ResInfoCategory cat) {
        return !valuecache.containsKey(cat) || valuecache.get(cat).isEmpty();
    }

    private boolean containsOnlyClosedNPs(final ResInfoCategory cat) {
        boolean containsOnlyClosedNPs = true;
        for (NursingProcess np : valuecache.get(cat)) {
            containsOnlyClosedNPs = np.isClosed();
            if (!containsOnlyClosedNPs) {
                break;
            }
        }
        return containsOnlyClosedNPs;
    }

    private int getMinimumNextEvalDays(final ResInfoCategory cat) {
        int days = Integer.MAX_VALUE;
        for (NursingProcess np : valuecache.get(cat)) {
            if (!np.isClosed())
                days = Math.min(Days.daysBetween(new DateTime(), new DateTime(np.getNextEval())).getDays(), days);
        }
        return days;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane jspNP;
    private CollapsiblePanes cpsPlan;
    // End of variables declaration//GEN-END:variables
}
