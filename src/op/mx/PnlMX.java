/*
 * Created by JFormDesigner on Tue Aug 02 11:17:34 CEST 2016
 */

package op.mx;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import entity.mx.MXmsg;
import entity.mx.MXmsgTools;
import entity.mx.MXrecipient;
import entity.mx.MXrecipientTools;
import entity.system.SYSPropsTools;
import entity.system.Users;
import gui.GUITools;
import gui.interfaces.CleanablePanel;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.jdesktop.swingx.VerticalLayout;
import tablerenderer.RNDHTML;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Torsten Löhr
 */
public class PnlMX extends CleanablePanel {

    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private MXmsg currentMessage = null;
    private PnlRecipients pnlRecipients;
    private TMmsgs tmmsgs;
    boolean splitPaneCurrentlyBeingSetBySystem = false;

    public PnlMX(JScrollPane jspSearch) {
        super("opde.mx");
        this.jspSearch = jspSearch;
        initComponents();
        pnlRecipients = new PnlRecipients();
        initTable();
        prepareSearchArea();

        splitPaneCurrentlyBeingSetBySystem = true;
        double pos;
        try {
            pos = Double.parseDouble(OPDE.getProps().getProperty("opde.mx:splitPaneDividerLocation"));
        } catch (Exception e) {
            pos = 0.5d;
        }
        splitPane1.setDividerLocation(SYSTools.getDividerInAbsolutePosition(splitPane1, pos));
        splitPaneCurrentlyBeingSetBySystem = false;

        btnSend.setText(SYSTools.xx("mx.send"));
        btnReply.setText(SYSTools.xx("mx.reply"));
        btnSave.setText(SYSTools.xx("mx.save.as.draft"));
        btnCancel.setText(SYSTools.xx("opde.wizards.buttontext.cancel"));
        panel2.add(pnlRecipients, CC.xy(1, 3, CC.DEFAULT, CC.DEFAULT));
        displayMessage();
    }

