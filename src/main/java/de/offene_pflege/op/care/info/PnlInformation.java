/*
 * Created by JFormDesigner on Fri Apr 12 15:56:27 CEST 2013
 */

package de.offene_pflege.op.care.info;

/*
 * Created by JFormDesigner on Fri Apr 12 15:56:27 CEST 2013
 */

import com.jidesoft.pane.CollapsiblePane;
import com.jidesoft.pane.CollapsiblePanes;
import com.jidesoft.pane.event.CollapsiblePaneAdapter;
import com.jidesoft.pane.event.CollapsiblePaneEvent;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.DefaultOverlayable;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideButton;
import com.jidesoft.wizard.WizardDialog;
import de.offene_pflege.backend.entity.done.*;
import de.offene_pflege.backend.services.*;
import de.offene_pflege.backend.services.PrescriptionTools;
import de.offene_pflege.backend.entity.process.*;
import de.offene_pflege.backend.entity.system.Commontags;
import de.offene_pflege.backend.entity.system.CommontagsTools;
import de.offene_pflege.backend.entity.system.SYSPropsTools;
import de.offene_pflege.backend.entity.values.ResValue;
import de.offene_pflege.gui.GUITools;
import de.offene_pflege.gui.interfaces.DefaultCPTitle;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.care.PnlCare;
import de.offene_pflege.op.care.sysfiles.DlgFiles;
import de.offene_pflege.op.process.DlgProcessAssign;
import de.offene_pflege.op.residents.DlgEditResidentBaseData;
import de.offene_pflege.op.residents.bwassistant.AddBWWizard;
import de.offene_pflege.op.system.InternalClassACL;
import de.offene_pflege.op.threads.DisplayManager;
import de.offene_pflege.op.threads.DisplayMessage;
import de.offene_pflege.op.tools.*;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.javatuples.Triplet;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.VerticalLayout;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.RollbackException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.text.DateFormat;
import java.util.List;
import java.util.*;

/**
 * @author Torsten Löhr
 */
public class PnlInformation extends NursingRecordsPanel implements HasLogger {

    private Resident resident;
    private JScrollPane jspSearch;
    private final PnlCare pnlCare;
    private CollapsiblePanes searchPanes;
    private CollapsiblePanes cpsAll;
    private JideButton btnResDied, btnResMovedOut, btnResIsAway, btnResIsBack;
    private JXSearchField txtSearch;
    private JList listSearchResults;
    private List<ResInfoCategory> listCategories = new ArrayList<ResInfoCategory>();
    private List<ResInfo> listAllInfos;
    private List<ResInfoType> listAllTypes;
    private Map<ResInfoCategory, ArrayList<ResInfoType>> mapCat2Type;
    private Map<ResInfoType, ArrayList<ResInfo>> mapType2ResInfos;
    private Map<ResInfo, PnlEditResInfo> mapInfo2Editor;
    private Map<String, CollapsiblePane> mapKey2CP;
    // Hier steht immer zu jedem aktuellen ResInfoType der zugehörige Equiv Key drin. Ich brauche nur diese Angabe, weil ich später die ganzen deprecated ResInfos auf diesen Type "buche".
    private Map<Integer, ResInfoType> mapEquiv2Type;
    private Map<String, CollapsiblePaneAdapter> cpListener;
    private List<Commontags> listUsedCommontags;

    public PnlInformation(Resident resident, JScrollPane jspSearch, PnlCare pnlCare) {
        super("nursingrecords.info");
        this.resident = resident;
        this.jspSearch = jspSearch;
        this.pnlCare = pnlCare;
        initComponents();
        initPanel();
    }

    public void initPanel() {
        cpsAll = new CollapsiblePanes();
        jspMain.setViewportView(cpsAll);
        cpListener = Collections.synchronizedMap(new HashMap<>());
        mapCat2Type = Collections.synchronizedMap(new HashMap<>());
        mapType2ResInfos = Collections.synchronizedMap(new HashMap<>());
        listAllInfos = Collections.synchronizedList(new ArrayList<>());
        listAllTypes = Collections.synchronizedList(new ArrayList<>());
        mapInfo2Editor = Collections.synchronizedMap(new HashMap<>());
        mapKey2CP = Collections.synchronizedMap(new HashMap<>());
        mapEquiv2Type = Collections.synchronizedMap(new HashMap<>());
        listUsedCommontags = Collections.synchronizedList(new ArrayList<>());
        prepareSearchArea();
        switchResident(resident);
    }

    public void sortData() {
        synchronized (mapType2ResInfos) {
            mapType2ResInfos.clear();
        }
        synchronized (mapCat2Type) {
            mapCat2Type.clear();
        }

        synchronized (listCategories) {
            Collections.sort(listCategories);
        }

        synchronized (mapCat2Type) {
            for (ResInfoCategory cat : listCategories) {
                mapCat2Type.put(cat, new ArrayList<>());
            }
        }
        for (ResInfoType type : listAllTypes) {
            synchronized (mapType2ResInfos) {
                mapType2ResInfos.put(type, new ArrayList<>()); // erstellt schonmal eine leere Liste für jeden aktiven Type
            }
            synchronized (mapCat2Type) { // Ordnet die Types den Kategorien zu
                mapCat2Type.get(type.getResInfoCat()).add(type);
            }

            if (type.getEquiv() > 0) { // falls der Type zu einer Gruppe gehört, wir hier schonmal eine leere Liste dafür angelegt. und dieser type hinzu gefügt. Somit ist der Erste immer der aktuelle
                synchronized (mapEquiv2Type) {
                    mapEquiv2Type.put(type.getEquiv(), type);
                }
            }
        }

        synchronized (mapType2ResInfos) {
            for (ResInfo info : listAllInfos) { // hier stehen alle Infos drin, egal ob aktiv oder nicht, egal ob die TYPES deprecated sind oder nicht
                // Hier sortiere ich die Infos entweder auf ihren Type, oder (falls sie deprecated sind) auf den Type zu dem sie gehören (über equiv).
                if (mapType2ResInfos.containsKey(info.getResInfoType())) { // das kann nur bei aktiven ResinfoTypes der Fall sein
                    mapType2ResInfos.get(info.getResInfoType()).add(info);
                } else if (mapEquiv2Type.containsKey(info.getResInfoType().getEquiv())) { // dieser ResInfoType ist deprecated, kommt somit auf den Austausch Type drauf. Ich achte darauf, dass es keine Types ohne Austausch gibt.
                    mapType2ResInfos.get(mapEquiv2Type.get(info.getResInfoType().getEquiv())).add(info);
                }
            }

            for (ResInfoType type : listAllTypes) {
                if (mapType2ResInfos.containsKey(type)) {
                    Collections.sort(mapType2ResInfos.get(type));
                }
            }
        }
    }

    private void refreshData() {
        cleanup();
        synchronized (listCategories) {
            listCategories.addAll(ResInfoCategoryService.getAll());
        }
        synchronized (listAllTypes) {
            listAllTypes.addAll(ResInfoTypeTools.getAllActive());
        }
        synchronized (listAllInfos) {
            listAllInfos.addAll(ResInfoService.getAll(resident));
        }
        sortData();
    }

