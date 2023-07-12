package org.example;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void ShutdownFunction(){
        DetermineIndexSize object = new DetermineIndexSize();
        object.terminate();
    }

    public static void main(String[] args) {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ShutdownFunction();
        }));

        String url1 = "https://raw.githubusercontent.com/Aryan-2509/Data/master/1.json";
        String url2 = "https://raw.githubusercontent.com/Aryan-2509/Data/master/sprinklr.employee8.json";
        String url3 = "https://raw.githubusercontent.com/Aryan-2509/Data/master/sprinklr.employee4.json";

        List<String> indexArray1 = new ArrayList<>();
        indexArray1.add("isRead");
        indexArray1.add("senderId");

        DetermineIndexSize obj = new DetermineIndexSize();

//        obj.findIndexSize(url2,indexArray1,5000000,true);
//        obj.findOverhead(url2,indexArray,1000000,"senderName");
//        obj.indexDiagnosis(url3,"sprinklr","employee4");
    }
}