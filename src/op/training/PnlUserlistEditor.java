/*
 * Created by JFormDesigner on Fri May 30 15:30:55 CEST 2014
 */

package op.training;

import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.SelectAllUtils;
import com.toedter.calendar.JCalendar;
import entity.staff.Training;
import entity.staff.Training2Users;
import entity.staff.Training2UsersTools;
import entity.staff.TrainingTools;
import entity.system.Users;
import entity.system.UsersTools;
import op.OPDE;
import op.care.sysfiles.DlgFiles;
import op.threads.DisplayManager;
import op.tools.GUITools;
import op.tools.RiverLayout;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Torsten Löhr
 */
public class PnlUserlistEditor extends JPanel {

    private final Training training;
    private final Closure editAction;
    private final boolean editmode;
    JTextField txtUsers;
    JPanel pnlUsersearch, pnlSelectedUsers;
    JList lstUsersFound;

    int MAXLINE = 4;

    public PnlUserlistEditor(Training training, Closure editAction) {
        this.training = training;
        this.editAction = editAction;
        this.editmode = editAction != null;
        setLayout(new BorderLayout(5, 5));

        initPanel();
    }


    private void initPanel() {

        pnlSelectedUsers = new JPanel(new RiverLayout(5, 5));
        add(pnlSelectedUsers, BorderLayout.CENTER);

        ArrayList<Training2Users> training2UsersArrayList = new ArrayList<>(training.getAttendees());
        Collections.sort(training2UsersArrayList);

        int i = 0;
        for (Training2Users training2Users : training2UsersArrayList) {
            if (i % MAXLINE == 0) {
                pnlSelectedUsers.add(createUserPanel(training2Users), RiverLayout.PARAGRAPH_BREAK);
            } else {
                pnlSelectedUsers.add(createUserPanel(training2Users), RiverLayout.LEFT);
            }
            i++;
        }


        if (editmode) {
            pnlUsersearch = new JPanel();
            pnlUsersearch.setLayout(new BoxLayout(pnlUsersearch, BoxLayout.PAGE_AXIS));
            add(pnlUsersearch, BorderLayout.WEST);
            lstUsersFound = new JList(new DefaultListModel());
            lstUsersFound.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) return;
                    if (lstUsersFound.getSelectedValue() == null) return;

                    final Users thisUser = (Users) lstUsersFound.getSelectedValue();

                    if (!Training2UsersTools.contains(training.getAttendees(), thisUser)) {

                        Training2Users training2Users = new Training2Users(training.getStarting(), thisUser, training);

                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();
                            Training2Users myT2U = em.merge(training2Users);

                            Training myTraining = em.merge(training);
                            em.lock(myTraining, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                            myTraining.getAttendees().add(myT2U);

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

//                        SwingUtilities.invokeLater(new Runnable() {
//                            @Override
//                            public void run() {
//                                revalidate();
//                                repaint();
//                            }
//                        });
                    }


                }
            });

            txtUsers = new JTextField(15);
            txtUsers.addCaretListener(new CaretListener() {
                @Override
                public void caretUpdate(CaretEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            lstUsersFound.setModel(SYSTools.list2dlm(UsersTools.getUsers(txtUsers.getText(), false)));
                            lstUsersFound.revalidate();
                            lstUsersFound.repaint();
                        }
                    });
                }
            });

            SelectAllUtils.install(txtUsers);

            txtUsers.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (Character.isAlphabetic(c) || Character.isDigit(c)) {
                        super.keyTyped(e);
                    } else {
                        e.consume();
                    }
                }
            });

            pnlUsersearch.add(txtUsers);

            pnlUsersearch.add(new JScrollPane(lstUsersFound));
        }

    }

    private JPanel createUserPanel(final Training2Users training2Users) {

        final JPanel pnlButton = new JPanel();
        pnlButton.setBorder(new EmptyBorder(0, 0, 0, 10));
        pnlButton.setToolTipText(training2Users.getAttendee().getUID() + "; " + DateFormat.getDateInstance(DateFormat.SHORT).format(training2Users.getPit()));
        pnlButton.add(new JLabel(training2Users.getAttendee().getFullname()));

        if (editmode) {
            JButton btnDelUser = GUITools.getTinyButton("misc.msg.delete", SYSConst.icon16userDel);
            btnDelUser.setPressedIcon(SYSConst.icon16Pressed);
            btnDelUser.setFont(SYSConst.ARIAL12);
            btnDelUser.setHorizontalTextPosition(SwingConstants.LEADING);
//            btnDelUser.setForeground(SYSConst.green2[SYSConst.dark4]);


            btnDelUser.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        Training myTraining = em.merge(training);
                        em.lock(myTraining, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                        Training2Users myT2U = em.merge(training2Users);
                        myTraining.getAttendees().remove(myT2U);
                        em.remove(myT2U);
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
            });

            pnlButton.add(btnDelUser);
        }

        JButton btnState = GUITools.getTinyButton(Training2UsersTools.getTooltip(training2Users), Training2UsersTools.getIcon(training2Users));
        btnState.setPressedIcon(SYSConst.icon16Pressed);
        if (editmode) {
            btnState.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        Training myTraining = em.merge(training);
                        em.lock(myTraining, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                        Training2Users myT2U = em.merge(training2Users);
                        myTraining.getAttendees().remove(myT2U);

                        byte state = myT2U.getState();
                        state++;
                        if (state > Training2UsersTools.STATE_REFUSED) state = Training2UsersTools.STATE_OPEN;
                        myT2U.setState(state);
                        myTraining.getAttendees().add(myT2U);

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
            });
        }
        pnlButton.add(btnState);


        if (training.getState() == TrainingTools.STATE_WORK_PLACE_RELATED) {

            final JButton btnDate = GUITools.getTinyButton("misc.msg.date", SYSConst.icon16date);
            btnDate.setPressedIcon(SYSConst.icon16Pressed);
            if (editmode) {
                btnDate.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        JCalendar jdc = new JCalendar(training2Users.getPit());
                        jdc.addPropertyChangeListener("calendar", new PropertyChangeListener() {
                            @Override
                            public void propertyChange(PropertyChangeEvent evt) {
                                if (evt.getNewValue() == null) return;
                                Date date = ((GregorianCalendar) evt.getNewValue()).getTime();

                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    Training myTraining = em.merge(training);
                                    em.lock(myTraining, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                                    Training2Users myT2U = em.merge(training2Users);
                                    myTraining.getAttendees().remove(myT2U);
                                    myT2U.setPit(date);
                                    myTraining.getAttendees().add(myT2U);

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
                        });

                        final JidePopup popupInfo = new JidePopup();
                        popupInfo.setMovable(false);
                        popupInfo.setContentPane(jdc);
                        popupInfo.removeExcludedComponent(jdc);
                        popupInfo.setDefaultFocusComponent(jdc);
                        popupInfo.setOwner(btnDate);

                        GUITools.showPopup(popupInfo, SwingConstants.CENTER);


                    }
                });
            }
            pnlButton.add(btnDate);
        }

        final JButton btnFiles = GUITools.getTinyButton(null, training2Users.getAttachedFilesConnections().isEmpty() ? SYSConst.icon16attach : SYSConst.icon16greenStar);

        if (!training2Users.getAttachedFilesConnections().isEmpty()){
            btnFiles.setText(Integer.toString(training2Users.getAttachedFilesConnections().size()));
        }
        btnFiles.setToolTipText(SYSTools.xx("misc.btnfiles.tooltip"));
        btnFiles.setForeground(Color.BLUE);
        btnFiles.setFont(SYSConst.ARIAL12BOLD);
        btnFiles.setHorizontalTextPosition(SwingUtilities.CENTER);
        btnFiles.setPressedIcon(SYSConst.icon16Pressed);
        btnFiles.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnFiles.setAlignmentY(Component.TOP_ALIGNMENT);
        btnFiles.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnFiles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Closure fileHandleClosure = !editmode ? null : new Closure() {
                    @Override
                    public void execute(Object o) {
                        EntityManager em = OPDE.createEM();
                        final Training myTraining = em.find(Training.class, training2Users.getId());
                        em.close();
                        editAction.execute(myTraining);
                    }
                };
                new DlgFiles(training2Users, fileHandleClosure);
            }
        });
        btnFiles.setEnabled(OPDE.isFTPworking());
        pnlButton.add(btnFiles);

        return pnlButton;
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
