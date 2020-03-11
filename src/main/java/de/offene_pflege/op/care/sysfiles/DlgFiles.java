/*
 * Created by JFormDesigner on Thu Jun 21 14:49:00 CEST 2012
 */

package de.offene_pflege.op.care.sysfiles;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.offene_pflege.entity.files.SYSFiles;
import de.offene_pflege.entity.files.SYSFilesTools;
import de.offene_pflege.entity.info.ResInfo;
import de.offene_pflege.entity.nursingprocess.NursingProcess;
import de.offene_pflege.entity.prescription.Prescription;
import de.offene_pflege.entity.reports.NReport;
import de.offene_pflege.entity.system.Users;
import de.offene_pflege.entity.values.ResValue;
import de.offene_pflege.interfaces.Attachable;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.system.FileDrop;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.MyJDialog;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Torsten Löhr
 */
public class DlgFiles extends MyJDialog {
    private Attachable attachable;
    //    private boolean filesAttached = false;
    private Closure afterAttachAction;
    private JList list;

    /**
     * Creates a generic file attachment dialog to show existing files and add new ones.
     *
     * @param attachable        object to add the files to
     * @param afterAttachAction what to do, after files have been attached. if this is <code>null</code>, file drops are not possible.
     */
    public DlgFiles(Attachable attachable, Closure afterAttachAction) {
        super(false);
        this.attachable = attachable;
        this.afterAttachAction = afterAttachAction;
        initComponents();
        initDialog();
    }

    private void initDialog() {
        if (!attachable.isActive()) {
            contentPanel.add(getFileListPanel(), CC.xywh(1, 1, 3, 1));
        } else {
            contentPanel.add(getFileDropPanel(), CC.xy(1, 1));
            contentPanel.add(getFileListPanel(), CC.xy(3, 1));
        }
    }

