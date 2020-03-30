/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offene_pflege.entity.system;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * @author tloehr
 */
@Entity
@Table(name = "opgroups")
public class OPGroups implements Serializable, Comparable<OPGroups> {

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
    @Column(name = "sysflag")
    private boolean sysflag;
    @Basic(optional = false)
    @Column(name = "Examen")
    private boolean qualified;
    @ManyToMany(mappedBy = "groups")
    private Collection<OPUsers> members;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "opgroups")
    private Collection<SYSGROUPS2ACL> icCollection;

    public OPGroups() {
        this.gid = null;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public void setMembers(Collection<OPUsers> members) {
        this.members = members;
    }

    public Collection<OPUsers> getMembers() {
        return members;
    }

    public Collection<SYSGROUPS2ACL> getIntClasses() {
        return icCollection;
    }

    public String getGID() {
        return gid;
    }

    public boolean isSysflag() {
        return sysflag;
    }

    public void setSysflag(boolean system) {
        this.sysflag = system;
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
    public int compareTo(OPGroups o) {
        return gid.toLowerCase().compareTo(o.getGID().toLowerCase());
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (gid != null ? gid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof OPGroups)) {
            return false;
        }
        OPGroups other = (OPGroups) object;
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
