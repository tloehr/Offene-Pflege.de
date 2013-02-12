package entity.prescription;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "doc")

public class Doc implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ArztID")
    private Long arztID;
    @Basic(optional = false)
    @Column(name = "Anrede")
    private String anrede;
    @Basic(optional = false)
    @Column(name = "Titel")
    private String titel;
    @Basic(optional = false)
    @Column(name = "Name")
    private String name;
    @Basic(optional = false)
    @Column(name = "Vorname")
    private String vorname;
    @Basic(optional = false)
    @Column(name = "Strasse")
    private String strasse;
    @Basic(optional = false)
    @Column(name = "PLZ")
    private String plz;
    @Basic(optional = false)
    @Column(name = "Ort")
    private String ort;
    @Basic(optional = false)
    @Column(name = "Tel")
    private String tel;
    @Basic(optional = false)
    @Column(name = "Fax")
    private String fax;
    @Column(name = "Mobil")
    private String mobil;
    @Column(name = "EMail")
    private String eMail;
    @Column(name = "Status")
    private Integer status;

    public Doc() {
        this.anrede = "";
        this.titel = "";
        this.name = "";
        this.vorname = "";
        this.strasse = "";
        this.plz = "";
        this.ort = "";
        this.tel = "";
        this.fax = "";
        this.eMail = "";
        this.mobil = "";
        this.status = 0;
    }

    public Doc(String anrede, String titel, String name, String vorname, String strasse, String plz, String ort, String tel, String fax) {
        this.anrede = anrede;
        this.titel = titel;
        this.name = name;
        this.vorname = vorname;
        this.strasse = strasse;
        this.plz = plz;
        this.ort = ort;
        this.tel = tel;
        this.fax = fax;
    }

    public Long getArztID() {
        return arztID;
    }

    public void setArztID(Long arztID) {
        this.arztID = arztID;
    }

    public String getAnrede() {
        return anrede;
    }

    public void setAnrede(String anrede) {
        this.anrede = anrede;
    }

    public String getTitle() {
        return titel;
    }

    public void setTitle(String title) {
        this.titel = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstname() {
        return vorname;
    }

    public void setFirstname(String vorname) {
        this.vorname = vorname;
    }

    public String getStreet() {
        return strasse;
    }

    public void setStreet(String strasse) {
        this.strasse = strasse;
    }

    public Integer getState() {
        return status;
    }

    public void setState(Integer status) {
        this.status = status;
    }

    public String getZIP() {
        return plz;
    }

    public void setZIP(String zip) {
        this.plz = zip;
    }

    public String getCity() {
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

    public String getMobile() {
        return mobil;
    }

    public void setMobile(String mobil) {
        this.mobil = mobil;
    }

    public String getEMail() {
        return eMail;
    }

    public void setEMail(String eMail) {
        this.eMail = eMail;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Doc doc = (Doc) o;

        if (anrede != null ? !anrede.equals(doc.anrede) : doc.anrede != null) return false;
        if (arztID != null ? !arztID.equals(doc.arztID) : doc.arztID != null) return false;
        if (eMail != null ? !eMail.equals(doc.eMail) : doc.eMail != null) return false;
        if (fax != null ? !fax.equals(doc.fax) : doc.fax != null) return false;
        if (mobil != null ? !mobil.equals(doc.mobil) : doc.mobil != null) return false;
        if (name != null ? !name.equals(doc.name) : doc.name != null) return false;
        if (ort != null ? !ort.equals(doc.ort) : doc.ort != null) return false;
        if (plz != null ? !plz.equals(doc.plz) : doc.plz != null) return false;
        if (status != null ? !status.equals(doc.status) : doc.status != null) return false;
        if (strasse != null ? !strasse.equals(doc.strasse) : doc.strasse != null) return false;
        if (tel != null ? !tel.equals(doc.tel) : doc.tel != null) return false;
        if (titel != null ? !titel.equals(doc.titel) : doc.titel != null) return false;
        if (vorname != null ? !vorname.equals(doc.vorname) : doc.vorname != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = arztID != null ? arztID.hashCode() : 0;
        result = 31 * result + (anrede != null ? anrede.hashCode() : 0);
        result = 31 * result + (titel != null ? titel.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (vorname != null ? vorname.hashCode() : 0);
        result = 31 * result + (strasse != null ? strasse.hashCode() : 0);
        result = 31 * result + (plz != null ? plz.hashCode() : 0);
        result = 31 * result + (ort != null ? ort.hashCode() : 0);
        result = 31 * result + (tel != null ? tel.hashCode() : 0);
        result = 31 * result + (fax != null ? fax.hashCode() : 0);
        result = 31 * result + (mobil != null ? mobil.hashCode() : 0);
        result = 31 * result + (eMail != null ? eMail.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "entity.rest.Doc[arztID=" + arztID + "]";
    }

}
