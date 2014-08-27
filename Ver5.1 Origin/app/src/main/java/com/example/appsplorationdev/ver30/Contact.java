package com.example.appsplorationdev.ver30;

/**
 * Created by appsplorationdev on 8/18/14.
 */
public class Contact {
    int image;
    String name;

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Contact(int image, String name) {
        super();
        this.image = image;
        this.name = name;
    }
}
