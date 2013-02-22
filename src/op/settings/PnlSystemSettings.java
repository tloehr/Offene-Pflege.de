/*
 * Created by JFormDesigner on Thu Dec 20 15:01:22 CET 2012
 */

package op.settings;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import entity.Homes;
import entity.Station;
import entity.StationTools;
import entity.prescription.MedStock;
import entity.system.SYSPropsTools;
import op.OPDE;
import op.system.LogicalPrinter;
import op.system.PrinterForm;
import op.threads.DisplayManager;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.VerticalLayout;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author Torsten Löhr
 */
public class PnlSystemSettings extends CleanablePanel {
    private final int TAB_LOCAL = 0;
    private final int TAB_GLOBAL = 1;
    public static final String internalClassID = "opde.settings";
    private MedStock testStock;
    private HashMap<String, CollapsiblePane> cpMap;
    private ArrayList<Homes> listHomes;

    private boolean configsHaveBeenSaved = false;

    public PnlSystemSettings(JScrollPane jspSearch) {
        jspSearch.setViewportView(new JPanel());
        cpMap = new HashMap<String, CollapsiblePane>();
        initComponents();
        initPanel();
    }

    private void btnTestLabelActionPerformed(ActionEvent e) {
        if (!configsHaveBeenSaved) return;

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
        OPDE.getLocalProps().setProperty(SYSPropsTools.KEY_STATION, ((Station) cmbStation.getSelectedItem()).getStatID().toString());
        OPDE.saveLocalProps();
        configsHaveBeenSaved = true;
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

        PrintService[] prservices = PrintServiceLookup.lookupPrintServices(null, null);

        if (prservices != null) {

            cmbPhysicalPrinters.setModel(new DefaultComboBoxModel(prservices));

            cmbPhysicalPrinters.setRenderer(new ListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                    if (o == null)
                        return new DefaultListCellRenderer().getListCellRendererComponent(jList, OPDE.lang.getString("misc.msg.error"), i, isSelected, cellHasFocus);
                    return new DefaultListCellRenderer().getListCellRendererComponent(jList, ((PrintService) o).getName(), i, isSelected, cellHasFocus);
                }
            });
            cmbLogicalPrinters.setRenderer(new ListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                    if (o == null)
                        return new DefaultListCellRenderer().getListCellRendererComponent(jList, OPDE.lang.getString("misc.msg.error"), i, isSelected, cellHasFocus);
                    return new DefaultListCellRenderer().getListCellRendererComponent(jList, ((LogicalPrinter) o).getLabel(), i, isSelected, cellHasFocus);
                }
            });
            cmbForm.setRenderer(new ListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                    if (o == null)
                        return new DefaultListCellRenderer().getListCellRendererComponent(jList, OPDE.lang.getString("misc.msg.error"), i, isSelected, cellHasFocus);
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
                    }
                    OPDE.saveLocalProps();
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
                        OPDE.saveLocalProps();
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
                    OPDE.saveLocalProps();
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
            }


            if (!OPDE.getLogicalPrinters().getLogicalPrintersList().isEmpty()) {
                cmbLogicalPrinters.setModel(new DefaultComboBoxModel(OPDE.getLogicalPrinters().getLogicalPrintersList().toArray()));
                LogicalPrinter logicalPrinter = OPDE.getLogicalPrinters().getMapName2LogicalPrinter().get(OPDE.getProps().getProperty(SYSPropsTools.KEY_LOGICAL_PRINTER));
                if (logicalPrinter == null) logicalPrinter = OPDE.getLogicalPrinters().getLogicalPrintersList().get(0);

                cmbLogicalPrinters.setSelectedItem(logicalPrinter);

                cmbForm.setModel(new DefaultComboBoxModel(logicalPrinter.getForms().values().toArray()));
                if (OPDE.getProps().containsKey(SYSPropsTools.KEY_MEDSTOCK_LABEL) && logicalPrinter.getForms().containsKey(OPDE.getProps().getProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL))) {
                    cmbForm.setSelectedItem(logicalPrinter.getForms().get(OPDE.getProps().getProperty(SYSPropsTools.KEY_MEDSTOCK_LABEL)));
                } else {
                    cmbForm.setSelectedIndex(0);
                }
            }
        }

        btnTestLabel.setEnabled(prservices != null);
        cmbForm.setEnabled(prservices != null);
        cmbLogicalPrinters.setEnabled(prservices != null);
        cmbPhysicalPrinters.setEnabled(prservices != null);

        lblPrinters.setText(OPDE.lang.getString(internalClassID + ".labelPrinters"));
        lblStation.setText(OPDE.lang.getString(internalClassID + ".station"));

        cmbStation.setModel(StationTools.getAll4Combobox(false));
        cmbStation.setSelectedItem(StationTools.getStationForThisHost());
    }

    private void initGlobal() {
        createHomesList();
    }


    private void createHomesList() {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery("SELECT e FROM Homes e ORDER BY e.eid");
        listHomes = new ArrayList(query.getResultList());
        em.close();
        cpsHomes.removeAll();
        cpsHomes.setLayout(new JideBoxLayout(cpsHomes, JideBoxLayout.Y_AXIS));
        for (final Homes home : listHomes) {

            JPanel pnlContentH = new JPanel(new VerticalLayout());
            JideButton btnAddTX = GUITools.createHyperlinkButton("add new station", null, null);
            pnlContentH.add(btnAddTX);

            Collections.sort(home.getStations());

            for (final Station station : home.getStations()) {
                String titleS = "<html><font size=+1>" + station.getName() + "</font></html>";
                DefaultCPTitle cpTitleS = new DefaultCPTitle(titleS, null);

                final JButton btnEditStation = new JButton(SYSConst.icon22edit);
                btnEditStation.setPressedIcon(SYSConst.icon22Pressed);
                btnEditStation.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnEditStation.setContentAreaFilled(false);
                btnEditStation.setBorder(null);

                btnEditStation.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        final JidePopup popup = new JidePopup();
                        popup.setMovable(false);
                        popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));

                        final JTextArea editor = new JTextArea(station.getName(), 10, 40);
                        editor.setLineWrap(false);
                        editor.setWrapStyleWord(false);
                        editor.setEditable(true);

                        popup.getContentPane().add(new JScrollPane(editor));
                        final JButton saveButton = new JButton(SYSConst.icon16apply);
                        saveButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    popup.hidePopup();
                                    Station myStation = em.merge(station);
                                    myStation.setName(editor.getText().trim());
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
                        });

                        saveButton.setHorizontalAlignment(SwingConstants.RIGHT);
                        JPanel pnl = new JPanel(new BorderLayout(10, 10));
                        JScrollPane pnlEditor = new JScrollPane(editor);

                        pnl.add(pnlEditor, BorderLayout.CENTER);
                        JPanel buttonPanel = new JPanel();
                        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
                        buttonPanel.add(saveButton);
                        pnl.setBorder(new EmptyBorder(10, 10, 10, 10));
                        pnl.add(buttonPanel, BorderLayout.SOUTH);

                        popup.setOwner(btnEditStation);
                        popup.removeExcludedComponent(btnEditStation);
                        popup.getContentPane().add(pnl);
                        popup.setDefaultFocusComponent(editor);

                        GUITools.showPopup(popup, SwingConstants.SOUTH_WEST);
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
                            new DlgYesNo(OPDE.lang.getString("misc.questions.delete1") + "<br/><i>" + station.getName() + "</i><br/>" + OPDE.lang.getString("misc.questions.delete2"), SYSConst.icon48delete, new Closure() {
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
                                            OPDE.getMainframe().prepareSearchArea();
                                        } catch (RollbackException ole) {
                                            if (em.getTransaction().isActive()) {
                                                em.getTransaction().rollback();
                                            }
                                            if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
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
            String titleH = "<html><font size=+1><b>" + home.getBezeichnung() + "</b></font></html>";
            DefaultCPTitle cpTitleH = new DefaultCPTitle(titleH, null);

            CollapsiblePane cpH = new CollapsiblePane();
            cpH.setSlidingDirection(SwingConstants.SOUTH);
            cpH.setHorizontalAlignment(SwingConstants.LEADING);
            cpH.setOpaque(false);
            cpH.setTitleLabelComponent(cpTitleH.getMain());
            cpH.setContentPane(pnlContentH);
            cpsHomes.add(cpH);

        }
        cpsHomes.addExpansion();
    }

    private void initComponents() {
        tabMain = new JTabbedPane();
        pnlLocal = new JPanel();
        lblPrinters = new JLabel();
        lblStation = new JLabel();
        cmbPhysicalPrinters = new JComboBox();
        cmbStation = new JComboBox();
        cmbLogicalPrinters = new JComboBox();
        cmbForm = new JComboBox();
        btnTestLabel = new JButton();
        panel1 = new JPanel();
        pnlGlobal = new JPanel();
        lblHomes = new JLabel();
        jspHomeStation = new JScrollPane();
        cpsHomes = new CollapsiblePanes();
        btnAddHome = new JButton();

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
                        "default, 2*($lcgap, default:grow), $lcgap, default",
                        "6*(default, $lgap), default:grow, $lgap, default, $lgap, 14dlu"));

                //---- lblPrinters ----
                lblPrinters.setText("Etiketten-Drucker");
                lblPrinters.setFont(new Font("Arial", Font.BOLD, 18));
                pnlLocal.add(lblPrinters, CC.xy(3, 3));

                //---- lblStation ----
                lblStation.setText("Etiketten-Drucker");
                lblStation.setFont(new Font("Arial", Font.BOLD, 18));
                pnlLocal.add(lblStation, CC.xy(5, 3));

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
                pnlLocal.add(cmbStation, CC.xy(5, 5));

                //---- cmbLogicalPrinters ----
                cmbLogicalPrinters.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        cmbLogicalPrintersItemStateChanged(e);
                    }
                });
                pnlLocal.add(cmbLogicalPrinters, CC.xy(3, 7));

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
                pnlLocal.add(panel1, CC.xywh(3, 15, 3, 1, CC.RIGHT, CC.DEFAULT));
            }
            tabMain.addTab("text", pnlLocal);


            //======== pnlGlobal ========
            {
                pnlGlobal.setLayout(new FormLayout(
                        "default, $lcgap, default:grow, $lcgap, default",
                        "pref, $lgap, default:grow, 2*($lgap, default)"));

                //---- lblHomes ----
                lblHomes.setText("Etiketten-Drucker");
                lblHomes.setFont(new Font("Arial", Font.BOLD, 18));
                pnlGlobal.add(lblHomes, CC.xy(3, 1));

                //======== jspHomeStation ========
                {

                    //======== cpsHomes ========
                    {
                        cpsHomes.setLayout(new BoxLayout(cpsHomes, BoxLayout.X_AXIS));
                    }
                    jspHomeStation.setViewportView(cpsHomes);
                }
                pnlGlobal.add(jspHomeStation, CC.xy(3, 3, CC.FILL, CC.FILL));

                //---- btnAddHome ----
                btnAddHome.setText("text");
                pnlGlobal.add(btnAddHome, CC.xy(3, 5, CC.LEFT, CC.DEFAULT));
            }
            tabMain.addTab("text", pnlGlobal);

        }
        add(tabMain);
        add(tabMain);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    @Override
    public void cleanup() {
        //To change body of implemented methods use File | Settings | File Templates.
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
        OPDE.getDisplayManager().setMainMessage(OPDE.lang.getString(internalClassID));
        OPDE.getDisplayManager().clearAllIcons();

        tabMain.setTitleAt(TAB_LOCAL, OPDE.lang.getString(internalClassID + ".tab.local"));
        tabMain.setTitleAt(TAB_GLOBAL, OPDE.lang.getString(internalClassID + ".tab.global"));

        lblHomes.setText(OPDE.lang.getString(internalClassID + ".lblHomes"));

        tabMainStateChanged(null);
    }

    private JTabbedPane tabMain;
    private JPanel pnlLocal;
    private JLabel lblPrinters;
    private JLabel lblStation;
    private JComboBox cmbPhysicalPrinters;
    private JComboBox cmbStation;
    private JComboBox cmbLogicalPrinters;
    private JComboBox cmbForm;
    private JButton btnTestLabel;
    private JPanel panel1;
    private JPanel pnlGlobal;
    private JLabel lblHomes;
    private JScrollPane jspHomeStation;
    private CollapsiblePanes cpsHomes;
    private JButton btnAddHome;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
