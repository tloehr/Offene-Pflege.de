package entity.system;

import entity.qms.Qmsplan;
import entity.staff.Training;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by tloehr on 28.05.14.
 */
@Entity
@Table(name = "commontags")
public class Commontags {

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
    @Column(name = "text", nullable = false, insertable = true, updatable = true, length = 100, unique = true)
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Basic
    @Column(name = "active", nullable = false, insertable = true, updatable = true)
    private boolean active;

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @ManyToMany(mappedBy = "commontags")
    private Collection<Training> trainings;

    @ManyToMany(mappedBy = "commontags")
    private Collection<Qmsplan> qmsplans;

    public Commontags() {
    }

    public Commontags(String text) {
        this.text = text;
        this.active = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Commontags that = (Commontags) o;

        if (active != that.active) return false;
        if (id != that.id) return false;
        if (qmsplans != null ? !qmsplans.equals(that.qmsplans) : that.qmsplans != null) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;
        if (trainings != null ? !trainings.equals(that.trainings) : that.trainings != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (active ? 1 : 0);
        result = 31 * result + (trainings != null ? trainings.hashCode() : 0);
        result = 31 * result + (qmsplans != null ? qmsplans.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return text;
    }
}
