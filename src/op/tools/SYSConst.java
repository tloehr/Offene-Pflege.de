/*
 * OffenePflege
 * Copyright (C) 2006-2012 Torsten Löhr
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

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;
import op.OPDE;
import op.system.AppInfo;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * @author tloehr
 */
public class SYSConst {

    public static final String sep = System.getProperty("file.separator");

    public static final Font ARIAL10BOLD = new Font("Arial", Font.BOLD, 10);
    public static final Font ARIAL10 = new Font("Arial", Font.PLAIN, 10);
    public static final Font ARIAL12BOLD = new Font("Arial", Font.BOLD, 12);
    public static final Font ARIAL12 = new Font("Arial", Font.PLAIN, 12);
    public static final Font ARIAL14 = new Font("Arial", Font.PLAIN, 14);
    public static final Font ARIAL14BOLD = new Font("Arial", Font.BOLD, 14);
    public static final Font ARIAL14ITALIC = new Font("Arial", Font.ITALIC, 14);
    public static final Font ARIAL16 = new Font("Arial", Font.PLAIN, 16);
    public static final Font ARIAL16BOLD = new Font("Arial", Font.BOLD, 14);
    public static final Font ARIAL20 = new Font("Arial", Font.PLAIN, 20);
    public static final Font ARIAL18 = new Font("Arial", Font.PLAIN, 18);
    public static final Font ARIAL18BOLD = new Font("Arial", Font.BOLD, 18);
    public static final Font ARIAL24BOLD = new Font("Arial", Font.BOLD, 24);
    public static final Font ARIAL24 = new Font("Arial", Font.PLAIN, 24);
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

    public static String html_22x22_StopSign = "<img src=\"" + SYSTools.replace(OPDE.getOPWD(), "\\", "/", true) + "/" + AppInfo.dirArtwork + "/" + AppInfo.dirArtwork22 + "/stop.png\" border=\"0\">";
    public static String html_22x22_Eraser = "<img src=\"" + SYSTools.replace(OPDE.getOPWD(), "\\", "/", true) + "/" + AppInfo.dirArtwork + "/" + AppInfo.dirArtwork22 + "/eraser.png\" border=\"0\">";
    public static String html_22x22_Edited = "<img src=\"" + SYSTools.replace(OPDE.getOPWD(), "\\", "/", true) + "/" + AppInfo.dirArtwork + "/" + AppInfo.dirArtwork22 + "/edited.png\" border=\"0\">";
    public static String html_48x48_biohazard = "<img src=\"" + SYSTools.replace(OPDE.getOPWD(), "\\", "/", true) + "/" + AppInfo.dirArtwork + "/" + AppInfo.dirArtwork48 + "/biohazard.png\" border=\"0\">";
    public static String html_48x48_warning = "<img src=\"" + SYSTools.replace(OPDE.getOPWD(), "\\", "/", true) + "/" + AppInfo.dirArtwork + "/" + AppInfo.dirArtwork48 + "/warning.png\" border=\"0\">";
    public static String html_48x48_diabetes = "<img src=\"" + SYSTools.replace(OPDE.getOPWD(), "\\", "/", true) + "/" + AppInfo.dirArtwork + "/" + AppInfo.dirArtwork48 + "/diabetes.png\" border=\"0\">";
    public static String html_48x48_allergy = "<img src=\"" + SYSTools.replace(OPDE.getOPWD(), "\\", "/", true) + "/" + AppInfo.dirArtwork + "/" + AppInfo.dirArtwork48 + "/allergy.png\" border=\"0\">";
    public static String html_22x22_tagPurple = "<img src=\"" + SYSTools.replace(OPDE.getOPWD(), "\\", "/", true) + "/" + AppInfo.dirArtwork + "/" + AppInfo.dirArtwork22 + "/tag_purple.png\" border=\"0\">";
    public static String html_16x16_Eraser = "<img src=\"" + SYSTools.replace(OPDE.getOPWD(), "\\", "/", true) + "/" + AppInfo.dirArtwork + "/" + AppInfo.dirArtwork16 + "/eraser.png\" border=\"0\">";
    public static String html_16x16_Edited = "<img src=\"" + SYSTools.replace(OPDE.getOPWD(), "\\", "/", true) + "/" + AppInfo.dirArtwork + "/" + AppInfo.dirArtwork16 + "/edited.png\" border=\"0\">";
    public static String html_16x16_tagPurple = "<img src=\"" + SYSTools.replace(OPDE.getOPWD(), "\\", "/", true) + "/" + AppInfo.dirArtwork + "/" + AppInfo.dirArtwork16 + "/tag_purple.png\" border=\"0\">";
    public static String html_16x16_apply = "<img src=\"" + SYSTools.replace(OPDE.getOPWD(), "\\", "/", true) + "/" + AppInfo.dirArtwork + "/" + AppInfo.dirArtwork16 + "/apply.png\" border=\"0\">";
    public static String html_16x16_cancel = "<img src=\"" + SYSTools.replace(OPDE.getOPWD(), "\\", "/", true) + "/" + AppInfo.dirArtwork + "/" + AppInfo.dirArtwork16 + "/cancel.png\" border=\"0\">";
    public static String html_16x16_empty = "<img src=\"" + SYSTools.replace(OPDE.getOPWD(), "\\", "/", true) + "/" + AppInfo.dirArtwork + "/" + AppInfo.dirArtwork16 + "/empty.png\" border=\"0\">";

