/*
 * Created by JFormDesigner on Thu Feb 02 12:08:28 CET 2012
 */

package op.care;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.pane.CollapsiblePanes;
import entity.info.Resident;
import entity.files.SYSFilesTools;
import op.OPDE;
import op.bw.tg.PnlTG;
import op.care.reports.PnlReport;
import op.care.bhp.PnlBHP;
import op.care.dfn.PnlDFN;
import op.care.info.PnlInfo;
import op.care.med.vorrat.PnlVorrat;
import op.care.planung.PnlPlanung;
import op.care.sysfiles.PnlFiles;
import op.care.verordnung.PnlVerordnung;
import op.care.vital.PnlVitalwerte;
import op.tools.CleanablePanel;
import op.tools.InternalClassACL;
import op.tools.NursingRecordsPanel;
import op.tools.SYSTools;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Torsten Löhr
 */
public class PnlPflege extends NursingRecordsPanel {

    public static final String internalClassID = "nursingrecords.main";
    public static final int TAB_UEBERSICHT = 0;
    public static final int TAB_PB = 1;
    public static final int TAB_DFN = 2;
    public static final int TAB_BHP = 3;
    public static final int TAB_VITAL = 4;
    public static final int TAB_VERORDNUNG = 5;
    public static final int TAB_VORRAT = 6;
    public static final int TAB_INFO = 7;
    public static final int TAB_PPLANUNG = 8;
    public static final int TAB_VORGANG = 9;
    public static final int TAB_FILES = 10;
    public static final int TAB_CASH = 11;

    private boolean initPhase;
    private String[] tabs = new String[]{
            OPDE.lang.getString(internalClassID + ".tab1"),
            OPDE.lang.getString(internalClassID + ".tab2"),
            OPDE.lang.getString(internalClassID + ".tab3"),
            OPDE.lang.getString(internalClassID + ".tab4"),
            OPDE.lang.getString(internalClassID + ".tab5"),
            OPDE.lang.getString(internalClassID + ".tab6"),
            OPDE.lang.getString(internalClassID + ".tab7"),
            OPDE.lang.getString(internalClassID + ".tab8"),
            OPDE.lang.getString(internalClassID + ".tab9"),
            OPDE.lang.getString(internalClassID + ".tab10"),
            OPDE.lang.getString(internalClassID + ".tab11"),
            OPDE.lang.getString(internalClassID + ".tab12")
    };
    private Resident currentBewohner = null;
    private CollapsiblePanes searchPanes;
    private JScrollPane jspSearch;
    private NursingRecordsPanel previousPanel;

    public PnlPflege(Resident bewohner, JScrollPane jspSearch) {
        initPhase = true;
        initComponents();
        this.jspSearch = jspSearch;
        currentBewohner = bewohner;
        initPanel();
        initPhase = false;
        jtpPflegeakteStateChanged(null);
    }


    @Override
    public void cleanup() {
        for (int i = 0; i < jtpPflegeakte.getTabCount(); i++) {
            if (jtpPflegeakte.getComponentAt(i) != null && jtpPflegeakte.getComponentAt(i) instanceof CleanablePanel) {
                CleanablePanel cp = (CleanablePanel) jtpPflegeakte.getComponentAt(i);
                cp.cleanup();
                SYSTools.unregisterListeners((JComponent) jtpPflegeakte.getComponentAt(i));
            }
            jtpPflegeakte.setComponentAt(i, null);
        }

        jtpPflegeakte.setEnabledAt(TAB_FILES, SYSFilesTools.isFTPServerReady());
        jtpPflegeakte.setEnabledAt(TAB_VORRAT, OPDE.isCalcMedi());

    }

    @Override
    public void switchResident(Resident bewohner) {
        currentBewohner = bewohner;
        ((NursingRecordsPanel) jtpPflegeakte.getSelectedComponent()).switchResident(bewohner);
    }

    @Override
    public void reload() {
        if (previousPanel != null) {
            previousPanel.reload();
        }
    }


