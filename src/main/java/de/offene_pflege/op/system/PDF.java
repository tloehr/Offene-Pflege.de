package de.offene_pflege.op.system;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import de.offene_pflege.entity.files.SYSFilesTools;
import de.offene_pflege.op.tools.SYSTools;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created with IntelliJ IDEA. User: tloehr Date: 07.05.13 Time: 16:49
 * <p>
 * A document is created in 5 steps:
 * <p>
 * 1. Create a document. This document doesn't know anything about the presentation of the document, only about its
 * content. 2. Create a writer. You are creating a PdfWriter that will translate the content into a presentation, more
 * specifically into a PDF document with one or more pages. 3. Open the document. 4. Add content. 5. Close the
 * document.
 */
@Log4j2
public class PDF  {

//    Paragraph footer, endofreport;

    public static final Font.FontFamily FAMILY = Font.FontFamily.HELVETICA;
    public static final Rectangle PAGESIZE = PageSize.A4;
    public static final char frac14 = (char) 188; // quarter symbol
    public static final char frac12 = (char) 189; // half symbol
    public static final char frac34 = (char) 190; // half symbol
    private static int SIZE = 10;

    private final File output;
    String footer = "";
    private Document document;
    private int mypagecounter;

    public static Font bold(int size) {
        return new Font(FAMILY, size, Font.BOLD);
    }

    public static Font plain(int size) {
        return new Font(FAMILY, size, Font.NORMAL);
    }

    public static Font underline(int size) {
        return new Font(FAMILY, size, Font.UNDERLINE);
    }

    public static Font italic(int size) {
        return new Font(FAMILY, size, Font.ITALIC);
    }

    public static Font bold() {
        return new Font(FAMILY, SIZE, Font.BOLD);
    }

    public static Font plain() {
        return new Font(FAMILY, SIZE, Font.NORMAL);
    }

    public static Font underline() {
        return new Font(FAMILY, SIZE, Font.UNDERLINE);
    }

    public static Font italic() {
        return new Font(FAMILY, SIZE, Font.ITALIC);
    }


    public static int sizeDefault() {
        return SIZE;
    }

    public static int sizeH1() {
        return SIZE + 12;
    }

    public static int sizeH2() {
        return SIZE + 6;
    }

    public PDF(File output, String footer, int basefontsize) throws IOException, DocumentException {
        this.footer = footer;
        SIZE = basefontsize;
        if (output == null) {
            this.output = SYSFilesTools.createTempFile("opde", ".pdf");

        } else {
            this.output = output;
        }
        mypagecounter = 0;
        init();
    }


    public Document getDocument() {
        return document;
    }

    private void init() throws FileNotFoundException, DocumentException {
        document = new Document(PAGESIZE, Utilities.millimetersToPoints(10), Utilities.millimetersToPoints(10), Utilities.millimetersToPoints(20), Utilities.millimetersToPoints(20));
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(output));

