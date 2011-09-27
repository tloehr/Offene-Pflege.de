/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;

/**
 *
 * @author tloehr
 */
@Entity
@Table(name = "Uebergabebuch")
@NamedQueries({
    @NamedQuery(name = "Uebergabebuch.findAll", query = "SELECT u FROM Uebergabebuch u"),
    @NamedQuery(name = "Uebergabebuch.findByUebid", query = "SELECT u FROM Uebergabebuch u WHERE u.uebid = :uebid"),
    @NamedQuery(name = "Uebergabebuch.findByEinrichtungAndDatum", query = " "
    + " SELECT u, count(ack) FROM Uebergabebuch u "
    + " LEFT JOIN u.usersAcknowledged ack "
    + " WHERE u.pit >= :von AND u.pit <= :bis AND u.einrichtung = :einrichtung AND ack.user = :user "
    + " GROUP BY u "
    + " ORDER BY u.pit DESC "),
    @NamedQuery(name = "Uebergabebuch.findByPit", query = "SELECT u FROM Uebergabebuch u WHERE u.pit = :pit")
})
@SqlResultSetMappings({
    @SqlResultSetMapping(name = "Uebergabebuch.findByEinrichtungAndDatumAndAckUserResultMapping", entities =
    @EntityResult(entityClass = Uebergabebuch.class), columns =
    @ColumnResult(name = "num"))
})
@NamedNativeQueries({
    /**
     * Diese Query ist eine native Query. Ich habe keinen anderen Weg gefunden auf SubSelects zu JOINen.
     * Das Ergebnis ist eine Liste aller Einrichtungsbezogenen Ubergabebuch Einträge mit einer
     * Angabe, ob ein bestimmter User diese bereits zur Kenntnis genommen hat oder nicht.
     * Durch eine passende SQLResultSetMap ist das Ergebnis ein 2 wertiges Array aus Objekten. Das erste
     * Objekt ist immer der Uebergabebericht, das zweiter ist ein Long Wert, der das count Ergebnis
     * des Joins enthält.
     * 
     */
    @NamedNativeQuery(name = "Uebergabebuch.findByEinrichtungAndDatumAndAckUser", query = ""
    + " SELECT u.*, ifnull(u2u.num, 0) num FROM Uebergabebuch u "
    + " LEFT OUTER JOIN ( SELECT uebid, count(*) num FROM Uebergabe2User WHERE UKennung=? GROUP BY uebid, UKennung) as u2u ON u2u.UEBID = u.UEBID "
    + " WHERE "
    + "     u.EKennung = ? "
    + "     AND u.PIT >= ? AND u.PIT <= ? "
    + " GROUP BY u.UEBID "
    + " ORDER BY u.PIT DESC", resultSetMapping = "Uebergabebuch.findByEinrichtungAndDatumAndAckUserResultMapping")
})
public class Uebergabebuch implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "UEBID")
    private Long uebid;
    @Basic(optional = false)
    @Column(name = "PIT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pit;
    @Lob
    @Column(name = "Text")
    private String text;
    @JoinColumn(name = "EKennung", referencedColumnName = "EKennung")
    @ManyToOne
    private Einrichtungen einrichtung;
    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bericht")
    private Collection<Uebergabe2User> usersAcknowledged;

    public Uebergabebuch() {
    }

    public Uebergabebuch(Date pit, String text, Einrichtungen einrichtung, Users user) {
        this.pit = pit;
        this.text = text;
        this.einrichtung = einrichtung;
        this.user = user;
    }
    
    public Long getUebid() {
        return uebid;
    }

    public void setUebid(Long uebid) {
        this.uebid = uebid;
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

    public Einrichtungen getEinrichtung() {
        return einrichtung;
    }

    public Users getUser() {
        return user;
    }

    public void setEinrichtung(Einrichtungen einrichtung) {
        this.einrichtung = einrichtung;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Collection<Uebergabe2User> getUsersAcknowledged() {
        return usersAcknowledged;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uebid != null ? uebid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Uebergabebuch)) {
            return false;
        }
        Uebergabebuch other = (Uebergabebuch) object;
        if ((this.uebid == null && other.uebid != null) || (this.uebid != null && !this.uebid.equals(other.uebid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Uebergabebuch[uebid=" + uebid + "]";
    }
}
