/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.*;

/**
 * This entity connects the user groups of OPDE with inner XML structure defined in appinfo.xml
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
    + "WHERE i.internalClassID = :classname AND :ocuser MEMBER OF i.groups.members AND EXISTS (SELECT a FROM Acl a WHERE a.acl = :shortacl)"  ),
    @NamedQuery(name = "IntClasses.findByGroup", query = " "
    + "SELECT i FROM IntClasses i WHERE i.groups = :gruppe "),
    @NamedQuery(name = "IntClasses.findByClassname", query = "SELECT i FROM IntClasses i WHERE i.internalClassID = :classname")})
public class IntClasses implements Serializable {

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
    private Groups groups;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "intclass")
    private Collection<Acl> aclCollection;

    public IntClasses() {
    }

    public IntClasses(String internalClassID, Groups groups) {
        this.internalClassID = internalClassID;
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
        return "entity.system.IntClasses[" + internalClassID + " " + icid + "]";
    }
}
