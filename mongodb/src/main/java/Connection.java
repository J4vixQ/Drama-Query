//package mongoDB;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.Document;
import com.mongodb.ReadPreference;

public class Connection {


    //static String uri = "mongodb+srv://chufan:2025@dramatext.pzpbe8l.mongodb.net/?retryWrites=true&w=majority&appName=dramaText";

    //private static MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");

    // MongoDB connection string
    static final String uri =
            "mongodb://chufan:2025@ac-qskqadq-shard-00-00.pzpbe8l.mongodb.net:27017," +
                    "ac-qskqadq-shard-00-01.pzpbe8l.mongodb.net:27017," +
                    "ac-qskqadq-shard-00-02.pzpbe8l.mongodb.net:27017/dramaDatabase" +
                    "?ssl=true&authSource=admin&retryWrites=true&w=majority";
    static MongoClient mongoClient = MongoClients.create(uri);

    // Get the database object named dramaDatabase
    static MongoDatabase database = mongoClient.getDatabase("dramaDatabase");

    // Get specified collection, text
    static MongoCollection<Document> collection = database.getCollection("texts").withReadPreference(ReadPreference.secondaryPreferred());

    // Method to obtain collection objects
    public static MongoCollection<Document> getCollection() {
        return collection;
    }

    // Method to obtain database objects
    public static MongoDatabase getDatabase() {
        return database;
    }

    // Method to close database connection
    public static void close() {
        mongoClient.close();
    }




}
