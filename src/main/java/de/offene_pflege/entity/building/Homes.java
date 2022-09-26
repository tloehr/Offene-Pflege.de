/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offene_pflege.entity.building;

import de.offene_pflege.entity.DefaultStringIDEntity;
import de.offene_pflege.gui.interfaces.EditorComponent;
import de.offene_pflege.gui.interfaces.NotRemovableUnlessEmpty;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * @author tloehr
 */
@Entity
@Table(name = "homes")

public class Homes extends DefaultStringIDEntity implements Serializable {
    @EditorComponent(label = "misc.msg.nameOfElement", component = {"textfield"})
    private String name;
    @EditorComponent(label = "misc.msg.street", component = {"textfield"})
    private String street;
    @EditorComponent(label = "misc.msg.zipcode", component = {"textfield"})
    private String zip;
    @EditorComponent(label = "misc.msg.city", component = {"textfield"})
    private String city;
    @EditorComponent(label = "misc.msg.phone", component = {"textfield"})
    private String tel;
    @EditorComponent(label = "misc.msg.fax", component = {"textfield"})
    private String fax;
    @EditorComponent(label = "misc.msg.maxcap", component = {"textfield"})
    private int maxcap;
    @EditorComponent(label = "misc.msg.careproviderid", parserClass = "de.offene_pflege.gui.parser.IntegerParser", component = {"textfield"}, tooltip="misc.tooltip.careproviderid")
    private int careproviderid;
//    @EditorComponent(label = "misc.msg.colorset", component = {"colorset"}, triggersReload = "true")
    private String color;
//    @EditorComponent(label = "misc.msg.active", component = {"onoffswitch", "misc.msg.active", "misc.msg.inactive"})
    private Boolean active;
    @NotRemovableUnlessEmpty(message = "msg.cantberemoved.stations.assigned")
    private List<Station> station;
    @NotRemovableUnlessEmpty(message = "msg.cantberemoved.floors.assigned")
    private List<Floors> floors;


    @Column(name = "Name", length = 30)
    @Size(min = 1, max = 30, message = "Die Anzahl der Zeichen stimmt nicht")
    @NotEmpty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "Str", length = 30)
    @Size(min = 1, max = 30)
    @NotEmpty
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Column(name = "ZIP", length = 5)
    @Size(min = 1, max = 5)
    @NotEmpty
    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    @Column(name = "City", length = 30)
    @Size(min = 1, max = 30)
    @NotEmpty
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    @Column(name = "Tel", length = 30)
    @Size(min = 1, max = 30)
    @NotEmpty
    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }


    @Column(name = "Fax", length = 30)
    @Size(min = 1, max = 30, message = "Die Länge muss zwischen 1 und 30 liegen.")
    @NotEmpty
    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    @Column(name = "maxcap", nullable = false, insertable = true, updatable = true)
    @Digits(integer = 10, fraction = 0, message = "Integer only")
    @NotNull
    public int getMaxcap() {
        return maxcap;
    }

    public void setMaxcap(int maxcap) {
        this.maxcap = maxcap;
    }

    @Column(name = "careproviderid", nullable = false, insertable = true, updatable = true)
    @Digits(integer = 10, fraction = 0, message = "Integer only")
    @NotNull
    /**
     * Das hier ist nicht die IK-Nummer der Pflegekasse, sondern die von DAS-PFLEGE zugeordnete ID Nummer.
     */
    public int getCareproviderid() {
        return careproviderid;
    }

    public void setCareproviderid(int careproviderid) {
        this.careproviderid = careproviderid;
    }

    @Column(name = "color", length = 6)
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Column(name = "active")
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "home", fetch = FetchType.EAGER)
    public List<Station> getStation() {
        return station;
    }

    public void setStation(List<Station> station) {
        this.station = station;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "home", fetch = FetchType.EAGER)
    public List<Floors> getFloors() {
        return floors;
    }

    public void setFloors(List<Floors> floors) {
        this.floors = floors;
    }
    //    @OneToMany(cascade = CascadeType.ALL, mappedBy = "home", fetch = FetchType.EAGER)
//    @NotRemovableUnlessEmpty(message = "msg.cantberemoved.qmssched.assigned")
//    private List<Qmssched> qmsscheds;

    public Homes() {
    }


//    public List<Qmssched> getQmsscheds() {
//        return qmsscheds;
//    }


    @Override
    public String toString() {
        return name;
    }


}
