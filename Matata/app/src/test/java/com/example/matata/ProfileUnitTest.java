package com.example.matata;

import static org.junit.Assert.*;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.test.core.app.ApplicationProvider;

import com.bumptech.glide.Glide;

import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;

import java.util.Arrays;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, manifest = Config.NONE)
public class ProfileUnitTest {

    private ProfileActivity mockprofile;
    private ActivityController<ProfileActivity> controller;
    @Before
    public void setUp(){
        controller = Robolectric.buildActivity(ProfileActivity.class);
        mockprofile = controller.get();

        mockprofile.nameEditText=new EditText(ApplicationProvider.getApplicationContext());
        mockprofile.phoneEditText=new EditText(ApplicationProvider.getApplicationContext());
        mockprofile.emailEditText=new EditText(ApplicationProvider.getApplicationContext());
        mockprofile.notifications=new Switch(ApplicationProvider.getApplicationContext());
        mockprofile.isOrganizer=new Switch(ApplicationProvider.getApplicationContext());
        mockprofile.profileIcon=new ImageView(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void testInitials(){
        System.out.println(mockprofile.nameEditText.getText().toString());
        mockprofile.nameEditText.setText("Test Name");
        String initials=mockprofile.getUserInitials(mockprofile.nameEditText.getText().toString());
        assertEquals("TN",initials);
    }

    @Test
    public void testGenerateInitialsBitmap() {

        String initials="TN";
        Context context=ApplicationProvider.getApplicationContext();
        Uri generatedUri=mockprofile.createImageFromString(context, initials);
        assertNotNull(generatedUri);
        String uriString=generatedUri.toString();
        assertTrue(uriString.startsWith("data:image/png;base64,"));
    }

    @Test
    public void testLoadProfilePictureWithNullUri() {
        Uri mockUri=null;
        mockprofile.loadProfilePicture(String.valueOf(mockUri));
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        assertNull(mockprofile.profileIcon.getDrawable());
    }



}
