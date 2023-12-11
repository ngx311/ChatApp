package com.example.thiago.chatapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.thiago.chatapp.ContactsActivity;
import com.example.thiago.chatapp.FindActivity;
import com.example.thiago.chatapp.FragmentActivity;
import com.example.thiago.chatapp.KeyboardActivity;
import com.example.thiago.chatapp.MainActivity;
import com.example.thiago.chatapp.R;
import com.example.thiago.chatapp.StoryActivity;
import com.example.thiago.chatapp.UserPage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserActivityFragment extends Fragment {
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    ImageButton buttonStory, homebtn, btnAddFriends, btnFriends, btnKeyboard;
    Intent intent;
    private String mCurrent_user_id;
    int check = 0;

    private Button buttonLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_user, container, false);


        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mCurrent_user_id);

        buttonLogout = view.findViewById(R.id.btnlogOut2);

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
            }
        });



        return view;
    }
}

