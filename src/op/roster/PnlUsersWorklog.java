/*
 * Created by JFormDesigner on Thu Aug 15 16:52:39 CEST 2013
 */

package op.roster;

import com.jidesoft.pane.*;
import com.jidesoft.swing.*;
import entity.roster.RPlan;
import entity.roster.RPlanTools;
import entity.roster.TMRoster;
import op.tools.CleanablePanel;
import org.joda.time.DateMidnight;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Torsten Löhr
 */
public class PnlUsersWorklog  extends CleanablePanel {

    private Map<String, CollapsiblePane> cpMap;
    private Map<String, JPanel> contentmap;

    public PnlUsersWorklog() {
        initComponents();
        initPanel();
    }

    private void initPanel(){

        DateMidnight month = new DateMidnight(2013,6,1);

        ArrayList<RPlan> list = RPlanTools.get4Month(month);

        TMRoster tmRoster = new TMRoster(list, month);

        tblRoster.setModel(tmRoster);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jideTabbedPane1 = new JideTabbedPane();
        scrollPane1 = new JScrollPane();
        tblRoster = new JTable();
        jspContent = new JScrollPane();
        cpsMain = new CollapsiblePanes();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== jideTabbedPane1 ========
        {

            //======== scrollPane1 ========
            {
                scrollPane1.setViewportView(tblRoster);
            }
            jideTabbedPane1.addTab("text", scrollPane1);

            //======== jspContent ========
            {

                //======== cpsMain ========
                {
                    cpsMain.setLayout(new BoxLayout(cpsMain, BoxLayout.X_AXIS));
                }
                jspContent.setViewportView(cpsMain);
            }
            jideTabbedPane1.addTab("text", jspContent);
        }
        add(jideTabbedPane1);
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
    private JideTabbedPane jideTabbedPane1;
    private JScrollPane scrollPane1;
    private JTable tblRoster;
    private JScrollPane jspContent;
    private CollapsiblePanes cpsMain;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
