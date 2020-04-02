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
package de.offene_pflege.op.controlling;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideTabbedPane;
import com.toedter.calendar.JDateChooser;
import de.offene_pflege.entity.building.Homes;
import de.offene_pflege.entity.building.HomesTools;
import de.offene_pflege.entity.building.Station;
import de.offene_pflege.entity.building.StationTools;
import de.offene_pflege.entity.files.SYSFilesTools;
import de.offene_pflege.entity.info.*;
import de.offene_pflege.entity.prescription.MedStockTools;
import de.offene_pflege.entity.process.QProcessElement;
import de.offene_pflege.entity.process.QProcessTools;
import de.offene_pflege.entity.qms.ControllingTools;
import de.offene_pflege.entity.reports.NReportTools;
import de.offene_pflege.entity.system.Commontags;
import de.offene_pflege.entity.system.CommontagsTools;
import de.offene_pflege.entity.system.SYSPropsTools;
import de.offene_pflege.entity.values.ResValueTools;
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.gui.interfaces.CleanablePanel;
import de.offene_pflege.gui.interfaces.DefaultCPTitle;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.system.InternalClassACL;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.*;
import org.apache.commons.collections.Closure;
import org.apache.commons.io.FileUtils;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * ACL ============ EXECUTE      Start this sub program and use the controlling queries UPDATE       Enter and edit new
 * / existing Qmsplans and Qmsschedules. Access the QMS tab in general. DELETE       Delete Qmsplan with everything
 * connected to it. But only when unused. MANAGER      show staff controlling tab and allow to delete even USED
 * Qmsplans. USER1        Can access the organisational request tab.
 *
 * @author tloehr
 */
public class PnlControlling extends CleanablePanel implements HasLogger {

    private JScrollPane jspSearch;

    Format monthFormatter = new SimpleDateFormat("MMMM yyyy");
    private Closure progressClosure;
    private CollapsiblePanes searchPanes;
    private boolean init = false;
    private Commontags filterTag = null;

    // Variables declaration - do not modify
    //GEN-BEGIN:variables
    private JideTabbedPane tabMain;
    private JScrollPane scrollPane1;
    private CollapsiblePanes cpsControlling;
    private JPanel panel2;
    // End of variables declaration//GEN-END:variables


//    JCheckBox[] jbs = null;
//    JFrame parent;
//    boolean isCancelled;
//    Object[] o = null;
//


    /**
     * Creates new form PnlControlling
     */
    public PnlControlling(JScrollPane jspSearch) {
        super("opde.controlling");
        this.jspSearch = jspSearch;
        progressClosure = o -> {
            Pair<Integer, Integer> progress = (Pair<Integer, Integer>) o;
            OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress.getFirst(), progress.getSecond()));
        };
        initComponents();

//        initPanel();
    }

    private void initPanel() {
//        pnlQMSPlan = null;
//        tabMain.setTitleAt(TAB_CONTROLLING, SYSTools.xx("opde.controlling.tab.controlling"));
////        tabMain.setTitleAt(TAB_QMS, SYSTools.xx("opde.controlling.tab.qms"));
//        tabMain.setTitleAt(TAB_QMSPLAN, SYSTools.xx("opde.controlling.tab.qmsplan"));
//        tabMain.setEnabledAt(TAB_QMSPLAN, OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, getInternalClassID()));
//
//        if (showMeFirst != null) {
//            tabMain.setSelectedIndex(TAB_QMSPLAN);
//        } else {
//            reload();
//        }


    }


    private CollapsiblePane createCP4Fall() {
        final CollapsiblePane cpPain = new CollapsiblePane();

        String title = "<html><font size=+1>" +
                SYSTools.xx("opde.controlling.fall") +
                "</font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, e -> {
            try {
                cpPain.setCollapsed(!cpPain.isCollapsed());
            } catch (PropertyVetoException pve) {
                // BAH!
            }
        });
        cpPain.setTitleLabelComponent(cptitle.getMain());
        cpPain.setSlidingDirection(SwingConstants.SOUTH);
        cpPain.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                cpPain.setContentPane(createContentPanel4Fall());
            }
        });

        if (!cpPain.isCollapsed()) {
            cpPain.setContentPane(createContentPanel4Fall());
        }

        cpPain.setHorizontalAlignment(SwingConstants.LEADING);
        //        cpOrga.setOpaque(false);
        //        cpOrga.setBackground(getColor(vtype, SYSConst.medium1));

        return cpPain;
    }


    private CollapsiblePane createCP4Pain() {
        final CollapsiblePane cpPain = new CollapsiblePane();

        String title = "<html><font size=+1>" +
                SYSTools.xx("opde.controlling.pain") +
                "</font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, e -> {
            try {
                cpPain.setCollapsed(!cpPain.isCollapsed());
            } catch (PropertyVetoException pve) {
                // BAH!
            }
        });
        cpPain.setTitleLabelComponent(cptitle.getMain());
        cpPain.setSlidingDirection(SwingConstants.SOUTH);
        cpPain.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                cpPain.setContentPane(createContentPanel4Pain());
            }
        });

        if (!cpPain.isCollapsed()) {
            cpPain.setContentPane(createContentPanel4Pain());
        }

        cpPain.setHorizontalAlignment(SwingConstants.LEADING);
        //        cpOrga.setOpaque(false);
        //        cpOrga.setBackground(getColor(vtype, SYSConst.medium1));

        return cpPain;
    }


    private CollapsiblePane createCP4Orga() {
        final CollapsiblePane cpOrga = new CollapsiblePane();

        String title = "<html><font size=+1>" +
                SYSTools.xx("opde.controlling.orga") +
                "</font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, e -> {
            try {
                cpOrga.setCollapsed(!cpOrga.isCollapsed());
            } catch (PropertyVetoException pve) {
                // BAH!
            }
        });
        cpOrga.setTitleLabelComponent(cptitle.getMain());
        cpOrga.setSlidingDirection(SwingConstants.SOUTH);
        cpOrga.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                cpOrga.setContentPane(createContentPanel4Orga());
            }
        });

        if (!cpOrga.isCollapsed()) {
            cpOrga.setContentPane(createContentPanel4Orga());
        }

        cpOrga.setHorizontalAlignment(SwingConstants.LEADING);
