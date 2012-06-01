package op.care.med.prodassistant;

import com.jidesoft.dialog.*;
import com.jidesoft.wizard.*;
import entity.verordnungen.*;
import op.OPDE;
import op.threads.DisplayMessage;
import org.apache.commons.collections.Closure;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 30.04.12
 * Time: 14:53
 * To change this template use File | Settings | File Templates.
 */
public class MedProductWizard {
    private WizardDialog wizard;
    private MedProdukte produkt;
    private Darreichung darreichung;
    private MedPackung packung;
    private MedHersteller hersteller;
    private Closure finishAction;

    private final int PAGE_WELCOME = 0;
    private final int PAGE_PRODUKT = 1;
    private final int PAGE_ZUSATZ = 2;
    private final int PAGE_PACKUNG = 3;
    private final int PAGE_HERSTELLER = 4;
    private final int PAGE_COMPLETION = 5;

    public MedProductWizard(Closure finishAction) {
        this.finishAction = finishAction;

        wizard = new WizardDialog(new JFrame(), false);
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
        wizard.setStepsPaneNavigable(false);
        wizard.setFinishAction(new AbstractAction("Finish") {
            public void actionPerformed(ActionEvent e) {
                if (wizard.closeCurrentPage(wizard.getButtonPanel().getButtonByName(ButtonNames.FINISH))) {
                    save();
                }
            }
        });
        wizard.pack();
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

            if (produkt.getHersteller() == null) {
                produkt.setHersteller(hersteller);
            }
            if (darreichung.getDafID() == null) {
                darreichung.setMedProdukt(produkt);
                produkt.getDarreichungen().add(darreichung);
            }
            packung.setDarreichung(darreichung);
            darreichung.getPackungen().add(packung);

            em.getTransaction().commit();
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(produkt.getBezeichnung() + ", " + DarreichungTools.toPrettyString(darreichung) + ", " + MedPackungTools.toPrettyString(packung) + " erfolgreich eingetragen", 6));
            finishAction.execute(packung);
        } catch (Exception e) {
            em.getTransaction().rollback();
            OPDE.fatal(e);
        } finally {
            em.close();
        }
    }

    private class WelcomePage extends WelcomeWizardPage {
        public WelcomePage(String title, String description) {
            super(title, description);
            setLeftPaneItems(LEFTPANE_STEPS);
            addPageListener(new PageListener() {
                @Override
                public void pageEventFired(PageEvent pageEvent) {
                    if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                        OPDE.debug("WelcomePage OPENDED");
                        produkt = null;
                        darreichung = null;
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
            txt.setText("<html><font face=\"" + OPDE.arial14.getFamily() + "\">Mit diesem Programm können Sie:" +
                    "<ul>" +
                    "<li>neue Medikamente ins System eintragen</li>" +
                    "<li>die Darreichungsform bestimmen</li>" +
                    "<li>Packungsgrößen eingeben</li>" +
                    "</ul>" +
                    "Sie werden hierzu Schritt für Schritt durch die Eingabe geführt.<p/>" +
                    "Die Bearbeitung kann jederzeit abgebrochen werden, drücken Sie dazu infach neben den Assistenten auf eine freie Fläche.</font></html>");

            addComponent(txt, true);
            addSpace();
            addText("Drücken Sie auf WEITER, wenn's los gehen soll.", OPDE.arial14);
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.CANCEL);
        }


    }

    private class ProduktPage extends DefaultWizardPage {
        public ProduktPage(String title, String description) {
            super(title, description);
            setLeftPaneItems(LEFTPANE_STEPS);

            addPageListener(new PageListener() {
                @Override
                public void pageEventFired(PageEvent pageEvent) {
                    if (pageEvent.getID() == PageEvent.PAGE_CLOSING) {
                        OPDE.debug(pageEvent.getSource());
                    } else if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                        OPDE.debug("ProduktPage OPENDED");
                        produkt = null;
                    }
                }
            });
        }

//        @Override
//        public Graphics getGraphics() {
//            return new ImageIcon(getClass().getResource("/artwork/aspecton3.jpg")).getImage().getGraphics();
//        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(produkt == null ? ButtonEvent.DISABLE_BUTTON : ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.CANCEL);
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
            }), true);
        }


    }

    private class ZusatzPage extends DefaultWizardPage {
        public ZusatzPage(String title, String description) {
            super(title, description);
            setLeftPaneItems(LEFTPANE_STEPS);
            addPageListener(new PageListener() {
                @Override
                public void pageEventFired(PageEvent pageEvent) {
                    if (pageEvent.getID() == PageEvent.PAGE_CLOSING) {
                        OPDE.debug(pageEvent.getSource());
                    } else if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                        OPDE.debug("ZusatzPage OPENDED");
                        darreichung = null;
                    }
                }
            });
        }


        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(darreichung == null ? ButtonEvent.DISABLE_BUTTON : ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.CANCEL);
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();
            addComponent(new PnlZusatz(new Closure() {
                @Override
                public void execute(Object o) {
                    darreichung = (Darreichung) o;
                    setupWizardButtons();
                }
            }, produkt), true);
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
                    updateNextPage();
                }
            }, darreichung);

            setLeftPaneItems(LEFTPANE_STEPS);
            addPageListener(new PageListener() {
                @Override
                public void pageEventFired(PageEvent pageEvent) {
                    if (pageEvent.getID() == PageEvent.PAGE_CLOSING) {
                        OPDE.debug(pageEvent.getSource());
                    } else if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                        OPDE.debug("PackungPage OPENDED");
                        packung = null;
                        pnlPackung.setLabelEinheit(MedFormenTools.toPrettyString(darreichung.getMedForm()));
                    }
                }
            });
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(packung == null ? ButtonEvent.DISABLE_BUTTON : ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.CANCEL);
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
        public HerstellerPage(String title, String description) {
            super(title, description);
            setLeftPaneItems(LEFTPANE_STEPS);
            addPageListener(new PageListener() {
                @Override
                public void pageEventFired(PageEvent pageEvent) {
                    if (pageEvent.getID() == PageEvent.PAGE_CLOSING) {
                        OPDE.debug(pageEvent.getSource());
                    } else if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                        OPDE.debug("HerstellerPage OPENDED");
                        packung = null;
                    }
                }
            });
        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
            fireButtonEvent(hersteller == null ? ButtonEvent.DISABLE_BUTTON : ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.CANCEL);
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();
            addComponent(new PnlHersteller(new Closure() {
                @Override
                public void execute(Object o) {
                    hersteller = (MedHersteller) o;
                    setupWizardButtons();
                }
            }), true);
        }

    }

    private class CompletionPage extends CompletionWizardPage {
        public CompletionPage(String title, String description) {
            super(title, description);
            setLeftPaneItems(LEFTPANE_STEPS);
            addPageListener(new PageListener() {
                @Override
                public void pageEventFired(PageEvent pageEvent) {
                    if (pageEvent.getID() == PageEvent.PAGE_OPENED) {
                        OPDE.debug("WelcomePage OPENDED");
                        produkt = null;
                        darreichung = null;
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
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.CANCEL);
        }

        private String check() {
            String result = "<b>Na dann schaun wir mal...</b><br/>";
            result += "Folgendes haben Sie eingeben:<br/>";
            result += "<ul>";
            result += "<li>Medikament: <b>" + produkt.getBezeichnung() + "</b>" + (produkt.getMedPID() == null ? " <i>wird neu erstellt</i>" : "") + "</li>";
            result += "<li>Zusatzbezeichnung und Darreichungsform: <b>" + DarreichungTools.toPrettyStringMedium(darreichung) + "</b>" + (darreichung.getDafID() == null ? " <i>wird neu erstellt</i>" : "") + "</li>";
            result += "<li>Es wird eine neue Packung eingetragen: <b>" + MedPackungTools.toPrettyString(packung) + "</b></li>";

            if (hersteller != null) {
                result += "<li>Hersteller: <b>" + hersteller.getFirma() + ", " + hersteller.getOrt() + "</b>" + (hersteller.getMphid() == null ? " <i>wird neu erstellt</i>" : "") + "</li>";
            }

            result += "</ul>";

            result += "<p>";
            result += "Wenn Sie sicher sind, dann bestätigen Sie nun die Eingaben. Ansonsten gehen Sie einfach zurück und korrigieren das Nötige.</p>" +
                    "</font>";
            return result;
        }


    }
}