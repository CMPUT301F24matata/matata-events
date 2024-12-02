package com.example.matata;

import static org.junit.Assert.*;

import android.content.Intent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

/**
 * Unit test class for SignUpSheet.
 * It uses Robolectric to simulate Android UI and Firebase Firestore interactions.
 */
import org.robolectric.annotation.Config;

@Config(manifest=Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class SignUpSheetUnitTest {
    private SignUpSheet signUpSheet;
    private EditText nameField, emailField, phoneField, emergencyNameField, emergencyPhoneField, arrivalField;
    private Spinner dietSpinner, accessibilitySpinner;
    private CheckBox termsCheckbox;
    private Button submitButton;

    @Before
    public void setUp() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SignUpSheet.class);
        intent.putExtra("Unique_id", "test_event_id");
        signUpSheet = Robolectric.buildActivity(SignUpSheet.class, intent)
                .create()
                .setup() // Ensures the activity is properly initialized
                .get();

        // Initialize UI components
        nameField = signUpSheet.findViewById(R.id.full_name);
        emailField = signUpSheet.findViewById(R.id.email);
        phoneField = signUpSheet.findViewById(R.id.contact_number);
        emergencyNameField = signUpSheet.findViewById(R.id.emergency_contact_name);
        emergencyPhoneField = signUpSheet.findViewById(R.id.emergency_contact_number);
        dietSpinner = signUpSheet.findViewById(R.id.dietary_preferences_spinner);
        arrivalField = signUpSheet.findViewById(R.id.arrival_time);
        accessibilitySpinner = signUpSheet.findViewById(R.id.accessibility_needs_spinner);
        termsCheckbox = signUpSheet.findViewById(R.id.agree_to_terms);
        submitButton = signUpSheet.findViewById(R.id.submit_button);
    }

    /**
     * Tests that all required fields must be filled out before submission.
     */
    @Test
    public void testFieldValidation() {
        // Leave fields empty and attempt to submit
        submitButton.performClick();
        Toast lastToast = ShadowToast.getLatestToast();
        assertEquals("No Name Found", ShadowToast.getTextOfLatestToast());

        // Fill out all fields
        nameField.setText("abc");
        emailField.setText("abc@example.com");
        phoneField.setText("123456");
        emergencyNameField.setText("abc");
        emergencyPhoneField.setText("123456");
        arrivalField.setText("2:20");
        termsCheckbox.setChecked(true);

        submitButton.performClick();
        assertNull(ShadowToast.getLatestToast()); // Ensure no validation error
    }



    /**
     * Tests that the terms and conditions checkbox must be checked before submission.
     */
    @Test
    public void testTermsValidation() {
        // Fill out all fields except terms checkbox
        nameField.setText("abc");
        emailField.setText("abc@example.com");
        phoneField.setText("123456");
        emergencyNameField.setText("abc");
        emergencyPhoneField.setText("123456");
        arrivalField.setText("2:20");

        termsCheckbox.setChecked(false); // Unchecked
        submitButton.performClick();

        Toast lastToast = ShadowToast.getLatestToast();
        assertEquals("Please Check the Terms and Condition box", ShadowToast.getTextOfLatestToast());
    }


}
