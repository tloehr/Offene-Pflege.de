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
import com.jidesoft.wizard.WizardDialog;
import entity.info.ResInfoTools;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.process.QProcess;
import entity.process.QProcessTools;
import entity.values.ResValue;
import entity.values.ResValueTools;
import op.OPDE;
import op.care.PnlCare;
import op.care.info.PnlInfo;
import op.care.values.PnlValues;
import op.process.PnlProcess;
import op.residents.bwassistant.AddBWWizard;
import op.system.InternalClass;
import op.system.InternalClassACL;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.DateMidnight;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

/**
 * @author Torsten Löhr
 */
public class PnlWelcome extends CleanablePanel {
    public static final String internalClassID = "opde.welcome";
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private java.util.List<QProcess> processList;
    private java.util.List<Object[]> birthdayList;
    ArrayList<Object[]> noStoolList;
    ArrayList<Object[]> violatingLiquidValues;
    private final int BIRTHDAY = 4;

    public PnlWelcome(JScrollPane jspSearch) {
        this.jspSearch = jspSearch;
        initComponents();
        initPanel();
        reloadDisplay();
    }


    @Override
    public void cleanup() {
        cpsWelcome.removeAll();

        SYSTools.clear(processList);
        SYSTools.clear(birthdayList);
        SYSTools.clear(noStoolList);
        SYSTools.clear(violatingLiquidValues);
    }

    @Override
    public void reload() {
        reloadDisplay();
    }

    private void initPanel() {
        if (OPDE.isUpdateAvailable()) {
            btnAbout.setText(OPDE.lang.getString("misc.msg.updateAvailable"));
        }
        addApps();

        prepareSearchArea();
    }


    private void addApps() {

        Collections.sort(OPDE.getAppInfo().getMainClasses());
        for (InternalClass ic : OPDE.getAppInfo().getMainClasses()) {

            if (!ic.getInternalClassID().equals(PnlWelcome.internalClassID) && OPDE.getAppInfo().isAllowedTo(InternalClassACL.EXECUTE, ic.getInternalClassID())) {

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

    @Override
    public String getInternalClassID() {
        return internalClassID;
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

        OPDE.getMainframe().setBlocked(true);
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));

        SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                int progress = -1;
                OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, 100));
                processList = QProcessTools.getActiveProcesses4(OPDE.getLogin().getUser());
                birthdayList = ResidentTools.getAllWithBirthdayIn(BIRTHDAY);
                noStoolList = ResValueTools.getNoStool();
                violatingLiquidValues = ResValueTools.getHighLowIn();
                Collections.sort(processList);
                int max = processList.size() + birthdayList.size() + noStoolList.size() + violatingLiquidValues.size();

