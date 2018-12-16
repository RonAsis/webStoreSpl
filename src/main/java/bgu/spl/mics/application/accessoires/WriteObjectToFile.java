package bgu.spl.mics.application.accessoires;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class WriteObjectToFile {

    public WriteObjectToFile(){ }

    /**
     * write the Object into file calls nameFile
     * @param nameFile
     * @param object
     */
    public  void printToFile(String nameFile,Object object){
        try {
            FileOutputStream fos = new FileOutputStream(nameFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            oos.close();
            fos.close();
            System.out.printf("Serialized HashMap data is saved in hashmap.ser");
        }catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
