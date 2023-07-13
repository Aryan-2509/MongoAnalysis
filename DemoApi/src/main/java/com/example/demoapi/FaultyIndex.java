package com.example.demoapi;

public class FaultyIndex {
    String name;
    int currentSize;
    int idealSize;

    public FaultyIndex() {}
    public FaultyIndex(String name, int currentSize, int idealSize) {
        this.name = name;
        this.currentSize = currentSize;
        this.idealSize = idealSize;
    }
}