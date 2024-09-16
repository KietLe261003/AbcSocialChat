package com.example.abcscialchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;

import de.hdodenhof.circleimageview.CircleImageView;

public class resgiter extends AppCompatActivity {
    TextView loginButon;
    EditText rg_username,rg_email,rg_password,rg_epassword;
    Button signupButon;
    CircleImageView rg_imageProfile;
    FirebaseAuth auth;
    Uri imgURI;
    String imguri;
    String emailPattern="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    FirebaseDatabase database;
    FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resgiter);
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        loginButon=findViewById(R.id.loginbutton);
        rg_username=findViewById(R.id.rgusername);
        rg_email=findViewById(R.id.rgemail);
        rg_password=findViewById(R.id.rgepassword);
        rg_epassword=findViewById(R.id.rgepassword);
        signupButon=findViewById(R.id.signupbutton);
        rg_imageProfile=findViewById(R.id.profilerg0);
        auth=FirebaseAuth.getInstance();
        loginButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(resgiter.this,login.class);
                startActivity(it);
                finish();
            }
        });
        signupButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName= rg_username.getText().toString();
                String email=rg_email.getText().toString();
                String pass=rg_password.getText().toString();
                String cpass=rg_epassword.getText().toString();
                String status = "Hello world";
                if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(cpass))
                {
                    Toast.makeText(resgiter.this, "Please Enter valid Information", Toast.LENGTH_SHORT).show();
                }
                else if(!email.matches(emailPattern))
                {
                    rg_email.setText("Type a valid email here");
                }
                else if(pass.length()<6)
                {
                    rg_password.setText("Password must be 6 character or more");
                }
                else if(!pass.equals(cpass))
                {
                    rg_password.setText("The password doesn't match");
                }
                else
                {
                    auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isComplete())
                            {
                                String id = task.getResult().getUser().getUid();
                                DatabaseReference reference = database.getReference().child("user").child(id);
                                StorageReference strongreference = storage.getReference().child("Upload").child(id);
                                if(imgURI!=null)
                                {
                                    strongreference.putFile(imgURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isComplete())
                                            {
                                                strongreference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imguri=uri.toString();
                                                        User user = new User(imguri,userName,pass,id,status,email);
                                                        reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isComplete())
                                                                {
                                                                    Intent it = new Intent(resgiter.this,MainActivity.class);
                                                                    startActivity(it);
                                                                    finish();
                                                                }
                                                                else
                                                                {
                                                                    Toast.makeText(resgiter.this, "Error for Creating the user", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                                else
                                {
                                    String status = "Hello world";
                                    imguri="https://firebasestorage.googleapis.com/v0/b/socialchat-9ff4d.appspot.com/o/man.png?alt=media&token=74555cd7-55c1-4d5e-9f3e-7e765cfb9342";
                                    User user = new User(imguri,userName,pass,id,status,email);
                                    reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isComplete())
                                            {
                                                Intent it = new Intent(resgiter.this,MainActivity.class);
                                                startActivity(it);
                                                finish();
                                            }
                                            else
                                            {
                                                Toast.makeText(resgiter.this, "Error for Creating the user", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }
                            }
                            else
                            {
                                Toast.makeText(resgiter.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        rg_imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.setType("image/*");
                it.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(it,"Select Picture"),10);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==10)
        {
            if(data!=null)
            {
                imgURI=data.getData();
                rg_imageProfile.setImageURI(imgURI);
            }
        }
    }
}