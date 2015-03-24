/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.building;

import entity.info.Resident;
import op.tools.SYSTools;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author tloehr
 */
@Entity
@Table(name = "station")

public class Station implements Serializable, Comparable<Station> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "StatID")
    private Long statID;
    @Basic(optional = false)
    @Column(name = "Name")
    private String name;
    @Version
    @Column(name = "version")
    private Long version;
    @JoinColumn(name = "EID", referencedColumnName = "EID")
    @ManyToOne
    private Homes home;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "station")
    private Collection<Rooms> rooms;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "station")
    private Collection<Resident> residents;

    public Station() {
    }

    public Station(String name, Homes home) {
        this.name = SYSTools.tidy(name);
        this.home = home;
        rooms = new ArrayList<Rooms>();
        residents = new ArrayList<Resident>();
    }

    public Long getStatID() {
        return statID;
    }

    public void setStatID(Long statID) {
        this.statID = statID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = SYSTools.tidy(name);
    }

    public Homes getHome() {
        return home;
    }

    public Collection<Resident> getResidents() {
        return residents;
    }

    public Collection<Rooms> getRooms() {
        return rooms;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (statID != null ? statID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof Station)) {
            return false;
        }
        Station other = (Station) object;
        if ((this.statID == null && other.statID != null) || (this.statID != null && !this.statID.equals(other.statID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Station o) {
        return getName().toLowerCase().compareTo(o.getName().toLowerCase());
    }
}
