package org.example;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public interface DetermineIndexSizeService {

    public void findOverhead(String url, List<String> indexArray, int numOfDocs, String field) throws JSONException;

    public void findIndexSize(String url, List<String> indexArray, int numOfDocs, boolean permutation) throws JSONException, IOException;

    public void indexDiagnosis(String path, String databaseName, String collectionName) throws JSONException, IOException;
}
