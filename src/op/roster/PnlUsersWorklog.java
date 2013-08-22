/*
 * Created by JFormDesigner on Thu Aug 15 16:52:39 CEST 2013
 */

package op.roster;

import com.jidesoft.grid.JideTable;
import com.jidesoft.grid.TableScrollPane;
import com.jidesoft.grid.TableUtils;
import com.jidesoft.pane.CollapsiblePane;
import entity.roster.*;
import op.OPDE;
import op.tools.CleanablePanel;
import org.joda.time.DateMidnight;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
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

        DateMidnight month = new DateMidnight(2013, 6, 1);

        ArrayList<RPlan> list = RPlanTools.get4Month(month);

        final TMRoster tmRoster = new TMRoster(list, month);
        TMRosterHeader tmRosterHeader = new TMRosterHeader(tmRoster);
        TMRosterFooter tmRosterFooter = new TMRosterFooter(tmRoster);

        tsp1 = new TableScrollPane(tmRoster, tmRosterHeader, tmRosterFooter, false);

        tsp1.getColumnHeaderTable().setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tsp1.getColumnFooterTable().setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tsp1.getMainTable().setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

//        tsp1.getMainTable().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tsp1.getRowHeaderTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tsp1.getRowFooterTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

//
//        TableUtils.autoResizeAllColumns(tsp1.getMainTable(), tmRoster.getMinimumWidths(), tmRoster.getMaximumWidths(), false, false);
//
//        tsp1.addComponentListener(new ComponentAdapter() {
//            @Override
//            public void componentResized(ComponentEvent e) {
//                OPDE.debug("resized");
//                SwingUtilities.invokeLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        TableUtils.autoResizeAllColumns(tsp1.getMainTable(), tmRoster.getMinimumWidths(), tmRoster.getMaximumWidths(), false, false);
//                    }
//                });
//
//            }
//        });

//        tsp1.getRowHeaderTable().putClientProperty(TableUtils.CLIENT_PROPERTY_AUTO_RESIZE_HIGH_PERFORMANCE, Boolean.FALSE);
//        tsp1.getMainTable().putClientProperty(TableUtils.CLIENT_PROPERTY_AUTO_RESIZE_HIGH_PERFORMANCE, Boolean.FALSE);
//        tsp1.getRowFooterTable().putClientProperty(TableUtils.CLIENT_PROPERTY_AUTO_RESIZE_HIGH_PERFORMANCE, Boolean.FALSE);

//        TableUtils.autoResizeAllColumns(tsp1.getRowHeaderTable());
//        TableUtils.autoResizeAllColumns(tsp1.getMainTable());


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
