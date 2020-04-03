/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offene_pflege.backend.entity.done;

import de.offene_pflege.backend.entity.DefaultStringIDEntity;
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

    private String name;

    private String street;

    private String zip;

    private String city;

    private String tel;

    private String fax;

    private int maxcap;

    private int careproviderid;

    private String color;

    private Boolean active;

    private List<Station> station;

    private List<Floors> floors;


    @Column(name = "Name", length = 30)
    @Size(min = 1, max = 30, message = "Die Anzahl der Zeichen stimmt nicht")
    @EditorComponent(label = "misc.msg.nameOfElement", component = {"textfield"})
    @NotEmpty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "Str", length = 30)
    @Size(min = 1, max = 30)
    @EditorComponent(label = "misc.msg.street", component = {"textfield"})
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
    @EditorComponent(label = "misc.msg.zipcode", component = {"textfield"})
    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    @Column(name = "City", length = 30)
    @Size(min = 1, max = 30)
    @EditorComponent(label = "misc.msg.city", component = {"textfield"})
    @NotEmpty
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    @Column(name = "Tel", length = 30)
    @Size(min = 1, max = 30)
    @EditorComponent(label = "misc.msg.phone", component = {"textfield"})
    @NotEmpty
    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }


    @Column(name = "Fax", length = 30)
    @Size(min = 1, max = 30, message = "Die Länge muss zwischen 1 und 30 liegen.")
    @EditorComponent(label = "misc.msg.fax", component = {"textfield"})
    @NotEmpty
    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    @Column(name = "maxcap", nullable = false, insertable = true, updatable = true)
    @Digits(integer = 10, fraction = 0, message = "Integer only")
    @EditorComponent(label = "misc.msg.maxcap", component = {"textfield"})
    @NotNull
    public int getMaxcap() {
        return maxcap;
    }

    public void setMaxcap(int maxcap) {
        this.maxcap = maxcap;
    }

    @Column(name = "careproviderid", nullable = false, insertable = true, updatable = true)
    @Digits(integer = 10, fraction = 0, message = "Integer only")
    @EditorComponent(label = "misc.msg.careproviderid", component = {"textfield"})
    @NotNull
    public int getCareproviderid() {
        return careproviderid;
    }

    public void setCareproviderid(int careproviderid) {
        this.careproviderid = careproviderid;
    }

    @Column(name = "color", length = 6)
    @EditorComponent(label = "misc.msg.colorset", component = {"colorset"}, triggersReload = "true")
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Column(name = "active")
    @EditorComponent(label = "misc.msg.active", component = {"onoffswitch", "misc.msg.active", "misc.msg.inactive"})
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "home", fetch = FetchType.EAGER)
    @NotRemovableUnlessEmpty(message = "msg.cantberemoved.stations.assigned")
    public List<Station> getStation() {
        return station;
    }

    public void setStation(List<Station> station) {
        this.station = station;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "home", fetch = FetchType.EAGER)
    @NotRemovableUnlessEmpty(message = "msg.cantberemoved.floors.assigned")
    public List<Floors> getFloors() {
        return floors;
    }

    public void setFloors(List<Floors> floors) {
        this.floors = floors;
    }

    public Homes() {
    }

    @Override
    public String toString() {
        return name;
    }


}
