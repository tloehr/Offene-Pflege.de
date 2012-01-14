/*
 * Created by JFormDesigner on Tue Jan 10 16:17:36 CET 2012
 */

package op.care.med;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.verordnungen.*;
import op.OPDE;
import op.tools.SYSTools;

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

/**
 * @author Torsten Löhr
 */
public class PnlProdAssistant extends JPanel {
    private double split1pos, split2pos, split3pos, splitProdPos, splitZusatzPos, splitHerstellerPos, splitPackungPos;
    private static int speedSlow = 700;
    private static int speedFast = 500;
    private MedProdukte produkt;
    private Darreichung darreichung;
    private MedPackung packung;
    private MedHersteller hersteller;
    private List<MedProdukte> listProd;
    private List<Darreichung> listZusatz;

    public PnlProdAssistant() {
        initComponents();
        initPanel();
    }

    private void txtProdCaretUpdate(CaretEvent e) {
        revertToPanel(1);

        if (!txtProd.getText().trim().isEmpty()) {
            EntityManager em = OPDE.createEM();
            Query query = em.createNamedQuery("MedProdukte.findByBezeichnungLike");
            query.setParameter("bezeichnung", "%" + txtProd.getText().trim() + "%");
            listProd = query.getResultList();
            em.close();

            DefaultListModel lmProd;

            if (!listProd.isEmpty()) {

                if (splitProdPos == 1d) {
                    splitProdPos = SYSTools.showSide(splitProd, 0.3d, speedFast);
                }

                lmProd = SYSTools.list2dlm(listProd);
//                lblProdMsg.setText("Es gibt bereits Medikamente, die so ähnlich heissen. Ist es vielleicht eins von diesen ?");
//                lmProd.addElement("<html><i><b>Nein, das gew&uuml;nschte Medikament ist nicht dabei. Ich m&ouml;chte ein neues eingeben.</b></i></html>");
                lstProd.setModel(lmProd);
                lstProd.setCellRenderer(MedProdukteTools.getMedProdukteRenderer());
            } else {
                if (splitProdPos != 1d) {
                    splitProdPos = SYSTools.showSide(splitProd, SYSTools.LEFT_UPPER_SIDE, speedFast);
                }
            }

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

    private void lstProdValueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() || lstProd.getSelectedIndex() < 0) {
            return;
        }

        if (split1pos == 1d) {
            split1pos = SYSTools.showSide(split1, 0.5d, speedFast);
        }

        darreichung = null;
        packung = null;
        hersteller = null;
        produkt = (MedProdukte) lstProd.getModel().getElementAt(lstProd.getSelectedIndex());

        showLabelTop();
        txtZusatzCaretUpdate(null);
        initZusatz();
    }

    private void showLabelTop() {
        String top = "";
        top += produkt == null ? "" : produkt.getBezeichnung();
        top += darreichung == null ? "" : " " + darreichung.getZusatz();
        top += packung == null ? "" : ", " + MedPackungTools.toPrettyString(packung);
        top += hersteller == null ? "" : ", " + hersteller.getFirma() + ", " + hersteller.getOrt();
        lblTop.setText(top);
    }

