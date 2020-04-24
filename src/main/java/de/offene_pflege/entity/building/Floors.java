package de.offene_pflege.entity.building;


import de.offene_pflege.entity.DefaultEntity;
import de.offene_pflege.gui.interfaces.EditorComponent;
import de.offene_pflege.gui.interfaces.NotRemovableUnlessEmpty;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;

@Entity
@Table(name = "floors")
public class Floors extends DefaultEntity implements Comparable<Floors> {
    @NotNull
    private Homes home;
    @Size(min = 1, max = 30)
    @NotEmpty
    @EditorComponent(label = "misc.msg.nameOfElement", component = {"textfield"})
    private String name;
    @Min(0)
    @Max(8)
    @EditorComponent(label = "misc.msg.floor", component = {"combobox", "2.Untergeschoss", "1.Untergeschoss", "Erdgeschoss", "1.Etage", "2.Etage", "3.Etage", "4.Etage", "5.Etage", "6.Etage"})
    private Integer level;
    @Min(0)
    @Max(10)
    @EditorComponent(label = "misc.msg.lifts", parserClass = "de.offene_pflege.gui.parser.IntegerParser", component = {"textfield"}, tooltip = "misc.msg.lifts.connecting.this.floor")
    private Integer lift;  // number of lifts connecting to this floor
    @NotRemovableUnlessEmpty(message = "msg.cantberemoved.rooms.assigned")
    private List<Rooms> rooms;

    public Floors() {
    }

    @JoinColumn(name = "HomeID", referencedColumnName = "id")
    @ManyToOne
    public Homes getHome() {
        return home;
    }

    public void setHome(Homes home) {
        this.home = home;
    }

    @Basic
    @Column(name = "name", nullable = true, insertable = true, updatable = true, length = 30)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "level", nullable = true, insertable = true, updatable = true)
    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @Basic
    @Column(name = "lift", nullable = true, insertable = true, updatable = true)
    public Integer getLift() {
        return lift;
    }

    public void setLift(Integer lift) {
        this.lift = lift;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "floor", fetch = FetchType.EAGER)

    public List<Rooms> getRooms() {
        return rooms;
    }

    public void setRooms(List<Rooms> rooms) {
        this.rooms = rooms;
    }

    @Override
    public int compareTo(Floors o) {
        return level.compareTo(o.getLevel());
    }

}
