package gui.interfaces;

import org.apache.commons.collections.CollectionUtils;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;

/**
 * Created by tloehr on 11.06.15.
 */
public class DefaultContentPane extends JPanel implements Reloadable {
    HashSet<Reloadable> reloadList = new HashSet<>();
    DefaultCollapsiblePanes cps = new DefaultCollapsiblePanes();
    int expansionPosition = -1;

    public DefaultContentPane() {
        super();
    }

    void addExpansion() {
        removeExpansion();
        cps.addExpansion();
        expansionPosition = cps.getComponentCount() - 1;
    }

    void removeExpansion() {
        // wie gehts hier weiter ?
        if (expansionPosition < 0) return;
        cps.remove(expansionPosition);
        expansionPosition = -1;
    }

    @Override
    public void reload() {
        CollectionUtils.forAllDo(reloadList, o -> ((Reloadable) o).reload());
    }

    @Override
    public Component add(Component comp) {
        if (comp instanceof Reloadable) {
            reloadList.add((Reloadable) comp);
        }
        return super.add(comp);
    }

    @Override
    public Component add(String name, Component comp) {
        if (comp instanceof Reloadable) {
            reloadList.add((Reloadable) comp);
        }
        return super.add(name, comp);
    }

    @Override
    public Component add(Component comp, int index) {
        if (comp instanceof Reloadable) {
            reloadList.add((Reloadable) comp);
        }
        return super.add(comp, index);
    }

    @Override
    public void add(Component comp, Object constraints) {
        if (comp instanceof Reloadable) {
            reloadList.add((Reloadable) comp);
        }
        super.add(comp, constraints);
    }

    @Override
    public void add(Component comp, Object constraints, int index) {
        if (comp instanceof Reloadable) {
            reloadList.add((Reloadable) comp);
        }
        super.add(comp, constraints, index);
    }

    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        if (comp instanceof Reloadable) {
            reloadList.add((Reloadable) comp);
        }
        super.addImpl(comp, constraints, index);
    }
}
