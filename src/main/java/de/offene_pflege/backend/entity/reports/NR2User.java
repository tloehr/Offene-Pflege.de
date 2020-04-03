/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.offene_pflege.backend.entity.reports;

import de.offene_pflege.backend.entity.system.OPUsers;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Diese Entity verbindet NReport mit Usern. Damit kann man
 * speichern, wer welchen Pflegebericht im Übergabeprotokoll zur Kenntnis
 * genommen hat.
 *
 * @author tloehr
 */
@Entity
@Table(name = "nr2user")
public class NR2User implements Serializable, Comparable<NR2User> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PKID")
    private Long pkid;
    @Basic(optional = false)
    @Column(name = "PIT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pit;
    @JoinColumn(name = "PBID", referencedColumnName = "PBID")
    @ManyToOne
    private NReport bericht;
    @JoinColumn(name = "UID", referencedColumnName = "UKennung")
    @ManyToOne
    private OPUsers user;

    public NR2User() {
    }

    public NR2User(Long pkid) {
        this.pkid = pkid;
    }

    public NR2User(NReport bericht, OPUsers user) {
        this.bericht = bericht;
        this.user = user;
        this.pit = new Date();
    }

    public NReport getBericht() {
        return bericht;
    }

    public OPUsers getUser() {
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

        if (!(object instanceof NR2User)) {
            return false;
        }
        NR2User other = (NR2User) object;
        if ((this.pkid == null && other.pkid != null) || (this.pkid != null && !this.pkid.equals(other.pkid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.reports.NR2User[pkid=" + pkid + "]";
    }

    @Override
    public int compareTo(NR2User o) {
        return pit.compareTo(o.getPit());
    }


}
