/*
 * Created by JFormDesigner on Thu Feb 09 11:53:11 CET 2012
 */

package op.care.bwinfo;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.Bewohner;
import entity.EntityTools;
import entity.Krankenhaus;
import entity.bwinfo.BWInfos;
import entity.bwinfo.BWInfosTools;
import org.jdesktop.swingx.JXComboBox;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.util.ArrayList;

/**
 * @author Torsten Löhr
 */
public class PnlBWInfo extends JPanel {
    public static final String internalClassID = "nursingrecords.information";
    private Bewohner bewohner;

    public PnlBWInfo(Bewohner bewohner) {
        initComponents();
        this.bewohner = bewohner;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        splitPane1 = new JSplitPane();
        panel1 = new JPanel();
        label1 = new JLabel();
        xComboBox1 = new JXComboBox();
        btnAddArzt = new JButton();
        scrollPane1 = new JScrollPane();
        xTaskPaneContainer1 = new JXTaskPaneContainer();
        panelKH = new JXTaskPane();
        panelDiagnosen = new JXTaskPane();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== splitPane1 ========
        {

            //======== panel1 ========
            {
                panel1.setLayout(new FormLayout(
                        "2*(default, $lcgap), 90dlu:grow, 2*($lcgap, default)",
                        "2*(default), $lgap, default:grow, $lgap, default"));

                //---- label1 ----
                label1.setText("Hausarzt");
                label1.setFont(new Font("Arial", Font.BOLD, 16));
                panel1.add(label1, CC.xy(3, 2));

                //---- xComboBox1 ----
                xComboBox1.setFont(new Font("Arial", Font.PLAIN, 16));
                panel1.add(xComboBox1, CC.xy(5, 2));

                //---- btnAddArzt ----
                btnAddArzt.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
                panel1.add(btnAddArzt, CC.xy(7, 2));

                //======== scrollPane1 ========
                {

                    //======== xTaskPaneContainer1 ========
                    {

                        //======== panelKH ========
                        {
                            panelKH.setFont(new Font("Arial", Font.BOLD, 16));
                            panelKH.setTitle("Krankenhaus Aufenthalte");
                            Container panelKHContentPane = panelKH.getContentPane();
                            panelKHContentPane.setLayout(new BoxLayout(panelKHContentPane, BoxLayout.PAGE_AXIS));
                        }
                        xTaskPaneContainer1.add(panelKH);

                        //======== panelDiagnosen ========
                        {
                            panelDiagnosen.setTitle("Diagnosen");
                            panelDiagnosen.setFont(new Font("Arial", Font.BOLD, 16));
                            Container panelDiagnosenContentPane = panelDiagnosen.getContentPane();
                            panelDiagnosenContentPane.setLayout(new BoxLayout(panelDiagnosenContentPane, BoxLayout.X_AXIS));
                        }
                        xTaskPaneContainer1.add(panelDiagnosen);
                    }
                    scrollPane1.setViewportView(xTaskPaneContainer1);
                }
                panel1.add(scrollPane1, CC.xywh(3, 4, 5, 1, CC.DEFAULT, CC.FILL));
            }
            splitPane1.setLeftComponent(panel1);
        }
        add(splitPane1);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private void initPanel() {

    }

    private void createKHAufenthalte() {

        BWInfos bwInfos = new BWInfos(4l, BWInfosTools.TypeID_KH, "Test", bewohner);
        java.util.List<BWInfos> list = new ArrayList<BWInfos>();
        list.add(bwInfos);

        for (BWInfos info : list) {

            Krankenhaus kh = EntityTools.find(Krankenhaus.class, info.getFk());

            JXTaskPane singleKHPanel = new JXTaskPane(kh.getName() + ", " + kh.getOrt() + " (" + DateFormat.getDateInstance(DateFormat.SHORT).format(info.getVon()) + " - =>|");

            singleKHPanel.add(new JLabel(kh.getName()));
            singleKHPanel.add(new JLabel(kh.getTel()));
        }


    }

    private void createKHAufenthalt() {

    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JSplitPane splitPane1;
    private JPanel panel1;
    private JLabel label1;
    private JXComboBox xComboBox1;
    private JButton btnAddArzt;
    private JScrollPane scrollPane1;
    private JXTaskPaneContainer xTaskPaneContainer1;
    private JXTaskPane panelKH;
    private JXTaskPane panelDiagnosen;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
