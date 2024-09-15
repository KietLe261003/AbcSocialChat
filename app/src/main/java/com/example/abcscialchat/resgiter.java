package com.example.abcscialchat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resgiter);
        loginButon=findViewById(R.id.loginbutton);
        rg_username=findViewById(R.id.rgusername);
        rg_email=findViewById(R.id.rgemail);
        rg_password=findViewById(R.id.rgepassword);
        rg_epassword=findViewById(R.id.rgepassword);
        signupButon=findViewById(R.id.signupbutton);
        rg_imageProfile=findViewById(R.id.profilerg0);

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