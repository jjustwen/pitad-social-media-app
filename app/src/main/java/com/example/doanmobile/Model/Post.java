package com.example.doanmobile.Model;

import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.ArrayList;


public class Post
{
    public int getStt()
    {
        return stt;
    }

    public void setStt(int stt)
    {
        this.stt = stt;
    }

    private int stt;
    private String postid;
    private String postimage;
    private String publish_date;

    public String getPublish_date()
    {
        return publish_date;
    }

    private String description;
    private String publisher;

    public void setPublish_date(String publish_date)
    {
        this.publish_date = publish_date;
    }

    public ArrayList<String> getLike()
    {
        if (like != null) return like;
        return new ArrayList<String>();

    }

    public void setLike(ArrayList<String> like)
    {
        this.like = like;
    }

    public ArrayList<String> getSave()
    {
        if (save != null) return save;
        return new ArrayList<String>();
    }

    public void setSave(ArrayList<String> save)
    {
        this.save = save;
    }

    private ArrayList<String> like;
    private ArrayList<String> save;
//    private ArrayList<Comment> comment;

    public Post()
    {
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    int number_post = 0;

    public Post(String postid, String postimage, String description, String publisher, int stt)
    {
        this.postid = postid;
        this.postimage = postimage;
        this.description = description;
        this.publisher = publisher;
        like = new ArrayList<String>();
        save = new ArrayList<String>();
        publish_date = LocalDate.now().toString();
        this.stt = stt;
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
