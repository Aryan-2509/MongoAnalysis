package org.example;

public class Index implements Comparable<Index>{
    String name;
    int size;
    boolean sparse;

    public Index(){}
    public Index(String name, int size,boolean sparse) {
        this.name = name;
        this.size = size;
        this.sparse = sparse;
    }

    @Override
    public int compareTo(Index that) {
        if(this.size > that.size)
            return 1;
        return -1;
    }
}
