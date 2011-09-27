/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author tloehr
 */
@Entity
@Table(name = "IntClasses")
@NamedQueries({
    @NamedQuery(name = "IntClasses.findAll", query = "SELECT i FROM IntClasses i"),
    @NamedQuery(name = "IntClasses.findByIcid", query = "SELECT i FROM IntClasses i WHERE i.icid = :icid"),
    @NamedQuery(name = "IntClasses.findByUserAndClassnameAndACL", query = " "
    + "SELECT i FROM IntClasses i "
    + "WHERE i.classname = :classname AND :ocuser MEMBER OF i.groups.members AND EXISTS (SELECT a FROM Acl a WHERE a.acl = :shortacl)"  ),
    @NamedQuery(name = "IntClasses.findByGroup", query = " "
    + "SELECT i FROM IntClasses i WHERE i.groups = :gruppe "),
    @NamedQuery(name = "IntClasses.findByClassname", query = "SELECT i FROM IntClasses i WHERE i.classname = :classname")})
public class IntClasses implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ICID")
    private Long icid;
    @Basic(optional = false)
    @Column(name = "classname")
    private String classname;
    @JoinColumn(name = "gkennung", referencedColumnName = "GKENNUNG")
    @ManyToOne
    private Groups groups;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "intclass")
    private Collection<Acl> aclCollection;

    public IntClasses() {
    }

    public IntClasses(String classname, Groups groups) {
        this.classname = classname;
        this.groups = groups;
        this.aclCollection = new ArrayList();
    }

    public Long getIcid() {
        return icid;
    }

    public void setIcid(Long icid) {
        this.icid = icid;
    }

    public Groups getGroups() {
        return groups;
    }

    public void setGroups(Groups groups) {
        this.groups = groups;
    }

    public Collection<Acl> getAclCollection() {
        return aclCollection;
    }

    public void setAclCollection(Collection<Acl> aclCollection) {
        this.aclCollection = aclCollection;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (icid != null ? icid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof IntClasses)) {
            return false;
        }
        IntClasses other = (IntClasses) object;
        if ((this.icid == null && other.icid != null) || (this.icid != null && !this.icid.equals(other.icid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.IntClasses[" + classname + " " + icid + "]";
    }
}
