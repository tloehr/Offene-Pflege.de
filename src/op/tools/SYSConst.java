/*
 * OffenePflege
 * Copyright (C) 2008 Torsten Löhr
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License V2 as published by the Free Software Foundation
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even 
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to 
 * the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
 * www.offene-pflege.de
 * ------------------------ 
 * Auf deutsch (freie Übersetzung. Rechtlich gilt die englische Version)
 * Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU General Public License, 
 * wie von der Free Software Foundation veröffentlicht, weitergeben und/oder modifizieren, gemäß Version 2 der Lizenz.
 *
 * Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, daß es Ihnen von Nutzen sein wird, aber 
 * OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN 
 * BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht, 
 * schreiben Sie an die Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA.
 * 
 */
package op.tools;

import op.OPDE;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * @author tloehr
 */
public class SYSConst {

    public static final Font ARIAL10BOLD = new Font("Arial", Font.BOLD, 10);
    public static final Font ARIAL12BOLD = new Font("Arial", Font.BOLD, 12);
    public static final Font ARIAL14 = new Font("Arial", Font.PLAIN, 14);
    public static final Font ARIAL14BOLD = new Font("Arial", Font.BOLD, 14);
    public static final Font ARIAL20 = new Font("Arial", Font.PLAIN, 20);
    public static final Font ARIAL20BOLD = new Font("Arial", Font.BOLD, 20);
    public static final Font ARIAL28 = new Font("Arial", Font.PLAIN, 28);

