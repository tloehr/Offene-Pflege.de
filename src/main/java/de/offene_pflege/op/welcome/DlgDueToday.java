/*
 * Created by JFormDesigner on Thu Dec 07 16:09:43 CET 2023
 */

package de.offene_pflege.op.welcome;

import de.offene_pflege.entity.EntityTools;
import de.offene_pflege.entity.system.SYSProps;
import de.offene_pflege.entity.system.SYSPropsTools;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;
import de.offene_pflege.op.tools.SoundPlayer;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;

import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.Format;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author tloehr
 */
@Log4j2
public class DlgDueToday extends JDialog {
    public DlgDueToday(Window owner) {
        super(owner);
        initComponents();

        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                SoundPlayer player = new SoundPlayer();
                player.play("audio/phone-outgoing-busy.wav");
                return null;
            }
        };
        worker.execute();
    }

    private void ok(ActionEvent e) {
        dispose();
    }

    @Override
    public void dispose() {
        if (cbShutUp.isSelected())
            SYSPropsTools.storeProp(SYSPropsTools.KEY_LEAVE_ME_ALONE_TODAY, LocalDate.now().format(DateTimeFormatter.ISO_DATE), OPDE.getMe());
        super.dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        scrollPane1 = new JScrollPane();
        textArea1 = new JTextPane();
        buttonBar = new JPanel();
        cbShutUp = new JCheckBox();
        okButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Wichtige Information");
        setModal(true);
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));

                //======== scrollPane1 ========
                {

                    //---- textArea1 ----
                    textArea1.setContentType("text/html");
                    textArea1.setEditable(false);
                    textArea1.setText("<html><head>\n<style type=\"text/css\" media=\"all\">\nbody { margin:0;\npadding:0;\n}\n#fontsmall { font-size:10px; font-weight:bold; font-family:Arial,sans-serif;}\n#fonth1 { font-size:24px; font-family:Arial,sans-serif;}\n#fonth2 { font-size:16px; font-weight:bold; font-family:Arial,sans-serif;}\n#fonth3 { font-size:12px; font-weight:bold; font-family:Arial,sans-serif;}\n#fonttext { font-size:12px; font-family:Arial,sans-serif;}\n#font14 { font-size:14px; font-family:Arial,sans-serif;}\n#font16 { font-size:16px; font-family:Arial,sans-serif;}\n#font18 { font-size:18px; font-family:Arial,sans-serif;}\n#font20 { font-size:20px; font-family:Arial,sans-serif;}\n#fonttextgray { font-size:12px; background-color:#CCCCCC; font-family:Arial,sans-serif; -webkit-print-color-adjust:exact;}\n#fonttextgray14 { font-size:14px; background-color:#CCCCCC; font-family:Arial,sans-serif; -webkit-print-color-adjust:exact;}\n#fonttextgray16 { font-size:16px; background-color:#CCCCCC; font-family:Arial,sans-serif; -webkit-print-color-adjust:exact;}\n#fonttextgray18 { font-size:18px; background-color:#CCCCCC; font-family:Arial,sans-serif; -webkit-print-color-adjust:exact;}\n#fonttextgray20 { font-size:20px; background-color:#CCCCCC; font-family:Arial,sans-serif; -webkit-print-color-adjust:exact;}\n.boxed {border: 1px solid black; background-color:#CCCCCC;}\n</style></head><body><h1 id=\"fonth1\" >Bitte beachten</h1>\n<p id=\"font16\">\nHeute sind Depot-Spritzen und/oder andere Medikamente dran, die nicht t&auml;glich verabreicht werden.</p>\n</body></html>");
                    scrollPane1.setViewportView(textArea1);
                }
                contentPanel.add(scrollPane1);
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

                //---- cbShutUp ----
                cbShutUp.setText("Heute nicht mehr melden");
                cbShutUp.setSelected(true);
                buttonBar.add(cbShutUp, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- okButton ----
                okButton.setText("OK");
                okButton.addActionListener(e -> ok(e));
                buttonBar.add(okButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(562, 307);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JScrollPane scrollPane1;
    private JTextPane textArea1;
    private JPanel buttonBar;
    private JCheckBox cbShutUp;
    private JButton okButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
