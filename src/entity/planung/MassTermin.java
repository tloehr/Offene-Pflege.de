/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.planung;

import entity.Intervention;
import op.tools.SYSCalendar;
import org.joda.time.DateMidnight;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author tloehr
 */
@Entity
@Table(name = "MassTermin")
@NamedQueries({
        @NamedQuery(name = "MassTermin.findAll", query = "SELECT m FROM MassTermin m"),
        @NamedQuery(name = "MassTermin.findByTermID", query = "SELECT m FROM MassTermin m WHERE m.termID = :termID"),
        @NamedQuery(name = "MassTermin.findByNachtMo", query = "SELECT m FROM MassTermin m WHERE m.nachtMo = :nachtMo"),
        @NamedQuery(name = "MassTermin.findByMorgens", query = "SELECT m FROM MassTermin m WHERE m.morgens = :morgens"),
        @NamedQuery(name = "MassTermin.findByMittags", query = "SELECT m FROM MassTermin m WHERE m.mittags = :mittags"),
        @NamedQuery(name = "MassTermin.findByNachmittags", query = "SELECT m FROM MassTermin m WHERE m.nachmittags = :nachmittags"),
        @NamedQuery(name = "MassTermin.findByAbends", query = "SELECT m FROM MassTermin m WHERE m.abends = :abends"),
        @NamedQuery(name = "MassTermin.findByNachtAb", query = "SELECT m FROM MassTermin m WHERE m.nachtAb = :nachtAb"),
        @NamedQuery(name = "MassTermin.findByUhrzeitAnzahl", query = "SELECT m FROM MassTermin m WHERE m.uhrzeitAnzahl = :uhrzeitAnzahl"),
        @NamedQuery(name = "MassTermin.findByUhrzeit", query = "SELECT m FROM MassTermin m WHERE m.uhrzeit = :uhrzeit"),
        @NamedQuery(name = "MassTermin.findByTaeglich", query = "SELECT m FROM MassTermin m WHERE m.taeglich = :taeglich"),
        @NamedQuery(name = "MassTermin.findByWoechentlich", query = "SELECT m FROM MassTermin m WHERE m.woechentlich = :woechentlich"),
        @NamedQuery(name = "MassTermin.findByMonatlich", query = "SELECT m FROM MassTermin m WHERE m.monatlich = :monatlich"),
        @NamedQuery(name = "MassTermin.findByTagNum", query = "SELECT m FROM MassTermin m WHERE m.tagNum = :tagNum"),
        @NamedQuery(name = "MassTermin.findByMon", query = "SELECT m FROM MassTermin m WHERE m.mon = :mon"),
        @NamedQuery(name = "MassTermin.findByDie", query = "SELECT m FROM MassTermin m WHERE m.die = :die"),
        @NamedQuery(name = "MassTermin.findByMit", query = "SELECT m FROM MassTermin m WHERE m.mit = :mit"),
        @NamedQuery(name = "MassTermin.findByDon", query = "SELECT m FROM MassTermin m WHERE m.don = :don"),
        @NamedQuery(name = "MassTermin.findByFre", query = "SELECT m FROM MassTermin m WHERE m.fre = :fre"),
        @NamedQuery(name = "MassTermin.findBySam", query = "SELECT m FROM MassTermin m WHERE m.sam = :sam"),
        @NamedQuery(name = "MassTermin.findBySon", query = "SELECT m FROM MassTermin m WHERE m.son = :son"),
        @NamedQuery(name = "MassTermin.findByErforderlich", query = "SELECT m FROM MassTermin m WHERE m.erforderlich = :erforderlich"),
        @NamedQuery(name = "MassTermin.findByLDatum", query = "SELECT m FROM MassTermin m WHERE m.lDatum = :lDatum"),
        @NamedQuery(name = "MassTermin.findByDauer", query = "SELECT m FROM MassTermin m WHERE m.dauer = :dauer")})