//        cpOrga.setOpaque(false);
//        cpOrga.setBackground(getColor(vtype, SYSConst.medium1));

        return cpOrga;
    }


    private CollapsiblePane createCP4Hygiene() {
        final CollapsiblePane cpHygiene = new CollapsiblePane();

        String title = "<html><font size=+1>" +
                SYSTools.xx("opde.controlling.hygiene") +
                "</font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, e -> {
            try {
                cpHygiene.setCollapsed(!cpHygiene.isCollapsed());
            } catch (PropertyVetoException pve) {
                // BAH!
            }
        });
        cpHygiene.setTitleLabelComponent(cptitle.getMain());
        cpHygiene.setSlidingDirection(SwingConstants.SOUTH);
        cpHygiene.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                cpHygiene.setContentPane(createContentPanel4Hygiene());
            }
        });

        if (!cpHygiene.isCollapsed()) {
            cpHygiene.setContentPane(createContentPanel4Hygiene());
        }

        cpHygiene.setHorizontalAlignment(SwingConstants.LEADING);
        //        cpOrga.setOpaque(false);
        //        cpOrga.setBackground(getColor(vtype, SYSConst.medium1));

        return cpHygiene;
    }

    private CollapsiblePane createCP4Nutrition() {
        final CollapsiblePane cpOrga = new CollapsiblePane();

        String title = "<html><font size=+1>" +
                SYSTools.xx("opde.controlling.nutrition") +
                "</font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, e -> {
            try {
                cpOrga.setCollapsed(!cpOrga.isCollapsed());
            } catch (PropertyVetoException pve) {
                // BAH!
            }
        });
        cpOrga.setTitleLabelComponent(cptitle.getMain());
        cpOrga.setSlidingDirection(SwingConstants.SOUTH);
        cpOrga.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                cpOrga.setContentPane(createContentPanel4Nutrition());
            }
        });

        if (!cpOrga.isCollapsed()) {
            cpOrga.setContentPane(createContentPanel4Nutrition());
        }

        cpOrga.setHorizontalAlignment(SwingConstants.LEADING);
