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
import entity.system.Users;
import entity.system.UsersTools;
import gui.GUITools;
import gui.events.RelaxedDocumentListener;
import gui.interfaces.CleanablePanel;
import op.OPDE;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.DlgYesNo;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.VerticalLayout;
import tablerenderer.RNDHTML;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Torsten Löhr
 */
public class PnlMX extends CleanablePanel {

    private JScrollPane jspSearch;
    private JPopupMenu menu;
    private CollapsiblePanes searchPanes;
    private MXmsg currentMessageInEditor = null;
    private PnlRecipients pnlRecipients;
    private TMmsgs tmmsgs;
    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    private Logger logger = Logger.getLogger(getClass());

    public PnlMX(JScrollPane jspSearch) {
        super("opde.mx");
        this.jspSearch = jspSearch;
        initComponents();
        txtMessage.getDocument().addDocumentListener(new RelaxedDocumentListener(50, var1 -> {
            if (currentMessageInEditor != null) {
                currentMessageInEditor.setText(txtMessage.getText());
            }
        }));

        txtSubject.getDocument().addDocumentListener(new RelaxedDocumentListener(50, var1 -> {
            if (currentMessageInEditor != null) {
                currentMessageInEditor.setSubject(txtSubject.getText());
            }
        }));
        pnlRecipients = new PnlRecipients();
        initTable();
        prepareSearchArea();


        btnSend.setText(SYSTools.xx("mx.send"));
        btnReply.setText(SYSTools.xx("mx.reply"));
        btnSave.setText(SYSTools.xx("mx.save.as.draft"));
        btnCancel.setText(SYSTools.xx("opde.wizards.buttontext.cancel"));

        btnSend.setIcon(SYSConst.icon22mailSend);
        btnReply.setIcon(SYSConst.icon22mailReply);
        btnSave.setIcon(SYSConst.icon22edit3);
        btnCancel.setIcon(SYSConst.icon22cancel);

        panel2.add(pnlRecipients, CC.xy(1, 3));
        displayMessage(null);
    }

