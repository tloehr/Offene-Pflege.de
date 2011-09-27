/*
 * OffenePflege
 * Copyright (C) 2008 Torsten Löhr
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

import entity.SYSPropsTools;
import entity.StationenTools;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import op.OPDE;
import op.care.berichte.PnlBerichte;
import op.tools.ListElement;
import op.tools.SYSCalendar;
import op.tools.SYSPrint;
import op.tools.SYSTools;

/**
 *
 * @author  tloehr
 */
public class FrmCtrlMonitor extends javax.swing.JFrame {

    JCheckBox[] jbs = null;
    JFrame parent;
    boolean isCancelled;
    Object[] o = null;

    /**
     * Creates new form FrmCtrlMonitor
     */
    public FrmCtrlMonitor() {
        parent = this;
        isCancelled = false;
        initComponents();
        initForm();
        o = new Object[]{pbPart, lblProgress, isCancelled};
        setTitle(SYSTools.getWindowTitle("Controlling Cockpit"));
        setVisible(true);
    }

    private void initForm() {
        SYSTools.restoreState(this.getClass().getName() + "::cbBVAktivitaet", cbBVAktivitaet);
        SYSTools.restoreState(this.getClass().getName() + "::cbSozialBerichte", cbSozialBerichte);
        SYSTools.restoreState(this.getClass().getName() + "::cbGewicht", cbGewicht);
        SYSTools.restoreState(this.getClass().getName() + "::cbBilanz", cbBilanz);
        SYSTools.restoreState(this.getClass().getName() + "::cbNichtAbgehakteBHPs", cbNichtAbgehakteBHPs);
        SYSTools.restoreState(this.getClass().getName() + "::cbPlanung", cbPlanung);
        SYSTools.restoreState(this.getClass().getName() + "::cbVerordnungenOhneAnbruch", cbVerordnungenOhneAnbruch);
        SYSTools.restoreState(this.getClass().getName() + "::cbSozialZeiten", cbSozialZeiten);
        SYSTools.restoreState(this.getClass().getName() + "::cbGeringeVorraete", cbGeringeVorraete);
        SYSTools.restoreState(this.getClass().getName() + "::cbMediControl", cbMediControl);
        SYSTools.restoreState(this.getClass().getName() + "::cbSturzAnonym", cbSturzAnonym);
        SYSTools.restoreState(this.getClass().getName() + "::cbSturz", cbSturz);
        SYSTools.restoreState(this.getClass().getName() + "::cbBerichte", cbBerichte);
        SYSTools.restoreState(this.getClass().getName() + "::cbWunden", cbWunden);
        SYSTools.restoreState(this.getClass().getName() + "::cbInko", cbInko);
        SYSTools.restoreState(this.getClass().getName() + "::cbBeschwerden", cbBeschwerden);
        jbs = new JCheckBox[]{cbBVAktivitaet, cbBilanz, cbGewicht, cbGeringeVorraete, cbNichtAbgehakteBHPs, cbPlanung,
                    cbSozialBerichte, cbSozialZeiten, cbVerordnungenOhneAnbruch, cbMediControl, cbSturzAnonym,
                    cbSturz, cbBerichte, cbWunden, cbInko, cbBeschwerden
                };
        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);

        // Standardeinstellung für die Spinners wieder herstellen.
        int bhptage;
        int planungentage;
        int bvwochen;
        int gewichtmonate;
        int sozialwochen;
        int sozialzeitenwochen;
        int vorratprozent;
        int sturzamonate;
        int sturzmonate;
        int berichtemonate;
        int wundenmonate;
        int beschwerdenmonate;
        try {
            planungentage = Integer.parseInt(OPDE.getProps().getProperty(this.getClass().getName() + "::spinPlanungenTage"));
        } catch (NumberFormatException nfe) {
            planungentage = 7;
        }
        try {
            bhptage = Integer.parseInt(OPDE.getProps().getProperty(this.getClass().getName() + "::spinBHPTage"));
        } catch (NumberFormatException nfe) {
            bhptage = 7;
        }
        try {
            bvwochen = Integer.parseInt(OPDE.getProps().getProperty(this.getClass().getName() + "::spinBVWochen"));
        } catch (NumberFormatException nfe) {
            bvwochen = 1;
        }
        try {
            gewichtmonate = Integer.parseInt(OPDE.getProps().getProperty(this.getClass().getName() + "::spinGewichtMonate"));
        } catch (NumberFormatException nfe) {
            gewichtmonate = 6;
        }
        try {
            sozialwochen = Integer.parseInt(OPDE.getProps().getProperty(this.getClass().getName() + "::spinSozialWochen"));
        } catch (NumberFormatException nfe) {
            sozialwochen = 1;
        }
        try {
            sozialzeitenwochen = Integer.parseInt(OPDE.getProps().getProperty(this.getClass().getName() + "::spinSozialZeitenWochen"));
        } catch (NumberFormatException nfe) {
            sozialzeitenwochen = 1;
        }
        try {
            sturzamonate = Integer.parseInt(OPDE.getProps().getProperty(this.getClass().getName() + "::spinSturzAMonate"));
        } catch (NumberFormatException nfe) {
            sturzamonate = 6;
        }
        try {
            sturzmonate = Integer.parseInt(OPDE.getProps().getProperty(this.getClass().getName() + "::spinSturzMonate"));
        } catch (NumberFormatException nfe) {
            sturzmonate = 6;
        }
        try {
            vorratprozent = Integer.parseInt(OPDE.getProps().getProperty(this.getClass().getName() + "::spinVorratProzent"));
        } catch (NumberFormatException nfe) {
            vorratprozent = 20;
        }
        try {
            berichtemonate = Integer.parseInt(OPDE.getProps().getProperty(this.getClass().getName() + "::spinBerichteMonate"));
        } catch (NumberFormatException nfe) {
            berichtemonate = 1;
        }
        try {
            wundenmonate = Integer.parseInt(OPDE.getProps().getProperty(this.getClass().getName() + "::spinWundenMonate"));
        } catch (NumberFormatException nfe) {
            wundenmonate = 1;
        }
        try {
            beschwerdenmonate = Integer.parseInt(OPDE.getProps().getProperty(this.getClass().getName() + "::spinBeschwerdenMonate"));
        } catch (NumberFormatException nfe) {
            beschwerdenmonate = 5;
        }

