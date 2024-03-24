package com.example.doanmobile.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doanmobile.Fragment.ProfileFragment;
import com.example.doanmobile.MainActivity;
import com.example.doanmobile.Model.Notification;
import com.example.doanmobile.Model.User;
import com.example.doanmobile.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>
{
    private Context mContext;
    private List<User> mUsers;
    private boolean isFragment;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;

    public UserAdapter(Context mContext, List<User> mUsers, boolean isFragment)
    {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position)
    {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String curUserID = firebaseUser.getUid().toString();
        db = FirebaseFirestore.getInstance();
        final User user = mUsers.get(position);
        if (user.getId().equals(curUserID))
        {
            holder.btn_follow_user.setVisibility(View.GONE);
        }
        else
        {

            if (user.getFollower().contains(curUserID))
            {
                holder.btn_follow_user.setText("FOLLOWED");
                holder.btn_follow_user.setBackgroundColor(Color.parseColor("#120460"));
                holder.btn_follow_user.setTextColor(Color.parseColor("#ffffff"));
            }
            else
            {
                holder.btn_follow_user.setText("FOLLOW");
                holder.btn_follow_user.setBackgroundColor(Color.parseColor("#1e8eab"));
                holder.btn_follow_user.setTextColor(Color.parseColor("#ffffff"));
            }
        }

        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getFullname());
        if (user.getImageurl() != null)
        {
            Glide.with(mContext).load(user.getImageurl()).into(holder.image_profile);
        }
        else
        {
            holder.image_profile.setImageResource(R.drawable.default_avatar);
        }
        holder.btn_follow_user.setOnClickListener(new View.OnClickListener()
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

                        if (usr.getFollowing().contains(user.getId()))
                        {
                            usr.getFollowing().remove(user.getId());
                        }
                        else
                        {
                            usr.getFollowing().add(user.getId());
                        }
                        db.collection("Users").document(curUserID).set(usr);
                    }
                });
                db.collection("Users").document(user.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                    {
                        User usr = task.getResult().toObject(User.class);
                        if (usr.getFollower().contains(curUserID))
                        {
                            usr.getFollower().remove(curUserID);
                            holder.btn_follow_user.setText("FOLLOW");
                            holder.btn_follow_user.setBackgroundColor(Color.parseColor("#1e8eab"));
                            holder.btn_follow_user.setTextColor(Color.parseColor("#ffffff"));
                        }
                        else
                        {
                            String notifyID = UUID.randomUUID().toString();
                            Notification followNotify = new Notification(notifyID, usr.getId(), curUserID, "đã theo dõi bạn", "");
                            db.collection("Notifications").document(notifyID).set(followNotify);
                            usr.getFollower().add(curUserID);
                            holder.btn_follow_user.setText("FOLLOWED");
                            holder.btn_follow_user.setBackgroundColor(Color.parseColor("#120460"));
                            holder.btn_follow_user.setTextColor(Color.parseColor("#ffffff"));
                        }

                        db.collection("Users").document(usr.getId()).set(usr);
                    }
                });
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isFragment)
                {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("profileid", user.getId());
                    editor.apply();

                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                }
                else
                {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("publisherid", user.getId());
                    mContext.startActivity(intent);
                }
            }
        });
    }


    @Override
    public int getItemCount()
    {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        public TextView username;
        public TextView fullname;
        public CircleImageView image_profile;
        public Button btn_follow_user;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            fullname = itemView.findViewById(R.id.fullname);
            image_profile = itemView.findViewById(R.id.image_profile);
            btn_follow_user = itemView.findViewById(R.id.btn_follow_user);
        }
    }
}
