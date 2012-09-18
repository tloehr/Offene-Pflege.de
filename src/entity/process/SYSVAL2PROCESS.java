package entity.process;

import entity.ResValues;

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
@NamedQueries({
        @NamedQuery(name = "SYSVAL2PROCESS.findActiveAssignedVorgaengeByElement", query = " " +
                " SELECT s.vorgang FROM SYSVAL2PROCESS s WHERE s.bwerte = :element AND s.vorgang.to = '9999-12-31 23:59:59' "),
        @NamedQuery(name = "SYSVAL2PROCESS.findByElementAndVorgang", query = " " +
                " SELECT s FROM SYSVAL2PROCESS s WHERE s.bwerte = :element AND s.vorgang = :process AND s.vorgang.to = '9999-12-31 23:59:59' ")
})
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
    private ResValues bwerte;

    protected SYSVAL2PROCESS() {
    }

    public SYSVAL2PROCESS(QProcess vorgang, ResValues bwerte) {
        this.id = 0;
        this.vorgang = vorgang;
        this.bwerte = bwerte;
    }

    public QProcess getVorgang() {
        return vorgang;
    }

    public void setVorgang(QProcess vorgang) {
        this.vorgang = vorgang;
    }

    public ResValues getBwerte() {
        return bwerte;
    }

    public void setBwerte(ResValues bwerte) {
        this.bwerte = bwerte;
    }
}
