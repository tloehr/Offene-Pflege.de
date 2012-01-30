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

package op.care.med.vorrat;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.Bewohner;
import entity.system.SYSPropsTools;
import entity.verordnungen.*;
import op.OPDE;
import op.care.med.PnlProdAssistant;
import op.tools.SYSPrint;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.JXSearchField;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.TimelinePropertyBuilder;
import org.pushingpixels.trident.TridentConfig;
import org.pushingpixels.trident.interpolator.PropertyInterpolator;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * @author tloehr
 */
public class DlgBestand extends javax.swing.JDialog {
    private boolean ignoreEvent;
    private Bewohner bewohner;
    private Component parent;
    private String template;
    //    private Darreichung darreichung = null;
    private boolean medEingegeben = false;
    private boolean mengeEingegeben = false;
    private boolean bwEingegeben = false;
    private boolean packungEingegeben = false;
    private BigDecimal menge;
    private boolean flashVorrat = false;
    private Thread thread = null;
    JDialog myMedAssistantDialog = null;
    private SwingWorker worker;
    private Timeline timeline;
    private String caretsDuringProcessing;

    public DlgBestand(JFrame parent) {
        this(parent, null, "");
    }

    public DlgBestand(JFrame parent, Bewohner bewohner, String template) {
        super(parent, true);
        this.parent = parent;
        this.bewohner = bewohner;
        this.template = template;
        worker = null;
        timeline = null;
        initDialog();
        pack();
    }

    private void txtMedSucheActionPerformed(ActionEvent evt) {
        if (ignoreEvent || (worker != null && !worker.isDone())) {
            return;
        }

        txtMenge.setText("");
        lblHardDisk.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/hdd_unmount.png")));
        medEingegeben = false;
        if (txtMedSuche.getText().trim().isEmpty()) {
            cmbMProdukt.setModel(new DefaultComboBoxModel());
            cmbPackung.setModel(new DefaultComboBoxModel());
            packungEingegeben = false;
        } else {
            String pzn = MedPackungTools.parsePZN(txtMedSuche.getText());
            if (pzn != null) { // Hier sucht man nach einer PZN. Im Barcode ist das führende 'ß' enthalten.
                EntityManager em = OPDE.createEM();
                Query query = em.createNamedQuery("MedPackung.findByPzn");
                query.setParameter("pzn", pzn);
                try {
                    MedPackung pznsuche = (MedPackung) query.getSingleResult();
                    cmbMProdukt.setModel(new DefaultComboBoxModel(new Darreichung[]{pznsuche.getDarreichung()}));
                    cmbMProdukt.setSelectedItem(0);
                    cmbMProduktItemStateChanged(null);
                } catch (NoResultException nre) {
                    cmbMProdukt.setModel(new DefaultComboBoxModel());
                    OPDE.debug(nre);
                } catch (Exception e) {
                    OPDE.fatal(e);
                } finally {
                    em.close();
                }
            } else { // Falls die Suche NICHT nur aus Zahlen besteht, dann nach Namen suchen.

                if (timeline == null) {
                    timeline = new Timeline(lblHardDisk);
                    ImageIcon hd1 = new ImageIcon(getClass().getResource("/artwork/32x32/hdd_mount.png"));
                    ImageIcon hd2 = new ImageIcon(getClass().getResource("/artwork/32x32/hdd_unmount.png"));

                    TimelinePropertyBuilder.PropertySetter<ImageIcon> propertySetter = new TimelinePropertyBuilder.PropertySetter<ImageIcon>() {
                        @Override
                        public void set(Object obj, String fieldName, ImageIcon value) {
                            lblHardDisk.setIcon(value);
                        }
                    };

                    PropertyInterpolator<ImageIcon> iconInterpolator = new PropertyInterpolator<ImageIcon>() {
                        @Override
                        public Class getBasePropertyClass() {
                            return ImageIcon.class;
                        }

                        @Override
                        public ImageIcon interpolate(ImageIcon imageIcon, ImageIcon imageIcon1, float timelinePosition) {
                            ImageIcon result = null;

                            if (timelinePosition > 0.5f) {
                                result = imageIcon;
                            } else {
                                result = imageIcon1;
                            }
                            return result;
                        }
                    };

                    TridentConfig.getInstance().addPropertyInterpolator(iconInterpolator);
                    timeline.addPropertyToInterpolate(Timeline.<ImageIcon>property("value").from(hd1).to(hd2).setWith(propertySetter));
                    timeline.setDuration(450);

                    timeline.playLoop(Timeline.RepeatBehavior.REVERSE);
                }

                worker = new SwingWorker() {
                    List<Darreichung> list;

                    @Override
                    protected Object doInBackground() throws Exception {
                        list = DarreichungTools.findDarreichungByMedProduktText(txtMedSuche.getText());
                        return null;
                    }

                    @Override
                    protected void done() {
                        cmbMProdukt.setModel(new DefaultComboBoxModel(list.toArray()));
                        cmbMProduktItemStateChanged(null);

                        if (timeline != null) {
                            timeline.cancel();
                            timeline = null;
                        }
                    }
                };
                worker.execute();
            }
        }
        setApply();
    }

//    public DlgBestand(JFrame parent, Bewohner bewohner, Darreichung darreichung) {
//        super(parent, true);
//        this.parent = parent;
//        this.bewohner = bewohner;
//        this.template = null;
//        this.darreichung = darreichung;
//        initDialog();
//    }
//
//    public DlgBestand(JDialog parent, Bewohner bewohner, String template) {
//        super(parent, true);
//        this.parent = parent;
//        this.bewohner = bewohner;
//        this.template = template;
//        initDialog();
//    }
//
//    public DlgBestand(JDialog parent, Bewohner bewohner, Darreichung darreichung) {
//        super(parent, true);
//        this.parent = parent;
//        this.bewohner = bewohner;
//        this.template = null;
//        this.darreichung = darreichung;
//        initDialog();
//    }

