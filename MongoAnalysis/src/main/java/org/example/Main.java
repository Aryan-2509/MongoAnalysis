package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class Main {
    public static MongoClient client = MongoClients.create("mongodb://localhost:27017");
    public static void main(String[] args) {

        //CALCULATE DOCUMENT SIZE
//        DetermineDocumentSize det = new DetermineDocumentSize();
//        det.findDocumentSize(URL OF THE DOCUMENT);
//        det.findOverhead(URL OF JSON OBJECT);

        //DETERMINE INDEX SIZE OF ALL PERMUTATIONS OVER GIVEN FIELDS
//        DetermineIndexSizeJSON obj = new DetermineIndexSizeJSON();
//        ArrayList<Index> allIndexes = obj.findIndexSize(URL OF DATA,INDEX ARRAY,NUMBER OF DOCUMENTS,PERMUTATIONS REQUIRED OR NOT);
//        obj.findOverhead(URL OF DATA,INDEX ARRAY,NUMBER OF DOCUMENTS,FIELD NAME);
//        obj.indexDiagnosis(URL OF DATA,DATABASE NAME,COLLECTION NAME);
    }
}