package com.example.abcscialchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class splash extends AppCompatActivity {
    ImageView logo;
    TextView logoname, own1, own2;
    Animation topAnim, bottomAnim;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        logo=findViewById(R.id.logoimg);
        logoname=findViewById(R.id.logonameimg);
        own1=findViewById(R.id.ownone);
        own2=findViewById(R.id.owntwo);
        auth=FirebaseAuth.getInstance();

        topAnim= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim=AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        logo.setAnimation(topAnim);
        logoname.setAnimation(bottomAnim);
        own1.setAnimation(bottomAnim);
        own2.setAnimation(bottomAnim);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (auth.getCurrentUser() != null) {
                    Intent intent = new Intent(splash.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
                else
                {
                    Intent it = new Intent(splash.this,login.class);
                    startActivity(it);
                }
            }
        },4000);
    }
}