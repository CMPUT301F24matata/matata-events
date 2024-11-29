package com.example.matata;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.IOException;
import java.io.OutputStream;

/**
 * QR_displayFragment displays a QR code and provides an option to save it as a PNG file.
 * This class takes a base64-encoded string as an argument, decodes it to a bitmap,
 * and displays the bitmap in an ImageView. It also includes a download button to save
 * the displayed QR code image to the device's external storage.
 *
 */
public class QR_displayFragment extends DialogFragment {

    /**
     * Base64 encoded string representing the QR code image.
     */
    private String base64encoded;

    /**
     * ImageView to display the QR code on the screen.
     */
    private ImageView displayQR;

    /**
     * ImageView for the download button to save the QR code locally.
     */
    private ImageView downloadQR;

    /**
     * Bitmap object representing the generated QR code.
     */
    private Bitmap QR;

    /**
     * Request code for write permission, set to 100.
     */
    private static final int REQUEST_WRITE_PERMISSION = 100;


    /**
     * Creates a new instance of QR_displayFragment with the base64-encoded QR code as an argument.
     *
     * @param encoded the base64-encoded string of the QR code image
     * @return a new instance of QR_displayFragment
     */
    public static QR_displayFragment newInstance(String encoded) {
        QR_displayFragment fragment = new QR_displayFragment();
        Bundle args = new Bundle();
        args.putString("encoded", encoded);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Inflates the fragment layout, decodes the QR code from base64, and sets it in the ImageView.
     * Also sets up the download button to save the QR code when clicked.
     *
     * @param inflater           the LayoutInflater object that can be used to inflate any views in the fragment
     * @param container          if non-null, this is the parent view that the fragment's UI should be attached to
     * @param savedInstanceState if non-null, this fragment is being re-constructed from a previous saved state
     * @return the View for the fragment's UI
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_qr, container, false);

        displayQR = view.findViewById(R.id.view_qr);
        downloadQR = view.findViewById(R.id.downlaod_QR);

        if (getArguments() != null) {
            base64encoded = getArguments().getString("encoded");
            QR = decodeBase64toBmp(base64encoded);
            displayQR.setImageBitmap(QR);
        }

        downloadQR.setOnClickListener(v -> saveQRpng(QR));

        return view;
    }

    /**
     * Saves the provided Bitmap QR code image as a PNG file to external storage.
     *
     * @param bmp the Bitmap of the QR code to be saved
     */
    public void saveQRpng(Bitmap bmp) {
        ContentResolver resolver = getContext().getContentResolver();
        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DISPLAY_NAME, "sampleQR.png");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/MyAppFolder");

        Uri imageUri = null;
        OutputStream outStream = null;
        try {
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (imageUri != null) {
                outStream = resolver.openOutputStream(imageUri);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                Toast.makeText(getContext(), "QR Code saved to gallery", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to save QR code", Toast.LENGTH_SHORT).show();
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Decodes a base64-encoded string into a Bitmap.
     *
     * @param bmp64 the base64-encoded string representing the QR code image
     * @return the decoded Bitmap
     */
    public Bitmap decodeBase64toBmp(String bmp64) {
        byte[] decodedBytes = Base64.decode(bmp64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

}
