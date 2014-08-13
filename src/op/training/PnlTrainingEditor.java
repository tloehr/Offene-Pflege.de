/*
 * Created by JFormDesigner on Thu Jul 17 15:44:17 CEST 2014
 */

package op.training;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDateChooser;
import entity.staff.Training;
import entity.staff.TrainingTools;
import entity.system.Commontags;
import op.OPDE;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.PnlCommonTags;
import op.tools.SYSCalendar;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.util.Date;

/**
 * @author Torsten Löhr
 */
public class PnlTrainingEditor extends JPanel {
    private Training training;
    private final Closure editAction;
    private PnlUserlistEditor pnlUserlistEditor;
    boolean initPhase = true;

    public PnlTrainingEditor(Training training, Closure editAction) {
        this.training = training;
        this.editAction = editAction;
        initComponents();
        initPanel();
    }

    private void initPanel() {
        lblDate.setText(SYSTools.xx("misc.msg.Date"));
        lblTitle.setText(SYSTools.xx("misc.msg.title"));
        lblDocent.setText(SYSTools.xx("opde.training.docent"));
        lblText.setText(SYSTools.xx("misc.msg.details"));
        lblTags.setText(SYSTools.xx("misc.msg.commontags"));
        lblTime.setText(SYSTools.xx("misc.msg.Time"));
        lblAttendees.setText(SYSTools.xx("misc.msg.attendees"));

        jdcStarting.setDate(training.getStarting());

        if (new LocalDate(training.getStarting()).toDateTimeAtStartOfDay().toDate().equals(training.getStarting())) {
            txtTimeStarting.setText(null);
            cbTime.setSelected(false);
            txtTimeStarting.setEnabled(false);
        } else {
            txtTimeStarting.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(training.getStarting()));
            cbTime.setSelected(true);
            txtTimeStarting.setEnabled(true);
        }

        cmbState.setModel(new DefaultComboBoxModel(SYSTools.translate(TrainingTools.STATES)));
        cmbState.setSelectedIndex(training.getState());
        cmbState.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() != ItemEvent.SELECTED) return;
                cmbStateItemStateChanged(e);
            }
        });

