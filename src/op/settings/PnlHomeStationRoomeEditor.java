/*
 * Created by JFormDesigner on Wed Apr 29 15:42:58 CEST 2015
 */

package op.settings;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import entity.building.Homes;
import entity.building.HomesTools;
import entity.building.Rooms;
import entity.building.Station;
import op.OPDE;
import op.threads.DisplayManager;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.VerticalLayout;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.RollbackException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

/**
 * @author Torsten Löhr
 */
public class PnlHomeStationRoomeEditor extends JPanel {
    public PnlHomeStationRoomeEditor() {
        initComponents();
        initPanel();
    }




    private CollapsiblePane createCP(Homes home) {
        JPanel pnlContentH = new JPanel(new VerticalLayout());
    }


    private JPanel createContentPane4Stations(Homes home) {
        JPanel pnlContent = new JPanel(new VerticalLayout());

        final JideButton btnAddStation = GUITools.createHyperlinkButton("opde.settings.btnAddStation", SYSConst.icon22add, null);
        btnAddStation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JidePopup popup = GUITools.getTextEditor(null, 1, 40, new Closure() {
                    @Override
                    public void execute(Object o) {
                        if (o != null && !o.toString().trim().isEmpty()) {
                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();
                                em.merge(new Station(o.toString(), em.merge(home)));
                                em.getTransaction().commit();
                                createHomesList();
                                OPDE.getMainframe().emptySearchArea();
                                OPDE.getMainframe().prepareSearchArea();
                            } catch (Exception e) {
                                em.getTransaction().rollback();
                                OPDE.fatal(e);
                            } finally {
                                em.close();
                            }
                        }
                    }
                }, btnAddStation);
                GUITools.showPopup(popup, SwingConstants.EAST);
            }
        });


        pnlContent.add(btnAddStation);

        return pnlContent;
    }

    private void initPanel () {
        ArrayList<Homes> listHomes = HomesTools.getAll();
        cpsHomes.removeAll();
        cpsHomes.setLayout(new JideBoxLayout(cpsHomes, JideBoxLayout.Y_AXIS));
        final JideButton btnAddHome = GUITools.createHyperlinkButton("opde.settings.btnAddHome", SYSConst.icon22add, null);
        btnAddHome.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final PnlHomes pnlHomes = new PnlHomes(new Homes(UUID.randomUUID().toString().substring(0, 15)));
                JidePopup popup = GUITools.createPanelPopup(pnlHomes, new Closure() {
                    @Override
                    public void execute(Object o) {
                        if (o != null) {
                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();
                                Homes home = em.merge((Homes) o);
                                em.getTransaction().commit();
                                // Fix Zendesk #5
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        createHomesList();
                                        OPDE.getMainframe().emptySearchArea();
                                        OPDE.getMainframe().prepareSearchArea();
                                    }
                                });
                            } catch (IllegalStateException ise) {
                                OPDE.error(ise);
                            } catch (Exception e) {
                                em.getTransaction().rollback();
                                OPDE.fatal(e);
                            } finally {
                                em.close();
                            }
                        }
                    }
                }, btnAddHome);
                GUITools.showPopup(popup, SwingConstants.EAST);
                pnlHomes.setStartFocus();
            }
        });
        cpsHomes.add(btnAddHome);
        for (final Homes home : listHomes) {


            //            Collections.sort(home.getRooms());

            CollapsiblePanes cpsInsideHome = new CollapsiblePanes();
            cpsInsideHome.setLayout(new JideBoxLayout(cpsInsideHome, JideBoxLayout.Y_AXIS));

            pnlContentH.add(cpsInsideHome);

            CollapsiblePane cpRooms = new CollapsiblePane(SYSTools.xx("misc.msg.room"));
            cpsInsideHome.add(cpRooms);

            JPanel pnlRooms = new JPanel();
            pnlRooms.setLayout(new BoxLayout(pnlRooms, BoxLayout.PAGE_AXIS));

            final JideButton btnAddRoom = GUITools.createHyperlinkButton("opde.settings.btnAddRoom", SYSConst.icon22add, null);
            btnAddRoom.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JidePopup popup = GUITools.getTextEditor(null, 1, 40, new Closure() {
                        @Override
                        public void execute(Object o) {
                            //                            if (o != null && !o.toString().trim().isEmpty()) {
                            //                                EntityManager em = OPDE.createEM();
                            //                                try {
                            //                                    em.getTransaction().begin();
                            //                                    em.merge(new Station(o.toString(), em.merge(home)));
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
                        }
                    }, btnAddRoom);
                    GUITools.showPopup(popup, SwingConstants.EAST);
                }
            });
            pnlRooms.add(btnAddRoom);

            for (final Rooms room : home.getRooms()) {
                String titleR = "<html><font size=+1>" + room.toString() + "</font></html>";
                DefaultCPTitle cpTitleR = new DefaultCPTitle(titleR, null);

                //                            final JButton btnEditStation = new JButton(SYSConst.icon22edit);
                //                            btnEditStation.setPressedIcon(SYSConst.icon22Pressed);
                //                            btnEditStation.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                //                            btnEditStation.setContentAreaFilled(false);
                //                            btnEditStation.setBorder(null);
                //
                //                            btnEditStation.addActionListener(new ActionListener() {
                //                                @Override
                //                                public void actionPerformed(ActionEvent e) {
                //
                //                                    final JidePopup popup = GUITools.getTextEditor(station.getName(), 1, 40, new Closure() {
                //                                        @Override
                //                                        public void execute(Object o) {
                //                                            if (o != null && !o.toString().trim().isEmpty()) {
                //                                                EntityManager em = OPDE.createEM();
                //                                                try {
                //                                                    em.getTransaction().begin();
                //                                                    Station myStation = em.merge(station);
                //                                                    myStation.setName(o.toString().trim());
                //                                                    em.getTransaction().commit();
                //                                                    createHomesList();
                //                                                    OPDE.getMainframe().emptySearchArea();
                //                                                    OPDE.getMainframe().prepareSearchArea();
                //                                                } catch (Exception e) {
                //                                                    em.getTransaction().rollback();
                //                                                    OPDE.fatal(e);
                //                                                } finally {
                //                                                    em.close();
                //                                                }
                //                                            }
                //                                        }
                //                                    }, btnEditStation);
                //                                    GUITools.showPopup(popup, SwingConstants.EAST);
                //                                }
                //                            });
                //
                //                            cpTitleS.getRight().add(btnEditStation);


                //                            if (station.getResidents().isEmpty()) {
                //                                /***
                //                                 *          _      _      _             _        _   _
                //                                 *       __| | ___| | ___| |_ ___   ___| |_ __ _| |_(_) ___  _ __
                //                                 *      / _` |/ _ \ |/ _ \ __/ _ \ / __| __/ _` | __| |/ _ \| '_ \
                //                                 *     | (_| |  __/ |  __/ ||  __/ \__ \ || (_| | |_| | (_) | | | |
                //                                 *      \__,_|\___|_|\___|\__\___| |___/\__\__,_|\__|_|\___/|_| |_|
                //                                 *
                //                                 */
                //                                final JButton btnDeleteStation = new JButton(SYSConst.icon22delete);
                //                                btnDeleteStation.setPressedIcon(SYSConst.icon22Pressed);
                //                                btnDeleteStation.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                //                                btnDeleteStation.setContentAreaFilled(false);
                //                                btnDeleteStation.setBorder(null);
                //
                //                                btnDeleteStation.addActionListener(new ActionListener() {
                //                                    @Override
                //                                    public void actionPerformed(ActionEvent e) {
                //                                        new DlgYesNo(SYSTools.xx("misc.questions.delete1") + "<br/><i>" + station.getName() + "</i><br/>" + SYSTools.xx("misc.questions.delete2"), SYSConst.icon48delete, new Closure() {
                //                                            @Override
                //                                            public void execute(Object answer) {
                //                                                if (answer.equals(JOptionPane.YES_OPTION)) {
                //                                                    EntityManager em = OPDE.createEM();
                //                                                    try {
                //                                                        em.getTransaction().begin();
                //                                                        Station myStation = em.merge(station);
                //                                                        em.lock(myStation, LockModeType.OPTIMISTIC);
                //                                                        em.remove(myStation);
                //                                                        em.getTransaction().commit();
                //                                                        createHomesList();
                //                                                        OPDE.getMainframe().emptySearchArea();
                //                                                        OPDE.getMainframe().prepareSearchArea();
                //                                                    } catch (RollbackException ole) {
                //                                                        if (em.getTransaction().isActive()) {
                //                                                            em.getTransaction().rollback();
                //                                                        }
                //                                                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                //                                                            OPDE.getMainframe().completeRefresh();
                //                                                        }
                //                                                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                //                                                    } catch (Exception e) {
                //                                                        if (em.getTransaction().isActive()) {
                //                                                            em.getTransaction().rollback();
                //                                                        }
                //                                                        OPDE.fatal(e);
                //                                                    } finally {
                //                                                        em.close();
                //                                                    }
                //                                                }
                //                                            }
                //                                        });
                //                                    }
                //                                });
                //                                cpTitleS.getRight().add(btnDeleteStation);


                pnlContentH.add(cpTitleR.getMain());

            }

            Collections.sort(home.getStations());
            for (final Station station : home.getStations()) {
                String titleS = "<html><font size=+1>" + station.getName() + "</font></html>";
                DefaultCPTitle cpTitleS = new DefaultCPTitle(titleS, null);

                /***
                 *               _ _ _         _        _   _
                 *       ___  __| (_) |_   ___| |_ __ _| |_(_) ___  _ __
                 *      / _ \/ _` | | __| / __| __/ _` | __| |/ _ \| '_ \
                 *     |  __/ (_| | | |_  \__ \ || (_| | |_| | (_) | | | |
                 *      \___|\__,_|_|\__| |___/\__\__,_|\__|_|\___/|_| |_|
                 *
                 */
                final JButton btnEditStation = new JButton(SYSConst.icon22edit);
                btnEditStation.setPressedIcon(SYSConst.icon22Pressed);
                btnEditStation.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnEditStation.setContentAreaFilled(false);
                btnEditStation.setBorder(null);

                btnEditStation.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        final JidePopup popup = GUITools.getTextEditor(station.getName(), 1, 40, new Closure() {
                            @Override
                            public void execute(Object o) {
                                if (o != null && !o.toString().trim().isEmpty()) {
                                    EntityManager em = OPDE.createEM();
                                    try {
                                        em.getTransaction().begin();
                                        Station myStation = em.merge(station);
                                        myStation.setName(o.toString().trim());
                                        em.getTransaction().commit();
                                        createHomesList();
                                        OPDE.getMainframe().emptySearchArea();
                                        OPDE.getMainframe().prepareSearchArea();
                                    } catch (Exception e) {
                                        em.getTransaction().rollback();
                                        OPDE.fatal(e);
                                    } finally {
                                        em.close();
                                    }
                                }
                            }
                        }, btnEditStation);
                        GUITools.showPopup(popup, SwingConstants.EAST);
                    }
                });

                cpTitleS.getRight().add(btnEditStation);


                if (station.getResidents().isEmpty()) {
                    /***
                     *          _      _      _             _        _   _
                     *       __| | ___| | ___| |_ ___   ___| |_ __ _| |_(_) ___  _ __
                     *      / _` |/ _ \ |/ _ \ __/ _ \ / __| __/ _` | __| |/ _ \| '_ \
                     *     | (_| |  __/ |  __/ ||  __/ \__ \ || (_| | |_| | (_) | | | |
                     *      \__,_|\___|_|\___|\__\___| |___/\__\__,_|\__|_|\___/|_| |_|
                     *
                     */
                    final JButton btnDeleteStation = new JButton(SYSConst.icon22delete);
                    btnDeleteStation.setPressedIcon(SYSConst.icon22Pressed);
                    btnDeleteStation.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    btnDeleteStation.setContentAreaFilled(false);
                    btnDeleteStation.setBorder(null);

                    btnDeleteStation.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            new DlgYesNo(SYSTools.xx("misc.questions.delete1") + "<br/><i>" + station.getName() + "</i><br/>" + SYSTools.xx("misc.questions.delete2"), SYSConst.icon48delete, new Closure() {
                                @Override
                                public void execute(Object answer) {
                                    if (answer.equals(JOptionPane.YES_OPTION)) {
                                        EntityManager em = OPDE.createEM();
                                        try {
                                            em.getTransaction().begin();
                                            Station myStation = em.merge(station);
                                            em.lock(myStation, LockModeType.OPTIMISTIC);
                                            em.remove(myStation);
                                            em.getTransaction().commit();
                                            createHomesList();
                                            OPDE.getMainframe().emptySearchArea();
                                            OPDE.getMainframe().prepareSearchArea();
                                        } catch (RollbackException ole) {
                                            if (em.getTransaction().isActive()) {
                                                em.getTransaction().rollback();
                                            }
                                            if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                                                OPDE.getMainframe().completeRefresh();
                                            }
                                            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                                        } catch (Exception e) {
                                            if (em.getTransaction().isActive()) {
                                                em.getTransaction().rollback();
                                            }
                                            OPDE.fatal(e);
                                        } finally {
                                            em.close();
                                        }
                                    }
                                }
                            });
                        }
                    });
                    cpTitleS.getRight().add(btnDeleteStation);
                }

                pnlContentH.add(cpTitleS.getMain());

            }
            String titleH = "<html><font size=+1><b>" + home.getName() + "</b></font></html>";
            DefaultCPTitle cpTitleH = new DefaultCPTitle(titleH, null);

            CollapsiblePane cpH = new CollapsiblePane();
            cpH.setSlidingDirection(SwingConstants.SOUTH);
            cpH.setHorizontalAlignment(SwingConstants.LEADING);
            cpH.setOpaque(false);
            cpH.setTitleLabelComponent(cpTitleH.getMain());

            /***
             *               _ _ _     _
             *       ___  __| (_) |_  | |__   ___  _ __ ___   ___
             *      / _ \/ _` | | __| | '_ \ / _ \| '_ ` _ \ / _ \
             *     |  __/ (_| | | |_  | | | | (_) | | | | | |  __/
             *      \___|\__,_|_|\__| |_| |_|\___/|_| |_| |_|\___|
             *
             */
            final JButton btnEditHome = new JButton(SYSConst.icon22edit);
            btnEditHome.setPressedIcon(SYSConst.icon22Pressed);
            btnEditHome.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnEditHome.setContentAreaFilled(false);
            btnEditHome.setBorder(null);
            btnEditHome.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    final PnlHomes pnlHomes = new PnlHomes(home);
                    GUITools.showPopup(GUITools.createPanelPopup(pnlHomes, new Closure() {
                        @Override
                        public void execute(Object o) {
                            if (o != null) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    Homes myHome = em.merge((Homes) o);
                                    em.getTransaction().commit();
                                    createHomesList();
                                    OPDE.getMainframe().emptySearchArea();
                                    OPDE.getMainframe().prepareSearchArea();
                                } catch (Exception e) {
                                    em.getTransaction().rollback();
                                    OPDE.fatal(e);
                                } finally {
                                    em.close();
                                }
                            }
                        }
                    }, btnEditHome), SwingConstants.SOUTH_WEST);

                }
            });
            cpTitleH.getRight().add(btnEditHome);

            if (home.getStations().isEmpty()) {
                final JButton btnDeleteHome = new JButton(SYSConst.icon22delete);
                btnDeleteHome.setPressedIcon(SYSConst.icon22Pressed);
                btnDeleteHome.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnDeleteHome.setContentAreaFilled(false);
                btnDeleteHome.setBorder(null);

                btnDeleteHome.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new DlgYesNo(SYSTools.xx("misc.questions.delete1") + "<br/><i>" + HomesTools.getAsText(home) + "</i><br/>" + SYSTools.xx("misc.questions.delete2"), SYSConst.icon48delete, new Closure() {
                            @Override
                            public void execute(Object answer) {
                                if (answer.equals(JOptionPane.YES_OPTION)) {
                                    EntityManager em = OPDE.createEM();
                                    try {
                                        em.getTransaction().begin();
                                        Homes myHome = em.merge(home);
                                        em.lock(myHome, LockModeType.OPTIMISTIC);
                                        em.remove(myHome);
                                        em.getTransaction().commit();
                                        createHomesList();
                                        OPDE.getMainframe().emptySearchArea();
                                        OPDE.getMainframe().prepareSearchArea();
                                    } catch (RollbackException ole) {
                                        if (em.getTransaction().isActive()) {
                                            em.getTransaction().rollback();
                                        }
                                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                                            OPDE.getMainframe().completeRefresh();
                                        }
                                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                                    } catch (Exception e) {
                                        if (em.getTransaction().isActive()) {
                                            em.getTransaction().rollback();
                                        }
                                        OPDE.fatal(e);
                                    } finally {
                                        em.close();
                                    }
                                }
                            }
                        });
                    }
                });
                cpTitleH.getRight().add(btnDeleteHome);
            }

            cpH.setContentPane(pnlContentH);
            cpsHomes.add(cpH);

        }
        cpsHomes.addExpansion();
    }

    private void initComponents() {
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
        add(scrollPane1, CC.xy(3, 3, CC.FILL, CC.FILL));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

private JScrollPane scrollPane1;
private CollapsiblePanes cpsHomes;
    private CollapsiblePanes cpsHomes;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
