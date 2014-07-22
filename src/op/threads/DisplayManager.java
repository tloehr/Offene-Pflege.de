package op.threads;

import entity.files.SYSFilesTools;
import entity.system.SYSLoginTools;
import entity.system.SYSPropsTools;
import op.OPDE;
import op.tools.FadingLabel;
import op.tools.Pair;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.joda.time.DateTime;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 28.03.12
 * Time: 16:00
 * To change this template use File | Settings | File Templates.
 */
public class DisplayManager extends Thread {
    public static final String internalClassID = "opde.displaymanager";
    private final Closure timeoutAction;
    private final JProgressBar pbTimeout;
    private boolean interrupted;
    private JProgressBar jp;
    private JLabel lblMain;
    private FadingLabel lblSub;
    private MessageQ messageQ;
    // a pair for the text and the value for the progressbar.
    private Pair<String, Integer> progressBarMessage;
    private final Color defaultColor = new Color(105, 80, 69);
    private Icon icondead, iconaway, icongone, iconbiohazard;
    //    private SwingWorker worker;
//    private boolean isIndeterminate = false;
    //    private JPanel pnlIcons;
    private JLabel lblBiohazard, lblDiabetes, lblAllergy, lblWarning;
    private long step = 0;
    private int lastMinute;
    private long lastoperation; // used for the timeout function to automatically log out idle users
    private boolean pbIsInUse; // for optimization. the jp.setString() was called a trillion times without any need.
//    private int TIMEOUTMINS;

//    private DateFormat df;

    /**
     * Creates a new instance of HeapStat
     */
    public DisplayManager(JProgressBar p, JLabel lblM, FadingLabel lblS, JPanel pnlIcons, JProgressBar pbTimeout, Closure timeoutAction) {
        super();
        this.pbTimeout = pbTimeout;
        progressBarMessage = new Pair<String, Integer>("", -1);
        this.timeoutAction = timeoutAction;
        setName("DisplayManager");
        touch();
        interrupted = false;
        jp = p;
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
        lblWarning.setVisible(false);
        lblWarning.setOpaque(false);
        lblAllergy = new JLabel(SYSConst.icon22allergy);
        lblAllergy.setVisible(false);
        lblAllergy.setOpaque(false);
        lblDiabetes = new JLabel(SYSConst.icon22diabetes);
        lblDiabetes.setVisible(false);
        lblDiabetes.setOpaque(false);

        pnlIcons.add(lblWarning);
        pnlIcons.add(lblBiohazard);
        pnlIcons.add(lblDiabetes);
        pnlIcons.add(lblAllergy);

        lblMain.setText(" ");
        lblSub.setText(" ");
        messageQ = new MessageQ();

    }

    public void setMainMessage(String message) {
        setMainMessage(message, null);
    }

    public void setMainMessage(final String message, final String tooltip) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
//                OPDE.debug("DisplayManager.setMainMessage");
                lblMain.setText(SYSTools.xx(message));
                lblMain.setIcon(null);
                lblMain.setToolTipText(tooltip);
            }
        });
    }

    public void clearAllMessages() {
        setMainMessage(" ");
        setIconBiohazard(null);
        setIconDiabetes(null);
        setIconWarning(null);
        setIconAllergy(null);
        messageQ.clear();
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

    public void setIconWarning(final String tooltip) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                lblWarning.setVisible(tooltip != null && !tooltip.isEmpty());
                lblWarning.setToolTipText(tooltip);
            }
        });
    }

    public void setIconAllergy(final String tooltip) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                lblAllergy.setVisible(tooltip != null && !tooltip.isEmpty());
                lblAllergy.setToolTipText(tooltip);
            }
        });
    }

    public void setIconDiabetes(final String tooltip) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                lblDiabetes.setVisible(tooltip != null && !tooltip.isEmpty());
                lblDiabetes.setToolTipText(tooltip);
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
        lblWarning.setVisible(false);
        lblDiabetes.setVisible(false);
        lblAllergy.setVisible(false);
    }

    public void setProgressBarMessage(DisplayMessage pbMessage) {
        synchronized (progressBarMessage) {
            if (pbMessage == null) {
                progressBarMessage.setFirst("");
                progressBarMessage.setSecond(-1);
                jp.setStringPainted(false);
            } else {

                progressBarMessage.setFirst(pbMessage.getMessage() == null ? "" : pbMessage.getMessage());
                progressBarMessage.setSecond(pbMessage.getPercentage());
                jp.setStringPainted(true);
            }
        }
    }

    public void setProgressBarMessage(String message, int percentage) {
        synchronized (progressBarMessage) {
            progressBarMessage.setFirst(message == null ? "" : message);
            progressBarMessage.setSecond(percentage);
            jp.setStringPainted(true);
        }
    }
