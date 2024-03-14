package com.example.doanmobile.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanmobile.Adapter.MyFotoAdapter;
import com.example.doanmobile.Adapter.PostAdapter;
import com.example.doanmobile.Model.Post;
import com.example.doanmobile.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class HomeFragment extends Fragment
{

    private RecyclerView recyclerView;
    //    private PostAdapter postAdapter;
    private PostAdapter postAdapter;
    private ArrayList<Post> postList;
    FirebaseFirestore db;

//    private List<String> followingList;

    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_story);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);
        db = FirebaseFirestore.getInstance();
//        recyclerView_story = view.findViewById(R.id.recycler_view_story);
//        recyclerView_story.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext() ,
//                LinearLayoutManager.HORIZONTAL , false);
//        recyclerView_story.setLayoutManager(linearLayoutManager1);


//        progressBar = view.findViewById(R.id.progress_circular);

//        checkFollowing();
        loadPosts();
        return view;
    }

    void loadPosts()
    {
        db.collection("Posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @SuppressLint("NotifyDataSetChanged")
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
                            postAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
//    private void checkFollowing()
//    {
//        followingList = new ArrayList<>();
//
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("Follow")
//                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .collection("following")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
//                {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task)
//                    {
//                        if (task.isSuccessful())
//                        {
//                            for (DocumentSnapshot document : task.getResult())
//                            {
//                                followingList.add(document.getId());
//                            }
//                            readPosts();
//                        }
//                    }
//                });
//    }

//    private void readPosts()
//    {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("Posts")
//                .orderBy("timestamp", Query.Direction.DESCENDING)
//                .addSnapshotListener(getActivity(), (value, error) ->
//                    {
//                    if (error != null)
//                    {
//                        return;
//                    }
//                    if (value != null)
//                    {
//                        postList.clear();
//                        for (DocumentChange dc : value.getDocumentChanges())
//                        {
//                            if (dc.getType() == DocumentChange.Type.ADDED)
//                            {
//                                Post post = dc.getDocument().toObject(Post.class);
//                                for (String id : followingList)
//                                {
//                                    if (post.getPublisher().equals(id))
//                                    {
//                                        postList.add(post);
//                                        postAdapter.notifyDataSetChanged();
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//                        progressBar.setVisibility(View.GONE);
//                    }
//                    });
//    }


}