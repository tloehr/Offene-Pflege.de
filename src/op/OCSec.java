/*
 * OffenePflege
 * Copyright (C) 2008 Torsten Löhr
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License V2 as published by the Free Software Foundation
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even 
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to 
 * the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
 * www.offene-pflege.de
 * ------------------------ 
 * Auf deutsch (freie Übersetzung. Rechtlich gilt die englische Version)
 * Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU General Public License, 
 * wie von der Free Software Foundation veröffentlicht, weitergeben und/oder modifizieren, gemäß Version 2 der Lizenz.
 *
 * Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, daß es Ihnen von Nutzen sein wird, aber 
 * OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN 
 * BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht, 
 * schreiben Sie an die Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA.
 */
package op;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import javax.swing.JComponent;
import op.tools.DlgException;

/**
 * OCSec ist die Basis für das Rechtekonzept in OPDE.de. In OC kann man für jede Component im DBTable OCRights einstellen, wer darauf
 * zugreifen darf und wer nicht.
 * <h3>OCRights</h3>
 * <table border="1">
 * <tr>
 * <th>Spaltenname</th>
 * <th>Datentyp</th>
 * <th>Erläuterung</th>
 * </tr>
 * <tr>
 * <td>OCRID</td>
 * <td>BIGINT UNSIGNED NOT NULL AUTO_INCREMENT</td>
 * <td>Primärschlüssel</td>
 * </tr>
 * <tr>
 * <td>GKennung</td>
 * <td>CHARACTER (20) NOT NULL</td>
 * <td>PK der Gruppe, die ein Recht erhält.</td>
 * </tr>
 * <tr>
 * <td>ClassName</td>
 * <td>VARCHAR (500) NOT NULL</td>
 * <td>Name der Klasse, auf die sich das Recht bezieht.</td>
 * </tr>
 * <tr>
 * <td>ComponentName</td>
 * <td>VARCHAR (500) NOT NULL</td>
 * <td>Variabelnname der Komponente innerhalb der Klasse, auf die sich das Recht bezieht.</td>
 * </tr>
 * <tr>
 * <td>Executable</td>
 * <td>BOOLEAN NOT NULL</td>
 * <td>Gibt an, ob der User das Modul starten darf oder nicht. Das EXEC Recht wird nur bei Frames berücksichtigt,
 * die von OCMain aus aufgerufen werden können. Für alle anderen Klassen gilt das EXEC Recht nicht.</td>
 * </tr>
 * <tr>
 * <td>Visible</td>
 * <td>BOOLEAN NOT NULL</td>
 * <td><i>Wird noch nicht benutzt.</i></td>
 * </tr>
 * <tr><td>Accessible</td>
 * <td>BOOLEAN NOT NULL</td>
 * <td>Gibt an, ob man eine bestimmte Component erreichen kann oder nicht. Führt später zu einer Entscheidung, ob man die Component per
 * setEnabled() auf true setzen kann oder nicht. Fehlt eine explizite Rechtezuordnung, dann gilt das ACCESS Recht als erteilt.
 * </tr>
 * </table>
 * <p>
 * Rechte können nur Gruppen zugeordnet werden. Die einzelnen Benuzter werden durch eine m:n Verknüpfung über die Tabelle OCMember mit den
 * einzelnen Gruppen verbunden. Sollte es nötig sein, dass ein bestimmtes Recht nur einem bestimmten User zugewiesen wird, dann muss man dafür
 * eine eigene, neue Gruppe erstellen.
 * <p/>
 * <p>
 * Es gibt zwei System-Gruppen, die immer vorhanden sind. Die eine heisst <i>admin</i> und die andere <i>everyone</i>. Sobald jemand Mitglied
 * der <b>admin</b> Gruppe ist, darf er immer alles. Für ihn gelten dann keine Beschränkungen mehr. Jeder User ist immer Mitglied der Gruppe
 * <b>everyone</b>. Das ist hilfreich, wenn es darum geht, dass bestimmte Components für die meisten <b>nicht</b> zu erreichen sein sollen und von
 * dieser Regel nur im Einzelfall abgewichen wird.
 * </p>
 * <p>Unterschieden werden müssen zwei Fälle. In der Klasse <B>OCMain</B> werden nur EXEC Rechte abgefragt. In diesem
 * Fenster werden wird nur darüber entschieden, welche Unterprogramme ein Benutzer aufrufen darf. Daher interssieren hier nur
 * die EXEC Rechte. Wenn jemand nicht explizit ein EXEC Recht besitzt, dann erscheint das entsprechende Programm Modul
 * erst gar nicht im Hauptfenster. Somit gilt: <B>ist etwas nicht erlaubt, dann ist es verboten.</B></p>
 * <p>In allen anderen Fällen geht es nur darum z.B. einen Button zu disabeln, damit eine bestimmte Gruppe von Usern
 * diesen nicht drücken kann. Hier gilt allerdings, <B>ist etwas nicht verboten, dann ist es erlaubt.</B></p>
 */
