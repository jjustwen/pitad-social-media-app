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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.doanmobile.Adapter.MyFotoAdapter;
import com.example.doanmobile.EditProfileActivity;
import com.example.doanmobile.FollowersActivity;
import com.example.doanmobile.Model.Post;
import com.example.doanmobile.Model.User;
import com.example.doanmobile.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileFragment extends Fragment
{

    private ImageView image_profile;
    private ImageView options;
    private TextView posts;
    private TextView followers;
    private TextView following;
    private TextView fullname;
    private TextView bio;
    private TextView username;
    private ImageButton my_fotos;
    private ImageButton saved_fotos;
    private Button editprofile;

    private RecyclerView recyclerView;
    private MyFotoAdapter myFotoAdapter;
    private List<Post> postList;

    private List<String> mySaves;
    private RecyclerView recyclerView_saves;
    private MyFotoAdapter myFotoAdapter_saves;
    private List<Post> postList_saves;

    private FirebaseUser firebaseUser;
    private String profileid;

    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");

        image_profile = view.findViewById(R.id.image_profile);
        options = view.findViewById(R.id.options);
        posts = view.findViewById(R.id.posts);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        fullname = view.findViewById(R.id.fullname);
        bio = view.findViewById(R.id.bio);
        my_fotos = view.findViewById(R.id.my_fotos);
        username = view.findViewById(R.id.username);
        saved_fotos = view.findViewById(R.id.saved_fotos);
        editprofile = view.findViewById(R.id.edit_profile);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        myFotoAdapter = new MyFotoAdapter(getContext(), postList);
        recyclerView.setAdapter(myFotoAdapter);

        recyclerView_saves = view.findViewById(R.id.recycler_view_save);
        recyclerView_saves.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new GridLayoutManager(getContext(), 3);
        recyclerView_saves.setLayoutManager(linearLayoutManager1);
        postList_saves = new ArrayList<>();
        myFotoAdapter_saves = new MyFotoAdapter(getContext(), postList_saves);
        recyclerView_saves.setAdapter(myFotoAdapter_saves);

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_saves.setVisibility(View.GONE);

        userInfo();
        getFollowers();
        getNrPosts();
        myFotos();
//        mysaves();

        if (profileid.equals(firebaseUser.getUid()))
        {
            editprofile.setText("Edit Profile");
        }
        else
        {
            checkFollow();
            saved_fotos.setVisibility(View.GONE);
        }

        editprofile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String btn = editprofile.getText().toString();

                if (btn.equals("Edit Profile"))
                {
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                }
                else if (btn.equals("follow"))
                {
                    followUser();
                }
                else if (btn.equals("following"))
                {
                    unfollowUser();
                }
            }
        });

        my_fotos.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                recyclerView.setVisibility(View.GONE);
                recyclerView_saves.setVisibility(View.VISIBLE);
            }
        });

        followers.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title", "Followers");
                startActivity(intent);
            }
        });

        following.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title", "Following");
                startActivity(intent);
            }
        });

        return view;
    }

    private void followUser()
    {
        HashMap<String, Object> followMap = new HashMap<>();
        followMap.put("userid", firebaseUser.getUid());

        db.collection("Follow").document(profileid)
                .collection("Followers")
                .document(firebaseUser.getUid())
                .set(followMap)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            addNotifications();
                            editprofile.setText("following");
                        }
                        else
                        {
                            Toast.makeText(getContext(), "Failed to follow user!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void unfollowUser()
    {
        db.collection("Follow").document(profileid)
                .collection("Followers")
                .document(firebaseUser.getUid())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            editprofile.setText("follow");
                        }
                        else
                        {
                            Toast.makeText(getContext(), "Failed to unfollow user!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addNotifications()
    {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "started following you");
        hashMap.put("postid", "");
        hashMap.put("ispost", false);

        db.collection("Notifications").document(profileid)
                .collection("Notifications")
                .add(hashMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>()
                {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task)
                    {
                        if (!task.isSuccessful())
                        {
                            Toast.makeText(getContext(), "Failed to add notification!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
                                bio.setText(user.getBio());
                            }
                        }
                    }
                });
    }

    private void checkFollow()
    {
        db.collection("Follow").document(firebaseUser.getUid())
                .collection("Following")
                .document(profileid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                    {
                        if (task.isSuccessful() && task.getResult() != null)
                        {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists())
                            {
                                editprofile.setText("following");
                            }
                            else
                            {
                                editprofile.setText("follow");
                            }
                        }
                    }
                });
    }

    private void getFollowers()
    {
        db.collection("Follow").document(profileid)
                .collection("Followers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful() && task.getResult() != null)
                        {
                            followers.setText(String.valueOf(task.getResult().size()));
                        }
                    }
                });

        db.collection("Follow").document(profileid)
                .collection("Following")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful() && task.getResult() != null)
                        {
                            following.setText(String.valueOf(task.getResult().size()));
                        }
                    }
                });
    }

    private void getNrPosts()
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
                            posts.setText(String.valueOf(task.getResult().size()));
                        }
                    }
                });
    }

    private void myFotos()
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
                                postList.add(0, post);
                            }
                            myFotoAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

//    private void mysaves() {
//        mySaves = new ArrayList<>();
//
//        db.collection("Saves").document(firebaseUser.getUid())
//                .collection("Saved")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful() && task.getResult() != null) {
//                            for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
//                                mySaves.add(snapshot.getId());
//                            }
//                            readSaves();
//                        }
//                    }
//                });
//    }
//
//    private void readSaves() {
//        db.collection("Posts")
//                .whereIn("postid", mySaves)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful() && task.getResult() != null) {
//                            postList_saves.clear();
//                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
//                                Post post = document.toObject(Post.class);
//                                postList_saves.add(post);
//                            }
//                            myFotoAdapter_saves.notifyDataSetChanged();
//                        }
//                    }
//                });
//    }
}