    public static String html_16x16_tagPurple_internal = "<img src=\"" + SYSConst.class.getResource("/artwork/16x16/tag_purple.png").toString() + "\" border=\"0\">";
    public static String html_16x16_Eraser_internal = "<img src=\"" + SYSConst.class.getResource("/artwork/16x16/eraser.png").toString() + "\" border=\"0\">";
    public static String html_16x16_Edited_internal = "<img src=\"" + SYSConst.class.getResource("/artwork/16x16/edited.png").toString() + "\" border=\"0\">";

    public static Image getPDF_16x16_tagPurple() throws BadElementException, IOException {
        return Image.getInstance(OPDE.getOPWD() + sep + AppInfo.dirArtwork + sep + AppInfo.dirArtwork16 + sep + "tag_purple.png");
    }

    String p = getClass().getResource("artwork/16x16/tag_purple.png").toString();

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
    public static Color yellow2 = new Color(0xEE, 0xEE, 0x00);
    public static Color yellow3 = new Color(0xCD, 0xCD, 0x00);
    public static Color yellow4 = new Color(0x8B, 0x8B, 0x00);
    public static Color grey80 = new Color(0xc7, 0xc7, 0xc5);
    public static Color grey50 = new Color(0x74, 0x71, 0x70);


    public static final int dark4 = 0;
    public static final int dark3 = 1;
    public static final int dark2 = 2;
    public static final int dark1 = 3;
    public static final int medium4 = 4;
    public static final int medium3 = 5;
    public static final int medium2 = 6;
    public static final int medium1 = 7;
    public static final int light4 = 8;
    public static final int light3 = 9;
    public static final int light2 = 10;
    public static final int light1 = 11;

    public static Color[] purple1 = new Color[]{
            GUITools.getColor("800080"), GUITools.getColor("BF00BF"),
            GUITools.getColor("DB00DB"), GUITools.getColor("F900F9"),
            GUITools.getColor("FF4AFF"), GUITools.getColor("FF86FF"),
            GUITools.getColor("FFA4FF"), GUITools.getColor("FFBBFF"),
            GUITools.getColor("FFCEFF"), GUITools.getColor("FFDFFF"),
            GUITools.getColor("FFECFF"), GUITools.getColor("FFF9FF")
    };

    public static Color[] greyscale = new Color[]{
            GUITools.getColor("2E2E2E"), GUITools.getColor("424242"),
            GUITools.getColor("585858"), GUITools.getColor("6E6E6E"),
            GUITools.getColor("848484"), GUITools.getColor("A4A4A4"),
            GUITools.getColor("BDBDBD"), GUITools.getColor("D8D8D8"),
            GUITools.getColor("E6E6E6"), GUITools.getColor("F2F2F2"),
            GUITools.getColor("FAFAFA"), GUITools.getColor("FFFFFF")
    };

