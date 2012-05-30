package op.threads;

import entity.system.SyslogTools;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 28.03.12
 * Time: 16:00
 * To change this template use File | Settings | File Templates.
 */
public class DisplayManager extends Thread {

    private boolean interrupted, dbAction;
    private JProgressBar jp;
    private JLabel lblMain, lblSub, lblDB;
    private List<DisplayMessage> messageQ, oldMessages;
    private DisplayMessage progressBarMessage, currentSubMessage;
    private long zyklen = 0, pbIntermediateZyklen = 0;
    private final Color defaultColor = new Color(105, 80, 69);
    private long dbZyklenRest = 0;

//    private DateFormat df;

    /**
     * Creates a new instance of HeapStat
     */
    public DisplayManager(JProgressBar p, JLabel lblM, JLabel lblS, JLabel lblDB) {
        super();
        setName("DisplayManager");
        interrupted = false;
        dbAction = false;
        jp = p;
        progressBarMessage = null;
        jp.setStringPainted(false);
        jp.setValue(0);
        jp.setMaximum(100);
        lblMain = lblM;
        lblSub = lblS;
        this.lblDB = lblDB;
        lblMain.setText(" ");
        lblSub.setText(" ");
        messageQ = new ArrayList<DisplayMessage>();
        oldMessages = new ArrayList<DisplayMessage>();
    }

    public void setMainMessage(String message) {
        lblMain.setText(message);
    }

    public void clearAllMessages() {
        setMainMessage(null);
        messageQ.clear();
        oldMessages.clear();
        processSubMessage();
    }

    public void setProgressBarMessage(DisplayMessage progressBarMessage) {
        this.progressBarMessage = progressBarMessage;
        jp.setStringPainted(progressBarMessage != null);
    }

    public void addSubMessage(DisplayMessage msg) {
//        OPDE.debug(msg);
        messageQ.add(msg);
        Collections.sort(messageQ);
    }

    public void showLastSubMessageAgain() {
        if (!oldMessages.isEmpty()) {
            DisplayMessage lastMessage = oldMessages.get(oldMessages.size() - 1);
            messageQ.add(lastMessage);
            processSubMessage();
        }
    }

    public void clearSubMessages() {
        messageQ.clear();
        currentSubMessage = null;
        processSubMessage();
    }

    public void setDBActionMessage(boolean action) {
        this.dbAction = action;
        lblDB.setVisible(action); // Damit es sofort sichtbar oder unsichtbar wird.
        dbZyklenRest = (zyklen + 1) % 2; // Damit es sofort blinkt.
    }

    private void processSubMessage() {
        DisplayMessage nextMessage = messageQ.isEmpty() ? null : messageQ.get(0);

        if (nextMessage != null) {
            pbIntermediateZyklen = 0;
            messageQ.remove(0);
            currentSubMessage = nextMessage;
            currentSubMessage.setProcessed(System.currentTimeMillis());
            lblSub.setText(currentSubMessage.getMessage());
            if (currentSubMessage.getPriority() == DisplayMessage.IMMEDIATELY) {
                SyslogTools.addLog("[" + currentSubMessage.getClassname() + "] " + currentSubMessage.getMessage(), SyslogTools.ERROR);
            }
        } else {
            pbIntermediateZyklen++;
            if (currentSubMessage == null || currentSubMessage.isObsolete()) {
                lblSub.setText(" ");
                if (currentSubMessage != null && !oldMessages.contains(currentSubMessage)) {
                    oldMessages.add(currentSubMessage);
                }
                currentSubMessage = null;
            }
        }

        if (currentSubMessage != null && currentSubMessage.getPriority() == DisplayMessage.IMMEDIATELY) {
            jp.setForeground(Color.RED);
            lblSub.setForeground(Color.RED);
        } else {
            lblSub.setForeground(defaultColor);
            jp.setForeground(defaultColor);
        }

        jp.setIndeterminate(currentSubMessage != null && pbIntermediateZyklen < 4); // 4x 500ms lang bei neuen Nachrichten leuchten
    }

    private void processProgressBar() {
        if (progressBarMessage != null) {  //  && zyklen/5%2 == 0 && zyklen % 5 == 0
//            System.out.println(zyklen/5%2);
            jp.setValue(progressBarMessage.getPercentage());
            jp.setString(progressBarMessage.getMessage());
        } else {
            if (jp.getValue() > 0) {
                jp.setValue(0);
                jp.setString(null);
            }
        }
//        } else {
//            if ((progressBarMessage != null && zyklen/5%2 == 1 && zyklen % 5 == 0) || zyklen % 10 == 0 || zyklen == 0) {
//                long heapSize = Runtime.getRuntime().totalMemory();
//                long heapFreeSize = Runtime.getRuntime().freeMemory();
//                long heapUsedSize = heapSize - heapFreeSize;
//                BigDecimal mbSize = new BigDecimal(heapSize).setScale(4).divide(new BigDecimal(1048576), BigDecimal.ROUND_HALF_UP);
//                BigDecimal mbUsedSize = new BigDecimal(heapUsedSize).setScale(4).divide(new BigDecimal(1048576), BigDecimal.ROUND_HALF_UP);
//                BigDecimal percentUsed = mbUsedSize.divide(mbSize, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
//                String stat = mbUsedSize.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "M/" + mbSize.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "M";
//                System.out.println("Memory percent " + percentUsed.toPlainString() + " %");
//                jp.setValue(percentUsed.intValue());
//                jp.setString(stat);
//            }
//        }
    }

    private void processDBMessage() {
        if (dbAction) {
            lblDB.setVisible(zyklen % 2 == dbZyklenRest);
        }
    }


    public void run() {
        while (!interrupted) {

            processDBMessage();
            processProgressBar();
            processSubMessage();

            try {
                zyklen++;
                Thread.sleep(500);
            } catch (InterruptedException ie) {
                interrupted = true;
                System.out.println("DisplayManager interrupted!");
            }
        }
    }


}
