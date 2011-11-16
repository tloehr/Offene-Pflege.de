package entity;

import javax.persistence.*;

/**
 * Diese Tabelle ist eine Art Kunstgriff. Normalerweise wäre die Zuordnung zwischen den Vorgängen und den Pflegeberichten eine simple M:N Relation.
 * Jetzt möchte ich aber noch zusätzlich festhalten, in welchem PDCA Abschnitt sich der vorliegende Pflegebericht bei der Zuordnung gehört.
 * Dazu wäre also eine "attributierte" Relationen Tabelle nötig. FÜr die Datenbank kein Problem. Aber JPA hat dafür kein Konzept. Also
 * weiche ich hier einfach darauf aus, die Relationentabelle selbst zu führen. Die beiden zu verbindgen Entity Klassen (hier Vorgaenge und Pflegeberichte)
 * erhalten jeweils eine 1:n Relation auf DIESE Klasse. So gehts dann.
 * Wer mehr wissen will: http://en.wikibooks.org/wiki/Java_Persistence/ManyToMany#Mapping_a_Join_Table_with_Additional_Columns
 * Leider bin ich nicht selbst drauf gekommen.
 */
@Entity
@Table(name = "SYSBWERTE2VORGANG")
@NamedQueries({
        @NamedQuery(name = "SYSBWerte2VORGANG.findActiveAssignedVorgaengeByElement", query = " " +
                " SELECT s.vorgang FROM SYSBWerte2VORGANG s WHERE s.bwerte = :element AND s.vorgang.bis = '9999-12-31 23:59:59' "),
        @NamedQuery(name = "SYSBWerte2VORGANG.findByElementAndVorgang", query = " " +
                " SELECT s FROM SYSBWerte2VORGANG s WHERE s.bwerte = :element AND s.vorgang = :vorgang AND s.vorgang.bis = '9999-12-31 23:59:59' ")
})
public class SYSBWerte2VORGANG {
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
    @JoinColumn(name = "BWID", referencedColumnName = "BWID")
    private BWerte bwerte;

    protected SYSBWerte2VORGANG() {
    }

    public SYSBWerte2VORGANG(Vorgaenge vorgang, BWerte bwerte) {
        this.id = 0;
        this.pdca = VorgaengeTools.PDCA_OFF;
        this.vorgang = vorgang;
        this.bwerte = bwerte;
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

    public BWerte getBwerte() {
        return bwerte;
    }

    public void setBwerte(BWerte bwerte) {
        this.bwerte = bwerte;
    }
}
