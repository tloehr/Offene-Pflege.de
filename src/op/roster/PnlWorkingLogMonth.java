/*
 * Created by JFormDesigner on Tue Oct 15 16:55:37 CEST 2013
 */

package op.roster;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import entity.roster.RosterParameters;
import entity.roster.Rplan;
import entity.roster.UserContracts;
import op.tools.SYSCalendar;
import op.tools.SYSConst;
import org.apache.commons.collections.Closure;
import org.joda.time.LocalDate;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormatSymbols;
import java.util.*;

/**
 * @author Torsten Löhr
 */
public class PnlWorkingLogMonth extends JPanel {
    private ArrayList<Rplan> listPlans;
    private RosterParameters rosterParameters;
    private UserContracts userContracts;
    private HashMap<LocalDate, Rplan> lookup;
    private LocalDate month;
//    private Users user;


    public PnlWorkingLogMonth(ArrayList<Rplan> listPlans, RosterParameters rosterParameters, UserContracts userContracts) {
        this.listPlans = listPlans;
        this.rosterParameters = rosterParameters;
        this.userContracts = userContracts;
        lookup = new HashMap<LocalDate, Rplan>();

        for (Rplan rplan : listPlans) {
            lookup.put(new LocalDate(rplan.getStart()), rplan);
        }

        month = SYSCalendar.bom(new LocalDate(listPlans.get(0).getStart()));

        Collections.sort(listPlans);
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
            "8*(pref:grow, $lcgap), default",
            "2*(default, $lgap), 3*(pref:grow, $lgap), 5*(default:grow, $lgap), default"));

        //---- lblUser ----
        lblUser.setText("text");
        lblUser.setHorizontalAlignment(SwingConstants.CENTER);
        lblUser.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblUser, CC.xywh(1, 1, 15, 1));

        //---- lblMonth ----
        lblMonth.setText("text");
        lblMonth.setHorizontalAlignment(SwingConstants.CENTER);
        lblMonth.setFont(new Font("Arial", Font.PLAIN, 18));
        add(lblMonth, CC.xywh(1, 3, 15, 1));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    public Collection<Rplan> getRPlans(){
        return lookup.values();
    }

    void initPanel() {

//        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");

        lblUser.setText(listPlans.get(0).getOwner().getFullname());
        lblMonth.setText(month.toString("MMMM yyyy"));

        DateFormatSymbols symbols = new DateFormatSymbols(new Locale(getDefaultLocale().getLanguage()));
        String[] daynames = new String[]{
                symbols.getShortWeekdays()[Calendar.MONDAY],
                symbols.getShortWeekdays()[Calendar.TUESDAY],
                symbols.getShortWeekdays()[Calendar.WEDNESDAY],
                symbols.getShortWeekdays()[Calendar.THURSDAY],
                symbols.getShortWeekdays()[Calendar.FRIDAY],
                symbols.getShortWeekdays()[Calendar.SATURDAY],
                symbols.getShortWeekdays()[Calendar.SUNDAY]
        };


        for (int day = 0; day < 7; day++) {
            JLabel lbl = new JLabel(daynames[day]);
            lbl.setFont(SYSConst.ARIAL18BOLD);
            add(lbl, CC.xy(day * 2 + 3, 5, CellConstraints.CENTER, CellConstraints.CENTER));
        }

        int posx = month.getDayOfWeek() * 2 + 1;
        int posy = 7;

        int kw = month.getWeekOfWeekyear();
        JLabel lblkw = new JLabel("KW" + kw);
        lblkw.setFont(SYSConst.ARIAL18BOLD);
        add(lblkw, CC.xy(1, 7));

        for (int day = 1; day <= month.dayOfMonth().withMaximumValue().getDayOfMonth(); day++) {
            final LocalDate date = month.plusDays(day - 1);
            if (lookup.containsKey(month.plusDays(day - 1))) {

                // new PnlWorkingLogDay(lookup.get(date), rosterParameters, userContracts.getParameterSet(date), new Closure() {
//                                    @Override
//                                    public void execute(Object o) {
//                                        if (o != null){
//                                            lookup.put(date, (Rplan) o);
//                                        }
//                                    }
//                                }

                add(new PnlControllerView(lookup.get(date), rosterParameters, userContracts.getParameterSet(date)), CC.xy(posx, posy, CC.FILL, CC.FILL));
            } else {
                JPanel blackpanel = new JPanel();
                blackpanel.setBackground(Color.black);
                add(blackpanel, CC.xy(posx, posy, CC.FILL, CC.FILL));
            }

            posx = posx + 2;

            if (posx > 15) {
                posx = 3;
                posy = posy + 2;

                kw = month.plusWeeks(posy / 2).getWeekOfWeekyear() - 1;
                JLabel lbl2 = new JLabel("KW" + kw);
                lbl2.setFont(SYSConst.ARIAL18BOLD);
                add(lbl2, CC.xy(1, posy));
            }
        }
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblUser;
    private JLabel lblMonth;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
