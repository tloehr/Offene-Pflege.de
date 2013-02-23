/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import op.OPDE;

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
    private String text;
    @Column(name = "Level")
    private Short level;
    @Column(name = "Single")
    private Boolean single;
    @Column(name = "Bath")
    private Boolean bath;
    @JoinColumn(name = "StatID", referencedColumnName = "StatID")
    @ManyToOne
    private Station station;

    public Rooms() {
    }


    public Long getRoomID() {
        return roomID;
    }

    public void setRoomID(Long roomID) {
        this.roomID = roomID;
    }

    public Short getLevel() {
        return level;
    }

    public void setLevel(Short level) {
        this.level = level;
    }

    public Boolean getSingle() {
        return single;
    }

    public void setSingle(Boolean single) {
        this.single = single;
    }

    public Boolean getBath() {
        return bath;
    }

    public void setBath(Boolean bath) {
        this.bath = bath;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public String getText() {
        return text;
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
        return OPDE.lang.getString("misc.msg.room") + " " + text + ", " + station.getName() + ", " + station.getHome().getName();
    }

}
