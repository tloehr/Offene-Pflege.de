/*
 * Created by JFormDesigner on Thu Apr 30 14:43:18 CEST 2015
 */

package op.settings;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideButton;
import entity.EntityTools;
import entity.building.*;
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
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.VerticalLayout;

import javax.persistence.EntityManager;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * @author Torsten Löhr
 */
public class PnlHomeStationRoomEditor extends DefaultPanel {

    private HashMap<String, DefaultCollapsiblePane> cpMap;
    private HashMap<String, DefaultCollapsiblePanes> parentCPS;
    private Logger logger = Logger.getLogger(this.getClass());
    private JScrollPane scrollPane1;
    private DefaultCollapsiblePanes cpsHomes;


    private String internalClassID = "opde.settings.home";

    public PnlHomeStationRoomEditor() {
        super();

        mainPanel.setLayout(new FormLayout(
                "default, $lcgap, default:grow, $lcgap, default",
                "default, $lgap, default:grow, $lgap, default"));

        cpsHomes = new DefaultCollapsiblePanes();
        scrollPane1 = new JScrollPane();

        scrollPane1.setViewportView(cpsHomes);
        mainPanel.add(scrollPane1, CC.xy(3, 3, CC.FILL, CC.FILL));

        cpMap = new HashMap<>();
        parentCPS = new HashMap<>();
        logger.setLevel(Level.DEBUG);
        loadAllData();
    }


    @Override
    public void reload() {

        CollectionUtils.forAllDo(cpMap.entrySet(), o -> {
            ((DefaultCollapsiblePane) o).reload();
        });



    }


