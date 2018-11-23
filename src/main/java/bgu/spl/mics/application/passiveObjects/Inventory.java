package bgu.spl.mics.application.passiveObjects;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Hashtable;

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
	private enum OrderResult{NOT_IN_STOCK,SUCCESSFULLY_TAKEN}
	private Hashtable<String,BookInventoryInfo> booksInventory;//check it this is safe//////////////////////////////////////////////////
	private static Inventory instance=null;
	/**
     * Retrieves the single instance of this class.
     */
	public static Inventory getInstance() {
		//TODO: Implement this
		if(checkExist())//////****//////
			createInsatnce();//////****//////
		return instance;//////****//////
	}
	private static boolean checkExist(){
		return instance==null;//////****//////
	}
	private static void createInsatnce(){
		instance=new Inventory();//////****//////
	}
	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (BookInventoryInfo[ ] inventory ) {
		for(BookInventoryInfo bookInventoryInfo:inventory)/////****//////
			this.booksInventory.put(bookInventoryInfo.getBookTitle(),bookInventoryInfo);/////****//////
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
			if (checkAvailabilty(book)!=null)
				return  OrderResult.SUCCESSFULLY_TAKEN;//////****//////
		return OrderResult.NOT_IN_STOCK;//////****//////
		//return null;////*****//////////
	}
	private BookInventoryInfo checkAvailabilty(String book){
		for(String key: booksInventory.keySet()) {//////****//////
			if (key.equals(book))//////****//////
				return booksInventory.get(key);
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
		 return  bookPrice(checkAvailabilty(book));
		//TODO: Implement this
	}
	private int bookPrice(BookInventoryInfo book){
		if (book!=null)
			return book.getPrice();
		return -1;
	}
	
	/**
     * 
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
     */
	public void printInventoryToFile(String filename){
		//TODO: Implement this
		createFile(createHashMap(),filename);//////////////**********///////////////

	}
	private void createFile(HashMap<String,Integer> hmap ,String filename){
		try//////////////**********///////////////
		{//////////////**********///////////////
			FileOutputStream fos =new FileOutputStream(filename);//////////////**********///////////////
			ObjectOutputStream oos = new ObjectOutputStream(fos);//////////////**********///////////////
			oos.writeObject(hmap);//////////////**********///////////////
			oos.close();//////////////**********///////////////
			fos.close();//////////////**********///////////////
		}catch(IOException ioe) {//////////////**********///////////////
			ioe.printStackTrace();//////////////**********///////////////
		}
	}
	private HashMap<String,Integer> createHashMap(){
		HashMap<String, Integer> hmap = new HashMap<String, Integer>();//////////////**********///////////////
		for(String key: booksInventory.keySet())//////****//////
			hmap.put(key,booksInventory.get(key).getAmountInInventory());//////////////**********///////////////
		return hmap;//////////////**********///////////////
	}
}
