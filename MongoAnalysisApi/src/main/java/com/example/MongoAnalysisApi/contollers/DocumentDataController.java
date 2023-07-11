package com.example.MongoAnalysisApi.contollers;

import com.example.MongoAnalysisApi.services.DetermineDocumentSize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DocumentDataController {

    @Autowired
    private DetermineDocumentSize determineDocumentSize;

    @GetMapping("/doc")
    public int GetOverhead(@RequestParam("url") String url) {

        return determineDocumentSize.findOverhead(url);
    }

    @GetMapping("/class")
    public int GetDocumentSize(@RequestParam("classPath") String classPath) {

        return determineDocumentSize.findDocumentSize(classPath);
    }

//    @Autowired
//    private DetermineDocumentSizeService determineDocumentSizeService;
//
//    @GetMapping("/doc")
//    public int GetOverhead(@RequestParam("url") String url) {
//
//        return determineDocumentSizeService.getOverhead(url);
//    }
//
//    @GetMapping("/class")
//    public int GetDocumentSize(@RequestParam("classPath") String classPath) {
//
//        return determineDocumentSizeService.getDocumentSize(classPath);
//    }
}