        spinBHPTage.setModel(new SpinnerNumberModel(bhptage, 1, 500, 1));
        spinPlanungenTage.setModel(new SpinnerNumberModel(planungentage, 1, 500, 1));
        spinBVWochen.setModel(new SpinnerNumberModel(bvwochen, 1, 52, 1));
        spinBerichteMonate.setModel(new SpinnerNumberModel(berichtemonate, 1, 12, 1));
        spinWundenMonate.setModel(new SpinnerNumberModel(wundenmonate, 1, 12, 1));
        spinGewichtMonate.setModel(new SpinnerNumberModel(gewichtmonate, 1, 12, 1));
        spinSozialWochen.setModel(new SpinnerNumberModel(sozialwochen, 1, 52, 1));
        //spinSozialZeitenWochen.setModel(new SpinnerNumberModel(sozialzeitenwochen, 1, 52, 1));
        spinVorratProzent.setModel(new SpinnerNumberModel(vorratprozent, 0, 90, 5));
        spinSturzAMonate.setModel(new SpinnerNumberModel(sturzamonate, 1, 12, 1));
        spinSturzMonate.setModel(new SpinnerNumberModel(sturzmonate, 1, 12, 1));
        spinBeschwerdenMonate.setModel(new SpinnerNumberModel(beschwerdenmonate, 1, 12, 1));

        Date von = SYSCalendar.bom(SYSCalendar.addField(new Date(), -3, GregorianCalendar.YEAR));
        Date bis = SYSCalendar.eom(new Date());
        DefaultComboBoxModel monthmodel1 = SYSCalendar.createMonthList(von, bis);
        DefaultComboBoxModel monthmodel2 = SYSCalendar.createMonthList(von, bis);

        cmbBilanzMonat.setModel(monthmodel1);
        cmbBilanzMonat.setSelectedIndex(cmbBilanzMonat.getModel().getSize() - 2); // Auf den letzten Eintrag setzen.
        cmbPEAMonat.setModel(monthmodel2);
        cmbPEAMonat.setSelectedIndex(cmbPEAMonat.getModel().getSize() - 2); // Auf den letzten Eintrag setzen.
        //cmbBilanzMonat.setModel(monthmodel);
        //cmbBilanzMonat.setSelectedIndex(cmbBilanzMonat.getModel().getSize() - 2); // Auf den letzten Eintrag setzen.

        StationenTools.setComboBox(cmbStation);

        // Leeren Kopf vor die Liste setzen.
        ListElement[] headtag = new ListElement[]{new ListElement("Keine Auswahl", "")};
// TODO: muss noch gefixt werden.
        //        ListElement[] mytags = SYSTools.merge(headtag, PnlBerichte.tags);
//        cmbTags.setModel(new DefaultComboBoxModel(mytags));
//        SYSTools.restoreState(this.getClass().getName() + ":cmbTags", cmbTags);

