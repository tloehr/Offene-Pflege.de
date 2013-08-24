/*
 * Created by JFormDesigner on Thu Aug 15 16:52:39 CEST 2013
 */

package op.roster;

import com.jidesoft.combobox.ExComboBox;
import com.jidesoft.combobox.ListExComboBox;
import com.jidesoft.converter.ConverterContext;
import com.jidesoft.converter.ObjectConverter;
import com.jidesoft.converter.ObjectConverterManager;
import com.jidesoft.grid.*;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.swing.StyledLabel;
import com.jidesoft.swing.StyledLabelBuilder;
import entity.Homes;
import entity.HomesTools;
import entity.roster.*;
import op.OPDE;
import op.tools.CleanablePanel;
import org.joda.time.DateMidnight;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Map;

/**
 * @author Torsten Löhr
 */
public class PnlUsersWorklog extends CleanablePanel {

    private Map<String, CollapsiblePane> cpMap;
    private Map<String, JPanel> contentmap;
    private TableScrollPane tsp1;

    public PnlUsersWorklog() {
        initComponents();
        initPanel();
    }

    private void initPanel() {

        DateMidnight month = new DateMidnight(2013, 6, 15);

        Rosters roster = RostersTools.get4Month(month);

        ObjectConverterManager.initDefaultConverter();
        CellEditorManager.initDefaultEditor();

        ObjectConverterManager.registerConverter(Homes.class, new ObjectConverter() {
            @Override
            public String toString(Object o, ConverterContext converterContext) {
                return o instanceof Homes ? ((Homes) o).getShortname() : "";
            }

            @Override
            public boolean supportToString(Object o, ConverterContext converterContext) {
                return true;
            }

            @Override
            public Object fromString(String s, ConverterContext converterContext) {
                return null;
            }

            @Override
            public boolean supportFromString(String s, ConverterContext converterContext) {
                return false;
            }
        });

        CellEditorManager.registerEditor(Homes.class, new CellEditorFactory() {
            public CellEditor create() {
                return new ExComboBoxCellEditor() {
                    @Override
                    public ExComboBox createExComboBox() {
                        ExComboBox myEditor = new ListExComboBox(HomesTools.getAll().toArray());
                        myEditor.setRenderer(HomesTools.getRenderer());
                        return myEditor;
                    }
                };
            }
        }, new EditorContext("HomesSelectionEditor"));

//        ObjectConverterManager.registerConverter(String.class, new ObjectConverter() {
//            @Override
//            public String toString(Object o, ConverterContext converterContext) {
//                return null;  //To change body of implemented methods use File | Settings | File Templates.
//            }
//
//            @Override
//            public boolean supportToString(Object o, ConverterContext converterContext) {
//                return false;  //To change body of implemented methods use File | Settings | File Templates.
//            }
//
//            @Override
//            public Object fromString(String s, ConverterContext converterContext) {
//                return null;  //To change body of implemented methods use File | Settings | File Templates.
//            }
//
//            @Override
//            public boolean supportFromString(String s, ConverterContext converterContext) {
//                return false;  //To change body of implemented methods use File | Settings | File Templates.
//            }
//        });

        final TMRoster tmRoster = new TMRoster(roster, false);

        TMRosterHeader tmRosterHeader = new TMRosterHeader(tmRoster);
        TMRosterFooter tmRosterFooter = new TMRosterFooter(tmRoster);

        tsp1 = new TableScrollPane(tmRoster, tmRosterHeader, tmRosterFooter, false);

        tsp1.getColumnHeaderTable().setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tsp1.getColumnFooterTable().setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tsp1.getMainTable().setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        tsp1.getRowHeaderTable().getColumnModel().getColumn(0).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                StyledLabel lbl = StyledLabelBuilder.createStyledLabel(value.toString());
                lbl.setBackground(((StyleModel) table.getModel()).getCellStyleAt(row,column).getBackground());
                lbl.setOpaque(true);
                return lbl;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
//
//        tsp1.getMainTable().setDefaultRenderer(String.class, new TableCellRenderer() {
//            @Override
//            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//                OPDE.debug("jkadsdas1");
//                StyledLabel lbl = StyledLabelBuilder.createStyledLabel(value.toString());
//                return lbl;  //To change body of implemented methods use File | Settings | File Templates.
//            }
//        });
//        tsp1.getRowHeaderTable().setDefaultRenderer(String.class, new TableCellRenderer() {
//            @Override
//            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//                OPDE.debug("jkadsdas");
//                StyledLabel lbl = new StyledLabel(value.toString());
//                return lbl;  //To change body of implemented methods use File | Settings | File Templates.
//            }
//        });

//        tsp1.getMainTable().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tsp1.getRowHeaderTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tsp1.getRowFooterTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        add(tsp1);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
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
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
