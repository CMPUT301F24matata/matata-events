package com.example.matata;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FacilityFrozenDialogFragment extends DialogFragment {

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_facility_frozen, container, false);

        Button createFacilityButton = view.findViewById(R.id.create_facility_button);

        String facilityId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        createFacilityButton.setOnClickListener(v -> {
            DocumentReference facilityRef = db.collection("FACILITY_PROFILES").document(facilityId);
            facilityRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    facilityRef.update("name", "");
                    facilityRef.update("address", "");
                    facilityRef.update("email", "");
                    facilityRef.update("notifications", false);
                    facilityRef.update("profileUri", "");
                    facilityRef.update("capacity", "");
                    facilityRef.update("contact", "");
                    facilityRef.update("owner", "");
                }
            });
            dismiss();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(false);
            getDialog().setCancelable(false);
        }
    }
}
