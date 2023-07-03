package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.json.JSONException;

import java.io.IOException;

public class Main {
    public static MongoClient client = MongoClients.create("mongodb://localhost:27017");
    public static void main(String[] args) throws JSONException, IOException {

//        String path = "/Users/aryantyagi/Documents/MongoDB/field.json";

        //CALCULATE DOCUMENT SIZE
//        DetermineDocumentSize det = new DetermineDocumentSize();
//        det.findDocumentSize(path);
//        det.findOverhead(path);

        //DETERMINE INDEX SIZE OF ALL PERMUTATIONS OVER GIVEN FIELDS
//        DetermineIndexSizeJSON obj = new DetermineIndexSizeJSON();
//        ArrayList<Index> allIndexes = obj.findIndexSize(JSON FILE PATH,INDEX ARRAY,NUMBER OF DOCUMENTS,PERMUTATION REQUIRED OR NOT);
        
//        if(allIndexes.size > 0){
//            for(Index index : allIndexes) {
//                System.out.println(index.name + " : " + index.size + " bytes");
//            }
//        }else{
//            System.out.println("Insufficient documents");
//        }

//        obj.findOverhead(JSON FILE PATH,INDEX ARRAY,NUMBER OF DOCUMENTS,FIELD NAME);
//        obj.indexDiagnosis(JSON FILE PATH,DATABASE NAME,COLLECTION NAME);
    }
}