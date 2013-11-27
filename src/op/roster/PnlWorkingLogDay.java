/*
 * Created by JFormDesigner on Wed Nov 27 14:23:26 CET 2013
 */

package op.roster;

import java.awt.*;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.swing.*;
import entity.roster.*;
import entity.system.Unique;
import entity.system.UniqueTools;
import op.OPDE;
import op.threads.DisplayManager;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Torsten Löhr
 */
public class PnlWorkingLogDay extends JPanel {
    private Rplan rplan;
    private RosterParameters rosterParameters;
    private ContractsParameterSet contractsParameterSet;
    private Closure afterAction;
    private ItemListener mainItemListener;
    private Symbol effectiveSymbol;
    private JTextField txtHours1, txtHours2, txtComment1, txtComment2;
    private boolean initPhase;

    public PnlWorkingLogDay(Rplan rplan, RosterParameters rosterParameters, ContractsParameterSet contractsParameterSet, Closure afterAction) {
        initPhase = true;
        this.rplan = rplan;
        this.rplan = rplan;
        this.rosterParameters = rosterParameters;
        this.contractsParameterSet = contractsParameterSet;
        this.afterAction = afterAction;
        effectiveSymbol = rosterParameters.getSymbol(rplan.getEffectiveSymbol());

        initComponents();
        initPanel();
        initPhase = false;
    }

    private void btnApplyItemStateChanged(ItemEvent e) {
        if (initPhase) return;
        if (e.getStateChange() == ItemEvent.SELECTED) {
            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();
                Rplan myRplan = em.merge(rplan);
                em.lock(myRplan, LockModeType.OPTIMISTIC);

                Unique unique = UniqueTools.getNewUID(em, "wlog_");

                for (Workinglog workinglog : WorkinglogTools.createWorkingLogs(myRplan, rosterParameters.getSymbol(myRplan.getEffectiveSymbol()), contractsParameterSet, unique.getUid())) {
                    myRplan.getWorkinglogs().add(em.merge(workinglog));
                }

                myRplan.setActual(myRplan.getEffectiveSymbol());

                em.getTransaction().commit();
                rplan = myRplan;
            } catch (OptimisticLockException ole) {
                OPDE.error(ole);
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
            } catch (Exception ex) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                OPDE.fatal(ex);
            } finally {
                em.close();

            }
        } else if (e.getStateChange() == ItemEvent.DESELECTED) {
            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();
                Rplan myRplan = em.merge(rplan);
                em.lock(myRplan, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                Collection<Workinglog> logs2remove = new ArrayList<Workinglog>();
                for (Workinglog workinglog : myRplan.getWorkinglogs()) {

                    if (workinglog.getActualKey() > 0) {
                        Workinglog myWorkinglog = em.merge(workinglog);
                        em.remove(myWorkinglog);
                        logs2remove.add(workinglog);
                    }

                    myRplan.getWorkinglogs().removeAll(logs2remove);
                    myRplan.setActual(null);
                }

                em.getTransaction().commit();
                rplan = myRplan;
            } catch (OptimisticLockException ole) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
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

    private void initPanel(){

        txtHours1 = new JTextField();
        txtHours2 = new JTextField();
        txtComment1 = new JTextField();
        txtComment2 = new JTextField();

        DefaultOverlayable overHours1 = new DefaultOverlayable(txtHours1);
        DefaultOverlayable overHours2 = new DefaultOverlayable(txtHours2);
        DefaultOverlayable overComm1 = new DefaultOverlayable(txtComment1);
        DefaultOverlayable overComm2 = new DefaultOverlayable(txtComment2);
        overHours1.addOverlayComponent(new JLabel("Stunden"));
        add(overHours1, CC.xy(3, 3));
        add(overHours2, CC.xy(3, 7));
        add(overComm1, CC.xy(3, 5));
        add(overComm2, CC.xy(3, 9));

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd.MM.");
        String text = "<b><font size=\"+1\">" + sdf.format(rplan.getStart()) + " &rarr; " + effectiveSymbol.getKey().toUpperCase() + "</font></b>"
                       + "<br/><font size=\"-1\">" + rplan.getEffectiveHome().getShortname() + "</font>"
                       + "<br/><i><font size=\"-1\">" + effectiveSymbol.getDescription() + "</font></i>";
        btnApply.setText(SYSTools.toHTMLForScreen(text));
        btnApply.setSelected(!rplan.getActual().isEmpty());
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        btnApply = new JToggleButton();
        scrollPane1 = new JScrollPane();
        txtSum = new JTextArea();
        btnAdditional1 = new JToggleButton();
        btnAdditional2 = new JToggleButton();

        //======== this ========
        setLayout(new FormLayout(
            "default, $lcgap, pref:grow, $lcgap, default:grow",
            "5*(default, $lgap), fill:default:grow"));

        //---- btnApply ----
        btnApply.setText("text");
        btnApply.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/empty.png")));
        btnApply.setHorizontalAlignment(SwingConstants.LEFT);
        btnApply.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/32x32/apply.png")));
        btnApply.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                btnApplyItemStateChanged(e);
            }
        });
        add(btnApply, CC.xywh(1, 1, 3, 1));

        //======== scrollPane1 ========
        {

            //---- txtSum ----
            txtSum.setEditable(false);
            scrollPane1.setViewportView(txtSum);
        }
        add(scrollPane1, CC.xywh(5, 1, 1, 11));

        //---- btnAdditional1 ----
        btnAdditional1.setText("1");
        btnAdditional1.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/ledred.png")));
        btnAdditional1.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/ledgreen.png")));
        btnAdditional1.setFont(new Font("Arial", Font.BOLD, 18));
        add(btnAdditional1, CC.xywh(1, 3, 1, 3));

        //---- btnAdditional2 ----
        btnAdditional2.setText("2");
        btnAdditional2.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/ledred.png")));
        btnAdditional2.setSelectedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/ledgreen.png")));
        btnAdditional2.setFont(new Font("Arial", Font.BOLD, 18));
        add(btnAdditional2, CC.xywh(1, 7, 1, 3));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JToggleButton btnApply;
    private JScrollPane scrollPane1;
    private JTextArea txtSum;
    private JToggleButton btnAdditional1;
    private JToggleButton btnAdditional2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
