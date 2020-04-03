/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offene_pflege.backend.entity.system;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author tloehr
 */
@Entity
@Table(name = "sysprops")

public class SYSProps implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SYSPID")
    private Long syspid;
    @Basic(optional = false)
    @Column(name = "K")
    private String key;
    @Basic(optional = false)
    @Column(name = "V")
    private String value;
    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private OPUsers user;

    public OPUsers getUser() {
        return user;
    }

    public void setUser(OPUsers user) {
        this.user = user;
    }

    public SYSProps() {
    }

    public SYSProps(String key, String value) {
        this.key = key;
        this.value = value;
        this.user = null;
    }

    public SYSProps(String key, String value, OPUsers user) {
        this.key = key;
        this.value = value;
        this.user = user;
    }

    public Long getSyspid() {
        return syspid;
    }

    public void setSyspid(Long syspid) {
        this.syspid = syspid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (syspid != null ? syspid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof SYSProps)) {
            return false;
        }
        SYSProps other = (SYSProps) object;
        if ((this.syspid == null && other.syspid != null) || (this.syspid != null && !this.syspid.equals(other.syspid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.system.SYSProps[syspid=" + syspid + "]";
    }
}
