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
package op.controlling;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.swing.JideBoxLayout;
import entity.Station;
import entity.StationTools;
import entity.files.SYSFilesTools;
import entity.info.ResInfoTools;
import entity.info.ResInfoTypeTools;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.prescription.MedStockTools;
import entity.process.QProcessElement;
import entity.process.QProcessTools;
import entity.reports.NReportTAGSTools;
import entity.reports.NReportTools;
import entity.system.SYSPropsTools;
import entity.values.ResValueTools;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.DateMidnight;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author tloehr
 */
public class PnlControlling extends CleanablePanel {
    public static final String internalClassID = "opde.controlling";
    private JScrollPane jspSearch;
    Format monthFormatter = new SimpleDateFormat("MMMM yyyy");
    private Closure progressClosure;

    // Variables declaration - do not modify
    //GEN-BEGIN:variables
    private JScrollPane scrollPane1;
    private CollapsiblePanes cpsControlling;
    // End of variables declaration//GEN-END:variables


//    JCheckBox[] jbs = null;
//    JFrame parent;
//    boolean isCancelled;
//    Object[] o = null;
//


    @Override
    public String getInternalClassID() {
        return internalClassID;
    }

    /**
     * Creates new form PnlControlling
     */
    public PnlControlling(JScrollPane jspSearch) {
        this.jspSearch = jspSearch;
        jspSearch.setViewportView(new JPanel());
        progressClosure = new Closure() {
            @Override
            public void execute(Object o) {
                Pair<Integer, Integer> progress = (Pair<Integer, Integer>) o;
                OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), progress.getFirst(), progress.getSecond()));
            }
        };
        initComponents();
        initPanel();
        reloadDisplay();
    }

    private void initPanel() {

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
        cpsControlling.removeAll();
        cpsControlling.setLayout(new JideBoxLayout(cpsControlling, JideBoxLayout.Y_AXIS));
        cpsControlling.add(createCP4Orga());
        cpsControlling.add(createCP4Nursing());
        cpsControlling.add(createCP4Nutrition());
        if (OPDE.isCalcMediUPR1()) {
            cpsControlling.add(createCP4Drugs());
        }
        cpsControlling.addExpansion();
    }


    private CollapsiblePane createCP4Orga() {
        final CollapsiblePane cpOrga = new CollapsiblePane();

        String title = "<html><font size=+1>" +
                OPDE.lang.getString("opde.controlling.orga") +
                "</font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cpOrga.setCollapsed(!cpOrga.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
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

    private CollapsiblePane createCP4Nutrition() {
        final CollapsiblePane cpOrga = new CollapsiblePane();

        String title = "<html><font size=+1>" +
                OPDE.lang.getString("opde.controlling.nutrition") +
                "</font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cpOrga.setCollapsed(!cpOrga.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
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
        final CollapsiblePane cpOrga = new CollapsiblePane();

        String title = "<html><font size=+1>" +
                OPDE.lang.getString("opde.controlling.drugs") +
                "</font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cpOrga.setCollapsed(!cpOrga.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            }
        });

        cpOrga.setTitleLabelComponent(cptitle.getMain());
        cpOrga.setSlidingDirection(SwingConstants.SOUTH);
        cpOrga.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                cpOrga.setContentPane(createContentPanel4Drugs());
            }
        });

        if (!cpOrga.isCollapsed()) {
            cpOrga.setContentPane(createContentPanel4Drugs());
        }

        cpOrga.setHorizontalAlignment(SwingConstants.LEADING);

        return cpOrga;
    }

    private CollapsiblePane createCP4Nursing() {
        final CollapsiblePane cpOrga = new CollapsiblePane();

        String title = "<html><font size=+1>" +
                OPDE.lang.getString("opde.controlling.nursing") +
                "</font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cpOrga.setCollapsed(!cpOrga.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            }
        });
        cpOrga.setTitleLabelComponent(cptitle.getMain());
        cpOrga.setSlidingDirection(SwingConstants.SOUTH);
        cpOrga.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                cpOrga.setContentPane(createContentPanel4Nursing());
            }
        });

        if (!cpOrga.isCollapsed()) {
            cpOrga.setContentPane(createContentPanel4Nursing());
        }

        cpOrga.setHorizontalAlignment(SwingConstants.LEADING);
