package com.raghav.tddwithspringboot.reviews.model;

import java.util.Date;

public class ReviewEntry {
    private String username;
    private Date date;
    private String review;

    public ReviewEntry() {
    }

    public ReviewEntry(String username, Date date, String review) {
        this.username = username;
        this.date = date;
        this.review = review;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
