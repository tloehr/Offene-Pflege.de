/*
 * Created by JFormDesigner on Thu Apr 30 14:43:18 CEST 2015
 */

package op.settings;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideButton;
import entity.EntityTools;
import entity.building.*;
import entity.info.ResInfo;
import gui.PnlBeanEditor;
import gui.events.ContentRequestedEventListener;
import gui.events.DataChangeEvent;
import gui.events.DataChangeListener;
import gui.events.JPADataChangeListener;
import gui.interfaces.CleanablePanel;
import gui.interfaces.DefaultCollapsiblePane;
import gui.interfaces.DefaultCollapsiblePanes;
import op.OPDE;
import op.tools.GUITools;
import op.tools.PopupPanel;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.VerticalLayout;

import javax.persistence.EntityManager;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author Torsten Löhr
 */
public class PnlHomeStationRoomEditor extends CleanablePanel {

    private HashMap<String, DefaultCollapsiblePanes> parentCPS;
    private Logger logger = Logger.getLogger(this.getClass());
    private JScrollPane scrollPane1;
    private DefaultCollapsiblePanes cpsHomes;

    public PnlHomeStationRoomEditor() {
        cpsHomes = new DefaultCollapsiblePanes();
        scrollPane1 = new JScrollPane();
        setLayout(new FormLayout(
                "default, $lcgap, default:grow, $lcgap, default",
                "default, $lgap, default:grow, $lgap, default"));

        scrollPane1.setViewportView(cpsHomes);
        add(scrollPane1, CC.xy(3, 3, CC.FILL, CC.FILL));

        parentCPS = new HashMap<>();
        logger.setLevel(Level.DEBUG);
        reload();
    }


    @Override
    public void reload() {
        refreshDisplay();
    }


    @Override
    public void cleanup() {

        for (CollapsiblePanes cps : parentCPS.entrySet().toArray(new CollapsiblePanes[]{})) {
            cps.removeAll();
        }
        parentCPS.clear();
        cpsHomes.removeAll();
    }


    @Override
    public String getInternalClassID() {
        return null;
    }


    synchronized void refreshDisplay() {

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
            dcp.getTitleButton().setFont(SYSConst.ARIAL24);
        };

        ContentRequestedEventListener<DefaultCollapsiblePane> contentUpdate = cre -> {
            DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) cre.getSource();
            Homes myHome = EntityTools.find(Homes.class, home.getEid());
            dcp.setContentPane(createContent(myHome, (DefaultCollapsiblePane<Homes>) cre.getSource()));
        };

        DefaultCollapsiblePane<Homes> cp = new DefaultCollapsiblePane(headerUpdate, contentUpdate, getMenu(home));
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
                }
                dcps1.addExpansion();

                dcp.setContentPane(dcps1);
            };

            dcps.add(new DefaultCollapsiblePane<>(headerUpdate1, contentUpdate1, getMenu(home)));


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
                }

                dcps1.addExpansion();

                dcp.setContentPane(dcps1);
            };

            dcps.add(new DefaultCollapsiblePane<>(headerUpdate2, contentUpdate2, getMenu(home)));


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
        };

        ContentRequestedEventListener<DefaultCollapsiblePane> contentUpdate = cre -> {
            DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) cre.getSource();
            Floors myFloor = EntityTools.find(Floors.class, floor.getFloorid());
            dcp.setContentPane(createContent(myFloor, (DefaultCollapsiblePane<Floors>) cre.getSource()));
        };

        DefaultCollapsiblePane<Homes> cp = new DefaultCollapsiblePane(headerUpdate, contentUpdate);
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
                parentCPS.put(getKey(floor), dcps1);
                dcps1.add(createAddRoomButton(floor));
                for (final Rooms room : myFloor.getRooms()) {
                    dcps1.add(createCP(room));
                }
                dcps1.addExpansion();

                dcp.setContentPane(dcps1);
            };
            dcps.add(new DefaultCollapsiblePane<>(headerUpdate, contentUpdate));

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

        DefaultCollapsiblePane<Homes> cp = new DefaultCollapsiblePane(headerUpdate, contentUpdate);
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
        final JideButton btnAddHome = GUITools.createHyperlinkButton("opde.settings.btnAddHome", SYSConst.icon22add, null);
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
        final JideButton btnAddHome = GUITools.createHyperlinkButton("opde.settings.btnAddFloor", SYSConst.icon22add, null);
        btnAddHome.addActionListener(e -> {

            Homes myHome = EntityTools.find(Homes.class, home.getEid());

            Floors newFloor = null;
            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();
                newFloor = em.merge(new Floors(em.merge(myHome), SYSTools.xx("opde.settings.btnAddFloor")));
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
        final JideButton btnAddHome = GUITools.createHyperlinkButton("opde.settings.btnAddRoom", SYSConst.icon22add, null);
        btnAddHome.addActionListener(e -> {

            Floors myFloor = EntityTools.find(Floors.class, floor.getFloorid());

            Rooms newRoom = null;
            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();
                newRoom = em.merge(new Rooms(SYSTools.xx("opde.settings.btnAddRoom"), true, true, em.merge(myFloor)));
                em.getTransaction().commit();
            } catch (Exception ex) {
                em.getTransaction().rollback();
                OPDE.fatal(logger, ex);
            } finally {
                em.close();
            }

            try {
                parentCPS.get(getKey(floor)).removeExpansion();
                parentCPS.get(getKey(floor)).add(createCP(newRoom));
                parentCPS.get(getKey(floor)).addExpansion();
            } catch (Exception e1) {
                OPDE.fatal(logger, e1);
            }
        });

        return btnAddHome;
    }

    private JideButton createAddStationButton(final Homes home) {
        final JideButton btnAddHome = GUITools.createHyperlinkButton("opde.settings.btnAddStation", SYSConst.icon22add, null);
        btnAddHome.addActionListener(e -> {

            Homes myHome = EntityTools.find(Homes.class, home.getEid());

            Station newStation = null;
            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();
                newStation = em.merge(new Station(SYSTools.xx("opde.settings.btnAddStation"), em.merge(myHome)));
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



        final JButton btnStop = GUITools.createHyperlinkButton("nursingrecords.info.btnStop.tooltip", SYSConst.icon22playerStop, null);
        pnlMenu.add(btnStop);

        btnStop.addActionListener(e -> {
            ((JidePopup) pnlMenu.getParent()).hidePopup();
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
