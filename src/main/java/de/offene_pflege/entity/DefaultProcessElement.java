package de.offene_pflege.entity;

import de.offene_pflege.entity.process.QProcess;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class DefaultProcessElement extends DefaultEntity {
    public QProcess qProcess;

    @ManyToOne
    @JoinColumn(name = "VorgangID", referencedColumnName = "id")
    public QProcess getqProcess() {
        return qProcess;
    }

    public void setqProcess(QProcess qProcess) {
        this.qProcess = qProcess;
    }
}
