/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.values;

import entity.files.SYSVAL2FILE;
import entity.info.Resident;
import entity.process.QProcess;
import entity.process.QProcessElement;
import entity.process.SYSVAL2PROCESS;
import entity.system.Users;
import op.OPDE;
import op.care.values.PnlValues;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;

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
@Table(name = "resvalue")

public class ResValue implements Serializable, QProcessElement, Cloneable, Comparable<ResValue> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BWID")
    private Long id;
    @Version
    @Column(name = "version")
    private Long version;
    @Basic(optional = false)
    @Column(name = "PIT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pit;
    @Basic(optional = false)
    @Column(name = "Wert2")
    private BigDecimal val2;
    @Basic(optional = false)
    @Column(name = "Wert3")
    private BigDecimal val3;
    @Basic(optional = false)
    @Column(name = "Wert")
    private BigDecimal val1;
    @Lob
    @Column(name = "Bemerkung")
    private String text;
    @Basic(optional = false)
    @Column(name = "_cdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    @Basic(optional = false)
    @Column(name = "_mdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date editDate;

    @JoinColumn(name = "editBy", referencedColumnName = "UKennung")
    @ManyToOne
    private Users editedBy;
    @JoinColumn(name = "ReplacedBy", referencedColumnName = "BWID")
    @OneToOne
    private ResValue replacedBy;
    @JoinColumn(name = "ReplacementFor", referencedColumnName = "BWID")
    @OneToOne
    private ResValue replacementFor;
    // ==
    // N:1 Relationen
    // ==
    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;
    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Resident resident;
    @JoinColumn(name = "Type", referencedColumnName = "ID")
    @ManyToOne
    private ResValueTypes vtype;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "value")
    private Collection<SYSVAL2FILE> attachedFilesConnections;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "resValue")
    private Collection<SYSVAL2PROCESS> attachedProcessConnections;

//    // ==
//    // M:N Relationen
//    // ==

    public ResValue() {
    }

    public ResValue(Resident resident, ResValueTypes vtype) {
        this(new Date(), vtype.getDefault1(), vtype.getDefault2(), vtype.getDefault3(), "", new Date(), new Date(), vtype, null, null, null, OPDE.getLogin().getUser(), resident);
    }

    public ResValue(Resident resident, ResValueTypes vtype, Date date) {
        this(date, vtype.getDefault1(), vtype.getDefault2(), vtype.getDefault3(), "", new Date(), new Date(), vtype, null, null, null, OPDE.getLogin().getUser(), resident);
    }

    public ResValue(Date pit, BigDecimal val1, BigDecimal val2, BigDecimal val3, String text, Date createDate, Date editDate, ResValueTypes vtype, Users editedBy, ResValue replacedBy, ResValue replacementFor, Users user, Resident resident) {
        this.pit = pit;
        this.val2 = val2;
        this.val3 = val3;
        this.val1 = val1;
        this.text = text;
        this.createDate = createDate;
        this.editDate = editDate;
        this.vtype = vtype;
        this.editedBy = editedBy;
        this.replacedBy = replacedBy;
        this.replacementFor = replacementFor;
        this.user = user;
        this.resident = resident;
        this.attachedFilesConnections = new ArrayList<SYSVAL2FILE>();
        this.attachedProcessConnections = new ArrayList<SYSVAL2PROCESS>();

    }

    public Long getId() {
        return id;
    }

    public Date getPit() {
        return pit;
    }

    public void setPit(Date pit) {
        this.pit = pit;
    }

    public BigDecimal getVal2() {
        return val2;
    }

    public void setVal2(BigDecimal val2) {
        this.val2 = val2;
    }

    public BigDecimal getVal3() {
        return val3;
    }

    public void setVal3(BigDecimal val3) {
        this.val3 = val3;
    }

    public BigDecimal getVal1() {
        return val1;
    }

    public void setVal1(BigDecimal val1) {
        this.val1 = val1;
    }

    public ResValueTypes getType() {
        return vtype;
    }

    public void setType(ResValueTypes type) {
        this.vtype = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = SYSTools.tidy(text);
    }

    public Users getEditedBy() {
        return editedBy;
    }

    public void setEditedBy(Users editedBy) {
        this.editedBy = editedBy;
    }

    public ResValue getReplacedBy() {
        return replacedBy;
    }

    public void setReplacedBy(ResValue replacedBy) {
        this.replacedBy = replacedBy;
    }

    public ResValue getReplacementFor() {
        return replacementFor;
    }

    public void setReplacementFor(ResValue replacementFor) {
        this.replacementFor = replacementFor;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date cdate) {
        this.createDate = cdate;
    }

    public Date getEditDate() {
        return editDate;
    }

    public void setEditDate(Date mdate) {
        this.editDate = mdate;
    }

    public boolean isWithoutValue() {
        return vtype.getID() == ResValueTypesTools.VOMIT || vtype.getID() == ResValueTypesTools.STOOL;
    }

    @Override
    public ArrayList<QProcess> getAttachedProcesses() {
        ArrayList<QProcess> list = new ArrayList<QProcess>();
        for (SYSVAL2PROCESS att : attachedProcessConnections) {
            list.add(att.getQProcess());
        }
        return list;
    }

    public Collection<SYSVAL2FILE> getAttachedFilesConnections() {
        return attachedFilesConnections;
    }

    public Collection<SYSVAL2PROCESS> getAttachedProcessConnections() {
        return attachedProcessConnections;
    }

    @Override
    public Resident getResident() {
        return resident;
    }

//    public boolean isWrongValues() {
//        if (vtype.getID() == ResValueTools.RR) {
//            return val1 == null || val2 == null || val3 == null;
//        } else {
//            return val1 == null;
//        }
//    }

    /**
     * @return
     */
    public boolean isReplaced() {
        return replacedBy != null;
    }

    public boolean isReplacement() {
        return replacementFor != null;
    }

    public boolean isObsolete() {
        return isReplaced() || isDeleted();
    }

    /**
     * @return
     */
    public boolean isDeleted() {
        return editedBy != null && replacedBy == null && replacementFor == null;
    }

    public void setDeletedBy(Users deletedBy) {
        editedBy = deletedBy;
        createDate = new Date();
        replacedBy = null;
        replacementFor = null;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
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
        return ResValueTools.getAsHTML(this);
    }

    @Override
    public String getPITAsHTML() {
        return ResValueTools.getPITasHTML(this, false, false);
    }

    @Override
    public long getID() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResValue resValue = (ResValue) o;
        return resValue.getID() == id;

    }

    //    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        ResValue resValue = (ResValue) o;
