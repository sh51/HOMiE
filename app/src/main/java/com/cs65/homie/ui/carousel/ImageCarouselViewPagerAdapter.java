package com.cs65.homie.ui.carousel;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.homie.R;


public class ImageCarouselViewPagerAdapter
    extends RecyclerView.Adapter<ImageCarouselViewHolder>
{

    ImageCarouselFragment parent = null;

    public ImageCarouselViewPagerAdapter(ImageCarouselFragment parent)
    {
        this.parent = parent;
    }

    public ImageCarouselViewHolder onCreateViewHolder(
        ViewGroup parent, int viewType
    )
    {
        return new ImageCarouselViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_image_carousel_entry, parent, false
            )
        );
    }

    public void onBindViewHolder(@NonNull ImageCarouselViewHolder holder, int position)
    {
        //holder.getImageView().setImageURI(
        //    this.parent.getImages().get(position)
        //);
        switch (position)
        {
            case 0:
                holder.getImageView().setImageResource(R.drawable.dart0);
                break;
            case 1:
                holder.getImageView().setImageResource(R.drawable.dart1);
                break;
            case 2:
                holder.getImageView().setImageResource(R.drawable.dart2);
                break;
            case 3:
                holder.getImageView().setImageResource(R.drawable.dart3);
                break;
            default:
                //pass
        }
    }

    public int getItemCount()
    {
        return this.parent.getImages().size();
    }

}
