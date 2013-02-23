package op.tools;

import javax.swing.*;
import javax.swing.text.JTextComponent;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 23.02.13
 * Time: 12:57
 * To change this template use File | Settings | File Templates.
 */
public class NonEmptyTextfieldVerifier extends InputVerifier {


    @Override
    public boolean verify(JComponent input) {
        return !((JTextComponent) input).getText().trim().isEmpty();
    }


}