        txtBerichte.setText(SYSTools.catchNull(OPDE.getProps().getProperty(this.getClass().getName() + "::txtBerichte")));

    }

    private void cleanup() {
        SYSTools.unregisterListeners(this);
    }

    private int anzahlGewuenschterAuswertungen() {
        int num = 0;
        for (int i = 0; i < jbs.length; i++) {
            if (jbs[i].isSelected()) {
                num++;
            }
        }
        return num;
    }

    private String createHTML() {

        StringBuilder html = new StringBuilder(1000);
        int progress = 0;
        pbMain.setMaximum(anzahlGewuenschterAuswertungen());
        pbMain.setMinimum(0);
        pbPart.setMinimum(0);
//
//        if (html.length() > 0) {
//            html.append("<p style=\"page-break-before: always\"/>");
//        }

        // ======================== MEDIZINISCH ======================== 
        if (!isCancelled && cbBilanz.isSelected()) {
            if (html.length() > 0) {
                html.append("<p style=\"page-break-before: always\"/>");
            }
            lblProgress.setText("Ein-/Ausfuhr/Bilanz");
            pbPart.setValue(0);
            Date monat = (Date) ((ListElement) cmbBilanzMonat.getSelectedItem()).getObject();
            html.append(DBHandling.getBilanzen(1, monat, o));
            progress++;
            pbMain.setValue(progress);
        }

        if (!isCancelled && cbGewicht.isSelected()) {
            if (html.length() > 0) {
                html.append("<p style=\"page-break-before: always\"/>");
            }
            lblProgress.setText("Gewichtstatistik");
            pbPart.setValue(0);
            int gewichtmonate = Integer.parseInt(spinGewichtMonate.getValue().toString());
            html.append(DBHandling.getGewichtsverlauf(gewichtmonate, 1, o));
            progress++;
            pbMain.setValue(progress);
        }

        // ORGANISATORISCH
        if (!isCancelled && cbBVAktivitaet.isSelected()) {
            if (html.length() > 0) {
                html.append("<p style=\"page-break-before: always\"/>");
            }
            lblProgress.setText("BV Aktivitäten");
            pbPart.setValue(0);
            int bvwochen = Integer.parseInt(spinBVWochen.getValue().toString());
            html.append(DBHandling.getBVBerichte("", "", 1, bvwochen));
            progress++;
            pbMain.setValue(progress);
        }

        if (!isCancelled && cbNichtAbgehakteBHPs.isSelected()) {
            if (html.length() > 0) {
                html.append("<p style=\"page-break-before: always\"/>");
            }
            lblProgress.setText("Nicht abgehakte BHPs");
            pbPart.setValue(0);
            int bhptage = Integer.parseInt(spinBHPTage.getValue().toString());
            html.append(DBHandling.getNichtAbgehakteBHPs(1, bhptage));
            progress++;
            pbMain.setValue(progress);
        }

        if (!isCancelled && cbPlanung.isSelected()) {
            if (html.length() > 0) {
                html.append("<p style=\"page-break-before: always\"/>");
            }
            lblProgress.setText("Planungen, die bald enden / abgelaufen sind");
            pbPart.setValue(0);
            int tage = Integer.parseInt(spinPlanungenTage.getValue().toString());
            html.append(DBHandling.getAblaufendePlanungen(1, tage));
            progress++;
            pbMain.setValue(progress);
        }

        if (!isCancelled && cbVerordnungenOhneAnbruch.isSelected()) {
            if (html.length() > 0) {
                html.append("<p style=\"page-break-before: always\"/>");
            }
            lblProgress.setText("Verordnungen ohne Medikamente im Anbruch");
            pbPart.setValue(0);
            html.append(DBHandling.getAktiveVorraeteOhneBestandImAnbruch(1));
            progress++;
            pbMain.setValue(progress);
        }

        if (!isCancelled && cbGeringeVorraete.isSelected()) {
            if (html.length() > 0) {
                html.append("<p style=\"page-break-before: always\"/>");
            }
            lblProgress.setText("Medikamenten Bestandsermittlung");
            pbPart.setValue(0);
            double prozent = Double.parseDouble(spinVorratProzent.getValue().toString());
            html.append(DBHandling.getGeringeVorraete(1, prozent, o));
            progress++;
        }
        pbMain.setValue(progress);

        if (!isCancelled && cbMediControl.isSelected()) {
            if (html.length() > 0) {
                html.append("<p style=\"page-break-before: always\"/>");
            }
            lblProgress.setText("Medikamenten Kontroll Liste");
            pbPart.setValue(0);
            String station = cmbStation.getSelectedItem().toString();
            html.append(DBHandling.getMediKontrolle(station, 1));
            progress++;
            pbMain.setValue(progress);
        }

        if (!isCancelled && cbBerichte.isSelected()) {

            if (html.length() > 0) {
                html.append("<p style=\"page-break-before: always\"/>");
            }
            lblProgress.setText("Pflegeberichte durchsuchen");
            pbPart.setValue(0);
            int berichtemonate = Integer.parseInt(spinBerichteMonate.getValue().toString());
            html.append(DBHandling.getBerichte(txtBerichte.getText(), (ListElement) cmbTags.getSelectedItem(), 1, berichtemonate, o));
            progress++;
            pbMain.setValue(progress);
        }

        if (!isCancelled && cbInko.isSelected()) {
            if (html.length() > 0) {
                html.append("<p style=\"page-break-before: always\"/>");
            }
            lblProgress.setText("Inkontinenz-Liste");
            pbPart.setValue(0);
            html.append(DBHandling.getInkontinenz(1));
            progress++;
            pbMain.setValue(progress);
        }

        if (!isCancelled && cbWunden.isSelected()) {
            if (html.length() > 0) {
                html.append("<p style=\"page-break-before: always\"/>");
            }
            lblProgress.setText("Wund-Liste");
            pbPart.setValue(0);
            int wundenmonat = Integer.parseInt(spinWundenMonate.getValue().toString());
            html.append(DBHandling.getWunden(1, wundenmonat, o));
            progress++;
            pbMain.setValue(progress);
        }

        if (!isCancelled && cbBeschwerden.isSelected()) {
            if (html.length() > 0) {
                html.append("<p style=\"page-break-before: always\"/>");
            }
            lblProgress.setText("Auswertung Beschwerden");
            pbPart.setValue(0);
            int beschwerdenmonat = Integer.parseInt(spinBeschwerdenMonate.getValue().toString());
            html.append(DBHandling.getBeschwerdeAuswertung(1, beschwerdenmonat));
            progress++;
            pbMain.setValue(progress);
        }

        // SOZIALES
        if (!isCancelled && cbSozialBerichte.isSelected()) {
            if (html.length() > 0) {
                html.append("<p style=\"page-break-before: always\"/>");
            }
            lblProgress.setText("Berichte Sozialer Dienst");
            pbPart.setValue(0);
            int sozialwochen = Integer.parseInt(spinSozialWochen.getValue().toString());
            html.append(DBHandling.getSozialBerichte("", "", 1, sozialwochen));
            progress++;
            pbMain.setValue(progress);
        }

        if (!isCancelled && cbSozialZeiten.isSelected()) {
            if (html.length() > 0) {
                html.append("<p style=\"page-break-before: always\"/>");
            }
            lblProgress.setText("Zeiten Sozialer Dienst");
            pbPart.setValue(0);
            Date monat = (Date) ((ListElement) cmbPEAMonat.getSelectedItem()).getObject();
            html.append(DBHandling.getSozialZeiten(1, monat));
            progress++;
            pbMain.setValue(progress);
        }

        // Statistik
        if (!isCancelled && cbSturzAnonym.isSelected()) {
            if (html.length() > 0) {
                html.append("<p style=\"page-break-before: always\"/>");
            }
            lblProgress.setText("Sturzstatistik anonym");
            pbPart.setValue(0);
            int sturzmonate = Integer.parseInt(spinSturzAMonate.getValue().toString());
            html.append(DBHandling.getAnonymSturz(1, sturzmonate));
            progress++;
            pbMain.setValue(progress);
        }
        if (!isCancelled && cbSturz.isSelected()) {
            if (html.length() > 0) {
                html.append("<p style=\"page-break-before: always\"/>");
            }
            lblProgress.setText("Sturzstatistik Bewohnerbezogen");
            pbPart.setValue(0);
            int sturzmonate = Integer.parseInt(spinSturzMonate.getValue().toString());
            html.append(DBHandling.getBWSturz(1, sturzmonate, o));
            progress++;
            pbMain.setValue(progress);
        }


        pbMain.setValue(0);
        pbPart.setValue(0);
        lblProgress.setText(" ");

        String result = "";
        if (!isCancelled) {
            result = SYSTools.toHTML(html.toString());
        }
        return result;
    }

    public void dispose() {
        cleanup();
        super.dispose();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        cbBVAktivitaet = new javax.swing.JCheckBox();
        cbNichtAbgehakteBHPs = new javax.swing.JCheckBox();
        cbPlanung = new javax.swing.JCheckBox();
        cbVerordnungenOhneAnbruch = new javax.swing.JCheckBox();
        spinBHPTage = new javax.swing.JSpinner();
        spinBVWochen = new javax.swing.JSpinner();
        spinPlanungenTage = new javax.swing.JSpinner();
        cbGeringeVorraete = new javax.swing.JCheckBox();
        spinVorratProzent = new javax.swing.JSpinner();
        cbBerichte = new javax.swing.JCheckBox();
        txtBerichte = new javax.swing.JTextField();
        spinBerichteMonate = new javax.swing.JSpinner();
        cbInko = new javax.swing.JCheckBox();
        cbBeschwerden = new javax.swing.JCheckBox();
        spinBeschwerdenMonate = new javax.swing.JSpinner();
        cmbTags = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        cbBilanz = new javax.swing.JCheckBox();
        cbGewicht = new javax.swing.JCheckBox();
        spinGewichtMonate = new javax.swing.JSpinner();
        cmbBilanzMonat = new javax.swing.JComboBox();
        cbMediControl = new javax.swing.JCheckBox();
        cmbStation = new javax.swing.JComboBox();
        cbWunden = new javax.swing.JCheckBox();
        spinWundenMonate = new javax.swing.JSpinner();
        jPanel5 = new javax.swing.JPanel();
        cbSozialBerichte = new javax.swing.JCheckBox();
        spinSozialWochen = new javax.swing.JSpinner();
        cbSozialZeiten = new javax.swing.JCheckBox();
        cmbPEAMonat = new javax.swing.JComboBox();
        jPanel6 = new javax.swing.JPanel();
        cbSturzAnonym = new javax.swing.JCheckBox();
        spinSturzAMonate = new javax.swing.JSpinner();
        cbSturz = new javax.swing.JCheckBox();
        spinSturzMonate = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        btnPrint = new javax.swing.JButton();
        pbMain = new javax.swing.JProgressBar();
        pbPart = new javax.swing.JProgressBar();
        lblProgress = new javax.swing.JLabel();
        btnStop = new javax.swing.JButton();

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Organisatorisch / Pflegerisch"));

        cbBVAktivitaet.setText("BV Aktivitäten");
        cbBVAktivitaet.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        cbBVAktivitaet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbBVAktivitaetActionPerformed(evt);
            }
        });

        cbNichtAbgehakteBHPs.setText("Nicht abgehakte BHPs");
        cbNichtAbgehakteBHPs.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        cbNichtAbgehakteBHPs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbNichtAbgehakteBHPsActionPerformed(evt);
            }
        });

        cbPlanung.setText("Planungen, die bald enden / abgelaufen sind");
        cbPlanung.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        cbPlanung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbPlanungActionPerformed(evt);
            }
        });

        cbVerordnungenOhneAnbruch.setText("Verordnungen ohne Medikamente im Anbruch");
        cbVerordnungenOhneAnbruch.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        cbVerordnungenOhneAnbruch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbVerordnungenOhneAnbruchActionPerformed(evt);
            }
        });

        spinBHPTage.setToolTipText("In den letzten n Tagen");
        spinBHPTage.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinBHPTageStateChanged(evt);
            }
        });

        spinBVWochen.setToolTipText("In den letzten n Wochen");
        spinBVWochen.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinBVWochenStateChanged(evt);
            }
        });

        spinPlanungenTage.setToolTipText("In den letzten n Tagen");
        spinPlanungenTage.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinPlanungenTageStateChanged(evt);
            }
        });

        cbGeringeVorraete.setText("geringe Vorräte / ohne Anbruch");
        cbGeringeVorraete.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        cbGeringeVorraete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbGeringeVorraeteActionPerformed(evt);
            }
        });

        spinVorratProzent.setToolTipText("Prozent des Bestandes");
        spinVorratProzent.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinVorratProzentStateChanged(evt);
            }
        });

        cbBerichte.setText("Berichte durchsuchen");
        cbBerichte.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        cbBerichte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbBerichteActionPerformed(evt);
            }
        });

        txtBerichte.setToolTipText("Suchbegriffe durch Leerzeichen getrennt. % als Wildcard.");
        txtBerichte.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBerichteFocusLost(evt);
            }
        });

        spinBerichteMonate.setToolTipText("Berichte der letzten n Monate durchsuchen");
        spinBerichteMonate.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinBerichteMonateStateChanged(evt);
            }
        });

        cbInko.setText("Inkontinenz-Liste");
        cbInko.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        cbInko.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbInkoActionPerformed(evt);
            }
        });

        cbBeschwerden.setText("Auswertungen Beschwerden");
        cbBeschwerden.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        cbBeschwerden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbBeschwerdenActionPerformed(evt);
            }
        });

        spinBeschwerdenMonate.setToolTipText("In den letzten n Monaten");
        spinBeschwerdenMonate.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinBeschwerdenMonateStateChanged(evt);
            }
        });

        cmbTags.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbTags.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbTagsItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cbBVAktivitaet)
                        .addGap(224, 224, 224)
                        .addComponent(spinBVWochen, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE))
                    .addComponent(cbVerordnungenOhneAnbruch)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbPlanung)
                            .addComponent(cbNichtAbgehakteBHPs))
                        .addGap(34, 34, 34)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(spinBHPTage, javax.swing.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)
                            .addComponent(spinPlanungenTage, javax.swing.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(cbBerichte)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBerichte, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE))
                            .addComponent(cbGeringeVorraete))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(spinBerichteMonate, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(spinVorratProzent, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbInko)
                            .addComponent(cbBeschwerden))
                        .addGap(139, 139, 139)
                        .addComponent(spinBeschwerdenMonate, javax.swing.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(cmbTags, 0, 366, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbBVAktivitaet)
                    .addComponent(spinBVWochen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbNichtAbgehakteBHPs)
                    .addComponent(spinBHPTage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbPlanung)
                    .addComponent(spinPlanungenTage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbVerordnungenOhneAnbruch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spinVorratProzent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbGeringeVorraete))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbBerichte)
                        .addComponent(txtBerichte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(spinBerichteMonate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbTags, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cbInko)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbBeschwerden))
                    .addComponent(spinBeschwerdenMonate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Medizinisch"));

        cbBilanz.setText("Ein- und Ausfuhr / Bilanzen");
        cbBilanz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbBilanzActionPerformed(evt);
            }
        });

        cbGewicht.setText("Gewichtsstatistik / BMI");
        cbGewicht.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbGewichtActionPerformed(evt);
            }
        });

        spinGewichtMonate.setToolTipText("In den letzten n Monaten");
        spinGewichtMonate.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinGewichtMonateStateChanged(evt);
            }
        });

        cmbBilanzMonat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbBilanzMonat.setToolTipText("Auswertung ab Monat");

        cbMediControl.setText("Medikamenten Prüfliste");
        cbMediControl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbMediControlActionPerformed(evt);
            }
        });

        cmbStation.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbStation.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbStationItemStateChanged(evt);
            }
        });

        cbWunden.setText("Wund-Berichte / Doku");
        cbWunden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbWundenActionPerformed(evt);
            }
        });

        spinWundenMonate.setToolTipText("In den letzten n Monaten");
        spinWundenMonate.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinWundenMonateStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(cbGewicht)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 178, Short.MAX_VALUE)
                        .addComponent(spinGewichtMonate, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbBilanz)
                            .addComponent(cbMediControl, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbStation, 0, 189, Short.MAX_VALUE)
                            .addComponent(cmbBilanzMonat, 0, 189, Short.MAX_VALUE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(cbWunden)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 183, Short.MAX_VALUE)
                        .addComponent(spinWundenMonate, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cbBilanz)
                    .addComponent(cmbBilanzMonat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbGewicht)
                    .addComponent(spinGewichtMonate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(cmbStation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbMediControl)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbWunden)
                    .addComponent(spinWundenMonate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(160, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder("Sozialer Dienst")));

        cbSozialBerichte.setText("Berichte Sozialer Dienst");
        cbSozialBerichte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSozialBerichteActionPerformed(evt);
            }
        });

        spinSozialWochen.setToolTipText("In den letzten n Wochen");
        spinSozialWochen.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinSozialWochenStateChanged(evt);
            }
        });

        cbSozialZeiten.setText("Zeiten Sozialer Dienst");
        cbSozialZeiten.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSozialZeitenActionPerformed(evt);
            }
        });

        cmbPEAMonat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbPEAMonat.setToolTipText("Auswertung ab Monat");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(cbSozialBerichte)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 151, Short.MAX_VALUE)
                        .addComponent(spinSozialWochen, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(cbSozialZeiten)
                        .addGap(54, 54, 54)
                        .addComponent(cmbPEAMonat, 0, 160, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbSozialBerichte)
                    .addComponent(spinSozialWochen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbSozialZeiten)
                    .addComponent(cmbPEAMonat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(108, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Statistik"));

        cbSturzAnonym.setText("Sturzstatistik anonym");
        cbSturzAnonym.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSturzAnonymActionPerformed(evt);
            }
        });

        spinSturzAMonate.setToolTipText("In den letzten n Monaten");
        spinSturzAMonate.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinSturzAMonateStateChanged(evt);
            }
        });

        cbSturz.setText("Sturzstatistik BW bezogen");
        cbSturz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSturzActionPerformed(evt);
            }
        });

        spinSturzMonate.setToolTipText("In den letzten n Monaten");
        spinSturzMonate.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinSturzMonateStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(cbSturzAnonym)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 183, Short.MAX_VALUE)
                        .addComponent(spinSturzAMonate, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(cbSturz)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 158, Short.MAX_VALUE)
                        .addComponent(spinSturzMonate, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbSturzAnonym)
                    .addComponent(spinSturzAMonate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbSturz)
                    .addComponent(spinSturzMonate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(109, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jPanel2, jPanel3});

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 18));
        jLabel1.setText("Controlling");

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/fileprint.png"))); // NOI18N
        btnPrint.setText("Drucken");
        btnPrint.setFocusable(false);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrint);

        pbPart.setForeground(new java.awt.Color(255, 102, 102));

        lblProgress.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblProgress.setText(" ");

        btnStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/artwork/22x22/cancel.png"))); // NOI18N
        btnStop.setText("STOP");
        btnStop.setEnabled(false);
        btnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 878, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pbPart, javax.swing.GroupLayout.DEFAULT_SIZE, 790, Short.MAX_VALUE)
                    .addComponent(pbMain, javax.swing.GroupLayout.DEFAULT_SIZE, 790, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnStop)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(lblProgress, javax.swing.GroupLayout.DEFAULT_SIZE, 744, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblProgress))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pbMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pbPart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnStop, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-878)/2, (screenSize.height-697)/2, 878, 697);
    }// </editor-fold>//GEN-END:initComponents

