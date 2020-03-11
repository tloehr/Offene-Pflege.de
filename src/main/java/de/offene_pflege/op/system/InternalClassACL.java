/*
 * 
 */
package de.offene_pflege.op.system;

import de.offene_pflege.op.tools.SYSTools;

/**
 * Neben der Darstellung durch die EntityBean Acl, welche ich hier
 *
 * @author tloehr
 */
public class InternalClassACL implements Comparable<InternalClassACL> {

    public static final short SELECT = 0; // Darf lesen
    public static final short INSERT = 1; // Darf neues erstellen
    public static final short DELETE = 2; // Darf löschen
    public static final short CANCEL = 3; // Darf stornieren
    public static final short UPDATE = 4; // Darf ändern
    public static final short GRANT = 5; // Darf Rechte verteilen
    public static final short EXECUTE = 6; // Darf irgendwas ausführen
    public static final short PRINT = 7; // Darf irgendwas ausdrucken
    public static final short USER1 = 8; // Bedeutung ist hier nicht festgelegt.
    public static final short USER2 = 9; // Bedeutung ist hier nicht festgelegt.
    public static final short USER3 = 10; // Bedeutung ist hier nicht festgelegt.
    public static final short USER4 = 11; // Bedeutung ist hier nicht festgelegt.
    public static final short ARCHIVE = 12; // Darf auf ein Archiv zugreifen
    public static final short MANAGER = 13; // Darf die Einträge anderer bearbeiten
    public static final short[] ACLS = new short[]{SELECT, INSERT, DELETE, CANCEL, UPDATE, GRANT, EXECUTE, PRINT, USER1, USER2, USER3, USER4, ARCHIVE, MANAGER};
    public static final String[] strACLS = new String[]{"SELECT", "INSERT", "DELETE", "CANCEL", "UPDATE", "GRANT", "EXECUTE", "PRINT", "USER1", "USER2", "USER3", "USER4", "ARCHIVE", "MANAGER"};

    private String description;
    private short acl;
//    private Acl aclEntity; // Wenn es eine bestehende Zuordnung zur Entity Acl gibt, dann steht die hier.

//    public InternalClassACL(String description, short acl, Acl aclEntity) {
//        this.description = description;
//        this.acl = acl;
////        this.aclEntity = aclEntity;
//    }

    public InternalClassACL(String langbundle, short acl) {
        this.description = SYSTools.xx(langbundle);
        this.acl = acl;
//        this.aclEntity = null;
    }


    public short getACLcode() {
        return acl;
    }

//    public Acl getAclEntity() {
//        return aclEntity;
//    }
//
//    public void setAclEntity(Acl aclEntity) {
//        this.aclEntity = aclEntity;
//    }
//
//    public boolean hasAclEntity() {
//        return this.aclEntity != null;
//    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description + " [" + strACLS[acl] + "]";
    }

    @Override
    public int compareTo(InternalClassACL o) {
        return new Short(acl).compareTo(o.getACLcode());
    }

}
