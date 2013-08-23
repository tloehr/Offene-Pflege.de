package entity.roster;

import java.util.HashMap;

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

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public RosterParameters() {
        symbolMap = new HashMap<String, Symbol>();
        assigns = new HashMap<String, String>();
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

}
