/*
 * Created by JFormDesigner on Sat May 17 15:36:50 CEST 2014
 */

package op.training;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.swing.JideBoxLayout;
import entity.staff.Training;
import entity.staff.TrainingTools;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.*;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.LocalDate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Torsten Löhr
 */
public class PnlTraining extends CleanablePanel {
    public static final String internalClassID = "opde.training";
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private Map<String, CollapsiblePane> cpMap;
    private Map<String, JPanel> contentmap;

    public PnlTraining(JScrollPane jspSearch) {
        initComponents();
        this.jspSearch = jspSearch;
        initPanel();
        reload();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jspMain = new JScrollPane();
        cpsMain = new CollapsiblePanes();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== jspMain ========
        {
            jspMain.setViewportView(cpsMain);
        }
        add(jspMain);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private void initPanel() {
        contentmap = Collections.synchronizedMap(new HashMap<String, JPanel>());
        cpMap = Collections.synchronizedMap(new HashMap<String, CollapsiblePane>());
        prepareSearchArea();
        OPDE.getDisplayManager().setMainMessage(internalClassID);

    }

    @Override
    public void cleanup() {

    }

    @Override
    public void reload() {
        synchronized (contentmap) {
            SYSTools.clear(contentmap);
        }
        synchronized (cpMap) {
            SYSTools.clear(cpMap);
        }
        OPDE.getMainframe().setBlocked(true);
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(OPDE.lang.getString("misc.msg.wait"), -1, 100));

        SwingWorker worker = new SwingWorker() {
            Date max = null;

            @Override
            protected Object doInBackground() throws Exception {

                Pair<LocalDate, LocalDate> minmax = TrainingTools.getMinMax();

                if (minmax != null) {
                    max = minmax.getSecond().toDate();
                    LocalDate start = minmax.getFirst().dayOfYear().withMinimumValue();
                    LocalDate end = minmax.getSecond().dayOfYear().withMinimumValue();
                    for (int year = end.getYear(); year >= start.getYear(); year--) {
                        createCP4Year(year);
                    }
                }

                return null;
            }

            @Override
            protected void done() {

                buildPanel();
//                initPhase = false;
                OPDE.getDisplayManager().setProgressBarMessage(null);
                OPDE.getMainframe().setBlocked(false);

            }
        };
        worker.execute();
    }


    private CollapsiblePane createCP4Year(final int year) {
        final String keyYear = Integer.toString(year) + ".year";
        synchronized (cpMap) {
            if (!cpMap.containsKey(keyYear)) {
                cpMap.put(keyYear, new CollapsiblePane());
                try {
                    cpMap.get(keyYear).setCollapsed(true);
                } catch (PropertyVetoException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

            }
        }

        final CollapsiblePane cpYear = cpMap.get(keyYear);

        String title = "<html><font size=+1>" +
                "<b>" + Integer.toString(year) + "</b>" +
                "</font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cpYear.setCollapsed(!cpYear.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            }
        });


        cpYear.setTitleLabelComponent(cptitle.getMain());
        cpYear.setSlidingDirection(SwingConstants.SOUTH);
        cpYear.setBackground(SYSConst.orange1[SYSConst.medium3]);
        cpYear.setOpaque(true);

        cpYear.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                JPanel pnlContent = new JPanel(new VerticalLayout());

                for (Training training : TrainingTools.getTrainings4(year)) {
                    pnlContent.add(createCP4(training));
                }

                cpYear.setContentPane(pnlContent);

            }
        });
        //        cpYear.setBackground(getColor(vtype, SYSConst.light4));

        if (!cpYear.isCollapsed()) {
            JPanel pnlContent = new JPanel(new VerticalLayout());

            for (Training training : TrainingTools.getTrainings4(year)) {
                pnlContent.add(createCP4(training));
            }

            cpYear.setContentPane(pnlContent);
            cpYear.setOpaque(false);
        }

        cpYear.setHorizontalAlignment(SwingConstants.LEADING);
        cpYear.setOpaque(false);

        return cpYear;
    }


    private CollapsiblePane createCP4(final Training training) {
        final String key = training.getId() + ".month";
        synchronized (cpMap) {
            if (!cpMap.containsKey(key)) {
                cpMap.put(key, new CollapsiblePane());
                try {
                    cpMap.get(key).setCollapsed(true);
                } catch (PropertyVetoException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

            }
        }
        final CollapsiblePane cpTraining = cpMap.get(key);

        String title = "<html><font size=+1><b>" +
                training.getTitle() + DateFormat.getDateInstance(DateFormat.SHORT).format(training.getDate()) +
                "</b>" +
                "</font></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cpTraining.setCollapsed(!cpTraining.isCollapsed());
                } catch (PropertyVetoException pve) {
                    // BAH!
                }
            }
        });

        cpTraining.setTitleLabelComponent(cptitle.getMain());
        cpTraining.setSlidingDirection(SwingConstants.SOUTH);
        cpTraining.setBackground(SYSConst.orange1[SYSConst.medium2]);
        cpTraining.setOpaque(true);
        cpTraining.setHorizontalAlignment(SwingConstants.LEADING);

//           cpTraining.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
//               @Override
//               public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
//                   cpTraining.setContentPane(createContentPanel4Month(month));
//               }
//           });
//
//           if (!cpTraining.isCollapsed()) {
//               cpTraining.setContentPane(createContentPanel4Month(month));
//           }


        return cpTraining;
    }


    private void buildPanel() {
        cpsMain.removeAll();
        cpsMain.setLayout(new JideBoxLayout(cpsMain, JideBoxLayout.Y_AXIS));

        synchronized (cpMap) {
            Pair<LocalDate, LocalDate> minmax = TrainingTools.getMinMax();
            if (minmax != null) {
                LocalDate start = minmax.getFirst().dayOfYear().withMinimumValue();
                LocalDate end = minmax.getSecond().dayOfYear().withMinimumValue();
                for (int year = end.getYear(); year >= start.getYear(); year--) {
                    final String keyYear = Integer.toString(year) + ".year";

                    cpsMain.add(cpMap.get(keyYear));
                }
            }
        }
        cpsMain.addExpansion();
    }

    private void prepareSearchArea() {

        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);

        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout(5));
        mypanel.setBackground(Color.WHITE);

        CollapsiblePane searchPane = new CollapsiblePane(OPDE.lang.getString(internalClassID));
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);

        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }

//           GUITools.addAllComponents(mypanel, addCommands());
//           GUITools.addAllComponents(mypanel, addFilters());

        searchPane.setContentPane(mypanel);

        searchPanes.add(searchPane);
        searchPanes.addExpansion();


    }

    @Override
    public String getInternalClassID() {
        return null;
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane jspMain;
    private CollapsiblePanes cpsMain;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
