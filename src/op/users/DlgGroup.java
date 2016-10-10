/*
 * Created by JFormDesigner on Tue Feb 19 16:41:42 CET 2013
 */

package op.users;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.system.Groups;
import op.OPDE;
import op.threads.DisplayMessage;
import gui.GUITools;
import op.tools.MyJDialog;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Torsten Löhr
 */
public class DlgGroup extends MyJDialog {
    public static final String internalClassID = "opde.users.dlggroup";
    private JToggleButton tbQualified;
    private Groups group;
    private Closure afterAction;

    public DlgGroup(Groups group, Closure afterAction) {
        super(false);
        this.group = group;
        this.afterAction = afterAction;
        initComponents();
        initPanel();
        pack();
        setVisible(true);
    }


    private void initPanel() {
        tbQualified = GUITools.getNiceToggleButton("opde.users.qualifiedGroup");
        tbQualified.setSelected(group.isQualified());
        txtText.setText(group.getDescription());

        txtGID.setEnabled(group.getGID() == null);
        txtGID.setText(group.getGID());

        lblGID.setText(SYSTools.xx(internalClassID + ".gid"));
        lblText.setText(SYSTools.xx(internalClassID + ".text"));
        getContentPane().add(tbQualified, CC.xyw(3, 7, 3, CC.LEFT, CC.DEFAULT));

    }

    private void btnOKActionPerformed(ActionEvent e) {
        if (txtText.getText().trim().isEmpty() || (txtGID.getText().trim().isEmpty() && group.getGID() == null)) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.emptyentry", DisplayMessage.WARNING));
            return;
        }

        if (group.getGID() == null) {
            EntityManager em = OPDE.createEM();
            Groups myGroup = em.find(Groups.class, txtGID.getText().trim());
            em.close();
            if (myGroup != null) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.alreadyExits", DisplayMessage.WARNING));
                return;
            }
            group.setGid(txtGID.getText().trim());
        }
        group.setDescription(txtText.getText());
        group.setQualified(tbQualified.isSelected());
        afterAction.execute(group);
        dispose();
    }

    private void btnCancelActionPerformed(ActionEvent e) {
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblGID = new JLabel();
        txtGID = new JTextField();
        lblText = new JLabel();
        txtText = new JTextField();
        panel1 = new JPanel();
        btnCancel = new JButton();
        btnOK = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "14dlu, $lcgap, pref, $lcgap, 156dlu:grow, $lcgap, 14dlu",
            "14dlu, 4*($lgap, default), $lgap, 14dlu"));

        //---- lblGID ----
        lblGID.setText("text");
        contentPane.add(lblGID, CC.xy(3, 3));
        contentPane.add(txtGID, CC.xy(5, 3));

        //---- lblText ----
        lblText.setText("text");
        contentPane.add(lblText, CC.xy(3, 5));
        contentPane.add(txtText, CC.xy(5, 5));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.LINE_AXIS));

            //---- btnCancel ----
            btnCancel.setText(null);
            btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
            btnCancel.addActionListener(e -> btnCancelActionPerformed(e));
            panel1.add(btnCancel);

            //---- btnOK ----
            btnOK.setText(null);
            btnOK.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnOK.addActionListener(e -> btnOKActionPerformed(e));
            panel1.add(btnOK);
        }
        contentPane.add(panel1, CC.xy(5, 9, CC.RIGHT, CC.DEFAULT));
        setSize(410, 175);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblGID;
    private JTextField txtGID;
    private JLabel lblText;
    private JTextField txtText;
    private JPanel panel1;
    private JButton btnCancel;
    private JButton btnOK;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
