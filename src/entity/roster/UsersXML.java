package entity.roster;

import op.tools.Pair;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 30.07.13
 * Time: 11:33
 * To change this template use File | Settings | File Templates.
 */
public class UsersXML extends DefaultHandler {

    UserContracts userContracts = null;

    String currentUID = "";
    ContractsParameterSet mySet = null;
    UserContract contract = null;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public UsersXML() {
    }

    @Override
    public void startDocument() throws SAXException {

    }

    @Override
    public void startElement(String nsURI, String strippedName, String tagName, Attributes attributes) throws SAXException {
//        System.out.println(currentUID);
//        System.out.println(tagName);
        try {
            if (tagName.equalsIgnoreCase("user")) {
                if (attributes.getValue("id") != null) {
                    currentUID = attributes.getValue("id");
                    userContracts = new UserContracts();
                }
                userContracts = new UserContracts();
            } else if (tagName.equalsIgnoreCase("contract")) {
                LocalDate from = new LocalDate(sdf.parse(attributes.getValue("from")));
                LocalDate to = new LocalDate(sdf.parse(attributes.getValue("to")));
                mySet = new ContractsParameterSet(from, to);
            } else if (tagName.equalsIgnoreCase("defaults")) {
            } else if (tagName.equalsIgnoreCase("probation")) {
                LocalDate from = new LocalDate(sdf.parse(attributes.getValue("from")));
                LocalDate to = new LocalDate(sdf.parse(attributes.getValue("to")));
                mySet = new ContractsParameterSet(from, to);
            } else if (tagName.equalsIgnoreCase("extension")) {
                LocalDate from = new LocalDate(sdf.parse(attributes.getValue("from")));
                LocalDate to = new LocalDate(sdf.parse(attributes.getValue("to")));
                mySet = new ContractsParameterSet(from, to);
            } else if (tagName.equalsIgnoreCase("alteration")) {
                LocalDate from = new LocalDate(sdf.parse(attributes.getValue("from")));
                LocalDate to = new LocalDate(sdf.parse(attributes.getValue("to")));
                mySet = new ContractsParameterSet(from, to);
            } else if (tagName.equalsIgnoreCase("vacationdays")) {
                mySet.setVacationDaysPerYear(new BigDecimal(attributes.getValue("value")));
            } else if (tagName.equalsIgnoreCase("wageperhour")) {
                mySet.setWagePerHour(new BigDecimal(attributes.getValue("value")));
            } else if (tagName.equalsIgnoreCase("workingdaysperweek")) {
                mySet.setWorkingDaysPerWeek(new BigDecimal(attributes.getValue("value")));
            } else if (tagName.equalsIgnoreCase("targethourspermonth")) {
                mySet.setTargetHoursPerMonth(new BigDecimal(attributes.getValue("value")));
            } else if (tagName.equalsIgnoreCase("holidaypremiumpercentage")) {
                mySet.setHolidayPremiumPercentage(new BigDecimal(attributes.getValue("value")));
            } else if (tagName.equalsIgnoreCase("nightpremiumpercentage")) {
                mySet.setNightPremiumPercentage(new BigDecimal(attributes.getValue("value")));
            } else if (tagName.equalsIgnoreCase("night")) {
                LocalTime from = LocalTime.parse(attributes.getValue("from"));
                LocalTime to = LocalTime.parse(attributes.getValue("to"));
                mySet.setNight(new Pair<LocalTime, LocalTime>(from, to));
            } else if (tagName.equalsIgnoreCase("section")) {
                mySet.setSection(attributes.getValue("value"));
            } else if (tagName.equalsIgnoreCase("trainee")) {
                mySet.setTrainee(true);
            } else if (tagName.equalsIgnoreCase("exam")) {
                mySet.setExam(true);
            }
        } catch (Exception e) {
            throw new SAXException(e);
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("contract")) {
            userContracts.add(contract);
        } else if (qName.equalsIgnoreCase("defaults")) {
            contract = new UserContract(mySet);
        } else if (qName.equalsIgnoreCase("probation")) {
            contract.addProbation(mySet);
        } else if (qName.equalsIgnoreCase("extension")) {
            contract.addExtension(mySet);
        } else if (qName.equalsIgnoreCase("alteration")) {
            contract.addAlteration(mySet);
        }
    }

    @Override
    public void endDocument() throws SAXException {

    }

    public UserContracts getUserContracts() {
        return userContracts;
    }
}