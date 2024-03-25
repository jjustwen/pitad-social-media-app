package com.example.doanmobile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.doanmobile.Model.Notification;
import com.example.doanmobile.Model.Post;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class PostActivity extends AppCompatActivity {

    Uri imageUri;
    Uri videoUri;
    String myUrl;
    StorageTask uploadTask;
    StorageReference storageReference;
    FirebaseFirestore db;

    ImageView close;
    ImageView image_added;
    VideoView video_added;
    TextView post;
    EditText description;
    Button btnimage,btnvideo;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        close = findViewById(R.id.close);
        image_added = findViewById(R.id.image_added);
        video_added = findViewById(R.id.video_added);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);
        btnimage = findViewById(R.id.btnimage);
        btnvideo = findViewById(R.id.btnvideo);



        btnimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooserImage();
            }
        });
        btnvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooserVideo();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this, MainActivity.class));
                finish();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUri != null){
                    uploadImage();


                } else{
                    uploadVideo();

                }
            }
        });
    }

    private void openFileChooserImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    private void openFileChooserVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    private void uploadVideo() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting...");
        progressDialog.show();

        if (videoUri != null) {
            storageReference = FirebaseStorage.getInstance().getReference("videos");
            db = FirebaseFirestore.getInstance();
            final StorageReference filereference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(videoUri));

            uploadTask = filereference.putFile(videoUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return filereference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();

                        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        String postID = UUID.randomUUID().toString();
                        String notifyID = UUID.randomUUID().toString();
                        db.collection("Posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                Post post = new Post(postID, myUrl,"video", description.getText().toString(), currentUserID, task.getResult().size() + 1);
                                Notification notification = new Notification(notifyID, currentUserID, "", "đã đăng bài viết mới", postID);


                                db.collection("Notifications").document(notifyID).set(notification);
                                db.collection("Posts").document(postID).set(post)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(PostActivity.this, "Đăng thành công", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(PostActivity.this, MainActivity.class));
                                                    finish();
                                                } else {

                                                    progressDialog.dismiss();
                                                    Toast.makeText(PostActivity.this, "Đăng không thành công", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(PostActivity.this, "Lỗi!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Vui lòng chọn ảnh hoặc video!", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting...");
        progressDialog.show();

        if (imageUri != null) {
            storageReference = FirebaseStorage.getInstance().getReference("posts");
            db = FirebaseFirestore.getInstance();
            final StorageReference filereference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            uploadTask = filereference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return filereference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();

                        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        String postID = UUID.randomUUID().toString();
                        String notifyID = UUID.randomUUID().toString();
                        db.collection("Posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                Post post = new Post(postID, myUrl,"image", description.getText().toString(), currentUserID, task.getResult().size() + 1);
                                Notification notification = new Notification(notifyID, currentUserID, "", "đã đăng bài viết mới", postID);


                                db.collection("Notifications").document(notifyID).set(notification);
                                db.collection("Posts").document(postID).set(post)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(PostActivity.this, "Đăng thành công", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(PostActivity.this, MainActivity.class));
                                                    finish();
                                                } else {

                                                    progressDialog.dismiss();
                                                    Toast.makeText(PostActivity.this, "Đăng không thành công", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(PostActivity.this, "Lỗi!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Vui lòng chọn ảnh hoặc video!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void displayVideo(Uri videoUri) {
        try {
            video_added.setVideoURI(videoUri);
            video_added.requestFocus(); // Yêu cầu fokus cho VideoView để người dùng có thể tương tác với nó
            video_added.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    // Đảm bảo rằng video đã sẵn sàng để phát
                    mediaPlayer.start();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            image_added.setVisibility(View.VISIBLE); // Hiển thị ImageView
            video_added.setVisibility(View.GONE); // Ẩn VideoView
            image_added.setImageURI(imageUri); // Hiển thị ảnh trong ImageView
        } else if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            videoUri = data.getData();
            video_added.setVisibility(View.VISIBLE);
            image_added.setVisibility(View.GONE);
            displayVideo(videoUri);
        } else {
            Toast.makeText(this, "Đã xảy ra lỗi, vui lòng thử lại !", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        }
    }
}