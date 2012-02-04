package op.vorgang;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 24.10.11
 * Time: 15:25
 * To change this template use File | Settings | File Templates.
 */
public class FrmVorgang extends JFrame {
    public FrmVorgang() throws HeadlessException {
        setSize(1024, 768);
        getContentPane().add(new PnlVorgang(null, null, this, null));
    }


}
