package com.example.doanmobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanmobile.Adapter.UserAdapter;
import com.example.doanmobile.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FollowingActivity extends AppCompatActivity
{
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private ArrayList<User> userArrayList;

    private ImageView back;
    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    String curUserID;
    String profileID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
        userArrayList = new ArrayList<>();
        back = findViewById(R.id.back);
        recyclerView = findViewById(R.id.recycler_view_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(FollowingActivity.this));
        userAdapter = new UserAdapter(FollowingActivity.this, userArrayList, true);
        recyclerView.setAdapter(userAdapter);


        Intent intent = getIntent();
        if (intent != null)
        {
            Bundle extras = intent.getExtras();
            if (extras != null)
            {
                profileID = extras.getString("id");
            }
        }
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        curUserID = firebaseUser.getUid().toString();
        loadFollowing();
        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    void loadFollowing()
    {
        db.collection("Users").document(profileID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful() && task.getResult() != null)
                {
                    User usr = task.getResult().toObject(User.class);
                    db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task)
                        {
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments())
                            {
                                User following = documentSnapshot.toObject(User.class);
                                if (usr.getFollowing().contains(following.getId()))
                                {
                                    userArrayList.add(following);
                                }
                            }
                            userAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }
}