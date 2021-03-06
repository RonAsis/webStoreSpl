package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable{
	//@INV availableAmountInCreditCard>=0
	private int id;//the id of the customer
	private String name;//the name of the customer
	private String address;// the address of the customer
	private int distance;// the distance of the customer’s address from the store
	private List<OrderReceipt> receipts;//all the receipts issued to the customer
	private int creditCard;//The number of the credit card of the customer
	private AtomicInteger availableAmountInCreditCard;//The remaining available amount of money in the credit card of the customer.

	/**
	 * Customer's constructor.
	 *
	 * @param id - the id of the customer.
	 * @param name - the name of the customer.
	 * @param address - the address of the customer.
	 * @param distance - the distance from the customer's address to the store.
	 * @param creditCard - the number of the customer's credit card.
	 * @param availableAmountInCreditCard - the remaining available amount of money in the customer's credit card.
	 */
	public Customer(int id,String name, String address,int distance,int creditCard,int availableAmountInCreditCard){
		this.id=id;
		this.name=name;
		this.address=address;
		this.distance=distance;
		this.creditCard=creditCard;
		this.availableAmountInCreditCard=new AtomicInteger();
		this.receipts=new LinkedList<>();
		this.availableAmountInCreditCard.set(availableAmountInCreditCard);
	}

	/**
	 * Retrieves the name of the customer.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Retrieves the ID of the customer  .
	 */
	public int getId() {
		return id;
	}

	/**
	 * Retrieves the address of the customer.
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Retrieves the distance of the customer from the store.
	 */
	public int getDistance() {
		return distance;
	}

	/**
	 * Retrieves a list of receipts for the purchases this customer has made.
	 * <p>
	 * @return A list of receipts.
	 */
	public List<OrderReceipt> getCustomerReceiptList() {
		return receipts;
	}

	/**
	 * Retrieves the amount of money left on this customers credit card.
	 * <p>
	 * @return Amount of money left.
	 */
	public int getAvailableCreditAmount() {
		return availableAmountInCreditCard.get();
	}

	/**
	 * Retrieves this customers credit card serial number.
	 */
	public int getCreditNumber() {
		return creditCard;
	}

	/**
	 * Adds the given receipt to the list of receipts.
	 * @param receipt - the receipt that needs to be added to list of receipts.
	 */

	public void addReceipts(Object receipt){
		receipts.add((OrderReceipt)receipt);
	}

	/**
	 * Charges the customer's credit card with the given amount.
	 * @param amount- the amount that needs to be charged.
	 */
	public void chargeCredit(int amount){
		Integer oldValue;
		Integer newValue;
		do {
			oldValue=this.availableAmountInCreditCard.get();
			newValue=this.availableAmountInCreditCard.get()-amount;
			if (newValue<0)
				break;
		} while(!this.availableAmountInCreditCard.compareAndSet(oldValue,newValue));
	}
}
