/*
 * Created by JFormDesigner on Wed Jul 25 16:15:09 CEST 2012
 */

package op.care.planung.massnahmen;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.InterventionTools;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.HorizontalLayout;
import org.jdesktop.swingx.JXSearchField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Torsten Löhr
 */
public class PnlSelectIntervention extends JPanel {
    private Closure actionBlock;

    public PnlSelectIntervention(Closure actionBlock) {
        this.actionBlock = actionBlock;
        initComponents();
        initPanel();
    }

    private void initPanel() {
        listInterventions.setModel(new DefaultListModel());
    }

    private void txtBezeichnungCaretUpdate(CaretEvent e) {
        // TODO add your code here
    }

    private void txtDauerCaretUpdate(CaretEvent e) {
        // TODO add your code here
    }

    private void cmbKategorieItemStateChanged(ItemEvent e) {
        // TODO add your code here
    }

    private void cmbArtItemStateChanged(ItemEvent e) {
        // TODO add your code here
    }

    private void cbAktivItemStateChanged(ItemEvent e) {
        // TODO add your code here
    }

    private void txtSearchActionPerformed(ActionEvent e) {
        listInterventions.setModel(SYSTools.list2dlm(InterventionTools.findMassnahmenBy(InterventionTools.MASSART_PFLEGE, txtSearch.getText())));
    }

    private void btnAddActionPerformed(ActionEvent e) {
        SYSTools.showSide(split1, SYSTools.RIGHT_LOWER_SIDE, SYSTools.SPEED_NORMAL);
    }

    private void btnBackActionPerformed(ActionEvent e) {
        SYSTools.showSide(split1, SYSTools.LEFT_UPPER_SIDE, SYSTools.SPEED_NORMAL);
    }

