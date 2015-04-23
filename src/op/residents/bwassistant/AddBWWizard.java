package op.residents.bwassistant;

import com.jidesoft.dialog.*;
import com.jidesoft.wizard.*;
import entity.building.Rooms;
import entity.building.Station;
import entity.info.*;
import entity.prescription.GP;
import entity.prescription.GPTools;
import entity.system.Unique;
import entity.system.UniqueTools;
import entity.system.Users;
import op.OPDE;
import op.threads.DisplayMessage;
import op.tools.SYSConst;
import op.tools.SYSTools;
import org.apache.commons.collections.Closure;
import org.javatuples.Triplet;

import javax.persistence.EntityManager;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.util.Date;
import java.util.Properties;

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
    private Resident resident;
    private Closure finishAction;
    private ResInfo resinfo_hauf, resinfo_room;

    public AddBWWizard(Closure finishAction) {
        this.finishAction = finishAction;

        createWizard();
    }

    private void createWizard() {
        wizard = new WizardDialog(new JFrame(), false);
        PageList model = new PageList();

        AbstractWizardPage page1 = new WelcomePage(SYSTools.xx("opde.admin.bw.wizard.page1.title"), SYSTools.xx("opde.admin.bw.wizard.page1.description"));
        AbstractWizardPage page2 = new BasisInfoPage(SYSTools.xx("opde.admin.bw.wizard.page2.title"), SYSTools.xx("opde.admin.bw.wizard.page2.description"));
        AbstractWizardPage page3 = new PNPage(SYSTools.xx("opde.admin.bw.wizard.page3.title"), SYSTools.xx("opde.admin.bw.wizard.page3.description"));
        AbstractWizardPage page4 = new GPPage(SYSTools.xx("opde.admin.bw.wizard.page4.title"), SYSTools.xx("opde.admin.bw.wizard.page4.description"));
//        AbstractWizardPage page5 = new LCPage(SYSTools.xx(PnlLC.internalClassID + ".title"), SYSTools.xx(PnlLC.internalClassID + ".description"));
        AbstractWizardPage page6 = new HaufPage(SYSTools.xx("opde.admin.bw.wizard.page6.title"), SYSTools.xx("opde.admin.bw.wizard.page6.description"));
        AbstractWizardPage page7 = new CompletionPage(SYSTools.xx("opde.admin.bw.wizard.page7.title"), SYSTools.xx("opde.admin.bw.wizard.page7.description"));

        model.append(page1);
        model.append(page2);
        model.append(page3);
        model.append(page4);
//        model.append(page5);
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
        ((JPanel) wizard.getContentPane()).setBorder(new LineBorder(Color.BLACK, 1));
        wizard.pack();
    }


    public WizardDialog getWizard() {
        return wizard;
    }

    private void save() {
        EntityManager em = OPDE.createEM();
        try {
            em.getTransaction().begin();


            String prefix = resident.getName().substring(0, 1) + resident.getFirstname().substring(0, 1);
            prefix = prefix.toUpperCase();

            Unique unique = UniqueTools.getNewUID(em, prefix);
            String bwkennung = prefix + unique.getUid();
            resident.setRID(bwkennung);

            resident = em.merge(resident);
            resinfo_hauf = em.merge(resinfo_hauf);
            if (resinfo_room != null) em.merge(resinfo_room);

            em.getTransaction().commit();
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(ResidentTools.getTextCompact(resident) + " " + SYSTools.xx("misc.msg.entrysuccessful"), 6));
            finishAction.execute(resident);
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
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();

            JTextPane txt = new JTextPane();
            txt.setEditable(false);
            txt.setContentType("text/html");
            txt.setOpaque(false);
            txt.setText("<html><font face=\"" + SYSConst.ARIAL14.getFamily() + "\">" +
                    SYSTools.xx("opde.admin.bw.wizard.page1.welcome") +
                    "</font></html>");

            addComponent(txt, true);
            addSpace();
            addText(SYSTools.xx("opde.wizards.buttontext.letsgo"), SYSConst.ARIAL14);
        }

