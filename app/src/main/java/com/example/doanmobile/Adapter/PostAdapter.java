package com.example.doanmobile.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doanmobile.Fragment.PostDetailFragment;
import com.example.doanmobile.Fragment.ProfileFragment;
import com.example.doanmobile.Model.Notification;
import com.example.doanmobile.Model.Post;
import com.example.doanmobile.Model.User;
import com.example.doanmobile.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    public FirebaseFirestore db;

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
        if (post.getPosttype().equals("image"))
        {
            holder.video_added.setVisibility(View.GONE);
            holder.image_added.setVisibility(View.VISIBLE);
            Glide.with(context).load(post.getPostimage()).placeholder(R.drawable.placeholder).into(holder.image_added);
        }
        else
        {
            holder.video_added.setVisibility(View.VISIBLE);
            holder.image_added.setVisibility(View.GONE);
            Uri videoUri = Uri.parse(post.getPostimage());
            holder.video_added.setVideoURI(videoUri);
            holder.video_added.requestFocus();
            holder.video_added.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
            {
                @Override
                public void onPrepared(MediaPlayer mp)
                {
                    mp.start();
                }
            });
        }
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(post.getPublisher());
        if (post.getLike().contains(curUserID))
        {
            holder.heart.setImageResource(R.drawable.ic_heart_after);
        }
        else
        {
            holder.heart.setImageResource(R.drawable.ic_heart);
        }
        db.collection("Users").document(curUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                User usr = task.getResult().toObject(User.class);
                if (usr.getSave().contains(post.getPostid()))
                {
                    holder.save.setImageResource(R.drawable.ic_save_black);
                }
                else
                {
                    holder.save.setImageResource(R.drawable.baseline_bookmark_border_24);
                }
            }
        });
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
        holder.save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                db.collection("Users").document(curUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                    {
                        User usr = task.getResult().toObject(User.class);
                        if (usr.getSave().contains(post.getPostid()))
                        {
                            usr.getSave().remove(post.getPostid());
                            holder.save.setImageResource(R.drawable.ic_save_black);
                        }
                        else
                        {
                            usr.getSave().add(post.getPostid());
                            holder.save.setImageResource(R.drawable.baseline_bookmark_border_24);
                        }
                        db.collection("Users").document(usr.getId()).set(usr);
                        notifyDataSetChanged();
                    }
                });
            }
        });

        if (post.getLike() != null)
        {
            holder.likes.setText(String.valueOf(post.getLike().size()) + " lượt thích");
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
        holder.image_added.setOnClickListener(new View.OnClickListener()
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
        holder.more.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        int id = item.getItemId();
                        if (id == R.id.edit)
                        {
                            editPost(post.getPostid());
                            notifyDataSetChanged();
                            return true;
                        }
                        else if (id == R.id.delete)
                        {
                            if (post.getPublisher().equals(curUserID))
                            {
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("Posts").document(post.getPostid())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>()
                                        {
                                            @Override
                                            public void onSuccess(Void aVoid)
                                            {
                                                Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener()
                                        {
                                            @Override
                                            public void onFailure(@NonNull Exception e)
                                            {
                                                Toast.makeText(context, "Failed to delete post.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                db.collection("Users").document(curUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                                    {
                                        User usr = task.getResult().toObject(User.class);
                                        usr.getPost().remove(post.getPostid());
                                        db.collection("Users").document(usr.getId()).set(usr);
                                    }
                                });
                                notifyDataSetChanged();
                            }
                            else
                            {
                                Toast.makeText(context, "You can't delete this post.", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                        else if (id == R.id.report)
                        {
                            Toast.makeText(context, "Report Sent!", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.post_menu);
                if (!post.getPublisher().equals(curUserID))
                {
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                }
                popupMenu.show();
            }
        });
    }

    private void editPost(final String postid)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Edit Post");

        final EditText editText = new EditText(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(lp);
        alertDialog.setView(editText);

        // Lấy description của postid
        DocumentReference postRef = db.collection("Posts").document(postid);
        postRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        String description = document.getString("description");
                        editText.setText(description);
                    }
                }
            }
        });

        alertDialog.setPositiveButton("Edit",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // Update post description
                        String newDescription = editText.getText().toString();
                        DocumentReference postRef = db.collection("Posts").document(postid);
                        postRef.update("description", newDescription);

                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

    @Override
    public int getItemCount()
    {
        return post_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView image_profile;
        public ImageView image_added;
        public VideoView video_added;
        public ImageView like;
        public ImageView comment;
        public ImageView save;
        public ImageView more;

        public TextView username;
        public TextView likes;
        public TextView publisher;
        public TextView description;
        public TextView comments;
        public ImageButton heart;
        public TextView publish_date;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            more = itemView.findViewById(R.id.more);
            image_profile = itemView.findViewById(R.id.image_profile);
            image_added = itemView.findViewById(R.id.image_added);
            video_added = itemView.findViewById(R.id.video_added);
            likes = itemView.findViewById(R.id.number_like);
            username = itemView.findViewById(R.id.username);
            heart = itemView.findViewById(R.id.like);
            save = itemView.findViewById(R.id.save);
            publish_date = itemView.findViewById(R.id.publish_date);
            description = itemView.findViewById(R.id.description);
        }
    }

}
