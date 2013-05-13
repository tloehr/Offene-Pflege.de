/*
 * Created by JFormDesigner on Fri Apr 12 15:56:27 CEST 2013
 */

package op.care.info;

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.swing.DefaultOverlayable;
import com.jidesoft.swing.JideBoxLayout;
import entity.info.*;
import op.OPDE;
import op.system.InternalClassACL;
import op.tools.*;
import org.apache.commons.collections.Closure;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.DateMidnight;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyVetoException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Torsten Löhr
 */
public class PnlInformation extends NursingRecordsPanel {


    public static final String internalClassID = "nursingrecords.info";

    private Resident resident;
    private JScrollPane jspSearch;
    private CollapsiblePanes searchPanes;
    private CollapsiblePanes cpsAll;

    private ArrayList<ResInfoCategory> listCategories = new ArrayList<ResInfoCategory>();
    private ArrayList<ResInfo> listAllInfos;
    private ArrayList<ResInfoType> listAllTypes;
    private HashMap<ResInfoCategory, ArrayList<ResInfoType>> mapCat2Type;
    private HashMap<ResInfoType, ArrayList<ResInfo>> mapType2List;
    private HashMap<ResInfo, PnlEditResInfo> mapInfo2Editor;


    public PnlInformation(Resident resident, JScrollPane jspSearch) {
        this.resident = resident;
        this.jspSearch = jspSearch;
        initComponents();
        initPanel();
    }

    public void initPanel() {
        cpsAll = new CollapsiblePanes();
        jspMain.setViewportView(cpsAll);
        mapCat2Type = new HashMap<ResInfoCategory, ArrayList<ResInfoType>>();
        mapType2List = new HashMap<ResInfoType, ArrayList<ResInfo>>();
        listAllInfos = new ArrayList<ResInfo>();
        listAllTypes = new ArrayList<ResInfoType>();
        mapInfo2Editor = new HashMap<ResInfo, PnlEditResInfo>();
        prepareSearchArea();
        reload();
    }

    public void sortData() {
        mapType2List.clear();
        mapCat2Type.clear();

        for (ResInfoCategory cat : listCategories) {
            mapCat2Type.put(cat, new ArrayList<ResInfoType>());
            OPDE.debug(cat.getText());
        }
        for (ResInfoType types : listAllTypes) {
            mapType2List.put(types, new ArrayList<ResInfo>());
            mapCat2Type.get(types.getResInfoCat()).add(types);
        }

        for (ResInfo info : listAllInfos) {
            mapType2List.get(info.getResInfoType()).add(info);
        }
    }

    private void refreshData() {
        cleanup();
        listCategories.addAll(ResInfoCategoryTools.getAll());
        listAllTypes.addAll(ResInfoTypeTools.getAllActive());
        listAllInfos.addAll(ResInfoTools.getAll(resident));
        sortData();
    }

