/*
 * Created by JFormDesigner on Fri Aug 03 14:53:03 CEST 2012
 */

package op.care.nursingprocess;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
import entity.info.ResidentTools;
import entity.nursingprocess.NursingProcess;
import entity.nursingprocess.NursingProcessTools;
import entity.system.SYSPropsTools;
import op.OPDE;
import op.tools.GUITools;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.JXSearchField;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Torsten Löhr
 */
public class PnlTemplate extends JPanel {
    public static final String internalClassID = "nursingrecords.nursingprocess.pnltemplate";
    private JToggleButton tbInactive;
    private Closure actionBlock;

    public PnlTemplate(Closure actionBlock) {
        this.actionBlock = actionBlock;
        initComponents();
        setPreferredSize(new Dimension(430, 480));
        initPanel();
    }

    private void initPanel() {
        txtSearch.setPrompt(OPDE.lang.getString(internalClassID + ".searchtopic"));
        tbInactive = GUITools.getNiceToggleButton(OPDE.lang.getString(internalClassID + ".inactive"));
        SYSPropsTools.restoreState(internalClassID + ".tbInactive", tbInactive);
        tbInactive.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                SYSPropsTools.storeState(internalClassID + ":tbInactive", tbInactive);
                refreshDisplay();
            }
        });
        lstTemplates.setCellRenderer(getListCellRenderer());
        lstTemplates.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && lstTemplates.getSelectedValue() != null) {
                    JidePopup popup = new JidePopup();
                    popup.setMovable(false);
                    popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
                    JTextPane txtHTML = new JTextPane();
                    txtHTML.setEditable(false);
                    txtHTML.setContentType("text/html");
                    txtHTML.setText(SYSTools.toHTML(NursingProcessTools.getAsHTML((NursingProcess) lstTemplates.getSelectedValue(), true, false, false)));
                    JPanel content = new JPanel();
                    content.setLayout(new BoxLayout(content, BoxLayout.LINE_AXIS));
                    content.add(new JScrollPane(txtHTML));
                    content.setPreferredSize(scrollTemplates.getParent().getPreferredSize());
                    popup.getContentPane().add(content);
                    popup.setOwner(scrollTemplates.getParent());
                    popup.removeExcludedComponent(scrollTemplates);
                    popup.setTransient(true);
                    GUITools.showPopup(popup, SwingConstants.EAST);
                }
            }
        });

        add(tbInactive, CC.xy(3, 7, CC.LEFT, CC.DEFAULT));
        refreshDisplay();
    }

    private void refreshDisplay() {
        java.util.List<NursingProcess> list = NursingProcessTools.getTemplates(txtSearch.getText(), tbInactive.isSelected());
        lstTemplates.setModel(SYSTools.list2dlm(list));
    }

    private void txtSearchActionPerformed(ActionEvent e) {
        refreshDisplay();
    }

    private void lstTemplatesMouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
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

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        txtSearch = new JXSearchField();
        scrollTemplates = new JScrollPane();
        lstTemplates = new JList();

        //======== this ========
        setLayout(new FormLayout(
                "default, $lcgap, default:grow, $lcgap, default",
                "2*(default, $lgap), default:grow, 3*($lgap, default)"));

        //---- txtSearch ----
        txtSearch.setFont(new Font("Arial", Font.PLAIN, 14));
        txtSearch.setPromptFontStyle(null);
        txtSearch.setInstantSearchDelay(1000);
        txtSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSearchActionPerformed(e);
            }
        });
        add(txtSearch, CC.xy(3, 3));

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
        add(scrollTemplates, CC.xy(3, 5, CC.FILL, CC.FILL));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JXSearchField txtSearch;
    private JScrollPane scrollTemplates;
    private JList lstTemplates;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
