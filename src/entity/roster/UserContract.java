package entity.roster;

import op.tools.Pair;
import op.tools.SYSConst;
import org.joda.time.LocalDate;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 22.07.13
 * Time: 14:57
 * To change this template use File | Settings | File Templates.
 */
public class UserContract implements Comparable<UserContract> {

    ContractsParameterSet defaults;

    ArrayList<Pair<LocalDate, LocalDate>> probations;
    ArrayList<Pair<LocalDate, LocalDate>> extensions;
//    ArrayList<Pair<LocalDate, LocalDate>> alterations;

    public UserContract(ContractsParameterSet defaults) {
        this.defaults = defaults;
        probations = new ArrayList<Pair<LocalDate, LocalDate>>();
        extensions = new ArrayList<Pair<LocalDate, LocalDate>>();
//        alterations = new ArrayList<Pair<LocalDate, LocalDate>>();
    }

    public ContractsParameterSet getDefaults() {
        return defaults;
    }

    public void addProbation(Pair<LocalDate, LocalDate> probation) {
        probations.add(probation);
        Collections.sort(probations, new Comparator<Pair<LocalDate, LocalDate>>() {
            @Override
            public int compare(Pair<LocalDate, LocalDate> o1, Pair<LocalDate, LocalDate> o2) {
                return o1.getFirst().compareTo(o2.getFirst());
            }
        });
    }

    public void addExtension(Pair<LocalDate, LocalDate> extension) {
        extensions.add(extension);
        Collections.sort(extensions, new Comparator<Pair<LocalDate, LocalDate>>() {
            @Override
            public int compare(Pair<LocalDate, LocalDate> o1, Pair<LocalDate, LocalDate> o2) {
                return o1.getFirst().compareTo(o2.getFirst());
            }
        });
    }

    public ArrayList<Pair<LocalDate, LocalDate>> getProbations() {
        return probations;
    }

    public ArrayList<Pair<LocalDate, LocalDate>> getExtensions() {
        return extensions;
    }

    public String toXML() {
        DecimalFormat df = new DecimalFormat("#####.##");


        String xml = String.format("    <contract from=\"%s\" to=\"%s\">\n", defaults.getFrom().toString("yyyy-MM-dd"), defaults.getTo().toString("yyyy-MM-dd"));
        xml += "            <defaults>\n";
        xml += String.format("                <vacationdays value=\"%s\"/>\n", df.format(defaults.getVacationDaysPerYear()).replace(",", "."));
        xml += String.format("                <wageperhour value=\"%s\"/>\n", df.format(defaults.getWagePerHour()).replace(",", "."));
        xml += String.format("                <workingdaysperweek value=\"%s\"/>\n", df.format(defaults.getWorkingDaysPerWeek()).replace(",", "."));
        xml += String.format("                <targethourspermonth value=\"%s\"/>\n", df.format(defaults.getTargetHoursPerMonth()).replace(",", "."));
        xml += String.format("                <night from=\"%s\" to=\"%s\"/>\n", defaults.getNight().getFirst().toString("HH:mm"), defaults.getNight().getSecond().toString("HH:mm"));
        xml += String.format("                <holidaypremiumpercentage value=\"%s\"/>\n", df.format(defaults.getHolidayPremiumPercentage()).replace(",", "."));
        xml += String.format("                <nightpremiumpercentage value=\"%s\"/>\n", df.format(defaults.getNightPremiumPercentage()).replace(",", "."));
        xml += String.format("                <section value=\"%s\"/>\n", defaults.getSection());
        xml += (defaults.isExam() ? "                <exam/>\n" : "");
        xml += (defaults.isTrainee() ? "                <trainee/>\n" : "");
        xml += "           </defaults>\n";

        for (Pair<LocalDate, LocalDate> p : probations) {
            xml += String.format("            <%s from=\"%s\" to=\"%s\"></%s>\n", "probation", p.getFirst().toString("yyyy-MM-dd"), p.getSecond().toString("yyyy-MM-dd"));
        }
        for (Pair<LocalDate, LocalDate> e : extensions) {
            xml += String.format("            <%s from=\"%s\" to=\"%s\"></%s>\n", "extension", e.getFirst().toString("yyyy-MM-dd"), e.getSecond().toString("yyyy-MM-dd"));
        }

        xml += "    </contract>\n";
        return xml;
    }

