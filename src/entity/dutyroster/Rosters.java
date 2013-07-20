package entity.dutyroster;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Date;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 20.07.13
 * Time: 13:52
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Rosters {
    private long id;
    private String section;
    private Date month;
    private String home;
    private short flag;
    private String xml;

    @javax.persistence.Column(name = "id", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Id
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @javax.persistence.Column(name = "section", nullable = false, insertable = true, updatable = true, length = 50, precision = 0)
    @Basic
    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    @javax.persistence.Column(name = "month", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    public Date getMonth() {
        return month;
    }

    public void setMonth(Date month) {
        this.month = month;
    }

    @javax.persistence.Column(name = "home", nullable = false, insertable = true, updatable = true, length = 15, precision = 0)
    @Basic
    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    @javax.persistence.Column(name = "flag", nullable = false, insertable = true, updatable = true, length = 5, precision = 0)
    @Basic
    public short getFlag() {
        return flag;
    }

    public void setFlag(short flag) {
        this.flag = flag;
    }

    @javax.persistence.Column(name = "xml", nullable = false, insertable = true, updatable = true, length = 16777215, precision = 0)
    @Basic
    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rosters rosters = (Rosters) o;

        if (flag != rosters.flag) return false;
        if (id != rosters.id) return false;
        if (home != null ? !home.equals(rosters.home) : rosters.home != null) return false;
        if (month != null ? !month.equals(rosters.month) : rosters.month != null) return false;
        if (section != null ? !section.equals(rosters.section) : rosters.section != null) return false;
        if (xml != null ? !xml.equals(rosters.xml) : rosters.xml != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (section != null ? section.hashCode() : 0);
        result = 31 * result + (month != null ? month.hashCode() : 0);
        result = 31 * result + (home != null ? home.hashCode() : 0);
        result = 31 * result + (int) flag;
        result = 31 * result + (xml != null ? xml.hashCode() : 0);
        return result;
    }
}
