package entity.prescription;

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
    private String strasse;
    @Column(name = "PLZ")
    private String plz;
    @Column(name = "Ort")
    private String ort;
    @Column(name = "Tel")
    private String tel;
    @Column(name = "Fax")
    private String fax;
    @Column(name = "Status")
    private Integer status;

    public Hospital() {
        this.name = "";
        this.strasse = "";
        this.plz = "";
        this.ort = "";
        this.tel = "";
        this.fax = "";
        this.status = 0;
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

    public String getStrasse() {
        return strasse;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    public String getPlz() {
        return plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
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
        if (ort != null ? !ort.equals(hospital.ort) : hospital.ort != null) return false;
        if (plz != null ? !plz.equals(hospital.plz) : hospital.plz != null) return false;
        if (status != null ? !status.equals(hospital.status) : hospital.status != null) return false;
        if (strasse != null ? !strasse.equals(hospital.strasse) : hospital.strasse != null) return false;
        if (tel != null ? !tel.equals(hospital.tel) : hospital.tel != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = khid != null ? khid.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (strasse != null ? strasse.hashCode() : 0);
        result = 31 * result + (plz != null ? plz.hashCode() : 0);
        result = 31 * result + (ort != null ? ort.hashCode() : 0);
        result = 31 * result + (tel != null ? tel.hashCode() : 0);
        result = 31 * result + (fax != null ? fax.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "entity.rest.Hospital[khid=" + khid + "]";
    }

}
