package entity.system;

import entity.info.ResInfo;
import entity.nursingprocess.NursingProcess;
import entity.prescription.Prescription;
import entity.qms.Qmsplan;
import entity.reports.NReport;
import entity.staff.Training;
import gui.interfaces.EditorComponent;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Collection;

/**
 * Created by tloehr on 28.05.14.
 */
@Entity
@Table(name = "commontags")
public class Commontags implements Comparable<Commontags> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "text", nullable = false, insertable = true, updatable = true, length = 15, unique = true)
    @EditorComponent(label = "misc.msg.nameOfElement", component = {"textfield"})
    @Size(min = 1, max = 15, message = "msg.string.length.error")
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    @Basic
    @Column(name = "type", nullable = true, insertable = true, updatable = true)
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Column(name = "color", nullable = false, insertable = true, updatable = true, length = 6)
    @EditorComponent(label = "misc.msg.colorset", component = {"colorset"})
    private String color;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Version
    @Column(name = "version")
    private Long version;

    //TODO: add a sorter row

    @ManyToMany(mappedBy = "commontags")
    private Collection<Training> trainings;

    public Collection<Training> getTrainings() {
        return trainings;
    }

    public Collection<Qmsplan> getQmsplans() {
        return qmsplans;
    }

    public Collection<NReport> getnReports() {
        return nReports;
    }

    public Collection<Prescription> getPrescriptions() {
        return prescriptions;
    }

    public Collection<ResInfo> getResinfos() {
        return resinfos;
    }

    public Collection<NursingProcess> getNursingProcesses() {
        return nursingProcesses;
    }

    @ManyToMany(mappedBy = "commontags")
    private Collection<Qmsplan> qmsplans;

    @ManyToMany(mappedBy = "commontags")
    private Collection<NReport> nReports;

    @ManyToMany(mappedBy = "commontags")
    private Collection<Prescription> prescriptions;

    @ManyToMany(mappedBy = "commontags")
    private Collection<ResInfo> resinfos;

    @ManyToMany(mappedBy = "commontags")
    private Collection<NursingProcess> nursingProcesses;

    public Commontags() {
    }

    public Commontags(String text) {
        this.text = text;
        this.type = CommontagsTools.TYPE_SYS_USER;
        this.color = "000000";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Commontags that = (Commontags) o;

        if (id != that.id) return false;
        if (type != that.type) return false;
        if (color != null ? !color.equals(that.color) : that.color != null) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + type;
        result = 31 * result + (color != null ? color.hashCode() : 0);
        return result;
    }


    @Override
    public int compareTo(Commontags o) {
        int compare = o.getColor().compareTo(getColor());
        compare = compare == 0 ? Boolean.compare(o.getType() > 0, getType() > 0) : compare;
        compare = compare == 0 ? o.getText().compareTo(getText()) * -1 : compare;

        return compare;
    }

    @Override
    public String toString() {
        return text;
    }
}
