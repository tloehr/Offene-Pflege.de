/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.offene_pflege.backend.entity.done;


import de.offene_pflege.backend.entity.DefaultEntity;
import de.offene_pflege.backend.entity.system.OPUsers;

import javax.persistence.*;
import java.util.Date;

/**
 * @author tloehr
 */
@Entity
@Table(name = "npcontrol")

public class NPControl extends DefaultEntity implements Comparable<NPControl> {
    private String bemerkung;
    private Date datum;
    private Boolean lastValidation;
    private NursingProcess nursingProcess;
    private OPUsers opUsers;

    @Lob
    @Column(name = "Bemerkung")
    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    @Basic(optional = false)
    @Column(name = "Datum")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    @Column(name = "Abschluss")
    public Boolean getLastValidation() {
        return lastValidation;
    }

    public void setLastValidation(Boolean lastValidation) {
        this.lastValidation = lastValidation;
    }

    @JoinColumn(name = "PlanID", referencedColumnName = "id")
    @ManyToOne
    public NursingProcess getNursingProcess() {
        return nursingProcess;
    }

    public void setNursingProcess(NursingProcess nursingProcess) {
        this.nursingProcess = nursingProcess;
    }

    @JoinColumn(name = "UKennung", referencedColumnName = "id")
    @ManyToOne
    public OPUsers getOpUsers() {
        return opUsers;
    }

    public void setOpUsers(OPUsers opUsers) {
        this.opUsers = opUsers;
    }


    public NPControl() {
    }


    @Override
    public int compareTo(NPControl o) {
        return datum.compareTo(o.getDatum()) * -1;
    }


}
