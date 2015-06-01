/*
 * Created by JFormDesigner on Thu Apr 30 14:43:18 CEST 2015
 */

package op.settings;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import entity.building.*;
import gui.PnlBeanEditor;
import interfaces.DataChangeEvent;
import interfaces.DataChangeListener;
import op.OPDE;
import op.threads.DisplayManager;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author Torsten Löhr
 */
public class PnlHomeStationRoomEditor extends CleanablePanel implements Runnable {

    private final DefaultMutableTreeNode root;
    private String keyToRefresh;

    private boolean refresh = false;
    private final Thread thread;

    private HashMap<String, DefaultCollapsiblePane> indexView;
    private HashMap<String, DefaultMutableTreeNode> indexModel;
    private Logger logger = Logger.getLogger(this.getClass());

    public PnlHomeStationRoomEditor() {
        initComponents();
        indexView = new HashMap<>();
        indexModel = new HashMap<>();
        keyToRefresh = null;

        root = new DefaultMutableTreeNode();
        thread = new Thread(this);
        thread.start();
        reload();
    }


    @Override
    public void reload() {
        initDataModel(null);
        refresh = true;
    }

    private String initDataModel(Homes newHome) {
        DefaultMutableTreeNode subtree = null;

        ArrayList<Homes> listHomes = null;
        if (newHome == null) {
            root.removeAllChildren();
            listHomes = HomesTools.getAll();
        } else {
            listHomes = new ArrayList<>();
            listHomes.add(newHome);
        }

        for (final Homes home : listHomes) {

            subtree = new DefaultMutableTreeNode(home);
            root.add(subtree);
            DefaultMutableTreeNode stationSubtree = new DefaultMutableTreeNode(Station.class);
            DefaultMutableTreeNode floorSubtree = new DefaultMutableTreeNode(Floors.class);
            subtree.add(floorSubtree);
            subtree.add(stationSubtree);

            indexModel.put(getKey(home), subtree);

            for (final Floors floor : home.getFloors()) {
                DefaultMutableTreeNode floorNode = new DefaultMutableTreeNode(floor);
                indexModel.put(getKey(floor), floorNode);
                floorSubtree.add(floorNode);
                for (final Rooms room : floor.getRooms()) {
                    DefaultMutableTreeNode roomNode = new DefaultMutableTreeNode(room);

                    floorNode.add(roomNode);
                    indexModel.put(getKey(room), roomNode);
                }
            }

            for (final Station station : home.getStations()) {
                DefaultMutableTreeNode stationNode = new DefaultMutableTreeNode(station);
                stationSubtree.add(stationNode);
                indexModel.put(getKey(station), stationNode);
            }
        }

        return getKey(newHome);
    }

    @Override
    public void cleanup() {
        thread.interrupt();
        indexView.clear();
        indexModel.clear();
        cpsHomes.removeAll();
        root.removeAllChildren();
    }


    @Override
    public String getInternalClassID() {
        return null;
    }

    @Override
    public void run() {
        while (!thread.isInterrupted()) {
            try {
                if (refresh) {
                    refresh = false;
                    refreshDisplay();
                }

                Thread.sleep(OPDE.DEFAULT_SCREEN_RESFRESH_MILLIS);
            } catch (InterruptedException e) {
                logger.debug("InterruptedException");
                return;
            }
        }
    }

    synchronized void refreshDisplay() {
        if (keyToRefresh == null) {
            cpsHomes.removeAll();
            cpsHomes.setLayout(new JideBoxLayout(cpsHomes, JideBoxLayout.Y_AXIS));
            cpsHomes.add(createAddHomeButton());

            Enumeration en = root.children();
            while (en.hasMoreElements()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
                cpsHomes.add(createCP(node));
            }

            cpsHomes.addExpansion();
        } else {
            if (indexView.containsKey(keyToRefresh)) {
                indexView.get(keyToRefresh).reload();
            } else {

                DefaultMutableTreeNode node = indexModel.get(keyToRefresh);
                if (node.getUserObject() instanceof Homes) {
                    cpsHomes.remove(cpsHomes.getComponentCount() - 1); // remove old Expansion
                    cpsHomes.add(createCP(node));
                    cpsHomes.addExpansion();
                } else {
                    indexView.get(keyToRefresh).reload();

                }

                SwingUtilities.invokeLater(() -> {
                    cpsHomes.revalidate();
                    cpsHomes.repaint();
                });
            }
            keyToRefresh = null;
        }
    }


