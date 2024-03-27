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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class RegisterActivity extends AppCompatActivity
{
    private EditText username;
    private EditText fullname;
    private EditText repasswd;
    private EditText email;
    private EditText password;
    private Button register;
    private TextView txt_login;
    private FirebaseAuth auth;
    private static int usernameFlag = 0;
    private ProgressDialog pd;
    private DatabaseReference reference;
    private FirebaseFirestore db;
    private TextView usernameStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        txt_login = findViewById(R.id.txt_login);
        usernameStatus = findViewById(R.id.availability_username);
        repasswd = findViewById(R.id.repassword);
//        usernameCheck = findViewById(R.id.check_username);
        txt_login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                String str_username = username.getText().toString();
                String str_fullname = fullname.getText().toString();
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();
                String str_repasswd = repasswd.getText().toString();

                if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname) ||
                        TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password) || TextUtils.isEmpty(str_repasswd))
                {
                    Toast.makeText(RegisterActivity.this, "Không được để trống !", Toast.LENGTH_SHORT).show();
                }
                else if (str_password.length() < 6)
                {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu phải nhiều hơn 6 ký tự.", Toast.LENGTH_SHORT).show();
                }
                else if (str_repasswd.compareTo(str_password) != 0)
                {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu không khớp.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    RegisterUser(str_username, str_fullname, str_email, str_password);
                }
            }
        });
    }

    private void RegisterUser(final String username, final String fullname, String email, String password)
    {


        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {

                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    String userid = firebaseUser.getUid();
                    User new_user = new User(userid, username, fullname, "default", "", password, email);
                    db.collection("Users").document(userid)
                            .set(new_user)
                            .addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });

                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(RegisterActivity.this, "Đăng ký không thành công", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void checkEmailAvailable()
    {
        final String txt_email = email.getText().toString();
        usernameStatus.setVisibility(View.VISIBLE);
        db = FirebaseFirestore.getInstance();
        db.collection("Users").whereEqualTo("email", txt_email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    if (!task.getResult().isEmpty())
                    {
                        usernameFlag = 1;
                        usernameStatus.setText("Không thể sử dụng");
                        Toast.makeText(RegisterActivity.this, "Email đã tồn tại, hãy thử Email khác!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else
                    {
                        usernameStatus.setText("Có thể sử dụng");
                        usernameFlag = 0;
                    }
                }
                else
                {
                    Toast.makeText(RegisterActivity.this, "Đã xảy ra lỗi khi kiểm tra email nguười dùng: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    private void checkUsernameAvailability()
    {

        final String txt_username = username.getText().toString();

        usernameStatus.setVisibility(View.VISIBLE);
        db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .whereEqualTo("username", txt_username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            if (!task.getResult().isEmpty())
                            {
                                usernameFlag = 1;
                                usernameStatus.setText("Không thể sử dụng");
                                Toast.makeText(RegisterActivity.this, "Tên đã tồn tại, hãy thử tên khác!", Toast.LENGTH_LONG).show();
                                finish();
                            }
                            else
                            {
                                usernameStatus.setText("Có thể sử dụng");
                                usernameFlag = 0;
                            }
                        }
                        else
                        {
                            Toast.makeText(RegisterActivity.this, "Đã xảy ra lỗi khi kiểm tra tên nguười dùng: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });
    }
}

