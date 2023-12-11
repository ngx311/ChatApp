package com.example.thiago.chatapp;
import com.example.thiago.chatapp.Users;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;



public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private List<Users> searchList;
    private Context context;


    public SearchAdapter(List<Users> searchList) {
        this.searchList = searchList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_find_hits, null, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users user = searchList.get(position);
        holder.username.setText(user.getname());
        if(user.getimage() != null) {
            Glide.with(context).load(user.getimage()).into(holder.profilePicture);
        } else {
            holder.profilePicture.setImageResource(R.drawable.profile_thumbnail);
        }
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profilePicture;
        TextView username;

        public ViewHolder(View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.find_profile_picture);
            username = itemView.findViewById(R.id.usernameTextView);
        }
    }
}