    private DefaultCollapsiblePane createCP(DefaultMutableTreeNode node) {

//        OPDE.debug(key);


//        DefaultCPTitle cptitle = null;

        Closure contentProvider = null;

//        DefaultMutableTreeNode node = node;

        if (node == null) return null;

        Object userObject = node.getUserObject();

        if (userObject instanceof Homes) {
            contentProvider = o -> {
                DefaultMutableTreeNode myNode = indexModel.get(getKey(node));
                DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) o;
                dcp.setTitleButtonText(((Homes) myNode.getUserObject()).getName());
                dcp.getTitleButton().setFont(SYSConst.ARIAL24);
                dcp.setContentPane(createContent(myNode));
            };
        } else if (node.getUserObject().equals(Floors.class)) {
            contentProvider = o -> {
                DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) o;
                dcp.setTitleButtonText("misc.msg.floor");
                dcp.getTitleButton().setFont(SYSConst.ARIAL20);
                dcp.setContentPane(createContent(node));
            };
        } else if (node.getUserObject() instanceof Floors) {
            contentProvider = o -> {
                DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) o;
                dcp.setTitleButtonText(((Floors) node.getUserObject()).getName());
                dcp.setContentPane(createContent(node));
            };
        } else if (node.getUserObject() instanceof Rooms) {
            contentProvider = o -> {
                DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) o;
                dcp.setTitleButtonText(((Rooms) node.getUserObject()).getText());
                dcp.setContentPane(createContent(node));
            };

//            JPanel contentPanel = new JPanel();
//            cp.setTitleButtonText(((Rooms) node.getUserObject()).getText());
//            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
//            contentPanel.add(new PnlRooms((Rooms) node.getUserObject(), new DataChangeListener<Rooms>() {
//                @Override
//                public void dataChanged(DataChangeEvent evt) {
////                    EntityManager em = OPDE.createEM();
////                    try {
////                        em.getTransaction().begin();
////                        Rooms myRoom = em.merge((Rooms) evt.getData());
////
////                        em.getTransaction().commit();
////
////                        DefaultMutableTreeNode found = indexN.get(getName(myRoom));
////                        found.setUserObject(myRoom);
////
////                        createCP(found);
////
////
//////                        DefaultMutableTreeNode found = find(evt.getData());
//////                        found.setUserObject(myRoom);
////
//////                        OPDE.getMainframe().emptySearchArea();
//////                        OPDE.getMainframe().prepareSearchArea();
////                    } catch (Exception e) {
////                        em.getTransaction().rollback();
////                        OPDE.fatal(e);
////                    } finally {
////                        em.close();
////                        setRefresh(true);
////                    }
//                }
//            }));
//            cp.setContentPane(contentPanel);
        } else if (node.getUserObject().equals(Station.class)) {

            contentProvider = o -> {
                DefaultCollapsiblePane dcp = (DefaultCollapsiblePane) o;
                dcp.setTitleButtonText("misc.msg.stations");
                dcp.getTitleButton().setFont(SYSConst.ARIAL20);
                JPanel contentPanel = new JPanel();
                contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));

                dcp.setContentPane(createContent4Stations(node));
            };


        }


        DefaultCollapsiblePane cp = new DefaultCollapsiblePane(contentProvider);


//        cp.setBackground(Color.white);

        if (getKey(node) != null) {
            indexView.put(getKey(node), cp);
        }


