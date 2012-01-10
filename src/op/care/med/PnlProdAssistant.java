/*
 * Created by JFormDesigner on Tue Jan 10 16:17:36 CET 2012
 */

package op.care.med;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import org.jdesktop.swingx.*;

/**
 * @author Torsten Löhr
 */
public class PnlProdAssistant extends JPanel {
    public PnlProdAssistant() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        splitPane1 = new JSplitPane();
        splitPane7 = new JSplitPane();
        panel1 = new JPanel();
        label1 = new JLabel();
        txtProd = new JTextField();
        btnClearProd = new JButton();
        panel2 = new JPanel();
        label2 = new JLabel();
        scrollPane1 = new JScrollPane();
        list1 = new JList();
        splitPane3 = new JSplitPane();
        splitZusatz = new JSplitPane();
        splitPane5 = new JSplitPane();
        splitPackung = new JSplitPane();
        splitHersteller = new JSplitPane();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== splitPane1 ========
        {
            splitPane1.setDividerSize(20);
            splitPane1.setDividerLocation(300);
            splitPane1.setFont(new Font("sansserif", Font.PLAIN, 24));

            //======== splitPane7 ========
            {
                splitPane7.setOrientation(JSplitPane.VERTICAL_SPLIT);

                //======== panel1 ========
                {
                    panel1.setLayout(new FormLayout(
                        "$rgap, $lcgap, default:grow, $lcgap, default, $lcgap, $rgap",
                        "$rgap, 2*($lgap, default), $lgap, default:grow, $lgap, default"));

                    //---- label1 ----
                    label1.setText("Medizin-Produkt");
                    label1.setFont(new Font("sansserif", Font.PLAIN, 18));
                    label1.setBackground(new Color(204, 204, 255));
                    label1.setOpaque(true);
                    label1.setForeground(Color.black);
                    label1.setHorizontalAlignment(SwingConstants.CENTER);
                    panel1.add(label1, CC.xywh(1, 3, 5, 1));
                    panel1.add(txtProd, CC.xy(3, 5));

                    //---- btnClearProd ----
                    btnClearProd.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/button_cancel.png")));
                    btnClearProd.setContentAreaFilled(false);
                    btnClearProd.setBorderPainted(false);
                    panel1.add(btnClearProd, CC.xy(5, 5));
                }
                splitPane7.setTopComponent(panel1);

                //======== panel2 ========
                {
                    panel2.setLayout(new FormLayout(
                        "default:grow",
                        "2*(default, $lgap), default"));

                    //---- label2 ----
                    label2.setText("text");
                    panel2.add(label2, CC.xy(1, 1));

                    //======== scrollPane1 ========
                    {
                        scrollPane1.setViewportView(list1);
                    }
                    panel2.add(scrollPane1, CC.xy(1, 3, CC.DEFAULT, CC.FILL));
                }
                splitPane7.setBottomComponent(panel2);
            }
            splitPane1.setLeftComponent(splitPane7);

            //======== splitPane3 ========
            {

                //======== splitZusatz ========
                {
                    splitZusatz.setOrientation(JSplitPane.VERTICAL_SPLIT);
                    splitZusatz.setDividerLocation(250);
                }
                splitPane3.setLeftComponent(splitZusatz);

                //======== splitPane5 ========
                {
                    splitPane5.setDividerLocation(250);

                    //======== splitPackung ========
                    {
                        splitPackung.setOrientation(JSplitPane.VERTICAL_SPLIT);
                    }
                    splitPane5.setLeftComponent(splitPackung);

                    //======== splitHersteller ========
                    {
                        splitHersteller.setOrientation(JSplitPane.VERTICAL_SPLIT);
                    }
                    splitPane5.setRightComponent(splitHersteller);
                }
                splitPane3.setRightComponent(splitPane5);
            }
            splitPane1.setRightComponent(splitPane3);
        }
        add(splitPane1);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JSplitPane splitPane1;
    private JSplitPane splitPane7;
    private JPanel panel1;
    private JLabel label1;
    private JTextField txtProd;
    private JButton btnClearProd;
    private JPanel panel2;
    private JLabel label2;
    private JScrollPane scrollPane1;
    private JList list1;
    private JSplitPane splitPane3;
    private JSplitPane splitZusatz;
    private JSplitPane splitPane5;
    private JSplitPane splitPackung;
    private JSplitPane splitHersteller;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
