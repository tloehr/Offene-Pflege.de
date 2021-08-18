/*
 * Created by JFormDesigner on Fri Aug 03 14:53:03 CEST 2012
 */

package de.offene_pflege.op.care.nursingprocess;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.offene_pflege.entity.info.ResidentTools;
import de.offene_pflege.entity.nursingprocess.NursingProcess;
import de.offene_pflege.entity.nursingprocess.NursingProcessTools;
import de.offene_pflege.entity.system.SYSPropsTools;
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.op.tools.MyJDialog;
import de.offene_pflege.op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Torsten Löhr
 */
public class PnlTemplate extends MyJDialog {
    public static final String internalClassID = "nursingrecords.nursingprocess.pnltemplate";
    private JToggleButton tbInactive;
    private Closure actionBlock;

    public PnlTemplate(Closure actionBlock) {
        super(false);
        this.actionBlock = actionBlock;
        initComponents();
        initPanel();
        pack();
    }

    private void initPanel() {
        txtSearch.setToolTipText(SYSTools.xx("nursingrecords.nursingprocess.pnltemplate.searchtopic"));
        btnSearch.setText(SYSTools.xx("misc.msg.search"));
        tbInactive = GUITools.getNiceToggleButton(SYSTools.xx("nursingrecords.nursingprocess.pnltemplate.inactive"));
        SYSPropsTools.restoreState("nursingrecords.nursingprocess.pnltemplate.tbInactive", tbInactive);
        tbInactive.addItemListener(e -> {
            SYSPropsTools.storeState("nursingrecords.nursingprocess.pnltemplate:tbInactive", tbInactive);
            refreshDisplay();
        });
//        tbSearch4Residents2 = GUITools.getNiceToggleButton(SYSTools.xx("nursingrecords.nursingprocess.pnltemplate.search4residents"));
//        SYSPropsTools.restoreState("nursingrecords.nursingprocess.pnltemplate.tbSearch4Residents2", tbSearch4Residents2);
//        tbSearch4Residents2.addItemListener(e -> {
//            SYSPropsTools.storeState("nursingrecords.nursingprocess.pnltemplate:tbSearch4Residents2", tbSearch4Residents2);
//            refreshDisplay();
//        });
        lstTemplates.setCellRenderer(getListCellRenderer());
        lstTemplates.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && lstTemplates.getSelectedValue() != null) {
                txtContent.setText(SYSTools.toHTML(NursingProcessTools.getAsHTML((NursingProcess) lstTemplates.getSelectedValue(), true, false, false, false)));
            }
        });

        JPanel jPanel = new JPanel();
        BoxLayout bl = new BoxLayout(jPanel, BoxLayout.LINE_AXIS);
        jPanel.setLayout(bl);

        jPanel.add(tbInactive);
//        jPanel.add(tbSearch4Residents2);
        add(jPanel, CC.xy(3, 7, CC.LEFT, CC.DEFAULT));

    }

    private void refreshDisplay() {
        if (txtSearch.getText().length() < 4) return;
        java.util.List<NursingProcess> list = NursingProcessTools.getTemplates(txtSearch.getText(), tbInactive.isSelected(), true);
        lstTemplates.setModel(SYSTools.list2dlm(list));
    }

    private void txtSearchActionPerformed(ActionEvent e) {
        refreshDisplay();
    }

    private void lstTemplatesMouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            dispose();
            actionBlock.execute(lstTemplates.getSelectedValue());
        }
    }

    private ListCellRenderer getListCellRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean b1) {
                if (o instanceof NursingProcess) {
                    NursingProcess np = (NursingProcess) o;
                    setText("<html>" + (np.isClosed() ? "<s>" : "") + np.getTopic() + (np.isClosed() ? "</s>" : "") + " (" + ResidentTools.getTextCompact(((NursingProcess) o).getResident()) + ")" + "</html>");
                }
                setForeground(Color.black);
                if (isSelected) {
                    setBackground(new Color(200, 210, 220));
                } else {
                    setBackground(Color.white);
                }
                return this;
            }
        };
    }

    private void btnApplyActionPerformed(ActionEvent e) {
        dispose();
        actionBlock.execute(lstTemplates.getSelectedValue());
    }

    private void btnCancelActionPerformed(ActionEvent e) {
        dispose();
        actionBlock.execute(null);
    }

    private void btnSearchActionPerformed(ActionEvent e) {
        refreshDisplay();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel2 = new JPanel();
        txtSearch = new JTextField();
        btnSearch = new JButton();
        splitPane1 = new JSplitPane();
        scrollTemplates = new JScrollPane();
        lstTemplates = new JList();
        scrollPane1 = new JScrollPane();
        txtContent = new JTextPane();
        panel1 = new JPanel();
        btnCancel = new JButton();
        btnApply = new JButton();

        //======== this ========
        var contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "default, $lcgap, 200dlu, $rgap, [250dlu,min]:grow, $lcgap, default",
            "2*(default, $lgap), [220dlu,min]:grow, 3*($lgap, default)"));

        //======== panel2 ========
        {
            panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

            //---- txtSearch ----
            txtSearch.setFont(new Font("Arial", Font.PLAIN, 14));
            txtSearch.addActionListener(e -> txtSearchActionPerformed(e));
            panel2.add(txtSearch);

            //---- btnSearch ----
            btnSearch.setText("Suchen");
            btnSearch.addActionListener(e -> btnSearchActionPerformed(e));
            panel2.add(btnSearch);
        }
        contentPane.add(panel2, CC.xywh(3, 3, 3, 1));

        //======== splitPane1 ========
        {
            splitPane1.setDividerLocation(400);

            //======== scrollTemplates ========
            {

                //---- lstTemplates ----
                lstTemplates.setFont(new Font("Arial", Font.PLAIN, 14));
                lstTemplates.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                lstTemplates.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        lstTemplatesMouseClicked(e);
                    }
                });
                scrollTemplates.setViewportView(lstTemplates);
            }
            splitPane1.setLeftComponent(scrollTemplates);

            //======== scrollPane1 ========
            {

                //---- txtContent ----
                txtContent.setContentType("text/html");
                scrollPane1.setViewportView(txtContent);
            }
            splitPane1.setRightComponent(scrollPane1);
        }
        contentPane.add(splitPane1, CC.xywh(3, 5, 3, 1, CC.FILL, CC.FILL));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.LINE_AXIS));

            //---- btnCancel ----
            btnCancel.setText(null);
            btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/cancel.png")));
            btnCancel.addActionListener(e -> btnCancelActionPerformed(e));
            panel1.add(btnCancel);

            //---- btnApply ----
            btnApply.setText(null);
            btnApply.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/apply.png")));
            btnApply.addActionListener(e -> btnApplyActionPerformed(e));
            panel1.add(btnApply);
        }
        contentPane.add(panel1, CC.xy(5, 9, CC.RIGHT, CC.DEFAULT));
        setSize(1030, 660);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel2;
    private JTextField txtSearch;
    private JButton btnSearch;
    private JSplitPane splitPane1;
    private JScrollPane scrollTemplates;
    private JList lstTemplates;
    private JScrollPane scrollPane1;
    private JTextPane txtContent;
    private JPanel panel1;
    private JButton btnCancel;
    private JButton btnApply;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
