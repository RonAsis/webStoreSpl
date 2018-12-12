package bgu.spl.mics.application.passiveObjects;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */

public class Inventory {
    //@INV: booksInventory.get(book).getAmountInInventory()>=0

	private ConcurrentHashMap<String,BookInventoryInfo> booksInventory;
	/**
	 * Retrieves the single instance of this class.
	 */
	private static class SingletonHolder {
		private static Inventory instance = new Inventory();
	}
	private Inventory() {
		booksInventory=new ConcurrentHashMap<String,BookInventoryInfo>();
	}
	public static Inventory getInstance() {
		return SingletonHolder.instance;
	}
	/**
	 * Initializes the store inventory. This method adds all the items given to the store
	 * inventory.
	 * <p>
	 * @param inventory 	Data structure containing all data necessary for initialization
	 * 						of the inventory.
	 */
	public void load (BookInventoryInfo[ ] inventory ) {
		for(BookInventoryInfo bookInventoryInfo:inventory)
			this.booksInventory.put(bookInventoryInfo.getBookTitle(),bookInventoryInfo);
	}

	/**
	 * Attempts to take one book from the store.
	 * <p>
	 * @param book 		Name of the book to take from the store
	 * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
	 * 			The first should not change the state of the inventory while the
	 * 			second should reduce by one the number of books of the desired type.
	 */
	//@PRE book!=null
    //POST if(booksInventory.get(book)!=null && booksInventory.get(book).lessAmountBook())booksInventory.get(book).getAmountInInventory()-1
	public OrderResult take (String book) {
		for (String key:booksInventory.keySet()){
			if (book.equals(key)) {
			    BookInventoryInfo b=booksInventory.get(book);
				if (b!=null && b.lessAmountBook()) {
					return OrderResult.SUCCESSFULLY_TAKEN;
				}
			}
		}
		return OrderResult.NOT_IN_STOCK;
	}
	/**
	 * Checks if a certain book is available in the inventory.
	 * <p>
	 * @param book 		Name of the book.
	 * @return the price of the book if it is available, -1 otherwise.
	 */
	public int checkAvailabiltyAndGetPrice(String book) {
		return  bookPrice(booksInventory.get(book));
	}
	private int bookPrice(BookInventoryInfo book){
		if (book!=null&& book.getAmountInInventory()>0)
			return book.getPrice();
		return -1;
	}

	/**
	 *
	 * <p>
	 * Prints to a file name @fiasd321zxc
	 * lename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
	 * should be the titles of the books while the values (type {@link Integer}) should be
	 * their respective available amount in the inventory.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printInventoryToFile(String filename){
		createFile(createHashMap(),filename);

	}
	private void createFile(HashMap<String,Integer> hashmap ,String filename){
		ObjectOutputStream outStream = null;
		try {
			outStream = new ObjectOutputStream(new FileOutputStream(filename));
			outStream.writeObject(hashmap);
		} catch (IOException ioException) {
			System.err.println("Error opening file.");
		} catch (NoSuchElementException noSuchElementException) {
			System.err.println("Invalid input.");
		} finally {
			try {
				if (outStream != null)
					outStream.close();
			} catch (IOException ioException) {
				System.err.println("Error closing file.");
			}
		}
	}
	private HashMap<String,Integer> createHashMap(){
		HashMap<String, Integer> hmap = new HashMap<String, Integer>();
		for(String key: booksInventory.keySet())
			hmap.put(key,booksInventory.get(key).getAmountInInventory());
		return hmap;
	}
}
