package com.example.matata;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutionException;

/**
 * QR_camera class provides functionality for scanning QR codes using the device's camera.
 * The scanned QR code value is used to navigate to the ViewEvent activity.
 *
 * Outstanding issues: This implementation does not handle QR code scanning errors extensively.
 * Also, if camera permission is denied, the user must manually enable it in settings.
 */
public class QR_camera extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private PreviewView previewView;
    private ImageView goBack;
    private boolean isActivityStarted = false;

    /**
     * Initializes the QR_camera activity, checks camera permissions, and starts the camera if permission is granted.
     *
     * @param savedInstanceState if the activity is being re-initialized, this contains the data it most recently supplied
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_scan_screen);

        previewView = findViewById(R.id.camera_screen);
        goBack = findViewById(R.id.go_back_qr_screen);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }

        goBack.setOnClickListener(v -> finish());
    }

    /**
     * Starts the camera and binds the preview and image analysis for QR code detection.
     */
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error starting camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    /**
     * Binds the camera preview and QR code analyzer to the lifecycle of the activity.
     *
     * @param cameraProvider the camera provider used to bind the lifecycle and camera functionality
     */
    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new QRCodeAnalyzer());

        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
    }

    /**
     * QRCodeAnalyzer class processes each camera frame for QR code analysis.
     */
    private class QRCodeAnalyzer implements ImageAnalysis.Analyzer {
        /**
         * Analyzes an image frame for QR code data.
         *
         * @param imageProxy the image frame to analyze
         */
        @Override
        public void analyze(@NonNull ImageProxy imageProxy) {
            @SuppressLint("UnsafeOptInUsageError")
            Image mediaImage = imageProxy.getImage();
            if (mediaImage != null) {
                InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
                processImage(image);
            }
            imageProxy.close();
        }
    }

    /**
     * Processes the scanned image for QR code detection and navigates to ViewEvent if a QR code is found.
     *
     * @param image the input image to process for QR code data
     */
    private void processImage(InputImage image) {
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();

        BarcodeScanner scanner = BarcodeScanning.getClient(options);

        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    if (!isActivityStarted) {
                        for (Barcode barcode : barcodes) {
                            String rawValue = barcode.getRawValue();
                            Log.d("QRCode", "QR Code found: " + rawValue);
                            Toast.makeText(QR_camera.this, "Scan Successful", Toast.LENGTH_SHORT).show();

                            isActivityStarted = true;
                            Intent intent = new Intent(this, ViewEvent.class);
                            intent.putExtra("Unique_id", rawValue);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                            finish();
                            break;
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("QRCode", "Failed to scan QR code", e));
    }

    /**
     * Handles the result of the camera permission request.
     *
     * @param requestCode  the request code passed in requestPermissions
     * @param permissions  the requested permissions
     * @param grantResults the grant results for the requested permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
