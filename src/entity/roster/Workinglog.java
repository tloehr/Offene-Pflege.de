package entity.roster;

import entity.Homes;
import entity.system.Users;
import op.OPDE;
import op.tools.SYSTools;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 14.08.13
 * Time: 17:02
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Workinglog implements Comparable<Workinglog> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
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
    @ManyToOne
    private Rplan rplan;
    @JoinColumn(name = "creator", referencedColumnName = "UKennung")
    @ManyToOne
    private Users creator;
    @JoinColumn(name = "controller", referencedColumnName = "UKennung")
    @ManyToOne
    private Users controller;
    @JoinColumn(name = "homeid", referencedColumnName = "EID")
    @ManyToOne
    private Homes home;
    @JoinColumn(name = "editBy", referencedColumnName = "UKennung")
    @ManyToOne
    private Users editedBy;
    @JoinColumn(name = "ReplacedBy", referencedColumnName = "id")
    @OneToOne
    private Workinglog replacedBy;
    @JoinColumn(name = "ReplacementFor", referencedColumnName = "id")
    @OneToOne
    private Workinglog replacementFor;

    public Workinglog() {
    }

    public Workinglog(Homes home, Rplan rplan, String actual, BigDecimal hours) {
        this.home = home;
        this.rplan = rplan;
        this.actual = actual;
        this.creator = OPDE.getLogin().getUser();
        this.hours = hours;
    }

    public Workinglog(Rplan rplan) {
        this.rplan = rplan;
        this.home = rplan.getEffectiveHome();
        this.actual = rplan.getEffectiveP();
        this.creator = OPDE.getLogin().getUser();
        this.hours = rplan.getBasehours();
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



        return new Long(id).equals(that.getId());
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (actual != null ? actual.hashCode() : 0);
        result = 31 * result + (hours != null ? hours.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (int) (version ^ (version >>> 32));
        result = 31 * result + (rplan != null ? rplan.hashCode() : 0);
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (controller != null ? controller.hashCode() : 0);
        result = 31 * result + (home != null ? home.hashCode() : 0);
        result = 31 * result + (editedBy != null ? editedBy.hashCode() : 0);
        result = 31 * result + (replacedBy != null ? replacedBy.hashCode() : 0);
        result = 31 * result + (replacementFor != null ? replacementFor.hashCode() : 0);
        return result;
    }

    public BigDecimal getHours() {

        return hours;
    }

    public void setHours(BigDecimal hours) {
        this.hours = hours;
    }

    public Rplan getRplan() {
        return rplan;
    }

    public void setRplan(Rplan rplan) {
        this.rplan = rplan;
    }

    public boolean isActual() {
        return !SYSTools.catchNull(actual).isEmpty();
    }

    public Users getEditedBy() {
        return editedBy;
    }

    public void setEditedBy(Users editedBy) {
        this.editedBy = editedBy;
    }

    public Workinglog getReplacedBy() {
        return replacedBy;
    }

    public void setReplacedBy(Workinglog replacedBy) {
        this.replacedBy = replacedBy;
    }

    public Workinglog getReplacementFor() {
        return replacementFor;
    }

    public void setReplacementFor(Workinglog replacementFor) {
        this.replacementFor = replacementFor;
    }

    public boolean isReplaced() {
        return replacedBy != null;

    }

    public boolean isReplacement() {
        return replacementFor != null;
    }

    public boolean isDeleted() {
        return editedBy != null && replacedBy == null && replacementFor == null;
    }

    @Override
    public int compareTo(Workinglog o) {

        int sort = SYSTools.nullCompare(getRplan(), o.getRplan()) * -1;
        if (sort == 0) {
            sort = new Long(id).compareTo(new Long(o.getId()));
        }
        return sort;

    }

    @Override
    public String toString() {
        return "Workinglog{" +
                "id=" + id +
                ", actual='" + actual + '\'' +
                ", hours=" + hours +
                ", text='" + text + '\'' +
                ", home=" + home +
                "} " + super.toString();
    }
}
