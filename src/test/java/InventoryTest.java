import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import static org.junit.Assert.*;

public class InventoryTest {
    private Inventory inventory;

    @Before
    public void setUp() throws Exception {
        try{
            this.inventory = inventory.getInstance();
        } catch (Exception e){
            System.out.println("Can't create a new 'Inventory'.");
        }
    }

    @After
    public void tearDown() throws Exception {
       // no need
    }

    @Test
    public void getInstance() {
        assertNotNull("should not be null", inventory.getInstance());
    }

    @Test
    public void load() {
        BookInventoryInfo [] books=new BookInventoryInfo[2];
        inventory.load(books); // loading booksInventory with an empty array, the array shouldn't be loaded
        inventory.take("Hidden Figures");
        assertSame(inventory.take("Hidden Figures"), OrderResult.NOT_IN_STOCK);

        books[0]=new BookInventoryInfo("Hidden Figures", 10, 100);
        books[1]=new BookInventoryInfo("The Help", 20, 90);
        inventory.load(books); // loading booksInventory with an array
        assertSame(inventory.take("Hidden Figures"), OrderResult.SUCCESSFULLY_TAKEN);
    }

      @Test
   public void take() {
          BookInventoryInfo [] books=new BookInventoryInfo[2];
          inventory.load(books);
          assertSame(inventory.take("Hidden Figures"), OrderResult.NOT_IN_STOCK); // taking a book from the empty booksInventory

          books[0]=new BookInventoryInfo("Hidden Figures", 1, 100);
          books[1]=new BookInventoryInfo("The Help", 20, 90);
          inventory.load(books);
          assertSame(inventory.take("Hidden Figures"), OrderResult.SUCCESSFULLY_TAKEN); // taking a book that is in booksInventory
          assertSame(inventory.take("Hidden Figures"), OrderResult.NOT_IN_STOCK); // taking a book that is in booksInventory but isn't in stock

          assertSame(inventory.take("Hidden Figures 2"), OrderResult.NOT_IN_STOCK); // taking a book that isn't in booksInventory
      }

    @Test
    public void checkAvailabiltyAndGetPrice() {
        BookInventoryInfo [] books=new BookInventoryInfo[2];
        inventory.load(books);
        assertEquals(inventory.checkAvailabiltyAndGetPrice("Hidden Figures"), -1); // searching for a book in the empty booksInventory

        books[0]=new BookInventoryInfo("Hidden Figures", 1, 100);
        books[1]=new BookInventoryInfo("The Help", 20, 90);
        inventory.load(books);
        assertEquals(inventory.checkAvailabiltyAndGetPrice("Hidden Figures"), books[0].getPrice()); // searching for a book in booksInventory
        assertEquals(inventory.checkAvailabiltyAndGetPrice("Hidden Figures 2"), -1); // searching for a book that isn't in the booksInventory
    }

    /**
     * This function writes into a file by using "printInventoryToFile",
     * reads the output file, saves it as a HashMap and compares it to the HashMap "createHashMap" returns,
     * which is the same as the one that "createHashMap" in Inventory creates.
     */
    @Test
    public void printInventoryToFile() {
        String output = "Output";
        BookInventoryInfo [] books=new BookInventoryInfo[2];
        inventory.load(books);
        inventory.printInventoryToFile(output);
        assertEquals(createHashMap(books), readFromFile(output));

        String output1 = "Output1";
        books[0]=new BookInventoryInfo("Hidden Figures", 20, 100);
        books[1]=new BookInventoryInfo("The Help", 1, 90);
        inventory.load(books);
        inventory.printInventoryToFile(output1);
        assertEquals(createHashMap(books), readFromFile(output1));
    }

    /**
     * This function creates a HasMap that is the same as the one that "createHashMap" in Inventory creates,
     * because there's no access to that HashMap.
     * @param books - the books that are in the booksInventory
     * @return a HasMap that holds all of the books' titles & amounts.
     */
    private HashMap<String,Integer> createHashMap(BookInventoryInfo [] books){
        HashMap<String, Integer> hashmap = new HashMap<String, Integer>();
        if (books!=null)
            for(BookInventoryInfo info: books)
                if (info!=null)
                    hashmap.put(info.getBookTitle(),info.getAmountInInventory());
        return hashmap;
    }

    /**
     * This function reads from given file, and saves it as a HasMap
     *
     * @param file - the file that needs to be saved as a HashMap.
     * @return the given file in a form of a HashMap.
     */
    private HashMap<String,Integer> readFromFile(String file) {
        HashMap<String,Integer> hashmap = new HashMap<String,Integer>();
        ObjectInputStream inputStream = null;
        try {
            inputStream = new ObjectInputStream(new FileInputStream(file));
            hashmap= (HashMap<String, Integer>) inputStream.readObject();
        } catch (ClassNotFoundException classNotFoundException) {
            System.err.println("Object creation failed.");
        } catch (FileNotFoundException fileNotFoundException) {
            System.err.println("File not found.");
        } catch (IOException ioException) {
            System.err.println("Error opening file.");
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException ioException) {
                System.err.println("Error closing file.");
            }
        }
        return hashmap;
    }
}