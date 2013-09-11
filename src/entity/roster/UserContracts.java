package entity.roster;

import org.joda.time.DateMidnight;
import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
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


}
