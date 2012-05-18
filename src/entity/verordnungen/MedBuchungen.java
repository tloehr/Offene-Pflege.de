package entity.verordnungen;

import entity.Users;
import op.OPDE;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "MPBuchung")
@NamedQueries({
        @NamedQuery(name = "MedBuchungen.findAll", query = "SELECT m FROM MedBuchungen m"),
        @NamedQuery(name = "MedBuchungen.findByBuchID", query = "SELECT m FROM MedBuchungen m WHERE m.buchID = :buchID"),
        @NamedQuery(name = "MedBuchungen.findByBestand", query = "SELECT m FROM MedBuchungen m WHERE m.bestand = :bestand ORDER BY m.pit"),
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
    @Version
    @Column(name = "version")
    private Long version;
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
        bestand.getBuchungen().add(this);
        this.user = OPDE.getLogin().getUser();
    }

    public MedBuchungen(MedBestand bestand, BigDecimal menge, BHP bhp) {
        this.bestand = bestand;
        this.menge = menge;
        this.pit = new Date();
        this.bhp = bhp;
        this.status = MedBuchungenTools.STATUS_AUSBUCHEN_NORMAL;
        this.user = OPDE.getLogin().getUser();
        bestand.getBuchungen().add(this);
        bhp.getBuchungen().add(this);
    }

//    public MedBuchungen(MedBestand bestand, BigDecimal menge, BHP bhp, short status) {
//        this.bestand = bestand;
//        this.menge = menge;
//        this.pit = new Date();
//        this.bhp = bhp;
//        this.status = status;
//        this.user = OPDE.getLogin().getUser();
//        bestand.getBuchungen().add(this);
//        bhp.getBuchungen().add(this);
//    }

    public MedBuchungen(MedBestand bestand, BigDecimal menge, short status) {
        this.bestand = bestand;
        this.menge = menge;
        this.pit = new Date();
        this.bhp = null;
        this.status = status;
        this.user = OPDE.getLogin().getUser();
        bestand.getBuchungen().add(this);
    }

    public Long getBuchID() {
        return buchID;
    }

    public void setBuchID(Long buchID) {
        this.buchID = buchID;
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

    public BHP getBhp() {
        return bhp;
    }

//    public void setBhp(BHP bhp) {
//        this.bhp = bhp;
//    }

    public MedBestand getBestand() {
        return bestand;
    }

//    public void setBestand(MedBestand bestand) {
//        this.bestand = bestand;
//    }

    public Users getUser() {
        return user;
    }

    // N:1 Relationen
    @JoinColumn(name = "BestID", referencedColumnName = "BestID")
    @ManyToOne
    //OWNER
    private MedBestand bestand;

    @JoinColumn(name = "BHPID", referencedColumnName = "BHPID")
    @ManyToOne
    //OWNER
    private BHP bhp;

    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    //OWNER
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
        return "MedBuchungen{" +
                "buchID=" + buchID +
                ", menge=" + menge +
                ", text='" + text + '\'' +
                ", status=" + status +
                ", pit=" + pit +
                ", bestand=" + bestand +
                ", bhp=" + bhp +
                ", user=" + user +
                '}';
    }
}
