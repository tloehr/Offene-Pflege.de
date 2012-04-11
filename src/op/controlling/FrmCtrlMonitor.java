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

import entity.PBerichtTAGS;
import entity.PflegeberichteTools;
import entity.Stationen;
import entity.StationenTools;
import entity.system.SYSPropsTools;
import entity.verordnungen.MedBestandTools;
import op.OPDE;
import op.tools.SYSCalendar;
import op.tools.SYSPrint;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author tloehr
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
        SYSPropsTools.restoreState(this.getClass().getName() + "::cbBVAktivitaet", cbBVAktivitaet);
        SYSPropsTools.restoreState(this.getClass().getName() + "::cbSozialBerichte", cbSozialBerichte);
        SYSPropsTools.restoreState(this.getClass().getName() + "::cbGewicht", cbGewicht);
        SYSPropsTools.restoreState(this.getClass().getName() + "::cbBilanz", cbBilanz);
        SYSPropsTools.restoreState(this.getClass().getName() + "::cbNichtAbgehakteBHPs", cbNichtAbgehakteBHPs);
        SYSPropsTools.restoreState(this.getClass().getName() + "::cbPlanung", cbPlanung);
        SYSPropsTools.restoreState(this.getClass().getName() + "::cbVerordnungenOhneAnbruch", cbVerordnungenOhneAnbruch);
        SYSPropsTools.restoreState(this.getClass().getName() + "::cbSozialZeiten", cbSozialZeiten);
        SYSPropsTools.restoreState(this.getClass().getName() + "::cbGeringeVorraete", cbGeringeVorraete);
        SYSPropsTools.restoreState(this.getClass().getName() + "::cbMediControl", cbMediControl);
        SYSPropsTools.restoreState(this.getClass().getName() + "::cbSturzAnonym", cbSturzAnonym);
        SYSPropsTools.restoreState(this.getClass().getName() + "::cbSturz", cbSturz);
        SYSPropsTools.restoreState(this.getClass().getName() + "::cbBerichte", cbBerichte);
        SYSPropsTools.restoreState(this.getClass().getName() + "::cbWunden", cbWunden);
        SYSPropsTools.restoreState(this.getClass().getName() + "::cbInko", cbInko);
        SYSPropsTools.restoreState(this.getClass().getName() + "::cbBeschwerden", cbBeschwerden);
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
        int sozialmonate;
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
            sozialmonate = Integer.parseInt(OPDE.getProps().getProperty(this.getClass().getName() + "::spinSozialMonate"));
        } catch (NumberFormatException nfe) {
            sozialmonate = 1;
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
        spinSozialMonate.setModel(new SpinnerNumberModel(sozialmonate, 1, 12, 1));
        //spinSozialZeitenWochen.setModel(new SpinnerNumberModel(sozialzeitenwochen, 1, 52, 1));
        spinVorratProzent.setModel(new SpinnerNumberModel(vorratprozent, 0, 90, 5));
        spinSturzAMonate.setModel(new SpinnerNumberModel(sturzamonate, 1, 12, 1));
        spinSturzMonate.setModel(new SpinnerNumberModel(sturzmonate, 1, 12, 1));
        spinBeschwerdenMonate.setModel(new SpinnerNumberModel(beschwerdenmonate, 1, 12, 1));

        Date von = SYSCalendar.bom(SYSCalendar.addField(new Date(), -3, GregorianCalendar.YEAR));
        Date bis = SYSCalendar.eom(new Date());

//        DefaultComboBoxModel monthmodel1 = SYSCalendar.createMonthList(von, bis);
//        DefaultComboBoxModel monthmodel2 = SYSCalendar.createMonthList(von, bis);

        cmbBilanzMonat.setModel(SYSCalendar.createMonthList(von, bis));
        cmbBilanzMonat.setRenderer(new ListCellRenderer() {
            Format formatter = new SimpleDateFormat("MMMM yyyy");

            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                String text = formatter.format(o);
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
            }
        });

        cmbBilanzMonat.setSelectedIndex(cmbBilanzMonat.getModel().getSize() - 2); // Auf den letzten Eintrag setzen.

        cmbPEAMonat.setModel(SYSCalendar.createMonthList(von, bis));
        cmbPEAMonat.setRenderer(new ListCellRenderer() {
            Format formatter = new SimpleDateFormat("MMMM yyyy");

            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                String text = formatter.format(o);
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
            }
        });
        cmbPEAMonat.setSelectedIndex(cmbPEAMonat.getModel().getSize() - 2); // Auf den letzten Eintrag setzen.

        EntityManager em = OPDE.createEM();
        Query query1 = em.createNamedQuery("Stationen.findAllSorted");
        cmbStation.setModel(new DefaultComboBoxModel(new Vector<Stationen>(query1.getResultList())));
        cmbStation.setSelectedItem(StationenTools.getSpecialStation());

        // Leeren Kopf vor die Liste setzen.
