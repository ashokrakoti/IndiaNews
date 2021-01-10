package com.example.android.indianews;

public class NewsItem {

     private String author;
     private String title;
     private String url;

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    private String imageUrl;

    public NewsItem(String author, String title, String url, String imageUrl) {
        this.author = author;
        this.title = title;
        this.url = url;
        this.imageUrl = imageUrl;
    }

}