                if (!processList.isEmpty()) {
                    String title = "<html><font size=+1>" +
                            OPDE.lang.getString(PnlProcess.internalClassID) +
                            "</font></html>";
                    CollapsiblePane cp = new CollapsiblePane(title);
                    JPanel pnlContent = new JPanel(new VerticalLayout());
                    for (QProcess process : processList) {
                        progress++;
                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, max));
                        pnlContent.add(createCP4(process).getMain());
                    }
                    cp.setContentPane(pnlContent);
                    cpsWelcome.add(cp);
                }

                if (!birthdayList.isEmpty()) {
                    String title = "<html><font size=+1>" +
                            OPDE.lang.getString(internalClassID + ".birthdayNext") + " " + BIRTHDAY + " " + OPDE.lang.getString("misc.msg.Days") +
                            "</font></html>";
                    CollapsiblePane cp = new CollapsiblePane(title);
                    JPanel pnlContent = new JPanel(new VerticalLayout());

                    final int RESID = 0;
                    final int NEWAGE = 1;
                    final int DAYS2BIRTHDAY = 2;

                    EntityManager em = OPDE.createEM();
                    for (Object[] objects : birthdayList) {
                        Resident resident = em.find(Resident.class, objects[RESID]);

                        progress++;
                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, max));
                        pnlContent.add(createCP4Birthdays(resident, ((Double) objects[NEWAGE]).intValue(), ((Long) objects[DAYS2BIRTHDAY]).intValue()).getMain());

                    }
                    em.close();


                    cp.setContentPane(pnlContent);
                    cpsWelcome.add(cp);
                }

                if (!noStoolList.isEmpty()) {
                    String title = "<html><font size=+1>" +
                            OPDE.lang.getString(PnlValues.internalClassID + ".residentsWithNoStool") + "</font></html>";
                    CollapsiblePane cp = new CollapsiblePane(title);
                    JPanel pnlContent = new JPanel(new VerticalLayout());
                    for (Object[] ns : noStoolList) {
                        progress++;
                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, max));
                        pnlContent.add(createCP4NoStool(ns).getMain());
                    }
                    cp.setContentPane(pnlContent);
                    cpsWelcome.add(cp);
                }

                if (!violatingLiquidValues.isEmpty()) {
                    String title = "<html><font size=+1>" +
                            OPDE.lang.getString(PnlValues.internalClassID + ".residentsWithHighOrLowIn") + "</font></html>";
                    CollapsiblePane cp = new CollapsiblePane(title);
                    JPanel pnlContent = new JPanel(new VerticalLayout());
                    for (Object[] ns : violatingLiquidValues) {
                        progress++;
                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress, max));
                        pnlContent.add(createCP4HighLowIn(ns).getMain());
                    }
                    cp.setContentPane(pnlContent);
                    cpsWelcome.add(cp);
                }

                if (max == 0) {
                    JPanel pnlContent = new JPanel(new VerticalLayout());
                    JLabel lbl = new JLabel(OPDE.lang.getString("misc.msg.noEntries"));
                    lbl.setFont(SYSConst.ARIAL18BOLD);
                    pnlContent.add(lbl);

                    cpsWelcome.add(pnlContent);
                }

                return null;
            }

            @Override
            protected void done() {
                cpsWelcome.addExpansion();
                OPDE.getDisplayManager().setProgressBarMessage(null);
                OPDE.getMainframe().setBlocked(false);
            }
        };
        worker.execute();

    }

    private DefaultCPTitle createCP4NoStool(Object[] ns) {
        final Resident resident = (Resident) ns[0];
        ResValue lastStool = (ResValue) ns[1];
        int daysControl = (Integer) ns[2];

        String title = "<html><table border=\"0\">" +
                "<tr valign=\"top\">" +
                "<td width=\"200\" align=\"left\">" +
                "<b>" + ResidentTools.getTextCompact(resident) + "</b></td>" +
                "<td width=\"200\" align=\"left\">" + OPDE.lang.getString(internalClassID + ".lastStool") + ": " +
                (lastStool == null ? OPDE.lang.getString("misc.msg.noentryyet") : DateFormat.getDateInstance().format(lastStool.getPit())) + "</td>" +
                "<td width=\"200\" align=\"left\">" + OPDE.lang.getString(internalClassID + ".controlPeriod") + ": " +
                daysControl + " " + OPDE.lang.getString("misc.msg.Days2") + "</td>" +
                "</tr>" +
                "</table>" +
                "</html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OPDE.getMainframe().clearPreviousProgbutton();
                OPDE.getMainframe().setCurrentResident(resident);
                OPDE.getMainframe().setPanelTo(new PnlCare(resident, jspSearch));
            }
        });
