/*
 * Created by JFormDesigner on Mon Jul 09 15:51:58 CEST 2012
 */

package op.residents.bwassistant;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
import entity.prescription.Doc;
import entity.prescription.DocTools;
import op.residents.PnlEditGP;
import op.tools.GUITools;
import op.tools.SYSConst;
import org.apache.commons.collections.Closure;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

/**
 * @author Torsten Löhr
 */
public class PnlGP extends JPanel {
    public static final String internalClassID = "opde.admin.bw.wizard.page4";
    private Closure validate;

    public PnlGP(Closure validate) {
        this.validate = validate;
        initComponents();
        initPanel();
    }

    private void initPanel() {
        ArrayList<Doc> listGPs = DocTools.getAllActive();
        listGPs.add(0, null);

        cmbArzt.setModel(new DefaultComboBoxModel(listGPs.toArray()));
        cmbArzt.setRenderer(DocTools.getRenderer());
    }

    private JidePopup createPopup(final PnlEditGP pnlGP) {
        final JidePopup popup = new JidePopup();
        popup.setMovable(false);
        JPanel pnl = new JPanel(new BorderLayout(10, 10));

        pnl.add(pnlGP, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));

        JButton save = new JButton(SYSConst.icon22apply);
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popup.hidePopup();
                if (pnlGP.getDoc() != null) {
                    cmbArzt.setModel(new DefaultComboBoxModel(new Doc[]{pnlGP.getDoc()}));
                    validate.execute(cmbArzt.getSelectedItem());
                }
            }
        });
        btnPanel.add(Box.createHorizontalGlue());
        btnPanel.add(save);
        pnl.add(btnPanel, BorderLayout.SOUTH);

        popup.setContentPane(pnl);
        popup.setPreferredSize(pnl.getPreferredSize());
        pnl.revalidate();
        popup.removeExcludedComponent(pnl);
        popup.setDefaultFocusComponent(pnl);
        return popup;
    }

    private void btnAddActionPerformed(ActionEvent e) {
        final JidePopup popupGP = createPopup(new PnlEditGP(new Doc()));
        popupGP.setOwner(btnAdd);
        popupGP.setMovable(false);
        GUITools.showPopup(popupGP, SwingConstants.SOUTH_WEST);
    }

    private void cmbArztItemStateChanged(ItemEvent e) {
        validate.execute(cmbArzt.getSelectedItem());
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        cmbArzt = new JComboBox();
        btnAdd = new JButton();

        //======== this ========
        setLayout(new FormLayout(
            "default:grow, $lcgap, default",
            "default"));

        //---- cmbArzt ----
        cmbArzt.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbArztItemStateChanged(e);
            }
        });
        add(cmbArzt, CC.xy(1, 1));

        //---- btnAdd ----
        btnAdd.setText(null);
        btnAdd.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
        btnAdd.setContentAreaFilled(false);
        btnAdd.setBorderPainted(false);
        btnAdd.setBorder(null);
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnAddActionPerformed(e);
            }
        });
        add(btnAdd, CC.xy(3, 1));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JComboBox cmbArzt;
    private JButton btnAdd;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