//        ListElement[] headtag = new ListElement[]{new ListElement("Keine Auswahl", "")};


        Query query = em.createNamedQuery("PBerichtTAGS.findAllActive");
        cmbTags.setModel(SYSTools.lst2cmb(SYSTools.list2dlm(query.getResultList())));
        cmbTags.setRenderer(new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {

                return new DefaultListCellRenderer().getListCellRendererComponent(jList, ((PBerichtTAGS) o).getBezeichnung(), i, isSelected, cellHasFocus);
            }
        });
        SYSPropsTools.restoreState(this.getClass().getName() + ":cmbTags", cmbTags);
        em.close();

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
        html.append("<div id=\"fonttext\">");

        int progress = 0;
        EntityManager em = OPDE.createEM();
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
            Date monat = (Date) cmbBilanzMonat.getSelectedItem();
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
            html.append(PflegeberichteTools.getBVBerichte(em, 1, bvwochen));
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
//            if (html.length() > 0) {
//                html.append("<p style=\"page-break-before: always\"/>");
//            }
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
//            if (html.length() > 0) {
//                html.append("<p style=\"page-break-before: always\"/>");
//            }
            lblProgress.setText("Medikamenten Kontroll Liste");
            pbPart.setValue(0);
//            String station = cmbStation.getSelectedItem().toString();
//            html.append(DBHandling.getMediKontrolle(station, 1));
            html.append(MedBestandTools.getMediKontrolle(em, (Stationen) cmbStation.getSelectedItem(), 1));
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
//            html.append(DBHandling.getBerichte(txtBerichte.getText(), (ListElement) cmbTags.getSelectedItem(), 1, berichtemonate, o));
            html.append(PflegeberichteTools.getBerichteASHTML(em, txtBerichte.getText(), (PBerichtTAGS) cmbTags.getSelectedItem(), 1, berichtemonate));
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
            int sozialmonate = Integer.parseInt(spinSozialMonate.getValue().toString());
            Query query = em.createNamedQuery("PBerichtTAGS.findByKurzbezeichnung");
            query.setParameter("kurzbezeichnung", "soz");
            PBerichtTAGS sozTag = (PBerichtTAGS) query.getSingleResult();

            html.append(PflegeberichteTools.getBerichteASHTML(em, "", sozTag, 1, sozialmonate));
