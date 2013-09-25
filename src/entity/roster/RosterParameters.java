package entity.roster;

import entity.Homes;
import entity.system.Users;
import op.OPDE;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.persistence.EntityManager;
import java.math.RoundingMode;
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
    private HashMap<Users, Homes> preferredHome;
//    private ArrayList<Users> userlist;

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public RosterParameters() {
        symbolMap = new HashMap<String, Symbol>();
        preferredHome = new HashMap<Users, Homes>();
//        userlist = new ArrayList<Users>();
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


    public void addPreferredHome(String uid, String homeid) {
        EntityManager em = OPDE.createEM();
        Homes myHome = em.find(Homes.class, homeid);
        Users user = em.find(Users.class, uid);
        em.close();
        preferredHome.put(user, myHome);
//        userlist.add(user);
    }

//    public void setUserlist(ArrayList<Users> ulist, HashMap<Users, Homes> preferredHome) {
//        userlist.clear();
//        this.preferredHome.clear();
//        for (Users user : ulist) {
//            userlist.add(user.getUID());
//            this.preferredHome.put(user.getUID(), preferredHome.get(user.getUID()).getEID());
//        }
//    }


    public HashMap<Users, Homes> getPreferredHome() {
        return preferredHome;
    }

//    public ArrayList<Users> getUserlist() {
//        return userlist;
//    }

    public String toXML() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String xml = SYSConst.xmlheader;
        xml += String.format("<roster section=\"%s\">\n", RosterXML.sections[section]);

        xml += "    <config>\n";
        xml += String.format("        <hourstoworkperyear value=\"%s\"/>\n", hoursperyear);
        xml += "    </config>\n";

        if (!symbolMap.isEmpty()) {
            xml += "    <symbols>\n";
            for (Map.Entry<String, Symbol> entry : symbolMap.entrySet()) {
                Symbol symbol = entry.getValue();

                String closeTagEarly = symbol.getAllowedDays().isEmpty() ? "/" : "";

                if (entry.getValue().getStart() != null) {

                    String shift1 = symbol.getShift1() == Symbol.SHIFT_NONE ? "" : String.format(" shift1=\"%s\" statvalue1=\"%s\" ", Symbol.SHIFT[symbol.getShift1()], symbol.getStatval1().setScale(2, RoundingMode.HALF_UP));
                    String shift2 = symbol.getShift1() == Symbol.SHIFT_NONE ? "" : String.format(" shift2=\"%s\" statvalue2=\"%s\" ", Symbol.SHIFT[symbol.getShift2()], symbol.getStatval2().setScale(2, RoundingMode.HALF_UP));

                    xml += String.format("        <symbol key=\"%s\" starttime=\"%s\" endtime=\"%s\" break=\"%s\" calc=\"%s\" type=\"%s\" description=\"%s\" " + shift1 + shift2 + closeTagEarly + ">\n",
                            symbol.getKey().toUpperCase(),
                            symbol.getStart().toString("HH:mm"),
                            symbol.getEnd().toString("HH:mm"),
                            symbol.getBreak().intValue(),
                            Symbol.CALC[symbol.getCalc()],
                            Symbol.TYPE[symbol.getSymbolType()],
                            SYSTools.catchNull(symbol.getDescription()));
                } else {
                    xml += String.format("        <symbol key=\"%s\" calc=\"%s\" type=\"%s\" description=\"%s\" " + closeTagEarly + ">\n",
                            symbol.getKey().toUpperCase(),
                            Symbol.CALC[symbol.getCalc()],
                            Symbol.TYPE[symbol.getSymbolType()],
                            SYSTools.catchNull(symbol.getDescription()));
                }

                if (closeTagEarly.isEmpty()) {
                    for (int day : symbol.getAllowedDays().toArray(new Integer[]{})) {
                        xml += String.format("            <%s/>\n", getWeekdayName(day));
                    }
                    xml += "        </symbol>\n";
                }
            }
            xml += "    </symbols>\n";
        }

//        if (!userlist.isEmpty()) {
//            xml += "    <users>\n";
//            for (Users user : userlist) {
//                xml += String.format("        <assign uid=\"%s\" homeid=\"%s\"/>\n", user.getUID(), preferredHome.get(user).getEID());
//            }
//            xml += "    </users>\n";
//        }
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
