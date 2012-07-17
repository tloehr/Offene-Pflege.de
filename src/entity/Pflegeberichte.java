/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import entity.files.Syspb2file;
import entity.vorgang.SYSPB2VORGANG;
import entity.vorgang.VorgangElement;
import op.OPDE;
import op.tools.SYSTools;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

/**
 * @author tloehr
 */
@Entity
@Table(name = "Pflegeberichte")
@NamedQueries({
        @NamedQuery(name = "Pflegeberichte.findAll", query = "SELECT p FROM Pflegeberichte p"),
        @NamedQuery(name = "Pflegeberichte.findByPbid", query = "SELECT p FROM Pflegeberichte p WHERE p.pbid = :pbid"),
        @NamedQuery(name = "Pflegeberichte.findFirst", query = "SELECT p FROM Pflegeberichte p WHERE p.pbid = :pbid"),

//        @NamedQuery(name = "Pflegeberichte.findByBewohnerWithinPeriod", query = " "
//                + " SELECT p FROM Pflegeberichte p "
//                + " WHERE p.bewohner = :bewohner AND p.pit >= :von AND p.pit <= :bis "
//                + " ORDER BY p.pit DESC "),
        /**
         * Ermittelt Pflegeberichte eines Bewohner innerhalb eines bestimmten Zeitraums.
         * Nur die als Besonders markiert sind.
         */
        @NamedQuery(name = "Pflegeberichte.findAllByBewohner", query = " "
                + " SELECT p FROM Pflegeberichte p "
                + " WHERE p.bewohner = :bewohner "
                + " ORDER BY p.pit "),
        /**
         * Sucht Berichte für einen Bewohner mit einem bestimmten Suchbegriff.
         */
        @NamedQuery(name = "Pflegeberichte.findByBewohnerAndSearchText", query = " "
                + " SELECT p FROM Pflegeberichte p "
                + " WHERE p.bewohner = :bewohner "
                + " AND p.text like :search "
                + " ORDER BY p.pit DESC "),
        /**
         * Sucht Berichte für einen Bewohner mit einem bestimmten Suchbegriff.
         * Lässt die geänderten jedoch weg.
         */
        @NamedQuery(name = "Pflegeberichte.findByBewohnerAndSearchTextWithoutEdits", query = " "
                + " SELECT p FROM Pflegeberichte p "
                + " WHERE p.bewohner = :bewohner "
                + " AND p.text like :search "
                + " AND p.editedBy is null "
                + " ORDER BY p.pit DESC "),
//        /**
//         * Sucht Berichte für einen Bewohner mit bestimmten Markierungen
//         */
//        @NamedQuery(name = "Pflegeberichte.findByBewohnerAndTagsWithoutEdits", query = " "
//                + " SELECT p FROM Pflegeberichte p "
//                + " WHERE p.bewohner = :bewohner "
//                + " AND p.tags IN (SELECT p1.tags FROM PBerichtTAGS t JOIN t.pflegeberichte p1 WHERE p1=p and t.pbtagid IN (1,5,7,9))"
//                + " AND p.editedBy is null "
//                + " ORDER BY p.pit DESC "),
        /**
         * Sucht Berichte für einen Bewohner mit bestimmten Markierungen
         */
        @NamedQuery(name = "Pflegeberichte.findByVorgang", query = " "
                + " SELECT p, av.pdca FROM Pflegeberichte p "
                + " JOIN p.attachedVorgaenge av"
                + " JOIN av.vorgang v"
                + " WHERE v = :vorgang "),
        /**
         * Ermittelt Pflegeberichte eines Bewohner innerhalb eines bestimmten Zeitraums. Diesmal aber ohne
         * die gelöschten und geänderten.
         */
        @NamedQuery(name = "Pflegeberichte.findByBewohnerWithinPeriodWithoutEdits", query = " "
                + " SELECT p FROM Pflegeberichte p "
                + " WHERE p.bewohner = :bewohner AND p.pit >= :von AND p.pit <= :bis "
                + " AND p.editedBy is null "
                + " ORDER BY p.pit DESC ")
})
@SqlResultSetMappings({
        @SqlResultSetMapping(name = "Pflegeberichte.findByEinrichtungAndDatumAndAckUserResultMapping", entities =
        @EntityResult(entityClass = Pflegeberichte.class), columns =
        @ColumnResult(name = "num")),
        @SqlResultSetMapping(name = "Pflegeberichte.findBVAktivitaetResultMapping", entities =
        @EntityResult(entityClass = Bewohner.class), columns =
        @ColumnResult(name = "mypbid")),
        @SqlResultSetMapping(name = "Pflegeberichte.findSozialZeitenResultMapping", entities =
        @EntityResult(entityClass = Bewohner.class), columns =
                {@ColumnResult(name = "sdauer"), @ColumnResult(name = "peadauer")})
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
        @NamedNativeQuery(name = "Pflegeberichte.findByEinrichtungAndDatumAndAckUser", query = ""
                + " SELECT p.*, ifnull(p2u.num, 0) num FROM Pflegeberichte p "
                + " INNER JOIN Bewohner bw ON bw.BWKennung = p.BWKennung "
                + " INNER JOIN PB2TAGS tag ON tag.PBID = p.PBID "
                + " INNER JOIN Stationen stat ON stat.StatID = bw.StatID "
                + " LEFT OUTER JOIN ( SELECT pbid, count(*) num FROM PB2User WHERE UKennung = ? GROUP BY pbid, ukennung ) AS p2u ON p2u.PBID = p.PBID "
                + " WHERE "
                + "     stat.EKennung = ? "
                + "     AND p.PIT >= ? AND p.PIT <= ? "
                + "     AND tag.PBTAGID = 1 "
                + "     AND p.editBy IS NULL "
                + " GROUP BY p.PBID "
                + " ORDER BY p.PIT DESC", resultSetMapping = "Pflegeberichte.findByEinrichtungAndDatumAndAckUserResultMapping"),
        /**
         * Diese Query sucht alle Aktivitäten der BVs anhand der erstellten (oder auch nicht erstellten) Pflegeberichte
         * seit dem angegebenen Datum heraus.
         */
        @NamedNativeQuery(name = "Pflegeberichte.findBVAktivitaet", query = ""
                + " SELECT b.*, a.PBID mypbid " +
                " FROM Bewohner b " +
                " LEFT OUTER JOIN ( " +
                "    SELECT pb.* FROM Pflegeberichte pb " +
                "    LEFT OUTER JOIN PB2TAGS pbt ON pbt.PBID = pb.PBID " +
                "    LEFT OUTER JOIN PBericht_TAGS pbtags ON pbt.PBTAGID = pbtags.PBTAGID " +
                "    WHERE pb.PIT > ? AND pbtags.Kurzbezeichnung = 'BV'" +
                " ) a ON a.BWKennung = b.BWKennung " +
                " WHERE b.StatID IS NOT NULL AND b.adminonly <> 2 " +
                " ORDER BY b.BWKennung, a.pit ", resultSetMapping = "Pflegeberichte.findBVAktivitaetResultMapping"),
        /**
         * Diese Query sucht alle Aktivitäten der BVs anhand der erstellten (oder auch nicht erstellten) Pflegeberichte
         * seit dem angegebenen Datum heraus.
         */
        @NamedNativeQuery(name = "Pflegeberichte.findSozialZeiten", query = ""
                + " SELECT b.*, ifnull(soz.dauer,0) sdauer, ifnull(pea.dauer,0) peadauer FROM Bewohner b " +
                " LEFT OUTER JOIN (" +
                "                  SELECT p.BWKennung, ifnull(SUM(p.Dauer), 0) dauer " +
                "                  FROM Pflegeberichte p" +
                "                  INNER JOIN PB2TAGS pb ON p.PBID = pb.PBID" +
                "                  INNER JOIN PBericht_TAGS pt ON pt.PBTAGID = pb.PBTAGID" +
                "                  WHERE pt.Kurzbezeichnung='soz' AND Date(PIT) >= DATE(?) AND Date(PIT) <= DATE(?) " +
                "                  GROUP BY BWKennung " +
                " ) soz ON soz.BWKennung = b.BWKennung" +
                " LEFT OUTER JOIN (" +
                "                  SELECT p.BWKennung, ifnull(SUM(p.Dauer), 0) dauer " +
                "                  FROM Pflegeberichte p" +
                "                  INNER JOIN PB2TAGS pb ON p.PBID = pb.PBID" +
                "                  INNER JOIN PBericht_TAGS pt ON pt.PBTAGID = pb.PBTAGID" +
                "                  WHERE pt.Kurzbezeichnung='pea' AND Date(PIT) >= DATE(?) AND Date(PIT) <= DATE(?) " +
                "                  GROUP BY BWKennung " +
                " ) pea ON pea.BWKennung = b.BWKennung" +
                " WHERE b.StatID IS NOT NULL ", resultSetMapping = "Pflegeberichte.findSozialZeitenResultMapping")
})
public class Pflegeberichte implements Serializable, VorgangElement {

