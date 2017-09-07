package com.scienstechnologies.tollyteaser.model;

/**
 * Created by onmymobile on 5/18/2017.
 */

public class ListModel {

    String title;
    String image_url;
    String description;
    String category;
    public  ListModel()
    {

    }

    public ListModel(String title, String image_url, String description,String category) {
        this.title = title;
        this.image_url = image_url;
        this.description = description;
        this.category=category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
