package com.example.doanmobile.Model;

import java.time.Duration;
import java.time.LocalTime;

public class Notification
{
    private String notifyid;
    private String userid;
    private String userid_interaction;
    private String notifyContent;
    private String postid;
    private String initNotifyTime; // Sử dụng String cho thời gian khi tạo thông báo
    private String notifyDuration;

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

    public Notification(String notifyid, String userid, String userid_interaction, String notifyContent, String postid)
    {
        this.notifyid = notifyid;
        this.userid = userid;
        this.userid_interaction = userid_interaction;
        this.notifyContent = notifyContent;
        this.postid = postid;
        this.initNotifyTime = LocalTime.now().toString(); // Chuyển đổi thành String
        this.notifyDuration = "0"; // Khoảng thời gian ban đầu là 0
    }

    public String getInitNotifyTime()
    {
        return initNotifyTime;
    }

    public String getNotifyDuration()
    {
        LocalTime initTime = LocalTime.parse(initNotifyTime); // Chuyển đổi thành LocalTime
        Duration durationNotify = Duration.between(initTime, LocalTime.now());

        long hours = durationNotify.toHours(); // Lấy số giờ
        long minutes = durationNotify.toMinutesPart(); // Lấy số phút
        long seconds = durationNotify.toSecondsPart(); // Lấy số giây

        if (hours > 0)
        {
            return hours + " giờ " + minutes + " phút " + "trước";
        }
        else if (minutes > 0)
        {
            return minutes + " phút " + "trước";
        }
        else
        {
            return seconds + " giây " + "trước";
        }
    }


}
