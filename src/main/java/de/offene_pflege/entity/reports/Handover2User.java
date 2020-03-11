/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offene_pflege.entity.reports;

import de.offene_pflege.entity.system.Users;

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
@Table(name = "handover2user")

public class Handover2User implements Serializable, Comparable<Handover2User> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PKID")
    private Long pkid;
    @Basic(optional = false)
    @Column(name = "PIT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pit;
    @JoinColumn(name = "HID", referencedColumnName = "HID")
    @ManyToOne
    private Handovers bericht;
    @JoinColumn(name = "UID", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;

    public Handover2User() {
    }

    public Handover2User(Handovers bericht, Users user) {
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

    public Handovers getHandover() {
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

        if (!(object instanceof Handover2User)) {
            return false;
        }
        Handover2User other = (Handover2User) object;
        if ((this.pkid == null && other.pkid != null) || (this.pkid != null && !this.pkid.equals(other.pkid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.reports.Handover2User[pkid=" + pkid + "]";
    }

    @Override
    public int compareTo(Handover2User o) {
        return pit.compareTo(o.getPit()); //getUser().getFullname().compareTo(o.getUser().getFullname());
    }


}
