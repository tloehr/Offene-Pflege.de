/*
 * Created by JFormDesigner on Mon Jul 09 15:51:58 CEST 2012
 */

package op.residents.bwassistant;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
import entity.info.LCustodian;
import entity.info.LCustodianTools;
import op.OPDE;
import op.residents.PnlEditLC;
import op.tools.GUITools;
import org.apache.commons.collections.Closure;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

/**
 * @author Torsten Löhr
 */
public class PnlLC extends JPanel {
    public static final String internalClassID = "opde.admin.bw.wizard.page5";
    //    private double split1Pos;
    private Closure validate;
//    private PnlEditLC pnlEditLC;


    public PnlLC(Closure validate) {
        this.validate = validate;
        initComponents();
        initPanel();
    }

    private void initPanel() {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM LCustodian b WHERE b.status >= 0 ORDER BY b.name, b.vorname");
        List<LCustodian> listLCustodian = query.getResultList();
        em.close();
        listLCustodian.add(0, null);

        cmbLC.setModel(new DefaultComboBoxModel(listLCustodian.toArray()));
        cmbLC.setRenderer(LCustodianTools.getRenderer());

    }


    private void btnAddActionPerformed(ActionEvent e) {

        final PnlEditLC pnlLC = new PnlEditLC(new LCustodian());
        final JidePopup popup = GUITools.createPanelPopup(pnlLC, new Closure() {
            @Override
            public void execute(Object o) {
                if (o != null) {
                    cmbLC.setModel(new DefaultComboBoxModel(new LCustodian[]{(LCustodian) o}));
                    validate.execute(cmbLC.getSelectedItem());
                }
            }
        }, btnAdd);
        GUITools.showPopup(popup, SwingConstants.EAST);
    }

    private void cmbBetreuerItemStateChanged(ItemEvent e) {
        validate.execute(cmbLC.getSelectedItem());
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        cmbLC = new JComboBox();
        btnAdd = new JButton();

        //======== this ========
        setLayout(new FormLayout(
                "default:grow, $lcgap, default",
                "default"));

        //---- cmbLC ----
        cmbLC.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbBetreuerItemStateChanged(e);
            }
        });
        add(cmbLC, CC.xy(1, 1));

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
    private JComboBox cmbLC;
    private JButton btnAdd;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
