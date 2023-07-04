package org.example;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetermineIndexSizeJSON extends Main{

    static MongoDatabase database = client.getDatabase("sprinklr");
    static String collectionName = "test2";
    static MongoCollection<Document> collection = database.getCollection(collectionName);
    private static List<List<String>> getPermutations(List<String> strings) {
        List<List<String>> result = new ArrayList<>();
        backtrack(strings, new ArrayList<>(), result);
        return result;
    }
    private static void backtrack(List<String> strings, List<String> currentPermutation, List<List<String>> result) {
        if (currentPermutation.size() == strings.size()) {
            result.add(new ArrayList<>(currentPermutation));
            return;
        }

        for (String str : strings) {
            if (!currentPermutation.contains(str)) {
                currentPermutation.add(str);
                backtrack(strings, currentPermutation, result);
                currentPermutation.remove(currentPermutation.size() - 1);
            }
        }
    }

    private static boolean checkSparse(JSONArray jsonArray,ArrayList<String> indexArray) throws JSONException {
        int size = jsonArray.length();
        int indexArraySize = indexArray.size();
        ArrayList<Integer> population = new ArrayList<>(Collections.nCopies(indexArraySize, 0));

        for(int i = 0 ; i < size ; i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            for(int j = 0 ; j < indexArraySize ; j++) {
                if(jsonObject.has(indexArray.get(j))){
                    int currentPopulation = population.get(j);
                    currentPopulation++;
                    population.set(j,currentPopulation);
                }
            }
        }

        for(int pop : population) {
            if(pop != size){
                return true;
            }
        }
        return false;
    }

    public void findOverhead(String url,ArrayList<String> indexArray,int numOfDocs,String field) throws IOException, JSONException {

        ArrayList<ArrayList<String>> permutations = new ArrayList<>();
        ArrayList<Index> allIndexes = new ArrayList<>();
        int indexArraySize = indexArray.size();
        ReadLink read = new ReadLink();
        JSONArray jsonArray = read.getJSONArray(url);
        int originalSize = 0;
        int n;
        int N;

        permutations.add(indexArray);

        for(int pos = 0 ; pos <= indexArraySize ; pos++){
            ArrayList<String> index = new ArrayList<>();

            for(int j = 0 ; j < indexArraySize ; j++){
                if(j == pos){
                    index.add(field);
                }
                index.add(indexArray.get(j));
            }

            if(pos == indexArraySize){
                index.add(field);
            }
            permutations.add(index);
        }

        boolean sparse = checkSparse(jsonArray,permutations.get(1));

        for (int i = 0 ; i < permutations.size() ; i++){
            String indexName = "";
            ArrayList<String> index = permutations.get(i);
            Document indexDocument = new Document();

            for(String attribute : index) {
                indexDocument.append(attribute,1);
                indexName += attribute + "_1_";
            }
            indexName = indexName.substring(0,indexName.length()-1);

            n = numberOfDocuments(indexDocument,indexName,sparse,jsonArray);
            if(n == -1) {
                System.out.println("Insufficient Documents");
                return;
            }else{
                N = numOfDocs;
                int indexSize = (int) ((Math.ceil((double)N/n) + 4)*4096);
                if(i == 0){
                    originalSize = indexSize;
                }else{
                    Index ind = new Index(indexName,indexSize,sparse);
                    allIndexes.add(ind);
                }
            }
        }

        Collections.sort(allIndexes);
        for(Index index : allIndexes) {
            System.out.println(index.name + " : Overhead : " + (index.size - originalSize) + " bytes");
        }
    }

    public ArrayList<Index> findIndexSize(String url,ArrayList<String> indexArray,int numOfDocs,boolean permutation) throws JSONException, IOException{
        ReadLink read = new ReadLink();
        JSONArray jsonArray = read.getJSONArray(url);

        return findIndexSizeHelper(url,indexArray,numOfDocs,permutation,jsonArray);
    }

    public ArrayList<Index> findIndexSizeHelper(String url,ArrayList<String> indexArray,int numOfDocs,boolean permutation,JSONArray jsonArray) throws JSONException, IOException {
        ArrayList<Index> allIndexes = new ArrayList<>();
        boolean sparse = checkSparse(jsonArray,indexArray);
        List<List<String>> permutations = new ArrayList<>();
        int n;
        int N;

        if(permutation){
            permutations = getPermutations(indexArray);
        }else{
            permutations.add(indexArray);
        }

        for(List<String> index : permutations) {
            String indexName = "";
            Document indexDocument = new Document();

            for(String attribute : index) {
                indexDocument.append(attribute,1);
                indexName += attribute + "_1_";
            }

            indexName = indexName.substring(0,indexName.length()-1);
            n = numberOfDocuments(indexDocument,indexName,sparse,jsonArray);

            if(n == -1){
                System.out.println("Insufficient Documents");
                return new ArrayList<>();
            }else{
                N = numOfDocs;
                int indexSize = (int) ((Math.ceil((double)N/n) + 4)*4096);
                Index ind = new Index(indexName,indexSize,sparse);
                allIndexes.add(ind);
            }
        }

        Collections.sort(allIndexes);
        return allIndexes;
    }

    public int numberOfDocuments(Document indexDocument,String indexName,boolean sparse,JSONArray jsonArray) throws JSONException {
        int numOfDocs = 0;
        int i = 0;
        int n = jsonArray.length();
        int prev = 20480;
        int current = 20480;

        while (current <= prev && i < n) {
            numOfDocs++;

            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Document document = Document.parse(jsonObject.toString());
            collection.insertOne(document);

            if(sparse){
                collection.createIndex(indexDocument,new IndexOptions().sparse(true));
            }else{
                collection.createIndex(indexDocument);
            }

            Document stats = database.runCommand(new Document("collStats",collectionName).append("indexDetails", true));
            Document indexDetails = (Document) stats.get("indexSizes");
            current = indexDetails.getInteger(indexName);
            collection.dropIndex(indexDocument);

            i++;
        }

        if(i >= n) {
            return -1;
        }
        collection.deleteMany(new Document());
        return numOfDocs-2;
    }

    public void indexDiagnosis(String path, String databaseName, String collectionName) throws JSONException, IOException {

        MongoDatabase database = client.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName);
        List<Document> indexes = (List<Document>) collection.listIndexes().into(new ArrayList<>());
        Document stats = database.runCommand(new Document("collStats", collectionName).append("indexDetails", true));
        int numOfDocs = stats.getInteger("count");
        Document indexSizes = (Document) stats.get("indexSizes");
        Document key;
        String indexName;
        ArrayList<String> indexFields;
        ArrayList<Index> idealIndex;
        int currentIndexSize;
        int idealIndexSize;
        ReadLink read = new ReadLink();
        JSONArray jsonArray = read.getJSONArray(path);

        for (Document index : indexes) {
            indexName = index.getString("name");
            if(indexName.equals("_id_")){
                continue;
            }
            key = (Document) index.get("key");
            indexFields = new ArrayList<>(key.keySet());
            currentIndexSize = indexSizes.getInteger(indexName);
            idealIndex = findIndexSizeHelper(path,indexFields,numOfDocs,false,jsonArray);

            if(idealIndex.size() == 0){
                System.out.println("Insufficient Documents");
                return;
            }
            idealIndexSize = idealIndex.get(0).size;
            if(currentIndexSize > 1.5*idealIndexSize){
                System.out.println(indexName + " : current size = " + currentIndexSize + " bytes, expected index size : " + idealIndexSize + " bytes");
            }
        }
    }
}
