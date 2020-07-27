/*
 * Created by JFormDesigner on Fri Oct 19 15:24:23 CEST 2012
 */

package de.offene_pflege.op.welcome;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import com.jidesoft.wizard.WizardDialog;
import de.offene_pflege.entity.info.ResInfoTools;
import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.entity.info.ResidentTools;
import de.offene_pflege.entity.prescription.MedStock;
import de.offene_pflege.entity.prescription.MedStockTools;
import de.offene_pflege.entity.prescription.Prescription;
import de.offene_pflege.entity.prescription.PrescriptionTools;
import de.offene_pflege.entity.process.QProcess;
import de.offene_pflege.entity.process.QProcessTools;
import de.offene_pflege.entity.values.ResValue;
import de.offene_pflege.entity.values.ResValueTools;
import de.offene_pflege.services.ResvaluetypesService;
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.gui.interfaces.CleanablePanel;
import de.offene_pflege.gui.interfaces.DefaultCPTitle;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.care.PnlCare;
import de.offene_pflege.op.care.prescription.PnlPrescription;
import de.offene_pflege.op.misc.DlgIntervention;
import de.offene_pflege.op.residents.bwassistant.AddBWWizard;
import de.offene_pflege.op.system.InternalClass;
import de.offene_pflege.op.system.InternalClassACL;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.MyJDialog;
import de.offene_pflege.op.tools.Pair;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.log4j.Logger;
import org.javatuples.Quintet;
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
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * @author Torsten Löhr
 */
public class PnlWelcome extends CleanablePanel {
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private java.util.List<QProcess> processList;
    private java.util.List<MedStock> expiryList;
    private ArrayList<Triple<Resident, Long, Long>> birthdayList;
    //    private java.util.List<Prescription> emptyStocksList;  //https://github.com/tloehr/Offene-Pflege.de/issues/102
    // https://www.apotheken-umschau.de/gewichtsverlust
    // Wer ungewollt mehr als zehn Prozent seines Gewichtes innerhalb weniger Monate verliert, sollte einen Arzt aufsuchen,
    // empfiehlt die Deutsche Gesellschaft für Gastroenterologie, Verdauungs- und Stoffwechselkrankheiten (DGVS).
    // Prozentuale Veränderung
    // (endwert-ausgangswert)/ausgangswert * 100
    // Denn hinter dem ungewollten Abnehmen stecken oft Magen-Darm-Erkankungen.
    // https://github.com/tloehr/Offene-Pflege.de/issues/98
    // mehr als 5% in 3 Monaten oder mehr als 10% in 6 Monaten
    private ArrayList<Quintet<Resident, java.time.LocalDate, java.time.LocalDate, BigDecimal, BigDecimal>> strangeWeightList;
    private ArrayList<Object[]> noStoolList;
    private ArrayList<Object[]> violatingLiquidValues;
    private final int BIRTHDAY = 4;
    private Logger logger = Logger.getLogger(getClass());
    private BigDecimal WEIGHT_PERCENT = new BigDecimal(5);
    private int WEIGHT_MONTHSBACK = 3;

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
                processList = QProcessTools.getActiveProcesses4(OPDE.getLogin().getUser());
                birthdayList = ResidentTools.getAllWithBirthdayIn(BIRTHDAY);
                expiryList = MedStockTools.getExpiryList(7);
                noStoolList = ResValueTools.getNoStool();
                violatingLiquidValues = ResValueTools.getHighLowIn();
                strangeWeightList = ResValueTools.findNotableWeightChanges(WEIGHT_MONTHSBACK, WEIGHT_PERCENT);    // 3 monate, 5%

                Collections.sort(processList);
                int max = processList.size() + birthdayList.size() + noStoolList.size() + violatingLiquidValues.size() + expiryList.size();

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


