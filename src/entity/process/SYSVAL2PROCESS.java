package entity.process;

import entity.values.ResValue;

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
@Table(name = "SYSVAL2PROCESS")
//@NamedQueries({
//        @NamedQuery(name = "SYSVAL2PROCESS.findActiveAssignedVorgaengeByElement", query = " " +
//                " SELECT s.vorgang FROM SYSVAL2PROCESS s WHERE s.resValue = :element AND s.vorgang.to = '9999-12-31 23:59:59' "),
//        @NamedQuery(name = "SYSVAL2PROCESS.findByElementAndVorgang", query = " " +
//                " SELECT s FROM SYSVAL2PROCESS s WHERE s.resValue = :element AND s.vorgang = :process AND s.vorgang.to = '9999-12-31 23:59:59' ")
//})
public class SYSVAL2PROCESS {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @ManyToOne
    @JoinColumn(name = "VorgangID", referencedColumnName = "VorgangID")
    private QProcess vorgang;

    @ManyToOne
    @JoinColumn(name = "BWID", referencedColumnName = "BWID")
    private ResValue resValue;

    protected SYSVAL2PROCESS() {
    }

    public SYSVAL2PROCESS(QProcess vorgang, ResValue resValue) {
        this.vorgang = vorgang;
        this.resValue = resValue;
    }

    public QProcess getQProcess() {
        return vorgang;
    }

    public ResValue getResValue() {
        return resValue;
    }

}
