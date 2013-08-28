/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import entity.reports.Handovers;
import op.tools.SYSConst;

import javax.persistence.*;
import java.awt.*;
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
    @Column(name = "Short")
    @Basic
    private String shortname;
    @Column(name = "Name")
    private String name;
    @Column(name = "Str")
    private String street;
    @Column(name = "ZIP")
    private String zip;
    @Column(name = "City")
    private String city;
    @Column(name = "Tel")
    private String tel;
    @Column(name = "Fax")
    private String fax;
    @Version
    @Column(name = "version")
    private Long version;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "home")
    private Collection<Handovers> handovers;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "home")
    private List<Station> station;

    public Homes() {
    }

    public String getShortname() {
          return shortname;
      }

      public void setShortname(String shortname) {
          this.shortname = shortname;
      }


    public Homes(String eid) {
        this.eid = eid;
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

    // todo: dummy implementation only. fix this!
    public Color getColor() {

        if (eid.equalsIgnoreCase("herchen")) return Color.green.darker();

        return Color.blue.darker();

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
        return shortname;
    }

    public List<Station> getStations() {
        return station;
    }
}
