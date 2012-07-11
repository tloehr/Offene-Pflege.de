package op.admin.residents.bwassistant;

import com.jidesoft.dialog.*;
import com.jidesoft.wizard.*;
import entity.*;
import entity.info.BWInfo;
import entity.info.BWInfoTypTools;
import op.OPDE;
import op.tools.Pair;
import op.tools.SYSConst;
import org.apache.commons.collections.Closure;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
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
    private Bewohner bewohner;
    private Closure finishAction;
    private BWInfo bwinfo_hauf;

    private final int PAGE_WELCOME = 0;
    private final int PAGE_BASISINFO = 1;
    private final int PAGE_HAUSARZT = 2;
    private final int PAGE_BETREUER = 3;
    private final int PAGE_BV = 4;
    private final int PAGE_AUFNAHME = 5;
    private final int PAGE_SUMMARY = 6;

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
        AbstractWizardPage page4 = new HausarztPage(OPDE.lang.getString(PnlHausarzt.internalClassID + ".title"), OPDE.lang.getString(PnlHausarzt.internalClassID + ".description"));
        AbstractWizardPage page5 = new BetreuerPage(OPDE.lang.getString(PnlBetreuer.internalClassID + ".title"), OPDE.lang.getString(PnlBetreuer.internalClassID + ".description"));
        AbstractWizardPage page6 = new HaufPage(OPDE.lang.getString(PnlHAUF.internalClassID + ".title"), OPDE.lang.getString(PnlHAUF.internalClassID + ".description"));
        AbstractWizardPage page7 = new CompletionPage(OPDE.lang.getString(internalClassID + ".page7.title"), OPDE.lang.getString(internalClassID + ".page7.description"));

        model.append(page1);
        model.append(page2);
        model.append(page3);
        model.append(page4);
        model.append(page5);
