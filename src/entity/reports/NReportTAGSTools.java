/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.reports;

import entity.EntityTools;
import op.OPDE;
import op.tools.GUITools;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.jdesktop.swingx.VerticalLayout;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author tloehr
 */
public class NReportTAGSTools {

    public static ListCellRenderer getPBerichtTAGSRenderer() {
//        final int v = verbosity;
        return new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean isSelected, boolean cellHasFocus) {
                String text;
                if (o == null) {
                    text = SYSTools.toHTML("<i>Keine Auswahl</i>");
                } else if (o instanceof NReportTAGS) {
                    NReportTAGS tag = (NReportTAGS) o;
                    text = tag.getBezeichnung() + " ("+tag.getKurzbezeichnung()+")";
                } else {
                    text = o.toString();
                }
                return new DefaultListCellRenderer().getListCellRendererComponent(jList, text, i, isSelected, cellHasFocus);
            }
        };
    }

    /**
     * Erstellt ein JMenu bestehend aus Checkboxen. Für jede aktive PBerichtTag jeweils eine.
     * Wenn man die anklickt, wird eine Markierung zum Bericht hinzugefügt. Dieses Menü
     * wird in PnlReport verwendet. Als Kontextmenü für die einzelnen Berichtszeilen.
     *
     * @param bericht Der Bericht, für den das Menü erzeugt werden soll.
     *                Je nachdem, welche Tags diesem Bericht schon zugewiesen sind, werden die Checkboxen bereits angeklickt oder auch nicht.
     *                Für das Menü wird ein Listener definiert, der weitere Tags setzt oder entfernt.
     * @return das vorbereitete Menü
     */
    public static JMenu createMenuForTags(NReport bericht) {
        final NReport finalbericht = bericht;
        EntityManager em = OPDE.createEM();
        Query query = em.createNamedQuery("PBerichtTAGS.findAllActive");
        ArrayList<NReportTAGS> tags = new ArrayList(query.getResultList());

        JMenu menu = new JMenu("Text-Markierungen");
        Iterator<NReportTAGS> itTags = tags.iterator();
        while (itTags.hasNext()) {
            final NReportTAGS tag = itTags.next();
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
    public static JPanel createCheckBoxPanelForTags(ItemListener listener, Collection<NReportTAGS> preselect, LayoutManager layout) {
        EntityManager em = OPDE.createEM();
        MouseAdapter ma = GUITools.getHyperlinkStyleMouseAdapter();
        Query query = em.createNamedQuery("PBerichtTAGS.findAllActive");
        ArrayList<NReportTAGS> tags = new ArrayList(query.getResultList());
        JPanel panel = new JPanel(layout);
        for (NReportTAGS tag : tags) {
            JCheckBox cb = new JCheckBox(tag.getBezeichnung());
            cb.setForeground(tag.getColor());
            if (tag.isBesonders()) {
                cb.setFont(SYSConst.ARIAL14BOLD);
            }
            cb.putClientProperty("UserObject", tag);

            cb.setSelected(preselect.contains(tag));
            cb.addItemListener(listener);
            cb.addMouseListener(ma);

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
     * @return das Panel zur weiteren Verwendung.
     */
    public static JPanel getCheckBoxPanelForTags(ItemListener listener, Collection<NReportTAGS> preselect) {
        EntityManager em = OPDE.createEM();
        JPanel panel = new JPanel(new VerticalLayout());
        MouseAdapter ma = GUITools.getHyperlinkStyleMouseAdapter();
        Query query = em.createNamedQuery("PBerichtTAGS.findAllActive");
        ArrayList<NReportTAGS> tags = new ArrayList(query.getResultList());
        Iterator<NReportTAGS> itTags = tags.iterator();
        while (itTags.hasNext()) {
            NReportTAGS tag = itTags.next();
            JCheckBox cb = new JCheckBox(tag.getBezeichnung());
            cb.setBackground(Color.WHITE);
            cb.setForeground(tag.getColor());
            if (tag.isBesonders()) {
                cb.setFont(new Font("Lucida Grande", Font.BOLD, 13));
            }
            cb.putClientProperty("UserObject", tag);

            cb.setSelected(preselect.contains(tag));
            cb.addItemListener(listener);
            cb.addMouseListener(ma);

            panel.add(cb);
        }
        em.close();
        return panel;
    }

    /**
     * Kleine Hilfsmethode, die ich brauche um festzustellen ob ein bestimmter bericht
     * ein Sozial Bericht ist.
     */
    public static boolean isSozial(NReport bericht) {
        Iterator<NReportTAGS> itTags = bericht.getTags().iterator();
        boolean yes = false;
        while (!yes && itTags.hasNext()) {
            yes = itTags.next().getKurzbezeichnung().equalsIgnoreCase("soz");
        }
        return yes;
    }

}
