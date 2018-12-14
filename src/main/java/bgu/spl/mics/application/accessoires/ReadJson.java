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
    private ConcurrentLinkedQueue< Thread > threads;
    private HashMap<Integer,Customer> customerHashMap;

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

    public HashMap<Integer,Customer> getCustomerHashMap(){
        return this.customerHashMap;
    }

    public ConcurrentLinkedQueue< Thread > getThreads(){
        return this.threads;
    }

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

    private void services(Object objectServices){
        LinkedHashMap<String, LinkedHashMap<String,Object>> mapServices= ( LinkedHashMap<String,LinkedHashMap<String,Object>>)objectServices;
        sellingService(mapServices.get("selling"));
        inventoryService(mapServices.get("inventoryService"));
        logisticsService(mapServices.get("logistics"));
        resourcesService(mapServices.get("resourcesService"));
        customers(mapServices.get("customers"));
        TimeService(mapServices.get("time"));
    }

    private void addThread(MicroService microService){
        Thread tempThread=new Thread(microService);
        threads.add(tempThread);
    }

    private void TimeService(LinkedHashMap<String,Object> mapTime){
        MicroService microService=new TimeService((Integer)mapTime.get("speed"),(Integer)mapTime.get("duration"));
        addThread(microService);
    }

    private void sellingService(Object object){
        int size=(Integer)object;
        for (int i =1;i<=size;i++){
            MicroService microService=new SellingService("selling "+i);
            addThread(microService);
        }
    }

    private void inventoryService(Object object){
        int size=(Integer)object;
        for (int i =1;i<=size;i++){
            MicroService microService=new InventoryService( "inventoryService "+i);
            addThread(microService);
        }
    }

    private void logisticsService(Object object){
        int size=(Integer)object;
        for (int i =1;i<=size;i++){
            MicroService microService=new LogisticsService( "logistic "+i);
            addThread(microService);
        }
    }

    private void resourcesService(Object object){
        int size=(Integer)object;
        for (int i =1;i<=size;i++){
            MicroService microService=new ResourceService( "resource "+i);
            addThread(microService);
        }
    }

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
