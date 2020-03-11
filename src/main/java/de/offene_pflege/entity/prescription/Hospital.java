package de.offene_pflege.entity.prescription;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "hospital")

public class Hospital implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KHID")
    private Long khid;
    @Basic(optional = false)
    @Column(name = "Name")
    private String name;
    @Column(name = "Strasse")
    private String street;
    @Column(name = "PLZ")
    private String zip;
    @Column(name = "Ort")
    private String city;
    @Column(name = "Tel")
    private String tel;
    @Column(name = "Fax")
    private String fax;
    @Column(name = "Status")
    private Integer state;
    @Version
    @Column(name = "version")
    private Long version;

    public Hospital() {
        this.name = "";
        this.street = "";
        this.zip = "";
        this.city = "";
        this.tel = "";
        this.fax = "";
        this.state = 0;
    }

    public Long getKhid() {
        return khid;
    }

    public void setKhid(Long khid) {
        this.khid = khid;
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

    public void setStreet(String strasse) {
        this.street = strasse;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String plz) {
        this.zip = plz;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer status) {
        this.state = status;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String ort) {
        this.city = ort;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hospital hospital = (Hospital) o;

        if (fax != null ? !fax.equals(hospital.fax) : hospital.fax != null) return false;
        if (khid != null ? !khid.equals(hospital.khid) : hospital.khid != null) return false;
        if (name != null ? !name.equals(hospital.name) : hospital.name != null) return false;
        if (city != null ? !city.equals(hospital.city) : hospital.city != null) return false;
        if (zip != null ? !zip.equals(hospital.zip) : hospital.zip != null) return false;
        if (state != null ? !state.equals(hospital.state) : hospital.state != null) return false;
        if (street != null ? !street.equals(hospital.street) : hospital.street != null) return false;
        if (tel != null ? !tel.equals(hospital.tel) : hospital.tel != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = khid != null ? khid.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (street != null ? street.hashCode() : 0);
        result = 31 * result + (zip != null ? zip.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (tel != null ? tel.hashCode() : 0);
        result = 31 * result + (fax != null ? fax.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "entity.rest.Hospital[khid=" + khid + "]";
    }

}
