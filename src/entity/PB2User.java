/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Diese Entity verbindet Pflegeberichte mit Usern. Damit kann man
 * speichern, wer welchen Pflegebericht im Übergabeprotokoll zur Kenntnis
 * genommen hat.
 *
 * @author tloehr
 */
@Entity
@Table(name = "PB2User")
@NamedQueries({
        @NamedQuery(name = "PB2User.findAll", query = "SELECT p FROM PB2User p"),
        @NamedQuery(name = "PB2User.findByPkid", query = "SELECT p FROM PB2User p WHERE p.pkid = :pkid"),
        @NamedQuery(name = "PB2User.findByPit", query = "SELECT p FROM PB2User p WHERE p.pit = :pit")})
public class PB2User implements Serializable, Comparable<PB2User> {
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
    @JoinColumn(name = "PBID", referencedColumnName = "PBID")
    @ManyToOne
    private Pflegeberichte bericht;
    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;

    public PB2User() {
    }

    public PB2User(Long pkid) {
        this.pkid = pkid;
    }

    public PB2User(Pflegeberichte bericht, Users user) {
        this.bericht = bericht;
        this.user = user;
        this.pit = new Date();
    }

    public Pflegeberichte getBericht() {
        return bericht;
    }

    public Users getUser() {
        return user;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pkid != null ? pkid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof PB2User)) {
            return false;
        }
        PB2User other = (PB2User) object;
        if ((this.pkid == null && other.pkid != null) || (this.pkid != null && !this.pkid.equals(other.pkid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.PB2User[pkid=" + pkid + "]";
    }

    @Override
    public int compareTo(PB2User o) {
        return getUser().getNameUndVorname().compareTo(o.getUser().getNameUndVorname());
    }


}
