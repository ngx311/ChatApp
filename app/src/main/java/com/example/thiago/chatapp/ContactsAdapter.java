package com.example.thiago.chatapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

    private Context context;
    private ArrayList<CustomContact> contactsList;

    public ContactsAdapter(Context context, ArrayList<CustomContact> contactsList) {
        this.context = context;
        this.contactsList = contactsList != null ? contactsList : new ArrayList<>();
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        CustomContact contact = contactsList.get(position);
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        holder.usernameTextView.setText(contact.getUsername());
        Glide.with(context)
                .load(contact.getImageURL())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.profileImage);

        holder.imageAddFriend.setClickable(false);
    }

    public void updateContactsList(ArrayList<CustomContact> updatedList) {
        this.contactsList = updatedList != null ? updatedList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        CircleImageView profileImage;
        ImageView imageAddFriend;
        TextView sendMessage;

        public ContactViewHolder(View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.contact_profile_picture);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            imageAddFriend = itemView.findViewById(R.id.image_addfriend);
            sendMessage = itemView.findViewById(R.id.send_message);
        }
    }
}