    private void reloadDisplay() {

        OPDE.getMainframe().setBlocked(true);
        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), -1, 100));


        cpsAll.removeAll();
        cpsAll.setLayout(new JideBoxLayout(cpsAll, JideBoxLayout.Y_AXIS));


        SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                int progress = 0;

                try {
                    for (final ResInfoCategory cat : listCategories) {
                        progress++;
                        OPDE.getDisplayManager().setProgressBarMessage(new DisplayMessage(SYSTools.xx("misc.msg.wait"), progress, listCategories.size()));

                        if (!mapCat2Type.get(cat).isEmpty()) {

                            final String keyResInfoCat = cat.getID() + ".resinfocat";
                            if (!mapKey2CP.containsKey(keyResInfoCat)) {
                                synchronized (mapKey2CP) {
                                    mapKey2CP.put(keyResInfoCat, new CollapsiblePane());
                                }
                                mapKey2CP.get(keyResInfoCat).setStyle(CollapsiblePane.TREE_STYLE);


                                CollapsiblePaneAdapter adapter = new CollapsiblePaneAdapter() {
                                    @Override
                                    public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                                        SYSPropsTools.storeProp(keyResInfoCat + ".expanded", "true", OPDE.getLogin().getUser());
                                    }

                                    @Override
                                    public void paneCollapsed(CollapsiblePaneEvent collapsiblePaneEvent) {
                                        SYSPropsTools.storeProp(keyResInfoCat + ".expanded", "false", OPDE.getLogin().getUser());
                                    }
                                };
                                synchronized (cpListener) {
                                    if (cpListener.containsKey(keyResInfoCat)) {
                                        mapKey2CP.get(keyResInfoCat).removeCollapsiblePaneListener(cpListener.get(keyResInfoCat));
                                    }
                                    cpListener.put(keyResInfoCat, adapter);
                                }

                                mapKey2CP.get(keyResInfoCat).addCollapsiblePaneListener(adapter);

                                try {
                                    mapKey2CP.get(keyResInfoCat).setCollapsed(!SYSPropsTools.isBooleanTrue(keyResInfoCat + ".expanded"));
                                } catch (PropertyVetoException e) {
                                    // Bah!
                                }

                            }

                            final CollapsiblePane cpCat = mapKey2CP.get(keyResInfoCat);
                            final DefaultCPTitle cpTitleCat = new DefaultCPTitle(cat.getText(), e -> {
                                try {
                                    cpCat.setCollapsed(!cpCat.isCollapsed());
                                } catch (PropertyVetoException pve) {
                                    // BAH!
                                }
                            });
//                            cpTitleCat.getRight().add(new JButton());


                            /***
                             *      ____       _       _
                             *     |  _ \ _ __(_)_ __ | |_
                             *     | |_) | '__| | '_ \| __|
                             *     |  __/| |  | | | | | |_
                             *     |_|   |_|  |_|_| |_|\__|
                             *
                             */
                            if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.PRINT, internalClassID)) {
                                final JButton btnPrint = GUITools.getTinyButton(SYSTools.xx("misc.commands.print"), SYSConst.icon22print2);
                                btnPrint.addActionListener(e -> {

                                    String html = "";
                                    html += "<h1 id=\"fonth1\" >" + SYSTools.xx("nursingrecords.info");
                                    html += " " + SYSTools.xx("misc.msg.for") + " " + ResidentTools.getLabelText(resident) + "</h1>\n";


                                    html += "<h2 id=\"fonth2\" >" + cat.getText() + "</h2>\n";
                                    for (ResInfoType type : ResInfoTypeTools.getByCat(cat)) {
                                        if (mapType2ResInfos.containsKey(type) && !mapType2ResInfos.get(type).isEmpty()) {
                                            html += "<h3 id=\"fonth3\" >" + type.getShortDescription() + "</h3>\n";

                                            html += type.getType() == ResInfoTypeTools.TYPE_INFECTION ? SYSConst.html_48x48_biohazard : "";
                                            html += type.getType() == ResInfoTypeTools.TYPE_DIABETES ? SYSConst.html_48x48_diabetes : "";
                                            html += type.getType() == ResInfoTypeTools.TYPE_ALLERGY ? SYSConst.html_48x48_allergy : "";
                                            html += type.getType() == ResInfoTypeTools.TYPE_WARNING ? SYSConst.html_48x48_warning : "";
                                            html += type.getType() == ResInfoTypeTools.TYPE_FALLRISK ? SYSConst.html_48x48_warning : "";

                                            html += ResInfoService.getResInfosAsHTML(mapType2ResInfos.get(type), true, null);
                                        }
                                    }

                                    SYSFilesService.print(html, true);
                                });
                                btnPrint.setAlignmentX(Component.RIGHT_ALIGNMENT);

                                cpTitleCat.getRight().add(btnPrint);
                            }


                            GUITools.addExpandCollapseButtons(cpCat, cpTitleCat.getRight());


                            cpTitleCat.getButton().setFont(SYSConst.ARIAL24);
                            cpTitleCat.getButton().setForeground(GUITools.getForeground(GUITools.getColor(cat.getColor())));

                            cpTitleCat.getButton().setForeground(GUITools.getColor(cat.getColor()));
                            cpCat.setBackground(Color.white);
                            cpCat.setTitleLabelComponent(cpTitleCat.getMain());

                            //                cpCat.setBackground(cat.getColor());


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
                            for (final ResInfoType resInfoType : mapCat2Type.get(cat)) {
                                final String keyResInfoType = resInfoType.getID() + ".resinfotype";
                                if (!mapKey2CP.containsKey(keyResInfoType)) {
                                    synchronized (mapKey2CP) {
                                        mapKey2CP.put(keyResInfoType, new CollapsiblePane());
                                    }
                                    mapKey2CP.get(keyResInfoType).setStyle(CollapsiblePane.TREE_STYLE);


                                    CollapsiblePaneAdapter adapter = new CollapsiblePaneAdapter() {
                                        @Override
                                        public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                                            SYSPropsTools.storeProp(keyResInfoType + ".expanded", "true", OPDE.getLogin().getUser());
                                        }

                                        @Override
                                        public void paneCollapsed(CollapsiblePaneEvent collapsiblePaneEvent) {
                                            SYSPropsTools.storeProp(keyResInfoType + ".expanded", "false", OPDE.getLogin().getUser());
                                        }
                                    };

                                    synchronized (cpListener) {
                                        if (cpListener.containsKey(keyResInfoType)) {
                                            mapKey2CP.get(keyResInfoType).removeCollapsiblePaneListener(cpListener.get(keyResInfoType));
                                        }
                                        cpListener.put(keyResInfoType, adapter);
                                    }
                                    mapKey2CP.get(keyResInfoType).addCollapsiblePaneListener(adapter);

                                    try {
                                        mapKey2CP.get(keyResInfoType).setCollapsed(!SYSPropsTools.isBooleanTrue(keyResInfoType + ".expanded"));
                                    } catch (PropertyVetoException e) {
                                        // Bah!
                                    }

                                }

                                CollapsiblePane cpResInfoType = mapKey2CP.get(keyResInfoType);

                                int active = 0;
                                int closed = 0;
                                int single = 0;
                                for (ResInfo resInfo : mapType2ResInfos.get(resInfoType)) {
                                    if (resInfo.isSingleIncident()) {
                                        single++;
                                    } else {
                                        if (resInfo.isClosed()) {
                                            closed++;
                                        } else {
                                            active++;
                                        }
                                    }
                                }

                                DefaultCPTitle dctpt = null;
                                if (resInfoType.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_SINGLE_INCIDENTS) {
                                    dctpt = new DefaultCPTitle(resInfoType.getShortDescription() + " [" + single + "]", null);
//                                    cpResInfoType.setTitleLabelComponent(dctpt.getMain());
                                } else {

                                    if (active + closed == 0) {
                                        dctpt = new DefaultCPTitle(resInfoType.getShortDescription(), null);
                                    } else {
                                        dctpt = new DefaultCPTitle(resInfoType.getShortDescription() + " [" + SYSTools.xx("misc.msg.active") + ": " + active +
                                                " " + SYSTools.xx("misc.msg.closed") + ": " + closed +
                                                "]", null);
                                    }
                                }
                                cpResInfoType.setTitleLabelComponent(dctpt.getMain());
                                /***
                                 *      ____       _       _
                                 *     |  _ \ _ __(_)_ __ | |_
                                 *     | |_) | '__| | '_ \| __|
                                 *     |  __/| |  | | | | | |_
                                 *     |_|   |_|  |_|_| |_|\__|
                                 *
                                 */
                                if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.PRINT, internalClassID)) {
                                    final JButton btnPrint = GUITools.getTinyButton(SYSTools.xx("misc.commands.print"), SYSConst.icon22print2);
                                    btnPrint.addActionListener(e -> {

                                        String html = "";
                                        html += "<h1 id=\"fonth1\" >" + SYSTools.xx("nursingrecords.info");
                                        html += " " + SYSTools.xx("misc.msg.for") + " " + ResidentTools.getLabelText(resident) + "</h1>\n";


                                        html += "<h2 id=\"fonth2\" >" + cat.getText() + "</h2>\n";

                                        if (mapType2ResInfos.containsKey(resInfoType) && !mapType2ResInfos.get(resInfoType).isEmpty()) {
                                            html += "<h3 id=\"fonth3\" >" + resInfoType.getShortDescription() + "</h3>\n";

                                            html += resInfoType.getType() == ResInfoTypeTools.TYPE_INFECTION ? SYSConst.html_48x48_biohazard : "";
                                            html += resInfoType.getType() == ResInfoTypeTools.TYPE_DIABETES ? SYSConst.html_48x48_diabetes : "";
                                            html += resInfoType.getType() == ResInfoTypeTools.TYPE_ALLERGY ? SYSConst.html_48x48_allergy : "";
                                            html += resInfoType.getType() == ResInfoTypeTools.TYPE_WARNING ? SYSConst.html_48x48_warning : "";
                                            html += resInfoType.getType() == ResInfoTypeTools.TYPE_FALLRISK ? SYSConst.html_48x48_warning : "";

                                            html += ResInfoService.getResInfosAsHTML(mapType2ResInfos.get(resInfoType), true, null);
                                        }


                                        SYSFilesService.print(html, true);
                                    });
                                    btnPrint.setAlignmentX(Component.RIGHT_ALIGNMENT);
                                    dctpt.getRight().add(btnPrint);
                                }

                                cpResInfoType.setFont(SYSConst.ARIAL18);

                                cpResInfoType.setForeground((GUITools.blend(cat.getColor(), Color.BLACK, 0.7f)));
                                cpResInfoType.setBackground(Color.WHITE);

                                JPanel pnlInfos = new JPanel();
                                pnlInfos.setLayout(new BoxLayout(pnlInfos, BoxLayout.Y_AXIS));

                                CollapsiblePanes cpsType = new CollapsiblePanes();
                                cpsType.setLayout(new JideBoxLayout(cpsType, JideBoxLayout.Y_AXIS));

                                pnlInfos.add(cpsType);

                                cpResInfoType.setContentPane(pnlInfos);

                                /***
                                 *                _     _
                                 *       __ _  __| | __| |
                                 *      / _` |/ _` |/ _` |
                                 *     | (_| | (_| | (_| |
                                 *      \__,_|\__,_|\__,_|
                                 *
                                 */
                                if (ResidentTools.isActive(resident) &&
                                        (resInfoType.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_SINGLE_INCIDENTS ||
                                                resInfoType.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_NOCONSTRAINTS ||
                                                mapType2ResInfos.get(resInfoType).isEmpty() ||
                                                ResInfoTypeTools.containsOnlyClosedInfos(mapType2ResInfos.get(resInfoType)) ||
                                                ResInfoTypeTools.containsOneActiveObsoleteInfo(mapType2ResInfos.get(resInfoType))
                                        )
                                ) {
                                    if (resInfoType.getType() == ResInfoTypeTools.TYPE_DIAGNOSIS) {
                                        final JideButton btnAdd = GUITools.createHyperlinkButton("nursingrecords.info.noconstraints", SYSConst.icon22add, null);
                                        btnAdd.addActionListener(e -> {
                                            currentEditor = new DlgDiag(ResInfoService.createResInfo(resInfoType, resident), o -> {
                                                if (o != null) {
                                                    EntityManager em = OPDE.createEM();
                                                    try {
                                                        em.getTransaction().begin();
                                                        em.lock(em.merge(resident), LockModeType.OPTIMISTIC);

                                                        final ResInfo newinfo = em.merge((ResInfo) o);

                                                        em.getTransaction().commit();
                                                        synchronized (mapType2ResInfos) {
                                                            mapType2ResInfos.get(newinfo.getResInfoType()).add(newinfo);
                                                            Collections.sort(mapType2ResInfos.get(newinfo.getResInfoType()));
                                                        }

                                                        synchronized (listAllInfos) {
                                                            listAllInfos.add(newinfo);
                                                        }

                                                        reload();

                                                    } catch (OptimisticLockException ole) {
                                                        OPDE.warn(ole);
                                                        if (em.getTransaction().isActive()) {
                                                            em.getTransaction().rollback();
                                                        }
                                                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                                                            OPDE.getMainframe().emptyFrame();
                                                            OPDE.getMainframe().afterLogin();
                                                        }
                                                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                                                    } catch (RollbackException ole) {
                                                        if (em.getTransaction().isActive()) {
                                                            em.getTransaction().rollback();
                                                        }
                                                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                                                            OPDE.getMainframe().emptyFrame();
                                                            OPDE.getMainframe().afterLogin();
                                                        }
                                                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                                                    } catch (Exception e1) {
                                                        if (em.getTransaction().isActive()) {
                                                            em.getTransaction().rollback();
                                                        }
                                                        OPDE.fatal(e1);
                                                    } finally {
                                                        em.close();
                                                    }
                                                }
                                                currentEditor = null;
                                            });
                                            currentEditor.setVisible(true);
                                        });
                                        cpsType.add(btnAdd);
                                    } else if (resInfoType.getType() == ResInfoTypeTools.TYPE_ABSENCE) {
                                        cpsType.add(new JLabel(SYSTools.xx("nursingrecords.info.cant.add.absence.here")));
                                    } else if (resInfoType.getType() == ResInfoTypeTools.TYPE_STAY) {
                                        cpsType.add(new JLabel(SYSTools.xx("nursingrecords.info.cant.add.stays.here")));
                                    } else if (ResInfoTypeTools.is4Annotations(resInfoType)) {
                                        cpsType.add(new JLabel(SYSTools.xx("nursingrecords.info.cant.add.annotations.here")));
                                    } else {

                                        String buttonText = "nursingrecords.info.no.entry.yet";
                                        if (!mapType2ResInfos.get(resInfoType).isEmpty()) {
                                            if (resInfoType.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_SINGLE_INCIDENTS) {
                                                buttonText = "nursingrecords.info.singleincident";
                                            } else if (resInfoType.getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_NOCONSTRAINTS) {
                                                buttonText = "nursingrecords.info.noconstraints";
                                            } else {
                                                buttonText = "nursingrecords.info.only.closed.entries";
                                            }
                                        }


                                        final JideButton btnAdd = GUITools.createHyperlinkButton(buttonText, SYSConst.icon22add, null);
                                        btnAdd.addActionListener(e -> {
                                            // https://github.com/tloehr/Offene-Pflege.de/issues/35
                                            final MyJDialog dlgPopup = new MyJDialog(true);
                                            PnlEditResInfo pnlEditResInfo = new PnlEditResInfo(ResInfoService.createResInfo(resInfoType, resident), o -> {
                                                dlgPopup.dispose();
                                                if (o != null) {

                                                    EntityManager em = OPDE.createEM();
                                                    try {
                                                        em.getTransaction().begin();
                                                        em.lock(em.merge(resident), LockModeType.OPTIMISTIC);

                                                        final ResInfo newinfo = em.merge((ResInfo) o);

                                                        // Herstellung einer Beziehung zwischen den einzelnen ResInfos einer Reihe, die immer durch "CHANGE" geändert wird
                                                        // Spielt bisher nur bei Wunden eine Rolle.
                                                        // Macht nur Sinn bei BY_DAY oder BY_SECOND
                                                        if (newinfo.getResInfoType().getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_BYDAY ||
                                                                newinfo.getResInfoType().getIntervalMode() == ResInfoTypeTools.MODE_INTERVAL_BYSECOND) {
                                                            ResInfoService.setConnectionId(em, newinfo);
                                                        }

//                                                        newinfo.setHtml(ResInfoTools.getContentAsHTML(newinfo));

                                                        // Falls es aktive resinfos gibt, mit einem deprecated Type, dann werden die jetzt abgeschlossen und durch den neuen ersetzt.
                                                        if (!newinfo.isSingleIncident()) {
                                                            for (ResInfo myResInfo : mapType2ResInfos.get(resInfoType)) {
                                                                if (!myResInfo.isClosed() && myResInfo.getResInfoType().isDeprecated()) {
                                                                    ResInfo closedResInfo = em.merge(myResInfo);
                                                                    em.lock(closedResInfo, LockModeType.OPTIMISTIC);
                                                                    ResInfoService.setTo(closedResInfo, new DateTime(newinfo.getFrom()).minusSeconds(1).toDate());
                                                                    closedResInfo.setUserOFF(em.merge(OPDE.getLogin().getUser()));
                                                                }
                                                            }
                                                        }

                                                        em.getTransaction().commit();

                                                        reload();

                                                        if (newinfo.getResInfoType().isAlertType()) {
                                                            GUITools.setResidentDisplay(resident);
                                                        }
                                                        OPDE.getMainframe().addBesonderheit(newinfo.getResInfoType(), resident);

                                                    } catch (OptimisticLockException ole) {
                                                        OPDE.warn(ole);
                                                        if (em.getTransaction().isActive()) {
                                                            em.getTransaction().rollback();
                                                        }
                                                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                                                            OPDE.getMainframe().emptyFrame();
                                                            OPDE.getMainframe().afterLogin();
                                                        }
                                                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                                                    } catch (RollbackException ole) {
                                                        if (em.getTransaction().isActive()) {
                                                            em.getTransaction().rollback();
                                                        }
                                                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                                                            OPDE.getMainframe().emptyFrame();
                                                            OPDE.getMainframe().afterLogin();
                                                        }
                                                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                                                    } catch (Exception e12) {
                                                        if (em.getTransaction().isActive()) {
                                                            em.getTransaction().rollback();
                                                        }
                                                        OPDE.fatal(e12);
                                                    } finally {
                                                        em.close();
                                                    }
                                                }
                                            }, GUITools.getColor(resInfoType.getResInfoCat().getColor()));
                                            pnlEditResInfo.setPanelEnabled(true, PnlEditResInfo.NEW);

                                            dlgPopup.getContentPane().setLayout(new BoxLayout(dlgPopup.getContentPane(), BoxLayout.X_AXIS));
                                            dlgPopup.getContentPane().add(new JScrollPane(pnlEditResInfo.getPanel()));
                                            dlgPopup.pack();
                                            dlgPopup.setVisible(true);
                                        });
                                        cpsType.add(btnAdd);

                                        try {
                                            mapKey2CP.get(keyResInfoType).setCollapsed(true);
                                        } catch (PropertyVetoException e) {
                                            // bah!
                                        }
                                    }
                                }

                                for (final ResInfo info : mapType2ResInfos.get(resInfoType)) {
                                    cpsType.add(createCP4(info));
                                }

                                cpsType.addExpansion();

                                pnlTypes.add(cpResInfoType);

                            }

                            cpCat.setContentPane(pnlTypes);

                            cpsAll.add(cpCat);
                        }

                    }
                    cpsAll.addExpansion();
                } catch (Exception e) {
                    OPDE.fatal(e);
                }
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

    private CollapsiblePane createCP4(final ResInfo resInfo) {
        final String keyResInfo = resInfo.getID() + ".resinfo";
        if (!mapKey2CP.containsKey(keyResInfo)) {
            mapKey2CP.put(keyResInfo, new CollapsiblePane());
            mapKey2CP.get(keyResInfo).setStyle(CollapsiblePane.TREE_STYLE);
        }

        final CollapsiblePane cpInfo = mapKey2CP.get(keyResInfo);

        final boolean normalInfoType = resInfo.getResInfoType().getType() != ResInfoTypeTools.TYPE_ABSENCE && resInfo.getResInfoType().getType() != ResInfoTypeTools.TYPE_STAY;

        String title = "<html><body>";


        if (resInfo.isSingleIncident()) {
            title += DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(resInfo.getFrom()) + " (" + resInfo.getUserON().getFullname() + ")";
        } else {
            title += DateFormat.getDateInstance().format(resInfo.getFrom()) + " (" + (resInfo.getUserON() != null ? resInfo.getUserON().getFullname() : "--") + ") " + " >> ";
            title += resInfo.isClosed() ? DateFormat.getDateInstance().format(resInfo.getTo()) + " (" + resInfo.getUserOFF().getFullname() + ")" : "";
        }

        // Dieser Type ist deprecated, aber es gibt einen Ersatz dafür.
        if (resInfo.getResInfoType().isDeprecated() && mapEquiv2Type.containsKey(resInfo.getResInfoType().getEquiv())) {
            title += " -" + resInfo.getResInfoType().getShortDescription() + "-";
        }

        if (!resInfo.getCommontags().isEmpty()) {
            title += "<br/>" + CommontagsTools.getAsHTML(resInfo.getCommontags(), SYSConst.html_16x16_tagPurple_internal, 4);
        }

        title += "</body></html>";

        DefaultCPTitle cptitle = new DefaultCPTitle(title, e -> {
            try {
                cpInfo.setCollapsed(!cpInfo.isCollapsed());
            } catch (PropertyVetoException pve) {
                // BAH!
            }
        });

        if (resInfo.getResInfoType().isDeprecated()) {
            cptitle.getButton().setIcon(SYSConst.icon22infogray);
            cptitle.getButton().setHorizontalTextPosition(SwingConstants.LEADING);
            cptitle.getButton().setToolTipText(SYSTools.toHTMLForScreen(SYSTools.xx("nursingrecords.info.outdated.form.explanation")));
        }

        if (!resInfo.isClosed() || !resInfo.isSingleIncident()) {
            cptitle.getButton().setForeground(GUITools.blend(resInfo.getResInfoType().getResInfoCat().getColor(), Color.BLACK, 0.25f));
            cpInfo.setBackground(Color.WHITE);
        }

        // das hier sind die Files und die Processes die angezeigt werden, wenn schon welche dran hängen
        if (!resInfo.getAttachedFilesConnections().isEmpty()) {
            /***
             *      _     _         _____ _ _
             *     | |__ | |_ _ __ |  ___(_) | ___  ___
             *     | '_ \| __| '_ \| |_  | | |/ _ \/ __|
             *     | |_) | |_| | | |  _| | | |  __/\__ \
             *     |_.__/ \__|_| |_|_|   |_|_|\___||___/
             *
             */
            final JButton btnFiles = new JButton(Integer.toString(resInfo.getAttachedFilesConnections().size()), SYSConst.icon22greenStar);
            btnFiles.setToolTipText(SYSTools.xx("misc.btnfiles.tooltip"));
            btnFiles.setForeground(Color.BLUE);
            btnFiles.setHorizontalTextPosition(SwingUtilities.CENTER);
            btnFiles.setFont(SYSConst.ARIAL18BOLD);
            btnFiles.setPressedIcon(SYSConst.icon22Pressed);
            btnFiles.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnFiles.setAlignmentY(Component.TOP_ALIGNMENT);
            btnFiles.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnFiles.setContentAreaFilled(false);
            btnFiles.setBorder(null);

            btnFiles.addActionListener(actionEvent -> {
                Closure fileHandleClosure = o -> {
                    EntityManager em = OPDE.createEM();
                    final ResInfo editinfo = em.find(ResInfo.class, resInfo.getID());
                    em.close();

                    // refresh data
                    synchronized (mapType2ResInfos) {
                        if (mapType2ResInfos.containsKey(resInfo.getResInfoType())) { // damit umgehe ich die Exception bei den Deprecated ResInfoTypes. Ist ein Kunstgriff. Will ich hier nicht mehr fixen. Lohnt nicht.
                            int oldIndex = mapType2ResInfos.get(resInfo.getResInfoType()).indexOf(resInfo);
                            mapType2ResInfos.get(resInfo.getResInfoType()).remove(resInfo);
                            mapType2ResInfos.get(editinfo.getResInfoType()).add(oldIndex, editinfo);
                        }
                    }

                    synchronized (listAllInfos) {
                        listAllInfos.remove(resInfo);
                        listAllInfos.add(editinfo);
                    }
                    synchronized (mapKey2CP) {
                        mapKey2CP.remove(keyResInfo);
                    }
                    currentEditor = null;
                    sortData();
                    reloadDisplay();
                };
                currentEditor = new DlgFiles(resInfo, fileHandleClosure);
                currentEditor.setVisible(true);
            });
            btnFiles.setEnabled(OPDE.isFTPworking());
            cptitle.getRight().add(btnFiles);
        }

        if (!resInfo.getAttachedQProcessConnections().isEmpty()) {
            /***
             *      _     _         ____
             *     | |__ | |_ _ __ |  _ \ _ __ ___   ___ ___  ___ ___
             *     | '_ \| __| '_ \| |_) | '__/ _ \ / __/ _ \/ __/ __|
             *     | |_) | |_| | | |  __/| | | (_) | (_|  __/\__ \__ \
             *     |_.__/ \__|_| |_|_|   |_|  \___/ \___\___||___/___/
             *
             */
            final JButton btnProcess = new JButton(Integer.toString(resInfo.getAttachedQProcessConnections().size()), SYSConst.icon22redStar);
            btnProcess.setToolTipText(SYSTools.xx("misc.btnprocess.tooltip"));
            btnProcess.setForeground(Color.YELLOW);
            btnProcess.setHorizontalTextPosition(SwingUtilities.CENTER);
            btnProcess.setFont(SYSConst.ARIAL18BOLD);
            btnProcess.setPressedIcon(SYSConst.icon22Pressed);
            btnProcess.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnProcess.setAlignmentY(Component.TOP_ALIGNMENT);
            btnProcess.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnProcess.setContentAreaFilled(false);
            btnProcess.setBorder(null);
            btnProcess.addActionListener(actionEvent -> {
                currentEditor = new DlgProcessAssign(resInfo, o -> {
                    if (o == null) {
                        currentEditor = null;
                        return;
                    }
                    Pair<ArrayList<QProcess>, ArrayList<QProcess>> result = (Pair<ArrayList<QProcess>, ArrayList<QProcess>>) o;

                    ArrayList<QProcess> assigned = result.getFirst();
                    ArrayList<QProcess> unassigned = result.getSecond();

                    EntityManager em = OPDE.createEM();

                    try {
                        em.getTransaction().begin();

                        em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                        ResInfo myInfo = em.merge(resInfo);
                        em.lock(myInfo, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                        ArrayList<SYSINF2PROCESS> attached = new ArrayList<SYSINF2PROCESS>(myInfo.getAttachedQProcessConnections());
                        for (SYSINF2PROCESS linkObject : attached) {
                            if (unassigned.contains(linkObject.getQProcess())) {
                                linkObject.getQProcess().getAttachedNReportConnections().remove(linkObject);
                                linkObject.getResInfo().getAttachedQProcessConnections().remove(linkObject);
                                em.merge(new PReport(SYSTools.xx(PReportTools.PREPORT_TEXT_REMOVE_ELEMENT) + ": " + myInfo.titleAsString() + " ID: " + myInfo.getID(), PReportTools.PREPORT_TYPE_REMOVE_ELEMENT, linkObject.getQProcess()));
                                em.remove(linkObject);
                            }
                        }
                        attached.clear();

                        for (QProcess qProcess : assigned) {
                            List<QElement> listElements = qProcess.getElements();
                            if (!listElements.contains(myInfo)) {
                                QProcess myQProcess = em.merge(qProcess);
                                SYSINF2PROCESS myLinkObject = em.merge(new SYSINF2PROCESS(myQProcess, myInfo));
                                em.merge(new PReport(SYSTools.xx(PReportTools.PREPORT_TEXT_ASSIGN_ELEMENT) + ": " + myInfo.titleAsString() + " ID: " + myInfo.getID(), PReportTools.PREPORT_TYPE_ASSIGN_ELEMENT, myQProcess));
                                qProcess.getAttachedResInfoConnections().add(myLinkObject);
                                myInfo.getAttachedQProcessConnections().add(myLinkObject);
                            }
                        }

                        em.getTransaction().commit();

                        synchronized (listAllInfos) {
                            listAllInfos.remove(resInfo);
                            listAllInfos.add(myInfo);
                        }
                        synchronized (mapKey2CP) {
                            mapKey2CP.remove(keyResInfo);
                        }
                        sortData();
                        reloadDisplay();

                    } catch (OptimisticLockException ole) {
                        OPDE.warn(ole);
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                            OPDE.getMainframe().emptyFrame();
                            OPDE.getMainframe().afterLogin();
                        } else {
                            reloadDisplay();
                        }
                    } catch (RollbackException ole) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                            OPDE.getMainframe().emptyFrame();
                            OPDE.getMainframe().afterLogin();
                        }
                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                    } catch (Exception e) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        OPDE.fatal(e);
                    } finally {
                        em.close();
                        currentEditor = null;
                    }

                });
                currentEditor.setVisible(true);
            });
            btnProcess.setEnabled(ResInfoService.isEditable(resInfo) && OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID));
            cptitle.getRight().add(btnProcess);
        }


        final JButton btnPrint = new JButton(SYSConst.icon22print2);
        btnPrint.setPressedIcon(SYSConst.icon22print2Pressed);
        btnPrint.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnPrint.setAlignmentY(Component.TOP_ALIGNMENT);
        btnPrint.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnPrint.setContentAreaFilled(false);
        btnPrint.setBorder(null);
        btnPrint.addActionListener(e -> {
            if (!mapInfo2Editor.containsKey(resInfo)) {
                try {
                    cpInfo.setCollapsed(false);
                } catch (PropertyVetoException e1) {
                    //bah!!
                }
            }

            /**
             * Wenn eine Diagnose, dann immer den HTML Drucken.
             * Wenn CTRL beim clicken gedrükt wurde auch
             * ansonsten, grafische Darstellung
             */
            SwingUtilities.invokeLater(() -> {
                if (resInfo.getResInfoType().getType() == ResInfoTypeTools.TYPE_DIAGNOSIS || GUITools.checkMod(e.getModifiers(), ActionEvent.CTRL_MASK)) {
                    String html = "";
                    html += "<h3 id=\"fonth2\" >" + ResidentTools.getLabelText(resident) + "</h3>\n";
                    ArrayList<ResInfo> list = new ArrayList<ResInfo>();
                    list.add(resInfo);

                    SYSFilesService.print(ResInfoService.getContentAsHTML(resInfo), false);
                } else {
                    mapInfo2Editor.get(resInfo).print();
                }
            });
        });
        cptitle.getRight().add(btnPrint);
        btnPrint.setEnabled(normalInfoType);

        // forward declaration
        final JButton btnEdit = new JButton(SYSConst.icon22edit3);
        final JButton btnMenu = new JButton(SYSConst.icon22menu);
        /***
         *           _
         *       ___| |__   __ _ _ __   __ _  ___
         *      / __| '_ \ / _` | '_ \ / _` |/ _ \
         *     | (__| | | | (_| | | | | (_| |  __/
         *      \___|_| |_|\__,_|_| |_|\__, |\___|
         *                             |___/
         */
        final JButton btnChange = new JButton(SYSConst.icon22playerPlay);
        btnChange.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnChange.setContentAreaFilled(false);
        btnChange.setBorder(null);
        btnChange.setAlignmentY(Component.TOP_ALIGNMENT);
        btnChange.setPressedIcon(SYSConst.icon22Pressed);
        cptitle.getRight().add(btnChange);
        btnChange.addActionListener(e -> {
            if (!mapInfo2Editor.containsKey(resInfo) || cpInfo.isCollapsed()) {
                try {
                    cpInfo.setCollapsed(false);
                } catch (PropertyVetoException e1) {
                    //bah!!
                }
            }
            btnPrint.setEnabled(false);
            btnMenu.setEnabled(false);
            boolean canBeEnabled = ResidentTools.isActive(resident) &&
                    resInfo.getResInfoType().getType() != ResInfoTypeTools.TYPE_DIAGNOSIS &&
                    resInfo.getResInfoType().getType() != ResInfoTypeTools.TYPE_STAY &&
                    !resInfo.getResInfoType().isDeprecated() &&
                    !resInfo.isClosed() &&
                    !resInfo.isSingleIncident() &&
                    !resInfo.isNoConstraints();

            if (canBeEnabled) {
                CollectionUtils.forAllDo(mapInfo2Editor.entrySet(), o -> {
                    PnlEditResInfo pnlEditResInfo = ((Map.Entry<ResInfo, PnlEditResInfo>) o).getValue();
                    if (pnlEditResInfo.isPanel_enabled()) {
                        pnlEditResInfo.cancel();
                    }
                });
            }

            mapInfo2Editor.get(resInfo).setPanelEnabled(canBeEnabled, PnlEditResInfo.CHANGE);
            final boolean wasChangeable = btnChange.isEnabled();
            btnChange.setEnabled(false);
            final boolean wasEditable = btnEdit.isEnabled();
            btnEdit.setEnabled(false);
            mapInfo2Editor.get(resInfo).setClosure(o -> {
                btnPrint.setEnabled(true);
                btnMenu.setEnabled(normalInfoType);
                mapInfo2Editor.get(resInfo).setPanelEnabled(false, PnlEditResInfo.CHANGE);
                btnChange.setEnabled(wasChangeable);
                btnEdit.setEnabled(wasEditable);

                if (o != null) { // Apply gedrückt
                    currentEditor = new DlgYesNo(SYSTools.xx("misc.questions.change1") + "<br/>&raquo;" + resInfo.getResInfoType().getShortDescription() + "&laquo;<br/>" + DateFormat.getDateInstance().format(resInfo.getFrom()) + "<br/>" + SYSTools.xx("misc.questions.change2"), SYSConst.icon48play, answer -> {
                        if (!answer.equals(JOptionPane.YES_OPTION)) {
                            return;
                        }

                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();
                            ResInfo oldinfo = em.merge(resInfo);
                            ResInfo newinfo = em.merge((ResInfo) o);
//                                    mapInfo2Editor.get(resInfo).getResInfo()
                            em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                            // so that no conflicts can occur if another user enters a new info at the same time
                            // todo: leave it for now. BUT this must be changed. You will also conflict with ANY other info for any other resident. You will need an auxiliary locking table like (id, rid, BWINFTYP, VERSION) and lock the records there. The same goes for the allowances. maybe resolve it with a spring technology
                            //
                            em.lock(em.merge(resInfo.getResInfoType()), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                            em.lock(oldinfo, LockModeType.OPTIMISTIC);

//                            newinfo.setHtml(ResInfoTools.getContentAsHTML(newinfo));
                            newinfo.setUserON(em.merge(OPDE.getLogin().getUser()));
                            ResInfoService.setFrom(newinfo, new Date());

                            ResInfoService.setTo(oldinfo, new DateTime(newinfo.getFrom()).minusSeconds(1).toDate());
                            oldinfo.setUserOFF(newinfo.getUserON());

                            em.getTransaction().commit();

                            synchronized (mapType2ResInfos) {
                                mapType2ResInfos.get(oldinfo.getResInfoType()).remove(resInfo);
                                mapType2ResInfos.get(newinfo.getResInfoType()).add(newinfo);
                                mapType2ResInfos.get(newinfo.getResInfoType()).add(oldinfo);
                                Collections.sort(mapType2ResInfos.get(newinfo.getResInfoType()));
                            }

                            synchronized (listAllInfos) {
                                listAllInfos.remove(resInfo);
                                listAllInfos.add(oldinfo);
                                listAllInfos.add(newinfo);
                            }

                            if (newinfo.getResInfoType().isAlertType()) {
                                GUITools.setResidentDisplay(resident);
                                if (newinfo.getResInfoType().getType() == ResInfoTypeTools.TYPE_FALLRISK) {
                                    if (ResInfoService.hasSevereFallRisk(newinfo)) {
                                        OPDE.getMainframe().addBesonderheit(newinfo.getResInfoType(), resident);
                                    } else {
                                        OPDE.getMainframe().removeBesonderheit(newinfo.getResInfoType(), resident);
                                    }
                                }
                            }
                        } catch (OptimisticLockException ole) {
                            OPDE.warn(ole);
                            if (em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }
                            if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                                OPDE.getMainframe().emptyFrame();
                                OPDE.getMainframe().afterLogin();
                            }
                            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                        } catch (Exception ex) {
                            if (em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }
                            OPDE.fatal(ex);
                        } finally {
                            em.close();
                            currentEditor = null;
                        }

                        synchronized (mapKey2CP) {
                            mapKey2CP.remove(keyResInfo);
                        }
                    });
                    currentEditor.setVisible(true);
                } else { // Cancel gedrückt
                    // den bestehenden mit einem neuen überschreiben, der die evtl. Änderungen nicht beinhaltet.
                    mapInfo2Editor.put(resInfo, new PnlEditResInfo(ResInfoService.clone(resInfo), GUITools.getColor(resInfo.getResInfoType().getResInfoCat().getColor())));
                }
                reloadDisplay();
            });
        });


        btnChange.setEnabled(
               ResInfoService.isChangeable(resInfo)
        );

        /***
         *               _ _ _
         *       ___  __| (_) |_
         *      / _ \/ _` | | __|
         *     |  __/ (_| | | |_
         *      \___|\__,_|_|\__|
         *
         */
        btnEdit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEdit.setPressedIcon(SYSConst.icon22Pressed);
        btnEdit.setContentAreaFilled(false);
        btnEdit.setBorder(null);
        btnEdit.setSelectedIcon(SYSConst.icon22edit3Pressed);
        btnEdit.setAlignmentY(Component.TOP_ALIGNMENT);
        cptitle.getRight().add(btnEdit);
        btnEdit.addActionListener(e -> {
            if (!mapInfo2Editor.containsKey(resInfo) || cpInfo.isCollapsed()) {
                try {
                    cpInfo.setCollapsed(false);

                } catch (PropertyVetoException e1) {
                    //bah!!
                }
            }
            btnPrint.setEnabled(false);
            btnMenu.setEnabled(false);
            mapInfo2Editor.get(resInfo).setPanelEnabled(true, PnlEditResInfo.EDIT);
            final boolean wasChangeable = btnChange.isEnabled();
            btnChange.setEnabled(false);
            final boolean wasEditable = btnEdit.isEnabled();
            btnEdit.setEnabled(false);

            mapInfo2Editor.get(resInfo).setClosure(o -> {
                btnPrint.setEnabled(true);
                btnMenu.setEnabled(normalInfoType);
                mapInfo2Editor.get(resInfo).setPanelEnabled(false, PnlEditResInfo.DISPLAY);
                btnChange.setEnabled(wasChangeable);
                btnEdit.setEnabled(wasEditable);

                if (mapInfo2Editor.get(resInfo).isChanged()) {
                    if (o != null) { // Apply wurde gedrückt
                        currentEditor = new DlgYesNo(SYSTools.xx("misc.questions.edit1") + "<br/>&raquo;" + resInfo.getResInfoType().getShortDescription() + "&laquo;<br/>" + DateFormat.getDateInstance().format(resInfo.getFrom()) + "<br/>" + SYSTools.xx("misc.questions.edit2"), SYSConst.icon48play, answer -> {

                            if (!answer.equals(JOptionPane.YES_OPTION)) {
                                return;
                            }

                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();
                                ResInfo editinfo = em.merge(resInfo);

                                ResInfo tmpInfo = (ResInfo) o; //mapInfo2Editor.get(resInfo).getResInfo();
//                                editinfo.setHtml(ResInfoTools.getContentAsHTML(tmpInfo));
                                editinfo.setProperties(tmpInfo.getProperties());
                                editinfo.setText(tmpInfo.getText());
                                if (editinfo.getResValue() != null) {
                                    ResValue oldValue = editinfo.getResValue();
                                    editinfo.setResValue(em.merge(tmpInfo.getResValue()));
                                    em.remove(oldValue);
                                }
                                editinfo.setUserON(em.merge(OPDE.getLogin().getUser()));

                                em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                // so that no conflicts can occur if another user enters a new info at the same time
                                em.lock(em.merge(editinfo.getResInfoType()), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                                em.lock(editinfo, LockModeType.OPTIMISTIC);

                                em.getTransaction().commit();


                                synchronized (mapType2ResInfos) {
                                    int oldIndex = mapType2ResInfos.get(resInfo.getResInfoType()).indexOf(resInfo);
                                    mapType2ResInfos.get(resInfo.getResInfoType()).remove(resInfo);
                                    mapType2ResInfos.get(editinfo.getResInfoType()).add(oldIndex, editinfo);
                                }
                                synchronized (listAllInfos) {
                                    listAllInfos.remove(resInfo);
                                    listAllInfos.add(editinfo);
                                }

                                if (editinfo.getResInfoType().isAlertType()) {
                                    GUITools.setResidentDisplay(resident);

                                    if (editinfo.getResInfoType().getType() == ResInfoTypeTools.TYPE_FALLRISK) {
                                        if (ResInfoService.hasSevereFallRisk(editinfo)) {
                                            OPDE.getMainframe().addBesonderheit(editinfo.getResInfoType(), resident);
                                        } else {
                                            OPDE.getMainframe().removeBesonderheit(editinfo.getResInfoType(), resident);
                                        }
                                    }
                                }


                            } catch (OptimisticLockException ole) {
                                OPDE.warn(ole);
                                if (em.getTransaction().isActive()) {
                                    em.getTransaction().rollback();
                                }
                                if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                                    OPDE.getMainframe().emptyFrame();
                                    OPDE.getMainframe().afterLogin();
                                }
                                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                            } catch (Exception ex) {
                                if (em.getTransaction().isActive()) {
                                    em.getTransaction().rollback();
                                }
                                OPDE.fatal(ex);
                            } finally {
                                em.close();
                                currentEditor = null;
                            }
                            synchronized (mapKey2CP) {
                                mapKey2CP.remove(keyResInfo);
                            }

                        });
                        currentEditor.setVisible(true);
                    } else { // cancel wurde gedrückt
                        // den bestehenden mit einem neuen überschreiben, der die evtl. Änderungen nicht beinhaltet.
                        mapInfo2Editor.put(resInfo, new PnlEditResInfo(ResInfoService.clone(resInfo), GUITools.getColor(resInfo.getResInfoType().getResInfoCat().getColor())));
                    }
                    reloadDisplay();
                }

            });
        });

        // Only active ones can be edited, and only by the same user that started it or the admin.
        btnEdit.setEnabled(ResInfoService.isEditable(resInfo) && (OPDE.isAdmin() ||
                (resInfo.getUserON().equals(OPDE.getLogin().getUser()) && new LocalDate(resInfo.getFrom()).equals(new LocalDate()))  // The same user only on the same day.
        ));


        /***
         *      __  __
         *     |  \/  | ___ _ __  _   _
         *     | |\/| |/ _ \ '_ \| | | |
         *     | |  | |  __/ | | | |_| |
         *     |_|  |_|\___|_| |_|\__,_|
         *
         */

        btnMenu.setPressedIcon(SYSConst.icon22Pressed);
        btnMenu.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnMenu.setAlignmentY(Component.TOP_ALIGNMENT);
        btnMenu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnMenu.setContentAreaFilled(false);
        btnMenu.setBorder(null);
        btnMenu.addActionListener(e -> {
            JidePopup popup = new JidePopup();
            popup.setMovable(false);
            popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
            popup.setOwner(btnMenu);
            popup.removeExcludedComponent(btnMenu);
            JPanel pnl = getMenu(resInfo);
            popup.getContentPane().add(pnl);
            popup.setDefaultFocusComponent(pnl);

            GUITools.showPopup(popup, SwingConstants.WEST);
        });
        cptitle.getRight().add(btnMenu);
