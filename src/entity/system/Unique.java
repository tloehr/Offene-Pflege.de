/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.system;

import java.io.Serializable;
import javax.persistence.*;

/**
 *
 * @author tloehr
 */
@Entity
@Table(name = "UNIQUEID")
@NamedQueries({
    @NamedQuery(name = "Uniqueid.findAll", query = "SELECT u FROM Unique u"),
    @NamedQuery(name = "Uniqueid.findByUniqid", query = "SELECT u FROM Unique u WHERE u.uniqid = :uniqid"),
    @NamedQuery(name = "Uniqueid.findByUid", query = "SELECT u FROM Unique u WHERE u.uid = :uid"),
    @NamedQuery(name = "Uniqueid.findByPrefix", query = "SELECT u FROM Unique u WHERE u.prefix = :prefix")})
public class Unique implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "UNIQID")
    private Long uniqid;
    @Basic(optional = false)
    @Column(name = "UID")
    private long uid;
    @Basic(optional = false)
    @Column(name = "PREFIX")
    private String prefix;
    @Version
    @Column(name = "version")
    private Long version;

    public Unique() {
    }

    public Unique(Long uniqid) {
        this.uniqid = uniqid;
    }

    public Unique(String prefix) {
        this.uid = 1l;
        this.prefix = prefix;
    }

    public Long getUniqid() {
        return uniqid;
    }

    public void setUniqid(Long uniqid) {
        this.uniqid = uniqid;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void incUID(){
        this.uid++;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uniqid != null ? uniqid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Unique)) {
            return false;
        }
        Unique other = (Unique) object;
        if ((this.uniqid == null && other.uniqid != null) || (this.uniqid != null && !this.uniqid.equals(other.uniqid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.Uniqueid[uniqid=" + uniqid + "]";
    }

}
