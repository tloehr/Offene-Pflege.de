package de.offene_pflege.backend.services;

import de.offene_pflege.backend.entity.done.Resident;
import de.offene_pflege.backend.entity.nursingprocess.Intervention;
import de.offene_pflege.backend.entity.nursingprocess.InterventionSchedule;
import de.offene_pflege.backend.entity.nursingprocess.NursingProcess;
import de.offene_pflege.op.OPDE;
import de.offene_pflege.op.tools.SYSCalendar;
import de.offene_pflege.op.tools.SYSConst;
import de.offene_pflege.op.tools.SYSTools;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.DateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA. User: tloehr Date: 19.07.12 Time: 16:23 To change this template use File | Settings | File
 * Templates.
 */
public class InterventionScheduleService {

    public static boolean isValid(InterventionSchedule is) {
        return (is.getUhrzeit() == null && is.getNachtMo() + is.getMorgens() + is.getMittags() + is.getNachmittags() + is.getAbends() + is.getNachtAb() > 0) || (is.getUhrzeit() != null && is.getUhrzeitAnzahl() > 0);
    }

    /**
     * @param date, zu prüfendes Datum.
     * @return Ist <code>true</code>, wenn diese Planung wöchentlich gilt und das Attribut mit dem aktuellen
     * Wochentagsnamen größer null ist.
     */
    public static boolean isPassenderWochentag(InterventionSchedule interventionSchedule, Date date) {
        boolean passend = false;

        if (interventionSchedule.getWoechentlich() > 0) { // wenn nicht wöchentlich, dann passt gar nix
            GregorianCalendar gcDate = SYSCalendar.toGC(date);
            switch (gcDate.get(GregorianCalendar.DAY_OF_WEEK)) {
                case GregorianCalendar.MONDAY: {
                    passend = interventionSchedule.getMon() > 0;
                    break;
                }
                case GregorianCalendar.TUESDAY: {
                    passend = interventionSchedule.getDie() > 0;
                    break;
                }
                case GregorianCalendar.WEDNESDAY: {
                    passend = interventionSchedule.getMit() > 0;
                    break;
                }
                case GregorianCalendar.THURSDAY: {
                    passend = interventionSchedule.getDon() > 0;
                    break;
                }
                case GregorianCalendar.FRIDAY: {
                    passend = interventionSchedule.getFre() > 0;
                    break;
                }
                case GregorianCalendar.SATURDAY: {
                    passend = interventionSchedule.getSam() > 0;
                    break;
                }
                case GregorianCalendar.SUNDAY: {
                    passend = interventionSchedule.getSon() > 0;
                    break;
                }
                default: {
                    passend = false;
                    break;
                }
            }
        }
        return passend;
    }

