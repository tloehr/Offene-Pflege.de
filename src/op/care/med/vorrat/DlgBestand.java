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
import op.system.Form;
import op.system.PrinterType;
import op.threads.DisplayMessage;
import op.tools.MyJDialog;
import op.tools.PrintListElement;
import op.tools.SYSTools;
import org.jdesktop.swingx.HorizontalLayout;
import org.jdesktop.swingx.JXSearchField;

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
public class DlgBestand extends MyJDialog {
    private boolean ignoreEvent;
    private Bewohner bewohner;
    private String template;

    private boolean medEingegeben = false;
    private boolean mengeEingegeben = false;
    private boolean bwEingegeben = false;
    private boolean packungEingegeben = false;
    private BigDecimal menge;
    private boolean flashVorrat = false;
    private Thread thread = null;
    JDialog myMedAssistantDialog = null;

    private PrinterType etiprinter;
    private Form form1;

    public DlgBestand() {
        this(null, "");
    }

    public DlgBestand(Bewohner bewohner, String template) {
        super();
        this.bewohner = bewohner;
        this.template = template;
        initComponents();
        initDialog();
    }

    private void txtMedSucheActionPerformed(ActionEvent evt) {
        if (ignoreEvent) {
            return;
        }

        txtMenge.setText("");
        medEingegeben = false;
        if (txtMedSuche.getText().trim().isEmpty()) {
            cmbMProdukt.setModel(new DefaultComboBoxModel());
            cmbPackung.setModel(new DefaultComboBoxModel());
            packungEingegeben = false;
        } else {

            OPDE.getDisplayManager().setDBActionMessage(true);

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

                List<Darreichung> list = DarreichungTools.findDarreichungByMedProduktText(txtMedSuche.getText());
                cmbMProdukt.setModel(new DefaultComboBoxModel(list.toArray()));
                cmbMProduktItemStateChanged(null);

            }


            OPDE.getDisplayManager().setDBActionMessage(false);
        }
        setApply();
    }

    private void btnPrintItemStateChanged(ItemEvent e) {
        if (ignoreEvent) {
            return;
        }
        SYSPropsTools.storeState(this.getClass().getName() + "::btnPrint", btnPrint);
    }

    private void initDialog() {
        ignoreEvent = true;

        etiprinter = OPDE.getPrinters().getPrinters().get(OPDE.getProps().getProperty("etitype1"));
        form1 = etiprinter.getForms().get(OPDE.getProps().getProperty("etiform1"));

        menge = null;
        cmbMProdukt.setRenderer(DarreichungTools.getDarreichungRenderer(DarreichungTools.LONG));
        if (bewohner != null) {

            ignoreEvent = false;

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

        SYSPropsTools.restoreState(this.getClass().getName() + "::btnPrint", btnPrint);

//        if (OPDE.getProps().containsKey(name)) {
//            cbDruck.setSelected(OPDE.getProps().getProperty(name).equalsIgnoreCase("true"));
//        } else {
//            cbDruck.setSelected(false);
//        }

        ignoreEvent = false;
        pack();
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
        jLabel1 = new JLabel();
        panel2 = new JPanel();
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
        jLabel7 = new JLabel();
        txtBemerkung = new JTextField();
        panel1 = new JPanel();
        btnPrint = new JToggleButton();
        btnClose = new JButton();
        btnApply = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Medikamente einbuchen");
        setMinimumSize(new Dimension(640, 425));
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
                "$ugap, $lcgap, default, $lcgap, 39dlu, $lcgap, default, $lcgap, default:grow, $lcgap, $ugap",
                "$ugap, 8*($lgap, fill:default), $lgap, $ugap"));

        //---- jLabel1 ----
        jLabel1.setText("PZN oder Suchbegriff");
        jLabel1.setFont(new Font("sansserif", Font.PLAIN, 14));
        contentPane.add(jLabel1, CC.xy(3, 3));

        //======== panel2 ========
        {
            panel2.setLayout(new BoxLayout(panel2, BoxLayout.LINE_AXIS));

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
        contentPane.add(panel2, CC.xywh(5, 3, 6, 1));

        //---- jLabel3 ----
        jLabel3.setText("Produkt");
        jLabel3.setFont(new Font("sansserif", Font.PLAIN, 14));
        contentPane.add(jLabel3, CC.xy(3, 5));

        //---- cmbMProdukt ----
        cmbMProdukt.setModel(new DefaultComboBoxModel(new String[]{

        }));
        cmbMProdukt.setFont(new Font("sansserif", Font.PLAIN, 14));
        cmbMProdukt.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbMProduktItemStateChanged(e);
            }
        });
        contentPane.add(cmbMProdukt, CC.xywh(5, 5, 6, 1));

        //---- lblVorrat ----
        lblVorrat.setText("vorhandene Vorr\u00e4te:");
        lblVorrat.setFont(new Font("sansserif", Font.PLAIN, 14));
        contentPane.add(lblVorrat, CC.xy(3, 11));

        //---- cmbVorrat ----
        cmbVorrat.setModel(new DefaultComboBoxModel(new String[]{

        }));
        cmbVorrat.setFont(new Font("sansserif", Font.PLAIN, 14));
        cmbVorrat.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                cmbVorratMouseEntered(e);
            }
        });
        contentPane.add(cmbVorrat, CC.xywh(5, 11, 6, 1));

        //---- jLabel4 ----
        jLabel4.setText("Zuordnung zu Bewohner:");
        jLabel4.setFont(new Font("sansserif", Font.PLAIN, 14));
        contentPane.add(jLabel4, CC.xy(3, 15));

        //---- txtBWSuche ----
        txtBWSuche.setFont(new Font("sansserif", Font.PLAIN, 14));
        txtBWSuche.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                txtBWSucheCaretUpdate(e);
            }
        });
        contentPane.add(txtBWSuche, CC.xy(5, 15));

        //---- cmbBW ----
        cmbBW.setModel(new DefaultComboBoxModel(new String[]{

        }));
        cmbBW.setFont(new Font("sansserif", Font.PLAIN, 14));
        cmbBW.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbBWItemStateChanged(e);
            }
        });
        contentPane.add(cmbBW, CC.xywh(7, 15, 4, 1));

        //---- lblMenge ----
        lblMenge.setText("Buchungsmenge");
        lblMenge.setFont(new Font("sansserif", Font.PLAIN, 14));
        contentPane.add(lblMenge, CC.xy(3, 9));

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
        contentPane.add(txtMenge, CC.xywh(5, 9, 6, 1));

        //---- jLabel6 ----
        jLabel6.setText("Packung");
        jLabel6.setFont(new Font("sansserif", Font.PLAIN, 14));
        contentPane.add(jLabel6, CC.xy(3, 7));

        //---- cmbPackung ----
        cmbPackung.setModel(new DefaultComboBoxModel(new String[]{

        }));
        cmbPackung.setFont(new Font("sansserif", Font.PLAIN, 14));
        cmbPackung.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbPackungItemStateChanged(e);
            }
        });
        contentPane.add(cmbPackung, CC.xywh(5, 7, 6, 1));

        //---- jLabel7 ----
        jLabel7.setText("Bemerkung:");
        jLabel7.setFont(new Font("sansserif", Font.PLAIN, 14));
        contentPane.add(jLabel7, CC.xy(3, 13));

        //---- txtBemerkung ----
        txtBemerkung.setFont(new Font("sansserif", Font.PLAIN, 14));
        txtBemerkung.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                txtBemerkungCaretUpdate(e);
            }
        });
        contentPane.add(txtBemerkung, CC.xywh(5, 13, 6, 1));

        //======== panel1 ========
        {
            panel1.setLayout(new HorizontalLayout(10));

            //---- btnPrint ----
            btnPrint.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/printer.png")));
            btnPrint.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/printer-off.png")));
            btnPrint.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    btnPrintItemStateChanged(e);
                }
            });
            panel1.add(btnPrint);

            //---- btnClose ----
            btnClose.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_eject.png")));
            btnClose.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnCloseActionPerformed(e);
                }
            });
            panel1.add(btnClose);

            //---- btnApply ----
            btnApply.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnApply.setEnabled(false);
            btnApply.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnApplyActionPerformed(e);
                }
            });
            panel1.add(btnApply);
        }
        contentPane.add(panel1, CC.xywh(9, 17, 2, 1, CC.RIGHT, CC.DEFAULT));
        pack();
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    private void txtMengeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMengeFocusGained
        SYSTools.markAllTxt((JTextField) evt.getSource());
    }//GEN-LAST:event_txtMengeFocusGained

    private void cbDruckItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbDruckItemStateChanged

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

        // TODO: Wird doppelt eingebucht. Bei neuem Vorrat

        try {
            em.getTransaction().begin();

            // Wenn die packung null ist, dann ist eine Sonderpackung
            MedPackung packung = null;
            if (cmbPackung.getSelectedItem() instanceof MedPackung) {
                packung = em.merge((MedPackung) cmbPackung.getSelectedItem());
                if (menge == null) {
                    menge = packung.getInhalt();
                }
            }

            Darreichung darreichung = em.merge((Darreichung) cmbMProdukt.getSelectedItem());

            MedVorrat vorrat = em.merge((MedVorrat) cmbVorrat.getSelectedItem());

            if (vorrat.getVorID() == null) { // neuen Vorrat anlegen
                vorrat.setText(darreichung.getMedProdukt().getBezeichnung());
            }

            MedBestand bestand = MedVorratTools.einbuchenVorrat(em, vorrat, packung, darreichung, txtBemerkung.getText(), menge);

            if (MedBestandTools.getBestandImAnbruch(vorrat) == null) {
                MedVorratTools.anbrechenNaechste(vorrat);
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Neuer Vorrat wurde direkt angebrochen", 2));

            }

            em.getTransaction().commit();

            if (btnPrint.isSelected()) {
                OPDE.getPrintProcessor().addPrintJob(new PrintListElement(bestand, etiprinter, form1, OPDE.getProps().getProperty("etiprinter1")));
            }

            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("Bestand Nr." + bestand.getBestID() + " wurde eingebucht", 2));
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
//
//        myMedAssistantDialog = new JDialog(this, "Medikamenten Assistent", true);
//        myMedAssistantDialog.setSize(1280, 800);
//        PnlProdAssistant myPnl = new PnlProdAssistant(new Closure() {
//            @Override
//            public void execute(Object o) {
//                OPDE.debug(o);
//                if (o != null) {
//                    if (o instanceof MedPackung) {
//                        txtMedSuche.setText(SYSTools.catchNull(((MedPackung) o).getPzn()));
//                    } else { // Darreichung
//                        cmbMProdukt.setModel(new DefaultComboBoxModel(new Darreichung[]{(Darreichung) o}));
//                    }
//                }
//                myMedAssistantDialog.dispose();
//                myMedAssistantDialog = null;
//            }
//        }, txtMedSuche.getText());
//        myMedAssistantDialog.setContentPane(myPnl);
//        SYSTools.centerOnParent(this, myMedAssistantDialog);
//        myMedAssistantDialog.setVisible(true);
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
//                lblMenge.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/editdelete.png")));
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

