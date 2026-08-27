package me.pan_truskawka045.Slither.mongodb;

import me.pan_truskawka045.Slither.util.ProcessUtil;
import me.pan_truskawka045.injector.Module;

public class MongoDBModule extends Module {

    @Override
    public void init() {
        String uri = ProcessUtil.getValue("MONGODB_URI", "mongodbUri", "mongodb://127.0.0.1:27017");
        String database = ProcessUtil.getValue("MONGODB_DB", "mongodbDatabase", "slither");

        register(new MongoDBService(uri, database));
    }
}
