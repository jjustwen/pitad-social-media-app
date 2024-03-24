package com.example.doanmobile.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doanmobile.Model.Notification;
import com.example.doanmobile.Model.Post;
import com.example.doanmobile.Model.User;
import com.example.doanmobile.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>
{

    private Context context;
    private List<Notification> mNotifications;

    public NotificationAdapter(Context mContext, List<Notification> mNotifications)
    {
        this.context = mContext;
        this.mNotifications = mNotifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {

        final Notification notification = mNotifications.get(position);
        holder.content.setText(notification.getNotifyContent());
        holder.duration_notify.setText(notification.getNotifyDuration());
        if (notification.getUserid_interaction().equals(""))
        {
            getUserInfo(holder.image_profile, holder.username, notification.getUserid());
        }
        else
        {
            getUserInfo(holder.image_profile, holder.username, notification.getUserid_interaction());
        }
        if (!notification.getPostid().equals(""))
        {
            getPostImage(holder.post_image, notification.getPostid());
        }
    }

    @Override
    public int getItemCount()
    {
        return mNotifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        public ImageView image_profile;
        public ImageView post_image;
        public TextView username;
        public TextView content;
        public TextView duration_notify;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            username = itemView.findViewById(R.id.username);
            content = itemView.findViewById(R.id.content);
            duration_notify = itemView.findViewById(R.id.duration_notify);
        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String userid)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(userid).get().addOnCompleteListener(task ->
            {
            if (task.isSuccessful())
            {
                DocumentSnapshot document = task.getResult();
                if (document.exists())
                {
                    User user = document.toObject(User.class);
                    if (user != null)
                    {
                        if (user.getImageurl().equals("default"))
                        {
                            Glide.with(context).load(R.drawable.default_avatar).into(imageView);
                        }
                        else
                        {
                            Glide.with(context).load(user.getImageurl()).into(imageView);
                        }
                        username.setText(user.getUsername());
                    }
                }
            }
            });
    }

    private void getPostImage(final ImageView imageView, String postid)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Posts").document(postid).get().addOnCompleteListener(task ->
            {
            if (task.isSuccessful())
            {
                DocumentSnapshot document = task.getResult();
                if (document.exists())
                {
                    Post post = document.toObject(Post.class);
                    if (post != null)
                    {
                        Glide.with(context).load(post.getPostimage()).into(imageView);
                    }
                }
            }
            });
    }
}
