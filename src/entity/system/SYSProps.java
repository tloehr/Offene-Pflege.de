/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.system;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

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
    @Basic(optional = false)
    @Column(name = "lastchange")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastchange;
    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public SYSProps() {
    }

    public SYSProps(String key, String value) {
        this.key = key;
        this.value = value;
        this.user = null;
        this.lastchange = new Date();
    }

    public SYSProps(String key, String value, Users user) {
        this.key = key;
        this.value = value;
        this.user = user;
        this.lastchange = new Date();
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
        this.lastchange = new Date();
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.lastchange = new Date();
        this.value = value;
    }

    public Date getLastchange() {
        return lastchange;
    }

    public void setLastchange(Date lastchange) {
        this.lastchange = lastchange;
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