    private void reloadDisplay() {
        cpsAll.removeAll();
        cpsAll.setLayout(new JideBoxLayout(cpsAll, JideBoxLayout.Y_AXIS));
        for (ResInfoCategory cat : listCategories) {

            CollapsiblePane cpCat = new CollapsiblePane(cat.getText());
            cpCat.setFont(SYSConst.ARIAL18BOLD);
            cpCat.setBackground(cat.getColor());

            JPanel pnlTypes = new JPanel();
            pnlTypes.setLayout(new BoxLayout(pnlTypes, BoxLayout.Y_AXIS));


            /***
             *                          _ _     _                        _             _
             *      _ __   ___  ___ ___(_) |__ | | ___    ___ ___  _ __ | |_ ___ _ __ | |_
             *     | '_ \ / _ \/ __/ __| | '_ \| |/ _ \  / __/ _ \| '_ \| __/ _ \ '_ \| __|
             *     | |_) | (_) \__ \__ \ | |_) | |  __/ | (_| (_) | | | | ||  __/ | | | |_
             *     | .__/ \___/|___/___/_|_.__/|_|\___|  \___\___/|_| |_|\__\___|_| |_|\__|
             *     |_|
             */
            for (ResInfoType resInfoType : mapCat2Type.get(cat)) {
                CollapsiblePane cpResInfoType = new CollapsiblePane(resInfoType.getShortDescription());
                cpResInfoType.setFont(SYSConst.ARIAL14BOLD);
                cpResInfoType.setBackground(cat.getColor().brighter());

                JPanel pnlInfos = new JPanel();
                pnlInfos.setLayout(new BoxLayout(pnlInfos, BoxLayout.Y_AXIS));

                CollapsiblePanes cpsType = new CollapsiblePanes();
                cpsType.setLayout(new JideBoxLayout(cpsType, JideBoxLayout.Y_AXIS));

                pnlInfos.add(cpsType);

                cpResInfoType.setContentPane(pnlInfos);

                /***
                 *                 _               _                   _             _
                 *       __ _  ___| |_ _   _  __ _| |   ___ ___  _ __ | |_ ___ _ __ | |_
                 *      / _` |/ __| __| | | |/ _` | |  / __/ _ \| '_ \| __/ _ \ '_ \| __|
                 *     | (_| | (__| |_| |_| | (_| | | | (_| (_) | | | | ||  __/ | | | |_
                 *      \__,_|\___|\__|\__,_|\__,_|_|  \___\___/|_| |_|\__\___|_| |_|\__|
                 *
                 */
                if (mapType2List.get(resInfoType).isEmpty()) {
                    cpsType.add(GUITools.createHyperlinkButton("Bisher kein Eintrag. Jetzt neu hinzufügen.", SYSConst.icon22add, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
//                            Closure closure = new Closure() {
//                                @Override
//                                public void execute(Object o) {
//                                    if (o != null) {
//                                        EntityManager em = OPDE.createEM();
//                                        try {
//                                            em.getTransaction().begin();
//                                            em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
//                                            // so that no conflicts can occur if another user enters a new info at the same time
//                                            em.lock(em.merge(type), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
//                                            final ResInfo newinfo = em.merge((ResInfo) o);
//                                            if (newinfo.getResInfoType().getType() != ResInfoTypeTools.TYPE_DIAGNOSIS) {
//                                                newinfo.setHtml(ResInfoTools.getContentAsHTML(newinfo));
//                                            }
//                                            em.getTransaction().commit();
//
//                                            if (!mapType2InfoList.containsKey(newinfo.getResInfoType())) {
//                                                mapType2InfoList.put(newinfo.getResInfoType(), new ArrayList<ResInfo>());
//                                            }
//                                            mapType2InfoList.get(newinfo.getResInfoType()).add(newinfo);
//                                            Collections.sort(mapType2InfoList.get(newinfo.getResInfoType()));
//                                            createCP4(newinfo.getResInfoType());
//                                            createCP4(newinfo.getResInfoType().getResInfoCat());
//
//                                            if (newinfo.getResInfoType().isAlertType()) {
//                                                GUITools.setResidentDisplay(resident);
//                                            }
//
//                                            if (mapKey2CP.get(keyType).isCollapsed()) {
//                                                try {
//                                                    mapKey2CP.get(keyType).setCollapsed(false);
//                                                } catch (PropertyVetoException e1) {
//                                                    // BAH!
//                                                }
//                                            }
//                                            GUITools.scroll2show(jspInfo, mapKey2CP.get(keyType), cpsInfo, new Closure() {
//                                                @Override
//                                                public void execute(Object o) {
//                                                    GUITools.flashBackground(mapInfo2Panel.get(newinfo), Color.YELLOW, 2);
//                                                }
//                                            });
//                                            buildPanel();
//
//                                        } catch (OptimisticLockException ole) {
//                                            if (em.getTransaction().isActive()) {
//                                                em.getTransaction().rollback();
//                                            }
//                                            if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                                OPDE.getMainframe().emptyFrame();
//                                                OPDE.getMainframe().afterLogin();
//                                            }
//                                            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                        } catch (RollbackException ole) {
//                                            if (em.getTransaction().isActive()) {
//                                                em.getTransaction().rollback();
//                                            }
//                                            if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                                OPDE.getMainframe().emptyFrame();
//                                                OPDE.getMainframe().afterLogin();
//                                            }
//                                            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                        } catch (Exception e) {
//                                            if (em.getTransaction().isActive()) {
//                                                em.getTransaction().rollback();
//                                            }
//                                            OPDE.fatal(e);
//                                        } finally {
//                                            em.close();
//                                        }
//                                    } else {
//                                        OPDE.getDisplayManager().clearSubMessages();
//                                    }
//                                }
//                            };
//
//                            if (type.getType().intValue() == ResInfoTypeTools.TYPE_DIAGNOSIS) {
//                                new DlgDiag(new ResInfo(type, resident), closure);
//                            } else {
//                                new DlgInfo(new ResInfo(type, resident), closure);
//                            }

                        }
                    }));

                } else {
                    for (final ResInfo info : mapType2List.get(resInfoType)) {
                        final CollapsiblePane cpInfo = new CollapsiblePane();

                        String title = DateFormat.getDateInstance().format(info.getFrom()) + " (" + info.getUserON().getFullname() + ") " + " >> " + DateFormat.getDateInstance().format(info.getTo());

                        DefaultCPTitle cptitle = new DefaultCPTitle(title, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {

                            }
                        });


                        JToggleButton btnChange = new JToggleButton(SYSConst.icon22playerPlay);
                        btnChange.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        btnChange.setContentAreaFilled(false);
                        btnChange.setBorder(null);
                        btnChange.setSelectedIcon(SYSConst.icon22playerPlayPressed);

                        cptitle.getRight().add(btnChange);

                        btnChange.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent e) {
                                mapInfo2Editor.get(info).setEnabled(e.getStateChange() == ItemEvent.SELECTED);
                                if (e.getStateChange() == ItemEvent.DESELECTED && mapInfo2Editor.get(info).isChanged()) {
                                    new DlgYesNo("geändert", SYSConst.icon48play, new Closure() {
                                        @Override
                                        public void execute(Object answer) {
                                            if (!answer.equals(JOptionPane.YES_OPTION)) {
                                                mapInfo2Editor.put(info, new PnlEditResInfo(info, null));
                                                cpInfo.setContentPane(mapInfo2Editor.get(info).getPanel());
                                            }
                                        }
                                    });
                                }
                            }
                        });


                        cpInfo.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
                            @Override
                            public void paneExpanding(CollapsiblePaneEvent collapsiblePaneEvent) {
                                if (!mapInfo2Editor.containsKey(info)) {
                                    mapInfo2Editor.put(info, new PnlEditResInfo(info, null));
                                }
                                cpInfo.setContentPane(mapInfo2Editor.get(info).getPanel());
                            }
                        });

                        cpsType.add(cpInfo);

                        // no idea why the above listener doesn't notice the expansion during the init but does so later on
                        if (!info.isClosed()) {
                            mapInfo2Editor.put(info, new PnlEditResInfo(info, null));
                            cpInfo.setContentPane(mapInfo2Editor.get(info).getPanel());
                        }
                        cpInfo.setTitleLabelComponent(cptitle.getMain());
                        try {
                            cpInfo.setCollapsed(info.isClosed());
                        } catch (PropertyVetoException e) {
                            // bah!
                        }

                    }
                }
                cpsType.addExpansion();

                pnlTypes.add(cpResInfoType);

            }

            cpCat.setContentPane(pnlTypes);

            cpsAll.add(cpCat);

        }
        cpsAll.addExpansion();
    }


    @Override
    public void switchResident(Resident resident) {
        this.resident = resident;
        GUITools.setResidentDisplay(resident);
        reload();
    }

    @Override
    public void cleanup() {
        cpsAll.removeAll();
        mapType2List.clear();
        mapCat2Type.clear();
        listAllTypes.clear();
        listCategories.clear();
        listAllInfos.clear();
        mapInfo2Editor.clear();
    }

    @Override
    public void reload() {
        refreshData();
        reloadDisplay();
    }

    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);

        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout());
        mypanel.setBackground(Color.WHITE);

        CollapsiblePane searchPane = new CollapsiblePane(OPDE.lang.getString(internalClassID));
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);


        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }


