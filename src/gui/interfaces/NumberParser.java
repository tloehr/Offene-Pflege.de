package gui.interfaces;

import op.tools.SYSTools;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by tloehr on 06.06.15.
 */
public class NumberParser implements TextParser<BigDecimal> {
    NumberFormat nf = DecimalFormat.getNumberInstance();

    @Override
    public BigDecimal parse(String in) {
        in = SYSTools.assimilateDecimalSeparators(in);
        Number num;
        try {
            num = nf.parse(in);
        } catch (ParseException ex) {
            num = null;
        }

        BigDecimal value = null;
        if (num != null) {
            if (num instanceof Long) {
                value = new BigDecimal(num.longValue());
            } else if (num instanceof Double) {
                value = new BigDecimal(num.doubleValue());
            } else if (num instanceof BigDecimal) {
                value = (BigDecimal) num;
            } else {
                value = null;
            }
        }

        return value;
    }
}
