/*
 * Created by JFormDesigner on Fri Oct 19 15:24:23 CEST 2012
 */

package op.welcome;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import entity.info.ResidentTools;
import entity.process.QProcess;
import entity.process.QProcessTools;
import op.OPDE;
import op.process.PnlProcess;
import op.system.InternalClass;
import op.system.InternalClassACL;
import op.tools.CleanablePanel;
import op.tools.DefaultCPTitle;
import op.tools.GUITools;
import op.tools.SYSConst;
import org.jdesktop.swingx.VerticalLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.text.DateFormat;
import java.util.Collections;

/**
 * @author Torsten Löhr
 */
public class PnlWelcome extends CleanablePanel {
    public static final String internalClassID = "opde.welcome";
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private java.util.List<QProcess> processList;

    public PnlWelcome(JScrollPane jspSearch) {
        this.jspSearch = jspSearch;
        initComponents();
        initPanel();
        reloadDisplay();
    }

    @Override
    public void cleanup() {
        cpsWelcome.removeAll();
    }

    @Override
    public void reload() {
        reloadDisplay();
    }

    private void initPanel() {
        addApps();
        prepareSearchArea();
        processList = QProcessTools.getActiveProcesses4(OPDE.getLogin().getUser());
        Collections.sort(processList);
    }


    private void addApps() {

//        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(PnlInfo.internalClassID, InternalClassACL.MANAGER)) { // => ACLMATRIX
//            JideButton addbw = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".addbw"), SYSConst.icon22addbw, null);
////            final MyJDialog dlg = new MyJDialog();
//            addbw.addMouseListener(GUITools.getHyperlinkStyleMouseAdapter());
//            addbw.setAlignmentX(Component.LEFT_ALIGNMENT);
//            addbw.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//                    final MyJDialog dlg = new MyJDialog();
//                    WizardDialog wizard = new AddBWWizard(new Closure() {
//                        @Override
//                        public void execute(Object o) {
//                            dlg.dispose();
//                            jspSearch.removeAll();
//                            jspSearch = null;
//                            jspApps.removeAll();
//                            jspApps = null;
//                            panesSearch.removeAll();
//                            panesSearch = null;
//                            panesApps.removeAll();
//                            panesApps = null;
//                            splitPaneLeft.removeAll();
//                            prepareSearchArea();
//                        }
//                    }).getWizard();
//                    dlg.setContentPane(wizard.getContentPane());
//                    dlg.pack();
//                    dlg.setSize(new Dimension(800, 550));
//                    dlg.setVisible(true);
//                }
//            });
//            mypanel.add(addbw);
//        }

        Collections.sort(OPDE.getAppInfo().getMainClasses());
        for (InternalClass ic : OPDE.getAppInfo().getMainClasses()) {

            if (!ic.getInternalClassID().equals(PnlWelcome.internalClassID) && OPDE.getAppInfo().userHasAccessLevelForThisClass(ic.getInternalClassID(), InternalClassACL.EXECUTE)) {

                final String shortDescription = ic.getShortDescription();
                final String longDescription = ic.getLongDescription();
                final String javaclass = ic.getJavaclass();

                Icon icon = null;
                try {
                    icon = new ImageIcon(getClass().getResource("/artwork/48x48/" + ic.getIconname()));
                } catch (Exception e) {
                    // bah!
                }

                JideButton progButton = GUITools.createHyperlinkButton(shortDescription, icon, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
//
//                        if (previousProgButton != null) {
//                            previousProgButton.setBackground(Color.WHITE);
//                            previousProgButton.setOpaque(false);
//                        }
//                        previousProgButton = (JideButton) actionEvent.getSource();
//                        previousProgButton.setBackground(Color.YELLOW);
//                        previousProgButton.setOpaque(true);
//
//                        displayManager.setMainMessage(shortDescription);
//                        displayManager.addSubMessage(new DisplayMessage(longDescription, 5));
                        OPDE.getMainframe().clearPreviousProgbutton();
                        OPDE.getMainframe().setPanelTo(OPDE.getMainframe().loadPanel(javaclass));
                    }
                });
                progButton.setFont(SYSConst.ARIAL20);
                progButton.setToolTipText(longDescription);

                pnlApps.add(progButton);
            }
        }
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


        cpsWelcome.removeAll();
        cpsWelcome.setLayout(new JideBoxLayout(cpsWelcome, JideBoxLayout.Y_AXIS));

        if (!processList.isEmpty()) {
            String title = "<html><font size=+1>" +
                    OPDE.lang.getString(PnlProcess.internalClassID) +
                    "</font></html>";
            CollapsiblePane cp = new CollapsiblePane(title);
            JPanel pnlContent = new JPanel(new VerticalLayout());
            for (QProcess process : processList) {
                pnlContent.add(createCP4(process).getMain());
            }
            cp.setContentPane(pnlContent);
            cpsWelcome.add(cp);
        }

        cpsWelcome.addExpansion();


    }

    private DefaultCPTitle createCP4(final QProcess qProcess) {
//        final CollapsiblePane cp = new CollapsiblePane();


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

        String title = "<html><font size=+1>" +
                qProcess.getTitle() +
                " <b>" +
                (qProcess.isCommon() ?
                        "" :
                        ResidentTools.getBWLabelTextKompakt(qProcess.getResident())) +
                "</b>, " +
                "[" +
                DateFormat.getDateInstance(DateFormat.SHORT).format(qProcess.getFrom()) + "&rarr;" +
                (qProcess.isClosed() ? DateFormat.getDateInstance(DateFormat.SHORT).format(qProcess.getTo()) : "|") +
                "]" +
                "</font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OPDE.getMainframe().clearPreviousProgbutton();
                OPDE.getMainframe().setPanelTo(OPDE.getMainframe().loadPanel("op.process.PnlProcess"));
            }
        });

        if (qProcess.isRevisionPastDue()) {
            cptitle.getButton().setIcon(SYSConst.icon22ledRedOn);
        } else if (qProcess.isRevisionDue()) {
            cptitle.getButton().setIcon(SYSConst.icon22ledYellowOn);
        } else if (qProcess.isClosed()) {
            cptitle.getButton().setIcon(SYSConst.icon22ledBlueOn);
        } else {
            cptitle.getButton().setIcon(SYSConst.icon22ledGreenOn);
        }


