package de.offene_pflege.gui.parser;

import de.offene_pflege.op.tools.SYSTools;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by tloehr on 06.06.15.
 */
public class IntegerParser implements TextParser<Integer> {
    NumberFormat nf = NumberFormat.getIntegerInstance();

    @Override
    public Integer parse(String in) throws ParseException{
        try {
            return nf.parse(in).intValue();
        } catch (ParseException pe){
            throw new ParseException(SYSTools.xx("exception.integer.parser"), pe.getErrorOffset());
        }
    }
}
