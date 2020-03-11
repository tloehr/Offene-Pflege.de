package de.offene_pflege.op.tools;

import com.jidesoft.popup.JidePopup;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 23.02.13
 * Time: 15:09
 * To change this template use File | Settings | File Templates.
 */
public  abstract class PopupPanel extends JidePopup {
    public abstract Object getResult();
    public abstract void setStartFocus();
    public abstract boolean isSaveOK();
}