//        /***
//         *      _     _         ____       _       _
//         *     | |__ | |_ _ __ |  _ \ _ __(_)_ __ | |_
//         *     | '_ \| __| '_ \| |_) | '__| | '_ \| __|
//         *     | |_) | |_| | | |  __/| |  | | | | | |_
//         *     |_.__/ \__|_| |_|_|   |_|  |_|_| |_|\__|
//         *
//         */
//        if (OPDE.getAppInfo().userHasAccessLevelForThisClass(internalClassID, InternalClassACL.PRINT)) {
//            final JButton btnPrint = new JButton(SYSConst.icon22print2);
//            btnPrint.setPressedIcon(SYSConst.icon22print2Pressed);
//            btnPrint.setAlignmentX(Component.RIGHT_ALIGNMENT);
//            btnPrint.setContentAreaFilled(false);
//            btnPrint.setBorder(null);
//            btnPrint.setToolTipText(OPDE.lang.getString(internalClassID + ".btnrevision.tooltip"));
//            btnPrint.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//                    String html = QProcessTools.getAsHTML(qProcess);
//                    html += QProcessTools.getElementsAsHTML(qProcess, tbSystem.isSelected());
//                    SYSFilesTools.print(html, true);
//                }
//            });
//            cptitle.getRight().add(btnPrint);
//        }
//
//        /***
//         *      __  __
//         *     |  \/  | ___ _ __  _   _
//         *     | |\/| |/ _ \ '_ \| | | |
//         *     | |  | |  __/ | | | |_| |
//         *     |_|  |_|\___|_| |_|\__,_|
//         *
//         */
//        final JButton btnMenu = new JButton(SYSConst.icon22menu);
//        btnMenu.setPressedIcon(SYSConst.icon22Pressed);
//        btnMenu.setAlignmentX(Component.RIGHT_ALIGNMENT);
//        btnMenu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//        btnMenu.setContentAreaFilled(false);
//        btnMenu.setBorder(null);
//        btnMenu.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JidePopup popup = new JidePopup();
//                popup.setMovable(false);
//                popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
//                popup.setOwner(btnMenu);
//                popup.removeExcludedComponent(btnMenu);
//                JPanel pnl = getMenu(qProcess);
//                popup.getContentPane().add(pnl);
//                popup.setDefaultFocusComponent(pnl);
//
//                GUITools.showPopup(popup, SwingConstants.WEST);
//            }
//        });
//        btnMenu.setEnabled(!qProcess.isClosed());
//        cptitle.getRight().add(btnMenu);