//            GUITools.addAllComponents(mypanel, addFilters());
//            GUITools.addAllComponents(mypanel, addKey());

        searchPane.setContentPane(mypanel);

        searchPanes.add(searchPane);
        searchPanes.addExpansion();
    }

    @Override
    public String getInternalClassID() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jspMain = new JScrollPane();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(jspMain);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane jspMain;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    private JPanel getMenu(final ResInfo resInfo) {

        final JPanel pnlMenu = new JPanel(new VerticalLayout());

        if (resInfo.getResInfoType().getType() != ResInfoTypeTools.TYPE_ABSENCE && resInfo.getResInfoType().getType() != ResInfoTypeTools.TYPE_STAY) {

            if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
                /***
                 *       ____ _
                 *      / ___| |__   __ _ _ __   __ _  ___
                 *     | |   | '_ \ / _` | '_ \ / _` |/ _ \
                 *     | |___| | | | (_| | | | | (_| |  __/
                 *      \____|_| |_|\__,_|_| |_|\__, |\___|
                 *                              |___/
                 */
                final JButton btnChange = GUITools.createHyperlinkButton(internalClassID + ".btnChange.tooltip", SYSConst.icon22playerPlay, null);
                btnChange.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnChange.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {

                        mapInfo2Editor.get(resInfo).setEnabled(true);

//                            new DlgInfo(resInfo.clone(), new Closure() {
//                                @Override
//                                public void execute(Object o) {
//                                    if (o != null) {
//                                        EntityManager em = OPDE.createEM();
//                                        try {
//                                            em.getTransaction().begin();
//                                            ResInfo oldinfo = em.merge(resInfo);
//                                            ResInfo newinfo = em.merge((ResInfo) o);
//                                            em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
//                                            // so that no conflicts can occur if another user enters a new info at the same time
//                                            em.lock(em.merge(resInfo.getResInfoType()), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
//                                            em.lock(oldinfo, LockModeType.OPTIMISTIC);
//
//                                            newinfo.setHtml(ResInfoTools.getContentAsHTML(newinfo));
//                                            newinfo.setUserON(em.merge(OPDE.getLogin().getUser()));
//                                            newinfo.setFrom(new Date());
//
//                                            oldinfo.setTo(new DateTime(newinfo.getFrom()).minusSeconds(1).toDate());
//                                            oldinfo.setUserOFF(newinfo.getUserON());
//
//                                            em.getTransaction().commit();
//
//                                            mapType2InfoList.get(resInfo.getResInfoType()).remove(resInfo);
//                                            mapType2InfoList.get(oldinfo.getResInfoType()).add(oldinfo);
//                                            mapType2InfoList.get(newinfo.getResInfoType()).add(newinfo);
//                                            Collections.sort(mapType2InfoList.get(newinfo.getResInfoType()));
//                                            CollapsiblePane myCP = createCP4(newinfo.getResInfoType());
//                                            buildPanel();
//                                            GUITools.flashBackground(myCP, Color.YELLOW, 2);
//                                        } catch (OptimisticLockException ole) {
//                                            if (em.getTransaction().isActive()) {
//                                                em.getTransaction().rollback();
//                                            }
//                                            if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                                OPDE.getMainframe().emptyFrame();
//                                                OPDE.getMainframe().afterLogin();
//                                            }
//                                            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                        } catch (RollbackException ole) {
//                                            if (em.getTransaction().isActive()) {
//                                                em.getTransaction().rollback();
//                                            }
//                                            if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                                OPDE.getMainframe().emptyFrame();
//                                                OPDE.getMainframe().afterLogin();
//                                            }
//                                            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                        } catch (Exception e) {
//                                            if (em.getTransaction().isActive()) {
//                                                em.getTransaction().rollback();
//                                            }
//                                            OPDE.fatal(e);
//                                        } finally {
//                                            em.close();
//                                        }
//                                    }
//                                }
//                            });
                    }
                });
                btnChange.setEnabled(resInfo.getResInfoType().getType() != ResInfoTypeTools.TYPE_DIAGNOSIS && resInfo.getResInfoType().getType() != ResInfoTypeTools.TYPE_OLD && !resInfo.isClosed() && !resInfo.isSingleIncident() && !resInfo.isNoConstraints());
                pnlMenu.add(btnChange);


                /***
                 *      ____  _
                 *     / ___|| |_ ___  _ __
                 *     \___ \| __/ _ \| '_ \
                 *      ___) | || (_) | |_) |
                 *     |____/ \__\___/| .__/
                 *                    |_|
                 */
                final JButton btnStop = GUITools.createHyperlinkButton(internalClassID + ".btnStop.tooltip", SYSConst.icon22playerStop, null);
                btnStop.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnStop.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
