package com.example.thiago.chatapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.thiago.chatapp.MainActivity;
import com.example.thiago.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class UserFragment extends Fragment {
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private DatabaseReference mDatabase, mUserBase, mCurrent_user;
    private Button buttonLogout;
    private TextView user_name;
    private CircleImageView profile_picture;
    private static final int TAKE_IMAGE_CODE = 10001;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_userprofile, container, false);

        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserBase = mDatabase.child("users");
        mCurrent_user = mUserBase.child(mCurrent_user_id);

        buttonLogout = view.findViewById(R.id.btnlogOut);
        user_name = view.findViewById(R.id.text_view_nickname);
        CircleImageView profile_picture = view.findViewById(R.id.image_view_profile);
        profile_picture.setOnClickListener(this::handleImageClick);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mCurrent_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("username").getValue().toString();
                user_name.setText(username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("UserListActivity", "Error occurred");
            }
        });

        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(requireActivity()).load(user.getPhotoUrl()).into(profile_picture);
            }
        }

        buttonLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(requireActivity(), MainActivity.class);
            startActivity(i);
        });

        return view;
    }

    public void handleImageClick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            activityResultLauncher.launch(intent);
        }
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                        profile_picture.setImageBitmap(bitmap);
                        handleUpload(bitmap);
                    }
                }
            }
    );

    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        String child = "profileImages/" + mCurrent_user_id + ".jpeg";
        StorageReference reference = FirebaseStorage.getInstance().getReference().child(child);

        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(taskSnapshot -> getDownloadURL(reference))
                .addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e.getCause()));
    }

    private void getDownloadURL(StorageReference reference) {
        reference.getDownloadUrl()
                .addOnSuccessListener(uri -> setUserProfileUrl(uri));
    }

    private void setUserProfileUrl(Uri uri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        if (user != null) {
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                            mCurrent_user.child("image").setValue(uri.toString());
                        }
                    });
        }
    }
}

