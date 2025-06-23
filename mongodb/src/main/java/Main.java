import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import mongoDB.Connection;

import org.bson.Document;
import com.mongodb.client.MongoCollection;

import java.io.File;

public class Main {
    public static void main(String[] args) {

        MongoCollection<Document> collection = Connection.getCollection();

        File folder = new File("data");
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"));

        if (files == null || files.length == 0) {
            System.out.println("No xml file...");
        } else {
            for (File file : files) {
                Document doc = ReadData.readXML(file.getPath());

                if (doc != null) {
                    collection.insertOne(doc);
                    System.out.println("Well done!");
                } else {
                    System.out.println("Oh, no no no...");
                }
            }
        }


        //Document doc = ReadData.readXML("data/alexander-croesus.xml");

        Connection.close();

    }
}