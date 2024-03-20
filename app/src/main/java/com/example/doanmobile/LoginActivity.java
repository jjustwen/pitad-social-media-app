package com.example.doanmobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doanmobile.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity
{

    private EditText email;
    private EditText password;
    private Button login;
    private TextView txt_signup;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        txt_signup = findViewById(R.id.txt_signup);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        txt_signup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
//        login.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
//                {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task)
//                    {
//                        for (DocumentSnapshot document : task.getResult().getDocuments())
//                        {
//                            User user_login = document.toObject(User.class);
//                            if (user_login.getEmail().equals(email.getText().toString()) && user_login.getPasswd().equals(password.getText().toString()))
//                            {
//                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                            }
//                        }
//                    }
//                });
//            }
//        });
        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                if (TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password))
                {
                    Toast.makeText(LoginActivity.this, "Không được để trống !", Toast.LENGTH_LONG).show();
                }
                else
                {
                    auth.signInWithEmailAndPassword(str_email, str_password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("Users").document(auth.getCurrentUser().getUid())
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                                        {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot)
                                            {
                                                pd.dismiss();
                                                if (documentSnapshot.exists())
                                                {
                                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                                else
                                                {
                                                    Toast.makeText(LoginActivity.this, "Không tìm thấy tài khoản.", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener()
                                        {
                                            @Override
                                            public void onFailure(@NonNull Exception e)
                                            {
                                                pd.dismiss();
                                                Toast.makeText(LoginActivity.this, "Đã xảy ra lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                            else
                            {
                                pd.dismiss();
                                Toast.makeText(LoginActivity.this, "Lỗi xác minh tài khoản.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

    }
}