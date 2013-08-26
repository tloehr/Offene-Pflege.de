package entity.roster;

import entity.Homes;
import entity.system.Users;
import op.tools.SYSConst;
import op.tools.SYSTools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 29.07.13
 * Time: 14:51
 * To change this template use File | Settings | File Templates.
 */
public class RosterParameters {

    private int hoursperyear;
    private int section;
    private HashMap<String, Symbol> symbolMap;
    private HashMap<String, String> assigns;
    private ArrayList<String> userlist;

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public RosterParameters() {
        symbolMap = new HashMap<String, Symbol>();
        assigns = new HashMap<String, String>();
        userlist = new ArrayList<String>();
    }

    public void setHoursperyear(int hoursperyear) {
        this.hoursperyear = hoursperyear;
    }

    public void addSymbol(String key, Symbol symbol) {
        symbolMap.put(key.toUpperCase(), symbol);
    }

    public Symbol getSymbol(String key) {
        return symbolMap.get(key.toUpperCase());
    }

    public int getHoursperyear() {
        return hoursperyear;
    }

    public String getPreferredHomeID(String uid) {
        return assigns.get(uid.toLowerCase());
    }

    public void addPreferredHomeID(String uid, String homeid) {
        assigns.put(uid.toLowerCase(), homeid.toLowerCase());
    }

    public void setUserlist(ArrayList<Users> ulist, HashMap<Users, Homes> preferredHome) {
        userlist.clear();
        assigns.clear();
        for (Users user : ulist) {
            userlist.add(user.getUID());
            assigns.put(user.getUID(), preferredHome.get(user.getUID()).getEID());
        }
    }

    public ArrayList<String> getUserlist() {
        return userlist;
    }

    public String toXML() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String xml = SYSConst.xmlheader;
        xml += String.format("<roster section=\"%s\">\n", RosterXML.sections[section]);

        xml += "<config>\n";
        xml += String.format("<hourstoworkperyear value=\"%s\"/>\n", hoursperyear);
        xml += "</config>\n";

        if (!symbolMap.isEmpty()) {
            xml += "<symbols>\n";
            for (Map.Entry<String, Symbol> entry : symbolMap.entrySet()) {
                Symbol symbol = entry.getValue();

                String closeTagEarly = symbol.getAllowedDays().isEmpty() ? "/" : "";

                if (entry.getValue().getStart() != null) {
                    xml += String.format("<symbol key=\"%s\" starttime=\"%s\" endtime=\"%s\" break=\"%s\" calc=\"%s\" type=\"%s\" description=\"%s\" " + closeTagEarly + ">\n",
                            symbol.getKey().toUpperCase(),
                            symbol.getStart().toString("HH:mm"),
                            symbol.getEnd().toString("HH:mm"),
                            symbol.getBreak().intValue(),
                            Symbol.CALC[symbol.getCalc()],
                            Symbol.TYPE[symbol.getSymbolType()],
                            SYSTools.catchNull(symbol.getDescription()));
                } else {
                    xml += String.format("<symbol key=\"%s\" calc=\"%s\" type=\"%s\" description=\"%s\" " + closeTagEarly + ">\n", symbol.getKey().toUpperCase(),
                            symbol.getKey().toUpperCase(),
                            Symbol.CALC[symbol.getCalc()],
                            Symbol.TYPE[symbol.getSymbolType()],
                            SYSTools.catchNull(symbol.getDescription()));
                }

                if (closeTagEarly.isEmpty()) {
                    for (int day : symbol.getAllowedDays().toArray(new Integer[]{})) {
                        xml += String.format("<%s/>\n", getWeekdayName(day));
                    }
                    xml += "</symbol>";
                }
            }
            xml += "</symbols>\n";
        }

        if (!userlist.isEmpty()) {
            xml += "<users>\n";
            for (String uid : userlist) {
                xml += String.format("<assign uid=\"%s\" homeid=\"%s\"/>\n", uid, assigns.get(uid));
            }
            xml += "</users>\n";
        }
        xml += "</roster>\n";
        return xml;
    }


    private String getWeekdayName(int day) {
        String name = "holiday";
        String[] daynames = new String[]{"", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};


        if (day != Symbol.HOLIDAY) {
            name = daynames[day];
        }
        return name;
    }
}
