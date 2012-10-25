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
import entity.files.SYSFilesTools;
import entity.reports.NReportTools;
import entity.system.SYSPropsTools;
import op.OPDE;
import op.tools.CleanablePanel;
import op.tools.DefaultCPTitle;
import op.tools.GUITools;
import op.tools.SYSCalendar;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.DateMidnight;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyVetoException;
import java.text.Format;
import java.text.SimpleDateFormat;

/**
 * @author tloehr
 */
public class PnlControlling extends CleanablePanel {
    public static final String internalClassID = "opde.controlling";
    private JScrollPane jspSearch;
    Format monthFormatter = new SimpleDateFormat("MMMM yyyy");

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

    /**
     * Creates new form PnlControlling
     */
    public PnlControlling(JScrollPane jspSearch) {
        this.jspSearch = jspSearch;
//        parent = this;
//        isCancelled = false;
//        initComponents();
//        initForm();
//        o = new Object[]{pbPart, lblProgress, isCancelled};
//        setTitle(SYSTools.getWindowTitle("Controlling Cockpit"));
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
        cpsControlling.addExpansion();
    }


    private CollapsiblePane createCP4Orga() {
        final CollapsiblePane cpOrga = new CollapsiblePane();

        String title = "<html><font size=+1>" +
                OPDE.lang.getString(internalClassID + ".orga") +
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
        final JButton btnBVActivities = GUITools.createHyperlinkButton(internalClassID + ".orga.bvactivities", null, null);
        int bvWeeksBack;
        try {
            bvWeeksBack = Integer.parseInt(OPDE.getProps().getProperty(internalClassID + "::bvactivitiesWeeksBack"));
        } catch (NumberFormatException nfe) {
            bvWeeksBack = 7;
        }
        final JTextField txtBVWeeksBack = GUITools.createIntegerTextField(1, 52, bvWeeksBack);
        txtBVWeeksBack.setToolTipText(OPDE.lang.getString("misc.msg.weeksback"));
        btnBVActivities.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SYSFilesTools.print(NReportTools.getBVActivites(new DateMidnight().minusWeeks(Integer.parseInt(txtBVWeeksBack.getText()))), false);
                SYSPropsTools.storeProp(internalClassID + "::bvactivitiesWeeksBack", txtBVWeeksBack.getText(), OPDE.getLogin().getUser());
            }
        });
        pnlBV.add(btnBVActivities, BorderLayout.WEST);
        pnlBV.add(txtBVWeeksBack, BorderLayout.EAST);
        pnlContent.add(pnlBV);

        JPanel pnlLiquidBalance = new JPanel(new BorderLayout());
        final JButton btnLiquidBalance = GUITools.createHyperlinkButton(internalClassID + ".orga.bvactivities", null, null);
        JComboBox cmbLiquidBalanceMonth = new JComboBox(SYSCalendar.createMonthList(new DateMidnight().minusYears(1), new DateMidnight()));
        btnLiquidBalance.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SYSFilesTools.print(NReportTools.getBVActivites(new DateMidnight().minusWeeks(Integer.parseInt(txtBVWeeksBack.getText()))), false);
            }
        });
        cmbLiquidBalanceMonth.setRenderer(new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                return new DefaultListCellRenderer().getListCellRendererComponent(list, monthFormatter.format(((DateMidnight) value).toDate()), index, isSelected, cellHasFocus);
            }
        });
        cmbLiquidBalanceMonth.setSelectedIndex(cmbLiquidBalanceMonth.getItemCount()-2);

        pnlLiquidBalance.add(btnLiquidBalance, BorderLayout.WEST);
        pnlLiquidBalance.add(cmbLiquidBalanceMonth, BorderLayout.EAST);
        pnlContent.add(pnlLiquidBalance);


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

    //