//                            new DlgYesNo(OPDE.lang.getString("misc.questions.cancel") + "<br/>" + resInfo.getResInfoType().getShortDescription() + "<br/>" + resInfo.getPITAsHTML(), SYSConst.icon48stop, new Closure() {
//                                @Override
//                                public void execute(Object answer) {
//                                    if (answer.equals(JOptionPane.YES_OPTION)) {
//                                        EntityManager em = OPDE.createEM();
//                                        try {
//                                            em.getTransaction().begin();
//                                            ResInfo newinfo = em.merge(resInfo);
//                                            em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
//                                            em.lock(newinfo, LockModeType.OPTIMISTIC);
//                                            newinfo.setTo(new Date());
//                                            newinfo.setUserOFF(em.merge(OPDE.getLogin().getUser()));
//                                            em.getTransaction().commit();
//
//                                            mapType2InfoList.get(resInfo.getResInfoType()).remove(resInfo);
//                                            mapType2InfoList.get(newinfo.getResInfoType()).add(newinfo);
//                                            Collections.sort(mapType2InfoList.get(newinfo.getResInfoType()));
//                                            CollapsiblePane myCP = createCP4(newinfo.getResInfoType());
//
//                                            if (newinfo.getResInfoType().isAlertType()) {
//                                                GUITools.setResidentDisplay(resident);
//                                            }
//
//                                            buildPanel();
//
//                                            GUITools.flashBackground(myCP, Color.YELLOW, 2);
//                                        } catch (OptimisticLockException ole) {
//                                            if (em.getTransaction().isActive()) {
//                                                em.getTransaction().rollback();
//                                            }
//                                            if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                                OPDE.getMainframe().emptyFrame();
//                                                OPDE.getMainframe().afterLogin();
//                                            }
//                                            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                        } catch (Exception e) {
//                                            if (em.getTransaction().isActive()) {
//                                                em.getTransaction().rollback();
//                                            }
//                                            OPDE.fatal(e);
//                                        } finally {
//                                            em.close();
//                                        }
//                                    }
//                                }
//                            });
                    }
                });
                btnStop.setEnabled(!resInfo.isClosed() && !resInfo.isSingleIncident());
                pnlMenu.add(btnStop);

                /***
                 *      _____    _ _ _
                 *     | ____|__| (_) |_
                 *     |  _| / _` | | __|
                 *     | |__| (_| | | |_
                 *     |_____\__,_|_|\__|
                 *
                 */
                final JButton btnEdit = GUITools.createHyperlinkButton(internalClassID + ".btnEdit.tooltip", SYSConst.icon22edit3, null);
                btnEdit.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnEdit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
