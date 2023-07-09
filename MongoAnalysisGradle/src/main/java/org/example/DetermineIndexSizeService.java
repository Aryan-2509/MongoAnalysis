package org.example;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public interface DetermineIndexSizeService {

    public List<IndexOverhead> findOverhead(String url, List<String> indexArray, int numOfDocs, String field) throws JSONException;

    public List<Index> findIndexSize(String url, List<String> indexArray, int numOfDocs, boolean permutation) throws JSONException, IOException;

    public List<FaultyIndex> indexDiagnosis(String path, String databaseName, String collectionName) throws JSONException, IOException;
}
