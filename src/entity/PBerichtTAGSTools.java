/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import op.OPDE;
import org.jdesktop.swingx.JXTaskPane;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author tloehr
 */
public class PBerichtTAGSTools {

    /**
     * Erstellt ein JMenu bestehend aus Checkboxen. Für jede aktive PBerichtTag jeweils eine.
     * Wenn man die anklickt, wird eine Markierung zum Bericht hinzugefügt. Dieses Menü
     * wird in PnlBerichte verwendet. Als Kontextmenü für die einzelnen Berichtszeilen.
     *
     * @param bericht Der Bericht, für den das Menü erzeugt werden soll.
     *                Je nachdem, welche Tags diesem Bericht schon zugewiesen sind, werden die Checkboxen bereits angeklickt oder auch nicht.
     *                Für das Menü wird ein Listener definiert, der weitere Tags setzt oder entfernt.
     * @return das vorbereitete Menü
     */
    public static JMenu createMenuForTags(Pflegeberichte bericht) {
        final Pflegeberichte finalbericht = bericht;
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("PBerichtTAGS.findAllActive");
        ArrayList<PBerichtTAGS> tags = new ArrayList(query.getResultList());

        JMenu menu = new JMenu("Text-Markierungen");
        Iterator<PBerichtTAGS> itTags = tags.iterator();
        while (itTags.hasNext()) {
            final PBerichtTAGS tag = itTags.next();
            JCheckBox cb = new JCheckBox(tag.getBezeichnung());
            cb.setForeground(tag.getColor());
            if (tag.isBesonders()) {
                cb.setFont(new Font("Lucida Grande", Font.BOLD, 13));
            }
            cb.setSelected(bericht.getTags().contains(tag));


            cb.addItemListener(new ItemListener() {

                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.DESELECTED) {
                        finalbericht.getTags().remove(tag);
                    } else {
                        finalbericht.getTags().add(tag);
                    }
                }
            });
            menu.add(cb);

        }

        menu.addMenuListener(new MenuListener() {

            @Override
            public void menuSelected(MenuEvent e) {
            }

            @Override
            public void menuDeselected(MenuEvent e) {
                EntityTools.merge(finalbericht);
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        });

        em.close();

        return menu;
    }

    /**
     * Erstellt eine JPanel, die mit Checkboxen gefüllt ist. Pro aktive PBerichtTag jeweils eine.
     *
     * @param listener  Ein ItemListener, der sagt, was geschehen soll, wenn man auf die Checkboxen klickt.
     * @param preselect Eine Collection aus Tags besteht. Damit kann man einstellen, welche Boxen schon vorher angeklickt sein sollen.
     * @param layout    Ein Layoutmanager für das Panel.
     * @return das Panel zur weiteren Verwendung.
     */
    public static JPanel createCheckBoxPanelForTags(ItemListener listener, Collection<PBerichtTAGS> preselect, LayoutManager layout) {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("PBerichtTAGS.findAllActive");
        ArrayList<PBerichtTAGS> tags = new ArrayList(query.getResultList());
        JPanel panel = new JPanel(layout);
        Iterator<PBerichtTAGS> itTags = tags.iterator();
        while (itTags.hasNext()) {
            PBerichtTAGS tag = itTags.next();
            JCheckBox cb = new JCheckBox(tag.getBezeichnung());
            cb.setForeground(tag.getColor());
            if (tag.isBesonders()) {
                cb.setFont(new Font("Lucida Grande", Font.BOLD, 13));
            }
            cb.putClientProperty("UserObject", tag);

            cb.setSelected(preselect.contains(tag));
            cb.addItemListener(listener);

            panel.add(cb);
        }
        em.close();
        return panel;

    }


    /**
     * Erstellt eine JXTaskPane, der mit Checkboxen gefüllt ist. Pro aktive PBerichtTag jeweils eine.
     *
     * @param listener  Ein ItemListener, der sagt, was geschehen soll, wenn man auf die Checkboxen klickt.
     * @param preselect Eine Collection aus Tags besteht. Damit kann man einstellen, welche Boxen schon vorher angeklickt sein sollen.
     * @param layout    Ein Layoutmanager für das Panel.
     * @return das Panel zur weiteren Verwendung.
     */
    public static void addCheckBoxPanelForTags(JXTaskPane panel, ItemListener listener, Collection<PBerichtTAGS> preselect) {
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("PBerichtTAGS.findAllActive");
        ArrayList<PBerichtTAGS> tags = new ArrayList(query.getResultList());
        Iterator<PBerichtTAGS> itTags = tags.iterator();
        while (itTags.hasNext()) {
            PBerichtTAGS tag = itTags.next();
            JCheckBox cb = new JCheckBox(tag.getBezeichnung());
            cb.setForeground(tag.getColor());
            if (tag.isBesonders()) {
                cb.setFont(new Font("Lucida Grande", Font.BOLD, 13));
            }
            cb.putClientProperty("UserObject", tag);

            cb.setSelected(preselect.contains(tag));
            cb.addItemListener(listener);

            panel.add(cb);
        }
        em.close();
    }

    /**
     * Kleine Hilfsmethode, die ich brauche um festzustellen ob ein bestimmter bericht
     * ein Sozial Bericht ist.
     */
    public static boolean isSozial(Pflegeberichte bericht) {
        Iterator<PBerichtTAGS> itTags = bericht.getTags().iterator();
        boolean yes = false;
        while (!yes && itTags.hasNext()) {
            yes = itTags.next().getKurzbezeichnung().equalsIgnoreCase("soz");
        }
        return yes;
    }

}
