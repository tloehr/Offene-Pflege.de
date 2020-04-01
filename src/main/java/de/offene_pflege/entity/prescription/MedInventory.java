package de.offene_pflege.entity.prescription;

import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.entity.system.OPUsers;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.SYSConst;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medinventory")
public class MedInventory implements Serializable, Comparable<MedInventory> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VorID")
    private Long id;
    @Version
    @Column(name = "version")
    private Long version;
    @Basic(optional = false)
    @Column(name = "Text")
    private String text;
    @Basic(optional = false)
    @Column(name = "Von")
    private LocalDateTime from;
    @Basic(optional = false)
    @Column(name = "Bis")
    private LocalDateTime to;

    public MedInventory() {


    }

    public Resident getResident() {
        return resident;
    }

    public OPUsers getUser() {
        return user;
    }

    public void setUser(OPUsers user) {
        this.user = user;
    }

    public MedInventory(Resident resident, String text) {
        this.resident = resident;
        this.text = text;
        this.user = OPDE.getLogin().getUser();
        this.from = LocalDateTime.now();
        this.to = SYSConst.LD_UNTIL_FURTHER_NOTICE;
        this.medStocks = new ArrayList<MedStock>();
    }

    public Long getID() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public void setTo(LocalDateTime to) {
        this.to = to;
    }

    // ==
    // 1:N Relations
    // only used for JPQL expressions. Not in code.
    // ==
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "inventory")
    private List<MedStock> medStocks;

    // ==
    // N:1 Relationen
    // ==
    @JoinColumn(name = "BWKennung", referencedColumnName = "id")
    @ManyToOne
    private Resident resident;
    // ==
    // N:1 Relationen
    // ==
    @JoinColumn(name = "UKennung", referencedColumnName = "UKennung")
    @ManyToOne
    private OPUsers user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MedInventory that = (MedInventory) o;

        if (from != null ? !from.equals(that.from) : that.from != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
//        if (medStocks != null ? !medStocks.equals(that.medStocks) : that.medStocks != null) return false;
        if (resident != null ? !resident.equals(that.resident) : that.resident != null) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;
        if (to != null ? !to.equals(that.to) : that.to != null) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (to != null ? to.hashCode() : 0);
//        result = 31 * result + (medStocks != null ? medStocks.hashCode() : 0);
        result = 31 * result + (resident != null ? resident.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "entity.rest.MedInventory[vorID=" + id + "]";
    }

    @Override
    public int compareTo(MedInventory o) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<MedStock> getMedStocks() {
        return medStocks;
    }
}
                                       