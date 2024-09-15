package com.example.abcscialchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
    Button btn;
    EditText email,password;
    FirebaseAuth auth;
    String emailPattern="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth=FirebaseAuth.getInstance();
        btn=findViewById(R.id.logbutton);
        email=findViewById(R.id.editTextLogEmail);
        password=findViewById(R.id.editTextLogPassword);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email=email.getText().toString();
                String pass=password.getText().toString();
                if((TextUtils.isEmpty(Email)))
                {
                    Toast.makeText(login.this, "Enter the email", Toast.LENGTH_SHORT).show();
                }
                else if((TextUtils.isEmpty(pass)))
                {
                    Toast.makeText(login.this, "Enter you password", Toast.LENGTH_SHORT).show();
                }
                else if(!Email.matches(emailPattern))
                {
                    email.setError("Give proper Email Address");
                }
                else if(pass.length()<6)
                {
                    password.setError("More then six characters");
                    Toast.makeText(login.this, "Password need to be longer then six character", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    auth.signInWithEmailAndPassword(Email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                try {
                                    Intent it = new Intent(login.this,MainActivity.class);
                                    startActivity(it);
                                    finish();
                                }catch (Exception e){
                                    Toast.makeText(login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}