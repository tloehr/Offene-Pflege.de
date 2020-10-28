package de.offene_pflege.gui.events;

import java.util.EventListener;

public interface AddTextListener extends EventListener {
    void addLog(String log);
    void setProgress(int running, int max);
}

