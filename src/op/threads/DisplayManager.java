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
    public static final String internalClassID = "opde.displaymanager";
    private boolean interrupted, dbAction;
    private JProgressBar jp;
    private JLabel lblMain;
    private FadingLabel lblSub;
    private List<DisplayMessage> messageQ, oldMessages;
    private DisplayMessage progressBarMessage, currentSubMessage;
    private long zyklen = 0, pbIntermediateZyklen = 0;
    private final Color defaultColor = new Color(105, 80, 69);
    private long dbZyklenRest = 0;
    private Icon icon1, icon2, icondead, iconaway, icongone, iconbiohazard;
    private SwingWorker worker;
    private boolean isIndeterminate = false;
    private JPanel pnlIcons;
    private JLabel lblBiohazard, lblDiabetes, lblAllergy, lblWarning;

//    private DateFormat df;

    /**
     * Creates a new instance of HeapStat
     */
    public DisplayManager(JProgressBar p, JLabel lblM, FadingLabel lblS, JPanel pnlIcons) {
        super();
        this.pnlIcons = pnlIcons;
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
        icondead = SYSConst.icon22residentDied;
        iconaway = SYSConst.icon22residentAbsent;
        icongone = SYSConst.icon22residentGone;
        iconbiohazard = SYSConst.icon22biohazard;

        lblBiohazard = new JLabel(iconbiohazard);
        lblBiohazard.setVisible(false);
        lblBiohazard.setOpaque(false);
        lblWarning = new JLabel(SYSConst.icon22warning);
        lblWarning.setVisible(true);
        lblWarning.setOpaque(false);
        lblAllergy = new JLabel(SYSConst.icon22allergy);
        lblAllergy.setVisible(true);
        lblAllergy.setOpaque(false);
        lblDiabetes = new JLabel(SYSConst.icon22diabetes);
        lblDiabetes.setVisible(true);
        lblDiabetes.setOpaque(false);

        pnlIcons.add(lblWarning);
        pnlIcons.add(lblBiohazard);
        pnlIcons.add(lblDiabetes);
        pnlIcons.add(lblAllergy);

//        this.lblDB.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/db.png")));
        lblMain.setText(" ");
        lblSub.setText(" ");
        messageQ = new ArrayList<DisplayMessage>();
        oldMessages = new ArrayList<DisplayMessage>();
    }

    public void setMainMessage(String message) {
        setMainMessage(message, null);
    }

    public void setMainMessage(final String message, final String tooltip) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
//                OPDE.debug("DisplayManager.setMainMessage");
                lblMain.setText(message);
                lblMain.setIcon(null);
                lblMain.setToolTipText(tooltip);
            }
        });
    }

    public void clearAllMessages() {
        setMainMessage(" ");
        messageQ.clear();
        oldMessages.clear();
        processSubMessage();
    }

    public void setIconDead() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                lblMain.setIcon(icondead);
            }
        });
    }

    public void setIconGone() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                lblMain.setIcon(icongone);
            }
        });
    }

    public void setIconBiohazard(final String tooltip) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                lblBiohazard.setVisible(tooltip != null && !tooltip.isEmpty());
                lblBiohazard.setToolTipText(tooltip);
            }
        });
    }

    public void setIconAway() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                lblMain.setIcon(iconaway);
            }
        });
    }

    public void clearAllIcons() {
        lblMain.setIcon(null);
        lblBiohazard.setVisible(false);
    }

    public void setProgressBarMessage(DisplayMessage progressBarMessage) {
        this.progressBarMessage = progressBarMessage;
        jp.setStringPainted(progressBarMessage != null);
//        if (progressBarMessage == null) {
//            jp.setIndeterminate(false);
//        }
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
            messageQ.remove(0); // remove head
            currentSubMessage = nextMessage;
            currentSubMessage.setProcessed(System.currentTimeMillis());
            lblSub.setText(currentSubMessage.getMessage());
//            lblMain.setToolTipText(SYSTools.toHTML(SYSConst.html_div_open + "<b>" + OPDE.lang.getString(internalClassID + ".lastmessage") + ":&nbsp;</b><p>" + DateFormat.getDateTimeInstance().format(new Date()) + "</p><p>" + currentSubMessage.getRawMessage() + "</p>" + SYSConst.html_div_close));
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

//        isIndeterminate = currentSubMessage != null && pbIntermediateZyklen < 40;
//        jp.setIndeterminate(isIndeterminate); // 40x 50ms lang bei neuen Nachrichten leuchten
    }

    private void processProgressBar() {
//        OPDE.debug("DisplayManager.processProgressBar");

        if (progressBarMessage != null) {  //  && zyklen/5%2 == 0 && zyklen % 5 == 0
            if (progressBarMessage.getPercentage() < 0) {
                if (!isIndeterminate) {
                    isIndeterminate = true;
                }
            } else {
                isIndeterminate = false;
//                OPDE.debug("jp.setValue(progressBarMessage.getPercentage())");
                jp.setValue(progressBarMessage.getPercentage());
            }

            jp.setString(progressBarMessage.getRawMessage());
        } else {
            if (jp.getValue() > 0) {
//                OPDE.debug("jp.setValue(0)");
                jp.setValue(0);
                jp.setString(null);
            }
            isIndeterminate = false;

        }

        // Exception ?
//            jp.setIndeterminate(isIndeterminate);

    }

    public static DisplayMessage getLockMessage() {
        return new DisplayMessage(OPDE.lang.getString("misc.msg.lockingexception"), DisplayMessage.IMMEDIATELY, OPDE.WARNING_TIME);
    }

    /**
     * @param text
     * @param operation can be one of deleted, closed, entered, changed, edited
     * @return
     */
    public static DisplayMessage getSuccessMessage(String text, String operation) {
        return new DisplayMessage("&raquo;" + text + " " + "&laquo; " + OPDE.lang.getString("misc.msg.successfully") + " " + OPDE.lang.getString("misc.msg." + operation), DisplayMessage.NORMAL);
    }

    public void run() {
        while (!interrupted) {
            try {
                lblMain.repaint();
                lblSub.repaint();

                processProgressBar();
                processSubMessage();


                zyklen++;
                Thread.sleep(50);
            } catch (InterruptedException ie) {
                interrupted = true;
                System.out.println("DisplayManager interrupted!");
            } catch (Exception e) {
                OPDE.fatal(e);
            }
        }
    }


}