    /**
     * @param date, zu prüfendes Datum.
     * @return Ist <code>true</code>, wenn diese Planung monatlich gilt und das Attribut <code>tagnum</code> dem
     * aktuellen Tag im Monat entspricht
     * <b>oder</b> das Attribut mit dem aktuellen Wochentagsnamen gleich dem Wochentag im Monat entpricht (der erste
     * Mitwwoch im Monat hat 1, der zweite 2 usw...).
     */
    public static boolean isPassenderTagImMonat(InterventionSchedule interventionSchedule, Date date) {
        boolean passend = false;
        if (interventionSchedule.getMonatlich() > 0) { // wenn nicht monatlich, dann passt gar nix
            GregorianCalendar gcDate = SYSCalendar.toGC(date);
            passend = interventionSchedule.getTagNum() == gcDate.get(GregorianCalendar.DAY_OF_MONTH);

            if (!passend) {
                switch (gcDate.get(GregorianCalendar.DAY_OF_WEEK)) {
                    case GregorianCalendar.MONDAY: {
                        passend = interventionSchedule.getMon() == gcDate.get(GregorianCalendar.DAY_OF_WEEK_IN_MONTH);
                        break;
                    }
                    case GregorianCalendar.TUESDAY: {
                        passend = interventionSchedule.getDie() == gcDate.get(GregorianCalendar.DAY_OF_WEEK_IN_MONTH);
                        break;
                    }
                    case GregorianCalendar.WEDNESDAY: {
                        passend = interventionSchedule.getMit() == gcDate.get(GregorianCalendar.DAY_OF_WEEK_IN_MONTH);
                        break;
                    }
                    case GregorianCalendar.THURSDAY: {
                        passend = interventionSchedule.getDon() == gcDate.get(GregorianCalendar.DAY_OF_WEEK_IN_MONTH);
                        break;
                    }
                    case GregorianCalendar.FRIDAY: {
                        passend = interventionSchedule.getFre() == gcDate.get(GregorianCalendar.DAY_OF_WEEK_IN_MONTH);
                        break;
                    }
                    case GregorianCalendar.SATURDAY: {
                        passend = interventionSchedule.getSam() == gcDate.get(GregorianCalendar.DAY_OF_WEEK_IN_MONTH);
                        break;
                    }
                    case GregorianCalendar.SUNDAY: {
                        passend = interventionSchedule.getSon() == gcDate.get(GregorianCalendar.DAY_OF_WEEK_IN_MONTH);
                        break;
                    }
                    default: {
                        passend = false;
                        break;
                    }
                }
            }
        }
        return passend;
    }


    public static InterventionSchedule create(NursingProcess planung, Intervention intervention) {
         // todo: hier gehts weiter
        this.morgens = 1;
        this.taeglich = 1;
        this.lDatum = new LocalDate().toDate();
        this.nursingProcess = planung;
        this.intervention = intervention;

        this.nachtMo = 0;
        this.mittags = 0;
        this.nachmittags = 0;
        this.abends = 0;
        this.nachtAb = 0;
        this.uhrzeitAnzahl = 0;
        this.uhrzeit = null;
        this.woechentlich = 0;
        this.monatlich = 0;
        this.tagNum = 0;
        this.mon = 0;
        this.die = 0;
        this.mit = 0;
        this.don = 0;
        this.fre = 0;
        this.sam = 0;
        this.son = 0;
        this.erforderlich = false;
        this.bemerkung = null;
        this.uuid = UUID.randomUUID().toString();
    }


