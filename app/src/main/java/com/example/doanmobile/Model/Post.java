package com.example.doanmobile.Model;

import java.time.LocalTime;
import java.util.ArrayList;


public class Post
{
    private String postid;
    private String postimage;
    private String publish_date;

    public String getPublish_date()
    {
        return publish_date;
    }

    public String getUserimage()
    {
        return userimage;
    }

    public void setUserimage(String userimage)
    {
        this.userimage = userimage;
    }

    private String userimage;
    private String description;
    private String publisher;

    public int getLike()
    {

        return like.size();
    }

    private ArrayList<String> like;
    private ArrayList<String> save;
//    private ArrayList<Comment> comment;

    public Post()
    {
    }

    public Post(String postid, String postimage, String description, String publisher)
    {
        this.postid = postid;
        this.postimage = postimage;
        this.description = description;
        this.publisher = publisher;
        like = new ArrayList<String>();
        save = new ArrayList<String>();
        publish_date = java.time.LocalTime.now().toString();
    }

    public String getPostid()
    {
        return postid;
    }

    public void setPostid(String postid)
    {
        this.postid = postid;
    }

    public String getPostimage()
    {
        return postimage;
    }

    public void setPostimage(String postimage)
    {
        this.postimage = postimage;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getPublisher()
    {
        return publisher;
    }

    public void setPublisher(String publisher)
    {
        this.publisher = publisher;
    }
}
