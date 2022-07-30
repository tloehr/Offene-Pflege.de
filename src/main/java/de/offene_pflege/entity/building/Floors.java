package de.offene_pflege.entity.building;


import de.offene_pflege.entity.DefaultEntity;
import de.offene_pflege.gui.interfaces.EditorComponent;
import de.offene_pflege.gui.interfaces.NotRemovableUnlessEmpty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;

@Entity
@Table(name = "floors")
@Getter
@Setter
public class Floors extends DefaultEntity implements Comparable<Floors> {
    @NotNull
    @JoinColumn(name = "HomeID", referencedColumnName = "id")
    @ManyToOne
    private Homes home;
    @Size(min = 1, max = 30)
    @NotEmpty
    @EditorComponent(label = "misc.msg.nameOfElement", component = {"textfield"})
    @Basic
    @Column(name = "name", nullable = true, insertable = true, updatable = true, length = 30)
    private String name;
    @Min(0)
    @Max(8)
    @EditorComponent(label = "misc.msg.floor", component = {"combobox", "2.Untergeschoss", "1.Untergeschoss", "Erdgeschoss", "1.Etage", "2.Etage", "3.Etage", "4.Etage", "5.Etage", "6.Etage"})
    @Basic
    @Column(name = "level", nullable = true, insertable = true, updatable = true)
    private Integer level;
    @Min(0)
    @Max(10)
    @EditorComponent(label = "misc.msg.lifts", parserClass = "de.offene_pflege.gui.parser.IntegerParser", component = {"textfield"}, tooltip = "misc.msg.lifts.connecting.this.floor")
    @Basic
    @Column(name = "lift", nullable = true, insertable = true, updatable = true)
    private Integer lift;  // number of lifts connecting to this floor
    @NotRemovableUnlessEmpty(message = "msg.cantberemoved.rooms.assigned")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "floor", fetch = FetchType.EAGER)
    private List<Rooms> rooms;

    @Override
    public int compareTo(Floors o) {
        return level.compareTo(o.getLevel());
    }
}
