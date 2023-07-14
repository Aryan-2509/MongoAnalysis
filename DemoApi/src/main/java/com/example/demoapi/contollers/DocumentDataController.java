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

    //localhost:8080/doc?url=https://raw.githubusercontent.com/Aryan-2509/Data/master/1.json
    @GetMapping("/doc")
    public void GetDocumentSize(@RequestParam("url") String url) {

        determineDocumentSize.findDocumentSize(url);
    }

    @GetMapping("doc/overhead")
    public void GetOverhead(@RequestParam("url") String url) {

        determineDocumentSize.findOverhead(url);
    }

    //localhost:8080/index?url=https://raw.githubusercontent.com/Aryan-2509/Data/master/findIndexSizeInput.json
    @GetMapping("/index")
    public void GetIndexSize(@RequestParam("url") String url) {

        determineIndexSize.findIndexSize(url);
    }

    //localhost:8080/index/diagnosis?url=https://raw.githubusercontent.com/Aryan-2509/Data/master/indexDiagnosisInput.json
    @GetMapping("/index/diagnosis")
    public void GetIndexDiagnosis(@RequestParam("url") String url) {

        determineIndexSize.indexDiagnosis(url);
    }

    //localhost:8080/index/overhead?url=https://raw.githubusercontent.com/Aryan-2509/Data/master/findIndexOverhead.json
    @GetMapping("/index/overhead")
    public void GetIndexOverhead(@RequestParam("url") String url) {

        determineIndexSize.findOverhead(url);
    }
}
