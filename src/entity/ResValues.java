/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import entity.info.Resident;
import entity.process.QProcess;
import entity.process.SYSVAL2PROCESS;
import entity.process.QProcessElement;
import entity.system.Users;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * @author tloehr
 */
@Entity
@Table(name = "BWerte")
@NamedQueries({
        @NamedQuery(name = "BWerte.findAll", query = "SELECT b FROM ResValues b"),
        /**
         * Sucht Berichte für einen Bewohner mit bestimmten Markierungen
         */
        @NamedQuery(name = "BWerte.findByVorgang", query = " "
                + " SELECT b FROM ResValues b "
                + " JOIN b.attachedVorgaenge av"
                + " JOIN av.vorgang v"
                + " WHERE v = :process "),
        @NamedQuery(name = "BWerte.findByBwid", query = "SELECT b FROM ResValues b WHERE b.bwid = :bwid"),
        @NamedQuery(name = "BWerte.findByPit", query = "SELECT b FROM ResValues b WHERE b.pit = :pit"),
        @NamedQuery(name = "BWerte.findByWert", query = "SELECT b FROM ResValues b WHERE b.wert = :wert"),
        @NamedQuery(name = "BWerte.findByReplacedBy", query = "SELECT b FROM ResValues b WHERE b.replacedBy = :replacedBy"),
        @NamedQuery(name = "BWerte.findByReplacementFor", query = "SELECT b FROM ResValues b WHERE b.replacementFor = :replacementFor"),
        @NamedQuery(name = "BWerte.findByCdate", query = "SELECT b FROM ResValues b WHERE b.cdate = :cdate"),
        @NamedQuery(name = "BWerte.findByMdate", query = "SELECT b FROM ResValues b WHERE b.mdate = :mdate")})