    public static Color[] yellow1 = new Color[]{
            GUITools.getColor("C8B400"), GUITools.getColor("D9C400"),
            GUITools.getColor("E6CE00"), GUITools.getColor("F7DE00"),
            GUITools.getColor("FFE920"), GUITools.getColor("FFF06A"),
            GUITools.getColor("FFF284"), GUITools.getColor("FFF7B7"),
            GUITools.getColor("FFF9CE"), GUITools.getColor("FFFBDF"),
            GUITools.getColor("FFFEF7"), GUITools.getColor("FFFFFF")
    };

    public static Color[] blue1 = new Color[]{
            GUITools.getColor("3923D6"), GUITools.getColor("6755E3"),
            GUITools.getColor("8678E9"), GUITools.getColor("9588EC"),
            GUITools.getColor("A095EE"), GUITools.getColor("B0A7F1"),
            GUITools.getColor("BCB4F3"), GUITools.getColor("CBC5F5"),
            GUITools.getColor("D7D1F8"), GUITools.getColor("E3E0FA"),
            GUITools.getColor("EFEDFC"), GUITools.getColor("F7F5FE")
    };

    public static Color[] red1 = new Color[]{
            GUITools.getColor("F70000"), GUITools.getColor("FF2626"),
            GUITools.getColor("FF5353"), GUITools.getColor("FF7373"),
            GUITools.getColor("FF8E8E"), GUITools.getColor("FFA4A4"),
            GUITools.getColor("FFB5B5"), GUITools.getColor("FFC8C8"),
            GUITools.getColor("FFEAEA"), GUITools.getColor("FFEAEA"),
            GUITools.getColor("FFFDFD"), GUITools.getColor("FFFDFD")
    };

    public static Color[] red2 = new Color[]{
            GUITools.getColor("B9264F"), GUITools.getColor("D73E68"),
            GUITools.getColor("DD597D"), GUITools.getColor("E37795"),
            GUITools.getColor("E994AB"), GUITools.getColor("EDA9BC"),
            GUITools.getColor("F0B9C8"), GUITools.getColor("F4CAD6"),
            GUITools.getColor("F8DAE2"), GUITools.getColor("FAE7EC"),
            GUITools.getColor("FEFAFB"), GUITools.getColor("FEFAFB")
    };

    public static Color[] orange1 = new Color[]{
            GUITools.getColor("FF800D"), GUITools.getColor("FF9C42"),
            GUITools.getColor("FFAC62"), GUITools.getColor("FFBD82"),
            GUITools.getColor("FFC895"), GUITools.getColor("FFCEA2"),
            GUITools.getColor("FFD7B3"), GUITools.getColor("FFE2C8"),
            GUITools.getColor("FFE6D0"), GUITools.getColor("FFF1E6"),
            GUITools.getColor("FFF9F4"), GUITools.getColor("FFF9F4")
    };

    public static Color[] green1 = new Color[]{
            GUITools.getColor("1FCB4A"), GUITools.getColor("27DE55"),
            GUITools.getColor("4AE371"), GUITools.getColor("7CEB98"),
            GUITools.getColor("93EEAA"), GUITools.getColor("A4F0B7"),
            GUITools.getColor("BDF4CB"), GUITools.getColor("D6F8DE"),
            GUITools.getColor("E3FBE9"), GUITools.getColor("E3FBE9"),
            GUITools.getColor("FAFEFB"), GUITools.getColor("FFFFFF")
    };

    public static Color[] green2 = new Color[]{
            GUITools.getColor("4A9586"), GUITools.getColor("5EAE9E"),
            GUITools.getColor("74BAAC"), GUITools.getColor("8DC7BB"),
            GUITools.getColor("A5D3CA"), GUITools.getColor("C0E0DA"),
            GUITools.getColor("CFE7E2"), GUITools.getColor("DCEDEA"),
            GUITools.getColor("E7F3F1"), GUITools.getColor("F2F9F8"),
            GUITools.getColor("F7FBFA"), GUITools.getColor("FFFFFF")
    };

    public static Color colorWeekday = GUITools.getColor("FFE6D0");
    public static Color colorWeekend = GUITools.getColor("FFC895");
    public static Color colorHolliday = GUITools.getColor("FF800D");

