/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.offene_pflege.entity.system;

import de.offene_pflege.op.tools.SYSConst;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 *
 * @author tloehr
 */
@Entity
@Table(name = "syslogin")
public class SYSLogin implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LoginID")
    private Long loginID;
    @Basic(optional = false)
    @Column(name = "Login")
    @Temporal(TemporalType.TIMESTAMP)
    private Date login;
    @Basic(optional = false)
    @Column(name = "Logout")
    @Temporal(TemporalType.TIMESTAMP)
    private Date logout;
    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;
        

    public SYSLogin() {
    }

    public SYSLogin(Users user) {
//        this.host = OPDE.getHost();
        this.user = user;
        this.login = new Date();
        this.logout = SYSConst.DATE_UNTIL_FURTHER_NOTICE;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Long getLoginID() {
        return loginID;
    }

    public void setLoginID(Long loginID) {
        this.loginID = loginID;
    }

    public Date getLogin() {
        return login;
    }

    public void setLogin(Date login) {
        this.login = login;
    }

    public Date getLogout() {
        return logout;
    }

    public void setLogout(Date logout) {
        this.logout = logout;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (loginID != null ? loginID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof SYSLogin)) {
            return false;
        }
        SYSLogin other = (SYSLogin) object;
        if ((this.loginID == null && other.loginID != null) || (this.loginID != null && !this.loginID.equals(other.loginID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.system.SYSLogin[loginID=" + loginID + "]";
    }

}
