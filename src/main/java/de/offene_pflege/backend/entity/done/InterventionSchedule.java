/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.offene_pflege.backend.entity.done;

import de.offene_pflege.backend.entity.DefaultEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author tloehr
 */
@Entity
@Table(name = "ischedule")

public class InterventionSchedule extends DefaultEntity implements Comparable<InterventionSchedule> {
    private Short nachtMo;
    private Short morgens;
    private Short mittags;
    private Short nachmittags;
    private Short abends;
    private Short nachtAb;
    private Short uhrzeitAnzahl;
    private Date uhrzeit;
    private Short taeglich;
    private Short woechentlich;
    private Short monatlich;
    private Short tagNum;
    private Short mon;
    private Short die;
    private Short mit;
    private Short don;
    private Short fre;
    private Short sam;
    private Short son;
    private Boolean erforderlich;
    private Date lDatum;
    private String bemerkung;
    private NursingProcess nursingProcess;
    private Intervention intervention;
    private String uuid; // nur zum Vergleich, damit ich nicht persistierte Elemente auseinander halten kann. Das kommt vor, bei Änderungen in den Pflegeplanungen.

    @Column(name = "NachtMo")
    public Short getNachtMo() {
        return nachtMo;
    }

    public void setNachtMo(Short nachtMo) {
        this.nachtMo = nachtMo;
    }

    @Column(name = "Morgens")
    public Short getMorgens() {
        return morgens;
    }

    public void setMorgens(Short morgens) {
        this.morgens = morgens;
    }

    @Column(name = "Mittags")
    public Short getMittags() {
        return mittags;
    }

    public void setMittags(Short mittags) {
        this.mittags = mittags;
    }

    @Column(name = "Nachmittags")
    public Short getNachmittags() {
        return nachmittags;
    }

    public void setNachmittags(Short nachmittags) {
        this.nachmittags = nachmittags;
    }

    @Column(name = "Abends")
    public Short getAbends() {
        return abends;
    }

    public void setAbends(Short abends) {
        this.abends = abends;
    }

    @Column(name = "NachtAb")
    public Short getNachtAb() {
        return nachtAb;
    }

    public void setNachtAb(Short nachtAb) {
        this.nachtAb = nachtAb;
    }

    @Column(name = "Taeglich")
    public Short getTaeglich() {
        return taeglich;
    }

    public void setTaeglich(Short taeglich) {
        this.taeglich = taeglich;
    }

    @Column(name = "Monatlich")
    public Short getMonatlich() {
        return monatlich;
    }

    public void setMonatlich(Short monatlich) {
        this.monatlich = monatlich;
    }

    @Column(name = "TagNum")
    public Short getTagNum() {
        return tagNum;
    }

    public void setTagNum(Short tagNum) {
        this.tagNum = tagNum;
    }

    @Column(name = "Mon")
    public Short getMon() {
        return mon;
    }

    public void setMon(Short mon) {
        this.mon = mon;
    }

    @Column(name = "Die")
    public Short getDie() {
        return die;
    }

    public void setDie(Short die) {
        this.die = die;
    }

    @Column(name = "Mit")
    public Short getMit() {
        return mit;
    }

    public void setMit(Short mit) {
        this.mit = mit;
    }

    @Column(name = "Don")
    public Short getDon() {
        return don;
    }

    public void setDon(Short don) {
        this.don = don;
    }

    @Column(name = "Fre")
    public Short getFre() {
        return fre;
    }

    public void setFre(Short fre) {
        this.fre = fre;
    }

    @Column(name = "Sam")
    public Short getSam() {
        return sam;
    }

    public void setSam(Short sam) {
        this.sam = sam;
    }

    @Column(name = "Son")
    public Short getSon() {
        return son;
    }

    public void setSon(Short son) {
        this.son = son;
    }

    @Column(name = "UhrzeitAnzahl")
    public Short getUhrzeitAnzahl() {
        return uhrzeitAnzahl;
    }

    public void setUhrzeitAnzahl(Short uhrzeitAnzahl) {
        this.uhrzeitAnzahl = uhrzeitAnzahl;
    }

    @Column(name = "Uhrzeit")
    public Date getUhrzeit() {
        return uhrzeit;
    }

    public void setUhrzeit(Date uhrzeit) {
        this.uhrzeit = uhrzeit;
    }

    @Column(name = "Woechentlich")
    public Short getWoechentlich() {
        return woechentlich;
    }

    public void setWoechentlich(Short woechentlich) {
        this.woechentlich = woechentlich;
    }

    @Column(name = "Erforderlich")
    public Boolean getErforderlich() {
        return erforderlich;
    }

    public void setErforderlich(Boolean erforderlich) {
        this.erforderlich = erforderlich;
    }

    @Basic(optional = false)
    @Column(name = "LDatum")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getlDatum() {
        return lDatum;
    }

    public void setlDatum(Date lDatum) {
        this.lDatum = lDatum;
    }

    @Basic(optional = false)
    @Lob
    @Column(name = "Bemerkung")
    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    @JoinColumn(name = "PlanID", referencedColumnName = "id")
    @ManyToOne
    public NursingProcess getNursingProcess() {
        return nursingProcess;
    }

    public void setNursingProcess(NursingProcess nursingProcess) {
        this.nursingProcess = nursingProcess;
    }

    @JoinColumn(name = "MassID", referencedColumnName = "id")
    @ManyToOne
    public Intervention getIntervention() {
        return intervention;
    }

    public void setIntervention(Intervention intervention) {
        this.intervention = intervention;
    }

    @Transient
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public InterventionSchedule() {
    }

    @Override
    public int compareTo(InterventionSchedule interventionSchedule) {
        return intervention.getBezeichnung().compareTo(interventionSchedule.getIntervention().getBezeichnung());
    }

    @Override
    public boolean equals(Object other) {
        boolean equal = super.equals(other);
        if (equal && getId() == null) { // nur für den fall der nicht presistierten Objekte
            equal = uuid.equals(((InterventionSchedule) other).getUuid());
        }
        return equal;
    }


}
