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

/**
 *
 * @author tloehr
 */
@Entity
@Table(name = "SYSProps")
@NamedQueries({
    @NamedQuery(name = "SYSProps.findAll", query = "SELECT s FROM SYSProps s"),
    @NamedQuery(name = "SYSProps.findBySyspid", query = "SELECT s FROM SYSProps s WHERE s.syspid = :syspid"),
    @NamedQuery(name = "SYSProps.findByKey", query = "SELECT s FROM SYSProps s WHERE s.key = :key"),
    @NamedQuery(name = "SYSProps.findByKeyAndUser", query = "SELECT s FROM SYSProps s WHERE s.key = :key AND s.user = :user "),
    @NamedQuery(name = "SYSProps.findByValue", query = "SELECT s FROM SYSProps s WHERE s.value = :value"),
    @NamedQuery(name = "SYSProps.findAllWOUsers", query = "SELECT s FROM SYSProps s WHERE s.user IS NULL"),
    @NamedQuery(name = "SYSProps.findByUser", query = "SELECT s FROM SYSProps s WHERE s.user = :user ")})
public class SYSProps implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
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
    }

    public SYSProps(String key, String value, Users user) {
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
        // TODO: Warning - this method won't work in the case the id fields are not set
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
        return "entity.SYSProps[syspid=" + syspid + "]";
    }
}
