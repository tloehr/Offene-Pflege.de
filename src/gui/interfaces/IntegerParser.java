package gui.interfaces;

import op.tools.SYSTools;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by tloehr on 06.06.15.
 */
public class IntegerParser implements TextParser<Integer> {
    NumberFormat nf = NumberFormat.getIntegerInstance();

    @Override
    public Integer parse(String in) {
        in = SYSTools.assimilateDecimalSeparators(in);
        Integer num = null;
        try {
            num = nf.parse(in).intValue();
        } catch (ParseException ex) {
            num = null;
        }

        return num;
    }
}
