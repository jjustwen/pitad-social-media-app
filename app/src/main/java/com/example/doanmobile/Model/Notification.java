package com.example.doanmobile.Model;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Date;

public class Notification
{
    private String notifyid;
    private String userid;
    private String userid_interaction;
    private String notifyContent;
    private String postid;
    private int stt;

    public String getNotifyTime()
    {
        return notifyTime;
    }

    public void setNotifyTime(String notifyTime)
    {
        this.notifyTime = notifyTime;
    }

    private String notifyTime;

    public String getNotifyid()
    {
        return notifyid;
    }

    public void setNotifyid(String notifyid)
    {
        this.notifyid = notifyid;
    }

    public String getUserid()
    {
        return userid;
    }

    public void setUserid(String userid)
    {
        this.userid = userid;
    }

    public String getUserid_interaction()
    {
        return userid_interaction;
    }

    public void setUserid_interaction(String userid_interaction)
    {
        this.userid_interaction = userid_interaction;
    }

    public String getNotifyContent()
    {
        return notifyContent;
    }

    public void setNotifyContent(String notifyContent)
    {
        this.notifyContent = notifyContent;
    }

    public String getPostid()
    {
        return postid;
    }

    public void setPostid(String postid)
    {
        this.postid = postid;
    }


    public Notification()
    {
        // Cần có constructor mặc định để deserialize từ Firestore
    }

    public Notification(String notifyid, String userid, String userid_interaction, String notifyContent, String postid, int stt)
    {
        this.notifyid = notifyid;
        this.userid = userid;
        this.userid_interaction = userid_interaction;
        this.notifyContent = notifyContent;
        this.postid = postid;
        Date time = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        this.notifyTime = dateFormat.format(time);
        this.stt = stt;
    }
}
