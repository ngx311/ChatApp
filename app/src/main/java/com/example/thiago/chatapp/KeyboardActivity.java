package com.example.thiago.chatapp;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

public class KeyboardActivity extends AppCompatActivity implements View.OnTouchListener, View.OnDragListener {
    private LinearLayout emojiBox;
    private LinearLayout keyboardLayout;
    private SharedPreferences sharedPreferences;
    private GestureDetectorCompat gestureDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);
        emojiBox = findViewById(R.id.emoji_box);
        keyboardLayout = findViewById(R.id.keyboard_layout);
        sharedPreferences = getSharedPreferences("CustomKeyboard", Context.MODE_PRIVATE);

        // Set drag listener for emoji box
        emojiBox.setOnDragListener(this);

        // Load and add emoji keys to emoji box
        loadEmojiKeys();

        gestureDetector = new GestureDetectorCompat(this, new SwipeGestureDetector());



        // Set touch listener for emoji keys in the keyboard layout
        for (int i = 0; i < keyboardLayout.getChildCount(); i++) {
            View emojiKey = keyboardLayout.getChildAt(i);
            emojiKey.setOnTouchListener(this);
            emojiKey.setTag(i); // Set the index of the emoji key as the tag
        }

        // Set drag listener for keyboard layout
        keyboardLayout.setOnDragListener(this);

        // Load and apply the saved emoji key positions
        loadCustomLayout();
    }

    private void loadEmojiKeys() {
        String[] emojis = getResources().getStringArray(R.array.emoji_drawables);

        for (String emoji : emojis) {
            ImageView emojiKey = new ImageView(this);
            emojiKey.setImageDrawable(getResources().getDrawable(getResources().getIdentifier(emoji, "drawable", getPackageName())));
            emojiKey.setPadding(8, 8, 8, 8);
            emojiKey.setTag(emoji); // Set the emoji identifier as the tag
            emojiKey.setOnTouchListener(this);
            emojiBox.addView(emojiKey);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Start drag when emoji key is touched
            ClipData clipData = ClipData.newPlainText("", "");
            View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(v);
            v.startDragAndDrop(clipData, dragShadowBuilder, v, 0);
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.gestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }


    @Override
    public boolean onDrag(View v, DragEvent event) {
        int action = event.getAction();
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                // Disable touch events on the emoji keys during the drag
                for (int i = 0; i < emojiBox.getChildCount(); i++) {
                    View emojiKey = emojiBox.getChildAt(i);
                    emojiKey.setOnTouchListener(null);
                }
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                // Handle drag entered event
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                // Handle drag exited event
                break;
            case DragEvent.ACTION_DROP:
                // Handle drop event
                View draggedView = (View) event.getLocalState();
                int draggedIndex = (int) draggedView.getTag();
                String droppedEmoji = (String) v.getTag();

                // Replace the old emoji key with the new one
                replaceEmojiKey(draggedView, droppedEmoji);

                // Save the updated emoji key positions
                saveCustomLayout();
                break;
            case DragEvent.ACTION_DRAG_ENDED:

// Enable touch events on the emoji keys after the drag ends
                for (int i = 0; i < emojiBox.getChildCount(); i++) {
                    View emojiKey = emojiBox.getChildAt(i);
                    emojiKey.setOnTouchListener(this);
                }
                break;
        }
        return true;
    }

    private void replaceEmojiKey(View oldEmojiKey, String newEmoji) {
        // Create a new emoji key ImageView
        ImageView newEmojiKey = new ImageView(this);
        newEmojiKey.setImageDrawable(getResources().getDrawable(getResources().getIdentifier(newEmoji, "drawable", getPackageName())));
        newEmojiKey.setPadding(8, 8, 8, 8);
        newEmojiKey.setTag(newEmoji);
        newEmojiKey.setOnTouchListener(this);

        // Find the position of the old emoji key in the keyboard layout
        int oldEmojiIndex = keyboardLayout.indexOfChild(oldEmojiKey);

        // Remove the old emoji key from the keyboard layout and add the new one at the same position
        keyboardLayout.removeViewAt(oldEmojiIndex);
        keyboardLayout.addView(newEmojiKey, oldEmojiIndex);

        // Set the new position for the new emoji key
        Point oldEmojiPos = new Point(oldEmojiKey.getLeft(), oldEmojiKey.getTop());
        newEmojiKey.layout(oldEmojiPos.x, oldEmojiPos.y, oldEmojiPos.x + newEmojiKey.getWidth(), oldEmojiPos.y + newEmojiKey.getHeight());

        // Remove the old emoji key from the emoji box and add it back
        emojiBox.removeView(oldEmojiKey);
        emojiBox.addView(oldEmojiKey);
    }

    private void saveCustomLayout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        StringBuilder positions = new StringBuilder();

        // Iterate over the emoji keys in the keyboard layout and save their positions
        for (int i = 0; i < keyboardLayout.getChildCount(); i++) {
            View emojiKey = keyboardLayout.getChildAt(i);
            int left = emojiKey.getLeft();
            int top = emojiKey.getTop();

            positions.append(left).append(",").append(top).append(";");
        }

        editor.putString("emoji_positions", positions.toString());
        editor.apply();
    }

    private void loadCustomLayout() {
        String positionsString = sharedPreferences.getString("emoji_positions", "");

        if (!positionsString.isEmpty()) {
            String[] positions = positionsString.split(";");

            // Iterate over the saved positions and update the emoji key positions
            for (int i = 0; i < positions.length; i++) {
                String[] position = positions[i].split(",");
                int left = Integer.parseInt(position[0]);
                int top = Integer.parseInt(position[1]);

                View emojiKey = keyboardLayout.getChildAt(i);
                emojiKey.layout(left, top, left + emojiKey.getWidth(), top + emojiKey.getHeight());
            }
        }
    }

    public void onSwipeRight() {
        saveCustomLayout();
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }

    private class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    // This is a left or right swipe
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            // This is a right swipe
                            onSwipeRight();
                        }
                    }
                    result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

}







