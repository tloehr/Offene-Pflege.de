package op.care.med.prodassistant;

import com.jidesoft.dialog.*;
import com.jidesoft.wizard.*;
import entity.prescription.*;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.SYSConst;
import org.apache.commons.collections.Closure;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 30.04.12
 * Time: 14:53
 * To change this template use File | Settings | File Templates.
 */
public class MedProductWizard {

    public static final String internalClassID = "opde.medication.medproduct.wizard";

    private WizardDialog wizard;
    private MedProdukte produkt;
    private TradeForm darreichung;
    private MedPackung packung;
    private MedHersteller hersteller;
    private Closure finishAction;
    private String pzntemplate = null, prodtemplate = null;

    private final int PAGE_WELCOME = 0;
    private final int PAGE_PRODUKT = 1;
    private final int PAGE_ZUSATZ = 2;
    private final int PAGE_PACKUNG = 3;
    private final int PAGE_HERSTELLER = 4;
    private final int PAGE_COMPLETION = 5;

    public MedProductWizard(Closure finishAction, String template) {
        this.finishAction = finishAction;

        if (template != null) {
            pzntemplate = MedPackungTools.parsePZN(template);
            if (pzntemplate == null) {
                prodtemplate = template.trim();
            }
        }
        createWizard();
    }

    private void createWizard(){
        wizard = new WizardDialog(OPDE.getMainframe(), false);
        PageList model = new PageList();

        AbstractWizardPage page1 = new WelcomePage("Assistent zur Eingabe von Medizin-Produkten",
                "Dieser Assistent ermöglicht es Ihnen auf einfache Weise neue Medizinprodukte in die Datenbank von OPDE einzugeben.");
        AbstractWizardPage page2 = new ProduktPage("Produkteingabe", "Geben Sie hier zunächst den Produktnamen ohne irgendwelche Zusätze ein. Also besonders keine Angaben wie '10mg' oder 'retard'.");
        AbstractWizardPage page3 = new ZusatzPage("Zusatzbezeichnungen und Darreichungsformen",
                "Geben Sie jetzt evtl. vorhandene Zusatzbezeichnungen ein. Das können z.B. sein '10mg', 'retard', 'forte' etc. Häufig handelt es sich um Angaben zur Stärke oder Wirkstoffentfaltung. " +
                        "Ebenso müssen Sie hier die Darreichungsform auswählen.");
        AbstractWizardPage page4 = new PackungPage("Packung", "Geben Sie hier eine Verpackung für dieses Medikament ein.");
        AbstractWizardPage page5 = new HerstellerPage("Hersteller", "Wählen Sie einen Hersteller aus der Liste aus, oder geben Sie einen neuen ein.");
        AbstractWizardPage page6 = new CompletionPage("Zusammenfassung", "Bitte prüfen Sie hier Ihre Eingaben.");

        model.append(page1);
        model.append(page2);
        model.append(page3);
        model.append(page4);
        model.append(page5);
        model.append(page6);

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
        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();
            produkt = em.merge(produkt);
            darreichung = em.merge(darreichung);
            packung = em.merge(packung);
            if (hersteller != null) {
                hersteller = em.merge(hersteller);
            }

            if (!produkt.getDarreichungen().contains(darreichung)) {
                produkt.getDarreichungen().add(darreichung);
            }

            darreichung.getPackungen().add(packung);

            em.getTransaction().commit();
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(produkt.getBezeichnung() + ", " + TradeFormTools.toPrettyString(darreichung) + ", " + MedPackungTools.toPrettyString(packung) + " erfolgreich eingetragen", 6));
            finishAction.execute(packung);
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
            setLeftPaneItems(LEFTPANE_GRAPHIC);
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();

            JTextPane txt = new JTextPane();
            txt.setEditable(false);
            txt.setContentType("text/html");
            txt.setOpaque(false);
            txt.setText("<html><font face=\"" + SYSConst.ARIAL14.getFamily() + "\">" +
                    OPDE.lang.getString(internalClassID + ".welcome.html") +
                    "</font></html>");

