package entity.roster;

import entity.Homes;
import entity.system.Users;
import op.OPDE;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 14.08.13
 * Time: 17:02
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Workinglog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Basic(optional = false)
    @Column(name = "pit")
    @Temporal(TemporalType.DATE)
    private Date pit;
    @Column(name = "actual", nullable = true, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    private String actual;
    @Column(name = "hours", nullable = false, insertable = true, updatable = true, length = 9, precision = 4)
    @Basic
    private BigDecimal hours;
    @Column(name = "text", nullable = true, insertable = true, updatable = true, length = 400, precision = 0)
    @Basic
    private String text;
    @Column(name = "version", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Version
    private long version;

    // ---
    @JoinColumn(name = "rplanid", referencedColumnName = "id")
    @OneToOne
    private Rplan rplan;
    @JoinColumn(name = "owner", referencedColumnName = "UKennung")
    @ManyToOne
    private Users owner;
    @JoinColumn(name = "creator", referencedColumnName = "UKennung")
    @ManyToOne
    private Users creator;
    @JoinColumn(name = "controller", referencedColumnName = "UKennung")
    @ManyToOne
    private Users controller;
    @JoinColumn(name = "homeid", referencedColumnName = "EID")
    @ManyToOne
    private Homes home;


    public Workinglog() {
    }

    public Workinglog(Homes home, Users owner, String actual, BigDecimal hours) {
        this.home = home;
        this.owner = owner;
        this.actual = actual;
        this.creator = OPDE.getLogin().getUser();
        this.hours = hours;
    }

    public Workinglog(Homes home, Users owner, BigDecimal hours) {
        this(home, owner, null, hours);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
    }


    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }


    public Users getCreator() {
        return creator;
    }

    public void setCreator(Users creator) {
        this.creator = creator;
    }

    public Users getController() {
        return controller;
    }

    public void setController(Users controller) {
        this.controller = controller;
    }

    public Homes getHome() {
        return home;
    }

    public void setHome(Homes home) {
        this.home = home;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Workinglog that = (Workinglog) o;

        if (id != that.id) return false;
        if (version != that.version) return false;
        if (actual != null ? !actual.equals(that.actual) : that.actual != null) return false;
        if (controller != null ? !controller.equals(that.controller) : that.controller != null) return false;
        if (creator != null ? !creator.equals(that.creator) : that.creator != null) return false;
        if (home != null ? !home.equals(that.home) : that.home != null) return false;
        if (hours != null ? !hours.equals(that.hours) : that.hours != null) return false;
        if (owner != null ? !owner.equals(that.owner) : that.owner != null) return false;
        if (pit != null ? !pit.equals(that.pit) : that.pit != null) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (pit != null ? pit.hashCode() : 0);
        result = 31 * result + (actual != null ? actual.hashCode() : 0);
        result = 31 * result + (hours != null ? hours.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (int) (version ^ (version >>> 32));
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (controller != null ? controller.hashCode() : 0);
        result = 31 * result + (home != null ? home.hashCode() : 0);
        return result;
    }

    public Date getPit() {
        return pit;
    }

    public void setPit(Date pit) {
        this.pit = pit;
    }


    public BigDecimal getHours() {

        return hours;
    }

    public void setHours(BigDecimal hours) {
        this.hours = hours;
    }

    public Users getOwner() {
        return owner;
    }

    public void setOwner(Users owner) {
        this.owner = owner;
    }

    public Rplan getRplan() {
        return rplan;
    }

    public void setRplan(Rplan rplan) {
        this.rplan = rplan;
    }


    @Override
    public String toString() {
        return "Workinglog{" +
                "id=" + id +
                ", pit=" + pit +
                ", actual='" + actual + '\'' +
                ", hours=" + hours +
                ", text='" + text + '\'' +
                ", home=" + home +
                "} " + super.toString();
    }
}
