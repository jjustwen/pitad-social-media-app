package com.example.doanmobile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LikeActivity extends AppCompatActivity
{

    private String postID;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);
        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        if (intent != null)
        {
            Bundle extras = intent.getExtras();
            if (extras != null)
            {
                postID = extras.getString("postid");
            }
        }

    }

    private void loadLikePeople()
    {
        db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {

            }
        });
    }
}