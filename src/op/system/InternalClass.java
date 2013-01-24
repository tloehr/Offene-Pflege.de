/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package op.system;

import entity.system.SYSGROUPS2ACL;

import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse dient dazu, die Beschreibungen von Modulen, sowie die Informationen über
 * erlaubte ACLs aus der appinfo.properties einzulesen und innerhalb des Systems während
 * der Laufzeit zum vereinbarten Zugriff bereit zu halten.
 *
 * @author tloehr
 */
public class InternalClass implements Comparable<InternalClass> {

    private String internalClassID;
    private String shortDescription;
    private String longDescription;
    private String javaclass;
    private boolean mainClass;
    private String iconname;

    // Enthält die möglichen acls für diese Klasse
    // inklusive der Beschreibungen (wenn vorhanden).
    private List<InternalClassACL> acls;

    private SYSGROUPS2ACL intClass;

    public InternalClass(String internalClassID, String shortDescription, String longDescription, boolean mainClass, String javaclass, String iconname) {
        this.internalClassID = internalClassID;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.mainClass = mainClass;
        this.javaclass = javaclass;
        acls = new ArrayList();
        this.intClass = null;
        this.iconname = iconname;
    }


    public SYSGROUPS2ACL getIntClass() {
        return intClass;
    }

    public void setIntClass(SYSGROUPS2ACL intClass) {
        this.intClass = intClass;
    }

    public boolean hasIntClass() {
        return this.intClass != null;
    }

    public String getIconname() {
        return iconname;
    }

    public void setIconname(String iconname) {
        this.iconname = iconname;
    }

    /**
     * Get the value of longDescription
     *
     * @return the value of longDescription
     */
    public String getLongDescription() {
        return longDescription;
    }

    /**
     * Eine Hashmap, die alle bekannten ACLs samt einer Beschreibung enthält.
     *
     * @return
     */
    public List<InternalClassACL> getPossibleACLs() {
        return acls;
    }

    /**
     * Get the value of shortDescription
     *
     * @return the value of shortDescription
     */
    public String getShortDescription() {
        return shortDescription;
    }

    public String getInternalClassID() {
        return internalClassID;
    }

    /**
     * String Bezeichnung, anhand der sich diese Klasse mittels der Reflection API laden lässt.
     * @return
     */
    public String getJavaclass() {
        return javaclass;
    }

    /**
     * Kann diese Klasse direkt als eingenständiges Modul aufegrufen werden ?
     * @return
     */
    public boolean isMainClass() {
        return mainClass;
    }

    @Override
    public String toString() {
        return shortDescription;
    }

    @Override
    public int compareTo(InternalClass o) {
        return shortDescription.compareTo(o.getShortDescription());
    }


}
