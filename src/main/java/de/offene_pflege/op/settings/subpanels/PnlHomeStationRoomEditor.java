/*
 * Created by JFormDesigner on Thu Apr 30 14:43:18 CEST 2015
 */

package de.offene_pflege.op.settings.subpanels;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideButton;
import de.offene_pflege.backend.entity.EntityTools;
import de.offene_pflege.backend.entity.done.Floors;
import de.offene_pflege.backend.entity.done.Homes;
import de.offene_pflege.backend.entity.done.Rooms;
import de.offene_pflege.backend.entity.done.Station;
import de.offene_pflege.backend.services.HomesService;
import de.offene_pflege.backend.services.StationService;
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

import javax.persistence.EntityManager;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Torsten Löhr
 */
public class PnlHomeStationRoomEditor extends DefaultPanel {

    private HashMap<String, DefaultCollapsiblePane> cpMap;
    private HashMap<String, DefaultCollapsiblePanes> parentCPS;
    private Logger logger = Logger.getLogger(this.getClass());
    private JScrollPane scrollPane1;
    private DefaultCollapsiblePanes cpsHomes;
    private int i = 0;  // just for the progressbar


    public PnlHomeStationRoomEditor() {
        super("opde.settings.homes");

        mainPanel.setLayout(new FormLayout(
                "default, $lcgap, default:grow, $lcgap, default",
                "default, $lgap, default:grow, $lgap, default"));

        cpsHomes = new DefaultCollapsiblePanes();
        scrollPane1 = new JScrollPane();

        scrollPane1.setViewportView(cpsHomes);
        mainPanel.add(scrollPane1, CC.xy(3, 3, CC.FILL, CC.FILL));

        cpMap = new HashMap<>();
        parentCPS = new HashMap<>();

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
        Iterator it = parentCPS.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, CollapsiblePanes> pair = (Map.Entry) it.next();
            pair.getValue().removeAll();
        }
        cpMap.clear();
        parentCPS.clear();
        cpsHomes.removeAll();
    }

    private void loadAllData() {
        cpsHomes.removeAll();
        cpsHomes.add(createAddHomeButton());

        OPDE.getMainframe().setBlocked(true);
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), 0, -1));

        SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                i = 0;

                for (final Homes home : HomesService.getAll()) {
                    try {
                        cpsHomes.add(createCP(home));
                    } catch (Exception e) {
                        OPDE.fatal(logger, e);
                    }
                }
                cpsHomes.addExpansion();

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

    private DefaultCollapsiblePane createCP(final Homes home) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ContentRequestedEventListener<DefaultCollapsiblePane> headerUpdate = cre -> {
            DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) cre.getSource();
            Homes myHome = EntityTools.find(Homes.class, home.getId());
            dcp.setTitleButtonText(myHome.getName());
            dcp.getTitleButton().setForeground(GUITools.blend(myHome.getColor(), Color.BLACK, 0.85f));
            dcp.setBackground(GUITools.blend(myHome.getColor(), Color.WHITE, 0.05f));

            Font font = SYSConst.ARIAL24;

            if (myHome.getActive().equals(Boolean.FALSE)) {
                Map attributes = font.getAttributes();
                attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
                font = new Font(attributes);
            }

            dcp.getTitleButton().setFont(font);
        };

        ContentRequestedEventListener<DefaultCollapsiblePane> contentUpdate = cre -> {
            DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) cre.getSource();
            Homes myHome = EntityTools.find(Homes.class, home.getId());
            dcp.setContentPane(createContent(myHome, (DefaultCollapsiblePane<Homes>) cre.getSource()));
        };

        DefaultCollapsiblePane<Homes> cp = new DefaultCollapsiblePane(headerUpdate, contentUpdate, getMenu(home));
        cp.setName(getKey(home));
        cpMap.put(cp.getName(), cp);
        return cp;
    }

    private DefaultCollapsiblePanes createContent(final Homes home, DataChangeListener<Homes> dcl) {
        DefaultCollapsiblePanes dcps = new DefaultCollapsiblePanes();
        dcps.setBackground(GUITools.blend(home.getColor(), Color.WHITE, 0.08f));

        try {
            PnlBeanEditor<Homes> pnlBeanEditor = new PnlBeanEditor<>(() -> EntityTools.find(Homes.class, home.getId()), Homes.class, PnlBeanEditor.SAVE_MODE_IMMEDIATE);
            pnlBeanEditor.addDataChangeListener(new JPADataChangeListener<>(evt -> {
                if (evt.isTriggersReload()) {
                    reload();
                } else {
                    pnlBeanEditor.reload(evt.getData());
                    dcl.dataChanged(new DataChangeEvent<>(pnlBeanEditor, evt.getData()));
                }
            }));

            dcps.add(pnlBeanEditor);

            ContentRequestedEventListener<DefaultCollapsiblePane> headerUpdate1 = cre -> {
                DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) cre.getSource();
                dcp.setTitleButtonText("misc.msg.floor");
                Homes myHome = EntityTools.find(Homes.class, home.getId());
                dcp.getTitleButton().setForeground(GUITools.blend(myHome.getColor(), Color.BLACK, 0.85f));
                dcp.setBackground(GUITools.blend(myHome.getColor(), Color.WHITE, 0.12f));
                dcp.getTitleButton().setFont(SYSConst.ARIAL20);
            };

            ContentRequestedEventListener<DefaultCollapsiblePane> contentUpdate1 = cre -> {
                DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) cre.getSource();
                Homes myHome = EntityTools.find(Homes.class, home.getId());
                DefaultCollapsiblePanes dcps1 = new DefaultCollapsiblePanes();
                dcps1.setBackground(GUITools.blend(myHome.getColor(), Color.WHITE, 0.15f));
                parentCPS.put(getKey(home) + ":floors", dcps1);

                dcps1.add(createAddFloorButton(home));
                for (final Floors floor : myHome.getFloors()) {
                    dcps1.add(createCP(floor));
                    parentCPS.put(getKey(floor), dcps1);
                }
                dcps1.addExpansion();

                dcp.setContentPane(dcps1);
            };

            dcps.add(new DefaultCollapsiblePane<>(headerUpdate1, contentUpdate1));

            ContentRequestedEventListener<DefaultCollapsiblePane> headerUpdate2 = cre -> {
                DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) cre.getSource();
                dcp.setTitleButtonText("misc.msg.station");
                Homes myHome = EntityTools.find(Homes.class, home.getId());
                dcp.getTitleButton().setForeground(GUITools.blend(myHome.getColor(), Color.BLACK, 0.85f));
                dcp.setBackground(GUITools.blend(myHome.getColor(), Color.WHITE, 0.12f));
                dcp.getTitleButton().setFont(SYSConst.ARIAL20);
            };

            ContentRequestedEventListener<DefaultCollapsiblePane> contentUpdate2 = cre -> {
                DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) cre.getSource();
                Homes myHome = EntityTools.find(Homes.class, home.getId());
                DefaultCollapsiblePanes dcps1 = new DefaultCollapsiblePanes();
                dcps1.setBackground(GUITools.blend(myHome.getColor(), Color.WHITE, 0.15f));
                parentCPS.put(getKey(home) + ":station", dcps1);

                dcps1.add(createAddStationButton(home));
                for (final Station station : myHome.getStation()) {
                    dcps1.add(createCP(station));
                    parentCPS.put(getKey(station), dcps1);
                }


                dcps1.addExpansion();

                dcp.setContentPane(dcps1);
            };

            dcps.add(new DefaultCollapsiblePane<>(headerUpdate2, contentUpdate2));


            dcps.addExpansion();
        } catch (Exception e) {
            OPDE.fatal(logger, e);
        }

        return dcps;
    }


    private DefaultCollapsiblePane createCP(final Floors floor) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        ContentRequestedEventListener<DefaultCollapsiblePane> headerUpdate = cre -> {
            DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) cre.getSource();
            Floors myFloor = EntityTools.find(Floors.class, floor.getFloorid());
            dcp.setTitleButtonText(myFloor.getName());
            dcp.getTitleButton().setForeground(GUITools.blend(floor.getHome().getColor(), Color.BLACK, 0.85f));
            dcp.setBackground(GUITools.blend(floor.getHome().getColor(), Color.WHITE, 0.18f));
        };

        ContentRequestedEventListener<DefaultCollapsiblePane> contentUpdate = cre -> {
            DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) cre.getSource();
            Floors myFloor = EntityTools.find(Floors.class, floor.getFloorid());
            dcp.setContentPane(createContent(myFloor, (DefaultCollapsiblePane<Floors>) cre.getSource()));
        };

        DefaultCollapsiblePane<Floors> cp = new DefaultCollapsiblePane(headerUpdate, contentUpdate, getMenu(floor));
        cp.setName(getKey(floor));
        cpMap.put(cp.getName(), cp);
        return cp;
    }

    private DefaultCollapsiblePanes createContent(final Floors floor, DefaultCollapsiblePane<Floors> dcl) {
        DefaultCollapsiblePanes dcps = new DefaultCollapsiblePanes();
        dcps.setBackground(GUITools.blend(floor.getHome().getColor(), Color.WHITE, 0.2f));
        try {
            PnlBeanEditor<Floors> pbe = new PnlBeanEditor<>(() -> EntityTools.find(Floors.class, floor.getFloorid()), Floors.class, PnlBeanEditor.SAVE_MODE_IMMEDIATE);

            pbe.addDataChangeListener(new JPADataChangeListener<Floors>(evt -> {
                pbe.reload(evt.getData());
                dcl.dataChanged(new DataChangeEvent<>(pbe, evt.getData()));
            }));
            pbe.setOpaque(true);
            pbe.setBackground(GUITools.blend(floor.getHome().getColor(), Color.WHITE, 0.23f));

            dcps.add(pbe);

            ContentRequestedEventListener<DefaultCollapsiblePane> headerUpdate = cre -> {
                DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) cre.getSource();
                dcp.setTitleButtonText("misc.msg.room");
                dcp.getTitleButton().setForeground(GUITools.blend(floor.getHome().getColor(), Color.BLACK, 0.85f));
                dcp.setBackground(GUITools.blend(floor.getHome().getColor(), Color.WHITE, 0.26f));
                dcp.getTitleButton().setFont(dcp.getTitleButton().getFont().deriveFont(Font.BOLD));
            };

            ContentRequestedEventListener<DefaultCollapsiblePane> contentUpdate = cre -> {
                DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) cre.getSource();
                Floors myFloor = EntityTools.find(Floors.class, floor.getFloorid());

                DefaultCollapsiblePanes dcps1 = new DefaultCollapsiblePanes();
                dcps1.setBackground(GUITools.blend(myFloor.getHome().getColor(), Color.WHITE, 0.29f));
                parentCPS.put("rooms4:" + getKey(floor), dcps1);

                dcps1.add(createAddRoomButton(floor));
                for (final Rooms room : myFloor.getRooms()) {
                    dcps1.add(createCP(room));
                    // #27
                    parentCPS.put(getKey(room), dcps1);
                }
                dcps1.addExpansion();

                dcp.setContentPane(dcps1);
            };
            dcps.add(new DefaultCollapsiblePane<Floors>(headerUpdate, contentUpdate));

            dcps.addExpansion();
        } catch (Exception e) {
            OPDE.fatal(logger, e);
        }

        return dcps;
    }

    private DefaultCollapsiblePane createCP(final Rooms room) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ContentRequestedEventListener<DefaultCollapsiblePane> headerUpdate = cre -> {
            DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) cre.getSource();
            Rooms myRoom = EntityTools.find(Rooms.class, room.getRoomID());
            dcp.setTitleButtonText(myRoom.getText());
            dcp.getTitleButton().setForeground(GUITools.blend(room.getFloor().getHome().getColor(), Color.BLACK, 0.85f));
            dcp.setBackground(GUITools.blend(room.getFloor().getHome().getColor(), Color.WHITE, 0.29f));
        };

        ContentRequestedEventListener<DefaultCollapsiblePane> contentUpdate = cre -> {
            DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) cre.getSource();
            Rooms myRoom = EntityTools.find(Rooms.class, room.getRoomID());
            dcp.setContentPane(createContent(myRoom, (DefaultCollapsiblePane<Rooms>) cre.getSource()));
        };

        DefaultCollapsiblePane<Homes> cp = new DefaultCollapsiblePane(headerUpdate, contentUpdate, getMenu(room));
        cp.setName(getKey(room));
        cpMap.put(cp.getName(), cp);
        return cp;
    }

    private JPanel createContent(final Rooms room, DefaultCollapsiblePane<Rooms> dcl) {
        JPanel result = null;
        try {
            PnlBeanEditor<Rooms> pbe = new PnlBeanEditor<>(() -> EntityTools.find(Rooms.class, room.getRoomID()), Rooms.class, PnlBeanEditor.SAVE_MODE_IMMEDIATE);
            pbe.setOpaque(true);
            pbe.setBackground(GUITools.blend(room.getFloor().getHome().getColor(), Color.WHITE, 0.33f));
            pbe.addDataChangeListener(new JPADataChangeListener<>(evt -> {
                pbe.reload(evt.getData());
                dcl.dataChanged(new DataChangeEvent<>(pbe, evt.getData()));
            }));
            result = pbe;
        } catch (Exception e) {
            OPDE.fatal(logger, e);
        }
        return result;
    }

    private DefaultCollapsiblePane createCP(final Station station) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ContentRequestedEventListener<DefaultCollapsiblePane> headerUpdate = cre -> {
            DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) cre.getSource();
            Station myStation = EntityTools.find(Station.class, station.getId());
            dcp.getTitleButton().setForeground(GUITools.blend(station.getHome().getColor(), Color.BLACK, 0.85f));
            dcp.setBackground(GUITools.blend(myStation.getHome().getColor(), Color.WHITE, 0.2f));
            dcp.setTitleButtonText(myStation.getName());
        };

        ContentRequestedEventListener<DefaultCollapsiblePane> contentUpdate = cre -> {
            DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) cre.getSource();
            Station myStation = EntityTools.find(Station.class, station.getId());
            dcp.setContentPane(createContent(myStation, (DefaultCollapsiblePane<Station>) cre.getSource()));
        };

        DefaultCollapsiblePane<Homes> cp = new DefaultCollapsiblePane(headerUpdate, contentUpdate, getMenu(station));
        cp.setName(getKey(station));
        cpMap.put(cp.getName(), cp);
        return cp;
    }

    private JPanel createContent(final Station station, DefaultCollapsiblePane<Station> dcl) {
        JPanel result = null;
        try {
            PnlBeanEditor<Station> pbe = new PnlBeanEditor<>(() -> EntityTools.find(Station.class, station.getId()), Station.class, PnlBeanEditor.SAVE_MODE_IMMEDIATE);

            pbe.addDataChangeListener(new JPADataChangeListener<>(evt -> {
                pbe.reload(evt.getData());
                dcl.dataChanged(new DataChangeEvent<>(pbe, evt.getData()));
            }));
            result = pbe;
        } catch (Exception e) {
            OPDE.fatal(logger, e);
        }
        return result;
    }


    private JideButton createAddHomeButton() {
        final JideButton btnAddHome = GUITools.createHyperlinkButton("opde.settings.home.btnAddHome", SYSConst.icon22add, null);
        btnAddHome.addActionListener(e -> {

            Homes newHome = null;
            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();
                newHome = em.merge(HomesService.create());

                em.getTransaction().commit();
            } catch (IllegalStateException ise) {
                logger.error(ise);
            } catch (Exception ex) {
                em.getTransaction().rollback();
                OPDE.fatal(logger, ex);
            } finally {
                em.close();
            }

            try {
                cpsHomes.removeExpansion();
                cpsHomes.add(createCP(newHome));
                cpsHomes.addExpansion();
            } catch (Exception e1) {
                OPDE.fatal(logger, e1);
            }
        });

        return btnAddHome;
    }


    private JideButton createAddFloorButton(final Homes home) {
        final JideButton btnAddHome = GUITools.createHyperlinkButton("opde.settings.home.btnAddFloor", SYSConst.icon22add, null);

        btnAddHome.addActionListener(e -> {
            Homes myHome = EntityTools.find(Homes.class, home.getId());
            btnAddHome.setForeground(GUITools.blend(myHome.getColor(), Color.BLACK, 0.85f));
            Floors newFloor = null;
            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();
                newFloor = em.merge(new Floors(em.merge(myHome), SYSTools.xx("opde.settings.home.btnAddFloor")));
                em.getTransaction().commit();
            } catch (Exception ex) {
                em.getTransaction().rollback();
                OPDE.fatal(logger, ex);
            } finally {
                em.close();
            }

            try {
                parentCPS.get(getKey(home) + ":floors").removeExpansion();
                parentCPS.get(getKey(home) + ":floors").add(createCP(newFloor));
                parentCPS.get(getKey(home) + ":floors").addExpansion();
            } catch (Exception e1) {
                OPDE.fatal(logger, e1);
            }
        });

        return btnAddHome;
    }

    private JideButton createAddRoomButton(final Floors floor) {
        final JideButton btnAddHome = GUITools.createHyperlinkButton("opde.settings.home.btnAddRoom", SYSConst.icon22add, null);
        btnAddHome.addActionListener(e -> {

            Floors myFloor = EntityTools.find(Floors.class, floor.getFloorid());

            Rooms newRoom = null;
            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();
                newRoom = em.merge(new Rooms(SYSTools.xx("opde.settings.home.btnAddRoom"), true, true, em.merge(myFloor)));
                em.getTransaction().commit();
            } catch (Exception ex) {
                em.getTransaction().rollback();
                OPDE.fatal(logger, ex);
            } finally {
                em.close();
            }

            try {
                parentCPS.get("rooms4:" + getKey(floor)).removeExpansion();
                parentCPS.get("rooms4:" + getKey(floor)).add(createCP(newRoom));

                parentCPS.get("rooms4:" + getKey(floor)).addExpansion();
                // #27
                parentCPS.put(getKey(newRoom), parentCPS.get("rooms4:" + getKey(floor)));
            } catch (Exception e1) {
                OPDE.fatal(logger, e1);
            }
        });

        return btnAddHome;
    }

    private JideButton createAddStationButton(final Homes home) {
        final JideButton btnAddHome = GUITools.createHyperlinkButton("opde.settings.home.btnAddStation", SYSConst.icon22add, null);
        btnAddHome.addActionListener(e -> {

            Homes myHome = EntityTools.find(Homes.class, home.getId());

            Station newStation = null;
            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();
                newStation = em.merge(StationService.create(SYSTools.xx("opde.settings.home.btnAddStation"), em.merge(myHome)));
                em.getTransaction().commit();
            } catch (Exception ex) {
                em.getTransaction().rollback();
                OPDE.fatal(logger, ex);
            } finally {
                em.close();
            }

            try {
                parentCPS.get(getKey(home) + ":station").removeExpansion();
                parentCPS.get(getKey(home) + ":station").add(createCP(newStation));
                parentCPS.get(getKey(home) + ":station").addExpansion();
            } catch (Exception e1) {
                OPDE.fatal(logger, e1);
            }
        });

        return btnAddHome;

    }

    private JPanel getMenu(final Homes home) {
        final JPanel pnlMenu = new JPanel(new VerticalLayout());

        final JButton btnDelete = GUITools.createHyperlinkButton("opde.settings.home.btnDelHome", SYSConst.icon22delete, null);
        pnlMenu.add(btnDelete);

        btnDelete.addActionListener(e -> {
            Container c = pnlMenu.getParent();
            ((JidePopup) c.getParent().getParent().getParent()).hidePopup();
//            if (!OPDE.isAdmin()) return;
            String message = EntityTools.mayBeDeleted(EntityTools.find(Homes.class, home.getId()));
            if (message != null) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(message, DisplayMessage.WARNING));
                return;
            }
            ask(new PnlYesNo(SYSTools.xx("misc.questions.delete1") + "<br/><br/>&raquo;" + home.getName() + " (" + home.getId() + ")" + "&laquo;<br/>" + "<br/>" + SYSTools.xx("misc.questions.delete2"), "opde.settings.home.btnDelHome", SYSConst.icon48delete, o -> {

                if (o.equals(JOptionPane.YES_OPTION)) {
                    EntityTools.delete(EntityTools.find(Homes.class, home.getId()));
                    cpsHomes.remove(cpMap.get(getKey(home)));
                    cpMap.remove(getKey(home));
                }
                mainView();
            }));
        });

        return pnlMenu;
    }

    private JPanel getMenu(final Floors floor) {
        final JPanel pnlMenu = new JPanel(new VerticalLayout());
        final JButton btnDelete = GUITools.createHyperlinkButton("opde.settings.home.btnDelFloor", SYSConst.icon22delete, null);
        pnlMenu.add(btnDelete);

        btnDelete.addActionListener(e -> {

            Container c = pnlMenu.getParent();
            ((JidePopup) c.getParent().getParent().getParent()).hidePopup();

            String message = EntityTools.mayBeDeleted(EntityTools.find(Floors.class, floor.getFloorid()));
            if (message != null) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(message, DisplayMessage.WARNING));
                return;
            }

            ask(new PnlYesNo(SYSTools.xx("misc.questions.delete1") + "<br/><br/>&raquo;" + floor.getName() + " (#" + floor.getFloorid() + ")" + "&laquo;<br/>" + "<br/>" + SYSTools.xx("misc.questions.delete2"), "opde.settings.home.btnDelFloor", SYSConst.icon48delete, o -> {
                if (o.equals(JOptionPane.YES_OPTION)) {
                    Floors myFloor = floor;
                    String key = getKey(myFloor);
                    EntityTools.delete(EntityTools.find(Floors.class, floor.getFloorid()));

                    parentCPS.get(getKey(myFloor.getHome()) + ":floors").remove(cpMap.get(key));
                    cpMap.remove(key);
                }
                mainView();
            }));
        });

        return pnlMenu;
    }

    private JPanel getMenu(final Rooms room) {
        final JPanel pnlMenu = new JPanel(new VerticalLayout());
        final JButton btnDelete = GUITools.createHyperlinkButton("opde.settings.home.btnDelRoom", SYSConst.icon22delete, null);
        pnlMenu.add(btnDelete);

        btnDelete.addActionListener(e -> {

            Container c = pnlMenu.getParent();
            ((JidePopup) c.getParent().getParent().getParent()).hidePopup();

            String message = EntityTools.mayBeDeleted(EntityTools.find(Rooms.class, room.getRoomID()));
            if (message != null) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(message, DisplayMessage.WARNING));
                return;
            }

            ask(new PnlYesNo(SYSTools.xx("misc.questions.delete1") + "<br/><br/>&raquo;" + room.getText() + " (#" + room.getRoomID() + ")" + "&laquo;<br/>" + "<br/>" + SYSTools.xx("misc.questions.delete2"), "opde.settings.home.btnDelFloor", SYSConst.icon48delete, o -> {
                if (o.equals(JOptionPane.YES_OPTION)) {
                    parentCPS.get(getKey(room)).remove(cpMap.get(getKey(room)));
                    cpMap.remove(getKey(room));
                    EntityTools.delete(EntityTools.find(Rooms.class, room.getRoomID()));
                }
                mainView();
            }));
        });

        return pnlMenu;
    }

    private JPanel getMenu(final Station station) {
        final JPanel pnlMenu = new JPanel(new VerticalLayout());
        final JButton btnDelete = GUITools.createHyperlinkButton("opde.settings.home.btnDelStation", SYSConst.icon22delete, null);
        pnlMenu.add(btnDelete);

        btnDelete.addActionListener(e -> {

            Container c = pnlMenu.getParent();
            ((JidePopup) c.getParent().getParent().getParent()).hidePopup();

            String message = EntityTools.mayBeDeleted(EntityTools.find(Station.class, station.getId()));
            if (message != null) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(message, DisplayMessage.WARNING));
                return;
            }

            ask(new PnlYesNo(SYSTools.xx("misc.questions.delete1") + "<br/><br/>&raquo;" + station.getName() + " (" + station.getId() + ")" + "&laquo;<br/>" + "<br/>" + SYSTools.xx("misc.questions.delete2"), "opde.settings.home.btnDelFloor", SYSConst.icon48delete, o -> {
                if (o.equals(JOptionPane.YES_OPTION)) {
                    Station myStation = station;
                    String key = getKey(myStation);
                    // #27
                    EntityTools.delete(EntityTools.find(Station.class, station.getId()));

                    parentCPS.get(getKey(myStation.getHome()) + ":station").remove(cpMap.get(key));
                    cpMap.remove(key);
                }
                mainView();
            }));
        });

        return pnlMenu;
    }

    private String getKey(Object object) {
        if (object instanceof DefaultMutableTreeNode)
            object = ((DefaultMutableTreeNode) object).getUserObject();
        if (object instanceof Rooms)
            return "room:" + ((Rooms) object).getRoomID();
        if (object instanceof Homes)
            return "home:" + ((Homes) object).getId();
        if (object instanceof Floors)
            return "floor:" + ((Floors) object).getFloorid();
        if (object instanceof Station)
            return "station:" + ((Station) object).getId();

        return null;
    }


}
