/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity;

import entity.info.Resident;
import entity.system.Users;
import op.OPDE;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author tloehr
 */
@Entity
@Table(name = "Taschengeld")
@NamedQueries({
        @NamedQuery(name = "Taschengeld.findAll", query = "SELECT t FROM Allowance t"),
        @NamedQuery(name = "Taschengeld.findByTgid", query = "SELECT t FROM Allowance t WHERE t.id = :tgid"),
        @NamedQuery(name = "Taschengeld.findByBelegDatum", query = "SELECT t FROM Allowance t WHERE t.date = :belegDatum"),
        @NamedQuery(name = "Taschengeld.findByBelegtext", query = "SELECT t FROM Allowance t WHERE t.text = :belegtext")})
public class Allowance implements Serializable, Comparable<Allowance> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TGID")
    private Long id;
    @Version
    @Column(name = "version")
    private Long version;
    @Basic(optional = false)
    @Column(name = "BelegDatum")
    @Temporal(TemporalType.DATE)
    private Date date;
    @Basic(optional = false)
    @Column(name = "Belegtext")
    private String text;
    @Basic(optional = false)
    @Column(name = "Betrag")
    private BigDecimal amount;
    @Basic(optional = false)
    @Column(name = "_edate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date editDate;
    @Basic(optional = false)
    @Column(name = "_cdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    public Allowance() {
    }

    public Allowance(Resident resident) {
        this.text = "";
        this.date = new Date();
        this.amount = BigDecimal.ZERO;

        this.resident = resident;
        this.createdBy = OPDE.getLogin().getUser();
        this.createDate = new Date();
    }

    @JoinColumn(name = "BWKennung", referencedColumnName = "BWKennung")
    @ManyToOne
    private Resident resident;
    @JoinColumn(name = "_editor", referencedColumnName = "UKennung")
    @ManyToOne
    private Users editedBy;
    @JoinColumn(name = "_creator", referencedColumnName = "UKennung")
    @ManyToOne
    private Users createdBy;


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Allowance)) {
            return false;
        }
        Allowance other = (Allowance) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getEditDate() {
        return editDate;
    }

    public void setEditDate(Date editDate) {
        this.editDate = editDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public Users getEditedBy() {
        return editedBy;
    }

    public void setEditedBy(Users editedBy) {
        this.editedBy = editedBy;
    }

    public Users getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Users createdBy) {
        this.createdBy = createdBy;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Taschengeld{" +
                "tgid=" + id +
                ", belegDatum=" + date +
                ", belegtext='" + text + '\'' +
                ", betrag=" + amount +
                ", bearbeitetAm=" + editDate +
                ", erstelltAm=" + createDate +
                ", bewohner=" + resident +
                ", bearbeitetVon=" + editedBy +
                ", erstelltVon=" + createdBy +
                '}';
    }

    @Override
    public int compareTo(Allowance other) {
        int result = date.compareTo(other.getDate()) * -1;
        if (result == 0) {
            result = id.compareTo(other.getId()) * -1;
        }
        return result;
    }
}
