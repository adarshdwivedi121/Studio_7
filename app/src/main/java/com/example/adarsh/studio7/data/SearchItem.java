package com.example.adarsh.studio7.data;

/**
 * Created by adarsh on 21/08/2017.
 */

public class SearchItem {
    private String id;
    private String title;
    private String artist;

    public SearchItem(String id, String title, String artist) {
        this.id = id;
        this.title = title;
        this.artist = artist;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }
}
