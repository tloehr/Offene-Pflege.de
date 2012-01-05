package entity.verordnungen;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "MPHersteller")
@NamedQueries({
    @NamedQuery(name = "MedHersteller.findAll", query = "SELECT m FROM MedHersteller m"),
    @NamedQuery(name = "MedHersteller.findByMphid", query = "SELECT m FROM MedHersteller m WHERE m.mphid = :mphid"),
    @NamedQuery(name = "MedHersteller.findByFirma", query = "SELECT m FROM MedHersteller m WHERE m.firma = :firma"),
    @NamedQuery(name = "MedHersteller.findByStrasse", query = "SELECT m FROM MedHersteller m WHERE m.strasse = :strasse"),
    @NamedQuery(name = "MedHersteller.findByPlz", query = "SELECT m FROM MedHersteller m WHERE m.plz = :plz"),
    @NamedQuery(name = "MedHersteller.findByOrt", query = "SELECT m FROM MedHersteller m WHERE m.ort = :ort"),
    @NamedQuery(name = "MedHersteller.findByTel", query = "SELECT m FROM MedHersteller m WHERE m.tel = :tel"),
    @NamedQuery(name = "MedHersteller.findByFax", query = "SELECT m FROM MedHersteller m WHERE m.fax = :fax"),
    @NamedQuery(name = "MedHersteller.findByWww", query = "SELECT m FROM MedHersteller m WHERE m.www = :www")
    })
public class MedHersteller implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "MPHID")
    private Long mphid;
    @Basic(optional = false)
    @Column(name = "Firma")
    private String firma;
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
    @Column(name = "WWW")
    private String www;

    public MedHersteller() {
    }

    public MedHersteller(String firma, String strasse, String plz, String ort, String tel, String fax, String www) {
        this.firma = firma;
        this.strasse = strasse;
        this.plz = plz;
        this.ort = ort;
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

    public String getFirma() {
        return firma;
    }

    public void setFirma(String firma) {
        this.firma = firma;
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

    public String getWww() {
        return www;
    }

    public void setWww(String www) {
        this.www = www;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (mphid != null ? mphid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MedHersteller)) {
            return false;
        }
        MedHersteller other = (MedHersteller) object;
        if ((this.mphid == null && other.mphid != null) || (this.mphid != null && !this.mphid.equals(other.mphid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MedHersteller{" +
                "mphid=" + mphid +
                ", firma='" + firma + '\'' +
                ", strasse='" + strasse + '\'' +
                ", plz='" + plz + '\'' +
                ", ort='" + ort + '\'' +
                ", tel='" + tel + '\'' +
                ", fax='" + fax + '\'' +
                ", www='" + www + '\'' +
                '}';
    }
}