//
//    public synchronized void clearProgressBarMessage() {
//        synchronized (progressBarMessage) {
//            progressBarMessage.setFirst("");
//            progressBarMessage.setSecond(-1);
//            jp.setStringPainted(false);
//        }
//    }

    public void addSubMessage(String text) {
        DisplayMessage msg = new DisplayMessage(text);
        addSubMessage(msg);
    }

    public void addSubMessage(DisplayMessage msg) {
        messageQ.add(msg);
    }

    public void clearSubMessages() {
        messageQ.clear();
        processSubMessage();
    }

    private void processSubMessage() {
        synchronized (messageQ) {

            if (!messageQ.isEmpty()) {
                if (messageQ.getHead().isObsolete()) {
                    messageQ.next();
                } else if (!messageQ.getHead().isProcessed()) {
                    messageQ.getHead().setProcessed();
                    lblSub.setText(SYSTools.toHTMLForScreen(messageQ.getHead().getMessage()));
                } else if (messageQ.hasNextMessage() && messageQ.getNextMessage().isUrgent()) {
                    messageQ.next();
                } else if (messageQ.getHead().isShowingTillReplacement() && messageQ.hasNextMessage()) {
                    messageQ.next();
                }
            } else {
                lblSub.setText(null);
            }


            // Coloring
            if (!messageQ.isEmpty() && messageQ.getHead().getPriority() == DisplayMessage.IMMEDIATELY) {
                jp.setForeground(Color.RED);
                lblSub.setForeground(Color.RED);
            } else if (!messageQ.isEmpty() && messageQ.getHead().getPriority() == DisplayMessage.WARNING) {
                jp.setForeground(SYSConst.darkorange);
                lblSub.setForeground(SYSConst.darkorange);
            } else {
                lblSub.setForeground(defaultColor);
                jp.setForeground(defaultColor);
            }
        }
    }

    private void check4MaintenanceMode() {
        if (OPDE.getLogin() == null) {
            return;
        }
        int minute = new DateTime().getMinuteOfHour();
        if (minute != lastMinute) {
            lastMinute = minute;
            if (SYSPropsTools.isTrue(SYSPropsTools.KEY_MAINTENANCE_MODE, null)) {
                SYSFilesTools.print(SYSTools.xx("maintenance.mode.sorry"), false);
                SYSLoginTools.logout();
                System.exit(0);
            }
        }
    }

    private void processProgressBar() {
        synchronized (progressBarMessage) {
            if (!progressBarMessage.getFirst().isEmpty() || progressBarMessage.getSecond() >= 0) {  //  && zyklen/5%2 == 0 && zyklen % 5 == 0
//                if (progressBarMessage.getSecond() < 0) {
//                    if (!jp.isIndeterminate()) {
//                        jp.setIndeterminate(true);
//                    }
//                } else {
//                    if (jp.isIndeterminate()) {
//                        jp.setIndeterminate(false);
//                    }
//                    jp.setValue(progressBarMessage.getSecond());
//                }
                if (progressBarMessage.getSecond() >= 0){
                    jp.setValue(progressBarMessage.getSecond());
                }
                jp.setString(progressBarMessage.getFirst());
                pbIsInUse = true;
            } else {

                if (progressBarMessage.getSecond() < 0 && pbIsInUse) {
//                    jp.setIndeterminate(false);
                    jp.setValue(0);
                    jp.setString(null);
                }

                if (jp.getValue() > 0) {
//                    if (jp.isIndeterminate()) {
//                        jp.setIndeterminate(false);
//                    }
                    jp.setValue(0);
                    jp.setString(null);
                }
                pbIsInUse = false;
            }
        }

    }

    public void touch() {
        lastoperation = System.currentTimeMillis();
    }

    public static DisplayMessage getLockMessage() {
        return new DisplayMessage(SYSTools.xx("misc.msg.lockingexception"), DisplayMessage.IMMEDIATELY, OPDE.WARNING_TIME);
    }

    /**
     * @param text
     * @param operation can be one of deleted, closed, entered, changed, edited
     * @return
     */
    public static DisplayMessage getSuccessMessage(String text, String operation) {
        return new DisplayMessage("&raquo;" + text + " " + "&laquo; " + SYSTools.xx("misc.msg.successfully") + " " + SYSTools.xx("misc.msg." + operation), DisplayMessage.NORMAL);
    }

    @Override
    public void run() {
        while (!interrupted) {
            try {
                step++;

//                lblMain.repaint();
//                lblSub.repaint();

                processProgressBar();
                processSubMessage();
                check4MaintenanceMode();

                int timeoutmins = OPDE.getTimeout();
                if (timeoutmins == 0) {
                    pbTimeout.setValue(0);
                    pbTimeout.setToolTipText(SYSTools.xx("misc.msg.auto.logoff") + " " + SYSTools.xx("misc.msg.turned.off"));
                }
                if (timeoutmins > 0) {
                    pbTimeout.setToolTipText(SYSTools.xx("misc.msg.auto.logoff") + " " + SYSTools.xx("misc.msg.in") + " " + timeoutmins + " " + SYSTools.xx("misc.msg.Minute(s)"));
                    // Timeout functions
                    if (OPDE.getLogin() != null) {
                        long timeoutPeriodInMillis = timeoutmins * 60 * 1000;
                        long millisOfTimeout = lastoperation + timeoutPeriodInMillis;
                        long millisToGo = millisOfTimeout - System.currentTimeMillis();
                        pbTimeout.setMaximum(new BigDecimal(timeoutmins * 60).intValue());
                        pbTimeout.setValue(new BigDecimal(millisToGo / 1000).intValue());
                    } else {
                        pbTimeout.setValue(0);
                    }

                    if (OPDE.getLogin() != null && System.currentTimeMillis() > lastoperation + (timeoutmins * 60 * 1000)) {
                        timeoutAction.execute(null);
                    }
                }
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
