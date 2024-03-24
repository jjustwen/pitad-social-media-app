package com.example.doanmobile;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.doanmobile.Fragment.ProfileFragment;
import com.example.doanmobile.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity
{

    private ImageView close;
    private ImageView image_profile;
    private TextView save;
    private TextView tv_change;
    private EditText fullname;
    private EditText username;
    private EditText bio;

    private FirebaseUser firebaseUser;

    private Uri mImageUri;
    private StorageTask uploadTask;
    private StorageReference storageRef;
    private static final int PICK_IMAGE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        close = findViewById(R.id.close);
        image_profile = findViewById(R.id.image_profile);
        save = findViewById(R.id.save);
        tv_change = findViewById(R.id.tv_change);
        fullname = findViewById(R.id.fullname);
        username = findViewById(R.id.username);
        bio = findViewById(R.id.bio);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference("uploads");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("Users").document(firebaseUser.getUid());

        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>()
        {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e)
            {


                if (e != null)
                {
                    Log.w(TAG, "Lỗi", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists())
                {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null)
                    {
                        fullname.setText(user.getFullname());
                        username.setText(user.getUsername());
                        bio.setText(user.getBio());
                        if (!isDestroyed())
                        {
                            Glide.with(EditProfileActivity.this)
                                    .load(user.getImageurl())
                                    .apply(RequestOptions.placeholderOf(R.drawable.default_avatar))
                                    .into(image_profile);
                        }

                    }
                }
                else
                {
                    Log.d(TAG, "Lỗi");
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        tv_change.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        image_profile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateProfile(fullname.getText().toString(),
                        username.getText().toString(),
                        bio.getText().toString());
                startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
            }
        });
    }

    private void updateProfile(String fullname, String username, String bio)
    {

        if (fullname.isEmpty() || username.isEmpty())
        {
            Toast.makeText(this, "Không được để trống thông tin! ", Toast.LENGTH_SHORT).show();
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("Users").document(firebaseUser.getUid());

        Map<String, Object> data = new HashMap<>();
        data.put("fullname", fullname);
        data.put("username", username);
        data.put("bio", bio);
        userRef.update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        Toast.makeText(EditProfileActivity.this, "Cập nhật thành công", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(EditProfileActivity.this, "Cập nhật không thành công", Toast.LENGTH_LONG).show();
                    }
                });
        startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
        finish();

    }

    private String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage()
    {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Đang tải lên...");
        pd.show();

        if (mImageUri != null)
        {
            final StorageReference filereference = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));

            uploadTask = filereference.putFile(mImageUri);
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
                        String myUrl = downloadUri.toString();

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference userRef = db.collection("Users").document(firebaseUser.getUid());

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("imageurl", myUrl);

                        userRef.set(hashMap, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>()
                                {
                                    @Override
                                    public void onSuccess(Void aVoid)
                                    {
                                        Toast.makeText(EditProfileActivity.this, "Tải ảnh thành công", Toast.LENGTH_SHORT).show();
                                        pd.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener()
                                {
                                    @Override
                                    public void onFailure(@NonNull Exception e)
                                    {
                                        Toast.makeText(EditProfileActivity.this, "Tải ảnh thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        pd.dismiss();
                                    }
                                });
                    }
                    else
                    {
                        Toast.makeText(EditProfileActivity.this, "Lỗi!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            Toast.makeText(EditProfileActivity.this, "Chưa chọn hình ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            mImageUri = data.getData();
            image_profile.setImageURI(mImageUri);
            uploadImage();
        }
        else
        {
            Toast.makeText(this, "Đã xảy ra lỗi! ", Toast.LENGTH_SHORT).show();
        }
    }


}