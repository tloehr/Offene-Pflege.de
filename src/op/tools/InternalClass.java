/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package op.tools;

import entity.system.IntClasses;

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

    private String internalClassname;
    private String shortDescription;
    private String longDescription;
    private String javaClass;
    private boolean mainClass;
    // Enthält die möglichen acls für diese Klasse
    // inklusive der Beschreibungen (wenn vorhanden).
    private List<InternalClassACL> acls;

    private IntClasses intClass;

    public InternalClass(String internalClassname, String shortDescription, String longDescription, boolean mainClass, String javaClass) {
        this.internalClassname = internalClassname;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.mainClass = mainClass;
        this.javaClass = javaClass;
        acls = new ArrayList();
        this.intClass = null;
    }


    public IntClasses getIntClass() {
        return intClass;
    }

    public void setIntClass(IntClasses intClass) {
        this.intClass = intClass;
    }

    public boolean hasIntClass() {
        return this.intClass != null;
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
    public List<InternalClassACL> getAcls() {
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

    public String getInternalClassname() {
        return internalClassname;
    }

    /**
     * String Bezeichnung, anhand der sich diese Klasse mittels der Reflection API laden lässt.
     * @return
     */
    public String getJavaClass() {
        return javaClass;
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
