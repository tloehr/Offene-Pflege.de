package entity.staff;

import entity.files.SYSFiles;
import entity.system.Users;
import entity.values.ResValue;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by tloehr on 19.05.14.
 */
@Entity
public class Training2Users {

    private Timestamp signup;
    private boolean attended;

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
    @Column(name = "signup", nullable = true, insertable = true, updatable = true)
    public Timestamp getSignup() {
        return signup;
    }

    public void setSignup(Timestamp signup) {
        this.signup = signup;
    }

    @Basic
    @Column(name = "attended", nullable = false, insertable = true, updatable = true)
    public boolean getAttended() {
        return attended;
    }

    public void setAttended(boolean attended) {
        this.attended = attended;
    }

    @JoinColumn(name = "uid", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;

    @JoinColumn(name = "trid", referencedColumnName = "id")
    @ManyToOne
    private Training training;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Training2Users that = (Training2Users) o;

        if (attended != that.attended) return false;
        if (id != that.id) return false;
        if (signup != null ? !signup.equals(that.signup) : that.signup != null) return false;
        if (training != null ? !training.equals(that.training) : that.training != null) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (signup != null ? signup.hashCode() : 0);
        result = 31 * result + (attended ? 1 : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (training != null ? training.hashCode() : 0);
        return result;
    }
}
