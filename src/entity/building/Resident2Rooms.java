package entity.building;

import entity.info.Resident;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by tloehr on 21.03.15.
 *
 *
 * github issues
 * @relates #9
 */
@Entity
public class Resident2Rooms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public Resident2Rooms() {
    }

    public Resident2Rooms(Resident resident, Rooms room, Date from, Date to) {
        this.resident = resident;
        this.room = room;
        this.from = from;
        this.to = to;
    }

    @Version
    @Column(name = "version")
    private Long version;

    @JoinColumn(name = "rid", referencedColumnName = "BWKennung")
    @ManyToOne
    private Resident resident;

    @JoinColumn(name = "roomid", referencedColumnName = "RID")
    @ManyToOne
    private Rooms room;

    @Basic(optional = false)
    @Column(name = "start")
    @Temporal(TemporalType.TIMESTAMP)
    private Date from;

    @Basic(optional = false)
    @Column(name = "end")
    @Temporal(TemporalType.TIMESTAMP)
    private Date to;


    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public Rooms getRoom() {
        return room;
    }

    public void setRoom(Rooms room) {
        this.room = room;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resident2Rooms that = (Resident2Rooms) o;

        if (from != null ? !from.equals(that.from) : that.from != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (resident != null ? !resident.equals(that.resident) : that.resident != null) return false;
        if (room != null ? !room.equals(that.room) : that.room != null) return false;
        if (to != null ? !to.equals(that.to) : that.to != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (resident != null ? resident.hashCode() : 0);
        result = 31 * result + (room != null ? room.hashCode() : 0);
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (to != null ? to.hashCode() : 0);
        return result;
    }
}
