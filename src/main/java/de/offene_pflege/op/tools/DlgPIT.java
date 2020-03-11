/*
 * Created by JFormDesigner on Mon Jul 16 15:30:13 CEST 2012
 */

package de.offene_pflege.op.tools;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.commons.collections.Closure;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Date;

/**
 *
 * @author Torsten Löhr
 */
public class DlgPIT extends MyJDialog {
    private Closure actionBlock;
    private PnlPIT pnlPIT;
    private Date pit;

    /**
     * creates a
     * @param text
     * @param actionBlock
     */
    public DlgPIT(String text, Closure actionBlock) {
        super(false);
        this.actionBlock = actionBlock;
        initComponents();
        lblText.setText(text);
        initDialog();
        pack();
        setVisible(true);
    }

    @Override
    public void dispose() {
        super.dispose();
        actionBlock.execute(pit);
    }

    private void initDialog() {
         pnlPIT = new PnlPIT();
         contentPanel.add(pnlPIT, CC.xy(1, 3));
    }

    private void btnOKActionPerformed(ActionEvent e) {
        pit = pnlPIT.getPIT();
        dispose();
    }

    private void btnCancelActionPerformed(ActionEvent e) {
        pit = null;
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        lblText = new JLabel();
        panel1 = new JPanel();
        btnCancel = new JButton();
        btnOK = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.DIALOG);
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new FormLayout(
                    "default:grow",
                    "default, $lgap, fill:default:grow, $lgap, pref"));

                //---- lblText ----
                lblText.setText("text");
                lblText.setFont(new Font("Arial", Font.PLAIN, 14));
                contentPanel.add(lblText, CC.xy(1, 1));

                //======== panel1 ========
                {
                    panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

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
                contentPanel.add(panel1, CC.xy(1, 5, CC.RIGHT, CC.DEFAULT));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel lblText;
    private JPanel panel1;
    private JButton btnCancel;
    private JButton btnOK;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
