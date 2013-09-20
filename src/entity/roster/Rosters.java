package entity.roster;

import entity.system.SYSLogin;
import org.joda.time.LocalDate;

import javax.persistence.*;
import java.util.Date;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @JoinColumn(name = "loginid", referencedColumnName = "loginid")
    @ManyToOne
    private SYSLogin openedBy;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "roster")
//    private List<Rplan> shifts;

//    public List<Rplan> getShifts() {
//        return shifts;
//    }

    public Rosters() {
    }

    public Rosters(LocalDate month1) {
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

    public SYSLogin getOpenedBy() {
        return openedBy;
    }

    public void setOpenedBy(SYSLogin openedBy) {
        this.openedBy = openedBy;
    }

    public boolean isActive() {
        return flag == RostersTools.FLAG_ACTIVE;
    }

    public boolean isClosed() {
        return flag == RostersTools.FLAG_CLOSED;
    }

    public boolean isLocked() {
        return flag == RostersTools.FLAG_LOCKED;
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
