package entity.qms;

import entity.system.Users;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by tloehr on 28.05.14.
 */
@Entity
@Table(name = "qms")
public class Qms {

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
    @Column(name = "target", nullable = false, insertable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date target;

    public Date getTarget() {
        return target;
    }

    public void setTarget(Date target) {
        this.target = target;
    }

    @Basic
    @Column(name = "actual", nullable = true, insertable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date actual;

    public Date getActual() {
        return actual;
    }

    public void setActual(Date actual) {
        this.actual = actual;
    }

    @Basic
    @Column(name = "state", nullable = true, insertable = true, updatable = true)
    private Short state;

    public Short getState() {
        return state;
    }

    public void setState(Short state) {
        this.state = state;
    }

    @Version
    @Column(name = "version", nullable = false, insertable = true, updatable = true)
    private long version;

    @JoinColumn(name = "uid", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;


    @JoinColumn(name = "qmspid", referencedColumnName = "id")
    @ManyToOne
    private Qmsplan qmsplan;

    @JoinColumn(name = "qmssid", referencedColumnName = "id")
    @ManyToOne
    private Qmssched qmssched;

    public Qms() {
    }

    public Qms(Date target, Qmsplan qmsplan, Qmssched qmssched) {
        this.target = target;
        this.qmsplan = qmsplan;
        this.qmssched = qmssched;
        this.user = null;
        this.actual = null;
        this.state = QmsTools.STATE_OPEN;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Qmsplan getQmsplan() {
        return qmsplan;
    }

    public void setQmsplan(Qmsplan qmsplan) {
        this.qmsplan = qmsplan;
    }

    public Qmssched getQmssched() {
        return qmssched;
    }

    public void setQmssched(Qmssched qmssched) {
        this.qmssched = qmssched;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Qms qms = (Qms) o;

        if (id != qms.id) return false;
        if (version != qms.version) return false;
        if (actual != null ? !actual.equals(qms.actual) : qms.actual != null) return false;
        if (qmsplan != null ? !qmsplan.equals(qms.qmsplan) : qms.qmsplan != null) return false;
        if (qmssched != null ? !qmssched.equals(qms.qmssched) : qms.qmssched != null) return false;
        if (state != null ? !state.equals(qms.state) : qms.state != null) return false;
        if (target != null ? !target.equals(qms.target) : qms.target != null) return false;
        if (user != null ? !user.equals(qms.user) : qms.user != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (target != null ? target.hashCode() : 0);
        result = 31 * result + (actual != null ? actual.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (int) (version ^ (version >>> 32));
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (qmsplan != null ? qmsplan.hashCode() : 0);
        result = 31 * result + (qmssched != null ? qmssched.hashCode() : 0);
        return result;
    }
}
