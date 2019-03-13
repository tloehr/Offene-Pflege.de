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
import entity.prescription.MedStock;
import entity.prescription.MedStockTools;
import entity.prescription.Prescription;
import entity.prescription.PrescriptionTools;
import entity.process.QProcess;
import entity.process.QProcessTools;
import entity.qms.Qms;
import entity.qms.QmsTools;
import entity.system.CommontagsTools;
import entity.values.ResValue;
import entity.values.ResValueTools;
import gui.GUITools;
import gui.interfaces.CleanablePanel;
import gui.interfaces.DefaultCPTitle;
import op.OPDE;
import op.care.PnlCare;
import op.care.prescription.PnlPrescription;
import op.controlling.PnlControlling;
import op.misc.DlgIntervention;
import op.residents.bwassistant.AddBWWizard;
import op.system.InternalClass;
import op.system.InternalClassACL;
import op.threads.DisplayMessage;
import op.tools.MyJDialog;
import op.tools.Pair;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;

/**
 * @author Torsten Löhr
 */
public class PnlWelcome extends CleanablePanel {
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private java.util.List<QProcess> processList;
    private java.util.List<MedStock> expiryList;
    private java.util.List<Object[]> birthdayList;
    private java.util.List<Prescription> emptyStocksList;  //https://github.com/tloehr/Offene-Pflege.de/issues/102
    // https://www.apotheken-umschau.de/gewichtsverlust
    //https://github.com/tloehr/Offene-Pflege.de/issues/98
    // mehr als 5% in 3 Monaten oder mehr als 10% in 6 Monaten
    private ArrayList<Pair<Resident, BigDecimal>> strangeWeightList;
    private ArrayList<Object[]> noStoolList;
    private ArrayList<Object[]> violatingLiquidValues;
    private ArrayList<Qms> dueQMSes;
    private final int BIRTHDAY = 4;
    private Logger logger = Logger.getLogger(getClass());

    public PnlWelcome(JScrollPane jspSearch) {
        super("opde.welcome");
        this.jspSearch = jspSearch;
        initComponents();
        initPanel();
        reloadDisplay();
    }


    @Override
    public void cleanup() {
        super.cleanup();
        cpsWelcome.removeAll();

        SYSTools.clear(processList);
        SYSTools.clear(birthdayList);
        SYSTools.clear(noStoolList);
        SYSTools.clear(strangeWeightList);
        SYSTools.clear(violatingLiquidValues);
        SYSTools.clear(dueQMSes);

    }

    @Override
    public void reload() {
        reloadDisplay();
    }

    private void initPanel() {

        //https://github.com/tloehr/Offene-Pflege.de/issues/100
        // noch nicht weiter bearbeitet. Nur schonmal rausgenommen.
        // Das kann große Probleme verursachen, wenn der Webserver nicht erreichbar ist.
        // Bzw. Opfer eines DDOS Angriffes ist
//        try {
//            btnAbout.setText(SYSTools.isUpdateAvailable() ? SYSTools.xx("misc.msg.updateAvailable") : null);
//        } catch (IOException e) {
//            logger.warn(e);
//            btnAbout.setText(null);
//        }

        addApps();
        prepareSearchArea();
    }


    private void addApps() {

        Collections.sort(OPDE.getAppInfo().getMainClasses());
        for (InternalClass ic : OPDE.getAppInfo().getMainClasses()) {

            if (!ic.getInternalClassID().equals(internalClassID)
                    && (!ic.getInternalClassID().equals("opde.dev") || (OPDE.isExperimental() && OPDE.isAdmin()))
                    && OPDE.getAppInfo().isAllowedTo(InternalClassACL.EXECUTE, ic.getInternalClassID())) {

                final String shortDescription = ic.getShortDescription();
                final String longDescription = ic.getLongDescription();
                final String javaclass = ic.getJavaclass();

                Icon icon = null;
                try {
                    icon = new ImageIcon(getClass().getResource("/artwork/48x48/" + ic.getIconname()));
                } catch (Exception e) {
                    // bah!
                }

                JideButton progButton = GUITools.createHyperlinkButton(shortDescription, icon, actionEvent -> {
                    OPDE.getMainframe().clearPreviousProgbutton();
                    OPDE.getMainframe().setPanelTo(OPDE.getMainframe().loadPanel(javaclass));
                });
                progButton.setFont(SYSConst.ARIAL20);
                progButton.setToolTipText(SYSTools.toHTMLForScreen("<p style=\"width:150px;\">" + longDescription + "</p>"));

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
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), -1, 100));

        SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                int progress = -1;
                OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, 100));
                emptyStocksList = PrescriptionTools.getAllActiveWithEmptyInventories();
                processList = QProcessTools.getActiveProcesses4(OPDE.getLogin().getUser());
                birthdayList = ResidentTools.getAllWithBirthdayIn(BIRTHDAY);
                expiryList = MedStockTools.getExpiryList(7);
                noStoolList = ResValueTools.getNoStool();
                violatingLiquidValues = ResValueTools.getHighLowIn();
                strangeWeightList = ResValueTools.findNotableWeightChanges(3, new BigDecimal(30));
                dueQMSes = QmsTools.getDueList(OPDE.getLogin().getUser());
                Collections.sort(processList);
                int max = processList.size() + birthdayList.size() + noStoolList.size() + violatingLiquidValues.size() + expiryList.size() + dueQMSes.size() + emptyStocksList.size();

                if (!processList.isEmpty()) {
                    String title = "<html><font size=+1>" +
                            SYSTools.xx("nursingrecords.qprocesses") +
                            "</font></html>";
                    CollapsiblePane cp = new CollapsiblePane(title);
                    JPanel pnlContent = new JPanel(new VerticalLayout());
                    for (QProcess process : processList) {
                        progress++;
                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
                        pnlContent.add(createCP4(process).getMain());
                    }
                    cp.setContentPane(pnlContent);
                    cpsWelcome.add(cp);
                }

                if (!expiryList.isEmpty()) {
                    String title = "<html><font size=+1>" +
                            SYSTools.xx("misc.msg.expiredStocks") +
                            "</font></html>";
                    CollapsiblePane cp = new CollapsiblePane(title);
                    JPanel pnlContent = new JPanel(new VerticalLayout());
                    for (MedStock process : expiryList) {
                        progress++;
                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
                        pnlContent.add(createCP4(process).getMain());
                    }
                    cp.setContentPane(pnlContent);
                    cpsWelcome.add(cp);
                }

                if (!emptyStocksList.isEmpty()) {
                    Collections.sort(emptyStocksList, Comparator.comparing(Prescription::getResident));
                    String title = "<html><font size=+1>" +
                            SYSTools.xx("nursingrecords.handover.emptyInventories") +
                            "</font></html>";

                    CollapsiblePane cp = new CollapsiblePane(title);
                    JPanel pnlContent = new JPanel(new VerticalLayout());
                    for (Prescription prescription : emptyStocksList) {
                        progress++;
                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
                        pnlContent.add(createCP4(prescription).getMain());
                    }
                    cp.setContentPane(pnlContent);
                    cpsWelcome.add(cp);
                }

                if (!birthdayList.isEmpty()) {
                    String title = "<html><font size=+1>" +
                            SYSTools.xx("opde.welcome.birthdayNext") + " " + BIRTHDAY + " " + SYSTools.xx("misc.msg.Days") +
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
                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
                        pnlContent.add(createCP4Birthdays(resident, ((Double) objects[NEWAGE]).intValue(), ((Long) objects[DAYS2BIRTHDAY]).intValue()).getMain());

                    }
                    em.close();


                    cp.setContentPane(pnlContent);
                    cpsWelcome.add(cp);
                }

                if (!noStoolList.isEmpty()) {
                    String title = "<html><font size=+1>" +
                            SYSTools.xx("nursingrecords.vitalparameters.residentsWithNoStool") + "</font></html>";
                    CollapsiblePane cp = new CollapsiblePane(title);
                    JPanel pnlContent = new JPanel(new VerticalLayout());
                    for (Object[] ns : noStoolList) {
                        progress++;
                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
                        pnlContent.add(createCP4NoStool(ns).getMain());
                    }
                    cp.setContentPane(pnlContent);
                    cpsWelcome.add(cp);
                }

                if (!violatingLiquidValues.isEmpty()) {
                    String title = "<html><font size=+1>" +
                            SYSTools.xx("nursingrecords.vitalparameters.residentsWithHighOrLowIn") + "</font></html>";
                    CollapsiblePane cp = new CollapsiblePane(title);
                    JPanel pnlContent = new JPanel(new VerticalLayout());
                    for (Object[] ns : violatingLiquidValues) {
                        progress++;
                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
                        pnlContent.add(createCP4HighLowIn(ns).getMain());
                    }
                    cp.setContentPane(pnlContent);
                    cpsWelcome.add(cp);
                }

                if (!dueQMSes.isEmpty()) {
                    String title = "<html><font size=+1>" +
                            SYSTools.xx("opde.controlling.qms.due.or.overdue") + "</font></html>";
                    CollapsiblePane cp = new CollapsiblePane(title);
                    JPanel pnlContent = new JPanel(new VerticalLayout());
                    for (Qms due : dueQMSes) {
                        progress++;
                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
                        pnlContent.add(createCP4DueQms(due).getMain());
                    }
                    cp.setContentPane(pnlContent);
                    cpsWelcome.add(cp);
                }


                if (max == 0) {
                    JPanel pnlContent = new JPanel(new VerticalLayout());
                    JLabel lbl = new JLabel(SYSTools.xx("misc.msg.noEntries"));
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
                "<td width=\"200\" align=\"left\">" + SYSTools.xx("opde.welcome.lastStool") + ": " +
                (lastStool == null ? SYSTools.xx("misc.msg.noentryyet") : DateFormat.getDateInstance().format(lastStool.getPit())) + "</td>" +
                "<td width=\"200\" align=\"left\">" + SYSTools.xx("controlling.misc.controlPeriod") + ": " +
                daysControl + " " + SYSTools.xx("misc.msg.Days2") + "</td>" +
                "</tr>" +
                "</table>" +
                "</html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, e -> {
            OPDE.getMainframe().clearPreviousProgbutton();
            OPDE.getMainframe().setCurrentResident(resident);
            OPDE.getMainframe().setPanelTo(new PnlCare(resident, jspSearch));
        });
