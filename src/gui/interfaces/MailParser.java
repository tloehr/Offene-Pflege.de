package gui.interfaces;

import op.system.Recipient;
import op.tools.SYSTools;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by tloehr on 06.06.15.
 */
public class MailParser implements TextParser<Recipient> {
    NumberFormat nf = NumberFormat.getIntegerInstance();

    @Override
    public Recipient parse(String in) throws ParseException{
//        in = SYSTools.assimilateDecimalSeparators(in);
        try {
            return nf.parse(in).intValue();
        } catch (ParseException pe){
            throw new ParseException(SYSTools.xx("exception.integer.parser"), pe.getErrorOffset());
        }
    }
}