//                            new DlgInfo(resInfo, new Closure() {
//                                @Override
//                                public void execute(Object o) {
//                                    if (o != null) {
//                                        EntityManager em = OPDE.createEM();
//                                        try {
//                                            em.getTransaction().begin();
//                                            ResInfo editinfo = em.merge((ResInfo) o);
//                                            em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
//                                            em.lock(editinfo, LockModeType.OPTIMISTIC);
//                                            editinfo.setHtml(ResInfoTools.getContentAsHTML(editinfo));
//                                            editinfo.setUserON(em.merge(OPDE.getLogin().getUser()));
//                                            em.getTransaction().commit();
//
//                                            mapType2InfoList.get(resInfo.getResInfoType()).remove(resInfo);
//                                            mapType2InfoList.get(editinfo.getResInfoType()).add(editinfo);
//                                            Collections.sort(mapType2InfoList.get(editinfo.getResInfoType()));
//                                            CollapsiblePane myCP = createCP4(editinfo.getResInfoType());
//                                            buildPanel();
//
//                                            GUITools.flashBackground(mapInfo2Panel.get(editinfo), Color.YELLOW, 2);
//                                        } catch (OptimisticLockException ole) {
//                                            if (em.getTransaction().isActive()) {
//                                                em.getTransaction().rollback();
//                                            }
//                                            if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                                OPDE.getMainframe().emptyFrame();
//                                                OPDE.getMainframe().afterLogin();
//                                            }
//                                            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                        } catch (Exception e) {
//                                            if (em.getTransaction().isActive()) {
//                                                em.getTransaction().rollback();
//                                            }
//                                            OPDE.fatal(e);
//                                        } finally {
//                                            em.close();
//                                        }
//                                    }
//                                }
//                            });
                    }
                });
                // Only active ones can be edited, and only by the same user that started it or the admin.
                btnEdit.setEnabled(ResInfoTools.isEditable(resInfo) && (OPDE.isAdmin() ||
                        (resInfo.getUserON().equals(OPDE.getLogin().getUser()) && new DateMidnight(resInfo.getFrom()).equals(new DateMidnight()))  // The same user only on the same day.
                ));
                pnlMenu.add(btnEdit);
            }


            if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.DELETE, internalClassID)) {
                /***
                 *      ____       _      _
                 *     |  _ \  ___| | ___| |_ ___
                 *     | | | |/ _ \ |/ _ \ __/ _ \
                 *     | |_| |  __/ |  __/ ||  __/
                 *     |____/ \___|_|\___|\__\___|
                 *
                 */
                final JButton btnDelete = GUITools.createHyperlinkButton(internalClassID + ".btnDelete.tooltip", SYSConst.icon22delete, null);
                btnDelete.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnDelete.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
//                            new DlgYesNo(OPDE.lang.getString("misc.questions.delete1") + "<br/><i>" + resInfo.getPITAsHTML() + "</i><br/>" + OPDE.lang.getString("misc.questions.delete2"), SYSConst.icon48delete, new Closure() {
//                                @Override
//                                public void execute(Object answer) {
//                                    if (answer.equals(JOptionPane.YES_OPTION)) {
//                                        EntityManager em = OPDE.createEM();
//                                        try {
//                                            em.getTransaction().begin();
//                                            ResInfo newinfo = em.merge(resInfo);
//                                            em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
//                                            em.lock(newinfo, LockModeType.OPTIMISTIC);
//                                            em.remove(newinfo);
//                                            em.getTransaction().commit();
//
//                                            mapType2InfoList.get(resInfo.getResInfoType()).remove(resInfo);
//                                            createCP4(newinfo.getResInfoType());
//                                            buildPanel();
//                                        } catch (OptimisticLockException ole) {
//                                            if (em.getTransaction().isActive()) {
//                                                em.getTransaction().rollback();
//                                            }
//                                            if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                                OPDE.getMainframe().emptyFrame();
//                                                OPDE.getMainframe().afterLogin();
//                                            }
//                                            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                        } catch (Exception e) {
//                                            if (em.getTransaction().isActive()) {
//                                                em.getTransaction().rollback();
//                                            }
//                                            OPDE.fatal(e);
//                                        } finally {
//                                            em.close();
//                                        }
//                                    }
//                                }
//                            });
                    }
                });
                pnlMenu.add(btnDelete);
            }
            if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
                /***
                 *      ____           _           _
                 *     |  _ \ ___ _ __(_) ___   __| |
                 *     | |_) / _ \ '__| |/ _ \ / _` |
                 *     |  __/  __/ |  | | (_) | (_| |
                 *     |_|   \___|_|  |_|\___/ \__,_|
                 *
                 */
                final JButton btnChangePeriod = GUITools.createHyperlinkButton(internalClassID + ".btnChangePeriod.tooltip", SYSConst.icon22changePeriod, null);
                btnChangePeriod.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnChangePeriod.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
