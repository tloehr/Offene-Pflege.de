/*
 * Created by JFormDesigner on Mon Jul 09 15:51:58 CEST 2012
 */

package op.residents.bwassistant;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
import entity.info.LCustodian;
import entity.info.LCustodianTools;
import op.OPDE;
import op.residents.PnlEditLC;
import op.tools.GUITools;
import op.tools.SYSConst;
import org.apache.commons.collections.Closure;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;
import java.util.List;

/**
 * @author Torsten Löhr
 */
public class PnlLC extends JPanel {
    public static final String internalClassID = "opde.admin.bw.wizard.page5";
    //    private double split1Pos;
    private Closure validate;
//    private PnlEditLC pnlEditLC;


    public PnlLC(Closure validate) {
        this.validate = validate;
        initComponents();
        initPanel();
    }

    private void initPanel() {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM LCustodian b WHERE b.status >= 0 ORDER BY b.name, b.vorname");
        List<LCustodian> listLCustodian = query.getResultList();
        em.close();
        listLCustodian.add(0, null);

//        pnlEditLC = new PnlEditLC(new LCustodian());
//        pnlRight.add(pnlEditLC, 0);

        cmbLC.setModel(new DefaultComboBoxModel(listLCustodian.toArray()));
        cmbLC.setRenderer(LCustodianTools.getRenderer());

    }

//    public void initSplitPanel() {
//        split1Pos = SYSTools.showSide(split1, SYSTools.LEFT_UPPER_SIDE);
//    }
//
//    private void btnCancelActionPerformed(ActionEvent e) {
//        split1Pos = SYSTools.showSide(split1, SYSTools.LEFT_UPPER_SIDE, SYSConst.SCROLL_TIME_FAST);
//    }
//
//    private void btnOKActionPerformed(ActionEvent e) {
//        LCustodian newLCustodian = pnlEditLC.getLCustodian();
//        if (newLCustodian != null) {
//            cmbBetreuer.setModel(new DefaultComboBoxModel(new LCustodian[]{newLCustodian}));
//            validate.execute(newLCustodian);
//        }
//        split1Pos = SYSTools.showSide(split1, SYSTools.LEFT_UPPER_SIDE, SYSConst.SCROLL_TIME_FAST);
//    }

    private JidePopup createPopup(final PnlEditLC pnlLC) {
        final JidePopup popup = new JidePopup();
        popup.setMovable(false);
        JPanel pnl = new JPanel(new BorderLayout(10, 10));

        pnl.add(pnlLC, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));

        JButton save = new JButton(SYSConst.icon22apply);
//        save.setAlignmentX(0.0f);
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popup.hidePopup();
                if (pnlLC.getLCustodian() != null) {
//                    EntityManager em = OPDE.createEM();
//                    try {
//                        em.getTransaction().begin();
//                        LCustodian myLC = em.merge(pnlLC.getLCustodian());
//                        em.getTransaction().commit();
                    cmbLC.setModel(new DefaultComboBoxModel(new LCustodian[]{pnlLC.getLCustodian()}));
                    validate.execute(cmbLC.getSelectedItem());
//                        resident.setLCustodian1(myLC);
//                    } catch (Exception ex) {
//                        if (em.getTransaction().isActive()) {
//                            em.getTransaction().rollback();
//                        }
//                        OPDE.fatal(ex);
//                    } finally {
//                        em.close();
//                    }
//                    cmbLCust.setModel(new DefaultComboBoxModel(new LCustodian[]{pnlLC.getLCustodian()}));
//                    resident.setLCustodian1(pnlLC.getLCustodian());
                }
            }
        });
        btnPanel.add(Box.createHorizontalGlue());
        btnPanel.add(save);
        pnl.add(btnPanel, BorderLayout.SOUTH);

        popup.setContentPane(pnl);
        popup.setPreferredSize(pnl.getPreferredSize());
        pnl.revalidate();
        popup.removeExcludedComponent(pnl);
        popup.setDefaultFocusComponent(pnl);
        return popup;
    }


    private void btnAddActionPerformed(ActionEvent e) {
        final JidePopup popupGP = createPopup(new PnlEditLC(new LCustodian()));
        popupGP.setOwner(btnAdd);
        popupGP.setMovable(false);
        GUITools.showPopup(popupGP, SwingConstants.SOUTH_WEST);
    }

    private void cmbBetreuerItemStateChanged(ItemEvent e) {
        validate.execute(cmbLC.getSelectedItem());
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        cmbLC = new JComboBox();
        btnAdd = new JButton();

        //======== this ========
        setLayout(new FormLayout(
            "default:grow, $lcgap, default",
            "default"));

        //---- cmbLC ----
        cmbLC.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cmbBetreuerItemStateChanged(e);
            }
        });
        add(cmbLC, CC.xy(1, 1));

        //---- btnAdd ----
        btnAdd.setText(null);
        btnAdd.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
        btnAdd.setContentAreaFilled(false);
        btnAdd.setBorderPainted(false);
        btnAdd.setBorder(null);
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnAddActionPerformed(e);
            }
        });
        add(btnAdd, CC.xy(3, 1));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JComboBox cmbLC;
    private JButton btnAdd;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
