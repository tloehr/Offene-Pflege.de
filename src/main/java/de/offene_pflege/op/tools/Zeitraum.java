package de.offene_pflege.op.tools;

import java.util.Date;

/**
 * Mini Hilfsklasse, damit ich den häufig verwendeten Zeitraum-Begriff besser formulieren kann.
 *
 */
public class Zeitraum {
    private Date von, bis;


    public Zeitraum(Date von, Date bis) throws Exception {

        if (von.compareTo(bis) > 0){
            throw new Exception("von muss vor bis liegen.");
        }

        this.von = von;
        this.bis = bis;

    }

    public Date getVon() {
        return von;
    }

    public void setVon(Date von) {
        this.von = von;
    }

    public Date getBis() {
        return bis;
    }

    public void setBis(Date bis) {
        this.bis = bis;
    }

    public boolean isCurrent(){
        Date now = new Date();
        return von.compareTo(now) <= 0 && bis.compareTo(now) >= 0;
    }

}
