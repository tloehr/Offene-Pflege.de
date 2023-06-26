package de.offene_pflege.services.qdvs;

import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.op.tools.SYSTools;

import java.util.ArrayList;
import java.util.List;

public class QdvsResidentInfoObject {
    public static final int MDS_GRUND_KEIN_AUSSCHLUSS = 0;
    public static final int MDS_GRUND_WENIGER_14_TAGE_DA = 1;
    public static final int MDS_GRUND_KURZZEIT = 2;
    public static final int MDS_GRUND_PALLIATIV = 3;
    public static final int MDS_GRUND_MEHR_ALS_21_TAGE_WEG = 4;
    // erst ab Spec30
    public static final int MDS_GRUND_HIRNSCHADEN_WACHKOMA_APALLISCH = 5;
    Resident resident;
    int ausschluss_grund;
    List<String> fehler;
    List<String> auffaelligkeiten;

    public QdvsResidentInfoObject(Resident resident) {
        this.resident = resident;
        ausschluss_grund = MDS_GRUND_KEIN_AUSSCHLUSS;
        fehler = new ArrayList<>();
        auffaelligkeiten = new ArrayList<>();
    }

    public void addLog(String message) {
        fehler.add(SYSTools.xx(message));
    }

    public Resident getResident() {
        return resident;
    }

    public void addAuffaeligkeit(String message) {
        auffaelligkeiten.add(SYSTools.xx(message));
    }

    public int getAusschluss_grund() {
        return ausschluss_grund;
    }

    public void setAusschluss_grund(int ausschluss_grund) {
        this.ausschluss_grund = ausschluss_grund;
    }

    public boolean isFehlerfrei() {
        return fehler.size() == 0;
    }

    public boolean isAuffaellig() {
        return auffaelligkeiten.size() > 0;
    }

    public List<String> getFehler() {
        return fehler;
    }

    public List<String> getAuffaelligkeiten() {
        return auffaelligkeiten;
    }
}
