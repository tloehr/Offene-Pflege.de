/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.building;

import entity.info.Resident;
import entity.qms.Qmssched;
import gui.interfaces.EditorComponent;
import gui.interfaces.NotRemovableUnlessEmpty;
import op.tools.SYSTools;
import org.hibernate.validator.constraints.NotEmpty;

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
    @EditorComponent(label = "misc.msg.primary.key", component = {"textfield"}, readonly = "true")
    private Long statID;
    @Basic(optional = false)
    @Column(name = "Name")
    @NotEmpty
    @EditorComponent(label = "misc.msg.nameOfElement", component = {"textfield"})
    private String name;
    @Version
    @Column(name = "version")
    private Long version;
    @JoinColumn(name = "EID", referencedColumnName = "EID")
    @ManyToOne
    private Homes home;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "station")
    @NotRemovableUnlessEmpty
    private Collection<Resident> residents;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "station")
    @NotRemovableUnlessEmpty
    private Collection<Qmssched> qmsscheds;

    public Collection<Qmssched> getQmsscheds() {
        return qmsscheds;
    }

    public Station() {
    }

    public Station(String name, Homes home) {
        this.name = SYSTools.tidy(name);
        this.home = home;
        residents = new ArrayList<>();
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
