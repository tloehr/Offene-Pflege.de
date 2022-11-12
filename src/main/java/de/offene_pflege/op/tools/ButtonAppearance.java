package de.offene_pflege.op.tools;

import javax.swing.*;

public interface ButtonAppearance {
    default String get_text() {
        return null;
    }

    default String get_tooltip() {
        return null;
    }

    default Icon get_icon() {
        return null;
    }

    default boolean is_enabled() {
        return true;
    }

}
