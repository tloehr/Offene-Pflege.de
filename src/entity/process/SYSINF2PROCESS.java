package entity.process;

import entity.info.ResInfo;

import javax.persistence.*;

/**
 * Diese Tabelle ist eine Art Kunstgriff. Normalerweise wäre die Zuordnung zwischen den Vorgängen und den Pflegeberichten eine simple M:N Relation.
 * Jetzt möchte ich aber noch zusätzlich festhalten, in welchem PDCA Abschnitt sich der vorliegende Pflegebericht bei der Zuordnung gehört.
 * Dazu wäre also eine "attributierte" Relationen Tabelle nötig. FÜr die Datenbank kein Problem. Aber JPA hat dafür kein Konzept. Also
 * weiche ich hier einfach darauf aus, die Relationentabelle selbst zu führen. Die beiden zu verbindgen Entity Klassen (hier QProcess und NReport)
 * erhalten jeweils eine 1:n Relation auf DIESE Klasse. So gehts dann.
 * Wer mehr wissen will: http://en.wikibooks.org/wiki/Java_Persistence/ManyToMany#Mapping_a_Join_Table_with_Additional_Columns
 * Leider bin ich nicht selbst drauf gekommen.
 */
@Entity
@Table(name = "SYSINF2PROCESS")
@NamedQueries({
        @NamedQuery(name = "SYSINF2PROCESS.findActiveAssignedVorgaengeByElement", query = " " +
                " SELECT s.vorgang FROM SYSINF2PROCESS s WHERE s.bwinfo = :element AND s.vorgang.to = '9999-12-31 23:59:59' "),
        @NamedQuery(name = "SYSINF2PROCESS.findByElementAndVorgang", query = " " +
                " SELECT s FROM SYSINF2PROCESS s WHERE s.bwinfo = :element AND s.vorgang = :process AND s.vorgang.to = '9999-12-31 23:59:59' ")
})
public class SYSINF2PROCESS {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @ManyToOne
    @JoinColumn(name = "VorgangID", referencedColumnName = "VorgangID")
    private QProcess vorgang;

    @ManyToOne
    @JoinColumn(name = "BWInfoID", referencedColumnName = "BWINFOID")
    private ResInfo bwinfo;

    protected SYSINF2PROCESS() {
    }

    public SYSINF2PROCESS(QProcess vorgang, ResInfo bwinfo) {
        this.id = 0;
        this.vorgang = vorgang;
        this.bwinfo = bwinfo;
    }

    public QProcess getVorgang() {
        return vorgang;
    }

    public void setVorgang(QProcess vorgang) {
        this.vorgang = vorgang;
    }

    public ResInfo getBwinfo() {
        return bwinfo;
    }

    public void setBwinfo(ResInfo bwinfo) {
        this.bwinfo = bwinfo;
    }
}