    private void btnOkActionPerformed(ActionEvent e) {
        actionBlock.execute(listInterventions.getSelectedValues());
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        split1 = new JSplitPane();
        panel2 = new JPanel();
        txtSearch = new JXSearchField();
        scrollPane1 = new JScrollPane();
        listInterventions = new JList();
        panel3 = new JPanel();
        btnAdd = new JButton();
        panel5 = new JPanel();
        btnOk = new JButton();
        jPanel3 = new JPanel();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        txtBezeichnung = new JTextField();
        txtDauer = new JTextField();
        jLabel3 = new JLabel();
        cmbKategorie = new JComboBox();
        jLabel4 = new JLabel();
        cmbArt = new JComboBox();
        cbAktiv = new JCheckBox();
        panel4 = new JPanel();
        btnBack = new JButton();

        //======== this ========
        setLayout(new BorderLayout());

        //======== panel1 ========
        {
            panel1.setBorder(new EmptyBorder(15, 15, 15, 15));
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

            //======== split1 ========
            {
                split1.setDividerLocation(300);

                //======== panel2 ========
                {
                    panel2.setLayout(new FormLayout(
                        "default:grow",
                        "default, $lgap, default:grow, $lgap, default"));

                    //---- txtSearch ----
                    txtSearch.setFont(new Font("Arial", Font.PLAIN, 14));
                    txtSearch.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            txtSearchActionPerformed(e);
                        }
                    });
                    panel2.add(txtSearch, CC.xy(1, 1));

                    //======== scrollPane1 ========
                    {

                        //---- listInterventions ----
                        listInterventions.setFont(new Font("Arial", Font.PLAIN, 14));
                        scrollPane1.setViewportView(listInterventions);
                    }
                    panel2.add(scrollPane1, CC.xy(1, 3, CC.FILL, CC.FILL));

                    //======== panel3 ========
                    {
                        panel3.setLayout(new BoxLayout(panel3, BoxLayout.X_AXIS));

                        //---- btnAdd ----
                        btnAdd.setText(null);
                        btnAdd.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
                        btnAdd.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnAddActionPerformed(e);
                            }
                        });
                        panel3.add(btnAdd);

                        //======== panel5 ========
                        {
                            panel5.setLayout(new HorizontalLayout());

                            //---- btnOk ----
                            btnOk.setText(null);
                            btnOk.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                            btnOk.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    btnOkActionPerformed(e);
                                }
                            });
                            panel5.add(btnOk);
                        }
                        panel3.add(panel5);
                    }
                    panel2.add(panel3, CC.xy(1, 5, CC.FILL, CC.DEFAULT));
                }
                split1.setLeftComponent(panel2);

                //======== jPanel3 ========
                {
                    jPanel3.setBorder(null);
                    jPanel3.setLayout(new FormLayout(
                        "default, $lcgap, default:grow",
                        "3*(fill:default, $lgap), 2*(default, $lgap), default:grow"));

                    //---- jLabel1 ----
                    jLabel1.setText("Bezeichnung");
                    jLabel1.setFont(new Font("Arial", Font.PLAIN, 14));
                    jPanel3.add(jLabel1, CC.xy(1, 1));

                    //---- jLabel2 ----
                    jLabel2.setText("Dauer");
                    jLabel2.setFont(new Font("Arial", Font.PLAIN, 14));
                    jPanel3.add(jLabel2, CC.xy(1, 3));

                    //---- txtBezeichnung ----
                    txtBezeichnung.setFont(new Font("Arial", Font.PLAIN, 14));
                    txtBezeichnung.addCaretListener(new CaretListener() {
                        @Override
                        public void caretUpdate(CaretEvent e) {
                            txtBezeichnungCaretUpdate(e);
                        }
                    });
                    jPanel3.add(txtBezeichnung, CC.xy(3, 1));

                    //---- txtDauer ----
                    txtDauer.setHorizontalAlignment(SwingConstants.RIGHT);
                    txtDauer.setText("10");
                    txtDauer.setToolTipText("Dauer in Minuten");
                    txtDauer.setFont(new Font("Arial", Font.PLAIN, 14));
                    txtDauer.addCaretListener(new CaretListener() {
                        @Override
                        public void caretUpdate(CaretEvent e) {
                            txtDauerCaretUpdate(e);
                        }
                    });
                    jPanel3.add(txtDauer, CC.xy(3, 3));

                    //---- jLabel3 ----
                    jLabel3.setText("Kategorie");
                    jLabel3.setFont(new Font("Arial", Font.PLAIN, 14));
                    jPanel3.add(jLabel3, CC.xy(1, 5));

                    //---- cmbKategorie ----
                    cmbKategorie.setModel(new DefaultComboBoxModel(new String[] {
                        "Item 1",
                        "Item 2",
                        "Item 3",
                        "Item 4"
                    }));
                    cmbKategorie.setFont(new Font("Arial", Font.PLAIN, 14));
                    cmbKategorie.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            cmbKategorieItemStateChanged(e);
                        }
                    });
                    jPanel3.add(cmbKategorie, CC.xy(3, 5));

                    //---- jLabel4 ----
                    jLabel4.setText("Art");
                    jLabel4.setFont(new Font("Arial", Font.PLAIN, 14));
                    jPanel3.add(jLabel4, CC.xy(1, 7));

                    //---- cmbArt ----
                    cmbArt.setModel(new DefaultComboBoxModel(new String[] {
                        "Item 1",
                        "Item 2",
                        "Item 3",
                        "Item 4"
                    }));
                    cmbArt.setFont(new Font("Arial", Font.PLAIN, 14));
                    cmbArt.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            cmbArtItemStateChanged(e);
                        }
                    });
                    jPanel3.add(cmbArt, CC.xy(3, 7));

                    //---- cbAktiv ----
                    cbAktiv.setText("Aktiv");
                    cbAktiv.setToolTipText("Soll diese Massnahmen f\u00fcr neue Pflegeplanungen angeboten werden ?");
                    cbAktiv.setFont(new Font("Arial", Font.PLAIN, 14));
                    cbAktiv.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            cbAktivItemStateChanged(e);
                        }
                    });
                    jPanel3.add(cbAktiv, CC.xy(1, 9));

                    //======== panel4 ========
                    {
                        panel4.setLayout(new BoxLayout(panel4, BoxLayout.X_AXIS));

                        //---- btnBack ----
                        btnBack.setText(null);
                        btnBack.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/1leftarrow.png")));
                        btnBack.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnBackActionPerformed(e);
                            }
                        });
                        panel4.add(btnBack);
                    }
                    jPanel3.add(panel4, CC.xywh(1, 11, 3, 1, CC.RIGHT, CC.BOTTOM));
                }
                split1.setRightComponent(jPanel3);
            }
            panel1.add(split1);
        }
        add(panel1, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JSplitPane split1;
    private JPanel panel2;
    private JXSearchField txtSearch;
    private JScrollPane scrollPane1;
    private JList listInterventions;
    private JPanel panel3;
    private JButton btnAdd;
    private JPanel panel5;
    private JButton btnOk;
    private JPanel jPanel3;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JTextField txtBezeichnung;
    private JTextField txtDauer;
    private JLabel jLabel3;
    private JComboBox cmbKategorie;
    private JLabel jLabel4;
    private JComboBox cmbArt;
    private JCheckBox cbAktiv;
    private JPanel panel4;
    private JButton btnBack;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
