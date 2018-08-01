package com.spear.canslim.database;

import java.time.LocalDate;
import java.util.Map;

public  class DataAdder {
    String collection;
    String document;
    Map<String, Object> data;
    Runnable listener;
    String dateStamp;

    private DataAdder() {
        dateStamp = LocalDate.now().toString();

    }

    public static DataAdder newInstance() {
        return new DataAdder();
    }

    public DataAdder collection(String collection) {
        this.collection = collection;
        return this;
    }

    public DataAdder document(String document) {
        this.document = document;
        return this;
    }

    public DataAdder data(Map<String, Object> data) {
        this.data = data;
        return this;
    }

    public DataAdder after(Runnable listener) {
        this.listener = listener;
        return this;
    }

    public void setData() {
        Database.getDatabase().addData(collection, document, data);
    }

    public void mergeData() {
        Database.getDatabase().updateData(collection, document, data);

    }

}
