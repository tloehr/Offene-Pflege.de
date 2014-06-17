package op.controlling;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.swing.JideBoxLayout;
import entity.qms.Qmsplan;
import op.tools.CleanablePanel;
import op.tools.DefaultCPTitle;
import org.jdesktop.swingx.VerticalLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tloehr on 17.06.14.
 */
public class PnlQMSPlan extends CleanablePanel {
    CollapsiblePanes cpsMain;
    private HashMap<String, CollapsiblePane> cpMap;
    private ArrayList<Qmsplan> listQMSPlans;

    public PnlQMSPlan(ArrayList<Qmsplan> listQMSPlans) {
        this.listQMSPlans = listQMSPlans;
        cpMap = new HashMap<>();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        cpsMain = new CollapsiblePanes();
        add(cpsMain);
        reload();
    }

    @Override
    public void cleanup() {
        cpsMain.removeAll();
    }

    @Override
    public void reload() {
        for (Qmsplan qmsplan : listQMSPlans) {
            createCP4(qmsplan);
        }
        buildPanel();
    }

    @Override
    public String getInternalClassID() {
        return null;
    }


    private void buildPanel() {
        cpsMain.removeAll();
        cpsMain.setLayout(new JideBoxLayout(cpsMain, JideBoxLayout.Y_AXIS));
        for (Qmsplan qmsplan : listQMSPlans) {
            cpsMain.add(cpMap.get(qmsplan.getId() + ".qmsplan"));
        }
        cpsMain.addExpansion();
    }


    private CollapsiblePane createCP4(final Qmsplan qmsplan) {
        final String key = qmsplan.getId() + ".qmsplan";
        if (!cpMap.containsKey(key)) {
            cpMap.put(key, new CollapsiblePane());
            try {
                cpMap.get(key).setCollapsed(true);
            } catch (PropertyVetoException e) {
                // Bah!
            }
        }
        final CollapsiblePane cpPlan = cpMap.get(key);

        String title = "<html><font size=+1><b>" +
                qmsplan.getTitle() +
                "</b></font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //                    if (cpCat.isCollapsed() && !tbInactive.isSelected()  && !isEmpty(cat) && containsOnlyClosedNPs(cat)) {
                    //                        tbInactive.setSelected(true);
                    //                    }
                    cpPlan.setCollapsed(!cpPlan.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            }
        });

        cpPlan.setTitleLabelComponent(cptitle.getMain());
        cpPlan.setSlidingDirection(SwingConstants.SOUTH);
//            cpPlan.setBackground(getColor(cat)[SYSConst.medium2]);
        cpPlan.setOpaque(true);
        cpPlan.setHorizontalAlignment(SwingConstants.LEADING);

        cpPlan.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                JPanel pnlContent = new JPanel(new VerticalLayout());
//                    if (valuecache.containsKey(cat)) {
//                        int i = 0; // for zebra pattern
//                        for (NursingProcess np : valuecache.get(cat)) {
//                            //                        if (!np.isClosed()) { // tbInactive.isSelected() ||
//                            JPanel pnl = createNPPanel(np);
//                            pnl.setBackground(i % 2 == 0 ? Color.WHITE : getColor(cat)[SYSConst.light3]);
//                            pnl.setOpaque(true);
//                            pnlContent.add(pnl);
//                            i++;
//                            //                        }
//                        }
//                    }
                cpPlan.setContentPane(pnlContent);
            }
        });

        if (!cpPlan.isCollapsed()) {
            JPanel pnlContent = new JPanel(new VerticalLayout());
//                if (valuecache.containsKey(cat)) {
//                    int i = 0; // for zebra pattern
//                    for (NursingProcess np : valuecache.get(cat)) {
//    //                    if (!np.isClosed()) { // tbInactive.isSelected() ||
//                        JPanel pnl = createNPPanel(np);
//                        pnl.setBackground(i % 2 == 0 ? Color.WHITE : getColor(cat)[SYSConst.light3]);
//                        pnl.setOpaque(true);
//                        pnlContent.add(pnl);
//                        i++;
//    //                    }
//                    }
//                }
            cpPlan.setContentPane(pnlContent);
        }

        return cpPlan;
    }


