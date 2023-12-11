package com.example.thiago.chatapp;
/*
import com.example.thiago.chatapp.Users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class SearchActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private DatabaseReference mDatabase, mUserBase, mCurrent_user, mCurrent_user_username;
    private RecyclerView searchRecyclerView;
    private SearchAdapter searchAdapter;
    private List<Users> searchList;
    private EditText searchEditText;
    private CircleImageView image_profile;
    private TextView sendRequest;



    private ValueEventListener userListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findfriend);

        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserBase = mDatabase.child("users");
        mCurrent_user = mUserBase.child(mCurrent_user_id);
        mCurrent_user_username = mCurrent_user.child("username");

        searchList = new ArrayList<>();
        searchRecyclerView = findViewById(R.id.searchRecyclerView);
        searchRecyclerView.setHasFixedSize(true);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchAdapter = new SearchAdapter(searchList);
        searchRecyclerView.setAdapter(searchAdapter);


        searchEditText = findViewById(R.id.toolbar);
        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users user = snapshot.getValue(Users.class);
                    if (user.getname().contains(searchEditText.getText().toString()) && !user.getname().equals(mCurrent_user_id)) {
                        searchList.add(user);
                    }

                }
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mUserBase.addListenerForSingleValueEvent(userListener);

        searchEditText = findViewById(R.id.toolbar);
        mUserBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users user = snapshot.getValue(Users.class);
                    if (user.getname().contains(searchEditText.getText().toString()) && !user.getname().equals(mCurrent_user_id)) {
                        searchList.add(user);
                    }
                }
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //This method is called before the text is changed, you can leave it empty
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchList.clear();
                final String searchText = charSequence.toString();
                if (!searchText.isEmpty()) {
                    mUserBase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            searchList.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Users user = snapshot.getValue(Users.class);
                                if (user.getname().contains(searchText) && !user.getname().equals(mCurrent_user_id)) {
                                    searchList.add(user);
                                }
                            }
                            searchAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle error
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //This method is called after the text is changed, you can leave it empty
            }
        });


        ImageView imageAddFriend = findViewById(R.id.image_addfriend);
        imageAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView sendRequest = findViewById(R.id.send_request);
                sendRequest.setText("REQUEST SENT!");
                SpannableStringBuilder ssb = new SpannableStringBuilder("REQUEST SENT!");
                ssb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                sendRequest.setText(ssb);

            }
        });




    }
}


*/

