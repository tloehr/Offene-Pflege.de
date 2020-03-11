/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offene_pflege.entity.system;

import de.offene_pflege.op.OPDE;

import javax.persistence.*;
import java.io.Serializable;

/**
 * This entity is assigned to SYSGROUPS2ACL in order to store rights for a specific user group.
 * @author tloehr
 */
@Entity
@Table(name = "acl")

public class Acl implements Serializable, Comparable<Acl> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACLID")
    private Long aclid;
    @Basic(optional = false)
    @Column(name = "acl")
    private short acl;
    @JoinColumn(name = "ICID", referencedColumnName = "ICID")
    @ManyToOne
    private SYSGROUPS2ACL intclass;

    public Acl() {
    }

    public Acl(short acl, SYSGROUPS2ACL intclass) {
        this.acl = acl;
        this.intclass = intclass;
    }

    public Long getAclid() {
        return aclid;
    }

    public SYSGROUPS2ACL getIntclass() {
        return intclass;
    }

    public void setIntclass(SYSGROUPS2ACL intclass) {
        this.intclass = intclass;
    }

    public short getAcl() {
        return acl;
    }

    public void setAcl(short acl) {
        this.acl = acl;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (aclid != null ? aclid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof Acl)) {
            return false;
        }
        Acl other = (Acl) object;
        if ((this.aclid == null && other.aclid != null) || (this.aclid != null && !this.aclid.equals(other.aclid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return OPDE.getAppInfo().getInternalClasses().get(intclass.getInternalClassID()).getPossibleACLs().get(acl).getDescription();
    }

    @Override
    public int compareTo(Acl o) {
        return new Integer(acl).compareTo(new Integer(o.getAcl()));
    }
}
