/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.offene_pflege.backend.entity.done;

import de.offene_pflege.backend.entity.DefaultEntity;
import de.offene_pflege.gui.interfaces.EditorComponent;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Collection;

/**
 * @author tloehr
 */
@Entity
@Table(name = "station")
public class Station extends DefaultEntity implements Serializable, Comparable<Station> {
    @EditorComponent(label = "misc.msg.nameOfElement", component = {"textfield"})
    private String name;
    private Homes home;
    private Collection<Resident> residents;

    @Basic(optional = false)
    @Column(name = "Name")
    @NotEmpty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @JoinColumn(name = "EID", referencedColumnName = "id")
    @ManyToOne
    public Homes getHome() {
        return home;
    }

    public void setHome(Homes home) {
        this.home = home;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "station")
    public Collection<Resident> getResidents() {
        return residents;
    }

    public void setResidents(Collection<Resident> residents) {
        this.residents = residents;
    }

    public Station() {
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