    public static char eurosymbol = '\u20AC';
    public static final GregorianCalendar VERY_BEGINNING = new GregorianCalendar(1970, GregorianCalendar.JANUARY, 1, 0, 0, 0);
    public static final GregorianCalendar UNTIL_FURTHER_NOTICE = new GregorianCalendar(9999, GregorianCalendar.DECEMBER, 31, 23, 59, 59);
    public static final Date DATE_THE_VERY_BEGINNING = new Date(VERY_BEGINNING.getTimeInMillis());
    public static final Date DATE_UNTIL_FURTHER_NOTICE = new Date(UNTIL_FURTHER_NOTICE.getTimeInMillis());

    public static final String UNITS[] = {"", SYSTools.xx("misc.msg.piece"), "ml", "l", "mg", "g", "cm", "m"};

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

    public static final String html_arial14 = "face=\"" + ARIAL14.getFamily() + "\"";
    public static final String html_fontface = "<font " + html_arial14 + " >";
    public static final String html_h1_open = "<h1 id=\"fonth1\" >";
    public static final String html_h1_close = "</h1>";
    public static final String html_h2_open = "<h2 id=\"fonth2\" >";
    public static final String html_h2_close = "</h2>";
    public static final String html_h3_open = "<h3 id=\"fonth3\" >";
    public static final String html_h3_close = "</h3>";

    public static String html_ul(String content) {
        return "<ul id=\"fonttext\">\n" + SYSTools.xx(content) + "</ul>\n";
    }

    public static String html_ol(String content) {
        return "<ol id=\"fonttext\">\n" + SYSTools.xx(content) + "</ol>\n";
    }

    public static String html_li(String content) {
        return "<li>" + SYSTools.xx(content) + "</li>\n";
    }

    public static String html_table_th(String content, String align) {
        return "<th " + SYSTools.catchNull(align, "align=\"", "\"") + ">" + SYSTools.xx(content) + "</th>\n";
    }

    public static String html_table_th(String content) {
        return "<th>" + SYSTools.xx(content) + "</th>\n";
    }

    public static String html_table_td(String content, String align) {
        return "<td " + SYSTools.catchNull(align, "align=\"", "\"") + ">" + SYSTools.xx(content) + "</td>\n";
    }

    public static String html_table_td(String content, String align, String valign) {
        return "<td " + SYSTools.catchNull(align, "align=\"", "\"") + " " + SYSTools.catchNull(valign, "valign=\"", "\"") + ">" + SYSTools.xx(content) + "</td>\n";
    }

    public static String html_table_td(String content) {
        return html_table_td(content, null);
    }

    public static String html_table_td(String content, boolean bold) {
        return html_table_td((bold ? "<b>" : "") + content + (bold ? "</b>" : ""), null);
    }

    public static String html_table_tr(String content) {
        return "<tr>" + SYSTools.xx(content) + "</tr>\n";
    }

    public static String html_table_tr(String content, boolean highlight) {
        return "<tr " + (highlight ? "id=\"fonttextgray\"" : "") + ">" + SYSTools.xx(content) + "</tr>\n";
    }

    public static String html_bold(String content) {
        return "<b>" + SYSTools.xx(content) + "</b>";
    }

    public static String html_italic(String content) {
        return "<i>" + SYSTools.xx(content) + "</i>";
    }

    public static String html_paragraph(String content) {
        return "<p id=\"fonttext\">\n" + SYSTools.xx(content) + "</p>\n";
    }

    public static String html_div(String content) {
        return "<div id=\"fonttext\">\n" + SYSTools.xx(content) + "</div>\n";
    }

    public static String html_h1(String content) {
        return "<h1 id=\"fonth1\" >" + SYSTools.xx(content) + "</h1>\n";
    }

    public static String html_h2(String content) {
        return "<h2 id=\"fonth2\" >" + SYSTools.xx(content) + "</h2>\n";
    }

    public static String html_h3(String content) {
        return "<h3 id=\"fonth3\" >" + SYSTools.xx(content) + "</h3>\n";
    }


