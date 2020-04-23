package de.offene_pflege.backend.entity.done;

import de.offene_pflege.backend.entity.DefaultEntity;
import de.offene_pflege.backend.entity.system.OPUsers;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "medinventory")
public class MedInventory extends DefaultEntity implements Comparable<MedInventory> {
    private String text;
    private LocalDateTime from;
    private LocalDateTime to;
    private List<MedStock> medStocks;
    private Resident resident;
    private OPUsers user;

    @Basic(optional = false)
    @Column(name = "Text")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Basic(optional = false)
    @Column(name = "Von")
    public LocalDateTime getFrom() {
        return from;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    @Basic(optional = false)
    @Column(name = "Bis")
    public LocalDateTime getTo() {
        return to;
    }

    public void setTo(LocalDateTime to) {
        this.to = to;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "inventory")
    public List<MedStock> getMedStocks() {
        return medStocks;
    }

    public void setMedStocks(List<MedStock> medStocks) {
        this.medStocks = medStocks;
    }

    @JoinColumn(name = "BWKennung", referencedColumnName = "id")
    @ManyToOne
    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    @JoinColumn(name = "UKennung", referencedColumnName = "id")
    @ManyToOne
    public OPUsers getUser() {
        return user;
    }

    public void setUser(OPUsers user) {
        this.user = user;
    }

    public MedInventory() {
    }

    @Override
    public int compareTo(MedInventory o) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }


}

