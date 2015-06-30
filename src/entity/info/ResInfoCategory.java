/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entity.info;

import entity.nursingprocess.Intervention;
import entity.nursingprocess.NursingProcess;
import gui.interfaces.EditorComponent;
import gui.interfaces.NotRemovableUnlessEmpty;
import op.tools.SYSTools;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;

/**
 * @author tloehr
 */
@Entity
@Table(name = "resinfocategory")
public class ResInfoCategory implements Serializable, Comparable {
    private static final long serialVersionUID = 1L;

    @Id
    @EditorComponent(label = "misc.msg.primary.key", component = {"textfield"}, readonly = "true")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BWIKID")
    private Long id;
    @EditorComponent(label = "misc.msg.nameOfElement", component = {"textfield"})
    @Size(min = 1, max = 100)
    @Column(name = "Bezeichnung")
    private String text;
    @Column(name = "KatArt")
    @EditorComponent(label = "Etage", renderer = "gui.renderer.ResInfoCategoryTypesRenderer", model = "gui.renderer.ResInfoCategoryTypesModel", component = {"combobox"})
    private Integer catType;
    @Column(name = "Sortierung")
    @EditorComponent(label = "misc.msg.sorting", parserClass = "gui.interfaces.IntegerParser", component = {"textfield"})
    private Integer sort;
    @Column(name = "color", length = 6)
    @EditorComponent(label = "misc.msg.colorset", component = {"colorset"})
    private String color;
    @Version
    @Column(name = "version")
    private Long version;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "resInfoCat")
    @NotRemovableUnlessEmpty(message = "msg.cantberemoved.rooms.assigned")
    private Collection<ResInfoType> resInfoTypes;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "category")
    @NotRemovableUnlessEmpty(message = "msg.cantberemoved.interventions.assigned")
    private Collection<Intervention> interventions;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "category")
    @NotRemovableUnlessEmpty(message = "msg.cantberemoved.nursingProcesses.assigned")
    private Collection<NursingProcess> nursingProcesses;

    public Collection<ResInfoType> getResInfoTypes() {
        return resInfoTypes;
    }

    public Collection<Intervention> getInterventions() {
        return interventions;
    }

    public Collection<NursingProcess> getNursingProcesses() {
        return nursingProcesses;
    }

    public ResInfoCategory() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public ResInfoCategory(Integer catType) {
        this.text = SYSTools.xx("opde.settings.model.btnAddCategory");
        this.catType = catType;
        this.sort = 1;
        this.color = "ffffff";
    }

    public ResInfoCategory(Long id) {
        this.id = id;
    }

    public Long getID() {
        return id;
    }

    public String getText() {
        return text;
    }

    @Override
    public int compareTo(Object o) {
        return text.compareTo(((ResInfoCategory) o).getText()); //id.compareTo(((ResInfoCategory) o).getID());
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getCatType() {

        return catType;
    }


    public void setCatType(Integer catType) {
        this.catType = catType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResInfoCategory that = (ResInfoCategory) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (catType != null ? !catType.equals(that.catType) : that.catType != null) return false;
//        if (resInfoTypes != null ? !resInfoTypes.equals(that.resInfoTypes) : that.resInfoTypes != null) return false;
        if (sort != null ? !sort.equals(that.sort) : that.sort != null) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (catType != null ? catType.hashCode() : 0);
        result = 31 * result + (sort != null ? sort.hashCode() : 0);
//        result = 31 * result + (resInfoTypes != null ? resInfoTypes.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return text;
    }

}
