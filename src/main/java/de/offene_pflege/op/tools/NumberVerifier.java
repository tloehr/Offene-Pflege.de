package de.offene_pflege.op.tools;

import lombok.AllArgsConstructor;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.math.BigDecimal;

@AllArgsConstructor
public class NumberVerifier extends InputVerifier {
    public static BigDecimal MAX = BigDecimal.valueOf(Integer.MAX_VALUE);
    BigDecimal min, max;
    boolean only_integer;

    public NumberVerifier() {
        min = BigDecimal.ONE;
        max = MAX;
        only_integer = true;
    }

    @Override
    public boolean verify(JComponent input) {
        String text = ((JTextComponent) input).getText();
        try {
            BigDecimal value = new BigDecimal(text);
            if (only_integer && !isInteger(value)) return false;
            if (min.compareTo(value) > 0 || value.compareTo(max) > 0) return false;
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isInteger(BigDecimal bd) {
        return bd.stripTrailingZeros().scale() <= 0;
    }


}