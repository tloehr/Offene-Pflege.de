package op.threads;

import op.OPDE;
import op.tools.FadingLabel;
import op.tools.Pair;
import op.tools.SYSConst;
import op.tools.SYSTools;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 28.03.12
 * Time: 16:00
 * To change this template use File | Settings | File Templates.
 */
public class DisplayManager extends Thread {
    public static final String internalClassID = "opde.displaymanager";
    private boolean interrupted;
    private JProgressBar jp;
    private JLabel lblMain;
    private FadingLabel lblSub;
    private MessageQ messageQ;
    private Pair<String, Integer> progressBarMessage;
    private final Color defaultColor = new Color(105, 80, 69);
    private Icon icondead, iconaway, icongone, iconbiohazard;
//    private SwingWorker worker;
    private boolean isIndeterminate = false;
//    private JPanel pnlIcons;
    private JLabel lblBiohazard, lblDiabetes, lblAllergy, lblWarning;


//    private DateFormat df;

    /**
     * Creates a new instance of HeapStat
     */
    public DisplayManager(JProgressBar p, JLabel lblM, FadingLabel lblS, JPanel pnlIcons) {
        super();
        progressBarMessage = new Pair<String, Integer>("", -1);
//        this.pnlIcons = pnlIcons;
        setName("DisplayManager");
        interrupted = false;
//        dbAction = false;
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

//        this.lblDB.setIcon(new ImageIcon(getClass().getResource("/artwork/22x22/db.png")));
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
                lblMain.setText(message);
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

    private void processProgressBar() {
        synchronized (progressBarMessage) {
            if (!progressBarMessage.getFirst().isEmpty() || progressBarMessage.getSecond() >= 0) {  //  && zyklen/5%2 == 0 && zyklen % 5 == 0
                if (progressBarMessage.getSecond() < 0) {
                    if (!isIndeterminate) {
                        isIndeterminate = true;
                    }
                } else {
                    isIndeterminate = false;
                    jp.setValue(progressBarMessage.getSecond());
                }
                jp.setString(progressBarMessage.getFirst());
            } else {
                if (jp.getValue() > 0) {
                    jp.setValue(0);
                    jp.setString(null);
                }
                isIndeterminate = false;
            }
        }

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

    @Override
    public void run() {
        while (!interrupted) {
            try {

                lblMain.repaint();
                lblSub.repaint();

                processProgressBar();
                processSubMessage();

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
