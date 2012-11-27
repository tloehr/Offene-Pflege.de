package entity.prescription;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Arzt")
//@NamedQueries({
//        @NamedQuery(name = "Arzt.findAll", query = "SELECT a FROM Doc a ORDER BY a.name, a.vorname"),
//        @NamedQuery(name = "Arzt.findAllActive", query = "SELECT a FROM Doc a WHERE a.status >= 0 ORDER BY a.name, a.vorname"),
//        @NamedQuery(name = "Arzt.findByArztID", query = "SELECT a FROM Doc a WHERE a.arztID = :arztID"),
//        @NamedQuery(name = "Arzt.findByAnrede", query = "SELECT a FROM Doc a WHERE a.anrede = :anrede"),
//        @NamedQuery(name = "Arzt.findByTitel", query = "SELECT a FROM Doc a WHERE a.titel = :titel"),
//        @NamedQuery(name = "Arzt.findByName", query = "SELECT a FROM Doc a WHERE a.name = :name"),
//        @NamedQuery(name = "Arzt.findByVorname", query = "SELECT a FROM Doc a WHERE a.vorname = :vorname"),
//        @NamedQuery(name = "Arzt.findByStrasse", query = "SELECT a FROM Doc a WHERE a.strasse = :strasse"),
//        @NamedQuery(name = "Arzt.findByPlz", query = "SELECT a FROM Doc a WHERE a.plz = :plz"),
//        @NamedQuery(name = "Arzt.findByOrt", query = "SELECT a FROM Doc a WHERE a.ort = :ort"),
//        @NamedQuery(name = "Arzt.findByTel", query = "SELECT a FROM Doc a WHERE a.tel = :tel"),
//        @NamedQuery(name = "Arzt.findByFax", query = "SELECT a FROM Doc a WHERE a.fax = :fax"),
//        @NamedQuery(name = "Arzt.findByMobil", query = "SELECT a FROM Doc a WHERE a.mobil = :mobil"),
//        @NamedQuery(name = "Arzt.findByEMail", query = "SELECT a FROM Doc a WHERE a.eMail = :eMail")})
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
    public int hashCode() {
        int hash = 0;
        hash += (arztID != null ? arztID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof Doc)) {
            return false;
        }
        Doc other = (Doc) object;
        if ((this.arztID == null && other.arztID != null) || (this.arztID != null && !this.arztID.equals(other.arztID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.Doc[arztID=" + arztID + "]";
    }

}