public class OCSec {
    /**
     * <code>classes</code> enthält alle Rechte, die bisher eingelesen wurden. Diese HashMap ist wie folgt aufebaut:
     * (classname::component, bitset(EXEC,VISIBLE,ACCESS))
     */
    private HashMap classes;
    /**
     * Die Klasse OCSec liest die Rechte-Informationen nur bei Bedarf ein. Nämlich dann, wenn sie auf bei einer Anfrage die
     * betreffende Klasse in <code>loadedClasses</code> nicht findet.
     */
    private ArrayList loadedClasses;
    /**
     * Steht für das EXEC Recht in einem BitSet.
     */
    private final int EXEC = 0;
    /**
     * Steht für das VISIBLE Recht in einem BitSet.
     */
    private final int VISIBLE = 1;
    /**
     * Steht für das ACCESS Recht in einem BitSet.
     */
    private final int ACCESS = 2;
    private final String[] BIT = {"EXEC","VISIBLE","ACCESS"};
    
    /**
     * Konstruktor
     */
    public OCSec() {
        classes = new HashMap();
        loadedClasses = new ArrayList();
    }
    
    /**
     * Aufräumarbeiten zur Vermeidung von Speicherleks.
     */
    public void cleanup(){
        classes.clear();
        loadedClasses.clear();
    }
    
    /**
     * Hier werden die Informationen bzgl. der Rechte eines Users eingelesen und anschließend in der HashMap <CODE>classes</CODE>
     * gespeichert.
     * @param classname gibt an, welche Informationen geladen werden sollen.
     */
    public void init(String classname){
        ResultSet rs = null;
        String sql = "SELECT ClassName, ComponentName, Executable, Visible, Access " +
                "FROM OCMember m INNER JOIN OCRights r ON m.GKennung = r.GKennung " +
                "WHERE r.ClassName=? AND m.UKennung=? " +
                " " +
                "UNION" +
                " " +
                "SELECT ClassName, ComponentName, Executable, Visible, Access FROM OCRights " +
                "WHERE ClassName=? AND GKennung='everyone'";
        try {
            OPDE.getLogger().debug("OCSEC: init class"+classname);
            PreparedStatement stmt = OPDE.getDb().db.prepareStatement(sql);
            stmt.setString(1, classname);
            stmt.setString(2, OPDE.getLogin().getUser().getUKennung());
            stmt.setString(3, classname);
            rs = stmt.executeQuery();
            loadedClasses.add(classname);
            if (rs.first()){
                rs.beforeFirst();
                while (rs.next()){
                    String key = getKey(rs.getString("ClassName"), rs.getString("ComponentName"));
                    // Falls mehrere verschiedene Rechte (über die verschiedenen Gruppenmitgliedschaften)
                    // vorhanden sind, gilt immer das, was für den User am besten ist.
                    // Erhält er über eine Gruppe A ein ACCESS Recht, dann hat er es auch dann
                    // falls er es über eine Gruppe B aberkannt bekommt.
                    if (classes.containsKey(key)){ // Es gibt schon eine Zuordnung. Hier muss jetzt geprüft werden.
                        BitSet bs = (BitSet) classes.get(key);
                        bs.set(EXEC, bs.get(EXEC) || rs.getBoolean("Executable"));
                        bs.set(VISIBLE, bs.get(VISIBLE) || rs.getBoolean("Visible"));
                        bs.set(ACCESS, bs.get(ACCESS) || rs.getBoolean("Access"));
                        classes.put(key, bs);
                        OPDE.getLogger().debug("OCSEC: altering right > "+key+" "+bs);
                    } else { // gibts noch nicht, dann einfach hinzufügen
                        BitSet bs = new BitSet();
                        bs.set(EXEC, rs.getBoolean("Executable"));
                        bs.set(VISIBLE, rs.getBoolean("Visible"));
                        bs.set(ACCESS, rs.getBoolean("Access"));
                        classes.put(key, bs);
                        OPDE.getLogger().debug("OCSEC: storing right > "+key+" "+bs);
                    }
                }
            }
        } catch (SQLException ex) {
            new DlgException(ex);
        }
        
    }
    
