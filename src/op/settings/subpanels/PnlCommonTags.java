package op.settings.subpanels;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideButton;
import entity.EntityTools;
import entity.system.Commontags;
import entity.system.CommontagsTools;
import gui.GUITools;
import gui.PnlBeanEditor;
import gui.PnlYesNo;
import gui.events.ContentRequestedEventListener;
import gui.events.DataChangeEvent;
import gui.events.DataChangeListener;
import gui.events.JPADataChangeListener;
import gui.interfaces.DefaultCollapsiblePane;
import gui.interfaces.DefaultCollapsiblePanes;
import gui.interfaces.DefaultPanel;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.VerticalLayout;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by tloehr on 03.07.15.
 */
public class PnlCommonTags extends DefaultPanel {
    private HashMap<Commontags, DefaultCollapsiblePane> cpMap;
    private Logger logger = Logger.getLogger(this.getClass());
    private JScrollPane scrollPane1;
    private DefaultCollapsiblePanes cpsMain;
    private int i = 0;  // just for the progressbar

    public PnlCommonTags() {
        super("opde.settings.commontags");

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

                for (final Commontags tag : CommontagsTools.getAll()) {
                    try {
                        cpsMain.add(createCP(tag));
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

    private DefaultCollapsiblePane createCP(final Commontags tag) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ContentRequestedEventListener<DefaultCollapsiblePane> headerUpdate = cre -> {
            DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) cre.getSource();
            Commontags myTag = EntityTools.find(Commontags.class, tag.getId());
            dcp.setTitleButtonText(myTag.getText());
            dcp.getTitleButton().setForeground(GUITools.blend(myTag.getColor(), Color.BLACK, 0.85f));
            dcp.setBackground(GUITools.blend(myTag.getColor(), Color.WHITE, 0.05f));
            dcp.getTitleButton().setFont(SYSConst.ARIAL24);
        };

        ContentRequestedEventListener<DefaultCollapsiblePane> contentUpdate = cre -> {
            DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) cre.getSource();

            try {
                PnlBeanEditor<Commontags> pnlBeanEditor = new PnlBeanEditor<>(() -> EntityTools.find(Commontags.class, tag.getId()), Commontags.class, PnlBeanEditor.SAVE_MODE_IMMEDIATE);
                pnlBeanEditor.addDataChangeListener(new JPADataChangeListener<>((DataChangeListener<Commontags>) evt -> {
                    if (evt.isTriggersReload()) {
                        reload();
                    } else {
                        pnlBeanEditor.reload(evt.getData());
                        ((DefaultCollapsiblePane<Commontags>) cre.getSource()).dataChanged(new DataChangeEvent<>(pnlBeanEditor, evt.getData()));
                    }
                }));

                pnlBeanEditor.setEnabled(tag.getType() == CommontagsTools.TYPE_SYS_USER);
                dcp.setContentPane(pnlBeanEditor);
            } catch (Exception e) {
                OPDE.fatal(logger, e);
            }
        };

        DefaultCollapsiblePane<Commontags> cp = new DefaultCollapsiblePane(headerUpdate, contentUpdate, getMenu(tag));

        cpMap.put(tag, cp);
        return cp;
    }


    private JideButton createAddButton() {
        final JideButton btnAdd = GUITools.createHyperlinkButton("opde.settings.btnNewCommontags", SYSConst.icon22add, null);
        btnAdd.addActionListener(e -> {
            Random rand = new Random();

            // nextInt is normally exclusive of the top value,
            // so add 1 to make it inclusive
            int randomNum = rand.nextInt((10000 - 1) + 1) + 1;
            Commontags newTag = EntityTools.merge(new Commontags("new:" + randomNum));
            if (newTag != null) {
                try {
                    cpsMain.removeExpansion();
                    cpsMain.add(createCP(newTag));
                    cpsMain.addExpansion();
                    cpsMain.validate();

                    GUITools.scroll2show(scrollPane1, cpMap.get(newTag), cpsMain, o -> GUITools.flashBackground(cpMap.get(newTag), Color.YELLOW, 2));
                } catch (Exception exc) {
                    OPDE.fatal(logger, exc);
                }
            }
        });
        return btnAdd;
    }


    private JPanel getMenu(final Commontags tag) {
        final JPanel pnlMenu = new JPanel(new VerticalLayout());

        final JButton btnDelete = GUITools.createHyperlinkButton("opde.settings.btnDelCommontags", SYSConst.icon22delete, null);
        pnlMenu.add(btnDelete);

        btnDelete.addActionListener(e -> {
            Container c = pnlMenu.getParent();
            ((JidePopup) c.getParent().getParent().getParent()).hidePopup();

            String message = EntityTools.mayBeDeleted(EntityTools.find(Commontags.class, tag.getId()));
            if (message != null) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(message, DisplayMessage.WARNING));
                return;
            }
            ask(new PnlYesNo(SYSTools.xx("misc.questions.delete1") + "<br/><br/>&raquo;" + tag.getText() + " (#" + tag.getId() + ")" + "&laquo;<br/>" + "<br/>" + SYSTools.xx("misc.questions.delete2"), "opde.settings.btnDelCommontags", SYSConst.icon48delete, o -> {

                if (o.equals(JOptionPane.YES_OPTION)) {
                    EntityTools.delete(EntityTools.find(Commontags.class, tag.getId()));
                    cpsMain.remove(cpMap.get(tag));
                    cpMap.remove(tag);
                }
                mainView();
            }));
        });

        btnDelete.setEnabled(tag.getType() == CommontagsTools.TYPE_SYS_USER);

        return pnlMenu;
    }


}
