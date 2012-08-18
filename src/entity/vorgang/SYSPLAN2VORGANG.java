package entity.vorgang;

import entity.nursingprocess.NursingProcess;

import javax.persistence.*;

/**
 * Diese Tabelle ist eine Art Kunstgriff. Normalerweise wäre die Zuordnung zwischen den Vorgängen und den Pflegeberichten eine simple M:N Relation.
 * Jetzt möchte ich aber noch zusätzlich festhalten, in welchem PDCA Abschnitt sich der vorliegende Pflegebericht bei der Zuordnung gehört.
 * Dazu wäre also eine "attributierte" Relationen Tabelle nötig. FÜr die Datenbank kein Problem. Aber JPA hat dafür kein Konzept. Also
 * weiche ich hier einfach darauf aus, die Relationentabelle selbst zu führen. Die beiden zu verbindgen Entity Klassen (hier Vorgaenge und NReport)
 * erhalten jeweils eine 1:n Relation auf DIESE Klasse. So gehts dann.
 * Wer mehr wissen will: http://en.wikibooks.org/wiki/Java_Persistence/ManyToMany#Mapping_a_Join_Table_with_Additional_Columns
 * Leider bin ich nicht selbst drauf gekommen.
 */
@Entity
@Table(name = "SYSPLAN2VORGANG")
@NamedQueries({
        @NamedQuery(name = "SYSPLAN2VORGANG.findActiveAssignedVorgaengeByElement", query = " " +
                " SELECT s.vorgang FROM SYSPLAN2VORGANG s WHERE s.nursingProcess = :element AND s.vorgang.bis = '9999-12-31 23:59:59' "),
        @NamedQuery(name = "SYSPLAN2VORGANG.findByElementAndVorgang", query = " " +
                " SELECT s FROM SYSPLAN2VORGANG s WHERE s.nursingProcess = :element AND s.vorgang = :vorgang AND s.vorgang.bis = '9999-12-31 23:59:59' ")
})
public class SYSPLAN2VORGANG {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private long id;

    @Basic(optional = false)
    @Column(name = "PDCA")
    private short pdca;

    @ManyToOne
    @JoinColumn(name = "VorgangID", referencedColumnName = "VorgangID")
    private Vorgaenge vorgang;

    @ManyToOne
    @JoinColumn(name = "PlanID", referencedColumnName = "PlanID")
    private NursingProcess nursingProcess;

    protected SYSPLAN2VORGANG() {
    }

    public SYSPLAN2VORGANG(Vorgaenge vorgang, NursingProcess nursingProcess) {
        this.id = 0;
        this.pdca = VorgaengeTools.PDCA_OFF;
        this.vorgang = vorgang;
        this.nursingProcess = nursingProcess;
    }

    public short getPdca() {
        return pdca;
    }

    public void setPdca(short pdca) {
        this.pdca = pdca;
    }

    public Vorgaenge getVorgang() {
        return vorgang;
    }

    public void setVorgang(Vorgaenge vorgang) {
        this.vorgang = vorgang;
    }

    public NursingProcess getNursingProcess() {
        return nursingProcess;
    }

    public void setNursingProcess(NursingProcess nursingProcess) {
        this.nursingProcess = nursingProcess;
    }
}