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
@Table(name = "SYSRunningClasses")
@NamedQueries({
    @NamedQuery(name = "SYSRunningClasses.findAll", query = "SELECT s FROM SYSRunningClasses s"),
    @NamedQuery(name = "SYSRunningClasses.findByRcid", query = "SELECT s FROM SYSRunningClasses s WHERE s.rcid = :rcid"),
    @NamedQuery(name = "SYSRunningClasses.findBySignature", query = "SELECT s FROM SYSRunningClasses s WHERE s.signature = :signature"),
    @NamedQuery(name = "SYSRunningClasses.findByLogin", query = "SELECT s FROM SYSRunningClasses s WHERE s.login = :login"),
    @NamedQuery(name = "SYSRunningClasses.findByStatus", query = "SELECT s FROM SYSRunningClasses s WHERE s.status = :status")})
public class SYSRunningClasses implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final short STATUS_RO = 0;
    public static final short STATUS_RW = 1;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "RCID")
    private Long rcid;
    @Basic(optional = false)
    @Column(name = "InternalClassID")
    private String classname;
    @Column(name = "Signature")
    private String signature;
    @Column(name = "STATUS")
    private Short status;
    @JoinColumn(name = "LoginID", referencedColumnName = "LoginID")
    @ManyToOne
    private SYSLogin login;

    public String getClassname() {
        return classname;
    }

    public SYSLogin getLogin() {
        return login;
    }

    public SYSRunningClasses() {
    }

    public SYSRunningClasses(String classname, String signature, Short status) {
        this.classname = classname;
        this.signature = signature;
        this.status = status;
        this.login = OPDE.getLogin();
    }


    public Long getRcid() {
        return rcid;
    }

    public void setRcid(Long rcid) {
        this.rcid = rcid;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public boolean isRW(){
        return status == STATUS_RW;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (rcid != null ? rcid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SYSRunningClasses)) {
            return false;
        }
        SYSRunningClasses other = (SYSRunningClasses) object;
        if ((this.rcid == null && other.rcid != null) || (this.rcid != null && !this.rcid.equals(other.rcid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.SYSRunningClasses[rcid=" + rcid + "]";
    }
}
