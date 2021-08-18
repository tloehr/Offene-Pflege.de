/*
 * Created by JFormDesigner on Wed Aug 18 15:51:25 CEST 2021
 */

package de.offene_pflege.op.care.info;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.combobox.DateExComboBox;
import de.offene_pflege.entity.info.ResInfo;
import de.offene_pflege.entity.info.ResInfoTools;
import org.apache.commons.collections4.Closure;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Torsten Löhr
 */
public class PnlMoveResinfo extends JPanel {
    private final long connectionID;
    private final Closure actionBlock;
    private final List<ResInfo> resInfos;

    public PnlMoveResinfo(long connectionID, Closure<List<ResInfo>> actionBlock) {
        super();
        this.connectionID = connectionID;
        this.actionBlock = actionBlock;
        this.resInfos = new ArrayList<>(ResInfoTools.getAll(connectionID));
        initComponents();
        initDialog();
    }

    private void initDialog() {
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        scrollPane1 = new JScrollPane();
        tblResInfos = new JTable();
        dateExComboBox1 = new DateExComboBox();
        label1 = new JLabel();
        dateExComboBox2 = new DateExComboBox();
        label2 = new JLabel();

        //======== this ========
        setLayout(new FormLayout(
            "default:grow, $lcgap, 56dlu",
            "4*(default, $lgap), fill:default:grow"));

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(tblResInfos);
        }
        add(scrollPane1, CC.xywh(1, 1, 1, 9));
        add(dateExComboBox1, CC.xy(3, 1));

        //---- label1 ----
        label1.setText("text");
        add(label1, CC.xy(3, 3));
        add(dateExComboBox2, CC.xy(3, 5));

        //---- label2 ----
        label2.setText("text");
        add(label2, CC.xy(3, 7));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane scrollPane1;
    private JTable tblResInfos;
    private DateExComboBox dateExComboBox1;
    private JLabel label1;
    private DateExComboBox dateExComboBox2;
    private JLabel label2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
