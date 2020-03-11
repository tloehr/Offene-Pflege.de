package de.offene_pflege.entity;

import de.offene_pflege.entity.building.Homes;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "qdvs_laufend")
public class QdvsLaufend extends DefaultEntity {
    private long id;
    private Short lfd;
    private long version;
    private Date stichtag;
    private Homes home;
    private short status;

    @Basic
    @Column(name = "lfd", nullable = false)
    public Short getLfd() {
        return lfd;
    }

    public void setLfd(Short lfd) {
        this.lfd = lfd;
    }

    @Basic
    @Column(name = "stichtag", nullable = false)
    public Date getStichtag() {
        return stichtag;
    }

    public void setStichtag(Date stichtag) {
        this.stichtag = stichtag;
    }

    @JoinColumn(name = "homeid", referencedColumnName = "id", nullable = false)
    @ManyToOne
    public Homes getHome() {
        return home;
    }

    public void setHome(Homes home) {
        this.home = home;
    }

    @Basic
    @Column(name = "status", nullable = false)
    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }


}