    public static Color darkgreen = new Color(0x00, 0x76, 0x00);
    public static Color darkred = new Color(0xbd, 0x00, 0x00);
    public static Color gold7 = new Color(0xff, 0xaa, 0x00);
    public static Color darkorange = new Color(0xff, 0x8c, 0x00);
    public static Color khaki1 = new Color(0xFF, 0xF3, 0x80);
    public static Color khaki2 = new Color(0xed, 0xe2, 0x75);
    public static Color khaki3 = new Color(0xc9, 0xbe, 0x62);
    public static Color khaki4 = new Color(0x82, 0x78, 0x39);
    public static Color deepskyblue = new Color(0, 191, 255);
    public static Color bluegrey = new Color(230, 230, 255);
    public static Color lightblue = new Color(192, 217, 217);
    public static Color bermuda_sand = new Color(246, 201, 204);
    public static Color melonrindgreen = new Color(223, 255, 165);
    public static Color orangered = new Color(255, 36, 0);
    public static Color sun3 = new Color(153, 153, 204);
    public static String html_darkgreen = "color=\"#007600\"";
    public static String html_darkred = "color=\"#bd0000\"";
    public static String html_gold7 = "color=\"#ffaa00\"";
    public static String html_darkorange = "color=\"#ff8c00\"";
    public static String html_khaki1 = "color=\"#fffg8f\"";
    public static String html_silver = "color=\"#C0C0C0\"";
    public static String html_lightslategrey = "color=\"#778899\"";
    public static String html_grey80 = "color=\"#c7c7c5\"";
    public static String html_grey50 = "color=\"#747170\"";
    public static String html_cyan = "color=\"#00ffff\"";
    public static String html_mediumpurple3 = "color=\"#8968cd\"";
    public static String html_mediumorchid3 = "color=\"#b452cd\"";
    public static Color salmon1 = new Color(0xFF, 0x8C, 0x69);
    public static Color salmon2 = new Color(0xEE, 0x82, 0x62);
    public static Color salmon3 = new Color(0xCD, 0x70, 0x54);
    public static Color salmon4 = new Color(0x8B, 0x4C, 0x39);
    public static Color lightsteelblue1 = new Color(0xc6, 0xde, 0xff);
    public static Color lightsteelblue2 = new Color(188, 210, 238);
    public static Color lightsteelblue3 = new Color(0x9a, 0xad, 0xc7);
    public static Color lightsteelblue4 = new Color(0x6E, 0x7B, 0x8B);
    public static Color darkolivegreen1 = new Color(0xcc, 0xfb, 0x5d);
    public static Color darkolivegreen2 = new Color(0xBC, 0xEE, 0x68);
    public static Color darkolivegreen3 = new Color(0xa0, 0xc5, 0x44);
    public static Color darkolivegreen4 = new Color(0x6E, 0x8B, 0x3D);
    public static Color gold2 = new Color(0xEE, 0xC9, 0x00);
    public static Color gold4 = new Color(0x8B, 0x75, 0x00);
    public static Color mediumpurple1 = new Color(0xAB, 0x82, 0xFF);
    public static Color mediumpurple2 = new Color(0x9F, 0x79, 0xEE);
    public static Color mediumpurple3 = new Color(0x89, 0x68, 0xCD);
    public static Color mediumpurple4 = new Color(0x5D, 0x47, 0x8B);
    public static Color mediumorchid1 = new Color(0xE0, 0x66, 0xFF);
    public static Color mediumorchid3 = new Color(0xB4, 0x52, 0xCD);
    public static Color mediumorchid2 = new Color(0xC4, 0x5A, 0xEC);
    public static Color mediumorchid4 = new Color(0x6A, 0x28, 0x7E);
    public static Color thistle1 = new Color(0xfc, 0xdf, 0xFF);
    public static Color thistle2 = new Color(0xe9, 0xcf, 0xEC);
    public static Color thistle3 = new Color(0xc6, 0xae, 0xc7);
    public static Color thistle4 = new Color(0x80, 0x6d, 0x7E);
    public static Color yellow1 = new Color(0xFF, 0xFF, 0x00);
    public static Color yellow2 = new Color(0xEE, 0xEE, 0x00);
    public static Color yellow3 = new Color(0xCD, 0xCD, 0x00);
    public static Color yellow4 = new Color(0x8B, 0x8B, 0x00);
    public static Color grey80 = new Color(0xc7, 0xc7, 0xc5);
    public static Color grey50 = new Color(0x74, 0x71, 0x70);
    //Gray50  	747170
    public static char eurosymbol = '\u20AC';
    public static final GregorianCalendar VON_ANFANG_AN = new GregorianCalendar(1970, GregorianCalendar.JANUARY, 1, 0, 0, 0);
    public static final GregorianCalendar BIS_AUF_WEITERES = new GregorianCalendar(9999, GregorianCalendar.DECEMBER, 31, 23, 59, 59);
    public static final GregorianCalendar BIS_AUF_WEITERES_WO_TIME = new GregorianCalendar(9999, GregorianCalendar.DECEMBER, 31, 0, 0, 0);
    public static final Date DATE_VON_ANFANG_AN = new Date(VON_ANFANG_AN.getTimeInMillis());
    public static final Date DATE_BIS_AUF_WEITERES = new Date(BIS_AUF_WEITERES.getTimeInMillis());
    public static final Date DATE_BIS_AUF_WEITERES_WO_TIME = new Date(BIS_AUF_WEITERES_WO_TIME.getTimeInMillis());
    //    public static final Timestamp TS_VON_ANFANG_AN = new Timestamp(VON_ANFANG_AN.getTimeInMillis());
//    public static final Timestamp TS_BIS_AUF_WEITERES = new Timestamp(BIS_AUF_WEITERES.getTimeInMillis());
    public static final String MYSQL_DATETIME_VON_ANFANG_AN = "'1000-01-01 00:00:00'";
    public static final String MYSQL_DATETIME_BIS_AUF_WEITERES = "'9999-12-31 23:59:59'";
//    public static final int GESCHLECHT_MAENNLICH = 1;
//    public static final int GESCHLECHT_WEIBLICH = 2;

    public static final String EINHEIT[] = {"", OPDE.lang.getString("misc.msg.piece"), "ml", "l", "mg", "g", "cm", "m"}; // Für AnwEinheit, PackEinheit, Dimension

    public static final byte UZ = 0; // Solluhrzeit
    public static final byte FM = 1; // Nacht Morgens
    public static final byte MO = 2; // Morgens
    public static final byte MI = 3; // Mittags
    public static final byte NM = 4; // Nachmittags
    public static final byte AB = 5; // Abends
    public static final byte NA = 6; // Nacht Abends

    public static final int ZEIT_ALLES = -1;
    public static final int ZEIT_NACHT_MO = 0;
    public static final int ZEIT_FRUEH = 1;
    public static final int ZEIT_SPAET = 2;
    public static final int ZEIT_NACHT_AB = 3;
    public static final String ZEIT[] = {"Alles", "Nacht, morgens", "Früh", "Spät", "Nacht, abends"};



    public static final String html_arial14 = "face=\"" + ARIAL14.getFamily() + "\"";
    public static final String html_fontface = "<font " + html_arial14 + " >";

    public static final String html_div_open = "<div id=\"fonttext\">";
    public static final String html_div_close = "</div>";

