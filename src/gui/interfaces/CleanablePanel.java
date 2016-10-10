/*
 * OffenePflege
 * Copyright (C) 2006-2012 Torsten Löhr
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

package gui.interfaces;

import op.OPDE;

import javax.swing.*;

/**
 * @author tloehr
 */
public abstract class CleanablePanel extends javax.swing.JPanel {
    protected String helpkey = null;
    protected String internalClassID = null;
    protected JDialog currentEditor;

    public CleanablePanel(String internalClassID) {
        super();
        this.internalClassID = internalClassID;
        helpkey = OPDE.getAppInfo().getInternalClasses().containsKey(internalClassID) ? OPDE.getAppInfo().getInternalClasses().get(internalClassID).getHelpurl() : null;
        OPDE.getDisplayManager().setMainMessage(internalClassID);
    }

    public void cleanup() {
        //  https://github.com/tloehr/Offene-Pflege.de/issues/62
        // closes an open modal dialog, if necessary.
        // when the timeout occurs
        if (currentEditor != null && currentEditor.isShowing()) {
            currentEditor.dispose();
        }
    }

    public void reload() {
        OPDE.getEMF().getCache().evictAll();
    }

    public String getInternalClassID() {
        return internalClassID;
    }

    public String getHelpKey() {
        return helpkey;
    }
}
