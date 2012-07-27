/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.planung;

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
        @NamedQuery(name = "InterventionSchedule.findAll", query = "SELECT m FROM InterventionSchedule m"),
        @NamedQuery(name = "InterventionSchedule.findByTermID", query = "SELECT m FROM InterventionSchedule m WHERE m.termID = :termID"),
        @NamedQuery(name = "InterventionSchedule.findByNachtMo", query = "SELECT m FROM InterventionSchedule m WHERE m.nachtMo = :nachtMo"),
        @NamedQuery(name = "InterventionSchedule.findByMorgens", query = "SELECT m FROM InterventionSchedule m WHERE m.morgens = :morgens"),
        @NamedQuery(name = "InterventionSchedule.findByMittags", query = "SELECT m FROM InterventionSchedule m WHERE m.mittags = :mittags"),
        @NamedQuery(name = "InterventionSchedule.findByNachmittags", query = "SELECT m FROM InterventionSchedule m WHERE m.nachmittags = :nachmittags"),
        @NamedQuery(name = "InterventionSchedule.findByAbends", query = "SELECT m FROM InterventionSchedule m WHERE m.abends = :abends"),
        @NamedQuery(name = "InterventionSchedule.findByNachtAb", query = "SELECT m FROM InterventionSchedule m WHERE m.nachtAb = :nachtAb"),
        @NamedQuery(name = "InterventionSchedule.findByUhrzeitAnzahl", query = "SELECT m FROM InterventionSchedule m WHERE m.uhrzeitAnzahl = :uhrzeitAnzahl"),
        @NamedQuery(name = "InterventionSchedule.findByUhrzeit", query = "SELECT m FROM InterventionSchedule m WHERE m.uhrzeit = :uhrzeit"),
        @NamedQuery(name = "InterventionSchedule.findByTaeglich", query = "SELECT m FROM InterventionSchedule m WHERE m.taeglich = :taeglich"),
        @NamedQuery(name = "InterventionSchedule.findByWoechentlich", query = "SELECT m FROM InterventionSchedule m WHERE m.woechentlich = :woechentlich"),
        @NamedQuery(name = "InterventionSchedule.findByMonatlich", query = "SELECT m FROM InterventionSchedule m WHERE m.monatlich = :monatlich"),
        @NamedQuery(name = "InterventionSchedule.findByTagNum", query = "SELECT m FROM InterventionSchedule m WHERE m.tagNum = :tagNum"),
        @NamedQuery(name = "InterventionSchedule.findByMon", query = "SELECT m FROM InterventionSchedule m WHERE m.mon = :mon"),
        @NamedQuery(name = "InterventionSchedule.findByDie", query = "SELECT m FROM InterventionSchedule m WHERE m.die = :die"),
        @NamedQuery(name = "InterventionSchedule.findByMit", query = "SELECT m FROM InterventionSchedule m WHERE m.mit = :mit"),
        @NamedQuery(name = "InterventionSchedule.findByDon", query = "SELECT m FROM InterventionSchedule m WHERE m.don = :don"),
        @NamedQuery(name = "InterventionSchedule.findByFre", query = "SELECT m FROM InterventionSchedule m WHERE m.fre = :fre"),
        @NamedQuery(name = "InterventionSchedule.findBySam", query = "SELECT m FROM InterventionSchedule m WHERE m.sam = :sam"),
        @NamedQuery(name = "InterventionSchedule.findBySon", query = "SELECT m FROM InterventionSchedule m WHERE m.son = :son"),
        @NamedQuery(name = "InterventionSchedule.findByErforderlich", query = "SELECT m FROM InterventionSchedule m WHERE m.erforderlich = :erforderlich"),
        @NamedQuery(name = "InterventionSchedule.findByLDatum", query = "SELECT m FROM InterventionSchedule m WHERE m.lDatum = :lDatum"),
        @NamedQuery(name = "InterventionSchedule.findByDauer", query = "SELECT m FROM InterventionSchedule m WHERE m.dauer = :dauer")})
