package entity.roster;

import entity.system.Users;
import org.jdesktop.swingx.util.Contract;
import org.joda.time.Days;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 24.07.13
 * Time: 11:56
 * To change this template use File | Settings | File Templates.
 */
public class UserContracts {

    final String xmlheader = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
    final String userdtd = "<!DOCTYPE users [\n" +
            "        <!ELEMENT users (user)*>\n" +
            "        <!ELEMENT user (contract)*>\n" +
            "        <!ATTLIST user\n" +
            "                id CDATA #REQUIRED>\n" +
            "        <!ELEMENT contract (defaults,probation*,extension*,alteration*)+>\n" +
            "        <!ATTLIST contract\n" +
            "                from CDATA #REQUIRED\n" +
            "                to CDATA #REQUIRED>\n" +
            "        <!ELEMENT defaults (vacationdays,wageperhour,workingdaysperweek,targethourspermonth,night,holidaypremiumpercentage,nightpremiumpercentage,section,exam?,trainee?)>\n" +
            "        <!ELEMENT vacationdays (#PCDATA)>\n" +
            "        <!ATTLIST vacationdays\n" +
            "                value CDATA #REQUIRED>\n" +
            "        <!ELEMENT wageperhour (#PCDATA)>\n" +
            "        <!ATTLIST wageperhour\n" +
            "                value CDATA #REQUIRED>\n" +
            "        <!ELEMENT workingdaysperweek (#PCDATA)>\n" +
            "        <!ATTLIST workingdaysperweek\n" +
            "                value CDATA #REQUIRED>\n" +
            "        <!ELEMENT targethourspermonth (#PCDATA)>\n" +
            "        <!ATTLIST targethourspermonth\n" +
            "                value CDATA #REQUIRED>\n" +
            "        <!ELEMENT night (#PCDATA)>\n" +
            "        <!ATTLIST night\n" +
            "                from CDATA #REQUIRED\n" +
            "                to CDATA #REQUIRED>\n" +
            "        <!ELEMENT holidaypremiumpercentage (#PCDATA)>\n" +
            "        <!ATTLIST holidaypremiumpercentage\n" +
            "                value CDATA #REQUIRED>\n" +
            "        <!ELEMENT nightpremiumpercentage (#PCDATA)>\n" +
            "        <!ATTLIST nightpremiumpercentage\n" +
            "                value CDATA #REQUIRED>\n" +
            "        <!ELEMENT section (#PCDATA)>\n" +
            "        <!ATTLIST section\n" +
            "                value CDATA #REQUIRED>\n" +
            "        <!ELEMENT exam (#PCDATA)>\n" +
            "        <!ELEMENT extension (#PCDATA)>\n" +
            "        <!ATTLIST extension\n" +
            "                from CDATA #REQUIRED\n" +
            "                to CDATA #REQUIRED>\n" +
            "        <!ELEMENT alteration (#PCDATA)>\n" +
            "        <!ATTLIST alteration\n" +
            "                from CDATA #REQUIRED\n" +
            "                to CDATA #REQUIRED>\n" +
            "        <!ELEMENT probation (#PCDATA)>\n" +
            "        <!ATTLIST probation\n" +
            "                from CDATA #REQUIRED\n" +
            "                to CDATA #REQUIRED>\n" +
            "        <!ELEMENT trainee (#PCDATA)>\n" +
            "        ]>";

    ArrayList<UserContract> listContracts;

    public UserContracts() {
        listContracts = new ArrayList<UserContract>();
    }

    public void add(UserContract contract) {
        listContracts.add(contract);
    }

    public ArrayList<UserContract> getListContracts() {
        return listContracts;
    }

    public String toXML() {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String xml = "<user>\n";
        for (UserContract contract : listContracts) {
            xml += contract.toXML();
        }
        xml += "</user>\n";
        return xml;
    }


    public ContractsParameterSet getParameterSet(LocalDate day) {
        ContractsParameterSet mySet = null;

        for (UserContract contract : listContracts) {
            mySet = contract.getParameterSet(day);
            if (mySet != null) {
                break;
            }
        }

        return mySet;
    }

