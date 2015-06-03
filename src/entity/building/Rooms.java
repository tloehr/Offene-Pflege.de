/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.building;

import op.tools.SYSTools;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author tloehr
 */
@Entity
@Table(name = "rooms")
public class Rooms implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RID")
    private Long roomID;
    @Column(name = "Text")
    @NotEmpty
    private String text;
    @Column(name = "Single")
    private Boolean single;
    @Column(name = "Bath")
    private Boolean bath;
    @Column(name = "active")
    private Boolean active;
    @Version
    @Column(name = "version")
    private Long version;

    @JoinColumn(name = "floorid", referencedColumnName = "floorid")
    @ManyToOne
    private Floors floor;

    public Rooms() {
    }

    public Rooms(String text, Boolean single, Boolean bath, Floors floor) {
        this.text = text;
        this.single = single;
        this.bath = bath;
        this.floor = floor;
        this.active = true;
    }

    public Long getRoomID() {
        return roomID;
    }

    public void setRoomID(Long roomID) {
        this.roomID = roomID;
    }


    /**
     * is this room a single ?
     *
     * @return
     */
    public boolean isSingle() {
        return single;
    }

    public Boolean getSingle() {
        return single;
    }

    public void setSingle(Boolean single) {
        this.single = single;
    }

    /**
     * #
     * does this room has a bath of its own ?
     *
     * @return
     */
    public boolean hasBath() {
        return bath;
    }

    public Boolean getBath() {
        return bath;
    }

    public void setBath(Boolean bath) {
        this.bath = bath;
    }

    public Floors getFloor() {
        return floor;
    }

    public void setFloor(Floors floor) {
        this.floor = floor;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * what is the name of that room (e.G. "Nr.1" or simply "1" or what else).
     *
     * @return
     */
    public String getText() {
        return text;
    }


    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomID != null ? roomID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof Rooms)) {
            return false;
        }
        Rooms other = (Rooms) object;
        if ((this.roomID == null && other.roomID != null) || (this.roomID != null && !this.roomID.equals(other.roomID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return SYSTools.xx("misc.msg.room") + " " + text + ", " + floor.getName() + ", " + floor.getHome().getName();
    }

}
