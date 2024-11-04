package com.example.matata;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class QR_displayFragment extends DialogFragment {

    private String base64encoded;
    private ImageView displayQR;
    private ImageView DownloadQR;
    private ImageView ShareQR;
    private Bitmap QR;
    private static final int REQUEST_WRITE_PERMISSION=100;


    public static QR_displayFragment newInstance(String encoded) {
        QR_displayFragment fragment = new QR_displayFragment();
        Bundle args=new Bundle();
        args.putString("encoded",encoded);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.view_qr, container, false);

        displayQR=view.findViewById(R.id.view_qr);
        ShareQR=view.findViewById(R.id.QR_share);
        DownloadQR=view.findViewById(R.id.downlaod_QR);

        if (getArguments()!=null) {
            base64encoded = getArguments().getString("encoded"); // Retrieve the string argument
            QR=decodeBase64toBmp(base64encoded);
            displayQR.setImageBitmap(QR);

        }



        DownloadQR.setOnClickListener(v -> SaveQRpng(QR));
        ShareQR.setOnClickListener(v->ShareQRpng());

        return view;
    }

    public void SaveQRpng(Bitmap bmp){

        ContentResolver resolver = getContext().getContentResolver();
        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DISPLAY_NAME, "sampleQR.png");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/MyAppFolder");

        Uri imageUri = null;
        OutputStream outStream = null;
        try {
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
            if (imageUri!=null) {
                outStream=resolver.openOutputStream(imageUri);
                bmp.compress(Bitmap.CompressFormat.PNG,100,outStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
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



    public void ShareQRpng(){

    }

    public Bitmap decodeBase64toBmp(String bmp64){
        byte[] decodedBytes = Base64.decode(bmp64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

    }


}
