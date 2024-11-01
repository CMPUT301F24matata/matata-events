package com.example.matata;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.sql.Date;
import java.sql.Time;

public class QR_displayFragment extends DialogFragment implements QR_displayListener{


    @Override
    public void onDataRead (String Title, String description, String Poster_id, Date date, Time time){
    }
}
