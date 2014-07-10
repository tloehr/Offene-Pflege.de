package entity.qms;

import entity.Homes;
import entity.Station;
import entity.system.Users;
import op.OPDE;
import org.joda.time.LocalDate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tloehr on 28.05.14.
 */
@Entity
@Table(name = "qmssched")
public class Qmssched {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Column(name = "startingon", nullable = false, insertable = true, updatable = true)
    @Temporal(TemporalType.DATE)
    private Date startingOn;

    public Date getStartingOn() {
        return startingOn;
    }

    public void setStartingOn(Date startingOn) {
        this.startingOn = startingOn;
    }

    @Basic
    @Column(name = "daily", nullable = true, insertable = true, updatable = true)
    private int daily;

    public int getDaily() {
        return daily;
    }

    public void setDaily(int daily) {
        this.daily = daily;
    }

    @Basic
    @Column(name = "weekly", nullable = true, insertable = true, updatable = true)
    private int weekly;

    public int getWeekly() {
        return weekly;
    }

    public void setWeekly(int weekly) {
        this.weekly = weekly;
    }

    @Basic
    @Column(name = "monthly", nullable = true, insertable = true, updatable = true)
    private int monthly;

    public int getMonthly() {
        return monthly;
    }

    public void setMonthly(int monthly) {
        this.monthly = monthly;
    }

    @Basic
    @Column(name = "yearly", nullable = true, insertable = true, updatable = true)
    private int yearly;

    public int getYearly() {
        return yearly;
    }

    public void setYearly(int yearly) {
        this.yearly = yearly;
    }

    @Basic
    @Column(name = "monthinyear", nullable = true, insertable = true, updatable = true)
    private int monthinyear;

    public int getMonthinyear() {
        return monthinyear;
    }

    public void setMonthinyear(int monthinyear) {
        this.monthinyear = monthinyear;
    }

    @Basic
    @Column(name = "dayinmonth", nullable = true, insertable = true, updatable = true)
    private int dayinmonth;

    public int getDayinmonth() {
        return dayinmonth;
    }

    public void setDayinmonth(int dayinmonth) {
        this.dayinmonth = dayinmonth;
    }

    @Basic
    @Column(name = "weekday", nullable = true, insertable = true, updatable = true)
    private int weekday;

    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }

    @Basic
    @Column(name = "workingday", nullable = true, insertable = true, updatable = true)
    private int workingday;

    public int getWorkingday() {
        return workingday;
    }

    public void setWorkingday(int workingday) {
        this.workingday = workingday;
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

    @Basic
    @Column(name = "state", nullable = false, insertable = true, updatable = true)
    private byte state;

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }


    @Basic
    @Column(name = "dueDays", nullable = true, insertable = true, updatable = true)
    private int duedays;

    public int getDuedays() {
        return duedays;
    }

    public void setDuedays(int duedays) {
        this.duedays = duedays;
    }

    @JoinColumn(name = "station", referencedColumnName = "StatID")
    @ManyToOne
    private Station station;

    @JoinColumn(name = "home", referencedColumnName = "EID")
    @ManyToOne
    private Homes home;


    @JoinColumn(name = "qmspid", referencedColumnName = "id")
    @ManyToOne
    private Qmsplan qmsplan;

    @JoinColumn(name = "uid", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "qmssched", orphanRemoval = true)
    private List<Qms> qmsList;

    public List<Qms> getQmsList() {
        return qmsList;
    }

    public Qmsplan getQmsplan() {
        return qmsplan;
    }

    public void setQmsplan(Qmsplan qmsplan) {
        this.qmsplan = qmsplan;
    }

    public Qmssched() {
    }

    public Qmssched(Qmsplan qmsplan) {
        this.qmsplan = qmsplan;

        this.startingOn = new LocalDate().toDate();

        this.measure = "";

        this.daily = 0;
        this.weekly = 0;
        this.monthly = 0;
        this.yearly = 0;

        this.dayinmonth = 0;
        this.monthinyear = 0;
        this.weekday = 0;

        this.home = null;
        this.station = null;

        this.text = null;

        this.qmsList = new ArrayList<>();
        this.user = OPDE.getLogin().getUser();
        this.state = QmsschedTools.STATE_ACTIVE;

        this.duedays = 3;

    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public Homes getHome() {
        return home;
    }

    public void setHome(Homes home) {
        this.home = home;
    }

    public boolean isDaily() {
        return daily > 0;
    }

    public boolean isWeekly() {
        return weekly > 0;
    }

    public boolean isMonthly() {
        return monthly > 0;
    }

    public boolean isYearly() {
        return yearly > 0;
    }

    public boolean isActive() {
        return qmsplan.isActive() && state == QmsschedTools.STATE_ACTIVE;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Qmssched qmssched = (Qmssched) o;

        if (daily != qmssched.daily) return false;
        if (dayinmonth != qmssched.dayinmonth) return false;
        if (id != qmssched.id) return false;
        if (monthinyear != qmssched.monthinyear) return false;
        if (monthly != qmssched.monthly) return false;
        if (version != qmssched.version) return false;
        if (weekday != qmssched.weekday) return false;
        if (weekly != qmssched.weekly) return false;
        if (workingday != qmssched.workingday) return false;
        if (yearly != qmssched.yearly) return false;
        if (home != null ? !home.equals(qmssched.home) : qmssched.home != null) return false;
        if (measure != null ? !measure.equals(qmssched.measure) : qmssched.measure != null) return false;
//        if (qmsList != null ? !qmsList.equals(qmssched.qmsList) : qmssched.qmsList != null) return false;
//        if (qmsplan != null ? !qmsplan.equals(qmssched.qmsplan) : qmssched.qmsplan != null) return false;
        if (startingOn != null ? !startingOn.equals(qmssched.startingOn) : qmssched.startingOn != null) return false;
        if (station != null ? !station.equals(qmssched.station) : qmssched.station != null) return false;
        if (text != null ? !text.equals(qmssched.text) : qmssched.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (measure != null ? measure.hashCode() : 0);
        result = 31 * result + (startingOn != null ? startingOn.hashCode() : 0);
        result = 31 * result + daily;
        result = 31 * result + weekly;
        result = 31 * result + monthly;
        result = 31 * result + yearly;
        result = 31 * result + monthinyear;
        result = 31 * result + dayinmonth;
        result = 31 * result + weekday;
        result = 31 * result + workingday;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (int) (version ^ (version >>> 32));
        result = 31 * result + (station != null ? station.hashCode() : 0);
        result = 31 * result + (home != null ? home.hashCode() : 0);
//        result = 31 * result + (qmsplan != null ? qmsplan.hashCode() : 0);
//        result = 31 * result + (qmsList != null ? qmsList.hashCode() : 0);
        return result;
    }
}