//        model.append(page6);

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

    private Image getLeftGraphic(String iconname) {
        JLabel lbl = new JLabel(new ImageIcon(getClass().getResource(iconname)));
        lbl.setSize(lbl.getPreferredSize());
        lbl.doLayout();
        GraphicsConfiguration gfxConfig =
                GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getDefaultScreenDevice()
                        .getDefaultConfiguration();
        BufferedImage image =
                gfxConfig.createCompatibleImage(lbl.getWidth(), lbl.getHeight());
        lbl.paint(image.getGraphics());
        return image;
    }

    public WizardDialog getWizard() {
        return wizard;
    }

    private void save() {
//        EntityManager em = OPDE.createEM();
//        try {
//            em.getTransaction().begin();
//            produkt = em.merge(produkt);
//            darreichung = em.merge(darreichung);
//            packung = em.merge(packung);
//            if (hersteller != null) {
//                hersteller = em.merge(hersteller);
//            }
//
//            if (!produkt.getDarreichungen().contains(darreichung)) {
//                produkt.getDarreichungen().add(darreichung);
//            }
//
//            darreichung.getPackungen().add(packung);
//
//            em.getTransaction().commit();
//            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(produkt.getBezeichnung() + ", " + DarreichungTools.toPrettyString(darreichung) + ", " + MedPackungTools.toPrettyString(packung) + " erfolgreich eingetragen", 6));
//            finishAction.execute(packung);
//        } catch (Exception e) {
//            if (em.getTransaction().isActive()) {
//                em.getTransaction().rollback();
//            }
//            OPDE.fatal(e);
//        } finally {
//            em.close();
//        }
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

        public BasisInfoPage(String title, String description) {
            super(title, description);
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();

            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(bewohner == null ? ButtonEvent.DISABLE_BUTTON : ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
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
            addComponent(new PnlBWBasisInfo(new Closure() {
                @Override
                public void execute(Object o) {
                    bewohner = (Bewohner) o;
                    setupWizardButtons();
                }
            }), true);
        }


    }

    private class BVPage extends DefaultWizardPage {

        public BVPage(String title, String description) {
            super(title, description);
            setupWizardButtons();
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();

            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(bewohner == null || bewohner.getBv1() == null ? ButtonEvent.DISABLE_BUTTON : ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.CANCEL);
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();
            addComponent(new PnlBV(new Closure() {
                @Override
                public void execute(Object o) {
                    bewohner.setBv1((Users) o);
                    setupWizardButtons();
                }
            }), true);
        }
    }

    private class HausarztPage extends DefaultWizardPage {
        private PnlHausarzt pnlHausarzt;
        private boolean alreadyexecute = false;

        public HausarztPage(String title, String description) {
            super(title, description);
            pnlHausarzt = new PnlHausarzt(new Closure() {
                @Override
                public void execute(Object o) {
                    if (o != null) {
                        bewohner.setHausarzt((Arzt) o);
                    }
                    setupWizardButtons();
                }
            });
            addPageListener(new PageListener() {
                @Override
                public void pageEventFired(PageEvent pageEvent) {
                    if (!alreadyexecute && pageEvent.getID() == PageEvent.PAGE_OPENED) {
                        alreadyexecute = true;
                        pnlHausarzt.initSplitPanel();
                    }
                }
            });
            setupWizardButtons();
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();

            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(bewohner == null || bewohner.getHausarzt() == null ? ButtonEvent.DISABLE_BUTTON : ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.CANCEL);
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();
            addComponent(pnlHausarzt, true);
        }
    }


    private class BetreuerPage extends DefaultWizardPage {
        private PnlBetreuer pnlBetreuer;
        private boolean alreadyexecute = false;

        public BetreuerPage(String title, String description) {
            super(title, description);
            pnlBetreuer = new PnlBetreuer(new Closure() {
                @Override
                public void execute(Object o) {
                    bewohner.setBetreuer1((Betreuer) o);
                    setupWizardButtons();
                }
            });
            addPageListener(new PageListener() {
                @Override
                public void pageEventFired(PageEvent pageEvent) {
                    if (!alreadyexecute && pageEvent.getID() == PageEvent.PAGE_OPENED) {
                        alreadyexecute = true;
                        pnlBetreuer.initSplitPanel();
                    }
                }
            });
            setupWizardButtons();
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();

            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(bewohner == null || bewohner.getBetreuer1() == null ? ButtonEvent.DISABLE_BUTTON : ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.CANCEL);
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();
            addComponent(pnlBetreuer, true);
        }
    }


    private class HaufPage extends DefaultWizardPage {

        public HaufPage(String title, String description) {
            super(title, description);
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
                    Date hauf = ((Pair<Date, Stationen>) o).getFirst();
                    Stationen station = ((Pair<Date, Stationen>) o).getSecond();
                    if (hauf != null) {
                        bwinfo_hauf = new BWInfo(BWInfoTypTools.findByBWINFTYP("HAUF"), bewohner);
                        bwinfo_hauf.setVon(hauf);
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
//            setLeftPaneItems(LEFTPANE_GRAPHIC);
            addPageListener(new PageListener() {
                @Override
                public void pageEventFired(PageEvent pageEvent) {
                    if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                        OPDE.debug("CompletionPage OPENDED");
                    }
                }
            });
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();

            JTextPane txt = new JTextPane();
            txt.setEditable(false);
            txt.setContentType("text/html");
            txt.setOpaque(false);
            txt.setText("<html><font face=\"" + OPDE.arial14.getFamily() + "\">" +
                    check() +
                    "</font>" +
                    "</html>");

            addComponent(txt, true);
            addSpace();
            addText("Drücken Sie auf WEITER, wenn's los gehen soll.", OPDE.arial14);
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.CANCEL);
        }

        private String check() {
            String result = "<b>" + OPDE.lang.getString(internalClassID + ".summaryline1") + "</b><br/>";
            result += OPDE.lang.getString(internalClassID + ".summaryline2") + "<br/>";
            result += "<ul>";
            result += "<li>Medikament: <b>" + produkt.getBezeichnung() + "</b>" + (produkt.getMedPID() == null ? " <i>wird neu erstellt</i>" : " <i>gab es schon</i>") + "</li>";
            result += "<li>Zusatzbezeichnung und Darreichungsform: <b>" + DarreichungTools.toPrettyStringMedium(darreichung) + "</b>" + (darreichung.getDafID() == null ? " <i>wird neu erstellt</i>" : " <i>gab es schon</i>") + "</li>";
            result += "<li>Es wird eine <b>neue</b> Packung eingetragen: <b>" + MedPackungTools.toPrettyString(packung) + "</b></li>";

            MedHersteller displayHersteller = hersteller == null ? produkt.getHersteller() : hersteller;
            result += "<li>Hersteller: <b>" + displayHersteller.getFirma() + ", " + displayHersteller.getOrt() + "</b>" + (displayHersteller.getMphid() == null ? " <i>wird neu erstellt</i>" : " <i>gab es schon</i>") + "</li>";

            result += "</ul>";

            result += "<p>";
            result += "Wenn Sie sicher sind, dann bestätigen Sie nun die Eingaben. Ansonsten gehen Sie einfach zurück und korrigieren das Nötige.</p>" +
                    "</font>";
            return result;
        }


    }


}
