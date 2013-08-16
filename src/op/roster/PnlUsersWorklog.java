/*
 * Created by JFormDesigner on Thu Aug 15 16:52:39 CEST 2013
 */

package op.roster;

import com.jidesoft.pane.*;
import op.tools.CleanablePanel;
import org.joda.time.DateMidnight;

import javax.swing.*;
import java.util.Map;

/**
 * @author Torsten Löhr
 */
public class PnlUsersWorklog  extends CleanablePanel {

    private Map<String, CollapsiblePane> cpMap;
    private Map<String, JPanel> contentmap;

    public PnlUsersWorklog() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jspContent = new JScrollPane();
        cpsMain = new CollapsiblePanes();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== jspContent ========
        {

            //======== cpsMain ========
            {
                cpsMain.setLayout(new BoxLayout(cpsMain, BoxLayout.X_AXIS));
            }
            jspContent.setViewportView(cpsMain);
        }
        add(jspContent);
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
    private JScrollPane jspContent;
    private CollapsiblePanes cpsMain;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