//    private void flash() {
//        flashVorrat = true;
//        thread = new Thread() {
//            public void run() {
//                try {
//                    OPDE.debug("thread");
//                    while (flashVorrat) {
//                        if (lblVorrat.getForeground() != Color.RED) {
//                            lblVorrat.setForeground(Color.RED);
//                        } else {
//                            lblVorrat.setForeground(Color.WHITE);
//                        }
//                        Thread.sleep(500);
//                    }
//                    lblVorrat.setForeground(Color.BLACK);
//                    flashVorrat = false;
//                } catch (InterruptedException e) {
//                    lblVorrat.setForeground(Color.BLACK);
//                    flashVorrat = false;
//                }
//            }
//        };
//        thread.start();
//    }

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
                cmbVorrat.setToolTipText("<html>Keinen <b>exakt</b> passender Vorrat gefunden. Wählen Sie selbst einen passenden aus oder verwenden Sie <b>automatisch</b>.<html>");
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("<html>Keinen <b>exakt</b> passender Vorrat gefunden. Wählen Sie selbst einen passenden aus oder verwenden Sie <b>automatisch</b>.<html>", 2));
                cmbVorrat.setEnabled(true);
            } else {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("<html>Ein neuer Vorrat wird <b>automatisch</b> erstellt.</html>", 2));
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
    private JLabel jLabel1;
    private JPanel panel2;
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
    private JLabel jLabel7;
    private JTextField txtBemerkung;
    private JPanel panel1;
    private JToggleButton btnPrint;
    private JButton btnClose;
    private JButton btnApply;
    // Ende der Variablendeklaration//GEN-END:variables

}
