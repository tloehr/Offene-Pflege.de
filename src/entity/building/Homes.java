/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.building;

import entity.reports.Handovers;
import gui.interfaces.EditorComponent;
import op.tools.SYSTools;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author tloehr
 */
@Entity
@Table(name = "homes")

public class Homes implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "EID")
    private String eid;
    @Column(name = "Name", length = 30)
    @Size(min = 1, max = 30)
    @NotEmpty
    @EditorComponent(label = "misc.msg.nameOfElement", component = {"textfield"})
    private String name;
    @Column(name = "Str", length = 30)
    @Size(min = 1, max = 30)
    @NotEmpty
    @EditorComponent(label = "misc.msg.street", component = {"textfield"})
    private String street;
    @Column(name = "ZIP", length = 5)
    @Size(min = 1, max = 5)
    @EditorComponent(label = "misc.msg.zipcode", component = {"textfield"})
    @NotEmpty
    private String zip;
    @Column(name = "City", length = 30)
    @Size(min = 1, max = 30)
    @NotEmpty
    @EditorComponent(label = "misc.msg.city", component = {"textfield"})
    private String city;
    @Column(name = "Tel", length = 30)
    @Size(min = 1, max = 30)
    @NotEmpty
    @EditorComponent(label = "misc.msg.phone", component = {"textfield"})
    private String tel;
    @Column(name = "Fax", length = 30)
    @Size(min = 1, max = 30)
    @NotEmpty
    @EditorComponent(label = "misc.msg.fax", component = {"textfield"})
    private String fax;
    @Version
    @Column(name = "version")
    private Long version;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "home")
    private Collection<Handovers> handovers;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "home", fetch = FetchType.EAGER)
    private List<Station> station;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "home", fetch = FetchType.EAGER)
    private List<Floors> floors;

    public Homes() {
    }

    public List<Floors> getFloors() {
        return floors;
    }

    public Homes(String eid) {
        this.eid = eid;
        this.name = SYSTools.xx("homes.new.home");
        this.street = SYSTools.xx("misc.msg.street");
        this.zip = "12345";
        this.city = SYSTools.xx("misc.msg.city");
        this.tel = SYSTools.xx("misc.msg.phone");
        this.fax = SYSTools.xx("misc.msg.fax");
    }

    public String getEID() {
        return eid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZIP() {
        return zip;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getZip() {
        return zip;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (eid != null ? eid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof Homes)) {
            return false;
        }
        Homes other = (Homes) object;
        if ((this.eid == null && other.eid != null) || (this.eid != null && !this.eid.equals(other.eid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

    public List<Station> getStations() {
        return station;
    }
}
