/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offene_pflege.entity.system;

import de.offene_pflege.entity.DefaultEntity;
import de.offene_pflege.op.OPDE;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * This entity is assigned to SYSGROUPS2ACL in order to store rights for a specific user group.
 * @author tloehr
 */
@Entity
@Table(name = "acl")
@NoArgsConstructor
public class Acl extends DefaultEntity implements Serializable, Comparable<Acl> {
    @Basic(optional = false)
    @Column(name = "acl")
    private short acl;
    @JoinColumn(name = "ICID", referencedColumnName = "ICID")
    @ManyToOne
    private SYSGROUPS2ACL intclass;

    public Acl(short acl, SYSGROUPS2ACL intclass) {
        this.acl = acl;
        this.intclass = intclass;
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
    public String toString() {
        return OPDE.getAppInfo().getInternalClasses().get(intclass.getInternalClassID()).getPossibleACLs().get(acl).getDescription();
    }

    @Override
    public int compareTo(Acl o) {
        return new Integer(acl).compareTo(new Integer(o.getAcl()));
    }
}
