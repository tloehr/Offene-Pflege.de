/*
 * Created by JFormDesigner on Thu Feb 02 12:08:28 CET 2012
 */

package op.care;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.pane.CollapsiblePanes;
import entity.Bewohner;
import op.OPDE;
import op.care.berichte.PnlBerichte;
import op.care.bhp.PnlBHP;
import op.care.dfn.PnlDFN;
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Torsten Löhr
 */
public class PnlPflege extends NursingRecordsPanel {

    public static final String internalClassID = "nursingrecords.main";
    public static final int TAB_UEBERSICHT = 0;
    public static final int TAB_PB = 1;
    public static final int TAB_DFN = 2;
    public static final int TAB_SOZIAL = 3;
    public static final int TAB_BHP = 3;
    public static final int TAB_VITAL = 4;
    public static final int TAB_VERORDNUNG = 5;
    public static final int TAB_INFO = 6;
    public static final int TAB_PPLANUNG = 7;
    public static final int TAB_VORGANG = 8;
    public static final int TAB_FILES = 9;

    private boolean initPhase;
    private Bewohner currentBewohner = null;
    private CollapsiblePanes searchPanes;
    private JScrollPane jspSearch;


    public PnlPflege(Bewohner bewohner, JScrollPane jspSearch) {
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
//        if (jtpMain.getComponentAt(MAINTAB_UEBERSICHT) != null && jtpMain.getComponentAt(MAINTAB_UEBERSICHT) instanceof CleanablePanel) {
//            CleanablePanel cp = (CleanablePanel) jtpMain.getComponentAt(MAINTAB_UEBERSICHT);
//            cp.cleanup();
//            SYSTools.unregisterListeners((JComponent) jtpMain.getComponentAt(MAINTAB_UEBERSICHT));
//            jtpMain.setComponentAt(MAINTAB_UEBERSICHT, null);
//        }
//
//

    }

    @Override
    public void change2Bewohner(Bewohner bewohner) {
        currentBewohner = bewohner;
        ((NursingRecordsPanel) jtpPflegeakte.getSelectedComponent()).change2Bewohner(bewohner);
    }