    private String getSubSection(ContractsParameterSet set, String sectionName) {
        DecimalFormat df = new DecimalFormat("#####.##");
        String xml = String.format("            <%s from=\"%s\" to=\"%s\">\n", sectionName, set.getFrom().toString("yyyy-MM-dd"), set.getTo().toString("yyyy-MM-dd"));
        xml += (set.getVacationDaysPerYear() != null ? String.format("                <vacationdays value=\"%s\"/>\n", df.format(set.getVacationDaysPerYear()).replace(",", ".")) : "") +
                (set.getWagePerHour() != null ? String.format("                <wageperhour value=\"%s\"/>\n", df.format(set.getWagePerHour()).replace(",", ".")) : "") +
                (set.getWorkingDaysPerWeek() != null ? String.format("                <workingdaysperweek value=\"%s\"/>\n", df.format(set.getWorkingDaysPerWeek()).replace(",", ".")) : "") +
                (set.getTargetHoursPerMonth() != null ? String.format("                <targethourspermonth value=\"%s\"/>\n", df.format(set.getTargetHoursPerMonth()).replace(",", ".")) : "") +
                (set.getNight() != null ? String.format("                <night from=\"%s\" to=\"%s\"/>\n", set.getNight().getFirst().toString("HH:mm"), set.getNight().getSecond().toString("HH:mm")) : "") +
                (set.getHolidayPremiumPercentage() != null ? String.format("                <holidaypremiumpercentage value=\"%s\"/>\n", df.format(set.getHolidayPremiumPercentage()).replace(",", ".")) : "") +
                (set.getNightPremiumPercentage() != null ? String.format("                <nightpremiumpercentage value=\"%s\"/>\n", df.format(set.getNightPremiumPercentage()).replace(",", ".")) : "") +
                String.format("            </%s>\n", sectionName);
        return xml;
    }

    public ContractsParameterSet getParameterSet(LocalDate day) {
        ContractsParameterSet mySet = null;

        if (defaults.getFrom().compareTo(day) <= 0 && defaults.getTo().compareTo(day) >= 0) {
            mySet = (ContractsParameterSet) defaults.clone();

//            ArrayList<ContractsParameterSet>[] list = new ArrayList[]{probations, extensions};

//            // apply alterations, if any
//            for (ArrayList<ContractsParameterSet> set : list) {
//                for (ContractsParameterSet paramset : set) {
//                    // only those which are currently valid
//                    if (paramset.getFrom().compareTo(day) <= 0 && paramset.getTo().compareTo(day) >= 0) {
//                        if (paramset.getNight() != null) {
//                            mySet.setNight(paramset.getNight());
//                        }
//                        if (paramset.getVacationDaysPerYear() != null) {
//                            mySet.setVacationDaysPerYear(paramset.getVacationDaysPerYear());
//                        }
//                        if (paramset.getWagePerHour() != null) {
//                            mySet.setWagePerHour(paramset.getWagePerHour());
//                        }
//                        if (paramset.getWorkingDaysPerWeek() != null) {
//                            mySet.setWorkingDaysPerWeek(paramset.getWorkingDaysPerWeek());
//                        }
//                        if (paramset.getTargetHoursPerMonth() != null) {
//                            mySet.setTargetHoursPerMonth(paramset.getTargetHoursPerMonth());
//                        }
//                        if (paramset.getHolidayPremiumPercentage() != null) {
//                            mySet.setHolidayPremiumPercentage(paramset.getHolidayPremiumPercentage());
//                        }
//                        if (paramset.getNightPremiumPercentage() != null) {
//                            mySet.setNightPremiumPercentage(paramset.getNightPremiumPercentage());
//                        }
//                    }
//                }
//            }
        }
        return mySet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserContract that = (UserContract) o;

        if (defaults != null ? !defaults.equals(that.defaults) : that.defaults != null) return false;
        if (extensions != null ? !extensions.equals(that.extensions) : that.extensions != null) return false;
        if (probations != null ? !probations.equals(that.probations) : that.probations != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = defaults != null ? defaults.hashCode() : 0;
        result = 31 * result + (probations != null ? probations.hashCode() : 0);
        result = 31 * result + (extensions != null ? extensions.hashCode() : 0);

        return result;
    }

    @Override
    public int compareTo(UserContract o) {
        return getDefaults().getFrom().compareTo(o.getDefaults().getFrom());
    }

    public String getPeriodAsHTML() {
        String result = "";
        DateFormat df = DateFormat.getDateInstance();
        result += "<table id=\"fonttext\" border=\"0\" cellspacing=\"0\">";
        result += "<tr>";
        result += "<td valign=\"top\">" + df.format(getDefaults().getFrom().toDate()) + "</td>";

        if (getDefaults().getTo().equals(SYSConst.LD_UNTIL_FURTHER_NOTICE)) {
            result += "<td valign=\"top\">&raquo;&raquo;</td>";
            result += "<td valign=\"top\"></td>";
        } else {
            result += "<td valign=\"top\">&raquo;</td>";
            result += "<td valign=\"top\">" + df.format(getDefaults().getTo().toDate()) + "</td>";
        }

        result += "</tr>\n";
        result += "</table>\n";

        return result;
    }
}
