package entity.roster;

import op.tools.SYSTools;
import org.apache.commons.lang3.ArrayUtils;
import org.joda.time.DateTimeConstants;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 29.07.13
 * Time: 14:49
 * To change this template use File | Settings | File Templates.
 */
public class RosterXML extends DefaultHandler {

    public static final int CARE = 0;
    public static final int SOCIAL = 1;
    public static final int KITCHEN = 2;
    public static final int CLEANING = 3;
    public static final int LAUNDRY = 4;
    public static final int JANITOR = 5;

    public static final String[] sections = new String[]{"care", "social", "kitchen", "cleaning", "laundry", "janitor"};

    RosterParameters myRoster = null;
    //    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Symbol symbol = null;

    public RosterXML() {
        myRoster = new RosterParameters();
    }

    @Override
    public void startDocument() throws SAXException {

    }

    public RosterParameters getRosterParameters() {
        return myRoster;
    }

    @Override
    public void startElement(String nsURI, String strippedName, String tagName, Attributes attributes) throws SAXException {
//        System.out.println(tagName);
        try {
            if (tagName.equalsIgnoreCase("roster")) {
                myRoster.setSection(Arrays.asList(sections).indexOf(attributes.getValue("section")));
            } else if (tagName.equalsIgnoreCase("hourstoworkperyear")) {
                myRoster.setHoursperyear(Integer.parseInt(attributes.getValue("value")));
            } else if (tagName.equalsIgnoreCase("symbol")) {

                //todo: implement "nbreak"

                symbol = new Symbol(attributes.getValue("key"), attributes.getValue("description"), attributes.getValue("starttime"), attributes.getValue("endtime"), Integer.parseInt(SYSTools.catchNull(attributes.getValue("break"), "0")), attributes.getValue("calc"), attributes.getValue("type"));

                if (attributes.getValue("shift1") != null) {
                    symbol.setShift1(attributes.getValue("shift1"), attributes.getValue("statvalue1"));
                }

                if (attributes.getValue("shift2") != null) {
                    symbol.setShift2(attributes.getValue("shift2"), attributes.getValue("statvalue2"));
                }

                if (attributes.getValue("section") != null) {
                    symbol.setSection(Arrays.asList(sections).indexOf(attributes.getValue("section")));
                }

//            } else if (tagName.equalsIgnoreCase("assign")) {
//                myRoster.addPreferredHome(attributes.getValue("uid"), attributes.getValue("homeid"));
            } else if (tagName.equalsIgnoreCase("monday")) {
                symbol.addDay(DateTimeConstants.MONDAY);
            } else if (tagName.equalsIgnoreCase("tuesday")) {
                symbol.addDay(DateTimeConstants.TUESDAY);
            } else if (tagName.equalsIgnoreCase("wednesday")) {
                symbol.addDay(DateTimeConstants.WEDNESDAY);
            } else if (tagName.equalsIgnoreCase("thursday")) {
                symbol.addDay(DateTimeConstants.THURSDAY);
            } else if (tagName.equalsIgnoreCase("friday")) {
                symbol.addDay(DateTimeConstants.FRIDAY);
            } else if (tagName.equalsIgnoreCase("saturday")) {
                symbol.addDay(DateTimeConstants.SATURDAY);
            } else if (tagName.equalsIgnoreCase("sunday")) {
                symbol.addDay(DateTimeConstants.SUNDAY);
            } else if (tagName.equalsIgnoreCase("holiday")) {
                symbol.addDay(Symbol.HOLIDAY);
            }
        } catch (Exception e) {
            throw new SAXException(e);
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("symbol")) {
            myRoster.addSymbol(symbol.getKey(), symbol);
            symbol = null;
        }
    }

    @Override
    public void endDocument() throws SAXException {

    }


}
