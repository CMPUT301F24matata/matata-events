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
import org.mockito.Mockito;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, manifest = Config.NONE)
public class ProfileUnitTest {

    private ProfileActivity mockprofile;

    @Before
    public void setUp(){
        mockprofile=new ProfileActivity();
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
        Uri uri =mockprofile.createImageFromString(ApplicationProvider.getApplicationContext(), initials);
        String uri_64= Arrays.toString(uri.toString().trim().split("data:image/png;base64,"));
        //System.out.println(uri_64);
        assertNotNull(uri_64);

    }

    @Test
    public void profileLoadTest(){
        Context context = ApplicationProvider.getApplicationContext();
        mockprofile.profileIcon = Mockito.mock(ImageView.class);
        mockprofile.loadProfilePicture(null);
        Mockito.verify(mockprofile.profileIcon).setImageBitmap(Mockito.any(Bitmap.class));
    }

}