    private JPanel getFileDropPanel() {
        JPanel dropPanel = new JPanel();
        dropPanel.setLayout(new BorderLayout());
        JLabel dropLabel = new JLabel(SYSTools.xx("nursingrecords.files.drophere"), SYSConst.icon48kgetdock, SwingConstants.CENTER);
        dropLabel.setFont(SYSConst.ARIAL20);
        dropLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        dropLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        dropPanel.add(BorderLayout.CENTER, dropLabel);
        dropPanel.setPreferredSize(new Dimension(180, 180));
        dropPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        new FileDrop(dropPanel, files -> {
            java.util.List<SYSFiles> successful = SYSFilesTools.putFiles(files, attachable);
            if (!successful.isEmpty()) {
                list.setModel(SYSTools.list2dlm(getAttachedFilesList(attachable)));
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(successful.size() + " " + SYSTools.xx("misc.msg.Files") + " " + SYSTools.xx("misc.msg.added")));

            }
        });
        return dropPanel;
    }

    @Override
    public void dispose() {
        if (afterAttachAction != null) {
            afterAttachAction.execute(null);
        }
        super.dispose();
    }

    private JPanel getFileListPanel() {
        list = new JList();
        final JPanel pnlFilesList = new JPanel();
        pnlFilesList.setLayout(new BoxLayout(pnlFilesList, BoxLayout.LINE_AXIS));

        ArrayList<SYSFiles> files = getAttachedFilesList(attachable);

        if (files.isEmpty()) {
            list.setModel(new DefaultListModel());
        } else {
            list.setModel(SYSTools.list2dlm(files));
        }

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(SYSFilesTools.getSYSFilesRenderer());

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (SwingUtilities.isLeftMouseButton(mouseEvent) && mouseEvent.getClickCount() == 2) {
                    SYSFilesTools.handleFile((SYSFiles) list.getSelectedValue(), Desktop.Action.OPEN);
                }

                //todo: this is much more complicated than it looks. maybe i need some sort of interface attachable
//                else if (SwingUtilities.isRightMouseButton(mouseEvent)) {
//                    JidePopupMenu jMenu = new JidePopupMenu();
//
//                    JMenuItem miDetachFile = new JMenuItem(SYSTools.xx("nursingrecords.files.detach"));
//
//                    jMenu.add(miDetachFile);
//
//                    miDetachFile.addActionListener(new ActionListener() {
//                        @Override
//                        public void actionPerformed(ActionEvent e) {
//
//                            EntityManager em = OPDE.getEMF().createEntityManager();
//                            SYSFiles attachedFile = em.merge((SYSFiles) list.getSelectedValue());
//
//
//
//
//                            em.close();
//
//
//                        }
//                    });
//
//                    jMenu.show(pnlFilesList, 0, pnlFilesList.getPreferredSize().height);
//                }
            }
        });

        pnlFilesList.setBorder(new EmptyBorder(10, 10, 10, 10));
        pnlFilesList.add(new JScrollPane(list));

        return pnlFilesList;
    }

    private ArrayList<SYSFiles> getAttachedFilesList(Attachable attachable) {
        ArrayList<SYSFiles> files = null;
        EntityManager em = OPDE.createEM();
        if (attachable instanceof NReport) {
            Query query = em.createQuery(" SELECT s "
                    + " FROM SYSFiles s "
                    + " JOIN s.nrAssignCollection sf "
                    + " WHERE sf.nReport = :nReport ");
            query.setParameter("nReport", attachable);
            files = new ArrayList<SYSFiles>(query.getResultList());
        } else if (attachable instanceof Prescription) {
            Query query = em.createQuery(" SELECT s "
                    + " FROM SYSFiles s "
                    + " JOIN s.preAssignCollection sf "
                    + " WHERE sf.prescription = :verordnung ");
            query.setParameter("verordnung", attachable);
            files = new ArrayList<SYSFiles>(query.getResultList());
        } else if (attachable instanceof ResInfo) {
            Query query = em.createQuery(" SELECT s "
                    + " FROM SYSFiles s "
                    + " JOIN s.bwiAssignCollection sf "
                    + " WHERE sf.bwinfo = :bwinfo ");
            query.setParameter("bwinfo", attachable);
            files = new ArrayList<SYSFiles>(query.getResultList());
        } else if (attachable instanceof ResValue) {
            Query query = em.createQuery("SELECT s FROM SYSFiles s JOIN s.valAssignCollection sf WHERE sf.value = :resval ");
            query.setParameter("resval", attachable);
            files = new ArrayList<SYSFiles>(query.getResultList());
        } else if (attachable instanceof NursingProcess) {
            Query query = em.createQuery("SELECT s FROM SYSFiles s JOIN s.npAssignCollection sf WHERE sf.nursingProcess = :np ");
            query.setParameter("np", attachable);
            files = new ArrayList<SYSFiles>(query.getResultList());
        } else if (attachable instanceof Users) {
            Query query = em.createQuery("SELECT s FROM SYSFiles s JOIN s.usersAssignCollection uf WHERE uf.user = :user ");
            query.setParameter("user", attachable);
            files = new ArrayList<SYSFiles>(query.getResultList());
        }
        
        Collections.sort(files);

        em.close();
        return files;
    }


    private void btnCancelActionPerformed(ActionEvent e) {
        dispose();
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        btnCancel = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BoxLayout(dialogPane, BoxLayout.X_AXIS));

            //======== contentPanel ========
            {
                contentPanel.setLayout(new FormLayout(
                        "default:grow, $lcgap, default:grow",
                        "fill:default:grow, $lgap, default"));

                //---- btnCancel ----
                btnCancel.setText(null);
                btnCancel.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/player_eject.png")));
                btnCancel.addActionListener(e -> btnCancelActionPerformed(e));
                contentPanel.add(btnCancel, CC.xy(3, 3, CC.RIGHT, CC.DEFAULT));
            }
            dialogPane.add(contentPanel);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(640, 415);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JButton btnCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
