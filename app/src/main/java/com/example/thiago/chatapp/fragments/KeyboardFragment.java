package com.example.thiago.chatapp.fragments;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;

import com.example.thiago.chatapp.R;

public class KeyboardFragment extends Fragment implements View.OnTouchListener, View.OnDragListener {
    private LinearLayout emojiBox;
    private LinearLayout keyboardLayout;
    private SharedPreferences sharedPreferences;
    private GestureDetectorCompat gestureDetector;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_keyboard, container, false);
        emojiBox = view.findViewById(R.id.emoji_box);
        keyboardLayout = view.findViewById(R.id.keyboard_layout);
        sharedPreferences = this.getActivity().getSharedPreferences("CustomKeyboard", Context.MODE_PRIVATE);

        // Set drag listener for emoji box
        emojiBox.setOnDragListener(this);

        // Load and add emoji keys to emoji box
        loadEmojiKeys();

        gestureDetector = new GestureDetectorCompat(getActivity(), new SwipeGestureDetector());

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

        return view;
    }

    private void loadEmojiKeys() {
        int[] emojiDrawables = getEmojiDrawables();

        for (int drawableId : emojiDrawables) {
            if (drawableId != 0) { // Check if the drawable resource was found
                try {
                    ImageView emojiKey = new ImageView(getContext());
                    emojiKey.setImageDrawable(getResources().getDrawable(drawableId, null));
                    emojiKey.setPadding(8, 8, 8, 8);
                    emojiKey.setTag(drawableId); // Set the drawable ID as the tag
                    emojiKey.setOnTouchListener(this);
                    emojiBox.addView(emojiKey);
                } catch (Exception e) {
                    Log.e("loadEmojiKeys", "Error loading drawable ID: " + drawableId, e);
                }
            } else {
                Log.e("loadEmojiKeys", "Drawable resource not found");
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
            ClipData dragData = new ClipData((CharSequence) v.getTag(), new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);

            View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(v);
            v.startDragAndDrop(dragData, dragShadowBuilder, v, 0);

            // Indicate that the long press has started the drag
            return true;
        }
        return false;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        int action = event.getAction();
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                // Optional: change the appearance of the view to indicate that it is a valid drop target
                break;

            case DragEvent.ACTION_DRAG_ENTERED:
                // Optional: change the appearance to indicate the user is over a valid drop target
                break;

            case DragEvent.ACTION_DRAG_EXITED:
                // Optional: revert appearance changes made on drag entered
                break;

            case DragEvent.ACTION_DROP:
                // Get the view that is being dragged
                View draggedView = (View) event.getLocalState();
                ViewGroup draggedViewParent = (ViewGroup) draggedView.getParent();

                // Check if the drop target is the keyboard or the emoji collection
                if (v == keyboardLayout && draggedViewParent == emojiBox) {
                    // Emoji is being added to the keyboard
                    addEmojiToKeyboard(draggedView);
                } else if (v == emojiBox && draggedViewParent == keyboardLayout) {
                    // Emoji is being removed from the keyboard
                    removeEmojiFromKeyboard(draggedView);
                }
                break;

            case DragEvent.ACTION_DRAG_ENDED:
                // Optional: revert any appearance changes made during dragging
                break;

            default:
                break;
        }
        return true;
    }

    private void addEmojiToKeyboard(View emojiView) {
        // Remove the emoji from its current parent (emoji collection)
        ViewGroup parent = (ViewGroup) emojiView.getParent();
        parent.removeView(emojiView);

        // Add the emoji to the keyboard layout
        keyboardLayout.addView(emojiView);

        // Optional: Additional code to update any related data structures or preferences
    }

    private void removeEmojiFromKeyboard(View emojiView) {
        // Remove the emoji from the keyboard
        ViewGroup parent = (ViewGroup) emojiView.getParent();
        parent.removeView(emojiView);

        // Add the emoji back to the emoji collection
        emojiBox.addView(emojiView);

        // Optional: Additional code to update any related data structures or preferences
    }

    // Other methods like saveCustomLayout, loadCustomLayout, SwipeGestureDetector, getEmojiDrawables remain unchanged

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

        // Implement onSwipeRight() or any other method you want to trigger on a swipe
        private void onSwipeRight() {
            // Handle right swipe
        }
    }


    private void loadCustomLayout() {
        // Retrieve the saved layout from SharedPreferences or another source
        String savedLayout = sharedPreferences.getString("savedLayoutKey", null);

        if (savedLayout != null) {
            // Parse the saved layout and apply it to your keyboard
            // This is just a template, you'll need to implement the parsing and application logic
            // based on how you're saving the layout and the structure of your keyboard
        }
    }

    private int[] getEmojiDrawables() {
        final int emojiCount = 1000; // Adjust the number based on your actual count
        int[] drawables = new int[emojiCount];
        for (int i = 1; i <= emojiCount; i++) {
            String drawableName = "emoji_icon_" + i;
            int drawableId = getResources().getIdentifier(drawableName, "drawable", getContext().getPackageName());
            if (drawableId != 0) { // 0 means the resource was not found
                drawables[i-1] = drawableId;
            }
        }
        return drawables;
    }



}