/*
 * Created by JFormDesigner on Mon Jul 09 15:22:09 CEST 2012
 */

package op.residents.bwassistant;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.system.Users;
import entity.system.UsersTools;
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
        Query query = em.createQuery("SELECT o FROM Users o WHERE o.status = :status ORDER BY o.nachname, o.vorname");
        query.setParameter("status", UsersTools.STATUS_ACTIVE);
        java.util.List<Users> listUsers = query.getResultList();
        em.close();
        listUsers.add(0, null);
        cmbBV.setModel(SYSTools.list2cmb(listUsers));
        cmbBV.setRenderer(UsersTools.getRenderer());
    }

    private void cmbBVItemStateChanged(ItemEvent e) {
        // TODO: hier gabs eine Exception. Siehe Mail vom 22.03. 10:40 Uhr
        /*
        execute	233	op.residents.bwassistant.AddBWWizard$PNPage$2	AddBWWizard.java
        cmbBVItemStateChanged	47	op.residents.bwassistant.PnlBV	PnlBV.java
        access$000	25	op.residents.bwassistant.PnlBV	PnlBV.java
        itemStateChanged	64	op.residents.bwassistant.PnlBV$1	PnlBV.java
         */
        validate.execute(cmbBV.getSelectedItem());
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        cmbBV = new JComboBox();

        //======== this ========
        setLayout(new FormLayout(
            "default:grow",
            "default"));

        //---- cmbBV ----
        cmbBV.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbBV.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbBVItemStateChanged(e);
            }
        });
        add(cmbBV, CC.xy(1, 1));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JComboBox cmbBV;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
