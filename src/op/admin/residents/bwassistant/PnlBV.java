/*
 * Created by JFormDesigner on Mon Jul 09 15:22:09 CEST 2012
 */

package op.admin.residents.bwassistant;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.UsersTools;
import op.OPDE;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Torsten Löhr
 */
public class PnlBV extends JPanel {
    public static final String internalClassID = "opde.admin.bw.wizard.page3";
    private Closure validate;

    public PnlBV(Closure validate) {
        this.validate = validate;
        initComponents();
        initPanel();
    }

    private void initPanel() {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("Users.findByStatusSorted");
        query.setParameter("status", UsersTools.STATUS_ACTIVE);
        cmbBV.setModel(SYSTools.list2cmb(query.getResultList()));
        em.close();
    }

    private void cmbBVItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            validate.execute(cmbBV.getSelectedItem());
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        cmbBV = new JComboBox();

        //======== this ========
        setLayout(new FormLayout(
            "default, $lcgap, default:grow, $lcgap, default",
            "2*(default, $lgap), default"));

        //---- cmbBV ----
        cmbBV.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbBV.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbBVItemStateChanged(e);
            }
        });
        add(cmbBV, CC.xy(3, 3));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JComboBox cmbBV;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
