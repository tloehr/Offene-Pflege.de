/*
 * Created by JFormDesigner on Fri Jun 01 11:55:36 CEST 2012
 */

package op.care.med.prodassistant;

import java.awt.*;
import java.awt.event.*;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import entity.verordnungen.MedHersteller;
import entity.verordnungen.MedHerstellerTools;
import entity.verordnungen.MedProdukte;
import op.OPDE;
import org.apache.commons.collections.Closure;

/**
 * @author Torsten Löhr
 */
public class PnlHersteller extends JPanel {
    private MedProdukte produkt;
    private Closure validate;

    public PnlHersteller(Closure validate) {
        this.validate = validate;
//        this.produkt = produkt;
        initComponents();
        initPanel();
    }

    private void initPanel(){
        EntityManager em = OPDE.createEM();
        Query query2 = em.createNamedQuery("MedHersteller.findAll");
        lstHersteller.setModel(new DefaultComboBoxModel(query2.getResultList().toArray(new MedHersteller[]{})));
        lstHersteller.setCellRenderer(MedHerstellerTools.getHerstellerRenderer(0));
        em.close();
    }

    private void btnAddActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void lstHerstellerValueChanged(ListSelectionEvent e) {
        validate.execute((MedHersteller) lstHersteller.getSelectedValue());
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        scrollPane1 = new JScrollPane();
        lstHersteller = new JList();
        btnAdd = new JButton();

        //======== this ========
        setLayout(new FormLayout(
            "default, $lcgap, default:grow, $lcgap, default",
            "default, $lgap, default:grow, 2*($lgap, default)"));

        //======== scrollPane1 ========
        {

            //---- lstHersteller ----
            lstHersteller.setFont(new Font("Arial", Font.PLAIN, 14));
            lstHersteller.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    lstHerstellerValueChanged(e);
                }
            });
            scrollPane1.setViewportView(lstHersteller);
        }
        add(scrollPane1, CC.xy(3, 3, CC.DEFAULT, CC.FILL));

        //---- btnAdd ----
        btnAdd.setText(null);
        btnAdd.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
        btnAdd.setContentAreaFilled(false);
        btnAdd.setBorder(null);
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnAddActionPerformed(e);
            }
        });
        add(btnAdd, CC.xy(3, 5, CC.LEFT, CC.DEFAULT));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane scrollPane1;
    private JList lstHersteller;
    private JButton btnAdd;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
