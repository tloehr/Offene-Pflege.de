package entity.roster;

import entity.Homes;
import entity.system.Users;
import op.tools.SYSTools;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 16.08.13
 * Time: 16:56
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Rplan {
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
    @Column(name = "p3", nullable = true, insertable = true, updatable = true, length = 20, precision = 0)
    @Basic
    private String p3;
    @Column(name = "start", nullable = true, insertable = true, updatable = true, length = 19, precision = 0)
    @Temporal(TemporalType.TIMESTAMP)
    @Basic
    private Date start;
    @Column(name = "end", nullable = true, insertable = true, updatable = true, length = 19, precision = 0)
    @Temporal(TemporalType.TIMESTAMP)
    @Basic
    private Date end;
    @Column(name = "basehours", nullable = false, insertable = true, updatable = true, length = 9, precision = 4)
    @Basic
    private BigDecimal basehours;
    @Column(name = "extrahours", nullable = false, insertable = true, updatable = true, length = 9, precision = 4)
    @Basic
    private BigDecimal extrahours;
    @Column(name = "break", nullable = false, insertable = true, updatable = true, length = 9, precision = 4)
    @Basic
    private BigDecimal breaktime;
    @Column(name = "type", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    private int type;
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
    @JoinColumn(name = "homeid3", referencedColumnName = "EID")
    @ManyToOne
    private Homes home3;
    @JoinColumn(name = "rosterid", referencedColumnName = "id")
    @ManyToOne
    private Rosters roster;
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

    public void setId(long id) {
        this.id = id;
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

    public void setP3(String p3) {
        if (p3 != null) {
            this.p3 = p3.toUpperCase();
        } else {
            this.p3 = null;
        }
    }

    public Homes getHome2() {
        return home2;
    }

    public void setHome2(Homes home2) {
        this.home2 = home2;
    }

    public Homes getHome3() {
        return home3;
    }

    public void setHome3(Homes home3) {
        this.home3 = home3;
    }

    public String getP1() {
        return SYSTools.catchNull(p1).toUpperCase();
    }

    public String getP2() {
        return SYSTools.catchNull(p2).toUpperCase();
    }

    public String getP3() {
        return SYSTools.catchNull(p3).toUpperCase();
    }

    public String getEffectiveP() {
        String p = getP3();
        if (p.isEmpty()) {
            p = getP2();
        }
        if (p.isEmpty()) {
            p = getP1();
        }
        return p;
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


    public BigDecimal getBasehours() {
        return basehours;
    }

    public void setBasehours(BigDecimal basehours) {
        this.basehours = basehours;
    }


    public BigDecimal getExtrahours() {
        return extrahours;
    }

    public void setExtrahours(BigDecimal extrahours) {
        this.extrahours = extrahours;
    }


    public BigDecimal getBreaktime() {
        return breaktime;
    }

    public void setBreaktime(BigDecimal breaktime) {
        this.breaktime = breaktime;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setValuesFromSymbol(Symbol symbol, ContractsParameterSet contractsParameterSet) {
        basehours = symbol.getBaseHours();
        extrahours = symbol.getExtraHours(new LocalDate(start), contractsParameterSet);
        breaktime = symbol.getBreak();
        start = symbol.getStart(new LocalDate(start)).toDate();
        DateTime end = symbol.getEnd(new LocalDate(start));
        this.end = end == null ? null : end.toDate();
    }


}
