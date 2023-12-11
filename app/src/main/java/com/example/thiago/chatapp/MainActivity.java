package com.example.thiago.chatapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

//import com.algolia.search.saas.Client;
//import com.algolia.search.saas.Index;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends Activity {

    private static int SPLASH_TIME_OUT = 3000;
    private Button goSignUp;
    private Button goLogIn;
    private FirebaseAuth firebaseAuth;
    //Client client = new Client("KG8J2X5I9X", "5245daa6cfc9412ce550aca54e5e9d6f");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        goSignUp = (Button) findViewById(R.id.btnSignUp);
        goLogIn = (Button) findViewById(R.id.btnLogIn);

        firebaseAuth = FirebaseAuth.getInstance();



        // File storage is needed for profile image upload and image messages
        //FirebaseFileStorageModule.activate();

        //if getCurrentUser does not returns null
        if (firebaseAuth.getCurrentUser() != null) {
            //that means user is already logged in
            //so close this activity
            finish();

            //and open profile activity
            startActivity(new Intent(getApplicationContext(), FragmentActivity.class));
        }


        goSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), SignUpActivity.class);
                startActivity(intent);

            }

        });

        goLogIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), LogInActivity.class);
                startActivity(intent);

            }

        });
    }
}

