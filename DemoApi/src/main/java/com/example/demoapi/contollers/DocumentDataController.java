package com.example.demoapi.contollers;

import com.example.demoapi.services.DetermineDocumentSize;
import com.example.demoapi.services.DetermineIndexSize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DocumentDataController {

    @Autowired
    private DetermineDocumentSize determineDocumentSize;

    @Autowired
    private DetermineIndexSize determineIndexSize;

    @GetMapping("doc/overhead")
    public void GetOverhead(@RequestParam("url") String url) {

        determineDocumentSize.findOverhead(url);
    }

    @GetMapping("/doc")
    public void GetDocumentSize(@RequestParam("url") String url) {

        determineDocumentSize.findDocumentSize(url);
    }

    @GetMapping("/index")
    public void GetIndexSize(@RequestParam("url") String url) {

        determineIndexSize.findIndexSize(url);
    }

    @GetMapping("/index/diagnosis")
    public void GetIndexDiagnosis(@RequestParam("url") String url) {

        determineIndexSize.indexDiagnosis(url);
    }

    @GetMapping("/index/overhead")
    public void GetIndexOverhead(@RequestParam("url") String url) {

        determineIndexSize.findOverhead(url);
    }
}
