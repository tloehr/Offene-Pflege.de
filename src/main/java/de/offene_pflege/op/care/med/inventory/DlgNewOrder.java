/*
 * Created by JFormDesigner on Thu Sep 22 11:25:38 CEST 2022
 */

package de.offene_pflege.op.care.med.inventory;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.*;

import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import de.offene_pflege.entity.EntityTools;
import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.entity.info.ResidentTools;
import de.offene_pflege.entity.prescription.*;
import de.offene_pflege.entity.system.SYSPropsTools;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.SYSTools;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.*;

/**
 * @author Torsten Löhr
 */
public class DlgNewOrder extends JDialog {


    public DlgNewOrder(JFrame owner) {
        super(owner, true);
        initComponents();
        cmbBW.setModel(new DefaultComboBoxModel(ResidentTools.getAllActive().toArray()));
        cmbBW.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                return super.getListCellRendererComponent(list, ResidentTools.getTextCompact(((Resident) value)), index, isSelected, cellHasFocus);
            }
        });

        final java.util.List<HasName> list_where_to_order = new ArrayList();
        list_where_to_order.addAll(GPTools.getAllActive());
        list_where_to_order.addAll(HospitalTools.getAll());
        Collections.sort(list_where_to_order, Comparator.comparing(HasName::getName));
        cmbWhereToOrder.setModel(SYSTools.list2cmb(list_where_to_order));
        cmbWhereToOrder.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                return super.getListCellRendererComponent(list, (value instanceof GP ?
                                SYSTools.anonymizeName(((GP) value).getName(), SYSTools.INDEX_LASTNAME) + ", " + SYSTools.anonymizeName(((GP) value).getFirstname(), SYSTools.INDEX_FIRSTNAME_MALE) :
                                ((Hospital) value).getName() + ", " + ((Hospital) value).getCity()
                        ),
                        index, isSelected, cellHasFocus);
            }
        });

        String[] prop = StringUtils.split(OPDE.getProps().getProperty(this.getClass().getName() + ":cmbwhere", ""), ":");
        if (prop.length > 1) {
            long id = Long.parseLong(prop[1]);
            if (prop[0].equalsIgnoreCase("gp")) {
                list_where_to_order.stream().filter(hasName -> hasName instanceof GP && ((GP) hasName).getArztID().equals(id)).findFirst().ifPresent(hasName -> cmbWhereToOrder.setSelectedItem(hasName));
            } else if (prop[0].equalsIgnoreCase("Hospital")) {
                list_where_to_order.stream().filter(hasName ->  hasName instanceof Hospital && ((Hospital) hasName).getKhid().equals(id)).findFirst().ifPresent(hasName -> cmbWhereToOrder.setSelectedItem(hasName));
            }
        }
    }

    private void cancel(ActionEvent e) {
        dispose();
    }

    private void ok(ActionEvent e) {
        MedOrder medOrder = new MedOrder();
        medOrder.setTradeForm(null);
        medOrder.setResident((Resident) cmbBW.getSelectedItem());
        medOrder.setCreated_on(LocalDateTime.now());
        medOrder.setCreated_by(OPDE.getLogin().getUser());
        medOrder.setAuto_created(false);
        HasName hasName = (HasName) cmbWhereToOrder.getSelectedItem();
        if (hasName instanceof GP) medOrder.setGp((GP) hasName);
        else medOrder.setHospital((Hospital) hasName);
        medOrder.setClosing_med_stock(null);
        medOrder.setNote(StringUtils.abbreviate(textField1.getText().trim(),200));
        EntityTools.persist(medOrder);
        dispose();
    }

    private void cmbWhereToOrderItemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) return;
        if (e.getItem() instanceof GP) {
            SYSPropsTools.storeProp(this.getClass().getName() + ":cmbwhere", "GP:" + ((GP) e.getItem()).getArztID(), OPDE.getLogin().getUser());
        } else {
            SYSPropsTools.storeProp(this.getClass().getName() + ":cmbwhere", "Hospital:" + ((Hospital) e.getItem()).getKhid(), OPDE.getLogin().getUser());
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        label1 = new JLabel();
        textField1 = new JTextArea();
        cmbWhereToOrder = new JComboBox<>();
        cmbBW = new JComboBox<>();
        buttonBar = new JPanel();
        cancelButton = new JButton();
        okButton = new JButton();

        //======== this ========
        setTitle("Bestell-Zusatz");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.createEmptyBorder("9dlu, 9dlu, 9dlu, 9dlu"));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new FormLayout(
                    "default, $ugap, 190dlu:grow",
                    "fill:120dlu:grow, 2*($lgap, default)"));

                //---- label1 ----
                label1.setText("Text");
                label1.setVerticalAlignment(SwingConstants.TOP);
                contentPanel.add(label1, CC.xy(1, 1));

                //---- textField1 ----
                textField1.setWrapStyleWord(true);
                textField1.setLineWrap(true);
                contentPanel.add(textField1, CC.xy(3, 1));

                //---- cmbWhereToOrder ----
                cmbWhereToOrder.addItemListener(e -> cmbWhereToOrderItemStateChanged(e));
                contentPanel.add(cmbWhereToOrder, CC.xywh(1, 3, 3, 1));
                contentPanel.add(cmbBW, CC.xywh(1, 5, 3, 1));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(Borders.createEmptyBorder("4dlu, 0dlu, 0dlu, 0dlu"));
                buttonBar.setLayout(new HorizontalLayout(5));

                //---- cancelButton ----
                cancelButton.setText(null);
                cancelButton.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
                cancelButton.addActionListener(e -> cancel(e));
                buttonBar.add(cancelButton);

                //---- okButton ----
                okButton.setText(null);
                okButton.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                okButton.addActionListener(e -> ok(e));
                buttonBar.add(okButton);
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel label1;
    private JTextArea textField1;
    private JComboBox<HasName> cmbWhereToOrder;
    private JComboBox<Resident> cmbBW;
    private JPanel buttonBar;
    private JButton cancelButton;
    private JButton okButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
