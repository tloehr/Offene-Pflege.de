package entity.roster;

import entity.Homes;
import entity.system.Users;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StatsPerDay {

    public static final int EXAM = 0;
    public static final int HELPER = 1;
    public static final int SOCIAL = 2;

    public static final int exam_early = 0;
    public static final int exam_late = 1;
    public static final int exam_night = 2;
    public static final int helper_early = 3;
    public static final int helper_late = 4;
    public static final int helper_night = 5;
    public static final int social_early = 6;
    public static final int social_late = 7;
    public static final int social_night = 8;

    //    private ArrayList<BigDecimal> daystats;
    private HashMap<Homes, ArrayList<BigDecimal[]>> daystats;
    //    private ArrayList<Homes> homeslist;
    private LocalDate month;
    private HashMap<Users, UserContracts> contracts;
    private RosterParameters rosterParameters;


    public StatsPerDay(List<Homes> homeslist, LocalDate month, HashMap<Users, ArrayList<Rplan>> content, HashMap<Users, UserContracts> contracts, RosterParameters rosterParameters) {
        this.month = month;
        this.contracts = contracts;
        this.rosterParameters = rosterParameters;
        daystats = new HashMap<Homes, ArrayList<BigDecimal[]>>();

        for (Homes home : homeslist) {
            ArrayList<BigDecimal[]> thisDay = new ArrayList<BigDecimal[]>(month.dayOfMonth().withMaximumValue().getDayOfMonth() - 1);
            daystats.put(home, thisDay);
            for (int i = 0; i < month.dayOfMonth().withMaximumValue().getDayOfMonth(); i++) {
                thisDay.add(null); // to fill the spot in the list
            }
        }

        for (int i = 0; i < month.dayOfMonth().withMaximumValue().getDayOfMonth(); i++) {
            update(content, i);
        }
    }

    private void clear(Homes home, int day) {
        daystats.get(home).set(day, new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO});
    }

    public void update(HashMap<Users, ArrayList<Rplan>> content, int day) {
        for (Homes home : daystats.keySet()) {
            clear(home, day);
        }
        for (Users user : content.keySet()) {
            Rplan rplan = content.get(user).get(day);

            if (rplan != null) {
                boolean exam = contracts.get(user).getParameterSet(month.plusDays(day)).isExam();
                Symbol symbol = rosterParameters.getSymbol(rplan.getEffectiveP());
                int type = symbol.getSection() == RosterXML.SOCIAL ? StatsPerDay.SOCIAL : (exam ? StatsPerDay.EXAM : StatsPerDay.HELPER);
                add(day, rplan.getEffectiveHome(), type, symbol);
            }
        }

    }

    private void add(int day, Homes home, int shift, int type, BigDecimal val) {
        if (shift == Symbol.SHIFT_NONE) return;

        if (shift == Symbol.SHIFT_EARLY) {
            if (type == EXAM) {
                daystats.get(home).get(day)[exam_early] = daystats.get(home).get(day)[exam_early].add(val);
            } else if (type == SOCIAL) {
                daystats.get(home).get(day)[social_early] = daystats.get(home).get(day)[social_early].add(val);
            } else if (type == HELPER) {
                daystats.get(home).get(day)[helper_early] = daystats.get(home).get(day)[helper_early].add(val);
            }
        } else if (shift == Symbol.SHIFT_LATE) {
            if (type == EXAM) {
                daystats.get(home).get(day)[exam_late] = daystats.get(home).get(day)[exam_late].add(val);
            } else if (type == SOCIAL) {
                daystats.get(home).get(day)[social_late] = daystats.get(home).get(day)[social_late].add(val);
            } else if (type == HELPER) {
                daystats.get(home).get(day)[helper_late] = daystats.get(home).get(day)[helper_late].add(val);
            }
        } else {
            if (type == EXAM) {
                daystats.get(home).get(day)[exam_night] = daystats.get(home).get(day)[exam_night].add(val);
            } else if (type == SOCIAL) {
                daystats.get(home).get(day)[social_night] = daystats.get(home).get(day)[social_night].add(val);
            } else if (type == HELPER) {
                daystats.get(home).get(day)[helper_night] = daystats.get(home).get(day)[helper_night].add(val);
            }
        }
    }

    public void add(int day, Homes home, int type, Symbol symbol) {
        add(day, home, symbol.getShift1(), type, symbol.getStatval1());
        add(day, home, symbol.getShift2(), type, symbol.getStatval2());
    }


    public BigDecimal getExam_early(Homes home, int day) {
        return daystats.get(home).get(day)[exam_early];
    }

    public BigDecimal getExam_late(Homes home, int day) {
        return daystats.get(home).get(day)[exam_late];
    }

    public BigDecimal getExam_night(Homes home, int day) {
        return daystats.get(home).get(day)[exam_night];
    }

    public BigDecimal getSocial_early(Homes home, int day) {
        return daystats.get(home).get(day)[social_early];
    }

    public BigDecimal getSocial_late(Homes home, int day) {
        return daystats.get(home).get(day)[social_late];
    }

    public BigDecimal getSocial_night(Homes home, int day) {
        return daystats.get(home).get(day)[social_night];
    }

    public BigDecimal getHelper_early(Homes home, int day) {
        return daystats.get(home).get(day)[helper_early];
    }

    public BigDecimal getHelper_late(Homes home, int day) {
        return daystats.get(home).get(day)[helper_late];
    }

    public BigDecimal getHelper_night(Homes home, int day) {
        return daystats.get(home).get(day)[helper_night];
    }
}