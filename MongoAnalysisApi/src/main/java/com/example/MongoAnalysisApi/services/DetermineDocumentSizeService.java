package com.example.MongoAnalysisApi.services;

public interface DetermineDocumentSizeService {

    public int findDocumentSize(String classPath);

    public int findOverhead(String url);
}
