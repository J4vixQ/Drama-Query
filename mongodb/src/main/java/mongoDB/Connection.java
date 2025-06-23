package mongoDB;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.Document;
import com.mongodb.ReadPreference;

public class Connection {


    //static String uri = "mongodb+srv://chufan:2025@dramatext.pzpbe8l.mongodb.net/?retryWrites=true&w=majority&appName=dramaText";

    //private static MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");

    static final String uri =
            "mongodb://chufan:2025@ac-qskqadq-shard-00-00.pzpbe8l.mongodb.net:27017," +
                    "ac-qskqadq-shard-00-01.pzpbe8l.mongodb.net:27017," +
                    "ac-qskqadq-shard-00-02.pzpbe8l.mongodb.net:27017/dramaDatabase" +
                    "?ssl=true&authSource=admin&retryWrites=true&w=majority";
    static MongoClient mongoClient = MongoClients.create(uri);

    static MongoDatabase database = mongoClient.getDatabase("dramaDatabase");


    static MongoCollection<Document> collection = database.getCollection("texts").withReadPreference(ReadPreference.secondaryPreferred());

    public static MongoCollection<Document> getCollection() {
        return collection;
    }

    public static MongoDatabase getDatabase() {
        return database;
    }

    public static void close() {
        mongoClient.close();
    }




}
