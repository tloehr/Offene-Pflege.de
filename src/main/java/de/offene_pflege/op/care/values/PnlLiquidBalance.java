/*
 * Created by JFormDesigner on Mon Mar 03 15:17:40 CET 2014
 */

package de.offene_pflege.op.care.values;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.grid.TableUtils;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import de.offene_pflege.entity.files.SYSFilesTools;
import de.offene_pflege.entity.files.SYSVAL2FILE;
import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.entity.info.ResidentTools;
import de.offene_pflege.entity.process.SYSVAL2PROCESS;
import de.offene_pflege.entity.values.ResValue;
import de.offene_pflege.entity.values.ResValueTools;
import de.offene_pflege.entity.values.ResValueTypes;
import de.offene_pflege.entity.values.ResValueTypesTools;
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.threads.DisplayManager;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.*;
import de.offene_pflege.tablerenderer.RNDHTML;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.jdesktop.swingx.VerticalLayout;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;


/**
 * @author Torsten Löhr
 */
public class PnlLiquidBalance extends NursingRecordsPanel {

    private Resident resident;
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private ArrayList<ResValue> listValues;
    private HashMap<java.time.LocalDate, MutableTriple<BigDecimal, BigDecimal, BigDecimal>> mapSums;
    private java.time.LocalDate startDay;
    private DateFormat df;
    private NumberFormat nf;
    private final ResValueTypes LIQUIDBALANCE;
    private ArrayList<java.time.LocalDate> listOfDays;
    private BigDecimal targetIn, highIn, lowIn;
    private Properties controlProps;

    public PnlLiquidBalance(Resident resident, JScrollPane jspSearch) {
        super("nursingrecords.liquidbalances");
        this.resident = resident;
        this.jspSearch = jspSearch;
        LIQUIDBALANCE = ResValueTypesTools.getType(ResValueTypesTools.LIQUIDBALANCE);
        listOfDays = new ArrayList<>();
        initComponents();
        initPanel();
        prepareSearchArea();
        switchResident(resident);
    }

