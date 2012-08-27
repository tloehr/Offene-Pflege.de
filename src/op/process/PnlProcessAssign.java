/*
 * Created by JFormDesigner on Mon Aug 27 16:33:56 CEST 2012
 */

package op.process;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.process.QProcess;
import entity.process.QProcessElement;
import entity.process.QProcessTools;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Torsten Löhr
 */
public class PnlProcessAssign extends JPanel {

    private QProcessElement element;
    private Closure afterAction;
    private ArrayList<QProcess> assigned, unassigned;

    public PnlProcessAssign(QProcessElement element, Closure afterAction) {
        this.element = element;
        this.afterAction = afterAction;
        initComponents();
        initPanel();
    }


    private void initPanel() {
        assigned = element.getAttachedProcesses();
        Collections.sort(assigned);
        unassigned = new ArrayList<QProcess>(QProcessTools.getProcesses4(element.getResident()));
        Collections.sort(unassigned);
        listAssigned.setModel(SYSTools.list2dlm(assigned));
        listUnassigned.setModel(SYSTools.list2dlm(unassigned));
    }

    private void listAssignedMouseClicked(MouseEvent e) {

    }

    private void listUnassignedMouseClicked(MouseEvent e) {
        // TODO add your code here
    }

    private void listAssignedValueChanged(ListSelectionEvent e) {
        // TODO: hier gehts weiter
    }

    private void listUnassignedValueChanged(ListSelectionEvent e) {
        // TODO add your code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        scrollPane1 = new JScrollPane();
        listAssigned = new JList();
        scrollPane2 = new JScrollPane();
        listUnassigned = new JList();

        //======== this ========
        setLayout(new FormLayout(
            "3*(default, $lcgap), default",
            "2*(default, $lgap), default"));

        //======== scrollPane1 ========
        {

            //---- listAssigned ----
            listAssigned.setFont(new Font("Arial", Font.PLAIN, 14));
            listAssigned.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    listAssignedMouseClicked(e);
                }
            });
            listAssigned.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    listAssignedValueChanged(e);
                }
            });
            scrollPane1.setViewportView(listAssigned);
        }
        add(scrollPane1, CC.xy(3, 3));

        //======== scrollPane2 ========
        {

            //---- listUnassigned ----
            listUnassigned.setFont(new Font("Arial", Font.PLAIN, 14));
            listUnassigned.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    listUnassignedMouseClicked(e);
                }
            });
            listUnassigned.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    listUnassignedValueChanged(e);
                }
            });
            scrollPane2.setViewportView(listUnassigned);
        }
        add(scrollPane2, CC.xy(5, 3));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane scrollPane1;
    private JList listAssigned;
    private JScrollPane scrollPane2;
    private JList listUnassigned;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
