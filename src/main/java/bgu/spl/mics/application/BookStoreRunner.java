package bgu.spl.mics.application;

import bgu.spl.mics.application.accessoires.ReadJson;
import bgu.spl.mics.application.accessoires.RunnerTherds;
import bgu.spl.mics.application.accessoires.WriteObjectToFile;
import bgu.spl.mics.application.passiveObjects.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    private static String []nameFiles = new String [4];//customersNameFile, booksNameFile, orderReceiptsNameFile, moneyRegisterNameFile
    private static ReadJson readJson;

    public static void main(String[] args) {
        if (args.length<5)
            throw  new IllegalArgumentException("Need 5 args have only: "+args.length);
        nameFiles(args);
        readJson=new ReadJson(args[0]);
        RunnerTherds runnerTherds=new RunnerTherds(readJson.getThreads());
        runnerTherds.runner();
        printTofiles();
        try {
            List<OrderReceipt> or=( List<OrderReceipt>) getReceipts(args[3]);
            System.out.println("amount of orders "+or.size());
            HashMap<Integer, Customer> customerHashMap=(HashMap<Integer, Customer>)getReceipts(args[1]);
            HashMap<String,BookInventoryInfo> booksInventory=(HashMap<String,BookInventoryInfo>)getReceipts(args[2]);
           MoneyRegister moneyRegister=(MoneyRegister)getReceipts(args[4]);
            System.out.println("amount of orders "+or.size());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Object getReceipts(String receiptsObj)
            throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(receiptsObj);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        Object receipts =  in.readObject();
        in.close();
        fileIn.close();
        return receipts;
    }
    private static void printTofiles(){
        WriteObjectToFile writeObjectToFile=new WriteObjectToFile();
        writeObjectToFile.printToFile(nameFiles[0], readJson.getCustomerHashMap());
        Inventory.getInstance().printInventoryToFile(nameFiles[1]);
        MoneyRegister.getInstance().printOrderReceipts(nameFiles[2]);
        writeObjectToFile.printToFile(nameFiles[3], MoneyRegister.getInstance());
    }

    private  static void nameFiles(String args[]){
        for(int i=1;i<5;i++){
            nameFiles[i-1]=args[i];
        }
    }
}

