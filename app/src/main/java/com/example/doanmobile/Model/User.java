package com.example.doanmobile.Model;

import java.util.ArrayList;

public class User
{
    private String id;
    private String username;
    private String email;

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    private String fullname;
    private String imageurl;
    private String passwd;

    public String getPasswd()
    {
        return passwd;
    }

    public void setPasswd(String passwd)
    {
        this.passwd = passwd;
    }

    private String bio;
    private ArrayList<String> follower;
    private ArrayList<String> following;
    private ArrayList<String> post;
    private ArrayList<String> save;


    public User()
    {
    }

    public User(String id, String username, String fullname, String imageurl, String bio, String passwd, String email)
    {
        this.id = id;
        this.username = username;
        this.fullname = fullname;
        this.imageurl = imageurl;
        this.email = email;
        this.bio = bio;
        this.passwd = passwd;
        follower = new ArrayList<String>();
        following = new ArrayList<String>();
        post = new ArrayList<String>();
        save = new ArrayList<String>();
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