private void cbBVAktivitaetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbBVAktivitaetActionPerformed
    SYSTools.storeState(this.getClass().getName() + "::cbBVAktivitaet", cbBVAktivitaet);
    btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
}//GEN-LAST:event_cbBVAktivitaetActionPerformed

private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed

    //String result = "";

    SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {

        protected String doInBackground() throws Exception {
            String s = createHTML();
            return s;
        }

        @Override
        protected void done() {
            try {
                String get = SYSTools.catchNull(get());
                String result = SYSTools.htmlUmlautConversion(SYSTools.catchNull(get()));
                if (!SYSTools.catchNull(result).equals("")) {
                    SYSPrint.print(parent, result, false);
                }
                btnPrint.setEnabled(true);
                btnStop.setEnabled(false);
                isCancelled = false;
                o[2] = isCancelled;
            } catch (InterruptedException ex) {
                Logger.getLogger(FrmCtrlMonitor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(FrmCtrlMonitor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };
    btnPrint.setEnabled(false);
    btnStop.setEnabled(true);
    worker.execute();

}//GEN-LAST:event_btnPrintActionPerformed

private void cbSozialBerichteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSozialBerichteActionPerformed
    SYSTools.storeState(this.getClass().getName() + "::cbSozialBerichte", cbSozialBerichte);
}//GEN-LAST:event_cbSozialBerichteActionPerformed

private void spinBVWochenStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinBVWochenStateChanged
    spinBVWochen.setToolTipText("In den letzten " + spinBVWochen.getValue().toString() + " Wochen");
    SYSPropsTools.storeProp(this.getClass().getName() + "::spinBVWochen", spinBVWochen.getValue().toString(), OPDE.getLogin().getUser());
    //SYSTools.putProps(this.getClass().getName() + "::spinBVWochen", spinBVWochen.getValue().toString(), false, "");
}//GEN-LAST:event_spinBVWochenStateChanged

private void spinBHPTageStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinBHPTageStateChanged
    spinBHPTage.setToolTipText("In den letzten " + spinBHPTage.getValue().toString() + " Tagen");
    SYSPropsTools.storeProp(this.getClass().getName() + "::spinBHPTage", spinBHPTage.getValue().toString(), OPDE.getLogin().getUser());
    //SYSTools.putProps(this.getClass().getName() + "::spinBHPTage", spinBHPTage.getValue().toString(), false, "");
}//GEN-LAST:event_spinBHPTageStateChanged

