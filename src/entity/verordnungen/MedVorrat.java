package entity.verordnungen;

import entity.Bewohner;
import entity.Users;
import op.OPDE;
import op.tools.SYSConst;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "MPVorrat")
@NamedQueries({
        @NamedQuery(name = "MedVorrat.findAll", query = "SELECT m FROM MedVorrat m"),
        @NamedQuery(name = "MedVorrat.findByVorID", query = "SELECT m FROM MedVorrat m WHERE m.vorID = :vorID"),
        @NamedQuery(name = "MedVorrat.findByText", query = "SELECT m FROM MedVorrat m WHERE m.text = :text"),
        @NamedQuery(name = "MedVorrat.findByVon", query = "SELECT m FROM MedVorrat m WHERE m.von = :von"),
        @NamedQuery(name = "MedVorrat.findByBis", query = "SELECT m FROM MedVorrat m WHERE m.bis = :bis"),
        @NamedQuery(name = "MedVorrat.getSumme", query = " " +
                " SELECT SUM(buch.menge) FROM MedVorrat vor JOIN vor.bestaende best JOIN best.buchungen buch WHERE vor = :vorrat "),
        @NamedQuery(name = "MedVorrat.findByBewohnerAndDarreichung", query = " " +
                " SELECT vor FROM MedVorrat vor " +
                " JOIN vor.bestaende best " +
                " WHERE vor.bewohner = :bewohner AND best.darreichung = :darreichung " +
                " AND vor.bis = " + SYSConst.MYSQL_DATETIME_BIS_AUF_WEITERES),
        @NamedQuery(name = "MedVorrat.findByBewohnerMitBestand", query = " " +
                " SELECT best.vorrat, SUM(buch.menge) FROM MedBestand best " +
                " JOIN best.buchungen buch " +
                " WHERE best.vorrat.bewohner = :bewohner AND best.vorrat.bis = '9999-12-31 23:59:59' " +
                " GROUP BY best.vorrat ")
})


public class MedVorrat implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "VorID")
    private Long vorID;
    @Basic(optional = false)
    @Column(name = "Text")
    private String text;
    @Basic(optional = false)
    @Column(name = "Von")
    @Temporal(TemporalType.TIMESTAMP)
    private Date von;
    @Basic(optional = false)
    @Column(name = "Bis")
    @Temporal(TemporalType.TIMESTAMP)
    private Date bis;

    public MedVorrat() {


    }

    public Bewohner getBewohner() {
        return bewohner;
    }

    public void setBewohner(Bewohner bewohner) {
        this.bewohner = bewohner;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public MedVorrat(Bewohner bewohner, String text) {
        this.bewohner = bewohner;
        this.text = text;
        this.user = OPDE.getLogin().getUser();
        this.von = new Date();
        this.bis = SYSConst.DATE_BIS_AUF_WEITERES;

    }

    public Long getVorID() {
        return vorID;
    }

    public void setVorID(Long vorID) {
        this.vorID = vorID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getVon() {
        return von;
    }

    public void setVon(Date von) {
        this.von = von;
    }

    public Date getBis() {
        return bis;
    }

    public void setBis(Date bis) {
        this.bis = bis;
    }

    // ==
    // 1:N Relationen
    // ==
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vorrat")
    private Collection<MedBestand> bestaende;

    // ==
    // N:1 Relationen
    // ==
    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Bewohner bewohner;
    // ==
    // N:1 Relationen
    // ==
    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (vorID != null ? vorID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MedVorrat)) {
            return false;
        }
        MedVorrat other = (MedVorrat) object;
        if ((this.vorID == null && other.vorID != null) || (this.vorID != null && !this.vorID.equals(other.vorID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.MedVorrat[vorID=" + vorID + "]";
    }

    public Collection<MedBestand> getBestaende() {
        return bestaende;
    }
}
                                       