    private void jtpPflegeakteStateChanged(ChangeEvent e) {
        if (initPhase) {
            return;
        }

        switch (jtpPflegeakte.getSelectedIndex()) {
            case TAB_UEBERSICHT: {
                jtpPflegeakte.setComponentAt(TAB_UEBERSICHT, new PnlBWUebersicht(currentBewohner, jspSearch));
                jtpPflegeakte.setTitleAt(TAB_UEBERSICHT, "Übersicht");
                break;
            }
            case TAB_PB: {
                jtpPflegeakte.setComponentAt(TAB_PB, new PnlBerichte(currentBewohner, jspSearch));
                jtpPflegeakte.setTitleAt(TAB_PB, "Pflegeberichte");
                break;
            }
            case TAB_DFN: {
                jtpPflegeakte.setComponentAt(TAB_DFN, new PnlDFN(new JFrame(), currentBewohner));
                jtpPflegeakte.setTitleAt(TAB_DFN, "DFN");
                break;
            }
            case TAB_VITAL: {
                jtpPflegeakte.setComponentAt(TAB_VITAL, new PnlVitalwerte(new JFrame(), currentBewohner));
                jtpPflegeakte.setTitleAt(TAB_VITAL, "Werte");
                break;
            }
            case TAB_INFO: {
                jtpPflegeakte.setComponentAt(TAB_INFO, new op.care.bwinfo.PnlInfo(new JFrame(), currentBewohner));
                jtpPflegeakte.setTitleAt(TAB_INFO, "Informationen");
                break;
            }
            case TAB_BHP: {
                jtpPflegeakte.setComponentAt(TAB_BHP, new PnlBHP(new JFrame(), currentBewohner));
                jtpPflegeakte.setTitleAt(TAB_BHP, "BHP");
                break;
            }
            case TAB_PPLANUNG: {
                jtpPflegeakte.setComponentAt(TAB_PPLANUNG, new PnlPlanung(new JFrame(), currentBewohner));
                jtpPflegeakte.setTitleAt(TAB_PPLANUNG, "Planung");
                break;
            }
            case TAB_VERORDNUNG: {
                jtpPflegeakte.setComponentAt(TAB_VERORDNUNG, new PnlVerordnung(currentBewohner, jspSearch));
                jtpPflegeakte.setTitleAt(TAB_VERORDNUNG, "Verordnungen");
                break;
            }
            case TAB_VORGANG: {
//                final PnlVorgang pnlVorgang = new PnlVorgang(parent, bewohner);
//                NursingRecordsPanel cp = new CleanablePanel() {
//
//                    @Override
//                    public void cleanup() {
//                        pnlVorgang.cleanup();
//                    }
//
//                    @Override
//                    public void change2Bewohner(Bewohner bewohner) {
//                        BewohnerTools.setBWLabel(bwlabel, bewohner);
//                        pnlVorgang.change2Bewohner(bewohner);
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
                jtpPflegeakte.setComponentAt(TAB_FILES, new PnlFiles(new JFrame(), currentBewohner));
                jtpPflegeakte.setTitleAt(TAB_FILES, "Dokumente");
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
        pnlUeber = new JPanel();
        pnlTB = new JPanel();
        pnlDFN = new JPanel();
        pnlBHP = new JPanel();
        pnlVitalDummy = new JPanel();
        pnlVer = new JPanel();
        pnlInfo = new JPanel();
        pnlPPlanung = new JPanel();
        pnlVorgang = new JPanel();
        pnlFiles = new JPanel();

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

                //======== pnlUeber ========
                {
                    pnlUeber.setLayout(new BoxLayout(pnlUeber, BoxLayout.X_AXIS));
                }
                jtpPflegeakte.addTab("\u00dcbersicht", pnlUeber);


                //======== pnlTB ========
                {
                    pnlTB.setLayout(new BoxLayout(pnlTB, BoxLayout.X_AXIS));
                }
                jtpPflegeakte.addTab("Pflegeberichte", pnlTB);


                //======== pnlDFN ========
                {
                    pnlDFN.setLayout(new BoxLayout(pnlDFN, BoxLayout.X_AXIS));
                }
                jtpPflegeakte.addTab("DFN", pnlDFN);


                //======== pnlBHP ========
                {
                    pnlBHP.setLayout(new BoxLayout(pnlBHP, BoxLayout.X_AXIS));
                }
                jtpPflegeakte.addTab("BHP", pnlBHP);


                //======== pnlVitalDummy ========
                {
                    pnlVitalDummy.setLayout(new BoxLayout(pnlVitalDummy, BoxLayout.X_AXIS));
                }
                jtpPflegeakte.addTab("Werte", pnlVitalDummy);


                //======== pnlVer ========
                {
                    pnlVer.setLayout(new BoxLayout(pnlVer, BoxLayout.X_AXIS));
                }
                jtpPflegeakte.addTab("Verordnungen", pnlVer);


                //======== pnlInfo ========
                {
                    pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.X_AXIS));
                }
                jtpPflegeakte.addTab("Informationen", pnlInfo);


                //======== pnlPPlanung ========
                {
                    pnlPPlanung.setLayout(new BoxLayout(pnlPPlanung, BoxLayout.X_AXIS));
                }
                jtpPflegeakte.addTab("Planung", pnlPPlanung);


                //======== pnlVorgang ========
                {
                    pnlVorgang.setLayout(new BoxLayout(pnlVorgang, BoxLayout.X_AXIS));
                }
                jtpPflegeakte.addTab("Vorg\u00e4nge", pnlVorgang);


                //======== pnlFiles ========
                {
                    pnlFiles.setLayout(new BoxLayout(pnlFiles, BoxLayout.X_AXIS));
                }
                jtpPflegeakte.addTab("Dokumente", pnlFiles);

            }
            panel1.add(jtpPflegeakte, CC.xy(1, 1, CC.FILL, CC.FILL));
        }
        add(panel1);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private void initPanel() {
        jtpPflegeakte.setEnabledAt(TAB_PB, OPDE.getAppInfo().userHasAccessLevelForThisClass(PnlBerichte.internalClassID, InternalClassACL.EXECUTE));
        jtpPflegeakte.setEnabledAt(TAB_FILES, OPDE.getAppInfo().userHasAccessLevelForThisClass(PnlFiles.internalClassID, InternalClassACL.EXECUTE));
    }

    private void print(String html) {
        try {
            // Create temp file.
            File temp = File.createTempFile("ueberleitung", ".html");

            // Delete temp file when program exits.
            temp.deleteOnExit();

            // Write to temp file
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));
            out.write(SYSTools.htmlUmlautConversion(html));

            out.close();
            //DlgFilesAssign.handleFile(this, temp.getAbsolutePath(), Desktop.Action.OPEN);
        } catch (IOException e) {
        }
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JTabbedPane jtpPflegeakte;
    private JPanel pnlUeber;
    private JPanel pnlTB;
    private JPanel pnlDFN;
    private JPanel pnlBHP;
    private JPanel pnlVitalDummy;
    private JPanel pnlVer;
    private JPanel pnlInfo;
    private JPanel pnlPPlanung;
    private JPanel pnlVorgang;
    private JPanel pnlFiles;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
