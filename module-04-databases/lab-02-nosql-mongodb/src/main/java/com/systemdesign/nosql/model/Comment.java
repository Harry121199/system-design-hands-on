package com.systemdesign.nosql.model;

import java.time.LocalDateTime;

public class Comment {

    private String author;
    private String text;
    private LocalDateTime postedAt;

    public Comment(){}

    public Comment(String author, String text) {
        this.author = author;
        this.text = text;
        this.postedAt = LocalDateTime.now();
    }

    public String getAuthor(){
        return this.author;
    }
    public void setAuthor(String author){
        this.author = author;
    }

    public String getText(){
        return this.text;
    }
    public void setText(String text){
        this.text = text;
    }

    public LocalDateTime getPostedAt(){
        return this.postedAt;
    }

    public void setPostedAt(LocalDateTime postedAt) {
        this.postedAt = postedAt;
    }
}

