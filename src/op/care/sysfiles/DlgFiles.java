/*
 * Created by JFormDesigner on Thu Jun 21 14:49:00 CEST 2012
 */

package op.care.sysfiles;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.Pflegeberichte;
import entity.files.SYSFiles;
import entity.files.SYSFilesTools;
import entity.info.BWInfo;
import entity.verordnungen.Verordnung;
import op.OPDE;
import op.system.FileDrop;
import op.threads.DisplayMessage;
import op.tools.MyJDialog;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Torsten Löhr
 */
public class DlgFiles extends MyJDialog {
    private Object attachable;
    private boolean filesAttached = false;
    private Closure afterAttachAction;
    private JList list;

    /**
     * Creates a generic file attachment dialog to show existing files and add new ones.
     * @param attachable object to add the files to
     * @param afterAttachAction what to do, after files have been attached. if this is <code>null</code>, file drops are not possible.
     */
    public DlgFiles(Object attachable, Closure afterAttachAction) {
        super();
        this.attachable = attachable;
        this.afterAttachAction = afterAttachAction;
        initComponents();
        initDialog();
        setVisible(true);
    }

    private void initDialog() {

        if (afterAttachAction == null) {
            contentPanel.add(getFileListPanel(), CC.xywh(1, 1, 3, 1));
        } else {
            contentPanel.add(getFileDropPanel(), CC.xy(1, 1));
            contentPanel.add(getFileListPanel(), CC.xy(3, 1));
        }


    }


    private JPanel getFileDropPanel() {
        JPanel dropPanel = new JPanel();
        dropPanel.setLayout(new BorderLayout());
        JLabel dropLabel = new JLabel(OPDE.lang.getString(PnlFiles.internalClassID + ".drophere"), new ImageIcon(Double.class.getResource("/artwork/48x48/kget_dock.png")), SwingConstants.CENTER);
        dropLabel.setFont(SYSConst.ARIAL20);
        dropLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        dropLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        dropPanel.add(BorderLayout.CENTER, dropLabel);
        dropPanel.setPreferredSize(new Dimension(180, 180));
        dropPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        new FileDrop(dropPanel, new FileDrop.Listener() {
            @Override
            public void filesDropped(File[] files) {
                java.util.List<SYSFiles> successful = SYSFilesTools.putFiles(files, attachable);
                if (!successful.isEmpty()) {
                    list.setModel(SYSTools.list2dlm(getAttachedFilesList(attachable)));
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(successful.size() + " " + OPDE.lang.getString("misc.msg.Files") + " " + OPDE.lang.getString("misc.msg.added")));
                    filesAttached = true;
                }
            }
        });
        return dropPanel;
    }

    @Override
    public void dispose() {
        if (filesAttached) {
            afterAttachAction.execute(null);
        }
        super.dispose();
    }

    private JPanel getFileListPanel() {
        list = new JList();
        JPanel pnlFilesList = new JPanel();
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
                if (mouseEvent.getClickCount() == 2) {
                    SYSFilesTools.handleFile((SYSFiles) list.getSelectedValue(), Desktop.Action.OPEN);
                }
            }
        });

        pnlFilesList.setBorder(new EmptyBorder(10, 10, 10, 10));
        pnlFilesList.add(new JScrollPane(list));


        return pnlFilesList;
    }

    private ArrayList<SYSFiles> getAttachedFilesList(Object attachable){
        ArrayList<SYSFiles> files = null;
        EntityManager em = OPDE.createEM();
        if (attachable instanceof Pflegeberichte) {
            Query query = em.createNamedQuery("SYSFiles.findByPB", SYSFiles.class);
            query.setParameter("pflegebericht", attachable);
            files = new ArrayList<SYSFiles>(query.getResultList());
            Collections.sort(files);
        } else if (attachable instanceof Verordnung) {
            Query query = em.createNamedQuery("SYSFiles.findByVerordnung", SYSFiles.class);
            query.setParameter("verordnung", attachable);
            files = new ArrayList<SYSFiles>(query.getResultList());
            Collections.sort(files);
        } else if (attachable instanceof BWInfo) {
            Query query = em.createNamedQuery("SYSFiles.findByBWInfo", SYSFiles.class);
            query.setParameter("bwinfo", attachable);
            files = new ArrayList<SYSFiles>(query.getResultList());
            Collections.sort(files);
        }

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
                btnCancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnCancelActionPerformed(e);
                    }
                });
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
