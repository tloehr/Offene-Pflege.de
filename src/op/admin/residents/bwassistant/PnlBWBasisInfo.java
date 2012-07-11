/*
 * Created by JFormDesigner on Mon Jul 09 10:39:27 CEST 2012
 */

package op.admin.residents.bwassistant;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDateChooser;
import entity.Bewohner;
import op.OPDE;
import op.threads.DisplayMessage;
import org.apache.commons.collections.Closure;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Years;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

/**
 * @author Torsten Löhr
 */
public class PnlBWBasisInfo extends JPanel {
    public static final String internalClassID = "opde.admin.bw.wizard.page2";
    Closure validate;
    String nachname = "", vorname = "";
    Date gebdatum;


    public PnlBWBasisInfo(Closure validate) {
        this.validate = validate;
        initComponents();
        initPanel();
        txtNachname.requestFocus();
    }

    private void initPanel() {
        lblName.setText(OPDE.lang.getString("misc.msg.surname"));
        lblVorname.setText(OPDE.lang.getString("misc.msg.firstname"));
        lblAge.setText(null);
        lblGebdatum.setText(OPDE.lang.getString("misc.msg.dob"));
        lblGeschlecht.setText(OPDE.lang.getString("misc.msg.gender"));
        cmbGender.setModel(new DefaultComboBoxModel(new String[]{OPDE.lang.getString("misc.msg.male"), OPDE.lang.getString("misc.msg.female")}));
        jdcDOB.setMaxSelectableDate(new Date());
        jdcDOB.setDate(new Date());
        gebdatum = jdcDOB.getDate();
    }

    private void jdcDOBPropertyChange(PropertyChangeEvent e) {
        gebdatum = jdcDOB.getDate();
        if (jdcDOB.getDate() != null) {
            DateMidnight birthdate = new DateTime(jdcDOB.getDate()).toDateMidnight();
            DateTime now = new DateTime();
            Years age = Years.yearsBetween(birthdate, now);
            lblAge.setText(age.getYears() + " " + OPDE.lang.getString("misc.msg.Years"));
            gebdatum = birthdate.toDate();
        }
        check();
    }

    private void check() {
        boolean complete = !vorname.isEmpty() && !nachname.isEmpty() && gebdatum != null;
        int geschlecht = cmbGender.getSelectedIndex() + 1;
        Bewohner bewohner = null;
        // Check if this resident has already been entered before.
        if (complete) {
            EntityManager em = OPDE.createEM();
            Query query = em.createQuery("SELECT b FROM Bewohner b WHERE b.nachname = :nachname AND b.vorname = :vorname AND b.gebDatum = :gebdatum AND b.geschlecht = :geschlecht ");
            query.setParameter("nachname", nachname);
            query.setParameter("vorname", vorname);
            query.setParameter("gebdatum", gebdatum);
            query.setParameter("geschlecht", geschlecht);
            if (query.getResultList().size() > 0) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(OPDE.lang.getString(internalClassID + ".alreadyexists"), DisplayMessage.WARNING));
            } else {
                bewohner = new Bewohner(nachname, vorname, geschlecht, gebdatum);
                OPDE.getDisplayManager().clearSubMessages();
            }
            em.close();
        }
        validate.execute(bewohner);
    }

    private void txtVornameFocusLost(FocusEvent e) {
        vorname = txtVorname.getText().trim();
        check();
    }

    private void txtNachnameFocusLost(FocusEvent e) {
        nachname = txtNachname.getText().trim();
        check();
    }

    private void cmbGenderItemStateChanged(ItemEvent e) {
        check();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblName = new JLabel();
        txtNachname = new JTextField();
        lblVorname = new JLabel();
        txtVorname = new JTextField();
        lblGebdatum = new JLabel();
        jdcDOB = new JDateChooser();
        lblAge = new JLabel();
        lblGeschlecht = new JLabel();
        cmbGender = new JComboBox();

        //======== this ========
        setLayout(new FormLayout(
            "default, $lcgap, pref, 2*($lcgap, default:grow), $lcgap, default",
            "5*(default, $lgap), default"));

        //---- lblName ----
        lblName.setText("text");
        lblName.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblName, CC.xy(3, 3));

        //---- txtNachname ----
        txtNachname.setFont(new Font("Arial", Font.PLAIN, 14));
        txtNachname.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                txtNachnameFocusLost(e);
            }
        });
        add(txtNachname, CC.xywh(5, 3, 3, 1));

        //---- lblVorname ----
        lblVorname.setText("text");
        lblVorname.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblVorname, CC.xy(3, 5));

        //---- txtVorname ----
        txtVorname.setFont(new Font("Arial", Font.PLAIN, 14));
        txtVorname.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                txtVornameFocusLost(e);
            }
        });
        add(txtVorname, CC.xywh(5, 5, 3, 1));

        //---- lblGebdatum ----
        lblGebdatum.setText("text");
        lblGebdatum.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblGebdatum, CC.xy(3, 7));

        //---- jdcDOB ----
        jdcDOB.setFont(new Font("Arial", Font.PLAIN, 14));
        jdcDOB.addPropertyChangeListener("date", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                jdcDOBPropertyChange(e);
            }
        });
        add(jdcDOB, CC.xy(5, 7));

        //---- lblAge ----
        lblAge.setText("text");
        lblAge.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblAge, CC.xy(7, 7));

        //---- lblGeschlecht ----
        lblGeschlecht.setText("text");
        lblGeschlecht.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblGeschlecht, CC.xy(3, 9));

        //---- cmbGender ----
        cmbGender.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbGender.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbGenderItemStateChanged(e);
            }
        });
        add(cmbGender, CC.xywh(5, 9, 3, 1));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblName;
    private JTextField txtNachname;
    private JLabel lblVorname;
    private JTextField txtVorname;
    private JLabel lblGebdatum;
    private JDateChooser jdcDOB;
    private JLabel lblAge;
    private JLabel lblGeschlecht;
    private JComboBox cmbGender;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