//        cpOrga.setOpaque(false);
//        cpOrga.setBackground(getColor(vtype, SYSConst.medium1));

        return cpOrga;
    }

    private CollapsiblePane createCP4Drugs() {
        final CollapsiblePane cpDrugs = new CollapsiblePane();

        String title = "<html><font size=+1>" +
                SYSTools.xx("opde.controlling.drugs") +
                "</font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, e -> {
            try {
                cpDrugs.setCollapsed(!cpDrugs.isCollapsed());
            } catch (PropertyVetoException pve) {
                // BAH!
            }
        });

        cpDrugs.setTitleLabelComponent(cptitle.getMain());
        cpDrugs.setSlidingDirection(SwingConstants.SOUTH);
        cpDrugs.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                cpDrugs.setContentPane(createContentPanel4Drugs());
            }
        });

        if (!cpDrugs.isCollapsed()) {
            cpDrugs.setContentPane(createContentPanel4Drugs());
        }

        cpDrugs.setHorizontalAlignment(SwingConstants.LEADING);

        return cpDrugs;
    }


    private JPanel createContentPanel4Hygiene() {
        JPanel pnlContent = new JPanel(new VerticalLayout());

        JPanel pnlPrevalence = new JPanel(new BorderLayout());
        final JButton btnPrevalence = GUITools.createHyperlinkButton("opde.controlling.hygiene.prevalence", null, null);
        final JDateChooser jdc = new JDateChooser(new Date());
        final JCheckBox cbAnonymous = new JCheckBox(SYSTools.xx("misc.msg.anon"));
        final JComboBox<Homes> cmbHomes = new JComboBox<>();
        HomesTools.setComboBox(cmbHomes);


        SYSPropsTools.restoreState("opde.controlling:prevalence::cbAnonymous", cbAnonymous);
        cbAnonymous.addItemListener(e -> SYSPropsTools.storeState("opde.controlling:prevalence::cbAnonymous", cbAnonymous));

        jdc.setMaxSelectableDate(new Date());

        btnPrevalence.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OPDE.getMainframe().setBlocked(true);
                SwingWorker worker = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        MREPrevalenceSheets mre = new MREPrevalenceSheets(new LocalDate(jdc.getDate()), (Homes) cmbHomes.getSelectedItem(), cbAnonymous.isSelected(), progressClosure);
                        return mre.createSheet();
                    }

                    @Override
                    protected void done() {
                        try {
                            File source = (File) get();

                            Object[] options = {
                                    SYSTools.xx("prevalence.optiondialog.option1"),
                                    SYSTools.xx("prevalence.optiondialog.option2"),
                                    SYSTools.xx("opde.wizards.buttontext.cancel")
                            };

                            int n = JOptionPane.showOptionDialog(OPDE.getMainframe(),
                                    SYSTools.xx("prevalence.optiondialog.question"),
                                    SYSTools.xx("prevalence.optiondialog.title"),
                                    JOptionPane.YES_NO_CANCEL_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    options,
                                    options[1]);

                            File copyTargetDirectory = null;

                            if (n == 1) {
                                JFileChooser chooser = new JFileChooser();
                                chooser.setDialogTitle("title");
                                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                                chooser.setMultiSelectionEnabled(false);
                                chooser.setAcceptAllFileFilterUsed(false);

                                if (chooser.showOpenDialog(pnlPrevalence) == JFileChooser.APPROVE_OPTION) {
                                    copyTargetDirectory = chooser.getSelectedFile();
                                }
                            }


                            if (copyTargetDirectory != null) {
                                FileUtils.copyFileToDirectory(source, copyTargetDirectory);
                            } else {
                                if (n == 0) {
                                    SYSFilesTools.handleFile((File) get(), Desktop.Action.OPEN);
                                }
                            }
                        } catch (IOException ioe) {
                            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(ioe.getMessage(), DisplayMessage.WARNING));
                            getLogger().warn(ioe);
                        } catch (Exception e) {
                            getLogger().fatal(e);
                        }

                        OPDE.getDisplayManager().setProgressBarMessage(null);
                        OPDE.getMainframe().setBlocked(false);
                    }
                };
                worker.execute();
            }
        });
        pnlPrevalence.add(btnPrevalence, BorderLayout.WEST);

        JPanel optionPanel = new JPanel();
        optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.LINE_AXIS));

        optionPanel.add(cbAnonymous);
        optionPanel.add(cmbHomes);
        optionPanel.add(jdc);

        pnlPrevalence.add(optionPanel, BorderLayout.EAST);
        pnlContent.add(pnlPrevalence);


        return pnlContent;
    }

    private CollapsiblePane createCP4Nursing() {
        final CollapsiblePane cpNursing = new CollapsiblePane();

        String title = "<html><font size=+1>" +
                SYSTools.xx("opde.controlling.nursing") +
                "</font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, e -> {
            try {
                cpNursing.setCollapsed(!cpNursing.isCollapsed());
            } catch (PropertyVetoException pve) {
                // BAH!
            }
        });
        cpNursing.setTitleLabelComponent(cptitle.getMain());
        cpNursing.setSlidingDirection(SwingConstants.SOUTH);
        cpNursing.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                cpNursing.setContentPane(createContentPanel4Nursing());
            }
        });

        if (!cpNursing.isCollapsed()) {
            cpNursing.setContentPane(createContentPanel4Nursing());
        }

        cpNursing.setHorizontalAlignment(SwingConstants.LEADING);


        return cpNursing;
    }

    private JPanel createContentPanel4Pain() {
        JPanel pnlContent = new JPanel(new VerticalLayout());

        JPanel pnlPainDossier = new JPanel(new BorderLayout());
        final JButton btnBVActivities = GUITools.createHyperlinkButton("opde.controlling.orga.paindossier", null, null);
        int monthsBack;
        try {
            monthsBack = Integer.parseInt(OPDE.getProps().getProperty("opde.controlling::paindossiermonthsback"));
        } catch (NumberFormatException nfe) {
            monthsBack = 1;
        }
        final JTextField txtPainMonthsBack = GUITools.createIntegerTextField(1, 52, monthsBack);
        txtPainMonthsBack.setToolTipText(SYSTools.xx("misc.msg.monthsback"));
        btnBVActivities.addActionListener(e -> {

            OPDE.getMainframe().setBlocked(true);
            SwingWorker worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    SYSPropsTools.storeProp("opde.controlling::paindossiermonthsback", txtPainMonthsBack.getText(), OPDE.getLogin().getUser());

                    return ControllingTools.getPainDossierAsHTML(new LocalDate().minusMonths(Integer.parseInt(txtPainMonthsBack.getText())), new LocalDate(), progressClosure);
                }

                @Override
                protected void done() {
                    try {
                        SYSFilesTools.print(get().toString(), false);
                    } catch (Exception e1) {
                        OPDE.fatal(e1);
                    }

                    OPDE.getDisplayManager().setProgressBarMessage(null);
                    OPDE.getMainframe().setBlocked(false);
                }
            };
            worker.execute();
        });
        pnlPainDossier.add(btnBVActivities, BorderLayout.WEST);
        pnlPainDossier.add(txtPainMonthsBack, BorderLayout.EAST);
        pnlContent.add(pnlPainDossier);


        return pnlContent;
    }

    private JPanel createContentPanel4Fall() {
        JPanel pnlContent = new JPanel(new VerticalLayout());

        /***
         *      _____     _ _          _
         *     |  ___|_ _| | |___     / \   _ __   ___  _ __  _   _ _ __ ___   ___  _   _ ___
         *     | |_ / _` | | / __|   / _ \ | '_ \ / _ \| '_ \| | | | '_ ` _ \ / _ \| | | / __|
         *     |  _| (_| | | \__ \  / ___ \| | | | (_) | | | | |_| | | | | | | (_) | |_| \__ \
         *     |_|  \__,_|_|_|___/ /_/   \_\_| |_|\___/|_| |_|\__, |_| |_| |_|\___/ \__,_|___/
         *                                                    |___/
         */
        JPanel pnlFallsAnon = new JPanel(new BorderLayout());
        final JButton btnFallsAnon = GUITools.createHyperlinkButton("opde.controlling.nursing.falls.anonymous", null, null);
        int fallsMonthsBack;
        try {
            fallsMonthsBack = Integer.parseInt(OPDE.getProps().getProperty("opde.controlling::fallsMonthsBack"));
        } catch (NumberFormatException nfe) {
            fallsMonthsBack = 7;
        }
        final JTextField txtFallsMonthsBack = GUITools.createIntegerTextField(1, 120, fallsMonthsBack);
        txtFallsMonthsBack.setToolTipText(SYSTools.xx("misc.msg.monthsback"));

        btnFallsAnon.addActionListener(e -> {
            OPDE.getMainframe().setBlocked(true);
            SwingWorker worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    SYSPropsTools.storeProp("opde.controlling::fallsMonthsBack", txtFallsMonthsBack.getText(), OPDE.getLogin().getUser());
                    SYSFilesTools.print(ResInfoTools.getFallsAnonymous(Integer.parseInt(txtFallsMonthsBack.getText()), progressClosure), false);
                    return null;
                }

                @Override
                protected void done() {
                    OPDE.getDisplayManager().setProgressBarMessage(null);
                    OPDE.getMainframe().setBlocked(false);
                }
            };
            worker.execute();
        });
        pnlFallsAnon.add(btnFallsAnon, BorderLayout.WEST);
        pnlFallsAnon.add(txtFallsMonthsBack, BorderLayout.EAST);
        pnlContent.add(pnlFallsAnon);

        /***
         *      _____     _ _       _           ____           _     _            _
         *     |  ___|_ _| | |___  | |__  _   _|  _ \ ___  ___(_) __| | ___ _ __ | |_
         *     | |_ / _` | | / __| | '_ \| | | | |_) / _ \/ __| |/ _` |/ _ \ '_ \| __|
         *     |  _| (_| | | \__ \ | |_) | |_| |  _ <  __/\__ \ | (_| |  __/ | | | |_
         *     |_|  \__,_|_|_|___/ |_.__/ \__, |_| \_\___||___/_|\__,_|\___|_| |_|\__|
         *                                |___/
         */
        JPanel pnlFallsRes = new JPanel(new BorderLayout());
        final JButton btnFallsRes = GUITools.createHyperlinkButton("opde.controlling.nursing.falls.byResident", null, null);
        int fallsResMonthsBack;
        try {
            fallsResMonthsBack = Integer.parseInt(OPDE.getProps().getProperty("opde.controlling::fallsResMonthsBack"));
        } catch (NumberFormatException nfe) {
            fallsResMonthsBack = 7;
        }
        final JTextField txtResFallsMonthsBack = GUITools.createIntegerTextField(1, 120, fallsResMonthsBack);
        txtResFallsMonthsBack.setToolTipText(SYSTools.xx("misc.msg.monthsback"));

        btnFallsRes.addActionListener(e -> {
            OPDE.getMainframe().setBlocked(true);
            SwingWorker worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    SYSPropsTools.storeProp("opde.controlling::fallsResMonthsBack", txtResFallsMonthsBack.getText(), OPDE.getLogin().getUser());
                    SYSFilesTools.print(ResInfoTools.getFallsByResidents(Integer.parseInt(txtResFallsMonthsBack.getText()), progressClosure), false);
                    return null;
                }

                @Override
                protected void done() {
                    OPDE.getDisplayManager().setProgressBarMessage(null);
                    OPDE.getMainframe().setBlocked(false);
                }
            };
            worker.execute();
        });
        pnlFallsRes.add(btnFallsRes, BorderLayout.WEST);
        pnlFallsRes.add(txtResFallsMonthsBack, BorderLayout.EAST);
        pnlContent.add(pnlFallsRes);


        /***
         *      _____     _ _     ___           _ _           _
         *     |  ___|_ _| | |___|_ _|_ __   __| (_) ___ __ _| |_ ___  _ __
         *     | |_ / _` | | / __|| || '_ \ / _` | |/ __/ _` | __/ _ \| '__|
         *     |  _| (_| | | \__ \| || | | | (_| | | (_| (_| | || (_) | |
         *     |_|  \__,_|_|_|___/___|_| |_|\__,_|_|\___\__,_|\__\___/|_|
         *
         */
        JPanel pnlFallsIndicator = new JPanel(new BorderLayout());
        final JButton btnFallsIndicator = GUITools.createHyperlinkButton("opde.controlling.nursing.fallsindicators.byMonth", null, null);
        int fallsIndicatorBack;
        try {
            fallsIndicatorBack = Integer.parseInt(OPDE.getProps().getProperty("opde.controlling::fallsIndicatorMonthsBack"));
        } catch (NumberFormatException nfe) {
            fallsIndicatorBack = 7;
        }
        final JTextField txtFallsIndicatorsMonthsBack = GUITools.createIntegerTextField(1, 120, fallsIndicatorBack);
        txtFallsIndicatorsMonthsBack.setToolTipText(SYSTools.xx("misc.msg.monthsback"));

        btnFallsIndicator.addActionListener(e -> {
            OPDE.getMainframe().setBlocked(true);
            SwingWorker worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    SYSPropsTools.storeProp("opde.controlling::fallsIndicatorMonthsBack", txtFallsIndicatorsMonthsBack.getText(), OPDE.getLogin().getUser());
                    return ResInfoTools.getFallsIndicatorsByMonth(Integer.parseInt(txtFallsIndicatorsMonthsBack.getText()), progressClosure);
                }

                @Override
                protected void done() {
                    try {
                        SYSFilesTools.print(get().toString(), true);
                    } catch (ExecutionException ee) {
                        OPDE.fatal(ee);
                    } catch (InterruptedException ie) {
                        // nop
                    }


                    OPDE.getDisplayManager().setProgressBarMessage(null);
                    OPDE.getMainframe().setBlocked(false);
                }
            };

            worker.execute();

        });
        pnlFallsIndicator.add(btnFallsIndicator, BorderLayout.WEST);
        pnlFallsIndicator.add(txtFallsIndicatorsMonthsBack, BorderLayout.EAST);
        pnlContent.add(pnlFallsIndicator);


        return pnlContent;
    }


    private JPanel createContentPanel4Orga() {
        JPanel pnlContent = new JPanel(new VerticalLayout());

        /***
         *      ______     ___        _   _       _ _   _
         *     | __ ) \   / / \   ___| |_(_)_   _(_) |_(_) ___  ___
         *     |  _ \\ \ / / _ \ / __| __| \ \ / / | __| |/ _ \/ __|
         *     | |_) |\ V / ___ \ (__| |_| |\ V /| | |_| |  __/\__ \
         *     |____/  \_/_/   \_\___|\__|_| \_/ |_|\__|_|\___||___/
         *
         */
        JPanel pnlBV = new JPanel(new BorderLayout());
        final JButton btnBVActivities = GUITools.createHyperlinkButton("opde.controlling.orga.bvactivities", null, null);
        int bvWeeksBack;
        try {
            bvWeeksBack = Integer.parseInt(OPDE.getProps().getProperty("opde.controlling::bvactivitiesWeeksBack"));
        } catch (NumberFormatException nfe) {
            bvWeeksBack = 7;
        }
        final JTextField txtBVWeeksBack = GUITools.createIntegerTextField(1, 52, bvWeeksBack);
        txtBVWeeksBack.setToolTipText(SYSTools.xx("misc.msg.weeksback"));
        btnBVActivities.addActionListener(e -> {

            OPDE.getMainframe().setBlocked(true);
            SwingWorker worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    SYSPropsTools.storeProp("opde.controlling::bvactivitiesWeeksBack", txtBVWeeksBack.getText(), OPDE.getLogin().getUser());
                    SYSFilesTools.print(NReportTools.getBVActivites(new LocalDate().minusWeeks(Integer.parseInt(txtBVWeeksBack.getText())), progressClosure), false);
                    return null;
                }

                @Override
                protected void done() {
                    OPDE.getDisplayManager().setProgressBarMessage(null);
                    OPDE.getMainframe().setBlocked(false);
                }
            };
            worker.execute();
        });
        pnlBV.add(btnBVActivities, BorderLayout.WEST);
        pnlBV.add(txtBVWeeksBack, BorderLayout.EAST);
        pnlContent.add(pnlBV);


        /***
         *                                _       _       _
         *       ___ ___  _ __ ___  _ __ | | __ _(_)_ __ | |_ ___
         *      / __/ _ \| '_ ` _ \| '_ \| |/ _` | | '_ \| __/ __|
         *     | (_| (_) | | | | | | |_) | | (_| | | | | | |_\__ \
         *      \___\___/|_| |_| |_| .__/|_|\__,_|_|_| |_|\__|___/
         *                         |_|
         */
        JPanel pnlComplaints = new JPanel(new BorderLayout());
        final JButton btnComplaints = GUITools.createHyperlinkButton("opde.controlling.orga.complaints", null, null);
        int complaintsMonthBack;
        try {
            complaintsMonthBack = Integer.parseInt(OPDE.getProps().getProperty("opde.controlling::complaintsMonthBack"));
        } catch (NumberFormatException nfe) {
            complaintsMonthBack = 7;
        }
        final JTextField txtComplaintsMonthsBack = GUITools.createIntegerTextField(1, 12, complaintsMonthBack);
        txtComplaintsMonthsBack.setToolTipText(SYSTools.xx("misc.msg.monthsback"));

        btnComplaints.addActionListener(e -> {
            OPDE.getMainframe().setBlocked(true);
            SwingWorker worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    SYSPropsTools.storeProp("opde.controlling::complaintsMonthBack", txtComplaintsMonthsBack.getText(), OPDE.getLogin().getUser());

                    int monthsback = Integer.parseInt(txtComplaintsMonthsBack.getText());

                    String content = QProcessTools.getComplaintsAnalysis(monthsback, progressClosure);
                    content += NReportTools.getComplaints(new LocalDate().minusMonths(monthsback).dayOfMonth().withMinimumValue(), progressClosure);

                    SYSFilesTools.print(content, false);
                    return null;
                }

                @Override
                protected void done() {
                    OPDE.getDisplayManager().setProgressBarMessage(null);
                    OPDE.getMainframe().setBlocked(false);
                }
            };
            worker.execute();
        });
        pnlComplaints.add(btnComplaints, BorderLayout.WEST);
        pnlComplaints.add(txtComplaintsMonthsBack, BorderLayout.EAST);
        pnlContent.add(pnlComplaints);


        // ResInfoTypeTools.TYPE_NURSING_INSURANCE
        // https://github.com/tloehr/Offene-Pflege.de/issues/71
        JPanel pnlInsuranceGrade = new JPanel(new BorderLayout());
        final JButton btnIG = GUITools.createHyperlinkButton("misc.msg.InsuranceGrades", null, null);


        btnIG.addActionListener(e -> {
            OPDE.getMainframe().setBlocked(true);
            SwingWorker worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    String content = SYSConst.html_h1("misc.msg.InsuranceGrades");


                    String ul = "";
                    for (Resident resident : ResidentTools.getAllActive()) {
                        if (resident.getAdminonly() == ResidentTools.NORMAL) {

                            ul += SYSConst.html_li(SYSConst.html_bold(ResidentTools.getLabelText(resident)));

                            ResInfo insurance = ResInfoTools.getLastResinfo(resident, ResInfoTypeTools.TYPE_NURSING_INSURANCE);

                            String ins = "";
                            if (insurance == null) {
                                ins = SYSTools.xx("misc.msg.noentryyet");
                            } else {
                                Properties props = load(insurance.getProperties());
                                String grade = SYSTools.xx("ninsurance.grade") + props.getProperty("grade");
                                ins = grade + (props.getProperty("grade").equals("assigned") ? ": " + props.getProperty("result") : "");
                            }


                            ul += SYSConst.html_ul(SYSConst.html_li(ins));
                        }
                    }

                    content += SYSConst.html_ul(ul);

                    SYSFilesTools.print(content, false);
                    return null;
                }

                @Override
                protected void done() {
                    OPDE.getDisplayManager().setProgressBarMessage(null);
                    OPDE.getMainframe().setBlocked(false);
                }
            };
            worker.execute();
        });
        pnlInsuranceGrade.add(btnIG, BorderLayout.WEST);
        pnlContent.add(pnlInsuranceGrade);


        return pnlContent;
    }


    private Properties load(String text) {
        Properties props = new Properties();
        try {
            StringReader reader = new StringReader(text);
            props.load(reader);
            reader.close();
        } catch (IOException ex) {
            OPDE.fatal(ex);
        }
        return props;
    }