//        if (!(node.getUserObject() instanceof Rooms)) {
//            cp = new CollapsiblePane();
//            ((CollapsiblePane) cp).setCollapsible(true);
//            ((CollapsiblePane) cp).setTitleLabelComponent(cptitle.getMain());
//            ((CollapsiblePane) cp).setSlidingDirection(SwingConstants.SOUTH);
//            try {
//                ((CollapsiblePane) cp).setCollapsed(false);
//            } catch (PropertyVetoException e) {
//                e.printStackTrace();
//            }
//            ((CollapsiblePane) cp).setContentPane(contentPanel);
//        } else {
//            cp = cptitle.getMain();
//        }

        return cp;
    }

    private JPanel createContent(DefaultMutableTreeNode node) {


//        DefaultMutableTreeNode node = node;

        CollapsiblePanes cps = new CollapsiblePanes();
        cps.setLayout(new JideBoxLayout(cps, JideBoxLayout.Y_AXIS));
        Enumeration en = node.children();
        final String key = getKey(node);

        if (node.getUserObject() instanceof Homes) {
            cps.add(new PnlHomes(new DataChangeListener<Homes>() {
                @Override
                public void dataChanged(DataChangeEvent evt) {
                    if (!evt.isValid()) {
                        OPDE.getDisplayManager().addSubMessage(evt.getValidationResult());
                        return;
                    }

                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        Homes myHome = em.merge((Homes) evt.getData());
                        em.lock(myHome, LockModeType.OPTIMISTIC);
                        em.getTransaction().commit();

                        DefaultMutableTreeNode found = find(evt.getData());
                        found.setUserObject(myHome);

                        keyToRefresh = getKey(myHome);
                        indexModel.put(keyToRefresh, found);

                    } catch (OptimisticLockException ole) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        OPDE.warn(logger, ole);
                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage(ole));
                    } catch (Exception e) {
                        em.getTransaction().rollback();
                        OPDE.fatal(logger, e);
                    } finally {
                        em.close();
                        ((PnlHomes) evt.getSource()).cleanup(); // this is a one time use panel. will be replaced with the next refresh
                        refresh = true;
                    }
                }


            }, () -> (Homes) indexModel.get(key).getUserObject()));
        } else if (node.getUserObject() instanceof Rooms) {
            cps.add(new PnlRooms(new DataChangeListener<Rooms>() {
                @Override
                public void dataChanged(DataChangeEvent evt) {
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        Rooms myRoom = em.merge((Rooms) evt.getData());
                        em.getTransaction().commit();

                        DefaultMutableTreeNode found = find(evt.getData());
                        found.setUserObject(myRoom);

                        keyToRefresh = getKey(myRoom);

                        indexModel.put(keyToRefresh, found);
                    } catch (OptimisticLockException ole) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        OPDE.warn(logger, ole);
                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage(ole));
                    } catch (Exception e) {
                        em.getTransaction().rollback();
                        OPDE.fatal(logger, e);
                    } finally {
                        em.close();
                        ((PnlRooms) evt.getSource()).cleanup(); // this is a one time use panel. will be replaced with the next refresh
                        refresh = true;
                    }
                }
            }, () -> (Rooms) indexModel.get(key).getUserObject()));
        } else if (node.getUserObject() instanceof Floors) {


            try {
                PnlBeanEditor<Floors> pbe = new PnlBeanEditor<>(new DataChangeListener<Floors>() {
                    @Override
                    public void dataChanged(DataChangeEvent evt) {

                    }
                }, () -> (Floors) indexModel.get(key).getUserObject(), Floors.class, new String[]{"name", "level", "lift"}, PnlBeanEditor.SAVE_MODE_IMMEDIATE);
                cps.add(pbe);
            } catch (Exception e) {
                OPDE.fatal(logger, e);
            }


        }

        while (en.hasMoreElements()) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) en.nextElement();
            cps.add(createCP(child));
        }




        cps.addExpansion();

        return cps;
    }


//    private JPanel createContent4Floors(DefaultMutableTreeNode node) {
////        JPanel pnl = new JPanel();
////        pnl.setLayout(new BoxLayout(pnl, BoxLayout.PAGE_AXIS));
//////        pnl.add(new JPanel().add(createAddRoomButton()));
//
//
//        CollapsiblePanes cps = new CollapsiblePanes();
//        Enumeration en = node.children();
//
//        while (en.hasMoreElements()) {
//            DefaultMutableTreeNode child = (DefaultMutableTreeNode) en.nextElement();
//            cps.add(createCP(child));
//        }
//
//        return cps;
//
//    }


//    private JPanel createContent4Floor(DefaultMutableTreeNode node) {
//
//            CollapsiblePanes cps = new CollapsiblePanes();
//            Enumeration en = node.children();
//
//            while (en.hasMoreElements()) {
//                DefaultMutableTreeNode child = (DefaultMutableTreeNode) en.nextElement();
//                cps.add(createCP(child));
//            }
//
//            return cps;
//        }