    public static String getTerminAsHTML(InterventionSchedule termin) {
        String result = "";

        final int ZEIT = 0;
        final int UHRZEIT = 1;
        int previousState = -1;

        int currentState;
        // Zeit verwendet ?
        if (termin.getUhrzeit() != null) {
            currentState = UHRZEIT;
        } else {
            currentState = ZEIT;
        }
        boolean headerNeeded = previousState == -1 || currentState != previousState;

        if (previousState > -1 && headerNeeded) {
            // noch den Footer vom letzten Durchgang dabei. Aber nur, wenn nicht
            // der erste Durchlauf, ein Wechsel stattgefunden hat und der
            // vorherige Zustand nicht MAXDOSE war, das braucht nämlich keinen Footer.
            result += "</table>";
        }
        previousState = currentState;
        if (currentState == ZEIT) {
            if (headerNeeded) {
                result += "<table id=\"fonttext\" border=\"1\">" +
                        "   <tr>" +
                        "      <th align=\"center\">" + SYSTools.xx("misc.msg.earlyinthemorning.short") + "</th>" +
                        "      <th align=\"center\">" + SYSTools.xx("misc.msg.morning.short") + "</th>" +
                        "      <th align=\"center\">" + SYSTools.xx("misc.msg.noon.short") + "</th>" +
                        "      <th align=\"center\">" + SYSTools.xx("misc.msg.afternoon.short") + "</th>" +
                        "      <th align=\"center\">" + SYSTools.xx("misc.msg.evening.short") + "</th>" +
                        "      <th align=\"center\">" + SYSTools.xx("misc.msg.lateatnight.short") + "</th>" +
                        "      <th align=\"center\">" + SYSTools.xx("misc.msg.repeat.short") + "</th>" +
                        "   </tr>";
            }
            String wdh = getRepeatPattern(termin);

            result += "    <tr>" +
                    "      <td align=\"center\">" + (termin.getNachtMo() > 0 ? termin.getNachtMo() : "--") + "</td>" +
                    "      <td align=\"center\">" + (termin.getMorgens() > 0 ? termin.getMorgens() : "--") + "</td>" +
                    "      <td align=\"center\">" + (termin.getMittags() > 0 ? termin.getMittags() : "--") + "</td>" +
                    "      <td align=\"center\">" + (termin.getNachmittags() > 0 ? termin.getNachmittags() : "--") + "</td>" +
                    "      <td align=\"center\">" + (termin.getAbends() > 0 ? termin.getAbends() : "--") + "</td>" +
                    "      <td align=\"center\">" + (termin.getNachtAb() > 0 ? termin.getNachtAb() : "--") + "</td>" +
                    "      <td>" + wdh + "</td>" +
                    "    </tr>";
        } else if (currentState == UHRZEIT) {
            if (headerNeeded) {
                result += "<table id=\"fonttext\" border=\"1\" >" +
                        "   <tr>" +
                        "      <th align=\"center\">" + SYSTools.xx("misc.msg.Time.long") + "</th>" +
                        "      <th align=\"center\">" + SYSTools.xx("misc.msg.Number") + "</th>" +
                        "      <th align=\"center\">" + SYSTools.xx("misc.msg.repeat.short") + "</th>" +
                        "   </tr>";
            }

            String wdh = getRepeatPattern(termin);
            result += "    <tr>" +
                    "      <td align=\"center\">" + DateFormat.getTimeInstance(DateFormat.SHORT).format(termin.getUhrzeit()) + " Uhr</td>" +
                    "      <td align=\"center\">" + termin.getUhrzeitAnzahl() + "</td>" +
                    "      <td>" + wdh + "</td>" +
                    "    </tr>";
        } else {
            result = "!!ERROR!!";
        }

        result += "</table>";

        result += SYSTools.catchNull(termin.getBemerkung(), "<div id=\"fonttext\"><b>" + SYSTools.xx("misc.msg.comment") + ": </b>", "</div><br/>&nbsp;");

        if (termin.isFloating()) {
            result += "<div id=\"fonttext\"><font color=\"blue\">" + SYSTools.xx("nursingrecords.nursingprocess.floatinginterventions") + "</font></div>";
        }

        return result;
    }