    @Override
    public void cleanup() {

        Iterator it = parentCPS.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, CollapsiblePanes> pair = (Map.Entry) it.next();
            pair.getValue().removeAll();
        }
        cpMap.clear();
        parentCPS.clear();
        cpsHomes.removeAll();
    }


    @Override
    public String getInternalClassID() {
        return internalClassID;
    }


    synchronized void loadAllData() {

        cpsHomes.removeAll();

        cpsHomes.add(createAddHomeButton());

        for (final Homes home : HomesTools.getAll()) {
            try {
                cpsHomes.add(createCP(home));
            } catch (Exception e) {
                OPDE.fatal(logger, e);
            }
        }
        cpsHomes.addExpansion();

    }

    private DefaultCollapsiblePane createCP(final Homes home) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ContentRequestedEventListener<DefaultCollapsiblePane> headerUpdate = cre -> {
            DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) cre.getSource();
            Homes myHome = EntityTools.find(Homes.class, home.getEid());
            dcp.setTitleButtonText(myHome.getName());
            dcp.getTitleButton().setForeground(GUITools.getColor(myHome.getColor()));
            dcp.getTitleButton().setFont(SYSConst.ARIAL24);
        };

        ContentRequestedEventListener<DefaultCollapsiblePane> contentUpdate = cre -> {
            DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) cre.getSource();
            Homes myHome = EntityTools.find(Homes.class, home.getEid());
            dcp.setContentPane(createContent(myHome, (DefaultCollapsiblePane<Homes>) cre.getSource()));
        };

        DefaultCollapsiblePane<Homes> cp = new DefaultCollapsiblePane(headerUpdate, contentUpdate, getMenu(home));
        cp.setName(getKey(home));
        cpMap.put(cp.getName(), cp);
        return cp;
    }

    private DefaultCollapsiblePanes createContent(final Homes home, DataChangeListener<Homes> dcl) {
        DefaultCollapsiblePanes dcps = new DefaultCollapsiblePanes();
        try {
            PnlBeanEditor<Homes> pnlBeanEditor = new PnlBeanEditor<>(() -> EntityTools.find(Homes.class, home.getEid()), Homes.class, PnlBeanEditor.SAVE_MODE_IMMEDIATE);
            pnlBeanEditor.addDataChangeListener(new JPADataChangeListener<>(editedHome -> {
                pnlBeanEditor.reload(editedHome);
                dcl.dataChanged(new DataChangeEvent<>(pnlBeanEditor, editedHome));
            }));
            dcps.add(pnlBeanEditor);

            ContentRequestedEventListener<DefaultCollapsiblePane> headerUpdate1 = cre -> {
                DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) cre.getSource();
                dcp.setTitleButtonText("misc.msg.floor");
                dcp.getTitleButton().setFont(SYSConst.ARIAL20);
            };

            ContentRequestedEventListener<DefaultCollapsiblePane> contentUpdate1 = cre -> {
                DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) cre.getSource();
                Homes myHome = EntityTools.find(Homes.class, home.getEid());
                DefaultCollapsiblePanes dcps1 = new DefaultCollapsiblePanes();
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
                dcp.getTitleButton().setFont(SYSConst.ARIAL20);
            };

            ContentRequestedEventListener<DefaultCollapsiblePane> contentUpdate2 = cre -> {
                DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) cre.getSource();
                Homes myHome = EntityTools.find(Homes.class, home.getEid());
                DefaultCollapsiblePanes dcps1 = new DefaultCollapsiblePanes();
                parentCPS.put(getKey(home) + ":station", dcps1);

                dcps1.add(createAddStationButton(home));
                for (final Station station : myHome.getStations()) {
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
            dcp.getTitleButton().setForeground(GUITools.blend(myFloor.getHome().getColor(), Color.BLACK, 0.7f));
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
        try {
            PnlBeanEditor<Floors> pbe = new PnlBeanEditor<>(() -> EntityTools.find(Floors.class, floor.getFloorid()), Floors.class, PnlBeanEditor.SAVE_MODE_IMMEDIATE);

            pbe.addDataChangeListener(new JPADataChangeListener<>(editedFloor -> {
                pbe.reload(editedFloor);
                dcl.dataChanged(new DataChangeEvent<>(pbe, editedFloor));
            }));
            pbe.setOpaque(true);
            pbe.setBackground(GUITools.blend(floor.getHome().getColor(), Color.WHITE, 0.35f));

            dcps.add(pbe);

            ContentRequestedEventListener<DefaultCollapsiblePane> headerUpdate = cre -> {
                DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) cre.getSource();
                dcp.setTitleButtonText("misc.msg.room");
                dcp.getTitleButton().setFont(dcp.getTitleButton().getFont().deriveFont(Font.BOLD));
            };

            ContentRequestedEventListener<DefaultCollapsiblePane> contentUpdate = cre -> {
                DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) cre.getSource();
                Floors myFloor = EntityTools.find(Floors.class, floor.getFloorid());

                DefaultCollapsiblePanes dcps1 = new DefaultCollapsiblePanes();
                parentCPS.put("rooms4:" + getKey(floor), dcps1);

                dcps1.add(createAddRoomButton(floor));
                for (final Rooms room : myFloor.getRooms()) {
                    dcps1.add(createCP(room));
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

            pbe.addDataChangeListener(new JPADataChangeListener<>(editedRoom -> {
                pbe.reload(editedRoom);
                dcl.dataChanged(new DataChangeEvent<>(pbe, editedRoom));
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
            Station myStation = EntityTools.find(Station.class, station.getStatID());
            dcp.setTitleButtonText(myStation.getName());
        };

        ContentRequestedEventListener<DefaultCollapsiblePane> contentUpdate = cre -> {
            DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) cre.getSource();
            Station myStation = EntityTools.find(Station.class, station.getStatID());
            dcp.setContentPane(createContent(myStation, (DefaultCollapsiblePane<Station>) cre.getSource()));
        };

        DefaultCollapsiblePane<Homes> cp = new DefaultCollapsiblePane(headerUpdate, contentUpdate);
        cp.setName(getKey(station));
        cpMap.put(cp.getName(), cp);
        return cp;
    }

    private JPanel createContent(final Station station, DefaultCollapsiblePane<Station> dcl) {
        JPanel result = null;
        try {
            PnlBeanEditor<Station> pbe = new PnlBeanEditor<>(() -> EntityTools.find(Station.class, station.getStatID()), Station.class, PnlBeanEditor.SAVE_MODE_IMMEDIATE);

            pbe.addDataChangeListener(new JPADataChangeListener<>(editedRoom -> {
                pbe.reload(editedRoom);
                dcl.dataChanged(new DataChangeEvent<>(pbe, editedRoom));
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
                newHome = em.merge(new Homes(UUID.randomUUID().toString().substring(0, 15)));

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

            Homes myHome = EntityTools.find(Homes.class, home.getEid());

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
            } catch (Exception e1) {
                OPDE.fatal(logger, e1);
            }
        });

        return btnAddHome;
    }

    private JideButton createAddStationButton(final Homes home) {
        final JideButton btnAddHome = GUITools.createHyperlinkButton("opde.settings.home.btnAddStation", SYSConst.icon22add, null);
        btnAddHome.addActionListener(e -> {

            Homes myHome = EntityTools.find(Homes.class, home.getEid());

            Station newStation = null;
            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();
                newStation = em.merge(new Station(SYSTools.xx("opde.settings.home.btnAddStation"), em.merge(myHome)));
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
            String message = EntityTools.mayBeDeleted(EntityTools.find(Homes.class, home.getEid()));
            if (message != null) {
                OPDE.getDisplayManager().addSubMessage(new DisplayMessage(message, DisplayMessage.WARNING));
                return;
            }
            ask(new PnlYesNo(SYSTools.xx("misc.questions.delete1") + "<br/><br/>&raquo;" + home.getName() + " (" + home.getEid() + ")" + "&laquo;<br/>" + "<br/>" + SYSTools.xx("misc.questions.delete2"), "opde.settings.home.btnDelHome", SYSConst.icon48delete, o -> {

                if (o.equals(JOptionPane.YES_OPTION)) {
                    EntityTools.delete(EntityTools.find(Homes.class, home.getEid()));
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

            ask(new PnlYesNo(SYSTools.xx("misc.questions.delete1") + "<br/><br/>&raquo;" + floor.getName() + " (" + floor.getFloorid() + ")" + "&laquo;<br/>" + "<br/>" + SYSTools.xx("misc.questions.delete2"), "opde.settings.home.btnDelFloor", SYSConst.icon48delete, o -> {
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

            ask(new PnlYesNo(SYSTools.xx("misc.questions.delete1") + "<br/><br/>&raquo;" + room.getText() + " (" + room.getRoomID() + ")" + "&laquo;<br/>" + "<br/>" + SYSTools.xx("misc.questions.delete2"), "opde.settings.home.btnDelFloor", SYSConst.icon48delete, o -> {
                if (o.equals(JOptionPane.YES_OPTION)) {
                    EntityTools.delete(EntityTools.find(Rooms.class, room.getRoomID()));

                    parentCPS.get(getKey(room)).remove(cpMap.get(getKey(room)));
                    cpMap.remove(getKey(room));
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
            return "home:" + ((Homes) object).getEid();
        if (object instanceof Floors)
            return "floor:" + ((Floors) object).getFloorid();
        if (object instanceof Station)
            return "station:" + ((Station) object).getStatID();

        return null;
    }


}
