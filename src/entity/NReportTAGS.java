/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.awt.Color;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author tloehr
 */
@Entity
@Table(name = "PBericht_TAGS")
@NamedQueries({
    @NamedQuery(name = "PBerichtTAGS.findAll", query = "SELECT p FROM NReportTAGS p"),
    @NamedQuery(name = "PBerichtTAGS.findAllActive", query = "SELECT p FROM NReportTAGS p WHERE p.aktiv = TRUE ORDER BY p.besonders DESC, p.sort DESC, p.bezeichnung "),
    @NamedQuery(name = "PBerichtTAGS.findByPbtagid", query = "SELECT p FROM NReportTAGS p WHERE p.pbtagid = :pbtagid"),
    @NamedQuery(name = "PBerichtTAGS.findByBezeichnung", query = "SELECT p FROM NReportTAGS p WHERE p.bezeichnung = :bezeichnung"),
    @NamedQuery(name = "PBerichtTAGS.findByKurzbezeichnung", query = "SELECT p FROM NReportTAGS p WHERE p.kurzbezeichnung = :kurzbezeichnung"),
    @NamedQuery(name = "PBerichtTAGS.findByAktiv", query = "SELECT p FROM NReportTAGS p WHERE p.aktiv = :aktiv"),
    @NamedQuery(name = "PBerichtTAGS.findBySystem", query = "SELECT p FROM NReportTAGS p WHERE p.system = :system"),
    @NamedQuery(name = "PBerichtTAGS.findByFarbe", query = "SELECT p FROM NReportTAGS p WHERE p.farbe = :farbe")
})
public class NReportTAGS implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final Color[] COLORS = new Color[]{Color.BLACK, Color.WHITE, Color.CYAN, Color.RED, Color.GREEN, Color.BLUE, Color.PINK, Color.MAGENTA, Color.YELLOW, Color.GRAY, Color.LIGHT_GRAY, Color.DARK_GRAY, Color.ORANGE};
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "PBTAGID")
    private Long pbtagid;
    @Basic(optional = false)
    @Column(name = "Bezeichnung", length = 45)
    private String bezeichnung;
    @Basic(optional = false)
    @Column(name = "Kurzbezeichnung", length = 45)
    private String kurzbezeichnung;
    @Basic(optional = false)
    @Column(name = "Aktiv")
    private boolean aktiv;
    @Basic(optional = false)
    @Column(name = "System")
    private boolean system;
    @Column(name = "Besonders")
    private boolean besonders;
    @Basic(optional = false)
    @Column(name = "Farbe")
    private int farbe;
    @Basic(optional = false)
    @Column(name = "Sort")
    private int sort;
//    @ManyToMany(mappedBy = "tags")
//    private Collection<NReport> pflegeberichte;

    public NReportTAGS() {
    }

    public Long getPbtagid() {
        return pbtagid;
    }

    public void setPbtagid(Long pbtagid) {
        this.pbtagid = pbtagid;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getKurzbezeichnung() {
        return kurzbezeichnung;
    }

    public void setKurzbezeichnung(String kurzbezeichnung) {
        this.kurzbezeichnung = kurzbezeichnung;
    }

    public boolean isBesonders() {
        return besonders;
    }

    public void setBesonders(boolean besonders) {
        this.besonders = besonders;
    }

//    public Collection<NReport> getNReport() {
//        return pflegeberichte;
//    }

    public boolean getAktiv() {
        return aktiv;
    }

    public void setAktiv(boolean aktiv) {
        this.aktiv = aktiv;
    }

    public boolean getSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    public Color getColor() {
        return COLORS[farbe];
    }

    public int getFarbe() {
        return farbe;
    }

    public void setFarbe(int farbe) {
        this.farbe = farbe;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pbtagid != null ? pbtagid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof NReportTAGS)) {
            return false;
        }
        NReportTAGS other = (NReportTAGS) object;
        if ((this.pbtagid == null && other.pbtagid != null) || (this.pbtagid != null && !this.pbtagid.equals(other.pbtagid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.NReportTAGS[pbtagid=" + pbtagid + "]";
    }
}
