/*
 * Created by JFormDesigner on Wed Aug 29 10:59:23 CEST 2012
 */

package op.users;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import entity.system.Users;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.MyJDialog;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.*;

/**
 * @author Torsten Löhr
 */
public class DlgChangePW extends MyJDialog {
    public static final String internalClassID = "opde.users.dlgchangepw";
    private Closure afterAction;
    private String password;
    private Users user;

    public DlgChangePW(Users user, Closure afterAction) {
        super();
        this.user = user;
        this.afterAction = afterAction;
        initComponents();
        initDialog();
    }

    @Override
    public void dispose() {
        super.dispose();
        afterAction.execute(password);
    }

    private void initDialog(){

    }

    private void btnApplyActionPerformed(ActionEvent e) {
        if (!user.getMd5pw().equals(SYSTools.hashword(txtOld.getText().trim()))){
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID+".oldpwwrong")));
            return;
        }
        if (txtNew.getText().trim().isEmpty()){
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID+".newpwempty")));
            return;
        }
        password = txtNew.getText().trim();
        dispose();
    }

    private void btnCancelActionPerformed(ActionEvent e) {
        password = null;
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        lblOld = new JLabel();
        txtOld = new JTextField();
        lblNew = new JLabel();
        txtNew = new JTextField();
        panel2 = new JPanel();
        btnCancel = new JButton();
        btnApply = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        //======== panel1 ========
        {
            panel1.setLayout(new FormLayout(
                "default, $lcgap, pref, $lcgap, default:grow, $lcgap, default",
                "3*(default, $lgap), $ugap, 2*($lgap, default)"));

            //---- lblOld ----
            lblOld.setText("text");
            lblOld.setFont(new Font("Arial", Font.PLAIN, 14));
            panel1.add(lblOld, CC.xy(3, 3));

            //---- txtOld ----
            txtOld.setFont(new Font("Arial", Font.PLAIN, 14));
            panel1.add(txtOld, CC.xy(5, 3));

            //---- lblNew ----
            lblNew.setText("text");
            lblNew.setFont(new Font("Arial", Font.PLAIN, 14));
            panel1.add(lblNew, CC.xy(3, 5));

            //---- txtNew ----
            txtNew.setFont(new Font("Arial", Font.PLAIN, 14));
            panel1.add(txtNew, CC.xy(5, 5));

            //======== panel2 ========
            {
                panel2.setLayout(new HorizontalLayout(5));

                //---- btnCancel ----
                btnCancel.setText(null);
                btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
                btnCancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnCancelActionPerformed(e);
                    }
                });
                panel2.add(btnCancel);

                //---- btnApply ----
                btnApply.setText(null);
                btnApply.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                btnApply.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnApplyActionPerformed(e);
                    }
                });
                panel2.add(btnApply);
            }
            panel1.add(panel2, CC.xy(5, 9, CC.RIGHT, CC.DEFAULT));
        }
        contentPane.add(panel1);
        setSize(450, 175);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel lblOld;
    private JTextField txtOld;
    private JLabel lblNew;
    private JTextField txtNew;
    private JPanel panel2;
    private JButton btnCancel;
    private JButton btnApply;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
