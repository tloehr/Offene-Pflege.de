/*
 * Created by JFormDesigner on Fri Feb 27 15:52:33 CET 2015
 */

package de.offene_pflege.op.care.prescription;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.offene_pflege.entity.info.ResInfo;
import de.offene_pflege.entity.info.ResInfoTools;
import de.offene_pflege.entity.info.ResInfoTypeTools;
import de.offene_pflege.entity.prescription.Prescription;
import de.offene_pflege.entity.system.Commontags;
import de.offene_pflege.entity.system.CommontagsTools;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.care.info.PnlEditResInfo;
import de.offene_pflege.op.tools.MyJDialog;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;

/**
 * @author Torsten Löhr
 */
public class DlgAnnotations extends MyJDialog {


    private final Prescription prescription;
    private final Closure actionEvent;

    public DlgAnnotations(Prescription prescription, Closure actionEvent) {
        super();
        this.prescription = prescription;
        this.actionEvent = actionEvent;
        setTitle(SYSTools.xx("nursingrecords.prescription.edit.annotations"));
        initComponents();
        initDialog();
    }

    private void initDialog() {
//        pnlResInfo.setLayout(new BoxLayout(pnlResInfo, BoxLayout.PAGE_AXIS));

        listCommontTags.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                JLabel comp = (JLabel) super.getListCellRendererComponent(list, value.toString(), index, isSelected, cellHasFocus);
                comp.setIcon(SYSConst.icon16tagPurple);
                return comp;
            }
        });

        DefaultListModel<Commontags> listModel = new DefaultListModel<>();
        for (Commontags tag : prescription.getCommontags()) {
            if (CommontagsTools.isAnnotationNecessary(tag)) {
                listModel.addElement(tag);
            }
        }
        listCommontTags.setModel(listModel);

    }

    private void listCommontTagsValueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;

        Commontags tag = (Commontags) listCommontTags.getSelectedValue();
        SwingUtilities.invokeLater(() -> {
            pnlResInfo.removeAll();
            revalidate();
            repaint();
        });

        if (!CommontagsTools.isAnnotationNecessary(tag)) return;

        int mode = PnlEditResInfo.EDIT;
        ResInfo annotation = ResInfoTools.getAnnotation4Prescription(prescription, tag);
        if (annotation == null) {
            annotation = ResInfoTools.createResInfo(ResInfoTypeTools.getResInfoType4Annotation(tag), prescription.getResident(), OPDE.getLogin().getUser());
            annotation.setPrescription(prescription);
            mode = PnlEditResInfo.NEW;
        }

        PnlEditResInfo pnl = new PnlEditResInfo(annotation, o -> {
            setVisible(false);
            actionEvent.execute(o);
        }, Color.WHITE);
        pnl.setPanelEnabled(true, mode);

        SwingUtilities.invokeLater(() -> {
            pnlResInfo.add(pnl.getPanel());

            revalidate();
            repaint();
        });

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        scrollPane1 = new JScrollPane();
        listCommontTags = new JList();
        scrollPane2 = new JScrollPane();
        pnlResInfo = new JPanel();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "52dlu, $lcgap, default:grow",
            "default:grow, 2*($lgap, default)"));

        //======== scrollPane1 ========
        {

            //---- listCommontTags ----
            listCommontTags.addListSelectionListener(e -> listCommontTagsValueChanged(e));
            scrollPane1.setViewportView(listCommontTags);
        }
        contentPane.add(scrollPane1, CC.xy(1, 1, CC.DEFAULT, CC.FILL));

        //======== scrollPane2 ========
        {

            //======== pnlResInfo ========
            {
                pnlResInfo.setLayout(new BoxLayout(pnlResInfo, BoxLayout.PAGE_AXIS));
            }
            scrollPane2.setViewportView(pnlResInfo);
        }
        contentPane.add(scrollPane2, CC.xy(3, 1, CC.DEFAULT, CC.FILL));
        setSize(865, 565);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane scrollPane1;
    private JList listCommontTags;
    private JScrollPane scrollPane2;
    private JPanel pnlResInfo;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
