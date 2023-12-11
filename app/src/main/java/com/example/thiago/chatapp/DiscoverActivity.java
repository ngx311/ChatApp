package com.example.thiago.chatapp;

/*
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.firebase.ui.auth.data.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnCompleteListener;



import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;


public class DiscoverActivity extends AppCompatActivity {

    public static Object UserViewHolder;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id, senderUsername, current_username, searchText;
    private TextView user_name;
    private RecyclerView listView;
    View friendRequestLayout;
    private ArrayList<String> friendRequests = new ArrayList<>();
    private ArrayAdapter<String> friendRequestsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, friendRequests);

    private DatabaseReference mDatabase, mUserBase, mCurrent_user, mCurrent_user_username;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findfriend);
        EditText search_bar = findViewById(R.id.toolbar);
        if (search_bar == null) {
            Log.e("onCreate", "search_bar is null!");
            return;
        }
        RecyclerView listView = findViewById(R.id.productList);
        if (listView == null) {
            Log.e("onCreate", "listView is null!");
            return;
        }
        listView.setLayoutManager(new LinearLayoutManager(this));
        String searchText = search_bar.getText().toString();

        ListView friendRequestsListView = (ListView) findViewById(R.id.friend_requests_list);
        ArrayAdapter<String> friendRequestsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, friendRequests);
        friendRequestsListView.setAdapter(friendRequestsAdapter);

        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserBase = mDatabase.child("users");
        mCurrent_user = mUserBase.child(mCurrent_user_id);
        mCurrent_user_username = mCurrent_user.child("username");

        mCurrent_user_username.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String find_username = dataSnapshot.getValue(String.class);

                current_username = find_username;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        UserSearchAdapter userSearchAdapter = new UserSearchAdapter(this, R.layout.activity_find_hits, friendRequests);
        listView.setAdapter(userSearchAdapter);
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                firebaseUserSearch(editable.toString());
            }
        });

        // Add the friend request to the layout
        //TextView friendRequestUsername = findViewById(R.id.friend_request_username);
        //friendRequestUsername.setText(senderUsername + " would like to add you as a friend.");
        //friendRequestLayout.setVisibility(View.VISIBLE);
    }

    private void firebaseUserSearch(String toString) {
        Query firebaseSearchQuery = mUserBase.orderByChild("username").startAt(searchText).endAt(searchText + "\uf8ff");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        // Create a new FirebaseRecyclerOptions using your custom Users model
        FirebaseRecyclerOptions<SignUpActivity.Users> options = new FirebaseRecyclerOptions.Builder<SignUpActivity.Users>()
                .setQuery(usersRef, SignUpActivity.Users.class)
                .build();

        FirebaseRecyclerAdapter<SignUpActivity.Users, addListenerOnTextChange.UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<SignUpActivity.Users, addListenerOnTextChange.UserViewHolder>(options) {
            @NonNull
            @Override
            public addListenerOnTextChange.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Inflate the layout for each user result and create a new UserViewHolder
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_find_hits, parent, false);
                return new addListenerOnTextChange.UserViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull addListenerOnTextChange.UserViewHolder holder, int position, @NonNull SignUpActivity.Users model) {
                // Set the details for each user result
                holder.setDetails(getApplicationContext(), model.getname(), model.getimage());
            }
        };
        listView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }


    //Accept friend request button
    public void acceptFriendRequest(View view) {
        ImageButton friendRequestButton = (ImageButton) view;
        processFriendRequest(friendRequestButton, senderUsername, true);
        friendRequestLayout.setVisibility(View.GONE);
    }

    //Deny friend request button
    public void denyFriendRequest(View view) {
        ImageButton friendRequestButton = (ImageButton) view;
        processFriendRequest(friendRequestButton, senderUsername, false);
        friendRequestLayout.setVisibility(View.GONE);
    }



    class addListenerOnTextChange implements TextWatcher {
        private Context mContext;
        EditText mEdittextview;
        private FirebaseAuth mAuth;
        private String mCurrent_user_id;
        private DatabaseReference mDatabase, mUserBase, mCurrent_user, mCurrent_user_username;
        private RecyclerView listView;
        Query query = mDatabase.child("users");

        public addListenerOnTextChange(Context context, EditText editextview, DatabaseReference mDatabase, RecyclerView listView) {
            super();
            this.mContext = context;
            this.mEdittextview= editextview;
            this.mDatabase = mDatabase;
            this.listView = listView;
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //What you want to do

            firebaseUserSearch(s.toString());



        }



        // View Holder View Class

        public class UserViewHolder extends RecyclerView.ViewHolder {
            View mView;

            public UserViewHolder(@NonNull View itemView) {
                super(itemView);

                mView = itemView;
            }

            public void setDetails(Context ctx, String userName, String userImage){

                TextView user_name = (TextView) mView.findViewById(R.id.find_user_name);
                ImageView user_image = (ImageView) mView.findViewById(R.id.find_profile_picture);


                user_name.setText(userName);

                Glide.with(ctx).load(userImage).into(user_image);


            }
        }



        private void firebaseUserSearch(String searchText) {
            Query firebaseSearchQuery = mUserBase.orderByChild("username").startAt(searchText).endAt(searchText + "\uf8ff");
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference usersRef = database.getReference("users");

            // Create a new FirebaseRecyclerOptions using your custom Users model
            FirebaseRecyclerOptions<SignUpActivity.Users> options = new FirebaseRecyclerOptions.Builder<SignUpActivity.Users>()
                    .setQuery(usersRef, SignUpActivity.Users.class)
                    .build();

            FirebaseRecyclerAdapter<SignUpActivity.Users, UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<SignUpActivity.Users, UserViewHolder>(options) {
                @NonNull
                @Override
                public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    // Inflate the layout for each user result and create a new UserViewHolder
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_find_hits, parent, false);
                    return new UserViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull SignUpActivity.Users model) {
                    // Set the details for each user result
                    holder.setDetails(getApplicationContext(), model.getname(), model.getimage());
                }
            };
            listView.setAdapter(firebaseRecyclerAdapter);
            firebaseRecyclerAdapter.startListening();
        }



    }

    private void searchUsers(String searchText) {
        UserSearchAdapter userSearchAdapter = new UserSearchAdapter(this, R.layout.activity_find_hits, friendRequests);
        // Create an instance of FirebaseFirestore
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        // Query the Firestore Database for users whose usernames match the search text
        firestore.collection("users").whereEqualTo("username", searchText).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // Clear the list of users
                        friendRequests.clear();
                        // Iterate through the query results and add each user's username to the list
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            SignUpActivity.Users user = document.toObject(SignUpActivity.Users.class);
                            if(user !=null)
                                friendRequests.add(user.getname());
                        }
                        // Notify the adapter that the data has changed, so the list view is updated
                        userSearchAdapter.notifyDataSetChanged();
                    }


                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(DiscoverActivity.this, "Error searching users", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private class UserSearchAdapter extends ArrayAdapter<String> implements ListAdapter {

        private List<String> allItems;
        private List<String> filteredItems;

        public UserSearchAdapter(Context context, int resource, List<String> items) {
            super(context, resource, items);
            this.allItems = new ArrayList<>(items);
            this.filteredItems = new ArrayList<>(items);
        }

        @Override
        public int getCount() {
            return filteredItems.size();
        }

        @Override
        public String getItem(int position) {
            return filteredItems.get(position);
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    List<String> suggestions = new ArrayList<>();
                    if (constraint == null || constraint.length() == 0) {
                        suggestions.addAll(allItems);
                    } else {
                        String filterPattern = constraint.toString().toLowerCase().trim();
                        for (String item : allItems) {
                            if (item.toLowerCase().contains(filterPattern)) {
                                suggestions.add(item);
                            }
                        }
                    }
                    results.values = suggestions;
                    results.count = suggestions.size();
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredItems.clear();
                    filteredItems.addAll((List) results.values);
                    notifyDataSetChanged();
                }
            };
        }
    }



    public void sendFriendRequest(String friendUsername) {
        // Check if the user is already logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }

        // Check if the user has already sent a friend request to the specified user
        String currentUsername = mCurrent_user_id;
        DatabaseReference friendRequestsRef = FirebaseDatabase.getInstance().getReference()
                .child("friend_requests").child(friendUsername);
        friendRequestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(currentUsername)) {
                    return;
                }

                // Check if the specified user is already a friend
                DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference()
                        .child("friends").child(currentUsername);
                friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(friendUsername)) {
                            return;
                        }

                        // Send the friend request
                        Map<String, Object> friendRequest = new HashMap<>();
                        friendRequest.put("timestamp", System.currentTimeMillis());
                        friendRequestsRef.child(currentUsername).setValue(friendRequest);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    public void displayPendingFriendRequests() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }
        String currentUsername = mCurrent_user_id;
        DatabaseReference friendRequestsRef = FirebaseDatabase.getInstance().getReference().child("friend_requests").child(currentUsername);
        friendRequestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
                    String senderUsername = requestSnapshot.getKey();
                    long timestamp = (long) requestSnapshot.child("timestamp").getValue();
                    // format and display the friend request information to the user
                    // for example:
                    // Log.d("Friend request","You have a friend request from "+senderUsername+" sent at "+ timestamp);
                    System.out.println("You have a friend request from " + senderUsername + " sent at " + timestamp);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    public void processFriendRequest(final ImageButton friendRequestButton, String senderUsername, boolean accept) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }
        String currentUsername = mCurrent_user_id;

        // Get a reference to the sender's friend request
        DatabaseReference friendRequestsRef = FirebaseDatabase.getInstance().getReference()
                .child("friend_requests").child(currentUsername).child(senderUsername);

        if(accept){
            //Accept the friend request
            friendRequestButton.setImageResource(R.drawable.friends);
            // Create a new child node under the "friends" reference
            DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference()
                    .child("friends").child(currentUsername);
            Map<String, Object> friend = new HashMap<>();
            friend.put("timestamp", System.currentTimeMillis());
            friendsRef.child(senderUsername).setValue(friend);
            // Create a new child node under the "friends" reference for the sender
            friendsRef = FirebaseDatabase.getInstance().getReference()
                    .child("friends").child(senderUsername);
            friend = new HashMap<>();
            friend.put("timestamp", System.currentTimeMillis());
            friendsRef.child(currentUsername).setValue(friend);

            // Remove the sender's friend request
            friendRequestsRef.removeValue();

        }else{
            //Deny the friend request
            friendRequestsRef.removeValue();
            ListView friendRequestsListView = (ListView) findViewById(R.id.friend_requests_list);
            friendRequestsListView.setAdapter(friendRequestsAdapter);
            int position = friendRequestsAdapter.getPosition(senderUsername);
            // remove the item from the adapter
            friendRequestsAdapter.remove(friendRequestsAdapter.getItem(position));
            // notify the adapter that the data has changed
            friendRequestsAdapter.notifyDataSetChanged();
        }
    }








    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

*/