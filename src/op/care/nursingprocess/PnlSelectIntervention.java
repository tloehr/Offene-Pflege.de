/*
 * Created by JFormDesigner on Wed Jul 25 16:15:09 CEST 2012
 */

package op.care.nursingprocess;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.info.ResInfoCategory;
import entity.info.ResInfoCategoryTools;
import entity.nursingprocess.Intervention;
import entity.nursingprocess.InterventionTools;
import op.OPDE;
import op.system.InternalClassACL;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.GUITools;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.HorizontalLayout;
import org.jdesktop.swingx.JXSearchField;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;

/**
 * @author Torsten Löhr
 */
public class PnlSelectIntervention extends JPanel {
    private Intervention intervention2Edit = null;
    public static final String internalClassID = PnlNursingProcess.internalClassID + ".pnlselectinterventions";
    private Closure actionBlock;
    private JToggleButton tbAktiv;
    Number dauer = BigDecimal.TEN;
    Component focusOwner = null;

    public PnlSelectIntervention(Closure actionBlock) {
        this.actionBlock = actionBlock;
        initComponents();
        initPanel();
        btnEdit.setEnabled(false);
        btnAdd.setEnabled(OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, PnlNursingProcess.internalClassID));
    }

    private void initPanel() {
        lblText.setText(OPDE.lang.getString(internalClassID + ".lbltext"));
        lblLength.setText(OPDE.lang.getString(internalClassID + ".lbllength"));
        lblCat.setText(OPDE.lang.getString(internalClassID + ".lblcat"));
        lblType.setText(OPDE.lang.getString(internalClassID + ".lbltype"));

        lstInterventions.setModel(new DefaultListModel());
        tbAktiv = GUITools.getNiceToggleButton(internalClassID + ".activeIntervention");
        pnlRight.add(tbAktiv, CC.xy(1, 9));
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SYSTools.showSide(split1, SYSTools.LEFT_UPPER_SIDE);
            }
        });

        cmbType.setModel(new DefaultComboBoxModel(new String[]{OPDE.lang.getString("misc.msg.interventions.CARE"), OPDE.lang.getString("misc.msg.interventions.PRESCRIPTION"), OPDE.lang.getString("misc.msg.interventions.SOCIAL")}));
        cmbCat.setModel(new DefaultComboBoxModel(ResInfoCategoryTools.getAll4NP().toArray()));
        cmbCategory.setModel(new DefaultComboBoxModel(ResInfoCategoryTools.getAll4NP().toArray()));
        cmbCategory.setSelectedItem(null);


        setFocusCycleRoot(true);
        setFocusTraversalPolicy(new FocusTraversalPolicy() {
            @Override
            public Component getComponentAfter(Container aContainer, Component aComponent) {
                if (focusOwner == null) {
                    focusOwner = txtText;
                } else if (focusOwner.equals(txtText)) {
                    focusOwner = txtLength;
                } else {
                    focusOwner = txtText;
                }
                return focusOwner;
            }

            @Override
            public Component getComponentBefore(Container aContainer, Component aComponent) {
                if (focusOwner == null) {
                    focusOwner = txtLength;
                } else if (focusOwner.equals(txtLength)) {
                    focusOwner = txtText;
                } else {
                    focusOwner = txtLength;
                }
                return focusOwner;
            }

            @Override
            public Component getFirstComponent(Container aContainer) {
                return txtText;
            }

            @Override
            public Component getLastComponent(Container aContainer) {
                return txtLength;
            }

            @Override
            public Component getDefaultComponent(Container aContainer) {
                return txtText;
            }
        });


    }

    private void txtSearchActionPerformed(ActionEvent e) {
        if (txtSearch.getText().isEmpty()) return;
        lstInterventions.setModel(SYSTools.list2dlm(InterventionTools.findBy(InterventionTools.TYPE_CARE, txtSearch.getText())));
        cmbCategory.setSelectedItem(null);
    }

    private void btnAddActionPerformed(ActionEvent e) {
        tbAktiv.setEnabled(false);
        intervention2Edit = null;
        SYSTools.showSide(split1, SYSTools.RIGHT_LOWER_SIDE, SYSTools.SPEED_NORMAL);
    }

    private void btnOkActionPerformed(ActionEvent e) {
        actionBlock.execute(lstInterventions.getSelectedValues());
    }

    private boolean saveok() {
        if (txtText.getText().trim().isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".textempty"), DisplayMessage.WARNING));
            return false;
        }
        return true;
    }

    private void txtDauerFocusLost(FocusEvent e) {
        try {
            dauer = NumberFormat.getNumberInstance().parse(txtLength.getText());
        } catch (ParseException e1) {
            dauer = BigDecimal.TEN;
            txtLength.setText("10");
        }
    }

    private void lstInterventionsMouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            btnOk.doClick();
        }
    }

    private void cmbCategoryItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED && e.getItem() != null) {
            txtSearch.setText(null);
            lstInterventions.setModel(SYSTools.list2dlm(InterventionTools.findBy((ResInfoCategory) e.getItem())));
        }
    }

