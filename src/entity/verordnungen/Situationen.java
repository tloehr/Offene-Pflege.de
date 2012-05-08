package entity.verordnungen;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Situationen")
@NamedQueries({
    @NamedQuery(name = "Situationen.findAll", query = "SELECT s FROM Situationen s"),
    @NamedQuery(name = "Situationen.findBySitID", query = "SELECT s FROM Situationen s WHERE s.sitID = :sitID"),
    @NamedQuery(name = "Situationen.findByKategorie", query = "SELECT s FROM Situationen s WHERE s.kategorie = :kategorie"),
    @NamedQuery(name = "Situationen.findByUKategorie", query = "SELECT s FROM Situationen s WHERE s.uKategorie = :uKategorie")})
public class Situationen implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "SitID")
    private Long sitID;
    @Column(name = "Kategorie")
    private Short kategorie;
    @Column(name = "UKategorie")
    private Short uKategorie;
    @Lob
    @Column(name = "Text")
    private String text;

    public Situationen() {
    }

    public Situationen(String text) {
        this.text = text;
    }

    public Long getSitID() {
        return sitID;
    }

    public void setSitID(Long sitID) {
        this.sitID = sitID;
    }

    public Short getKategorie() {
        return kategorie;
    }

    public void setKategorie(Short kategorie) {
        this.kategorie = kategorie;
    }

    public Short getUKategorie() {
        return uKategorie;
    }

    public void setUKategorie(Short uKategorie) {
        this.uKategorie = uKategorie;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (sitID != null ? sitID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Situationen)) {
            return false;
        }
        Situationen other = (Situationen) object;
        if ((this.sitID == null && other.sitID != null) || (this.sitID != null && !this.sitID.equals(other.sitID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Situationen{" +
                "sitID=" + sitID +
                ", kategorie=" + kategorie +
                ", uKategorie=" + uKategorie +
                ", text='" + text + '\'' +
                '}';
    }
}