//        cptitle.getTitleButton().setCursor(null);

        if (ResInfoTools.isAway(resident)) {
            cptitle.getButton().setIcon(SYSConst.icon22residentAbsent);
            cptitle.getButton().setVerticalTextPosition(SwingConstants.TOP);
        }

        return cptitle;
    }

    private DefaultCPTitle createCP4DueQms(final Qms due) {

        String title = SYSTools.toHTMLForScreen(QmsTools.toHTML(due)
                + " " + SYSConst.html_bold(due.getQmssched().getMeasure())
                + " [" + SYSConst.html_italic(due.getQmsplan().getTitle())
                + " " + CommontagsTools.getAsHTML(due.getQmsplan().getCommontags(), SYSConst.html_16x16_tagPurple_internal)
                + "]"
        );

        DefaultCPTitle cptitle = new DefaultCPTitle(title, e -> {
            OPDE.getMainframe().clearPreviousProgbutton();
            OPDE.getMainframe().setCurrentResident(null);
            OPDE.getMainframe().setPanelTo(new PnlControlling(jspSearch, due.getQmsplan()));
        });

        return cptitle;
    }

    private DefaultCPTitle createCP4HighLowIn(Object[] ns) {
        final Resident resident = (Resident) ns[0];
        ArrayList<Pair<LocalDate, BigDecimal>> violatingValues = (ArrayList<Pair<LocalDate, BigDecimal>>) ns[1];
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

        for (Pair<LocalDate, BigDecimal> val : violatingValues) {
            title += "<tr valign=\"top\">" +
                    "<td width=\"100\" align=\"left\">" +
                    DateFormat.getDateInstance().format(val.getFirst().toDate()) + "</td>" +
                    "<td width=\"100\" align=\"left\">" +
                    SYSTools.formatBigDecimal(val.getSecond().setScale(2, RoundingMode.HALF_UP)) + " ml</td>";

            // TODO: replace ml with the values of the ResValueTypes
            if (highin != null && highin.compareTo(val.getSecond()) < 0) {
                title += "<td width=\"350\" align=\"left\"><b>" +
                        SYSTools.xx("misc.msg.tooHigh") + "</b>, " + SYSTools.xx("opde.welcome.highin") +
                        ": " + SYSTools.formatBigDecimal(highin.setScale(2, RoundingMode.HALF_UP)) + " ml</td>";
            }
            if (lowin != null && lowin.compareTo(val.getSecond()) > 0) {
                title += "<td width=\"350\" align=\"left\"><b>" +
                        SYSTools.xx("misc.msg.tooLow") + "</b>, " + SYSTools.xx("opde.welcome.lowin") +
                        ": " + SYSTools.formatBigDecimal(lowin.setScale(2, RoundingMode.HALF_UP)) + " ml</td>";
            }

            title += "</tr>";
        }

        title += "</table>";
        title += "</td></tr>";

        title += "</table>" +
                "</html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, e -> {
            OPDE.getMainframe().clearPreviousProgbutton();
            OPDE.getMainframe().setCurrentResident(resident);
            OPDE.getMainframe().setPanelTo(new PnlCare(resident, jspSearch));
        });

        if (ResInfoTools.isAway(resident)) {
            cptitle.getButton().setIcon(SYSConst.icon22residentAbsent);
            cptitle.getButton().setVerticalTextPosition(SwingConstants.TOP);
        }

        return cptitle;
    }

    private DefaultCPTitle createCP4Birthdays(final Resident resident, int newAge, int days2Birthday) {


        String textInTheMiddle = SYSTools.xx("opde.welcome.becomesindays") + " " + days2Birthday + " " + SYSTools.xx("misc.msg.Days");
        if (days2Birthday == 1) {
            textInTheMiddle = SYSTools.xx("misc.msg.becomes") + " " + SYSTools.xx("misc.msg.tomorrow");
        } else if (days2Birthday == 0) {
            textInTheMiddle = SYSTools.xx("misc.msg.becomes") + " " + SYSTools.xx("misc.msg.today");
        }

        String title = "<html><table border=\"0\">" +
                "<tr valign=\"top\">" +
                "<td width=\"100\" align=\"left\">" + DateFormat.getDateInstance().format(resident.getDOB()) + "</td>" +
                "<td width=\"400\" align=\"left\">" +
                "<b>" + ResidentTools.getTextCompact(resident) + "</b> " + textInTheMiddle + " " + newAge + " " + SYSTools.xx("opde.welcome.yearsold") +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, e -> {
            OPDE.getMainframe().clearPreviousProgbutton();
            OPDE.getMainframe().setCurrentResident(resident);
            OPDE.getMainframe().setPanelTo(new PnlCare(resident, jspSearch));
        });
