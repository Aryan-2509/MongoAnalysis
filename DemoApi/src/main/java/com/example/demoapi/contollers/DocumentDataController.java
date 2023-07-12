package com.example.demoapi.contollers;

import com.example.demoapi.services.DetermineDocumentSize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DocumentDataController {

    @Autowired
    private DetermineDocumentSize determineDocumentSize;

    @GetMapping("/overhead")
    public void GetOverhead(@RequestParam("url") String url) {

        determineDocumentSize.findOverhead(url);
    }

    @GetMapping("/doc")
    public void GetDocumentSize(@RequestParam("url") String url) {

        determineDocumentSize.findDocumentSize(url);
    }
}
