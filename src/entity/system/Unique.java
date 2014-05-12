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
@Table(name = "uniqueid")

public class Unique implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    /**
     * a unique long number for the given PREFIX.
     * @return
     */
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
