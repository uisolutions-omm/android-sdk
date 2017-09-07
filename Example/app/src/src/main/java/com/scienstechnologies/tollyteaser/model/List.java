package com.scienstechnologies.tollyteaser.model;

/**
 * Created by onmymobile on 5/18/2017.
 */
//Model class for firebase
public class List {
    String title;
    String image_url;
    String description;
    String category;
    public void list()
    {

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
    public String getCategory()
    {
        return category;
    }
    public void setCategory(String category)
    {
        this.category=category;
    }
}