    public static final String html_report_footer = "<hr/>" +
            html_fontface +
            "<b>" + OPDE.lang.getString("misc.msg.endofreport") + "</b><br/>" + (OPDE.getLogin() != null ? SYSTools.htmlUmlautConversion(OPDE.getLogin().getUser().getNameUndVorname()) : "")
            + "<br/>" + DateFormat.getDateTimeInstance().format(new Date())
            + "<br/>http://www.offene-pflege.de</font>\n";

    public static final int SCROLL_TIME_FAST = 500; // for the sliding splitpanes

    public static HashMap getNachnamenAnonym() {
        HashMap hm = new HashMap();
        hm.put("a", new String[]{"Anders", "Ackerman", "Acord", "Adams", "Addison"});
        hm.put("b", new String[]{"Baesman", "Bahden", "Bailie", "Bäke", "Baker"});
        hm.put("c", new String[]{"Clefisch", "Cleimann", "Clemann", "Clever", "Cleverdon"});
        hm.put("d", new String[]{"Dammann", "Dammer", "Dammermann", "Damschröder", "Dankel"});
        hm.put("e", new String[]{"Ellebracht", "Ellerbrock", "Ellerkamp", "Ellermann", "Ellinghaus"});
        hm.put("f", new String[]{"Fehring", "Feickert", "Feistkorn", "Feldhus", "Feldmann"});
        hm.put("g", new String[]{"Gaunert", "Gausebrink", "Gausmann", "Geck", "Gehl"});
        hm.put("h", new String[]{"Habighorst", "Hackemüller", "Hackemöller", "Hackmann", "Hackstedt"});
        hm.put("i", new String[]{"Imbusch", "Imeyer", "Imholz", "Irmer", "Irmscher"});
        hm.put("j", new String[]{"Jensen", "Jobstvogt", "Jobusch", "Joeckle", "Joesting"});
        hm.put("k", new String[]{"Kalmey", "Kalthof", "Kamlage", "Kammerer", "Kamp"});
        hm.put("l", new String[]{"Lohfener", "Löhr", "Lohrbach", "Lohse", "Long"});
        hm.put("m", new String[]{"Magna", "Mailänder", "Malasse", "Mandrella", "Mann"});
        hm.put("n", new String[]{"Nehring", "Nelson", "Nelz", "Nendel", "Nentrup"});
        hm.put("o", new String[]{"Obermeyer", "Obermüller", "Oberniehaus", "Ostmann", "Oberwahrenbrock"});
        hm.put("p", new String[]{"Papenburg", "Pardieck", "Parr", "Pörsch", "Partzsch"});
        hm.put("q", new String[]{"Quam", "Quark", "Quast", "Quest", "Quench"});
        hm.put("r", new String[]{"Ramms", "Randall", "Rappold", "Raschack", "Rathert"});
        hm.put("s", new String[]{"Sandkühler", "Sandner", "Sandy", "Sarner", "Sarvela"});
        hm.put("t", new String[]{"Tegeder", "Teigeler", "Tellmann", "Temme", "Tessmann"});
        hm.put("u", new String[]{"Ulbricht", "Ullmann", "Ullrich", "Unland", "Unnerstall"});
        hm.put("v", new String[]{"Vegesack", "Vehling", "Vehring", "Vemmer", "Venckhaus"});
        hm.put("w", new String[]{"Wanscheer", "Warber", "Ward", "Warnecke", "Warner"});
        hm.put("x", new String[]{"Xaver", "Xanderin", "Xanders", "Xandri", "Xanking"});
        hm.put("y", new String[]{"Yanker", "Yareck", "Yaritz", "Yark", "Yarletts"});
        hm.put("z", new String[]{"Zeiser", "Zeretzki", "Ziebart", "Ziegemeier", "Zieger"});
        hm.put("ä", new String[]{"Anders", "Ackerman", "Acord", "Adams", "Addison"});
        hm.put("ö", new String[]{"Obermeyer", "Obermüller", "Oberniehaus", "Ostmann", "Oberwahrenbrock"});
        hm.put("ü", new String[]{"Ulbricht", "Ullmann", "Ullrich", "Unland", "Unnerstall"});
        return hm;
    }

