/*
 * Created by JFormDesigner on Thu Aug 15 16:52:39 CEST 2013
 */

package op.roster;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.grid.TableScrollPane;
import com.jidesoft.pane.CollapsiblePane;
import entity.roster.Rosters;
import entity.roster.RostersTools;
import op.OPDE;
import op.tools.CleanablePanel;
import op.tools.SYSCalendar;
import op.tools.SYSTools;
import org.joda.time.DateMidnight;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Torsten Löhr
 */
public class PnlUsersWorklog extends CleanablePanel {

    private Map<String, CollapsiblePane> cpMap;
    private Map<String, JPanel> contentmap;
    private TableScrollPane tsp1;
    private ArrayList<Rosters> lstAllRosters;

    public PnlUsersWorklog() {
        initComponents();
        initPanel();
    }

    private void initPanel() {

        EntityManager em = OPDE.createEM();
        lstAllRosters = new ArrayList<Rosters>(em.createQuery("SELECT r FROM Rosters r ORDER BY r.month DESC").getResultList());
        em.close();

        lstRosters.setModel(SYSTools.list2dlm(lstAllRosters));
        lstRosters.setCellRenderer(RostersTools.getRenderer());

//        DateMidnight month = new DateMidnight(2013, 6, 15);
//
//        Rosters roster = RostersTools.get4Month(month);
//
//        ObjectConverterManager.initDefaultConverter();
//        CellEditorManager.initDefaultEditor();
//
//        ObjectConverterManager.registerConverter(Homes.class, new ObjectConverter() {
//            @Override
//            public String toString(Object o, ConverterContext converterContext) {
//                return o instanceof Homes ? ((Homes) o).getShortname() : "";
//            }
//
//            @Override
//            public boolean supportToString(Object o, ConverterContext converterContext) {
//                return true;
//            }
//
//            @Override
//            public Object fromString(String s, ConverterContext converterContext) {
//                return null;
//            }
//
//            @Override
//            public boolean supportFromString(String s, ConverterContext converterContext) {
//                return false;
//            }
//        });
//
//        CellEditorManager.registerEditor(Homes.class, new CellEditorFactory() {
//            public CellEditor create() {
//                return new ExComboBoxCellEditor() {
//                    @Override
//                    public ExComboBox createExComboBox() {
//                        ExComboBox myEditor = new ListExComboBox(HomesTools.getAll().toArray());
//                        myEditor.setRenderer(HomesTools.getRenderer());
//                        return myEditor;
//                    }
//                };
//            }
//        }, new EditorContext("HomesSelectionEditor"));
//
//        final TMRoster tmRoster = new TMRoster(roster, false);
//
//        TMRosterHeader tmRosterHeader = new TMRosterHeader(tmRoster);
//        TMRosterFooter tmRosterFooter = new TMRosterFooter(tmRoster);
//
//        tsp1 = new TableScrollPane(tmRoster, tmRosterHeader, tmRosterFooter, false);
//
//        tsp1.getColumnHeaderTable().setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
//        tsp1.getColumnFooterTable().setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
//        tsp1.getMainTable().setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
//
//        tsp1.getRowHeaderTable().getColumnModel().getColumn(0).setCellRenderer(new TableCellRenderer() {
//            @Override
//            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//                StyledLabel lbl = StyledLabelBuilder.createStyledLabel(value.toString());
//                lbl.setBackground(((StyleModel) table.getModel()).getCellStyleAt(row,column).getBackground());
//                lbl.setOpaque(true);
//                return lbl;  //To change body of implemented methods use File | Settings | File Templates.
//            }
//        });
//
//        tsp1.getRowHeaderTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//        tsp1.getRowFooterTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//
//        add(tsp1);
    }

    private void lstRostersMouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            final FrmRoster frmRoster = OPDE.getMainframe().addRoster((Rosters) lstRosters.getSelectedValue());
            final int pos = lstAllRosters.indexOf(lstRosters.getSelectedValue());
            lstAllRosters.remove(pos);
            lstAllRosters.add(pos, frmRoster.getRoster());
            lstRosters.setModel(SYSTools.list2dlm(lstAllRosters));
            frmRoster.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    lstAllRosters.remove(pos);
                    lstAllRosters.add(pos, frmRoster.getRoster());
                    lstRosters.setModel(SYSTools.list2dlm(lstAllRosters));
                    super.windowClosed(e);
                }
            });
        }
    }

    private void btnNewRosterActionPerformed(ActionEvent e) {
        LocalDate monthToCreate = null;
        String paramsXML = null;
        if (lstAllRosters.isEmpty()) {
            JComboBox cmbMonth = new JComboBox(SYSCalendar.createMonthList(new DateMidnight().minusYears(1).monthOfYear().withMinimumValue(), new DateMidnight().monthOfYear().withMaximumValue()));
            final Format monthFormatter = new SimpleDateFormat("MMMM yyyy");
            cmbMonth.setRenderer(new ListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    return new DefaultListCellRenderer().getListCellRendererComponent(list, monthFormatter.format(((DateMidnight) value).toDate()), index, isSelected, cellHasFocus);
                }
            });
            cmbMonth.setSelectedItem(new DateMidnight());
            JOptionPane.showMessageDialog(this, cmbMonth);
            monthToCreate = new LocalDate(cmbMonth.getSelectedItem());
            paramsXML = RostersTools.DEFAULT_XML;
        } else {
            monthToCreate = new LocalDate(lstAllRosters.get(lstAllRosters.size() - 1).getMonth()).plusMonths(1);
            paramsXML = lstAllRosters.get(lstAllRosters.size() - 1).getXml();
        }


        EntityManager em = OPDE.createEM();
        em.getTransaction().begin();
        Rosters newRoster = em.merge(new Rosters(monthToCreate, paramsXML));

        grmpf
        // stunden soll für den neuen monat eintragen.
        // ma liste für plan eintragen. auch mehrfach nennnungen erlauben. vielleicht über sysprops.

        em.getTransaction().commit();
        em.close();

        lstAllRosters.add(newRoster);
        lstRosters.setModel(SYSTools.list2dlm(lstAllRosters));


    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        scrollPane1 = new JScrollPane();
        lstRosters = new JList();
        btnNewRoster = new JButton();

        //======== this ========
        setLayout(new FormLayout(
                "default, $lcgap, default:grow, $lcgap, default",
                "default, $lgap, default:grow, 2*($lgap, default)"));

        //======== scrollPane1 ========
        {

            //---- lstRosters ----
            lstRosters.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    lstRostersMouseClicked(e);
                }
            });
            scrollPane1.setViewportView(lstRosters);
        }
        add(scrollPane1, CC.xy(3, 3, CC.DEFAULT, CC.FILL));

        //---- btnNewRoster ----
        btnNewRoster.setText("new roster");
        btnNewRoster.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnNewRosterActionPerformed(e);
            }
        });
        add(btnNewRoster, CC.xy(3, 5));
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
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane scrollPane1;
    private JList lstRosters;
    private JButton btnNewRoster;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
