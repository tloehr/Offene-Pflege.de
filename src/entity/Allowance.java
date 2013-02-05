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
@Table(name = "allowance")
public class Allowance implements Serializable, Comparable<Allowance> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Version
    @Column(name = "version")
    private Long version;
    @Basic(optional = false)
    @Column(name = "pit")
    @Temporal(TemporalType.DATE)
    private Date pit;
    @Basic(optional = false)
    @Column(name = "text")
    private String text;
    @Basic(optional = false)
    @Column(name = "amount")
    private BigDecimal amount;
    @Basic(optional = false)
    @Column(name = "editpit")
    @Temporal(TemporalType.TIMESTAMP)
    private Date editPit;


    @JoinColumn(name = "editBy", referencedColumnName = "UKennung")
    @ManyToOne
    private Users editedBy;
    @JoinColumn(name = "replacedby", referencedColumnName = "id")
    @OneToOne
    private Allowance replacedBy;
    @JoinColumn(name = "replacementfor", referencedColumnName = "id")
    @OneToOne
    private Allowance replacementFor;

    public Allowance() {
    }

    public Allowance(Resident resident) {
        this.text = "";
        this.pit = new Date();
        this.amount = BigDecimal.ZERO;

        this.resident = resident;
        this.user = OPDE.getLogin().getUser();
        this.replacedBy = null;
        this.replacementFor = null;
        this.editedBy = null;
    }

    public Allowance(Allowance allowanceToBeUndone) {
        this.text = allowanceToBeUndone.getText();
        this.pit = allowanceToBeUndone.getPit();
        this.amount = allowanceToBeUndone.getAmount().negate();

        this.resident = allowanceToBeUndone.getResident();
        this.user = OPDE.getLogin().getUser();
        this.replacedBy = null;
        this.replacementFor = allowanceToBeUndone;
        this.editedBy = null;
    }

    @JoinColumn(name = "resid", referencedColumnName = "BWKennung")
    @ManyToOne
    private Resident resident;
    @JoinColumn(name = "uid", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;


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


    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }


    public Date getPit() {
        return pit;
    }

    public void setPit(Date date) {
        this.pit = date;
    }


    public boolean isReplaced() {
        return replacedBy != null;
    }

    public boolean isReplacement() {
        return replacementFor != null;
    }

    public boolean isUndone() {
        return editedBy != null;
    }

    public Users getUser() {
        return user;
    }

    public Allowance getReplacedBy() {
        return replacedBy;
    }

    public void setReplacedBy(Allowance replacedBy, Users editedBy) {
        this.replacedBy = replacedBy;
        this.editPit = new Date();
        this.editedBy = editedBy;
    }

    public void setReplacedBy(Allowance replacedBy) {
        this.replacedBy = replacedBy;
    }

    public Allowance getReplacementFor() {
        return replacementFor;
    }

    public void setEditPit(Date editPit) {
        this.editPit = editPit;
    }

    public void setEditedBy(Users editedBy) {
        this.editedBy = editedBy;
    }

    public void setReplacementFor(Allowance replacementFor) {
        this.replacementFor = replacementFor;
    }

    public Users getEditedBy() {
        return editedBy;
    }


    public Date getEditPit() {
        return editPit;
    }


    @Override
    public String toString() {
        return "Allowance{" +
                "id=" + id +

                '}';
    }

    @Override
    public int compareTo(Allowance other) {
        int result = pit.compareTo(other.getPit()) * -1;
        if (result == 0) {
            result = id.compareTo(other.getId()) * -1;
        }
        return result;
    }
}