//

    private JPanel createContentPanel4Nursing() {
        JPanel pnlContent = new JPanel(new VerticalLayout());
        /***
         *       ___  ______     ______    ____  __ ____  _  __
         *      / _ \|  _ \ \   / / ___|  / /  \/  |  _ \| |/ /
         *     | | | | | | \ \ / /\___ \ / /| |\/| | | | | ' /
         *     | |_| | |_| |\ V /  ___) / / | |  | | |_| | . \
         *      \__\_\____/  \_/  |____/_/  |_|  |_|____/|_|\_\
         *
         */
        JPanel pnlQDVS = new JPanel(new BorderLayout());
        final JButton btnQDVS = GUITools.createHyperlinkButton("opde.controlling.qdvs", null, null);
        LocalDateTime lastQDVSDate = null;
        try {
            lastQDVSDate = Instant.ofEpochMilli(Long.parseLong(OPDE.getProps().getProperty("opde.controlling::qdvslastdate"))).atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (NumberFormatException nfe) {
            lastQDVSDate = LocalDateTime.of(2019, Month.JULY, 01, 0, 0);
        }
        final JTextField txtLastDate = new JTextField(lastQDVSDate.format(DateTimeFormatter.ISO_DATE_TIME));
        txtLastDate.setToolTipText(SYSTools.xx("letzte Kontrolle"));


        final LocalDateTime tmp = lastQDVSDate;

        btnQDVS.addActionListener(e -> {
            OPDE.getMainframe().setBlocked(true);
            SwingWorker worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
//                    SYSPropsTools.storeProp("opde.controlling::qdvslastdate", txtWoundsMonthsBack.getText(), OPDE.getLogin().getUser());
//                    SYSFilesTools.print("", false);
                     getLogger().debug(getQDVS(tmp, null));
                    return null;
                }

                @Override
                protected void done() {
                    OPDE.getDisplayManager().setProgressBarMessage(null);
                    OPDE.getMainframe().setBlocked(false);
                }
            };
            worker.execute();
        });
        pnlQDVS.add(btnQDVS, BorderLayout.WEST);
        pnlQDVS.add(txtLastDate, BorderLayout.EAST);
        pnlContent.add(pnlQDVS);


        /***
         *     __        __                    _
         *     \ \      / /__  _   _ _ __   __| |___
         *      \ \ /\ / / _ \| | | | '_ \ / _` / __|
         *       \ V  V / (_) | |_| | | | | (_| \__ \
         *        \_/\_/ \___/ \__,_|_| |_|\__,_|___/
         *
         */
        JPanel pnlWounds = new JPanel(new BorderLayout());
        final JButton btnWounds = GUITools.createHyperlinkButton("opde.controlling.nursing.wounds", null, null);
        int woundsMonthsBack;
        try {
            woundsMonthsBack = Integer.parseInt(OPDE.getProps().getProperty("opde.controlling::woundsMonthsBack"));
        } catch (NumberFormatException nfe) {
            woundsMonthsBack = 7;
        }
        final JTextField txtWoundsMonthsBack = GUITools.createIntegerTextField(1, 12, woundsMonthsBack);
        txtWoundsMonthsBack.setToolTipText(SYSTools.xx("misc.msg.monthsback"));

        btnWounds.addActionListener(e -> {
            OPDE.getMainframe().setBlocked(true);
            SwingWorker worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    SYSPropsTools.storeProp("opde.controlling::woundsMonthsBack", txtWoundsMonthsBack.getText(), OPDE.getLogin().getUser());
                    SYSFilesTools.print(getWounds(Integer.parseInt(txtWoundsMonthsBack.getText()), progressClosure), false);
                    return null;
                }

                @Override
                protected void done() {
                    OPDE.getDisplayManager().setProgressBarMessage(null);
                    OPDE.getMainframe().setBlocked(false);
                }
            };
            worker.execute();
        });
        pnlWounds.add(btnWounds, BorderLayout.WEST);
        pnlWounds.add(txtWoundsMonthsBack, BorderLayout.EAST);
        pnlContent.add(pnlWounds);

        return pnlContent;
    }

    private JPanel createContentPanel4Drugs() {
        JPanel pnlContent = new JPanel(new VerticalLayout());


        /***
         *      ____                      ____            _             _   _     _     _
         *     |  _ \ _ __ _   _  __ _   / ___|___  _ __ | |_ _ __ ___ | | | |   (_)___| |_
         *     | | | | '__| | | |/ _` | | |   / _ \| '_ \| __| '__/ _ \| | | |   | / __| __|
         *     | |_| | |  | |_| | (_| | | |__| (_) | | | | |_| | | (_) | | | |___| \__ \ |_
         *     |____/|_|   \__,_|\__, |  \____\___/|_| |_|\__|_|  \___/|_| |_____|_|___/\__|
         *                       |___/
         */
        JPanel pnlDrugControl = new JPanel(new BorderLayout());
        final JButton btnDrugControl = GUITools.createHyperlinkButton("opde.controlling.drugs.controllist", null, null);
        final JComboBox cmbStation = new JComboBox(StationTools.getAll4Combobox(false));
        btnDrugControl.addActionListener(e -> {
            OPDE.getMainframe().setBlocked(true);
            SwingWorker worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    return MedStockTools.getListForMedControl((Station) cmbStation.getSelectedItem(), progressClosure);
                }

                @Override
                protected void done() {
                    try {
                        SYSFilesTools.print(get().toString(), true);
                    } catch (ExecutionException ee) {
                        OPDE.fatal(ee);
                    } catch (InterruptedException ie) {
                        // nop
                    }

                    OPDE.getDisplayManager().setProgressBarMessage(null);
                    OPDE.getMainframe().setBlocked(false);
                }
            };
            worker.execute();
        });
        pnlDrugControl.add(btnDrugControl, BorderLayout.WEST);
        pnlDrugControl.add(cmbStation, BorderLayout.EAST);
        pnlContent.add(pnlDrugControl);

        /***
         *     __        __   _       _     _    ____            _             _   _   _                     _   _
         *     \ \      / /__(_) __ _| |__ | |_ / ___|___  _ __ | |_ _ __ ___ | | | \ | | __ _ _ __ ___ ___ | |_(_) ___ ___
         *      \ \ /\ / / _ \ |/ _` | '_ \| __| |   / _ \| '_ \| __| '__/ _ \| | |  \| |/ _` | '__/ __/ _ \| __| |/ __/ __|
         *       \ V  V /  __/ | (_| | | | | |_| |__| (_) | | | | |_| | | (_) | | | |\  | (_| | | | (_| (_) | |_| | (__\__ \
         *        \_/\_/ \___|_|\__, |_| |_|\__|\____\___/|_| |_|\__|_|  \___/|_| |_| \_|\__,_|_|  \___\___/ \__|_|\___|___/
         *                      |___/
         */
        JPanel pnlWeightControllNarcotics = new JPanel(new BorderLayout());
        final JButton btnWeightControl = GUITools.createHyperlinkButton("opde.controlling.prescription.narcotics.weightcontrol", null, null);