    private void initPanel() {
        startDay = java.time.LocalDate.now();
        lblLeft.setText(SYSTools.xx("nursingrecords.liquidbalances.summary"));
        lblRight.setText(SYSTools.xx("nursingrecords.liquidbalances.details"));
        lblLimits.setText(null);

        df = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT);
        nf = DecimalFormat.getNumberInstance();
    }

    private void btnTodayActionPerformed(ActionEvent e) {
        startDay = java.time.LocalDate.now();
        listOfDays.clear();
        reload();
    }

    private void btnBackActionPerformed(ActionEvent e) {
        int pos = listOfDays.indexOf(startDay);
        if (pos > 0) {
            startDay = listOfDays.get(pos - 1);
        } else {
            startDay = java.time.LocalDate.now();
        }
        reload();
    }

    private void btnForwardActionPerformed(ActionEvent e) {
        if (mapSums == null || mapSums.isEmpty()) return;

        int pos = listOfDays.indexOf(startDay);
        if (!listOfDays.contains(startDay) || pos == listOfDays.size() - 1) {


            startDay = listOfDays.get(listOfDays.size() - 1).minusDays(1);
            //new LocalDate(((java.sql.Date) listSummaries.get(listSummaries.size() - 1)[0]).getTime()).minusDays(1);
            listOfDays.add(startDay);
        } else {
            startDay = listOfDays.get(pos + 1);
        }
        reload();
    }

    private void btnPrintActionPerformed(ActionEvent e) {
        if (listValues == null || listValues.isEmpty()) return;
        SYSFilesTools.print(SYSTools.toHTML(ResValueTools.getAsHTML(listValues)), true);
    }

    private void scrlLeftComponentResized(ComponentEvent e) {
        SwingUtilities.invokeLater(() -> {
            tblLeft.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            if (highIn != null || lowIn != null) {
                TableUtils.autoResizeAllColumns(tblLeft, null, new int[]{100, 100, 100, 100}, true, false);
            } else {
                TableUtils.autoResizeAllColumns(tblLeft, null, new int[]{100, 100, 100, 100, 100}, true, false);
            }

            TableUtils.autoResizeAllRows(tblLeft);

        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblLeft = new JLabel();
        lblRight = new JLabel();
        scrlLeft = new JScrollPane();
        tblLeft = new JTable();
        scrlRight = new JScrollPane();
        tblRight = new JTable();
        lblLimits = new JLabel();
        panel1 = new JPanel();
        btnToday = new JButton();
        btnBack = new JButton();
        btnForward = new JButton();
        panel2 = new JPanel();
        btnPrint = new JButton();

        //======== this ========
        setLayout(new FormLayout(
                "pref, $lcgap, pref:grow",
                "default, $lgap, default:grow, 2*($lgap, default)"));

        //---- lblLeft ----
        lblLeft.setText("text");
        lblLeft.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblLeft, CC.xy(1, 1, CC.CENTER, CC.DEFAULT));

        //---- lblRight ----
        lblRight.setText("text");
        lblRight.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblRight, CC.xy(3, 1, CC.CENTER, CC.DEFAULT));

        //======== scrlLeft ========
        {
            scrlLeft.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    scrlLeftComponentResized(e);
                }
            });

            //---- tblLeft ----
            tblLeft.setFont(new Font("Arial", Font.PLAIN, 16));
            tblLeft.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            scrlLeft.setViewportView(tblLeft);
        }
        add(scrlLeft, CC.xywh(1, 3, 1, 3, CC.FILL, CC.FILL));

        //======== scrlRight ========
        {

            //---- tblRight ----
            tblRight.setFont(new Font("Arial", Font.PLAIN, 16));
            scrlRight.setViewportView(tblRight);
        }
        add(scrlRight, CC.xy(3, 3, CC.FILL, CC.FILL));

        //---- lblLimits ----
        lblLimits.setText("text");
        add(lblLimits, CC.xy(3, 5));

        //======== panel1 ========
        {
            panel1.setLayout(new BoxLayout(panel1, BoxLayout.LINE_AXIS));

            //---- btnToday ----
            btnToday.setText(null);
            btnToday.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/2leftarrow.png")));
            btnToday.addActionListener(e -> btnTodayActionPerformed(e));
            panel1.add(btnToday);

            //---- btnBack ----
            btnBack.setText(null);
            btnBack.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/1leftarrow.png")));
            btnBack.addActionListener(e -> btnBackActionPerformed(e));
            panel1.add(btnBack);

            //---- btnForward ----
            btnForward.setText(null);
            btnForward.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/1rightarrow.png")));
            btnForward.addActionListener(e -> btnForwardActionPerformed(e));
            panel1.add(btnForward);
        }
        add(panel1, CC.xy(1, 7, CC.CENTER, CC.DEFAULT));

        //======== panel2 ========
        {
            panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

            //---- btnPrint ----
            btnPrint.setText(null);
            btnPrint.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/printer1.png")));
            btnPrint.addActionListener(e -> btnPrintActionPerformed(e));
            panel2.add(btnPrint);
        }
        add(panel2, CC.xy(3, 7, CC.RIGHT, CC.DEFAULT));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    @Override
    public void switchResident(Resident resident) {
        this.resident = resident;
        GUITools.setResidentDisplay(resident);
        cleanup();
        parseControlling();
        reload();
    }

    private void parseControlling() {
        controlProps = ResidentTools.getControlling(resident);
        lowIn = SYSTools.parseDecimal(controlProps.getProperty(ResidentTools.KEY_LOWIN));
        targetIn = SYSTools.parseDecimal(controlProps.getProperty(ResidentTools.KEY_TARGETIN));
        highIn = SYSTools.parseDecimal(controlProps.getProperty(ResidentTools.KEY_HIGHIN));

    }

    @Override
    public void cleanup() {
        super.cleanup();
        SYSTools.clear(listValues);
        SYSTools.clear(mapSums);
        SYSTools.clear(controlProps);
    }

    @Override
    public void reload() {
        cleanup();
        loadData();
    }

    private void loadData() {
        loadLeftTable();
        tblRight.setModel(new DefaultTableModel());
    }

    void loadLeftTable() {
        Vector<Vector> dataLeft = new Vector<>();
        mapSums = ResValueTools.getLiquidBalances(resident, startDay, startDay.minusDays(20));
        listOfDays = new ArrayList<>(mapSums.keySet());
        listOfDays.sort((o1, o2) -> o1.compareTo(o2) * -1);

        for (java.time.LocalDate day : listOfDays) {
            Vector line = new Vector();
            line.add(day);
            BigDecimal liquidin = mapSums.get(day).getLeft();
            line.add(liquidin);

            line.add(mapSums.get(day).getMiddle());


            BigDecimal liquidresult = mapSums.get(day).getRight();
            line.add(liquidresult);

            if (highIn != null || lowIn != null) {
                String evaluation = "";
                if (lowIn != null && liquidin.compareTo(lowIn) < 0) {
                    evaluation = SYSConst.html_color(Color.blue, SYSConst.html_bold("misc.msg.too.less"));
                } else if (highIn != null && liquidin.compareTo(highIn) > 0) {
                    evaluation = SYSConst.html_color(Color.red, SYSConst.html_bold("misc.msg.too.much"));
                } else {
                    evaluation = SYSConst.html_color(Color.green.darker(), SYSConst.html_bold("misc.msg.ok"));
                }
                line.add(evaluation);
            }


            dataLeft.add(line);
        }

        Vector<String> headerLeft = new Vector<>();
        headerLeft.add(SYSTools.xx("misc.msg.Date"));
        headerLeft.add(SYSTools.xx("misc.msg.ingestion")); // einfuhr
        headerLeft.add(SYSTools.xx("misc.msg.egestion")); // ausfuhr
        headerLeft.add(SYSTools.xx("misc.msg.liquid.result")); // bilanz
        if (highIn != null || lowIn != null) {
            headerLeft.add(SYSTools.xx("misc.msg.evaluation"));
        }
        DefaultTableModel tmLeft = new DefaultTableModel(dataLeft, headerLeft) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblLeft.setModel(tmLeft);
        tblLeft.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();

            int minIndex = lsm.getMinSelectionIndex();
            int maxIndex = lsm.getMaxSelectionIndex();

            if (minIndex < 0 || maxIndex < 0) return;

            cleanup();

            loadRightTable((java.time.LocalDate) tblLeft.getModel().getValueAt(maxIndex, 0), (java.time.LocalDate) tblLeft.getModel().getValueAt(minIndex, 0));
        });
        tblLeft.getColumnModel().getColumn(0).setCellRenderer((table, o, isSelected, hasFocus, row, column) -> {
            String text;
            if (o == null) {
                text = SYSTools.xx("misc.commands.>>noselection<<");
            } else if (o instanceof java.time.LocalDate) {
                text = ((java.time.LocalDate) o).format(DateTimeFormatter.ofLocalizedDate( FormatStyle.MEDIUM));
            } else {
                text = o.toString();
            }
            return new DefaultTableCellRenderer().getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);
        });


        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tblLeft.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        tblLeft.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        tblLeft.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        if (highIn != null || lowIn != null) {
            tblLeft.getColumnModel().getColumn(4).setCellRenderer(new RNDHTML());
        }

        scrlLeftComponentResized(null);


    }

    void loadRightTable(final java.time.LocalDate from, final java.time.LocalDate to) {

        if (listValues != null) {
            listValues.clear();
        }
        listValues = ResValueTools.getResValuesNoEdits(resident, ResValueTypesTools.LIQUIDBALANCE, from, to);

        Object[][] data = new Object[listValues.size()][5];
        int row = 0;
        for (ResValue val : listValues) {
            data[row][0] = df.format(val.getPit());
            data[row][1] = nf.format(val.getVal1());
            data[row][2] = val.getText();
            data[row][3] = SYSTools.anonymizeUser(val.getUser());
            data[row][4] = SYSConst.icon22delete;
            row++;
        }

        Object[] headers = new Object[]{SYSTools.xx("misc.msg.Date"), SYSTools.xx("misc.msg.amount"), SYSTools.xx("misc.msg.Text"), SYSTools.xx("misc.msg.user"), SYSTools.xx("misc.msg.delete")};

        final DefaultTableModel tmRight = new DefaultTableModel(data, headers) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // only the del-button
            }
        };

        tmRight.setColumnCount(5);
        tblRight.setModel(tmRight);

        tblRight.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tblRight.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);

        TableUtils.autoResizeAllColumns(tblRight, null, new int[]{200, 200, 200, 200, 100}, true, false);
        TableUtils.autoResizeAllRows(tblRight);

        /***
         *      ____       _      _
         *     |  _ \  ___| | ___| |_ ___
         *     | | | |/ _ \ |/ _ \ __/ _ \
         *     | |_| |  __/ |  __/ ||  __/
         *     |____/ \___|_|\___|\__\___|
         *
         */
        ButtonColumn bc = new ButtonColumn(tblRight, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                final int row = Integer.parseInt(ae.getActionCommand());
                final ResValue val2Delete = listValues.get(row);
                currentEditor = new DlgYesNo(SYSTools.xx("misc.questions.delete1") + "<br/><i>" + "<br/><i>" + df.format(val2Delete.getPit()) + "<br/>" + nf.format(val2Delete.getVal1()) + " ml<br/>" + val2Delete.getUser().toString() + "</i><br/>" + SYSTools.xx("misc.questions.delete2"), SYSConst.icon48delete, answer -> {
                    if (answer.equals(JOptionPane.YES_OPTION)) {


                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();
                            ResValue myValue = em.merge(val2Delete);
                            em.lock(myValue, LockModeType.OPTIMISTIC);
                            myValue.setDeletedBy(em.merge(OPDE.getLogin().getUser()));
                            for (SYSVAL2FILE file : myValue.getAttachedFilesConnections()) {
                                em.remove(file);
                            }
                            myValue.getAttachedFilesConnections().clear();
                            for (SYSVAL2PROCESS connObj : myValue.getAttachedProcessConnections()) {
                                em.remove(connObj);
                            }
                            myValue.getAttachedProcessConnections().clear();
                            myValue.getAttachedProcesses().clear();
                            em.getTransaction().commit();
                            listValues.remove(row);
                            tmRight.removeRow(row);
                            loadLeftTable();
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
                            currentEditor = null;
                        }

                    }
                });
                currentEditor.setVisible(true);
            }
        }, 4);
        bc.setMnemonic(KeyEvent.VK_DELETE);

    }

    @Override
    public String getInternalClassID() {
        return internalClassID;
    }


    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);

        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout(3));
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

        /***
         *      _   _
         *     | \ | | _____      __
         *     |  \| |/ _ \ \ /\ / /
         *     | |\  |  __/\ V  V /
         *     |_| \_|\___| \_/\_/
         *
         */

        JideButton addButton = GUITools.createHyperlinkButton(SYSTools.xx("misc.commands.new"), new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")), actionEvent -> {
            if (!ResidentTools.isActive(resident)) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.cantChangeInactiveResident"));
                return;
            }
            currentEditor = new DlgValue(new ResValue(resident, LIQUIDBALANCE), DlgValue.MODE_NEW, o -> {
                ResValue myValue = null;

                if (o != null) {

                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        myValue = em.merge((ResValue) o);
                        em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                        em.getTransaction().commit();

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

                    startDay = JavaTimeConverter.toJavaLocalDateTime(myValue.getPit()).toLocalDate();
                    loadLeftTable();
                    loadRightTable(startDay, startDay);

                }
                currentEditor = null;
            });
            currentEditor.setVisible(true);
        });
        list.add(addButton);


        JideButton controlButton = GUITools.createHyperlinkButton(SYSTools.xx("nursingrecords.vitalparameters.btnControlling.tooltip"), SYSConst.icon22magnify1, actionEvent -> {
            if (!ResidentTools.isActive(resident)) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.cantChangeInactiveResident"));
                return;
            }
            currentEditor = new DlgValueControl(resident, o -> {
                if (o != null) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        Resident myResident = em.merge(resident);
                        em.lock(myResident, LockModeType.OPTIMISTIC);
                        ResidentTools.setControlling(myResident, (Properties) o);
                        em.getTransaction().commit();
                        resident = myResident;
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
                    parseControlling();
                    loadLeftTable();
                    currentEditor = null;
                }
            });
            currentEditor.setVisible(true);
        });
        list.add(controlButton);


        return list;
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblLeft;
    private JLabel lblRight;
    private JScrollPane scrlLeft;
    private JTable tblLeft;
    private JScrollPane scrlRight;
    private JTable tblRight;
    private JLabel lblLimits;
    private JPanel panel1;
    private JButton btnToday;
    private JButton btnBack;
    private JButton btnForward;
    private JPanel panel2;
    private JButton btnPrint;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