    public static String html_h4(String content) {
        return "<h4 id=\"fontsmall\">" + SYSTools.xx(content) + "</h4>\n";
    }

    public static String html_table(String content, String border) {
        return "<table id=\"fonttext\" border=\"" + border + "\">" + SYSTools.xx(content) + "</table>\n";
    }

    public static final String html_div_open = "<div id=\"fonttext\">";
    public static final String html_div_close = "</div>";

    public static final String html_color(Color color, String in) {
        return "<font color=#" + GUITools.toHexString(color) + ">" + in + "</font>";
    }

//    public static final String html_report_footer = "<hr/>" +
//            html_fontface +
//            "<b>" + SYSTools.xx("misc.msg.endofreport") + "</b><br/>" + (OPDE.getLogin() != null ? SYSTools.htmlUmlautConversion(OPDE.getLogin().getUser().getFullname()) : "")
//            + "<br/>" + DateFormat.getDateTimeInstance().format(new Date())
//            + "<br/>http://www.offene-pflege.de</font>\n";

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

    //    public static final Icon icon16bysecond = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/bw/bysecond.png"));
    public static final Icon icon16exec = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/exec.png"));
    //    public static final Icon icon16pit = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/bw/pointintime.png"));
    public static final Icon icon16redStar = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/redstar.png"));
    public static final Icon icon16greenStar = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/greenstar.png"));
    public static final Icon icon22redStar = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/redstar.png"));
    public static final Icon icon22greenStar = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/greenstar.png"));
    public static final Icon icon16unlink = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/unlink.png"));
    public static final Icon icon16delete = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/deleteall.png"));
    public static final Icon icon16unlinkPressed = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/unlink_pressed.png"));
    public static final Icon icon22add = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/add.png"));
    public static final Icon icon22addGroup = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/add_group.png"));
    public static final Icon icon22addPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/add-pressed.png"));
    public static final Icon icon22addUser = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/add_user.png"));
    public static final Icon icon22addbw = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/add_bw.png"));
    public static final Icon icon16ambulance = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/ambulance2.png"));
    public static final Icon icon22ambulance = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/ambulance2.png"));
    public static final Icon icon22ambulance2Pressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/ambulance2_pressed.png"));
    public static final Icon icon22apply = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/apply.png"));
    public static final Icon icon16cancel = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/cancel.png"));
    public static final Icon icon16empty = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/empty.png"));
    public static final Icon icon16apply = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/apply.png"));
    public static final Icon icon16info = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/info.png"));
    public static final Icon icon16internet = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/internet.png"));
    public static final Icon icon16infoPressed = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/info-pressed.png"));
    public static final Icon icon22applyPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/apply_pressed.png"));
    public static final Icon icon22attach = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/attach.png"));
    public static final Icon icon16attach = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/attach.png"));
    public static final Icon icon22attachPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/attach_pressed.png"));
    public static final Icon icon22calendar = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/calenders.png"));
    public static final Icon icon22calendarPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/calenders_pressed.png"));
    public static final Icon icon22cancel = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/cancel.png"));
    public static final Icon icon22cancelPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/cancel_pressed.png"));
    public static final Icon icon22changePeriod = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/reload_page.png"));
    public static final Icon icon22changePeriodPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/reload_page_pressed.png"));
    public static final Icon icon22clock = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/clock.png"));
    public static final Icon icon22clock1 = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/clock.png"));
    public static final Icon icon22clockPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/pressed.png"));
    public static final Icon icon22delete = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/editdelete.png"));
    public static final Icon icon22deletePressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/editdelete_pressed.png"));
    public static final Icon icon22deleteall = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/deleteall.png"));
    public static final Icon icon22deleteallPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/deleteall_pressed.png"));
    public static final Icon icon22down = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/1downarrow.png"));
    public static final Icon icon22edit = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/edit3.png"));
    public static final Icon icon22edit3 = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/edit3.png"));
    public static final Icon icon22menu = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/menu.png"));
    public static final Icon icon32menu = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/bw/menu.png"));
    public static final Icon icon32Pressed = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/bw/pressed.png"));
    public static final Icon icon22Pressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/pressed.png"));
    public static final Icon icon16Pressed = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/pressed.png"));
    public static final Icon icon22edit3Pressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/edit3_invert.png"));
    //    public static final Icon icon22edit1 = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/edit1.png"));
//    public static final Icon icon22edit1Pressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/edit1_pressed.png"));
    public static final Icon icon22editPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/kspread_pressed.png"));
    public static final Icon icon22empty = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/empty.png"));
    public static final Icon icon22eraser = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/eraser.png"));
    public static final Icon icon22emptyPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/empty_pressed.png"));
    public static final Icon icon22give = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/hand-over.png"));
    public static final Icon icon22givePressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/hand-over_pressed.png"));
    public static final Icon icon22gotoEnd = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/player_end.png"));
    public static final Icon icon22gotoEndPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/player_end_pressed.png"));
    public static final Icon icon22info = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/info.png"));
    public static final Icon icon22infogreen2 = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/infogreen2.png"));
    public static final Icon icon22infoPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/info_pressed.png"));
    public static final Icon icon22infogray = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/infogray.png"));
    public static final Icon icon22infoblue = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/infoblue.png"));
    public static final Icon icon22infogreen = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/infogreen.png"));
    public static final Icon icon22infored = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/infored.png"));
    public static final Icon icon22infoyellow = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/infoyellow.png"));
    public static final Icon icon22link = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/link.png"));
    public static final Icon icon22linkPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/link_pressed.png"));
    public static final Icon icon22myself = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/me.png"));
    public static final Icon icon22password = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/password.png"));
    public static final Icon icon22passwordPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/password_pressed.png"));
    public static final Icon icon22playerPlay = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/player_play.png"));
    public static final Icon icon22playerPlayPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/player_play_invert.png"));
    public static final Icon icon22playerStart = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/player_start.png"));
    public static final Icon icon22playerStartPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/player_start_pressed.png"));
    public static final Icon icon22playerStop = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/player_stop.png"));
    public static final Icon icon22playerStopPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/player_stop_pressed.png"));
    public static final Icon icon16date = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/date.png"));
    //    public static final Icon icon22printPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/printer1_pressed.png"));
    public static final Icon icon22print2 = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/printer2.png"));
    public static final Icon icon22print2Pressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/printer2_pressed.png"));
    public static final Icon icon22redo = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/redo.png"));
    public static final Icon icon22calc = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/kcalc.png"));
    public static final Icon icon22redoPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/redo_pressed.png"));
    public static final Icon icon22residentAbsent = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/resident-absent.png"));
    public static final Icon icon22residentActive = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/resident-active.png"));
    public static final Icon icon22residentBack = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/resident-back.png"));
    public static final Icon icon22residentBoth = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/resident-both.png"));
    public static final Icon icon22residentDied = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/resident-died.png"));
    public static final Icon icon22residentGone = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/resident-gone.png"));

