/*
 * Created by JFormDesigner on Fri Jan 10 14:34:44 CET 2014
 */

package op.roster;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.roster.Timeclock;
import entity.roster.TimeclockTools;
import entity.system.Users;
import entity.system.UsersTools;
import op.OPDE;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

/**
 * @author Torsten Löhr
 */
public class PnlTimeClock extends JPanel {
    public static final String internalClassID = "dlglogin.timeclock";

    Vector<Vector> data;
    private Users user;
    private Timeclock activeTC;
    boolean onlyOnce = true;
    boolean initMode = false;

    public PnlTimeClock() {
        this.user = OPDE.getLogin().getUser();
        activeTC = null;
        initComponents();
        lblComment.setText(OPDE.lang.getString("misc.msg.comment"));
        initPanel();
    }

    private void initPanel() {

        txtComment.setText(null);

        // TODO: remove after development
        if (onlyOnce) {
            onlyOnce = false;
            cmbUser.setModel(new DefaultComboBoxModel(new Vector<Users>(UsersTools.getUsers(true))));
            cmbUser.setSelectedItem(user);
            cmbUser.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        user = (Users) e.getItem();
                        initPanel();
                    }
                }
            });
        }

        ArrayList<Timeclock> list = TimeclockTools.getAllWithinLast(4, user);
        data = new Vector<Vector>();
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        for (Timeclock timeclock : list) {
            Vector line = new Vector(3);
            line.add(df.format(timeclock.getBegin()));

            if (timeclock.isOpen()) {
                line.add(">>>>>>>>");
            } else {
                line.add(df.format(timeclock.getEnd()));
            }


            line.add(SYSTools.catchNull(timeclock.getText()));
            data.add(line);

        }

        activeTC = TimeclockTools.getActive(user);

        initMode = true;
        btnCome.setSelected(activeTC != null && activeTC.isOpen());
        btnGo.setSelected(activeTC == null || !activeTC.isOpen());
        lblYouAre.setText(btnCome.isSelected() ? OPDE.lang.getString("dlglogin.timeclock.youAreIn") : OPDE.lang.getString("dlglogin.timeclock.youAreOut"));
        initMode = false;


        Vector header = new Vector(3);
        header.add(OPDE.lang.getString("dlglogin.timeclock.came"));
        header.add(OPDE.lang.getString("dlglogin.timeclock.gone"));
        header.add(OPDE.lang.getString("misc.msg.comment"));


        tblTimeclocks.setModel(new DefaultTableModel(data, header));

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jspTimeclocks.getVerticalScrollBar().setValue(0);
            }
        });


    }

    private void btnComeItemStateChanged(ItemEvent ie) {
        if (initMode) return;
        if (ie.getStateChange() == ItemEvent.SELECTED) {

            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();
                activeTC = em.merge(new Timeclock(user, OPDE.getMyStation().getHome()));
                activeTC.setText(SYSTools.catchNull(txtComment.getText()));

                Users myUser = em.merge(user);
                em.lock(myUser, LockModeType.OPTIMISTIC);

                em.getTransaction().commit();

                initPanel();
                lblYouAre.setText(OPDE.lang.getString("dlglogin.timeclock.youAreInNow"));

            } catch (OptimisticLockException ole) {
                OPDE.warn(ole);
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                OPDE.fatal(e);
            } finally {
                em.close();
            }


        }
    }

    private void btnGoItemStateChanged(ItemEvent ie) {
        if (initMode) return;
        if (ie.getStateChange() == ItemEvent.SELECTED) {

            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();

                Timeclock myTimeclock = em.merge(activeTC);

                String text = SYSTools.catchNull(myTimeclock.getText());
                if (!SYSTools.catchNull(txtComment.getText()).isEmpty()) {
                    if (!text.isEmpty()) {
                        text = SYSTools.catchNull(txtComment.getText()) + "; " + text;
                    } else {
                        text = SYSTools.catchNull(txtComment.getText());
                    }
                }
                myTimeclock.setText(text);

                em.lock(myTimeclock, LockModeType.OPTIMISTIC);
                myTimeclock.setEnd(new Date());

                Users myUser = em.merge(user);
                em.lock(myUser, LockModeType.OPTIMISTIC);

                em.getTransaction().commit();

                initPanel();
                lblYouAre.setText(OPDE.lang.getString("dlglogin.timeclock.youAreOutNow"));

            } catch (OptimisticLockException ole) {
                OPDE.warn(ole);
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                OPDE.fatal(e);
            } finally {
                em.close();
            }


        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        btnCome = new JToggleButton();
        btnGo = new JToggleButton();
        lblYouAre = new JLabel();
        lblComment = new JLabel();
        scrollPane1 = new JScrollPane();
        txtComment = new JTextArea();
        jspTimeclocks = new JScrollPane();
        tblTimeclocks = new JTable();
        cmbUser = new JComboBox();

        //======== this ========
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setLayout(new BorderLayout());

        //======== panel1 ========
        {
            panel1.setLayout(new FormLayout(
                    "default:grow, $lcgap, default:grow",
                    "fill:default:grow, $rgap, 2*(default, $lgap), fill:30dlu:grow, $lgap, fill:default:grow, $lgap, default"));

            //---- btnCome ----
            btnCome.setText("gekommen");
            btnCome.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/arrivals_pictogram_T_32.png")));
            btnCome.setForeground(new Color(0, 153, 0));
            btnCome.setFont(new Font("Arial", Font.BOLD, 16));
            btnCome.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    btnComeItemStateChanged(e);
                }
            });
            panel1.add(btnCome, CC.xy(1, 1));

            //---- btnGo ----
            btnGo.setText("gegangen");
            btnGo.setIcon(new ImageIcon(getClass().getResource("/artwork/32x32/departures_pictogram_T_32.png")));
            btnGo.setFont(new Font("Arial", Font.BOLD, 16));
            btnGo.setForeground(new Color(153, 0, 0));
            btnGo.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    btnGoItemStateChanged(e);
                }
            });
            panel1.add(btnGo, CC.xy(3, 1));

            //---- lblYouAre ----
            lblYouAre.setText("text");
            lblYouAre.setFont(new Font("Arial", Font.BOLD, 18));
            panel1.add(lblYouAre, CC.xywh(1, 3, 3, 1));

            //---- lblComment ----
            lblComment.setText("text");
            lblComment.setFont(new Font("Arial", Font.PLAIN, 11));
            panel1.add(lblComment, CC.xy(3, 5, CC.RIGHT, CC.DEFAULT));

            //======== scrollPane1 ========
            {
                scrollPane1.setViewportView(txtComment);
            }
            panel1.add(scrollPane1, CC.xywh(1, 7, 3, 1));

            //======== jspTimeclocks ========
            {
                jspTimeclocks.setViewportView(tblTimeclocks);
            }
            panel1.add(jspTimeclocks, CC.xywh(1, 9, 3, 1));
            panel1.add(cmbUser, CC.xywh(1, 11, 3, 1));
        }
        add(panel1, BorderLayout.CENTER);

        //---- buttonGroup1 ----
        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(btnCome);
        buttonGroup1.add(btnGo);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JToggleButton btnCome;
    private JToggleButton btnGo;
    private JLabel lblYouAre;
    private JLabel lblComment;
    private JScrollPane scrollPane1;
    private JTextArea txtComment;
    private JScrollPane jspTimeclocks;
    private JTable tblTimeclocks;
    private JComboBox cmbUser;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