    /**
     * Erstellt den Key aus <I>classname</I> und <I>componentname</I>. Dieser Key wird dann später in der HashMap <CODE>classes</CODE> benutzt.
     * @param c1
     * @param c2
     * @return key der form classname :: componentname
     */
    private String getKey(String c1, String c2){
        return c1 + "::" + c2;
    }
    
    /**
     * Ermittelt, ob eine bestimmte Component ausführbar ist.
     * @param classname
     * @param component
     * @return true, wenn ausführbar, false sonst.
     */
    public boolean isExecutable(String classname, String component){
        return isSet(EXEC, classname, component);
    }
    /**
     * Ermittelt, ob eine bestimmte Component sichtbar sein darf.
     * @param classname
     * @param component
     * @return true, wenn ja, false sonst.
     */
    public boolean isVisible(String classname, String component){
        return isSet(VISIBLE, classname, component);
    }
    /**
     * Ermittelt, ob eine bestimmte Component erreichbar sein darf.
     * @param classname
     * @param component
     * @return true, wenn ja, false sonst.
     */
    public boolean isAccessible(String classname, String component){
        return isSet(ACCESS, classname, component);
    }
    
    public boolean isAccessible(Object cls, String component){
        return isSet(ACCESS, cls.getClass().getName(), component);
    }
    
    /**
     * ermittelt ob ein bestimmtes Recht vorhanden ist. Sollten noch keine Informationen über dieses Klasse
     * vorhanden sein, dann werden sie nachgeladen.
     * @param bit
     * @param classname
     * @param component
     * @return true, wenn ja, false sonst.
     */
    private boolean isSet(int bit, String classname, String component){
        boolean result = false;
        String key = getKey(classname, component);
        if (!OPDE.isAdmin()) {
            // Bei Bedarf werden die Informationen über die Klasse nachgeladen.
            if (!loadedClasses.contains(classname)){
                init(classname);
            }
            // Bei EXEC kann man nur aufrufen, wenn die Information vorhanden ist UND auch positiv
            if (bit == EXEC){
                result = classes.containsKey(key) && ((BitSet) classes.get(key)).get(EXEC);
            } else { // Bei den anderen beiden Fällen geht es auch dann, wenn keine Informationen vorliegen.
                result = !classes.containsKey(key) || ((BitSet) classes.get(key)).get(bit);
            }
        } else {
            result = true;
        }
        OPDE.getLogger().debug("OCSEC: "+key+" ("+BIT[bit]+") => "+result);
        return result;
    }
    
    /**
     * Hiermit wird ermittelt, ob einem bestimmten Zugriffs Wunsch auf eine Component gemäß den gesetzten
     * Rechten entsprochen werden kann oder nicht.
     * @param classname Name der Klasse, in der sich die Komponente befindet.
     * @param component Name der Componente als String.
     * @param jc Die Komponente selbst.
     * @param desiredState der Zustand der gesetzt werden soll.
     * @return true, wenn der gewünschte Zustand möglich ist. false sonst.
     */
    public void setEnabled(String classname, String component, JComponent jc, boolean desiredState){
        jc.setEnabled(desiredState && isAccessible(classname, component));
    }
    
    public void setEnabled(Object o, String component, JComponent jc, boolean desiredState){
        setEnabled(o.getClass().getName(), component, jc, desiredState);
    }
    
}