package me.pan_truskawka045.Slither.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import me.pan_truskawka045.injector.Init;
import org.bson.Document;

@Log4j2
public class MongoDBService {

    private final String uri;
    private final String databaseName;

    @Getter
    private MongoClient mongoClient;
    @Getter
    private MongoDatabase database;

    public MongoDBService(String uri, String databaseName) {
        this.uri = uri;
        this.databaseName = databaseName;
    }

    @Init
    private void init() {
        log.info("Creating MongoDB client for database {}", databaseName);
        mongoClient = MongoClients.create(uri);
        database = mongoClient.getDatabase(databaseName);
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "mongodb-shutdown"));
    }

    public MongoCollection<Document> getCollection(String tableName) {
        return this.getDatabase().getCollection(tableName);
    }

    public void shutdown() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
