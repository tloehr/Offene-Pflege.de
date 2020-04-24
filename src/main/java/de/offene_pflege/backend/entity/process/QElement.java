/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.offene_pflege.backend.entity.process;

import de.offene_pflege.backend.entity.done.Resident;
import de.offene_pflege.backend.entity.system.OPUsers;

import java.util.ArrayList;

/**
 * Dieses Interface dient dazu, dass ich mit den Elementen eines Vorgangs in den Hilfsklassen einheitlich umgehen kann.
 * Ich definiere hier einfach alle Methoden und Eigenschaften, welche diese Klasse gemeinsam haben. Somit können alle
 * Entitäten, die ein QProcessElement sind an bestehende Vorgänge angehangen werden.
 *
 * @author tloehr
 */
public interface QElement {

    /**
     * Hilfsmethode, die bei der Comparator Klasse dazu dient, das Sortierkriterium zu erhalten. Also Point in Time
     * (PIT) als long.
     *
     * @return
     */
    long pitInMillis();

    ArrayList<QProcess> findAttachedProcesses();

    /**
     * Liefert eine Beschreibung des Elements als HTML.
     *
     * @return
     */
    String contentAsHTML();

    String titleAsString();

    /**
     * Liefert eine Darstellung für die Datumsspalte in HTML.
     *
     * @return
     */
    String pitAsHTML();

    OPUsers findOwner();

    Resident getResident();

}
