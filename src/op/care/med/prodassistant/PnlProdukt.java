/*
 * Created by JFormDesigner on Tue May 29 16:41:50 CEST 2012
 */

package op.care.med.prodassistant;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.verordnungen.MedProdukte;
import entity.verordnungen.MedProdukteTools;
import op.OPDE;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.JXSearchField;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Torsten Löhr
 */
public class PnlProdukt extends JPanel {
    private java.util.List listProd;
    private MedProdukte produkt;
    private Closure validate;

    public PnlProdukt(Closure validate) {
        OPDE.debug("CONSTRUCTOR PNLPRODUKT");
        produkt = null;
        this.validate = validate;
        initComponents();
        initPanel();
    }

    private void initPanel() {
        lblProdMsg.setVisible(false);
        jsp1.setVisible(false);
        lstProd.setVisible(false);
    }

    private void txtProdActionPerformed(ActionEvent e) {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("MedProdukte.findByBezeichnungLike");
        query.setParameter("bezeichnung", "%" + txtProd.getText().trim() + "%");
        listProd = query.getResultList();
        em.close();

        lblProdMsg.setVisible(!listProd.isEmpty());
        jsp1.setVisible(!listProd.isEmpty());
        lstProd.setVisible(!listProd.isEmpty());

        if (!listProd.isEmpty()) {
            listProd.add(0, "<html><b>Nein, keins von diesen</b></html>");
            DefaultListModel lmProd;
            lmProd = SYSTools.list2dlm(listProd);
            lstProd.setModel(lmProd);
            lstProd.setCellRenderer(MedProdukteTools.getMedProdukteRenderer());
        } else {
            produkt = txtProd.getText().trim().isEmpty() ? null : new MedProdukte(txtProd.getText().trim());
            validate.execute(produkt);
        }
    }

    private void lstProdValueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            if (lstProd.getSelectedIndex() == 0) {
                produkt = txtProd.getText().trim().isEmpty() ? null : new MedProdukte(txtProd.getText().trim());
            } else {
                produkt = (MedProdukte) lstProd.getSelectedValue();
            }
            validate.execute(produkt);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        txtProd = new JXSearchField();
        lblProdMsg = new JLabel();
        jsp1 = new JScrollPane();
        lstProd = new JList();

        //======== this ========
        setPreferredSize(new Dimension(610, 198));
        setLayout(new FormLayout(
            "default, $lcgap, default:grow, $lcgap, default",
            "2*(default), $ugap, default, $lgap, default:grow, $lgap, default"));

        //---- txtProd ----
        txtProd.setFont(new Font("Arial", Font.PLAIN, 14));
        txtProd.setInstantSearchDelay(500);
        txtProd.setPrompt("Produktname");
        txtProd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtProdActionPerformed(e);
            }
        });
        add(txtProd, CC.xy(3, 2));

        //---- lblProdMsg ----
        lblProdMsg.setFont(new Font("Arial", Font.PLAIN, 14));
        lblProdMsg.setText("Es gibt bereits Medis, die so \u00e4hnlich heissen. Ist es eins von diesen ?");
        lblProdMsg.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblProdMsg, CC.xy(3, 4));

        //======== jsp1 ========
        {

            //---- lstProd ----
            lstProd.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            lstProd.setFont(new Font("Arial", Font.PLAIN, 14));
            lstProd.setVisible(false);
            lstProd.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    lstProdValueChanged(e);
                }
            });
            jsp1.setViewportView(lstProd);
        }
        add(jsp1, CC.xy(3, 6, CC.DEFAULT, CC.FILL));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JXSearchField txtProd;
    private JLabel lblProdMsg;
    private JScrollPane jsp1;
    private JList lstProd;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
