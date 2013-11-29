/*
 * Created by JFormDesigner on Wed Nov 27 12:13:22 CET 2013
 */

package op.roster;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.roster.RPlanTools;
import entity.roster.RosterParameters;
import entity.roster.Rplan;
import entity.roster.UserContracts;
import entity.system.Users;
import op.tools.SYSCalendar;
import org.apache.commons.collections.Closure;
import org.joda.time.LocalDate;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * @author Torsten Löhr
 */
public class PnlWorkingLogWeek extends JPanel {
    //    private ArrayList<Rplan> listPlans;
    private RosterParameters rosterParameters;
    private UserContracts userContracts;
    private HashMap<LocalDate, Rplan> lookup;
    private Users user;
    private LocalDate week;

    public PnlWorkingLogWeek(Users user, LocalDate week, RosterParameters rosterParameters, UserContracts userContracts) {
        super();
        this.user = user;
        this.week = SYSCalendar.bow(week);
        this.rosterParameters = rosterParameters;
        this.userContracts = userContracts;
        initData();

//        Collections.sort(listPlans);
        //        this.user = user;
        //        this.month = month.dayOfMonth().withMinimumValue();
        initComponents();
        initPanel();

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        scrollPane1 = new JScrollPane();
        pnlWeek = new JPanel();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== scrollPane1 ========
        {

            //======== pnlWeek ========
            {
                pnlWeek.setLayout(new FormLayout(
                    "default:grow, $lcgap, default:grow",
                    "3*(default, $lgap), default"));
            }
            scrollPane1.setViewportView(pnlWeek);
        }
        add(scrollPane1);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private void initData() {
        lookup = new HashMap<LocalDate, Rplan>();

        for (Rplan rplan : RPlanTools.getAllInWeek(week, user)) {
            lookup.put(new LocalDate(rplan.getStart()), rplan);
        }
    }

    private void initPanel() {
        lblUser.setText(user.getFullname());
        lblMonth.setText("KW" + week.toString("ww/yyyy"));

        int posy = -1;
        int posx = 1;
        for (int day = 0; day < 7; day++) {
            final LocalDate date = week.plusDays(day);
            posy += 2;

            if (posy > 7) {
                posy = 1;
                posx = 3;
            }

            if (lookup.containsKey(date)) {
                pnlWeek.add(new PnlWorkingLogDay(lookup.get(date), rosterParameters, userContracts.getParameterSet(date), new Closure() {
                    @Override
                    public void execute(Object o) {
                        if (o != null) {
                            lookup.put(date, (Rplan) o);
                        }
                    }
                }), CC.xy(posx, posy, CC.FILL, CC.FILL));
            } else {
                pnlWeek.add(new JLabel("--"), CC.xy(posx, posy, CC.FILL, CC.FILL));
            }


        }


    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane scrollPane1;
    private JPanel pnlWeek;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
