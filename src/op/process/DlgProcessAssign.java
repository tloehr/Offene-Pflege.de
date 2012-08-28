/*
 * Created by JFormDesigner on Mon Aug 27 16:33:56 CEST 2012
 */

package op.process;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.process.*;
import op.OPDE;
import op.tools.MyJDialog;
import op.tools.Pair;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.HorizontalLayout;
import org.jdesktop.swingx.VerticalLayout;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Torsten Löhr
 */
public class DlgProcessAssign extends MyJDialog {
    public static final String internalClassID = "nursingrecords.qprocesses.pnlprocessassign";

    private QProcessElement element;
    private Closure afterAction;
    private ArrayList<QProcess> assigned, unassigned;
    private Pair<ArrayList<QProcess>, ArrayList<QProcess>> result = null;

    public DlgProcessAssign(QProcessElement element, Closure afterAction) {
        super();
        this.element = element;
        this.afterAction = afterAction;
        initComponents();
        initPanel();
        setVisible(true);
    }

    @Override
    public void dispose() {
        super.dispose();
        afterAction.execute(result);
    }

    private void initPanel() {
        assigned = element.getAttachedProcesses();
        Collections.sort(assigned);
        unassigned = new ArrayList<QProcess>(QProcessTools.getActiveProcesses4(element.getResident()));
        unassigned.removeAll(assigned);
        Collections.sort(unassigned);
        listAssigned.setModel(SYSTools.list2dlm(assigned));
        listUnassigned.setModel(SYSTools.list2dlm(unassigned));
        lblAssigned.setText(OPDE.lang.getString(internalClassID+".assigned"));
        lblUnAssigned.setText(OPDE.lang.getString(internalClassID+".unassigned"));
        cmbPCat.setModel(SYSTools.list2cmb(PCatTools.getPCats()));
    }

    private void listUnassignedMouseClicked(MouseEvent e) {
        // TODO add your code here
    }

    private void btnRightActionPerformed(ActionEvent e) {
        if (listAssigned.getSelectedValue() == null) {
            return;
        }
        QProcess txProcess = (QProcess) listAssigned.getSelectedValue();
        assigned.remove(txProcess);
        listAssigned.setModel(SYSTools.list2dlm(assigned));
        if (txProcess.getPkid() != null) {
            unassigned.add(txProcess);
            Collections.sort(unassigned);
            listUnassigned.setModel(SYSTools.list2dlm(unassigned));
            listUnassigned.setSelectedValue(txProcess, true);
        }
    }

    private void btnLeftActionPerformed(ActionEvent e) {
        if (listUnassigned.getSelectedValue() == null) {
            return;
        }
        QProcess txProcess = (QProcess) listUnassigned.getSelectedValue();
        unassigned.remove(txProcess);
        assigned.add(txProcess);
        Collections.sort(assigned);
        listAssigned.setModel(SYSTools.list2dlm(assigned));
        listUnassigned.setModel(SYSTools.list2dlm(unassigned));
        listAssigned.setSelectedValue(txProcess, true);
    }

    private void btnAddProcessActionPerformed(ActionEvent e) {
        if (txtNew.getText().trim().isEmpty()) {
            return;
        }
        QProcess qProcess = new QProcess(txtNew.getText(), element.getResident(), (PCat) cmbPCat.getSelectedItem());
        assigned.add(qProcess);
        Collections.sort(assigned);
        listAssigned.setModel(SYSTools.list2dlm(assigned));
    }

    private void btnCancelActionPerformed(ActionEvent e) {
        dispose();
    }

