/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.reports;

import entity.Homes;
import entity.system.Users;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * @author tloehr
 */
@Entity
@Table(name = "Handovers")
//@NamedQueries({
//        @NamedQuery(name = "Uebergabebuch.findAll", query = "SELECT u FROM Handovers u"),
//        @NamedQuery(name = "Uebergabebuch.findByUebid", query = "SELECT u FROM Handovers u WHERE u.hid = :uebid"),
//        @NamedQuery(name = "Uebergabebuch.findByEinrichtungAndDatum", query = " "
//                + " SELECT u, count(ack) FROM Handovers u "
//                + " LEFT JOIN u.usersAcknowledged ack "
//                + " WHERE u.pit >= :von AND u.pit <= :bis AND u.einrichtung = :einrichtung AND ack.user = :user "
//                + " GROUP BY u "
//                + " ORDER BY u.pit DESC "),
//        @NamedQuery(name = "Uebergabebuch.findByPit", query = "SELECT u FROM Handovers u WHERE u.pit = :pit")
//})
//@SqlResultSetMappings({
//        @SqlResultSetMapping(name = "Handovers.findByEinrichtungAndDatumAndAckUserResultMapping", entities =
//        @EntityResult(entityClass = Handovers.class), columns =
//        @ColumnResult(name = "num"))
//})
//@NamedNativeQueries({
//        /**
//         * Diese Query ist eine native Query. Ich habe keinen anderen Weg gefunden auf SubSelects zu JOINen.
//         * Das Ergebnis ist eine Liste aller Einrichtungsbezogenen Ubergabebuch Einträge mit einer
//         * Angabe, ob ein bestimmter User diese bereits zur Kenntnis genommen hat oder nicht.
//         * Durch eine passende SQLResultSetMap ist das Ergebnis ein 2 wertiges Array aus Objekten. Das erste
//         * Objekt ist immer der Uebergabebericht, das zweiter ist ein Long Wert, der das count Ergebnis
//         * des Joins enthält.
//         *
//         */
//        @NamedNativeQuery(name = "Uebergabebuch.findByEinrichtungAndDatumAndAckUser", query = ""
//                + " SELECT u.*, ifnull(u2u.num, 0) num FROM Uebergabebuch u "
//                + " LEFT OUTER JOIN ( SELECT uebid, count(*) num FROM Uebergabe2User WHERE UKennung=? GROUP BY uebid, UKennung) as u2u ON u2u.UEBID = u.UEBID "
//                + " WHERE "
//                + "     u.EID = ? "
//                + "     AND u.PIT >= ? AND u.PIT <= ? "
//                + " GROUP BY u.UEBID "
//                + " ORDER BY u.PIT DESC", resultSetMapping = "Handovers.findByEinrichtungAndDatumAndAckUserResultMapping")
//})
public class Handovers implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HID")
    private Long hid;
    @Basic(optional = false)
    @Column(name = "PIT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pit;
    @Lob
    @Column(name = "Text")
    private String text;
    @JoinColumn(name = "EKennung", referencedColumnName = "EID")
    @ManyToOne
    private Homes einrichtung;
    @JoinColumn(name = "UKennung", referencedColumnName = "UID")
    @ManyToOne
    private Users user;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bericht")
    private Collection<Handover2User> usersAcknowledged;

    public Handovers() {
    }

    public Handovers(Date pit, String text, Homes einrichtung, Users user) {
        this.pit = pit;
        this.text = text;
        this.einrichtung = einrichtung;
        this.user = user;
    }

    public Long getUebid() {
        return hid;
    }

    public void setUebid(Long uebid) {
        this.hid = uebid;
    }

    public Date getPit() {
        return pit;
    }

    public void setPit(Date pit) {
        this.pit = pit;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Homes getEinrichtung() {
        return einrichtung;
    }

    public Users getUser() {
        return user;
    }

    public void setEinrichtung(Homes einrichtung) {
        this.einrichtung = einrichtung;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Collection<Handover2User> getUsersAcknowledged() {
        return usersAcknowledged;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (hid != null ? hid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof Handovers)) {
            return false;
        }
        Handovers other = (Handovers) object;
        if ((this.hid == null && other.hid != null) || (this.hid != null && !this.hid.equals(other.hid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.reports.Handovers[uebid=" + hid + "]";
    }
}