//        cptitle.getTitleButton().setCursor(null);

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

        DefaultCPTitle cptitle = new DefaultCPTitle(title, e -> {
            OPDE.getMainframe().clearPreviousProgbutton();
            OPDE.getMainframe().setPanelTo(OPDE.getMainframe().loadPanel("op.process.PnlProcess"));
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

    private DefaultCPTitle createCP4(final MedStock stock) {

        String title = "<html><table border=\"0\">" +
                "<tr>" +
                "<td width=\"600\" align=\"left\">" + MedStockTools.getAsHTML(stock) + " (" + ResidentTools.getNameAndFirstname(stock.getInventory().getResident()) + ")</td>" +
                "</tr>" +
                "</table>" +


                "</html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, e -> {
            OPDE.getMainframe().clearPreviousProgbutton();
            OPDE.getMainframe().setCurrentResident(stock.getInventory().getResident());
            OPDE.getMainframe().setPanelTo(new PnlCare(stock.getInventory().getResident(), jspSearch));
        });

        return cptitle;
    }

    private DefaultCPTitle createCP4(final Prescription prescription) {

        String title = "<html><table border=\"0\">" +
                "<tr>" +
                "<td width=\"600\" align=\"left\">" + PrescriptionTools.toPrettyHTML(prescription) + " (" + ResidentTools.getNameAndFirstname(prescription.getResident()) + ")</td>" +
                "</tr>" +
                "</table>" +
                "</html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, e -> {
            OPDE.getMainframe().clearPreviousProgbutton();
            OPDE.getMainframe().setCurrentResident(prescription.getResident());
            OPDE.getMainframe().setPanelTo(new PnlPrescription(prescription.getResident(), jspSearch));
        });

        return cptitle;
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
//        GUITools.addAllComponents(mypanel, addFilters());

        searchPane.setContentPane(mypanel);

        searchPanes.add(searchPane);
        searchPanes.addExpansion();


    }

    private void btnAboutActionPerformed(ActionEvent e) {
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI(SYSTools.xx("opde.general.website.url")));
        } catch (IOException ioe) {
            logger.warn(ioe);
        } catch (URISyntaxException use) {
            logger.warn(use);
        }
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        btnAbout = new JButton();
        scrollPane1 = new JScrollPane();
        cpsWelcome = new CollapsiblePanes();
        panel2 = new JScrollPane();
        pnlApps = new JPanel();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== panel1 ========
        {
            panel1.setLayout(new FormLayout(
                    "default:grow, $lcgap, pref",
                    "default, default:grow"));

            //---- btnAbout ----
            btnAbout.setText(null);
            btnAbout.setIcon(new ImageIcon(getClass().getResource("/artwork/64x64/opde-logo.png")));
            btnAbout.setHorizontalAlignment(SwingConstants.TRAILING);
            btnAbout.setFont(new Font("Arial", Font.BOLD, 14));
            btnAbout.setForeground(Color.red);
            btnAbout.setHorizontalTextPosition(SwingConstants.LEADING);
            btnAbout.setBorderPainted(false);
            btnAbout.setContentAreaFilled(false);
            btnAbout.setBorder(null);
            btnAbout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnAbout.addActionListener(e -> btnAboutActionPerformed(e));
            panel1.add(btnAbout, CC.xy(3, 1));

            //======== scrollPane1 ========
            {

                //======== cpsWelcome ========
                {
                    cpsWelcome.setLayout(new BoxLayout(cpsWelcome, BoxLayout.X_AXIS));
                }
                scrollPane1.setViewportView(cpsWelcome);
            }
            panel1.add(scrollPane1, CC.xywh(1, 1, 1, 2, CC.DEFAULT, CC.FILL));

            //======== panel2 ========
            {

                //======== pnlApps ========
                {
                    pnlApps.setLayout(new VerticalLayout(2));
                }
                panel2.setViewportView(pnlApps);
            }
            panel1.add(panel2, CC.xy(3, 2, CC.DEFAULT, CC.FILL));
        }
        add(panel1);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    private java.util.List<Component> addCommands() {
        java.util.List<Component> list = new ArrayList<Component>();

        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, "nursingrecords.info")) { // => ACLMATRIX
            JideButton addbw = GUITools.createHyperlinkButton(SYSTools.xx("opde.welcome.addbw"), SYSConst.icon22addbw, null);
            addbw.addMouseListener(GUITools.getHyperlinkStyleMouseAdapter());

            addbw.setAlignmentX(Component.LEFT_ALIGNMENT);
            addbw.addActionListener(actionEvent -> {
                currentEditor = new MyJDialog(false);
                WizardDialog wizard = new AddBWWizard(o -> {
                    currentEditor.dispose();
                    OPDE.getMainframe().completeRefresh();
                }).getWizard();
                currentEditor.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        super.windowClosing(e);
                        currentEditor = null;
                    }
                });

                currentEditor.setContentPane(wizard.getContentPane());
                currentEditor.pack();
                currentEditor.setSize(new Dimension(800, 550));
                currentEditor.setVisible(true);
            });
            list.add(addbw);
        }


        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, "nursingrecords.nursingprocess")) {
            JideButton editInterventions = GUITools.createHyperlinkButton(SYSTools.xx("opde.welcome.editInterventions"), SYSConst.icon22work, null);
            editInterventions.addMouseListener(GUITools.getHyperlinkStyleMouseAdapter());
            editInterventions.setAlignmentX(Component.LEFT_ALIGNMENT);
            editInterventions.addActionListener(actionEvent -> {
                currentEditor = new DlgIntervention();
                currentEditor.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        super.windowClosing(e);
                        currentEditor = null;
                    }
                });
                currentEditor.setVisible(true);
            });
            list.add(editInterventions);
        }

        return list;
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JButton btnAbout;
    private JScrollPane scrollPane1;
    private CollapsiblePanes cpsWelcome;
    private JScrollPane panel2;
    private JPanel pnlApps;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
