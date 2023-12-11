package com.example.thiago.chatapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
//import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;

import static com.android.volley.VolleyLog.TAG;

//import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * Created by Thiago on 10/11/2016.
 */
public class ForgotActivity extends Activity {
    private Button forgotpassword;
    private EditText emailAddress;
    private String emailString;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        emailAddress = (EditText) findViewById(R.id.editText);
        emailString = emailAddress.getText().toString();
        String email = emailAddress.getText().toString().trim();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        forgotpassword = (Button) findViewById(R.id.btnForgot2);

        forgotpassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mAuth.sendPasswordResetEmail(emailString)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Email sent.");
                                }
                            }
                        });


            }
        });

    }


}

