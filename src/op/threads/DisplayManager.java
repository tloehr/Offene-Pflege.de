package op.threads;

import entity.system.SyslogTools;
import op.OPDE;
import op.tools.FadingLabel;
import op.tools.SYSConst;

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
    private FadingLabel lblMain, lblSub;
    private List<DisplayMessage> messageQ, oldMessages;
    private DisplayMessage progressBarMessage, currentSubMessage;
    private long zyklen = 0, pbIntermediateZyklen = 0;
    private final Color defaultColor = new Color(105, 80, 69);
    private long dbZyklenRest = 0;
    private Icon icon1, icon2, icondead, iconaway, icongone;
    private SwingWorker worker;

//    private DateFormat df;

    /**
     * Creates a new instance of HeapStat
     */
    public DisplayManager(JProgressBar p, FadingLabel lblM, FadingLabel lblS) {
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
        icondead = new ImageIcon(getClass().getResource("/artwork/22x22/cross1.png"));
        iconaway = new ImageIcon(getClass().getResource("/artwork/22x22/person-away.png"));
        icongone = new ImageIcon(getClass().getResource("/artwork/22x22/delete_user.png"));
//        this.lblDB.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/db.png")));
        lblMain.setText(" ");
        lblSub.setText(" ");
        messageQ = new ArrayList<DisplayMessage>();
        oldMessages = new ArrayList<DisplayMessage>();
    }

    public void setMainMessage(String message) {
        lblMain.setToolTipText(message);
        lblMain.setText(message);
        lblMain.setIcon(null);
    }

    public void clearAllMessages() {
        setMainMessage(null);
        messageQ.clear();
        oldMessages.clear();
        processSubMessage();
    }

    public void setIconDead(){
        lblMain.setIcon(icondead);
    }

    public void setIconGone(){
        lblMain.setIcon(icongone);
    }

    public void setIconAway(){
        lblMain.setIcon(iconaway);
    }

    public void setProgressBarMessage(DisplayMessage progressBarMessage) {
        this.progressBarMessage = progressBarMessage;
        jp.setStringPainted(progressBarMessage != null);
    }

    public void addSubMessage(DisplayMessage msg) {
        messageQ.add(msg);
        Collections.sort(messageQ);
    }

//    public void showLastSubMessageAgain() {
//        if (!oldMessages.isEmpty()) {
//            DisplayMessage lastMessage = oldMessages.get(oldMessages.size() - 1);
//            messageQ.add(lastMessage);
//            processSubMessage();
//        }
//    }

    public void clearSubMessages() {
        messageQ.clear();
        currentSubMessage = null;
        processSubMessage();
    }

    public void setDBActionMessage(boolean action) {
//        if (this.dbAction == action) {
//            return;
//        }
//        this.dbAction = action;
//
//        if (action) {
//            worker = new SwingWorker() {
//                @Override
//                protected Object doInBackground() throws Exception {
//                    boolean visible = true;
//                    while (!isCancelled()) {
//                        lblDB.setIcon(visible ? icon1 : icon2);
//                        lblDB.repaint();
//                        OPDE.debug("lbldb: "+visible);
//                        visible = !visible;
//                        Thread.sleep(150);
//                    }
//
//                    return null;
//                }
//            };
//            worker.execute();
//        } else {
//            worker.cancel(true);
//        }
//        dbZyklenRest = (zyklen + 1) % 2; // Damit es sofort blinkt.
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
                lblSub.setText(null);
                if (currentSubMessage != null && !oldMessages.contains(currentSubMessage)) {
                    oldMessages.add(currentSubMessage);
                }
                currentSubMessage = null;
            }
        }

        if (currentSubMessage != null && currentSubMessage.getPriority() == DisplayMessage.IMMEDIATELY) {
            jp.setForeground(Color.RED);
            lblSub.setForeground(Color.RED);
        } else if (currentSubMessage != null && currentSubMessage.getPriority() == DisplayMessage.WARNING) {
            jp.setForeground(SYSConst.darkorange);
            lblSub.setForeground(SYSConst.darkorange);
        } else {
            lblSub.setForeground(defaultColor);
            jp.setForeground(defaultColor);
        }

        jp.setIndeterminate(currentSubMessage != null && pbIntermediateZyklen < 40); // 40x 50ms lang bei neuen Nachrichten leuchten
    }

    private void processProgressBar() {
        if (progressBarMessage != null) {  //  && zyklen/5%2 == 0 && zyklen % 5 == 0

            if (progressBarMessage.getPercentage() < 0) {
                jp.setIndeterminate(true);
            } else {
                jp.setIndeterminate(false);
                jp.setValue(progressBarMessage.getPercentage());
            }

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

//    private void processDBMessage() {
//        if (dbAction) {
//            lblDB.setVisible(zyklen % 2 == dbZyklenRest);
//        }
//    }


    public static DisplayMessage getLockMessage(){
        return new DisplayMessage(OPDE.lang.getString("misc.msg.lockingexception"), DisplayMessage.IMMEDIATELY, 5);
    }

    public void run() {
        while (!interrupted) {

            lblMain.repaint();
            lblSub.repaint();

            processProgressBar();
            processSubMessage();

            try {
                zyklen++;
                Thread.sleep(50);
            } catch (InterruptedException ie) {
                interrupted = true;
                System.out.println("DisplayManager interrupted!");
            }
        }
    }


}
