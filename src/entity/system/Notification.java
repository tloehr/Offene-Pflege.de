package entity.system;

import javax.persistence.*;

/**
 * Created by tloehr on 16.01.15.
 */
@Entity
@Table(name = "notification")
public class Notification {


    private String nkey;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JoinColumn(name = "UID", referencedColumnName = "UKennung")
    @ManyToOne
    private Users user;

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    @Basic
    @Column(name = "nkey", nullable = false, insertable = true, updatable = true, length = 512)
    public String getNkey() {
        return nkey;
    }

    public void setNkey(String nkey) {
        this.nkey = nkey;
    }

    public Notification() {
    }

    public Notification(String nkey, Users user) {
        this.nkey = nkey;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Notification that = (Notification) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (nkey != null ? !nkey.equals(that.nkey) : that.nkey != null) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = nkey != null ? nkey.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }
}
