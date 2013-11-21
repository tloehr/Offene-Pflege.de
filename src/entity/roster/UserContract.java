package entity.roster;

import org.joda.time.LocalDate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 22.07.13
 * Time: 14:57
 * To change this template use File | Settings | File Templates.
 */
public class UserContract {

    ContractsParameterSet defaults;

    ArrayList<ContractsParameterSet> probations;
    ArrayList<ContractsParameterSet> extensions;
    ArrayList<ContractsParameterSet> alterations;

    public UserContract(ContractsParameterSet defaults) {
        this.defaults = defaults;
        probations = new ArrayList<ContractsParameterSet>();
        extensions = new ArrayList<ContractsParameterSet>();
        alterations = new ArrayList<ContractsParameterSet>();
    }

    public ContractsParameterSet getDefaults() {
        return defaults;
    }

    public void addProbation(ContractsParameterSet probation) {
        probations.add(probation);
        Collections.sort(probations);
    }

    public void addExtension(ContractsParameterSet extension) {
        extensions.add(extension);
        Collections.sort(extensions);
    }

    public void addAlteration(ContractsParameterSet alteration) {
        alterations.add(alteration);
        Collections.sort(alterations);
    }

    public ArrayList<ContractsParameterSet> getProbations() {
        return probations;
    }

    public ArrayList<ContractsParameterSet> getExtensions() {
        return extensions;
    }

    public ArrayList<ContractsParameterSet> getAlterations() {
        return alterations;
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

        for (ContractsParameterSet set : probations) {
            xml += getSubSection(set, "probation");
        }
        for (ContractsParameterSet set : extensions) {
            xml += getSubSection(set, "extension");
        }
        for (ContractsParameterSet set : alterations) {
            xml += getSubSection(set, "alteration");
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

            ArrayList<ContractsParameterSet>[] list = new ArrayList[]{probations, extensions, alterations};

            // apply alterations, if any
            for (ArrayList<ContractsParameterSet> set : list) {
                for (ContractsParameterSet paramset : set) {
                    // only those which are currently valid
                    if (paramset.getFrom().compareTo(day) <= 0 && paramset.getTo().compareTo(day) >= 0) {
                        if (paramset.getNight() != null) {
                            mySet.setNight(paramset.getNight());
                        }
                        if (paramset.getVacationDaysPerYear() != null) {
                            mySet.setVacationDaysPerYear(paramset.getVacationDaysPerYear());
                        }
                        if (paramset.getWagePerHour() != null) {
                            mySet.setWagePerHour(paramset.getWagePerHour());
                        }
                        if (paramset.getWorkingDaysPerWeek() != null) {
                            mySet.setWorkingDaysPerWeek(paramset.getWorkingDaysPerWeek());
                        }
                        if (paramset.getTargetHoursPerMonth() != null) {
                            mySet.setTargetHoursPerMonth(paramset.getTargetHoursPerMonth());
                        }
                        if (paramset.getHolidayPremiumPercentage() != null) {
                            mySet.setHolidayPremiumPercentage(paramset.getHolidayPremiumPercentage());
                        }
                        if (paramset.getNightPremiumPercentage() != null) {
                            mySet.setNightPremiumPercentage(paramset.getNightPremiumPercentage());
                        }
                    }
                }
            }
        }
        return mySet;
    }
}
