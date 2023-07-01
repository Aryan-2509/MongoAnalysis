package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.json.JSONException;

import java.io.IOException;

public class Main {
    public static MongoClient client = MongoClients.create("mongodb://localhost:27017");

    public static void main(String[] args) throws JSONException, IOException {

        //CALCULATE DOCUMENT SIZE
        //DetermineDocumentSize det = new DetermineDocumentSize();
        //det.findDocumentSize("JSON FILE PATH");

        //DETERMINE INDEX SIZE OF ALL PERMUTATIONS OVER GIVEN FIELDS
        //DetermineIndexSizeJSON obj = new DetermineIndexSizeJSON();
        //obj.findIndexSize(JSON FILE PATH,DATABASE NAME,COLLECTION NAME,INDEX ARRAY,NUMBER OF DOCUMENTS);
        //obj.findOverhead(JSON FILE PATH,DATABASE NAME,COLLECTION NAME,INDEX ARRAY,NUMBER OF DOCUMENTS,FIELD NAME);
    }
}