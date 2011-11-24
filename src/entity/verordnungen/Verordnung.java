/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.verordnungen;

import entity.*;
import entity.files.Sysver2file;
import entity.vorgang.SYSVER2VORGANG;
import entity.vorgang.VorgangElement;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * @author tloehr
 */
@Entity
@Table(name = "BHPVerordnung")
@NamedQueries({
        @NamedQuery(name = "Verordnung.findAll", query = "SELECT b FROM Verordnung b"),
        @NamedQuery(name = "Verordnung.findByVerID", query = "SELECT b FROM Verordnung b WHERE b.verid = :verid"),
        @NamedQuery(name = "Verordnung.findByAnDatum", query = "SELECT b FROM Verordnung b WHERE b.anDatum = :anDatum"),
        @NamedQuery(name = "Verordnung.findByVorgang", query = " "
                + " SELECT ve, av.pdca FROM Verordnung ve "
                + " JOIN ve.attachedVorgaenge av"
                + " JOIN av.vorgang v"
                + " WHERE v = :vorgang "),
        @NamedQuery(name = "Verordnung.findByAbDatum", query = "SELECT b FROM Verordnung b WHERE b.abDatum = :abDatum"),
        @NamedQuery(name = "Verordnung.findByBisPackEnde", query = "SELECT b FROM Verordnung b WHERE b.bisPackEnde = :bisPackEnde"),
        @NamedQuery(name = "Verordnung.findByVerKennung", query = "SELECT b FROM Verordnung b WHERE b.verKennung = :verKennung"),
        @NamedQuery(name = "Verordnung.findByStellplan", query = "SELECT b FROM Verordnung b WHERE b.stellplan = :stellplan")})

@SqlResultSetMappings({
        @SqlResultSetMapping(name = "Verordnung.findByBewohnerResultMapping",
                entities = @EntityResult(entityClass = Verordnung.class),
                columns = {@ColumnResult(name = "bestand.summe"), @ColumnResult(name = "vor.saldo")}
        )
})

@NamedNativeQueries({
        /**
         * Diese Query ist eine native Query. Ich habe keinen anderen Weg gefunden auf SubSelects zu JOINen.
         * Das Ergebnis ist eine Liste aller Einrichtungsbezogenen Pflegeberichte mit einer
         * Angabe, ob ein bestimmter User diese bereits zur Kenntnis genommen hat oder nicht.
         * Durch eine passende SQLResultSetMap ist das Ergebnis ein 2 wertiges Array aus Objekten. Das erste
         * Objekt ist immer der Pflegebericht, das zweiter ist ein Long Wert, der das count Ergebnis
         * des Joins enthält.
         *
         */
        @NamedNativeQuery(name = "Verordnung.findByBewohnerMitSaldenUndVorrat", query = " " +
                " SELECT v.*, bestand.*, vor.* " +
                " FROM BHPVerordnung v " +
                // Dieser Konstrukt bestimmt die Vorräte für einen Bewohner
                // Dabei wird berücksichtigt, dass ein Vorrat unterschiedliche Hersteller umfassen
                // kann. Dies wird durch den mehrfach join erreicht. Dadurch stehen die verschiedenen
                // DafIDs der unterschiedlichen Produkte im selben Vorrat jeweils in verschiedenen Zeilen.
                // Da sind dann für jeden Vorrat alle die DafIDs enthalten, die jemals auf den Vorrat
                // eingebucht wurden. Man kann z.B. sehen, dass VorID 435 bisher schon die DafIDs 50, 165 und 553
                // beinhaltet hatte.
                // Durch den LEFT OUTER JOIN pickt sich die Datenbank die richtigen Paare heraus.
                // Das braucht man, weil in der Verordnung ja nur die DafID steht, die am Anfang
                // verwendet wurde. Das kann ja mittlerweile eine ganz andere sein.
                " LEFT OUTER JOIN " +
                " ( " +
                "       SELECT DISTINCT a.* FROM ( " +
                "       SELECT best.*, sum(buch.Menge) saldo FROM MPBestand best " +
                "       INNER JOIN MPBuchung buch ON buch.BestID = best.BestID " +
                "       INNER JOIN MPVorrat vor1 ON best.VorID = vor1.VorID" +
                "       WHERE vor1.BWKennung = ? AND vor1.Bis = '9999-12-31 23:59:59'" +
                "       GROUP BY VorID" +
                "   ) a  " +
                "   INNER JOIN (" +
                "       SELECT best.VorID, best.DafID FROM MPBestand best " +
                "   ) b ON a.VorID = b.VorID " +
                " ) vor ON vor.DafID = v.DafID " +
                // Hier kommen jetzt die Bestände im Anbruch dabei. Die Namen der Medikamente könnten ja vom
                // ursprünglich verordneten abweichen.
                " LEFT OUTER JOIN( " +
                "       SELECT best1.*, SUM(buch1.Menge) summe " +
                "       FROM MPBestand best1 " +
                "       INNER JOIN MPBuchung buch1 ON buch1.BestID = best1.BestID " +
                "       WHERE best1.Aus = '9999-12-31 23:59:59' AND best1.Anbruch < now() " +
                "       GROUP BY best1.BestID" +
                " ) bestand ON bestand.VorID = vor.VorID " +
                " WHERE BWKennung = ? "
                , resultSetMapping = "Verordnung.findByBewohnerResultMapping")

})

