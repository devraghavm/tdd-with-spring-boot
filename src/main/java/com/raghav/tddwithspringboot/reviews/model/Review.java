package com.raghav.tddwithspringboot.reviews.model;


import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "Reviews")
public class Review {
    private String id;

    private Integer productId;

    private Integer version = 1;

    private List<ReviewEntry> entries = new ArrayList<>();

    public Review() {
    }

    public Review(Integer productId) {
        this.productId = productId;
    }

    public Review(Integer productId, Integer version) {
        this.productId = productId;
        this.version = version;
    }

    public Review(String id, Integer productId, Integer version) {
        this.id = id;
        this.productId = productId;
        this.version = version;
    }

    public Review(String id, Integer productId, Integer version, List<ReviewEntry> entries) {
        this.id = id;
        this.productId = productId;
        this.version = version;
        this.entries = entries;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public List<ReviewEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<ReviewEntry> entries) {
        this.entries = entries;
    }
}
