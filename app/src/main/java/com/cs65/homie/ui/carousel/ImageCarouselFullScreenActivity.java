package com.cs65.homie.ui.carousel;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.cs65.homie.R;

import org.jetbrains.annotations.NotNull;


/**
 * Class for showing an image in (most) full screen
 *
 * Does not support true full screen and zoom
 */
public class ImageCarouselFullScreenActivity
    extends AppCompatActivity
    implements View.OnClickListener
{

    public static final String BUNDLE_ARG_KEY_URI
        = "FULL_SCREEN_IMAGE_URI";

    private Uri image = null;


    public void onClick(View v)
    {
        this.finish();
    }

    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        // Find the image URI out of a bundle
        if (this.getIntent() != null)
        {

            Bundle args = this.getIntent().getExtras();
            if (args != null)
            {
                this.image = args.getParcelable(BUNDLE_ARG_KEY_URI);
            }

        }
        else
        {
            this.image = savedInstanceState.getParcelable(BUNDLE_ARG_KEY_URI);
        }

        // Hide the title bar
        ActionBar bar = this.getSupportActionBar();
        if (bar != null)
        {
            bar.hide();
        }

        // Load in the image and setup the exit click listener
        this.setContentView(R.layout.activity_image_full_screen);
        ImageView imageView = this.findViewById(R.id.fullScreenImageView);
        if (imageView != null)
        {

            // This is deprecated, but supports some full-screen capability
            //
            //imageView.setSystemUiVisibility(
            //    View.SYSTEM_UI_FLAG_FULLSCREEN
            //    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            //);
            imageView.setImageURI(null);
            imageView.setImageURI(this.image);
            imageView.setOnClickListener(this);

        }

    }

    protected void onSaveInstanceState(@NotNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (this.image != null)
        {
            outState.putParcelable(BUNDLE_ARG_KEY_URI, this.image);
        }
    }

}