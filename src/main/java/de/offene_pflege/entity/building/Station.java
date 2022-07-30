/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.offene_pflege.entity.building;

import de.offene_pflege.entity.DefaultEntity;
import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.gui.interfaces.EditorComponent;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Collection;

/**
 * @author tloehr
 */
@Entity
@Table(name = "station")
@Getter
@Setter
public class Station extends DefaultEntity implements Serializable, Comparable<Station> {
    @EditorComponent(label = "misc.msg.nameOfElement", component = {"textfield"})
    @Basic(optional = false)
    @Column(name = "Name")
    @NotEmpty
    private String name;
    @JoinColumn(name = "EID", referencedColumnName = "id")
    @ManyToOne
    private Homes home;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "station")
    private Collection<Resident> residents;

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Station o) {
        return getName().toLowerCase().compareTo(o.getName().toLowerCase());
    }
}
