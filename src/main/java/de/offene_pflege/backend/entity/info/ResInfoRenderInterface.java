package de.offene_pflege.backend.entity.info;

import de.offene_pflege.op.tools.InfoTreeNodeBean;

import java.util.ArrayList;

/**
 * Dieses Interface wird von der Render Klasse verwendet. Damit man den Renderer nur einmal braucht und dann zwischen HTML und TXT direkt wählen kann.
 */
public interface ResInfoRenderInterface {
    String renderBodyparts(ArrayList<String> bodyparts);
    String renderKeyValue(String key, String value);
    String renderListEntry(String entry);
    String renderBoolean(String label, String sBool, boolean showFalseEntries);
    String renderLabel(String label);
    String renderGroupOfEntries(String header, String list);
    String renderOption(InfoTreeNodeBean parent, InfoTreeNodeBean child);
    String renderNewLine();
}
