/*
 * Created by JFormDesigner on Thu Dec 20 15:01:22 CET 2012
 */

package op.settings;

import com.enterprisedt.net.ftp.FileTransferClient;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import entity.Homes;
import entity.HomesTools;
import entity.Station;
import entity.StationTools;
import entity.files.SYSFilesTools;
import entity.info.ICD;
import entity.info.ResInfoCategory;
import entity.info.ResInfoCategoryTools;
import entity.prescription.MedStock;
import entity.system.Commontags;
import entity.system.CommontagsTools;
import entity.system.SYSPropsTools;
import op.OPDE;
import op.system.*;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.apache.commons.io.FileUtils;
import org.jdesktop.swingx.VerticalLayout;

import javax.persistence.*;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * @author Torsten Löhr
 */
public class PnlSystemSettings extends CleanablePanel {
    private final int TAB_LOCAL = 0;
    private final int TAB_GLOBAL = 1;
    public static final String internalClassID = "opde.settings";
    private MedStock testStock;
    private HashMap<String, CollapsiblePane> cpMap;
    private HashMap<String, JPanel> cpPanel;
    private ArrayList<Homes> listHomes;
    private JToggleButton tbauth, tbtls, tbstarttls, tbactive, tbCalcMed;
    private DefaultListModel<File> dlmICDFiles;
    private ArrayList<ICD> listICDs;
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;

    public PnlSystemSettings(JScrollPane jspSearch) {
        this.jspSearch = jspSearch;
        cpMap = new HashMap<String, CollapsiblePane>();
        cpPanel = new HashMap<String, JPanel>();
        listICDs = new ArrayList<>();
        initComponents();
        initPanel();
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

        searchPane.setContentPane(mypanel);

        searchPanes.add(searchPane);
        searchPanes.addExpansion();

    }


