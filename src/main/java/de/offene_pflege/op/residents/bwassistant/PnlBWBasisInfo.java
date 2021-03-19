/*
 * Created by JFormDesigner on Mon Jul 09 10:39:27 CEST 2012
 */

package de.offene_pflege.op.residents.bwassistant;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.entity.info.ResidentTools;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.SYSCalendar;
import de.offene_pflege.op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Years;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.text.DateFormat;
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
        lblName.setText(SYSTools.xx("misc.msg.surname"));
        lblVorname.setText(SYSTools.xx("misc.msg.firstname"));
        lblAge.setText(null);
        lblGebdatum.setText(SYSTools.xx("misc.msg.dob"));
        lblGeschlecht.setText(SYSTools.xx("misc.msg.gender"));
        cmbGender.setModel(new DefaultComboBoxModel(new String[]{SYSTools.xx("misc.msg.male"), SYSTools.xx("misc.msg.female")}));
        txtDOB.setText(DateFormat.getDateInstance().format(new Date()));

        gebdatum = null;
    }

    private void check() {
        boolean complete = !vorname.isEmpty() && !nachname.isEmpty() && gebdatum != null;
        int geschlecht = cmbGender.getSelectedIndex() + 1;
        Resident resident = null;
        // Check if this resident has already been entered before.
        if (complete) {
            EntityManager em = OPDE.createEM();
            Query query = em.createQuery("SELECT b FROM Resident b WHERE b.name = :nachname AND b.firstname = :vorname AND b.dob = :gebdatum AND b.gender = :geschlecht ");
            query.setParameter("nachname", nachname);
            query.setParameter("vorname", vorname);
            query.setParameter("gebdatum", gebdatum);
            query.setParameter("geschlecht", geschlecht);
            if (query.getResultList().size() > 0) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("opde.admin.bw.wizard.page2.alreadyexists"), DisplayMessage.WARNING));
            } else {
                resident = ResidentTools.createResident(nachname, vorname, geschlecht, gebdatum, OPDE.getLogin().getUser(), OPDE.isCalcMediUPR1());
                OPDE.getDisplayManager().clearSubMessages();
            }
            em.close();
        }
        validate.execute(resident);
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

    private void txtDOBFocusLost(FocusEvent evt) {
        //TODO: Das sollte nicht "HARDGECODED" werden
        int maxage = 120;
        int minage = 15;

        SYSCalendar.handleDateFocusLost(evt, new LocalDate().minusYears(maxage), new LocalDate().minusYears(minage));

        gebdatum = SYSCalendar.parseDate(txtDOB.getText());

        if (SYSCalendar.isBirthdaySane(gebdatum)) {
            DateMidnight birthdate = new DateTime(gebdatum).toDateMidnight();
            DateTime now = new DateTime();
            Years age = Years.yearsBetween(birthdate, now);
            lblAge.setText(age.getYears() + " " + SYSTools.xx("misc.msg.Years"));
//            gebdatum = birthdate.toDate();
        } else {
            gebdatum = null;
        }
        check();
    }

    private void txtNachnameActionPerformed(ActionEvent e) {
        txtVorname.requestFocus();
    }

    private void txtVornameActionPerformed(ActionEvent e) {
        txtDOB.requestFocus();
    }

    private void txtDOBActionPerformed(ActionEvent e) {
        txtNachname.requestFocus();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblName = new JLabel();
        txtNachname = new JTextField();
        lblVorname = new JLabel();
        txtVorname = new JTextField();
        lblGebdatum = new JLabel();
        txtDOB = new JTextField();
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
        txtNachname.addActionListener(e -> txtNachnameActionPerformed(e));
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
        txtVorname.addActionListener(e -> txtVornameActionPerformed(e));
        add(txtVorname, CC.xywh(5, 5, 3, 1));

        //---- lblGebdatum ----
        lblGebdatum.setText("text");
        lblGebdatum.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblGebdatum, CC.xy(3, 7));

        //---- txtDOB ----
        txtDOB.setFont(new Font("Arial", Font.PLAIN, 14));
        txtDOB.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                txtDOBFocusLost(e);
            }
        });
        txtDOB.addActionListener(e -> txtDOBActionPerformed(e));
        add(txtDOB, CC.xy(5, 7));

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
        cmbGender.addItemListener(e -> cmbGenderItemStateChanged(e));
        add(cmbGender, CC.xywh(5, 9, 3, 1));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblName;
    private JTextField txtNachname;
    private JLabel lblVorname;
    private JTextField txtVorname;
    private JLabel lblGebdatum;
    private JTextField txtDOB;
    private JLabel lblAge;
    private JLabel lblGeschlecht;
    private JComboBox cmbGender;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
