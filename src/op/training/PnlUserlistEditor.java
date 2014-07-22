/*
 * Created by JFormDesigner on Fri May 30 15:30:55 CEST 2014
 */

package op.training;

import com.jidesoft.swing.AutoCompletion;
import com.jidesoft.swing.SelectAllUtils;
import entity.staff.Training;
import entity.staff.Training2Users;
import entity.staff.Training2UsersTools;
import entity.system.Users;
import entity.system.UsersTools;
import op.tools.RiverLayout;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Torsten Löhr
 */
public class PnlUserlistEditor extends JPanel {

    private final Training training;
    private final Closure editAction;
    private final boolean editmode;
    //    HashSet<Training2Users> listSelectedUsers;
    HashMap<Training2Users, JPanel> mapButtons;
    ArrayList<String> completionList;
    JTextField txtUsers;
    AutoCompletion ac;
    JPanel pnlUsersearch, pnlSelectedUsers;
    JList lstUsersFound;

    int MAXLINE = 6;

    public PnlUserlistEditor(Training training, Closure editAction) {
        this.training = training;
        this.editAction = editAction;
        this.editmode = editAction != null;
        setLayout(new BorderLayout(5, 5));

//        this.listSelectedUsers = new HashSet<>(training.getAttendees());
        this.completionList = new ArrayList<>();

        initPanel();
    }


    private void initPanel() {

        pnlSelectedUsers = new JPanel(new RiverLayout(5, 5));
        add(pnlSelectedUsers, BorderLayout.CENTER);

        mapButtons = new HashMap<>();

        for (Training2Users training2Users : training.getAttendees()) {
            pnlSelectedUsers.add(createUserPanel(training2Users));
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

//                        SwingUtilities.invokeLater(new Runnable() {
//                            @Override
//                            public void run() {
//
//
//
//
//
//
////                                txtUsers.setText("");
//                                revalidate();
//                                repaint();
//                            }
//                        });

                        Training2Users training2Users = new Training2Users(new Date(), thisUser, training);

                        if (training.getAttendees().size() % MAXLINE == 0) {
                            pnlSelectedUsers.add(createUserPanel(training2Users), RiverLayout.PARAGRAPH_BREAK);
                        } else {
                            pnlSelectedUsers.add(createUserPanel(training2Users), RiverLayout.LEFT);
                        }

                        training.getAttendees().add(training2Users);

                        editAction.execute(training);
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

//            ac.setStrict(false);
//            ac.setStrictCompletion(false);
//            txtUsers.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    cmbTagsActionPerformed(e);
//                }
//            });
//

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

    private void notifyCaller() {

    }

//    private void cmbTagsActionPerformed(ActionEvent e) {
//
//        if (!editmode) return;
//
//        if (txtUsers.getText().isEmpty()) return;
//        if (txtUsers.getText().length() > 100) return;
//
//        final String enteredText = SYSTools.tidy(txtUsers.getText());
//
//        if (!mapAllTags.containsKey(enteredText)) {
//            Commontags myNewCommontag = new Commontags(SYSTools.tidy(enteredText));
//            mapAllTags.put(enteredText, myNewCommontag);
//            ac.uninstallListeners();
//            ac = new AutoCompletion(txtUsers, mapAllTags.keySet().toArray(new String[]{}));
//            ac.setStrict(false);
//            ac.setStrictCompletion(false);
//        }
//
//        if (!listSelectedTags.contains(mapAllTags.get(enteredText))) {
//            listSelectedTags.add(mapAllTags.get(enteredText));
//
//            SwingUtilities.invokeLater(new Runnable() {
//                @Override
//                public void run() {
//
//                    if (listSelectedTags.size() % MAXLINE == 0) {
//                        add(createUserPanel(mapAllTags.get(enteredText)), RiverLayout.LINE_BREAK);
//                    } else {
//                        add(createUserPanel(mapAllTags.get(enteredText)), RiverLayout.LEFT);
//                    }
//
//                    txtUsers.setText("");
//                    revalidate();
//                    repaint();
//                }
//            });
//        }
//    }


    private JPanel createUserPanel(final Training2Users training2Users) {

        if (mapButtons.containsKey(training2Users)) {
            return mapButtons.get(training2Users);
        }

        final JPanel pnlButton = new JPanel();
        pnlButton.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        pnlButton.setToolTipText(training2Users.getAttendee().getFullname() + "; " + DateFormat.getDateInstance(DateFormat.SHORT).format(training2Users.getPit()));
        pnlButton.add(new JLabel(training2Users.getAttendee().getUID()));

        if (editmode) {
            JButton btnDelUser = new JButton(null, SYSConst.icon16userDel);

            btnDelUser.setFont(SYSConst.ARIAL12);
            btnDelUser.setHorizontalTextPosition(SwingConstants.LEADING);
//            btnDelUser.setForeground(SYSConst.green2[SYSConst.dark4]);


//            btnDelUser.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    listSelectedUsers.remove(training2Users);
//                    mapButtons.remove(training2Users);
//                    training.getAttendees().remove(training2Users);
//
//                    SwingUtilities.invokeLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            removeAll();
//
//                            add(txtUsers);
//                            int tagnum = 1;
//
//                            for (JPanel btn : mapButtons.values()) {
//                                if (tagnum % MAXLINE == 0) {
//                                    pnlSelectedUsers.add(btn, RiverLayout.LINE_BREAK);
//                                } else {
//                                    pnlSelectedUsers.add(btn, RiverLayout.LEFT);
//                                }
//                                tagnum++;
//                            }
//
//                            pnlButton.remove(pnlButton);
//                            pnlButton.revalidate();
//                            pnlButton.repaint();
//                        }
//                    });
//                }
//            });

            pnlButton.add(btnDelUser);
        }

        JButton btnState = new JButton(Training2UsersTools.getIcon(training2Users));
        if (editmode) {
            btnState.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                }
            });
        }


        pnlButton.add(new JToggleButton("OK"));
        pnlButton.add(new JButton("Time"));


        return pnlButton;
    }


//    public static JButton createFilterButton(final Commontags commontag, ActionListener al) {
//
//
//        jButton.setFont(SYSConst.ARIAL12);
//        jButton.setBorder(new RoundedBorder(10));
//        jButton.setHorizontalTextPosition(SwingConstants.LEADING);
//        jButton.setForeground(SYSConst.purple1[SYSConst.medium4]);
//
//        return jButton;
//    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