//    private JPanel createContent4Rooms(DefaultMutableTreeNode node) {
//        JPanel pnl = new JPanel();
//        pnl.setLayout(new BoxLayout(pnl, BoxLayout.PAGE_AXIS));
////        pnl.add(new JPanel().add(createAddRoomButton()));
//
//        Enumeration en = node.children();
//
//        while (en.hasMoreElements()) {
//            DefaultMutableTreeNode child = (DefaultMutableTreeNode) en.nextElement();
//            Rooms room = (Rooms) child.getUserObject();
//            DefaultCPTitle cptitle = new DefaultCPTitle(room.toString(), null);
//            pnl.add(cptitle.getMain());
//        }
//
//        return pnl;
//    }

    private JPanel createContent4Stations(DefaultMutableTreeNode node) {
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.PAGE_AXIS));
        pnl.add(createAddStationButton());

        Enumeration en = node.children();

        while (en.hasMoreElements()) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) en.nextElement();
            Station station = (Station) child.getUserObject();
            DefaultCPTitle cptitle = new DefaultCPTitle(station.getName(), null);
            pnl.add(cptitle.getMain());
        }

        return pnl;
    }


    private DefaultMutableTreeNode find(Object userObject) {
        DefaultMutableTreeNode found = null;

        Enumeration en = root.breadthFirstEnumeration();
        while (en.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
            if (node.getUserObject() != null && node.getUserObject().equals(userObject)) {
                found = node;
                break;
            }
        }
        return found;
    }


    private void create(Homes home) {

//            pnlContentH.add(cpsInsideHome);
//
//            CollapsiblePane cpRooms = new CollapsiblePane(SYSTools.xx("misc.msg.room"));
//            cpsInsideHome.add(cpRooms);


//        DefaultCPTitle cptitle = new DefaultCPTitle(home.getName(), null);
//        CollapsiblePane cpHome = new CollapsiblePane();
//        cpHome.setCollapsible(false);
//        cpHome.setTitleLabelComponent(cptitle.getMain());
//        cpHome.setSlidingDirection(SwingConstants.SOUTH);


//        final JideButton btnAddRoom = GUITools.createHyperlinkButton("opde.settings.btnAddRoom", SYSConst.icon22add, null);
//        btnAddRoom.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JidePopup popup = GUITools.getTextEditor(null, 1, 40, new Closure() {
//                    @Override
//                    public void execute(Object o) {
//                        //                            if (o != null && !o.toString().trim().isEmpty()) {
//                        //                                EntityManager em = OPDE.createEM();
//                        //                                try {
//                        //                                    em.getTransaction().begin();
//                        //                                    em.merge(new Station(o.toString(), em.merge(home)));
//                        //                                    em.getTransaction().commit();
//                        //                                    createHomesList();
//                        //                                    OPDE.getMainframe().emptySearchArea();
//                        //                                    OPDE.getMainframe().prepareSearchArea();
//                        //                                } catch (Exception e) {
//                        //                                    em.getTransaction().rollback();
//                        //                                    OPDE.fatal(e);
//                        //                                } finally {
//                        //                                    em.close();
//                        //                                }
//                        //                            }
//                    }
//                }, btnAddRoom);
//                GUITools.showPopup(popup, SwingConstants.EAST);
//            }
//        });
//        subtree.add(new DefaultMutableTreeNode(btnAddRoom));


//                String titleR = "<html><font size=+1>" + room.toString() + "</font></html>";
//                DefaultCPTitle cpTitleR = new DefaultCPTitle(titleR, null);
//
//                //                            final JButton btnEditStation = new JButton(SYSConst.icon22edit);
//                //                            btnEditStation.setPressedIcon(SYSConst.icon22Pressed);
//                //                            btnEditStation.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//                //                            btnEditStation.setContentAreaFilled(false);
//                //                            btnEditStation.setBorder(null);
//                //
//                //                            btnEditStation.addActionListener(new ActionListener() {
//                //                                @Override
//                //                                public void actionPerformed(ActionEvent e) {
//                //
//                //                                    final JidePopup popup = GUITools.getTextEditor(station.getName(), 1, 40, new Closure() {
//                //                                        @Override
//                //                                        public void execute(Object o) {
//                //                                            if (o != null && !o.toString().trim().isEmpty()) {
//                //                                                EntityManager em = OPDE.createEM();
//                //                                                try {
//                //                                                    em.getTransaction().begin();
//                //                                                    Station myStation = em.merge(station);
//                //                                                    myStation.setName(o.toString().trim());
//                //                                                    em.getTransaction().commit();
//                //                                                    createHomesList();
//                //                                                    OPDE.getMainframe().emptySearchArea();
//                //                                                    OPDE.getMainframe().prepareSearchArea();
//                //                                                } catch (Exception e) {
//                //                                                    em.getTransaction().rollback();
//                //                                                    OPDE.fatal(e);
//                //                                                } finally {
//                //                                                    em.close();
//                //                                                }
//                //                                            }
//                //                                        }
//                //                                    }, btnEditStation);
//                //                                    GUITools.showPopup(popup, SwingConstants.EAST);
//                //                                }
//                //                            });
//                //
//                //                            cpTitleS.getRight().add(btnEditStation);
//
//
//                //                            if (station.getResidents().isEmpty()) {
//                //                                /***
//                //                                 *          _      _      _             _        _   _
//                //                                 *       __| | ___| | ___| |_ ___   ___| |_ __ _| |_(_) ___  _ __
//                //                                 *      / _` |/ _ \ |/ _ \ __/ _ \ / __| __/ _` | __| |/ _ \| '_ \
//                //                                 *     | (_| |  __/ |  __/ ||  __/ \__ \ || (_| | |_| | (_) | | | |
//                //                                 *      \__,_|\___|_|\___|\__\___| |___/\__\__,_|\__|_|\___/|_| |_|
//                //                                 *
//                //                                 */
//                //                                final JButton btnDeleteStation = new JButton(SYSConst.icon22delete);
//                //                                btnDeleteStation.setPressedIcon(SYSConst.icon22Pressed);
//                //                                btnDeleteStation.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//                //                                btnDeleteStation.setContentAreaFilled(false);
//                //                                btnDeleteStation.setBorder(null);
//                //
//                //                                btnDeleteStation.addActionListener(new ActionListener() {
//                //                                    @Override
//                //                                    public void actionPerformed(ActionEvent e) {
//                //                                        new DlgYesNo(SYSTools.xx("misc.questions.delete1") + "<br/><i>" + station.getName() + "</i><br/>" + SYSTools.xx("misc.questions.delete2"), SYSConst.icon48delete, new Closure() {
//                //                                            @Override
//                //                                            public void execute(Object answer) {
//                //                                                if (answer.equals(JOptionPane.YES_OPTION)) {
//                //                                                    EntityManager em = OPDE.createEM();
//                //                                                    try {
//                //                                                        em.getTransaction().begin();
//                //                                                        Station myStation = em.merge(station);
//                //                                                        em.lock(myStation, LockModeType.OPTIMISTIC);
//                //                                                        em.remove(myStation);
//                //                                                        em.getTransaction().commit();
//                //                                                        createHomesList();
//                //                                                        OPDE.getMainframe().emptySearchArea();
//                //                                                        OPDE.getMainframe().prepareSearchArea();
//                //                                                    } catch (RollbackException ole) {
//                //                                                        if (em.getTransaction().isActive()) {
//                //                                                            em.getTransaction().rollback();
//                //                                                        }
//                //                                                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
//                //                                                            OPDE.getMainframe().completeRefresh();
//                //                                                        }
//                //                                                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                //                                                    } catch (Exception e) {
//                //                                                        if (em.getTransaction().isActive()) {
//                //                                                            em.getTransaction().rollback();
//                //                                                        }
//                //                                                        OPDE.fatal(e);
//                //                                                    } finally {
//                //                                                        em.close();
//                //                                                    }
//                //                                                }
//                //                                            }
//                //                                        });
//                //                                    }
//                //                                });
//                //                                cpTitleS.getRight().add(btnDeleteStation);
//
//
//                pnlContentH.add(cpTitleR.getMain());
//
//            }

//        Collections.sort(home.getStations());
//        for (final Station station : home.getStations()) {
//            String titleS = "<html><font size=+1>" + station.getName() + "</font></html>";
//            DefaultCPTitle cpTitleS = new DefaultCPTitle(titleS, null);
//
//            /***
//             *               _ _ _         _        _   _
//             *       ___  __| (_) |_   ___| |_ __ _| |_(_) ___  _ __
//             *      / _ \/ _` | | __| / __| __/ _` | __| |/ _ \| '_ \
//             *     |  __/ (_| | | |_  \__ \ || (_| | |_| | (_) | | | |
//             *      \___|\__,_|_|\__| |___/\__\__,_|\__|_|\___/|_| |_|
//             *
//             */
//            final JButton btnEditStation = new JButton(SYSConst.icon22edit);
//            btnEditStation.setPressedIcon(SYSConst.icon22Pressed);
//            btnEditStation.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//            btnEditStation.setContentAreaFilled(false);
//            btnEditStation.setBorder(null);
//
//            btnEditStation.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//
//                    final JidePopup popup = GUITools.getTextEditor(station.getName(), 1, 40, new Closure() {
//                        @Override
//                        public void execute(Object o) {
//                            if (o != null && !o.toString().trim().isEmpty()) {
//                                EntityManager em = OPDE.createEM();
//                                try {
//                                    em.getTransaction().begin();
//                                    Station myStation = em.merge(station);
//                                    myStation.setName(o.toString().trim());
//                                    em.getTransaction().commit();
//                                    createHomesList();
//                                    OPDE.getMainframe().emptySearchArea();
//                                    OPDE.getMainframe().prepareSearchArea();
//                                } catch (Exception e) {
//                                    em.getTransaction().rollback();
//                                    OPDE.fatal(e);
//                                } finally {
//                                    em.close();
//                                }
//                            }
//                        }
//                    }, btnEditStation);
//                    GUITools.showPopup(popup, SwingConstants.EAST);
//                }
//            });
//
//            cpTitleS.getRight().add(btnEditStation);
//
//
//            if (station.getResidents().isEmpty()) {
//                /***
//                 *          _      _      _             _        _   _
//                 *       __| | ___| | ___| |_ ___   ___| |_ __ _| |_(_) ___  _ __
//                 *      / _` |/ _ \ |/ _ \ __/ _ \ / __| __/ _` | __| |/ _ \| '_ \
//                 *     | (_| |  __/ |  __/ ||  __/ \__ \ || (_| | |_| | (_) | | | |
//                 *      \__,_|\___|_|\___|\__\___| |___/\__\__,_|\__|_|\___/|_| |_|
//                 *
//                 */
//                final JButton btnDeleteStation = new JButton(SYSConst.icon22delete);
//                btnDeleteStation.setPressedIcon(SYSConst.icon22Pressed);
//                btnDeleteStation.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//                btnDeleteStation.setContentAreaFilled(false);
//                btnDeleteStation.setBorder(null);
//
//                btnDeleteStation.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        new DlgYesNo(SYSTools.xx("misc.questions.delete1") + "<br/><i>" + station.getName() + "</i><br/>" + SYSTools.xx("misc.questions.delete2"), SYSConst.icon48delete, new Closure() {
//                            @Override
//                            public void execute(Object answer) {
//                                if (answer.equals(JOptionPane.YES_OPTION)) {
//                                    EntityManager em = OPDE.createEM();
//                                    try {
//                                        em.getTransaction().begin();
//                                        Station myStation = em.merge(station);
//                                        em.lock(myStation, LockModeType.OPTIMISTIC);
//                                        em.remove(myStation);
//                                        em.getTransaction().commit();
//                                        createHomesList();
//                                        OPDE.getMainframe().emptySearchArea();
//                                        OPDE.getMainframe().prepareSearchArea();
//                                    } catch (RollbackException ole) {
//                                        if (em.getTransaction().isActive()) {
//                                            em.getTransaction().rollback();
//                                        }
//                                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
//                                            OPDE.getMainframe().completeRefresh();
//                                        }
//                                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                    } catch (Exception e) {
//                                        if (em.getTransaction().isActive()) {
//                                            em.getTransaction().rollback();
//                                        }
//                                        OPDE.fatal(e);
//                                    } finally {
//                                        em.close();
//                                    }
//                                }
//                            }
//                        });
//                    }
//                });
//                cpTitleS.getRight().add(btnDeleteStation);
//            }
//
//            pnlContentH.add(cpTitleS.getMain());
//
//        }
//        String titleH = "<html><font size=+1><b>" + home.getName() + "</b></font></html>";
//        DefaultCPTitle cpTitleH = new DefaultCPTitle(titleH, null);
//
//        CollapsiblePane cpH = new CollapsiblePane();
//        cpH.setSlidingDirection(SwingConstants.SOUTH);
//        cpH.setHorizontalAlignment(SwingConstants.LEADING);
//        cpH.setOpaque(false);
//        cpH.setTitleLabelComponent(cpTitleH.getMain());
//
//        /***
//         *               _ _ _     _
//         *       ___  __| (_) |_  | |__   ___  _ __ ___   ___
//         *      / _ \/ _` | | __| | '_ \ / _ \| '_ ` _ \ / _ \
//         *     |  __/ (_| | | |_  | | | | (_) | | | | | |  __/
//         *      \___|\__,_|_|\__| |_| |_|\___/|_| |_| |_|\___|
//         *
//         */
//        final JButton btnEditHome = new JButton(SYSConst.icon22edit);
//        btnEditHome.setPressedIcon(SYSConst.icon22Pressed);
//        btnEditHome.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//        btnEditHome.setContentAreaFilled(false);
//        btnEditHome.setBorder(null);
//        btnEditHome.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                final PnlHomes pnlHomes = new PnlHomes(home);
//                GUITools.showPopup(GUITools.createPanelPopup(pnlHomes, new Closure() {
//                    @Override
//                    public void execute(Object o) {
//                        if (o != null) {
//                            EntityManager em = OPDE.createEM();
//                            try {
//                                em.getTransaction().begin();
//                                Homes myHome = em.merge((Homes) o);
//                                em.getTransaction().commit();
//                                createHomesList();
//                                OPDE.getMainframe().emptySearchArea();
//                                OPDE.getMainframe().prepareSearchArea();
//                            } catch (Exception e) {
//                                em.getTransaction().rollback();
//                                OPDE.fatal(e);
//                            } finally {
//                                em.close();
//                            }
//                        }
//                    }
//                }, btnEditHome), SwingConstants.SOUTH_WEST);
//
//            }
//        });
//        cpTitleH.getRight().add(btnEditHome);
//
//        if (home.getStations().isEmpty()) {
//            final JButton btnDeleteHome = new JButton(SYSConst.icon22delete);
//            btnDeleteHome.setPressedIcon(SYSConst.icon22Pressed);
//            btnDeleteHome.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//            btnDeleteHome.setContentAreaFilled(false);
//            btnDeleteHome.setBorder(null);
//
//            btnDeleteHome.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    new DlgYesNo(SYSTools.xx("misc.questions.delete1") + "<br/><i>" + HomesTools.getAsText(home) + "</i><br/>" + SYSTools.xx("misc.questions.delete2"), SYSConst.icon48delete, new Closure() {
//                        @Override
//                        public void execute(Object answer) {
//                            if (answer.equals(JOptionPane.YES_OPTION)) {
//                                EntityManager em = OPDE.createEM();
//                                try {
//                                    em.getTransaction().begin();
//                                    Homes myHome = em.merge(home);
//                                    em.lock(myHome, LockModeType.OPTIMISTIC);
//                                    em.remove(myHome);
//                                    em.getTransaction().commit();
//                                    createHomesList();
//                                    OPDE.getMainframe().emptySearchArea();
//                                    OPDE.getMainframe().prepareSearchArea();
//                                } catch (RollbackException ole) {
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
//                                        OPDE.getMainframe().completeRefresh();
//                                    }
//                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                } catch (Exception e) {
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    OPDE.fatal(e);
//                                } finally {
//                                    em.close();
//                                }
//                            }
//                        }
//                    });
//                }
//            });
//            cpTitleH.getRight().add(btnDeleteHome);
//        }
//
//        cpH.setContentPane(pnlContentH);
//        cpsHomes.add(cpH);
//        root.add(subtree);
    }