private void spinSozialWochenStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinSozialWochenStateChanged
    spinSozialWochen.setToolTipText("In den letzten " + spinSozialWochen.getValue().toString() + " Wochen");
    SYSPropsTools.storeProp(this.getClass().getName() + "::spinSozialWochen", spinSozialWochen.getValue().toString(), OPDE.getLogin().getUser());
    //SYSTools.putProps(this.getClass().getName() + "::spinSozialWochen", spinBHPTage.getValue().toString(), false, "");
}//GEN-LAST:event_spinSozialWochenStateChanged

private void spinGewichtMonateStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinGewichtMonateStateChanged
    spinGewichtMonate.setToolTipText("In den letzten " + spinGewichtMonate.getValue().toString() + " Monate");
    SYSPropsTools.storeProp(this.getClass().getName() + "::spinGewichtMonate", spinGewichtMonate.getValue().toString(), OPDE.getLogin().getUser());
    //SYSTools.putProps(this.getClass().getName() + "::spinGewichtMonate", spinGewichtMonate.getValue().toString(), false, "");
}//GEN-LAST:event_spinGewichtMonateStateChanged

private void cbNichtAbgehakteBHPsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbNichtAbgehakteBHPsActionPerformed
    SYSTools.storeState(this.getClass().getName() + "::cbNichtAbgehakteBHPs", cbNichtAbgehakteBHPs);
    btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
}//GEN-LAST:event_cbNichtAbgehakteBHPsActionPerformed