//
//        if (createDate != null ? !createDate.equals(resValue.createDate) : resValue.createDate != null) return false;
//        if (editDate != null ? !editDate.equals(resValue.editDate) : resValue.editDate != null) return false;
////        if (editedBy != null ? !editedBy.equals(resValue.editedBy) : resValue.editedBy != null) return false;
//        if (id != null ? !id.equals(resValue.id) : resValue.id != null) return false;
//        if (pit != null ? !pit.equals(resValue.pit) : resValue.pit != null) return false;
////        if (replacedBy != null ? !replacedBy.equals(resValue.replacedBy) : resValue.replacedBy != null) return false;
////        if (replacementFor != null ? !replacementFor.equals(resValue.replacementFor) : resValue.replacementFor != null)
////            return false;
//        if (resident != null ? !resident.equals(resValue.resident) : resValue.resident != null) return false;
//        if (text != null ? !text.equals(resValue.text) : resValue.text != null) return false;
//        if (user != null ? !user.equals(resValue.user) : resValue.user != null) return false;
//        if (val1 != null ? !val1.equals(resValue.val1) : resValue.val1 != null) return false;
//        if (val2 != null ? !val2.equals(resValue.val2) : resValue.val2 != null) return false;
//        if (val3 != null ? !val3.equals(resValue.val3) : resValue.val3 != null) return false;
//        if (version != null ? !version.equals(resValue.version) : resValue.version != null) return false;
//        if (vtype != null ? !vtype.equals(resValue.vtype) : resValue.vtype != null) return false;
//
//        return true;
//    }
//
//    @Override
//    public int hashCode() {
//        int result = id != null ? id.hashCode() : 0;
//        result = 31 * result + (version != null ? version.hashCode() : 0);
//        result = 31 * result + (pit != null ? pit.hashCode() : 0);
//        result = 31 * result + (val2 != null ? val2.hashCode() : 0);
//        result = 31 * result + (val3 != null ? val3.hashCode() : 0);
//        result = 31 * result + (val1 != null ? val1.hashCode() : 0);
//        result = 31 * result + (text != null ? text.hashCode() : 0);
//        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
//        result = 31 * result + (editDate != null ? editDate.hashCode() : 0);
////        result = 31 * result + (editedBy != null ? editedBy.hashCode() : 0);
////        result = 31 * result + (replacedBy != null ? replacedBy.hashCode() : 0);
////        result = 31 * result + (replacementFor != null ? replacementFor.hashCode() : 0);
//        result = 31 * result + (user != null ? user.hashCode() : 0);
//        result = 31 * result + (resident != null ? resident.hashCode() : 0);
//        result = 31 * result + (vtype != null ? vtype.hashCode() : 0);
//        return result;
//    }

    @Override
    public String getTitle() {
        return SYSTools.xx(PnlValues.internalClassID) + ": " + vtype.getText();
    }

    @Override
    public ResValue clone() {
        final ResValue clonedValue = new ResValue(pit, val1, val2, val3, text, new Date(), new Date(), vtype, null, null, null, OPDE.getLogin().getUser(), resident);
        CollectionUtils.forAllDo(attachedProcessConnections, new Closure() {
            public void execute(Object o) {
                SYSVAL2PROCESS oldAssignment = (SYSVAL2PROCESS) o;
                clonedValue.attachedProcessConnections.add(new SYSVAL2PROCESS(oldAssignment.getQProcess(), clonedValue));
            }
        });

        CollectionUtils.forAllDo(attachedFilesConnections, new Closure() {
            public void execute(Object o) {
                SYSVAL2FILE oldAssignment = (SYSVAL2FILE) o;
                clonedValue.attachedFilesConnections.add(new SYSVAL2FILE(oldAssignment.getSysfile(), clonedValue, clonedValue.getUser(), clonedValue.getPit()));
            }
        });

        return clonedValue;
    }

    @Override
    public int compareTo(ResValue o) {
        return pit.compareTo(o.getPit()) * -1;
    }


    @Override
    public String toString() {
        return "entity.values.ResValue[bwid=" + id + "]";
    }
}
