/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.offene_pflege.entity.building;

import de.offene_pflege.gui.interfaces.EditorComponent;
import de.offene_pflege.gui.interfaces.NotRemovableUnlessEmpty;
import de.offene_pflege.op.tools.SYSTools;
import javax.validation.constraints.NotEmpty;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author tloehr
 */
@Entity
@Table(name = "rooms")
@NotRemovableUnlessEmpty(message = "msg.cantberemoved.resinfo.assigned", evalualedByClass = "entity.building.RoomsNotRemovableEvaluation")
public class Rooms implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RID")
    @EditorComponent(label = "misc.msg.primary.key", component = {"textfield"}, readonly = "true")
    private Long roomID;
    @Column(name = "Text")
    @NotEmpty
    @EditorComponent(label = "misc.msg.nameOfElement", component = {"textfield"})
    private String text;
    @Column(name = "Single")
    @EditorComponent(label = "misc.msg.room", component = {"onoffswitch", "misc.msg.single.room", "misc.msg.double.room"})
    private Boolean single;
    @Column(name = "Bath")
    @EditorComponent(label = "misc.msg.room.bath", component = {"onoffswitch", "misc.msg.with", "misc.msg.without"})
    private Boolean bath;
    @Column(name = "active")
    @EditorComponent(label = "misc.msg.active", component = {"onoffswitch","misc.msg.active","misc.msg.inactive"})
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
    public Boolean getBath() {
        return bath;
    }

    public void setBath(Boolean bath) {
        this.bath = bath;
    }


    public Boolean getActive() {
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


    public Floors getFloor() {
        return floor;
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