private void cbPlanungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbPlanungActionPerformed
    SYSTools.storeState(this.getClass().getName() + "::cbPlanung", cbPlanung);
    btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
}//GEN-LAST:event_cbPlanungActionPerformed

private void cbVerordnungenOhneAnbruchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbVerordnungenOhneAnbruchActionPerformed
    SYSTools.storeState(this.getClass().getName() + "::cbVerordnungenOhneAnbruch", cbVerordnungenOhneAnbruch);
    btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
}//GEN-LAST:event_cbVerordnungenOhneAnbruchActionPerformed

private void cbBilanzActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbBilanzActionPerformed
    SYSTools.storeState(this.getClass().getName() + "::cbBilanz", cbBilanz);
    btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
}//GEN-LAST:event_cbBilanzActionPerformed

private void cbGewichtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbGewichtActionPerformed
    SYSTools.storeState(this.getClass().getName() + "::cbGewicht", cbGewicht);
    btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
}//GEN-LAST:event_cbGewichtActionPerformed

private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed
    isCancelled = true;
    o[2] = isCancelled;
}//GEN-LAST:event_btnStopActionPerformed

private void cbSozialZeitenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSozialZeitenActionPerformed
    SYSTools.storeState(this.getClass().getName() + "::cbSozialZeiten", cbSozialZeiten);
    btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
}//GEN-LAST:event_cbSozialZeitenActionPerformed

