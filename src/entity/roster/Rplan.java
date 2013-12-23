package entity.roster;

import entity.Homes;
import entity.system.Users;
import op.tools.SYSTools;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 16.08.13
 * Time: 16:56
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Rplan implements Comparable<Rplan> {
    @Column(name = "id", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;
    @Column(name = "p1", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    private String p1;
    @Column(name = "p2", nullable = true, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    private String p2;
//    @Column(name = "actual", nullable = true, insertable = true, updatable = true, length = 20, precision = 0)
//    @Basic
//    private String actual;
    @Column(name = "start", nullable = true, insertable = true, updatable = true, length = 19, precision = 0)
    @Temporal(TemporalType.TIMESTAMP)
    @Basic
    private Date start;
    @Column(name = "end", nullable = true, insertable = true, updatable = true, length = 19, precision = 0)
    @Temporal(TemporalType.TIMESTAMP)
    @Basic
    private Date end;
//    @Column(name = "basehours", nullable = false, insertable = true, updatable = true, length = 9, precision = 4)
//    @Basic
//    private BigDecimal basehours;
//    @Column(name = "extrahours", nullable = false, insertable = true, updatable = true, length = 9, precision = 4)
//    @Basic
//    private BigDecimal extrahours;
//    @Column(name = "break", nullable = false, insertable = true, updatable = true, length = 9, precision = 4)
//    @Basic
//    private BigDecimal breaktime;
//    @Column(name = "type", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
//    @Basic
//    private int type;
    @Column(name = "text", nullable = true, insertable = true, updatable = true, length = 200, precision = 0)
    @Basic
    private String text;
    // -----------
    @JoinColumn(name = "owner", referencedColumnName = "UKennung")
    @ManyToOne
    private Users owner;
    @JoinColumn(name = "homeid1", referencedColumnName = "EID")
    @ManyToOne
    private Homes home1;
    @JoinColumn(name = "homeid2", referencedColumnName = "EID")
    @ManyToOne
    private Homes home2;
//    @JoinColumn(name = "homeactual", referencedColumnName = "EID")
//    @ManyToOne
//    private Homes homeactual;
    @JoinColumn(name = "rosterid", referencedColumnName = "id")
    @ManyToOne
    private Rosters roster;
    @JoinColumn(name = "controller", referencedColumnName = "UKennung")
    @ManyToOne
    private Users controller;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rplan", fetch = FetchType.EAGER)
    private List<Workinglog> workinglogs;
    @Column(name = "version", nullable = false, insertable = true, updatable = true, length = 20, precision = 0)
    @Version
    private long version;

    public Rplan() {
    }

    public Rplan(Rosters roster, Homes home1, Date start, Users owner) {
        this.start = start;
        this.owner = owner;
        this.roster = roster;
        this.home1 = home1;

    }

    public Homes getHome1() {
        return home1;
    }

    public void setHome1(Homes home) {
        this.home1 = home;
    }

    public Rosters getRoster() {
        return roster;
    }

    public void setRoster(Rosters roster) {
        this.roster = roster;
    }

    public Users getOwner() {
        return owner;
    }

    public void setOwner(Users owner) {
        this.owner = owner;
    }

    public long getId() {
        return id;
    }

    public void setP1(String p1) {
        if (p1 != null) {
            this.p1 = p1.toUpperCase();
        } else {
            this.p1 = null;
        }
    }

    public void setP2(String p2) {
        if (p2 != null) {
            this.p2 = p2.toUpperCase();
        } else {
            this.p2 = null;
        }
    }

//    public void setActual(String actual) {
//        this.actual = actual;
//    }

    public Homes getHome2() {
        return home2;
    }

    public void setHome2(Homes home2) {
        this.home2 = home2;
    }



    public String getP1() {
        return SYSTools.catchNull(p1).toUpperCase();
    }

    public String getP2() {
        return SYSTools.catchNull(p2).toUpperCase();
    }

//    public String getActual() {
//        return SYSTools.catchNull(actual).toUpperCase();
//    }
//
//    public Homes getHomeactual() {
//        return homeactual;
//    }
//
//    public void setHomeactual(Homes homeactual) {
//        this.homeactual = homeactual;
//    }

    public String getEffectiveSymbol() {
        String p = getP2();
//        if (p.isEmpty()) {
//            p = getP2();
//        }
        if (p.isEmpty()) {
            p = getP1();
        }
        return p;
    }

    public Homes getEffectiveHome() {
        Homes h = getHome2();
//        if (h == null) {
//            h = getHome2();
//        }
        if (h == null) {
            h = getHome1();
        }
        return h;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public List<Workinglog> getWorkinglogs() {
        return workinglogs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rplan rplan = (Rplan) o;

        if (id != rplan.id) return false;
        if (version != rplan.version) return false;
//        if (actual != null ? !actual.equals(rplan.actual) : rplan.actual != null) return false;
        if (end != null ? !end.equals(rplan.end) : rplan.end != null) return false;
        if (home1 != null ? !home1.equals(rplan.home1) : rplan.home1 != null) return false;
        if (home2 != null ? !home2.equals(rplan.home2) : rplan.home2 != null) return false;
//        if (homeactual != null ? !homeactual.equals(rplan.homeactual) : rplan.homeactual != null) return false;
        if (owner != null ? !owner.equals(rplan.owner) : rplan.owner != null) return false;
        if (p1 != null ? !p1.equals(rplan.p1) : rplan.p1 != null) return false;
        if (p2 != null ? !p2.equals(rplan.p2) : rplan.p2 != null) return false;
        if (roster != null ? !roster.equals(rplan.roster) : rplan.roster != null) return false;
        if (start != null ? !start.equals(rplan.start) : rplan.start != null) return false;
        if (text != null ? !text.equals(rplan.text) : rplan.text != null) return false;


        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (p1 != null ? p1.hashCode() : 0);
        result = 31 * result + (p2 != null ? p2.hashCode() : 0);
//        result = 31 * result + (actual != null ? actual.hashCode() : 0);
        result = 31 * result + (start != null ? start.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (home1 != null ? home1.hashCode() : 0);
        result = 31 * result + (home2 != null ? home2.hashCode() : 0);
//        result = 31 * result + (homeactual != null ? homeactual.hashCode() : 0);
        result = 31 * result + (roster != null ? roster.hashCode() : 0);

        result = 31 * result + (int) (version ^ (version >>> 32));
        return result;
    }

    public Date getStart() {

        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Users getController() {
        return controller;
    }

    public void setController(Users controller) {
        this.controller = controller;
    }

    public boolean isLocked(){
        return controller != null || roster.isLocked() || roster.isClosed();
    }

    //
    public void setStartEndFromSymbol(Symbol symbol) {
//        basehours = symbol.getBaseHours();
//        extrahours = symbol.getExtraHours(new LocalDate(start), contractsParameterSet);
//
//        breaktime = symbol.getBreak();
        start = symbol.getStart(new LocalDate(start)).toDate();
//        type = symbol.getSymbolType();
        DateTime end = symbol.getEnd(new LocalDate(start));
        this.end = end == null ? null : end.toDate();
    }

    @Override
    public int compareTo(Rplan o) {
        return getStart().compareTo(o.getStart());
    }

//    public BigDecimal getNetValue() {
//        return basehours.subtract(breaktime);
//    }




}
