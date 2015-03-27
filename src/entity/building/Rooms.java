/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.building;

import op.tools.SYSTools;

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
    @Column(name = "inuse")
    private Boolean inuse;
    @Column(name = "inactive")
    private Boolean inactive;

    @JoinColumn(name = "HomeID", referencedColumnName = "EID")
    @ManyToOne
    private Homes home;

    public Rooms() {
    }


    public Long getRoomID() {
        return roomID;
    }

    public void setRoomID(Long roomID) {
        this.roomID = roomID;
    }

    /**
     * the floor of this room (0 means ground level, 1 means 1st floor...)
     *
     * @return
     */
    public Short getLevel() {
        return level;
    }

    public void setLevel(Short level) {
        this.level = level;
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

    public Homes getHome() {
        return home;
    }

    public void setHome(Homes home) {
        this.home = home;
    }

    public Boolean isInactive() {
        return inactive;
    }

    public void setInactive(Boolean inactive) {
        this.inactive = inactive;
    }

    public Boolean isInuse() {
        return inuse;
    }

    public void setInuse(Boolean inuse) {
        this.inuse = inuse;
    }

    /**
     * what is the name of that room (e.G. "Nr.1" or simply "1" or what else).
     *
     * @return
     */
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
        return SYSTools.xx("misc.msg.room") + " " + text + ", " + SYSTools.xx("misc.msg.floor") + ": " + (level == 0 ? SYSTools.xx("misc.msg.groundlevel") : level) + ", " + home.getName();
    }

}
