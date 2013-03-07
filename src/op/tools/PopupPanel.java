package op.tools;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 23.02.13
 * Time: 15:09
 * To change this template use File | Settings | File Templates.
 */
public  abstract class PopupPanel extends javax.swing.JPanel {
    public abstract Object getResult();
    public abstract void setStartFocus();
    public abstract boolean isSaveOK();
}
