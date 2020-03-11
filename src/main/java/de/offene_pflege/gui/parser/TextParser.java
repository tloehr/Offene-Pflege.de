package de.offene_pflege.gui.parser;

import java.text.ParseException;

/**
 * Created by tloehr on 06.06.15.
 */
public interface TextParser<T> {
    T parse(String in) throws ParseException;
}