//               final JComboBox cmbStation = new JComboBox(StationTools.getAll4Combobox(false));
        btnWeightControl.addActionListener(e -> {
            OPDE.getMainframe().setBlocked(true);
            SwingWorker worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {

                    return MedStockTools.getNarcoticsWeightList(new LocalDate().minusMonths(1), new LocalDate());
                }

                @Override
                protected void done() {

                    try {
                        SYSFilesTools.print(get().toString(), true);
                    } catch (ExecutionException ee) {
                        OPDE.fatal(ee);
                    } catch (InterruptedException ie) {
                        // nop
                    }

                    OPDE.getDisplayManager().setProgressBarMessage(null);
                    OPDE.getMainframe().setBlocked(false);
                }
            };
            worker.execute();
        });

//        final JToggleButton btnNotify = new JToggleButton(SYSConst.icon22mailOFF);
//        btnNotify.setSelectedIcon(SYSConst.icon22mailON);
//        btnNotify.setSelected(NotificationTools.find(OPDE.getLogin().getUser(), NotificationTools.NKEY_DRUG_WEIGHT_CONTROL) != null);
//        btnNotify.setToolTipText(SYSTools.xx("opde.notification.enable.for.this.topic"));
//
//        btnNotify.addItemListener(new ItemListener() {
//            @Override
//            public void itemStateChanged(ItemEvent e) {
//
//                EntityManager em = OPDE.createEM();
//                try {
//                    em.getTransaction().begin();
//                    Users user = em.merge(OPDE.getLogin().getUser());
//                    em.lock(user, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
//
//                    if (e.getStateChange() == ItemEvent.SELECTED) {
//                        Notification myNotification = em.merge(new Notification(NotificationTools.NKEY_DRUG_WEIGHT_CONTROL, user));
//                        user.getNotifications().add(myNotification);
//                    } else {
//                        Notification myNotification = em.merge(NotificationTools.find(OPDE.getLogin().getUser(), NotificationTools.NKEY_DRUG_WEIGHT_CONTROL));
//                        user.getNotifications().remove(myNotification);
//                        em.remove(myNotification);
//                    }
//
//                    em.getTransaction().commit();
//                    OPDE.getLogin().setUser(user);
//                } catch (OptimisticLockException ole) {
//                    OPDE.warn(ole);
//                    if (em.getTransaction().isActive()) {
//                        em.getTransaction().rollback();
//                    }
//                    if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
//                        OPDE.getMainframe().emptyFrame();
//                        OPDE.getMainframe().afterLogin();
//                    }
//                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                } catch (Exception ex) {
//                    if (em.getTransaction().isActive()) {
//                        em.getTransaction().rollback();
//                    }
//                    OPDE.fatal(ex);
//                } finally {
//                    em.close();
//                }
//
//            }
//        });

        pnlWeightControllNarcotics.add(btnWeightControl, BorderLayout.WEST);