    public static String getRepeatPattern(InterventionSchedule schedule) {
        String result = "";

        if (schedule.isTaeglich()) {
            if (schedule.getTaeglich() > 1) {
                result += "alle " + schedule.getTaeglich() + " Tage";
            } else {
                result += "jeden Tag";
            }
        } else if (schedule.isWoechentlich()) {
            if (schedule.getWoechentlich() == 1) {
                result += "jede Woche ";
            } else {
                result += "alle " + schedule.getWoechentlich() + " Wochen ";
            }

            String daylist = "";

            daylist += (schedule.getMon() > 0 ? SYSTools.xx("misc.msg.monday").substring(0, 3) + ", " : "");
            daylist += (schedule.getDie() > 0 ? SYSTools.xx("misc.msg.tuesday").substring(0, 3) + ", " : "");
            daylist += (schedule.getMit() > 0 ? SYSTools.xx("misc.msg.wednesday").substring(0, 3) + ", " : "");
            daylist += (schedule.getDon() > 0 ? SYSTools.xx("misc.msg.thursday").substring(0, 3) + ", " : "");
            daylist += (schedule.getFre() > 0 ? SYSTools.xx("misc.msg.friday").substring(0, 3) + ", " : "");
            daylist += (schedule.getSam() > 0 ? SYSTools.xx("misc.msg.saturday").substring(0, 3) + ", " : "");
            daylist += (schedule.getSon() > 0 ? SYSTools.xx("misc.msg.sunday").substring(0, 3) + ", " : "");

            if (!daylist.isEmpty()) {
                result += "{" + daylist.substring(0, daylist.length() - 2) + "}";
            }

        } else if (schedule.isMonatlich()) {
            if (schedule.getMonatlich() == 1) {
                result += SYSTools.xx("misc.msg.everyMonth") + " ";
            } else {
                result += SYSTools.xx("misc.msg.every") + " " + schedule.getMonatlich() + " " + SYSTools.xx("misc.msg.months") + " ";
//                result += "alle " + schedule.getMonatlich() + " Monate ";
            }

            if (schedule.getTagNum() > 0) {
                result += SYSTools.xx("misc.msg.atchrono") + " " + schedule.getTagNum() + ". " + SYSTools.xx("misc.msg.ofTheMonth");
//                result += "jeweils am " + schedule.getTagNum() + ". des Monats";
            } else {
                int wtag = 0;
                String tag = "";
                tag += (schedule.getMon() > 0 ? SYSTools.xx("misc.msg.monday") : "");
                tag += (schedule.getDie() > 0 ? SYSTools.xx("misc.msg.tuesday") : "");
                tag += (schedule.getMit() > 0 ? SYSTools.xx("misc.msg.wednesday") : "");
                tag += (schedule.getDon() > 0 ? SYSTools.xx("misc.msg.thursday") : "");
                tag += (schedule.getFre() > 0 ? SYSTools.xx("misc.msg.friday") : "");
                tag += (schedule.getSam() > 0 ? SYSTools.xx("misc.msg.saturday") : "");
                tag += (schedule.getSon() > 0 ? SYSTools.xx("misc.msg.sunday") : "");

                // In this case, only one of the below can be >0. So this will work.
                wtag += schedule.getMon();
                wtag += schedule.getDie();
                wtag += schedule.getMit();
                wtag += schedule.getDon();
                wtag += schedule.getFre();
                wtag += schedule.getSam();
                wtag += schedule.getSon();

                result += SYSTools.xx("misc.msg.atchrono") + " " + wtag + ". " + tag + " " + SYSTools.xx("misc.msg.ofTheMonth");
            }
        } else {
            result = "";
        }

        DateMidnight ldatum = new DateTime(schedule.getLDatum()).toDateMidnight();
        DateMidnight today = new DateMidnight();

        if (ldatum.compareTo(today) > 0) { // Die erste Ausführung liegt in der Zukunft
            result += SYSTools.xx("nursingrecords.prescription.firstApplication") + ": " + DateFormat.getDateInstance().format(schedule.getLDatum());
        }

        return result;
    }

    public static ArrayList<InterventionSchedule> getAllActiveByFlag(Resident resident, int flag) {
        EntityManager em = OPDE.createEM();
        Query query = em.createQuery(" " +
                " SELECT i FROM InterventionSchedule i " +
                " WHERE i.nursingProcess.resident = :resident AND i.nursingProcess.to = :ufn AND i.intervention.flag = :flag ORDER BY i.intervention.bezeichnung ");
        query.setParameter("ufn", SYSConst.DATE_UNTIL_FURTHER_NOTICE);
        query.setParameter("resident", resident);
        query.setParameter("flag", flag);
        ArrayList<InterventionSchedule> listIS = new ArrayList<InterventionSchedule>(query.getResultList());
        em.close();
        return listIS;
    }

