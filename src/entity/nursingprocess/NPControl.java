/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.nursingprocess;

import entity.system.Users;
import op.OPDE;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author tloehr
 */
@Entity
@Table(name = "npcontrol")

public class NPControl implements Serializable, Comparable<NPControl> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PKonID")
    private Long pKonID;
    @Lob
    @Column(name = "Bemerkung")
    private String bemerkung;
    @Basic(optional = false)
    @Column(name = "Datum")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datum;
    @Column(name = "Abschluss")
    private Boolean lastValidation;

    @JoinColumn(name = "PlanID", referencedColumnName = "PlanID")
    @ManyToOne
    private NursingProcess nursingProcess;

    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;

    public NPControl() {
    }

    public NPControl(String bemerkung, NursingProcess nursingProcess) {
        this.bemerkung = bemerkung;
        this.lastValidation = false;
        this.nursingProcess = nursingProcess;
        this.user = OPDE.getLogin().getUser();
        this.datum = new Date();
    }

    public Long getPKonID() {
        return pKonID;
    }

    public void setPKonID(Long pKonID) {
        this.pKonID = pKonID;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public Boolean isLastValidation() {
        return lastValidation;
    }

    public void setLastValidation(Boolean lastValidation) {
        this.lastValidation = lastValidation;
    }

    public NursingProcess getNursingProcess() {
        return nursingProcess;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pKonID != null ? pKonID.hashCode() : 0);
        return hash;
    }

    @Override
    public int compareTo(NPControl o) {
        return datum.compareTo(o.getDatum()) * -1;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof NPControl)) {
            return false;
        }
        NPControl other = (NPControl) object;
        if ((this.pKonID == null && other.pKonID != null) || (this.pKonID != null && !this.pKonID.equals(other.pKonID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.PlanKontrolle[pKonID=" + pKonID + "]";
    }

}