//        cp.addCollapsiblePaneListener(new
//                CollapsiblePaneAdapter() {
//                    @Override
//                    public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
////                        cp.setContentPane(createContentPanel4(qProcess));
////                        cp.setOpaque(false);
//                    }
//                }
//
//        );
//        cp.setBackground(QProcessTools.getBG1(qProcess));
//
//
//        cp.setHorizontalAlignment(SwingConstants.LEADING);
//        cp.setOpaque(false);

        return cptitle;
    }

//    private JPanel createContentPanel4(final QProcess qProcess) {
//        JTextPane contentPane = new JTextPane();
//        contentPane.setContentType("text/html");
//        contentPane.setEditable(false);
//        contentPane.setText(SYSTools.toHTML(QProcessTools.getAsHTML(qProcess)));
//
//        JPanel elementPanel = new JPanel();
//        elementPanel.setLayout(new VerticalLayout());
//        elementPanel.add(contentPane);
//
//        for (final QProcessElement element : qProcess.getElements()) {
//            if (tbSystem.isSelected() || !(element instanceof PReport) || !((PReport) element).isSystem()) {
//                final CollapsiblePane cpElement = createCP4(element, qProcess);
//                if (element instanceof PReport && ((PReport) element).isSystem()) {
//                    cpElement.setIcon(SYSConst.icon16exec);
//                }
//                cpElement.setBackground(QProcessTools.getBG2(qProcess));
//                elementMap.put(element, cpElement);
//                elementPanel.add(elementMap.get(element));
//            }
//        }
//        return elementPanel;
//    }

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

//        GUITools.addAllComponents(mypanel, addCommands());
//        GUITools.addAllComponents(mypanel, addFilters());

        searchPane.setContentPane(mypanel);

        searchPanes.add(searchPane);
        searchPanes.addExpansion();


    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        scrollPane1 = new JScrollPane();
        cpsWelcome = new CollapsiblePanes();
        pnlApps = new JPanel();
        label1 = new JLabel();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== panel1 ========
        {
            panel1.setLayout(new FormLayout(
                "default:grow, $lcgap, default",
                "default:grow"));

            //======== scrollPane1 ========
            {

                //======== cpsWelcome ========
                {
                    cpsWelcome.setLayout(new BoxLayout(cpsWelcome, BoxLayout.X_AXIS));
                }
                scrollPane1.setViewportView(cpsWelcome);
            }
            panel1.add(scrollPane1, CC.xy(1, 1, CC.DEFAULT, CC.FILL));

            //======== pnlApps ========
            {
                pnlApps.setLayout(new VerticalLayout(2));

                //---- label1 ----
                label1.setText(null);
                label1.setIcon(new ImageIcon(getClass().getResource("/artwork/64x64/home.png")));
                label1.setHorizontalAlignment(SwingConstants.TRAILING);
                pnlApps.add(label1);
            }
            panel1.add(pnlApps, CC.xy(3, 1, CC.DEFAULT, CC.FILL));
        }
        add(panel1);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private void buildPanel(boolean collapseAll) {

    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JScrollPane scrollPane1;
    private CollapsiblePanes cpsWelcome;
    private JPanel pnlApps;
    private JLabel label1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
