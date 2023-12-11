package com.example.thiago.chatapp;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class CustomHorizontalScrollView extends HorizontalScrollView {
    private Button captureButton;
    private LinearLayout selectionContainer;
    private boolean isPhotoMode;

    public CustomHorizontalScrollView(Context context) {
        super(context);
    }

    public CustomHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCaptureButton(Button captureButton) {
        this.captureButton = captureButton;
    }

    public void setSelectionContainer(LinearLayout selectionContainer) {
        this.selectionContainer = selectionContainer;
    }

    private void updateSelectionUI() {

    }

    @Override
    public boolean performClick() {
        int captureButtonX = captureButton.getLeft() + captureButton.getWidth() / 2;
        int scrollViewX = getScrollX();
        int selectedButtonIndex = -1;
        int minDistance = Integer.MAX_VALUE;

        for (int i = 0; i < selectionContainer.getChildCount(); i++) {
            ImageButton button = (ImageButton) selectionContainer.getChildAt(i);
            int buttonX = button.getLeft() + button.getWidth() / 2;
            int distance = Math.abs(buttonX + scrollViewX - captureButtonX);

            if (distance < minDistance) {
                minDistance = distance;
                selectedButtonIndex = i;
            }
        }

        if (selectedButtonIndex != -1) {
            isPhotoMode = selectedButtonIndex == 0;
            updateSelectionUI();
        }

        return super.performClick();
    }
}

