package com.cs65.homie.ui.carousel;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.cs65.homie.R;

import java.util.ArrayList;
import java.util.List;


public class ImageCarouselFragment extends Fragment
{

    public static final String INTENT_KEY_IMAGE_URIS
        = "IMAGE_CAROUSEL_INTENT_KEY_IMAGE_URIS";

    private List<Uri> images = new ArrayList<Uri>();
    private ViewPager2 viewPager = null;

    public List<Uri> getImages()
    {
        return this.images;
    }

    public void onCreate(Bundle savedInstanceState)
    {

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

        // FIXME The proportions of the movement, padding, etc,
        // completely break down on landscape
        this.viewPager = view.findViewById(R.id.imageCarouselViewPager);
        if (this.viewPager != null)
        {

            CompositePageTransformer transformer
                = new CompositePageTransformer();
            transformer.addTransformer(new MarginPageTransformer(20));
            transformer.addTransformer(new ViewPager2.PageTransformer()
            {
                @Override
                public void transformPage(@NonNull View page, float position)
                {
                    page.setScaleY((float)(0.85 + (1 - Math.abs(position)) * 0.15));
                }
            });

            this.viewPager.setAdapter(new ImageCarouselViewPagerAdapter(this));
            this.viewPager.setOffscreenPageLimit(2);
            View child = this.viewPager.getChildAt(0);
            if (child != null)
            {
                child.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
            }
            this.viewPager.setPageTransformer(transformer);

        }

    }

    public void setImages(List<Uri> images)
    {
        this.images = images;
        this.viewPager.invalidate();
    }

}