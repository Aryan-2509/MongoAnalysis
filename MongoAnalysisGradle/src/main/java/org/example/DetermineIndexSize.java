package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DetermineIndexSize implements DetermineIndexSizeService{
    private static final Logger logger = Logger.getLogger(DetermineIndexSize.class.getName());
    private static ExcelWriter excelWriter = new ExcelWriter();
    private static MongoClient client = MongoClients.create("mongodb://localhost:27017");
    private static MongoDatabase database = client.getDatabase("sprinklr");
    private static String collectionName = "test2";
    private static MongoCollection<Document> collection = database.getCollection(collectionName);

    public static void terminate(){
        collection.deleteMany(new Document());
        collection.dropIndexes();
    }

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
            if(str == null){
                logger.log(Level.SEVERE, "ERROR: Field name can't be Null");
                throw new RuntimeException("ERROR: Field name can't be Null");
            }
            if (!currentPermutation.contains(str)) {
                currentPermutation.add(str);
                backtrack(strings, currentPermutation, result);
                currentPermutation.remove(currentPermutation.size() - 1);
            }
        }
    }

    private static boolean checkDatabaseAndCollectionExistence(String databaseName, String collectionName) {
        boolean databaseExists = client.listDatabaseNames().into(new ArrayList<>()).contains(databaseName);
        if (!databaseExists) {
            return false;
        }
        boolean collectionExists = database.listCollectionNames().into(new ArrayList<>()).contains(collectionName);
        return collectionExists;
    }

    boolean areFieldsPresent(JSONArray jsonArray, List<String> indexArray) {
        for (String fieldName : indexArray) {
            if (fieldName == null) {
                logger.log(Level.SEVERE, "ERROR: Field name can't be Null");
                throw new RuntimeException("ERROR: Field name can't be Null");
            }

            boolean isFieldPresent = false;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject document;
                try {
                    document = jsonArray.getJSONObject(i);
                    if (document.has(fieldName)) {
                        isFieldPresent = true;
                        break;
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            if (!isFieldPresent) {
                return false;
            }
        }

        return true;
    }

    boolean checkDuplicateFields(List<String> indexArray){
        HashSet<String> set = new HashSet<>();

        for (String fieldName : indexArray) {
            if (set.contains(fieldName)) {
                return true;
            }
            set.add(fieldName);
        }

        return false;
    }

    private static boolean checkSparse(JSONArray jsonArray, List<String> indexArray) {
        int jsonArraySize = jsonArray.length();
        int indexArraySize = indexArray.size();
        List<Integer> population = new ArrayList<>(Collections.nCopies(indexArraySize, 0));
        JSONObject jsonObject;

        for (int i = 0; i < jsonArraySize; i++) {
            try {
                jsonObject = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                logger.log(Level.SEVERE, "ERROR: ", e);
                throw new RuntimeException(e);
            }

            for (int j = 0; j < indexArraySize; j++) {
                if (jsonObject.has(indexArray.get(j))) {
                    int currentPopulation = population.get(j);
                    currentPopulation++;
                    population.set(j, currentPopulation);
                }
            }
        }

        double thresholdPercentage;
        try {
            thresholdPercentage = Double.parseDouble(System.getProperty("thresholdPercentage", "0.0"));
            if (thresholdPercentage < 0 || thresholdPercentage > 1) {
                logger.log(Level.SEVERE, "ERROR: Invalid threshold percentage value");
                throw new RuntimeException("Invalid threshold percentage value");
            }
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "ERROR: Invalid threshold percentage value", e);
            throw new RuntimeException("Invalid threshold percentage value", e);
        }

        int threshold = (int) (thresholdPercentage * jsonArraySize);

        for (int pop : population) {
            if (pop < threshold) {
                return true;
            }
        }
        return false;
    }

    private static int getIndexSizeCollStats(String indexName){
        Document stats = database.runCommand(new Document("collStats", collectionName).append("indexDetails", true));
        Document indexDetails = (Document) stats.get("indexSizes");
        return indexDetails.getInteger(indexName);
    }

    private int numberOfDocuments(Document indexDocument, String indexName, boolean sparse, JSONArray jsonArray) {
        int numOfDocs = 0;
        int i = 0;
        int n = jsonArray.length();
        int prev = 20480;
        int current = 20480;
        JSONObject jsonObject;

        while (current <= prev && i < n) {
            numOfDocs++;

            try {
                jsonObject = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                terminate();
                logger.log(Level.SEVERE, "ERROR: ", e);
                throw new RuntimeException(e);
            }

            Document document = Document.parse(jsonObject.toString());
            collection.insertOne(document);

            if (sparse) {
                collection.createIndex(indexDocument, new IndexOptions().sparse(true));
            } else {
                collection.createIndex(indexDocument);
            }

            current = getIndexSizeCollStats(indexName);
            collection.dropIndex(indexDocument);
            i++;
        }

        if (i >= n) {
            terminate();
            logger.log(Level.SEVERE, "Insufficient documents");
            throw new RuntimeException("Error: Insufficient documents");
        }
        collection.deleteMany(new Document());
        return numOfDocs - 2;
    }


    private List<Index> findIndexSizeHelper(List<String> indexArray, int numOfDocs, boolean permutation, JSONArray jsonArray) throws JSONException, IOException {
        ArrayList<Index> allIndexes = new ArrayList<>();
        boolean sparse = checkSparse(jsonArray, indexArray);
        List<List<String>> permutations = new ArrayList<>();
        int numOfDocsLocal;

        if (permutation) {
            permutations = getPermutations(indexArray);
        } else {
            permutations.add(indexArray);
        }

        for (List<String> index : permutations) {
            String indexName = "";
            Document indexDocument = new Document();

            for (String attribute : index) {
                indexDocument.append(attribute, 1);
                indexName += attribute + "_1_";
            }

            indexName = indexName.substring(0, indexName.length() - 1);
            numOfDocsLocal = numberOfDocuments(indexDocument, indexName, sparse, jsonArray);
            int indexSize = (int) ((Math.ceil((double) numOfDocs / numOfDocsLocal) + 4) * 4096);
            Index ind = new Index(indexName, indexSize, sparse);
            allIndexes.add(ind);
        }

        Collections.sort(allIndexes);
        return allIndexes;
    }
    // public static IndexColl{

    @Override
    public void findOverhead(String url, List<String> indexArray, int numOfDocs, String field) {
        List<List<String>> permutations = new ArrayList<>();
        ArrayList<IndexOverhead> allIndexes = new ArrayList<>();
        ReadLink read = new ReadLink();
        JSONArray jsonArray = read.getJSONArray(url);
        int indexArraySize = indexArray.size();
        int originalSize = 0;
        int numOfDocsLocal;

        indexArray.add(field);
        boolean fieldsPresent = areFieldsPresent(jsonArray, indexArray);
        boolean duplicateFields = checkDuplicateFields(indexArray);

        if (!fieldsPresent) {
            logger.log(Level.SEVERE, "Error: Field absent from data");
            throw new RuntimeException("Error: Field absent from data");
        }

        if (duplicateFields) {
            logger.log(Level.SEVERE, "Error: Repeated field names found");
            throw new RuntimeException("Error: Repeated field names found");
        }

        indexArray.remove(indexArray.size() - 1);
        permutations.add(indexArray);
        List<String> newIndexArray = new ArrayList<>();
        for(String fieldName : indexArray){
            newIndexArray.add(fieldName);
        }
        newIndexArray.add(field);
        permutations.add(newIndexArray);

//        for (int position = 0; position <= indexArraySize; position++) {
//            ArrayList<String> index = new ArrayList<>();
//
//            for (int j = 0; j < indexArraySize; j++) {
//                if (j == position) {
//                    index.add(field);
//                }
//                index.add(indexArray.get(j));
//            }
//
//            if (position == indexArraySize) {
//                index.add(field);
//            }
//            permutations.add(index);
//        }

        boolean sparse = checkSparse(jsonArray, permutations.get(1));

        for (int i = 0; i < permutations.size(); i++) {
            try {
                List<Index> indexList = findIndexSizeHelper(permutations.get(i), numOfDocs, false, jsonArray);

                if (i == 0) {
                    originalSize = indexList.get(0).size;
                } else {
                    int overhead = indexList.get(0).size - originalSize;
                    String indexName = indexList.get(0).name;
                    IndexOverhead indexOverhead = new IndexOverhead(indexName, overhead);
                    allIndexes.add(indexOverhead);
                }

            } catch (JSONException e) {
                terminate();
                logger.log(Level.SEVERE, "Error: ", e);
                throw new RuntimeException(e);
            } catch (IOException e) {
                terminate();
                logger.log(Level.SEVERE, "Error: ", e);
                throw new RuntimeException(e);
            }
        }

        Collections.sort(allIndexes);
        excelWriter.WriteIndexOverhead(allIndexes);
    }


    // name
    @Override
    public void findIndexSize(String url, List<String> indexArray, int numOfDocs, boolean permutation) {
        ReadLink read = new ReadLink();
        JSONArray jsonArray = read.getJSONArray(url);

        boolean fieldsPresent = areFieldsPresent(jsonArray, indexArray);
        boolean duplicateFields = checkDuplicateFields(indexArray);

        if (!fieldsPresent) {
            logger.log(Level.SEVERE, "ERROR: Field absent from data");
            throw new RuntimeException("ERROR: Field absent from data");
        }

        if (duplicateFields) {
            logger.log(Level.SEVERE, "ERROR: Repeated field names found");
            throw new RuntimeException("ERROR: Repeated field names found");
        }

        try {
            List<Index> allIndexes = findIndexSizeHelper(indexArray, numOfDocs, permutation, jsonArray);
            excelWriter.WriteIndex(allIndexes);
            //return findIndexSizeHelper(indexArray, numOfDocs, permutation, jsonArray);
        } catch (JSONException e) {
            logger.log(Level.SEVERE, "ERROR: ", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "ERROR: ", e);
            throw new RuntimeException(e);
        }
    }


    @Override
    public void indexDiagnosis(String path, String databaseName, String collectionName) {
        boolean databaseAndCollectionExist = checkDatabaseAndCollectionExistence(databaseName, collectionName);
        if (!databaseAndCollectionExist) {
            logger.log(Level.SEVERE, "ERROR: Database or Collection does not exist");
            throw new RuntimeException("ERROR: Database or Collection does not exist");
        }

        MongoDatabase database = client.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName);
        List<Document> indexes = (List<Document>) collection.listIndexes().into(new ArrayList<>());
        Document stats = database.runCommand(new Document("collStats", collectionName).append("indexDetails", true));

        int numOfDocs = stats.getInteger("count");
        Document indexSizes = (Document) stats.get("indexSizes");
        Document key;
        String indexName;
        List<String> indexFields;
        List<Index> idealIndex;
        int currentIndexSize;
        int idealIndexSize;
        ReadLink read = new ReadLink();
        JSONArray jsonArray = read.getJSONArray(path);
        List<FaultyIndex> faultyIndexes = new ArrayList<>();

        for (Document index : indexes) {
            indexName = index.getString("name");
            if (indexName.equals("_id_")) {
                continue;
            }
            key = (Document) index.get("key");
            indexFields = new ArrayList<>(key.keySet());
            currentIndexSize = indexSizes.getInteger(indexName);

            try {
                idealIndex = findIndexSizeHelper(indexFields, numOfDocs, false, jsonArray);
            } catch (JSONException | IOException e) {
                terminate();
                logger.log(Level.SEVERE, "An error occurred", e);
                throw new RuntimeException(e);
            }

            if (idealIndex.size() == 0) {
                return;
            } else {
                idealIndexSize = idealIndex.get(0).size;
                if (currentIndexSize > 1.5 * idealIndexSize) {
                    FaultyIndex faultyIndex = new FaultyIndex(indexName, currentIndexSize, idealIndexSize);
                    faultyIndexes.add(faultyIndex);
                }
            }
        }

        excelWriter.WriteFaultyIndex(faultyIndexes);
    }

}
