package com.example.matata;

import static android.content.ContentValues.TAG;

import static java.lang.System.out;

import android.app.Activity;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.checkerframework.checker.units.qual.N;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddEvent extends AppCompatActivity implements TimePickerListener,DatePickerListener{
    private ImageView backBtn;
    private TextView eventTime;
    private LinearLayout dateGroup;
    private LinearLayout timeGroup;
    private TextView eventDate;
    private ImageView posterPic;
    private TextView location;
    private FloatingActionButton genrQR;
    private EditText eveTitle;
    private EditText descriptionBox;
    private EditText capacity;
    private FirebaseFirestore db;
    private StorageReference ref;
    private String USER_ID;
    private String posterURI;
    private boolean isDefaultImage = true;

    private static final int PICK_IMAGE_REQUEST = 1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();
        ref=FirebaseStorage.getInstance("gs://matata-d53da.firebasestorage.app").getReference();


        String EVENT_ID=generateRandomEventID();

        USER_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_event);


        backBtn=findViewById(R.id.btnBackCreateEvent);
        eventTime=findViewById(R.id.dateField);
        dateGroup=findViewById(R.id.dateGroup);
        timeGroup=findViewById(R.id.timeGroup);
        eventDate=findViewById(R.id.editTextDate);
        posterPic=findViewById(R.id.posterPicUpload);
        genrQR=findViewById(R.id.genQR);
        eveTitle=findViewById(R.id.eventTitle);
        descriptionBox=findViewById(R.id.desc_box);
        capacity=findViewById(R.id.number_of_people_event);
        location=findViewById(R.id.editTextLocation);


        //DocumentReference doc = db.collection("EVENT_PROFILES").document(EVENT_ID);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                if ((!eveTitle.getText().toString().equals("") ||
                        !descriptionBox.getText().toString().equals("") ||
                        !eventDate.getText().toString().equals("") ||
                        !eventTime.getText().toString().equals("") ||
                        !capacity.getText().toString().equals("") )
                ){
                    ConfirmationFragment backpress=new ConfirmationFragment();
                    backpress.show(getSupportFragmentManager(),"BackPressFragment");
                }
                else{
                    finish();
                }
            }
        });

        dateGroup.setOnClickListener(v->{

            DialogFragment timePicker=new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(),"timePicker");

        });

        timeGroup.setOnClickListener(v->{
            DialogFragment datePicker=new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(),"datePicker");
        });

        posterPic.setOnClickListener(v -> {
            openSelector();
        });

        genrQR.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (!eveTitle.getText().toString().equals("") &&
                        !descriptionBox.getText().toString().equals("") &&
                        !eventDate.getText().toString().equals("") &&
                        !eventTime.getText().toString().equals("") &&
                        !capacity.getText().toString().equals("")
                ){
                    Log.wtf(TAG,"Okayyyy Letts goooo");


                    Event event=new Event(eveTitle.getText().toString(),eventDate.getText().toString(),eventTime.getText().toString(),location.getText().toString(),descriptionBox.getText().toString(), Integer.parseInt(capacity.getText().toString()),EVENT_ID,USER_ID);

                    Intent intent = new Intent(view.getContext(), ViewEvent.class);
                    String u_id=SaveEventInfo(EVENT_ID,event,intent,view);





                }else{
                    Toast.makeText(AddEvent.this, "Please fill in all the details", Toast.LENGTH_SHORT).show();
                }
            }

        });


    }


    @Override
    public void onTimeSelected(int hour, int min) {
        // Handle the selected time
        String time=String.format("%02d:%02d",hour,min);
        Log.wtf(TAG,time);
        eventTime.setText(time);

    }

    @Override
    public void onDateSelected(int year, int month, int date) {
        String date_str=String.format("%02d/%02d/%04d",date,month+1,year);
        eventDate.setText(date_str);
    }



    private ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    posterPic.setImageURI(imageUri);
                    isDefaultImage = false;
                }
            }
    );

    public void openSelector(){
        Intent intent =new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }



    private String SaveEventInfo(String EVENT_ID,Event event,Intent intent,View view){

        Bitmap bmp=generateQRbitmap(EVENT_ID);
        String compressedBMP=bmpCompression(bmp);

        StorageReference imagesRef = ref.child("EventsPosters/"+EVENT_ID+".jpg");


        Map<String, Object> Event_details = new HashMap<>();
        if (isDefaultImage){
            Event_details.put("Poster","");
        }else{


            Bitmap bmpjpg=((BitmapDrawable) posterPic.getDrawable()).getBitmap();
            File temp=new File(getCacheDir(),EVENT_ID+".jpg");

            try (FileOutputStream out=new FileOutputStream(temp)){
                bmpjpg.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }
             catch (IOException e) {
                throw new RuntimeException(e);
            }
            Uri returned=Uri.fromFile(temp);

            imagesRef.putFile(returned)
                    .addOnSuccessListener(v->imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        posterURI= uri.toString();
                        Log.wtf("345",posterURI);
                        Event_details.put("Poster",posterURI);
                        Log.wtf("345",Event_details.toString());
                        Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        temp.delete();
                    }))
                    .addOnFailureListener(e -> Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }



        Event_details.put("Title", event.getTitle());
        Event_details.put("Date", event.getDate());
        Event_details.put("Time", event.getTime());
        Event_details.put("Location",event.getLocation());
        Event_details.put("Description",event.getDescription());
        Event_details.put("Capacity",event.getCapacity());
        Event_details.put("bitmap",compressedBMP);

        Log.wtf("23", Event_details.toString());

        DocumentReference doc = db.collection("EVENT_PROFILES").document(EVENT_ID);
        doc.get()
                .addOnSuccessListener(documentSnapshot -> {
                    executeDBchange(Event_details,EVENT_ID);
                    intent.putExtra("Unique_id", EVENT_ID);
                    view.getContext().startActivity(intent);


                }).addOnFailureListener(v->Toast.makeText(AddEvent.this, "Something Went Wrong with server upload", Toast.LENGTH_SHORT).show());


        return EVENT_ID;
    }

    public String generateRandomEventID(){
        return UUID.randomUUID().toString();
    }

    public void executeDBchange(Map Event_details,String EVENT_ID){

        db.collection("EVENT_PROFILES").document(EVENT_ID)
                .set(Event_details, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Toast.makeText(AddEvent.this, "Event saved successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(AddEvent.this, "Failed to save profile", Toast.LENGTH_SHORT).show());
    }

    private Bitmap generateQRbitmap(String EVENT_ID){
        BarcodeEncoder barcodeEncoder=new BarcodeEncoder();

        Bitmap bitmap= null;
        try {
            bitmap = barcodeEncoder.encodeBitmap(EVENT_ID, BarcodeFormat.QR_CODE,500,500);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }

        return bitmap;
    }

    //ChatGPT prompt "Give me some secure hash function for storage on cloud"
    public String generateHash(String input) {
        try {
            MessageDigest digest=MessageDigest.getInstance("SHA-256");
            byte[] hash=digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length()==1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    //ChatGPT prompt "How to store bitmap as a string"
    public String bmpCompression(Bitmap bmp){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte[] byteArray=byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray,Base64.DEFAULT);
    }
}