    public Workaccount getTargetHoursForMonth(LocalDate month, Users user) {
        // em.merge(new Workaccount(monthToCreate.toDate(), mapUsers.get(user).getParameterSet(monthToCreate).getTargetHoursPerMonth().negate(), WorkAccountTools.HOURS_AUTO, user));
        ArrayList<UserContract> myContracts = getContractsWithinMonth(month);
        Workaccount workaccount = null;

        DateFormat df = DateFormat.getDateInstance();

        Interval intervalMonth = new Interval(
                month.dayOfMonth().withMinimumValue().toDateTime(LocalTime.MIDNIGHT),
                month.dayOfMonth().withMaximumValue().toDateTime(LocalTime.now().hourOfDay().withMaximumValue().minuteOfHour().withMaximumValue().secondOfMinute().withMaximumValue())
        );

        int maxdays = month.dayOfMonth().withMaximumValue().getDayOfMonth();

        String text = "";

        if (!myContracts.isEmpty()) {
            BigDecimal hours = BigDecimal.ZERO;

            for (UserContract contract : myContracts) {
                Interval intervalContract = new Interval(
                        contract.getDefaults().getFrom().toDateTime(LocalTime.MIDNIGHT),
                        contract.getDefaults().getTo().toDateTime(LocalTime.now().hourOfDay().withMaximumValue().minuteOfHour().withMaximumValue().secondOfMinute().withMaximumValue())
                );

                Interval overlap = intervalContract.overlap(intervalMonth);

                if (overlap != null) {
//                    OPDE.debug(overlap.toPeriod().getDays());

//                    OPDE.debug(quota.multiply(new BigDecimal(100)) + " %");
//                    OPDE.debug(contract.getDefaults().getTargetHoursPerMonth());
//                    OPDE.debug(contract.getDefaults().getTargetHoursPerMonth().multiply(quota));

                    BigDecimal quota = new BigDecimal(Days.daysIn(overlap).getDays() + 1).divide(new BigDecimal(maxdays), 4, RoundingMode.HALF_UP);
                    text += String.format("Vertrag vom %s bis %s. Sollstunden im Monat: %s. Somit Sollstunden für diesen Monat: %s (%s %%)\n",
                            df.format(contract.getDefaults().getFrom().toDate()),
                            df.format(contract.getDefaults().getTo().toDate()),
                            contract.getDefaults().getTargetHoursPerMonth(),
                            contract.getDefaults().getTargetHoursPerMonth().multiply(quota),
                            quota.multiply(new BigDecimal(100))
                    );

                    hours = hours.add(contract.getDefaults().getTargetHoursPerMonth().multiply(quota));

                }

                if (myContracts.size() > 1) {
                    text += String.format("Somit gesamt: %s\n", hours);
                }


                workaccount = new Workaccount(month.toDate(), hours.negate(), WorkAccountTools.HOURS_AUTO, user);
                workaccount.setText(text);

            }
        }

        return workaccount;
    }

    public ArrayList<UserContract> getContractsWithinMonth(LocalDate month) {
        ArrayList<UserContract> myContracts = new ArrayList<UserContract>();

        Interval intervalMonth = new Interval(
                month.dayOfMonth().withMinimumValue().toDateTime(LocalTime.MIDNIGHT),
                month.dayOfMonth().withMaximumValue().toDateTime(LocalTime.now().hourOfDay().withMaximumValue().minuteOfHour().withMaximumValue().secondOfMinute().withMaximumValue())
        );

        for (UserContract contract : listContracts) {
            Interval intervalContract = new Interval(
                    contract.getDefaults().getFrom().toDateTime(LocalTime.MIDNIGHT),
                    contract.getDefaults().getTo().toDateTime(LocalTime.now().hourOfDay().withMaximumValue().minuteOfHour().withMaximumValue().secondOfMinute().withMaximumValue())
            );

            if (intervalContract.overlaps(intervalMonth)) {
                myContracts.add(contract);
            }

        }

        return myContracts;
    }

    public boolean hasValidContractsInMonth(LocalDate month) {
        boolean hasValid = false;
        Interval intervalMonth = new Interval(
                month.dayOfMonth().withMinimumValue().toDateTime(LocalTime.MIDNIGHT),
                month.dayOfMonth().withMaximumValue().toDateTime(LocalTime.now().hourOfDay().withMaximumValue().minuteOfHour().withMaximumValue().secondOfMinute().withMaximumValue())
        );

        for (UserContract contract : listContracts) {
            Interval intervalContract = new Interval(
                    contract.getDefaults().getFrom().toDateTime(LocalTime.MIDNIGHT),
                    contract.getDefaults().getTo().toDateTime(LocalTime.now().hourOfDay().withMaximumValue().minuteOfHour().withMaximumValue().secondOfMinute().withMaximumValue())
            );

            hasValid = intervalContract.overlaps(intervalMonth);

            if (hasValid) break;

        }

        return hasValid;
    }

    public UserContract getValidContractOn(LocalDate day) {
        UserContract valid = null;
        for (UserContract contract : listContracts) {
            if (contract.getDefaults().getFrom().compareTo(day) <= 0 && contract.getDefaults().getTo().compareTo(day) >= 0){
                valid = contract;
                break;
            }
        }
        return valid;
    }



//    public boolean replaceContract(UserContract contract) {
//
//
//        listContracts.
//
//        contract.getDefaults().setTo(day);
//
//        return true;
//    }

}
