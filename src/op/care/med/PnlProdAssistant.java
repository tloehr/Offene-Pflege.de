/*
 * Created by JFormDesigner on Tue Jan 10 16:17:36 CET 2012
 */

package op.care.med;

import javax.swing.border.*;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.verordnungen.MedProdukte;
import entity.verordnungen.MedProdukteTools;
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
import org.jdesktop.swingx.*;
import org.pushingpixels.trident.Timeline;

/**
 * @author Torsten Löhr
 */
public class PnlProdAssistant extends JPanel {
    private double split1pos, split2pos, split3pos, splitProdPos, splitZusatzPos, splitPackungPos, splitHerstellerPos;
    private static int speedSlow = 700;
    private static int speedFast = 500;
    private List<MedProdukte> listProd;

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
            // NO
        } else {
            Object selectedObject = lstProd.getModel().getElementAt(lstProd.getSelectedIndex());
            OPDE.debug(selectedObject); // muss das MedProdukt sein, dass wir verwenden will
        }

    }

    private void thisComponentResized(ComponentEvent e) {




        SYSTools.showSide(splitZusatz, splitZusatzPos);
        SYSTools.showSide(splitPackung, splitPackungPos);
        SYSTools.showSide(splitHersteller, splitHerstellerPos);

        OPDE.debug(split1pos);
        OPDE.debug(split2pos);
        OPDE.debug(split3pos);
        OPDE.debug(splitProdPos);
        OPDE.debug(splitZusatzPos);
        OPDE.debug(splitPackungPos);
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
        textField1 = new JTextField();
        btnClearZusatz = new JButton();
        cmbForm = new JComboBox();
        panel4 = new JPanel();
        lblZusatzMsg = new JLabel();
        scrollPane2 = new JScrollPane();
        lstZusatz = new JList();
        split3 = new JSplitPane();
        splitPackung = new JSplitPane();
        panel5 = new JPanel();
        panel6 = new JPanel();
        splitHersteller = new JSplitPane();
        panel7 = new JPanel();
        panel8 = new JPanel();

        //======== this ========
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
                "left:default:grow",
                "fill:default, fill:default:grow, $lgap, default"));

            //---- lblTop ----
            lblTop.setText("text");
            panel11.add(lblTop, CC.xy(1, 1));

            //======== split1 ========
            {
                split1.setDividerLocation(300);
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
                            "default:grow",
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
                    split2.setDividerLocation(250);
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

                                //---- textField1 ----
                                textField1.setFont(new Font("sansserif", Font.PLAIN, 16));
                                panel10.add(textField1);

                                //---- btnClearZusatz ----
                                btnClearZusatz.setBorderPainted(false);
                                btnClearZusatz.setBorder(null);
                                btnClearZusatz.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/bw/button_cancel.png")));
                                panel10.add(btnClearZusatz);
                            }
                            panel3.add(panel10, CC.xy(1, 5));

                            //---- cmbForm ----
                            cmbForm.setFont(new Font("sansserif", Font.PLAIN, 18));
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
                                scrollPane2.setViewportView(lstZusatz);
                            }
                            panel4.add(scrollPane2, CC.xy(1, 3, CC.DEFAULT, CC.FILL));
                        }
                        splitZusatz.setBottomComponent(panel4);
                    }
                    split2.setLeftComponent(splitZusatz);

                    //======== split3 ========
                    {
                        split3.setDividerLocation(250);
                        split3.addComponentListener(new ComponentAdapter() {
                            @Override
                            public void componentResized(ComponentEvent e) {
                                split3ComponentResized(e);
                            }
                        });

                        //======== splitPackung ========
                        {
                            splitPackung.setOrientation(JSplitPane.VERTICAL_SPLIT);

                            //======== panel5 ========
                            {
                                panel5.setLayout(new FormLayout(
                                    "default, $lcgap, default",
                                    "2*(default, $lgap), default"));
                            }
                            splitPackung.setTopComponent(panel5);

                            //======== panel6 ========
                            {
                                panel6.setLayout(new FormLayout(
                                    "default, $lcgap, default",
                                    "2*(default, $lgap), default"));
                            }
                            splitPackung.setBottomComponent(panel6);
                        }
                        split3.setLeftComponent(splitPackung);

                        //======== splitHersteller ========
                        {
                            splitHersteller.setOrientation(JSplitPane.VERTICAL_SPLIT);

                            //======== panel7 ========
                            {
                                panel7.setLayout(new FormLayout(
                                    "default, $lcgap, default",
                                    "2*(default, $lgap), default"));
                            }
                            splitHersteller.setTopComponent(panel7);

                            //======== panel8 ========
                            {
                                panel8.setLayout(new FormLayout(
                                    "default, $lcgap, default",
                                    "2*(default, $lgap), default"));
                            }
                            splitHersteller.setBottomComponent(panel8);
                        }
                        split3.setRightComponent(splitHersteller);
                    }
                    split2.setRightComponent(split3);
                }
                split1.setRightComponent(split2);
            }
            panel11.add(split1, CC.xy(1, 2));
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
        splitPackungPos = SYSTools.showSide(splitPackung, SYSTools.LEFT_UPPER_SIDE);
        splitHerstellerPos = SYSTools.showSide(splitHersteller, SYSTools.LEFT_UPPER_SIDE);
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
    private JTextField textField1;
    private JButton btnClearZusatz;
    private JComboBox cmbForm;
    private JPanel panel4;
    private JLabel lblZusatzMsg;
    private JScrollPane scrollPane2;
    private JList lstZusatz;
    private JSplitPane split3;
    private JSplitPane splitPackung;
    private JPanel panel5;
    private JPanel panel6;
    private JSplitPane splitHersteller;
    private JPanel panel7;
    private JPanel panel8;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