    private void jtpPflegeakteStateChanged(ChangeEvent e) {
        if (initPhase) {
            return;
        }

        if (previousPanel != null) {
            previousPanel.cleanup();
        }

        switch (jtpPflegeakte.getSelectedIndex()) {
            case TAB_UEBERSICHT: {
                previousPanel = new PnlBWUebersicht(currentBewohner, jspSearch);
                jtpPflegeakte.setComponentAt(TAB_UEBERSICHT, previousPanel);
                jtpPflegeakte.setTitleAt(TAB_UEBERSICHT, "Übersicht");
                break;
            }
            case TAB_PB: {
                previousPanel = new PnlReport(currentBewohner, jspSearch);
                jtpPflegeakte.setComponentAt(TAB_PB, previousPanel);

                break;
            }
            case TAB_DFN: {
                previousPanel = new PnlDFN(currentBewohner, jspSearch);
                jtpPflegeakte.setComponentAt(TAB_DFN, previousPanel);

                break;
            }
            case TAB_VITAL: {
                previousPanel = new PnlVitalwerte(currentBewohner, jspSearch);
                jtpPflegeakte.setComponentAt(TAB_VITAL, previousPanel);

                break;
            }
            case TAB_INFO: {
                previousPanel = new PnlInfo(currentBewohner, jspSearch);
                jtpPflegeakte.setComponentAt(TAB_INFO, previousPanel);

                break;
            }
            case TAB_BHP: {
                previousPanel = new PnlBHP(currentBewohner, jspSearch);
                jtpPflegeakte.setComponentAt(TAB_BHP, previousPanel);

                break;
            }
            case TAB_PPLANUNG: {
                previousPanel = new PnlPlanung(currentBewohner, jspSearch);
                jtpPflegeakte.setComponentAt(TAB_PPLANUNG, previousPanel);

                break;
            }
            case TAB_VERORDNUNG: {
                previousPanel = new PnlVerordnung(currentBewohner, jspSearch);
                jtpPflegeakte.setComponentAt(TAB_VERORDNUNG, previousPanel);

                break;
            }
            case TAB_VORRAT: {
                previousPanel = new PnlVorrat(currentBewohner, jspSearch);
                jtpPflegeakte.setComponentAt(TAB_VORRAT, previousPanel);

                break;
            }
            case TAB_VORGANG: {
//                previousPanel =   new PnlProcess(parent, bewohner)
//                final PnlProcess pnlVorgang = new PnlProcess(parent, bewohner);
//                NursingRecordsPanel cp = new CleanablePanel() {
//
//                    @Override
//                    public void cleanup() {
//                        pnlVorgang.cleanup();
//                    }
//
//                    @Override
//                    public void switchResident(Bewohner bewohner) {
//                        BewohnerTools.setBWLabel(bwlabel, bewohner);
//                        pnlVorgang.switchResident(bewohner);
//                        validate();
//                    }
//                };
//                bwlabel = BewohnerTools.getBWLabel(bewohner);
////                            bwlabel.setAlignmentX(Component.LEFT_ALIGNMENT);
//                BoxLayout boxLayout = new BoxLayout(cp, BoxLayout.PAGE_AXIS);
//                cp.setLayout(boxLayout);
//                cp.add(bwlabel);
//                cp.add(pnlVorgang);
//
//                jtpPflegeakte.setComponentAt(TAB_VORGANG, cp);
//                jtpPflegeakte.setTitleAt(TAB_VORGANG, "Vorgänge");
                break;
            }
            case TAB_FILES: {
                previousPanel = new PnlFiles(currentBewohner, jspSearch);
                jtpPflegeakte.setComponentAt(TAB_FILES, previousPanel);

                break;
            }
            case TAB_CASH: {
//                previousPanel = new PnlFiles(currentBewohner, jspSearch);
//                jtpPflegeakte.setComponentAt(TAB_FILES, previousPanel);

                break;
            }
            default: {
            }
        }
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        jtpPflegeakte = new JTabbedPane();

        //======== this ========
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        //======== panel1 ========
        {
            panel1.setLayout(new FormLayout(
                    "default:grow",
                    "default:grow"));

            //======== jtpPflegeakte ========
            {
                jtpPflegeakte.setTabPlacement(SwingConstants.BOTTOM);
                jtpPflegeakte.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        jtpPflegeakteStateChanged(e);
                    }
                });
            }
            panel1.add(jtpPflegeakte, CC.xy(1, 1, CC.FILL, CC.FILL));
        }
        add(panel1);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private void initPanel() {
        for (int i = 0; i < tabs.length; i++) {
            jtpPflegeakte.add(tabs[i], new JPanel());
        }
//        jtpPflegeakte.setEnabledAt(TAB_PB, OPDE.getAppInfo().userHasAccessLevelForThisClass(PnlReport.internalClassID, InternalClassACL.EXECUTE));
//        jtpPflegeakte.setEnabledAt(TAB_FILES, OPDE.getAppInfo().userHasAccessLevelForThisClass(PnlFiles.internalClassID, InternalClassACL.EXECUTE));
        jtpPflegeakte.setEnabledAt(TAB_CASH, OPDE.getAppInfo().userHasAccessLevelForThisClass(PnlTG.internalClassID, InternalClassACL.EXECUTE));
    }

//    private void print(String html) {
//        try {
//            // Create temp file.
//            File temp = File.createTempFile("ueberleitung", ".html");
//
//            // Delete temp file when program exits.
//            temp.deleteOnExit();
//
//            // Write to temp file
//            BufferedWriter out = new BufferedWriter(new FileWriter(temp));
//            out.write(SYSTools.htmlUmlautConversion(html));
//
//            out.close();
//            //DlgFilesAssign.handleFile(this, temp.getAbsolutePath(), Desktop.Action.OPEN);
//        } catch (IOException e) {
//        }
//    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JTabbedPane jtpPflegeakte;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