//        cptitle.getButton().setCursor(null);

        if (ResInfoTools.isAway(resident)) {
            cptitle.getButton().setIcon(SYSConst.icon22residentAbsent);
            cptitle.getButton().setVerticalTextPosition(SwingConstants.TOP);
        }

        return cptitle;
    }

    private DefaultCPTitle createCP4HighLowIn(Object[] ns) {
        final Resident resident = (Resident) ns[0];
        ArrayList<Pair<DateMidnight, BigDecimal>> violatingValues = (ArrayList<Pair<DateMidnight, BigDecimal>>) ns[1];
        Properties controlling = resident.getControlling();

        BigDecimal lowin = null;
        if (controlling.containsKey(ResidentTools.KEY_LOWIN) && !controlling.getProperty(ResidentTools.KEY_LOWIN).equals("off")) {
            lowin = new BigDecimal(controlling.getProperty(ResidentTools.KEY_LOWIN));
        }
        BigDecimal highin = null;
        if (controlling.containsKey(ResidentTools.KEY_HIGHIN) && !controlling.getProperty(ResidentTools.KEY_HIGHIN).equals("off")) {
            highin = new BigDecimal(controlling.getProperty(ResidentTools.KEY_HIGHIN));
        }
//                    (controlling.containsKey(ResidentTools.KEY_HIGHIN) && !controlling.getProperty(ResidentTools.KEY_HIGHIN).equals("off"))) {


        String title = "<html><table border=\"0\">" +
                "<td width=\"450\" align=\"left\">" +
                "<b>" + ResidentTools.getTextCompact(resident) + "</b></td>" +
                "</td>";

        title += "<tr><td>";
        title += "<table border=\"0\">";

        for (Pair<DateMidnight, BigDecimal> val : violatingValues) {
            title += "<tr valign=\"top\">" +
                    "<td width=\"100\" align=\"left\">" +
                    DateFormat.getDateInstance().format(val.getFirst().toDate()) + "</td>" +
                    "<td width=\"100\" align=\"left\">" +
                    val.getSecond().setScale(2, RoundingMode.HALF_UP).toPlainString() + " ml</td>";

            // TODO: replace ml with the values of the ResValueTypes
            if (highin != null && highin.compareTo(val.getSecond()) < 0) {
                title += "<td width=\"350\" align=\"left\"><b>" +
                        OPDE.lang.getString("misc.msg.tooHigh") + "</b>, " + OPDE.lang.getString(internalClassID + ".highin") +
                        ": " + highin.setScale(2, RoundingMode.HALF_UP).toPlainString() + " ml</td>";
            }
            if (lowin != null && lowin.compareTo(val.getSecond()) > 0) {
                title += "<td width=\"350\" align=\"left\"><b>" +
                        OPDE.lang.getString("misc.msg.tooLow") + "</b>, " + OPDE.lang.getString(internalClassID + ".lowin") +
                        ": " + lowin.setScale(2, RoundingMode.HALF_UP).toPlainString() + " ml</td>";
            }

            title += "</tr>";
        }

        title += "</table>";
        title += "</td></tr>";

        title += "</table>" +
                "</html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OPDE.getMainframe().clearPreviousProgbutton();
                OPDE.getMainframe().setCurrentResident(resident);
                OPDE.getMainframe().setPanelTo(new PnlCare(resident, jspSearch));
            }
        });

        if (ResInfoTools.isAway(resident)) {
            cptitle.getButton().setIcon(SYSConst.icon22residentAbsent);
            cptitle.getButton().setVerticalTextPosition(SwingConstants.TOP);
        }

        return cptitle;
    }

    private DefaultCPTitle createCP4Birthdays(final Resident resident, int newAge, int days2Birthday) {


        String textInTheMiddle = OPDE.lang.getString(internalClassID + ".becomesindays") + days2Birthday + OPDE.lang.getString("misc.msg.Days");
        if (days2Birthday == 1) {
            textInTheMiddle = OPDE.lang.getString("misc.msg.becomes") + " " + OPDE.lang.getString("misc.msg.tomorrow");
        } else if (days2Birthday == 0) {
            textInTheMiddle = OPDE.lang.getString("misc.msg.becomes") + " " + OPDE.lang.getString("misc.msg.today");
        }

        String title = "<html><table border=\"0\">" +
                "<tr valign=\"top\">" +
                "<td width=\"100\" align=\"left\">" + DateFormat.getDateInstance().format(resident.getDOB()) + "</td>" +
                "<td width=\"400\" align=\"left\">" +
                "<b>" + ResidentTools.getTextCompact(resident) + "</b> " + textInTheMiddle + " " + newAge + " " + OPDE.lang.getString(internalClassID + ".yearsold") +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OPDE.getMainframe().clearPreviousProgbutton();
                OPDE.getMainframe().setCurrentResident(resident);
                OPDE.getMainframe().setPanelTo(new PnlCare(resident, jspSearch));
            }
        });
