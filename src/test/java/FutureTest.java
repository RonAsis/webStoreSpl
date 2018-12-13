import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;

public class FutureTest {
    private Future<DeliveryVehicle> futureDeliveryVehicle;
    private Future<OrderReceipt> futureOrderReceipt;
    private Future<BookInventoryInfo> futureBookInventoryInfo;

    @Before
    public void setUp() throws Exception {
        try {
            this.futureDeliveryVehicle = new Future<DeliveryVehicle>();
            this.futureOrderReceipt = new Future<OrderReceipt>();
            this.futureBookInventoryInfo = new Future<BookInventoryInfo>();
        }
        catch (Exception e){
            System.out.println("Can't create the 'Future' objects.");
        }
    }

    @After
    public void tearDown() throws Exception {
        // no need
    }

    @Test
    public void get() { // it's not possible to check if the function waits until "result" is available
        ResolveAndGet();
    }

    @Test
    public void resolve() {
        this.futureDeliveryVehicle.resolve(new DeliveryVehicle(123,90));
        this.futureOrderReceipt.resolve(new OrderReceipt());
        this.futureBookInventoryInfo.resolve(new BookInventoryInfo("Hidden Figures", 10, 99));
    }
    private void ResolveAndGet(){
       resolve();
        assertNotNull("should not be null", this.futureDeliveryVehicle.get());
        assertNotNull("should not be null", this.futureOrderReceipt.get());
        assertNotNull("should not be null", this.futureBookInventoryInfo.get());
    }

    @Test
    public void isDone() {

        assertFalse(this.futureDeliveryVehicle.isDone());
        assertFalse(this.futureOrderReceipt.isDone());
        assertFalse(this.futureBookInventoryInfo.isDone());

        resolve();
        assertTrue(this.futureDeliveryVehicle.isDone());
        assertTrue(this.futureOrderReceipt.isDone());
        assertTrue(this.futureBookInventoryInfo.isDone());
    }

    @Test
    public void get1() {

        assertNull("should be null", this.futureDeliveryVehicle .get(100, TimeUnit.MILLISECONDS));
        assertNull("should be null", this.futureOrderReceipt .get(100, TimeUnit.MILLISECONDS));
        assertNull("should be null", this.futureBookInventoryInfo .get(100, TimeUnit.MILLISECONDS));

        resolve();
        assertNotNull("shouldn't be null", this.futureDeliveryVehicle.get(100, TimeUnit.MILLISECONDS));
        assertNotNull("shouldn't be null", this.futureOrderReceipt.get(100, TimeUnit.MILLISECONDS));
        assertNotNull("shouldn't be null", this.futureBookInventoryInfo.get(100, TimeUnit.MILLISECONDS));
    }
}