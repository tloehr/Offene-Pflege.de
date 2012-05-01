package op.threads;

import javax.swing.*;
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

    private boolean interrupted;
    private JProgressBar jp;
    private JLabel lblMain, lblSub;
    private List<DisplayMessage> messageQ, oldMessages;
    private DisplayMessage progressBarMessage, currentSubMessage;
    private long zyklen = 0, pbIntermediateZyklen = 0;

//    private DateFormat df;

    /**
     * Creates a new instance of HeapStat
     */
    public DisplayManager(JProgressBar p, JLabel lblM, JLabel lblS) {
        super();
        setName("DisplayManager");
        interrupted = false;
        jp = p;
        progressBarMessage = null;
        jp.setStringPainted(false);
        jp.setValue(0);
        lblMain = lblM;
        lblSub = lblS;
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
        processSubMessage();
    }

    public void setProgressBarMessage(DisplayMessage progressBarMessage) {
        this.progressBarMessage = progressBarMessage;
        jp.setStringPainted(progressBarMessage != null);
    }

    public void addSubMessage(DisplayMessage msg) {
        messageQ.add(msg);
        Collections.sort(messageQ);
    }

    private void processSubMessage() {
        DisplayMessage nextMessage = messageQ.isEmpty() ? null : messageQ.get(0);

        if (nextMessage != null) {
            pbIntermediateZyklen = 0;
            oldMessages.add(currentSubMessage);
            messageQ.remove(0);
            currentSubMessage = nextMessage;
            currentSubMessage.setProcessed(System.currentTimeMillis());
            lblSub.setText(currentSubMessage.getMessage());
            // df.format(new Date()) + ": " +
        } else {
            pbIntermediateZyklen++;
            if (currentSubMessage == null || currentSubMessage.isObsolete()) {
                lblSub.setText(" ");
                currentSubMessage = null;
            }
        }

        jp.setIndeterminate(currentSubMessage != null && pbIntermediateZyklen < 4); // 4x 500ms lang bei neuen Nachrichten leuchten
    }

    private void processProgressBar() {
        if (progressBarMessage != null) {  //  && zyklen/5%2 == 0 && zyklen % 5 == 0
//            System.out.println(zyklen/5%2);
            jp.setValue(progressBarMessage.getPercentage());
            jp.setString(progressBarMessage.getMessage());
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


    public void run() {
        while (!interrupted) {

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
