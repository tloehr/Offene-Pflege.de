/*
 * Created by JFormDesigner on Mon Jun 17 10:48:02 CEST 2013
 */

package op.care.info;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
import entity.prescription.GP;
import entity.prescription.GPTools;
import op.OPDE;
import op.residents.PnlEditGP;
import op.tools.GUITools;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.HorizontalLayout;

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
 * @author Torsten Löhr
 */
public class PnlGP extends JPanel {
    private final Closure changeEvent;
    private GP selected = null;
    private Boolean neurologist;
    private Boolean dermatology;
    ArrayList<GP> listGPs = new ArrayList<GP>();

    public PnlGP(Closure changeEvent) {
        this.changeEvent = changeEvent;
        this.neurologist = null;
        this.dermatology = null;
        initComponents();
        initPanel();
    }

    public PnlGP(Closure changeEvent, Boolean neurologist, Boolean dermatology) {
        this.changeEvent = changeEvent;
        this.neurologist = neurologist;
        this.dermatology = dermatology;
        initComponents();
        initPanel();
    }

    public void setSelected(GP selected){
        if (listGPs.contains(selected)){
            cmbGP.setSelectedItem(selected);
        } else {
            cmbGP.setModel(new DefaultComboBoxModel(new GP[]{selected}));
        }
    }

    private void initPanel() {
        if (neurologist != null && neurologist.booleanValue()) {
            EntityManager em = OPDE.createEM();
            Query queryGP = em.createQuery("SELECT a FROM GP a WHERE a.status >= 0 AND a.neurologist = TRUE ORDER BY a.name, a.vorname");
            listGPs.addAll(queryGP.getResultList());
            em.close();
        }

        if (neurologist == null) {
            listGPs.addAll(GPTools.getAllActive());
        }

        listGPs.add(0, null);
        cmbGP.setModel(new DefaultComboBoxModel(listGPs.toArray()));
        cmbGP.setRenderer(GPTools.getRenderer());

    }

    private void btnAddGPActionPerformed(ActionEvent e) {
        final PnlEditGP pnlGP = new PnlEditGP(new GP());
        final JidePopup popup = GUITools.createPanelPopup(pnlGP, new Closure() {
            @Override
            public void execute(Object o) {
                if (o != null) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        GP myGP = em.merge((GP) o);
                        em.getTransaction().commit();
                        cmbGP.setModel(new DefaultComboBoxModel(new GP[]{myGP}));
                        selected = myGP;
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
        }, btnAddGP);
        GUITools.showPopup(popup, SwingConstants.EAST);
    }

    private void btnEditGPActionPerformed(ActionEvent e) {
        if (cmbGP.getSelectedItem() == null) return;
        final PnlEditGP pnlGP = new PnlEditGP((GP) cmbGP.getSelectedItem());
        final JidePopup popup = GUITools.createPanelPopup(pnlGP, new Closure() {
            @Override
            public void execute(Object o) {
                if (o != null) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        GP myGP = em.merge((GP) o);
                        em.getTransaction().commit();
                        cmbGP.setModel(new DefaultComboBoxModel(new GP[]{myGP}));
                        selected = myGP;
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
        }, btnEditGP);
        GUITools.showPopup(popup, SwingConstants.EAST);
    }

    private void cmbGPItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            selected = (GP) e.getItem();
            changeEvent.execute(selected);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        cmbGP = new JComboBox();
        panel3 = new JPanel();
        btnAddGP = new JButton();
        btnEditGP = new JButton();

        //======== this ========
        setLayout(new FormLayout(
                "default:grow, $lcgap, default",
                "default"));

        //---- cmbGP ----
        cmbGP.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbGP.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbGPItemStateChanged(e);
            }
        });
        add(cmbGP, CC.xy(1, 1));

        //======== panel3 ========
        {
            panel3.setLayout(new HorizontalLayout(5));

            //---- btnAddGP ----
            btnAddGP.setText(null);
            btnAddGP.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
            btnAddGP.setBorderPainted(false);
            btnAddGP.setContentAreaFilled(false);
            btnAddGP.setBorder(null);
            btnAddGP.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnAddGPActionPerformed(e);
                }
            });
            panel3.add(btnAddGP);

            //---- btnEditGP ----
            btnEditGP.setText(null);
            btnEditGP.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/edit3.png")));
            btnEditGP.setBorderPainted(false);
            btnEditGP.setContentAreaFilled(false);
            btnEditGP.setBorder(null);
            btnEditGP.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnEditGPActionPerformed(e);
                }
            });
            panel3.add(btnEditGP);
        }
        add(panel3, CC.xy(3, 1));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JComboBox cmbGP;
    private JPanel panel3;
    private JButton btnAddGP;
    private JButton btnEditGP;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
