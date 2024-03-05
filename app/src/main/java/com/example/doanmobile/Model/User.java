package com.example.doanmobile.Model;

public class User {
    private String id;
    private String username;
    private String fullname;
    private String image;
    private String bio;
    public User() {
    }

    public User(String id, String username, String fullname, String image, String bio) {
        this.id = id;
        this.username = username;
        this.fullname = fullname;
        this.image = image;
        this.bio = bio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
