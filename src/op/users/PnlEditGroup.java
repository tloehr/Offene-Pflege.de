/*
 * Created by JFormDesigner on Wed Aug 29 10:35:47 CEST 2012
 */

package op.users;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

/**
 * @author Torsten Löhr
 */
public class PnlEditGroup extends JPanel {
    public PnlEditGroup() {
        initComponents();
    }

    private void txtGKennungCaretUpdate(CaretEvent e) {
        // TODO add your code here
    }

    private void txtGKennungActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void txtGroupDescriptionCaretUpdate(CaretEvent e) {
        // TODO add your code here
    }

    private void cbExamenItemStateChanged(ItemEvent e) {
        // TODO add your code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jPanel9 = new JPanel();
        jLabel11 = new JLabel();
        jLabel12 = new JLabel();
        txtGKennung = new JTextField();
        txtGroupDescription = new JTextField();
        lblGKennungFree = new JLabel();
        cbExamen = new JCheckBox();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== jPanel9 ========
        {
            jPanel9.setBorder(LineBorder.createBlackLineBorder());

            //---- jLabel11 ----
            jLabel11.setText("Gruppenbezeichnung");

            //---- jLabel12 ----
            jLabel12.setText("Erl\u00e4uterung");

            //---- txtGKennung ----
            txtGKennung.setDragEnabled(false);
            txtGKennung.addCaretListener(new CaretListener() {
                @Override
                public void caretUpdate(CaretEvent e) {
                    txtGKennungCaretUpdate(e);
                }
            });
            txtGKennung.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    txtGKennungActionPerformed(e);
                }
            });

            //---- txtGroupDescription ----
            txtGroupDescription.setDragEnabled(false);
            txtGroupDescription.addCaretListener(new CaretListener() {
                @Override
                public void caretUpdate(CaretEvent e) {
                    txtGroupDescriptionCaretUpdate(e);
                }
            });

            //---- lblGKennungFree ----
            lblGKennungFree.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/ballred.png")));

            //---- cbExamen ----
            cbExamen.setText("Examen");
            cbExamen.setToolTipText("Die Mitgliedschaft in dieser Gruppe gew\u00e4hrt Examensrechte.");
            cbExamen.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    cbExamenItemStateChanged(e);
                }
            });

            GroupLayout jPanel9Layout = new GroupLayout(jPanel9);
            jPanel9.setLayout(jPanel9Layout);
            jPanel9Layout.setHorizontalGroup(
                jPanel9Layout.createParallelGroup()
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel9Layout.createParallelGroup()
                            .addComponent(jLabel11)
                            .addComponent(jLabel12))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup()
                            .addComponent(txtGroupDescription, GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                            .addGroup(GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                                .addComponent(txtGKennung, GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblGKennungFree)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbExamen)))
                        .addContainerGap())
            );
            jPanel9Layout.setVerticalGroup(
                jPanel9Layout.createParallelGroup()
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel9Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(txtGKennung, GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                            .addComponent(jLabel11)
                            .addComponent(lblGKennungFree, GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                            .addComponent(cbExamen, GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel9Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(txtGroupDescription, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30))
            );
        }
        add(jPanel9);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel jPanel9;
    private JLabel jLabel11;
    private JLabel jLabel12;
    private JTextField txtGKennung;
    private JTextField txtGroupDescription;
    private JLabel lblGKennungFree;
    private JCheckBox cbExamen;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