    public static HashMap getVornamenFrauAnonym() {
        HashMap hm = new HashMap();
        hm.put("a", new String[]{"Adrina", "Agnes", "Alexandra", "Alina", "Amelie"});
        hm.put("b", new String[]{"Barbara", "Beate", "Berit", "Berta", "Bettina"});
        hm.put("c", new String[]{"Celina", "Celine", "Charlotte", "Corinna", "Cornelia"});
        hm.put("d", new String[]{"Dagmar", "Daniela", "Daria", "Doreen", "Denise"});
        hm.put("e", new String[]{"Edith", "Eleonora", "Elfriede", "Erika", "Elisa"});
        hm.put("f", new String[]{"Fabienne", "Finja", "Fiona", "Franziska", "Frieda"});
        hm.put("g", new String[]{"Gabriele", "Galadriel", "Gerda", "Gertrud", "Gisela"});
        hm.put("h", new String[]{"Heike", "Helen", "Helena", "Helene", "Helga"});
        hm.put("i", new String[]{"Ilse", "Imke", "Inge", "Ingeborg", "Ingrid"});
        hm.put("j", new String[]{"Jacqueline", "Jana", "Janin", "Janina", "Jasmin"});
        hm.put("k", new String[]{"Krista", "Kristiane", "Kristin", "Kristina", "Klaudia"});
        hm.put("l", new String[]{"Lara", "Laura", "Lea", "Lena", "Leni"});
        hm.put("m", new String[]{"Mandy", "Manou", "Manuela", "Mareike", "Margarethe"});
        hm.put("n", new String[]{"Nadine", "Natalie", "Nele", "Natascha", "Nicole"});
        hm.put("o", new String[]{"Olga", "Oliana", "Olisa", "Olivia", "Ottilie"});
        hm.put("p", new String[]{"Paula", "Pauline", "Petra", "Pia", "Patrizia"});
        hm.put("q", new String[]{"Quella", "Quenby", "Querida", "Quilla", "Quintia"});
        hm.put("r", new String[]{"Ranya", "Rebekka", "Regina", "Renate", "Ronja"});
        hm.put("s", new String[]{"Sabine", "Sabrina", "Sandra", "Sara", "Sascha"});
        hm.put("t", new String[]{"Tania", "Tara", "Thea", "Tiana", "Tiffany"});
        hm.put("u", new String[]{"Ute", "Ursula", "Uschi", "Ulrike", "Ursa"});
        hm.put("v", new String[]{"Vanessa", "Vera", "Viktoria", "Veronika", "Verena"});
        hm.put("w", new String[]{"Wally", "Walli", "Waltheide", "Waltraud", "Wanda"});
        hm.put("x", new String[]{"Xandra", "Xaveria", "Xynthia", "Xaviera", "Xenia"});
        hm.put("y", new String[]{"Yvonne", "Yasmin", "Yana", "Yola", "Yuki"});
        hm.put("z", new String[]{"Zatiye", "Zäzilie", "Zdenka", "Zelda", "Zia"});
        hm.put("ä", new String[]{"Adrina", "Agnes", "Alexandra", "Alina", "Amelie"});
        hm.put("ö", new String[]{"Olga", "Oliana", "Olisa", "Olivia", "Ottilie"});
        hm.put("ü", new String[]{"Ute", "Ursula", "Uschi", "Ulrike", "Ursa"});
        return hm;
    }

    public static HashMap getVornamenMannAnonym() {
        HashMap hm = new HashMap();
        hm.put("a", new String[]{"Aaron", "Adrian", "Albert", "Alexander", "Alfred"});
        hm.put("b", new String[]{"Bastian", "Bela", "Ben", "Benjamin", "Bernd"});
        hm.put("c", new String[]{"Christian", "Christoph", "Christopher", "Claus", "Carl"});
        hm.put("d", new String[]{"Daniel", "David", "Dennis", "Detlef", "Dieter"});
        hm.put("e", new String[]{"Elias", "Emil", "Erik", "Ernst", "Erich"});
        hm.put("f", new String[]{"Fabian", "Felix", "Ferdinand", "Florian", "Frank"});
        hm.put("g", new String[]{"Georg", "Gerd", "Gerhard", "Gustav", "Günther"});
        hm.put("h", new String[]{"Hagen", "Hannes", "Hartmut", "Hans", "Harald"});
        hm.put("i", new String[]{"Ingo", "Ingobald", "Ingolf", "Ingram", "Ingwar"});
        hm.put("j", new String[]{"Jakob", "Jan", "Jens", "Joachim", "Josef"});
        hm.put("k", new String[]{"Kristian", "Kristoph", "Kristopher", "Klaus", "Karl"});
        hm.put("l", new String[]{"Lars", "Lasse", "Leo", "Leon", "Ludwig"});
        hm.put("m", new String[]{"Manfred", "Manuel", "Marcel", "Markus", "Mario"});
        hm.put("n", new String[]{"Nevio", "Nick", "Nico", "Niklas", "Norbert"});
        hm.put("o", new String[]{"Olaf", "Ole", "Oliver", "Oskar", "Otto"});
        hm.put("p", new String[]{"Pascal", "Patrick", "Paul", "Peter", "Philipp"});
        hm.put("q", new String[]{"Quentin", "Quico", "Quillan", "Quinlan", "Quinn"});
        hm.put("r", new String[]{"Ralf", "René", "Richard", "Robert", "Rudolf"});
        hm.put("s", new String[]{"Sebastian", "Simon", "Stefan", "Steffen", "Sven"});
        hm.put("t", new String[]{"Thomas", "Timotheus", "Tobias", "Torsten", "Tarek"});
        hm.put("u", new String[]{"Uberto", "Ulrich", "Uwe", "Udo", "Ulf"});
        hm.put("v", new String[]{"Vaclav", "Vincent", "Volker", "Vico", "Valentin"});
        hm.put("w", new String[]{"Walter", "Werner", "Wilhelm", "Willi", "Wolfgang"});
        hm.put("x", new String[]{"Xaver", "Xander", "Xaverius", "Xavier", "Xerxes"});
        hm.put("y", new String[]{"Yanick", "Yann", "Yannis", "Yash", "Yashodhan"});
        hm.put("z", new String[]{"Zacharias", "Zaki", "Zafer", "Zadok", "Zenobius"});
        hm.put("ä", new String[]{"Aaron", "Adrian", "Albert", "Alexander", "Alfred"});
        hm.put("ö", new String[]{"Olaf", "Ole", "Oliver", "Oskar", "Otto"});
        hm.put("ü", new String[]{"Uberto", "Ulrich", "Uwe", "Udo", "Ulf"});
        return hm;
    }

