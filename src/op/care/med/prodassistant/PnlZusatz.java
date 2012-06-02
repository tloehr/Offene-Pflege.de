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
import org.jdesktop.swingx.HorizontalLayout;
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
    private boolean ignoreEvent = false;

    public PnlZusatz(Closure validate, MedProdukte produkt) {
        OPDE.debug("CONSTRUCTOR PNLZUSATZ");
        this.validate = validate;
        this.produkt = produkt;
        initComponents();
        initPanel();
    }

    public void setProdukt(MedProdukte produkt) {
        this.produkt = produkt;
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
        jsp1.setVisible(!produkt.getDarreichungen().isEmpty());
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
        cmbFormen.setEnabled(true);
        darreichung = new Darreichung(produkt, txtZusatz.getText().trim(), form);
        validate.execute(darreichung);
        if (lstDaf.isVisible() && lstDaf.getSelectedIndex() != 0){
            lstDaf.setSelectedIndex(0);
        }
    }

    private void lstDafValueChanged(ListSelectionEvent e) {
        if (ignoreEvent){
            return;
        }
        if (!e.getValueIsAdjusting()) {
            if (lstDaf.getSelectedIndex() > 0) {
                ignoreEvent = true;
                darreichung = (Darreichung) lstDaf.getSelectedValue();
                txtZusatz.setText(null);
                cmbFormen.setEnabled(false);
                validate.execute(darreichung);
                ignoreEvent = false;
            }
        }
    }

    private void cmbFormenItemStateChanged(ItemEvent e) {
        form = (MedFormen) cmbFormen.getSelectedItem();

        lblAPV.setVisible(!form.anwUndPackEinheitenGleich());
        lblPV.setVisible(!form.anwUndPackEinheitenGleich());
        txtA.setVisible(!form.anwUndPackEinheitenGleich());

        if (!form.anwUndPackEinheitenGleich()) {
            txtA.setText("1");
            lblPV.setText(" " + form.getAnwText() + " entsprechen 1 " + MedFormenTools.EINHEIT[form.getPackEinheit()]);
        }

        darreichung = new Darreichung(produkt, txtZusatz.getText().trim(), form);

        validate.execute(darreichung);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        txtZusatz = new JXSearchField();
        cmbFormen = new JComboBox();
        panel1 = new JPanel();
        lblAPV = new JLabel();
        txtA = new JTextField();
        lblPV = new JLabel();
        lblMsg = new JLabel();
        jsp1 = new JScrollPane();
        lstDaf = new JList();

        //======== this ========
        setLayout(new FormLayout(
            "default, $lcgap, default:grow, $lcgap, default",
            "2*(default, $lgap), default, $rgap, pref, 14dlu, default, $lgap, default:grow, $lgap, default"));

        //---- txtZusatz ----
        txtZusatz.setFont(new Font("Arial", Font.PLAIN, 14));
        txtZusatz.setInstantSearchDelay(0);
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

        //======== panel1 ========
        {
            panel1.setLayout(new HorizontalLayout(10));

            //---- lblAPV ----
            lblAPV.setText("Verh\u00e4ltnis zwischen Anwendung und Packung:");
            lblAPV.setFont(new Font("Arial", Font.PLAIN, 14));
            panel1.add(lblAPV);

            //---- txtA ----
            txtA.setColumns(10);
            txtA.setFont(new Font("Arial", Font.PLAIN, 14));
            panel1.add(txtA);

            //---- lblPV ----
            lblPV.setText("x entspricht 1 g");
            lblPV.setFont(new Font("Arial", Font.PLAIN, 14));
            panel1.add(lblPV);
        }
        add(panel1, CC.xy(3, 7, CC.RIGHT, CC.DEFAULT));

        //---- lblMsg ----
        lblMsg.setFont(new Font("Arial", Font.PLAIN, 14));
        lblMsg.setText("Es gibt bereits Darreichungsformen. Ist es eins von diesen ?");
        add(lblMsg, CC.xy(3, 9));

        //======== jsp1 ========
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
            jsp1.setViewportView(lstDaf);
        }
        add(jsp1, CC.xy(3, 11, CC.DEFAULT, CC.FILL));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JXSearchField txtZusatz;
    private JComboBox cmbFormen;
    private JPanel panel1;
    private JLabel lblAPV;
    private JTextField txtA;
    private JLabel lblPV;
    private JLabel lblMsg;
    private JScrollPane jsp1;
    private JList lstDaf;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
