package op.controlling;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideBoxLayout;
import entity.qms.Qmsplan;
import entity.qms.QmsplanTools;
import op.OPDE;
import op.system.InternalClassACL;
import op.threads.DisplayManager;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.VerticalLayout;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tloehr on 17.06.14.
 */
public class PnlQMSPlan extends CleanablePanel {
    public static final String internalClassID = "opde.controlling.qms.pnlqmsplan";
    CollapsiblePanes cpsMain;
    private HashMap<String, CollapsiblePane> cpMap;
    private ArrayList<Qmsplan> listQMSPlans;

    public PnlQMSPlan(ArrayList<Qmsplan> listQMSPlans) {
        this.listQMSPlans = listQMSPlans;
        cpMap = new HashMap<>();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        cpsMain = new CollapsiblePanes();
        add(new JScrollPane(cpsMain));
        reload();
    }

    public ArrayList<Qmsplan> getListQMSPlans() {
        return listQMSPlans;
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
        return internalClassID;
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
                JPanel pnl = getMenu(qmsplan);
                popup.getContentPane().add(pnl);
                popup.setDefaultFocusComponent(pnl);

                GUITools.showPopup(popup, SwingConstants.WEST);
            }
        });

        btnMenu.setEnabled(!qmsplan.isClosed());

        cptitle.getRight().add(btnMenu);

        cpPlan.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
//                JPanel pnlContent = new JPanel(new VerticalLayout());
//
//                int i = 0; // for zebra pattern
//                for (Qmsplan myQmsplan : listQMSPlans) {
//                    //                        if (!np.isClosed()) { // tbInactive.isSelected() ||
//                    JPanel pnl = createContent4(myQmsplan);
//                    pnl.setBackground(i % 2 == 0 ? Color.WHITE : Color.DARK_GRAY);
//                    pnl.setOpaque(true);
//                    pnlContent.add(pnl);
//                    i++;
//                    //                        }
//                }
                cpPlan.setContentPane(createContent4(qmsplan));


            }
        });

        if (!cpPlan.isCollapsed()) {
            cpPlan.setContentPane(createContent4(qmsplan));

        }

        return cpPlan;
    }


    private JPanel createContent4(final Qmsplan qmsplan) {
        JPanel pnl = new JPanel(new VerticalLayout());
//        pnl.setLayout(new BoxLayout(pnl, BoxLayout.X_AXIS));

        String title = SYSTools.toHTMLForScreen(SYSConst.html_paragraph(QmsplanTools.getAsHTML(qmsplan)));

        pnl.add(new JLabel(title));

        return pnl;
    }


    private JPanel getMenu(final Qmsplan qmsplan) {

        final JPanel pnlMenu = new JPanel(new VerticalLayout());
        long numQMS = 0l;//DFNTools.getNumDFNs(np);

        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {


            /***
             *               _ _ _
             *       ___  __| (_) |_
             *      / _ \/ _` | | __|
             *     |  __/ (_| | | |_
             *      \___|\__,_|_|\__|
             *
             */
            JButton btnEdit = GUITools.createHyperlinkButton("misc.commands.edit", SYSConst.icon22edit, null);
            btnEdit.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnEdit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgQMSPlan(qmsplan, new Closure() {
                        @Override
                        public void execute(Object qmsplan) {
                            if (qmsplan != null) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    Qmsplan myQMSPlan = (Qmsplan) em.merge(qmsplan);
                                    em.getTransaction().commit();
                                    listQMSPlans.remove(qmsplan);
                                    listQMSPlans.add(myQMSPlan);
                                    reload();
                                } catch (OptimisticLockException ole) {
                                    OPDE.warn(ole);
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                    if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
                                        OPDE.getMainframe().emptyFrame();
                                        OPDE.getMainframe().afterLogin();
                                    } else {
                                        reload();
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
            btnEdit.setEnabled(!qmsplan.isClosed() && numQMS == 0);
            pnlMenu.add(btnEdit);
        }

        /***
         *          _      _      _
         *       __| | ___| | ___| |_ ___
         *      / _` |/ _ \ |/ _ \ __/ _ \
         *     | (_| |  __/ |  __/ ||  __/
         *      \__,_|\___|_|\___|\__\___|
         *
         */
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.DELETE, internalClassID)) {  // => ACL_MATRIX
            JButton btnDelete = GUITools.createHyperlinkButton("misc.commands.delete", SYSConst.icon22delete, null);
            btnDelete.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnDelete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    new DlgYesNo(OPDE.lang.getString("misc.questions.delete1") + "<br/><b>" + qmsplan.getTitle() + "</b><br/>" + OPDE.lang.getString("misc.questions.delete2"), SYSConst.icon48delete, new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o.equals(JOptionPane.YES_OPTION)) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    Qmsplan myQMSPlan = (Qmsplan) em.merge(qmsplan);
                                    em.remove(myQMSPlan);
                                    em.getTransaction().commit();


                                    // Refresh Display
                                    listQMSPlans.remove(qmsplan);
                                    reload();

                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getSuccessMessage(qmsplan.getTitle(), "deleted"));

                                } catch (OptimisticLockException ole) {
                                    OPDE.warn(ole);
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
            btnDelete.setEnabled(!qmsplan.isClosed() && numQMS == 0);
            pnlMenu.add(btnDelete);
        }


        return pnlMenu;
    }
}
