package com.example.MongoAnalysisApi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.util.Date;
import java.util.List;

public class Book {
    @JsonProperty("_id")
    private int id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("isbn")
    private String isbn;

    @JsonProperty("pageCount")
    private int pageCount;

    @JsonProperty("publishedDate")
    @JsonSerialize(using = ToStringSerializer.class)
    private Date publishedDate;

    @JsonProperty("thumbnailUrl")
    private String thumbnailUrl;

    @JsonProperty("shortDescription")
    private String shortDescription;

    @JsonProperty("longDescription")
    private String longDescription;

    @JsonProperty("status")
    private String status;

    @JsonProperty("authors")
    private List<String> authors;

    @JsonProperty("categories")
    private List<String> categories;

    @JsonProperty("index")
    private Index index;
}
