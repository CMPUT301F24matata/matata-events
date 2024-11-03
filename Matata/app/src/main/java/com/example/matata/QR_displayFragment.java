package com.example.matata;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.sql.Date;
import java.sql.Time;

public class QR_displayFragment extends DialogFragment {

    private String base64encoded;
    private ImageView displayQR;
    private ImageView DownloadQR;
    private ImageView ShareQR;


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
            Bitmap QR=decodeBase64toBmp(base64encoded);
            displayQR.setImageBitmap(QR);

        }

        DownloadQR.setOnClickListener(v -> SaveQRpng());
        ShareQR.setOnClickListener(v->ShareQRpng());

        return view;
    }

    public void SaveQRpng(){

    }

    public void ShareQRpng(){

    }

    public Bitmap decodeBase64toBmp(String bmp64){
        byte[] decodedBytes = Base64.decode(bmp64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


}
