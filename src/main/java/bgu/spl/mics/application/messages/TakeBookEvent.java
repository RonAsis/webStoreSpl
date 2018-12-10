package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;

/**
 * An event that is sent when a book needs to be taken from the inventory.
 * SellingService sends this event,InventoryService responds.
 */
public class TakeBookEvent implements Event {
    String bookName; // the name of the book that need to be taken from the inventory
    int moneyLeft; // the customer's available credit amount

    /**
     * TakeBookEvent's constructor.
     * @param bookName - the name of the book that need to be taken from the inventory/
     * @param moneyLeft - the customer's available credit amount.
     */
    public TakeBookEvent(String bookName, int moneyLeft ){
        this.bookName = bookName;
        this.moneyLeft = moneyLeft;
    }

    /**
     * Getters.
     */
    public int getMoneyLeft() {
        return moneyLeft;
    }

    public String getBookName() {
        return bookName;
    }
}
