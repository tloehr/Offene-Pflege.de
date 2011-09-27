/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

/**
 * Diese Entity verbindet Übergaberichte mit Usern. Damit kann man 
 * speichern, wer welchen Bericht im Übergabeprotokoll zur Kenntnis
 * genommen hat.
 * @author tloehr
 */
@Entity
@Table(name = "Uebergabe2User")
@NamedQueries({
    @NamedQuery(name = "Uebergabe2User.findAll", query = "SELECT u FROM Uebergabe2User u"),
    @NamedQuery(name = "Uebergabe2User.findByPkid", query = "SELECT u FROM Uebergabe2User u WHERE u.pkid = :pkid"),
    @NamedQuery(name = "Uebergabe2User.findByPit", query = "SELECT u FROM Uebergabe2User u WHERE u.pit = :pit")})
public class Uebergabe2User implements Serializable, Comparable<Uebergabe2User> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "PKID")
    private Long pkid;
    @Basic(optional = false)
    @Column(name = "PIT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pit;
    @JoinColumn(name = "UEBID", referencedColumnName = "UEBID")
    @ManyToOne
    private Uebergabebuch bericht;
    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;

    public Uebergabe2User() {
    }

    public Uebergabe2User(Uebergabebuch bericht, Users user) {
        this.bericht = bericht;
        this.user = user;
        this.pit = new Date();
    }

    public Long getPkid() {
        return pkid;
    }

    public void setPkid(Long pkid) {
        this.pkid = pkid;
    }

    public Date getPit() {
        return pit;
    }

    public void setPit(Date pit) {
        this.pit = pit;
    }

    public Uebergabebuch getBericht() {
        return bericht;
    }

    public Users getUser() {
        return user;
    }

    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pkid != null ? pkid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Uebergabe2User)) {
            return false;
        }
        Uebergabe2User other = (Uebergabe2User) object;
        if ((this.pkid == null && other.pkid != null) || (this.pkid != null && !this.pkid.equals(other.pkid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Uebergabe2User[pkid=" + pkid + "]";
    }

    @Override
    public int compareTo(Uebergabe2User o) {
        return getUser().getNameUndVorname().compareTo(o.getUser().getNameUndVorname());
    }


}