private void spinPlanungenTageStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinPlanungenTageStateChanged
    spinPlanungenTage.setToolTipText("In den letzten " + spinPlanungenTage.getValue().toString() + " Tagen");
    SYSPropsTools.storeProp(this.getClass().getName() + "::spinPlanungenTage", spinPlanungenTage.getValue().toString(), OPDE.getLogin().getUser());
    //SYSTools.putProps(this.getClass().getName() + "::spinPlanungenTage", spinPlanungenTage.getValue().toString(), false, "");
}//GEN-LAST:event_spinPlanungenTageStateChanged

private void spinVorratProzentStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinVorratProzentStateChanged
    spinVorratProzent.setToolTipText("In den letzten " + spinVorratProzent.getValue().toString() + " Tagen");
    SYSPropsTools.storeProp(this.getClass().getName() + "::spinVorratProzent", spinVorratProzent.getValue().toString(), OPDE.getLogin().getUser());
    //SYSTools.putProps(this.getClass().getName() + "::spinVorratProzent", spinVorratProzent.getValue().toString(), false, "");
}//GEN-LAST:event_spinVorratProzentStateChanged

private void cbGeringeVorraeteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbGeringeVorraeteActionPerformed
    SYSTools.storeState(this.getClass().getName() + "::cbGeringeVorraete", cbGeringeVorraete);
    btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
}//GEN-LAST:event_cbGeringeVorraeteActionPerformed

private void cbMediControlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbMediControlActionPerformed
    SYSTools.storeState(this.getClass().getName() + "::cbMediControl", cbMediControl);
    btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
}//GEN-LAST:event_cbMediControlActionPerformed

private void cmbStationItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbStationItemStateChanged
    SYSTools.storeState(this.getClass().getName() + "::cmbStation", cmbStation);
}//GEN-LAST:event_cmbStationItemStateChanged

private void spinSturzAMonateStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinSturzAMonateStateChanged
    spinSturzAMonate.setToolTipText("In den letzten " + spinSturzAMonate.getValue().toString() + " Monaten");
    SYSPropsTools.storeProp(this.getClass().getName() + "::spinSturzAMonate", spinSturzAMonate.getValue().toString(), OPDE.getLogin().getUser());
    //SYSTools.putProps(this.getClass().getName() + "::spinSturzAMonate", spinSturzAMonate.getValue().toString(), false, "");
}//GEN-LAST:event_spinSturzAMonateStateChanged

private void cbSturzAnonymActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSturzAnonymActionPerformed
    SYSTools.storeState(this.getClass().getName() + "::cbSturzAnonym", cbSturzAnonym);
    btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
}//GEN-LAST:event_cbSturzAnonymActionPerformed

private void spinSturzMonateStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinSturzMonateStateChanged
    spinSturzMonate.setToolTipText("In den letzten " + spinSturzMonate.getValue().toString() + " Monaten");
    SYSPropsTools.storeProp(this.getClass().getName() + "::spinSturzMonate", spinSturzMonate.getValue().toString(), OPDE.getLogin().getUser());
    //SYSTools.putProps(this.getClass().getName() + "::spinSturzMonate", spinSturzMonate.getValue().toString(), false, "");
}//GEN-LAST:event_spinSturzMonateStateChanged

