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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

/**
 * @author Torsten Löhr
 */
public class PnlProdAssistant extends JPanel {
    private double split1pos, split2pos, split3pos, splitProdPos, splitZusatzPos, splitHerstellerPos;
    private static int speedSlow = 700;
    private static int speedFast = 500;
    private MedProdukte produkt;
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

            DefaultListModel listModelProd;

            if (!listProd.isEmpty()) {

                if (splitProdPos == 1d) {
                    splitProdPos = SYSTools.showSide(splitProd, 0.3d, speedFast);
                }

                listModelProd = SYSTools.list2dlm(listProd);
                lblProdMsg.setText("Es gibt bereits Medikamente, die so ähnlich heissen. Ist es vielleicht eins von diesen ?");
                listModelProd.addElement("<html><b>Nein, das gew&uuml;nschte Medikament ist nicht dabei. Ich m&ouml;chte ein neues eingeben.</b></html>");
                lstProd.setModel(listModelProd);
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
    }

    private void lstProdValueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() || lstProd.getSelectedIndex() < 0) {
            return;
        }

        if (split1pos == 1d) {
            split1pos = SYSTools.showSide(split1, 0.5d, speedFast);
        }

        // letzte Zeile ist immer die "NEIN" Antwort des Anwenders
        if (lstProd.getSelectedIndex() == lstProd.getModel().getSize()) {
            produkt = null;
        } else {
            produkt = (MedProdukte) lstProd.getModel().getElementAt(lstProd.getSelectedIndex());
            OPDE.debug(produkt); // muss das MedProdukt sein, dass wir verwenden wollen
        }

    }

    private void thisComponentResized(ComponentEvent e) {

        OPDE.debug("PnlProdAssistant resized to: width="+this.getWidth() +" and heigth="+this.getHeight());

        split1ComponentResized(e);
        split2ComponentResized(e);
        split3ComponentResized(e);
        splitProdComponentResized(e);
        SYSTools.showSide(splitZusatz, splitZusatzPos);

        SYSTools.showSide(splitHersteller, splitHerstellerPos);

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

    private void txtZusatzCaretUpdate(CaretEvent e) {
        // Eine Prüfung von vorhandenen Zusätzen ist nur Nötig,
        // wenn wir uns auf ein bereits bestehendes Medikament beziehen.
        String zusatz = txtZusatz.getText();
        if (produkt != null && !zusatz.isEmpty()) {
            zusatz = "%" + zusatz + "%";
            EntityManager em = OPDE.createEM();
            Query query1 = em.createQuery("SELECT d FROM Darreichung d WHERE d.medProdukt = :produkt AND d.medForm = :form AND d.zusatz LIKE :zusatz");
            query1.setParameter("produkt", produkt);
            query1.setParameter("form", cmbForm.getSelectedItem());
            query1.setParameter("zusatz", zusatz);
            listZusatz = query1.getResultList();
            lstZusatz.setModel(SYSTools.list2dlm(listZusatz));
            em.close();

            if (!listZusatz.isEmpty()) {
                if (splitZusatzPos == 1d) {
                    splitZusatzPos = SYSTools.showSide(splitZusatz, 0.3d, speedFast);
                }
            }


        } else {
            lstZusatz.setModel(new DefaultComboBoxModel());
            listZusatz = null;
            if (splitZusatzPos != 1d) {
                splitZusatzPos = SYSTools.showSide(splitZusatz, SYSTools.LEFT_UPPER_SIDE, speedFast);
            }
        }
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
        panel4 = new JPanel();
        lblZusatzMsg = new JLabel();
        scrollPane2 = new JScrollPane();
        lstZusatz = new JList();
        split3 = new JSplitPane();
        panel5 = new JPanel();
        label4 = new JLabel();
        label3 = new JLabel();
        txtPZN = new JTextField();
        label6 = new JLabel();
        comboBox1 = new JComboBox();
        label5 = new JLabel();
        txtInhalt = new JTextField();
        toggleButton1 = new JToggleButton();
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
                "default",
                "fill:default, fill:default:grow, $lgap, default"));

            //---- lblTop ----
            lblTop.setText("text");
            lblTop.setFont(new Font("sansserif", Font.PLAIN, 18));
            lblTop.setBackground(new Color(255, 0, 51));
            lblTop.setOpaque(true);
            lblTop.setForeground(new Color(255, 255, 51));
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
                    splitProd.addComponentListener(new ComponentAdapter() {
                        @Override
                        public void componentResized(ComponentEvent e) {
                            splitProdComponentResized(e);
                        }
                    });

                    //======== panel1 ========
                    {
                        panel1.setLayout(new FormLayout(
                            "default",
                            "$rgap, 2*($lgap, default)"));

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
                    }
                    splitProd.setTopComponent(panel1);

                    //======== panel2 ========
                    {
                        panel2.setLayout(new FormLayout(
                            "default:grow",
                            "default, $lgap, default:grow"));

                        //---- lblProdMsg ----
                        lblProdMsg.setFont(new Font("sansserif", Font.BOLD, 16));
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

                        //======== panel3 ========
                        {
                            panel3.setLayout(new FormLayout(
                                "default:grow",
                                "$rgap, 3*($lgap, default)"));

                            //---- label2 ----
                            label2.setText("Zusatzbezeichnung");
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
                        }
                        splitZusatz.setTopComponent(panel3);

                        //======== panel4 ========
                        {
                            panel4.setLayout(new FormLayout(
                                "default:grow",
                                "default, $lgap, default:grow"));

                            //---- lblZusatzMsg ----
                            lblZusatzMsg.setFont(new Font("sansserif", Font.BOLD, 16));
                            panel4.add(lblZusatzMsg, CC.xy(1, 1));

                            //======== scrollPane2 ========
                            {

                                //---- lstZusatz ----
                                lstZusatz.setFont(new Font("sansserif", Font.PLAIN, 16));
                                scrollPane2.setViewportView(lstZusatz);
                            }
                            panel4.add(scrollPane2, CC.xy(1, 3, CC.DEFAULT, CC.FILL));
                        }
                        splitZusatz.setBottomComponent(panel4);
                    }
                    split2.setLeftComponent(splitZusatz);

                    //======== split3 ========
                    {
                        split3.setDividerLocation(300);
                        split3.addComponentListener(new ComponentAdapter() {
                            @Override
                            public void componentResized(ComponentEvent e) {
                                split3ComponentResized(e);
                            }
                        });

                        //======== panel5 ========
                        {
                            panel5.setLayout(new FormLayout(
                                "default, $lcgap, default:grow",
                                "$rgap, 5*($lgap, default)"));

                            //---- label4 ----
                            label4.setText("Verpackung");
                            label4.setFont(new Font("sansserif", Font.PLAIN, 18));
                            label4.setBackground(new Color(255, 255, 204));
                            label4.setOpaque(true);
                            label4.setForeground(Color.black);
                            label4.setHorizontalAlignment(SwingConstants.CENTER);
                            panel5.add(label4, CC.xywh(1, 3, 3, 1));

                            //---- label3 ----
                            label3.setText("PZN");
                            label3.setFont(new Font("sansserif", Font.PLAIN, 16));
                            panel5.add(label3, CC.xy(1, 5));
                            panel5.add(txtPZN, CC.xy(3, 5));

                            //---- label6 ----
                            label6.setText("Gr\u00f6\u00dfe");
                            label6.setFont(new Font("sansserif", Font.PLAIN, 16));
                            panel5.add(label6, CC.xy(1, 7));

                            //---- comboBox1 ----
                            comboBox1.setFont(new Font("sansserif", Font.PLAIN, 16));
                            panel5.add(comboBox1, CC.xy(3, 7));

                            //---- label5 ----
                            label5.setText("Inhalt");
                            label5.setFont(new Font("sansserif", Font.PLAIN, 16));
                            panel5.add(label5, CC.xy(1, 9));

                            //---- txtInhalt ----
                            txtInhalt.setFont(new Font("sansserif", Font.PLAIN, 16));
                            panel5.add(txtInhalt, CC.xy(3, 9));

                            //---- toggleButton1 ----
                            toggleButton1.setText("Keine Packung eintragen");
                            toggleButton1.setFont(new Font("sansserif", Font.PLAIN, 16));
                            panel5.add(toggleButton1, CC.xywh(1, 11, 3, 1));
                        }
                        split3.setLeftComponent(panel5);

                        //======== splitHersteller ========
                        {
                            splitHersteller.setOrientation(JSplitPane.VERTICAL_SPLIT);
                            splitHersteller.setDividerLocation(200);

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
                                panel7.add(cmbHersteller, CC.xy(1, 5));
                            }
                            splitHersteller.setTopComponent(panel7);

                            //======== panel6 ========
                            {
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
                                txtPLZ.setText("jTextField1");
                                txtPLZ.setFont(new Font("sansserif", Font.PLAIN, 16));
                                panel6.add(txtPLZ, CC.xy(3, 9));

                                //---- txtFirma ----
                                txtFirma.setText("jTextField2");
                                txtFirma.setFont(new Font("sansserif", Font.PLAIN, 16));
                                panel6.add(txtFirma, CC.xywh(3, 5, 3, 1));

                                //---- txtStrasse ----
                                txtStrasse.setText("jTextField3");
                                txtStrasse.setFont(new Font("sansserif", Font.PLAIN, 16));
                                panel6.add(txtStrasse, CC.xywh(3, 7, 3, 1));

                                //---- txtTel ----
                                txtTel.setText("jTextField4");
                                txtTel.setFont(new Font("sansserif", Font.PLAIN, 16));
                                panel6.add(txtTel, CC.xywh(3, 11, 3, 1));

                                //---- txtFax ----
                                txtFax.setText("jTextField5");
                                txtFax.setFont(new Font("sansserif", Font.PLAIN, 16));
                                panel6.add(txtFax, CC.xywh(3, 13, 3, 1));

                                //---- txtWWW ----
                                txtWWW.setText("jTextField6");
                                txtWWW.setFont(new Font("sansserif", Font.PLAIN, 16));
                                panel6.add(txtWWW, CC.xywh(3, 15, 3, 1));

                                //---- txtOrt ----
                                txtOrt.setText("jTextField2");
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
            panel11.add(split1, CC.xy(1, 2));

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
        splitZusatzPos = SYSTools.showSide(splitZusatz, SYSTools.LEFT_UPPER_SIDE);
        splitHerstellerPos = SYSTools.showSide(splitHersteller, SYSTools.LEFT_UPPER_SIDE);

        EntityManager em = OPDE.createEM();
        Query query1 = em.createNamedQuery("MedFormen.findAll");
        cmbForm.setModel(new DefaultComboBoxModel(query1.getResultList().toArray(new MedFormen[]{})));
        cmbForm.setRenderer(MedFormenTools.getMedFormenRenderer());
        Query query2 = em.createNamedQuery("MedHersteller.findAll");
        cmbHersteller.setModel(new DefaultComboBoxModel(query2.getResultList().toArray(new MedHersteller[]{})));
        cmbHersteller.setRenderer(MedHerstellerTools.getHerstellerRenderer());
        em.close();

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
    private JPanel panel4;
    private JLabel lblZusatzMsg;
    private JScrollPane scrollPane2;
    private JList lstZusatz;
    private JSplitPane split3;
    private JPanel panel5;
    private JLabel label4;
    private JLabel label3;
    private JTextField txtPZN;
    private JLabel label6;
    private JComboBox comboBox1;
    private JLabel label5;
    private JTextField txtInhalt;
    private JToggleButton toggleButton1;
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
