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

    int hoursperyear;

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    int section;
    HashMap<String, Symbol> symbolMap;

    public RosterParameters() {
        symbolMap = new HashMap<String, Symbol>();
    }

    public void setHoursperyear(int hoursperyear) {
        this.hoursperyear = hoursperyear;
    }

    public void addSymbol(String key, Symbol symbol){
        symbolMap.put(key.toLowerCase(), symbol);
    }

    public Symbol getSymbol(String key){
        return symbolMap.get(key.toLowerCase());
    }

    public int getHoursperyear() {
        return hoursperyear;
    }


}
