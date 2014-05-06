package entity.prescription;

import op.tools.SYSTools;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "situations")

public class Situations implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SitID")
    private Long sitID;
    @Column(name = "Kategorie")
    private Short kategorie;
    @Column(name = "UKategorie")
    private Short uKategorie;
    @Lob
    @Column(name = "Text")
    private String text;

    public Situations() {
    }

    public Situations(String text) {
        this.text = SYSTools.tidy(text);
    }

    public Long getSitID() {
        return sitID;
    }

    public void setSitID(Long sitID) {
        this.sitID = sitID;
    }

    public Short getKategorie() {
        return kategorie;
    }

    public void setKategorie(Short kategorie) {
        this.kategorie = kategorie;
    }

    public Short getUKategorie() {
        return uKategorie;
    }

    public void setUKategorie(Short uKategorie) {
        this.uKategorie = uKategorie;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = SYSTools.tidy(text);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Situations that = (Situations) o;

        if (kategorie != null ? !kategorie.equals(that.kategorie) : that.kategorie != null) return false;
        if (sitID != null ? !sitID.equals(that.sitID) : that.sitID != null) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;
        if (uKategorie != null ? !uKategorie.equals(that.uKategorie) : that.uKategorie != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sitID != null ? sitID.hashCode() : 0;
        result = 31 * result + (kategorie != null ? kategorie.hashCode() : 0);
        result = 31 * result + (uKategorie != null ? uKategorie.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Situations{" +
                "sitID=" + sitID +
                ", kategorie=" + kategorie +
                ", uKategorie=" + uKategorie +
                ", text='" + text + '\'' +
                '}';
    }
}