    /*
     * Native Queries
     */
//    public static String NativeFindByEinrichtungAndDatum = " "
//            + " SELECT p.*, ifnull(p2u.num,0) num FROM Pflegeberichte p "
//            + " INNER JOIN Bewohner bw ON bw.BWKennung = p.BWKennung "
//            + " INNER JOIN PB2TAGS tag ON tag.PBID = p.PBID "
//            + " INNER JOIN Stationen stat ON stat.StatID = bw.StatID "
//            + " LEFT OUTER JOIN ( SELECT pbid, count(*) num FROM PB2User WHERE UKennung = :ukennung GROUP BY pbid, ukennung ) AS p2u ON p2u.PBID = p.PBID "
//            + " WHERE "
//            + "     stat.EKennung = 'wiedenhof' "
//            + "     AND p.PIT >= '2010-05-16 00:00:00.0' AND p.PIT <= '2010-05-16 23:59:59.0' "
//            + "     AND tag.PBTAGID = 1 "
//            + "     AND p.editBy IS NULL "
//            + "     GROUP BY p.PBID "
//            + " ORDER BY p.PIT DESC";
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "PBID")
    private Long pbid;
    @Version
    @Column(name = "version")
    private Long version;
    @Basic(optional = false)
    @Column(name = "PIT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pit;
    @Basic(optional = false)
    @Column(name = "EditPIT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date editpit;
    @Lob
    @Column(name = "Text")
    private String text;
    @Basic(optional = false)
    @Column(name = "Dauer")
    private int dauer;
    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;
    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Bewohner bewohner;
    @JoinColumn(name = "editBy", referencedColumnName = "UKennung")
    @ManyToOne
    private Users editedBy;
    @JoinColumn(name = "ReplacedBy", referencedColumnName = "PBID")
    @OneToOne
    private Pflegeberichte replacedBy;
    @JoinColumn(name = "ReplacementFor", referencedColumnName = "PBID")
    @OneToOne
    private Pflegeberichte replacementFor;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pflegebericht")
    private Collection<Syspb2file> attachedFiles;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bericht")
    private Collection<PB2User> usersAcknowledged;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pflegebericht")
    private Collection<SYSPB2VORGANG> attachedVorgaenge;

    // ==
    // M:N Relationen
    // ==
    @ManyToMany
    @JoinTable(name = "PB2TAGS", joinColumns =
    @JoinColumn(name = "PBID"), inverseJoinColumns =
    @JoinColumn(name = "PBTAGID"))
    private Collection<PBerichtTAGS> tags;
//    @ManyToMany
//    @JoinTable(name = "SYSPB2VORGANG", joinColumns =
//    @JoinColumn(name = "PBID"), inverseJoinColumns =
//    @JoinColumn(name = "VorgangID"))
//    private Collection<Vorgaenge> vorgaenge;


    public Pflegeberichte() {
    }

    public Pflegeberichte(Bewohner bewohner) {
        this.pbid = 0l;
        this.bewohner = bewohner;
        this.user = OPDE.getLogin().getUser();
        this.attachedFiles = new ArrayList<Syspb2file>();
        this.tags = new ArrayList<PBerichtTAGS>();
    }

    public Long getPbid() {
        return pbid;
    }

    public void setPbid(Long pbid) {
        this.pbid = pbid;
    }

    public Collection<PB2User> getUsersAcknowledged() {
        return usersAcknowledged;
    }

    /**
     * PIT steht für Point In Time. In diesem Fall Datum und Uhrzeit des Bericht Eintrages.
     *
     * @return
     */
    public Date getPit() {
        return pit;
    }

    /**
     * @param pit steht für Point In Time. In diesem Fall Datum und Uhrzeit des Bericht Eintrages.
     */
    public void setPit(Date pit) {
        this.pit = pit;
    }

    /**
     * Dieses Date ist aus technischen Gründen vorhanden. Es gibt mehrere Möglichkeiten, wie diese PIT zu verstehen ist:
     * <ul>
     * <li> Bei einem gelöschten Beitrag steht hier der Zeitpunkt der Lösung drin.</li>
     * <li> Wurde dieser Eintrag durch einen anderen ersetzt, steht hier drin wann das passiert ist.</li>
     * <li> Ist dies ein Eintrag, der einen anderen ersetzt hat dann steht hier <code>null</code>.</li>
     * <li> Wurde der Eintrag nicht geändert, dann steht hier ebenfalls <code>null</code>.</li>
     * <ul>
     *
     * @return
     */
    public Date getEditpit() {
        return editpit;
    }

    public void setEditpit(Date editpit) {
        this.editpit = editpit;
    }

    public String getText() {
        SYSTools.anonymizeText(bewohner.getNachnameNieAnonym(), text);
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getDauer() {
        return dauer;
    }

    public void setDauer(int dauer) {
        this.dauer = dauer;
    }

    public Bewohner getBewohner() {
        return bewohner;
    }

    public void setBewohner(Bewohner bewohner) {
        this.bewohner = bewohner;
    }

    public Collection<Syspb2file> getAttachedFiles() {
        return attachedFiles;
    }

    public void setAttachedFiles(Collection<Syspb2file> attachedFiles) {
        this.attachedFiles = attachedFiles;
    }

    public void setDeletedBy(Users deletedBy) {
        editedBy = deletedBy;
        editpit = new Date();
        replacedBy = null;
        replacementFor = null;
    }

    public boolean isBesonders() {
        boolean found = false;

        Iterator<PBerichtTAGS> it = tags.iterator();
        while (!found && it.hasNext()) {
            found = it.next().isBesonders();
        }
        return found;
    }

    /**
     * Pflegeberichte, die gelöscht oder geändert wurden enthalten als <code>editBy</code> den User, der die Änderung vorgenommen hat.
     * Ein normaler, ungeänderter, ungelöschter Bericht gibt hier <code>null</code> zurück.
     *
     * @return User, der geändert oder gelöscht hat oder null, wenn ungeändert.
     */
    public Users getEditedBy() {
        return editedBy;
    }

    public void setEditedBy(Users editedBy) {
        this.editedBy = editedBy;
    }

    /**
     * Geänderte Berichte treten immer im Doppel auf. In diesem Fall enthält der alte Bericht in <code>replacedBy</code> einen Verweis auf
     * den Bericht, "der ihn ersetzt". Normale Berichte geben hier <code>null</code> zurück.
     *
     * @return
     */
    public Pflegeberichte getReplacedBy() {
        return replacedBy;
    }

    public void setReplacedBy(Pflegeberichte replacedBy) {
        this.replacedBy = replacedBy;
    }

    public boolean isReplaced() {
        return replacedBy != null;
    }

    public boolean isReplacement() {
        return replacementFor != null;
    }

    public boolean isDeleted() {
        return editedBy != null && replacedBy == null && replacementFor == null;
    }

    /**
     * Ein "Ersatzbericht", "weiss" wer er mal war, indem er in <code>replacementFor</code> auf den alten,
     * ersetzten Bericht zeigt. Normale Berichte geben hier <code>null</code> zurück.
     *
     * @return
     */
    public Pflegeberichte getReplacementFor() {
        return replacementFor;
    }

    public void setReplacementFor(Pflegeberichte replacementFor) {
        this.replacementFor = replacementFor;
    }

    public Collection<PBerichtTAGS> getTags() {
        return tags;
    }

//    public void setTags(Collection<PBerichtTAGS> tags) {
//        this.tags = tags;
//    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Collection<SYSPB2VORGANG> getAttachedVorgaenge() {
        return attachedVorgaenge;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pbid != null ? pbid.hashCode() : 0);
        return hash;
    }

    @Override
    public String getContentAsHTML() {
        return PflegeberichteTools.getBerichtAsHTML(this, false);
    }

    @Override
    public String getPITAsHTML() {
        return PflegeberichteTools.getPITAsHTML(this);
    }

    @Override
    public long getID() {
        return pbid;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof Pflegeberichte)) {
            return false;
        }
        Pflegeberichte other = (Pflegeberichte) object;
        if ((this.pbid == null && other.pbid != null) || (this.pbid != null && !this.pbid.equals(other.pbid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Pflegeberichte[pbid=" + pbid + "]";
    }

    @Override
    public long getPITInMillis() {
        return pit.getTime();
    }
}