            addComponent(txt, true);
            addSpace();
            addText("Drücken Sie auf WEITER, wenn's los gehen soll.", SYSConst.ARIAL14);
        }

        @Override
        public Image getGraphic() {
            return getLeftGraphic("/artwork/aspecton1.png");
        }

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

    private class ProduktPage extends DefaultWizardPage {

        public ProduktPage(String title, String description) {
            super(title, description);
            setLeftPaneItems(LEFTPANE_GRAPHIC);

//            addPageListener(new PageListener() {
//                @Override
//                public void pageEventFired(PageEvent pageEvent) {
//                    if (pageEvent.getID() == PageEvent.PAGE_CLOSING) {
//                        OPDE.debug(pageEvent.getSource());
//                    } else if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
//                        OPDE.debug("ProduktPage OPENDED");
//                    }
//                }
//            });
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();

            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(produkt == null ? ButtonEvent.DISABLE_BUTTON : ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.CANCEL);
        }

        @Override
        public Image getGraphic() {
            return getLeftGraphic("/artwork/aspecton1.png");
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();
            addComponent(new PnlProdukt(new Closure() {
                @Override
                public void execute(Object o) {
                    produkt = (MedProdukte) o;
                    setupWizardButtons();
                }
            }, prodtemplate), true);
        }


    }

    private class ZusatzPage extends DefaultWizardPage {
        private PnlZusatz pnlZusatz;

        public ZusatzPage(String title, String description) {
            super(title, description);
            setLeftPaneItems(LEFTPANE_GRAPHIC);
            addPageListener(new PageListener() {
                @Override
                public void pageEventFired(PageEvent pageEvent) {
                    if (pageEvent.getID() == PageEvent.PAGE_CLOSING) {
                        OPDE.debug(pageEvent.getSource());
                    } else if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                        OPDE.debug("ZusatzPage OPENDED");
//                        darreichung = null;
                        pnlZusatz.setProdukt(produkt);
                    }
                }
            });
        }

        @Override
        public Image getGraphic() {
            return getLeftGraphic("/artwork/aspecton1.png");
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(darreichung == null ? ButtonEvent.DISABLE_BUTTON : ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.CANCEL);
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();
            pnlZusatz = new PnlZusatz(new Closure() {
                @Override
                public void execute(Object o) {
                    darreichung = (TradeForm) o;
                    setupWizardButtons();
                }
            }, produkt);
            addComponent(pnlZusatz, true);
        }
    }

    private class PackungPage extends DefaultWizardPage {
        private PnlPackung pnlPackung;

        public PackungPage(String title, String description) {
            super(title, description);
            pnlPackung = new PnlPackung(new Closure() {
                @Override
                public void execute(Object o) {
                    packung = (MedPackung) o;
                    setupWizardButtons();
                }
            }, pzntemplate);

            setLeftPaneItems(LEFTPANE_STEPS);
            addPageListener(new PageListener() {
                @Override
                public void pageEventFired(PageEvent pageEvent) {
                    if (pageEvent.getID() == PageEvent.PAGE_CLOSING) {
                        OPDE.debug(pageEvent.getSource());
                    } else if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                        OPDE.debug("PackungPage OPENDED");
//                        packung = null;
                        pnlPackung.setLabelEinheit(DosageFormTools.toPrettyStringPackung(darreichung.getDosageForm()));
                        pnlPackung.setDarreichung(darreichung);
                    }
                }
            });
        }


        @Override
        public Image getGraphic() {
            return getLeftGraphic("/artwork/aspecton1.png");
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(packung == null ? ButtonEvent.DISABLE_BUTTON : ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.CANCEL);
            updateNextPage();
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();
            addComponent(pnlPackung, true);
        }

        private void updateNextPage() {
            if (produkt.getHersteller() != null) {
                getOwner().setNextPage(getOwner().getPageList().getPage(PAGE_COMPLETION));
            } else {
                getOwner().setNextPage(getOwner().getPageList().getPage(PAGE_HERSTELLER));
            }
        }
    }

    private class HerstellerPage extends DefaultWizardPage {
        private PnlHersteller pnlHersteller;

        public HerstellerPage(String title, String description) {
            super(title, description);
            setLeftPaneItems(LEFTPANE_GRAPHIC);
            addPageListener(new PageListener() {
                @Override
                public void pageEventFired(PageEvent pageEvent) {
                    if (pageEvent.getID() == PageEvent.PAGE_CLOSING) {
                        OPDE.debug(pageEvent.getSource());
                    } else if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                        OPDE.debug("HerstellerPage OPENDED");
//                        hersteller = null;
                        pnlHersteller.setProdukt(produkt);
//                        setupWizardButtons();
                    }
                }
            });
        }

        @Override
        public Image getGraphic() {
            return getLeftGraphic("/artwork/aspecton1.png");
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(hersteller == null ? ButtonEvent.DISABLE_BUTTON : ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.CANCEL);
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();
            pnlHersteller = new PnlHersteller(new Closure() {
                @Override
                public void execute(Object o) {
                    hersteller = (MedHersteller) o;
                    setupWizardButtons();
                }
            }, produkt, wizard);
            addComponent(pnlHersteller, true);
        }

    }

    private class CompletionPage extends CompletionWizardPage {
        public CompletionPage(String title, String description) {
            super(title, description);
            setLeftPaneItems(LEFTPANE_GRAPHIC);
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
            txt.setText("<html><font face=\"" + SYSConst.ARIAL14.getFamily() + "\">" +
                    check() +
                    "</font>" +
                    "</html>");

            addComponent(txt, true);
            addSpace();
            addText("Drücken Sie auf WEITER, wenn's los gehen soll.", SYSConst.ARIAL14);
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
            result += "<li>Zusatzbezeichnung und Darreichungsform: <b>" + TradeFormTools.toPrettyStringMedium(darreichung) + "</b>" + (darreichung.getDafID() == null ? " <i>wird neu erstellt</i>" : " <i>gab es schon</i>") + "</li>";
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