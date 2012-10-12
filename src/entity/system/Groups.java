/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.system;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * @author tloehr
 */
@Entity
@Table(name = "OCGroups")
//@NamedQueries({
//        @NamedQuery(name = "Groups.findAll", query = "SELECT g FROM Groups g"),
//        @NamedQuery(name = "Groups.findAllSorted", query = "SELECT g FROM Groups g ORDER BY g.gid"),
//        @NamedQuery(name = "Groups.findAllUnassigned", query = "SELECT g FROM Groups g WHERE :ocuser NOT MEMBER OF g.members ORDER BY g.gid"),
//        @NamedQuery(name = "Groups.findByUserAndAdmin", query = "SELECT g FROM Groups g WHERE g.gid = 'admin' AND :user MEMBER OF g.members "),
//        @NamedQuery(name = "Groups.findByUserAndExamen", query = "SELECT g FROM Groups g WHERE g.qualified = true AND :user MEMBER OF g.members "),
//        @NamedQuery(name = "Groups.findByGkennung", query = "SELECT g FROM Groups g WHERE g.gid = :gkennung")})
public class Groups implements Serializable, Comparable<Groups> {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "GKENNUNG")
    private String gid;
    @Lob
    @Column(name = "Beschreibung")
    private String description;
    @Version
    @Column(name = "version")
    private Long version;
    @Basic(optional = false)
    @Column(name = "System")
    private boolean system;
    @Basic(optional = false)
    @Column(name = "Examen")
    private boolean qualified;
    @ManyToMany(mappedBy = "groups")
    private Collection<Users> members;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "groups")
    private Collection<SYSGROUPS2ACL> icCollection;

    public Groups() {
        this.gid = null;
    }

    public void setMembers(Collection<Users> members) {
        this.members = members;
    }

    public Collection<Users> getMembers() {
        return members;
    }

    public Collection<SYSGROUPS2ACL> getIntClasses() {
        return icCollection;
    }

    public String getGID() {
        return gid;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    public boolean isQualified() {
        return qualified;
    }

    public boolean isAdmin(){
        return gid.equals("admin");
    }

    public boolean isEveryone(){
        return gid.equals("everyone");
    }

    public void setQualified(boolean qualified) {
        this.qualified = qualified;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int compareTo(Groups o) {
        return gid.compareTo(o.getGID());
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (gid != null ? gid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof Groups)) {
            return false;
        }
        Groups other = (Groups) object;
        if ((this.gid == null && other.gid != null) || (this.gid != null && !this.gid.equals(other.gid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "[" + gid.toUpperCase() + "] " + description;
    }
}
