/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.system;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * @author tloehr
 */
@Entity
@Table(name = "SYSHosts")
@NamedQueries({
        @NamedQuery(name = "SYSHosts.findAll", query = "SELECT s FROM SYSHosts s"),
        @NamedQuery(name = "SYSHosts.findByHostID", query = "SELECT s FROM SYSHosts s WHERE s.hostID = :hostID"),
        @NamedQuery(name = "SYSHosts.findByHostKey", query = "SELECT s FROM SYSHosts s WHERE s.hostKey = :hostKey"),
        @NamedQuery(name = "SYSHosts.findByHostname", query = "SELECT s FROM SYSHosts s WHERE s.hostname = :hostname"),
        @NamedQuery(name = "SYSHosts.findByIp", query = "SELECT s FROM SYSHosts s WHERE s.ip = :ip"),
        @NamedQuery(name = "SYSHosts.findOtherRunningMainHosts", query = "SELECT s FROM SYSHosts s WHERE s.mainHost = TRUE AND s.hostKey = :hostKey AND s.lpol IS NOT null "),
        @NamedQuery(name = "SYSHosts.findByLpol", query = "SELECT s FROM SYSHosts s WHERE s.lpol = :lpol")})
public class SYSHosts implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "HostID")
    private Long hostID;
    @Basic(optional = false)
    @Column(name = "HostKey")
    private String hostKey;
    @Column(name = "Hostname")
    private String hostname;
    @Column(name = "IP")
    private String ip;
    @Column(name = "MainHost")
    private Boolean mainHost;
    @Column(name = "LPOL")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lpol;
    @Column(name = "UP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date up;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "host")
    private Collection<SYSLogin> logins;

    public SYSHosts() {
    }

    public SYSHosts(String hostKey, String hostname, String ip, Boolean mainHost) {
        this.hostKey = hostKey;
        this.hostname = hostname;
        this.ip = ip;
        this.mainHost = mainHost;
        this.lpol = null;
    }

    public Long getHostID() {
        return hostID;
    }

    public void setHostID(Long hostID) {
        this.hostID = hostID;
    }

    public String getHostKey() {
        return hostKey;
    }

    public void setHostKey(String hostKey) {
        this.hostKey = hostKey;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Boolean getMainHost() {
        return mainHost;
    }

    public void setMainHost(Boolean mainHost) {
        this.mainHost = mainHost;
    }

    public Date getLpol() {
        return lpol;
    }

    public void setLpol(Date lpol) {
        this.lpol = lpol;
    }

    public Date getUp() {
        return up;
    }

    public void setUp(Date up) {
        this.up = up;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (hostID != null ? hostID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SYSHosts)) {
            return false;
        }
        SYSHosts other = (SYSHosts) object;
        if ((this.hostID == null && other.hostID != null) || (this.hostID != null && !this.hostID.equals(other.hostID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.system.SYSHosts[hostID=" + hostID + "]";
    }
}
