/*
 * Created by JFormDesigner on Tue Jan 10 16:17:36 CET 2012
 */

package op.care.med;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.verordnungen.*;
import op.OPDE;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.*;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.TimelinePropertyBuilder;
import org.pushingpixels.trident.TridentConfig;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;
import org.pushingpixels.trident.interpolator.PropertyInterpolator;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Torsten Löhr
 */
public class PnlProdAssistant extends JPanel {
    private double split1pos, split2pos, split3pos, splitProdPos, splitZusatzPos, splitHerstellerPos, splitEditorSummaryPos;
    private static int speedFast = 500;
    private MedProdukte produkt;
    private Darreichung darreichung;
    private MedPackung packung;
    private MedHersteller hersteller;
    private List<MedProdukte> listProd;
    private List<Darreichung> listZusatz;
    private short numOfVisibleFrames = 1;
    private Closure fertig123;
    private Timeline timeline;
    private SwingWorker worker;
    private String template, pzn;

    public PnlProdAssistant(Closure fertig123, String template) {
        this.fertig123 = fertig123;
        worker = null;
        this.template = template;
        pzn = null;
        initComponents();
        initPanel();
    }

    private void txtProdCaretUpdate(CaretEvent e) {

    }

    private void lstProdValueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() || (worker != null && !worker.isDone()) || lstProd.getSelectedIndex() < 0) {
            return;
        }

        produkt = (MedProdukte) lstProd.getModel().getElementAt(lstProd.getSelectedIndex());
        darreichung = null;
        packung = null;
        hersteller = produkt.getHersteller();

        showLabelTop();
        proceedToFrame2();
    }

    private void showLabelTop() {
        String top = "";
        top += produkt == null ? "" : produkt.getBezeichnung();
        top += darreichung == null ? "" : " " + DarreichungTools.toPrettyStringMedium(darreichung);
        top += packung == null ? "" : ", " + MedPackungTools.toPrettyString(packung);
        top += hersteller == null ? "" : ", " + hersteller.getFirma() + ", " + hersteller.getOrt();
        lblTop.setText(top);
        thisComponentResized(null);
    }

    private void thisComponentResized(ComponentEvent e) {
        OPDE.debug("PnlProdAssistant resized to: width=" + this.getWidth() + " and heigth=" + this.getHeight());

        splitEditSummaryComponentResized(e);
        split1ComponentResized(e);
        split2ComponentResized(e);
        split3ComponentResized(e);
        splitProdComponentResized(e);
        splitZusatzComponentResized(e);
        splitHerstellerComponentResized(e);

        OPDE.debug(split1pos);
        OPDE.debug(split2pos);
        OPDE.debug(split3pos);
        OPDE.debug(splitProdPos);
        OPDE.debug(splitZusatzPos);
        OPDE.debug(splitHerstellerPos);
    }

    private void btnClearProdActionPerformed(ActionEvent e) {
        txtProd.setText("");
    }

    private void split3ComponentResized(ComponentEvent e) {
        SYSTools.showSide(split3, split3pos);
    }

    private void split2ComponentResized(ComponentEvent e) {
        SYSTools.showSide(split2, split2pos);
    }

    private void split1ComponentResized(ComponentEvent e) {
        SYSTools.showSide(split1, split1pos);
    }

    private void splitProdComponentResized(ComponentEvent e) {
        SYSTools.showSide(splitProd, splitProdPos);
    }

    private void initZusatz() {
        if (produkt.getMedPID() != null) {
            EntityManager em = OPDE.createEM();
            Query query1 = em.createNamedQuery("Darreichung.findByMedProdukt");
            query1.setParameter("medProdukt", produkt);
            listZusatz = query1.getResultList();

            if (!listZusatz.isEmpty()) {
                if (splitZusatzPos == 1d) {
                    splitZusatzPos = SYSTools.showSide(splitZusatz, new Integer(150), speedFast);
                }
            }
            DefaultListModel lmZusatz = SYSTools.list2dlm(listZusatz);
            lstZusatz.setModel(lmZusatz);
            lstZusatz.setCellRenderer(DarreichungTools.getDarreichungRenderer(DarreichungTools.MEDIUM));
            em.close();

        } else {
            lstZusatz.setModel(new DefaultListModel());
            listZusatz = null;
            if (splitZusatzPos != 1d) {
                splitZusatzPos = SYSTools.showSide(splitZusatz, SYSTools.LEFT_UPPER_SIDE, speedFast);
            }

//            darreichung = new Darreichung(produkt, txtZusatz.getText().trim(), (MedFormen) cmbForm.getSelectedItem());
        }
        txtZusatz.requestFocus();
        darreichung = (listZusatz == null || listZusatz.isEmpty()) ? new Darreichung(produkt, txtZusatz.getText().trim(), (MedFormen) cmbForm.getSelectedItem()) : listZusatz.get(0);
    }

    private void txtZusatzCaretUpdate(CaretEvent e) {
        if (produkt.getMedPID() != null && numOfVisibleFrames > 2) {
            revertToPanel(2);
        }
//        cmbForm.setEnabled(true);


        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT m FROM Darreichung m WHERE UPPER(m.zusatz) = :zusatz and m.medProdukt = :produkt and m.medForm = :form");
        query.setParameter("zusatz", txtZusatz.getText().trim().toUpperCase());
        query.setParameter("produkt", produkt);
        query.setParameter("form", cmbForm.getSelectedItem());
        listZusatz = query.getResultList();
        em.close();

        // Wenn es eine genaue Übereinstimmung schon gab, dann reden wir nicht lange sondern verwenden diese direkt.
        darreichung = listZusatz.isEmpty() ? new Darreichung(produkt, txtZusatz.getText().trim(), (MedFormen) cmbForm.getSelectedItem()) : listZusatz.get(0);


//        darreichung = null;
//        cmbForm.setEnabled(true);
        showLabelTop();
        packung = null;
    }

    private void lstZusatzValueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() || lstZusatz.getSelectedIndex() < 0) {
            return;
        }

        proceedToFrame3();
        cmbHersteller.setEnabled(hersteller == null);
