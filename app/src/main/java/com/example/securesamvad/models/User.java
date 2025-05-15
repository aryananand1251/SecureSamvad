package com.example.securesamvad.models;

public class User {

    private String name;
    private String imageUrl;

    public User() {}  // Empty constructor for Firebase

    public User(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
}
