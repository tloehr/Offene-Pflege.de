package entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 19.11.11
 * Time: 14:12
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "Betreuer")
@NamedQueries({
        @NamedQuery(name = "Betreuer.findAll", query = "SELECT b FROM Betreuer b"),
        @NamedQuery(name = "Betreuer.findAllActive", query = "SELECT b FROM Betreuer b WHERE b.status >= 0 ORDER BY b.name, b.vorname"),
        @NamedQuery(name = "Betreuer.findByBetrID", query = "SELECT b FROM Betreuer b WHERE b.betrID = :betrID"),
        @NamedQuery(name = "Betreuer.findByAnrede", query = "SELECT b FROM Betreuer b WHERE b.anrede = :anrede"),
        @NamedQuery(name = "Betreuer.findByName", query = "SELECT b FROM Betreuer b WHERE b.name = :name"),
        @NamedQuery(name = "Betreuer.findByVorname", query = "SELECT b FROM Betreuer b WHERE b.vorname = :vorname"),
        @NamedQuery(name = "Betreuer.findByStrasse", query = "SELECT b FROM Betreuer b WHERE b.strasse = :strasse"),
        @NamedQuery(name = "Betreuer.findByPlz", query = "SELECT b FROM Betreuer b WHERE b.plz = :plz"),
        @NamedQuery(name = "Betreuer.findByOrt", query = "SELECT b FROM Betreuer b WHERE b.ort = :ort"),
        @NamedQuery(name = "Betreuer.findByTel", query = "SELECT b FROM Betreuer b WHERE b.tel = :tel"),
        @NamedQuery(name = "Betreuer.findByPrivat", query = "SELECT b FROM Betreuer b WHERE b.privat = :privat"),
        @NamedQuery(name = "Betreuer.findByFax", query = "SELECT b FROM Betreuer b WHERE b.fax = :fax"),
        @NamedQuery(name = "Betreuer.findByMobil", query = "SELECT b FROM Betreuer b WHERE b.mobil = :mobil"),
        @NamedQuery(name = "Betreuer.findByEMail", query = "SELECT b FROM Betreuer b WHERE b.eMail = :eMail")})
public class Betreuer implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "BetrID")
    private Long betrID;
    @Basic(optional = false)
    @Column(name = "Anrede")
    private String anrede;
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
    @Column(name = "Privat")
    private String privat;
    @Basic(optional = false)
    @Column(name = "Fax")
    private String fax;
    @Column(name = "Mobil")
    private String mobil;
    @Column(name = "EMail")
    private String eMail;
    @Column(name = "Status")
    private Integer status;

    public Betreuer() {
        this.anrede = "";
        this.name = "";
        this.vorname = "";
        this.strasse = "";
        this.plz = "";
        this.ort = "";
        this.tel = "";
        this.privat = "";
        this.fax = "";
        this.eMail = "";
        this.mobil = "";
        this.status = 0;
    }

    public Long getBetrID() {
        return betrID;
    }

    public void setBetrID(Long betrID) {
        this.betrID = betrID;
    }

    public String getAnrede() {
        return anrede;
    }

    public void setAnrede(String anrede) {
        this.anrede = anrede;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
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

    public String getPrivat() {
        return privat;
    }

    public void setPrivat(String privat) {
        this.privat = privat;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getMobil() {
        return mobil;
    }

    public void setMobil(String mobil) {
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
        hash += (betrID != null ? betrID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Betreuer)) {
            return false;
        }
        Betreuer other = (Betreuer) object;
        if ((this.betrID == null && other.betrID != null) || (this.betrID != null && !this.betrID.equals(other.betrID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.Betreuer[betrID=" + betrID + "]";
    }

}
