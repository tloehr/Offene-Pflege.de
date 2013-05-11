/*
 * Created by JFormDesigner on Fri Apr 12 15:56:27 CEST 2013
 */

package op.care.info;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.swing.JideBoxLayout;
import entity.info.*;
import op.OPDE;
import op.tools.GUITools;
import op.tools.NursingRecordsPanel;
import op.tools.SYSConst;
import org.jdesktop.swingx.VerticalLayout;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Torsten Löhr
 */
public class PnlInformation extends NursingRecordsPanel {
    public static final String internalClassID = "nursingrecords.information";

    private Resident resident;
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private List<ResInfoCategory> listCategories = new ArrayList<ResInfoCategory>();
    private CollapsiblePanes cpsAll;
    private ArrayList<ResInfo> listInfo;


    public PnlInformation(Resident resident, JScrollPane jspSearch) {
        this.resident = resident;
        this.jspSearch = jspSearch;
        initComponents();
        initPanel();
    }

    public void initPanel() {
        cpsAll = new CollapsiblePanes();
        jspMain.setViewportView(cpsAll);
        prepareSearchArea();
        reload();
    }

    private void refreshData() {
        cleanup();
        listCategories.addAll(ResInfoCategoryTools.getAll4ResInfo());
//        listInfo = ResInfoTools.getAllActive(resident);
    }

    private void reloadDisplay() {
        cpsAll.setLayout(new JideBoxLayout(cpsAll, JideBoxLayout.Y_AXIS));
        for (ResInfoCategory cat : listCategories) {

            CollapsiblePane cpCat = new CollapsiblePane(cat.getText());
            cpCat.setFont(SYSConst.ARIAL18BOLD);

            JPanel pnlTypes = new JPanel();
            pnlTypes.setLayout(new BoxLayout(pnlTypes, BoxLayout.Y_AXIS));


            for (ResInfoType resInfoType : ResInfoTypeTools.getByCat(cat)) {
                CollapsiblePane cpResInfoType = new CollapsiblePane(resInfoType.getShortDescription());

                JPanel pnlInfos = new JPanel();
                pnlInfos.setLayout(new BoxLayout(pnlInfos, BoxLayout.Y_AXIS));
                CollapsiblePanes cpsType = new CollapsiblePanes();
                pnlInfos.add(cpsType);

                cpResInfoType.setContentPane(pnlInfos);

                for (ResInfo info : ResInfoTools.getAll(resident, resInfoType)) {

                    CollapsiblePane cpInfo = new CollapsiblePane(info.getTitle());
                    cpInfo.setContentPane(new PnlEditResInfo(info, null).getPanel());
                    cpsType.add(cpInfo);

                }
                cpsType.addExpansion();

                pnlTypes.add(cpResInfoType);

            }

            cpCat.setContentPane(pnlTypes);

            cpsAll.add(cpCat);

        }
        cpsAll.addExpansion();
    }


    @Override
    public void switchResident(Resident resident) {
        this.resident = resident;
        GUITools.setResidentDisplay(resident);
        reload();
    }

    @Override
    public void cleanup() {
        cpsAll.removeAll();
        listCategories.clear();
    }

    @Override
    public void reload() {
        refreshData();
        reloadDisplay();
    }

    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);

        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout());
        mypanel.setBackground(Color.WHITE);

        CollapsiblePane searchPane = new CollapsiblePane(OPDE.lang.getString(internalClassID));
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);


        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }


//            GUITools.addAllComponents(mypanel, addFilters());
//            GUITools.addAllComponents(mypanel, addKey());

        searchPane.setContentPane(mypanel);

        searchPanes.add(searchPane);
        searchPanes.addExpansion();
    }

    @Override
    public String getInternalClassID() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jspMain = new JScrollPane();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(jspMain);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane jspMain;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
