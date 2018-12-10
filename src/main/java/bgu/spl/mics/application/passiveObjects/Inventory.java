package bgu.spl.mics.application.passiveObjects;
import java.io.*;
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
	private static ConcurrentHashMap<String,BookInventoryInfo> booksInventory;
	private static Inventory instance=null;

	/**
	 * Retrieves the single instance of this class.
	 */
	public static Inventory getInstance() {
		return SingletonHolder.instance;
	}

	private static class SingletonHolder{
		private static Inventory instance = new Inventory();
	}

	private Inventory(){
		createInstance();
		createBooksInventory();
	}

	/**
	 * This method returns true if "instance" was created.
	 *
	 * @return true if "instance" was created, false otherwise.
	 */
	private static boolean checkExist(){
		return instance == null;
	}

	/**
	 * This method creates "instance"
	 */
	private static void createInstance(){
		instance = new Inventory();
	}

	/**
	 * This method creates "booksInventory".
	 */
	private static void createBooksInventory(){
		booksInventory = new ConcurrentHashMap<String, BookInventoryInfo>();
	}

	/**
	 * Initializes the store inventory. This method adds all the items given to the store
	 * inventory.
	 * <p>
	 * @param inventory 	Data structure containing all data necessary for initialization
	 * 						of the inventory.
	 */
	public void load (BookInventoryInfo[ ] inventory ) {
		if (booksInventory.size()!=0)// load will be called only once, in the case of load being called again, not changing the inventory
			return;
		if (booksInventory==null) // in case booksInventory wasn't initialized
			createBooksInventory();
		for(BookInventoryInfo info:inventory) // adding the books to booksInventory
			if ((info!=null)){
				booksInventory.put(info.getBookTitle(),info);
			}
	}

	/**
	 * Attempts to take one book from the store.
	 * <p>
	 * @param book 		Name of the book to take from the store
	 * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
	 * 			The first should not change the state of the inventory while the
	 * 			second should reduce by one the number of books of the desired type.
	 */
	public OrderResult take (String book) {
		if (booksInventory!=null && checkAvailability(book)!=null)
			return  OrderResult.SUCCESSFULLY_TAKEN;
		return OrderResult.NOT_IN_STOCK;
	}

	/**
	 * This method checks if a given book is in the booksInventory, if it is,
	 * the method reduces by one the amount of copies of this book there are in the inventory.
	 *
	 * @param book - the book that is being looked for.
	 * @return the book's BookInventoryInfo, if the book was found and
	 * if there are copies of it in the inventory, null otherwise.
	 */
	private BookInventoryInfo checkAvailability(String book){
		if (booksInventory!=null)
			for(int i=0; i<booksInventory.size(); i++) {
				if (booksInventory.get(book)!=null && booksInventory.get(book).getAmountInInventory()>0) {
					booksInventory.get(book).lessAmountBook();
					return booksInventory.get(book);
				}
			}
		return null;
	}

	/**
	 * Checks if a certain book is available in the inventory.
	 * <p>
	 * @param book 		Name of the book.
	 * @return the price of the book if it is available, -1 otherwise.
	 */
	public int checkAvailabiltyAndGetPrice(String book) {
		return  bookPrice(checkAvailability(book));
	}

	/**
	 * This method returns the given book's price or -1 if the book equals null.
	 *
	 * @param book - the BookInventoryInfo of the book, that price needs to be returned.
	 * @return the price of the book, or -1, if it equals to null.
	 */
	private int bookPrice(BookInventoryInfo book){
		if (book!=null)
			return book.getPrice();
		return -1;
	}

	/**
	 * <p>
	 * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
	 * should be the titles of the books while the values (type {@link Integer}) should be
	 * their respective available amount in the inventory.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printInventoryToFile(String filename){
		writeToFile(createHashMap(),filename);
	}

	/**
	 * This method creates a HashMap that holds the "title"s
	 * and "amountInInventory"s of all of the books that are in the inventory, and returns it.
	 *
	 * @return a HashMap that holds the "title"s
	 * sand "amountInInventory"s of all of the books that are in the inventory.
	 */
	private HashMap<String,Integer> createHashMap(){
		HashMap<String, Integer> hashmap = new HashMap<String, Integer>();
		for(String key: booksInventory.keySet())
			hashmap.put(key,booksInventory.get(key).getAmountInInventory());
		return hashmap;
	}

	/**
	 * This method writes the given hashmap in an output file named "file".
	 *
	 * @param hashmap - the HasMap that need to be written in the output file.
	 * @param file - the name of the output file.
	 */
	private void writeToFile(HashMap<String,Integer> hashmap, String file) {
		ObjectOutputStream outStream = null;
		try {
			outStream = new ObjectOutputStream(new FileOutputStream(file));
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

}
