package gui.parser;

import javax.validation.ConstraintViolationException;
import java.text.ParseException;

/**
 * Created by tloehr on 06.06.15.
 */
public interface TextParser<T> {
    T parse(String in) throws ParseException;
}
