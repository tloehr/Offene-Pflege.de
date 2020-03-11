/*
 * Created by JFormDesigner on Fri Jun 01 11:55:36 CEST 2012
 */

package de.offene_pflege.op.care.med.prodassistant;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
import de.offene_pflege.entity.prescription.ACME;
import de.offene_pflege.entity.prescription.ACMETools;
import de.offene_pflege.entity.prescription.MedProducts;
import de.offene_pflege.op.OPDE;
import org.apache.commons.collections.Closure;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Torsten Löhr
 */
public class PnlACME extends JPanel {
    private MedProducts produkt;
    private Closure validate;
    private Dialog parent;

    public PnlACME(Closure validate, MedProducts produkt, Dialog parent) {
        this.validate = validate;
        this.produkt = produkt;
        this.parent = parent;
        initComponents();
        initPanel();
    }

    public void setProdukt(MedProducts produkt) {
        this.produkt = produkt;
    }

    private void initPanel() {
        EntityManager em = OPDE.createEM();
        Query query2 = em.createQuery("SELECT m FROM ACME m ORDER BY m.name, m.city");
        lstHersteller.setModel(new DefaultComboBoxModel(query2.getResultList().toArray(new ACME[]{})));
        lstHersteller.setCellRenderer(ACMETools.getRenderer(0));
        em.close();
    }

    private void btnAddActionPerformed(ActionEvent e) {
        final JidePopup popup = new JidePopup();

        DlgACME dlg = new DlgACME(o -> {
            if (o != null) {
                lstHersteller.setModel(new DefaultComboBoxModel(new ACME[]{(ACME) o}));
                lstHersteller.setSelectedIndex(0);
                popup.hidePopup();
            }
        });

        popup.setMovable(false);
        popup.setResizable(false);
        popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
        popup.getContentPane().add(dlg);
        popup.setOwner(btnAdd);
        popup.removeExcludedComponent(btnAdd);
        popup.setTransient(true);
        popup.setDefaultFocusComponent(dlg);

        popup.showPopup(new Insets(-5, 0, -5, 0), btnAdd);
    }

    private void lstHerstellerValueChanged(ListSelectionEvent e) {
        produkt.setACME((ACME) lstHersteller.getSelectedValue());
        validate.execute(lstHersteller.getSelectedValue());
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        label1 = new JLabel();
        scrollPane1 = new JScrollPane();
        lstHersteller = new JList();
        btnAdd = new JButton();

        //======== this ========
        setLayout(new FormLayout(
            "2*(default, $lcgap), default:grow, $lcgap, default",
            "default, $lgap, default:grow, 2*($lgap, default)"));

        //---- label1 ----
        label1.setText(null);
        label1.setIcon(new ImageIcon(getClass().getResource("/artwork/other/medicine-acme.png")));
        add(label1, CC.xy(3, 3));

        //======== scrollPane1 ========
        {

            //---- lstHersteller ----
            lstHersteller.setFont(new Font("Arial", Font.PLAIN, 14));
            lstHersteller.addListSelectionListener(e -> lstHerstellerValueChanged(e));
            scrollPane1.setViewportView(lstHersteller);
        }
        add(scrollPane1, CC.xy(5, 3, CC.DEFAULT, CC.FILL));

        //---- btnAdd ----
        btnAdd.setText(null);
        btnAdd.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
        btnAdd.setContentAreaFilled(false);
        btnAdd.setBorder(null);
        btnAdd.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add-pressed.png")));
        btnAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> btnAddActionPerformed(e));
        add(btnAdd, CC.xy(5, 5, CC.LEFT, CC.DEFAULT));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel label1;
    private JScrollPane scrollPane1;
    private JList lstHersteller;
    private JButton btnAdd;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
