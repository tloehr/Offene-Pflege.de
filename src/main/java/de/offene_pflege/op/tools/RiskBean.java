package de.offene_pflege.op.tools;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 23.06.12
 * Time: 14:32
 * To change this template use File | Settings | File Templates.
 */
public class RiskBean {
    BigDecimal from, to;
    String label;
    String color;
    String rating;

    public RiskBean(String from, String to, String label, String color, String rating) {
        try {
            this.from = new BigDecimal(NumberFormat.getNumberInstance().parse(from).doubleValue());
        } catch (ParseException e) {
            this.from = null;
        }

        try {
            this.to = new BigDecimal(NumberFormat.getNumberInstance().parse(to).doubleValue());
        } catch (ParseException e) {
            this.to = null;
        }
        this.label = label;
        this.color = color;
        this.rating = rating;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public BigDecimal getFrom() {
        return from;
    }

    public void setFrom(BigDecimal from) {
        this.from = from;
    }

    public BigDecimal getTo() {
        return to;
    }

    public void setTo(BigDecimal to) {
        this.to = to;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
