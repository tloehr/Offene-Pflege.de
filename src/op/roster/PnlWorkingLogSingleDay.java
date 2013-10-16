package op.roster;

import entity.roster.Workinglog;
import entity.roster.WorkinglogTools;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.joda.time.LocalDate;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 16.10.13
 * Time: 15:55
 * To change this template use File | Settings | File Templates.
 */
public class PnlWorkingLogSingleDay extends JPanel {
    private JList listLogs;
    private LocalDate day;
//    private ArrayList<Workinglog> lstLogs;

    public PnlWorkingLogSingleDay(LocalDate day) {
        super();
        this.day = day;
        initPanel();
    }

    void initPanel() {
//        lstLogs = WorkinglogTools.getAll(day, day);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        listLogs = new JList(SYSTools.list2dlm(WorkinglogTools.getAll(day, day)));
        JScrollPane scrl = new JScrollPane();
        scrl.setViewportView(listLogs);
        add(scrl);
        add(new JButton(SYSConst.icon22add));
    }
}
