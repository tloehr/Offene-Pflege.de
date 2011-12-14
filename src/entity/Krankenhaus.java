package entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "KH")
@NamedQueries({
    @NamedQuery(name = "Krankenhaus.findAll", query = "SELECT k FROM Krankenhaus k ORDER BY k.name"),
    @NamedQuery(name = "Krankenhaus.findByKhid", query = "SELECT k FROM Krankenhaus k WHERE k.khid = :khid"),
    @NamedQuery(name = "Krankenhaus.findByName", query = "SELECT k FROM Krankenhaus k WHERE k.name = :name"),
    @NamedQuery(name = "Krankenhaus.findByStrasse", query = "SELECT k FROM Krankenhaus k WHERE k.strasse = :strasse"),
    @NamedQuery(name = "Krankenhaus.findByPlz", query = "SELECT k FROM Krankenhaus k WHERE k.plz = :plz"),
    @NamedQuery(name = "Krankenhaus.findByOrt", query = "SELECT k FROM Krankenhaus k WHERE k.ort = :ort"),
    @NamedQuery(name = "Krankenhaus.findByTel", query = "SELECT k FROM Krankenhaus k WHERE k.tel = :tel"),
    @NamedQuery(name = "Krankenhaus.findByFax", query = "SELECT k FROM Krankenhaus k WHERE k.fax = :fax")})
public class Krankenhaus implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
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

    public Krankenhaus() {
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
    public int hashCode() {
        int hash = 0;
        hash += (khid != null ? khid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Krankenhaus)) {
            return false;
        }
        Krankenhaus other = (Krankenhaus) object;
        if ((this.khid == null && other.khid != null) || (this.khid != null && !this.khid.equals(other.khid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.Krankenhaus[khid=" + khid + "]";
    }

}
