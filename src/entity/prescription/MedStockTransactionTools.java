package entity.prescription;

/**
 * Created by shortelliJ IDEA.
 * User: tloehr
 * Date: 15.12.11
 * Time: 16:30
 * To change this template use File | Settings | File Templates.
 */
public class MedStockTransactionTools {
    public static final short STATE_DEBIT = 0;
    public static final short STATE_CREDIT = 1;
    public static final short STATE_EDIT_MANUAL = 2;
    public static final short STATE_EDIT_EMPTY_NOW = 3;
    public static final short STATE_EDIT_EMPTY_SOON = 4;
    public static final short STATE_EDIT_EMPTY_PAST_EXPIRY = 5;
    public static final short STATE_EDIT_EMPTY_BROKEN_OR_LOST = 6;
    public static final short STATE_EDIT_STOCK_CLOSED = 7;
    public static final short STATE_EDIT_INVENTORY_CLOSED = 8;




}
