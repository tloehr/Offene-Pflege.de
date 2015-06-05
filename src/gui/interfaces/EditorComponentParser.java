package gui.interfaces;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * Created by tloehr on 05.06.15.
 */
public class EditorComponentParser extends AbstractProcessor {

    public EditorComponentParser() {
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return false;
    }
}
