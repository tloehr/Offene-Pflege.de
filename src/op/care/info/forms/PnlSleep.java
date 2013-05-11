/*
 * Created by JFormDesigner on Fri May 10 16:35:10 CEST 2013
 */

package op.care.info.forms;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.info.ResInfo;
import entity.info.ResInfoTools;
import op.tools.GUITools;
import op.tools.SYSTools;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

/**
 * @author Torsten Löhr
 */
public class PnlSleep extends JPanel {

    private final ResInfo resInfo;
    private Properties content;
    private ArrayList<Component> components;
//    private HashMap<String, Component> components;

    public PnlSleep(ResInfo resInfo) {
        this.resInfo = resInfo;
        initComponents();
        initPanel();

    }

    private void initPanel() {
        content = ResInfoTools.getContent(resInfo);
//        components = new HashMap<String, Component>();
//        for (AbstractButton btn : Collections.list(buttonGroup2.getElements())) {
//            components.put(btn.getName(), btn);
//        }
//        components.put(txtHabits.getName(), txtHabits);
//        components.put(cbSleep1.getName(), cbSleep1);
//        components.put(cbSleep2.getName(), cbSleep2);
//        components.put(cbSleep3.getName(), cbSleep3);
//        components.put(cbSleep4.getName(), cbSleep4);


        components = new ArrayList<Component>();
        components.addAll(Collections.list(buttonGroup2.getElements()));

        components.add(txtHabits);
        components.add(cbSleep1);
        components.add(cbSleep2);
        components.add(cbSleep3);
        components.add(cbSleep4);

        GUITools.load(content, components);
    }



    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblCat = new JLabel();
        lbl = new JLabel();
        panel1 = new JPanel();
        cbSleep1 = new JCheckBox();
        cbSleep2 = new JCheckBox();
        cbSleep3 = new JCheckBox();
        cbSleep4 = new JCheckBox();
        label1 = new JLabel();
        txtHabits = new JTextField();
        label2 = new JLabel();
        panel2 = new JPanel();
        rbSleep1 = new JRadioButton();
        rbSleep2 = new JRadioButton();
        rbSleep3 = new JRadioButton();
        rbSleep4 = new JRadioButton();
        buttonGroup2 = new ButtonGroup();

        //======== this ========
        setLayout(new FormLayout(
                "default, $lcgap, default:grow, $lcgap, default",
                "default, $lgap, default, $ugap, default, $lgap, default, $ugap, default, $lgap, default, $ugap, default, $lgap, default"));

        //---- lblCat ----
        lblCat.setText("Ruhen und Schlafen");
        lblCat.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblCat, CC.xy(3, 3));

        //---- lbl ----
        lbl.setText("Schlafverhalten");
        add(lbl, CC.xy(3, 5));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

            //---- cbSleep1 ----
            cbSleep1.setText("normal");
            cbSleep1.setName("normal");
            panel1.add(cbSleep1);

            //---- cbSleep2 ----
            cbSleep2.setText("Einschlafst\u00f6rungen");
            cbSleep2.setName("fallAsleep");
            panel1.add(cbSleep2);

            //---- cbSleep3 ----
            cbSleep3.setText("Durchschlafst\u00f6rungen");
            cbSleep3.setName("sleepThrough");
            panel1.add(cbSleep3);

            //---- cbSleep4 ----
            cbSleep4.setText("Unruhe");
            cbSleep4.setName("restless");
            panel1.add(cbSleep4);
        }
        add(panel1, CC.xy(3, 7));

        //---- label1 ----
        label1.setText("Schlafgewohnheiten");
        add(label1, CC.xy(3, 9));

        //---- txtHabits ----
        txtHabits.setName("sleepingHabits");
        add(txtHabits, CC.xy(3, 11));

        //---- label2 ----
        label2.setText("Tag / Nachtrhythmus");
        add(label2, CC.xy(3, 13));

        //======== panel2 ========
        {
            panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

            //---- rbSleep1 ----
            rbSleep1.setText("unauff\u00e4llig");
            rbSleep1.setName("normal");
            panel2.add(rbSleep1);

            //---- rbSleep2 ----
            rbSleep2.setText("teilweise umgekehrt");
            rbSleep2.setName("partlyReversed");
            panel2.add(rbSleep2);

            //---- rbSleep3 ----
            rbSleep3.setText("vollst\u00e4ndig umgekehrt");
            rbSleep3.setName("reversed");
            panel2.add(rbSleep3);

            //---- rbSleep4 ----
            rbSleep4.setText("kein Rhythmus erkennbar");
            rbSleep4.setName("noRhythm");
            panel2.add(rbSleep4);
        }
        add(panel2, CC.xy(3, 15));

        //---- buttonGroup2 ----
        buttonGroup2.add(rbSleep1);
        buttonGroup2.add(rbSleep2);
        buttonGroup2.add(rbSleep3);
        buttonGroup2.add(rbSleep4);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblCat;
    private JLabel lbl;
    private JPanel panel1;
    private JCheckBox cbSleep1;
    private JCheckBox cbSleep2;
    private JCheckBox cbSleep3;
    private JCheckBox cbSleep4;
    private JLabel label1;
    private JTextField txtHabits;
    private JLabel label2;
    private JPanel panel2;
    private JRadioButton rbSleep1;
    private JRadioButton rbSleep2;
    private JRadioButton rbSleep3;
    private JRadioButton rbSleep4;
    private ButtonGroup buttonGroup2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
