package entity.qms;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by tloehr on 28.05.14.
 */
@Entity
@Table(name = "qmssched")
public class Qmssched {


    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "measure", nullable = false, insertable = true, updatable = true, length = 400)
    private String measure;

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }


    @Basic
    @Column(name = "time", nullable = true, insertable = true, updatable = true)
    @Temporal(TemporalType.TIME)
    private Date time;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Basic
    @Column(name = "daily", nullable = true, insertable = true, updatable = true)
    private Byte daily;

    public Byte getDaily() {
        return daily;
    }

    public void setDaily(Byte daily) {
        this.daily = daily;
    }

    @Basic
    @Column(name = "weekly", nullable = true, insertable = true, updatable = true)
    private Byte weekly;

    public Byte getWeekly() {
        return weekly;
    }

    public void setWeekly(Byte weekly) {
        this.weekly = weekly;
    }

    @Basic
    @Column(name = "monthly", nullable = true, insertable = true, updatable = true)
    private Byte monthly;

    public Byte getMonthly() {
        return monthly;
    }

    public void setMonthly(Byte monthly) {
        this.monthly = monthly;
    }

    @Basic
    @Column(name = "daynum", nullable = true, insertable = true, updatable = true)
    private Byte daynum;

    public Byte getDaynum() {
        return daynum;
    }

    public void setDaynum(Byte daynum) {
        this.daynum = daynum;
    }

    @Basic
    @Column(name = "mon", nullable = true, insertable = true, updatable = true)
    private Byte mon;

    public Byte getMon() {
        return mon;
    }

    public void setMon(Byte mon) {
        this.mon = mon;
    }

    @Basic
    @Column(name = "tue", nullable = true, insertable = true, updatable = true)
    private Byte tue;

    public Byte getTue() {
        return tue;
    }

    public void setTue(Byte tue) {
        this.tue = tue;
    }

    @Basic
    @Column(name = "wed", nullable = true, insertable = true, updatable = true)
    private Byte wed;

    public Byte getWed() {
        return wed;
    }

    public void setWed(Byte wed) {
        this.wed = wed;
    }

    @Basic
    @Column(name = "thu", nullable = true, insertable = true, updatable = true)
    private Byte thu;

    public Byte getThu() {
        return thu;
    }

    public void setThu(Byte thu) {
        this.thu = thu;
    }

    @Basic
    @Column(name = "fri", nullable = true, insertable = true, updatable = true)
    private Byte fri;

    public Byte getFri() {
        return fri;
    }

    public void setFri(Byte fri) {
        this.fri = fri;
    }

    @Basic
    @Column(name = "sat", nullable = true, insertable = true, updatable = true)
    private Byte sat;

    public Byte getSat() {
        return sat;
    }

    public void setSat(Byte sat) {
        this.sat = sat;
    }

    @Basic
    @Column(name = "sun", nullable = true, insertable = true, updatable = true)
    private Byte sun;

    public Byte getSun() {
        return sun;
    }

    public void setSun(Byte sun) {
        this.sun = sun;
    }

    @Basic
    @Column(name = "LDate", nullable = false, insertable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lDate;

    public Date getlDate() {
        return lDate;
    }

    public void setlDate(Date lDate) {
        this.lDate = lDate;
    }


    @Basic
    @Column(name = "text", nullable = true, insertable = true, updatable = true, length = 16777215)
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Version
    @Column(name = "version", nullable = false, insertable = true, updatable = true)
    private long version;

    @JoinColumn(name = "qmspid", referencedColumnName = "id")
    @ManyToOne
    private Qmsplan qmsplan;


    public Qmsplan getQmsplan() {
        return qmsplan;
    }

    public void setQmsplan(Qmsplan qmsplan) {
        this.qmsplan = qmsplan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Qmssched qmssched = (Qmssched) o;

        if (id != qmssched.id) return false;
        if (version != qmssched.version) return false;
        if (daily != null ? !daily.equals(qmssched.daily) : qmssched.daily != null) return false;
        if (daynum != null ? !daynum.equals(qmssched.daynum) : qmssched.daynum != null) return false;
        if (fri != null ? !fri.equals(qmssched.fri) : qmssched.fri != null) return false;
        if (lDate != null ? !lDate.equals(qmssched.lDate) : qmssched.lDate != null) return false;
        if (measure != null ? !measure.equals(qmssched.measure) : qmssched.measure != null) return false;
        if (mon != null ? !mon.equals(qmssched.mon) : qmssched.mon != null) return false;
        if (monthly != null ? !monthly.equals(qmssched.monthly) : qmssched.monthly != null) return false;
        if (qmsplan != null ? !qmsplan.equals(qmssched.qmsplan) : qmssched.qmsplan != null) return false;
        if (sat != null ? !sat.equals(qmssched.sat) : qmssched.sat != null) return false;
        if (sun != null ? !sun.equals(qmssched.sun) : qmssched.sun != null) return false;
        if (text != null ? !text.equals(qmssched.text) : qmssched.text != null) return false;
        if (thu != null ? !thu.equals(qmssched.thu) : qmssched.thu != null) return false;
        if (time != null ? !time.equals(qmssched.time) : qmssched.time != null) return false;
        if (tue != null ? !tue.equals(qmssched.tue) : qmssched.tue != null) return false;
        if (wed != null ? !wed.equals(qmssched.wed) : qmssched.wed != null) return false;
        if (weekly != null ? !weekly.equals(qmssched.weekly) : qmssched.weekly != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (measure != null ? measure.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (daily != null ? daily.hashCode() : 0);
        result = 31 * result + (weekly != null ? weekly.hashCode() : 0);
        result = 31 * result + (monthly != null ? monthly.hashCode() : 0);
        result = 31 * result + (daynum != null ? daynum.hashCode() : 0);
        result = 31 * result + (mon != null ? mon.hashCode() : 0);
        result = 31 * result + (tue != null ? tue.hashCode() : 0);
        result = 31 * result + (wed != null ? wed.hashCode() : 0);
        result = 31 * result + (thu != null ? thu.hashCode() : 0);
        result = 31 * result + (fri != null ? fri.hashCode() : 0);
        result = 31 * result + (sat != null ? sat.hashCode() : 0);
        result = 31 * result + (sun != null ? sun.hashCode() : 0);
        result = 31 * result + (lDate != null ? lDate.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (int) (version ^ (version >>> 32));
        result = 31 * result + (qmsplan != null ? qmsplan.hashCode() : 0);
        return result;
    }
}
