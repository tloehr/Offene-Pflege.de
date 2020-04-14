package de.offene_pflege.backend.services;

import de.offene_pflege.op.tools.InfoTreeNodeBean;
import de.offene_pflege.op.tools.SYSTools;

import java.util.ArrayList;

public class ResInfoTextRenderer implements ResInfoRenderInterface {
    public ResInfoTextRenderer() {
    }

    @Override
    public String renderBodyparts(ArrayList<String> bodyparts) {
        String text = "";
        for (String bodykey : bodyparts) {
//                                                           VV das bedeutet, LETZTER IN DER LISTE VV
            text += renderListEntry(SYSTools.xx(bodykey)) + (bodyparts.indexOf(bodykey) == bodyparts.size() - 1 ? "" : ", ");
        }
//        if (!text.isEmpty()) text = text.substring(0, text.length() - 2); // letztes komma abschneiden
        return renderGroupOfEntries("misc.msg.bodylocations", text) + "\n";
    }

    @Override
    public String renderKeyValue(String key, String value) {
        return SYSTools.xx(key) + ": " + SYSTools.xx(value);
    }

    @Override
    public String renderListEntry(String entry) {
        return entry.isEmpty() ? "" : entry;
    }

    @Override
    public String renderOption(InfoTreeNodeBean parent, InfoTreeNodeBean child) {
        if (parent.getValue().isPresent() && parent.getValue().get().toString().equals(child.getName()))
            return child.getLabel();
        return "";
    }

    @Override
    public String renderNewLine() {
        return "; ";
    }

    @Override
    public String renderBoolean(String label, String sBool, boolean showFalseEntries) {
        Boolean bool = Boolean.parseBoolean(sBool);
        // Items mit nicht angeklickten Checkboxen weglassen.
        if (showFalseEntries)
            return "[" + (bool ? "\u2713" : "\u274d") + " " + label + "] ";
        else
            return bool ? "[\u2713 " + label + "] " : "";

        //        return label + " " + (bool ? "(\u2713)" : "( )");
    }

    @Override
    public String renderLabel(String label) {
        return "";
    }

    @Override
    public String renderGroupOfEntries(String header, String list) {
        return list.isEmpty() ? "" : SYSTools.xx(header) + ": " + list + "; ";
    }
}