    private void initDialog() {
        ignoreEvent = true;
        initComponents();
        menge = null;
        cmbMProdukt.setRenderer(DarreichungTools.getDarreichungRenderer(DarreichungTools.LONG));
        if (bewohner != null) {
//            txtBWSuche.setText(bewohner.getBWKennung());
            ignoreEvent = false;
//            txtBWSucheCaretUpdate(null);
            txtBWSuche.setEnabled(false);
            bwEingegeben = true;
            cmbBW.setModel(new DefaultComboBoxModel(new Bewohner[]{bewohner}));
        }
        if (!template.isEmpty()) {
            txtMedSuche.setText(template);
        }
//        if (darreichung != null) {
//            cmbMProdukt.setModel(new DefaultComboBoxModel(new Darreichung[]{darreichung}));
//            cmbMProduktItemStateChanged(null);
//            txtMedSuche.setEnabled(false);
//        }
        String name = this.getClass().getName() + "::cbDruck";
        if (OPDE.getProps().containsKey(name)) {
            cbDruck.setSelected(OPDE.getProps().getProperty(name).equalsIgnoreCase("true"));
        } else {
            cbDruck.setSelected(false);
        }

        ignoreEvent = false;
//        if (!txtMedSuche.getText().equals("")) {
//            txt
//        }

        SYSTools.centerOnParent(parent, this);
        setTitle(SYSTools.getWindowTitle("Medikamente buchen"));
        setVisible(true);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Erzeugter Quelltext ">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblFrage = new JLabel();
        jSeparator1 = new JSeparator();
        jLabel1 = new JLabel();
        panel2 = new JPanel();
        lblHardDisk = new JLabel();
        txtMedSuche = new JXSearchField();
        btnMed = new JButton();
        jLabel3 = new JLabel();
        cmbMProdukt = new JComboBox();
        lblVorrat = new JLabel();
        cmbVorrat = new JComboBox();
        jLabel4 = new JLabel();
        txtBWSuche = new JTextField();
        cmbBW = new JComboBox();
        lblMenge = new JLabel();
        txtMenge = new JTextField();
        jLabel6 = new JLabel();
        cmbPackung = new JComboBox();
        jSeparator2 = new JSeparator();
        jLabel7 = new JLabel();
        txtBemerkung = new JTextField();
        cbDruck = new JCheckBox();
        jLabel12 = new JLabel();
        panel1 = new JPanel();
        btnApply = new JButton();
        btnClose = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Medikamente einbuchen");
        setMinimumSize(new Dimension(640, 425));
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "$rgap, $lcgap, default, $lcgap, 28dlu, $lcgap, default, $lcgap, default:grow, $lcgap, $rgap",
            "$rgap, $lgap, fill:default, $lgap, default, 8*($lgap, fill:default), $lgap, fill:default:grow, $lgap, fill:default, $lgap, $rgap"));

        //---- lblFrage ----
        lblFrage.setFont(new Font("Dialog", Font.BOLD, 24));
        lblFrage.setText("Med.-Best\u00e4nde buchen");
        contentPane.add(lblFrage, CC.xywh(3, 3, 8, 1));
        contentPane.add(jSeparator1, CC.xywh(3, 5, 8, 1));

        //---- jLabel1 ----
        jLabel1.setText("PZN oder Suchbegriff:");
        jLabel1.setFont(new Font("sansserif", Font.PLAIN, 14));
        contentPane.add(jLabel1, CC.xy(3, 7));

        //======== panel2 ========
        {
            panel2.setLayout(new BoxLayout(panel2, BoxLayout.LINE_AXIS));

            //---- lblHardDisk ----
            lblHardDisk.setFont(new Font("sansserif", Font.PLAIN, 16));
            lblHardDisk.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/hdd_unmount.png")));
            panel2.add(lblHardDisk);

            //---- txtMedSuche ----
            txtMedSuche.setFont(new Font("sansserif", Font.PLAIN, 14));
            txtMedSuche.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    txtMedSucheActionPerformed(e);
                }
            });
            panel2.add(txtMedSuche);

            //---- btnMed ----
            btnMed.setBackground(Color.white);
            btnMed.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
            btnMed.setToolTipText("Medikamente bearbeiten");
            btnMed.setBorder(null);
            btnMed.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnMedActionPerformed(e);
                }
            });
            panel2.add(btnMed);
        }
        contentPane.add(panel2, CC.xywh(5, 7, 6, 1));

        //---- jLabel3 ----
        jLabel3.setText("Produkt:");
        jLabel3.setFont(new Font("sansserif", Font.PLAIN, 14));
        contentPane.add(jLabel3, CC.xy(3, 9));

        //---- cmbMProdukt ----
        cmbMProdukt.setModel(new DefaultComboBoxModel(new String[] {

        }));
        cmbMProdukt.setFont(new Font("sansserif", Font.PLAIN, 14));
        cmbMProdukt.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbMProduktItemStateChanged(e);
            }
        });
        contentPane.add(cmbMProdukt, CC.xywh(5, 9, 6, 1));

        //---- lblVorrat ----
        lblVorrat.setText("vorhandene Vorr\u00e4te:");
        lblVorrat.setFont(new Font("sansserif", Font.PLAIN, 14));
        contentPane.add(lblVorrat, CC.xy(3, 15));

        //---- cmbVorrat ----
        cmbVorrat.setModel(new DefaultComboBoxModel(new String[] {

        }));
        cmbVorrat.setFont(new Font("sansserif", Font.PLAIN, 14));
        cmbVorrat.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                cmbVorratMouseEntered(e);
            }
        });
        contentPane.add(cmbVorrat, CC.xywh(5, 15, 6, 1));

        //---- jLabel4 ----
        jLabel4.setText("Zuordnung zu Bewohner:");
        jLabel4.setFont(new Font("sansserif", Font.PLAIN, 14));
        contentPane.add(jLabel4, CC.xy(3, 19));

        //---- txtBWSuche ----
        txtBWSuche.setFont(new Font("sansserif", Font.PLAIN, 14));
        txtBWSuche.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                txtBWSucheCaretUpdate(e);
            }
        });
        contentPane.add(txtBWSuche, CC.xy(5, 19));

        //---- cmbBW ----
        cmbBW.setModel(new DefaultComboBoxModel(new String[] {

        }));
        cmbBW.setFont(new Font("sansserif", Font.PLAIN, 14));
        cmbBW.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbBWItemStateChanged(e);
            }
        });
        contentPane.add(cmbBW, CC.xywh(7, 19, 4, 1));

        //---- lblMenge ----
        lblMenge.setText("Buchungsmenge:");
        lblMenge.setFont(new Font("sansserif", Font.PLAIN, 14));
        contentPane.add(lblMenge, CC.xy(3, 13));

        //---- txtMenge ----
        txtMenge.setFont(new Font("sansserif", Font.PLAIN, 14));
        txtMenge.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                txtMengeCaretUpdate(e);
            }
        });
        txtMenge.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtMengeFocusGained(e);
            }
        });
        contentPane.add(txtMenge, CC.xywh(5, 13, 6, 1));

        //---- jLabel6 ----
        jLabel6.setText("Packung:");
        jLabel6.setFont(new Font("sansserif", Font.PLAIN, 14));
        contentPane.add(jLabel6, CC.xy(3, 11));

        //---- cmbPackung ----
        cmbPackung.setModel(new DefaultComboBoxModel(new String[] {

        }));
        cmbPackung.setFont(new Font("sansserif", Font.PLAIN, 14));
        cmbPackung.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbPackungItemStateChanged(e);
            }
        });
        contentPane.add(cmbPackung, CC.xywh(5, 11, 6, 1));
        contentPane.add(jSeparator2, CC.xywh(3, 23, 8, 1));

        //---- jLabel7 ----
        jLabel7.setText("Bemerkung:");
        jLabel7.setFont(new Font("sansserif", Font.PLAIN, 14));
        contentPane.add(jLabel7, CC.xy(3, 17));

        //---- txtBemerkung ----
        txtBemerkung.setFont(new Font("sansserif", Font.PLAIN, 14));
        txtBemerkung.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                txtBemerkungCaretUpdate(e);
            }
        });
        contentPane.add(txtBemerkung, CC.xywh(5, 17, 6, 1));

        //---- cbDruck ----
        cbDruck.setText("Belegdruck");
        cbDruck.setBorder(BorderFactory.createEmptyBorder());
        cbDruck.setMargin(new Insets(0, 0, 0, 0));
        cbDruck.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cbDruckItemStateChanged(e);
            }
        });
        contentPane.add(cbDruck, CC.xywh(9, 21, 2, 1, CC.RIGHT, CC.DEFAULT));

        //---- jLabel12 ----
        jLabel12.setText("<html>Hinweis: &frac14; = 0,25 | <sup>1</sup>/<sub>3</sub> = 0,33 | &frac12; = 0,5 | &frac34; = 0,75</html>");
        contentPane.add(jLabel12, CC.xywh(3, 21, 5, 1));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.LINE_AXIS));

            //---- btnApply ----
            btnApply.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnApply.setText("Buchen");
            btnApply.setEnabled(false);
            btnApply.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnApplyActionPerformed(e);
                }
            });
            panel1.add(btnApply);

            //---- btnClose ----
            btnClose.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
            btnClose.setText("Schlie\u00dfen");
            btnClose.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnCloseActionPerformed(e);
                }
            });
            panel1.add(btnClose);
        }
        contentPane.add(panel1, CC.xywh(9, 25, 2, 1, CC.RIGHT, CC.DEFAULT));
        pack();
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    private void txtMengeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMengeFocusGained
        SYSTools.markAllTxt((JTextField) evt.getSource());
    }//GEN-LAST:event_txtMengeFocusGained

    private void cbDruckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbDruckItemStateChanged
        if (ignoreEvent) {
            return;
        }
        SYSPropsTools.storeState(this.getClass().getName() + "::cbDruck", cbDruck);
    }//GEN-LAST:event_cbDruckItemStateChanged

    private void cmbVorratMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmbVorratMouseEntered
        if (thread != null && flashVorrat) {
            thread.interrupt();
        }
    }//GEN-LAST:event_cmbVorratMouseEntered

    private void txtBemerkungCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtBemerkungCaretUpdate
        setApply();
    }//GEN-LAST:event_txtBemerkungCaretUpdate

    private void txtMedSucheFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMedSucheFocusGained
        SYSTools.markAllTxt(txtMedSuche);
    }//GEN-LAST:event_txtMedSucheFocusGained

    private void cmbBWItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbBWItemStateChanged
        bewohner = (Bewohner) cmbBW.getSelectedItem();
        bwEingegeben = bewohner != null;
        if (medEingegeben) { // Vorrat erneut ermitteln
            initCmbVorrat((Darreichung) cmbMProdukt.getSelectedItem());
        }
        setApply();
    }//GEN-LAST:event_cmbBWItemStateChanged

    private void btnApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyActionPerformed
        if (thread != null && flashVorrat) {
            thread.interrupt();
        }
        save();
        if (template != null && !template.equals("")) {
            dispose();
        } else {
            txtMenge.setText("0");
            txtBemerkung.setText("");
            txtMedSuche.setText("");
            txtMedSuche.requestFocus();
            btnApply.setEnabled(false);
        }
    }//GEN-LAST:event_btnApplyActionPerformed

    private void save() {
        EntityManager em = OPDE.createEM();

        try {
            em.getTransaction().begin();

            // Wenn die packung null ist, dann ist eine Sonderpackung
            MedPackung packung = null;
            if (cmbPackung.getSelectedItem() instanceof MedPackung) {
                packung = (MedPackung) cmbPackung.getSelectedItem();
                if (menge == null) {
                    menge = packung.getInhalt();
                }
            }

            Darreichung darreichung = (Darreichung) cmbMProdukt.getSelectedItem();

            MedVorrat vorrat = (MedVorrat) cmbVorrat.getSelectedItem();
            if (vorrat.getVorID() == null) { // neuen Vorrat anlegen
                vorrat.setText(darreichung.getMedProdukt().getBezeichnung());
                em.persist(vorrat);
            }
            MedBestand bestand = MedVorratTools.einbuchenVorrat(em, vorrat, packung, darreichung, txtBemerkung.getText(), menge);

            em.getTransaction().commit();

            if (MedVorratTools.getImAnbruch(vorrat) == null &&
                    JOptionPane.showConfirmDialog(this, "Dieser Vorrat enthält bisher nur verschlossene Packungen.\n" +
                            "Soll die neue Packung direkt als angebrochen markiert werden ?", "Packungs-Anbruch",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                MedVorratTools.anbrechenNaechste(vorrat);
            }

            if (cbDruck.isSelected()) {
                SYSPrint.printLabel(bestand);
            }

        } catch (Exception ex) {
            em.getTransaction().rollback();
            OPDE.fatal(ex);
        } finally {
            em.close();
        }
    }

    private void btnMedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMedActionPerformed
//        ArrayList result = new ArrayList();
//        result.add(txtSuche.getText());

        myMedAssistantDialog = new JDialog(this, "Medikamenten Assistent", true);
        myMedAssistantDialog.setSize(1280, 800);
        PnlProdAssistant myPnl = new PnlProdAssistant(new Closure() {
            @Override
            public void execute(Object o) {
                OPDE.debug(o);
                if (o != null) {
                    if (o instanceof MedPackung) {
                        txtMedSuche.setText(SYSTools.catchNull(((MedPackung) o).getPzn()));
                    } else { // Darreichung
                        cmbMProdukt.setModel(new DefaultComboBoxModel(new Darreichung[]{(Darreichung) o}));
                    }
                }
                myMedAssistantDialog.dispose();
                myMedAssistantDialog = null;
            }
        }, txtMedSuche.getText());
        myMedAssistantDialog.setContentPane(myPnl);
        SYSTools.centerOnParent(this, myMedAssistantDialog);
        myMedAssistantDialog.setVisible(true);
//        txtMedSucheCaretUpdate(null);
    }//GEN-LAST:event_btnMedActionPerformed

    public void dispose() {
        if (thread != null && flashVorrat) {
            thread.interrupt();
        }
        SYSTools.unregisterListeners(this);
        super.dispose();
    }

    private void txtMengeCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtMengeCaretUpdate
        if (txtMenge.getText().trim().isEmpty()) {
            lblMenge.setIcon(null);
            txtMenge.setToolTipText(null);
            menge = null;
            return;
        }

        menge = SYSTools.checkBigDecimal(txtMenge.getText().trim());

        if (menge != null) {
            if (menge.compareTo(BigDecimal.ZERO) <= 0) {
                lblMenge.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/editdelete.png")));
                txtMenge.setToolTipText(SYSTools.toHTML("<i>Mengen müssen größer 0 sein.</i>"));
                menge = null;
            } else if (packungEingegeben && menge.compareTo(((MedPackung) cmbPackung.getSelectedItem()).getInhalt()) > 0) {
                lblMenge.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/editdelete.png")));
                txtMenge.setToolTipText(SYSTools.toHTML("<i>Mengen dürfen nicht größer als der Packungsinhalt sein.</i>"));
                menge = ((MedPackung) cmbPackung.getSelectedItem()).getInhalt();
            } else {
                lblMenge.setIcon(null);
                txtMenge.setToolTipText(null);
            }
        } else {
            lblMenge.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/editdelete.png")));
            txtMenge.setToolTipText(SYSTools.toHTML("<i>Die Mengenangabe ist falsch.</i>"));
        }

        mengeEingegeben = menge != null;

        setApply();
    }//GEN-LAST:event_txtMengeCaretUpdate

    private void txtBWSucheCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtBWSucheCaretUpdate
        if (ignoreEvent || !txtBWSuche.isEnabled()) {
            return;
        }
        if (txtBWSuche.getText().equals("")) {
            cmbBW.setModel(new DefaultComboBoxModel());
            bwEingegeben = false;
        } else {
            DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
            EntityManager em = OPDE.createEM();
            if (txtBWSuche.getText().length() == 3) { // Könnte eine Suche nach der Kennung sein
                Bewohner mybw = em.find(Bewohner.class, txtBWSuche.getText());
                if (mybw != null) {
                    dcbm = new DefaultComboBoxModel(new Bewohner[]{mybw});
                }
            }
            if (dcbm.getSize() == 0) { //Nachname ?

                Query query = em.createQuery(" SELECT b FROM Bewohner b WHERE b.station IS NOT NULL AND b.nachname like :nachname ORDER BY b.nachname, b.vorname ");
                query.setParameter("nachname", txtBWSuche.getText().trim() + "%");
                java.util.List<Bewohner> listbw = query.getResultList();

                dcbm = new DefaultComboBoxModel(listbw.toArray());
            }
            if (dcbm.getSize() > 0) {
                cmbBW.setModel(dcbm);
                cmbBW.setSelectedIndex(0);
                bwEingegeben = true;
                cmbBWItemStateChanged(null);
            } else {
                cmbBW.setModel(new DefaultComboBoxModel());
                bewohner = null;
                bwEingegeben = false;
            }
            em.close();

        }
        setApply();
    }//GEN-LAST:event_txtBWSucheCaretUpdate

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void cmbPackungItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbPackungItemStateChanged
        if (ignoreEvent) {
            return;
        }
        packungEingegeben = cmbPackung.getSelectedItem() != null;
        txtMenge.setText("");
        setApply();
    }//GEN-LAST:event_cmbPackungItemStateChanged

    private void flash() {
        flashVorrat = true;
        thread = new Thread() {
            public void run() {
                try {
                    OPDE.debug("thread");
                    while (flashVorrat) {
                        if (lblVorrat.getForeground() != Color.RED) {
                            lblVorrat.setForeground(Color.RED);
                        } else {
                            lblVorrat.setForeground(Color.WHITE);
                        }
                        Thread.sleep(500);
                    }
                    lblVorrat.setForeground(Color.BLACK);
                    flashVorrat = false;
                } catch (InterruptedException e) {
                    lblVorrat.setForeground(Color.BLACK);
                    flashVorrat = false;
                }
            }
        };
        thread.start();
    }

    private void initCmbVorrat(Darreichung darreichung) {
        boolean foundExactMatch = false;
        cmbVorrat.setRenderer(MedVorratTools.getMedVorratRenderer());
        if (darreichung == null) {
            cmbVorrat.setModel(new DefaultComboBoxModel());
        } else {
            List<MedVorrat> vorraete = new ArrayList<MedVorrat>();
            MedVorrat myVorrat = DarreichungTools.getVorratZurDarreichung(bewohner, darreichung);
            foundExactMatch = myVorrat != null;
            if (!foundExactMatch) {
                vorraete = DarreichungTools.getPassendeVorraeteZurDarreichung(bewohner, darreichung);
            } else {
                vorraete.add(myVorrat);
            }
            cmbVorrat.setModel(new DefaultComboBoxModel(vorraete.toArray()));
        }
        if (!foundExactMatch) {
            DefaultComboBoxModel dcbm = (DefaultComboBoxModel) cmbVorrat.getModel();
            dcbm.insertElementAt(new MedVorrat(bewohner, "<AUTOMATISCH>"), 0);
            cmbVorrat.setSelectedIndex(0);
            if (dcbm.getSize() > 1) {
                cmbVorrat.setToolTipText("<html>Keinen <b>exakt</b> passender Vorrat gefunden. Wählen Sie selbst einen passenden aus <br/>oder verwenden Sie <b>automatisch</b>.<html>");
                cmbVorrat.showPopup();
                flash();
                cmbVorrat.setEnabled(true);
            } else {
                cmbVorrat.setToolTipText("<html><b>automatisch</b> erstellt direkt einen neuen Vorrat. Da brauchen Sie nichts mehr zu ändern.</html>");
                cmbVorrat.setEnabled(false);
            }
        } else {
//            MedVorrat vorrat = (MedVorrat) cmbVorrat.getSelectedItem();
//            if (vorrat != null) {
//                cmbVorrat.setToolTipText("Bestand: " + MedVorratTools.getSumme(vorrat) + " " + MedFormenTools.EINHEIT[darreichung.getMedForm().getPackEinheit()]);
//            }
            cmbVorrat.setEnabled(false);
        }
    }

    private void cmbMProduktItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbMProduktItemStateChanged
        if (ignoreEvent) {
            return;
        }
        medEingegeben = cmbMProdukt.getModel().getSize() > 0;
        if (medEingegeben) {
            Darreichung myDarreichung = (Darreichung) cmbMProdukt.getSelectedItem();
            DefaultComboBoxModel dcbm = new DefaultComboBoxModel(myDarreichung.getPackungen().toArray());
            dcbm.insertElementAt("<Sonderpackung>", 0);
            cmbPackung.setModel(dcbm);
            cmbPackung.setRenderer(MedPackungTools.getMedPackungRenderer());

            if (cmbPackung.getModel().getSize() > 0) {
                cmbPackung.setSelectedIndex(cmbPackung.getModel().getSize() - 1);
                packungEingegeben = true;
//                cmbPackungItemStateChanged(null);
            }
            initCmbVorrat(myDarreichung);
        } else {
            cmbPackung.setModel(new DefaultComboBoxModel());
            packungEingegeben = false;
            initCmbVorrat(null);
        }
