/*
 * Created by JFormDesigner on Fri Apr 12 15:56:27 CEST 2013
 */

package op.care.info;

import com.jidesoft.swing.JideTabbedPane;
import entity.info.ResInfoCategory;
import entity.info.ResInfoCategoryTools;
import entity.info.Resident;
import op.tools.NursingRecordsPanel;
import op.tools.SYSConst;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Torsten Löhr
 */
public class PnlInformation extends NursingRecordsPanel {
    public static final String internalClassID = "nursingrecords.information";

    private Resident resident;
    private JScrollPane jspSearch;
    private List<ResInfoCategory> listCategories = new ArrayList<ResInfoCategory>();


    public PnlInformation(Resident resident, JScrollPane jspSearch) {
        this.resident = resident;
        this.jspSearch = jspSearch;
        initComponents();
        initPanel();
    }

    public void initPanel() {
        tabCats.setFont(SYSConst.ARIAL14);
        tabCats.setSelectedTabFont(SYSConst.ARIAL14BOLD);
        tabCats.setColorTheme(JideTabbedPane.COLOR_THEME_DEFAULT);
        tabCats.setTabColorProvider(JideTabbedPane.ONENOTE_COLOR_PROVIDER);

        reload();
    }

    private void refreshData() {
        cleanup();
        listCategories.addAll(ResInfoCategoryTools.getAll4ResInfo());
    }

    private void reloadDisplay() {
        for (ResInfoCategory cat : listCategories) {
            tabCats.addTab(cat.getText(), new JPanel());
        }
    }


    @Override
    public void switchResident(Resident resident) {
        this.resident = resident;
        reload();
    }

    @Override
    public void cleanup() {
        tabCats.removeAll();
        listCategories.clear();
    }

    @Override
    public void reload() {
        refreshData();
        reloadDisplay();
    }

    @Override
    public String getInternalClassID() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        tabCats = new JideTabbedPane();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(tabCats);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JideTabbedPane tabCats;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