        writer.setPageEvent(new PdfPageEventHelper() {
            PdfTemplate totalPages;

            @Override
            public void onOpenDocument(PdfWriter writer, Document document) {
                totalPages = writer.getDirectContent().createTemplate(30, 22);
            }

            @Override
            public void onStartPage(PdfWriter writer, Document document) {
                mypagecounter++;
            }

            @Override
            public void onEndPage(PdfWriter writer, Document document) {

                PdfPTable table = new PdfPTable(3);
                try {
                    table.setWidths(new int[]{24, 24, 2});
                    table.setTotalWidth(527);
                    table.setLockedWidth(true);
                    table.getDefaultCell().setFixedHeight(20);
                    table.getDefaultCell().setBorder(Rectangle.TOP);

                    table.addCell(footer);
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(SYSTools.xx("misc.msg.pageOf", writer.getPageNumber()));

                    PdfPCell cell = new PdfPCell(Image.getInstance(totalPages));
                    cell.setBorder(Rectangle.TOP);
                    table.addCell(cell);
                    table.writeSelectedRows(0, -1, 34, 40, writer.getDirectContent());
                } catch (DocumentException de) {
                    throw new ExceptionConverter(de);
                }
            }


            @Override
            public void onCloseDocument(PdfWriter writer, Document document) {
                // 20200501 - der Footer zeigte die falsche maximale Seitenzahl an. Immer eine zu wenig.
                log.debug("mypagecounter: " + mypagecounter);
                log.debug("writer.getPageNumber(): " + writer.getPageNumber());
                ColumnText.showTextAligned(totalPages, Element.ALIGN_LEFT, new Phrase(String.valueOf(writer.getPageNumber()), plain(12)), 2, 8, 0);
            }
        });
        document.open();


    }

    public File getOutputFile() {
        output.deleteOnExit();
        return output;
    }

    public static Chunk chunk(String text) {
        return chunk(text, plain());
    }

    public static Chunk chunk(String text, Font font) {
        Chunk c = new Chunk(SYSTools.xx(text));
        c.setFont(font);
        return c;
    }

    public static PdfPCell cell(String text, Font font, int halign, int valign) {
        PdfPCell cell = new PdfPCell(new Phrase(SYSTools.xx(text), font));
        cell.setHorizontalAlignment(halign);
        cell.setVerticalAlignment(valign);
        return cell;
    }

//    public static Paragraph getEndOfReport() {
//        Paragraph endofreport = new Paragraph();
//        endofreport.setFont(plain());
//
//        Chunk c = chunk("misc.msg.endofreport");
//        c.setFont(bold());
//        endofreport.add(c);
//        endofreport.add(" ");
//
//        if (OPDE.getLogin() != null) {
//            endofreport.add(OPDE.getLogin().getUser().getUID());
//        }
//        endofreport.add(Chunk.NEWLINE);
//        endofreport.add(DateFormat.getDateTimeInstance().format(new Date()));
//
//        endofreport.add(Chunk.NEWLINE);
//        endofreport.add(OPDE.getAppInfo().getProgname() + ", v" + OPDE.getAppInfo().getVersion());
//
//        return endofreport;
//    }

    public static Phrase getAsPhrase(BigDecimal bd) {

//        BigDecimal bd = in.stripTrailingZeros();

        Phrase phrase = new Phrase();
        phrase.setFont(plain());

        String formattedbd = SYSTools.formatBigDecimal(bd.setScale(2, RoundingMode.HALF_UP));
        if (formattedbd.equals("0")) phrase.add("");
//        else if (formattedbd.equals("0,5")) phrase.add(Character.toString(frac12));
//        else if (formattedbd.equals("0,25")) phrase.add(Character.toString(frac14));
//        else if (formattedbd.equals("0,75")) phrase.add(Character.toString(frac34));
        else phrase.add(SYSTools.formatBigDecimal(bd.setScale(2, RoundingMode.HALF_UP)));

//        if (bd.compareTo(BigDecimal.ZERO) == 0) {
//            // nop
////        } else if (bd.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0) {
////            phrase.add(Integer.toString(bd.intValue()));
//        } else if (bd.compareTo(new BigDecimal(0.5d)) == 0) {
//            phrase.add(new Chunk(frac12));
//        } else if (bd.compareTo(new BigDecimal(0.25d)) == 0) {
//            phrase.add(new Chunk(frac14));
//        } else if (bd.compareTo(new BigDecimal(0.75d)) == 0) {
//            phrase.add(new Chunk(frac34));
//        } else if (bd.setScale(2, RoundingMode.HALF_UP).toString().equals("0.33")) {
//            Chunk one = new Chunk("1");
//            one.setTextRise(0.1f);
//            Chunk third = new Chunk("3");
//            third.setTextRise(-0.1f);
//
//            phrase = new Phrase(one);
//            phrase.add("/");
//            phrase.add(third);
//
//        } else {
//            DecimalFormat df = new DecimalFormat();
//
//            df.setMaximumFractionDigits(2);
//            df.setMinimumFractionDigits(0);
//            df.setGroupingUsed(false);
//
//            phrase.add(df.format(bd.setScale(2, RoundingMode.HALF_UP)));
//        }

        return phrase;
    }


}
