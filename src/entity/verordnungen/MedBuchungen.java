package entity.verordnungen;

import entity.Users;
import op.OPDE;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "MPBuchung")
@NamedQueries({
        @NamedQuery(name = "MedBuchungen.findAll", query = "SELECT m FROM MedBuchungen m"),
        @NamedQuery(name = "MedBuchungen.findByBuchID", query = "SELECT m FROM MedBuchungen m WHERE m.buchID = :buchID"),
        @NamedQuery(name = "MedBuchungen.findByMenge", query = "SELECT m FROM MedBuchungen m WHERE m.menge = :menge"),
        @NamedQuery(name = "MedBuchungen.findByText", query = "SELECT m FROM MedBuchungen m WHERE m.text = :text"),
        @NamedQuery(name = "MedBuchungen.findByStatus", query = "SELECT m FROM MedBuchungen m WHERE m.status = :status"),
        @NamedQuery(name = "MedBuchungen.findByPit", query = "SELECT m FROM MedBuchungen m WHERE m.pit = :pit")})
public class MedBuchungen implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "BuchID")
    private Long buchID;
    @Column(name = "BHPID")
    private BigInteger bhpid;
    @Basic(optional = false)
    @Column(name = "Menge")
    private BigDecimal menge;
    @Column(name = "Text")
    private String text;
    @Basic(optional = false)
    @Column(name = "Status")
    private short status;
    @Basic(optional = false)
    @Column(name = "PIT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pit;

    public MedBuchungen() {
    }

    public MedBuchungen(MedBestand bestand, BigDecimal menge) {
        this.pit = new Date();
        this.bestand = bestand;
        this.menge = menge;
        this.bhp = null;
        this.status = MedBuchungenTools.STATUS_EINBUCHEN_ANFANGSBESTAND;
        this.user = OPDE.getLogin().getUser();
    }

    public Long getBuchID() {
        return buchID;
    }

    public void setBuchID(Long buchID) {
        this.buchID = buchID;
    }

    public BigInteger getBhpid() {
        return bhpid;
    }

    public void setBhpid(BigInteger bhpid) {
        this.bhpid = bhpid;
    }

    public BigDecimal getMenge() {
        return menge;
    }

    public void setMenge(BigDecimal menge) {
        this.menge = menge;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }

    public Date getPit() {
        return pit;
    }

    public void setPit(Date pit) {
        this.pit = pit;
    }

    // N:1 Relationen
    @JoinColumn(name = "BestID", referencedColumnName = "BestID")
    @ManyToOne(cascade = CascadeType.ALL)
    private MedBestand bestand;

    @JoinColumn(name = "BHPID", referencedColumnName = "BHPID")
    @ManyToOne
    private BHP bhp;

    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (buchID != null ? buchID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MedBuchungen)) {
            return false;
        }
        MedBuchungen other = (MedBuchungen) object;
        if ((this.buchID == null && other.buchID != null) || (this.buchID != null && !this.buchID.equals(other.buchID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.MedBuchungen[buchID=" + buchID + "]";
    }

}