//        btnMenu.setPanelEnabled(normalInfoType);


        CollapsiblePaneAdapter adapter = new CollapsiblePaneAdapter() {
            @Override
            public void paneExpanded(CollapsiblePaneEvent collapsiblePaneEvent) {
                if (resInfo.getResInfoType().getType() == ResInfoTypeTools.TYPE_ABSENCE || resInfo.getResInfoType().getType() == ResInfoTypeTools.TYPE_STAY || resInfo.getResInfoType().getType() == ResInfoTypeTools.TYPE_DIAGNOSIS || resInfo.getPrescription() != null) {
                    JTextPane txt = new JTextPane();
                    txt.setContentType("text/html");
                    txt.setEditable(false);


                    String content = "";
                    if (resInfo.getPrescription() != null) {
                        content = PrescriptionTools.getPrescriptionAsHTML(resInfo.getPrescription(), false, false, false, false) + "<hrule/><br/>";
                    }

                    content += ResInfoService.getContentAsHTML(resInfo);

                    txt.setText(SYSTools.toHTMLForScreen(SYSConst.html_fontface + content + "</font>"));//   SYSConst.html_div(resInfo.getContentAsHTML()));
                    cpInfo.setContentPane(new JScrollPane(txt));
                } else {
                    if (!mapInfo2Editor.containsKey(resInfo)) {
                        mapInfo2Editor.put(resInfo, new PnlEditResInfo(ResInfoService.clone(resInfo), GUITools.getColor(resInfo.getResInfoType().getResInfoCat().getColor())));
                    }
                    cpInfo.setContentPane(new JScrollPane(mapInfo2Editor.get(resInfo).getPanel()));
                }

            }
        };


        synchronized (cpListener) {
            if (cpListener.containsKey(keyResInfo)) {
                mapKey2CP.get(keyResInfo).removeCollapsiblePaneListener(cpListener.get(keyResInfo));
            }
            cpListener.put(keyResInfo, adapter);
            mapKey2CP.get(keyResInfo).addCollapsiblePaneListener(adapter);
        }

        cpInfo.setTitleLabelComponent(cptitle.getMain());
        try {
            cpInfo.setCollapsed(resInfo.isClosed() || resInfo.isSingleIncident());
        } catch (PropertyVetoException e) {
            // bah!
        }
        return cpInfo;
    }


    @Override
    public void switchResident(Resident resident) {
        this.resident = resident;
        GUITools.setResidentDisplay(resident);
        reload();
    }

    @Override
    public void cleanup() {
        super.cleanup();
        cpsAll.removeAll();
        synchronized (mapType2ResInfos) {
            mapType2ResInfos.clear();
        }
        synchronized (mapCat2Type) {
            mapCat2Type.clear();
        }
        synchronized (listCategories) {
            listCategories.clear();
        }
        synchronized (listAllTypes) {
            listAllTypes.clear();
        }
        synchronized (listAllInfos) {
            listAllInfos.clear();
        }
        synchronized (listUsedCommontags) {
            listUsedCommontags.clear();
        }
        synchronized (mapInfo2Editor) {
            mapInfo2Editor.clear();
        }
        synchronized (mapKey2CP) {
            mapKey2CP.clear();
        }
        synchronized (mapEquiv2Type) {
            mapEquiv2Type.clear();
        }
        synchronized (cpListener) {
            cpListener.clear();
        }

    }

    @Override
    public void reload() {
        refreshData();
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, internalClassID)) {
            btnResDied.setEnabled(ResidentTools.isActive(resident));
            btnResMovedOut.setEnabled(ResidentTools.isActive(resident));
        }
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
            btnResIsAway.setEnabled(ResidentTools.isActive(resident) && !ResInfoService.isAway(resident));
            btnResIsBack.setEnabled(ResidentTools.isActive(resident) && ResInfoService.isAway(resident));
        }
        reloadDisplay();
    }

    private void prepareSearchArea() {
        searchPanes = new CollapsiblePanes();
        searchPanes.setLayout(new JideBoxLayout(searchPanes, JideBoxLayout.Y_AXIS));
        jspSearch.setViewportView(searchPanes);

        JPanel mypanel = new JPanel();
        mypanel.setLayout(new VerticalLayout());
        mypanel.setBackground(Color.WHITE);

        CollapsiblePane searchPane = new CollapsiblePane(SYSTools.xx(internalClassID));
        searchPane.setStyle(CollapsiblePane.PLAIN_STYLE);
        searchPane.setCollapsible(false);


        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }


        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, internalClassID) || OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
            JPanel cmdPanel = new JPanel();
            CollapsiblePane commandPane = new CollapsiblePane(SYSTools.xx("nursingrecords.info.functions"));
            cmdPanel.setLayout(new VerticalLayout());
            GUITools.addAllComponents(cmdPanel, addCommands());
            commandPane.setContentPane(cmdPanel);
            searchPanes.add(commandPane);
            try {
                commandPane.setCollapsed(true);
            } catch (PropertyVetoException e) {
                //BAH!
            }
        }


        /***
         *      ____       _       _
         *     |  _ \ _ __(_)_ __ | |_
         *     | |_) | '__| | '_ \| __|
         *     |  __/| |  | | | | | |_
         *     |_|   |_|  |_|_| |_|\__|
         *
         */
        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.PRINT, internalClassID)) {
            final JButton btnPrint = GUITools.createHyperlinkButton(SYSTools.xx("misc.commands.print"), SYSConst.icon22print2, e -> {

                String html = "";
                html += "<h1 id=\"fonth1\" >" + SYSTools.xx("nursingrecords.info");
                html += " " + SYSTools.xx("misc.msg.for") + " " + ResidentTools.getLabelText(resident) + "</h1>\n";

                for (ResInfoCategory cat : listCategories) {
                    html += "<h2 id=\"fonth2\" >" + cat.getText() + "</h2>\n";
                    for (ResInfoType type : ResInfoTypeTools.getByCat(cat)) {
                        if (mapType2ResInfos.containsKey(type) && !mapType2ResInfos.get(type).isEmpty()) {
                            html += "<h3 id=\"fonth3\" >" + type.getShortDescription() + "</h3>\n";

                            html += type.getType() == ResInfoTypeTools.TYPE_INFECTION ? SYSConst.html_48x48_biohazard : "";
                            html += type.getType() == ResInfoTypeTools.TYPE_DIABETES ? SYSConst.html_48x48_diabetes : "";
                            html += type.getType() == ResInfoTypeTools.TYPE_ALLERGY ? SYSConst.html_48x48_allergy : "";
                            html += type.getType() == ResInfoTypeTools.TYPE_WARNING ? SYSConst.html_48x48_warning : "";
                            html += type.getType() == ResInfoTypeTools.TYPE_FALLRISK ? SYSConst.html_48x48_warning : "";

                            html += ResInfoService.getResInfosAsHTML(mapType2ResInfos.get(type), true, null);
                        }
                    }
                }
                SYSFilesService.print(html, true);
            });
            btnPrint.setAlignmentX(Component.RIGHT_ALIGNMENT);

            mypanel.add(btnPrint);
        }


        try {
            searchPane.setCollapsed(false);
        } catch (PropertyVetoException e) {
            OPDE.error(e);
        }


        GUITools.addAllComponents(mypanel, addDisplayCommands());
        GUITools.addAllComponents(mypanel, addFilters());


        searchPane.setContentPane(mypanel);

        searchPanes.add(searchPane);
        searchPanes.addExpansion();
    }

    @Override
    public String getInternalClassID() {
        return internalClassID;
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
        final String keyResInfo = resInfo.getID() + ".resinfo";

        if (resInfo.getResInfoType().getType() != ResInfoTypeTools.TYPE_ABSENCE && resInfo.getResInfoType().getType() != ResInfoTypeTools.TYPE_STAY) {

            if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {


                /***
                 *      ____  _
                 *     / ___|| |_ ___  _ __
                 *     \___ \| __/ _ \| '_ \
                 *      ___) | || (_) | |_) |
                 *     |____/ \__\___/| .__/
                 *                    |_|
                 */
                final JButton btnStop = GUITools.createHyperlinkButton("nursingrecords.info.btnStop.tooltip", SYSConst.icon22playerStop, null);
                btnStop.setAlignmentX(Component.RIGHT_ALIGNMENT);
                btnStop.addActionListener(actionEvent -> {
                    currentEditor = new DlgYesNo(SYSTools.xx("misc.questions.cancel") + "<br/>" + resInfo.getResInfoType().getShortDescription() + "<br/>" + resInfo.pitAsHTML(), SYSConst.icon48playerStop, answer -> {
                        if (answer.equals(JOptionPane.YES_OPTION)) {
                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();
                                ResInfo editinfo = em.merge(resInfo);
                                em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                em.lock(editinfo, LockModeType.OPTIMISTIC);
                                ResInfoService.setTo(editinfo, new Date());
                                editinfo.setUserOFF(em.merge(OPDE.getLogin().getUser()));
                                em.getTransaction().commit();

                                synchronized (mapType2ResInfos) {
                                    ResInfoType referenceType = mapType2ResInfos.containsKey(resInfo.getResInfoType()) ? resInfo.getResInfoType() : mapEquiv2Type.get(resInfo.getResInfoType().getEquiv());
                                    // refresh data
                                    int oldIndex = mapType2ResInfos.get(referenceType).indexOf(resInfo);
                                    mapType2ResInfos.get(referenceType).remove(resInfo);
                                    mapType2ResInfos.get(referenceType).add(oldIndex, editinfo);
                                }
                                synchronized (listAllInfos) {
                                    listAllInfos.remove(resInfo);
                                    listAllInfos.add(editinfo);
                                }
                                synchronized (mapKey2CP) {
                                    mapKey2CP.remove(keyResInfo);
                                }

                                sortData();
                                reloadDisplay();
                                if (editinfo.getResInfoType().isAlertType()) {
                                    GUITools.setResidentDisplay(resident);
                                }
                                OPDE.getMainframe().removeBesonderheit(editinfo.getResInfoType(), resident);

                            } catch (OptimisticLockException ole) {
                                OPDE.warn(ole);
                                if (em.getTransaction().isActive()) {
                                    em.getTransaction().rollback();
                                }
                                if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                                    OPDE.getMainframe().emptyFrame();
                                    OPDE.getMainframe().afterLogin();
                                }
                                OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                            } catch (Exception e) {
                                if (em.getTransaction().isActive()) {
                                    em.getTransaction().rollback();
                                }
                                OPDE.fatal(e);
                            } finally {
                                em.close();
                                currentEditor = null;
                            }
                        }
                    });
                    currentEditor.setVisible(true);
                });
                btnStop.setEnabled(!resInfo.isClosed() && !resInfo.isSingleIncident());
                pnlMenu.add(btnStop);


                if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.DELETE, internalClassID)) {
                    /***
                     *      ____       _      _
                     *     |  _ \  ___| | ___| |_ ___
                     *     | | | |/ _ \ |/ _ \ __/ _ \
                     *     | |_| |  __/ |  __/ ||  __/
                     *     |____/ \___|_|\___|\__\___|
                     *
                     */
                    final JButton btnDelete = GUITools.createHyperlinkButton("nursingrecords.info.btnDelete.tooltip", SYSConst.icon22delete, null);
                    btnDelete.setEnabled(ResidentTools.isActive(resident));
                    btnDelete.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    btnDelete.addActionListener(actionEvent -> {
                        currentEditor = new DlgYesNo(SYSTools.xx("misc.questions.delete1") + "<br/><i>" + resInfo.pitAsHTML() + "</i><br/>" + SYSTools.xx("misc.questions.delete2"), SYSConst.icon48delete, answer -> {
                            if (answer.equals(JOptionPane.YES_OPTION)) {
                                EntityManager em = OPDE.createEM();
                                try {
                                    em.getTransaction().begin();
                                    ResInfo editinfo = em.merge(resInfo);

                                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                    em.lock(editinfo, LockModeType.OPTIMISTIC);

                                    if (editinfo.getResValue() != null) {
                                        em.remove(em.merge(editinfo.getResValue()));
                                    }
                                    em.remove(editinfo);

                                    em.getTransaction().commit();


                                    synchronized (mapType2ResInfos) {
                                        // refresh data
                                        ResInfoType referenceType = mapType2ResInfos.containsKey(resInfo.getResInfoType()) ? resInfo.getResInfoType() : mapEquiv2Type.get(resInfo.getResInfoType().getEquiv());
                                        mapType2ResInfos.get(referenceType).remove(resInfo);

                                    }
                                    synchronized (listAllInfos) {
                                        listAllInfos.remove(resInfo);
                                    }
                                    sortData();
                                    synchronized (mapKey2CP) {
                                        mapKey2CP.remove(keyResInfo);
                                    }

                                    reloadDisplay();
                                    if (editinfo.getResInfoType().isAlertType()) {
                                        GUITools.setResidentDisplay(resident);
                                    }
                                    OPDE.getMainframe().removeBesonderheit(editinfo.getResInfoType(), resident);
                                } catch (OptimisticLockException ole) {
                                    OPDE.warn(ole);
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                    if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                                        OPDE.getMainframe().emptyFrame();
                                        OPDE.getMainframe().afterLogin();
                                    }
                                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                                } catch (Exception e) {
                                    if (em.getTransaction().isActive()) {
                                        em.getTransaction().rollback();
                                    }
                                    OPDE.fatal(e);
                                } finally {
                                    em.close();
                                    currentEditor = null;
                                }
                            }
                        });
                        currentEditor.setVisible(true);
                    });
                    pnlMenu.add(btnDelete);
                }

            }
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
            final JButton btnChangePeriod = GUITools.createHyperlinkButton("nursingrecords.info.btnChangePeriod.tooltip", SYSConst.icon22changePeriod, null);
            btnChangePeriod.setAlignmentX(Component.RIGHT_ALIGNMENT);
            btnChangePeriod.addActionListener(actionEvent -> {
                if (resInfo.isSingleIncident()) {
                    final JidePopup popup = new JidePopup();
                    PnlPIT pnlPIT = new PnlPIT(new DateTime(resInfo.getFrom()), null, null, o -> {
                        popup.hidePopup();
                        if (o != null) {
                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();
                                ResInfo editinfo = em.merge(resInfo);
                                em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                em.lock(editinfo, LockModeType.OPTIMISTIC);
                                DateTime date = (DateTime) o;
                                ResInfoService.setFrom(editinfo, date.toDate());
                                ResInfoService.setTo(editinfo, date.toDate());
                                em.getTransaction().commit();

                                synchronized (mapType2ResInfos) {
                                    int oldIndex = mapType2ResInfos.get(resInfo.getResInfoType()).indexOf(resInfo);
                                    mapType2ResInfos.get(resInfo.getResInfoType()).remove(resInfo);
                                    mapType2ResInfos.get(editinfo.getResInfoType()).add(oldIndex, editinfo);
                                }
                                synchronized (listAllInfos) {
                                    listAllInfos.remove(resInfo);
                                    listAllInfos.add(editinfo);
                                }
                                synchronized (mapKey2CP) {
                                    mapKey2CP.remove(keyResInfo);
                                }
                                sortData();
                                reloadDisplay();
                                if (editinfo.getResInfoType().isAlertType() || editinfo.getResInfoType().getType() == ResInfoTypeTools.TYPE_ABSENCE) {
                                    GUITools.setResidentDisplay(resident);
                                    if (editinfo.isClosed()) {
                                        OPDE.getMainframe().removeBesonderheit(editinfo.getResInfoType(), resident);
                                    } else {
                                        OPDE.getMainframe().addBesonderheit(editinfo.getResInfoType(), resident);
                                    }
                                }
                            } catch (OptimisticLockException ole) {
                                OPDE.warn(ole);
                                if (em.getTransaction().isActive()) {
                                    em.getTransaction().rollback();
                                }
                                if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                                    OPDE.getMainframe().emptyFrame();
                                    OPDE.getMainframe().afterLogin();
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
                    });
                    popup.setMovable(false);
                    popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));

                    popup.setOwner(pnlMenu);
                    popup.removeExcludedComponent(pnlMenu);
                    popup.getContentPane().add(pnlPIT);
                    popup.setDefaultFocusComponent(pnlPIT);
                    GUITools.showPopup(popup, SwingConstants.WEST);
                } else {
                    final JidePopup popup = new JidePopup();
                    Pair<Date, Date> expansion = ResInfoService.getMinMaxExpansion(resInfo, mapType2ResInfos.get(resInfo.getResInfoType()));
                    PnlPeriod pnlPeriod = new PnlPeriod(expansion.getFirst(), expansion.getSecond(), resInfo.getFrom(), resInfo.getTo(), o -> {
                        popup.hidePopup();
                        if (o != null) {
                            EntityManager em = OPDE.createEM();
                            try {
                                em.getTransaction().begin();
                                ResInfo editinfo = em.merge(resInfo);
                                em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                                em.lock(editinfo, LockModeType.OPTIMISTIC);
                                Pair<Date, Date> period = (Pair<Date, Date>) o;
                                ResInfoService.setFrom(editinfo, period.getFirst());
                                ResInfoService.setTo(editinfo, period.getSecond());
                                editinfo.setUserOFF(editinfo.getTo().equals(SYSConst.DATE_UNTIL_FURTHER_NOTICE) ? null : em.merge(OPDE.getLogin().getUser()));
                                em.getTransaction().commit();

                                synchronized (mapType2ResInfos) {
                                    int oldIndex = mapType2ResInfos.get(resInfo.getResInfoType()).indexOf(resInfo);
                                    mapType2ResInfos.get(resInfo.getResInfoType()).remove(resInfo);
                                    mapType2ResInfos.get(editinfo.getResInfoType()).add(oldIndex, editinfo);
                                }
                                synchronized (listAllInfos) {
                                    listAllInfos.remove(resInfo);
                                    listAllInfos.add(editinfo);
                                }
                                synchronized (mapKey2CP) {
                                    mapKey2CP.remove(keyResInfo);
                                }
                                sortData();
                                reloadDisplay();
                            } catch (OptimisticLockException ole) {
                                OPDE.warn(ole);
                                if (em.getTransaction().isActive()) {
                                    em.getTransaction().rollback();
                                }
                                if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                                    OPDE.getMainframe().emptyFrame();
                                    OPDE.getMainframe().afterLogin();
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

                    });
                    popup.setMovable(false);
                    popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));

                    popup.setOwner(pnlMenu);
                    popup.removeExcludedComponent(pnlMenu);
                    popup.getContentPane().add(pnlPeriod);
                    popup.setDefaultFocusComponent(pnlPeriod);
                    GUITools.showPopup(popup, SwingConstants.WEST);
                }

            });
            btnChangePeriod.setEnabled((ResInfoService.isEditable(resInfo) || resInfo.getResInfoType().getType() == ResInfoTypeTools.TYPE_STAY)
                    && (OPDE.isAdmin() ||
                    (resInfo.getUserON().equals(OPDE.getLogin().getUser()) && new LocalDate(resInfo.getFrom()).equals(new LocalDate()))  // The same user only on the same day.
            ));
            pnlMenu.add(btnChangePeriod);

            pnlMenu.add(new JSeparator());
        }


        /***
         *      _     _       _____  _    ____
         *     | |__ | |_ _ _|_   _|/ \  / ___|___
         *     | '_ \| __| '_ \| | / _ \| |  _/ __|
         *     | |_) | |_| | | | |/ ___ \ |_| \__ \
         *     |_.__/ \__|_| |_|_/_/   \_\____|___/
         *
         */
        final JButton btnTAGs = GUITools.createHyperlinkButton("misc.msg.editTags", SYSConst.icon22tagPurple, null);
        btnTAGs.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnTAGs.addActionListener(actionEvent -> {
            final JidePopup popup = new JidePopup();

            final JPanel pnl = new JPanel(new BorderLayout(5, 5));
            final PnlCommonTags pnlCommonTags = new PnlCommonTags(resInfo.getCommontags(), true, 3);
            pnl.add(new JScrollPane(pnlCommonTags), BorderLayout.CENTER);
            JButton btnApply = new JButton(SYSConst.icon22apply);
            pnl.add(btnApply, BorderLayout.SOUTH);
            btnApply.addActionListener(ae -> {
                EntityManager em = OPDE.createEM();
                try {

                    em.getTransaction().begin();
                    em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                    ResInfo myInfo = em.merge(resInfo);
                    em.lock(myInfo, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                    myInfo.getCommontags().clear();
                    for (Commontags commontag : pnlCommonTags.getListSelectedTags()) {
                        myInfo.getCommontags().add(em.merge(commontag));
                    }

                    em.getTransaction().commit();

                    synchronized (mapType2ResInfos) {
                        // entweder der referenceType ist aktiv, dann steht er in der ersten Liste, sonst nehmen wir den aktuellen Ersatz.
                        ResInfoType referenceType = mapType2ResInfos.containsKey(resInfo.getResInfoType()) ? resInfo.getResInfoType() : mapEquiv2Type.get(resInfo.getResInfoType().getEquiv());
                        // refresh data
                        int oldIndex = mapType2ResInfos.get(referenceType).indexOf(resInfo);
                        mapType2ResInfos.get(referenceType).remove(resInfo);
                        mapType2ResInfos.get(referenceType).add(oldIndex, myInfo);
                    }
                    synchronized (listAllInfos) {
                        listAllInfos.remove(resInfo);
                        listAllInfos.add(myInfo);
                    }
                    synchronized (mapKey2CP) {
                        mapKey2CP.remove(keyResInfo);
                    }

                    sortData();
                    reloadDisplay();

                } catch (OptimisticLockException ole) {
                    OPDE.warn(ole);
                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                        OPDE.getMainframe().emptyFrame();
                        OPDE.getMainframe().afterLogin();
                    } else {
                        reloadDisplay();
                    }
                } catch (Exception e) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    OPDE.fatal(e);
                } finally {
                    em.close();
                }
            });

            popup.setMovable(false);
            popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));
            popup.setOwner(btnTAGs);
            popup.removeExcludedComponent(btnTAGs);
            pnl.setPreferredSize(new Dimension(350, 150));
            popup.getContentPane().add(pnl);
            popup.setDefaultFocusComponent(pnl);

            GUITools.showPopup(popup, SwingConstants.WEST);

        });
        btnTAGs.setEnabled(!resInfo.isClosed() && resInfo.getPrescription() == null);
        pnlMenu.add(btnTAGs);


        pnlMenu.add(new JSeparator());

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
            btnFiles.addActionListener(actionEvent -> {
                // If the closure is null, only attached files can be viewed but no new ones can be attached.
                Closure closure = o -> currentEditor = null;

                if (!resInfo.isClosed() && !resInfo.getResInfoType().isDeprecated()) {
                    closure = o -> {
                        EntityManager em = OPDE.createEM();
                        final ResInfo editinfo = em.find(ResInfo.class, resInfo.getID());
                        em.close();

                        synchronized (mapType2ResInfos) {
                            int oldIndex = mapType2ResInfos.get(resInfo.getResInfoType()).indexOf(resInfo);
                            mapType2ResInfos.get(resInfo.getResInfoType()).remove(resInfo);
                            mapType2ResInfos.get(editinfo.getResInfoType()).add(oldIndex, editinfo);
                        }
                        synchronized (listAllInfos) {
                            listAllInfos.remove(resInfo);
                            listAllInfos.add(editinfo);
                        }
                        synchronized (mapKey2CP) {
                            mapKey2CP.remove(keyResInfo);
                        }
                        currentEditor = null;
                        sortData();
                        reloadDisplay();
                    };
                }
                currentEditor = new DlgFiles(resInfo, closure);
                currentEditor.setVisible(true);
            });
            btnFiles.setEnabled(OPDE.isFTPworking() && resInfo.getPrescription() == null);
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
            btnProcess.addActionListener(actionEvent -> {
                currentEditor = new DlgProcessAssign(resInfo, o -> {
                    if (o == null) {
                        currentEditor = null;
                        return;
                    }
                    Pair<ArrayList<QProcess>, ArrayList<QProcess>> result = (Pair<ArrayList<QProcess>, ArrayList<QProcess>>) o;

                    ArrayList<QProcess> assigned = result.getFirst();
                    ArrayList<QProcess> unassigned = result.getSecond();

                    EntityManager em = OPDE.createEM();

                    try {
                        em.getTransaction().begin();

                        em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                        final ResInfo myInfo = em.merge(resInfo);
                        em.lock(myInfo, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                        ArrayList<SYSINF2PROCESS> attached = new ArrayList<SYSINF2PROCESS>(myInfo.getAttachedQProcessConnections());
                        for (SYSINF2PROCESS linkObject : attached) {
                            if (unassigned.contains(linkObject.getQProcess())) {
                                linkObject.getQProcess().getAttachedNReportConnections().remove(linkObject);
                                linkObject.getResInfo().getAttachedQProcessConnections().remove(linkObject);
                                em.merge(new PReport(SYSTools.xx(PReportTools.PREPORT_TEXT_REMOVE_ELEMENT) + ": " + myInfo.titleAsString() + " ID: " + myInfo.getID(), PReportTools.PREPORT_TYPE_REMOVE_ELEMENT, linkObject.getQProcess()));
                                em.remove(linkObject);
                            }
                        }
                        attached.clear();

                        for (QProcess qProcess : assigned) {
                            List<QElement> listElements = qProcess.getElements();
                            if (!listElements.contains(myInfo)) {
                                QProcess myQProcess = em.merge(qProcess);
                                SYSINF2PROCESS myLinkObject = em.merge(new SYSINF2PROCESS(myQProcess, myInfo));
                                em.merge(new PReport(SYSTools.xx(PReportTools.PREPORT_TEXT_ASSIGN_ELEMENT) + ": " + myInfo.titleAsString() + " ID: " + myInfo.getID(), PReportTools.PREPORT_TYPE_ASSIGN_ELEMENT, myQProcess));
                                qProcess.getAttachedResInfoConnections().add(myLinkObject);
                                myInfo.getAttachedQProcessConnections().add(myLinkObject);
                            }
                        }

                        em.getTransaction().commit();

                        synchronized (listAllInfos) {
                            listAllInfos.remove(resInfo);
                            listAllInfos.add(myInfo);
                        }
                        synchronized (mapKey2CP) {
                            mapKey2CP.remove(keyResInfo);
                        }
                        sortData();
                        reloadDisplay();

                    } catch (OptimisticLockException ole) {
                        OPDE.warn(ole);
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                            OPDE.getMainframe().emptyFrame();
                            OPDE.getMainframe().afterLogin();
                        }
                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                    } catch (RollbackException ole) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                            OPDE.getMainframe().emptyFrame();
                            OPDE.getMainframe().afterLogin();
                        }
                        OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                    } catch (Exception e) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        OPDE.fatal(e);
                    } finally {
                        em.close();
                        currentEditor = null;
                    }

                });
                currentEditor.setVisible(true);
            });
            btnProcess.setEnabled(ResInfoService.isEditable(resInfo) && OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID));

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

    private java.util.List<Component> addCommands() {

        java.util.List<Component> list = new ArrayList();

        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.MANAGER, internalClassID)) {
            /***
             * _  _ ____ _  _ ____ ____    ___  ____ _ _ _ ____ _  _ _  _ ____ ____
             * |\ | |___ |  | |___ |__/    |__] |___ | | | |  | |__| |\ | |___ |__/
             * | \| |___ |__| |___ |  \    |__] |___ |_|_| |__| |  | | \| |___ |  \
             *
             */
            JideButton addRes = GUITools.createHyperlinkButton(SYSTools.xx("nursingrecords.info.addbw"), SYSConst.icon22addbw, null);
            addRes.addMouseListener(GUITools.getHyperlinkStyleMouseAdapter());
            addRes.setAlignmentX(Component.LEFT_ALIGNMENT);
            addRes.addActionListener(actionEvent -> {
                final MyJDialog dlg = new MyJDialog(false);
                WizardDialog wizard = new AddBWWizard(o -> {
                    dlg.dispose();
                    // to refresh the resident list
                    OPDE.getMainframe().emptySearchArea();
                    jspSearch = OPDE.getMainframe().prepareSearchArea();
                    prepareSearchArea();
                }).getWizard();
                dlg.setContentPane(wizard.getContentPane());
                dlg.pack();
                dlg.setSize(new Dimension(800, 550));
                dlg.setVisible(true);
            });
            list.add(addRes);


            /**
             * _  _ _  _ ____ ___  ___  ____ _ ___    ____ _  _ ___  ____ ___
             * |_/  |  | |__/   /    /  |___ |  |     |___ |\ | |  \ |___  |
             * | \_ |__| |  \  /__  /__ |___ |  |     |___ | \| |__/ |___  |
             */
            JideButton kzp_endet = GUITools.createHyperlinkButton(SYSTools.xx("nursingrecords.info.kzpendet"), SYSConst.icon22addbw, null);
            kzp_endet.addMouseListener(GUITools.getHyperlinkStyleMouseAdapter());
            kzp_endet.setAlignmentX(Component.LEFT_ALIGNMENT);
            kzp_endet.addActionListener(actionEvent -> {
                if (!ResInfoService.isKZP(resident)) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("nursingrecords.info.msg.notkzp"));
                    return;
                }
                if (ResInfoService.isDead(resident)) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("nursingrecords.info.msg.isdeadnow"));
                    return;
                }
                if (ResInfoService.isGone(resident)) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("nursingrecords.info.resident.movedout"));
                    return;
                }

                new DlgPIT(SYSTools.xx("nursingrecords.info.dlg.endofkzp"), o -> {
                    if (o != null) {
                        Date dod = (Date) o;
                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();
                            em.lock(em.merge(resident), LockModeType.OPTIMISTIC);

                            /*
                            Wenn die KZP endet und der BW dann für immer bleibt, endet die bestehende HAUF (die mit KZP=TRUE) und eine neue HAUF wird nahtlos
                            angehangen. Dann mit KZP = FALSE UND STAY=""
                            Bei der alten HAUF wird STAY=ResInfoTypeTools.STAY_VALUE_NOW_PERMANENT gesetzt.
                             */

                            ResInfo bwinfo_kzp_hauf = em.merge(ResInfoService.getLastResinfo(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_STAY)));
                            ResInfo bwinfo_hauf_nach_kzp = em.merge(ResInfoService.createStayResInfo(resident, new Date(), false));

                            // Bei dem alten den STAY ändern und die Props zurückschreiben
                            Properties kzp_props = ResInfoService.getContent(bwinfo_kzp_hauf);
                            kzp_props.put(ResInfoTypeTools.STAY_KEY, ResInfoTypeTools.STAY_VALUE_NOW_PERMANENT);
                            ResInfoService.setContent(bwinfo_kzp_hauf, kzp_props);
                            // und Enden lassen (1 Sekunde VOR dem neuen)
                            bwinfo_kzp_hauf.setTo(new DateTime(bwinfo_hauf_nach_kzp.getFrom()).minusSeconds(1).toDate());
                            bwinfo_kzp_hauf.setUserOFF(OPDE.getLogin().getUser());

                            em.getTransaction().commit();

                            switchResident(resident);
                            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("nursingrecords.info.msg.isPermanentNow")));
                            OPDE.getMainframe().removeBesonderheitKZP(resident);
                        } catch (OptimisticLockException ole) {
                            OPDE.warn(ole);
                            if (em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }
                            if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                                OPDE.getMainframe().emptyFrame();
                                OPDE.getMainframe().afterLogin();
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
                });
            });
            list.add(kzp_endet);


            /**
             * ___  ____ _ _ _ ____ _  _ _  _ ____ ____    ___  _ ____ _  _ ___
             * |__] |___ | | | |  | |__| |\ | |___ |__/      /  | |___ |__|  |
             * |__] |___ |_|_| |__| |  | | \| |___ |  \     /__ | |___ |  |  |
             *
             * _ _ _ _ ____ ___  ____ ____    ____ _ _  _
             * | | | | |___ |  \ |___ |__/    |___ | |\ |
             * |_|_| | |___ |__/ |___ |  \    |___ | | \|
             */
            // @relates GitHub #10
            JideButton resComesback = GUITools.createHyperlinkButton(SYSTools.xx("opde.info.dlg.resident.returns"), SYSConst.icon22addbw, null);
            resComesback.addMouseListener(GUITools.getHyperlinkStyleMouseAdapter());
            resComesback.setAlignmentX(Component.LEFT_ALIGNMENT);
            resComesback.addActionListener(actionEvent -> {

                if (ResInfoService.isDead(resident)) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("nursingrecords.info.msg.isdeadnow"));
                    return;
                }
                if (!ResInfoService.isGone(resident)) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("nursingrecords.info.msg.stillhere"));
                    return;
                }
                ResInfo last_stay = ResInfoService.getLastResinfo(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_STAY));
                LocalDate departed = new LocalDate().minusDays(2);
                if (last_stay != null) {
                    departed = new LocalDate(last_stay.getTo());
                }

                if (!departed.isBefore(new LocalDate())) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("nursingrecords.info.msg.toorecent"));
                    return;
                }

                LocalDate minDate = SYSCalendar.max(new LocalDate().minusDays(2), departed).plusDays(1);
                minDate = SYSCalendar.min(new LocalDate(), minDate);


                currentEditor = new DlgResidentReturns(minDate.toDateTimeAtStartOfDay().toDate(), o -> {
                    if (o != null) {
                        Date stay = ((Triplet<Date, Station, Rooms>) o).getValue0();
                        Station station = ((Triplet<Date, Station, Rooms>) o).getValue1();
                        Rooms room = ((Triplet<Date, Station, Rooms>) o).getValue2();

                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();

                            Resident myResident = em.merge(resident);
                            myResident.setStation(station);
                            ResInfo resinfo_stay = em.merge(ResInfoService.createResInfo(ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_STAY), myResident));
                            ResInfoService.setFrom(resinfo_stay, stay);

                            if (room != null) {
                                ResInfo resinfo_room = em.merge(ResInfoService.createResInfo(ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_ROOM), myResident));
                                Properties props = new Properties();
                                props.put("room.id", Long.toString(room.getRoomID()));
                                props.put("room.text", room.toString());
                                ResInfoService.setContent(resinfo_room, props);
                                ResInfoService.setFrom(resinfo_room, stay);
                            }

                            em.getTransaction().commit();

                            resident = myResident;

                            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(ResidentTools.getTextCompact(resident) + " " + SYSTools.xx("misc.msg.entrysuccessful"), 6));

                        } catch (Exception e) {
                            if (em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }
                            OPDE.fatal(e);
                        } finally {
                            em.close();
                            SwingUtilities.invokeLater(() -> {
                                OPDE.getMainframe().emptySearchArea();
                                jspSearch = OPDE.getMainframe().prepareSearchArea();
                                prepareSearchArea();
                                GUITools.setResidentDisplay(resident);
                                reload();
                            });
                        }
                    }
                    currentEditor = null;
                });

                currentEditor.setVisible(true);
            });
            list.add(resComesback);

            JideButton editRes = GUITools.createHyperlinkButton(SYSTools.xx("nursingrecords.info.editbw"), SYSConst.icon22edit3, null);
            editRes.addMouseListener(GUITools.getHyperlinkStyleMouseAdapter());
            editRes.setAlignmentX(Component.LEFT_ALIGNMENT);
            editRes.addActionListener(actionEvent -> {
                if (!ResidentTools.isActive(resident)) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.cantChangeInactiveResident"));
                    return;
                }
                currentEditor = new DlgEditResidentBaseData(resident, o -> {
                    if (o != null) {
                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();
                            Resident myResident = em.merge((Resident) o);
                            em.lock(em.merge(myResident), LockModeType.OPTIMISTIC);
                            em.getTransaction().commit();
                            resident = myResident;
                            GUITools.setResidentDisplay(resident);
                            // to refresh the resident list
                            OPDE.getMainframe().emptySearchArea();
                            jspSearch = OPDE.getMainframe().prepareSearchArea();
                            prepareSearchArea();
                            pnlCare.setJspSearch(jspSearch);
                        } catch (OptimisticLockException ole) {
                            OPDE.warn(ole);
                            if (em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }
                            if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                                OPDE.getMainframe().emptyFrame();
                                OPDE.getMainframe().afterLogin();
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
                    currentEditor = null;
                });
                currentEditor.setVisible(true);
            });
            list.add(editRes);

            /***
             *      _       ____                 _
             *     (_)___  |  _ \  ___  __ _  __| |
             *     | / __| | | | |/ _ \/ _` |/ _` |
             *     | \__ \ | |_| |  __/ (_| | (_| |
             *     |_|___/ |____/ \___|\__,_|\__,_|
             *
             */
            btnResMovedOut = GUITools.createHyperlinkButton(SYSTools.xx("nursingrecords.info.resident.movedout"), SYSConst.icon22residentGone, null);
            btnResDied = GUITools.createHyperlinkButton(SYSTools.xx("nursingrecords.info.resident.died"), SYSConst.icon22residentDied, null);
            btnResDied.addActionListener(actionEvent -> new DlgPIT(SYSTools.xx("nursingrecords.info.dlg.dateofdeath"), o -> {
                if (o != null) {
                    Date dod = (Date) o;
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                        resident.setStation(null);
                        ResidentTools.endOfStay(em, em.merge(resident), dod, ResInfoTypeTools.STAY_VALUE_DEAD);
                        em.getTransaction().commit();
                        btnResDied.setEnabled(false);
                        btnResMovedOut.setEnabled(false);
                        btnResIsAway.setEnabled(false);
                        btnResIsBack.setEnabled(false);

                        OPDE.getMainframe().emptySearchArea();
                        jspSearch = OPDE.getMainframe().prepareSearchArea();
                        prepareSearchArea();
                        pnlCare.setJspSearch(jspSearch);
                    } catch (OptimisticLockException ole) {
                        OPDE.warn(ole);
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                            OPDE.getMainframe().emptyFrame();
                            OPDE.getMainframe().afterLogin();
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
            }));
            btnResDied.setEnabled(ResidentTools.isActive(resident));
            list.add(btnResDied);

            /***
             *                                   _               _
             *      _ __ ___   _____   _____  __| |   ___  _   _| |_
             *     | '_ ` _ \ / _ \ \ / / _ \/ _` |  / _ \| | | | __|
             *     | | | | | | (_) \ V /  __/ (_| | | (_) | |_| | |_
             *     |_| |_| |_|\___/ \_/ \___|\__,_|  \___/ \__,_|\__|
             *
             */
            btnResMovedOut.addActionListener(actionEvent -> new DlgPIT(SYSTools.xx("nursingrecords.info.dlg.dateofmoveout"), o -> {
                if (o != null) {
                    Date dod = (Date) o;
                    EntityManager em = OPDE.createEM();
                    try {
                        em.getTransaction().begin();
                        em.lock(em.merge(resident), LockModeType.OPTIMISTIC);
                        resident.setStation(null);

                        ResidentTools.endOfStay(em, em.merge(resident), dod, ResInfoTypeTools.STAY_VALUE_LEFT);
                        em.getTransaction().commit();

                        btnResDied.setEnabled(false);
                        btnResMovedOut.setEnabled(false);
                        btnResIsAway.setEnabled(false);
                        btnResIsBack.setEnabled(false);

                        OPDE.getMainframe().emptySearchArea();
                        jspSearch = OPDE.getMainframe().prepareSearchArea();
                        prepareSearchArea();
                        pnlCare.setJspSearch(jspSearch);
                    } catch (OptimisticLockException ole) {
                        OPDE.warn(ole);
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                            OPDE.getMainframe().emptyFrame();
                            OPDE.getMainframe().afterLogin();
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
            }));
            btnResMovedOut.setEnabled(ResidentTools.isActive(resident));
            list.add(btnResMovedOut);
        } // MANAGER


        if (OPDE.getAppInfo().isAllowedTo(InternalClassACL.UPDATE, internalClassID)) {
            btnResIsAway = GUITools.createHyperlinkButton(SYSTools.xx("nursingrecords.info.resident.isaway"), SYSConst.icon22residentAbsent, null);
            /***
             *      _          _
             *     (_)___     / \__      ____ _ _   _
             *     | / __|   / _ \ \ /\ / / _` | | | |
             *     | \__ \  / ___ \ V  V / (_| | |_| |
             *     |_|___/ /_/   \_\_/\_/ \__,_|\__, |
             *                                  |___/
             */
            btnResIsAway.addActionListener(actionEvent -> {
                final JidePopup popup = new JidePopup();
                PnlAway pnlAway = new PnlAway(ResInfoService.createResInfo(ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_ABSENCE), resident), o -> {
                    popup.hidePopup();
                    if (o != null) {
                        EntityManager em = OPDE.createEM();
                        try {
                            em.getTransaction().begin();
                            Resident myResident = em.merge(resident);
                            em.lock(myResident, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
                            ResInfo newAbsence = em.merge((ResInfo) o);
                            em.getTransaction().commit();

                            switchResident(myResident);
                            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("nursingrecords.info.msg.isawaynow")));
                            OPDE.getMainframe().addBesonderheit(newAbsence.getResInfoType(), resident);
                        } catch (OptimisticLockException ole) {
                            OPDE.warn(ole);
                            if (em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }
                            if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                                OPDE.getMainframe().emptyFrame();
                                OPDE.getMainframe().afterLogin();
                            }
                            OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                        } catch (RollbackException ole) {
                            if (em.getTransaction().isActive()) {
                                em.getTransaction().rollback();
                            }
                            if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                                OPDE.getMainframe().emptyFrame();
                                OPDE.getMainframe().afterLogin();
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
                });
                popup.setMovable(false);
                popup.getContentPane().setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.LINE_AXIS));

                popup.setOwner(btnResIsAway);
                popup.removeExcludedComponent(btnResIsAway);
                popup.getContentPane().add(pnlAway);
                popup.setDefaultFocusComponent(pnlAway);
                GUITools.showPopup(popup, SwingConstants.NORTH_EAST);
            });
            btnResIsAway.setEnabled(ResidentTools.isActive(resident) && !ResInfoService.isAway(resident));
            list.add(btnResIsAway);

            btnResIsBack = GUITools.createHyperlinkButton(SYSTools.xx("nursingrecords.info.resident.isback"), SYSConst.icon22residentBack, null);
            /***
             *      _       ____             _
             *     (_)___  | __ )  __ _  ___| | __
             *     | / __| |  _ \ / _` |/ __| |/ /
             *     | \__ \ | |_) | (_| | (__|   <
             *     |_|___/ |____/ \__,_|\___|_|\_\
             *
             */
            btnResIsBack.addActionListener(actionEvent -> {
                EntityManager em = OPDE.createEM();
                try {
                    em.getTransaction().begin();
                    Resident myResident = em.merge(resident);
                    em.lock(myResident, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

                    ResInfo lastabsence = em.merge(ResInfoService.getLastResinfo(resident, ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_ABSENCE)));
                    em.lock(lastabsence, LockModeType.OPTIMISTIC);
                    ResInfoService.setTo(lastabsence, new Date());
                    lastabsence.setUserOFF(em.merge(OPDE.getLogin().getUser()));
                    em.getTransaction().commit();

                    switchResident(myResident);
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage(SYSTools.xx("nursingrecords.info.msg.isbacknow")));
                    OPDE.getMainframe().removeBesonderheit(lastabsence.getResInfoType(), resident);
                } catch (OptimisticLockException ole) {
                    OPDE.warn(ole);
                    OPDE.getDisplayManager().addSubMessage(DisplayManager.getLockMessage());
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    reload();
                } catch (RollbackException ole) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    if (ole.getMessage().indexOf("Class> entity.info.Resident") > -1) {
                        OPDE.getMainframe().emptyFrame();
                        OPDE.getMainframe().afterLogin();
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
            });
            btnResIsBack.setEnabled(ResidentTools.isActive(resident) && ResInfoService.isAway(resident));
            list.add(btnResIsBack);


        } // UPDATE


        return list;
    }

    private java.util.List<Component> addDisplayCommands() {
        java.util.List<Component> list = new ArrayList<Component>();

        final JButton btnExpandAll = GUITools.createHyperlinkButton("misc.msg.expand.active", SYSConst.icon22expand, null);
        btnExpandAll.addActionListener(actionEvent -> {
            try {
                for (ResInfo info : listAllInfos) {
                    final String keyResInfo = info.getID() + ".resinfo";
                    final String keyResInfoType = info.getResInfoType().getID() + ".resinfotype";
                    final String keyResInfoCat = info.getResInfoType().getResInfoCat().getID() + ".resinfocat";
                    if (!info.isClosed()) {
                        if (mapKey2CP.containsKey(keyResInfo) && mapKey2CP.get(keyResInfo).isCollapsed()) {
                            mapKey2CP.get(keyResInfo).setCollapsed(false);
                        }
                        if (mapKey2CP.containsKey(keyResInfoType) && mapKey2CP.get(keyResInfoType).isCollapsed()) {
                            mapKey2CP.get(keyResInfoType).setCollapsed(false);
                        }
                        if (mapKey2CP.containsKey(keyResInfoCat) && mapKey2CP.get(keyResInfoCat).isCollapsed()) {
                            mapKey2CP.get(keyResInfoCat).setCollapsed(false);
                        }
                    }
                }
            } catch (PropertyVetoException e) {
                // bah!
            }
        });
        list.add(btnExpandAll);

        final JButton btnCollapseAll = GUITools.createHyperlinkButton("misc.msg.collapseall", SYSConst.icon22collapse, null);

        btnCollapseAll.addActionListener(actionEvent -> {
            try {
                GUITools.setCollapsed(cpsAll, true);
            } catch (PropertyVetoException e) {
                // bah!
            }
        });
        list.add(btnCollapseAll);

        return list;
    }

    private java.util.List<Component> addFilters() {
        java.util.List<Component> list = new ArrayList<Component>();

        listSearchResults = new JList();
        listSearchResults.setModel(new DefaultListModel());
//        listSearchResults.setVisibleRowCount(7);
        listSearchResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSearchResults.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;


            SwingUtilities.invokeLater(() -> {
                // collapse everything first
                try {
                    GUITools.setCollapsed(cpsAll, true);
                } catch (PropertyVetoException ev) {
                    // bah!
                }


                String key = "";
                if (listSearchResults.getSelectedValue() instanceof ResInfo) {
                    key = ((ResInfo) listSearchResults.getSelectedValue()).getID() + ".resinfo";
                }

                if (listSearchResults.getSelectedValue() instanceof ResInfoType) {
                    key = ((ResInfoType) listSearchResults.getSelectedValue()).getID() + ".resinfotype";
                }


                if (mapKey2CP.containsKey(key)) {
                    try {
                        GUITools.expand(mapKey2CP.get(key));
                    } catch (PropertyVetoException e1) {
                        //bah!!
                    }
                }
                GUITools.scroll2show(jspMain, mapKey2CP.get(key), cpsAll, null);
            });


        });

        listSearchResults.setCellRenderer((list1, value, index, isSelected, cellHasFocus) -> {
            String text = "";
            if (value instanceof ResInfo) {
                text = ((ResInfo) value).getResInfoType().getShortDescription() + ", " + DateFormat.getDateInstance(DateFormat.SHORT).format(((ResInfo) value).getFrom());
            } else if (value instanceof ResInfoType) {
                text = ((ResInfoType) value).getShortDescription();
            } else {
                text = value.toString();
            }

            return new DefaultListCellRenderer().getListCellRendererComponent(list1, text, index, isSelected, cellHasFocus);

        });

        txtSearch = new JXSearchField(SYSTools.xx("misc.msg.searchphrase"));
        txtSearch.setFont(SYSConst.ARIAL14);
        txtSearch.setInstantSearchDelay(750);
        txtSearch.addActionListener(e -> {
            if (SYSTools.catchNull(txtSearch.getText()).trim().length() > 3) {

                ArrayList<ResInfo> searchResults1 = searchInInfos(txtSearch.getText().trim());
                ArrayList<ResInfoType> searchResults2 = searchInTypes(txtSearch.getText().trim());
                if (searchResults1.isEmpty() && searchResults2.isEmpty()) {
                    OPDE.getDisplayManager().addSubMessage(new DisplayMessage("misc.msg.searchempty"));
                    listSearchResults.setModel(new DefaultListModel());
                } else {

                    DefaultListModel dlm = new DefaultListModel();

                    for (ResInfoType o : searchResults2) {
                        dlm.addElement(o);
                    }
                    for (ResInfo o : searchResults1) {
                        if (searchResults2.contains(o)) {
                            dlm.removeElement(o.getResInfoType());
                        }
                        dlm.addElement(o);
                    }

                    listSearchResults.setModel(dlm);
                }
            }
        });

        list.add(txtSearch);
        JPanel pnlList = new JPanel();
        pnlList.setLayout(new BoxLayout(pnlList, BoxLayout.PAGE_AXIS));
        pnlList.add(new JScrollPane(listSearchResults));
        list.add(pnlList);

        return list;
    }

    private boolean containsOnlyClosedInfos(final ResInfoType type) {
        boolean containsOnlyClosedInfos = true;
        if (mapType2ResInfos.containsKey(type)) {
            for (ResInfo info : mapType2ResInfos.get(type)) {
                containsOnlyClosedInfos = info.isClosed();
                if (!containsOnlyClosedInfos) {
                    break;
                }
            }
        }
        return containsOnlyClosedInfos;
    }

    private ArrayList<ResInfo> searchInInfos(String pattern) {
        pattern = pattern.toLowerCase();
        ArrayList<ResInfo> hits = new ArrayList<ResInfo>();
        for (ResInfo info : listAllInfos) {
            boolean hit = false;
            if (!hit) hit |= SYSTools.catchNull(info.getText()).toLowerCase().indexOf(pattern) >= 0;
//            if (!hit) hit |= SYSTools.catchNull(info.getHtml()).toLowerCase().indexOf(pattern) >= 0;
            if (!hit) hit |= SYSTools.catchNull(info.getProperties()).toLowerCase().indexOf(pattern) >= 0;
            if (hit) hits.add(info);
        }
        return hits;
    }

    private ArrayList<ResInfoType> searchInTypes(String pattern) {
        pattern = pattern.toLowerCase();

        ArrayList<ResInfoType> hits = new ArrayList<ResInfoType>();
        for (ResInfoType type : listAllTypes) {
            boolean hit = false;
            if (!hit)
                hit |= SYSTools.catchNull(type.getShortDescription()).toLowerCase().indexOf(pattern) >= 0;
            if (!hit)
                hit |= SYSTools.catchNull(type.getResInfoCat().getText()).toLowerCase().indexOf(pattern) >= 0;
            if (!hit)
                hit |= SYSTools.catchNull(type.getLongDescription()).toLowerCase().indexOf(pattern) >= 0;
            if (!hit)
                hit |= SYSTools.catchNull(type.getXml()).toLowerCase().indexOf(pattern) >= 0;
            if (hit) hits.add(type);
        }

        return hits;
    }
}