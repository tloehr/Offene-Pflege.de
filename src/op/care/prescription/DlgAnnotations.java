/*
 * Created by JFormDesigner on Fri Feb 27 15:52:33 CET 2015
 */

package op.care.prescription;

import javax.swing.event.*;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.prescription.Prescription;
import entity.system.Commontags;
import op.OPDE;
import op.tools.MyJDialog;

import javax.swing.*;
import java.awt.*;

/**
 * @author Torsten Löhr
 */
public class DlgAnnotations extends MyJDialog {


    private final Prescription prescription;

    public DlgAnnotations(Prescription prescription) {
        super(OPDE.getMainframe(), true);
        this.prescription = prescription;
        initComponents();
        initDialog();
    }

    private void initDialog() {

        listCommontTags.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                return super.getListCellRendererComponent(list, value.toString(), index, isSelected, cellHasFocus);
            }
        });

        DefaultListModel<Commontags> listModel = new DefaultListModel<>();
        for (Commontags tag : prescription.getCommontags()) {
            listModel.addElement(tag);
        }
        listCommontTags.setModel(listModel);
    }

    private void listCommontTagsValueChanged(ListSelectionEvent e) {
        // TODO add your code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        scrollPane1 = new JScrollPane();
        listCommontTags = new JList();
        pnlResInfo = new JPanel();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "default, $lcgap, default:grow",
            "default:grow, 2*($lgap, default)"));

        //======== scrollPane1 ========
        {

            //---- listCommontTags ----
            listCommontTags.addListSelectionListener(e -> listCommontTagsValueChanged(e));
            scrollPane1.setViewportView(listCommontTags);
        }
        contentPane.add(scrollPane1, CC.xy(1, 1, CC.DEFAULT, CC.FILL));

        //======== pnlResInfo ========
        {
            pnlResInfo.setLayout(new FormLayout(
                "default:grow",
                "2*(default, $lgap), default"));
        }
        contentPane.add(pnlResInfo, CC.xy(3, 1, CC.FILL, CC.FILL));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane scrollPane1;
    private JList listCommontTags;
    private JPanel pnlResInfo;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
