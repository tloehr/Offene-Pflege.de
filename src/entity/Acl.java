/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import op.OPDE;

/**
 *
 * @author tloehr
 */
@Entity
@Table(name = "ACL")
@NamedQueries({
    @NamedQuery(name = "Acl.findAll", query = "SELECT a FROM Acl a"),
    @NamedQuery(name = "Acl.findByAclid", query = "SELECT a FROM Acl a WHERE a.aclid = :aclid"),
    @NamedQuery(name = "Acl.findByAclidAndSHORTACL", query = " "
    + " SELECT a FROM Acl a "
    + " WHERE a.intclass.classname = :classname AND a.acl = :shortacl AND a.intclass.groups = :gruppe"),
    @NamedQuery(name = "Acl.findByAcl", query = "SELECT a FROM Acl a WHERE a.acl = :acl")})
public class Acl implements Serializable, Comparable<Acl> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ACLID")
    private Long aclid;
    @Basic(optional = false)
    @Column(name = "acl")
    private short acl;
    @JoinColumn(name = "ICID", referencedColumnName = "ICID")
    @ManyToOne
    private IntClasses intclass;

    public Acl() {
    }

    public Acl(short acl, IntClasses intclass) {
        this.acl = acl;
        this.intclass = intclass;
    }

    public Long getAclid() {
        return aclid;
    }

    public void setAclid(Long aclid) {
        this.aclid = aclid;
    }

    public IntClasses getIntclass() {
        return intclass;
    }

    public void setIntclass(IntClasses intclass) {
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
        // TODO: Warning - this method won't work in the case the id fields are not set
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
        return OPDE.getAppInfo().getInternalClasses().get(intclass.getClassname()).getAcls().get(acl).getDescription();
    }

    @Override
    public int compareTo(Acl o) {
        return new Integer(acl).compareTo(new Integer(o.getAcl()));
    }
}
