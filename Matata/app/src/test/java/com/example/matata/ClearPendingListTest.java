package com.example.matata;

import android.app.AlertDialog;
import android.widget.Button;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.LooperMode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.matata.ClearPendingListFragment;
import com.example.matata.R;
@RunWith(AndroidJUnit4.class)
public class ClearPendingListTest {

    @Test
    public void testDialogButtons() {
        // Launch the ClearPendingListFragment
        FragmentScenario<ClearPendingListFragment> scenario =
                FragmentScenario.launchInContainer(ClearPendingListFragment.class);

        scenario.onFragment(fragment -> {
            // Obtain the dialog
            AlertDialog dialog = (AlertDialog) fragment.getDialog();
            assertNotNull(dialog);

            // Verify the "Yes" button
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            assertNotNull(positiveButton);
            assertEquals("Yes", positiveButton.getText());

            // Verify the "No" button
            Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            assertNotNull(negativeButton);
            assertEquals("No", negativeButton.getText());
        });
    }
}