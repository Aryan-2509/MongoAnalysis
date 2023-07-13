package com.example.demoapi.services;

import org.json.JSONException;
import java.io.IOException;
import java.util.List;

public interface DetermineIndexSizeService {

    public void findOverhead(String url) throws JSONException;

    public void findIndexSize(String url) throws JSONException, IOException;

    public void indexDiagnosis(String path) throws JSONException, IOException;
}