    private void btnApplyActionPerformed(ActionEvent e) {
        result = new Pair<ArrayList<QProcess>, ArrayList<QProcess>>(assigned, unassigned);
        dispose();
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblAssigned = new JLabel();
        panel4 = new JPanel();
        lblUnAssigned = new JLabel();
        scrollPane1 = new JScrollPane();
        listAssigned = new JList();
        panel1 = new JPanel();
        btnRight = new JButton();
        btnLeft = new JButton();
        scrollPane2 = new JScrollPane();
        listUnassigned = new JList();
        panel2 = new JPanel();
        txtNew = new JTextField();
        cmbPCat = new JComboBox();
        panel3 = new JPanel();
        btnAddProcess = new JButton();
        panel5 = new JPanel();
        btnCancel = new JButton();
        btnApply = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "default, $lcgap, 100dlu:grow, $lcgap, default, $lcgap, 100dlu:grow, $lcgap, default",
            "3*(default, $lgap), pref, $lgap, $ugap, 2*($lgap, default)"));

        //---- lblAssigned ----
        lblAssigned.setText("text");
        lblAssigned.setFont(new Font("Arial", Font.PLAIN, 18));
        contentPane.add(lblAssigned, CC.xy(3, 3));

        //======== panel4 ========
        {
            panel4.setLayout(new HorizontalLayout());

            //---- lblUnAssigned ----
            lblUnAssigned.setText("text");
            lblUnAssigned.setFont(new Font("Arial", Font.PLAIN, 18));
            panel4.add(lblUnAssigned);
        }
        contentPane.add(panel4, CC.xy(7, 3, CC.RIGHT, CC.DEFAULT));

        //======== scrollPane1 ========
        {

            //---- listAssigned ----
            listAssigned.setFont(new Font("Arial", Font.PLAIN, 14));
            listAssigned.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            scrollPane1.setViewportView(listAssigned);
        }
        contentPane.add(scrollPane1, CC.xy(3, 5));

        //======== panel1 ========
        {
            panel1.setLayout(new VerticalLayout(10));

            //---- btnRight ----
            btnRight.setText(null);
            btnRight.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/1rightarrow.png")));
            btnRight.setBorderPainted(false);
            btnRight.setContentAreaFilled(false);
            btnRight.setBorder(null);
            btnRight.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/1rightarrow_pressed.png")));
            btnRight.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnRightActionPerformed(e);
                }
            });
            panel1.add(btnRight);

            //---- btnLeft ----
            btnLeft.setText(null);
            btnLeft.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/1leftarrow.png")));
            btnLeft.setBorderPainted(false);
            btnLeft.setContentAreaFilled(false);
            btnLeft.setBorder(null);
            btnLeft.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/1leftarrow_pressed.png")));
            btnLeft.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnLeftActionPerformed(e);
                }
            });
            panel1.add(btnLeft);
        }
        contentPane.add(panel1, CC.xy(5, 5, CC.DEFAULT, CC.CENTER));

        //======== scrollPane2 ========
        {

            //---- listUnassigned ----
            listUnassigned.setFont(new Font("Arial", Font.PLAIN, 14));
            listUnassigned.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            listUnassigned.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    listUnassignedMouseClicked(e);
                }
            });
            scrollPane2.setViewportView(listUnassigned);
        }
        contentPane.add(scrollPane2, CC.xy(7, 5));

        //======== panel2 ========
        {
            panel2.setBorder(new BevelBorder(BevelBorder.RAISED));
            panel2.setLayout(new FormLayout(
                "default:grow",
                "pref, $lgap, default:grow, $rgap, default"));

            //---- txtNew ----
            txtNew.setFont(new Font("Arial", Font.PLAIN, 14));
            panel2.add(txtNew, CC.xy(1, 1));

            //---- cmbPCat ----
            cmbPCat.setFont(new Font("Arial", Font.PLAIN, 14));
            panel2.add(cmbPCat, CC.xy(1, 3));

            //======== panel3 ========
            {
                panel3.setLayout(new BoxLayout(panel3, BoxLayout.X_AXIS));

                //---- btnAddProcess ----
                btnAddProcess.setText(null);
                btnAddProcess.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
                btnAddProcess.setBorderPainted(false);
                btnAddProcess.setContentAreaFilled(false);
                btnAddProcess.setBorder(null);
                btnAddProcess.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add-pressed.png")));
                btnAddProcess.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnAddProcessActionPerformed(e);
                    }
                });
                panel3.add(btnAddProcess);
            }
            panel2.add(panel3, CC.xy(1, 5, CC.RIGHT, CC.DEFAULT));
        }
        contentPane.add(panel2, CC.xywh(3, 7, 5, 1, CC.FILL, CC.FILL));

        //======== panel5 ========
        {
            panel5.setLayout(new HorizontalLayout(5));

            //---- btnCancel ----
            btnCancel.setText(null);
            btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
            btnCancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnCancelActionPerformed(e);
                }
            });
            panel5.add(btnCancel);

            //---- btnApply ----
            btnApply.setText(null);
            btnApply.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnApply.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnApplyActionPerformed(e);
                }
            });
            panel5.add(btnApply);
        }
        contentPane.add(panel5, CC.xywh(3, 11, 5, 1, CC.RIGHT, CC.DEFAULT));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblAssigned;
    private JPanel panel4;
    private JLabel lblUnAssigned;
    private JScrollPane scrollPane1;
    private JList listAssigned;
    private JPanel panel1;
    private JButton btnRight;
    private JButton btnLeft;
    private JScrollPane scrollPane2;
    private JList listUnassigned;
    private JPanel panel2;
    private JTextField txtNew;
    private JComboBox cmbPCat;
    private JPanel panel3;
    private JButton btnAddProcess;
    private JPanel panel5;
    private JButton btnCancel;
    private JButton btnApply;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
