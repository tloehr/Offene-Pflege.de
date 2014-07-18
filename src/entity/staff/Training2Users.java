package entity.staff;

import entity.system.Users;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by tloehr on 17.07.14.
 */
@Entity
@Table(name = "training2users")
public class Training2Users {

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
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "pit", nullable = false, insertable = true, updatable = true)
    private Date pit;

    public Date getPit() {
        return pit;
    }

    public void setPit(Date pit) {
        this.pit = pit;
    }

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
    @Column(name = "text", nullable = true, insertable = true, updatable = true, length = 200)
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

    @JoinColumn(name = "trid", referencedColumnName = "id")
    @ManyToOne
    private Training training;

    @JoinColumn(name = "uid", referencedColumnName = "UKennung")
    @ManyToOne
    private Users attendee;


    public Users getAttendee() {
        return attendee;
    }

    public void setAttendee(Users attendee) {
        this.attendee = attendee;
    }

    public Training getTraining() {
        return training;
    }

    public void setTraining(Training training) {
        this.training = training;
    }

    public Training2Users() {
    }

    public Training2Users(Date pit, Users attendee, Training training) {
        this.pit = pit;
        this.attendee = attendee;
        this.training = training;
    }
}
