package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

/**
 * Passive data-object representing a receipt that should 
 * be sent to a customer after the completion of a BookOrderEvent.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class OrderReceipt implements Serializable {
	private int orderId;//the id of the order
	private String seller;//the name of the service which handled the order
	private int customerId;//The id of the customer
	private String bookTitle;//title of the book
	private int price;// the price that the customer paid for the book
	private int issuedTick;// tick in which this receipt was issued
	private int orderTick;// tick in which the customer ordered the book
	private int processTick;//tick in which the selling service started processing the order

	public OrderReceipt (){

	}

	public OrderReceipt (OrderReceipt orderReceipt){
		this.orderId = orderReceipt.getOrderId();
		this.seller = orderReceipt.getSeller();
		this.customerId = orderReceipt.getCustomerId();
		this.bookTitle = orderReceipt.getBookTitle();
		this.price = orderReceipt.getPrice();
		this.issuedTick = orderReceipt.getIssuedTick();
		this.orderTick = orderReceipt.getOrderTick();
		this.processTick = orderReceipt.getProcessTick();
	}

	public OrderReceipt (int issuedTick,String seller,int customerId,String bookTitle,int price,int orderTick){
		this.issuedTick=issuedTick;
		this.seller=seller;
		this.customerId=customerId;
		this.bookTitle=bookTitle;
		this.price=price;
		this.orderTick=orderTick;
	}

	/**
	 * Retrieves the orderId of this receipt.
	 */
	public int getOrderId() {
		return  this.orderId;
	}

	/**
	 * Retrieves the name of the selling service which handled the order.
	 */
	public String getSeller() {
		return this.seller;
	}

	/**
	 * Retrieves the ID of the customer to which this receipt is issued to.
	 * <p>
	 * @return the ID of the customer
	 */
	public int getCustomerId() {
		return this.customerId;
	}

	/**
	 * Retrieves the name of the book which was bought.
	 */
	public String getBookTitle() {
		return this.bookTitle;
	}

	/**
	 * Retrieves the price the customer paid for the book.
	 */
	public int getPrice() {
		return this.price;
	}

	/**
	 * Retrieves the tick in which this receipt was issued.
	 */
	public int getIssuedTick() {
		return this.issuedTick;
	}

	/**
	 * Retrieves the tick in which the customer sent the purchase request.
	 */
	public int getOrderTick() {
		return this.orderTick;
	}

	/**
	 * Retrieves the tick in which the treating selling service started
	 * processing the order.
	 */
	public int getProcessTick() {
		return this.processTick;
	}

	public void setOrderId(int orderId){
		this.orderId=orderId;
	}
	public void setSeller(String seller){
		this.seller=seller;
	}
	public void setCustomerId(int customerId){
		this.customerId=customerId;
	}
	public void setBookTitle(String bookTitle){
		this.bookTitle=bookTitle;
	}
	public void setPrice(int price){
		this.price=price;
	}
	public void setIssuedTick(int issuedTick){
		this.issuedTick=issuedTick;
	}
	public void setOrderTick(int orderTick){
		this.orderTick=orderTick;
	}
	public void setProcessTick(int processTick){
		this.processTick = processTick;
	}

}