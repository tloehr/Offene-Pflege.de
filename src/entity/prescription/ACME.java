package entity.prescription;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "MPHersteller")
//@NamedQueries({
//    @NamedQuery(name = "MedHersteller.findAll", query = "SELECT m FROM ACME m ORDER BY m.firma, m.ort"),
//    @NamedQuery(name = "MedHersteller.findByMphid", query = "SELECT m FROM ACME m WHERE m.mphid = :mphid"),
//    @NamedQuery(name = "MedHersteller.findByFirma", query = "SELECT m FROM ACME m WHERE m.firma = :firma"),
//    @NamedQuery(name = "MedHersteller.findByStrasse", query = "SELECT m FROM ACME m WHERE m.strasse = :strasse"),
//    @NamedQuery(name = "MedHersteller.findByPlz", query = "SELECT m FROM ACME m WHERE m.plz = :plz"),
//    @NamedQuery(name = "MedHersteller.findByOrt", query = "SELECT m FROM ACME m WHERE m.ort = :ort"),
//    @NamedQuery(name = "MedHersteller.findByTel", query = "SELECT m FROM ACME m WHERE m.tel = :tel"),
//    @NamedQuery(name = "MedHersteller.findByFax", query = "SELECT m FROM ACME m WHERE m.fax = :fax"),
//    @NamedQuery(name = "MedHersteller.findByWww", query = "SELECT m FROM ACME m WHERE m.www = :www")
//    })
public class ACME implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MPHID")
    private Long mphid;
    @Basic(optional = false)
    @Column(name = "Firma")
    private String name;
    @Column(name = "Strasse")
    private String street;
    @Column(name = "PLZ")
    private String zipcode;
    @Column(name = "Ort")
    private String city;
    @Column(name = "Tel")
    private String tel;
    @Column(name = "Fax")
    private String fax;
    @Column(name = "WWW")
    private String www;

    public ACME() {
    }

    public ACME(String name, String street, String zipcode, String city, String tel, String fax, String www) {
        this.name = name;
        this.street = street;
        this.zipcode = zipcode;
        this.city = city;
        this.tel = tel;
        this.fax = fax;
        this.www = www;
    }

    public Long getMphid() {
        return mphid;
    }

    public void setMphid(Long mphid) {
        this.mphid = mphid;
    }

    public String getName() {
        return name;
    }

    public void setName(String firma) {
        this.name = firma;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String strasse) {
        this.street = strasse;
    }

    public String getPlz() {
        return zipcode;
    }

    public void setPlz(String plz) {
        this.zipcode = plz;
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

    public String getWww() {
        return www;
    }

    public void setWww(String www) {
        this.www = www;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ACME acme = (ACME) o;

        if (city != null ? !city.equals(acme.city) : acme.city != null) return false;
        if (fax != null ? !fax.equals(acme.fax) : acme.fax != null) return false;
        if (mphid != null ? !mphid.equals(acme.mphid) : acme.mphid != null) return false;
        if (name != null ? !name.equals(acme.name) : acme.name != null) return false;
        if (street != null ? !street.equals(acme.street) : acme.street != null) return false;
        if (tel != null ? !tel.equals(acme.tel) : acme.tel != null) return false;
        if (www != null ? !www.equals(acme.www) : acme.www != null) return false;
        if (zipcode != null ? !zipcode.equals(acme.zipcode) : acme.zipcode != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mphid != null ? mphid.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (street != null ? street.hashCode() : 0);
        result = 31 * result + (zipcode != null ? zipcode.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (tel != null ? tel.hashCode() : 0);
        result = 31 * result + (fax != null ? fax.hashCode() : 0);
        result = 31 * result + (www != null ? www.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ACME{" +
                "mphid=" + mphid +
                ", firma='" + name + '\'' +
                ", strasse='" + street + '\'' +
                ", plz='" + zipcode + '\'' +
                ", ort='" + city + '\'' +
                ", tel='" + tel + '\'' +
                ", fax='" + fax + '\'' +
                ", www='" + www + '\'' +
                '}';
    }
}