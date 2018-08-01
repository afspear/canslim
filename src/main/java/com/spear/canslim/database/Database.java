package com.spear.canslim.database;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface Database {

    static Database getDatabase() {
        return MongoDB.MONGO_DB;
    }

    void consumeCollection(String path, Consumer<Map<String, Object>> dataConsumer);
    List<Map<String, Object>> getCollection(String collectionName);
    Map<String, Object> getDocument(String collectionPath, String documentPath);
    void addData(String collectionPath, String documentPath, Map<String,Object> data);
    void updateData(String collectionPath, String documentPath, Map<String, Object> data);
}
