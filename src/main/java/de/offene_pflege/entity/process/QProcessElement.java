/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offene_pflege.entity.process;

import de.offene_pflege.entity.info.Resident;
import de.offene_pflege.entity.system.OPUsers;

import java.util.ArrayList;

/**
 * Dieses Interface dient dazu, dass ich mit den Elementen eines Vorgangs
 * in den Hilfsklassen einheitlich umgehen kann. Ich definiere
 * hier einfach alle Methoden und Eigenschaften, welche diese
 * Klasse gemeinsam haben. Somit können alle Entitäten, die ein QProcessElement sind
 * an bestehende Vorgänge angehangen werden.
 *
 * @author tloehr
 */
public interface QProcessElement {

    /**
     * Hilfsmethode, die bei der Comparator Klasse dazu dient, das Sortierkriterium zu erhalten.
     * Also Point in Time (PIT) als long.
     *
     * @return
     */
    public long getPITInMillis();

    public ArrayList<QProcess> getAttachedProcesses();

    /**
     * Liefert eine Beschreibung des Elements als HTML.
     *
     * @return
     */
    public String getContentAsHTML();

    public String getTitle();

    /**
     * Liefert eine Darstellung für die Datumsspalte in HTML.
     *
     * @return
     */
    public String getPITAsHTML();

    /**
     * Gibt eine ID des Objektes zurück. Das ist der Primary Key aus der Datenbank.
     *
     * @return
     */
    public long getID();

    public OPUsers getUser();

    public Resident getResident();
}