//                            if (resInfo.isSingleIncident()) {
//                                final JidePopup popup = new JidePopup();
//                                PnlPIT pnlPIT = new PnlPIT(resInfo.getFrom(), new Closure() {
//                                    @Override
//                                    public void execute(Object o) {
//                                        popup.hidePopup();
//                                        if (o != null) {
//                                            EntityManager em = OPDE.createEM();
//                                            try {
//                                                em.getTransaction().begin();
//                                                ResInfo editinfo = em.merge(resInfo);
//                                                em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
//                                                em.lock(editinfo, LockModeType.OPTIMISTIC);
//                                                Date date = (Date) o;
//                                                editinfo.setFrom(date);
//                                                editinfo.setTo(date);
//                                                em.getTransaction().commit();
//
//                                                mapType2InfoList.get(resInfo.getResInfoType()).remove(resInfo);
//                                                mapType2InfoList.get(editinfo.getResInfoType()).add(editinfo);
//                                                Collections.sort(mapType2InfoList.get(editinfo.getResInfoType()));
//                                                createCP4(editinfo.getResInfoType());
//                                                buildPanel();
//                                            } catch (OptimisticLockException ole) {
//                                                if (em.getTransaction().isActive()) {
//                                                    em.getTransaction().rollback();
//                                                }
//                                                if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                                    OPDE.getMainframe().emptyFrame();
//                                                    OPDE.getMainframe().afterLogin();
//                                                }
//                                                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                            } catch (Exception e) {
//                                                if (em.getTransaction().isActive()) {
//                                                    em.getTransaction().rollback();
//                                                }
//                                                OPDE.fatal(e);
//                                            } finally {
//                                                em.close();
//                                            }
//                                        }
//                                    }
//                                });
//                                popup.setMovable(false);
//                                popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
//
//                                popup.setOwner(pnlMenu);
//                                popup.removeExcludedComponent(pnlMenu);
//                                popup.getContentPane().add(pnlPIT);
//                                popup.setDefaultFocusComponent(pnlPIT);
//                                GUITools.showPopup(popup, SwingConstants.WEST);
//                            } else {
//                                final JidePopup popup = new JidePopup();
//                                Pair<Date, Date> expansion = ResInfoTools.getMinMaxExpansion(resInfo, mapType2InfoList.get(resInfo.getResInfoType()));
//                                PnlPeriod pnlPeriod = new PnlPeriod(expansion.getFirst(), expansion.getSecond(), resInfo.getFrom(), resInfo.getTo(), new Closure() {
//                                    @Override
//                                    public void execute(Object o) {
//                                        popup.hidePopup();
//                                        if (o != null) {
//                                            EntityManager em = OPDE.createEM();
//                                            try {
//                                                em.getTransaction().begin();
//                                                ResInfo editinfo = em.merge(resInfo);
//                                                em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
//                                                em.lock(editinfo, LockModeType.OPTIMISTIC);
//                                                Pair<Date, Date> period = (Pair<Date, Date>) o;
//                                                editinfo.setFrom(period.getFirst());
//                                                editinfo.setTo(period.getSecond());
//                                                editinfo.setUserOFF(editinfo.getTo().equals(SYSConst.DATE_UNTIL_FURTHER_NOTICE) ? null : em.merge(OPDE.getLogin().getUser()));
//                                                em.getTransaction().commit();
//
//                                                mapType2InfoList.get(resInfo.getResInfoType()).remove(resInfo);
//                                                mapType2InfoList.get(editinfo.getResInfoType()).add(editinfo);
//                                                Collections.sort(mapType2InfoList.get(editinfo.getResInfoType()));
//                                                createCP4(editinfo.getResInfoType());
//                                                buildPanel();
//                                            } catch (OptimisticLockException ole) {
//                                                if (em.getTransaction().isActive()) {
//                                                    em.getTransaction().rollback();
//                                                }
//                                                if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                                    OPDE.getMainframe().emptyFrame();
//                                                    OPDE.getMainframe().afterLogin();
//                                                }
//                                                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                            } catch (Exception e) {
//                                                if (em.getTransaction().isActive()) {
//                                                    em.getTransaction().rollback();
//                                                }
//                                                OPDE.fatal(e);
//                                            } finally {
//                                                em.close();
//                                            }
//                                        }
//
//                                    }
//                                });
//                                popup.setMovable(false);
//                                popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
//
//                                popup.setOwner(pnlMenu);
//                                popup.removeExcludedComponent(pnlMenu);
//                                popup.getContentPane().add(pnlPeriod);
//                                popup.setDefaultFocusComponent(pnlPeriod);
//                                GUITools.showPopup(popup, SwingConstants.WEST);
//
//                            }

                    }
                });
                btnChangePeriod.setEnabled(ResInfoTools.isEditable(resInfo) && !resInfo.isSingleIncident()
                        && (OPDE.isAdmin() ||
                        (resInfo.getUserON().equals(OPDE.getLogin().getUser()) && new DateMidnight(resInfo.getFrom()).equals(new DateMidnight()))  // The same user only on the same day.
                ));
                //            btnChangePeriod.setEnabled(!resInfo.isClosed() && !resInfo.isSingleIncident());
                pnlMenu.add(btnChangePeriod);

                pnlMenu.add(new JSeparator());
            }
        }
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
            /***
             *      _     _         _____ _ _
             *     | |__ | |_ _ __ |  ___(_) | ___  ___
             *     | '_ \| __| '_ \| |_  | | |/ _ \/ __|
             *     | |_) | |_| | | |  _| | | |  __/\__ \
             *     |_.__/ \__|_| |_|_|   |_|_|\___||___/
             *
             */
            final JButton btnFiles = GUITools.createHyperlinkButton(resInfo.isClosed() ? "misc.btnfiles.tooltip.closed" : "misc.btnfiles.tooltip", SYSConst.icon22attach, null);
            btnFiles.setAlignmentX(Component.RIGHT_ALIGNMENT);