//            html.append(DBHandling.getSozialBerichte("", "", 1, sozialmonate));
            progress++;
            pbMain.setValue(progress);
        }

        if (!isCancelled && cbSozialZeiten.isSelected()) {
            if (html.length() > 0) {
                html.append("<p style=\"page-break-before: always\"/>");
            }
            lblProgress.setText("Zeiten Sozialer Dienst");
            pbPart.setValue(0);
            Date monat = (Date) cmbPEAMonat.getSelectedItem();
//            html.append(DBHandling.getSozialZeiten(1, monat));
            html.append(PflegeberichteTools.getSozialZeiten(em, 1, monat));
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

        em.close();

        pbMain.setValue(0);
        pbPart.setValue(0);
        lblProgress.setText(" ");

        String result = "";
        if (!isCancelled) {
            html.append("</div>");
            result = html.toString();
        }
        return result;
    }

    public void dispose() {
        cleanup();
        super.dispose();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new JPanel();
        jPanel2 = new JPanel();
        cbBVAktivitaet = new JCheckBox();
        cbNichtAbgehakteBHPs = new JCheckBox();
        cbPlanung = new JCheckBox();
        cbVerordnungenOhneAnbruch = new JCheckBox();
        spinBHPTage = new JSpinner();
        spinBVWochen = new JSpinner();
        spinPlanungenTage = new JSpinner();
        cbGeringeVorraete = new JCheckBox();
        spinVorratProzent = new JSpinner();
        cbBerichte = new JCheckBox();
        txtBerichte = new JTextField();
        spinBerichteMonate = new JSpinner();
        cbInko = new JCheckBox();
        cbBeschwerden = new JCheckBox();
        spinBeschwerdenMonate = new JSpinner();
        cmbTags = new JComboBox();
        jPanel3 = new JPanel();
        cbBilanz = new JCheckBox();
        cbGewicht = new JCheckBox();
        spinGewichtMonate = new JSpinner();
        cmbBilanzMonat = new JComboBox();
        cbMediControl = new JCheckBox();
        cmbStation = new JComboBox();
        cbWunden = new JCheckBox();
        spinWundenMonate = new JSpinner();
        jPanel5 = new JPanel();
        cbSozialBerichte = new JCheckBox();
        spinSozialMonate = new JSpinner();
        cbSozialZeiten = new JCheckBox();
        cmbPEAMonat = new JComboBox();
        jPanel6 = new JPanel();
        cbSturzAnonym = new JCheckBox();
        spinSturzAMonate = new JSpinner();
        cbSturz = new JCheckBox();
        spinSturzMonate = new JSpinner();
        jLabel1 = new JLabel();
        jToolBar1 = new JToolBar();
        btnPrint = new JButton();
        pbMain = new JProgressBar();
        pbPart = new JProgressBar();
        lblProgress = new JLabel();
        btnStop = new JButton();
        jPanel4 = new JPanel();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));

            //======== jPanel2 ========
            {
                jPanel2.setBorder(new TitledBorder("Organisatorisch / Pflegerisch"));

                //---- cbBVAktivitaet ----
                cbBVAktivitaet.setText("BV Aktivit\u00e4ten");
                cbBVAktivitaet.setBorder(new EmptyBorder(1, 1, 1, 1));
                cbBVAktivitaet.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cbBVAktivitaetActionPerformed(e);
                    }
                });

                //---- cbNichtAbgehakteBHPs ----
                cbNichtAbgehakteBHPs.setText("Nicht abgehakte BHPs");
                cbNichtAbgehakteBHPs.setBorder(new EmptyBorder(1, 1, 1, 1));
                cbNichtAbgehakteBHPs.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cbNichtAbgehakteBHPsActionPerformed(e);
                    }
                });

                //---- cbPlanung ----
                cbPlanung.setText("Planungen, die bald enden / abgelaufen sind");
                cbPlanung.setBorder(new EmptyBorder(1, 1, 1, 1));
                cbPlanung.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cbPlanungActionPerformed(e);
                    }
                });

                //---- cbVerordnungenOhneAnbruch ----
                cbVerordnungenOhneAnbruch.setText("Verordnungen ohne Medikamente im Anbruch");
                cbVerordnungenOhneAnbruch.setBorder(new EmptyBorder(1, 1, 1, 1));
                cbVerordnungenOhneAnbruch.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cbVerordnungenOhneAnbruchActionPerformed(e);
                    }
                });

                //---- spinBHPTage ----
                spinBHPTage.setToolTipText("In den letzten n Tagen");
                spinBHPTage.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        spinBHPTageStateChanged(e);
                    }
                });

                //---- spinBVWochen ----
                spinBVWochen.setToolTipText("In den letzten n Wochen");
                spinBVWochen.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        spinBVWochenStateChanged(e);
                    }
                });

                //---- spinPlanungenTage ----
                spinPlanungenTage.setToolTipText("In den letzten n Tagen");
                spinPlanungenTage.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        spinPlanungenTageStateChanged(e);
                    }
                });

                //---- cbGeringeVorraete ----
                cbGeringeVorraete.setText("geringe Vorr\u00e4te / ohne Anbruch");
                cbGeringeVorraete.setBorder(new EmptyBorder(1, 1, 1, 1));
                cbGeringeVorraete.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cbGeringeVorraeteActionPerformed(e);
                    }
                });

                //---- spinVorratProzent ----
                spinVorratProzent.setToolTipText("Prozent des Bestandes");
                spinVorratProzent.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        spinVorratProzentStateChanged(e);
                    }
                });

                //---- cbBerichte ----
                cbBerichte.setText("Berichte durchsuchen");
                cbBerichte.setBorder(new EmptyBorder(1, 1, 1, 1));
                cbBerichte.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cbBerichteActionPerformed(e);
                    }
                });

                //---- txtBerichte ----
                txtBerichte.setToolTipText("Suchbegriffe durch Leerzeichen getrennt. % als Wildcard.");
                txtBerichte.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        txtBerichteFocusLost(e);
                    }
                });

                //---- spinBerichteMonate ----
                spinBerichteMonate.setToolTipText("Berichte der letzten n Monate durchsuchen");
                spinBerichteMonate.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        spinBerichteMonateStateChanged(e);
                    }
                });

                //---- cbInko ----
                cbInko.setText("Inkontinenz-Liste");
                cbInko.setBorder(new EmptyBorder(1, 1, 1, 1));
                cbInko.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cbInkoActionPerformed(e);
                    }
                });

                //---- cbBeschwerden ----
                cbBeschwerden.setText("Auswertungen Beschwerden");
                cbBeschwerden.setBorder(new EmptyBorder(1, 1, 1, 1));
                cbBeschwerden.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cbBeschwerdenActionPerformed(e);
                    }
                });

                //---- spinBeschwerdenMonate ----
                spinBeschwerdenMonate.setToolTipText("In den letzten n Monaten");
                spinBeschwerdenMonate.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        spinBeschwerdenMonateStateChanged(e);
                    }
                });

                //---- cmbTags ----
                cmbTags.setModel(new DefaultComboBoxModel(new String[]{
                        "Item 1",
                        "Item 2",
                        "Item 3",
                        "Item 4"
                }));
                cmbTags.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        cmbTagsItemStateChanged(e);
                    }
                });

                GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
                jPanel2.setLayout(jPanel2Layout);
                jPanel2Layout.setHorizontalGroup(
                        jPanel2Layout.createParallelGroup()
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(jPanel2Layout.createParallelGroup()
                                                .addGroup(jPanel2Layout.createSequentialGroup()
                                                        .addComponent(cbBVAktivitaet)
                                                        .addGap(224, 224, 224)
                                                        .addComponent(spinBVWochen, GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE))
                                                .addComponent(cbVerordnungenOhneAnbruch)
                                                .addGroup(jPanel2Layout.createSequentialGroup()
                                                        .addGroup(jPanel2Layout.createParallelGroup()
                                                                .addComponent(cbPlanung)
                                                                .addComponent(cbNichtAbgehakteBHPs))
                                                        .addGap(34, 34, 34)
                                                        .addGroup(jPanel2Layout.createParallelGroup()
                                                                .addComponent(spinBHPTage, GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                                                                .addComponent(spinPlanungenTage, GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)))
                                                .addGroup(jPanel2Layout.createSequentialGroup()
                                                        .addGroup(jPanel2Layout.createParallelGroup()
                                                                .addGroup(jPanel2Layout.createSequentialGroup()
                                                                        .addComponent(cbBerichte)
                                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(txtBerichte, GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE))
                                                                .addComponent(cbGeringeVorraete))
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                                .addComponent(spinBerichteMonate, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(spinVorratProzent, GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)))
                                                .addGroup(jPanel2Layout.createSequentialGroup()
                                                        .addGroup(jPanel2Layout.createParallelGroup()
                                                                .addComponent(cbInko)
                                                                .addComponent(cbBeschwerden))
                                                        .addGap(139, 139, 139)
                                                        .addComponent(spinBeschwerdenMonate, GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE))
                                                .addGroup(jPanel2Layout.createSequentialGroup()
                                                        .addGap(25, 25, 25)
                                                        .addComponent(cmbTags, 0, 376, Short.MAX_VALUE)))
                                        .addContainerGap())
                );
                jPanel2Layout.setVerticalGroup(
                        jPanel2Layout.createParallelGroup()
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(cbBVAktivitaet)
                                                .addComponent(spinBVWochen, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(cbNichtAbgehakteBHPs)
                                                .addComponent(spinBHPTage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(cbPlanung)
                                                .addComponent(spinPlanungenTage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cbVerordnungenOhneAnbruch)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup()
                                                .addComponent(spinVorratProzent, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(cbGeringeVorraete))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup()
                                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(cbBerichte)
                                                        .addComponent(txtBerichte, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                                .addComponent(spinBerichteMonate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cmbTags, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                .addGroup(jPanel2Layout.createSequentialGroup()
                                                        .addComponent(cbInko)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(cbBeschwerden))
                                                .addComponent(spinBeschwerdenMonate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addContainerGap())
                );
            }

            //======== jPanel3 ========
            {
                jPanel3.setBorder(new TitledBorder("Medizinisch"));

                //---- cbBilanz ----
                cbBilanz.setText("Ein- und Ausfuhr / Bilanzen");
                cbBilanz.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cbBilanzActionPerformed(e);
                    }
                });

                //---- cbGewicht ----
                cbGewicht.setText("Gewichtsstatistik / BMI");
                cbGewicht.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cbGewichtActionPerformed(e);
                    }
                });

                //---- spinGewichtMonate ----
                spinGewichtMonate.setToolTipText("In den letzten n Monaten");
                spinGewichtMonate.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        spinGewichtMonateStateChanged(e);
                    }
                });

                //---- cmbBilanzMonat ----
                cmbBilanzMonat.setModel(new DefaultComboBoxModel(new String[]{
                        "Item 1",
                        "Item 2",
                        "Item 3",
                        "Item 4"
                }));
                cmbBilanzMonat.setToolTipText("Auswertung ab Monat");

                //---- cbMediControl ----
                cbMediControl.setText("Medikamenten Pr\u00fcfliste");
                cbMediControl.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cbMediControlActionPerformed(e);
                    }
                });

                //---- cmbStation ----
                cmbStation.setModel(new DefaultComboBoxModel(new String[]{
                        "Item 1",
                        "Item 2",
                        "Item 3",
                        "Item 4"
                }));
                cmbStation.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        cmbStationItemStateChanged(e);
                    }
                });

                //---- cbWunden ----
                cbWunden.setText("Wund-Berichte / Doku");
                cbWunden.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cbWundenActionPerformed(e);
                    }
                });

                //---- spinWundenMonate ----
                spinWundenMonate.setToolTipText("In den letzten n Monaten");
                spinWundenMonate.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        spinWundenMonateStateChanged(e);
                    }
                });

                GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
                jPanel3.setLayout(jPanel3Layout);
                jPanel3Layout.setHorizontalGroup(
                        jPanel3Layout.createParallelGroup()
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(jPanel3Layout.createParallelGroup()
                                                .addGroup(jPanel3Layout.createSequentialGroup()
                                                        .addComponent(cbGewicht)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 131, Short.MAX_VALUE)
                                                        .addComponent(spinGewichtMonate, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE))
                                                .addGroup(jPanel3Layout.createSequentialGroup()
                                                        .addGroup(jPanel3Layout.createParallelGroup()
                                                                .addComponent(cbBilanz)
                                                                .addComponent(cbMediControl, GroupLayout.PREFERRED_SIZE, 196, GroupLayout.PREFERRED_SIZE))
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                        .addGroup(jPanel3Layout.createParallelGroup()
                                                                .addComponent(cmbStation, 0, 133, Short.MAX_VALUE)
                                                                .addComponent(cmbBilanzMonat, 0, 133, Short.MAX_VALUE)))
                                                .addGroup(jPanel3Layout.createSequentialGroup()
                                                        .addComponent(cbWunden)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 132, Short.MAX_VALUE)
                                                        .addComponent(spinWundenMonate, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)))
                                        .addContainerGap())
                );
                jPanel3Layout.setVerticalGroup(
                        jPanel3Layout.createParallelGroup()
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                .addComponent(cbBilanz)
                                                .addComponent(cmbBilanzMonat, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(cbGewicht)
                                                .addComponent(spinGewichtMonate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel3Layout.createParallelGroup()
                                                .addGroup(jPanel3Layout.createSequentialGroup()
                                                        .addGap(2, 2, 2)
                                                        .addComponent(cmbStation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                                .addGroup(jPanel3Layout.createSequentialGroup()
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(cbMediControl)))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel3Layout.createParallelGroup()
                                                .addComponent(cbWunden)
                                                .addComponent(spinWundenMonate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addContainerGap(197, Short.MAX_VALUE))
                );
            }

            //======== jPanel5 ========
            {
                jPanel5.setBorder(new TitledBorder(new TitledBorder("Sozialer Dienst"), ""));

                //---- cbSozialBerichte ----
                cbSozialBerichte.setText("Berichte Sozialer Dienst");
                cbSozialBerichte.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cbSozialBerichteActionPerformed(e);
                    }
                });

                //---- spinSozialMonate ----
                spinSozialMonate.setToolTipText("In den letzten n Monaten");
                spinSozialMonate.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        spinSozialWochenStateChanged(e);
                    }
                });

                //---- cbSozialZeiten ----
                cbSozialZeiten.setText("Zeiten Sozialer Dienst");
                cbSozialZeiten.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cbSozialZeitenActionPerformed(e);
                    }
                });

                //---- cmbPEAMonat ----
                cmbPEAMonat.setModel(new DefaultComboBoxModel(new String[]{
                        "Item 1",
                        "Item 2",
                        "Item 3",
                        "Item 4"
                }));
                cmbPEAMonat.setToolTipText("Auswertung ab Monat");

                GroupLayout jPanel5Layout = new GroupLayout(jPanel5);
                jPanel5.setLayout(jPanel5Layout);
                jPanel5Layout.setHorizontalGroup(
                        jPanel5Layout.createParallelGroup()
                                .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(jPanel5Layout.createParallelGroup()
                                                .addGroup(jPanel5Layout.createSequentialGroup()
                                                        .addComponent(cbSozialBerichte)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 202, Short.MAX_VALUE)
                                                        .addComponent(spinSozialMonate, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE))
                                                .addGroup(jPanel5Layout.createSequentialGroup()
                                                        .addComponent(cbSozialZeiten)
                                                        .addGap(54, 54, 54)
                                                        .addComponent(cmbPEAMonat, 0, 211, Short.MAX_VALUE)))
                                        .addContainerGap())
                );
                jPanel5Layout.setVerticalGroup(
                        jPanel5Layout.createParallelGroup()
                                .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(cbSozialBerichte)
                                                .addComponent(spinSozialMonate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel5Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(cbSozialZeiten)
                                                .addComponent(cmbPEAMonat, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addContainerGap(52, Short.MAX_VALUE))
                );
            }

            //======== jPanel6 ========
            {
                jPanel6.setBorder(new TitledBorder("Statistik"));

                //---- cbSturzAnonym ----
                cbSturzAnonym.setText("Sturzstatistik anonym");
                cbSturzAnonym.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cbSturzAnonymActionPerformed(e);
                    }
                });

                //---- spinSturzAMonate ----
                spinSturzAMonate.setToolTipText("In den letzten n Monaten");
                spinSturzAMonate.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        spinSturzAMonateStateChanged(e);
                    }
                });

                //---- cbSturz ----
                cbSturz.setText("Sturzstatistik BW bezogen");
                cbSturz.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cbSturzActionPerformed(e);
                    }
                });

                //---- spinSturzMonate ----
                spinSturzMonate.setToolTipText("In den letzten n Monaten");
                spinSturzMonate.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        spinSturzMonateStateChanged(e);
                    }
                });

                GroupLayout jPanel6Layout = new GroupLayout(jPanel6);
                jPanel6.setLayout(jPanel6Layout);
                jPanel6Layout.setHorizontalGroup(
                        jPanel6Layout.createParallelGroup()
                                .addGroup(jPanel6Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(jPanel6Layout.createParallelGroup()
                                                .addGroup(jPanel6Layout.createSequentialGroup()
                                                        .addComponent(cbSturzAnonym)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 135, Short.MAX_VALUE)
                                                        .addComponent(spinSturzAMonate, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))
                                                .addGroup(jPanel6Layout.createSequentialGroup()
                                                        .addComponent(cbSturz)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 109, Short.MAX_VALUE)
                                                        .addComponent(spinSturzMonate, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)))
                                        .addContainerGap())
                );
                jPanel6Layout.setVerticalGroup(
                        jPanel6Layout.createParallelGroup()
                                .addGroup(jPanel6Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(cbSturzAnonym)
                                                .addComponent(spinSturzAMonate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(cbSturz)
                                                .addComponent(spinSturzMonate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addContainerGap(38, Short.MAX_VALUE))
                );
            }

            GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                    jPanel1Layout.createParallelGroup()
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                            .addComponent(jPanel5, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jPanel2, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel1Layout.createParallelGroup()
                                            .addComponent(jPanel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addContainerGap())
            );
            jPanel1Layout.setVerticalGroup(
                    jPanel1Layout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel1Layout.createParallelGroup()
                                            .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel1Layout.createParallelGroup()
                                            .addComponent(jPanel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jPanel5, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addContainerGap())
            );
            jPanel1Layout.linkSize(SwingConstants.VERTICAL, new Component[]{jPanel2, jPanel3});
        }

        //---- jLabel1 ----
        jLabel1.setFont(new Font("Dialog", Font.BOLD, 18));
        jLabel1.setText("Controlling");

        //======== jToolBar1 ========
        {
            jToolBar1.setFloatable(false);
            jToolBar1.setRollover(true);

            //---- btnPrint ----
            btnPrint.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/fileprint.png")));
            btnPrint.setText("Drucken");
            btnPrint.setFocusable(false);
            btnPrint.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnPrintActionPerformed(e);
                }
            });
            jToolBar1.add(btnPrint);
        }

        //---- pbPart ----
        pbPart.setForeground(new Color(255, 102, 102));

        //---- lblProgress ----
        lblProgress.setHorizontalAlignment(SwingConstants.RIGHT);
        lblProgress.setText(" ");

        //---- btnStop ----
        btnStop.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
        btnStop.setText("STOP");
        btnStop.setEnabled(false);
        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnStopActionPerformed(e);
            }
        });

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addComponent(jToolBar1, GroupLayout.DEFAULT_SIZE, 876, Short.MAX_VALUE)
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(pbPart, GroupLayout.DEFAULT_SIZE, 770, Short.MAX_VALUE)
                                        .addComponent(pbMain, GroupLayout.DEFAULT_SIZE, 770, Short.MAX_VALUE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnStop)
                                .addContainerGap())
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(lblProgress, GroupLayout.DEFAULT_SIZE, 732, Short.MAX_VALUE)
                                .addContainerGap())
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addComponent(jToolBar1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(lblProgress))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                .addComponent(pbMain, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(pbPart, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(btnStop, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        setSize(878, 697);
        setLocationRelativeTo(null);

        //======== jPanel4 ========
        {

            GroupLayout jPanel4Layout = new GroupLayout(jPanel4);
            jPanel4.setLayout(jPanel4Layout);
            jPanel4Layout.setHorizontalGroup(
                    jPanel4Layout.createParallelGroup()
                            .addGap(0, 100, Short.MAX_VALUE)
            );
            jPanel4Layout.setVerticalGroup(
                    jPanel4Layout.createParallelGroup()
                            .addGap(0, 100, Short.MAX_VALUE)
            );
        }
    }// </editor-fold>//GEN-END:initComponents

    private void cbBVAktivitaetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbBVAktivitaetActionPerformed
        SYSPropsTools.storeState(this.getClass().getName() + "::cbBVAktivitaet", cbBVAktivitaet);
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
//                    String result = SYSTools.htmlUmlautConversion(SYSTools.catchNull(get()));
                    if (!SYSTools.catchNull(get).isEmpty()) {
                        SYSPrint.print(parent, get, false);
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
        SYSPropsTools.storeState(this.getClass().getName() + "::cbSozialBerichte", cbSozialBerichte);
        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
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
        spinSozialMonate.setToolTipText("In den letzten " + spinSozialMonate.getValue().toString() + " Monaten");
        SYSPropsTools.storeProp(this.getClass().getName() + "::spinSozialMonate", spinSozialMonate.getValue().toString(), OPDE.getLogin().getUser());
        //SYSTools.putProps(this.getClass().getName() + "::spinSozialWochen", spinBHPTage.getValue().toString(), false, "");
    }//GEN-LAST:event_spinSozialWochenStateChanged

    private void spinGewichtMonateStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinGewichtMonateStateChanged
        spinGewichtMonate.setToolTipText("In den letzten " + spinGewichtMonate.getValue().toString() + " Monate");
        SYSPropsTools.storeProp(this.getClass().getName() + "::spinGewichtMonate", spinGewichtMonate.getValue().toString(), OPDE.getLogin().getUser());
        //SYSTools.putProps(this.getClass().getName() + "::spinGewichtMonate", spinGewichtMonate.getValue().toString(), false, "");
    }//GEN-LAST:event_spinGewichtMonateStateChanged

    private void cbNichtAbgehakteBHPsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbNichtAbgehakteBHPsActionPerformed
        SYSPropsTools.storeState(this.getClass().getName() + "::cbNichtAbgehakteBHPs", cbNichtAbgehakteBHPs);
        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
    }//GEN-LAST:event_cbNichtAbgehakteBHPsActionPerformed

    private void cbPlanungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbPlanungActionPerformed
        SYSPropsTools.storeState(this.getClass().getName() + "::cbPlanung", cbPlanung);
        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
    }//GEN-LAST:event_cbPlanungActionPerformed

    private void cbVerordnungenOhneAnbruchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbVerordnungenOhneAnbruchActionPerformed
        SYSPropsTools.storeState(this.getClass().getName() + "::cbVerordnungenOhneAnbruch", cbVerordnungenOhneAnbruch);
        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
    }//GEN-LAST:event_cbVerordnungenOhneAnbruchActionPerformed

    private void cbBilanzActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbBilanzActionPerformed
        SYSPropsTools.storeState(this.getClass().getName() + "::cbBilanz", cbBilanz);
        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
    }//GEN-LAST:event_cbBilanzActionPerformed

    private void cbGewichtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbGewichtActionPerformed
        SYSPropsTools.storeState(this.getClass().getName() + "::cbGewicht", cbGewicht);
        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
    }//GEN-LAST:event_cbGewichtActionPerformed

    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed
        isCancelled = true;
        o[2] = isCancelled;
    }//GEN-LAST:event_btnStopActionPerformed

    private void cbSozialZeitenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSozialZeitenActionPerformed
        SYSPropsTools.storeState(this.getClass().getName() + "::cbSozialZeiten", cbSozialZeiten);
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
        SYSPropsTools.storeState(this.getClass().getName() + "::cbGeringeVorraete", cbGeringeVorraete);
        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
    }//GEN-LAST:event_cbGeringeVorraeteActionPerformed

    private void cbMediControlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbMediControlActionPerformed
        SYSPropsTools.storeState(this.getClass().getName() + "::cbMediControl", cbMediControl);
        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
    }//GEN-LAST:event_cbMediControlActionPerformed

    private void cmbStationItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbStationItemStateChanged
        SYSPropsTools.storeState(this.getClass().getName() + "::cmbStation", cmbStation);
    }//GEN-LAST:event_cmbStationItemStateChanged

    private void spinSturzAMonateStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinSturzAMonateStateChanged
        spinSturzAMonate.setToolTipText("In den letzten " + spinSturzAMonate.getValue().toString() + " Monaten");
        SYSPropsTools.storeProp(this.getClass().getName() + "::spinSturzAMonate", spinSturzAMonate.getValue().toString(), OPDE.getLogin().getUser());
        //SYSTools.putProps(this.getClass().getName() + "::spinSturzAMonate", spinSturzAMonate.getValue().toString(), false, "");
    }//GEN-LAST:event_spinSturzAMonateStateChanged

    private void cbSturzAnonymActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSturzAnonymActionPerformed
        SYSPropsTools.storeState(this.getClass().getName() + "::cbSturzAnonym", cbSturzAnonym);
        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
    }//GEN-LAST:event_cbSturzAnonymActionPerformed

    private void spinSturzMonateStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinSturzMonateStateChanged
        spinSturzMonate.setToolTipText("In den letzten " + spinSturzMonate.getValue().toString() + " Monaten");
        SYSPropsTools.storeProp(this.getClass().getName() + "::spinSturzMonate", spinSturzMonate.getValue().toString(), OPDE.getLogin().getUser());
        //SYSTools.putProps(this.getClass().getName() + "::spinSturzMonate", spinSturzMonate.getValue().toString(), false, "");
    }//GEN-LAST:event_spinSturzMonateStateChanged

    private void cbSturzActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSturzActionPerformed
        SYSPropsTools.storeState(this.getClass().getName() + "::cbSturz", cbSturz);
        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
    }//GEN-LAST:event_cbSturzActionPerformed

    private void cbBerichteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbBerichteActionPerformed
        SYSPropsTools.storeState(this.getClass().getName() + "::cbBerichte", cbBerichte);
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
        SYSPropsTools.storeState(this.getClass().getName() + "::cbWunden", cbWunden);
        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
    }//GEN-LAST:event_cbWundenActionPerformed

    private void spinWundenMonateStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinWundenMonateStateChanged
        SYSPropsTools.storeProp(this.getClass().getName() + "::spinWundenMonate", spinWundenMonate.getValue().toString(), OPDE.getLogin().getUser());
        //SYSTools.putProps(this.getClass().getName() + "::spinWundenMonate", spinWundenMonate.getValue().toString(), false, "");
    }//GEN-LAST:event_spinWundenMonateStateChanged

    private void cbInkoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbInkoActionPerformed
        SYSPropsTools.storeState(this.getClass().getName() + "::cbInko", cbInko);
        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
    }//GEN-LAST:event_cbInkoActionPerformed

    private void cbBeschwerdenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbBeschwerdenActionPerformed
        SYSPropsTools.storeState(this.getClass().getName() + "::cbBeschwerden", cbBeschwerden);
        btnPrint.setEnabled(anzahlGewuenschterAuswertungen() > 0);
    }//GEN-LAST:event_cbBeschwerdenActionPerformed

    private void spinBeschwerdenMonateStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinBeschwerdenMonateStateChanged
        SYSPropsTools.storeProp(this.getClass().getName() + "::spinBeschwerdenMonate", spinBeschwerdenMonate.getValue().toString(), OPDE.getLogin().getUser());
        //SYSTools.putProps(this.getClass().getName() + "::spinBeschwerdenMonate", spinBeschwerdenMonate.getValue().toString(), false, "");
    }//GEN-LAST:event_spinBeschwerdenMonateStateChanged

    private void cmbTagsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbTagsItemStateChanged
        SYSPropsTools.storeState(this.getClass().getName() + "::cmbTags", cmbTags);
    }//GEN-LAST:event_cmbTagsItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JCheckBox cbBVAktivitaet;
    private JCheckBox cbNichtAbgehakteBHPs;
    private JCheckBox cbPlanung;
    private JCheckBox cbVerordnungenOhneAnbruch;
    private JSpinner spinBHPTage;
    private JSpinner spinBVWochen;
    private JSpinner spinPlanungenTage;
    private JCheckBox cbGeringeVorraete;
    private JSpinner spinVorratProzent;
    private JCheckBox cbBerichte;
    private JTextField txtBerichte;
    private JSpinner spinBerichteMonate;
    private JCheckBox cbInko;
    private JCheckBox cbBeschwerden;
    private JSpinner spinBeschwerdenMonate;
    private JComboBox cmbTags;
    private JPanel jPanel3;
    private JCheckBox cbBilanz;
    private JCheckBox cbGewicht;
    private JSpinner spinGewichtMonate;
    private JComboBox cmbBilanzMonat;
    private JCheckBox cbMediControl;
    private JComboBox cmbStation;
    private JCheckBox cbWunden;
    private JSpinner spinWundenMonate;
    private JPanel jPanel5;
    private JCheckBox cbSozialBerichte;
    private JSpinner spinSozialMonate;
    private JCheckBox cbSozialZeiten;
    private JComboBox cmbPEAMonat;
    private JPanel jPanel6;
    private JCheckBox cbSturzAnonym;
    private JSpinner spinSturzAMonate;
    private JCheckBox cbSturz;
    private JSpinner spinSturzMonate;
    private JLabel jLabel1;
    private JToolBar jToolBar1;
    private JButton btnPrint;
    private JProgressBar pbMain;
    private JProgressBar pbPart;
    private JLabel lblProgress;
    private JButton btnStop;
    private JPanel jPanel4;
    // End of variables declaration//GEN-END:variables
}
