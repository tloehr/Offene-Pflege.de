package de.offene_pflege.op.settings.subpanels;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideButton;
import de.offene_pflege.entity.EntityTools;
import de.offene_pflege.entity.info.ResInfoCategory;
import de.offene_pflege.entity.info.ResInfoCategoryTools;
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.gui.PnlBeanEditor;
import de.offene_pflege.gui.PnlYesNo;
import de.offene_pflege.gui.events.ContentRequestedEventListener;
import de.offene_pflege.gui.events.DataChangeEvent;
import de.offene_pflege.gui.events.DataChangeListener;
import de.offene_pflege.gui.events.JPADataChangeListener;
import de.offene_pflege.gui.interfaces.DefaultCollapsiblePane;
import de.offene_pflege.gui.interfaces.DefaultCollapsiblePanes;
import de.offene_pflege.gui.interfaces.DefaultPanel;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.VerticalLayout;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * Created by tloehr on 30.06.15.
 */
public class PnlModelEditor extends DefaultPanel {

    private HashMap<ResInfoCategory, DefaultCollapsiblePane> cpMap;
    private Logger logger = Logger.getLogger(this.getClass());
    private JScrollPane scrollPane1;
    private DefaultCollapsiblePanes cpsMain;
    private int i = 0;  // just for the progressbar

    public PnlModelEditor() {
        super("opde.settings.model");

        mainPanel.setLayout(new FormLayout(
                "default, $lcgap, default:grow, $lcgap, default",
                "default, $lgap, default:grow, $lgap, default"));

        cpsMain = new DefaultCollapsiblePanes();
        scrollPane1 = new JScrollPane();

        scrollPane1.setViewportView(cpsMain);
        mainPanel.add(scrollPane1, CC.xy(3, 3, CC.FILL, CC.FILL));

        cpMap = new HashMap<>();

        loadAllData();
    }


    @Override
    public void reload() {
        super.reload();
        OPDE.getMainframe().setBlocked(true);
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), -1, cpMap.size()));

        SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                i = -1;
                CollectionUtils.forAllDo(cpMap.values(), o -> {
                    try {
                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), i, cpMap.size()));
                        i++;
                        ((DefaultCollapsiblePane) o).reload();
                    } catch (Exception e) {
                        OPDE.fatal(logger, e);
                    }
                });
                return null;
            }

            @Override
            protected void done() {
                OPDE.getDisplayManager().setProgressBarMessage(null);
                OPDE.getMainframe().setBlocked(false);
            }
        };
        worker.execute();
    }

    @Override
    public void cleanup() {
        super.cleanup();
        cpMap.clear();
        cpsMain.removeAll();
    }


    private void loadAllData() {

        cpsMain.removeAll();

        cpsMain.add(createAddButton());


        OPDE.getMainframe().setBlocked(true);
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), 0, -1));

        SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                i = 0;

                for (final ResInfoCategory cat : ResInfoCategoryTools.getAll()) {
                    try {
                        cpsMain.add(createCP(cat));
                    } catch (Exception e) {
                        OPDE.fatal(logger, e);
                    }
                }
                cpsMain.addExpansion();

                return null;
            }

            @Override
            protected void done() {
                OPDE.getDisplayManager().setProgressBarMessage(null);
                OPDE.getMainframe().setBlocked(false);
            }
        };
        worker.execute();

    }

    private DefaultCollapsiblePane createCP(final ResInfoCategory cat) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ContentRequestedEventListener<DefaultCollapsiblePane> headerUpdate = cre -> {
            DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) cre.getSource();
            ResInfoCategory myCat = EntityTools.find(ResInfoCategory.class, cat.getID());
            dcp.setTitleButtonText(myCat.getText());
            dcp.getTitleButton().setForeground(GUITools.blend(myCat.getColor(), Color.BLACK, 0.85f));
            dcp.setBackground(GUITools.blend(myCat.getColor(), Color.WHITE, 0.05f));
            dcp.getTitleButton().setFont(SYSConst.ARIAL24);
        };

        ContentRequestedEventListener<DefaultCollapsiblePane> contentUpdate = cre -> {
            DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) cre.getSource();

            try {
                PnlBeanEditor<ResInfoCategory> pnlBeanEditor = new PnlBeanEditor<>(() -> EntityTools.find(ResInfoCategory.class, cat.getID()), ResInfoCategory.class, PnlBeanEditor.SAVE_MODE_IMMEDIATE);
                pnlBeanEditor.addDataChangeListener(new JPADataChangeListener<>((DataChangeListener<ResInfoCategory>) evt -> {
                    if (evt.isTriggersReload()) {
                        reload();
                    } else {
                        pnlBeanEditor.reload(evt.getData());
                        ((DefaultCollapsiblePane<ResInfoCategory>) cre.getSource()).dataChanged(new DataChangeEvent<>(pnlBeanEditor, evt.getData()));
                    }
                }));

                dcp.setContentPane(pnlBeanEditor);
            } catch (Exception e) {
                OPDE.fatal(logger, e);
            }
        };

        DefaultCollapsiblePane<ResInfoCategory> cp = new DefaultCollapsiblePane(headerUpdate, contentUpdate, getMenu(cat));

        cpMap.put(cat, cp);
        return cp;
    }


    private JideButton createAddButton() {
        final JideButton btnAdd = GUITools.createHyperlinkButton("opde.settings.model.btnAddCategory", SYSConst.icon22add, null);
        btnAdd.addActionListener(e -> {
            ResInfoCategory newCat = EntityTools.merge(new ResInfoCategory(ResInfoCategoryTools.BASICS));
            if (newCat != null) {
                try {
                    cpsMain.removeExpansion();
                    cpsMain.add(createCP(newCat));
                    cpsMain.addExpansion();
                } catch (Exception exc) {
                    OPDE.fatal(logger, exc);
                }
            }
        });
        return btnAdd;
    }


    private JPanel getMenu(final ResInfoCategory cat) {
        final JPanel pnlMenu = new JPanel(new VerticalLayout());

        final JButton btnDelete = GUITools.createHyperlinkButton("opde.settings.model.btnDelCategory", SYSConst.icon22delete, null);
        pnlMenu.add(btnDelete);

        btnDelete.addActionListener(e -> {
            Container c = pnlMenu.getParent();
            ((JidePopup) c.getParent().getParent().getParent()).hidePopup();
            //            if (!OPDE.isAdmin()) return;
            String message = EntityTools.mayBeDeleted(EntityTools.find(ResInfoCategory.class, cat.getID()));
            if (message != null) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(message, DisplayMessage.WARNING));
                return;
            }
            ask(new PnlYesNo(SYSTools.xx("misc.questions.delete1") + "<br/><br/>&raquo;" + cat.getText() + " (#" + cat.getID() + ")" + "&laquo;<br/>" + "<br/>" + SYSTools.xx("misc.questions.delete2"), "opde.settings.home.btnDelHome", SYSConst.icon48delete, o -> {

                if (o.equals(JOptionPane.YES_OPTION)) {
                    EntityTools.delete(EntityTools.find(ResInfoCategory.class, cat.getID()));
                    cpsMain.remove(cpMap.get(cat));
                    cpMap.remove(cat);
                }
                mainView();
            }));
        });

        return pnlMenu;
    }
}
