package op.tools;

import java.util.*;

/**
 * @author Dierk Meinig
 *         Sortiert die Properties in natuerlicher Folge bei der Speicherung
 */
public class SortedProperties extends Properties {

    /**
     *
     */
    private static final long serialVersionUID = 8397564014128693064L;

    /*
      * (non-Javadoc)
      * @see java.util.Hashtable#keys()
      */
    public synchronized Enumeration keys() {
        Enumeration keysEnum = super.keys();
        Vector keyList = new Vector();
        while (keysEnum.hasMoreElements()) {
            keyList.add(keysEnum.nextElement());
        }
        Collections.sort(keyList);
        return keyList.elements();
    }

    /* (non-Javadoc)
      * @see java.util.Hashtable#keySet()
      * used in storeToXml
      */
    public Set<Object> keySet() {
        SortedSet set = new TreeSet(super.keySet());
        return set;
    }
}