public class Verordnung implements Serializable, VorgangElement {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "VerID")
    private Long verid;
    @Basic(optional = false)
    @Column(name = "AnDatum")
    @Temporal(TemporalType.TIMESTAMP)
    private Date anDatum;
    @Basic(optional = false)
    @Column(name = "AbDatum")
    @Temporal(TemporalType.TIMESTAMP)
    private Date abDatum;
    @Column(name = "BisPackEnde")
    private Boolean bisPackEnde;
    @Basic(optional = false)
    @Column(name = "VerKennung")
    private long verKennung;
    @Lob
    @Column(name = "Bemerkung")
    private String bemerkung;
    @Basic(optional = false)
    @Column(name = "Stellplan")
    private boolean stellplan;

    // ==
    // 1:N Relationen
    // ==
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "verordnung")
    private Collection<Sysver2file> attachedFiles;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "verordnung")
    private Collection<SYSVER2VORGANG> attachedVorgaenge;
    // ==
    // N:1 Relationen
    // ==
    @JoinColumn(name = "AnUKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users angesetztDurch;
    @JoinColumn(name = "AbUKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users abgesetztDurch;
    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Bewohner bewohner;
    @JoinColumn(name = "MassID", referencedColumnName = "MassID")
    @ManyToOne
    private Massnahmen massnahme;
    @JoinColumn(name = "DafID", referencedColumnName = "DafID")
    @ManyToOne
    private Darreichung darreichung;
    @JoinColumn(name = "SitID", referencedColumnName = "SitID")
    @ManyToOne
    private Situationen situation;
    @JoinColumn(name = "AnKHID", referencedColumnName = "KHID")
    @ManyToOne
    private Krankenhaus anKH;
    @JoinColumn(name = "AbKHID", referencedColumnName = "KHID")
    @ManyToOne
    private Krankenhaus abKH;
    @JoinColumn(name = "AnArztID", referencedColumnName = "ArztID")
    @ManyToOne
    private Arzt anArzt;
    @JoinColumn(name = "AbArztID", referencedColumnName = "ArztID")
    @ManyToOne
    private Arzt abArzt;


    public Verordnung() {
    }

    public Long getVerid() {
        return verid;
    }

    public void setVerid(Long verid) {
        this.verid = verid;
    }

    public Date getAnDatum() {
        return anDatum;
    }

    public void setAnDatum(Date anDatum) {
        this.anDatum = anDatum;
    }

    public Date getAbDatum() {
        return abDatum;
    }

    public void setAbDatum(Date abDatum) {
        this.abDatum = abDatum;
    }

    public Krankenhaus getAnKH() {
        return anKH;
    }

    public void setAnKH(Krankenhaus anKH) {
        this.anKH = anKH;
    }

    public Krankenhaus getAbKH() {
        return abKH;
    }

    public void setAbKH(Krankenhaus abKH) {
        this.abKH = abKH;
    }

    public Arzt getAnArzt() {
        return anArzt;
    }

    public void setAnArzt(Arzt anArzt) {
        this.anArzt = anArzt;
    }

    public Arzt getAbArzt() {
        return abArzt;
    }

    public void setAbArzt(Arzt abArzt) {
        this.abArzt = abArzt;
    }

    public Boolean isBisPackEnde() {
        return bisPackEnde;
    }

    public void setBisPackEnde(Boolean bisPackEnde) {
        this.bisPackEnde = bisPackEnde;
    }

    public long getVerKennung() {
        return verKennung;
    }

    public void setVerKennung(long verKennung) {
        this.verKennung = verKennung;
    }

    public String getBemerkung() {
        return SYSTools.catchNull(bemerkung);
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public boolean isStellplan() {
        return stellplan;
    }

    public void setStellplan(boolean stellplan) {
        this.stellplan = stellplan;
    }

    public Users getAbgesetztDurch() {
        return abgesetztDurch;
    }

    public Situationen getSituation() {
        return situation;
    }

    public boolean hasMedi() {
        return darreichung != null;
    }

    public void setSituation(Situationen situation) {
        this.situation = situation;
    }

    public Darreichung getDarreichung() {
        return darreichung;
    }

    public void setDarreichung(Darreichung darreichung) {
        this.darreichung = darreichung;
    }

    public Massnahmen getMassnahme() {
        return massnahme;
    }

    public void setMassnahme(Massnahmen massnahme) {
        this.massnahme = massnahme;
    }

    public void setAbgesetztDurch(Users abgesetztDurch) {
        this.abgesetztDurch = abgesetztDurch;
    }

    public Users getAngesetztDurch() {
        return angesetztDurch;
    }

    public void setAngesetztDurch(Users angesetztDurch) {
        this.angesetztDurch = angesetztDurch;
    }

    public Collection<Sysver2file> getAttachedFiles() {
        return attachedFiles;
    }

    public Collection<SYSVER2VORGANG> getAttachedVorgaenge() {
        return attachedVorgaenge;
    }

    public Bewohner getBewohner() {
        return bewohner;
    }

    public void setBewohner(Bewohner bewohner) {
        this.bewohner = bewohner;
    }

    public boolean isAbgesetzt() {
        return abDatum.before(SYSConst.DATE_BIS_AUF_WEITERES);
    }

    public boolean isBedarf() {
        return situation != null;
    }

    @Override
    public long getPITInMillis() {
        return anDatum.getTime();
    }

    @Override
    public String getContentAsHTML() {
        // TODO: fehlt noch
        return "<html>not yet</html>";
    }

    @Override
    public String getPITAsHTML() {
        // TODO: fehlt noch
        return "<html>not yet</html>";
    }

    @Override
    public long getID() {
        return verid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (verid != null ? verid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Verordnung)) {
            return false;
        }
        Verordnung other = (Verordnung) object;
        if ((this.verid == null && other.verid != null) || (this.verid != null && !this.verid.equals(other.verid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.verordnungen.Verordnung[verID=" + verid + "]";
    }

}
