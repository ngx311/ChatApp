package com.example.thiago.chatapp.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.thiago.chatapp.CustomizeActivity;
import com.example.thiago.chatapp.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class StoryFragment extends Fragment {

    private static final String TAG = "StoryFragment";
    private static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };

    private PreviewView previewView;
    private Button captureButton;
    private ImageButton switchCameraButton;
    private ImageButton flashButton;
    private View imageModeIndicator;
    private View videoModeIndicator;
    private TextView timerTextView;

    private int lensFacing = CameraSelector.LENS_FACING_BACK;
    private boolean isFlashOn = false;
    private boolean isImageMode = true;

    private boolean isRecording = false;
    private CountDownTimer timer;
    private long elapsedMillis = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_story, container, false);

        // Initialize views
        previewView = view.findViewById(R.id.previewView);
        captureButton = view.findViewById(R.id.captureButton);
        switchCameraButton = view.findViewById(R.id.switchCameraButton);
        flashButton = view.findViewById(R.id.flashButton);
        imageModeIndicator = view.findViewById(R.id.imageModeIndicator);
        videoModeIndicator = view.findViewById(R.id.videoModeIndicator);
        timerTextView = view.findViewById(R.id.timerTextView);

        // Request camera and storage permissions if not granted
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        // Set click listeners for buttons
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    stopRecording();
                } else {
                    startRecording();
                }
            }
        });

        switchCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });

        flashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFlash();
            }
        });

        return view;
    }

    // Rest of the methods would remain the same except for one change
    // Replace all 'this' with 'getActivity()' for context in the startCamera() method

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(getActivity());
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                    // Set up preview
                    Preview preview = new Preview.Builder().build();
                    preview.setSurfaceProvider(previewView.getSurfaceProvider());

                    // Set up image capture
                    ImageCapture.Builder imageCaptureBuilder = new ImageCapture.Builder();
                    if (!isImageMode) {
                        imageCaptureBuilder.setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY);
                    }
                    ImageCapture imageCapture = imageCaptureBuilder.build();

                    // Select the camera based on the lens facing
                    CameraSelector cameraSelector = new CameraSelector.Builder()
                            .requireLensFacing(lensFacing)
                            .build();

                    // Unbind any previously bound use cases and bind the camera to the lifecycle
                    cameraProvider.unbindAll();
                    cameraProvider.bindToLifecycle(getActivity(), cameraSelector, preview, imageCapture);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to bind camera", e);
                }
            }
        }, ContextCompat.getMainExecutor(getActivity()));
    }

    // Permission request handling
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(getActivity(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                // You may need to handle this situation more gracefully in a production app
            }
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void toggleFlash() {
        isFlashOn = !isFlashOn;
        if (isFlashOn) {
            flashButton.setImageResource(R.drawable.ic_flash_on);
        } else {
            flashButton.setImageResource(R.drawable.ic_flash_off);
        }
        startCamera();
    }

    private void switchCamera() {
        lensFacing = (lensFacing == CameraSelector.LENS_FACING_BACK)
                ? CameraSelector.LENS_FACING_FRONT : CameraSelector.LENS_FACING_BACK;
        startCamera();
    }

    private void startRecording() {
        isRecording = true;
        elapsedMillis = 0;
        timerTextView.setVisibility(View.VISIBLE);

        timer = new CountDownTimer(60000, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                elapsedMillis = 60000 - millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                stopRecording();
            }
        };

        timer.start();

        // TODO: Start video recording logic here
    }

    private void stopRecording() {
        isRecording = false;
        timerTextView.setVisibility(View.GONE);

        if (timer != null) {
            timer.cancel();
        }

        // TODO: Stop video recording logic here

        // Check if the video duration reached the limit
        if (elapsedMillis >= 60000) {
            // Save the recorded video
            File videoFile = new File(getOutputDirectory(), "recorded_video.mp4");
            Uri recordedVideoUri = Uri.fromFile(videoFile);

            // Start CustomizeActivity to edit the video
            Intent intent = new Intent(requireActivity(), CustomizeActivity.class);
            intent.setData(recordedVideoUri);
            startActivity(intent);
        }
    }

    private File getOutputDirectory() {
        // TODO: Replace with your own logic to determine the output directory
        return new File(requireActivity().getFilesDir(), "media");
    }

    /**
     * Update the timer text view with the elapsed time.
     */
    private void updateTimerText() {
        int minutes = (int) (elapsedMillis / 60000);
        int seconds = (int) ((elapsedMillis % 60000) / 1000);
        int milliseconds = (int) (elapsedMillis % 1000);

        timerTextView.setText(String.format(Locale.getDefault(), "%02d:%02d.%03d", minutes, seconds, milliseconds));
    }





}

