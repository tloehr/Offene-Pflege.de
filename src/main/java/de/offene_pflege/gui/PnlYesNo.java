/*
 * Created by JFormDesigner on Fri May 18 14:51:51 CEST 2012
 */

package de.offene_pflege.gui;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.offene_pflege.op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Torsten Löhr
 */
public class PnlYesNo extends JPanel {


//    private Validator validator;
    private Closure actionBlock;
    //    private int result;
    private boolean editorMode;

    public PnlYesNo(String message, String title,  Icon icon, Closure actionBlock) {
        super(false);
//        validator = null;
        editorMode = false;
        initComponents();
        this.actionBlock = actionBlock;
        txtMessage.setText(SYSTools.toHTML("<div id=\"font16\">" + message + "</div>"));
        lblTitle.setText(SYSTools.xx(title));
//        result = JOptionPane.CANCEL_OPTION;
        lblTitle.setIcon(icon);
    }

//    /**
//     * Same as the other constructor, but converts this Dlg into a Texteditor.
//     *
//     * @param icon
//     * @param actionBlock
//     */
//    public PnlYesNo(Icon icon, Closure actionBlock, String title, String preset, Validator validator) {
//        super(true);
//
//        this.validator = validator;
//
//
//        editorMode = true;
//        initComponents();
//
//        this.actionBlock = actionBlock;
//        lblTitle.setText(SYSTools.xx(title));
//        txtMessage.setEditable(true);
//        txtMessage.setContentType("text/plain");
//        txtMessage.setText(SYSTools.catchNull(preset));
//
//        setVisible(true);
//    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        if (editorMode) {
            actionBlock.execute(null);
        } else {
            actionBlock.execute(JOptionPane.NO_OPTION);
        }
    }

    private void okButtonActionPerformed(ActionEvent e) {
        if (editorMode) {
//            if (validator != null) {
//                actionBlock.execute(validator.isValid(txtMessage.getText()) ? validator.parse(txtMessage.getText()) : null);
//            } else {
//                actionBlock.execute(txtMessage.getText() == null ? null : txtMessage.getText().trim());
//            }
        } else {
            actionBlock.execute(JOptionPane.YES_OPTION);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        lblTitle = new JLabel();
        scrollPane1 = new JScrollPane();
        txtMessage = new JTextPane();
        buttonBar = new JPanel();
        cancelButton = new JButton();
        okButton = new JButton();

        //======== this ========
        setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new FormLayout(
                    "171dlu:grow",
                    "pref, $lgap, fill:124dlu:grow, $lgap, pref"));

                //---- lblTitle ----
                lblTitle.setText("text");
                lblTitle.setFont(new Font("Arial", Font.PLAIN, 16));
                contentPanel.add(lblTitle, CC.xy(1, 1));

                //======== scrollPane1 ========
                {

                    //---- txtMessage ----
                    txtMessage.setFont(new Font("Arial", Font.PLAIN, 18));
                    txtMessage.setEditable(false);
                    txtMessage.setContentType("text/html");
                    scrollPane1.setViewportView(txtMessage);
                }
                contentPanel.add(scrollPane1, CC.xy(1, 3, CC.FILL, CC.FILL));

                //======== buttonBar ========
                {
                    buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                    buttonBar.setLayout(new BoxLayout(buttonBar, BoxLayout.LINE_AXIS));

                    //---- cancelButton ----
                    cancelButton.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
                    cancelButton.addActionListener(e -> cancelButtonActionPerformed(e));
                    buttonBar.add(cancelButton);

                    //---- okButton ----
                    okButton.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                    okButton.addActionListener(e -> okButtonActionPerformed(e));
                    buttonBar.add(okButton);
                }
                contentPanel.add(buttonBar, CC.xy(1, 5, CC.RIGHT, CC.DEFAULT));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);
        }
        add(dialogPane, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel lblTitle;
    private JScrollPane scrollPane1;
    private JTextPane txtMessage;
    private JPanel buttonBar;
    private JButton cancelButton;
    private JButton okButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