    public static String getTerminAsCompactText(InterventionSchedule schedule) {
        String result = "";

        final int ZEIT = 0;
        final int UHRZEIT = 1;
//            int previousState = -1;

        int currentState;
        // Zeit verwendet ?
        if (schedule.verwendetUhrzeit()) {
            currentState = UHRZEIT;
        } else {
            currentState = ZEIT;
        }

        if (currentState == ZEIT) {
            result += (schedule.getNachtMo() > 0 ? SYSTools.xx("misc.msg.earlyinthemorning.medium") + ", " : "") +
                    (schedule.getMorgens() > 0 ? SYSTools.xx("misc.msg.morning.medium") + ", " : "") +
                    (schedule.getMittags() > 0 ? SYSTools.xx("misc.msg.noon.medium") : "") + ", " +
                    (schedule.getNachmittags() > 0 ? SYSTools.xx("misc.msg.afternoon.medium") + ", " : "") +
                    (schedule.getAbends() > 0 ? SYSTools.xx("misc.msg.evening.medium") + ", " : "") +
                    (schedule.getNachtAb() > 0 ? SYSTools.xx("misc.msg.lateatnight.medium") + ", " : "");

            if (schedule.getTaeglich() != 1) {
                result += getRepeatPattern(schedule);
            } else {
                result = result.substring(0, result.length() - 2);
            }

//            result += (schedule.getTaeglich() != 1 ? getRepeatPattern(schedule) : "");
        } else if (currentState == UHRZEIT) {

            DateTime dt = new DateTime(schedule.getUhrzeit());

            if (dt.getMinuteOfHour() == 0) {
                result += dt.getHourOfDay() + "h";
            } else {
                result += DateFormat.getTimeInstance(DateFormat.SHORT).format(schedule.getUhrzeit()) + "h";
            }

        } else {
            result = "!!ERROR!!";
        }

        return result;
    }

    /**
     * this copies the sheduling part of the template Schedule to every InterventionSchedule of the provided nursing
     * process.
     *
     * @param source
     * @param nursingProcess
     */
    public static void copySchedule(InterventionSchedule source, List<InterventionSchedule> selected, NursingProcess nursingProcess) {
        for (InterventionSchedule is : selected) {
            int index = nursingProcess.getInterventionSchedule().indexOf(is);

            nursingProcess.getInterventionSchedule().get(index).setNachtMo(source.getNachtMo());
            nursingProcess.getInterventionSchedule().get(index).setMorgens(source.getMorgens());
            nursingProcess.getInterventionSchedule().get(index).setMittags(source.getMittags());
            nursingProcess.getInterventionSchedule().get(index).setNachmittags(source.getNachmittags());
            nursingProcess.getInterventionSchedule().get(index).setAbends(source.getAbends());
            nursingProcess.getInterventionSchedule().get(index).setNachtAb(source.getNachtAb());
            nursingProcess.getInterventionSchedule().get(index).setUhrzeitAnzahl(source.getUhrzeitAnzahl());
            nursingProcess.getInterventionSchedule().get(index).setUhrzeit(source.getUhrzeit());
            nursingProcess.getInterventionSchedule().get(index).setTaeglich(source.getTaeglich());
            nursingProcess.getInterventionSchedule().get(index).setWoechentlich(source.getWoechentlich());
            nursingProcess.getInterventionSchedule().get(index).setMonatlich(source.getMonatlich());
            nursingProcess.getInterventionSchedule().get(index).setTagNum(source.getTagNum());
            nursingProcess.getInterventionSchedule().get(index).setMon(source.getMon());
            nursingProcess.getInterventionSchedule().get(index).setDie(source.getDie());
            nursingProcess.getInterventionSchedule().get(index).setMit(source.getMit());
            nursingProcess.getInterventionSchedule().get(index).setDon(source.getDon());
            nursingProcess.getInterventionSchedule().get(index).setFre(source.getFre());
            nursingProcess.getInterventionSchedule().get(index).setSam(source.getSam());
            nursingProcess.getInterventionSchedule().get(index).setSon(source.getSon());
            nursingProcess.getInterventionSchedule().get(index).setFloating(source.isFloating());
            nursingProcess.getInterventionSchedule().get(index).setLDatum(source.getLDatum());
            nursingProcess.getInterventionSchedule().get(index).setBemerkung(source.getBemerkung());
        }
    }

}