//    private void initForm() {
//        SYSPropsTools.restoreState(this.getClass().getName() + "::cbBVAktivitaet", cbBVAktivitaet);
//        SYSPropsTools.restoreState(this.getClass().getName() + "::cbSozialBerichte", cbSozialBerichte);
//        SYSPropsTools.restoreState(this.getClass().getName() + "::cbGewicht", cbGewicht);
//        SYSPropsTools.restoreState(this.getClass().getName() + "::cbBilanz", cbBilanz);
//        SYSPropsTools.restoreState(this.getClass().getName() + "::cbNichtAbgehakteBHPs", cbNichtAbgehakteBHPs);
//        SYSPropsTools.restoreState(this.getClass().getName() + "::cbPlanung", cbPlanung);
//        SYSPropsTools.restoreState(this.getClass().getName() + "::cbVerordnungenOhneAnbruch", cbVerordnungenOhneAnbruch);
//        SYSPropsTools.restoreState(this.getClass().getName() + "::cbSozialZeiten", cbSozialZeiten);
//        SYSPropsTools.restoreState(this.getClass().getName() + "::cbGeringeVorraete", cbGeringeVorraete);
//        SYSPropsTools.restoreState(this.getClass().getName() + "::cbMediControl", cbMediControl);
//        SYSPropsTools.restoreState(this.getClass().getName() + "::cbSturzAnonym", cbSturzAnonym);
//        SYSPropsTools.restoreState(this.getClass().getName() + "::cbSturz", cbSturz);
//        SYSPropsTools.restoreState(this.getClass().getName() + "::cbBerichte", cbBerichte);
//        SYSPropsTools.restoreState(this.getClass().getName() + "::cbWunden", cbWunden);
//        SYSPropsTools.restoreState(this.getClass().getName() + "::cbInko", cbInko);
//        SYSPropsTools.restoreState(this.getClass().getName() + "::cbBeschwerden", cbBeschwerden);
//        jbs = new JCheckBox[]{cbBVAktivitaet, cbBilanz, cbGewicht, cbGeringeVorraete, cbNichtAbgehakteBHPs, cbPlanung,
//                cbSozialBerichte, cbSozialZeiten, cbVerordnungenOhneAnbruch, cbMediControl, cbSturzAnonym,
//                cbSturz, cbBerichte, cbWunden, cbInko, cbBeschwerden
//        };
//        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
//
//        // Standardeinstellung für die Spinners wieder herstellen.
//        int bhptage;
//        int planungentage;
//        int bvwochen;
//        int gewichtmonate;
//        int sozialmonate;
//        int sozialzeitenwochen;
//        int vorratprozent;
//        int sturzamonate;
//        int sturzmonate;
//        int berichtemonate;
//        int wundenmonate;
//        int beschwerdenmonate;
//        try {
//            planungentage = Integer.parseInt(OPDE.getProps().getProperty(this.getClass().getName() + "::spinPlanungenTage"));
//        } catch (NumberFormatException nfe) {
//            planungentage = 7;
//        }
//        try {
//            bhptage = Integer.parseInt(OPDE.getProps().getProperty(this.getClass().getName() + "::spinBHPTage"));
//        } catch (NumberFormatException nfe) {
//            bhptage = 7;
//        }
//        try {
//            bvwochen = Integer.parseInt(OPDE.getProps().getProperty(this.getClass().getName() + "::spinBVWochen"));
//        } catch (NumberFormatException nfe) {
//            bvwochen = 1;
//        }
//        try {
//            gewichtmonate = Integer.parseInt(OPDE.getProps().getProperty(this.getClass().getName() + "::spinGewichtMonate"));
//        } catch (NumberFormatException nfe) {
//            gewichtmonate = 6;
//        }
//        try {
//            sozialmonate = Integer.parseInt(OPDE.getProps().getProperty(this.getClass().getName() + "::spinSozialMonate"));
//        } catch (NumberFormatException nfe) {
//            sozialmonate = 1;
//        }
//        try {
//            sozialzeitenwochen = Integer.parseInt(OPDE.getProps().getProperty(this.getClass().getName() + "::spinSozialZeitenWochen"));
//        } catch (NumberFormatException nfe) {
//            sozialzeitenwochen = 1;
//        }
//        try {
//            sturzamonate = Integer.parseInt(OPDE.getProps().getProperty(this.getClass().getName() + "::spinSturzAMonate"));
//        } catch (NumberFormatException nfe) {
//            sturzamonate = 6;
//        }
//        try {
//            sturzmonate = Integer.parseInt(OPDE.getProps().getProperty(this.getClass().getName() + "::spinSturzMonate"));
//        } catch (NumberFormatException nfe) {
//            sturzmonate = 6;
//        }
//        try {
//            vorratprozent = Integer.parseInt(OPDE.getProps().getProperty(this.getClass().getName() + "::spinVorratProzent"));
//        } catch (NumberFormatException nfe) {
//            vorratprozent = 20;
//        }
//        try {
//            berichtemonate = Integer.parseInt(OPDE.getProps().getProperty(this.getClass().getName() + "::spinBerichteMonate"));
//        } catch (NumberFormatException nfe) {
//            berichtemonate = 1;
//        }
//        try {
//            wundenmonate = Integer.parseInt(OPDE.getProps().getProperty(this.getClass().getName() + "::spinWundenMonate"));
//        } catch (NumberFormatException nfe) {
//            wundenmonate = 1;
//        }
//        try {
//            beschwerdenmonate = Integer.parseInt(OPDE.getProps().getProperty(this.getClass().getName() + "::spinBeschwerdenMonate"));
//        } catch (NumberFormatException nfe) {
//            beschwerdenmonate = 5;
//        }
//
//        spinBHPTage.setModel(new SpinnerNumberModel(bhptage, 1, 500, 1));
//        spinPlanungenTage.setModel(new SpinnerNumberModel(planungentage, 1, 500, 1));
//        spinBVWochen.setModel(new SpinnerNumberModel(bvwochen, 1, 52, 1));
//        spinBerichteMonate.setModel(new SpinnerNumberModel(berichtemonate, 1, 12, 1));
//        spinWundenMonate.setModel(new SpinnerNumberModel(wundenmonate, 1, 12, 1));
//        spinGewichtMonate.setModel(new SpinnerNumberModel(gewichtmonate, 1, 12, 1));
//        spinSozialMonate.setModel(new SpinnerNumberModel(sozialmonate, 1, 12, 1));
//        //spinSozialZeitenWochen.setModel(new SpinnerNumberModel(sozialzeitenwochen, 1, 52, 1));
//        spinVorratProzent.setModel(new SpinnerNumberModel(vorratprozent, 0, 90, 5));
//        spinSturzAMonate.setModel(new SpinnerNumberModel(sturzamonate, 1, 12, 1));
//        spinSturzMonate.setModel(new SpinnerNumberModel(sturzmonate, 1, 12, 1));
//        spinBeschwerdenMonate.setModel(new SpinnerNumberModel(beschwerdenmonate, 1, 12, 1));
//
//        Date von = SYSCalendar.bom(SYSCalendar.addField(new Date(), -3, GregorianCalendar.YEAR));
//        Date bis = SYSCalendar.eom(new Date());
//
////        DefaultComboBoxModel monthmodel1 = SYSCalendar.createMonthList(von, bis);
////        DefaultComboBoxModel monthmodel2 = SYSCalendar.createMonthList(von, bis);
//
//        cmbBilanzMonat.setModel(SYSCalendar.createMonthList(von, bis));
//        cmbBilanzMonat.setRenderer(new ListCellRenderer() {
//            Format formatter = new SimpleDateFormat("MMMM yyyy");
//
//            @Override
//            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
//                String text = formatter.format(o);
//                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
//            }
//        });
//
//        cmbBilanzMonat.setSelectedIndex(cmbBilanzMonat.getModel().getSize() - 2); // Auf den letzten Eintrag setzen.
//
//        cmbPEAMonat.setModel(SYSCalendar.createMonthList(von, bis));
//        cmbPEAMonat.setRenderer(new ListCellRenderer() {
//            Format formatter = new SimpleDateFormat("MMMM yyyy");
//
//            @Override
//            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
//                String text = formatter.format(o);
//                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
//            }
//        });
//        cmbPEAMonat.setSelectedIndex(cmbPEAMonat.getModel().getSize() - 2); // Auf den letzten Eintrag setzen.
//
//        EntityManager em = OPDE.createEM();
//        Query query1 = em.createNamedQuery("Station.findAllSorted");
//        cmbStation.setModel(new DefaultComboBoxModel(new Vector<Station>(query1.getResultList())));
//        cmbStation.setSelectedItem(StationTools.getSpecialStation());
//
//        // Leeren Kopf vor die Liste setzen.
////        ListElement[] headtag = new ListElement[]{new ListElement("Keine Auswahl", "")};
//
//
//        Query query = em.createNamedQuery("PBerichtTAGS.findAllActive");
//        cmbTags.setModel(SYSTools.lst2cmb(SYSTools.list2dlm(query.getResultList())));
//        cmbTags.setRenderer(new ListCellRenderer() {
//            @Override
//            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
//
//                return new DefaultListCellRenderer().getListCellRendererComponent(jList, ((NReportTAGS) o).getText(), i, isSelected, cellHasFocus);
//            }
//        });
//        SYSPropsTools.restoreState(this.getClass().getName() + ":cmbTags", cmbTags);
//        em.close();
//
//        txtBerichte.setText(SYSTools.catchNull(OPDE.getProps().getProperty(this.getClass().getName() + "::txtBerichte")));
//
//    }
//
//    private void cleanup() {
//        SYSTools.unregisterListeners(this);
//    }
//
//    private int anzahlGewuenschterAuswertungen() {
//        int num = 0;
//        for (int i = 0; i < jbs.length; i++) {
//            if (jbs[i].isSelected()) {
//                num++;
//            }
//        }
//        return num;
//    }
//
//    private String createHTML() {
//
//        StringBuilder html = new StringBuilder(1000);
//        html.append("<div id=\"fonttext\">");
//
//        int progress = 0;
//        EntityManager em = OPDE.createEM();
//        pbMain.setMaximum(anzahlGewuenschterAuswertungen());
//        pbMain.setMinimum(0);
//        pbPart.setMinimum(0);
////
////        if (html.length() > 0) {
////            html.append("<p style=\"page-break-before: always\"/>");
////        }
//
//        // ======================== MEDIZINISCH ========================
//        if (!isCancelled && cbBilanz.isSelected()) {
//            if (html.length() > 0) {
//                html.append("<p style=\"page-break-before: always\"/>");
//            }
//            lblProgress.setText("Ein-/Ausfuhr/Bilanz");
//            pbPart.setValue(0);
//            Date monat = (Date) cmbBilanzMonat.getSelectedItem();
//            html.append(DBHandling.getBilanzen(1, monat, o));
//            progress++;
//            pbMain.setValue(progress);
//        }
//
//        if (!isCancelled && cbGewicht.isSelected()) {
//            if (html.length() > 0) {
//                html.append("<p style=\"page-break-before: always\"/>");
//            }
//            lblProgress.setText("Gewichtstatistik");
//            pbPart.setValue(0);
//            int gewichtmonate = Integer.parseInt(spinGewichtMonate.getValue().toString());
//            html.append(DBHandling.getGewichtsverlauf(gewichtmonate, 1, o));
//            progress++;
//            pbMain.setValue(progress);
//        }
//
//        // ORGANISATORISCH
//        if (!isCancelled && cbBVAktivitaet.isSelected()) {
//            if (html.length() > 0) {
//                html.append("<p style=\"page-break-before: always\"/>");
//            }
//            lblProgress.setText("BV Aktivitäten");
//            pbPart.setValue(0);
//            int bvwochen = Integer.parseInt(spinBVWochen.getValue().toString());
//            html.append(NReportTools.getBVBerichte(em, 1, bvwochen));
//            progress++;
//            pbMain.setValue(progress);
//        }
//
//        if (!isCancelled && cbNichtAbgehakteBHPs.isSelected()) {
//            if (html.length() > 0) {
//                html.append("<p style=\"page-break-before: always\"/>");
//            }
//            lblProgress.setText("Nicht abgehakte BHPs");
//            pbPart.setValue(0);
//            int bhptage = Integer.parseInt(spinBHPTage.getValue().toString());
//            html.append(DBHandling.getNichtAbgehakteBHPs(1, bhptage));
//            progress++;
//            pbMain.setValue(progress);
//        }
//
//        if (!isCancelled && cbPlanung.isSelected()) {
//            if (html.length() > 0) {
//                html.append("<p style=\"page-break-before: always\"/>");
//            }
//            lblProgress.setText("Planungen, die bald enden / abgelaufen sind");
//            pbPart.setValue(0);
//            int tage = Integer.parseInt(spinPlanungenTage.getValue().toString());
//            html.append(DBHandling.getAblaufendePlanungen(1, tage));
//            progress++;
//            pbMain.setValue(progress);
//        }
//
//        if (!isCancelled && cbVerordnungenOhneAnbruch.isSelected()) {
////            if (html.length() > 0) {
////                html.append("<p style=\"page-break-before: always\"/>");
////            }
//            lblProgress.setText("Verordnungen ohne Medikamente im Anbruch");
//            pbPart.setValue(0);
//            html.append(DBHandling.getAktiveVorraeteOhneBestandImAnbruch(1));
//            progress++;
//            pbMain.setValue(progress);
//        }
//
//        if (!isCancelled && cbGeringeVorraete.isSelected()) {
//            if (html.length() > 0) {
//                html.append("<p style=\"page-break-before: always\"/>");
//            }
//            lblProgress.setText("Medikamenten Bestandsermittlung");
//            pbPart.setValue(0);
//            double prozent = Double.parseDouble(spinVorratProzent.getValue().toString());
//            html.append(DBHandling.getGeringeVorraete(1, prozent, o));
//            progress++;
//        }
//        pbMain.setValue(progress);
//
//        if (!isCancelled && cbMediControl.isSelected()) {
////            if (html.length() > 0) {
////                html.append("<p style=\"page-break-before: always\"/>");
////            }
//            lblProgress.setText("Medikamenten Kontroll Liste");
//            pbPart.setValue(0);
////            String station = cmbStation.getSelectedItem().toString();
////            html.append(DBHandling.getMediKontrolle(station, 1));
//            html.append(MedStockTools.getMediKontrolle(em, (Station) cmbStation.getSelectedItem(), 1));
//            progress++;
//            pbMain.setValue(progress);
//        }
//
//        if (!isCancelled && cbBerichte.isSelected()) {
//
//            if (html.length() > 0) {
//                html.append("<p style=\"page-break-before: always\"/>");
//            }
//            lblProgress.setText("NReport durchsuchen");
//            pbPart.setValue(0);
//            int berichtemonate = Integer.parseInt(spinBerichteMonate.getValue().toString());
////            html.append(DBHandling.getBerichte(txtBerichte.getText(), (ListElement) cmbTags.getSelectedItem(), 1, berichtemonate, o));
//            html.append(NReportTools.getBerichteASHTML(em, txtBerichte.getText(), (NReportTAGS) cmbTags.getSelectedItem(), 1, berichtemonate));
//            progress++;
//            pbMain.setValue(progress);
//        }
//
//        if (!isCancelled && cbInko.isSelected()) {
//            if (html.length() > 0) {
//                html.append("<p style=\"page-break-before: always\"/>");
//            }
//            lblProgress.setText("Inkontinenz-Liste");
//            pbPart.setValue(0);
//            html.append(DBHandling.getInkontinenz(1));
//            progress++;
//            pbMain.setValue(progress);
//        }
//
//        if (!isCancelled && cbWunden.isSelected()) {
//            if (html.length() > 0) {
//                html.append("<p style=\"page-break-before: always\"/>");
//            }
//            lblProgress.setText("Wund-Liste");
//            pbPart.setValue(0);
//            int wundenmonat = Integer.parseInt(spinWundenMonate.getValue().toString());
//            html.append(DBHandling.getWunden(1, wundenmonat, o));
//            progress++;
//            pbMain.setValue(progress);
//        }
//
//        if (!isCancelled && cbBeschwerden.isSelected()) {
//            if (html.length() > 0) {
//                html.append("<p style=\"page-break-before: always\"/>");
//            }
//            lblProgress.setText("Auswertung Beschwerden");
//            pbPart.setValue(0);
//            int beschwerdenmonat = Integer.parseInt(spinBeschwerdenMonate.getValue().toString());
//            html.append(DBHandling.getBeschwerdeAuswertung(1, beschwerdenmonat));
//            progress++;
//            pbMain.setValue(progress);
//        }
//
//        // SOZIALES
//        if (!isCancelled && cbSozialBerichte.isSelected()) {
//            if (html.length() > 0) {
//                html.append("<p style=\"page-break-before: always\"/>");
//            }
//            lblProgress.setText("Berichte Sozialer Dienst");
//            pbPart.setValue(0);
//            int sozialmonate = Integer.parseInt(spinSozialMonate.getValue().toString());
//            Query query = em.createNamedQuery("PBerichtTAGS.findByKurzbezeichnung");
//            query.setParameter("kurzbezeichnung", "soz");
//            NReportTAGS sozTag = (NReportTAGS) query.getSingleResult();
//
//            html.append(NReportTools.getBerichteASHTML(em, "", sozTag, 1, sozialmonate));
////            html.append(DBHandling.getSozialBerichte("", "", 1, sozialmonate));
//            progress++;
//            pbMain.setValue(progress);
//        }
//
//        if (!isCancelled && cbSozialZeiten.isSelected()) {
//            if (html.length() > 0) {
//                html.append("<p style=\"page-break-before: always\"/>");
//            }
//            lblProgress.setText("Zeiten Sozialer Dienst");
//            pbPart.setValue(0);
//            Date monat = (Date) cmbPEAMonat.getSelectedItem();
////            html.append(DBHandling.getSozialZeiten(1, monat));
//            html.append(NReportTools.getSozialZeiten(em, 1, monat));
//            progress++;
//            pbMain.setValue(progress);
//        }
//
//        // Statistik
//        if (!isCancelled && cbSturzAnonym.isSelected()) {
//            if (html.length() > 0) {
//                html.append("<p style=\"page-break-before: always\"/>");
//            }
//            lblProgress.setText("Sturzstatistik anonym");
//            pbPart.setValue(0);
//            int sturzmonate = Integer.parseInt(spinSturzAMonate.getValue().toString());
//            html.append(DBHandling.getAnonymSturz(1, sturzmonate));
//            progress++;
//            pbMain.setValue(progress);
//        }
//        if (!isCancelled && cbSturz.isSelected()) {
//            if (html.length() > 0) {
//                html.append("<p style=\"page-break-before: always\"/>");
//            }
//            lblProgress.setText("Sturzstatistik Bewohnerbezogen");
//            pbPart.setValue(0);
//            int sturzmonate = Integer.parseInt(spinSturzMonate.getValue().toString());
//            html.append(DBHandling.getBWSturz(1, sturzmonate, o));
//            progress++;
//            pbMain.setValue(progress);
//        }
//
//        em.close();
//
//        pbMain.setValue(0);
//        pbPart.setValue(0);
//        lblProgress.setText(" ");
//
//        String result = "";
//        if (!isCancelled) {
//            html.append("</div>");
//            result = html.toString();
//        }
//        return result;
//    }
//
//    public void dispose() {
//        cleanup();
//        super.dispose();
//    }
//
//    /**
//     * This method is called from within the constructor to
//     * initialize the form.
//     * WARNING: Do NOT modify this code. The content of this method is
//     * always regenerated by the Form Editor.
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
//
//    private void cbBVAktivitaetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbBVAktivitaetActionPerformed
//        SYSPropsTools.storeState(this.getClass().getName() + "::cbBVAktivitaet", cbBVAktivitaet);
//        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
//    }//GEN-LAST:event_cbBVAktivitaetActionPerformed
//
//    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
//
//        //String result = "";
//
//        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
//
//            protected String doInBackground() throws Exception {
//                String s = createHTML();
//                return s;
//            }
//
//            @Override
//            protected void done() {
//                try {
//                    String get = SYSTools.catchNull(get());
////                    String result = SYSTools.htmlUmlautConversion(SYSTools.catchNull(get()));
//                    if (!SYSTools.catchNull(get).isEmpty()) {
//                        SYSFilesTools.print(get, false);
//                    }
//                    btnPrint.setEnabled(true);
//                    btnStop.setEnabled(false);
//                    isCancelled = false;
//                    o[2] = isCancelled;
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(PnlControlling.class.getName()).log(Level.SEVERE, null, ex);
//                } catch (ExecutionException ex) {
//                    Logger.getLogger(PnlControlling.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        };
//        btnPrint.setEnabled(false);
//        btnStop.setEnabled(true);
//        worker.execute();
//
//    }//GEN-LAST:event_btnPrintActionPerformed
//
//    private void cbSozialBerichteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSozialBerichteActionPerformed
//        SYSPropsTools.storeState(this.getClass().getName() + "::cbSozialBerichte", cbSozialBerichte);
//        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
//    }//GEN-LAST:event_cbSozialBerichteActionPerformed
//
//    private void spinBVWochenStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinBVWochenStateChanged
//        spinBVWochen.setToolTipText("In den letzten " + spinBVWochen.getValue().toString() + " Wochen");
//        SYSPropsTools.storeProp(this.getClass().getName() + "::spinBVWochen", spinBVWochen.getValue().toString(), OPDE.getLogin().getUser());
//        //SYSTools.putProps(this.getClass().getName() + "::spinBVWochen", spinBVWochen.getValue().toString(), false, "");
//    }//GEN-LAST:event_spinBVWochenStateChanged
//
//    private void spinBHPTageStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinBHPTageStateChanged
//        spinBHPTage.setToolTipText("In den letzten " + spinBHPTage.getValue().toString() + " Tagen");
//        SYSPropsTools.storeProp(this.getClass().getName() + "::spinBHPTage", spinBHPTage.getValue().toString(), OPDE.getLogin().getUser());
//        //SYSTools.putProps(this.getClass().getName() + "::spinBHPTage", spinBHPTage.getValue().toString(), false, "");
//    }//GEN-LAST:event_spinBHPTageStateChanged
//
//    private void spinSozialWochenStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinSozialWochenStateChanged
//        spinSozialMonate.setToolTipText("In den letzten " + spinSozialMonate.getValue().toString() + " Monaten");
//        SYSPropsTools.storeProp(this.getClass().getName() + "::spinSozialMonate", spinSozialMonate.getValue().toString(), OPDE.getLogin().getUser());
//        //SYSTools.putProps(this.getClass().getName() + "::spinSozialWochen", spinBHPTage.getValue().toString(), false, "");
//    }//GEN-LAST:event_spinSozialWochenStateChanged
//
//    private void spinGewichtMonateStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinGewichtMonateStateChanged
//        spinGewichtMonate.setToolTipText("In den letzten " + spinGewichtMonate.getValue().toString() + " Monate");
//        SYSPropsTools.storeProp(this.getClass().getName() + "::spinGewichtMonate", spinGewichtMonate.getValue().toString(), OPDE.getLogin().getUser());
//        //SYSTools.putProps(this.getClass().getName() + "::spinGewichtMonate", spinGewichtMonate.getValue().toString(), false, "");
//    }//GEN-LAST:event_spinGewichtMonateStateChanged
//
//    private void cbNichtAbgehakteBHPsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbNichtAbgehakteBHPsActionPerformed
//        SYSPropsTools.storeState(this.getClass().getName() + "::cbNichtAbgehakteBHPs", cbNichtAbgehakteBHPs);
//        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
//    }//GEN-LAST:event_cbNichtAbgehakteBHPsActionPerformed
//
//    private void cbPlanungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbPlanungActionPerformed
//        SYSPropsTools.storeState(this.getClass().getName() + "::cbPlanung", cbPlanung);
//        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
//    }//GEN-LAST:event_cbPlanungActionPerformed
//
//    private void cbVerordnungenOhneAnbruchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbVerordnungenOhneAnbruchActionPerformed
//        SYSPropsTools.storeState(this.getClass().getName() + "::cbVerordnungenOhneAnbruch", cbVerordnungenOhneAnbruch);
//        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
//    }//GEN-LAST:event_cbVerordnungenOhneAnbruchActionPerformed
//
//    private void cbBilanzActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbBilanzActionPerformed
//        SYSPropsTools.storeState(this.getClass().getName() + "::cbBilanz", cbBilanz);
//        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
//    }//GEN-LAST:event_cbBilanzActionPerformed
//
//    private void cbGewichtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbGewichtActionPerformed
//        SYSPropsTools.storeState(this.getClass().getName() + "::cbGewicht", cbGewicht);
//        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
//    }//GEN-LAST:event_cbGewichtActionPerformed
//
//    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed
//        isCancelled = true;
//        o[2] = isCancelled;
//    }//GEN-LAST:event_btnStopActionPerformed
//
//    private void cbSozialZeitenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSozialZeitenActionPerformed
//        SYSPropsTools.storeState(this.getClass().getName() + "::cbSozialZeiten", cbSozialZeiten);
//        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
//    }//GEN-LAST:event_cbSozialZeitenActionPerformed
//
//    private void spinPlanungenTageStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinPlanungenTageStateChanged
//        spinPlanungenTage.setToolTipText("In den letzten " + spinPlanungenTage.getValue().toString() + " Tagen");
//        SYSPropsTools.storeProp(this.getClass().getName() + "::spinPlanungenTage", spinPlanungenTage.getValue().toString(), OPDE.getLogin().getUser());
//        //SYSTools.putProps(this.getClass().getName() + "::spinPlanungenTage", spinPlanungenTage.getValue().toString(), false, "");
//    }//GEN-LAST:event_spinPlanungenTageStateChanged
//
//    private void spinVorratProzentStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinVorratProzentStateChanged
//        spinVorratProzent.setToolTipText("In den letzten " + spinVorratProzent.getValue().toString() + " Tagen");
//        SYSPropsTools.storeProp(this.getClass().getName() + "::spinVorratProzent", spinVorratProzent.getValue().toString(), OPDE.getLogin().getUser());
//        //SYSTools.putProps(this.getClass().getName() + "::spinVorratProzent", spinVorratProzent.getValue().toString(), false, "");
//    }//GEN-LAST:event_spinVorratProzentStateChanged
//
//    private void cbGeringeVorraeteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbGeringeVorraeteActionPerformed
//        SYSPropsTools.storeState(this.getClass().getName() + "::cbGeringeVorraete", cbGeringeVorraete);
//        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
//    }//GEN-LAST:event_cbGeringeVorraeteActionPerformed
//
//    private void cbMediControlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbMediControlActionPerformed
//        SYSPropsTools.storeState(this.getClass().getName() + "::cbMediControl", cbMediControl);
//        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
//    }//GEN-LAST:event_cbMediControlActionPerformed
//
//    private void cmbStationItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbStationItemStateChanged
//        SYSPropsTools.storeState(this.getClass().getName() + "::cmbStation", cmbStation);
//    }//GEN-LAST:event_cmbStationItemStateChanged
//
//    private void spinSturzAMonateStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinSturzAMonateStateChanged
//        spinSturzAMonate.setToolTipText("In den letzten " + spinSturzAMonate.getValue().toString() + " Monaten");
//        SYSPropsTools.storeProp(this.getClass().getName() + "::spinSturzAMonate", spinSturzAMonate.getValue().toString(), OPDE.getLogin().getUser());
//        //SYSTools.putProps(this.getClass().getName() + "::spinSturzAMonate", spinSturzAMonate.getValue().toString(), false, "");
//    }//GEN-LAST:event_spinSturzAMonateStateChanged
//
//    private void cbSturzAnonymActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSturzAnonymActionPerformed
//        SYSPropsTools.storeState(this.getClass().getName() + "::cbSturzAnonym", cbSturzAnonym);
//        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
//    }//GEN-LAST:event_cbSturzAnonymActionPerformed
//
//    private void spinSturzMonateStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinSturzMonateStateChanged
//        spinSturzMonate.setToolTipText("In den letzten " + spinSturzMonate.getValue().toString() + " Monaten");
//        SYSPropsTools.storeProp(this.getClass().getName() + "::spinSturzMonate", spinSturzMonate.getValue().toString(), OPDE.getLogin().getUser());
//        //SYSTools.putProps(this.getClass().getName() + "::spinSturzMonate", spinSturzMonate.getValue().toString(), false, "");
//    }//GEN-LAST:event_spinSturzMonateStateChanged
//
//    private void cbSturzActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSturzActionPerformed
//        SYSPropsTools.storeState(this.getClass().getName() + "::cbSturz", cbSturz);
//        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
//    }//GEN-LAST:event_cbSturzActionPerformed
//
//    private void cbBerichteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbBerichteActionPerformed
//        SYSPropsTools.storeState(this.getClass().getName() + "::cbBerichte", cbBerichte);
//        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
//    }//GEN-LAST:event_cbBerichteActionPerformed
//
//    private void txtBerichteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBerichteFocusLost
//        SYSPropsTools.storeProp(this.getClass().getName() + "::txtBerichte", txtBerichte.getText(), OPDE.getLogin().getUser());
//        //SYSTools.putProps(this.getClass().getName() + "::txtBerichte", txtBerichte.getText(), false, "");
//    }//GEN-LAST:event_txtBerichteFocusLost
//
//    private void spinBerichteMonateStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinBerichteMonateStateChanged
//        SYSPropsTools.storeProp(this.getClass().getName() + "::spinBerichteMonate", spinBerichteMonate.getValue().toString(), OPDE.getLogin().getUser());
//        //SYSTools.putProps(this.getClass().getName() + "::spinBerichteMonate", spinBerichteMonate.getValue().toString(), false, "");
//    }//GEN-LAST:event_spinBerichteMonateStateChanged
//
//    private void cbWundenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbWundenActionPerformed
//        SYSPropsTools.storeState(this.getClass().getName() + "::cbWunden", cbWunden);
//        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
//    }//GEN-LAST:event_cbWundenActionPerformed
//
//    private void spinWundenMonateStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinWundenMonateStateChanged
//        SYSPropsTools.storeProp(this.getClass().getName() + "::spinWundenMonate", spinWundenMonate.getValue().toString(), OPDE.getLogin().getUser());
//        //SYSTools.putProps(this.getClass().getName() + "::spinWundenMonate", spinWundenMonate.getValue().toString(), false, "");
//    }//GEN-LAST:event_spinWundenMonateStateChanged
//
//    private void cbInkoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbInkoActionPerformed
//        SYSPropsTools.storeState(this.getClass().getName() + "::cbInko", cbInko);
//        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
//    }//GEN-LAST:event_cbInkoActionPerformed
//
//    private void cbBeschwerdenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbBeschwerdenActionPerformed
//        SYSPropsTools.storeState(this.getClass().getName() + "::cbBeschwerden", cbBeschwerden);
//        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
//    }//GEN-LAST:event_cbBeschwerdenActionPerformed
//
//    private void spinBeschwerdenMonateStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinBeschwerdenMonateStateChanged
//        SYSPropsTools.storeProp(this.getClass().getName() + "::spinBeschwerdenMonate", spinBeschwerdenMonate.getValue().toString(), OPDE.getLogin().getUser());
//        //SYSTools.putProps(this.getClass().getName() + "::spinBeschwerdenMonate", spinBeschwerdenMonate.getValue().toString(), false, "");
//    }//GEN-LAST:event_spinBeschwerdenMonateStateChanged
//
//    private void cmbTagsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbTagsItemStateChanged
//        SYSPropsTools.storeState(this.getClass().getName() + "::cmbTags", cmbTags);
//    }//GEN-LAST:event_cmbTagsItemStateChanged
//

//


}
