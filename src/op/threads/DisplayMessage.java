package op.threads;


/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 02.02.12
 * Time: 16:30
 * To change this template use File | Settings | File Templates.
 */
public class DisplayMessage implements Comparable<DisplayMessage> {

    public static final int IMMEDIATELY = 10;
    public static final int NORMAL = 30;
    public static final int INDEFFERENT = 40;

    private String message;
    private int priority;
    private long timestamp;
    private long processed;
    private int secondsToShow;
    private int percentage;



    public DisplayMessage(String message) {
        this.message = message;
        this.priority = NORMAL;
        this.timestamp = System.currentTimeMillis();
        this.processed = 0;
        this.secondsToShow = 0;
        this.percentage = 0;
    }

    public DisplayMessage(String message, int secondsToShow) {
        this.message = message;
        this.priority = NORMAL;
        this.timestamp = System.currentTimeMillis();
        this.processed = 0;
        this.secondsToShow = secondsToShow;
        this.percentage = 0;
    }

    public DisplayMessage(String message, int priority, int secondsToShow) {
        this.message = message;
        this.priority = priority;
        this.timestamp = System.currentTimeMillis();
        this.processed = 0;
        this.secondsToShow = secondsToShow;
        this.percentage = 0;
    }

    public DisplayMessage(String message, int priority, long timestamp, long processed, int secondsToShow) {
        this.message = message;
        this.priority = priority;
        this.timestamp = timestamp;
        this.processed = processed;
        this.secondsToShow = secondsToShow;
        this.percentage = 0;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
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

    /**
     *
     * @return true, wenn die Nachricht
     */
    public boolean isObsolete(){
        return !isShowingTillReplacement() && processed + secondsToShow * 1000 <= System.currentTimeMillis();
    }

    public boolean isProcessed(){
        return processed != 0;
    }

    public boolean isShowingTillReplacement(){
        return secondsToShow == 0;
    }

    public int compareTo(DisplayMessage other) {
        int sort = new Integer(priority).compareTo(other.priority);
        if (sort == 0){
            sort = new Long(timestamp).compareTo(other.timestamp);
        }
        return sort;
    }
}
