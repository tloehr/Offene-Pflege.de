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

package op.threads;

import op.OPDE;
import op.tools.SYSTools;

import javax.swing.*;

/**
 * @author tloehr
 */
public class HeapStat
        extends Thread {
    private boolean interrupted;
    private JProgressBar jp;
    private JLabel lb;

    /**
     * Creates a new instance of HeapStat
     */
    public HeapStat(JProgressBar p, JLabel l) {
        super();
        this.setName("HeapStat");
        this.interrupted = false;
        this.jp = p;
        this.lb = l;
    }

    public void run() {
        while (!interrupted) {
            // Get current size of heap in bytes
            long heapSize = Runtime.getRuntime().totalMemory();


            // Get amount of free memory within the heap in bytes. This size will increase
            // after garbage collection and decrease as new objects are created.
            long heapFreeSize = Runtime.getRuntime().freeMemory();
            long heapUsedSize = heapSize - heapFreeSize;
            double mbSize = SYSTools.roundScale2(((double) heapSize) / 1048576);
            double mbUsedSize = SYSTools.roundScale2(((double) heapUsedSize) / 1048576);
            double percentUsed = SYSTools.roundScale2(mbUsedSize / mbSize * 100);
            String stat = mbUsedSize + "M/" + mbSize + "M (" + percentUsed + "%)";
            lb.setText(stat);
            jp.setValue((int) percentUsed);
            //OPDE.getLogger().debug("HeapStat running: "+stat);

            try {
                Thread.sleep(5000); // Millisekunden
            } catch (InterruptedException ie) {
                interrupted = true;
                OPDE.getLogger().debug("HeapStat interrupted!");
            }
        }
    }

}