//        txtTimeStarting.setEnabled();

        txtTitle.setText(training.getTitle());
        txtDocent.setText(training.getDocent());
        txtText.setText(training.getText());

        add(new PnlCommonTags(training.getCommontags(), new Closure() {
            @Override
            public void execute(Object o) {
                if (o == null) return;
                EntityManager em = OPDE.createEM();

                try {
                    em.getTransaction().begin();

                    Commontags commontag = em.merge((Commontags) o);
                    Training myTraining = em.merge(training);
                    em.lock(myTraining, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                    if(myTraining.getCommontags().contains(commontag)){
                        myTraining.getCommontags().remove(commontag);
                    } else {
                        myTraining.getCommontags().add(commontag);
                    }

                    em.getTransaction().commit();

                    editAction.execute(myTraining);

                } catch (OptimisticLockException ole) {
                    OPDE.warn(ole);
                    OPDE.warn(ole);
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                    editAction.execute(null);
                } catch (Exception ex) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    OPDE.fatal(ex);
                } finally {
                    em.close();
                }
            }
        }), CC.xywh(1, 13, 9, 1));


        Closure afterUserlistEdited = editAction != null ? new Closure() {
            @Override
            public void execute(Object o) {
                if (o == null) {
                    editAction.execute(null);
                } else {
                    training = (Training) o;
                    editAction.execute(training);
                }
            }
        } : null;
        pnlUserlistEditor = new PnlUserlistEditor(training, afterUserlistEdited);
        add(pnlUserlistEditor, CC.xywh(1, 17, 9, 1));

        jdcStarting.addPropertyChangeListener("date", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                jdcStartingPropertyChange(evt);
            }
        });
        initPhase = false;
    }

    private void txtTimeFocusLost(FocusEvent e) {

        OPDE.debug("txtTimeFocusLost");

        EntityManager em = OPDE.createEM();

        try {
            Date time = SYSCalendar.parseTime(txtTimeStarting.getText()).getTime();
            LocalTime localTime = new LocalTime(time);
            LocalDate localDate = new LocalDate(training.getStarting());
            DateTime newPit = localDate.toDateTime(localTime);

            em.getTransaction().begin();
            Training myTraining = em.merge(training);
            em.lock(myTraining, LockModeType.OPTIMISTIC);
            myTraining.setStarting(newPit.toDate());
            em.getTransaction().commit();

            editAction.execute(myTraining);
        } catch (NumberFormatException nfe) {
            txtTimeStarting.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(training.getStarting()));
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.wrongtime"));

        } catch (OptimisticLockException ole) {
            OPDE.warn(ole);
            OPDE.warn(ole);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
            editAction.execute(null);
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.fatal(ex);
        } finally {
            em.close();
        }


    }

    private void cbTimeItemStateChanged(ItemEvent e) {
        if (initPhase) return;
        OPDE.debug("cbTimeItemStateChanged");
//        txtTimeStarting.setEnabled(e.getStateChange() == ItemEvent.SELECTED);

//        if (e.getStateChange() != ItemEvent.SELECTED) {
        EntityManager em = OPDE.createEM();

        try {

            LocalDate localDate = new LocalDate(training.getStarting());
            DateTime newPit = e.getStateChange() == ItemEvent.SELECTED ? localDate.toDateTimeAtCurrentTime() : localDate.toDateTimeAtStartOfDay();

            em.getTransaction().begin();
            Training myTraining = em.merge(training);
            em.lock(myTraining, LockModeType.OPTIMISTIC);
            myTraining.setStarting(newPit.toDate());
            em.getTransaction().commit();

            editAction.execute(myTraining);

        } catch (OptimisticLockException ole) {
            OPDE.warn(ole);
            OPDE.warn(ole);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
            editAction.execute(null);
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.fatal(ex);
        } finally {
            em.close();
        }
//        }

    }

    private void txtTitleFocusLost(FocusEvent e) {
        OPDE.debug("txtTitleFocusLost");
        if (txtTitle.getText().trim().isEmpty()) {
            return;
        }
        EntityManager em = OPDE.createEM();

        try {

            em.getTransaction().begin();
            Training myTraining = em.merge(training);
            em.lock(myTraining, LockModeType.OPTIMISTIC);
            myTraining.setTitle(SYSTools.tidy(txtTitle.getText()));
            em.getTransaction().commit();

            editAction.execute(myTraining);

        } catch (OptimisticLockException ole) {
            OPDE.warn(ole);
            OPDE.warn(ole);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
            editAction.execute(null);
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.fatal(ex);
        } finally {
            em.close();
        }
    }

    private void txtDocentFocusLost(FocusEvent e) {
        OPDE.debug("txtDocentFocusLost");
        EntityManager em = OPDE.createEM();

        try {

            em.getTransaction().begin();
            Training myTraining = em.merge(training);
            em.lock(myTraining, LockModeType.OPTIMISTIC);
            myTraining.setDocent(SYSTools.tidy(txtDocent.getText()));
            em.getTransaction().commit();

            editAction.execute(myTraining);

        } catch (OptimisticLockException ole) {
            OPDE.warn(ole);
            OPDE.warn(ole);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
            editAction.execute(null);
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.fatal(ex);
        } finally {
            em.close();
        }
    }

    private void jdcStartingPropertyChange(PropertyChangeEvent e) {
        OPDE.debug("jdcStartingPropertyChange");
        EntityManager em = OPDE.createEM();
        LocalTime localTime = new LocalTime(training.getStarting());
        LocalDate localDate = new LocalDate(jdcStarting.getDate());
        DateTime newPit = localDate.toDateTime(localTime);

        try {

            em.getTransaction().begin();
            Training myTraining = em.merge(training);
            em.lock(myTraining, LockModeType.OPTIMISTIC);
            myTraining.setStarting(newPit.toDate());
            em.getTransaction().commit();

            editAction.execute(myTraining);

        } catch (OptimisticLockException ole) {
            OPDE.warn(ole);
            OPDE.warn(ole);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
            editAction.execute(null);
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.fatal(ex);
        } finally {
            em.close();
        }
    }

    private void txtTextFocusLost(FocusEvent e) {
        OPDE.debug("txtTextFocusLost");
        EntityManager em = OPDE.createEM();

        try {

            em.getTransaction().begin();
            Training myTraining = em.merge(training);
            em.lock(myTraining, LockModeType.OPTIMISTIC);
            myTraining.setText(SYSTools.tidy(txtText.getText()));
            em.getTransaction().commit();

            editAction.execute(myTraining);

        } catch (OptimisticLockException ole) {
            OPDE.warn(ole);
            OPDE.warn(ole);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
            editAction.execute(null);
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.fatal(ex);
        } finally {
            em.close();
        }
    }

    private void cmbStateItemStateChanged(ItemEvent e) {
        OPDE.debug("cmbStateItemStateChanged");
        EntityManager em = OPDE.createEM();

        try {

            em.getTransaction().begin();
            Training myTraining = em.merge(training);
            em.lock(myTraining, LockModeType.OPTIMISTIC);
            myTraining.setState((byte) cmbState.getSelectedIndex());

            LocalDate localDate = new LocalDate(myTraining.getStarting());
            if (myTraining.getState() == TrainingTools.STATE_WORK_PLACE_RELATED) {
                myTraining.setStarting(localDate.toDateTimeAtStartOfDay().toDate());
            }
            em.getTransaction().commit();

            editAction.execute(myTraining);

        } catch (OptimisticLockException ole) {
            OPDE.warn(ole);
            OPDE.warn(ole);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
            editAction.execute(null);
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.fatal(ex);
        } finally {
            em.close();
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblTitle = new JLabel();
        lblDocent = new JLabel();
        lblText = new JLabel();
        txtTitle = new JTextField();
        txtDocent = new JTextField();
        scrollPane1 = new JScrollPane();
        txtText = new JTextArea();
        lblDate = new JLabel();
        lblTime = new JLabel();
        jdcStarting = new JDateChooser();
        cbTime = new JCheckBox();
        txtTimeStarting = new JTextField();
        cmbState = new JComboBox();
        lblTags = new JLabel();
        lblAttendees = new JLabel();

        //======== this ========
        setLayout(new FormLayout(
                "pref, $lcgap, default, 2*($lcgap, pref), $lcgap, default:grow",
                "$lcgap, 5*($lgap, default), $lgap, fill:30dlu, $lgap, default, $lgap, fill:default:grow"));

        //---- lblTitle ----
        lblTitle.setText("text");
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 10));
        add(lblTitle, CC.xywh(1, 3, 5, 1));

        //---- lblDocent ----
        lblDocent.setText("text");
        lblDocent.setFont(new Font("Arial", Font.PLAIN, 10));
        add(lblDocent, CC.xy(7, 3));

        //---- lblText ----
        lblText.setText("text");
        lblText.setFont(new Font("Arial", Font.PLAIN, 10));
        add(lblText, CC.xy(9, 3, CC.DEFAULT, CC.TOP));

        //---- txtTitle ----
        txtTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        txtTitle.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                txtTitleFocusLost(e);
            }
        });
        add(txtTitle, CC.xywh(1, 5, 5, 1));

        //---- txtDocent ----
        txtDocent.setFont(new Font("Arial", Font.PLAIN, 14));
        txtDocent.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                txtDocentFocusLost(e);
            }
        });
        add(txtDocent, CC.xy(7, 5));

        //======== scrollPane1 ========
        {

            //---- txtText ----
            txtText.setLineWrap(true);
            txtText.setWrapStyleWord(true);
            txtText.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    txtTextFocusLost(e);
                }
            });
            scrollPane1.setViewportView(txtText);
        }
        add(scrollPane1, CC.xywh(9, 5, 1, 5));

        //---- lblDate ----
        lblDate.setText("text");
        lblDate.setFont(new Font("Arial", Font.PLAIN, 10));
        add(lblDate, CC.xy(1, 7));

        //---- lblTime ----
        lblTime.setText("text");
        lblTime.setFont(new Font("Arial", Font.PLAIN, 10));
        add(lblTime, CC.xy(5, 7));

        //---- jdcStarting ----
        jdcStarting.setFont(new Font("Arial", Font.PLAIN, 14));
        add(jdcStarting, CC.xy(1, 9));

        //---- cbTime ----
        cbTime.setText(null);
        cbTime.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cbTimeItemStateChanged(e);
            }
        });
        add(cbTime, CC.xy(3, 9));

        //---- txtTimeStarting ----
        txtTimeStarting.setFont(new Font("Arial", Font.PLAIN, 14));
        txtTimeStarting.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                txtTimeFocusLost(e);
            }
        });
        add(txtTimeStarting, CC.xy(5, 9));
        add(cmbState, CC.xy(7, 9));

        //---- lblTags ----
        lblTags.setText("text");
        lblTags.setFont(new Font("Arial", Font.PLAIN, 10));
        add(lblTags, CC.xywh(1, 11, 7, 1, CC.DEFAULT, CC.TOP));

        //---- lblAttendees ----
        lblAttendees.setText("text");
        lblAttendees.setFont(new Font("Arial", Font.PLAIN, 10));
        add(lblAttendees, CC.xywh(1, 15, 7, 1, CC.DEFAULT, CC.TOP));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblTitle;
    private JLabel lblDocent;
    private JLabel lblText;
    private JTextField txtTitle;
    private JTextField txtDocent;
    private JScrollPane scrollPane1;
    private JTextArea txtText;
    private JLabel lblDate;
    private JLabel lblTime;
    private JDateChooser jdcStarting;
    private JCheckBox cbTime;
    private JTextField txtTimeStarting;
    private JComboBox cmbState;
    private JLabel lblTags;
    private JLabel lblAttendees;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
