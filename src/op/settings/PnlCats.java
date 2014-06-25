/*
 * Created by JFormDesigner on Sat Feb 23 12:00:49 CET 2013
 */

package op.settings;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.info.ResInfoCategory;
import entity.info.ResInfoCategoryTools;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.PopupPanel;
import op.tools.SYSTools;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * @author Torsten Löhr
 */
public class PnlCats extends PopupPanel {
    public static final String internalClassID = "opde.settings.pnlcats";
    private ResInfoCategory resInfoCategory;

//    private ArrayList<JTextComponent> allTXT;
//    private ArrayList<Component> allComponents;

    public PnlCats(ResInfoCategory resInfoCategory) {
        this.resInfoCategory = resInfoCategory;

        initComponents();
        initPanel();
    }

    @Override
    public void setStartFocus() {
        txtName.requestFocus();
    }

    private void initPanel() {
        lblName.setText(SYSTools.xx("misc.msg.category"));
        lblCatType.setText(SYSTools.xx("misc.msg.type"));

        txtName.setText(resInfoCategory.getText());
        cmbCatType.setModel(new DefaultComboBoxModel(ResInfoCategoryTools.TYPESS));
        cmbCatType.setSelectedIndex(Arrays.binarySearch(ResInfoCategoryTools.TYPES, resInfoCategory.getCatType()));
    }

    @Override
    public Object getResult() {
        resInfoCategory.setText(txtName.getText().trim());
        resInfoCategory.setCatType(ResInfoCategoryTools.TYPES[cmbCatType.getSelectedIndex()]);
//        if (isSaveOK()) {
//            resInfoCategory.setText(txtName.getText().trim());
//            resInfoCategory.setCatType(ResInfoCategoryTools.TYPES[cmbCatType.getSelectedIndex()]);
//        } else {
//            resInfoCategory = null;
//        }
        return resInfoCategory;
    }

    @Override
    public boolean isSaveOK() {
        boolean ok = !txtName.getText().trim().isEmpty();

        if (!ok) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.emptyFields", DisplayMessage.WARNING));
        }

        return ok;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblName = new JLabel();
        txtName = new JTextField();
        lblCatType = new JLabel();
        cmbCatType = new JComboBox();

        //======== this ========
        setLayout(new FormLayout(
                "2*(default, $lcgap), 162dlu:grow, $lcgap, default",
                "3*(default, $lgap), default"));

        //---- lblName ----
        lblName.setText("Anrede");
        lblName.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblName, CC.xy(3, 3));

        //---- txtName ----
        txtName.setFont(new Font("Arial", Font.PLAIN, 14));
        add(txtName, CC.xy(5, 3));

        //---- lblCatType ----
        lblCatType.setText("text");
        lblCatType.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblCatType, CC.xy(3, 5));

        //---- cmbCatType ----
        cmbCatType.setFont(new Font("Arial", Font.PLAIN, 14));
        add(cmbCatType, CC.xy(5, 5));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblName;
    private JTextField txtName;
    private JLabel lblCatType;
    private JComboBox cmbCatType;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
