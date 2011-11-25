/*
 * Created by JFormDesigner on Fri Nov 25 10:08:56 CET 2011
 */

package op;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import org.jdesktop.swingx.border.*;

/**
 * @author Torsten Löhr
 */
public class FrmMain extends JFrame {
    public FrmMain() {
        initComponents();



    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        pnlMainMessage = new JPanel();
        label1 = new JLabel();
        label2 = new JLabel();
        progressBar1 = new JProgressBar();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "default, $lcgap, default:grow, $lcgap, default",
            "fill:default, $lgap, default:grow, $lgap, default"));

        //======== panel1 ========
        {

            //======== pnlMainMessage ========
            {
                pnlMainMessage.setBackground(new Color(234, 237, 223));
                pnlMainMessage.setBorder(new DropShadowBorder(Color.black, 5, 0.3f, 12, true, true, true, true));

                //---- label1 ----
                label1.setText("Main Message Line for Main Text");
                label1.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 12));
                label1.setForeground(new Color(105, 80, 69));

                //---- label2 ----
                label2.setText("Main Message Line for Main Text");
                label2.setFont(new Font("Arial", Font.PLAIN, 12));
                label2.setForeground(new Color(105, 80, 69));

                //---- progressBar1 ----
                progressBar1.setValue(50);

                GroupLayout pnlMainMessageLayout = new GroupLayout(pnlMainMessage);
                pnlMainMessage.setLayout(pnlMainMessageLayout);
                pnlMainMessageLayout.setHorizontalGroup(
                    pnlMainMessageLayout.createParallelGroup()
                        .addGroup(pnlMainMessageLayout.createSequentialGroup()
                            .addGroup(pnlMainMessageLayout.createParallelGroup()
                                .addGroup(pnlMainMessageLayout.createSequentialGroup()
                                    .addGap(157, 157, 157)
                                    .addGroup(pnlMainMessageLayout.createParallelGroup()
                                        .addComponent(label2, GroupLayout.PREFERRED_SIZE, 404, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label1, GroupLayout.PREFERRED_SIZE, 404, GroupLayout.PREFERRED_SIZE)))
                                .addGroup(pnlMainMessageLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(progressBar1, GroupLayout.PREFERRED_SIZE, 652, GroupLayout.PREFERRED_SIZE)))
                            .addContainerGap(31, Short.MAX_VALUE))
                );
                pnlMainMessageLayout.setVerticalGroup(
                    pnlMainMessageLayout.createParallelGroup()
                        .addGroup(pnlMainMessageLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(label1)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(label2)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(progressBar1, GroupLayout.PREFERRED_SIZE, 6, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap())
                );
            }

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addComponent(pnlMainMessage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(31, Short.MAX_VALUE))
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnlMainMessage, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(63, Short.MAX_VALUE))
            );
        }
        contentPane.add(panel1, CC.xywh(1, 1, 5, 1));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JPanel pnlMainMessage;
    private JLabel label1;
    private JLabel label2;
    private JProgressBar progressBar1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
