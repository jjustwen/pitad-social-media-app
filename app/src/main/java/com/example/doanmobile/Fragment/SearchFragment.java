package com.example.doanmobile.Fragment;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanmobile.Adapter.UserAdapter;
import com.example.doanmobile.Model.User;
import com.example.doanmobile.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment
{
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private ImageView explore_people;
    private List<User> mUsers;
    private EditText search_bar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        explore_people = view.findViewById(R.id.explore_people);
        search_bar = view.findViewById(R.id.search_bar);
        explore_people.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = auth.getCurrentUser();
                String curUserID = firebaseUser.getUid().toString();
                db.collection("Users").document(curUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                    {
                        User me = task.getResult().toObject(User.class);
                        db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task)
                            {
                                mUsers.clear();
                                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments())
                                {

                                    User people = documentSnapshot.toObject(User.class);
                                    if (!me.getFollowing().contains(people.getId()))
                                    {
                                        mUsers.add(people);
                                    }
                                }

                            }
                        });
                        userAdapter.notifyDataSetChanged();
                    }
                });

            }
        });
        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), mUsers, true);
        recyclerView.setAdapter(userAdapter);

        search_bar.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                searchUser(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });

        return view;
    }

    private void searchUser(String s)
    {
        recyclerView.setVisibility(View.VISIBLE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String searchKey = s.toLowerCase(); // Chuyển đổi chuỗi tìm kiếm thành chữ thường

        Query query = db.collection("Users")
                .orderBy("username") // Sắp xếp theo username trước khi áp dụng startAt và endAt
                .startAt(searchKey)
                .endAt(searchKey + "\uf8ff");

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                mUsers.clear();

                for (QueryDocumentSnapshot doc : queryDocumentSnapshots)
                {
                    User user = doc.toObject(User.class);
                    mUsers.add(user);
                }

                userAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.w(TAG, "Lỗi khi đọc dữ liệu", e);
            }
        });
    }


}