    public static final Icon icon16residentAbsent = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/resident-absent.png"));
    public static final Icon icon16residentDied = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/resident-died.png"));
    public static final Icon icon16residentGone = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/resident-gone.png"));

    public static final Icon icon22residentInactive = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/resident-inactive.png"));
    public static final Icon icon22stop = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/player_stop.png"));
    public static final Icon icon22stopPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/player_stop_pressed.png"));
    public static final Icon icon22take = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/take-over.png"));
    public static final Icon icon22takePressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/take-over_pressed.png"));
    public static final Icon icon22todo = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/korganizer_todo.png"));
    public static final Icon icon22todoPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/korganizer_todo_pressed.png"));
    public static final Icon icon22checkbox = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/checkbox.png"));
    public static final Icon icon22checkboxPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/checkbox_pressed.png"));
    public static final Icon icon22undo = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/undo.png"));
    public static final Icon icon22unlink = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/unlink.png"));
    public static final Icon icon22edited = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/edited.png"));
    public static final Icon icon22unlinkPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/unlink_pressed.png"));
    public static final Icon icon22up = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/1uparrow.png"));
    //    public static final Icon icon22view = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/viewmag.png"));
//    public static final Icon icon22viewPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/viewmag-selected.png"));
    public static final Icon icon32ambulance2 = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/ambulance2.png"));
    public static final Icon icon32ambulance2Pressed = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/ambulance2_pressed.png"));
    public static final Icon icon48ambulance2 = new ImageIcon(SYSConst.class.getResource("/artwork/48x48/ambulance2.png"));
    public static final Icon icon48ambulance2Pressed = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/ambulance2_pressed.png"));
    public static final Icon icon48delete = new ImageIcon(SYSConst.class.getResource("/artwork/48x48/bw/editdelete.png"));
    public static final Icon icon48give = new ImageIcon(SYSConst.class.getResource("/artwork/48x48/hand-over.png"));
    public static final Icon icon48kgetdock = new ImageIcon(SYSConst.class.getResource("/artwork/48x48/kget_dock.png"));
    public static final Icon icon48play = new ImageIcon(SYSConst.class.getResource("/artwork/48x48/bw/player_play.png"));
    public static final Icon icon48undo = new ImageIcon(SYSConst.class.getResource("/artwork/48x48/bw/undo.png"));
    public static final Icon icon48playerStop = new ImageIcon(SYSConst.class.getResource("/artwork/48x48/bw/player_stop.png"));
    public static final Icon icon48take = new ImageIcon(SYSConst.class.getResource("/artwork/48x48/take-over.png"));
    public static final Icon icon48userconfig = new ImageIcon(SYSConst.class.getResource("/artwork/48x48/userconfig.png"));
    public static final Icon icon48systemconfig = new ImageIcon(SYSConst.class.getResource("/artwork/48x48/systemconfig.png"));
    //    public static final Icon icon16byday = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/bw/byday.png"));
    public static final Icon icon22collapse = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/collapse.png"));
    public static final Icon icon22expand = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/expand.png"));
    public static final Icon icon22pdca = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/pdca.png"));
    public static final Icon icon22RedFlag = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/flag.png"));
    public static final Icon icon22ledRedOn = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/ledred.png"));
    public static final Icon icon32ledRedOn = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/ledred.png"));
    public static final Icon icon32ledGrey = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/ledgrey.png"));
    public static final Icon icon22ledRedOff = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/leddarkred.png"));
    public static final Icon icon32ledRedOff = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/leddarkred.png"));
    public static final Icon icon22ledYellowOn = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/ledyellow.png"));
    public static final Icon icon22ledYellowOff = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/leddarkyellow.png"));
    public static final Icon icon22ledGreenOn = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/ledgreen.png"));
    public static final Icon icon22ledGreenOff = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/leddarkgreen.png"));
    public static final Icon icon16ledGreenOn = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/ledgreen.png"));
    public static final Icon icon163ledGreenOn = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/3ledlightgreen.png"));
    public static final Icon icon16ledGreenOff = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/leddarkgreen.png"));
    public static final Icon icon22ledOrangeOn = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/ledorange.png"));
    public static final Icon icon22helpMe = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/helpme.png"));
    public static final Icon icon22ledOrangeOff = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/leddarkorange.png"));
    public static final Icon icon22ledPurpleOn = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/ledpurple.png"));
    public static final Icon icon22ledPurpleOff = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/leddarkpurple.png"));
    public static final Icon icon22ledBlueOn = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/ledlightblue.png"));
    public static final Icon icon22ledBlueOff = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/leddarkblue.png"));
    public static final Icon icon22singleIncident = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/single-incident.png"));
    public static final Icon icon22intervalByDay = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/by-day.png"));
    public static final Icon icon22intervalBySecond = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/by-second.png"));
    public static final Icon icon22intervalNoConstraints = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/no-constraints.png"));
    public static final Icon icon22nothing = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/nothing.png"));
    public static final Icon icon22stopSignGray = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/stop-gray.png"));
    public static final Icon icon22stopSign = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/stop.png"));
    public static final Icon icon22comment = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/comment.png"));
    public static final Icon icon48comment = new ImageIcon(SYSConst.class.getResource("/artwork/48x48/comment.png"));
    public static final Icon icon22magnify1 = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/magnify1.png"));
    public static final Icon icon22home = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/home.png"));
    public static final Icon icon22wizard = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/wizard.png"));
    public static final Icon icon22addrow = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/shetaddrow.png"));
    public static final Icon icon16biohazard = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/biohazard.png"));
    public static final Icon icon22biohazard = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/biohazard.png"));
    public static final Icon icon48biohazard = new ImageIcon(SYSConst.class.getResource("/artwork/48x48/biohazard.png"));
    public static final Icon icon48edit = new ImageIcon(SYSConst.class.getResource("/artwork/48x48/edit3.png"));
    public static final Icon icon22diabetes = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/diabetes.png"));
    public static final Icon icon16diabetes = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/diabetes.png"));
    public static final Icon icon22medical = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/medical.png"));
    public static final Icon icon22warning = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/warning.png"));
    public static final Icon icon48sideeffects = new ImageIcon(SYSConst.class.getResource("/artwork/48x48/prescription_bottle.png"));
    public static final Icon icon22sideeffects = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/prescription_bottle.png"));
    public static final Icon icon48teacher = new ImageIcon(SYSConst.class.getResource("/artwork/48x48/teacher_blackboard.png"));
    public static final Icon icon22teacher = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/teacher_blackboard.png"));
    public static final Icon icon16warning = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/warning.png"));
    public static final Icon icon22work = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/work.png"));
    public static final Icon icon22chat = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/chat.png"));
    public static final Icon icon22colorset = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/colorset.png"));
    public static final Icon icon22colorsetPressed = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/bw/colorset_pressed.png"));
    public static final Icon icon22allergy = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/allergy.png"));
    public static final Icon icon16allergy = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/allergy.png"));
    public static final Icon icon32selected = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/bw/selected.png"));
    public static final Icon icon32unselected = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/bw/unselected.png"));
    public static final Icon gfx259x203medic0 = new ImageIcon(SYSConst.class.getResource("/artwork/other/medicine0.png"));
    public static final Icon gfx259x203medic1 = new ImageIcon(SYSConst.class.getResource("/artwork/other/medicine1.png"));
    public static final Icon gfx259x203medic2 = new ImageIcon(SYSConst.class.getResource("/artwork/other/medicine2.png"));
    public static final Icon gfx259x203medic3 = new ImageIcon(SYSConst.class.getResource("/artwork/other/medicine3.png"));
    public static final Icon gfx259x203medic4 = new ImageIcon(SYSConst.class.getResource("/artwork/other/medicine4.png"));

