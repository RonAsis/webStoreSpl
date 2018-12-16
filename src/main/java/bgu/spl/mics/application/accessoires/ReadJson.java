package bgu.spl.mics.application.accessoires;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ReadJson {
    private ConcurrentLinkedQueue< Thread > threads;//for start all the thread the same time and when end play the timer. Also for do Join all
    private HashMap<Integer,Customer> customerHashMap;//for print in the end of the program the HashMap<Integer,Customer> to file

    /**
     * constructor read the input.json that get the path of him and start to initial all the Object in the program
     * @param path
     */
    public ReadJson(String path){
        customerHashMap=new HashMap<>();
        threads=new ConcurrentLinkedQueue<>();
        ObjectMapper mapper = new ObjectMapper();
        MapType type = mapper.getTypeFactory().constructMapType(
                Map.class, String.class, Object.class);
        Map<String, Object> data=null;
        try {
            data = mapper.readValue(new File(path), type);
            initialInventory(data.get("initialInventory"));
            initialResources(data.get("initialResources"));
            services(data.get("services"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return HashMap<Integer,Customer>
     */
    public HashMap<Integer,Customer> getCustomerHashMap(){
        return this.customerHashMap;
    }

    /**
     * @return ConcurrentLinkedQueue< Thread >
     */
    public ConcurrentLinkedQueue< Thread > getThreads(){
        return this.threads;
    }

    /**
     * initial the Inventory
     * @param objecetInventory contain the Inventory
     */
    private void initialInventory(Object objecetInventory){
        Inventory inventory=Inventory.getInstance();
        ArrayList<Map<String,Object>> mapInventory=(ArrayList<Map<String,Object>>)objecetInventory;
        BookInventoryInfo[] bookInventoryInfos=new BookInventoryInfo[mapInventory.size()];
        int i=0;
        Iterator it= mapInventory.iterator();
        while(it.hasNext()){
            if(i<bookInventoryInfos.length) {
                Map<String,Object> book=(Map<String,Object>)it.next();
                bookInventoryInfos[i]=new BookInventoryInfo((String)book.get("bookTitle"),(Integer)book.get("amount"),(Integer)book.get("price"));
                i++;
            }
        }
        inventory.load(bookInventoryInfos);
    }

    /**
     * initial the resources
     * @param objectResuorces
     */
    private void initialResources(Object objectResuorces){
        ResourcesHolder resourcesHolder=ResourcesHolder.getInstance();
        ArrayList<Map<String,Object>> mapResources=(ArrayList<Map<String,Object>>)objectResuorces;
        Map<String,Object> map=mapResources.get(0);
        ArrayList<Map<String,Object>> arrayList=( ArrayList<Map<String,Object>>)map.get("vehicles");
        DeliveryVehicle[] vehicles=new DeliveryVehicle[arrayList.size()];
        int i=0;
        Iterator it= arrayList.iterator();
        while(it.hasNext()){
            if(i<vehicles.length) {
                Map<String,Object> cars=(Map<String,Object>)it.next();
                vehicles[i]=new DeliveryVehicle((Integer)cars.get("license"),(Integer)cars.get("speed"));
                i++;
            }
        }
        resourcesHolder.load(vehicles);
    }

    /**
     * initial the services
     * @param objectServices
     */
    private void services(Object objectServices){
        LinkedHashMap<String, LinkedHashMap<String,Object>> mapServices= ( LinkedHashMap<String,LinkedHashMap<String,Object>>)objectServices;
        sellingService(mapServices.get("selling"));
        inventoryService(mapServices.get("inventoryService"));
        logisticsService(mapServices.get("logistics"));
        resourcesService(mapServices.get("resourcesService"));
        customers(mapServices.get("customers"));
        TimeService(mapServices.get("time"));
    }

    /**
     * create new Thread of MicroService and add him to threads
     * @param microService
     */
    private void addThread(MicroService microService){
        Thread tempThread=new Thread(microService);
        threads.add(tempThread);
    }

    /**
     * Create the thread for the time service
     * @param mapTime
     */
    private void TimeService(LinkedHashMap<String,Object> mapTime){
        MicroService microService=new TimeService((Integer)mapTime.get("speed"),(Integer)mapTime.get("duration"));
        addThread(microService);
    }

    /**
     * create the server of sellingService
     * @param object
     */
    private void sellingService(Object object){
        int size=(Integer)object;
        for (int i =1;i<=size;i++){
            MicroService microService=new SellingService("selling "+i);
            addThread(microService);
        }
    }

    /**
     * create the server of inventoryService
     * @param object
     */
    private void inventoryService(Object object){
        int size=(Integer)object;
        for (int i =1;i<=size;i++){
            MicroService microService=new InventoryService( "inventoryService "+i);
            addThread(microService);
        }
    }
    /**
     * create the server of logisticsService
     * @param object
     */
    private void logisticsService(Object object){
        int size=(Integer)object;
        for (int i =1;i<=size;i++){
            MicroService microService=new LogisticsService( "logistic "+i);
            addThread(microService);
        }
    }
    /**
     * create the server of resourcesService
     * @param object
     */
    private void resourcesService(Object object){
        int size=(Integer)object;
        for (int i =1;i<=size;i++){
            MicroService microService=new ResourceService( "resource "+i);
            addThread(microService);
        }
    }

    /**
     * create the object of Customer and add him
     * @param object
     */
    private void customers(Object object){
        ArrayList arrayList=(ArrayList)object;
        Iterator it= arrayList.iterator();
        int numberName=1;
        while(it.hasNext()){
            Map<String,Object> apiService=(Map<String,Object>)it.next();
            Map<String,Object> creditCard=(Map<String,Object>)apiService.get("creditCard");
            Customer customer=new Customer((Integer)apiService.get("id"),(String)apiService.get("name"),(String)apiService.get("address"),(Integer)apiService.get("distance"),(Integer)creditCard.get("number"),(Integer)creditCard.get("amount"));
            customerHashMap.put(customer.getId(),customer);
            ArrayList  orderSchedule=(ArrayList)apiService.get("orderSchedule");
            Iterator itOrderSchedule= orderSchedule.iterator();
            List<Pair> bookAndTick=new ArrayList<Pair>();
            while(itOrderSchedule.hasNext()){
                Map<String,Object> mapOrderSchedule=(Map<String,Object>)itOrderSchedule.next();
                bookAndTick.add(new Pair((String)mapOrderSchedule.get("bookTitle"),(Integer)mapOrderSchedule.get("tick")));
            }
            MicroService microService=new APIService("Customer "+numberName,customer,bookAndTick);
            numberName++;
            addThread(microService);
        }
    }
}
