package com.example.thiago.chatapp;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.thiago.chatapp.fragments.ContactsFragment;
import com.example.thiago.chatapp.fragments.FindFragment;
import com.example.thiago.chatapp.fragments.KeyboardFragment;
import com.example.thiago.chatapp.fragments.StoryFragment;
import com.example.thiago.chatapp.fragments.UserActivityFragment;
import com.example.thiago.chatapp.fragments.UserFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FragmentActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private boolean isUserTriggered;
    private DatabaseReference receivedRequestsRef;
    private ValueEventListener receivedRequestsListener;
    private DatabaseReference sentRequestsRef;
    private ValueEventListener sentRequestsListener;
    private DatabaseReference friendsRef;
    private ValueEventListener friendsListener;

    private FirebaseAuth mAuth;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_fragment);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        viewPager2 = findViewById(R.id.viewpager);
        viewPager2.setUserInputEnabled(true);  // Enable swipe

        viewPager2.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return new StoryFragment();
                    case 1:
                        return new UserActivityFragment();
                    case 2:
                        return new FindFragment();
                    case 3:
                        return new ContactsFragment();
                    case 4:
                        return new UserFragment();
                    case 5:
                        return new KeyboardFragment();
                    default:
                        return new StoryFragment();
                }
            }

            @Override
            public int getItemCount() {
                return 6; // as you have 6 fragments
            }
        });

        // Sets the UserFragment as the first fragment
        viewPager2.setCurrentItem(1, false);

        // This sets up the circular navigation
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (!isUserTriggered) {
                    if (position == 0) {
                        isUserTriggered = true;
                        viewPager2.setCurrentItem(5, false);
                    } else if (position == 5) {
                        isUserTriggered = true;
                        viewPager2.setCurrentItem(0, false);
                    }
                } else {
                    isUserTriggered = false;
                }
            }
        });

        // Finding the buttons
        ImageButton btnAddFriendHome = findViewById(R.id.addFriendHome);
        ImageButton btnFriends = findViewById(R.id.btnfriends);
        ImageButton btnLogo = findViewById(R.id.btnlogo);
        ImageButton btnKeyboard = findViewById(R.id.btnkeyboard);
        ImageButton addStory = findViewById(R.id.addStory);

        // Setting up click listeners for the buttons
        btnAddFriendHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager2.setCurrentItem(2, true);
            }
        });

        btnFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager2.setCurrentItem(3, true);
            }
        });

        btnLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager2.setCurrentItem(1, true);
            }
        });

        btnKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager2.setCurrentItem(5, true);
            }
        });

        addStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager2.setCurrentItem(0, true);
            }
        });

        // Setup Firebase references for receivedRequests, sentRequests, and friends
        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID);
        receivedRequestsRef = currentUserRef.child("receivedRequests");
        sentRequestsRef = currentUserRef.child("sentRequests");
        friendsRef = currentUserRef.child("friends");

        // Add listeners for receivedRequests, sentRequests, and friends
        receivedRequestsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Handle received requests data change
                ContactsFragment contactsFragment = (ContactsFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager2.getCurrentItem());
                if (contactsFragment != null) {
                    contactsFragment.handleReceivedRequestsChange(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        };
        receivedRequestsRef.addValueEventListener(receivedRequestsListener);

        sentRequestsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Handle sent requests data change
                ContactsFragment contactsFragment = (ContactsFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager2.getCurrentItem());
                if (contactsFragment != null) {
                    contactsFragment.handleSentRequestsChange(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        };
        sentRequestsRef.addValueEventListener(sentRequestsListener);

        friendsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Handle friends data change
                ContactsFragment contactsFragment = (ContactsFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager2.getCurrentItem());
                if (contactsFragment != null) {
                    contactsFragment.handleFriendsListChange(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        };
        friendsRef.addValueEventListener(friendsListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Remove the listeners when the activity is destroyed
        receivedRequestsRef.removeEventListener(receivedRequestsListener);
        sentRequestsRef.removeEventListener(sentRequestsListener);
        friendsRef.removeEventListener(friendsListener);
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }

}
