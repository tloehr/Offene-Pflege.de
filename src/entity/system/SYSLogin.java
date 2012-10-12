/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.system;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
//import op.tools.SYSConst;

/**
 *
 * @author tloehr
 */
@Entity
@Table(name = "SYSLogin")
//@NamedQueries({
//    @NamedQuery(name = "SYSLogin.findAll", query = "SELECT s FROM SYSLogin s"),
//    @NamedQuery(name = "SYSLogin.findByLoginID", query = "SELECT s FROM SYSLogin s WHERE s.loginID = :loginID"),
//    @NamedQuery(name = "SYSLogin.findByLogin", query = "SELECT s FROM SYSLogin s WHERE s.login = :login"),
////    @NamedQuery(name = "SYSLogin.findByHost", query = "SELECT s FROM SYSLogin s WHERE s.host = :host"),
//    @NamedQuery(name = "SYSLogin.findByLogout", query = "SELECT s FROM SYSLogin s WHERE s.logout = :logout")})
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
//    @JoinColumn(name = "HostID", referencedColumnName = "HostID")
//    @ManyToOne
//    private SYSHosts host;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "login")
//    private Collection<SYSRunningClasses> runningClasses;
    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;
        

    public SYSLogin() {
    }

    public SYSLogin(Users user) {
//        this.host = OPDE.getHost();
        this.user = user;
        this.login = new Date();
        this.logout = op.tools.SYSConst.DATE_BIS_AUF_WEITERES;
    }

    public Users getUser() {
        return user;
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
