package op.tools;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 23.06.12
 * Time: 15:12
 * To change this template use File | Settings | File Templates.
 */
public class InfoTreeNodeBean {
    String tagName, name, label;
    BigDecimal score;
    int size;


    public InfoTreeNodeBean(String tagName, String name, String label, BigDecimal score) {
        this.tagName = tagName;
        this.name = name;
        this.label = label;
        this.score = score;
        this.size = 0;
    }

    public InfoTreeNodeBean(String tagName, String name, String label) {
        this.tagName = tagName;
        this.name = name;
        this.label = label;
        this.score = null;
        this.size = 0;
    }

    public InfoTreeNodeBean(String tagName, String name, String label, int size) {
        this.size = size;
        this.label = label;
        this.name = name;
        this.tagName = tagName;
        this.score = null;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getTagName() {
        return tagName;
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

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "InfoTreeNodeBean{" +
                "tagName='" + tagName + '\'' +
                ", name='" + name + '\'' +
                ", label='" + label + '\'' +
                ", score=" + score +
                ", size=" + size +
                '}';
    }
}
