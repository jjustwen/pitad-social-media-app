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
        LocalTime now = LocalTime.now();
        this.initNotifyTime = now.toString();
        this.notifyDuration = "0";
    }

    public String getInitNotifyTime()
    {
        return initNotifyTime;
    }

    public String getNotifyDuration()
    {
        Duration durationNotify;
        LocalTime initTime = LocalTime.parse(initNotifyTime); // Chuyển đổi thành LocalTime
        if (LocalTime.now().compareTo(initTime) < 0)
        {
            LocalTime time_a_day = LocalTime.MAX;
            // Tính khoảng thời gian từ initTime đến time_a_day
           durationNotify = Duration.between(initTime, time_a_day);

            // Thêm khoảng thời gian từ thời điểm hiện tại đến thời điểm hiện tại + durationNotify
            durationNotify = durationNotify.plusHours(LocalTime.now().getHour()).plusMinutes(LocalTime.now().getMinute());
        }
        else
        {
            durationNotify = Duration.between(initTime, LocalTime.now());
        }
        long hours = durationNotify.toHours(); // Lấy số giờ
        long minutes = durationNotify.toMinutesPart(); // Lấy số phút
        long seconds = durationNotify.toSecondsPart(); // Lấy số giây

        if (hours > 0)
        {
            if (minutes != 0)
            {
                return hours + " giờ " + minutes + " phút " + "trước";
            }
            else return hours + " giờ " + "trước";
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
//    public String getNotifyDuration()
//    {
//        LocalDateTime initTime = LocalDateTime.parse(initNotifyTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); // Chuyển đổi thành LocalDateTime
//        LocalDateTime now = LocalDateTime.now();
//
//        Duration durationNotify = Duration.between(initTime, now);
//        long days = durationNotify.toDays();
//
//        // Loại bỏ số ngày khỏi khoảng thời gian
//        durationNotify = durationNotify.minusDays(days);
//
//        long hours = durationNotify.toHours(); // Lấy số giờ
//        long minutes = durationNotify.toMinutesPart(); // Lấy số phút
//        long seconds = durationNotify.toSecondsPart(); // Lấy số giây
//
//        StringBuilder result = new StringBuilder();
//
//        if (days > 0)
//        {
//            result.append(days).append(" ngày ");
//        }
//        if (hours > 0)
//        {
//            result.append(hours).append(" giờ ");
//        }
//        if (minutes > 0)
//        {
//            result.append(minutes).append(" phút ");
//        }
//        if (seconds > 0 || result.length() == 0)
//        {
//            result.append(seconds).append(" giây ");
//        }
//        result.append("trước");
//
//        return result.toString();
//    }

}
