package op.system;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import op.OPDE;
import op.tools.SYSTools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 07.05.13
 * Time: 16:49
 * To change this template use File | Settings | File Templates.
 */
public class PDF {

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


    public static int sizeDefault(){
            return SIZE;
        }

    public static int sizeH1(){
        return SIZE+12;
    }

    public static int sizeH2(){
        return SIZE+6;
    }

    public PDF(File output, String footer, int basefontsize) throws IOException, DocumentException {
        this.footer = footer;
        SIZE = basefontsize;
        if (output == null) {
            this.output = File.createTempFile("opde", ".pdf");
        } else {
            this.output = output;
        }

        init();
    }


    public Document getDocument() {
        return document;
    }

    private void init() throws FileNotFoundException, DocumentException {


        document = new Document(PAGESIZE, Utilities.millimetersToPoints(10), Utilities.millimetersToPoints(10), Utilities.millimetersToPoints(20), Utilities.millimetersToPoints(20));


        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(output));


        writer.setPageEvent(new PdfPageEventHelper() {
            int pagenumber;
            PdfTemplate totalPages;

            @Override
            public void onOpenDocument(PdfWriter writer, Document document) {
                totalPages = writer.getDirectContent().createTemplate(30, 22);
            }

            @Override
            public void onStartPage(PdfWriter writer, Document document) {
                pagenumber++;
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
                    table.addCell(String.format(OPDE.lang.getString("misc.msg.pageOf"), writer.getPageNumber()));

                    PdfPCell cell = new PdfPCell(Image.getInstance(totalPages));
                    cell.setBorder(Rectangle.TOP);
                    table.addCell(cell);
                    table.writeSelectedRows(0, -1, 34, 40, writer.getDirectContent());
                } catch (DocumentException de) {
                    throw new ExceptionConverter(de);
                }

//                Rectangle rect = writer.getBoxSize("art");
//                ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(OPDE.lang.getString() + pagenumber + " " + DateFormat.getDateTimeInstance().format(new Date())), (rect.getLeft() + rect.getRight()) / 2, rect.getBottom() - 18, 0);
            }


            @Override
            public void onCloseDocument(PdfWriter writer, Document document) {
                ColumnText.showTextAligned(totalPages, Element.ALIGN_LEFT, new Phrase(String.valueOf(writer.getPageNumber() - 1), plain(16)), 2, 2, 0);
            }
        });
        document.open();


    }

    public File getOutputFile() {
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

    public static Paragraph getEndOfReport() {
        Paragraph endofreport = new Paragraph();
        endofreport.setFont(plain());

        Chunk c = chunk("misc.msg.endofreport");
        c.setFont(bold());
        endofreport.add(c);
        endofreport.add(" ");

        if (OPDE.getLogin() != null) {
            endofreport.add(OPDE.getLogin().getUser().getUID());
        }
        endofreport.add(Chunk.NEWLINE);
        endofreport.add(DateFormat.getDateTimeInstance().format(new Date()));

        endofreport.add(Chunk.NEWLINE);
        endofreport.add(OPDE.getAppInfo().getProgname() + ", v" + OPDE.getAppInfo().getVersion() + "/" + OPDE.getAppInfo().getBuildnum());

        return endofreport;
    }

    public static Phrase getAsPhrase(BigDecimal bd) {

        Phrase phrase = new Phrase();
        if (bd.compareTo(BigDecimal.ZERO) == 0) {
            // nop
        } else if (bd.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0) {
            phrase.add(Integer.toString(bd.intValue()));
        } else if (bd.compareTo(new BigDecimal(0.5d)) == 0) {
            phrase.add(new Chunk(frac12));
        } else if (bd.compareTo(new BigDecimal(0.25d)) == 0){
            phrase.add(new Chunk(frac14));
        } else if (bd.compareTo(new BigDecimal(0.75d)) == 0) {
            phrase.add(new Chunk(frac34));
        } else if (bd.setScale(2, RoundingMode.HALF_UP).toString().equals("0.33")) {
            Chunk one = new Chunk("1");
            one.setTextRise(0.1f);
            Chunk third = new Chunk("3");
            third.setTextRise(-0.1f);

            phrase = new Phrase(one);
            phrase.add("/");
            phrase.add(third);

        } else {
            phrase.add(bd.setScale(2, RoundingMode.HALF_UP).toString());
        }

        return phrase;
    }


}
