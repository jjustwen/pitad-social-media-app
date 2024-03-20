package com.example.doanmobile.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doanmobile.Fragment.PostDetailFragment;
import com.example.doanmobile.Fragment.ProfileFragment;
import com.example.doanmobile.Model.Notification;
import com.example.doanmobile.Model.Post;
import com.example.doanmobile.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.UUID;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>
{
    public Context context;
    public List<Post> post_list;

    public PostAdapter(Context context, List<Post> post_list)
    {
        this.context = context;
        this.post_list = post_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser curUser = auth.getCurrentUser();
    String curUserID = curUser.getUid();

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        final Post post = post_list.get(position);
        Glide.with(context).load(post.getPostimage()).into(holder.post_image);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(post.getPublisher());
        if (post.getLike().contains(curUserID))
        {
            holder.heart.setImageResource(R.drawable.ic_heart_after);
        }
        else
        {
            holder.heart.setImageResource(R.drawable.ic_heart);
        }
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {

                DocumentSnapshot document = task.getResult();
                String username = document.getString("username");
                String profileImageUrl = document.getString("imageurl");

                holder.username.setText(username);
                if (profileImageUrl.equals("default"))
                {
                    Glide.with(context).load(R.drawable.default_avatar).into(holder.image_profile);
                }
                else
                {
                    Glide.with(context).load(profileImageUrl).into(holder.image_profile);
                }
            }
        });

        holder.heart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (post.getLike().contains(curUserID))
                {
                    post.getLike().remove(curUserID);
                    holder.heart.setImageResource(R.drawable.ic_heart);
                }
                else
                {
                    //Nếu tự like thì ko cần thông báo lên
                    if (!post.getPublisher().equals(curUserID))
                    {
                        String heart_notify_id = UUID.randomUUID().toString();
                        Notification heart_notify = new Notification(heart_notify_id, post.getPublisher(), curUserID, "thích bài viết của bạn", post.getPostid());
                        db.collection("Notifications").document(heart_notify_id).set(heart_notify);
                    }
                    post.getLike().add(curUserID);
                    holder.heart.setImageResource(R.drawable.ic_heart_after);
                }
                notifyItemChanged(position);
                db.collection("Posts").document(post.getPostid()).set(post);
            }
        });


        if (post.getLike() != null)
        {
            holder.likes.setText(String.valueOf(post.getLike().size()));
        }
        else
        {

            holder.likes.setText("0");
        }
        holder.publish_date.setText(post.getPublish_date());
        holder.description.setText(post.getDescription());
        holder.username.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
            }
        });
        holder.post_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postid", post.getPostid());
                editor.apply();
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PostDetailFragment()).commit();
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return post_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {

        public ImageView image_profile;
        public ImageView post_image;

        public TextView username;
        public TextView likes;
        public ImageButton heart;
        public TextView publish_date;
        public TextView description;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            likes = itemView.findViewById(R.id.number_like);
            username = itemView.findViewById(R.id.username);
            heart = itemView.findViewById(R.id.like);
            publish_date = itemView.findViewById(R.id.publish_date);
            description = itemView.findViewById(R.id.description);

        }
    }

}
