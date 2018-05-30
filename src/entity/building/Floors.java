package entity.building;

import gui.interfaces.EditorComponent;
import gui.interfaces.NotRemovableUnlessEmpty;
import op.tools.SYSTools;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tloehr on 04.05.15.
 */
@Entity
@Table(name = "floors")
public class Floors {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "floorid")
    @EditorComponent(label = "misc.msg.primary.key", component = {"textfield"}, readonly = "true")
    private long floorid;

    @JoinColumn(name = "HomeID", referencedColumnName = "EID")
    @ManyToOne
    @NotNull
    private Homes home;

    @Basic
    @Column(name = "name", nullable = true, insertable = true, updatable = true, length = 30)
    @Size(min = 1, max = 30)
    @NotEmpty
    @EditorComponent(label = "misc.msg.nameOfElement", component = {"textfield"})
    private String name;

    @Basic
    @Column(name = "level", nullable = true, insertable = true, updatable = true)
    @Min(0)
    @Max(8)
    @EditorComponent(label = "misc.msg.floor", component = {"combobox", "2.Untergeschoss", "1.Untergeschoss", "Erdgeschoss", "1.Etage", "2.Etage", "3.Etage", "4.Etage", "5.Etage", "6.Etage"})
    private Integer level;

    @Basic
    @Column(name = "lift", nullable = true, insertable = true, updatable = true)
    @Min(0)
    @Max(10)
    @EditorComponent(label = "misc.msg.lifts", parserClass = "gui.parser.IntegerParser", component = {"textfield"}, tooltip = "misc.msg.lifts.connecting.this.floor")
    private Integer lift;  // number of lifts connecting to this floor

    @Version
    @Column(name = "version")
    private Long version;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "floor", fetch = FetchType.EAGER)
    @NotRemovableUnlessEmpty(message = "msg.cantberemoved.rooms.assigned")
    private List<Rooms> rooms;

    private static final long serialVersionUID = 1L;


    public long getFloorid() {
        return floorid;
    }


    public void setFloorid(long floorid) {
        this.floorid = floorid;
    }

    public Floors() {
    }

    public Floors(Homes home, String name) {
        this.home = home;
        this.name = name;
        level = 2; // means groundfloor
        lift = 0;
        rooms = new ArrayList<>();
        Rooms newRoom = new Rooms(SYSTools.xx("opde.settings.home.btnAddRoom"), true, true, this);
        rooms.add(newRoom);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }


    public Integer getLift() {
        return lift;
    }

    public void setLift(Integer lift) {
        this.lift = lift;
    }

    public Homes getHome() {
        return home;
    }

    public void setHome(Homes home) {
        this.home = home;
    }

    public List<Rooms> getRooms() {
        return rooms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Floors floors = (Floors) o;

        if (floorid != floors.floorid) return false;
        if (name != null ? !name.equals(floors.name) : floors.name != null) return false;
        if (level != null ? !level.equals(floors.level) : floors.level != null) return false;
        if (lift != null ? !lift.equals(floors.lift) : floors.lift != null) return false;
        return !(home != null ? !home.equals(floors.home) : floors.home != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (floorid ^ (floorid >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (level != null ? level.hashCode() : 0);
        result = 31 * result + (lift != null ? lift.hashCode() : 0);
        result = 31 * result + (home != null ? home.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Floors{" +
                "floorid=" + floorid +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", lift=" + lift +
                ", home=" + home +
                ", rooms=" + rooms +
                '}';
    }
//
//    public String getKey(){
//        return "floor:"+floorid;
//    }
}
