package de.offene_pflege.op.tools;

import java.util.Optional;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 23.06.12
 * Time: 15:12
 * To change this template use File | Settings | File Templates.
 */
public class InfoTreeNodeBean {
    String tagName, name, label;
    Optional value;

    public InfoTreeNodeBean(String tagName, String name, String label, Optional value) {
        this.tagName = tagName;
        this.name = name;
        this.label = label;
        this.value = value;
    }
//
//    public InfoTreeNodeBean(String tagName, String name, String label, int size) {
//        this.size = size;
//        this.label = label;
//        this.name = name;
//        this.tagName = tagName;
//        this.score = null;
//
//    }


    public String getTagName() {
        return tagName.toLowerCase();
    }

    public boolean isCheckbox(){
        return value.isPresent() && (value.get().toString().equalsIgnoreCase("true") || value.get().toString().equalsIgnoreCase("false"));
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "InfoTreeNodeBean{" +
                "tagName='" + tagName + '\'' +
                ", name='" + name + '\'' +
                ", label='" + label + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public Optional getValue() {
        return value;
    }

    public void setValue(Optional value) {
        this.value = value;
    }
}
