package op.residents.bwassistant;

import com.jidesoft.dialog.*;
import com.jidesoft.wizard.*;
import entity.Station;
import entity.info.*;
import entity.prescription.Doc;
import entity.prescription.DocTools;
import entity.system.Unique;
import entity.system.UniqueTools;
import entity.system.Users;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.Pair;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 09.07.12
 * Time: 10:05
 * To change this template use File | Settings | File Templates.
 */
public class AddBWWizard {

    public static final String internalClassID = "opde.admin.bw.wizard";

    private WizardDialog wizard;
    private Resident bewohner;
    private Closure finishAction;
    private ResInfo bwinfo_hauf;

    public AddBWWizard(Closure finishAction) {
        this.finishAction = finishAction;

        createWizard();
    }

    private void createWizard() {
        wizard = new WizardDialog(new JFrame(), false);
        PageList model = new PageList();

        AbstractWizardPage page1 = new WelcomePage(OPDE.lang.getString(internalClassID + ".page1.title"), OPDE.lang.getString(internalClassID + ".page1.description"));
        AbstractWizardPage page2 = new BasisInfoPage(OPDE.lang.getString(PnlBWBasisInfo.internalClassID + ".title"), OPDE.lang.getString(PnlBWBasisInfo.internalClassID + ".description"));
        AbstractWizardPage page3 = new BVPage(OPDE.lang.getString(PnlBV.internalClassID + ".title"), OPDE.lang.getString(PnlBV.internalClassID + ".description"));
        AbstractWizardPage page4 = new HausarztPage(OPDE.lang.getString(PnlGP.internalClassID + ".title"), OPDE.lang.getString(PnlGP.internalClassID + ".description"));
        AbstractWizardPage page5 = new BetreuerPage(OPDE.lang.getString(PnlLC.internalClassID + ".title"), OPDE.lang.getString(PnlLC.internalClassID + ".description"));
        AbstractWizardPage page6 = new HaufPage(OPDE.lang.getString(PnlHAUF.internalClassID + ".title"), OPDE.lang.getString(PnlHAUF.internalClassID + ".description"));
        AbstractWizardPage page7 = new CompletionPage(OPDE.lang.getString(internalClassID + ".page7.title"), OPDE.lang.getString(internalClassID + ".page7.description"));

        model.append(page1);
        model.append(page2);
        model.append(page3);
        model.append(page4);
        model.append(page5);
        model.append(page6);
        model.append(page7);

        wizard.setPageList(model);

        wizard.setFinishAction(new AbstractAction("Finish") {
            public void actionPerformed(ActionEvent e) {
                if (wizard.closeCurrentPage(wizard.getButtonPanel().getButtonByName(ButtonNames.FINISH))) {
                    save();
                }
            }
        });

        wizard.setCancelAction(new AbstractAction("Cancel") {
            public void actionPerformed(ActionEvent e) {
                if (wizard.closeCurrentPage(wizard.getButtonPanel().getButtonByName(ButtonNames.FINISH))) {
                    finishAction.execute(null);
                }
            }
        });

        wizard.pack();
    }

//    private Image getLeftGraphic(String iconname) {
//        JLabel lbl = new JLabel(new ImageIcon(getClass().getResource(iconname)));
//        lbl.setSize(lbl.getPreferredSize());
//        lbl.doLayout();
//        GraphicsConfiguration gfxConfig =
//                GraphicsEnvironment.getLocalGraphicsEnvironment()
//                        .getDefaultScreenDevice()
//                        .getDefaultConfiguration();
//        BufferedImage image =
//                gfxConfig.createCompatibleImage(lbl.getWidth(), lbl.getHeight());
//        lbl.paint(image.getGraphics());
//        return image;
//    }

    public WizardDialog getWizard() {
        return wizard;
    }