public class ResValues implements Serializable, QProcessElement, Cloneable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BWID")
    private Long bwid;
    @Version
    @Column(name = "version")
    private Long version;
    @Basic(optional = false)
    @Column(name = "PIT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pit;
    @Basic(optional = false)
    @Column(name = "Wert2")
    private BigDecimal wert2;
    @Basic(optional = false)
    @Column(name = "Wert3")
    private BigDecimal wert3;
    @Basic(optional = false)
    @Column(name = "Wert")
    private BigDecimal wert;
    @Lob
    @Column(name = "Bemerkung")
    private String bemerkung;
    @Basic(optional = false)
    @Column(name = "_cdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date cdate;
    @Basic(optional = false)
    @Column(name = "_mdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date mdate;
    @Basic(optional = false)
    @Column(name = "Type")
    private Integer type;
    // ==
    // 1:1 Relationen
    // ==
    @JoinColumn(name = "editBy", referencedColumnName = "UKennung")
    @ManyToOne
    private Users editedBy;
    @JoinColumn(name = "ReplacedBy", referencedColumnName = "BWID")
    @OneToOne
    private ResValues replacedBy;
    @JoinColumn(name = "ReplacementFor", referencedColumnName = "BWID")
    @OneToOne
    private ResValues replacementFor;
    // ==
    // N:1 Relationen
    // ==
    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;
    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Resident bewohner;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "wert")
//    private Collection<Sysbwerte2file> attachedFiles;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bwerte")
    private Collection<SYSVAL2PROCESS> attachedVorgaenge;

//    // ==
//    // M:N Relationen
//    // ==
//    @ManyToMany
//    @JoinTable(name = "SYSBWERTE2VORGANG", joinColumns =
//    @JoinColumn(name = "BWID"), inverseJoinColumns =
//    @JoinColumn(name = "VorgangID"))
//    private Collection<QProcess> vorgaenge;

    public ResValues() {
    }

    public ResValues(Resident bewohner, Users user) {
        this(new Date(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "", new Date(), new Date(), ResValuesTools.UNKNOWN, null, null, null, user, bewohner);
    }

    public ResValues(Date pit, BigDecimal wert2, BigDecimal wert3, BigDecimal wert, String bemerkung, Date cdate, Date mdate, Integer type, Users editedBy, ResValues replacedBy, ResValues replacementFor, Users user, Resident bewohner) {
        this.pit = pit;
        this.wert2 = wert2;
        this.wert3 = wert3;
        this.wert = wert;
        this.bemerkung = bemerkung;
        this.cdate = cdate;
        this.mdate = mdate;
        this.type = type;
        this.editedBy = editedBy;
        this.replacedBy = replacedBy;
        this.replacementFor = replacementFor;
        this.user = user;
        this.bewohner = bewohner;
    }

    public Long getBwid() {
        return bwid;
    }

    public void setBwid(Long bwid) {
        this.bwid = bwid;
    }

    public Date getPit() {
        return pit;
    }

    public void setPit(Date pit) {
        this.pit = pit;
    }

    public BigDecimal getWert() {
        return wert;
    }

    public void setWert(BigDecimal wert) {
        this.wert = wert;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public Users getEditedBy() {
        return editedBy;
    }

    public void setEditedBy(Users editedBy) {
        this.editedBy = editedBy;
    }

    public ResValues getReplacedBy() {
        return replacedBy;
    }

    public void setReplacedBy(ResValues replacedBy) {
        this.replacedBy = replacedBy;
    }

    public ResValues getReplacementFor() {
        return replacementFor;
    }

    public void setReplacementFor(ResValues replacementFor) {
        this.replacementFor = replacementFor;
    }

    public Date getCdate() {
        return cdate;
    }

    public void setCdate(Date cdate) {
        this.cdate = cdate;
    }

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    public boolean isOhneWert(){
        return type == ResValuesTools.ERBRECHEN || type == ResValuesTools.STUHLGANG;
    }

    public Collection<SYSVAL2PROCESS> getAttachedVorgaenge() {
        return attachedVorgaenge;
    }

    @Override
    public ArrayList<QProcess> getAttachedProcesses(){
        ArrayList<QProcess> list = new ArrayList<QProcess>();
        for (SYSVAL2PROCESS att : attachedVorgaenge){
            list.add(att.getVorgang());
        }
        return list;
    }

    @Override
    public Resident getResident() {
        return bewohner;
    }

    public boolean isWrongValues(){
        if (type == ResValuesTools.RR){
            return wert == null || wert2 == null || wert3 == null;
        } else {
            return wert == null;
        }
    }

    /**
     * @return
     */
    public boolean isReplaced() {
        return replacedBy != null;
    }

    public boolean isReplacement() {
        return replacementFor != null;
    }

    /**
     * @return
     */
    public boolean isDeleted() {
        return editedBy != null && replacedBy == null && replacementFor == null;
    }

    public void setDeletedBy(Users deletedBy) {
        editedBy = deletedBy;
        cdate = new Date();
        replacedBy = null;
        replacementFor = null;
    }

    public void setBewohner(Resident bewohner) {
        this.bewohner = bewohner;
    }

    public BigDecimal getWert2() {
        return wert2;
    }

    public void setWert2(BigDecimal wert2) {
        this.wert2 = wert2;
    }

    public BigDecimal getWert3() {
        return wert3;
    }

    public void setWert3(BigDecimal wert3) {
        this.wert3 = wert3;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    @Override
    public long getPITInMillis() {
        return pit.getTime();
    }

    @Override
    public String getContentAsHTML() {
        return ResValuesTools.getAsHTML(this, false);
    }

    @Override
    public String getPITAsHTML() {
        return ResValuesTools.getPITasHTML(this, false, false);
    }

    @Override
    public long getID() {
        return bwid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bwid != null ? bwid.hashCode() : 0);
        return hash;
    }

    @Override
    public String getTitle() {
        return ResValuesTools.getTitle(this);
    }

    @Override
    public ResValues clone() {
        return new ResValues(pit, wert2, wert3, wert, bemerkung, new Date(), new Date(), type, editedBy, replacedBy, replacementFor, user, bewohner);
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof ResValues)) {
            return false;
        }
        ResValues other = (ResValues) object;
        if ((this.bwid == null && other.bwid != null) || (this.bwid != null && !this.bwid.equals(other.bwid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ResValues[bwid=" + bwid + "]";
    }
}