//    private JPanel createContentPane4Stations(Homes home) {
//        JPanel pnlContent = new JPanel(new VerticalLayout());
//
//        final JideButton btnAddStation = GUITools.createHyperlinkButton("opde.settings.btnAddStation", SYSConst.icon22add, null);
//        btnAddStation.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JidePopup popup = GUITools.getTextEditor(null, 1, 40, new Closure() {
//                    @Override
//                    public void execute(Object o) {
//                        if (o != null && !o.toString().trim().isEmpty()) {
//                            EntityManager em = OPDE.createEM();
//                            try {
//                                em.getTransaction().begin();
//                                em.merge(new Station(o.toString(), em.merge(home)));
//                                em.getTransaction().commit();
//                                createHomesList();
//                                OPDE.getMainframe().emptySearchArea();
//                                OPDE.getMainframe().prepareSearchArea();
//                            } catch (Exception e) {
//                                em.getTransaction().rollback();
//                                OPDE.fatal(e);
//                            } finally {
//                                em.close();
//                            }
//                        }
//                    }
//                }, btnAddStation);
//                GUITools.showPopup(popup, SwingConstants.EAST);
//            }
//        });
//
//
//        pnlContent.add(btnAddStation);
//
//        return pnlContent;
//    }


//    private JideButton createAddRoomButton(Floors floor) {
//        final JideButton btnAddRoom = GUITools.createHyperlinkButton("opde.settings.btnAddRoom", SYSConst.icon22add, null);
//        btnAddRoom.addActionListener(e -> {
//            final PnlRooms pnlRooms = new PnlRooms(new Rooms("", false, false, floor));
//            JidePopup popup = GUITools.createPanelPopup(pnlRooms, new Closure() {
//                @Override
//                public void execute(Object o) {
//                    if (o != null) {
////                        EntityManager em = OPDE.createEM();
////                        try {
////                            em.getTransaction().begin();
////                            home = em.merge((Homes) o);
////                            create(home);
////                            em.getTransaction().commit();
////
////                        } catch (IllegalStateException ise) {
////                            OPDE.error(ise);
////                        } catch (Exception e) {
////                            em.getTransaction().rollback();
////                            OPDE.fatal(e);
////                        } finally {
////                            em.close();
////                            setRefresh(true);
////                        }
//                    }
//                }
//            }, btnAddRoom);
//            GUITools.showPopup(popup, SwingConstants.EAST);
//            pnlRooms.setStartFocus();
//        });
//
//        btnAddRoom.setAlignmentX(Component.LEFT_ALIGNMENT);
//
//        return btnAddRoom;
//
//    }

    private JideButton createAddHomeButton() {
        final JideButton btnAddHome = GUITools.createHyperlinkButton("opde.settings.btnAddHome", SYSConst.icon22add, null);
        btnAddHome.addActionListener(e -> {

            EntityManager em = OPDE.createEM();
            try {
                em.getTransaction().begin();
                Homes home = em.merge(new Homes(UUID.randomUUID().toString().substring(0, 15)));
                em.getTransaction().commit();
                keyToRefresh = initDataModel(home);
            } catch (IllegalStateException ise) {
                logger.error(ise);
            } catch (Exception ex) {
                em.getTransaction().rollback();
                logger.fatal(ex);
            } finally {
                em.close();
                refresh = true;
            }
        });

        return btnAddHome;
    }


    private JideButton createAddStationButton() {
        final JideButton btnAddHome = GUITools.createHyperlinkButton("opde.settings.btnAddStation", SYSConst.icon22add, null);
//        btnAddHome.addActionListener(e -> {
//            final PnlHomes pnlHomes = new PnlHomes(new Homes(UUID.randomUUID().toString().substring(0, 15)));
//            JidePopup popup = GUITools.createPanelPopup(pnlHomes, new Closure() {
//                @Override
//                public void execute(Object o) {
//                    if (o != null && !o.toString().trim().isEmpty()) {
////                            EntityManager em = OPDE.createEM();
////                            try {
////                                em.getTransaction().begin();
////                                em.merge(new Station(o.toString(), em.merge(home)));
////                                em.getTransaction().commit();
////                                createHomesList();
////                                OPDE.getMainframe().emptySearchArea();
////                                OPDE.getMainframe().prepareSearchArea();
////                            } catch (Exception e) {
////                                em.getTransaction().rollback();
////                                OPDE.fatal(e);
////                            } finally {
////                                em.close();
////                            }
//                    }
//                }
//            }, btnAddHome);
//            GUITools.showPopup(popup, SwingConstants.EAST);
//            pnlHomes.setStartFocus();
//        });

        return btnAddHome;

    }

    private String getKey(Object object) {
        if (object instanceof DefaultMutableTreeNode)
            object = ((DefaultMutableTreeNode) object).getUserObject();
        if (object instanceof Rooms)
            return "room:" + ((Rooms) object).getRoomID();
        if (object instanceof Homes)
            return "home:" + ((Homes) object).getEID();
        if (object instanceof Floors)
            return "floor:" + ((Floors) object).getFloorid();
        if (object instanceof Station)
            return "station:" + ((Station) object).getStatID();

        return null;
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        scrollPane1 = new JScrollPane();
        cpsHomes = new CollapsiblePanes();

        //======== this ========
        setLayout(new FormLayout(
                "default, $lcgap, default:grow, $lcgap, default",
                "default, $lgap, default:grow, $lgap, default"));

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(cpsHomes);
        }
        add(scrollPane1, CC.xy(3, 3, CC.FILL, CC.FILL));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane scrollPane1;
    private CollapsiblePanes cpsHomes;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
