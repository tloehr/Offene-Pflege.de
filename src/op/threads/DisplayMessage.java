package op.threads;


import op.OPDE;
import op.tools.SYSConst;
import op.tools.SYSTools;

import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 02.02.12
 * Time: 16:30
 * To change this template use File | Settings | File Templates.
 */
public class DisplayMessage implements Comparable<DisplayMessage> {

    public static final short IMMEDIATELY = 10;
    public static final short WARNING = 20;
    public static final short NORMAL = 30;
    public static final short INDEFFERENT = 40;

    public static final int WAIT_TIL_NEXT_MESSAGE = 0;

    private String message;
    private short priority;
    private long timestamp;
    private long processed;
    private int secondsToShow;
    private int percentage;
    private String uid;
    private String classname;


    public DisplayMessage(String message) {
        String title = SYSTools.catchNull(message);
        try {
            title = SYSTools.xx(message);
        } catch (Exception e){
            // ok, its not a langbundle key
        }

        this.message = title;
        this.priority = NORMAL;
        this.timestamp = System.currentTimeMillis();
        this.processed = 0;
        this.secondsToShow = OPDE.INFO_TIME;
        this.percentage = 0;
        uid = UUID.randomUUID().toString();
        this.classname = "";
    }

    public DisplayMessage(String message, int secondsToShow) {

        String title = SYSTools.catchNull(message);
        try {
            title = SYSTools.xx(message);
        } catch (Exception e){
            // ok, its not a langbundle key
        }

        this.message = title;
        this.priority = NORMAL;
        this.timestamp = System.currentTimeMillis();
        this.processed = 0;
        this.secondsToShow = secondsToShow;
        this.percentage = 0;
        uid = UUID.randomUUID().toString();
        this.classname = "";
    }

    public DisplayMessage(String message, int progress, int max) {
        String title = SYSTools.catchNull(message);
        try {
            title = SYSTools.xx(message);
        } catch (Exception e){
            // ok, its not a langbundle key
        }

        this.message = title;
        this.priority = NORMAL;
        this.timestamp = System.currentTimeMillis();
        this.processed = 0;
        this.secondsToShow = 0;
        this.percentage = new Double(new Double(progress) / new Double(max) * 100d).intValue();
        uid = UUID.randomUUID().toString();
        this.classname = "";
    }

    public DisplayMessage(String message, short priority, int secondsToShow) {
        String title = SYSTools.catchNull(message);
        try {
            title = SYSTools.xx(message);
        } catch (Exception e){
            // ok, its not a langbundle key
        }

        this.message = title;
        this.priority = priority;
        this.timestamp = System.currentTimeMillis();
        this.processed = 0;
        this.secondsToShow = secondsToShow;
        this.percentage = 0;
        uid = UUID.randomUUID().toString();
        this.classname = "";
    }

    public DisplayMessage(String message, short priority) {
        String title = SYSTools.catchNull(message);
        try {
            title = SYSTools.xx(message);
        } catch (Exception e){
            // ok, its not a langbundle key
        }

        this.message = title;
        this.priority = priority;
        this.timestamp = System.currentTimeMillis();
        this.processed = 0;
        if (priority == IMMEDIATELY) {
            secondsToShow = OPDE.ERROR_TIME;
        } else if (priority == WARNING) {
            secondsToShow = OPDE.WARNING_TIME;
        } else {
            secondsToShow = OPDE.INFO_TIME;
        }
        this.percentage = 0;
        uid = UUID.randomUUID().toString();
        this.classname = "";
    }

    public DisplayMessage(String message, String classname) {
        String title = SYSTools.catchNull(message);
        try {
            title = SYSTools.xx(message);
        } catch (Exception e){
            // ok, its not a langbundle key
        }
        this.message = title;
        this.priority = IMMEDIATELY;
        this.timestamp = System.currentTimeMillis();
        this.processed = 0;
        this.secondsToShow = OPDE.ERROR_TIME;
        this.percentage = 0;
        uid = UUID.randomUUID().toString();
        this.classname = classname;
    }

    public String getMessage() {
        return message; //SYSTools.toHTML(SYSConst.html_div_open + message + SYSConst.html_div_close);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public short getPriority() {
        return priority;
    }

    public void setPriority(short priority) {
        this.priority = priority;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getProcessed() {
        return processed;
    }


    public void setProcessed(long processed) {
        this.processed = processed;
    }

    public void setProcessed() {
            this.processed = System.currentTimeMillis();
        }

    public int getSecondsToShow() {
        return secondsToShow;
    }

    public void setSecondsToShow(int secondsToShow) {
        this.secondsToShow = secondsToShow;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public boolean isUrgent(){
        return priority == IMMEDIATELY;
    }

    public String getUID() {
        return uid;
    }

    /**
     * @return true, wenn die Nachricht
     */
    public boolean isObsolete() {
        return isProcessed() && !isShowingTillReplacement() && processed + secondsToShow * 1000 <= System.currentTimeMillis();
    }

    public boolean isProcessed() {
        return processed > 0;
    }

    public boolean isShowingTillReplacement() {
        return secondsToShow == 0;
    }

    public int compareTo(DisplayMessage other) {
        int sort = new Short(priority).compareTo(other.priority);
        if (sort == 0) {
            sort = new Long(timestamp).compareTo(other.timestamp);
        }
        return sort;
    }

    @Override
    public boolean equals(Object o) {
        return ((DisplayMessage) o).getUID().equals(uid);
    }

    @Override
    public String toString() {
        return "DisplayMessage{" +
                "message='" + message + '\'' +
                ", priority=" + priority +
                ", timestamp=" + timestamp +
                ", processed=" + processed +
                ", secondsToShow=" + secondsToShow +
                ", percentage=" + percentage +
                ", uid='" + uid + '\'' +
                '}';
    }
}
