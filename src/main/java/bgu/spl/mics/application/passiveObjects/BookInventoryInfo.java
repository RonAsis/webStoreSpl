package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a information about a certain book in the inventory.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class BookInventoryInfo {
	private String fBookTitle;// the title of the book
	private AtomicInteger fAmountInInventory;//numbers of book from this type
	private int fPrice;//the price of the book

	public  BookInventoryInfo(String bookTitle,Integer amountInInventory, Integer price){
		this.fBookTitle=bookTitle;
		this.fAmountInInventory.set(amountInInventory);
		this.fPrice=price;
	}
	public boolean lessAmountBook(){
		Integer oldValue;
		Integer newValue;
		do{
			oldValue=fAmountInInventory.get();
			if(oldValue>0)
				newValue=fAmountInInventory.get()-1;
			else
				return false;

		}while(fAmountInInventory.compareAndSet(oldValue,newValue));
		return true;
	}
	/**
     * Retrieves the title of this book.
     * <p>
     * @return The title of this book.   
     */
	public String getBookTitle() {
		return fBookTitle;
	}

	/**
     * Retrieves the amount of books of this type in the inventory.
     * <p>
     * @return amount of available books.      
     */
	public int getAmountInInventory() {
		return fAmountInInventory.get();
	}

	/**
     * Retrieves the price for  book.
     * <p>
     * @return the price of the book.
     */
	public int getPrice() {
		return fPrice;
	}
	
	

	
}
