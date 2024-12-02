package com.example.matata;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * The `TestViewEvent` class provides a simplified implementation for displaying event details
 * in the Matata application. It uses hardcoded sample event data to test UI components and user interactions.
 * This class focuses on showcasing event information and toggling the user's waitlist status for an event.
 *
 * <h2>Key Features:</h2>
 * <ul>
 *     <li>Displays event details including title, description, capacity, time, date, and location.</li>
 *     <li>Allows the user to join or withdraw from the event's waitlist using a toggle button.</li>
 *     <li>Provides utilities for encoding and decoding images to and from Base64 format.</li>
 *     <li>Designed for local UI testing without integrating Firebase or other external services.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <p>This class is intended for testing UI elements and user interactions in a controlled environment
 * with hardcoded event data.</p>
 */
public class TestViewEvent extends AppCompatActivity {

    /**
     * ImageView for displaying the event's poster image.
     */
    private ImageView poster;

    /**
     * TextView for displaying the event title.
     */
    private TextView title;

    /**
     * TextView for displaying the event capacity.
     */
    private TextView capacity;

    /**
     * TextView for displaying the event description.
     */
    private TextView desc;

    /**
     * TextView for displaying the event time.
     */
    private TextView time;

    /**
     * TextView for displaying the event date.
     */
    private TextView date;

    /**
     * TextView for displaying the event location.
     */
    private TextView location;

    /**
     * Button for joining or withdrawing from the event waitlist.
     */
    private Button waitlistBtn;

    /**
     * Map containing the details of a sample event used for testing purposes.
     */
    private Map<String, Object> sampleEvent;

    /**
     * Called when the activity is created. Initializes the UI components, loads sample event data,
     * populates the UI with event details, and sets up the waitlist toggle button functionality.
     *
     * @param savedInstanceState If the activity is being re-initialized, this contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_event_details);

        // Initialize UI elements
        poster = findViewById(R.id.poster_pic_Display);
        title = findViewById(R.id.ViewEventTitle);
        capacity = findViewById(R.id.ViewEventCapacity);
        desc = findViewById(R.id.ViewEventDesc);
        time = findViewById(R.id.ViewEventTime);
        date = findViewById(R.id.ViewEventDate);
        location = findViewById(R.id.ViewEventLoc);
        waitlistBtn = findViewById(R.id.join_waitlist_button);

        // Manually populate a sample event
        loadSampleEvent();

        // Populate the UI with event details
        loadEventDetails();

        // Handle waitlist button click
        waitlistBtn.setOnClickListener(v -> toggleWaitlistStatus());
    }

    /**
     * Loads a hardcoded sample event into the `sampleEvent` map for testing purposes.
     */
    private void loadSampleEvent() {
        sampleEvent = new HashMap<>();
        sampleEvent.put("Title", "Cultural Night");
        sampleEvent.put("Capacity", 100);
        sampleEvent.put("Description", "A night to celebrate cultural diversity.");
        sampleEvent.put("Time", "7:00 PM");
        sampleEvent.put("Date", "2024-12-15");
        sampleEvent.put("Location", "University Auditorium");
    }

    /**
     * Populates the UI components with details from the `sampleEvent` map.
     */
    private void loadEventDetails() {
        title.setText((String) sampleEvent.get("Title"));
        capacity.setText(String.valueOf(sampleEvent.get("Capacity")));
        desc.setText((String) sampleEvent.get("Description"));
        time.setText((String) sampleEvent.get("Time"));
        date.setText((String) sampleEvent.get("Date"));
        location.setText((String) sampleEvent.get("Location"));
    }

    /**
     * Toggles the waitlist button between "Join Waitlist" and "Withdraw".
     * Displays a toast message indicating the current action.
     */
    private void toggleWaitlistStatus() {
        String currentText = waitlistBtn.getText().toString();
        if ("Join Waitlist".equals(currentText)) {
            waitlistBtn.setText("Withdraw");
            Toast.makeText(this, "You have joined the waitlist.", Toast.LENGTH_SHORT).show();
        } else {
            waitlistBtn.setText("Join Waitlist");
            Toast.makeText(this, "You have withdrawn from the waitlist.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Encodes a sample image resource to a Base64 string.
     *
     * @return The Base64-encoded string of the sample image.
     */
    private String encodeSampleImageToBase64() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_upload); // Replace with your sample image
        byte[] byteArray = convertBitmapToByteArray(bitmap);
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /**
     * Converts a `Bitmap` image to a byte array.
     *
     * @param bitmap The `Bitmap` image to convert.
     * @return A byte array representation of the `Bitmap`.
     */
    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        java.io.ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * Decodes a Base64-encoded string into a `Bitmap` image.
     *
     * @param base64 The Base64-encoded string.
     * @return A `Bitmap` representation of the decoded image.
     */
    private Bitmap decodeBase64toBmp(String base64) {
        byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
