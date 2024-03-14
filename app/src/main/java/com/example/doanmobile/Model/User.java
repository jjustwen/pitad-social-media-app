package com.example.doanmobile.Model;

import java.util.ArrayList;

public class User
{
    private String id;
    private String username;
    private String fullname;
    private String imageurl;
    private String passwd;
    private String bio;
    private ArrayList<User> follower;
    private ArrayList<User> following;
    private ArrayList<Post> post;
    private ArrayList<Post> save;


    public User()
    {
    }

    public User(String id, String username, String fullname, String imageurl, String bio, String passwd)
    {
        this.id = id;
        this.username = username;
        this.fullname = fullname;
        this.imageurl = imageurl;
        this.bio = bio;
        this.passwd = passwd;
        follower = new ArrayList<User>();
        following = new ArrayList<User>();
        post = new ArrayList<Post>();
        save = new ArrayList<Post>();
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getFullname()
    {
        return fullname;
    }

    public void setFullname(String fullname)
    {
        this.fullname = fullname;
    }

    public String getImageurl()
    {
        return imageurl;
    }

    public void setImageurl(String imageurl)
    {
        this.imageurl = imageurl;
    }

    public String getBio()
    {
        return bio;
    }

    public void setBio(String bio)
    {
        this.bio = bio;
    }
}
