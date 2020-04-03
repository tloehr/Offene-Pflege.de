/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.offene_pflege.backend.entity.done;

import de.offene_pflege.backend.entity.DefaultEntity;
import de.offene_pflege.gui.interfaces.EditorComponent;
import de.offene_pflege.gui.interfaces.NotRemovableUnlessEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

/**
 * @author tloehr
 */
@Entity
@Table(name = "rooms")
@NotRemovableUnlessEmpty(message = "msg.cantberemoved.resinfo.assigned", evalualedByClass = "entity.building.RoomsNotRemovableEvaluation")
public class Rooms extends DefaultEntity {
    private String text;
    private Boolean single;
    private Boolean bath;
    private Boolean active;

    private Floors floor;

    public Rooms() {
    }

    @Column(name = "Text")
    @NotEmpty
    @EditorComponent(label = "misc.msg.nameOfElement", component = {"textfield"})
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Column(name = "Single")
    @EditorComponent(label = "misc.msg.room", component = {"onoffswitch", "misc.msg.single.room", "misc.msg.double.room"})
    public Boolean getSingle() {
        return single;
    }

    public void setSingle(Boolean single) {
        this.single = single;
    }

    @Column(name = "Bath")
    @EditorComponent(label = "misc.msg.room.bath", component = {"onoffswitch", "misc.msg.with", "misc.msg.without"})
    public Boolean getBath() {
        return bath;
    }

    public void setBath(Boolean bath) {
        this.bath = bath;
    }

    @Column(name = "active")
    @EditorComponent(label = "misc.msg.active", component = {"onoffswitch", "misc.msg.active", "misc.msg.inactive"})
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @JoinColumn(name = "floorid", referencedColumnName = "id")
    @ManyToOne
    public Floors getFloor() {
        return floor;
    }

    public void setFloor(Floors floor) {
        this.floor = floor;
    }
}