private void cbSturzActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSturzActionPerformed
    SYSTools.storeState(this.getClass().getName() + "::cbSturz", cbSturz);
    btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
}//GEN-LAST:event_cbSturzActionPerformed

private void cbBerichteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbBerichteActionPerformed
    SYSTools.storeState(this.getClass().getName() + "::cbBerichte", cbBerichte);
    btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
}//GEN-LAST:event_cbBerichteActionPerformed

private void txtBerichteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBerichteFocusLost
    SYSPropsTools.storeProp(this.getClass().getName() + "::txtBerichte", txtBerichte.getText(), OPDE.getLogin().getUser());
    //SYSTools.putProps(this.getClass().getName() + "::txtBerichte", txtBerichte.getText(), false, "");
}//GEN-LAST:event_txtBerichteFocusLost

private void spinBerichteMonateStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinBerichteMonateStateChanged
    SYSPropsTools.storeProp(this.getClass().getName() + "::spinBerichteMonate", spinBerichteMonate.getValue().toString(), OPDE.getLogin().getUser());
    //SYSTools.putProps(this.getClass().getName() + "::spinBerichteMonate", spinBerichteMonate.getValue().toString(), false, "");
}//GEN-LAST:event_spinBerichteMonateStateChanged

private void cbWundenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbWundenActionPerformed
    SYSTools.storeState(this.getClass().getName() + "::cbWunden", cbWunden);
    btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
}//GEN-LAST:event_cbWundenActionPerformed

private void spinWundenMonateStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinWundenMonateStateChanged
    SYSPropsTools.storeProp(this.getClass().getName() + "::spinWundenMonate", spinWundenMonate.getValue().toString(), OPDE.getLogin().getUser());
    //SYSTools.putProps(this.getClass().getName() + "::spinWundenMonate", spinWundenMonate.getValue().toString(), false, "");
}//GEN-LAST:event_spinWundenMonateStateChanged

private void cbInkoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbInkoActionPerformed
    SYSTools.storeState(this.getClass().getName() + "::cbInko", cbInko);
    btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
}//GEN-LAST:event_cbInkoActionPerformed

private void cbBeschwerdenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbBeschwerdenActionPerformed
    SYSTools.storeState(this.getClass().getName() + "::cbBeschwerden", cbBeschwerden);
    btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
}//GEN-LAST:event_cbBeschwerdenActionPerformed

private void spinBeschwerdenMonateStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinBeschwerdenMonateStateChanged
    SYSPropsTools.storeProp(this.getClass().getName() + "::spinBeschwerdenMonate", spinBeschwerdenMonate.getValue().toString(), OPDE.getLogin().getUser());
    //SYSTools.putProps(this.getClass().getName() + "::spinBeschwerdenMonate", spinBeschwerdenMonate.getValue().toString(), false, "");
}//GEN-LAST:event_spinBeschwerdenMonateStateChanged

private void cmbTagsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbTagsItemStateChanged
    SYSTools.storeState(this.getClass().getName() + "::cmbTags", cmbTags);
}//GEN-LAST:event_cmbTagsItemStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnStop;
    private javax.swing.JCheckBox cbBVAktivitaet;
    private javax.swing.JCheckBox cbBerichte;
    private javax.swing.JCheckBox cbBeschwerden;
    private javax.swing.JCheckBox cbBilanz;
    private javax.swing.JCheckBox cbGeringeVorraete;
    private javax.swing.JCheckBox cbGewicht;
    private javax.swing.JCheckBox cbInko;
    private javax.swing.JCheckBox cbMediControl;
    private javax.swing.JCheckBox cbNichtAbgehakteBHPs;
    private javax.swing.JCheckBox cbPlanung;
    private javax.swing.JCheckBox cbSozialBerichte;
    private javax.swing.JCheckBox cbSozialZeiten;
    private javax.swing.JCheckBox cbSturz;
    private javax.swing.JCheckBox cbSturzAnonym;
    private javax.swing.JCheckBox cbVerordnungenOhneAnbruch;
    private javax.swing.JCheckBox cbWunden;
    private javax.swing.JComboBox cmbBilanzMonat;
    private javax.swing.JComboBox cmbPEAMonat;
    private javax.swing.JComboBox cmbStation;
    private javax.swing.JComboBox cmbTags;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblProgress;
    private javax.swing.JProgressBar pbMain;
    private javax.swing.JProgressBar pbPart;
    private javax.swing.JSpinner spinBHPTage;
    private javax.swing.JSpinner spinBVWochen;
    private javax.swing.JSpinner spinBerichteMonate;
    private javax.swing.JSpinner spinBeschwerdenMonate;
    private javax.swing.JSpinner spinGewichtMonate;
    private javax.swing.JSpinner spinPlanungenTage;
    private javax.swing.JSpinner spinSozialWochen;
    private javax.swing.JSpinner spinSturzAMonate;
    private javax.swing.JSpinner spinSturzMonate;
    private javax.swing.JSpinner spinVorratProzent;
    private javax.swing.JSpinner spinWundenMonate;
    private javax.swing.JTextField txtBerichte;
    // End of variables declaration//GEN-END:variables
}
