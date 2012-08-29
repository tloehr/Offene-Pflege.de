/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import entity.system.Users;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Diese Entity verbindet Übergaberichte mit Usern. Damit kann man
 * speichern, wer welchen Bericht im Übergabeprotokoll zur Kenntnis
 * genommen hat.
 *
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
        return getUser().getFullname().compareTo(o.getUser().getFullname());
    }


}
