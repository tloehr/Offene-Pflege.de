package gui.parser;

import op.tools.SYSTools;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by tloehr on 06.06.15.
 */
public class CurrencyParser implements TextParser<BigDecimal> {
    NumberFormat nf = DecimalFormat.getCurrencyInstance();

    @Override
    public BigDecimal parse(String in) throws ParseException {
        Number num;

        num = nf.parse(in);

//        throw new ParseException(SYSTools.xx("exception.integer.parser"), pe.getErrorOffset());

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
