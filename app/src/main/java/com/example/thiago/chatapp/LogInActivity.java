package com.example.thiago.chatapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

public class LogInActivity extends Activity {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonForgot;
    private ProgressBar progressBar;
    private CheckBox saveLoginCheckBox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    private static final String APP_ID = "068D66D2-5466-45C2-9810-8D90E70C05C2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //initializing views
        editTextEmail = (EditText) findViewById(R.id.login_input_email);
        editTextPassword = (EditText) findViewById(R.id.login_input_password);

        buttonForgot = (Button) findViewById(R.id.btnForgot);
        buttonLogin = (Button) findViewById(R.id.btnLogIn2);
        progressBar = (ProgressBar) findViewById(R.id.determinateBar);
        saveLoginCheckBox = (CheckBox)findViewById(R.id.saveLoginCheckBox);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin) {
            editTextEmail.setText(loginPreferences.getString("email", ""));
            editTextPassword.setText(loginPreferences.getString("password", ""));
            saveLoginCheckBox.setChecked(true);
        }

        //if getCurrentUser does not returns null
        if (firebaseAuth.getCurrentUser() != null) {
            //that means user is already logged in
            //so close this activity
            finish();

            //and open profile activity
            startActivity(new Intent(getApplicationContext(), FragmentActivity.class));
        }

        buttonForgot.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(LogInActivity.this, ForgotActivity.class);
                startActivity(i);
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                progressBar.setVisibility(ProgressBar.VISIBLE);
                userLogin();
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                if (saveLoginCheckBox.isChecked()) {
                    loginPrefsEditor.putBoolean("saveLogin", true);
                    loginPrefsEditor.putString("email", email);
                    loginPrefsEditor.putString("password", password);
                    loginPrefsEditor.apply();
                } else {
                    loginPrefsEditor.clear();
                    loginPrefsEditor.commit();
                }

            }
        });
    }

    //method for user login
    private void userLogin() {
        String emailOrUsername = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(emailOrUsername)) {
            Toast.makeText(this, "Please enter email or username", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }

        if (isEmailValid(emailOrUsername)) {
            // Try to log in with email
            firebaseAuth.signInWithEmailAndPassword(emailOrUsername, password)
                    .addOnCompleteListener(this, getOnCompleteListener());
        } else {
            // Assume it's a username and query the database
            mDatabase.child("users").orderByChild("username").equalTo(emailOrUsername)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Username exists, get the email
                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                    Users user = userSnapshot.getValue(Users.class);
                                    if (user != null) {
                                        // Try to log in with the obtained email
                                        firebaseAuth.signInWithEmailAndPassword(user.getemail(), password)
                                                .addOnCompleteListener(LogInActivity.this, getOnCompleteListener());
                                    }
                                }
                            } else {
                                Toast.makeText(LogInActivity.this, "Username not found", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Error handling
                        }
                    });
        }
    }

    private OnCompleteListener<AuthResult> getOnCompleteListener() {
        return new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    finish();
                    startActivity(new Intent(getApplicationContext(), FragmentActivity.class));
                } else {
                    Toast.makeText(LogInActivity.this, "Login failed", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private boolean isEmailValid(String email) {
        String validemail = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+";
        Matcher matcherObj = Pattern.compile(validemail).matcher(email);
        return matcherObj.matches();
    }
}