public class MassTermin implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "TermID")
    private Long termID;
    @Column(name = "NachtMo")
    private Short nachtMo;
    @Column(name = "Morgens")
    private Short morgens;
    @Column(name = "Mittags")
    private Short mittags;
    @Column(name = "Nachmittags")
    private Short nachmittags;
    @Column(name = "Abends")
    private Short abends;
    @Column(name = "NachtAb")
    private Short nachtAb;
    @Column(name = "UhrzeitAnzahl")
    private Short uhrzeitAnzahl;
    @Column(name = "Uhrzeit")
    @Temporal(TemporalType.TIME)
    private Date uhrzeit;
    @Column(name = "Taeglich")
    private Short taeglich;
    @Column(name = "Woechentlich")
    private Short woechentlich;
    @Column(name = "Monatlich")
    private Short monatlich;
    @Column(name = "TagNum")
    private Short tagNum;
    @Column(name = "Mon")
    private Short mon;
    @Column(name = "Die")
    private Short die;
    @Column(name = "Mit")
    private Short mit;
    @Column(name = "Don")
    private Short don;
    @Column(name = "Fre")
    private Short fre;
    @Column(name = "Sam")
    private Short sam;
    @Column(name = "Son")
    private Short son;
    @Column(name = "Erforderlich")
    private Boolean erforderlich;
    @Basic(optional = false)
    @Column(name = "LDatum")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lDatum;
    @Basic(optional = false)
    @Column(name = "Dauer")
    private BigDecimal dauer;
    @Lob
    @Column(name = "XML")
    private String xml;
    @Lob
    @Column(name = "Bemerkung")
    private String bemerkung;
    @Version
    @Column(name = "version")
    private Long version;

    @JoinColumn(name = "PlanID", referencedColumnName = "PlanID")
    @ManyToOne
    private Planung planung;
    @JoinColumn(name = "MassID", referencedColumnName = "MassID")
    @ManyToOne
    private Intervention massnahme;

    public MassTermin() {
    }

    public MassTermin(Planung planung, Intervention massnahme) {
        this.morgens = 1;
        this.taeglich = 1;
        this.lDatum = new DateMidnight().toDate();
        this.dauer = massnahme.getDauer();
        this.planung = planung;
        this.massnahme = massnahme;
    }

    public Long getTermID() {
        return termID;
    }

    public void setTermID(Long termID) {
        this.termID = termID;
    }

    public Short getNachtMo() {
        return nachtMo;
    }

    public void setNachtMo(Short nachtMo) {
        this.nachtMo = nachtMo;
    }

    public Short getMorgens() {
        return morgens;
    }

    public void setMorgens(Short morgens) {
        this.morgens = morgens;
    }

    public Short getMittags() {
        return mittags;
    }

    public void setMittags(Short mittags) {
        this.mittags = mittags;
    }

    public Short getNachmittags() {
        return nachmittags;
    }

    public void setNachmittags(Short nachmittags) {
        this.nachmittags = nachmittags;
    }

    public Short getAbends() {
        return abends;
    }

    public void setAbends(Short abends) {
        this.abends = abends;
    }

    public Short getNachtAb() {
        return nachtAb;
    }

    public void setNachtAb(Short nachtAb) {
        this.nachtAb = nachtAb;
    }

    public Short getUhrzeitAnzahl() {
        return uhrzeitAnzahl;
    }

    public void setUhrzeitAnzahl(Short uhrzeitAnzahl) {
        this.uhrzeitAnzahl = uhrzeitAnzahl;
    }

    public Date getUhrzeit() {
        return uhrzeit;
    }

    public void setUhrzeit(Date uhrzeit) {
        this.uhrzeit = uhrzeit;
    }

    public Short getTaeglich() {
        return taeglich;
    }

    public void setTaeglich(Short taeglich) {
        this.taeglich = taeglich;
    }

    public Short getWoechentlich() {
        return woechentlich;
    }

    public void setWoechentlich(Short woechentlich) {
        this.woechentlich = woechentlich;
    }

    public Short getMonatlich() {
        return monatlich;
    }

    public void setMonatlich(Short monatlich) {
        this.monatlich = monatlich;
    }

    public Short getTagNum() {
        return tagNum;
    }

    public void setTagNum(Short tagNum) {
        this.tagNum = tagNum;
    }

    public Short getMon() {
        return mon;
    }

    public void setMon(Short mon) {
        this.mon = mon;
    }

    public Short getDie() {
        return die;
    }

    public void setDie(Short die) {
        this.die = die;
    }

    public Short getMit() {
        return mit;
    }

    public void setMit(Short mit) {
        this.mit = mit;
    }

    public Short getDon() {
        return don;
    }

    public void setDon(Short don) {
        this.don = don;
    }

    public Short getFre() {
        return fre;
    }

    public void setFre(Short fre) {
        this.fre = fre;
    }

    public Short getSam() {
        return sam;
    }

    public void setSam(Short sam) {
        this.sam = sam;
    }

    public Short getSon() {
        return son;
    }

    public void setSon(Short son) {
        this.son = son;
    }

    public Boolean isErforderlich() {
        return erforderlich;
    }

    public void setErforderlich(Boolean erforderlich) {
        this.erforderlich = erforderlich;
    }

    public Date getLDatum() {
        return lDatum;
    }

    public void setLDatum(Date lDatum) {
        this.lDatum = lDatum;
    }

    public BigDecimal getDauer() {
        return dauer;
    }

    public void setDauer(BigDecimal dauer) {
        this.dauer = dauer;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public Intervention getMassnahme() {
        return massnahme;
    }

    public void setMassnahme(Intervention massnahme) {
        this.massnahme = massnahme;
    }

    public Date getlDatum() {
        return lDatum;
    }

    public void setlDatum(Date lDatum) {
        this.lDatum = lDatum;
    }

    public Planung getPlanung() {
        return planung;
    }

    public void setPlanung(Planung planung) {
        this.planung = planung;
    }

    public boolean verwendetUhrzeit() {
        return uhrzeit != null;
    }

    public boolean isTaeglich() {
        return taeglich > 0;
    }

    public boolean isWoechentlich() {
        return woechentlich > 0;
    }

    /**
     * @param date, zu prüfendes Datum.
     * @return Ist <code>true</code>, wenn diese Planung wöchentlich gilt und das Attribut mit dem aktuellen Wochentagsnamen größer null ist.
     */
    public boolean isPassenderWochentag(Date date) {
        boolean passend = false;

        if (isWoechentlich()) { // wenn nicht wöchentlich, dann passt gar nix
            GregorianCalendar gcDate = SYSCalendar.toGC(date);
            switch (gcDate.get(GregorianCalendar.DAY_OF_WEEK)) {
                case GregorianCalendar.MONDAY: {
                    passend = mon > 0;
                    break;
                }
                case GregorianCalendar.TUESDAY: {
                    passend = die > 0;
                    break;
                }
                case GregorianCalendar.WEDNESDAY: {
                    passend = mit > 0;
                    break;
                }
                case GregorianCalendar.THURSDAY: {
                    passend = don > 0;
                    break;
                }
                case GregorianCalendar.FRIDAY: {
                    passend = fre > 0;
                    break;
                }
                case GregorianCalendar.SATURDAY: {
                    passend = sam > 0;
                    break;
                }
                case GregorianCalendar.SUNDAY: {
                    passend = son > 0;
                    break;
                }
                default: {
                    passend = false;
                    break;
                }
            }
        }
        return passend;
    }

    /**
     * @param date, zu prüfendes Datum.
     * @return Ist <code>true</code>, wenn diese Planung monatlich gilt und das Attribut <code>tagnum</code> dem aktuellen Tag im Monat entspricht
     *         <b>oder</b> das Attribut mit dem aktuellen Wochentagsnamen gleich dem Wochentag im Monat entpricht (der erste Mitwwoch im Monat hat 1, der zweite 2 usw...).
     */
    public boolean isPassenderTagImMonat(Date date) {
        boolean passend = false;
        if (isMonatlich()) { // wenn nicht monatlich, dann passt gar nix
            GregorianCalendar gcDate = SYSCalendar.toGC(date);

            passend = tagNum == gcDate.get(GregorianCalendar.DAY_OF_MONTH);

            if (!passend) {
                switch (gcDate.get(GregorianCalendar.DAY_OF_WEEK)) {
                    case GregorianCalendar.MONDAY: {
                        passend = mon == gcDate.get(GregorianCalendar.DAY_OF_WEEK_IN_MONTH);
                        break;
                    }
                    case GregorianCalendar.TUESDAY: {
                        passend = die == gcDate.get(GregorianCalendar.DAY_OF_WEEK_IN_MONTH);
                        break;
                    }
                    case GregorianCalendar.WEDNESDAY: {
                        passend = mit == gcDate.get(GregorianCalendar.DAY_OF_WEEK_IN_MONTH);
                        break;
                    }
                    case GregorianCalendar.THURSDAY: {
                        passend = don == gcDate.get(GregorianCalendar.DAY_OF_WEEK_IN_MONTH);
                        break;
                    }
                    case GregorianCalendar.FRIDAY: {
                        passend = fre == gcDate.get(GregorianCalendar.DAY_OF_WEEK_IN_MONTH);
                        break;
                    }
                    case GregorianCalendar.SATURDAY: {
                        passend = sam == gcDate.get(GregorianCalendar.DAY_OF_WEEK_IN_MONTH);
                        break;
                    }
                    case GregorianCalendar.SUNDAY: {
                        passend = son == gcDate.get(GregorianCalendar.DAY_OF_WEEK_IN_MONTH);
                        break;
                    }
                    default: {
                        passend = false;
                        break;
                    }
                }
            }
        }
        return passend;
    }

    public boolean isMonatlich() {
        return monatlich > 0;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (termID != null ? termID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MassTermin)) {
            return false;
        }
        MassTermin other = (MassTermin) object;
        if ((this.termID == null && other.termID != null) || (this.termID != null && !this.termID.equals(other.termID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.MassTermin[termID=" + termID + "]";
    }

}
