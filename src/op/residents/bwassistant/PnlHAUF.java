/*
 * Created by JFormDesigner on Mon Jul 09 10:39:27 CEST 2012
 */

package op.residents.bwassistant;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDateChooser;
import entity.Station;
import op.OPDE;
import op.tools.Pair;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

/**
 * @author Torsten Löhr
 */
public class PnlHAUF extends JPanel {
    public static final String internalClassID = "opde.admin.bw.wizard.page6";
    Closure validate;

    public PnlHAUF(Closure validate) {
        this.validate = validate;
        initComponents();
        initPanel();
    }

    private void initPanel() {
        lblHAUF.setText(OPDE.lang.getString("misc.msg.movein"));
        lblStation.setText(OPDE.lang.getString("misc.msg.subdivision"));

        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT s FROM Station s ORDER BY s.bezeichnung");
        cmbStation.setModel(SYSTools.list2cmb(query.getResultList()));
        em.close();

        jdcHAUF.setMaxSelectableDate(new Date());
        jdcHAUF.setDate(new Date());
    }

    private void jdcDOBPropertyChange(PropertyChangeEvent e) {
        check();
    }

    private void check() {
        validate.execute(new Pair<Date, Station>(jdcHAUF.getDate(), (Station) cmbStation.getSelectedItem()));
    }

    private void cmbStationItemStateChanged(ItemEvent e) {
        check();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblHAUF = new JLabel();
        jdcHAUF = new JDateChooser();
        lblStation = new JLabel();
        cmbStation = new JComboBox();

        //======== this ========
        setLayout(new FormLayout(
            "default, $lcgap, pref, $lcgap, default:grow, $lcgap, default",
            "3*(default, $lgap), default"));

        //---- lblHAUF ----
        lblHAUF.setText("text");
        lblHAUF.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblHAUF, CC.xy(3, 3));

        //---- jdcHAUF ----
        jdcHAUF.setFont(new Font("Arial", Font.PLAIN, 14));
        jdcHAUF.addPropertyChangeListener("date", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                jdcDOBPropertyChange(e);
            }
        });
        add(jdcHAUF, CC.xy(5, 3));

        //---- lblStation ----
        lblStation.setText("text");
        lblStation.setFont(new Font("Arial", Font.PLAIN, 14));
        add(lblStation, CC.xy(3, 5));

        //---- cmbStation ----
        cmbStation.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbStation.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbStationItemStateChanged(e);
            }
        });
        add(cmbStation, CC.xywh(5, 5, 2, 1));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblHAUF;
    private JDateChooser jdcHAUF;
    private JLabel lblStation;
    private JComboBox cmbStation;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