    private void save() {
        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();


            String prefix = bewohner.getName().substring(0, 1) + bewohner.getFirstname().substring(0, 1);
            prefix = prefix.toUpperCase();

            Unique unique = UniqueTools.getNewUID(em, prefix);
            String bwkennung = prefix + unique.getUid();
            bewohner.setRID(bwkennung);

            bewohner = em.merge(bewohner);
            bwinfo_hauf = em.merge(bwinfo_hauf);
            em.getTransaction().commit();
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(ResidentTools.getTextCompact(bewohner) + " " + OPDE.lang.getString("misc.msg.entrysuccessful"), 6));
            finishAction.execute(bewohner);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            OPDE.fatal(e);
        } finally {
            em.close();
        }
    }

    private class WelcomePage extends WelcomeWizardPage {

        public WelcomePage(String title, String description) {
            super(title, description);
//            setLeftPaneItems(LEFTPANE_GRAPHIC);
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();

            JTextPane txt = new JTextPane();
            txt.setEditable(false);
            txt.setContentType("text/html");
            txt.setOpaque(false);
            txt.setText("<html><font face=\"" + SYSConst.ARIAL14.getFamily() + "\">" +
                    OPDE.lang.getString(internalClassID + ".page1.welcome") +
                    "</font></html>");

            addComponent(txt, true);
            addSpace();
            addText(OPDE.lang.getString("opde.wizards.buttontext.letsgo"), SYSConst.ARIAL14);
        }

//        @Override
//        public Image getGraphic() {
//            return getLeftGraphic("/artwork/aspecton1.png");
//        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.CHANGE_BUTTON_TEXT, ButtonNames.BACK, OPDE.lang.getString("opde.wizards.buttontext.back"));
            fireButtonEvent(ButtonEvent.CHANGE_BUTTON_TEXT, ButtonNames.NEXT, OPDE.lang.getString("opde.wizards.buttontext.next"));
            fireButtonEvent(ButtonEvent.CHANGE_BUTTON_TEXT, ButtonNames.FINISH, OPDE.lang.getString("opde.wizards.buttontext.finish"));
            fireButtonEvent(ButtonEvent.CHANGE_BUTTON_TEXT, ButtonNames.CANCEL, OPDE.lang.getString("opde.wizards.buttontext.cancel"));

            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.CANCEL);
        }
    }

    private class BasisInfoPage extends DefaultWizardPage {
//        boolean alreadyexecute = false;

        public BasisInfoPage(String title, String description) {
            super(title, description);
            addPageListener(new PageListener() {
                @Override
                public void pageEventFired(PageEvent pageEvent) {
                    if (pageEvent.getID() != PageEvent.PAGE_OPENED) return;
                    setupWizardButtons();
                }
            });
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();

            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(bewohner == null ? ButtonEvent.DISABLE_BUTTON : ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.CANCEL);
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();
            addComponent(new PnlBWBasisInfo(new Closure() {
                @Override
                public void execute(Object o) {
                    bewohner = (Resident) o;
                    setupWizardButtons();
                }
            }), true);
        }


    }

    private class BVPage extends DefaultWizardPage {
//         boolean alreadyexecute = false;

        public BVPage(String title, String description) {
            super(title, description);
            addPageListener(new PageListener() {
                @Override
                public void pageEventFired(PageEvent pageEvent) {
                    if (pageEvent.getID() != PageEvent.PAGE_OPENED) return;
                    setupWizardButtons();
//                    alreadyexecute = true;
                }
            });
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();

            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(bewohner == null || bewohner.getPN1() == null ? ButtonEvent.DISABLE_BUTTON : ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.CANCEL);
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();
            addComponent(new PnlBV(new Closure() {
                @Override
                public void execute(Object o) {
                    bewohner.setPN1((Users) o);
                    setupWizardButtons();
                }
            }), true);
        }
    }

    private class HausarztPage extends DefaultWizardPage {
        private PnlGP pnlGP;
//        private boolean alreadyexecute = false;

        public HausarztPage(String title, String description) {
            super(title, description);
            pnlGP = new PnlGP(new Closure() {
                @Override
                public void execute(Object o) {
                    if (o != null) {
                        bewohner.setGP((Doc) o);
                    }
                    setupWizardButtons();
                }
            });
            addPageListener(new PageListener() {
                @Override
                public void pageEventFired(PageEvent pageEvent) {
                    if (pageEvent.getID() != PageEvent.PAGE_OPENED) return;
                    setupWizardButtons();
                }
            });
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();

            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(bewohner == null || bewohner.getGP() == null ? ButtonEvent.DISABLE_BUTTON : ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.CANCEL);
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();
            addComponent(pnlGP, true);
        }
    }


    private class BetreuerPage extends DefaultWizardPage {
        private PnlLC pnlLC;
//        private boolean alreadyexecute = false;

        public BetreuerPage(String title, String description) {
            super(title, description);
            pnlLC = new PnlLC(new Closure() {
                @Override
                public void execute(Object o) {
                    bewohner.setLCustodian1((LCustodian) o);
                    setupWizardButtons();
                }
            });
            addPageListener(new PageListener() {
                @Override
                public void pageEventFired(PageEvent pageEvent) {
                    if (pageEvent.getID() != PageEvent.PAGE_OPENED) return;
                    setupWizardButtons();
                }
            });
            setupWizardButtons();
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();

            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(bewohner == null || bewohner.getLCustodian1() == null ? ButtonEvent.DISABLE_BUTTON : ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.CANCEL);
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();
            addComponent(pnlLC, true);
        }
    }


    private class HaufPage extends DefaultWizardPage {

        public HaufPage(String title, String description) {
            super(title, description);
            addPageListener(new PageListener() {
                @Override
                public void pageEventFired(PageEvent pageEvent) {
                    if (pageEvent.getID() != PageEvent.PAGE_OPENED) return;
                    setupWizardButtons();
                }
            });
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();

            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(bewohner == null || bwinfo_hauf == null || bewohner.getStation() == null ? ButtonEvent.DISABLE_BUTTON : ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.CANCEL);
        }

//        @Override
//        public Image getGraphic() {
//            return getLeftGraphic("/artwork/aspecton1.png");
//        }

        @Override
        protected void initContentPane() {
            super.initContentPane();
            addComponent(new PnlHAUF(new Closure() {
                @Override
                public void execute(Object o) {
                    Date hauf = ((Pair<Date, Station>) o).getFirst();
                    Station station = ((Pair<Date, Station>) o).getSecond();
                    if (hauf != null) {
                        bwinfo_hauf = new ResInfo(ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_STAY), bewohner);
                        bwinfo_hauf.setFrom(hauf);
                    } else {
                        bwinfo_hauf = null;
                    }
                    bewohner.setStation(station);
                    setupWizardButtons();
                }
            }), true);
        }
    }


    private class CompletionPage extends CompletionWizardPage {
        public CompletionPage(String title, String description) {
            super(title, description);
            addPageListener(new PageListener() {
                @Override
                public void pageEventFired(PageEvent pageEvent) {
                    if (pageEvent.getID() != PageEvent.PAGE_OPENED) return;
                    setupWizardButtons();
                }
            });
//            setLeftPaneItems(LEFTPANE_GRAPHIC);
//            addPageListener(new PageListener() {
//                @Override
//                public void pageEventFired(PageEvent pageEvent) {
//                    if (pageEvent.getGID() == PageEvent.PAGE_OPENED) {
//                        OPDE.debug("CompletionPage OPENDED");
//                    }
//                }
//            });
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();

            JTextPane txt = new JTextPane();
            txt.setEditable(false);
            txt.setContentType("text/html");
            txt.setOpaque(false);
            txt.setText(SYSTools.toHTML(SYSConst.html_div(check())));

            addComponent(txt, true);
            addSpace();
//            addText("Drücken Sie auf FERTIG, wenn's los gehen soll.", SYSConst.ARIAL14);
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.CANCEL);
        }

        private String check() {
            String result = "<b>" + OPDE.lang.getString(internalClassID + ".page7.summaryline1") + "</b><br/>";
            result += OPDE.lang.getString(internalClassID + ".page7.summaryline2") + "<br/>";
            result += "<ul>";
            result += "<li>" + ResidentTools.getFullName(bewohner) + "</li>";
            result += "<li>" + OPDE.lang.getString("misc.msg.dob") + ": " + DateFormat.getDateInstance().format(bewohner.getDOB()) + "</li>";
            result += "<li>" + OPDE.lang.getString("misc.msg.primaryNurse") + ": " + bewohner.getPN1().getFullname() + "</li>";
            result += "<li>" + OPDE.lang.getString("misc.msg.gp") + ": " + DocTools.getFullName(bewohner.getGP()) + "</li>";
            result += "<li>" + OPDE.lang.getString("misc.msg.lc") + ": " + LCustodianTools.getFullName(bewohner.getLCustodian1()) + "</li>";

            result += "<li>" + OPDE.lang.getString("misc.msg.movein") + ": " + DateFormat.getDateInstance().format(bwinfo_hauf.getFrom()) + "</li>";
            result += "<li>" + OPDE.lang.getString("misc.msg.subdivision") + ": " + bewohner.getStation().getName() + "</li>";

            result += "</ul>";

            result += "<p>" + OPDE.lang.getString(internalClassID + ".page7.summaryline3") + "</p>";
            return result;
        }


    }


}