    private java.util.List<Component> addCommands() {

        java.util.List<Component> list = new ArrayList<Component>();


        final JideButton btnCommontags = GUITools.createHyperlinkButton(SYSTools.xx("opde.settings.global.btnCommontags"), SYSConst.icon22add, null);
        btnCommontags.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
//                ArrayList list = new ArrayList();
                final JPanel selPanel = new JPanel();
                selPanel.setLayout(new BoxLayout(selPanel, BoxLayout.PAGE_AXIS));

                for (final Commontags tags : CommontagsTools.getAll()) {
                    if (tags.getType() == CommontagsTools.TYPE_SYS_USER) {
                        final JPanel pnl = new JPanel(new BorderLayout());
                        pnl.add(new JLabel(tags.getText()), BorderLayout.CENTER);
                        JButton btn = new JButton(SYSConst.icon16delete);
                        btn.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent ae) {

                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();


                                    Commontags myTag = em.merge(tags);

                                    em.remove(myTag);

                                    em.getTransaction().commit();

                                    SwingUtilities.invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            selPanel.remove(pnl);
                                            selPanel.revalidate();
                                            selPanel.repaint();
                                        }
                                    });


                                } catch (OptimisticLockException ole) {
                                    OPDE.warn(ole);
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                    if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                                        OPDE.getMainframe().emptyFrame();
                                        OPDE.getMainframe().afterLogin();
                                    }
                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                                } catch (Exception e) {
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                    OPDE.fatal(e);
                                } finally {
                                    em.close();
                                }


                            }
                        });
                        pnl.add(btn, BorderLayout.EAST);

                        selPanel.add(pnl);
                    }
                }

                final MyJDialog dlg = new MyJDialog(false);

                JButton btnClose = new JButton(SYSConst.icon16cancel);
                btnClose.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dlg.dispose();
                    }
                });

                selPanel.add(btnClose, BorderLayout.SOUTH);

                dlg.setContentPane(new JScrollPane(selPanel));
                dlg.pack();
                dlg.setSize(new Dimension(selPanel.getPreferredSize().width, 450));
                dlg.setVisible(true);
            }
        });
        list.add(btnCommontags);


        return list;
    }

    private void btnTestLabelActionPerformed(ActionEvent e) {

        try {
            EntityManager em = OPDE.createEM();
            Query query = em.createNativeQuery("SELECT BestID FROM medstock ORDER BY RAND() LIMIT 0,1");
            if (!query.getResultList().isEmpty()) {
                testStock = em.find(MedStock.class, ((BigInteger) query.getResultList().get(0)).longValue());
            }
            em.close();
        } catch (Exception e1) {
            testStock = null;

        }
        if (testStock == null) return;

        LogicalPrinter localPrinter = OPDE.getLogicalPrinters().getMapName2LogicalPrinter().get(OPDE.getProps().getProperty(SYSPropsTools.KEY_LOGICAL_PRINTER));
        PrinterForm printerForm1 = localPrinter.getForms().get(OPDE.getProps().getProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL));

        OPDE.getPrintProcessor().addPrintJob(new PrintListElement(testStock, localPrinter, printerForm1, OPDE.getProps().getProperty(SYSPropsTools.KEY_PHYSICAL_PRINTER)));
    }

    private void cmbStationItemStateChanged(ItemEvent e) {
        if (cmbStation.getSelectedItem() == null) return;
        OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_STATION, ((Station) cmbStation.getSelectedItem()).getStatID().toString());
        OPDE.saveLocalProps();
    }

    private void cmbPhysicalPrintersItemStateChanged(ItemEvent e) {

    }

    private void cmbLogicalPrintersItemStateChanged(ItemEvent e) {
        LogicalPrinter logicalPrinter = (LogicalPrinter) cmbLogicalPrinters.getSelectedItem();
        if (logicalPrinter != null) {
            OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_LOGICAL_PRINTER, logicalPrinter.getName());
            OPDE.getProps().setProperty(SYSPropsTools.KEY_LOGICAL_PRINTER, logicalPrinter.getName());
        }
        OPDE.saveLocalProps();
    }

    private void cmbFormItemStateChanged(ItemEvent e) {
        PrinterForm form = (PrinterForm) cmbForm.getSelectedItem();
        if (form != null) {
            OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL, ((PrinterForm) cmbForm.getSelectedItem()).getName());
            OPDE.getProps().setProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL, ((PrinterForm) cmbForm.getSelectedItem()).getName());
        }
        OPDE.saveLocalProps();
    }

    private void tabMainStateChanged(ChangeEvent e) {
        if (tabMain.getSelectedIndex() == TAB_GLOBAL) {
            initGlobal();
        } else {
            initLocal();

        }
    }

    private void initLocal() {
        testStock = null;

        cmbPhysicalPrinters.setModel(new DefaultComboBoxModel());
        cmbForm.setModel(new DefaultComboBoxModel());
        cmbLogicalPrinters.setModel(new DefaultComboBoxModel());

        jspSearch.setViewportView(new JPanel());

        PrintService[] prservices = PrintServiceLookup.lookupPrintServices(null, null);

        // this prevents exceptions when there are no printers installed on the OS yet
        if (prservices != null && prservices.length == 0) {
            prservices = null;
        }

        if (prservices != null) {
            cmbPhysicalPrinters.setModel(new DefaultComboBoxModel(prservices));
            cmbPhysicalPrinters.setRenderer(new ListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                    if (o == null)
                        return new DefaultListCellRenderer().getListCellRendererComponent(jList, SYSTools.xx("misc.msg.error"), i, isSelected, cellHasFocus);
                    return new DefaultListCellRenderer().getListCellRendererComponent(jList, ((PrintService) o).getName(), i, isSelected, cellHasFocus);
                }
            });

            cmbLogicalPrinters.setRenderer(new ListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                    if (o == null)
                        return new DefaultListCellRenderer().getListCellRendererComponent(jList, SYSTools.xx("misc.msg.error"), i, isSelected, cellHasFocus);
                    return new DefaultListCellRenderer().getListCellRendererComponent(jList, ((LogicalPrinter) o).getLabel(), i, isSelected, cellHasFocus);
                }
            });
            cmbForm.setRenderer(new ListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                    if (o == null)
                        return new DefaultListCellRenderer().getListCellRendererComponent(jList, SYSTools.xx("misc.msg.error"), i, isSelected, cellHasFocus);
                    return new DefaultListCellRenderer().getListCellRendererComponent(jList, ((PrinterForm) o).getLabel(), i, isSelected, cellHasFocus);
                }
            });

            cmbPhysicalPrinters.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        PrintService printService = (PrintService) cmbPhysicalPrinters.getSelectedItem();
                        OPDE.getProps().setProperty(SYSPropsTools.KEY_PHYSICAL_PRINTER, printService.getName());
                        OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_PHYSICAL_PRINTER, printService.getName());
                        OPDE.saveLocalProps();
                    }

                }
            });

            cmbLogicalPrinters.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        LogicalPrinter logicalPrinter = (LogicalPrinter) cmbLogicalPrinters.getSelectedItem();
                        cmbForm.setModel(new DefaultComboBoxModel(logicalPrinter.getForms().values().toArray()));
                        if (OPDE.getProps().containsKey(SYSPropsTools.KEY_MEDSTOCK_LABEL) && logicalPrinter.getForms().containsKey(OPDE.getProps().getProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL))) {
                            cmbForm.setSelectedItem(logicalPrinter.getForms().get(OPDE.getProps().getProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL)));
                        }
                        OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_LOGICAL_PRINTER, logicalPrinter.getName());
                        OPDE.getProps().setProperty(SYSPropsTools.KEY_LOGICAL_PRINTER, logicalPrinter.getName());
                        OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL, ((PrinterForm) cmbForm.getSelectedItem()).getName());
                        OPDE.getProps().setProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL, ((PrinterForm) cmbForm.getSelectedItem()).getName());

                    }
                }
            });

            cmbForm.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        OPDE.getProps().setProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL, ((PrinterForm) cmbForm.getSelectedItem()).getName());
                        OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL, ((PrinterForm) cmbForm.getSelectedItem()).getName());
                    }

                }
            });

            if (OPDE.getPrintProcessor().isWorking()) {
                cmbLogicalPrinters.setModel(new DefaultComboBoxModel(OPDE.getLogicalPrinters().getLogicalPrintersList().toArray()));
                LogicalPrinter logicalPrinter = OPDE.getLogicalPrinters().getMapName2LogicalPrinter().get(OPDE.getProps().getProperty(SYSPropsTools.KEY_LOGICAL_PRINTER));
                cmbLogicalPrinters.setSelectedItem(logicalPrinter);

                cmbForm.setModel(new DefaultComboBoxModel(logicalPrinter.getForms().values().toArray()));
                cmbForm.setSelectedItem(logicalPrinter.getForms().get(OPDE.getProps().getProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL)));
            }
            if (OPDE.getProps().containsKey(SYSPropsTools.KEY_PHYSICAL_PRINTER) && OPDE.getLogicalPrinters().getPrintService(OPDE.getProps().getProperty(SYSPropsTools.KEY_PHYSICAL_PRINTER)) != null) {
                cmbPhysicalPrinters.setSelectedItem(OPDE.getLogicalPrinters().getPrintService(OPDE.getProps().getProperty(SYSPropsTools.KEY_PHYSICAL_PRINTER)));
            } else {
                PrintService printService = (PrintService) cmbPhysicalPrinters.getSelectedItem();
                OPDE.getProps().setProperty(SYSPropsTools.KEY_PHYSICAL_PRINTER, printService.getName());
                OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_PHYSICAL_PRINTER, printService.getName());

            }

            if (!OPDE.getLogicalPrinters().getLogicalPrintersList().isEmpty()) {
                cmbLogicalPrinters.setModel(new DefaultComboBoxModel(OPDE.getLogicalPrinters().getLogicalPrintersList().toArray()));
                LogicalPrinter logicalPrinter = OPDE.getLogicalPrinters().getMapName2LogicalPrinter().get(OPDE.getProps().getProperty(SYSPropsTools.KEY_LOGICAL_PRINTER));
                if (logicalPrinter == null) logicalPrinter = OPDE.getLogicalPrinters().getLogicalPrintersList().get(0);

                if (!OPDE.getProps().containsKey(SYSPropsTools.KEY_LOGICAL_PRINTER)) {
                    OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_LOGICAL_PRINTER, logicalPrinter.getName());
                    OPDE.getProps().setProperty(SYSPropsTools.KEY_LOGICAL_PRINTER, logicalPrinter.getName());

                }

                cmbLogicalPrinters.setSelectedItem(logicalPrinter);

                cmbForm.setModel(new DefaultComboBoxModel(logicalPrinter.getForms().values().toArray()));
                if (OPDE.getProps().containsKey(SYSPropsTools.KEY_MEDSTOCK_LABEL) && logicalPrinter.getForms().containsKey(OPDE.getProps().getProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL))) {
                    cmbForm.setSelectedItem(logicalPrinter.getForms().get(OPDE.getProps().getProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL)));
                } else {
                    cmbForm.setSelectedIndex(0);
                    OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL, ((PrinterForm) cmbForm.getSelectedItem()).getName());
                    OPDE.getProps().setProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL, ((PrinterForm) cmbForm.getSelectedItem()).getName());

                }
            }
        } else {
            cmbPhysicalPrinters.setEnabled(false);
            cmbLogicalPrinters.setEnabled(false);
            cmbPhysicalPrinters.setEnabled(false);
        }

        btnTestLabel.setEnabled(prservices != null);
        cmbForm.setEnabled(prservices != null);
        cmbLogicalPrinters.setEnabled(prservices != null);
        cmbPhysicalPrinters.setEnabled(prservices != null);

        lblPrinters.setText(SYSTools.xx("opde.settings.local.labelPrinters"));
        lblStation.setText(SYSTools.xx("opde.settings.local.station"));
        lblTimeout.setText(SYSTools.xx("opde.settings.local.timeout"));

        SpinnerNumberModel snm = new SpinnerNumberModel(OPDE.getTimeout(), 0, 999, 1);
        final JSpinner spinTimeout = new JSpinner(snm);

        spinTimeout.setToolTipText(SYSTools.xx("opde.settings.local.timeout.tooltip"));
        spinTimeout.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                OPDE.setTimeout((Integer) spinTimeout.getValue());
            }
        });
        pnlLocal.add(spinTimeout, CC.xy(7, 9));

        cmbStation.setModel(StationTools.getAll4Combobox(false));
        cmbStation.setSelectedItem(StationTools.getStationForThisHost());
        OPDE.saveLocalProps();
    }

    private void initGlobal() {
        createHomesList();
        createCountryList();
        createCatList();
        createICDImporter();
        createMailSystem();
        createFTPSystem();
        createCalcMed();

        prepareSearchArea();

    }

    private void createCatList() {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT b FROM ResInfoCategory b ORDER BY b.text ");
        java.util.List<ResInfoCategory> listCats = query.getResultList();
        em.close();
        lstCat.setModel(SYSTools.list2dlm(listCats));

    }

    private void createICDImporter() {
        btnImportICD.setText(SYSTools.xx("opde.settings.global.btnImportICD"));
        btnImportICD.setIcon(SYSConst.icon22ledRedOn);
        btnEmptyList.setToolTipText(SYSTools.xx("opde.settings.global.btnEmptyList"));
        pnlICD.add(GUITools.getDropPanel(new FileDrop.Listener() {
            public void filesDropped(java.io.File[] files) {
//                File opdeicd = null, dimdixml = null, dimdidtd = null;

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
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            lstIcdFiles.revalidate();
                            lstIcdFiles.repaint();
                        }
                    });
                }


                if (dlmICDFiles.size() == 1) {
                    File opdeicd = dlmICDFiles.elementAt(0).exists() ? dlmICDFiles.elementAt(0) : null;

                    if (!SYSFilesTools.filenameExtension(opdeicd.getPath()).equalsIgnoreCase("xml")) {
                        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.settings.global.wrongfile", DisplayMessage.WARNING));
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
                            OPDE.getDisplayManager().addSubMessage("opde.settings.global.noICDFile");
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
                        OPDE.getDisplayManager().addSubMessage("opde.settings.global.noICDFile");
                        listICDs.clear();
                        btnImportICD.setIcon(SYSConst.icon22ledRedOn);
                    }

                } else {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.settings.global.only1or2files", DisplayMessage.WARNING));
                    btnImportICD.setIcon(SYSConst.icon22ledRedOn);
                    listICDs.clear();
                }
            }
        }, SYSTools.xx("opde.settings.global.dropICDHere")), CC.xyw(1, 1, 2));
    }


    private void createMailSystem() {
        btnTestmail.setText(SYSTools.xx("opde.settings.global.mail.btnTestmail"));

        txtMailHost.setText(SYSTools.catchNull(OPDE.getProps().getProperty(SYSPropsTools.KEY_MAIL_HOST)));
        txtMailPort.setText(SYSTools.catchNull(OPDE.getProps().getProperty(SYSPropsTools.KEY_MAIL_PORT), "25"));
        txtMailUser.setText(SYSTools.catchNull(OPDE.getProps().getProperty(SYSPropsTools.KEY_MAIL_USER)));
        txtMailPassword.setText(SYSTools.catchNull(OPDE.getProps().getProperty(SYSPropsTools.KEY_MAIL_PASSWORD)));
        txtMailSender.setText(SYSTools.catchNull(OPDE.getProps().getProperty(SYSPropsTools.KEY_MAIL_SENDER)));
        txtMailRecipient.setText(SYSTools.catchNull(OPDE.getProps().getProperty(SYSPropsTools.KEY_MAIL_RECIPIENT)));
        txtMailSenderPersonal.setText(SYSTools.catchNull(OPDE.getProps().getProperty(SYSPropsTools.KEY_MAIL_SENDER_PERSONAL)));
        txtMailRecipientPersonal.setText(SYSTools.catchNull(OPDE.getProps().getProperty(SYSPropsTools.KEY_MAIL_RECIPIENT_PERSONAL)));
        txtMailSpamfilter.setText(SYSTools.catchNull(OPDE.getProps().getProperty(SYSPropsTools.KEY_MAIL_SPAMFILTER_KEY)));

        lblMailHost.setText(SYSTools.xx("opde.settings.global.mail.host"));
        lblMailPort.setText(SYSTools.xx("opde.settings.global.mail.port"));
        lblMailUser.setText(SYSTools.xx("opde.settings.global.mail.user"));
        lblMailPassword.setText(SYSTools.xx("opde.settings.global.mail.password"));
        lblMailSender.setText(SYSTools.xx("opde.settings.global.mail.sender"));
        lblMailRecipient.setText(SYSTools.xx("opde.settings.global.mail.recipient"));
        lblMailSenderPersonal.setText(SYSTools.xx("opde.settings.global.mail.sender.personal"));
        lblMailRecipientPersonal.setText(SYSTools.xx("opde.settings.global.mail.recipient.personal"));
        lblMailSpamFilter.setText(SYSTools.xx("opde.settings.global.mail.recipient.spamfilter"));

        lblAuth.setText(SYSTools.xx("opde.settings.global.mail.auth"));
        tbauth = GUITools.getNiceToggleButton(null);
        tbauth.setSelected(SYSTools.catchNull(OPDE.getProps().getProperty(SYSPropsTools.KEY_MAIL_AUTH)).equalsIgnoreCase("true"));
        pnlMail.add(tbauth, CC.xywh(3, 19, 1, 1, CC.LEFT, CC.DEFAULT));

        lblStarttls.setText(SYSTools.xx("opde.settings.global.mail.starttls"));
        tbstarttls = GUITools.getNiceToggleButton(null);
        tbstarttls.setSelected(SYSTools.catchNull(OPDE.getProps().getProperty(SYSPropsTools.KEY_MAIL_STARTTLS)).equalsIgnoreCase("true"));
        pnlMail.add(tbstarttls, CC.xywh(3, 21, 1, 1, CC.LEFT, CC.DEFAULT));

        lblTLS.setText(SYSTools.xx("opde.settings.global.mail.tls"));
        tbtls = GUITools.getNiceToggleButton(null);
        tbtls.setSelected(SYSTools.catchNull(OPDE.getProps().getProperty(SYSPropsTools.KEY_MAIL_TLS)).equalsIgnoreCase("true"));
        pnlMail.add(tbtls, CC.xywh(3, 23, 1, 1, CC.LEFT, CC.DEFAULT));

        lblActive.setText(SYSTools.xx("opde.settings.global.mail.active"));
        tbactive = GUITools.getNiceToggleButton(null);
        tbactive.setSelected(SYSTools.catchNull(OPDE.getProps().getProperty(SYSPropsTools.KEY_MAIL_SYSTEM_ACTIVE)).equalsIgnoreCase("true"));
        pnlMail.add(tbactive, CC.xywh(3, 27, 1, 1, CC.LEFT, CC.DEFAULT));
        tbactive.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                SYSPropsTools.storeProp(SYSPropsTools.KEY_MAIL_SYSTEM_ACTIVE, Boolean.toString(tbactive.isSelected()));
                if (e.getStateChange() == ItemEvent.SELECTED) {

                    for (Map.Entry entry : getMailProps().entrySet()) {
                        SYSPropsTools.storeProp(entry.getKey().toString(), entry.getValue().toString());
                    }

                }
            }
        });
    }

    private void createCountryList() {

        String[] countries = new String[]{"germany", "austria"};
        cmbCountry.setModel(SYSTools.list2cmb(Arrays.asList(countries)));
        cmbCountry.setRenderer(new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                String text = SYSTools.xx("country." + value.toString());
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, index, isSelected, cellHasFocus);
            }
        });

        cmbCountry.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    SYSPropsTools.storeProp(SYSPropsTools.KEY_COUNTRY, e.getItem().toString());
                }
            }
        });


        if (OPDE.getProps().containsKey(SYSPropsTools.KEY_COUNTRY)) {
            cmbCountry.setSelectedItem(OPDE.getProps().getProperty(SYSPropsTools.KEY_COUNTRY));
        } else {
            cmbCountry.setSelectedItem("germany");
            SYSPropsTools.storeProp(SYSPropsTools.KEY_COUNTRY, "germany");
        }

    }

    private void createCalcMed() {
        lblCalcMed.setText(SYSTools.xx("opde.settings.global.calcmed"));
        tbCalcMed = GUITools.getNiceToggleButton("opde.settings.global.tbCalcMed");
        tbCalcMed.setSelected(SYSTools.catchNull(OPDE.getProps().getProperty(SYSPropsTools.KEY_CALC_MEDI_UPR1)).equalsIgnoreCase("true"));
        pnlCalcMed.add(tbCalcMed, CC.xy(1, 1));
        tbCalcMed.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                SYSPropsTools.storeProp(SYSPropsTools.KEY_CALC_MEDI_UPR1, Boolean.toString(tbCalcMed.isSelected()));
            }
        });
    }

    private void createFTPSystem() {
        btnFTPTest.setText(SYSTools.xx("opde.settings.global.mail.btnFTPTest"));

        txtFTPServer.setText(SYSTools.catchNull(OPDE.getProps().getProperty(SYSPropsTools.KEY_FTP_SERVER)));
        txtFTPPort.setText(SYSTools.catchNull(OPDE.getProps().getProperty(SYSPropsTools.KEY_FTP_PORT), "21"));
        txtFTPUser.setText(SYSTools.catchNull(OPDE.getProps().getProperty(SYSPropsTools.KEY_FTP_USER)));
        txtFTPPassword.setText(SYSTools.catchNull(OPDE.getProps().getProperty(SYSPropsTools.KEY_FTP_PASSWORD)));
        txtFTPWorkingDir.setText(SYSTools.catchNull(OPDE.getProps().getProperty(SYSPropsTools.KEY_FTP_WD)));

        lblFTP.setText(SYSTools.xx("opde.settings.global.ftp"));
        lblFTPServer.setText(SYSTools.xx("opde.settings.global.ftp.server"));
        lblFTPPort.setText(SYSTools.xx("opde.settings.global.ftp.port"));
        lblFTPUser.setText(SYSTools.xx("opde.settings.global.ftp.user"));
        lblFTPPassword.setText(SYSTools.xx("opde.settings.global.ftp.password"));
        lblFTPWD.setText(SYSTools.xx("opde.settings.global.ftp.wd"));

    }

    private void createHomesList() {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT e FROM Homes e ORDER BY e.eid");
        listHomes = new ArrayList(query.getResultList());
        em.close();
        cpsHomes.removeAll();
        cpsHomes.setLayout(new JideBoxLayout(cpsHomes, JideBoxLayout.Y_AXIS));
        final JideButton btnAddHome = GUITools.createHyperlinkButton("opde.settings.btnAddHome", SYSConst.icon22add, null);
        btnAddHome.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final PnlHomes pnlHomes = new PnlHomes(new Homes(UUID.randomUUID().toString().substring(0, 15)));
                JidePopup popup = GUITools.createPanelPopup(pnlHomes, new Closure() {
                    @Override
                    public void execute(Object o) {
                        if (o != null) {
                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();
                                Homes home = em.merge((Homes) o);
                                em.getTransaction().commit();
                                createHomesList();
                                OPDE.getMainframe().emptySearchArea();
                                OPDE.getMainframe().prepareSearchArea();
                            } catch (Exception e) {
                                em.getTransaction().rollback();
                                OPDE.fatal(e);
                            } finally {
                                em.close();
                            }
                        }
                    }
                }, btnAddHome);
                GUITools.showPopup(popup, SwingConstants.EAST);
                pnlHomes.setStartFocus();
            }
        });
        cpsHomes.add(btnAddHome);
        for (final Homes home : listHomes) {
            JPanel pnlContentH = new JPanel(new VerticalLayout());
            final JideButton btnAddStation = GUITools.createHyperlinkButton("opde.settings.btnAddStation", SYSConst.icon22add, null);
            btnAddStation.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JidePopup popup = GUITools.getTextEditor(null, 1, 40, new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null && !o.toString().trim().isEmpty()) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    em.merge(new Station(o.toString(), em.merge(home)));
                                    em.getTransaction().commit();
                                    createHomesList();
                                    OPDE.getMainframe().emptySearchArea();
                                    OPDE.getMainframe().prepareSearchArea();
                                } catch (Exception e) {
                                    em.getTransaction().rollback();
                                    OPDE.fatal(e);
                                } finally {
                                    em.close();
                                }
                            }
                        }
                    }, btnAddStation);
                    GUITools.showPopup(popup, SwingConstants.EAST);
                }
            });


            pnlContentH.add(btnAddStation);

            Collections.sort(home.getStations());

            for (final Station station : home.getStations()) {
                String titleS = "<html><font size=+1>" + station.getName() + "</font></html>";
                DefaultCPTitle cpTitleS = new DefaultCPTitle(titleS, null);

                /***
                 *               _ _ _         _        _   _
                 *       ___  __| (_) |_   ___| |_ __ _| |_(_) ___  _ __
                 *      / _ \/ _` | | __| / __| __/ _` | __| |/ _ \| '_ \
                 *     |  __/ (_| | | |_  \__ \ || (_| | |_| | (_) | | | |
                 *      \___|\__,_|_|\__| |___/\__\__,_|\__|_|\___/|_| |_|
                 *
                 */
                final JButton btnEditStation = new JButton(SYSConst.icon22edit);
                btnEditStation.setPressedIcon(SYSConst.icon22Pressed);
                btnEditStation.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnEditStation.setContentAreaFilled(false);
                btnEditStation.setBorder(null);

                btnEditStation.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        final JidePopup popup = GUITools.getTextEditor(station.getName(), 1, 40, new Closure() {
                            @Override
                            public void execute(Object o) {
                                if (o != null && !o.toString().trim().isEmpty()) {
                                    EntityManager em = OPDE.createEM();
                                    try {
                                        em.getTransaction().begin();
                                        Station myStation = em.merge(station);
                                        myStation.setName(o.toString().trim());
                                        em.getTransaction().commit();
                                        createHomesList();
                                        OPDE.getMainframe().emptySearchArea();
                                        OPDE.getMainframe().prepareSearchArea();
                                    } catch (Exception e) {
                                        em.getTransaction().rollback();
                                        OPDE.fatal(e);
                                    } finally {
                                        em.close();
                                    }
                                }
                            }
                        }, btnEditStation);
                        GUITools.showPopup(popup, SwingConstants.EAST);
                    }
                });

                cpTitleS.getRight().add(btnEditStation);


                if (station.getResidents().isEmpty()) {
                    /***
                     *          _      _      _             _        _   _
                     *       __| | ___| | ___| |_ ___   ___| |_ __ _| |_(_) ___  _ __
                     *      / _` |/ _ \ |/ _ \ __/ _ \ / __| __/ _` | __| |/ _ \| '_ \
                     *     | (_| |  __/ |  __/ ||  __/ \__ \ || (_| | |_| | (_) | | | |
                     *      \__,_|\___|_|\___|\__\___| |___/\__\__,_|\__|_|\___/|_| |_|
                     *
                     */
                    final JButton btnDeleteStation = new JButton(SYSConst.icon22delete);
                    btnDeleteStation.setPressedIcon(SYSConst.icon22Pressed);
                    btnDeleteStation.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    btnDeleteStation.setContentAreaFilled(false);
                    btnDeleteStation.setBorder(null);

                    btnDeleteStation.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            new DlgYesNo(SYSTools.xx("misc.questions.delete1") + "<br/><i>" + station.getName() + "</i><br/>" + SYSTools.xx("misc.questions.delete2"), SYSConst.icon48delete, new Closure() {
                                @Override
                                public void execute(Object answer) {
                                    if (answer.equals(JOptionPane.YES_OPTION)) {
                                        EntityManager em = OPDE.createEM();
                                        try {
                                            em.getTransaction().begin();
                                            Station myStation = em.merge(station);
                                            em.lock(myStation, LockModeType.OPTIMISTIC);
                                            em.remove(myStation);
                                            em.getTransaction().commit();
                                            createHomesList();
                                            OPDE.getMainframe().emptySearchArea();
                                            OPDE.getMainframe().prepareSearchArea();
                                        } catch (RollbackException ole) {
                                            if (em.getTransaction().isActive()) {
                                                em.getTransaction().rollback();
                                            }
                                            if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                                                OPDE.getMainframe().completeRefresh();
                                            }
                                            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
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
                            });
                        }
                    });
                    cpTitleS.getRight().add(btnDeleteStation);
                }

                pnlContentH.add(cpTitleS.getMain());

            }
            String titleH = "<html><font size=+1><b>" + home.getName() + "</b></font></html>";
            DefaultCPTitle cpTitleH = new DefaultCPTitle(titleH, null);

            CollapsiblePane cpH = new CollapsiblePane();
            cpH.setSlidingDirection(SwingConstants.SOUTH);
            cpH.setHorizontalAlignment(SwingConstants.LEADING);
            cpH.setOpaque(false);
            cpH.setTitleLabelComponent(cpTitleH.getMain());

            /***
             *               _ _ _     _
             *       ___  __| (_) |_  | |__   ___  _ __ ___   ___
             *      / _ \/ _` | | __| | '_ \ / _ \| '_ ` _ \ / _ \
             *     |  __/ (_| | | |_  | | | | (_) | | | | | |  __/
             *      \___|\__,_|_|\__| |_| |_|\___/|_| |_| |_|\___|
             *
             */
            final JButton btnEditHome = new JButton(SYSConst.icon22edit);
            btnEditHome.setPressedIcon(SYSConst.icon22Pressed);
            btnEditHome.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnEditHome.setContentAreaFilled(false);
            btnEditHome.setBorder(null);
            btnEditHome.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    final PnlHomes pnlHomes = new PnlHomes(home);
                    GUITools.showPopup(GUITools.createPanelPopup(pnlHomes, new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    Homes myHome = em.merge((Homes) o);
                                    em.getTransaction().commit();
                                    createHomesList();
                                    OPDE.getMainframe().emptySearchArea();
                                    OPDE.getMainframe().prepareSearchArea();
                                } catch (Exception e) {
                                    em.getTransaction().rollback();
                                    OPDE.fatal(e);
                                } finally {
                                    em.close();
                                }
                            }
                        }
                    }, btnEditHome), SwingConstants.SOUTH_WEST);

                }
            });
            cpTitleH.getRight().add(btnEditHome);

            if (home.getStations().isEmpty()) {
                final JButton btnDeleteHome = new JButton(SYSConst.icon22delete);
                btnDeleteHome.setPressedIcon(SYSConst.icon22Pressed);
                btnDeleteHome.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnDeleteHome.setContentAreaFilled(false);
                btnDeleteHome.setBorder(null);

                btnDeleteHome.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new DlgYesNo(SYSTools.xx("misc.questions.delete1") + "<br/><i>" + HomesTools.getAsText(home) + "</i><br/>" + SYSTools.xx("misc.questions.delete2"), SYSConst.icon48delete, new Closure() {
                            @Override
                            public void execute(Object answer) {
                                if (answer.equals(JOptionPane.YES_OPTION)) {
                                    EntityManager em = OPDE.createEM();
                                    try {
                                        em.getTransaction().begin();
                                        Homes myHome = em.merge(home);
                                        em.lock(myHome, LockModeType.OPTIMISTIC);
                                        em.remove(myHome);
                                        em.getTransaction().commit();
                                        createHomesList();
                                        OPDE.getMainframe().emptySearchArea();
                                        OPDE.getMainframe().prepareSearchArea();
                                    } catch (RollbackException ole) {
                                        if (em.getTransaction().isActive()) {
                                            em.getTransaction().rollback();
                                        }
                                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                                            OPDE.getMainframe().completeRefresh();
                                        }
                                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
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
                        });
                    }
                });
                cpTitleH.getRight().add(btnDeleteHome);
            }

            cpH.setContentPane(pnlContentH);
            cpsHomes.add(cpH);

        }
        cpsHomes.addExpansion();
    }

    private void lstCatMousePressed(MouseEvent e) {
        if (e.getClickCount() == 2) {
            final PnlCats pnlCats = new PnlCats((ResInfoCategory) lstCat.getSelectedValue());
            GUITools.showPopup(GUITools.createPanelPopup(pnlCats, new Closure() {
                @Override
                public void execute(Object o) {
                    if (o != null) {
                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();
                            ResInfoCategory myCat = em.merge((ResInfoCategory) o);
                            em.getTransaction().commit();
                            createCatList();
                        } catch (Exception e) {
                            em.getTransaction().rollback();
                            OPDE.fatal(e);
                        } finally {
                            em.close();
                        }
                    }
                }
            }, lblCat), SwingConstants.NORTH_EAST);
        }
    }

    private void btnAddCatActionPerformed(ActionEvent e) {
        final PnlCats pnlCats = new PnlCats(new ResInfoCategory(ResInfoCategoryTools.BASICS));
        GUITools.showPopup(GUITools.createPanelPopup(pnlCats, new Closure() {
            @Override
            public void execute(Object o) {
                if (o != null) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        ResInfoCategory myCat = em.merge((ResInfoCategory) o);
                        em.getTransaction().commit();
                        createCatList();
                    } catch (Exception e) {
                        em.getTransaction().rollback();
                        OPDE.fatal(e);
                    } finally {
                        em.close();
                    }
                }
            }
        }, lblCat), SwingConstants.NORTH_EAST);
    }

    private void btnImportICDActionPerformed(ActionEvent e) {
        if (listICDs.isEmpty()) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.settings.global.noICDFile", DisplayMessage.WARNING));
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

    private Properties getMailProps() {
        Properties myMailProps = new Properties();
        myMailProps.put(SYSPropsTools.KEY_MAIL_HOST, txtMailHost.getText().trim());
        myMailProps.put(SYSPropsTools.KEY_MAIL_PORT, txtMailPort.getText().trim());
        myMailProps.put(SYSPropsTools.KEY_MAIL_USER, txtMailUser.getText().trim());
        myMailProps.put(SYSPropsTools.KEY_MAIL_PASSWORD, txtMailPassword.getText().trim());
        myMailProps.put(SYSPropsTools.KEY_MAIL_SENDER, txtMailSender.getText().trim());
        myMailProps.put(SYSPropsTools.KEY_MAIL_RECIPIENT, txtMailRecipient.getText().trim());
        myMailProps.put(SYSPropsTools.KEY_MAIL_SENDER_PERSONAL, txtMailSenderPersonal.getText().trim());
        myMailProps.put(SYSPropsTools.KEY_MAIL_RECIPIENT_PERSONAL, txtMailRecipientPersonal.getText().trim());
        myMailProps.put(SYSPropsTools.KEY_MAIL_AUTH, Boolean.toString(tbauth.isSelected()));
        myMailProps.put(SYSPropsTools.KEY_MAIL_TLS, Boolean.toString(tbtls.isSelected()));
        myMailProps.put(SYSPropsTools.KEY_MAIL_STARTTLS, Boolean.toString(tbstarttls.isSelected()));
        myMailProps.put(SYSPropsTools.KEY_MAIL_SPAMFILTER_KEY, SYSTools.tidy(SYSTools.catchNull(txtMailSpamfilter.getText())));
        return myMailProps;
    }

    private Properties getFTPProps() {
        Properties myFTPProps = new Properties();
        myFTPProps.put(SYSPropsTools.KEY_FTP_SERVER, txtFTPServer.getText().trim());
        myFTPProps.put(SYSPropsTools.KEY_FTP_PORT, txtFTPPort.getText().trim());
        myFTPProps.put(SYSPropsTools.KEY_FTP_USER, txtFTPUser.getText().trim());
        myFTPProps.put(SYSPropsTools.KEY_FTP_PASSWORD, txtFTPPassword.getText().trim());
        myFTPProps.put(SYSPropsTools.KEY_FTP_WD, txtFTPWorkingDir.getText().trim());
        return myFTPProps;
    }

    private void btnTestmailActionPerformed(ActionEvent e) {

        tbactive.setSelected(false);
        tbactive.setEnabled(false);
//        final Pair<String, String>[] testRecipient = new Pair[]{new Pair(getMailProps().getProperty(SYSPropsTools.KEY_MAIL_RECIPIENT), getMailProps().getProperty(SYSPropsTools.KEY_MAIL_RECIPIENT))};
        final Recipient testRecipient = new Recipient(OPDE.getProps().getProperty(SYSPropsTools.KEY_MAIL_RECIPIENT), OPDE.getProps().getProperty(SYSPropsTools.KEY_MAIL_RECIPIENT_PERSONAL));
        btnTestmail.setEnabled(false);
        OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.settings.global.mail.testing", DisplayMessage.WAIT_TIL_NEXT_MESSAGE));

        SwingWorker worker = new SwingWorker() {
            boolean success = false;

            @Override
            protected Object doInBackground() throws Exception {
                success = EMailSystem.sendMail(SYSTools.xx("opde.settings.global.mail.testsubject"), SYSTools.xx("opde.settings.global.mail.testbody"), testRecipient, null);
                return null;
            }

            @Override
            protected void done() {
                btnTestmail.setEnabled(true);
                tbactive.setEnabled(true);
                if (success) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.settings.global.mail.success"));
                } else {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.settings.global.mail.fail", DisplayMessage.WARNING));
                }
            }
        };

        worker.execute();


    }

    private void btnFTPTestActionPerformed(ActionEvent e) {
        Properties ftpprops = getFTPProps();
        try {
            FileTransferClient ftp = SYSFilesTools.getFTPClient(ftpprops);
            File file = new File(OPDE.getOPWD() + File.separator + "opdestart.jar");
            String md5a = SYSTools.getMD5Checksum(file);
            ftp.uploadFile(file.getPath(), "ftptest.file");
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.settings.global.ftp.msg.upload", 1));
            ftp.downloadFile(OPDE.getOPWD() + File.separator + "ftptest.file", "ftptest.file");
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.settings.global.ftp.msg.download", 1));
            File file2 = new File(OPDE.getOPWD() + File.separator + "ftptest.file");
            String md5b = SYSTools.getMD5Checksum(file2);
            if (md5b.equalsIgnoreCase(md5a)) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.settings.global.ftp.msg.files.equal", 1));
            } else {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.settings.global.ftp.msg.files.not.equal", DisplayMessage.WARNING));
                throw new Exception("MD5 error");
            }
            FileUtils.deleteQuietly(file2);
            ftp.deleteFile("ftptest.file");
            ftp.disconnect();
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage("opde.settings.global.ftp.msg.test.ok", 2));
            SYSPropsTools.storeProp(SYSPropsTools.KEY_FTP_IS_WORKING, "true");
            for (Map.Entry entry : ftpprops.entrySet()) {
                SYSPropsTools.storeProp(entry.getKey().toString(), entry.getValue().toString());
            }
        } catch (Exception ftpEx) {
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(ftpEx.getMessage(), DisplayMessage.WARNING));
            SYSPropsTools.storeProp(SYSPropsTools.KEY_FTP_IS_WORKING, "false");
        }

    }

    private void btnEmptyListActionPerformed(ActionEvent e) {


        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                dlmICDFiles.clear();
                lstIcdFiles.revalidate();
                lstIcdFiles.repaint();
            }
        });

    }

    private void txtMailRecipientCaretUpdate(CaretEvent e) {
        btnTestmail.setEnabled(EMailSystem.isValidEmailAddress(SYSTools.tidy(txtMailRecipient.getText())));
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        tabMain = new JTabbedPane();
        pnlLocal = new JPanel();
        lblPrinters = new JLabel();
        panel4 = new JPanel();
        lblStation = new JLabel();
        cmbPhysicalPrinters = new JComboBox();
        cmbStation = new JComboBox();
        cmbLogicalPrinters = new JComboBox();
        lblTimeout = new JLabel();
        cmbForm = new JComboBox();
        btnTestLabel = new JButton();
        panel1 = new JPanel();
        jspGlobal = new JScrollPane();
        pnlGlobal = new JPanel();
        panel5 = new JPanel();
        panel6 = new JPanel();
        lblHomes = new JLabel();
        lblICD = new JLabel();
        lblEMail = new JLabel();
        jspHomeStation = new JScrollPane();
        cpsHomes = new CollapsiblePanes();
        pnlICD = new JPanel();
        scrollPane1 = new JScrollPane();
        lstIcdFiles = new JList();
        btnEmptyList = new JButton();
        btnImportICD = new JButton();
        panel8 = new JScrollPane();
        pnlMail = new JPanel();
        lblMailHost = new JLabel();
        txtMailHost = new JTextField();
        lblMailPort = new JLabel();
        txtMailPort = new JTextField();
        lblMailUser = new JLabel();
        txtMailUser = new JTextField();
        lblMailPassword = new JLabel();
        txtMailPassword = new JTextField();
        lblMailSender = new JLabel();
        txtMailSender = new JTextField();
        lblMailRecipient = new JLabel();
        txtMailRecipient = new JTextField();
        lblMailSenderPersonal = new JLabel();
        txtMailSenderPersonal = new JTextField();
        lblMailRecipientPersonal = new JLabel();
        txtMailRecipientPersonal = new JTextField();
        lblMailSpamFilter = new JLabel();
        txtMailSpamfilter = new JTextField();
        lblAuth = new JLabel();
        lblStarttls = new JLabel();
        lblTLS = new JLabel();
        btnTestmail = new JButton();
        lblActive = new JLabel();
        cmbCountry = new JComboBox();
        panel7 = new JPanel();
        lblCat = new JLabel();
        lblCalcMed = new JLabel();
        lblFTP = new JLabel();
        jspCat = new JScrollPane();
        lstCat = new JList();
        pnlCalcMed = new JPanel();
        panel3 = new JPanel();
        lblFTPServer = new JLabel();
        txtFTPServer = new JTextField();
        lblFTPPort = new JLabel();
        txtFTPPort = new JTextField();
        lblFTPUser = new JLabel();
        txtFTPUser = new JTextField();
        lblFTPPassword = new JLabel();
        txtFTPPassword = new JTextField();
        lblFTPWD = new JLabel();
        txtFTPWorkingDir = new JTextField();
        btnFTPTest = new JButton();
        panel2 = new JPanel();
        btnAddCat = new JButton();
        btnDeleteCat = new JButton();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== tabMain ========
        {
            tabMain.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    tabMainStateChanged(e);
                }
            });

            //======== pnlLocal ========
            {
                pnlLocal.setLayout(new FormLayout(
                        "default, $lcgap, default:grow, $lcgap, default, $lcgap, default:grow, $lcgap, default",
                        "6*(default, $lgap), pref, $lgap, default, $lgap, 14dlu, $lgap, default"));

                //---- lblPrinters ----
                lblPrinters.setText("labelPrinter");
                lblPrinters.setFont(new Font("Arial", Font.BOLD, 18));
                pnlLocal.add(lblPrinters, CC.xy(3, 3));

                //======== panel4 ========
                {
                    panel4.setBackground(Color.gray);
                    panel4.setLayout(new FlowLayout());
                }
                pnlLocal.add(panel4, CC.xywh(5, 1, 1, 17));

                //---- lblStation ----
                lblStation.setText("Default Station");
                lblStation.setFont(new Font("Arial", Font.BOLD, 18));
                pnlLocal.add(lblStation, CC.xy(7, 3));

                //---- cmbPhysicalPrinters ----
                cmbPhysicalPrinters.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        cmbPhysicalPrintersItemStateChanged(e);
                    }
                });
                pnlLocal.add(cmbPhysicalPrinters, CC.xy(3, 5));

                //---- cmbStation ----
                cmbStation.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        cmbStationItemStateChanged(e);
                    }
                });
                pnlLocal.add(cmbStation, CC.xy(7, 5));

                //---- cmbLogicalPrinters ----
                cmbLogicalPrinters.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        cmbLogicalPrintersItemStateChanged(e);
                    }
                });
                pnlLocal.add(cmbLogicalPrinters, CC.xy(3, 7));

                //---- lblTimeout ----
                lblTimeout.setText("Timeout");
                lblTimeout.setFont(new Font("Arial", Font.BOLD, 18));
                pnlLocal.add(lblTimeout, CC.xy(7, 7));

                //---- cmbForm ----
                cmbForm.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        cmbFormItemStateChanged(e);
                    }
                });
                pnlLocal.add(cmbForm, CC.xy(3, 9));

                //---- btnTestLabel ----
                btnTestLabel.setText("Test");
                btnTestLabel.setEnabled(false);
                btnTestLabel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnTestLabelActionPerformed(e);
                    }
                });
                pnlLocal.add(btnTestLabel, CC.xy(3, 11, CC.RIGHT, CC.DEFAULT));

                //======== panel1 ========
                {
                    panel1.setLayout(new BoxLayout(panel1, BoxLayout.LINE_AXIS));
                }
                pnlLocal.add(panel1, CC.xywh(3, 15, 5, 1, CC.RIGHT, CC.DEFAULT));
            }
            tabMain.addTab("text", pnlLocal);

            //======== jspGlobal ========
            {

                //======== pnlGlobal ========
                {
                    pnlGlobal.setLayout(new FormLayout(
                            "default, $lcgap, default:grow, $lcgap, default, $ugap, default:grow, $lcgap, default, $ugap, default:grow, 2*($lcgap, default)",
                            "default, $lgap, pref, $lgap, fill:default:grow, $lgap, pref, 2*($lgap), 2*(default, $lgap), fill:default:grow, 2*($lgap, default)"));

                    //======== panel5 ========
                    {
                        panel5.setBackground(Color.gray);
                        panel5.setLayout(new FlowLayout());
                    }
                    pnlGlobal.add(panel5, CC.xywh(5, 1, 1, 18));

                    //======== panel6 ========
                    {
                        panel6.setBackground(Color.gray);
                        panel6.setLayout(new FlowLayout());
                    }
                    pnlGlobal.add(panel6, CC.xywh(9, 1, 1, 18));

                    //---- lblHomes ----
                    lblHomes.setText("Homes");
                    lblHomes.setFont(new Font("Arial", Font.BOLD, 18));
                    pnlGlobal.add(lblHomes, CC.xy(3, 3));

                    //---- lblICD ----
                    lblICD.setText("ICD");
                    lblICD.setFont(new Font("Arial", Font.BOLD, 18));
                    pnlGlobal.add(lblICD, CC.xy(7, 3));

                    //---- lblEMail ----
                    lblEMail.setText("E-Mail System");
                    lblEMail.setFont(new Font("Arial", Font.BOLD, 18));
                    pnlGlobal.add(lblEMail, CC.xy(11, 3));

                    //======== jspHomeStation ========
                    {

                        //======== cpsHomes ========
                        {
                            cpsHomes.setLayout(new BoxLayout(cpsHomes, BoxLayout.X_AXIS));
                        }
                        jspHomeStation.setViewportView(cpsHomes);
                    }
                    pnlGlobal.add(jspHomeStation, CC.xy(3, 5, CC.FILL, CC.FILL));

                    //======== pnlICD ========
                    {
                        pnlICD.setLayout(new FormLayout(
                                "default:grow, default",
                                "fill:default:grow, $lgap, 60dlu, $lgap, default"));

                        //======== scrollPane1 ========
                        {
                            scrollPane1.setViewportView(lstIcdFiles);
                        }
                        pnlICD.add(scrollPane1, CC.xy(1, 3));

                        //---- btnEmptyList ----
                        btnEmptyList.setText(null);
                        btnEmptyList.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/deleteall.png")));
                        btnEmptyList.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnEmptyListActionPerformed(e);
                            }
                        });
                        pnlICD.add(btnEmptyList, CC.xy(2, 3, CC.DEFAULT, CC.FILL));

                        //---- btnImportICD ----
                        btnImportICD.setText("importICD");
                        btnImportICD.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnImportICDActionPerformed(e);
                            }
                        });
                        pnlICD.add(btnImportICD, CC.xy(1, 5, CC.LEFT, CC.DEFAULT));
                    }
                    pnlGlobal.add(pnlICD, CC.xywh(7, 5, 1, 3));

                    //======== panel8 ========
                    {

                        //======== pnlMail ========
                        {
                            pnlMail.setLayout(new FormLayout(
                                    "default, $lcgap, default:grow",
                                    "13*(default, $lgap), default"));

                            //---- lblMailHost ----
                            lblMailHost.setText("host");
                            pnlMail.add(lblMailHost, CC.xy(1, 1));
                            pnlMail.add(txtMailHost, CC.xy(3, 1));

                            //---- lblMailPort ----
                            lblMailPort.setText("port");
                            pnlMail.add(lblMailPort, CC.xy(1, 3));
                            pnlMail.add(txtMailPort, CC.xy(3, 3));

                            //---- lblMailUser ----
                            lblMailUser.setText("user");
                            pnlMail.add(lblMailUser, CC.xy(1, 5));
                            pnlMail.add(txtMailUser, CC.xy(3, 5));

                            //---- lblMailPassword ----
                            lblMailPassword.setText("password");
                            pnlMail.add(lblMailPassword, CC.xy(1, 7));
                            pnlMail.add(txtMailPassword, CC.xy(3, 7));

                            //---- lblMailSender ----
                            lblMailSender.setText("sender");
                            pnlMail.add(lblMailSender, CC.xy(1, 9));
                            pnlMail.add(txtMailSender, CC.xy(3, 9));

                            //---- lblMailRecipient ----
                            lblMailRecipient.setText("error-recipient");
                            pnlMail.add(lblMailRecipient, CC.xy(1, 11));

                            //---- txtMailRecipient ----
                            txtMailRecipient.addCaretListener(new CaretListener() {
                                @Override
                                public void caretUpdate(CaretEvent e) {
                                    txtMailRecipientCaretUpdate(e);
                                }
                            });
                            pnlMail.add(txtMailRecipient, CC.xy(3, 11));

                            //---- lblMailSenderPersonal ----
                            lblMailSenderPersonal.setText("sender personal");
                            pnlMail.add(lblMailSenderPersonal, CC.xy(1, 13));
                            pnlMail.add(txtMailSenderPersonal, CC.xy(3, 13));

                            //---- lblMailRecipientPersonal ----
                            lblMailRecipientPersonal.setText("recipient personal");
                            pnlMail.add(lblMailRecipientPersonal, CC.xy(1, 15));
                            pnlMail.add(txtMailRecipientPersonal, CC.xy(3, 15));

                            //---- lblMailSpamFilter ----
                            lblMailSpamFilter.setText("text");
                            pnlMail.add(lblMailSpamFilter, CC.xy(1, 17));
                            pnlMail.add(txtMailSpamfilter, CC.xy(3, 17));

                            //---- lblAuth ----
                            lblAuth.setText("auth");
                            pnlMail.add(lblAuth, CC.xy(1, 19));

                            //---- lblStarttls ----
                            lblStarttls.setText("starttls");
                            pnlMail.add(lblStarttls, CC.xy(1, 21));

                            //---- lblTLS ----
                            lblTLS.setText("tls");
                            pnlMail.add(lblTLS, CC.xy(1, 23));

                            //---- btnTestmail ----
                            btnTestmail.setText("Send Testmail");
                            btnTestmail.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    btnTestmailActionPerformed(e);
                                }
                            });
                            pnlMail.add(btnTestmail, CC.xywh(1, 25, 3, 1, CC.LEFT, CC.DEFAULT));

                            //---- lblActive ----
                            lblActive.setText("active");
                            pnlMail.add(lblActive, CC.xy(1, 27));
                        }
                        panel8.setViewportView(pnlMail);
                    }
                    pnlGlobal.add(panel8, CC.xywh(11, 5, 1, 3));
                    pnlGlobal.add(cmbCountry, CC.xy(3, 7));

                    //======== panel7 ========
                    {
                        panel7.setBackground(Color.gray);
                        panel7.setLayout(new FlowLayout());
                    }
                    pnlGlobal.add(panel7, CC.xywh(3, 10, 9, 1));

                    //---- lblCat ----
                    lblCat.setText("ResInfoCat");
                    lblCat.setFont(new Font("Arial", Font.BOLD, 18));
                    pnlGlobal.add(lblCat, CC.xy(3, 12));

                    //---- lblCalcMed ----
                    lblCalcMed.setText("ResInfoCat");
                    lblCalcMed.setFont(new Font("Arial", Font.BOLD, 18));
                    pnlGlobal.add(lblCalcMed, CC.xy(7, 12));

                    //---- lblFTP ----
                    lblFTP.setText("FTP System");
                    lblFTP.setFont(new Font("Arial", Font.BOLD, 18));
                    pnlGlobal.add(lblFTP, CC.xy(11, 12));

                    //======== jspCat ========
                    {

                        //---- lstCat ----
                        lstCat.setFont(new Font("Arial", Font.PLAIN, 14));
                        lstCat.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        lstCat.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mousePressed(MouseEvent e) {
                                lstCatMousePressed(e);
                            }
                        });
                        jspCat.setViewportView(lstCat);
                    }
                    pnlGlobal.add(jspCat, CC.xy(3, 14, CC.FILL, CC.FILL));

                    //======== pnlCalcMed ========
                    {
                        pnlCalcMed.setLayout(new FormLayout(
                                "default:grow",
                                "2*(default, $lgap), default"));
                    }
                    pnlGlobal.add(pnlCalcMed, CC.xy(7, 14));

                    //======== panel3 ========
                    {
                        panel3.setLayout(new FormLayout(
                                "default, $lcgap, default:grow",
                                "5*(default, $lgap), default"));

                        //---- lblFTPServer ----
                        lblFTPServer.setText("host");
                        panel3.add(lblFTPServer, CC.xy(1, 1));
                        panel3.add(txtFTPServer, CC.xy(3, 1));

                        //---- lblFTPPort ----
                        lblFTPPort.setText("port");
                        panel3.add(lblFTPPort, CC.xy(1, 3));
                        panel3.add(txtFTPPort, CC.xy(3, 3));

                        //---- lblFTPUser ----
                        lblFTPUser.setText("user");
                        panel3.add(lblFTPUser, CC.xy(1, 5));
                        panel3.add(txtFTPUser, CC.xy(3, 5));

                        //---- lblFTPPassword ----
                        lblFTPPassword.setText("password");
                        panel3.add(lblFTPPassword, CC.xy(1, 7));
                        panel3.add(txtFTPPassword, CC.xy(3, 7));

                        //---- lblFTPWD ----
                        lblFTPWD.setText("working dir");
                        panel3.add(lblFTPWD, CC.xy(1, 9));
                        panel3.add(txtFTPWorkingDir, CC.xy(3, 9));

                        //---- btnFTPTest ----
                        btnFTPTest.setText("FTP Test");
                        btnFTPTest.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnFTPTestActionPerformed(e);
                            }
                        });
                        panel3.add(btnFTPTest, CC.xywh(1, 11, 3, 1, CC.LEFT, CC.DEFAULT));
                    }
                    pnlGlobal.add(panel3, CC.xy(11, 14));

                    //======== panel2 ========
                    {
                        panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

                        //---- btnAddCat ----
                        btnAddCat.setText(null);
                        btnAddCat.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")));
                        btnAddCat.setBorderPainted(false);
                        btnAddCat.setBorder(null);
                        btnAddCat.setContentAreaFilled(false);
                        btnAddCat.setPressedIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/pressed.png")));
                        btnAddCat.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                btnAddCatActionPerformed(e);
                            }
                        });
                        panel2.add(btnAddCat);

                        //---- btnDeleteCat ----
                        btnDeleteCat.setText(null);
                        btnDeleteCat.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/bw/editdelete.png")));
                        btnDeleteCat.setBorderPainted(false);
                        btnDeleteCat.setBorder(null);
                        btnDeleteCat.setContentAreaFilled(false);
                        btnDeleteCat.setEnabled(false);
                        panel2.add(btnDeleteCat);
                    }
                    pnlGlobal.add(panel2, CC.xy(3, 16));
                }
                jspGlobal.setViewportView(pnlGlobal);
            }
            tabMain.addTab("text", jspGlobal);
        }
        add(tabMain);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    @Override
    public void cleanup() {
        cpMap.clear();
        cpPanel.clear();
        cpsHomes.removeAll();
        if (listICDs != null) {
            listICDs.clear();
        }
    }

    @Override
    public void reload() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getInternalClassID() {
        return internalClassID;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private void initPanel() {
        OPDE.getDisplayManager().setMainMessage(SYSTools.xx(internalClassID));
        OPDE.getDisplayManager().clearAllIcons();

        dlmICDFiles = new DefaultListModel<>();
        lstIcdFiles.setModel(dlmICDFiles);

        tabMain.setTitleAt(TAB_LOCAL, SYSTools.xx("opde.settings.local"));
        tabMain.setTitleAt(TAB_GLOBAL, SYSTools.xx("opde.settings.global"));

        lblHomes.setText(SYSTools.xx("opde.settings.global.homes"));
        lblCat.setText(SYSTools.xx("opde.settings.global.categories"));
        tabMainStateChanged(null);

    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JTabbedPane tabMain;
    private JPanel pnlLocal;
    private JLabel lblPrinters;
    private JPanel panel4;
    private JLabel lblStation;
    private JComboBox cmbPhysicalPrinters;
    private JComboBox cmbStation;
    private JComboBox cmbLogicalPrinters;
    private JLabel lblTimeout;
    private JComboBox cmbForm;
    private JButton btnTestLabel;
    private JPanel panel1;
    private JScrollPane jspGlobal;
    private JPanel pnlGlobal;
    private JPanel panel5;
    private JPanel panel6;
    private JLabel lblHomes;
    private JLabel lblICD;
    private JLabel lblEMail;
    private JScrollPane jspHomeStation;
    private CollapsiblePanes cpsHomes;
    private JPanel pnlICD;
    private JScrollPane scrollPane1;
    private JList lstIcdFiles;
    private JButton btnEmptyList;
    private JButton btnImportICD;
    private JScrollPane panel8;
    private JPanel pnlMail;
    private JLabel lblMailHost;
    private JTextField txtMailHost;
    private JLabel lblMailPort;
    private JTextField txtMailPort;
    private JLabel lblMailUser;
    private JTextField txtMailUser;
    private JLabel lblMailPassword;
    private JTextField txtMailPassword;
    private JLabel lblMailSender;
    private JTextField txtMailSender;
    private JLabel lblMailRecipient;
    private JTextField txtMailRecipient;
    private JLabel lblMailSenderPersonal;
    private JTextField txtMailSenderPersonal;
    private JLabel lblMailRecipientPersonal;
    private JTextField txtMailRecipientPersonal;
    private JLabel lblMailSpamFilter;
    private JTextField txtMailSpamfilter;
    private JLabel lblAuth;
    private JLabel lblStarttls;
    private JLabel lblTLS;
    private JButton btnTestmail;
    private JLabel lblActive;
    private JComboBox cmbCountry;
    private JPanel panel7;
    private JLabel lblCat;
    private JLabel lblCalcMed;
    private JLabel lblFTP;
    private JScrollPane jspCat;
    private JList lstCat;
    private JPanel pnlCalcMed;
    private JPanel panel3;
    private JLabel lblFTPServer;
    private JTextField txtFTPServer;
    private JLabel lblFTPPort;
    private JTextField txtFTPPort;
    private JLabel lblFTPUser;
    private JTextField txtFTPUser;
    private JLabel lblFTPPassword;
    private JTextField txtFTPPassword;
    private JLabel lblFTPWD;
    private JTextField txtFTPWorkingDir;
    private JButton btnFTPTest;
    private JPanel panel2;
    private JButton btnAddCat;
    private JButton btnDeleteCat;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
