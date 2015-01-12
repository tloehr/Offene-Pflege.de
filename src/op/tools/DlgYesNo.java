/*
 * Created by JFormDesigner on Fri May 18 14:51:51 CEST 2012
 */

package op.tools;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import op.system.Validator;
import org.apache.commons.collections.Closure;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Torsten Löhr
 */
public class DlgYesNo extends MyJDialog {


    private Validator validator;
    private Closure actionBlock;
    //    private int result;
    private boolean editorMode;

    public DlgYesNo(String message, Icon icon, Closure actionBlock) {
        super(false);
        validator = null;
        editorMode = false;
        initComponents();
        this.actionBlock = actionBlock;
        txtMessage.setText(SYSTools.toHTML("<div id=\"fonttext\">" + message + "</div>"));
        lblTitle.setText(null);
//        result = JOptionPane.CANCEL_OPTION;
        lblIcon.setIcon(icon);
        pack();
        setVisible(true);
    }

    /**
     * Same as the other constructor, but converts this Dlg into a Texteditor.
     *
     * @param icon
     * @param actionBlock
     */
    public DlgYesNo(Icon icon, Closure actionBlock, String title, String preset, Validator validator) {
        super(false);
        this.validator = validator;


        editorMode = true;
        initComponents();

        this.actionBlock = actionBlock;
        lblTitle.setText(SYSTools.xx(title));
        txtMessage.setEditable(true);
        txtMessage.setContentType("text/plain");
        txtMessage.setText(SYSTools.catchNull(preset));
        lblIcon.setIcon(icon);
        pack();
        setVisible(true);
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        if (editorMode) {
            actionBlock.execute(null);
        } else {
            actionBlock.execute(JOptionPane.NO_OPTION);
        }
        dispose();
    }

    private void okButtonActionPerformed(ActionEvent e) {
        if (editorMode) {
            if (validator != null) {
                actionBlock.execute(validator.isValid(txtMessage.getText()) ? validator.parse(txtMessage.getText()) : null);
            } else {
                actionBlock.execute(txtMessage.getText() == null ? null : txtMessage.getText().trim());
            }
        } else {
            actionBlock.execute(JOptionPane.YES_OPTION);
        }
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        lblTitle = new JLabel();
        lblIcon = new JLabel();
        scrollPane1 = new JScrollPane();
        txtMessage = new JTextPane();
        buttonBar = new JPanel();
        cancelButton = new JButton();
        okButton = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new FormLayout(
                        "default, $lcgap, 171dlu",
                        "pref, $lgap, 124dlu, $lgap, pref"));

                //---- lblTitle ----
                lblTitle.setText("text");
                contentPanel.add(lblTitle, CC.xywh(1, 1, 3, 1));
                contentPanel.add(lblIcon, CC.xy(1, 3, CC.DEFAULT, CC.TOP));

                //======== scrollPane1 ========
                {

                    //---- txtMessage ----
                    txtMessage.setFont(new Font("Arial", Font.PLAIN, 18));
                    txtMessage.setEditable(false);
                    txtMessage.setContentType("text/html");
                    scrollPane1.setViewportView(txtMessage);
                }
                contentPanel.add(scrollPane1, CC.xy(3, 3, CC.FILL, CC.FILL));

                //======== buttonBar ========
                {
                    buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                    buttonBar.setLayout(new BoxLayout(buttonBar, BoxLayout.LINE_AXIS));

                    //---- cancelButton ----
                    cancelButton.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
                    cancelButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cancelButtonActionPerformed(e);
                        }
                    });
                    buttonBar.add(cancelButton);

                    //---- okButton ----
                    okButton.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
                    okButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            okButtonActionPerformed(e);
                        }
                    });
                    buttonBar.add(okButton);
                }
                contentPanel.add(buttonBar, CC.xy(3, 5, CC.RIGHT, CC.DEFAULT));
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
    private JLabel lblTitle;
    private JLabel lblIcon;
    private JScrollPane scrollPane1;
    private JTextPane txtMessage;
    private JPanel buttonBar;
    private JButton cancelButton;
    private JButton okButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