    private void thisComponentResized(ComponentEvent e) {

        OPDE.debug("PnlProdAssistant resized to: width=" + this.getWidth() + " and heigth=" + this.getHeight());

        split1ComponentResized(e);
        split2ComponentResized(e);
        split3ComponentResized(e);
        splitProdComponentResized(e);
        splitZusatzComponentResized(e);
        splitPackungComponentResized(e);
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
                    splitZusatzPos = SYSTools.showSide(splitZusatz, 0.3d, speedFast);
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
        }
        txtZusatz.requestFocus();
    }

    private void txtZusatzCaretUpdate(CaretEvent e) {
        revertToPanel(2);
        packung = null;
    }

    private void btnPackungEintragenItemStateChanged(ItemEvent e) {
        if (btnPackungEintragen.isSelected()) { // Packung eintragen
            splitPackungPos = SYSTools.showSide(splitPackung, 0.9d, speedFast);
            txtPZN.setText("");
            txtInhalt.setText("1");
            pnlPackLower.remove(btnPackungEintragen);
            pnlPackLower.add(btnPackungEintragen, BorderLayout.PAGE_END);
            btnPackungEintragen.setText("Keine Packung eintragen.");
            lblPackEinheit.setText(MedFormenTools.EINHEIT[darreichung.getMedForm().getPackEinheit()]);
        } else { // Keine Packung eintragen
            splitPackungPos = SYSTools.showSide(splitPackung, SYSTools.RIGHT_LOWER_SIDE, speedFast);
            btnPackungEintragen.setText("Packung eintragen.");
            pnlPackLower.remove(btnPackungEintragen);
            pnlPackLower.add(btnPackungEintragen, BorderLayout.PAGE_START);

        }
        packung = null;
    }

    private void lstZusatzValueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() || lstZusatz.getSelectedIndex() < 0) {
            return;
        }

        if (split3pos == 1d) {
            split3pos = SYSTools.showSide(split3, 0.5d, speedFast);
        }

        if (split2pos == 1d) {
            split2pos = SYSTools.showSide(split2, 0.33d, speedFast);
        }

        if (split1pos == 0.5d) {
            split1pos = SYSTools.showSide(split1, 0.25d, speedFast);
        }

        darreichung = (Darreichung) lstZusatz.getModel().getElementAt(lstZusatz.getSelectedIndex());

        showLabelTop();
        btnPackungEintragenItemStateChanged(null);
        thisComponentResized(null);
    }

    private void splitPackungComponentResized(ComponentEvent e) {
        SYSTools.showSide(splitPackung, splitPackungPos);
    }

    private void btnCheckPackungActionPerformed(ActionEvent e) {
        String pzn = MedPackungTools.checkNewPZN(txtPZN.getText().trim());
        BigDecimal inhalt = SYSTools.parseBigDecinal(txtInhalt.getText());

        if (pzn == null) {
            lblPZN.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/editdelete.png")));
        } else {
            lblPZN.setIcon(null);
        }

        if (inhalt == null) {
            lblInhalt.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/editdelete.png")));
        } else {
            lblInhalt.setIcon(null);
        }

        if (pzn != null && inhalt != null) {
            packung = new MedPackung(darreichung);
            packung.setPzn(pzn);
            packung.setInhalt(inhalt);
            packung.setGroesse((short) cmbGroesse.getSelectedIndex());
        } else {
            packung = null;
        }
        showLabelTop();
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
        if (produkt != null || txtProd.getText().trim().isEmpty()) {
            return;
        }
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT m FROM MedProdukte m WHERE m.bezeichnung = :bezeichnung");
        query.setParameter("bezeichnung", txtProd.getText().trim());
        List<MedProdukte> listeProdukte = query.getResultList();
        em.close();
        // Wenn es eine genaue Übereinstimmung schon gab, dann reden wir nicht lange sondern verwenden diese direkt.
        produkt = listeProdukte.isEmpty() ? new MedProdukte(null, txtProd.getText().trim()) : listeProdukte.get(0);

        if (split1pos == 1d) {
            split1pos = SYSTools.showSide(split1, 0.5d, speedFast);
        }

        darreichung = null;
        packung = null;
        hersteller = null;

        showLabelTop();
        initZusatz();
    }

    private void btnZusatzWeiterActionPerformed(ActionEvent e) {
        // Wenn die Darreichung schon gewählt wurde, gar nix machen
        // wenn nicht, dann darf nicht auch noch das Textfeld leer sein.
        if (darreichung != null || txtZusatz.getText().trim().isEmpty()) {
            return;
        }
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT m FROM Darreichung m WHERE m.zusatz = :zusatz and m.medProdukt = :produkt and m.medForm = :form");
        query.setParameter("zusatz", txtZusatz.getText().trim());
        query.setParameter("produkt", produkt);
        query.setParameter("form", cmbForm.getSelectedItem());
        List<Darreichung> listeDarreichungen = query.getResultList();
        em.close();

        // Wenn es eine genaue Übereinstimmung schon gab, dann reden wir nicht lange sondern verwenden diese direkt.
        darreichung = listeDarreichungen.isEmpty() ? new Darreichung(produkt, txtZusatz.getText().trim(), (MedFormen) cmbForm.getSelectedItem()) : listeDarreichungen.get(0);
        packung = null;

        if (split3pos == 1d) {
            split3pos = SYSTools.showSide(split3, 0.5d, speedFast);
        }

        if (split2pos == 1d) {
            split2pos = SYSTools.showSide(split2, 0.33d, speedFast);
        }

        if (split1pos == 0.5d) {
            split1pos = SYSTools.showSide(split1, 0.25d, speedFast);
        }

        btnPackungEintragenItemStateChanged(null);
        hersteller = (MedHersteller) cmbHersteller.getSelectedItem();
        showLabelTop();
        thisComponentResized(null);
    }

    private void cmbHerstellerItemStateChanged(ItemEvent e) {
        hersteller = (MedHersteller) cmbHersteller.getSelectedItem();
        showLabelTop();
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel11 = new JPanel();
        lblTop = new JLabel();
        split1 = new JSplitPane();
        splitProd = new JSplitPane();
        panel1 = new JPanel();
        label1 = new JLabel();
        panel9 = new JPanel();
        txtProd = new JTextField();
        btnClearProd = new JButton();
        panel12 = new JPanel();
        btnProdWeiter = new JButton();
        panel2 = new JPanel();
        lblProdMsg = new JLabel();
        scrollPane1 = new JScrollPane();
        lstProd = new JList();
        split2 = new JSplitPane();
        splitZusatz = new JSplitPane();
        panel3 = new JPanel();
        label2 = new JLabel();
        panel10 = new JPanel();
        txtZusatz = new JTextField();
        btnClearZusatz = new JButton();
        cmbForm = new JComboBox();
        panel13 = new JPanel();
        btnZusatzWeiter = new JButton();
        panel4 = new JPanel();
        lblZusatzMsg = new JLabel();
        scrollPane2 = new JScrollPane();
        lstZusatz = new JList();
        split3 = new JSplitPane();
        splitPackung = new JSplitPane();
        panel5 = new JPanel();
        label4 = new JLabel();
        lblPZN = new JLabel();
        txtPZN = new JTextField();
        label6 = new JLabel();
        cmbGroesse = new JComboBox();
        lblInhalt = new JLabel();
        txtInhalt = new JTextField();
        lblPackEinheit = new JLabel();
        panel14 = new JPanel();
        btnCheckPackung = new JButton();
        pnlPackLower = new JPanel();
        btnPackungEintragen = new JToggleButton();
        splitHersteller = new JSplitPane();
        panel7 = new JPanel();
        label7 = new JLabel();
        cmbHersteller = new JComboBox();
        panel6 = new JPanel();
        jLabel1 = new JLabel();
        jSeparator1 = new JSeparator();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        jLabel4 = new JLabel();
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
        button1 = new JButton();

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
                "fill:default, fill:default:grow, $lgap, default"));

            //---- lblTop ----
            lblTop.setFont(new Font("sansserif", Font.PLAIN, 18));
            lblTop.setBackground(new Color(255, 204, 204));
            lblTop.setOpaque(true);
            lblTop.setForeground(new Color(153, 0, 51));
            lblTop.setHorizontalAlignment(SwingConstants.CENTER);
            panel11.add(lblTop, CC.xy(1, 1));

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
                            "$rgap, 2*($lgap, default), $lgap, 30px:grow"));

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

                            //---- txtProd ----
                            txtProd.setFont(new Font("sansserif", Font.PLAIN, 16));
                            txtProd.addCaretListener(new CaretListener() {
                                @Override
                                public void caretUpdate(CaretEvent e) {
                                    txtProdCaretUpdate(e);
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

                        //======== panel12 ========
                        {
                            panel12.setLayout(new BorderLayout());

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
                            panel12.add(btnProdWeiter, BorderLayout.PAGE_END);
                        }
                        panel1.add(panel12, CC.xy(1, 7, CC.DEFAULT, CC.FILL));
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
                split1.setLeftComponent(splitProd);

                //======== split2 ========
                {
                    split2.setDividerLocation(400);
                    split2.setMinimumSize(new Dimension(200, 372));
                    split2.setPreferredSize(new Dimension(200, 372));
                    split2.setDividerSize(0);
                    split2.addComponentListener(new ComponentAdapter() {
                        @Override
                        public void componentResized(ComponentEvent e) {
                            split2ComponentResized(e);
                        }
                    });

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

                        //======== panel3 ========
                        {
                            panel3.setMinimumSize(new Dimension(83, 93));
                            panel3.setLayout(new FormLayout(
                                "default:grow",
                                "$rgap, 3*($lgap, default), $lgap, default:grow"));

                            //---- label2 ----
                            label2.setText("Zusatz und Darreichung");
                            label2.setFont(new Font("sansserif", Font.PLAIN, 18));
                            label2.setBackground(new Color(255, 204, 255));
                            label2.setOpaque(true);
                            label2.setForeground(Color.black);
                            label2.setHorizontalAlignment(SwingConstants.CENTER);
                            panel3.add(label2, CC.xy(1, 3));

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
                            panel3.add(panel10, CC.xy(1, 5));

                            //---- cmbForm ----
                            cmbForm.setFont(new Font("sansserif", Font.PLAIN, 16));
                            panel3.add(cmbForm, CC.xy(1, 7));

                            //======== panel13 ========
                            {
                                panel13.setLayout(new BorderLayout());

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
                                panel13.add(btnZusatzWeiter, BorderLayout.PAGE_END);
                            }
                            panel3.add(panel13, CC.xy(1, 9, CC.DEFAULT, CC.FILL));
                        }
                        splitZusatz.setTopComponent(panel3);

                        //======== panel4 ========
                        {
                            panel4.setMinimumSize(new Dimension(83, 93));
                            panel4.setPreferredSize(new Dimension(83, 93));
                            panel4.setLayout(new FormLayout(
                                "default:grow",
                                "default, $lgap, default:grow"));

                            //---- lblZusatzMsg ----
                            lblZusatzMsg.setFont(new Font("sansserif", Font.BOLD, 16));
                            lblZusatzMsg.setText("Zu diesem Produkt gibt es schon Darreichungen");
                            panel4.add(lblZusatzMsg, CC.xy(1, 1));

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
                            panel4.add(scrollPane2, CC.xy(1, 3, CC.DEFAULT, CC.FILL));
                        }
                        splitZusatz.setBottomComponent(panel4);
                    }
                    split2.setLeftComponent(splitZusatz);

                    //======== split3 ========
                    {
                        split3.setDividerLocation(400);
                        split3.setDividerSize(0);
                        split3.addComponentListener(new ComponentAdapter() {
                            @Override
                            public void componentResized(ComponentEvent e) {
                                split3ComponentResized(e);
                            }
                        });

                        //======== splitPackung ========
                        {
                            splitPackung.setOrientation(JSplitPane.VERTICAL_SPLIT);
                            splitPackung.setDividerSize(0);
                            splitPackung.setDividerLocation(300);
                            splitPackung.addComponentListener(new ComponentAdapter() {
                                @Override
                                public void componentResized(ComponentEvent e) {
                                    splitPackungComponentResized(e);
                                }
                            });

                            //======== panel5 ========
                            {
                                panel5.setMinimumSize(new Dimension(83, 93));
                                panel5.setPreferredSize(new Dimension(83, 93));
                                panel5.setLayout(new FormLayout(
                                    "default, $lcgap, default:grow, $lcgap, default",
                                    "$rgap, 4*($lgap, default), $ugap, default:grow"));

                                //---- label4 ----
                                label4.setText("Verpackung");
                                label4.setFont(new Font("sansserif", Font.PLAIN, 18));
                                label4.setBackground(new Color(255, 255, 204));
                                label4.setOpaque(true);
                                label4.setForeground(Color.black);
                                label4.setHorizontalAlignment(SwingConstants.CENTER);
                                panel5.add(label4, CC.xywh(1, 3, 5, 1));

                                //---- lblPZN ----
                                lblPZN.setText("PZN");
                                lblPZN.setFont(new Font("sansserif", Font.PLAIN, 16));
                                panel5.add(lblPZN, CC.xy(1, 5));

                                //---- txtPZN ----
                                txtPZN.setFont(new Font("sansserif", Font.PLAIN, 16));
                                panel5.add(txtPZN, CC.xywh(3, 5, 3, 1));

                                //---- label6 ----
                                label6.setText("Gr\u00f6\u00dfe");
                                label6.setFont(new Font("sansserif", Font.PLAIN, 16));
                                panel5.add(label6, CC.xy(1, 7));

                                //---- cmbGroesse ----
                                cmbGroesse.setFont(new Font("sansserif", Font.PLAIN, 16));
                                panel5.add(cmbGroesse, CC.xywh(3, 7, 3, 1));

                                //---- lblInhalt ----
                                lblInhalt.setText("Inhalt");
                                lblInhalt.setFont(new Font("sansserif", Font.PLAIN, 16));
                                panel5.add(lblInhalt, CC.xy(1, 9));

                                //---- txtInhalt ----
                                txtInhalt.setFont(new Font("sansserif", Font.PLAIN, 16));
                                panel5.add(txtInhalt, CC.xy(3, 9));

                                //---- lblPackEinheit ----
                                lblPackEinheit.setFont(new Font("sansserif", Font.PLAIN, 16));
                                panel5.add(lblPackEinheit, CC.xy(5, 9));

                                //======== panel14 ========
                                {
                                    panel14.setLayout(new BorderLayout());

                                    //---- btnCheckPackung ----
                                    btnCheckPackung.setText("Pr\u00fcfen");
                                    btnCheckPackung.setFont(new Font("sansserif", Font.PLAIN, 16));
                                    btnCheckPackung.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/viewmag.png")));
                                    btnCheckPackung.setActionCommand("Weiter");
                                    btnCheckPackung.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            btnCheckPackungActionPerformed(e);
                                        }
                                    });
                                    panel14.add(btnCheckPackung, BorderLayout.PAGE_END);
                                }
                                panel5.add(panel14, CC.xywh(1, 11, 5, 1, CC.DEFAULT, CC.FILL));
                            }
                            splitPackung.setTopComponent(panel5);

                            //======== pnlPackLower ========
                            {
                                pnlPackLower.setMinimumSize(new Dimension(83, 93));
                                pnlPackLower.setPreferredSize(new Dimension(83, 93));
                                pnlPackLower.setLayout(new BorderLayout());

                                //---- btnPackungEintragen ----
                                btnPackungEintragen.setText("Keine Packung eintragen");
                                btnPackungEintragen.setFont(new Font("sansserif", Font.PLAIN, 16));
                                btnPackungEintragen.setSelected(true);
                                btnPackungEintragen.addItemListener(new ItemListener() {
                                    @Override
                                    public void itemStateChanged(ItemEvent e) {
                                        btnPackungEintragenItemStateChanged(e);
                                    }
                                });
                                pnlPackLower.add(btnPackungEintragen, BorderLayout.PAGE_END);
                            }
                            splitPackung.setBottomComponent(pnlPackLower);
                        }
                        split3.setLeftComponent(splitPackung);

                        //======== splitHersteller ========
                        {
                            splitHersteller.setOrientation(JSplitPane.VERTICAL_SPLIT);
                            splitHersteller.setDividerLocation(200);
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

                                //---- cmbHersteller ----
                                cmbHersteller.setFont(new Font("sansserif", Font.PLAIN, 16));
                                cmbHersteller.addItemListener(new ItemListener() {
                                    @Override
                                    public void itemStateChanged(ItemEvent e) {
                                        cmbHerstellerItemStateChanged(e);
                                    }
                                });
                                panel7.add(cmbHersteller, CC.xy(1, 5));
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
                                panel6.add(jSeparator1, CC.xywh(1, 3, 5, 1));

                                //---- jLabel2 ----
                                jLabel2.setText("Firma:");
                                jLabel2.setFont(new Font("sansserif", Font.PLAIN, 16));
                                panel6.add(jLabel2, CC.xy(1, 5));

                                //---- jLabel3 ----
                                jLabel3.setText("Strasse:");
                                jLabel3.setFont(new Font("sansserif", Font.PLAIN, 16));
                                panel6.add(jLabel3, CC.xy(1, 7));

                                //---- jLabel4 ----
                                jLabel4.setText("PLZ, Ort:");
                                jLabel4.setFont(new Font("sansserif", Font.PLAIN, 16));
                                panel6.add(jLabel4, CC.xy(1, 9));

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
                        split3.setRightComponent(splitHersteller);
                    }
                    split2.setRightComponent(split3);
                }
                split1.setRightComponent(split2);
            }
            panel11.add(split1, CC.xy(1, 2, CC.FILL, CC.FILL));

            //---- button1 ----
            button1.setText("Fertig, 1.2.3., Fertig");
            button1.setFont(new Font("sansserif", Font.PLAIN, 18));
            panel11.add(button1, CC.xy(1, 4));
        }
        add(panel11);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private void initPanel() {
        split1pos = SYSTools.showSide(split1, SYSTools.LEFT_UPPER_SIDE);
        split2pos = SYSTools.showSide(split2, SYSTools.LEFT_UPPER_SIDE);
        split3pos = SYSTools.showSide(split3, SYSTools.LEFT_UPPER_SIDE);
        splitProdPos = SYSTools.showSide(splitProd, SYSTools.LEFT_UPPER_SIDE);
        splitPackungPos = SYSTools.showSide(splitPackung, 0.3d);
        splitZusatzPos = SYSTools.showSide(splitZusatz, SYSTools.LEFT_UPPER_SIDE);
        splitHerstellerPos = SYSTools.showSide(splitHersteller, SYSTools.LEFT_UPPER_SIDE);

        EntityManager em = OPDE.createEM();
        Query query1 = em.createNamedQuery("MedFormen.findAll");
        cmbForm.setModel(new DefaultComboBoxModel(query1.getResultList().toArray(new MedFormen[]{})));
        cmbForm.setRenderer(MedFormenTools.getMedFormenRenderer(25));
        Query query2 = em.createNamedQuery("MedHersteller.findAll");
        cmbHersteller.setModel(new DefaultComboBoxModel(query2.getResultList().toArray(new MedHersteller[]{})));
        cmbHersteller.setRenderer(MedHerstellerTools.getHerstellerRenderer(25));
        em.close();

        cmbGroesse.setModel(new DefaultComboBoxModel(MedPackungTools.GROESSE));

        thisComponentResized(null);
    }

    private void revertToPanel(int num) {
        if (num < 4 && split3pos != 1d) {
            split3pos = SYSTools.showSide(split3, SYSTools.LEFT_UPPER_SIDE, speedFast);
        }

        if (num < 3 && split2pos != 1d) {
            split2pos = SYSTools.showSide(split2, SYSTools.LEFT_UPPER_SIDE, speedFast);
        }

        if (num < 2 && split1pos != 1d) {
            split1pos = SYSTools.showSide(split1, SYSTools.LEFT_UPPER_SIDE, speedFast);
        }
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel11;
    private JLabel lblTop;
    private JSplitPane split1;
    private JSplitPane splitProd;
    private JPanel panel1;
    private JLabel label1;
    private JPanel panel9;
    private JTextField txtProd;
    private JButton btnClearProd;
    private JPanel panel12;
    private JButton btnProdWeiter;
    private JPanel panel2;
    private JLabel lblProdMsg;
    private JScrollPane scrollPane1;
    private JList lstProd;
    private JSplitPane split2;
    private JSplitPane splitZusatz;
    private JPanel panel3;
    private JLabel label2;
    private JPanel panel10;
    private JTextField txtZusatz;
    private JButton btnClearZusatz;
    private JComboBox cmbForm;
    private JPanel panel13;
    private JButton btnZusatzWeiter;
    private JPanel panel4;
    private JLabel lblZusatzMsg;
    private JScrollPane scrollPane2;
    private JList lstZusatz;
    private JSplitPane split3;
    private JSplitPane splitPackung;
    private JPanel panel5;
    private JLabel label4;
    private JLabel lblPZN;
    private JTextField txtPZN;
    private JLabel label6;
    private JComboBox cmbGroesse;
    private JLabel lblInhalt;
    private JTextField txtInhalt;
    private JLabel lblPackEinheit;
    private JPanel panel14;
    private JButton btnCheckPackung;
    private JPanel pnlPackLower;
    private JToggleButton btnPackungEintragen;
    private JSplitPane splitHersteller;
    private JPanel panel7;
    private JLabel label7;
    private JComboBox cmbHersteller;
    private JPanel panel6;
    private JLabel jLabel1;
    private JSeparator jSeparator1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
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
    private JButton button1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
