/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.offene_pflege.backend.entity.done;

import de.offene_pflege.backend.entity.DefaultEntity;
import de.offene_pflege.gui.interfaces.EditorComponent;
import de.offene_pflege.gui.interfaces.NotRemovableUnlessEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Collection;

/**
 * @author tloehr
 */
@Entity
@Table(name = "resinfocategory")
public class ResInfoCategory extends DefaultEntity implements Comparable {
    private String text;
    private Integer catType;
    private Integer sort;
    private String color;
    private Collection<ResInfoType> resInfoTypes;
    private Collection<Intervention> interventions;
    private Collection<NursingProcess> nursingProcesses;

    public ResInfoCategory() {
    }

    @EditorComponent(label = "misc.msg.nameOfElement", component = {"textfield"})
    @Size(min = 1, max = 100)
    @Column(name = "Bezeichnung")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Column(name = "KatArt")
    @EditorComponent(label = "misc.msg.categorytype", renderer = "gui.renderer.ResInfoCategoryTypesRenderer", model = "gui.renderer.ResInfoCategoryTypesModel", component = {"combobox"})

    public Integer getCatType() {
        return catType;
    }

    public void setCatType(Integer catType) {
        this.catType = catType;
    }

    @Column(name = "Sortierung")
    @EditorComponent(label = "misc.msg.sorting", parserClass = "gui.parser.IntegerParser", component = {"textfield"})
    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Column(name = "color", length = 6)
    @EditorComponent(label = "misc.msg.colorset", component = {"colorset"})


    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "resInfoCat")
    @NotRemovableUnlessEmpty(message = "msg.cantberemoved.rooms.assigned")
    public Collection<ResInfoType> getResInfoTypes() {
        return resInfoTypes;
    }

    public void setResInfoTypes(Collection<ResInfoType> resInfoTypes) {
        this.resInfoTypes = resInfoTypes;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "category")
    @NotRemovableUnlessEmpty(message = "msg.cantberemoved.interventions.assigned")
    public Collection<Intervention> getInterventions() {
        return interventions;
    }

    public void setInterventions(Collection<Intervention> interventions) {
        this.interventions = interventions;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "category")
    @NotRemovableUnlessEmpty(message = "msg.cantberemoved.nursingProcesses.assigned")
    public Collection<NursingProcess> getNursingProcesses() {
        return nursingProcesses;
    }

    public void setNursingProcesses(Collection<NursingProcess> nursingProcesses) {
        this.nursingProcesses = nursingProcesses;
    }

    @Override
    public int compareTo(Object o) {
        return text.compareTo(((ResInfoCategory) o).getText()); //id.compareTo(((ResInfoCategory) o).getID());
    }


}