    private void initTable() {
        tmmsgs = new TMmsgs(MXmsgTools.getAllFor(OPDE.getLogin().getUser()));
        tblMsgs.setModel(tmmsgs);
        tblMsgs.getColumnModel().getColumn(TMmsgs.COL_PIT).setCellRenderer(new RNDHTML());
        tblMsgs.getColumnModel().getColumn(TMmsgs.COL_USER).setCellRenderer(new RNDHTML());
        tblMsgs.getColumnModel().getColumn(TMmsgs.COL_SUBJECT).setCellRenderer(new RNDHTML());
        tblMsgs.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mousePressedOnTable(e);
            }
        });
    }

    private void mousePressedOnTable(MouseEvent evt) {
        Point p = evt.getPoint();
        ListSelectionModel lsm = tblMsgs.getSelectionModel();
        Point p2 = evt.getPoint();
        SwingUtilities.convertPointToScreen(p2, tblMsgs);


        boolean singleRowSelected = lsm.getMaxSelectionIndex() == lsm.getMinSelectionIndex();

        final int row = tblMsgs.rowAtPoint(p);
        final int col = tblMsgs.columnAtPoint(p);

        if (singleRowSelected) {
            lsm.setSelectionInterval(row, row);
        }

        currentMessage = tmmsgs.getRow(row);
        displayMessage();

    }

    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);

        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout());
        mypanel.setBackground(Color.WHITE);

        CollapsiblePane searchPane = new CollapsiblePane(SYSTools.xx(internalClassID));
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);

        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }

        GUITools.addAllComponents(mypanel, addCommands());
        GUITools.addAllComponents(mypanel, addFilters());
        searchPane.setContentPane(mypanel);
        searchPanes.add(searchPane);
        searchPanes.addExpansion();
    }

    @Override
    public void cleanup() {
        tmmsgs.cleanup();
    }

    private java.util.List<Component> addCommands() {
        java.util.List<Component> list = new ArrayList<Component>();

        JideButton newMessage = GUITools.createHyperlinkButton(SYSTools.xx("mx.new.message"), SYSConst.icon22work, null);
        newMessage.addMouseListener(GUITools.getHyperlinkStyleMouseAdapter());
        newMessage.setAlignmentX(Component.LEFT_ALIGNMENT);
        newMessage.addActionListener(actionEvent -> {
            currentMessage = new MXmsg(OPDE.getLogin().getUser());
            currentMessage.setText("mx.text");
            currentMessage.setSubject("mx.subject");
            displayMessage();
        });
        list.add(newMessage);

        return list;
    }

    private java.util.List<Component> addFilters() {
        java.util.List<Component> list = new ArrayList<Component>();

        final JButton btnIncoming = GUITools.createHyperlinkButton("mx.incoming", SYSConst.icon16tagPurple, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TMmsgs newModel = new TMmsgs(MXmsgTools.getAllFor(OPDE.getLogin().getUser()));
                tblMsgs.setModel(newModel);
                tmmsgs.cleanup();
                tmmsgs = newModel;
            }
        });

        list.add(btnIncoming);
        return list;
    }

    private void displayMessage() {
        if (currentMessage == null) {
            txtSubject.setText("");
            txtMessage.setText("");
            lblFrom.setText("");
            txtMessage.setEditable(false);
            SwingUtilities.invokeLater(() -> {
                panel2.remove(pnlRecipients);
                pnlRecipients.clear();
                pnlRecipients = new PnlRecipients();
                panel2.add(pnlRecipients, CC.xy(1, 1, CC.DEFAULT, CC.DEFAULT));
                revalidate();
                repaint();
            });
            txtSubject.setEditable(false);
            return;
        }

        txtMessage.setText(currentMessage.getText());

        SwingUtilities.invokeLater(() -> {
            panel2.remove(pnlRecipients);
            pnlRecipients.clear();
            pnlRecipients = new PnlRecipients(MXrecipientTools.getAllUsersFor(currentMessage.getRecipients()), currentMessage.isDraft());
            pnlRecipients.addNotifyListeners(o -> SwingUtilities.invokeLater(() -> {
                Users recipientToChange = ((Users) o);
                MXrecipient mxRcp = MXrecipientTools.findMXrecipient(currentMessage, recipientToChange);
                if (mxRcp == null) {
                    currentMessage.getRecipients().add(new MXrecipient(recipientToChange, currentMessage));
                } else {
                    currentMessage.getRecipients().remove(mxRcp);
                }

                revalidate();
                repaint();
            }));
            panel2.add(pnlRecipients, CC.xy(1, 1, CC.DEFAULT, CC.DEFAULT));
            revalidate();
            repaint();
        });

        txtSubject.setText(currentMessage.getSubject());
        lblFrom.setText(SYSTools.xx("mx.sender") + ": " + currentMessage.getSender().getVorname() + " " + currentMessage.getSender().getName() + " [" + currentMessage.getSender().getUID() + "]");
        pnlRecipients.setEditable(currentMessage.isDraft());
        txtMessage.setEditable(currentMessage.isDraft());
        txtSubject.setEditable(currentMessage.isDraft());

        if (!currentMessage.isDraft() && !currentMessage.getSender().equals(OPDE.getLogin().getUser()) && MXrecipientTools.isUnread(currentMessage, OPDE.getLogin().getUser())) {
            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();
                final MXmsg myMessage = em.merge(currentMessage);
                final MXrecipient myRCP = em.merge(MXrecipientTools.findMXrecipient(myMessage, OPDE.getLogin().getUser()));
                myRCP.setReceived(new Date());
                myRCP.setUnread(false);
                myMessage.getRecipients().add(myRCP);
                em.getTransaction().commit();
                currentMessage = myMessage;
                tmmsgs.updateMsg(currentMessage);
            } catch (OptimisticLockException ole) {
                OPDE.warn(ole);
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
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

    private void btnSendActionPerformed(ActionEvent e) {
        if (currentMessage.getRecipients().isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("mx.cant.send.without.recipients", DisplayMessage.WARNING)));
            return;
        }

        if (!currentMessage.isDraft()) return;

        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            final MXmsg myMessage = em.merge(currentMessage);
            myMessage.setDraft(false);
            em.getTransaction().commit();
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("mx.msg.sent")));
        } catch (OptimisticLockException ole) {
            OPDE.warn(ole);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.fatal(ex);
        } finally {
            em.close();
            currentMessage = null;
            displayMessage();
        }
    }

    private void btnSaveActionPerformed(ActionEvent e) {
        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            final MXmsg myMessage = em.merge(currentMessage);
            myMessage.setDraft(true);
            em.getTransaction().commit();
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("mx.msg.draft.saved")));
        } catch (OptimisticLockException ole) {
            OPDE.warn(ole);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.fatal(ex);
        } finally {
            em.close();
            currentMessage = null;
            displayMessage();
        }
    }

    private void txtSubjectFocusGained(FocusEvent e) {
        SYSTools.markAllTxt((JTextField) e.getSource());
    }

    private void txtMessageCaretUpdate(CaretEvent e) {
        currentMessage.setText(txtMessage.getText());
    }

    private void txtSubjectCaretUpdate(CaretEvent e) {
        currentMessage.setSubject(txtSubject.getText());
    }

    private void splitPane1PropertyChange(PropertyChangeEvent e) {
        if (!splitPaneCurrentlyBeingSetBySystem) {
            SYSPropsTools.storeProp("opde.mx:splitPaneDividerLocation", SYSTools.getDividerInRelativePosition(splitPane1).toString(), OPDE.getLogin().getUser());
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        splitPane1 = new JSplitPane();
        scrollPane5 = new JScrollPane();
        tblMsgs = new JTable();
        panel2 = new JPanel();
        lblFrom = new JLabel();
        txtSubject = new JTextField();
        scrollPane4 = new JScrollPane();
        txtMessage = new JTextArea();
        panel1 = new JPanel();
        btnSend = new JButton();
        btnReply = new JButton();
        btnSave = new JButton();
        btnCancel = new JButton();

        //======== this ========
        setLayout(new FormLayout(
            "pref:grow",
            "default:grow, default"));

        //======== splitPane1 ========
        {
            splitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
            splitPane1.addPropertyChangeListener(e -> splitPane1PropertyChange(e));

            //======== scrollPane5 ========
            {
                scrollPane5.setViewportView(tblMsgs);
            }
            splitPane1.setTopComponent(scrollPane5);

            //======== panel2 ========
            {
                panel2.setLayout(new FormLayout(
                    "default:grow",
                    "default, $nlgap, pref, $nlgap, default, $nlgap, fill:default:grow"));

                //---- lblFrom ----
                lblFrom.setText("text");
                lblFrom.setFont(new Font("Dialog", Font.BOLD, 18));
                panel2.add(lblFrom, CC.xy(1, 1));

                //---- txtSubject ----
                txtSubject.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        txtSubjectFocusGained(e);
                    }
                });
                txtSubject.addCaretListener(e -> txtSubjectCaretUpdate(e));
                panel2.add(txtSubject, CC.xy(1, 5));

                //======== scrollPane4 ========
                {

                    //---- txtMessage ----
                    txtMessage.addCaretListener(e -> txtMessageCaretUpdate(e));
                    scrollPane4.setViewportView(txtMessage);
                }
                panel2.add(scrollPane4, CC.xy(1, 7));
            }
            splitPane1.setBottomComponent(panel2);
        }
        add(splitPane1, CC.xy(1, 1, CC.DEFAULT, CC.FILL));

        //======== panel1 ========
        {
            panel1.setLayout(new FormLayout(
                "3*(default, $lcgap), right:default:grow",
                "default"));

            //---- btnSend ----
            btnSend.setText("Senden");
            btnSend.addActionListener(e -> btnSendActionPerformed(e));
            panel1.add(btnSend, CC.xy(1, 1));

            //---- btnReply ----
            btnReply.setText("text");
            panel1.add(btnReply, CC.xy(3, 1));

            //---- btnSave ----
            btnSave.setText("text");
            btnSave.addActionListener(e -> btnSaveActionPerformed(e));
            panel1.add(btnSave, CC.xy(5, 1));

            //---- btnCancel ----
            btnCancel.setText("text");
            panel1.add(btnCancel, CC.xy(7, 1));
        }
        add(panel1, CC.xy(1, 2));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JSplitPane splitPane1;
    private JScrollPane scrollPane5;
    private JTable tblMsgs;
    private JPanel panel2;
    private JLabel lblFrom;
    private JTextField txtSubject;
    private JScrollPane scrollPane4;
    private JTextArea txtMessage;
    private JPanel panel1;
    private JButton btnSend;
    private JButton btnReply;
    private JButton btnSave;
    private JButton btnCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