//    private void btnCancelActionPerformed(ActionEvent e) {
//        SYSTools.showSide(split1, SYSTools.LEFT_UPPER_SIDE, SYSTools.SPEED_NORMAL);
//    }

    private void btnEditActionPerformed(ActionEvent e) {
        intervention2Edit = (Intervention) lstInterventions.getSelectedValue();
        txtText.setText(intervention2Edit.getBezeichnung());
        txtLength.setText(intervention2Edit.getDauer().toBigInteger().toString());
        tbAktiv.setSelected(intervention2Edit.isActive());
        cmbCategory.setSelectedItem(intervention2Edit.getCategory());
        tbAktiv.setEnabled(true);
        cmbType.setSelectedIndex(intervention2Edit.getInterventionType() - 1);

        SYSTools.showSide(split1, SYSTools.RIGHT_LOWER_SIDE, SYSTools.SPEED_NORMAL);
    }

    private void lstInterventionsValueChanged(ListSelectionEvent e) {
//        btnEdit.setEnabled(lstInterventions.getSelectedValues().length == 1 && ((Intervention) lstInterventions.getSelectedValue()).getMassID() != null && OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, PnlNursingProcess.internalClassID));
        btnEdit.setEnabled(false);
    }

    private void btnSaveActionPerformed(ActionEvent evt) {
        if (saveok()) {
            if (intervention2Edit == null) {
                Intervention intervention = new Intervention(txtText.getText().trim(), new BigDecimal(dauer.doubleValue()), cmbType.getSelectedIndex() + 1, (ResInfoCategory) cmbCat.getSelectedItem());
                lstInterventions.setModel(SYSTools.list2dlm(Arrays.asList(intervention)));
                btnEdit.setEnabled(false);
            } else {
                EntityManager em = OPDE.createEM();
                try {
                    em.getTransaction().begin();
                    Intervention myIntervention = em.merge(intervention2Edit);
                    em.lock(myIntervention, LockModeType.OPTIMISTIC);
                    myIntervention.setBezeichnung(txtText.getText().trim());
                    myIntervention.setDauer(new BigDecimal(dauer.doubleValue()));
                    myIntervention.setCategory(em.merge((ResInfoCategory) cmbCat.getSelectedItem()));
                    myIntervention.setInterventionType(cmbType.getSelectedIndex() + 1);
                    myIntervention.setActive(tbAktiv.isSelected());
                    em.getTransaction().commit();
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(internalClassID + ".interventionedited"));
                    actionBlock.execute(null);
                } catch (OptimisticLockException ole) { OPDE.warn(ole);
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
                        OPDE.getMainframe().emptyFrame();
                        OPDE.getMainframe().afterLogin();
                    }
                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                } catch (Exception e) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    OPDE.fatal(e);
                } finally {
                    em.close();
                }

            }
        }
        SYSTools.showSide(split1, SYSTools.LEFT_UPPER_SIDE, SYSTools.SPEED_NORMAL);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        split1 = new JSplitPane();
        panel2 = new JPanel();
        txtSearch = new JXSearchField();
        cmbCategory = new JComboBox();
        scrollPane1 = new JScrollPane();
        lstInterventions = new JList();
        panel3 = new JPanel();
        panel5 = new JPanel();
        btnOk = new JButton();
        btnAdd = new JButton();
        btnEdit = new JButton();
        pnlRight = new JPanel();
        lblText = new JLabel();
        lblLength = new JLabel();
        txtText = new JTextField();
        txtLength = new JTextField();
        lblCat = new JLabel();
        cmbCat = new JComboBox();
        lblType = new JLabel();
        cmbType = new JComboBox();
        panel4 = new JPanel();
        btnSave = new JButton();

        //======== this ========
        setLayout(new BorderLayout());

        //======== panel1 ========
        {
            panel1.setBorder(new EmptyBorder(15, 15, 15, 15));
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

            //======== split1 ========
            {
                split1.setDividerLocation(150);
                split1.setDividerSize(1);
                split1.setEnabled(false);

                //======== panel2 ========
                {
                    panel2.setLayout(new FormLayout(
                        "default:grow",
                        "2*(default, $lgap), default:grow, $lgap, default"));

                    //---- txtSearch ----
                    txtSearch.setFont(new Font("Arial", Font.PLAIN, 14));
                    txtSearch.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            txtSearchActionPerformed(e);
                        }
                    });
                    panel2.add(txtSearch, CC.xy(1, 1));

                    //---- cmbCategory ----
                    cmbCategory.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            cmbCategoryItemStateChanged(e);
                        }
                    });
                    panel2.add(cmbCategory, CC.xy(1, 3));

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
                        lstInterventions.addListSelectionListener(new ListSelectionListener() {
                            @Override
                            public void valueChanged(ListSelectionEvent e) {
                                lstInterventionsValueChanged(e);
                            }
                        });
                        scrollPane1.setViewportView(lstInterventions);
                    }
                    panel2.add(scrollPane1, CC.xy(1, 5, CC.FILL, CC.FILL));

                    //======== panel3 ========
                    {
                        panel3.setLayout(new BoxLayout(panel3, BoxLayout.X_AXIS));

                        //======== panel5 ========
                        {
                            panel5.setLayout(new HorizontalLayout());

                            //---- btnOk ----
                            btnOk.setText(null);
                            btnOk.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                            btnOk.setBorderPainted(false);
                            btnOk.setBorder(null);
                            btnOk.setContentAreaFilled(false);
                            btnOk.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                            btnOk.setSelectedIcon(null);
                            btnOk.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/pressed.png")));
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
                        btnAdd.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
                        btnAdd.setBorder(null);
                        btnAdd.setBorderPainted(false);
                        btnAdd.setContentAreaFilled(false);
                        btnAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        btnAdd.setSelectedIcon(null);
                        btnAdd.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/pressed.png")));
                        btnAdd.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnAddActionPerformed(e);
                            }
                        });
                        panel3.add(btnAdd);

                        //---- btnEdit ----
                        btnEdit.setText(null);
                        btnEdit.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/edit3.png")));
                        btnEdit.setBorder(null);
                        btnEdit.setBorderPainted(false);
                        btnEdit.setContentAreaFilled(false);
                        btnEdit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        btnEdit.setSelectedIcon(null);
                        btnEdit.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/pressed.png")));
                        btnEdit.setEnabled(false);
                        btnEdit.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnEditActionPerformed(e);
                            }
                        });
                        panel3.add(btnEdit);
                    }
                    panel2.add(panel3, CC.xy(1, 7, CC.FILL, CC.DEFAULT));
                }
                split1.setLeftComponent(panel2);

                //======== pnlRight ========
                {
                    pnlRight.setBorder(null);
                    pnlRight.setLayout(new FormLayout(
                        "default, $lcgap, default:grow",
                        "3*(fill:default, $lgap), 2*(default, $lgap), default:grow"));

                    //---- lblText ----
                    lblText.setText("Bezeichnung");
                    lblText.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlRight.add(lblText, CC.xy(1, 1));

                    //---- lblLength ----
                    lblLength.setText("Dauer");
                    lblLength.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlRight.add(lblLength, CC.xy(1, 3));

                    //---- txtText ----
                    txtText.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlRight.add(txtText, CC.xy(3, 1));

                    //---- txtLength ----
                    txtLength.setHorizontalAlignment(SwingConstants.RIGHT);
                    txtLength.setText("10");
                    txtLength.setToolTipText(null);
                    txtLength.setFont(new Font("Arial", Font.PLAIN, 14));
                    txtLength.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusLost(FocusEvent e) {
                            txtDauerFocusLost(e);
                        }
                    });
                    pnlRight.add(txtLength, CC.xy(3, 3));

                    //---- lblCat ----
                    lblCat.setText("Kategorie");
                    lblCat.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlRight.add(lblCat, CC.xy(1, 5));

                    //---- cmbCat ----
                    cmbCat.setModel(new DefaultComboBoxModel(new String[] {
                        "Item 1",
                        "Item 2",
                        "Item 3",
                        "Item 4"
                    }));
                    cmbCat.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlRight.add(cmbCat, CC.xy(3, 5));

                    //---- lblType ----
                    lblType.setText("Art");
                    lblType.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlRight.add(lblType, CC.xy(1, 7));

                    //---- cmbType ----
                    cmbType.setModel(new DefaultComboBoxModel(new String[] {
                        "Item 1",
                        "Item 2",
                        "Item 3",
                        "Item 4"
                    }));
                    cmbType.setFont(new Font("Arial", Font.PLAIN, 14));
                    pnlRight.add(cmbType, CC.xy(3, 7));

                    //======== panel4 ========
                    {
                        panel4.setLayout(new BoxLayout(panel4, BoxLayout.X_AXIS));

                        //---- btnSave ----
                        btnSave.setText(null);
                        btnSave.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                        btnSave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        btnSave.setBorderPainted(false);
                        btnSave.setBorder(null);
                        btnSave.setContentAreaFilled(false);
                        btnSave.setSelectedIcon(null);
                        btnSave.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/pressed.png")));
                        btnSave.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnSaveActionPerformed(e);
                            }
                        });
                        panel4.add(btnSave);
                    }
                    pnlRight.add(panel4, CC.xywh(1, 11, 3, 1, CC.RIGHT, CC.BOTTOM));
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
    private JComboBox cmbCategory;
    private JScrollPane scrollPane1;
    private JList lstInterventions;
    private JPanel panel3;
    private JPanel panel5;
    private JButton btnOk;
    private JButton btnAdd;
    private JButton btnEdit;
    private JPanel pnlRight;
    private JLabel lblText;
    private JLabel lblLength;
    private JTextField txtText;
    private JTextField txtLength;
    private JLabel lblCat;
    private JComboBox cmbCat;
    private JLabel lblType;
    private JComboBox cmbType;
    private JPanel panel4;
    private JButton btnSave;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
