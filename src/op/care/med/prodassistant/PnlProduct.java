/*
 * Created by JFormDesigner on Tue May 29 16:41:50 CEST 2012
 */

package op.care.med.prodassistant;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.prescription.MedProducts;
import entity.prescription.MedProductsTools;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Torsten Löhr
 */
public class PnlProduct extends JPanel {
    public static final String internalClassID = "opde.medication.medproduct.wizard.product";
    private java.util.List listProd;
    private MedProducts produkt;
    private Closure validate;
    private String template;

    public PnlProduct(Closure validate, String template) {
        produkt = null;
        this.validate = validate;
        this.template = template;

        initComponents();
        initPanel();
    }

    private void initPanel() {
        lblProdMsg.setVisible(false);
        lblProdMsg.setText(SYSTools.xx(internalClassID + ".existingProducts"));
        jsp1.setVisible(false);
        lstProd.setVisible(false);
        txtProd.setText(template);
    }

    private void txtProdActionPerformed(ActionEvent e) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT m FROM MedProducts m WHERE m.text LIKE :bezeichnung ORDER BY m.text");
        query.setParameter("bezeichnung", "%" + txtProd.getText().trim() + "%");
        listProd = query.getResultList();
        em.close();

        if (!listProd.isEmpty()) {
            lblProdMsg.setVisible(true);
            jsp1.setVisible(true);
            lstProd.setVisible(true);
            listProd.add(0, "<html><b>" + SYSTools.xx("misc.msg.noneOfThem") + "</b></html>");
            DefaultListModel lmProd;
            lmProd = SYSTools.list2dlm(listProd);
            lstProd.setModel(lmProd);
            lstProd.setCellRenderer(MedProductsTools.getMedProdukteRenderer());
        } else {
            produkt = txtProd.getText().trim().isEmpty() ? null : new MedProducts(txtProd.getText().trim());
            lblProdMsg.setVisible(false);
            jsp1.setVisible(false);
            lstProd.setVisible(false);
            validate.execute(produkt);
        }
    }

    private void lstProdValueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            if (lstProd.getSelectedIndex() == 0) {
                produkt = txtProd.getText().trim().isEmpty() ? null : new MedProducts(txtProd.getText().trim());
            } else {
                produkt = (MedProducts) lstProd.getSelectedValue();
            }
            validate.execute(produkt);
        }
    }

    private void lstProdMouseClicked(MouseEvent e) {
        // TODO: double click handed over to wizard.
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        txtProd = new JXSearchField();
        label1 = new JLabel();
        lblProdMsg = new JLabel();
        jsp1 = new JScrollPane();
        lstProd = new JList();

        //======== this ========
        setPreferredSize(new Dimension(610, 198));
        setLayout(new FormLayout(
            "2*(default, $lcgap), default:grow, $lcgap, default",
            "2*(default), $ugap, default, $lgap, default:grow, $lgap, default"));

        //---- txtProd ----
        txtProd.setFont(new Font("Arial", Font.PLAIN, 14));
        txtProd.setInstantSearchDelay(500);
        txtProd.setPrompt("Produktname");
        txtProd.addActionListener(e -> txtProdActionPerformed(e));
        add(txtProd, CC.xywh(3, 2, 3, 1));

        //---- label1 ----
        label1.setText(null);
        label1.setIcon(new ImageIcon(getClass().getResource("/artwork/other/medicine1.png")));
        add(label1, CC.xywh(3, 4, 1, 3, CC.CENTER, CC.DEFAULT));

        //---- lblProdMsg ----
        lblProdMsg.setFont(new Font("Arial", Font.PLAIN, 14));
        lblProdMsg.setText("Es gibt bereits Medis, die so \u00e4hnlich heissen. Ist es eins von diesen ?");
        lblProdMsg.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblProdMsg, CC.xy(5, 4));

        //======== jsp1 ========
        {

            //---- lstProd ----
            lstProd.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            lstProd.setFont(new Font("Arial", Font.PLAIN, 14));
            lstProd.setVisible(false);
            lstProd.addListSelectionListener(e -> lstProdValueChanged(e));
            lstProd.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    lstProdMouseClicked(e);
                }
            });
            jsp1.setViewportView(lstProd);
        }
        add(jsp1, CC.xy(5, 6, CC.DEFAULT, CC.FILL));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JXSearchField txtProd;
    private JLabel label1;
    private JLabel lblProdMsg;
    private JScrollPane jsp1;
    private JList lstProd;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