//        cptitle.getButton().setCursor(null);

        if (ResInfoTools.isAway(resident)) {
            cptitle.getButton().setIcon(SYSConst.icon22residentAbsent);
            cptitle.getButton().setVerticalTextPosition(SwingConstants.TOP);
        }

        return cptitle;
    }

    private DefaultCPTitle createCP4(final QProcess qProcess) {

        String title = "<html><table border=\"0\">" +
                "<tr valign=\"top\">" +
                "<td width=\"100\" align=\"left\">" + qProcess.getPITAsHTML() + "</td>" +
                "<td width=\"100\" align=\"left\">" + " <b>" +
                (qProcess.isCommon() ?
                        "" :
                        ResidentTools.getTextCompact(qProcess.getResident())) +
                "</b>, "
                + "</td>" +
                "<td width=\"400\" align=\"left\">" +
                (qProcess.isClosed() ? "<s>" : "") +
                qProcess.getTitle() +
                (qProcess.isClosed() ? "</s>" : "") +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</html>";

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
            cptitle.getButton().setIcon(SYSConst.icon22stopSign);
        } else {
            cptitle.getButton().setIcon(SYSConst.icon22ledGreenOn);
        }
        cptitle.getButton().setVerticalTextPosition(SwingConstants.TOP);

        return cptitle;
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
//        GUITools.addAllComponents(mypanel, addFilters());

        searchPane.setContentPane(mypanel);

        searchPanes.add(searchPane);
        searchPanes.addExpansion();


    }

    private void btnAboutActionPerformed(ActionEvent e) {
        Desktop desktop = Desktop.getDesktop();
        try {
            if (OPDE.isUpdateAvailable()) {
                desktop.browse(new URI("https://www.offene-pflege.de/de/versionen"));
            } else {
                desktop.browse(new URI("https://www.offene-pflege.de"));
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (URISyntaxException use) {
            use.printStackTrace();

        }
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        scrollPane1 = new JScrollPane();
        cpsWelcome = new CollapsiblePanes();
        pnlApps = new JPanel();
        btnAbout = new JButton();

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

                //---- btnAbout ----
                btnAbout.setText(null);
                btnAbout.setIcon(new ImageIcon(getClass().getResource("/artwork/64x64/OPDE-blue.png")));
                btnAbout.setHorizontalAlignment(SwingConstants.TRAILING);
                btnAbout.setFont(new Font("Arial", Font.BOLD, 14));
                btnAbout.setForeground(Color.red);
                btnAbout.setHorizontalTextPosition(SwingConstants.LEADING);
                btnAbout.setBorderPainted(false);
                btnAbout.setContentAreaFilled(false);
                btnAbout.setBorder(null);
                btnAbout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnAbout.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnAboutActionPerformed(e);
                    }
                });
                pnlApps.add(btnAbout);
            }
            panel1.add(pnlApps, CC.xy(3, 1, CC.DEFAULT, CC.FILL));
        }
        add(panel1);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    private java.util.List<Component> addCommands() {
        java.util.List<Component> list = new ArrayList<Component>();

        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, PnlInfo.internalClassID)) { // => ACLMATRIX
            JideButton addbw = GUITools.createHyperlinkButton(OPDE.lang.getString(internalClassID + ".addbw"), SYSConst.icon22addbw, null);
//            final MyJDialog dlg = new MyJDialog();
            addbw.addMouseListener(GUITools.getHyperlinkStyleMouseAdapter());
            addbw.setAlignmentX(Component.LEFT_ALIGNMENT);
            addbw.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    final MyJDialog dlg = new MyJDialog(false);
                    WizardDialog wizard = new AddBWWizard(new Closure() {
                        @Override
                        public void execute(Object o) {
                            dlg.dispose();
                            OPDE.getMainframe().completeRefresh();
                        }
                    }).getWizard();
                    dlg.setContentPane(wizard.getContentPane());
                    dlg.pack();
                    dlg.setSize(new Dimension(800, 550));
                    dlg.setVisible(true);
                }
            });
            list.add(addbw);
        }


        return list;
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JScrollPane scrollPane1;
    private CollapsiblePanes cpsWelcome;
    private JPanel pnlApps;
    private JButton btnAbout;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
