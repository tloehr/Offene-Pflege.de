/*
 * Created by JFormDesigner on Fri Jun 26 15:31:19 CEST 2015
 */

package op.settings.subpanels;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.files.SYSFilesTools;
import entity.info.ICD;
import gui.GUITools;
import gui.interfaces.DefaultPanel;
import op.OPDE;
import op.settings.ClaMLImporter;
import op.settings.ICDImporter;
import op.threads.DisplayMessage;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.io.FileUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * @author Torsten Löhr
 */
public class PnlICD extends DefaultPanel {
    private DefaultListModel<File> dlmICDFiles;
    private ArrayList<ICD> listICDs;


    public PnlICD() {
        super("opde.settings.icd");
        initComponents();

        dlmICDFiles = new DefaultListModel<>();
        lstIcdFiles.setModel(dlmICDFiles);
        listICDs = new ArrayList<>();

        btnImportICD.setText(SYSTools.xx("opde.settings.icd"));
        btnImportICD.setIcon(SYSConst.icon22ledRedOn);
        btnEmptyList.setToolTipText(SYSTools.xx("opde.settings.icd.btnEmptyList"));

        pnlICD.add(GUITools.getDropPanel(files -> {
            for (File file : files) {
                if (file.isDirectory()) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.nodirectories"));
                    return;
                }
            }

            boolean attached = false;
            for (File file : files) {
                if (!dlmICDFiles.contains(file)) {
                    dlmICDFiles.addElement(file);
                    attached = true;
                }
            }

            if (attached) {
                SwingUtilities.invokeLater(() -> {
                    lstIcdFiles.revalidate();
                    lstIcdFiles.repaint();
                });
            }


            if (dlmICDFiles.size() == 1) {
                File opdeicd = dlmICDFiles.elementAt(0).exists() ? dlmICDFiles.elementAt(0) : null;

                if (!SYSFilesTools.filenameExtension(opdeicd.getPath()).equalsIgnoreCase("xml")) {
//                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.settings.icd.wrongfile", DisplayMessage.WARNING));
                    btnImportICD.setIcon(SYSConst.icon22ledRedOn);
                } else {
                    try {
                        SAXParserFactory factory = SAXParserFactory.newInstance();
                        SAXParser saxParser = factory.newSAXParser();
                        ICDImporter icdImporter = new ICDImporter();
                        saxParser.parse(opdeicd, icdImporter);

                        listICDs.clear();
                        listICDs.addAll(icdImporter.getICDs());

                        btnImportICD.setIcon(SYSConst.icon22ledGreenOn);
                    } catch (Exception e) {
                        OPDE.debug(e.getMessage());
                        e.printStackTrace();
                        OPDE.getDisplayManager().addSubMessage("opde.settings.icd.noICDFile");
                        listICDs.clear();
                        btnImportICD.setIcon(SYSConst.icon22ledRedOn);
                    }
                }
            } else if (dlmICDFiles.size() == 2) {

                File dimdixml = null, dimdidtd = null;

                File file1 = dlmICDFiles.elementAt(0).exists() ? dlmICDFiles.elementAt(0) : null;
                File file2 = dlmICDFiles.elementAt(1).exists() ? dlmICDFiles.elementAt(1) : null;

                if (SYSFilesTools.filenameExtension(file1.getPath()).equalsIgnoreCase("xml")) {
                    dimdixml = file1;
                }
                if (SYSFilesTools.filenameExtension(file2.getPath()).equalsIgnoreCase("xml")) {
                    dimdixml = file2;
                }
                if (SYSFilesTools.filenameExtension(file1.getPath()).equalsIgnoreCase("dtd")) {
                    dimdidtd = file1;
                }
                if (SYSFilesTools.filenameExtension(file2.getPath()).equalsIgnoreCase("dtd")) {
                    dimdidtd = file2;
                }

                if (dimdidtd != null && dimdixml != null) {
                    try {

                        Path tempDir = Files.createTempDirectory("icdimport");
                        FileUtils.copyFileToDirectory(dimdidtd, tempDir.toFile(), false);
                        FileUtils.copyFileToDirectory(dimdixml, tempDir.toFile(), false);
                        File claML = new File(tempDir.toString() + SYSConst.sep + dimdixml.getName());

                        SAXParserFactory factory = SAXParserFactory.newInstance();
                        SAXParser saxParser = factory.newSAXParser();
                        ClaMLImporter cl = new ClaMLImporter();
                        saxParser.parse(claML, cl);

                        listICDs.clear();
                        listICDs.addAll(cl.getICDs());

                        btnImportICD.setIcon(SYSConst.icon22ledGreenOn);

                    } catch (Exception e) {
                        listICDs.clear();
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage(e.getMessage(), DisplayMessage.WARNING));
                        btnImportICD.setIcon(SYSConst.icon22ledRedOn);
                    }
                    //
                } else {
                    OPDE.getDisplayManager().addSubMessage("opde.settings.icd.noICDFile");
                    listICDs.clear();
                    btnImportICD.setIcon(SYSConst.icon22ledRedOn);
                }

            } else {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.settings.icd.only1or2files", DisplayMessage.WARNING));
                btnImportICD.setIcon(SYSConst.icon22ledRedOn);
                listICDs.clear();
            }
        }, SYSTools.xx("opde.settings.icd.dropICDHere")), CC.xyw(1, 1, 2));


    }

    @Override
    public void cleanup() {
        super.cleanup();
        dlmICDFiles.clear();
        listICDs.clear();
    }

    private void btnEmptyListActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            dlmICDFiles.clear();
            listICDs.clear();
            btnImportICD.setIcon(SYSConst.icon22ledRedOn);
            lstIcdFiles.revalidate();
            lstIcdFiles.repaint();
        });
    }

    private void btnImportICDActionPerformed(ActionEvent e) {
        if (listICDs.isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.settings.icd.noICDFile", DisplayMessage.WARNING));
            return;
        }

        OPDE.getMainframe().setBlocked(true);
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), -1, listICDs.size()));

        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                EntityManager em = OPDE.createEM();

                try {
                    int progress = 0;

                    em.getTransaction().begin();
                    Query query = em.createQuery("DELETE FROM ICD icd");
                    query.executeUpdate();

                    for (ICD icd : listICDs) {
                        progress++;
                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, listICDs.size()));
                        em.merge(icd);
                    }
                    OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait.for.db") + " " + listICDs.size() + " ICDs.", progress, -1));
                    em.getTransaction().commit();
                } catch (Exception ex) {
                    em.getTransaction().rollback();
                    OPDE.fatal(ex);
                } finally {
                    em.close();
                }

                return null;
            }

            @Override
            protected void done() {
                listICDs.clear();
                dlmICDFiles.clear();
                lstIcdFiles.revalidate();
                lstIcdFiles.repaint();
                btnImportICD.setIcon(SYSConst.icon22ledRedOn);
                OPDE.getDisplayManager().setProgressBarMessage(null);
                OPDE.getMainframe().setBlocked(false);
            }
        };
        worker.execute();
    }

    @Override
    public String getHelpKey() {
        return SYSTools.xx("opde.settings.icd.helpurl");
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        pnlICD = new JPanel();
        scrollPane1 = new JScrollPane();
        lstIcdFiles = new JList();
        btnEmptyList = new JButton();
        btnImportICD = new JButton();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== pnlICD ========
        {
            pnlICD.setLayout(new FormLayout(
                "default:grow, default",
                "fill:default:grow, $lgap, pref, $lgap, default"));

            //======== scrollPane1 ========
            {
                scrollPane1.setViewportView(lstIcdFiles);
            }
            pnlICD.add(scrollPane1, CC.xy(1, 3));

            //---- btnEmptyList ----
            btnEmptyList.setText(null);
            btnEmptyList.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/deleteall.png")));
            btnEmptyList.addActionListener(e -> btnEmptyListActionPerformed(e));
            pnlICD.add(btnEmptyList, CC.xy(2, 3, CC.DEFAULT, CC.FILL));

            //---- btnImportICD ----
            btnImportICD.setText("importICD");
            btnImportICD.addActionListener(e -> btnImportICDActionPerformed(e));
            pnlICD.add(btnImportICD, CC.xy(1, 5, CC.LEFT, CC.DEFAULT));
        }
        add(pnlICD);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel pnlICD;
    private JScrollPane scrollPane1;
    private JList lstIcdFiles;
    private JButton btnEmptyList;
    private JButton btnImportICD;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
