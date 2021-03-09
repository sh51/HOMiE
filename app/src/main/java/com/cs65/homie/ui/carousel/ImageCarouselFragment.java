package com.cs65.homie.ui.carousel;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.cs65.homie.R;
import com.cs65.homie.ui.ImageFullScreenActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * Generic image carousel fragment
 */
@SuppressWarnings("Convert2Diamond")
public class ImageCarouselFragment
    extends Fragment
    implements View.OnClickListener
{

    public static final String INTENT_KEY_IMAGE_URIS
        = "IMAGE_CAROUSEL_INTENT_KEY_IMAGE_URIS";

    private List<Uri> images = new ArrayList<Uri>();
    private ViewPager2 viewPager = null;


    public List<Uri> getImages()
    {
        return this.images;
    }

    public void onClick(View view)
    {

        // Spawn the fuller-screen image view
        Intent intent = new Intent(
            this.getContext(), ImageFullScreenActivity.class
        );
        intent.putExtra(
            ImageFullScreenActivity.BUNDLE_ARG_KEY_URI,
            this.images.get((int)view.getTag())
        );
        this.startActivity(intent);

    }


    public void onCreate(Bundle savedInstanceState)
    {

        // Since this is a dependent fragment, we don't save state
        // That is the responsibility of the containing activity/fragment

        super.onCreate(savedInstanceState);

        Bundle args = this.getArguments();
        if (args != null)
        {
            this.images = args.getParcelableArrayList(
                INTENT_KEY_IMAGE_URIS
            );
        }

    }

    public View onCreateView(
        LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
    )
    {
        return inflater.inflate(
            R.layout.fragment_image_carousel, container, false
        );
    }

    public void onViewCreated(View view, Bundle savedInstanceState)
    {

        // Since this is poor in landscape, and is inflexible overall,
        // magic number remain for now
        this.viewPager = view.findViewById(R.id.imageCarouselViewPager);
        if (this.viewPager != null)
        {

            // Transformer supports the partial images
            CompositePageTransformer transformer
                = new CompositePageTransformer();
            transformer.addTransformer(new MarginPageTransformer(20));
            transformer.addTransformer((page, position) -> page.setScaleY(
                (float)(0.85 + (1 - Math.abs(position)) * 0.15)
            ));

            this.viewPager.setAdapter(new ImageCarouselViewPagerAdapter(this));
            // Basically we only need the left and right images offscreen
            // loaded at any one time
            this.viewPager.setOffscreenPageLimit(2);
            View child = this.viewPager.getChildAt(0);
            if (child != null)
            {
                child.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
            }
            this.viewPager.setPageTransformer(transformer);

        }

    }

    /**
     * Set the list of images held by the carousel
     *
     * All images will be replaced and reloaded on the invocation of this method
     *
     * @param images    Carousel images
     */
    public void setImages(List<Uri> images)
    {
        this.images = images;
        if (this.viewPager != null)
        {
            this.viewPager.invalidate();
        }
    }

}