//        @Override
//        public Image getGraphic() {
//            return getLeftGraphic("/artwork/aspecton1.png");
//        }

        @Override
        public void setupWizardButtons() {
            super.setupWizardButtons();
            fireButtonEvent(ButtonEvent.CHANGE_BUTTON_TEXT, ButtonNames.BACK, SYSTools.xx("opde.wizards.buttontext.back"));
            fireButtonEvent(ButtonEvent.CHANGE_BUTTON_TEXT, ButtonNames.NEXT, SYSTools.xx("opde.wizards.buttontext.next"));
            fireButtonEvent(ButtonEvent.CHANGE_BUTTON_TEXT, ButtonNames.FINISH, SYSTools.xx("opde.wizards.buttontext.finish"));
            fireButtonEvent(ButtonEvent.CHANGE_BUTTON_TEXT, ButtonNames.CANCEL, SYSTools.xx("opde.wizards.buttontext.cancel"));

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
            fireButtonEvent(resident == null ? ButtonEvent.DISABLE_BUTTON : ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.CANCEL);
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();
            addComponent(new PnlBWBasisInfo(new Closure() {
                @Override
                public void execute(Object o) {
                    resident = (Resident) o;
                    setupWizardButtons();
                }
            }), true);
        }


    }

    private class PNPage extends DefaultWizardPage {
//         boolean alreadyexecute = false;

        public PNPage(String title, String description) {
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
            fireButtonEvent(resident == null ? ButtonEvent.DISABLE_BUTTON : ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.CANCEL);
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();
            addComponent(new PnlBV(new Closure() {
                @Override
                public void execute(Object o) {
                    resident.setPN1((Users) o);
                    setupWizardButtons();
                }
            }), true);
        }
    }

    private class GPPage extends DefaultWizardPage {
        private PnlGP pnlGP;
//        private boolean alreadyexecute = false;

        public GPPage(String title, String description) {
            super(title, description);
            pnlGP = new PnlGP(new Closure() {
                @Override
                public void execute(Object o) {
                    if (o != null) {
                        resident.setGP((GP) o);
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
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.CANCEL);
        }

        @Override
        protected void initContentPane() {
            super.initContentPane();
            addComponent(pnlGP, true);
        }
    }


//    private class LCPage extends DefaultWizardPage {
//        private PnlLC pnlLC;
////        private boolean alreadyexecute = false;
//
//        public LCPage(String title, String description) {
//            super(title, description);
//            pnlLC = new PnlLC(new Closure() {
//                @Override
//                public void execute(Object o) {
//                    resident.setLCustodian1((LCustodian) o);
//                    setupWizardButtons();
//                }
//            });
//            addPageListener(new PageListener() {
//                @Override
//                public void pageEventFired(PageEvent pageEvent) {
//                    if (pageEvent.getID() != PageEvent.PAGE_OPENED) return;
//                    setupWizardButtons();
//                }
//            });
//            setupWizardButtons();
//        }
//
//        @Override
//        public void setupWizardButtons() {
//            super.setupWizardButtons();
//
//            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.BACK);
//            fireButtonEvent(ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
//            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
//            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.CANCEL);
//        }
//
//        @Override
//        protected void initContentPane() {
//            super.initContentPane();
//            addComponent(pnlLC, true);
//        }
//    }


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
            fireButtonEvent(resident == null || resinfo_hauf == null || resident.getStation() == null ? ButtonEvent.DISABLE_BUTTON : ButtonEvent.ENABLE_BUTTON, ButtonNames.NEXT);
            fireButtonEvent(ButtonEvent.HIDE_BUTTON, ButtonNames.FINISH);
            fireButtonEvent(ButtonEvent.SHOW_BUTTON, ButtonNames.CANCEL);
        }


        @Override
        protected void initContentPane() {
            super.initContentPane();
            addComponent(new PnlHAUF(new Closure() {
                @Override
                public void execute(Object o) {
                    Date hauf = ((Triplet<Date, Station, Rooms>) o).getValue0();
                    Station station = ((Triplet<Date, Station, Rooms>) o).getValue1();
                    Rooms room = ((Triplet<Date, Station, Rooms>) o).getValue2();

                    if (hauf != null) {
                        resinfo_hauf = new ResInfo(ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_STAY), resident);
                        resinfo_hauf.setFrom(hauf);
                    } else {
                        resinfo_hauf = null;
                    }
                    if (room != null) {
                        resinfo_room = new ResInfo(ResInfoTypeTools.getByType(ResInfoTypeTools.TYPE_ROOM), resident);

                        Properties props = new Properties();
                        props.put("room.id", Long.toString(room.getRoomID()));
                        props.put("room.text", room.toString());
                        ResInfoTools.setContent(resinfo_room, props);
                        resinfo_room.setFrom(hauf);
                    } else {
                        resinfo_room = null;
                    }
                    resident.setStation(station);
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
            String result = "<b>" + SYSTools.xx("opde.admin.bw.wizard.page7.summaryline1") + "</b><br/>";
            result += SYSTools.xx("opde.admin.bw.wizard.page7.summaryline2") + "<br/>";
            result += "<ul>";
            result += "<li>" + ResidentTools.getFullName(resident) + "</li>";
            result += "<li>" + SYSTools.xx("misc.msg.dob") + ": " + DateFormat.getDateInstance().format(resident.getDOB()) + "</li>";
            result += "<li>" + SYSTools.xx("misc.msg.primaryNurse") + ": " + (resident.getPN1() == null ? SYSTools.xx("misc.msg.noentryyet") : resident.getPN1().getFullname()) + "</li>";
            result += "<li>" + SYSTools.xx("misc.msg.gp") + ": " + GPTools.getFullName(resident.getGP()) + "</li>";
//            result += "<li>" + SYSTools.xx("misc.msg.lc") + ": " + LCustodianTools.getFullName(resident.getLCustodian1()) + "</li>";

            result += "<li>" + SYSTools.xx("misc.msg.movein") + ": " + DateFormat.getDateInstance().format(resinfo_hauf.getFrom()) + "</li>";
            result += "<li>" + SYSTools.xx("misc.msg.room") + ": " + (resinfo_room == null ? SYSTools.xx("misc.msg.noentryyet") : resinfo_room.toString()) + "</li>";
            result += "<li>" + SYSTools.xx("misc.msg.subdivision") + ": " + resident.getStation().getName() + "</li>";

            result += "</ul>";

            result += "<p>" + SYSTools.xx("opde.admin.bw.wizard.page7.summaryline3") + "</p>";
            return result;
        }


    }


}
