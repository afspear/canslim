package com.spear.canslim.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.Filters.eq;

public enum MongoDB implements Database {

    MONGO_DB;

    MongoDatabase database;

    MongoDB() {

        MongoClient mongoClient =
                Optional.ofNullable(System.getenv("MONGODB_URI"))
                    .map(s -> MongoClients.create(s))
                    .orElse(MongoClients.create());

        
        database = mongoClient.getDatabase("canslim");
        mongoClient.listDatabaseNames().forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println(s);
            }
        });

    }

    @Override
    public void consumeCollection(String path, Consumer<Map<String, Object>> dataConsumer) {
        database.getCollection(path).find().forEach(new Consumer<Document>() {
            @Override
            public void accept(Document document) {
                dataConsumer.accept(document);
            }
        });
    }

    @Override
    public List<Map<String, Object>> getCollection(String collectionName) {

        Iterator<Document> documentIterator = database.getCollection(collectionName).find().iterator();
        Iterable<Document> iterable = () -> documentIterator;
        return StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getDocument(String collectionPath, String documentName) {


        return database.getCollection(collectionPath).find(eq("_id", documentName)).first();
    }

    @Override
    public void addData(String collectionPath, String documentPath, Map<String, Object> data) {


        try {
            database.getCollection(collectionPath).insertOne(new Document(data).append("_id", documentPath));
        } catch (Exception e) {
            database.getCollection(collectionPath).deleteOne(new Document().append("_id", documentPath));
            database.getCollection(collectionPath).insertOne(new Document(data).append("_id", documentPath));

        }




    }

    public void updateData(String collectionPath, String documentPath, Map<String, Object> data) {


        database.getCollection(collectionPath).updateOne(eq("_id", documentPath), new Document("$set", new Document(data)), new UpdateOptions().upsert(true));

    }
}
