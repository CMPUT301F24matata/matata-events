package com.example.matata;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;

public class ProfilePicActivity extends Fragment {

    private ImageView ivProfilePicture;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                requireActivity().getContentResolver(), selectedImageUri);
                        ivProfilePicture.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Log.e(TAG,Log.getStackTraceString(e));
                        Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Image selection canceled", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_pic, container, false);

        ivProfilePicture = view.findViewById(R.id.ivProfilePicture);
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        Button btnUploadPicture = view.findViewById(R.id.btnUploadPicture);
        Button btnDeletePicture = view.findViewById(R.id.btnDeletePicture);

        btnBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        ivProfilePicture.setOnClickListener(v -> openImagePicker());

        btnUploadPicture.setOnClickListener(v -> uploadToProfile());

        btnDeletePicture.setOnClickListener(v -> deleteProfilePicture());

        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        pickImageLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private void deleteProfilePicture() {
        ivProfilePicture.setImageResource(R.drawable.ic_upload);
        selectedImageUri = null;

        requireActivity().getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
                .edit()
                .remove("profile_image_uri")
                .apply();

        requireActivity().setResult(Activity.RESULT_OK);
        requireActivity().getSupportFragmentManager().popBackStack();

        Toast.makeText(getContext(), "Profile picture deleted", Toast.LENGTH_SHORT).show();
    }

    private void uploadToProfile() {
        if (selectedImageUri != null) {
            requireActivity().getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
                    .edit()
                    .putString("profile_image_uri", selectedImageUri.toString())
                    .apply();

            Toast.makeText(getContext(), "Profile picture uploaded successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "No image selected to upload", Toast.LENGTH_SHORT).show();
        }
    }

}
