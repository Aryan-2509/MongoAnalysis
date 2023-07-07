package org.example;

public class IndexOverhead implements Comparable<IndexOverhead>{
    String name;
    int overhead;

    public IndexOverhead() {}
    public IndexOverhead(String name, int overhead) {
        this.name = name;
        this.overhead = overhead;
    }


    @Override
    public int compareTo(IndexOverhead that) {
        if(this.overhead > that.overhead){
            return 1;
        }
        return -1;
    }
}
