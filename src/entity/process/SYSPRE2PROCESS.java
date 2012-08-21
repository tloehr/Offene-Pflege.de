package entity.process;

import entity.prescription.Prescriptions;

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
@Table(name = "SYSVER2VORGANG")
@NamedQueries({
        @NamedQuery(name = "SYSVER2VORGANG.findActiveAssignedVorgaengeByElement", query = " " +
                " SELECT s.vorgang FROM SYSPRE2PROCESS s WHERE s.verordnung = :element AND s.vorgang.bis = '9999-12-31 23:59:59' "),
        @NamedQuery(name = "SYSVER2VORGANG.findByElementAndVorgang", query = " " +
                " SELECT s FROM SYSPRE2PROCESS s WHERE s.verordnung = :element AND s.vorgang = :vorgang AND s.vorgang.bis = '9999-12-31 23:59:59' ")
})
public class SYSPRE2PROCESS {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private long id;

    @ManyToOne
    @JoinColumn(name = "VorgangID", referencedColumnName = "VorgangID")
    private QProcess vorgang;

    @ManyToOne
    @JoinColumn(name = "VerID", referencedColumnName = "VerID")
    private Prescriptions verordnung;

    protected SYSPRE2PROCESS() {
    }

    public SYSPRE2PROCESS(QProcess vorgang, Prescriptions verordnung) {
        this.id = 0;
        this.vorgang = vorgang;
        this.verordnung = verordnung;
    }

    public QProcess getVorgang() {
        return vorgang;
    }

    public void setVorgang(QProcess vorgang) {
        this.vorgang = vorgang;
    }

    public Prescriptions getPrescription() {
        return verordnung;
    }

    public void setVerordnung(Prescriptions verordnung) {
        this.verordnung = verordnung;
    }
}