//                btnFiles.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent actionEvent) {
//                        // If the closure is null, only attached files can be viewed but no new ones can be attached.
//                        Closure closure = null;
//                        if (!resInfo.isClosed() && resInfo.getResInfoType().getType() != ResInfoTypeTools.TYPE_OLD) {
//                            closure = new Closure() {
//                                @Override
//                                public void execute(Object o) {
//                                    EntityManager em = OPDE.createEM();
//                                    final ResInfo myInfo = em.merge(resInfo);
//                                    em.refresh(myInfo);
//                                    em.close();
//
//                                    mapType2InfoList.get(resInfo.getResInfoType()).remove(resInfo);
//                                    mapType2InfoList.get(myInfo.getResInfoType()).add(myInfo);
//                                    Collections.sort(mapType2InfoList.get(myInfo.getResInfoType()));
//                                    createPanel(myInfo);
//
//                                    buildPanel();
//                                }
//                            };
//                        }
//                        new DlgFiles(resInfo, closure);
//                    }
//                });
            btnFiles.setEnabled(OPDE.isFTPworking());
            pnlMenu.add(btnFiles);


            /***
             *      _     _         ____
             *     | |__ | |_ _ __ |  _ \ _ __ ___   ___ ___  ___ ___
             *     | '_ \| __| '_ \| |_) | '__/ _ \ / __/ _ \/ __/ __|
             *     | |_) | |_| | | |  __/| | | (_) | (_|  __/\__ \__ \
             *     |_.__/ \__|_| |_|_|   |_|  \___/ \___\___||___/___/
             *
             */
            final JButton btnProcess = GUITools.createHyperlinkButton("misc.btnprocess.tooltip", SYSConst.icon22link, null);
            btnProcess.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnProcess.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