    public static final Icon icon22tagPurpleDelete2 = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/tag_purple_delete2.png"));
    public static final Icon icon22tagPurpleDelete4 = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/tag_purple_delete4.png"));
    public static final Icon icon22tagPurple = new ImageIcon(SYSConst.class.getResource("/artwork/22x22/tag_purple.png"));

    public static final Icon icon16tagPurpleDelete2 = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/tag_purple_delete2.png"));
    public static final Icon icon16tagPurpleDelete4 = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/tag_purple_delete4.png"));
    public static final Icon icon16tagPurple = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/tag_purple.png"));

    public static final Icon icon16user = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/user_active.png"));
    public static final Icon icon16userDel = new ImageIcon(SYSConst.class.getResource("/artwork/16x16/delete_user.png"));

    public static final Icon icon32reload0 = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/reload0000.png"));
    public static final Icon icon32reload1 = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/reload0225.png"));
    public static final Icon icon32reload2 = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/reload0450.png"));
    public static final Icon icon32reload3 = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/reload0675.png"));
    public static final Icon icon32reload4 = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/reload0900.png"));
    public static final Icon icon32reload5 = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/reload1125.png"));
    public static final Icon icon32reload6 = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/reload1350.png"));
    public static final Icon icon32reload7 = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/reload1575.png"));
    public static final Icon icon32reload8 = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/reload1800.png"));
    public static final Icon icon32reload9 = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/reload2025.png"));
    public static final Icon icon32reload10 = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/reload2250.png"));
    public static final Icon icon32reload11 = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/reload2475.png"));
    public static final Icon icon32reload12 = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/reload2700.png"));
    public static final Icon icon32reload13 = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/reload2925.png"));
    public static final Icon icon32reload14 = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/reload3150.png"));
    public static final Icon icon32reload15 = new ImageIcon(SYSConst.class.getResource("/artwork/32x32/reload3375.png"));

}
