/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.offene_pflege.entity.building;

import de.offene_pflege.entity.DefaultEntity;
import de.offene_pflege.gui.interfaces.EditorComponent;
import de.offene_pflege.gui.interfaces.NotRemovableUnlessEmpty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

/**
 * @author tloehr
 */

/**
 * @author tloehr
 */
@Entity
@Table(name = "rooms")
@NotRemovableUnlessEmpty(message = "msg.cantberemoved.resinfo.assigned", evalualedByClass = "entity.building.RoomsNotRemovableEvaluation")
@Getter
@Setter
public class Rooms extends DefaultEntity {
    @NotEmpty
    @EditorComponent(label = "misc.msg.nameOfElement", component = {"textfield"})
    @Column(name = "Text")
    private String text;
    @EditorComponent(label = "misc.msg.room", component = {"onoffswitch", "misc.msg.single.room", "misc.msg.double.room"})
    @Column(name = "Single")
    private Boolean single;
    @EditorComponent(label = "misc.msg.room.bath", component = {"onoffswitch", "misc.msg.with", "misc.msg.without"})
    @Column(name = "Bath")
    private Boolean bath;
    @EditorComponent(label = "misc.msg.active", component = {"onoffswitch", "misc.msg.active", "misc.msg.inactive"})
    @Column(name = "active")
    private Boolean active;
    @JoinColumn(name = "floorid", referencedColumnName = "id")
    @ManyToOne
    private Floors floor;

}