package com.example.thiago.chatapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class StoryActivity extends AppCompatActivity {

    private static final String TAG = "StoryActivity";
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        // Initialize views
        previewView = findViewById(R.id.previewView);
        captureButton = findViewById(R.id.captureButton);
        switchCameraButton = findViewById(R.id.switchCameraButton);
        flashButton = findViewById(R.id.flashButton);
        imageModeIndicator = findViewById(R.id.imageModeIndicator);
        videoModeIndicator = findViewById(R.id.videoModeIndicator);
        timerTextView = findViewById(R.id.timerTextView);

        // Request camera and storage permissions if not granted
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
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
    }

    /**
     * Start the camera and bind the camera use cases.
     */
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
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
                    cameraProvider.bindToLifecycle(StoryActivity.this, cameraSelector, preview, imageCapture);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to bind camera", e);
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    /**
     * Start video recording.
     */
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

    /**
     * Stop video recording.
     */
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
            Intent intent = new Intent(this, CustomizeActivity.class);
            intent.setData(recordedVideoUri);
            startActivity(intent);
        }
    }

    /**
     * Switch between the front and rear cameras.
     */
    private void switchCamera() {
        lensFacing = (lensFacing == CameraSelector.LENS_FACING_BACK)
                ? CameraSelector.LENS_FACING_FRONT : CameraSelector.LENS_FACING_BACK;
        startCamera();
    }

    /**
     * Toggle the flash on or off.
     */
    private void toggleFlash() {
        isFlashOn = !isFlashOn;
        if (isFlashOn) {
            flashButton.setImageResource(R.drawable.ic_flash_on);
        } else {
            flashButton.setImageResource(R.drawable.ic_flash_off);
        }
        startCamera();
    }

    /**
     * Check if all required permissions are granted.
     *
     * @return True if all permissions are granted, false otherwise.
     */
    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the output directory for saving captured media.
     *
     * @return The output directory.
     */
    private File getOutputDirectory() {
        // TODO: Replace with your own logic to determine the output directory
        return new File(getFilesDir(), "media");
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

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
