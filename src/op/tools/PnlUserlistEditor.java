/*
 * Created by JFormDesigner on Fri May 30 15:30:55 CEST 2014
 */

package op.tools;

import com.jidesoft.swing.AutoCompletion;
import com.jidesoft.swing.SelectAllUtils;
import entity.staff.Training2Users;
import entity.system.Commontags;
import entity.system.Users;
import entity.system.UsersTools;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Torsten Löhr
 */
public class PnlUserlistEditor extends JPanel {

    private final boolean editmode;
    //    HashMap<String, Commontags> mapAllTags = new HashMap<>();
    HashSet<Training2Users> listSelectedUsers;
    HashMap<Training2Users, JButton> mapButtons;
    ArrayList<String> completionList;
    JTextField txtUsers;
    AutoCompletion ac;
    JPanel pnlUsersearch, pnlSelectedUsers;
    JList lstUsersFound;

    int MAXLINE = 8;

    public PnlUserlistEditor(Collection<Training2Users> listSelectedUsers) {
        this(listSelectedUsers, false);
    }

    public PnlUserlistEditor(Collection<Training2Users> listSelectedUsers, boolean editmode, int maxline) {
        this(listSelectedUsers, editmode);
        MAXLINE = maxline;
    }

    public PnlUserlistEditor(Collection<Training2Users> listSelectedUsers, boolean editmode) {
        this.editmode = editmode;
        setLayout(new BorderLayout(5, 5));

        this.listSelectedUsers = new HashSet<>(listSelectedUsers);
        this.completionList = new ArrayList<>();

        initPanel();
    }

    public HashSet<Training2Users> getListSelectedUsers() {
        return listSelectedUsers;
    }

    private void initPanel() {

        pnlSelectedUsers = new JPanel(new RiverLayout(10, 5));
        add(pnlSelectedUsers, BorderLayout.CENTER);

        mapButtons = new HashMap<>();

//        for (Commontags commontags : CommontagsTools.getAll()) {
//            mapAllTags.put(commontags.getText(), commontags);
//        }
        for (Training2Users training2Users : listSelectedUsers) {
            pnlSelectedUsers.add(createButton(training2Users));
        }

        if (editmode) {
            pnlUsersearch = new JPanel();
            pnlUsersearch.setLayout(new BoxLayout(pnlUsersearch, BoxLayout.PAGE_AXIS));
            add(pnlUsersearch, BorderLayout.WEST);

            txtUsers = new JTextField(30);
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
            lstUsersFound = new JList(new DefaultListModel());
            pnlUsersearch.add(new JScrollPane(lstUsersFound));
        }
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
//                        add(createButton(mapAllTags.get(enteredText)), RiverLayout.LINE_BREAK);
//                    } else {
//                        add(createButton(mapAllTags.get(enteredText)), RiverLayout.LEFT);
//                    }
//
//                    txtUsers.setText("");
//                    revalidate();
//                    repaint();
//                }
//            });
//        }
//    }


    private JButton createButton(final Training2Users training2Users) {

        if (mapButtons.containsKey(training2Users)) {
            return mapButtons.get(training2Users);
        }

        final JButton jButton = new JButton(training2Users.getAttendee().getUID(), editmode ? SYSConst.icon16tagPurpleDelete2 : SYSConst.icon16tagPurple);
        jButton.setFont(SYSConst.ARIAL12);
        jButton.setBorder(new RoundedBorder(10));
        jButton.setHorizontalTextPosition(SwingConstants.LEADING);
//        jButton.setMargin(new Insets(2, 2, 2, 2));
        jButton.setForeground(SYSConst.purple1[SYSConst.dark1]);

        if (editmode) {

            jButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    listSelectedUsers.remove(training2Users);
                    mapButtons.remove(training2Users);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            removeAll();

                            add(txtUsers);
                            int tagnum = 1;

                            for (JButton btn : mapButtons.values()) {
                                if (tagnum % MAXLINE == 0) {
                                    add(btn, RiverLayout.LINE_BREAK);
                                } else {
                                    add(btn, RiverLayout.LEFT);
                                }
                                tagnum++;
                            }

                            remove(jButton);
                            revalidate();
                            repaint();
                        }
                    });
                }
            });
        }
        mapButtons.put(training2Users, jButton);

        return jButton;
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
