package bgu.spl.mics.application;

import bgu.spl.mics.application.accessoires.ReadJson;
import bgu.spl.mics.application.accessoires.RunnerTherds;
import bgu.spl.mics.application.accessoires.WriteObjectToFile;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;

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