    private void initTable() {
        MXDataModelProvider incoming = new MXDataModelProvider("incoming") {
            @Override
            public void loadModel() {
                setModel(MXmsgTools.getAllFor(OPDE.getMe()));
            }
        };

        MXDataModelProvider sent = new MXDataModelProvider("sent") {
            @Override
            public void loadModel() {
                setModel(MXmsgTools.getSentFor(OPDE.getMe()));
            }
        };

        MXDataModelProvider draft = new MXDataModelProvider("draft") {
            @Override
            public void loadModel() {
                setModel(MXmsgTools.getDrafts(OPDE.getMe()));
            }
        };

        MXDataModelProvider trash = new MXDataModelProvider("trash") {
            @Override
            public void loadModel() {
                setModel(MXmsgTools.getTrashed(OPDE.getMe()));
            }
        };


        tmmsgs = new TMmsgs(incoming, sent, draft, trash);

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

        displayMessage(tmmsgs.getRow(tblMsgs.convertRowIndexToModel(row)));

        if (SwingUtilities.isRightMouseButton(evt)) {

            SYSTools.unregisterListeners(menu);
            menu = new JPopupMenu();

            /***
             *      _____    _ _ _
             *     | ____|__| (_) |_
             *     |  _| / _` | | __|
             *     | |__| (_| | | |_
             *     |_____\__,_|_|\__|
             *
             */
            JMenuItem itemPopupEdit = new JMenuItem(SYSTools.xx("misc.msg.edit"), SYSConst.icon22edit3);
            itemPopupEdit.addActionListener(actionEvent -> {
                EntityManager em = OPDE.createEM();
                try {
                    em.getTransaction().begin();
                    final MXmsg myMessage = em.merge(currentMessageInEditor);
                    em.lock(myMessage, LockModeType.OPTIMISTIC);
                    myMessage.setDraft(true);
                    em.getTransaction().commit();
                    displayMessage(currentMessageInEditor);
                } catch (OptimisticLockException ole) {
                    OPDE.warn(ole);
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                    reload();
                } catch (Exception ex) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    OPDE.fatal(ex);
                } finally {
                    em.close();

                }
            });
            itemPopupEdit.setEnabled(
                    currentMessageInEditor.getSender().equals(OPDE.getMe()) &&
                            (currentMessageInEditor.isDraft() || MXmsgTools.isUnread(currentMessageInEditor))
            );
            menu.add(itemPopupEdit);


            /***
             *      ____       _      _
             *     |  _ \  ___| | ___| |_ ___
             *     | | | |/ _ \ |/ _ \ __/ _ \
             *     | |_| |  __/ |  __/ ||  __/
             *     |____/ \___|_|\___|\__\___|
             *
             */
            JMenuItem itemPopupDelete = new JMenuItem(SYSTools.xx("misc.msg.delete"), SYSConst.icon22delete);
            itemPopupDelete.addActionListener(actionEvent -> {
                currentEditor = new DlgYesNo(SYSTools.xx("misc.questions.delete1") + "<br/><i>" + DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(currentMessageInEditor.getPit()) + "</i><br/>" + SYSTools.xx("misc.questions.delete2"), SYSConst.icon48delete, answer -> {
                    if (answer.equals(JOptionPane.YES_OPTION)) {


                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();
                            final MXmsg myMessage = em.merge(currentMessageInEditor);
                            em.lock(myMessage, LockModeType.OPTIMISTIC);

                            if (myMessage.getSender().equals(OPDE.getMe())) {
                                // my message ? then delete it
                                em.remove(myMessage);
                            } else {
                                // FOR me ? mark it as trashed
                                final MXrecipient myRCP = em.merge(MXrecipientTools.findMXrecipient(myMessage, OPDE.getMe()));
                                em.lock(myRCP, LockModeType.OPTIMISTIC);
                                myRCP.setTrashed(true);
                            }

                            em.getTransaction().commit();
                            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("mx.msg.trashed")));

                            displayMessage(null);

                        } catch (OptimisticLockException ole) {
                            OPDE.warn(ole);
                            if (em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }
                            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                            reload();
                        } catch (Exception ex) {
                            if (em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }
                            OPDE.fatal(ex);
                        } finally {
                            em.close();
                            currentEditor = null;
                        }
                    }
                });
                currentEditor.setVisible(true);
            });

            itemPopupDelete.setEnabled(
                    (
                            // if this is my message, then I can only delete it as long as it is in Draft mode or yet unread.
                            // my own messages are REALLY delete rather than marked as trashed
                            currentMessageInEditor.getSender().equals(OPDE.getMe()) &&
                                    (currentMessageInEditor.isDraft() || MXmsgTools.isUnread(currentMessageInEditor))
                    ) ||
                            (
                                    // I can always delete messages sent to me, because they are never really
                                    // deleted. simply put to trash.
                                    !currentMessageInEditor.getSender().equals(OPDE.getMe())
                            )
            );
            menu.add(itemPopupDelete);

            menu.show(evt.getComponent(), (int) p.getX(), (int) p.getY());
        }
    }

    @Override
    public void reload() {
        super.reload();
        displayMessage(null);
        tmmsgs.reload();
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
        super.cleanup();
        tmmsgs.cleanup();
    }

    private java.util.List<Component> addCommands() {
        java.util.List<Component> list = new ArrayList<Component>();

        JideButton newMessage = GUITools.createHyperlinkButton(SYSTools.xx("misc.commands.new"), SYSConst.icon22mailNew, null);
        newMessage.addMouseListener(GUITools.getHyperlinkStyleMouseAdapter());
        newMessage.setAlignmentX(Component.LEFT_ALIGNMENT);
        newMessage.addActionListener(actionEvent -> {
            MXmsg myMsg = new MXmsg(OPDE.getMe());
            myMsg.setText(SYSTools.xx("mx.enter.text.here"));
            myMsg.setSubject(SYSTools.xx("mx.col_subject"));
            displayMessage(myMsg);
        });
        list.add(newMessage);

        return list;
    }

    private java.util.List<Component> addFilters() {
        java.util.List<Component> list = new ArrayList<Component>();

        final JButton btnIncoming = GUITools.createHyperlinkButton("mx.incoming", SYSConst.icon16mailGet, e -> {
            currentMessageInEditor = null;
            tmmsgs.setCurrentMpdel("incoming");
            displayMessage(null);
            String buttonText = SYSTools.xx("mx.incoming") + " (" + tmmsgs.getRowCount() + ")";
            OPDE.getDisplayManager().setMainMessage(buttonText);
        });
        list.add(btnIncoming);

        final JButton btnSent = GUITools.createHyperlinkButton("mx.sent", SYSConst.icon16mailSend, e -> {
            tmmsgs.setCurrentMpdel("sent");
            displayMessage(null);
            OPDE.getDisplayManager().setMainMessage("mx.sent");
        });
        list.add(btnSent);

        final JButton btnDrafts = GUITools.createHyperlinkButton("mx.drafts", SYSConst.icon16edit, e -> {
            tmmsgs.setCurrentMpdel("draft");
            displayMessage(null);
            OPDE.getDisplayManager().setMainMessage("mx.drafts");
        });
        list.add(btnDrafts);

        final JButton btnTrashed = GUITools.createHyperlinkButton("mx.trashed", SYSConst.icon16trash, e -> {
            tmmsgs.setCurrentMpdel("trash");
            displayMessage(null);
            OPDE.getDisplayManager().setMainMessage("mx.trashed");
        });
        list.add(btnTrashed);

        return list;
    }

    private void displayMessage(final MXmsg msg) {
        currentMessageInEditor = msg;

        if (currentMessageInEditor == null) {
            txtSubject.setText("");
            txtMessage.setText("");
            lblFrom.setText("");
            txtMessage.setEditable(false);


            SwingUtilities.invokeLater(() -> {
                panel2.remove(pnlRecipients);
                pnlRecipients.clear();
                pnlRecipients = new PnlRecipients();
                panel2.add(pnlRecipients, CC.xy(1, 3, CC.DEFAULT, CC.DEFAULT));
                revalidate();
                repaint();
            });
            txtSubject.setEditable(false);
            return;
        }

//        logger.debug("displayMessage(" + msg.toString() + ")");

        // as soon as you can read the message in the editor, it is marked as RECEIVED with the current time.
        if (!currentMessageInEditor.isDraft() && !currentMessageInEditor.getSender().equals(OPDE.getMe()) && MXrecipientTools.isUnread(currentMessageInEditor, OPDE.getMe())) {
            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();
                final MXmsg myMessage = em.merge(currentMessageInEditor);
                final MXrecipient myRCP = em.merge(MXrecipientTools.findMXrecipient(myMessage, OPDE.getMe()));
                myRCP.setReceived(new Date());
                myRCP.setUnread(false);
                myMessage.getRecipients().add(myRCP);
                em.getTransaction().commit();
                currentMessageInEditor = myMessage;
                tmmsgs.updateMsg(myMessage);
            } catch (OptimisticLockException ole) {
                OPDE.warn(ole);
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                reload();
            } catch (Exception ex) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                OPDE.fatal(ex);
            } finally {
                em.close();
                OPDE.getDisplayManager().mailCheck();
            }
        }


        txtMessage.setText(currentMessageInEditor.getText());


        SwingUtilities.invokeLater(() -> {
            panel2.remove(pnlRecipients);
            pnlRecipients.clear();
            pnlRecipients = new PnlRecipients(MXrecipientTools.getAllUsersFor(currentMessageInEditor.getRecipients()), currentMessageInEditor.isDraft());
            pnlRecipients.addNotifyListeners(o -> SwingUtilities.invokeLater(() -> {
                Users recipientToChange = ((Users) o);
                MXrecipient mxRcp = MXrecipientTools.findMXrecipient(currentMessageInEditor, recipientToChange);
                if (mxRcp == null) {
                    currentMessageInEditor.getRecipients().add(new MXrecipient(recipientToChange, currentMessageInEditor));
                } else {
                    currentMessageInEditor.getRecipients().remove(mxRcp);
                }
            }));
            panel2.add(pnlRecipients, CC.xy(1, 3, CC.DEFAULT, CC.DEFAULT));
            revalidate();
            repaint();
        });

        txtSubject.setText(currentMessageInEditor.getSubject());
        lblFrom.setText(SYSTools.xx("mx.sender") + ": " + currentMessageInEditor.getSender().getVorname() + " " + currentMessageInEditor.getSender().getName() + " [" + currentMessageInEditor.getSender().getUID() + "]");
        pnlRecipients.setEditable(currentMessageInEditor.isDraft());
        txtMessage.setEditable(currentMessageInEditor.isDraft());
        txtSubject.setEditable(currentMessageInEditor.isDraft());


    }

    private void btnSendActionPerformed(ActionEvent e) {

        if (currentMessageInEditor == null) {
            return;
        }

        if (currentMessageInEditor.getRecipients().isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("mx.cant.send.without.recipients", DisplayMessage.WARNING)));
            return;
        }

        if (!currentMessageInEditor.isDraft()) return;

        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            final MXmsg myMessage = em.merge(currentMessageInEditor);
            em.lock(myMessage, LockModeType.OPTIMISTIC);
            myMessage.setDraft(false);
            em.getTransaction().commit();
            MXmsgTools.sendNotificationsFor(myMessage);
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("mx.msg.sent")));
        } catch (OptimisticLockException ole) {
            OPDE.warn(ole);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
            reload();
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.fatal(ex);
        } finally {
            em.close();
            displayMessage(null);
        }
    }

    private void btnSaveActionPerformed(ActionEvent e) {
        if (currentMessageInEditor == null) return;
        if (!currentMessageInEditor.getSender().equals(OPDE.getMe())) return;
        if (!currentMessageInEditor.isDraft()) return;

        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            final MXmsg myMessage = em.merge(currentMessageInEditor);
            em.lock(myMessage, LockModeType.OPTIMISTIC);
            myMessage.setDraft(true);
            em.getTransaction().commit();
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("mx.msg.draft.saved")));
        } catch (OptimisticLockException ole) {
            OPDE.warn(ole);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
            reload();
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.fatal(ex);
        } finally {
            em.close();
            displayMessage(null);
        }
    }

    private void txtSubjectFocusGained(FocusEvent e) {
        SYSTools.markAllTxt((JTextField) e.getSource());
    }

    private void splitPane1PropertyChange(PropertyChangeEvent e) {
//        if (!splitPaneCurrentlyBeingSetBySystem) {
//            SYSPropsTools.storeProp("opde.mx:splitPaneDividerLocation", SYSTools.getDividerInRelativePosition(splitPane1).toString(), OPDE.getMe());
//        }
    }

    private void splitPane1ComponentResized(ComponentEvent e) {
        splitPane1.setDividerLocation(SYSTools.getDividerInAbsolutePosition(splitPane1, 0.5d));
    }

    private void splitPane1ComponentShown(ComponentEvent e) {

//        splitPane1.setDividerLocation(SYSTools.getDividerInAbsolutePosition(splitPane1, 0.5d));

        /*
        splitPaneCurrentlyBeingSetBySystem = true;
        double pos;
        try {
            pos = Double.parseDouble(OPDE.getProps().getProperty("opde.mx:splitPaneDividerLocation"));
        } catch (Exception ex) {
            pos = 0.5d;
        }
        splitPane1.setDividerLocation(SYSTools.getDividerInAbsolutePosition(splitPane1, pos));
        splitPaneCurrentlyBeingSetBySystem = false;
        */
    }

    private void btnCancelActionPerformed(ActionEvent e) {
        displayMessage(null);
    }

    private void btnReplyActionPerformed(ActionEvent e) {
        if (currentMessageInEditor == null) return;
        if (currentMessageInEditor.isDraft()) return;
        if (currentMessageInEditor.getSender().equals(OPDE.getMe())) return; // cant reply to myself
        MXmsg answer = new MXmsg(OPDE.getMe());

        answer.getRecipients().add(new MXrecipient(currentMessageInEditor.getSender(), answer));
        answer.setText("\n----\n" + SYSTools.xx("mx.in.reply.to") + "\n" + UsersTools.getFullnameWithID(currentMessageInEditor.getSender()) + ", " + df.format(currentMessageInEditor.getPit()) + "\n" + currentMessageInEditor.getText());
        answer.setSubject(currentMessageInEditor.getSubject().startsWith("Re:") ? currentMessageInEditor.getSubject() : "Re: " + currentMessageInEditor.getSubject());

        displayMessage(answer);
        pnlRecipients.setEditable(false);

    }

    private void scrollPane5ComponentResized(ComponentEvent evt) {
        resizeTable();
    }

    private void resizeTable() {
        Dimension dim = scrollPane5.getSize();
        int textWidth = dim.width - 140 - 20;
        tblMsgs.getColumnModel().getColumn(0).setPreferredWidth(textWidth / 2);
        tblMsgs.getColumnModel().getColumn(1).setPreferredWidth(textWidth / 2);
        tblMsgs.getColumnModel().getColumn(2).setPreferredWidth(140);
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
            splitPane1.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    splitPane1ComponentResized(e);
                }
            });

            //======== scrollPane5 ========
            {
                scrollPane5.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        scrollPane5ComponentResized(e);
                    }
                });

                //---- tblMsgs ----
                tblMsgs.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
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
                panel2.add(txtSubject, CC.xy(1, 5));

                //======== scrollPane4 ========
                {

                    //---- txtMessage ----
                    txtMessage.setLineWrap(true);
                    txtMessage.setWrapStyleWord(true);
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
                    "5*(default, $lcgap), right:default:grow",
                    "default"));

            //---- btnSend ----
            btnSend.setText("Senden");
            btnSend.addActionListener(e -> btnSendActionPerformed(e));
            panel1.add(btnSend, CC.xy(1, 1));

            //---- btnReply ----
            btnReply.setText("text");
            btnReply.addActionListener(e -> btnReplyActionPerformed(e));
            panel1.add(btnReply, CC.xy(3, 1));

            //---- btnSave ----
            btnSave.setText("text");
            btnSave.addActionListener(e -> btnSaveActionPerformed(e));
            panel1.add(btnSave, CC.xy(5, 1));

            //---- btnCancel ----
            btnCancel.setText("text");
            btnCancel.addActionListener(e -> btnCancelActionPerformed(e));
            panel1.add(btnCancel, CC.xy(11, 1));
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
