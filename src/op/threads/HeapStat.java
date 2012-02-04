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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author tloehr
 */
public class HeapStat extends Thread {
    private boolean interrupted;
    private JProgressBar jp;
    private JLabel lblMain, lblSub;
    private List<DisplayMessage> messageQ;
    private DisplayMessage progressBarMessage, currentSubMessage;
    private long zyklen = 0;

    /**
     * Creates a new instance of HeapStat
     */
    public HeapStat(JProgressBar p, JLabel lblMain, JLabel lblSub) {
        super();
        this.setName("HeapStat");
        this.interrupted = false;
        this.jp = p;
        progressBarMessage = null;
        jp.setStringPainted(true);
        this.lblMain = lblMain;
        this.lblSub = lblSub;
        this.lblMain.setText(null);
        this.lblSub.setText(null);
        messageQ = new ArrayList<DisplayMessage>();
    }

    public void setMainMessage(String message) {
//        lblMain.setText(message);

        SYSTools.fadein(lblMain, message);
//        msg.setProcessed(System.currentTimeMillis());
    }

    public void setProgressBarMessage(DisplayMessage progressBarMessage) {
        this.progressBarMessage = progressBarMessage;
    }

    public void addSubMessage(DisplayMessage msg) {
        messageQ.add(msg);
        Collections.sort(messageQ);
    }

    private void processSubMessage() {
        if (messageQ.isEmpty() && currentSubMessage == null) {
            return;
        }

        if (currentSubMessage == null) {
            currentSubMessage = messageQ.get(0);
            messageQ.remove(currentSubMessage);
            currentSubMessage.setProcessed(System.currentTimeMillis());
            SYSTools.fadein(lblSub, currentSubMessage.getMessage());
        }

        if (currentSubMessage.getProcessed() + currentSubMessage.getSecondsToShow() * 1000 > System.currentTimeMillis()) {
            currentSubMessage = null;
            SYSTools.fadeout(lblSub);
        }
    }


    private void processProgressBar() {
        if (progressBarMessage != null) {
            jp.setValue(progressBarMessage.getPercentage());
            jp.setString(progressBarMessage.getMessage());
        } else {
            if (zyklen % 10 == 0) {
                long heapSize = Runtime.getRuntime().totalMemory();
                long heapFreeSize = Runtime.getRuntime().freeMemory();
                long heapUsedSize = heapSize - heapFreeSize;
                double mbSize = SYSTools.roundScale2(((double) heapSize) / 1048576);
                double mbUsedSize = SYSTools.roundScale2(((double) heapUsedSize) / 1048576);
                double percentUsed = SYSTools.roundScale2(mbUsedSize / mbSize * 100);
                String stat = mbUsedSize + "M/" + mbSize + "M";

                jp.setValue((int) percentUsed);
                jp.setString(stat);
            }
        }
    }


    public void run() {
        while (!interrupted) {

            processProgressBar();
            processSubMessage();

            try {
                zyklen++;
                Thread.sleep(500); // Millisekunden
            } catch (InterruptedException ie) {
                interrupted = true;
                OPDE.debug("HeapStat interrupted!");
            }
        }
    }

}
