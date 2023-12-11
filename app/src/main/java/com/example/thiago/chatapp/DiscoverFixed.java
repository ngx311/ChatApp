package com.example.thiago.chatapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

//import com.algolia.instantsearch.core.InstantSearch;
//import com.algolia.instantsearch.core.helpers.Searcher;
//import com.algolia.instantsearch.ui.utils.ItemClickSupport;
//import com.algolia.instantsearch.ui.views.Hits;
//import com.algolia.instantsearch.core.hits.HitsView;
//import com.algolia.search.model.search.ResponseFields;
//import com.algolia.search.saas.Client;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

/*

public class DiscoverFixed extends AppCompatActivity {
    private ResponseFields.Hits hits;
    private ResponseFields.Hits getHits;
    private static final String ALGOLIA_APP_ID = "KG8J2X5I9X";
    private static final String ALGOLIA_SEARCH_API_KEY = "5245daa6cfc9412ce550aca54e5e9d6f";
    private static final String ALGOLIA_INDEX_NAME = "user_index";
    Client client = new Client("KG8J2X5I9X", "c50f0d5ea77f5e6b73ab4bfce90ab3ed");
    private Searcher searcher;
    private InstantSearch helper;
    private EditText mSearchbar;
    private ImageButton addicon;
    private CircleImageView ProfileImg;
    private TextView ItemName;
    private RecyclerView rvItems;
    private EndlessRecyclerViewScrollListener scrollListener;

    GridAdapter adapter;

    private String userchild;
    private String hit_user_id;
    private String newfriend;
    private String newfriendid;
    private String current_username;
    private String searchText;
    private TextView user_name;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findfriend);
        searcher = Searcher.create(ALGOLIA_APP_ID, ALGOLIA_SEARCH_API_KEY, ALGOLIA_INDEX_NAME);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String mCurrent_user_id = mAuth.getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mUserBase = mDatabase.child("users");
        DatabaseReference mCurrent_user = mUserBase.child(mCurrent_user_id);
        DatabaseReference mCurrent_user_username = mCurrent_user.child("username");

        //Query query = mDatabase.child("users");

        mSearchbar = (EditText) findViewById(R.id.searchbar);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setFocusable(false);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

        adapter = new GridAdapter(this);
        List<JSONObject> array = new ArrayList<JSONObject>();
        hits = (ResponseFields.Hits) findViewById(R.id.search_hits);

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {


            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);


            }
        };
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener);


        hits.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {


            @Override
            public void onItemClick(RecyclerView recyclerView, int position, View v) {
                addicon = findViewById(R.id.image_addfriend);
                String hit_username;
                JSONObject hit = hits.get(position);
                // Do something with the hit



                addicon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Drawable[] layers = new Drawable[2];
                        layers[0] = getResources().getDrawable(R.drawable.add_friend_icon_final);
                        layers[1] = getResources().getDrawable(R.drawable.waving_hand);
                        TransitionDrawable transition = new TransitionDrawable(layers);
                        transition.setCrossFadeEnabled(true);
                        addicon.setImageDrawable(transition);



                        try{
                            JSONObject object = hits.get(position);
                            newfriendid = object.getString("objectID");
                            newfriend = object.getString("username");
                            mDatabase.child("friends").child(mCurrent_user_id).child(newfriendid).setValue(newfriend);
                            mDatabase.child("friends").child(newfriendid).child(mCurrent_user_id).setValue(current_username);




                            array.add(

                                    new JSONObject().put("username", newfriend.trim()).put("friend", mCurrent_user_id)
                            );

                            array.add(

                                    new JSONObject().put("username", mCurrent_user_username).put("friend", newfriend).put("objectID", newfriendid)
                            );
                            //index.addObjectsAsync(new JSONArray(array), null);

                            //Algolia Database
                            friendindex.addObjectsAsync(new JSONArray(array), null);
                        }catch(JSONException e){
                            // Recovery
                        }
                    }
                });





                Intent i = new Intent(DiscoverFixed.this, FriendProfile.class);
                startActivity(i);
            }
        });











    }


    @Override
    protected void onDestroy() {
        searcher.destroy();
        super.onDestroy();
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
    }
}

*/

