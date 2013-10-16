/*
 * Created by JFormDesigner on Tue Oct 15 16:55:37 CEST 2013
 */

package op.roster;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import entity.system.Users;
import org.joda.time.LocalDate;

import javax.swing.*;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

/**
 * @author Torsten Löhr
 */
public class PnlWorkingLogWholeMonth extends JPanel {
    private LocalDate month;
    private Users user;


    public PnlWorkingLogWholeMonth(LocalDate month, Users user) {
        this.user = user;
        this.month = month.dayOfMonth().withMinimumValue();
        initComponents();
        initPanel();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents

        //======== this ========
        setLayout(new FormLayout(
            "7*(default, $lcgap), default",
            "6*(default, $lgap), default"));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    void initPanel() {

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
            add(lbl, CC.xy(day * 2 + 3, 1));
        }

        int posx = month.getDayOfWeek() * 2 + 1;
        int posy = 3;

        int kw = month.getWeekOfWeekyear();
        JLabel lblkw = new JLabel("KW" + kw);
        add(lblkw, CC.xy(1, 3));


        for (int day = 1; day <= month.dayOfMonth().withMaximumValue().getDayOfMonth(); day++) {
//            JLabel lbl = new JLabel(day + "");
            add(new PnlWorkingLogSingleDay(month.plusDays(day - 1)), CC.xy(posx, posy));

            posx = posx + 2;

            if (posx > 15) {
                posx = 3;
                posy = posy + 2;

                kw = month.plusWeeks(posy / 2).getWeekOfWeekyear() - 1;
                JLabel lbl2 = new JLabel("KW" + kw);
                add(lbl2, CC.xy(1, posy));
            }

        }

    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
