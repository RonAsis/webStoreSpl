package bgu.spl.mics.application;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import bgu.spl.mics.application.passiveObjects.Pair;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    private static String []nameFiles=new String [4];//customersNameFile, booksNameFile, orderReceiptsNameFile, moneyRegisterNameFile
    private static ConcurrentLinkedQueue< Thread > threads;
    private static HashMap<Integer,Customer> customerHashMap;
    public static void main(String[] args) {
        if (args.length<5)
            throw  new IllegalArgumentException("Need 5 args have only: "+args.length);
        nameFiles(args);
        customerHashMap=new HashMap<>();
        threads=new ConcurrentLinkedQueue<>();
        ObjectMapper mapper = new ObjectMapper();
        MapType type = mapper.getTypeFactory().constructMapType(
                Map.class, String.class, Object.class);
        Map<String, Object> data=null;
        try {
            data = mapper.readValue(new File(args[0]), type);
            initialInventory(data.get("initialInventory"));
            initialResources(data.get("initialResources"));
            services(data.get("services"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        runner();
        terminate();
    }
    private static void terminate(){
        printToFile(nameFiles[0], customerHashMap);
        Inventory.getInstance().printInventoryToFile(nameFiles[1]);
        MoneyRegister.getInstance().printOrderReceipts(nameFiles[2]);
        printToFile(nameFiles[3], MoneyRegister.getInstance());


    }
    public static void printToFile(String nameFile,Object object){
        try
        {
            FileOutputStream fos =
                    new FileOutputStream(nameFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            oos.close();
            fos.close();
            System.out.printf("Serialized HashMap data is saved in hashmap.ser");
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
    private static void runner(){
        for(Thread t:threads){
            t.start();
        }
        for(Thread t:threads){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
private  static void nameFiles(String args[]){
        for(int i=1;i<5;i++){
            nameFiles[i-1]=args[i];
        }
    }
private static void initialInventory(Object objecetInventory){
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

private static void initialResources(Object objectResuorces){
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
private static void services(Object objectServices){
    LinkedHashMap<String,LinkedHashMap<String,Object>> mapServices= ( LinkedHashMap<String,LinkedHashMap<String,Object>>)objectServices;
    sellingService(mapServices.get("selling"));
    inventoryService(mapServices.get("inventoryService"));
    logisticsService(mapServices.get("logistics"));
    resourcesService(mapServices.get("resourcesService"));
    customers(mapServices.get("customers"));
    TimeService(mapServices.get("time"));
}
private static void addThread(MicroService microService){
    Thread tempThread=new Thread(microService);
    threads.add(tempThread);
}

private static void TimeService(LinkedHashMap<String,Object> mapTime){
    MicroService microService=new TimeService((Integer)mapTime.get("speed"),(Integer)mapTime.get("duration"));
    addThread(microService);
}
private static void sellingService(Object object){
        int size=(Integer)object;
        for (int i =1;i<=size;i++){
            MicroService microService=new SellingService("selling "+i);
            addThread(microService);
        }
}
private  static void inventoryService(Object object){
    int size=(Integer)object;
    for (int i =1;i<=size;i++){
        MicroService microService=new InventoryService( "inventoryService "+i);
        addThread(microService);
    }
}
private  static void logisticsService(Object object){
    int size=(Integer)object;
    for (int i =1;i<=size;i++){
        MicroService microService=new LogisticsService( "logistic "+i);
        addThread(microService);
    }
}
private static void resourcesService(Object object){
    int size=(Integer)object;
    for (int i =1;i<=size;i++){
        MicroService microService=new ResourceService( "resource "+i);
        addThread(microService);
    }
}
private static void customers(Object object){
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

