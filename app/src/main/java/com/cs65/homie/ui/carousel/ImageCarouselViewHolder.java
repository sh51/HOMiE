package com.cs65.homie.ui.carousel;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs65.homie.R;


class ImageCarouselViewHolder extends RecyclerView.ViewHolder
{

    private final ImageView imageView;

    public ImageCarouselViewHolder(@NonNull View itemView)
    {
        super(itemView);
        this.imageView = itemView.findViewById(
            R.id.imageCarouselItem
        );
    }

    public ImageView getImageView()
    {
        return this.imageView;
    }

}