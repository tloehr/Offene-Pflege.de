/*
 * Created by JFormDesigner on Wed Jul 25 16:15:09 CEST 2012
 */

package op.care.nursingprocess;

import java.awt.event.*;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.info.BWInfoKatTools;
import entity.nursingprocess.Intervention;
import entity.nursingprocess.InterventionTools;
import entity.info.BWInfoKat;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.GUITools;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.HorizontalLayout;
import org.jdesktop.swingx.JXSearchField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;

/**
 * @author Torsten Löhr
 */
public class PnlSelectIntervention extends JPanel {
    public static final String internalClassID = "nursingrecords.nursingprocess.pnlselectinterventions";
    private Closure actionBlock;
    private JToggleButton tbAktiv;
    Number dauer = BigDecimal.TEN;

    public PnlSelectIntervention(Closure actionBlock) {
        this(actionBlock, false);
    }

    public PnlSelectIntervention(Closure actionBlock, boolean withEditFunction) {
        this.actionBlock = actionBlock;
        initComponents();
        initPanel();
        btnAdd.setEnabled(withEditFunction);
    }

    private void initPanel() {
        lstInterventions.setModel(new DefaultListModel());
        tbAktiv = GUITools.getNiceToggleButton("Aktiv");
        tbAktiv.setEnabled(false);
        pnlRight.add(tbAktiv, CC.xy(1, 9));
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SYSTools.showSide(split1, SYSTools.LEFT_UPPER_SIDE);
            }
        });

        cmbArt.setModel(new DefaultComboBoxModel(new String[]{"Pflege","Verordungen"}));
        cmbKategorie.setModel(new DefaultComboBoxModel(BWInfoKatTools.getCategoriesForNursingProcess().toArray()));
    }

    private void txtSearchActionPerformed(ActionEvent e) {
        lstInterventions.setModel(SYSTools.list2dlm(InterventionTools.findMassnahmenBy(InterventionTools.MASSART_PFLEGE, txtSearch.getText())));
    }

    private void btnAddActionPerformed(ActionEvent e) {
        SYSTools.showSide(split1, SYSTools.RIGHT_LOWER_SIDE, SYSTools.SPEED_NORMAL);
    }

    private void btnBackActionPerformed(ActionEvent e) {
        if (saveok()) {
            Intervention intervention = new Intervention(txtBezeichnung.getText().trim(), new BigDecimal(dauer.doubleValue()), cmbArt.getSelectedIndex(), (BWInfoKat) cmbKategorie.getSelectedItem());
            lstInterventions.setModel(SYSTools.list2dlm(Arrays.asList(intervention)));
        }
        SYSTools.showSide(split1, SYSTools.LEFT_UPPER_SIDE, SYSTools.SPEED_NORMAL);
    }

    private void btnOkActionPerformed(ActionEvent e) {
        actionBlock.execute(lstInterventions.getSelectedValues());
    }

    private boolean saveok() {
        if (txtBezeichnung.getText().trim().isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID+".textempty"), DisplayMessage.WARNING));
            return false;
        }
        return true;
    }

    private void txtDauerFocusLost(FocusEvent e) {
        try {
            dauer = NumberFormat.getNumberInstance().parse(txtDauer.getText());
        } catch (ParseException e1) {
            dauer = BigDecimal.TEN;
            txtDauer.setText("10");
        }
    }

    private void lstInterventionsMouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2){
            btnOk.doClick();
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        split1 = new JSplitPane();
        panel2 = new JPanel();
        txtSearch = new JXSearchField();
        scrollPane1 = new JScrollPane();
        lstInterventions = new JList();
        panel3 = new JPanel();
        panel5 = new JPanel();
        btnOk = new JButton();
        btnAdd = new JButton();
        pnlRight = new JPanel();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        txtBezeichnung = new JTextField();
        txtDauer = new JTextField();
        jLabel3 = new JLabel();
        cmbKategorie = new JComboBox();
        jLabel4 = new JLabel();
        cmbArt = new JComboBox();
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
                split1.setDividerSize(1);
                split1.setEnabled(false);

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

                        //---- lstInterventions ----
                        lstInterventions.setFont(new Font("Arial", Font.PLAIN, 14));
                        lstInterventions.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                lstInterventionsMouseClicked(e);
                            }
                        });
                        scrollPane1.setViewportView(lstInterventions);
                    }
                    panel2.add(scrollPane1, CC.xy(1, 3, CC.FILL, CC.FILL));

                    //======== panel3 ========
                    {
                        panel3.setLayout(new BoxLayout(panel3, BoxLayout.X_AXIS));

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

                        //---- btnAdd ----
                        btnAdd.setText(null);
                        btnAdd.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/1rightarrow.png")));
                        btnAdd.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnAddActionPerformed(e);
                            }
                        });
                        panel3.add(btnAdd);
                    }
                    panel2.add(panel3, CC.xy(1, 5, CC.FILL, CC.DEFAULT));
                }
                split1.setLeftComponent(panel2);

                //======== pnlRight ========
                {
                    pnlRight.setBorder(null);
                    pnlRight.setLayout(new FormLayout(
                        "default, $lcgap, default:grow",
                        "3*(fill:default, $lgap), 2*(default, $lgap), default:grow"));

                    //---- jLabel1 ----
                    jLabel1.setText("Bezeichnung");
                    jLabel1.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlRight.add(jLabel1, CC.xy(1, 1));

                    //---- jLabel2 ----
                    jLabel2.setText("Dauer");
                    jLabel2.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlRight.add(jLabel2, CC.xy(1, 3));

                    //---- txtBezeichnung ----
                    txtBezeichnung.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlRight.add(txtBezeichnung, CC.xy(3, 1));

                    //---- txtDauer ----
                    txtDauer.setHorizontalAlignment(SwingConstants.RIGHT);
                    txtDauer.setText("10");
                    txtDauer.setToolTipText(null);
                    txtDauer.setFont(new Font("Arial", Font.PLAIN, 14));
                    txtDauer.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusLost(FocusEvent e) {
                            txtDauerFocusLost(e);
                        }
                    });
                    pnlRight.add(txtDauer, CC.xy(3, 3));

                    //---- jLabel3 ----
                    jLabel3.setText("Kategorie");
                    jLabel3.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlRight.add(jLabel3, CC.xy(1, 5));

                    //---- cmbKategorie ----
                    cmbKategorie.setModel(new DefaultComboBoxModel(new String[] {
                        "Item 1",
                        "Item 2",
                        "Item 3",
                        "Item 4"
                    }));
                    cmbKategorie.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlRight.add(cmbKategorie, CC.xy(3, 5));

                    //---- jLabel4 ----
                    jLabel4.setText("Art");
                    jLabel4.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlRight.add(jLabel4, CC.xy(1, 7));

                    //---- cmbArt ----
                    cmbArt.setModel(new DefaultComboBoxModel(new String[] {
                        "Item 1",
                        "Item 2",
                        "Item 3",
                        "Item 4"
                    }));
                    cmbArt.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlRight.add(cmbArt, CC.xy(3, 7));

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
                    pnlRight.add(panel4, CC.xywh(1, 11, 3, 1, CC.LEFT, CC.BOTTOM));
                }
                split1.setRightComponent(pnlRight);
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
    private JList lstInterventions;
    private JPanel panel3;
    private JPanel panel5;
    private JButton btnOk;
    private JButton btnAdd;
    private JPanel pnlRight;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JTextField txtBezeichnung;
    private JTextField txtDauer;
    private JLabel jLabel3;
    private JComboBox cmbKategorie;
    private JLabel jLabel4;
    private JComboBox cmbArt;
    private JPanel panel4;
    private JButton btnBack;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