//        cpOrga.setOpaque(false);
//        cpOrga.setBackground(getColor(vtype, SYSConst.medium1));

        return cpOrga;
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
        txtBVWeeksBack.setToolTipText(OPDE.lang.getString("misc.msg.weeksback"));
        btnBVActivities.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                OPDE.getMainframe().setBlocked(true);
                SwingWorker worker = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        SYSPropsTools.storeProp("opde.controlling::bvactivitiesWeeksBack", txtBVWeeksBack.getText(), OPDE.getLogin().getUser());
                        SYSFilesTools.print(NReportTools.getBVActivites(new DateMidnight().minusWeeks(Integer.parseInt(txtBVWeeksBack.getText())), progressClosure), false);
                        return null;
                    }

                    @Override
                    protected void done() {
                        OPDE.getDisplayManager().setProgressBarMessage(null);
                        OPDE.getMainframe().setBlocked(false);
                    }
                };
                worker.execute();
            }
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
        txtComplaintsMonthsBack.setToolTipText(OPDE.lang.getString("misc.msg.monthsback"));

        btnComplaints.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OPDE.getMainframe().setBlocked(true);
                SwingWorker worker = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        SYSPropsTools.storeProp("opde.controlling::complaintsMonthBack", txtComplaintsMonthsBack.getText(), OPDE.getLogin().getUser());

                        int monthsback = Integer.parseInt(txtComplaintsMonthsBack.getText());

                        String content = QProcessTools.getComplaintsAnalysis(monthsback, progressClosure);
                        content += NReportTools.getComplaints(new DateMidnight().minusMonths(monthsback).dayOfMonth().withMinimumValue(), progressClosure);

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
            }
        });
        pnlComplaints.add(btnComplaints, BorderLayout.WEST);
        pnlComplaints.add(txtComplaintsMonthsBack, BorderLayout.EAST);
        pnlContent.add(pnlComplaints);

        return pnlContent;
    }

    private JPanel createContentPanel4Nursing() {
        JPanel pnlContent = new JPanel(new VerticalLayout());


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
        txtWoundsMonthsBack.setToolTipText(OPDE.lang.getString("misc.msg.monthsback"));

        btnWounds.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
            }
        });
        pnlWounds.add(btnWounds, BorderLayout.WEST);
        pnlWounds.add(txtWoundsMonthsBack, BorderLayout.EAST);
        pnlContent.add(pnlWounds);


        /***
         *      ____             _       _   _____ _
         *     / ___|  ___   ___(_) __ _| | |_   _(_)_ __ ___   ___  ___
         *     \___ \ / _ \ / __| |/ _` | |   | | | | '_ ` _ \ / _ \/ __|
         *      ___) | (_) | (__| | (_| | |   | | | | | | | | |  __/\__ \
         *     |____/ \___/ \___|_|\__,_|_|   |_| |_|_| |_| |_|\___||___/
         *
         */
        JPanel pblSocialTimes = new JPanel(new BorderLayout());
        final JButton btnSocialTimes = GUITools.createHyperlinkButton("opde.controlling.nursing.social", null, null);
        final JComboBox cmbSocialTimes = new JComboBox(SYSCalendar.createMonthList(new LocalDate().minusYears(1), new LocalDate()));
        btnSocialTimes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OPDE.getMainframe().setBlocked(true);
                SwingWorker worker = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        DateMidnight month = (DateMidnight) cmbSocialTimes.getSelectedItem();
                        SYSFilesTools.print(NReportTools.getTimes4SocialReports(month, progressClosure), false);
                        return null;
                    }

                    @Override
                    protected void done() {
                        OPDE.getDisplayManager().setProgressBarMessage(null);
                        OPDE.getMainframe().setBlocked(false);
                    }
                };
                worker.execute();
            }
        });
        cmbSocialTimes.setRenderer(new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                return new DefaultListCellRenderer().getListCellRendererComponent(list, monthFormatter.format(((LocalDate) value).toDate()), index, isSelected, cellHasFocus);
            }
        });
        cmbSocialTimes.setSelectedIndex(cmbSocialTimes.getItemCount() - 2);
        pblSocialTimes.add(btnSocialTimes, BorderLayout.WEST);
        pblSocialTimes.add(cmbSocialTimes, BorderLayout.EAST);
        pnlContent.add(pblSocialTimes);

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
        txtFallsMonthsBack.setToolTipText(OPDE.lang.getString("misc.msg.monthsback"));

        btnFallsAnon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
            }
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
        txtResFallsMonthsBack.setToolTipText(OPDE.lang.getString("misc.msg.monthsback"));

        btnFallsRes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
            }
        });
        pnlFallsRes.add(btnFallsRes, BorderLayout.WEST);
        pnlFallsRes.add(txtResFallsMonthsBack, BorderLayout.EAST);
        pnlContent.add(pnlFallsRes);


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
        btnDrugControl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OPDE.getMainframe().setBlocked(true);
                SwingWorker worker = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        SYSFilesTools.print(MedStockTools.getListForMedControl((Station) cmbStation.getSelectedItem(), progressClosure), false);
                        return null;
                    }

                    @Override
                    protected void done() {
                        OPDE.getDisplayManager().setProgressBarMessage(null);
                        OPDE.getMainframe().setBlocked(false);
                    }
                };
                worker.execute();
            }
        });
        pnlDrugControl.add(btnDrugControl, BorderLayout.WEST);
        pnlDrugControl.add(cmbStation, BorderLayout.EAST);
        pnlContent.add(pnlDrugControl);


        return pnlContent;
    }

    private JPanel createContentPanel4Nutrition() {
        JPanel pnlContent = new JPanel(new VerticalLayout());


        /***
         *      _ _             _     _   _           _
         *     | (_) __ _ _   _(_) __| | | |__   __ _| | __ _ _ __   ___ ___
         *     | | |/ _` | | | | |/ _` | | '_ \ / _` | |/ _` | '_ \ / __/ _ \
         *     | | | (_| | |_| | | (_| | | |_) | (_| | | (_| | | | | (_|  __/
         *     |_|_|\__, |\__,_|_|\__,_| |_.__/ \__,_|_|\__,_|_| |_|\___\___|
         *             |_|
         */
        JPanel pnlLiquidBalance = new JPanel(new BorderLayout());
        final JButton btnLiquidBalance = GUITools.createHyperlinkButton("opde.controlling.nutrition.liquidbalance", null, null);
        final JComboBox cmbLiquidBalanceMonth = new JComboBox(SYSCalendar.createMonthList(new LocalDate().minusYears(1), new LocalDate()));
        btnLiquidBalance.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OPDE.getMainframe().setBlocked(true);
                SwingWorker worker = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        DateMidnight month = (DateMidnight) cmbLiquidBalanceMonth.getSelectedItem();
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
            }
        });
        cmbLiquidBalanceMonth.setRenderer(new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                return new DefaultListCellRenderer().getListCellRendererComponent(list, monthFormatter.format(((DateMidnight) value).toDate()), index, isSelected, cellHasFocus);
            }
        });
        cmbLiquidBalanceMonth.setSelectedIndex(cmbLiquidBalanceMonth.getItemCount() - 2);
        pnlLiquidBalance.add(btnLiquidBalance, BorderLayout.WEST);
        pnlLiquidBalance.add(cmbLiquidBalanceMonth, BorderLayout.EAST);
        pnlContent.add(pnlLiquidBalance);

        /***
         *                   _       _     _         _        _   _     _   _
         *     __      _____(_) __ _| |__ | |_   ___| |_ __ _| |_(_)___| |_(_) ___ ___
         *     \ \ /\ / / _ \ |/ _` | '_ \| __| / __| __/ _` | __| / __| __| |/ __/ __|
         *      \ V  V /  __/ | (_| | | | | |_  \__ \ || (_| | |_| \__ \ |_| | (__\__ \
         *       \_/\_/ \___|_|\__, |_| |_|\__| |___/\__\__,_|\__|_|___/\__|_|\___|___/
         *                     |___/
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
        txtWSMonthsBack.setToolTipText(OPDE.lang.getString("misc.msg.monthsback"));
        btnWeightStats.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OPDE.getMainframe().setBlocked(true);
                SwingWorker worker = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        SYSPropsTools.storeProp("opde.controlling::wsMonthsBack", txtWSMonthsBack.getText(), OPDE.getLogin().getUser());
                        SYSFilesTools.print(ResValueTools.getWeightStats(Integer.parseInt(txtWSMonthsBack.getText()), progressClosure), false);
                        return null;
                    }

                    @Override
                    protected void done() {
                        OPDE.getDisplayManager().setProgressBarMessage(null);
                        OPDE.getMainframe().setBlocked(false);
                    }
                };
                worker.execute();
            }
        });
        pnlWeight.add(btnWeightStats, BorderLayout.WEST);
        pnlWeight.add(txtWSMonthsBack, BorderLayout.EAST);
        pnlContent.add(pnlWeight);


        return pnlContent;
    }

    @Override
    public void cleanup() {
        cpsControlling.removeAll();
    }

    @Override
    public void reload() {
        reloadDisplay();
    }


    //    /**
//     * This method is called from within the constructor to
//     * initialize the form.
//     * WARNING: Do NOT modify this code. The content of this method is
//     * always regenerated by the PrinterForm Editor.
//     */
//    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        scrollPane1 = new JScrollPane();
        cpsControlling = new CollapsiblePanes();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== scrollPane1 ========
        {

            //======== cpsControlling ========
            {
                cpsControlling.setLayout(new BoxLayout(cpsControlling, BoxLayout.X_AXIS));
            }
            scrollPane1.setViewportView(cpsControlling);
        }
        add(scrollPane1);
    }// </editor-fold>//GEN-END:initComponents


    public static String getWounds(int monthsback, Closure progress) {
        StringBuilder html = new StringBuilder(1000);

        int p = -1;
        progress.execute(new Pair<Integer, Integer>(p, 100));

        DateMidnight from = new DateMidnight().minusMonths(monthsback).dayOfMonth().withMinimumValue();
        EntityManager em = OPDE.createEM();
        DateFormat df = DateFormat.getDateInstance();

        String jpql1 = " SELECT b FROM ResInfo b WHERE b.from > :from AND b.resident.adminonly <> 2 AND b.bwinfotyp.type = :type ORDER BY b.resident.rid, b.from DESC ";
        Query query1 = em.createQuery(jpql1);
        query1.setParameter("type", ResInfoTypeTools.TYPE_WOUNDS);
        query1.setParameter("from", from.toDate());
        ArrayList<QProcessElement> listVal = new ArrayList<QProcessElement>(query1.getResultList());

        String jpql2 = " " +
                " SELECT n FROM NReport n " +
                " JOIN n.tags t " +
                " WHERE n.pit > :from " +
                " AND n.resident.adminonly <> 2 " +
                " AND n.replacedBy IS NULL " +
                " AND t.system = :tagsystem " +
                " ORDER BY n.resident.rid, n.pit DESC ";
        Query query2 = em.createQuery(jpql2);
        query2.setParameter("tagsystem", NReportTAGSTools.TYPE_SYS_WOUNDS);
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

        html.append(SYSConst.html_h1(OPDE.lang.getString("opde.controlling.nursing.wounds") + ": " + df.format(from.toDate()) + " &raquo;&raquo; " + df.format(new Date())));
//        html.append(SYSConst.html_h2(OPDE.lang.getString("misc.msg.analysis") + ": " + );

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

            Collections.sort(listData.get(resident), new Comparator<QProcessElement>() {
                @Override
                public int compare(QProcessElement o1, QProcessElement o2) {
                    return new Long(o1.getPITInMillis()).compareTo(new Long(o2.getPITInMillis())) * -1;
                }
            });

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


}
