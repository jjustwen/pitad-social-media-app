package com.example.doanmobile.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.doanmobile.Adapter.MyFotoAdapter;
import com.example.doanmobile.EditProfileActivity;
import com.example.doanmobile.Model.Notification;
import com.example.doanmobile.Model.Post;
import com.example.doanmobile.Model.User;
import com.example.doanmobile.OptionsActivity;
import com.example.doanmobile.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.UUID;

public class ProfileFragment extends Fragment
{

    private ImageView image_profile, options;
    private TextView posts, followers, following, fullname, bio, username;
    private ImageButton my_fotos, saved_fotos;
    private MyFotoAdapter myFotoAdapter;
    private ArrayList<Post> postList;
    private String profileid;
    private Button btn_follow;

    private RecyclerView recyclerView, recyclerView_saves;
    private FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private String curUserID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        SharedPreferences prefs = getActivity().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid", "");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        curUserID = firebaseUser.getUid().toString();
        db = FirebaseFirestore.getInstance();
        image_profile = view.findViewById(R.id.image_profile);
        options = view.findViewById(R.id.options);
        posts = view.findViewById(R.id.posts);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        fullname = view.findViewById(R.id.fullname);
        bio = view.findViewById(R.id.bio);
        my_fotos = view.findViewById(R.id.my_fotos);
        username = view.findViewById(R.id.username);
//        saved_fotos = view.findViewById(R.id.saved_fotos);

        btn_follow = view.findViewById(R.id.btn_follow);

        recyclerView = view.findViewById(R.id.recycler_view_my_post);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        postList = new ArrayList<>();
        myFotoAdapter = new MyFotoAdapter(getContext(), postList);
        recyclerView.setAdapter(myFotoAdapter);
        options.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getContext(), OptionsActivity.class);
                startActivity(intent);
            }
        });
//        recyclerView_saves = view.findViewById(R.id.recycler_view_save);
//        recyclerView_saves.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager1 = new GridLayoutManager(getContext(), 3);
//        recyclerView_saves.setLayoutManager(linearLayoutManager1);
//        postList_saves = new ArrayList<>();
//        myFotoAdapter_saves = new MyFotoAdapter(getContext(), postList_saves);
//        recyclerView_saves.setAdapter(myFotoAdapter_saves);

//        my_fotos.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                recyclerView.setVisibility(View.GONE);
//                recyclerView_saves.setVisibility(View.VISIBLE);
//            }
//        });

//        followers.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                Intent intent = new Intent(getContext(), FollowersActivity.class);
//                intent.putExtra("id", profileid);
//                intent.putExtra("title", "Followers");
//                startActivity(intent);
//            }
//        });

//        following.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                Intent intent = new Intent(getContext(), FollowersActivity.class);
//                intent.putExtra("id", profileid);
//                intent.putExtra("title", "Following");
//                startActivity(intent);
//            }
//        });

        if (!profileid.equals(curUserID))
        {
            btn_follow.setOnClickListener(new View.OnClickListener()
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

                            if (usr.getFollowing().contains(profileid))
                            {
                                usr.getFollowing().remove(profileid);
                            }
                            else
                            {
                                usr.getFollowing().add(profileid);
                            }
                            db.collection("Users").document(curUserID).set(usr);
                        }
                    });
                    db.collection("Users").document(profileid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task)
                        {
                            User usr = task.getResult().toObject(User.class);
                            if (usr.getFollower().contains(curUserID))
                            {
                                usr.getFollower().remove(curUserID);
                                btn_follow.setText("FOLLOW");
                            }
                            else
                            {
                                String notifyID = UUID.randomUUID().toString();
                                Notification followNotify = new Notification(notifyID, usr.getId(), curUserID, "đã theo dõi bạn", "");
                                db.collection("Notifications").document(notifyID).set(followNotify);
                                usr.getFollower().add(curUserID);
                                btn_follow.setText("FOLLOWED");
                                btn_follow.setBackgroundResource(R.color.colorAccent);
                            }

                            db.collection("Users").document(usr.getId()).set(usr);
                            userInfo();
                        }
                    });
                }
            });
            loadFotos();
        }
        else
        {
            btn_follow.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(getContext(), EditProfileActivity.class);
                    startActivity(intent);
                }
            });
        }
        userInfo();
        return view;
    }

    private void userInfo()
    {

        db.collection("Users").document(profileid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                    {
                        if (task.isSuccessful() && task.getResult() != null)
                        {
                            User user = task.getResult().toObject(User.class);
                            if (user != null)
                            {
                                Glide.with(getContext())
                                        .load(user.getImageurl())
                                        .apply(RequestOptions.placeholderOf(R.drawable.default_avatar))
                                        .into(image_profile);
                                username.setText(user.getUsername());
                                fullname.setText(user.getFullname());
                                if (user.getPost() != null)
                                    posts.setText(String.valueOf(user.getPost().size()));
                                else
                                    posts.setText("0");
                                if (user.getFollower() != null)
                                    followers.setText(String.valueOf(user.getFollower().size()));
                                else
                                    followers.setText("0");
                                if (user.getFollowing() != null)
                                    following.setText(String.valueOf(user.getFollowing().size()));
                                else
                                    following.setText("0");
                                bio.setText(user.getBio());
                                if (profileid.equals(curUserID))
                                {
                                    btn_follow.setText("EDIT PROFILE");
                                }
                                else
                                {
                                    if (user.getFollower().contains(String.valueOf(firebaseUser.getUid())))
                                    {
                                        btn_follow.setText("FOLLOWED");
                                        btn_follow.setBackgroundResource(R.color.colorAccent);
                                    }
                                    else
                                    {
                                        btn_follow.setText("FOLLOW");
                                        btn_follow.setBackgroundResource(R.color.colorBlack);
                                    }

                                }
                                loadFotos();
                            }
                        }
                    }
                });
    }


    private void loadFotos()
    {
        db.collection("Posts")
                .whereEqualTo("publisher", profileid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful() && task.getResult() != null)
                        {
                            postList.clear();
                            for (DocumentSnapshot document : task.getResult().getDocuments())
                            {
                                Post post = document.toObject(Post.class);
                                postList.add(post);
                            }
                            myFotoAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

}