    public static final Icon icon22apply = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/apply.png"));
    public static final Icon icon22applyPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/apply_pressed.png"));
    public static final Icon icon22cancel = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/cancel.png"));
    public static final Icon icon22cancelPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/cancel_pressed.png"));
    public static final Icon icon22empty = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/empty.png"));
    public static final Icon icon22emptyPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/empty_pressed.png"));
    public static final Icon icon16redStar = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/redstar.png"));
    public static final Icon icon22add = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/add.png"));
    public static final Icon icon22addPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/add-pressed.png"));
    public static final Icon icon22attach = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/attach.png"));
    public static final Icon icon22attachPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/attach_pressed.png"));
    public static final Icon icon22edit = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/kspread.png"));
    public static final Icon icon22editPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/kspread_pressed.png"));
    public static final Icon icon22gotoEnd = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/player_end.png"));
    public static final Icon icon22gotoEndPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/player_end_pressed.png"));
    public static final Icon icon22stop = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/player_stop.png"));
    public static final Icon icon22stopPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/player_stop_pressed.png"));
    public static final Icon icon22redo = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/redo.png"));
    public static final Icon icon22redoPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/redo_pressed.png"));
    public static final Icon icon22ambulance2 = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/ambulance2.png"));
    public static final Icon icon22ambulance2Pressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/ambulance2_pressed.png"));
    public static final Icon icon48stop = new ImageIcon(SYSConst.class.getResource("/artwork/48x48/bw/player_stop.png"));
    public static final Icon icon22delete = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/editdelete.png"));
    public static final Icon icon22deletePressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/editdelete_pressed.png"));
    public static final Icon icon48delete = new ImageIcon(SYSConst.class.getResource("/artwork/48x48/bw/editdelete.png"));
    public static final Icon icon22view = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/viewmag.png"));
    public static final Icon icon22viewPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/viewmag-selected.png"));
    public static final Icon icon22changePeriod = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/reload_page.png"));
    public static final Icon icon22changePeriodPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/reload_page_pressed.png"));
    public static final Icon icon16bysecond = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/bw/bysecond.png"));
    public static final Icon icon16byday = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/bw/byday.png"));
    public static final Icon icon16pit = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/bw/pointintime.png"));
    public static final Icon icon22clock = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/clock.png"));
    public static final Icon icon22clockPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/clock_pressed.png"));
    public static final Icon icon22addbw = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/add_bw.png"));
    public static final Icon icon22infoblue= new ImageIcon(SYSConst.class.getResource("/artwork/22x22/infoblue.png"));
    public static final Icon icon22infored= new ImageIcon(SYSConst.class.getResource("/artwork/22x22/infored.png"));
    public static final Icon icon22infogreen= new ImageIcon(SYSConst.class.getResource("/artwork/22x22/infogreen.png"));
    public static final Icon icon22infoyellow= new ImageIcon(SYSConst.class.getResource("/artwork/22x22/infoyellow.png"));
}
