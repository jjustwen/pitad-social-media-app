package com.example.doanmobile;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doanmobile.Model.Post;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;
import java.util.UUID;

public class PostActivity extends AppCompatActivity
{

    Uri imageUri;
    String myUrl;
    StorageTask uploadTask;
    StorageReference storageReference;
    FirebaseFirestore db;

    ImageView close;
    ImageView image_added;
    TextView post;
    EditText description;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        close = findViewById(R.id.close);
        image_added = findViewById(R.id.image_added);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);

        storageReference = FirebaseStorage.getInstance().getReference("posts");
        db = FirebaseFirestore.getInstance();

        image_added.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openFileChooser();
            }
        });
        close.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(PostActivity.this, MainActivity.class));
                finish();
            }
        });

        post.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                uploadImage();
            }
        });
    }

    private void openFileChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void uploadImage()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting...");
        progressDialog.show();

        if (imageUri != null)
        {
            final StorageReference filereference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            uploadTask = filereference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation()
            {
                @Override
                public Object then(@NonNull Task task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return filereference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>()
            {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if (task.isSuccessful())
                    {
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();


                        String postID = UUID.randomUUID().toString();
                        Post post = new Post(postID, myUrl, description.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getUid());
                        db.collection("Posts").document(postID).set(post)
                                .addOnCompleteListener(new OnCompleteListener<Void>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if (task.isSuccessful())
                                        {
                                            progressDialog.dismiss();
                                            Toast.makeText(PostActivity.this, "Đăng thành công", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(PostActivity.this, MainActivity.class));
                                            finish();
                                        }
                                        else
                                        {

                                            progressDialog.dismiss();
                                            Toast.makeText(PostActivity.this, "Đăng không thành công", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(PostActivity.this, "Lỗi!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    progressDialog.dismiss();
                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            progressDialog.dismiss();
            Toast.makeText(this, "Chưa chọn hình ảnh !", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            imageUri = data.getData();
            image_added.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(this, "Đã xảy ra lỗi, vui lòng thử lại !", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        }
    }

}

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            imageUri = result.getUri();
//
//            image_added.setImageURI(imageUri);
//        } else {
//            Toast.makeText(this, "Something went wrong , try again!", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(PostActivity.this , MainActivity.class));
//            finish();
//        }
//
//    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        final ArrayAdapter<Hashtag> hashtagAdapter = new HashtagArrayAdapter<>(getApplicationContext());
//        final DatabaseReference mHashTagRef = FirebaseDatabase.getInstance().getReference().child("HashTags");
//
//        mHashTagRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    hashtagAdapter.add(new Hashtag(snapshot.getKey() , (int)snapshot.getChildrenCount()));
//                    Log.d("HashTag" , snapshot.getKey());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        description.setHashtagAdapter(hashtagAdapter);
//    }
