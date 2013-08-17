package entity.roster;

import javax.persistence.*;
import java.sql.Date;

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
    @Basic
    private Date month;
    @Column(name = "flag", nullable = false, insertable = true, updatable = true, length = 5, precision = 0)
    @Basic
    private short flag;
    @Column(name = "xml", nullable = false, insertable = true, updatable = true, length = 16777215, precision = 0)
    @Basic
    private String xml;
    @Column(name = "openedby", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    private String openedby;
    @Column(name = "version", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Version
    private long version;

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


    public String getOpenedby() {
        return openedby;
    }

    public void setOpenedby(String openedby) {
        this.openedby = openedby;
    }


    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rosters rosters = (Rosters) o;

        if (flag != rosters.flag) return false;
        if (id != rosters.id) return false;
        if (section != rosters.section) return false;
        if (version != rosters.version) return false;
        if (month != null ? !month.equals(rosters.month) : rosters.month != null) return false;
        if (openedby != null ? !openedby.equals(rosters.openedby) : rosters.openedby != null) return false;
        if (xml != null ? !xml.equals(rosters.xml) : rosters.xml != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) section;
        result = 31 * result + (month != null ? month.hashCode() : 0);
        result = 31 * result + (int) flag;
        result = 31 * result + (xml != null ? xml.hashCode() : 0);
        result = 31 * result + (openedby != null ? openedby.hashCode() : 0);
        result = 31 * result + (int) (version ^ (version >>> 32));
        return result;
    }
}
