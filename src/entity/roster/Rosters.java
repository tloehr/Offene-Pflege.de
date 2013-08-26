package entity.roster;

import entity.system.Users;
import org.joda.time.DateMidnight;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 14.08.13
 * Time: 17:02
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Rosters {
    @Column(name = "id", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    private long id;
    @Column(name = "section", nullable = false, insertable = true, updatable = true, length = 5, precision = 0)
    @Basic
    private short section;
    @Column(name = "month", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Temporal(TemporalType.TIMESTAMP)
    @Basic
    private Date month;
    @Column(name = "flag", nullable = false, insertable = true, updatable = true, length = 5, precision = 0)
    @Basic
    private short flag;
    @Column(name = "xml", nullable = false, insertable = true, updatable = true, length = 16777215, precision = 0)
    @Basic
    private String xml;
    @Column(name = "version", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Version
    private long version;
    /**
     * _       _   _
     * _ __ ___| | __ _| |_(_) ___  _ __  ___
     * | '__/ _ \ |/ _` | __| |/ _ \| '_ \/ __|
     * | | |  __/ | (_| | |_| | (_) | | | \__ \
     * |_|  \___|_|\__,_|\__|_|\___/|_| |_|___/
     */
    @JoinColumn(name = "openedby", referencedColumnName = "UKennung")
    @ManyToOne
    private Users openedBy;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "roster")
    private List<RPlan> shifts;

    public List<RPlan> getShifts() {
        return shifts;
    }

    public Rosters() {
    }

    public Rosters(DateMidnight month1) {
        this.month = month1.dayOfMonth().withMinimumValue().toDate();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public short getSection() {
        return section;
    }

    public void setSection(short section) {
        this.section = section;
    }


    public Date getMonth() {
        return month;
    }

    public void setMonth(Date month) {
        this.month = month;
    }


    public short getFlag() {
        return flag;
    }

    public void setFlag(short flag) {
        this.flag = flag;
    }


    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }


    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Users getOpenedBy() {
        return openedBy;
    }

    public void setOpenedBy(Users owner) {
        this.openedBy = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rosters rosters = (Rosters) o;

        if (id != rosters.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));

        return result;
    }
}
