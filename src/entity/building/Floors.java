package entity.building;

import javax.persistence.*;
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
    private long floorid;
    private String name;
    private Short level;
    private Short lift;

    @Version
    @Column(name = "version")
    private Long version;

    private static final long serialVersionUID = 1L;


    public long getFloorid() {
        return floorid;
    }


    public void setFloorid(long floorid) {
        this.floorid = floorid;
    }

    @JoinColumn(name = "HomeID", referencedColumnName = "EID")
    @ManyToOne
    private Homes home;

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
    public Short getLevel() {
        return level;
    }

    public void setLevel(Short level) {
        this.level = level;
    }

    @Basic
    @Column(name = "lift", nullable = true, insertable = true, updatable = true)
    public Short getLift() {
        return lift;
    }

    public void setLift(Short lift) {
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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "floor", fetch = FetchType.EAGER)
    private List<Rooms> rooms;

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
