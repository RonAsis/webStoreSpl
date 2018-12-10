package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

/**
 * An event that is sent when a client of the store wishes to buy a book.
 * APIService sends this event, SellingService responds.
 */
public class BookOrderEvent implements Event {
    private String bookName;
    private Customer customer;
    private int orderTickTime; // The time in which the customer placed his order

    /**
     * BookOrderEvent's constructor.
     *
     * @param bookName - the name of the book the customer wants to buy.
     * @param customer - the customer that wants to buy the given bookName.
     * @param orderTickTime - the time in which the customer placed his order.
     */
    public BookOrderEvent(String bookName, Customer customer, int orderTickTime){
        this.bookName = bookName;
        this.customer = customer;
        this.orderTickTime = orderTickTime;
    }

    /**
     * Getters.
     */
    public String getBookName() {
        return this.bookName;
    }

    public Customer getCustomer(){
        return this.customer;
    }

    public int getOrderTickTime() {
        return this.orderTickTime;
    }
}
