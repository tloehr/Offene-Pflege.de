package op.care.info;

import com.jidesoft.popup.JidePopup;
import entity.prescription.Hospital;
import entity.prescription.HospitalTools;
import op.OPDE;
import op.residents.PnlEditHospital;
import op.tools.GUITools;
import org.apache.commons.collections.Closure;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

/**
 * Created by tloehr on 06.03.15.
 */
public class PnlHospital extends JPanel {

    private final Closure changeEvent;
    private Hospital selected = null;
    ArrayList<Hospital> listHospitals = new ArrayList<Hospital>();
    private JComboBox<Hospital> cmbHospital;
    private JButton btnAdd, btnEdit;

    public PnlHospital(Closure changeEvent) {
        this.changeEvent = changeEvent;
        initComponents();
        initPanel();
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 0));
        cmbHospital = new JComboBox();
        JPanel panel3 = new JPanel();
        panel3.setLayout(new BoxLayout(panel3, BoxLayout.LINE_AXIS));

        cmbHospital.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbHospital.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbHospitalItemStateChanged(e);
            }
        });

        btnAdd = new JButton();
        btnAdd.setText(null);
        btnAdd.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
        btnAdd.setBorderPainted(false);
        btnAdd.setContentAreaFilled(false);
        btnAdd.setBorder(null);
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnAddActionPerformed(e);
            }
        });
        panel3.add(btnAdd);

        //---- btnEdit ----
        btnEdit = new JButton();
        btnEdit.setText(null);
        btnEdit.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/edit3.png")));
        btnEdit.setBorderPainted(false);
        btnEdit.setContentAreaFilled(false);
        btnEdit.setBorder(null);
        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnEditActionPerformed(e);
            }
        });
        panel3.add(btnEdit);

        add(cmbHospital, BorderLayout.CENTER);
        add(panel3, BorderLayout.EAST);
    }


    public void setSelected(Hospital selected) {
        if (listHospitals.contains(selected)) {
            cmbHospital.setSelectedItem(selected);
        } else {
            cmbHospital.setModel(new DefaultComboBoxModel(new Hospital[]{selected}));
        }
    }

    private void initPanel() {

        EntityManager em = OPDE.createEM();
        Query queryGP = em.createQuery("SELECT a FROM Hospital a WHERE a.state >= 0 ORDER BY a.name");
        listHospitals.addAll(queryGP.getResultList());
        em.close();

        listHospitals.add(0, null);

        cmbHospital.setModel(new DefaultComboBoxModel(listHospitals.toArray()));
        cmbHospital.setRenderer(HospitalTools.getKHRenderer());

    }

    private void btnAddActionPerformed(ActionEvent e) {
        final PnlEditHospital pnlEditHospital = new PnlEditHospital(new Hospital());
        final JidePopup popup = GUITools.createPanelPopup(pnlEditHospital, new Closure() {
            @Override
            public void execute(Object o) {
                if (o != null) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        Hospital myHospital = em.merge((Hospital) o);
                        em.getTransaction().commit();
                        cmbHospital.setModel(new DefaultComboBoxModel(new Hospital[]{myHospital}));
                        selected = myHospital;
                        changeEvent.execute(selected);
                    } catch (Exception ex) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        OPDE.fatal(ex);
                    } finally {
                        em.close();
                    }
                }
            }
        }, btnAdd);
        GUITools.showPopup(popup, SwingConstants.EAST);
    }

    private void btnEditActionPerformed(ActionEvent e) {
        if (cmbHospital.getSelectedItem() == null) return;
        final PnlEditHospital pnlEditHospital = new PnlEditHospital((Hospital) cmbHospital.getSelectedItem());
        final JidePopup popup = GUITools.createPanelPopup(pnlEditHospital, new Closure() {
            @Override
            public void execute(Object o) {
                if (o != null) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        Hospital myHospital = em.merge((Hospital) o);
                        em.getTransaction().commit();
                        cmbHospital.setModel(new DefaultComboBoxModel(new Hospital[]{myHospital}));
                        selected = myHospital;
                        changeEvent.execute(selected);
                    } catch (Exception ex) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        OPDE.fatal(ex);
                    } finally {
                        em.close();
                    }
                }
            }
        }, btnEdit);
        GUITools.showPopup(popup, SwingConstants.EAST);
    }

    private void cmbHospitalItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            selected = (Hospital) e.getItem();
            changeEvent.execute(selected);
        }
    }
}
