/*
 * Created by JFormDesigner on Wed May 30 16:04:17 CEST 2012
 */

package op.care.med.prodassistant;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.verordnungen.*;
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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author Torsten Löhr
 */
public class PnlZusatz extends JPanel {
    private MedProdukte produkt;
    private Darreichung darreichung;
    private MedFormen form;
    private Closure validate;

    public PnlZusatz(Closure validate, MedProdukte produkt) {
        this.validate = validate;
        this.produkt = produkt;
        initComponents();
        initPanel();
    }

    private void initPanel() {
        if (!produkt.getDarreichungen().isEmpty()) {
            ArrayList model = new ArrayList(produkt.getDarreichungen());
            model.add(0, "<html><b>Nein, keins von diesen</b></html>");
            DefaultListModel lmDaf = SYSTools.list2dlm(model);
            lstDaf.setModel(lmDaf);
            lstDaf.setCellRenderer(DarreichungTools.getDarreichungRenderer(DarreichungTools.LONG));
        }
        lblMsg.setVisible(!produkt.getDarreichungen().isEmpty());
        lstDaf.setVisible(!produkt.getDarreichungen().isEmpty());

        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("MedFormen.findAll");

        java.util.List listFormen = query.getResultList();
        Collections.sort(listFormen, new Comparator<Object>() {
            @Override
            public int compare(Object us, Object them) {
                return MedFormenTools.toPrettyString((MedFormen) us).compareTo(MedFormenTools.toPrettyString((MedFormen) them));
            }
        });

        cmbFormen.setModel(SYSTools.list2cmb(listFormen));
        cmbFormen.setRenderer(MedFormenTools.getMedFormenRenderer(0));
        em.close();

        form = (MedFormen) cmbFormen.getSelectedItem();
        lblAPV.setVisible(!form.anwUndPackEinheitenGleich());
        lblPV.setVisible(!form.anwUndPackEinheitenGleich());
        txtA.setVisible(!form.anwUndPackEinheitenGleich());
    }

    private void txtZusatzActionPerformed(ActionEvent e) {
        if (txtZusatz.getText().isEmpty()) {
            return;
        }
        lstDaf.setSelectedIndex(0);
//        darreichung = new Darreichung(produkt, txtZusatz.getText().trim(), null);
//        validate.execute(darreichung);
    }

    private void lstDafValueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            if (lstDaf.getSelectedIndex() == 0) {
                if (txtZusatz.getText().trim().isEmpty()) {
                    darreichung = null;
                } else {
                    darreichung = new Darreichung(produkt, txtZusatz.getText().trim(), form);
                }
            } else {
                txtZusatz.setText(null);
                darreichung = (Darreichung) lstDaf.getSelectedValue();
            }
            validate.execute(darreichung);
        }
    }

    private void cmbFormenItemStateChanged(ItemEvent e) {
        form = (MedFormen) cmbFormen.getSelectedItem();

        lblAPV.setVisible(!form.anwUndPackEinheitenGleich());
        lblPV.setVisible(!form.anwUndPackEinheitenGleich());
        txtA.setVisible(!form.anwUndPackEinheitenGleich());

        if (!form.anwUndPackEinheitenGleich()){
            txtA.setText("1");
            lblPV.setText(" "+form.getAnwEinheit());
        }

        if (txtZusatz.getText().trim().isEmpty()) {
            darreichung = null;
        } else {
            darreichung = new Darreichung(produkt, txtZusatz.getText().trim(), form);
        }
        validate.execute(darreichung);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        txtZusatz = new JXSearchField();
        cmbFormen = new JComboBox();
        lblAPV = new JLabel();
        panel1 = new JPanel();
        txtA = new JTextField();
        lblPV = new JLabel();
        lblMsg = new JLabel();
        scrollPane1 = new JScrollPane();
        lstDaf = new JList();

        //======== this ========
        setLayout(new FormLayout(
                "default, $lcgap, default:grow, $lcgap, default",
                "2*(default, $lgap), default, $rgap, default, $lgap, default, 14dlu, default, $lgap, default:grow, $lgap, default"));

        //---- txtZusatz ----
        txtZusatz.setFont(new Font("Arial", Font.PLAIN, 14));
        txtZusatz.setInstantSearchDelay(500);
        txtZusatz.setPrompt("Zusatzbezeichnung");
        txtZusatz.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtZusatzActionPerformed(e);
            }
        });
        add(txtZusatz, CC.xy(3, 3));

        //---- cmbFormen ----
        cmbFormen.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbFormen.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbFormenItemStateChanged(e);
            }
        });
        add(cmbFormen, CC.xy(3, 5));

        //---- lblAPV ----
        lblAPV.setText("In welchem Verh\u00e4ltnis stehen Anwendung und Verpackung ?");
        lblAPV.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblAPV, CC.xy(3, 7));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.LINE_AXIS));
            panel1.add(txtA);

            //---- lblPV ----
            lblPV.setText("text");
            lblPV.setFont(new Font("Arial", Font.PLAIN, 14));
            panel1.add(lblPV);
        }
        add(panel1, CC.xy(3, 9));

        //---- lblMsg ----
        lblMsg.setFont(new Font("Arial", Font.PLAIN, 14));
        lblMsg.setText("Es gibt bereits Darreichungsformen. Ist es eins von diesen ?");
        add(lblMsg, CC.xy(3, 11));

        //======== scrollPane1 ========
        {

            //---- lstDaf ----
            lstDaf.setFont(new Font("Arial", Font.PLAIN, 14));
            lstDaf.setVisible(false);
            lstDaf.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    lstDafValueChanged(e);
                }
            });
            scrollPane1.setViewportView(lstDaf);
        }
        add(scrollPane1, CC.xy(3, 13, CC.DEFAULT, CC.FILL));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JXSearchField txtZusatz;
    private JComboBox cmbFormen;
    private JLabel lblAPV;
    private JPanel panel1;
    private JTextField txtA;
    private JLabel lblPV;
    private JLabel lblMsg;
    private JScrollPane scrollPane1;
    private JList lstDaf;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