//    private JPanel createContent4(final Qmsplan qmsplan) {
//
//            String title = "<html><table border=\"0\">" +
//                    "<tr valign=\"top\">" +
//                    "<td width=\"280\" align=\"left\">" + np.getPITAsHTML() + "</td>" +
//                    "<td width=\"500\" align=\"left\">" +
//                    (np.isClosed() ? "<s>" : "") +
//                    np.getContentAsHTML() +
//                    (np.isClosed() ? "</s>" : "") +
//                    "</td>" +
//                    "</table>" +
//                    "</html>";
//
//            DefaultCPTitle cptitle = new DefaultCPTitle(title, null);
//            cptitle.getButton().setVerticalTextPosition(SwingConstants.TOP);
//
//            if (!np.getAttachedFilesConnections().isEmpty()) {
//                /***
//                 *      _     _         _____ _ _
//                 *     | |__ | |_ _ __ |  ___(_) | ___  ___
//                 *     | '_ \| __| '_ \| |_  | | |/ _ \/ __|
//                 *     | |_) | |_| | | |  _| | | |  __/\__ \
//                 *     |_.__/ \__|_| |_|_|   |_|_|\___||___/
//                 *
//                 */
//                final JButton btnFiles = new JButton(Integer.toString(np.getAttachedFilesConnections().size()), SYSConst.icon22greenStar);
//                btnFiles.setToolTipText(OPDE.lang.getString("misc.btnfiles.tooltip"));
//                btnFiles.setForeground(Color.BLUE);
//                btnFiles.setHorizontalTextPosition(SwingUtilities.CENTER);
//                btnFiles.setFont(SYSConst.ARIAL18BOLD);
//                btnFiles.setPressedIcon(SYSConst.icon22Pressed);
//                btnFiles.setAlignmentX(Component.RIGHT_ALIGNMENT);
//                btnFiles.setAlignmentY(Component.TOP_ALIGNMENT);
//                btnFiles.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//                btnFiles.setContentAreaFilled(false);
//                btnFiles.setBorder(null);
//                btnFiles.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent actionEvent) {
//                        Closure fileHandleClosure = np.isClosed() ? null : new Closure() {
//                            @Override
//                            public void execute(Object o) {
//                                EntityManager em = OPDE.createEM();
//                                final NursingProcess myNP = em.find(NursingProcess.class, np.getID());
//                                em.close();
//                                // Refresh Display
//                                valuecache.get(np.getCategory()).remove(np);
//                                contenPanelMap.remove(np);
//                                valuecache.get(myNP.getCategory()).add(myNP);
//                                Collections.sort(valuecache.get(myNP.getCategory()));
//
//                                createCP4(myNP.getCategory());
//                                buildPanel();
//                            }
//                        };
//                        new DlgFiles(np, fileHandleClosure);
//                    }
//                });
//                btnFiles.setEnabled(OPDE.isFTPworking());
//                cptitle.getRight().add(btnFiles);
//            }
//
//            if (!np.getAttachedQProcessConnections().isEmpty()) {
//                /***
//                 *      _     _         ____
//                 *     | |__ | |_ _ __ |  _ \ _ __ ___   ___ ___  ___ ___
//                 *     | '_ \| __| '_ \| |_) | '__/ _ \ / __/ _ \/ __/ __|
//                 *     | |_) | |_| | | |  __/| | | (_) | (_|  __/\__ \__ \
//                 *     |_.__/ \__|_| |_|_|   |_|  \___/ \___\___||___/___/
//                 *
//                 */
//                final JButton btnProcess = new JButton(Integer.toString(np.getAttachedQProcessConnections().size()), SYSConst.icon22redStar);
//                btnProcess.setToolTipText(OPDE.lang.getString("misc.btnprocess.tooltip"));
//                btnProcess.setForeground(Color.YELLOW);
//                btnProcess.setHorizontalTextPosition(SwingUtilities.CENTER);
//                btnProcess.setFont(SYSConst.ARIAL18BOLD);
//                btnProcess.setPressedIcon(SYSConst.icon22Pressed);
//                btnProcess.setAlignmentX(Component.RIGHT_ALIGNMENT);
//                btnProcess.setAlignmentY(Component.TOP_ALIGNMENT);
//                btnProcess.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//                btnProcess.setContentAreaFilled(false);
//                btnProcess.setBorder(null);
//                btnProcess.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent actionEvent) {
//                        new DlgProcessAssign(np, new Closure() {
//                            @Override
//                            public void execute(Object o) {
//                                if (o == null) {
//                                    return;
//                                }
//                                Pair<ArrayList<QProcess>, ArrayList<QProcess>> result = (Pair<ArrayList<QProcess>, ArrayList<QProcess>>) o;
//
//                                ArrayList<QProcess> assigned = result.getFirst();
//                                ArrayList<QProcess> unassigned = result.getSecond();
//
//                                EntityManager em = OPDE.createEM();
//
//                                try {
//                                    em.getTransaction().begin();
//
//                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
//                                    final NursingProcess myNP = em.merge(np);
//                                    em.lock(myNP, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
//
//                                    ArrayList<SYSNP2PROCESS> attached = new ArrayList<SYSNP2PROCESS>(myNP.getAttachedQProcessConnections());
//                                    for (SYSNP2PROCESS linkObject : attached) {
//                                        if (unassigned.contains(linkObject.getQProcess())) {
//                                            linkObject.getQProcess().getAttachedNReportConnections().remove(linkObject);
//                                            linkObject.getNursingProcess().getAttachedQProcessConnections().remove(linkObject);
//                                            em.merge(new PReport(OPDE.lang.getString(PReportTools.PREPORT_TEXT_REMOVE_ELEMENT) + ": " + myNP.getTitle() + " ID: " + myNP.getID(), PReportTools.PREPORT_TYPE_REMOVE_ELEMENT, linkObject.getQProcess()));
//                                            em.remove(linkObject);
//                                        }
//                                    }
//                                    attached.clear();
//
//                                    for (QProcess qProcess : assigned) {
//                                        java.util.List<QProcessElement> listElements = qProcess.getElements();
//                                        if (!listElements.contains(myNP)) {
//                                            QProcess myQProcess = em.merge(qProcess);
//                                            SYSNP2PROCESS myLinkObject = em.merge(new SYSNP2PROCESS(myQProcess, myNP));
//                                            em.merge(new PReport(OPDE.lang.getString(PReportTools.PREPORT_TEXT_ASSIGN_ELEMENT) + ": " + myNP.getTitle() + " ID: " + myNP.getID(), PReportTools.PREPORT_TYPE_ASSIGN_ELEMENT, myQProcess));
//                                            qProcess.getAttachedNursingProcessesConnections().add(myLinkObject);
//                                            myNP.getAttachedQProcessConnections().add(myLinkObject);
//                                        }
//                                    }
//
//                                    em.getTransaction().commit();
//
//                                    // Refresh Display
//                                    valuecache.get(np.getCategory()).remove(np);
//                                    contenPanelMap.remove(np);
//                                    valuecache.get(myNP.getCategory()).add(myNP);
//                                    Collections.sort(valuecache.get(myNP.getCategory()));
//
//                                    createCP4(myNP.getCategory());
//                                    buildPanel();
//
//                                } catch (OptimisticLockException ole) {
//                                    OPDE.warn(ole);
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                        OPDE.getMainframe().emptyFrame();
//                                        OPDE.getMainframe().afterLogin();
//                                    } else {
//                                        reloadDisplay();
//                                    }
//                                } catch (RollbackException ole) {
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                        OPDE.getMainframe().emptyFrame();
//                                        OPDE.getMainframe().afterLogin();
//                                    }
//                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                } catch (Exception e) {
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    OPDE.fatal(e);
//                                } finally {
//                                    em.close();
//                                }
//
//                            }
//                        });
//                    }
//                });
//                btnProcess.setEnabled(OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID));
//                cptitle.getRight().add(btnProcess);
//            }
//
//            if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.PRINT, internalClassID)) {
//                /***
//                 *      _     _         ____       _       _
//                 *     | |__ | |_ _ __ |  _ \ _ __(_)_ __ | |_
//                 *     | '_ \| __| '_ \| |_) | '__| | '_ \| __|
//                 *     | |_) | |_| | | |  __/| |  | | | | | |_
//                 *     |_.__/ \__|_| |_|_|   |_|  |_|_| |_|\__|
//                 *
//                 */
//                JButton btnPrint = new JButton(SYSConst.icon22print2);
//                btnPrint.setContentAreaFilled(false);
//                btnPrint.setBorder(null);
//                btnPrint.setPressedIcon(SYSConst.icon22print2Pressed);
//                btnPrint.setAlignmentX(Component.RIGHT_ALIGNMENT);
//                btnPrint.setAlignmentY(Component.TOP_ALIGNMENT);
//                btnPrint.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent actionEvent) {
//                        SYSFilesTools.print(NursingProcessTools.getAsHTML(np, true, true, true, true), true);
//                    }
//                });
//
//                cptitle.getRight().add(btnPrint);
//                //                cptitle.getButton().setVerticalTextPosition(SwingConstants.TOP);
//            }
//
//
//            /***
//             *      __  __
//             *     |  \/  | ___ _ __  _   _
//             *     | |\/| |/ _ \ '_ \| | | |
//             *     | |  | |  __/ | | | |_| |
//             *     |_|  |_|\___|_| |_|\__,_|
//             *
//             */
//            final JButton btnMenu = new JButton(SYSConst.icon22menu);
//            btnMenu.setPressedIcon(SYSConst.icon22Pressed);
//            btnMenu.setAlignmentX(Component.RIGHT_ALIGNMENT);
//            btnMenu.setAlignmentY(Component.TOP_ALIGNMENT);
//            btnMenu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//            btnMenu.setContentAreaFilled(false);
//            btnMenu.setBorder(null);
//            btnMenu.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    JidePopup popup = new JidePopup();
//                    popup.setMovable(false);
//                    popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
//                    popup.setOwner(btnMenu);
//                    popup.removeExcludedComponent(btnMenu);
//                    JPanel pnl = getMenu(np);
//                    popup.getContentPane().add(pnl);
//                    popup.setDefaultFocusComponent(pnl);
//
//                    GUITools.showPopup(popup, SwingConstants.WEST);
//                }
//            });
//
//            btnMenu.setEnabled(!np.isClosed());
//            cptitle.getButton().setIcon(getIcon(np));
//
//            cptitle.getRight().add(btnMenu);
//            cptitle.getMain().setBackground(getColor(np.getCategory())[SYSConst.light2]);
//            cptitle.getMain().setOpaque(true);
//            contenPanelMap.put(np, cptitle.getMain());
//
//
//        return contenPanelMap.get(np);
//    }
}
