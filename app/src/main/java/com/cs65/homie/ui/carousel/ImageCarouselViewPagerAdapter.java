package com.cs65.homie.ui.carousel;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.homie.R;


class ImageCarouselViewPagerAdapter
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
        holder.getImageView().setImageURI(
            this.parent.getImages().get(position)
        );
        holder.getImageView().setOnClickListener(this.parent);
        holder.getImageView().setTag(position);
    }

    public int getItemCount()
    {
        return this.parent.getImages().size();
    }

}
