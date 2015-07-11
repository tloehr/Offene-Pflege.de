package gui.parser;

import op.tools.SYSTools;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by tloehr on 06.06.15.
 */
public class MailParser implements TextParser<InternetAddress> {
    NumberFormat nf = NumberFormat.getIntegerInstance();

    @Override
    public InternetAddress parse(String in) throws ParseException {
//        in = SYSTools.assimilateDecimalSeparators(in);
        try {

            InternetAddress emailAddr = new InternetAddress(in);
            emailAddr.validate();

            return emailAddr;
        } catch (AddressException e) {
            throw new ParseException(SYSTools.xx("exception.integer.parser"), e.getPos());
        }
    }
}
