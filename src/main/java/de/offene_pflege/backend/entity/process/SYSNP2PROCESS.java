package de.offene_pflege.backend.entity.process;

import de.offene_pflege.backend.entity.done.NursingProcess;

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
@Table(name = "sysnp2process")

public class SYSNP2PROCESS {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @ManyToOne
    @JoinColumn(name = "VorgangID", referencedColumnName = "VorgangID")
    private QProcess qProcess;

    @ManyToOne
    @JoinColumn(name = "PlanID", referencedColumnName = "PlanID")
    private NursingProcess nursingProcess;

    protected SYSNP2PROCESS() {
    }

    public SYSNP2PROCESS(QProcess qProcess, NursingProcess nursingProcess) {
        this.id = 0;
        this.qProcess = qProcess;
        this.nursingProcess = nursingProcess;
    }

    public QProcess getQProcess() {
        return qProcess;
    }

    public void setQProcess(QProcess qProcess) {
        this.qProcess = qProcess;
    }

    public NursingProcess getNursingProcess() {
        return nursingProcess;
    }

    public void setNursingProcess(NursingProcess nursingProcess) {
        this.nursingProcess = nursingProcess;
    }
}