public class InterventionSchedule implements Serializable, Cloneable {
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
    @Column(name = "Bemerkung")
    private String bemerkung;
    @Version
    @Column(name = "version")
    private Long version;

    @JoinColumn(name = "PlanID", referencedColumnName = "PlanID")
    @ManyToOne
    private NursingProcess nursingProcess;
    @JoinColumn(name = "MassID", referencedColumnName = "MassID")
    @ManyToOne
    private Intervention intervention;

    public InterventionSchedule() {
    }

    public InterventionSchedule(NursingProcess planung, Intervention intervention) {
        this.morgens = 1;
        this.taeglich = 1;
        this.lDatum = new DateMidnight().toDate();
        this.dauer = intervention.getDauer();
        this.nursingProcess = planung;
        this.intervention = intervention;

        this.nachtMo = 0;
        this.mittags = 0;
        this.nachmittags = 0;
        this.abends = 0;
        this.nachtAb = 0;
        this.uhrzeitAnzahl = 0;
        this.uhrzeit = null;
        this.woechentlich = 0;
        this.monatlich = 0;
        this.tagNum = 0;
        this.mon = 0;
        this.die = 0;
        this.mit = 0;
        this.don = 0;
        this.fre = 0;
        this.sam = 0;
        this.son = 0;
        this.erforderlich = false;
        this.bemerkung = null;
    }

    public InterventionSchedule(Short nachtMo, Short morgens, Short mittags, Short nachmittags, Short abends, Short nachtAb, Short uhrzeitAnzahl, Date uhrzeit, Short taeglich, Short woechentlich, Short monatlich, Short tagNum, Short mon, Short die, Short mit, Short don, Short fre, Short sam, Short son, Boolean erforderlich, Date lDatum, BigDecimal dauer, String bemerkung, NursingProcess nursingProcess, Intervention intervention) {
        this.nachtMo = nachtMo;
        this.morgens = morgens;
        this.mittags = mittags;
        this.nachmittags = nachmittags;
        this.abends = abends;
        this.nachtAb = nachtAb;
        this.uhrzeitAnzahl = uhrzeitAnzahl;
        this.uhrzeit = uhrzeit;
        this.taeglich = taeglich;
        this.woechentlich = woechentlich;
        this.monatlich = monatlich;
        this.tagNum = tagNum;
        this.mon = mon;
        this.die = die;
        this.mit = mit;
        this.don = don;
        this.fre = fre;
        this.sam = sam;
        this.son = son;
        this.erforderlich = erforderlich;
        this.lDatum = lDatum;
        this.dauer = dauer;
        this.bemerkung = bemerkung;
        this.nursingProcess = nursingProcess;
        this.intervention = intervention;
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

    public Boolean isFloating() {
        return erforderlich;
    }

    public void setFloating(Boolean floating) {
        this.erforderlich = floating;
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

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public Intervention getIntervention() {
        return intervention;
    }

    public void setIntervention(Intervention intervention) {
        this.intervention = intervention;
    }

    public Date getlDatum() {
        return lDatum;
    }

    public void setlDatum(Date lDatum) {
        this.lDatum = lDatum;
    }

    public NursingProcess getNursingProcess() {
        return nursingProcess;
    }

    public void setNursingProcess(NursingProcess nursingProcess) {
        this.nursingProcess = nursingProcess;
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

        if (!(object instanceof InterventionSchedule)) {
            return false;
        }
        InterventionSchedule other = (InterventionSchedule) object;
        if ((this.termID == null && other.termID != null) || (this.termID != null && !this.termID.equals(other.termID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.InterventionSchedule[termID=" + termID + "]";
    }

    @Override
    public InterventionSchedule clone() {
        return new InterventionSchedule(nachtMo, morgens, mittags, nachmittags, abends, nachtAb, uhrzeitAnzahl, uhrzeit, taeglich, woechentlich, monatlich, tagNum, mon, die, mit, don, fre, sam, son, erforderlich, lDatum, dauer, bemerkung, nursingProcess, intervention);
    }
}
