/*
 * Created by JFormDesigner on Fri Aug 03 14:53:03 CEST 2012
 */

package de.offene_pflege.op.care.nursingprocess;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.offene_pflege.backend.services.ResidentTools;
import de.offene_pflege.backend.entity.nursingprocess.NursingProcess;
import de.offene_pflege.backend.services.NursingProcessService;
import de.offene_pflege.backend.entity.system.SYSPropsTools;
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.op.tools.MyJDialog;
import de.offene_pflege.op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.JXSearchField;

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
        txtSearch.setPrompt(SYSTools.xx("nursingrecords.nursingprocess.pnltemplate.searchtopic"));
        tbInactive = GUITools.getNiceToggleButton(SYSTools.xx("nursingrecords.nursingprocess.pnltemplate.inactive"));
        SYSPropsTools.restoreState("nursingrecords.nursingprocess.pnltemplate.tbInactive", tbInactive);
        tbInactive.addItemListener(e -> {
            SYSPropsTools.storeState("nursingrecords.nursingprocess.pnltemplate:tbInactive", tbInactive);
            refreshDisplay();
        });
        lstTemplates.setCellRenderer(getListCellRenderer());
        lstTemplates.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && lstTemplates.getSelectedValue() != null) {
                txtContent.setText(SYSTools.toHTML(NursingProcessService.getAsHTML((NursingProcess) lstTemplates.getSelectedValue(), true, false, false, false)));
            }
        });

        add(tbInactive, CC.xy(3, 7, CC.LEFT, CC.DEFAULT));
        refreshDisplay();
    }

    private void refreshDisplay() {
        java.util.List<NursingProcess> list = NursingProcessService.getTemplates(txtSearch.getText(), tbInactive.isSelected());
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

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        txtSearch = new JXSearchField();
        scrollTemplates = new JScrollPane();
        lstTemplates = new JList();
        scrollPane1 = new JScrollPane();
        txtContent = new JTextPane();
        panel1 = new JPanel();
        btnCancel = new JButton();
        btnApply = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "default, $lcgap, 150dlu, $rgap, [250dlu,min]:grow, $lcgap, default",
            "2*(default, $lgap), [220dlu,min]:grow, 3*($lgap, default)"));

        //---- txtSearch ----
        txtSearch.setFont(new Font("Arial", Font.PLAIN, 14));
        txtSearch.setPromptFontStyle(null);
        txtSearch.setInstantSearchDelay(1000);
        txtSearch.addActionListener(e -> txtSearchActionPerformed(e));
        contentPane.add(txtSearch, CC.xywh(3, 3, 3, 1));

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
        contentPane.add(scrollTemplates, CC.xy(3, 5, CC.FILL, CC.FILL));

        //======== scrollPane1 ========
        {

            //---- txtContent ----
            txtContent.setContentType("text/html");
            scrollPane1.setViewportView(txtContent);
        }
        contentPane.add(scrollPane1, CC.xy(5, 5, CC.FILL, CC.FILL));

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
        setSize(830, 530);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JXSearchField txtSearch;
    private JScrollPane scrollTemplates;
    private JList lstTemplates;
    private JScrollPane scrollPane1;
    private JTextPane txtContent;
    private JPanel panel1;
    private JButton btnCancel;
    private JButton btnApply;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
