/*
 * Created by JFormDesigner on Mon Mar 03 15:17:40 CET 2014
 */

package op.care.values;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.grid.TableUtils;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import entity.files.SYSFilesTools;
import entity.files.SYSVAL2FILE;
import entity.info.Resident;
import entity.info.ResidentTools;
import entity.process.SYSVAL2PROCESS;
import entity.values.ResValue;
import entity.values.ResValueTools;
import entity.values.ResValueTypes;
import entity.values.ResValueTypesTools;
import gui.GUITools;
import op.OPDE;
import op.threads.DisplayManager;
import op.threads.DisplayMessage;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.LocalDate;
import tablerenderer.RNDHTML;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
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
    private ArrayList<Object[]> listSummaries;
    private LocalDate startDay;
    private DateFormat df;
    private NumberFormat nf;
    private final ResValueTypes LIQUIDBALANCE;
    private ArrayList<LocalDate> listOfStartDays;
    private BigDecimal targetIn, highIn, lowIn;
    private Properties controlProps;

    public PnlLiquidBalance(Resident resident, JScrollPane jspSearch) {
        super("nursingrecords.liquidbalances");
        this.resident = resident;
        this.jspSearch = jspSearch;
        LIQUIDBALANCE = ResValueTypesTools.getType(ResValueTypesTools.LIQUIDBALANCE);
        listOfStartDays = new ArrayList<>();
        initComponents();
        initPanel();
        prepareSearchArea();
        switchResident(resident);
    }

    private void initPanel() {
        startDay = new LocalDate();
        lblLeft.setText(SYSTools.xx("nursingrecords.liquidbalances.summary"));
        lblRight.setText(SYSTools.xx("nursingrecords.liquidbalances.details"));
        lblLimits.setText(null);

        df = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT);
        nf = DecimalFormat.getNumberInstance();
    }

    private void btnTodayActionPerformed(ActionEvent e) {
        startDay = new LocalDate();
        listOfStartDays.clear();
        reload();
    }

    private void btnBackActionPerformed(ActionEvent e) {
        int pos = listOfStartDays.indexOf(startDay);
        if (pos > 0) {
            startDay = listOfStartDays.get(pos - 1);
        } else {
            startDay = new LocalDate();
        }
        reload();
    }

    private void btnForwardActionPerformed(ActionEvent e) {
        if (listSummaries == null || listSummaries.isEmpty()) return;

        int pos = listOfStartDays.indexOf(startDay);
        if (!listOfStartDays.contains(startDay) || pos == listOfStartDays.size() - 1) {
            startDay = new LocalDate(((java.sql.Date) listSummaries.get(listSummaries.size() - 1)[0]).getTime()).minusDays(1);
            listOfStartDays.add(startDay);
        } else {
            startDay = listOfStartDays.get(pos + 1);
        }
        reload();
    }

    private void btnPrintActionPerformed(ActionEvent e) {
        if (listValues == null || listValues.isEmpty()) return;
        SYSFilesTools.print(SYSTools.toHTML(ResValueTools.getAsHTML(listValues)), true);
    }

    private void scrlLeftComponentResized(ComponentEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                tblLeft.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

                if (highIn != null || lowIn != null) {
                    TableUtils.autoResizeAllColumns(tblLeft, null, new int[]{100, 100, 100, 100}, true, false);
                } else {
                    TableUtils.autoResizeAllColumns(tblLeft, null, new int[]{100, 100, 100, 100, 100}, true, false);
                }

                TableUtils.autoResizeAllRows(tblLeft);

            }
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
            btnToday.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnTodayActionPerformed(e);
                }
            });
            panel1.add(btnToday);

            //---- btnBack ----
            btnBack.setText(null);
            btnBack.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/1leftarrow.png")));
            btnBack.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnBackActionPerformed(e);
                }
            });
            panel1.add(btnBack);

            //---- btnForward ----
            btnForward.setText(null);
            btnForward.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/1rightarrow.png")));
            btnForward.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnForwardActionPerformed(e);
                }
            });
            panel1.add(btnForward);
        }
        add(panel1, CC.xy(1, 7, CC.CENTER, CC.DEFAULT));

        //======== panel2 ========
        {
            panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

            //---- btnPrint ----
            btnPrint.setText(null);
            btnPrint.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/printer1.png")));
            btnPrint.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btnPrintActionPerformed(e);
                }
            });
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
        controlProps = resident.getControlling();
        lowIn = SYSTools.parseDecimal(controlProps.getProperty(ResidentTools.KEY_LOWIN));
        targetIn = SYSTools.parseDecimal(controlProps.getProperty(ResidentTools.KEY_TARGETIN));
        highIn = SYSTools.parseDecimal(controlProps.getProperty(ResidentTools.KEY_HIGHIN));

    }

    @Override
    public void cleanup() {
        SYSTools.clear(listValues);
        SYSTools.clear(listSummaries);
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
        listSummaries = ResValueTools.getLiquidBalances(resident, startDay, 20);

        for (Object[] obj : listSummaries) {
            Vector line = new Vector();
            line.add(new LocalDate(((java.sql.Date) obj[0]).getTime()));
            BigDecimal liquidin = (BigDecimal) obj[1];
            line.add(liquidin);

            line.add(obj[2]);


            BigDecimal liquidresult = (BigDecimal) obj[3];
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
        headerLeft.add(SYSTools.xx("misc.msg.ingestion"));
        headerLeft.add(SYSTools.xx("misc.msg.egestion"));
        headerLeft.add(SYSTools.xx("misc.msg.liquid.result"));
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
        tblLeft.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                int minIndex = lsm.getMinSelectionIndex();
                int maxIndex = lsm.getMaxSelectionIndex();

                if (minIndex < 0 || maxIndex < 0) return;

                cleanup();

                loadRightTable((LocalDate) tblLeft.getModel().getValueAt(maxIndex, 0), (LocalDate) tblLeft.getModel().getValueAt(minIndex, 0));
            }
        });
        tblLeft.getColumnModel().getColumn(0).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object o, boolean isSelected, boolean hasFocus, int row, int column) {
                String text;
                if (o == null) {
                    text = SYSTools.xx("misc.commands.>>noselection<<");
                } else if (o instanceof LocalDate) {
                    text = ((LocalDate) o).toString("dd.MM.yyyy");
                } else {
                    text = o.toString();
                }
                return new DefaultTableCellRenderer().getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);
            }
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

    void loadRightTable(final LocalDate from, final LocalDate to) {

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
            data[row][3] = SYSTools.anonymizeUser(val.getUser().getUID());
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
                new DlgYesNo(SYSTools.xx("misc.questions.delete1") + "<br/><i>" + "<br/><i>" + df.format(val2Delete.getPit()) + "<br/>" + nf.format(val2Delete.getVal1()) + " ml<br/>" + val2Delete.getUser().toString() + "</i><br/>" + SYSTools.xx("misc.questions.delete2"), SYSConst.icon48delete, new Closure() {
                    @Override
                    public void execute(Object answer) {
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
                            }


                        }
                    }
                });
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

        JideButton addButton = GUITools.createHyperlinkButton(SYSTools.xx("misc.commands.new"), new ImageIcon(getClass().getResource("/artwork/22x22/bw/add.png")), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!resident.isActive()) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.cantChangeInactiveResident"));
                    return;
                }
                new DlgValue(new ResValue(resident, LIQUIDBALANCE), DlgValue.MODE_NEW, new Closure() {
                    @Override
                    public void execute(Object o) {
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

                            startDay = new LocalDate(myValue.getPit());
                            loadLeftTable();
                            loadRightTable(startDay, startDay);

                        }

                    }
                });

            }
        });
        list.add(addButton);


        JideButton controlButton = GUITools.createHyperlinkButton(SYSTools.xx("nursingrecords.vitalparameters.btnControlling.tooltip"), SYSConst.icon22magnify1, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!resident.isActive()) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.cantChangeInactiveResident"));
                    return;
                }
                new DlgValueControl(resident, new Closure() {
                    @Override
                    public void execute(Object o) {
                        if (o != null) {
                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();
                                Resident myResident = em.merge(resident);
                                em.lock(myResident, LockModeType.OPTIMISTIC);
                                myResident.setControlling((Properties) o);
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

                        }
                    }
                });
            }
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
