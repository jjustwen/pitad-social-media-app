package com.example.doanmobile.Model;

import java.time.Duration;
import java.time.LocalTime;

public class Notification
{

    private String userid;

    public Duration getNotifyTime()
    {
        return notifyTime = Duration.between(initNotifyTime, LocalTime.now());
    }

    public LocalTime getInitNotifyTime()
    {
        return initNotifyTime;
    }

    public String getNotifyContent()
    {
        return notifyContent;
    }

    public void setNotifyContent(String notifyContent)
    {
        this.notifyContent = notifyContent;
    }

    private Duration notifyTime;
    private LocalTime initNotifyTime;
    private String notifyContent;
    private String postid;


    public Notification()
    {

    }

    public Notification(String userid, String notifyContent, String postid)
    {
        this.userid = userid;
        this.notifyContent = notifyContent;
        this.postid = postid;
        initNotifyTime = LocalTime.now();
    }

    public String getUserid()
    {
        return userid;
    }

    public void setUserid(String userid)
    {
        this.userid = userid;
    }

    public String getText()
    {
        return notifyContent;
    }

    public void setText(String text)
    {
        this.notifyContent = text;
    }

    public String getPostid()
    {
        return postid;
    }

    public void setPostid(String postid)
    {
        this.postid = postid;
    }


}