//                        new DlgProcessAssign(resInfo, new Closure() {
//                            @Override
//                            public void execute(Object o) {
//                                if (o == null) {
//                                    return;
//                                }
//                                Pair<ArrayList<QProcess>, ArrayList<QProcess>> result = (Pair<ArrayList<QProcess>, ArrayList<QProcess>>) o;
//
//                                ArrayList<QProcess> assigned = result.getFirst();
//                                ArrayList<QProcess> unassigned = result.getSecond();
//
//                                EntityManager em = OPDE.createEM();
//
//                                try {
//                                    em.getTransaction().begin();
//
//                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
//                                    final ResInfo myInfo = em.merge(resInfo);
//                                    em.lock(myInfo, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
//
//                                    ArrayList<SYSINF2PROCESS> attached = new ArrayList<SYSINF2PROCESS>(myInfo.getAttachedQProcessConnections());
//                                    for (SYSINF2PROCESS linkObject : attached) {
//                                        if (unassigned.contains(linkObject.getQProcess())) {
//                                            linkObject.getQProcess().getAttachedNReportConnections().remove(linkObject);
//                                            linkObject.getResInfo().getAttachedQProcessConnections().remove(linkObject);
//                                            em.merge(new PReport(OPDE.lang.getString(PReportTools.PREPORT_TEXT_REMOVE_ELEMENT) + ": " + myInfo.getTitle() + " ID: " + myInfo.getID(), PReportTools.PREPORT_TYPE_REMOVE_ELEMENT, linkObject.getQProcess()));
//                                            em.remove(linkObject);
//                                        }
//                                    }
//                                    attached.clear();
//
//                                    for (QProcess qProcess : assigned) {
//                                        java.util.List<QProcessElement> listElements = qProcess.getElements();
//                                        if (!listElements.contains(myInfo)) {
//                                            QProcess myQProcess = em.merge(qProcess);
//                                            SYSINF2PROCESS myLinkObject = em.merge(new SYSINF2PROCESS(myQProcess, myInfo));
//                                            em.merge(new PReport(OPDE.lang.getString(PReportTools.PREPORT_TEXT_ASSIGN_ELEMENT) + ": " + myInfo.getTitle() + " ID: " + myInfo.getID(), PReportTools.PREPORT_TYPE_ASSIGN_ELEMENT, myQProcess));
//                                            qProcess.getAttachedResInfoConnections().add(myLinkObject);
//                                            myInfo.getAttachedQProcessConnections().add(myLinkObject);
//                                        }
//                                    }
//
//                                    em.getTransaction().commit();
//
//                                    mapType2InfoList.get(resInfo.getResInfoType()).remove(resInfo);
//                                    mapType2InfoList.get(myInfo.getResInfoType()).add(myInfo);
//                                    Collections.sort(mapType2InfoList.get(myInfo.getResInfoType()));
//
//                                    createPanel(myInfo);
//
//                                    buildPanel();
//
//                                } catch (OptimisticLockException ole) {
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                        OPDE.getMainframe().emptyFrame();
//                                        OPDE.getMainframe().afterLogin();
//                                    }
//                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
//                                } catch (RollbackException ole) {
//                                    if (em.getTransaction().isActive()) {
//                                        em.getTransaction().rollback();
//                                    }
//                                    if (ole.getMessage().indexOf("Class> entity.info.Bewohner") > -1) {
//                                        OPDE.getMainframe().emptyFrame();
//                                        OPDE.getMainframe().afterLogin();
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
//
//                            }
//                        });
                }
            });
            btnProcess.setEnabled(ResInfoTools.isEditable(resInfo));

            if (!resInfo.getAttachedQProcessConnections().isEmpty()) {
                JLabel lblNum = new JLabel(Integer.toString(resInfo.getAttachedQProcessConnections().size()), SYSConst.icon16redStar, SwingConstants.CENTER);
                lblNum.setFont(SYSConst.ARIAL14BOLD);
                lblNum.setForeground(Color.YELLOW);
                lblNum.setHorizontalTextPosition(SwingConstants.CENTER);
                DefaultOverlayable overlayableBtn = new DefaultOverlayable(btnProcess, lblNum, DefaultOverlayable.SOUTH_EAST);
                overlayableBtn.setOpaque(false);
                pnlMenu.add(overlayableBtn);
            } else {
                pnlMenu.add(btnProcess);
            }
        }

        return pnlMenu;
    }
}
