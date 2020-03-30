/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offene_pflege.entity.system;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This entity connects the user groups of OPDE with inner XML structure defined in appinfo.xml
 *
 * @author tloehr
 */
@Entity
@Table(name = "sysgroups2acl")
public class SYSGROUPS2ACL implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ICID")
    private Long icid;
    @Basic(optional = false)
    @Column(name = "internalClassesID")
    private String internalClassID;
    @Version
    @Column(name = "version")
    private Long version;
    @JoinColumn(name = "gid", referencedColumnName = "GKENNUNG")
    @ManyToOne
    private OPGroups opgroups;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "intclass")
    private Collection<Acl> aclCollection;

    public SYSGROUPS2ACL() {
    }

    public SYSGROUPS2ACL(String internalClassID, OPGroups opgroups) {
        this.internalClassID = internalClassID;
        this.opgroups = opgroups;
        this.aclCollection = new ArrayList();
    }

    public Long getIcid() {
        return icid;
    }

    public void setIcid(Long icid) {
        this.icid = icid;
    }

    public OPGroups getOPGroups() {
        return opgroups;
    }

    public void setOPGroups(OPGroups opgroups) {
        this.opgroups = opgroups;
    }

    public Collection<Acl> getAclCollection() {
        return aclCollection;
    }

    public void setAclCollection(Collection<Acl> aclCollection) {
        this.aclCollection = aclCollection;
    }

    public String getInternalClassID() {
        return internalClassID;
    }

    public void setClassname(String classname) {
        this.internalClassID = classname;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (icid != null ? icid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof SYSGROUPS2ACL)) {
            return false;
        }
        SYSGROUPS2ACL other = (SYSGROUPS2ACL) object;
        if ((this.icid == null && other.icid != null) || (this.icid != null && !this.icid.equals(other.icid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.system.SYSGROUPS2ACL[" + internalClassID + " " + icid + "]";
    }
}
