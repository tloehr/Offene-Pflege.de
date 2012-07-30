package entity.info;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "BWInfoVorlagen")
@NamedQueries({
    @NamedQuery(name = "BWInfoVorlagen.findAll", query = "SELECT b FROM BWInfoVorlagen b"),
    @NamedQuery(name = "BWInfoVorlagen.findByBwivid", query = "SELECT b FROM BWInfoVorlagen b WHERE b.bwivid = :bwivid"),
    @NamedQuery(name = "BWInfoVorlagen.findByBezeichnung", query = "SELECT b FROM BWInfoVorlagen b WHERE b.bezeichnung = :bezeichnung"),
    @NamedQuery(name = "BWInfoVorlagen.findByBwinftyp", query = "SELECT b FROM BWInfoVorlagen b WHERE b.bwinftyp = :bwinftyp")})
public class BWInfoVorlagen implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "BWIVID")
    private Long bwivid;
    @Basic(optional = false)
    @Column(name = "Bezeichnung")
    private String bezeichnung;
    @Basic(optional = false)
    @Column(name = "BWINFTYP")
    private String bwinftyp;

    public BWInfoVorlagen() {
    }


    public Long getBwivid() {
        return bwivid;
    }

    public void setBwivid(Long bwivid) {
        this.bwivid = bwivid;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getBwinftyp() {
        return bwinftyp;
    }

    public void setBwinftyp(String bwinftyp) {
        this.bwinftyp = bwinftyp;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bwivid != null ? bwivid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof BWInfoVorlagen)) {
            return false;
        }
        BWInfoVorlagen other = (BWInfoVorlagen) object;
        if ((this.bwivid == null && other.bwivid != null) || (this.bwivid != null && !this.bwivid.equals(other.bwivid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.BWInfoVorlagen[bwivid=" + bwivid + "]";
    }

}