                if (!strangeWeightList.isEmpty()) {
                    String title = "<html><font size=+1>" +
                            SYSTools.xx("misc.msg.notableWeightChanges") + " " + WEIGHT_MONTHSBACK + " " + SYSTools.xx("misc.msg.months") + ", " +
                            WEIGHT_PERCENT + "%" +
                            "</font></html>";
                    CollapsiblePane cp = new CollapsiblePane(title);
                    JPanel pnlContent = new JPanel(new VerticalLayout());
                    for (Quintet<Resident, java.time.LocalDate, java.time.LocalDate, BigDecimal, BigDecimal> entry : strangeWeightList) {
                        progress++;
                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
                        pnlContent.add(createCP4(entry).getMain());
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

//                if (!emptyStocksList.isEmpty()) {
//                    Collections.sort(emptyStocksList, Comparator.comparing(Prescription::getResident));
//                    String title = "<html><font size=+1>" +
//                            SYSTools.xx("nursingrecords.handover.emptyInventories") +
//                            "</font></html>";
//
//                    CollapsiblePane cp = new CollapsiblePane(title);
//                    JPanel pnlContent = new JPanel(new VerticalLayout());
//                    for (Prescription prescription : emptyStocksList) {
//                        progress++;
//                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
//                        pnlContent.add(createCP4(prescription).getMain());
//                    }
//                    cp.setContentPane(pnlContent);
//                    cpsWelcome.add(cp);
//                }

                if (!birthdayList.isEmpty()) {
                    String title = "<html><font size=+1>" +
                            SYSTools.xx("opde.welcome.birthdayNext") + " " + BIRTHDAY + " " + SYSTools.xx("misc.msg.Days") +
                            "</font></html>";
                    CollapsiblePane cp = new CollapsiblePane(title);
                    JPanel pnlContent = new JPanel(new VerticalLayout());

                    EntityManager em = OPDE.createEM();

                    birthdayList.forEach(residentLongLongTriple -> {

                    });

                    for (Triple<Resident, Long, Long> triple : birthdayList) {
                        progress++;
                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, max));
                        pnlContent.add(createCP4Birthdays(triple.getLeft(), triple.getMiddle(), triple.getRight()).getMain());

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
                try {
                    OPDE.debug("Done");
                    get();
                } catch (ExecutionException e) {
                    e.getCause().printStackTrace();
                    OPDE.debug(e);
                } catch (InterruptedException e) {
                    // Process e here
                } finally {
                    cpsWelcome.addExpansion();
                    OPDE.getDisplayManager().setProgressBarMessage(null);
                    OPDE.getMainframe().setBlocked(false);
                }

            }
        };
        worker.execute();

    }

    private DefaultCPTitle createCP4NoStool(Object[] ns) {
        final Resident resident = (Resident) ns[0];
        Optional<ResValue> lastStool = (Optional<ResValue>) ns[1];
        int daysControl = (Integer) ns[2];

        String title = "<html><table border=\"0\">" +
                "<tr valign=\"top\">" +
                "<td width=\"200\" align=\"left\">" +
                "<b>" + ResidentTools.getTextCompact(resident) + "</b></td>" +
                "<td width=\"200\" align=\"left\">" + SYSTools.xx("opde.welcome.lastStool") + ": " +
                (!lastStool.isPresent() ? SYSTools.xx("misc.msg.noentryyet") : DateFormat.getDateInstance().format(lastStool.get().getPit())) + "</td>" +
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



    private DefaultCPTitle createCP4HighLowIn(Object[] ns) {
        final Resident resident = (Resident) ns[0];
        ArrayList<Pair<LocalDate, BigDecimal>> violatingValues = (ArrayList<Pair<LocalDate, BigDecimal>>) ns[1];
        Properties controlling = ResidentTools.getControlling(resident);

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

            // TODO: replace ml with the values of the Resvaluetypes
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

    /**
     * erstellt den Abschnitt für die Gewichts Warnungen
     *
     * @param entry
     * @return
     */
    private DefaultCPTitle createCP4(Quintet<Resident, java.time.LocalDate, java.time.LocalDate, BigDecimal, BigDecimal> entry) {
        Resident resident = entry.getValue0();
        java.time.LocalDate startDate = entry.getValue1();
        java.time.LocalDate endDate = entry.getValue2();
        BigDecimal absolut = entry.getValue3();
        BigDecimal prozent = entry.getValue4();

        Optional<ResValue> height = ResValueTools.getLast(resident, ResvaluetypesService.HEIGHT);
        Optional<ResValue> weight = ResValueTools.getLast(resident, ResvaluetypesService.WEIGHT);

        String title = "<html><table border=\"0\">" +
                "<tr valign=\"top\">" +
                "<td width=\"200\" align=\"left\">" +
                "<b>" + ResidentTools.getTextCompact(resident) + "</b></td>" +
                "<td width=\"200\" align=\"left\">" +//+ SYSTools.xx("misc.msg.period") + ": " +
                startDate + " &rarr; " + endDate + "</td>" +
                "<td width=\"200\" align=\"left\">" +// + SYSTools.xx("misc.msg.change") + ": " +
                absolut + " kg (" + prozent.setScale(2, RoundingMode.HALF_UP) + "%" + (height.isPresent() && weight.isPresent() ? ", BMI: " + ResValueTools.getBMI(weight.get().getVal1(), height.get().getVal1()) : "") + ")" + "</td>" +
                "</tr>" +
                "</table>" +
                "</html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, e -> {
            OPDE.getMainframe().clearPreviousProgbutton();
            OPDE.getMainframe().setCurrentResident(resident);
            OPDE.getMainframe().setPanelTo(new PnlCare(resident, jspSearch));
        });

        return cptitle;
    }


    public static String format(Period d) {

        return
                (d.getMonths() == 0 ? "" : d.getMonths() + " Monate, ") +
                        (d.getDays() == 0 ? "" : d.getDays() + " Tage");
    }

    private DefaultCPTitle createCP4Birthdays(final Resident resident, long newAge, long days2Birthday) {


        String textInTheMiddle = SYSTools.xx("opde.welcome.becomesindays") + " " + days2Birthday + " " + SYSTools.xx("misc.msg.Days");
        if (days2Birthday == 1) {
            textInTheMiddle = SYSTools.xx("misc.msg.becomes") + " " + SYSTools.xx("misc.msg.tomorrow");
        } else if (days2Birthday == 0) {
            textInTheMiddle = SYSTools.xx("misc.msg.becomes") + " " + SYSTools.xx("misc.msg.today");
        }

        String title = "<html><table border=\"0\">" +
                "<tr valign=\"top\">" +
                "<td width=\"100\" align=\"left\">" + DateFormat.getDateInstance().format(resident.getDob()) + "</td>" +
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
