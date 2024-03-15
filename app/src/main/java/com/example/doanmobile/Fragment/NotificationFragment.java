package com.example.doanmobile.Fragment;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanmobile.Adapter.NotificationAdapter;
import com.example.doanmobile.Model.Notification;
import com.example.doanmobile.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;


public class NotificationFragment extends Fragment
{

    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser curUser = FirebaseAuth.getInstance().getCurrentUser();
    String currentUserID;
    private ArrayList<Notification> notificationList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        currentUserID = curUser.getUid();
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(getContext(), notificationList);
        recyclerView.setAdapter(notificationAdapter);

        loadNotification();
//        readNotification();

        return view;
    }


    private void loadNotification()
    {
        db.collection("Notifications")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful() && task.getResult() != null)
                        {
                            notificationList.clear();
                            for (DocumentSnapshot document : task.getResult().getDocuments())
                            {
                                Notification notification = document.toObject(Notification.class);
                                notificationList.add(0, notification);
                            }
                            notificationAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void readNotification()
    {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Notifications").document(firebaseUser.getUid())
                .collection("Notifications")
                .addSnapshotListener(new EventListener<QuerySnapshot>()
                {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error)
                    {
                        if (error != null)
                        {
                            Log.e(TAG, "Listen failed.", error);
                            return;
                        }
                        if (snapshot != null && !snapshot.isEmpty())
                        {
                            notificationList.clear();
                            for (DocumentSnapshot document : snapshot.getDocuments())
                            {
                                Notification notification = document.toObject(Notification.class);
                                notificationList.add(notification);
                            }
                            Collections.reverse(notificationList);
                            notificationAdapter.notifyDataSetChanged();
                        }
                        else
                        {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });
    }

}
