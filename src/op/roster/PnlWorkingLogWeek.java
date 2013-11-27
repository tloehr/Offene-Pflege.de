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
        lblUser = new JLabel();
        lblMonth = new JLabel();

        //======== this ========
        setLayout(new FormLayout(
            "default:grow, $rgap, default:grow",
            "default, $lgap, fill:default, $lgap, pref, 3*($lgap, default)"));

        //---- lblUser ----
        lblUser.setText("text");
        lblUser.setHorizontalAlignment(SwingConstants.CENTER);
        lblUser.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblUser, CC.xywh(1, 1, 3, 1));

        //---- lblMonth ----
        lblMonth.setText("text");
        lblMonth.setHorizontalAlignment(SwingConstants.CENTER);
        lblMonth.setFont(new Font("Arial", Font.PLAIN, 18));
        add(lblMonth, CC.xywh(1, 3, 3, 1));
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

        int posy = 3;
        int posx = 1;
        for (int day = 0; day < 7; day++) {
            final LocalDate date = week.plusDays(day);
            posy += 2;

            if (posy > 11) {
                posy = 5;
                posx = 3;
            }

            if (lookup.containsKey(date)) {
                add(new PnlWorkingLogDay(lookup.get(date), rosterParameters, userContracts.getParameterSet(date), new Closure() {
                    @Override
                    public void execute(Object o) {
                        if (o != null) {
                            lookup.put(date, (Rplan) o);
                        }
                    }
                }), CC.xy(posx, posy, CC.FILL, CC.FILL));
            } else {
                add(new JLabel("--"), CC.xy(posx, posy, CC.FILL, CC.FILL));
            }


        }


    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblUser;
    private JLabel lblMonth;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
