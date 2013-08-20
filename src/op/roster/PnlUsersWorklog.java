/*
 * Created by JFormDesigner on Thu Aug 15 16:52:39 CEST 2013
 */

package op.roster;

import com.jidesoft.grid.*;
import com.jidesoft.grid.JideTable;
import com.jidesoft.grid.NestedTableHeader;
import com.jidesoft.grid.TableColumnGroup;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.swing.JideTabbedPane;
import entity.roster.*;
import op.tools.CleanablePanel;
import org.joda.time.DateMidnight;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
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

        TMRoster tmRoster = new TMRoster(list, month);
        TMRosterHeader tmRosterHeader = new TMRosterHeader(tmRoster);
        TMRosterFooter tmRosterFooter = new TMRosterFooter(tmRoster);

//        tblRoster.setModel(tmRoster);
//
//        tblRoster.setAutoResizeMode(JideTable.AUTO_RESIZE_FILL);

        tsp1 = new TableScrollPane(tmRoster, tmRosterHeader, tmRosterFooter, true);


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
