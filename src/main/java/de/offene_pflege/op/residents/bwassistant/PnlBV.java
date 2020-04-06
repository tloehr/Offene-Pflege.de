/*
 * Created by JFormDesigner on Mon Jul 09 15:22:09 CEST 2012
 */

package de.offene_pflege.op.residents.bwassistant;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.offene_pflege.backend.entity.system.OPUsers;
import de.offene_pflege.backend.services.OPUsersService;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

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
        Query query = em.createQuery("SELECT o FROM OPUsers o WHERE o.userstatus = :status ORDER BY o.nachname, o.vorname");
        query.setParameter("status", OPUsersService.STATUS_ACTIVE);
        java.util.List<OPUsers> listUsers = query.getResultList();
        em.close();
        listUsers.add(0, null);
        cmbBV.setModel(SYSTools.list2cmb(listUsers));
        cmbBV.setRenderer(OPUsersService.getRenderer());
    }

    private void cmbBVItemStateChanged(ItemEvent e) {
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
        cmbBV.addItemListener(e -> cmbBVItemStateChanged(e));
        add(cmbBV, CC.xy(1, 1));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JComboBox cmbBV;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