//        pnlWeightControllNarcotics.add(btnNotify, BorderLayout.EAST);
        pnlContent.add(pnlWeightControllNarcotics);


        return pnlContent;
    }

    private JPanel createContentPanel4Nutrition() {
        JPanel pnlContent = new JPanel(new VerticalLayout());


        /***
         *      _____ _               _              __       _               ____  _ _
         *     | ____(_)_ __         / \  _   _ ___ / _|_   _| |__  _ __     | __ )(_) | __ _ _ __  ____
         *     |  _| | | '_ \ _____ / _ \| | | / __| |_| | | | '_ \| '__|____|  _ \| | |/ _` | '_ \|_  /
         *     | |___| | | | |_____/ ___ \ |_| \__ \  _| |_| | | | | | |_____| |_) | | | (_| | | | |/ /
         *     |_____|_|_| |_|    /_/   \_\__,_|___/_|  \__,_|_| |_|_|       |____/|_|_|\__,_|_| |_/___|
         *
         */
        JPanel pnlLiquidBalance = new JPanel(new BorderLayout());
        final JButton btnLiquidBalance = GUITools.createHyperlinkButton("opde.controlling.nutrition.liquidbalance", null, null);
        final JComboBox cmbLiquidBalanceMonth = new JComboBox(SYSCalendar.createMonthList(new LocalDate().minusYears(1), new LocalDate()));
        btnLiquidBalance.addActionListener(e -> {
            OPDE.getMainframe().setBlocked(true);
            SwingWorker worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    LocalDate month = (LocalDate) cmbLiquidBalanceMonth.getSelectedItem();
                    SYSFilesTools.print(ResValueTools.getLiquidBalance(month, progressClosure), false);
                    return null;
                }

                @Override
                protected void done() {
                    OPDE.getDisplayManager().setProgressBarMessage(null);
                    OPDE.getMainframe().setBlocked(false);
                }
            };
            worker.execute();
        });
        cmbLiquidBalanceMonth.setRenderer((list, value, index, isSelected, cellHasFocus) -> new DefaultListCellRenderer().getListCellRendererComponent(list, monthFormatter.format(((LocalDate) value).toDate()), index, isSelected, cellHasFocus));
        cmbLiquidBalanceMonth.setSelectedIndex(cmbLiquidBalanceMonth.getItemCount() - 2);
        pnlLiquidBalance.add(btnLiquidBalance, BorderLayout.WEST);
        pnlLiquidBalance.add(cmbLiquidBalanceMonth, BorderLayout.EAST);
        pnlContent.add(pnlLiquidBalance);

        /***
         *       ____               _      _     _      ______  __  __ ___    ______  _        _   _     _   _ _
         *      / ___| _____      _(_) ___| |__ | |_   / / __ )|  \/  |_ _|  / / ___|| |_ __ _| |_(_)___| |_(_) | __
         *     | |  _ / _ \ \ /\ / / |/ __| '_ \| __| / /|  _ \| |\/| || |  / /\___ \| __/ _` | __| / __| __| | |/ /
         *     | |_| |  __/\ V  V /| | (__| | | | |_ / / | |_) | |  | || | / /  ___) | || (_| | |_| \__ \ |_| |   <
         *      \____|\___| \_/\_/ |_|\___|_| |_|\__/_/  |____/|_|  |_|___/_/  |____/ \__\__,_|\__|_|___/\__|_|_|\_\
         *
         */
        JPanel pnlWeight = new JPanel(new BorderLayout());


        final JButton btnWeightStats = GUITools.createHyperlinkButton("opde.controlling.nutrition.weightstats", null, null);
        int wsMonthsBack;
        try {
            wsMonthsBack = Integer.parseInt(OPDE.getProps().getProperty("opde.controlling::wsMonthsBack"));
        } catch (NumberFormatException nfe) {
            wsMonthsBack = 7;
        }
        final JTextField txtWSMonthsBack = GUITools.createIntegerTextField(1, 24, wsMonthsBack);
        txtWSMonthsBack.setToolTipText(SYSTools.xx("misc.msg.monthsback"));

        boolean wsRetiredToo;
        wsRetiredToo = Boolean.parseBoolean(OPDE.getProps().getProperty("opde.controlling::wsRetiredToo"));
        final JCheckBox cbWSRetiredToo = new JCheckBox(SYSTools.xx("opde.controlling.exResidents"), wsRetiredToo);

        btnWeightStats.addActionListener(e -> {
            OPDE.getMainframe().setBlocked(true);
            SwingWorker worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    SYSPropsTools.storeProp("opde.controlling::wsMonthsBack", txtWSMonthsBack.getText(), OPDE.getLogin().getUser());
                    SYSPropsTools.storeProp("opde.controlling::wsRetiredToo", Boolean.toString(cbWSRetiredToo.isSelected()), OPDE.getLogin().getUser());
                    SYSFilesTools.print(ResValueTools.getWeightStats(Integer.parseInt(txtWSMonthsBack.getText()), cbWSRetiredToo.isSelected(), progressClosure), false);
                    return null;
                }

                @Override
                protected void done() {
                    OPDE.getDisplayManager().setProgressBarMessage(null);
                    OPDE.getMainframe().setBlocked(false);
                }
            };
            worker.execute();
        });
        // https://github.com/tloehr/Offene-Pflege.de/issues/79


        JPanel pnlParams = new JPanel();
        BoxLayout layout = new BoxLayout(pnlParams, BoxLayout.LINE_AXIS);
        pnlParams.setLayout(layout);
        pnlParams.add(cbWSRetiredToo);
        pnlParams.add(txtWSMonthsBack);

        pnlWeight.add(btnWeightStats, BorderLayout.CENTER);
        pnlWeight.add(pnlParams, BorderLayout.EAST);

        pnlContent.add(pnlWeight);


        return pnlContent;
    }


    @Override
    public void cleanup() {
        super.cleanup();
        cpsControlling.removeAll();
//        if (pnlQMSPlan != null) {
//            pnlQMSPlan.cleanup();
//        }
    }

    private void tabMainStateChanged(ChangeEvent e) {
        reload();
    }

    @Override
    public void reload() {

        // defers from my usual method, because every tab can have its own searcharea.
        prepareSearchArea();

        helpkey = OPDE.getAppInfo().getInternalClasses().containsKey(internalClassID) ? OPDE.getAppInfo().getInternalClasses().get(internalClassID).getHelpurl() : null;
        OPDE.getDisplayManager().setMainMessage(internalClassID);

        cpsControlling.removeAll();
        cpsControlling.setLayout(new JideBoxLayout(cpsControlling, JideBoxLayout.Y_AXIS));

        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.USER1, internalClassID)) {
            cpsControlling.add(createCP4Orga());
        }


        cpsControlling.add(createCP4Nursing());
        cpsControlling.add(createCP4Nutrition());
        cpsControlling.add(createCP4Pain());
        cpsControlling.add(createCP4Fall());

        cpsControlling.add(createCP4Hygiene());

        if (OPDE.isCalcMediUPR1()) {
            cpsControlling.add(createCP4Drugs());
        }
        cpsControlling.addExpansion();

    }


    //    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        tabMain = new JideTabbedPane();
        scrollPane1 = new JScrollPane();
        cpsControlling = new CollapsiblePanes();
        panel2 = new JPanel();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== tabMain ========
        {
            tabMain.setBoldActiveTab(true);
            tabMain.setFont(new Font("Arial", Font.PLAIN, 16));
            tabMain.setShowTabButtons(true);
            tabMain.setTabPlacement(SwingConstants.LEFT);
            tabMain.setHideOneTab(true);
            tabMain.addChangeListener(e -> tabMainStateChanged(e));

            //======== scrollPane1 ========
            {

                //======== cpsControlling ========
                {
                    cpsControlling.setLayout(new BoxLayout(cpsControlling, BoxLayout.X_AXIS));
                }
                scrollPane1.setViewportView(cpsControlling);
            }
            tabMain.addTab("controlling", scrollPane1);

            //======== panel2 ========
            {
                panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
            }
            tabMain.addTab("text", panel2);
        }
        add(tabMain);
    }// </editor-fold>//GEN-END:initComponents


    public static String getQDVS(LocalDateTime lastTime, Resident resident) {
        return "";
    }


    public static String getWounds(int monthsback, Closure progress) {
        StringBuilder html = new StringBuilder(1000);

        int p = -1;
        progress.execute(new Pair<Integer, Integer>(p, 100));

        LocalDate from = new LocalDate().minusMonths(monthsback).dayOfMonth().withMinimumValue();
        EntityManager em = OPDE.createEM();
        DateFormat df = DateFormat.getDateInstance();

        String jpql1 = " SELECT b FROM ResInfo b WHERE b.from > :from AND b.resident.adminonly <> 2 AND b.bwinfotyp.type = :type ORDER BY b.resident.id, b.from DESC ";
        Query query1 = em.createQuery(jpql1);
        query1.setParameter("type", ResInfoTypeTools.TYPE_WOUNDS);
        query1.setParameter("from", from.toDate());
        ArrayList<QProcessElement> listVal = new ArrayList<QProcessElement>(query1.getResultList());

        String jpql2 = " " +
                " SELECT n FROM NReport n " +
                " JOIN n.commontags ct " +
                " WHERE n.pit > :from " +
                " AND n.resident.adminonly <> 2 " +
                " AND n.replacedBy IS NULL " +
                " AND ct.type = :type " +
                " ORDER BY n.resident.id, n.pit DESC ";
        Query query2 = em.createQuery(jpql2);
        query2.setParameter("type", CommontagsTools.TYPE_SYS_WOUNDS);
        query2.setParameter("from", from.toDate());
        listVal.addAll(new ArrayList<QProcessElement>(query2.getResultList()));

        em.close();

        HashMap<Resident, ArrayList<QProcessElement>> listData = new HashMap<Resident, ArrayList<QProcessElement>>();
        for (QProcessElement element : listVal) {
            if (!listData.containsKey(element.getResident())) {
                listData.put(element.getResident(), new ArrayList<QProcessElement>());
            }
            listData.get(element.getResident()).add(element);
        }

        ArrayList<Resident> listResident = new ArrayList<Resident>(listData.keySet());
        Collections.sort(listResident);

        html.append(SYSConst.html_h1(SYSTools.xx("opde.controlling.nursing.wounds") + ": " + df.format(from.toDate()) + " &raquo;&raquo; " + df.format(new Date())));
//        html.append(SYSConst.html_h2(SYSTools.xx("misc.msg.analysis") + ": " + );

        p = 0;

        for (Resident resident : listResident) {
            progress.execute(new Pair<Integer, Integer>(p, listResident.size()));
            p++;

            html.append(SYSConst.html_h2(ResidentTools.getTextCompact(resident)));

            StringBuffer table = new StringBuffer(1000);

            table.append(SYSConst.html_table_tr(
                    SYSConst.html_table_th("misc.msg.Date") +
                            SYSConst.html_table_th("misc.msg.details")
            ));

            Collections.sort(listData.get(resident), (o1, o2) -> new Long(o1.getPITInMillis()).compareTo(new Long(o2.getPITInMillis())) * -1);

            for (QProcessElement element : listData.get(resident)) {
                table.append(SYSConst.html_table_tr(
                        SYSConst.html_table_td(element.getPITAsHTML(), "left", "top") +
                                SYSConst.html_table_td(element.getContentAsHTML())
                ));
            }

            html.append(SYSConst.html_table(table.toString(), "1"));
        }
        return html.toString();
    }

    private void prepareSearchArea() {

        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);

        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout(5));
        mypanel.setBackground(Color.WHITE);

        CollapsiblePane searchPane = new CollapsiblePane(SYSTools.xx(internalClassID));
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);

        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }

//
//        GUITools.addAllComponents(mypanel, addCommands());
//        GUITools.addAllComponents(mypanel, addFilters());

        searchPane.setContentPane(mypanel);

        searchPanes.add(searchPane);
        searchPanes.addExpansion();


    }


}
