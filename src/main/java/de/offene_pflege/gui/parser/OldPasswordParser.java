package de.offene_pflege.gui.parser;

import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.SYSTools;

import java.text.ParseException;

/**
 * Created by tloehr on 06.06.15.
 */
public class OldPasswordParser implements TextParser<String> {

    @Override
    public String parse(String in) throws ParseException {
        if (in.trim().isEmpty()) return "";
        boolean passwordCorrect = OPDE.getLogin().getUser().getMd5pw().equals(SYSTools.hashword(in.trim()));
        if (!passwordCorrect) {
            throw new ParseException(SYSTools.xx("opde.settings.personal.oldpw.wrong"), in.length() - 1);
        }
        return in.trim();

    }
}
