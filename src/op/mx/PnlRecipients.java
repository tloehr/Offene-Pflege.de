/*
 * Created by JFormDesigner on Fri May 30 15:30:55 CEST 2014
 */

package op.mx;

import com.jidesoft.swing.AutoCompletion;
import com.jidesoft.swing.SelectAllUtils;
import entity.system.Users;
import entity.system.UsersTools;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.RiverLayout;
import op.tools.RoundedBorder;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * a generic panel to edit or show lists of recipients
 *
 * @author Torsten Löhr
 */
public class PnlRecipients extends JPanel {

    private boolean editable;
    HashMap<String, Users> mapAllUsers = new HashMap<>();
    HashSet<Users> recipients;
    HashMap<Users, JButton> mapBtn4Recipient;
    JTextField txtRecipients;
    AutoCompletion ac;
    ArrayList<Closure> listeners;

    int MAXLINE = 5;

    public PnlRecipients() {
        this(new ArrayList<>(), false);
    }

    public PnlRecipients(Collection<Users> recipients, boolean editable) {
        this.editable = editable;
        setLayout(new RiverLayout(10, 5));
        listeners = new ArrayList<>();
        this.recipients = new HashSet<>(recipients);
        initPanel();
    }


    public void addNotifyListeners(Closure listener) {
        listeners.add(listener);
    }

    void notifyListeners(Users users) {
        for (Closure listener : listeners) {
            listener.execute(users);
        }
    }


    public HashSet<Users> getRecipients() {
        return recipients;
    }

    private void initPanel() {

        mapBtn4Recipient = new HashMap<>();

        int rcpnum = 1;
        for (Users recipient : recipients) {
            if (rcpnum % MAXLINE == 0) {
                add(createButton(recipient), RiverLayout.LINE_BREAK);
            } else {
                add(createButton(recipient), RiverLayout.LEFT);
            }
            rcpnum++;
        }


        ArrayList<Users> userListToSearchIn = UsersTools.getUsers(false);
        userListToSearchIn.remove(OPDE.getLogin().getUser()); // remove myself from the searchlist
        for (Users users : userListToSearchIn) {
            mapAllUsers.put(users.getUID(), users);
            mapAllUsers.put(users.getFullname().toLowerCase(), users);
            mapAllUsers.put((users.getVorname() + " " + users.getName()).toLowerCase(), users);
        }

        txtRecipients = new JTextField(10);
        SelectAllUtils.install(txtRecipients);
        ac = new AutoCompletion(txtRecipients, mapAllUsers.keySet().toArray(new String[]{}));


        ac.setStrict(false);
        ac.setStrictCompletion(false);
        txtRecipients.addActionListener(e -> recipientsChanged());

        txtRecipients.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                recipientsChanged();
            }
        });

        txtRecipients.addKeyListener(new KeyAdapter() {
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

        add(txtRecipients);

        txtRecipients.setEnabled(editable);
    }

    private void recipientsChanged() {

        if (!editable) return;

        if (txtRecipients.getText().isEmpty()) return;
        if (txtRecipients.getText().length() > 100) return;

        final String enteredText = SYSTools.tidy(txtRecipients.getText()).toLowerCase();

        if (!mapAllUsers.containsKey(enteredText)) return;

        if (!recipients.contains(mapAllUsers.get(enteredText))) {
            recipients.add(mapAllUsers.get(enteredText));

            SwingUtilities.invokeLater(() -> {
                if (recipients.size() % MAXLINE == 0) {
                    add(createButton(mapAllUsers.get(enteredText)), RiverLayout.LINE_BREAK);
                } else {
                    add(createButton(mapAllUsers.get(enteredText)), RiverLayout.LEFT);
                }
                txtRecipients.setText("");
                revalidate();
                repaint();
                notifyListeners(mapAllUsers.get(enteredText));
            });

        }

    }

    public void setEditable(boolean editable) {
        this.editable = editable;
        txtRecipients.setEnabled(editable);
    }


    private JButton createButton(final Users recipient) {

        if (mapBtn4Recipient.containsKey(recipient)) {
            return mapBtn4Recipient.get(recipient);
        }

        final JButton jButton = new JButton(recipient.getFullname() + " [" + recipient.getUID() + "]", SYSConst.icon16userDel);
        jButton.setFont(SYSConst.ARIAL12);
        jButton.setBorder(new RoundedBorder(10));
        jButton.setHorizontalTextPosition(SwingConstants.LEADING);
        jButton.setForeground(SYSConst.blue1[SYSConst.dark3]);

        jButton.addActionListener(e -> {
            if (!editable) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("mx.recipients.cant.edit"));
                return;
            }
            recipients.remove(recipient);
            mapBtn4Recipient.remove(recipient);
            SwingUtilities.invokeLater(() -> {
                removeAll();
                add(txtRecipients);

                int rcpnum = 1;
                for (JButton btn : mapBtn4Recipient.values()) {
                    if (rcpnum % MAXLINE == 0) {
                        add(btn, RiverLayout.LINE_BREAK);
                    } else {
                        add(btn, RiverLayout.LEFT);
                    }
                    rcpnum++;
                }

                remove(jButton);
                revalidate();
                repaint();
                notifyListeners(recipient);
            });

        });

        mapBtn4Recipient.put(recipient, jButton);

        return jButton;
    }

    public void clear() {
        listeners.clear();
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
