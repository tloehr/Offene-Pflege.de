package entity.verordnungen;

/**
 * Created by IntelliJ IDEA.
 * User: tloehr
 * Date: 15.12.11
 * Time: 16:30
 * To change this template use File | Settings | File Templates.
 */
public class MedBuchungenTools {
    public static final int STATUS_AUSBUCHEN_NORMAL = 0;
    public static final int STATUS_EINBUCHEN_ANFANGSBESTAND = 1;
    public static final int STATUS_KORREKTUR_MANUELL = 2;
    public static final int STATUS_KORREKTUR_AUTO_LEER = 3;
    public static final int STATUS_KORREKTUR_AUTO_VORAB = 4;
    public static final int STATUS_KORREKTUR_AUTO_ABGELAUFEN = 5;
    public static final int STATUS_KORREKTUR_AUTO_RUNTERGEFALLEN = 6;
    public static final int STATUS_KORREKTUR_AUTO_ABSCHLUSS_BEI_PACKUNGSENDE = 7;
    public static final int STATUS_KORREKTUR_AUTO_ABSCHLUSS_BEI_VORRATSABSCHLUSS = 8;
}
