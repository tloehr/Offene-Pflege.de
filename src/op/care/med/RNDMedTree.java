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
 * 
 */

package op.care.med;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import op.OPDE;

/**
 *
 * @author tloehr
 */
public class RNDMedTree extends JLabel implements TreeCellRenderer  {
    Icon openIcon;
    Icon closedIcon;
    Icon leafIcon;
    JPopupMenu menu = new JPopupMenu();
    
    /** Creates a new instance of RNDMedTree */
    public RNDMedTree() {
        DefaultTreeCellRenderer dtcr = new DefaultTreeCellRenderer();
        openIcon = dtcr.getOpenIcon();
        closedIcon = dtcr.getClosedIcon();
        leafIcon = dtcr.getLeafIcon();
        
        // Create and add a menu item
        JMenuItem item = new JMenuItem("Item Label");
        //item.addActionListener(actionListener);
        menu.add(item);
        
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                OPDE.getLogger().debug(evt);
                if (evt.isPopupTrigger()) {
                    OPDE.getLogger().debug(evt.getButton());
                    menu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
            public void mouseReleased(MouseEvent evt) {
                OPDE.getLogger().debug(evt);
                if (evt.isPopupTrigger()) {
                    menu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        });
        
    }
    
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        setText(value.toString());
        setToolTipText("test");
        setOpaque(selected);
        
        if (expanded) {
            setIcon(openIcon);
        } else if (leaf) {
            setIcon(leafIcon);
        } else {
            setIcon(closedIcon);
        }
        
        return this;
    }
    
}