//        cmbForm.setEnabled(false);

        darreichung = (Darreichung) lstZusatz.getModel().getElementAt(lstZusatz.getSelectedIndex());
        packung = null;

        showLabelTop();

    }


    private void btnCheckPackungActionPerformed(ActionEvent e) {

    }

    private void splitZusatzComponentResized(ComponentEvent e) {
        SYSTools.showSide(splitZusatz, splitZusatzPos);
    }

    private void splitHerstellerComponentResized(ComponentEvent e) {
        SYSTools.showSide(splitHersteller, splitHerstellerPos);
    }

    private void btnProdWeiterActionPerformed(ActionEvent e) {
        // Wenn das Produkt schon gewählt wurde, gar nix machen
        // wenn nicht, dann darf nicht auch noch das Textfeld leer sein.
        if (produkt != null || (worker != null && !worker.isDone()) || txtProd.getText().trim().isEmpty()) {
            return;
        }
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT m FROM MedProdukte m WHERE UPPER(m.bezeichnung) = :bezeichnung");
        query.setParameter("bezeichnung", txtProd.getText().trim().toUpperCase());
        List<MedProdukte> listeProdukte = query.getResultList();
        em.close();
        // Wenn es eine genaue Übereinstimmung schon gab, dann reden wir nicht lange sondern verwenden diese direkt.
        produkt = listeProdukte.isEmpty() ? new MedProdukte(null, txtProd.getText().trim()) : listeProdukte.get(0);


        if (splitProdPos != 1d) {
            splitProdPos = SYSTools.showSide(splitProd, SYSTools.LEFT_UPPER_SIDE, speedFast);
        }

        darreichung = null;
        hersteller = produkt.getHersteller();
        packung = null;

        showLabelTop();
        proceedToFrame2();
    }

    private void btnZusatzWeiterActionPerformed(ActionEvent e) {
        if (numOfVisibleFrames > 2) {
            return;
        }

        proceedToFrame3();
        cmbHersteller.setEnabled(hersteller == null);
        if (hersteller == null){
            hersteller = (MedHersteller) cmbHersteller.getSelectedItem();
        }
        showLabelTop();
        thisComponentResized(null);
    }

    private void cmbHerstellerItemStateChanged(ItemEvent e) {
        hersteller = (MedHersteller) cmbHersteller.getSelectedItem();

        showLabelTop();
        proceedToFrame4();

        txtPZN.setText("");
        txtInhalt.setText("1");
        lblPackEinheit.setText(MedFormenTools.EINHEIT[darreichung.getMedForm().getPackEinheit()]);
        packung = null;
    }


    private void proceedToFrame2() {
//        cmbForm.setEnabled(true);
        if (numOfVisibleFrames == 1) {
            numOfVisibleFrames = 2;
            split1pos = SYSTools.showSide(split1, 0.5d, speedFast, new TimelineCallbackAdapter() {
                @Override
                public void onTimelineStateChanged(Timeline.TimelineState oldState, Timeline.TimelineState newState, float durationFraction, float timelinePosition) {
                    if (newState == Timeline.TimelineState.DONE) {
                        thisComponentResized(null);
                        initZusatz();

                    }
                }
            });
        }
    }

    private void proceedToFrame3() {
//        cmbForm.setEnabled(false);
        if (numOfVisibleFrames >= 2) {
            numOfVisibleFrames = 3;
            split1pos = SYSTools.showSide(split1, 0.33d, speedFast / 3, new TimelineCallbackAdapter() {
                @Override
                public void onTimelineStateChanged(Timeline.TimelineState oldState, Timeline.TimelineState newState, float durationFraction, float timelinePosition) {
                    if (newState == Timeline.TimelineState.DONE) {
                        split2pos = SYSTools.showSide(split2, 0.5d, speedFast / 3, new TimelineCallbackAdapter() {
                            @Override
                            public void onTimelineStateChanged(Timeline.TimelineState oldState, Timeline.TimelineState newState, float durationFraction, float timelinePosition) {
                                if (newState == Timeline.TimelineState.DONE) {

                                    // Wenn das Produkt bereits in der Datenbank steht, dann hat es auch einen
                                    // Hersteller, den man hier nicht mehr verändern kann.
                                    // Dann zeigen wir nur noch die Produkte an.
                                    boolean targetPosition = produkt.getMedPID() != null ? SYSTools.RIGHT_LOWER_SIDE : SYSTools.LEFT_UPPER_SIDE;
                                    txtPZN.setText("");
                                    txtInhalt.setText("1");
                                    lblPackEinheit.setText(MedFormenTools.EINHEIT[darreichung.getMedForm().getPackEinheit()]);
                                    packung = null;
                                    split3pos = SYSTools.showSide(split3, targetPosition, speedFast / 3, new TimelineCallbackAdapter() {
                                        @Override
                                        public void onTimelineStateChanged(Timeline.TimelineState oldState, Timeline.TimelineState newState, float durationFraction, float timelinePosition) {
                                            if (newState == Timeline.TimelineState.DONE) {
                                                thisComponentResized(null);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void proceedToFrame4() {
        if (numOfVisibleFrames >= 3) {
            numOfVisibleFrames = 4;
            split1pos = SYSTools.showSide(split1, 0.25d, speedFast / 3, new TimelineCallbackAdapter() {
                @Override
                public void onTimelineStateChanged(Timeline.TimelineState oldState, Timeline.TimelineState newState, float durationFraction, float timelinePosition) {
                    if (newState == Timeline.TimelineState.DONE) {
                        split2pos = SYSTools.showSide(split2, 0.33d, speedFast / 3, new TimelineCallbackAdapter() {
                            @Override
                            public void onTimelineStateChanged(Timeline.TimelineState oldState, Timeline.TimelineState newState, float durationFraction, float timelinePosition) {
                                if (newState == Timeline.TimelineState.DONE) {
                                    txtPZN.setText("");
                                    txtInhalt.setText("1");
                                    lblPackEinheit.setText(MedFormenTools.EINHEIT[darreichung.getMedForm().getPackEinheit()]);
                                    packung = null;
                                    split3pos = SYSTools.showSide(split3, 0.5d, speedFast / 3, new TimelineCallbackAdapter() {
                                        @Override
                                        public void onTimelineStateChanged(Timeline.TimelineState oldState, Timeline.TimelineState newState, float durationFraction, float timelinePosition) {
                                            if (newState == Timeline.TimelineState.DONE) {
                                                thisComponentResized(null);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void btnHerstellerWeiterActionPerformed(ActionEvent e) {
        if (numOfVisibleFrames > 3) {
            return;
        }

        if (hersteller == null) {
            if (splitHerstellerPos == 1.0d) {
                cmbHerstellerItemStateChanged(null);
            } else {
                if (txtFirma.getText().trim().isEmpty() || txtOrt.getText().trim().isEmpty()) {
                    lblFirma.setIcon(txtFirma.getText().trim().isEmpty() ? new ImageIcon(getClass().getResource("/artwork/22x22/bw/editdelete.png")) : null);
                    lblOrt.setIcon(txtOrt.getText().trim().isEmpty() ? new ImageIcon(getClass().getResource("/artwork/22x22/bw/editdelete.png")) : null);
                    hersteller = null;
                } else {
                    hersteller = new MedHersteller(txtFirma.getText().trim(), txtStrasse.getText().trim(), txtPLZ.getText().trim(), txtOrt.getText().trim(), txtTel.getText().trim(), txtFax.getText().trim(), txtWWW.getText().trim());
                }
            }
        } else {
            showLabelTop();
            proceedToFrame4();
        }
    }

    private void save() {
        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            produkt.setHersteller(hersteller);
            darreichung.setMedProdukt(produkt);
            produkt.getDarreichungen().add(darreichung);
            if (packung != null) {
                packung.setDarreichung(darreichung);
                darreichung.getPackungen().add(packung);
            }

            if (hersteller.getMphid() == null) {
                em.persist(hersteller);
            }

            if (produkt.getMedPID() == null) {
                em.persist(produkt);
            } else {
                produkt = em.merge(produkt);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            OPDE.fatal(e);
        } finally {
            em.close();
        }
    }

    public MedPackung getPackung() {
        return packung;
    }

    public Darreichung getDarreichung() {
        return darreichung;
    }

    public MedProdukte getProdukt() {
        return produkt;
    }

    public MedHersteller getHersteller() {
        return hersteller;
    }

    private void btnKeinePackungActionPerformed(ActionEvent e) {
//        numOfVisibleFrames = 3;
//        split1pos = SYSTools.showSide(split1, 0.33d, speedFast);
//        split2pos = SYSTools.showSide(split2, 0.5d, speedFast);
//        split3pos = SYSTools.showSide(split3, SYSTools.LEFT_UPPER_SIDE, speedFast);

        if (produkt.getMedPID() != null) {
            revertToPanel(2);
        } else {
            proceedToFrame3();
        }

        packung = null;
        showLabelTop();
    }

    private void btnNewHerstellerActionPerformed(ActionEvent e) {
        if (numOfVisibleFrames > 3 || produkt.getHersteller() != null) {
            return;
        }
        if (splitHerstellerPos == 1.0d) {
            splitHerstellerPos = SYSTools.showSide(splitHersteller, new Integer(70), speedFast, new TimelineCallbackAdapter() {
                @Override
                public void onTimelineStateChanged(Timeline.TimelineState oldState, Timeline.TimelineState newState, float durationFraction, float timelinePosition) {
                    if (newState == Timeline.TimelineState.DONE) {
                        thisComponentResized(null);
                    }
                }
            });
        }
    }

    private void cmbFormItemStateChanged(ItemEvent e) {
        if (produkt.getMedPID() != null && numOfVisibleFrames > 2) {
            revertToPanel(2);
        }

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT m FROM Darreichung m WHERE UPPER(m.zusatz) = :zusatz and m.medProdukt = :produkt and m.medForm = :form");
        query.setParameter("zusatz", txtZusatz.getText().trim().toUpperCase());
        query.setParameter("produkt", produkt);
        query.setParameter("form", cmbForm.getSelectedItem());
        List<Darreichung> listeDarreichungen = query.getResultList();
        em.close();

        // Wenn es eine genaue Übereinstimmung schon gab, dann reden wir nicht lange sondern verwenden diese direkt.
        darreichung = listeDarreichungen.isEmpty() ? new Darreichung(produkt, txtZusatz.getText().trim(), (MedFormen) cmbForm.getSelectedItem()) : listeDarreichungen.get(0);
        showLabelTop();
    }

    private void btnPackungWeiterActionPerformed(ActionEvent e) {
        String pzn = MedPackungTools.checkNewPZN(txtPZN.getText().trim(), null);
        BigDecimal inhalt = SYSTools.parseBigDecimal(txtInhalt.getText());
        String txt = "";

        if (pzn == null) {
            txt += "<li>Die PZN ist falsch oder wird bereits verwendet.</li>";
            lblPZN.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/editdelete.png")));
        } else {
            lblPZN.setIcon(null);
        }

        if (inhalt == null) {
            lblInhalt.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/editdelete.png")));
            txt += "<li>Die Inhaltsangabe ist falsch.</li>";
        } else {
            lblInhalt.setIcon(null);
        }
        txtPackungMessage.setText(SYSTools.toHTML(txt));

        if (pzn != null && inhalt != null) {
            packung = new MedPackung(darreichung);
            packung.setPzn(pzn);
            packung.setInhalt(inhalt);
            packung.setGroesse((short) cmbGroesse.getSelectedIndex());
            showLabelTop();
            btnCheckSave.doClick();
        } else {
            packung = null;
            showLabelTop();
        }

    }

    private void btnCheckSaveActionPerformed(ActionEvent e) {

        if (splitEditorSummaryPos == 1.0d) { // Steht auf Edit

            boolean notYetReady = produkt == null || darreichung == null || hersteller == null;

            if (!notYetReady) {
                btnCheckSave.setText("Eingabe speichern");
            }

            btnCheckSave.setEnabled(!notYetReady);

            btnCancelBack.setText("Zurück");
            String result = "<html><body><h1>Na dann schaun wir mal...</h1>";

            result += "<font size=\"14\"><ul>";
            result += "Folgendes haben Sie eingeben:<br/>";
            result += "<ul>";
            if (produkt == null) {
                result += "<li><font color=\"red\">noch kein Medikament eingegeben</font></li>";
            } else {
                result += "<li>Medikament: <b>" + produkt.getBezeichnung() + "</b>" + (produkt.getMedPID() == null ? " <i>wird neu erstellt</i>" : "") + "</li>";
            }

            if (darreichung == null) {
                result += "<li><font color=\"red\">Keinen Darreichung festgelegt</font></li>";
            } else {
                result += "<li>Zusatzbezeichnung und Darreichungsform: <b>" + DarreichungTools.toPrettyStringMedium(darreichung) + "</b>" + (darreichung.getDafID() == null ? " <i>wird neu erstellt</i>" : "") + "</li>";
            }

            if (hersteller == null) {
                result += "<li><font color=\"red\">Keinen Hersteller festgelegt</font></li>";
            } else {
                result += "<li>Hersteller: <b>" + hersteller.getFirma() + ", " + hersteller.getOrt() + "</b>" + (hersteller.getMphid() == null ? " <i>wird neu erstellt</i>" : "") + "</li>";
            }

            if (packung != null) {
                result += "<li>neue Packung wird eingetragen: <b>" + MedPackungTools.toPrettyString(packung) + "</b></li>";
            }
            result += "</ul>";
            if (notYetReady) {
                result += "<br/>Drücken sie auf ZURÜCK und vervollständigen Sie die Angaben.";
            } else {
                result += "<br/>";
                result += "Wenn Sie sicher sind, dann bestätigen Sie nun die Eingaben.</font>";
            }

            result += "</body></html>";
            txtSummary.setText(result);
            splitEditorSummaryPos = SYSTools.showSide(splitEditSummary, SYSTools.RIGHT_LOWER_SIDE, speedFast);
        } else {// steht auf summary
            save();
            fertig123.execute(packung != null ? packung : darreichung);
        }


    }

    private void btnCancelBackActionPerformed(ActionEvent e) {
        if (splitEditorSummaryPos == 1.0d) { // Steht auf Edit
            fertig123.execute(null);
        } else {// steht auf summary
            btnCheckSave.setText("Eingaben prüfen");
            btnCancelBack.setText("Abbrechen");
            btnCheckSave.setEnabled(true);
            splitEditorSummaryPos = SYSTools.showSide(splitEditSummary, SYSTools.LEFT_UPPER_SIDE, speedFast);
        }
    }

    private void splitEditSummaryComponentResized(ComponentEvent e) {
        SYSTools.showSide(splitEditSummary, splitEditorSummaryPos);
    }

    private void txtProdActionPerformed(ActionEvent e) {
        revertToPanel(1);
        if (worker != null && !worker.isDone()) {
            return;
        }


        if (!txtProd.getText().trim().isEmpty()) {

            if (timeline == null) {
//                timeline = SYSTools.flashLabel(label1, "Datenbank");
//                lblHardDisk.setText("Datenbankzugriff");
                timeline = new Timeline(lblHardDisk);
                ImageIcon hd1 = new ImageIcon(getClass().getResource("/artwork/32x32/hdd_mount.png"));
                ImageIcon hd2 = new ImageIcon(getClass().getResource("/artwork/32x32/hdd_unmount.png"));
//                timeline.addPropertyToInterpolate("icon", new ImageIcon(getClass().getResource("/artwork/32x32/bw/hdd_mount.png")), new ImageIcon(getClass().getResource("/artwork/32x32/bw/hdd_unmount.png")));

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

//                timeline.addCallback(new TimelineCallbackAdapter() {
//                    @Override
//                    public void onTimelineStateChanged(Timeline.TimelineState oldState, Timeline.TimelineState newState, float durationFraction, float timelinePosition) {
//                        OPDE.debug(newState);
//                        if (newState == Timeline.TimelineState.CANCELLED || newState == Timeline.TimelineState.DONE) {
//                            lblHardDisk.setText(null);
//                            lblHardDisk.setIcon(null);
//                        }
//                    }
//                });
                timeline.playLoop(Timeline.RepeatBehavior.REVERSE);
            }

            worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
//                    txtProd.setEditable(false);
                    EntityManager em = OPDE.createEM();
                    Query query = em.createNamedQuery("MedProdukte.findByBezeichnungLike");
                    query.setParameter("bezeichnung", "%" + txtProd.getText().trim() + "%");
                    listProd = query.getResultList();
                    em.close();
                    return null;
                }

                @Override
                protected void done() {
                    if (!listProd.isEmpty()) {
                        if (splitProdPos == 1d) {
                            splitProdPos = SYSTools.showSide(splitProd, new Integer(150), speedFast);
                        }
                        DefaultListModel lmProd;
                        lmProd = SYSTools.list2dlm(listProd);
                        lstProd.setModel(lmProd);
                        lstProd.setCellRenderer(MedProdukteTools.getMedProdukteRenderer());
                    } else {
                        if (splitProdPos != 1d) {
                            splitProdPos = SYSTools.showSide(splitProd, SYSTools.LEFT_UPPER_SIDE, speedFast);
                        }
                    }

                    if (timeline != null) {
                        timeline.cancel();
                        timeline = null;
                    }
//                    lblProdMsg.setForeground(Color.BLACK);
//                    txtProd.setEditable(true);
                    txtProd.requestFocus();
                }
            };
            worker.execute();
        } else {
            lstProd.setModel(new DefaultListModel());
            listProd = null;
            if (splitProdPos != 1d) {
                splitProdPos = SYSTools.showSide(splitProd, SYSTools.LEFT_UPPER_SIDE, speedFast);
            }
        }
        produkt = null;
        darreichung = null;
        packung = null;
        showLabelTop();
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel11 = new JPanel();
        lblTop = new JLabel();
        splitEditSummary = new JSplitPane();
        split1 = new JSplitPane();
        pnlSplitProd = new JPanel();
        splitProd = new JSplitPane();
        panel1 = new JPanel();
        label1 = new JLabel();
        panel9 = new JPanel();
        lblHardDisk = new JLabel();
        txtProd = new JXSearchField();
        btnClearProd = new JButton();
        panel2 = new JPanel();
        lblProdMsg = new JLabel();
        scrollPane1 = new JScrollPane();
        lstProd = new JList();
        btnProdWeiter = new JButton();
        split2 = new JSplitPane();
        pnlSplitZusatz = new JPanel();
        splitZusatz = new JSplitPane();
        pnlSplitTop = new JPanel();
        label2 = new JLabel();
        panel10 = new JPanel();
        txtZusatz = new JTextField();
        btnClearZusatz = new JButton();
        cmbForm = new JComboBox();
        pnlSplitBottom = new JPanel();
        lblZusatzMsg = new JLabel();
        scrollPane2 = new JScrollPane();
        lstZusatz = new JList();
        btnZusatzWeiter = new JButton();
        split3 = new JSplitPane();
        pnlSplitHersteller = new JPanel();
        splitHersteller = new JSplitPane();
        panel7 = new JPanel();
        label7 = new JLabel();
        panel8 = new JPanel();
        cmbHersteller = new JComboBox();
        btnNewHersteller = new JButton();
        panel6 = new JPanel();
        jLabel1 = new JLabel();
        lblFirma = new JLabel();
        jLabel3 = new JLabel();
        lblOrt = new JLabel();
        jLabel5 = new JLabel();
        jLabel6 = new JLabel();
        jLabel7 = new JLabel();
        txtPLZ = new JTextField();
        txtFirma = new JTextField();
        txtStrasse = new JTextField();
        txtTel = new JTextField();
        txtFax = new JTextField();
        txtWWW = new JTextField();
        txtOrt = new JTextField();
        btnHerstellerWeiter = new JButton();
        pnlPackung = new JPanel();
        label4 = new JLabel();
        lblPZN = new JLabel();
        txtPZN = new JTextField();
        label6 = new JLabel();
        cmbGroesse = new JComboBox();
        lblInhalt = new JLabel();
        txtInhalt = new JTextField();
        lblPackEinheit = new JLabel();
        panel14 = new JPanel();
        scrollPane3 = new JScrollPane();
        txtPackungMessage = new JTextPane();
        panel3 = new JPanel();
        btnKeinePackung = new JButton();
        btnPackungWeiter = new JButton();
        scrollPane4 = new JScrollPane();
        txtSummary = new JTextPane();
        panel4 = new JPanel();
        btnCheckSave = new JButton();
        btnCancelBack = new JButton();

        //======== this ========
        setPreferredSize(new Dimension(100, 100));
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                thisComponentResized(e);
            }
        });
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== panel11 ========
        {
            panel11.setLayout(new FormLayout(
                "default:grow",
                "fill:default, $lgap, default:grow, $lgap, default"));

            //---- lblTop ----
            lblTop.setFont(new Font("sansserif", Font.PLAIN, 18));
            lblTop.setBackground(new Color(255, 204, 204));
            lblTop.setOpaque(true);
            lblTop.setForeground(new Color(153, 0, 51));
            lblTop.setHorizontalAlignment(SwingConstants.CENTER);
            panel11.add(lblTop, CC.xy(1, 1));

            //======== splitEditSummary ========
            {
                splitEditSummary.setOrientation(JSplitPane.VERTICAL_SPLIT);
                splitEditSummary.setDividerSize(0);
                splitEditSummary.setDividerLocation(470);
                splitEditSummary.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        splitEditSummaryComponentResized(e);
                    }
                });

                //======== split1 ========
                {
                    split1.setDividerLocation(200);
                    split1.setFont(new Font("sansserif", Font.PLAIN, 24));
                    split1.setDoubleBuffered(true);
                    split1.setEnabled(false);
                    split1.setDividerSize(1);
                    split1.addComponentListener(new ComponentAdapter() {
                        @Override
                        public void componentResized(ComponentEvent e) {
                            split1ComponentResized(e);
                        }
                    });

                    //======== pnlSplitProd ========
                    {
                        pnlSplitProd.setLayout(new FormLayout(
                            "default:grow",
                            "fill:default:grow, fill:default"));

                        //======== splitProd ========
                        {
                            splitProd.setOrientation(JSplitPane.VERTICAL_SPLIT);
                            splitProd.setDividerSize(1);
                            splitProd.setEnabled(false);
                            splitProd.setDoubleBuffered(true);
                            splitProd.setDividerLocation(200);
                            splitProd.addComponentListener(new ComponentAdapter() {
                                @Override
                                public void componentResized(ComponentEvent e) {
                                    splitProdComponentResized(e);
                                }
                            });

                            //======== panel1 ========
                            {
                                panel1.setMinimumSize(new Dimension(83, 93));
                                panel1.setLayout(new FormLayout(
                                    "default:grow",
                                    "$rgap, 2*($lgap, default), $lgap, top:30px:grow"));

                                //---- label1 ----
                                label1.setText("Medizin-Produkt");
                                label1.setFont(new Font("sansserif", Font.PLAIN, 18));
                                label1.setBackground(new Color(204, 204, 255));
                                label1.setOpaque(true);
                                label1.setForeground(Color.black);
                                label1.setHorizontalAlignment(SwingConstants.CENTER);
                                panel1.add(label1, CC.xy(1, 3));

                                //======== panel9 ========
                                {
                                    panel9.setLayout(new BoxLayout(panel9, BoxLayout.LINE_AXIS));

                                    //---- lblHardDisk ----
                                    lblHardDisk.setFont(new Font("sansserif", Font.PLAIN, 16));
                                    lblHardDisk.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/hdd_unmount.png")));
                                    panel9.add(lblHardDisk);

                                    //---- txtProd ----
                                    txtProd.setFont(new Font("sansserif", Font.PLAIN, 16));
                                    txtProd.setInstantSearchDelay(500);
                                    txtProd.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            txtProdActionPerformed(e);
                                        }
                                    });
                                    panel9.add(txtProd);

                                    //---- btnClearProd ----
                                    btnClearProd.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/button_cancel.png")));
                                    btnClearProd.setContentAreaFilled(false);
                                    btnClearProd.setBorderPainted(false);
                                    btnClearProd.setBorder(null);
                                    btnClearProd.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            btnClearProdActionPerformed(e);
                                        }
                                    });
                                    panel9.add(btnClearProd);
                                }
                                panel1.add(panel9, CC.xy(1, 5));
                            }
                            splitProd.setTopComponent(panel1);

                            //======== panel2 ========
                            {
                                panel2.setMinimumSize(new Dimension(83, 93));
                                panel2.setPreferredSize(new Dimension(83, 93));
                                panel2.setLayout(new FormLayout(
                                    "default:grow",
                                    "default, $lgap, default:grow"));

                                //---- lblProdMsg ----
                                lblProdMsg.setFont(new Font("sansserif", Font.BOLD, 16));
                                lblProdMsg.setText("Diese \u00e4hnlichen Medis gibt es schon");
                                panel2.add(lblProdMsg, CC.xy(1, 1));

                                //======== scrollPane1 ========
                                {

                                    //---- lstProd ----
                                    lstProd.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                                    lstProd.setFont(new Font("sansserif", Font.PLAIN, 16));
                                    lstProd.addListSelectionListener(new ListSelectionListener() {
                                        @Override
                                        public void valueChanged(ListSelectionEvent e) {
                                            lstProdValueChanged(e);
                                        }
                                    });
                                    scrollPane1.setViewportView(lstProd);
                                }
                                panel2.add(scrollPane1, CC.xy(1, 3, CC.DEFAULT, CC.FILL));
                            }
                            splitProd.setBottomComponent(panel2);
                        }
                        pnlSplitProd.add(splitProd, CC.xy(1, 1));

                        //---- btnProdWeiter ----
                        btnProdWeiter.setText("Weiter");
                        btnProdWeiter.setFont(new Font("sansserif", Font.PLAIN, 16));
                        btnProdWeiter.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/1rightarrow.png")));
                        btnProdWeiter.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnProdWeiterActionPerformed(e);
                            }
                        });
                        pnlSplitProd.add(btnProdWeiter, CC.xy(1, 2, CC.FILL, CC.DEFAULT));
                    }
                    split1.setLeftComponent(pnlSplitProd);

                    //======== split2 ========
                    {
                        split2.setDividerLocation(100);
                        split2.setMinimumSize(new Dimension(200, 372));
                        split2.setPreferredSize(new Dimension(200, 372));
                        split2.setDividerSize(0);
                        split2.addComponentListener(new ComponentAdapter() {
                            @Override
                            public void componentResized(ComponentEvent e) {
                                split2ComponentResized(e);
                            }
                        });

                        //======== pnlSplitZusatz ========
                        {
                            pnlSplitZusatz.setLayout(new FormLayout(
                                "default:grow",
                                "fill:default:grow, $lgap, default"));

                            //======== splitZusatz ========
                            {
                                splitZusatz.setOrientation(JSplitPane.VERTICAL_SPLIT);
                                splitZusatz.setDividerLocation(250);
                                splitZusatz.setDividerSize(0);
                                splitZusatz.addComponentListener(new ComponentAdapter() {
                                    @Override
                                    public void componentResized(ComponentEvent e) {
                                        splitZusatzComponentResized(e);
                                    }
                                });

                                //======== pnlSplitTop ========
                                {
                                    pnlSplitTop.setMinimumSize(new Dimension(83, 93));
                                    pnlSplitTop.setLayout(new FormLayout(
                                        "default:grow",
                                        "$rgap, 3*($lgap, default), $lgap, default:grow"));

                                    //---- label2 ----
                                    label2.setText("Zusatz und Darreichung");
                                    label2.setFont(new Font("sansserif", Font.PLAIN, 18));
                                    label2.setBackground(new Color(255, 204, 255));
                                    label2.setOpaque(true);
                                    label2.setForeground(Color.black);
                                    label2.setHorizontalAlignment(SwingConstants.CENTER);
                                    pnlSplitTop.add(label2, CC.xy(1, 3));

                                    //======== panel10 ========
                                    {
                                        panel10.setLayout(new BoxLayout(panel10, BoxLayout.LINE_AXIS));

                                        //---- txtZusatz ----
                                        txtZusatz.setFont(new Font("sansserif", Font.PLAIN, 16));
                                        txtZusatz.addCaretListener(new CaretListener() {
                                            @Override
                                            public void caretUpdate(CaretEvent e) {
                                                txtZusatzCaretUpdate(e);
                                            }
                                        });
                                        panel10.add(txtZusatz);

                                        //---- btnClearZusatz ----
                                        btnClearZusatz.setBorderPainted(false);
                                        btnClearZusatz.setBorder(null);
                                        btnClearZusatz.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/button_cancel.png")));
                                        panel10.add(btnClearZusatz);
                                    }
                                    pnlSplitTop.add(panel10, CC.xy(1, 5));

                                    //---- cmbForm ----
                                    cmbForm.setFont(new Font("sansserif", Font.PLAIN, 16));
                                    cmbForm.addItemListener(new ItemListener() {
                                        @Override
                                        public void itemStateChanged(ItemEvent e) {
                                            cmbFormItemStateChanged(e);
                                        }
                                    });
                                    pnlSplitTop.add(cmbForm, CC.xy(1, 7));
                                }
                                splitZusatz.setTopComponent(pnlSplitTop);

                                //======== pnlSplitBottom ========
                                {
                                    pnlSplitBottom.setMinimumSize(new Dimension(83, 93));
                                    pnlSplitBottom.setPreferredSize(new Dimension(83, 93));
                                    pnlSplitBottom.setLayout(new FormLayout(
                                        "default:grow",
                                        "default, $lgap, default:grow"));

                                    //---- lblZusatzMsg ----
                                    lblZusatzMsg.setFont(new Font("sansserif", Font.BOLD, 16));
                                    lblZusatzMsg.setText("Zu diesem Produkt gibt es schon Darreichungen");
                                    pnlSplitBottom.add(lblZusatzMsg, CC.xy(1, 1));

                                    //======== scrollPane2 ========
                                    {

                                        //---- lstZusatz ----
                                        lstZusatz.setFont(new Font("sansserif", Font.PLAIN, 16));
                                        lstZusatz.addListSelectionListener(new ListSelectionListener() {
                                            @Override
                                            public void valueChanged(ListSelectionEvent e) {
                                                lstZusatzValueChanged(e);
                                            }
                                        });
                                        scrollPane2.setViewportView(lstZusatz);
                                    }
                                    pnlSplitBottom.add(scrollPane2, CC.xy(1, 3, CC.DEFAULT, CC.FILL));
                                }
                                splitZusatz.setBottomComponent(pnlSplitBottom);
                            }
                            pnlSplitZusatz.add(splitZusatz, CC.xy(1, 1));

                            //---- btnZusatzWeiter ----
                            btnZusatzWeiter.setText("Weiter");
                            btnZusatzWeiter.setFont(new Font("sansserif", Font.PLAIN, 16));
                            btnZusatzWeiter.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/1rightarrow.png")));
                            btnZusatzWeiter.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    btnZusatzWeiterActionPerformed(e);
                                }
                            });
                            pnlSplitZusatz.add(btnZusatzWeiter, CC.xy(1, 3, CC.FILL, CC.DEFAULT));
                        }
                        split2.setLeftComponent(pnlSplitZusatz);

                        //======== split3 ========
                        {
                            split3.setDividerLocation(50);
                            split3.setDividerSize(0);
                            split3.addComponentListener(new ComponentAdapter() {
                                @Override
                                public void componentResized(ComponentEvent e) {
                                    split3ComponentResized(e);
                                }
                            });

                            //======== pnlSplitHersteller ========
                            {
                                pnlSplitHersteller.setLayout(new FormLayout(
                                    "default:grow",
                                    "fill:default:grow, $lgap, default"));

                                //======== splitHersteller ========
                                {
                                    splitHersteller.setOrientation(JSplitPane.VERTICAL_SPLIT);
                                    splitHersteller.setDividerLocation(100);
                                    splitHersteller.setDividerSize(0);
                                    splitHersteller.addComponentListener(new ComponentAdapter() {
                                        @Override
                                        public void componentResized(ComponentEvent e) {
                                            splitHerstellerComponentResized(e);
                                        }
                                    });

                                    //======== panel7 ========
                                    {
                                        panel7.setLayout(new FormLayout(
                                            "default:grow",
                                            "$rgap, 3*($lgap, default)"));

                                        //---- label7 ----
                                        label7.setText("Hersteller");
                                        label7.setFont(new Font("sansserif", Font.PLAIN, 18));
                                        label7.setBackground(new Color(204, 255, 204));
                                        label7.setOpaque(true);
                                        label7.setForeground(Color.black);
                                        label7.setHorizontalAlignment(SwingConstants.CENTER);
                                        panel7.add(label7, CC.xy(1, 3));

                                        //======== panel8 ========
                                        {
                                            panel8.setLayout(new BoxLayout(panel8, BoxLayout.LINE_AXIS));

                                            //---- cmbHersteller ----
                                            cmbHersteller.setFont(new Font("sansserif", Font.PLAIN, 16));
                                            cmbHersteller.addItemListener(new ItemListener() {
                                                @Override
                                                public void itemStateChanged(ItemEvent e) {
                                                    cmbHerstellerItemStateChanged(e);
                                                }
                                            });
                                            panel8.add(cmbHersteller);

                                            //---- btnNewHersteller ----
                                            btnNewHersteller.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/add.png")));
                                            btnNewHersteller.setBorder(null);
                                            btnNewHersteller.addActionListener(new ActionListener() {
                                                @Override
                                                public void actionPerformed(ActionEvent e) {
                                                    btnNewHerstellerActionPerformed(e);
                                                }
                                            });
                                            panel8.add(btnNewHersteller);
                                        }
                                        panel7.add(panel8, CC.xy(1, 5));
                                    }
                                    splitHersteller.setTopComponent(panel7);

                                    //======== panel6 ========
                                    {
                                        panel6.setMinimumSize(new Dimension(83, 93));
                                        panel6.setPreferredSize(new Dimension(83, 93));
                                        panel6.setLayout(new FormLayout(
                                            "default, 2*($lcgap, default:grow)",
                                            "7*(fill:default, $lgap), fill:default"));

                                        //---- jLabel1 ----
                                        jLabel1.setFont(new Font("SansSerif", Font.PLAIN, 16));
                                        jLabel1.setText("Neuen Hersteller eingeben");
                                        panel6.add(jLabel1, CC.xywh(1, 1, 5, 1));

                                        //---- lblFirma ----
                                        lblFirma.setText("Firma:");
                                        lblFirma.setFont(new Font("sansserif", Font.PLAIN, 16));
                                        panel6.add(lblFirma, CC.xy(1, 5));

                                        //---- jLabel3 ----
                                        jLabel3.setText("Strasse:");
                                        jLabel3.setFont(new Font("sansserif", Font.PLAIN, 16));
                                        panel6.add(jLabel3, CC.xy(1, 7));

                                        //---- lblOrt ----
                                        lblOrt.setText("PLZ, Ort:");
                                        lblOrt.setFont(new Font("sansserif", Font.PLAIN, 16));
                                        panel6.add(lblOrt, CC.xy(1, 9));

                                        //---- jLabel5 ----
                                        jLabel5.setText("Telefon:");
                                        jLabel5.setFont(new Font("sansserif", Font.PLAIN, 16));
                                        panel6.add(jLabel5, CC.xy(1, 11));

                                        //---- jLabel6 ----
                                        jLabel6.setText("Fax:");
                                        jLabel6.setFont(new Font("sansserif", Font.PLAIN, 16));
                                        panel6.add(jLabel6, CC.xy(1, 13));

                                        //---- jLabel7 ----
                                        jLabel7.setText("WWW:");
                                        jLabel7.setFont(new Font("sansserif", Font.PLAIN, 16));
                                        panel6.add(jLabel7, CC.xy(1, 15));

                                        //---- txtPLZ ----
                                        txtPLZ.setFont(new Font("sansserif", Font.PLAIN, 16));
                                        panel6.add(txtPLZ, CC.xy(3, 9));

                                        //---- txtFirma ----
                                        txtFirma.setFont(new Font("sansserif", Font.PLAIN, 16));
                                        panel6.add(txtFirma, CC.xywh(3, 5, 3, 1));

                                        //---- txtStrasse ----
                                        txtStrasse.setFont(new Font("sansserif", Font.PLAIN, 16));
                                        panel6.add(txtStrasse, CC.xywh(3, 7, 3, 1));

                                        //---- txtTel ----
                                        txtTel.setFont(new Font("sansserif", Font.PLAIN, 16));
                                        panel6.add(txtTel, CC.xywh(3, 11, 3, 1));

                                        //---- txtFax ----
                                        txtFax.setFont(new Font("sansserif", Font.PLAIN, 16));
                                        panel6.add(txtFax, CC.xywh(3, 13, 3, 1));

                                        //---- txtWWW ----
                                        txtWWW.setFont(new Font("sansserif", Font.PLAIN, 16));
                                        panel6.add(txtWWW, CC.xywh(3, 15, 3, 1));

                                        //---- txtOrt ----
                                        txtOrt.setFont(new Font("sansserif", Font.PLAIN, 16));
                                        panel6.add(txtOrt, CC.xy(5, 9));
                                    }
                                    splitHersteller.setBottomComponent(panel6);
                                }
                                pnlSplitHersteller.add(splitHersteller, CC.xy(1, 1));

                                //---- btnHerstellerWeiter ----
                                btnHerstellerWeiter.setText("Weiter");
                                btnHerstellerWeiter.setFont(new Font("sansserif", Font.PLAIN, 16));
                                btnHerstellerWeiter.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/1rightarrow.png")));
                                btnHerstellerWeiter.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        btnHerstellerWeiterActionPerformed(e);
                                    }
                                });
                                pnlSplitHersteller.add(btnHerstellerWeiter, CC.xy(1, 3));
                            }
                            split3.setLeftComponent(pnlSplitHersteller);

                            //======== pnlPackung ========
                            {
                                pnlPackung.setMinimumSize(new Dimension(83, 93));
                                pnlPackung.setPreferredSize(new Dimension(83, 93));
                                pnlPackung.setLayout(new FormLayout(
                                    "default, $lcgap, default:grow, $lcgap, default",
                                    "$rgap, 4*($lgap, default), $ugap, default:grow"));

                                //---- label4 ----
                                label4.setText("Verpackung");
                                label4.setFont(new Font("sansserif", Font.PLAIN, 18));
                                label4.setBackground(new Color(255, 255, 204));
                                label4.setOpaque(true);
                                label4.setForeground(Color.black);
                                label4.setHorizontalAlignment(SwingConstants.CENTER);
                                pnlPackung.add(label4, CC.xywh(1, 3, 5, 1));

                                //---- lblPZN ----
                                lblPZN.setText("PZN");
                                lblPZN.setFont(new Font("sansserif", Font.PLAIN, 16));
                                pnlPackung.add(lblPZN, CC.xy(1, 5));

                                //---- txtPZN ----
                                txtPZN.setFont(new Font("sansserif", Font.PLAIN, 16));
                                pnlPackung.add(txtPZN, CC.xywh(3, 5, 3, 1));

                                //---- label6 ----
                                label6.setText("Gr\u00f6\u00dfe");
                                label6.setFont(new Font("sansserif", Font.PLAIN, 16));
                                pnlPackung.add(label6, CC.xy(1, 7));

                                //---- cmbGroesse ----
                                cmbGroesse.setFont(new Font("sansserif", Font.PLAIN, 16));
                                pnlPackung.add(cmbGroesse, CC.xywh(3, 7, 3, 1));

                                //---- lblInhalt ----
                                lblInhalt.setText("Inhalt");
                                lblInhalt.setFont(new Font("sansserif", Font.PLAIN, 16));
                                pnlPackung.add(lblInhalt, CC.xy(1, 9));

                                //---- txtInhalt ----
                                txtInhalt.setFont(new Font("sansserif", Font.PLAIN, 16));
                                pnlPackung.add(txtInhalt, CC.xy(3, 9));

                                //---- lblPackEinheit ----
                                lblPackEinheit.setFont(new Font("sansserif", Font.PLAIN, 16));
                                pnlPackung.add(lblPackEinheit, CC.xy(5, 9));

                                //======== panel14 ========
                                {
                                    panel14.setLayout(new FormLayout(
                                        "left:default:grow",
                                        "fill:default:grow, 2*(fill:default)"));

                                    //======== scrollPane3 ========
                                    {

                                        //---- txtPackungMessage ----
                                        txtPackungMessage.setEditable(false);
                                        txtPackungMessage.setOpaque(false);
                                        txtPackungMessage.setEnabled(false);
                                        txtPackungMessage.setContentType("text/html");
                                        scrollPane3.setViewportView(txtPackungMessage);
                                    }
                                    panel14.add(scrollPane3, CC.xy(1, 1, CC.FILL, CC.FILL));

                                    //======== panel3 ========
                                    {
                                        panel3.setLayout(new FormLayout(
                                            "default, default:grow",
                                            "default"));

                                        //---- btnKeinePackung ----
                                        btnKeinePackung.setText("ausblenden");
                                        btnKeinePackung.setFont(new Font("sansserif", Font.PLAIN, 16));
                                        btnKeinePackung.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/player_rev.png")));
                                        btnKeinePackung.addActionListener(new ActionListener() {
                                            @Override
                                            public void actionPerformed(ActionEvent e) {
                                                btnKeinePackungActionPerformed(e);
                                            }
                                        });
                                        panel3.add(btnKeinePackung, CC.xy(1, 1));

                                        //---- btnPackungWeiter ----
                                        btnPackungWeiter.setText("Weiter");
                                        btnPackungWeiter.setFont(new Font("sansserif", Font.PLAIN, 16));
                                        btnPackungWeiter.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/1rightarrow.png")));
                                        btnPackungWeiter.setActionCommand("Weiter");
                                        btnPackungWeiter.addActionListener(new ActionListener() {
                                            @Override
                                            public void actionPerformed(ActionEvent e) {
                                                btnPackungWeiterActionPerformed(e);
                                            }
                                        });
                                        panel3.add(btnPackungWeiter, CC.xy(2, 1, CC.FILL, CC.FILL));
                                    }
                                    panel14.add(panel3, CC.xy(1, 3, CC.FILL, CC.DEFAULT));
                                }
                                pnlPackung.add(panel14, CC.xywh(1, 11, 5, 1, CC.DEFAULT, CC.FILL));
                            }
                            split3.setRightComponent(pnlPackung);
                        }
                        split2.setRightComponent(split3);
                    }
                    split1.setRightComponent(split2);
                }
                splitEditSummary.setTopComponent(split1);

                //======== scrollPane4 ========
                {

                    //---- txtSummary ----
                    txtSummary.setContentType("text/html");
                    txtSummary.setEditable(false);
                    scrollPane4.setViewportView(txtSummary);
                }
                splitEditSummary.setBottomComponent(scrollPane4);
            }
            panel11.add(splitEditSummary, CC.xy(1, 3, CC.FILL, CC.FILL));

            //======== panel4 ========
            {
                panel4.setLayout(new FormLayout(
                    "default:grow, $lcgap, default:grow",
                    "default:grow"));

                //---- btnCheckSave ----
                btnCheckSave.setText("Eingaben pr\u00fcfen");
                btnCheckSave.setFont(new Font("sansserif", Font.PLAIN, 18));
                btnCheckSave.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/viewmag.png")));
                btnCheckSave.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnCheckSaveActionPerformed(e);
                    }
                });
                panel4.add(btnCheckSave, CC.xy(1, 1));

                //---- btnCancelBack ----
                btnCancelBack.setText("Abbrechen");
                btnCancelBack.setFont(new Font("sansserif", Font.PLAIN, 16));
                btnCancelBack.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/button_cancel.png")));
                btnCancelBack.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnCancelBackActionPerformed(e);
                    }
                });
                panel4.add(btnCancelBack, CC.xy(3, 1));
            }
            panel11.add(panel4, CC.xy(1, 5, CC.FILL, CC.DEFAULT));
        }
        add(panel11);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private void initPanel() {

        if (template != null && !template.isEmpty()){
            pzn = MedPackungTools.parsePZN(template);
            if (pzn != null){
                txtPZN.setText(pzn);
            } else {
                txtProd.setText(template.trim());
            }
        }

        splitEditorSummaryPos = SYSTools.showSide(splitEditSummary, SYSTools.LEFT_UPPER_SIDE);
        split1pos = SYSTools.showSide(split1, SYSTools.LEFT_UPPER_SIDE);
        split2pos = SYSTools.showSide(split2, SYSTools.LEFT_UPPER_SIDE);
        split3pos = SYSTools.showSide(split3, SYSTools.LEFT_UPPER_SIDE);
        splitProdPos = SYSTools.showSide(splitProd, SYSTools.LEFT_UPPER_SIDE);
        splitZusatzPos = SYSTools.showSide(splitZusatz, SYSTools.LEFT_UPPER_SIDE);
        splitHerstellerPos = SYSTools.showSide(splitHersteller, SYSTools.LEFT_UPPER_SIDE);

        EntityManager em = OPDE.createEM();
        Query query1 = em.createNamedQuery("MedFormen.findAll");
        cmbForm.setModel(new DefaultComboBoxModel(query1.getResultList().toArray(new MedFormen[]{})));
        cmbForm.setRenderer(MedFormenTools.getMedFormenRenderer(30));
        Query query2 = em.createNamedQuery("MedHersteller.findAll");
        cmbHersteller.setModel(new DefaultComboBoxModel(query2.getResultList().toArray(new MedHersteller[]{})));
        cmbHersteller.setRenderer(MedHerstellerTools.getHerstellerRenderer(30));
        em.close();

        cmbGroesse.setModel(new DefaultComboBoxModel(MedPackungTools.GROESSE));

//        thisComponentResized(null);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            OPDE.debug(e);
        }

        thisComponentResized(null);

    }

    private void revertToPanel(int num) {
        if (num < 4 && split3pos != 1d) {
            numOfVisibleFrames = 3;
            split3pos = SYSTools.showSide(split3, SYSTools.LEFT_UPPER_SIDE, speedFast, new TimelineCallbackAdapter() {
                @Override
                public void onTimelineStateChanged(Timeline.TimelineState oldState, Timeline.TimelineState newState, float durationFraction, float timelinePosition) {
                    if (newState == Timeline.TimelineState.DONE) {
                        thisComponentResized(null);
                    }
                }
            });
        }

        if (num < 3 && split2pos != 1d) {
            numOfVisibleFrames = 2;
            split1pos = 0.5d;
            split2pos = SYSTools.showSide(split2, SYSTools.LEFT_UPPER_SIDE, speedFast, new TimelineCallbackAdapter() {
                @Override
                public void onTimelineStateChanged(Timeline.TimelineState oldState, Timeline.TimelineState newState, float durationFraction, float timelinePosition) {
                    if (newState == Timeline.TimelineState.DONE) {
                        thisComponentResized(null);
                    }
                }
            });
        }

        if (num < 2 && split1pos != 1d) {

            numOfVisibleFrames = 1;

//            cmbForm.setEnabled(true);

            split1pos = SYSTools.showSide(split1, SYSTools.LEFT_UPPER_SIDE, speedFast, new TimelineCallbackAdapter() {
                @Override
                public void onTimelineStateChanged(Timeline.TimelineState oldState, Timeline.TimelineState newState, float durationFraction, float timelinePosition) {
                    if (newState == Timeline.TimelineState.DONE) {
                        thisComponentResized(null);
                    }
                }
            });
        }
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel11;
    private JLabel lblTop;
    private JSplitPane splitEditSummary;
    private JSplitPane split1;
    private JPanel pnlSplitProd;
    private JSplitPane splitProd;
    private JPanel panel1;
    private JLabel label1;
    private JPanel panel9;
    private JLabel lblHardDisk;
    private JXSearchField txtProd;
    private JButton btnClearProd;
    private JPanel panel2;
    private JLabel lblProdMsg;
    private JScrollPane scrollPane1;
    private JList lstProd;
    private JButton btnProdWeiter;
    private JSplitPane split2;
    private JPanel pnlSplitZusatz;
    private JSplitPane splitZusatz;
    private JPanel pnlSplitTop;
    private JLabel label2;
    private JPanel panel10;
    private JTextField txtZusatz;
    private JButton btnClearZusatz;
    private JComboBox cmbForm;
    private JPanel pnlSplitBottom;
    private JLabel lblZusatzMsg;
    private JScrollPane scrollPane2;
    private JList lstZusatz;
    private JButton btnZusatzWeiter;
    private JSplitPane split3;
    private JPanel pnlSplitHersteller;
    private JSplitPane splitHersteller;
    private JPanel panel7;
    private JLabel label7;
    private JPanel panel8;
    private JComboBox cmbHersteller;
    private JButton btnNewHersteller;
    private JPanel panel6;
    private JLabel jLabel1;
    private JLabel lblFirma;
    private JLabel jLabel3;
    private JLabel lblOrt;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JTextField txtPLZ;
    private JTextField txtFirma;
    private JTextField txtStrasse;
    private JTextField txtTel;
    private JTextField txtFax;
    private JTextField txtWWW;
    private JTextField txtOrt;
    private JButton btnHerstellerWeiter;
    private JPanel pnlPackung;
    private JLabel label4;
    private JLabel lblPZN;
    private JTextField txtPZN;
    private JLabel label6;
    private JComboBox cmbGroesse;
    private JLabel lblInhalt;
    private JTextField txtInhalt;
    private JLabel lblPackEinheit;
    private JPanel panel14;
    private JScrollPane scrollPane3;
    private JTextPane txtPackungMessage;
    private JPanel panel3;
    private JButton btnKeinePackung;
    private JButton btnPackungWeiter;
    private JScrollPane scrollPane4;
    private JTextPane txtSummary;
    private JPanel panel4;
    private JButton btnCheckSave;
    private JButton btnCancelBack;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