//        medEingegeben = cmbMProdukt.getModel().getSize() > 0;
        setApply();
    }//GEN-LAST:event_cmbMProduktItemStateChanged

    private void setApply() {
        boolean txtEntry = true;
        if (cmbPackung.getSelectedIndex() < 0) {
            txtEntry = !txtBemerkung.getText().isEmpty();
        }
        OPDE.debug("setApply(): med:" + medEingegeben);
        OPDE.debug("setApply(): packung:" + packungEingegeben);
        OPDE.debug("setApply(): menge:" + mengeEingegeben);
        OPDE.debug("setApply(): bw:" + bwEingegeben);
        OPDE.debug("setApply(): txt:" + txtEntry);
        btnApply.setEnabled(medEingegeben && (mengeEingegeben || packungEingegeben) && bwEingegeben && txtEntry);
    }



    // Variablendeklaration - nicht modifizieren//GEN-BEGIN:variables
    private JLabel lblFrage;
    private JSeparator jSeparator1;
    private JLabel jLabel1;
    private JPanel panel2;
    private JLabel lblHardDisk;
    private JXSearchField txtMedSuche;
    private JButton btnMed;
    private JLabel jLabel3;
    private JComboBox cmbMProdukt;
    private JLabel lblVorrat;
    private JComboBox cmbVorrat;
    private JLabel jLabel4;
    private JTextField txtBWSuche;
    private JComboBox cmbBW;
    private JLabel lblMenge;
    private JTextField txtMenge;
    private JLabel jLabel6;
    private JComboBox cmbPackung;
    private JSeparator jSeparator2;
    private JLabel jLabel7;
    private JTextField txtBemerkung;
    private JCheckBox cbDruck;
    private JLabel jLabel12;
    private JPanel panel1;
    private JButton btnApply;
    private JButton btnClose;
    // Ende der Variablendeklaration//GEN-END:variables

}
