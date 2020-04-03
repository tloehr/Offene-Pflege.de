package de.offene_pflege.backend.entity.done;

import de.offene_pflege.backend.entity.DefaultEntity;
import de.offene_pflege.gui.interfaces.EditorComponent;
import de.offene_pflege.gui.interfaces.NotRemovableUnlessEmpty;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;

/**
 * Created by tloehr on 04.05.15.
 */
@Entity
@Table(name = "floors")
public class Floors extends DefaultEntity implements Comparable<Floors> {
    private Homes home;
    private String name;
    private Integer level;
    private Integer lift;  // number of lifts connecting to this floor
    private List<Rooms> rooms;

    public Floors() {
    }

    @JoinColumn(name = "HomeID", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    public Homes getHome() {
        return home;
    }

    public void setHome(Homes home) {
        this.home = home;
    }

    @Basic
    @Column(name = "name", nullable = true, insertable = true, updatable = true, length = 30)
    @Size(min = 1, max = 30)
    @NotEmpty
    @EditorComponent(label = "misc.msg.nameOfElement", component = {"textfield"})
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "level", nullable = true, insertable = true, updatable = true)
    @Min(0)
    @Max(8)
    @EditorComponent(label = "misc.msg.floor", component = {"combobox", "2.Untergeschoss", "1.Untergeschoss", "Erdgeschoss", "1.Etage", "2.Etage", "3.Etage", "4.Etage", "5.Etage", "6.Etage"})
    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @Basic
    @Column(name = "lift", nullable = true, insertable = true, updatable = true)
    @Min(0)
    @Max(10)
    @EditorComponent(label = "misc.msg.lifts", parserClass = "gui.parser.IntegerParser", component = {"textfield"}, tooltip = "misc.msg.lifts.connecting.this.floor")
    public Integer getLift() {
        return lift;
    }

    public void setLift(Integer lift) {
        this.lift = lift;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "floor", fetch = FetchType.EAGER)
    @NotRemovableUnlessEmpty(message = "msg.cantberemoved.rooms.assigned")
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
